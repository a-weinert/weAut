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
import java.time.Duration;

import de.frame4j.util.MinDoc;

/** <b>A switchable clock with synchronised updates and readings</b>. <br />
 *  <br />
 *  An object of this class both is and wraps a 
 *  {@link java.time.Clock java.time.Clock} for real time applications. It
 *  provides readings of the time in different forms: as long (ms since 
 *  1.1.1070), {@link java.time.Instant Instant}, 
 *  {@link java.time.ZonedDateTime ZonedDateTime} and else. All readings are
 *  consistent with the last {@link #update()}.<br />
 *  <br />
 *  So far, {@link SwiClock} is like {@link SynClock}. With
 *  {@link SynClock} the underlying {@link Clock} (mechanism) and the 
 *  {@link ZoneId zone} are set finally at construction.<br />
 *  In contrast to {@link SynClock}, with {@link SwiClock} both {@link Clock} 
 *  and {@link ZoneId zone} are "switchable". Additionally, {@link SwiClock}
 *  is an {@link SClock observer} and can be switched to the observed time
 *  events as source.<br />
 *  In {@link #isObserver() observer state} observed time setting events can
 *  be determined by {@link #hasObserved()} and used / set by
 *  {@link #updateObserved()} or as usual by just {@link #update()}.<br />
 *  <br />
 *  {@link SwiClock} provides the information on its actual time source and
 *  methods to see the abbreviations / differences to system time.<br />
 *  <br />
 *  Hence, {@link SwiClock} is also a base for providing a simulated Clock for
 *  tests and demos as well as to read the time from a controlled system, 
 *  like, e.g., a Simatic PLC  system, for real time automation 
 *  applications. Those process control applications may (and do) extend
 *  SwiClock for their special time keeping requirements. That's why 
 *  {@link SwiClock} is not final.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see  de.frame4j.time.TimeHelper
 */
 // so far :  V.180  (20.08.2016) :  new 
 
