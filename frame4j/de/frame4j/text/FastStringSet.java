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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import de.frame4j.io.FileCriteria;
import de.frame4j.io.FileHelper;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;

/** <b>A fast set of Strings</b>. <br />
 *  <br />
 *  An object of this class is a set of character sequences ({@link String}s).
 *  Its intended use is for relatively small sets of texts that are once build
 *  and used quite often afterwards, like e.g. lists of roles, filenames or
 *  of types in the context of authenticated user accounts respectively 
 *  file criteria or visitors. null is not allowed as entry but the empty
 *  String is.<br />
 *  <br />
 *  According to the intended use cases<ul>
 *  <li>adding operations take the burden of concurrency (by doing the 
 *      modifications synchronised).</li>
 *  <li>Removing operations are not supported (except for {@link #clear()};
 *      see the warning hint {@link #clear() there}).</li>
 *  <li>All (&quot;const&quot;) operations doing only information retrieving
 *      are optimised for speed exploiting the partial immutability 
 *      &mdash; that's the fast part.</li>
 *  </ul>
 *  Disregarding {@link #clear()} a {@link FastStringSet} is immutable in the
 *  sense that texts once added will not be removed and internally keep in
 *  the order of insertion.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *    Copyright 2009 &nbsp; Albrecht Weinert<br />
 *  <br /> 

 *  @see FileCriteria
 */
 //           V.134+ (06.01.2016) : FileHelper

