/*  Copyright 2021 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.MinDoc;
import de.weAut.ClientPigpiod;
import java.io.IOException;
import javax.management.JMException;

/** <b>BlinkOnPi &ndash; a Java pigpiod test and demo program</b>.<br />
 *  <br />
 *  This program is based on a port of pigpiod test and demo program
 *  rdGnPiGpioDBlink from C to Java. It uses Joan N.N's pigpio library's 
 *  <a href="http://abyz.me.uk/rpi/pigpio/sif.html">socket interface</a> 
 *  directly. Hence, it is 100% pure Java &mdash; that means no JNI
 *  (Java native interface) is used.<br />
 *  <br />
 *  Comment excerpt of the original/ported C source file: <br /><pre>
  A fifth program for Raspberry's GPIO pins

  Rev. $Revision: 43 $  $Date: 2021-05-04 20:53:48 +0200 (Di, 04 Mai 2021) $

  Copyright  (c)  2019   Albrecht Weinert <br />
  weinert-automation.de      a-weinert.de

  It uses two/three pins as output assuming two LEDs connected to as H=on
  Pi 1  / Pi 3       Pin
  GPIO17/ 17 : red    11
  GPIO21/ 27 : green  13
  GPIO25     : yellow 22  (since Revision 45)
  This program forces application singleton and may be used as service.  

  Its functions are the same as rdGnBlinkBlink (even sharing the lockFile)
  except for using the pigpioD library. Our makefiles define
  MCU and PLATFORM as make variables and macros. So we could make the
  GPIO pin and address assignment automatically in the C programmes.
  (End of original C comment excerpt)
</pre><br />  
 *  In Java, for good reasons, we do not have the "make and macro and all can
 *  be automatically changed" mechanism. In a Java program, by playing with
 *  inheritance and polymorphism, we can postpone the Pi type decision in a 
 *  better way to runtime, e.g. by making a {@link de.weAut.ThePi ThePi}
 *  object either by a {@link de.weAut.Pi3} (for Pi0, 3 and 4) or
 *  {@link de.weAut.Pi1} interface's inner class.<br />
 *  <br />
 *  Settings, start parameter evaluation etc. are defined by
 *  <a href="../doc-files/PiUtil.properties">PiUtil.properties</a> and
 *  <a href="./doc-files/BlinkOnPi.properties">BlinkOnPi.properties</a>.
 *
 *  Copyright  &copy;  2021  Albrecht Weinert <br />
 *  @see BlinkOnPiMBean TestOnPi
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 43 $ ($Date: 2021-05-04 20:53:48 +0200 (Di, 04 Mai 2021) $)
 */
