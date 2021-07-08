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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import de.frame4j.io.FileHelper;
import de.frame4j.io.FileService;
import de.frame4j.io.FileVisitor;
import de.frame4j.io.Input;
import de.frame4j.io.OutMode;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.util.Prop;
import de.frame4j.text.TextHelper;
import de.frame4j.text.RK;
import de.frame4j.text.CleverSSS;

/** <b>Find texts in files and replace them</b>. <br />
 *  <br />
 *  This application visits text files in a start directory and optionally 
 *  (option&nbsp;-r) also recursively in sub-directories. All file and 
 *  directory criteria available for Frame4J applications may be used.<br />
 *  <br />
 *  In every file visited all occurrences of old text patterns are searched
 *  for and replaced by a new or substitute pattern. The old pattern may be
 *  specified as single text (properties &quot;old&quot;), in the sense 
 *  of:<br /> &nbsp;
 *  replace every occurrence of &quot;&lt;br&gt;&quot; (old) by  
 *  &quot;&lt;br \&gt;&quot; (new).<br />
 *  <br />
 *  On the other hand every text pattern to be replaced can be specified
 *  as bracketed by two sequences (old and oldEnd) with arbitrary content in 
 *  between, in the sense of:<br /> &nbsp;
 *  replace everything between 
 *  &quot;&lt;!--&quot; (old) and &quot;--&gt;&quot; (oldEnd) by
 *  nothing &quot;&quot; (new).<br />
 *  The regular expression for the example would be 
 *  &quot;&quot;&lt;!--&quot;.*&quot;--&gt;&quot;&quot;.<br />
 *  <br />
 *  One single replacement task (for every specified file) <br /> 
 *  &nbsp; oldText, [oldEnd,] newText<br />
 *  can be specified by start parameters (command line / script). The 
 *  application can also handle up to 188 replacement commands/pattern
 *  specified in an extra .properties or up to 999 replacements 
 *  ([de-] hyphenations) given in a hyphenation specification file.
 *  These replacements are worked through for every text file content
 *  sequentially (in the defined order).<br />
 *  The properties<br /> &nbsp;
 *  old, [end,] new|newFile [ignoreCase] [ignoreWS] [keepBraces] <br />
 *  are the given in the form <br /> &nbsp;
 *     old0, [end0,] new0 to (max.) old150, [end150,] new150 <br />
 *  governed by {@link Prop}'s syntax for indexed properties (new150 and
 *  new[150] would both be recognised, e.g.).<br /> 
 *  <br />
 *  The meaning hereby is<ul>
 *  <li>newFile instead of new: the associated substitute text is not
 *       given directly but read from the textfile named. <br />
 *       Multiply named files &mdash; newFile132=partner_links.txt and
 *       newFile[17]=partner_links.txt for example &mdash; are read only
 *       once. Such a file not being readable, aborts the application.</li>
 *  <li>useNewModif: if true (and {@link #keepFileDate} = true) the 
 *       modification date of a modified file will be set to the maximum
 *       of it's own old file date and that of all used replacement files
 *       for which that property is true. (In other words: it's possible
 *       if a newer file is inserted into an older one, to set the 
 *       newer include file's date.)</li>
 *  <li>ignoreCase, ignoreWS: ignore case respectively embedded white
 *       space bay matching old or old-oldEnd-braces.</li>
 *  <li>keepBraces: set old and oldEnd (both search braces if given) in front
 *       and at the end of the substitute text (new or newFile content).</li>
 *  </ul> <br />
 *  <b>Remark 1</b>: The newFile/keepBraces feature is heavily used to
 *  get a &quot;include files&quot; for languages without that
 *  feature (like HTML respectively Java). See also the method 
 {@link TextHelper#fUr(CharSequence, StringBuilder, CleverSSS, CleverSSS, String)
 *  TextHelper.fUr(CharSequence, ..)} used, of course, in this application's
 *  {@link FileVisitor}s.<br />
 *  <br />
 *  <b>Remark 2</b>: Obviously the application does not use regular
 *  expressions for text matching. But the bracing and ignore white space
 *  features, when used well, are a good, robust and performing substitute
 *  in most cases. They give enough freedom in white space ignoring 
 *  languages like HTML, XML and Java.<br />
 *  <br />
 *  This application's real power shows up when doing many dozens of 
 *  replacements in many hundred files, selected by complex criteria, in one
 *  step. Voluminous changes and maintenance work on web spaces have been 
 *  specified in one .properties file and done orders of magnitude faster and
 *  more robust than by script programmed nested loops and 
 *  transformations.<br />    
 *  <br />
 *  Besides these big
 *  &quot;some 100 replacements times hundreds of files&quot; use cases,
 *  this application also does the little every day things.
 *  <b>Examples</b>:<br />
 *  {@code java FuR -i .\+.java -r de.weaut de.weAut }<br />
 *  &#160; &#160; replaces in all files *.java in the actual tree the
 *  text de.weaut in any case by the same text in the correct case.<br />
 *  <br />
 *  {@code java FuR .\+.html "<br>" "<br />" -i -r}<br />
 *  &#160; &#160; replaces in all .html files frumpish breaks by the
 *  good ones loved by XML.<br />
 *  <br />
 *  {@code java FuR jdk1.3\docs\de\frame4j\+.html api\ api/ -i -v}<br />
 *  &#160; &#160; replaces in all .html files &#160; 
 *  <code> ap&#105;\&#160;</code> 
 *  by &#160;  <code> api/</code> &#160; so repairing a fault done by quite
 *  many javaDoc.exe versions in Java3 days when doing relative linking.<br />
 *  <br />
 *  <b>[De-] hyphenation</b>: A special find and replace feature is
 *  replacing words without soft hyphen entities by words with them,
 *  like<br /> &nbsp; &nbsp;
 *  {@code Feuerwehrhauptmann ==>  Feu&shy;er&shy;wehr&shy;haupt&shy;mann}
 *  &nbsp; &ndash; or the other way round.<br />
 *  One only needs a hyphenation definition file with one
 *  {@code &shy;} hyphenated word per per line made according to the example  
 *   <a href="./doc-files/hyphTest.txt" target="_top">hyphTest.txt</a>.<br />
 *  The hyphenation definition file's default encoding is UTF-8, everywhere;
 *  use options {@code -hyphEnc ISO8859-1 }, e.g. or 
 *  {@code -hyphISO1 } to change.<br />
 *  <br />
 *  <br /> 
 *  <b>Hint 1</b>: In some cases, like text and layout files for jekyll, it
 *  might be advisable to exclude the front matter from all text changes by 
 *  {@link FuR} (use option -omitFrntMt).<br />
 *  <br />
 *  <b>Hint 2</b>: The file FuR.properties is an integral part of this 
 *  application. It's placed in the same directory (usually within the 
 *  deployment .jar) as FuR.class. The file  
 *  <a href="doc-files/FuR.properties" target="_top">FuR.properties</a>
 *  may be regarded as part of the documentation.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a> 
 *  Copyright 2000 - 2004, 2009, 2015  &nbsp; Albrecht Weinert <br />
 *  <br />
 *  @see     App
 *  @see     AppBase
 *  @see     Prop
 */
 // so far   V00.02 (02.10.2001) :  multiple
 //          V00.10 (22.12.2001) :  Bracket old end
 //          V00.20 (22.12.2001) :  parse partial, space is possible
 //          V00.21 (03.05.2002) :  Datei.Criteria.parse
 //          V01.31 (28.06.2002) :  dir, old, [oldEnd,] new
 //          V02.00 (23.04.2003) :  CVS Eclipse
 //          V02.05 (09.06.2003) :  New package structure
 //          V02.13 (29.04.2004) :  CVS bug repair,  prep. regex
 //          V02.20 (19.05.2005) :  utilise Prop's enhancements
 //          V.10   (19.11.2008) :  cvsNT -> SVN; SVN version saltoes
 //          V.o23+ (25.11.2008) :  keepFileDate keepBraces
 //          V.o69+ (11.02.2009) :  -useReplDate
 //          V.o29+ (15.05.2012) :  null pointer bug (1  w/o propfile)
 //          V.135+ (25.06.2015) :  -test now working (no file modif.)
 //          V.137+ (06.01.2016) :  FileHelper
 //          V.003+ (06.01.2017) :  SVN new on Ubuntu, (hence) filModEnc
 //          V.  51 (03.07.2021) :  hyphenation de-hyphenation (experimental)

