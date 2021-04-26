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

import de.frame4j.io.FileCriteria;
import de.frame4j.io.FileHelper;
import de.frame4j.io.FileService;
import de.frame4j.io.FileVisitor;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.MinDoc;
import de.frame4j.util.Prop;

/** <b>Deleting files</b>. <br />
 *  <br />
 *  This application deletes files and directories by command line without
 *  any prior question. One purpose is the use in scripts. By command line
 *  parameters or by an extra .properties file even complex file and 
 *  directory criteria may be applied.<br />
 *  <br />
 *  For deleting according to multiple file or directory denotations also 
 *  given as a list use the tool {@link Era}. {@link Era}, allowing optional
 *  safety questions, is also suitable for &quot;hand work&quot; / attended 
 *  use. This tool {@link Del} is a &quot;hard&quot; tool for automated / 
 *  batch processing, that tries to do what it is told &mdash; at once.<br />
 *  So use it with very great care at the command line input.<br />
 *  <br />
 *  Options allow <ul>
 *  <li>recursively visiting sub-directories, also (-r),</li>
 *  <li>specifying an age range for files to be deleted (-since, -til, 
 *      -days, -daysOld),</li>
 *  <li>deleting all (visited) directories if empty (-d, -delEmpty),</li>
 *  <li>deleting also non empty directories if the fit the file criteria 
 *      (-t, -tDel).<br />
 *      Nota bene: file criteria and not the directory criteria specifying the 
 *      sub-directories to be  visited.</li>
 *  <li>Output a help text (-?, -help)</li></ul>
 * 
 *  The ample possibilities for file and directory criteria may (best) be set
 *  by {@link de.frame4j.util.Prop Prop}erties. See 
 *  {@link FileService FileService} .  
 *  {@link FileService#set(de.frame4j.util.PropMap) set(PropMap)}.<br />
 *  <br />
 *  <b>Hints</b>:<br />
 *  <br />
 *  See the .properties file 
 *  <a href="doc-files/Del.properties" target="_top">Del.properties</a>. It is
 *  an integral part of this application and may as part of the documentation
 *  answer open questions.<br />
 *  <br />
 *  For denominating files in the command line so called wildcards may be 
 *  used:<br />
 *  <code> &nbsp; &nbsp; ..\tmp\+.j+  
 *   &nbsp; &nbsp;  ..\tmp\class;bak;tmp;obj;map</code><br />
 *  Like in the example + may be substituted for *. If you use * or ? the
 *  parameter has to be put in quotes. Otherwise the expansion mechanism
 *  of Linux shells will spoil all. No &quot;all clear&quot; signal for 
 *  Windows here: this bug has been meticulously ported to Windows JREs/JDKs
 *  by SUN&amp;Oracle. <br />
 *  <br />
 *
 *  <a href="./package-summary.html#co">&copy;</a> 
 *  Copyright 2003 &nbsp; Albrecht Weinert 
 *  @see      de.frame4j.Era
 *  @see      Prop
 *  @see      FileCriteria
 *  @see      FileService
 */
 // so far    V02.01 (20.05.2003 10:00) :  new, off spin of Era
 //           V.o10+ (03.02.2009 15:00  :  ported to Frame4J
 //           V.129+ (06.01.2016) :  FileHelper
 //           V.144+ (05.08.2016) :  refactored to Frame4J'89 slimline
 
