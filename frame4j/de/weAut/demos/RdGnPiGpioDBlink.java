/*  Copyright 2021 AlbrechtWeinert, Bochum, Germany (a-weinert.de)
 *  All rights reserved.
 *  
 *  This file is part of Frame4J notwithstading being in de.weAut... 
 *  ( frame4j.de  https://weinert-automation.de/software/frame4j/ )
 * 
 *  Frame4J is made available under the terms of the 
 *  Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/  or as text in
 https://weinert-automation.de/java/docs/frame4j/de/frame4j/doc-files/epl.txt
 *  within the source distribution 
 */
package  de.weAut.demos;
import de.weAut.PiUtil;     // Raspberry Pi handling utilities (IO lock)
import de.frame4j.util.ComVar;
import de.weAut.ClientPigpiod;
import java.io.IOException;
import java.io.PrintWriter;

/** <b>RdGnPiGpioDBlink &ndash; a Java pigpiod test and demo program</b>.<br />
 *  <br />
 *  This program is based on a port of pigpiod test and demo program
 *  rdGnPiGpioDBlink from C to Java. It uses Joan N.N's pigpio library's 
 *  <a href="http://abyz.me.uk/rpi/pigpio/sif.html">socket interface</a> 
 *  directly. Hence, it is 100% pure Java &mdash; that means no JNI
 *  (Java native interface) is used.<br />
 *  <br />
 *  Comment excerpt of the original/ported C source file: <br /><pre>
  A fifth program for Raspberry's GPIO pins

  Rev. $Revision: 50 $  $Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $
  Copyright  (c)  2019   Albrecht Weinert <br />
  weinert-automation.de      a-weinert.de

  It uses two/three pins as output assuming two LEDs connected to as H=on
  Pi 1  / Pi 3       Pin
  GPIO17/ 17 : red    11
  GPIO21/ 27 : green  13
  GPIO25     : yellow 22  (since Revision 45)
  This program forces application singleton and may be used as service.  
</pre><br />  
  Its functions are the same as rdGnBlinkBlink (even sharing the lockFile)
  except for using the pigpioD library. Our makefiles define
  MCU and PLATFORM as make variables and macros. So we could make the
  GPIO pin and address assignment automatically in the C programmes.<br />
  (--- End of original C comment excerpt ---)<br />
<br />
 *  In Java, for good reasons, we do not have the "make plus macros and all
 *  can be wildly changed" mechanism. In a Java program, by playing with
 *  inheritance and polymorphism, we can postpone the Pi type decision &ndash;
 *  in a better way &ndash; to program's run time. Here this is done by 
 *  {@linkplain de.weAut.ThePi#make(String, int, int, int) making} (by the 
 *  factory method)
 *  {@link de.weAut.ThePi#make(String, int, int, int) ThePi.make(...)} a
 *  {@link de.weAut.ThePi ThePi} object either as
 *  {@link de.weAut.Pi3} (for Pi0, 3 and 4) or
 *  as a {@link de.weAut.Pi1} or {@link de.weAut.Pi2} (interface's inner
 *  class) object.<br />
 *  <br />
 *  Copyright  &nbsp;&copy;&nbsp; 2019 Albrecht Weinert <br />
 *  @see BlinkOnPi
 *  @see de.weAut.TestOnPi
 *  @see <a href="../doc-files/Raspi4testPins.png"
 *   title="GPIOs and pins">Raspi4testPins</a>
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 50 $ ($Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $)
 */
// so far:   V.  21  (21.05.2019) : new, minimal functionality
//           V. -26  (31.05.2019) : three LEDs, IO lock 
//           V.  35  (01.04.2021) : MBean for JConsole 
public class RdGnPiGpioDBlink implements PiUtil, RdGnPiGpioDBlinkMBean {

/** The LEDs to blink. <br />
 *  <br />
 *  The default pins for rd/ye/gn are 11, 22, and 13. <br />
 *  This is the traditional 2017 experimental setup. With a piTraffic 
 *  (cross roads) head  {@link #ledGNpin} would be south yellow and
 *  {@link #ledYEpin} west green.  
 */
  public int ledRDpin = 11, // piTraffic south rd
             ledYEpin = 22, // piTraffic west  gn
             ledGNpin = 13; // piTraffic south ye
  
