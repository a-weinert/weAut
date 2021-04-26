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

import static de.frame4j.util.ComVar.CONSOL_ENCODING;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Handler;
import java.util.logging.Logger;

import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.AppLangMap;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.util.PropMap;
import de.frame4j.text.TextHelper;

/** <b>Base of input and output for Java applications</b>. <br />
 *  <br />
 *  For Java applications this class sets the basis for comfortable and robust
 *  input and output. Recurring I/O tasks are handled here<ul>
 *  <li> providing a PrintWriter ({@link #out}) for normal output,</li>
 *  <li> a PrintWriter ({@link #err}) for error messages</li>
 *  <li> a PrintWriter  ({@link #log}) with optional branching to 
 *       normal output and to a file,</li>
 *  <li> a BufferedReader ({@link #in}) for normal input,</li>
 *  <li> handling the different code-pages of / within console 
 *       ({@link java.lang.System}.out, .err and .in) and graphic / file
 *       encoding. These differences usually occur with  Windows' 
 *       shell.</li></ul>
 * 
 *  To have this comfort (compared to the Spartan default features for Java
 *  applications) the system streams are decorated by Reader / Writers and
 *  two  {@link TeeWriter}s are utilised in the following way:<br /><br />
 *  <a href="../doc-files/AppIO.png" target="_top"><img
 *  src="../doc-files/AppIO.png" style="border:0;" height="375" width="586"
 *  alt="AppIO.png (full size)" 
 *  title="An AppIO object's structure and connections" /></a><br />
 *  <br /> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
 *          The I/O structure &nbsp; (click for full size)<br />
 *   &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
 *          (To an ugly <a href="../doc-files/AppIOstruk.txt"
 *           target="_top" >text depiction</a>
 *          for browsers with defaulted graphic support.)<br />
 *  <br />
 *  Applications extending {@link App} get these functions automatically by
 *  an associated {@link AppIO} (and  an{@link AppBase}) object.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2000 - 2005 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see App
 *  @see TeeWriter
 */
 // so far:   V00.00 (14.12.2001 11:04) : new moved out of App
 //           V00.02 (28.08.2002 15:50) : switchOut1 at setCodePage
 //           V02.21 (31.12.2004 15:41) : nationalising by AppLangMap
 //           V.107- (08.04.2009 18:45) : ported to Frame4J
 //           V.104  (11.04.2014 18:00) : some minor changes (String -> ChS)
 //           V.135+ (06.01.2016) : FileHelper
