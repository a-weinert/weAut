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

/** <b>Program RdGnPiGpioDBlink's MBeans</b>.<br />
 *  <br />
 *  The test and demo program {@link  de.weAut.demos.RdGnPiGpioDBlink} makes
 *  use of a few Frame4J utilities and forsakes the benedictions gained by
 *  inheriting {@link de.frame4j.util.App}. <br />
 *  As the (little) usage of Frame4J is redirected over 
 *  {@link de.weAut.PiUtil} it is relatively simple to take the sources and
 *  isolate {@link  de.weAut.demos.RdGnPiGpioDBlink} completely from Frame4J
 *  if one wants a stand alone pigpioD with Java demo.<br />
 *  <br />
 *  Nevertheless a little observation and control as MBean, e.g. via the
 *  utility JConsole/JMX is implemented.<br />
 *  <br />
 *  Copyright  &copy;  2021  Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 41 $ ($Date: 2021-04-23 20:44:27 +0200 (Fr, 23 Apr 2021) $)
 */
// so far:   V.  33  (27.03.2021) : new, minimal functionality
//           V.  34  (04.04.2021) : getPiType added 

public interface RdGnPiGpioDBlinkMBean {
  
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
   
/** Stop the cycle loop. <br />
 *  <br />
 *  Invoked by JConsole e.g. this ends the otherwise endless loop and 
 *  the program in a civilised manner. The forcible alternative of stopping
 *  the application by cntl-C would be, by the way, civilised by a shut
 *  down hook.
 */
   public void stop();

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
   
} // RdGnPiGpioDBlinkMbean (09.2020)
