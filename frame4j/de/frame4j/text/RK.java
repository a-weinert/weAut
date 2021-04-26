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

import de.frame4j.util.MinDoc;;

/** <b>Subsequence search &nbsp;&mdash;&nbsp; the immutable state</b>. <br />
 *  <br /> 
 *  An object of this class represents a character sequence to be searched 
 *  for in (longer) character sequences in all variants of 
 *  &quot;indexOf&quot;. A {@link RK} object holds<ul>
 *  <li>all immutable state </li>
 *  <li>plus some settings </li>
 *  <li>and methods </li>
 *  <li>implementing some search algorithms, in this class especially <ul>
 *     <li>Rabin Karp &mdash; hence the class name {@link RK RK}</li>
 *     <li>and optimistic (O(n)) search guarded by 
 *         a feasibility check.</li></ul></li>
 *  </ul>
 *  The rationale behind Rabin Karp &quot;indexOf&quot; algorithms is their
 *  quite successful strive for O(n). The standard (&quot;naive&quot;
 *  loop-in-loop) algorithms, also used in {@link String} and consorts, are 
 *  of type O(n*m).<br />
 *  <br />
 *  Rabin Karp does not primarily compare character by character but two 
 *  hash values. The hash of the smaller character sequence searched for is 
 *  constant and stored finally in the {@link RK} object. The other hash is 
 *  &quot;sliding&quot; over the longer character sequence to search in. The 
 *  hash function used here is a polynomial with the character values acting
 *  as coefficients.<br />
 *  <br />
 *  The rationale behind making objects of this class immutable respectively
 *  restricting the state to the constant parts of a concrete RK search is
 *  their multiple and threadsafe re-usability.<br />
 *  <br />
 *  Motivation for clever subsequence searches and further background
 *  information see please in the abstract {@link CleverSSS super class}.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2009, 2010 &nbsp; Albrecht Weinert  
 */
 // so far    V.179+ (06.01.2010) : extracted from TextHelper
 //           V.184+ (24.01.2010) : prepare for KMP sibling
 //           V.191+ (27.01.2010) : some common parts to CleverSSS
 //           V.o07+ (15.02.2010) : CleverSSS polymorphy extended

