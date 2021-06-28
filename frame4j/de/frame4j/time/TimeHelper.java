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

import java.text.DateFormatSymbols;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.zone.ZoneRules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.frame4j.text.TextHelper;
import de.frame4j.util.Action;
import de.frame4j.util.App;
import de.frame4j.util.AppHelper;
import de.frame4j.util.AppLangMap;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;

import static de.frame4j.time.AClock.*;

/** <b>Methods and (final) values for time / date handling</b>. <br />
 *  <br />
 *  This class features some methods &mdash; partly very powerful &mdash; for
 *  time and calendar tasks, as well as some relevant values.<br />
 *  <br />
 *  August 2016 Frame4J has removed its former time and date types ConstTime, 
 *  Time, TimeRO and TimeST from package de.frame4j.util as most of its 
 *  benefits were taken to Java8's java.time package. Frame4J's better parsing
 *  and formatting (with PHP like format symbols) were kept and mainly moved 
 *  to this class.<br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2000 - 2009, 2016 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see      de.frame4j.util.ComVar
 */
 // so far :  V.10+  (30.11.2008) :  cvsNT>SVN; parse improvement 13:15 OK
 //           V.36-  (02.01.2009) :  ported from de.a_weinert (German)
 //           V.o78+ (20.02.2009) :  application as inner ano. App
 //           V.o98+ (20.03.2009) :  loop as start parameter
 //           V.134+ (04.08.2016) :  refactored to Frame4J'89 slimline
 
