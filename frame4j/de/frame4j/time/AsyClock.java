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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.io.Serializable;
import java.time.Clock;

import de.frame4j.util.MinDoc;

/** <b>A simple asynchronous AClock wrapper for a Clock</b>. <br />
 *  <br />
 *  An object of this class both is and wraps a 
 *  {@link java.time.Clock java.time.Clock} for as an AClock object. It is
 *  asynchronous and {@link #update()} does nothing.<br />
 *  <br />
 *  Best use is wrapping a {@link Clock} when an {@link AClock} is required 
 *  and, then, preferably use   {@link Clock} methods only:
 *  {@link #getZone()}, {@link #instant()} and {@link #millis()}. <br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see      de.frame4j.time.TimeHelper
 */
 // so far :  V.182 (18.08.2016) :  new 
@MinDoc(
   copyright = "Copyright 2016  A. Weinert",
   author  = "Albrecht Weinert",
   version = "V.$Revision: 33 $",
   lastModified = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage = "wrap use",  
   purpose = "a Clock ia a AClock disguise"
) public final class AsyClock extends Clock implements Serializable, AClock {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153009100101L;
//                                      magic /Id./maMi

/** Hidden constructor. */
   private AsyClock(final Clock clock){ this(clock, clock.getZone()); }
   
/** Hidden constructor. */   
   private AsyClock(final Clock clock, final ZoneId zone){ 
      this.clock = clock;
      this.zone = zone;
   } // SynClock(Clock, ZoneId)
   
/** Obtain a AsyClock. <br />
 *  <br />
 *  If both parameters are null 
 *  {@link Clock#systemDefaultZone() Clock.systemDefaultZone()} will be used
 *  for clock and zone. <br />
 *  If only zone is null, clock's zone will be used.<br />
 *  If only clock is null, {@link Clock#system(ZoneId) Clock.system(zone)}
 *  will be used.<br />
 *  @param zone  the time zone to use
 *  @param clock the underlying Clock
 *  @return a new SynClock {@link #update() updated} to clock's actual time
 */   
   public static AsyClock make(final Clock clock, final ZoneId zone){ 
      if (zone == null) {
         if (clock == null) return new AsyClock(Clock.systemDefaultZone());
         return new AsyClock(clock);                      
      }
      if (clock == null) return new AsyClock(Clock.system(zone), zone);
      return new AsyClock(clock, zone); 
   } // SynClock(Clock, ZoneId)   

// serialised fields

/** The underlying clock. */     
   public final Clock clock;
   
/** This Clock's / SynClocks's time-zone. */  
   public final ZoneId zone;

/** The clock's time-zone. <br />
 *  <br />
 *  This is the zone used to convert long / Instant to {@link ZonedDateTime}.
 *  It is finally set at construction, never null and defaults to
 *  {@link #clock clock's} zone.
 *  @return this clocks time-zone
 */
   @Override public ZoneId getZone(){ return zone; }

/** Provide another AsyClock with a different time-zone. <br />
 *  <br />
 *  @param zone the time zone for the new AsyClock, null defaults to the 
 *         {@link #getZone() actual} zone.
 *  @return a copy of this SysClock with optionally another zone   
 */
   @Override public AsyClock withZone(ZoneId zone){
      if (zone == null) zone = this.zone;
      AsyClock ret = new AsyClock(this.clock, zone);
      return ret;
   } // withZone(ZoneId)

/** The current instant of this AsyClock. <br />
 *  <br />
 *  @return the time as Instant (never null)
 */
   @Override public Instant instant(){ return this.clock.instant(); }

/** The current time of this AsyClock. <br />
 *  <br />
 *  @return the ms since 1.1.1970
 */
   @Override public long millis(){ return this.clock.millis(); }

/** Update this AsyClock to its current time. <br /> 
 *  <br />
 *  As all readings are asynchronously forwarded to the wrapped 
 *  {@link #clock} this method does the same. I.e., if the wrapped
 *  {@link #clock} is an {@link AClock} the update is just forwarded.<br />
 *  In other cases this method does nothing and returns true, as a 
 *  substantial change by asynchronous reading can never be excluded.
 */
   @Override public boolean update(){
      if (this.clock instanceof AClock) return ((AClock)this.clock).update();
      return true; 
   } // update()

/** The state as String. <br />
 *  @return  AsyClock[{@link #instant() instant}, {@link #zone zone}]
 */
   @Override public String toString(){
      return "AsyClock[" + instant() + ", " + getZone() + "]";
   } // toString()

   @Override public synchronized FixClock fix(){ 
      return new FixClock(null, getActTime()); 
   } // fix()

} // AsyClock (17.08.2016)
