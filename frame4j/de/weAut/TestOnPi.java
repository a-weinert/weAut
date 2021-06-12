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
import de.frame4j.util.MinDoc;
import static de.weAut.PiGpioDdefs.PI_CMD_MODES;
import static de.weAut.PiGpioDdefs.PI_INPUT;
import static de.frame4j.util.AppLangMap.valueUL;
import java.io.IOException;
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
 *  <br />
 *  Examples: <pre><code>
 *    java de.weAut.TestOnPi -help
 *    java TestOnPi -?
 *    java TestOnPi -help -de
 *    java TestOnPi -en -help</code></pre>
 *  Help output, optionally using the the Starter application in the anonymous
 *  package (for typing comfort), optionally choosing the output language.
 *  -de (Deutsch) stands for German.<pre><code>
 *    java TestOnPi -blink -south
 *    java TestOnPi -west -east -blink 192.168.178.67
 *    java TestOnPi raspi67 -north -blink -cycLim 3122</code></pre>
 *  Blink the south cross road LEDs on the default Pi. If run on Pi with Java8
 *  and Frame4J this is the Pi itself; on a non Pi machine like a Windows PC
 *  (with Java(8) and Frame4J) this will be a Pi with the IPv4 address ending
 *  with .67 in the same (W)LAN.<br />
 *  Blink two other LED groups optionally naming the (other) Pi explicitly 
 *  by its IP address or blink another group naming the other Pi by its DNS
 *  name optionally limiting the number of (blink) cycles.<br />
 *  Without the limit the program would run (blink) endlessly; it can be
 *  stopped by signal, cntl-C or JMX/JConsole command. <pre><code>
 *    java TestOnPi -outPort 3 -winkServo -de -cycLim 89012 </code></pre>
 *  Test a RC servo attached to Pin 3 (and 5V and Gnd) by winking over almost
 *  the whole (180°) range in many small steps.<br />   
 *  <br /> 
 *  <br /> 
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
 *  @see <a href="./doc-files/Raspi4testPins.png"
 *   title="GPIOs and pins">Raspi4testPins</a>
 *
 *  @version  $Revision: 52 $ ($Date: 2021-06-12 13:01:58 +0200 (Sa, 12 Jun 2021) $)
 */
// so far:   V.  21  (21.05.2019) : new, minimal functionality
//           V.  26  (31.05.2019) : three LEDs, IO lock 
//           V.  35  (01.04.2021) : MBean for JConsole
//           V.  47  (12.05.2021) : end of rudimentary prototype state 
@MinDoc(
  copyright = "Copyright 2021  A. Weinert",
  version   = "V.$Revision: 52 $",
  lastModified   = "$Date: 2021-06-12 13:01:58 +0200 (Sa, 12 Jun 2021) $",
  usage   = "start as Java application (-? for help)",  
  purpose = "a Frame4J program to test IO devices on a Pi via pigpioD"
) public class TestOnPi extends App implements PiUtil, TestOnPiMBean {

  @Override public final boolean parsePartial(){ return true; }
 
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
    if (isTest()) out.println("  // TEST ledRDpin[" + index + "] = " + pin);
    ledRDpins[index] = pin;
  } // setLedRDpin(2*int)
/** Indexed property ledYEpin[], setter. <br /> */ 
  public void setLedYEpin(int index, int pin){
    if (isTest()) out.println("  // TEST ledYEpin[" + index + "] = " + pin);
    ledYEpins[index] = pin;
  } // setLedYEpin(2*int)
