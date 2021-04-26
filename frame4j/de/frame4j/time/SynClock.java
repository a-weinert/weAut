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

/** <b>A clock with synchronised updates and readings</b>. <br />
 *  <br />
 *  An object of this class both is and wraps a 
 *  {@link java.time.Clock java.time.Clock} for real time applications. It
 *  provides readings of the time in different forms: as long (ms since 
 *  1.1.1070), {@link java.time.Instant Instant}, 
 *  {@link java.time.ZonedDateTime ZonedDateTime} and else. All readings are
 *  consistent with the last {@link #update()}.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see      de.frame4j.time.TimeHelper
 */
 // so far :  V.177+ (18.08.2016) :  new (replacement for ..util.*Time)
 
@MinDoc(
   copyright = "Copyright 2016  A. Weinert",
   author  = "Albrecht Weinert",
   version = "V.$Revision: 33 $",
   lastModified = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage = "make read update read ....",  
   purpose = "a versatile Clock for real time applications"
) public final class SynClock extends Clock implements Serializable, AClock {

/** A SynClock with the target platform's system clock and local zone. </br>
 *  <br />
 *  This is to provide the common actual time (stamp). It will be initialised 
 *  at application start (respectively the loading time of this class to be
 *  exact).<br />
 *  It can and may be updated any time and by anybody via {@link #update()},
 *  {@link #setActTime()} or {@link #setActTimeMs()}, causing an update to the
 *  ongoing system / platform time. The latter two methods are the only way to
 *  change this common time stamp. <br />
 *  <br />
 *  Except for setting the system time to the past, {@link #getActTime()} and
 *  {@link #millis()} will yield a common time stamp that has a 
 *  guaranteed monotony.<br />
 */   
   static public final SynClock sys = make(null, null);

/** The common starting time. <br />
 *  <br />
 *  This is start time of the framework's first application &mdash; or to
 *  be more precise: it's the loading time of this class. <br />
 *  The same time is also available as {@link ZonedDateTime}
 *  {@link #startZoDtAll} as well as long {@link #startAll}. 
 */
   public static final FixClock startSys = sys.fix();

/** The common starting time. <br />
 *  <br />
 *  This is start time of the framework's first application &mdash; or to
 *  be more precise: it's the loading time of this class. <br />
 *  The same time is also available as {@link ZonedDateTime}
 *  {@link #startZoDtAll} as well as long {@link #startAll}. 
 */
   public static final Instant startInstAll = startSys.actTime;

/** The common starting time. <br />
 *  <br />
 *  This is start time of the framework's first application &mdash; or to
 *  be more precise: it's the loading time of this class. <br />
 *  The same time is also available as 
 *  {@link Instant} {@link #startInstAll}. 
 */
   public static final ZonedDateTime startZoDtAll = startSys.actTimeZd;
                              
/** The common starting time (long). <br />
 *  <br />
 *  This is start time of the framework's first application in milliseconds 
 *  since  1.1.1970 00:00 UTC.<br />
 *  (To be more precise: it's the loading time of this class.)<br />
 */
   public static final long startAll = startSys.actTimeMS;
   
// end static constants and utility objects   
   
/** Version number for serialising.  */
   static final long serialVersionUID = 260153002800101L;
//                                      magic /Id./maMi
   

/** Hidden constructor. */
   private SynClock(Clock clock){ this(clock, clock.getZone()); }
   
/** Hidden constructor. */   
   private SynClock(final Clock clock, final ZoneId zone){ 
      this.clock = clock;
      this.zone = zone;
      this.actTime = this.clock.instant(); // current instant of the clock
      this.actTimeMS = this.actTime.toEpochMilli();
   } // SynClock(Clock, ZoneId)
   
/** Obtain a SynClock. <br />
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
 * */   
   public static SynClock make(final Clock clock, final ZoneId zone){ 
      if (zone == null) {
         if (clock == null) return new SynClock(Clock.systemDefaultZone());
         return new SynClock(clock);                      
      }
      if (clock == null) return new SynClock(Clock.system(zone), zone);
      return new SynClock(clock, zone); 
   } // SynClock(Clock, ZoneId)   

// serialised fields

/** The underlying clock. */     
   public final Clock clock;
/** This Clock's / SynClocks's time-zone. */  
   public final ZoneId zone;
   
   private Instant actTime;
   private long actTimeMS;

// above set by constructor and de-serialisation (below not)   
   private transient ZonedDateTime actTimeZd;
 

/** The clock's time-zone. <br />
 *  <br />
 *  This is the zone used to convert long / Instant to {@link ZonedDateTime}.
 *  It is finally set at construction, never null and defaults to
 *  {@link #clock clock's} zone.
 *  @return this clocks time-zone
 */
   @Override public ZoneId getZone(){ return zone; }

/** Provide another SysClock with a different time-zone. <br />
 *  <br />
 *  @param zone the time zone for the new SysClock, null defaults to the 
 *         {@link #getZone() actual} zone.
 *  @return a copy of this SysClock with optionally another zone   
 */
   @Override public synchronized  SynClock withZone(ZoneId zone){
      if (zone == null) zone = this.zone;
      SynClock ret = new SynClock(this.clock, zone);
      ret.actTime = this.actTime;
      ret.actTimeMS = this.actTimeMS;
      return ret;
   } // withZone(ZoneId)

/** The current instant of this SynClock. <br />
 *  <br />
 *  @return the time as Instant (never null)
 */
   @Override public synchronized Instant instant(){ return this.actTime; }

/** The updated instant of this SynClock. <br />
 *  <br />
 *  @return the updated time as Instant (never null)
 */
  @Override public synchronized Instant setInstant(){
     update(); 
     return this.actTime;
  } // setInstant()

/** The current time of this SynClock. <br />
 *  <br />
 *  @return the ms since 1.1.1970
 */
   @Override public synchronized long millis(){ return this.actTimeMS; }

/** The current zone related time of this SynClock. <br />
 *  <br />
 *  @return the zoned time (never null) related to this SynClock's {@link #zone}
 */
   @Override public synchronized ZonedDateTime getActTime(){ 
      if (actTimeZd == null)
         actTimeZd = ZonedDateTime.ofInstant(actTime, this.zone);
      return this.actTimeZd; 
   } // getActTime()
   
/** Update and get current zone related time of this SynClock. <br />
 *  <br />
 *  The method effectively {@link #update() updates} and returns 
 *  the {@link #getActTime() actual} time.
 *  @return the updated zoned time (never null) related to this 
 *          SynClock's {@link #zone}
 */
   @Override public synchronized ZonedDateTime setActTime(){ 
      this.actTime = this.clock.instant(); // current instant of the clock
      this.actTimeMS = this.actTime.toEpochMilli();
      return this.actTimeZd = ZonedDateTime.ofInstant(actTime, this.zone);
   } // setActTime()

/** The current zone related time of this SynClock. <br />
 *  <br />
 *  @param zone the zone to relate to; null will be this SynClock's {@link #zone}
 *  @return the zoned time (never null)
 */
   @Override public synchronized ZonedDateTime getActTime(ZoneId zone){ 
      if (zone == null || zone == this.zone || zone.equals(this.zone))
         return getActTime();
      // todo consider caching
      return ZonedDateTime.ofInstant(actTime, zone);
   } // getActTime()
   
/** Update this SynClock to its current time. <br /> */
   @Override public synchronized boolean update(){
      long oldMS = this.actTimeMS;
      this.actTime = this.clock.instant(); // current instant of the clock
      this.actTimeZd = null; // clear cache
      this.actTimeMS = this.actTime.toEpochMilli();
      return oldMS != this.actTimeMS;
   } //  update()

/** The state as String. <br />
 *  @return  SynClock[{@link #instant() instant}, {@link #zone zone}]
 */
   @Override public String toString(){
      return "SynClock[" + actTime + ", " + zone + "]";
   } // toString()

   @Override public synchronized FixClock fix(){ 
      return new FixClock(actTime, getActTime()); 
   } // fix()

} // SynClock (17.08.2016)
