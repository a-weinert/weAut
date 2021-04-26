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
package de.frame4j.xml;

import java.io.PrintWriter;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import de.frame4j.util.AppLangMap;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;

/** <b>An error handler for XML-Parser</b>. <br />
 *  <br />
 *  An object of this class is an error handler, that just reports errors
 *  and optionally (see {@link #reThrow}) tries to go on in spite of
 *  errors.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2001 - 2005 &nbsp; Albrecht Weinert<br />  
 *  <br />
 *  @see      de.frame4j
 *  @see SAXHandler
 */
 // so far:   V00.00 (15.08.2001 12:49) :  new
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V02.13 (08.03.2004 08:43) :  /**
 //           V02.02 (26.04.2005 08:49) :  reportErrors()

@MinDoc(
   copyright = "Copyright  2001 - 2005, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use in XML applications",  
   purpose = "an error handler for XML parser, base of other handlers"
) public class ParseErrorHandler implements  org.xml.sax.ErrorHandler {

/** The counter for errors.<br /> */
   public int errorCount;

/** The counter for warnings.<br /> */
   public int warningCount;

/** The counter for fatal errors.<br /> */
   public int fatalCount;

/** Denomination of the input. <br />
 *  <br />
 *  Must not be null!<br />
 */
   protected String name;

/** Denomination of the input. <br />
 *  <br />
 *  Is never null; &quot;...&quot; 
 *  will be used if no good value is given.<br />
 */
   public final String getName() {return name;}


/** Output for errors and warnings. <br />
 *  <br />
 *  Set (finally) at construction.<br />
 *  Never null; default: System.out.<br />
 */
   protected final PrintWriter log;


/** Throw exceptions after counting and logging (again). <br />
 *  <br />
 *  If true the event methods {@link #error(SAXParseException)} and
 *  {@link #fatalError(SAXParseException)} re-throw the 
 *  {@link SAXParseException}s  passed to them (again).<br />
 *  <br />
 *  Hint:  The method {@link #warning(SAXParseException)} never does 
 *  that.<br />
 *  <br />
 *  default: false
 */
   protected boolean reThrow;

/** An error handler by name, XML-input and report output. <br />
 *  <br />
 *  An {@link ErrorHandler} object is made named according to the parameter
 *  {@link #name} or &quot;...&quot;.<br />
 *  <br />
 *  As PrintStream {@link #log} the parameter {@code log} is taken if not
 *  null and the (decorated) normal output (System.out) otherwise.<br />
 *  <br />
 *  @param name the handler's or the input's name (stripped from surrounding 
 *              white space; null or empty is taken as &quot;...&quot;
 *  @param log  destination for reports
 *  @param reThrow if true error exceptions are thrown (again) and not just
 *                 reported
 *  @see #init(CharSequence, boolean)
 */
   public ParseErrorHandler(CharSequence name,
                        PrintWriter log, boolean reThrow) {
      this.log = log != null ? log : new PrintWriter(System.out);
      init(name, reThrow);
   } // constructor


/** Initialise an error handler giving the XML input a name. <br />
 *  <br />
 *  This method sets the {@link #getName() name} and {@link #reThrow} to the
 *  parameter values and resets all error counters and else.<br />
 *  <br />
 *  This allows a re-use of this {@link ParseErrorHandler} without any
 *  problems or throwaway objects.<br />
 *  <br />
 *  @param name the handler's or the input's name (stripped from surrounding 
 *              white space; null or empty is taken as &quot;...&quot;
 *  @param reThrow if true error exceptions are thrown (again) and not just
 *                 reported
 *  @see TextHelper#trimUq(CharSequence, String)
 */
   public void init(CharSequence name, boolean reThrow) {
      this.name = TextHelper.trimUq(name, "...");
      this.reThrow = reThrow;
      warningCount = errorCount = fatalCount = 0;
   } // init(CharSequence


/** Handling a fatal Error. <br />
 *  <br />
 *  A one line report on the error with line and column number is ouput to
 *  {@link #log}.<br />
 *  The error is counted.<br />
 *  <br />
 *  @throws SAXParseException the {@code err} provided will be (re-) thrown
 *          if {@link #reThrow} is true
 *  @see #appRep(StringBuilder, String, SAXParseException)
 */
   @Override public void fatalError(SAXParseException err) 
                                                  throws SAXParseException {
      ++fatalCount;
      log.println(appRep(null, "Fatal", err));
      if (reThrow) throw err;
   } // fatalError(SAXParseException 
   
