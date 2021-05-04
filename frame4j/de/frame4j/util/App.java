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

import java.awt.AWTEvent;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
// import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Flushable;
import java.io.IOException;
//  import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
// import javax.management.DynamicMBean;
// import javax.management.MBeanException;
// import javax.management.MBeanInfo;
// import javax.management.ReflectionException;
import javax.swing.AbstractButton;

import de.frame4j.graf.ColorHelper;
import de.frame4j.graf.WeAutLogo;
import de.frame4j.io.AppIO;
import de.frame4j.io.LogWriterHandler;
import de.frame4j.io.OutMode;
import de.frame4j.io.TeeWriter;
import de.frame4j.net.AttrSettable;
import de.frame4j.text.TextHelper.MessageComponents;
import de.frame4j.text.TextHelper;
import de.frame4j.time.SynClock;
import de.frame4j.time.TimeHelper;

/** <b>The parent for Frame4J based Java applications</b>. <br />
 *  <br />
 *  A Java application written as an {@code App} inheritor gets a powerful 
 *  infrastructure and wide range of often needed abilities otherwise to be 
 *  programmed again and again. {@code App} by itself supports no Graphics or 
 *  GUIs, but is suitable for graphical applications.<br />
 *  <br /> 
 *  With {@code App} and the framework 
 *  <a href="../package-summary.html#package_description">Frame4J</a> 
 *  implementing Java applications is quite simplified. Ever recurring tasks
 *  are solved already, as among others<ul>
 *  <li> providing a PrintWriter ({@link #out}) for normal output,</li>
 *  <li> providing a PrintWriter ({@link #err}) for error messages</li>
 *  <li> providing a PrintWriter ({@link #log}) with optional branching to 
 *       normal output and a file,</li>
 *  <li> optional providing of a {@link #getAppLogger() logger} connected
 *       to {@link #log},</li>
 *  <li> providing a BufferedReader ({@link AppIO#in}) for normal input,</li>
 *  <li> handling and checking (parsing) of programme parameters,</li>    
 *  <li> handling of programme's properties and settings,</li>    
 *  <li> support for (inter) nationalising,</li>
 *  <li> handling of recurring input and output problems,</li>
 *  <li> concurrent execution of actions,</li>
 *  <li> handling of (on-line) short documentation or helps,</li>
 *  <li> supporting a role as JMX server.</li>
 *  </ul>
 *  An application to use {@code App} by inheritance may do so directly or 
 *  by an inheriting (simple) inner class just by adhering to an relatively 
 *  simple schema best explained by examples like HelloFrame4J
 *  or  SAXdemo. For the inner class scheme, see the end of 
 *  {@link AppLangMap}'s source as an example.<br />
 *  The schema in short is<ol>
 *  <li>In the application's start method (static main(String[] args) make
 *     the applications object by the default constructor and call one of 
 *     {@code App}'s {@link #go(String[]) go(...)} methods:<br /> 
 *     <code>new MyAppInheritor().go(args, ...)</code>;</li>
 *  <li>provide a simple  MyAppInheritor.properties text file in the same 
 *      directory or say you won't by overwriting 
 *      {@link #allowNoPropertiesFile()} with return true.<br />
 *      If you do the text file put the (nationalised) help texts as property
 *      helpText, de.helpText, en.helpText there.</li> 
 *  <li>implement the abstract method {@link #doIt()} to do the 
 *      real work of your application:<br />
 *      {@code public int doIt()}{<br />
 *      {@code log.prinln("Hi, I'm a sloth-bear."); return 0;}}</li>
 *  <li>(Type {@code java MyAppInheritor -?} and enjoy your help
 *       text.</li>
 *  </ol>  
 *  Between the call of {@link #go(String[]) go(...)} and the entry
 *  into {@link #doIt()} all the initialising, command line parsing work is 
 *  done and lot more &mdash; quite invisibly in the background.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 1997 - 2006, 2009  &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 43 $ ($Date: 2021-05-04 20:53:48 +0200 (Di, 04 Mai 2021) $
 *  @see de.frame4j.io.AppIO
 *  @see Prop
 *  @see #go(String[], String, CharSequence)
 */
 // so far  V.0.02 (13.09.1997) : new, no version control (yet) 
 //         V00.03 (08.05.2000) :  de.a_weinert instead of weinertBib
 //         V00.20 (13.12.2001) :  AppBase out / ext. Object
 //         V00.30 (27.06.2002) :  parsePartial(), getMinDoc()
 //         V01.50 (28.06.2002) :  unified with WinApp
 //         V02.00 (24.04.2003) :  CVS and Eclipse
 //         V02.03 (26.04.2003) :  JavaDoc bug (1.4.2beta) revealed
 //         V02.12 (16.07.2003) :  WinApp swingy
 //         V02.13 (01.10.2003) :  AppMBean.Adapter resolved
 //         V02.23 (08.07.2007) :  queueAction
 //         V02.29 (07.10.2004) :  allowNoPropertiesFile
 //         V02.33 (21.12.2004) :  International., Action. improved
 //         V02.53 (04.12.2005) :  Exit code constants
 //         V02.60 (04.08.2006) :  Logger support
 //         V02.68 (11.04.2007) :  JMX -> JConsole / 1.5
 //         V.107+ (26.04.2009) :  setBgColor error corrected
 //         V.173+ (10.01.2010) :  getMyFrame() to support graph.inh.
 //         V.011+ (03.02.2010) :  moved to Assembla due to Oracle-Sun
 //         V.o12+ (10.02.2010) :  handling of doIt() Exceptions 
 //         V.o20+ (12.02.2010) :  errorExit(int, ex , + int) now 3Par
 //         V.160+ (10.05.2016) :  Web Start compatibility
 //         V.167+ (05.08.2016) :  refactored to Frame4J'89 slimline
 //         V.  36 (07.04.2021) :  AppMBean instead of AppMBean.xml
 //         V.  41 (20.04.2021) :  implements+ ComVar (2 static imports before)

