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
package de.frame4j;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import de.frame4j.graf.AskDialog;
import de.frame4j.graf.ColorHelper;
import de.frame4j.io.Input;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;


/** <b>Report and / or ask a question with timeout</b>. <br />
 *  <br />
 *  This application shows alarm, error or status reports, to be acknowledged,
 *  on a window or it asks a question. The timeout to acknowledge or respond
 *  as well as the default answer are settable.<br />
 *  The window shown is simply structured (from top to bottom):<ul>
 *  <li> a window title,<br />
 *  <li> a (report) text, also multi-line, not to be edited or copied,</li>
 *  <li> up to three buttons, with meaning Yes, Cancel, No and</li>
 *  <li> a question respectively a second one line report text.</li></ul>
 * 
 *  Window layout and content, the number of buttons (0..3) and else can be 
 *  freely customised via parameters and options. Hints by<br />
 *  &nbsp; <code> &nbsp; java AskAlert -help</code><br />
 *  <br />
 *  Besides a variety of settings the level of  
 *  {@link de.frame4j.util.App#getVerbosity() verbosity} determines, what is 
 *  logged:<ul>
 *  <li> == {@link de.frame4j.util.Verbos#SILENT} (option -silent) : 
 *         no log output, </li>
 *  <li> &lt;= {@link de.frame4j.util.Verbos#NORMAL} (default) : &nbsp; 
 *         the report text,</li>
 *  <li> &lt;= {@link de.frame4j.util.Verbos#VERBOSE} (Option -v) : also
 *         the question and the response.</li></ul>
 *  
 *  By starting the application {@link AskAlert} the window is made visible
 *  and it is now waited for a limited time for a user reaction. This time 
 *  until answering or pressing a button is settable in 1/10s units in the
 *  range 5 to 90000 seconds.<br />
 *  <br />
 *  Timeout or user reaction will end the application. The event is signalled
 *  via the exit code:<br />
 *  <br />
 *  <table border="1" cellspacing="0" cellpadding="5" style="text-align:center;"
 *  summary="Return values">
 *  <tr><th colspan="2"> Value </th><th> Meaning </th></tr>
 *  <tr><td style="text-align:center;"> &gt;5 </td>
 *  <td style="text-align:center;"> problems </td><td> The application had startup 
 *        problems; the window was not shown. </td></tr>
 *  <tr><td align=center> 5  </td>
 *  <td align=center> cancel </td>
 *           <td> Cancel button / middle button</td></tr>
 *  <tr><td style="text-align:center;"> 4 </td>
 *  <td style="text-align:center;"> closed </td><td> window was explicitly closed (by
 *        X or icon menu). </td></tr>
 *  <tr><td style="text-align:center;"> 3 </td>
 *  <td style="text-align:center;"> time out </td><td> No response, waiting time  
 *                  over *1). </td></tr>
 *  <tr><td align=center> 1 </td>
 *  <td align=center> no </td>
 *        <td>  No button / right button</td></tr>
 *  <tr><td align=center> 0 </td>
 *  <td align=center> yes </td>
 *        <td> Yes button / left button</td></tr></table>
 *  <br style="clear:both;">
 *  Remark *1): The return code for time-out is freely settable by options.
 *  Among other possibilities, this simplifies the interpretation of time-out
 *  as default yes in a batch script.<br />
 *  <br />
 *  This application can be used for report and control tasks in batch 
 *  scripts (.bat instead of {@code @Echo} and {@code @Pause}).<br />
 *  Example:<br />
 *  <code> &nbsp; java AskAlert &quot;Onwards with WWW server update&quot; -v
 *    &quot;Stop and cancel&quot;  -wa -wp</code><br />
 *  <code> &nbsp; if ERRORLEVEL 1 goto :ende</code><br />
 *  <br />
 *  <br />
 *  Additional function:<br />
 *  Started without any parameter, this application provides some information
 *  about version and copyright of the Frame4J installation it is part of (see
 *  <a href="https://frame4j.de/index_en.html" 
 *   target="_top">also here</a>).<br />
 *  When possible (accessible) the main manifest entries are displayed from
 *  Frame4J's main .jar-file, namely &quot;{@code frame4j.jar}&quot;, that
 *  should be an installed extension in directory {@code ...jre\lib\ext}. 
 *  Therefore {@link AskAlert} is the start application of the .jar file
 *  frame4j.jar. By<br /> &nbsp;
 *  {@code  java -jar  frame4j.jar} or by double clicking the .jar file this
 *  application {@link AskAlert} is started in this info mode. Exit codes
 *  0, 5 (buttons), 4 (close) as well as 3 (time-out) are returned also in
 *  this mode.<br />  
 *  <br />
 *  <br />
 *  <b>Hints</b>:<br />
 *  <br />
 *  Execution / command line parameters containing blanks, quotes or asterisks
 *  must be quoted (&quot;&quot; at least on Windows).<br /> 
 *  <br />
 *  
 *  To this application {@link AskAlert} 
 *  (<a href="doc-files/AskAlert.java" target="_top">source</a>) belongs
 *  (as integral part) a .properties file named 
 *  <a href="doc-files/AskAlert.properties" 
 *   target="_top">AskAlert.properties</a>. It's part of the 
 *  documentation.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a> 
 *  Copyright 2004, 2008 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see      de.frame4j
 *  @see      App
 *  @see      de.frame4j.graf
 *  @see      AskDialog
 *  @author   Albrecht Weinert
 *  @version  $Revision: 50 $ ($Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $)
 */
 // so far    V00.00 (09.09.2004) : new
 //           V02.12 (15.07.2005) : Text and Key delete mutually
 //           V.101+ (10.02.2014) : trial to avoid getting keyboard focus
 //           V.135+ (15.08.2015) : prepare for Java9 (no installed ext.)
 //           V.141+ (05.08.2016) : refactored to Frame4J'89 slimline
