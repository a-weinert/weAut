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
package de.frame4j.text;
import static de.frame4j.text.TextHelper.lowerC;
import static de.frame4j.text.TextHelper.simpCharEqu;

import java.util.Arrays;

import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;


/** <b>Subsequence search algorithms &nbsp;&mdash;&nbsp; the base</b>. <br />
 *  <br /> 
 *  An object of this type represents a character sequence to be searched
 *  for in other and longer character sequences. This class features a big 
 *  variety of &quot;indexOf&quot; implementation variants. A 
 *  {@link CleverSSS} object holds<ul>
 *  <li> all immutable state </li>
 *  <li> plus some settings </li>
 *  <li> and methods </li>
 *  <li> implementing some search algorithms being clever in the sense of
 *      O(n) instead of the usual O(n*m).</li>
 *  </ul>
 *  The rationale behind clever &quot;indexOf&quot; algorithms like Rabin
 *  Karp ({@link RK RK}, Knuth Morris Pratt ({@link KMP}) and others is their 
 *  very successful striving for O(n). The standard (&quot;naive&quot;
 *  loop-in-loop) algorithms, also used in {@link String} and consorts, are 
 *  of type O(n*m).<br />
 *  <br />
 *  This class and its extensions (like {@link RK} and {@link KMP}) hide their
 *  constructors behind factory respectively make() methods. Inheritors do
 *  neither have to handle the cases, where an 
 *  &quot;{@link #optimisticOK optimistic}&quot; search is 
 *  sufficient nor do they have to handle the the trivial cases of<ul>
 *   <li>subsequences to search for that are null, empty or shorter than
 *       two characters either from start,</li>
 *   <li>or after removing white space to be 
 *       {@link #ignoreWS ignored} optionally.</li></ul>
 *       
 *  This class handles those trivial optimistic, length one and empty cases.
 *  The latter are handled according to {@link String java.lang.String}:
 *  {@link String String} regards an empty subsequence as occurring everywhere
 *  or being every other {@link String}'s prefix. But the implementing classes
 *  factories must recognise those special cases and indirectly call this
 *  classes factories or constructors accordingly.<br />  
 *  <br />
 *  {@link CleverSSS} objects made or returned by the factories are immutable.
 *  The rationale behind making objects of this type immutable is their
 *  multiple and threadsafe re-usability. Inheritors must be immutable too
 *  and should preferably be final.<br />
 *  <br />
 *  As a {@link CharSequence} a {@link CleverSSS} object, besides providing
 *  the search algorithms, <b>is</b> at the same time (as of type
 *  {@link CharSequence}) the sub sequence to search for. In case of 
 *  {@link #ignoreCase} being true this {@link CharSequence sequence} is all
 *  lower case. In case of {@link #ignoreWS} it does not contain any blanks at
 *  all and may hence be significantly shorter than the {@link CharSequence}
 *  supplied to the factory method as the original search sequence.<br />
 *  <br />
 *  Motivation to use more clever substring searches (CleverSSS):<br />
 *  Subsequence searches of the {@link #indexOf(CharSequence, int) indexOf()}
 *  type are where heavy duty text processing tools &mdash; like 
 *  {@link de.frame4j.SVNkeys SVNkeys} or {@link de.frame4j.FuR FuR} &mdash;
 *  do spend most of their processing time [sic!]. For example, beautifying
 *  all SVN keys of all (some 200) Frame4J's source files by 
 *  {@link de.frame4j.SVNkeys SVNkeys}  once took over 15 seconds. Shifting to
 *  {@link RK} and then to {@link KMP} took this down to 600ms on the same
 *  machine. <br />
 *  It might well be put in those words: If a search for the same subsequence
 *  occurs more than twice in your application, using one of 
 *  {@link CleverSSS}'s inheritors is usually well worth the virtually nil
 *  extra effort.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2010 &nbsp; Albrecht Weinert  
 *  @see de.frame4j.text.RK
 */
 // so far    V.184+ (24.01.2010) : extracted from RK
 //           V.191+ (27.01.2010) : more burden (and better OO) put here
 //           V.o21+ (14.02.2010) : embed. class, off Kenai rev. jump
 //           V.o22+ (15.02.2010) : []where added
 //           V.111+ (03.06.2015) : minor comment corrections (MakeIndex out) 

