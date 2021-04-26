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
package de.frame4j.io;

import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import de.frame4j.util.MinDoc;

/** <b>Common to (this packagage's) logger handlers</b>. <br />
 *  <br />
 *  <br />   
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2009  &nbsp; Albrecht Weinert. 
 *  @version  $Revision: 33 $ ($Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $)
 *  @author   Albrecht Weinert
 *  @see LogWriterHandler
 */

@MinDoc(
   copyright = "Copyright  2006, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use for (java.) logging",  
   purpose = "parent for logging handlers"
) public abstract class LogHandler extends Handler {

/** Log-Level Off. <br />
 *  <br />
 *  @see Level#OFF
 */
   public static final int OFF_LEVEL = Level.OFF.intValue();

/** Closed and done. <br />
 *  <br />
 *  @see #isClosed()
 */
   protected boolean closed;

/** A filter. <br /> */
   Filter filter;

/** Closed (and done). <br />
 *  <br />
 *  If true this {@link Handler} is not usable any more and should be 
 *  forgotten as soon as possible.<br />
 *  <br />
 *  Not usable means that all new logging orders will be filtered out.
 *  Nevertheless cached logging (as with LogDbHandler) will be 
 *  forwarded in a working thread (using the {@link #doClose} flag
 *  as hurry up signal.<br />
 */
   public final boolean isClosed() {return closed;}

/** This LogDbHandler's logging level as number. <br />
 *  <br />
 *  The actual {@link Level} of this {@link LogHandler} 
 *  ({@link #setLevel(Level) level}) will be held here as integer 
 *  number. <br />
 */
   protected int intLevel;

/** Set a filter. <br />
 *  <br />
 *  Hint: This is a opportunity for complex filter criteria. They will
 *  live on cost of the the logging process control threads.<br />
 *  <br /> 
 *  default: null<br />
 *  <br />
 *  @param filter the (one) extra filter or null for just level filtering
 */
   @Override  public final synchronized void setFilter(final Filter filter){
      if (this.filter == filter) return;
      this.filter = filter;
      try {
         super.setFilter(filter);
      } catch (Exception e) { } // couldn't care less if superclass refuses   
   } // setFilter(Filter)

/** Decide if a LogRecord is loggable on this Handler. <br />
 *  <br />
 *  record null or {@link LogHandler} closed gets a false.<br />
 *  <br />
 *  Other wise the level and if given filter criteria apply.<br />
 *  <br />
 *  @see Handler#isLoggable(java.util.logging.LogRecord)
 */
   @Override public final boolean isLoggable(final LogRecord record){
      if (closed || doClose || record ==  null ) { // || intLevel == OFF_LEVEL) {
         return false;
      }
      if (record.getLevel().intValue() < intLevel) return false;
      if (filter == null) return true;
      return filter.isLoggable(record);
    } // isLoggable(LogRecord)

/** Do close, when finished. <br />
 *  <br />
 *  Can be used as Signal by overridden method {@link #close()} to other 
 *  working threads.<br />
 */
   protected volatile boolean doClose;

/** Set this LogDbHandler's logging level .<br />
 *  <br />
 *  {@link LogRecord}s having a lower Level (integer value) as the one set here
 *  will be ignored by this {@link LogHandler}.<br />
 *  <br />
 *  Implementation hint: This class and its inheritors handle the filtering
 *  meant by this level more efficient than the superclass {@link Handler}.
 *  Nevertheless this info (level) is forwarded to it.<br />
 *  <br />
 *  Hint: The basic level filtering is made / should be made by the Logger, 
 *  thus avoiding the construction of {@link LogRecord}s  than thrown
 *  away by the {@link Handler}s.<br />
 *  <br />
 *  default: {@link Level}.{@link Level#ALL ALL}
 */
   @Override public final synchronized void setLevel(final Level level){
      if (level == null) return;
      Level oldLevel = getLevel();
      if (level == oldLevel) return;
      try { 
         super.setLevel(level);
      } catch (Exception e) {} // couldn't care less; we use intLevel only
      intLevel = level.intValue();
   } // setLevel(Level)

/** The formatter. <br />
 *  <br />
 *  Hint: This final Formatter may be used as lock object as is in
 * LogDbHandler.<br />
 */   
   public final Formatter formatter;


/** The formatter. <br />
 *  <br />
 *  Final and never null.<br />
 */   
   @Override public final Formatter getFormatter(){ return formatter; }

/** Set the formatter. <br />
 *  <br />
 *  This method does nothing if the formatter to be set and already set are 
 *  the same. Otherwise an {@link IllegalArgumentException} is thrown as this
 *  classes {@link #getFormatter() formatter} is final and never null. (This
 *  senseless method is nevertheless necessary to override a not fitting 
 *  inherited implementation.)<br />
 */   
   @Override public final synchronized void setFormatter(
                                         final Formatter formatter){
     if (formatter != this.formatter) 
      throw new IllegalArgumentException("This handler's formatter is final");
   } // setFormatter()

/** Constructor. <br />
 *  <br />   
 *  @param formatter non null (and forever)
 */
   public LogHandler(final Formatter formatter){
      super();
      this.formatter = formatter;
      if (formatter == null) 
             throw new NullPointerException("LogHandler needs a Formatter");
   } // constructor

} // class LogHandler