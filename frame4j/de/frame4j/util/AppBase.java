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

import java.io.PrintWriter;
import java.time.Instant;

//import javax.management.Attribute;
//import javax.management.AttributeList;
// import javax.management.AttributeNotFoundException;
//import javax.management.InvalidAttributeValueException;
//  import javax.management.MBeanException;

import de.frame4j.io.AppIO;
//import de.frame4j.text.TextHelper;
import de.frame4j.time.SynClock;

/** <b>Basic common services for Java applications</b>. <br />
 *  <br />
 *  An object of this class is / should be a singleton within a JVM shared by
 *  all applications etc. running. It provides a substantial part of Frame4J's
 *  powerful infrastructure as well as some services.<br />
 *  <br />
 *  Java applications extending {@link App} automatically have a common
 *  {@link AppBase} object. It provides, also by an associated 
 *  {@link de.frame4j.io.AppIO} object, the following services and 
 *  utilities:<ul>
 *  <li> a PrintWriter ({@link AppIO#out}) for normal output,</li>
 *  <li> a PrintWriter ({@link AppIO#err}) for error messages</li>
 *  <li> a PrintWriter ({@link AppIO#log}) with optional branching to 
 *       normal output and a file,</li>
 *  <li> a BufferedReader ({@link AppIO#in}) for normal input,</li>
 *  <li> the handling of ever recurring I/O tasks,</li>
 *  <li> shutdown and start of applications,</li>
 *  <li> some time-keeping support and </li>
 *  </ul>
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2002 - 2003 &nbsp; Albrecht Weinert<br />
 *  <br /> 
 *  @see de.frame4j.io.AppIO
 *  @see de.frame4j.io.TeeWriter
 *  @see App
 */
 // so far:   V00.00 (25.04.2002) : moved out of App / WinApp 
 //           V02.03 (26.04.2003) : JavaDoc(1.4.2beta) bug  
 //           V02.06 (12.06.2003) : NOT_WINDOWS allows negative retCode
 //           V02.08 (29.09.2003) : JMX support
 //           V02.09 (08.11.2003) : shutdown improved
 //           V.o57+ (02.02.2009) : SVN, Frame4J
 //           V.154  (01.09.2009) : exit Error; formerly masked by < J6
 //           V.159+ (28.10.2009) : exit killJVM handling improved
 //           V.167+ (05.08.2016) : refactored to Frame4J'89 slimline: JMX out
 //           V.  38 (16.04.2021) : cosmetic repair