@MinDoc(
   copyright = "Copyright 2003 - 2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application (-? for help)",  
   purpose = "delete files and / or directories specified also by complex criteria"
) public class Del extends App implements FileVisitor{


/** File(s) to work on. <br /> */ 
   public String fileName;

/** Do delete files. <br />
 *  <br />
 *  default : true
 */
   public boolean  fDel = true;

/** Also delete directories. <br />
 *  <br />
 *  default: false
 */
   public boolean  tDel;

/** Don't delete the start directory. <br />
 *  <br />
 *  If true, the start directory (specified by parameter) is kept, even if it
 *  fits the criteria and is (afterwards) empty.<br />
 *  <br />
 *  default: false
 */
   public boolean  noStartDel;
   
   private File leaveThis;

//-------------------------------------------------------------------------


/** Start method of {@link Del}. <br />
 *  <br />
 *  Return code:<br />
 *   Exit 0, if run successfully.<br />
 *   Exit 1, if the deleting of any file or directory was impossible (due to
 *           any objections (can only be a lock at DOS/Windows)<br />
 *   Exit 2, if deleting caused exceptions<br />
 *   Exit &gt; 2, Start or parameter problems (nothing done at all)<br />
 *  <br />
 *  @param  args command line parameters   <br />
 */
   public static void main(String[] args){
      try {
         new Del().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])


//---------------------------------------------------------------------------

   FileService delTreeDS;  // simple FileService object for "speedy" clean
   FileService dS;   // complex  FileService object for /exact) this task 
   boolean     dirLdel;   // delete empty directory on the way
     
// Book keeping 
   int   noOfFiles;   // Number of visited file (and -t directories)
   int   noOfDelFil;  // Number of deleted files
   int   noOfDelDir;  // Number of deleted directories
   int   noOfRejects; // total number of failed deletes
   int   noOfErrors;  // Number of failed deletes due to exceptions
   

   final FileVisitor ratzFaz = new FileVisitor(){
      @Override public int visit(File aktDatei){
          boolean nex = !aktDatei.exists();
          if (nex || FileHelper.compare(aktDatei, leaveThis) == 0) {
             return 0;
          }
          ++noOfFiles;
          String ifL = FileHelper.infoLine(aktDatei);
          if (isTest()) {
             log.println(ifL);
             log.println("   ... would be (tree) deleted  TEST");
             log.flush();
             return 1;
          }
          boolean isDir = aktDatei.isDirectory();
          try {
             if (aktDatei.delete()) {
                if (isDir) ++noOfDelDir; else ++noOfDelFil;               
                if (verbose) {
                   log.println(ifL);
                   log.println("   ... (tree) deleted .");
                } 
                return 1;
             }
          } catch (Exception e) {
             log.println(ifL);
             log.println("   ...  no del(tree), Error : " + e);
             ++noOfErrors;
             return 0;             
          }
          if (verbose && !isDir) {
            log.println(ifL);
            log.println("   ...  del(tree) rejected.");
             ++noOfRejects;
          }   
          return 0;         
       }
   };      // ratzFaz

/** The file visitor implementation.<br />
 *  <br />
 *  This is the &quot;desolator&quot;.<br />
 *  <br />
 *  @param aktDatei the file or directory to be deleted (without any fuss)
 */
   @Override public int visit(File aktDatei){
      boolean nex = !aktDatei.exists();
      if (nex || aktDatei.equals(leaveThis)) {
         return 0;
      }
      ++noOfFiles;
      String ifL = FileHelper.infoLine(aktDatei);
      if (isTest()) {
          log.println(ifL);
          log.println("   ... would be deleted  TEST");
          log.flush();
          return 1;
      }
      boolean isDir = aktDatei.isDirectory();
      try {
         if (aktDatei.delete()) { // if file deleted
            if (isDir) ++noOfDelDir; else ++noOfDelFil; 
            if (verbose) {
               log.println (ifL);
               log.println(isDir ? "   ... (empty) deleted ."
                                 : "   ... deleted ."); 
            } 
            return 1;
         } // if file deleted
         if (isDir) { // dir (nonempty
            final String dir = aktDatei.getPath(); 
            delTreeDS.dirVisit(dir, null,
                     ratzFaz,   // file
                     ratzFaz,      // empty dir
                     null ,     //  dir
                     ratzFaz);  // dir after
            return 0;         
         }  // dir (nonempty
      } catch (Exception e) {
         log.println(ifL);
         log.println("   ...  no del, Error : " + e);
         ++noOfErrors;
         return 0;             
      }
      if (verbose) {
         log.println(ifL);
         log.println("   ...  del rejected.");
      } 
      ++noOfRejects;
      return 0;         
   } // visit
   
//-------------------------------------------------------------------------

/** Del's working method. */
   @Override public int doIt(){
      log.println();
      if (verbose) {
         log.println(twoLineStartMsg().append('\n'));
         File logFile = appIO.getLogFile();
         if (logFile != null)       
            log.println("  /// Log file " 
                 + FileHelper.pathName(logFile, null, false, false));
      }           

      delTreeDS = new FileService(null, null, true);
      dS = new FileService();
      try {
         dS.set(prop);
      } catch (IllegalArgumentException e) {
         return errMeld(12, "Parameter error " + e);
      }   
      if (fileName == null) return errMeld(13, "Parameter error, no file");
         
      String path = dS.filCrit.parse(fileName);
      dS.filCrit.setAllowDir(tDel);
      dS.filCrit.setAllowFile(fDel);
      dirLdel = dS.isDelEmpty();
      if (noStartDel) leaveThis = FileHelper.getInstance(path, null);

      if (verbose) {
         if (isTest()) {
             log.println("     \n  /// " + dS + "     \n");
             log.println("  /// file(s) : " + fileName 
                       + "  \n  /// path is : " + path + '\n'); 
          if (tDel)
            log.println("\n\n  /// treedel with " + delTreeDS + '\n'); 
         }
      }
      
      dS.dirVisit(path, null,
            this,    // file
            dirLdel ? ratzFaz : null,   // empty dir
            null ,     // filBes,   // dir
            dirLdel ? ratzFaz : null);  // dir after
      
      if (noOfDelFil > 0 || noOfDelDir > 0 || verbose)
         log.println(noOfDelFil + " files + " + noOfDelDir
            + " dirs of " + noOfFiles + " deleted.");
      if (noOfRejects > 0 || noOfErrors > 0) {
          log.println(noOfErrors + "  errors + " 
                + noOfRejects + " rejections occurred");
          return noOfErrors > 0 ? 2 : 1;
      } // if dirDel
      return 0;
   } // doIt

} // class Del ( 11.06.2003, 03.02.2009 )
