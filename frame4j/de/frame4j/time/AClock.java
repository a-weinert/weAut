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
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRules;

import de.frame4j.util.MinDoc;
import de.frame4j.time.TimeHelper;

/** <b>A clock with update and various readings</b>. <br />
 *  <br />
 *  An object of an implementing class provides the functions of a
 *  {@link java.time.Clock java.time.Clock} for real time applications &mdash;
 *  usually by extending and/or wrapping it. An AClock provides readings of
 *  the time in different forms, as long, {@link java.time.Instant Instant}, 
 *  {@link java.time.ZonedDateTime ZonedDateTime} and else. All readings are
 *  consistent with the last explicit {@link #update()} or setting,
 *  should {@link SClock} be implemented,too.<br />
 *  <br />
 *  A "fixed" implementation must implement/leave {@link #update()} as doing 
 *  nothing and may omit the wrapped  {@link java.time.Clock java.time.Clock}.
 *  <br />
 *  Hint: {@link #getZone()}, {@link #instant()} and {@link #millis()} are 
 *  implemented in the class {@link java.time.Clock java.time.Clock}, which
 *  the package {@link java.time java.time} provides no suitable interface
 *  for. <br />
 *  {@link #update()}, {@link #getActTime()} and {@link #getActTime(ZoneId)}
 *  provide extra readings and a synchronised consistent update of all 
 *  readings.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2016 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see      de.frame4j.util.ComVar
 */
 // so far : V.178  (19.08.2016) :  new, preliminary
 //          V.181  (22.08.2016) :  update signature changed (bean enable)
 //          V.188  (29.08.2016) :  full set of getters
 //          V.191  (05.09.2016) :  clarifications in javaDoc
 
