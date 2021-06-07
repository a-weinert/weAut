/*  Copyright 2013 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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
package de.frame4j.text;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import de.frame4j.time.TimeHelper;
import de.frame4j.util.App;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;

/** <b>Text handling &mdash; support for Java applications</b>. <br />
 *  <br />
 *  This class has static methods, and (internal) interfaces and classes
 *  for formatting, parsing and modifying texts, which include<ul>
 *  <li> straight implementation of frequently needed formats,</li>
 *  <li> parsers accepting a wider range of (human used) input formats,</li>
 *  <li> processing of parameter arrays, </li>
 *  <li> decomposing of strings into parameters (token), </li>
 *  <li> some methods to compare and search extending those 
 *       featured by {@link java.lang.String}, as among others <ul>
 *  <li> the comparison of a file name with a wildcard pattern, </li>
 *  <li> some search and compare methods optionally ignoring
 *       case and / or white space, </li></ul>
 *  <li> powerful search and replace, </li>
 *  <li> partly using Rabin Karp algorithms, implemented by 
 *       {@link RK RK}, </li>
 *  <li> find and replace by visitors (visitor pattern).</li>
 *  </ul>
 *  The mentioned white-space ignoring comparisons analyse both sequences as
 *  if all white space had been moved out. They often are a more simple (and 
 *  faster) substitute for things like regular expressions (that, clearly,
 *  may be more adequate in certain circumstances). They have been heavily
 *  used over decades in Frame4J (respectively predecessor) based tools to
 *  recognise pattern in white space ignoring languages (HTML, XML, Java).
 *  Here it was adequate in all our and partners use cases:<br />
 *  &nbsp; &lt;table cols=&quot;3&quot; summary=&quot;Lit&quot;&gt; 
 *  &nbsp; and<br />
 *  &nbsp; &lt;table &nbsp; cols= &quot;3&quot; summary = 
 *  &quot;Lit&quot; &gt;<br />
 *  were (correctly) considered as equivalent, but (falsely) also <br />
 *  &nbsp; &lt;ta b lecols=&quot;3&quot;su mmary=&quot;L it&quot;&gt;
 *  &nbsp;.<br />
 *  The white-space ignoring methods can &mdash; as this example shows &mdash;
 *  safely be used. If one can assume some minimal correctness of the text under work
 *  the quite significant performance gain is worth the sacrifice of regular
 *  expressions' checking power.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2000 - 2009, 2021 &nbsp; Albrecht Weinert  
 *  @see de.frame4j.util.PropMap
 */
 // so far  V00.00 (19.06.2000) :  excerpt from AppHelper and MakeIndex
 //         V00.03 (04.09.2000) :  additional methods (StringBuilder)
 //         V02.00 (24.04.2003) :  CVS to Eclipse migration
 //         V02.03 (26.04.2003) :  JavaDoc(1.4.2beta) has a bug   
 //         V02.08 (01.06.2003) :  dismiss Java 1.3 compatibility; CharSeq 
 //         V02.20 (15.05.2004) :  unambiguous Boolean-Interpret., trimUq
 //         V02.25 (19.09.2004) :  tokenise regEx; more hexForm
 //         V02.32 (10.07.2005) :  wildEqual optimised & ignCase
 //         V02.33 (02.10.2005) :  split without RexEx, indexOf
 //         V02.38 (01.11.2006) :  lastIndexOfWS()
 //         V.179+ (06.01.2010) :  Rabin Karp outsourced to RK.java
 //         V.o94+ (04.09.2013) :  compare() bug-
 //         V.  33 (25.03.2021) :  type flexibility enh. + other improvements
 //         V.  38 (16.04.2021) :  dig(int), dec formatting enhanced
 //         V.  48 (15.05.2021) :  eightDigitHex() (new in March 21) bug-

