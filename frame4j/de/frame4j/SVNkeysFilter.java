/*  Copyright 2018 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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

import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import de.frame4j.text.TextHelper.ReplaceVisitor;
import de.frame4j.time.TimeHelper;
import de.frame4j.io.Input;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.text.CleverSSS;
import de.frame4j.text.KMP;

/** <b>Beautify one text files got from Subversion </b>. <br />
 *  <br />
 *  This application reads one input (text) file and beautifies / modifies
 *  the text contained in Subversion (SVN) keywords (tags). The modified text
 *  is put to normal / standard output.<br />
 *  <br />
 *  This functionality is a simplification of {@link de.frame4j.SVNkeys}; see
 *  the details there. This (only) one file to standard output simplification
 *  fits exactly the specification of a Doxygen filter &mdash; and that is
 *  the purpose of this programme.<br />
 *  <br />
 *  In the text file read some replacements may be done; in the standard 
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
 *  <br />   
 *  <b>Hint 2</b>: With CVS (and cvsNT) you just have the so called
 *  &quot;keyword substitution&quot; always. You have to kill it explicitly 
 *  sometimes for non text files. With Subversion this nice feature is
 *  off by default and must be switched on for every single file, like in 
 *  example:<br /> &nbsp;
 *   svn propset svn:keywords "Date Revision Author Id HeadURL" *.java<br />
 *  Stupidly it's really for the single file(s) not for the file type, as the
 *  example might suggest. Bringing in a new .java file you must repeat the
 *  procedure for it.<br />
 *  <br />
 *  <b>Hint 3</b>: This application is part of the framework Frame4J. Frame4J's
 *  building and deploying scripts also use this application before compiling
 *  or doing the (javaDoc) documentation. That makes Frame4J 
 *  &quot;self&nbsp;beautifying&quot; (and 
 *  involved &quot;self&nbsp;building&quot;).<br />
 *  So the text you just see is (also) edited by {@link SVNkeys} or 
 *  {@link SVNkeysFilter}.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a> 
 *       Copyright 2018  2021 &nbsp; Albrecht Weinert<br />
 *  <br /> 
 */
 // so far    V.   2 (15.02.2018) :  extracted from SVNkeys