@MinDoc(
   copyright = "Copyright  2001, 2009, 2014  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 35 $",
   lastModified   = "$Date: 2021-04-04 12:58:39 +0200 (So, 04 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use instead of own normal I/O plumbing",  
   purpose = "the I/O base for java applications"
) public final class AppIO {

//---  in err  ------------------------------------------------------------

/** The normal input as BufferedReader. <br />
 *  <br />
 *  This Reader is a /better) substitute for the byte stream {@code System.in},
 *  which delivers it to via {@link #isr}.<br />
 *  <br />
 *  Hint 1: A BufferedReader's methods may throw  {@link IOException}s,
 *          which {@code System.in} avoids. <br />
 *  <br />
 *  Hint 2: Entering cntl-Z (control-Z) on the keyboard as the only character
 *          in a line will be interpreted by {@code readline()} as EoF 
 *          (End of File). <br />
 *  <br />
 *  @see #setInCodePage setInCodePage(String)
 *  @see #out
 *  @see #err
 *  @see #log
 *  @see FilterReader
 */
   public final BufferedReader in;


/** The SwitchedReader underlying the BufferedReader in. <br />
 *  <br />
 *  It will be finally made on construction and be the backup for the (final) 
 *  Reader {@link #in}.<br />
 *  <br />
 *  @see #setInCodePage setInCodePage(String)
 */
   protected final SwitchedReader swr;


/** The InputStreamReader underlying the SwitchedReader swr. <br />
 *  <br />
 *  It will be made using the default code page 
 *  {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING}.<br />
 *  <br />
 *  @see #setInCodePage setInCodePage(String)
 */
   protected InputStreamReader isr;

   
//---  err     ------------------------------------------------------------

/** The error output as PrintWriter. <br />
 *  <br />
 *  This Writer is a (better) substitute for the byte Stream
 *  {@code System.err}, which it is connected to by {@link #esw} and 
 *  {@link #errTW}.<br />
 *  @see #setErrCodePage setErrCodePage(String)
 *  @see #out
 *  @see #in
 *  @see #log
 */
   public final PrintWriter err;


/** The TeeWriter for err. <br /> 
 *  <br />
 *  This TeeWriter is finally connected to {@link #err} for getting 
 *  output.<br />
 *  <br />
 *  Its own output (2) is connected via {@link #esw} to {@code System.err}.
 *  The branching to the other output may be set by
 *  {@link TeeWriter#setOut1(OutputStream) setOut1()} if needed.<br >
 *  <br />
 */
   public final TeeWriter errTW;

/** The OutputStreamWriter decorating System.err . <br />
 *  <br />
 *  It will be made using the default code page 
 *  {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING}.<br />
 *  <br />
 *  @see #setErrCodePage setErrCodePage()
 */
   protected volatile OutputStreamWriter esw;

//---  out     ------------------------------------------------------------

/** The standard output as PrintWriter. <br />
 *  <br />
 *  This Writer is a (better) substitute for the byte Stream
 *  {@code System.out}, which it is connected to by {@link #osw} and 
 *  {@link #outTW}.<br />
 *  <br />
 *  This features a comfortable and robust normal output. By de-coupling
 *  it will not block the application if the console output is blocked.<br />
 *  <br />
 *  Hint: Against some people's unbelief the (Windows) console can easily be
 *  blocked for ever by just marking text. <br />
 *  @see #setOutCodePage setOutCodePage()
 *  @see #in
 *  @see #err
 *  @see #log
 */
   public final PrintWriter out;

/** The OutputStreamWriter decorating System.out . <br />
 *  <br />
 *  It will be made using the default code page 
 *  {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING}.<br />
 *  <br />
 *  @see #setOutCodePage setOutCodePage()
 */
   protected volatile OutputStreamWriter osw;


/** The TeeWriter for out. <br /> 
 *  <br />
 *  This TeeWriter is finally connected to {@link #out}for getting 
 *  output.<br />
 *  <br />
 *  Its own output (1) is connected via {@link #osw} to {@code System.out}.
 *  The branching to the other output may be set by
 *  {@link TeeWriter#setOut2(OutputStream) setOut2()} if needed.<br >
 *  <br />
 */
   public final TeeWriter outTW;


//---  log     ------------------------------------------------------------

/** A log output as PrintWriter. <br />
 *  <br />
 *  This Writer feeds via the {@link TeeWriter} {@link #logTW} the (normal)
 *  output {@link #outTW} and optionally a (log) file.<br />
 *  <br />
 *  If needed use {@link #setLogOut2(OutputStream, String) setLogOut2()}
 *  to set the second output to whatever (usually a file).<br />
 *  <br />
 *  @see #in
 *  @see #out
 *  @see #err
 */
   public final PrintWriter log;

/** The TeeWriter connected to the PrintWriter log . <br />
 *  <br />
 */
   public final TeeWriter logTW;

/** The TeeWriter's log encoding for its second output stream. <br />
 *  <br />
 *  If the {@link TeeWriter}'s {@link #logTW} second output was set with
 *  an explicit encoding it is stored here.<br />
 *  <br />
 *  default: null
 */
   protected String cp2;
   
//------- log Logging ------------------------------------------------------
   
/** The Logger handler. <br />
 *  <br >
 *  @see #getLogHandler()
 */   
   volatile LogWriterHandler logHndlr;

/** The Logger handler. <br />
 *  <br >
 *  This method delivers a {@link Handler} connected to {@link #log}. It is
 *  made on the first call.<br />
 *  <br />
 */   
   public final LogWriterHandler getLogHandler(){
      if (logHndlr != null ) return logHndlr;
      synchronized (this) {
         if (logHndlr != null ) return logHndlr;
         logHndlr = new LogWriterHandler(log);
         return logHndlr;
      } // sync
   } // getLogHandler()

/** Closing the Logger handler. <br />
 *  <br />
 *  If there is (still) a {@link LogWriterHandler} made by
 *  {@link #getLogHandler()}, it will be closed, opted out (if known to it)
 *  from an {@link #getAppLogger() appLogger} and cleared away.<br />
 *  <br /> 
 */   
   public final void closeLogHandler(){
      if (logHndlr == null) return;
      synchronized (this) {
         if (logHndlr == null) return;
         logHndlr.close();
         if (appLogger != null) try {
            appLogger.removeHandler(logHndlr);
         } catch (SecurityException ex) {} // not interested in that
         logHndlr = null;
      } // snyc
   } // closeLogHandler()
   
/** Base logger for the applications. <br />
 *  <br />
 */
   volatile Logger appLogger;

/** Base logger for the applications. <br />
 *  <br />
 *  This method delivers a basic Logger for the application utilising this 
 *  {@link AppIO} object. The {@link Logger} will be made on the first 
 *  call.<br />
 *  <br />
 *  The {@link Logger}'s name will be &quot;de.frame4j&quot;<br />
 *  In the basic settings made it uses  the {@link Handler} available by 
 *  {@link #getLogHandler()}, and hence a  {@link LogTextFormatter} object for
 *  output to {@link #log}.<br />
 *  The {@link java.util.logging.Level} set will be t 
 *  {@link java.util.logging.Level#INFO INFO}. If this {@link Logger} should 
 *  have been indirectly via 
 *  {@link App}.{@link App#getAppLogger() getAppLogger()} the 
 *  {@link java.util.logging.Level Level} will be set automatically according
 *  to  {@link App}.{@link App#getVerbosity() verbosity}. The same applies for
 *  {@link #getLogHandler() logHandler}.<br />
 *  <br />
 *  This {@link Logger} is the last one in a handler chain; that means it will
 *  not delegate further to a Java-Logging-API made default Logger, like
 *  {@link java.util.logging.ConsoleHandler}. This would counteract all 
 *  Frame4J's respectively {@link AppIO}'s or {@link App}'s I/O 
 *  organisation.<br /> 
 *  <br /> 
 */
   public final Logger getAppLogger(){
      if (appLogger != null) return  appLogger;
      synchronized (this) {
         if (appLogger != null) return  appLogger;
         appLogger = Logger.getLogger("de.frame4j");
         appLogger.addHandler(getLogHandler());
         appLogger.setUseParentHandlers(false); // end of chain or standalone
         return appLogger;
      } // sync
   } // getAppLogger()



/** The name of a log file (if given / used). <br />
 *  <br />
 *  The file name the {@link #logFile} was created / opened with is
 *  stored here. null means no extra file logging for {@link #log}.<br />
 *  <br />
 *  default: null <br />
 */ 
   protected volatile String logDat;

/** The  name of a log file (if given / used). <br />
 *  <br />
 *  The file name the {@link #logFile} was created / opened with is
 *  returned here. null means no extra file logging for {@link #log}.<br />
 *  <br />
 *  default: null <br />
 */ 
   public final String getLogDat(){ return logDat; }

/** The log file (if given / used). <br /> */
   protected  volatile File logFile;

/** The log file (if given / used). <br /> */
   public File getLogFile(){ return logFile; }


/** Setting a file for the log output. <br />
 *  <br />
 *  @param name the file name; null or &quot;null&quot; just means close the
 *              actual file.
 *  @param encoding character encoding; null or &quot;defaultEncoding&quot;
 *              or non implemented ones mean default (system file) encoding.
 *  @param outMode  Ask, Append, Overwrite or Create
 *  @param logOnLog true means do report success or failure on {@link #log}
 *  @return true on success
 */
   public boolean setLogDat(final String name, final String encoding, 
                                   OutMode outMode, final boolean logOnLog){
        if (name == null || name.isEmpty() || name.equals("null")) {
           if (logDat != null && logOnLog)
              out.println ("  ** Closing " + logDat + " on log.");
           logDat = null;
           logFile = null;
           logTW.close1();  // no log file output 
           return true;
        }
        if (name.equals(logDat)) return true; // already done

        if (name.equals(outDat)) {
           if (logOnLog)
              out.println ("  ** " + name + " is connected to log & out ");
           return true;
        }

        if (outMode == null) outMode = OutMode.ASK; // AskOverwrite

        logFile =  FileHelper.getInstance(name);
        FileHelper.OS os   =  FileHelper.makeOS(logFile, outMode);
        if (os == null) {
           logFile = null;
           if (logOnLog)
              log.println(AppLangMap.formMessageUL("logfiler", null,
                                  new String[]{name, outMode.name}));
               //    " * Log output " + name + " (" + outMode 
               //    + ") not allowed.\n * No file set.");
           return false;
        }
        logTW.setOut1(os, encoding);
        logTW.noExplFlush1 = true;
        logDat = name; 
        if (logOnLog)
           log.println(AppLangMap.formMessageUL("logfilrp", null, outDat));
               /// (" * Log output set to " + logDat + " .");
        return true;
     } // setLogDat(...

/** The name of a out file (if given / used). <br />
 *  <br />
 *  The named file (if used) takes the outputs to {@link #out} instead of them
 *  going to System.out .<br />
 *  <br />
 *  default: &quot;System.out&quot;.<br />
 */ 
   protected volatile String outDat;

/** The name of a out file (if given / used). <br />
 *  <br />
 *  The named file (if used) takes the outputs to {@link #out} instead of them
 *  going to System.out .<br />
 *  <br />
 *  default: &quot;System.out&quot;.<br />
 */ 
   public final String getOutDat(){ return outDat; }

/** The out file (if given / used). <br /> */
   protected volatile File outFile;

   /** The out file (if given / used). <br /> */
   public File getOutFile(){ return outFile; }


/** Setting the file fort the output out. <br />
 *  <br />
 *  @param name the file name; null or &quot;null&quot; just means close the
 *              actual file.
 *  @param encoding character encoding; null or &quot;defaultEncoding&quot;
 *              or non implemented ones mean default (system file) encoding.
 *  @param outMode  Ask, Append, Overwrite or Create
 *  @param logOnLog true means do report success or failure on {@link #log}
 *  @return true on success
 */
   public boolean setOutDat(final String name, final String encoding, 
                                     OutMode outMode, final boolean logOnLog){
        if (name == null || name.isEmpty() || name.equals("null")) {
           if (outDat != null && logOnLog)
              out.println ("  ** Closing " + outDat + " on out.");
           outDat = null;
           outFile = null;
           outTW.close1();  // no out to normal out 
           return true;
        }
        if (name.equals(outDat)) return true; // done already
        if (name.endsWith("System.out")) {
           if ("System.out".equals(outDat)) return true;
           outTW.setOut1(System.out, encoding);
           outTW.noExplFlush1 = false;
           outFile = null;
           outDat = "System.out";
           if (logOnLog)
              out.println ("  ** out connected to System.out ");
           return true;
        }
        outFile =  FileHelper.getInstance(name);
        FileHelper.OS os   =  FileHelper.makeOS(outFile, outMode);
        if (os == null) {
           outFile = null;
           if (logOnLog)
              log.println(AppLangMap.formMessageUL("outfiler", null,
                                      new String[]{name, outMode.name}));
               ///    " * Out output to " + name + " (" + outMode 
               ///    + ") not allowed.\n * No file set.");
           return false;
        }
        outTW.setOut1(os, encoding);
        outTW.noExplFlush1 = true;
        outDat = name; 
        if (logOnLog)
           log.println(AppLangMap.formMessageUL("outfilrp", null, outDat));
                   //// " * Out output set to " + outDat + " .");
        return true;
     } // setOutDat(...

/** Set some properties by a PropMap object. <br />
 *  <br />
 *  If prop is null nothing happens.<br />
 *  <br />
 *  Otherwise the following properties are taken (if given) from the  
 *  {@link PropMap} object:<br />
 *  {@link #logDat}, {@link #outDat}.<br /> 
 *  <br />
 */
   public void set(PropMap prop){ 
      if (prop == null) return;
      logDat  = prop.getString("logDat", logDat);
      outDat  = prop.getString("outDat", outDat);
   } // set(PropMap)
  
   
//---  Constructor   -------------------------------------------------------
   
//   private static volatile AppIO me; 
   
/** Make with settings. <br />
 *  <br />
 *  An AppIO object is made with setting the buffer sizes of both 
 *  {@link TeeWriter}s {@link #logTW} and {@link #outTW} as well as the 
 *  console I/O's encoding.<br />
 *  <br />
 *  {@link #outTW}'s output 2 remains unconnected as well as 
 *  {@link AppIO#errTW}'s output 1.<br />
 *  <br />
 *  @param outBuffLen buffer size  out TeeWriter, 
 *         1024 to 400000 character, default 20K
 *  @param logBuffLen buffer size  log TeeWriter, 
 *         1024 to 400000 character, default 10K
 *  @param consEncoding 
 *            null: {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING};<br />
 *         an other AppIO object: take its settings;<br />
 *         String: names the character encoding for console I/O,  
 *         &quot;defaultEncoding&quot; or naming a non implemented encoding
 *         uses the (file) default encoding.
 */
  public static AppIO get(final int outBuffLen,
                             final int logBuffLen, final Object consEncoding){
    return new AppIO(outBuffLen, logBuffLen, consEncoding); 
  } // get(2*int, Object)

/** Private constructor with settings. <br />
 *  <br />
 *  The only constructor is made private to allow for making AppIO singleton.
 *  From the beginnings (2000) until now it is not, to allow for different
 *  {@link de.frame4j.util.App App} inheritors running in the same JVM using
 *  different {@link #log log} objects with different 
 *  {@link #logfile log} files. <br />
 *  Implementation hint: When dropping such (past) use cases and switching
 *  to singleton make {@link #get(int, int, Object)} synchronized. 
 *  @see #get(int, int, Object) get()
 */
   private AppIO(int outBuffLen, int logBuffLen, Object consEncoding){
      boolean defEnc = false;
      String cEc = null;
      AppIO  apE = consEncoding instanceof AppIO ? (AppIO)consEncoding : null;
      if (apE != null) {
         esw = apE.esw;
         isr = apE.isr;
         osw = apE.osw;
      } else if (consEncoding instanceof String) {
         cEc = (String)consEncoding;
         if (cEc.length() < 1)
            cEc = CONSOL_ENCODING;
         else
            defEnc = TextHelper.areEqual("defaultEncoding", cEc, true);
      } else { // null or wrong type
         cEc = CONSOL_ENCODING;
      }

      if (!defEnc && cEc != null && apE == null) try {
         esw = new OutputStreamWriter(System.err, cEc);
         isr = new InputStreamReader(System.in, cEc);
         osw = new OutputStreamWriter(System.out, cEc);
      }  catch (UnsupportedEncodingException e) {
         defEnc = true; 
      }
      if (defEnc) {
         esw = new OutputStreamWriter(System.err);
         osw = new OutputStreamWriter(System.out);
         isr = new InputStreamReader(System.in);
      }
      
      //-- the final ins ---
      swr = new SwitchedReader(isr); // the switched Reader for standard in
      in  = new BufferedReader(swr); // the decorating BufferedReader    in
       
      errTW = new TeeWriter(logBuffLen, 4400);
      errTW.setOut2(esw);
      err = new PrintWriter(errTW, true);   // the PrintWriter   err

      if (outBuffLen < 1024 || outBuffLen > 400000)
         outBuffLen = 20480;
      if (logBuffLen < 1024 || logBuffLen > 400000)
         logBuffLen = 10240;

      outTW = new TeeWriter(outBuffLen, 56);
      if (apE == null)
         outTW.setOut1(osw );
      else
         outTW.setOut1(apE.outTW);
     
      out = outTW.getPrintWriter(true);  // the out TeeWriter's PrintWriter

      logTW = new TeeWriter(logBuffLen, 4400);
      logTW.setOut2(outTW).noExplFlush1 = true;
      log   = logTW.getPrintWriter(true);  // the log TeeWriter's PrintWriter
      outTW.setOut2((Writer) null);  ////   textPanelWriter);
      outTW.noExplFlush2 = true;
   } // AppIO(....)

/** Make with default settings.
 *  
 *  If a (singleton) AppIO object exists it is returned. Otherwise it
 *  is made  with basic default settings.<br />
 *  For console I/O  {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING} is used 
 *  and the platforms default file encoding for all else.<br />
 */
   public static AppIO get(){ return get(0, 0, null); }

//---  methods ------------------------------------------------------


/** Set log's second writer as stream. <br />
 *  <br />
 *  If there is / was a second writer already it is cleared (flush()) and 
 *  closed (close()).<br />
 *  {@code os2} will be decorated by a Writer and set as {@link #log}'s 
 *  second output.<br />
 *  <br />
 *  Internal implementation hint: it is {@link #logTW}'s out1.<br />
 *  <br />
 *  @param os2 The stream to connect to
 *  @param cp2 The encoding to use; null will be 
 *                            {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING}
 */
   public void setLogOut2(final OutputStream os2, final String cp2){
      this.cp2 = cp2; 
      logTW.setOut1(os2, cp2);
   } // setLogOut2(OutputStream, String)

/** Set log's second writer as writer. <br />
 *  <br />
 *  If there is / was a second writer already it is cleared (flush()) and 
 *  closed (close()).<br />
 *  {@code wr2} will be set as {@link #log}'s second output.<br />
 *  <br />
 *  Internal implementation hint: it is {@link #logTW}'s out1.<br />
 *  <br />
 *  @param wr2 the Writer to connect to
 */
   public void setLogOut2(final Writer wr2){
      logTW.setOut1(wr2);
      cp2 = null;
   } // setLogOut2(Writer)


/** Set normal out's code page (character encoding). <br />
 *  <br />
 *  The TeeWriter's {@link #outTW} encoding for branch normal output will be
 *  changed.<br />
 *  If {@code codePage} is empty 
 *  {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING} is used.<br />
 *  <br />
 *  @param  codePage encoding to set
 *  @return true on success
 */
   public boolean setOutCodePage(final CharSequence codePage){
      String cp = TextHelper.trimUq(codePage, CONSOL_ENCODING);
      if (cp.equals(osw.getEncoding())) return true;

      synchronized (outTW) {
         /////    log.flush();  /// 28.08.2002

         try {
            boolean wasSysOut =  outTW.out1 == osw;
            osw = new OutputStreamWriter(System.out, cp);
            if (wasSysOut) {  //  22.10.2003 :  no off for other outputs
               outTW.forceFlush1();   //// 28.08.2002
               outTW.switchOut1(osw);
            }
            return true;
         }  catch (UnsupportedEncodingException e) { } //  do nothing
      }
      return false;
   } //  setOutCodePage(CharSequence)
 

/** Set PrintWriter err's code page (character encoding). <br />
 *  <br />
 *  The TeeWriter's {@link #errTW} encoding for branch normal output will be
 *  changed.<br />
 *  If {@code codePage} is empty 
 *  {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING} is used.<br />
 *  @param  codePage encoding to set
 *  @return true on success
 */
   public boolean setErrCodePage(final CharSequence codePage){
      String cp = TextHelper.trimUq(codePage, CONSOL_ENCODING);
      if (cp.equals(esw.getEncoding())) return true;
      synchronized (errTW) {
         err.flush();
         try {
            esw = new OutputStreamWriter(System.err, cp);
            errTW.setOut2(esw);       // autoflush
            return true;
         }  catch (UnsupportedEncodingException e) {} //  do nothing
      }
      return false;
   } // setErrCodePage(CharSequence) 

//----------------------------------------------------------------------------

/** Set BufferedReader in's code page (character encoding). <br />
 *  <br />
 *  The input's {@link #in} (normal input) encoding will be changed.<br />
 *  If {@code codePage} is empty 
 *  {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING} is used.<br />
 *  <br />
 *  @param  codePage encoding to set
 *  @return true on success
 */
   public boolean setInCodePage(final CharSequence codePage){
      final String cp = TextHelper.trimUq(codePage, CONSOL_ENCODING);
      if (cp.equals(isr.getEncoding())) return true;
      synchronized (swr) {
         try {
            isr = new InputStreamReader(System.in, cp);
            swr.switchInput(isr, false);
            return true;
         }   catch (UnsupportedEncodingException e) { //  do nothing 
         } catch (IOException e) {} //  do nothing either (will not happen)
      } // sync publish isr
      return false;
   } // setInCodePage(CharSequence) 

//---------------------------------------------------------------------------

/** Code-Page for Reader in and the Writer out and err. <br />
 *  <br />
 *  @see #getCodePage()
 */
   protected volatile String codePage;


/** Code-Page for Reader in and the Writer out and err. <br />
 *  <br />
 *  If the character encoding for Reader in and the Writer out and err are
 *  commonly set by {@link #setCodePages(CharSequence)} the setting is provided 
 *  here for reference. null means no such or failed setting attempt.<br />
 *  <br />
 */
   public String getCodePage(){ return codePage; }



/** Set code-Page for Reader in and the Writer out and err. <br />
 *  <br />
 *  If {@code codePage} is empty 
 *  {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING} is used.<br />
 *  <br />
 *  @param  codePage encoding to set
 *  @return true on success
 *  @see #setOutCodePage setOutCodePage()
 *  @see #setErrCodePage setErrCodePage()
 *  @see #setInCodePage setInCodePage()
 */
   public boolean setCodePages(final CharSequence  codePage){
      final String cp = TextHelper.trimUq(codePage, CONSOL_ENCODING);

      boolean ret = setInCodePage(cp) & setOutCodePage(cp)
             & setErrCodePage(cp);
      this.codePage = ret ? cp : null;
      
      return ret;       
   } // setInCodePages(CharSequence) 

//---------------------------------------------------------------------------


/** Output only a not empty String as line. <br />
 *  <br />
 *  The parameter s will be passed via {@link #out}.println(), if and only if
 *  if is not empty.<br />
 *  <br />
 *  @param  s the character sequence to output to {@link #out} 
 *  @return Writer {@link #out} 
 */
   public PrintWriter toOutln(final CharSequence s){
      if (s != null && s.length() > 0) out.println(s.toString());
      return out;
   } // toOutln(CharSequence)

//---------------------------------------------------------------------------


/** Connect the TeeWriter's outputs for log and out by a PropMap. <br />
 *  <br />
 *  If properties logDat and / or outDat are file names the will be used and 
 *  be set as extra branches for the {@link TeeWriter}s 
 *  {@link #logTW} respectively {@link #outTW} using the appropriate 
 *  connect..() methods.<br />
 *  <br />
 *  If one of these connections fails (due to reject or file problems) this
 *  is signalled by return 1 (logDat) respectively return 2 (outDat). Success
 *  returns 0.<br />
 *  <br />
 *  Besides being the source for the names outDot and logDat {@code prop}
 *  serves to set the properties of the graphical &quot;file ask window&quot;
 *  if that could be needed (askGraf true) (see
 *  {@link FileVisitor.AskGrafImpl}).<br />
 *  <br />
 *  @param prop must not be null 
 *  @param outMode null acts like {@link OutMode#ASK}
 */
   public int connect(OutMode outMode, PropMap prop, boolean askGraf){ 
 
      outDat = prop.getString("outDat", null);
      if (outDat == null || "System.out".equals(outDat) ) {
         outDat = null;  //  no explicit out file
      } else if ("null".equals(outDat)) {
         outTW.close1();  //  no normal out by out 
         outDat = null;   //  no explicit out file
      } else if (outDat.equals(logDat)) {
         logDat = null;   //  two times into the same file makes no sense
      }
      
      if (logDat != null || outDat != null) { // explicit file names given
         if (outMode == null) outMode = OutMode.ASK;   // AskOverwrite
         if (askGraf && outMode == OutMode.ASK) { 
            // todo has this to be here ???
           Object parent = null;
           // AppBase appBase = AppBase.getAppBase();
           // Object parent = appBase == null ? null : appBase.baseApp;
           FileHelper.askOverwrite =  // this is stupid modify a static
               new FileVisitor.AskGrafImpl(false, prop, parent , false);
         }
         if (logDat != null) { // Log-File
            logFile =  FileHelper.getInstance(logDat);
            FileHelper.OS os   =  FileHelper.makeOS(logFile, outMode);
            if (os == null) {
               log.println(
                 " * Log output " + logDat + " (" + outMode 
                 + ") not allowed.\n * No log file set.");
               logFile = null;
               return 1;
            }
            logTW.setOut1(os, "defaultEncoding");
            logTW.noExplFlush1 = true;
         } // log-File

         if (outDat != null) { // Out-File
            outFile =  FileHelper.getInstance(outDat);
            FileHelper.OS os   =  FileHelper.makeOS(outFile, outMode);
            if (os == null) {
               log.println(
                 " * out output " + outDat + " (" + outMode 
                 + ") not allowed.\n * no out file set.");
               outFile = null;
               return 2;
            }
            outTW.setOut1(os, "defaultEncoding");
            outTW.noExplFlush1 = true;
         } // Out-File
      } //  explicit file names given

      if (outDat == null) outDat = "System.out";
      return 0; 
   } // connect(OutMode,..

} // class AppIO (24.04.2003, 31.12.2004, 22.05.2009, 06.01.2016)