@MinDoc(
   copyright = "Copyright 2000 - 2009, 2016  A. Weinert",
   author  = "Albrecht Weinert",
   version = "V.$Revision: 56 $",
   lastModified = "$Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage = "import TimeHelper to your classes  or\n\n"
        +  "  java de.frame4j.time.TimeHelper [options] [time [time .. [loop]",  
   purpose = "common time and date utilities and constants, parser"
) public final class TimeHelper implements ComVar { 

/** Make no object of this class. */
   private TimeHelper(){}

//--------------    Time and Date Values           -------------------------
   
/** Days in four years (implying one of it being a leap year). <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   public static final int DAYSin4YEARS = 365 * 3 + 366;


/** Lengths of months summed up for a leap year. <br />
 *  <br />
 *  The first month [0] (only in this case!) as index for this array is March, 
 *  the last one [11] is February.<br />
 *  The content is: <br />
 *  { 31, 61, 92, 122, 153, 184, 214, 245, 275, 306, 337, 366 } <br />
 *  <br />
 *  Implementation hint: Letting, occasionally, start the year on March is a 
 *  known algorithmic trick for calendar problems, gratefully to the
 *  inventors, used here and there in this package also.<br />
 *  The background (usually unknown to past Kings and Emperors) is, that 
 *  putting extra &quot;leap;&quot; days, hours (summer time), seconds at the
 *  end of day and or year avoids complications, otherwise unavoidable.<br />
 */
   static int[] MLnAbMRZ = 
       { 31, 61, 92, 122, 153, 184, 214, 245, 275, 306, 337, 366 }; 
      // [0 March],                              , 9 D, 10 J 11 F


/** English abbreviated names of days in week [0=Sunday - 6=Saturday]. <br />
 *  <br />
 *  The indexing is 0=Sunday .. 6=Saturday, 7=Sunday (again)
 *  Three letter abbreviations.<br />
 */
   static String[] WDAY3EN =  {"Sun", "Mon", "Tue", "Wed", "Thu",
                                                    "Fri", "Sat", "Sun"};

/** English abbreviated names of months [0=, 1=January - 12=December]. <br />
 *  <br />
 *  Three letter abbreviations.<br />
 *  Index 0 gives the {@link ComVar#EMPTY_STRING empty String} 
 *  ({@link de.frame4j.util.ComVar#EMPTY_STRING EMPTY_STRING}).<br />
 */
   static String[] MONtH3EN = {ComVar.EMPTY_STRING,
                "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                "Sept", "Oct", "Nov", "Dec" };

   
//--------------    A common Time Stamp            -------------------------
   


//--------------    Time and Date Formatting       -------------------------

/** Format the time part as hh:mm:ss. <br />
 *  <br />
 *  Format hh:mm:ss from the date time parameter <br />
 *  @param zoDtTi the time to format
 *  @param bastel where to append the formatted time; 
 *        if null an empty StrinBuuilder is supplied
 *  @return bastel
 */
   public static final StringBuilder formHH_MMSS(final ZonedDateTime zoDtTi, 
                                        StringBuilder bastel){
      if (bastel == null) bastel = new StringBuilder(20);
      bastel.append(TextHelper.twoDigit(zoDtTi.getHour())).append(':');
      bastel.append(TextHelper.twoDigit(zoDtTi.getMinute())).append(':');
      bastel.append(TextHelper.twoDigit(zoDtTi.getSecond()));
      return bastel;
   } // formHH_MMSS(ZonedDateTime, StringBuilder)

/** Formatting of date and time as 2006-08-14 23:21:59 . <br />
 *  <br />
 *  This method formats the point of {@code time} defined by the parameter in
 *  in SQL format. 
 *  It knows nothing about time zones or so, but it is sortable as 
 *  String.<br />
 *  This is the format preferred for (MY-) SQL-DATETIME or -TIMESTAMP.<br />
 *  <br />
 *  @return  the as String like  &quot;2006-08-14 23:21:59&quot;
 *  @param   time ms since 1.1.1970
 */
   public static String formatSQL(final long time){
      final Instant inst = Instant.ofEpochMilli(time);
      final ZonedDateTime zoDtTi = ZonedDateTime.ofInstant(inst, UTC_ZONE);
      return formatSQL(zoDtTi);
   } // formatSQL(long)
   
/** Formatting of date and time as 2006-08-14 23:21:59 . <br />
 *  <br />
 *  This method formats the point of {@code time} defined by the parameter in
 *  in SQL format. 
 *  It knows nothing about time zones or so, but it is sortable as 
 *  String.<br />
 *  This is the format preferred for (MY-) SQL-DATETIME or -TIMESTAMP.<br />
 *  <br />
 *  @return  the as String like  &quot;2006-08-14 23:21:59&quot;
 *  @param zoDtTi  the time to format 
 */
   public static String formatSQL(final ZonedDateTime zoDtTi){
      final StringBuilder bastel = new StringBuilder(20);
      
      bastel.append(zoDtTi.getYear()).append('-');
      bastel.append(TextHelper.twoDigit(zoDtTi.getMonthValue())).append('-');
      bastel.append(TextHelper.twoDigit(zoDtTi.getDayOfMonth())).append(' ');
      return formHH_MMSS(zoDtTi, bastel).toString();
   } // formatSQL(ZonedDateTime)
   

/** Format date and time of day in the Form 08.01.2001 10:43:59. <br />
 *  <br />
 *  This method gives the usual format according to 
 *  &quot;DIN 5008 optional&quot;, being the decade long and still persisting 
 *  habitualness in many European countries.<br />
 *  <br />
 *  This method is a (faster) replacement for 
 *  {@link #format(java.lang.CharSequence, ZonedDateTime)
 *  format(&quot;j.m.Y H:i:s&quot;, zoDtTi)}.<br />
 *  The performance gain is due to no parsing the format string.<br />
 *  <br />
 *  @return        The time as String  like 
 *                 &quot;12.01.2009 11:19:54&quot;
 *  @param zoDtTi  the time to format
 */
   public static String formatDIN(final ZonedDateTime zoDtTi){
      StringBuilder bastel = new StringBuilder(20);
      bastel.append(TextHelper.twoDigit(zoDtTi.getDayOfMonth())).append('.');
      bastel.append(TextHelper.twoDigit(zoDtTi.getMonthValue())).append('.');      
      bastel.append(zoDtTi.getYear()).append(' ');
      return formHH_MMSS(zoDtTi, bastel).toString();
   } // formatDIN(ZonedDateTime)
   

/** Format date and time of day in the Form 08.01.2001 10:43:59. <br />
 *  <br />
 *  This method gives the usual format according to 
 *  &quot;DIN 5008 optional&quot;, being the decade long and still persisting 
 *  habitualness in many European countries.<br />
 *  <br />
 *  This method uses the default (/system) time zone
 *  ({@link de.frame4j.time.AClock#DEF_ZONE DEF_ZONE}).<br />
 *  <br />
 *  @return        The time as String  like 
 *                 &quot;12.01.2009 11:19:54&quot;
 *  @param time    the time to format as  ms from 1.1.1970
 */
   public static String formatDIN(final long time){
      final Instant inst = Instant.ofEpochMilli(time);
      final ZonedDateTime zoDtTi = ZonedDateTime.ofInstant(inst, DEF_ZONE);
      return formatDIN(zoDtTi);
   } // formatDIN(long)
   

//--------------    Time and Date Formatting with PHP format String   ------

/** Format date and time of day freely (PHP format string). <br />
 *  <br />
 *  This method formats this Time by using a format String, like e.g.<br />
 *  &#160; <code> &quot;D, d.m.Y, H:i:s (l)&quot;</code>.<br />
 *  <br />
 *  In a format single letters are significant:<ul>
  <li> D  &#160; day of week as two letter abbreviation (So Mo ..)</li>
  <li> l  &#160; day of week as full word (Sonntag .. or Sunday)</li>
  <li> w  &#160; one digit day of week (1..7 for Sunday to Saturday)</li>
  <li> W  &#160; ISO-8601 week number of year, weeks starting on Monday</li>
 *
  <li> j  &#160; day in month one or two digit (1..31)</li>
  <li> d  &#160; day in month two digit (01..31)</li>
 * 
  <li> F  &#160; month as word (Januar .. January ..)</li>
  <li> m  &#160; month as two digit number (01..12)</li>
  <li> n  &#160; month number, without leading zeros  (1..12)</li>
  <li> M  &#160; month abbreviated to four letters including trailing dot
                 (Jan. Feb. .. Mai Juni ...; Jan. Feb. .. May June ... )</li>
  <li> t  &#160; number of days in the given month   (28 .. 31)</li>
 *
  <li> y  &#160; two digit year (09)</li>
  <li> Y  &#160; four digit year (2009)</li>
  <li> L  &#160; 1 if leap year, 0 else  (0, 1)</li>
 *
  <li> H  &#160; hour (00..23)</li>
  <li> G  &#160; hour (0..23)</li>
  <li> i  &#160; minute (00..59)</li>
  <li> s  &#160; second (00..59)</li> 
  <li> u  &#160; thousandth of second (ms) three digits (000 .. 999)</li>
 *
  <li> h  &#160; hour (00..12) &mdash; Attention: American ambiguous</li>
  <li> g  &#160; hour (0..12) &mdash; Attention: American ambiguous</li>
  <li> a  &#160; am or pm </li>
  <li> A  &#160; AM or PM</li> 
 *
  <li> T  &#160; (Base) time zone short (CET)</li> 
  <li> e  &#160; (Base) time zone long 
             (Central European Time / Mitteleuropäische Zeit)</li>
  <li> I  &#160; light saving (Sommerzeit) short (SZ, WZ, sum, std )</li>
  <li> O  &#160; difference to UTC in two digit hours concatenated with
 *            two digit minutes  Example: +0100 (for CET) </li>
 *
  <li> c  &#160; ISO 8601 full date time 2009-01-12T14:53:11+01:00</li>
  <li> r  &#160; RFC 2822 full date time Mon, 12 Jan 2009 14:53:11+0100</li>
  <li> U  &#160; seconds since 1.1.1970 UTC </li>
 </ul><br />
 *  Remark: As probably noticed, these are predominantly PHP (date_format) 
 *  symbols and not the Java8 ones. Hence most Web application format strings
 *  are applicable here and vice versa.<br />
 *  <br />
 *  All other letters or signs are directly transfered to the output. If one
 *  of the significant letters shall go as is to output, it has to be 
 *  escaped by  \ (backslash). A  backslash \ itself is given as \\.<br />
 *  If the format String is null or empty, a standard format 
 *  ({@link #toString()}) is used.<br />
 *  If the format String starts with something like 
 *  &quot;&lt;en&gt;&quot;, &quot;&lt;EN&gt;&quot;, &quot;&lt;de&gt;&quot;,
 *  &quot;&lt;DE&gt;&quot;, &quot;&lt;fr&gt;&quot;, being a two letter
 *  language code, the names of days, months zones etc. are in this
 *  language.<br />
 *  <br />
 *  The available languages may depend on those implemented in the class
 *  {@link AppLangMap}. English and German must always be available 
 *  and (hopefully) the platform language.<br />
 *  <br />
 *  <b>Example</b>: toString(&quot;&lt;de&gt;D, \d\er j.m.Y, H:i:s&quot;) 
 *  gives &quot;Montag, der 12.01.2009, 07:44&quot;
 *  @param form The format String. 
 *  @return     The time as String, like e.g. "22.12.98 13:55:59"
 *  @see de.frame4j.text.TextHelper#twoDigit(int)
 *  @see #formatDIN(ZonedDateTime)
 *  @see AppLangMap
 */
  public static String format(final CharSequence form, 
             final ZonedDateTime zoDtTi){ return format(form, zoDtTi, null); }
  
/** Format date and time of day freely with default time zone. <br />
 *  <br />
 *  This method is, except for giving the date and time in ms and using 
 *  {@link de.frame4j.time.AClock#DEF_ZONE  DEF_ZONE}, like 
 *  {@link #format(CharSequence, ZonedDateTime)}.<br />
 *  <br />
 *  @param form Format; see {@link #format(CharSequence, ZonedDateTime)}
 *  @param time ms since 1.1.1970
 */ 
   public static String format(final CharSequence form, final long time){
      return format(form, time, null);
   } // format(CharSequence, long)

/** Format date and time of day freely with default time zone. <br />
 *  <br />
 *  This method is, except for giving the date and time in ms and using 
 *  {@link de.frame4j.time.AClock#DEF_ZONE  DEF_ZONE}, like 
 *  {@link #format(CharSequence, ZonedDateTime)}.<br />
 *  <br />
 *  @param form Format; see {@link #format(CharSequence, ZonedDateTime)}
 *  @param inst the time 
 */ 
   public static String format(final CharSequence form, final Instant inst){
      return format(form, inst, null);
   } // format(CharSequence, long)
   

/** Format date and time of day freely with default time zone. <br />
 *  <br />
 *  This method is, except for giving the date and time in ms and using 
 *  {@link de.frame4j.time.AClock#DEF_ZONE  DEF_ZONE}, like 
 *  {@link #format(CharSequence, ZonedDateTime)}.<br />
 *  <br />
 *  @param form Format; see {@link #format(CharSequence, ZonedDateTime)}
 *  @param time the time if {@link #instanceOfTime(Object) instanceOfTime}
 *  @return the formated time if feasible or the 
 *                  {@link ComVar#EMPTY_STRING empty String} 
 */ 
   public static String format(final CharSequence form, final Object time){
      if (time instanceof Instant) return format(form, (Instant)time, null);
      if (time instanceof ZonedDateTime) 
                       return format(form, (ZonedDateTime)time, null);
      if (time instanceof Long)
                  return format(form, ((Long)time).longValue(), null);
      if (time instanceof Date)
                  return format(form, ((Date)time).toInstant(), null);
      return ComVar.EMPTY_STRING;
   } // format(CharSequence, Object)

/** Is an object of a time type. <br />
 *  <br />
 *  The types accepted are (as of Frame4J Rev.168, Aug. 2016): 
 *  {@link Long}, {@link Instant}, {@link ZonedDateTime}, {@link Date}.
 *  <br /> 
 *  @param time the object to be type checked
 *  @return true if time is an instance of a of a type formattable by
 *          {@link #format(CharSequence, Object)}
 */
   public static boolean instanceOfTime(final Object time){
      if (time == null) return false;
      Class<? extends Object> cl = time.getClass();
      return cl == Long.class || cl == Instant.class || cl == Date.class
                              || cl == ZonedDateTime.class; 
   } // instanceOfTime(Object)
   

/** Format date and time of day freely with explicit time zone. <br />
 *  <br />
 *  This method is, except for giving the date and time in ms, like 
 *  {@link #format(CharSequence, ZonedDateTime)}.<br />
 *  <br />
 *  @param form Format; see {@link #format(CharSequence, ZonedDateTime)}
 *  @param time ms since 1.1.1970
 *  @param zone the time zone to be used; null will be
 *              {@link de.frame4j.time.AClock#DEF_ZONE  DEF_ZONE}
 */ 
   public static String format(final CharSequence form, final long time,
                                                         final ZoneId zone){
      final Instant inst = Instant.ofEpochMilli(time);
      return format(form, inst, zone);
   } // format(CharSequence, long, ZoneId zone)

/** Format date and time of day freely with explicit time zone. <br />
 *  <br />
 *  This method is, except for giving the date and time in ms, like 
 *  {@link #format(CharSequence, ZonedDateTime)}.<br />
 *  <br />
 *  @param form Format; see {@link #format(CharSequence, ZonedDateTime)}
 *  @param inst the time 
 *  @param zone the time zone to be used; null will be
 *              {@link de.frame4j.time.AClock#DEF_ZONE  DEF_ZONE}
 */ 
   public static String format(final CharSequence form, final Instant inst,
                                                               ZoneId zone){
      if (zone == null) zone = DEF_ZONE;
      final ZonedDateTime zoDtTi = ZonedDateTime.ofInstant(inst, zone);
      return format(form, zoDtTi, null);
   } // format(CharSequence, long, ZoneId zone)
   

/** Format date and time of day freely and with explicit language. <br />
 *  <br />
 *  This method is, except for the choice of language, like 
 *  {@link #format(CharSequence, ZonedDateTime)}.<br />
 *  <br />
 *  @param form Format; see {@link #format(CharSequence, ZonedDateTime)}
 *  @param alm explicit language (null: default)
 */ 
   public static String format(final CharSequence form, 
                        final ZonedDateTime zoDtTi, AppLangMap alm){
     if (form == null) return formatDIN(zoDtTi);
     final int formL = form.length(); 
     if (formL == 0) return formatDIN(zoDtTi);
     
     CharSequence almCode = null;
     int i = 0; // char-Index - Start
     int ch = form.charAt(0);
     stc: if (ch == '<' && formL >= 5) {
        ch = form.charAt(1);
        char c2 = form.charAt(2);
        char c3 = form.charAt(3);
        if (c3 == '>') { ///  <de>..., <en>....,
           i = 4;
           almCode = new StringBuilder(2).append(ch).append(c2); 
           break stc;
        } ///  <de>..., <en>...., <fr>....
     } // <..>
     
     final int dayOfWeek = zoDtTi.getDayOfWeek().getValue();
     final int day    = zoDtTi.getDayOfMonth();
     final int month  = zoDtTi.getMonthValue();
     final int year   = zoDtTi.getYear();
     final int hour   = zoDtTi.getHour();
     final int minute = zoDtTi.getMinute();
     final int second = zoDtTi.getSecond();
     final int ms     = zoDtTi.getNano() / 1000000;
     ZoneId zone = zoDtTi.getZone();
     if (zone == null) zone = AClock.UTC_ZONE; // should never happen
     ZoneRules rules = zone.getRules();
     Instant inst = zoDtTi.toInstant();
     boolean dayLightSaving = rules.isDaylightSavings(inst);
     
     if (almCode != null) alm = null; // if language code given, make new
     
     StringBuilder bastel = new StringBuilder(30);
     thruForm: for ( ; i < formL; ++i) {
        ch = form.charAt(i);
        if (ch == '\\') {
           ++i;
           if (i == formL) break thruForm;
           bastel.append(form.charAt(i));
           ++i;
           continue thruForm;
        } // \x escapes to x
        boolean shrt = false;
        switch (ch) {    
           default: bastel.append((char) ch); 
           break;
           case'D':   // day of week short  (mostly 2 characters)
              shrt = true; // fall through
           case 'l':   // day of week fully
              if (alm == null) alm = AppLangMap.getMap(almCode);
              bastel.append(shrt ? alm.shortWeekDay(dayOfWeek) 
                                      : alm.weekDay(dayOfWeek));
              break; 
           case 'w':   // day of week 1..7
              bastel.append(dayOfWeek);
              break; 
           case 'W':   // Number of week
              bastel.append(weekInYear(zoDtTi.getDayOfYear(), dayOfWeek));
              break; 
  
           case'M':    // month short
              shrt = true; // fall through
           case'F':    // month <= 4 characters
              if (alm == null) alm = AppLangMap.getMap(almCode);
              bastel.append(shrt ? alm.shortMonth(month)
                                      : alm.month(month));
              break; 
           case'm':   // month two digits
              bastel.append(TextHelper.twoDigit(month));
              break;
           case'n':   // month two digits
              bastel.append(month);
              break;
  
           case'j': // day 1-2 digits 1..31
              bastel.append(day);  
              break; 
           case'd': // day zwei digits 01..31
              bastel.append(TextHelper.twoDigit(day));  
              break; 
  
  
           case'y': bastel.append(TextHelper.twoDigit(year));
              break; 
           case'Y': bastel.append(year);
              break; 
  
           case'H': bastel.append(TextHelper.twoDigit(hour)); // 00..23
              break; 
           case'G': bastel.append(hour); // 0..23
              break; 
           case'g':
              shrt = true; // intentionally fall through
           case'h':        // 27.08.14  hour -> (below)
              int tmp = hour > 12 ? hour -12 : (hour == 0 ? 12 : hour); 
              if (shrt)
                 bastel.append(tmp); // 0..12
              else
                 bastel.append(TextHelper.twoDigit(tmp)); // 00..12
              break; 
  
           case'a':
              shrt = true;  // intentionally fall through
           case'A':         // 27.08.14 > -> >=
              bastel.append(hour >= 12 ? shrt?"pm":"PM" : shrt?"am":"AM");
              break;
  
           case'i':   // minute two digit
              bastel.append(TextHelper.twoDigit(minute));
              break;
  
           case's': 
              bastel.append(TextHelper.twoDigit(second)); 
              break; 
  
           case'u': // ms three digit
              bastel.append(TextHelper.threeDigit(ms));
              break;
  
           case'L':  
              boolean leapY = TimeHelper.isLeapYear(year);
              bastel.append(leapY ? '1' : '0'); // PHP horrible
              break; // do we leave this
  
  
           case 'T': 
              shrt = true;
           case 'e':  // time zone long / short with daylight
              if (alm == null) alm = AppLangMap.getMap(almCode);
              Locale loci = alm.getLocale();
              bastel.append(zone.getDisplayName(
                            shrt ? TextStyle.SHORT : TextStyle.FULL, loci ));
              break;
  
           case 'I': // daylight saving  abbreviated
              if (alm == null) alm = AppLangMap.getMap(almCode);
              bastel.append(alm.summerTime(dayLightSaving, true));
              break;
  
           case 'U': // seconds since 1.1.1970
              bastel.append(inst.getEpochSecond());
              break;
  
           case 'r': // RFC [2]822
              formatRFC(zoDtTi, bastel);
              break;
  
           case 'c': // ISO 8601 full date 
              formatISO(zoDtTi, bastel);
              break;
  
        }} // for switch
     return bastel.toString();
  } // format(CharSequence, ZonedDateTime, AppLangMap)

//--------------    Time and Date Formatting fixed formats DIN/ISO/.. ------   

/** Format date and time of day like  Mon, 11 Mar 2002 14:09:45 +0100 . <br />
 *  <br />
 *  This method is mainly used for formating time for e-mail 
 *  applications.<br />
 *  <br /> 
 *  (Date:) conforming to RFC 822 (RFC 2822 ?).<br />
 *  <br />
 *  @return  The time as String, like e.g.
 *                &quot;Mon, 11 Mar 2002 14:09:45 +0100&quot;
 *  @see de.frame4j.text.TextHelper#twoDigit(int)
 */
   public static StringBuilder formatRFC(final ZonedDateTime zoDtTi, StringBuilder bastel){
      if (bastel == null) bastel = new StringBuilder(30);
      bastel.append(TimeHelper.WDAY3EN[zoDtTi.getDayOfWeek().getValue()]).append(' ');
      bastel.append(TextHelper.twoDigit(zoDtTi.getDayOfMonth())).append(' ');
      bastel.append(TimeHelper.MONtH3EN[zoDtTi.getMonthValue()]).append(' ');
      bastel.append(zoDtTi.getYear()).append(' ');
      formHH_MMSS(zoDtTi, bastel);
      bastel.append(' ');
      
      int offset = zoDtTi.getOffset().getTotalSeconds();
      
      if (offset == 0) {
         bastel.append("+0000");
      } else {
         if (offset < 0) {
            offset = -offset / 60;
            bastel.append('-');
         } else {
            bastel.append('+');
            offset /= 60; // in minutes
         }
         
         bastel.append(TextHelper.twoDigit(offset / 60));
         bastel.append(TextHelper.twoDigit(offset % 60));
      }
      return bastel;      
   } // formatRFC(ZonedDateTime, StringBuilder)

   
/** Format date and time of day like  Mon, 11 Mar 2002 14:09:45 +0100 (GMT). <br />
 *  <br />
 *  This method is mainly used for formatting time for e-mail 
 *  applications.<br />
 *  <br /> 
 *  (Date:) conforming to RFC 822 (RFC 2822 ?).<br />
 *  <br />
 *  @return  The time as String, like e.g.
 *                &quot; Mon, 11 Mar 2002 14:09:45 +0100 (GMT)&quot;
 *  @see de.frame4j.text.TextHelper#twoDigit(int)
 */
   public static String formatRFC(final ZonedDateTime zoDtTi){
      StringBuilder bastel = formatRFC(zoDtTi, null);
      return bastel.append("(GMT)").toString();      
   } // formatRFC(ZonedDateTime)
   

/** Format date and time of day like   Mon, 11 Mar 2002 14:09:45 +0100. <br />
 *  <br />
 *  This method is mainly used for formatting time for e-mail 
 *  applications.<br />
 *  <br /> 
 *  (Date:) conforming to RFC 822 (RFC 2822 ?).<br />
 *  <br />
 *  @return  The time as String, like e.g.
 *                &quot; Mon, 11 Mar 2002 14:09:45 +0100&quot;
 *  @param   zone the time zone; null will be
 *            {@link de.frame4j.time.AClock#CET_ZONE CET_ZONE}              
 */
   public static String formatRFC(final Instant inst, ZoneId zone){
      if (zone == null) zone = CET_ZONE;
      
      final ZonedDateTime zoDtTi = ZonedDateTime.ofInstant(inst, zone);
     return formatRFC(zoDtTi, null).toString();      
   } // formatRFC(Instance, ZoneId)
   

/** Format date and time of day like   2009-02-02T11:00:00.000Z . <br />
 *  <br />
 *  This method is mainly used for formating time for e-mail 
 *  applications.<br />
 *  <br /> 
 *  (Date:) conforming to ISO 8601.<br />
 *  <br />
 *  @return  The time as String, like e.g.
 *                &quot;2009-02-02T11:00:00.000Z&quot;
 *  @see de.frame4j.text.TextHelper#twoDigit(int)
 */
   public static StringBuilder formatISO(final ZonedDateTime zoDtTi,
                                                        StringBuilder bastel){
      if (bastel == null) bastel = new StringBuilder(30);
      bastel.append(zoDtTi.getYear()).append('-');
      bastel.append(TextHelper.twoDigit(zoDtTi.getMonthValue())).append('-');
      bastel.append(TextHelper.twoDigit(zoDtTi.getDayOfMonth())).append('T');
      formHH_MMSS(zoDtTi, bastel).append('.');
      final int ms     = zoDtTi.getNano() / 1000000;
      bastel.append(TextHelper.threeDigit(ms));
      int offset = zoDtTi.getOffset().getTotalSeconds();
      
      if (offset == 0) {
         bastel.append('Z'); // since 08.08.16
      } else {
         if (offset < 0) {
            offset = -offset / 60;
            bastel.append('-');
         } else {
            bastel.append('+');
            offset /= 60; // in minutes
         }
         bastel.append(TextHelper.twoDigit(offset / 60));
         bastel.append(':');
         bastel.append(TextHelper.twoDigit(offset % 60));
      }
      return bastel;      
   } // formatISO(ZonedDateTime, StringBuilder)

   
/** Format date and time of day like   2009-02-02T11:00:00.000Z . <br />
 *  <br />
 *  This method is mainly used for formatting time for e-mail 
 *  applications.<br />
 *  <br /> 
 *  (Date:) conforming to ISO 8601.<br />
 *  <br />
 *  @return  The time as String, like e.g.
 *                &quot; 2009-02-02T11:00:00.000Z&quot;
 *  @see de.frame4j.text.TextHelper#twoDigit(int)
 */
   public static String formatISO(final ZonedDateTime zoDtTi){
      return formatISO(zoDtTi, null).toString();
   } // formatISO(ZonedDateTime)
   

/** Format date and time of day like  2009-02-02T11:00:00.000Z. <br />
 *  <br />
 *  This method is mainly used for formatting time for e-mail 
 *  applications.<br />
 *  <br /> 
 *  (Date:) conforming to ISO 8601.<br />
 *  <br />
 *  @return  The time as String, like e.g.
 *                &quot;2009-02-02T11:00:00.000Z&quot;
 *  @param   zone the time zone; null will 
 *           be {@link de.frame4j.time.AClock#UTC_ZONE  UTC_ZONE}              
 */
   public static String formatISO(final Instant inst, ZoneId zone){
      if (zone == null) zone = UTC_ZONE;
      final ZonedDateTime zoDtTi = ZonedDateTime.ofInstant(inst, zone);
      return formatISO(zoDtTi, null).toString();      
   } // formatISO(Instant, ZoneId)


/** Format date and time of day as 8 Jan 2001 10:43:59 GMT. <br />
 *  <br />
 *  This method is suitable for Internet and logging applications.<br />
 *  <br />
 *  @return      time like &quot;8 Jan 2001 10:43:59 GMT&quot;
 *  @see de.frame4j.text.TextHelper#twoDigit(int)
 */
   public static String formatGMT(ZonedDateTime zoDtTi){
      
      int offset = zoDtTi.getOffset().getTotalSeconds();
      if (offset != 0) {
         zoDtTi = ZonedDateTime.ofInstant(zoDtTi.toInstant(), GMT_ZONE);
      }
      StringBuilder bastel = new StringBuilder(30);
      
      bastel.append(zoDtTi.getDayOfMonth()).append(' ');
      bastel.append(TimeHelper.MONtH3EN[zoDtTi.getMonthValue()]).append(' ');
      bastel.append(zoDtTi.getYear()).append(' ');

      formHH_MMSS(zoDtTi, bastel);
      return bastel.append(" GMT").toString();
   } // formatGMT(ZonedDateTime)
   
    
//--------------    Time and Date Arithmetics      -------------------------

/** Calculate the days since  1&#160;March 1968 .<br />
 *  <br />
 *  @param d day in month, 1 .. max, but not restricted<br /> &nbsp; &nbsp;
 *  (using d-=100 before would go 100 days before the given date).
 *  @param m Month, 1..12, (other values are illegal and default to March of
 *       year j or year j-1)
 *  @param j full year like  1871, 1953, 1998, 2009, ...)
 */
   static public int daysSince1Mrz68(int d, int m, int j){
      if (m < 3) {
         m += 8; 
         j-=1;        // year starts March trick
      } else { 
         m -= 4;       // m == -1 is March m=10, February 
      }   
      j -= 1968;
      int jd4 = j / 4;
      int jm4 = j % 4;
      if (jm4 < 0) {
         jd4 -= 1;
         jm4 += 4;
      }
      if (m >= 0 && m < 11)
         m = MLnAbMRZ[m];
      else
         m = 0; // default March 1st

      d = jd4 * DAYSin4YEARS + jm4 * 365 + m +  d - 1;
      if (d > 47540 + 671) { // 1.3.2100  47540
         d -= 1;
      } else  if (d < -24837) { // < 1.3.1900
         d+=1; // 1900 is no leap year
         if (d < -61361) {
            d += 1; // < 1.3.1800 (no leap year) (wrong 671 -62033)
            if (d < -97885) d+=1;  // < 1.3.1700
         }
      } // < 1. March 1900
      return d;
   } // daysSince1Mrz68(3int)   (18.05.1999)

/** Number of week. <br />
 *  <br />
 *  The week number according to DIN 1355 / ISO 8601 is calculated:<ul>
 <li> A week is Monday to Sunday.</li>
 <li> Week 1 contains the first Thursday respectively January 4th.</li></ul>
 *  Days before week number 1 get week 0.
 *  @param dayInYear the day within the year; 1..365|366: Jan1st..Dec31st
 *  @param dayOfWeek the day in the week of dayInYear; 
 *                   0..6: Monday..Saturday, all else: Sunday
 *  @return the ISO week number 0..53  
 */
  public static int weekInYear(int dayInYear, int dayOfWeek){
     if (dayOfWeek <= 0 || dayOfWeek > 7) dayOfWeek = 7;
     final int doDayM1 = dayInYear - dayOfWeek + 3 + 14;
     return doDayM1 / 7 -1;
  } // weekInYear(2*int)   (11.08.2016


/** Transform Microsoft time stamp (long, ns) to  Java time (long, ms). <br />
 *  <br />
 *  Microsoft's long time stamp in in nanoseconds and differs in start 
 *  time.<br />
 *  <br />
 *  @see #javaTimetoMS(long)
 */   
   public static long msToJavaTime(final long msTimeStamp){
      return  msTimeStamp / 10000L - 11644473600000L;
   } // msToJavaTime(long)

/** Transform Java time (long, ms to Microsoft time stamp (long, ns). <br />
 *  <br />
 *  @see #msToJavaTime(long)
 */   
   public static long javaTimetoMS(final long javaTime) {
      return   (javaTime + 11644473600000L) * 10000L;
   } 

/** Leap year. <br />
 *  <br />
 *  This method applies Gregorian rules for y &gt; 1000.<br />
 */
   public static boolean isLeapYear(final int y){
      if ((y & 3) != 0) return false;
      if (y < 1582) return true; // before leap every four year ??
      return y % 400 == 0 || y % 100 != 0;
   } // isLeapYear(int)
   

//--------------    Time and Date Parsing          -------------------------   

/** Get / parse a time from String representation. <br />
 *  <br />
 *  <strong>Interpret a String as date / time of day (overview)</strong><br />
 *  <br />
 *  This method tries to interpret the String parameter as time, that is 
 *  date and / or time of day &mdash; h:m:s, h:m or h:m:s.ms &mdash;. 
 *  No time of day acts like 00:00:00.000<br />
 *  <br /> 
 *  {@link #parse(CharSequence)} and {@link #parse(CharSequence, boolean)}
 *  understands a big variety of (syntactically) different formats. Likewise 
 *  time zone offsets are understood in a variety of forms.
 *  The parsed formats include those usual in (North-) America, Europe and 
 *  Germany, as well as some Internet and operating system formats as well as 
 *  those usual in version control systems SVN and before CVS; see
 *  {@link de.frame4j.SVNkeys}).<br />
 *  <br />
 *  Should, in spite of this flexibility, all interpretation of the 
 *  parameter <tt>s</tt> fail, an clearly worded IllegalArgumentException is
 *  thrown. In the case of success, the absolute UTC/Java milliseconds 
 *  (1.1.1968  based) are returned. This class as 
 *  application (see {@link #main(String[])}) can be used to explore the 
 *  time parser.<br />
 *  <br />
 *  Without any time zone settings in the parameter, the default zone of the 
 *  underlying runtime is assumed.<br />
 *  <br />
 *  Many three or four letter time zone abbreviations are recognised, like<br />
 *  UTC, GMT / WET, CET / MEZ, BST / IST / WEST, CEST / MES,  MST / PST, EDT,
 *  CDT, MDT / PDT, HKT, VET and many more.<br />
 *  Furthermore every time zone may be given as offset to UTC by + or - in
 *  the style of &quot;Sat, 12 Aug 1995  13:30:00 +0430&quot; (4 hours 
 *  30 minutes west of Greenwich) or 
 *  &quot;Sat, 12 Aug 1995  13:30:00 +04:30&quot;.<br />
 *  A prefix GMT, UT or UTC<br />
 *  (&quot;Sat, 12 Aug 1995  13:30:00 GMT+0430&quot;)<br />
 *  or a prefix signifying the same offset (as often used by Internet
 *  applications, like CET+01:00) is allowed.<br />
 *  <br />
 *  Besides the numeric formats many keywords in English, French and German
 *  (so far, and partly other languages) are correctly interpreted 
 *  for setting dates and or times like:<br />
 *  &quot;Tagesende&quot;, &quot;end-of-day&quot;, &quot;fin-du-jour&quot;,
 *  &quot;Mittag&quot;, &quot;noon&quot;, &quot;midi&quot;,
 *  &quot;Mitternacht&quot;, &quot;midnight&quot;, &quot;minuit&quot;,
 *  &quot;heute&quot;, &quot;today&quot;, &quot;aujourd'hui&quot;,
 *  &quot;morgen&quot;, &quot;tomorrow&quot;, &quot;demain&quot;, 
 *  &quot;gestern&quot;, &quot;yesterday&quot;, &quot;hier&quot;, 
 *  &quot;vorgestern&quot;, &quot;beforeyesterday avant-hier&quot;, 
 *  &quot;vorvorgestern&quot;, &quot;jetzt&quot;, &quot;now&quot;,
 *  &quot;maintenant and some more.<br />
 *  All those keywords are not case sensitive and they may be abbreviated,
 *  like &quot;mañana&quot; or &quot;mañ&quot;, &quot;mañ.&quot; as long as no 
 *  ambiguity arises by that.<br />
 *  Running this class as application without program parameters 
 *  (java&nbsp;{@link TimeHelper de.frame4j.utils.TimeHelper}) shows all
 *  understood time keywords of Frame4J's installed version.<br />
 *  <br />  
 *  <br />
 *  <strong>Background</strong><br />
 *  <br />
 *  Two fundamental assumptions &mdash; both wrong &mdash; govern all Java
 *  format classes, before Java8 and, alas, in Java8, too. (JDK 1.1, once upon 
 *  a time, tried to make it better, but soon abandoned the apptoach.):<ul>
 *  <li>The first is that formating and 
 *  analysing (parsing) are complementary procedures. This gives any Format 
 *  class the burden of being able to parse, ruled by a grammar, that is
 *  defined by a format (control) String. This, often being not or hardly 
 *  feasible, makes this classes unnecessarily thick even if parsing with a 
 *  given format is never asked for.</li>
 *  <li>The other is, that parsing is to be bound to just one language or 
 *  locale.</li></ul>
 *  
 *  In the case of formating, of course, one often wants to control every
 *  field of the output, its sequence, its language and so on. Controlling 
 *  the output &quot;to the last 
 *  letter&quot; means defining a unique format or pattern. On the other hand,
 *  at parsing, you are perfectly within your rights when expecting, that
 *  all unambiguous notations for the same values or states are 
 *  understood:<br />
 *  &quot;15. Januar 2009 Mittag&quot; and &quot;2009-1-15 12:00&quot; denote 
 *  the same date and time of day and should be (are here) understood so
 *  without switching formats or locales.<br />
 *  <br />
 *  <br />
 *  <strong>Some accepted / parsed formats (examples)</strong><br />
 *  <br />
 *  &#160; Sat, 12 Aug 1995 13:21:00.321 GMT+1<br />
 *  &#160; Samst., 12.8.95  13:21 MEZ<br />
 *  &#160; Samstag 12. aug.  95  13:21 MEZ<br />
 *  &#160; Samedi, août 12 95  13:21 CET<br />
 *  &#160; Sat. 8/12/95 13:21 GMT+0100<br />
 *  &#160; Sat. 8/12/95 13:21 +0100<br />
 *  &#160;      Aug. 12 95 11 PM +1<br />
 *  &#160;      12 Aug 95 11 PM +1<br />
 *  &#160; Dienstag 13:46<br />
 *  &#160; jetzt<br />
 *  &#160; vorgestern<br />
 *  &#160; gestern mittag<br />
 *  &#160; now<br />
 *  &#160; yesterday noon<br />
 *  &#160; 2200-02-28T09:30:11.100+01:00<br />
 *  &#160; 2200-02-28T08:30:11.100Z<br />
 * 
 *  This is all understood as well as the same in some other sequences, parts
 *  of it and so on.<br />
 *  <br />
 *  Names of months, days of week and others are (so far) accepted in the 
 *  above mentioned languages, full length abbreviated and with and without 
 *  accents. Ambiguous abbreviations are illegal.<br />
 *  <br />
 *  Days of week have to be written correctly or abbreviated in a way not to
 *  be mixed up with abbreviated months or times zones; Saturday  is wrong.
 *  If a date (12.7.03) is given, nevertheless, the day of week is ignored as
 *  redundant information. If there is no date, a given day of week denotes
 *  the latest possible day in the past, i.e. 1 to 7 days back. This fits to
 *  the habit of some file and mail programmes, to omit the date and just give
 *  the  day of week, if the mail or the modification date is less than eight
 *  days back.<br />
 *  <br />
 *  All key word are case insensitive (CET cet CeT).<br />
 *  <br />
 *  <br />
 *  <strong>Operation</strong><br />
 *  <br />
 *  The String <tt>s</tt> is processed left to right. White spaces, underline
 *  _ and comma as well as all within braces ( ) has no significance besides
 *  the separation of fields. Brace pairs can be embedded ( Co(mme)nt ). This
 *  is a reminiscence (i.e. compatibility) to the late Date.parse(), that
 *  &mdash; why on earth &mdash; did so.<br /> 
 *  <br />
 *  Any sequence of digits 1..9 and 0 is interpreted as number.<br />
 *  Any sequence of character s up to the next separator or digit or sign
 *  (+ -) is regarded as keyword, naming a month, a day of week, a relative
 *  date, a time of day or a time zone offset.<br />
 *  <br />
 *  This is used to sift out <ul>
 *  <li>day</li>
 *  <li>month (number or name, abbreviation),</li>
 *  <li>year  (0..69 becomes 2000..2069, 70..99 becomes 1979..1999, 
 *             +0 is just 0),</li></ul>
 *  on one hand and, on the other side<ul>
 *  <li>hour</li>
 *  <li>minute</li>
 *  <li>second</li>
 *  <li>part of second or millisecond 0..999</li>
 *  <li>day of week,</li>
 *  <li>time zone (offset).</li></ul>
 *  
 *  Besides a perhaps ignored day of week, no part may explicitly or 
 *  implicitly given more than once and contradictory.<br />
 *  <br />
 *  The interpretation of a number is ruled by surrounding signs and
 *  indications already made; i.e. sequence and partly magnitude.<br />
 *  Points (.) lead to German standard interpretation day.month.year 
 *  tag.monat.jahr (in this sequence if no other info was present).<br />
 *  An unsigned number &gt; 62 (except following seconds) will be taken as
 *  year. (Limit of 62 is taking leap seconds into consideration). So a year
 *  can have any position (2003/04/24).<br />
 *  <br />
 *  Colons (:) lead to time of day interpretation hour:minute:second or
 *  hour:minute:second.millisecond .<br />
 *  An AM or vormittags is ignored if an hour 1..11 is given; 12 makes a 0
 *  in this case.<br />   
 *  A PM or nachmittags is accepted if the hour is in the range 1..12; to
 *  1..11 a 12 will be added, while 12 PM will be left as is. <br />
 *  In the end we have (internally) the non-ambiguous 0..23 range. To sum up
 *  American time: 12AM is 0; 12PM is 12; else PM is +12. <br />
 *  <br />  
 *  If no year was given signs + - lead to the interpretation as year (- as
 *  BC).<br />
 *  If a year was given signs + - lead to the interpretation as time zone
 *  offset in the range of -16 .. +16 (hours) or in the four digit hour-minute
 *  form (with or without separating colon :) that allow for exotic half (and 
 *  even quarter) hour offsets or in the two digit hour form.
 *  Time zone offsets can be made redundantly by digits and time zone 
 *  abbreviations as long as they are not contradictory.<br />
 *  <br /> 
 *  Besides those cases of AM, time zones and the ignored day of week,
 *  all else redundancy like &quot;12:00:00 noon&quot; is an error, as are 
 *  most range violations. All errors give an expressive 
 *  IllegalArgumentException.<br />
 *  <br />
 *  Keywords are understood in English, French, German, Italian and Spanish
 *  (as of November 2014). Spaces in keywords have to be replaced by hyphen 
 *  and accents may be omitted, examples: &quot;l'après-midi&quot; and  
 *  &quot;l'apres-midi&quot;, &quot;end-of-day&quot;, &quot;août&quot;, 
 *  &quot;aout&quot;<br />
 *  Names of months and day of week are also understood in Portuguese and
 *  Dutch.<br /> 
 *  <br />
 *  <br />
 *  This method is equivalent to 
 *  {@link #parse(CharSequence, boolean) parse(time, false)}.<br />
 *  <br />
 *  To do: Try to understand the non human readable ISO 8601 formats without
 *  most field separators like 20991231T235959.9942 .<br />
 *  <br />
 *  Hint: Executing this class as application by <br /> &nbsp; &nbsp;
 *     java de.frame4j.time.TimeHelper<br />
 *  lists all understood keywords and names; see {@link #main(String[])}.<br />
 *  Doing so with arguments tries to parse those as time.<br />
 *  <br />     
 *  <br />
 *  @param   time  the string to be interpreted as a indication of time
 *  @throws  IllegalArgumentException if time can't be parsed.
 *  @return  the absolute milliseconds since 1.1.1970 00:00:00 UTC.
 *  @see     de.frame4j.util.Action
 *  @see     de.frame4j.text.TextHelper
 */
   public static long parse(final CharSequence time)
                                          throws IllegalArgumentException { 
      return parse(time, false);
   } // parse(CharSequence)

/** Get / parse a time from String representation. <br />
 *  <br />
 *  This method is equivalent to {@link #parse(CharSequence)}
 *  with the extra possibility to specify the end of the day (near midnight)
 *  as default if no time of day is given.<br />
 *  So with this method you can choose the default time of day either as  
 *  00:00:00.0 (endDay false) or 23:59:59.999 (endDay true).<br />
 *  <br />
 *  Non regarding a leap second, the intervals &quot;beginDay..endDay&quot; of
 *  two consecutive days adjoin without gap.<br />
 *  <br />
 *  Hint: Having a keyword Tagesende, end-of-day oder fin-du-jour in the 
 *  parameter String would have the same effect as endDay == true without 
 *  giving time of day time.<br />
 *  <br />
 *  @param   endDay true: no time of day in <code>time</code> 
 *           defaults to 23:59:59.999
 *  @param   time  the string to be interpreted as a indication of time
 *  @throws  IllegalArgumentException  if time can't be parsed.
 *  @return  the absolute milliseconds since 1.1.1970 00:00:00 UTC.
 *  @see Action#selectedBy(CharSequence, boolean)
 */
   public static long parse(CharSequence time, boolean endDay)
                                         throws IllegalArgumentException {
      String s = TextHelper.trimUq(time, null);
      if (s == null)
         throw new IllegalArgumentException("null is no date");
      s = TextHelper.unComment(s, '(', ')', true, " ").trim(); // .toLowerCase();
      int limit = s.length(); 
      if (limit < 3)
         throw new IllegalArgumentException(s + " too short");
      int year = Integer.MIN_VALUE;
      int mon  = -1;
      int mday = -1;
      int hour = -1;
      int min  = -1;
      int sec  = -1;
      int msec = -1;
      
      int wDay = -1;
      int dOf  = Integer.MIN_VALUE;  // direct day set
      
      int  tzoffset = Integer.MIN_VALUE;
      char prevc    = 0;  // prefix character
      char c        = 0;  // actual (parsed) characger
      boolean msFollows = false;   /// h:m:s.MS
      int i = 0;          // character index 
      
      ///tem.out.println(" /// parse Test input " + s);

      tts:  while (i < limit) {  // through String
         c = s.charAt(i);
         ++i;
         if (c <= ' ' || c == ','|| c == '_')   { // separator character
            prevc    = 0; // forget the prefix
            msFollows = false;
            continue tts;
         } // separator character
         if (c == '/' || c == ':' || c == '+'  
                      || c == '-'|| c == '.') { // prefix-character
            prevc = c; // keep in mind
            msFollows &= c == '.';
            continue tts;
         } // prefix                  

     //----------   N u m b e r   -----------------------------------
         if ('0' <= c && c <= '9') { // number
            int n = c - '0';
            boolean vorZ = prevc == '+' || prevc == '-';
            int stell = 1;
            boolean zoneSyntax = false; // +hh:mm -hh:mm
            buildNumber:  while (i < limit) {
               c = s.charAt(i);
               if (c == ':') { // check zone syntax
                  if (vorZ && stell == 2) {  // should be
                     zoneSyntax = true;
                     ++i; // ignore yx:.. within number
                     continue  buildNumber; 
                  } // should be
                  break buildNumber; // stop number at :
               } // check zone syntax
               if (c < '0' || c > '9') break buildNumber;
               n = n * 10 + c - '0';
               ++stell;
               if (n > 9999 || stell > 4)
                  throw new IllegalArgumentException(
                              "number too large at " + i + " :"  + s);
               c = ' '; // separator if end of s
               i++;
            } // build number
            if (zoneSyntax && stell != 4) throw new IllegalArgumentException(
                   "illegal syntax for time zone offset at " + i + " :"  + s);

            
            ///tem.out.println(" /// parse Test number " + n + " " + prevc
                      //              + ".." + c);
            
     //----------   Time zone (Number)   ----------------------------
            if (vorZ && ( zoneSyntax      // + or - and  zone +hh:mm
                  || year != Integer.MIN_VALUE)) { // or year already given
               // time zone offset
               boolean illegalValue = false;
               if (n < 18 && stell <3) {
                  n = n * 60; // EG. "GMT-3"
               } else if (stell == 4) {
                  n = n % 100  +   n / 100 * 60; // e.g. "+04:30"
               } else illegalValue = true;
               if (illegalValue || n > 16*60) {
                  throw new IllegalArgumentException(
                      "illegal value for time zone offset " + n + " :"  + s);
               }
              if (prevc == '+') // plus means west hence -offset
                  n = -n;
               if (tzoffset != n  && tzoffset != 0 
                                       && tzoffset != Integer.MIN_VALUE)
                  throw new IllegalArgumentException("contradictory timezones"
                                          + s);
               tzoffset = n;
               ///tem.out.println(" /// parse Test as zone offs " + n );

               prevc    = 0; // forget prefix
               continue tts;
            } // +/- time zone

       //----------   Year (Number)   ----------------------------
            if (vorZ || (n >= 63 && !msFollows)) { // must be year
               if (year != Integer.MIN_VALUE)  
                  throw new IllegalArgumentException("duplicate year: " + s);
               if (vorZ) {
                  if (prevc == '-') n = -n;
               } else { // no sign
                  if (prevc == ':')
                     throw new IllegalArgumentException("illegal char \'" 
                          + prevc + "\' before year: " + s);
                  if (n < 100) { // unsigned 61 .. 99
                     n = n >= 70 ? 1900 + n : 2000 + n;
                  }
               }
               if (c <= ' ' || c == ',' || c == '/' 
                        || c == '-' /* 14.05.2003  */|| i >= limit) {
                  year = n;
                  ///tem.out.println(" /// parse Test as year " + n );

                  if (c == '-') ++i; // forget - after year
               } else  // year >= 70
                  throw new IllegalArgumentException("illegal char \'" 
                        + c + "\' after year: " + s);
               prevc    = 0; // forget 
               continue tts;
            } // must be year


            //----------   Time (Number)   --  h:m:s.ms  --------------
            
            if (c == ':' || prevc == ':' || msFollows) { // h:m:s
               boolean good = sec < 0 || msFollows && n < 1000;
               hms: while (good) {
                  if (msFollows) {
                     if (stell == 1) {
                        n *= 100;
                     } else if (stell == 2) {
                        n *= 10;
                     }   
                     msFollows = false;
                     msec = n; 
                     ///tem.out.println(" /// parse Test as milliseconds " + n );

                     break hms;   
                  } // 
                  if (c == ':' && prevc == ':') { // must be minute
                     good = hour >= 0 && min < 0 && n < 60;
                     min = n;
                     ///tem.out.println(" /// parse Test as minute " + n );

                     break hms;   
                  } // must be minute
                  if (hour < 0) {  // must be hour
                     good = prevc != ':' && n < 24;
                     hour = n;
                     ///tem.out.println(" /// parse Test as hour " + n );

                     break hms;   
                  } // must be hour    
                  if (min < 0) {  // must be minute
                     min = n;
                     ///tem.out.println(" /// parse Test as minute " + n );

                     break hms;   
                  } // must be minute    
                    sec = n;
                    ///tem.out.println(" /// parse Test as second " + n );

                    msFollows = c == '.';
                    break hms;
               } // h:m:s
               if (!good)
                  throw new IllegalArgumentException(
                        "too many or illegal h:m:s : " + s);
               prevc    = 0; // forget the prefix
               continue tts;
            } // h:m:s   

      //----------   m/d/y (Number)   ----------------------------
            
            if (c == '/' || prevc == '/') { // m/d/y or yyyy/m/d
               boolean good =  dOf == Integer.MIN_VALUE && 
                         (mon < 0 || mday < 0 || year == Integer.MIN_VALUE);
               // year > 62 and +-year already set  
               mdy: while (good) {
                  if (mon < 0) {
                     good = n <= 12;
                     mon = n;
                     ///tem.out.println(" /// parse Test as month " + n );

                     break mdy;
                  }
                  if (mday < 0) {
                     good = n <= 31;
                     mday = n; 
                     ///tem.out.println(" /// parse Test as day " + n );

                     break mdy;
                  }
                  year = n + 2000; // year > 62 and +-year already set  
                  ///tem.out.println(" /// parse Test as year 2000 + " + n );

                  break mdy;
               } // m-d-y
               if (!good) throw new IllegalArgumentException(
                                         "too many or illegal m/d/y : " + s);
               prevc    = 0; // forget
               continue tts;
            } // m/d/y or yyyy/m/d   


      //----------   t.m.j (Number)   ----------------------------
            
            if (c == '.' || prevc == '.') { // t.m.j
               boolean good = dOf == Integer.MIN_VALUE && 
                (mon < 0 || mday < 0 || year == Integer.MIN_VALUE);
               tmj: while (good) {
                  if (mday < 0) {
                     good = n <= 31;
                     mday = n; 
                     ///tem.out.println(" /// parse Test as day. " + n );

                     break tmj;
                  }
                  if (mon < 0) {
                     good = n <= 12;
                     mon = n; 
                     ///tem.out.println(" /// parse Test as .month. " + n );

                     break tmj;
                  }
                  year = n + 2000;
                  ///tem.out.println(" /// parse Test as .year 2000 + " + n );

                  break tmj;
               } // mdy
               if (!good)
                  throw new IllegalArgumentException(
                        "too many or illegal d.m.y : " + s);
               prevc    = 0; // forget prefix
               continue tts;
            } // t.m.j   


      //----------   y-m-d (Number)   ----------------------------
            
            if (c == '-' || prevc == '-') { // y-m-d
               boolean good = dOf == Integer.MIN_VALUE && 
                      (mon < 0 || mday < 0 || year == Integer.MIN_VALUE);
               ymd: while (good) {
                  if (year == Integer.MIN_VALUE) {
                     year = n + 2000;
                     ///tem.out.println(" /// parse Test as year-  2000 + " + n );
                     break ymd;
                  }
                  if (mon < 0) {
                     good = n <= 12;
                     mon = n;
                     ///tem.out.println(" /// parse Test as -month- " + n );
                     break ymd;
                  }
                  good = n <= 31;
                  mday = n;
                  ///tem.out.println(" /// parse Test as -day + " + n );
                  /*  if (good && (c == 'T')) {
                     ///tem.out.println(" /// parse Test ign -dayT at " + i );
                     ++i; // ignore trailing T after -dd ISO 8601 
                     continue tts;
                  } */
                  break ymd;
               } // ymd: y-m-d
               if (!good) throw new IllegalArgumentException(
                                       "too many or illegal y-m-d : " + s);
               prevc    = 0; // forget
               if (c == '-') ++i; // ignore extra - behind 
               continue tts;
            } // y-m-d   

       //--- not yet worked on number  --  (here illegal, except) ------
       //  legal cases : Aug. 12 91 11 PM
       //                12 Aug 81 12 AM 
       // Illegal except  day year hour in that sequence, than n > 31 is year
            if (mday < 0 && n < 32) {
               mday = n;
               ///tem.out.println(" /// parse Test lc day  " + n );
               
               if (c == 'T' && year !=Integer.MIN_VALUE 
                                  && mon !=Integer.MIN_VALUE && hour < 0) {
                  prevc = 0;
                  ++i;
                  continue tts; 
               } // ISO 8601 T after full date treated as separator
               

            } else if (year == Integer.MIN_VALUE) {
               year = n + 2000; 
               ///tem.out.println(" /// parse Test as lc year  2000 + " + n );

            } else if  (hour < 0 && n < 13) {
               hour = n;
               ///tem.out.println(" /// parse Test as lc hour " + n );

            }  else {
               throw new IllegalArgumentException("too many numbers : " + s);
            }
            //---------- Not yet worked on character behind number
            // n.b. .:/- are through 
            if (c != ',' && c > ' ' && c != '_') { // illegal ?
              
                throw new IllegalArgumentException("illegal char \'" 
                        + c + "\' after number " + n + " : " + s);
             }  // only , and space

            prevc    = 0; // forget prefix
            continue tts;               
         }   //  end of number interpretation 

   //----------  Keyword   ----------------------------------------
         msFollows = false;
         
         if (c < '@' || c > 252 || c > 'z' && c < 0xC0) { // character start
            throw new IllegalArgumentException("Illegal character \'" + c
                + "\' in : " + s);
         }
         int st = i - 1;
            while (i < limit) {
              c = s.charAt(i);
              if (c < '$' || c == '_' || c == '.' 
                  || c == '+' || c==',' ) break;
              ++i;
              if (c == '-') {
                 if (i == limit) break; // End with  - should be illegal
                 c = s.charAt(i);
                 if (c < '@') {
                    --i;
                    break;
                 }   
                 ++i; // inner - (minus)  OK                    
              }
            } // while
            if (c == '.') ++i; // forget the . after keyword

            String wo;
            Action action;
            if (st == i-1 && s.charAt(st) == 'Z') {
               action = ACTION_TZ0;// single ISO 8601 time zone Z
               wo = "Z";
            } else { 
               wo = s.substring(st, i);
               action = Action.select(TIME_CHOOSE, null, wo, true);
               if (action == null) {
                  throw new IllegalArgumentException("key word \"" + wo
                                                        + "\" not recognised");
               }
            }
            ///tem.out.println(" /// parse Test action " + action);
            int val = action.value;
            actSwitch: switch (action.code) {
             case Action.SMON: 
               if (mon < 0) {
                  mon = val;
                  ///tem.out.println(" /// parse Test month " + val );

               } else
                  throw new IllegalArgumentException("month  " + wo 
                     + "was already set (" + mon + ")"); 
               break actSwitch;
               
             case Action.SWD: 
               if (wDay < 0) {
                  wDay = val;
                  ///tem.out.println(" /// parse Test wDay " + val );

               } else
                  throw new IllegalArgumentException("dayOfWeek  " + wo 
                       + "was already set (" + wDay + ")"); 
               break actSwitch;

             case Action.SDAY:
               if ((dOf != Integer.MIN_VALUE && dOf != val)
                    || mon > 0 || wDay >= 0 || mday >= 0 
                    || year != Integer.MIN_VALUE)
                  throw new IllegalArgumentException(
                    "contradictory dates " + wo);
               dOf = val;
               ///tem.out.println(" /// parse Test rel.day " + val );

               ++year; // Marker Date set
               break actSwitch;
               
             case Action.STZO:
               val = -val;
               if (tzoffset == val) break actSwitch;
               if (tzoffset != Integer.MIN_VALUE)
                  throw new IllegalArgumentException(
                    "contradictory timezones " + wo);
               tzoffset = val;
               ///tem.out.println(" /// parse Test tzOffset " + val );

               break actSwitch;
             
             case Action.SDAT:
               if (year != Integer.MIN_VALUE || mon >= 10 || mday > 0
                       || wDay >= 0 
                         || dOf != Integer.MIN_VALUE || hour >= 0)
                  throw new IllegalArgumentException(
                  "contradictory date " + wo);
               ///tem.out.println(" /// parse Test absTime now + " + val );
               
               if (val == 0)
                  return SynClock.sys.millis(); 
                break actSwitch;
              
              case Action.STOD:
                if (val == 1) { // AM
                   if (hour > 12 || hour < 1)
                     throw new IllegalArgumentException("AM illegal for " 
                        + hour);
                    else if (hour == 12)
                       hour = 0;
                } else if (val == 3) { // PM
                   if (hour > 12 || hour < 1)
                      throw new IllegalArgumentException("PM illegal for " 
                           + hour);
                   else if (hour < 12) {
                      hour += 12;
                      ///tem.out.println(" /// parse Test hour  + 12 by pm");
                   }
                } else { //  if (val == 2 || val = 0 || val = 4)// noon
                   if (hour != -1) throw new IllegalArgumentException(
                                                "contradictory times " + wo);
                   if (val == 4) { // endOfDay
                       endDay = true;
                       hour = -2;
                       ///tem.out.println(" /// parse Test end of day");

                       break actSwitch;          
                   }
                   hour = min = sec = 0;
                   if (val == 2) {// noon
                      hour = 12;
                      ///tem.out.println(" /// parse Test noon");
                   }    else {
                      ///tem.out.println(" /// parse Test begin of day");
                   }
                }
                break actSwitch;
                  
            } // actSwitch
            prevc = 0;
         } // tts:  while through the String

         if (year == Integer.MIN_VALUE || mon < 0 || mday < 0) { // no date
            if (wDay < 0 && dOf == Integer.MIN_VALUE) {
               if (hour < 0 && min < 0) // also no time
                  throw new IllegalArgumentException("m d y not set : " + s);
               dOf = 0; // today // changed at Revision 10 
            }
            LocalDateTime now = LocalDateTime.now(); // just read the clock
            mday = now.getDayOfMonth();
            mon = now.getMonthValue();
            year = now.getYear();
            if (wDay >= 0) {
                dOf = wDay - now.getDayOfWeek().getValue(); // Sunday 7 | 0 OK
                if (dOf >= 0) dOf -= 7;
            }     
            mday += dOf;
         }
         if (hour < 0) { // no time of day
            if (endDay){
               hour = 23;
               min = sec = 59;
               msec = 999;
            } else {
               hour = min = sec = msec = 0;
            }
         } else {
            if (sec  < 0)  sec   = 0;
            if (min  < 0)  min   = 0;
            if (msec < 0)  msec  = endDay ? 999 : 0;
         }
       
         if (tzoffset == Integer.MIN_VALUE) { // no zone was indicated
            // use the thread local helper  to get daylight auto 
            ZonedDateTime ttmp = ZonedDateTime.of(year, mon, mday, hour, min,
                                                     sec, 0, AClock.sysZone);
            return ttmp.toEpochSecond() * 1000 + msec;
         } //  no zone was indicated

         int daysSince010368 = daysSince1Mrz68(mday, mon, year);
         min += tzoffset; 
         return (daysSince010368 - 671) * ComVar.ONE_DAY 
                     + hour * ComVar.ONE_HOUR +
                     min * ComVar.ONE_MINUTE + sec * 1000L + msec; 
   } // parse(CharSequence, boolean)

//------------------------------------------------------------------------
   
/** Parsing a duration (or period). <br />
 *  <br />
 *  Syntax: [white space] number [white space] [unit] [white space]<br />
 *  Or: [white space]  keyword [white space] <br />
 *  <br />
 *  Unit: ms (default), s, m, h, d, w<br />
 *  for  milliseconds (default) seconds, minutes, hours, days or weeks.<br />
 *  Keyword: &quot;sekündlich&quot;, &quot;secondly&quot;, 
 *  &quot;minütlich&quot;, &quot;minutely&quot;, &quot;stündlich&quot;, 
 *  &quot;hourly&quot;, &quot;horaire&quot;, &quot;ogni-ora&quot;, 
 *  &quot;por-hora&quot;, &quot; cada-hora&quot;, &quot;täglich&quot;,
 *  &quot;dayly&quot;, &quot;quotidien&quot;, &quot;giornaliero&quot;,
 *  &quot;por-día&quot;, &quot;por-dia&quot;, &quot;wöchendlich&quot;,
 *  &quot;weekly&quot;, &quot;hebdomadaire&quot;, &quot;settimanale&quot;,
 *  &quot;semanal&quot;.<br />
 *  <br />
 *  Number: Defined by 
 *      {@link Long}.{@link Long#decode(java.lang.String) decode()}.<br />
 *  <br /> 
 *  Hint: months and years don't give a unique duration in the sense 
 *  of a constant frequency.<br />
 *  <br />
 *  @param rate the duration or rate
 *  @throws IllegalArgumentException if  syntax problems.
 *  @return the duration in ms <br />
 *  Hint: This is in the sense of {@link java.time.Duration}. Handling in the 
 *  sense of {@link java.time.Period} may require DST and leap second 
 *  considerations by the user.
 */   
   public static long parseDuration(final CharSequence rate) 
                                         throws IllegalArgumentException {
      String rS = TextHelper.trimUq(rate, null);
      if (rS == null) throw new IllegalArgumentException("empty duration");
      char cL =  rS.charAt(0);
      if (cL >= 'A') { // may be keyword
         final Action action = Action.select(RATE_CHOOSE, null, rS, true);
         final int actV = (action == null || action.code != Action.SRATE) 
         ? 9999 : action.value;
         actSwitch: switch (actV) {
            case 1: return  1000L;
            case 2: return  60000L;
            case 3: return  ComVar.ONE_HOUR;
            case 4: return  ComVar.ONE_DAY;
            case 5: return  ComVar.ONE_WEEK;
            default: throw new 
                     IllegalArgumentException("illegal duration key: " + rS);
         } // switch
      } // may be keyword
      final int len = rS.length();
      long mul = 1L;
      int trimL = 0; 
      // long set   = defV;
      getUnit: if (len >= 2) {  // unit
         cL = rS.charAt(len-1);
         final char cbL = rS.charAt(len-2);
         if (cL == 's') { // sec or ms
            if (cbL == 'm') {
               trimL = 2;
               break getUnit;
            }
            mul = 1000;
            trimL = 1;
            break getUnit;
         }  // sec or ms
         if (cL == 'm') { // minute
            mul = ComVar.ONE_MINUTE;
            trimL = 1;
            break getUnit;
         }  // minute
         if (cL == 'h') { // hour
            mul = ComVar.ONE_HOUR;
            trimL = 1;
            break getUnit;
         }  // hour
         if (cL == 'd') { // days
            mul = ComVar.ONE_DAY;
            trimL = 1;
            break getUnit;
         }  // days
         if (cL == 'w') { // week
            mul = ComVar.ONE_WEEK;
            trimL = 1;
            break getUnit;
         }  // week
      } // unit
      if (trimL > 0) {
         rS = TextHelper.trimUq(rS.substring(0, len-trimL), null);
      }
      try {
         long set = Long.decode(rS).longValue();
         return set * mul;
      } catch (NumberFormatException ne) {
         throw new IllegalArgumentException("illegal number format: " + rS);        
      }
   } // parseDuration(CharSequence   

   
/**  Format a duration or rate. <br />
  *  <br />
  *  Returned syntax/form is: 2w3d5h12m30,123s  or 987ms<br />
  *  Not applicable parts are omitted; a negative parameter will get a 
  *  leading - .<br />
  *  <br />
  *  Hint: The form delivered by this method
  *  {@link #appendDuration(StringBuilder, long)} is not understood by the
  *  parsing method {@link #parseDuration(CharSequence)}.<br />
  *  <br />
  *  @param bastel the StringBuilder to append to. If null, it will be 
  *                          generated  with starting capacity 56. 
  *  @param dur duration or rate to format in ms
  *  @return bastel
   */
   public static StringBuilder appendDuration(StringBuilder bastel, long dur){
      if (bastel == null) bastel = new StringBuilder(56);
      if (dur < 0L) {
         bastel.append('-');
         dur = -dur;
      }
      int d;
      if (dur < 60000) { // less than a minute (simple cases directly)
         d = (int) dur;        
         if (d < 1000) {
            return bastel.append(d).append("ms");    
         }
         return bastel.append(d/1000).append(',').append(d%1000).append('s');
      } //  less than a minute (simple cases directly)
      
      final long weeks = dur / ComVar.ONE_WEEK;
      if (weeks != 0) {
         bastel.append(weeks).append('w');
         dur %=  ComVar.ONE_WEEK;
      }
      d = (int) dur;
      int w = d / ComVar.D;
      if (w != 0) {
         bastel.append(w).append('d');
         d %= ComVar.D;
      }

      w = d / ComVar.H;
      if (w != 0) {
         bastel.append(w).append('h');
         d %= ComVar.H;
      }

      w = d / ComVar.M;
      if (w != 0) {
         bastel.append(w).append('m');
         d %= ComVar.M;
      }
      
      if (d == 0) return bastel;
      if (d < 1000) {
         return bastel.append(d).append("ms");    
      }
      return bastel.append(d/1000).append(',').append(d%1000).append('s');
   } // appendDuration
  
   
//--------------    Time and Date related Actions  -------------------------
   
/** Action list for duration or rates. <br />
 *  <br />
 *  Languages: <ul>
 *    <li>English, German,</li>
 *    <li>just partly Italian, French.</li>
 * </ul>
 * @see #parseDuration(CharSequence)
 */
  final public static Action[] RATE_CHOOSE = {
     new Action(Action.SRATE, 1, new String[]{"sekündlich", "secondly"} ), 
     new Action(Action.SRATE, 2, new String[]{"minütlich", "minutely"} ), 
     new Action(Action.SRATE, 3, new String[]{"stündlich", "hourly", 
                           "horaire", "ogni-ora", "por-hora", " cada-hora"} ), 
     new Action(Action.SRATE, 4, new String[]{"täglich", "dayly", "quotidien", 
                                "giornaliero",   "por-día", "por-dia"} ), 
     new Action(Action.SRATE, 5, new String[]{"wöchendlich", "weekly",
                                "hebdomadaire", "settimanale", "semanal"} ) 
  };

/** Action for time zone offset 0 , UTC. <br /> 
 *  <br />
 *  Action to set time zone (raw) offset to 0; like in UTC.
 *  <br />
 */
  public final static Action ACTION_TZ0 = new Action(5,        0, 
                               new String[]{"GMT", "UT", "UTC", "WET"  });
      
      
/** Action list for date and time of day. <br />
 *  <br />
 *  @see #parse(CharSequence)
 *  @see #main(String[])
 */
  final public static Action[] TIME_CHOOSE = {
    new Action(6, 1, new String[]{"AM", "vormittags", "matinée", "matinee", 
                            "a.m.", "antimeridiano", "del-mattino",
                            "de-la-mañana", "de-la-manana"} ), 
    new Action(6, 3, new String[]{"PM", "nachmittags", 
                    "l'après-midi", "l'apres-midi", "del-pomeriggio",
            "de-la-tarde", "después-del-mediodía", "despues-del-mediodia"} ), 
    new Action(6, 4, new String[]{"Tagesende", "end-of-day", "fin-du-jour"}), 
    new Action(6, 2, new String[]{"Mittag", "noon", "midi", 
                                "mezzogiorno", "mediodía", "mediodia"}), 
    new Action(6, 0, new String[]{"Mitternacht", "midnight", "minuit",
                                               "mezzanotte", "medianoche"}), 

    new Action(7, 0, new String[]{"heute", "today", "aujourd'hui", 
                          "oggigiorno" /*"oggi"*/, "hoy-día",  "hoy-dia" }), 
    new Action(7,+1, new String[]{"morgen", "tomorrow", "demain",
                                             "domani", "mañana", "manana"}), 
    new Action(7,-1, new String[]{"gestern", "yesterday", "hier", 
                                                          "ieri", "ayer"}), 
    new Action(7,-2, new String[]{"vorgestern", "beforeyesterday",
                                                "avant-hier", "anteayer"}), 
    new Action(7,-3, new String[]{"vorvorgestern", "three-days-ago"}), 

    new Action(8, 0, new String[]{"jetzt", "now", "maintenant",      // [10]
                                                      "adesso", "ahora"}), 


    //------------    day of week  ------------------------------
    // it "domenica","lunedì",  "martedì",  "mercoledì",  "giovedì",
    //                                             "venerdì",  "sabato"
    // sp "domingo",  "lunes",  "martes",  "miércoles",  "jueves",
     //                                                  "viernes",  "sábado"
    // nl "zondag",  "maandag",  "dinsdag",  "woensdag",  "donderdag",
    //                                                 "vrijdag",  "zaterdag"
    // pt "Domingo",  "Segunda-feira",  "Terça-feira",  "Quarta-feira", 
    //                              "Quinta-feira",  "Sexta-feira",  "Sábado"
    
    
    new Action(3, 0, new String[]{"Sonntag", "Sunday", "dimanche", 
                                          "domenica", "domingo", "zondag" }), 
    new Action(3, 1, new String[]{"Montag", "Monday", "lundi",
                            "lunedì", "lunes", "maandag", "Segunda-feira" }), 
    new Action(3, 2, new String[]{"Dienstag", "Tuesday", "mardi",
                            "martedì", "martes", "dinsdag", "Terça-feira",
                            "Terca-feira"}), 
    new Action(3, 3, new String[]{"Mittwoch", "Wednesday", "mercredi",
                            "mercoledì", "mercoledi", "miércoles", "miercoles",
                            "woensdag", "Quarta-feira"}), 
    new Action(3, 4, new String[]{"Donnerstag", "Thursday", "jeudi",
                            "giovedì", "jueves",  "donderdag",
                            "Quinta-feira"}), 
    new Action(3, 5, new String[]{"Freitag", "Friday", "vendredi",
                            "venerdì", "viernes", "vrijdag", "Sexta-feira" }), 
    new Action(3, 6, new String[]{"Samstag", "Saturday", "samedi",
                            "sabato", "sábado", "sabado", "zaterdag"}), // [17]

    //------------    Month    ------------------------------
    //  it "gennaio",  "febbraio",  "marzo",  "aprile",  "maggio", 
    //              "giugno",  "luglio",  "agosto",  "settembre", 
    //               "ottobre",  "novembre",  "dicembre"
    //  sp "enero",  "febrero",  "marzo",  "abril",  "mayo", 
    //              "junio",   "julio",  "agosto",  "septiembre",
    //                  "octubre",  "noviembre",  "diciembre"
    //  nl "januari",  "februari",  "maart",  "april",  "mei",
    //             "juni",  "juli",  "augustus",  "september",
    //             "oktober",  "november",  "december"
    //  pt  "Janeiro",  "Fevereiro",  "Março",  "Abril",  "Maio",
    //              "Junho",  "Julho",  "Agosto",  "Setembro",
    //             "Outubro",  "Novembro",  "Dezembro"

    new Action(4, 1, new String[]{"Januar", "January", "janvier",
                              "gennaio", "enero", "januari", "Janeiro"}), 
    new Action(4, 2, new String[]{"Februar", "February", "février",
              "fevrier", "febbraio",  "febrero", "februari", "Fevereiro"}), 
    new Action(4, 3, new String[]{"März", "Maerz", "Mrz", "March", "mars",
                              "marzo", "maart", "mrt",  "Março", "Marco"}), 
    new Action(4, 4, new String[]{"April", "avril", "april",
                               "aprile", "abril"}), 
    new Action(4, 5, new String[]{"Mai", "May", "mai",  "maggio", "mayo",
                              "mei", "Maio"}), 
    new Action(4, 6, new String[]{"Juni", "June", "juin", "giugno", "junio",
                              "Junho"}), 
    new Action(4, 7, new String[]{"Juli", "July", "juillet", "luglio",
                              "julio", "Julho"  }), 
    new Action(4, 8, new String[]{"August", "août", "aout", "agosto",
                              "augustus" }), 
    new Action(4, 9, new String[]{"September", "septembre", "settembre",
                               "septiembre", "Setembro"}), 
    new Action(4,10, new String[]{"Oktober", "October", "octobre", "ottobre",
                               "octubre", "Outubro"}), 
    new Action(4,11, new String[]{"November", "novembre", "noviembre",
                               "Novembro"  }), 
    new Action(4,12, new String[]{"Dezember", "December", "décembre",
                               "decembre", "dicembre", "diciembre",
                               "Dezembro" }),  // [29]

// Time zone offset
    ACTION_TZ0, // "GMT", "UT", "UTC", "WET" 
    new Action(5,      +60, new String[]{"CET", "MEZ", "BST", "WAT", // West Afrika
                                            "IST" ,"WEST" }),  // [31]
    new Action(5,  +2 * 60, new String[]{"MESZ", "CEST", "EET", "CAT", "SAST"  }), // C Eur C Afrique, South Africa Standard Time 
    new Action(5,  +3 * 60, new String[]{"MSK", "EEST", "CEMT", "EAT"}),  //,,, E Africa 
    new Action(5,  +3 * 60 + 30, new String[]{"IRST" }), // Iran Standard Time
    new Action(5,  +4 * 60, new String[]{"MSD","AZT"}), // Moskau Sommer, Azerbaijan Time

    new Action(5, -(2*60 + 30), new String[]{"NDT" }), // Newfundland Sommer 
    new Action(5, -(3*60 + 30), new String[]{"NST" }), // Newfundland Std 
    
    // AST is also   Arabia Standard Time  +03:00
    new Action(5,  -4 * 60, new String[]{"AST", "EDT", "BOT", "CLT",  // Atlanic Std USA, Bolivia, Chile
                                  "FKT", "GYT"}), // Falkland, Guyana Time, 
     // EST is not unique E US + (New South Wales) +
    new Action(5,  -5 * 60, new String[]{"EST", "CDT", "ACT", // Eastern Std USA, Acre Time, 
                            "COT", "PET"}), //Columbia, Peru
     // CST is not unique China Standard Time, Central Standard Time (South Australia)
    new Action(5,  -6 * 60, new String[]{"CST", "MDT", "GALT"}), // Central Std USA , Galapagos Time
    new Action(5,  -7 * 60, new String[]{"MST", "PDT", }), // Mountain Std US 
    
    // PST is not unique: PST   -480   -08:00   // Pitcairn Standard Time the same offset
    new Action(5,  -8 * 60, new String[]{"PST", "AKDT"}), // Pacific Std USA 
    new Action(5,  -9 * 60, new String[]{"AKST",}), // Alaska Standard USA 
    new Action(5, -10 * 60, new String[]{"HST", "TAHT", "HAST"}), // Hawaii / Honolulu Std USA, Tahiti
    
    // since january 2009
    new Action(5,  -11* 60 ,     new String[]{"SST"}), // Samoa Standard Time
    new Action(5,  -4* 60 +30 ,  new String[]{"VET"}), // Venezuela
    new Action(5,  -3* 60 ,     new String[]{"ART", "BRT", "GFT", // Argentine Time, Brasilia,   French Guiana Time
              "NFT", "WGT", "SRT", "UYT"}), // Newfoundland Standard Time, West Greenland,  Suriname Time, Uruguay Time
    new Action(5,  -2* 60 ,     new String[]{"FNT"}), // Fernando de Noronha Time
    new Action(5,  -1* 60 ,     new String[]{"AZOT", "CVT", "EGT"}), // Azores Time, Cape Verde Time, Eastern Greenland Time 

    
    new Action(5,  +4 * 60 + 30, new String[]{"AFT", "GET",  // Afganistan, Georgia Time,
                            "MUT", "SCT"}),   // Mauritius, Seychelles Time
    new Action(5,  +5 * 60 ,     new String[]{"AQTT", "ORAT", "TMT"}),  // Aqtau Time, Oral, Turkmenistan Time
    new Action(5,  +5 * 60 + 45, new String[]{"NPT",}),  // Nepal Time
    new Action(5,  +6 * 60 ,     new String[]{"ALMT", "BTT", "CCT",  // Alma-Ata Time, Bhutan, Cocos Islands
          "IOT", "KGT", "NOVT",// Indian Ocean Territory Time,  Kirgizstan Time,  Novosibirsk
          "OMST", "BDT"}), //  Omsk, Bangladesh Time
    
    new Action(5,  +7 * 60 ,     new String[]{"ICT", "WIT", "CXT"}), // Indochina Time, West Indonesia,  Christmas Island Time
    new Action(5,  +8 * 60 ,     new String[]{"IRKT", "ULAT", "HKT", // Irkutsk Time,  Ulaanbaatar Time, Hongkong,
                            "MYT", "SGT"}), //  Malaysia Time,  Singapore Time
    new Action(5,  +8 * 60 + 45, new String[]{"CWST"}), // Central Western Standard Time (Australia)

    
    new Action(5,  +9 * 60 ,     new String[]{"CHOT", "EIT", // Choibalsan Time, East Indonesia Time, 
                            "JST", "KST", "PWT"}),  // Korea, Japan, Palau Time
    new Action(5, +10 * 60 ,     new String[]{"ChsT", "PGT", // Chamorro Standard Time,  Papua New Guinea Time
                            "SAKT"}),  // Sakhalin Time
    new Action(5, +11 * 60 ,     new String[]{"KOST",  // Kosrae Time
                                              "NCT", "SBT"}), // New Caledonia Time, Solomon Is. Time
    new Action(5, +12 * 60     , new String[]{"FJT", "GILT", "NRT", // Fiji, Gilbert Is., Narau
                            "NZST", "TVT"}),  // New Zealand Standard Time, Tuvalu Time
                            
    new Action(5, +12 * 60 + 45, new String[]{"CHAST", "MHT"}), // Chatham Standard Time, Marshall Islands Time
   
  };


//--------------    Time and Date Test and Demo    -------------------------

/** Display some info about class, time zones, explore the parser etc. <br />
 *  <br /> 
 *  If no arguments are given, this application displays info about time zones
 *  and the keywords defined or understood by this class' parsers.<br />
 *  <br />
 *  Any given arguments are used to demonstrate the date/time parser.<br />
 *  <br />
 *  Run by: java de.frame4j.time.TimeHelper [time] 
 *  @param args date / time string to parse and redisplay
 *  @see #parse(CharSequence)
 */
   public static void main(String[] args){

      new App(args){
         @Override protected int doIt(){
            ZonedDateTime time  = ZonedDateTime.now();
            ZonedDateTime utcTime = null;
            boolean argShown = false;
            for (String s : args) {
               String u = TextHelper.trimUq(s, null);
               if (u == null) continue;

               log.println("\n\n---   time to parse  : \"" + u + "\"");
               boolean loop = u.equals("loop");

               long ti = 0;
               try { do {
                  if (loop) {
                     log.println("\n--------------------(^C to stop)----");
                     AppHelper.sleep(831);
                     time  = ZonedDateTime.now();
                     log.println("toSt.(d.m.Y H:i:s.u) : " 
                                             + format("d.m.Y H:i:s.u", time));
                  } else {
                     ti = parse(u);
                     Instant inst = Instant.ofEpochSecond(ti / 1000,
                                                         ti % 1000 * 1000000); 
                     time = ZonedDateTime.ofInstant(inst, CET_ZONE);
                  }
                  log.println("format ( )           : " + time);
                  log.println("ms since 1.1.1970    : " + ti
                                     +  ", days since 1.3.1968 : "
                                     + daysSince1Mrz68(time.getDayOfMonth(), 
                                 time.getMonth().getValue(), time.getYear()));

                  log.println("format (U)          : " + format("U", time));

                  log.println("format (y-n-d g:i a): " 
                                          + format("y-n-d g:i a", time));
                  log.println("fot(j. F Y H:i:s e I): "
                                        + format("j. F Y H:i:s e I", time));
                  log.println("format  (c   W)eek   : " 
                                          + format("c   W", time));
                  utcTime = ZonedDateTime.ofInstant(time.toInstant(),
                                                                  UTC_ZONE);
                  log.println("format  (c  UTC_ZONE : " 
                                          + format("c        W", utcTime));
                  argShown = true;
               } while (loop); } catch (Exception ex) {
                  ex.printStackTrace(log);
               }
            } // for args

            if (argShown) return 0;

            ///// copied from Test 
            Set<String> allZones = ZoneId.getAvailableZoneIds();
            LocalDateTime dt = LocalDateTime.now();
            Instant instNow = Instant.now();

            // Create a List using the set of zones and sort it.
            List<String> zoneList = new ArrayList<String>(allZones);
            Collections.sort(zoneList);

            log.println();
            ZoneId zone = ZoneId.systemDefault();
            log.printf("We're in zone%22s %n",  zone);

            for (String s : zoneList) {
               zone = ZoneId.of(s);
               ZoneId nrmZ = zone.normalized();
               ZoneRules rules = zone.getRules();

               ZonedDateTime zdt = dt.atZone(zone);
               ZoneOffset offset = zdt.getOffset();
               int secondsOfHour = offset.getTotalSeconds() % (60 * 60);
               String txt = String.format("%35s %10s ", zone, offset);
               if (secondsOfHour != 0) txt += "non hour offset";
               else if (! s.equals(nrmZ.toString())) txt += nrmZ;
               if (rules.isFixedOffset()) txt += "   fixed offset";
               else if(rules.isDaylightSavings(instNow)) txt += "   DST";
               log.println(txt);
            }


            log.println("\n\n Localised Time zone:\n");
            DateFormatSymbols dFS = null;
            Locale locale;
            String[][] zoneStrings = null;
            try {
               locale = new Locale(getLanguage());
               dFS    = new DateFormatSymbols(locale);
               zoneStrings = dFS.getZoneStrings();
               for (String[] zonI : zoneStrings ) {
                  if (zonI == null || zonI.length < 5) continue;
                  log.println(Arrays.toString(zonI ));
               }
            } catch (Exception e) {
               log.println("not available " + e.getMessage());
            } // ignore


            log.println("\n\n Time choose keywords:\n");
            for(Action a: TIME_CHOOSE) {
               log.println(a);
            }

            log.println("\n\n Duration or rate choose keywords:\n");
            for(Action a: RATE_CHOOSE) {
               log.println(a);
            }
            return 1;  
         } // doIt
      };
   } // main(String[])
  
} // class TimeHelper (24.06.2003, 14.02.2006, 15.01.2009, 10.08.2016)
