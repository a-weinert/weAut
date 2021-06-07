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
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.frame4j.util.MinDoc;
import de.frame4j.util.Prop;
import de.frame4j.text.TextHelper;
import de.frame4j.text.TextHelper.ReplaceVisitor;
import de.frame4j.time.TimeHelper;
import de.frame4j.io.FileCriteria;
import de.frame4j.io.FileHelper;
import de.frame4j.io.FileService;
import de.frame4j.io.FileVisitor;
import de.frame4j.io.Input;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.text.CleverSSS;
import de.frame4j.util.ComVar;
import de.frame4j.text.KMP;


/** <b>Beautify text files got from Subversion / SVN (or CVS) </b>. <br />
 *  <br />
 *  This application beautifies / modifies the text contained in Subversion 
 *  (SVN) or also the outdated Concurrent Versioning System (CVS)
 *  keywords. This is usually done prior to building or deployment. Optionally
 *  the &quot;keyword brace&quot; can be removed. Using this tool is the 
 *  &mdash; better &mdash; substitute for export, -kv or similar.<br />
 *  <br />
 *  The task can be done in one step for a whole project as this application
 *  visits all specified text files starting from a base directory. 
 *  Sub-directories are visited recursively too as default (use
 *  option -nr if no descend to sub-directories is wanted).<br />
 *  <br />
 *  The standard setting for files to be visited is: all files of
 *  types<br /> &nbsp;
 conf;c;h;css;htaccess;java;html;properties;bat;xml;txt;asm;cmd;htm;xsl;xsd;dtd;ent;cpp;php;ini
 *   <br />
 *  Those are the usual source and control text files of a (Java-, XML-, ..) 
 *  software or web publishing project.<br />
 *  <br />
 *  All the standard settings, like file, directory and text criteria etc. 
 *  described here, can be modified by start parameters or by extra 
 *  .properties files supplied in very great detail and freedom.<br />
 *  <br /> 
 *  In every visited text file some replacements may be done; in the standard 
 *  setting they are:
 *  <ul><li>replacing tabs by three spaces</li>
 *  <li>replacing dollar bracketed $CVSkeyword$ or $CVSkeyword: value$, namely<ul>
 *    <li>&quot;Date&quot; by &quot;03.11.2009&quot;, that is by the actual
 *        date or by the date of last file modification in the form defined
 *        by &quot;j.m.Y&quot;  (see 
{@link de.frame4j.time.TimeHelper#format(CharSequence, java.time.ZonedDateTime)}) </li>
 *    <li>&quot;Date: utcDate&quot; by the reformatted date relates to 
 *        local time zone in the form defined
 *        by &quot;j.m.Y&quot; (see
{@link de.frame4j.time.TimeHelper#format(CharSequence, java.time.ZonedDateTime)})<br />
 *        Remark: CVS and cvsNT use UTC without specifying it while SVN 
 *        explicitly specifies the zone offset; both works here.</li>
 *    <li>&quot;Author&quot; by &quot;A. Weinert&quot;<br />
 *        Remark: thats changeable, of course, see above and 
 *         <a href="doc-files/SVNkeys.properties"
 *                target="_top">SVNkeys.properties</a></li>
 *    <li>&quot;Author: name&quot; by &quot;name&quot;</li>
 *    <li>&quot;Name&quot; by &quot;last revision (HEAD)&quot;</li>
 *    <li>&quot;Name: release&quot; by &quot;release&quot;</li>
 *    <li>&quot;Revision&quot; by &quot;new&quot;</li>
 *    <li>&quot;Revision: revision&quot; by 
 *         &quot;revision&quot;</li>
 *    <li>&quot;Rev&quot; and &quot;LastChangedRevision&quot; are treated
 *         like Revision.</li>
 *    <li>&quot;LastChangedBy&quot; by &quot;(unknown)&quot;</li>
 *    <li>&quot;LastChangedBy: name&quot; by &quot;name&quot;</li>
 *    <li>&quot;Id&quot; by &quot;(this file)&quot;</li>
 *    <li>&quot;Id: pigsty&quot; by &quot;pigsty&quot;</li>
 *    <li>&quot;HeadURL&quot; by &quot;&quot; (nothing)</li>
 *    <li>&quot;HeadURL: serverURL&quot; by &quot;serverURL&quot;</li>
 *    <li>&quot;Branch&quot; by &quot;&quot; (nothing)</li>
 *    <li>&quot;Branch: hare&quot; by &quot;hare&quot;</li>
 *    <li>&quot;URL&quot;is treated like HeadURL.</li>
 *     </ul></ul>
 *     
 *  The first point fights a bug of some (Eclipse-Java-) editors using 
 *  tabulators instead of spaces for indentation against being ordered 
 *  otherwise. The other points overcome faults and the deformednes of the 
 *  CVS and SVN checkout and export functions (especially the -kv option
 *  bringing just anger and tears).<br />
 *  Just for clarification: This application renders export, -kv and all else
 *  superfluous. Just work on checked out files (or a copy thereof) before 
 *  preparing (compiling, documenting, building) the project for 
 *  deployment.<br />
 *  <br />
 *  In the procedure of removing the dollar-keyword-braces the now got 
 *  value may undergo another non case sensitive compare and replace. This
 *  feature is normally used to replace log-in names set in by SVN 
 *  by the person's name, as &quot;FB3-MEVA\weinert&quot; by 
 *  &quot;Albrecht&nbsp;Weinert&quot; for example. This substitutions are
 *  freely configurable.<br />
 *  <br />  
 *  In another mode of operation is the &quot;draining&quot; of CVS-keys or
 *  Subversion-keywords, that is replacing $CVSkeyword: value$ by just
 *  $CVSkeyword$.<br />
 *  Hint: This &quot;draining&quot; is more useful for SVN. Due to a bug, 
 *  SVN fails to modify on some content between colon (:) and closing 
 *  dollar ($).<br />
 *  <br />
 *  <b>Hint 2</b>: To this application belongs (as integral part) a
 *  .properties file, named <a href="doc-files/SVNkeys.properties"
 *  target="_top">SVNkeys.properties</a>. It resides in the actual directory,
 *  in jre/lib or (best) in the .jar file containing this application.<br />
 *  <br />
 *  This file <a href="doc-files/SVNkeys.properties"
 *  target="_top">SVNkeys.properties</a> is part of the documentation.<br />
 *  <br />
 *  <b>Hint 3</b>: <u>All</u> public object variables and setters are 
 *  properties steering the operation mode of SVNkeys. They are set
 *  <u>automatically</u> by start arguments and .properties file(s) by 
 *  {@link Prop de.frame4j.util.Prop} (see 
 *  {@link Prop#setFields(Object) setFields()}).<br />
 *  <br />
 *  <b>Hint 4</b>: With CVS (and cvsNT) you just have the so called
 *  &quot;keyword substitution&quot; always. You have to kill it explicitly 
 *  sometimes for non text files. With Subversion this nice feature is
 *  off by default and must be switched on for every single file, like in 
 *  example:<br /> &nbsp;
 *   svn propset svn:keywords "Date Revision Author Id HeadURL" *.java<br />
 *  Stupidly it's really for the single file(s) not for the file type, as the
 *  example might suggest. Bringing in a new .java file you must repeat the
 *  procedure for it!<br />
 *  This application {@link SVNkeys} gets the types of text files to modify
 *  by the property  
 *  {@link FileCriteria}.{@link FileCriteria#getTypes() types}, the default
 *  see above or in  <a href="doc-files/SVNkeys.properties"
 *  target="_top">SVNkeys.properties</a>.<br /> 
 *  <br />
 *  <b>Hint 5</b>: This application is part of the framework Frame4J. Frame4J's
 *  building and deploying scripts also use this application before compiling
 *  or doing the (javaDoc) documentation. That makes Frame4J 
 *  &quot;self&nbsp;beautifying&quot; (and 
 *  involved &quot;self&nbsp;building&quot;).<br />
 *  So the text you just see is (also)  edited by {@link SVNkeys} or 
 *  {@link SVNkeysFilter}. For sake of
 *  compactness the actual SVN keywords of this source follow. If you don't
 *  see the something like <br /> &nbsp;
 *  &#036;Date: 2013-01-12 23:47:57 +0100 (Sa, 12 Jan 2013) &#036;<br >
 *  {@link SVNkeys} did work:<ul>
 *  <li>Revision: &quot;$Revision: 50 $&quot;</li>
 *  <li>Date: &nbsp; &nbsp; &quot;$Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $&quot;</li>
 *  <li>Author: &quot;$Author: albrecht $&quot;</li>
 *  </ul>
 *  <br />
 *  <b>Hint 6</b>: This application uses 4 to 8 parallel threads / tasks to
 *  work on the the files. The beautifying of a single file is totally 
 *  independent of all others and in project with hundreds of files the gain
 *  is significant compared to single thread sequential work.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a> 
 *       Copyright 2003, 2009, 2013 &nbsp; Albrecht Weinert<br />
 *  <br /> 
 */
 // so far    V02.01 (25.05.2003) :  new extracted from FuR
 //           V02.05 (03.06.2003) :  first productive
 //           V02.27 (01.07.2005) :  Subversion
 //           V02.32 (08.12.2005) :  keep file date (and substitute)
 //           V.107+ (22.05.2009) :  allow SVN's # field length delim.
 //           V.o01+ (03.02.2010) :  moved to Assembla due to Oracle-Sun
 //           V.o72+ (12.01.2013) :  handle RejectedExecutionException
 //           V.129+ (07.06.2015) : renamed to SVNkeys
 //           V.135+ (06.01.2016) : FileHelper
 //           V.135+ (05.08.2016) : refactored to Frame4J'89 slimline
 //           V.003+ (06.01.2017) : SVN new on Ubuntu, (hence) filModEnc

