/*  Copyright 2009 2021 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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

import java.io.Console;
import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import de.frame4j.io.Input;
import de.frame4j.text.TextHelper;

/** <b>Common (constant) values</b>. <br />
 *  <br />
 *  This interface accommodates some final values used by many of Frame4J's
 *  classes. Of course, they might be used elsewhere:<ul>
 *  <li>constant values related to time and date (some in both long and
 *      int flavour),</li>
 *  <li>read only time updated (only) on command by platform time with the
 *      same guaranteed monotony,</li> 
 *  <li>platform property values,</li>
 *  <li>some immutable objects for common singleton use, like the empty 
 *      String, frequently used empty arrays and so on.</li>
 *  </ul>
 *  Implementation hint: Some of the final values given by this interface are
 *  not just hard-coded but calculated or read from system data. Albrecht
 *  Weinert's &quot;embedded evaluation class&quot; pattern used in these
 *  cases allows arbitrary initialisations, more complex as initialisers would
 *  allow. But as with initialisers the order of evaluation is often relevant.
 *  So, please don't change the order of declarations, except if knowing all
 *  consequences in the light of the embedded class {@link Impl}.<br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  1997 - 2009, 2021 &nbsp;  Albrecht Weinert<br />
 *  <br />  
 *  @see      de.frame4j.util.App
 */
 // so far    V02.31 (01.03.2006) : UD
 //           V.032  (02.01.2009) : ported from de.a_weinert (German)
 //           V.128+ (21.08.2009) : JAR_ENCODING for .properties in .jar
 //           V.135+ (22.12.2015) : better CONSOLE_ENCODINg determination
 //           V.167+ (05.08.2016) : refactored to Frame4J'89 slimline
 //           V.031  (11.03.2021) : onPI and notWindows added

