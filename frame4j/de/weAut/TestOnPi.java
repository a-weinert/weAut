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
import de.weAut.ClientPigpiod.CmdState;
import de.frame4j.text.TextHelper;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import java.io.IOException;
import java.util.Arrays;
import javax.management.JMException;

/** <b>TestOnPi &ndash; Demonstrate and test IO devices on a Pi</b>.<br />
 *  <br />
 *  This program is a collection of tests and auxiliary functions on 
 *  a Raspberry Pi. <br />
Single functions can be started with settings made by command line options
like e.g. <pre><code>
-input       select input gpio or pin (prefix P) and optional pull resistor
             settings (postfix U D or N); P0 means no input
-output      dto. for (non LED) output operations
-ON    -OFF  set selected (non LED) output ON respectively OFF
-val     -v  set (by next parameter) dutycycle 0..255 or servo pulse
             width 0:off; 500 ..1500:middle.. 2500:clockwise for the output
-wait    -w  wait for about 1 second
-help        help output. See for further command line options and more.
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
 *  @version  $Revision: 49 $ ($Date: 2021-05-19 16:47:26 +0200 (Mi, 19 Mai 2021) $)
 */
// so far:   V.  21  (21.05.2019) : new, minimal functionality
//           V.  26  (31.05.2019) : three LEDs, IO lock 
//           V.  35  (01.04.2021) : MBean for JConsole
//           V.  47  (12.05.2021) : end of rudimentary prototype state 
@MinDoc(
  copyright = "Copyright 2021  A. Weinert",
  author    = "Albrecht Weinert",
  version   = "V.$Revision: 49 $",
  lastModified   = "$Date: 2021-05-19 16:47:26 +0200 (Mi, 19 Mai 2021) $",
  usage   = "start as Java application (-? for help)",  
  purpose = "a Frame4J program to test IO devices on a Pi via pigpioD"
) public class TestOnPi extends App implements PiUtil, TestOnPiMBean {

  @Override public final boolean parsePartial(){ return true; }
  
  public final boolean prgTst = true; // true test debug for development
 
//-- values set by parameters or (partly) by JMX/JConsole -----------   

/** The LEDs to blink. <br />
 *  <br />
 *  The default pins for a maximum of (each) five red yellow and green 
 *  LEDs are all (15) "no pin" (i.e. pin number 0). 
 */
  public int[] ledRDpins = new int[5], // max. 5 red LEDs
                ledYEpins= new int[5], // dto. yellow
                ledGNpins= new int[5]; // dto. green

// setter for indexed property (must be public)
// no checks here .properties and/or arguments must be valid
/** Indexed property ledRDpin[], setter. <br /> */ 
  public void setLedRDpin(int index, int pin){
    if (prgTst) out.println("  // TEST ledRDpin[" + index + "] = " + pin);
    ledRDpins[index] = pin;
  } // setLedRDpin(2*int)
/** Indexed property ledYEpin[], setter. <br /> */ 
  public void setLedYEpin(int index, int pin){
    if (prgTst) out.println("  // TEST ledYEpin[" + index + "] = " + pin);
    ledYEpins[index] = pin;
  } // setLedYEpin(2*int)
/** Indexed property ledGNpin[], setter. <br /> */
  public void setLedGNpin(int index, int pin){
    if (prgTst) out.println("  // TEST ledGNpin[" + index + "] = " + pin);
    ledGNpins[index] = pin;
  } // setLedGNpin(2*int) 

/** The input or button. <br />
 *  <br />
 *  This is a general purpose input. <br />
 *  On a trafficPi shield e.g. the pin for a lo-active button (needs
 *  a pull up) is 7. Hence, as mnemonic, the names for options, setters and
 *  getters refer to / abbreviate button.<br />
 *  <br />
 *  default: {@link PiVals#PINig  PINig} resp. 0 : ignore no operation
 *           and {@link PI_PUD_KP} : keep pull up/down/none as is 
 */
  public int buttPin = 0, buttGpio = PINig, buttPUD = PI_PUD_KP; 
    
/** The GPIO respectively pin to use for (single / non LED) operations. <br />
 *  <br />
 *  This is general purpose to test a device connected. <br />
 *  On a trafficPi shield, e.g., besides the 12 LEDs, this could be the 
 *  buzzer. It is controlled (hi-active) by pin 12. Hence, as mnemonic,
 *  the names for options, setters and  getters partly refer to / 
 *  abbreviate buzzer.<br />  
 *  default: {@link PiVals#PINig  PINig} resp. 0 : ignore no operation
 *           and {@link PI_PUD_KP} : keep pull up/down/none as is           
 */
   public int outGpio = PINig, outPin = 0, outPUD = PI_PUD_KP; 
  
//--- state / control  -  exposed as MBean ---------------------------------  
  
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
    
//----  MBean Operations / Implementation    -------------------------------

  @Override public final Integer getCycCount(){ return cycCount; }
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
    final int i = val.intValue();
    this.val = i <= 0 ? 0 : i >= 2500 ? 2500 : i;
  } // setVal(Integer)

  @Override public Integer getVal(){ return val; }

  @Override public void setValUp(Integer valUp){
    final int i = valUp.intValue();
    this.valUp = i <= 0 ? 0 : i >= 2500 ? 2500 : i;
  } // setValUp(Integer)

  @Override public Integer getValUp(){ return valUp; }

  @Override public void setValLo(Integer valLo){
    final int i = valLo.intValue();
    this.valLo = i <= 0 ? 0 : i >= 2500 ? 2500 : i;
  } // setValLO(Integer)

  @Override public Integer getValLo(){ return valLo; }
  