@MinDoc(
   copyright = "Copyright  2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "a limited set of unsorted texts for frequent use",  
   purpose = "collect a (limited) set of texts, then use it as quasi immutable"
) public final class FastStringSet implements  Set<String> {
   
   private volatile String[] myList;
   private volatile int length;
   private volatile int haCo;
   
/** Make an empty Set. <br />
 *  <br /> 
 *  @param size initial capacity, outside  4..5000 gets 50
 */
   public FastStringSet(int size){
      if (size < 4 || size > 5000) size = 50;
      myList = new String[size];
   } // constructor
   

/** Make a Set with initial content. <br />
 *  <br /> 
 *  @param startContent the initial size and content
 */
   public FastStringSet(CharSequence[] startContent){
      if (startContent == null) {
         myList = new String[50];
         return;
      }
      final int size = startContent.length;
      myList = new String[size];
      addAll(startContent);
   } // FastStringSet(CharSequence[])

/** Make a Set with initial content (copy constructor). <br />
 *  <br /> 
 *  @param startContent the initial size and content as {@link FastStringSet}
 */
   public FastStringSet(final FastStringSet startContent){
      final String[] oCo = startContent == null ? null : startContent.myList;
      if (oCo == null) {
         myList = new String[50];
         return;
      }
      this.myList = oCo.clone();
      int len = 0;
      int h = 0;
      for (String a : myList) {
         if (a == null) break;
         ++len;
         h += a.hashCode();
      }
      this.length = len;
      this.haCo = h;
   } // constructor

/** Add the text to the set if not yet there. <br />
 *  <br />
 *  @param e the text to add; null always returns false, as null is not 
 *           allowed as {@link FastStringSet}'s element 
 *  @return true if the set has changed (by adding e)
 */
   @Override public synchronized boolean add(String e){
      if (e == null) return false; // null is not allowed as content
      int size = myList.length;
      guckLoop: for(String drin : myList) {
         if (drin == null) break guckLoop;
         if (drin.equals(e)) return false;
      } // guckLoop
      if (length == size) {
         final int oldSize = size;
         size += 50;
         final String[] tmp =  new String[size];
         System.arraycopy(myList, 0, tmp, 0, oldSize);
         myList = tmp;
      }
      myList[length] = e;
      ++length;
      haCo += e.hashCode();
      return true;
   } // add(String)


/** Add all elements of the Collection supplied. <br />
 *  <br />
 *  This ensures that all non null entries of the collection {@code c} 
 *  supplied are members of this set. If {@code c} is null or empty nothing
 *  happens.<br />
 *  Otherwise the collection {@code c} is asked for its array representation
 *  and {@link #addAll(Object[])} is used.<br />
 *  <br />
 *  @param  c the collection of elements to add if appropriate
 *  @return true if this set changed
 */
   @Override public boolean addAll(final Collection<? extends String> c){
      if (c == null || c.isEmpty()) return false;
      final Object[] into = c.toArray();
      if (into == null || into.length == 0) return false;
      return addAll(into);
   } // addAll(Collection)
   
/** Add all CharSequence elements. <br />
 *  <br />
 *  This method adds all elements that are of type CharSequence of the 
 *  supplied array {@code into}.<br />
 *  <br />
 *  @param into elements to add
 *  @return true if this set changed
 */
   public synchronized boolean addAll(Object[] into){
      if (into == null) return false;
      if (into.length == 0) return false;
      boolean modif = false;
      int size = myList.length;
      putLoop: for(Object akt : into) {
         if (!(akt instanceof CharSequence)) continue putLoop;
         guckLoop: for(String drin : myList) {
            if (drin == null) break guckLoop;
            if (drin.contentEquals(( CharSequence) akt)) continue putLoop;
         } // guckLoop
         if (length == size) {
            final int oldSize = size;
            size += into.length;
            final String[] tmp =  new String[size];
            System.arraycopy( myList, 0, tmp, 0, oldSize);
            myList = tmp;
         }
         final String e = akt.toString(); // String.toString returns this
         myList[length] = e;
         ++length;
         haCo += e.hashCode();
         modif = true;
      } // putLoop
      return modif;
   } // addAll(Object[])
   

/** Add a attribute text in original or shortened version. <br />
 *  <br />
 *  If the parameter {@code s} is null nothing happens and false is 
 *  returned. Otherwise {@code s} and / or a shortened version of {@code s},
 *  if feasible, is added to the set if not yet there in each case.<br />
 *  <br />
 *  For {@code addShortened} = 1 only a shortened version of {@code s} being 
 *  the part between the first equals sign (=) and a comma (,) or the end of 
 *  {@code s}. If no equals sign (=) is in {@code s} s is added as is.<br />
 *  <br />
 *  For {@code addShortened} = 2 (both) the long original version and,
 *  if possible, the just described short version are both entered.<br />
 *  <br />
 *  For all other values of {@code addShortened} only the long original 
 *  version is entered.<br />
 *  <br />
 *  Use case: This method is meant do add LDAP entries, i.e. something
 *   like<br /> &nbsp; 
 *    &quot;DC=FB3-MEVA,DC=fh-bochum,DC=de"&quot;,<br />
 *  in this original (long) form or shortened to  &quot;FB3-MEVA &quot; or
 *  both.<br />
 *  <br /> 
 *  @param s the value (attribute text)  to enter
 *  @param addShortened 1: shortened version only; 2: both long and shortened;
 *                         else: only s as is (long)
 *  @return true if this set has changed 
 */      
   public boolean add(String s, final int addShortened){
      if (s == null) return false;
      String s2 = s;

      makeShort: if (addShortened == 2 || addShortened == 1) {
         int sLe = s.length();
         if (sLe < 4) break makeShort;
         int start = s.indexOf('=') + 1;
         if (start < 2 || start == sLe) break makeShort; // no .=....
         int end = s.indexOf(',', start);
         if (end < 1) end = sLe;   // no separate components after one =
         s2 = s.substring(start, end);
         if (addShortened == 1) s = s2; // only short
      } // makeShort: if 
      synchronized (this) {
         boolean modif = add(s);  
         if (s2 != s)  modif |= add(s2);
         return modif;
      } 
   } // add(String, int)


/** Add file type (criteria) entries. <br />
 *  <br />
 *  This method adds one or more file type entries to this
 *  {@link FastStringSet}. The entries entered will start with a dot (.) and
 *  be in lower case only or be  transformed to that format.<br />
 *  <br />
 *  Surrounding white spaces will be stripped from the parameter
 *  {@code types}. An empty sequence will be taken as null and cause no
 *  change (return false). <br />
 *  <br />
 *  Multiple types to be accepted may be given as a semicolon separated
 *  list. Case does not matter for the file types (a Window only concept
 *  foreign to Linux).<br />
 *  <br />
 *  Entries within the parameter {@code types} may freely have forms like 
 *  &quot;typ&quot;, &quot;*.typ&quot; or &quot;+.typ&quot;; plus 
 *  characters + will be transformed to asterisks *. (A leading *. will bear
 *  no information anyway and a single * will be ignored as type.)<br />
 *  <br />
 *  @see FileHelper#isOfType(java.io.File, CharSequence)
 *  @return true if this set has changed 
 */
   public synchronized boolean addTypes(final CharSequence types){
      String typen = TextHelper.trimUq(types, null);
      if (typen == null ) return false;
      typen = typen.replace('+', '*');
      final int len = typen.length();
      int colonPos = typen.indexOf(';');
      if (colonPos < 0)  colonPos = len;
      boolean modif = false;
      for (int startPos = 0; startPos < len; ) {
         String actName = typen.substring(startPos, colonPos).trim();
         startPos = colonPos + 1;
         colonPos = typen.indexOf(';', startPos);
         if (colonPos < 0) colonPos = len;
         int aLen = actName.length();
         if (aLen < 1) continue;
         if (actName.charAt(0) == '*') {
            if (aLen == 1) continue;
            actName = actName.substring(1);
            --aLen;
         }
         if (actName.charAt(0) == '.') {
            if (aLen == 1) continue;
         } else {
            actName = '.' + actName;
         }
         modif |= this.add(TextHelper.simpLowerC(actName));
      }
      return modif;
   } // addTypes(CharSequence) 


/** Set file type (criteria) entries. <br />
 *  <br />
 *  This method {@link #clear() clears} this set. Then it is like
 *  {@link #addTypes(CharSequence)}.<br />
 *  <br />
 *  @return true if this set has changed 
 */
   public synchronized boolean setTypes(final CharSequence types){
      boolean modif = false;
      if (length != 0) {
         clear();
         modif = true;
      }
      modif |= addTypes(types);
      return modif;
   } // setTypes(CharSequence)


/** Remove all elements from this FastStringSet. <br />
 *  <br />
 *  This is the only one removing operation.<br />
 *  The usage of this method is not recommended in multi-threading
 *  applications of this class. This method may cause concurrent calls of
 *  {@link FastStringSet#toArray()} to return arrays containing null elements
 *  and hence violate its contract.<br />
 */
   @Override public synchronized void clear(){
      while (length > 0) {
         myList[--length] = null;
      }
      haCo = 0;
   } // clear()
   
/** This FastStringSet's hash code. <br />
 *  <br />
 *  The hash code of any {@link Set} is defined to be the sum of the hash 
 *  codes of the elements in the set.<br />
 *  <br />
 *  @return this set's hash code
 *  @see #equals(Object)
 *  @see Set#equals(Object)
 *  @see Set#hashCode()
 */
   @Override public int hashCode() { return haCo; }

/** Compares this FastStringSet with the specified object for equality. <br />
 *  <br />
 *  Like for all {@link Set}'s this method returns true if the other object#
 *  {@code o} is a {@link FastStringSet} of the same {@link #size()} 
 *  containing the same texts.<br />
 *  <br />
 *  @param o object to be compared for equality with this FastStringSet
 *  @return true if equal
 *  @see #hashCode()
 *  @see #containsAll(CharSequence[])
 */
   @Override public boolean equals(final Object o){
      if (o == this) return true;
      if (!(o instanceof FastStringSet)) return false;
      final String[] oCont = ((FastStringSet)o).myList;  // snapshot other
      if (((FastStringSet)o).length != this.length) return false;
      if (((FastStringSet)o).haCo != this.haCo) return false;
      return this.containsAll(oCont);
   } // equals(Object)

/** The state / content as multi-line String. <br />
 *  <br />
 *  @see TextHelper#format(Appendable, CharSequence, CharSequence[], int)   
 */
   @Override public String toString(){
      final String[] myCont = myList;
      final int myLen = length;
      return TextHelper.format(null, 
           "<FastStringSet(" + myLen + " entries)", myCont, myLen).toString();
   } // toString()
   

/** The content as comma separated list. <br />
 *  <br />
 *  @see TextHelper#format(Appendable, CharSequence, CharSequence[], int)   
 */
   public Appendable csL(Appendable dest){
      final String[] myCont = myList;
      int myLen = length;
      if (dest == null) dest = new StringBuilder(16 + myLen * 16);
      if (myLen > myCont.length) myLen = myCont.length;
      if (myLen == 0) return dest;
      try {
       dest.append(myCont[0]);
       for (int i = 1; i < myLen; ++i) {
         dest.append(", ").append(myCont[i]);
       } // for  , text
      } catch (IOException ex) { TextHelper.lastFormatingExc = ex; } // ignore  
      return dest;
   } // csL(StringBuilder)


/** Is the specified sequence contained in this FastStringSet. <br />
 *  <br />
 *  This method returns true if this FastStringSet contains an element that is
 *  textually equal to the object ({@link CharSequence}) {@code o} 
 *  supplied.<br />
 *  null always returns false, as a FastStringSet will never contain 
 *  (String) null.<br />
 *  <br />
 *  @param o element the presence of it's text in this FastStringSet is tested
 *  @return true if the text is present
 */
   @Override public boolean contains(Object o){
      if (!(o instanceof CharSequence)) return false;
      guckLoop: for(String drin : myList) {
         if (drin == null) break guckLoop;
         if (drin.contentEquals((CharSequence) o)) return true;
      } // guckLoop
      return false;
   } // contains(Object)


/** Is the specified sequence contained in this FastStringSet. <br />
 *  <br />
 *  This method returns false if this FastStringSet is empty or if {@code s}
 *  is null.<br />
 *  <br />
 *  This method returns true if this FastStringSet contains an element
 *  that is textually equal to the the sequence  {@code s} supplied 
 *  optionally ignoring case and optionally regarding wildcard rules for
 *  this sets entries (see 
 *  {@link TextHelper#wildEqual(CharSequence, CharSequence, boolean)
 *   TextHelper.wildEqual(...)}).<br />
 *  <br />
 *  @param s sequence the presence of which in this FastStringSet is tested
 *  @return true if the text is present or if this set is empty
 */
   public boolean contains(final CharSequence s, 
                         final boolean ignoreCase, final boolean wildEqual){
      if (length == 0 || s == null) return false;
      final int sLen = s.length();
      guckLoop: for(String drin : myList) {
         if (drin == null) break guckLoop; // last entry stops
         final int dLen = drin.length();
         if (dLen != sLen) continue guckLoop;
         if (sLen == 0) return true;
         if (wildEqual) {
            if (drin.contentEquals(s)) return true;
            if (TextHelper.wildEqual(drin, s, ignoreCase)) return true;
            continue guckLoop;
         }
         for (int j = 0; j < sLen; ++j) {
            if (!TextHelper.simpCharEqu(s.charAt(j), drin.charAt(j),
                                         ignoreCase))  continue guckLoop;
         }
         return true;
      } // guckLoop
      return false;
   } // contains(harSequence, 2*boolean)



/** Are all sequences in {@code c} also contained in this 
 *                                                      FastStringSet. <br />
 *  <br />
 *  This method returns true if this FastStringSet contains all elements
 *  contained in the Collection {@code c} supplied.<br />
 *  <br />
 *  If  {@code c} is null or empty, true is returned as no collected objects
 *  are supplied, for which the condition can fail.<br />
 *  <br />
 *  If the collection  {@code c} contains any element that is not of type 
 *  CharSequence (including null) false is returned, as a FastStringSet will 
 *  never contain such elements.<br />
 *  <br />
 *  @param cS  the text the presence of which in this FastStringSet is tested
 *  @return true if the text is present
 */
    public boolean containsAll(final CharSequence[] cS){
      if (cS == null) return true;
      for (CharSequence o : cS) {
         if (o == null) return false;
         guckLoop: for(String drin : myList) {
            if (drin == null) return false; // end reached and not contained
            if (drin.contentEquals(o)) break guckLoop;
         } // guckLoop
      } // for over c
      return true;
   } // containsAll(CharSequence[])


/** Are all texts in {@code cS} also contained in this FastStringSet. <br />
 *  <br />
 *  This method returns true if this FastStringSet contains all texts
 *  contained in the array {@code cS} supplied.<br />
 *  <br />
 *  If  {@code cS} is null or empty, true is returned as no texts are 
 *  supplied, for which the condition can fail.<br />
 *  <br />
 *  If the array  {@code cS} contains null as element false is returned, as a 
 *  FastStringSet will never contain null elements.<br />
 *  <br />
 *  @param c  the collection the presence of it's contained texts in this 
 *            FastStringSet is tested
 *  @return true  if the text is present
 *  @throws ClassCastException if {@code o}'s type is not CharSequence 
 *          (or derived including, of course, String)
 */
    @Override public boolean containsAll(final Collection<?> c){
      if (c == null) return true;
      for (Object o : c) {
         if (!(o instanceof CharSequence)) return false;
         guckLoop: for(String drin : myList) {
            if (drin == null) return false; // end reached and not contained
            if (drin.contentEquals(( CharSequence) o)) break guckLoop;
         } // guckLoop
      } // for over c
      return true;
   } // containsAll(Collection)


/** Is this FastStringSet empty. <br /> */
   @Override  public boolean isEmpty(){ return length == 0; }

/** An Iterator over this FastStringSet's elements. <br />
 *  <br />
 *  The order in which the elements are returned is the inserting 
 *  order.<br /> 
 *  The Iterator returned is not for concurrent use / use in more than one
 *  thread at the same time. But between making (this method) and each
 *  hasNext() / next() pair the using thread may change.<br />
 *  <br />
 *  @return an <tt>Iterator</tt> over the elements in this collection
 */
   @Override public Iterator<String> iterator(){ //=========================
      return new Iterator<String>() {
         volatile String leNext; // the next
         volatile int ind;       // index of leNext in myList
         
         { // pseudo constructor
            if (length != 0) try {
               leNext = myList[0]; 
            } catch (Exception dontCare) {}
         } // pseudo constructor

/** Has this Iterator (at least) one more element of its FastStringSet. <br />
 *  <br />         
 */
         @Override public boolean hasNext() { return leNext != null; }

/** This FastStringSet iterator's next String. <br />
 *  <br />
 *  @return the next String element
 *  @exception NoSuchElementException if no more elements available or the set
 *             was cleared
 */
         @Override  public String next(){
            String ret = leNext;
            if (ret == null ) throw 
                 new NoSuchElementException ("FastStringSet.iterator empty");
            if (++ind < length) { leNext = myList[ind]; }
            return ret;
         } // next()

/** Remove the iterator's actual element from the FastStringSet. <br />
 *  <br />
 *  Removes are not supported by / allowed with {@link FastStringSet}.<br />
 *  <br />
 *  @throws UnsupportedOperationException always
 */
         @Override  public void remove(){
            throw new  
                 UnsupportedOperationException("FastStringSet has no remove");
         } // remove()
         
      }; // Iterator  ======================================================
   } // iterator() 

   
/** Remove the object supplied from this FastStringSet. <br />
 *  <br />
 *  Removes are not supported by / allowed with {@link FastStringSet}.<br />
 *  <br />
 *  @param  o the Object / text to be removed; if  no CharSequence false is
 *            returned as o could not be contained, and hence no 
 *            remove ordered
 *  @throws UnsupportedOperationException always (almost)
 */
   @Override public boolean remove(Object o){
      if (!(o instanceof CharSequence)) return false;
      throw new  UnsupportedOperationException("FastStringSet has no remove");
   } // remove(Object)

/** Remove all the Collection's objects from this FastStringSet. <br />
 *  <br />
 *  Removes are not supported by / allowed with {@link FastStringSet}.<br />
 *  <br />
 *  @param  c the Collection of the texts to be removed; if  null false is
 *            returned as no  remove is ordered then
 *  @throws UnsupportedOperationException always (almost)
 */
   @Override public boolean removeAll(Collection<?> c){
      if (c == null) return false;
      throw new  UnsupportedOperationException("FastStringSet has no remove");
   } // removeAll(Collection)

/** Remove all but the Collection's objects from this FastStringSet. <br />
 *  <br />
 *  Removes are not supported by / allowed with {@link FastStringSet}.<br />
 *  @throws UnsupportedOperationException always
 */
   @Override public boolean retainAll(Collection<?> c){
      throw new 
           UnsupportedOperationException("FastStringSet has no remove");
   } // retainAll(Collection)

/** The number of texts / Strings contained in this FastStringSet. <br /> */   
   @Override public int size() { return length; }


/** The texts contained in this FastStringSet as array. <br />
 *  <br />
 *  This method returns a new String array of fitting length containing
 *  all ({@link #size()}) elements.<br />
 *  <br />
 *  @see #toArray(Object[])
 *  @see #list()
 */     
   @Override public Object[] toArray(){
      final String[] tmp = myList;
      int size = length;
      if (size > tmp.length) size = tmp.length;
      if (size == 0) return ComVar.NO_STRINGS;
      String[] ret = new String[size];
      System.arraycopy(tmp, 0, ret, 0, size);
      return ret;
    } // toArray()

/** The texts contained in this FastStringSet as internal array. <br />
 *  <br />
 *  This method returns the internal String array for sake of speed. The array
 *  returned may be read, but it must never be modified. It will contain
 *  {@link #size()} non null elements; if it is longer, the elements following
 *  will be null. A null element may be taken as end marker.<br />
 *  <br />
 *  @see #toArray(Object[])
 *  @see #list()
 */     
    public final String[] asArray(){ return myList; }
   

/** The texts contained in this FastStringSet as ArrayList. <br />
 *  <br />
 *  This method returns a new ArrayList&lt;String&gt; of fitting length 
 *  containing all ({@link #size()}) elements.<br />
 *  <br />
 *  @see #toArray(Object[])
 *  @see #toArray()
 */     
   public ArrayList<String> list(){
      final String[] tmp = myList;
      int size = length;
      if (size > tmp.length) size = tmp.length;
      ArrayList<String> ret = new ArrayList<>(size);
      for (String akt : tmp) {
         if (akt == null) break;
         ret.add(akt);
      }
      return ret;
    } // list()


/** The texts contained in this FastStringSet as array. <br />
 *  <br />
 *  This method stores this FastStringSet's Strings in the supplied array 
 *  {@code a} if that is big enough. If it is longer than {@link #size()}
 *  a[{@link #size()}] will be set null as an end marker. {@code a}'s elements
 *  beyond, if any, are left unchanged.<br />
 *  If the supplied array {@code a} is shorter than {@link #size()} a new 
 *  array of the same (run time) type and fitting length will be made and 
 *  returned instead.<br />
 *  <br />
 *  @param a the array into which this FastStringSet's Strings are to be
 *           stored, if it is big enough; otherwise, a new array of the same
 *           runtime type is made (and returned) for this purpose.
 *  @return an array containing all this FastStringSet's texts, preferably o
 *          {@code a} itself
 *  @throws ArrayStoreException if{@code a}'s type is not String or of
 *          a String's supertype
 *  @throws NullPointerException if {@code a} is null
 *  @see #toArray()
 *  @see #list()
 *  @see Arrays#copyOf(Object[], int, Class)
 */   
   @SuppressWarnings("unchecked")
   @Override  public <T> T[] toArray(T[] a){
      final String[] tmp = myList;
      int size = length;
      if (size > tmp.length) size = tmp.length;
      if (a.length < size) { // provided array too small
         return (T[]) Arrays.copyOf(tmp, size, a.getClass());
      } // provided array too small
      System.arraycopy(tmp, 0, a, 0, size);
      if (a.length > size) a[size] = null;
      return a;
   } // Set<String>,

} // FastStringSet (07.06.2009)
