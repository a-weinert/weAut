
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

/** <b>BlinkOnPi &ndash; a pigpiod test and demo program</b>.<br />
 *  <br />
 *  This program uses Joan N.N's pigpio library's 
 *  <a href="http://abyz.me.uk/rpi/pigpio/sif.html">socket interface</a> 
 *  directly. Hence, it is 100% pure Java &mdash; that means no JNI
 *  (Java native interface) is used.<br />
 *  <br />
 *  Its main function is to blink three LEDs:<br /><pre>
  Pi 1  / Pi 3       Pin
  GPIO17/ 17 : red    11
  GPIO25     : yellow 22 
  GPIO21/ 27 : green  13</pre>
 *  Additionally, a Lo input on Pin {@code 7} will set Pin {@code 12} Hi.
 *  Those Pins are a Lo active button and a buzzer in the trafficPi shield
 *  (featuring also 12 LEDs as traffic lights on a cross road to play
 *  with).<br />
 *  The button to buzzer function can be disabled by start parameter
 *  {@code -noButBuz} and the four (trafficPi) traffic light can be chosen
 *  as LEDs by options {@code -north -east -south} or {@code -west}.<br />
 *  This program's  help output by<br/> 
 *  &nbsp; &nbsp; {@code java de.weAut.demos.BlinkOnPi -help [-en | -de]}<br />
 *  will show all options and parameters. {@code -en} or {@code -de} will
 *  switch the program's (help) output to English respectively German if
 *  the operating system tells otherwise.<br />
 *  <br />
 *  In Java, for good reasons, we do not have the "make and macro and all can
 *  be automatically changed" mechanism. In a Java program, by playing with
 *  inheritance and polymorphism, we can postpone the Pi type decision in a 
 *  better way to runtime, e.g. by making a {@link de.weAut.ThePi ThePi}
 *  object either by a {@link de.weAut.Pi3} (for Pi0, 3 and 4),
 *  {@link de.weAut.Pi1} or {@link de.weAut.Pi2} interface's inner class.<br />
 *  <br />
 *  Settings, start parameter evaluation etc. are defined by
 *  <a href="./doc-files/BlinkOnPi.properties">BlinkOnPi.properties</a>.<br />
 *  <br />
 *
 *  Copyright  &copy;  2021  Albrecht Weinert <br />
 *  @see BlinkOnPiMBean
 *  @see de.weAut.TestOnPi
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 56 $ ($Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $)
 *  @see BlinkOnPiMBean
 *  @see PiUtil
 *  @see <a href="./doc-files/BlinkOnPi.properties">BlinkOnPi.properties</a>
 *  @see <a href="../doc-files/Raspi4testPins.png"
 *   title="GPIOs and pins">Raspi4testPins</a>
 */
// so far:   V.  21  (21.05.2019) : new, minimal functionality
//           V.  26  (31.05.2019) : three LEDs, IO lock 
//           V.  35  (01.04.2021) : MBean for JConsole
//           V.  45  (08.05.2021) : piTraffic buzzer and button
@MinDoc(
  copyright = "Copyright 2021  A. Weinert",
  version   = "V.$Revision: 56 $",
  lastModified   = "$Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $",
  usage   = "start as Java application (-? for help)",  
  purpose = "a Frame4J program to blink LEDs on a Pi via pigpioD"
) public class BlinkOnPi extends App implements PiUtil, BlinkOnPiMBean {

 // @Override public final boolean parsePartial(){ return true; } // not yet

/** A LED to blink. <br />
 *  <br />
 *  The default pins for rd/ye/gn LED are 11, 22, and 13. 
 *  This is the traditional (2017) experimental setup. <br />
 *  With a piTraffic (cross roads) head  {@link #ledGNpin} would be
 *  south yellow and {@link #ledYEpin} west green.  
 */
  public int ledRDpin = 11, // piTraffic south rd
             ledYEpin = 22, // piTraffic west  gn
             ledGNpin = 13; // piTraffic south ye

/** The buzzer. <br />
 *  <br />
 *  The default pin for a buzzer (respectively its switching transistor) is
 *  12. This is the trafficPi assignment.
 */
  public int buzzPin = 12; // piTraffic buzzer to 5V by npn transistor

/** The button pin. <br />
 *  <br />
 *  The default pin for a low active button (would need a pull up, internal
 *  or external) is 7. This is the trafficPi assignment.
 */
  public int buttPin =  7; // piTraffic button switch to ground

  ClientPigpiod  pI; // Pi and its connection
  
    
//--- internal state  exposed as MBean -------------------------------------  
  
  int cycCount; // incremented on every red & green blink cycle (600ms)
  boolean yLd; // status of the yellow LED(s)
  boolean rLd; // status of the red LED(s)
  boolean gLd; // status of the green LED(s)
  
  boolean leBut0; // status of the button (the one input here)
  boolean leBut0prev; // previous status of the button (for flank action)
  int leBuz0; // status of buzzer (the extra output here)
    
//----  MBean Operations / Implementation    -------------------------------
  
 @Override public Integer getCycCount(){ return cycCount; }
 @Override public void setCycCount(Integer cycCount){
                           BlinkOnPi.this.cycCount = cycCount; }
 @Override public Boolean getLEDye(){ return yLd; }
 @Override public Boolean getLEDgn(){ return gLd; }
 @Override public Boolean getLEDrd(){ return rLd; }
 @Override public Boolean getLeBut(){ return leBut0; }
 @Override public Boolean getLeBuz(){ return leBuz0 > 21; }
 @Override public void setLeBuz(final Boolean on){
   final int booLeBuz = on ? 255 : 0;
   if (leBuz0 == booLeBuz) return;
   pI.logIfBad(pI.setOutput(buzz0, (leBuz0 = booLeBuz) == 255));
 }
 @Override public void setLeBuzPWM(final Integer pwm){
   pI.logIfBad(pI.setPWMcycle(buzz0, leBuz0 = pwm));
 } // setLeBuzPWM(Integer)
 @Override public Integer getLeBuzPWM(){ return leBuz0; } 
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
    try { new BlinkOnPi().go(args);
    } catch (Exception e) { AppBase.exit(e, INIT_ERROR); }
  } // main(String[])
    
