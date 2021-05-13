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

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.frame4j.io.FileHelper;
import de.frame4j.io.FileService;
import de.frame4j.io.FileVisitor;
import de.frame4j.io.Input;
import de.frame4j.net.AttrSettable;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.util.Prop;
import de.frame4j.text.TextHelper;
import de.frame4j.time.AClock;
import de.frame4j.time.SynClock;
import de.frame4j.time.TimeHelper;

/** <b>Search files, sort and list; search doublets</b>. <br />
 *  <br />
 *  This application searches files and directories and detects doublets 
 *  visiting one or more base directories and optionally their
 *  sub-directories. The selection criteria may be quite complex:<br />
 *  &nbsp; &nbsp;  &nbsp; &nbsp; 
 java FS \source\java;html;xml -omitDirs CVS;.svn;.settings -since 1.3.03 doc\html;xml -r<br />
 *  <br />
 *  Criteria and options set (-omitDirs, -since and -r in above example) are 
 *  valid for all files respectively directories no, matter what the parameter 
 *  sequence &quot;and mix  &quot; is. At the end the tool lists all matching
 *  files sorted by file names.<br />
 *  <br />
 *  A search for file doublets is available too. This is a good first step for
 *  automated or hand crafted clean up campaigns. For any two files to be 
 *  considered as equal by this tools at least two conditions must be 
 *  met:<ul> 
 *  <li>the names (of course without the parent paths)  &nbsp;and</li>
 *  <li>the file sizes must be equal.</li>
 *  </ul>
 *  Additionally any combination of the following criteria can be set
 *  as further equality condition<ul>
 *  <li>the modification dates must be the same (default on).</li>
 *  <li>the content (byte by byte) must be equal (default off).</li>
 *  </ul>
 *  When generating a list of doublets (of files equal according to the 
 *  chosen set of criteria) one file of every doublets group can be commented
 *  out on the list by option -keep. An (middle clever) algorithm tries do
 *  do as much &quot;keeps&quot; as possible in the same directories. If
 *  you feed the list generated this way even unmodified to the delete tool
 *  <a href="Era.html">Era</a> would keep exactly one file out of every 
 *  doublet group and it would empty directories containing only
 *  doublets with significant probability (as one normally would like on
 *  clean up).<br />
 *  <br />
 *  <b>SVN repair</b><br />
 *  <br />
 *  Another feature is the &quot;Subversion modified date bug&quot; repair 
 *  mode switched on by option -svnRepair. Hereby the &quot;listing&quot; is
 *  generated additionally as a XML file containing an ANT task to restore
 *  the dates forgotten if (and only if) no further changes and commits were
 *  done to the file added.<br />
 *  The option -svnRepair sets the following properties:<br />
 *  pureName=false ## antTime=true ## equalsOnly = false ## relateToDir = true
 *  ## svnRepair = true ## omitDirs = .svn ## recursion=true<br />
 *  The behaviour of the repair mode is further governed by the following 
 *  properties (listed with their default values):<code><pre>
 *  &nbsp; targetName = repairDates4SVN
 *  &nbsp; beforeDateName = firstCommit
 *  &nbsp; buildFileName = build.xml
 *  &nbsp; projectName = repair SVN's file modification time bug
 *  &nbsp; antCompareTimeFormat = m/d/Y h:i a
 &nbsp; afterFirstCeckInTime  (null defaults to 10 minutes after generation)
 *  &nbsp; antXMLencoding = ISO-8859-1
 *  </pre></code>
 *  The property {@code afterFirstCeckInTime} is the criterion for changes and
 *  further check ins. For those (only) the modification time delivered by SVN
 *  can be considered as non-nonsense. This property may be set by the option
 <br /> &nbsp; {@code -addTime &quot;yesterday noon&quot;}  &nbsp; (e.g.)<br />
 *  in any form understood by Frame4J's time parsers.<br />
 *  <br />
 *  A more canonical way to remedy SVN's modification date bug is to save 
 *  (pre-commit, local) and restore (post-update, local) the last modified
 *  date as (SVN-) property. Save example:<code><pre> 
 *  &nbsp; java FS -relateToDir -omitDirs .svn;build  -antTime |\
 *  &nbsp;  &nbsp;  grep "m " | \
 *  &nbsp;  gawk '{print "svn propset mtime \"" $1 " " $2 " " $3 " \" "  $5 }'\
 *  &nbsp;  &nbsp;  &nbsp; &gt; saveTheDate.bat
 *  &nbsp; call saveTheDate.bat
 *  </pre></code>  Restore example:<code><pre> 
 *  &nbsp; svn propget mtime -R | \
 *  &nbsp;   gawk '{print "touch -m --date=\"" $3 " " $4 " " $5 "\" "  $1 }'\
 *  &nbsp;  &nbsp;  &nbsp; &gt; rescueTheDate.bat
 *  &nbsp; call  rescueTheDate.bat
 *  </pre></code>  
 *  <br />
 *  <b>Common examples</b><br />
 *  <br />
 *  <code>java FS .\+.class -r</code><br />
 *  &#160; &#160; lists all files *.class in the actual directory and all
 *  its subdirectories.<br />
 *  <code><br />java FS C: -omitDirs WINNT -r</code><br />
 *  &#160; &#160; lists all files on drive C: in including all subdirectories
 *  (-r) with the exception of directories named WINNT (and its subs).<br />
 *  <code><br />java FS D:\weinert\+.doc  -e</code><br />
 *  &#160; &#160; lists all doublets (equal option -e) .doc files in the 
 *  directory named (and of course subdirectories).<br />
 *  <code><br />java FS +.java;+.asm -oneDay -d -r</code><br />
 *  &#160; &#160; lists all .java and .asm source files in the actual 
 *  directory and its subdirectories modified within the last 24 hours.<br />
 *  <code><br />java FS .\+.class P:\+.class -since 
 *                10.06.09 -til 12.06.09_13:00 </code><br />
 *  &#160; &#160; lists all files *.class in the actual directory and in drive
 *  P's root modified between 10th on June 2009 00:00 and 12th of June 13:00.
 *  (13 = 1_PM for those who love ambiguous time of day indications).<br />
 *  <br />
 *  <br />
 *  <b>Hint</b>: To this application FS belongs (as an integral part) 
 *  a .properties file named  
 *  <a href="./doc-files/FS.properties" target="_top">FS.properties</a>.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a> 
 *  Copyright 1997 - 2003, 2005, 2009 &nbsp; Albrecht Weinert <br />
 *  <br /> 
 *  @see      App
 *  @see      Prop
 *  @see      FileHelper
 */
 // so far    V00.00 (28.04.1998) :  new from DateiTest
 //           V00.09 (29.03.1999) :  changes in Datei.Criteria
 //           V00.12 (13.12.2000) :  since, til, multiple Directories
 //           V01.00 (04.07.2001) :  Type lists, dateCompare; quoteSpace
 //           V01.10 (06.12.2001) :  any # of dirs, contentCompare
 //           V01.20 (05.05.2002) :  de
 //           V.160  (25.10.2009) :  SVN date bug repair mode
 //           V.105  (27.08.2014) :  ant time bug repaired
 //           V.133+ (06.01.2016) :  FileHelper
 //           V.163+ (05.08.2016) :  refactored to Frame4J'89 slimline