/** Indexed property ledGNpin[], setter. <br /> */
  public void setLedGNpin(int index, int pin){
    if (isTest()) out.println("  // TEST ledGNpin[" + index + "] = " + pin);
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
  public ThePi.Port portIn;
  String inPort; // the settings
  String inPortName  = "singleIn";
  String outPortName = "singlOut";
  String outPort; // the settings
  
 // int buttPin = 0, buttGpio = PINig, buttPUD = PI_PUD_KP; 
    
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
  public ThePi.Port portOut;
 //  public int outGpio = PINig, outPin = 0, outPUD = PI_PUD_KP; 
  
//--- state / control  -  exposed as MBean ---------------------------------  
  
  int cycCount; // incremented on every red & green blink cycle (600ms)
  int cycLim = 0; // cycle count limit
  
  boolean yLd; // status of the yellow LED(s)
  boolean rLd; // status of the red LED(s)
  boolean gLd; // status of the green LED(s)
  

  int stepDelay = 10; // delay between operations; default 10ms
  int stepVal  = 10; // step between two PMW or servo values; default 10
  int val    = 100; // PMW or servo current value; default 100
  int valUp = 255; // PMW or servo value upper limit; default 255
  int valLo = 0;  // PMW or servo value lower limit; default 0
  
  boolean runOnTask;
  
//---- standard Bean / argument  implementation   --------------------------
  
  public final void setStepVal(final int stepVal){ 
    this.stepVal = stepVal < 1 ? 1 : (stepVal > 2000 ? 2000 : stepVal);
    if (isTest()) out.println("  // TEST stepVal = " + this.stepVal);
   } // setStepVal(int)
  
  public final void setCycLim(final int cycLim){ 
    this.cycLim = cycLim;
    this.cycCount = 0;
    if (isTest()) out.println("  // TEST cycLim = " + this.cycLim);
  } // setCycLim(int)
  
  public final void setValUp(final int valUp){
    this.valUp = valUp <= 0 ? 0 : valUp >= 2500 ? 2500 : valUp;
    if (isTest()) out.println("  // TEST valUp = " + this.valUp);
  } // setValUp(int)

  public void setValLo(final int valLo){
    this.valLo = valLo <= 0 ? 0 : valLo >= 2500 ? 2500 : valLo;
    if (isTest()) out.println("  // TEST valLo = " + this.valLo);
  } // setValLO(int) 
  
  public final void setStepDelay(final int delay){
    this.stepDelay = delay >= 2 && delay <= 600000 ? delay : 80;
    if (isTest()) out.println("  // TEST stepDelay = " + this.stepDelay);
  } // setStepDelay(int)
  
  public void setVal(final int val){
    this.val = val <= 0 ? 0 : val >= 2500 ? 2500 : val;
    if (isTest()) out.println("  // TEST val = " + this.val);
  } // setVal(val)
    
//----  MBean operations / implementation    -------------------------------

  @Override public final Integer getCycCount(){ return cycCount; }
  @Override public void setCycCount(Integer cycCount){
                           TestOnPi.this.cycCount = cycCount;
  } // setCycCount(Integer)
  @Override public final Integer getCycOvr(){ return getOvrCnt(); }
  @Override public final Integer getCycLim(){ return cycLim; }
  @Override public void setCycLim(final Integer cycLim){
    setCycLim(cycLim.intValue());
  } // setCycLim(Integer)
  
  @Override public Boolean getLEDye(){ return yLd; }
  @Override public Boolean getLEDgn(){ return gLd; }
  @Override public Boolean getLEDrd(){ return rLd; }
  @Override public Integer getPiType(){ return pI.thePi.type(); }
  @Override public final void setStepDelay(final Integer delay){
    setStepDelay(delay.intValue());
  } // setStepDelay(Integer)
  @Override public final Integer getStepDelay(){ return this.stepDelay; }
  @Override public final void setStepVal(final Integer stepVal){ 
   setStepVal(stepVal.intValue());
  } // setStepVal(Integer)
  @Override public final Integer getStepVal(){ return stepVal; }
  @Override public void setVal(final Integer val){
    setVal(val.intValue());
  } // setVal(Integer)
  @Override public Integer getVal(){ return val; }
  @Override public void setValUp(final Integer valUp){
    setValUp(valUp.intValue());
  } // setValUp(Integer)
  @Override public Integer getValUp(){ return valUp; }
  @Override public void setValLo(final Integer valLo){
    setValLo(valLo.intValue());
  } // setValLO(Integer)
  @Override public Integer getValLo(){ return valLo; }
  @Override public void stopTask(){
    if (runOnTask) { runOnTask = false; } else { stop(); }
  } // stopTask()
  
//-------------- non LED input and output ------------------ 
 
/** Set the output for (non LED) actions. <br /> 
*  <br />
*  This method sets the output port either as pin number or as GPIO with a
*  prefix G. A postfix U, D or N will set the pull resistor as up, down or
*  none.<br />
*  Without postfix the pull resistor setting will be kept untouched.
*   
*  @param outPort output pin (12N, e.g.) or gpio (with leading G; G18, e.g.)
*/
  @Override public void setOutPort(String outPort){ 
    outPort = TextHelper.trimUq(outPort, null);
    if (outPort == null) return;
    this.outPort = outPort;
    if (portOut != null) {
      portOut.setPort(outPort);
      pI.setPullR(portOut, PI_PUD_DT); // set the ports PUD setting
    }
  } // setOutPort(String)

/** Set the input for input actions. <br />
 *  <br />
 *  This method sets the input port either as pin number or as GPIO with a
 *  prefix G. A postfix U, D or N will set the pull resistor as up, down or
 *  none. Without postfix the pull resistor setting will be kept untouched. 
 *
 *  @param inPort input pin (7, e.g.) or gpio (with leading G; G4U, e.g.)
 */
  @Override public void setInPort(String inPort){
    inPort = TextHelper.trimUq(inPort, null);
    if (inPort == null) return;
    this.inPort = inPort;
    if (portIn != null) {
      portIn.setPort(inPort);
      pI.setPullR(portIn, PI_PUD_DT); // set the ports PUD setting
    }
  } // setInPort(String)

  @Override public String getOutPort(){
    if (pI == null || portOut == null) return   "unknown"; 
    //     return pI.pinDescr(outPin, outPUD);
    return portOut.toString();
  } // getOutPort()
  @Override public String getInPort(){
    if (pI == null || portIn == null) return   "unknown"; 
    //  return pI.pinDescr(buttPin, buttPUD);
    return portIn.toString();
  } // getInPort() 
  
//-------------- actions -----------------------------------
  
  boolean invInToOut; // inverted input to output on every (delay step)  

/** Helper for starting cyclic task action. <br /> */   
  boolean reportTskStrt(final String taskName) {
   if (!runOnTask || !isRunFlag()) return false;
   if (cycLim <= 0 || cycLim >= 2000111222) {
     out.println("  " + taskName + "  (" + valueLang("endless") 
                                 + " -> JConsole)");
   } else {
     out.println("  " + taskName + "  (" + cycLim + " "
                                 + valueLang("times") + ")");
   }
   cycCount = 0; // allow full cycLim
   return true;
  } // reportTskStrt(String)
  
  void reportWnkPar(){
    out.println("  " + val + " \u00B1" + stepVal + "/"
      + TextHelper.formatDuration(null, stepDelay)
                            + " [" + valLo + ".." + valUp + "]");
  } // reportWnkPar()
  
/** Delay with run on check. <br >
 *  @param millies the number of ms to delay relativ to the last call
 *  @return true when to run on  
 */
  boolean chkDelay(final int millies){
    if (invInToOut)inToInvOut();
    if (runOnTask && isRunFlag()){
      thrDelay(millies);
    } 
    return runOnTask && isRunFlag();
  } // chkDelay(int)
 
/** Blink the LEDs. <br /> */
  @Override public void blink(){
    final int ledOut = rdLEDs | yeLEDs | gnLEDs;
    if (ledOut == 0) {
      out.println("  TestOnPi  blink: " + valueUL("fem0", "no") + " LEDs");
      return;
    }
    runOnTask = true; // start a local task
    invInToOut = portIn.isIO(); // input & eventually output in every step
    //  out.println("  TestOnPi set mode output rd ye gn/up  14 mA");
    pI.logCommand(pI.setPadS(0, 14));
    pI.setAsOutputs(verbose, "red LED", rdLEDs);
    pI.setAsOutputs(verbose, "yel LED", yeLEDs);
    pI.setAsOutputs(verbose, "grn LED", gnLEDs);
    if (reportTskStrt("blink LEDs")) for(;;) { // red yellow green time/state 
      pI.logIfBad(pI.setOutSet(rdLEDs, rLd=HI));// on
      if (!chkDelay(200)) break;               //                  200 ms red
      yLd = !yLd;                             //       toggle
      pI.logIfBad(pI.setOutSet(yeLEDs, yLd)); //       on off
      pI.logIfBad(pI.setOutSet(gnLEDs, gLd=HI)); //            on
      if (!chkDelay(100)) break;                //                 100 ms both
      pI.logIfBad(pI.setOutSet(rdLEDs, rLd=LO));// off
      if (!chkDelay(100)) break;                //                 100ms green
      pI.logIfBad(pI.setOutSet(gnLEDs, gLd=LO)); //           off
      if (!chkDelay(200)) break;                //                 200 ms dark
      if (++cycCount == cycLim) break;
    } // for endless (leave on stop signals)
    out.println("  " + valueLang("releaseLEDs", "release LED outputs") + " "
                             + TextHelper.eightDigitHex(null, ledOut));
    
    pI.gpioActionsByMsk(ledOut, gpio -> pI.stdCmd(PI_CMD_MODES, gpio, PI_INPUT));
  } // blink()

/** Servo wink respectively PWM up/down. <br /> 
 *  <br />
 *  If {@link #portOut} allows no out nothing is done except a message.<br />
 *  The start value {@link #val} determines the mode: <br />
 *  0..255 : pulse width modulation PWM 0%..100% <br />
 *  500..1500..2500: servo positions left..centre..right <br />
 *  else: no action.
 */
  @Override public void wink(){
    if (! portOut.isIO()) {
      out.println("  TestOnPi  wink: " + valueUL("mal0", "no") + " port");
      return;
    }  // no GPIO
    final int outGpio = portOut.gpio;
    final boolean isPWM = val >= 0 && val <= 255;
    if( !isPWM && (val < 500 || val > 2500)) {
       out.println("  TestOnPi  wink: error, val= " + val);
       return;
    } // val error
    if (valLo > val) valLo = val; // [val..valUp]
    if (valUp < val) valUp = val; // [valLo..val]
    boolean up = true;
    if (isPWM) { // PWM
      if (valUp > 255) valUp = 255;
      if (valLo < 0) valLo = 0;
      if (valUp <= valLo) { // [.]
        if (valLo < 128) valUp = valLo + 60;
        if (valUp >= 128) valLo = valUp -60;  
      } // [.]
      up = val < 128;
    } else { //  (PWM else ) servo
      if (valUp > 2500) valUp = 2500;
      if (valLo < 500) valLo = 500;
      if (valUp <= valLo) { // [.]
        if (valLo < 1500) valUp = valLo + 600;
        if (valUp >= 1500) valLo = valUp -600;  
      } // [.]
      up = val < 1500;
    } // servo
    //int dif = valUp - valLo;
    //if (stepVal > dif) stepVal = dif;  // ?? necessary ???
    runOnTask = true; // start a local task
    if (reportTskStrt(isPWM ? "wink PMM" : "wink servo")) {
      reportWnkPar();
      for(;;) {
    
      if (isPWM) {  pI.setPWMcycle(outGpio, val);
      } else pI.setServoPos(outGpio, val);
      if (!chkDelay(stepDelay)) break;  
      if (++cycCount == cycLim) break;
      if (up) { // 
        if (val >= valUp) { 
          up = false; val = valUp - stepVal;
        } else if ((val += stepVal) > valUp) { 
          up = false; val = valUp; 
        } 
      } else { // down
        if (val <= valLo) {
          up = true; 
          val = valLo + stepVal;
        } else if ((val -= stepVal) < valLo) { 
          val = valLo; 
          up = true;
        } 
      } // if (up else) down
    }} // if do   for
    out.println("  " + valueLang("releaseOut", "release output") + " "
                                               + ThePi.gpio2String(outGpio));
    pI.logIfBad(pI.setMode(outGpio, PI_INPUT));
  } // wink()

  @Override public void setOutput(final String out){
    if (pI == null || portOut == null) return; // to early
    int ret = pI.setOutput(portOut.gpio, out);
    if (ret < 0) { // error
      this.val = ret;
    } else { // retrieve val from command parameter
      final CmdState cmdSt = ClientPigpiod.lastCmdState.get();
      final int lastCmd = cmdSt.lastCmd;
      this.val = cmdSt.lastP2;
      if (lastCmd == PiGpioDdefs.PI_CMD_WRITE && this.val == 1) this.val = 255;
    }  // retrieve val from command parameter
  } // setOutput(String) 

// Input and set the output with the inverted result.
  @Override public Boolean inToInvOut(){ 
    if (portIn == null || pI == null) return Boolean.FALSE; // no input
    final int buttGpio = portIn.gpio;
    if (buttGpio >= PINig) return Boolean.FALSE; // no input
    final boolean out = pI.getInp(buttGpio) == 0;
    if (portOut != null && portOut.isIO())
                          pI.setOutput(portOut.gpio, out); // output inverted
    return out ? Boolean.FALSE : Boolean.TRUE; 
  } // inToInvOut()

// just input 
  @Override public Boolean input(){
    if (portIn == null || pI == null) return Boolean.FALSE; // no input
    if (!portIn.isIO()) return Boolean.FALSE; // no input
    return pI.getInp(portIn.gpio) == 1 ? Boolean.TRUE : Boolean.FALSE; 
  } // input()

/** The state of the output. <br />
*  <br />
*  This method returns the last state of the (non LED) output, as
*  set by {@link #setOutput(String) setOutput(value)} or other actions.
*/
  @Override public String getOutput(){ 
    if (val < 0)    return " error";
    if (val ==   0) return "Off Lo";
    if (val == 255) return "On  Hi";
    if (val > 2500 || (val > 255 && val < 500)) return "undef'd";
    StringBuilder dest = new StringBuilder(8);
    dest.append(val > 1000 ? (val < 2000 ? '1' : '2') : '0');
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
   } catch (Exception e) { AppBase.exit(e, INIT_ERROR); }
  } // main(String[])
  