  ClientPigpiod  pI;
  int argPiType = 3; // start argument 
  int argPort; // 0 -> default 8888
  int argTimeout = 10000; // 10s default (not yet evaluated as arg)
  String argHost = null; // null not yet set -> default
  
//--- properties exposed as MBean -------------------------------------------   
  
  boolean runOn; // run endless resp. while true
  int cycCount; // incremented on every red & green blink cycle (600ms)
  boolean yLd; // status of the yellow LED(s)
  boolean rLd; // status of the red LED(s)
  boolean gLd; // status of the green LED(s)
  
  final String[] args;
  
  /* The normal output. <br /> */
  public PrintWriter out;
  
/** Make the application object. <br /> */  
  protected RdGnPiGpioDBlink(String[] args) {
    this.args = args;
    this.out = getOut();
  } // RdGnPiGpioDBlink()

//----  MBean Operations / Implementation    --------------------------------
  @Override public Integer getCycCount(){ return cycCount; }
  @Override public void setCycCount(Integer cycCount){ this.cycCount = cycCount; }
  @Override public Boolean getLEDye(){ return yLd; }
  @Override public Boolean getLEDgn(){ return gLd; }
  @Override public Boolean getLEDrd(){ return rLd; }
  @Override public void stop(){ runOn = false; }
  @Override public Integer getPiType(){ return pI.thePi.type(); }

/** The application start. <br />
 *  <br />
 *  Will blink with three LEDs in an endless loop (in {@link #doIt()}). 
 *  Can be stopped by signal (cntlC), kill command and the like, as well as
 *  by an MBean client, like e.g. JConsole, by operation {@link #stop()}.<br />
 *  <br />
 *  Run by java RdGnPiGpioDBlink [host [port]] [-option [-option ....<br />
 *  LEDs rd,ye,gn Options are:
{@code -north  29 31 33; -east 36 38 40;  -south 11 13 15; -west 16 18 22;}<br />
{@code -LEDefault 11 22 13} <br />
 *  Pi type options are:
{@code -pi0 -pi1 -pi3 -pi4} (0, 1, 3 and 4 map to 3, the default)<br />
 *  @param args start parameters.
 */
  public static void main(String[] args){
    RdGnPiGpioDBlink rdGnPiGpioDBlink;
    try {
      // Initialise the application as RdGnPiGpioDBlinkMbean
      rdGnPiGpioDBlink = new RdGnPiGpioDBlink(args);
      rdGnPiGpioDBlink.regAsStdMBean();
    } catch (Exception e) { // should not happen
      e.printStackTrace();
      return; // should not happen
    } // make and register
    rdGnPiGpioDBlink.doIt(); // run on the RdGnPiGpioDBlink object
  } // main(String[])
  
/** The application's work.<br />
 *  <br />
 *  When  the startup succeeds this will run in an endless loop until 
 *  killed by signal or stopped by MBean/JConsole command.
 */
  public void doIt(){
    out.println("\n  " + ComVar.PROG_SHORT + " start" +
                (ComVar.ON_PI ? " on Pi" : " on " + ComVar.HOST_NAME 
                                              + " under " + ComVar.OS));
    if (args != null && args.length > 0) {
      for (String actArg : args) {
        if (actArg.length() < 2) continue;
        if (actArg.charAt(0) != '-') {
          if (argHost == null) {
            argHost = actArg;
          // PiUtil.out.println("  RdGnPiGpioDBlink port set to:" + argHost);
          } else {
            try { // decode as port number
              argPort = Integer.decode(actArg).intValue();
          // PiUtil.out.println("  RdGnPiGpioDBlink port set to:" + argPort);
            } catch (Exception e) {
              out.println("  RdGnPiGpioDBlink no number:" + actArg);
              systemExit(93);
            } // decode as port number 
          } // host , port
          continue;
        } // not -xyz
        int hit = 0;
        if ("-pi1".startsWith(actArg)) { argPiType = 1; ++hit; } // -pi1
        if ("-pi2".startsWith(actArg)) { argPiType = 2; ++hit; } // -pi2
        if ("-pi0".startsWith(actArg)) { argPiType = 0; ++hit; } // -pi0
        if ("-pi4".startsWith(actArg)) { argPiType = 4; ++hit; } // -pi4
        if ("-pi3".startsWith(actArg)) { argPiType = 3; ++hit; } // -pi3

        if ("-north".startsWith(actArg)) {
          ledRDpin = 29; ledYEpin = 31; ledGNpin = 33;
          ++hit; } // -north
        if ("-east".startsWith(actArg)) {
          ledRDpin = 36; ledYEpin = 38; ledGNpin = 40;
          ++hit; } // -east
        if ("-south".startsWith(actArg)) {
          ledRDpin = 11; ledYEpin = 13; ledGNpin = 15;
          ++hit; } // -south
        if ("-west".startsWith(actArg)) {
          ledRDpin = 16; ledYEpin = 18; ledGNpin = 22;
          ++hit; } // -west
        if ("-LEDefault".startsWith(actArg)) {
          ledRDpin = 11; ledYEpin = 22; ledGNpin = 13;
          ++hit;} // -LEDefault
        if (hit > 1) {
          out.println("  RdGnPiGpioDBlink ambigous arg:" + actArg);
          systemExit(91);
        } else if (hit == 0) {
          out.println("  RdGnPiGpioDBlink undefined arg:" + actArg);
          systemExit(92);
        }
      } // for over args
    } // at least one argument

    final int oL = openLock(null, false);
    runOn = oL == 0;
    if (! runOn) {
      out.println("  RdGnJPiGpioDBlink getIOlock error: "
                                                  + PiUtil.errorText(oL));
      return;
    }
    try { // make and connect
      pI = ClientPigpiod.make(argHost, argPort, argTimeout, argPiType, this);
      out.println("  RdGnPiGpioDBlink connect " + pI);
    } catch (IOException ex) {
      ex.printStackTrace();
      return; // can't open socket
    }  // null, 8888, 2
    // as we have the concrete Pi type now make GPIO numbers for pins
    int LEDrd = pI.thePi.gpio4pin(ledRDpin);
    int LEDgn = pI.thePi.gpio4pin(ledGNpin);
    int LEDye = pI.thePi.gpio4pin(ledYEpin);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> { // shutdownHook
      runOn = false;
      if (pI != null) {
        out.println("\n  RdGnPiGpioDBlink shutdown " + pI);
        pI.releaseOutputsReport(out);
        try {
          pI.disconnect();
        } catch (IOException e) { } 

        // pigpio.gpioTerminate();
      } else { // pI not null else null
        out.println("\n  RdGnPiGpioDBlink shutdown ");
      } // pI null
      // e.printStackTrace();
      //   }
      closeLock();
    })); // shutdownHook.run()

    out.println("  RdGnPiGpioDBlink set mode output rd ye gn/up  14 mA");
    pI.logCommand(pI.setMode(LEDrd, GPIO_OUT));
    pI.logCommand(pI.setMode(LEDgn, GPIO_OUT));
    pI.logCommand(pI.setMode(LEDye, GPIO_OUT));
    pI.logCommand(pI.setPullR(LEDgn, PI_PUD_UP));
    pI.logCommand(pI.setPadS(0, 14));
    out.println("  RdGnPiGpioDBlink start endless loop (try JConsole)\n");

    for(;runOn; ++cycCount) {                  // red green time/state  yellow
      pI.logIfBad(pI.setOutput(LEDrd, rLd=HI));// on
      thrDelay(200);                           //           200 ms red
      yLd = !yLd;                              //                       toggle
      pI.logIfBad(pI.setOutput(LEDye, yLd));
      pI.logIfBad(pI.setOutput(LEDgn, gLd=HI));//      on
      thrDelay(100);                           //           100 ms both
      pI.logIfBad(pI.setOutput(LEDrd, rLd=LO));// off
      thrDelay(100);                           //           100 ms green
      pI.logIfBad(pI.setOutput(LEDgn, gLd=LO));//      off
      thrDelay(200);                           //           200 ms dark
    } // for endless
  } // doIt()
} // RdGnPiGpioDBlink