@MinDoc(
   copyright = "Copyright 2010  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 39 $",
   lastModified   = "$Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "make for substrings to search for and use multiply",  
   purpose = "Rabin Karp substring search implementation"
) public class RK extends CleverSSS {

   
/** The sub sequence's last character. <br /> 
 *  <br />
 *  There is always at least one character, see {@link #length()}.<br />
 *  In case of {@link #ignoreCase} this character is lower case.<br />
 *  In case of {@link #ignoreWS} this character is guaranteed to be 
 *  &gt; blank.<br />
 *  <br />  
 */        
   public final char subLast;
      
/** The hash factor to remove the actual first character from
 *                                                     a sliding hash. <br />
 *  <br />
 *  Used in the Rabin Karp search algorithm. <br />                                                           
 */
   public final int iFac;
            
/** Constructor (for factory methods only, no checks). <br /> */
   protected RK(final char[] subC, final char subLast,
                           final int subHash, final int iFac,
                           final boolean ignoreCase, final boolean ignoreWS,
                           final boolean optimisticOK){
      super("RK (Rabin Karp)", subC, ignoreCase, ignoreWS, optimisticOK);
      this.iFac = iFac;
      this.subHash = subHash;
      this.subLast = subLast;
   } // full constructor, no checks

      
/** Make a RK for a subsequence to be searched for. <br />
 *  <br />       
 *  This method makes and returns a {@link RK} object for the  
 *  {@code sub} sequence supplied. That object is suitable for subsequent
 *  multiple and multi-thread {@link #indexOf(CharSequence, int) indexOf} type
 *  searches for that {@code sub} sequence. If (all) that searches are to
 *  ignore case, this has to has to be (finally) set here.<br />
 *  <br />
 *  For a null or empty  {@code sub} null (no {@link RK} object) is returned,
 *  as this case requires no search at all.<br />
 *  <br />
 */
   public static CleverSSS make(final CharSequence sub, 
                                         final boolean ignoreCase){
      return make(sub, ignoreCase, false);
   } //  make optionally ignoring case

      
/** Make a RK for a subsequence to be searched for (later). <br />
 *  <br />     
 *  This method is equivalent to {@link #make(CharSequence, boolean)} with the
 *  extra option to make the subsequence search totally white space ignoring
 *  if the the third parameter {@code ignoreWS} is supplied as true.<br />
 *  <br />
 *  In that case only  {@code sub}'s non blank characters would be transferred
 *  to the {@link RK} object to be made.<br />
 *  <br />
 *  Example: A search for {@code &quot;al L&quot; } in 
 *  {@code &quot; H a l l o &quot; } both ignoring white space and case would
 *  yield 3 as the first matching index (and 8 as the first non matching index
 *  after the fitting sub sequence pattern).<br />
 *  <br />
 */
   public static CleverSSS make(final CharSequence sub, 
                            final boolean ignoreCase, final boolean ignoreWS){
      int ls = sub == null ? 0 : sub.length();
      if (ls == 0) {
         return make(ignoreCase, ignoreWS);
      }  // trivial length 0 handled by super class

      // look for the first character
      int j = 0;
      int len = ls;
      char subAct = sub.charAt(0);
      if (len == 1) return make(subAct, ignoreCase, ignoreWS);

      if (ignoreWS) { // special treatment ignore white space --
         while (subAct <= ' ') {
            ++j;
            if (--len == 0) make(ignoreCase, ignoreWS); // now empty
            subAct = sub.charAt(j);
         } // look for the first character
         if (len > 1) { // look for the last character
            for (int k = ls -1; k > j; --k, --len) {
               char act = sub.charAt(k);
               if (act > ' ') break;
            } // for last
         } // look for the last character if necessary
      } // special treatment ignore white space --
 
      /// TEST back in local hist
      ///    if (ignoreCase) subAct = lowerC(subAct);
   
      if (len == 1) { // special case 1 character only
         return make(subAct, ignoreCase, ignoreWS);
      } // special case 1 character only
      
      if (ignoreCase) subAct = lowerC(subAct);
      // subAct is first at the moment (in LC)
      final char firtsC = subAct;
      boolean optimisticOK = true; 
      int i = 0;
      int iFac = 1;
      ls = len;
      char[] subSub = new char[ls];
      int subHash = 0;

      hLoop: for ( ; i < len;  ++j) {
         if (i != 0) {
            iFac = (iFac * primF) & mskMod; //  % largePrime;
            subHash *= primF;
            subAct =  sub.charAt(j);
            while (ignoreWS && subAct <= ' ') { // special treatment  WS
               --len;
               ++j; // must end at last character > ' '
               subAct =  sub.charAt(j);
            } // special treatment ignore WS
            if (ignoreCase) subAct =  lowerC(subAct);
            optimisticOK = optimisticOK && subAct != firtsC;
         } 
         subSub[i] = subAct;
         ++i;
         subHash = (subHash + subAct)  & mskMod; // % largePrime;
      } // for the hash and all

      if (ignoreWS) { // ignore white space may have shortened length
         // Hint: len == 1 can not happen here, is already treated above.
         if (len < ls) {
            char[] tmp = subSub;
            subSub = new char[len];
            System.arraycopy(tmp, 0, subSub, 0, len);
         }
      } // ignore white space may have shortened length

      return new RK(subSub, subAct, subHash, iFac,
                              ignoreCase, ignoreWS, optimisticOK);
   } // make(CharSequence, 2*boolean)

/** Extend a RK hash by one character (helper function). <br />
 *  <br />
 *  Hint: This (helper) method takes whatever value supplied for 
 *  {@code charIn}. Ignoring case and or white space has to be done by the 
 *  caller.<br />
 *  <br />  
 *  @param oldHash the old hash value; supply 0 for new start 
 *  @param charIn the character to put as new last character to the hash
 *  @return the updated hash value
 */
   public static int extHash(final int oldHash, final char charIn){
      return ((oldHash & mskMod) * primF + charIn)  & mskMod;
      // No need to make long and mask the multiply: If an integer 
      // multiplication overflows, then the result is the low-order bits 
      // of  the mathematical product as represented in some sufficiently 
      // large two's-complement format. [from Java language specification]
   } // extHash(int, char) 


/** Slide a hash for this RK hash by one character (helper function). <br />
 *  <br />
 *  Hint: This (helper) method takes whatever value supplied for 
 *  {@code charIn} and {@code charOut}. Ignoring {@link #ignoreCase case} and
 *  or {@link #ignoreWS white space} has to be done by the caller.<br />
 *  <br />  
 *  @param oldHash the old hash value; supply 0 for new start 
 *  @param charIn the character to put as new last character to the hash
 *  @param charOut the character to remove as old first character from the
 *                  the sliding hash; supply 0 for new start or use 
 *                  {@link #extHash(int, char) extHash(1, charIn)} to start
 *  @return the updated hash value
 */
   public final int slideHash(final int oldHash, final char charOut,
                                                         final char charIn){
      final long shc = oldHash - (charOut * iFac);
      return  (((int)shc & mskMod ) * primF + charIn)  & mskMod;
   } // slide(int, 2*char) 

/** The basic implementation of the RK search algorithm 
 *                                      using this  object's settings. <br />
 *  <br />
 *  This method is called by {@link #whereImpl(CharSequence, int, int)} for
 *  all non trivial cases and with checked / correct parameters.<br />
 *  <br />
 *  @see CleverSSS#implAlgWhere(CharSequence, int, int, int) CleverSSS.implAlgWhere()
 *  @return  first index and last index + 1 where sub was found in one long
 *           or -1 if no match
 */ 
   @Override protected final long implAlgWhere(final CharSequence sequ, 
                                       final int lk, int sI, final int mxSi){ 
      char seqAct = sequ.charAt(sI);
      if (ignoreWS) { // special treatment ignore WS
         while (seqAct <= ' ') {
            if (++sI > mxSi) return -1L;  // no space
            seqAct = sequ.charAt(sI);
          } // skip over leading  WS
      } // special treatment ignore white space
 
      final char[] seqSub = new char[len];
      int seqHash = 0;
      
      // the very first round
      boolean starEq = true;
      int j = sI;
      int i = 0;

      for (; i < len && j < lk; ++i, ++j) {
         char subAct =  subC[i];
         if (i != 0) {
            seqAct = sequ.charAt(j);
            while (ignoreWS && seqAct <= ' ') { // special treatment ignore WS
               if (++ j == lk) return -1L; // too short ignoring WS
               seqAct = sequ.charAt(j);
             } // special treatment ignore WS
         } // not the beginning   
         if (ignoreCase) seqAct = lowerC(seqAct);
         seqSub[i] = seqAct;
         seqHash = (seqHash * primF + seqAct)  & mskMod; // % largePrime;
         starEq = starEq && seqAct == subAct;
      } // for the first round
      
      if (i != len) return -1L;  // too short ignoring WS 
      if (starEq) {
         // [0] = sI; [1] = j;  j is last + 1 due to for ++j above
         return ((long)j << 32) | sI; // [0] = sI, [1] = k
      }
      
      slideHash: for (++sI; /* sI <= mxSi && */ j < lk; ++sI, ++j, ++i) {
         if (i == len) i = 0;
         seqAct = sequ.charAt(j);
         if (ignoreWS) {  // special treatment ignore WS
            // a) sI was advanced;  advance further if WS hit
            while (sequ.charAt(sI) <= ' ') {
               if (++sI > mxSi) return -1L;  // no space
            }
            // b) j (end) was advanced; advance further if WS hit
            while (seqAct <= ' ') { // special treatment of WS
               if (++ j == lk) return -1L; // too short ignoring WS 
               seqAct = sequ.charAt(j);
            } // special treatment of WS ignoring search
         } // special treatment of WS ignoring search

         if (ignoreCase) seqAct =  lowerC(seqAct);
         
         long shc = seqHash /*+  0x40000000*/ - (seqSub[i] * iFac);
         seqHash = (((int)shc & mskMod ) * primF + seqAct )  & mskMod;
         seqSub[i] = seqAct;

         if (seqHash != subHash) continue slideHash; // unequal hash: go on
         if (subLast != seqAct) continue slideHash;  // unequal last: go on

         for (int k = 0, m = i + 1; k < len; ++m) { // character wise compare
            if (m == len) m = 0;
            if (subC[k] != seqSub[m]) continue slideHash;
            ++k; // omit last char compare (was done above)
         } // character wise compare
         return ((long)(j +1) << 32) | sI; // [0] = sI, [1] = j + 1
      } // slideHsh
      return -1L;
   } // implAlgWhere(CharSeq, 3*int)
         
} // class RK (06.01.2010 , 24.01.2010)
