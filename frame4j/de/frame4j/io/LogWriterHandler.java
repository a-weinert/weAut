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
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import de.frame4j.util.App;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import de.frame4j.time.TimeHelper;

import static de.frame4j.util.ComVar.EMPTY_STRING;

/** <b>A logger handler for PrintWriter</b>. <br />
 *  <br />
 *  An object of this class is a plain efficient 
 *  {@link java.util.logging.Handler} connecting the  
 *  {@link java.util.logging} API to a {@link java.io.PrintWriter}, like for
 *  example {@link App App}.{@link App#log log}verbindet.<br />
 *  <br style="clear:both;">
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2006 &nbsp; Albrecht Weinert  
 *
 *  @see TextHelper
 *  @see TimeHelper
 */
 // so far    V02.00 (27.06.2006) : new 
 //           V.122+ (04.01.2020) : minor spelling

@MinDoc(
   copyright = "Copyright  2006, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 39 $",
   lastModified   = "$Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use for (java.) logging",  
   purpose = "a handler for logging"
) public class LogWriterHandler extends LogHandler {
 
/** The headline was output already. <br /> */
   boolean doneHeader;

/** The Writer for logging. <br /> */   
   public final PrintWriter writer;

/** Format and output a &quot;LogRecord&quot;.<br />
 *  <br />
 *  Logging conditions met, {@link #getFormatter() formatter} will format 
 *  {@code record}. The result will be output to the  {@link PrintWriter}
 *  {@link #writer}.<br />
 *  <br />
 *  Conditions are this {@link LogWriterHandler} not being closed plus the
 *  log and filter conditions.<br />
 *  <br />
 *  @param  record  description of the event to be logged
 *  @see Handler#isLoggable(java.util.logging.LogRecord) 
 *                  Handler.isLoggable(LogRecord)
 */
   @Override public void publish(final LogRecord record){
      if (!isLoggable(record)) return;
      ensureHead();
      String msg;
      try {
         msg = formatter.format(record);
      } catch (Exception ex) {
         reportError(null, ex, ErrorManager.FORMAT_FAILURE);
         return;
      }
      if (msg != null) try {
         writer.println(msg);
      } catch (Exception ex) {
         reportError(null, ex, ErrorManager.WRITE_FAILURE);
      }
   } // publish(LogRecord)

   private void ensureHead(){
      if (doneHeader) return;
      doneHeader = true;
      String msg = formatter.getHead(this);
      if (msg != null && msg != EMPTY_STRING) try {
         writer.println(msg);
      } catch (Exception ex) {
         reportError(null, ex, ErrorManager.WRITE_FAILURE);
      }
   } // ensureHead()


/** Make a LogWriterHandler. <br /> */   
  public LogWriterHandler(final PrintWriter writer, final Formatter formatter){
     super(formatter != null ?  formatter : new LogTextFormatter(null, null));
     if (writer == null) 
       throw new NullPointerException("LogWriterHandler needs non-null Writer");
     this.writer = writer;
     intLevel = getLevel().intValue();
  } // LogWriterHandler(PrintWriter, Formatter)

/** Make a LogWriterHandler with  default formatter. <br /> */   
   public LogWriterHandler(final PrintWriter writer){ this(writer, null); }
   
/** Flush the underlying Writer. <br /> */
   @Override public void flush(){
      if (closed) return;
      try {
        writer.flush();
      } catch (Exception ex) {  
         reportError(null, ex, ErrorManager.FLUSH_FAILURE);
      }
   } // flush()

/** Close this Handlers. <br />
 *  <br />
 *  The underlying Writer {@link #writer} will not be closed.<br />
 *  After writing {@link #getFormatter() formatter}.{@link
 *   java.util.logging.Formatter#getTail(java.util.logging.Handler) tail}
 *  (if given) it is only &quot;{@link #flush()}ed&quot;<br /> 
 */
   @Override public void close(){
      if (closed) return;
      ensureHead();
      String msg = TextHelper.trimUq(formatter.getTail(this), null);
      if (msg != null) try {
         writer.println(msg);
      } catch (Exception ex) {
         reportError(null, ex, ErrorManager.WRITE_FAILURE);
      }
      flush();
      closed = true;
   } // close()

} // class LogWriterHandler (27.06.2006, 04.01.2010)