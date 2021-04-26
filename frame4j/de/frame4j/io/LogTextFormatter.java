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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import de.frame4j.util.AppLangMap;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import de.frame4j.time.TimeHelper;

/** <b>A logger formatter for the simple text form</b>. <br />
 *  <br />
 *  An object of this class is a plain efficient 
 *  {@link java.util.logging.Formatter}, suitable for the 
 *  {@link java.util.logging logging} API's 
 *  {@link java.util.logging.Handler} like e.g. 
 *  {@link LogWriterHandler}.<br />
 *  It transforms {@link java.util.logging.LogRecord}s into a simple human
 *  readable text format.<br />
 *  <br style="clear:both;">
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2006 &nbsp; Albrecht Weinert  
 *  @author   Albrecht Weinert
 *
 *  @see TextHelper
 *  @see TimeHelper
 */
 // so far    V02.00 (28.06.2006 08:19) : neu 
 //           V02.18 (18.05.2004 07:38) : /** 
 //           V.134+ (02.11.2015) : method made static

@MinDoc(
   copyright = "Copyright  2001 - 2002, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 39 $",
   lastModified   = "$Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use for (java.) logging",  
   purpose = "a formatter for log messages (as text)"
) public class LogTextFormatter extends Formatter {
   
/** Make a formatter (LogRecord -&gt; Text). <br />
 *  <br />
 */   
   public LogTextFormatter(CharSequence head, CharSequence tail){
      this.head = TextHelper.trimUq(head, ComVar.EMPTY_STRING);
      this.tail = TextHelper.trimUq(tail, ComVar.EMPTY_STRING);
   } // LogTextFormatter(CharSequence

/** Header for this Formatter's output. <br /> */   
   protected String head;   

/** Header for this Formatter's output. <br />
 *  <br />
 *  This method returns the sequence set on construction. (The parameter is 
 *  ignored.)<br />
 *  <br />
 *  default: {@link ComVar#EMPTY_STRING} 
 */   
   @Override public String getHead(Handler h){
      return head;
   } // getHead(Handler)

/** End text for this Formatter's output. <br /> */   
   protected String tail;   

/** End text for this Formatter's output. <br />
 *  <br />
 *  This method returns the sequence set on construction. (The parameter is 
 *  ignored.)<br />
 *  <br />
 *  default: {@link ComVar#EMPTY_STRING} 
 */   
   @Override public String getTail(Handler h){
      return tail;
   } // getTail(Handler)
   
/** Formatting a message. <br />
 *  <br />
 *  The {@link LogRecord}'s ({@code record}'s) 
 *  {@link LogRecord#getMessage() message} ) remains unchanged if null or 
 *  empty. Otherwise it is tried to interpret it as a key for a language 
 *  specific report or message format  ({@link AppLangMap}.{@link
 AppLangMap#valueUL(CharSequence, String) valueUL(message, message)}).<br />
 *  <br />
 *  If the &mdash; may be language specifically &mdasH; modified  message is
 *  empty or contains no pattern  &quot;{0&quot; .. &quot;{9&quot; (for 
 *  formatting of parameters; see {@link TextHelper}.{@link
 *  TextHelper#messageFormat(StringBuilder, CharSequence, Object)
 *  messageFormat(..)}) it is returned as (now) is. The same applies if 
 *  record has no or an empty parameter entry
 *  ({@link LogRecord#getParameters()}).<br /> 
 *  <br />
 *  As a especialness this formatter replaces a pattern &quot;{*} by the 
 *  comma separated list of all parameters. If this pattern is there this will
 *  be the only parameter formatting made.<br />
 *  <br />
 *  Otherwise message will be used as formatting pattern for
 *  {@link TextHelper}.{@link
 *   TextHelper#messageFormat(StringBuilder, CharSequence, Object)
 *  messageFormat(..)}) and the result will be returned.<br />
 *  <br />
 *  Returning a result here means the appending to a  {@link StringBuilder}
 *  supplied or newly generated.<br />
 *  <br />
 *  Hint: This is the implementing / helper method for 
 *   {@link #format(LogRecord)}.<br />
 *  <br />
 *  @param    record  the log entry with the plain (raw) message
 *  @param    bastel  the builder to append to. If null made with start
 *            capacity 99
 *  @return   bastel
 *  @see AppLangMap
 *  @see AppLangMap#valueUL(CharSequence, String)
 */
   public static StringBuilder formatMessage(StringBuilder bastel, 
                                                  LogRecord record){
      if (bastel == null) bastel = new StringBuilder(99);
      if (record == null) return bastel;
      String format = record.getMessage();
      format = AppLangMap.valueUL(format, format);
      final int formLen = format == null ? 0 : format.length();
      if (formLen < 3 || format == null) return bastel.append(format);
 
      Object[] param = record.getParameters();
      if (param == null || param.length == 0) return bastel.append(format);

      final int firstOpenBrace = format.indexOf('{');
      if (firstOpenBrace < formLen -3) return bastel.append(format); // no {
      if (format.charAt(firstOpenBrace + 1) == '*' 
                      && format.charAt(firstOpenBrace + 2) == '}') { // {*}
        if (firstOpenBrace > 0)
           bastel.append(format.substring(0, firstOpenBrace));

        for (int i = 0; i < param.length; ++i) {
           Object akt = param[i];
           if (akt == null) {
              bastel.append(" ,");
           } else {
              bastel.append(' ').append(akt.toString()).append(',');
           }
        } // for
           
        if (firstOpenBrace + 3 < formLen)
              bastel.append(format.substring(firstOpenBrace + 3, formLen));
              
        return bastel;    
      } // {*}
      
      return TextHelper.messageFormat(bastel, format, param);
   } // formatMessage(StringBuilder, LogRecord)
   
/** Formatting a message. <br />
 *  <br />
 *  The call is equivalent to <br /> &nbsp;
 * {@link #formatMessage(StringBuilder, LogRecord) formatMessage(null, record)}
 *   returned as String.<br />
 *  <br />
 */        
   @Override public final synchronized String formatMessage(
                                                    final LogRecord record){
      return formatMessage(null, record).toString();
   } // formatMessage(LogRecord)
   
/** (Nationalised) time format. <br />
 *  <br />
 *  Never null.<br />
 *  default: 
 *  {@link AppLangMap}.{@link AppLangMap#valueUL(java.lang.CharSequence)
 *    valueUL(&quot;wedaclock&quot;)}.<br />
 *  <br />
 *  @see de.frame4j.time.TimeHelper#format(CharSequence, java.time.ZonedDateTime) TimeHelper.format(CharSequence, ZonedDateTime)
 */
   public final String getTimeFormat(){
      if (wedaclock == null) wedaclock =  AppLangMap.valueUL("wedaclock");        
      return wedaclock;
   } // getTimeFormat()

/** (Nationalised) time format. <br />
 *  <br />
 *  @see #getTimeFormat()
 */
   protected transient String wedaclock;
   
/** Formatting a LogRecord in readable text format. <br />
 *  <br />
 *  The output is:<br />
 *  time, levelName [sequence], sourceClassName (or LoggerName as substitute),
 *  MethodName :
 *      \\<br /> &nbsp;
 *   message(parameters if given) <br /> &nbsp;
 *   Exception stack trace (if given)<br />
 *  <br /> 
 *  Hint 1: time will formated as  <br /> &nbsp;
 *   day of week, date , time of day<br />
 *  using the standard nationalised formatting; for French one may see e.g.
 *  something like<br /> &nbsp;
 *  je, 29.06.2006, 09:28:50<br />
 *  <br />
 *  Hint 2: Logging applications should take the sense to supply a unique 
 *  source object's / actor's name instead of class or method names usually
 *  expected, supported or even determined at immense costs (by &quot;helper
 *  exceptions&quot;) by logging APIs.<br />
 *  <br />
 *  That logging APIs (including the Java-Logging-API) don't see that as use
 *  case stems from the fact that those logging APIs are all being designed 
 *  in reality as debugging &mdash; software event collecting &mdash; APIs
 *  for the sake of the programmer and not as logging support for say 
 *  process control applications.<br />
 *  <br />
 *  @param    record  the log entry with the plain (raw) message
 *  @return   record's content human readable (may be multi-line)
 */
   @Override public String format(LogRecord record){
      StringBuilder bastel  = new StringBuilder(255);
      long tmpLong =  record.getMillis();
      bastel.append(TimeHelper.format(getTimeFormat(), tmpLong));
      bastel.append(" (.");
      bastel.append(TextHelper.threeDigit((int) (tmpLong % 1000)));
      bastel.append("), ");
      
      Level level = record.getLevel();
      if (level != null) bastel.append(level.getName()).append(' ');
      
      bastel.append('[').append(record.getSequenceNumber()).append("], ");
      
      String text = record.getSourceClassName();
      if (text == null) text = record.getLoggerName();
      if (text != null) bastel.append(text).append(", ");

      text = record.getSourceMethodName();
      if (text != null) bastel.append(text).append(' ');

      bastel.append(": \\\\\n    \\\\ ");
      
      formatMessage(bastel, record);
      
      Throwable thrown = record.getThrown();
      if (thrown != null) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         thrown.printStackTrace(pw);
         bastel.append(sw.toString());
      }
      return bastel.toString();
   } // format(LogRecord)
 
} // class LogTextFormatter (28.06.2006, 19.03.2009)