@MinDoc(
   copyright = "Copyright 2003 - 2013, 2016, 2017  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 50 $",
   lastModified   = "$Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $",
   usage   = "start as Java application (-? for help)",  
   purpose = "refine and beautify text files got from SVN"
) public class SVNkeys extends App implements FileVisitor {

/** No objects by others (no javaDoc also). */
   private SVNkeys(){}

/** Start method of SVNkeys. <br />
 *  <br />
 *  An exit code (ERRORLEVEL) 0 signalises a run without trouble.<br />
 *  <br />
 *  Problems lead to &gt;0. (&lt; 0 not used for sake of Windows.)<br />
 *  <br />
 *  exit code 0: OK.<br />
 *  exit code 1: completely run through, but I/O problems with some files 
 *  or directories visited.<br />
 *  exit code&gt;1: parameter or other starting problems; nothing done.<br />
 *  <br />
 *  @param args the application's start parameter
 */
   public static void main(final String[] args){
      try {
         new SVNkeys().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

//-----  Properties, automatic setting by Prop's parameter evaluation  ----
   
/** Encoding of files to be modified. <br />
 *  <br />
 *  The name of the real/current encoding of the text files to be modified.
 *  It must fit and is not changed. <br />
 *  <br />
 *  No explicit setting (null) defaults to the platform encoding. <br />
 *  @since January 2017
 */
   public String filModEnc;
   Charset filModCs; 

/** Visit (recursively) also sub-directories. <br />
 *  <br />
 *  If true all matching files are worked on in sub-directories, also.<br />
 *  <br />
 *  default: false
 */ 
   public boolean recursion;

/** Replace Tabs by spaces. <br />
 *  <br />
 *  default: true<br />
 *  @see #tabWidth
 */  
  public boolean tabReplace = true;
   
/** Replace tabs by that number of spaces. <br />
 *  <br />
 *  allowed: 2..12<br />
 *  default: 3<br />
 *  @see #tabReplace
 */  
   public int tabWidth = 3;
   
/** Opening brace for keywords. <br />
 *  <br />
 *  default: (SVN / Subversion standard) is Dollar $
 */
   public String opBrace = "$";
 
/** Closing brace for keywords. <br />
 *  <br />
 *  default: (SVN / Subversion standard) is Dollar $
 */
   public String clBrace = "$";
   CleverSSS clBrk;
 
/** Set operator for keywords. <br />
 *  <br />
 *  default: (SVN / Subversion standard) is colon :
 */
   public String setOperator = ":";

/** Maximal distance for keyword braces. <br />
*  <br />
*  allowed 24 .. 300<br />
*  other values: no limit (not recommended)<br />
*  default = 150 (seems high, but #36;HeadURL: .... #36; can get longer.
*/
   public int  maxBraceDist = 150;

/** Field end marker keywords. <br />
 *  <br />
 *  default: Subversion only uses sharp (#)<br />
 *  set to blank or 0 for no effect
 */
   public String fieldLengthMarker = "#";

/** Remove the keyword braces. <br />
 *  <br />
 *  Removing the ugly Dollars plus keyword plus colon (:) is the absolute
 *  minimal cosmetic prior to deployment.<br />
 *  <br /> 
 *  default: true
 */
   public boolean removeBraces = true;

/** Remove the keyword content. <br />
 *  <br />
 *  default: false
 */
   public boolean removeContent;
   

/** Ignore case for keywords. <br />
 *  <br />
 *  Note: SVN / Subversion and CVS are both case sensitive (Date != date).<br />
 *  <br />
 *  default: false = case sensitive keywords
 */
   public boolean ignoreKeyCase;

/** Keyword for Date. <br />
 *  <br />
 *  Date and time of date are subject to optional reformatting due to the 
 *  usual ugliness not fit for customers and / or documentation.<br />
 *  <br />
 *  default: Date<br />
 *  @see #dateFormat
 */
   public String dateKey = "Date";
   
/** Re-format the date. <br />
 *  <br />
 *  default: true<br />
 *  @see #dateKey
 */
   public boolean reformatDate = true;
   
/** Re-format the date; format String. <br />
 *  <br />
 *  default: j.m.Y<br />
 *  <br />
 *  The format characters are described at
 *  {@link de.frame4j.time.TimeHelper#format(CharSequence, java.time.ZonedDateTime)}.<br />
 *  @see #dateKey   
 */
   public String dateFormat = "j.m.Y";

/** For empty &#36;Date&#36; insert current time. <br />
 *  <br />
 *  If true the replacement for an empty date is the current time, of course
 *  formatted with {@link #dateFormat}. If false just {@link #dateDefault}
 *  will be inserted instead.<br />
 *  <br />
 *  default: false<br />
 *  <br />
 *  @see #dateDefaultToMod
 */
   public boolean dateDefaultToNow;

/** For empty &#36;Date&#36; insert the file's modification date. <br />
 *  <br />
 *  If true the replacement for an empty date is the file modification
 *  date, of course formatted with {@link #dateFormat}. This setting has 
 *  priority over {@link #dateDefaultToNow} and {@link #dateDefault}.<br />
 *  <br />
 *  This feature gives a sensible date also for files not under version 
 *  control (by just inserting &#36;Date&#36;.<br />
 *  <br />
 *  Hint: Due to a bug Subversion will usually not work on &#36;Date&#36;. It 
 *  needs &#36;Date: &#36; or &#36;Date: rhubarb &#36; including the trailing
 *  blank!<br />
 *  <br />
 *  default: true
 *  @see #keepLastModif
 */
   public boolean dateDefaultToMod = true;


/** Substitute for empty &#36;Date&#36;. <br />
 *  <br />
 *  default: (no date)
 */
   public String dateDefault = "(no date)";

/** Keep the file modification date, also with replacements. <br />
 *  <br />
 *  If true the last file modification date will be kept, even if this 
 *  applications made &quot;keyword replacements&quot;. The file date then
 *  still mirrors the date of the last content modification. That is CVS's
 *  point of view. Subversion's big bug is not to preserve modification dates
 *  but puts the dates of commit and checkout instead. 
 *  No matter what, the date of beautifying by this tool normally will
 *  not be of interest. <br />
 *  <br />
 *  default: true (i.e. do not consider beautifying as modification)
 */
    public boolean keepLastModif = true;

/** Keys (multiple). <br />
 *  <br />
 *  Storage for Properties keyN (N = 0..99).
 */
   CleverSSS[] rkKey;

/** Keys (multiple); default value. <br />
 *  <br />
 *  Storage for Properties defN (N = 0..99).
 */
   String[] def;

/** Keys (multiple); switch. <br />
 *  <br />
 *  Storage for Properties onN (N = 0..99).
 */
   boolean[] on;
  
/** Values (multiple). <br />
 *  <br />
 *  Storage for Properties valN (N = 0..99).
 */
   String[] val;

/** Substitutes for values (multiple)). <br />
 *  <br />
 *  Storage for Properties subN (N = 0..99).
 */
   String[] sub;

/** Text to mark excluded file. <br />
 *  <br />
 *  If ignFilesWith is neither null nor empty, files that contain that 
 *  text (regarding case) will not be worked on, even if matching all other
 *  file criteria.<br />
 *  <br />
 *  Hereby one can exclude files from processing by setting a significant
 *  marker text (usually in an comment or hidden text).<br />
 *  <br />
 *  default: null (no textual exclude criterion)<br />
 *  <br />
 *  @see FuR
 */
   public String  ignFilesWith;

/** Number of Key replacements. <br /> */
   int anzKeys = -1;

/** Number of value substitutions. <br /> */
   int anzValSub = -1;
 
/** Start directory. <br />
 *  <br />
 *  default: . (actual directory)
 */
   public String  directory;

//--------  Book keeping    --------------------------------

   volatile int dNr;  // file number / number of all files
   int dAnz; // number of all files processed
   int dAus; // number of files fitting exclude criterion
   int dFel; // number of files having read or write problems
   int eAnz; // number of  all substitutions
   int tAnz; // number of  tab substitutions
   int dtFm; // number of date substitutions

//===========================================================================
   
/** <b>The single task for a file visited</b>.<br />
 *  <br />
 *  The overhead of this task is about 3ms / file on a &quot;lame&quot; two
 *  core Atom compared to having the file visitor directly in the 
 *  {@link App}lication's main thread.<br />  
 */
   protected final class MyTask 
                           implements ReplaceVisitor, FileVisitor, Runnable {

     private final int dNr;  // file number / number of all files
      private int dAnz; // number of all files processed
      private int dAus; // number of files fitting exclude criterion
      private int dFel; // number of files having read or write problems
      private int eAnz; // number of  all substitutions
      private int tAnz; // number of  tab substitutions
      private int dtFm; // number of date substitutions
      
      private int replaceInd; // Index of key; 0 = Date,  Visit's steering var.

/** The file to act on. <br /> */      
      final protected File dD;
      
/** The files original modification date (if to keep). <br /> */      
      private long modifFile;

/** Make a task for one file . <br />
 *  <br />
 *  @param dD the file to act upon
 */      
      protected MyTask(File dD){
         this.dD = dD;
         this.dNr =  ++ SVNkeys.this.dNr;
      } // constructor

/** The file visitor implementation for the executor task. <br />  */     
      @Override public void run(){
         visit(dD);
         synchronized(SVNkeys.this.log) {
            SVNkeys.this.dAnz += this.dAnz; // number of all files processed
            SVNkeys.this.dAus += this.dAus; // files fitting exclude criterion
            SVNkeys.this.dFel += this.dFel; // files having I/O problems
            SVNkeys.this.eAnz += this.eAnz; // of  all substitutions
            SVNkeys.this.tAnz += this.tAnz; // of  tab substitutions
            SVNkeys.this.dtFm += this.dtFm; // of date substitutions 
            if (bastel != null && bastel.length() != 0) {
               SVNkeys.this.log.println(bastel.toString());
               bastel = null;
            }
         } // sync
      } // run()

      StringBuilder bastel;
      
/** File visitor. <br />
 *  <br />
 *  Procession of all text substitutions on the visited file.<br /> 
 */  
   @SuppressWarnings("resource")  // fr will be indirectly closed as file  
   @Override public int visit(final File dD){
     if (verbose) startFileLog();
     String ai  = null;
     int    aiL = 0;
     try { 
        Input fr = new Input(dD);
        ai = fr.getAsString(filModCs); // filModEnc since 6.1.16 (here input)
     } catch (IOException e) {
        ++dFel;
        if (!verbose) startFileLog();
        bastel.append(valueLang("notreadbl", "is not readable")).append('\n');
        return 0;
     }
     if (ai == null || (aiL = ai.length()) == 0) {
        if (verbose) {
           bastel.append(valueLang("nocontent",
                                          "no content (empty)")).append('\n');
        }
        return 0;
     }
     if (verbose) {
        bastel.append(formMessage("noofchar", aiL)).append('\n');
     }

     if (ignFilesWith != null && ai.indexOf(ignFilesWith) >= 0) {
          ++dAus;
        if (verbose) {
           bastel.append(valueLang("exclcrit",
                                   "matches exclude criterion")).append('\n');
        }
        return 0;
     } // exclude ?
     
     if (verbose) log.println();
     modifFile = dD.lastModified(); // to keep 08.12.05

     StringBuilder bu1 = new StringBuilder(ai);
     StringBuilder bu2 = new StringBuilder(aiL + 500);

     int vork = 0;
     if (tabReplace) {
        int tki = TextHelper.fUr(bu1, bu2, "\t", null, trs, false);
        if (tki > 0) {
           vork += tki;
           tAnz += tki;
           StringBuilder tmp = bu2;
           bu2 = bu1;
           bu1 = tmp;
           bu2.setLength(0); // clear
           if (verbose)
              bastel.append(formMessage("tabsrelp", tki)).append('\n');
       } // any repl
     }
            
     replLoop: for (replaceInd = 0; replaceInd < anzKeys; ++replaceInd) {
        if (!on[replaceInd]) continue;
        CleverSSS k = rkKey[replaceInd];
        int trp = TextHelper.fUr(bu1, bu2, k, clBrk,  this);
        if (trp > 0) {
          vork += trp;
          StringBuilder tmp = bu2;
          bu2 = bu1;
          bu1 = tmp;
          if (verbose) {
             bastel.append(formMessage("ntimsrepl",
                           new Object[]{Integer.valueOf(trp) , k})).append('\n');
          }
        } // any repl
        bu2.setLength(0); // clear
               
     } // for replLoop
            
     if (vork == 0) {
        if (verbose)  bastel.append(valueLang("nosrchtxt")).append('\n');
         return 0;
     }
     ++dAnz;
     eAnz += vork;
            
     if (verbose) bastel.append(formMessage("timschtxt", vork)).append('\n');

     //  FileHelper.OS os = FileHelper.makeOS(dD, OutMode.OVERWRITE, filModEnc); 
     
     FileHelper.OS os = null;
     try {
      os = new FileHelper.OS(dD, false, filModCs);
   } catch (IOException e) {
      bastel.append(e.getMessage());
   } 
     
     
     if (os == null) {                 // filModEnc since 6.1.16 (here output)
        ++dFel;
        if (!verbose) log.print("\n  " + dD.getPath() + "\n " + dNr + " \t");
        bastel.append(valueLang("cntbeovwr")).append('\n');
        return 0;
     }
     PrintWriter pw = os.pw;
     pw.print(bu1);
     os.close();
     if (keepLastModif) { // keep orig. modif. date
        dD.setLastModified(modifFile);  
     } // keep orig. modif.date
     return 1;
   } // visit()

   private void startFileLog(){
      if (bastel == null) bastel = new StringBuilder(520);
      bastel.append("\n  "). append(dD.getPath()).append("\n ");
      bastel.append(dNr).append(" \t");
   } // startFileLog()
      
     
      
/** Calculate the text replacements. <br />
 *  <br />
 *  Implement the visitor pattern; here using {@link TextHelper}.{@link
TextHelper#fUr(CharSequence, StringBuilder, CleverSSS, CleverSSS, ReplaceVisitor)
 *  fUr(CharSequence, ..., ReplaceVisitor)}.<br />
 *  <br />
 *  @see ReplaceVisitor
 */
   @Override public String visit(final String opBr, String cont, String clBr){
      int kop = opBr == null ? 0 : opBr.length();
      int kco = cont == null ? 0 : cont.length();
      int kcl = clBr == null ? 0 : clBr.length();
      int ks = kop + kco + kcl;

      if (ks > maxBraceDist) return null; // Safety
      if (removeContent) return TextHelper.con3cat(opBr, clBr, null);
      
      final int fiMaLen = fieldLengthMarker == null ? 0 
                                                : fieldLengthMarker.length();
      boolean fieldEndMarkerRemoved = false;
      String contS = null;
      if (cont != null) {
         contS = cont.toString().trim();
         if (contS.startsWith (setOperator)) contS = contS.substring(1).trim();
         kco = contS.length(); 
         if (fiMaLen != 0 && contS.endsWith(fieldLengthMarker)) {
            contS = contS.substring(0, kco - fiMaLen).trim();
            kco = contS.length(); 
            fieldEndMarkerRemoved = true;
         }
      } // cont != null
      if (kco == 0) {  
         contS = def[replaceInd];
         if (dateDefaultToMod && replaceInd == 0) { // time/date empty and
            contS = TimeHelper.format(dateFormat, modifFile);
         } // time/date empty and do take modification date then
      } else if (reformatDate && replaceInd == 0) { // empty else reformat 
         ++dtFm;
         long t = -1L;
         try { // CVS and cvsNT use UTC without offset specification 
            t = TimeHelper.parse(contS + " UTC ");  /// CVS UTC
         } catch (IllegalArgumentException e) {
            try {
               t = TimeHelper.parse(contS);  /// Subversion with +0200
            } catch (IllegalArgumentException ex) {
               log.println("Illegal time format found : " + contS);
            }
         }
         contS = t == -1L ? def[0] : TimeHelper.format(dateFormat, t);
      } // Date reformat
      
      if (removeBraces) { // pure Value 
         if (replaceInd > 0) for (int i = 0; i < anzValSub; ++i) {
            final String v = val[i];
            if (v == null) continue; 
            if (TextHelper.areEqual(v, contS, true)) return sub[i];
            // if (v.equalsIgnoreCase(contS)) return(sub[i]);
         } // for value substitutions on not time
         return contS; 
      }
       
      // to do / clarify : why did we not value substitution on keeping braces
      ///  if () clBr = fieldLengthMarker + clBr;
      return TextHelper.con3cat(opBr, setOperator, " ") 
               + TextHelper.con3cat(contS, 
                  fieldEndMarkerRemoved ? " " +  fieldLengthMarker  : " ",
                                          clBr);                   
   } // visit(3*String)
   } // MyTask ==============================================================

   private volatile String trs = "   ";  // Number of spaces for tab subst. 
   
   ExecutorService exec = new ThreadPoolExecutor(4, 8, 30,
             TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(300, false));
  
/** File visitor. <br />
 *  <br />
 *  Processing all text substitutions on the visited file.<br /> 
 */  
   @Override public int visit(File dD){
      MyTask myTask = new MyTask(dD);
      ///myTask.run(); // the no change test
      try {
         exec.execute(myTask);  // the executor version
      } catch (RejectedExecutionException ex) {
         myTask.run(); // execute in callers main thread & delay parallel work
      }
      return 0;
   } // visit()
      

/** Working method of SVNkeys. <br /> */
   @Override public int doIt(){
      log.println();
      if (verbose) log.println(twoLineStartMsg().append('\n'));

      FileService dS = new FileService();
      try {
         dS.set(prop);
      } catch (IllegalArgumentException iae) {
         return errMeld (5, iae);
      }
      
      if (tabWidth < 2 || tabWidth > 12)   tabWidth = 3;
      if (tabReplace && tabWidth != 3)   
         trs = "            ".substring(0, tabWidth);    
          
      if (opBrace == null || opBrace.length() == 0)  opBrace = "$";
      if (clBrace == null || clBrace.length() == 0)  clBrace = "$";
      clBrk = KMP.make(clBrace, ignoreKeyCase && !"$".equals(clBrace));
      if (setOperator == null || setOperator.length() != 1) setOperator = ":";
      if (maxBraceDist < 24 || maxBraceDist > 300) maxBraceDist = -1;
      if (removeBraces) removeContent = false;

      dateKey = TextHelper.trimUq(dateKey, "Date");
      if (dateFormat == null || dateFormat.length()== 0) dateFormat = "j.m.Y";
      if (dateDefault == null || dateDefault.length()== 0)
         dateDefault = "(no date)";

      if (directory == null 
              || (directory = directory.trim()).length() == 0)
         return errMeld(15, valueLang("nospcdir", 
                                           "No directory (or file) given"));
      
      if (filModEnc == null 
         ||  TextHelper.areEqual("defaultEncoding", filModEnc, true)) {
        filModEnc = FILE_ENCODING;
      } 
      try {
         filModCs =  Charset.forName(filModEnc);
      } catch (Exception e) {
         return errMeld(17, "Illegal file encoding " + filModEnc); 
      }
      
      if (isNormal()) {
         log.println ("\n  ///   Dir/Files = "  +  directory + "  (" 
                    + filModEnc  + (recursion ? ")  -recursion\n" : ")\n"));
      } // normal
      

      String path = directory  ;   //// dS.filCrit.parse(directory);
 
      if (isTest()) {
         err.println(path+" < > " + dS.filCrit.getWildName()); 
         err.println(path+" <;> " + dS.filCrit.getTypes()); 
         err.println("Start DirVisiter " + path+ " rec=" + dS.isRecursion());
         err.flush();
      } // Test

 //----  Key substitutions         
      for (int i = 99; i >= 0; --i) {
         String k  = prop.getString("key", i, null);
         if (k == null) continue;
         boolean o = prop.getBoolean("on", i, true);
         /// String d = prop.getString("on", i);
         ///boolean o = true;
         ///if (d != null) {
          ///  o = TextHelper.asBoolean(d);
         ///}
         if (!o && anzKeys == -1)  continue;
         String d = prop.getString("def", i, ComVar.EMPTY_STRING);
         /// if (d == null) d = ComVar.EMPTY_STRING;
         if (anzKeys == -1) {
            anzKeys = i + 2;
            ///key = new String[anzKeys];
            rkKey = new CleverSSS[anzKeys];
            def = new String[anzKeys];
            on  = new boolean[anzKeys];
         }
         String obK = opBrace + k;
         ///key[i+1] = obK;
         rkKey[i+1] =  KMP.make(obK, ignoreKeyCase);
         def[i+1] = d;
         on[i+1]  = o;
         if (o && verbose) {
            log.println("Key [ " + i + " ]\t\"" + k 
               + "\", def. \"" + d + "\"");
         }
      } // for key[i]
      
      if (anzKeys == -1) {
        anzKeys = 1;
        ///key = new String[1];
        rkKey = new CleverSSS[1];
        def = new String[1];
        on  = new boolean[1];
     }
     /// key[0] = opBrace + dateKey;
     rkKey[0] = KMP.make(opBrace + dateKey, ignoreKeyCase);
     def[0] = dateDefaultToNow ? TimeHelper.format(dateFormat, appStartTimeMS)
                               : dateDefault;
     on[0]  = true;
     
//---------- Val substitutions     
      for (int i = 99; i >= 0; --i) {
         String v  = prop.getString("val", i, null);
         if (v == null) continue;
         v = v.trim();
         if (v.length() == 0) continue;
         String s  = prop.getString("sub", i, null);
         if (s == null) continue;
         s = s.trim();
         if (s.length() == 0 || s.equals(v)) continue;
         if (anzValSub == -1) {
            anzValSub = i + 1;
            val = new String[anzValSub];
            sub = new String[anzValSub];
         }
         val[i] = v;
         sub[i] = s;
         if (verbose) {
            log.println("val[" + i + "]\t\"" + v 
                                               + "\", repl. \"" + s + "\"");
         }  
      } // for val[i]
 
     if (ignFilesWith == null || ignFilesWith.length() == 0)
         ignFilesWith = null;

//---   End of parameter parsing - now visit directories    -----------------
   
      dS.dirVisit(path, null,  // start directory
         this,               // File visitor
         null, null);      // no visitors for empty / filled directories
      
      exec.shutdown(); // no more  no more  no more  no more
      try {
         if (!exec.awaitTermination(90, TimeUnit.SECONDS))
            log.println(valueLang("executiob", "Executor timed out"));
      } catch (InterruptedException e) {
         log.println(valueLang("executint", "Executor interrupt"));
      }

      if (isNormal() || dFel > 0) { // verbose or error
         synchronized(SVNkeys.this.log) {
         int[] nums = new int[] 
                   //{ 0      1    2    3     4     5      6        7     8             
                     {eAnz, dAnz, dNr, dFel, dAus, tAnz, tabWidth, dtFm, 
                                                          eAnz - tAnz - dtFm};
         log.println (formMessage("replreprt", nums));
         //   "\n  ///   " + eAnz + "\t replacements in " + dAnz
         //        + " / " + dNr + " files");
         if (dFel != 0)
            log.println (formMessage("repfioprb", nums));
         /// "  ///   " + dFel + "\t files with i/o problems !!!"); 
         if (dAus != 0)
            log.println (formMessage("repfiexcl", nums));
         /// "  ///   " + dAus + "\t files matching exclude crit.");
         if (tAnz != 0 || dtFm != 0) {
            if (tAnz > 0) log.println(formMessage("reptabrep", nums));
            ///  "  ///   " + tAnz + "\t tabs replaced by "
            ///  + tabWidth + " spaces");
            if (dtFm > 0) log.println(formMessage("repdtrefr", nums));
            //// "  ///   " + dtFm + "\t date reformatting");
            log.println (formMessage("repkeywrp", nums));
            /// "  ///   " + (eAnz -tAnz -dtFm) + "\t keyword replacements.");
         }
         log.println( threeLineEndMsg().append('\n'));
      }} // verbose oder error | sync
      return dFel > 0 ? 1 : 0; 
   } //  doIt        

} // class SVNkeys (05.06.2003, 07.12.2005, 05.06.2009, 05.08.2016)