/** GPIO mask for LEDs  (0 = no entry). <br /> */
  int rdLEDs, yeLEDs, gnLEDs; // GPIO masks for red yellow and green LEDs  
                          
/** The application's work. <br />
 *  <br />
 *  When  the startup succeeds this will run an ordered (or default) task
 *  in an endless loop until killed by signal or stopped by MBean/JConsole
 *  command.
 *  @return 0: application ended OK; otherwise error
 */
  @Override public int doIt(){
    out.println(formMessage("startOn") );
    String oName = null;
    try {
      oName = regAsStdMBean(); // registration as MBean 
      out.println("  MBean: " + oName + " (JConsole)"); // success
    } catch (JMException ex) { repExc(out, ex, false); } // report fail
    
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
      out.println(formMessage("connected") + pI + "\n");
    } catch (IOException ex) {
      return errorExit(ERR_PIGPIOD_CON, ex, 
                                          PiUtil.errorText(ERR_PIGPIOD_CON));
    }  // make and connect
    try { // assign GPIOs to LED pins
       rdLEDs = pI.thePi.gpios4pinsChck("red LED", ledRDpins);
       gnLEDs = pI.thePi.gpios4pinsChck("grn LED", ledGNpins);
       yeLEDs = pI.thePi.gpios4pinsChck("yel LED", ledYEpins);

       portOut = pI.thePi.portByGPIO(PINig, outPortName); // make no out
       portOut.setPort(outPort); // set by arg
       pI.setPullR(portOut, PI_PUD_DT); // set the ports PUD setting
       
       portIn = pI.thePi.portByGPIO(PINig, inPortName); // make no out
       portIn.setPort(inPort); // set by arg
       pI.setPullR(portIn, PI_PUD_DT); // set the ports PUD setting
    } catch (IOException  ex) {
      return errorExit(ERR_ASSIGN_PIN, ex, PiUtil.errorText(ERR_ASSIGN_PIN));
    } // assign GPIOs to LED pins
    
    int argsLeft = TextHelper.countParams(args);
    if (argsLeft == 0) {
      if (rdLEDs != 0) {
         args = new String[]{"-blinkLEDs"};
         argsLeft = 1; // default task blink LEDs
      } else if (portOut.isIO()) {
        args = new String[]{"-winkServo"};
        argsLeft = 1; // default task wink servo
      }
    } else if (isTest()) {
      String restArgs = TextHelper.prepParams(args);
      out.println("  // TEST args left: " + restArgs); 
    }
    char nextArgFor = 0; // O,V,U,L,s,v,d: for out, val Up Lo, steps val delay
    int valForArg = 0;
    String forArg = null;
    ovArgs: for (String arg : args) {
      if (!isRunFlag()) break ovArgs;
      arg = TextHelper.trimUq(arg, null);
      if (arg == null || arg.length() == 0) continue ovArgs;
      arg = TextHelper.simpLowerC(arg);
      if (nextArgFor != 0) {
        if (nextArgFor == 'O') { // for output (String)
          setOutput(arg);
          if (isRunFlag()) thrDelay(stepDelay);
        } else { // arg must parse to int
          try {
            valForArg = Integer.parseInt(arg);
          } catch (NumberFormatException ex) {
            errorExit(113, ex, forArg);
          }
          nxArg: switch (nextArgFor) {
           case 'V': setVal(valForArg); break nxArg;
           case 'U': setValUp(valForArg); break nxArg;
           case 'L': setValLo(valForArg); break nxArg;
           case 'v': setStepVal(valForArg); break nxArg;
           case 'd': setStepDelay(valForArg); break nxArg;
           case 's': setCycLim(valForArg); break nxArg;
        }} // switch 
        if (this.val < 0) errorExit(val, forArg); // error
        nextArgFor = 0;
        continue ovArgs;
      } // is a second argument for previous option
      forArg = arg;
      swArgs: switch (arg) {
      case "-blink":
      case "-blinkleds": // -blink[LEDs] 
        blink();
        continue ovArgs;
      case "-winkpwm": // -winkPWM
        valLo=2; val=127; valUp=254;
        wink();
        continue ovArgs;
      case "-winkservo": // -wink[Servo]
        valLo=580; val=1501; valUp=2420;
      case "-wink":
        wink();
        continue ovArgs;
      case "-out":
        nextArgFor = 'O';
        continue ovArgs;
      case "-val":
        nextArgFor = 'V';
        continue ovArgs;
      case "-valUp":
        nextArgFor = 'U';
        continue ovArgs;
      case "-valLo":
        nextArgFor = 'L';
        continue ovArgs;
      case "-stepVal":
        nextArgFor = 'v';
        continue ovArgs;
      case "-steps":
        nextArgFor = 's';
        continue ovArgs;
      case "-stepdelay":
        nextArgFor = 'd';
        continue ovArgs;
      } // switch 
    } // for (over only those parameters left by partial parsing)
    
    // shutdown tasks
    if (pI != null) {
    //  out.println("\n  TestOnPi shutdown " + pI + "    cyc/ovr: "
      //              + getCycCnt() + "/" + getOvrCnt());
      pI.releaseOutputsReport(out);
      try {
         pI.disconnect();
      } catch (IOException e) { } 
        // pigpio.gpioTerminate();
    } else { // pI not null else null
     // out.println("\n  TestOnPi shutdown ");
    } // pI null
    closeLock();
    log.println( threeLineEndMsg());
    return 0; // normal end
  } // doIt()
} // TestOnPi (April 2021)
