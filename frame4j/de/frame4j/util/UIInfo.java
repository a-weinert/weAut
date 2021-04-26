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

import java.lang.annotation.Annotation;

import de.frame4j.time.TimeHelper;
import de.frame4j.text.TextHelper;

/** <b>A set of user interface information elements</b>. <br />
 *  <br />
 *  Objects of this type (objects of an implementing class) provide a common
 *  set of text information for user interfaces and documentation.<br />
 *  <br />
 *  Applications might be able to provide some textual user information, like
 *  an online help text or version information. Usually those texts are asked
 *  for by menu or by command line options like -help or -version.<br />
 *  This interface type provides an uniform way to gather those texts.<br />
 *  <br /> 
 *  Implementations may, of course, directly provide those informations. 
 *  Often it's better to generate them from {@link Annotation}s or
 *  (preferably nationalised) from associated {@link PropMap properties} 
 *  using Frame4J's applicable helper methods.<br />   
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2009  &nbsp; Albrecht Weinert 
 *  @see ComVar
 *  @see TimeHelper
 *  @see TextHelper
 *  @see AppHelper
 *  @see PropMap
 *  @see Prop
 *  @see AppLangMap
 *  @see MinDoc
 */
 //  So far   V.o55+   (29.01.2009 14:43) : new
 //           V.o5x+   (22.01.2009 07:10) : reduced

@MinDoc(
   copyright = "Copyright 2009, 2015  Albrecht Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 37 $",
   lastModified   = "$Date: 2021-04-10 10:00:03 +0200 (Sa, 10 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "type for (alterable) date/time including zone/calendar info",  
   purpose = "the type (interface) of an alterable Time-Object"
) public interface UIInfo {

/** Get the author's name. <br />
 *  <br />
 *  Returned is the first name and last name of the (software's) author 
 *  without surrounding whitespace. Multiple authors are separated by 
 *  &quot;, &quot;. The returned text is not longer than 62 characters.<br /> 
 *  <br />
 *  The named person(s) may be different from those (optionally) named in
 *  {@link #getVersDate versDate} as last modifier or in 
 *  {@link #getCopyright copyright} as copyright holder.<br />
 *  
 *  @return the name of the source code author
 */
   public String getAuthor();
   

/** Get a small abstract or about text on the object or its class. <br />
 *  <br />
 *  The returned value should be a small synopsis in the sense of the usual
 *  about texts. The text may be in English or the author's language, but
 *  preferable nationalised to a language set or the platform language.<br />
 *  <br />
 *  The returned text may be multi-line, with no line length longer than
 *  78 characters and with no (0) preceding or trailing empty lines.<br />
 *  <br />
 *  If not available or applicable, null is returned.<br /> 
 *    
 *  @return the application's short about text
 */
   public String getAbout();


/** Get a title text. <br />
 *  <br />
 *  The returned value is for the use as windows titles for graphical
 *  components or command shells, if the output to be seen there is generated
 *  mainly by this object or application.<br />
 *  <br />
 *  The returned text is not longer than 78 characters and has no 
 *  surrounding whitespace.<br />
 *  <br />
 *  If not available or applicable, null is returned.<br />
 *  @return the (window) title
 */
   public String getTitle();


/** Get a one line usage hint. <br />
 *  <br />
 *  The returned value is a little one line usage hint for this object or 
 *  application, like: <br /> &nbsp; &nbsp;
  java de.frame4j.time.TimeHelper [&quot;time to parse&quot; [&quot;time..<br />
 *  <br />
 *  The returned text should not be longer than 78 characters and has no 
 *  surrounding whitespace.<br />
 *  <br />
 *  If not available or applicable, null is returned.<br />
 *  <br />
 *  The returned value is to be used as default for / by  
 *  {@link #getHelp()}.<br /> 
 *  <br />
 *  @return a short hint text on the application's usage
 */
   public String getUsage();


/** Get a comprehensive help text. <br />
 *  <br />
 *  The returned value should on the effect and the usage of the object or 
 *  application from the sight of the user, using it by the provided interface
 *  at the command line or how ever applicable. The text may be in  English 
 *  or the author's language, but preferable nationalised to a set (by 
 *  command) r the platform language.<br />
 *  <br />
 *  The returned text may be multi-line up to one page, with no line length 
 *  longer than 78 characters and with no (0) preceding or trailing empty
 *  lines.<br />
 *  <br />
 *  Any class / object that can be handled by a user interface should explain
 *  its usage, its effect, the handling the options, (command line) parameters
 *  and their effect in concise way.<br />
 *  <br />
 *  If no such description can be made available, {@link #getUsage()} is
 *  returned.<br />
 *  Only if really not applicable, null is returned. (But then the question
 *  is suggesting: Why the the hell did the programmer implement this 
 *  interface anyway?) <br />  
 *  <br />
 *  @return the application's comprehensive help text   
 */
   public default String getHelp(){ return getUsage(); }
   

/** Get a copyright line. <br />
 *  <br />
 *  The returned value is just one line in this exemplary form:<br /> &nbsp;
 *   Copyright 1997 - 2009, 2016  Albrecht Weinert<br />
 *  <br />
 *  The returned text is not be longer than 78 characters, has no 
 *  surrounding whitespace and is never null.<br />
 *  <br />
 *  @return the application's copyright text   
 */
   public String getCopyright();

/** Get a version line. <br />
 *  <br />
 *  The returned value is just one line in this exemplary form:<br /> &nbsp;
 *   V.59 (09.04.2008)<br />
 *  containing a version info plus its date.<br />
 *  <br />
 *  The content is usually generated by letting a versioning system (SVN)
 *  making its repugnantly formated &quot;&#39;Revision: 55 &#39;&quot; and
 *  &quot;&#39;Date: 2009-01-23 18:04:19 +0100 (Fr, 23 Jan 2009) &#39;&quot;
 *  and having it beautified by {@link de.frame4j.SVNkeys SVNkeys}
 *  in deployment script; see also {@link MinDoc}.<br />
 *  <br />
 *  The (beautified) returned text is not be longer than 78 characters, 
 *  has no surrounding whitespace and is never null.<br />
 *  <br /> 
 *  @return the application's version and revision date   
 */
   public String getVersDate();

/** Get the name. <br />
 *  <br />
 *  The returned value is a simple short name, 
 *  has no surrounding whitespace and is never null.<br />
 *  <br />
 *  It is either a given non empty name or the class name without 
 *  package(s).<br />
 *  @return the application's short name  
 */
   public String getName();
   
/* * Just test and play * /
   public default Class<? extends UIInfo> getTheClass(){
      return this.getClass();
   }  */
 
} // interface UIInfo (20.01.2009, 08.06.2015)