@MinDoc(
   copyright = "Copyright 1997 - 2016, 2021 A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 43 $",
   lastModified   = "$Date: 2021-05-04 20:53:48 +0200 (Di, 04 Mai 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application",  
   purpose = "Base class for powerful, robust and comfortable applications"
) public abstract class App implements ActionListener, AppMBean, ComVar,
                                   UIInfo, MessageComponents, AttrSettable {
   
/** The application's start up time. <br />
 *  <br />
 *  @see #getAppStartTime()
 */
    public final Instant appStartTime = SynClock.sys.setInstant();
    
/** The application's start up time in ms since 1.1.1970. */
    public final long appStartTimeMS = SynClock.sys.millis();
 
/** The application's start up time. <br />
 *  <br />
 *  @see #appStartTime
 *  @return the application's start time
 */
    public final Instant getAppStartTime(){ return appStartTime; }

/** The application's start up time as Text. <br />
 *  <br />
 *  Returns {@link #appStartTime} formatted according to 
 *  {@link AppLangMap}{@link
 *  AppLangMap#valueUL(CharSequence) .valueUL(&quot;wedaclock&quot;)}.<br />
 *  <br />
 *  Hint: The formatting is only done once, {@link #appStartTime} being
 *  final. Afterwards a stored String is returned (singleton).<br />
 *  <br />
 *  @return the application's start time
 */
   @Override public String getStartTime(){
      if (startUpTimeSingleton == null) {
         startUpTimeSingleton = TimeHelper.format(
                            AppLangMap.valueUL("wedaclock"), appStartTimeMS);
      }
      return startUpTimeSingleton;
   } //  String getStartAppTime()
   // To be exact: it does not matter if multiple threads do it separately. 
   private String startUpTimeSingleton;

/** Number of messageFormat components of this application. <br />
 *  <br />
 *  @see #getMessageComponent(int)     
 *  @return 29 if not overridden
 */
   @Override public int getMessageComponentsLength(){ return 29; }
      
/** Get a message component by its index. <br />
 *  <br />
 *  This method fetches the following information on this application:<br />
 *  [5] : {@link AppHelper#getActTime() actual Time} (formatted)<br />
 *  [7] : {@link #appStartTime start up Time}<br />
 *  [8] : {@link #getStartTime() start up Time} (formatted)<br />
 *  [9] : {@link #getExecTimeString() execution time} (duration formatted)<br />
 *  [10]: {@link #fullClassName} (full class name; by class not by command)<br />
 *  [11]: {@link #getName() name} (short class name; not by command as [24])<br />
 *  [12]: {@link #getTitle() title}<br />
 *  [13]: {@link #getAuthor() author}<br />
 *  [14]: {@link #getCopyright() copyright}<br />
 *  [15]: {@link #getVersDate() versDate}<br />
 *  [16]: {@link #getNameWithVersDate() nameWithVersDate}<br />
 *  [17]: {@link #getAbout() aboutText}<br />
 *  [18]: {@link #getPurpose() purpose}<br />
 *  [19]: {@link #getUsage() usage}<br />
 *  [20]: {@link #getHelp() help text}<br />
 *  [21]: {@link #getLanguage() user language set} (two letter)<br />
 *  [22]: {@link #getOutMode() how handle} output to existing files<br />
 *  [23]: {@link #getStateString()} (multi-line status text)<br />
 *  
 *  [24]: {@link ComVar#PROG_NAME} full program/class; by command, not class<br />
 *  [25]: {@link ComVar#PROG_SHORT} (short program name; by command<br />
 *  [26]: {@link ComVar#OS} (operating system's name, like Windows 10)<br />
 *  [27]: {@link ComVar#HOST_NAME} host's name if can be determined<br />
 *  [28]: {@link ComVar#ON_PI} true when running on a Pi<br />
 *  
 *  [27]: {@link ComVar#PROG_NAME} full program/class by command<br />
 *  [28] = [11]: {@link ComVar#PROG_SHORT} program short name like e.g. Exec<br />
 *  [29] : {@link ComVar#HOST_NAME} host's name if can be determined<br />
 *  [30] : {@link ComVar#OS} operating system's name<br />
 *  [31] : {@link ComVar#ON_PI} true when running on a Pi<br />

 *  [0..4]: reserved for user defined Object arrays to distinguish them by
 *         number from message components; returns null here
 *        overridden.<br />
 *  [27..]: null if this method and {@link #getMessageComponentsLength()}
 *          are not overridden to tell otherwise.     
 *  @param  index Range 0 .. {@link #getMessageComponentsLength()} - 1 
 *  @return the (actual) piece of information or null.  
 */
   @Override public Object getMessageComponent(final int index){
      if (index > 4 && index < 29) switch (index) {
         case 5:  return SynClock.sys.setInstant(); // actual time
         case 6:  return AppHelper.getActTime(); // formatted
         case 7:  return appStartTime;             // startup time
         case 8:  return getStartTime();          // formatted
         case 9:  return getExecTimeString();    // execution time formatted
         case 10: return fullClassName; // by class
         case 11: return getName();    // by class
         case 12: return getTitle();
         case 13: return getAuthor();
         case 14: return getCopyright();
         case 15: return getVersDate();
         case 16: return getNameWithVersDate();  // name + version etc
         case 17: return getAbout();
         case 18: return getPurpose();
         case 19: return getUsage();
         case 20: return getHelp();
         case 21: return getLanguage();
         case 22: return getOutMode();
         case 23: return getStateString();
         
         case 24: return ComVar.PROG_NAME; // program / command / class name
         case 25: return ComVar.PROG_SHORT; // program / command short name 
         case 26: return ComVar.HOST_NAME; // machine's name (if determinable)
         case 27: return ComVar.OS;       // OS name
         case 28: return ComVar.ON_PI ? Boolean.TRUE :  Boolean.FALSE;
      } // if switch
      return null;
   } // getMessageComponent(int) 
  
/** Two line standard start report (nationalised). <br />
 *  <br />
 *  returns e.g. for German, English, French:<br /><code><pre>
 *  
 *   ///   AppLangMap V2.10 (15.12.2004, A. Weinert)
 *   ///   Start:  Mi, 15.12.2004, 17:14:44
 * 
 *   ///   AppLangMap V2.10 (15.12.2004, A. Weinert)
 *   ///   start:  We, Dec. 15 2004, 17:13:42
 *  
 *   ///   AppLangMap V2.10 (15.12.2004, A. Weinert)
 *   ///   démarrage:  me, 15.12.2004, 17:14:20</pre></code>
 * 
 *   The first line is obtained by 
 *          {@link #getNameWithVersDate() getNameWithVersDate()}.<br />
 *  <br />
 *  @see AppLangMap
 *  @see TextHelper#messageFormat(StringBuilder, CharSequence, Object)
 *  @see #getMessageComponent(int)
 *  @return the report
 */
    public StringBuilder twoLineStartMsg(){
       StringBuilder startMeld = new StringBuilder(100);
       TextHelper.messageFormat(startMeld, valueLang("strt_pt", null), this);
       return startMeld;
    } // twoLineStartMsg()
    
/** Two line standard end report (nationalised). <br />
 *  <br />
 *  See the resembling {@link #twoLineStartMsg()}.<br />
 *  <br />
 *  @see #getMessageComponent(int)
 *  @return the report
 */
    public StringBuilder twoLineEndMsg(){
       StringBuilder endMeld = new StringBuilder(100);
       TextHelper.messageFormat(endMeld, valueLang("endt_pt", null), this);
       return endMeld;
    } // twoLineEndMsg()
    
 /** Two line standard end report (nationalised). <br />
  *  <br />
  *  See the resembling {@link #twoLineStartMsg()}
  *  and {@link #twoLineEndMsg()}.<br />
  *  The difference to  {@link #twoLineEndMsg()} is an extra line reporting
  *  the execution time.
  *  <br />
  *  @see #getMessageComponent(int)
  *  @see #getExecTimeMs()
  *  @see #getExecTimeString()
  *  @return the report
  */
   public StringBuilder threeLineEndMsg(){
     StringBuilder endMeld = new StringBuilder(100);
     TextHelper.messageFormat(endMeld, valueLang("end3_pt", null), this);
     return endMeld;
   } // twoLineEndMsg()

/** Get a value for a key &mdash; regarding set user language. <br />
 *  <br />
 *  This method tries (hard) to get an user language specific value for
 *  {@code key} taking {@link #getLanguage() language} and region into
 *  account searching in this ({@link App}) object's {@link #prop} 
 *  (properties) and in  {@link AppLangMap}.<br />
 *  <br />
 *  If all fails or key is empty {@code def} is returned.<br />
 *  <br />
 *  @see Prop#valueLang(CharSequence)
 *  @see AppLangMap#valueUL(CharSequence)
 *  @param key the key to get a value for
 *  @param def the default value if nothing can be found by key
 *  @return the key's or the default value
 */
   public final String valueLang(final CharSequence key, String def){
      if (prop != null) return prop.valueLang(key, def);
      if (key == null) return def;
      String ret = AppLangMap.valueUL(key);  // for prop cleared, only 
      return ret != null ? ret : def;
   } //  valueLang(CharSequence, String) 
   
/** Get a value for a key &mdash; regarding set user language. <br />
 *  <br />
 *  The call is equivalent to <br /> &nbsp;
 *  {@link #valueLang(CharSequence, String) valueLang(key, null)}.<br />
 *  <br />
 *  @see AppLangMap#valueUL(CharSequence)
 *  @see Prop#valueLang(CharSequence)
 *  @param key the key to get a value for
 *  @return the key's value or null if nothing coud be found for key
 */
   public final String valueLang(final CharSequence key){
      return valueLang(key, null);
   } // valueLang(CharSequence)
   
/** Get the short (2 character) notation of the user's language. <br />
 *  <br />
 *  If {@link #prop} is not (yet) null, 
 *  {@link #prop}.{@link Prop#getLanguage() getLanguage()} is returned.<br />
 *  Next choice is {@link AppLangMap}.{@link AppLangMap#getUMap() 
 *  getUMap()}.{@link AppLangMap#getLanguage() getLanguage()}, if available.
 *  Otherwise  {@link ComVar}.{@link ComVar#UL UL} is returned.<br />
 *  @return the language code (2 letter)
 */   
  @Override public final String getLanguage(){
      if (prop != null) return prop.getLanguage();
      AppLangMap tmp = AppLangMap.getUMap();
      if (tmp != null) return tmp.getLanguage();
      return ComVar.UL;      
   } // getLanguage()

/** A keyed message formatter. <br />
 *  <br />
 *  This method is just an replacement for the otherwise (hopefully for 
 *  internationalisation) often used sequence:<pre><code>
 *  &nbsp;   String pattern = valueLang(patternKey, patternDefault);
 *  &nbsp;   TextHelper.messageFormat(bastel, pattern, args);</code></pre>
 * 
 *  <br />
 *  @param bastel  StringBuilder on which to append to; if null it will be 
 *                 made with starting capacity of 81
 *  @param patternKey key for pattern (nationalised) to append to  bastel
 *  @param patternDefault pattern to append to bastel
 *  @param args    Array of arguments; may be of type Object[] or
 *                 int[]; null oder empty means that pattern is to be
 *                 appended to bastel without any interpretation (message
 *                 formatting).
 *                 If args is no array it is treated as an one elementary 
 *                 just for the spot {0} in the pattern. (This saves 
 *                 throw-away objects for very frequent cases).
 *  @return bastel (parameter or new, never null
 *  @see AppLangMap
 *  @see #valueLang(CharSequence, String)
 *  @see TextHelper#messageFormat(StringBuilder, CharSequence, Object)
 */
   public StringBuilder messageFormat(StringBuilder bastel, 
               CharSequence patternKey, String patternDefault, Object args){
      String pattern = valueLang(patternKey, patternDefault);
      return TextHelper.messageFormat(bastel, pattern, args);
   } // messageFormat(StringBuilder, CharSequence, String, Object) 


///-----------   AppMBean - implementation  begin  -----------------------
   
/** The actual (system) time as text. <br />
 *  <br />
 *  The actual platform time is updated to system clock and then
 *  formatted according to {@link AppLangMap}{@link
 *  AppLangMap#valueUL(CharSequence) .valueUL(&quot;wedaclock&quot;)}.<br />
 *  <br />
 *  In the case of user language German the format is 
 *  &quot;D, d.m.Y, H:i:s&quot;; in the case of English it would be 
 *  &quot;w, M d Y, H:i:s&quot;. That would give  
 *  &quot;Do,&nbsp;05.02.2009,&nbsp;13:45:19&quot; or
 *  &quot;Th,&nbsp;Feb.&nbsp;05&nbsp;2009,&nbsp;13:45:19&quot; 
 *  respectively.<br />
 *  <br />
 *  @see AppHelper#getActTime()
 *  @return the platform's time
 */  
   @Override public final String getActTime(){ return AppHelper.getActTime(); }
   
/** The start parameters. <br />
 *  <br />
 *  Usually set by one of the start methods 
 *  {@link #go(String[], String, CharSequence) go(...)}
 *  called within <code>void main(String[] args)</code>.<br />
 *  Here in args the parameters used in (partial) parsing will be set null,
 *  while argsOrig will be an untouched copy. <br />
 *  <br />
 *  default: {@link ComVar}.{@link ComVar#NO_STRINGS  NO_STRINGS}<br />
 */
   protected String[] args = NO_STRINGS, argsOrig = NO_STRINGS;

/** The start parameters. <br />
 *  <br />
 *  Applications based on the <a href="package-summary.html#be">Framework</a>
 *  Frame4J will be inheritors of @link App} in most cases. They get their 
 *  start parameters as String array by one of the methods
 *  {@link App#go(String[], String, CharSequence) App.go()}. 
 *  And {@code go()} is usually called in the applications 
 *  {@code void main(String[] args)} forwarding just the start parameter
 *  array supplied by the JVM.<br />
 *  <br />
 *  In the spirit of JVM's treaty with {@code main(String[])} that array
 *  will never be null. But it may be the empty array 
 *  {@link ComVar#NO_STRINGS}.<br />
 *  <br />
 *  This method returns the originally supplied start parameters (see
 *  {@link #argsOrig}) as one String containing the start parameters 
 *  space separated (as from the shell's command line).<br />
 *  <br />
 *  @see TextHelper#prepParams(String[])
 *  @return the application's start parameters
 */
   @Override public final String getArgs(){
     if (argsOrig == NO_STRINGS) return EMPTY_STRING;
     if (argsOrig == null || argsOrig.length == 0) {
        argsOrig = NO_STRINGS;
        return EMPTY_STRING;
     } 
     return TextHelper.prepParams(argsOrig);
   } // getArgs()

/** Register this application as standard MBean. <br />
 * 
 *  @return the registered object name
 *  @throws JMException if the registering fails 
 */
  public String regAsStdMBean() throws JMException {
    MBeanServer platformMBeanServer =
                            ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = null;
    Class<?> clasz = this.getClass();
    String fullName = clasz.getName();
    String fullMBname = fullName + "MBean";
    Class<?>[] intfacs = clasz.getInterfaces();
    Class<?> mBintf = null;
    for (Class<?> i : intfacs) {
      if (fullMBname.equals(i.getName())) { mBintf = i; break; }
    } // for
    if (mBintf == null) throw  // not implementing m.y.SelfMBean
      new NotCompliantMBeanException("MBean interface not implemented");
    int fnL = fullName.length();
    int lastDot = fullName.lastIndexOf ('.');
    if (lastDot == -1 || (lastDot + 2) >= fnL) throw  // not package.class
      new NotCompliantMBeanException("no package.class");
    String oName = fullName.substring (0, lastDot) + ":type="
                                + fullName.substring (lastDot + 1);
    objectName = new ObjectName(oName);
    platformMBeanServer.registerMBean(this, objectName);
    return oName;
  } // regAsStdMBean()


//---------------------------------------------------------------

/** Detailedness of reports or logging. <br />
 *  <br />
 *  The levels are defined according to the table in
 *  {@link AppHelper}.{@link AppHelper#getVerbosityAsString(int)}.<br />
 *  <br />
 *  default: {@link AppHelper#NORMAL}<br />
 *  <br />
 *  @see #isSilent()
 *  @see #setVerbosity(int)
 *  @see #setVerbosity(String)
 *  @see #getVerbose()
 *  @return the current verbosity
 */
   public final int getVerbosity(){ return verbosity; }

/** Detailedness of reports or logging. <br />
 *  <br />
 *  The levels are defined according to the table in
 *  {@link AppHelper}.{@link AppHelper#getVerbosityAsString(int)}.<br />
 *  <br />
 *  <br />
 *  @return {@link AppHelper}.{@link AppHelper#getVerbosityAsString(int)
 *     getVerbosityAsString(verbosity)}<br />
 *  @see #getVerbosity()
 */
   @Override public String getVerbose(){
      return AppHelper.getVerbosityAsString(verbosity);
   } // getVerbose()


/** Set the report detailedness of this application. <br />
 *  <br />
 *  Hint: All other methods setting the 
 *  {@link #getVerbosity() report detailedness} use this method. So (only) 
 *  this method has to be overridden if modifications are necessary.<br />
 *  <br />
 *  @see #getVerbosity()
 *  @see #setVerbosity(String)
 *  @param verbosity Level AppHelper.DEBUG .. AppHelper.SILENT; 
 *     default: AppHelper.NORMAL
 *  @see AppHelper#NORMAL
 *  @see AppHelper#getVerbosityAsString(int)
 *  @see AppHelper#getVerbosityLevel(int)   
 */
   public void setVerbosity(int verbosity){
      if (verbosity < AppHelper.DEBUG || verbosity > AppHelper.SILENT) 
            verbosity = AppHelper.NORMAL;
      if (this.verbosity != verbosity) { // change
         this.verbosity = verbosity;
         this.verbose = verbosity <= AppHelper.VERBOSE;
         if (appLogger != null) {
            Level level = AppHelper.getVerbosityLevel(verbosity);
            appLogger.setLevel(level);
            getLogHandler().setLevel(level);
          }   
      } // change
   } // setVerbosity(int)

/** Set the report detailedness of this application. <br />
 *  <br />
 *  The parameter {@code verbosity} will be interpreted by the method 
 *  {@link AppHelper}.{@link AppHelper#getVerbosity(String)} and the result
 *  will be passed to {@link #setVerbosity(int)}.<br />
 *  <br />
 *  @see #getVerbosity()
 *  @see #setVerbosity(int)
 *  @param verbosity the verbosity level to be set
 */
   public final void setVerbosity(String verbosity){
      setVerbosity(AppHelper.getVerbosity(verbosity));
   } // setVerbosity(String)

/** Set the report detailedness of this application. <br />
 *  <br />
 *  @see #setVerbosity(String) setVerbosity(verbosity)
 *  @param verbosity the verbosity level to be set
 */
   public final void setVerbose(String verbosity){ setVerbosity(verbosity); }

/** Set the report detailedness of this application. <br />
 *  <br />
 *  @see #isVerbose()
 *  @param verbose the verbosity level to be set; true: verbose; false: normal
 */
   public final void setVerbose(boolean verbose){
      if (this.verbose == verbose) return;
      setVerbosity(verbose ? AppHelper.VERBOSE : AppHelper.NORMAL);
   } // setVerbose(boolean) 
   
/** Report detailedness of this application. <br />
 *  <br />
 *  default: {@link AppHelper#NORMAL}<br />
 *  <br />
 *  @see #getVerbosity()
 */
   protected int verbosity = AppHelper.NORMAL;

/** Report detailedness of this application.  <br />
 *  <br />
 *  @see #getVerbosity()
 *  @return the current verbosity level; true: above normal
 */
   public final boolean isVerbose(){
      return verbose;
   } // isVerbose()

/** Verbose reports. <br />
 *  <br />
 *  @see #isVerbose()
 *  @see #setVerbose(boolean)
 *  @see #setVerbosity(String)
 *  @see #setVerbosity(int)
 */
   protected boolean verbose;

/** Almost no reports. <br />
 *  <br />
 *  If true only reports on catastrophic events are to be generated.<br />
 *  <br />
 *  @see #isVerbose()
 *  @see #isNormal()
 *  @see #getVerbosity()
 *  @return the current verbosity level; true: silent
 */ 
   public final boolean isSilent(){
     return verbosity >= AppHelper.SILENT;
   } // isSilent()


/** Just the normal reports. <br />
 *  <br />
 *  If true the so called &quot;normal&quot; events shall be generated, those
 *  of every day relevance for the process done or controlled by this 
 *  application.<br />
 *  <br />
 *  @see #isVerbose()
 *  @see #isSilent()
 *  @see #getVerbosity()
 *  @return the current verbosity level; true: normal (or more)
 */ 
   public final boolean isNormal(){ return verbosity <=  AppHelper.NORMAL; }

/** Many reports &mdash; for testing. <br />
 *  <br />
 *  If true many events, also  to a very fine granularity (normally of low 
 *  relevance for the process done or controlled by this application), are 
 *  generated.<br />
 *  <br />
 *  @see #isVerbose()
 *  @see #isSilent()
 *  @see #isNormal()
 *  @see #getVerbosity()
 *  @return the current verbosity level; true: test events (or more)
 */ 
   public final boolean isTest() { return verbosity <=  AppHelper.TEST; }

/** All reports &mdash; for test and debugging.  <br />
 *  <br />
 *  If true many events, also programme technical ones (normally of nil
 *  relevance for the process done or controlled by this application), are 
 *  generated.<br />
 *  <br />
 *  @see #isVerbose()
 *  @see #isSilent()
 *  @see #isNormal()
 *  @see #getVerbosity()
 *  @return the current verbosity level; true: debug (or more)
 */ 
   public final boolean isDebug(){ return verbosity <= AppHelper.DEBUG; }


//------------------------   Action-Queue-------------------------------
  
/** ActionList. */
   AWTEvent[] eL;
/** ActionList. */
   String[] cL;
/** ActionList. */
   int eSize, putInd, getInd, eCap;
/** ActionList. */
   Thread eThread;

/** React to an event by queueing an action. <br />
 *  <br />
 *  This method is provided for graphical {@link App} inheritors mostly for
 *  being used in graphical event listeners indirectly via 
 *  {@link #actionPerformed(ActionEvent) actionPerformed()} etc.<br />
 *  <br />
 *  It may be called directly as well.<br />
 *  <br />
 *  If <code>command</code> and <code>event</code> are null or empty nothing
 *  happens.<br />
 *  <br />
 *  <br />
 *  If  <code>command</code> is null, it will be tried to determine it 
 *  directly as actionCommand or indirectly (via getSource() respectively 
 *  getItemSelectable()) from  <code>event</code>.<br />
 *  <br />
 *  If that is not possible or if <code>command</code> is now still null or 
 *  empty nothing is done.<br />
 *  <br />
 *  Otherwise using the (given or determined) <code>command</code> and
 *  <code>event</code>
 *  {@link #performeAction(String, AWTEvent)} will be called. This is done via
 *  a queue in a special thread. The actions of this class's
 *  {@link #performeAction(String, AWTEvent)} and its extensions do not run in
 *  the thread that directly or indirectly called this method.<br />
 *  <br />
 *  The queue implemented here independently from AWT or swing fulfils the
 *  recommendation or request not to handle longer actions in the event 
 *  source's thread.<br />
 *  <br />
 *  The queue's length is limited to 110 events. Overflow forgets the oldest 
 *  event. This border is a hint which event handler execution time would 
 *  require an extra thread.<br />
 *  <br />
 *  Hint: If {@link #verbosity} is &lt;= {@link AppHelper#TEST} e.g. equal to
 *  {@link AppHelper#DEBUG} every action will be logged on {@link #log} before 
 *  calling  {@link #performeAction(String, AWTEvent)}.<br />
 *  <br />
 *  @see #performeAction(String, AWTEvent)
 *  @param command the command, if null it will be extracted from event
 *  @param event graphical event, may be null if command is not null
 */  
  public final void queueAction(String command, final AWTEvent event){
     commandSet:  if (command == null) {
        if (event == null) return;
        if (event instanceof ActionEvent) {
           command =  ((ActionEvent)event).getActionCommand();
           if (command != null) break commandSet;
        }
        Object s = event instanceof ItemEvent 
                         ? ((ItemEvent)event).getItemSelectable() 
                          :  event.getSource();
        if (s instanceof AbstractButton) {
           command =  ((AbstractButton)s).getActionCommand();
        } else if (s instanceof  CheckboxMenuItem) {
           command = ((CheckboxMenuItem)s).getActionCommand();        
        } 
        if (command == null) return;
     } // commandSet: 
     if (command.isEmpty()) return;
     if (command.equals("do not act")) {
        if (verbosity <= AppHelper.TEST) {
           String from = " ???? ";
           if (event != null) {
              Object s = event.getSource();
              from = s == null ? event.toString() : s.toString();
           }
           log.println(" // action [not queued] : \"do not act\" from "
                     + from);
        }   
        return;
     }
     
     if (cL == null) { // first real use
        eCap = 110;
        cL   = new String[eCap];
        eL   = new AWTEvent[eCap];
        putInd = -1;
        eThread = new Thread() {  // ==== ano. inner ====
           String command = null;
           AWTEvent event = null;
           @Override public void run() {
              endLess:  while (true) {
                 synchronized(cL){
                    while (eSize == 0) { 
                       try {
                          cL.wait();
                       } catch (InterruptedException e) {}
                    } // 
                    command = cL[getInd];
                    event = eL[getInd];
                    ++getInd;
                    if (getInd == eCap) getInd = 0;
                    --eSize;
                 } // sync
                 if (verbosity <= AppHelper.TEST) {
                    String from = " ???? ";
                    if (event != null) {
                       Object s = event.getSource();
                       from = s == null ? event.toString() : s.toString();
                    }
                           
                    log.println(" // action [" + getInd
                           + (eSize != 0 ? "] / " + eSize : "]") 
                           + " dequeued : \"" + command
                            + "\" from " + from);
                 }   
                 try { // since 29.04.2005 robust
                    performeAction(command, event); // unsync. , concurrent
                 } catch (Exception e) {
                    log.println(" // action [" + getInd
                             + "]   dequeued : \"" + command
                          + "\"\n    threw  " + e);
                    if (verbosity <= AppHelper.DEBUG) 
                       e.printStackTrace(log);
                 }  // try catch since 29.04.2005: robust
              } // endless
           } // run
        }; // Thread                ==== ano. inner ====
        eThread.setDaemon(true);
        eThread.setPriority(Thread.MAX_PRIORITY -2);
        eThread.start();
     } // first real use

     synchronized (cL) {
        if (eSize < eCap) {
           ++eSize;
        } else {
           ++getInd;
           if (getInd == eCap) getInd = 0; // forget
        }
        ++putInd;
        if (putInd == eCap) putInd = 0;
        cL[putInd] = command;
        eL[putInd] = event;
        cL.notify();
        
     } // sync
  }  // queueAction(String, AWTEvent) 
  
  
//------------------------   Action-Queue  -------------------------------
  
/** The reaction to an action; implementation. <br />
 *  <br />
 *  This method will be called indirectly via
 *  {@link #queueAction(String, AWTEvent)}. It may as well be called 
 *  directly.<br />
 *  <br />
 *  This implementation reacts to no command at all (as of January 2010). As
 *  long as {@link #isRunFlag() runFlag} is true and if 
 *  {@link #getVerbosity() verbosity} is &lt;= {@link AppHelper#DEBUG} the 
 *  {@code command} will be logged with the actual time.<br /> 
 *  <br />
 *  In case of a possible reaction true is returned. true is also returned
 *  without any action performed if {@link #runFlag} is false.<br />
 *  <br />
 *  All other cases return false.<br >
 *  <br />
 *  This signalling by boolean return value allows an overriding method to
 *  use this method purposefully:<code><pre>
 *  &#160;   if (super.performeAction(command, e)) return true;
 *  &#160;   // Implementing further actions / commands
 *  </pre></code>
 *  
 *  This behaviour should be kept over the whole inheritance chain.<br />
 *  <br />
 *  @param command the &quot;action command&quot;
 *  @param e if given an {@link ActionEvent} or an {@link ItemEvent};<br />
 *         may be null or give supplementary information on the desired 
 *         action
 *  @return true if no more actions are to be performed        
 */  
  public boolean performeAction(String command, AWTEvent e){
     if (!runFlag) return true;  // no more actions on stop signal
     
     if (verbosity <= AppHelper.DEBUG) {
        log.println(" ///  Action  ( " + AppHelper.getActTime()
             + " ) .. " + command  + (e != null
             ?  " ... from ... " + e.getClass().getSimpleName()
                                                      : " .. no source "));
     }  // log command 
     ///  if (command == null || command.length() == 0) return false;
     //  command = TextHelper.simpLowerC(command);

     return false;
  } // performeAction(String, AWTEvent)

/** The reaction to an action; mostly by a menu. <br />
 *  <br />
 *  This method will be called by the underlying graphical system as a 
 *  reaction to the user's actions (menu or button events).<br />
 *  <br />
 *  This implementation determines in the end the command from the 
 *  {@link java.awt.event.ActionEvent} e and calls if one was given 
 *  {@link #performeAction performeAction(command, e)}.<br />
 *  <br />
 *  Important detail: This implementation will put this call by using the
 *  method {@link #queueAction(String, AWTEvent)} into a thread different 
 *  from the one calling this method
 *  {@link #actionPerformed(ActionEvent)}.<br />
 *  <br />
 *  Hint: It will usually be better for extending classes to override 
 *  {@link #performeAction performeAction()} instead of this method
 *  {@link #actionPerformed(ActionEvent)}. The extra gift is the decoupling
 *  thread working independently from swing, AWT or other event sources.<br />
 *  <br />
 *  Hint 2: This method implements the interface {@link ActionListener}.<br />
 *  <br />
 *  Hint 3: Touching Swing components by the method
 *  {@link #performeAction performeAction()} have nevertheless to obey all
 *  Swing threading rules (usually by using  invokeLater() for that 
 *  purpose).<br />
 *  @param e the action to be queued
 */  
  @Override public void actionPerformed(ActionEvent e){
     if (e == null) return;
     queueAction(e.getActionCommand(), e);
  } //  actionPerformed(ActionEvent)
  
//-----------------------------------------------------------------------
  
   
/** Do only (on line) help output; have no other effects. <br />
 *  <br />
 *  If inheriting / implementing classes (objects) find this property set true
 *  (by start parameters option -? or -help or else) the normal work of the
 *  application shall never begin. Instead only the online help text
 *  (hopefully nationalised) shall be output.<br />
 *  <br />
 *  Graphical applications may start the graphics so far as to output the
 *  help text graphically (text window) instead of using {@link #log}.<br />
 *  <br />
 *  Considering the start parameters none of them except -? or -help itself
 *  shall have any effect. The first exception from that rule are those
 *  switching the user language. i.e. -en, -de, -fr etc. The second exception
 *  may be those that branch the log output to a file. The methods
 *  {@link #go(String[]) go(..)} would normally have done all this
 *  before the overridden {@link #doIt()} must decide to do just help output
 *  plus nothing.<br />
 *  <br />
 *  This property shall be evaluated on application's startup (in the sense of
 *  entering  {@link #doIt()}) only. Setting it to true later must have no
 *  effect.<br />
 *  <br />
 *  default: false
 *  @return true if a help output (-help -?) is requested
 */ 
   public final boolean isHelp(){ return help; }

/** Only (on line) help output, no other effects. <br />
 *  <br />
 *  @see #isHelp()
 */
   protected boolean help;

/** Do only (on line) help output; have no other effects. <br />
 *  <br />
 *  @see #isHelp()
 *  @param help true if a help output (-help -?) is requested
 */
   public final void setHelp(boolean help) { this.help = help; } 

   
///------------------------------------------------------------

/** Output mode for log and out files. <br />
 *  <br />
 *  default: {@link OutMode#ASK Ask}<br />
 *  <br />
 *  @see #setOutMode(CharSequence)
 *  @see #setOutMode(OutMode)
 *  @return the mode for logging and else output
 */ 
   public final OutMode getOutMode(){ return outMode; }

/** Output mode for log and out files. <br />
 *  <br />
 *  @see #getOutMode()
 */
   protected OutMode outMode = OutMode.ASK;

/** Set output mode for log, out and may be other files. <br />
 *  <br />
 *  @see #getOutMode()
 *  @param outMode null: no change
 */
   public final void setOutMode(OutMode outMode ){
      if (outMode == null) return;
      this.outMode = outMode; 
   } // setOutMode(OutMode)

/** Set output mode for log, out and may be other files. <br />
 *  <br />
 *  @see #getOutMode()
 *  @see OutMode#of(CharSequence, OutMode)
 *  @param outMode null, empty, no fit : no change
 */
   public final void setOutMode(CharSequence outMode ) {
      if (outMode == null) return;
      this.outMode = OutMode.of(outMode, this.outMode); 
   } // setOutMode(CharSequence)

//---------------------------------------------------------------------------


/** Flag to stop all threads of this application. <br />
 *  <br />
 *  This flag will be set false before or on ending this application. If
 *  false it is a signal to all the application's threads to end their work 
 *  gracefully but without any undue delay. The usual coding will be
 *  {@code while(runFlag)} or {@code while(isRunFlag())}. This also the
 *  correct substitute for the depreciated Thread.stop().<br />
 *  <br /> 
 *  Using the flag is application / extension programmer's responsibility.
 *  To set it to false use {@link #stop()}.<br />
 *  <br />
 *  Start value: true<br />
 *  <br />
 *  This method returns the AND of the {@link #runFlag} (reset by 
 *  {@link #stop()}) and the common runFlag's, i.e.
 *  {@link AppBase}.{@link AppBase#isCommonRun() commonRun}. If only the 
 *  latter is false, this method will call {@link #stop()}.<br />
 *  <br />
 *  @see AppBase#isCommonRun()
 *  @return true if the application may run on
 */
   public final boolean isRunFlag(){
      if (runFlag && AppBase.commonRun) return true;
      if (runFlag) stop();
      return false;
   } // isRunFlag()
   
/** Flag to stop all threads of this application. <br />
 *  <br />
 *  This application and all its threads shall only run as long as this
 *  variable is true.<br />
 *  <br />
 *  Important implementation hint: The only reason for this variable being 
 *  protected is the very frequent reading by some inheritors. Under no 
 *  circumstances an inheritor is entitled to write to 
 *  {@link #isRunFlag() runFlag}. One legal way to to set it false is
 *  the method {@link #stop()}.<br />
 *  <br />
 *  @see #isRunFlag()
 *  @see #stop()
 */   
   protected volatile boolean runFlag = true;
   
  
/** Stop the application. <br />
 *  <br />
 *  This method shall gracefully end this application. It will set the 
 *  {@link #isRunFlag() runFlag} to false if it is not yet so.<br />
 *  <br />
 *  If {@link #isRunFlag runFlag} is false, this method does nothing.<br />
 *  <br />
 *  Otherwise the {@link #isRunFlag runFlag} is set false. The enforced
 *  output buffering (optionally set for graphical inheritors) of {@link #log}
 *  and {@link #out} is switched off. The application's 
 *  {@link #mainThread main thread} is 
 *  interrupted by {@link Thread}.{@link Thread#interrupt() interrupt()} (if
 *  it still exists and if this method is not called by it).<br />
 *  <br />
 *  It is the responsibility of the application (i.e. its waked up main thread
 *  and all its other threads to end in an ordered fashion. This proceeding 
 *  to end (by App's contract)  in calling
 *  {@link #normalExit normalExit()} or
 *   {@link #errorExit errorExit()}.<br />
 *  <br />
 *  It can be said that this method {@link #stop()} is the signal to end
 *  and the methods xyzExit() are the end itself. The latter may be called 
 *  without former stop signal.<br />
 *  <br />
 *  Is this application the &quot;mother application&quot; of a whole
 *  bundle of applications
 *  {@link AppBase}.{@link AppBase#isCommonRun() commonRun} will be set false
 *  also as a stop signal to all others.<br />
 */   
   @Override public void stop(){
      if (!runFlag) return;
      runFlag = false;
      if (appBase != null && appBase.baseApp == this) { // Mother app
         AppBase.commonRun = false;
      }
      appIO.logTW.noExplFlush1 = false; 
      appIO.outTW.noExplFlush2 = false; 
      log.flush();
      if (mainThread != null && mainThread != Thread.currentThread()) try {
         mainThread.interrupt(); 
      } catch (Exception ise){} // ignore illegal State,
  } // stop()

/** Wake up the main thread. <br />
 *  <br />
 *  This method supports applications that want to do triggered tasks in their
 *   main thread ({@link #mainThread}, that is {@link #doIt()}). This thread
 *   will be waked up robustly and reliably. 
 *  The success will be signalled by returning true.<br />
 *  <br />
 *  Missing preconditions or (non probable) wake up exceptions will be 
 *  signalled by returning false.<br />
 *  <br />
 *  <br />
 *  Implementation hint: If this application shall run and if the  
 *  {@link #mainThread} still exists and if the caller is not in that main 
 *  that thread will be interrupted.<br />
 *  <br />
 *  @see AppHelper#sleep(long)
 *  @see AppHelper#getTimer()
 *  @see AppHelper#scheduleAbsolute(TimerTask, long, long)
 *  @see AppHelper#scheduleAtFixedRate(TimerTask, long, long)
 *  @return true if the main thread was woken up   
 */
   public final boolean wakeMainThread(){
      if (!runFlag) return false;
      final Thread tT = mainThread; // almost threadsafe
      if (tT != null && tT != Thread.currentThread()) try {
         tT.interrupt();
         return true;
      } catch (Exception ise){} // ignore illegal State,
      return false;
   } // wakeMainThread()
   

/** The condition of (just) outputting the on-line help. <br />
 *  <br />
 *  This method returns (among others for {@link #condHelpLog()} the condition
 *  that an on-line help output to {@link #log} shall be the only work or
 *  effect of this application's actual execution.<br />
 *  <br />
 *  This implementation just returns {@link #help}. This is the standard 
 *  condition.<br />
 *  <br />
 *  An inheritor may override this method to modify this condition, may be to
 *  output the online help on missing or wrong parameters also, 
 *  e.g. by doing<code><pre>
 *    return help || extraCondition(); // like args.length == 0</pre></code>
 *    
 *  @return {@link #help}
 *  @see #isHelp()
 *  @see #condHelpLog()
 */
   protected boolean isHelpLog(){ return help; }

/** Conditional output of on-line help to log, and then stop. <br />
 *  <br />
 *  If the method {@link #isHelpLog()} returns true, this method does the
 *  following:<pre></code>
 *     {@link #log}.{@link #getHelpText() println(helpText)});
 *     {@link #stop()};</pre></code>
 *     
 *  This outputs the (nationalised) help text and ends this application.<br />
 *  <br />
 *  If {@link #isHelpLog()} returns false this method does nothing.<br />
 *  <br />
 *  @return {@link #isHelpLog()}
 *  @see #isHelp()
 *  @see #go(String[], String, CharSequence) go(..)
 */
   protected final boolean condHelpLog(){ // -help oder -?
      if (isHelpLog()) {
         String tmp = getHelpText();
         if (tmp != null) log.println(tmp);
         stop();
         return true;
      } // condHelp()
      return false;
   } // condHelpLog()

/** A client's command to this application. <br />
 *  <br />
 *  This method will be called if a (may be remote, may be JMX, JConsole or
 *  alike) client has an order (agreed upon). The parameter {@code command}
 *  must contain all necessary denotations, like command, parameter,
 *  credentials and so on. {@code command} must not be empty.<br />
 *  <br />
 *  The text returned shall be a report on the execution of the order 
 *  given. It shall be human readable. If the order has to be refused, this
 *  may as well or better be done by an  IllegalArgumentException  with a
 *  expressive message.<br />
 *  <br />
 *  For the sake of uniformity every App inheritor, whose object are usable
 *  as MBeans, shall implement the parameterless commands 
 *  &quot;help&quot; and &quot;status&quot;.<br />
 *  &quot;help&quot; shall provide a survey on all (public) orders
 *  (clientOrders) and &quot;status&quot; shall give information on the 
 *  applications current execution state.<br />
 *  <br />
 *  This implementation realises this minimal standard with &quot;help&quot;
 *  and &quot;status&quot; as well as some command not published by 
 *  &quot;help&quot; for sake or inheriting classes:<br />
 *  <br />
 *  <table cols="3" border="1" cellpadding="3" 
 *  cellspacing="0" summary="Commands">
 *  <tr><th style="width:50;"> Command </th><th style="width:120;"> Parameter </th>
 *  <th> Effect / text returned </th></tr>
 *  <tr><td>help, ?</td><td>none</td><td>shows help and status as the 
 *         command list.</td></tr>
 *  <tr><td>status</td><td>none</td><td>shows the execution state like
 *         returned by {@link #getStateString()}.</td></tr>
 *  <tr><td>appHelp</td><td>none</td><td>shows the help text as by
 *        {@link #getHelpText() helpText}.</td></tr>
 *  <tr><td>about</td><td>none</td><td>shows a describing text as by
 *             {@link #getAboutText()}.</td></tr>
 *  <tr><td>log</td><td>none</td><td>returns the (last 10000 characters) of
 *             the log output.</td></tr>
 *  <tr><td>out</td><td>none</td><td>returns the (last 10000 characters) of
 *             the output to out.</td></tr>
 *  <tr><td>prop</td><td>none</td><td>returns all properties 
 *           ({@link Prop Prop} {@link #prop}) as Text.</td></tr>
 *  <tr><td>stop</td><td>none</td><td>ends this application by calling 
 *               {@link #stop()}).</td></tr>
 *  <tr><td>others</td><td>others</td><td>throw an 
 *                 IllegalArgumentException aus.</td></tr>
 *  </table>
 *  <br />
 *  Hint 1: An extending class may, of course, use this comprehensive 
 *  implementation as is. Normally it will override it, and be it only to 
 *  add the commands already implemented here to the help list; see above.
 *  The overriding method will use this implementation by 
 *  super.clientOrder(command) after doing additional command or blocking
 *  unwanted ones.
 *
 *  @param command the order (containing all parameters and else)
 *  @throws AttributeNotFoundException if the order is not implemented
 *           or if the parameters are wrong; see table
 *  @return success report or information requested
 */   
   public String clientOrder(final String command)
                                           throws AttributeNotFoundException {
      
   if (command == null || command.equals("help") || command.equals("?")) { 
          return
             "\n  JMX - commands (remote) to "  +  myClass.getName()
             +              " are:\n"
             + "  help, ?   :  this help (via JMX).\n"
             + "  appHelp   :  the application's (online) help text.\n"
             + "  status    :  the application's status text.\n"
             + "\n"
             + " Copyright &copy; 2003, 2007, Albrecht Weinert "
             + " (a-weinert.de)\n\n";
      }  // help

      if (command.equals("status")) return getStateString();

      if (command.equals("log")  ) 
         return appIO.logTW.getContent(10000, false, 132);
      if (command.equals("out")  ) 
         return appIO.outTW.getContent(10000, false, 132);

      if (command.equals("about")  )  return getAbout();

      if (command.equals("appHelp"))  return getHelp();

      if (command.equals("prop")) {
         if (prop == null) return "prop is null.";
            return prop.list().toString();
      } // prop

      if (command.equals("stop") || command.equals("stop") ) {
         stop();  //  09.11.03
         return ("  ***   " + myClass.getName() + " stopped");
      }
           
      throw new AttributeNotFoundException("command < "
                              + command + " > illegal. ");      
   } // clientOrder(String)


/** The running state as text. <br />
 *  <br />
 *  This method returns an informing text about the actual execution state
 *  of this application. This method may be overridden accordingly in
 *  extending classes.<br />
 *  <br />
 *  This implementation returns the concatenation of
 *  {@link #makeStatusTextStart(StringBuilder) makeStatusTextStart(.)}
 *  and 
 *  {@link #makeStatusTextEnd(StringBuilder)  makeStatusTextEnd(.)}.<br />
 *  <br />
 *  @return the application's state
 */
   public String getStateString(){
      StringBuilder bastel = makeStatusTextStart(null);
      return makeStatusTextEnd(bastel).toString(); 
   } // getStateString()

/** Append the standard start to a status text. <br />
 *  <br />
 *  Appended to the StringBuilder {@code bastel} will be the full class name,
 *  {@link #runFlag}'s state as &quot;active&quot; or &quot;ending&quot;, the
 *  state of {@link #verbosity} as text like by
 *  {@link #getVerbose()}. No line feed follows.<br />
 *  <br />
 *  @param  bastel the StringBuilder to append to; if null it is made 
 *  @return bastel (i.e. the StringBuilder for further use)
 */
   public StringBuilder makeStatusTextStart(StringBuilder bastel){
      if (bastel == null) bastel = new StringBuilder(320);
      bastel.append(" ").append(myClass.getName());
      bastel.append(" ** state: ");
      bastel.append(runFlag ? "active" : "ending");
      bastel.append(", verbosity: ").append(getVerbose());
      return bastel;
   } // makeStatusTextStart(StringBuilder)

/** Append the standard end to a status text. <br />
 *  <br />
 *  Appended to the StringBuilder {@code bastel} will be a line feed and 
 *  the six lines lines with informations on window title, start arguments,
 *  open mode for log files, start time, execution time so far and
 *  system time.<br />
 *  <br />
 *  @param bastel the StringBuilder to append to; if null it is made 
 *  @return bastel
 */
   public StringBuilder makeStatusTextEnd(StringBuilder bastel) {
      if (bastel == null) bastel = new StringBuilder(180);
      AppLangMap uM = AppLangMap.getUMap();     
      
      bastel.append("\n ").append(uM.value("wintit"));
      bastel.append(": \"").append(title);
      bastel.append("\"\n ").append(uM.value("strtargs"));
      bastel.append(": \"").append(getArgs());
      bastel.append("\"\n ").append(uM.value("logfmode"));
      bastel.append(":  \"").append(outMode);
      bastel.append("\"\n ").append(uM.value("strttime"));
      bastel.append(":  ").append(getStartTime());
      bastel.append("  \n ").append(uM.value("runtime"));
      bastel.append(":  ").append(getExecTimeString());
      bastel.append("  \n ").append(uM.value("systime"));
      bastel.append(":  ").append(getActTime());
      return bastel;
   } // makeStatusTextEnd(StringBuilder) 

//-----------   Support for graphical elements / inheritors       -----------

/** The preferred title for &#160;Frames or Dialogues (if used). <br />
 *  <br />
 *  @see #getTitle()
 */
   protected String title;

/** The preferred title for &#160;Frames or Dialogues (if used).  <br />
 *  <br />
 *  If not set (by application's .properties as normal case) it will default 
 *  to the application's simple (w/o package) class name.<br />
 *  @see #setTitle(String)
 *  @return the (window) title
 */
   @Override public final String getTitle(){
      if (this.title == null) setTitle(null); // set default
      return title; 
   } // getTitle()
   
/** The preferred title for &#160;Frames or Dialogues (if used).  <br />
 *  <br />
 *  @see #getTitle()
 *  @param  title the (window) title
 */
   public void setTitle(String title){
         this.title = TextHelper.trimUq(title, shortClassName);
   } // setTitle(String

/** The applications main / base graphical window, if any. <br />
 *  <br />
 *  If the inheritor is a graphical application it will have a main window.
 *  This will usually be of type [J]Dialog, [J]Frame or [J]Window.<br />
 *  <br />
 *  This implementation returns null. That can and probably will be changed
 *  by (graphical) inheritors.<br />
 *  <br />
 *  @return the basic frame
 */
   public Window getMyFrame(){ return this.myFrame; }  

/** The application's main / base graphical window, if any. <br /> 
 *  <br />
 *  An inheritor may (and should) use this field with a reference to its
 *  main window. If doing so it must not override {@link #getMyFrame()} as
 *  it returns {@link #myFrame}. <br />
 */  
   protected Window myFrame;
   
/** A simple stop all when Window closes. <br />
 *  <br />
 *  This method produces and registers a Listener which regularly shuts down 
 *  this {@link App} when the given frame closes.<br />
 *  If {@link #getMyFrame() myFrame} is null it will be set by the parameter 
 *  frame.<br />
 *  @return the Listener made or null if frame is null
 *  @param frame The window the listener is to be made for; if null 
 *  {@link #getMyFrame() myFrame} is used. If still null nothing is done.
 */   
   public WindowListener getTheCloser(final Window frame){
      final Window theFrame = frame == null ? this.myFrame : frame; 
      if (theFrame == null) return null;
      final WindowListener ret = new WindowListener(){
         @Override public void windowOpened(WindowEvent e){}
         @Override public void windowIconified(WindowEvent e){}
         @Override public void windowDeiconified(WindowEvent e){}
         @Override public void windowActivated(WindowEvent e){}
         @Override public void windowDeactivated(WindowEvent e){}         
         @Override public void windowClosing(WindowEvent e){
            theFrame.setVisible(false);
            theFrame.dispose();
            wakeMainThread();
            runFlag = false;
         } // windowClosing

         @Override public void windowClosed(WindowEvent e) {
            appBase.normalExit(App.this, retCode);
         } // windowClosed
      }; // WindowListener
      theFrame.addWindowListener(ret);
      if (this.myFrame == null) this.myFrame = theFrame;
      return ret;
   } // getTheCloser(Window)

/** The application's icon, if any. */  
   protected Image icon;

/** The application's icon, if any. <br /> 
 *  <br />
 *  @see #haveIcon()
 *  @return the application's icon
 */  
   public Image getIcon(){ return this.icon; }


/** Get the application's icon. <br /> 
 *  <br />
 *  If an {@link #getIcon() icon} was already set it is returned.
 *  Otherwise the 
 *  <a href="https://weinert-automation.de">weinert-automation</a>'s logo 
 *  will be set and returned.<br />
 *  <br />
 *  @see #getIcon()
 *  @return the application's icon (already set or now made)
 */  
   public Image haveIcon(){ 
      if (this.icon != null) { return this.icon; }
      synchronized (this) {
         this.icon = WeAutLogo.getIcon();
      }
      return this.icon;
   } // haveIcon()

   
/** Preferred background colour for graphical elements (if used).  <br />
 *  <br />
 *  Initial value: &quot;white&quot; (0xFFffFF)<br />
 *  <br />
 */ 
   public String bgColor = "white";  

/** Preferred background colour for graphical elements (if used). <br />
 *  <br />
 *  The parameter {@code bgColor} will be stripped from surrounding white
 *  space and must (then) be interpretable as colour name or number according
 *  to the method's {@link ColorHelper#getColor(CharSequence)} rules.<br />
 *  <br />
 *  Null or (stripped) empty defaults to the previous value, initially 
 *  &quot;white&quot; (0xFFFFFF).<br />
 *  <br />
 *  Setting to an uninterpretable character sequence will / should have the
 *  consequence of using the background colour of graphical parent 
 *  containers.<br />
 *  <br />
 *  default: &quot;white&quot;
 *  @param bgColor the new background colour
 */ 
   public void setBgColor(final CharSequence bgColor){
       this.bgColor = TextHelper.trimUq(bgColor, this.bgColor);
   } // setBgColor(CharSequence)


//---  Variables ------------------------------------------------------------

/** The own class (the class object). <br />
 *  <br />
 *  Set finally at construction ({@link #App() App()}).<br />
 *  <br />
 *  Hint: Clearly, it is not {@link App App}.class, but that of the 
 *  inheritor.<br />
 */
   public final Class<? extends App> myClass; 

/** The own class name. <br />
 *  <br />
 *  Set finally at construction ({@link #App() App()}), the long form meaning
 *  all. The short form is without packages and without inner or embedded 
 *  classes, be the named or anonymous (no $-numbering or $-names).
 *  The {@link #packName} is the package part with trailing dot or empty.<br />
 *  <br />
 *  Hint: Clearly, in nearly all cases it is not 
 *        &quot;de.frame4j.util.App&quot; but the inheritor's package 
 *        name.<br />
 */
   public final String fullClassName, shortClassName; 

/** The own package name. <br />
 *  <br />
 *  Set finally at construction ({@link #App() App()}).
 *  It is this class' package part with trailing dot or empty.<br />
 *  <br />
 *  Hint: Clearly, it is not &quot;de.frame4j.util.&quot; but the 
 *  inheritor's full or short class name.<br />
 */
   public final String  packName; 
   
/** The own MinDoc annotation. <br />
 *  <br />
 *  Set finally at construction ({@link #App() App()}).<br />
 *  If the inheritor is {@link MinDoc} annotated, this annotation is set here.
 *  Otherwise that of an enclosing class or an ancestor is taken; that 
 *  may be lastly that of this class {@link App App}.<br />
 */
   public final MinDoc ano;
   
/** As String. <br />
 *  <br />
 *  This implementation returns <br /> &nbsp;
 &quot;de.frame4j.util.App &lt;-- {@link #fullClassName fullClassName}&quot;.
 *  <br /><br />
 */   
   @Override public String toString() {
      return "de.frame4j.util.App <-- " + fullClassName;
   } // toString() 

   
//----------------- properties ---------------------------------------------   

/** Decide, if the basic .properties file may be omitted. <br />
 *  <br />
 *  Starting an application inheriting from {@link App} with a go method
 *  constructs the {@link App}'s {@link Prop} object. In this process 
 *  .properties files of appropriate name (appName.properties) are searched
 *  as file or as resource (in the .jar).<br />
 *  <br />
 *  It is a basic (compile time) property of the application class, if or if
 *  not one such .properties file must be found. If true and neither such file
 *  nor resource can be found and read, this would be an initialisation error, 
 *  detaining the application from being started.<br />
 *  <br />
 *  At all places (in this class) the decision if no such file found is an 
 *  initialisation error, is done by calling this method. It can (or must)
 *  be overridden in extending classes accordingly.<br />
 *  <br />
 *  This implementation returns<ul>
 *  <li> false (basic .properties mandatory) in 
 *       &quot;normal&quot; cases and</li>
 *  <li> true (basic .properties not required) if the inheritor is an 
 *        anonymous extension of {@link App}.</li></ul>
 *    
 *  The start up procedure (usually by one of the methods
 *  {@link #go(String[]) go()}) constructs a {@link Prop} object
 *  for the application using a constructor like 
 *  {@link Prop#Prop(App, CharSequence)}. Hereby a basic property file 
 *  named  {@link #shortClassName}.properties is searched for as file or 
 *  resource at different places. Finding it nowhere gets a
 *  {@link FileNotFoundException} going up to the starter method. 
 *  The reason for that stringency is, that  {@link App}-Extensions
 *  and their .properties file are considered as a unit. If not
 *  this is considered as the special case 
 *  &quot;allowNoPropertiesFile&quot;.<br />
 *  <br />
 *  Without that file one only has the common default properties for 
 *  all Apps (see{@link Prop#Prop()}).<br />
 *  <br />
 *  Hint: If there is a inheritance hierarchy of {@link App} only the 
 *  last ones base .properties file is searched for.<br />
 *  <br />
 *  <br />
 *  @return false: normal App class, true: inner class
 */
   protected boolean allowNoPropertiesFile() { return allowNoBaseProps; }
   private transient boolean allowNoBaseProps;
   
/** Decide, if and which extra .properties has to be loaded. <br />
 *  <br />
 *  If this method returns a non empty String it is taken as the name of an
 *  extra .properties file to be loaded before class related ones.
 *  @return null
 */   
   protected String extraPropertiesFile(){ return null; }


/** The Prop(erties) object. <br />
 *  <br />
 *  It will be made on initialisation by the methods 
 *  {@link #go(String[], String, CharSequence) go(..)}, loaded
 *  and partly evaluated.<br />
 *  @return the properties
 */
  public Prop getProp(){ return prop; }

/** The Prop(erties) object.<br />
 *  <br />
 *  @see #getProp()
 */
  protected volatile Prop prop; // usually set once and never changed

/** Partial evaluation of start parameters by Prop. <br />
 *  <br />
 *  This method has to return true if the {@link Prop Prop} object shall 
 *  evaluate and check the start parameters (see {@link #args}) only 
 *  partially. For the meaning of &quot;partial evaluation&quot; please see
 *  {@code Prop#parse(String[],CharSequence,boolean)
    Prop.parse(String[], CharSequence, boolean)}.<br />
 *  <br />
 *  As the partial evaluation of start arguments by {@link Prop Prop} is a
 *  fixed property of an application classes extending App must override this
 *  method finally this methods default behaviour is not wanted.<br />
 *  @return false except when made by the 
 *  make'n go constructor {@link #App(String[])} 
 */
   public boolean parsePartial(){ return parsPart; } // parsePartial() 
   private boolean parsPart;

/*  * Provide a XML input stream. <br />
 *  <br />
 *  The {@code fileName} provided as parameter will be suffixed with .xml if
 *  not yet ending so.<br />
 *  By using {@link #prop prop}.{@link Prop#getAsStream(String, String)
 *  prop.getAsStream(suffixedFileName, null)} it is tried to get a stream 
 *  from a file or a resource (that would be mostly from the application's
 *  .jar file).<br />
 *  <br />
 *  If {@link #prop prop} already was set to null, it will be tried to open a
 *  {@link FileInputStream} using the {@code fileName} perhaps suffixed.<br />
 *  <br />
 *  @param fileName the name (will be suffixed with .xml)
 *  @return a stream or null
 *  @see Prop#getAsStream(String, String)
 *  /
   public final InputStream getXMLinput(final CharSequence fileName) {
      String fileNam = TextHelper.makeFName(fileName, ".xml");
      if (fileNam == null) return null;
      if (prop != null) { // prop will also handle the file
         return prop.getAsStream(fileNam, null);
      }  // prop still there
      try  {  // (only) if prop already null only file will be tried
         return new FileInputStream(fileNam);
      } catch (FileNotFoundException fnf) {}
      return null;     
   } // getXMLinput(CharSequence)   xxxx  removed 01.05.2021  */

/** The main thread. <br />
 *  <br />
 *  {@code mainThread} is the thread which the working method
 *  {@link #doIt doIt()} is started in. This variable will be set prior to
 *  entering 
 *  {@link #doIt doIt()} and reset to null after return from it.<br />
 *  <br />
 *  Hint: Setting this variable null within {@link #doIt doIt()} immediately 
 *  before returning will be interpreted as signal to &quot;carry on&quot; 
 *  even after ending {@link #doIt doIt()} and hence its thread:<br />
 *  &nbsp; &nbsp; &nbsp; {@link #mainThread} = null; // carry on<br />
 *  &nbsp; &nbsp; &nbsp; return 0; // end of main Thread<br />
 *  <br />
 *  @see #go(String[], String, CharSequence)
 *  @see #go(String[], String)
 */
   protected volatile Thread  mainThread;


//---  Constructor   -------------------------------------------------------

/** The standard constructor. <br />
 *  <br />
 *  This constructor (by just  calling 
 *  {@link App#App(int, int) App(-1, -1)}) does all basic initialisations 
 *  for the made (this) {@link App} object: <ul>
 *  <li> gets or makes the (singleton-) {@link AppBase} object by
 *       AppBase.getAppBase(this),</li>
 *  <li> gets or makes the {@link de.frame4j.io.AppIO AppIO} object by
 *       AppBase.getAppEA(this, -1, -1, null) fetching from there</li>
 *  <li> the initialised PrintWriter out, log and err.</li>
 *  </ul>
 *  Other settings (like getting and parsing the arguments {@link #args}) are
 *  done afterwards by using one of the starter methods go(....).<br />
 *  <br />
 *  As this constructor is called by the default constructors of inheriting
 *  classes, there is usually no need to write a constructor 
 *  for inheritors.<br />
 *  <br />
 *  @see AppBase#getAppBase()
 *  @see #App(int, int)
 */
   public App(){ this( -1 , -1); }

/** Constructor setting buffer sizes for out and log. <br />
 *  <br />
 *  The (then to be written) parameterless constructor of an inheriting
 *  class must call this explicitly, if not satisfied with default buffer
 *  sizes.<br />
 *  Otherwise the standard (default) constructor chain is sufficient and
 *  should not be changed.<br />
 *  <br />
 *  Implementation hint: All constructors must go through this, as is done by
 *  {@link App#App() App()} calling 
 *  {@link App#App(int, int) App(-1, -1)}.<br />
 *  <br />
 *  @see #App()
 *  @param outBuffLen TeeWriter's buffer size  for {@link #out}, 
 *         1024 .. 400000 characters, default 20K
 *  @param logBuffLen TeeWriter's buffer size  for  {@link #log}, 
 *         1024 .. 400000 characters, default 10K
 */
   public App(final int outBuffLen, final int logBuffLen){
      synchronized (AppBase.class) { // sync. with class of singleton base
         this.myClass  = this.getClass();
         String shortName = this.fullClassName = myClass.getName();
         String packName = ComVar.EMPTY_STRING;
         int lpp = shortName.lastIndexOf ('.');
         if (lpp >= 0 && lpp < shortName.length()-1) {
            shortName = shortName.substring(lpp + 1);
            packName = this.fullClassName.substring(0, lpp + 1);
         } // pure className (getSimplename() does almost the same)

         if (myClass.isAnonymousClass()) {
            allowNoBaseProps = true; // anonymous => .properties not required 
            Class<? extends Object> cl = myClass;
            try { cl = myClass.getEnclosingClass(); } catch (Exception x) {}
            MinDoc anoEncl = cl.getAnnotation(MinDoc.class);
            this.ano = anoEncl != null ? anoEncl 
                               : (MinDoc) myClass.getAnnotation(MinDoc.class);
            lpp = shortName.indexOf('$');
            if (lpp > 0) shortName = shortName.substring(0, lpp); 
         } else {
            this.ano = myClass.getAnnotation(MinDoc.class);
         }
         this.shortClassName = shortName;
         this.packName       = packName;

         appBase = AppBase.getAppBase(this, outBuffLen , logBuffLen, null);
         appIO   = appBase.getAppIO(this, outBuffLen , logBuffLen, null);
         out = appIO.out;
         log = appIO.log;
         err = appIO.err;
      } // sync. with class of singleton base (done anyway in getAppBase()
   }  //  App(2*int)

/** The make'n go constructor. <br />
 *  <br />
 *  This constructor is meant for non complex extensions of {@link App} and 
 *  may well be used in anonymous embedded classes. The simple idiom to make 
 *  any class (not itself extending App} an &quot;{@link App}lication&quot;
 *  with all inherited comfort is:<pre><code>
 *  &nbsp;    public static void main(String[] args) {
 *  &nbsp;       new App(args){
 *  &nbsp;          protected int {@link #doIt()} {
 *  &nbsp;             {@link #log}.println({@link #twoLineStartMsg()});
 *  &nbsp;             /// do the task
 *  &nbsp;             {@link #log}.println({@link #twoLineEndMsg()});
 *  &nbsp;             return retCode;
 *  &nbsp;          } // doIt
 *  &nbsp;       }; // App
 *  &nbsp;   } // main (String[])  my class as App  </code></pre>
 *  
 *  This constructor does all basic initialisations for the made  
 *  {@link App} object and then directly starts the application 
 *  like a starter method go(....).<br />
 *  <br />
 *  This is especially comfortable for not so complex inheritors, as is often
 *  the case with anonymous inner classes extending {@link App}.<br />
 *  <br />
 *  @see AppBase#getAppBase()
 *  @see #App(int, int)
 *  @param args start parameters for the application
 */
   public App(final String[] args){
      this( -1 , -1);
      parsPart = true;
      try {
          go(args, null, null, true);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // App(String[])


//------------------------------------------------------------------------

/** The one (singleton) AppBase object. <br />
 *  <br />
 *  The  AppBase object organises the concurrent run of multiple App based 
 *  or other applications using the same Frame4J resources. It features
 *  especially services for a &quot;well behaved&quot; ending of one or all
 *  those applications.<br />
 *  <br />
 *  Additionally AppBase object organises JMX service.<br />
 */
   protected final AppBase appBase;

/** An AppIO object for log output and console I/O. <br />
 *  <br />
 *  The {@link AppIO} object {@code appIO} features robust and comfortable
 *  I/O.<br />
 *  A {@link PrintWriter} appIO.out outputs to normal out (System.out) and
 *  optionally to a file.<br />
 *  A {@link PrintWriter} appIO.log outputs to appIO.out and optionally to the
 *  file appIO.logDat.<br />
 *  The other  {@link PrintWriter} appIO.err and the  {@code BufferedReader}
 *  appIO.in are of less interest for graphical applications.<br />
 *  The output branches described will be made in the 
 *  {@link de.frame4j.io.AppIO AppIO} object by the use of two 
 *  {@link de.frame4j.io.TeeWriter}s; details see 
 *  {@link de.frame4j.io.AppIO there}.
 */
   protected final AppIO appIO;

/** The &quot;normal&quot; output. <br />
 *  <br />
 *  Output to {@code out} goes by default to normal out (System.out) and 
 *  optionally to a file.<br />
 *  <br />
 *  (Identical to {@link #appIO}.out.)
 */
   public final PrintWriter out;

/** The error output. <br />
 *  <br />
 *  This is the {@link #appIO} object's {@link AppIO#err err}, that is 
 *  System.err decorated with a PrintWriter.<br />
 */
   public final PrintWriter err;

/** The log output. <br />
 *  <br />
 *  Outputs to log will be branched to {@link #out} and if given to the log
 *  file {@link #appIO}.logDat.<br />
 *  (It is {@link #appIO}.{@link AppIO#log log}.)
 */
   public final PrintWriter log;

/** The Logger's handler. <br />
 *  <br >
 *  This method returns a {@link Handler} connected to {@link #log}. It will
 *  be made on the first call. This method just delegates to
 *  {@link #appIO}.{@link AppIO#getLogHandler() getLogHandler()}.)
 *  <br />
 *  @return the handler
 */   
   public final LogWriterHandler getLogHandler(){
      return appIO.getLogHandler();
   } // getLogHandler()

/** The application's base Logger. <br />
 *  <br />
 */
   Logger appLogger;

/** The application's base Logger. <br />
 *  <br />
 *  This method fetches this application's base logger.<br />
 *  The effect is almost identical to
 *  {@link #appIO}.{@link AppIO#getAppLogger() getAppLogger()}. If the 
 *  {@link Logger} is newly generated its {@link Level} will be set according
 *  to {@link #getVerbosity()}.<br />
 *  <br />
 *  @see AppHelper#getVerbosityLevel(int)
 *  @see #setVerbosity(int) 
 *  @return the logger
 */
   public final Logger getAppLogger(){
      if (appLogger != null) return  appLogger;
      synchronized (appIO) {
         appLogger = appIO.getAppLogger();
         Level level = AppHelper.getVerbosityLevel(verbosity);
         appLogger.setLevel(level);
         getLogHandler().setLevel(level);
         if (dbLogger == null) dbLogger = appLogger;
      }
      return appLogger;
   } // getAppLogger()

/** A DB (data base) Logger for the application. <br />
 *  <br />
 */
   Logger dbLogger;

   protected Logger getDbLogger(){ return dbLogger = appLogger; }

/** Log also into a DB (data base). <br />
 *  <br />
 *  The message consisting of the parameters will be generated and logged 
 *  using the {@link Level}.{@link Level#FINE FINE} that is equivalent to
 *  {@link AppHelper#VERBOSE}. If a separate {@link #getDbLogger() dbLogger}
 *  was made with the logging will be
 *  into the database.<br />
 *  @param sourceClass  source of log message
 *  @param sourceMethod  source of log message
 *  @param msg    log message
 *  @param params further parameters for log message
 */   
   public final void logVerbose(String sourceClass, String sourceMethod,
                           String msg, Object params[]) {
      if (dbLogger == null)  getDbLogger();
      dbLogger.logp(Level.FINE, sourceClass, sourceMethod, msg, params);
   } // logVerbose(3*String, Objecr[])

/** Log also into a DB (data base).<br />
 *  <br />
 *  Like {@link #logVerbose(String, String, String, Object[])}, but without 
 *  parameter array.<br />
 *  @param sourceClass  source of log message
 *  @param sourceMethod  source of log message
 *  @param msg    log message
 */   
   public final void logVerbose(final String sourceClass, 
                               final String sourceMethod, final String msg){
      if (dbLogger == null) getDbLogger();
      dbLogger.logp(Level.FINE, sourceClass, sourceMethod, msg);
   } // logVerbose(3*String)

   
//-------------------------------------------------------------------------   

/** Setting the code pages for the Reader in and the 
 *                                              Writers out and err. <br />
 *  <br />
 *  This method calls
 *  {@link #appIO}.{@link de.frame4j.io.AppIO#setCodePages setCodePages()}
 *  if and only if this application is the (one) base / mother application.
 *  That is the one which made the singleton {@link AppBase AppBase}
 *  object.<br />
 *  <br />
 *  Otherwise nothing happens at all.<br />
 *  @param codePage the new encoding for all text I/O streams
 */ 
   public void setCodePages(final String codePage){
      if (this == appBase.baseApp) appIO.setCodePages(codePage);
   } // setCodePages(String)

/** Set log's second Writer as Stream. <br />
 *  <br />
 *  The call is equivalent to:<br />
* {@link #appIO}.{@link de.frame4j.io.AppIO#setLogOut2(OutputStream, String)
 *  setLogOut2(os2, cp2)}<br />
 *  <br />
 *  @param os2 the new second (Tee) writer
 *  @param cp2 its encoding
 */
   public void setLogOut2(final OutputStream os2, final String cp2){
      appIO.setLogOut2(os2, cp2);
   } // setLogOut2(OutputStream, String) 


/** Connect the TeeWriter's outputs for log and out. <br />
 *  <br />
 *  If this application is not the mother application nothing happens (except
 *  return -1).<br />
 *  <br />
 *  Otherwise it will be tried to connect in {@link de.frame4j.io.AppIO}
 *  object {@link #appIO} in each case an output of the {@link TeeWriter}s
 *  log and out to the files specified by the properties logDat respectively
 *  outDat. The mode (append, create,  overwrite, ask) will be determined by
 *  the parameter {@code outMode}.<br />
 *  <br />
 *  If one of the connections fails (due to rejection or file problems e.g.)
 *  1 is returned (logDat) or 2 (outDat). If both succeed 0 is 
 *  returned.<br />
 *  <br />
 *  If asking for a file is necessary and the asking shall be done graphically
 *  the parameter prop will be used to determine the asking windows 
 *  properties (see {@link 
 *  de.frame4j.io.FileVisitor.AskGrafImpl
 *  de.frame4j.io.FileVisitor.AskGrafImpl}).<br />
 *  <br />
 *  @see #INIT_ERROR
 *  @see #LOG_OUT_ERROR
 *  @param outMode the mode for opening an output stream
 *  @param prop the properties to get all information from
 *  @return 0: OK; 1,2: connection failed
 */
   protected int connect(final OutMode outMode, final Prop prop){
      return appIO.connect(outMode, prop, appBase.askGraf);
   } // connect(OutMode, Prop)

//-------------------------------------------------------------------------

/** The return code of the application. <br />
 *  <br />
 *  Start value: {@link #NO_PARS_ERROR} (Start and evaluation problems)<br />
 *  <br />
 *  Hint: Internal use. Not to be manipulated by inheritor's.<br />
 */   
   protected int retCode = NO_PARS_ERROR;
   
//---------------------------------------------------------------------------

/** Comfortable start of the application. <br />
 *  <br />
 *  This is a comfortable and universal starting method for an
 *  {@link App}lication featuring evaluation of start arguments .properties
 *  files and (inter) nationalising.<br />
 *  <br />
 *  This method also sets the code pages of {@link de.frame4j.io.AppIO#in in},
 *  {@link #out out} and {@link AppIO#err err} if this application is the base
 *  (&quot;mother&quot;) application.<br />
 *  <br />
 *  After all initialisations the (overridden) method {@link #doIt() doIt()}
 *  will be called.<br />
 *  <br />
 *  The call is slightly equivalent to:<code><br />
 *  {@link #setCodePages(String) setCodePages(codePage)};<br />
 *  prop = {@link Prop#Prop(App, CharSequence)
 *  new Prop}(this, commBeg, partial);<br />
 *  go(args,  1, 0);</code><br />
 *  <br />
 *  {@link #NO_PARS_ERROR} or {@link #INIT_ERROR} is returned on 
 *  initialisation problems (without calling {@link #doIt() doIt()}).<br />
 *  Otherwise {@link #doIt() doIt()}'s return value is returned.<br />
 *  <br />
 *  @param  args    start arguments passed on (not null)
 *  @param  codePage the encoding for in, out and err (or null)
 *  @param  commBeg the start pattern of a start argument's comment;
 *          the comment would be the end of the start arguments
 *  @return {@link #NO_PARS_ERROR}: Start problem,<br />
 *  &nbsp; &nbsp;  0: OK (or any other return from doIt())
 *  @see #go(String[], String)
 *  @throws IllegalArgumentException on parameter evaluating errors
 *  @throws FileNotFoundException base .properties file is missing
 */
   protected final int go(final String[] args, 
                      final String codePage, final CharSequence commBeg) 
                   throws FileNotFoundException, IllegalArgumentException {
       return go(args, codePage, commBeg, false);
   } // go(final String[], String, CharSequence)

/** Comfortably start the application. <br />
 *  <br />
 *  This method is equivalent to 
 *  {@link #go(String[], String, CharSequence)
 *     go(args, null, partial, null)} except for those extras:<br />
 *  <br />
 *  Before calling  {@link #doIt()} the equivalent of the following will
 *  be done<code><pre>
 |          if (connect(outMode, prop) &gt; 0) 
 |             return minDoc.errMeld({@link #LOG_OUT_ERROR}, valueLang(&quot;logouterr&quot;));
 |
 |           if (help) {
 |               minDoc.list(log);
 |               return 0;
 |           } // -help oder -?</pre></code>
 *
 * That would be the standard start of most non graphical applications and
 * can hence be omitted using this go().<br />
 *  <br />
 *  Hint: If {@link #help} is true or {@link #isHelpLog()} returns true, the
 *  method {@link #doIt()} is not called; 0 ({@link #JOB_DONE_OK}) is
 *  returned as exit code.<br />
 *  <br />
 *  @param  args    the start arguments passed on
 *  @see #go(String[], String, CharSequence)
 *  @see #condHelpLog()
 *  @return {@link #NO_PARS_ERROR}: Start problem,<br />
 *  &nbsp; &nbsp;  0: OK (or any other return from doIt())
 *  @throws java.io.FileNotFoundException forwarded 
 *         from {@link #go(String[], String, CharSequence)}
 */
   protected final int go(final String[] args) 
                    throws FileNotFoundException, IllegalArgumentException {
       return go(args,  null, null, true);
   } //  go(final String[])

   
/** Comfortable start of the application. <br />
 *  <br />
 *  This is the most comfortable starting method for an
 *  {@link App}lication featuring evaluation of start arguments .properties
 *  files and (inter) nationalising.<br />
 *  <br />
 *  This method also sets the code pages of {@link de.frame4j.io.AppIO#in in},
 *  {@link #out out} and {@link AppIO#err err} if this application is the base
 *  (&quot;mother&quot;) application.<br />
 *  <br />
 *  After all initialisations the (overridden) method {@link #doIt() doIt()}
 *  will be called.<br />
 *  <br />
 *  <br />
 *  The call is slightly equivalent to:<code><br />
 *  {@link #setCodePages(String) setCodePages(codePage)};<br />
 *  prop = {@link Prop#Prop(App, CharSequence)
 *  new Prop}(this, commBeg, partial);<br />
 *  go(args,  1, 0);</code><br />
 *  <br />
 *  {@link #NO_PARS_ERROR} or {@link #INIT_ERROR} is returned on 
 *  initialisation problems (without calling {@link #doIt() doIt()}).<br />
 *  Otherwise {@link #doIt() doIt()}'s return value is returned.<br />
 *  <br />
 *  @param  args    start arguments passed on (not null)
 *  @param  codePage the encoding for in, out and err (or null)
 *  @return {@link #NO_PARS_ERROR}: Start problem,<br />
 *  &nbsp; &nbsp;  0: OK (or any other return from doIt())
 *  @see #go(String[])
 *  @throws IllegalArgumentException on parameter evaluating errors
 *  @throws FileNotFoundException base .properties file is missing
 */
   protected final int go(final String[] args, final String codePage)
                   throws FileNotFoundException, IllegalArgumentException {
      return go(args, codePage, null, false);
   } // go(String[], String)


/** Internal go() method. <br />
 *  <br />
 *  @param  args    start arguments passed on (not null)
 *  @param  codePage the encoding for in, out and err (or null)
 *  @param commBeg  start of comment in start parameters (; often used) 
 *  @param stdStart true: do also the connect and help 
 *  @return {@link #NO_PARS_ERROR}: Start problem,<br />
 *  &nbsp; &nbsp;  0: OK (or any other return from doIt())
 */ 
   final int go(final String[] args,
                           final String codePage, 
                           CharSequence commBeg,  boolean stdStart) 
                     throws FileNotFoundException, IllegalArgumentException {
      setCodePages(codePage);
      if (args == null) {
         appBase.errorExit(this, NO_PARS_ERROR, "no parameter array");
         return NO_PARS_ERROR;
      } // exit args == null
      this.args = args;
      if (args.length != 0) {
        argsOrig = new String[this.args.length];
        int i = -1;
        for (String arg : this.args) { argsOrig[++i] = arg; }
      } // args not empty; save orig; since 30.04.2021
      prop = new Prop(this, commBeg);
      mainThread = Thread.currentThread();
      stdSt:  if (stdStart) {
         if (connect(outMode, prop) > 0) {
            errMeld(System.err, LOG_OUT_ERROR, valueLang("logouterr"));
            retCode = LOG_OUT_ERROR;
            break stdSt;
         } 
         //   log.println("  ////// TEST  help = " + help);
         if (condHelpLog()) {
            retCode = JOB_DONE_OK; // if true condHelpLog stops
            break stdSt;      // so we won't normally get here
         }
         if (isDebug()) {
            log.println();
            log.println(twoLineStartMsg().toString());
            log.println('\n');
            prop.list(log);
            log.println(
                 "\n  ----------------------------/debug, start/------\n");
            log.flush();
         } // debug
         stdStart = false;  // no error, no help
      } //  stdSt:  if 
      
      if (!stdStart) try {
         retCode = doIt();  // the programme's work
      } catch (Exception e) {  // unhandled Exception in the main thread
         retCode = MAIN_THREAD_EXC;
         mainThread = null;
         runFlag = false;
         log.println("\n\n   /////// ----------------------   \n\n" +
                                 "   ///  unhandled Exception in main thread (doIt()):\n");
         if (isVerbose()) {
            e.printStackTrace(log);  
         } else 
            log.println(e.getMessage());
         log.flush();
         log.println(twoLineEndMsg());
         appBase.errorExit(this, retCode, null);
         return retCode;
      } // unhandled Exception in the main thread
  
      //  mainThread null => others alive    since 14.02.2006
      if (retCode != 0  || !runFlag || mainThread != null) { // main
         mainThread = null;
         appBase.normalExit(this, retCode);
      }  // main-Thread-Error or norm-End 
      mainThread = null;
      return retCode;
   } // go(String[], String, CharSequence, boolean)

/** Report an exception. <br />
 * 
 *  @param out the output for the report
 *  @param exc the exception to report on
 *  @param trace true: print also a stack trace
 *               (and only if out is a PrintWriter or a PrrintStream)
 */
  public void repExc(final Appendable out, final Throwable exc,
                                     final boolean trace){
    if (out == null || exc == null) return;
    Throwable ex = exc.getCause();
    if (ex == null) ex = exc;
    try {
      out.append("\n  ").append(PROG_SHORT).append(' ')
         .append(ex.getClass().getName());
      out.append("\n  ").append(PROG_SHORT).append(' ')
         .append(ex.getMessage()).append('\n');
      if (out instanceof Flushable) ((Flushable)out).flush();
    } catch (IOException e) { // should not happen for usual output objects
      return; // just ignore
    } // should not happen for StringBuilder, PrintWriter, PrintStream etc.
    if (trace) {
      if (out instanceof PrintWriter) {
        exc.printStackTrace((PrintWriter) out);
      } else if (out instanceof PrintStream) {
        exc.printStackTrace((PrintStream) out);
      } 
    } // trace
  } // repExc(Appendable, Throwable) 

  
//-------------------------------------------------------------  
  
/** Make an (inter) nationalised message.. <br />
 *  <br />
 *  By using 
 *   {@link #prop}.{@link Prop#valueLang(CharSequence, String)
 *      valueLang(key, null)}
 *  a user language dependent pattern is determined for {@code key}. If the 
 *  pattern cannot be determined the empty String
 *  {@link ComVar#EMPTY_STRING} is returned.<br />
 *  <br />
 *  If {@code param} is the pattern (itself) is returned. (Therefore the
 *  calling {@link Prop#valueLang(CharSequence, String) valueLang()} would
 *  have been sufficient.)<br />
 *  <br />
 *  Using the determined pattern and the parameter {@code param} the method
 *  {@link TextHelper#messageFormat(StringBuilder, CharSequence, Object)
 *  messageFormat(null, pattern, param)} is used to form a message that will
 *  be returned as String.<br />
 *  <br />
 *  @param key   language independent key for a language dependent pattern
 *  @param param the parameter(s) to be parts of the (nationalised) 
 *               message to be formed
 *  @return      the (inter) nationalised message as String
 */
   public final String formMessage(final String key, final Object param) {
      String pattern = prop.valueLang(key, null);
      /// log.println(" /// TEST formMessage(" + key + ", " + param 
          ///                     + ") patt= "  + pattern);
      if (pattern == null) return ComVar.EMPTY_STRING;
      return TextHelper.messageFormat(null, pattern, param).toString();
   } // formMessage(String, Object)
   
/** Make an (inter) nationalised message. <br />
 *  <br />
 *  See {@link #formMessage(String, Object)}; this is the &quot;auto
 *  boxing&quot; variant for the frequent case of just one (index [0]) 
 *  integer message parameter.<br />
 *  <br />
 *  @param key    language independent key for a language dependent pattern
 *  @return  the (inter) nationalised message as String
 */ 
   public final String formMessage(final String key) {
      return formMessage(key, this);
   } // formMessage(String, int)

/** Make an (inter) nationalised message. <br />
 *  <br />
 *  See {@link #getMessageComponent(int)}; this is the &quot;self
 *  contained&quot; variant using this {@link App App's}
 *  {@link #getMessageComponent(int) message components[5..23]}.<br />
 *  <br />
 *  @param key    language independent key for a language dependent pattern
 *  @param parInt the one integer parameter to be parts of the (nationalised) 
 *                message to be formed
 *  @return  the (inter) nationalised message as String
 */ 
   public final String formMessage(final String key, final int parInt) {
      return formMessage(key, new int[] {parInt});
   } // formMessage(String, int)


//--------------------------------------------------------------------------

/** This application's working method. <br />
 *  <br />
 *  This method, being the sole abstract one, has to be implemented by an
 *  inheritor of {@link App}. <br />
 *  <br />
 *  The application's intrinsic or core task are usually done here after 
 *  having inherited all initialisation by {@link App}'s constructors and
 *  {@link #go(String[]) go()} methods in a simple main(String[])
 *  just doing {@code new MyApp().go(args, false)}, for example.<br />
 *  <br />
 *  @see Prop Prop
 *  @see #go(String[])
 *  @see #go(String[], String, CharSequence)
 *  @see #mainThread
 *  @return the exit code; 0 means OK
 */
   abstract protected int doIt(); // the only one abstract method in App

  
//-------------- Termination  ------------------------------------------

/** Normal exit. <br />
 *  <br />
 *  The call is equivalent to<br />
 *  {@link #appBase}.{@link AppBase#normalExit(Object, int)
 *   normalExit(this, returnCode)}.<br />
 *  <br />
 *  @param returnCode &nbsp; return/exit value; &gt; 0 (on Win/DOS) : error
 *        number
 *  @return returnCode
 *  @see #JOB_DONE_OK
 *  @see #NO_PARS_ERROR
 *  @see #LOG_OUT_ERROR
 *  @see #INIT_ERROR
 */
   public int normalExit(final int returnCode){ 
       appBase.normalExit(this, returnCode);
       return returnCode;
   } // normalExit(int) 

/** Error exit. <br />
 *  <br />
 *  The call is equivalent to<br />
 *  {@link #appBase}.{@link AppBase#errorExit(Object, int, String)
 *   errorExit(this, returnCode, errText)}.<br />
 *  <br />
 *  @param errNum    &gt; 0 : error number
 *  @param errText   additional error text
 *  @return errNum   for comfortable use in {@link #doIt()}'s return
 *  @see #JOB_DONE_OK
 *  @see #NO_PARS_ERROR
 *  @see #LOG_OUT_ERROR
 *  @see #INIT_ERROR
 */
   public int errorExit(final int errNum, final String errText) {
      appBase.errorExit(this, errNum, errText);
      return errNum;
  } // errorExit(int, String) 

/** Error exit with exception. <br />
 *  <br />
 *  If the verbosity level ({@link #verbosity}) is &gt;  
 *  {@link AppHelper#VERBOSE}
 *  the stack trace of {@code ex} will be output to {@link #log}; otherwise
 *  only {@code ex}'s message is used.<br />
 *  <br />
 *  The call ends by <br /> &nbsp;
 *  {@link #appBase}.{@link AppBase#errorExit(Object, int, String)
 *   errorExit(this, returnCode, null)}.<br />
 *  <br />
 *  @param errNum   &gt;0 : error number
 *  @param ex   the exception that made any further work senseless
 *  @param errText   additional error text; may usually be null as ex's
 *                   message will be logged 
 *  @return errNum   for comfortable use in {@link #doIt()}'s return
 *  @see #NO_PARS_ERROR
 *  @see #LOG_OUT_ERROR
 *  @see #INIT_ERROR
 */
   public int errorExit(final int errNum, final Exception ex,
                                                 final String errText) {
      if (ex != null) {
         if (verbosity < AppHelper.VERBOSE) {
            ex.printStackTrace(log);
         } else {  // ex.name added 12.04.2021
            log.println(ex.getClass().getName() + ": " + ex.getMessage());
         }
      }
      appBase.errorExit(this, errNum, errText);
      return errNum;
  } // errorExit(int, Exception, String) 

//--------  Information about the application itself --------------------   

/** The application's execution time (milliseconds) so far as text. <br />
 *  <br />
 *  @return time since the application's start formatted as
 *          9788ms, 3599s, 1h00min or  1d00h00min
 *  @see #getExecTimeMs()
 *  @see AppBase#setActTime()
 *  @see TextHelper#durationAsString(long)
 */
   public final String getExecTimeString(){
     return TextHelper.durationAsString(getExecTimeMs());
   } //  getExecTimeString()

/** The application's execution time (milliseconds) so far. <br />
 *  <br />
 * @return Elapsed (not CPU) time in milliseconds since startup;
 * @see #getExecTimeString()
 * @see AppBase#setActTime()
 */
   public final long getExecTimeMs(){
      final long nowMS = SynClock.sys.setActTimeMs();
      return nowMS - appStartTimeMS;    
   } // getExecTimeMs()
   
/** The application's execution time (milliseconds) so far. <br />
 *  <br />
 * @return Elapsed (not CPU) time in (Long) milliseconds since startup;
 * @see #getExecTimeMs()
 * @see AppBase#setActTime()
 */
   @Override public final Long getExecTimeMsL(){
      final long nowMS = SynClock.sys.setActTimeMs();
      return new Long(nowMS - appStartTimeMS);    
   } // getExecTimeMsL()

/** A (short) description of this application (about text). <br />
 *  <br />
 *  @return the short about text
 */
   @Override public final String getAbout(){
      return getAboutText();
   } // getAbout()
   

/** A text to guide the user of this App based application (help-Text). <br />
 *  <br />
 *  This method generates and returns a (nationalised, multi line) text 
 *  composed from name, version and copyright information plus<ul>
 *  <li> the property &quot;helpText&quot;&nbsp; or  if not available</li>
 *  <li> the {@link MinDoc}'s annotation &quot;usage&quot;&nbsp; 
 *       supplemented by {@link App}'s common options info.</li></ul> 
 *  @return the help text     
 */
   @Override public String getHelp() {
      return getHelpText();
   } // getHelp()
   
/** Version with date and optional author / last modifier. */ 
   String versDate;

/** Set version with date and optional author / last modifier. <br />
 *  <br />
 *  This method sets the field {@link #getVersDate versDate}, it it was not
 *  yet set and if the parameter is not empty even after stripping surrounding
 *  white space.<br />
 *  <br />
 *  @see #getVersDate()
 *  @param versDate version and date of this {@link App} inheritors
 *         class (taken without surrounding white spaces)
 */
   public void setVersDate(String versDate){
      if (versDate != null) {
         this.versDate = TextHelper.trimUq(versDate, ComVar.EMPTY_STRING);
         return;
      }
      String v = null;
      String d = null;
      String m = null;
      if (ano != null) {
         v = TextHelper.trimUq(ano.version(), null);
         d = TextHelper.trimUq(ano.lastModified(), null);
         m = TextHelper.trimUq(ano.lastModifiedBy(), "A. Weinert");
      } 
      if (v == null) { 
         MinDoc dano = App.class.getAnnotation(MinDoc.class);
         v = TextHelper.trimUq(dano.version(), "V.xxx");
         if (d == null) d = TextHelper.trimUq(dano.lastModified(), null);
         if (m == null) m =
            TextHelper.trimUq(dano.lastModifiedBy(), "A. Weinert");
      }
      versDate = v;
      if (d != null || v != null) {
         versDate += " (";
         if (d != null) {
            versDate += d;
            if (m != null) versDate += ", ";
         }
         if (m != null) versDate += m;
         versDate += ")";
      }
      this.versDate = versDate;
   } // setVersDate(String)
   
   @Override public String getVersDate(){
      if (versDate != null) return versDate;
      setVersDate(null); // set default
      return versDate;
   } // getVersDate()

   @Override public String getName(){
      if (name != null) return name;
      setName(null); // set default
      return name;
   } // getName()
   
/** The application's name. */   
   protected String name;

/** Set the (short) name. <br />
 *  <br />
 *  @param name the application's short name
 */
   public void setName(String name){
      if (name != null) {
         this.name = TextHelper.trimUq(name, ComVar.EMPTY_STRING);
         return;
      }
      this.name = shortClassName;
   } // setTitle(String)

/** The application's name, version and last modification date. <br />
 *  <br />
 *  @return the applications name, version and revision date
 * */   
   public String getNameWithVersDate(){
      String vD = getVersDate();
      String n = getName();
      if (vD == null) return n;
      if (n == null) return vD;
      return  n + " " + vD;
   } // getNameWithVersDate()
 

//-----------------------------------------------------------

/** The author. <br /> */ 
   String author;

/** The author. <br />
 *  <br />
 *  See {@link UIInfo#getAuthor() description} in {@link UIInfo}.<br />
 *  This method returns the name(s) explicitly set or tries to set and 
 *  get a default by {@link #setAuthor(String) setAuthor(null)}.<br />
 *  <br />
 *  Noting set is returned as {@link ComVar#EMPTY_STRING}.<br />
 *  <br />
 *  default: {@link  ComVar#AUTHOR}<br />
 */ 
   @Override public String getAuthor(){
      if (this.author != null) return this.author;
      setAuthor(null);
      return this.author;
   } // getAuthor()

/** Set the author(s). <br />
 *  <br />
 *  See {@link UIInfo#getAuthor() description} in {@link UIInfo}.<br />
 *  <br />
 *  The parameter, stripped from surrounding white space, is taken as 
 *  author(s) of the application.<br />
 *  null does nothing if already set. Otherwise a default value is determined
 *  from {@link MinDoc}-{@link Annotation annotation} or as 
 *  {@link ComVar#AUTHOR}.<br />
 *  <br />
 *  @param author null and already set: do nothing;<br /> &nbsp; &nbsp; 
 *       null and not set: get default; else: set as author.
 */
   public void setAuthor(String author){
      if (author == null) {
         if (this.author != null) return;
         if (ano != null) {
            author = ano.author();
         }
         this.author = TextHelper.trimUq(author, ComVar.AUTHOR);
         return;
      }
      this.author = TextHelper.trimUq(author, ComVar.EMPTY_STRING);
   } // setAuthor(String)  

//------------------------------------------------------------------------

/** Copyright. <br />  */ 
   String copyright;

/** The copyright notice. <br />
 *  <br />
 *  This method provides this application's copyright note in the usual 
 *  format<br />
 *  Copyright 1999 - 2002, Albrecht Weinert <br />
 *  <br />
 *  default: {@link ComVar#COPYRIGHT}<br />
 *  <br />
 *  @see #getAbout()
 *  @see #getAuthor()
 */ 
   @Override public String getCopyright(){
      if (this.copyright == null) setCopyright(null); // set default
      return this.copyright;
   } // getCopyright()

/** Set the copyright notice. <br />
 *  <br />
 *  This method sets this application's copyright note 
 *  {@link #getCopyright() copyright}, only if not yet set and if the 
 *  parameter, stripped from surrounding white spaces, is not empty.<br />
 *  <br />
 *  @param copyright The note to be set
 *  @see #getCopyright()
 *  @see #getAbout()
 *  @see #getAuthor()
 */
   public void setCopyright(CharSequence copyright){
      if (copyright == null) {
         if (ano != null) {
            copyright = ano.copyright();
         } else {
            this.copyright = ComVar.COPYRIGHT;
            return;
         }
      }
      this.copyright = TextHelper.trimUq(copyright, ComVar.COPYRIGHT);
   } // setCopyright(CharSequence)

//------------------------------------------------------------------------

/** The purpose of this application. */
   String purpose;

/** The purpose of this application. <br />
 *  <br />
 *  This is a short (not longer than eight lines) description of this class /
 *  application. It is the essential part of an &quot;about text&quot;.<br />
 *  <br />
 *  In this text's original setting  a  sequence {11} gets replaced by the 
 *  ({@link #getName name}) (see {@link #getMessageComponent(int)}).<br />
 *  <br />
 *  Example: <br /> &nbsp; &nbsp; 
 * &quot;The application {11} copies a file or an URL into a file &quot;<br /> 
 *  <br />
 *  @return the short application's purpose text
 */
   @Override public String getPurpose(){
      if (this.purpose != null) return this.purpose;
      setPurpose(null); // set default
      return this.purpose;
   } // getPurpose()

/** Set the purpose of this application. <br />
 *  <br />
 *  This method is to set a short purpose text for this class / 
 *  application.<br />
 *  <br />
 *  If the parameter is null, the text to
 *  set is taken from the {@link MinDoc}'s purpose field. If no such 
 *  annotation {@link #ano} is set (nearly impossible) or its purpose field is
 *  empty  &quot;de.frame4j.util.App&nbsp;&lt;-- &nbsp;{11}&quot; is
 *  set instead.<br />
 *  <br /> 
 *  @param  purpose null: no effect if set or set to annotation (default)
 *  @see #getPurpose()
 *  @see #getMessageComponent(int)
 */
   public void setPurpose(final CharSequence purpose){
      String purp = null;
      if (purpose == null) {
         // if (this.purpose != null) return; // already set null no change
         if (ano != null)   purp = TextHelper.trimUq(ano.purpose(), null);
         if (purp == null)  purp = "de.frame4j.util.App <-- {11}";
      } else {
         purp = TextHelper.trimUq(purpose, ComVar.EMPTY_STRING);
      }
      this.purpose = TextHelper.messageFormat(null, purp, this).toString();
   } // setPurpose(CharSequence)

/** A (short) description of this application (about text). <br /> */
   String aboutText;

/** A (short) description of this application (about text). <br />
 *  <br />
 *  @return the short about text
 */
   public String getAboutText(){
      if (this.aboutText == null) setAboutText(null); 
      return this.aboutText ;
   } // getAboutText() 

/** Set the (short) description of this application (about text). <br />
 *  <br />
 *  This method is to set a short introductory text for this class / 
 *  application.<br />
 *  If the parameter is null and the about text was not set yet the text will
 *  be composed of (as default setting) {@link App#getNameWithVersDate()},
 *  {@link #getCopyright()} and {@link #getPurpose()}.<br />
 *  <br />
 *  @param  aboutText  null: no effect if set or set default. <br />
 *  @see #getAbout()
 *  @see #getMessageComponent(int)
 */
   public void setAboutText(final CharSequence aboutText) {
      CharSequence about = aboutText;
      if (aboutText == null) {
         if (this.aboutText != null) return; // already set null no change
         StringBuilder bastel = new StringBuilder(500);
         
         bastel.append(getNameWithVersDate()).append("\n\n");
         bastel.append(getCopyright()).append("\n\n");
         bastel.append(getPurpose()).append("\n\n");
         about = bastel;
      } 
      this.aboutText = TextHelper.trimUq(about, ComVar.EMPTY_STRING);
   } // setAboutText(CharSequence)

//------------------------------------------------------------------------
   
/** AttrSettable implementation. <br />
 *  <br />
 *  This method does the the settings of the properties: 
 *  {@link #getVerbosity() verbose/verbosity}, 
 *  {@link #getOutMode() outMode},
 *  {@link #getTitle() title}, 
 *  {@link #getAuthor() author},
 *  {@link #getAbout() aboutText}, 
 *  {@link #bgColor}, {@link #setCodePages(String) codePages}, 
 *  {@link #getCopyright() copyright}, 
 *  {@link #getName() name}, {@link #getUsage() usage} and 
 *  {@link #getVersDate() versDate} (mostly) by delegating to the appropriate 
 *  setter (with one parameter).<br />
 *  <br />
 *  One effect of this method is the saving of introspection expenses at
 *  startup. Inheritors may override the method 
 *  {@link #setAttribute(String, char, Object, Class, boolean, boolean)} to
 *  implement similar savings if adding many settable properties. It is
 *  called here if none of the property settings implemented here is 
 *  affected.<br />
 *  <br />
 *  The intensive checking and forwarding of essential results makes the 
 *  implementation / overriding of 
 *  {@link #setAttribute(String, char, Object, Class, boolean, boolean)} 
 *  quite simple. Nevertheless, overriding this method 
 *  {@link #setAttribute(String, Object)} is also feasible. In any case
 *  the overriding method must delegate back to the overridden one if it
 *  could not handle a non null name.<br />
 *  <br />
 *  @param name the property's / attributes name; if null, empty or not 
 *              lower case (Java name convention) {@link #NO_ATTRIBUTE}
 *              is returned and nothing is done
 *  @param value the value to set as Object; the type {@link CharSequence}
 *              should always be accepted
 *  @return status / result of the setting            
 */
   @Override public final int setAttribute(final String name, Object value){
      if (name == null || name.isEmpty()) return NO_ATTRIBUTE;
      final char name0 = name.charAt(0);
      if (name0 < 'a') return NO_ATTRIBUTE; // Java name convention: LC!
      final boolean isNull = value == null;
      final Class<? extends Object>  vClass = isNull ? null : value.getClass();
      boolean isStringVal = false;
      if (!isNull && value instanceof CharSequence) {
         if (vClass != java.lang.String.class) value = value.toString();
         isStringVal = true;    // value is String
      } // is charSec
      int ret = 
         setAttributeImpl(name, name0, value, vClass, isNull, isStringVal);
      if (ret == NO_KNOWN_ATTRIBUTE)
         ret = setAttribute(name, name0, value, vClass, isNull, isStringVal);
      return ret;
   } // setAttribute(String, Object) 
   
/** Overridable implementation of setAtttibute(String, Object). <br />
 *  <br />
 *  This method will be called by 
 {@link App}.{@link #setAttribute(String, Object) setAttribute(name, value)} 
 *  for all name value pairs not clearly handled or rejected by said 
 *  App's method. In this call all parameters are correct and the parameter 
 *  {@code value} is converted to {@link String} if it was (only) of type
 *  {@link CharSequence}.<br />
 *  <br />
 *  Hint: The gain (for the prize of 6 parameters) is that all preliminaries,
 *  and checks otherwise necessary (if directly overriding 
 *  {@link #setAttribute(String, Object)}) are superfluous here.<br />
 *  <br />
 *  This implementation does nothing. It  
 *  @param  name        name of the property (never empty)
 *  @param  name0       first character of name (&gt;= a)
 *  @param  value       the (new) value of the property
 *  @param  vClass      the (original) class of value (or null if isNull)
 *  @param  isNull      true if value is null
 *  @param  isStringVal  true if value is of type String
 *  @return {@link #NO_KNOWN_ATTRIBUTE}
 *  @see AttrSettable
 *  @see #ILLEGAL_TYPE
 *  @see #ILLEGAL_VALUE
 *  @see #NO_VALUE 
 *  @see #OK
 */
   protected int setAttribute(final String name, final char name0,
                           final Object value, final Class<?> vClass,
                          final boolean isNull, final boolean isStringVal){
      return NO_KNOWN_ATTRIBUTE; 
   } // setAttribute(String, char, Object, Class<?>, 2*boolean)
   
   private final int setAttributeImpl(final String name, final char name0,
                           final Object value, final Class<?> vClass,
                          final boolean isNull, final boolean isStringVal){   
      switch (name0) {
         case 'a' :   
            if ("author".equals(name)) {
               if (isStringVal || isNull) {
                  setAuthor((String)value);
                  return 0; // OK
               }
               return ILLEGAL_TYPE; 
            }
            if ("aboutText".equals(name)) {
               if (isStringVal || isNull) {
                  setAboutText((String)value);
                  return 0;
               }
               return ILLEGAL_TYPE; 
            }
            if ("askGraf".equals(name)) return 0; // ignore handled in Prop
            break; // a
         case 'b' :
            if ("bgColor".equals(name)) {
               if (isNull) return 0; // done
               if (!isStringVal) return 4;
               setBgColor((String)value);
               return 0;
            }
            break; // a
         case 'c' :
            if ("codePages".equals(name)) {
               if (isStringVal || isNull) {
                  setCodePages((String)value);
                  return 0;
               }
               return ILLEGAL_TYPE; 
            }
            if ("copyright".equals(name)) {
               if (isStringVal || isNull) {
                  setCopyright((String)value);
                  return 0;
               }
               return ILLEGAL_TYPE; 
            }
            break; // c
         case 'h' :
           if ("helpText".equals(name)) {  // ignore helptext internals
             return 0; 
           } // ignore helptext internal handling
           break; // h

         case 'n' :
            if ("name".equals(name)) {
               if (isStringVal || isNull) {
                  setName((String)value);
                  return 0;
               }
               return ILLEGAL_TYPE; 
            }
            break; // n
         case 'o' :
            if ("outMode".equals(name)) {
               if (isNull) return 0; // done
               if (!isStringVal) return ILLEGAL_TYPE; // 4
               setOutMode((String)value);
               return 0;
            }  
            break; // o
         case 'p' :
           if (name.startsWith("propFile")) {  // ignore helptext internals
             return 0; // ignore  propFileName propFileVers propFileDate 
           } // ignore helptext internals
           break; // p
         case 't' :
            if ("title".equals(name)) {
               if (isStringVal || isNull) {
                  setTitle((String)value);
                  return 0;
               }
               return ILLEGAL_TYPE; 
            }  
            break; // t
         case 'u' :
            if ("usage".equals(name)) {
               if (isStringVal || isNull) {
                  setUsage((String)value);
                  return 0; // OK
               }
               return ILLEGAL_TYPE; 
            }
            break; // u
         case 'v' :
            // verbose, verbosity, verbosityString
            if (TextHelper.startsWith(name, "verbos", false)) {
               if (isStringVal || isNull) {
                  setVerbosity((String)value);
                  return 0;
               }
               if (vClass == Integer.class)  {
                  setVerbosity(((Integer)value).intValue());
                  return 0;
               }
               if (vClass != Boolean.class) return ILLEGAL_TYPE;
               setVerbosity(((Boolean)value).booleanValue()
                                 ? AppHelper.VERBOSE : AppHelper.NORMAL);
               return 0;
            } // verbose

            if ("versDate".equals(name)) {
               if (isStringVal || isNull) {
                  setVersDate((String)value);
                  return 0;  // OK
               }
               return ILLEGAL_TYPE; 
            }
            break; // v
      } // switch
      return NO_KNOWN_ATTRIBUTE; 
   } // setAttributeImpl(String, char, Object, Class<?>, 2*boolean)


//------------------------------------------------------------------------

/** The usage (quick help). */
   String usage;

/** The usage (quick help). <br />
 *  <br />
 *  This is a short textual guidance to use this class / application. It may
 *  be used in the sense of a online help. There's no limit to the number of
 *  lines, bat more than 50 usually should go to a separate multi-page
 *  manual.<br />
 *  <br />
 *  In this text's original setting  a  sequence {11} gets replaced by the 
 *  ({@link #getName name}) (see {@link #getMessageComponent(int)}).<br />
 *  <br />
 *  Example: <br /> &nbsp; &nbsp; 
 *   &quot;Start by java {11} [file  | URL [file]]&quot;<br /> 
 *  <br />
 *  @see #getHelpText() 
 */
   @Override public String getUsage(){
      ///log.println("  ////// TEST  getUsage () " + usage);
      if (this.usage == null) setUsage(null); // get default
      return this.usage;
   } // getUsage()

/** Set the usage (quick help). <br />
 *  <br />
 *  The short textual guidance (see {@link #getUsage()}) will be set.<br />
 *  If the parameter is null and the text was not set yet it will be 
 *  taken from the {@link MinDoc}'s pusage field. If no such 
 *  annotation {@link #ano} is set (nearly impossible) or its usage field is
 *  empty  &quot;java&nbsp;{10}&nbsp;-help&quot; is
 *  set instead.<br />
 *  <br />
 *  @param  usage  null: no effect if set or set default. <br />
 *  @see #getUsage()
 *  @see #getMessageComponent(int)
 */
   public void setUsage(final CharSequence usage){
      /// log.println("  ////// TEST  setUsage (" + usage);
      String use = ComVar.EMPTY_STRING;
      if (usage == null) {
         if (this.usage != null) return; // already set null no change
         if (ano != null) {
            use = TextHelper.trimUq(ano.usage(), ComVar.EMPTY_STRING);
         }   
         if (use == ComVar.EMPTY_STRING) use = "java {10} -help";
      } else {
         use = TextHelper.trimUq(usage, ComVar.EMPTY_STRING);
      }
      /// log.println("  ////// TEST  setUsage by " + use);
      this.usage = TextHelper.messageFormat(null, use, this).toString();
   } // setUsage(CharSequence)

/** A text to guide the user of this App based application (help-Text). */
   String helpText;

/** A text to guide the user of this App based application (help-Text). <br />
 *  <br />
 *  This method generates and returns a (nationalised, multi line) text 
 *  composed from name, version and copyright information plus<ul>
 *  <li> the property &quot;helpText&quot;&nbsp; or  if not available</li>
 *  <li> the {@link MinDoc}'s annotation &quot;usage&quot;&nbsp; 
 *       supplemented by {@link App}'s common options info.</li></ul> 
 *  @return the help text     
 */
   public String getHelpText(){
      if (helpText != null) return helpText;
      String vD = getVersDate();
      String n = getName();
      String ht  = prop.getHelpText(null);
      /// log.println("  ////// TEST  setHelpText() ht.prop =  " + ht);

      if (ht == null) { // no help text as property
         ht = getUsage();
         if (ht == null || ht == ComVar.EMPTY_STRING) {
            ht = "  java {10} -help";
         }
         String hlpfwopt = valueLang("hlpfwopt", null);
         if (hlpfwopt != null) ht += hlpfwopt;
         ht = prop.percReplace(ht);
      } // no help text as property; defaults to usage and ...

      StringBuilder bastel = new StringBuilder(500);
      bastel.append("  \n  ").append(n).append("  ");
      bastel.append(vD).append("\n  \n  ");
      bastel.append(getCopyright());

      if (ht != null) {
          bastel.append("\n  \n");
          TextHelper.messageFormat(bastel, ht, this);
      }      
      return helpText = new String(bastel.append("\n   "));
   } // getHelpText() 
   
/** Generate a multi line error report. <br />
 *  <br />
 *  A String is generated and returned consisting of those parts:<ul>
 *  <li> an empty line </li>
 *  <li> {@link #getNameWithVersDate nameWithVersDate}<br />
 *   Error <br />
 *   errnum, if &gt; 0 </li>
 *  <li> suplText </li>
 *  </ul>
 *  If the extra text suplText is a character sequence, format elements like
 *  {11} e.g. are replaced is replaced accordingly.<br />
 *  <br />
 *  @param  errNum  the error number, if &gt; 0
 *  @param  suplText an extra text (String, StringBuilder, CharSequence
 *                   respectively Exception or Throwable)
 *  @see TextHelper#messageFormat(StringBuilder, CharSequence, Object)
 *  @see #getMessageComponent(int)
 *  @return the error report text                 
 */
   public String errorText(final int errNum, final Object suplText){
      int l = 0;
      String txt = null;
      if (suplText != null) {
         if (suplText  instanceof CharSequence)
            txt = TextHelper.messageFormat(null, 
                                      (CharSequence)suplText, this).toString();
         else if (suplText instanceof Throwable) {
            txt = ((Throwable)suplText).getMessage();
            if (txt == null || txt.isEmpty()) txt = suplText.toString();
         }
      } // Parameter suplText given
      if (txt != null)  l = txt.length();
      if (l == 0)       txt = null;
      StringBuilder bastel = new StringBuilder(l + 90);
      bastel.append("  \n");
      if (name != null) {
          bastel.append(name);
          if (versDate != null)
              bastel.append(' ').append(versDate);
          bastel.append("  ");
      }
      bastel.append("Error ");
      if (errNum > 0)
         bastel.append(errNum);
      if (txt != null)
         bastel.append("  ").append(txt); // no line feed (16.04.2021)
      return new String(bastel);
   } // errorText(int, Object)

/** Output a (multi line) error report. <br />
 *  <br />
 *  On {@link #log} an error text, produced according to
 *  {@link #errorText(int, Object) errorText(errNum, suplText)}, is 
 *  output.<br />
 *  <br />
 *  Is the parameter <code>suplText<code> an {@link Exception} and the 
 *  level of  {@link #getVerbosity() verbosity} &lt;= {@link AppHelper#TEST},
 *  the exception's call stack is output, too.<br />
 *  <br />
 *  @param  errNum  the error number, if &gt; 0
 *  @param  suplText additional text, describing the error<br /> &nbsp; &nbsp;
 *                  may be String, StringBuilder, CharSequence or Exception)
 *  @return         errNum (the error number)
 */
   public final int errMeld(final int errNum, final Object suplText){
      log.println(errorText(errNum, suplText));
      if (verbosity  <= AppHelper.TEST && suplText instanceof Throwable) {
         log.println("\n   ///  -------   stack trace:\n");
         ((Throwable)suplText).printStackTrace(log);
         log.println("\n   ///  -------   \n\n");
      }
      return errNum;
   } // errMeld(int, Object)

/** Output a (multi line) error report. <br />
 *  <br />
 *  On <code>out</code> an error text, produced according to
 *  {@link #errorText(int, Object) errorText(errNum, suplText)}, is 
 *  output.<br />
 *  <br />
 *  Is the parameter <code>suplText<code> an {@link Exception} and the 
 *  level of  {@link #getVerbosity() verbosity} &lt;= {@link AppHelper#TEST},
 *  the exception's call stack is output, too.<br />
 *  <br />
 *  @param  errNum  the error number, if &gt; 0
 *  @param  suplText additional text, describing the error<br /> &nbsp; &nbsp;
 *                  may be String, StringBuilder, CharSequence or Exception)
 *  @return         errNum (the error number)
 *  @param  out     the Stream to output to
 *  @see #condHelpLog()
*/
   public int errMeld(final PrintStream out, 
                                  final int errNum, final Object suplText){
      if (out == null) return errNum;
      out.println(errorText(errNum, suplText));
      return errNum;
   } // errMeld(PrintStream, int, Object)


} // App 08.12.2003, 29.09.2004, 22.06.2005, 15.02.2006, 25.07.2009
