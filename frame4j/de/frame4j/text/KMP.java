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

import java.util.Arrays;

import de.frame4j.util.MinDoc;

import static de.frame4j.text.TextHelper.lowerC;;

/** <b>Subsequence search &nbsp;&mdash;&nbsp; the immutable state</b>. <br />
 *  <br /> 
 *  An object of this class represents a character sequence to be searched 
 *  for in (longer) character sequences in all variants of 
 *  &quot;indexOf&quot;. A {@link KMP} object holds<ul>
 *  <li>all immutable state </li>
 *  <li>plus some settings </li>
 *  <li>and methods </li>
 *  <li>implementing some search algorithms, in this class especially <ul>
 *     <li>Knuth Morris Pratt (KMP) &mdash; hence the class
 *          name {@link KMP KMP}</li>
 *     <li>and optimistic (O(n)) search guarded by 
 *         a feasibility check.</li></ul></li>
 *  </ul>
 *  The rationale behind Knuth Morris Pratt &quot;indexOf&quot; algorithms is
 *  that they very successfully strive for O(n). The standard
 *  (&quot;naive&quot; loop-in-loop) algorithms, also used in {@link String}
 *  and consorts, are of type O(n*m).<br />
 *  <br />
 *  Knuth Morris Pratt does primarily compare character by character. But in
 *  the case of mismatches it has prepared a state machine (automaton) that
 *  tells where to go on with the search, thus avoiding to do any comparison
 *  twice (or even m times). This automaton is prepared once when 
 *  {@link #make(CharSequence, boolean, boolean) making} this {@link KMP}
 *  object.<br />
 *  <br />
 *  The rationale behind making objects of this class immutable respectively
 *  restricting the state to the constant parts of a concrete KMP search is
 *  their multiple and threadsafe re-usability.<br />
 *  <br />
 *  Motivation for clever subsequence searches and further background
 *  information see please in the abstract {@link CleverSSS super class}.<br />
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2009, 2010 &nbsp; Albrecht Weinert  
 */
 // so far    V.184+ (24.01.2010 13:48) : modified from RK
 //           V.191+ (27.01.2010 16:26) : some common parts to CleverSSS
 //           moved to Assembla due to Oracle-Sun: SVN revision jump 
 //           V.o07+ (15.02.2010 10:20) : CleverSSS polymorphy extended