//-------------- non LED input and output ------------------ 
 
/** Set GPIO number for (non LED) output operations. <br /> */
  public void setOutGpio(final int gpio){
    this.outGpio = (gpio < 0 || gpio > 31) ? PINig : gpio;
    if (this.outGpio >= PINig) { 
      this.outPin = 0;  this.outPUD = PI_PUD_KP;
      return; // no pin no setting
    } 
    if (pI != null) this.outPin = pI.thePi.gpio2pin(this.outGpio);
  } // setOutGpio(int)   

/** Set pin for (non LED) output operations. <br /> */
  public void setOutPin(int pin){
    if (pI != null) {
      this.outGpio =  pI.thePi.gpio4pin(pin);
      if (this.outGpio < 31) { this.outPin = pin; return; } //ready
      pin = 0; // assure error
    } // have the Pi already
    if (pin < 1 || pin > 40) { // error
      this.outPin = 0; this.outGpio = PINig; this.outPUD = PI_PUD_KP;
      return; // no pin no setting
    } // error
    this.outPin = pin; // no Pi yet, set pin, only
  } // setOutPin(int)
  
/** Set GPIO number for input or output operations. <br /> */
  public void setGpio(final boolean out, final int gpio){
    if (out) { setOutGpio(gpio);
    } else setInGpio(gpio);
  } // setGpio(boolean, int)

/** Set pin for input or output operations. <br /> */
  public void setPin(final boolean out, final int pin){
    if (out) { setOutPin(pin);
    } else setInPin(pin);
  } // setPin(boolean out, int)

/** Set the output for (non LED) actions. <br /> 
*  <br />
*  This method sets the output port either as pin number or as GPIO with a
*  prefix G. A postfix U, D or N will set the pull resistor as up, down or
*  none.<br />
*  Without postfix the pull resistor setting will be kept untouched. 
*  @param outPort output pin (12N, e.g.) or gpio (with leading G; G18, e.g.)
*/
  @Override public void setOutPort(String outPort){ setPort(true, outPort); }
    
   