@MinDoc(
   copyright = "Copyright 2004-2016, 2019  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 50 $",
   lastModified   = "$Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $",
   usage   = "start as Java application (-? for help)",  
   purpose = "report or question on Window with timeout"
) public class AskAlert extends App {

//-----  Properties, automatic set by Prop parameter parsing ------

/** Maximal wait time in tenth (1/10) of seconds. <br />
 *  <br />
 *  Range: 50..900000 (5s .. ~1d)<br />
 *  default: 120 (12s)<br />
 */ 
   protected int waitMax = 120;

/** Set maximal wait time in tenth (1/10) of seconds. <br />
 *  <br />
 *  Range: 50..900000 (5s .. ~1d)<br />
 *  default: 120 (12s)<br />
 *  @param waitMax  wait time in 1/10s
 */ 
   public void setWaitMax(int waitMax){
      this.waitMax =  waitMax >= 50 && waitMax <= 900000 ? waitMax : 120;
   } //  setWaitMax(int)

/** The report / text above buttons. <br />
 *  <br />
 *  default: null (no report text)
 */
   public String upper;

/** Set the report / text above buttons. <br /> 
 *  <br />
 *  @param upper message / report text (above the buttons)
 */
   public void setUpper(String upper){ this.upper = upper; }

/** Font of upper text non proportional. <br />
 *  <br />
 *  Usually better to display file or directory names.<br />
 *  <br />
 *  default: false (hence proportional)<br />
 */
   public boolean upperMonospaced;

/** The question / text below the buttons. <br />
 *  <br />
 *  default: null (no question text)
 */
   public String lower;
   
/** Set the question / text below the buttons. <br /> 
 *  <br />
 *  @param lower message / report text (below the buttons)
 */
   public void setLower(final String lower){ this.lower = lower; }

/** Left Yes-button, text. <br />
 *  <br />
 *  default: null<br />
 *  Null or empty means no left button, if also the language key
 *  ({@link #yesKey}) is empty.<br />
 *  <br />
 */
   public String yes;

/** Left Yes-button, key. <br />
 *  <br />
 *  If the text {@link #yes} value is null or empty, this is the key to a 
 *  (inter) nationalised, user language dependent value fetched by 
 *  (@link App#valueLang(CharSequence) valueLang(yesKey)}.<br />
 *  <br />
 *  default: &quot;yes&quot;<br />
 *  <br />
 *  @see de.frame4j.util.AppLangMap
 */
   public String yesKey = "yes";
 
/** Setter for {@link #yes}. <br />
 *  <br />
 *  Sets {@link #yes} according to the parameter and leaves {@link #yesKey}
 *  unchanged.<br />
 *  <br />
 *  @see #setYesKey(String)
 *  @param yes  text (label) for yes button
 */
   public void setYes(final String yes){ 
      /// log.println("  /// TEST setYes(" + yes);
      this.yes = yes;
      this.yesKey = null;
  } // setYes(String)

/** Setter for {@link #yesKey}. <br />
 *  <br />
 *  Sets {@link #yesKey} according to parameter and leaves {@link #yes} (text)
 *  unchanged.<br />
 *  <br />
 *  @see #setYes(String)
 *  @param yesKey the key, will be stripped from surrounding white space; 
 *          (then) empty acts like null 
 */
   public void setYesKey(final String yesKey){
      this.yesKey =  TextHelper.trimUq(yesKey, null);
   } // setYesKey(String)
   
/** Middle Cancel-button, text. <br />
 *  <br />
 *  Same proceeding as with {@link #yes} and {@link #yesKey}.<br />
 *  default: null<br />
 */
   public String cancel;

/** Middle Cancel-button, key. <br />
 *  <br />
 *  Same proceeding as with {@link #yes} and {@link #yesKey}.<br />
 *  default: &quot;abort&quot;<br />
 */
   public String cancelKey = "abort";
   
/** Setter for Cancel text. <br />
 *  <br />
 *  Same proceeding as with {@link #yes} and {@link #yesKey}.<br />
 *  @param cancel  text (label) for cancel button
 */
   public void setCancel(final String cancel){ this.cancel = cancel; }
   
/** Setter for Cancel key. <br />
 *  <br />
 *  Same proceeding as with {@link #yes} and {@link #yesKey}.<br />
 *  @param cancelKey  key (command)  for cancel button
 */
   public void setCancelKey(final String cancelKey){
      this.cancelKey = TextHelper.trimUq(cancelKey, null);
   } // setCancelKey(String)

/** Setter for No text. <br />
 *  <br />
 *  Same proceeding as with {@link #yes} and {@link #yesKey}.<br />
 *  @param no  text (label) for no button
 */
   public void setNo(final String no){ this.no = no; }

/** Setter for No key. <br />
 *  <br />
 *  Same proceeding as with {@link #yes} and {@link #yesKey}.<br />
 *  @param noKey  key (command)  for no button
 */
   public void setNoKey(final String noKey){
      this.noKey = TextHelper.trimUq(noKey, null);
   } // setNoKey(String) 

/** Right No button, text. <br />
 *  <br />
 *  Same proceeding as with {@link #yes} and {@link #yesKey}.<br />
 */
   public String no;

/** Right No button, key. <br />
 *  <br />
 *  Same proceeding as with {@link #yes} and {@link #yesKey}.<br />
 */
   public String noKey = "no";

/** Set dialogoue's background colour. <br />
 *  <br />
 *  If set non empty the value is used as the dialogue windows background
 *  colour before making it visible.<br />
 *  <br />
 *  It is tried to interpret <code>bg</code> as colour by 
 *  {@link ColorHelper#getColor(CharSequence)}. If this is not possible null is
 *  set or left.<br />
 *  <br />
 *  default: null (standard background colour of runtime) <br />
 *  @param bg names a background colour
 */
   public void setBg(final CharSequence bg){
      this.bg =  ColorHelper.getColor(bg);
   } // setBg(CharSequence bg) 

/** Dialouege's background colour. <br />
 *  <br />
 *  @see #setBg(CharSequence)
 */
   protected Color bg;
   
/** Return code for timeout. <br />
 *  <br />
 *  If ended by time-out due to no user response the standard return code 
 *  is 3 (time-out).<br />
 *  <br />
 *  This can be arbitrarily changed by this property. So any default response
 *  can be substituted for time-out, usually simplifying the return code
 *  evaluation in scripts significantly (especially when blessed with 
 *  Window's rich &quot;IF ERRORLEVEL&quot; syntax.<br />
 *  <br />
 *  default: 3 <br />
 */   
   public int timeOutCode = 3; 

/** AskAlert's start method. <br />
 *  <br />
 *  Execute: java AskAlert [options] [report [question [title]]] <br />
 *  The application returns with exit codes signalling the user's response;
 *  see <a href="#navbar_top">above</a>.<br />
 *  <br />
 *  @param  args command line parameters 
 */
   public static void main(String[] args){
      try {
         new AskAlert().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

   
/** AskAlert's working method. <br />
 *  <br />
 *  @return 0..5: exit code; &gt; 5: start or parameter error
 */
   @Override public int doIt(){
      final boolean noArgs = args.length == 0 
                              || (args.length == 1 && args[0].length() == 3
                              && args[0].charAt(0) == '-');
      if (verbose || noArgs) {
         log.println();
         log.println(twoLineStartMsg());
      }

      if (noArgs) { // with no args AskAlert acts as framework info
         StringBuilder bastel = new StringBuilder(500);
         bastel.append(valueLang("sumLin"));
         bastel.append("\n\u00A0  Copyright 2019 \u00A0 "
                                 + "Albrecht Weinert \u00A0 a-weinert.de \n");
         JarFile jarFile   = null;
         InputStream manIS = null;
         Input inManif     = null;
         String jfN = ComVar.JRL == null ? null 
                             : ComVar.JRL + "ext" + ComVar.FS + "frame4j.jar";
         String firstTryError = null;
         if (jfN != null) { // first try /lib/ext relative jar
            try {
               jarFile = new JarFile(jfN);
            } catch (IOException e) {
               firstTryError = jfN + " jar file problem : " + e.getMessage();
               jfN = null;
            } // catch
         } // first try /lib/ext relative jar

         
         // To prepare for Java9 that destroyed installed extensions
         // Frame4J is using since more than 15 years
         if (jfN == null || jarFile == null) { // second try: determine own jar
            jfN = de.frame4j.AskAlert.class.getProtectionDomain()
                                    .getCodeSource().getLocation().getPath();
            if (jfN != null)  try {
               jarFile = new JarFile(jfN);
            } catch (IOException e) {
               log.println(jfN + " jar file problem : " + e.getMessage());
               jfN = null;
            } // catch
         } // second try: determine own jar
         
         
         if (jarFile == null) {
            if (firstTryError != null) log.println(firstTryError);
            if (jfN != null) log.println("\n ### no jar File : " + jfN);
         } else {
            ZipEntry zip = jarFile.getEntry("META-INF/MANIFEST.MF");
            try {
               manIS = jarFile.getInputStream(zip);
               if (manIS != null) {
                  inManif = new Input(manIS, null);
               } else {
                  log.println("no input resource (META-INF/MANIFEST.MF)");
               }
            } catch (Exception e) {
               log.println("no manifest inputStream : " + e.getMessage());
            }
         }
         String theMan     = null;
         String theMainMan = null;
         if (inManif == null) {
            log.println("no .../META-INF/MANIFEST.MF readable");
         } else try {
            theMan = inManif.getAsString(ComVar.JAR_ENCODING);
            if (theMan != null) { //     0123456789X12345
             ///   int epo = theMan.indexOf("Extension-Name: ", 20) + 21;
               int epo = theMan.indexOf("Name: de", 500);
               theMainMan = epo < 1 ? theMan : theMan.substring(0, epo);
               /// log.println(theMainMan);
            }
            ///  inManif.recycle(null, 0L);
            ///  mani = new Manifest(inManif);
         } catch (Exception ex) {
            log.println(" manifest get error : " + ex.getMessage());
         } 

         if (theMainMan != null) { // with  manifest infos
            bastel.append("\n\u00A0  jarFile: \u00A0 ").append(jfN );

            bastel.append("\n\u00A0  manifest (main entries): \n\n");
            bastel.append(theMainMan);
         } else { // with / without manifest infos
            bastel.append("\u00A0  jarFile:  frame4j.jar (not located) \n");
            bastel.append("\u00A0  Author / Vendor: \u00A0  Albrecht Weinert\n");
         } //  without manifest infos
         bastel.append("\n  ");
         upper = bastel.toString();
         log.println(upper);
         bastel = null;
         
         yes     = valueLang("gratulKey");
         cancel  = valueLang("gratulKey2");
         waitMax = 485; // 40 sec
         no = null;
         title = "\u00A0  Framework \u00A0 Frame4J  (R.12)         \u00A0";
         lower = "\u00A0  AskAlert  V.$Revision: 50 $" 
             +  " ($Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $);"
         		               + " package: de.frame4j... \u00A0";
      } else {
         if (isDebug()) prop.list(log);
         
         yes    = TextHelper.trimUq(yes, null);
         if (yes  == null) yes     = valueLang(yesKey);
         
         no     = TextHelper.trimUq(no, null);
         if (no     == null) no      = valueLang(noKey);
    
         cancel = TextHelper.trimUq(cancel, null);
         if (cancel == null) cancel  = valueLang(cancelKey);

         if (isNormal() && upper != null) log.println(upper);
      }
      int ans = -1; // we use the timeout code if something went wrong
      
      try {
      ans = AskDialog.getAnswer(title, upper,
                              upperMonospaced, yes, cancel, no,
                              waitMax, lower, bg);
      } catch (Exception hex) {
         final String ernWarnKey = hex instanceof java.awt.HeadlessException
                                      ?  "runHeadlessWarn" : "noGraphError";
         String mess = hex.getLocalizedMessage();
         if (mess == null) mess = hex.getMessage();
         if (mess == null) mess = hex.toString();
         log.println(valueLang(ernWarnKey, "You're headless") + ": " + mess);
         if (!noArgs) log.println(valueLang("responseHeadless",
                          "timeout response w/o delay") + " " + timeOutCode);
         verbose = false;
      }
      
/*  Mapping AskAlert return (ans) to ExitCode (ret):
 *                                                    ret = 2 - ans
 *  AskDialog      |  AskAlert
 *  CLOSED = -2;         4
 *  NOT_YET = -1;        3    (oder substitute timeOutCode)
 *  CANCEL = 0;          5
 *  NO = 1;              1
 *  YES = 2;             0
 */         
       final int ret = ans == 0 ? 5 
                      : ans == -1 ? timeOutCode    : 2 - ans; // s.o.
      if (verbose) {
         log.println( (lower != null ? lower + "  "
                                 : valueLang("respRep", "Antwort : ") )
            + "  " + ret + " (" + AskDialog.answerAsText(ans) + ")");
      }
      return ret;
  } // doIt()

} // class AskAlert (01.10.2004, 02.06.08, 05.02.09, 10.02.2014, 15.08.2015)