@MinDoc(
   copyright = "Copyright 1997 - 2003, 2014, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 44 $",
   lastModified   = "$Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $",
   usage   = "start as Java application (-? for help)",  
   purpose = "search files, sort and list; search doublets"
) public class FS extends App {

/** FS wants just only partial parameter parsing by Prop. */
   @Override public boolean parsePartial(){ return true; }

/** Start method of FS. <br />
 *  <br />
 *  Return-Code:<br />
 *  &nbsp; Exit = 0 on success, <br />
 *  &nbsp; Exit &gt; 0  on (file) problems,<br />
 *  &nbsp; Exit &gt; 90 on wrong parameters.<br />
 *  <br /> 
 *  @param args command line parameter
 */
   public static void main(String[] args){
      try {
         new FS().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

   
   // Hint omitting this method would have no effect except for start-up
   // performance. App and PropMap would do the same job using 
   // introspection.
   @Override protected int setAttribute(final String name, final char name0,
                           final Object value, final Class<?> vClass,
                           final boolean isNull, final boolean isStringVal){
      int supRet = super.setAttribute(name, name0, 
                                        value, vClass, isNull, isStringVal);
      if (supRet != NO_KNOWN_ATTRIBUTE) return supRet;
      if (name0 == 'a') {
         if (name.equals("antXMLencoding")) { // ISO-8859-1
            if (! ( isNull || isStringVal)) return AttrSettable.ILLEGAL_TYPE;
            this.antXMLencoding = 
               TextHelper.trimUq((String)value, "ISO-8859-1");
            return AttrSettable.OK;
        } // antXMLencoding       

         if (name.equals("antCompareTimeFormat")) { // m/d/Y h:i a
            if (! ( isNull || isStringVal)) return AttrSettable.ILLEGAL_TYPE;
            this.antCompareTimeFormat = 
               TextHelper.trimUq((String)value, "m/d/Y h:i a");
            return AttrSettable.OK;
        } // antCompareTimeFormat       
 
        if (name.equals("afterFirstCeckInTime")) { // null
            if (! ( isNull || isStringVal)) return AttrSettable.ILLEGAL_TYPE;
            this.afterFirstCeckInTime = 
               TextHelper.trimUq((String)value, null);
            return AttrSettable.OK;
        } // afterFirstCeckInTime   
        
        if (name.equals("antTime")) {
           Boolean vb = TextHelper.asBoolObj(value);
           if (vb != null) this.antTime = vb.booleanValue();
           return AttrSettable.OK;
        } // antTime

      } // a   
         
      if (name0 == 'b') {
         if (name.equals("beforeDateName")) { // firstCommit
            if (! ( isNull || isStringVal)) return AttrSettable.ILLEGAL_TYPE;
            this.beforeDateName = 
               TextHelper.trimUq((String)value, "firstCommit");
            return AttrSettable.OK;
        } // beforeDateName                       
         
         if (name.equals("buildFileName")) { // def build.xml
            if (! ( isNull || isStringVal)) return AttrSettable.ILLEGAL_TYPE;
            this.buildFileName = 
               TextHelper.trimUq((String)value, "build.xml");
            return AttrSettable.OK;
        } // buildFileName       
         
         if (name.equals("buildFileDir")) { // def null
            if (! ( isNull || isStringVal)) return AttrSettable.ILLEGAL_TYPE;
            this.buildFileName =  TextHelper.trimUq((String)value, null);
            return AttrSettable.OK;
        } // buildFileDir                       
         
      } // b
      
      if (name0 == 'c') {
         if (name.equals("contentCompare")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.contentCompare = vb.booleanValue();
            return AttrSettable.OK;
         } // contentCompare 
      } // c

      if (name0 == 'd') {
         if (name.equals("directory")) {
            if (isNull) {
               this.directory = null;
               return AttrSettable.OK;
            }
            if (!isStringVal) return AttrSettable.ILLEGAL_TYPE;
            this.directory = (String)value;
            return AttrSettable.OK;
        } // directory       
         
         if (name.equals("dateCompare")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.dateCompare = vb.booleanValue();
            return AttrSettable.OK;
         } // dateCompare
         if (name.equals("doSort")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.doSort = vb.booleanValue();
            return AttrSettable.OK;
         } // doSort
      } // d

      if (name0 == 'e') {
         if (name.equals("equalsOnly")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.equalsOnly = vb.booleanValue();
            return AttrSettable.OK;
         } // equalsOnly 
      } // e
      
      if (name0 == 'k') {
         if (name.equals("keepOne")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.keepOne = vb.booleanValue();
            return AttrSettable.OK;
         } // keepOne 
      } // k
      
      if (name0 == 'l') {
         if (name.equals("listDirs")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.listDirs = vb.booleanValue();
            return AttrSettable.OK;
         } // listDirs
      } // l
      
      if (name0 == 'p') {
         if (name.equals("projectName")) { // .......
            if (! ( isNull || isStringVal)) return AttrSettable.ILLEGAL_TYPE;
            this.projectName = 
               TextHelper.trimUq((String)value,
                                  "repair SVN's file modification time bug");
            return AttrSettable.OK;
        } // projectName  
      } // p

      if (name0 == 'q') {
         if (name.equals("quoteSpace")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.quoteSpace = vb.booleanValue();
            return AttrSettable.OK;
         } // quoteSpace
      } // q

      if (name0 == 'r') {
         if (name.equals("relateTo")) {
            if (isNull) {
               this.relateTo = null;
               return AttrSettable.OK;
            }
            if (!isStringVal) return AttrSettable.ILLEGAL_TYPE;
            this.relateTo = (String)value;
            return AttrSettable.OK;
        } // directory    
         
         if (name.equals("recursion")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.recursion = vb.booleanValue();
            return AttrSettable.OK;
         } // recursion
         
         if (name.equals("relateToDir")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.relateToDir = vb.booleanValue();
            return AttrSettable.OK;
         } // relateToDir

      } // r
      
      if (name0 == 's') {
         if (name.equals("svnRepair")) {
            Boolean vb = TextHelper.asBoolObj(value);
            if (vb != null) this.svnRepair = vb.booleanValue();
            return AttrSettable.OK;
         } // svnRepair
      } // s
      
      if (name0 == 't') {
         if (name.equals("targetName")) { // repairDates4SVN
            if (! ( isNull || isStringVal)) return AttrSettable.ILLEGAL_TYPE;
            this.targetName = 
               TextHelper.trimUq((String)value, "repairDates4SVN");
            return AttrSettable.OK;
        } // targetName 
      } // t
      
      return NO_KNOWN_ATTRIBUTE; 
  } // setAttribute(String, char, Object, Class<?>, 2*boolean)

//--------------------------------------------------------------------   
   
/** First directory to be listed. <br />
 *  <br />
 *  Appended to the directory path can be file denotations that may use 
 *  wildcards or type lists:<br />
 *  <code> &nbsp; &nbsp; ..\tmp\+.j+  (+ used instead of *)  
 *   &nbsp; &nbsp;  ..\tmp\class;bak;tmp;obj;map</code><br />
 *  <br />
 *  There may be multiple parameters of that form in the start parameters
 *  then worked on sequentially.<br />
 *  <br />
 *  default: &quot;.\+.+&quot;; without specifying anything &quot;all in the
 *          actual directory&quot; (see {@link ComVar#UD}) is assumed.
 */
   public String directory;

/** List also sub-directories (like files). <br />
 *  <br />
 *  If true sub-directories matching the file [sic!] criteria are put in the 
 *  file list.<br />
 *  <br />
 *  Don't intermix this with recursive ({@link #recursion}) descend to 
 *  sub-directories and the with directory criteria used to select the 
 *  sub-directories to be visited.<br />
 *  <br />
 *  default: false
 */ 
   public boolean listDirs;

/** List also files in sub-directories. <br />
 *  <br />
 *  If true sub-directories matching the directory criteria will be visited
 *  and their files (matching the file criteria) will be listed 
 *  (and later worked on, sorting, doublet search or what else).<br />
 *  <br />
 *  default: false
 */ 
   public boolean recursion;

/** Regard only files with doublets. <br />
 *  <br />
 *  If true, after collecting (and sorting) all files will be removed from
 *  the file list, that do not have at least one other equal file (in 
 *  another directory).<br />
 *  <br />
 *  If this property is true and only one (base) directory is specified
 *  {@link #recursion} will be set true.<br />
 *  <br />
 *  If {@link #svnRepair} is true and more than one directory is specified
 *  this property {@link #equalsOnly} will be set true. ANT touch entries will
 *  only generated (relative to and) only for files of the first directory,
 *  if that file (same name and size) has a sibling in another directory. The
 *  oldest date of all those equal files will be taken as the set date.<br />
 *  <br />
 *  default: false
 */ 
   public boolean equalsOnly;

/** Use / list quotes for filenames with spaces. <br />
 *  <br />
 *  If this property is true files containing spaces within their (full) 
 *  name will have that in quotes on the list output.<br />
 *  <br />
 *  default: true (makes sense as Windows file system allows spaces in names)
 */ 
   public boolean quoteSpace = true;

/** Regard also the date as criterion for file equality. <br />
 *  <br />
 *  If true only files with the same last modification time (file system's 
 *  time stamp) may be considered as equal. The default is true.<br />
 *  If files on different computers or ones copied to removable media are
 *  considered false is often the better choice (option -ndc).<br />
 *  <br />
 *  default: true<br />
 *  <br />
 *  @see #equalsOnly
 *  @see #svnRepair
 */ 
   public boolean dateCompare = true;

/** Regard also the content as criterion for file equality. <br />
 *  <br />
 *  If true only files with the same content in all bytes may be considered 
 *  as equal. This implies {@link #dateCompare} = false.<br />
 *  <br />
 *  default: false
 */ 
   public boolean contentCompare;

/** Comment out one file out of every group of doublets. <br />
 *  <br />
 *  If true one file out of every doublet group is commented out on the 
 *  file list (using  /// ).<br />
 *  <br />
 *  The list can be fed directly / unmodified to the tool 
 *  <a href="Era.html">Era</a> to have the maximum clean up effect without 
 *  loosing any content.<br />
 *  <br />
 *  default: false
 */ 
   public boolean keepOne;

/** Omit file attributes from listing. <br />
 *  <br />
 *  If true only the name appears on the file list output, no length, no date
 *  or else.<br />
 *  <br />
 *  default: false
 */ 
   public boolean pureName;

/** Make filenames relative to this directory specification. <br />
 *  <br />
 *  This property must either be null or denote an existing directory and
 *  end with {@link ComVar#FS} (to make the kind clear).<br />
 *  <br />
 *  If set all full file names will be shortened by that prefix, leaving
 *  a file denotation relative to the directory {@link #relateTo}.<br />
 *  <br />
 *  default: null, but set to first specified directory in case of 
 *  {@link #relateToDir} is true.<br />
 */ 
   public String relateTo;

/** Make filenames relative to (first) directory to be listed. <br />
 *  <br />
 *  If true {@link #relateTo} will be set to {@link #directory}.<br />
 *  <br />
 *  No effect in case of doublets search etc.<br />
 *  <br />
 *  default: false<br />
 *
 *  @see #svnRepair
 */ 
   public boolean relateToDir;
   
/** No file attributes in listing except the time in ugly ANT format.<br />
 *  <br />
 *  If true only the name (without length and else) will be output, preceded
 *  (yes in front!) by the time in the disgusting time format ANT's 
 *  touch task requires.<br />
 *  <br />
 *  Implies {@link #pureName} in the said sense and sets {@link #quoteSpace}
 *  false.<br />
 *  <br />
 *  default: false <br />
 *  <br />
 *  @see de.frame4j.time.TimeHelper#format(CharSequence, java.time.ZonedDateTime)
 */  
   public boolean antTime;

/** Sort listing by names. <br />
 *  <br />
 *  If true the list is sorted by (pure) file names. That is default and
 *  (forced) precondition for doublets search etc.<br />
 *  <br />
 *  default: true.
 */ 
   public boolean doSort = true;
 
/** Repair the SVN bug to forget file modification time on add. <br />
 *  <br />
 *  If true the &quot;listing&quot; format is an ANT task to repair the 
 *  (very) hard SVN bug to set the file dates of a file added to the commit
 *  time.<br />
 *  <br />  
 *  default: false<br />
 *
 *  @see #equalsOnly  
 */
   public boolean svnRepair;

/** Name of the generated ANT task. <br />
 *  <br />
 *  default: repairDates4SVN
 *  @see #svnRepair 
 *  @see #projectName  
 */
   public String targetName = "repairDates4SVN";
   
/** Name of the time property when to repair file dates. <br />
 *  <br />
 *  The value has to be in the ugly American ANT time format without 
 *  seconds. <br />
 *  <br />
 *  default: firstCommit <br />
 *  @see #svnRepair  
 *  @see #antCompareTimeFormat 
 */
   public String beforeDateName = "firstCommit";    
   
/** Name of the output file. <br />
 *  <br />
 *  default: build.xml <br />
 *  @see #svnRepair  
 *  @see #projectName
 *  @see #antXMLencoding
 *  @see #buildFileDir
 */
   public String buildFileName = "build.xml";
 
/** Directory of the output (build) file. <br />
 *  <br />
 *  default: null (means use the first directory scanned and related to)<br />
 *  @see #svnRepair  
 *  @see #buildFileName
 */
   public String buildFileDir;

/** Encoding of the output file. <br />
 *  <br />
 *  default: ISO-8859-1 <br />
 *  @see #svnRepair  
 *  @see #buildFileName
 */
   public String antXMLencoding = "ISO-8859-1";

/** Name of the SVN bug repair project. <br />
 *  <br />
 *  @see #svnRepair  
 *  @see #targetName
 */
   public String projectName = "repair SVN's file modification time bug";

/** Format of the compare time. <br />
 *  <br />
 *  default: m/d/Y h:i a   <br />
 *  @see #svnRepair  
 *  @see #beforeDateName
 *  @see #afterFirstCeckInTime
 *  @see de.frame4j.time.TimeHelper#format(CharSequence, java.time.ZonedDateTime)
 */
   public String antCompareTimeFormat = "m/d/Y h:i a";

/** The compare time. <br />
 *  <br />
 *  default: null (will be made 10 minutes after run time) <br />
 *  @see #svnRepair  
 *  @see #beforeDateName
 *  @see #antCompareTimeFormat
 */
   public String afterFirstCeckInTime;
   
//----------------------------------------------------------------

/** Working method of FS. */
   @Override public int doIt(){
      log.println();
      if (isNormal()) log.println(twoLineStartMsg().append('\n'));

      FileService dS = new FileService();
      try {
         dS.set(prop);
      } catch (IllegalArgumentException iae) {
         return errMeld(25, iae);
      }

      int dCnt = 0;
      String[] dirs = null;
      if (directory == null) { // too few parameters no path
         directory = "."; // default is then the actual dir
         if (args.length == 0) {
            dCnt = 1;
            dirs = new String[]{directory};
         }
      }  //no path parameter 

      if (dirs == null) dirs = new  String[args.length];
      
      // collect the specified directories 
      for (int i = 0; i < args.length; ++i) {
         if (i != 0) {  // first word param is as word-0 already in directory
            directory = args[i];
         }
         directory = TextHelper.makeFNL(directory);
         if (directory == null) continue;
         if (buildFileDir == null) buildFileDir = directory;
         dirs[dCnt] = directory;
         ++dCnt;
      } // for dir prepare the directory list
      
      if (equalsOnly && dCnt == 1) recursion = true;
      
      String antCompTime = null;
      
      if(svnRepair) {
         long svnAddTime = 0L;
         afterFirstCeckInTime = TextHelper.trimUq(afterFirstCeckInTime, null);
         if (afterFirstCeckInTime != null) try {
            svnAddTime = TimeHelper.parse(afterFirstCeckInTime);
         } catch (Exception e) {
            return errMeld(35, e);
         }
         if (svnAddTime == 0L) {
            svnAddTime = SynClock.sys.setActTimeMs() + ComVar.M * 10;
         } // 10 minutes after now
         
         antCompTime = TimeHelper.format(antCompareTimeFormat, svnAddTime);
         if (verbose) {
            log.println ("Making ANT task to repair SVN file time bug\n"
                            + "SVN add time = "  
                            + TimeHelper.formatDIN(svnAddTime) + " / ANT ugly: "
                            + antCompTime );
         }
         if (dCnt != 1) {
            equalsOnly = doSort = true;
            if (verbose) {
              log.println("SVN repair for dir " + dirs[0]
                           + " and files contained in other directories");  
            }
         }  
         keepOne = false;
      }  // svnRepair
      
 
      dateCompare &= !(contentCompare || svnRepair);
      dS.setRecursion(recursion);
      dS.filCrit.setAllowDir(listDirs);

      if (isTest())  log.println(dS);
         
//---   End of parameter evaluation          -----------------------

      Collection<File>    dV  =
                             new ArrayList<>((recursion ? 90 : 25 )* dCnt);
      FileVisitor intoVecVisitor = FileService.makeInCollectionVisitor(dV,
                              equalsOnly || dCnt > 1); 

//---   Directory evaluation  and visits      -----------------------

      // Phase 1: visit all specified dirs to collect all matching files 
      for (int i = 0; i < dCnt; ++i) {
         directory = dirs[i];
         if (!(directory.endsWith(File.separator))) 
            directory = dS.filCrit.parse(directory);

         if (i == 0 && relateToDir) { // dir 0 (word 0)
            relateTo = directory;
            if (directory.charAt(0) == '.') 
               relateTo = ComVar.UD + ComVar.FS;
            if (isTest()) {
               err.println("relateTo = " + relateTo);
            }
         } // dir 0 (word 0)
         
         if (isDebug()) {
            err.println(directory + " < > " + dS.filCrit.getWildName()); 
            err.println("Start DirVisiter " + directory
                                  + (recursion ? " + subdirs " : ""));                  
            err.flush();
         } // TEST */
         dS.dirVisit(directory, null, 
                               intoVecVisitor,     // File visitor
                               null, null, null); // no directory actions here
      } // for  Dirs

      
      int noOfFi = dV.size();
      if (dV.isEmpty() || equalsOnly && noOfFi <= 1) { // nothing found & do
         if (verbose)
            log.println(valueLang("nofilfnd"));
          return 0;
      }  // nothing found means nothing to do at all

   //---  the non empty file list is in dV

      File[] dA = dV.toArray(FileHelper.NO_FileX);  //  now in one Array 
      dV = null; 
      if (doSort || equalsOnly ) Arrays.sort(dA, FileHelper.comparator);
       
   //--- here non empty file list is [sorted] in the array dA

      File    xmFi = null;
      FileHelper.OS xmOS = null;
      PrintWriter xmPw = null;
      String antCompProp = null;
            
      if(svnRepair) {
         xmFi =  FileHelper.getInstance(buildFileDir, buildFileName);
         Charset antXMLcs = Charset.forName(antXMLencoding);
         xmOS =  FileHelper.makeOS(xmFi, outMode, antXMLcs);
         if (xmOS != null) xmPw = xmOS.pw;
         if (verbose || xmPw == null) {
            log.println("ANT-repair file: " + buildFileName
                                    + " ; encoding: " + antXMLencoding);
            if (xmPw == null) {
               return errMeld(39, "xml output file failed");
            }
         }
         
         if (!xmOS.append) {
            xmPw.println("<?xml version=\"1.0\" encoding=\"" 
                                                    + antXMLencoding +"\"?>");
      
            xmPw.println("<project name=\"" + projectName
                   + "\"\n     default=\"" + targetName + "\" basedir=\".\">");
         }
         xmPw.println(
            "<!-- Conditional touches to repair the SVN forget mTime bug\n"
          + "     Generated at " + TimeHelper.formatDIN(SynClock.sys.millis())
        + "\n     by de.frame4j.FS  V.$Revision: 44 $  ("
                  + "$Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $)\n"
          + "     Copyright (c) 2009  A. Weinert  (a-weinert.de, frame4j.de)\n" 
     + "-->\n  <target name=\"" + targetName + "\">");
         
         xmPw.println(
              "    <property name=\"" + beforeDateName + "\" value=\"" 
                                 + antCompTime + "\"/>\n");
         antCompProp = "${" + beforeDateName + "}";
          
      } // SVN  repair (preparation)
 
      // ModifTime helpZt =   TimeHelper.helpTimeHolder.get();
      // if (antTime) helpZt.setTimeZone(TimeZone.getTimeZone("GMT"), false);

      if (!equalsOnly) { // simple case w/o doublets search
         if (verbose)
            log.println(formMessage("filsfnd ", noOfFi));
              ///"\n ///   " + noOfF + " files found.\n");
         int rltL =  relateTo == null ? 0 : relateTo.length();
         
         if (rltL  < 2 || relateTo.charAt(rltL -1) != ComVar.FS) {
            rltL =  0;
            relateTo = null;
         }
         if (antTime || rltL != 0) quoteSpace = false;
         
         
         for (int i = 0 ;  i < noOfFi; ++i) { 
            ///log.print('+');  // TeeWriter-Test // delete!
            if (pureName || antTime) {
               
               final StringBuilder bastel =
                FileHelper.pathName(dA[i], null, false, quoteSpace, relateTo);
               if (antTime) {
                  final long modL = dA[i].lastModified();
                  // format ANT as 12/28/2001 10:43:59 am
                  final String uglyAntTime = TimeHelper.format(
                                     "m/d/Y h:i:s a", modL, AClock.UTC_ZONE);
                  if (svnRepair) { // make the file entry for SVN bug
                     final StringBuilder bastel3 = 
                            touchTask(null, bastel, uglyAntTime, antCompProp);
                      xmPw.println(bastel3.toString());
                  } // make the file entry for SVN bug repair touches 
                  final StringBuilder bastel2 = new StringBuilder(56);
                  bastel2.append(' ').append(dA[i].length() );
                  while(bastel2.length() < 10) bastel2.insert(0, ' '); 
                  bastel2.insert(0, uglyAntTime);
                  bastel2.append("  ");
                  bastel.insert(0, bastel2);
               }
               
               log.println(bastel.toString());
               
            } else { // pureName || antTime
               log.println(FileHelper.infoLine(dA[i], null, false, quoteSpace,
                                    relateTo));
            } // else pureName || antTime
         } // for  (over the files, cases w/o non equality treatment
         
         if(svnRepair) {
            xmPw.println("  </target>");
            if (!xmOS.append)  xmPw.println("</project>\n");
            xmPw.close();
         }       

         return 0;
      } //  simple cases w/o doublets search

   //--- to here only in case of doublets search (in Array  dA, length > 1)
      int eNo        = 0;
      int dGrStart   = -1;
      int doubGroups = 0;
      int doubFiles  = 0;
      int[] isDoub   = new int[noOfFi];

   //--- register doublets in isDoub

      File lastD = dA[0];
      File aktD  = null;
      byte[] lastContent = null;
      byte[] aktContent  = null;
      for (int i = 1 ;  i < noOfFi; ++i) {  // search for doublet groups
         aktD = dA[i];
         long len = aktD.length();
         boolean eqToLast = FileHelper.sameName(lastD, aktD)
            && (lastD.length() == len)
            && (!dateCompare 
               || lastD.lastModified() == aktD.lastModified());
         while (eqToLast && contentCompare) { // equal except for ? content
            if (eNo == 0) try { // start for doublets search found
               Input ein = new Input(lastD);
               lastContent = new byte[(int)len];
               if (ein.readStartOfContent(lastContent) != len)
                  lastContent = null;
               ein.close();
            } catch (Exception ex) { lastContent = null; }
            if (lastContent == null) {
               eqToLast = false;
               break;
            }
            try {
               Input ein = new Input(aktD);
               aktContent = new byte[(int)len];
               if (ein.readStartOfContent(aktContent) != len)
                  aktContent = null;
               ein.close();
            } catch (Exception ex) { aktContent = null; }
           
            eqToLast = Arrays.equals(aktContent, lastContent);
            break;
         } // equal except for ? content

         if (eqToLast) { // equal
            if (eNo == 0) {  // start of doublet group found
               ++doubGroups;
               dGrStart = i-1;
               isDoub[dGrStart] = 1;
               eNo = 1;
               ++doubFiles;
            }
            isDoub[i] = ++eNo;
            ++doubFiles;
         } else { // not equal
            eNo = 0;
            lastD = aktD;
         } // not equal
      }  // search for doublet groups

      if (verbose)
        log.println(formMessage("fssumry", 
                                 new int[] {noOfFi, doubFiles, doubGroups}));
       //  "\n ///   xXx  files found," + 
       //     "\n ///   thereby yYy  doublets in zZz groups.\n");
       
      int[] votes = keepOne ? new int[noOfFi] : null;
      long repairTime = 0L;
      String uglyAntTime = null;
      
      list: for (int i = 0;  i < noOfFi; ++i) {
         eNo = isDoub[i];
         if (eNo == 0) continue list; // no doublet

         if (eNo == 1 && keepOne) { // start of doublet group: voting
            log.println(" ");
            int groupInd = i; 
            voting1: do {
               aktD = dA[groupInd];
               voting2:  for (int j = groupInd+1; j < noOfFi; ++j) {
                  if  (isDoub[j] == 0) continue voting2;
                  lastD = dA[j];
                  if (FileHelper.sameDir(aktD, lastD)) {
                      ++votes[groupInd];
                      ++votes[j];
                  }
               }             
               ++groupInd;
            } while (groupInd < noOfFi && isDoub[groupInd] > 1); 

            int maxVoteInd = i;
            int maxVote = votes[maxVoteInd]; 
            voting3: for (groupInd = i+1; 
                       groupInd < noOfFi && isDoub[groupInd] > 1;
                                                       ++groupInd) {
               int tmp = votes[groupInd]; 
               if (tmp > maxVote) {
                  maxVote = tmp;
                  maxVoteInd = groupInd;
               }
             } // voting3
             if (maxVoteInd == i) 
                eNo = -1;
             else
                isDoub[maxVoteInd] = -isDoub[maxVoteInd]; 

         } // start of doublet group:  voting

         aktD = dA[i];

         if (svnRepair) {
            if (eNo == 1) { // start of doublet group: svnRepair 
               repairTime = aktD.lastModified();
               if (isDebug()) {
                  log.println("\nSVNPRepair doublet min time search \n // "
                                                 + FileHelper.infoLine(aktD));
                  log.println(" // t[" + i + "] \t = " + repairTime 
                                  + " = " + TimeHelper.formatDIN(repairTime));
               }
               
               timeMinSrch: for (int j = i+1; j < noOfFi; ++j) {
                  if (isDoub[j] < 1) break timeMinSrch;
                  final long modT = dA[j].lastModified();
                  if (isDebug()) {
                     log.println(" // t[" + j + "] \t = " + modT 
                                        + " = " + TimeHelper.formatDIN(modT));
                  }
                  if (modT < repairTime) repairTime = modT;
               } //  timeMinSrch
               // format ANT as 12/28/2001 10:43:59 am
              uglyAntTime = TimeHelper.format("m/d/Y h:i:s a", repairTime, 
                                                             AClock.UTC_ZONE); 
            }  // start of doublet group: svnPrepair
            
            String parent = aktD.getParent();
            boolean takeIt = false;
            if (relateTo != null && parent != null) {
               if (!parent.endsWith(File.separator))
                  parent = parent + File.separator;   // FileHelper.sortGK ??
               takeIt = TextHelper.startsWith(parent, relateTo, false );
            }
            
            if (takeIt) {
               final StringBuilder bastel =
                 FileHelper.pathName(aktD, null, false, quoteSpace, relateTo);
               final StringBuilder bastel3 = 
                      touchTask(null, bastel, uglyAntTime, antCompProp);
               xmPw.println(bastel3.toString());
               if (isTest()) {
                  log.println(" // t[" + i + "] \t " + bastel + " << " 
                                          +   relateTo);
               }
            }
            
         } // svnRepair

         if (eNo == 1 || eNo == -1) log.println(" ");
         StringBuilder b = new StringBuilder(100);
         if (eNo < 0) b.append(" /// ");  /// outvoted 
         if (antTime) {
            b.append(TimeHelper.format("m/d/Y h:i:s a", aktD.lastModified(),
                                              AClock.UTC_ZONE)).append("  "); 
            FileHelper.pathName(aktD, b, false, quoteSpace, relateTo);
         } else {
            FileHelper.infoLine(aktD, b, false, quoteSpace, relateTo);
            if (eNo > 0) b.append(' ').append(eNo);
         }
         log.println(b);
      } // list it
      
      if(svnRepair) {
         xmPw.println("  </target>");
         if (!xmOS.append)  xmPw.println("</project>\n");
         xmPw.close();
      }       
      
      return 0; 
   } //  doIt        

/** Make an ANT touch task, optionally conditional. <br />
 *  <br />
 *  @param bastel the StringBuilder to append to, if null one is made
 *  @param fileName the name / designation of the file to touch
 *  @param uglyAntTime the time to set to in ANT / American format
 *  @param antCompTime the optional compare time (same ugly w/o seconds)<br />
 *    &nbsp; &nbsp; null means unconditional touch / time set
 *  @return bastel
 */
   static public StringBuilder touchTask(StringBuilder bastel, 
                        final CharSequence fileName,
                        final CharSequence uglyAntTime, 
                        final CharSequence antCompTime){
      if (bastel == null) bastel = new StringBuilder(120);
      bastel.append("    <touch datetime=\"");
      bastel.append(uglyAntTime).append("\">\n");
      bastel.append("      <fileset  erroronmissingdir=\"false\"\n" 
      		      + "          file= \"");
      String s = fileName.toString();
      boolean asIs = File.separatorChar == '/' || s.indexOf(':') != -1
          || s.indexOf('\\') == -1;
      if (asIs) {
         bastel.append(s);
      } else {
         bastel.append(s.replace('\\', '/'));
      }
      bastel.append("\">\n"); 
      if (antCompTime != null) {
         bastel.append("        <date datetime=\"").append(antCompTime);
         bastel.append("\" when=\"before\" />\n");
      //  V<date datetime="01/01/2001 12:00 AM" when="before"/>
      }
      bastel.append("      </fileset>\n    </touch>\n");  
      return bastel;
   } //  touchTask(StringBuilder, 3*CharSequence)

} // FS (10.08.2003, 29.12.2004, 06.02.2009, 07.11.2009, 18.08.2014)