/** Set the input or output for (non LED) actions. <br /> 
*  <br />
*  This method sets the output port either as pin number or as GPIO with a
*  prefix G. A postfix U, D or N will set the pull resistor as up, down or
*  none.<br />
*  Without postfix the pull resistor setting will be kept untouched. 
*  @param port output pin (12N, e.g.) or gpio (with leading G; G18, e.g.)
*/
  public void setPort(final boolean out, String port){ 
    port = TextHelper.trimUq(port, EMPTY_STRING);
    int len = port.length();
    if (len == 0) { setGpio(out, PINig); return; } // no output
    char c0 = port.charAt(0);
    int num = 0;
    int nx = 1;
    boolean isGPIO = c0  == 'G' || c0 == 'g';
    if (isGPIO || c0  == 'P' || c0 == 'G') { // c0 said pin or gpio
      if (len == 1) { setGpio(out, PINig); return; } // none specified
      c0 = port.charAt(1); nx = 2;
    } // c0 (before) said pin or gpio
    if (c0 < '0' || (num = c0 - '0') > 9) { // c0 must be cif  but is not 
      setGpio(out, PINig); return; // none specified (format error)
    } // c0 must be cif  but is not
    while (nx < len) { // 2nd cif and / or PUD (U D N) follow
      c0 = port.charAt(nx); ++nx;
      if (c0 <= ' ') break; // end of value (start comment)
      if (c0 >= '0' &&  c0 <= '9') { // 2nd cif
        num = num * 10 + c0 - '0';
        if (num >= 99) break; // error ends c0 character loop
        continue;
      } // 2nd cif
      // to here no cif; must be U D N K (u d n k)
      if (c0 >= 'a') c0 -= 32; // poor man's toUpperCase for primitive USascii
      if (c0 == 'G' || c0 == 'P') break; // ignore redundant gpio or pin
      int pud = PI_PUD_KP;  // keep PUD (restore default)
      if (c0 == 'D') pud = PI_PUD_DOWN;
      else if (c0 == 'U') pud = PI_PUD_UP;
      else if (c0 == 'N') pud = PI_PUD_OFF;  // shall we accept O also ?
      else if (c0 != 'K') num = 100; // now in error anyway
      if (pud == PI_PUD_KP) break;
      if (out) outPUD = pud; else buttPUD = pud;
      break; // postfix ends char loop
    } // while c0 character loop
    if (isGPIO) { setGpio(out, num); // set by GPIO number (prefix G)
    } else setPin(out, num);  // set by pin number
  } // setPort(boolean, String)

/** Set the input for input actions. <br />
 *  <br />
 *  This method sets the input port either as pin number or as GPIO with a
 *  prefix G. A postfix U, D or N will set the pull resistor as up, down or
 *  none. Without postfix the pull resistor setting will be kept untouched. 
 *
 *  @param inPort input pin (7, e.g.) or gpio (with leading G; G4U, e.g.)
 */
  @Override public void setInPort(String inPort){ setPort(false, inPort); }

  @Override public String getOutPort(){
    if (pI == null) return   "unknown"; 
    return pI.pinDescr(outPin, outPUD);
  } // getOutPort()
  @Override public String getInPort(){
    if (pI == null) return   "unknown"; 
    return pI.pinDescr(buttPin, buttPUD);
  } // getInPort()
  
/** Set GPIO for input operations. <br /> */
  public void setInGpio(final int gpio){
    this.buttGpio = (gpio < 0 || gpio > 56) ? PINig : gpio;
    if (this.buttGpio >= PINig) { 
      this.buttPin = 0; this.buttPUD = PI_PUD_KP; // ignore
      return;
    } 
    if (pI != null) this.buttPin = pI.thePi.gpio2pin(this.buttGpio);
  } // setIntGpio(int)

/** Set pin for input operations. <br /> */
  public void setInPin(int pin){
    if (pI != null) {
      this.buttGpio =  pI.thePi.gpio4pin(pin);
      if (this.buttGpio < 56) { this.buttPin = pin; return; }
      pin = 0; // assure error
    } // have the Pi already
    if (pin < 1 || pin > 40) { // error
      this.buttPin = 0; this.buttGpio = PINig; this.buttPUD = PI_PUD_KP;
      return; // no pin no setting
    } // error
    this.buttPin = pin; // no Pi yet, set pin, only
  } // setOutPin(int)

//-------------- actions -----------------------------------

/** Blink the LEDs. <br /> */
  @Override public void blink(){;} // todo

/** Servo wink respectively PWM up/down. <br /> */
  @Override public void wink(){;} // todo 

  @Override public void setOutput(final String out){
    if (pI == null) return; // to early
    int ret = pI.setOutput(outGpio, out);
    if (ret < 0) { // error
      this.val = ret;
    } else { // retrieve val from command parameter
      final CmdState cmdSt = ClientPigpiod.lastCmdState.get();
      final int lastCmd = cmdSt.lastCmd;
      this.val = cmdSt.lastP2;
      if (lastCmd == PiGpioDdefs.PI_CMD_WRITE && this.val == 1) this.val = 255;  
    }  // retrieve val from command parameter
  } // setOutput(String) 

