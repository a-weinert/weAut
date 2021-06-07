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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import de.frame4j.io.Input;
import de.frame4j.text.TextHelper;

/** <b>Properties, extended to support App based and other applications</b>. <br />
 *  <br />
 *  <b> &nbsp; <a name="inh" id="inh">Content</a></b><br />
 *  <br />
 *  <a href="#gnd"><img src="doc-files/blue.gif" style="border:0;" 
 *  width="21" height="10" style="border:0;  margin: 0 17px;"
 *  alt="go to ... &gt;"/></a>Fundamentals<br />
 *  <a href="#pint"><img src="doc-files/blue.gif" style="border:0;" 
 *  width="21" height="10" style="border:0;  margin: 0 17px;"
 *  alt="go to ... &gt;"/></a>Prop (erties) &#160; load and
 *    (inter) nationalise<br />
 *  <a href="#jst"><img src="doc-files/blue.gif" style="border:0;" 
 *  width="21" height="10" style="border:0;  margin: 0 17px;"
 *  alt="go to ... &gt;"/></a>The J V M &#160;and 
 *        &#160;start parameter<br />
 *  <a href="#par"><img src="doc-files/blue.gif" style="border:0;" 
 *  width="21" height="10" style="border:0;  margin: 0 17px;"
 *  alt="go to ... &gt;"/></a>de.frame4j.util.Prop &#160;and 
 *        &#160;start parameter<br />
 *  <a href="#cop"><img src="doc-files/blue.gif" style="border:0;" 
 *  width="21" height="10" style="border:0;  margin: 0 17px;" 
 *  alt="go to ... &gt;"/></a>Copyright,  version and so on<br />
 *  <br />
 *  <br />
 *  <b> &nbsp; <a name="gnd" id="gnd">Fundamentals</a></b><br />
 *  <br />
 *  Properties in JavaBeans' sense are part of the visible state of an object.
 *  In the classes' {@link java.util.Map}, {@link java.util.AbstractMap} and 
 *  {@link PropMap de.frame4j.util.PropMap} sense properties are set of 
 *  key-value-pairs.<br />
 *  <br />
 *  This class {@link Prop} unites both these aspects. {@code Prop} extends
 *  {@link PropMap} by an ample bundle of utility functions for applications 
 *  of all kinds. Among others these are:<ul>
 *  <li> comfortable evaluation of properties,</li>
 *  <li> effortless setting of properties,</li>
 *  <li> evaluating a String array for property settings using a 
 *       &quot;grammar&quot; (rules and default values) contained in the 
 *       properties (self),</li>
 *  <li> quite simple (inter) nationalising of properties,</li>
 *  <li> the setting of an objects variables / state by fittingly named 
 *       properties either by<ul>
 *       <li> calling the appropriate public setter methods (according to
 *         Beans conventions) <br />or by</li>
 *       <li> writing the variables.</li></ul> 
 *  </li></ul>
 *  Most of this class' constructors generate (by using the constructor
 *  {@link #Prop() Prop()}  a {@link Prop} object containing the (basic) 
 *  properties for the parameter evaluation mentioned above; see
 *  {@link #Prop() there}. This enables all utilising applications to evaluate
 *  start respectively initialisation parameters
 *  with little to null programming effort.<br />
 *  <br />
 *  <b> &nbsp; <a name="pint">Prop (erties) &#160; load and
 *    internationalise</a></b><br />
 *  <br />
 *  {@link java.util.Properties} and 
 *  {@link PropMap} entries can be loaded from text files (streams) obeying a
 *  quite primitive syntax rules. Some of the constructors and methods load 
 *  files and resources of that kind, using a simple name and some 
 *  suffixes<ul>
 *  <li>name.properties</li>
 *  <li>name_language.properties</li>
 *  <li>name_language_region.properties</li></ul>
 *  That loading is done / tried from<ol>
 *  <li>the actual directory (the applications starting directory),</li>
 *  <li>the JVM's jre\lib directory,</li>
 *  <li>the deployment archive (.jar file) should the application have been
 *      loaded from that (that would include all applications and tools
 *      deployed as installed extensions prior Java 9), or</li>
 *  <li>on base of an URL (if given)</li></ol>
 *  (tried in the named sequence).<br />
 *  <br /> 
 *  &quot;name&quot; here is in most cases the (pure) name of the application
 *  (see also {@link App} and 
 *  {@link #Prop(App, CharSequence)}).<br />
 *  &quot;language&quot; here means the lower case two letter language
 *  denotation (like de, fr, en or it, according to 
 *  <a href="https://www.loc.gov/standards/iso639-2/php/code_list.php"
 *  target="_top">http-&gt;.ISO 639-2</a>).<br />
 *  &quot;region&quot; here means the short two upper case country denotation
 *  (DE, GB, US).<br />
 *  Some constructors and methods search and or put these denotations in 
 *  the {@link Prop} object's properties named (keyed) &quot;language&quot;
 *  and &quot;region&quot;. Some methods, like  
 *  {@link #nationalise(CharSequence, CharSequence, CharSequence)} allow a 
 *  direct setting of language and / or region. This supports easy 
 *  nationalising of applications without re-starting (or worse 
 *  re-building).<br />
 *  <br />
 *  The class {@link AppLangMap} supports these tasks further; in co-operation
 *  with this class even a very basic {@link Prop} (the name.properties above)
 *  object has multilingual stereotypical expressions and output patterns; see
 *  also {@link #valueLang(CharSequence, String)}.<br />
 *  <br />
 *  <b> &nbsp; <a name="jst">The J V M &#160;and 
 *        &#160;start parameter</a></b><br />
 *  <br />
 *  Java forwards an appliation's start parameter (as a undue reverence to C) 
 *  as a String array to the started class' static main method (greetings by
 *  C again).<br />
 *  <br />
 *  As values for constructing this parameter array the JVM (java.exe or
 *  javaw.exe in the case of Windows) takes all start (command line) 
 *  parameters that follow the parameter denoting the start class or the 
 *  .jar file.<br />
 *  <br />
 *  This class {@link Prop} widely simplifies (especially when extending 
 *  {@link App de.frame4j.util.App}) the handling and evaluation of those 
 *  start parameters (<a href="#par">s.u.</a>). See also some helpers in the 
 *  class {@link TextHelper de.frame4j.text.TextHelper} for preparing start
 *  parameters for sub processes, applications or plug-ins.<br />
 *  <br />
 *  Hint: When preparing start parameters all known versions of SUN's JVM /
 *  JRE for Windows show a feature that closer consideration debunks as 
 *  dangerous bug:<br />
 *  If one of the original parameters contains at least one wildcard character
 *  (One or more * asterisk or ? question mark), this original will be 
 *  replaced by as many matching file names as will be found in the actual
 *  directory, forwarding more parameters to the application (main(String[])) 
 *  as are seen on the command line e.g.<br />
 *  If there are no such files (or directories) the parameter containing 
 *  question mark (?) or asterisk (*) is forwarded unchanged.<br />
 *  This behaviour is (of course) totally unwanted if changing the number and
 *  hence sequence of parameters spoils their parsing or if the parameter's
 *  semantic has nothing to to with files. But even if files or directories
 *  are meant the application might want to keep the control over the 
 *  evaluation of wild card expressions.<br />
 *  The unwanted and often dangerous behaviour can only be stopped by 
 *  surrounding the parameter by quotes. This and other Frame4J's classes 
 *  allow substituting a plus + (as wildcard) for the * as a often suitable
 *  alternative.<br />
 *  <br />
 *  <b> &nbsp; <a name="par">de.frame4j.util.Prop &#160;and 
 *        &#160;start parameter</a></b><br />
 *  <br />
 *  An application should use this class {@link Prop} to evaluate its 
 *  (String[]) start parameters; this is supported among others by the methods
 *  {@link #parse(String[], CharSequence) parse()} 
 *  and {@link #setFields setFields()}. An application extending 
 *  {@link App de.frame4j.util.App} &mdash; a good choice instead of starting
 *  on empty ground &mdash; gets all this by just using one of the 
 *  inherited methods  
 *  {@link App#go(String[], String, CharSequence) go(String[]...)} 
 *  for starting.<br />
 *  <br />
 *  Evaluating start parameters by Prop provides many advantages in
 *  flexibility and in self documentation, as the evaluating rules (the 
 *  &quot;grammar&quot;) are defined as properties that are human
 *  readable and writable put in the basic .property file or resource. See 
 *  among others the examples <a href="../doc-files/Era.properties" 
 *  target="_top">Era.properties</a> and <a
 *  href="../doc-files/SVNkeys.properties" 
 *  target="_top">SVNkeys.properties</a> of the tools 
 *  Era 
 *  (<a href="../doc-files/Era.java" target="_top">source</a>) 
 *  and  {@link de.frame4j.SVNkeys SVNkeys}.<br />
 *  <br />
 *  Hint: Direct Applet support was removed August 2016.<br />
 *  <br />
 *  <br />
 *  <b> &nbsp; <a name="cop" id="cop">Copyright,  version and so on</a></b><br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 1999 - 2004, 2006, 2016  &nbsp; Albrecht Weinert.<br />
 *  <br />
 *  <br />
 *  To the <a href="#inh">table of content</a>. 
 *  @see PropMap
 *  @see AppLangMap
 *  @see AppHelper
 *  @see App
 *  @see #parse(String[], CharSequence, boolean)
 *  @see #parse(String[], CharSequence)
 *  @see #setFields(Object)
 *  @see PropMapHelper#setField(Object, String, String)
 *  @author   Albrecht Weinert
 *  @version  $Revision: 50 $ ($Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $)
 */
 // so far    V00.00 (05.04.1999) : new
 //           V00.02 (11.07.2000) : Inter nationalising, extensions
 //           V00.06 (10.11.2000) : fit for Applets
 //           V00.08 (28.06.2001) : Load from the application's .jar
 //           V01.01 (04.10.2001) : getBoolean(String, boolean);
 //           V01.10 (09.12.2001) : parse(...,  partial), logDat
 //           V01.12 (18.04.2002) : option+xyz, load@Prop (f. partial)
 //           V01.20 (17.05.2002) : de, AppBase, Beans-setPr corrected 
 //           V01.21 (05.06.2002) : get(String, long) 
 //           V01.30 (26.06.2002) : verbose, help from the mother app
 //           V01.31 (27.06.2002) : parsePartial() - automatic for App
 //           V01.35 (12.09.2002) : partial && loadProp arg = null 
 //           V02.03 (24.04.2003) : load from resource first
 //           V02.08 (12.10.2003) : wordstopp-
 //           V02.19 (04.05.2004) : setFields() improved
 //           V02.26 (03.08.2004) : indexed Properties
 //           V02.32 (27.10.2004) : PropMap inheritor
 //           V02.35 (16.12.2004) : AppLangMap like extensions
 //           V02.47 (03.06.2005) : setXyz(CharSequence) before int,String
 //           V02.57 (09.10.2006) : extension for Servlet (1)
 //           V02.64 (07.04.2008) : indexed Prop. [0] may omit the 0
 //           V.o59+ (29.01.2009) : ported Frame4J, svn
 //           V.o83+ (20.02.2009) : options -lang[uage] -regio[n]
 //           V.133+ (21.08.2009) : .properties in .jar UTF-8
 //           V.159+ (21.11.2009) : codePages added; nationalise w/o rep.
 //           V.184+ (23.01.2010) : getAsResourceStream() corrected
 //           V.118+ (23.02.2015) : Servlet support removed 
 //           V.153+ (09.05.2016) : Web Start compatibility 
 //           V.176  (16.08.2016) : Applet support killed (Hi Java 9)
 //           V.  44 (05.05.2021) : wordstopp- removed