//--- the GPIOs --------------  
  int rdLED = PINig;
  int gnLED = PINig;
  int yeLED = PINig;
  
  int buzz0 = PINig;
  int butt0 = PINig;
  
/** Helper method delay and button/buzzer IO. <br /> */
  void buttzDel(final int drl){
    leBut0 = pI.getInp(butt0) == 1; // input button
    if (leBut0 != leBut0prev) { // button input changed
      pI.logIfBad(pI.setOutput(buzz0, leBut0prev)); // buzzer 
      leBut0prev = leBut0;
    } // button is Lo active action on change only
    thrDelay(drl); // and now the delay
  } // butuzDel(int) 
                          
/** The application's work. <br />
 *  <br />
 *  When  the startup succeeds this will run in an endless loop until 
 *  killed by signal or stopped by MBean/JConsole command.
 *  @return 0: application ended OK; no errors, no results 
 */
  @Override public int doIt(){
    out.println(formMessage("startOn") );
    try {
      String oName = regAsStdMBean();
      out.println("\n  " + PROG_SHORT + " MBean: " + oName);
    } catch (JMException ex) { } // registration as MBean failed
    if (getUseLock()) {
      final int oL = openLock(null, false);
      if (oL != 0) return errorExit(oL, formMessage("errLock")
                                                    + PiUtil.errorText(oL));
      out.println(formMessage("gotLock")); // BlinkOnPi got file/process
    } else {
      out.println(formMessage("noLockFil")); // no lock file/process
    }
    try { // make and connect
      pI = ClientPigpiod.make(this);                    
      out.println("  BlinkOnPi connect " + pI);
    } catch (IOException ex) {
      return errorExit(ERR_PIGPIOD_CON, ex, 
                                          PiUtil.errorText(ERR_PIGPIOD_CON));
    }  // make and connect
    try { // assign GPIOs to IO pin numbers
     // GPIO =   piType    .                       f(pin);
       rdLED = pI.thePi.gpio4pinChck("red LED", ledRDpin);
       gnLED = pI.thePi.gpio4pinChck("grn LED", ledGNpin);
       yeLED = pI.thePi.gpio4pinChck("yel LED", ledYEpin);
       buzz0 = pI.thePi.gpio4pinChck("buzzerH",  buzzPin);
       butt0 = pI.thePi.gpio4pinChck("buttonL",  buttPin);
    } catch (IOException  ex) {
      return errorExit(ERR_ASSIGN_PIN, ex, PiUtil.errorText(ERR_ASSIGN_PIN));
    } // assign GPIOs to LED pins
    // pI.logCommand(0); // Test log w/o command before (must show "none")
    out.println("  BlinkOnPi set mode output rd ye gn/up  14 mA");
    pI.logCommand(pI.setMode(rdLED, GPIO_OUT));
    pI.logCommand(pI.initAsHiDrive(gnLED, 3));
    pI.logCommand(pI.initAsOutput(yeLED));
    pI.logCommand(pI.setMode(buzz0, GPIO_OUT));
    pI.logCommand(pI.setPadS(0, 14));
    pI.logCommand(pI.initAsLoInput(butt0));
    out.println("  BlinkOnPi start endless loop (try JConsole)\n");

    for(;isRunFlag(); ++cycCount) {           //   red yellow green time/state 
      pI.logIfBad(pI.setOutput(rdLED, rLd=HI)); // on
      buttzDel(200); if (!isRunFlag()) break;  //                  200 ms red
      yLd = !yLd;                             //       toggle
      pI.logIfBad(pI.setOutput(yeLED, yLd)); //        on off
      pI.logIfBad(pI.setOutput(gnLED, gLd=HI)); //             on
      buttzDel(100); if (!isRunFlag()) break;   //                 100 ms both
      pI.logIfBad(pI.setOutput(rdLED, rLd=LO)); // off
      buttzDel(100); if (!isRunFlag()) break;   //                 100ms green
      pI.logIfBad(pI.setOutput(gnLED, gLd=LO)); //             off
      buttzDel(200); if (!isRunFlag()) break;  //                  200 ms dark
    } // for endless (leave on stop signals)
    
    // shutdown tasks
    if (pI != null) {
      out.println("\n  BlinkOnPi shutdown " + pI + "    cyc/ovr: "
                     + getDelCnt() + "/" + getOvrCnt());
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