@MinDoc(
   copyright = "Copyright 2004 - 2009, 2015  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 38 $",
   lastModified   = "$Date: 2021-04-16 19:38:01 +0200 (Fr, 16 Apr 2021) $",
//// lastModifiedBy = "$Author: albrecht $",
   usage   = "Hold a singleton object",  
   purpose = "Basic services for applications"
) public final class AppBase implements ComVar {

/** The generating application object / 
 *                                the &quot;mother&quot; application.<br />
 *  <br />
 *  Normally this will be of type / inheritor of 
 *  {@link de.frame4j.util.App de.frame4j.util.App}, which is then signalled 
 *  by {@link #isBaseApp} being true.<br />
 *  <br >
 *  {@code baseApp} is the first requester for which this singleton / common
 *  infrastructure object was made.<br />
 *  <br /> 
 *  @see #getAppBase(Object, int, int, String)
 *  @see #getAppBase(Object, String)
 *  @see #getAppBase()
 *  @see #isBaseApp
 */
   public final Object baseApp; 
   
/** The one common AppIO object.  */   
   public final AppIO baseAppIO; 

/** The generating application object is of type App. <br />
 *  <br />
 *  @see de.frame4j.util.App
 *  @see #baseApp
 */
   public final boolean isBaseApp; 

//--------------------------------------------------------------------------

/** Common flag: the applications shall / might run on. <br />
 *  <br />
 *  This flag remains true, as long as there is a (mother) application
 *  {@link #baseApp} that is running (or shall do so).<br />
 *  <br /> 
 *  If set to false (e.g. by mother application's 
 *  {@link App#errorExit(int, String) errorExit()} or 
 *  {@link App#normalExit(int) normalExit()}), this has to be used by all
 *  running threads and plug-ins as a &quot;stop ASAP!&quot; signal.<br />
 *  <br />
 *  In the spirit of this contract calling one of those exit methods
 *  may lead to this flag and the mother application's ({@link #baseApp})
 *  {@link App#runFlag runFlag} being reciprocative reset.<br />
 *  <br />
 *  Between resetting this flag and killing the JVM a grace period of
 *  {@link #killJVMdelay} milliseconds will interleave, to allow all threads
 *  applications and plug-ins to end well-behaved.<br />
 *  <br />
 *  @see App#isRunFlag()
 *  @return the current state of the common run flag
 */
   public static boolean isCommonRun(){
      if (!commonRun) return false;
       if (appBase != null) {
          if (appBase.baseApp instanceof App)
             commonRun &= (((App)(appBase.baseApp)).runFlag &= commonRun);
       } else
          return false;
       return commonRun;
   } // isCommonRun()

/** Common flag, all applications shall run. <br />
 *  <br />
 *  Initial value: true
 */
   static volatile boolean commonRun = true;

/** Delay before stopping JVM in ms. <br />
 *  <br />
 *  This is the grace period between the mother application's 
 *  {@link #errorExit errorExit()} or 
 *  {@link #normalExit normalExit()} and the killing of the JVM.<br />
 *  <br />
 *  Allowed values are in the range of 50 to 35000 (0,05 .. 35 s), otherwise
 *  there's no such grace period.<br />
 *  <br />
 *  Hint: The waiting time is done in a so called shutdown hook which in
 *  turn resets the run flags (which normally is already done). This 
 *  proceeding may give the usable grace period even in cases of external
 *  control-C or platform shutdown.<br />
 *  <br />
 *  default: 64
 *  @see #isCommonRun
 */
   public volatile int killJVMdelay = 64; 


/** Error exit. <br />
 *  <br />
 *  The flag {@link #isCommonRun commonRun} will be reset (to false) at 
 *  once.<br />
 *  <br />
 *  If the parameter {@code theApp} is of type {@link App} the following will
 *  be done:<ul> 
 *  <li>{@code theApp}'s {@link App#runFlag runFlag} will be reset to false.<br />
 *  If {@code errNum} is not 0 or {@code errText} not empty, a multi-line
 *  report will be generated (by {@link App#errMeld(int, Object) errMeld()})
 *  and output on {@code log}.</li>
 *  <li>If {@code errNum} is 
 *  &gt;= {@link App}.{@link App#INIT_ERROR INIT_ERROR} the text
 *  {@link App#getUsage() getUsage()} will be output to
 *  {@link #baseAppIO baseAppIO.log}.</li></ul>
 * 
 *  If the parameter {@code theApp} is the base or mother application
 *  the following will be done:<ul> 
 *  <li>Is  {@link #killJVMdelay} in the allowed range (0,1 .. 30 s), the 
 *  caller (the calling thread) will be delayed by this grace period.</li>
 *  <li>Afterwards the caller and all (i.e. the JVM) will be killed
 *  by System.exit(errNum). If errNum &lt; 0 and not 
 *  {@link ComVar#NOT_WINDOWS} (i.e. probably Windows), System.exit(-errNum)
 *  is used, as negative process return codes are immoral there.</li></ul>
 *  <br />
 *  This means this method, if called by the mother application 
 *  {@link #baseApp} or if called on {@link #isCommonRun commonRun} already
 *  false, will not return to the caller. In this sense it's like
 *  calling System.exit(), that no other framework based software ever 
 *  shall do (!).<br />
 *  <br />
 *  In the other cases this method returns normally, but the application
 *  code should have done all its clean up anyway before.<br />
 *  <br />
 *  @param theApp the calling application (may be null).
 *  @param errNum  &nbsp; !=0 : error (or special case) code
 *  @param errText    supplemental (error) text
 */
   public void errorExit(Object theApp, final int errNum, String errText){
       exitImpl(theApp, errNum, errText, true);
   } // errorExit(Object, int, String)

/** Normal or failure caused end of an application. */
   void exitImpl(final Object theApp, 
                 final int errNum, final String errText,
                 final boolean error) {
      PrintWriter meld = baseAppIO.log;
      if (theApp == baseApp)  commonRun  = false;
      
      if (theApp instanceof App) {
         App app = (App)theApp;
         app.runFlag = false;
         meld = app.log;
         if (app.isDebug()) {
            meld.println("  ///   DEBUG exitImpl " + errNum
                           + (error ? " by errorExit\n" : "  normal exit\n"));
         } 
         if (error && (errNum != 0 || errText != null && errText.isEmpty())) {
            meld.println(app.errorText(errNum, errText));
            meld.println();
            if (errNum >= App.INIT_ERROR) {
               String tmp = app.getUsage();
               if (tmp != null) {
                  meld.println(tmp);
                  meld.println();
               }
            }
            meld.flush();
         }
      } //  else if (theApp instanceof blah) { }
      
      // new 12.04.2021
      baseAppIO.log.flush();
      baseAppIO.out.flush();
      baseAppIO.err.flush();
      try { Thread.sleep(8);} catch (Exception ex) {} 

      if (!commonRun) { // System exit
         Thread kill2 = new Thread(){
            @Override public void run() {
               int delay = killJVMdelay;
               if (delay < 50) delay = 50;
               else if (delay > 30000) delay = 30000;
               AppHelper.sleep(delay);
               RUNTIME.exit(errNum >= 0 || NOT_WINDOWS ? errNum : -errNum);
            }
         };
         //   kill2.setDaemon(true);
         kill2.start();
      } // System exit
      return;
   } // exitImpl(Object, int, String, boolean)

/** Normal exit. <br />
 *  <br />
 *  If the application to end is the base or mother application
 *  the returnCode will be the common JVM process return value for all
 *  applications or plug-ins. This value can be processed as process 
 *  (java.exe) exit code for the platform. In the case of DOS-Win32 
 *  the rules are:<br />
 *  0&nbsp; &nbsp;= &nbsp; O.K.<br />
 *  &gt;0      = &nbsp; special case or error (the higher the worse)<br />
 *  <br />
 *  In scripts (batch or command files) this value can be evaluated by
 *   &quot;IF ERRORLEVEL n&quot;. Negative return codes used sometimes by
 *  Unix applications should be avoided for sake of platform independence. So
 *  some framework exit methods (including this one) suppress negative return 
 *  codes; see 
 *  {@link #errorExit(Object, int, String)}.<br />
 *  <br />
 *  @param theApp the calling application (may be null).
 *  @param returnCode &nbsp; !=0 : error or special case code
 */
   public void normalExit(final Object theApp, final int returnCode){
       exitImpl(theApp, returnCode, null, false);
   } // normalExit(Object, int)

//-------------------------------------------------------------------------

// Hint: &#88; is X 
//       Three adjacent X could form an Eclipse task tag (not meant here).
 
/** Static Exit &mdash; helper method. <br />
 *  <br />
 *  This method is the framework's substitute for System.exit() and the like.
 *  No Frame4J based / Frame4J using software should ever use murder
 *  methods ( System.exit() and consorts) directly.<br />
 *  <br />
 *  If (still) in a static context, e.g. in the main(String[] ) or in static
 *  initialiser's exception handling, this is the method of choice.<br />
 *  <br />
 *  In all other cases the {@link App}'s or the {@link AppBase} object's exit
 *  methods are to be used.<br />
 *  <br /> 
 *  On one hand this method supports the handling of an Exception comfortably.
 *  On the other hand another (mother) application(s) that may be already
 *  running under the framework will be saved from the disastrous effects of
 *  System.exit().<br />
 *  <br />
 *  To repeat it clearly: No code using this framework Frame4J must call
 *  System.exit() and the like under whatever circumstances. Period.<br />
 *  <br />
 *  For the output described in the following {@link #baseAppIO BaseAppIO.log}
 *  is used, if that is available and else System.err:<ul>
 *  <li>If the Exception {@code excp} exists and errNum is not 0, the 
 *      text<br />
 *      &quot; *** classname Error errNum X&#88;X&quot;<br />
 *      is output.<br />
 *  <li>For a given Exception {@code excp} the call stack is output in the
 *      case of a RunTimeException but no IllegalArgumentException. (If this
 *      condition holds, there's often a programming error where the call 
 *      stack is helpful.)</li>
 *  <li>For another given Exception {@code excp} only the message text is
 *      output. If that is empty, the stack trace is output.</li>
 *  <li>If the above mentioned other running (mother) applications do not
 *      exist and run, an  errNum != 0 will be forwarded by (something like)
 *      System.exit(errNum). If the platform is not Windows a negative number
 *      will be forwarded as is, otherwise the absolute value is taken.<br />
 *      With other applications or plug-ins still running this method returns
 *      normally to the caller, that should then end its thread by leaving
 *      static main(), run(), doIt() or whatever the type's starting or 
 *      working method may be be.<br />
 *      In such cases (return to the caller) the report text (part)
 *      <br />
 *       &quot; *** className Error errNum X&#88;X&quot;<br />
 *      will be replaced by <br />
 *       &quot; *** className Error errNum ***&quot;.</li></ul>
 *  <br />
 *  Hint: Very clever JIT-Compilers and certain optimisations may reduce
 *  the information contained in the generated report texts.<br />
 * 
 *  @param excp   the given exception (or null).
 *  @param errNum  &gt;0 : the error number
 */
   public static void exit(final Exception excp, final int errNum){
       boolean baseExist = appBase != null;
       boolean willKillJVM = true;
       String className = null;
       Exception fClass = excp != null ? excp : new Exception(); 
       try {
          StackTraceElement[] stack =  fClass.getStackTrace();
          if (stack != null && stack.length > 0)
             className = stack[stack.length-1].getClassName();
          final String baseClassName = appBase.baseApp.getClass().getName();
          if (baseExist && className != null && baseClassName != null
               && ! ( baseClassName.equals(className) || 
                      baseClassName.endsWith('.' + className)))
             willKillJVM = false;
       } catch (Exception e){}
       PrintWriter logerr = null;
       if (baseExist)logerr = appBase.baseAppIO.log; 
       if (logerr == null) logerr = new PrintWriter(System.err);  
       if (errNum != 0 && className != null) {
          String tmp = "\n  ***  " + className + "  Error " + errNum;
           //+ (willKillJVM ? " #vm" : " *vm"); // nice but incomprehensible
          logerr.println(tmp);
       }
       if (excp != null) {
          String tmp = excp.getMessage();
          if (tmp == null || tmp.isEmpty()
             ||  excp instanceof RuntimeException 
                 && !(excp instanceof IllegalArgumentException) ) {
             excp.printStackTrace(logerr);
          } else { 
             logerr.println(tmp);
          }
       } // excp != null
       if (willKillJVM) {
          if (errNum >= 0 || NOT_WINDOWS) System.exit(errNum);
          System.exit(-errNum); 
       }
   } // exit(Exception, int)


/** The only (private) Constructor (singleton). */
   private AppBase(final Object baseApp, final int outBuffLen, 
                                final int logBuffLen, final String codePage){
      baseAppIO = AppIO.get(outBuffLen, logBuffLen, codePage);
      this.baseApp = baseApp;
      isBaseApp    = baseApp instanceof App;
      RUNTIME.addShutdownHook(new Thread(){
         @Override public void run(){ shutDownHook(); }
      });
      appBase = this;
   }  //  AppBase()

/** The one (singleton) AppBase object. */
   private static volatile AppBase appBase;

/** JVV is dying / shutdown hook. <br /> */
   private void shutDownHook(){
      long reldel = System.currentTimeMillis();
 ///     baseAppIO.log.println("TEST before killDelay " );
      commonRun = false;
      int delay = killJVMdelay;
      if (delay < 166 || delay > 30000) delay = 0;
      if (baseApp instanceof App && ((App)baseApp).runFlag) { // non null app
         ((App)baseApp).stop();
         if ( ((App)baseApp).getMyFrame() != null && delay < 970) 
            delay = 980; // give graphical Apps threads a fair chance
         if (delay < 50) delay = 64; // give App a chance       
      }  // non null app was running
      
      AppHelper.sleep(delay);
      
      baseAppIO.logTW.noExplFlush1 = false;
      baseAppIO.outTW.noExplFlush2 = false;
      reldel =  System.currentTimeMillis() - reldel;
  ///    baseAppIO.log.println("TEST after killDelay " + reldel + " ms");
      baseAppIO.log.flush();
      baseAppIO.log.close();
      baseAppIO.out.close();
      AppHelper.sleep(12);
   } // shutdownHook

//------------------------------------------------------------------------   

/** Graphical question on files instead of console question. <br />
 *  <br />
 *  Is askGraf true questions about how to deal with existing files will be 
 *  asked in a little window, otherwise on the console.<br />
 *  <br />
 *  default: false
 */
   public volatile boolean askGraf;


//-------------------------------------------------------------------------

/** The one /singleton) AppBase object. <br />
 *  <br />
 *  This method returns the (maximal one singleton) AppBase object, if it was 
 *  already constructed elsewhere.<br />
 *  <br />
 *  Otherwise null is returned.<br />
 *  <br />
 *  @see #getAppBase(Object, String)
 *  @see #getAppBase(Object, int, int, String)
 *  @return the single AppBase
 */
   public static final AppBase getAppBase(){ return appBase; }

/** Fetching or making the one AppBase object. <br />
 *  <br />
 *  This method returns the (singleton) AppBase object unchanged, if it was 
 *  already constructed elsewhere. In this case the  caller's parameters are 
 *  all irrelevant.<br />
 *  <br />
 *  Otherwise the object will be constructed according to the parameters and
 *  returned. Illegal parameter values are punished by an
 *  {@link IllegalArgumentException}.<br />
 *  <br />
 *  @param baseApp The (first) using application; it will be normally of type
 *                 {@link App}, must not be null   
 *  @param  codePage The code page for in, out and err (oder null)
 *  @return the single AppBase
 *  @see #getAppIO(Object, int, int, String)
 *  @throws IllegalArgumentException if any used parameter values are illegal
 */
   public static synchronized AppBase getAppBase(final Object baseApp,
                     final String codePage) throws IllegalArgumentException {
      return getAppBase(baseApp, 0, 0, codePage);
   } //  getAppBase(Object

/** Fetching or making the one AppBase object. <br />
 *  <br />
 *  This method returns the (singleton) AppBase object unchanged, if it was 
 *  already constructed elsewhere. In this case the caller's parameters are 
 *  all irrelevant.<br />
 *  <br />
 *  Otherwise the object will be constructed according to the parameters and
 *  returned. Illegal parameter values are punished by an
 *  {@link IllegalArgumentException}.<br />
 *  <br />
 *  @param baseApp The (first) using application; it will be normally of type
 *                 {@link App}, must not be null
 *  @param outBuffLen the out TeeWriter's buffer size, 
 *         1024 to 400000 characters, default 20K
 *  @param logBuffLen the log TeeWriter's buffer size, 
 *         1024 to 400000 characters, default 10K
 *  @param codePage encoding for console I/O, null will act as  
 *         {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING}, 
 *         &quot;defaultEncoding&quot; or specifying unimplemented encodings
 *         will default to the system's file encoding 
 *         ({@link ComVar#FILE_ENCODING}) 
 *  @throws IllegalArgumentException if any used parameter values are illegal
 *  @return the single AppBase
 */
   public static synchronized AppBase getAppBase(final Object baseApp, 
             final int outBuffLen, final int logBuffLen,
                     final String codePage) throws IllegalArgumentException {
      if (appBase != null) return appBase;
      if (baseApp == null) 
         throw new IllegalArgumentException("No AppBase without baseApp");
      new AppBase(baseApp, outBuffLen, logBuffLen, codePage);
      return appBase;
   } // getAppBase(Object

/** Fetching or making the one fitting AppIO object. <br />
 *  <br />
 *  This method returns an {@link de.frame4j.io.AppIO AppIO} object fitting
 *  for a given application. Applications of type {@link App} this method is
 *  used accordingly in  {@link App}'s constructors (as the one {@link AppIO}
 *  object associated with an {@link App} is final).<br />
 *  <br />
 *  If theApp is null, an Exception will occur.<br />
 *  <br />
 *  If theApp is the one base (or mother) application of this (also singleton) 
 *  AppBase object, the already made {@link de.frame4j.io.AppIO AppIO} object
 *  will be returned. All other parameters are then irrelevant.<br />
 *  <br />
 *  If theApp is of type {@link App}, the already associated
 *  {@link de.frame4j.io.AppIO AppIO} object is returned, if it is already
 *  made. (This will be so for all calls outside / after {@link App} 
 *  constructors). All other parameters are then irrelevant.<br />
 *  <br />
 *  If no already made and associated {@link de.frame4j.io.AppIO AppIO} object
 *  can be determined, one is made according to the parameters. The free 
 *  outputs of the log and out {@link de.frame4j.io.TeeWriter TeeWriter}s will
 *  be forwarded / connected to log and out of this AppBase object's 
 *  {@link de.frame4j.io.AppIO AppIO} object.<br />
 *  <br />
 *  @param theApp The using application; it will be normally of type
 *                 {@link App}, must not be null
 *  @param outBuffLen the out TeeWriter's buffer size, 
 *         1024 to 400000 characters, default 20K
 *  @param logBuffLen the log TeeWriter's buffer size, 
 *         1024 to 400000 characters, default 10K
 *  @param codePage encoding for console I/O, null will act as  
 *         {@link ComVar#CONSOL_ENCODING CONSOL_ENCODING}, 
 *         &quot;defaultEncoding&quot; or specifying unimplemented encodings
 *         will default to the system's file encoding 
 *         ({@link ComVar#FILE_ENCODING}) 
 *  @throws IllegalArgumentException if any used parameter values are illegal
 *  @return a fitting AppIO
 */
   public synchronized AppIO getAppIO(final Object theApp, 
                       final int outBuffLen, final int logBuffLen,
                     final String codePage) throws IllegalArgumentException {
      if (theApp == null) 
         throw new IllegalArgumentException("No AppIO without theApp");
      if (theApp == this.baseApp)   
          return baseAppIO;  
      if (theApp instanceof App && ((App)theApp).appIO != null)
           return ((App)theApp).appIO;
      return AppIO.get(outBuffLen, logBuffLen, baseAppIO);
   } // getAppIO(Object, 2*int, String)

//--------------------------------------------------------------------------

/** Update the commonly used actual (platform based) Time. <br />
 *  <br />
 *  The commonly used (actual) {@link SynClock#sys time (stamp)} will be 
 *  {@link SynClock#update() updated} to platform time and fetched. <br />
 *  <br />
 *  @return the updated time
 */
   public static final Instant setActTime(){ 
      return SynClock.sys.setInstant();
   } // setActTime()
       

/** Delay the applications running by waiting. <br />
 *  <br />
 *  This method's effect is essentially that of the method
 *  {@link java.lang.Thread Thread.sleep(long)} (used). <br />
 *  <br />
 *  This method throws no exception. Interrupted wait sleep is instead
 *  signalled via the return value.<br />
 *  <br />
 *  The waiting / sleeping time is given in ms (milliseconds). The resolution
 *  may on some operating systems more coarse.<br />
 *  To use longer times one may use the constant values of the interface
 *  {@link ComVar de.frame4j.util.ComVar}.<br />
 *  <br />
 *  @param  millis (&gt;0 !) the delay / sleep in ms
 *  @return true if the sleep was complete; false on interrupt (wake up)
 *  @see de.frame4j.util.AppHelper#sleep AppHelper.sleep()
 */
   static public boolean sleep(final long millis){
      if (millis <= 0) return true;
      try {
         Thread.sleep(millis);
         return true;
      } catch (Exception e) {
         return false;
      }
   } // sleep(long) 

//------------------------------------------------------------------------

/** Comfortable start of an application extending App. <br />
 *  <br />
 *  This method starts an {@link App} object. It may of course be used from
 *  an application extending {@link App App} itself. The started 
 *  {@link App App}lication's main thread ({@link App#doIt() doIt()}) does
 *  run in the thread of this method's caller.<br /> 
 *  <br />
 *  The return value is {@link App#NO_PARS_ERROR}, if {@code app} or
 *  {@code args} is null. It is {@link App#INIT_ERROR} on errors while 
 *  evaluating .properties files or the arguments supplied.<br />
 *  Otherwise this methods return value is that of the started 
 *  {@link App App}'s method {@code app}.{@link App#doIt() doIt()}.<br />
 *  <br />
 *  app.{@link App#verbose} and app.{@link App#help} will be set according to
 *  the &quot; mother application&quot; {@link #baseApp}. Besides that this
 *  methods effect is mostly that of:<br />
 *  {@link App#go(String[], String, CharSequence)
 *      app.go(args, false, null);}.<br />
 *  <br />
 *  <b>Hint</b>: Some applications extending {@link App} are written in a way
 *  that they require (default) initialising their object variables by 
 *  constructor or initialisers. This is of course perfectly in order. But 
 *  if that is so, the same object {@code app} may not be started multiple
 *  times by this method. In that case do not<br>
 *  &nbsp; &nbsp; App app = new myApp();<br />
 *  &nbsp; &nbsp; AppBase.start(app, args1);<br />
 *  &nbsp; &nbsp; AppBase.start(app, args2);<br />
 *  but do<br />
 *  &nbsp; &nbsp; AppBase.start(new myApp(), args1);<br />
 *  &nbsp; &nbsp; AppBase.start(new myApp(), args2);<br />
 *  <br />
 *  @param  app   the application to start
 *  @param  args  start parameter
 *  @see App#go(String[], String, CharSequence)
 *  @return {@link App#INIT_ERROR} etc.: start error, 0: 
 *          OK (return by doIt())
 */
   public static final int start(final App app, final String[] args){
      if (app == null || args == null) return App.NO_PARS_ERROR;
      app.args = args;
      try {
         app.prop = new Prop(app, null);
         app.mainThread = Thread.currentThread();
         int ret = app.doIt();  // overridden:  Die EIGENTLICHE Anwendung 
         if (ret != 0 || !app.runFlag || app.mainThread != null) {
            app.mainThread = null;
            appBase.normalExit(app, ret);
         } else {
            app.mainThread = null;   /// since 14.02.2006
         }
         return ret;
      }  catch (Exception e) {
         app.appBase.errorExit(app, App.INIT_ERROR, 
           app + " : Param. Eval. / introspection, start : " + e);
         return App.INIT_ERROR;   
      }
   } // start (App, String[]) 
       
} // class AppBase (10.12.2003, 26.02.2009, 25.06.2015, 16.04.2021)