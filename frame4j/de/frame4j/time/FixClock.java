/*  Copyright 2009 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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

/** <b>A clock with fixed times and state</b>. <br />
 *  <br />
 *  An object of this class both is a Clock and an {@link AClock} with fixed
 *  time and zone. It may be used to hold time stamps.<br />
 *  <br />
 *  This class provides equals() and hashCode() based on the absolute time
 *  (stamp) held here, only.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see de.frame4j.time.SynClock
 */
 // so far : V.178  (19.08.2016) :  new 
@MinDoc(
   copyright = "Copyright 2016  A. Weinert",
   author  = "Albrecht Weinert",
   version = "V.$Revision: 33 $",
   lastModified = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage = "make read update read ....",  
   purpose = "a versatile Clock for real time applications"
) public final class FixClock extends Clock implements Serializable, AClock { 
   
/** Version number for serialising.  */
   static final long serialVersionUID = 260153002900101L;
//                                      magic /Id./maMi
   
/** Internal constructor. <br />
 *  <br />
 *  @param actTime    the instant; must be either consistent to actTimeZd 
 *                    or null
 *  @param actTimeZd  the time and the zone; must not be  null
 */   
   protected FixClock(final Instant actTime, final ZonedDateTime actTimeZd){ 
      this.actTimeZd = actTimeZd;
      this.actTime = actTime != null ? actTime: actTimeZd.toInstant();
      this.actTimeMS = this.actTime.toEpochMilli();
      this.zone = actTimeZd.getZone();
   } // FixClock(Instant, ZonedDateTime)

/** Make or get a FixClock. <br />
 *  <br />
 *  @param actTimeZd  the time and the zone; must not be  null
 */   
   public static FixClock of(final ZonedDateTime actTimeZd){ 
      if (actTimeZd == null) return null;
      // todo cache ??
      return new FixClock(null, actTimeZd);
   } // of(ZonedDateTime)

/** Make or get a FixClock. <br />
 *  <br />
 *  @param actTime  the time; must not be  null
 *  @param zone     the zone; must not be  null
 */   
   public static FixClock ofInstant(final Instant actTime, final ZoneId zone){ 
      if (actTime == null || zone == null) return null;
      // todo cache ??
      ZonedDateTime actTimeZd = ZonedDateTime.ofInstant(actTime, zone);
      return new FixClock(actTime, actTimeZd);
   } // ofInstant(Instant, ZoneId)

//-- serialised fields  ----------------------------------------------------

/** This Clock's / FixClocks's time-zone. */  
   public final ZoneId zone;

/** The fixed time. */     
   public Instant actTime;
   
/** The fixed time. <br />
 *  <br > ms since 1.1.1970. */     
   public final long actTimeMS;

/** The fixed time and zone. */      
   public final ZonedDateTime actTimeZd;

   
/** The clock's time-zone. <br />
 *  <br />
 *  This is the zone used to convert long / Instant to {@link ZonedDateTime}.
 *  @return this clocks fixed time-zone
 */
   @Override public final ZoneId getZone(){ return zone; }

/** Provide another FixClock with a different time-zone. <br />
 *  <br />
 *  @param zone the time zone for the new SysClock, null defaults to the 
 *         {@link #getZone() actual} zone.
 *  @return a copy of this SysClock with optionally another zone   
 */
   @Override public FixClock withZone(ZoneId zone){
      if (zone == null || zone.equals(this.zone)) return this;
      return new FixClock(actTime, actTime.atZone(zone));
   } // withZone(ZoneId)

/** The current instant of this FixClock. <br />
 *  <br />
 *  @return the time as Instant (never null)
 */
   @Override public final Instant instant(){ return this.actTime; }

/** The instant of this FixClock. <br />
 *  <br />
 *  @return the fixed time as Instant (no update, as fixed)
 */
  @Override public final Instant setInstant(){ return this.actTime; }

/** The time of this FixClock. <br />
 *  <br />
 *  @return the ms since 1.1.1970
 */
   @Override public final long millis(){ return this.actTimeMS; }

/** The zone related time of this FixClock. <br />
 *  <br />
 *  @return the zoned {@link #actTime time} related 
 *          to this FixClock's {@link #zone}
 */
   @Override public final ZonedDateTime getActTime(){ return this.actTimeZd; }
   
/** Update and get current zone related time of this FixClock. <br />
 *  <br />
 *  As this ACloock is fixed, there's no update. 
 *  @return the zoned {@link #actTime time} related to
 *          this SynClock's {@link #zone}
 */
   @Override public final ZonedDateTime setActTime(){ return this.actTimeZd; }

/** The current zone related time of this FixClock. <br />
 *  <br />
 *  @param zone the zone to relate to;
 *              null will be this SynClock's {@link #zone}
 *  @return the zoned time (never null)
 */
   @Override public final ZonedDateTime getActTime(ZoneId zone){ 
      if (zone == null || zone == this.zone || zone.equals(this.zone))
         return this.actTimeZd;
      // todo consider caching
      return ZonedDateTime.ofInstant(actTime, zone);
   } // getActTime(ZoneId)

/** Update this FixClock (no action). <br /> 
 *  <br />
 *  As this AClock is "fixed", there's no updating.
 *  @return false
 */
   @Override public boolean update(){ return false; }

/** Update this FixClock. <br /> 
 *  <br />
 *  As this AClock is "fixed", there's no updating.
 *  @return #actTimeMS
 */
   @Override public final long setActTimeMs(){ return actTimeMS; }

/** The state as String. <br />
 *  @return  FixClock[{@link #instant() instant}, {@link #zone zone}]
 */
   @Override public String toString(){
      return "FixClock[" + actTime + ", " + zone + "]";
   } // toString()

/** Compare with other Objects's state. <br />
 *  <br />
 *  The other Object is considered equal if it is a FixClock holding the same
 *  absolute point in time (ms, ns). <br />
 *  <br />
 *  Hint: This is, of course compatible with 
 *  {@link FixClock}.{@link #hashCode()}. Objects of {@link FixClock} are
 *  suitable as hash keys, those of other {@link AClock} descendants mostly
 *  aren't.   
 */
   @Override public boolean equals(final Object obj){
      if (! (obj instanceof FixClock)) return false;
      if (obj == this) return true;
      final FixClock other = (FixClock)obj;
      return actTimeMS == other.actTimeMS && 
                                actTime.getNano() == other.actTime.getNano();
   } // equals(Object)
   
   @Override public int hashCode(){
      return 17 * 31  + (int)(actTimeMS ^ (actTimeMS >>> 32));
   } // hashCode()

/** Provide a FixClock with a fixed time according to current state. <br />
 *  <br />
 *  As this is a "fixed" AClock implementation this FixedClock itself
 *  is returned.<br />
 *  @return this
 */
   @Override public final FixClock fix(){ return this; }

} // FixClock (19.08.2016)