/** Make / append a line containing a parse error report.<br />
 *  <br />
 *  Appended to the StringBuilder bastel is a (may be multi-line) message
 *  containing the {@code typ} (usually &quot;error&quot;, &quot;fatal&quot;
 *  or &quot;warning&quot;) and substantial informations gathered from
 *  {@code err}.<br />
 *  <br />
 *  @param bastel The StringBuilder to append to; if null it will be made with
 *                91 characters starting capacity
 *  @param typ    the message type: error warning or else
 *  @param err    the exception describing the XML parse error
 *  @return       bastel
 */   
   public static StringBuilder appRep(StringBuilder bastel, String typ,
                        SAXParseException err) {
      if (bastel == null) bastel = new StringBuilder(91);
      bastel.append("** ").append(typ);
      if (err == null) {
         bastel.append("  **\n");
         return bastel;
      }
      int tmp = err.getLineNumber();
      if (tmp > 0) {
         bastel.append(", ").append(AppLangMap.valueUL("line", "line"));
         bastel.append(' ').append(tmp);
      }
      tmp = err.getColumnNumber();
      if (tmp > 0) {
         bastel.append(", ").append(AppLangMap.valueUL("column", "column"));
         bastel.append(' ').append(tmp);
      }
      bastel.append("\n ** ").append(err.getMessage());
      return bastel;
   } // appRep(StringBuilder
   
/** Handling an error. <br />
 *  <br />
 *  To {@link #log} a one line message will be output, containing also the
 *  line and column denotation got from {@code err}.<br />
 *  The error is counted.<br />
 *  <br />
 *  @throws SAXParseException the {@code err} provided will be (re-) thrown
 *          if {@link #reThrow} is true
 *  @see #appRep(StringBuilder, String, SAXParseException)
 */
   @Override public void error(SAXParseException err) throws SAXParseException {
      ++errorCount;
      log.println(appRep(null, "Error", err));
      if (reThrow) throw err;
   } // error(SAXParseException 

/** Handling a warning. <br />
 *  <br />
 *  To {@link #log} a one line message will be output, containing also the
 *  line and column denotation got from {@code err}.<br />
 *  The warning is counted.<br />
 *  <br />
 *  No exception will be (re-) thrown by this method.<br />
 */
   @Override public void warning(SAXParseException err)  {
       ++warningCount;
       log.println(appRep(null, "Warning", err));
   } // warning(SAXParseException


/** Summarised report. <br />
 *  <br />
 *  On {@link #log} a multi-line report, made by
 *  {@link #toStringBuilder(StringBuilder)}, will be output including the
 *  so far error / warning counts.<br />
 *  <br />
 *  @see #reportErrors()
 */
   public  void report() {
      log.println(toStringBuilder(null));
   } // report()

/** Summarised report, only in case of warnings or errors. <br />
 *  <br />
 *  On {@link #log} a multi-line report, made by
 *  {@link #toStringBuilder(StringBuilder)}, will be output including the
 *  so far error / warning counts only if such events occurred.<br />
 *  <br />
 *  If the error and warning counts are zero, nothing will happen. This is
 *  the &quot;silent on success&quot; method.<br />
 *  <br />
 *  @see #toStringBuilder(StringBuilder)
 */
   public  void reportErrors() {
      if (warningCount == 0 && errorCount == 0 && fatalCount == 0) return;
      log.println(toStringBuilder(null));
   } // reportErrors()


/** Summarised report. <br />
 *  <br />
 *  Appended to the {@link StringBuilder} bastel will be a multi-line report
 *  containing also the so far error / warning counts.
 *  <br />
 *  If the error and warning counts are zero, an OK message will be
 *  appended.<br />
 *  <br />
 *  @param bastel The StringBuilder to append to; if null it will be made with
 *                91 characters starting capacity
 *  @return bastel
 */
   public StringBuilder toStringBuilder(StringBuilder bastel) {
      if (bastel == null) bastel = new StringBuilder(91);
      bastel.append("\n***  ").append(AppLangMap.valueUL("input", "input"));
      bastel.append(' ').append(name).append(" parsed");
      if (warningCount == 0 && errorCount == 0  && fatalCount == 0) {
         bastel.append(" (OK).\n");
         return bastel;
      }
      bastel.append(":\n");
      int[] par = new int[1];
      if (warningCount > 0 ) {
         par[0] = warningCount;
         bastel.append("***  ");
         TextHelper.messageFormat(bastel,
                                   AppLangMap.valueUL("noowarns"), par);
         bastel.append(".\n");
      }
      if (errorCount > 0 ) {
         par[0] = errorCount;
         bastel.append("***  ");
         TextHelper.messageFormat(bastel,
                                   AppLangMap.valueUL("nooerrs"), par);
         bastel.append(".\n");
      }
      if (fatalCount > 0 ) {
         par[0] = fatalCount;
         bastel.append("***  ");
         TextHelper.messageFormat(bastel, 
                                   AppLangMap.valueUL("noofaterr"), par);
         bastel.append(".\n");
      }
      return bastel;
   } // report()

/** State as  String. <br />
 *  <br />
 *  Returns 
 *  {@link #toStringBuilder(StringBuilder) toStringBuilder(null)}.<br />
 */   
   @Override public String toString() {
      return toStringBuilder(null).toString();
   } // toString()

} // class ParseErrorHandler (08.02.2002, 27.1.2009)