@MinDoc(
   copyright = "Copyright 2000 - 2009, 2015, 2017  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 57d $",
   lastModified   = "$Date: 2021-07-08 $",
   usage   = "start as Java application (-? for help)",  
   purpose = "find and replace multiple texts in multiple files"
) public class FuR extends App {

/** No objects. */
   private FuR(){}

/** FuR requests only partial start parameter parsing by Prop. */
   @Override public boolean parsePartial(){ return true; }

/** Start method of FuR . <br />
 *  <br />
 *  Return code:<br />
 *  &nbsp; Exit = 0, if run successfully, <br />
 *  &nbsp; Exit &gt;0  error(s) while running,<br />
 *  &nbsp; Exit &gt;90 parameter or start errors; nothing done.<br />
 *  <br /> 
 *  @param args Command line start parameters
 */
   public static void main(final String[] args){
      try {
         new FuR().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

//-----  Properties, automatic set by Prop.parse() ----------------------
   
// hyphenation de-hyphenation (03.07.2021, experimental)
/** Hyphenate or de-hyphenate. <br />
 *  <br />
 *  If one of {@link #hyphen} or {@link #deHyphen} is {@code true} FuR gets
 *  its old / new find and replace pattern from the text file 
 *  {@link #hyphFile} only.<br />
 *  If both are true the program ends with an error message.
 *  @since July 2021  
 */
   public boolean hyphen, deHyphen;
   
/** Path of the hyphenation definition file. <br />
 *  <br />
 *  This is a strict one hyphenated (by {@code &shy;}) word per line text
 *  file:<pre>
 *  {@code Klas&shy;sen&shy;ver&shy;band}
 *  {@code Feuer&shy;wehr&shy;haupt&shy;mann}
 *  {@code twelve&shy;year&shy;old}
 *  {@code anx&shy;ious}
 *  {@code stu&shy;pid}</pre><br />
 *  Note 1: In most cases, it is better to have language specific hyphenation
 *  definition files.<br />
 *  Note 2: If you happen to have a good hyphenation definition file having
 *  hyphens {@code -;} instead of the XML entity {@code &shy;}, let this
 *  program {@link de.frame4j.FuR} replace the first with the latter:<br />
 *  &nbsp; {@code java de.frame4j.FuR myHyphFile.txt "-" "&shy;" -v}
 *  
 *  @see #hyphen
 *  @see #deHyphen
 */
   public String hyphFile;

/** Encoding of the hyphenation definition file. <br />
 *  <br />
 *  Default: UTF-8
 *  @see #hyphFile   
 */
   public String hypFilEnc = "UTF-8";
   
/** Omit front matter. <br />
 *  <br />
 *  If true a jekyll front matter is searched for in each file to be
 *  processed and, if found, left untouched.<br />
 *  default: false
 *  @see TextHelper#isFrntMttr(CharSequence) 
 */
   public boolean omitFrntMt;

/** Recursively visit sub-directories. <br />
 *  <br />
 *  If true also files in sub-directories are considered and if applicable
 *  worked on.<br />
 *  <br />
 *  default: false
 */ 
   public boolean recursion;

/** Search text (begin). <br /> 
 *  <br />
 *  This is the complete search (or old) text or the opening brace for a spot
 *  to be replaced. <br />
 *  @see #oldEnd
 */
   public String  oldText;
  
/** Search text (end). <br />
 *  <br />
 *  Is {@link #oldEnd} not null or empty, then {@link #oldText} and 
 *  {@link #oldEnd} form braces to find a spot for replacement. An
 *  exemplary use would be two distinct HTML comments to mark a spot for
 *  inserting or replacing a new text (block).<br />
 *  <br />
 *  Note that the new text(s) may be given as (text) value, also multi-line,
 *  or as a path to a file. The difference is specified by the name of
 *  the (indexed) property: new[32] versus newFile[32]<br />
 *  <br />
 */
   public String  oldEnd;

/** Replacement text; substitute. */
   public String  newText;

/** Use the modification time of the replacement text or substitute. <br />
 *  <br />
 *  If useNewModif is true and if the replacement text has a modification
 *  date (because read from a file) and if the modification date is to be kept 
 *  ({@link #keepFileDate} is true)<br />
 *  then the maximum of the file modification dates is used as the (new)
 *  modification date.<br />
 *  <br />
 *  Note: this single property useNewModif is used as default for all
 *  indexed properties useNewModif[32]. <br />
 *  Note 2: These properties have no effect if {@link #keepFileDate} is 
 *  false.<br />
 *  <br />
 *  default: true 
 */
   public boolean  useNewModif = true;

/** Keep the file date even after replacements. <br /> 
 *  <br />
 *  default: true <br />
 *  @see #incFileDate
 */
   public boolean keepFileDate = true;   
   
/** Add (only) 1/2 s the last file in case of replacements. <br />
 *  <br />
 *  Like @link {@link #keepFileDate}, but with a tiny increase of the last
 *  file modification date.<br />
 *  The purpose is a sticking to the last otherwise done file modification
 *  date, but nevertheless having a little time difference to an unmodified 
 *  copy.<br />
 *  Some tools like Update might otherwise refrain from from necessary
 *  updates.<br />
 *  <br />
 *  default: true <br />
 */
   public boolean incFileDate = true;   

/** Ignore case for search text matching. <br />
 *  <br />
 *  If true the matching of the oldText oldEnd pattern is not case 
 *  sensitive.<br />
 *  <br />
 *  Note: this single property ignoreCase is used as default for all
 *  indexed properties ignoreCase[32]. <br />
 *  <br />
 *  default: false
 */ 
    public boolean ignoreCase;

/** Ignore white spaces for search text matching. <br />
 *  <br />
 *  If true the matching of the oldText oldEnd pattern ignores embedded
 *  white spaces in the pattern and the original text.<br />
 *  <br />
 *  Note: this single property ignoreWS is used as default for all
 *  indexed properties ignoreWS[32]. <br />
 *  <br />
 *  default: false
 */ 
    public boolean ignoreWS;

/** Search text (multiple). <br />
 *  @see #oldText
 */
   protected String[]  oldT;
   CleverSSS[] oldRKt, oldRKe;

/** Search text, end (multiple). <br />
 *  @see #oldEnd
 */
   protected String[] oldE;

/** Ignore case for search text matching (multiple). <br />
 *  @see #ignoreCase
 */ 
   protected boolean[] ignCase;

/** Ignore white spaces search text matching (multiple). <br />
 *  @see #ignoreWS
 */ 
   protected boolean[] ignWS;

/** Replacement text; substitute (multiple). <br /> */
   protected String[] newT;

/** Replacement text / substitute file name;  (multiple). <br /> */
   protected String[] fileNs;


/** Modification date for the substitute (multiple). <br />  */
   protected long[] newModif;

/** Keep the braces, when replacing text. <br />
 *  <br />
 *  default: true   
 */
   public boolean keepBraces = true;
   
/** Keep the braces, when replacing text (multiple). <br /> */
   protected boolean[] keepBrace;

/** Exclude text. <br />
 *  <br />
 *  Is {@link #ignFilesWith} not null or empty, files containing this text 
 *  as is will not be touched (even if matching the visitor's file and 
 *  directory criteria).<br />
 *  <br />
 *  If this stopper text is inserted by one run of {@link FuR} that's the 
 *  procedure to inhibit multiple works on the same file.<br />
 *  <br />
 *  default: null<br />
 */
   public String ignFilesWith;

/** Exclude text (number 2). <br />
 *  <br />
 *  Same effect as {@link #ignFilesWith}. (Both excluding conditions are 
 *  ORed.)
 */
   public String ignFilesWith2;

/** Number of (multiple) replacements. <br /> */
   int anzAltNeu = -1;
 
/** Start directory. <br /> */
   public String directory;

/** File types to regard. <br /> */
   public String types;
   
/** Encoding of files to be modified. <br />
 *  <br />
 *  The name of the real/current encoding of the text files to be modified.
 *  It must fit and is not changed. <br />
 *  <br />
 *  No explicit setting (null) defaults to the platform encoding.
 *  
 *  @since January 2017
 */
   public String filModEnc;
   Charset filModCs; 
 
//----------------------------------------------------------------

   int dNr  = 0; // total number of visited files
   int dAnz = 0; // number of modified files
   int eAnz = 0; // total number of replacements

/** Working method of FuR. <br /> */
   @Override public int doIt(){
      log.println();
      if (verbose)log.println(twoLineStartMsg().append('\n'));
      if (filModEnc == null 
         ||  TextHelper.areEqual("defaultEncoding", filModEnc, true)) {
        filModEnc = FILE_ENCODING;
      } 
      try {
         filModCs =  Charset.forName(filModEnc);
      } catch (Exception e) {
         return errMeld(17, "Illegal file encoding " + filModEnc); 
      }
      FileService dS = new FileService();
      try {
         dS.set(prop);
      } catch (IllegalArgumentException iae) {
         return errMeld(5, iae);
      }
      for (int i = 0; i < args.length; ++i) { // parse remaining args
         String aktArg = args[i];
         if (aktArg == null) continue;
         if (directory == null) { // 1st remaining is [directory\]file
            aktArg = aktArg.trim();
            if (aktArg.length() != 0) directory = aktArg;
            continue;
         } // 1st
         if (oldText == null) { // 2nd remaining is text to be replaced 
            oldText = aktArg;
            continue;
         } // 2nd remaining is text to be replaced or its begin
         if (oldEnd == null) { // 3rd is old end or new text 
            oldEnd = aktArg;
            continue;
         } // 3rd
         if (newText == null) { // 4th is new text 
            newText = aktArg;
            continue;
         } // 4th 
         return errMeld(14, valueLang("tooManStandPar", 
                 "Too many or wrong standard parameters: ")  + aktArg);
      } // for (parse remaining args)

      if (verbose) {
         log.println ("\n  ///   Dir/Files = "  +  directory 
                       + (recursion ? "  -recursion\n" : "\n"));
      } // if (verbose

      if (newText == null) { // no occurrence of  oldEnd
          newText = oldEnd;
          oldEnd  = null;
      } else if (oldEnd == null || oldEnd.length() == 0)
          oldEnd = null;

      if (newText == null || newText.length() == 0) newText = ""; // just del.
      
      if (hyphen || deHyphen) { // pepare [de-] hyphenation
        if (hyphen && deHyphen) {
          return errMeld(41, valueLang("hyphAndDe", 
                                  "hyphenate as well as de-hypenate"));
        } // must be either hyphenate or de-hypenate
        hyphen = true; // from now hyphen means both and deHyphen distinguishes
        final int MXHY = 999;
        oldE = new String[MXHY];
        oldT = new String[MXHY];
        anzAltNeu = 0;
        try (Stream<String> stream = Files.lines(Paths.get(hyphFile),
                                          Charset.forName(hypFilEnc)) ){
          stream.forEach(line -> { // process hyphenation file lines
             //  log.println(line); // test only sofar
             String lin = TextHelper.trimUq(line, null);
             boolean lOK = lin != null; // OK replaces missing continue
             int linLen = lOK ? lin.length() : 0;
             boolean noCom = true; // no comment line
             lOK = linLen >= 9     // long enough and ...
                  && (noCom = lin.charAt(0) != '#'); // ... no comment line
             if (verbose && ! noCom) log.println(line); // print comments

             int firstHyph = lOK ? lin.indexOf(SHY) : -1;
             lOK = firstHyph >= 2 && linLen > firstHyph + 6; // aa..&shy;bb
             if (lOK) { // check last 
               firstHyph = lin.lastIndexOf(SHY); // can't be -1 as one is there
               lOK = linLen > firstHyph + 6; // and aa&shy;..bb
             } // check last 
             String linPure = null;
             if (lOK) {
               linPure = lin.replace(SHY, EMPTY_STRING); // replaces all occ
             }
             if (lOK && verbose) {
               log.println("-[" + TextHelper.threeDigit(anzAltNeu) + "]: "
                              + linPure + (deHyphen ? " < " : " > ") + lin);
             }
             if (lOK && anzAltNeu < MXHY) {
               oldE[anzAltNeu] = lin;
               oldT[anzAltNeu] = linPure;
               ++anzAltNeu;
             } // line OK and space
          }); // process hyphenation file lines
        } catch (IOException ex) {
          log.println(valueLang("hyphFilErr", "hyphenation file error:"));
          return errMeld(41, ex);                   
        } // try stream over lines of hyphFile
        if (anzAltNeu == 0) return errMeld(43, valueLang("hyphNoSpec", 
                                                "no hyphenation specified"));
        ignoreCase = false; // we must keep case intakt
        ignoreWS   = false; // white space is signifikant in word search
        keepBraces = true; // we have none
        // above oldE is text with SHY and oldT is pure text
        newT = new String[anzAltNeu];
        if (deHyphen) { // de-hyphenate
          newT = oldT;
          oldT = oldE; // i.e. text with hyphen
        } else { // de-hyphenate else hyphenate
          newT = oldE; // i.e. text with hyphen
        } // hyphenate (oldT i.e. pure text is OK)
        oldRKt = new CleverSSS[anzAltNeu];
        for (int i = 0; i < anzAltNeu; ++i) {
          oldRKt[i] = RK.make(oldT[i], ignoreCase, ignoreWS);
        } // prepare hyphen for
        
      } else for (int i = 189; i >= 0; --i) { // prepare std find&replc 
         String a  = prop.getString("old", i, null);
         if (a == null) continue; 
         if (anzAltNeu == -1) { // first old/new pair found
            anzAltNeu = i + 2;
            oldT = new String[anzAltNeu];
            oldRKe = new CleverSSS[anzAltNeu];
            oldRKt = new CleverSSS[anzAltNeu];
            newT = new String[anzAltNeu];
            newModif = new long[anzAltNeu];
            oldE = new String[anzAltNeu];
            ignCase = new boolean[anzAltNeu];
            ignWS = new boolean[anzAltNeu];
            keepBrace = new  boolean[anzAltNeu];
            fileNs =  new String[anzAltNeu];
            if (oldText != null && oldText.length() != 0) {
               if (!ignoreCase && oldText.equals(newText)) {
                  return errMeld(27, messageFormat(null, "oldNewEqual", 
                     "Search and replacement texts = \"{0}\" are equal", 
                                                                    oldText));
               }
               oldT[0] = oldText;
               newT[0] = newText;
               oldE[0] = oldEnd;
               ignCase[0] = ignoreCase;
               ignWS[0] = ignoreWS;
               keepBrace[0] = keepBraces;
               oldRKe[0] = RK.make(oldEnd, ignoreCase, ignoreWS);
               oldRKt[0] = RK.make(oldText, ignoreCase, ignoreWS);
            }
         }
         oldT[i+1] = a;
         String n = prop.getString("new", i, ComVar.EMPTY_STRING);
         getfromF: if (n == ComVar.EMPTY_STRING) {
            String tfn = prop.getString("newFile", i, null);
            if (tfn == null) break getfromF;
            fileAgain: for (int j = i + 2; j < anzAltNeu; ++j) {
               if (!tfn.equals(fileNs[j])) continue fileAgain;
               newModif[i+1] = newModif[j];
               n = newT[j]; // instead of reading the same file again
               break getfromF;
            } // fileAgain: for 
            try {
               Input eing = Input.openFileOrURL(tfn);
               n = eing.getAsString(filModCs);
               long mdf = eing.lastModified();
               if (mdf > 0L 
                       &&  prop.getBoolean("useNewModif", i, useNewModif)) {
                  newModif[i+1] = mdf; // record input's modTime
               }
               fileNs[i+1] = tfn;
            } catch (Exception ex) {
               log.println(messageFormat(null, "openexcp",
                        "can\'t read file {0} ", tfn).append(" [" + i + "]"));
               if (verbose) ex.printStackTrace(log);
            }
         } // getfromF: if 
         
         String e = prop.getString("end", i, null);
         boolean kB = e != null 
                            && prop.getBoolean("keepBraces", i, keepBraces);
         keepBrace[i+1] = kB;
         newT[i+1] = n;
         oldE[i+1] = e;
         final boolean iC = ignCase[i+1] = prop.getBoolean(
                                                   "ignCase", i, ignoreCase);
         final boolean iWS = ignWS[i+1] = prop.getBoolean("ignWS", i, ignoreWS); 
         if (!iC && !iWS && e != null && a.equals(n))
            return errMeld(37, messageFormat(null, "oldNewEqual", 
                 "Search and replacement texts = \"{0}\" are equal", 
                                    a).append(" [" + i + "]"));
         oldRKe[i+1] = RK.make(e, iC, iWS);
         oldRKt[i+1] = RK.make(a, iC, iWS);
                                    
        /// ???  ignWS[i+1] = iC &&   prop.getBoolean("ignWS", i, ignoreWS);    
      } // else for  prepare std find&replc (for old[i])

      if (anzAltNeu == -1) {
         if (oldText == null || oldText.length() == 0) {
            return errMeld(6, valueLang("noSpecSearch", 
                                                "No search text specified"));
         }   
         if (!ignoreCase && oldText.equals(newText)) {
            return errMeld(19, messageFormat(null, "oldNewEqual", 
                 "Search and replacement texts = \"{0}\" are equal", 
                                                                    oldText));
         }   
         anzAltNeu = 1;
         newT = new String[]{newText};
         oldT = new String[]{oldText};
         oldE = new String[]{oldEnd};
         ignCase = new boolean[]{ignoreCase};
         ignWS   = new boolean[]{ignoreWS};
         keepBrace = new  boolean[]{ keepBraces};
         newModif = new long[1];
         oldRKt = new CleverSSS[]{RK.make(oldText, ignoreCase, ignoreWS)};
         oldRKe = new CleverSSS[]{RK.make(oldEnd,  ignoreCase, ignoreWS)};
      }
    

      if (directory == null 
              || (directory = directory.trim()).length() == 0) {
         return errMeld (5, valueLang("nospcdir",
                                      "No directory (nor file) specified"));
      }
      if (ignFilesWith == null || ignFilesWith.length() == 0)
         ignFilesWith = null;

   //---   End of parameter and prop-file evaluation    --------------------

      FileVisitor replaceBesucher =  new FileVisitor() {
         @SuppressWarnings("resource") // fr will be indirectly closed as file  
         @Override public int visit(final File dD){
            ++ dNr;
            if (verbose) {
               log.print("\n  " + dD.getPath() + "\n " + dNr + " \t");
               log.flush();
            }
            String ai  = null;
            int    aiL = 0;
            try { 
               Input fr = new Input(dD);
               ai = fr.getAsString(filModCs); 
            } catch (IOException e) {
               if (!verbose) {
                  log.print("\n  " + dD.getPath() + "\n " + dNr + " \t");
               }
               log.println("is not readable");
               return 0;
            }
            if (ai == null || (aiL = ai.length()) == 0) {
               if (verbose) log.println("no content (empty)");
               return 0;
            }
            if (verbose) {
               log.print(aiL + " characters \t");
               log.flush();
            }

            if (ignFilesWith != null && ai.indexOf(ignFilesWith) >= 0) {
               if (verbose) log.println("exclude criterion 1 fulfilled");
               return 0;
            } // exclude  ??

            if (ignFilesWith2 != null && ai.indexOf(ignFilesWith2) >= 0) {
               if (verbose) log.println("exclude criterion 2 fulfilled");
               return 0;
            } // exclude 2 ??
            
            // file content
            
            int frntMatLen = 0; // length of start part to be left unchanged
            String frntMat = null; // the start part to be passend unchanged
            StringBuilder bu1 = null;
            if (omitFrntMt) {
              frntMatLen = TextHelper.isFrntMttr(ai);
              if (frntMatLen > 2) {
                if (verbose) {
                  log.print("front matter(" + frntMatLen + ") \t");
                }
                if (frntMatLen == aiL) {
                  if (verbose) log.println(" is all content.");
                  return 0; // front matter only content
                }
                frntMat = ai.substring(0, frntMatLen);
                bu1 = new StringBuilder(ai.substring(frntMatLen));
              } else frntMatLen = 0; // keep 0 (not -1)
            } else { // check front matter
              bu1 = new StringBuilder(ai);
            } // no front matter

            StringBuilder bu2 = new StringBuilder(aiL + 500);
            long modL = dD.lastModified();
            final long origModL = modL;

            int vork = 0;
            searchLoop: for (int i = 0; i < anzAltNeu; ++i) {
               final CleverSSS a = oldRKt[i];
               if (a == null) continue searchLoop;
               final CleverSSS e = hyphen ? null : oldRKe[i];
               String n = newT[i];
               
               if (e != null && e.len != 0 && keepBrace[i]) {
                  n = oldT[i] + n + oldE[i];
               } // keep braces 

               int vki = TextHelper.fUr(bu1, bu2, a, e, n); //, ignCase[i], ignWS[i]);
               if (vki > 0) {
                 long insMod = hyphen ? 0L : newModif[i];
                 if (insMod > modL) modL = insMod;
                 vork += vki;
                 StringBuilder tmp = bu2;
                 bu2 = bu1;
                 bu1 = tmp;
                 bu2.setLength(0); // clear
              } // any replacements
            }  // for

            if (vork == 0) {
               if (verbose)
                  log.println("no occurrence of search texts");
                return 0;
            }
            ++dAnz;
            eAnz += vork;
            if (isNormal()) {
               if (!verbose) log.print("\n  " + dD.getPath()
                                                      + "\n " + dNr + " \t");
               if (isTest()) { // since 25.06.2015
                  log.println(vork + " finds; not modif. (TEST)");
                  return 0;  // out before file modification
               } // since 25.06.2015
               log.println(vork + " occurrences of search texts");
            }

            FileHelper.OS os = FileHelper.makeOS(dD, 
                                                OutMode.OVERWRITE, filModCs );
            if (os == null) {
               if (!isNormal())
                  log.print("\n  " + dD.getPath()
                       + "\n " + dNr + " \t");
               log.println("can not be overwritten");
               return 0;
            }
            PrintWriter pw = os.pw;
            if (frntMatLen > 0 && frntMat != null) pw.print(frntMat);
            pw.print(bu1);
            os.close();
            if (keepFileDate || incFileDate) {
               if (incFileDate && modL == origModL) modL += 501; // + 1/2 s
               dD.setLastModified(modL);
            }
            return 1;
         } // visit(File)
      }; //  FileVisitor replaceBesucher

//--- Directory evaluation and directory visits   --------------------

      String path = dS.filCrit.parse(directory, types);
 
      if (isTest()) {
         err.println(path+" < > " + dS.filCrit.getWildName()); 
         err.println(path+" <;> " + dS.filCrit.getTypes()); 
         err.println("Start DirVisiter " + path + " rec=" 
             + dS.isRecursion());
         err.flush();
      } // isTest
   
      dS.dirVisit(path, null,  
         replaceBesucher,   // visitor for files  (see above)
         null,              // visitor for empty directories
         null);             // visitor for filled directories

      if (verbose || isNormal() && eAnz != 0) {
         log.println(messageFormat(null, "replSum", 
                          "\n  ///   {0} replacement in {1} / {2} files\n",
                            new int[]{eAnz,           dAnz,   dNr}));
         if (isTest()) log.println(valueLang("nomodtst",
                                                "No files modified (TEST)"));
         log.println(threeLineEndMsg());
       }
      return 0; 
   } // doIt()        
} // FuR (03.06.2003, 05.2004, 04.2006, 02.2009, 06.2015, 03.07.2021)
