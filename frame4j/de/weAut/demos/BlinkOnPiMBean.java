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

import de.frame4j.util.AppMBean;

/** <b>Test and demo program BlinkOnPi as MBean</b>. <br />
 *  <br />
 *  Contrary to {@link de.weAut.demos.RdGnPiGpioDBlink} the test and demo
 *  program {@link  de.weAut.demos.BlinkOnPi} makes full use of Frame4J
 *  by inheriting {@link de.frame4j.util.App}. <br />
 *  Hence, additionally to own operations and properties here we can have also
 *  {@link de.frame4j.util.AppMBean}.<br />
 *  Optionally, also {@link de.frame4j.util.UIInfo} (implemented by 
 *  {@link de.frame4j.util.App} could be extended to make those also 
 *  available as MBen operations (not recommended).<br />
 *  <br />
 *  Copyright  &copy;  2021  Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 47 $ ($Date: 2021-05-13 19:06:22 +0200 (Do, 13 Mai 2021) $)
 *  @see BlinkOnPi
 *  @see de.weAut.TestOnPi
 *  @see <a href="./doc-files/BlinkOnPi.properties">BlinkOnPi.properties</a>
 */
// so far:   V.  33  (27.03.2021) : new, minimal functionality
//           V.  34  (04.04.2021) : getPiType added 
//           V.  43  (08.05.2021) : leBut

public interface BlinkOnPiMBean extends AppMBean {
  
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
 

/** The state of the button. <br />
 *  <br />
 *  The button is low active. Hence, false means pressed.
 *  @return true when open, released
 */
 public Boolean getLeBut();
 
/** The state of the buzzer. <br />
 *  <br />
 *  The buzzer is an extra output. Default is pin 12. On the trafficPi
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

     
/** See the number of cycles. <br />
 *  <br />
 *  As the duration of one cycle is 600ms it would take more than 40 years to
 *  overflow to negative.<br />
 *  Note: Java has no unsigned number type. int is int32_t there is 
 *  no uint32_t. 
 *  @return the loop count
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
 */
  public void resetCycCount();
      
/** Get the Pi type. <br /> */
  public Integer getPiType();

} // BlinkOnPiMBean (29.04.2021)