// so far:   V.  21  (21.05.2019) : new, minimal functionality
//           V.  26  (31.05.2019) : three LEDs, IO lock 
//           V.  35  (01.04.2021) : MBean for JConsole
@MinDoc(
  copyright = "Copyright 2021  A. Weinert",
  author    = "Albrecht Weinert",
  version   = "V.$Revision: 43 $",
  lastModified   = "$Date: 2021-05-04 20:53:48 +0200 (Di, 04 Mai 2021) $",
  usage   = "start as Java application (-? for help)",  
  purpose = "a Frame4J program to blink LEDs on a Pi via pigpioD"
) public class BlinkOnPi extends App implements PiUtil, BlinkOnPiMBean {

  @Override public final boolean parsePartial(){ return true; }
  @Override protected final String extraPropertiesFile(){
    return "de/weAut/PiUtil.properties";
  } // extraPropertiesFile()

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

  ClientPigpiod  pI; // Pi and its connection
  
  public int argPiType = 3; // start argument 
  public int argPort; // 0 -> default 8888
  public int argTimeout = 10000; // 10s default (not yet evaluated as arg)
  public String argHost = null; // null not yet set -> default
  
//--- internal state  exposed as MBean -------------------------------------  
  
  int cycCount; // incremented on every red & green blink cycle (600ms)
  boolean yLd; // status of the yellow LED(s)
  boolean rLd; // status of the red LED(s)
  boolean gLd; // status of the green LED(s)
    
//----  MBean Operations / Implementation    -------------------------------
  
 @Override public Integer getCycCount(){ return cycCount; }
 @Override public void setCycCount(Integer cycCount){
                           BlinkOnPi.this.cycCount = cycCount; }
 @Override public void resetCycCount(){ cycCount = 0; }
 @Override public Boolean getLEDye(){ return yLd; }
 @Override public Boolean getLEDgn(){ return gLd; }
 @Override public Boolean getLEDrd(){ return rLd; }
 @Override public Integer getPiType(){ return pI.thePi.type(); }
 
//-------------------------------------------------------------------------- 

/** The application start. <br />
 *  <br />
 *  Will blink with three LEDs in an endless loop (in {@link #doIt()}). 
 *  Can be stopped by signal (cntlC), kill command and the like, as well as
 *  by an MBean client, like e.g. JConsole, by operation {@link #stop()}.
 *  
 *  Run by java BlinkOnPi [host [port]] [-option [-option ....<br />
 *  LEDs rd,ye,gn Options are:
{@code -north  29 31 33; -east 36 38 40;  -south 11 13 15; -west 16 18 22;}<br />
{@code -LEDefault 11 22 13} <br />
 *  Pi type options are:
{@code -pi0 -pi1 -pi3 -pi4} (0, 1, 3 and 4 map to 3, the default)<br />
 *  @param args start parameters.
 */
  public static void main(String[] args){
    try {
      new BlinkOnPi().go(args);
   } catch (Exception e) {
      AppBase.exit(e, INIT_ERROR);
   }
  } // main(String[])
    
//--- the GPIOs --------------  
  
  int rdLED = PINig;
  int gnLED = PINig;
  int yeLED = PINig;
                          
/** The application's work. <br />
 *  <br />
 *  When  the startup succeeds this will run in an endless loop until 
 *  killed by signal or stopped by MBean/JConsole command.
 *  @return 0: application ended OK; no errors, no results 
 */
  @Override public int doIt(){
//  boolean loadExtra = prop.load1(this.getClass(),
//    "de/weAut/PiUtil.properties", null); // loadExtra TEST out
    out.println(formMessage("startOn") );
      // + "\n  ### loadExtra = " + loadExtra); // loadExtra TEST out
    String oName = null;
    try {
      oName = regAsStdMBean();
      out.println("\n  " + PROG_SHORT + " MBean: " + oName);
    } catch (JMException ex) {
      
    } // registration as MBean failed
    if (getUseLock()) {
      final int oL = openLock(null, false);
      if (oL != 0) return errorExit(oL, formMessage("errLock")
                                                    + PiUtil.errorText(oL));
      out.println(formMessage("gotLock")); // BlinkOnPi got file/process
    } else {
      out.println(formMessage("noLockFil")); // no lock file/process
    }
    try {
      //  out.println("  BlinkOnPi TEST piType=" + argPiType);
      pI = ClientPigpiod.make(argHost, argPort, argTimeout,
                                                           argPiType, this); 
      out.println("  BlinkOnPi connect " + pI);
    } catch (IOException ex) {
      return errorExit(ERR_PIGPIOD_CON, ex, 
                                          PiUtil.errorText(ERR_PIGPIOD_CON));
    }  // make and connect
    try { // assign GPIOs to LED pins
       rdLED = pI.thePi.gpio4pinChck("red LED", ledRDpin);
       gnLED = pI.thePi.gpio4pinChck("grn LED", ledGNpin);
       yeLED = pI.thePi.gpio4pinChck("yel LED", ledYEpin);
    } catch (IOException  ex) {
      return errorExit(ERR_ASSIGN_PIN, ex, PiUtil.errorText(ERR_ASSIGN_PIN));
    } // assign GPIOs to LED pins

    out.println("  BlinkOnPi set mode output rd ye gn/up  14 mA");
    pI.logCommand(pI.setMode(rdLED, GPIO_OUT));
    pI.logCommand(pI.setMode(gnLED, GPIO_OUT));
    pI.logCommand(pI.setMode(yeLED, GPIO_OUT));
    pI.logCommand(pI.setPullR(gnLED, PI_PUD_UP));
    pI.logCommand(pI.setPadS(0, 14));
    out.println("  BlinkOnPi start endless loop (try JConsole)\n");

    for(;isRunFlag(); ++cycCount) {           //   red yellow green time/state 
      pI.logIfBad(pI.setOutput(rdLED, rLd=HI)); // on
      thrDelay(200); if (!isRunFlag()) break;  //                  200 ms red
      yLd = !yLd;                             //       toggle
      pI.logIfBad(pI.setOutput(yeLED, yLd)); //        on off
      pI.logIfBad(pI.setOutput(gnLED, gLd=HI)); //            on
      thrDelay(100); if (!isRunFlag()) break;   //                 100 ms both
      pI.logIfBad(pI.setOutput(rdLED, rLd=LO)); // off
      thrDelay(100); if (!isRunFlag()) break;   //                 100ms green
      pI.logIfBad(pI.setOutput(gnLED, gLd=LO)); //             off
      thrDelay(200);                            //                 200 ms dark
    } // for endless (leave on stop signals)
    
    // shutdown tasks
    if (pI != null) {
      out.println("\n  BlinkOnPi shutdown " + pI + "    cyc/ovr: "
                     + getCycCnt() + "/" + getOvrCnt());
      pI.releaseOutputsReport(out);
      try {
         pI.disconnect();
      } catch (IOException e) { } 
        // pigpio.gpioTerminate();
    } else { // pI not null else null
      out.println("\n  BlinkOnPi shutdown ");
    } // pI null
    closeLock();
    return 0; // normal end
  } // doIt()
} // BlinkOnPi (April 2021)