@MinDoc(
   copyright = "Copyright 2018  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 41 $",
   lastModified   = "$Date: 2021-04-23 20:44:27 +0200 (Fr, 23 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application (option -?)\n"
      +  "java de.frame4j.SVNkeysFilter inputFile  \n"
      +  "java de.frame4j.SVNkeysFilter [options]  \n"
      +  "see de.frame4j.SVNkeysFilter javaDoc for details",  
   purpose = "refine and beautify a file's SVN tags / Doxygen filter"
) public class SVNkeysFilter extends App implements ReplaceVisitor {

/** No objects by others (no javaDoc also). */
   private SVNkeysFilter(){}
   
/** No own .property file required. <br />
 *  @return true 
 */   
   @Override protected boolean allowNoPropertiesFile(){ return true; } 

/** Only only partial start parameter parsing by Prop. <br />
 *  @return true 
 */
   @Override public boolean parsePartial(){ return true; }

/** Start method of SVNkeys. <br />
 *  <br />
 *  An exit code (ERRORLEVEL) 0 signalises a run without trouble.<br />
 *  <br />
 *  Problems lead to &gt;0. (&lt; 0 not used for sake of Windows.)<br />
 *  <br />
 *  exit code 0: OK.<br />
 *  exit code 1: completely run through, content of file, maybe filtered, 
 *               output to standard output
 *  exit code&gt;1: parameter or other starting problems; nothing done.<br />
 *  <br />
 *  Hint: Start with -options or more than one file parameter can lead to
 *  message output. For use as Doxygen filter provide the file name as just
 *  one start parameter.  
 *  <br />
 *  @param args the application's start parameter
 */
   public static void main(final String[] args){
      try {
         new SVNkeysFilter().go(args);
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

  
/** Re-format the date. <br />
 *  <br />
 *  default: true<br />
 */
   public boolean reformatDate = true;
    
   
/** Re-format the date; format String. <br />
 *  <br />
 *  default: j.m.Y<br />
 *  <br />
 *  The format characters are described at
 *  {@link de.frame4j.time.TimeHelper#format(CharSequence, java.time.ZonedDateTime)}.<br />
 */
   public String dateFormat = "j.m.Y";

/** For empty &#36;Date&#36; insert current time. <br />
 *  <br />
 *  If true the replacement for an empty date is the current time, of course
 *  formatted with {@link #dateFormat}.<br />
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
 *  priority over {@link #dateDefaultToNow}.<br />
 *  <br />
 *  Hint: Due to a bug Subversion will usually not work on &#36;Date&#36;. It 
 *  needs &#36;Date: &#36; or &#36;Date: rhubarb &#36; including the trailing
 *  blank!<br />
 *  <br />
 *  default: true
 */
   public boolean dateDefaultToMod = true;


/** Keys pure key. <br />
 *  <br />
 *  Storage for Properties onN (N = 0..99).
 */
  final String[] key = {"Date", "Author", // 0..1
                 "Name", "Revision", "HeadURL", // 2..4
                "Rev", "LastChangedRevision", "LastChangedBy", "Id"}; // 5..8

/** Keys (multiple) default value. <br />
 *  <br />
 *  Storage for Properties defN (N = 0..99).
 */
   final String[] def = {"(no date)",  "A. Weinert", // 0..1
                   "last revision (HEAD)", "new", "(unknown)", // 2..4
                   "new", "new", "(unknown)", "(this file)"}; // 5..8

/** Number of Key replacements. <br /> */
   final int anzKeys = 9; // must mach length of key and def

/** Keys (multiple) clever String search. <br />
 */
   final CleverSSS[] rkKey = new CleverSSS[anzKeys] ;

   private int replaceInd; // Index of key; 0 = Date,  Visit's steering var.


/** The file to act on. <br /> */      
   protected File dD;

/** The files original modification date (if to keep). <br /> */      
   private long modifFile;

      
/** File visitor. <br />
 *  <br />
 *  Procession of all text substitutions on the visited file.<br /> 
 */  
   @SuppressWarnings("resource")  // fr will be indirectly closed as file  
   public int visit(final File dD){
     String ai  = null;
     int    aiL = 0;
     try { 
        Input fr = new Input(dD);
        ai = fr.getAsString(filModCs); // filModEnc since 6.1.16 (here input)
     } catch (IOException e) {
        return 0;
     }
     StringBuilder bu1 = new StringBuilder(ai);
     StringBuilder bu2 = new StringBuilder(aiL + 500);
     if (tabReplace) {
        int tki = TextHelper.fUr(bu1, bu2, "\t", null, trs, false);
        if (tki > 0) {
           StringBuilder tmp = bu2;
           bu2 = bu1;
           bu1 = tmp;
           bu2.setLength(0); // clear
       } // any repl
     }
     modifFile = dD.lastModified(); // to keep 08.12.05
            
     replLoop: for (replaceInd = 0; replaceInd < anzKeys; ++replaceInd) {
        CleverSSS k = rkKey[replaceInd];
        int trp = TextHelper.fUr(bu1, bu2, k, clBrk,  this);
        if (trp > 0) {
          StringBuilder tmp = bu2;
          bu2 = bu1;
          bu1 = tmp;
        } // any repl
        bu2.setLength(0); // clear
     } // for replLoop
     
     PrintWriter pw = out;
     pw.print(bu1);
     return 1;
   } // visit()
     
      
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
          return contS; 
      }
       
      // to do / clarify : why did we not value substitution on keeping braces
      ///  if () clBr = fieldLengthMarker + clBr;
      return TextHelper.con3cat(opBr, setOperator, " ") 
               + TextHelper.con3cat(contS, 
                  fieldEndMarkerRemoved ? " " +  fieldLengthMarker  : " ",
                                          clBr);                   
   } // visit(3*String)

   private String trs = "   ";  // Number of spaces for tab subst.; def. 3 


/** Working method of SVNkeysFilter. <br /> */
   @Override public int doIt(){
      if (args.length != 1 || args[0] == null // options done
                           || args[0].length() == 0) return 7;
      if (tabWidth < 2 || tabWidth > 12) tabWidth = 3;
      if (tabReplace && tabWidth != 3)   
                  trs = "            ".substring(0, tabWidth);    
          
      if (opBrace == null || opBrace.length() == 0) opBrace = "$";
      if (clBrace == null || clBrace.length() == 0) clBrace = "$";
      clBrk = KMP.make(clBrace, ignoreKeyCase && !"$".equals(clBrace));
      if (setOperator == null || setOperator.length() != 1) setOperator = ":";
      if (maxBraceDist < 24 || maxBraceDist > 300) maxBraceDist = -1;
      if (removeBraces) removeContent = false;
         
      if (dateFormat == null || dateFormat.length()== 0) dateFormat = "j.m.Y";
     
      if (filModEnc == null 
             ||  TextHelper.areEqual("defaultEncoding", filModEnc, true)) {
        filModEnc = FILE_ENCODING;
      } 
      try {
         filModCs = Charset.forName(filModEnc);
      } catch (Exception e) {
         return 17; 
      }
      
 //----  Key substitutions         
      for (int i = 0; i <anzKeys; ++i) {
         String obK = opBrace + key[i];
         rkKey[i] =  KMP.make(obK, ignoreKeyCase);
       } // for key[i]
      if (dateDefaultToNow)
         def[0] = TimeHelper.format(dateFormat, appStartTimeMS);

//---   End of preparation  - now visit the file    -----------------
      dD = new File(args[0]);
      int retV =  visit(dD);
      return retV == 1 ? 0 : 5; 
   } //  doIt        

} // class SVNkeysFilter 16.02.2018
