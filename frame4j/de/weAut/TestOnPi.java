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
package  de.weAut;

import de.weAut.PiUtil;     // Raspberry Pi handling utilities (IO lock)
import de.frame4j.text.TextHelper;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import java.io.IOException;
import javax.management.JMException;

/** <b>TestOnPi &ndash; demonstrate and test IO devices on a Pi</b>.<br />
 *  <br />
 *  This program is a collection of tests and auxiliary functions on 
 *  a Raspberry Pi. <br />
Single functions can be started with settings made by command line options
like e.g. <pre><code>
--GPIO    -G   GPIO number for IO operation
--pin     -p   IO connector pin number for IO operation
--IN   --OUT   set selected GPIO to mode INput respectively OUTput
--ON   --OFF   set selected GPIO ON respectively OFF
--duty    -d   set dutycycle 0..255 for selected GPIO
--servo   -s   set servo pulse width 0:off; 500 ..1500:middle.. 2500:clockwise
--wait    -w   wait for about 1 second
</code></pre>
 *  Additionally to command line arguments, the settings and functions might
 *  be made or called via JMX/JConsole. <br >
 *  <pre><code>  
   Copyright  &copy;  2021  Albrecht Weinert <pre><code>
   weinert-automation.de        a-weinert.de

     /         /      /\
    /         /___   /  \      |
    \        /____\ /____\ |  _|__
     \  /\  / \    /      \|   |
      \/  \/   \__/        \__/|_
</code></pre>
 *  @see de.weAut.demos.BlinkOnPi
 *  @see TestOnPiMBean
 *  @see PiUtil
 *  @see <a href="./doc-files/TestOnPi.properties">TestOnPi.properties</a>
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  46 (11.05.2021)
 */