/** Input and set the output with the inverted result. <br /> */
  @Override public Boolean input(){ 
    if (buttGpio >= PINig || pI == null) return Boolean.FALSE; // no input
    int in = pI.getInp(buttGpio);
    if (outGpio < 32) pI.setOutput(outGpio, in == 0); // output inverted
    return in == 1 ? Boolean.TRUE : Boolean.FALSE; 
  } // todo

/** The state of the output. <br />
*  <br />
*  This method returns the last state of the (non LED) output, as
*  set by {@link #setOutput(String) setOutput(value)} or other actions.
*/
  @Override public String getOutput(){ 
    if (val < 0)    return " error";
    if (val ==   0) return "Off Lo";
    if (val == 255) return "On  Hi";
    if (val < 0 || val > 2500 || ( val > 255
     && val < 500)) return "undef'd";
    StringBuilder dest = new StringBuilder(8); 
    if (val <= 255) {
      return dest.append("PWM").append(TextHelper.threeDigit(val)).toString();
    }
    dest.append("Sr");
    dest.append(val > 1000 ? (val < 2000 ? '1' : '2') : 'v');
    return dest.append(TextHelper.threeDigit(val)).toString();
  } // getOutput()

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
  
  int rdLEDs; // GPIO masks for LEDs  (0 = no entry)
  int gnLEDs, yeLEDs;
                          
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
       rdLEDs = pI.thePi.gpios4pinsChck("red LED", ledRDpins);
       gnLEDs = pI.thePi.gpios4pinsChck("grn LED", ledGNpins);
       yeLEDs = pI.thePi.gpios4pinsChck("yel LED", ledYEpins);
       out.println("\n  " + PROG_SHORT + " rdLEDs: " + 
                            TextHelper.eightDigitHex(null, rdLEDs)
           + " by " + Arrays.toString(ledRDpins)); // TEST
       if (outPin > 0 ) {
        outGpio = pI.thePi.gpio4pinChck("singleIO", outPin);
       } else {
         outPin = pI.thePi.gpio2pin(outGpio);
       }
    } catch (IOException  ex) {
      return errorExit(ERR_ASSIGN_PIN, ex, PiUtil.errorText(ERR_ASSIGN_PIN));
    } // assign GPIOs to LED pins

    out.println("  TestOnPi set mode output rd ye gn/up  14 mA");
 //   pI.logCommand(pI.setMode(rdLED, GPIO_OUT));
 //   pI.logCommand(pI.setMode(gnLED, GPIO_OUT));
 //   pI.logCommand(pI.setMode(yeLED, GPIO_OUT));
 //   pI.logCommand(pI.setPullR(gnLED, PI_PUD_UP));
    pI.logCommand(pI.setPadS(0, 14));
    pI.setAsOutputsReport("red LED", rdLEDs);
    pI.setAsOutputsReport("yel LED", yeLEDs);
    pI.setAsOutputsReport("grn LED", gnLEDs);
    out.println("  TestOnPi start endless loop (try JConsole)\n");

    for(;isRunFlag(); ++cycCount) {          //   red yellow green time/state 
      pI.logIfBad(pI.setOutSet(rdLEDs, rLd=HI));// on
      thrDelay(200); if (!isRunFlag()) break;  //                  200 ms red
      yLd = !yLd;                             //       toggle
      pI.logIfBad(pI.setOutSet(yeLEDs, yLd)); //       on off
      pI.logIfBad(pI.setOutSet(gnLEDs, gLd=HI)); //            on
      thrDelay(100); if (!isRunFlag()) break;   //                 100 ms both
      pI.logIfBad(pI.setOutSet(rdLEDs, rLd=LO));// off
      thrDelay(100); if (!isRunFlag()) break;   //                 100ms green
      pI.logIfBad(pI.setOutSet(gnLEDs, gLd=LO)); //           off
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