@MinDoc(
   copyright = "Copyright 1999 - 2009, 2016, 2021  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 50 $",
   lastModified   = "$Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $",
   usage   = "3 phase: make, populate, use (preferably not further modified)",  
   purpose = 
      "extended capabilities of PropMap for applications, App, ..."
) public class Prop extends PropMap {

/** Version number for serialising.  <br />
 *  <br />
 *  Changed 21.11.2009, V.159+, due to added nationalisation history.
 */
   static final long serialVersionUID = 260153008402021L;
//                                      magic /Id./maMi
   static final boolean RESTEST = false; // enable development logs on S.out

/** A base URL. <br />
 *  <br />
 *  The base URL is set by some constructors.<br />
 *  <br />
 *  If not null, it is used as base to search for .properties files by some
 *  of the {@link #load(CharSequence, CharSequence) load()} methods.<br />
 *  <br />
 *  default: null <br />
 */
   protected URL baseURL;

/** A base class. <br />
 *  <br />
 *  The base class is set by some constructors.<br />
 *  <br />
 *  If not null, it is used as base to search for .properties files by some
 *  of the {@link #load(CharSequence, CharSequence) load()} methods
 *  extending the search into the .jar
 *  file from which the application class may have been loaded.<br />
 *  <br />
 *  default: null <br />
 */
   protected Class<?> baseClass;

//---------------------------------------------------------------------

/** Constructor with basic presettings for applications. <br />
 *  <br />
 *  A Prop object with some basic properties for applications will be made.
 *  The new Prop already has those properties:
 *  <code><ul><li type="dot">verbosity=normal  &nbsp; *)</li>
 *  <li>help = false &nbsp; *)</li>
 *  <li type="circle">option-v= verbosity = verbose </li>
 *  <li type="circle">option-verbose= verbosity = verbose</li>
 *  <li type="circle">option-silent= verbosity = silent</li>
 *  <li type="circle">option-normal= verbosity = normal</li>
 *  <li type="circle">option-debug= verbosity = debug</li>
 *  <li type="circle">option-test= verbosity = test</li>
 *  <li type="circle">option-a= outMode=append</li>
 *  <li type="circle">option-o= outMode=overwrite</li>
 *  <li type="circle">option-c= outMode=create</li>
 *  <li type="circle">option-askGraf= askGraf=true</li>
 *  <li type="circle">option-askCons= askGraf=false</li>
 *  <li type="circle">option-log=  logDat</li>
 *  <li type="circle">option-?= &#160; &#160; &#160; help=true</li>
 *  <li type="circle">option-help= &#160; &#160;help=true <br />
 *     </li>
 *  <li type="dot">source=</li>
 *  <li>outMode=ask</li>
 *  <li>askGraf=!{@link ComVar}.{@link ComVar#NOT_WINDOWS NOT_WINDOWS}
 *           **)</li>
 *  <li>codePages=</li>
 *  <li>dest=</li>
 *  <li>logDat=
 *        <br /></li>
 *  <li>outDat=
 *        <br /></li>
 *  <li>language= &lt;wie user.language or {@link ComVar#UL}&gt;</li>
 *  <li>region= &#160; &lt;wie user.region bzw. {@link ComVar#UR}&gt;</li>
 *  <li type="circle">option-de= language=de ## region=DE</li>
 *  <li type="circle">option-deDE= language=de ## region=DE</li>
 *  <li type="circle">option-deCH= language=de ## region=CH</li>
 *  <li type="circle">option-en= language=en ## region=GB</li>
 *  <li type="circle">option-enGB= language=en ## region=GB</li>
 *  <li type="circle">option-fr= language=fr ## region=FR</li>
 *  <li type="circle">option-frFR= language=fr ## region=FR</li>
 *  <li type="circle">option-frCH= language=fr ## region=CH</li>
 *  <li type="circle">option-frBE= language=fr ## region=BE</li>
 *  <li type="circle">option-it= language=it ## region=IT</li>
 *  <li type="circle">option-itIT= language=it ## region=IT</li>
 *  <li type="circle">option-itCH= language=it ## region=CH</li>
 *  <li type="circle">option-es= language=es ## region=ES</li>
 *  <li type="circle">option-da= language=da ## region=DK</li>
 *  <li type="circle">option-nl= language=nl ## region=NL</li>
 *  <li type="circle">option-pt= language=pt ## region=PT <br />
 *  <li type="circle">option-lang= language <br />
 *  <li type="circle">option-regio= region  <br />
 *     </li>
 *
 *  <li>thoseDirs=</li>
 *  <li>omitDirs=</li>
 *  <li>thoseFiles=</li>
 *  <li>omitFiles=</li>
 *  <li>days=nosetting</li>
 *  <li>daysOld=nosetting</li>
 *  <li>since=</li>
 *  <li>til= <br />
 *        </li>
 *  <li type="dot">ignoreCase= !{@link ComVar#NOT_WINDOWS}</li>
 *  <li type="circle">option-cs=  ignoreCase=false</li>
 *  <li type="circle">option-case=  ignoreCase=false</li>
 *  <li type="circle">option-ncs=  ignoreCase=true</li>
 *  <li type="circle">option-nocase=  ignoreCase=true</li>
 *  </ul></code>
 *  These are the basic properties including the basic syntax for evaluating
 *  start parameters as well as as base for often used file and directory 
 *  criteria.<br />
 *  <br />
 *  Remark *): If constructed with an {@link App} (inheritor) object
 *  (see e.g. {@link #Prop(App, CharSequence)}), that in not the base 
 *  application (that is usually the one started with the JVM) but is an
 *  &quot;App plug in&quot;) of another suitable &quot;mother
 *  application&quot;, then some properties are copied from there:
 *  help and verbosity.<br />
 *  <br />
 *  Hint: A new totally empty Prop object can be made by  
 *  {@link #Prop(Map) new Prop((Map)null)}. In that case, ask the question,
 *  if not a {@link PropMap} is the thing meant.<br />
 *  <br />
 *  Internal implementation hint: The description of the common basic 
 *  properties set here with respect to start parameters is provided as
 *  property &quot;hlpfwopt&quot; in at least German and English in
 *  <a href="doc-files/AppLangMap.properties" 
 *  target="_top">AppLangMap.properties</a>. Changes here must at once be
 *  mirrored there, otherwise the help output of most tools and applications
 *  (inserting this by %hlpfwopt% its help texts) would get misleading.<br />
 *  <br />
 *  @see #load(CharSequence, CharSequence) load()
 *  @see #Prop(Class, String, boolean, String)
 *  @see #Prop(URL, String, CharSequence) Prop(URL, String, CharSequence)
 */
   public Prop() {  
      super(271);
      putNewImpl("help",     "false"); 
      putNewImpl("verbosity",  "normal");
      putNewImpl("source",   ComVar.EMPTY_STRING);
      putNewImpl("dest",     ComVar.EMPTY_STRING);
      putNewImpl("logDat",   ComVar.EMPTY_STRING);
      putNewImpl("outDat",   ComVar.EMPTY_STRING);
      putNewImpl("outMode",  "ask");
      putNewImpl("askGraf",  ComVar.NOT_WINDOWS
                                   ? "false" : "true"); // since 21.04.2005
      // putNewImpl("xmlMBdescr", "<default>"); // out since 04.05.2021
      putNewImpl("codePages", ComVar.EMPTY_STRING);  // since 21.11.2009 TEST

      
      putNewImpl("language", ComVar.UL);
      putNewImpl("region",   ComVar.UR);
      setAppServBasePrp();   ///    immutable  05.10.2006
      putNewImpl("option-log",     "logDat");
      putNewImpl("option-a",    "outMode=append");
      putNewImpl("option-o",    "outMode=overwrite");
      putNewImpl("option-c",    "outMode=create");
      putNewImpl("option-askgraf", "askGraf=true");
      putNewImpl("option-askcons", "askGraf=false");

      putNewImpl("option-?",       "help=true");
      putNewImpl("thoseDirs",  ComVar.EMPTY_STRING);
      putNewImpl("omitDirs",   ComVar.EMPTY_STRING);
      putNewImpl("thoseFiles", ComVar.EMPTY_STRING);
      putNewImpl("omitFiles",  ComVar.EMPTY_STRING);
      putNewImpl("ignoreCase", ComVar.NOT_WINDOWS  ? "false" : "true");

      putNewImpl("days",   "nosetting");
      putNewImpl("daysOld","nosetting");
      putNewImpl("since",  ComVar.EMPTY_STRING);
      putNewImpl("til",    ComVar.EMPTY_STRING);

      putNewImpl("option-cs",      "ignoreCase=false");
      putNewImpl("option-case",    "ignoreCase=false");
      putNewImpl("option-ncs",     "ignoreCase=true");
      putNewImpl("option-nocase",  "ignoreCase=true");
      
      putNewImpl("option-lang",  "language"); // since 20.02.2009
      putNewImpl("option-regio", "region");  // ditto
   }  // Prop()

   
/** Constructor with basic pre-settings for web services. <br />
 *  <br />
 *  A Prop object with some basic properties for Applets and Servlets will
 *  be made. These are a small subset of those made for applications
 *  respectively programs (by other constructors or factory methods). If
 *  neither Servlet (S) nor Applet (A) is specified (only) this constructor
 *  returns an empty Prop object (capacity 271).<br />
 *  <br />
 *  The new Prop for a Servlet or an Applet has those
 *  properties:
 *  <code><ul><li type="dot">verbosity=normal  &nbsp;</li>
 *  <li>help = false &nbsp;</li>
 *  <li>language= &lt;like user.language respectively {@link ComVar#UL}&gt;</li>
 *  <li>region= &#160; &lt;like user.region respectively {@link ComVar#UR}&gt;</li>
 *  <li type="dot">ignoreCase set true for Windows (but not if Servlet)</li>
 *  </ul></code>
 *  If a Servlet, you get additionally<code><ul>
 *  <li type="circle">option-v= verbosity = verbose </li>
 *  <li type="circle">option-verbose= verbosity = verbose</li>
 *  <li type="circle">option-silent= verbosity = silent</li>
 *  <li type="circle">option-normal= verbosity = normal</li>
 *  <li type="circle">option-debug= verbosity = debug</li>
 *  <li type="circle">option-test= verbosity = test<br /> 
 *  </li>
 *  <li type="circle">option-de= language=de ## region=DE</li>
 *  <li type="circle">option-deDE= language=de ## region=DE</li>
 *  <li type="circle">option-deCH= language=de ## region=CH</li>
 *  <li type="circle">option-en= language=en ## region=GB</li>
 *  <li type="circle">option-enGB= language=en ## region=GB</li>
 *  <li type="circle">option-fr= language=fr ## region=FR</li>
 *  <li type="circle">option-frFR= language=fr ## region=FR</li>
 *  <li type="circle">option-frCH= language=fr ## region=CH</li>
 *  <li type="circle">option-frBE= language=fr ## region=BE</li>
 *  <li type="circle">option-it= language=it ## region=IT</li>
 *  <li type="circle">option-itIT= language=it ## region=IT</li>
 *  <li type="circle">option-itCH= language=it ## region=CH</li>
 *  <li type="circle">option-es= language=es ## region=ES</li>
 *  <li type="circle">option-da= language=da ## region=DK</li>
 *  <li type="circle">option-nl= language=nl ## region=NL</li>
 *  <li type="circle">option-pt= language=pt ## region=PT</li>
 *  </ul></code>
 *  This features the base properties usual for web services without the file
 *  and directory criteria for (most) applications (by {@link #Prop()}).<br />
 *  <br />
 *  @param se S, s: Servlet; A, a: Applet; else: empty.
 *  @see #load(CharSequence, CharSequence) load()
 *  @see #Prop()
 *  @see #Prop(URL, String, CharSequence)
 */
   public Prop(char se){  
      super(271);
      if (se <= 'Z') se += 32;
      boolean ser = se == 's';
      if (!ser || se != 'a') return; // neither s nor a

      putNewImpl("help",       "false"); 
      putNewImpl("verbosity",  "normal");
      // putNewImpl("xmlMBdescr", ComVar.EMPTY_STRING); // out since 04.05.2021
      putNewImpl("language", ComVar.UL);
      putNewImpl("region",   ComVar.UR);
      putNewImpl("ignoreCase", ComVar.NOT_WINDOWS && ser ? "false" : "true");
      if (ser) setAppServBasePrp();  // Servlet
   }  // Prop(char)


/** The basic properties for applications and Servlets. <br />
 *  <br />
 *  @throws UnsupportedOperationException if changing immutable entries
 */
   private void setAppServBasePrp() throws UnsupportedOperationException {
      putNewImut("option-de",   "language=de ## region=DE");
      putNewImut("option-dede", "language=de ## region=DE");
      putNewImut("option-dech", "language=de ## region=CH");
      putNewImut("option-en",   "language=en ## region=GB");
      putNewImut("option-engb", "language=en ## region=GB");
      putNewImut("option-fr",   "language=fr ## region=FR");
      putNewImut("option-frfr", "language=fr ## region=FR");
      putNewImut("option-frch", "language=fr ## region=CH");
      putNewImut("option-frbe", "language=fr ## region=BE");
      putNewImut("option-it",   "language=it ## region=IT");
      putNewImut("option-itit", "language=it ## region=IT");
      putNewImut("option-itch", "language=it ## region=CH");
      putNewImut("option-es",   "language=es ## region=ES");
      putNewImut("option-da",   "language=da ## region=DK");
      putNewImut("option-nl",   "language=nl ## region=NL");
      putNewImut("option-pt",   "language=pt ## region=PT");

      putNewImpl("option-v",       "verbosity = verbose");
      putNewImpl("option-verbose", "verbosity = verbose");
      putNewImpl("option-silent",  "verbosity = silent");
      putNewImpl("option-normal",  "verbosity = normal");
      putNewImpl("option-debug",   "verbosity = debug");
      putNewImpl("option-test",    "verbosity = test");
      putNewImpl("option-help",    "help=true");
   } // setAppServBasePrp()


/** Constructor with presettings from a file (by name). <br />
 *  <br />
 *  A Prop object without any basic properties as set by the other 
 *  {@link #Prop() constructor}s is made. Using 
 *  {@link #load(CharSequence, CharSequence) load(fileName, encoding)} a 
 *  preset by the content of text file (in Java/SUN properties syntax) is
 *  tried (if {@code fileName} is not empty).<br />
 *  <br />
 *  If this fails, a FileNotFoundException is thrown. If more than one file
 *  shall be tried it's better to call this constructor with null and 
 *  handle the (trial) loads one by one.<br />
 *  Calling this constructor with null, returns a (totally) empty 
 *  {@link #Prop()}.<br />
 *  <br />
 *  @param fileName the name of properties text file
 *  @exception FileNotFoundException if file fileName is nor readable.
 *  @see #load(CharSequence, CharSequence)
 *  @see #Prop(Class, String, boolean, String)
 *  @see #Prop(URL, String, CharSequence)
 *  @see #Prop()
 */
   public Prop(CharSequence fileName, 
                         CharSequence encoding) throws FileNotFoundException {
      super();
      if ((fileName == null) || (fileName.length() == 0)) return;
      if (!load(fileName, encoding)) 
         throw new FileNotFoundException(valueLang("noprfil") 
                                  + fileName); // " No properties file "
   } // Prop(String)


/** Constructor with presettings from a file with a base URL. <br />
 *  <br />
 *  A Prop object preset like by {@link #Prop()} is made and (by
 *  {@link #load(CharSequence, CharSequence)}) additionally set by a 
 *  file specified by
 *  fileName + Basis-URL.<br />
 *  <br />
 *  If this fails, a FileNotFoundException is thrown.<br />
 *  If {@code fileName} is null the effect is like  {@link #Prop() Prop()} 
 *  plus (if given) the setting of a {@link #baseURL}.<br />
 *  <br />
 *  @param fileName the name of properties text file
 *  @exception FileNotFoundException if no such file readable
 *  @see #load(CharSequence, CharSequence)
 *  @see #Prop(Class, String, boolean, String)
 *  @see #Prop(CharSequence, CharSequence)
 *  @see #Prop()
 */
   public Prop(URL baseURL, String fileName, CharSequence encoding) 
                     throws FileNotFoundException {
      this();
      this.baseURL = baseURL;
      if ((fileName == null) || (fileName.length() == 0)) return;
      if (!load(fileName, encoding)) throw new 
         FileNotFoundException(valueLang("noprfil") + fileName);
   } // Prop(URL, String)


/** Constructor with presettings by a Map. <br />
 *  <br />
 *  A Prop object will be constructed that will contain all entries of the 
 *  {@link Map} {@code preset} that fulfil {@link PropMap}'s conditions on
 *  key-value-pairs.<br />
 *  <br />
 *  If {@code preset} is null (or empty), the newly made Properties object 
 *  is empty. It will also not contain any standard or default entries made
 *  by other constructors like {@link #Prop()}.<br />
 *  <br />
 *  @see #Prop(Class, String, boolean, String)
 *  @see #Prop(CharSequence, CharSequence)
 *  @see #Prop()
 */
   public Prop(Map<CharSequence, String> preset){ 
      super(preset == null ? 271 : 2 * preset.size());
      putAll(preset);
   } //  Prop(Map) 

//--------------------------------------------------------------------------

   final int MAX_HIER = 3;
   String[] basePropHierarch = new String[ MAX_HIER];
   Class<?>[]  baseClassHierarch = new Class[ MAX_HIER];
   int basePropFill;

   boolean allowNoPropFile;  // since 06.04.2020
   
/** Constructor with presettings related to a class. <br />
 *  <br />
 *  A Prop object will be made with all standard / default entries as made
 *  by the parameterless constructor {@link #Prop() Prop()}. Then, using
 *  {@link #load(CharSequence, CharSequence)} all properties from the file
 *  className+".properties" are added. Used therefore is the pure (short)
 *  class name of the class {@code cl}, that means without packages and 
 *  else.<br />
 *  <br />
 *  If the reading of the file in the actual directory meets with no success
 *  the directory java.home\lib is tried. If that fails reading a resource 
 *  from the applications (deployment) .jar file is tried. If all that fails
 *  a FileNotFoundException is thrown.<br />
 *  <br />
 *  After loading the file / resource nationalising the {@link Prop} object
 *  made will be tried by<br /> 
 {@link #nationalise nationalise}<code>(className, null, null)</code>.<br />
 *  <br />
 *  A speciality for direct and and indirect {@link de.frame4j}
 *  inheritors:<br />
 *  If {@code cl} is an inheritor of a tool of a {@link de.frame4j}
 *  application its .properties file (from the .jar file) will be loaded 
 *  first. For that the &quot;total&quot; name de/frame4j/parentApp will be 
 *  used. The effect is that fitting properties of the base application don't
 *  have to be repeated in the inheriting applications .properties file.<br />  
 *  <br />
 *  @param allowNoPropFile if true the absence of an own .properties file will
 *              be allowed; otherwise an exception would be thrown
 *  @param extraProp if not null or empty, the name of extra properties
 *              to be loaded before the class related ones   
 *  @exception FileNotFoundException if the base .properties file is not 
 *              readable and allowNoPropFile is false
 *  @see #load(CharSequence, CharSequence) load()
 *  @see #Prop(CharSequence, CharSequence)
 */
   public Prop(Class<?> cl, String shortClassName, 
                 final boolean allowNoPropFile,
                 final String extraProp) throws FileNotFoundException {
      this();
      this.allowNoPropFile = allowNoPropFile; // since 06.04.2020 (remember)
      baseClass = cl;
      if (cl == null) return;
      String n = cl.getName();
      String className = n; 
      if (extraProp != null && extraProp.length() > 2) {
        boolean loadExtra = load1(cl, extraProp, null);
        if (RESTEST) System.out.println(" ///  TEST Prop loadExtra "
                + extraProp + (loadExtra ? " OK" : " failed.")); // TEST out
      } // extraProp load
      parentSearch: while (true) { // basePropFill < MAX_HIER
         baseClassHierarch[basePropFill] = cl;
         
         int lpp = n.lastIndexOf ('.'); // last point position
         boolean innerPoint = lpp > 0 && lpp < n.length() - 1;
         String pacName = innerPoint ? n.substring(0, lpp) : null;
         String classN = innerPoint ? n.substring(lpp + 1) : n;
         int fdp = classN.indexOf('$');
         if (fdp > 0)  classN =  classN.substring(0, fdp);
         if(basePropFill == 0) className =  classN; // remember pure name par.                   
         
         n = basePropHierarch[basePropFill] = innerPoint 
                                ? pacName.replace('.', '/') + '/' + classN 
                                : classN;
         if (RESTEST) {
            System.out.println(" ///  TEST Prop[" + basePropFill + "]: "
                  + cl + " = " + n);
         }
         
         if (++basePropFill == MAX_HIER) break parentSearch;
         
         cl = cl.getSuperclass();
         if (cl == App.class)     break parentSearch;
         if (cl == Object.class)  break parentSearch;
         n = cl.getName();
      } // parentSearch: while 
      
      boolean hadALoad = false;
      
      loadLoop: for (int i = basePropFill - 1; i >= 0 ; --i) {
         n = basePropHierarch[i]; 
         cl = baseClassHierarch[i];
         boolean aktLoad = false;
         loadDeed: while (n != null) {
            boolean propJar = load1(cl, n + ".properties", null); //null: JAR
            if (!propJar) aktLoad = load1(n + ".properties", null);
            if (shortClassName != null && !n.equals(shortClassName)) {
               final String fileName = shortClassName + ".properties";
               if (ComVar.UD != null   // actual directory
                      && load1(ComVar.UD + fileName, ComVar.FILE_ENCODING)) {
                  aktLoad = true;
                  if (Prop.RESTEST) {
                     System.out.println("\n ///  TEST Prop load[" + i 
                                             + "]: shortClass in act dir");
                  }
               }
               if (ComVar.JRL != null  // in JRE-Lib
                       && load1(ComVar.JRL + fileName, ComVar.FILE_ENCODING)) {
                  aktLoad = true;
                  if (Prop.RESTEST) {
                      System.out.println("\n ///  TEST Prop load[" + i
                                         + "]: shortClass in jre\\lib\\ext");
                  }
               }
               shortClassName = null; // one trial is enough
            }
               ///propJar = 
               ///load1(cl, n + ".properties", null); //null: JAR encoding
            ///if (!propJar) {
               ///aktLoad = load1(n + ".properties", null);
           ///  }
            if (aktLoad || propJar) {
               if (Prop.RESTEST) {
                 System.out.println("\n ///  TEST Prop load[" + i + "]: " + n
               + " < " + ( propJar ? ".jar " : "") + (aktLoad ? "file" : ""));
               }
               hadALoad = true;
               nationalise(n, null, null);
               continue loadLoop; 
            } 
            if (Prop.RESTEST) {
               System.out.println("\n ///  TEST Prop load[" + i + "]: " 
                                         + n + " fail");
            }
            if (i == 0 &&  n != className && !hadALoad) { // try pure
               n = className;
               i = -1;
               continue loadDeed;
            } // pure name of parameter class (if different) as last resort
            continue loadLoop;
         } // loadDeed
      } // load and nationalise loop 1
      
      if (!hadALoad && !allowNoPropFile) throw new FileNotFoundException(
                                         valueLang("nobaprfil")  + className);
    } // Prop(Class, boolean)

/** Constructor with presettings by an application (App object). <br />
 *  <br />
 *  This is the most powerful and most comfortable constructor for Java 
 *  applications extending {@link App de.frame4j.util.App} and fulfilling 
 *  its contract. This constructor does all the evaluating of start arguments,
 *  the nationalising according to all available .properties files for that
 *  application.<br />
 *  <br />
 *  The {@link Prop} object is made by 
 *  {@link #Prop(Class, String, boolean, String)}.
 *  If the {@link App} object {@code app} has a mother application the 
 *  properties help and verbosity take its values as defaults (instead of
 *  false and {@link Verbos#NORMAL NORMAL}.<br />
 *  <br />
 *  After that a file named &quot;ClassName.properties&quot; will be searched
 *  and loaded to this Prop object. Then other .properties files with 
 *  names generated according to the (system) properties  user.language and 
 *  user.region are used to nationalise it.<br />
 *  <br />
 *  The .properties files will be searched for in the actual directory, in
 *  java.home\lib and as resource in the application's deployment .jar (in
 *  that sequence). If all fails for base .properties file a
 *  FileNotFoundException will be thrown.<br />
 *  <br />
 *  Afterwards the application's start arguments will be evaluated (using
 *  {@link #parse(String[], CharSequence) parse()}).<br />
 *  <br />
 *  If after that one of the properties language or region have a value 
 *  different from the system properties  user.language respectively
 *  user.region, this Prop object will be nationalised (again) using those
 *  new values.<br />
 *  <br />
 *  And at last the variables (fields) of the {@link App} object {@code app} 
 *  will be set by {@link #setFields setFields()}.<br />
 *  <br />
 *  @exception FileNotFoundException if no base .properties file for
 *             {@code app} can be found and read, except the application 
 *             explicitly allows that omission; see
 *             {@link App#allowNoPropertiesFile()}
 *  @exception NullPointerException if app is null
 *  @exception IllegalArgumentException on parsing exception and if
 *             a called set(..) method raises an exception like 
 *             {@link IllegalArgumentException} or an 
 *             {@link java.beans.PropertyVetoException}. The message of that 
 *             exception will be transfered to the new 
 *             {@link IllegalArgumentException}
 *  @see #parse(String[], CharSequence) parse()
 *  @see #setFields setFields()
 *  @see #getText getText()
 *  @see #Prop(CharSequence, CharSequence)
 *  @see #Prop(Class, String, boolean, String)
 */
   public Prop(App app, CharSequence commBeg) throws FileNotFoundException {
      this(app.myClass, app.shortClassName, app.allowNoPropertiesFile(),
                                                app.extraPropertiesFile());
      Object ba = app.appBase.baseApp;
      if (app != ba && ba instanceof App) {
         put("verbosity", ((App)ba).getVerbose());
         if (((App)ba).help)    put("help", "true"); // why the hell
      }
      parse(app.args, commBeg, app.parsePartial()); /// parameter evaluation
      
      final String region   = getString("region",  ComVar.UR); 
      final String language = getString("language",ComVar.UL);
      if ( !TextHelper.areEqual(ComVar.UR, region, true)  
           || !TextHelper.areEqual(ComVar.UL, language, true) ) { // new lang
         setRegion(region);
         setLanguage(language);
         
         loadLoop: for (int i = basePropFill - 1; i >= 0 ; --i) {
            String n = basePropHierarch[i]; 
            if (RESTEST) {
              System.out.println("\n ///  TEST Prop parse lang reg ch (1) nat"
                     + "\n ///  : " + n + " lr: " + language + region);
            }
            nationalise(n, null, null); // 07.10.2004 .apps.-base
         }         

         String n = app.packName + app.shortClassName;
         n = n.replace('.', '/');
  ///       System.out.println("\n ///  TEST Prop parse lang reg ch (2) nat"
     ///                    + "\n ///  : " + n + " lr: " + language + region);
         nationalise(n, null, null); // 17.02.2009
        
         
         AppLangMap.setULang(language); // put there
      } // new lang
         
         /// String className = app.shortClassName;
         //// still needed ?????? 
   ///   System.out.println("\n ///  TEST Prop parse lang reg ch (3) nat"
      ///                        + "\n ///  : " + app.shortClassName);

      nationalise(app.shortClassName, null, null); // 15:22 08.11.00
 
      setFields(app);
      /// if (app instanceof WinApp) putImpl("askGraf", "true");
      if (app == app.appBase.baseApp) {
         app.appBase.askGraf = getBoolean("askGraf", app.appBase.askGraf);
      }
      /// app.appBase.set(app, this);
      app.appIO.set(this);
   } // Prop(App, CharSequence, int)

//--------------------------------------------------------------------------


/** Loading of properties from a text file; URL related. <br />
 *  <br />
 *  The file denoted by {@code fileName} related to the provided {@code url}
 *  or this {@link Prop}'s {@link #baseURL} will be loaded into this 
 *  {@link Prop} object. For the text file's syntax see
 *  {@link java.util.Properties#load(InputStream) Properties.load()}.<br />
 *  <br />
 *  If that succeeds true is returned. In this case this {@link Prop} object's
 *  properties are usually modified or added to.<br />
 *  If this fails or the file handling leads to exceptions false is 
 *  returned.<br />
 *  <br />
 *  @param url the (base) URL. If null, the base URL set by some constructors
 *                  is used instead. If that is null too, false is returned.
 *  @param fileName the URL related file name. \ will be replaced by / . 
 *  @return true on successful load
 *  @see #load1(Class, CharSequence, CharSequence) 
 *  @see PropMap#load1(CharSequence, CharSequence)
 *  @see #load(CharSequence, CharSequence)
 */ 
   public boolean load1(URL url, final CharSequence fileName,
                                                    CharSequence encoding){
      if (url == null) url = baseURL;
      if (url == null) return false;
      String fN = TextHelper.makeFNameUJ(fileName);
      try {
         if (fN != null) url = new URL(url, fN);
         InputStream in = url.openStream();
         PropMapHelper.load(in, encoding, this);         ///  load(in);
         if (fileAnz < FILE_LIST_LEN) {
             fileList[fileAnz] = fN != null 
                                 ? fN : url.getPath();
             ++fileAnz;
         }
         return true;
      } catch (Exception e) {
         if (e instanceof SecurityException)
            throw (SecurityException)e; // 12.12.00
      }
      return false;     
   } // load1(URL, 2*CharSequence)



/** Loading of properties from a text file; class related. <br />
 *  <br />
 *  The file denoted by {@code fileName} related to the class {@code cl}
 *  or to a {@link #baseClass} set by some constructors will be loaded into
 *  this {@link Prop} object. For the text file's syntax see
 *  {@link java.util.Properties#load(InputStream) Properties.load()}.<br />
 *  <br />
 *  If that succeeds, true is returned. In this case this {@link Prop}
 *  object's properties are usually modified or added to.<br />
 *  If this fails or the file handling leads to exceptions false is 
 *  returned.<br />
 *  <br />
 *  This method just delegates to {@link PropMap}'s method 
 *  {@link PropMap#load1(Class, CharSequence, CharSequence)}. The differences
 *  are the mentioned substituting of {@code cl} == null by 
 *  {@link #baseClass} and {@code encoding} == null by 
 *  {@link ComVar}.{@link ComVar#JAR_ENCODING JAR_ENCODING}.<br />
 *  <br />
 *  @param cl the (base) class. If null, the base class set by some 
 *                 constructors is used instead. If that is null too, false
 *                 is returned.
 *  @param fileName the class related file name. \ will be replaced by / .
 *  @param encoding the resource / file encoding. null or empty will get
 *                  {@link ComVar#JAR_ENCODING JAR_ENCODING}
 *  @return true on successful load
 *  @throws IllegalArgumentException if put(String, String) will be 
 *           so rejected
 *  @throws UnsupportedOperationException if put(String, String)
 *           will be so rejected
 *  @throws ClassCastException if a key violates {@link PropMap}' contract
 *  @throws   ClassCastException a key violates {@link PropMap}' contract
 *  @see #load1(URL, CharSequence, CharSequence)
 *  @see PropMap#load1(CharSequence, CharSequence)
 *  @see #load(CharSequence, CharSequence)
 */ 
   @Override public boolean load1(Class<?> cl,  final CharSequence fileName,
                           CharSequence encoding)  throws ClassCastException, 
              UnsupportedOperationException,  UnsupportedOperationException {
      if (cl == null) cl = baseClass;
      if (cl == null) return false;
      final String enco = TextHelper.trimUq(encoding, ComVar.JAR_ENCODING); 
      return super.load1(cl, fileName, enco);
   } // load1(Class, 2*CharSequence)  // since 28.06.01

//---------------------------------------------------------------

/** Loading of properties from a .properties text file. <br />
 *  <br />
 *  Into this Prop object properties from the file denoted by {@code fileName}
 *  will be loaded. true is returned on success. For the text file's syntax
 *  see 
 *  {@link java.util.Properties#load(InputStream) Properties.load()}.<br />
 *  <br />
 *  If {@code fileName} contains no path (no \ or /), it ends with .properties
 *  and if {@link #baseClass} is not null, it will be tried to read the file
 *  as class (loader) related resource. For frame4j base classes it will be
 *  tried to find the .properties file under de.frame4j first. For that .jar
 *  encoding (usually UTF8) will be used, no matter, what {@code encoding}
 *  says. If the resource loading succeeds or not direct file loading is tried
 *  next.<br /> 
 *  <br />
 *  If that fails {@code fileName} will be suffixed by &quot;.properties&quot;
 *  if not yet ending so, and the trial will be repeated.<br />
 *  <br />
 *  If that file loading fails also the following is done:<ul >
 *  <li>If {@code fileName} contains no path (not even .\)  the trial is
 *      repeated in the jre\lib directory ({@link ComVar#JRL}).</li>
 *  <li>If that fails and if the first resource loading from a jar had failed
 *      and if a base URL ({@link #baseURL}) is set the trial is
 *      repeated as URL related load.</li></ul>
 *  <br />
 *  If all toils lead to no properties load false is returned.<br />
 *  <br />
 *  If an input stream could be made and the properties loading leads to an 
 *  exceptions (due to key or value / contract violations)b all further work
 *  ends and that exception is passed on.<br />
 *  <br />
 *  @return true if loading from a file, resource or URL succeeded
 *  @see PropMap#load1(CharSequence, CharSequence)
 *  @see #load1(URL, CharSequence, CharSequence)
 *  @see #load1(URL, CharSequence, CharSequence)
 *  @see ComVar#JRL
 */ 
   public boolean load(final CharSequence fName, final CharSequence encoding){
      String fileName = TextHelper.makeFName(fName, null);
      if (fileName == null) return false;
      String enco = TextHelper.trimUq(encoding, ComVar.FILE_ENCODING);
      String encoJ = null; // for .jar
      boolean ewp = TextHelper.endsWith(fileName, ".properties", true);
      boolean npd = fileName.indexOf(ComVar.FS) < 0;
      
      //System.out.println(" ///  TEST Prop load(" + fileName + ", " + enco
        //          + ");\n ///   " 
          //        + (ewp ? " XY.properties " : " other end ")
            //      + (npd ? " pure " : " \\ in name "));

      boolean jarLoaded = false;
      jarLoad: if (ewp && npd && baseClass != null) { // direct  baseClass related
         final Package bcP = baseClass.getPackage();
         final String bCn = bcP == null ? baseClass.getName() : bcP.getName();
         if (bcP != null) { // since May 2016
            if (load1(baseClass,
                            bCn.replace('.',  '/') + '/' + fileName, encoJ)) {
               jarLoaded = true;
               break jarLoad;
            }
         } // package based first   
         if (bCn.startsWith("de.frame4j")) { // de.frame4j as base second
            // until May 2016 "de/frame4j/" + fileName first
            if (load1(baseClass, "de/frame4j/" + fileName, encoJ)) {
               jarLoaded = true;
               break jarLoad;
            }
         }  // de.frame4j as base second (can be omitted)   
         jarLoaded = (load1(baseClass, fileName, encoJ)); // base 3rd
      } // direct  baseClass related
      
      if (load1(fileName, enco)) {
         return true;
      }
      if (!ewp) {
         fileName = fileName + ".properties";
         jarLoaded = jarLoaded || load1(baseClass, fileName, encoJ);
         if (load1(fileName, enco)) return true;
      }
      
      if (npd) { // no path defined
         if (ComVar.UD != null   // actual directory
                       && load1(ComVar.UD + fileName, enco)) return true;
         if (ComVar.JRL != null  // in JRE-Lib
                       && load1(ComVar.JRL + fileName, enco)) return true;
      } // no path defined
      if (jarLoaded) return true;
      if (load1(baseURL,   fileName, enco)) return true;
     
  /// System.out.println(" ///  TEST Prop load(" + fileName + ",) no load\n");
      return false;
   } // load(2*CharSequence)

//-----------------------------------------------------------------------
   

/** Nationalising the properties. <br />
 *  <br />
 *  This {@link Prop}erties will be nationalised according to language and 
 *  region given as parameters. null or empty will default to the properties 
 *  language and region. These, if not otherwise set explicitly, are given
 *  by {@link ComVar#UL} and {@link ComVar#UR}.<br />
 *  <br />
 *  A missing {@code fileName} is substituted by the first (originally) loaded
 *  file for this {@link Prop}.<br />
 *  <br />
 *  For nationalising two versions<ul>
 *  <li>filename_lang.properties and </li>
 *  <li>filename_lang_reg.properties.</li></ul>
 *  based on {@code fileName} are tried to be loaded from the class files 
 *  resource and in case of a pure file name on base of 
 *  {@link ComVar#JRL} and {@link ComVar#UD} by 
 *  {@link #load(CharSequence, CharSequence)}.<br />
 *  <br />
 *  If at least one of the loads is successful, true is returned.<br />
 *  <br />
 *  If the call just repeats previous calls for the same effective
 *  language and region, nothing is done and false is returned.<br />
 *  <br />
 */ 
   public synchronized boolean nationalise(final CharSequence fileName, 
                             final CharSequence lang, final CharSequence reg){
      if (RESTEST) {
         System.out.println(" ///  TEST Prop nat (" + fileName + ", " + lang
                 + ", " + reg + ");\n /// 1st :  " + firstFile);
      }
      String fileNam = TextHelper.trimUq(fileName, null);
      if (fileNam == null) {
         if (fileAnz == 0) return false;
         fileNam = firstFile;
        // int blp =  fileNam.indexOf(' ');
        // if (blp > 0) fileNam = fileNam.substring(0, blp);
      } // 15:26 08.11.00, 26.11.2001 19:53
      if (TextHelper.endsWith(fileNam, ".properties", true)){
        int len = fileNam.length();
        if (len <= 11) return false;
        fileNam = fileNam.substring(0, len - 11);
      }
      String langCode = TextHelper.trimUq(lang, null);
      if (langCode == null) langCode = getLanguage();
      String regCode = TextHelper.trimUq(reg, null);
      if (regCode == null) 
         regCode = getString("region",  ComVar.UR); // 15:11 08.11.00

      /// String forLastNat = langCode + '/' + regCode  + " :  " + fileNam;
     ///  System.out.println(" ///  TEST Prop nat " + forLastNat );
      
      if (regCode.equals(lastRC) && langCode.equals(lastLC)) {
         searchAlreadyDone: for (String s : lastNat) {
            if (s == null) break searchAlreadyDone;
            if (s.equals(fileNam)) {
             /// System.out.println(" ///  TEST Prop nat duplicate reject\n");
              return false;
            }
         } // searchAlreadyDone
         lastNat[lastNatWr] = fileNam;   // memorise this deed
         if (++lastNatWr == LAST_NAT_LEN) lastNatWr = 0;
       } else {
         lastLC = langCode;
         lastRC = regCode;
         Arrays.fill(lastNat, 1, LAST_NAT_LEN, null);
         lastNat[0] = fileNam;
         lastNatWr = 1;
      }
     
      fileNam = fileNam + "_" + langCode;
      return load(fileNam + ".properties", null) 
           | load(fileNam + "_" + regCode + ".properties", null);
   } // nationalise(3*CharSequence)
   
   final int LAST_NAT_LEN = 9;
   final String[] lastNat = new String[LAST_NAT_LEN];
   String lastLC, lastRC;
   int lastNatWr; 
   

/** Get a substitute entry for a key &mdash; nationalised. <br />
 *  <br />
 *  For the (already worked on) case of not having found an entry for<ul>
 *  <li>language_region.key</li>
 *  <li>language.key</li></ul>
 *  this method tries to find a substitute entry in the following order:<ol>
 *  <li>an entry of an {@link AppLangMap} fitting lang</li>
 *  <li>key</li>
 *  <li>en.key (only if lang is not en)</li>
 *  <li>en_GB.key (only if lang is not en and if regio is not GB)</li>
 *  <li>de.key (only if lang is not de</li>
 *  <li>an entry of an {@link AppLangMap} fitting 
 *     {@link ComVar#UL user language}, but not fitting lang</li></ol>
 *  
 *  Steps 2 .. 5 are like in the overridden 
 *  {@link PropMap#substLang(String, String, String, String)
 *                           PropMap.substLang()}.<br />
 *  If none is found, null is returned.<br />
 *  This method changes the behaviour of the (final) inherited method 
 *  {@link #valueLang(CharSequence, String)}.<br />
 *  <br />
 *  @param key non empty
 *  @param lang two letter lower case language code
 *  @param regio two letter upper case region or country code
 *           or {@link ComVar#EMPTY_STRING} if no region to be used
 *  @param def the default value; ignored in this implementation         
 */
   @Override protected PropMapHelper.Entry substLang(final String key,
                    final String lang, final String regio, final String def){
      PropMapHelper.Entry retEnt = null;
      AppLangMap alm = lastAlm;
      
      //System.out.println("  /// Prop substLang ( " + key + ", lang: " + lang
        //                     + ", reg: " + regio + ", def: " + def);
      
      if (lastAlm != null && lastLang.equals(lang)) { // already determined
         retEnt = lastAlm.entry(key);
         //System.out.println("  /// Prop substLang , lang already fit -> " + retEnt);
      } else {
         alm = lastAlm = AppLangMap.getMap(lang);
         lastLang = lastAlm.langCode;
         //System.out.println("  /// Prop substLang , get AppLangMap -> " + lang);

         if (alm.langCode.equals(lang)) // exakt fit to lang
            retEnt = alm.entry(key);
            //  System.out.println("  /// Prop substLang , lang now fit -> " + retEnt);
      }
      if (retEnt != null) return retEnt; // by AppLangMap

      // from here on like super.... ----
      retEnt = entry(key); // key
      if (retEnt != null) return retEnt;
      
      
      if (!"en".equals(lang)) {
         retEnt = entry("en." + key); // lang.key
         if (retEnt != null) return retEnt;
         if (!"GB".equals(regio)) {
            retEnt = entry("en_GB." + key); // lang.key
            if (retEnt != null) return retEnt;
         } // default en_GB.
      } // default en.
      if (!"de".equals(lang)) {
         retEnt = entry("de." + key); // lang.key
         if (retEnt != null) return retEnt;
      } // default de.
      
      // last resort lastAlm was not taken as not fitting lang
      // should then be userLang
      if (! alm.langCode.equals(lang)) // not fit to lang (already tried)
         return alm.entry(key);
      
      return null;
   } // substLang(3*String) 
   
   private String lastLang;
   private AppLangMap lastAlm;
   
/** Fitting language object. <br />
 *  <br />
 */   
   public AppLangMap getAppLangMap(){
      if (lastAlm != null) return lastAlm;
      final String lang = getLanguage();  // clears cache on change; s. above
      lastAlm =AppLangMap.getMap(lang);
      lastLang = lastAlm.langCode;
      return lastAlm;
   } // getAppLangMap()
   
/** Fetching a value preferably in user language. <br />
 *  <br />
 *  The call is equivalent to <br /> &nbsp;
 *  {@link #valueLang(CharSequence, String) valueLang(key, null)}.<br />
 *  <br />
 *  @see AppLangMap#valueUL(CharSequence)
 *  @since V.02.37 (13.12.2004)
 */
   public final String valueLang(final CharSequence key){
      return valueLang(key, null); // see substLang()
   } // valueLang(CharSequence)


//-----------------------------------------------------------------------


/** Get an input stream for a file name. <br />
 *  <br />
 *  It will be tried to make and return an {@link java.io.InputStream} for the
 *  given file name {@code fileN}. In case of success the source is 
 *  registered in the list of opened / used file resources.
 *  No success returns null.<br />
 *  <br />
 *  @return an input stream in case of success
 *  @see PropMap#load1(CharSequence, CharSequence)
 *  @see #getAsStream(String, String)
 */ 
   public InputStream getAsFileStream(final CharSequence fileName){
      final String fileN = TextHelper.makeFName(fileName, null);
      if (fileN == null) return null;
      InputStream ret = null; 
      try {
         ret = new FileInputStream(fileN);
         if (fileAnz < FILE_LIST_LEN) { //  && ret != null) {
             fileList[fileAnz] = fileN + " (as Stream)";
             ++fileAnz;
         }
      } catch (Exception e) {}
      return ret;
   } // getAsFileStream(CharSequence) 
    

/** Get an input stream for a file name as resource. <br />
 *  <br />
 *  It will be tried to determine an input stream 
 *  ({@link java.io.InputStream})from the {@code fileName} provided, that must
 *  contain no path (not even .\).<br />
 *  <br />
 *  The following will be tried:<ul>
 *  <li>a {@link #baseClass} related resource. (This would in effect be
 *      a file within the deployment .jar),</li> 
 *  <li>a file or archive resource Datei related to the base URL 
 *  ({@link #baseURL}) if set.</li></ul>
 *  <br />
 *  @return  an input stream in case of success
 *  @see #load(CharSequence, CharSequence)
 *  @see ComVar#JRL
 *  @see #getAsFileStream getAsFileStream(String)
 *  @see #getAsStream(String, String)
 */ 
   public InputStream getAsResourceStream(String fileName){
      if (baseClass == null && baseURL == null) return null;
      
      if (RESTEST) {
         System.out.println("   ///   TEST   getAsStream(" + fileName + ")");
         System.out.flush();
      }
      fileName = TextHelper.makeFName(fileName, null);
      if (fileName == null) return null;

      // check relative to actual path (not ../ or D:)
      if (fileName.indexOf(':') >= 0)  return null; 
      char firstC = fileName.charAt(0);
      if (firstC == ComVar.FS || firstC == '.') 
         return null;   // no path allowed

      if (ComVar.FS == '\\')
         fileName = fileName.replace('\\','/');
      
      InputStream ret = null;
      if (baseClass != null) try {
         ClassLoader clld = baseClass.getClassLoader();
         final String n = baseClass.getName(); // here already for Test output
         if (RESTEST) {
            System.out.println("   ///   TEST   getAsStream " + fileName 
                                                   + " by class " + n + ")");
            System.out.flush();
         }
         ret = clld.getResourceAsStream(fileName);

         if (ret == null && n.length() > 11) { // try de.frame4j etc. 
            if (n.length() > 18 && n.startsWith("de.a_weinert.apps.")) {
               ret = clld.getResourceAsStream("de/a_weinert/apps/"
                                                            + fileName);
            } else if (n.startsWith("de.frame4j.")) {
               ret = clld.getResourceAsStream("de/frame4j/" + fileName);

            }  
            if (RESTEST) {
               System.out.println("   ///   TEST   getAsStream apps " 
                       + "de/a_weinert/apps/"  + fileName 
                    + "\n      ..... = " + ret );
               System.out.flush();
            }
         } // try de.frame4j and  de.a_weinert.apps. 

         if (ret != null) {
            if (fileAnz < FILE_LIST_LEN) {
               fileList[fileAnz] = fileName + " (as Resource)";
               ++fileAnz;
               if (firstFile == null) firstFile = fileName; 
            } 
            return ret;
         } 
       } catch (Exception e) {}
 
      if (baseURL != null) try {
         URL url = new URL(baseURL, fileName);
         ret = url.openStream();
         if (fileAnz < FILE_LIST_LEN && ret != null) {
             fileList[fileAnz] = fileName + " (as URL-Stream)";
             ++fileAnz;
         }
      } catch (Exception e) {
         if (e instanceof SecurityException)
            throw (SecurityException)e; // 12.12.00
      }
      return ret;     
   } // getAsResourceStream(String)


/** Get an input stream for a file name as file or as resource.  <br />
 *  <br />
 *  In a vast variety of way it will be tried to determine an input stream
 *  ({@link java.io.InputStream}) for the {@code fileName} provided. If that 
 *  succeeds that {@link java.io.InputStream} will be returned and the
 *  {@code fileName} entered to the list of loaded files.
 *  If all fails, null is returned.<br />
 *  <br />
 *  The first trial is for files named {@code fileName} and {@code fileName}
 *  suffixed {@code ext} if {@code fileName} is not yet ending so.<br />
 *  <br />
 *  If that fails an {@code fileName} contains no path (not even  .\), the 
 *  following is tried (with and without {@code ext}):<ul>
 *  <li>a file in the jre\lib directory ({@link ComVar#JRL}),</li>
 *  <li>a resource related to the base class' ({@link #baseClass}) 
 *      loader (this would in effect be a file within 
 *      the deployment .jar),</li>
 *  <li>a file or archive resource Datei related to the base URL 
 *  ({@link #baseURL}) if set.</li></ul>
 *  <br />
 *  @return  an input stream in case of (any) success
 *  @see #load(CharSequence, CharSequence)
 *  @see ComVar#JRL
 *  @see #getAsFileStream getAsFileStream(String)
 *  @see #getAsResourceStream(String)
 */ 
   public InputStream getAsStream(String fileName, String ext) {
//      System.out.println("   ///   TEST   getAsStream(" + fileName 
  //                            + ", " + ext + ")");
      fileName = TextHelper.makeFName(fileName, null);
      if (fileName == null) return null;
      InputStream ret = getAsFileStream(fileName); 
      if (ret != null) return ret;
      while (ext != null) {
         int l1 = fileName.length();
         fileName = TextHelper.makeFName(fileName, ext);
         if (l1 == fileName.length()) break;
         ret = getAsFileStream(fileName); 
         if (ret != null) return ret;
         break;
      } // breakable if

      // check relative to actual path (not  ../  or  D:)
      if (fileName.indexOf(':') >= 0)  return null; 
      char firstC = fileName.charAt(0);
      if (firstC == ComVar.FS || firstC == '.') 
         return null;   // keine Pfadangabe

      if (ComVar.JRL != null) {
         ret = getAsFileStream(ComVar.JRL + fileName); 
         if (ret != null) return ret;
      }
      return getAsResourceStream(fileName);
   } // getAsStream(2*String) 

//----------------------------------------------------------------------


/** Change an existing property. <br />
 *  <br />
 *  This method is effectively like  
 *  {@link PropMap#setProperty(CharSequence)} with the difference of only
 *  changing an existing property (key value pair) and not creating a new
 *  one.<br />
 *  <br />
 *  If the key ends with one or more decimal digits (and is not consisting 
 *  of digits only), like e.g. butTarget18, a property beginning with 
 *  allowIndexed- and ending with the key name is searched; in the example
 *  that would be allowIndexed-butTarget. If this property has a integer
 *  interpretable value that is &gt;= that of the ending digits, than this 
 *  new property is considered as pre-existing and added to this {@link Prop}
 *  object. Returned is the empty String 
 *  {@link ComVar#EMPTY_STRING ComVar.EMPTY_STRING} in this case.<br />
 *  <br >
 *  @param   keyValue key-value pair 
 *  @return  the old value or {@link ComVar#EMPTY_STRING} if a) null before
 *           or b) allowed as new &quot;indexed property&quot;<br />
 *           null in case of failures or if rejected
 */ 
   public Object changeProperty(CharSequence keyValue){
      String keyVal = TextHelper.trimUq(keyValue, null);
      if (keyVal == null) return null;
      int len = keyVal.length();
      
      int separatorIndex;
      for (separatorIndex = 0; separatorIndex < len;
                          ++separatorIndex) { // look for separator
            char currentChar = keyVal.charAt(separatorIndex);
            if (currentChar <=  ' ' || currentChar == '='
                  || currentChar == ':') break;
      } // for look for separator
      // Skip over whitespace after key if any
      int valueIndex = separatorIndex;
      boolean sepFound = false;  // = or : found as separator
      for  ( ; valueIndex < len; ++valueIndex) {
         char actChar = keyVal.charAt(valueIndex);
         if (actChar  > ' ') {
            if (sepFound) break;
            sepFound = actChar == '=' ||  actChar == ':';
            if (!sepFound) break; 
         }
      } // skip over whitespace and 1* : or =

      String key = keyVal.substring(0, separatorIndex);
      String value = valueIndex < len ? keyVal.substring(valueIndex) : null;

      PropMapHelper.Entry entry = entry(key);
      if (entry != null) { // Entry is already existing
         String oldValue = entry.value;
         entry.setValue(value);
         return oldValue != null ? oldValue : ComVar.EMPTY_STRING;
      } // Entry is already existing

      // check if it is an indexed property
      len = key.length();
      if (len < 2) return null;
      int indBegNumber = len -1;
      int numb = 0;
      int mult = 1;
      numbSearch: for (; indBegNumber >= 0; --indBegNumber) {
         char lC = key.charAt(indBegNumber);
         if (lC < '0' || lC > '9') {
            ++indBegNumber;
            break numbSearch;
         } 
         numb += (lC - '0') * mult;  
         mult *= 10;
      } // numbSearch
      if (mult == 1 ||  indBegNumber < 1 ) return null; // nur | 0 Ziffs.
      String pureKeyName = key.substring(0, indBegNumber);
      int maxInd = getInt("allowIndexed-" + pureKeyName, -1);
      if (numb > maxInd) return null; 
      put(key, value);
      return ComVar.EMPTY_STRING;
  } // changeProperty(CharSequence) 


/** Evaluate or parse a sequence of parameters. <br />
 *  <br />
 *  An array of String parameters (String[]) {@code args} will be worked on
 *  from start to end or the first occurrence of the character sequence 
 *  {@code commSt}. This is accompanied by a strict syntax check: parameters
 *  the usage of with is not defined in this {@link Prop} object throw an 
 *  exception. By using the similar but less strict method 
 *  {@link #parse(java.lang.String[],CharSequence,boolean)
 *  parse(String[],CharSequence,false)} this strictness can be partly switched 
 *  off (for applications that will rework the parameters).<br />
 *  <br />
 *  The parameters fall in three categories:<ul>
 *  <li>  Option's parameters<br />
 *        They start with a minus sign (-), 
 *        Case does not not matter with option parameters.</li>
 *  <li>  Set parameters<br />
 *        They contain an equals sign (=), but no question mark (?) or colon
 *        (:) before it.</li>
 *  <li>  Property file parameters <br />
 *        They start with the ape sign (@).</li>
 *  <li>  Standard parameters<br />
 *        That are all that don't fall in one of the above 
 *        categories.</li></ul>
 *        
 *  <b>Option parameters<br /></b>
 *  For an option parameter a property named <code>option-parameter</code>
 *  (in all lower case) is searched in this {@link Prop} object. If it does
 *  not exist or has an empty value an exception is raised.<br />
 *  <br />
 *  If this property has a value in the form &quot;key=value&quot; (with an
 *  equals sign) the property key is set to value.<br />
 *  <br />
 *  If this property has a value in the form &quot;key&quot; (without an
 *  equals sign) the property key is set to the next parameter in the array 
 *  {@code args} as it is. If there is no next parameters (before 
 *  {@code commSt}) an exception is raised.<br />
 *  <br />
 *  Multiple entries defining the effect of the option can be put into one
 *  such property separated by &quot;##&quot;. Example:<br />
 *  option-floor1 = logDat=floor1.log ## language=de ## alertLevel=9<br />
 *  In case of space problems continuation lines are available.<br />
 *  <br />
 *  <b>Set parameters<br /></b>
 *  <br />
 *  For a set parameter, having the form &quot;key=value&quot; (with an equals
 *  sign) an existing property key will be set to the new value. If this 
 *  property does not exist an exception is raised. A set parameter may not
 *  (!) define a non existing property.<br />
 *  The only exception are indexed properties in the form 
 *  {@code "butHref17=otto"}, the definition of which may be allowed by a
 *  special property {@code "allowIndexed-butHref=19"} (all examples) in the
 *  range {@code butHref0} respectively {@code butHref00}
 *  to {@code butHref19}.<br />
 *  <br />
 *  <b>Property file parameter<br /></b>
 *  <br />
 *  For a property file parameter @filXY (e.g) the properties of a file named
 *  filXY (in the example, without the leading @) or filxy.properties
 *  will be loaded and if possible its nationalised versions by 
 *  {@link #nationalise nationalise()}). If no such file exists an exception
 *  is raised.<br />
 *  Attention: If the properties language or region were changed, nationalised
 *  versions of the base .properties (named according to application class
 *  in most cases) file will be loaded as last step in that proceeding.<br />
 *  <br />
 *  <b>Standard parameters<br /></b>
 *  <br />
 *  For the first found standard parameter a property named {@code word-0} or 
 *  is searched for; for the second {@code word-1} and so on. If this 
 *  property does not exist an exception is raised. Otherwise its value is
 *  taken as the name of the property to be set by the standard 
 *  parameter.<br />
 *  <br />
 *  Word numbers in the range 0 to 9 may be given as two digit letters with a
 *  leading zero, as {@code word-00} to {@code word-09}.<br />
 *  <br />
 *  Hint <b>Minus sign handling</b>: &nbsp; To begin a standard parameter with
 *  a minus sign double it. Example: -otto is option parameter otto and --otto
 *  is the standard parameter  -otto. The same applies for numbers: to have a
 *  negative one as parameter use --1. (Otherwise it would be option 1 
 *  requiring an existing property option-1 .<br />
 *  <br />
 *  Hint on <b>null</b>: &nbsp; A String supplied by the JVM to the 
 *  application's method main() is never null. It may be empty, but if not, no
 *  String contained is null (and very seldom empty). The methods 
 *  {@link #parse(String[], CharSequence)} and 
 *  {@link #parse(String[], CharSequence, boolean)} rely on the supplied
 *  String[] fulfilling the same contract as JVM to main(String[]) to do
 *  useful work. If not they do just nothing.<br />
 *  <br />
 *  This method just calls  {@link #parse(String[], CharSequence, boolean) 
 *  parse(args, commSt, false)}.<br />
 *  <br />
 *  @param args   the (start) parameters / arguments to parse
 *  @param commSt if not empty a comment start within {@code args} and hence
 *                its effective end for this methods work
 *  @exception IllegalArgumentException on the contract violations named
 *  @see PropMap#setProperty(CharSequence) setProperty()
 *  @see #changeProperty changeProperty()
 *  @see #load(CharSequence, CharSequence) load()
 *  @see #setFields setFields()
 */
   public void parse(String[] args, CharSequence commSt) 
                      throws IllegalArgumentException {
      parse(args, commSt, false);
   } // parse(String[], CharSequence)


/** Evaluate or parse a sequence of parameters. <br />
 *  <br />
 *  This method is like exactly like 
 *  {@link #parse(String[],CharSequence) parse(String[], CharSequence)} if 
 *  the parameter {@code partial} is false.<br />
 *  When {@code partial} is true there are the following differences:<ol>
 *  <li>Parameter beginning with @ are not interpreted as order to
 *      load another .properties file but as standard parameter, except when a
 *      property load@prop is true (by 
 *       &quot;load@prop&nbsp;=&nbsp;on&quot; in the base .properties file
 *       e.g.).</li>
 *  <li>Standard parameters don't throw exceptions if the related property
 *      word-0, word-1 ... etc. does not exist.</li>
 *  <li>For option parameters a property {@code option-name} and also a 
 *      property {@code option+name} is looked for (all in lower case). If 
 *      both don't exist an exception is thrown.</li>
 *  <li>An option defined by a &quot;+ property&quot; (see point 3) is 
 *      considered as only relevant for the following parameters / arguments
 *      in their sequence. <br />
 *      It will not be set null in {@code args} and may be (sequence
 *      dependent re-) evaluated by the application code.<br />
 *      Therefore it is allowed to define an option with no effect for this
 *      method by the form &quot;<code>option+dummy=</code>&quot; or 
 *      &quot;<code>option+dummy= ##</code>&quot; without raising an 
 *      exception. If the option value stars with # (as in the second example)
 *      the count of word parameters will be set to 30 if less.<br />
 *      The effect is that unsatisfied word-0, word-1 etc. options won't
 *      get one of the word parameters to follow, hence, keeping them for 
 *      later evaluation by application code.</li>
 *  <li>An option (-xyz) defined by a &quot;minus property&quot;, like
 *      {@code option-xyz=effect}, is considered as effective for the 
 *      application as whole and independent of parameter sequence. The 
 *      related parameter(s) will be set null in {@code args} after 
 *      evaluation.</li>
 *  <li>A standard parameter used by the preceding option or by a
 *      word-0, word-1 etc. definition will be set null in {@code args}.</li>
 *  <li>If a comment start defined by {@code commSt} all parameters thereafter
 *      will be set null or shortened accordingly.</li>
 *  </ol><br />
 *  This method is (called with partial=true) made for applications who
 *  want a comfortable and powerful basic evaluation of the start arguments
 *  by {@link Prop}, but want to re-evaluate the (remaining) arguments in
 *  their sequence. Points 5 to 7 facilitate this proceeding by eliminating
 *  all evaluated global parameters as well as a parameter (command line)
 *  comment. An example for this is the tool Era (source 
 *  <a href="../doc-files/Era.java">Era.java</a>, .properties file
 *  <a href="../doc-files/Era.properties">Era.properties</a>).<br />
 *  <br />
 *  @see  #parse(String[],CharSequence) parse(String[], CharSequence)
 *  @param partial if true behaviour other than 
 *        {@link #parse(String[], CharSequence)} if false see  
 *        {@link #parse(String[], CharSequence) description there}
 *  @exception IllegalArgumentException on the contract violations described
 */
   public void parse(final String[] args, final CharSequence commSt, 
                      final boolean partial) throws IllegalArgumentException {
     if (args == null) return;
     int argsL  = args.length;
     if (argsL == 0)   return;
     String aktArg = null;
     int commPos   = commSt != null && commSt.length() > 0 ? -1 : -9;
     String keyUse = null; // flag to use next parameter as value for keyUse
     int wordNo    = 0; 
     boolean loadProp = !partial || getBoolean("load@prop");
     if (RESTEST) System.out.println(" /// TEST Prop.parse: partial = " 
                                     + partial + ", propload = " + loadProp);
     boolean plusOption = false;
     parLoop: for (int i = 0; i < argsL && commPos < 0; ++i) {
        aktArg = args[i];
        if (aktArg == null) continue parLoop;
        aktArg = aktArg.trim();
        if (aktArg.length() == 0) continue parLoop;
        if (commPos == -1) { // search comment 
           commPos = TextHelper.indexOf(aktArg, commSt, 0, false);
                   ////  aktArg.indexOf(commSt);
           if (commPos >= 0) { // comment found 
               if (commPos == 0) { 
                  aktArg = null;
               } else {
                  aktArg = aktArg.substring(0, commPos).trim();
                  if (aktArg.length() == 0) aktArg = null;
               }
               if (partial) { // if partial delete / clear the unused
                  args[i] = aktArg;
                  for (int j = i + 1; j < argsL; ++j)
                     args[j] = null;
               } // partial
               if (aktArg == null) break parLoop;
           }   // comment found            
        }   // search comment              
        if (keyUse != null) { // this parameter is the value for ...
          if (RESTEST) System.out.println(" /// TEST prop \"" + keyUse
              + "\" = " + aktArg + " ("
                 + (partial && !plusOption ? "removed)" : "kept)"));
           put(keyUse, aktArg);
           keyUse = null; // done
           if (partial && !plusOption)  args[i] = null;
           continue parLoop;
        } // this parameter is the value for ...
        if (loadProp && aktArg.charAt(0) == '@') { // file denotation
           aktArg = aktArg.substring(1);
           boolean anyLoad = load(aktArg, null);
           if (!anyLoad  
                && !TextHelper.simpLowerC(aktArg).endsWith(".properties"))
              anyLoad = load(aktArg + ".properties", null);
           anyLoad |= nationalise(aktArg, null, null);
           if (!anyLoad)  
              throw new IllegalArgumentException(valueLang("nofil")
                                     + aktArg + "[.properties]");
           if (partial)  args[i] = null; //// 12.09.2002
           continue parLoop;
        } // file parameter
        
        if (aktArg.charAt(0) == '-' && aktArg.length() > 1) { // Start mit -
          final char char1 = aktArg.charAt(1);
          if (char1 == '-') { // double -
             aktArg = aktArg.substring(1); // first minus removed
             if (RESTEST) System.out.println(" /// TEST option - removed = "
                                                                  + aktArg);
          } else if  (char1 >= '0' && char1 <= '9') { // neg number
             // will fall though to pure word parameter if no = inside
          } else { // neg number else -option
           aktArg = TextHelper.simpLowerC(aktArg.substring(1));
           plusOption = false;
           int     len    = 0;
           String  optSet = value("option-" + aktArg);
           if (optSet == null) {
               if (partial) {
                   optSet = value("option+" + aktArg);
                   plusOption = optSet != null;
               }
           }
           if (optSet != null) {
             len = (optSet = optSet.trim()).length();
           }
           if (RESTEST) System.out.println(" /// TEST parse "
                                  + (plusOption ? '+' : '-') + "\"" + aktArg 
                                     + "\" by \""  + optSet + "\"");
           if (len == 0) { // no action for option defined
              if (!plusOption && !allowNoPropFile) // && !all.. since 06.04.21
                 throw new IllegalArgumentException(valueLang("unknopt") 
                                                                  + aktArg);
              // to here only with option+ and parse partial 
              continue parLoop; 
           } // no action for option defined
           if (plusOption && optSet.charAt(0) == '#') { // option+yxz= #.....
             if (wordNo < 30) wordNo = 30;
           } // option+yxz= #.....
           int startIndex =  0;
           int endIndex   = -1;
           int glPos      = -1;
           String optLine = null;
           optLineLoop: do {
              endIndex = optSet.indexOf("##", startIndex);
              if (endIndex < 0) endIndex = len;
              optLine = optSet.substring(startIndex, endIndex).trim();
              startIndex = endIndex + 2;
              if (optLine.length() == 0) continue optLineLoop;
              glPos = optLine.indexOf('=');
              if (glPos < 1) {  // +defined option not evaluated here
                 if (plusOption) {
                    if (glPos == 0)  continue optLineLoop;
                    glPos = optLine.indexOf(',');
                    if (glPos >= 0)  continue optLineLoop;
                 } // +defined option not evaluated here
                 if (keyUse != null) { /// CAN that happen here ????
                    throw new IllegalArgumentException(
                       "\n * multiple settings with next parameter" + aktArg);
                 }
                 keyUse = optLine;   // error corrected 20.03.20000
                 continue optLineLoop;
              }
              setProperty(optLine);
           } while (endIndex < len);  // optLineLoop
           if (partial && !plusOption)
               args[i] = null;  // used, -opt  && partial ==> remove
           continue parLoop; 
         } // option
        } // Start mit -
        
        int glPos = aktArg.indexOf('=');
        dtSet: if (glPos > 0) { // direct set
           int fdPos = aktArg.indexOf(':');
           if (fdPos >= 0 && fdPos < glPos) break dtSet; // ..:..= is word
           fdPos = aktArg.indexOf('?');
           if (fdPos >= 0 && fdPos < glPos) break dtSet; // ..?..= is word
           if (changeProperty(aktArg) == null) 
              throw new IllegalArgumentException(valueLang("unknprop")
                                              + aktArg.substring(0, glPos));
           if (partial) args[i] = null;  // used && partial ==> remove
           continue parLoop;             // ^ and the next please
        } // direct set
        
        // to here only with a pure word parameter 
        String wordSet = getProperty("word-", wordNo);
        if (wordSet == null || wordSet.length() == 0) { // no wordset
           if (partial) continue parLoop; // allows keep pure word param
           throw new IllegalArgumentException(
                TextHelper.messageFormat(null, valueLang("morespar"), 
                    new int[]{wordNo} ).toString());
        } // no wordset
        put(wordSet, aktArg);
        if (RESTEST) System.out.println(" /// TEST prop set / changed "
                      + wordSet + "[" + wordNo + "] = "
                      + aktArg + " (" + (partial ? "removed)" : "kept)"));
        wordNo++;
        if (partial) args[i] = null;  // used && partial ==> remove
     }// for parLoop 
     if (RESTEST) {
       String restArgs = TextHelper.prepParams(args);
       System.out.println(" /// TEST Prop.parse: partial = " + partial
                 + ", wordNo = " + wordNo
                     + "\n /// TEST args left: " + restArgs); 
     }  // RESTEST
     if (keyUse != null) { // missing set parameter after option xyz=
        throw new IllegalArgumentException(
             TextHelper.messageFormat(null, valueLang("missval"), 
                                                        aktArg).toString());
     } // missing set parameter after option xyz=
   } // parse(String[], CharSequence, boolean)

   
/** Fetching a &quot;numbered&quot; or indexed property as non empty 
 *             String. <br />
 *  <br />
 *  This method returns a non empty String if and only if in this {@link Prop}
 *  object contains a property named &quot;{@code name}&quot; + {@code num} 
 *  according to the rules of the method 
 *  {@link #getProperty(CharSequence, int) getProperty(name, num)}, the value
 *  of which is a non empty String also after removing surrounding white
 *  space.<br />
 *  <br />
 *  This non empty String is returned or {@code def} otherwise.<br />
 *  <br />
 *  <br />
 *  @param   name the searched for property's name
 *  @param   num  its number or index
 *  @param   def  the substitute value if no non empty value is found
 *  @return       the non empty value for key(name, number) or def
 *  @see #getString(CharSequence, String) getString(String,String)
 *  @see #getInt getInt()
 *  @see #getBoolean getBoolean()
 *  @see PropMap#setProperty(CharSequence) setProperty()
 *  @see #changeProperty changeProperty()
 */
   public String getString(String name, int num, String def){
      if (name == null) return null;
      name = name.trim();
      String val = getProperty(name, num);
      if (val == null) return def;
      return TextHelper.trimUq(val, def);
   } // getString(String, int, String) 

   

/** Fetching a &quot;numbered&quot; or indexed property as non empty 
 *             String. <br />
 *  <br />           
 *  If {@code key} is null or empty or if {@code num} is &lt; 0, null is
 *  returned.<br />
 *  <br />
 *  If this {@link Prop} object contains a property {@code key + num} in the 
 *  variants described its value is returned even if empty.<br />
 *  <br />
 *  If  <code>num</code> is in the range 0 to 9 and no such property (like 
 *  e.g. &quot;name7&quot;) was found the trial is repeated with a leading 
 *  zero (hence &quot;name07&quot; in the example).<br />
 *  <br />
 *  If no such property exists the trial is repeated using index brackets in
 *  for the property's name, like &quot;name[7]&quot; in the example.<br />
 *  <br />
 *  If also this fails and if {@code num} is 0 the property {@code key}
 *  without any number is looked for.<br />
 *  <br />
 *  Otherwise null is returned. This is done for {@code key} null or 
 *  {@code num} &lt;  0.<br />
 *  <br />
 *  @param key the key (without number) to the property
 *  @param num the number (&gt;= 0) 
 *  
 */
   public String getProperty(final CharSequence key, final int num){
      if (key == null || num < 0) return  null;
      final String name = TextHelper.trimUq(key, null);
      if (name == null) return  null;
      final String nuSt = String.valueOf(num);
      String ret = value(name + nuSt);
      if (ret == null && num <= 9) 
         ret = value(name + '0' + nuSt); // leading  0 for one digit
         
      if (ret == null)
         ret = value(name + '[' + nuSt + ']'); // try index brackets
            
      if (ret == null && num == 0)
         ret = value(name); // also direct for index [0] // since April 2008
      
      return ret;  ////  instanceof String ? (String)ret : null;
   } // getProperty(CharSequence, int)
   

/** Fetching a property also indirectly from a resource &mdash; 
 *                                                      nationalised. <br />
 *  <br />
 *  If this {@link Prop} object contains a property named 
 *  {@code &lt;key&gt;File} ({@code key} being the parameter}, may be 
 *  language prefixed in the form .de, .en .fr and so on, its non empty value,
 *  if given, will be taken as the name of a file or resource. It is tried 
 *  to read its content by using 
 *  {@link #getAsStream getAsStream(keyFile, null)}. If this succeeds the 
 *  read content is returned as String.<br />
 *  <br />
 *  If this indirect fetching fails or if the mentioned auxiliary property
 *  {@code &lt;key&gt;File} is not given 
 *  {@link  #valueLang(CharSequence, String) valueLanguage(key, def)}
 *  is returned.<br />
 *  <br />
 *  Hint: The resolution via {@code &lt;key&gt;File} including a default 
 *  resource name without language prefix has priority over a possibly 
 *  existing property lang.key fitting the language. This is to keep in mind
 *  if both possibilities are mixed.<br />
 *  <br />
 *  Hint 2: This method's look up proceeding is not quite cheap (which is 
 *  partly the source of its comfort for the application programmer). Contrary
 *  to {@link #valueLang(CharSequence, String)} this method does no caching.
 *  So this method should be called only once per language dependent property
 *  or file / resource. If the value is needed multiple times it should be 
 *  stored / cashed by the application.<br />
 *  <br />
 *  @param   key  the searched property's name
 *  @param   def  a substitute value (may be null)
 *  @return       the property's value or def
 *  @see #valueLang(CharSequence)
 *  @see #getAsStream(String, String)
 */
   public String getResString(final CharSequence key, String def){
      final String name = TextHelper.trimUq(key, null);
      if (name == null) return def;
      String val = valueLang(name + "File", null);
      tryFile: if (val != null) {
        InputStream is = getAsStream(val, null);
        if (is == null) break tryFile; 
        Input ein = new Input(is);
        ByteArrayOutputStream bao = new ByteArrayOutputStream(500);
        ein.copyTo(bao);  // this also closes ein
        ein.close(); // not necessary; just to get rid of Eclipse warning
        try {
           val = bao.toString(ComVar.FILE_ENCODING);
        } catch (UnsupportedEncodingException e1) {
           val = bao.toString();
        }
        if (val != null && val.length() != 0) return val;
      } // tryFile
     return valueLang(name, def);   
   } // getResString (CharSequence, String)


/** Fetching a property as (non null) text. <br />
 *  <br />
 *  This method fetches the value of the property {@code key} 
 *  or the empty String {@link ComVar#EMPTY_STRING}.<br />
 *  <br />
 *  If in the property value fetched other properties's keys are embedded 
 *  embraced in two percent (%) signs, such an entry (that spot) will be
 *  replaced by the named property's value.<br />
 *  %% will be escaped to %.<br />
 *  <br />
 *  @param   key  the searched property's name
 *  @return  key's value as String, may be with the substitutions described
 *  @see PropMap#getString(CharSequence)
 *  @see PropMap#getString(CharSequence, String)
 *  @see PropMap#getInt(CharSequence, int)
 *  @see PropMap#getBoolean(CharSequence)
 *  @see PropMap#setProperty(CharSequence) setProperty()
 *  @see #changeProperty changeProperty()
 */
   public String getText(final CharSequence key){
      if (key == null) return ComVar.EMPTY_STRING;
      final String name = TextHelper.trimUq(key, null);
      if (name == null) return ComVar.EMPTY_STRING;
      final String val = value(name);
      return percReplace(val);
   } // getText(CharSequence )

/** Replace references to properties by their values. <br />
 *  <br />
 *  The sequence {@code val} will be searched for names enclosed by two
 *  percent signs %, without enclosing a space. In 
 *  &quot;xxxyyy%nameX%&nbsp;hhh&quot; for example nameX would be recognised 
 *  as such a name, but not in the case of
 *  &quot;xxxyyy%name&nbsp;%&nbsp;hhh&quot;.<br />
 *  <br />
 *  All spots found in this way (&quot;%nameX%&quot;) will be replaced by
 *  the value of the property so named, the value being evaluated by
 *  {@link #valueLang(CharSequence) valueLang(name)}. The replacement is
 *  hence nationalised.<br />
 *  <br />
 *  If no replacement value can be found the spot remains unchanged including
 *  the surrounding percents %.<br />
 *  <br />
 *  %% is replaced by %.<br />
 *  <br />
 *  If {@code val} is null {@link ComVar#EMPTY_STRING} is returned.<br />
 *  <br />
 *  @param val the sequence to work on
 *  @return val, may be with replacements; never null
 */
   public String percReplace(final String val){
      if (val == null) return ComVar.EMPTY_STRING; 
      final int valLen = val.length();
      if (valLen == 0) return ComVar.EMPTY_STRING;
      int proz1 = val.indexOf('%');
      if (proz1 < 0 || (proz1 + 1 == valLen)) return val;
      int proz2 = val.indexOf('%', proz1 + 1);
      if (proz2 < 0) return val;
      int lProz2 = 0;
      StringBuilder ret = new StringBuilder (valLen + 40);
        while (proz2 > 0) {
           ret.append (val.substring(lProz2, proz1));
           lProz2 = proz2 + 1;
           if (proz1 + 1 ==  proz2) {
              ret.append('%');
           } else {
              String nam = val.substring(proz1 + 1, proz2);
              if (nam.indexOf(' ') >= 0) {
                ret.append('%');
                ret.append(nam);
                lProz2 = proz2;
              } else {
                 String pv = valueLang(nam, null); // lang since 24.01.2005
                 if (pv != null) 
                    ret.append(pv);
                 else 
                    ret.append(val.substring(proz1, lProz2));
              } // prop-inset
           }      
           if (lProz2 == valLen) break;
           proz2 = -1;
           proz1 = val.indexOf('%', lProz2);
           if ((proz1 > 0) && (proz1 + 2 < valLen)) 
              proz2 = val.indexOf('%', proz1 + 1);
           if (proz2 < 0) 
               ret.append(val.substring(lProz2));
        } //while
        return ret.toString();   
   } // percReplace(String)

/** Fetching the property &quot;helpText&quot; nationalised. <br />
 *  <br />
 *  The property's &quot;helpText&quot; will be nationalised (by
 *  {@link #valueLang(CharSequence, String) 
 *     valueLang(&quot;helpText&quot;, def)}) and with possible replacements
 *  (compare  {@link #percReplace(String) percReplace()} and returned.<br />
 *  <br />
 *  @param def substitute text if the property helpText respectively 
 *            de.helpText, en.HelpText etc. does not exist 
 *  @return the nationalised help text with may be some replacements
 */
   public final String getHelpText(final String def){
      final String vL = valueLang("helpText", def);
      if (vL == def) return def;
      return percReplace(vL);
   } // getHelpText(String)
   
/** Fetching a &quot;numbered&quot; property as boolean with default. <br />
 *  <br />
 *  This method determines by the rules of the method 
 *  {@link #getProperty(CharSequence, int)
 *  getProperty(name, num)} the (indexed) property specified. If no such 
 *  property exists {@code def} is returned.<br />
 *  <br />
 *  Otherwise it is tried to interpret the value of the property as boolean by
 *  {@link TextHelper#asBoolean(CharSequence, boolean) 
 *  asBoolean(value, def)}. The result is returned.<br />
 *  <br />
 *  @see PropMap#getBoolean(CharSequence, boolean)
 *  <br />
 *  @param   name the searched property's name
 *  @param   def  a substitute value (may be null)
 *  @return  the boolean value of the property or def
 */
   @Override public boolean getBoolean(final CharSequence name, 
                                          final int num, final boolean def){
     String value = getProperty(name, num);
     if (value == null) return def;
     return TextHelper.asBoolean(value, def);
   } // getBoolean(CharSequence, int, boolean) 


/** Fetching a &quot;numbered&quot; property as int with default. <br />
 *  <br />
 *  This method determines by the rules of the method 
 *  {@link #getProperty(CharSequence, int)
 *  getProperty(name, num)} the (indexed) property specified. If no such 
 *  property exists {@code def} is returned.<br />
 *  <br />
 *  Otherwise it is tried to interpret the value of the property as int. If
 *  possible that value is returned otherwise def.<br />
 *  <br />
 *  @param   name the searched property's name
 *  @param   def  a substitute value
 *  @return  the int value of the property or def
 *  @see #getInt(CharSequence, int)
 *  @see TextHelper#asInt(CharSequence, int)
 *  @see PropMap#getLong(CharSequence, long) getLong()
 *  @see TextHelper#asInt(CharSequence, int)
 */
   @Override public int getInt(final String name, 
                                              final int num, final int def){
     if (name == null) return def;
     String value = getProperty(name, num);
     if (value == null) return def;
     return TextHelper.asInt(value, def);
   } // getInt(String, 2*int)

//---------------------------------------------------------------------------   

   
/** Setting an object's fields (beans properties). <br />
 *  <br />
 *  The fields (variables) respectively the Java Beans properties of the
 *  object {@code obj} will be set by this {@link Prop} object's properties 
 *  of the same name. Keys containing dot (.), minus (-) or plus (+) can't be
 *  variable's names and are not considered in the proceeding (see 
 *  below).<br />
 *  <br />
 *  For every possible key <code>property</code> a property 
 *  &quot;lang.property&quot; is looked for (lang being the result of 
 *  {@link #getLanguage()}). If it exists its value is taken otherwise 
 *  that of  <code>property</code>.<br /> 
 *  <br />
 *  The setting of the variable &quot;property&quot; is either done directly 
 *  by setting the public object variable or preferably by a fitting setter 
 *  method named {@code setProperty(...)}. Thereby this method uses the 
 *  methods {@link PropMapHelper#setField(Object, String, String)} and
 *  searches in the sequence described 
 *  {@link PropMapHelper#setField(Object, String, String) there} setters 
 *  and at last the direct access to the public (not inherited)
 *  variable.<br />
 *  <br />
 *  This method uses &quot;introspection&quot; of {@code obj} to call methods
 *  or set variables the existence of which this method can by no means know
 *  beforehand, of course.<br />
 *  <br />
 *  Hint: The proceeding described prefers on language English (e.g.) a
 *  property en.helpText (e.g.) over helpText even if helpText was
 *  nationalised (English) by a loaded language specific .properties file
 *  (loaded by 
 *  {@link #nationalise(CharSequence, CharSequence, CharSequence)}).
 *  Hence multilingual values in one .properties file should not be combined
 *  with extra nationalised .properties files for the same key.<br />
 *  <br />
 *  @param obj the object the field of which shall be modified
 *  @return the number of fields / variables changed
 *  @exception SecurityException if the necessary introspection is
 *          forbidden
 *  @throws IllegalArgumentException if a set method called throws an 
 *          exception (e.g. an {@link IllegalArgumentException} or a 
 *          {@link java.beans.PropertyVetoException}. The exception's
 *          message is put into the (new) {@link IllegalArgumentException}
 *  @see PropMap#getString(CharSequence)
 *  @see #getInt getInt()
 *  @see #getBoolean getBoolean()
 *  @see #parse(String[], CharSequence) parse()
 *  @see PropMapHelper#setField(Object, String, String)
 */
   public int setFields(final Object obj)
                         throws SecurityException, IllegalArgumentException {
      if (obj == null) return 0;
      int noChanged = 0;
      final String lang = getLanguage() + '.';
      
      loopEntries: for (PropMapHelper.Entry e : entries) {
         if (e == null) continue loopEntries;
         final String key = e.key;
         if (key.indexOf('-') >= 0 
                      || key.indexOf('+') >= 0 || key.indexOf('.') >= 0 )  
            continue loopEntries; // No toils for option-, option+ and word-
         final int indLang = indexOfKey(lang + key);
         if (indLang >= 0) e =  entries[indLang];
         final String value = e.value;
            ///  PropMap.load allows null as value (without the =)
            ///  TextHelper.trimUq(e.value, null);
            ///  e.value; back again breaks code 18.01.2005
                 ////   getString(key, null); //  e.value;
         if (PropMapHelper.setField(obj, key, value)) ++noChanged;
      } // loopEntries: for 
      return noChanged;
   } // setFields(Object)

   
/** Output all properties to a PrintStream . <br />
 *  <br />
 *  @see #list()
 */
   public void list(final PrintStream out){
      if (out != null) out.println(list());
   } // list(PrintStream)

/** Output all properties to a PrintWriter . <br />
 *  <br />
 *  @see #list()
 */
   public void list(final PrintWriter out){
      if (out != null) out.println(list());
   } // list(PrintWriter)


/** List  all properties as StringBuilder . <br />
 *  <br />
 *  This method is useful for tests.<br />
 *  <br />
 *  After a headline comes the list of all loaded files or resources. 
 *  Thereafter all properties contained are listed in (key) alphabetical
 *  order (one line per property).<br />
 *  <br />
 *  If the value is interpretable as number or as boolean value
 *  (by {@link #getInt getInt()} respectively by 
 *  {@link #getBoolean getBoolean()}) this interpretation is appended 
 *  after &lt;&lt;.<br />
 *  <br />
 *  keys (not allowed, can not happen)  and values beginning or ending by
 *  white space will be set in quotes.<br />
 *  <br />
 *  @see #sortedKeys()
 *  @see #list(PrintStream)   list(PrinWriter/PrintStream)
 */
   public StringBuilder list(){
      String[] sKeys = sortedKeys();
      int len = sKeys.length;
      StringBuilder bastel = new StringBuilder((len + 3) * 45);

      bastel.append(valueLang("ldprplst"));
     // fr.ldprplst  = \n\nListe der properties:\n\n
     // fr.ldseares  = fichiers / resources cherchées / chargées:\n

      if (fileAnz > 0) {
         bastel.append(valueLang("ldseares"));
                    // "   Geladene/gesuchte Dateien/Resourcen:\n");
         for (int i = 0; i <  fileAnz; ++i) {
            bastel.append("\n   ");
            bastel.append(i + 1);
            bastel.append(" : ");
            bastel.append(fileList[i]);
         }
         bastel.append("\n\n");
      }

      Boolean b = null;
      int n = 0x80000002;
      String value = null, key = null;
      for (int i = 0; i < len; ++i ) {
         key   = sKeys[i];
         int lk = key.length();
         value = value(key);
         b = TextHelper.asBoolObj(value);
         if (b == null) n = TextHelper.asInt(value, 0x80000002);
         boolean quote = !key.equals(key.trim());
         if (quote) bastel.append('\"');
         bastel.append(key);
         if (quote) {
            bastel.append('\"');
            lk += 2;
         }
         lk = 18 - lk;
         while (lk > -30 && lk < 0 ) lk += 6;
         for (; lk > 0; --lk) bastel.append(' ');          
         bastel.append(" = ");
         quote = value != null && !value.equals(value.trim());
         if (quote) bastel.append('\"');
         bastel.append(value);
         if (quote) bastel.append('\"');
         bastel.append(" \t");
  
         if (b != null) {
            if (b == Boolean.TRUE && ! "true".equals(value)) {
               bastel.append(" >> true");
            } else if (b == Boolean.FALSE && ! "false".equals(value))
                bastel.append(" >> false");
         } else if (n != 0x80000002) {
             bastel.append(" >> ");
             String tmp = Integer.toString(n);
             if(! tmp.equals(value)) {
                bastel.append(tmp);
             } else {
                tmp = "0x" + Integer.toHexString(n);
                if(! TextHelper.areEqual(tmp, value, true)) 
                    bastel.append(tmp);
             }
         }
         bastel.append('\n');
      } // for
      return bastel;
   } // list()
  
} // class Prop (08.12.2003, 02.07.2004, 22.09.2006, 09.05.2016)