@MinDoc(
   copyright = "Copyright 1997 - 2009, 2021  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 42 $",
   lastModified   = "$Date: 2021-05-01 18:54:54 +0200 (Sa, 01 Mai 2021) $",
// lastModifiedBy = "$Author: albrecht $",
// usage   = "static import",  
   purpose = "Common final values, platform info, constants and so on."
) public interface ComVar {

/** The author and project owner of the framework. <br />
 *  <br />
 *  Value: {@value}
 */
   String AUTHOR = "Albrecht Weinert";
   

/** Copyright for the framework. <br />
 *  <br />
 *  Value: {@value}
 */
   String COPYRIGHT = "Copyright (c) 2021  Albrecht Weinert";
   
/** An empty array of (0) Strings. <br />
 *  <br />
 *  An empty array, being immutable, should always be used singleton. 
 *  This is for Strings.<br />
 */
   String[] NO_STRINGS = new String[0];

/** An empty array of (0) bytes. <br />
 *  <br />
 *  An empty array, being immutable, should always be used singleton. 
 *  This is for byte.<br />
 */
   byte[] NO_BYTES = new byte[0];


/** An empty array of (0) Objects. <br />
 *  <br />
 *  An empty array, being immutable, should always be used singleton. 
 *  This is for Objects.<br />
 */
   Object[] NO_OBJECTS = new Object[0];


/**  An empty array of (0) Classes. <br />
 *  <br />
 *  An empty array, being immutable, should always be used singleton. 
 *  This is for Class.<br />
 */
   Class<?>[] NO_CLASSES = new Class[0];


/** An empty array of (0) doubles. <br />
 *  <br />
 *  An empty array, being immutable, should always be used singleton. 
 *  This is for double.<br />
 */
  double[] NO_DOUBLES = new double[0];

/** Minus infinity for double. <br>
 *  <br />
 *  D_NegInf is just 
 *  {@link java.lang.Double#NEGATIVE_INFINITY Double.NEGATIVE_INFINITY}.
 */
  double D_NegInf = Double.NEGATIVE_INFINITY;

/** Plus infinity for  double. <br>
 *  <br />
 *  D_PosInf is just 
 *  {@link java.lang.Double#POSITIVE_INFINITY Double.POSITIVE_INFINITY}.
 */
  double D_PosInf = Double.POSITIVE_INFINITY;

/** Invalid value (NaN) for double. <br>
 *  <br />
 *  D_NaN is just {@link java.lang.Double}.NaN.
 */
  double D_NaN = Double.NaN;

/** An empty String. <br />
 *  <br />
 *  An empty String is used very often. String objects being immutable, it 
 *  makes sense to use just this as singleton, apart from transpiring
 *  the programmer's intention to be more readable.<br />
 *  <br />
 *  Hint: Consequent use of {@link ComVar}'s common (final) reference values
 *  allows for == in places, where equals() would else be necessary. This is
 *  used throughout the framework and should not be broken. <br />
 *  <br />
 *  Remark: String literals should allow this due to javac's use of 
 *  {@link String#intern()}; but there's no guarantee.<br />
 */
   String EMPTY_STRING = "";

/** 40 blanks. <br />
 *  <br />
 *  A String with forty blanks.
 */
   String BLANK_STRING = "                                        ";

// Just to remind you: We're in an interface. 
// All variables are public final static 

/** An empty int array. <br />
 *  <br />
 *  As empty arrays are immutable, it makes sense to use just this as 
 *  singleton, apart from transpiring the programmer's
 *  intention more readable.<br />
 *  <br />
 *  @see #EMPTY_STRING
  */
   int[] EMPTY_INT_A = new int[0];


/** Milliseconds per second. <br />
 *  <br />
 *  Value: 1000<br />
 *  <br />
 *  @see #ONE_SECOND
 */
   int S = 1000;

/** Milliseconds per minute. <br />
 *  <br />
 *  Value: 60000<br />
 *  <br />
 *  @see #ONE_MINUTE
 */
   int M = 60000;

/** Milliseconds per hour. <br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #ONE_HOUR
 */
   int H =  60 * M; 

/** Milliseconds per day. <br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  @see #ONE_DAY
 */
   int D  = 24 * H; 
   

/** Milliseconds per second. <br />
 *  <br />
 *  Value: 1000L &nbsp; (...l oder L means long)<br />
 *  <br />
 *  @see #S
 */
   long ONE_SECOND = 1000L; 

/** Milliseconds per minute. <br />
 *  <br />
 *  Value: {@value} &nbsp; (...L means long)
 */
   long ONE_MINUTE = 60000L;          // ms in 1 minute

/** Milliseconds per hour. <br />
 *  <br />
 *  Value: {@value} &nbsp; (...L means long)
 */
   long ONE_HOUR =  60L * ONE_MINUTE;  // ms in 1 hour

/** Milliseconds per day. <br />
 *  <br />
 *  Value: {@value} &nbsp; (...L means long)
 */
   public static final long ONE_DAY  = 24L * ONE_HOUR;     // ms in 1 day

/** Milliseconds per week (long). <br />
 *  <br />
 *  Value: {@value} &nbsp; (...L means long)<br />
 */
   long ONE_WEEK  = 7L * ONE_DAY;      

/** Milliseconds per year (365 days). <br />
 *  <br />
 *  Value: {@value} &nbsp; (...L means long)<br />
 */
   long ONE_YEAR  = 365L * ONE_DAY;    

/** Milliseconds per leap year (366 days). <br />
 *  <br />
 *  Value: {@value} &nbsp; (...L means long)<br />
 */
   long ONE_LEAP_YEAR  = 366L * ONE_DAY;
 

/** No start parameters. <br />
 *  <br />
 *  Something requiring a non null not empty {@link String String[]}
 *  (usually as command line parameters) was called without it.
 *  <br /> 
 *  Return-Code, Exit-Code (Error-Level); value = {@value}
 */
public static final int NO_PARS_ERROR = 99;

/** Error while evaluating start parameters and setting properties. <br />
 *  <br />
 *  An application constructor or start method ended in an exception.<br />
 *  This might have been done e.g. by calling 
 *  {@link AppBase}.{@link AppBase#exit(Exception, int)}.<br />
 *  <br /> 
 *  Return-Code, Exit-Code (Error-Level); value = {@value}
 */
public static final int INIT_ERROR = 94;

/** Not handled exception in the main thread. <br />
 *  <br />
 *  Value = {@value}
 */
public static final int MAIN_THREAD_EXC = 90;

/** Exception or error while initialising log and out. <br />
 *  <br />
 *  The initial providing of the standard {@link PrintWriter}s log and/or
 *  out failed. This may happen if command line parameters order an 
 *  unusable log file, e.g.<br />
 *  <br /> 
 *  Return-Code, Exit-Code (Error-Level); value = {@value}
 */
public static final int LOG_OUT_ERROR = 91;

/** Requested job is done. <br />
 *  <br />
 *  The requested job was done successfully.<br />
 *  <br />
 *  If the return code is to be used or interpreted as answer or user's 
 *  reaction this return value means Yes, OK, Ja, Si ...<br />
 *  <br />
 *  Return-Code, Exit-Code (Error-Level); value = {@value}
 */
public static final int JOB_DONE_OK = 0;
   
//---- End simple constants -------------------------------------  

   
/** The one (and only) Runtime-Object of the JVM. <br /> */
   Runtime RUNTIME = Runtime.getRuntime(); 

// Just to remind you: We're in an interface.
// All variables are public final static 
// And implementation hint: Due to calculations in the embedded class Impl
// from here on order of declaration is relevant
   
   
/** Operating system's name. <br />
 *  <br />
 *  The name of the underlying operating system or &quot;unknown&quot; if not
 *  ascertainable. Possible values are:<br />
 *  &quot;Windows 10&quot; &quot;Windows 2003&quot;, 
 *  &quot;Windows 2000&quot;,<br />
 *  &quot;Linux&quot; &quot;Raspbian GNU/Linux&quot;
 *  &quot;Raspbian GNU/Linux 10&quot; (buster) or so.<br />
 */
   String OS = Impl.getOS();

/** Operating system is not Windows. <br />
 *  <br />
 *  true means, the OS name was ascertainable and does'nt start with
 *  Windows.<br />
 *  false means, the OS name was not ascertainable or it starts with 
 *  Windows.<br /> 
 *  <br />
 *  default: false  
 */
   boolean NOT_WINDOWS = Impl.notWindows;
   
/** Running on a Raspberry Pi. <br />
 *  <br />
 *  true means, the (pretty) OS name starts with Raspbian (and, of course,
 *  not with Windows).<br />
 *  false means, the OS name was not ascertainable or is no Linux with
 *  readable /etc/os-release.<br />
 *  N.b.: No hardware criteria are used for this constant, only operating
 *  system name properties. If you run an unusual OS on your Pi or even 
 *  installed a Windows there ON_PI will, wrongly, be false.  
 *  <br />
 *  default: false  
 */
   boolean ON_PI = Impl.onPi;
   
/** The host name. <br />
 *  <br />
 *  If the host name could not be determined the default value will be
 *  "unknown".  
 */
   String HOST_NAME = Impl.getHostname();
   
/** The host IP. <br />
 *  <br />
 *  If the ip address could not be determined the default value will be
 *  "127.0.0.1", the local host.
 */ 
   String HOST_IP = Impl.hostIP;
   
   
/** The host IP is V4. <br />
 *  <br />
 *  The value is {@link HOST_IP} could be determined and is of Type IPv4.
 */ 
   boolean HOST_IPv4 = Impl.hostIPv4;   
   
/** The command / program name.
 *    
 *  The program / command / main() class name like e.g. de.frame4j.Exec.
 */
   String PROG_NAME = Impl.getProgramName();
   
/** The short command / program name.
 *    
 *  The short  program / command / main() class name with package part
 *  (if any) omitted, like e.g. Exec.
 */
    String PROG_SHORT = Impl.shortProgramName;
  

/** Encoding for (file) input and output. <br />
 *  <br />
 *  FILE_ENCODING is the coding of (text) characters when reading them from
 *  or writing them to files. It is a property of the underlying operating or 
 *  runtime system.<br />
 *  The value is determined by the system properties file.encoding or 
 *  sun.jnu.encoding which do normally exist on both Windows and Linux JVMs. 
 *  <br />
 *  If not ascertainable, the default is &quot;UTF-8&quot; if 
 *  {@link NOT_WINDOWS} or &quot;ISO-8859-1&quot; else.<br />
 */
   String FILE_ENCODING = Impl.getFileEncoding();


/** Encoding for (file) resources in Java archives. <br />
 *  <br />
 *  To be portable, plain text files (mainly Frame4J's .properties) in 
 *  deployment archives have to have an encoding independent of the build
 *  / development platform. This variable makes it known to the deployment
 *  platform. It will be regarded by {@link Prop}'s and {@link AppLangMap}'s
 *  relevant {@link Prop#load1(Class, CharSequence, CharSequence) load} 
 *  methods as default.<br />
 *  <br />
 *  Value: &quot;UTF-8&quot; <br />
 *  Since: V.133+ 21.08.2009 <br />
 */
   String JAR_ENCODING = "UTF-8";


/** Encoding for console input and output. <br />
 *  <br />
 *  The so called DOS-Shells (command shells) of European Windows 
 *  installations tend to use an other encoding for console output
 *  and keyboard input streams. This is usually the quite 
 *  &quote;historical&quote; code page 850 or &quot;Cp850&quot;.<br />
 *  <br />
 *  If OS starts with Windows {@code CONSOL_ENCODING} gets the value
 *  &quot;Cp850&quot;; otherwise {@code FILE_ENCODING}.<br />
 *  <br />
 *  This operating system dependent choice is overridden if a system
 *  environment variable named &quot;activeCodePage&quot; could be read. In
 *  this case its (non empty) value is taken as {@code CONSOL_ENCODING}. This
 *  holds for all operating systems.<br />
 *  <br />
 *  If it is clear, that we are not on Windows CONSOL_ENCODING gets the same
 *  value as {@link #FILE_ENCODING}.<br />
 *  <br />
 *  Hint: If the console encoding was changed, e.g. by <code>chcp<code> this
 *  would go undetected here.<br /> 
 */
   String CONSOL_ENCODING = Impl.getConsolEncoding();
   

/** The real console, if any. <br />
 *  <br />
 *  If the underlying system provides a so called DOS-Shell (command shell,
 *  Windows), terminal (Linux) or the like this is it. Otherwise the value is 
 *  null.
 */  
   Console cons = System.console();

/** The real console, if any, is provided. <br />
 *  <br />
 *  This is true, if {@link #cons} is not null. In that case the underlying
 *  system provided a shell or terminal as 
 *  {@link java.io.Console java.io.Console}.<br />
 *  Hint / Attention: Even if that would be so, this console will be removed 
 *  under Eclipse. In that case do test and run outside Eclipse.
 */  
   boolean hasCons = cons != null;


/** The property &quot;file.separator&quot;. <br />
 *  <br />
 *  This is either '\' for DOS, Windows or '/'
 *  for all other operating systems (Unix, Linux).<br />
 */
   char FS = File.separatorChar;
   

/** The property &quot;file.separator&quot;. <br />
 *  <br />
 *  This is either &quot;\&quot; for DOS, Windows or &quot;/&quot;
 *  for all other operating systems (Unix, Linux).<br />
 */
   String FSS = String.valueOf(FS);


/** The system property &quot;path.separator&quot;. <br />
 *  <br />
 *  This is either  &quot;;&quot; for DOS, Windows or &quot;:&quot;
 *  for all other operating systems (Unix, Linux).<br />
 *  <br />
 *  default: ; (hence Windows) , if no access to system properties 
 */
   char PS = Impl.getPS();


/** The system property &quot;user.region&quot;. <br />
 *  <br />
 *  This would be the country of the underlying Java installation, e.g. DE for
 *  Germany (Deutschland in German).<br />
 *  The value is read from the property &quot;user.region&quot; or from 
 *  (due to changes by JDK 1.4)&quot;user.country&quot;as substitute, if any
 *  of those two is readable.<br />
 *  <br />
 *  default: GB, if no access to system properties
 *  @see #UL
 *  @see #UL_UR_da          
 */
   String UR = Impl.getUR();


/** The system property &quot;user.language&quot;. <br />
 *  <br />
 *  This would be the language of the underlying Java installation, e.g. de
 *  for German (deutsch in German) or en for English.<br />
 *  <br />
 *  default: en, if no access to system properties  
 *  @see #UR
 *  @see #UL_UR_da          
 */
   String UL = Impl.getUL();

   // Just to remind you: We're in an interface.
// All variables are public final static 
// And implementation hint: Due to calculations in the embedded class Impl
// here the order of declaration is relevant


/** The system property &quot;user.dir&quot;. <br />
 *  <br />
 *  The &quot;user directory&quot; usually is the actual directory when 
 *  starting the JVM.<br />
 *  <br />
 *  default: null, if not to be evaluated.<br />
 */
   String UD = Impl.getUD();


/** The system property &quot;java.home&quot;. <br />
 *  <br />
 *  This is the JRE directory of a JDK installation or JRE installation
 *  without trailing &quot;file-separator&quot;.<br />
 *  <br />
 *  A usual value (in a good installation) would be:<br /> &nbsp; 
 *  C:\programme\jdk\jre<br />
 *  <br />
 *  default: null, if not to be evaluated
 */
   String JH = Impl.getJH();


/** The &quot;jre\lib&quot; directory. <br />
 *  <br />
 *  This is the jre\lib directory of a JDK or JRE installation with the
 *  trailing &quot;file-separator&quot;.<br /> 
 *  <br />
 *  A usual value (in a good installation) would be:<br /> &nbsp; 
 *  C:\programme\jdk\jre\lib\<br />
 *  <br />
 *  In this directory some framework methods 
 *  ({@link Prop#load(CharSequence, CharSequence) Prop.load()})
 *  may search for .properties files.<br />
 *  <br />
 *  default: null, if not to be evaluated
 *  @see #JH
 *  @see #FS
 */
   String JRL = JH == null ? null : JH + FS + "lib" + FS;


/** The properties user.language and .region were readable. <br />
 *  <br />
 *  The value true signalises that both {@link #UL} and {@link #UR} were 
 *  evaluated and not just set to a default value.<br />
 */
   boolean UL_UR_da = Impl.UL_UR_da;
   
/** The class loader. <br />
 *  <br />
 *  The class loader, by which the this class (interface) {@link ComVar} 
 *  was loaded is held.<br />
 *  <br /> 
 *  As the framework is usually deployed as a .jar file (put in 
 *  {@link #JRL}ext\ as installed extension, this would be the class loader 
 *  to load from that archive.<br />
 */   
    ClassLoader FRW_CLLD = Impl.getClassLoader();
    
/** The Web Start version. <br />
 *  <br />
 *  If this class (and the application involved) was loaded by Web Start
 *  this is the version String. Otherwise it is the 
 *  {@link #EMPTY_STRING EMPTY_STRING}.   
 */
    String WEB_START_VERS = Impl.getWebStartVers();
    
/** Started by Web Start. <br /> */
    static public boolean isWebStart(){ 
       return EMPTY_STRING != WEB_START_VERS;
    } // isWebStart()
    

//===================   inner class just for initialisations    ===========

/** <b>Internal implementation class (for ComVar)</b>. <br />
 *  <br />
 *  The almost single purpose in life of that embedded class is the 
 *  evaluation of some of {@link ComVar}'s final values. Albrecht Weinert's 
 *  &quot;embedded evaluation class&quot; pattern allows for run evaluations
 *  of any complexity, that initialisers for final values could never 
 *  accomplish.<br />
 *  <br />
 *  In this sense this class is not for the user of the frame work, nor does
 *  she or he need it's documentation. <br />
 *  <br />
 *  Background: This (javaDoc) documentation is only visible because all 
 *  elements in an interface have to be public. This &quot;enforced 
 *  publicity&quot; mechanism also applies to embedded classes (which is a
 *  pity). Otherwise class would have gotten package visibility (by the 
 *  author's will), thus hiding it's existence from reader's eyes.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2001 - 2009, 2015  Albrecht Weinert  
 *  @author   Albrecht Weinert
 *  @version  as enclosing Interface
 */   
  static final class Impl {

   private Impl(){} // no objects, no docu

   private static volatile boolean UL_UR_da = true;
   private static volatile boolean notWindows;
   private static volatile boolean onPi;
   

   private static volatile String hostIP = "127.0.0.1";
   private static volatile boolean hostIPv4;


/** Web Start Version or EMPTY_STRING. <br /> */
   static String getWebStartVers(){
     try {
          String ret =  System.getProperty("javawebstart.version");
          /// System.out.println(" /// TEST javawebstart.version = " + ret);
          if (ret != null) return ret; 
     } catch (Exception e) {
          /// System.out.println(" /// TEST get javawebstart.version : " + e);
     }
     return EMPTY_STRING;
   } // getWebStartVers()

/** user.language or en. <br /> */
   static String getUL(){
     try {
       return System.getProperty("user.language");
     } catch (Exception e) {
       UL_UR_da = false;
       return "en";
     }
   } // getUL()

/** user.region or user.country or GB. */
   static String getUR(){
     try {
       String tmp = System.getProperty("user.region");
       if (tmp == null) 
         tmp = System.getProperty("user.country", "GB");
       return tmp;
     } catch (Exception e) {
       UL_UR_da = false;
       return "GB";
     }
   } //  getUR()

/** user.dir or null. */
   static String getUD(){
     try {
       final String ret =  System.getProperty("user.dir");
       if (ret == null || ret.isEmpty()) return null;
       return ret;
     } catch (Exception e) { } // ignore, returning null
     return null;
   } // getUD()

/** path.separator or ; . */
   static char getPS(){
     try {
       return System.getProperty("path.separator").charAt(0);
     } catch (Exception e) {
       return ';';
     }
   } // getPS()

/** java.home or null. */
   static String getJH(){
     try {
       return System.getProperty("java.home");
     } catch (Exception e) {
       return null;
     }
   } // getJH()

/** os.name or &quot;unknown&quot;. */
   static String getOS(){
     String tmp = null;
     try {
       tmp = System.getProperty("os.name");
     } catch (Exception e) {}
     if (tmp == null || tmp.isEmpty()) return "unknown";
     if (!TextHelper.startsWith(tmp, "windows", true)) {
       notWindows = true; // definitely no Windows
       if (TextHelper.startsWith(tmp, "linux", true)) {
         final File osRelease = new File("/etc", "os-release");
         try {
           Input inp = new Input(osRelease);
           String strB = inp.getAsString(Charset.forName("UTF-8"));
           inp.close();
           int start = TextHelper.indexOfOpt(strB, "NAME=", 0, false);
           if (start >= 0) {
             start += 5; // + "NAME="
             int end = strB.indexOf('\n', start + 1);
             if (end > start + 4) {
               String sub = TextHelper.trimUq(strB.substring(start, end), null);
               onPi = TextHelper.startsWith(sub, "raspbian", true);
               if (sub != null) return sub;
             }
           }
         } catch (Exception ex) {
           // System.out.println(ex); // for TEST only
           return "Linux (no /etc/os-release)";
         } // ignore all exceptions and return os.name
       } // Linux
     } // not Windows
     return tmp;
   } // getOS() 
   
   static String getHostname(){
     InetAddress ip;
     try {
       ip = InetAddress.getLocalHost();
       hostIPv4 = ip instanceof java.net.Inet4Address;
       hostIP = ip.getHostAddress();
     } catch (UnknownHostException e) {
       return "unknown";
     }
     return ip.getHostName();
   } // getHostname()

   private static String shortProgramName;
   static String getProgramName(){
     String ret = null;
     try {
       ret = System.getProperty("sun.java.command");
       int blnkF = ret.indexOf(' ');
       if (blnkF >= 1) ret = ret.substring(0, blnkF);
     } catch (Exception ex) {
       return shortProgramName = "frame4j-app";
     }
     
     final int doI = ret.lastIndexOf('.') + 1; // is 0 if no . found
     shortProgramName = doI < ret.length() ? ret.substring(doI): ret;
     return ret;
   } // getProgramName()
   

/** file.encoding or ISO-8859-1 on Windows. */
   static String getFileEncoding(){
     String tmp = null;
     try { // the property file.encoding is found on Linux and Windows
       tmp = TextHelper.trimUq(System.getProperty("file.encoding"), null);
     } catch (Exception e) {}
     if (tmp == null) try { // file.encoding usually == sun.jnu.encoding
       tmp = TextHelper.trimUq(System.getProperty("sun.jnu.encoding"), null);
     } catch (Exception e) {} 
     if (tmp == null) tmp = notWindows ? "UTF-8" : "ISO-8859-1";
     try {
       tmp =  Charset.forName(tmp).name();
     } catch (Exception ex) { tmp = "ISO-8859-1"; }
     return tmp;
   } // getFileEncoding()


/** Default encoding for console (stream) input and output. <br />
 *  <br />
 *  If a current system environment variable named &quot;activeCodePage&quot;
 *  could be read its value is taken.<br />
 *  Otherwise the system properties &quot;sun.stdout.encoding&quot; or
 *  &quot;sun.stderr.encoding&quot; are used if found. They exist on newer
 *  Windows JVMs.<br />
 *  If all that fails as OS starts with Windows &quot;Cp850&quot; else 
 *  FILE_ENCODING is returned.<br />
 *  If on the other hand a current system environment variable named
 *  &quot;activeCodePage&quot; could be read its value is taken.<br /> 
 */
   static String getConsolEncoding(){ 
     try {
       String tmp = System.getenv("activeCodePage");
       tmp = TextHelper.trimUq(tmp, null); // necessary ?
       if (tmp != null) {
         return Charset.forName(tmp).name();
       }
     } catch (Exception e) {}
     String[] encProps = {"sun.stdout.encoding", "sun.stderr.encoding"};
     for (String encProp : encProps) try {
       String tmp = System.getProperty(encProp);
       tmp = TextHelper.trimUq(tmp, null); // necessary ?
       if (tmp != null) {
         return Charset.forName(tmp).name();
       }
     } catch (Exception e) {}
     return notWindows ? FILE_ENCODING : "Cp850";
   } // getConsolEncoding()

/** Evaluate which class loader. <br />
 *  <br />
 *  If not to be evaluated, return null.
 */     
   static ClassLoader getClassLoader(){
     try {
       return ComVar.Impl.class.getClassLoader();
     } catch (Exception e) {} // ignore
     return null;
   } // getClassLoader()

 } // static class Impl (12.06.2003) ====================================
} // ComVar (08.11.2003, 21.01.2005, 21.08.2009, 05.08.2016, 10.03.2021)