// so far:   V.  21  (21.05.2019) : new, minimal functionality
//           V.  26  (31.05.2019) : three LEDs, IO lock 
//           V.  35  (01.04.2021) : MBean for JConsole
//           V.  47  (12.05.2021) : end of rudimentary prototype state 
@MinDoc(
  copyright = "Copyright 2021  A. Weinert",
  author    = "Albrecht Weinert",
  version   = "V.46",
  lastModified   = "11.05.2021",
  usage   = "start as Java application (-? for help)",  
  purpose = "a Frame4J program to test IO devices on a Pi via pigpioD"
) public class TestOnPi extends App implements PiUtil, TestOnPiMBean {

  @Override public final boolean parsePartial(){ return true; }
 
//-- values set by parameters or (partly) by JMX/JConsole    

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

/** The input or button. <br />
 *  <br />
 *  This is a general purpose input (to be tested). <br />
 *  On a trafficPi shield e.g. the pin for a lo-active button (needs
 *  a pull up) is 7. Hence, as mnemonic, the names for options, setters and
 *  getters refer to / abbreviate button.<br />
 *  <br />
 *  default: {@link PiVals#PINig  PINig ignore, no operation} resp. 0 
 */
  public int buttPin = 0, buttGpio = PINig;
    
/** The GPIO respectively pin to use for (single / non LED) operations. <br />
 *  <br />
 *  This is general purpose output to be tested. <br />
 *  On a trafficPi shield, e.g., besides the 12 LEDs, this could be the 
 *  buzzer. It is controlled (hi-active) by pin 12. Hence, as mnemonic,
 *  the names for options, setters and  getters partly refer to / 
 *  abbreviate buzzer.<br />  
 *  default: {@link PiVals#PINig  PINig ignore, no operation} resp. 0 
 */
   public int gpio = PINig, pin = 0; 
  
//--- internal state  exposed as MBean -------------------------------------  
  
  int cycCount; // incremented on every red & green blink cycle (600ms)
  boolean yLd; // status of the yellow LED(s)
  boolean rLd; // status of the red LED(s)
  boolean gLd; // status of the green LED(s)
  
  
  int stepDelay = 10; // delay between operations; default 10ms
  int stepVal = 10; // step between two PMW or servo values; default 10
  int val = 100; // PMW or servo current value; default 100
  int valUp = 255; // PMW or servo value upper limit; default 255
  int valLo = 0; // PMW or servo value lower limit; default 0
  
  boolean leBut0; // status of the button (the one input here)
  boolean leBut0prev; // previous status of the button (for flank action)
//  int leBuz0; // status of buzzer (the extra output here) use val

    
//----  MBean Operations / Implementation    -------------------------------

  @Override public Integer getCycCount(){ return cycCount; }
  @Override public void setCycCount(Integer cycCount){
                           TestOnPi.this.cycCount = cycCount; }
  @Override public Integer resetCycCount(){ 
   final int ret = cycCount;  cycCount= 0; 
   return ret;
  } // resetCycCount()
  @Override public Boolean getLEDye(){ return yLd; }
  @Override public Boolean getLEDgn(){ return gLd; }
  @Override public Boolean getLEDrd(){ return rLd; }
  @Override public Integer getPiType(){ return pI.thePi.type(); }
  
  @Override public Boolean getLeBut(){ return leBut0; }
  @Override public Boolean getLeBuz(){ return val> 21; }
  @Override public void setLeBuz(final Boolean on){
    final int booLeBuz = on ? 255 : 0;
    if (val == booLeBuz) return;
    pI.logIfBad(pI.setOutput(gpio, (val = booLeBuz) == 255));
  }
  @Override public void setLeBuzPWM(final Integer pwm){
    pI.logIfBad(pI.setPWMcycle(gpio, val = pwm));
  } // setLeBuzPWM(Integer)
  @Override public Integer getLeBuzPWM(){ return val; } 

  
  @Override public Integer getGpio(){ return gpio; }
  @Override public Integer getPin(){
    return pI == null ? pin : pI.thePi.gpio2pin(gpio); 
  } // getPin()
  
  @Override public void setGpio(Integer gpio){
    if (gpio < 0 || gpio > 31) { 
      this.pin = 0;
      this.gpio = PINig;
    }
    if (pI != null) pin = pI.thePi.gpio2pin(gpio); 
    this.gpio = gpio;
  } // setGpio(Integer)
  
  @Override public void setPin(Integer pin){ 
    if (pin < 1 || pin > 40) {
      this.gpio = PINig;
      this.pin = 0; 
    }
    if (pI != null) {
      this.gpio = pI.thePi.gpio4pin(pin);
      if (this.gpio >= PINig) this.pin = 0; 
    }
    this.pin = pin;
  } // setPin(Integer)

  @Override public final void setStepDelay(final Integer delay){
    int i = delay;
    this.stepDelay = i >= 2 && i < 30000 ? i : 10;
  } // setDelay(Integer)

  @Override public final Integer getStepDelay(){ return this.stepDelay; }

 @Override public final void setStepVal(final Integer stepVal){ 
   this.stepVal = stepVal;
 } // setStepVal(Integer)

  @Override public final Integer getStepVal(){ return stepVal; }
  
  @Override public void setVal(final Integer val){
    final int i = val;
    this.val = i <= 0 ? 0 : i >= 2500 ? 2500 : i;
  } // setVal(Integer)

  @Override public Integer getVal(){ return val; }

  @Override public void setValUp(Integer valUp){
    final int i = valUp;
    this.valUp = i <= 0 ? 0 : i >= 2500 ? 2500 : i;
  } // setValUp(Integer)

  @Override public Integer getValUp(){ return valUp; }

  @Override public void setValLo(Integer valLo){
    final int i = valLo;
    this.valLo = i <= 0 ? 0 : i >= 2500 ? 2500 : i;
  } // setValLO(Integer)

  @Override public Integer getValLo(){ return valLo; }

//--------------------------------------------------------------------------
 
 ClientPigpiod  pI; // Pi and its connection

/** The application start. <br />
 *  <br />
 *  Will blink with three LEDs in an endless loop (in {@link #doIt()}). 
 *  Can be stopped by signal (cntlC), kill command and the like, as well as
 *  by an MBean client, like e.g. JConsole, by operation {@link #stop()}.
 *  
 *  Run by java TestOnPi [host [port]] [-option [-option ....<br />
 *  LEDs rd,ye,gn Options are:
{@code -north  29 31 33; -east 36 38 40;  -south 11 13 15; -west 16 18 22;}<br />
{@code -LEDefault 11 22 13} <br />
 *  Pi type options are:
{@code -pi0 -pi1 -pi3 -pi4} (0, 1, 3 and 4 map to 3, the default)<br />
 *  @param args start parameters.
 */
  public static void main(String[] args){
    try { new TestOnPi().go(args);
   } catch (Exception e) {  AppBase.exit(e, INIT_ERROR); }
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
    out.println(formMessage("startOn") );
    String oName = null;
    try {
      oName = regAsStdMBean(); // registration as MBean 
      out.println("\n  " + PROG_SHORT + " MBean: " + oName); // success
    } catch (JMException ex) { repExc(out, ex, false); } // report fail
    
    String restArgs = TextHelper.prepParams(args);
    if (restArgs != ComVar.EMPTY_STRING) {
      out.println("\n  " + PROG_SHORT + " argsLeft: " + restArgs); // success
    }

    if (getUseLock()) {
      final int oL = openLock(null, false);
      if (oL != 0) return errorExit(oL, formMessage("errLock")
                                                    + PiUtil.errorText(oL));
      out.println(formMessage("gotLock")); // TestOnPi got file/process
    } else {
      out.println(formMessage("noLockFil")); // no lock file/process
    }
    try {
      pI = ClientPigpiod.make(this); 
      out.println("\n  TestOnPi connect " + pI);
    } catch (IOException ex) {
      return errorExit(ERR_PIGPIOD_CON, ex, 
                                          PiUtil.errorText(ERR_PIGPIOD_CON));
    }  // make and connect
    try { // assign GPIOs to LED pins
       rdLED = pI.thePi.gpio4pinChck("red LED", ledRDpin);
       gnLED = pI.thePi.gpio4pinChck("grn LED", ledGNpin);
       yeLED = pI.thePi.gpio4pinChck("yel LED", ledYEpin);
       if (pin > 0 ) {
        gpio = pI.thePi.gpio4pinChck("singleIO", pin);
       } else {
         pin = pI.thePi.gpio2pin(gpio);
       }
    } catch (IOException  ex) {
      return errorExit(ERR_ASSIGN_PIN, ex, PiUtil.errorText(ERR_ASSIGN_PIN));
    } // assign GPIOs to LED pins

    out.println("  TestOnPi set mode output rd ye gn/up  14 mA");
    pI.logCommand(pI.setMode(rdLED, GPIO_OUT));
    pI.logCommand(pI.setMode(gnLED, GPIO_OUT));
    pI.logCommand(pI.setMode(yeLED, GPIO_OUT));
    pI.logCommand(pI.setPullR(gnLED, PI_PUD_UP));
    pI.logCommand(pI.setPadS(0, 14));
    out.println("  TestOnPi start endless loop (try JConsole)\n");

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
      out.println("\n  TestOnPi shutdown " + pI + "    cyc/ovr: "
                     + getCycCnt() + "/" + getOvrCnt());
      pI.releaseOutputsReport(out);
      try {
         pI.disconnect();
      } catch (IOException e) { } 
        // pigpio.gpioTerminate();
    } else { // pI not null else null
      out.println("\n  TestOnPi shutdown ");
    } // pI null
    closeLock();
    return 0; // normal end
  } // doIt()
} // TestOnPi (April 2021)
