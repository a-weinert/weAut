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
 *  <br />
 *  Copyright  &copy;  2021  Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  43 (4.05.2021)
 *  @see TestOnPi
 *  @see PiUtil
 *  @see de.weAut.demos.BlinkOnPi
 *  @see <a href="./doc-files/TestOnPi.properties">TestOnPi.properties</a>
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
     
/** See the number of cycles. <br />
 *  <br />
 *  As the duration of one cycle is 600ms it would take more than 40 years to
 *  overflow to negative.<br />
 *  Note: Java has no unsigned number type. int is int32_t there is 
 *  no uint32_t. 
 *  @return the loop / cycle or delay count
 *  @see #resetCycCount()
 */
  public Integer getCycCount();
      
/** Set the number of cycles. <br /> */   
  public void setCycCount(Integer cycCount);
  
/** Reset the number of cycles counter. <br />
 *  <br />
 *  If you don't want to wait almost 82 years to see low positive numbers 
 *  again, you may reset the counter (via JConsole e.g.).
 *  @see #getCycCount()
 *  @return the last value before this reset
 */
  public Integer resetCycCount();
      
/** Get the Pi type. <br /> */
  public Integer getPiType();
  
/** Get GPIO for (single / non LED) operations. <br />  */
  public Integer getGpio();
  
/** Get pin for (single / non LED) operations. <br />  
 *  <br />
 *  This is {@link #getGpio()} as pin number (0 means ignore / invalid). 
 */
  public Integer getPin();

/** Set GPIO for (single / non LED) operations. <br /> */
  public void setGpio(Integer gpio);

/** Set pin for (single / non LED) operations. <br /> */
  public void setPin(Integer pin);

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


/** The state of the button. <br />
 *  <br />
 *  The button is low active. Hence, false means pressed.
 *  @return true when open, released
 */
 public Boolean getLeBut();
 
/** The state of the buzzer. <br />
 *  <br />
 *  The buzzer is an extra output. Default is no pin. On the trafficPi
 *  shield pin 12 is a buzzer switched on by Hi (via a npn transistor). <br />
 *  This method returns the binary on/off state of the pin in question.
 *  When actuated via {@linkplain #setLeBuzPWM(Integer) PWM}  0..21 is 
 *  considered as OFF (low, false) and 22..255 as ON (hi, true).
 *  @return true when active (Hi)
 *  @see #setLeBuz(Boolean)
 *  @see #setLeBuzPWM(Integer)
 *  @see #getLeBuzPWM()
 */
  public Boolean getLeBuz();

/** The PWM state of the buzzer. <br />
 *  <br />
 *  The buzzer is an extra output. Default is pin 12.
 *  @return 0..255 which is 0..100% PWM
 *  @see #getLeBuz()
 */
  public Integer getLeBuzPWM();
  
/** The state of the buzzer. <br /> */   
  public void setLeBuz(Boolean on);

/** Set buzzer output by PMM. <br /> 
 * 
 *  @param pwm  0..255 is 0..100% PWM
 *  @see #getLeBuzPWM()
 *  @see #getLeBuz()
 */   
  public void setLeBuzPWM(Integer pwm);


} // TestOnPiMBean (29.04.2021, 13.05.2021)

