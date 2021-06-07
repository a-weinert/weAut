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

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.BlockingQueue;


import de.frame4j.util.App;
import de.frame4j.util.AppHelper;
import de.frame4j.util.AppLangMap;
import de.frame4j.util.MinDoc;
import de.frame4j.util.PropMap;
import de.frame4j.util.Verbos;
import de.frame4j.text.TextHelper;
import de.frame4j.time.TimeHelper;

import static de.frame4j.util.ComVar.*;

/** <b>File and directory services</b>. <br />
 *  <br />
 *  Objects of this class support file and directory services. Therefore they
 *  have <ol>
 *  <li> an ample set of properties for those kind of services (like 
 *    &quot;act recursively on sub-directories too&quot; {@link #recursion},
 *    &quot;revert or equalise the roles of source and destination&quot;
 *   ({@link #recursion} respectively {@link #biDirect})<br /> as well as</li>
 *  <li> the necessary criteria for the files ({@link #filCrit}) and 
 *    directories ({@link #dirCrit}) to act on as two (associated) 
 *    {@link FileCriteria} objects.</li></ol>
 *    
 *  Point 2 is quite flexibly and comfortably implemented by the class
 *  {@link FileCriteria}.<br />
 *  <br />
 *  Point 1 relates to the criteria and settings for the actions / services
 *  to do on the selected files and directories, like {@link #recursion},
 *  {@link #createLowerCase}, {@link #noLCforTypes}, {@link #delEmpty} etc..
 *  These properties form a fairly complete set for a very wide variety of
 *  services, so some of them are, of course not relevant, for a concrete
 *  service to be implemented. Examples of services / tools using 
 *  {@link FileService} are de.frame4j.Update, {@link de.frame4j.FuR},
 *  de.frame4j.Del and {@link de.frame4j.SVNkeys}, to name a few.<br />
 *  <br />
 *  At first all properties and criteria of a {@link FileService} object have
 *  to be set. The most comfortable way is do do that in one step by the 
 *  method {@link #set(PropMap)} in one step using {@link PropMap} object with
 *  the right settings .<br />
 *  The second and last step is just to start the service (any service you can
 *  think of) by calling the method 
 {@link #dirVisit(String, String, FileVisitor, FileVisitor, FileVisitor, FileVisitor) dirVisit()}.
 *  It will visit all directories and files, that meet the selection criteria
 *  and other properties.<br />
 *  <br />
 *  The kind of service so (simply) done will be determined by one to three
 *  (four) objects passed to the above method, which implement the simple
 *  interface {@link FileVisitor}. By providing up to three such objects one
 *  can (but must not) implement separately the single isolated action on<ul>
 *  <li>a file, </li>
 *  <li>a non empty directors (visited before or after the recursive descend)
 *      and </li>
 *  <li>an empty directory.</li></ul>
 *  
 *  If the same action is wanted in more than one circumstance, supply the 
 *  same {@link FileVisitor} object. Is nothing to do specially in one of the
 *  cases, simply supply null.<br />
 *  <br />
 *  In any case the {@link FileVisitor}'s method 
 *  {@link FileVisitor#visit FileVisitor.visit}({@link File}) 
 *  implements the wanted action in each case. And that can be anything like 
 *  even just do nothing, just list, delete, or even quit complex text 
 *  operation like the Subversion (SVN) / CVS keyword beautifying for
 *  deployment by a tool like {@link de.frame4j.SVNkeys}.<br />
 *  The use of the visitor pattern by Frame4J lets one do comfortably 
 *  <u>an</u>y<u>thin</u> with files and directories by just implementing the 
 *  single action.<br />
 *  <br />
 *  <a name="visBild" id="visBild"
 *     href="./doc-files/dirVisit.png" target="_top"><img 
 *     src="./doc-files/dirVisit-kl.png" style="border:0;  margin: 0 10px;"
 *  width="602" height="353" alt="Click for full size" /></a><br />
 *  Figure 1: Visitor pattern using 
 {@link #dirVisit(String, String, FileVisitor, FileVisitor, FileVisitor) dirVisit()}<br />
 *  <br />
 *  A tool proceeding (like others) in that manner is {@link de.frame4j.FuR} 
 *  (Find and Replace on a set of files with arbitrary complex criteria). 
 *  After preparing all criteria and text replacements (most of 
 *  {@link de.frame4j.FuR}'s code) all text replacements on all files are just 
 *  done by using the {@link FileService} object (here called dS):
 *  <pre><code> &nbsp;     dS.{@link #dirVisit(String, String, FileVisitor, FileVisitor, FileVisitor) dirVisit}(path, null,  
 *   &nbsp;           replaceBesucher,   // the visitor for files
 *   &nbsp;           null, // visitor for empty directory (no action)
 *   &nbsp;           null, // visitor for filled directory (no action)
 *   &nbsp;           null); // dito for visit after content visiting</code></pre>
 *
 *  Implementation hint: Writing the {@link FileVisitor} as an inner class
 *  of the application / tool, as is done with replaceBesucher in 
 *  {@link de.frame4j.FuR}, gives that visitor access to all application 
 *  information needed. (If just one visitor is needed it can, of course
 *  be directly implemented by the application class.<br />
 *  <br />
 *  <br />
 *  Via factory methods &#160; 
 *  ({@link #makeOutputListVisitor(PrintWriter)}, &#160; 
 *   {@link #makeOutputInfoVisitor(PrintWriter)} &#160; and 
 {@link #makeInCollectionVisitor(Collection, boolean) makeInCollectionVisitor()})
 *        &#160; this class offers three types of multiply usable
 *   {@link FileVisitor} object, used, of course, in Frame4J's tools.<br />
 *  <br />
 *  Two often used file / directory services including all needed visitors
 *  are provided here too:<ol>
 *  <li>a comfortable and performing multistage backup and update service
 *      {@link #doUpdate doUpdate()} (used / wrapped in the tool 
 *      de.frame4j.Update),</li>
 *  <li>a supplementary cleaning service with optional backup 
 *      {@link #doClean doClean()}.</li></ol>
 *
 *  <br />
 *  <b>Implementation remarks on threading</b><br />
 *  <br />
 *  The visits done by  the method 
 {@link #dirVisit(String, String, FileVisitor, FileVisitor, FileVisitor, FileVisitor) dirVisit()}.
 *  and hence all calls to the the {@link FileVisitor}'s methods 
 *  {@link FileVisitor#visit FileVisitor.visit}({@link File}) come in a
 *  deterministic sequence in one thread. Hence, from this respect, those 
 *  methods implementing {@link FileVisitor} don't have to be thread safe and
 *  don't have to bother with concurrency in their book keeping or 
 *  whatever.<br />
 *  <br />
 *  On the other hand (by this lack of concurrency as yet) complex (text) 
 *  operations on hundreds of files &mdash; take {@link de.frame4j.FuR} and 
 *  {@link de.frame4j.SVNkeys} as examples &mdash; are done sequentially
 *  adding their execution time. If the actions on single files (as in the
 *  example {@link de.frame4j.SVNkeys}) were independent, concurrent execution
 *  would be allowed. The gain in time could be quite significant alone by
 *  not adding waits on I/O operations (even on just one core).<br />
 *  <br />
 *  Future versions of this class may bring an &quot;concurrency option&quot;
 *  in the sense of acting on multiple files in parallel.<br />
 *  <br />
 *  In between it is (under the condition of independence) possible for the
 *  application to supply a file visitor to the method 
 *  {@link #dirVisit dirVisit(... queuingVisitor ..)} that returns at once
 *  delegating the real work 
 *  (via a{@link BlockingQueue BlockingQueue<File>}) to an arbitrary 
 *  number of working threads.<br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2000 - 2003, 2005, 2006 &nbsp; Albrecht Weinert<br />
 *  <br /> 
 *  @see FileHelper
 *  @see FileHelper.OS
 *  @see FileCriteria
 *  @see #doUpdate(File, File, File, PrintWriter)
 *  @see #dirVisit(String, String, FileVisitor, FileVisitor, FileVisitor)
 */
 // so far    V00.00 (06.09.2000) : moved out of Datei 
 //           V00.01 (28.09.2000) : doUpdate -> back
 //           V00.10 (27.03.2002) : doClean
 //           V00.21 (06.06.2002) : FileService, set(Prop)
 //           V02.03 (21.05.2003) : toString
 //           V02.13 (29.12.2004) : internationalised
 //           V02.28 (15.11.2005) : noLCforTypes
 //           V02.29 (02.02.2006) : difOld <= 0L is xcopy mode
 //           V.10   (19.11.2008) : Version's jump cvsNT -> SVN
 //           V.26   (19.12.2008) : option -noReplace (difOld == -2L)
 //           V.170+ (07.11.2009) : omitExtraDirs omitExtraFiles added
 //           V.142+ (06.01.2016) : FileHelper
 //           V.  50 (01.06.2021) : changes due to Verbos

