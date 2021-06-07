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
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import de.frame4j.time.TimeHelper;


/** <b>Update files and directories</b>. <br />
 *  <br />
 *  This application updates files for a source to a destination directory.
 *  Optionally sub-directories can be worked on recursively. Additionally
 *  it is possible to copy files to a backup-directory before they are
 *  replaced in the destination directory.<br />
 *  <br />
 *  The making of non existent destination directories can be forbidden.<br />
 *  <br />
 *  Also, the creating of non existent files in the destination directories
 *  can be prohibited; that would mean &quot;update existing files
 *  only&quot;.<br />
 *  <br />
 *  Fundamental rule:<br />
 *  A file will be copied from the source to the destination directory if
 *  not existing there or if there older than in source. For source files
 *  of length 0 deleting in source instead of copying the empty file can be
 *  chosen as option.<br />
 *  <br />
 *  Attention: If source and destination directories are on different
 *  computers (as network drives e.g.) different time handling and especially
 *  asynchronous handling of daylight saving clock switches, can lead to
 *  totally surprising results (like updating thousands of files in the wrong
 *  direction). The same is true for diskettes or partitions using different
 *  file systems. Those effects may be experienced with all tools depending on
 *  file date/time &mdash; not just this one. But this features some safety /
 *  counter measures proven efficient over some years of experience in
 *  form of harder and adaptive age difference criteria.<br />
 *  <br />
 *  The evaluation of age differences as update criterion may be switched off
 *  (by the -xCopy option). This makes the update to a plain copy, but
 *  featuring all the other {@link FileService} control possibilities.<br />
 *  <br />
 *  <b>Hint</b>:  To this application {@link Update} belongs (as integral
 *  part) a .properties file named
 <a href="./doc-files/Update.properties" target="_top">Updatey.properties</a>.
 *  It's part of the documentation.<br />
 *  <br />
 *  <br />
 *  Copyright 1998 - 2005, 2009  &nbsp; Albrecht Weinert
 *  @see      App
 *  @see      FileCriteria
 */
 // so far    V00.00 (05.11.1998) : new made out of FS
 //           V00.02 (23.11.1998) : Backup directory
 //           V00.03 (04.12.1998) : verbose, difOld
 //           V00.05 (15.12.1998) : Type lists and -nonew
 //           V00.06 (10.01.1999) : delEmpty
 //           V01.03 (01.04.1999) : zoneSave
 //           V01.04 (28.06.1999) : Prop
 //           V02.03 (03.12.2000) : since, til with report
 //           V02.20 (24.05.2002) : de, AppBase
 //           V02.30 (24.05.2002) : App/WinApp, plug-in
 //           V02.24 (26.05.2005) : twoLineEndMeld
 //           V02.27 (16.11.2005) : noLCforTypes
 //           V02.28 (02.02.2006) : -xcopy; delete before copy in DaSe.
 //           V.129+ (06.01.2016) : FileHelper