@MinDoc(
   copyright = "Copyright 2000 - 2013, 2021  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 51 $",
   lastModified   = "$Date: 2021-06-07 16:31:39 +0200 (Mo, 07 Jun 2021) $",
   usage   = "import",  
   purpose = "common text utilities: check, parse and formating of texts"
) public abstract class TextHelper {

/** No objects. */
   private TextHelper(){ }  // no JavaDoc >= package

//==========================================================================
   
/** <b>Visitor for text replacements</b>. <br />
 *  <br />
 *  An object of this type ReplaceVisitor implements one text replacement
 *  (of arbitrary complexity or simplicity) for multiple (multi spot, multi
 *  file e.g.) text replacements applying the visitor pattern. The 
 *  applications / tools {@link de.frame4j.FuR}, {@link de.frame4j.SVNkeys}
 *  and {@link de.frame4j.SVNkeysFilter} make ample use of this pattern
 *  and this interface.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2003, Albrecht Weinert  
 *  @author   Albrecht Weinert
 *  @since V2.05 (27.05.2003)
 *  @see TextHelper#fUr(CharSequence, StringBuilder, CharSequence, CharSequence, boolean)
 */
@MinDoc(
   copyright = "Copyright 2003, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "see enclosing class TextHelper",
   lastModified   = "see enclosing class TextHelper",
   lastModifiedBy = "see enclosing class TextHelper",
   usage   = "import",  
   purpose = "visitor pattern for text find and replace"
)   public static interface ReplaceVisitor {

/** Evaluate the replacement text. <br />
 *  <br />
 *  This method will be called for every single &quot;visited&quot; place
 *  in the text under work.<br />
 *  <br />
 *  The content of this visited place will be referred to by (max.) 
 *  three Strings:<ul>
 *  <li>opening brace (pattern),</li>
 *  <li>the found text (itself),</li>
 *  <li>closing brace (pattern).</li>
 *  </ul>
 *  These can all be null, but (according to contract) at least one, 
 *  mostly {@code cont}, should be non null.<br />
 *  <br />
 *  The return value of this method is the new text, that shall replace all
 *  (!) three Strings constituting the visited place in the text under
 *  work.<br />
 *  <br />
 *  That means, if the original text shall be kept unchanged at the visited 
 *  place, do <br /> &nbsp; return
 {@link #con3cat(CharSequence, CharSequence, CharSequence) con3cat}(opBr,
 *    cont, clBr);<br />
 *  or just return null.<br />  
 *  <br />
 *  The contract says, that returning null will also induce the caller to 
 *  leave the place untouched. The visit then shall not be considered or 
 *  counted as a replacement. If you want the visited spot emptied 
 *  do <br /> &nbsp;
 *   return {@link ComVar}.{@link ComVar#EMPTY_STRING EMPTY_STRING};<br />
 *  <br />
 *  @see TextHelper#con3cat(CharSequence, CharSequence, CharSequence)
 *  @param opBr opening brace 
 *  @param cont content
 *  @param clBr closing brace
 *  @return replacement (for all three)
 */  
   public String visit(String opBr, String cont, String clBr);
} // ReplaceVisitor ======

   
/** Concatenate three Strings. <br />
 *  <br />
 *  The three String parameters will be concatenated and returned 
 *  <code>s1 + s2 + s3</code> as far as not null or empty in each case.<br />
 *  <br />
 *  If all three are empty an empty String ({@link ComVar#EMPTY_STRING}) 
 *  is returned.<br />
 *  <br />
 *  Hint: The effect (and internal work) of this is method is not the same as
 *  just doing {@code (s1 + s2 +..)}.<br />
 *  <br />
 *  @return the concatenated content of the parameters; 
 *          may be empty but never null
 */
   public static String con3cat(final CharSequence s1,
                                final CharSequence s2, final CharSequence s3){
      final int k1 = s1 == null ? 0 : s1.length();
      final int k2 = s2 == null ? 0 : s2.length();
      final int k3 = s3 == null ? 0 : s3.length();
      final int ks = k1 + k2 + k3;
      if (ks == 0) return ComVar.EMPTY_STRING;
      if (k1 == 0) { // no s1
         if (k2 == 0) return s3.toString();
         if (k3 == 0) return s2.toString();
      } else if (k1 == ks) {
         return s1.toString(); 
      }
      StringBuilder dest = new StringBuilder(ks);
      if (k1 != 0) dest.append(s1);
      if (k2 != 0) dest.append(s2);
      if (k3 != 0) dest.append(s3);
      return dest.toString();
   } // con3cat(3*CharSequence)
   
/** Select (property-) text from different choices. <br />
 *  <br />
 *  {@code text} is freed / stripped of all surrounding white spaces. If 
 *  (then) not empty, it is returned.<br />
 *  <br />
 *  Otherwise the same proceeding is done with {@code default1}. Does that 
 *  also give no non empty value, {@code default2} is returned as is.<br /> 
 */   
   public static String getPropertyText(final CharSequence text, 
                        final CharSequence default1, final String default2){
      String tText = getPropertyText(text, null);
      if (tText != null) return tText;
      return getPropertyText(default1, default2);
   } // getPropertyText(CharSequence, 2*String) 

   
/** Select (property-) text from different choices &mdash; simple trim. <br />
 *  <br />
 *  {@code text} is freed / stripped of all surrounding white spaces. If 
 *  (then) not empty, it is returned. Otherwise default2 is returned as 
 *  is.<br />
 *  <br />
 *  The difference to 
 *  {@link #trimUq(CharSequence, String) trimUq(text, default2)} is this
 *  method having no &quot;un-quote&quot;; see 
 *  {@link #trimUq(CharSequence, String) there}. The differences to
 *  {@link String}.{@link String#trim() trim()} are the acceptance of any
 *  {@link CharSequence} and the default value. 
 *  {@link String}.{@link String#trim() trim()} behaviour is got by 
 *  supplying {@link ComVar}.{@link ComVar#EMPTY_STRING EMPTY_STRING} as
 *  {@code def}.<br />
 *  <br />
 *  @see TextHelper#getPropertyText(CharSequence, CharSequence, String)
 *  @see #trimUq(CharSequence, String)
 */   
   public static String getPropertyText(final CharSequence text,
                                                           final String def){
      if (text == null) return def; // (1) null
      int endInd = text.length();
      if (endInd == 0) return def;  // (2) empty
      char c1 = text.charAt(0);
      --endInd;
      char c2 = endInd == 0 ? c1 : text.charAt(endInd);
      if (c1 > ' ' && c2 > ' ') {  // simple case: no surrounding WS 
         return text.toString();
      } // simple case: no surrounding WS (fast path out)
      
      int anfInd = 0;
      while (c1 <= ' ' && anfInd < endInd) {
         ++anfInd;
         c1 = text.charAt(anfInd);
      }
      if (anfInd == endInd) { // through
         if (c1 <= ' ') return def;  //  (3) stripped empty
      }  else while (c2 <= ' ' && endInd > anfInd) {
         --endInd;
         c2 = text.charAt(endInd);
      }
      ++endInd;
      if (text instanceof String) {
         return ((String)text).substring(anfInd, endInd);
      }
      return text.subSequence(anfInd, endInd).toString();
   } // getPropertyText(CharSequence, String) 
   
//-------------------------------------------------------------------

/** Construct a file name. <br />
 *  <br />
 *  This method returns a non empty filename according to the parameters
 *  {@code name} and {@code ext} or it returns null.<br />
 *  <br />
 *  {@code name} will be stripped from surrounding white space. If then empty
 *  null will be returned.<br />
 *  <br />
 *  Forward / respectively back  \ slashes will be replaced by the underlying
 *  system's file separator ({@link de.frame4j.util.ComVar#FS ComVar.FS}).
 *  If {@code ext} is not null or empty (after stripping white spaces) it will
 *  be prefixed by a dot (.) if does not yet start so.<br />
 *  The (dot prefixed)  {@code ext} will be appended to the name if that
 *  is not yet ending so (disregarding case).<br />
 *  <br />
 *  @param name file name demanded
 *  @param ext  default extension (appended if not yet name's end)
 *  @return the worked up file name 
 */
   public static String makeFName(CharSequence name, String ext){
      String fName = TextHelper.trimUq(name, null);
      if (fName == null) return null;
      if (ComVar.FS != '/')
         fName = fName.replace('/', ComVar.FS);
      else
         fName = fName.replace('\\', ComVar.FS);
      while (ext != null) { // breakable if
         ext = ext.trim();
         int el = ext.length();
         if (el == 0) break;
         if (ext.charAt(0) != '.') {
            ext = '.' + ext; 
         } else if (el < 1) break;
         if (endsWith(fName, ext, true)) break;
         return /*lastMadeFName =*/ fName + ext;
      } // breakable if
      return fName;
   } // makeFName(CharSequence, String)

/** Construct a file name for URLs or .jar entries. <br />
 *  <br />
 *  This method returns a non empty filename according to the parameter
 *  {@code name} or null.<br />
 *  <br />
 *  {@code name} will be stripped from surrounding white space. If then empty
 *  null will be returned.<br />
 *  <br />
 *  Backslashes \ will be replaced by forward slashes /. This changes a 
 *  Windows file name to one suitable for URLs or for within .jar files 
 *  respectively to the JAR-entry syntax.<br />
 *  <br />
 *  @param name file or entry name
 */
   public static String makeFNameUJ(CharSequence name){
      String fName = TextHelper.trimUq(name, null);
      if (fName == null) return null;
      fName = fName.replace('\\', '/');
      return fName;
   } // makeFNameUJ(CharSequence)

/** Working up names of files or of file name lists. <br />
 *  <br />
 *  This method returns a non empty file denotation according to the parameter
 *  {@code path} or null. {@code path} will be stripped from surrounding 
 *  white space. If then empty null will be returned.<br />
 *  <br />
 *  forward / respectively back  \ slashes will be replaced by the underlying
 *  system's file separator
 *  ({@link de.frame4j.util.ComVar#FS ComVar.FS}).<br />
 *  <br />
 *  To uniquely handle lists a semicolon will be used as list separator.
 *  Under Windows it is expected to be so. If the path separator is a colon
 *  (: meaning Linux or Unix) it will be replaced by semicolon (;).<br />
 *  <br />
 */
   public static String makeFNL(String path){
      if (path == null) return null;
      path = path.trim();
      int ql = path.length();
      if (ql == 0) return null;
      path = path.replace('+', '*');
      if (ComVar.FS == '\\')
         path = path.replace('/', ComVar.FS);
      else
         path = path.replace('\\', ComVar.FS);
         
      if (ComVar.PS == ':')
          path = path.replace(':', ';');
      return path;
   } // makeFNL(String)

//------------------------------------------------------------------------

   
/** Determine if a character sequence ends with another one. <br />
 *  <br />
 *  This method returns true if the character sequence {@code st1} ends with
 *  the sequence {@code st2}, disregarding case if {@code ignoreCase} is
 *  true.<br />
 *  This method returns false, if {@code st1} is null or empty.<br />
 *  It returns true, if {@code st2} is null or empty.<br />
 *  <br />
 *  @param st1 the sequence which's end is tested to be st2
 *  @param st2 the sequence to be matched to the end of st1
 *  @param ignoreCase if true the comparison is not case sensitive
 *  @return true if st1 ends with st2 (optionally disregarding case)
 *  @see #simpCharEqu(char, char, boolean)
 */
   public static boolean endsWith(final CharSequence st1,
                           final CharSequence st2, final boolean ignoreCase){
      if (st1 == null) return false;
      int len1 = st1.length();
      if (len1 == 0) return false;
      if (st2 == null) return true;
      int len2 = st2.length();
      if (len2 == 0) return true;
      if (len2 > len1) return false;
      for (--len2, --len1; len2 >= 0; --len2, --len1) {
         if (! simpCharEqu(st1.charAt(len1), st2.charAt(len2), ignoreCase)) 
                                                                return false;
      }
      return true;
   } // endsWith(2*CharSequence, boolean)
 
/** Determine if a character sequence begins with another one. <br />
 *  <br />
 *  This method returns true if the character sequence {@code st1} begins with
 *  the sequence {@code st2}, disregarding case if {@code ignoreCase} is 
 *  true.<br />
 *  This method returns false, if {@code st1} is null or empty.<br />
 *  It returns true, if {@code st2} is null or empty.<br />
 *  <br />
 *  @see #endsWith(CharSequence, CharSequence, boolean)
 *  @see #simpCharEqu(char, char)
 *  @see #simpCharEqu(char, char, boolean)
 */
   public static boolean startsWith(final CharSequence st1,
                           final CharSequence st2, final boolean ignoreCase) {
      if (st1 == null) return false;
      final int len1 = st1.length();
      if (len1 == 0) return false;
      if (st2 == null) return true;
      int len2 = st2.length();
      if (len2 == 0) return true;
      if (len2 > len1) return false;
      for (--len2; len2 >= 0; --len2) {
         final char c1 = st1.charAt(len2);
         final char c2 = st2.charAt(len2);
         if (! simpCharEqu(c1, c2, ignoreCase)) return false;
      }
      return true;
   } // startsWith(2*CharSequence, boolean)
   
/** Interpret a character sequence as boolean. <br />
 *  <br />
 *  This method returns true, if the sequence {@code value} is 
 *     "true", "wahr", "ja", "yes", "an" or "on"
 *  disregarding case.<br />
 *  All other cases including null and empty return false.<br />
 *  <br />
 */
   public static boolean asBoolean(CharSequence value) {
      if (value == null) return false;
      final int len = value.length();
      if (len < 2 || len > 4) return false;
      final char c1 = simpLowerC(value.charAt(0));
      final char c2 = simpLowerC(value.charAt(1));
      if (c2 == 'n') { // an or or on
         return len == 2 && (c1 == 'o' || c1 == 'a'); 
      }   // an or on  
      // an on  | ja yes wahr true
      boolean secA = c2 == 'a';
      if (secA) { // ja or wa[hr
        if (len == 2 && c1 == 'j') return true;
        if (len != 4 || c1 != 'w') return false;
      }  // ja or wahr    
      // an on  ja wa[hr] true yes 
      if (len < 3) return false;
      char c3 = simpLowerC(value.charAt(2));
      if (len == 4) {
         if (secA && c3 != 'h') return false;
         char c4 = simpLowerC(value.charAt(3));
         if (secA) return c4 != 'r'; // wah?
         return c1 == 't' && c2 == 'r' && c3 == 'u' && c4 == 'e';
      }   
      // to here only with len 3
      return c1 == 'y' && c2 == 'e' && c3 == 's';
   } // asBoolean(CharSequence)

/** Interpret an Object as Boolean. <br />
 *  <br />
 *  This method returns a value of type {@link Boolean} as is and delegates
 *  {@link CharSequence}s to the method 
 *  {@link #asBoolObj(CharSequence)}.<br />
 *  <br />
 *  All other types return null.<br />
 *  <br />
 *  Hint: Later versions of this method may (in future) interpret the 0 
 *  as false and the value 1 as true for some numerical types.<br />
 *  <br />
 *  @see #asBoolObj(CharSequence)
 */
   public static Boolean asBoolObj(final Object value) {
      if (value == null) return null;
      if (value instanceof Boolean) return (Boolean)value;
      if (value instanceof CharSequence) 
             return asBoolObj((CharSequence) value);
      return null;
   } // asBoolObj(Object)

/** Interpret a character sequence as Boolean. <br />
 *  <br />
 *  This method returns {@link Boolean}.TRUE, if the sequence {@code value} is 
 *     "true", "wahr", "ja", "yes", "an" or "on"
 *  disregarding case.<br />
 *  It returns {@link Boolean}.FALSE, if the sequence {@code value} is 
 *     "fals?", "fals??", (including thus  "false" and  "falsch"), "ne??", 
 *     "nein", "no", "aus" or "off" (? means any character) ,
 *  also disregarding case.<br />
 *  <br />
 *  Otherwise null is returned.<br />
 */
   public static Boolean asBoolObj(final CharSequence value){
      if (value == null) return null;
      final int len = value.length();
      if (len < 2 || len > 6) return null;
      char c1 = simpLowerC(value.charAt(0));
      char c2 = simpLowerC(value.charAt(1));
      if (c1 == 'f') {
        if (len < 5 || c2 != 'a') return null;
        if (simpLowerC(value.charAt(2)) != 'l') return null;
        if (simpLowerC(value.charAt(3)) != 's') return null;
        return Boolean.FALSE;
        
      } else if (c1 == 'n') {
        if (c2 == 'o' && len == 2) return Boolean.FALSE;
        if (len != 4) return null;
        return c2 == 'e' ? Boolean.FALSE : null;
      } 
      // to here only if not beginning with f or n 
      if (asBoolean(value)) return Boolean.TRUE;
      // now only aus and off as false, all else null
      if (len != 3) return null;
      char c3 = simpLowerC(value.charAt(2));
      if (c1 == 'a') {
         return c2 == 'u' && c3 == 's' ? Boolean.FALSE : null;
      } else if (c1 == 'o') {
         return c2 == 'f' && c3 == 'f' ? Boolean.FALSE : null;
      } else return null;
   } // asBoolObj(CharSequence) 

/** Interpret a character sequence as Boolean. <br />
 *  <br />
 *  This method returns true, if the sequence {@code value} is 
 *     "true", "wahr", "ja", "yes", "an" or "on"
 *  disregarding case.<br />
 *  This method returns false, if  the sequence {@code value} is 
 *     "fals?", "fals??", "false", "falsch", "ne??", "nein", 
 *     "no", "aus" or "off" (? means any character).<br /> 
 *  <br />
 *  Otherwise {@code def} is returned.<br />
 *  <br />
 *  Hint: Of all (three) {@link #asBoolean(CharSequence) asBoolean} 
 *  respectively {@link #asBoolObj(CharSequence) asBoolObj} methods this is
 *  the most efficient (fastest) as the parsing is steered (shortened) by the
 *  default value supplied.<br />
 */
   public static boolean asBoolean(final CharSequence value, 
                                                         final boolean def){
      if (value == null) return def;
      final int len = value.length();
      if (len < 2 || len > 6) return def;
      char c1 = simpLowerC(value.charAt(0));
      char c2 = simpLowerC(value.charAt(1));
      boolean secA = c2 == 'a';
      if (c1 == 'f') { // starts by f (false, falsch, fals??, fals?)
        if (!def) return false; // don't cogitate longer if default is false
        if (len < 5 || !secA) return def;
        if (simpLowerC(value.charAt(2)) != 'l') return def;
        if (simpLowerC(value.charAt(3)) != 's') return def;
        return false;
      } // f  
      if (c1 == 'n') { // n no, nein ne??
         if (!def) return false; // don't cogitate longer if default is false
         if (c2 == 'o' && len == 2) return false; // no
         if (len != 4) return def; // thats clearly true here (by the way)
         return c2 != 'e'; // ne???
      } // begins with n

      // to here only if not beginning with  f or n
      // remain:  on an aus off wahr ja yes true
      if (c2 == 'n') { //only an an or or on
         if (def) return true; // no further mull over if default is true
         if (len == 2 && (c1 == 'o' || c1 == 'a')) return true; 
      } else if (secA) { // ja or wahr
         if (def) return true;
         if (len == 2 && c1 == 'j') return true;
         if (len != 4 || c1 != 'w') return false;
      }  // ja or wahr   // an or or on  

      // remain  aus off yes true wahr
      if (len < 3) return def;
      char c3 = simpLowerC(value.charAt(2));
      if (len == 4) { // ggf. true or wahr
         if (def) return true;
         char c4 = simpLowerC(value.charAt(3));
         if (c1 == 'w' && secA && c4 == 'r' && c3 == 'h') return true;
         if (c1 == 't' && c2 == 'r' && c3 == 'u' && c4 == 'e') return true;
      } // ggf. true or wahr  

      // remain  aus off yes 
      if (len != 3) return def;
      if (c1 == 'y') { // yes
         if (def) return true;
         if  ( c2 == 'e' && c3 == 's')  return true;       
      }
      
      // remain  aus off 
      if (!def) return false;
      if (c1 == 'a') {
         return !(c2 == 'u' && c3 == 's');
      } else if (c1 == 'o') {
         return !(c2 == 'f' && c3 == 'f');
      } else return def;
   } // asBoolean(CharSequence, boolean) 
   

/** Interpret a character sequence as Integer. <br />
 *  <br />
 *  {@code value} will be stripped from surrounding white space and perhaps
 *  enclosing quotes. If {@code value} is (then) empty (Integer)null will be 
 *  returned.<br />
 *  <br />
 *  Otherwise it is tried to interpret the (remaining) sequence as whole 
 *  number given in decimal, hexadecimal (0x) or octal (leading 0) notation.
 *  In case of success an {@link Integer} object wrapping the number is 
 *  returned and null in case of the no interpretation succeeded.<br />
 *  <br />
 *  Hint: This method is a comfortable and &quot; exception free wrapper&quot;
 *  for {@link Integer#decode(java.lang.String)}.<br />
 *  <br />
 *  @param   value the sequence to be interpreted as whole number
 *  @return  Integer object on success, null otherwise
 *  @see #trimUq(CharSequence, String)
 */
   public static Integer asIntObj(CharSequence value){
     String v = trimUq(value, null);
     if (v == null) return null;
     try {
        return Integer.decode(v);
     } catch (Exception e) {
        return null;
     }
   } // asIntObj(CharSequence) 


/** Interpret a character sequence as int with default value. <br />
 *  <br />
 *  {@code value} will be stripped from surrounding white space and perhaps
 *  enclosing quotes. If {@code value} is (then) empty {@code def} will be 
 *  returned.<br />
 *  <br />
 *  @param   value  the sequence to be interpreted as whole number
 *  @param   def    the substitute value
 *  @return         the number fitting value or def
 *  @see #trimUq(CharSequence, String)
 *  @see #asIntObj(CharSequence)
 */
   public static int asInt(CharSequence value, int def) {
      Integer iO = asIntObj(value);
      return iO == null ? def : iO.intValue();
   } // asInt(CharSequence, int)
   
/** Primitive lower case. <br />
 *  <br />
 *  For evaluating simple parameters, options, literals and so on 
 *  {@link String}.{@link String#toLowerCase() toLowerCase()} is very 
 *  frequently used by most programmers. That's a quite expensive operation.
 *  Under the circumstances named with only the the ISO9989-1 or even only the
 *  USASCII subset of Unicode the expenses may be a bit reduced by 
 *  {@link String#toLowerCase(java.util.Locale) toLowerCase(Locale.ENGLISH)},
 *  but still are high.<br />
 *  <br />
 *  This method just converts &quot;A..Z ..Ä..Ö..Ü..À..Ý&quot; to
 *  &quot;a..z ..ä..ö..ü..à..ý&quot;  very effectively and should be used 
 *  whenever nothing else is required.<br />
 *  <br />
 *  null returns null; empty {@code val} returns the empty String.<br />
 *  <br />
 *  @param val the character sequence to return as lower case String
 *  @see #simpLowerC(char)
 */
   public static String simpLowerC(final CharSequence val) {
      if (val == null) return null;
      final int len = val.length();
      if (len == 0) return ComVar.EMPTY_STRING;
      StringBuilder sB = null;
      convLoop: for (int i = 0; i < len; ++i) {
         char act = val.charAt(i);
         if (act < 'A') continue convLoop;
         if (act <= 'Z') {
            act += 32;
         } else if (act >= 0xC0 && act <= 0xDD) { // À .. Ý to à .. ý
            if (act == 0xD7) continue convLoop;   // not mult to div
            act += 32;
         } else continue convLoop; 
         if (sB == null) sB = new StringBuilder(val);
         sB.setCharAt(i, act);
      } // convLoop: for 
      if (sB != null) return sB.toString();
      return val.toString(); // String.toString() will return val
   } // simpLower(CharSequence)


/** Primitive lower case. <br />
 *  <br />
 *  For evaluating simple parameters, options, literals and so on 
 *  {@link Character}.{@link Character#toLowerCase(char) toLowerCase()} is 
 *  frequently used. That's a quite expensive operation.<br />
 *  <br />
 *  This method just converts &quot;A..Z ..Ä..Ö..Ü..À..Ý&quot; to
 *  &quot;a..z ..ä..ö..ü..à..ý&quot; very effectively and may be used if 
 *  nothing else is required.<br />
 *  <br />
 *  Hint: This method does <u>not</u> convert upper case case characters 
 *  outside Unicode's subset 0..255 (that's ISO 8859-1).<br />
 *  <br />
 *  @param val the character to convert to lower case (ISO 8859-1 subset)
 *  @see #simpLowerC(CharSequence)
 *  @see #simpCharEqu(char, char)
 *  @see #lowerC(char)
 */
   public static char simpLowerC(final char val) {
      if (val < 'A') return val;
      if (val <= 'Z') {
         return (char) (val + 32);
      }
      if (val >= 0xC0 && val <= 0xDD) { // À .. Ý to à .. ý
         if (val == 0xD7) return val;   // not mult to div
         return (char) (val + 32);
      } 
      return val; 
   } // simpLower(char)


/** A optimised lower case. <br />
 *  <br />
 *  This method is like {@link #simpLowerC(char)} except that it falls back to
 *  {@link Character}.{@link Character#toLowerCase(char) toLowerCase()} 
 *  for character values outside 0..255&nbsp;=&nbsp;0xFF (ISO 8859-1).<br />
 *  <br />
 *  @param val the character to convert to lower case
 *  @see #simpLowerC(char)
 *  @see #simpLowerC(CharSequence)
 *  @see #simpCharEqu(char, char)
 */
   public static char lowerC(final char val) {
      if (val < 'A') return val;
      if (val <= 'Z') {
         return (char) (val + 32);
      }
      if (val >= 0xC0 && val <= 0xDE) { // À .. Ý. to à .. ý.
         if (val == 0xD7) return val;   // not mult to div
         return (char) (val + 32);
      } 
      return val < 0x100 ? val : Character.toLowerCase(val);
   } //  lowerC(char)

/** A optimised upper case. <br />
 *  <br />
 *  This method is the counterpart to {@link #lowerC(char)}. It uses an 
 *  optimised algorithm for the range 0..255&nbsp;=&nbsp;0xFF (ISO 8859-1) and
 *  falls back to
 *  {@link Character}.{@link Character#toUpperCase(char) toUpperCase()} 
 *  for character values outside .<br />
 *  <br />
 *  Hint: It might be worth noting that even in Unicode's ISO 8859-1
 *  (&lt;= 255) subrange to upper and to lower are not totally symmetric due
 *  to two Greek lower case letters, whose upper case is not in this code 
 *  range.<br />
 *  <br />
 *  @param val the character to convert to lower case
 *  @see #simpLowerC(char)
 *  @see #simpLowerC(CharSequence)
 *  @see #simpCharEqu(char, char)
 */
   public static char upperC(final char val) {
      if (val < 'a') return val;
      if (val <= 'z') {
         return (char) (val - 32);
      }
      if (val >= 0xE0 && val <= 0xFE) { // À .. Ý. <-- à .. ý.
         if (val == 0xF7) return val;   // not mult <-- div
         return (char) (val - 32);
      } 
      // asymmetries due to lower Greek at B5 and FF
      if (val == 0xB5) return (char)924; // upper of µ lower Greek asymmetry
      return val < 255 ? val : Character.toUpperCase(val);
   } //  lowerC(char)

/** Primitive character equals ignoring case. <br />
 *  <br />
 *  This method returns true <ul>
 *  <li>if the characters c1 and c2 supplied are equal or</li>
 *  <li>if the are equal ignoring case in Unicode's subset 0..255 (that's 
 *      ISO 8859-1).</li>
 *  </ul>
 *  Otherwise false is returned.<br />
 *  The latter happens also if characters above Unicode 256 differ only in
 *  case. That's the &quot;primitive&quot; respectively the price for 
 *  speed.<br />
 *  If this is not acceptable one should just use 
 *  {@link #simpCharEqu(char, char, boolean) simpCharEqu(c1, c2, true)} 
 *  instead, that will always yield the correct result. (That method will as
 *  last resort fall back to 
 {@link Character}.{@link Character#toLowerCase(char) toLowerCase()}.)<br />
 *  <br />
 *  @return true if c1 and c2 are really equal or equalIgnoreCase in Unicode's 
 *             ISO 8859-1 subset
 *  @see #simpLowerC(CharSequence)
 *  @see #simpCharEqu(char, char, boolean)
 *  @see #lowerC(char)
 *  @see #upperC(char)
 */
   public static boolean simpCharEqu(char c1, final char c2){
      if (c1 == c2) return true;
      final int dif = c1 -c2;

      if (dif > 0) { // may be c1 lower c2 upper
         if (dif != 32) return false;
         c1 = c2; 
      } else { // may be c1 upper c2 lower
         if (dif != -32) return false; 
      } // may be c1 upper c2 lower
      
      // now c1 is the possible upper wrt c2
      if (c1 < 'A') return false;  // A..
      if (c1 <= 'Z') return true; // ..Z
      if (c1 < 0xC0) return false; // À ..
      if (c1 > 0xDD) return false; // .. Ý
      if (c1 == 0xD7) return false; // but not div and mult
      return true; // different or too primitive to know about cases > 255
   } // simpCharEqu(2*char)

/** Character equals optionally ignoring case. <br />
 *  <br />
 *  This method returns true <ul>
 *  <li>if the characters c1 and c2 supplied are equal or</li>
 *  <li>if they are equal ignoring case in Unicode's subset 0..255 (that's 
 *      ISO 8859-1) or</li>
 *  <li>if they are equal when both converted to lower case.</li>
 *  </ul>
 *  Otherwise false is returned.<br />
 *  <br />
 *  This method avoids the expenses of {@link Character#toLowerCase(char)}
 *  as far as possible (i.e until the third step above).<br />
 *  <br />
 *  The two parameter companion method {@link #simpCharEqu(char, char)} avoids
 *  this effort anyway for the price of errors in Greek or Georgian alphabet
 *  regions. But that might be adequate in many database or programming / web
 *  language situations.<br />
 *  <br />
 *  @return true if c1 and c2 are really equal or equal ignoring case if 
 *                ignCase is true 
 *  @see #simpLowerC(CharSequence)
 *  @see #simpCharEqu(char, char)
 */
   public static boolean simpCharEqu(char c1, char c2, 
                                                    final boolean ignCase){
      if (c1 == c2) return true;
      if (!ignCase) return false;
      if (c1 < 0x100) { // inline of simpCharEqu( c1,  c2)
         final int dif = c1 - c2;
         //  System.out.println(" /// simpCharEqu(" + c1 + ", " + c2 + ") " 
            ///                     + (int)c1 + " - "  + (int) c2 + " = " + dif);
         /// if (c1 == c2) return true;
         
         if (dif > 0) { // may be c1 lower c2 upper
            if (dif != 32) return false;
            c1 = c2; 
         } else { // may be c1 upper c2 lower
            if (dif != -32) return false; 
         } // may be c1 upper c2 lower
         
         // now c1 is the possible upper wrt c2
         if (c1 < 'A') return false;  // A..
         if (c1 <= 'Z') return true; // ..Z
         if (c1 < 0xC0) return false; // À ..
         if (c1 > 0xDD) return false; // .. Ý
         if (c1 == 0xD7) return false; // but not div and mult
         return true; // equal or too primitive to know about cases > 255
      } // < 0x100
      c1 = Character.toLowerCase(c1);
      if (c1 == c2) return true;
      c2 = Character.toLowerCase(c2);
      return c1 == c2;
   } // simpCharEqu(2*char, boolean)
   
  /** A String compare optionally (optimised) ignoring case.
   * 
   *  This is a lexical String compare. s1 is considered greater if the first
   *  occurrence of a unequal character is greater or if it is longer.
   *  
   *  If ignCase is true the character ordering ignores case for the lower
   *  (ISO 8859-1) codes only, as would do 
   *  {@link #simpCharEqu(char, char) simpCharEqu(c1, c2)}. 
   * 
   *  
   *  @param s1 the first String to be compared
   *  @param s2 the second String to be compared
   *  @param ignCase true: compare ignore case (in the ISO8859-1 range only)
   *  @return 0: s1 and s2 are equal or the same; +: s1 is greater;
   *             -: s2 is greater
   *  @see #areEqual(CharSequence, CharSequence, boolean)
   */
   public static int compare(final CharSequence s1, 
                             final CharSequence s2, final boolean ignCase){
      if (s1 == s2) return 0; // includes (null == null)
      if (s1 == null) return -1; 
      if (s2 == null) return 1;
      final int n1 = s1.length();
      final int n2 = s2.length();
      final int min = (n1 <= n2) ? n1 : n2;
      over: for (int i = 0; i < min; ++i) {
          char c1 = s1.charAt(i);
          char c2 = s2.charAt(i);
          if (c1 == c2) continue over;
          if (!ignCase) return c1 - c2;
          c1 = simpLowerC(c1);
          if (c1 == c2) continue over;
          c2 = simpLowerC(c2);
          if (c1 == c2) continue over;
          return c1 - c2;
      } // for over
      return n1 - n2;
  } // compare(2*CharSequence, boolean)

/** Trim and &quot;un-quote&quot; a character sequence. <br />
 *  <br />
 *  Is the character sequence <code>val</code> null or empty the substitute
 *  {@code def} is returned. Usual choices for the default value 
 *  {@code def} are null or the empty String 
 *  ({@link ComVar#EMPTY_STRING}).<br />
 *  <br />
 *  Otherwise the character sequence <code>val</code> will be stripped from
 *  leading and  trailing white spaces (spaces, tabulators, feeds). If then
 *  empty, the substitute {@code def} is returned.<br />
 *  <br />
 *  Otherwise the (may be shortened) sequence <code>val</code> is returned as
 *  String, if it not begins and ends with double quotes (&quot;).<br />
 *  <br />
 *  In the latter case those surrounding quotes found are removed and the
 *  sequence is  returned as it then is (meaning even if now empty
 *  or perhaps surrounded by white spaces).<br />
 *  <br />
 *  @param val the character sequence to consider
 *  @param def substitute value for null or empty or white space only
 *  @return val without surrounding whitespace and quotes or def
 *  @see #getPropertyText(CharSequence, String)
 *  @see #getPropertyText(CharSequence, CharSequence, String)
 */
   public static String trimUq(final CharSequence val, final String def){
      if (val == null) return def; 
      int endInd = val.length();
      if (endInd == 0) return def;
      char c1 = val.charAt(0);
      --endInd;
      char c2 = endInd == 0 ? c1 : val.charAt(endInd);
      int anfInd = 0;
      while (c1 <= ' ' && anfInd < endInd) {
         ++anfInd;
         c1 = val.charAt(anfInd);
      }
      if (anfInd == endInd) { // through
         if (c1 <= ' ') return def;       //  now empty
         return Character.toString(c1);   //  length 1
      }  // through (the following can no more run empty
      while (c2 <= ' ' && endInd > anfInd) {
         --endInd;
         c2 = val.charAt(endInd);
      }
      if (anfInd == endInd) return Character.toString(c1);   // length 1
      
      if (c1 == '\"' && c2 == '\"' ) { // "..."
         ++anfInd;
         if (anfInd == endInd) return ComVar.EMPTY_STRING;
         --endInd;
         if (anfInd == endInd) Character.toString(val.charAt(endInd)); // 1L
      } // "..."
      
      ++endInd;
      if (val instanceof String) {
         return ((String)val).substring(anfInd, endInd);
      }
      return val.subSequence(anfInd, endInd).toString();
   } // trimUq(CharSequence, String)

//------------------------------------------------------------------------

/** Compare two character sequences, optionally ignoring case. <br />
 *  <br />
 *  This method compares the two given character sequences.<br />
 *  If both are null true is returned.<br />
 *  If both are empty true is returned.<br />
 *  Otherwise true is returned if and only if every character in s1 equals
 *  the corresponding character in s2, optionally ignoring case.<br />
 *  <br />
 *  @see #compare(CharSequence, CharSequence, boolean)
 */
   public static boolean areEqual(final CharSequence s1, 
                            final CharSequence s2, final boolean ignCase){
      if (s1 == s2) return true;
      if (s1 == null || s2 == null) return false;
      final int len = s1.length();
      if (len != s2.length()) return false;
      compLoop: for (int i = 0; i < len; ++i) {
         char c1 = s1.charAt(i);
         char c2 = s2.charAt(i);
         if (!simpCharEqu(c1, c2, ignCase)) return false;
      }
      return true;
   } // areEqual(2* CharSequence, boolean)

/** Compare a name and a pattern with wildcards. <br />
 *  <br />
 *  This method compares the (second parameter) {@code name} to a pattern
 *  {@code wildName}, that may contain the wildcard characters ? and * 
 *  (question mark and asterisk / starlet).<br />
 *  <br />
 *  {@code name} and {@code wildName} are considered as as equal if the 
 *  characters coincide, applying the extra rules for characters in 
 *  {@code wildName}:<ul>
 *  <li> ? coincides with any character of {@code name}.</li>
 *  <li> * coincides with any character sequence of {@code name} of any
 *       length including 0.<br />
 *  If a * {@code wildName} is followed by another character the 
 *  &quot;sequence of any length&quot; in {@code name} (the coincidence) ends 
 *  at the first occurrence of that character. From then on the comparing 
 *  goes on as described.</li>
 *  </ul>
 *  This method provides among others the file name comparison according to
 *  most file / operating systems wildcard rules including DOS and Windows,
 *  but goes a bit further.<br />
 *  <br />
 *  Examples:<br />
 *  A {@code name}  1234.567.890 would fit the pattern {@code wildName}<br />
 *   1234.567.890  ,&nbsp; 12??.567.890  ,&nbsp; 123*.890  ,&nbsp; 12*.*0
 *   &nbsp; and &nbsp;  123*4.567.890<br />
 *  but not the pattern<br />
 *   1234.567.89 ,&nbsp; 1234.567.890a ,&nbsp; 12?.567.890 ,&nbsp; 
 *   123*.567.890  &nbsp; and &nbsp;  123?*4.567.890<br />
 *  <br />
 *  <b>Hints</b>:<br />
 *  Command line parameters containing wildcards will be expanded to a number
 *  of parameters fitting all existing files' names (if any) by most Unix / 
 *  Linux shells. To mimic this behaviour, under all seen Windows JVMs 
 *  (java.exe, javaw.exe) emulate this dangerous behaviour. To avoid this one
 *  has to put those parameters in quotes. Some of Frame4J's methods and tools
 *  consider the plus sign (+) as equivalent to the wildcard asterisk, 
 *  featuring another &quot;expansion avoidance&quot;.<br />
 *  This method does not so; here the + is no *.<br />
 *  <br />
 *  @param wildName the pattern to match; included wildcards (* ?) are 
 *                  interpreted as described. In case of null or empty true is
 *                  returned.
 *  @param name     the name to match / compare. If name is null or empty (and
 *                  wildName not so) false is returned.
 *  @param ignoreCase if true the comparison is not case sensitive
 *  @return         true is matching according to rules described
 */
   public static boolean wildEqual(final CharSequence wildName, 
                          final CharSequence name, final  boolean ignoreCase){
      if (wildName == null) return true;
      final int wildlen = wildName.length();
      if (wildlen  == 0) return true;
      if (name == null) return false;
      final int namelen =  name.length();
      if (namelen == 0) return false;
      int i = 0; // index in wild
      int n = 0; // index in name
      boolean srchStFol = false;
      for ( ; i < wildlen  && n < namelen;  ++i, ++n ) {
         char wch = wildName.charAt(i);
         if (wch == '?') continue;  // ? matches any single character 
         if (wch == '*') { // * matches any sequence ending with ....
            if (++i == wildlen) return true;  
            wch = wildName.charAt(i);  // .. this character
            srchStFol = true;
         } // *
         char nameC = name.charAt(n);
         if (wch == nameC) {
            srchStFol = false;
            continue;
         }
         if (ignoreCase) {
            if (Character.toLowerCase(nameC) == wch 
                            || Character.toUpperCase(nameC) == wch) {
               srchStFol = false;
               continue;
            }
         } // ignoreCase
         if (srchStFol) { // search the starlet follower
            --i;
            continue;
         } // search the starlet follower
         return false; 
      } // for 
      return !(i < wildlen || n < namelen);
   } // wildEqual(2*CharSequence, boolean)   

//--------------------------------------------------------------------

/** Find and Replace a character sequence's parts (multiply). <br />
 *  <br />
 *  The character sequence {@code source} will be appended to the 
 *  StringBuilder {@code dest}. Hereby any sequence {@code oldText} (out of
 *  source} will be be replaced by {@code newText} (while transferring into 
 *  {@code dest}). If {@code ignoreCase} is true, the sequences 
 *  {@code oldText} are searched for disregarding case.<br />
 *  <br />
 *  Returned is the total number of oldText by newText replacements. In case
 *  of 0 (due to no occurrence of oldText in source) the StringBuilder
 *  {@code dest} is not touched.<br />
 *  <br />
 *  Hint: If the last part &mdash; not touching {@code dest} if no 
 *  replacements &mdash; is unwanted on has to do
 *  <code>dest.append(source)</code> on return of 0.<br />
 *  <br />
 *  The call is equivalent to:<br />  &nbsp; 
 {@link  #fUr(CharSequence, StringBuilder, CharSequence, CharSequence, CharSequence, boolean)
 *     fUr(source, dest, oldText, null, newText, ignoreCase)}<br />
 *  <br />
 *  @see de.frame4j.FuR
 */
   public static int fUr(final CharSequence source, final StringBuilder dest,
                  final CharSequence oldText, final CharSequence newText,
                  final boolean ignoreCase){
      return fUr(source, dest, oldText, null, newText, ignoreCase);
   } // fUr(CharSequence, StringBuilder, 2*CharSequence, boolean)  


/** Find and Replace character sequence's parts (multiply). <br />
 *  <br />
 *  This method is equivalent to the simpler 
 {@link #fUr(CharSequence, StringBuilder, CharSequence, CharSequence, boolean) fUr()}
 *  except for the following extras:<ul>
 *  <li> there is not just one pattern for the {@code source}'s sequence to be 
 *       replaced but a bracket of to pattern {@code oldStart} and 
 *       {@code oldEnd} that might enclose arbitrary other text to be 
 *       replaced by {@code newText}. </li>
 *  <li> By setting {@code ignoreWS} true all white space is ignored on
 *       matching {@code oldStart} and {@code oldEnd} to {@code source}.</li>    
 * </ul>      
 *  If {@code oldEnd} is null only {@code oldStart} is searched for in 
 *  {@code source}.<br />
 @see #fUr(CharSequence, StringBuilder, CleverSSS, CleverSSS, ReplaceVisitor)
 */ 
   public static int fUr(final CharSequence source, final StringBuilder dest,
                final CharSequence oldStart, final CharSequence oldEnd,
                    final CharSequence newText, final boolean ignoreCase){
      if (source == null || dest == null) return 0;
      int atl = oldStart == null ? 0 : oldStart.length();
      if (atl == 0) return 0;          // missing denotations
      int ql = source.length();
      int ael = oldEnd != null ? oldEnd.length() :  0;
      if (ql < (atl + ael)) return 0;  // no content or too short
      boolean ntd = newText != null && newText.length() > 0;
      if (ael == 0 && ntd   /// would be a replacement by
           && oldStart.equals(newText) ) return 0; // itself in each case
       
      int si   = 0;
      int vork = 0;
      while (si < ql) {
         int ei = indexOf(source, oldStart, si, ignoreCase);
         if (ei < 0) {
            if (vork == 0) break;
            dest.append(source.subSequence(si, ql));
            break;
         } 
         int endStart = ei + atl;
         if (ael != 0) {
            if (endStart + ael > ql)
               endStart = -1;
            else 
               endStart = indexOf(source, oldEnd, endStart, ignoreCase); 
            if (endStart < 0)  {
               if (vork == 0) break;
               dest.append(source.subSequence(si, ql));
                break;
            } // end not found
            endStart += ael;
         } // end pattern search
         ++vork;
         dest.append(source.subSequence(si, ei));
         if (ntd) dest.append(newText);
         si = endStart;
      } // while
      return vork;
   } // fUr(...)

/** Find and Replace character sequence's parts (multiple as visitor). <br />
 *  <br />
 *  The character sequence {@code source} will be appended to the 
 *  StringBuilder {@code dest}. Hereby any sequence in {@code source}
 *  consisting of<br /> &nbsp; 
 *  &nbsp;  {@code oldStart}, any sequence and {@code oldEnd}<br />
 *  is searched for.
 *  If {@code ignoreCase} is true the character comparison is made 
 *  disregarding case.<br />
 *  <br />
 *  For each found block (oldStart, text, oldEnd) the method
 *  {@link ReplaceVisitor#visit repl.visit()} is called with those three
 *  sequences as parameters. In the case of {@code ignoreCase}  true the
 *  found brackets are supplied and not {@code oldStart} and {@code oldEnd}
 *  which might differ in case. The value returned by the 
 *  {@link ReplaceVisitor}'s method 
 *  {@link ReplaceVisitor#visit visit()} will be put to {@code dest} instead
 *  of that &quot;block of three&quot;. If null is returned by the visitor
 *  that spot will be transfered unchanged and this will not be counted 
 *  as replacement.<br />
 *  <br />
 *  Returned is the total number of replacements. In case of no (0) 
 *  replacements the text {@code source} was transferred unmodified to the 
 *  {@link StringBuilder} {@code dest}.<br />
 *  <br />
 *  {@code oldEnd} is null only {@code oldStart} is searched for in 
 *  {@code source} and the {@link ReplaceVisitor}'s method 
 *  {@link ReplaceVisitor#visit visit()} will be called so
 *  {@link ReplaceVisitor#visit visit(null, oldStart, null)}. To be exact:
 *  in the case of {@code ignoreCase} true the original text fitting oldStart
 *  is supplied as parameter.<br />
 *  <br />
 *  This method implements the visitor pattern for arbitrary text 
 *  replacements by separating the single &quot;calculation of the replacement
 *  text&quot; (just a function on text for up to three) from doing all the 
 *  searching, matching and the replacing.<br />
 *  <br />
 *  @see ReplaceVisitor#visit ReplaceVisitor.visit()
 *  @return number of replacements 
 */ 
   public static int fUr(CharSequence quell, StringBuilder ziel,
                              String oldStart, String oldEnd,
                                    boolean ignoreCase, ReplaceVisitor repl){
      if (quell == null || ziel == null) return 0;
      int atl = oldStart == null ? 0 : oldStart.length(); // L Start
      int ql  = quell.length();                           // L source
      int ael = oldEnd == null ? 0 : oldEnd.length();     // L End
      if (repl == null  || atl == 0 || ql < (atl + ael) ) {
         ziel.append(quell);       // otherwise than both other  fUr()
         return 0;
      } // missing or too long search pattern of missing visitor
      
      int su   = 0;  // Search start  in source
      int sa;        // Start oldStart in source
      int ca   = 0;  // Start cont in source
      int se   = 0;  // Start oldEnd in source
      int vork = 0;  // Number of replacements
      
      String oB   = ael == 0 ? null : oldStart;
      String cont = ael == 0 ?  oldStart : null;
      String cB   = ael == 0 ? null : oldEnd;
      
      such: while (su + atl + ael <= ql) {
         sa = indexOf(quell, oldStart, su, ignoreCase); 
         if (sa < 0) { // not found
            ziel.append(quell.subSequence(su, ql));  // append rest
            break such;
         } // not found
         
         ca = se = sa + atl; // least start for oldEnd if given
 
         if (ael == 0) { // without End pattern
            if (ignoreCase)
               cont = quell.subSequence(sa, ca).toString();
         } else { // with End pattern
            if (se + ael > ql) {
               se = -1; // no more space for end pattern
            } else { 
               se =  indexOf(quell, oldEnd, ca, ignoreCase); 
            }            
            if (se < 0 ){ // not found
               ziel.append(quell.subSequence(su, ql));  // append rest
               break such;
            } // not found
            int set = se;
            se += ael; // there yonder
            if (ignoreCase) {
               oB = quell.subSequence(sa, ca).toString();   // start bracket
               cB = quell.subSequence(set, se).toString(); // end .. orig case 
            }
            cont = quell.subSequence(ca, set).toString();
         } // with End pattern
         String ersT = repl.visit(oB, cont, cB);
         if (ersT == null) { // returned null is order not to change this spot
            ziel.append(quell.subSequence(su, se));
         } else {
            ++vork;
            ziel.append(quell.subSequence(su, sa)).append(ersT);
         }
         su = se;
      } // such:  while 
      return vork;
   } // fUr(CharSequence, ... visitor)

/** Find and Replace character sequence's parts (multiple as visitor). <br />
 *  <br />
 *  The character sequence {@code source} will be appended to the 
 *  StringBuilder {@code dest}. Hereby any sequence in {@code source}
 *  consisting of<br /> &nbsp; 
 *  &nbsp;  {@code oldStart}, any sequence and {@code oldEnd}<br />
 *  is searched (by 
 *  {@link CleverSSS}.{@link CleverSSS#where(CharSequence, int)
 *    where(quell, index)}).<br />
 *  <br />
 *  For each found block (oldStart, text, oldEnd) the method
 *  {@link ReplaceVisitor#visit repl.visit()} is called with those three
 *  sequences as parameters. As oldStart and oldEnd the brackets found in
 *  quell are supplied to the visitor. (They might differ in case and length
 *  to the  {@link CleverSSS}'s internal search sequence.) The value returned
 *  by the {@link ReplaceVisitor}'s method 
 *  {@link ReplaceVisitor#visit visit()} will be put to {@code dest} instead
 *  of that &quot;block of three&quot;. If null is returned by the visitor
 *  that spot will be transfered unchanged and this will not be counted 
 *  as replacement.<br />
 *  <br />
 *  Returned is the total number of replacements. In case of no (0) 
 *  replacements the text {@code source} was transferred unmodified to the 
 *  {@link StringBuilder} {@code dest}.<br />
 *  <br />
 *  If {@code oldEnd} is null only {@code oldStart} is searched for in 
 *  {@code source} and the {@link ReplaceVisitor}'s method 
 *  {@link ReplaceVisitor#visit visit()} will be called so
 *  {@link ReplaceVisitor#visit visit(null, oldStart, null)}.<br />
 *  If {@code oldEnd} is not null, a (last) finding of  {@code oldStar}
 *  without closing {@code oldEnd} will not be regarded add treated as 
 *  hit.<br />  
 *  <br />
 *  This method implements the visitor pattern for arbitrary text 
 *  replacements by separating the single &quot;calculation of the replacement
 *  text&quot; (just a function on text for up to three) from doing all the 
 *  searching, matching and the replacing. By using {@link CleverSSS} objects
 *  as matchers any flexibility in optionally ignoring case or white space
 *  as well as {@link CleverSSS}'s inheritor's (like {@link RK} or
 *  {@link KMP}) speed is featured here.<br />
 *  <br />
 *  @see ReplaceVisitor#visit ReplaceVisitor.visit()
 *  @return number of replacements 
 */ 
   public static int fUr(final CharSequence quell, final StringBuilder dest,
                           final CleverSSS oldStart, final CleverSSS oldEnd,
                                                  final ReplaceVisitor repl){
      if (quell == null || dest == null) return 0;
      final int ql  = quell.length();                            // L source
      if (ql == 0) return 0; // nothing to append
      final int atl = oldStart == null || repl == null ? 0 
                                            : oldStart.length(); // L Start
      long weStart = -1L;
      if (atl != 0) weStart = oldStart.whereImpl(quell, 0, 0);
      
      if (weStart ==  -1L) { // nothing to replace
         dest.append(quell); // transfer the original fully and unchanged
         return 0;
      } // nothing to replace (no start found) or no replacer

      final boolean bothBrace = oldEnd != null && oldEnd.len != 0;
      final int ael = bothBrace ?  oldEnd.len : 0;            // L End
      final int minPattLen  = atl + ael;
      if (ql < minPattLen ) {
         dest.append(quell);  
         return 0;
      } // missing or too long search pattern of missing visitor
      
      int iSs = (int) weStart;  // start of start find (not -1 on first)
      int iSe = (int) (weStart >>> 32);  // end+1 of start find
      int iEs = -1;          // start of end find
      int iEe = iSe;         // end+1 of end find
      long weEnd = -1L;
      if (bothBrace) {
         weEnd = oldEnd.whereImpl(quell, iSe, 0);
         iEs = (int) weEnd;
         iEe = (int) (weEnd >>> 32);
      }
      
      if (iEe == -1) { // both braces and no first end
         dest.append(quell); 
         return 0;
      }  // both braces and no first end
      
      int vork = 0;
      int lastEnd = 0;
      String cont;
      String repS;
      
      replLoop: while(true) {
         final String fS = quell.subSequence(iSs, iSe).toString();
         if (bothBrace) {
            final String ob = fS;
            cont = quell.subSequence(iSe, iEs).toString();
            final String cb = quell.subSequence(iEs, iEe).toString();
            repS = repl.visit(ob, cont, cb);
            
         } else {
            cont = fS;
            repS = repl.visit(null, cont, null); 
         }
         if (repS == null) { // keep untouched
            dest.append(quell.subSequence(lastEnd, iEe));
            lastEnd = iEe;
         } else { // keep untouched else replace
            ++vork;
            dest.append(quell.subSequence(lastEnd, iSs));
            dest.append(repS);
            lastEnd = iEe;
         } // replace finding
         if (lastEnd + minPattLen >= ql) break replLoop; // no chance for next
         weStart = oldStart.whereImpl(quell, lastEnd, 0);
         iSs = (int) weStart;
         if (iSs == -1)  break replLoop; // no next
         iEe = iSe = (int) (weStart >>> 32);
         if (bothBrace) {
            weEnd = oldEnd.whereImpl(quell, iSe, 0);
            iEs = (int) weEnd;
            if (iEs == -1)  break replLoop; // no next
            iEe = (int) (weEnd >>> 32);
         }
      } //  replLoop
      
      if (lastEnd < ql) dest.append(quell.subSequence(lastEnd, ql));
      return vork;
    } // fUr( , 2*CleverSSS, visitor)

/** Find and Replace character sequence's parts (multiple, String). <br />
 *  <br />
 *  The character sequence {@code source} will be appended to the 
 *  StringBuilder {@code dest}. Hereby any sequence in {@code source}
 *  consisting of<br /> &nbsp; 
 *  &nbsp;  {@code oldStart}, any sequence and {@code oldEnd}<br />
 *  is searched (by 
 *  {@link CleverSSS}.{@link CleverSSS#where(CharSequence, int)
 *   where(quell, index)}).<br />
 *  <br />
 *  Each found block (oldStart, text, oldEnd) will be completely replaced by 
 *  the String {@code newText}. If that is null or empty that part is
 *  effectively removed.<br />
 *  <br />
 *  Returned is the total number of replacements. In case of no (0) 
 *  replacements the {@link StringBuilder} {@code dest} will not be 
 *  touched.<br />
 *  Hint: That behaviour differs from 
 {@link #fUr(CharSequence, StringBuilder, CleverSSS, CleverSSS, ReplaceVisitor)}
 *  !<br />
 *  <br />
 *  If {@code oldEnd} is null or empty only {@code oldStart} is searched for
 *  in {@code source} and the {@link ReplaceVisitor}'s method 
 *  {@link ReplaceVisitor#visit visit()} will be called so
 *  {@link ReplaceVisitor#visit visit(null, oldStart, null)}.<br />
 *  If {@code oldEnd} is not null, a (last) finding of  {@code oldStar}
 *  without closing {@code oldEnd} will not be regarded add treated as 
 *  hit.<br />  
 *  <br />
 *  This method implements the visitor pattern for arbitrary text 
 *  replacements by separating the single &quot;calculation of the replacement
 *  text&quot; (just a function on text for up to three) from doing all the 
 *  searching, matching and the replacing. By using {@link CleverSSS} objects
 *  as matchers any flexibility in optionally ignoring case or white space
 *  as well as {@link CleverSSS}' inheritor's (like {@link RK} or {@link KMP})
 *  speed is featured here.<br />
 *  <br />
 *  @see ReplaceVisitor#visit ReplaceVisitor.visit()
 *  @return number of replacements 
 */ 
   public static int fUr(final CharSequence quell, final StringBuilder dest,
                          final CleverSSS oldStart, final CleverSSS  oldEnd,
                                                       final String newText){
      if (quell == null || dest == null 
                        || oldStart == null || oldStart.len == 0) return 0;
      final int ql  = quell.length();                   // L source
      if (ql == 0) return 0; // nothing to append
      final int atl = oldStart.len;                // L Start
      long weStart = oldStart.whereImpl(quell, 0, 0);
      
      if (weStart == -1L) { // nothing to replace
         return 0;
      } // nothing to replace (no start found) or no replacer

      final boolean bothBrace = oldEnd != null && oldEnd.len != 0;
      final int ael = bothBrace ?  oldEnd.length() : 0;            // L End
      final int minPattLen  = atl + ael;
      if (ql < minPattLen ) {
         return 0;
      } // missing or too long search pattern of missing visitor
      
      int iSe = (int) (weStart >>> 32);  // end+1 of start find
      int iEs = -1;          // start of end find
      int iEe = iSe;         // end+1 of end find
      long weEnd = -1L;
      if (bothBrace) {
         weEnd = oldEnd.whereImpl(quell, iSe, 0);
         iEs = (int) weEnd;
         iEe = (int) (weEnd >>> 32);
      }
      
      if (iEe == -1) { // both braces and no first end
         return 0;
      }  // both braces and no first end
      
      int iSs = (int) weStart;  // start of start find (not -1 on first)

      
      int vork = 0;
      int lastEnd = 0;
      final boolean newTextEx = newText != null && newText.length() != 0;
      
      replLoop: while(true) {
         ++vork;
         dest.append(quell.subSequence(lastEnd, iSs));
         if (newTextEx) dest.append(newText);
         lastEnd = iEe;
         if (lastEnd + minPattLen >= ql) break replLoop; // no chance for next
         weStart = oldStart.whereImpl(quell, lastEnd, 0);
         iSs = (int) weStart;
         if (iSs == -1)  break replLoop; // no next
         iEe = iSe = (int) (weStart >>> 32);
         if (bothBrace) {
            weEnd = oldEnd.whereImpl(quell, iSe, 0);
            iEs = (int) weEnd;
            if (iEs == -1)  break replLoop; // no next
            iEe = (int) (weEnd >>> 32);
         }
      } //  replLoop
      if (lastEnd < ql) dest.append(quell.subSequence(lastEnd, ql));
      return vork;
    } // fUr(CharSequence, StringBuilder, 2*CleverSSS , String)


/** An &quot;indexOf&quot; ignoring case. <br />
 *  <br />
 *  This method is similar to 
 {@link java.lang.String#indexOf(java.lang.String,int) String.indexOf(String, int)}
 *  except for optionally ignoring case. Additionally by accepting any
 *  {@link CharSequence}s instead of Strings only this method is more
 *  versatile.<br /><br />
 *  <br />
 *  This method never throws any exception; if parameters don't allow the
 *  search -1 (not found) is returned.<br />
 *  <br />
 *  Hint: If the subsequence {@code sub} is searched for more than once using
 *  the same settings on ignoring case or white space it is better to
 *  {@link RK#make(CharSequence, boolean, boolean) make} a {@link CleverSSS} 
 *  object for it and use that's search methods.<br />
 *  <br />
 *  @param sequ the String to be searched within
 *  @param sub the Substring to be found in sequ
 *  @param sI  the index in sequ to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @param ignoreCase true means ignore case in the matching process
 *  @return find / start index (&gt;= sI) in sequ or -1
 */ 
  public static int indexOf(final CharSequence sequ, final CharSequence sub,
                                         int sI,  final boolean ignoreCase){
      if (sI < 0) sI = 0;
      if (sub == null) return sI;
      final int ls = sub.length();
      if (ls == 0) return sI;
      if (sequ == null) return -1;
      final int lk = sequ.length();
      if (lk == 0) return -1;
      final int mxSi = lk - ls;
      if (sI > mxSi) return -1;  // no space
      
      char othCas = sub.charAt(0);
      char subAct = othCas;
      if (ignoreCase) {
          subAct = lowerC(othCas);
          if (othCas == subAct) othCas = upperC(othCas);
      }
      if (ls == 1) { // special case char search only 
         for (; sI < lk; ++sI) {
            char seqAct = sequ.charAt(sI);
            if (seqAct == othCas) return sI;
            if (ignoreCase && seqAct == subAct) return sI;
         }
         return -1;
      } // special case char search only      
      
      ////// sliding hash algorithm (Rabin Karp)
      
      final char[] subSub = new char[ls];
      int subHash = 0;
      final char[] seqSub = new char[ls];
      int seqHash = 0;

      // the very first round
      boolean starEq = true;
      int j = sI;
      int i = 0;
      
      int iFac = 1;
      for (; i < ls; ++i, ++j) {
         if (i != 0) {
            iFac = (iFac * CleverSSS.primF)  & CleverSSS.mskMod;
            subAct =  sub.charAt(i);
            if (ignoreCase) {
               subAct =  lowerC(subAct);
            }
         } 
         subSub[i] = subAct;
         subHash = RK.extHash(subHash, subAct);
         char seqAct = sequ.charAt(j);
         if (ignoreCase) seqAct = lowerC(seqAct);
         seqSub[i] = seqAct;
         seqHash = RK.extHash(seqHash, seqAct);
         starEq = starEq && seqAct == subAct;
      } // for the first
      
      // subAct frozen from now on: its the last of sub 
      if (starEq) return sI;
      
      slideHash: for (++sI; sI<= mxSi ; ++sI, ++j, ++i) {
         if (i == ls) i = 0;
         char seqAct = sequ.charAt(j);
         if (ignoreCase) seqAct = lowerC(seqAct);
         long shc = seqHash - (seqSub[i] * iFac);
         seqHash = ( ((int)shc & CleverSSS.mskMod )
                              * CleverSSS.primF + seqAct ) & CleverSSS.mskMod;
         seqSub[i] = seqAct;

         if (seqHash != subHash) continue slideHash; // unequal hash: go on
         if (subAct != seqAct) continue slideHash;  // unequal last: go on

         for (int k = 0, m = i + 1; k < ls; ++m) { // character wise compare
            if (m == ls) m = 0;
            if (subSub[k] != seqSub[m]) continue slideHash;
            ++k; // omit last char compare (was done above)
         } // character wise compare
         return sI;
      } // slideHsh
      return -1;
   } // indexOf(CharSeq

/** An optimistic &quot;indexOf&quot; optionally ignoring case. <br />
 *  <br />
 *  This method is functionally equivalent to
 *  {@link #indexOf(CharSequence, CharSequence, int, boolean)}.<br />
 *  <br />
 *  The difference is that this method optimistically assumes that any 
 *  starting pattern of {@code sub} (also ignoring case if so opted) is not
 *  repeated within {@code sub}.<br />
 *  This is not (!) checked by this method. If the assumption is wrong, this
 *  method may return false results.<br />
 *  <br />
 *  <u>Warnin</u>g: Do not use this method if an unreliable
 *  &quot;optimistic assumption&quot; might lead to intolerable false results.
 *  In that cases better use 
 *  {@link #indexOf(CharSequence, CharSequence, int, boolean) indexOf()} or
 *  one of {@link CleverSSS} (see hint below) inheritors instead.<br />
 *  <br />
 *  The most simple case fulfilling the condition is {@code sub}'s first 
 *  character being unique in {@code sub} (optionally ignoring case).<br /> 
 *  <br />
 *  Examples: As parameter {@code sub} &quot;PD323S&quot;, &quot;aBB&quot; or
 *  &quot;&#036;Date: &quot; are OK, but &quot;PD3PD323S&quot; or 
 *  &quot;aaBB&quot; are not. The reason is that this method would not hit 
 *  &quot;...aaaBB...&quot;  by  &quot;aaBB&quot; e.g. <br />
 *  <br />
 *  The rationale behind relying on the assumption (to be made by the caller /
 *  programmer) is the guaranteed reduction from  O(n*m) to O(n) for the search
 *  complexity / run time with no extra efforts.<br />
 *  <br />
 *  Hint: The class {@link CleverSSS}'s  and its inheritors methods
 *  {@link CleverSSS#where(CharSequence, int) where()},
 *  {@link CleverSSS#indexOf(CharSequence, int) indexOf()},
 *  {@link CleverSSS#lastWhereImpl(CharSequence, int, int) lastWhere..} etc.
 *  switch to a similar optimistic search automatically.<br />
 *  <br />
 *  No exceptions are thrown; non adequate parameters return -1 .<br />
 *  <br />
 *  @param sequ the String to be searched within.
 *  @param sub the Substring to be found in {@code sequ}.
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @param ignoreCase true means ignore case in the matching process
 *  @return find / start index (&gt;= sI) in {@code sequ} or -1
 */ 
   public static int indexOfOpt(CharSequence sequ, CharSequence  sub,
                                               int sI, boolean ignoreCase){
      if (sI < 0) sI = 0;
      if (sub == null) return sI;
      final int ls = sub.length();
      if (ls == 0) return sI;
      if (sequ == null) return -1;
      final int lk = sequ.length();
      if (lk == 0) return -1;
      final int mxSi = lk - ls;
      int j = 1;
      final char subCo = sub.charAt(0);
      sequLoop: for (; sI <= mxSi ; sI += j, j = 1) {
         char cK = sequ.charAt(sI);   // compare the first
         if (!simpCharEqu(cK, subCo, ignoreCase)) continue sequLoop;
         subLoop: for (; j < ls; ++j) {
                 cK = sequ.charAt(sI + j);
            char cS = sub.charAt(j);
            if (!simpCharEqu(cK, cS, ignoreCase)) continue sequLoop;
         } // subLoop
         return sI; 
      } // sequLoop 
      return -1;
   } // indexOfOpt(2*CharSeqence, int, boolean)

//------------------------------------------------------------------

/** A lastIndexOf ignoring case. <br />
 *  <br />
 *  This method is similar to    
 *  {@link java.lang.String#lastIndexOf(java.lang.String)
 *  String.lastIndexOf(String)} but with case insensitive match and
 *  more freedom for the types {@link CharSequence} instead of String 
 *  only.<br />
 *  <br />
 *  The call is equivalent to    
 *  {@link #lastIndexOf(CharSequence, CharSequence, int, boolean)
 *     lastIndexOf(sequ, sub, -1, true)}.<br />
 *  <br />
 *  No exceptions: inadequate parameters return -1.<br />
 *  <br />
 *  @see #indexOf(CharSequence, CharSequence, int, boolean) indexOf()
 *  @param sequ the sequence, which is searched in.
 *  @param sub the (sub) sequence searched for.
 *  @return next found (index) or -1
 */ 
   public static int lastIndexOf(CharSequence sequ, CharSequence sub){
      return lastIndexOf(sequ, sub, -1, true);
   } // lastIndexOf(2*CharSequence)

/** A lastIndexOf ignoring case. <br />
 *  <br />
 *  This method is equivalent to   
 *  {@link java.lang.String#lastIndexOf(java.lang.String,int)
 *  String.lastIndexOf(String,int)} but with case insensitive match.<br />
 *  <br />
 *  No exceptions: inadequate parameters return -1.<br />
 *  <br />
 *  @see #indexOf(CharSequence, CharSequence, int, boolean) indexOf()
 *  @param sequ the sequence, which sub is searched in.
 *  @param sub the (sub) sequence searched for.
 *  @param sI  the character in sequ, the search is started 
 *             (default from the end).
 *  @return next found (index) or -1
 */ 
   public static int lastIndexOf(CharSequence  sequ,
                                            CharSequence  sub, int sI){
      return lastIndexOf(sequ, sub, sI, true);
   } // lastIndexOf(CharSeq
   
/** A lastIndexOf optionally ignoring case. <br />
 *  <br />
 *  This method is equivalent to 
 *  {@link java.lang.String#lastIndexOf(java.lang.String,int)
 *  String.lastIndexOf(String,int)} with the additional ability to 
 *  (optionally) ignore case.<br />
 *  <br />
 *  No exceptions: inadequate parameters return -1.<br />
 *  <br />
 *  @see #indexOf(CharSequence, CharSequence, int, boolean) indexOf()
 *  @param sequ the sequence, which white space is searched in
 *  @param sub the (sub) sequence searched for.
 *  @param sI  the character in sequ, the search is started 
 *             (default from the end).
 *  @param ignoreCase true: ignore case for fitting
 *  @return next found (index) or -1
 */ 
   public static int lastIndexOf(CharSequence  sequ,
                                 CharSequence  sub,
                                 int sI, boolean ignoreCase) {
      if (sequ == null) return -1;
      final int lk = sequ.length();
      if (lk == 0) return -1;
      int ls = sub == null ? 0 : sub.length();
      int mxSi = lk - ls;
      if (mxSi < 0)  return -1;
      if (sI < 0 || sI > mxSi) sI = mxSi;
      if (ls == 0 || sub == null) return sI; // || sub only to supr.warn.
      sequLoop: for ( ; sI >= 0; --sI) {
         subLoop: for (int j=0; j < ls; ++j) {
            char cK = sequ.charAt(sI + j);
            char cS = sub.charAt(j);
            if (cS == cK) continue subLoop;
            if (!ignoreCase) continue sequLoop;
            cK = Character.toLowerCase(cK);
            if (cS == cK) continue subLoop;
            if (cK != Character.toLowerCase(cS)) continue sequLoop;
         } // subLoop
         return sI;
      } // sequLoop 
      return -1;
   } // lastIndexOf(2*CharSequence,int,boolean)

/** A white space searching lastIndexOf. <br />
 *  <br />
 *  This method returns the last index &lt;= sI of white space's occurrence in 
 *  {@code sequ}.<br />
 *  <br />
 *  The definition of white space here / in this class simply &lt;= space (in
 *  accordance with {@link String#trim() String.trim()} &mdash; which
 *  means breaking white space.<br />
 *  <br />
 *  No exceptions: inadequate parameters return -1.<br />
 *  <br />
 *  @see #indexOf(CharSequence, CharSequence, int, boolean) indexOf()
 *  @param sequ the sequence, which white space is searched in.
 *  @return next occurrence &lt;= sI -1
 */ 
   public static int lastIndexOfWS(CharSequence sequ, int sI){
      if (sequ == null) return -1;
      final int lk = sequ.length();
      if (lk == 0) return -1;
      if (sI < 0 || sI >= lk) sI = lk -1;
      sequLoop: for ( ; sI >= 0; --sI) {
         char cK = sequ.charAt(sI);
         if (cK <= ' ') return sI;
      } // sequLoop 
      return -1;
   } // lastIndexOfWS(CharSequence,int)

/** Index of first mismatch optionally ignoring case. <br />
 *  <br />
 *  This method compares (a little bit like 
 *  {@link String}{@link String#equals(java.lang.Object) .equals(Object)}
 *  respectively {@link String#equalsIgnoreCase(java.lang.String)
 *  .equalsIgnoreCase(String)}) two character sequences  
 *  character by character.<br />
 *  <br />
 *  Returned will be the index of the first non matching character. A 
 *  mismatch hence will give a value in the range 0 to smaller length (or
 *  common equal length -1).<br />
 *  A full match will return -3.<br />
 *  If both (partial) sequences are empty or null -2 is returned.<br />
 *  One sequence empty or null returns -1.<br />
 *  <br />
 *  @param sequ The first character sequence (pattern from sI).
 *  @param sI   The spot in {@code sequ}, where the matching will start
 *              (&lt;0 is taken as 0)
 *  @param seq2 The second character sequence; matching object, starting index
 *              here is always 0.
 *  @param ignoreCase if true matching is not case sensitive
 *  @return The index (in seq2 or relative to sI) of the first mismatch;
 *             -3 : full match (including length of seq2 and length of sequ -sI);
 *             -2 : both null or empty;
 *             -1 : one  null or empty. 
 */ 
   public static int equalsTil(final CharSequence sequ, int sI,
                               final CharSequence  seq2,
                               final boolean ignoreCase){
      int ls = seq2 == null ? 0 : seq2.length();
      if (sI < 0) sI = 0;
      int lk = sequ == null ? 0 : sequ.length(); // remaining length 
      if (lk <= sI) {
         return ls == 0 ? -2 : -1;
      }
      if (ls == 0) return -1;
      int ind = 0;
      for ( ; ind < ls && sI < lk; ++sI, ++ind) {
         char cK = sequ.charAt(sI);
         char cS = seq2.charAt(ind);
         if (!simpCharEqu(cS, cK, ignoreCase)) return ind; 
      } // for
      if (ind == ls && sI == lk) return -3; // same length and equal
      return ind;
   } // equalsTil(CharSeq...boolean)

/** Index of first mismatch optionally ignoring case. <br />
 *  <br />
 *  This method applies the comparison of the method 
 *  {@link #equalsTil(CharSequence, int, CharSequence, boolean)} (with 
 *  sI = 0 and ignoreCase forwarded) to any pair of {@code sequences}.
 *  The maximum of all returned values is returned.<br />
 *  <br />
 *  This return value + 1 (this maximum + 1) is the minimal length of an 
 *  unambiguous abbreviation for all elements in {@code sequences}. Applied to
 *  English or German days of week the return value will be 1. This means, 
 *  that two characters (&quot;Mo&quot;) will unambiguously abbreviate days
 *  of week in these languages. For Portuguese days of week 2 would be 
 *  returned, as you need three letter abbreviations to distinguish 
 *  Seg./Sex.<br />
 *  <br />
 *  @param sequences The sequences to be pairwise matched
 *  @param ignoreCase if true matching is not case sensitive
 *  @return the maximum Index of all mismatches;
 *          -4, if sequences itself is null or shorter than 2 
 */ 
   public static int equalsTil(final CharSequence[] sequences,
                                              final boolean ignoreCase){
      if (sequences == null) return -4;
      final int leKe = sequences.length;
      int ret = -4;
      for (int i = 0, j = 1; j < leKe; ++i, j = i + 1) {
           CharSequence ket1 = sequences[i];
           for (; j < leKe; ++j) {
              int r = equalsTil(ket1, 0, sequences[j], ignoreCase);
              if (r > ret) ret = r;
           } // inner for
      } // outer for
      return ret;
   } // equalsTil(CharSeq[], boolean)


//--------------------------------------------------------------------------

/** The hexadecimal digits as char[]. <br /> */   
  private static final char[] zif = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
  
  private static final char[] ones = { // Integer.class improved
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 00
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 10
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 20
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 30
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 40
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 50
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 60
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 70
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 80
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 90
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // C0
                '0', '1', '2', '3', '4', '5', '6', '7' }; // 110..127

  private static final char[] tens = {
                '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
                '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
                '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
                '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
                '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
                '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
                '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
                '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
                '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
                '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
                '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', // 90
                '1', '1', '1', '1', '1', '1', '1', '1', '1', '1', // C0
                '2', '2', '2', '2', '2', '2', '2', '2' }; // 110..127
  
  private static final int[] times100 = {
   0, 100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300};
  
/** A hexadecimal digit as char. <br />
 *  <br />
 *  @param value the value for the least significant hexadecimal digit
 *  @return the hexadecimal digit 0..9A..F for value % 16 
 *          respectively value &amp; 0x0F 
 */   
 public static char zif(final int value){ return zif[value & 0x0F]; }  

/** A decimal digit as char. <br />
 *  <br />
 *  @param value the value in the range 0..100 for the least significant
 *         decimal digit
 *  @return the least significant decimal digit 0..9 for value;
 *         outside the range 0..127 the result is rubbish
 */   
  public static char dig(final int value){ return ones[value & 0x7F]; }  
 

/** Format as two digit hexadecimal number. <br />
 *  <br />
 *  The two digit hexadecimal representation of {@code value}'s lower 8 bits
 *  is returned as String.<br />
 *  <br />
 *  If the target is a StringBuilder, PrintWriter etc. take 
 *  {@link #twoDigitHex(Appendable, int)} instead of this method. 
 *  <br />
 *  @param  value  the number
 *  @return        the two digit hex of the LSB &quot;00&quot;..&quot;FF&quot;
 */
   public static String twoDigitHex(int value){
      value &= 0x0FF;
      if (value == 0) return "00";
      int lo = value & 0x0F;
      value >>= 4; // hi
      return String.valueOf(zif[value]) + zif[lo];
   } //  zweiStellHex(int)
   
/** The last exception when formatting. <br />
 *  <br />
 *  If the destination {@link Appendable} object is not a supplied or 
 *  generated {@link StringBuilder} but e.g. a  {@link PrintWriter} 
 *  {@link IOException}s may occur. They are caught silently in the 
 *  formatting method but recorded here.<br />
 *  Applications formatting to stream or writers might rest this value
 *  to null and check it afterwards.
 */
   public static Exception lastFormatingExc;

/** Format as two digit hexadecimal number. <br />
 *  <br />
 *  Appended to {@code dest} (generated StringBuilder if supplied as null) 
 *  is the two digit hexadecimal representation of {@code value}'s lower 8 
 *  bits.<br />
 *  <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity of 10
 *  @param  value  the number 
 *  @return        dest (appended is &quot;00&quot;..&quot;FF&quot;)
 *  @see #twoDigitDec(Appendable, int)
 *  @see #fourDigitHex(Appendable, int)
 *  @see #sixDigitHex(Appendable, int)
 *  @see #eightDigitHex(Appendable, int)
 */
   public static Appendable twoDigitHex(Appendable dest, final int value){
      if (dest == null) dest = new StringBuilder(10);
      try {
        dest.append(zif[(value & 0x0F0) >> 4]).append(zif[value & 0x0F]);
      } catch (IOException ex) { lastFormatingExc = ex; } // save & ignore       
      return dest;
   } // twoDigitHex(Appendable,  int)


/** Format as four digit hexadecimal number. <br />
 *  <br />
 *  Appended to {@code dest} (generated StringBuilder if supplied as null) 
 *  is the four digit hexadecimal representation of {@code value}'s lower 16 
 *  bits.<br />
 *  <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity of 10
 *  @return        dest (appended is  &quot;0000&quot;..&quot;FFFF&quot;
 *  @see #twoDigitHex(Appendable, int)
 *  @see #sixDigitHex(Appendable, int)
 *  @see #eightDigitHex(Appendable, int)
 */
   public static Appendable fourDigitHex(Appendable dest, final int value){
      if (dest == null) dest = new StringBuilder(10);
      try {
        dest.append(zif[ (value >> 12) & 0x0F ])
          .append(zif[ (value >>  8) & 0x0F ])
            .append(zif[(value & 0x0F0) >> 4]).append(zif[value & 0x0F]);
      } catch (IOException ex) { lastFormatingExc = ex; } // save & ignore 
      return dest;
   } // fourDigitHex(Appendable,  int)

/** Format as six digit hexadecimal number. <br />
 *  <br />
 *  Appended to {@code dest} (generated StringBuilder if supplied as null) 
 *  is the six digit hexadecimal representation of {@code value}'s lower 24 
 *  bits. This representation is quite usual for 24 bit RGB colours.<br />
 *  <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity of 12
 *  @return        dest (appended is &quot;000000&quot;..&quot;FFFFFF&quot;
 *  @see java.awt.Color
 *  @see #twoDigitDec(Appendable, int)
 *  @see #twoDigitHex(Appendable, int)
 *  @see #fourDigitHex(Appendable, int)
 *  @see #eightDigitHex(Appendable, int)
 */
   public static Appendable sixDigitHex(Appendable dest, final int value){
      if (dest == null) dest = new StringBuilder(12);
      try {
      dest.append(zif(value >> 20));
      dest.append(zif(value >> 16));
      dest.append(zif(value >> 12));
      dest.append(zif(value >>  8));
      dest.append(zif(value >>  4)).append(zif(value));
      } catch (IOException ex) { lastFormatingExc = ex; } // save & ignore 
      return dest;
   } // sixDigitHex(Appendable,  int)

/** Format as eight digit hexadecimal number. <br />
  *  <br />
  *  Appended to {@code dest} (generated StringBuilder if supplied as null) 
  *  is the six digit hexadecimal representation of {@code value}'s lower 24 
  *  bits. This representation is quite usual for 24 bit RGB colours.<br />
  *  <br />
  *  @param  dest   destination to append to; if null dest is made as
  *                 StringBuilder with initial capacity of 14
  *  @return        dest (appended is &quot;000000&quot;..&quot;FFFFFF&quot;
  *  @see java.awt.Color
  *  @see #twoDigitDec(Appendable, int)
  *  @see #twoDigitHex(Appendable, int)
  *  @see #fourDigitHex(Appendable, int)
  *  @see #sixDigitHex(Appendable, int)
  */
    public static Appendable eightDigitHex(Appendable dest, final int value){
       if (dest == null) dest = new StringBuilder(14);
       try {
         dest.append(zif(value >> 28));
         dest.append(zif(value >> 24));
         dest.append(zif(value >> 20));
         dest.append(zif(value >> 16));
         dest.append(zif(value >> 12));
         dest.append(zif(value >>  8));
         dest.append(zif(value >>  4)).append(zif(value));
       } catch (IOException ex) { lastFormatingExc = ex; } // save & ignore 
       return dest;
    } // eightDigitHex(Appendable,  int)

/** Byte array as separated sequence of two digit hex numbers. <br />
 *  <br />
 *  Appended to {@code dest} (generated StringBuilder if supplied as null) 
 *  will be the content of the array {@code bA} as a sequence of two digit
 *  hex numbers (like made by 
 *  {@link #twoDigitHex(Appendable, int) zweiStellHex()}). The numbers
 *  are separated by &quot;, &quot; (comma blank).<br />
 *  <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity reserve of 20
 *  @return dest
 */
   public static Appendable append(Appendable dest, final byte[] bA){
      return append(dest, bA, 0, -1);
   } // append(Appendable, byte[])

/** Part of byte array as separated sequence of two digit hex numbers. <br />
 *  <br />
 *  Appended to the (generated if supplied as null) StringBuilder {@code dest}
 *  will be the max. {@code len} bytes of the array {@code bA} starting from
 *  index {@code off} as a sequence of two digit hex numbers (like made by 
 *  {@link #twoDigitHex(Appendable, int) zweiStellHex()}). The numbers
 *  are separated by &quot;, &quot; (comma blank).<br />
 *  <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity reserve of 20
 *  @return dest
 *  @param  len wished number of bytes processed, will be limited if too
 *          large; &lt; 0 means as much as possible
 *  @param  off staring index in bA;  &lt; 0 is taken as 0
 */
   public static Appendable append(Appendable dest,
                                       final byte[] bA, int off, int len){
      if (bA == null) {
         if (dest != null) return dest;
         return new StringBuilder(20);
      }
      int start = 0;
      int end = 0;
      final int baL = bA.length;
      if (len < 0) len = baL;
      if (off > 0) start = off;
      end = start + len;
      if (end > baL) end = baL;         
      if (dest == null) dest = new StringBuilder(4 * len + 20);
      --end;
      for (int i = off; i <= end; ++i) {
        int wert = bA[i] & 0x00FF;
        int lo = wert & 0x0F;
        wert >>= 4; // hi
        try {
          dest.append(zif[wert]).append(zif[lo]);
          if (i != end) dest.append(", ");
        } catch (IOException ex) { lastFormatingExc = ex; } // save & ignore 
      } // for
      return dest;  
   } // append(Appendable, byte[], 2*int)

/** Format as two digit decimal number with leading zero. <br />
 *  <br />
 *  Appended to {@code dest} (generated StringBuilder if supplied as null) 
 *  is the two digit decimal representation of {@code value} if in the range
 *  0..99's. Otherwise two blanks are appended.<br />
 *  <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity of 6
 *  @param  value  the number 
 *  @return        dest (appended is &quot;00&quot;..&quot;99&quot;)
 */
    public static Appendable twoDigitDec(Appendable dest, final int value){
       if (dest == null) dest = new StringBuilder(6);
       try {
         if (value < 1 || value > 99) {
           dest.append(value == 0 ? "00" : "  ");
         } else {
           dest.append(tens[value]).append(ones[value]);
         }
       } catch (IOException ex) { lastFormatingExc = ex; } // save & ignore       
       return dest;
    } //  twoDigitDec(Appendable,  int)

/** Format decimal number right justified . <br />
 *  <br />
 *  Appended to {@code dest} (generated StringBuilder if supplied as null) 
 *  is the decimal representation of {@code value} filled from left with 
 *  blanks to get at least width (1..38).<br />
 *  <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity of width + 4
 *  @param  value  the number
 *  @param  width  the minimal field width (max. 38) filled from left with
 *                 blanks if the number is shorter.
 *  @return        dest (appended is &quot;00&quot;..&quot;99&quot;)
 */
    public static Appendable formDec(Appendable dest, final int value,
                                                                int width){
      final String erg = Integer.toString(value);
      final int ergL = erg.length();
      if (width < ergL) {
        width = ergL;
      } else if (width > 38) width = 38;
      if (dest == null) dest = new StringBuilder(width + 6);
      final int dif = width - ergL; // 6 - 2 
      try {
         if (dif > 0) dest.append(ComVar.BLANK_STRING.substring(0, dif)); 
         dest.append(erg);
       } catch (IOException ex) { lastFormatingExc = ex; } // save & ignore       
       return dest;
    } // formDec(Appendable, 2*int)

/** Format as two digit number with leading zero. <br />
 *  <br />
 *  A positive number will be formatted as two digit decimal number (tens
 *  and ones) with leading zero.<br />
 *  A negative {@code value} and 0 return &quot;00&quot;.<br />
 *  For a {@code value} &gt; 100 the modulo 100 will be taken.<br />
 *  <br />
 *  Application example is time and date formatting: 16.06.09 00:02:05<br /> 
 *  <br />
 *  @param  value  the number
 *  @return        something between &quot;00&quot; and &quot;99&quot;
 */
   public static String twoDigit(int value){
      if (value <= 0) return "00";
      if (value > 99) value %=100;
      char[] res = {tens[value], ones[value]};
      return String.valueOf(res);
   } // twoDigit(int)

/** Format as three digit number with leading zeros. <br />
 *  <br />
 *  A positive number will be formatted as three digit decimal number 
 *  (hundreds, tens and ones) with leading zero(s).<br />
 *  A negative {@code value} and 0 return &quot;000&quot;.<br />
 *  For a {@code value} &gt; 1000 the modulo 1000 will be taken.<br />
 *  <br />
 *  Application example is time formatting with milliseconds:  
 *  00:02:05,020<br /> 
 *  <br />
 *  @param  value  the number
 *  @return        something between &quot;000&quot; and &quot;999&quot;
 */
   public static String threeDigit(int value) {
      if (value <= 0) return "000";
      if (value > 999) value %= 1000;
      int hu = 0;
      if (value >= 100) {
        hu = value / 100;
        value -= times100[hu]; // hu * 100
      }
      char[] res = {ones[hu], tens[value], ones[value]};
      return String.valueOf(res);
   } // threeDigit(int)

/** Check (quite rawly) the syntax and uniform a MAC address. <br />
 *  <br />
 *  In the StringBuilder a MAC address in the forms  
 *  00:04:76:19:5C:5F or 
 *  00-04-76-19-5C-5F or 00&nbsp;04&nbsp;76&nbsp;19&nbsp;5C&nbsp;5F
 *  is expected and will be transformed to the form 00:04:76:19:5C:5F<br />
 *  <br />
 *  If length and allowed separators it, true is returned.<br />
 *  <br />
 *  @param leMac containing just one (correct) MAC address + nothing
 */
   public static boolean checkMac(StringBuilder leMac){
      if (leMac == null || leMac.length() != 17) return false;
      colLoo: for (int i = 2; i < 15; i = i + 3) {
         char akt = leMac.charAt(i);
         if (akt == ':') continue colLoo; 
         if (akt != '-' && akt != ' ') return false;
         leMac.setCharAt(i, ':');
      }
      return true;
   } // checkMac(StringBuilder)

/** Check and correct language code. <br />
 *  <br />
 *  Syntactically correct language codes are (to this method) just two lower
 *  case ASCII (a..z) letter Strings.<br />
 *  <br />
 *  The parameter language will be white space stripped. If it is two letters
 *  ASCII (a..z or A..Z) its lower case equivalent will be returned.<br />
 *  <br />
 *  Otherwise the same procedure will be taken for the parameter 
 *  defValue.<br />
 *  <br />
 *  If both are syntactically not acceptable or correctable 
 *  {@link ComVar#UL} or &quot;en&quot; will be returned.<br />
 *  <br />
 *  @param language the language (1st choice)
 *  @param defValue the language (default)
 *  @return a two letter (lower case) language code, never null
 *  @see #checkRegion(CharSequence, CharSequence)
 *  @see #trimUq(CharSequence, String)
 */
   public static String checkLanguage(CharSequence language, 
                                                      CharSequence defValue){
      String retLang = null;
      tryParams: while (language != null || defValue != null) {
         if (language != null) {
            retLang = trimUq(language, null);
            language = null;
         } else if (defValue != null) {
            retLang = trimUq(defValue, null);
            defValue = null;           
        }
        if (retLang == null) continue tryParams;
        if (retLang.length() != 2) continue tryParams;
        char c1 = retLang.charAt(0);
        char c2 = retLang.charAt(1);
        final boolean c1OK = c1 >= 'a' && c1 <= 'z';
        final boolean c2OK = c2 >= 'a' && c2 <= 'z';
        if (c1OK && c2OK) return retLang;
        if (c1 >= 'A' && c1 <= 'Z') {
           c1 += 32;
        } else if (!c1OK) continue tryParams;
        
        if (c2 >= 'A' && c2 <= 'Z') {
           c2 += 32;
        } else  if (!c2OK) continue tryParams;
        return new StringBuilder(2).append(c1).append(c2).toString();
      } // try use parameters
      if (ComVar.UL_UR_da) return ComVar.UL;
      return "en";
   } // checkLanguage(2*CharSequence)
   
/** Check and correct region code. <br />
 *  <br />
 *  Syntactically correct region codes are (to this method) just two upper
 *  case ASCII (A..Z) letter Strings.<br />
 *  <br />
 *  The parameter region will be white space stripped. If it is two letters
 *  ASCII (a..z or A..Z) its upper case equivalent will be returned.<br />
 *  <br />
 *  Otherwise the same procedure will be taken for the parameter 
 *  defValue.<br />
 *  <br />
 *  If both are syntactically unacceptable or uncorrectable 
 *  {@link ComVar#UR} or GB will be returned.<br />
 *  @param region   the region (1st choice)
 *  @param defValue the region (default)
 *  @return a two letter (upper case) region code, never null
 *  @see #checkLanguage(CharSequence, CharSequence)
 *  @see #trimUq(CharSequence, String)
 */
   public static String checkRegion(CharSequence region, 
                                                      CharSequence defValue){
      String retRegio = null;
      tryParams: while (region != null || defValue != null) {
         if (region != null) {
            retRegio = trimUq(region, null);
            region = null;
         } else if (defValue != null) {
            retRegio = trimUq(defValue, null);
            defValue = null;           
        }
        if (retRegio == null) continue tryParams;
        if (retRegio.length() != 2) continue tryParams;
        char c1 = retRegio.charAt(0);
        char c2 = retRegio.charAt(1);
        final boolean c1OK = c1 >= 'A' && c1 <= 'Z';
        final boolean c2OK = c2 >= 'A' && c2 <= 'Z';
        if (c1OK && c2OK) return retRegio;
        if (c1 >= 'a' && c1 <= 'z') {
           c1 -= 32;
        } else if (!c1OK) continue tryParams;
        
        if (c2 >= 'a' && c2 <= 'z') {
           c2 -= 32;
        } else  if (!c2OK) continue tryParams;
        return new StringBuilder(2).append(c1).append(c2).toString();
      } // tryParams
      if (ComVar.UL_UR_da) return ComVar.UR;
      return "GB";
   } // checkRegion(2*CharSequence)

//------------------------------------------------------------------------

/** Remove bracketed comments, optionally nested. <br />
 *  <br />
 *  Subsequences (named comments here) beginning with the single
 *  character {@code stC} (opening brace ( e.g.) and ending with {@code endC}
 *  (closing brace ) e.g.) will be removed from
 *  the {@code s} and replaced by {@code repl} (if that is not empty).<br >
 *  <br />
 *  If {@code nested} is true the parethis is nested. In that case within 
 *  &quot; with a ( com(en)t ) !&quot;  would  &quot;( com(en)t )&quot; be 
 *  recognised as comment and not  &quot;(&nbsp;com(en)&quot;.<br />
 *  <br />
 *  An application (the use case within Frame4J) is the removing of 
 *  bracketed (even nested) comments found in some time denotations got
 *  from the Internet.<br />
 *  <br />
 *  {@code s}'s end will be regarded as the closing of the comment even if
 *  the parethis might not have been closed. If and {@code stC} and 
 *  {@code endC} are equal {@code nested} is true everything starting 
 *  from the first occurrence of {@code stC} will be removed or replaced.<br />
 *  <br />
 *  If no comment is found {@code s}  is returned as is.<br/>
 *  <br />
 *  Hint: This method is not performing well for long Strings containing many 
 *  comments.<br />
 *  <br />
 *  @param s the String to &quot;uncomment&quot;
 *  @return s freed from comments (never null, may be empty 
 */
   public static String unComment(String s, final char stC, final char endC, 
                      final boolean nested, final CharSequence  repl){
      if (s == null) return ComVar.EMPTY_STRING;
      int limit = s.length(); 
      if (limit == 0) return ComVar.EMPTY_STRING;
      int i = s.indexOf(stC);
      suCo: while (i >= 0) { // replace () comments by repl
         int depth = 1;
         int j = i + 1;
         String begS = s.substring(0, i);
         if (repl != null) begS += repl;
         if (nested && stC == endC) return begS;
         String endS = null;
         ttc: while (j < limit) { // () comments
            char c = s.charAt(j);
            ++j;
            if (c == endC && --depth == 0) {
                if (j >=  limit) return begS; // comment is the end
                endS = s.substring(j);
                break ttc;
            } 
            if (nested && c == stC)  ++depth;
         } // ttc: comment 
         i = begS.length();
         s = begS + endS;
         limit = s.length();
         i = s.indexOf(stC , i); 
      } // suCo skip ()-Comments
      return s.toString();
   } // unComment(String, 2*char, boolean, CharSequence)

//-------------------------------------------------------------------------

/** Search a character sequence in a list / array. <br />
 *  <br />
 *  @return the index of text in list or -1 if not inside
 */   
    public static int indexOf(final String text, final String[] list){
       if (list == null) return -1;
       final int len =  list.length;
       if (len == 0) return -1;
       search: for (int i = 0; i < len; ++i) {
          final String akt = list[i];
          if (akt == text) return i; // null == null done
          if (akt == null) continue search;
          if (akt.equals(text)) return i;
       }
       return -1;
    } // indexOf(String, String[])

/** Working up of parameter arrays. <br />
 *  <br />
 *  This method returns an array of (parameter) Strings according to the 
 *  String array {@code args} supplied. The returned array is either empty
 *  or a newly created one; the latter may be a complete copy.<br />
 *  <br />
 *  If {@code args} is null or empty (length 0) the empty array 
 *  {@link de.frame4j.util.ComVar#NO_STRINGS} will be returned.<br />
 *  <br /> 
 *  All single Strings out of {@code args} will be put into the (new) array
 *  returned in their sequence. This copying ends where the (sub) sequence
 *  {@code commSt} (begin of comment) is detected within one such String 
 *  that is not enclosed in quotes.<br />
 *  <br />
 *  Strings (parameters) in  {@code args} that are null will not be copied
 *  to the new array.<br />
 *  <br />
 *  Due to the comment function and the masking of null parameters the
 *  returned array may be shorter than {@code args} and it's last String
 *  may also be shorter (by the comment removed) than its counterpart in
 *  the original {@code args}.<br />
 *  <br />
 *
 *  @param  args   the original parameter array 
 *  @param commSt  the start sequence for comment (if not null or empty)
 *  @param unQuote if true parameter enclosed in quotation marks (&quot;)
 *                 will get them removed
 *  @return        A new parameter array may be shortened or empty, but never
 *                 null
 */
   static public String[] prepParams(String[] args, 
                                final String commSt, final boolean unQuote){
      if (args == null) return ComVar.NO_STRINGS;
      final int argL = args.length;
      if (argL == 0) return ComVar.NO_STRINGS;
      String[] ret  = new String[argL];
      int commStL = commSt == null ? 0 : commSt.length();
      int newL = 0;
      for (String akt : args) {
         if (akt == null) continue;
         int aktL = akt.length();
         boolean quoted = aktL >= 2 && akt.charAt(0) == '\"'
                               && akt.charAt(aktL-1) == '\"';
         if (quoted) {
             if (unQuote) 
                akt = akt.substring(1, aktL-1);
         } else if (commStL != 0 && aktL >= commStL) {
             int cB = akt.indexOf(commSt);
             if (cB >= 0) { // comm found
                if (cB == 0) break;
                akt = akt.substring(0, cB);
                ret[newL] = akt;
                ++newL;
                break;
             }  // comm found
         }
         ret[newL] = akt;
         ++newL;
      }
      if (newL < argL) {
         if (newL == 0) return ComVar.NO_STRINGS;
         args = ret;
         ret  = new String[newL];
         System.arraycopy(args, 0, ret, 0, newL);
      }
      return ret;
   }  // prepParams(String[], String, boolean)

/** Make a parameter array from a character sequence (command line). <br />
 *  <br />
 *  This method returns a decomposition of the sequence {@code argLine} 
 *  to an array of Strings.<br />
 *  <br />
 *  {@code argLine} will be cut to pieces separated by white space or 
 *  enclosed in double quotes (&quot;). These pieces will be put into a 
 *  String array and returned as such.<br />
 *  <br />
 *  The first subsequence {@code commSt} (comment start) in one of the 
 *  pieces not in quotes will end that transfer to the target array.<br />
 *  <br />
 *  Not by chance, this decomposition is the one needed for separating
 *  arguments from command lines. This type of decomposition is quite often 
 *  needed, also for other use cases. This implementation does it better as it
 *  could be made by StringTokenizer or regular expressions.<br />
 *  <br />
 *  @param argLine the &quote;line&quot; with the parameters
 *  @param commSt  the start sequence for comment (if not null or empty)
 *  @param unQuote if true parameter enclosed in quotation marks (&quot;)
 *                 will get them removed
 *  @return        A new parameter array may be shortened or empty, but never
 *                 null
 *  @see #splitCsWS(CharSequence)
 */
   static public String[] prepParams(final CharSequence argLine,
                                 final String commSt, final boolean unQuote){
      final String argSt = trimUq(argLine, null);
      if (argSt == null)  return ComVar.NO_STRINGS;
      final int len  = argSt.length();
      int commI = len;
      int stInd = commSt != null ? commSt.length() : 0; // temp len comment

      if (stInd > 0 && stInd <= len) { // search comment start
         commI = argSt.indexOf(commSt);
         if (commI == 0) return ComVar.NO_STRINGS;
         if (commI < 0) commI = len;
      } // search comment start
      
      stInd = 0;
      int argZahl = (len + 1) / 2;
      int[] start = new int[argZahl];
      int[] end   = new int[argZahl];
      argZahl     = 0;

      parsLoop: while (stInd < commI) {
         if (argSt.charAt(stInd) == '\"') { // quoted
            if (++stInd >= len) break parsLoop;
            start[argZahl] = unQuote ? stInd +1 :stInd;
            stInd = argSt.indexOf ('\"', stInd);
            if (stInd < 0) 
               end[argZahl] = stInd = len;
            else if (stInd > commI) {
               commI = argSt.indexOf(commSt, stInd);
               if (commI < 0) commI = len;
               end[argZahl] = unQuote ? stInd - 1 : stInd;
            }
         } /*quoted*/ else {
            start[argZahl] = stInd;
            while (++stInd < commI && argSt.charAt(stInd) > ' '){} // empty
            end[argZahl] = stInd;
         } // unquoted
         argZahl++;
         while ((++stInd < commI) && (argSt.charAt(stInd) <= ' ')){} // empty
      } // parsLoop
      if (argZahl == 0) return ComVar.NO_STRINGS;
 
      String[] ret = new String[argZahl];
      for (commI = 0; commI < argZahl; ++commI)  {
         int s = start[commI];
         int e = end[commI];
         ret[commI] = s >= e ? ComVar.EMPTY_STRING : argSt.substring(s, e);
      } // for
      return ret;
   } // prepParams(CharSequence, String, boolean)
   

/** Make a parameter array from a character sequence (command line). <br />
 *  <br />
 *  This method returns a decomposition of the sequence {@code argLine} 
 *  to an array of Strings.<br />
 *  <br />
 *  {@code argLine} will be cut to pieces separated by white space or 
 *  enclosed in double quotes (&quot;). These pieces will be put into a 
 *  String array and returned as such.<br />
 *  <br />
 *  The decomposition will end at the piece with index {@code endArg}. If 
 *  {@code endArg} is low enough {@code return[endArg]} will contain 
 *  everything after the first whitespace separating the preceding 
 *  element.<br /> &nbsp;
 *   {@code prepParams(&quot;a bc &nbsp; u h xx &nbsp; &quot;, 2)} <br />
 *  will return &nbsp; &nbsp;
 *  { &quot;a&quot;,  &quot;bc&quot;, &quot;&nbsp; u h xx &nbsp; &quot;}<br />
 *  <br />
 *  The special case of {@code endArg} == 0 will return the  {@code argLine} 
 *  without preceding white space in one String packed in a String arrays 
 *  of length 1.<br />
 *  <br /> 
 *  @param argLine the &quote;line&quot; with the parameters
 *  @param endArg  the maximum index of the last Sting (element) returned
 *  @return        A new parameter array may be shortened or empty, but never
 *                 null
 *  @see #splitCsWS(CharSequence)
 *  @see #splitWords(String)
 *  @see #prepParams(CharSequence, String, boolean)
 */
   static public String[] prepParams(final CharSequence argLine,
                                                           final int endArg){
      if (argLine == null)  return ComVar.NO_STRINGS;
      int len  = argLine.length();
      if (len == 0) return ComVar.NO_STRINGS;
      int stInd = 0;
      char akt = argLine.charAt(stInd);
      while (akt <= ' ') {
         if (++stInd >= len) return ComVar.NO_STRINGS; //only white space beg
         akt = argLine.charAt(stInd);
      } // search first non white space
      String argSt = argLine.subSequence(stInd, len).toString();
      if (endArg == 0) return new String[] { argSt };
      len = len -stInd;
      stInd = 0;
      int argZahl = (len + 1) / 2; // max arg number
      if (endArg >= 1 && argZahl > endArg) argZahl = endArg + 1;
      int[] start = new int[argZahl];
      int[] end   = new int[argZahl];
      argZahl     = 0;

      parsLoop: while (stInd < len) {
         if (argSt.charAt(stInd) == '\"') { // quoted
            if (++stInd >= len) break parsLoop;
            start[argZahl] = stInd;
            stInd = argSt.indexOf ('\"', stInd);
            if (stInd < 0) stInd = len;
         } /*quoted*/ else {
            start[argZahl] = stInd;
            while (++stInd < len && argSt.charAt(stInd) > ' '){} // empty
         } // unquoted
         end [argZahl] = stInd;
         if (++argZahl == endArg) {
            if (++stInd >= len) break parsLoop; // beyond nothing more
            start[argZahl] = stInd;
            end[argZahl] = len; // last parameter = all
            ++argZahl;
            break parsLoop;
         }
         while ((++stInd < len) && (argSt.charAt(stInd) <= ' ')){} // empty
      } // parsLoop
      if (argZahl == 0) return ComVar.NO_STRINGS;
 
      String[] ret = new String[argZahl];
      for (stInd = 0; stInd < argZahl; ++stInd)  {
         int s = start[stInd];
         int e = end[stInd];
         ret[stInd] = s == e ? ComVar.EMPTY_STRING : argSt.substring(s, e);
      } // for
      return ret;
   } // prepParams(CharSequence, int)

/** Split a String to single words. <br />
 *  <br />
 *  This method returns a decomposition of the sequence {@code origContent} 
 *  to an array of Strings.<br />
 *  <br />
 *  {@code origContent} will be cut to pieces separated by white space, comma
 *  (,), semicolon (;), braces (()), quotes (&quot;'), ETligature
 *  (&amp;) or colon (:). Single characters so separated will be ignored. 
 *  These pieces will be put into a String array and returned as such.<br />
 *  <br />
 *  This decomposition is just the one needed for the use case of separating
 *  single words of word forms quite efficiently. It is done better here as it
 *  could be made by StringTokenizer or regular expressions.<br />
 *  <br />
 *  @param origContent the &quote;line&quot; with the parameters
 *  @return        A new String array containing the single words; may be
 *                 empty but never  null
 *  @see #prepParams(CharSequence, String, boolean)               
 */
   static public String[] splitWords(final String origContent){
      if (origContent == null)  return ComVar.NO_STRINGS;
      final int len  = origContent.length();
      int argZahl = (len + 1) / 2;
      int[] start = new int[argZahl];
      int[] end   = new int[argZahl];
      argZahl     = 0;
      int stInd = 0;
      parsLoop: while (stInd < len) {
         char c = origContent.charAt(stInd);
         if (c <= ' ' || c == ')' ||  c == '(' ||  c == '\"' 
            || c == '\'' ||  c == ',' ||  c == ';' 
               ||  c == '&') {
            ++stInd;
            continue parsLoop;
         } // search word start
         final int wS = stInd;
         final int minEnd = stInd;
         if (minEnd >= len) break parsLoop; // end 
         stInd = minEnd;
         searchEnd: while (stInd < len) {
            c = origContent.charAt(stInd);
            if (c <= ' ' || c == ')' ||  c == '(' ||  c == '\"' 
               || c == '\'' ||  c == ',' ||  c == ';'
                  ||  c == '&') break searchEnd;
            ++stInd;
         } // search word end
         start[argZahl] = wS;
         end[argZahl] = stInd;
         ++argZahl;
         ++stInd;
      } // parsLoop;
      if (argZahl == 0) return ComVar.NO_STRINGS;
      String[] ret = new String[argZahl];
      for (int commI = 0; commI < argZahl; ++commI)  {
         final int s = start[commI];
         final int e = end[commI];
         ret[commI] = origContent.substring(s, e);
      } // for
      return ret;
   } // splitWords(String)


/** Separating at comma, semicolon or white space. <br />
 *  <br />
 *  This method separates a character sequence ({@code input}) at comma,
 *  semicolon or white space, mostly like described for 
 *  {@link #getCommaSemicolonWSpattern()}. Additionally this method
 *  regards subsequences enclosed in quotes as one piece or token.<br />
 *  <br />
 *  An empty input sequence returns the empty String array.<br />
 *  An input sequence without occurrence of one of the separating characters 
 *  named (outside quotes) will be returned as String inside a String array 
 *  of length one.<br />
 *  <br />
 *  Example: &quot;, a ,, b \&quot;,,,a,,,\&quot;,, c;&quot; returns<br />
 *   &nbsp; &nbsp;
 *   { &quot;&quot;, &quot;a&quot;, &quot;&quot;, &quot;&quot;, 
 *     &quot;b&quot;, &quot;,,,a,,,&quot;, &quot;&quot;,
 *      &quot;c&quot;   }.<br />
 *  <br /> 
 *  Example 2: &quot;Z:;X:&quot; returns<br />
 *   &nbsp; &nbsp;  { &quot;Z:&quot;, &quot;X:&quot; }.<br />
 *  <br /> 
 *  
 *  Hint: Instead of this method one may be tempted to use 
 *  {@link java.util.StringTokenizer} or a regular expression like
 *   {@link #getCommaSemicolonWSpattern()} with
 *  {@link #getCommaSemicolonWSpattern()}.{@link
 *  Pattern#split(java.lang.CharSequence, int) split(input, -1)}. Regular
 *  expressions are even more costly than the 
 *  {@link java.util.StringTokenizer}, but would avoid the following 
 *  mistake:<br />
 *  A  {@link java.util.StringTokenizer}
 *  (using  <code>&quot; ,;\t\n\r&quot;</code> as pattern) would see  for
 *  <code>&quot;a ; ; , , b&quot;</code> only one instead of four separations 
 *  giving only two instead of five token returned by a suitable regular 
 *  expression and by this method.<br />
 *  <br />
 *  @param  input the character sequence to tokenise
 *  @return result array (all token)<br />
 *          the length may be maximal input.length() (if input is all
 *          comma e.g).<br />
 *          never null bat may be empty as {@link ComVar#NO_STRINGS}
 *  @see #getCommaSemicolonWSpattern()
 *  @see ComVar#NO_STRINGS
 *  @see #splitWords(String)
 */   
   public static String[] splitCsWS(CharSequence input){
      final String argSt = trimUq(input, null);
      if (argSt == null)  return ComVar.NO_STRINGS;
      final int len  = argSt.length();
      final int mxA = (len -1) / 2 + 1;
      int[] start = new int[mxA];
      int[] end   = new int[mxA];
      int argZahl = 0;
      int stInd   = 0;

      parsLoop: while (stInd < len) {
         boolean quoted = false;
         char aktChar = argSt.charAt(stInd);
         if (aktChar == ',' || aktChar == ';') { 
            start[argZahl] = stInd;   // start == end  = empty
         } else  if (aktChar == '\"') { // leer / quoted
            if (++stInd >= len) break parsLoop;
            start[argZahl] = stInd;
            quoted = true;
            stInd = argSt.indexOf ('\"', stInd);
            if (stInd < 0) { 
               stInd = len;  
               quoted = false;
            } 
         }  else {  // empty / quoted / normal
            start[argZahl] = stInd;
            // search end: end is ws or , or ;
            while (++stInd < len 
                     && (aktChar = argSt.charAt(stInd)) > ' '
                     && aktChar != ',' && aktChar != ';' ){} // empty
         } // not quoted
         end [argZahl] = stInd;
         argZahl++;
         if (quoted) ++stInd; // ignore closing " and at least one separator 
         while ((++stInd < len) && (argSt.charAt(stInd) <= ' ')){} // empty
      } // parsLoop
      
      if (argZahl == 0) return ComVar.NO_STRINGS;
      
      String[] ret = new String[argZahl];
      for (stInd = 0; stInd < argZahl; ++stInd)  {
         final int s = start[stInd];
         final int e = end[stInd];
         ret[stInd] = s == e ? ComVar.EMPTY_STRING : argSt.substring(s, e);
      } // for
      return ret;
      //  return getCommaSemicolonWSpattern().split(input, -1);
   } // splitCsWS(CharSequence)
   

/** Count of non empty Strings or arguments in a (parameter) array. <br />
 *  <br />
 *  This method return the number of non empty entries in a String array.
 *  The array is left untouched. <br />
 *  One application is to determine the number of start 
 *  {@link App#args parameters} not evaluated and cleared by 
 *  {@link App#go(String[]) partial} parsing.
 *  
 *  @param args    the parameter array
 *  @return        the corresponding (command) line
 *  @see #prepParams(String[])
 */
   public static int countParams(final String[] args){
     if (args == null) return 0;
     int argAnz = args.length;
     if (argAnz == 0) return 0;
     for (String arg : args) {
        if (arg == null || arg.length() == 0) {
          if (--argAnz == 0) return 0; // no more content to be found
        } // no content
     } // for
     return argAnz;
   } // countParams(String[])

/** Make an argument line from a parameter array. <br />
 *  <br />
 *  This method generates one (command) line from an array of arguments or
 *  parameters separated by one space each.<br />
 *  <br />
 *  Parameter, containing space ( ), semicolon (;), comma (,), question mark 
 *  (?),  asterisk(*) or sharp (#), will be set in quotes (&quot;) if not yet
 *  so.<br />
 *  <br />
 *  If there are no parameters the 
 *  {@linkplain ComVar#EMPTY_STRING empty String} is returned.<br />
 *
 *  @param args the parameters
 *  @return     the corresponding (command) line
 *  @see #countParams(String[])
 */
   public static String prepParams(final String[] args){
      if (args == null) return ComVar.EMPTY_STRING;
      final int argAnz = args.length;
      if (argAnz == 0) return ComVar.EMPTY_STRING;
      boolean notFirst = false;
      StringBuilder dest = null;
      for (int i = 0; i < argAnz; ++i) {
         String akt = args[i];
         if (akt == null) continue;
         if (notFirst) {
            dest.append(' ');
         } else {
           notFirst = true;
           dest = new StringBuilder(argAnz * 8);
         }
         boolean quote = shouldQuote(akt);
         if (quote) dest.append('\"');
         dest.append(akt);
         if (quote) dest.append('\"');
      }
      return notFirst ? dest.toString() : ComVar.EMPTY_STRING;
   } // prepArgs(String argLine, String)

/** Make a recommendation about quoting argument. <br />
 *  <br />
 *  It says if a sequence should be quoted as command line argument. <br />
 *  <br />
 *  Null or empty return true.<br />
 *  Begin and end by quote (already) returns false.<br >
 *  Any occurrence of ?, *, ;, ,(comma), # or space returns true.<br />
 *  <br />
 */
   public static boolean shouldQuote(CharSequence akt){
      int aktL = akt == null ? 0 : akt.length();
      if (aktL == 0) return true;
      if (aktL > 1 && akt.charAt(0) == '\"'
             &&  akt.charAt(aktL - 1) == '\"') return false;
      for (int i = 0; i < aktL; ++i) {
         char a =  akt.charAt(i);
         if (a == ' ' || a == ',' || a == ';' || a == '#'
              || a == '?' || a == '*') return true;
      }
      return false;
   } // shouldQuote(CharSequence)

/** Check / complete an e-Mail address. <br />
 *  <br />
 *  It will be checked if {@code adr} fulfils (at lest) the minimal / basic
 *  requirements on a robust (ASCCI) mail address. The server part, if 
 *  missing, will be added by the value of {@code defaultServer} resulting in
 *  adr@defaultServer.<br />
 *  <br />
 *  In the case of {@code forceServer} true a server part may already be given
 *  in the parameter {@code adr} but that must then be equal to 
 *  {@code defaultServer}.<br />
 *  <br />
 *  Returned is the accepted or completed address or null for all cases of
 *  rejection.
 *  <br />
 */
   public static String checkMail(final CharSequence adr, 
                                final CharSequence defaultServer, 
                                          final boolean forceServer){
      String givenAdr = trimUq(adr, null);
      if (givenAdr == null) return null;
      String givenDefault = trimUq(defaultServer, null);
      if (givenDefault.charAt(0) == '@') {
         givenDefault = givenDefault.length() == 1 ? null
                                 : givenDefault.substring(1); 
      }
      int giLen = givenAdr.length();
      int posAffe = givenAdr.indexOf('@', 0);
      boolean attachDefault = false;
      if (posAffe == -1) {
         if (givenDefault == null) return null;
         attachDefault = true;
      } else {
         if (posAffe < 2 || posAffe > giLen -3)  return null;
         if (forceServer && givenDefault != null) {
            if (!givenAdr.substring(posAffe + 1).equals(givenDefault))
               return null;
         } // force 
      }
      // put further grammar checks there (later)  
      return attachDefault ? givenAdr + '@' + givenDefault : givenAdr;
   } // checkMail

//-----------------------------------------------------------------------


/** Format a String array. <br />
 *  <br />
 *  The String array {@code sA} now named {@code name} will be output to
 *  {@code dest} (made as StringBuilder if supplied as null).<br />
 *  The multi-line format is equivalent to the Java's initialised String[]
 *  declaration syntax.<br /> 
 *  <br />
 *  @see #write(PrintWriter, CharSequence, CharSequence[])
 *  @param  dest   destination to append to; if null dest is made as
 *          StringBuilder with initial capacity of 50 + 16 * sA's length
 *  @param name the name to be given to sA;
 *               null will be taken as &quot;...&quot;
 *  @param sA    the String[] to be formatted / listed
 *  @param sLen  if &gt;= 0 and &lt; sA.length used to reduce the effective
 *               length of sA to format
 *  @return dest
 */
   public static Appendable format(Appendable dest, CharSequence name,
                           final CharSequence[] sA, final int sLen){
      int saL = sA == null ? 0 : sA.length;
      if (sLen >= 0 && saL > sLen) saL = sLen;
      if (dest == null) dest = new StringBuilder(50 + saL * 16);
      if (name == null) {
         if (sA == null) return dest;
         name = "...";
      }
      try {
        dest.append("  String[] ").append(name).append(" = ");
      if (sA == null) return  dest.append("null;"); 
      if (saL == 0)   return  dest.append("{};");
      dest.append('{');
      for (int i = 0; i < saL; ++i) {
        CharSequence akt = sA[i];
          if (akt == null) {
             dest.append("\n   null");
          } else {
             dest.append("\n   \"" ).append(akt).append('\"');
          }
          dest.append(", // [" + i + "]");
          
      } // for;
      dest.append("\n    };");
      } catch (IOException ex) { lastFormatingExc = ex; } // save & ignore  
      return dest;
   } // format(Appendable , CharSequence, CharSequence[], int)


/** Output a String array. <br />
 *  <br />
 *  The String array {@code sA} now named {@code name} will be output to
 *  {@code out}. The multi-line format is equivalent to the Java's
 *  initialised String[] declaration syntax.<br />
 *  This method does (almost) the same and in fact delegates to
 {@link #format(Appendable, CharSequence, CharSequence[], int) format(out, name, sA, 0},
 *  except for doing nothing when out is 0 and outputting always the whole
 *  String array  
 *  <br />
 *  @see #format(Appendable, CharSequence, CharSequence[], int)
 */
   public static void write(final PrintWriter out, final CharSequence name,
                                                    final CharSequence[] sA){
      if (out == null) return;
      format(out, name, sA, 0);
   } // write(PrintWriter, CharSequence, CharSequence[])
   
/** Formatting a runtime or period (long ms) as text. <br />
 *  <br />
 *  {@code dur} is duration in ms (milliseconds). According to its magnitude
 *  it will formatted in four ways, namely like 9788ms, 3599s, 1h00min
 *  or 1d00h00min<br />
 *  <br />
 *  @see de.frame4j.util.App#getExecTimeMs()
 *  @param  dest   destination to append to; if null dest is made as
 *          StringBuilder with initial capacity of 16
 *  @param dur the duration
 *  @return dest
 */
   public static StringBuilder formatDuration(StringBuilder dest, long dur){
      if (dest == null) dest = new StringBuilder(16);
      if (dur < 0) {
         dur = -dur;
         dest.append('-');
      } 
      if (dur < 9789) 
         return dest.append(dur).append("ms");
      int durs = (int)((dur + 500L) / 1000L); // seconds total
      if (durs < 3600) 
        return  dest.append(durs).append('s');
      durs /= 60;   // minutes total
      int min = durs % 60; // minutes 0..59
      durs /= 60;   // hours total
      if (durs < 24) {
         dest.append(durs).append('h').append(TextHelper.twoDigit(min));
         return dest.append("min");
      }
      int h = durs % 24; // hours 0..23
      durs /= 24;   // days
      dest.append(durs).append('d').append(TextHelper.twoDigit(h));
      dest.append('h').append(TextHelper.twoDigit(min));
      return dest.append("min");
   } // durationAsString()

/** Pattern for separation at comma, semicolon or white space. <br />
 *  <br />
 *  safe singleton<br />
 *  Hint: Pattern are threadsafe as immutable, Matchers are not.<br />
 */   
   static private class CommaSemicolonWSpattern {
      public final static Pattern commaSemicolonWSpattern =
                                     Pattern.compile("\\s*[,;\\|\\s]\\s*");
   } // CommaSemicolonWSpattern 

/** Pattern for separation at comma, semicolon or white space. <br />
 *  <br />
 *  This method returns a pattern, i.e. a compiled regular 
 *  expression for<br /> &nbsp; 
 *   (any white space)  &nbsp; 0 or  1 times (comma or semicolon)
 *   &nbsp;  (any white space) <br />
 *  in total at least one character (white space, comma or semicolon).<br />
 *  <br />
 *  This pattern is able to separate character sequences at just those spots
 *  and hence tokenise the usual (parameter) lists generated by humans or by
 *  tools (CSV etc.).<br />
 *  <br />
 *  A call <br /><code> &nbsp;
 *  TextHelper.getCommaColonWSpattern().split(&quot;a,b, c , d , e 
 *    ,, f g ,, &quot;, -1)</code><br />
 *  for example would get 10 token (as String[]), among those one empty
 *  before the &quot;f&quot; and two empty after the &quot;g&quot;.<br />
 *  <br />
 *  Hint: A leading white space before the example's &quot;a&quot; would get
 *  an extra empty first token (avoided by trimming before; see 
 *  {@link #trimUq(CharSequence, String)}).<br />
 *  <br />
 *  Hint 2: A call  <br /><code> &nbsp;
 *  TextHelper.getCommaColonWSpattern().split(string, -1)</code><br />
 *  is better (cheaper) replaced by  <br /><code> &nbsp;
 *  TextHelper.{@link TextHelper#splitCsWS(CharSequence)
 *   splitCcWS(string)}</code><br />
 *  <br />
 *  Hint 3: This method always returns the (singleton) object made at the 
 *  very first call. Pattern objects are threadsafe as immutable (Matchers by
 *  the way are not).<br />
 *  <br />
 *  Hint 4: {@link Pattern#split(java.lang.CharSequence, int) split()} is 
 *  preferable over
 *   {@link String#split(java.lang.String) String.split()}.<br /> 
 *  <br />
 *  @see Pattern#split(java.lang.CharSequence, int)
 *  @see String#split(java.lang.String)
 *  @see #splitCsWS(CharSequence)
 */   
   public static Pattern getCommaSemicolonWSpattern(){
      return CommaSemicolonWSpattern.commaSemicolonWSpattern;
   } // getCommaSemicolonWSpattern()
   

/** A simple message formatter. <br />
 *  <br />
 *  This method provides a basic &quot;message format&quot; for the frequent
 *  cases, where the class {@link java.text.MessageFormat} is much to 
 *  circumstantial or just overkill.<br />
 *  <br />
 *  {@link java.text.ChoiceFormat}, {@link java.util.Currency} etc. will not
 *  be supported here, however the most flexible time / date formatting by
 *  using pattern directly given in the format strings.<br />
 *  <br />
 *  For the ever recurring case of formatting just integer values an 
 *  int[] instead of the costly {@link Object Object[]} containing
 *  {@link Integer}s is accepted as well. Additionally the case of just one 
 *  parameter (the frequent {0} only case) is supported by supplying just the 
 *  object instead of a {@link Object new Object[]}{arg} with just that one 
 *  element.<br />
 *  <br />
 *  In pattern the simplest cases are like {0} {1} etc.; here all between the 
 *  braces including them is a placeholder just replaced by the the String
 *  representation {@link Object#toString() toString()} of the argument 
 *  indexed by the number. For theses simple cases pattern is compatible 
 *  between this method and the format()-methods of
 *  {@link java.text.MessageFormat}.<br />
 *  For an opening { brace to be considered as a placeholder and not go 
 *  through unprocessed the next character must be a digit 0..9 or a space.
 *  A double {{ goes as one { unprocessed.<br />
 *  <br />
 *  Is one argument  ({19} for example ) of type xxxtimexxx, everything
 *  between the last index digit and the closing } brace ({19D, M d Y, H:i:s}
 *  for example) is taken as format string to 
 *  {xxx ConstTime#toString(CharSequence)}. This controlled formatting of
 *  time (in this  <a href="package-summary.html#be">framework</a>) 
 *  considers the user, platform or application settings for the 
 *  language.<br />
 *  <br />
 *  Example with two pattern:<br />
 *  &quot;Die Anwendung {1} startete am{ 0 D, j. F Y, H:i:s T I}.&quot;<br />
 *  &quot;Application {1} started at{0 D, Y/d/m, H:i:s (T)}.&quot;<br />
 *  <br />
 *  Example of using those (as variable pat):<br /> &nbsp;
 *  log.println(TextHelper.messageFormat(null, pat, 
 *             uIInfo));<br />
 *  The  uIInfo used in the example is for {@link App} inheritors the object
 *  itself by grace of the method {@link App#getMessageComponent(int)}; a
 *  (inter) nationalised start message generated by this schema is returned by
 *  the method
 *  {@link App}.{@link App#twoLineStartMsg() twoLineStartMsg()}.<br />
 *  <br />
 *  If one argument  ({21} e.g.) of type {@link Boolean}, anything between 
 *  the last index digit and the closing } ({21Dateien?Datei} e.g. or
 *  {21chicken?mice}) is used as format String for the boolean value. If that 
 *  contains a ? this separates two parts the first taken for true and the 
 *  last for false. Without the ? the whole thing (except the index digits
 *  is taken for true and nothing for false. The same applies if the argument
 *  is a number (int or {@link Integer}), where value != 1 (= plural) is 
 *  taken as boolean; examples:<br />
 *  &quot;There {19are?is} {19} child{19ren}.&quot; or<br />
 *  &quot;{0} file{0s} + {1} director{1ies?y} of {2} deleted&quot;<br />
 *  <br />
 *  @param dest  StringBuilder to append to; if null it is made with a
 *                 starting capacity of 81
 *  @param pattern the pattern to append to 
 *  @param args    An array of the arguments; may be of type Object[] or
 *                 int[]; null or empty means pattern being appended to 
 *                 dest uninterpreted.<br />
 *                 If args is no array it is considered
 *                 as just one argument of index null.<br />
 *                 Additionally args may be an object of type 
 *                 {@link MessageComponents} whose method 
 *                 {@link MessageComponents#getMessageComponent(int)} will be
 *                 called to provide (dynamically) the arguments denoted in
 *                 pattern.
 *  @return dest
 *  @see de.frame4j.util.AppLangMap
 */
   public static StringBuilder messageFormat(StringBuilder dest, 
                             final CharSequence pattern, final Object args){
      if (dest ==  null) dest = new StringBuilder(81);
      final int patLen = pattern == null ? 0 : pattern.length();
      if (patLen == 0) return dest; // no pattern -> return unchanged
      int startSearch = 0;
      if (pattern instanceof String ) { // simple things first
         startSearch = ((String) pattern).indexOf('{');
         if (startSearch == -1) { // no { : no fields in pattern 
            return dest.append(pattern); // no { : pass pattern as is
         } // no { : no fields in pattern  
      } // simple things first on String pattern
      
      int argsLen = 0; // determine the number of arguments (if indexed)
      boolean isSiOb = false;                            // single Object
      boolean isMC = args instanceof MessageComponents; // MessComp
      boolean isOA = isMC || args instanceof Object[]; // indexed 
      if (isOA) {
         argsLen =  isMC 
                 ? ((MessageComponents)args).getMessageComponentsLength()
                 : ((Object[])args).length;
      } else if (args instanceof int[]) {
         argsLen =  ((int[])args).length;
      } else if (args != null) { // considered as single arg (toString value)
         argsLen = 1;
         isSiOb = true;
      }
      if (argsLen == 0) return dest.append(pattern); // 0 arg: pattern as is

      if (startSearch > 0) dest.append(pattern.subSequence(0, startSearch));
      
      // simple things done, now parse (through) the pattern
      boolean inArg  = false;
      boolean inZiff = false;
      boolean ziffFn = false;
      int argInd    = -1;
      int formStart = 0;
      int formEnd   = 0;
      thruPatt: for (int i = startSearch; i < patLen; ++i) {
         char ch = pattern.charAt(i);
         if (inArg) {
            if (ch == '}') { // end of {index [formatString]} -> do argument
               inArg = false;
               Object put = null;
               argDa: if (ziffFn && argInd < argsLen ) { // argument  is there
                  boolean bw = false;
                  boolean isBool = false;
                  int iw = 0;
                  boolean isInt = false;
                  String subPatt = formEnd >= formStart 
                     ? pattern.subSequence(formStart, formEnd + 1).toString()
                      : null;
                  if (isOA || isSiOb) {  // determining type 
                     put = isSiOb ? args : isMC 
                      ? ((MessageComponents)args).getMessageComponent(argInd)                   
                                                  : ((Object[])args)[argInd];
                     if (put instanceof Boolean) {
                        bw = ((Boolean)put).booleanValue();
                        isBool = true;
                     } else if (put instanceof Integer) {
                        iw = ((Integer)put).intValue();
                        isInt = true;
                    } else if (TimeHelper.instanceOfTime(put)
                                             && subPatt != null) { // Zt
                        put = TimeHelper.format(subPatt, put);
                        break argDa; // formatting completed
                     }  
                  } else {
                     iw = ((int[])args)[argInd];
                     isInt = true;
                  }  // Type determination
                  if (isInt && subPatt != null) {
                     bw = iw != 1;
                     isBool = true;
                     isInt = false;
                  }
                  if (isBool) { // boolean
                     if (subPatt != null) { // Boolean formatting
                        int frInd = subPatt.indexOf('?');
                        if (frInd < 0) {
                           put = bw ? subPatt : ComVar.EMPTY_STRING;
                        } else {
                           put = ComVar.EMPTY_STRING;
                           if (bw && frInd > 0) {
                              put = subPatt.substring(0, frInd);
                           } else if (!bw && ++frInd != subPatt.length()) {
                              put = subPatt.substring(frInd);
                           }
                        }
                     }   // Boolean formatting
                     break argDa; // formatting completed
                  } // boolean
                  if (isInt) { // int formatting               
                     put = Integer.toString(iw, 10);
                     break argDa; // formatting completed
                  }    
               } // argDa: argument is (principally) there 
               dest.append (put == null ? "null" : put.toString());
               continue thruPatt;
            } //  // end of {index [formatString]} -> do argument
            
            if (ch >= '0' && ch <= '9' && inZiff) {
               argInd = 10 * argInd + (ch - '0');
               ziffFn = true;
            } else if (ziffFn) {
               if (inZiff) {
                  inZiff = false;
                  formStart = formEnd = i;
               } else 
                  formEnd = i;
               }
         } else {  // inArg / not inArg
            if (ch != '{') {
               dest.append(ch);
               continue thruPatt;
            } // no {
            // here with starting {
            ++i;
            if (i == patLen) {
               dest.append('{');
               break thruPatt; // last char { : leave and out
            }
            ch = pattern.charAt(i);
            if (ch == '{') {
               dest.append('{');
               continue thruPatt; // double {{ :  escape as one { not as brace
            }
            argInd = 0;     // argument number (to be summed up by digits)
            ziffFn = false; // no digits and hence no argument number yet
            
            // must now be 0..9 or space followed by 0..9 to be placeholder
            // we only check 0..9 or space here
            if (ch >= '0' && ch <= '9') { // digit
               argInd = ch - '0'; // digit found
               ziffFn = true;    // OK we have (the start of) a number
            } else if (ch != ' ') { // no digit and no space
               dest.append('{').append(ch); // take as is and go on
               continue thruPatt;
            } // no digit and no space : never a place holder
            inArg  = true;
            inZiff = true;
            formEnd = -1;
         } // out arg   
      } // for
      return dest;
   } //  messageFormat(StringBuilder, CharSequence, Object)
   
/** Make MessageComponents from an array. <br />
 *  <br />  
 */
   public static MessageComponents makeMessageComponents(final Object[] oa){
      return new MessageComponents() {
         final int len = oa == null ? 0 : oa.length;

         @Override public Object getMessageComponent(int index){
            if (index < 0 || index >= len) return null;
            return oa[index];
         }

         @Override public int getMessageComponentsLength(){ return len; }
         
      }; // ano MessageComponents
   } // makeMessageComponents(Object[])
   
//========================================================================   
   
/** <b>Deliverer of messageFormat components</b>. <br />
 *  <br />
 *  An object of an implementing class might be used (instead of Object[])
 *  as parameter {@code args} for 
 *  {@link #messageFormat(StringBuilder, CharSequence, Object) 
 messageFormat(StringBuilder dest, CharSequence pattern, Object args)}.<br />
 *  <br />
 *  The effect is almost the same, with the possible advantages of <ul>
 *  <li> hiding the array from manipulation, which would then have to
 *       be effected by clone (see )</li>
 *  <li> triggering the getting of the information pieces just or only
 *       when really needed.</li>
 *  </ul>
 *  Clearly the second point is the real &quot;beauty&quot; of the approach,
 *  as it allows the triggering of message element's updates in the
 *  moment when and only if needed.<br />
 *  <br /> 
 */
   public static interface MessageComponents {

/** The index range of of messageFormat components. <br />
 *  <br />
 *  This method returns the {@code highest index} of a message component 
 *  {@code +1 } delivered
 *  by this object.<br />
 *  <br />
 *  Range: 1 .. max. index -1<br />
 *  <br />      
 *  @return the length
 */
      public abstract int getMessageComponentsLength();
      
/** Get a messageFormat component by its index. <br />
 *  <br />
 *  @param  index Range 0 .. {@link #getMessageComponentsLength()} - 1      
 *  @return the component or null if the component[index] is not available
 */
      public abstract Object getMessageComponent(int index);
   } // MessssageComponents ============
} // class TextHelper (22.11.2003, 29.04.2008, 18.05.2021)