@MinDoc(
   copyright = "Copyright  2000 - 2009, 2015, 2021  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 51 $",
   lastModified   = "$Date: 2021-06-07 16:31:39 +0200 (Mo, 07 Jun 2021) $",
   usage   = "use when multiple file and directories have to be worked on",  
   purpose = "file and directory services"
) public class FileService implements Serializable {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153007500201L;
//                                      magic /Id./maMi

/** Reversing the copy or update direction. <br />
 *  <br />
 *  @see #isReverse()
 */
   protected boolean reverse;

/** Reversing the copy or update direction. <br />
 *  <br />
 *  If true the roles of source and destination in all services is 
 *  reversed. This feature might bring surprising and harmful results if
 *  used inadvertently.<br />
 *  On the other hand it simplified the implementation (specification by
 *  .properties files) of complicated bidirectional update or synchronisation
 *  tasks so much, that it is indispensable there.<br />
 *  <br />  
 *  default: false
 */
   public boolean isReverse(){ return reverse; }

/** Reversing the copy or update direction. <br />
 *  <br />
 *  @see #isReverse()
 */
   public void setReverse(boolean reverse){ this.reverse = reverse; }

/** Use both copy or update directions at once. <br />
 *
 *  @see #isBidirect()
 */
   protected boolean biDirect;

/** Use both copy or update directions at once. <br />
 *  <br />
 *  Contrary to {@link #isReverse() reverse} no services implemented in this 
 *  class uses (obeys) this flag. It's here to make {@link FileService} more
 *  commonly usable.<br />
 *  The in
 *  <br />
 *  default: false
 */
   public boolean isBidirect(){ return biDirect; }

/** Use both  copy or update directions at once. <br />
 *  <br />
 *  @see #isBidirect()
 */
   public void setBidirect(boolean biDirect){ this.biDirect = biDirect; }


/** Delete directories, even when not empty. <br />
 *
 *  @see #isDelTree()
 */
   protected boolean delTree;

/** Delete directories, even when not empty. <br />
 *  <br />
 *  default: false
 */
   public boolean isDelTree(){ return delTree; }
   
/** Delete directories, even when not empty. <br />
 *
 *  @see #isDelTree()
 */
   public void setDelTree(boolean delTree){ this.delTree = delTree; }


/** Recursively visit subdirectories. <br />
 *
 *  @see #isRecursion()
 */
   protected boolean recursion;

/** Recursively visit subdirectories.. <br />
 *  <br />
 *  default: false<br />
 */
   public boolean isRecursion(){ return recursion; }

/** Recursively visit subdirectories. <br />
 *  <br />
 *  If {@link #isRecursion() recursion} is set true here, {@link #dirCrit}'s
 *  allowDir will be set true also (which it normally should be anyway).<br />
 *  <br />
 */
   public void setRecursion(boolean recursion){
      this.recursion = recursion;
      if (recursion)  dirCrit.allowDir = true;
   } // setRecursion(boolean)

/** Delete empty files (length 0) or directories (0 files, subdirectories). <br />
 *
 *  @see #isDelEmpty()
 */
   protected boolean delEmpty;

/** Delete empty files (length 0) or directories (0 files, subdirectories). <br />
 *  <br />
 *  Meaning for {@link #doClean doClean()}:<br />
 *  If true destination directories, that were or became empty, will be 
 *  removed.<br />
 *  <br />
 *  Meaning for {@link #doUpdate doUpdate()}:<br />
 *  If both {@link #delEmptySource} and {@link #delEmpty} are true and if
 *  the (younger) source file has zero (0) length not only the corresponding
 *  destination file will be deleted, but (on success) this zero length source
 *  file also.<br />
 *  <br /> 
 *  Users of related services (like de.frame4j.Update) requested this
 *  just to have an acknowledge in the only directories (i.e. their source
 *  directories) the had access to.<br />
 *  <br />
 *  Hint: alternatively to this (file granular) delete by zero length one 
 *  could comfortably run a {@link #doClean doClean()} to delete all 
 *  destination files that don't exist in the source<br />
 *  <br />
 *  default: false<br />
 */
   public boolean isDelEmpty(){ return delEmpty; }

/** Delete empty files (length 0) or directories (0 files, subdirectories). <br />
 *  <br />
 *  @see #isDelEmpty()
 */
   public void setDelEmpty(boolean delEmpty){ this.delEmpty = delEmpty; }

/** Delete empty files or directories in the source directory. <br />
 *
 *  @see #isDelEmptySource()
 */
   protected boolean delEmptySource;

/** Delete empty files or directories in the source directory. <br />
 *  <br />
 *  Hint: This property if true (and obeyed by a {@link FileService} based
 *  service) may result in modifying source directories, which might be 
 *  semantically surprising.<br />
 *  <br />
 *  default: false;
 */
   public boolean isDelEmptySource(){ return delEmptySource; }

/** Delete empty files or directories in the source directory. <br />
 *  <br />
 *  @see #isDelEmptySource()
 */
   public void setDelEmptySource(boolean delEmptySource){
      this.delEmptySource = delEmptySource;
   } // setDelEmptySource(boolean)

/** No creation of non existing files. <br />
 *
 *  @see #isNonew()
 */
   protected boolean nonew;

/** No creation of non existing files. <br />
 *  <br />
 *  Meaning for {@link #doUpdate doUpdate()}:<br />
 *  <br />
 *  Files matching the criteria in the source directory will replace older
 *  ones in the destination directory. But, if a file of hat name does not 
 *  yet exist in the destination, it will not be made.<br />
 *  Nevertheless files replaced in destination directory will be made in / put
 *  to the backup directory even if not yet there in the case of multistage
 *  update.<br />
 *  <br />
 *  default: false <br />
 */
   public boolean isNonew(){ return nonew; }

/** No creation of non existing files. <br />
 *  <br />
 *  If {@link #isNonew() nonew} is set to false by this method
 *  {@link #isMakeDirs() makeDirs} will be set so too.<br />
 *  <br />
 *  @see #isNonew()
 */
   public void setNonew(boolean nonew){
      this.nonew = nonew;
      makeDirs &= !nonew; // if no new files are allowed => no new dirs 
   } // setNonew(boolean)

/** Make not yet existing (sub) directories. <br />
 *
 *  @see #isMakeDirs()
 */
   protected boolean makeDirs;

/** Make not yet existing (sub) directories. <br />
 *  <br />
 *  Meaning for {@link #doUpdate doUpdate()}:<br />
 *  <br />
 *  If makeDirs is set false a destination directory needed by the existence
 *  of a matching source directory will not be made.<br />
 *  Nevertheless if files are replaced in an existing destination directory 
 *  the matching directory in the backup will be made in the case of 
 *  multistage update.<br />
 *  <br />
 *  default: true <br />
 */
   public boolean isMakeDirs(){ return makeDirs; }

/** Make not yet existing (sub) directories. */
   public void setMakeDirs(boolean makeDirs){
      this.makeDirs = makeDirs;
   } // setMakeDirs(boolean)

/** Make new files and directories with lower case names. <br />
 *  <br />
 *  @see #isCreateLowerCase()
 */
   protected boolean createLowerCase;

/** Make new files and directories with lower case names. <br />
 *  <br />
 *  If this property is true, for files and directories to be made the names 
 *  will be used in lower case.<br />
 *  <br />
 *  default: false<br />
 *
 *  @see #doUpdate doUpdate()
 */
   public boolean isCreateLowerCase(){ return createLowerCase; }

/** Make new files and directories with lower case names. <br />
 *  <br />
 *  @see #isCreateLowerCase()
 */
   public void setCreateLowerCase(boolean createLowerCase){
      this.createLowerCase = createLowerCase;
   } // setCreateLowerCase(boolean)
   
/** Avoid lower casing names for specified types. <br />
 *  <br />
 *  @see #getNoLCforTypes()
 */
   protected String noLCforTypes = ".class;.java";
     

/** Avoid lower casing names for specified types. <br />
 *  <br />
 *  This property {@link #getNoLCforTypes() noLCforTypes} has no effect if
 *  null or if {@link #isCreateLowerCase() createLowerCase} is false.<br />
 *  <br />
 *  But if {@link #isCreateLowerCase() createLowerCase} is true and 
 *  {@link #getNoLCforTypes() noLCforTypes} is a not empty list of types 
 *  files of that type will be created in the case given by the source's name
 *  and not converted to lower case.<br />
 *  <br />
 *  default: .class;.java <br />
 *  <br />
 *  Rationale: In Java the source and the class file's name must exactly 
 *  mirror the class' name. And as name convention required the first 
 *  character being upper case, converting those file names to lower case 
 *  would be in all cases erroneous.<br />
 *
 *  @see FileHelper#isOfType(File, CharSequence)
 *  @see #setNoLCforTypes(String)
 */
   public final String getNoLCforTypes(){ return noLCforTypes; }

/** Avoid lower casing names for specified types. <br />
 *  <br />
 *  @see #getNoLCforTypes()
 *  @see TextHelper#trimUq(CharSequence, String)
 */
   public void setNoLCforTypes(String noLCforTypes){
      this.noLCforTypes = TextHelper.trimUq(noLCforTypes, null);
   } // setNoLCforTypes(String)

//------------------------------------------------------------------------

/** Verbosity of reports. <br />
 *  <br />
 *  @see #getVerbosity()
 */
   protected Verbos verbosity = Verbos.NORMAL;
   

/** Verbosity of reports. <br />
 *  <br />
 *  Meaning and handling see at 
 *  {@link AppHelper}.{@link Verbos#toString()}.<br />
 *  For values &lt;= {@link Verbos#TEST TEST} all implemented services 
 *  shall show no effect to the out side world, i.e. manipulate no files or 
 *  directories.<br />
 *  <br />
 *  default: {@link Verbos#NORMAL} (10)<br />
 *
 *  @see Verbos#SILENT
 *  @see Verbos#NORMAL
 *  @see Verbos#VERBOSE
 *  @see Verbos#DEBUG
 *  @see Verbos#TEST
 */
   public final Verbos getVerbosity(){ return verbosity; }
   
//-------------------------------------------------------------------------   

/** Minimal age difference of files. <br />
 *
 *  @see #getDifOld()
 */
   protected long difOld;

/** Minimal age difference of files. <br />
 *  <br />
 *  If &gt;= 0 the value is the minimum age difference in milliseconds 
 *  above which to files will be considered of different age.<br />
 *  <br />
 *  A value of -1 means ignore / don't use age as criterion. For the method / 
 *  service
 *  {@link #doUpdate(File, File, File, PrintWriter)} that means
 *  the switch to &quot;hard&quot; XCopy behaviour.<br /> 
 *  <br />
 *  A value of -2 means that an existing file will not be replaced no matter
 *  which (source or destination) is newer. This is a &quot;no replace&quot;
 *  behaviour. (See option -noReplace in de.frame4j.Update.)<br /> 
 *  <br />
 *  default: 0L
 *
 *  @see #doUpdate(File, File, File, PrintWriter) doUpdate()
 */
   public long getDifOld(){ return difOld;}

/** Set the minimal age difference of files. <br />
 *  <br />
 *  A parameter value {@code difOld < 0} and {@code difOld != -2} will be
 *  taken as -1.<br />
 *  <br />
 *  @see #getDifOld()
 */
   public void setDifOld(long difOld){ 
       this.difOld = difOld >= -0L || difOld == -2L ? difOld : -1L;
   } // setDifOld(long)

/** Summer / winter time switch precaution. <br />
 *
 *  @see #isZoneSafe()
 */
   protected boolean zoneSafe;

/** Summer / winter time switch precaution. <br />
 *  <br />
 *  If zoneSafe is true and the files considered are older than three days
 *  the minimal age difference is effectively raised to three hours, if
 *  difOld is less three hours and &gt;= 0.<br />
 *  <br />
 *  default: false<br />
 *  <br />
 *  @see      #setZoneSafe
 *  @see      #getDifOld()
 *  @see      #doUpdate(File, File, File, PrintWriter)
 */
   public boolean isZoneSafe(){ return zoneSafe; }

/** Summer / winter time switch precaution. <br />
 *
 *  @see      #isZoneSafe()
 */
   public void setZoneSafe(boolean zoneSafe){ this.zoneSafe = zoneSafe; }

/** File criteria. <br />
 *  <br />
 *  Two {@link FileCriteria} objects, {@link #filCrit} and {@link #dirCrit},
 *  hold all (filter) criteria for files and directories for services /
 *  operations using this {@link FileService} object.<br />
 *  <br />
 *  Hint: The (questionable) decision of having those criteria public 
 *  greatly simplified the implementation of those services.<br />
 *  <br />
 */
    public final FileCriteria filCrit;

/** Directory criteria. <br />
 *  <br />
 *  @see #filCrit
 */
    public final FileCriteria dirCrit;

//--------------------------------------------------------------------------

/** Standard Constructor. <br />
 *  <br />
 *  The associated {@link #filCrit} and {@link #dirCrit} are made in 
 *  almost default (allow all files respectively all directories).<br />
 *  <br />
 */   
   public FileService(){ this(null, null, false); }

/** Constructor with criteria. <br />
 *  <br />
 *  {@link #filCrit} and {@link #dirCrit} are taken in as a copy of the 
 *  objects supplied.<br />
 */   
   public FileService(final FileCriteria filCrit, final FileCriteria dirCrit,
                                                     final boolean recursion){
      if (filCrit == null) {
         this.filCrit = new FileCriteria();
      } else
         this.filCrit = filCrit.clone();

      if (dirCrit == null) {
         this.dirCrit = new FileCriteria();
      } else
         this.dirCrit = dirCrit.clone();

      this.filCrit.allowDir = false;
      this.dirCrit.allowFile = false;
      this.recursion = recursion;
   } // FileService(2*FileCriteria, boolean) 

/** Copy constructor. <br />
 *  <br />
 *  All fields and properties are copied from the other 
 *  {@link FileService} object that must not be null.<br />
 */   
   public FileService(final FileService other){
      this(other.filCrit, other.dirCrit, other.recursion);
      this.biDirect = other.biDirect;
      this.createLowerCase = other.createLowerCase;
      this.delEmpty = other.delEmpty;
      this.delEmptySource = other.delEmptySource;
      this.delTree = other.delTree;
      this.difOld = other.difOld;
      this.makeDirs = other.makeDirs;
      this.noLCforTypes = other.noLCforTypes;
      this.nonew = other.nonew;
      this.reverse = other.reverse;
      this.verbosity = other.verbosity;
      this.zoneSafe = other.zoneSafe;
   } // FileService(FileService) 

/** Set the properties by a PropMap object. <br />
 *  <br />
 *  The properties and the criteria of this {@link FileService} object will
 *  be set by the provided {@link PropMap}'s following
 *  properties (if given):<ul>
 *  <li> verbosity</li>
 *  <li> recursion</li>
 *  <li> zoneSafe</li>
 *  <li> lcNames sets {@link #createLowerCase}</li>
 *  <li> noLCforTypes</li>
 *  <li> delEmpty</li>
 *  <li> delEmptySource</li>
 *  <li> reverse</li>
 *  <li> biDirect</li>
 *  <li> delTree</li>
 *  <li> difOld</li>
 *  <li> noMd<br />
 *    &nbsp; &nbsp; not noMd sets {@link #makeDirs}.</li>
 *  <li> noNew<br />
 *    &nbsp; &nbsp; sets {@link #nonew}.</li>
 *  <li> ignoreCase<br />
 *    &nbsp; &nbsp; sets ignoreCase in {@link #dirCrit} and in
 *    {@link #filCrit}.</li>
 *  <li> thoseDirs<br />
 *    &nbsp; &nbsp; sets wildName in {@link #dirCrit}.</li>
 *  <li> omitDirs &nbsp;and&nbsp; omitExtraDirs<br />
 *    &nbsp; &nbsp; set the  excludeNames in {@link #dirCrit}.</li>
 *  <li> types respectively thoseFiles<br />
 *    &nbsp; &nbsp; sets wildName in {@link #filCrit}.</li>
 *  <li> omitFiles &nbsp;and&nbsp; omitExtraFiles<br />
 *    &nbsp; &nbsp; set the  excludeNames in {@link #filCrit}.</li>
 *  <li> days<br />
 *    &nbsp; &nbsp; &gt; 0 sets the time range in {@link #filCrit} to 
 *    &quot;since days * 24 h before now until ... &quot;.<br />
 *    If this property exists it must be &quot;nosetting&quot; (which is
 *    then of no effect) of it must be parsable as an whole number &gt; 0.
 *    Otherwise there will be an exception.</li>
 *  <li> daysOld<br />
 *    &nbsp; &nbsp; &gt; 0 sets den the time range in {@link #filCrit} to 
 *    &quot;from ... until days * 24 h before now&quot;.<br />
 *    If this property exists it must be &quot;nosetting&quot; (which is
 *    then of no effect) of it must be parsable as an whole number &gt; 0.
 *    Otherwise there will be an exception.</li>
 *  <li> minTime respectively since and maxTime respectively til<br />
 *    &nbsp; &nbsp; &gt; set the time range in {@link #filCrit}.<br />
 *    If one or both of these properties are there, they have to be 
 *    parsable according to the (ample) possibilities of
 {@link de.frame4j.time.TimeHelper#parse(CharSequence, boolean) TimeHelper.parse()}
 *    as a time. Otherwise an Exception will occur. The same will occur if the
 *    time boundary in case will also be defined by property days respectively
 *    daysOld.</li>
 *  <li> minLen and maxLen<br />
 *    &nbsp; &nbsp; &gt; set the length range in {@link #filCrit}.</li>
 *  </ul><br />
 *  Is one of the named properties not contained (non empty) in {@code prop} 
 *  (or prop itself is null) nothing happens in each case.<br />
 *  <br />
 *  @param prop a Map containing the properties to set. In applications / 
 *         tools this will most often be the {@link App}'s own PropMap
 *  @throws IllegalArgumentException in case of wrong or contradictory
 *         (like since as well as days) properties
 */
   public void set(final PropMap prop) throws IllegalArgumentException {
      if (prop == null) return;
      
      this.verbosity = Verbos.getVerbosity(
                                  prop.getString("verbosity", null));
      setCreateLowerCase(prop.getBoolean("lcNames", createLowerCase));
      setNoLCforTypes(prop.getString("noLCforTypes", noLCforTypes));
      
      setDelEmpty(  prop.getBoolean("delEmpty", delEmpty));
      setDelEmptySource( 
                    prop.getBoolean("delEmptySource", delEmptySource));

      setDelTree(   prop.getBoolean("delTree", delTree));
      setReverse(   prop.getBoolean("reverse", reverse));
      setBidirect(  prop.getBoolean("biDirect", biDirect));

      setRecursion( prop.getBoolean("recursion", recursion));
      setZoneSafe(  prop.getBoolean("zoneSafe", zoneSafe));
      setDifOld(    prop.getLong("difOld", difOld));
      setMakeDirs(! prop.getBoolean("noMd", !makeDirs));
      setNonew(     prop.getBoolean("noNew", nonew));

      String tmProp = prop.getString("ignoreCase");
      if (tmProp != null) {
        filCrit.ignoreCase = TextHelper.asBoolean(tmProp, filCrit.ignoreCase);
        dirCrit.ignoreCase = TextHelper.asBoolean(tmProp, dirCrit.ignoreCase);
      }
      
      tmProp = prop.getString("thoseDirs", null);
      if (tmProp != null) dirCrit.setWildName(tmProp);

      tmProp = prop.getString("omitDirs", null);
      if (tmProp != null) dirCrit.setExcludeNames(tmProp);
      tmProp = prop.getString("omitExtraDirs", null);
      if (tmProp != null) dirCrit.addExcludeNames(tmProp);

      tmProp = prop.getString("types", null);
      if (tmProp == null) {
         tmProp = prop.getString("thoseFiles", null);
      } else { // given as types  22.03.2016
         if (File.separatorChar != ';') 
            tmProp = tmProp.replace(File.separatorChar , ';');
         if (tmProp.indexOf(';') < 0) tmProp += ';'; // force type list syntax
      } // given as types; since 22.03.2016
      if (tmProp != null) filCrit.setWildName(tmProp);

      tmProp = prop.getString("omitFiles", null);
      if (tmProp != null) filCrit.setExcludeNames(tmProp);
      tmProp = prop.getString("omitExtraFiles", null);
      if (tmProp != null) filCrit.addExcludeNames(tmProp);

      String since = prop.getString("minTime", null);
      if (since == null) since = prop.getString("since", null);
      
      long now = -99999999;
      tmProp = prop.getString("days", "nosetting");
      if (!TextHelper.areEqual("nosetting", tmProp, true) ) {
         if (since != null)
            throw new IllegalArgumentException(
                     "multiple settings by keys \"since\" and \"days\"");
         int days = -1;
         try {
            days = Integer.decode(tmProp).intValue();
         } catch (Exception e) {
            throw new IllegalArgumentException(
                                         "malformed value for \"days\"");
         }

         if (days > 0) {
            now = System.currentTimeMillis();
            filCrit.minTime  = now - (days * ONE_DAY);
         } else {
            throw new IllegalArgumentException(
                      "incorrect value for \"days\"");
         }
      } // days given 
      if (since != null ) {
         try {
            filCrit.minTime  = TimeHelper.parse(since, false);
         } catch (Exception e) {
            throw new IllegalArgumentException(
                   "malformed time specification for \"minTime / since\"");
         }
      } // since

      String til = prop.getString("maxTime", null);
      if (til == null) til = prop.getString("til", null);
      tmProp = prop.getString("daysOld", "nosetting");
      if (!TextHelper.areEqual("nosetting", tmProp, true) ) {
         if (til != null)
            throw new IllegalArgumentException(
                   "multiple settings  by keys \"til\" and \"daysOld\"");
         int daysOld = -1;
         try {
            daysOld = Integer.decode(tmProp).intValue();
         } catch (Exception e) {
            throw new IllegalArgumentException(
                                     "malformed value for \"daysOld\"");
         }
         if (daysOld > 0) {
            if (now == -99999999) now = System.currentTimeMillis();
            filCrit.maxTime  = now - (daysOld * ONE_DAY);
         } else {
            throw new IllegalArgumentException(
                                    "incorrect value for  \"daysOld\"");
         }
      } // daysOld given
      if (til != null ) {
         try {
            filCrit.maxTime  = TimeHelper.parse( til, true);
         } catch (Exception e) {
            throw new IllegalArgumentException(
                   "malformed time specification for\"maxTime / til\"");
         }
      } // til

      filCrit.maxLen = prop.getLong("maxLen", filCrit.maxLen);
      filCrit.minLen = prop.getLong("minLen", filCrit.minLen);
   } // set(PropMap)

/** State as String. <br />
 *
 *  @return the properties as multi-line text
 */
   @Override public String toString(){
      StringBuilder bastel = new StringBuilder(500);
      bastel.append("de.frame4j.io.FileService  :  { \n");
 
      bastel.append("\n ** verbosity  = ").append(verbosity.toString());
      bastel.append("\n ** recursion  = ").append(recursion);
      bastel.append("\n ** createLowerCase = ").append(createLowerCase);
      if (createLowerCase && noLCforTypes != null) {
         bastel.append(" except for filenames if of type ")
                                                  .append(noLCforTypes);
      }
      bastel.append("\n ** delEmpty        = ").append(delEmpty);
      bastel.append("\n ** delEmptySource  = ").append(delEmptySource);
      bastel.append("\n ** delTree    = ").append(delTree);
      bastel.append("\n ** reverse    = ").append(reverse);
      bastel.append("\n ** biDirect   = ").append(biDirect);
      bastel.append("\n ** difOld     = ");
      if (difOld >= 0L) {
         bastel.append(difOld).append(" ms");      
      } else {
         bastel.append(" none");  
         if (difOld == -1L)  {
            bastel.append(" (xcopy)");  
         } else  bastel.append(" none");  
         if (difOld == -2L)  {
            bastel.append(" (no-replace)");  
         } 
      }

      bastel.append("\n ** makeDirs   = ").append(makeDirs);
      bastel.append("\n ** nonew      = ").append(nonew);
      bastel.append("\n\n ** FileCriteria    = ").append(filCrit);
      bastel.append("\n ** DirCriteria     = ").append(dirCrit);
      bastel.append("\n **  } // de.frame4j.io.FileService\n");
        return bastel.toString(); 
   } // toString()

//--------------------------------------------------------------------------

/** Helper for doUpdate(). <br />
 *  <br />
 *  This message formatter helper is used four times here.<br />
 *  <br /> 
 *  @param qDir    source 
 *  @param dDir    destination 
 *  @param wasMade destination  directory was just made 
 *  @return (inter) nationalised headline for reports 
 */   
   final static StringBuilder messHdl(String qDir, String dDir, 
                                                             boolean wasMade){
      Object[] param = new Object[] {qDir, dDir,
                              wasMade ? Boolean.TRUE : Boolean.FALSE};
      return AppLangMap.formMessageUL("filuprhdl",
         "\n From  {0}\n To {1}{2    (just made)?   (was there)}", param);
   } // messHdl

/** Working method for dating or backing up files and directories. <br />
 *  <br />
 *  According to the criteria and properties of this {@link FileService}
 *  object files will be copied from a source directory {@code soD} to a 
 *  destination directory {@code deD}.
 *  If ordered so, files before being replaced in the destination directory
 *  may be copied before to a backup directory {@code bcD}.<br />
 *  The property {@link #isReverse() reverse} if true will interchange
 *  the roles of destination and source, i.e. 
 *  {@code soD} and {@code deD}.<br />
 *  <br />
 *  Copying / updating can also be done {@link #isRecursion() recursively}
 *  including sub-directories. The than perhaps required making of
 *  destinations's new sub-directories would be done automatically, but
 *  can be forbidden.<br />
 *  <br />
 *  In the same manner the making of files not existing in the source 
 *  can be forbidden, restricting the service to the mere updating of 
 *  destination's existing files ({@link #isNonew() nonew}).<br />
 *  Taking the other extreme it is as well possible to prohibit all 
 *  overwriting of destination's existing files, restricting the service to
 *  just adding new files ({@link #getDifOld() noRepalce}).<br />
 *  <br />
 *  Otherwise that rule applies:<br />
 *  A file will copied from source to destination if it does not exist there
 *  or if it is sufficiently older than in source.<br />
 *  A value -1 of {@link #getDifOld() difOld} makes the copying independent
 *  of age (xcopy option).<br /> 
 *  If the source file has a length of zero bytes (bearing hardly any 
 *  information besides its existence and attributes) deleting can be chosen
 *  instead of copying (optionally deleting this 0 length source file 
 *  on success).<br />
 *  <br />
 *  Attention: If source and destination are on different computers 
 *  (network nodes) different time keeping strategies especially when
 *  (automatic) handling of daylight saving is involved can have quite 
 *  surprising effects on any update services including this one. Here
 *  {@link FileService#getDifOld() difOLd} and {@link #isZoneSafe() zoneSafe}
 *  can form a line of defence (of proven efficiency in many those
 *  circumstances).<br />
 *  Similar warning (and remedies) apply among others for removable disks, 
 *  CDS, DVDs, USB sticks or even partitions using different file systems and
 *  time formats / resolutions.<br />
 *  <br />
 *  A file that will be overwritten or deleted in the source directory will
 *  beforehand be copied to the backup directory if {@code bcD} if that is
 *  given.<br />
 *  <br />
 *  Hint on I/O  problems: The method {@link java.io.File File.list()} used 
 *  here to also on descending to sub-directories returns null on 
 *  I/O error. This will be logged if possible. That event will, of course,
 *  halt further descending recursively from this directory, just as if
 *  would contain no sub-directories (be that true or not).<br />
 *  <br />
 *  @param  soD source directory
 *  @param  deD destination directory
 *  @param  bcD backup directory  or null, if no &quot;two stage &quot; 
 *              backup wanted.<br /><br />
 *  @param  log for reports or null, if no logging at all is asked for
 *  @return total number of files copied or deleted
 */
   public final int doUpdate(File soD, File deD, File bcD,
                                                     final PrintWriter log){
      if (deD == null || (deD.exists() && !deD.isDirectory())) return 0;
      if (soD == null || !soD.exists() || !soD.isDirectory()) return 0;
      if (bcD != null &&  bcD.exists() && !bcD.isDirectory()) return 0;
      final boolean sil  = verbosity.isSilent() || log == null; 
     //  final boolean verb = verbose &&   log != null; // verbose + output
      final boolean test = verbosity.isTest();
      if (test && sil) return 0;   // no test runs without log 
      final boolean verbL = verbosity.verbose && !sil;
      final boolean mormL = !sil && verbosity.isNormal();
      
      File bFile;
      if (reverse) {
         bFile = soD;
         soD   = deD;
         deD   = bFile;
      } // source destination reverting
      
      /// log.println (" ////  TEST doUpdate( test = " +  test + " " +zvz);
      
      final String   qdir  = soD.getPath(); 
      String         zdir  = deD.getPath();
      if (createLowerCase) zdir = zdir.toLowerCase(); 
      final String   bDir  = bcD != null ? bcD.getPath() : null; 
      String[]  itsFiles   = soD.list(filCrit);
      if (itsFiles == null && log != null) { //  log the I/O-Error on listing
         log.println(AppLangMap.formMessageUL("filsflioe", null, qdir));
       }  //log the I/O-Error on listing the content
      String[] itsDirs    = null;
      if (recursion) { 
         itsDirs  = soD.list(dirCrit); 
         // list returns null on IO-Error (caught since 04.11.99)
         if (itsDirs == null) { //  log the I/O-Error on listing
            if (log != null) 
              log.println(AppLangMap.formMessageUL("filsdlioe", null, qdir));
                //   "Bei " + qdir + " Dir-Liste: I/O-Error");
         } else if (itsDirs.length == 0) itsDirs = null;
      } // recursion

      boolean qNzGemeld = false;
      boolean zMade     = false;
      int     ret       =     0;

      if (itsFiles != null && itsFiles.length != 0) { // there are files

         if (!deD.exists()) { // ensure existing destination directory
            if (test || !makeDirs) { // test: don't change / create anything
               if (!sil)   log.println(AppLangMap.formMessageUL(
                                                  "nodestdir", null, zdir));
                  /// log no make dest cause of test;
               return 0;
            } // making destination directory forbidden by test mode
            if (!deD.mkdirs()) {
             if (log != null && verbosity.isTest() )
              log.println(AppLangMap.formMessageUL("nomkdstdr", null, zdir));
                  ///  log no make dest cause of failure
             return 0;
            }
            qNzGemeld = verbosity.verbose;
            zMade      = true;
            if (verbosity.verbose && log != null) 
               log.println(messHdl(qdir, zdir, zMade));
         } // destination directory did not exist
         
         File qFile, zFile;
         long  qfm,  zfm,   bfm;      // for original last modified times
         int anz;
         boolean backed  = false;
         boolean delDeFil = false;

    /// -----  loop   fuD   is over the files    ---------------------------
         fuD: for (int i = 0; i < itsFiles.length; i++) { 
            final String listFileName = itsFiles[i];
            String zFileName = listFileName;
            qFile =  FileHelper.getInstance(qdir, listFileName);
            qfm   = qFile.lastModified();
            zfm   = bfm = -1L; 
            if (createLowerCase && // 15.11.2005 lo Case automatic
                  (noLCforTypes == null || !FileHelper.isOfType(qFile, noLCforTypes))) {
               zFileName = listFileName.toLowerCase();
            }
            zFile = FileHelper.getInstance(zdir, zFileName);
            delDeFil = delEmpty && (qFile.length() == 0L);
         //-- update criteria zFile
         //   no update if  nonew  and non existing
         if (!zFile.exists()) { // non existing
            if (nonew || delDeFil) {
               boolean qDel =                     // 27.03.2002
                    delEmptySource && delDeFil   // Q empty shall be deleted 
                       && (test || qFile.delete()); // not on TEST 28.05.05
               if (verbL) {
                  if (!qNzGemeld) {
                     log.println(messHdl(qdir, zdir, zMade));
                     qNzGemeld = true;
                  } 
                  StringBuilder b = FileHelper.infoLine(qFile, null, true, false);
                  if (nonew) {
                     b.append(" : no dest.");
                  } else {
                     b.append(qDel ? " : emptyDel" : " : empty S.");
                  }
                  log.println(b);
               } // verbose
               continue fuD;
            } // nonew or source length  0 and delEmpty
         } else {  //  source file exists
            if (difOld == -2L) { // noReplace
               if (verbL) {
                  if (!qNzGemeld) {
                    log.println(messHdl(qdir, zdir, zMade));
                    qNzGemeld = true;
                  } 
                  StringBuilder b = FileHelper.infoLine(zFile, null, true, false);
                  b.append(" : ").append("kept");
                  log.println(b);
               } // verbose
               continue fuD;  // no update, cause exists  and option noReplace
            } // noReplace
            zfm = zFile.lastModified(); 
            if (difOld >= 0L) {
               long diO = difOld; 
               final long dif = qfm - zfm; // + : source older; - : dest older
               if (zoneSafe && dif > 0 && (difOld < 3*ONE_HOUR)) {
                  if ((System.currentTimeMillis() - qfm)> 72*ONE_HOUR)
                       diO = 3*ONE_HOUR;
               } // time zone switch precaution
               if (diO >= dif) { 
               //--- Z. exists and no update, cause younger | not older enough 
                 if (verbL) {
                    if (!qNzGemeld) {
                      log.println(messHdl(qdir, zdir, zMade));
                      qNzGemeld = true;
                    } 
                    StringBuilder b = 
                                FileHelper.infoLine(qFile, null, true, false);
                    b.append(" : ").append(dif >= 0 ? "uptodate" : "older");
                    if (dif < -1000L) {
                       b.append("...\n     // ^^ //    ...  destination is ");
                       TextHelper.formatDuration(b, -dif).append(" younger ");
                    }    
                    log.println(b);
                 } // verbose
                 continue fuD;  // no update, cause younger | not older enough 
               } // no update cause of ...
            } 
          } // destination file is already there (no copy as new)

         /// -- to here only with update s to d cause not yet exists or older
         ///    or because difOld == -1L (xCopy behaviour)
            backed = false;
            if (zFile.exists() && bcD != null) { // with backup
              if (!bcD.exists()) {
 
                  if (test || !bcD.mkdirs()) { // not on TEST. 20.05.05
                      if (log != null)
                         log.println ("Backup directory  "
                            + bDir + " can't be made.");
                      return ret;
                  } else if (verbL) 
                      log.println ("\n Backup  " + bDir + " (made)");
              } // bcD did not yet exist
              if (bcD.isDirectory()) { // 15:00 15.09.99
                 bFile = new File(bDir, zFileName);
                 // backup,  if not exists or older there 
                 if ((!bFile.exists()) // not yet there or 
                     || difOld <= 0L  ||  // no age constraint                    
                     ((bfm = bFile.lastModified()) < (zfm-difOld)) ) { // back
                    int a = -1;
                    if (test) {
                       backed = true; //no effect in TEST 20.05.2005
                    } else try { 
                       Input ein = new Input(zFile);
                       a= FileHelper.copyFrom(ein, bFile, OutMode.OVERWRITE);
                       if (a >= 0) {
                          backed = true;
                          ++ret; // file made / changed
                       } else if (a == -3) {  // IOException of copyFrom
                          bFile.delete(); // clean
                       }
                       ein.close(); // since 17:50 13.01.99
                    } catch (Exception e) {}
                 } // back
              } // Backup exists and is a directory  
            } // with backup

            try {
               boolean aufr = true;
               anz = 0;
               if (!delDeFil) {  // do copy
                  if (test) {   // no effect on  TEST
                     anz = (int)(qFile.length());
                  } else {
                     Input ein = new Input(qFile);
                     if (zFile.exists()) { // delete before copy
                        zFile.delete();
                     }  // delete before copy cause of name case
                     anz =  FileHelper.copyFrom(ein, zFile, OutMode.OVERWRITE); // Exc-3
                     ein.close(); // since 08:29 23.03.99
                  }
                  if (anz >= 0) ++ret;
               } // do copy
               if (delDeFil || (anz == -3)) {  // delZiel -3 = Exception
                  if (test) {
                     aufr = true; //no effect in TEST
                  } else  {
                     aufr = zFile.delete(); // true if delete succ.
                     if (aufr && delDeFil && delEmptySource)
                         qFile.delete(); // 27.03.2002
                     if (aufr &&  (anz >= 0)) ++ret;
                  }
               } // delZiel 


              if (!sil && (verbL || anz < 0 || mormL && !zMade)){
                 if (!qNzGemeld) {
                      log.println(messHdl(qdir, zdir, zMade));
                      qNzGemeld = true;
                  } 
                  StringBuilder b = FileHelper.infoLine(qFile, null, true, false);
                  b.append(" : ");
                  if (anz < 0) {
                     b.append("Error ").append(aufr ? -anz : anz);
                  } else if (anz > 0 ) {
                     b.append("OK");
                  } else {
                     b.append(delDeFil ? "DelL0" : "Len=0");
                  }
                  if (backed) b.append("+back");
                  log.println(b);
/*XXXX       log.println(qFile.infoLine(true, false) + " : "  
                       + (anz<0 ? "Error " + (aufr ? -anz : anz)
                                : anz > 0 ? "OK": 
                                         delZFile ? "DelL0" : "Len=0") +
                       (backed? "+back":"")   );   XXXX */
                  if (verbosity.isTest()  && zfm >= 0) {
                      log.println (" //.. Q(+0s) --> Save("+
                                        ((zfm - qfm) / 1000)+"s)"+
                          ((backed && bfm >= 0)?" --> Back("+
                                       ((bfm - zfm) / 1000)+"s)":""));
                  }
               } 
            } catch (Exception e) {
               if (log != null) {
                  if (!qNzGemeld) {
                     log.println(messHdl(qdir, zdir, zMade));
                      qNzGemeld = true;
                  }
                  StringBuilder b = FileHelper.infoLine(qFile, null, true, false);
                  b.append(" : Error ...\n   ").append(e.getMessage());
                  log.println(b);
               }
            }
         } // for over the files

      } // there are files in the directory

      if (itsDirs == null) return ret;

      ////----  work on  (sub) directories  -------------  
      
      File quvz = null;
      File zuvz = null;
      File buvz = null;

       for (int i = 0; i < itsDirs.length; i++) { 
           String itDir = itsDirs[i];
           if (createLowerCase) itDir = itDir.toLowerCase(); 
           quvz = new File(qdir, itDir);
           zuvz = new File(zdir, itDir);
           if (bcD != null) buvz = new File(bDir, itDir);
         if (reverse){
            bFile = quvz;
            quvz = zuvz;
            zuvz = bFile;
         } // reverse the internal source / destination interchange for call
           ret += doUpdate(quvz, zuvz, buvz, log);
       }
      return ret;
   } // doUpdate(3*File, PrintWriter)

//------------------------------------------------------------------------

/** Working method to clean up files and directories. <br />
 *  <br />
 *  According to the criteria and properties of this {@link FileService}
 *  object files will be deleted in the destination directory {@code deD} if 
 *  the related file does not exist in the source directory {@code soD}.<br />
 *  If a {@code soD} directory is not specified or does not exist, the latter
 *  condition is regarded as fulfilled: the file will be deleted.<br />
 *  <br />
 *  If ordered so files, before being deleted in the destination directory
 *  may be copied before to a backup directory {@code bcD}.<br />
 *  The deleting can also be done {@link #isRecursion() recursively}
 *  including sub-directories.<br />
 *  <br />
 *  The property {@link #isReverse() reverse} if true will interchange
 *  the roles of destination and source, i.e. {@code soD} and 
 *  {@code deD}.<br />
 *  <br />
 *  {@code deD} must denote an existing directory.<br />
 *  {@code soD} and {@code bcD} may be null, denote and existing or not 
 *  existing directory, but never an existing file.<br />
 *  
 *  @see  #doUpdate doUpdate()
 *  @since 31.05.2002
 *  @param  soD source directory (acts quasi as list of files and directories 
 *              NOT to be deleted)
 *  @param  deD destination directory (it's here where the cleaning 
 *              work is done)
 *  @param  bcD backup directory for files deleted in deD or null, if no two 
 *              stage clean up is asked for. (must not be a file!)
 *  @param  log for reports or null, if no logging at all is asked for
 *  @return total number of files copied or deleted
 */
   public final int doClean(File soD, File deD, File bcD,
                                                      final PrintWriter log){
      File bFile;
      if (reverse) {
         bFile = soD;
         soD   = deD;
         deD   = bFile;
      } // source destination reverting

      if (deD == null || !deD.exists() || !deD.isDirectory()) return 0;
      if (bcD != null &&  bcD.exists() && !bcD.isDirectory()) {
         if (log != null) 
            log.println(AppLangMap.formMessageUL("bckupndir", null, 
                              FileHelper.pathName(bcD, null, false, true)));
            //"Backup " + bvz.pathName(false, true) + " is no directory");
          return 0;
      }  /// Dirs
      boolean noPtDir = soD == null || !soD.exists();
      if (!noPtDir && !soD.isDirectory()) { /// no delete pattern directory
         if (log != null) 
            log.println(AppLangMap.formMessageUL("soulndir", null, 
                              FileHelper.pathName(soD, null, false, true)));
         ///"Source / List " + lvz.pathName(false, true) + " is no directory");
          return 0;
      }  /// no delete pattern directory
      
      final boolean verb = verbosity.verbose && log != null;   // verbose + output
      final boolean test = verbosity.isTest();
      if (test && !verb) return 0; // no TEST run without log
      
      final String  bDir = bcD == null ?  null :  bcD.getPath();
      final String  pDir = noPtDir ?  null :  soD.getPath();
      final String  dDir  = deD.getPath();

      String[] itsFiles   = deD.list(filCrit);
      if (itsFiles == null) {
         if (log != null) 
            log.println(AppLangMap.formMessageUL("filsflioe", null, dDir));
            /// log.println ("At " + dDir + " file list I/O-Error");
      } else if (itsFiles.length == 0)
         itsFiles = null;

      String[] itsDirs    = null;
      if (recursion) {
         itsDirs  = deD.list(dirCrit); 
         if (itsDirs == null) { // list returns null bei IO-Error
            if (log != null) 
              log.println(AppLangMap.formMessageUL("filsdlioe", null, dDir));
              // log.println ("at " + dDir + " dir list : I/O-Error");
         } else if (itsDirs.length == 0)
            itsDirs = null;
      } // recursion

      boolean qNzGemeld = false;
      int     ret       =     0;

      if (itsFiles != null) { // files exist (work on)

         File  lFile, dFile;
         /// bFile = null;
         boolean delIt   = false;

    /// ----- loop over the files   --------------------------
         fuD: for (String loFiNa :  itsFiles) { 
            dFile = FileHelper.getInstance(dDir, loFiNa);
            delIt = noPtDir;
            if (!delIt) {
               lFile = FileHelper.getInstance(pDir, loFiNa);
               delIt = !lFile.exists() || lFile.isDirectory();
            }
            boolean backed  = false;
            boolean deleted = false;
            boolean backIt  = delIt && bcD != null;

            if (backIt) { // with backup
               if (!bcD.exists()) {
                  if (test || !bcD.mkdirs()) { //no effect in TEST
                      if (log != null)
                         log.println ("Backup directory "
                          +  bDir + " can't be made.");
                      return ret;
                  } else if (verb) 
                      log.println ("\n Backup  " + bDir + "(made)");
               } // bvz din not yet exist
               bFile = new File(bDir, loFiNa);
               if (test) { // TEST only pretend;  do nothing ...
                  backed = true;
                  ++ret; // ... but count
               } else try { 
                  Input ein = new Input(dFile);
                  int a = FileHelper.copyFrom(ein, bFile, OutMode.OVERWRITE);
                  if (a >= 0) {
                     backed = true;
                     ++ret; // one file made / changed
                  } else if (a == -3) {  // IOException at copyFrom
                     bFile.delete(); // clean up
                  }
                  ein.close();
               } catch (Exception e) {}
            } // back

            StringBuilder fInfo = null;
            if (delIt && (bcD == null || backed)) { // may be deleted
               fInfo = FileHelper.infoLine(dFile, null, true, false); // before delete
               deleted = test //no effect in TEST 
                        ||  dFile.delete(); // true if delete succ.
               if (deleted) ++ret;
            } // may be deleted
               
            if ((verbosity.verbose || (deleted && verbosity.isNormal())
                           || backIt && !backed)  && log != null) {
               if (!qNzGemeld) {
                  log.println ("\n Clean  " + dDir + (pDir == null ? ""
                          :    "\n like   " + pDir  ) 
                          + ( bDir == null ?  "\n"
                          :    "\n backup " + bDir +"\n"));
                  qNzGemeld = true;
               } 
               if (fInfo == null)
                   fInfo = FileHelper.infoLine(dFile, null, true, false);
               fInfo.append(deleted ? " : deleted" : " : not deleted");
               if (backIt)
                  fInfo.append(backed ? " + backup" : ", backup failed");
               log.println(fInfo);
              
                   ///     + (deleted ? " : deleted" : " : not deleted")
                      ///  + (backIt 
                         ///  ? backed ? " + backup" : ", backup failed" 
                           ///: ""   ));

            } // log
         } // for fuD
      } // files exist (work on)

      ////----  work on  (sub) directories  --------------  

      if (itsDirs != null) { // directories exist
         for (String itDir : itsDirs) { 
           File luvz = pDir == null ? null : new File(pDir, itDir);
           File duvz = new File(dDir, itDir);
           File buvz = bDir == null ? null : new File(bDir, itDir);
           if (reverse){
              bFile = luvz;
              luvz = duvz;
              duvz = bFile;
           } // reverse the internal source / destination interchange for call

           ret += doClean(luvz, duvz, buvz, log);
         } // for
      } // directories exist

      ///--- if directory now empty ... kill  -------     

      if (delEmpty ) {
         try {
            if (test || deD.delete()) { // no effect if test
              ++ret;
              if (log != null) {
                 log.println ("\n Clean  " + dDir + " empty, deleted");
              }
            } 
         } catch (Exception e) {}
      } 
      return ret;
   } // doClean(3*File, PrintWriter)

//------------------------------------------------------------------------

/** This FileVisitors working method to visit files and directories. <br />
 *  <br />
 *  If {@code path} and {@code name} denote exactly any one file it will be 
 *  visited calling {@code  filBes.visit()} if {@code fileBes} is not null.
 *  &quot;Any one file&quot; in this means all that could mean an existing or
 *  not existing file but not an existing directory.<br />
 *  <br />
 *  If {@code path} and {@code name} denote an existing directory all files
 *  matching {@link #filCrit} and all directories matching {@link #dirCrit} 
 *  are determined. (Its an internal snapshot of that diretory's state.)
 *  Then the following is done:<ol>
 *  <li>If these snapshot lists are both empty emptyDirBes.visit() will be 
 *      called for that regarded (start) directory. And that's all.</li> 
 *  <li>dirBes.visit() will be called for that regarded (start) directory as
 *      a &quot;pre-visit&quot;.</li>
 *  <li>filBes.visit() will be called for all the listed files 
 *      (matching {@link #filCrit}).</li>
 *  <li>If (@link {@link #isRecursion() recursion} is false dirBes.visit()
 *      will be called for all directories in the snapshot list (matching
 *      {@link #dirCrit}) be they empty or not.<br />
 *      The next step will be omitted.</li>
 *  <li>If (@link {@link #isRecursion() recursion} is true the whole 
 *       proceeding will be done for every directory in  the snapshot list
 *       (matching {@link #dirCrit}). (That is done, of course, by 
 *       by calling this method  recursively.)</li>
 *  <li>At last after visiting all matching files and directories contained 
 *      dirBes2.visit() will be called for that regarded (start) directory as
 *      a &quot;post-visit&quot;.</li></ol>
 *  
 *  What is done on the visited files or directories is totally the matter 
 *  (and responsibility) of the supplied one to four visitors, some of which
 *  might of course be null or the same if no or equal action is wanted.<br />
 *  <br />
 *  @param path &#160; the start (parent) path (not null!)<br />
 *  @param name &#160; the start file or directory; may be null if completely
 *              included in path
 *  @param fileBes &#160; implements the action on each file in case (matching
 *              {@link #filCrit})
 *  @param emptyDirBes &#160; implements the action on the (start) directory 
 *              if it contains neither files nor directories matching the 
 *              criteria ({@link #dirCrit} or {@link #filCrit}); 
 *              &quot;empty&quot;  means nothing with respect to those criteria.
 *  @param dirBes &#160; implements a) on one hand implements the action on 
 *              the (start) directory if it is not empty in the above 
 *              sense.<br /> 
 *              b) On the other hand dirBes implements the action on each 
 *              directory matching {@link #dirCrit}.<br />
 *              Hint: Recursive descend 
 *              is organised by this method and <u>not</u> the task of the 
 *              visitor dirBes (that will be called in role a) in that case).
 *  @param dirBes2 &#160; implements the action on the (start) directory do be
 *              done after returning from all file and / or directory visits
 *              on its content.
 */
   public final void dirVisit(final String path, final String name, 
          final FileVisitor fileBes,
          final FileVisitor emptyDirBes,
          final FileVisitor dirBes,
          final FileVisitor dirBes2){

      if (fileBes == null && emptyDirBes == null && dirBes == null // no task
            && dirBes2 == null) return; // .. save all work and exceptions  

      File dD = FileHelper.getInstance(path, name); // Start directory ? 
      if ( !dD.exists() || !dD.isDirectory()) {
         if (fileBes != null) fileBes.visit(dD); // visit the file
         return;  // perverted case "single visit": just one file specified
      }  // single file case
      
      // from here on normal start on an existing directory + tasks / visitors
      final String dir = dD.getPath(); 
      File theFile =  null;
      String[] itsFiles = null;
      int iFL = 0;
      String[] itsDirs  = null;
      int iDL = 0;
      if (filCrit.isAllowDir() || filCrit.isAllowFile()) {
          itsFiles = dD.list(filCrit);
          if (itsFiles != null) iFL = itsFiles.length;
      }
      if (dirCrit.isAllowDir()) {
          dirCrit.setAllowFile(false);
          itsDirs  = dD.list(dirCrit); // only real directories
          if (itsDirs != null) iDL = itsDirs.length;
      }

      if (iFL == 0 && iDL == 0) {
         if (emptyDirBes != null)
            emptyDirBes.visit(dD);  // visit the empty directory (if wanted) 
         return;
      } // empty directory   
         
      if (dirBes != null)
         dirBes.visit(dD);  // visit non empty directory on arrival
      
      if (fileBes != null) { // (real) files are to be visited
         for (int i = 0; i < iFL; i++) {
            theFile = FileHelper.getInstance(dir, itsFiles[i]);
            fileBes.visit(theFile);
         }
      }  // (real) files are to be visited
         
      if (iDL != 0) { // there are sub directories
         if (recursion) {  // recursive descend
            for (int i = 0; i < iDL; i++) {
               dirVisit (dir, itsDirs[i], 
                  fileBes, emptyDirBes, dirBes, dirBes2);
            } // for
         } else { // recursion / no recursion
            if (dirBes != null) 
               for (int i = 0; i < iDL; i++) {
                  theFile = FileHelper.getInstance(dir, itsDirs[i]);
                  dirBes.visit(theFile); // even empty ones !
            }
         } // no recursion
      } // there are sub directories

      if (dirBes2 != null)  // on return from content visiting
         dirBes2.visit(dD);  // visit the (previous non empty) directory (post)
 
   } // dirVisit(2*String, 4* FileVisitor)

/** Working method to visit files and directories. <br />
 *  <br />
 *  The call is equivalent to<br /> &nbsp; &nbsp;
 {@link #dirVisit(String, String, FileVisitor, FileVisitor, FileVisitor, FileVisitor)
 *  dirVisit}(path, name, fileBes, emptyDirBes, dirBes, null)<br />
 *  as shorter notation for cases without &quot;post visitor&quot; (on tree 
 *  ascend) on non mepty directories.<br />   
 */
   public final void dirVisit(String path, String name, 
            FileVisitor fileBes, FileVisitor emptyDirBes, FileVisitor dirBes){
      dirVisit(path, name, fileBes, emptyDirBes, dirBes, null);
   } // // dirVisit(2*String, 3* FileVisitor)


//-------------------------------------------------------------------------

/** Produce a listing visitor (a FileVisitor factory). <br />
 *  <br />
 *  The {@link FileVisitor} object made by this method just lists all visited
 *  {@link File}s on the PrintWriter provided.<br />
 *  The returned {@link FileVisitor}'s method 
 *  {@link FileVisitor#visit(File) visit(File dD)} returns 0 as 
 *  normal, -1 if {@code pos} is null and -2 if the provided {@link File}
 *  {@code dD} is null.<br />
 *  <br />
 *  @param pos the list output (null is accepted but makes no sense)
 *  @return a listing FileVisitor
 *  @see FileVisitor
 *  @see FileHelper#listLine(File)
 */
   public static FileVisitor makeOutputListVisitor(final PrintWriter pos){
       return  new FileVisitor() {
          @Override public int visit(File dD) {
             if (pos == null) return -1;
             if (dD == null) return -2;
             pos.println(FileHelper.listLine(dD));
             return 0;
          }
       };
   } // makeOutputListVisitor(PrintWriter)

/** Produce an informing visitor (a FileVisitor factory). <br />
 *  <br />
 *  The {@link FileVisitor} object made by this method just lists all visited
 *  {@link File}s on the PrintWriter provided.<br />
 *  The returned {@link FileVisitor}'s method 
 *  {@link FileVisitor#visit(File) visit(File dD)} returns 0 as 
 *  normal, -1 if {@code pos} is null and -2 if the provided {@link File}
 *  {@code dD} is null.<br />
 *  <br />
 *  @param pos the list output (null is accepted but makes no sense)
 *  @return a listing FileVisitor
 *  @see FileVisitor
 *  @see FileHelper#infoLine(File)
 */
   public static FileVisitor makeOutputInfoVisitor(final PrintWriter pos){
       return  new FileVisitor() {
          @Override public int visit (File dD){
             if (pos == null) return -1;
             if (dD == null) return -2;
             pos.println(FileHelper.infoLine(dD));
             return 0;
          }
       };
   } // makeAusgabeInfoBesucher(PrintWriter)
   

/** Produce a collecting visitor (a FileVisitor factory). <br />
 *  <br />
 *  The {@link FileVisitor} object made by this method just puts all visited
 *  {@link File}s into the Collection provided.<br />
 *  The returned {@link FileVisitor}'s method 
 *  {@link FileVisitor#visit(File) visit(File dD)} returns 0 as 
 *  normal, -1 if {@code vec} is null and -2 if the provided {@link File}
 *  {@code dD} is null. null is not put into {@code  vec}.<br />
 *  <br />
 *  @param vec the collecting container
 *  @param noDoub do not enter a {@link File} twice; i.e. enforce set
 *         behaviour even if {@code  vec} if not of type {@link Set}
 *  @return a listing/collecting {@link FileVisitor}
  */
   public static FileVisitor 
                     makeInCollectionVisitor(final Collection<File> vec,
                                                      final boolean noDoub){
       return  new FileVisitor(){
          final boolean guard = noDoub && !(vec instanceof Set);
          @Override public int visit (File dD) {
             if (vec == null) return -1;
             if (dD == null) return -2;
             if (guard && vec.contains(dD)) return -3;
             vec.add(dD);
             return 0;
          }
       };
   } // makeInCollectionVisitor(Collection, boolean)

} // FileService (10.08.2003, 02.02.2006, 19.12.2008, 26.06.2015, 22.03,2016) 
