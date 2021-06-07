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
import de.frame4j.util.AppMBean;

/** <b>IO/device on Pi test and demo program TestOnPi as MBean</b>. <br />
 *  <br />
 *  Copyright  &copy;  2021  Albrecht Weinert 
 *
 *  @version  $Revision: 51 $ ($Date: 2021-06-07 16:31:39 +0200 (Mo, 07 Jun 2021) $)
 *  @see TestOnPi
 *  @see PiUtil
 *  @see de.weAut.demos.BlinkOnPi
 *  @see <a href="./doc-files/TestOnPi.properties">TestOnPi.properties</a>
 *  @see <a href="../doc-files/Raspi4testPins.png"
 *    title="GPIOs and pins">Raspi4testPins</a>
 */
// so far:   V.  42  (29.04.2021) : new, minimal functionality

public interface TestOnPiMBean extends AppMBean { 
  
/** The state of the yellow LED(s). <br />
 *  <br />
 *  The yellow LED(s) are inverted only once in a
 *  {@link #getCycCount() cycle}.  
 *  @return true when ON
 */
 public Boolean getLEDye();

/** The state of the red LED(s). <br />
 *  <br />
 *  The red LED(s) are inverted twice in a {@link #getCycCount() cycle}.  
 *  @return true when ON
 */
 public Boolean getLEDrd();

/** The state of the green LED(s). <br />
 *  <br />
 *  The yellow LED(s) are inverted twice in a {@link #getCycCount() cycle}.  
 *  @return true when ON
 */
 public Boolean getLEDgn();
     
/** The number of cycles. <br />
 *  <br />
 *  As the duration of one cycle is typically 600ms it would take more
 *  than 40 years to overflow to negative.<br />
 *  Note: Java has no unsigned number type. int is int32_t there is 
 *  no uint32_t.
 *  Note 2: Here for greater values, the required (ill) return type Integer
 *  would abandon the cache guarantee for small values. Requesting this value
 *  regularly would lead to heavy object creation and garbage collection
 *  detrimental to real time applications. Hence, this method might be
 *  deprecated or removed in future.
 *  @return the loop / cycle or delay count
 *  @see #setCycCount(Integer)
 *  @see #getCycOvr()
 */
  public Integer getCycCount();
      
/** Set the number of cycles. <br /> */   
  public void setCycCount(Integer cycCount);
  
/** The limit for the number of cycles. <br />
 *  <br />
 *  When this limit is hit when incrementing the 
 *  {@linkplain #getCycCount() number of cycles} the current task will be
 *  stopped. The next task will be started 
 *  (with {@link #setCycCount(Integer) setCycCount(0)}, if any, or the
 *  application will stop.
 *
 *  @return the limit for the loop or cyclecount
 *  @see #setCycLim(Integer)
 *  @see #getCycCount()
 */
  public Integer getCycLim();
      
/** Set the limit for number of cycles. <br />
 *  <br />
 *  This method sets the {@linkplain #getCycLim() limit} for number of cycles
 *  by the parameter {@code cycLim} and, also,
 *  resets the {@linkplain #getCycCount() number of cycles} to 0.
 *  
 *  @see#getCycLim()
 *  @param cycLim the limit; 0 effectively means endless
 */   
  public void setCycLim(Integer cycLim);
  
/** Get the number of spoiled delays. <br />
 *  <br />   
 *  @return the number of spoiled thread delays
 *  @see PiUtil#thrDelay(int) 
 *  @see #getCycCount()
 */
  public Integer getCycOvr();
   
/** Get the Pi type. <br /> */
  public Integer getPiType();
  
/** Set the output for (non LED) actions. <br /> 
 *  <br />
 *  This method sets the output port either as pin number or as GPIO with a
 *  prefix G.<br />
 *  An optional postfix U, D or N will set the pull resistor as up, down
 *  or none. Without postfix the pull resistor setting will be kept untouched. 
 *  @param outPort output pin (12N, e.g.) or gpio (with leading G; G18, e.g.)
 *  @see #getOutPort()
 *  @see #getInPort()
 *  @see #setInPort(String)
 */
  public void setOutPort(String outPort);
  
/** Get the output for (non LED) actions. <br /> 
 *  <br />
 *  If all informations are available the text will show pin number, pull
 *  resistor setting and GPIO; example: {@code "07UG04"}. <br />
 *  The numbers will always be given as two digit decimal. The format will be
 *  understood (parsed) by {@link #setOutPort(String)}; for the example
 *  {@code setOutPort("7U")} would be sufficient.
 *  @see #setOutPort(String)
 *  @see #getInPort()
 *  @see #setInPort(String)
 *  */
  public String getOutPort();

/** Set the port for input actions. <br />
 *  <br />
 *  This method sets the input port either as pin number or as GPIO with a
 *  prefix G. A postfix U, D or N will set the pull resistor as up, down or
 *  none. Without postfix the pull resistor setting will be kept untouched. 
 *
 *  @param inPort input pin (7, e.g.) or gpio (with leading G; G4U, e.g.)
 *  @see #getInPort()
 *  @see #setOutPort(String)
 *  @see #getOutPort()
 */
  public void setInPort(String inPort);
  
/** Get the port for input actions. <br /> 
 *  <br />
 *  @see #setInPort(String)
 *  @see #setOutPort(String)
 *  @see #getOutPort()
 */
  public String getInPort();

/** Set delay / ms between two operations. <br />
 *  @param delay 2 .. 29999 ms; default 10 ms
 */
  public void setStepDelay(Integer delay);

/** Get delay / ms between two operations. <br /> 
 *  @return the actual value
 */
  public Integer getStepDelay();

/** Set step between two PMW or servo values. <br />
 *  @param step the value will be adjusted at use if not fitting
 */
  public void setStepVal(Integer step);

/** Get step between two PMW or servo values. <br />
 *  @return the actual value; default 10
 */
  public Integer getStepVal();

/** Set PMW or servo current value. <br />
 *  @param val the value will be adjusted at use if not fitting
 */
  public void setVal(Integer val);

/** Get PMW or servo current value. <br />
 *  @return the actual value; default 100
 */
  public Integer getVal();

/** Set PMW or servo value upper limit. <br />
 *  @param valUp the limit will be adjusted at use if not fitting
 */
  public void setValUp(Integer valUp);

/** Get PMW or servo value upper limit. <br />
 *  @return the actual value; default 255
 */
  public Integer getValUp();

/** Set PMW or servo value lower limit. <br />
 *  @param valLo the limit will be adjusted at use if not fitting
 */
  public void setValLo(Integer valLo);

/** Get PMW or servo value lower limit. <br />
 *  @return the actual value; default 0
 */
  public Integer getValLo();

//-------------- actions -----------------------------------
  
/** Blink the LEDs. <br /> */
  public void blink();
  
/** Servo wink respectively PWM up/down. <br /> */
  public void wink(); 
  
/** Set the output. <br /> 
 *  <br />
 *  This method will interpret the {@code String out} as does
{@link ClientPigpiod#setOutput(int, String) ClientPigpiod.setOutput(gpio, out)}
 *  usually by implementing it so.
 *  @see ClientPigpiod#setOutput(int, String)
 *  @param out the output value
 */
  public void setOutput(String out);  
  
/** Input and set the output with the inverted result. <br />
 *  <br />
 *  If no input port is defined this method does nothing and returns FALSE.
 *  If an operable output port is defined (also) the inverted input value 
 *  will be output to it.  
 *  @return the state of the input port
 *  @see #input
 *  @see #setOutput(String)
 */
  public Boolean inToInvOut();

/** Just input. <br />
 *  <br />
 *  If no input port is defined this method does nothing and returns FALSE.
 *  Otherwise the input is read and if that returns {@link PiVals#H1 1/Hi}
 *  TRUE will be returned.<br />
 *  Note, that a returned false is a bit ambiguous. Besides signal Lo it
 *  might mean no input port defined or error while reading.
 *  @return the state of the input port
 *  @see #inToInvOut()
 */
  public Boolean input();
  
 
/** The state of the output. <br />
 *  <br />
 *  This method returns the last state, i.e. setting by
 *  {@link #setOutput(String) setOutput(out)},
 *  of the output. This method returns the binary on/off state as well as the
 *  numerical PMW or servo state as text.
 */
  public String getOutput();
  
/** Stop the task. <br />
 *  <br />
 *  This method will stop a periodic task (like {@link #blink()} or 
 *  {@link #wink()} etc.) if any is running. This will start the next task
 *  pending. Otherwise it will act like {@link de.frame4j.util.App#stop()}.
 */   
  public void stopTask();
} // TestOnPiMBean (29.04.2021, 13.05.2021)

