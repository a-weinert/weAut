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
package de.frame4j.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URLClassLoader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import de.frame4j.text.TextHelper;
import de.frame4j.time.SynClock;
import de.frame4j.time.TimeHelper;


/** <b>Helper for applications</b>.  <br />
 *  <br />
 *  This  abstract class contains some (static) helpers for applications.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 1999 - 2006, 2009 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see TextHelper
 */
 //  so far   V00.01 (19.06.2000 10:26) : DE., moves to TextHelper
 //           V00.05 (27.12.2001 14:24) : universal toOutln 
 //           V00.06 (27.12.2001 14:24) : setProxy 
 //           V02.25 (15.10.2004 11:00) : messageFormat
 //           V02.30 (21.01.2005 18:07) : getAsResourceStream()
 //           V02.32 (15.02.2006 17:00) : Timer support
 //           V.o58+ (28.01.2009 13:21) : ported
 //           V.125+ (14.04.2ß15 11:46) : OSexec out (remains in AdmHelper)
 //           V.134+ (02.11.2015) : Class<?>

@MinDoc(
   copyright = "Copyright  1999 - 2006, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use the basic services",  
   purpose = "provide helper methods for applications"
) public abstract class AppHelper  {

/** No objects */
   private AppHelper(){}

/** Delay programme execution by just waiting. <br />
 *  <br />
 *  The effect is that of the (used) method
 *  {@link Thread}.{@link Thread#sleep(long) sleep(long)}.<br />
 *  <br />
 *  As distinguished from {@link Thread}'s method this 
 *  {@link #sleep(long) sleep()} throws no Exception. An early return due to
 *  an interrupt of the wait/sleep is signalled by returning false.<br />
 *  <br />
 *  The time to wait is specified as milliseconds, the accuracy being 
 *  runtime dependent.<br />
 *  <br />
 *  @param  millis (&gt;0 !) wait time in ms   
 *  @return true after uninterrupted expiration of the time
 *  @see ComVar#ONE_MINUTE
 *  @see ComVar#ONE_HOUR
 *  @see ComVar#ONE_DAY
 */
   static final public boolean sleep(final long millis){
      if (millis <= 0) return true;
      try {
         Thread.sleep(millis);
         return true;
      } catch (Exception e) {
         return false;
      }
   } // sleep(long) 

//---------------------------------------------------------------------------

/** String[] (array of String) as Parameter. <br />
 *  <br />
 *  Array of {@link Class}, length 1, just containing the class of
 *  String[]. Used to denote just one Parameter of type String[] for
 *  reflection.<br />
 *  <br />
 *  Do not modify! (hence package access).<br />
 */
   static final Class<?>[] SAPC = new Class[]{String[].class};

/** Empty Class array. <br /> */
   public static final Class<?>[] ECL = new Class[0];

/** Empty Object array. <br /> */
   public static final Object[] EOB = new Object[0];
    
/** URL ClasLoader. <br /> */
   static URLClassLoader urlCL;
  
//---------------------------------------------------------------------------

/** Output a non empty String. <br />
 *  <br />
 *  The output of {@code s} by out.println() will be made only,<br /> 
 *  if out is either a {@link java.io.PrintWriter} or a
 *  {@link java.io.PrintStream} and<br />
 *  if  {@code s} is not empty.<br />
 *  <br />
 *  This method covers the cases where a (log) output should be feasible
 *  to both a character and a byte stream. The latter being (still) often
 *  used and supplied for log output by legacy code.<br />
 *  But it is a nuisance in cases where one has to handle a
 *  {@link java.io.PrintStream} not being able to use a 
 *  {@link java.io.PrintWriter} (the better choice) in the same 
 *  implementation.<br />
 *  <br />
 *  As there's no interface for describing the common capabilities of 
 *  {@link java.io.PrintStream} and  {@link java.io.PrintWriter} just
 *  {@link java.lang.Object java.lang.Object} is the single common type.
 *  The intricateness induced by using it as such is buried in this 
 *  convenience method.<br />
 *  <br />
 *  @param out  PrintStream or PrintWriter. If null or  of other types 
 *              nothing happens at all.
 *  @param  s   Sequence to be output. If empty (or null) nothing happens.
 */
   static public void toOutln(final Object out, final CharSequence s){
      if (s == null ||  s.length() == 0) return;
      if (out instanceof PrintStream) {
         ((PrintStream)out).println(s.toString());
         return;
      }
      if (out instanceof PrintWriter) {
         ((PrintWriter)out).println(s.toString());
      }
   } // toOutln(Object, ..)


//-----------------------------------------------------------------------

/** Set the system properties governing the use of a (LAN) proxy. <br />
 *  <br />
 *  This method sets the (JVM) system variables for proxy behaviour.<br />
 *  <br />
 *  @param on false: use no proxy; both other parameters are taken in, even
 *       if so of no relevance.
 *  @param proxyHost Name of the proxy server of this (LAN) network segment
 *                  (often proyx or cache);  null or empty means no change.
 *  @param proxyPort Port of the proxy server of this (LAN) network segment;
 *                  (often 8080); null or empty means no change.
 */
   public static void setProxy(final boolean on,
                    final String proxyHost, final String proxyPort) {
      System.setProperty("proxySet", on ? "true" : "false");
      if (proxyHost != null && proxyHost.isEmpty()) 
         System.setProperty("proxyHost", proxyHost);
      if (proxyPort != null && proxyPort.isEmpty()) 
         System.setProperty("proxyPort", proxyPort);
   } // setProxy(boolean, 2*String)


/** The actual (system) time as text. <br />
 *  <br />
 *  The actual platform time (supplied in the framework as common
 *  {@link de.frame4j.time.SynClock#sys Time stamp} is 
 *  {@link de.frame4j.time.AClock#update() updated} to system clock and then 
 *  formatted according to {@link AppLangMap}.{@link
 *  AppLangMap#valueUL(CharSequence) valueUL(&quot;wedaclock&quot;)}.<br />
 *  <br />
 *  In the case of user language German the format is 
 *  &quot;D, d.m.Y, H:i:s&quot;; in the case of English it would be 
 *  &quot;w, M d Y, H:i:s&quot;. That would give  
 *  &quot;Do,&nbsp;05.02.2009,&nbsp;13:45:19&quot; or
 *  &quot;Th,&nbsp;Feb.&nbsp;05&nbsp;2009,&nbsp;13:45:19&quot; 
 *  respectively.<br />
 */
   public static String getActTime(){
      long time = SynClock.sys.setActTimeMs();
      return TimeHelper.format(AppLangMap.valueUL("wedaclock"), time);
      //  "Mi, 28.01.2009, 12:13:41");
   } // getActTime()

//----------------------  Detailedness of reports    -------------------   


/** Detailedness of reports or logging. <br />
 *  <br />
 *  One common way to designate the detailedness or frequency of reports or
 *  log entries is by the assignment of integer values, here in the range of
 *  0 to 1000, called levels.<br />
 *  Levels like this have a double meaning:<br />
 *  At one hand they are assigned to an event or the report about, whereby 
 *  higher values mean higher severity (no meaning to catastrophic) of the
 *  event or the importance of the report or log.<br />
 *  On the other hand the level serves as a threshold in the sense that 
 *  reports of levels lower than threshold are not generated at all or 
 *  filtered out.<br />
 *  <br />
 *  Such &quot;generating threshold&quot; may be assigned to an application, 
 *  but also to every agent, class or object. Very often, only two &mdash;
 *  verbose or not &mdash; or three levels &mdash; silent, normal and 
 *  verbose &mdash; are distinguished, but seldom more than five /as defined
 *  here) make sense.<br />
 *  <br />
 *  <table  border="1" cellspacing="0" cellpadding="3" style="text-align:center;"
    width="593" summary="Verbosity levels" >
 *  <tr style="text-align:center;"><th style="width:100;"> Level </th>
 *  <th style="width:120;"> name </th><th style="width:160;"> description </th>
 *  <th style="width:160;"> logging API name </th></tr>
 *  <tr><td> 300 </td><td> debug </td>
 *         <td>test, debug and commissioning reports</td><td>FINEST</td></tr>
 *  <tr><td> 400 </td><td> test </td>
 *                <td>commissioning and some tests</td><td>FINER</td></tr>
 *  <tr><td> 500 </td><td>verbose</td>
 *        <td>single agent (normal) state changes and events</td><td>FINE</td></tr>
 *  <tr><td> 700 </td><td> normal </td>
 *        <td>configuration / plant state changes and events, 
 *           start up and shut down</td><td>CONFIG</td></tr>
 *  <tr><td> 900 </td><td> error </td>
 *        <td>only errors and failures</td><td>WARNING</td></tr>
 *  <tr><td> 1000 </td><td> silent </td>
 *      <td>no reports, except catastrophic</td><td>SEVERE</td></tr>
 *  </table>
 *  <br style="clear:both;" />
 *  The usual default is 700 (=normal).<br />
 *  <br />
 *  @param verbosity level 300..1000
 *  @return the level's name; see table
 *  @see App#setVerbosity(int)
 *  @see java.util.logging.Level
 */
   public static String getVerbosityAsString(final int verbosity){
      if (verbosity >= SILENT) return "silent";
      if (verbosity >= NORMAL) {
         return verbosity >= ERROR ? "error" : "normal";
      }
      if (verbosity >= TEST) {
         return verbosity >= VERBOSE ? "verbose" : "test";
      }
      return "debug";
   }  // getVerbosityAsString(int)

/** Level values for report detailedness. <br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 */   
   public static // should be made enum ??
     final int SILENT  = 1000, // say almost nothing
               ERROR   = 900,  // only report unusual incidents
               NORMAL  = 700,  // standard report level, normal mode
               VERBOSE = 500,  // verbose reports
               TEST    = 400,  // finer reports, test/simulate mode
               DEBUG   = 300;  // search systematic faults, commissioning mode 
                           
/** Logging API's level by integer detailedness. <br />
 *  <br />
 *  See table at {@link #getVerbosityAsString(int)}.<br />
 *  <br />
 *  @see App#setVerbosity(int)
 */
   public static Level getVerbosityLevel(int verbosity){
      if (verbosity >= SILENT) return Level.SEVERE;
      if (verbosity >= NORMAL) {
         return verbosity >= ERROR ? Level.WARNING : Level.CONFIG;
      }
      if (verbosity >= TEST) {
         return verbosity >= VERBOSE ? Level.FINE : Level.FINER;
      }
      return Level.FINEST;
   } // getVerbosityLevel(int )
   

/** Action list for report level. <br />
 *  <br />
 *  See table at {@link #getVerbosityAsString(int)}.<br />
 *  <br />
 *  Languages: English (partly German).<br />
 *  <br />
 *  @see Action#select(Action[], de.frame4j.util.Action.Filter, CharSequence, boolean)
 */
  final public static Action[] LEVEL_CHOOSE = {
     new Action(Action.SVERBOS, NORMAL,  new String[]{"normal", 
                                      "default", "false", "config", "700"} ), 
     new Action(Action.SVERBOS, SILENT,  new String[]{"silent", "severe", 
                                     "off", "aus", "none", "keine", "1000"} ), 
     new Action(Action.SVERBOS, ERROR, 
                        new String[]{"error", "Fehler", "warning", "900" } ),
     new Action(Action.SVERBOS, VERBOSE, 
                    new String[]{"verbose", "ausführlich", "fine", "500" } ),
     new Action(Action.SVERBOS, TEST, 
                             new String[]{"test", "finer", "true", "400" } ),
     new Action(Action.SVERBOS, DEBUG, 
                             new String[]{"debug", "finest", "300" } )  };

   
/** Determine report level from text. <br />
 *  <br />
 *  The (key) words and numbers in the table at 
 *  {@link #getVerbosityAsString(int)} are recognised.<br />
 *  The match is case insensitive and allows for abbreviations: err orr err., 
 *  e.g., instead off error. If no match is found, not 700 (normal)
 *  is returned.<br />
 *  <br />
 *  <br />
 *  @see #getVerbosityAsString(int)
 *  @see Action#select(Action[], de.frame4j.util.Action.Filter, CharSequence, boolean)
 *  @param verbosity key word or number
 */
   public static int getVerbosity(String verbosity){
      Action act = Action.select(LEVEL_CHOOSE, null, verbosity, true);
      return act == null ? NORMAL : act.value;
   } // setVerbosity(String)
   
/** Designate an input stream from a file or a resource. <br />
 *  <br />
 *  Using the given {@code fileName} it is tried to produce and return 
 *  an input stream  connected or related to the class loader registered
 *  at start in {@link ComVar}.{@link ComVar#FRW_CLLD FRW_CLLD}. Normally
 *  this would be a file contained in the 
 *  (<a href="../package-summary.html#be">Frame4J</a>'s) deployment
 *  .jar-file.<br />
 *  <br />
 *  null is returned if no original class loader 
 *  ({@link ComVar#FRW_CLLD FRW_CLLD}) is known or if no resource named 
 *  {@code fileName} could be determined or found.<br />
 *  <br />
 *  @return an input stream in case of success
 *  @throws SecurityException if the trials to determine the resource were 
 *          denied due to security reasons.
 */ 
   static public InputStream getAsRessourceStream(CharSequence fileName)
                                                   throws SecurityException {
      if (ComVar.FRW_CLLD == null) return null;
      String fName = TextHelper.trimUq(fileName, null);
      if (fName == null) return null;
      fName = fName.replace('\\', '/');
      try {
         return ComVar.FRW_CLLD.getResourceAsStream(fName);
      } catch (Exception e) {
         if (e instanceof SecurityException)
            throw (SecurityException)e; // 12.12.00
      }
      return null;     
   } // getAsRessourceStream(CharSequence)
   

//-------------------------------------------------------------------------
   
/** Fetch one (singleton) Timer. <br />
 *  <br />
 *  One single {@link Timer} is able to handle thousands of 
 *  {@link java.util.TimerTask}s also for multiple {@link Thread}s.<br />
 *  <br />
 *  So there has to be just one (singleton). This methods fetches it
 *  (as daemon).<br />
 *  <br />
 *  Hint: {@link Timer#cancel()} shall never be called for this singleton
 *  Timer. It would destroy it for other users.<br /> 
 */ 
   public static Timer getTimer(){ return TimerContainment.tim; }
   
   private static class TimerContainment { //---------
      final static Timer tim = new Timer("Frame4J common", true);
   } //  class TimerContainment              =========
   
   
/** Schedule a task for relative (delayed) start and repeated execution.<br />
 *  <br />
 *  After a delay the task will be started and periodically repeated 
 *  afterwards. Variations in the scheduling period will be corrected in the
 *  next steps, so that in the long run there is a stable and precise 
 *  frequency. In the example &quot;tomorrow at 03:15:31, and then daily" 
 *  would keep on about 3 hours 15 minutes and 31 seconds every day (except
 *  perhaps for leap seconds?).<br />
 *  <br />
 *  Hint: day light saving switches keep the 24h period, but shift the tasks
 *  starting time of day by one hour. This might give surprising effects
 *  for daily administrative tasks.<br />
 *  <br />
 *  See also 
 *  {@link Timer#scheduleAtFixedRate(java.util.TimerTask, long, long)}.<br />
 *  <br />
 *  @param task   the task to be scheduled
 *  @param delay  very first start delay in ms (&gt;= 0 !)
 *  @param period repeating period in ms (&gt; 0 !)
 *  @throws IllegalArgumentException if parameter values point to the past
 *  @throws IllegalStateException if the task task was scheduled already or if
 *        it or the {@link #getTimer() timer} was stopped
 *  @see #getTimer()      
 */
   public static void scheduleAtFixedRate(final TimerTask task,
                           final long delay, final long period)
                      throws IllegalStateException, IllegalArgumentException {
      if (task == null) return;
      getTimer().scheduleAtFixedRate(task, delay, period);
   } // scheduleAtFixedRate



/** Schedule a task for absolute start and repeated execution. <br />
 *  <br />
 *  This method is mostly equivalent to
 *  {@link #scheduleAtFixedRate(TimerTask, long, long)}. The difference is
 *  {@code start} being an absolute starting time as ms.<br />
 *  <br />
 *  If start is in the past respectively less than about 1.5 s in the future 
 *  multiples of period will be added to bring start between 1.5 s and 1.5 s +
 *  period to the future. This ensures meaningful starting conditions for the
 *  class {@link Timer}). The used start time is the return value.<br />
 *  <brt />
 *  @param task   the task planned to run
 *  @param start  the time for first start (&gt;= year 2009 !)
 *  @param period repeat rate in ms (milliseconds, &gt; 10 !)
 *  @throws IllegalArgumentException falls die Parameter point (to far) into
 *           past
 *  @throws IllegalStateException if the task was planned already or if it or
 *           the Timer has ended already
 *  @throws NullPointerException if  task  is null
 *  @return start  may be + n * period
 *  @see #getTimer()      
 */
   public static long scheduleAbsolute(final TimerTask task, long start, 
                         final long period) throws IllegalStateException,
                             IllegalArgumentException,  NullPointerException {
      if (task == null)
         throw new NullPointerException("No task.");
      if (period < 10)
         throw new IllegalArgumentException("Non-positive period.");
      if (start <= 1230764400000L)  //  2002  1009756800000L) 
         throw new IllegalArgumentException("no start before 2009.");
      final Timer tim = getTimer();
      
      long now = SynClock.sys.setActTimeMs();
      long dif = now - start;
      
      if (dif >= -1450L) { // not in the future
         start += period;
         dif = now - start;
         if (dif >= 0L) {// still in the future
            long  mul =  dif / period;
            if (mul <= 0L) mul = 1L;
            start += period * mul;
         } // still in the future
      } // not in the future
      tim.scheduleAtFixedRate(task, start - now, period);
      return start;
   } // scheduleAbsolute(TimerTask, 2*long)
   
} // class AppHelper (15.10.2003, 21.01.2005, 15.02.2006, 28.01.2009)