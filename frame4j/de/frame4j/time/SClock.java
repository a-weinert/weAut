/*  Copyright 2016 Albrecht Weinert, Bochum, Germany (a-weinert.de)
 *  All rights reserved.
 *  
 *  This file is part of Frame4J 
 *  ( frame4j.de  https://weinert-automation.de/software/frame4j/ )
 * 
 *  Frame4J is made available under the terms of the 
 *  Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/  or as text in
 https://weinert-automation.de/java/docs/frame4j/de/frame4j/doc-files/epl.txt
 *  within the source distribution 
 */
package de.frame4j.time;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.EventListener;

import de.frame4j.util.MinDoc;

/** <b>A clock settable by ms, Instant or ZonedDateTime</b>. <br />
 *  <br />
 *  An object of an implementing class is able to receive change events from
 *  a clock source in the widest sense. Usually it will be an inheritor of 
 *  {@link AClock} or {@link Clock}, using the event to update its own 
 *  time.<br />
 *  <br />
 *  Usually this SClock object will have to be registered with a suitable
 *  time source. It is recommended that such source will consistently use just
 *  one of the three setting methods provided here.<br /> 
 *  <br />
 *  Warning / hint: {@link AClock}'s promises consistent readings and 
 *  {@link AClock#update() updates}. This most often implies synchronised 
 *  implementations of all {@link AClock} methods. Notwithstanding this, it is 
 *  essential for most clock sources used that no event listener method
 *  &mdash; neither {@link #instantChange(Instant) instantChange()} nor
 *  {@link #msChange(long) msChange()} &mdash; must ever block the clock 
 *  sources thread.<br /> 
 *  A recommend implementation is 
 *  {@link #instantChange(Instant) instantChange()} and
 *  {@link #msChange(long) msChange()} setting a (volatile) flag reseted by
 *  {@link AClock#update() update()} plus implementing a conditional update,
 *  either explicitly or automatically on reads.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see      de.frame4j.util.ComVar
 */
 // so far : V.181  (22.08.2016) :  new
 //          V.191  (05.09.2016) :  + instantChange(ZonedDateTime actTime)
 
@MinDoc(
   copyright = "Copyright 2016  A. Weinert",
   author  = "Albrecht Weinert",
   version = "V.$Revision: 33 $",
   lastModified = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage = "implement",  
   purpose = "a common type for settable Clocks"
) public interface SClock extends EventListener { 

/** Set the time by an Instant. <br />
 *  <br />
 *  This method is to be called by the time source (clock in the widest sense)
 *  used after a significant change of the time provided. <br />
 *  <br />
 *  @param instant the changed time provided 
 */
  public void instantChange(Instant instant);

/** Set the time by a ZonedDateTime. <br />
 *  <br />
 *  This method is to be called by the time source (clock in the widest sense)
 *  used after a significant change of the time provided. <br />
 *  <br />
 *  @param actTimeZd the changed time provided 
 */
  public void instantChange(ZonedDateTime actTimeZd);  

/** Set the time by ms since 1.1.1970. <br />
 *  <br />
 *  This method is to be called by the time source (clock in the widest sense)
 *  used after a significant change of the time provided. <br />
 *  <br />
 *  @param ms the changed time provided by the source
 */
  public void  msChange(long ms);
 
} // SClock (17.08.2016, 05.09.2016)