@MinDoc(
   copyright = "Copyright 2010  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "make for substrings to search for and use multiply",  
   purpose = "Knuth Morris Pratt substring search implementation"
) public class KMP extends CleverSSS {
 
/** The KPM automaton definition. <br /> */        
   protected final int[] kmpSM;
      

/** Constructor (for factory methods only, no checks). <br /> */
   protected KMP(final char[] subC, int[] kmpSM,
                           final boolean ignoreCase, final boolean ignoreWS,
                           final boolean optimisticOK) {
      super("KMP (Knuth etc.)", subC, ignoreCase, ignoreWS, optimisticOK);
      this.kmpSM = kmpSM;
   } // full constructor, no checks

/** Constructor (for factory methods only, no checks), 
 *                                       one character only case. <br />
 */
   protected KMP(final char subOne, final boolean ignoreCase, 
                                                    final boolean ignoreWS){
      super("KMP (Knuth etc.)", subOne, ignoreCase, ignoreWS);
      this.kmpSM = new int[]{-1, 0}; // always this way if length is 1
   } // length one constructor, no checks

      
/** Make a KMP for a subsequence to be searched for. <br />
 *  <br />       
 *  This method makes and returns a {@link KMP} object for the {@code sub}
 *  sequence supplied. That object is suitable for subsequent multiple and 
 *  multi-thread searches for that {@code sub} sequence. If (all) that 
 *  searches are to ignore case, this has to be (finally) set here.<br />
 *  <br />
 */
   public static CleverSSS make(final CharSequence sub,
                                                  final boolean ignoreCase){
      return make(sub, ignoreCase, false);
   } //  make optionally ignoring case

      
/** Make a KMP for a subsequence to be searched for (later). <br />
 *  <br />     
 *  This method is equivalent to {@link #make(CharSequence, boolean)} with the
 *  extra option to make the subsequence search totally white space ignoring
 *  if the the third parameter {@code ignoreWS} is supplied as true.<br />
 *  <br />
 *  In that case only  {@code sub}'s non blank characters would be transferred
 *  to the {@link KMP} object to be made.<br />
 *  <br />
 *  Example: A search for {@code &quot;al L&quot; } in 
 *  {@code &quot; H a l l o &quot; } both ignoring white space and case would
 *  yield 3 as the first matching index (and 8 as the first non matching index
 *  after the fitting sub sequence pattern).<br />
 *  <br />
 */
   public static CleverSSS make(final CharSequence sub, 
                            final boolean ignoreCase, final boolean ignoreWS){
      final int ls = sub == null ? 0 : sub.length();
      if (ls == 0) return make(ignoreCase, ignoreWS);
      if (ls == 1) return make(sub.charAt(0), ignoreCase, ignoreWS);
      char[] subSub = toCharA(sub, ignoreCase, ignoreWS);
      if (subSub == null) make(ignoreCase, ignoreWS);
      final int len = subSub.length;
      if (ignoreWS && len == 1) { // now length 1 due to ignore WS
         return new KMP(subSub[0], ignoreCase, ignoreWS);
      } // now length 1 due to ignore white spaces
      
      // all trivial cases done above
      // 2nd step prepare automaton  int[] kmpSM
      
      int[] kmpSM = new int[len + 1];
      kmpSM[0] = -1; // always
      
      int i = 0;
      int j = -1;
      boolean optimisticOK = true;
      while (i < len) {
         while (j >= 0 && subSub[i] != subSub[j]) { j = kmpSM[j]; }
         i++; j++;
         kmpSM[i] = j;
         optimisticOK = optimisticOK && j == 0;
      }

      return new KMP(subSub, kmpSM, ignoreCase, ignoreWS, optimisticOK);
   } // make(CharSequence, 2*boolean)

   
   @Override public String state(){ 
      StringBuffer bastel = commonState();
      bastel.append(" fails: {").append(Arrays.toString(kmpSM));
      bastel.append("}\n");
      return bastel.toString(); 
   } // state()


/** The basic implementation of the KMP search algorithm 
 *                                      using this object's settings. <br />
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
      int i = sI;
      int j = 0;
      int[] startInds = null;
      if (ignoreWS) startInds = new int[len];
      int stIi = 0;
      KMPsearch: while (i < lk) {
         char chAkt = sequ.charAt(i);
         if (ignoreWS) {
            if (chAkt <= ' ') {
               ++i;
               continue  KMPsearch; // skip WS
            }
            startInds[stIi] = i;
            if (++stIi == len) stIi = 0;
         }
         if (ignoreCase) chAkt = lowerC(chAkt);

         while (j >= 0 && chAkt != subC[j]) { j = kmpSM[j]; }
         /// if (j == 0) sI = i;
         ++i; 
         ++j;
         if (j == len) { // match
            if (ignoreWS) {
               sI =  startInds[stIi];
            } else sI = i - j;
            // startIndex sI  (( == (i - j) if no WS ignored ))
            // endIndex   i
            // System.out.println(" KMP match \"" + len + "\" at " + (i - j)
              //                      + " = " + sI + " .. " + i);

            return ((long)(i) << 32) | sI; // [0] = sI, [1] = j + 1

            /// if multiple searches
            // 0; // kmpSM[[j] for overlapping matches vs.  0 for not olm
            // j = kmpSM[j]; // 0;
            
            // sI = i -j;
         }  // match
      }
      return -1L;
    } // implAlgWhere(CharSeq, 3*int)
   
   
/** The algorithmic implementation of allWhere(). <br />
 *  <br />
 *  Called by 
 *  {@link #allWhere(long[], CharSequence, int, int, boolean) allWhere()}
 *  after handling all trivial, exceptional, illegal and optimistic
 *  cases.<br />
 *  <br />
 *  This implementation exploits the fact that the KPM algorithm is 
 *  predestined for consecutive multiple searches.<br />
 *  <br />
 */
   @Override protected int implAlgWhere(final long[] therFnd,
                            final CharSequence sequ, final int lk, int sI, 
                                      final int mxSi, final boolean overlap){
      final int maxFnd = therFnd.length;
      int ret = 0;
      
      int i = sI;
      int j = 0;
      int[] startInds = null;
      if (ignoreWS) startInds = new int[len];
      int stIi = 0;
      KMPsearch: while (i < lk) {
         char chAkt = sequ.charAt(i);
         if (ignoreWS) {
            if (chAkt <= ' ') {
               ++i;
               continue  KMPsearch; // skip WS
            }
            startInds[stIi] = i;
            if (++stIi == len) stIi = 0;
         }
         if (ignoreCase) chAkt = lowerC(chAkt);
         while (j >= 0 && chAkt != subC[j]) { j = kmpSM[j]; }

         ++i; 
         ++j;
         if (j == len) { // match
            if (ignoreWS) {
               sI =  startInds[stIi];
            } else sI = i - j;
            // startIndex sI  (( == (i - j) if no WS ignored ))
            // endIndex   i
            ///  System.out.println(" KMP match \"" + len + "\" at " + (i - j)
            ///                     + " = " + sI + " .. " + i);

            therFnd[ret] = ((long)(i) << 32) | sI; // [0] = sI, [1] = j + 1
            ++ret;
            if (ret == maxFnd) return ret;
            // go on for multiple searches with j = 0 or .... 
            // kmpSM[[j] for overlapping matches vs.  0 for not olm
            j = overlap ? kmpSM[j] : 0;
            sI = i -j;
         }  // match
      }
      return ret;
   } // implAlgWhere(final long[], ...)
      
} // class KMP (06.01.2010 , 24.01.2010)