@MinDoc(
   copyright = "Copyright 2016  A. Weinert",
   author  = "Albrecht Weinert",
   version = "V.$Revision: 33 $",
   lastModified = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage = "make read update read .... switch read ..",  
   purpose = "a versatile Clock for real time applications"
) public class SwiClock extends Clock implements Serializable, AClock, SClock {
   
/** Version number for serialising.  */
   static final long serialVersionUID = 260153003700101L;
//                                      magic /Id./maMi
   
/** Constructor for inheritors. <br />
 *  <br />
 *  If clock is null, {@link Clock#system(ZoneId) Clock.system(zone)}
 *  will be used.<br />
 *  @param clock the underlying Clock
 *  @param observer see {@link #ofClock(Clock, boolean)}
 */
   protected SwiClock(final Clock clock, final boolean observer){
      this.clock = clock != null ? clock : SynClock.sys;
      this.zone = this.clock.getZone();
      this.actTime = this.clock.instant();
      this.actTimeMS = this.actTime.toEpochMilli();
      this.observer = observer;
   } // SwiClock(Clock, boolean)
   
/** Obtain a SwiClock. <br />
 *  <br />
 *  If clock is null, {@link Clock#system(ZoneId) Clock.system(zone)}
 *  will be used.<br />
 *  @param clock the underlying Clock
 *  @param observer true: the time source will be the object where this new
 *         SwiClock will (in future) be registered to as 
 *         {@link SClock observer}. Nevertheless, the first setting at 
 *         construction will be to the provided clock. This can, of course, be
 *         inconsistent to the observed subject.
 *  @return a new SynClock {@link #update() updated} to clock's actual time
 */   
   public static SwiClock ofClock(Clock clock, boolean observer){ 
      return new SwiClock(clock, observer); 
   } // ofClock(Clock, boolean)   

//--  serialised fields  ----------------------------------------------

/** The underlying clock. */     
   protected Clock clock;
   
/** This Clock's / SwiClocks's time-zone. */  
   protected ZoneId zone;
   
   protected Instant actTime;
   protected long actTimeMS;

// above set by constructor and de-serialisation (below not)   
   protected transient ZonedDateTime actTimeZd;
 

/** The SwiClock's actually used time-zone. <br />
 *  <br />
 *  This is the zone used to convert long / Instant to {@link ZonedDateTime}.
 */
   @Override public final synchronized ZoneId getZone(){ return zone; }

/** The SwiClock's actually used Clock. <br />
 *  <br />
 *  This is the actual clock used as time source. 
 */
   public final synchronized Clock getClock(){ return this.clock; }

/** Set the SwiClock's actually used Clock. <br />
 *  <br />
 *  This method may change is the actual clock used as time source by a non
 *  null (and not self) parameter. If the the time source was effectively
 *  changed and if this SwiClock is not an {@link #isObserver() observer} this
 *  SwiClock will be {@link #update() updated}. 
 *   
 *  @param clock the new time source; null: no action
 */
   public synchronized final void setClock(final Clock clock){
      if (clock == null || clock.equals(this.clock) 
                                               || clock.equals(this)) return;
      this.clock = clock;
      if (this.observer) return;
      update();
  } // setClock(Clock)

/** Switch this SwiClocks time-zone. <br />
 *  <br />
 *  This method does never return a newly created SwiClocks object (it always
 *  returns this). Instead, it potentially created a new
 *  {@link #getClock() internal Clock} object.<br />
 *  This method neither changes the {@link #setObserver(boolean) observer}
 *  status nor does it {@link #update() update}.
 *  @param zone the time zone for the new SysClock, null defaults to the 
 *         {@link #getZone() actual} zone.
 *  @return always this
 */
   @Override public synchronized SwiClock withZone(final ZoneId zone){
      if (zone == null) return this;
      if (zone.equals(this.zone)) return this;
      Clock newClock = clock.withZone(zone);
      this.clock = newClock;
      this.zone = zone;
      this.actTimeZd = null; // clear cache
      return this;
   } // withZone(ZoneId)

/** Based on the underlying system's clock. <br /> 
 *  <br />
 *  This method return true, if this SwiClock wraps (as
 *  {@link #getClock() clock}) the {@link SynClock#sys system clock} and is
 *  not an {@link #isObserver() observer}. 
 */
  public final synchronized boolean isSystemClockBased(){ 
     return !observer && SynClock.sys.equals(this.clock); 
  } // isSystemClockBased()
  
/** Difference to the underlying system's clock. <br /> 
 *  <br />
 *  Hint: Even if this SwiClock is based 
 *  {@link #isSystemClockBased() on the system} clock this method will seldom
 *  return &gt;= 0L, as it will mostly be behind between
 *  {@link #update() updates}.
 *  @return 0L: perfect sync; &lt; 0: slow; &gt; 0: fast
 *  not an {@link #setObserver(boolean) observer}.
 */
    public final synchronized long difToSystemClock(){ 
       return actTimeMS - SynClock.sys.millis(); 
    } // difToSystemClock()
    
/** Difference to a time in ms. <br /> 
 *  <br />
 *  @return 0L: This SwiClock's difference to a time in ms since 1.1.1970
 */
    public final synchronized long difToMs(final long ms){
       return actTimeMS - ms; 
    } // difToMs(long)

/** Difference to the underlying system's clock as Duration. <br /> 
 *  <br />
 *  @see #difToSystemClock()
 */
    public final synchronized Duration between(){
       return Duration.ofMillis(actTimeMS - SynClock.sys.millis()); 
    } // isSystemClockBased()  

/** Set based on the underlying system's clock. <br />
 *  <br />
 *  This method neither changes the {@link #setObserver(boolean) observer}
 *  status nor does it {@link #update() update}. <br />
 *  If this changes the clock source (returns false) an {@link #update()} is
 *  recommended.<br />
 *  @return true: the {@link #getClock() clock} was already the system clock;
 *             no action
 */
  public final synchronized boolean setSystemClockBased(){ 
    if (SynClock.sys.equals(this.clock)) return true; 
    this.clock = SynClock.sys;
    this.zone = SynClock.sys.zone;
    this.actTimeZd = null; // clear cache
    return false;
  } // setSystemClockBased()

/** The current instant of this SynClock. <br />
 *  <br />
 *  @return the time as Instant (never null)
 */
   @Override public final synchronized Instant instant(){ 
      if (this.actTime == null) this.actTime = Instant.ofEpochMilli(actTimeMS);
      return this.actTime; 
   } // instant()


/** The current time of this SynClock. <br />
 *  <br />
 *  @return the ms since 1.1.1970
 */
   @Override public final synchronized long millis(){ 
      return this.actTimeMS;
   } // millis()

/** The current zone related time of this SynClock. <br />
 *  <br />
 *  @return the zoned time (never null) related to this SynClock's {@link #zone}
 */
   @Override public final synchronized ZonedDateTime getActTime(){ 
      if (actTimeZd == null)
         actTimeZd = ZonedDateTime.ofInstant(instant(), this.zone);
      return this.actTimeZd; 
   } // getActTime()
   

/** The current zone related time of this SynClock. <br />
 *  <br />
 *  @param zone the zone to relate to; null will be this SynClock's {@link #zone}
 *  @return the zoned time (never null)
 */
   @Override public final synchronized ZonedDateTime getActTime(ZoneId zone){ 
      if (zone == null || zone == this.zone || zone.equals(this.zone))
         return getActTime();
      // todo consider caching
      return ZonedDateTime.ofInstant(instant(), zone);
   } // getActTime()

   
/** Update this SynClock to its current time. <br /> */
   @Override public synchronized boolean update(){
      final long oldMS = this.actTimeMS;
      if (observer){ // observer 
         if (gotMs) {
            this.actTimeMS = observedMs;
            gotMs = false;
            this.actTime = null; 
            this.actTimeZd = null; // clear cache
         } else if (observedInstant != null) {
            this.actTime = observedInstant;
            observedInstant = null;
            this.actTimeMS = this.actTime.toEpochMilli();
            this.actTimeZd = null; // clear cache
         } else if (observedDateTime != null) {
            this.actTimeZd = observedDateTime;
            observedDateTime = null;
            this.actTime = Instant.from(this.actTimeZd);
            this.actTimeMS = this.actTime.toEpochMilli();
         } else return false;
      } else {  // observer else clock source
         if (this.clock instanceof AClock) ((AClock)this.clock).update();
         this.actTime = this.clock.instant(); 
         this.actTimeMS = this.actTime.toEpochMilli();
         this.actTimeZd = null; // clear cache
      } // else clock source
      return oldMS != this.actTimeMS;
   } //  update()
   
/** Update this SynClock to its current time. <br />
 *  <br />
 *  This method returns
 *  <code>{@link #hasObserved()} &amp;&amp; {@link #update()};</code> 
 *  efficiently and in one synchronisation step.
 */
   public synchronized boolean updateObserved(){
      if (!observer) return false;
      final long oldMS = this.actTimeMS;
      if (gotMs) {
          this.actTimeMS = observedMs;
          gotMs = false;
          this.actTime = null; 
          this.actTimeZd = null; // clear cache
      } else if (observedInstant != null) {
          this.actTime = observedInstant;
          observedInstant = null;
          this.actTimeMS = this.actTime.toEpochMilli();
          this.actTimeZd = null; // clear cache
      } else if (observedDateTime != null) {
          this.actTimeZd = observedDateTime;
          observedDateTime = null;
          this.actTime = Instant.from(this.actTimeZd);
          this.actTimeMS = this.actTime.toEpochMilli();
      } else return false;
      return oldMS != this.actTimeMS;
   } //  updateObseved()
   

/** The state as String. <br />
 *  @return  SwiClock[{@link #instant() instant}, {@link #zone zone}]
 */
   @Override public String toString(){
      return "SwiClock[" + instant() + ", " + zone + "]";
   } //  toString()

   @Override public final synchronized FixClock fix(){ 
      return new FixClock(instant(), getActTime()); 
   } // toString()
   
   
//---------------  observer implementation ----------------------------------
   
   protected boolean observer; // true if switched to observer
   
 //   protected boolean autoupdate; // true if switched to observer
//    protected transient boolean wasAutoUpdated; // last automatic updates returned true

   protected volatile transient Instant observedInstant;
   protected volatile transient ZonedDateTime observedDateTime;
   protected volatile transient long    observedMs;
   protected volatile transient boolean gotMs;
   
/** The observed time source set new values. <br />
 *  <br />
 *  This method returns true in {@link #isObserver() observer} state when
 *  {@link #instantChange(Instant) instantChange()} or {@link #msChange(long)}
 *  have occurred since last {@link #update()}. <br />
 *  <br />
 *  Instead of <code>if({@link #hasObserved()}) {@link #update()};</code>
 *  respectively
 *  <code>{@link #hasObserved()} &amp;&amp; {@link #update()}</code> use
 *  <code>{@link #updateObserved()};</code>.
 */
   public synchronized boolean hasObserved(){
      return observer 
            && (gotMs || observedInstant != null || observedDateTime != null);
   } // hasObserved()

/** Use as observer. <br />
 *  <br /> 
 *  This method sets the {@link #isObserver() observer} state.
 */
   public synchronized void setObserver(final boolean observer){
      if (observer) {
         if (this.observer) return;
         this.observedInstant = null;
         this.observedDateTime = null;
         gotMs = false;
         this.observer = true;
         return;
      }
      this.observer = false;
   } // setObserver(boolean) 


/** Use as observer. <br /> */
   public final synchronized boolean isObserver(){ return observer; }

/* * Is auto updating. <br />
 *  <br /> 
 *  If set every method to get time information held in this SwiClock also 
 *  causes an {@link #update() update}. 
 *  Hint: Except when being actually an {@link #isObserver() observer} this
 *  would violate {@link AClock}'s time stamp semantics. With objects
 *  of this {@link SwiClock class} both flags are held equal.
 * /
   public final synchronized boolean isAutoUpdate(){ return autoupdate; } */
   

   @Override final public void instantChange(final Instant instant){
      if (! observer) return;
      observedInstant = instant; // observedInstant is volatile
      gotMs = false;            // gotMs is volatile
   } // instantChange(Instant)
   
   @Override final public void instantChange(final ZonedDateTime actTimeZD){
      if (! observer) return;
      observedDateTime = actTimeZD; // observedDateTime is volatile
      gotMs = false;
   } // instantChange(Instant)

   @Override final public void msChange(final long ms){
      if (! observer) return;
      observedMs = ms;  // observedMs is volatile
      this.observedInstant = null;
      gotMs = true;
   } // msChange(long)

} // SwiClock (28.08.2016, 11.09.2016)