@MinDoc(
   copyright = "Copyright 2016  A. Weinert",
   author  = "Albrecht Weinert",
   version = "V.$Revision: 33 $",
   lastModified = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
//   usage = "implement",  
   purpose = "a common type for Clocks"
) public interface AClock { 

/** The JVM's system time zone.  <br /> 
 *  <br >
 *  This is the time zone of the underlying system this JVM is running.
 *  Its {@link ZoneRules rules} are available in {@link #sysZoneRules}.<br />
 *  This is also available as {@link #DEF_ZONE}.<br />
 */ 
   public static final ZoneId sysZone = ZoneId.systemDefault();

/** The JVM's system time zone rules.  <br /> */ 
   public static final ZoneRules sysZoneRules = sysZone.getRules();

/** Time zone for common use. <br /> */   
   static public final ZoneId GMT_ZONE = ZoneId.of("GMT");
/** Time zone for common use. <br /> */   
   static public final ZoneId WET_ZONE = ZoneId.of("WET");
/** Time zone for common use. <br /> */   
   static public  final ZoneId CET_ZONE = ZoneId.of("CET");
/** Time zone for common use. <br /> */   
   static public  final ZoneId EET_ZONE = ZoneId.of("EET");
/** Time zone for common use. <br /> */   
   static public  final ZoneId UTC_ZONE = ZoneId.of("UTC");

/** Time zone for common use. <br /> */   
   static public  final ZoneId DEF_ZONE = sysZone;  

//  end static constants  ---------------------------------------------------

/** The clock's time-zone. <br />
 *  <br />
 *  This is the zone used to convert long / Instant to {@link ZonedDateTime}.
 *  It must never be null.
 *  @return this clocks time-zone
 */
   public ZoneId getZone();

/** Provide another AClock with a different time-zone. <br />
 *  <br />
 *  A not "fixed" AClock implementation shall provide a new object; in case 
 *  of null this would be a clone.<br />
 *  A "fixed" implementation should return this, if the parameter is null
 *  or equals the own zone.<br />
 *  @param zone the time zone for the new SysClock, null defaults to the 
 *         {@link #getZone() actual} zone.
 *  @return a copy of this AClock with optionally another zone   
 */
   public AClock withZone(ZoneId zone);
   
/** Provide another AClock with a fixed time according to current state. <br />
 *  <br />
 *  A not "fixed" AClock implementation shall provide a new object of a
 *  "fixing"/"fixable" type.<br />
 *  A permanently "fixed" implementation must return this.<br />
 *  @return an AClock with current time state fixed   
 */
   public AClock fix();

/** The current instant of this AClock. <br />
 *  <br />
 *  @return the time as Instant (never null)
 */
  public Instant instant();

/** The updated instant of this AClock. <br />
 *  <br />
 *  The default implementation<br /> &nbsp; &nbsp;
 *  <code>  update(); return instant() </code><br />
 *  should be  overridden in most cases.
 *  @return the time as Instant (never null)
 */
  public default Instant setInstant(){ update(); return instant(); }

  
/** The current time of this Clock. <br />
 *  <br />
 *  The default implementation<br /> &nbsp; &nbsp;
 *  <code>  return instant().toEpochMilli() </code><br />
 *  should be  overridden in most cases.
 *  @return the ms since 1.1.1970
 */
   public default long millis(){ return instant().toEpochMilli(); }

/** The updated ms of this AClock. <br />
 *  <br />
 *  The default implementation<br /> &nbsp; &nbsp;
 *  <code>  update(); return instant() </code><br />
 *  <br /> 
 *  The default implementation is just <br /> &nbsp; &nbsp;
 *  <code> {@link #update()}; return {@link #millis()} </code>. <br />
 *  @return the ms since 1.1.1970
 */
   public default long setActTimeMs(){ update(); return millis(); }
   
/** The current zone related time of this AClock. <br />
 *  <br />
 *  The default implementation<br /> &nbsp; &nbsp;
 *  <code>   return ZonedDateTime.ofInstant(instant(), getZone()) </code><br />
 *  should be overwritten in most cases.
 *  @return the zoned time (never null) related to 
 *           this Clock's {@link #getZone() zone}
 */
   public default ZonedDateTime getActTime(){ 
      return ZonedDateTime.ofInstant(instant(), getZone());
   } // getActTime()
  

/** Update and get current zone related time of this AClock. <br />
 *  <br />
 *  The default implementation<br /> &nbsp; &nbsp;
 *  <code>   update(); return getActTime(); </code><br />
 *  should be overwritten in most cases.
 *  @return the zoned time (never null) related to this Clock's
 *          {@link #getZone() zone}
 */
   public default ZonedDateTime setActTime(){ 
      update();
      return getActTime();
   } // setActTime()

   
/** The current time of this AClock related to a given zone. <br />
 *  <br />
 *  @param zone the zone to relate to; 
 *              null will be this Clock's {@link #getZone() zone}
 *  @return the zoned time (never null)
 */
   public default ZonedDateTime getActTime(ZoneId zone){ 
      if (zone == null || zone == getZone() || zone.equals(getZone()))
         return getActTime();
      return ZonedDateTime.ofInstant(instant(), zone);
   } // getActTime()
   
/** Update this AClock to its current time. <br />
 *  <br />
 *  The (seldom) "asynchronous" implementations of AClock will return a 
 *  current updated time with all reading methods. For those implementations
 *  {@link #update()} will never have and effect (and shall be implemented
 *  just returning true). <br />
 *  <br />
 *  All "synchronous" implementations of AClock shall keep their state until
 *  {@link #update()} is called directly or indirectly by
 *  {@link #setActTimeMs()}, {@link #setInstant()} or {@link #setActTime()}.  
 *  <br />
 *  The return value true shall inform the caller that a substantial change
 *  has been caused since the previous {@link #update() update}. Substantial
 *  is meant relative to this clock's resolution; that may usually be ms but
 *  will depend on application and platform. If in doubt or if the calculation
 *  is intolerably expensive return true.<br /> 
 *  Rule: The caller (if interested on the return value at all) must tolerate
 *  a "false true" but may fail on a "false false".<br />
 *  <br />
 *  @return true if a substantial change relative to this clock's resolution
 */
   public boolean update(); 
   
/** The state as String. <br />
 *  <br />
 *  An implementation should always override the implementation if inherited
 *  from java.lang.Object. It is recommended to return something like <br />
 *  "ClockTypeName[instantToString, zoneToString]"
 */
   @Override  public String toString();

/** Format the actual time freely (PHP format string). <br />
 *  <br />
 *  The default implementation is <br /> &nbsp; &nbsp; &nbsp;
<code>return {@link TimeHelper}.format(form, {@link #getActTime()})</code>
 *  @see de.frame4j.time.TimeHelper#format(CharSequence, ZonedDateTime)
 */
   public default String format(CharSequence form){
      return TimeHelper.format(form, getActTime());
   } // toString(CharSequence)

/** Format the actual time according to DIN. <br />
 *  <br />
 *  The default implementation is <br /> &nbsp; &nbsp; &nbsp;
<code>return {@link TimeHelper}.formatDIN({@link #getActTime()})</code>
 *  @see de.frame4j.time.TimeHelper#formatDIN(ZonedDateTime)
 */
   public default String formatDIN(){
      return TimeHelper.formatDIN(getActTime());
   } // toString(CharSequence)
   
   public default ZonedDateTime of(final int hour, final int minute, 
                            final int second, final int nanoOfSecond){
      return ZonedDateTime.of(getYear(), getMonthOfYear(), getDayOfMonth(), 
                   hour, minute, second, nanoOfSecond, getZone());
   } // of(4*int)

/** The year. <br />
 *  <br />
 *  @return the full year, e.g. 2016
 */
   public default int getYear(){ return getActTime().getYear(); }
   
/** The month of the year. <br />
 *  <br />
 *  @return month of the {@link #getYear() year}, Jan..Dec
 */
   public default Month getMonth(){ return getActTime().getMonth(); }
   
   /** The month of the year. <br />
 *  <br />
 *  @return month of the {@link #getYear() year}, 1..12; <br /> &nbsp; &nbsp;
 *    {@link #getMonth()  getMonth()}.{@link Month#getValue() getValue()}
 */
   public default int getMonthOfYear(){ return getMonth().getValue(); }   

/** The day in the month. <br />
 *  <br />
 *  @return day of {@link #getMonthOfYear() month}, 1..31
 */
   public default int getDayOfMonth(){ return getActTime().getDayOfMonth(); }
   
   
/** The hour of the day. <br />
 *  <br /> 
 *  @return the hour of the {@link #getDayOfMonth() day}, 0..23
 */
   public default int getHour(){ return getActTime().getHour(); }

/** The minute in the hour. <br />
 *  <br /> 
 *  @return the minute of the {@link #getHour() hour}, 0..59
 */
   public default int getMinute(){ return getActTime().getMinute(); }

/** The second in the minute. <br />
 *  <br /> 
 *  @return the second of {@link #getMinute() minute}, 0..59 (60, 61)
 */
   public default int getSecond(){ return getActTime().getSecond(); }

/** The nanoseconds in the second. <br />
 *  <br /> 
 *  @return the ns in the {@link #getSecond() second}, 0..999999999
 */
   public default int getNano(){ return getActTime().getNano(); }
   
/** The milliseconds in the second. <br />
 *  <br /> 
 *  @return {@link #getNano() getNano() / } 1000000
 */
    public default int getMilli(){ return getNano() / 1000000; }   
   
 } // AClock (17.08.2016)