@MinDoc(
   copyright = "Copyright 2010  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 56 $",
   lastModified   = "$Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "extend for clever substring search",  
   purpose = "common type for clever searches like Rabin Karp (RK), KMP etc."
) public abstract class CleverSSS 
                              implements CharSequence, Comparable<CleverSSS> {
   

/** The prime factor used in the (RK sliding) hash. <br /> 
 *  <br />
 *  This constant is used in the (sliding) hash and is vital for the Rabin
 *  Karp {@link RK (RK)} implementations / inheritors of this class.<br />
 *  <br />
 *  Remark: In theory the value should be larger (~2**16) due to Unicode but 
 *  we expect mostly 8 bit (ASCII , ISO8859-1 range) values. This assumption
 *  will not be true for use in Eastern Asia. The hash quality might even then 
 *  be sufficient for {@link RK RK}, but that has to be explored then.<br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #hashCode()
 *  @see #mskMod
 */      
   public final static int primF = 347; 
 

/** The modulo mask used in the (RK sliding) hash. <br />
 *  <br />
 *  Hint: A large prime number a and real modulo instead of a binary power
 *  would give a still better hashing quality. The rationale for bit masking
 *  is speed and, in the first place, avoiding all dangers of overflow (into
 *  sign bit) as well as Java's kinky modulo (%) sign effects.<br /> 
 *  <br />
 *  Hint 2: Having the constants {@link #primF} and {@link #mskMod} here is 
 *  transporting {@link RK Rabin Karp}'s implementation details to its (this)
 *  superclass. The rationale behind this design decision is to have the same 
 *  {@link CleverSSS#hashCode() hash} algorithm in all {@link CleverSSS}
 *  types. And that is by nature clearly determined by
 *  {@link RK Rabin Karp}.<br />
 *  <br /> 
 *  Value: <code>{@value}</code> &nbsp;= 0x3FFFFFFF =  0x40000000-1
 *  @see #hashCode()
 *  @see #primF
 */      
   public final static int mskMod = 0x3FFFFFFF; /// 0x40000000 -1
   

/** The sub sequence's (RK) hash value. <br /> */        
   volatile int  subHash = -1; // not possible as RK hash

/** The sub sequence's (RK) hash value. <br /> 
*  <br />
*  {@link CleverSSS} objects, like {@link RK} and {@link KMP}, may very well
*  be used as (hash) keys. In the case of {@link #ignoreCase} true the hash
*  value respectively the hashing function is not case sensitive. This would
*  be a feature not available with {@link String}s as keys.<br />
*  <br />
*  As the type of hash function is vital to the {@link RK Rabin Karp} 
*  algorithm but irrelevant here {@link KMP} uses {@link RK}'s hash function
*  for consistency within the type {@link CleverSSS}.<br />
*/        
   @Override public final int  hashCode(){ 
      if (subHash == -1) synchronized (this) {
      // we don't care calculating multiple times in the very improbable case
      // of a -1 hash value. 
      // At present (Jan. 2010) that cannot happen as RK hashes are >= 0.
         int hv = 0;
         for (char charIn : subC ) {
            hv = ((hv & mskMod) * primF + charIn)  & mskMod;
         }
         subHash = hv;
      }
      return subHash; 
   } // hashCode()

/** Compare with other CleverSSS object for same state. <br />
 *  <br />
 *  This method returns true if the {@code other} object is of type 
 *  {@link CleverSSS} and has (completely) the same state.<br />
 *  Note that this equality of both the contained (sub) sequence and the 
 *  settings is slightly incompatible with 
 *  {@link #compareTo(CleverSSS) compareTo(other)}.<br />
 *  <br />      
 */
   @Override public final boolean equals(final Object other){
      if (other == this) return true;
      if (!(other instanceof CleverSSS)) return false;
      final CleverSSS oKMP = (CleverSSS) other;
      if (oKMP.len != this.len || oKMP.subHash != this.subHash) return false;
      if (oKMP.ignoreWS != this.ignoreWS 
                            || oKMP.optimisticOK != this.optimisticOK      
                      || oKMP.ignoreCase != this.ignoreCase) return false;
      if (oKMP.subC == this.subC) return true;
      if (!Arrays.equals(oKMP.subC, this.subC)) return false;
     //  this.subC = oKMP.subC; // share for future use; 
      return true;
   } //  equals(Object)

   
//---- common immutable state ---------------------------------------
   
/** The sub sequence's length. <br />
 *  <br />
 *  @see #length()
 */      
   public final int len;

/** The sub sequence's length. <br />
 *  <br />
 *  No object for this class will be made for an empty sub sequence to be 
 *  searched. Hence the length returned here will always be 1 or more. <br />
 */      
   @Override public final int length(){ return len; }


/** The internal character array. <br /> 
 *  <br />
 *  Implementation hint for subclass programmers:
 *  Do not modify its content, treat it as final.<br />
 */
   protected final char[] subC;


/** The content (sub sequence) as String. <br /> 
 *  <br />
 *  This method returns sequence to be searched for (i.e. the internal 
 *  character array) as String.<br />
 *  <br />
 *  This implementation fits to {@link CleverSSS}'s type {@link CharSequence}
 *  and the naive use in String concatenating expressions.<br /> 
 */      
   @Override public final String toString(){
      if (subAsString != null) return subAsString;
      if (this.len  == 0) return subAsString = ComVar.EMPTY_STRING;

      // We don't care if made twice or thrice by different threads, (hence no
      // sync. except for volatile). But we do if this expensive String 
      // construction is used again and again in usual naive (often just 
      // "+ concatenation") usage of this object (hence the effort).
      return subAsString = String.valueOf(subC);
   } // toString()
   volatile transient String subAsString;


/** The subsequence's value at {@code index}.<br />
 *  <br />
 *  This method exhibits the CleverSSS object as {@link CharSequence}.<br />
 *  <br />
 *  This method features unrestricted read access to the internal character 
 *  array and hence the sub sequence to search for. N.b.: CleverSSS objects
 *  are (also) of type {@link CharSequence}.<br /> 
 *  <br />
 *  @throws NullPointerException if this sequence is empty
 *  @throws ArrayIndexOutOfBoundsException  if index &lt; 0 
 *                                or &gt;= {@link #length() length}
 */
   @Override public final char charAt(final int index) 
               throws NullPointerException, ArrayIndexOutOfBoundsException {
      return subC[index];
   } // charAt(int)


/** A &quot;sub sub&quot; sequence (made as String) . <br /> */      
   @Override public final CharSequence subSequence(final int start, 
                                                            final int end){
      if (start >= end || start >= len) return ComVar.EMPTY_STRING;
      if (start < 0 || end > len) 
         throw new IndexOutOfBoundsException("start < 0 or end > length");
      return new String(subC, start, end - start);
   } // subSequence(2*int);


/** Compare with other CleverSSS object for order. <br />
 *  <br />
 *  This method returns a positive value if this object is regarded as
 *  greater than the {@code other} CleverSSS and a negative value if the
 *  {@code other} CleverSSS is regarded as greater.<br />
 *  <br />
 *  Greater means containing a {@link #length() longer} (sub) sequence or 
 *  if the {@link #length() length}s are equal the (sub) sequence being 
 *  greater by simple character value compare.<br />
 *  <br />
 *  Note that this method defines a natural ordering that is only partly
 *  consistent with {@link Object#equals(Object) equals()}: <br />
 *  {@code this.equals(other)} true implies that {@code this.compareTo(other)}
 *  returns 0. But not the other way round!<br />
 *  On the other hand {@code this.compareTo(other)} == 0 implies equal 
 *  subsequences to search for both objects. So, the consistency might be 
 *  adequate for most uses as {@link Comparable}.<br />
 *  <br />
 *  Note 2: Consider also the side effects of {@link #ignoreCase} and 
 *  {@link #ignoreWS}, that might be somewhat surprising, especially when set
 *  differently on both {@link CleverSSS} objects to compare.<br />
 *  <br />
 *  @throws NullPointerException if other is null   
 */
   @Override public final int compareTo(CleverSSS other){
      if (other == this) return 0;
      int lenDif = this.len - other.length();
      if (lenDif != 0) return lenDif;
      
      char[] oC = other.subC;
      if (this.subC == oC) return 0; // made same by clever construction
      for (int i = 0; i < len; ++i) {
            lenDif = subC[i] - oC[i];
            if (lenDif != 0) return lenDif;
      }
      return 0;
   } // compareTo(CleverSSS)


/** The comparison / search is not case sensitive. <br />
 *  <br />
 *  If true characters are converted to lower case for comparison,
 *  {@link #hashCode()} hashing and else.<br />      
 */
   public final boolean ignoreCase;

/** The comparison / search has to ignore all white space. <br />
 *  <br />
 *  If true characters &lt;= blank are to be ignored completely, not stored in
 *  the {@link #charAt(int) internal array}. They are not counted for the 
 *  subsequence's {@link #length() length} nor considered for 
 *  {@link #hashCode() hashing}.<br />
 *  <br />
 *  Hint: The definition of white space in this class is simply &lt;= space. 
 *  This is good practice and, e.g., in accordance with 
 *  {@link String#trim() String.trim()}.<br />
 *  <br />
 */
   public final boolean ignoreWS;


/** The very simple optimistic search is OK. <br />
 *  <br />
 *  True means a very simple O(n) search yields the correct results.<br /> 
 *  This is the case if the first character of the sub sequence is distinct
 *  from all others (optionally ignoring {@link #ignoreCase case} or
 *  {@link #ignoreWS white space}, as well as, of course, for empty or 
 *  length one sequences.<br />
 *  For {@link #optimisticOK} true all searches are handled in this class
 *  without calling the inheritor's 
 *  {@link #implAlgWhere(CharSequence, int, int, int) implementation} that 
 *  can restrict its cleverness to the &quot;hard&quot; requests.<br />
 */
   public final boolean optimisticOK;
   
/** The algorithm's (short) name. <br />
 *  <br />
 *  Short means something like
 *  &quote;RK (Rabin Karp)&quote;,
 *  &quote;KMP (Knuth etc.)&quote; or
 *  &quote;CleverSSS.Simple&quote;.<br />
 *  <br />
 *  Never null or empty; preferred length 16; 
 *  no surrounding white space.<br />   
 */
   public final String algName;

/** The state as String. <br /> 
 *  <br />
 *  This method returns information about the immutable state of this 
 *  {@link CleverSSS} object. The purpose is debugging or fabrication of
 *  often used searches. The result is usually multi line.<br />
 *  <br />
 */      
   public String state(){ return commonState().toString(); }

/** The (common) state as StringBuffer. <br />
 *  <br />
 *  @return same text as {@link #state()} 
 */
   public final StringBuffer commonState(){
      StringBuffer bastel = new StringBuffer(90);
      bastel.append(algName).append(" \"").append(toString());
      bastel.append("\"\n length: ").append(len);
      if (ignoreCase) bastel.append(" ignoreCase");
      if (ignoreWS) bastel.append(" ignoreWS");
      if (optimisticOK) bastel.append(" optimistic");
      bastel.append(",  hash: ").append(hashCode()).append("\n");
      return bastel;
   } // commonState()

   
// ----   making of CleverSSS ----------------------------------------------    
   
/** Constructor for subclass / factory use, no checks. <br />
 *  <br />  
 *  This constructor for superclass / factory use transfers all parameter
 *  values to (final) object state without any checks. It is the 
 *  responsibility of the inheritor's factory not to supply inconsistent or
 *  illegal parameters.<br />
 *  The only exception from this &quot;blind trust&quot; is
 *  {@link #optimisticOK} being forced to true regardless of the respective 
 *  parameter if {@code  subC} is null, empty or of length one.<br />
 *  <br />
 *  @param algName the algorithms short name; null or empty defaults to
 *                 &quot;CleverSSS (ext.)&quot;
 *  @param subC the sequence to search for
 *  @param optimisticOK true for trivial (length &lt;= 1) cases as well as
 *         those where an optimistic O(n) left to right search is feasible
 *  @see #ignoreCase
 *  @see #ignoreWS
 */
  protected CleverSSS(final String algName, 
                      final char[] subC, final boolean ignoreCase,
                      final boolean ignoreWS, final boolean optimisticOK){
     this.algName = TextHelper.trimUq(algName, "CleverSSS (ext.)");
     this.subC = subC;                        //123456789x123456
     this.len  = subC != null ? subC.length : 0;
     this.optimisticOK = this.len < 2 ? true : optimisticOK ;
     this.ignoreCase = ignoreCase;
     this.ignoreWS = ignoreWS;
  } // CleverSSS(char[]) 
  
/** <b>Simple substring search</b>, clever only for the short and optimistic 
 *                                                               cases. <br />
 *  <br />
 *  As all {@link CleverSSS#optimisticOK optimistic} cases are perfectly
 *  handled in the abstract class {@link CleverSSS} these cases are made 
 *  available hereby.<br />
 *  <br />
 *  Additionally this class provides the simple 
 *  {@link #implAlgWhere(CharSequence, int, int, int) loop in loop} search 
 *  algorithm and may so act as a reference implementation for all other 
 *  inheritors.<br />
 *  <br />
 *  @see CleverSSS enclosing class
 */
  @MinDoc(
     copyright = "Copyright 2010  A. Weinert",
     author    = "Albrecht Weinert",
     version   = "see enclosing class CleverSSS",
     lastModified   = "see enclosing class CleverSSS",
     lastModifiedBy = "see enclosing class CleverSSS",
     usage   = "make and use for optimistic substring search",  
     purpose = "make the searches implemented in (abstract) " +
     		                 "super class available if adequate"
   ) public static class Simple extends CleverSSS {

/** Constructor for the single character case. <br />
 *  <br />  
 *  If {@link #ignoreWS} is true and {@code subOne} is a space (&lt;=' ')
 *  this is considered as the empty sequence case.<br />
 *  <br /> 
 *  @param subOne single character search sequence
 *  @see #ignoreCase
 *  @see #ignoreWS
 */
     protected Simple(char subOne, final boolean ignoreCase,
                                                     final boolean ignoreWS){
        super("CleverSSS.Simple", subOne, ignoreCase, ignoreWS);
     } // Simple(char, 2*boolean) 

/** Constructor for the empty search sequence cases. <br />
 *  <br />  
 *  This constructor for superclass / factory use is for the trivial case of
 *  searching for an empty sequence. Empty means length 0 or null from start
 *  or after {@link #ignoreWS ignoring} white space if so requested.<br />
 *  <br />
 *  @see #ignoreCase
 *  @see #ignoreWS
 */
     protected Simple(final boolean ignoreCase, final boolean ignoreWS){
        super("CleverSSS.Simple", ignoreCase, ignoreWS);
     } // Simple(2*boolean) 
   
/** Constructor for superclass / factory use, no checks. <br />
 *  <br />  
 *  This constructor for superclass / factory use transfers all parameter
 *  values to (final) object state without any checks. It is the 
 *  responsibility of  respective factory not to supply inconsistent or
 *  illegal parameters.<br />
 *  The only exception from this &quot;blind trust&quot; is
 *  {@link #optimisticOK} being forced to true regardless of the parameter
 *  if {@code  subC} is null, empty or of length one.<br />
 *  <br />
 *  @param subC the sequence to search for
 *  @param optimisticOK true for trivial (length &lt;= 1) cases as well as
 *         those where an optimistic O(n) left to right search is feasible
 *  @see #ignoreCase
 *  @see #ignoreWS
 */
      protected Simple(final char[] subC, final boolean ignoreCase,
                         final boolean ignoreWS, final boolean optimisticOK){
        super("CleverSSS.Simple", subC, ignoreCase, ignoreWS, optimisticOK);
     } // Simple(char[], 3*boolean)

/** The algorithmic implementation of whereImpl(). <br />
 *  <br />
 *  The method {@link #whereImpl(CharSequence, int, int)} does all parameter 
 *  checks and handles all exceptional / illegal cases by returning -1. It
 *  also handles the {@link #optimisticOK optimistic} cases.<br />
 *  <br />
 *  As this class is manly for those optimistic cases, this method is not 
 *  called for those.<br />
 *  <br />
 *  This method implements the (not so clever) loop in loop search algorithm
 *  as is done so in {@link String} with the extra of handling both ignore 
 *  {@link CleverSSS#ignoreCase case} and  {@link CleverSSS#ignoreWS}. By that
 *  this is {@link CleverSSS}'s reference implementation for all its 
 *  inheritors.<br />
 *  <br /> 
 *  @param sequ the sequence to search in
 *  @param lk   its correct or shortened) length  
 *  @param sI   the starting index for the search
 *  @param mxSi the maximum possible value of the match index
 *  @return  first index and last index + 1 where sub was found (combined 
 *           in one long, first index in the lower 32 bits) or -1 if no match
 */
      @Override protected long implAlgWhere(final CharSequence sequ, 
                                        final int lk, int sI, final int mxSi){
         final char caF = subC[0];
         sequLoop: for (; sI <= mxSi ; ++sI) {
            char cK = sequ.charAt(sI);   // compare the first
            if (ignoreWS && cK <= ' ') continue sequLoop;
            if (!simpCharEqu(cK, caF, ignoreCase)) {
               continue sequLoop;
            }
            subLoop: for (int j = 1, k = sI + 1;;) {
               cK = sequ.charAt(k);
               ++k;
               if (ignoreWS && cK <= ' ') {
                  if (k == lk) return -1L;
                  continue subLoop;
               }
               char cS = subC[j];
               if (!simpCharEqu(cK, cS, ignoreCase)) continue sequLoop;
               if (++j == len) {
                  return ((long)(k) << 32) | sI; // [0] = sI, [1] = j + 1
               }
               if (k == lk) return -1L;
            } // subLoop
         } // sequLoop 
         return -1L;
      } // implAlgWhere(CharSequence,..) only called if optimisticOK is false
  
      
  } // Simple     ===========================================================
  
  
/** Constructor for subclass / factory use, single character. <br />
 *  <br />  
 *  If {@link #ignoreWS} is true and {@code subOne} is a space (&lt;=' ') this
 *  is considered as the empty sequence case.<br />
 *  <br /> 
 *  @param subOne single character search sequence
 *  @see #ignoreCase
 *  @see #ignoreWS
 *  @param ignoreCase true: ignore case
 *  @param ignoreWS   true: ignore white spaces
 */
  protected CleverSSS(final String algName, char subOne, 
                         final boolean ignoreCase, final boolean ignoreWS){
     this.algName = TextHelper.trimUq(algName, "CleverSSS (ext.)");
     if (ignoreWS && subOne <= ' ') {
        this.subC = null;
        this.len  = 0;
     } else {
        if (ignoreCase) subOne = lowerC(subOne );
        this.subC = new char[] {subOne};
        this.len  = 1;
     }
     this.ignoreCase = ignoreCase;
     this.ignoreWS   = ignoreWS;
     this.optimisticOK = true;
  } // CleverSSS(String, char, 2*boolean) 

/** Constructor for subclass / factory use, empty search sequence. <br />
 *  <br />  
 *  This constructor for superclass / factory use is for the trivial case of
 *  searching for an empty sequence. Empty means length 0 or null from start
 *  or after {@link #ignoreWS ignoring} white space if so requested.<br />
 *  <br />
 *  @see #ignoreCase
 *  @see #ignoreWS
 *  @param ignoreCase true: ignore case
 *  @param ignoreWS   true: ignore white spaces
 */
  protected CleverSSS(final String algName, 
                          final boolean ignoreCase, final boolean ignoreWS){
     this.algName = TextHelper.trimUq(algName, "CleverSSS (len0)");
     this.subC = null;
     this.len  = 0;
     this.optimisticOK = true;
     this.ignoreCase = ignoreCase;
     this.ignoreWS = ignoreWS;
  } // CleverSSS(String, 2*boolean) 

/** Make a CleverSSS object, to search for the empty substring. <br /> 
 *  <br /> 
 *  @return the clever substring searcher made
 */
   public static CleverSSS make(final boolean ignoreCase,
                                                   final boolean ignoreWS) {
      return new Simple(ignoreCase, ignoreWS);
   } // make(2*boolean)


/** Make a CleverSSS object, to search for a single character. <br />
 *  <br />  
 *  @param ignoreCase true: ignore case
 *  @param ignoreWS   true: ignore white spaces
 *  @return the clever substring searcher made
 */
   public static CleverSSS make(final char subOne, 
                         final boolean ignoreCase, final boolean ignoreWS){
      if (ignoreWS && subOne <= ' ')  return make(ignoreCase, ignoreWS);
      return new Simple(subOne, ignoreCase, ignoreWS);
   } // make(char, 2*boolean)


/** Make a &quot;stupid&quot; or simple reference implementation. <br />
 *  <br />
 *  This factory returns a &quot;{@link Simple simple}&quot; 
 *  {@link CleverSSS} object that is not so clever in the light of Rabin,
 *  Knuth &amp; alii by implementing the real searches (naively) by O(n*m)
 *  loop in loop (as does {@link String}.<br />
 *  <br />
 *  Nevertheless the trivial and optimistic cases are handled well so the 
 *  facilities and often the performance is better than {@link String}'s
 *  indexOf methods &mdash; non regarding the extra ability to optionally
 *  ignore {@link #ignoreCase case} and / or 
 *  {@link #ignoreWS white space}.<br />
 *  <br />
 *  @param sub the sequence to search for
 *  @see #ignoreCase
 *  @see #ignoreWS
 *  @param ignoreCase true: ignore case
 *  @param ignoreWS   true: ignore white spaces
 *  @return the clever substring searcher made
 */
   public static CleverSSS makeSimple(final CharSequence sub, 
                          final boolean ignoreCase, final boolean ignoreWS){
      final int ls = sub == null ? 0 : sub.length();
      if (ls == 0) return make(ignoreCase, ignoreWS);
      if (ls == 1) return make(sub.charAt(0),ignoreCase, ignoreWS);
      char[] ca = toCharA(sub, ignoreCase, ignoreWS);
      if (ca == null) return make(ignoreCase, ignoreWS);
      final char caF = ca[0];
      final int len = ca.length;
      if (ignoreWS &&  len == 1) { // now length 1 due to ignore WS
         return make(caF, ignoreCase, ignoreWS);
      } // now length 1 due to ignore white spaces
      
      // all trivial cases done so far
      // above lines may be copied to subclass factories
      
      boolean optimisticOK = true;
      for (int i = 1; i < len && optimisticOK; ++i) { // quite simple o-check
         optimisticOK = caF != ca[i];
      }  // quite simple check for optimistic OK

      return new Simple(ca, ignoreCase, ignoreWS, optimisticOK);
   } // makeSimple(CharSequence, 2*boolean)
   

/** Prepare a character array from a sequence. <br />
 *  <br />
 *  This helper method converts a character sequence to a character array
 *  optionally converting to lower case and optionally omitting white 
 *  spaces. A (then) empty sequence is returned as null.<br />
 *  <br />
 *  @param  sub the input sequence
 *  @param  ignoreCase true means convert to lower case
 *  @param  ignoreWS true means do not transfer white spaces 
 *  @return a non empty char array according to sub 
 *          (empty is returned as null)
 */
   public static char[] toCharA(final CharSequence sub, 
                          final boolean ignoreCase, final boolean ignoreWS){
      final int ls = sub == null ? 0 : sub.length();
      if (ls == 0) return null;
      int len = ls; // length, may be < ls if ignoreCase
      int j = 0; 
      char subAct = sub.charAt(0);

      if (ignoreWS) { // special treatment ignore white space --
         while (subAct <= ' ') { // look for the first character
            ++j;
            if (--len == 0) return null; // empty now
            subAct = sub.charAt(j);
         } // look for the first character
         
         if (len > 1) { // look for the last character
            for (int k = ls -1; k > j; --k, --len) {
               char act = sub.charAt(k);
               if (act > ' ') break;
            } // for last
         } // look for the last character if necessary
      } // special treatment ignore white space --

      if (ignoreCase) subAct = lowerC(subAct);
      char[] subSub = new char[len];
      subSub[0] = subAct;
      
      if (len == 1) { // special case 1 character only
         return subSub;
      } // special case 1 character only

      final int lk_fc = len;
      ++j; // previous done as subSub[0]
      
      prepCA: for (int i = 1; i < len && j < ls; ++j) { // for prepare char[]
         subAct = sub.charAt(j);
         if (ignoreWS && subAct <= ' ') {
            --len; // one shorter
            continue prepCA;
         }
         if (ignoreCase) subAct = lowerC(subAct);
         subSub[i] = subAct;
         ++i;
      } // for prepare char[]
      
      if (ignoreWS) { // ignore white space may have shortened length
         // Hint: len == 1 can not happen here, is already treated above.
         if (len < lk_fc) {
            char[] tmp = subSub;
            subSub = new char[len];
            System.arraycopy(tmp, 0, subSub, 0, len);
         }
      } // ignore white space may have shortened length
      return subSub;
   } // toCharA(CharSequence, 2*boolean)

/** Formate a long as int pair. <br />
 *  <br />  
 *  The long value val will be formatted as two int in the form
 *  &quot;( 123, 345 )&quot; wehre the first value will be the lower and the 
 *  second the upper 32 bit of the 64 bit long.<br />  
 */
   public static final StringBuilder asPair(StringBuilder bastel,
                                                            final long val){
      if (bastel == null) bastel = new StringBuilder(25);
      bastel.append("( ").append((int)val).append(", ");
      bastel.append((int)(val >>> 32)).append(" )");
      return bastel;
   }  // asPair(StringBuilder, long);
   
/** An int pair as long value. <br />
 *  <br />
 *  This helper method just packs the simple expression<br /> &nbsp; &nbsp;
 *   ((long)eI &lt;&lt; 32) | sI <br />
 *  for the sake of compact (JUnit) test code and else. In Frame4J's 
 *  production code it is not used instead of the expression.<br />
 *  <br />
 *  @param sI first int (start index) goes to the lower 32 bits
 *  @param eI first int (end index) goes to the upper 32 bits
 */
   public static long pair2long(int sI, int eI){
      return ((long)eI << 32) | sI;
   } // pair2long(2*int)
   

/** Formate a long array as int pairs . <br />
 *  <br />  
 *  The elements of long array value vals will be formatted as sequence of 
 *  two int in the form
 *  &quot;{ ( 123, 345 ) ( 678, 901) }&quot; like described for 
 *  {@link #asPair(StringBuilder, long)}. The formatting stops at the end 
 *  of the array or at the first element == -1L (exclusive).<br />
 */
   public static final StringBuilder asPairs(StringBuilder bastel,
                                                           final long[] vals){
      if (bastel == null) {
         final int noOv = vals == null ? 0 : vals.length; 
         bastel = new StringBuilder(5 + 24 * noOv);
      }
      bastel.append("{ ");
      for (long val: vals) {
         if (val == -1L) break;
         asPair(bastel, val).append(' ');
      }
      return bastel.append('}');
   } // asPairs(StringBuilder, long[]);

//-----  Methods to search from left to right  ------------------


/** An &quot;indexOf&quot; using this object's final settings. <br />
 *  <br />
 *  This method searches this {@link CleverSSS}'s (sub) 
 *  {@link #toString() sequence} in the {@link CharSequence} {@code seq}
 *  supplied. The search is done once starting at position {@code sI} and
 *  going to the end of {@code seq} respectively {@code mxLen -1}.<br />
 *  <br />
 *  The search will be ignoring {@link #ignoreCase case} or ignoring
 *  {@link #ignoreWS white space} (or both or none) according to this
 *  {@link CleverSSS} object's final settings.<br />
 *  <br />
 *  This method is equivalent to<ul>
 *  <li>
 {@link #whereImpl(CharSequence, int, int)  (int)whereImpl(sequ, sI, mxLen)}
 *  </li>
 *  this being also the implementation based on the inheritors implementation
 *  of {@link #whereImpl(CharSequence, int, int)}.<br />
 *  <br />
 *  This method never throws any exception; if parameters don't allow the
 *  search -1 (not found) is returned.<br />
 *  <br />
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for this 
 *             object's (sub) sequence (&lt;0 is regarded as 0)
 *  @param mxLen if &gt; 0 and &lt; {@code seq}'s length it is taken as
 *              &quot;shortened&quot; length of {@code sequ} 
 *  @return  index where the subsequence was found or -1 if no match
 */
   public final int indexOf(final CharSequence sequ, final int sI,
                                                             final int mxLen){
      return (int)whereImpl(sequ, sI, mxLen);
   } // indexOf(CharSeq, 2*int)


/** An &quot;indexOf&quot; using this object's final settings. <br />
 *  <br />
 *  This method is equivalent to<ul>
 *  <li> {@link #indexOf(CharSequence, int, int) indexOf(seq, sI, 0)} and</li>
 *  <li>
 *  {@link #whereImpl(CharSequence, int, int)  (int)whereImpl(sequ, sI, 0)}
 *  </li>
 *  the latter being the implementation based on the inheritors implementation
 *  of {@link #whereImpl(CharSequence, int, int)}.<br />
 *  <br />
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @return  index where sub was found or -1 if no match
 */
   public final int indexOf(final CharSequence sequ, final int sI){
      return (int)whereImpl(sequ, sI, 0);
   } // indexOf(CharSeq, int)


/** An &quot;endIndexOf&quot; using this object's settings. <br />
 *  <br />
 *  This method is similar to 
 *  {@link #indexOf(CharSequence, int, int) indexOf(seq, sI, mxLen} except
 *  for returning the match's end index + 1  instead of the first index. This
 *  value can only be unequal to first index plus subsequence's
 *  {@link #length() length} in the case of  {@link #ignoreWS ignoring}
 *  white space.<br />
 *  <br />
 *  This method is equivalent to<ul>
 *  <li> {@link #whereImpl(CharSequence, int, int) 
 *                  (int)(whereImpl(sequ, sI, mxLen) >>> 32)}
 *  </li>
 *  this being also the implementation based on the inheritors implementation
 *  of {@link #whereImpl(CharSequence, int, int)}.<br />
 *  <br />
 *  This method never throws any exception; if parameters don't allow the
 *  search -1 (not found) is returned.<br />
 *  <br />
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @param mxLen if &gt; 0 and &lt; {@code seq}'s length it is taken as
 *              &quot;shortened&quot; length of {@code sequ} 
 *  @return  index + 1 = behind where sub was found or -1 if no match
 */
   public final int endIndexOf(final CharSequence sequ, int sI,
                                                            final int mxLen){
      return (int)(whereImpl(sequ, sI, mxLen) >>> 32);
   } // endIndexOf(CharSeq, 2*int)


/** An &quot;endIndexOf&quot; using this object's settings. <br />
 *  <br />
 *  This method is similar to 
 *  {@link #indexOf(CharSequence, int, int) indexOf(seq, sI} except for 
 *  returning the match's end index + 1  instead of the first index. This
 *  value can only be unequal to first index plus subsequence's
 *  {@link #length() length} in the case of  {@link #ignoreWS ignoring}
 *  white space.<br />
 *  <br />
 *  This method is equivalent to<ul>
 *  <li> {@link #endIndexOf(CharSequence, int, int) enIndexOf(seq, sI, 0)}
 *       and </li>
 *  <li> {@link #whereImpl(CharSequence, int, int) 
 *         (int)(whereImpl(sequ, sI, 0) >>> 32)}
 *  </li>
 *  the latter being the implementation based on the inheritors implementation
 *  of {@link #whereImpl(CharSequence, int, int)}.<br />
 *  <br />
 *  This method never throws any exception; if parameters don't allow the
 *  search -1 (not found) is returned.<br />
 *  <br />
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @return  index + 1 = behind the matching region, where this objects
 *           subsequence was found in {@code seq} or -1 if no match
 */
   public final int endIndexOf(final CharSequence sequ, int sI){
      return (int)(whereImpl(sequ, sI, 0) >>> 32);
   } // endIndexOf(CharSeq, int)


/** An &quot;indexOf&quot; (where) using this object's settings. <br />
 *  <br />
 *  This method returns the combined results of<ul>
 *  <li>{@link #indexOf(CharSequence, int, int)} and</li>
 *  <li>{@link #endIndexOf(CharSequence, int, int)})</li></ul>
 *  in one call respectively search.<br />
 *  <br />
 *  This method is equivalent to<pre><code>
 *  &nbsp;   final long ret = whereImpl(sequ, sI, mxLen);
 *  &nbsp;   return new int[] {(int) ret, (int)(ret &gt;&gt;&gt; 32)};</code></pre>
 *  *
 *  this being also the implementation based on the inheritors implementation
 *  of {@link #whereImpl(CharSequence, int, int)}.<br />
 *  <br />
 *  This method always returns a (new) int[] of length 2 and never null.<br />
 *  return[0] and return[1] are both -1 in case of no hit respectively 
 *  finding.<br />
 *  This method never throws any exception; if parameters don't allow the
 *  search, also an array containing two minus ones (-1) is returned.<br />
 *  <br />
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @param  mxLen if &gt; 0 and &lt; {@code seq}'s length it is taken as
 *          shortened length of {@code sequ} 
 *  @return  first index and last index + 1 where sub was found 
 *           or { -1, -1 } if no match
 */
   public final int[] where(final CharSequence sequ, final int sI, 
                                                             final int mxLen){
      final long ret = whereImpl(sequ, sI, mxLen);
      return new int[] {(int) ret, (int)(ret >>> 32)};
   } // where(CharSeqence, int, int)


   
/** An &quot;indexOf&quot; (where) using this object's settings. <br />
 *  <br />
 *  This method is equivalent to
 *  {@link #where(CharSequence, int, int) where(seq, sI, 0)}.<br />
 *  <br />
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @return  first index and last index + 1 where sub was found 
 *           or { -1, -1 } if no match
 */
   public final int[] where(final CharSequence sequ, final int sI){
      final long ret = whereImpl(sequ, sI, 0);
      return new int[] {(int) ret, (int)(ret >>> 32)};
   } // where(CharSeqence, int)
   
/** All (respectively up to limit) findings of this sub sequence. <br />
 *  <br /> 
 *  This method returns all (respectively max. length of {@code therFnd}) 
 *  findings of this substring in {@code seq}. The found spots are returned
 *  as modifications of the array {@code therFnd} supplied; see
 *  {@link #whereImpl(CharSequence, int, int) whereImpl()} for the 
 *  interpretation of the long value.<br />
 *  <br />
 *  Returned is the number of findings (between sI and mxLen). If the array  
 *  {@code therFnd} supplied is longer as the (returned) number of findings
 *  the next element ([# of findings]) will be set to -1L. This ensures a
 *  comfortable break out solution in (enhanced for) loops over that
 *  array.<br />
 *  <br />
 *  This method handles all trivial, exceptional, illegal and optimistic
 *  cases, delegating only the hard questions to
 *  {@link #implAlgWhere(long[], CharSequence, int, int, int, boolean)} that 
 *  might cleverly be overridden in extending classes, if applicable.<br />
 *  <br />
 *  @param therFnd where to put all findings; this array will be 
 *         modified up to [return value] and at least up to [0] (if that
 *         element exists)
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @param  mxLen if &gt; 0 and &lt; {@code seq}'s length it is taken as
 *          shortened length of {@code sequ} 
 *  @param  overlap if true the findings may overlap, meaning that 
 *          &quot;otto&quot; is twice found in &quot;ottotto&quot; ( 0 and 3)
 *          instead of once (from beginning to end)         
 *  @return the number of findings, 0 meaning none, 
 *         -1 meaning everywhere (empty substring) and -2 means
 *         nothing to do as there is therFnd is null or empty
 *  @see #implAlgWhere(long[], CharSequence, int, int, int, boolean)
 *  @see #whereImpl(CharSequence, int, int)       
 */
  public final int allWhere(final long[] therFnd, final CharSequence sequ,
                             int sI, final int mxLen, final boolean overlap){
     final int maxFnd = therFnd == null ? 0 : therFnd.length;
     if (maxFnd != 0) therFnd[0] = -1L;  // stopper for non found cases
     if (sequ == null) return 0;
     int j = sequ.length();
     if (j == 0) return 0;
     final int lk = (mxLen > 0 && mxLen < j) ? mxLen : j;
     if (lk == 0) return 0;
     if (len == 0) return -1;
     final int mxSi = lk - len;
     if (sI < 0) sI = 0;
     if (sI > mxSi) return 0;  // no space
     if (maxFnd == 0) return -2;
     int ret = 0;
     
     if (optimisticOK) {
        repeatedSearch: for (int fInd = 0; ; ) {
           final long aktFnd = therFnd[fInd] = whereImpl(sequ, sI, mxLen);
           if (aktFnd == -1L) break repeatedSearch;
           ++ret;
           if (++fInd == maxFnd) break repeatedSearch;
           sI = (int)(aktFnd >>> 32); // overlap can't happen for optimistic 
           if (sI > mxSi) break repeatedSearch;
        } // repeatedSearch
     } else ret = implAlgWhere(therFnd, sequ, lk, sI, mxSi, overlap);
     
     if (ret < maxFnd)  therFnd[ret] = -1L; // stopper for through array    
     return ret;
  } // allWhere(long[], CharSequence, 2*int, boolean)

/** The algorithmic implementation of allWhere(). <br />
 *  <br />
 *  Called by 
 *  {@link #allWhere(long[], CharSequence, int, int, boolean) allWhere()}
 *  after handling all trivial, exceptional, illegal and optimistic
 *  cases.<br />
 *  <br />
 *  This implementation calls 
 *  {@link #implAlgWhere(CharSequence, int, int, int)} in an adequately 
 *  organised loop. That's what can be done for most algorithms. But some
 *  (like {@link KMP Knuth Morris Pratt}) could do those multiple searches
 *  much better.<br />
 *  <br />
 */
   protected int implAlgWhere(final long[] therFnd, final CharSequence sequ,
                 final int lk, int sI, final int mxSi, final boolean overlap){
      final int maxFnd = therFnd.length;
      int ret = 0;
      repeatedSearch: for (int fInd = 0; ; ) {
         long aktFnd = therFnd[fInd] = implAlgWhere(sequ, lk, sI, mxSi); 
         sI = (int)aktFnd;
         if (sI == -1) break repeatedSearch;
         ++ret;
         if (++fInd == maxFnd) break repeatedSearch;
         if (overlap) {
            ++sI;
         } else sI = (int)(aktFnd >>> 32);
         if (sI > mxSi) break repeatedSearch;
      } // repeatedSearch
      return ret;
   } // implAlgWhere(final long[], CharSequence, 2*int, boolean)

/** An &quot;indexOf&quot; (where) using this object's settings. <br />
 *  <br />
 *  This method is the basic implementation of all left to right searches 
 *  of this {@link CleverSSS} objects subsequence within the sequence 
 *  {@code seq} supplied as parameter. This method handles all trivial
 *  and {@link #optimisticOK optimistic} cases by itself 
 *  {@link #implAlgWhere(CharSequence, int, int, int) delegating} only
 *  the hard requests to the inheritor's clever
 *  {@link #implAlgWhere(CharSequence, int, int, int) algorthm}.<br />
 *  <br />
 *  Inheriting classes, like {@link RK RK (Rabin Karp)} or 
 *  {@link  KMP KMP (Knuth &amp;alii)} do implement the famous inventor's
 *  clever substring search algorithm by implementing the abstract method
 *  {@link #implAlgWhere(CharSequence, int, int, int)}.<br />
 *  The inheritor {@link Simple} on the other hand provides the 
 *  ({@link String} standard) O(n*m) simple loop in loop implementation as 
 *  a reference.<br />
 *  The 
 *  <br />
 *  This method may very well be called directly instead of using 
 *  {@link #indexOf(CharSequence, int, int)}, 
 *  {@link #indexOf(CharSequence, int)}, 
 *  {@link #endIndexOf(CharSequence, int, int)},
 *  {@link #where(CharSequence, int, int)} or 
 *  {@link #where(CharSequence, int)}.<br />
 *  This method fulfils the contract of all these methods named.<br />
 *  <br />
 *  The rationale behind having this method 
 *  {@link #whereImpl(CharSequence, int, int)} instead of only a
 *  {@link #where(CharSequence, int, int) where(..)} is avoiding the 
 *  throwaway object (returning one long primitive value instead of an
 *  two element int array).<br />
 *  <br />
 *  @see #implAlgWhere(CharSequence, int, int, int)
 *  @see #optimisticOK
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 is regarded as 0)
 *  @param  mxLen if &gt; 0 and &lt; {@code seq}'s length it is taken as
 *          shortened length of {@code sequ} 
 *  @return  first index and last index + 1 where sub was found (combined 
 *           in one long, first index in the lower 32 bits) or -1 if no match
 */
   public final long whereImpl(CharSequence sequ, int sI, int mxLen){
      if (sequ == null) return -1L;
      int j = sequ.length();
      if (j == 0) return -1L;
      final int lk = (mxLen > 0 && mxLen < j) ? mxLen : j;
      if (lk == 0) return -1L;

      final int mxSi = lk - len;
      if (sI < 0) sI = 0;
      if (sI > mxSi) return -1L;  // no space

      if (optimisticOK)  {
         if (len == 0) return ((long)(sI + 1) << 32) | sI;
         if (len == 1) { // special case char search only 
            final char subLast = subC[0];
            for (; sI < lk; ++sI) {
               char seqAct = sequ.charAt(sI);
               if (seqAct == subLast 
                     || ignoreCase && lowerC(seqAct) == subLast) {
                  // no problem here and below as sI is >= 0
                  return ((long)(sI + 1) << 32) | sI;
               } // equal
            } // for
            return -1L; // -1, -1
         } // special case char search only 

         j = 0;
         int k = sI;

         searchOpt: for(; sI <= mxSi && k < lk; ) {
            char seqAct = sequ.charAt(k);
            /// char compC = subC[j];
            /// System.out.println(" // opt " + seqAct + " sI " + sI + 
            /// "<" + compC + "> j " + j + " k " + k);
            if (ignoreWS && seqAct <= ' ') { // skipWS
               ++k;
               if (j == 0) sI = k;
               continue searchOpt;
            } // skipWS
            char compC = subC[j];
            missMatch: if (seqAct != compC) {
               if (ignoreCase) {
                  seqAct =  lowerC(seqAct);
                  if (seqAct == compC) break missMatch;
               } // ignoreCase
               if (j == 0) {
                  ++k;
               } else {
                  j = 0;
               }
               sI = k;
               continue searchOpt;
            } // missMatch
            ++k;
            ++j;
            if (j == len) { // full match
               return ((long)k << 32) | sI; // [0] = sI, ret[1] = k
            } // full match
         } // searchOpt;
         return -1L;
      }
      return implAlgWhere(sequ, lk, sI, mxSi);
   } // whereImpl(CharSequence, 2*int)


/** The algorithmic implementation of whereImpl(). <br />
 *  <br />
 *  The method {@link #whereImpl(CharSequence, int, int)} does all parameter 
 *  checks and handles all exceptional / illegal cases by returning -1. It
 *  also handles the {@link #optimisticOK optimistic} cases.<br />
 *  <br />
 *  All other cases are the hard part, where one of the famous inventors
 *  (Rabin, Karp, Knuth et alii) clever algorithm is wanted. This method, to
 *  be implemented in the respective inheriting classes is called then.<br />
 *  <br />
 *  As is clear from the just said and by this method being not public 
 *  this method does neither have to handle the trivial optimistic cases 
 *  nor must it ever check the parameter values.<br />
 *  <br />
 *  @param sequ the sequence to search in
 *  @param lk   its correct or shortened) length  
 *  @param sI   the starting index for the search
 *  @param mxSi the maximum possible value of the match index
 *  @return  first index and last index + 1 where sub was found (combined 
 *           in one long, first index in the lower 32 bits) or -1 if no match
 */
   protected abstract long implAlgWhere(CharSequence sequ, 
                                                  int lk, int sI, int mxSi);

   
// --- end search left to right ---  Methods to search from right to left --

/* Internal remark: The search from right to left is quite unsupported by 
 * RK or KMP. To have it there would require the objects constructed "from 
 * the other side".
 * The question is: Do we really need these methods here?
 * Or would it be better to put all efforts into a "find all occurrences in
 * one step"-method à la long[] where(....) or int where(long[] ....)?
 */
   
/** A &quot;lastIndexOf&quot; (lastWhere) using this object's
 *                                                          settings. <br />
 *  <br />
 *  This method is similar to
 *  {@link TextHelper#lastIndexOf(CharSequence, CharSequence, int)} with the 
 *  difference of returning both the index of the first matching and of the
 *  first non matching character (behind) in {@code seq} in case of 
 *  a hit.<br />
 *  <br />
 *  In case of {@link #ignoreWS} false these both numbers just differ by the
 *  {@link #length() length} of the sub sequence searched for.<br />
 *  <br />
 *  In the case of a white space {@link #ignoreWS ignoring} search the 
 *  difference mentioned can be greater than {@link #length() length} 
 *  as well as shorter than the subsequence used to make this
 *  {@link CleverSSS} object.<br />
 *  <br />
 *  This method returns both numbers packed in a long in the same way as does
 *  {@link #whereImpl(CharSequence, int, int)}.<br />
 *  <br />
 *  This method never throws any exception; if parameters don't allow the
 *  search, two minus ones (-1) are returned as -1L.<br />
 *  <br />
 *  Hint: This implementation is not so clever in the sense of using an 
 *  O(n*m) loop in loop algorithm for the real searches. Nevertheless it is
 *  clever enough to handle the trivial and optimistic cases quite well.
 *  So it very well fits the
 *  &quot;{@link #makeSimple(CharSequence, boolean, boolean) simple}&quot;
 *  search objects, but may disappoint if used with an object of one of the
 *  (clever) inheritors like {@link RK} or {@link KMP}.<br />
 *  Remark: One might be tempted to request the inheritors overriding this 
 *  implementation. But their state (hashing, final automatons etc.) are
 *  designed for left to right searches and not the other way round.<br />
 *  <br />
 *  @param sequ the sequence in which sub is searched for
 *  @param sI  the index in {@code sequ} to start the search for sub 
 *            (&lt;0 or too large is regarded as from the end)
 *  @param  minSI a stopper down to where seq will be searched;
 *          &lt;= 0 is taken as 0; &gt; (adjusted) sI will return -1
 *  @return  first index and last index + 1 where sub was found (combined 
 *           in one long, first index in the lower 32 bits) or -1 if no match
 */
   public long lastWhereImpl(final CharSequence sequ, int sI, int minSI){
      if (sequ == null) return -1L;
      final int lk = sequ.length();
      if (lk == 0) return -1L;

      final int mxSi = lk - len;
      if (mxSi < 0) return -1L;  // no space
      if (sI < 0 || sI > mxSi) sI = mxSi; // regard outside as from end
      if (minSI < 0) minSI = 0;
      if (sI < minSI) return -1L;
       
      if (optimisticOK) { // optimistic O(n) 
         final char subOne = subC[0];
         if (len == 1) { // special case char search only 
            for (; sI >= minSI; --sI) {
               char seqAct = sequ.charAt(sI);
               if (seqAct == subOne 
                                 || ignoreCase && lowerC(seqAct) == subOne) {
                  // no problem here and below as sI is >= 0
                  return ((long)(sI + 1) << 32) | sI;
               } // equal
            } // for
            return -1L; // -1, -1
         } // special case char search only 
         
         char startC = subOne;
         
         searchReverseOpt: while (sI >= minSI) {
            char seqAct = sequ.charAt(sI);
            if (ignoreWS && seqAct <= ' ') { // skip first WS
               --sI;
               continue searchReverseOpt;
            } // skip first WS
            firstMissMatch: if (seqAct != startC) {
               if (ignoreCase) {
                  seqAct =  lowerC(seqAct);
                  if (seqAct == startC) break firstMissMatch;
               } // ignoreCase
               --sI;
               continue searchReverseOpt;
            } // firstMissMatch
            int k = sI;
            int j = 1;

            charWcomp: while (j < len) {
               if (++k >= lk) {
                  sI -= len;
                  continue searchReverseOpt;
               }
               seqAct = sequ.charAt(k);
               if (ignoreWS && seqAct <= ' ') continue charWcomp;
               char sC = subC[j];
               ++j;
               nextMissMatch: if(sC != seqAct) {
                  if (ignoreCase) {
                     seqAct =  lowerC(seqAct);
                     if (seqAct == sC) break nextMissMatch;
                  } // ignoreCase
               sI -= len; // single value first char can't be part, hence ..
               continue searchReverseOpt; // .. optimistic so far back 
               } // nextMissMatch
            } // charWcomp
            return ((long)(k + 1) << 32) | sI; // [0] = sI, ret[1] = k
         } // searchReverseOpt
         return -1L;
      }  // optimistic O(n) ---  not Rabin Karp (RK)
      
     // last index : stupid loop in loop (not Rabin K)      

      sequLoop: for ( ; sI >= minSI; --sI) {
         int k = sI;
         subLoop: for (int j = 0; j < len; ++j) {
            if (k == lk) continue sequLoop;
            char cK = sequ.charAt(k);
            ++k;
            if (ignoreWS) {
               while (cK <= ' ') {
                  if (k == lk) continue sequLoop;
                  cK = sequ.charAt(k);
                  ++k;
               }
            } // ignore WS
            char cS = subC[j];
            /// System.out.println(" // stu " + cK + " sI " + sI +  " k " + k + "<" + cS + "> j " + j );

            if (cS == cK) continue subLoop;
            if (!ignoreCase) continue sequLoop;
            cK = Character.toLowerCase(cK);
            if (cS == cK) continue subLoop;
         } // subLoop
         ///System.out.println(" // stu ret  sI " + sI +  " k " + k );
         return ((long)k << 32) | sI; // [0] = sI, [1] = j
      } // sequLoop 
      return -1L;
   } // lastWhereImpl(CharSeqence, 2*int)  

   
/** A &quot;lastIndexOf&quot; using this object's settings. <br />
 *  <br />
 *  This method is equivalent to
 *  {@link #lastWhereImpl(CharSequence, int, int) 
 *  (int)lastWhereImpl(sequ, sI, 0)}.<br />
 *  <br />
 */
   public final int lastIndexOf(final CharSequence sequ, final int sI){
       return (int)lastWhereImpl(sequ, sI, 0);
   } // lastIndexOf(CharSeq, int)


/** A &quot;lastIndexOf&quot; using this object's settings. <br />
 *  <br />
 *  This method is equivalent to
 *  {@link #lastWhereImpl(CharSequence, int, int) 
 *  (int)lastWhereImpl(sequ, sI, minSI)}.<br />
 *  <br />
 */
   public final int lastIndexOf(final CharSequence sequ, 
                                               final int sI, final int minSI){
       return (int)lastWhereImpl(sequ, sI, minSI);
   } // lastIndexOf(CharSeq, int)


} // abstract class CleverSSS (24.01.2010, 27.01.2010, 03.06.2015)