@MinDoc(
   copyright = "Copyright  1998 - 2005, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 50 $",
   lastModified   = "$Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $",
   usage   = "start as Java application (-? for help)",
   purpose = "updates files and / or directories also by complex criteria"
) public class Update extends App {

   private Update(){} // no objects; no javaDoc 

//--- Service  public properties set by Prop.

/** Source directory with (optional) file specification. <br />
 *  <br />
 *  After the denotation of the source directory  (example D:\weinert\java\)
 *  file denotations may follow directly. They may contain wildcards. Using
 *  * or ? means that the corresponding commend line parameter has to be put
 *  in quotes. Alternatively + can be used instead of *. (Example:
 *   D:\weinert\java\Data+.+).<br />
 *  <br />
 *  Instead of a file name denotation like in the example a list of types
 *  is also possible (example: D:\weinert\java\java;class;bat;html).<br />
 *  <br />
 */
   public String  source;

/** Destination directory. <br />
 *  <br />
 *  This is the denotation of the destination directory where to copy or update
 *  the files to.<br />
 *  <br />
 *  Non existing directories (and sub-directories) will be made, if that is
 *  not explicitly forbidden.<br />
 *  <br />
 *  In destination directory denotations the properties stJMT, stHM and
 *  stJMTHM  (in batch files embraced in percent signs, like %stJMT%, %stHM%
 *  and %stJMTHM%) may be used as part of the names. These contain the date
 *  and time of the applications start.<br />
 */
   public String  dest;

/** Reverse roles of source and destination.<br />
 *  <br />
 *  default: false<br />
 *  <br />
 *  @see FileService#isReverse() FileService.reverse
 */
   public boolean reverse;

/** Backup directory. <br />
 *  <br />
 *  If not empty it is taken as a denotation of a directory (that will be made
 *  if needed and not yet existing). To this directory files will be copied,
 *  before the will be replaced or deleted in the destination directory
 *  {@link #dest}.<br />
 *  <br />
 */
   public String  back;

/** Start method of Update . <br />
 *  <br />
 *  If the application did its job successfully it will end with exit code 0.
 *  An exit code &gt; 0 means an abort due to a problem.<br />
 *
 *  @param  args   command line parameter
 *  @see #doIt
 */
   public static void main(final String[] args){
      try {
         new Update().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

/** Working method of Update. <br /> */
   @Override public int doIt(){
      String stJMT  = TimeHelper.format("Y-m-d", appStartTimeMS);
      String stHM   = TimeHelper.format("h-i", appStartTimeMS);
      prop.setProperty("stJMT", stJMT);
      prop.setProperty("stHM", stHM);
      prop.setProperty("stJMTHM", stJMT + "_"+stHM);   // *1)
      if (condHelpLog()) return 0;
      if (isNormal()) {
         log.println();
         log.println(twoLineStartMsg().append('\n'));
      }
      source = prop.getText("source");   // *1)
      dest  = prop.getText("dest");    // because of getText time
      back  = prop.getText("back");
      if (dest == null || source == null
           ||  source.length() < 1  || dest.length() < 1 )
         return errMeld(15, valueLang("toofewpar"));
      FileService service = new FileService();
      service.setDifOld(120000); // default instead of 0L
      try {
         service.set(prop);
      } catch (IllegalArgumentException iae) {
         return errMeld(17, iae);
      }

    File qvz = FileHelper.getInstance(source, null);  // Start source dir
    if (!qvz.exists() || !qvz.isDirectory()) {
       qvz = null;
       source = service.filCrit.parse(source); // sets filCrit.wildname
    }
    if ("*.*".equals(service.filCrit.getWildName()))
       service.filCrit.setWildName(null);

    if ("=".equals(service.dirCrit.getWildName()))
       service.dirCrit.setWildName(service.filCrit.getWildName());

    if (qvz == null)
          qvz = FileHelper.getInstance(source, null);  // Start source directory
    File zvz = FileHelper.getInstance(dest, null); // Start destination dir.
    File bvz = null;                             // Start backup directory


    if (!qvz.exists() || !qvz.isDirectory()) {
       return errMeld(15, formMessage("sournodir", qvz.getPath()));
    }

    if (zvz.exists() && !zvz.isDirectory()) {
       return errMeld(17, formMessage("destnodir", zvz.getPath()));
    }

    if ((back != null) && (back.length() > 0)) {
       bvz = FileHelper.getInstance(back, null);   // Start backup dir
       if (bvz.exists() && !bvz.isDirectory()) {
          return errMeld(19, formMessage("backnodir", bvz.getPath()));
       }
    } // BVZ given 1

    StringBuilder bastel = new StringBuilder(480);
    if ((service.filCrit.getWildName() == null) &&
                    (service.filCrit.getTypes() == null)) {
       bastel.append(valueLang("updtallfi"));
    } else {
       bastel.append(valueLang("updtofils"));
       String tmp = service.filCrit.getWildName();
       if (tmp != null) bastel.append(tmp).append(' ');
       String[] tys = service.filCrit.getTypes().asArray();
       for (String act : tys) {
          if (act == null) break;
          bastel.append(act).append(' ');
       }
    }
    if (service.isRecursion()) bastel.append(valueLang("incsubdir"));
    bastel.append('\n');
    if (service.filCrit.isTimeSet()) {
       bastel.append(" modif. ");
       if (service.filCrit.isMinTimeSet()) {
          bastel.append(valueLang("since", " von "));
          bastel.append(TimeHelper.formatDIN(service.filCrit.getMinTime()));
       }
       if (service.filCrit.isMaxTimeSet()) {
          bastel.append(valueLang("until", " to "));
          bastel.append(TimeHelper.formatDIN(service.filCrit.getMaxTime()));
       }
    }
    String fr = reverse ? zvz.getPath() : qvz.getPath();
    String to = reverse ? qvz.getPath() : zvz.getPath();

    bastel.append(valueLang("updfrom")).append(fr);
    bastel.append(valueLang("updto")).append(to);
    if (bvz != null)
       bastel.append(valueLang("updbackup")).append(bvz.getPath());

    log.println(bastel.toString());
    bastel = null;

    if (TextHelper.areEqual(fr, to, true)) {  // todo repl. by file system CS
       return errMeld(33, formMessage("soueqdest", fr));
    }

    if (service.isRecursion() &&  (reverse
          ? FileHelper.contains(zvz, qvz) : FileHelper.contains(qvz, zvz)) ) {
       return errMeld(39, valueLang("rectosubd"));
    }   // recursive copies to (own) sub-directories not allowed.

    if (bvz != null) {
       if (TextHelper.areEqual(fr, bvz.getPath(), true)
           ||  TextHelper.areEqual(to,  bvz.getPath(), true))   {
          return errMeld(43, formMessage("soueqback", fr));
       }
       if (service.isRecursion() && (FileHelper.contains(qvz, bvz)
                                    || FileHelper.contains(zvz, bvz)) ) {
          return errMeld(17, valueLang("rebtosubd"));
       } // recursive backup to (own) sub-directories forbidden
    } // backup directory denoted 2

   //---   End of parameter evaluation ----------------------------------

      int anz = service.doUpdate(qvz, zvz, bvz, log);
      if (isNormal()) {
         log.println(formMessage("updnofrep", anz));
         if (verbose || anz > 32) {
            log.println();
            log.println(twoLineEndMsg().append('\n'));
         }
      } //

      return 0;
   } //  doIt()
} // Update (20.02.2004, 26.05.2005, 04.02.2009)
