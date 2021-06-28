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
package de.frame4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import de.frame4j.text.TextHelper;


/** <b>A set of properties</b>. <br />
 *  <br />
 *  A {@code PropMap} object features a set of String-String key value pairs.
 *  So it is a {@link java.util.Map}. The class is quite similar to 
 *  {@link java.util.Properties java.util.Properties} respectively to
 *  {@link java.util.Dictionary java.util.Dictionary}. Under circumstances, 
 *  often given and defined below, {@link PropMap} is the better choice
 *  than any of the standard {@link java.util.Map}s.<br />
 *  <br />
 *  The interface's {@link java.util.Map} freedoms are limited a bit (beyond
 *  &lt;CharSequence, String&gt;):<ul>
 *  <li> Keys and values are character sequences (hence internally
 *       {@link java.lang.String}s).</li>
 *  <li> Keys are not empty and do not have surrounding white space.<br />
 *       (Especially they never begin or end with a blank.)</li></ul>
 * 
 *  As already contracted by {@link java.util.Map}, keys are unique. A
 *  {@link PropMap} object is by this condition a set ({@link java.util.Set});
 *  see also {@link #entrySet()} respectively {@link #keySet()}).<br />
 *  <br />
 *  According to the &quot;non empty non white space surrounded&quot; 
 *  condition, many methods accept {@link java.lang.CharSequence}s as 
 *  parameter for the key, which they strip from surrounding white spaces, 
 *  should those be there. See
 *  {@link de.frame4j.text.TextHelper#trimUq(CharSequence, String)
 *    TextHelper.trimUq(..)}. If the key is then still not empty, it is 
 *  accepted and used as such.<br />
 *  <br />
 *  <br />
 *  <b> &nbsp; <a name="cob"> Class and object relations</a></b><br />
 *  <br />
 *  This class &nbsp;{@code PropMap}&nbsp; is the parent of {@link AppLangMap}
 *  and {@link Prop de.frame4j.util.Prop} in the same package. The pedigree
 *  {@link de.frame4j.util.Prop} -&gt;&nbsp;{@link PropMap} 
 *  -&gt;&nbsp;{@link AbstractMap} separates 
 *  {@link java.util.Collection Collection}'s aspects clearly from many extra
 *  services for applications, Servlets  and else, especially for
 *  {@link App de.frame4j.util.App}'s inheritors.<br />
 *  <br />
 *  Figure 1 shows those relations between the mentioned types, namely
 *  {@link PropMap}, its inner classes
 *  {@link PropMapHelper.Entry}, {@link PropMap.EntrySet} and
 *  {@link PropMap.KeySet}, as well as {@link de.frame4j.util.Prop} and
 *  {@link AppLangMap} and the Java-Collection-Framework's classes and
 *  interfaces.<br />
 *  <a href="./doc-files/propmap.png" target="_top"><img
 *  src="./doc-files/propmap.png" style="border:0;" width="636" height="358"
 *  alt="Click to see only the picture" 
 *  title="Click to see only the picture" 
 *   style="border:0; margin: 8px 8px; textalign:right;" /></a><br style="clear:both;" />
 *  Figure 1: PropMap and the Collection-Framework  &mdash; relations.<br />
 *    &nbsp; &nbsp; &nbsp;
 *   (Click the picture for full size and sharpness.)<br />
 *  <br />
 *  <br />
 *  <b> &nbsp; <a name="anw">de.frame4j.util.PropMap's &#160;and .Prop's
 *            application background</a></b><br />
 *  <br />
 *  Primary intended usage of {@link PropMap} (and of its inheritor
 *  {@link de.frame4j.util.Prop}) is the settings of modes and options as
 *  well as the handling and evaluation of start parameters for applications
 *  of all kinds as well as for other single objects (Beans).<br />
 *  <br />
 *  One logical consequence was the limitation to &lt;non empty String, 
 *  String&gt; &lt;key, value&gt; pairs, with the additional conditions of<ul>
 *  <li>keys not being surrounded by whitespace and</li>
 *  <li>allowing empty and null as value.</li>
 *  </ul>
 *  All (nearly all) those settings of applications, Servlets and so on are 
 *  given as character sequences (in text form) in the beginning, be it as 
 *  command line parameters, contents of .properties, .ini or .xml files or 
 *  as (parameter) attributes in a .html-tag.<br />
 *  <br />
 *  As second consequence of the main intended usage is a wide support for
 *  &quot;prefixed&quot; and &quot;indexed&quot; properties, by optional 
 *  special handling for exemplary keys like 
 *  {@code uhu9 uhu09 uhu[9] de.uhu de_DE.uhu en_GB.uhu[123]}.<br />
 *  <br />
 *  <br />
 *  <b> &nbsp; <a name="opt">Design aspects, optimisation goals
 *       and limits</a></b><br />
 *  <br />
 *  The main intended usage for mode, controlling, options handling and 
 *  evaluation of parameters (see 
 *  {@link Prop#parse(String[], CharSequence)}) means for such use cases, that
 *  a &quot;{@link java.util.Map}&quot; of (application) properties
 *  <br />
 *  &nbsp;a) more often is of small or medium size of from about 30 
 *           &quot;{@link PropMapHelper.Entry Entry}s&quot; up to about 200,
 *           more being exceptional<br />
 *    &nbsp; &nbsp; &nbsp;  and that<br />
 *  &nbsp;b) a &quot;two phase&quot; pattern of usage (or life cycle) 
 *    is the rule:<br />
 *  &nbsp; &nbsp; &nbsp; Phase 1: Constructing, building, adding of 
 *      entries<br />
 *      &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; and afterwards<br />
 *  &nbsp; &nbsp; &nbsp; Phase 2: utilisation with now fixed endowment of
 *     keys.<br />
 *  And usually phase 1 is done in exact one thread (initialising the 
 *  application, Servlet end so on).<br />
 *  <br />
 *  To put it short, the designated normal use case pattern is:<br />
 *  Make and modify) the PropMap in one thread completely, and then use 
 *  (read, copy, clone) it unmodified in one or more other threads.<br />
 *  <br /> 
 *  Applications sticking mostly to this frequent pattern of usage are served
 *  by this class and its inheritors quite well and with less consumption of 
 *  resources than the standard alternatives. So {@link PropMap} and its 
 *  extensions may be a good choice even when the extra features and services
 *  are not needed.<br />
 *  <br />
 *  The generation of subsets is often needed in above mentioned phase 2 and
 *  is normally quite expensive. Here is a cost optimised realisation (by
 *  {@link #subSet(CharSequence)}).<br />
 *  <br />
 *  <br />
 *  Hint 1: Allowing null as value, goes beyond what 
 *  {@link java.util.Properties} is willing to forbear. Here it is used to
 *  distinguish an empty from a not yet set value. If that difference is not 
 *  needed or disturbing it can be masked away by using<br /> &nbsp; &nbsp;
 *  {@link #getString(CharSequence, String) getString(key, &quot;&quot;)}
 *  &nbsp; bzw. &nbsp;  
 *  {@link #getString(CharSequence, String) getString(key,} 
 *  {@link de.frame4j.util.ComVar#EMPTY_STRING ComVar.EMPTY_STRING)},<br />
 *  which will substitute the empty String for null.<br />
 *  <br />
 *  Hint 2: The methods {@link #equals(Object)} and {@link #hashCode()} of 
 *  standard containers (including the parent class {@link AbstractMap}) are
 *  (have to be) quite expensive. They are optimised here finally.<br />
 *  <br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2004 - 2006, 2008, 2009  &nbsp; Albrecht Weinert.<br />
 *  <br /> 
 *  @see de.frame4j
 *  @see de.frame4j.util.App
 *  @see de.frame4j.util
 *  @see de.frame4j.text.TextHelper
 *  @author   Albrecht Weinert
 *  @version  $Revision: 56 $ ($Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $
 */
 // so far    V02.00 (22.10.2004) :  new, alpha
 //           V02.07 (29.10.2004) :  final, Entry modif. intern make
 //           V02.10 (14.01.2005) :  getLanguage, valueLanguage
 //           V02.14 (16.05.2005) :  load1Impl
 //           V02.16 (22.09.2006) :  Servlet
 //           V.o10+ (01.01.2009) :  Generics for Frame4J (experimental)
 //           V.107+ (15.04.2009) :  further translations / clarifications
 //           V.o001 (23.01.2010) :  last ex Kenai move to Assembla
 //           V.o10+ (10.02.2010) :  no null values in Properties
 //                                      and asProperties with filter
 //           V.118+ (23.02.2015) : Servlet support removed 
 //           V.156+ (09.05.2016) : Web Start compatibility 


@MinDoc(
   copyright = "Copyright 2004 - 2008, 2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 56 $",
   lastModified   = "$Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "3 phase: make, populate, use (preferably not further modified)",  
   purpose = "initial properties for applications and other objects"
) public class PropMap extends AbstractMap<CharSequence, String>
     implements ConcurrentMap<CharSequence, String>, Cloneable, Serializable {

/** Version number for serialising.  */
   static final long serialVersionUID = 260153008500202L;
//                                      magic /Id./maMi
   
   final ReentrantReadWriteLock rwLock =  new ReentrantReadWriteLock();
   
/** The lock for reads. <br />
 *  <br />
 *  This is only to guard multi thread reads against critical writes 
 *  guarded by {@link #wLock}.<br />
 *  {@link ReadLock#lock()  lock()} and 
 *  {@link ReadLock#unlock() unlock()} must be used
 *  as guaranteed pair.<br />
 */
  protected final ReentrantReadWriteLock.ReadLock rLock = rwLock.readLock();
  
/** The lock for writes. <br />
 *  <br />
 *  This is to guard those writes / modifications that must not interfere with
 *  multi thread critical reads (to be guarded by {@link #rLock}).<br />
 *  {@link WriteLock#lock() lock()} and 
 *  {@link WriteLock#unlock() unlock()} must be used
 *  as guaranteed pair.<br />
 */
  protected final ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
   
/** The entries. <br />  */
   protected volatile PropMapHelper.Entry[] entries;
   
/** Number of entries. <br /> */
   volatile int size;
   
/** Number of entries. <br /> */
   @Override public final int size(){ return size; }

/** Equality with other object. <br />
 *  <br />
 *  This method returns true, if the other object {@code o} is of type 
 *  {@link PropMap}, has the same {@link #size() size} and for all 
 *  {@code o}'s {@link PropMapHelper.Entry entries} one own can be found that
 *  is {@link PropMapHelper.Entry#equals(Object) equal}.<br />
 *  <br /> 
 *  Hint: This comparison uses no synchronisation. If one of the 
 *  {@link PropMap PropMap}s change in the proceeding this method's result 
 *  might reflect the new or the previous state.<br />  
 */
   @Override public final boolean equals(final Object o){
      if (!(o instanceof PropMap)) return false;
      if (o == this) return true;
      final PropMap oPm = (PropMap) o;
      if (size != oPm.size) return false;
      if (size == 0) return true; // both empty
      compareAll: for (PropMapHelper.Entry oEnt : oPm.entries) {
         if (oEnt == null) continue compareAll;
         int ind = indexOfKey(oEnt.key);
         if (ind < 0) return false; // not in ? ==> the sets are not equal
         final String oValue = oEnt.value;
         final String myValue = entries[ind].value;
         if (oValue == myValue) continue compareAll; // optimise and null case
         if (oValue == null) return false;
         if (!oValue.equals(myValue)) return false;
      } // for over others
      return true;
   } // equals(Object)

/** The whole maps hash code. <br />
 *  <br />
*/
   @Override public final int hashCode(){
      int ret = 0;
      for (PropMapHelper.Entry myEnt : entries) {
         if (myEnt == null) continue;
         ret += myEnt.entHash;
      } // for over entries
      return ret;
   } // hashCode()
   

/** Maximum of &quot;hash miss side steps&quot;. <br />
 *  <br />
 *  This number is the maximum of additional search steps for an entry. If
 *  non existing entry is looked for exactly {@link #hashMissMax} + 1 search 
 *  steps are necessary for this discovery.<br />
 *  <br />
 *  In a empty or weakly populated {@link PropMap} this value will normally 
 *  be 0. Experiments show (see {@link #hashQuality(StringBuilder)}) that 
 *  values &gt; 5 are quite improbable.<br />
 *  <br /> 
 *  Implementation hint: As this {@link Map} is not intended for use cases
 *  with a highly dynamic content, the value will not be decremented should
 *  this be possible after a {@link #remove(Object) remove()}. So after 
 *  a lot of removes an explicit
 {@link PropMap#rehash(de.frame4j.util.PropMapHelper.Entry, int) rehash(null, 0)}
 *  might be appropriate.<br />
 *  <br />
 *  @see #hashQuality(StringBuilder)
 *  @see #hashMissSum
 */   
   protected volatile int hashMissMax;

/** Summed up &quot;hash miss side steps&quot;. <br />
 *  <br />
 *  hashMissSum / size is a measurement number for the actual quality of the
 *  internal hashing. It's the average of search steps to find an existing 
 *  entry.<br />
 *  <br />
 *  Hint: This variable is pure measurement; its value has no influence 
 *  on the working of any method.<br />
 *  <br />
 *  @see #hashMissMax
 *  @see #hashQuality(StringBuilder)
 *  @see #reHashes
 */   
   protected int hashMissSum;

/** Summed up re-hashes (since new or empty). <br />
 *  <br />
 *  This counts the occurrence of necessary or explicit {@link #rehash()}s.
 *  This is pure measurement.<br />
 *  @see #hashQuality(StringBuilder)
 *  @see #hashMissMax
 */   
   protected int reHashes;

//----------------------------------------------------------
   
/** A copy of this PropMap. <br />
 *  <br />
 *  A shallow copy of this {@link PropMap} is made and returned. Original and 
 *  copy share the {@link PropMapHelper.Entry}s existing or contained
 *  at the time of the call. Changing the value of a (mutable) entry effects
 *  both; adding and removing effects only one. The first call to 
 *  {@link #deepIt()} clones all shared mutable {@link PropMapHelper.Entry}s,
 *  hence making the copy independent.<br />
 *  <br />
 *  The operation is (internally) synchronised and (compared to 
 *  {@link java.util.Hashtable}) low-cost.<br />
 *  <br /> 
 *  @see #deepIt()
 */ 
   @Override public Object clone(){
      PropMap result;
      try { rLock.lock();  // read synchronise / lock 
         try {
            result = (PropMap) super.clone();
         } catch (CloneNotSupportedException e) {
            throw new InternalError(" should not happen for cloneable " + e);
         }
         result.fileList  = new String[FILE_LIST_LEN];
         if (fileAnz != 0) {
            System.arraycopy(this.fileList, 0, result.fileList, 0, fileAnz);
         }
         result.entries = this.entries.clone(); // clones array not the entries 
      } finally { rLock.unlock(); } // read synchronise / lock 
      result.internalEntrySet = result.new EntrySet();
      result.internalKeySet   = result.new KeySet<CharSequence>();
      result.isShallow = result.size != 0;
      return result;
   } // clone()
   
   private volatile boolean isShallow;

/** &quot;Deepening&quot; a preceding clone(). <br />
 *  <br />
 *  This method, when called the first time for a {@link #clone() cloned}
 *  {@code PropMap} replaces every mutable {@link PropMapHelper.Entry} by its
 *  copy (using {@link PropMapHelper.Entry#clone() entry.clone()}). This 
 *  makes the shallow {@link #clone()} deep.<br />
 *  If called a second time, this method does nothing.<br />
 *  @see #clone()
 */   
   public final void deepIt(){
      if (isShallow)  { 
         try { wLock.lock();  // write synchronise / lock 
            if (!isShallow) return; // another thread did the work already
            reHashes = 0;  // consider a deep clone as a new Map
            suList: for (int suInd = 0; suInd < entries.length; ++suInd) {
               PropMapHelper.Entry e = entries[suInd];
               if (e == null || e.immutable) continue suList;
               entries[suInd] = (PropMapHelper.Entry) e.clone();
            } // suList: for
            isShallow = false;
         } finally { wLock.unlock(); } // write synchronise / lock 
      } // if shallow
   } // deepIt() 
   
//--------------------------------------------------------------------------
  
   
/** Is it empty.<br />
 *  <br />
 *  @see java.util.Map#isEmpty()
 */
   @Override public final boolean isEmpty(){ return size == 0; }

/** Clear / forget all. <br />
 *  <br />
 *  Makes this an empty {@link PropMap} (with a starting capacity of 71
 *  entries).
 *  <br />
 *  @see #clear(int)
 */
   @Override public final void clear(){ clear(71); }

/** Clear / forget all and choose starting capacity. <br />
 *  <br />
 *  Makes this an empty {@link PropMap} with a specified minimal 
 *  (stating) capacity.<br />
 *  <br />
 *  Implementation hint: inheriting classes wanting to prohibit such total 
 *  clearing have to override only this method, where all direct or 
 *  indirect total clearances go through.<br />
 *  <br />
 */
   public synchronized void clear(final int startCap){
      try { wLock.lock();  // write synchronise / lock 
         reHashes = hashMissMax = hashMissSum = size = 0;
         Arrays.fill(entries, null); // forget all entries
         int cap  = PropMapHelper.gtPrim(startCap);
         if (cap == -1) cap = 71;
         final int oldTooLargeBy = entries.length - cap;
         if (oldTooLargeBy < 0 || oldTooLargeBy > cap) // new only if too
            entries = new PropMapHelper.Entry[cap];   // small or double size 
      } finally { wLock.unlock(); } // write synchronise / lock 
   } // clear(int)


/** Make an empty set of properties.<br />
 *  <br />
 *  Is like {@link PropMap#PropMap(int) PropMap(71)}.<br />
 */   
   public PropMap(){ this(71); }

/** Make an empty set of properties, choosing the starting capacity.<br />
 *  <br />
  */   
   public PropMap(final int startCap){
      int cap  = PropMapHelper.gtPrim(startCap);
      if (cap == -1) cap = 71;
      entries = new PropMapHelper.Entry[cap];
      internalEntrySet = this.new EntrySet();
      internalKeySet   = this.new KeySet<CharSequence>();
   } // PropMap(int)
   
/** Remove an Entry by (internal) index. <br />
 *  <br />
 *  Internal helper method.<br />
 *  <br />
 *  Implementation hint: This implementation removes the indexed entry from
 *  this PropMap. All other removal methods use this method. It's the one to
 *  be modified / overridden if inheritors want to avert the removal of 
 *  certain entries.<br />
 *  <br /> 
 *  @return true if an existing entry was removed.
 */
    protected boolean remove(final int i){
       if (i < 0 || i >= entries.length || size == 0) return false;
       PropMapHelper.Entry akt = entries[i];
       if (akt == null) return false; // empty already
       wLock.lock();
       if (i >= entries.length || size == 0) { // now out of range
          wLock.unlock();
          return false;
       } // now out of range
       akt = entries[i];
       if (akt != null) { // remove non null
         entries[i] = null;
         --size;
       } // remove non null
       wLock.unlock();
       return true;
    } // remove(int)
    
/** Find the index for a key. <br />
 *  <br />
 *  Internal helper with little use outside this class or its 
 *  inheritors.<br />
 *  Hint: This method uses no synchronisation at all. If used outside (other
 *  method's) sync. blocks it may lie in case of concurrent 
 *  modifications.<br /> 
 *  <br />
 *  @return -1, if no such key.
 */    
    public final int indexOfKey(final CharSequence key){
       if (key == null || size == 0) return -1; // no key or empty
       final int le = key.length();
       if (le == 0 || key.charAt(0) == ' ' || key.charAt(le -1) == ' '  )
           return -1; // forbidden as key
       final String kS = key.toString();
       final int cap = entries.length;

       int suInd = (PropMapHelper.posHash(kS) % cap) -1; // poor man's hash
       suList: for (int step = 0; step <= hashMissMax; ++step ) {
          if (++suInd == cap) suInd = 0;
          PropMapHelper.Entry e = entries[suInd];
          if (e == null) continue suList;
          final String eKey = e.key;
          if (eKey.equals(kS)) return suInd;
       } // suList: for 
       return -1;
    } // indexOfKey(CharSequence)


/** Is there a certain key. <br />
 *  <br />
 *  Keys have to be non empty character sequences, even after 
 *  &quot;trimming&quot;.<br />
 *  <br />
 *  @see java.util.Map#containsKey(java.lang.Object)
 */
   @Override public final boolean containsKey(Object key){
      if (!(key instanceof CharSequence)) return false;
      final String kS = TextHelper.trimUq((CharSequence)key, null);
      return indexOfKey(kS) >= 0;
   } // containsKey(Object )

/** Get the value for a key. <br />
 *  <br />
 *  If an entry fitting the key (that has to be a legal character sequence) 
 *  exist its value is returned; otherwise null.<br />
 *  <br />
 *  Hint: null may also be the value of an entry found. If this 
 *  ambiguity is not acceptable, use {@link #containsKey(Object)}.<br /> 
 *  <br />
 *  @see java.util.Map#get(java.lang.Object)
 *  @see #containsKey(Object)
 *  @return String oder null
 */
   @Override public String get(final Object key){
      if (!(key instanceof CharSequence)) return null;
      PropMapHelper.Entry found = entry((CharSequence)key);
      return found == null ? null : found.value;
   } // get(Object)

/** Get the value for a key. <br />
 *  <br />
 *  This method is just like {@link #get(Object) get(Object)}, but with
 *  a method signature better fitting this class'  contract.<br />
 *  <br />
 *  @see java.util.Map#get(java.lang.Object)
 */
   public final String value(final CharSequence key){
      if (key == null) return null;
      PropMapHelper.Entry found = entry(key);
      return found == null ? null : found.value;
   } // value(CharSequence)

/** Get a property as String. <br />
 *  <br />
 *  This methods delegates to {@link #value(CharSequence)}. Its only purpose
 *  is the compatibility to the late {@link java.util.Properties} 
 *  (facilitating the change of pedigree in some cases).<br />
 */
   public final String getProperty(final String name){
      return value(name);
   } // getProperty(String)
   
   
/** Get a property  as  non empty String. <br />
 *  <br />
 *  This method returns a non empty String only if this PropMap contains
 *  a property / entry with key <code>name</code>, the value of it being
 *  a non empty String even after stripping surrounding white spaces 
 *  (from the value).<br />
 *  <br />
 *  This (may be stripped) value is returned, or null otherwise.<br />
 *  <br />
 *  The call is equivalent to 
 *  {@link #getString(CharSequence, String) getString(name, null)}.<br />
 *  <br />
 *  @param   name the key of the searched for property / entry
 *  @return       a non empty String or null
 *  @see #getString(CharSequence, String)
 *  @see #getInt(CharSequence, int)
 *  @see #getBoolean(CharSequence)
 */
   public final String getString(final CharSequence name){
      return getString(name, null);
   } // getString(CharSequence)


/** Get a property if non empty String. <br />
 *  <br />
 *  If this {@link PropMap} does not contain a property fitting 
 *  <code>key</code> (the parameter will be stripped from surrounding 
 *  white space before) the substitute {@code defV} will be returned.<br />
 *  <br />
 *  If the property exists, its value will (also) be stripped from 
 *  surrounding white space. If (still) not null nor empty it is returned.
 *  Otherwise the substitute {@code defV} will be returned.<br />
 *  <br />
 *  In other words: Instead of the value got by <code>key</code> from this
 *  {@link PropMap} 
 *  {@link TextHelper}{@link TextHelper#trimUq(CharSequence, String)
 *    .trimUq(value, defV)} is returned.<br />
 *  <br />
 *  @param   key  the name of the searched for property / entry
 *  @param   defV  the substitute for unavailable null or 
 *           (stripped) empty value
 *  @return  the (stripped) value or defV
 *  @see #getString(CharSequence)
 *  @see #getInt(CharSequence, int)
 *  @see #getBoolean(CharSequence)
 *  @see TextHelper#trimUq(CharSequence, String)
 */
   public String getString(final CharSequence key, final String defV){
     if (key == null) return defV;
     final String name = TextHelper.trimUq(key, null);
     if (name == null) return defV;
     final String val = value(name);
     if (val == null) return defV;
     return TextHelper.trimUq(val, defV);
   } // getString (CharSequence, String)


/** Get the entry for a key. <br />
 *  <br />
 *  {@code key} must be a non empty character sequence, even after stripping
 *  surrounding white space.<br />
 *  <br />
 *  Implementation hint: {@link #get(Object)},  {@link #value(CharSequence)} 
 *  and all other entry / value getters *) delegate to this method. Hence it's 
 *  the only one to be overridden in inheriting classes, if the determination
 *  of the value is less straightforward (e.g. by having to consult
 *  substitute {@link Map}s or the like).<br />
 *  <br />
 *  Hint *): {@link #containsValue(Object)},  {@link #containsKey(Object)}
 *  and {@link EntrySet#contains(Object)} are exceptions; the 
 *  work alone.<br />
 *  <br />
 *  @see java.util.Map#get(java.lang.Object)
 *  @see #get(Object)
 */
   public PropMapHelper.Entry entry(final CharSequence key){
      String k = TextHelper.trimUq(key, null);
      if (k == null) return null;
      try { rLock.lock();  // read synchronise / lock 
         int ind = indexOfKey(k);
         if (ind < 0) return null;
         return entries[ind];
      } finally { rLock.unlock(); } // read synchronise / lock 
   } // entry(CharSequence)
  
//------------------------------------------------------------------------
   
   
/** Is there a value. <br />
 *  <br />
 *  Values are character sequences; null is allowed.<br />
 *  <br />
 *  @see java.util.Map#containsValue(java.lang.Object)
 */
   @Override public boolean containsValue(Object value){
      final boolean askNull = value == null;
      if (!(askNull || value instanceof CharSequence)) return false;
      final String v = askNull ? null : ((CharSequence)value).toString();
      entriesLoop: for (PropMapHelper.Entry e : entries) {
         if (e == null) continue entriesLoop;
         final String eVal = e.value;
         if (askNull) {
            if (eVal == null) return true; 
         } else {
            if (v.equals(eVal)) return true;
         }
      } //  entriesLoop: for
      return false;
   } // containsValue(Object)
   

/** Get a property as boolean. <br />
 *  <br />
 *  Returns true, if and only if a property / entry by key {@code name} 
 *  exists in this PropMap, having a value of 
 *  "true", "wahr", "ja", "yes", "an" or "on".<br /> 
 *  The comparison is not case sensitive.<br />
 *  <br />
 *  To be exact: The value has to be interpreted by the (rules of the) method
 *  {@link TextHelper#asBoolean(CharSequence)} as true.<br />
 *  <br />
 *  @param    name  the key of the searched for property / entry
 *  @return   the boolean interpretation of its  value
 *  @see #getInt getInt()
 *  @see #getBoolean(CharSequence, boolean)
 */
   public final boolean getBoolean(final CharSequence name){
      if (name == null) return false;
      String value = value(name);
      if (value == null) return false;
      return TextHelper.asBoolean(value);
   } // getBoolean(CharSequence)


/** Get a property as boolean with default. <br />
 *  <br />
 *  If there's no property named {@code name} here {@code defV} is
 *  returned.<br />
 *  <br />
 *  Otherwise it is tried to interpret the value by
 *  {@link TextHelper#asBoolean(CharSequence, boolean) 
 *  asBoolean(value, defV)} as a logical value. If that is feasible by 
 *  that method's rules  that value is returned and {@code defV} 
 *  otherwise.<br />
 *  <br />
 *  @see #getBoolean(CharSequence)
 *  <br />
 *  @param   name the properties name
 *  @param   defV default / substitute value
 *  @return       the property's boolean value if so interpretable, or defV
 */
   public final boolean getBoolean(CharSequence name, boolean defV){
      if (name == null) return defV;
     String value = value(name);
     if (value == null) return defV;
     return TextHelper.asBoolean(value, defV);
   } // getBoolean(CharSequence, boolean) 

   
/** Get a property as int. <br />
 *  <br />
 *  If there's no property named {@code name} here {@code defV} is
 *  returned.<br />
 *  <br />
 *  Otherwise it is tried to interpret the value by
 *  {@link TextHelper#asInt(CharSequence, int) asInt(value, defV)} as a whole
 *  number. If that is feasible by that method's rules that value is returned 
 *  and {@code defV} otherwise.<br />
 *  <br />
 *  @param   name the properties name
 *  @param   defV default / substitute value
 *  @return       the property's integer value if so interpretable, or defV
 *  @see #getBoolean(CharSequence)
 *  @see #getLong(CharSequence, long)
 *  @see TextHelper#asInt(CharSequence, int)
 */
   public final int getInt(CharSequence name, int defV){
     if (name == null) return defV;
     String value = value(name);
     if (value == null) return defV;
     return TextHelper.asInt(value, defV);
   } // getInt(CharSequence, int)

/** Get a property as  long. <br />
 *  <br />
 *  If there's no property named {@code name} here {@code defV} is
 *  returned.<br />
 *  <br />
 *  Otherwise it is tried to interpret the value as a (long) whole number. If 
 *  that is feasible the that long value is returned and {@code defV} 
 *  otherwise.<br />
 *  <br />
 *  @param   name the properties name
 *  @param   defV default / substitute value
 *  @return       the property's long value if so interpretable, or defV
 *  @see #getBoolean(CharSequence)
 *  @see #getInt(CharSequence, int)
 */
   public long getLong(CharSequence name, long defV){
     if (name == null) return defV;
     String value = value(name);
     if (value == null) return defV;
     try {
        return Long.decode(value).longValue();
     } catch (Exception e) {}
     return defV;
   } // getLong(CharSequence, long)
  
//--------------------------------------------------------------------------   
   

/** Remove an entry. <br />
 *  <br />
 *  If there's no property / entry named {@code key} here null will be 
 *  returned.<br />
 *  Otherwise the entry will be removed and its value returned.<br />
 *  <br />
 *  In the case of the enty's removal is denied (could be done by an 
 *  overridden method {@link #remove(int)} only) null is returned also.<br />
 *  <br />  
 *  @param key the key to the entry to be removed
 *  @return    the removed entry's value or null 
 */ 
   @Override public String remove(final Object key){
      if (!(key instanceof CharSequence) || size == 0) return null;
      String k = TextHelper.trimUq((CharSequence)key, null);
      if (k == null) return null;
      try { wLock.lock();  // write synchronise / lock 
         int ind = indexOfKey(k);
         if (ind < 0) return null;
         PropMapHelper.Entry toRemove =  entries[ind];
         return remove(ind) ? toRemove.value : null;
      } finally { wLock.unlock(); } // write synchronise / lock 
    } // remove(Object)

   
   
/** Enter a key value pair. <br />
 *  <br />
 *  The key supplied has to be a non empty character sequence even after being
 *  stripped from surrounding white space by
 *  {@link TextHelper}{@link TextHelper#trimUq(CharSequence, String)}.
 *  Otherwise an {@link IllegalArgumentException} is thrown.<br />
 *  <br />
 *  If there's already an {@link PropMapHelper.Entry} for {@code key}, its
 *  value will be changed, otherwise a new pair (key, value) will be made
 *  and entered to this {@link Map}.<br />
 *  <br /> 
 *  @param key   the new entry's key
 *  @param value the new value; may be null
 *  @return      if there was an entry for key before its value or null
 *               otherwise
 *  @throws   UnsupportedOperationException if the call hits an existing
 *            entry with another value marked as immutable
 *  @throws   IllegalArgumentException  if the key supplied violates 
 *            {@link PropMap}'s rules
 *  @see #put(de.frame4j.util.PropMapHelper.Entry)
 */   
   @Override public String put(final CharSequence key, final String value)
                                       throws UnsupportedOperationException {
      final String kS = TextHelper.trimUq(key, null);
      if (kS == null)
                     throw new IllegalArgumentException(PropMap.keyStringMld);
      return putImpl(kS, value);
    } //  put(CharSequence, String)

/** Enter a key/value pair. <br />
 *  <br />
 *  This method is like {@link #put(CharSequence, String)} but without
 *  checks; it's a internal helper for correct parameters.<br />
 *  <br />
 *  @param key   the new entry's key
 *  @param value the new value; may be null
 *  @return      if there was an entry for key before its value or null
 *               otherwise
 *  @throws   UnsupportedOperationException if the call hits an existing
 *            entry with another value marked as immutable
 *  @see #put(de.frame4j.util.PropMapHelper.Entry)
 *  @see #put(CharSequence, String)
 */   
   protected final String putImpl(final String key, final CharSequence value)
                                    throws UnsupportedOperationException {
      /// final int keyHash = PropMapHelper.posHash(key); xxxxxxx
      final String vS = value == null ? null :
                 value instanceof String ? (String)value : value.toString();
     PropMapHelper.Entry newEnt = new PropMapHelper.Entry(key, vS, false);
     PropMapHelper.Entry oldEnt = put(newEnt);
     return oldEnt == null ? null : oldEnt.value;
      /// } finally { wLock.unlock(); } // write synchronise / lock 
   } //  putImpl(CharSequence, String)

/** Enter a key/value pair, guaranteed to be new. <br />
 *  <br />
 *  Like {@link #put(CharSequence, String)} but without any checks and for a
 *  really new entry only.<br />
 *  <br />
 *  Internal helper.<br />
 *  <br /> 
 *  <br />
 *  @param key   the new entry's key
 *  @param value the new value; may be null
 *  @throws   UnsupportedOperationException if the call hits an existing
 *            entry with another value marked as immutable
 *  @see #put(de.frame4j.util.PropMapHelper.Entry)
 */   
   protected final void putNewImpl(final String key, final String value)
                                       throws UnsupportedOperationException {
      put(new PropMapHelper.Entry(key, value, false));
   } // putNewImpl(String, String);

/** Enter a new immutable key/value pair. <br />
 *  <br />
 *  Like {@link #put(CharSequence, String)} but without any checks; internal
 *  helper.<br />
 *  <br /> 
 *  @param key   the new entry's key
 *  @param value the new value; may be null
 *  @throws   UnsupportedOperationException if the call hits an existing
 *            entry with another value marked as immutable
 *  @see #put(de.frame4j.util.PropMapHelper.Entry)
 *  @see #put(CharSequence, String)
 */   
   protected final void putNewImut(final String key, final String value)
                                       throws UnsupportedOperationException {
      put(new PropMapHelper.Entry(key, value, true));
   } // putNewImut(String, String);


/** Enter a key/value pair. <br />
 *  <br />
 *  This method just delegates to {@link #put(CharSequence, String)}, better
 *  called directly if value's type is String.<br />
 *  <br />
 *  @see #put(CharSequence, String) 
 *  @see #put(de.frame4j.util.PropMapHelper.Entry)
 */ 
   public final Object setProperty(final CharSequence key,
                                                  final CharSequence value){
      final String vS = value == null ? null : value.toString();
      return put(key, vS);
   } // setProperty(2*CharSequence)
   
   

/** Get a value for a key with default. <br />
 *  <br />
 *  Keys must be non empty character sequences even after stripping 
 *  surrounding white space.<br />
 *  <br />
 *  If there's no entry for key defaultValue is returned.<br />
 *  <br />
 *  Otherwise the entry's value is returned even if it is null. (Thas is the
 *  difference to {@link #getString(CharSequence, String)}.<br />
 *  <br />
 *  @see java.util.Map#get(java.lang.Object)
 *  @see #get(Object)
 *  @see #value(CharSequence)
 */
   public final String getProperty(final CharSequence key, 
                                                 final String defaultValue){
      PropMapHelper.Entry found = entry(key);
      return found == null ? defaultValue : found.value;
   } // getProperty(CharSequence, String) 
   
   

/** Get a  &quot;numbered / indexed&quot; property as String. <br />
 *  <br />
 *  If <code>key</code> is null or empty or if <code>num</code> is &lt; 0,
 *  null is returned.<br />
 *  Otherwise the following applies:<br />
 *  <br />
 *  If there's a property <code>key + num</code> (in any of the forms 
 *  mentioned below) its value is returned even if empty.<br />
 *  <br />
 *  If <code>num</code> in the range 0 to 9 and if <code>key + num</code>
 *  above fails the same is tried with one leading zero (&quot;name7&quot; 
 *  e.g. and also &quot;name07&quot;).<br />
 *  <br />
 *  If that fails index brackets ([]) around the number are tried also (would
 *  be &quot;name[7]&quot; in above example).<br />
 *  <br />
 *  If that fails and if {@code num == 0} the &quot;pure&quot; property 
 *  <code>key</code> is searched for.<br />
 *  <br />
 *  If all fails {@code defV} is returned.<br />
 *  <br />
 *  @param key the property's (pure) name
 *  @param num the property's index / number (&gt;=0)
 *  @param defV the default / substitute value for nothing or only null values
 *              found 
 */
   public String getProperty(final CharSequence key, final int num, 
                                                        final String defV){
      if (key == null || num < 0) return  null;
      final String name = TextHelper.trimUq(key, null);
      if (name == null) return  null;
      final String nuSt = String.valueOf(num);
      String ret = value(name + nuSt);
      if (ret == null && num <= 9) 
         ret = value(name + '0' + nuSt); // one leading zero
      if (ret == null)
         ret = value(name + '[' + nuSt + ']'); // try index brackets
      if (ret == null && num == 0)
         ret = value(name); // try direct for 0 only (since April 2008)
      return ret == null ? defV : ret;
   } // getProperty(CharSequence, int, String)


/** Get a property wit a (may be) prefixed key. <br />
 *  <br />
 *  If <code>key</code> is null or empty null is returned.<br />
 *  Otherwise the following applies:<br />
 *  <br />
 *  If {@code pref1} is not null and there is a property named
 *  {@code pref1.key} its value is returned (also if it is empty).<br />
 *  <br />
 *  Otherwise the same is repeated with{@code pref2}.<br />
 *  <br />
 *  If that failed the &quot;pure&quot; non prefixed property <code>key</code>
 *  is searched for.<br />
 *  <br />
 *  If none is found defV is returned.<br />
 *  <br />
 *  @param key the property's (pure) name
 *  @param pref1 the first prefix to try
 *  @param pref2 the second prefix to try
 *  @param defV  the default / substitute value for nothing or only null values
 *               found 
 */
   public String getProperty(final CharSequence key, final String pref1,
                                    final String pref2, final String defV){
      if (key == null) return  null;
      final String name = TextHelper.trimUq(key, null);
      if (name == null) return  null;
      String ret = null;
      if (pref1 != null) {
         ret = value(pref1 + '.' + name);
         if (ret != null) return ret;
      }
      if (pref2 != null) {
         ret = value(pref2 + '.' + name);
         if (ret != null) return ret;
      }
      ret = value(name); // pure trial
      return ret == null ? defV : ret;
   } // getProperty(CharSequence, 3* String)
   

   
/** Get a &quot;numbered / indexed&quot; property as boolean with default. <br />
 *  <br />
 *  This method gets a property value by 
 {@link #getProperty(CharSequence, int, String) getProperty(name, num, null)}.
 *  If that returns null, defV is returned.<br />
 *  <br />
 *  Otherwise the returned value is interpreted by
 *  {@link TextHelper#asBoolean(CharSequence, boolean) asBoolean(value, defV)}
 *  as logical value that will be returned.<br />
 *  <br />
 *  @see #getBoolean(CharSequence, boolean)
 *  <br />
 *  @param name the property's (pure) name
 *  @param num  the property's index / number (&gt;=0)
 *  @param defV the default / substitute value for nothing or only null values
 *              found 
 */
   public boolean getBoolean(CharSequence name, int num, boolean defV) {
     String value = getProperty(name, num, null);
     if (value == null) return defV;
     return TextHelper.asBoolean(value, defV);
   } // getBoolean(CharSequence, int, boolean) 


/** Get a &quot;numbered / indexed&quot; property as int with default. <br />
 *  <br />
 *  This method gets a property value by 
 {@link #getProperty(CharSequence, int, String) getProperty(name, num, null)}.
 *  If that returns null, defV is returned.<br />
 *  <br />
 *  Otherwise the returned value is interpreted by
 *  {@link TextHelper#asInt(CharSequence, int) asInt(value, defV)}
 *  as numerical value that will be returned.<br />
 *  <br />
 *  @param name the property's (pure) name
 *  @param num  the property's index / number (&gt;=0)
 *  @param defV the default / substitute value for nothing or only null values
 *              found 
 *  @see #getBoolean getBoolean()
 *  @see PropMap#getLong(CharSequence, long) getLong()
 *  @see TextHelper#asInt(CharSequence, int)
 */
   public int getInt(String name, int num, int defV) {
     if (name == null) return defV;
     String value = getProperty(name, num, null);
     if (value == null) return defV;
     return TextHelper.asInt(value, defV);
   } // getInt(String, 2*int)

//---------------------------------------------------------------------
   
/** Cache size for valueLang() look up. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see #valueLang(CharSequence, String) 
 *  @see Prop#valueLang(CharSequence)
 *  @see Prop#valueLang(CharSequence, String)
 */
   public static final int VL_CL = 11;          // cache size
   private volatile transient String[] cchKeys; // key cache for valueLang(..)
   private transient String[] cchVals;          // value cache 
   private volatile transient int cchputInd;    // put index 
   
/** Get value for a key &mdash; nationalised. <br />
 *  <br />
 *  For a null or empty key (after removal of surrounding white space) the 
 *  substitute value defV is returned.<br />
 *  <br />
 *  Otherwise language ({@link #getLanguage()}) and region 
 *  ({@link #getRegion()}) codes are used in combination with key to 
 *  search for properties in the following order:<ol>
 *  <li>language_region.key</li>
 *  <li>language.key</li>
 *  <li>key</li>
 *  <li>en.key</li>
 *  <li>en_GB.key</li>
 *  <li>de.key</li></ol>
 *  
 *  The steps 3..6 are done by
 *   {@link #substLang(String, String, String, String) 
 *   substLang(key, lang, regio, defV)},
 *  so this behaviour or strategy could be changed in classes extending
 *  {@link PropMap}.<br />
 *  If all endeavour fails defV is returned.<br />
 *  The returned value is cached for the (non empty key); so subsequent
 *  requests for the same ({@link #VL_CL}) keys are returned very fast.<br />
 *  <br />
 */
   public final String valueLang(final CharSequence key, String defV){
      final String tKey = TextHelper.trimUq(key, null); 
      if (tKey == null) return defV;
      final String lang = getLanguage();  // empties cache if change
      final String regio = getRegion();   // empties cache if change
      
      //System.out.println("  /// TEST valueLang( " + tKey + ", " + def
      //                     + ") lang = " + lang + ", regio = " + regio);
      PropMapHelper.Entry retEnt = null;
      try { rLock.lock();  // read synchronise / lock 
         if (cchKeys!= null) { // search cache
            for (int si = 0; si < VL_CL; ++si) { // for search
               final String cachKey = cchKeys[si];
               if (cachKey != null && tKey.equals(cachKey)) { // found in cache
                  String fnd =  cchVals[si];
                  //System.out.println("  /// TEST valueLang(" + key 
                    //                        + ", ) hit: "  + fnd);
                  return fnd == null ? defV : fnd;    
               }  // found in cache
            }  // for cache search
         }
         if (regio != ComVar.EMPTY_STRING) {
            retEnt = entry(lang + '_' + regio + '.' + tKey); // lang_reg.key
            //System.out.println("  /// TEST valueLang try " + lang + '_' + regio + '.' + tKey
              //                      + " > " + retEnt);
         }
         if (retEnt == null) {
            retEnt = entry(lang + '.' + tKey); // lang.key
            //System.out.println("  /// TEST valueLang try " + lang + '.' + tKey
              //                      + " > " + retEnt);
            if (retEnt == null) 
               retEnt = substLang(tKey, lang, regio, defV);
               /// System.out.println("  /// TEST valueLang try subst  > " + retEnt);
         }
         if (retEnt != null) {
            defV =  retEnt.value; // value found replace def
         }
      } finally { rLock.unlock(); } // read synchronise / lock 
   //  little double entry risk as rwlock allows no upgrade        
      try { wLock.lock();  // write synchronise / lock 
         if (cchKeys == null) {
            cchVals = new String[VL_CL];
            cchKeys = new String[VL_CL];
            cchputInd = 0;
         } else  {
            if (++cchputInd >= VL_CL) cchputInd = 0;
         }
         cchKeys[cchputInd] = tKey;  // cache key
         cchVals[cchputInd] = retEnt != null ? defV : null; // cache also null
      } finally { wLock.unlock(); } // write synchronise / lock 
     
      return  defV;  // hint: may have been replaced by found value 
   } // valueLang(CharSequence, String)
   

/** Get a substitute entry for a key &mdash; nationalised. <br />
 *  <br />
 *  For the (already worked on) case of not having found an entry for<ol>
 *  <li>language_region.key</li>
 *  <li>language.key</li></ol>
 *  this method tries to find a substitute entry in the following order:<ol>
 *  <li>key</li>
 *  <li>en.key (only if lang is not en)</li>
 *  <li>en_GB.key (only if lang is not en and if regio is not GB)</li>
 *  <li>de.key (only if lang is not de</li></ol>
 *  
 *  If none is found, null is returned.<br />
 *  This method is used in the (final) method 
 *  {@link #valueLang(CharSequence, String)} and may be overridden in 
 *  extending classes to implement further endeavours or other strategies 
 *  to get nationalised property values.<br />
 *  <br />
 *  @param key non empty
 *  @param lang two letter lower case language code
 *  @param regio two letter upper case region or country code
 *           or {@link ComVar#EMPTY_STRING} if no region to be used
 *  @param def the default value; ignored in this implementation (the 
 *             parameter being only there for potential use in inheritors)         
 */
   protected PropMapHelper.Entry substLang(final String key, 
                 final String lang, final String regio, final String def){
      PropMapHelper.Entry retEnt = entry(key); // key
      if (retEnt != null) return retEnt;
      
     // System.out.println("  /// PropMap substLang ( " + key + ", lang: " + lang
       //                       + ", reg: " + regio + ", def: " + def);

      if (!"en".equals(lang)) {
         retEnt = entry("en." + key); // lang.key
         if (retEnt != null) return retEnt;
         if (!"GB".equals(regio)) {
            retEnt = entry("en_GB." + key); // lang.key
            if (retEnt != null) return retEnt;
         } // default en_GB.
      } // default en.
      if (!"de".equals(lang)) {
         return entry("de." + key); // lang.key
      } // default de.
      return null;
   } // substLang(4*String) 
   


//--------------------- language and region --------------------------------  

/** The (preferred) language of this PropMap. <br />
 *  <br />
 *  @see #getLanguage()   
 */
   protected String language;

/** The (preferred) language of this PropMap. <br />
 *  <br />
 *  If a language was set, it will be returned.<br />
 *  Otherwise the default (or runtime) language will be set before.<br />
 *  <br />
 *  @see #setLanguage(CharSequence)
 *  @return a two letter (lower case) language code, never null   
 */
   public final String getLanguage(){
      if (this.language == null) setLanguage(null);
      return this.language;
   } // getLanguage()

/** Set the (preferred) language of this PropMap. <br />
 *  <br />
 *  If the parameter language is null the (preferred) language will remain
 *  unchanged, if it was already set. Otherwise the values will be chosen 
 *  from a property &quot;language&quot;, if present, or by the (default) 
 *  rules of 
 *  {@link TextHelper#checkLanguage(CharSequence, CharSequence)}.<br />
 *  <br />
 *  The parameter will be checked / corrected according to the rules of
 *  TextHelper.checkLanguage() with an already set language as default.<br />
 *  The (preferred) language of this PropMap will the be set 
 *  accordingly.<br />
 *  <br />
 *  Should this PropMap contain a property key=&quot;language&quot;, its value
 *  will a) be used as default for parameter null and b) be set 
 *  accordingly.<br />
 *  <br />
 *  Five letter parameters on the form &quot;en_GB&quot; are accepted, too.
 *  in this case the last two characters are passed to 
 *  {@link #setRegion(CharSequence)} and the first two taken as effective
 *  argument to this method.<br />
 *  <br />
 *  @see #getLanguage()
 *  @param language new preferred language
 */
   public void setLanguage(final CharSequence language){
      final String oldLang = this.language;
      final String oldReg  = this.region;
      String newReg = oldReg;
      String newLang = TextHelper.trimUq(language, null);
      if (newLang == null) {
         if (oldLang != null && oldLang != ComVar.EMPTY_STRING) return; 
         newLang = TextHelper.trimUq(value("language"), "en");
      }
      if (newLang.length() == 5 && newLang.charAt(2) == '_') { // lang_regio
         newReg = newLang.substring(3);
         newLang = newLang.substring(0, 2);
      } // my be lang_regio
      
      newLang = TextHelper.checkLanguage(newLang, null);
      if (oldReg == newReg && newLang.equals(oldLang)) return; // nothing to do

      try { wLock.lock();  // write synchronise / lock 
         if (oldReg != newReg) {
            setRegion(newReg); // region changed
            //  if (newLang.equals(oldLang)) return; // only region  changed ??
         }
         int ind = indexOfKey("language");
         if (ind >= 0) {
            entries[ind].setVal(newLang);
         }
         this.language = newLang;
         cchKeys = null;  // clear valueLang()'s cache    
      } finally { wLock.unlock(); } // write synchronise / lock 
   } // setLanguage(CharSequence)

   
/** The (preferred) region of this PropMap. <br />
 *  <br />
 *  @see #getRegion()   
 */
   protected String region = ComVar.EMPTY_STRING;

/** The (preferred) region of this PropMap. <br />
 *  <br />
 *  If a region was set, it will be returned.<br />
 *  Otherwise the default (or runtime) language will be set before.<br />
 *  <br />
 *  @see #setRegion(CharSequence)
 *  @return a two letter (lower case) region code, never null   
 */
   public final String getRegion(){
      if (this.region == null || this.region == ComVar.EMPTY_STRING) 
         setRegion(null);
      return this.region;
   } // getRegion()

/** Set the (preferred) region of this PropMap. <br />
 *  <br />
 *  If the parameter region is null the (preferred) region will remain
 *  unchanged, if it was already set. Otherwise the values will be chosen 
 *  from a property &quot;region&quot;, if present, or by the (default) rules
 *   of {@link TextHelper#checkRegion(CharSequence, CharSequence)}.<br />
 *  <br />
 *  The parameter will be checked / corrected according to the rules of
 *  TextHelper.checkRegion() with an already set region as default.<br />
 *  The (preferred) region of this PropMap will the be set 
 *  accordingly.<br />
 *  <br />
 *  Should this PropMap contain a property key=&quot;region&quot;, its value
 *  will a) be used as a default for parameter null and b) be set accordingly.<br />
 *  <br />
 *  @see #getRegion()
 *  @param region new preferred region, null will be replaced  
 */
   public void setRegion(final CharSequence region){
      final String oldRegio = this.region;
      CharSequence parVal;
      if (region == null) { // null no change or set default
         if (oldRegio != null && oldRegio != ComVar.EMPTY_STRING) return;
         parVal = value("region");
      } else { // null no change or set default
         parVal = region;
      }
      final String newVal = TextHelper.checkRegion(parVal, oldRegio);
      if (newVal == oldRegio) return; // no change
    
      try { wLock.lock();  // write synchronise / lock 
          int ind = indexOfKey("region");
          if (ind >= 0) {
             entries[ind].setVal(newVal);
          }
          this.region = newVal;
          cchKeys = null;  // clear valueLang()'s cache    
      } finally { wLock.unlock(); } // write synchronise / lock 
   } // setRegion(CharSequence)

//--------------------------------------------------------------------------  

/** Re-hash. <br />
 *  <br />
 *  This method enlarges the capacity and enters {@code newEnt} at
 *  the &quot;hash right&quot; spot.<br />
 *  <br />
 *  @param newEnt null or until now not yet entered.<br />
 *          Violating this rule produces any chaos. <br />
 *  @param ensCap ordered minimal new capacity, if larger than before
 */
   protected void rehash(final PropMapHelper.Entry newEnt, final int ensCap){
      try { wLock.lock();  // write synchronise / lock 
         final PropMapHelper.Entry[] oldEntries = entries;
         int cap = entries.length;
         int i = cap - 1; // old cap !  // putLoop 
         
         int needSize = size + cap + 13; // pseudo doubling
         if (ensCap > needSize) needSize = ensCap; // request
         cap = PropMapHelper.gtPrim(needSize);  // nx / max. dop. prim
         entries = new PropMapHelper.Entry[cap];
         if (newEnt != null) {
            int setInd =  newEnt.keyHash % cap;
            entries[setInd] = newEnt; // put the new if any to the right spot 
            //System.out.println("\n  ///   TEST PropMap rehash ["
              //    + setInd + "] / "   + size + " / " + cap + " : \n      " 
                //  + newEnt + "---- \n");
            ++size;
         }
         hashMissMax = hashMissSum = 0;
         ++reHashes;
   
         int foul;
         putInLoop: for ( ; i >= 0; --i) {
            PropMapHelper.Entry e = oldEntries[i];
            if (e == null) continue putInLoop;
            int suInd = e.keyHash % cap;
            foul  = 0; // 0 = hit;
            while(true) { // find empty
               if (entries[suInd] == null) { // empty slot found
                  entries[suInd] = e;
                  if (foul != 0) {
                     if (hashMissMax < foul) hashMissMax = foul; // max.
                     hashMissSum += foul; // Add.
                  }
                  
                  ///System.out.println("  ///   TEST PropMap.rhs [" + suInd 
                     ///  + (foul == 0 ? " / hit" : " / +" + foul )
                      ///  + "] = " + e.key);
                  
                  continue putInLoop;
               } // empty slot found
               ++ foul; // Miss side steps
               if (++suInd == cap) suInd = 0;            
            } // find empty
         } // putInLoop: for
      } finally { wLock.unlock(); } // write synchronise / lock 
   } // rehash(Entry, int)


/** Re-hash. <br />
 *  <br />
 *  This method enlarges the capacity and re-organises the internal 
 *  hashing. The call is equivalent to<br /> &nbsp;
 {@link #rehash(de.frame4j.util.PropMapHelper.Entry, int) rehash(null, 0)}.<br />
 */
   public void rehash(){ rehash(null, 0); }

/** Enter a key value pair as Entry. <br />
 *  <br />
 *  If an entry with the same key as {@code newEntry} is not yet in this map
 *  {@code newEntry} will be put in and null will be returned. Null will also 
 *  be returned if {@code newEntry} is null.<br />
 *  <br />
 *  If an entry with the same key exists already it is replaced only if it 
 *  is either not immutable or the values are the same.<br />
 *  <br />
 *  Trying to change / replace an immutable entry will be punished by an
 *  {@link  UnsupportedOperationException}.<br />
 *  <br />
 *  Implementation hint: All other entry methods of this class rely on / 
 *  delegate to this one if changes to the set are due. Extending classes must
 *  only override this method (and keep the implementation by super(..))
 *  if extra work on changes has to be done.<br />
 *  <br /> 
 *  @param     newEntry an Entry object (according to the contract) or null
 *  @return    the former entry if one was replaced or null
 *  @throws    UnsupportedOperationException if an immutable entry would 
 *             have to be replaced / modified
 *  @see #put(CharSequence, String)
 */   
   public PropMapHelper.Entry put(PropMapHelper.Entry newEntry)
                                      throws UnsupportedOperationException {
      if (newEntry == null) return null;
      final String kS   = newEntry.key;
      final String vS   = newEntry.value;
      final int keyHash = newEntry.keyHash;
      try { wLock.lock();  // write synchronise / lock 
         final int cap = entries.length;
         int suInd     = (keyHash % cap ) - 1; // poor man's hash
         int emptySlot = -1;
         int foul  = 0;
         
         such: for (int seaSt = 0; seaSt < cap ; ++seaSt ) {
            if (++suInd == cap) suInd = 0;
            PropMapHelper.Entry e = entries[suInd];
            if (e == null) { // empty slot found
               if (emptySlot == -1) {
                  emptySlot = suInd; // slot found
                  foul  = seaSt;
               }
               if (seaSt >= hashMissMax) break such; // further search no sense
               continue such; 
            } // empty slot found
            if (e == newEntry) return e; // found newEntry self, do nil
            
            if (seaSt > hashMissMax) {  // further search no sense
               if (emptySlot != -1) break such; // gap was found already 
               if (seaSt > 13 && size + size > cap) break such; // rehash !!!
               continue such;
            } // further search no sense
   
            final String eKey = e.key;
            if (eKey.equals(kS)) { // entry(key) found
               if (e.immutable) { // previous entry is immutable
                  if (e.value == vS) return e; // same value >>> OK
                  if (vS != null && vS.equals(e.value)) return e; // equal  OK
                  throw new UnsupportedOperationException(
                     "put to immutable Map.Entry " + eKey);
               }  // previous entry is immutable
               entries[suInd] = newEntry;  // just replace on spot
               return e;  /// ===> entry found and done 
            } // entry(key) found
         } // such: for
   
         //  to here only if no entry of equal key was found
         
         // System.out.println("  ///   TEST PropMap.putE [" + emptySlot 
            //         + (foul == 0 ? " / hit" : " / +" + foul )
              //       + "] = " + kS);
         
         putInEmpty: if (emptySlot != -1) { // an empty slot was found above
               if (foul != 0) { // hash-Miss
                  if (foul > 10 && size + size > cap) 
                     break putInEmpty; // rehash !!!!
                  if (hashMissMax < foul) hashMissMax = foul; // max.
                  hashMissSum += foul; // Add.
              } // hash-Miss
              entries[emptySlot] = newEntry;
              ++size;
              return null;
         } //  an empty slot was found above
   
         // to here only if no sensible empty slot or if no space:  rehash
         rehash(newEntry, 0);
      } finally { wLock.unlock(); } // write synchronise / lock 
      return null;
   } //  put(Entry)


/** Adding a new property. <br />
 *  <br />
 *  The parameter {@code key} is stripped from surrounding white space. 
 *  If (then) empty  nothing happens and and false is returned.<br />
 *  <br />
 *  If an entry fitting {@code key} is already in this Map it is not touched 
 *  and false is returned. Otherwise a new entry key/value is made and 
 *  entered. true is returned in that case.<br />
 *  <br />
 *  Non regarding the return and parameter types this method is exactly like 
 *  {@link #putIfAbsent(CharSequence, String)}.<br />
 *  <br />
 *  @see #put(CharSequence, String)
 *  @see #putIfAbsent(CharSequence, String)
 */ 
   public boolean add(final CharSequence key, final CharSequence value){
      final String kS = TextHelper.trimUq(key, null);
      if (kS == null) throw new NullPointerException(
                              "add(k,v) empty/null key forbidden in PropMap");
      try { wLock.lock();  // write synchronise / lock 
         if (indexOfKey(key) != -1) return false;
         putImpl(kS, value);
      } finally { wLock.unlock(); } // write synchronise / lock 
      return true;
   } // add(2*CharSequence)


/** Putting a new property. <br />
 *  <br />
 *  This method makes a new entry, i.e. a key value pair according to the 
 *  parameters (after trimming key from surrounding white space) if 
 *  no entry fitting the key is yet there.<br />
 *  <br />
 *  If key is empty or there is already an entry fitting key nothing happens
 *  and null or the existing value is returned.<br />
 *  <br />
 *  Otherwise a new mutable entry (trimmed) key / value is added to this 
 *  PropMap and null is returned.<br />
 *  <br />
 *  @see #put(CharSequence, String)
 *  @see #add(CharSequence, CharSequence)
 *  @return the value associated with the specified key if the entry is
 *          already contained, or null if a new entry was made. (null
 *          can also be returned, if an already existing entry does have that 
 *          value.)
 */ 
   @Override public String putIfAbsent(final CharSequence key, 
                                                       final String value){
      final String kS = TextHelper.trimUq(key, null);
      if (kS == null) return null;
      try { wLock.lock();  // write synchronise / lock 
         int found = indexOfKey(key);
         if (found != -1) return entries[found].value;
         return putImpl(kS, value);
      } finally { wLock.unlock(); } // write synchronise / lock 
   } // putIfAbsent(CharSequence, String)

/** Remove an entry. <br />
 *  <br />
 *  The entry for key is removed if<ul>
 *  <li> is exists in this map and </li>
 *  <li> has {@code value } as value. </li>
 *  </ul>
 *  <br />
 *  This method removes immutable entries (in the the sense that immutable
 *  does not mean non removable.)<br />
 *  <br />
 *  @return <tt>true</tt> if the an entry for key was found in and removed
 *          from this map
 */
   @Override public boolean remove(final Object key, final Object value){
      if (! (key instanceof CharSequence)) return false;
      String theVal = null; 
      if (value != null) {
         if (! (value instanceof CharSequence)) return false;         
         theVal = value.toString();
      }
     try { wLock.lock();  // write synchronise / lock 
        final int index = indexOfKey((CharSequence)key);
        if (index < 0) return false;
        PropMapHelper.Entry e = entries[index];
        final String v = e.value;
        if (v != theVal) { // optimise and null==null done
              if (v == null) return false;
              if (!v.equals(theVal)) return false;
        } // optimise and null==null done
        entries[index] = null;
        --size;
        return true;
     } finally { wLock.unlock(); } // write synchronise / lock 
   } //  remove(2*Object) 

/** Replace an entry's value.  <br />
 *  <br />
 *  This method replaces the value of an entry for {@code key} only if that
 *  entry is already in this map and it is not immutable.<br />
 *  <br />
 */
   @Override public String replace(final CharSequence key,
                                                          final String value){
      try { rLock.lock();  // read synchronise / lock 
         final int ind = indexOfKey(key);
         if (ind < 0) return null;
         final PropMapHelper.Entry e = entries[ind];
         final String v = e.value;
         if (v == value) return v; // equal (including null == null)
         if (v != null && v.equals(value)) return v;
         if (e.immutable) return null;
         if (!v.equals(value)) e.setVal(value);
         return v;
      } finally { rLock.unlock(); } // read synchronise / lock 
   } // replace(CharSequence, String)

/** Replace an entry's value.  <br />
 *  <br />
 *  This method replaces the value of an entry for {@code key} only if that
 *  entry is already in this map and it is not immutable and if its former
 *  value id oldValue.<br />
 *  <br />
 */
   @Override public boolean replace(final CharSequence key, 
                             final String oldValue, final String newValue){
      if (key == null) return false; // should be NullP or IllegalArg
      try { rLock.lock();  // read synchronise / lock 
         final int ind = indexOfKey(key);
         if (ind < 0) return false;
         final PropMapHelper.Entry e = entries[ind];
         final String v = e.value;
         if (v != oldValue) {  // optimise and null==null
            if (oldValue == null || ! oldValue.equals(v)) return false;
         } // optimise and null==null
         if (oldValue == newValue) return true;
         if (oldValue != null && oldValue.equals(newValue)) return true;
         if (e.immutable) return false; /// should better be unsupOp
         e.setVal(newValue);
         return true;
     } finally { rLock.unlock(); } // read synchronise / lock 
   } // replace(CharSequence, 2*String)


/** Set a property. <br />
 *  <br />
 *  The call is equivalent to {@link #setProperty(CharSequence, boolean)
 *  setProperty(keyValue, false)}.<br /> 
 *  <br />
 *  @see #getBoolean(CharSequence, boolean) getBooelan()
 *  @see #getInt(CharSequence, int)     getInt()
 *  @see #setNewProperty(CharSequence)
 */ 
   public boolean setProperty(final CharSequence keyValue){
      return setProperty(keyValue, false);
  } // setProperty(CharSequence)


/** Set a new property. <br />
 *  <br />
 *  The call is equivalent to {@link #setProperty(CharSequence, boolean)
 *  setProperty(keyValue, true)}.<br /> 
 *  <br />
 *  @see #setProperty(CharSequence)
 *  @see #setNewProperty(CharSequence, String)
 *  @see #setProperty(CharSequence, boolean)
 */ 
   public boolean setNewProperty(CharSequence keyValue) {
      return setProperty(keyValue, true);
  } // setNewProperty(CharSequence)
   
   

/** Set property or make a new one. <br />
 *  <br />
 *  The character sequence keyValue will be stripped from surrounding white 
 *  space and then separated at the first occurrence of<ul>
 *  <li> Equal sign (=),</li>
 *  <li> Colon (:) oder</li>
 *  <li> Space ( ) or a control character (&lt;\\u0020),</li>
 *  </ul>
 *  where  : or = may be surrounded by spaces.
 *  This separation gives a key / value pair. &quot;  uhu   =   otto 3 &quot;,
 *  for example, means key &quot;uhu&quot; and value &quot;otto 3&quot;.<br />
 *  <br />
 *  A missing or empty key will have no effect at all and return false.<br />
 *  <br />
 *  A missing value will be (String)null (not the empty String).<br />
 *  <br />
 *  If onlyNew is true and an entry fitting key already exists nothing happens
 *  and false is returned.<br />
 *  <br />
 *  Otherwise an entry fitting key will be made or modified. True is
 *  returned if the entry is new or had the value null until now.<br />
 *  <br />
 *  @see #setProperty(CharSequence)
 *  @see #setNewProperty(CharSequence, String)
 */ 
   public final boolean setProperty(final CharSequence keyValue, 
                                                     final boolean onlyNew){
      ///System.out.println("  ///  TEST setProperty(" + keyValue
         ///                      + ", " + onlyNew + ")");
      final String keyVal = TextHelper.trimUq(keyValue, null);
      if (keyVal == null) return false;
      final int len = keyVal.length();
      
      int separatorIndex;
      for (separatorIndex = 0; separatorIndex < len;
                          ++separatorIndex) { // look for separator
         char currentChar = keyVal.charAt(separatorIndex);
         if (currentChar <=  ' ' || currentChar == '='
                                               || currentChar == ':') break;
      } // for look for separator
      // Skip over whitespace after key if any
      int valueIndex = separatorIndex;
      boolean sepFound = false;  // = or : found as separator
      for  ( ; valueIndex < len; ++valueIndex) {
         char actChar = keyVal.charAt(valueIndex);
         if (actChar  > ' ') {
            if (sepFound) break;
            sepFound = actChar == '=' ||  actChar == ':';
            if (!sepFound) break; 
         }
      } // skip over whitespace and 1* : or =

      final String key = keyVal.substring(0, separatorIndex);
      final String value = valueIndex < len ? keyVal.substring(valueIndex) : null;
      
      if (onlyNew) return setNewProperty(key, value);

      return putImpl(key, value) == null;
  } // setNewProperty(CharSequence, boolean)
   

/** Set a new property. <br />
 *  <br />
 *  This method makes a new entry, i.e. a key value pair according to the 
 *  parameters (after trimming key from surrounding white space).<br />
 *  <br />
 *  If key is empty or there is already an entry fitting key nothing happens
 *  and false is returned.<br />
 *  <br />
 *  Otherwise a new mutable entry (trimmed) key / value is added to this 
 *  PropMap and true is returned.<br />
 *  <br /> 
 *  @see #setProperty(CharSequence) setProperty()
 *  @see #setNewProperty(CharSequence)
 */ 
   public boolean setNewProperty(final CharSequence key, final String value){
      final String kS = TextHelper.trimUq(key, null);
      if (kS == null) return false;
      try { wLock.lock();  // write synchronise / lock 
         int found = indexOfKey(kS);
         if (found != -1) return false;
         put( new PropMapHelper.Entry(kS, value, false));
         return true;
      } finally { wLock.unlock(); } // write synchronise / lock 
  } // setNewProperty(CharSequence, String) 
   
   
/** Take all entries from another {@link Map}.<br />
 *  <br />
 *  The obvious effect of this method is that described for 
 *  {@link AbstractMap} with extra exploiting the cases where {@code map} is
 *  of type {@link PropMap} or where  {@code map}'s Entry{@link Set} contains
 *  {@link PropMapHelper.Entry}s.<br />
 *  <br />
 *  If map {@code map} is not a {@link PropMap} only those entries will be 
 *  transfered that fulfil this classes contract for values and keys.
 *  key/value pairs of type {@link CharSequence}/{@link Number},
 *  {@link CharSequence}/{@link Character}  or 
 *  {@link CharSequence}/{@link Boolean} will be transformed accordingly.<br />
 *  <br />
 *  @param map entries to be taken to this map
 *  @throws UnsupportedOperationException if an existing immutable entry
 *          would have to be changed
 */
   @Override public void putAll(
                   final Map<? extends CharSequence, ? extends String> map){
      if (map  == null || map == this) return;
      
      if (map instanceof PropMap) {
         final PropMap putMap = (PropMap) map;
         try { putMap.rLock.lock(); this.wLock.lock(); // other r me w lock 
            final int putMapSize = putMap.size;
            if (putMapSize == 0) return;
            if (size < putMapSize) rehash(null, putMapSize);
            final PropMapHelper.Entry[] putMapEntries = putMap.entries;
            for (PropMapHelper.Entry e : putMapEntries) {
               if (e == null) continue;
               put((PropMapHelper.Entry) e.clone());
            }
         return;
         } finally { this.wLock.unlock(); putMap.rLock.unlock(); } // sync 
      }  // is PropMap
      
      final Set<?> entSet = map.entrySet();
      if (entSet == null || entSet.size() == 0) return;
      final Iterator<?> i = entSet.iterator();
      try { wLock.lock();  // write synchronise / lock 
         copyLoop: while (i.hasNext()) {
            Map.Entry<?, ?> e =   (java.util.Map.Entry<?, ?>) i.next();
            if (e instanceof PropMapHelper.Entry) {
               put((PropMapHelper.Entry) ((PropMapHelper.Entry)e).clone());
            } else {
               String key = null;
               final Object keyO = e.getKey();
               if (keyO instanceof CharSequence) 
                  key = TextHelper.trimUq((CharSequence)keyO, null);
               if (key == null) continue copyLoop;
               String value = null;
               final Object valueO = e.getValue();
               if (valueO != null) {
                  if (valueO instanceof CharSequence
                      ||  valueO instanceof Number
                      ||  valueO instanceof Boolean
                      ||  valueO instanceof Character) {
                     value = valueO.toString();
                  } else continue copyLoop;
               }
               putImpl(key, value);
            }
         } // copyLoop: while
      } finally { wLock.unlock(); } // write synchronise / lock 
   } // putAll(Map)


/** The hash quality as two line String. <br />
 *  <br />
 *  Appended to the supplied {@link StringBuilder} are two comment lines 
 *  (beginning with  &nbsp;# ) describing the actual hash quality of this
 *  {@link PropMap} object. For testing and debugging purposes.<br />
 *  <br />
 *  @param bastel StringBuilder to append to. If non existing, it will be
 *                made with a starting capacity of 133
 *  @return bastel
 *  @see #toString()
 */   
    public final StringBuilder hashQuality(StringBuilder bastel){
       if (bastel == null) bastel = new StringBuilder(133);
       bastel.append("  #  < ").append(getClass().getName());
       bastel.append("  ").append(size).append(" / ").append(entries.length)
       .append(" entries; re-hashes= ").append(reHashes)
        .append(";\n  #  miss max.= ").append(hashMissMax)
           .append(" steps, avrg.= (").append(100 * hashMissSum / size)
           .append("/100) steps >");
       
       return bastel;
    } // hashQuality(StringBuilder)
   


/** Sorted multi line entries list. <br />
 *  <br />
 *  Appended to the supplied {@link StringBuilder} will be a multi-line list
 *  sorted by keys (with at least one line per entry) of all entries 
 *  ({@link PropMapHelper.Entry}).<br />
 *  <br />
 *  @param bastel StringBuilder to append to. If non existing, it will be
 *                made with a starting capacity of {@link #size()} * 43 + 122
 *  @return bastel
 *  @see #toString()
 *  @see #hashQuality(StringBuilder)
 */   
    public StringBuilder sortedList(StringBuilder bastel){
       PropMapHelper.Entry[] list = 
                                 (PropMapHelper.Entry[]) entrySet().toArray();
       Arrays.sort(list);
       final int liLe = list.length;
       if (bastel == null) bastel = new StringBuilder(liLe * 43 + 122);
       for (PropMapHelper.Entry e : list) { 
          e.appendTo(bastel).append('\n');
       }
       return bastel;
    } // sortedList(StringBuilder)
    


/** State as multi line String. <br />
 *  <br />
 *  A multi-line list sorted by keys (with at least one line per entry) of all
 *  entries ({@link PropMapHelper.Entry}) will be returned.<br />
 *  <br />
 *  The returned list will be prepended by two comment lines describing
 *  the actual hash quality of this {@link PropMap} and by two empty 
 *  lines.<br />
 *  <br />
 *  The outputs syntax is a property list readable / parsable by this classes
 *  respectively Java API's rules.<br />
 *  <br />
 *  @see PropMapHelper.Entry#toString()
 *  @see #hashQuality(StringBuilder)
 *  @see #sortedList(StringBuilder)
 *  @see #load(InputStream, CharSequence)
 */   
    @Override public final String toString(){
       if (size == 0) return " # < empty PropMap > ";
       StringBuilder bastel = new StringBuilder(size * 43 + 122);
       hashQuality(bastel).append("\n\n");
       sortedList(bastel);
       return bastel.toString();
    } // toString()
    
    
/** Load key value pairs from an input stream. <br />
 *  <br />
 *  Calls 
 {@link PropMapHelper#load(InputStream, CharSequence, Map) load(input, encoding, this)}.<br />
 *  <br />
 *  @param input        the stream to read the properties from
 *  @param encoding     the stream's encoding (character set)
 *  @throws IOException stream or file problems
 *  @see PropMapHelper#load(InputStream, CharSequence, Map)
 *  @see PropMapHelper#store(Map, OutputStream, CharSequence, CharSequence)
 *  @see java.util.Properties#load(InputStream)
 *  @see java.util.Properties#store(java.io.OutputStream, java.lang.String)
 *  @see #store(OutputStream, CharSequence, CharSequence)
 */    
    public void load(final InputStream input, final CharSequence encoding)
                                                         throws IOException {
       try { wLock.lock();  // write synchronise / lock 
          PropMapHelper.load(input, encoding, this);
       } finally { wLock.unlock(); } // write synchronise / lock 
    } // load(InputStream)
    


/** Maximum length of loaded .properties files or resources list. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   public static final int FILE_LIST_LEN = 25;

/** Loaded .properties files or resources list. <br />
 *  <br />
 *  In this list the method {@link #load1(CharSequence, CharSequence) load1()}
 *  usually used for all loads from files records the names of all files 
 *  loaded successfully.<br />   
 *  <br />
 *  Implementation hint: {@link #fileList} would be final, if not in the way
 *  of {@link #clone()}.<br /> 
 */
   String[] fileList = new String[FILE_LIST_LEN];

/** Actual length of loaded .properties files or resources list. */
   volatile int fileAnz;

/** First load from file or resource. */
   volatile String firstFile;


/** Load properties from (just) one text file. <br />
 *  <br />
 *  Into this {@link PropMap} object all properties (key/value pairs) parsable
 *  from the text file {@code FileName} will be entered or modified 
 *  accordingly. See 
 *  {@link PropMapHelper#load(InputStream, CharSequence, Map)} for  syntax 
 *  and all details.<br />
 *  <br />
 *  If the file opening, reading or closing fails (by any exceptions) false is 
 *  returned. Nevertheless some entries may have been changed or added to this
 *  Map.<br />
 *  <br />
 *  Exceptions thrown by {@link PropMap} operations will be forwarded. That
 *  will end the process.<br />
 *  <br />
 *  In case of success true is returned and {@code fileName} will be recorded
 *  as one of the files / resources loaded.<br />
 *  <br />
 *  @param fileName the name of the text file to be parsed and loaded. If null
 *                  false will be returned.
 *  @return true on success
 *  @param encoding     the file's or stream's encoding (character set)
 *  @see #load(InputStream, CharSequence)
 *  @see PropMapHelper#load(InputStream, CharSequence, Map)
 *  @throws IllegalArgumentException if put(String, String) will be so rejected
 *  @throws UnsupportedOperationException if put(String, String) will be so rejected
 *  @throws ClassCastException if a key violates {@link PropMap}' contract
 */ 
   public boolean load1(CharSequence fileName, final CharSequence encoding)
                     throws IllegalArgumentException,  ClassCastException {
      fileName = TextHelper.makeFName(fileName, null);  // now String
      if (fileName == null) return false;
      FileInputStream fis = null;
      try {
         fis = new FileInputStream((String)fileName);
      } catch (Exception ex1) {
         return false;
      }
      try { wLock.lock();  // write synchronise / lock 
         try {
            PropMapHelper.load(fis, encoding, this);   // load(fis);
         } catch (IOException e1) {
           return false;
         }
         if (fileAnz < FILE_LIST_LEN) {
            fileList[fileAnz] = (String)fileName;
            if (firstFile == null) firstFile = (String)fileName; 
            ++fileAnz;
         }
         try {
            fis.close();
         } catch (IOException e) {
            return false;  // 
         }
         return true;
      } finally { wLock.unlock(); } // write synchronise / lock 

   } // load1(2*CharSequence)


/** Load properties from a text file, class related. <br />
 *  <br />
 *  Into this {@link PropMap} object all properties (key/value pairs) parsable
 *  from the text file {@code FileName} will be entered or modified 
 *  accordingly. See 
 *  {@link PropMapHelper#load(InputStream, CharSequence, Map)} for syntax and 
 *  all details.<br />
 *  <br />
 *  The file will be related, if feasible, to the class loader determined by 
 *  the parameter {@code cl}. Used accordingly, this can have the effect of 
 *  getting the denoted text file as resource from the application's or 
 *  Frame4's (the framework's) .jar file. This is the best way of deploying 
 *  properties, (beans) settings and else as resources. If the .jar file is 
 *  signed, the protection against manipulation applies to the properties as
 *  well as to the classes / code.<br />
 *  <br />
 *  If the file opening, reading or closing fails (by any exceptions) false is 
 *  returned. Nevertheless some entries may have been changed or added to this
 *  Map.<br />
 *  <br />
 *  Exceptions thrown by {@link PropMap} operations will be forwarded. That
 *  will end the process.<br />
 *  <br />
 *  In case of success true is returned and {@code fileName} will be recorded
 *  as one of the files / resources loaded.<br />
 *  <br />
 *  @param cl the base class to relate resources to. If null false is returned
 *  @param fileName the name of the text file to be parsed and loaded. If null
 *                  false will be returned.
 *  @param encoding     the file's or stream's encoding (character set)
 *  @return true on success
 *  @see #load(InputStream, CharSequence)
 *  @see PropMapHelper#load(InputStream, CharSequence, Map)
 *  @throws IllegalArgumentException if put(String, String) will be 
 *           so rejected
 *  @throws UnsupportedOperationException if put(String, String)
 *           will be so rejected
 *  @throws ClassCastException if a key violates {@link PropMap}' contract
 *  @throws   ClassCastException a key violates {@link PropMap}' contract
 *  @see #load1(CharSequence, CharSequence)
 */ 
   public boolean load1(Class<?> cl,  final CharSequence fileName, 
                        CharSequence encoding) throws ClassCastException, 
                                              UnsupportedOperationException {
      if (cl == null) return false;
      String fN = TextHelper.makeFNameUJ(fileName);
      if (fN == null) return false;
      return  load1Impl(cl, fN, encoding) != null;
   } // load1(Class, String)        // since 10.12.04


/** Load properties from a text file, class related. <br />
 *  <br />
 *  Internal implementation, package access, no checks.<br />
 *  <br />
 *  @param cl the base class; never null
 *  @param fN the file name relative to the classloader's resource
 *  @return the class of the used streams on success, null otherwise
 *  @throws IllegalArgumentException if put(String, String) will be so rejected
 *  @throws UnsupportedOperationException if put(String, String) will be so rejected
 *  @throws ClassCastException if a key violates {@link PropMap}' contract
 *  @see #load1(CharSequence, CharSequence)
 */ 
   final Class<? extends InputStream> load1Impl(Class<?> cl,  final String fN,
                        final CharSequence encoding)
                   throws ClassCastException, UnsupportedOperationException {
      InputStream stream = null;
      try {
         ClassLoader clld = cl.getClassLoader();
         stream = clld.getResourceAsStream(fN);
         if (stream == null) {
            if (Prop.RESTEST) {
               System.out.println( "  //////  TEST load1( "
                                 + cl + "  " + fN
                              + "\n          no stream; classLoader " + clld);  
            }
            return null;
         }
      } catch (Exception ex1) {
         if (Prop.RESTEST) {
            System.out.println( "  //////  TEST load1( "
                      + cl + "  " + fN + "\n" + ex1);  
            ex1.printStackTrace(System.out);   
         }
         return null;
      } // no stream or classloader   
      
      try { wLock.lock();  // write synchronise / lock 
         try {
            if (Prop.RESTEST) {
               System.out.println("  TEST stream " + fN + " = " + stream);
            }
            PropMapHelper.load(stream, encoding,  this); // load(stream)
            if (Prop.RESTEST) {
               System.out.println("              " + fN + " succ. as res.");
            }
         } catch (IOException e) {
            if (Prop.RESTEST) {
               System.out.println("               failed" + e.getMessage());
            }
            return null;
         }
         if (fileAnz < FILE_LIST_LEN) {
            fileList[fileAnz] = fN + "  (as " + cl.getName() + " resource)";
            ++fileAnz;
            if (firstFile == null) firstFile = fN; 
         }
         return stream.getClass();
      } finally { wLock.unlock(); } // write synchronise / lock 
   } // load1Impl(Class, CharSequence)

//-------------------------------------------------------------------------

/** Storing all key value pairs to an output stream. <br />
 *  <br />
 *  Calls {@link PropMapHelper#store(Map, OutputStream, CharSequence, CharSequence)
 *  store(this, output, startComment, encoding)}.<br />
 *  <br />
 *  @param output        the stream to write the properties to
 *  @param startComment  a describing comment to put at the begin
 *  @param encoding      the streams encoding (character set)
 *  @throws IOException when output problems
 *  @throws NullPointerException when output or map are null
 *  @see PropMapHelper#load(InputStream, CharSequence, Map)
 *  @see PropMapHelper#store(Map, OutputStream, CharSequence, CharSequence)
 *  @see java.util.Properties#load(java.io.InputStream)
 *  @see java.util.Properties#store(java.io.OutputStream, java.lang.String)
 *  @see #load(InputStream, CharSequence)
 */    
    public void store(final OutputStream output,
             final CharSequence startComment,
             final CharSequence encoding) throws IOException {
       PropMapHelper.store(this, output, startComment, encoding);
    } // store(OutputStream, 2*CharSequence)
    
//--------------------------------------------------------------------------
    

/** Make a subset of this key/value pairs by key prefix. <br />
 *  <br />
 *  If {@code prefix} is null or empty this {@link PropMap} object itself is 
 *  returned (this).<br />
 *  <br />
 *  Otherwise a new {@link PropMap} object will be made and filled 
 *  only with those entries, which's keys are longer than prefix and start 
 *  with it. Each new entry's key will be shortened by the given prefix.<br />
 *  <br />
 *  If this {@link PropMap} is of the type {@link Prop}, a {@link Prop}
 *  object will be returned.<br />
 *  <br />
 *  Example: If this Map contains a property with the key
 *  &quot;serialA-baudRate&quot; and the value &quot;38600&quot;,  
 *  subSet(&quot;serialA-&quot;) will contain a property &quot;baudRate&quot;
 *  with the (same) value &quot;38600&quot;.<br />
 *  <br />
 *  @param prefix the prefix for the properties to take in
 *  @return the filtered new map (it may be empty)
 */
   public PropMap subSet(final CharSequence prefix){
      final int prefLen = prefix == null ? 0 : prefix.length();
      if (prefLen == 0) return this;
      final String prefixS = prefix.toString();
      final PropMap ret = this instanceof Prop ? new Prop(' ') // empty
                                               : new PropMap();
      try { rLock.lock();  // read synchronise / lock 
         entriesLoop: for (PropMapHelper.Entry e : entries) {
               if (e == null) continue entriesLoop;
               final String keyOrig = e.key;
               int kOlen = keyOrig.length();
               if (kOlen <= prefLen) continue entriesLoop;
               if (!keyOrig.startsWith(prefixS)) continue entriesLoop;
               ret.put(keyOrig.substring(prefLen), e.value);
            } //  entriesLoop: for
         return ret;
      } finally { rLock.unlock(); } // read synchronise / lock 
   } // subSet(CharSequence)
   
   
/** The content as Properties. <br />
 *  <br />
 *  Some methods (of legacy classes) do insist on getting a 
 *  {@link java.util.Properties} or a {@link java.util.Dictionary} object, 
 *  while really wanting any {@link Map}. This happens mainly in older APIs, 
 *  like in javax.mail but also in OSGI implementations.<br />
 *  <br />
 *  For those cases only, this method delivers the whole content
 *  of this {@link PropMap} as a new {@link java.util.Properties} 
 *  object.<br />
 *  <br />
 *  Returned is a new @link java.util.Properties} object containing all 
 *  key/value pairs of this object except for those, whose
 *  {@link #value(CharSequence) value} is null. A {@link PropMap} allows for
 *  null values while a {@link Properties} container does not.<br />
 *  <br />
 *  @param filter if (trimmed) not empty or null, it is used as a key filter
 *       letting only those keys pass that start with filter (e.g. mail.)
 *  @return a new {@link java.util.Properties} object containing  the
 *         key/value pairs of this object; never null, but may be empty
 *         
 */    
    public final Properties asProperties(final CharSequence filter){
       Properties ret = new Properties();
       if (size == 0) return ret; // we are empty
       final String filt = TextHelper.trimUq(filter, null);
       final boolean doFi = filt != null;
       final int fiLe = doFi ? 0 : filt.length();
       
       try { rLock.lock();  // read synchronise / lock 
          if (size != 0) for (PropMapHelper.Entry e : entries) {
             if (e == null) continue;
             final String eV = e.value;
             if (eV == null) continue;
             final String eK = e.key;
             if (doFi) {
                if (eK.length() < fiLe) continue; // shorter than prefix
                if (!eK.startsWith(filt)) continue; // does not have prefix
             }
             ret.put(eK, eV);
          } //  entriesLoop: for
       } finally { rLock.unlock(); } // read synchronise / lock 
    return ret;
    } // asProperties(CharSequence)
    
//==========================================================================   

    
/** <b>The keys as Set</b>. <br />
 *  <br />
 *  An object of this inner class features a set view of the enclosing 
 *  {@link PropMap}'s keys.<br />
 *  <br />
 *  This {@link PropMap.KeySet KeySet} object always mirrors (1:1) the 
 *  enclosing {@link PropMap}'s state. For that reason on will get just one
 *  (pseudo singleton per {@link PropMap}) {@link PropMap.KeySet} object no
 *  matter how many are requested.<br /> 
 *  <br />
 *  Hint: The seldom used (partly expensive or not recommended) methods
 *  {@link AbstractSet#add(Object)}
 *  {@link java.util.AbstractSet#addAll(java.util.Collection)},
 *  {@link java.util.AbstractSet#retainAll(java.util.Collection)},
 *  {@link AbstractSet#toArray(Object[])} and 
 *  {@link java.util.AbstractSet#removeAll(java.util.Collection)}
 *  are unchanged inheritance.<br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2004 &nbsp; Albrecht Weinert.<br />
 *  <br /> 
 *  @author   Albrecht Weinert
 *  @version  like enclosing class {@link PropMap}
 *  @see java.util.Map#size()
 *  @see PropMap#size()
 */
   public final class KeySet<S> extends AbstractSet<S> implements Set<S> {

/** No objects made directly. */
      private  KeySet(){}
      
/** Number of keys. <br />
 *  <br />
 *  @see java.util.Map#size()
 *  @return {@link PropMap#size()}
 */
      @Override public int size(){ return size; }

/** HashCode (of the {@link PropMap}). <br /> */      
      @Override public int hashCode(){ return PropMap.this.hashCode(); }
      
/** Equality with other object. <br />
 *  <br />
 *  Returns true is the enclosing PropMap has the same set of keys as the 
 *  other object contains {@link java.lang.String String}s respectively 
 *  {@link CharSequence}s as set.<br />
 *  This method returns false if the other object is no {@link Set}, if its
 *  size is not equal to the enclosing {@link PropMap} or if it contains 
 *  anything that is not a {@link CharSequence}.<br />
 *  <br />
 */      
      @Override public boolean equals(final Object other){
         if (other == this)           return true;
         if (!(other instanceof Set)) return false;
         final Set<?> oSet = (Set<?>) other;
         if (oSet.size() != size) return false; // unequal sizes
         final Object[] otherContent = oSet.toArray();

         try { rLock.lock();  // read synchronise / lock 
           if (oSet.size() != size) return false; // still unequal sizes
           for (Object oKey : otherContent) {
              if (!(oKey instanceof CharSequence)) return false;
              final int ind = indexOfKey((CharSequence) oKey);
              if (ind < 0) return false;
           }
           return true;
         } finally { rLock.unlock(); } // read synchronise / lock 
      } // equals(Object) 
      

/** An Iterator over all keys.<br />
 *  <br />
 *  This method returns an Iterator over all keys. Even if returning a 
 *  new {@link Iterator} object every time, the call is quite 
 *  inexpensive.<br />
 *  <br />
 *  The returned {@link Iterator}'s behaviour will get partly unpredictable
 *  if the enclosing {@link PropMap} changes while using the iterator.<br />
 *  <br /> 
 *  @return  a new  {@link Iterator} object (anonymous inner class)
 */
      @Override public Iterator<S> iterator(){ 
         return new Iterator<S>() {
            int ind = -1; // start- aktIndex
            int remains = size;
            
            @Override public boolean hasNext(){
               if (size == 0) remains = 0; // register a clear
               return remains > 0;
            } // hasNext()
            
            @Override public void remove(){ PropMap.this.remove(ind); }
            
            @SuppressWarnings("unchecked") // seem unavoidable as one has
                       // to cast from S to S (considered unsafe!) 
            @Override public S next(){
               try { rLock.lock();  // read synchronise / lock 
                   if (!hasNext()) 
                     throw new NoSuchElementException("no more keys");
                  --remains;
                  while(++ind < entries.length) {
                     PropMapHelper.Entry e = entries[ind];
                     if (e != null) {
                        return (S) e.key;
                     } // nextKey
                  }
                  throw new NoSuchElementException("removed in between");
               } finally { rLock.unlock(); } // read synchronise / lock 
            } // next() 

         }; // new Iterator
      } // iterator()
      
/** Is the map empty.<br />
 *  <br />
 *  @see java.util.AbstractCollection#isEmpty()
 *  @return false
 */
      @Override public final boolean isEmpty(){ return  size == 0; }


/** The keys as array.<br />
 *  <br />
 *  This method returns an array of all keys of the enclosing {@link PropMap}. 
 *  It is dense (contains no null) and is of the actual 
 *  {@link #size() size}.<br />
 *  <br />    
 *  @see java.util.AbstractCollection#toArray()
 *  @return an array of String, may be empty, but never null
 */
      @Override public Object[] toArray(){ 
         try { rLock.lock();  // read synchronise / lock 
            final int sz = size;
            final java.lang.String[] ret = new java.lang.String[sz]; 
            final int cap = entries.length;
            int setIn = 0;
            for (int i = 0; i < cap && setIn < sz; ++i ) {
               PropMapHelper.Entry e = entries[i];
               ///System.out.println(" /// TEST PropMap.KeySet.toArray ["
                  ///         + setIn + ", " + i + " / " + sz +  ", " + cap 
                     ///      + "] =\n   " + e);
               if (e == null) continue;
               ret[setIn] = e.key;
               ++setIn;
            } // do the set in
            return ret;
         } finally { rLock.unlock(); } // read synchronise / lock 
      } // toArray()

/** Is an object (as key) contained. <br />
 *  <br />
 *  This method returns true, if o is a non empty {@link CharSequence}
 *  and is equal to an existing key.<br />
 *  <br />
 *  @see java.util.AbstractCollection#contains(java.lang.Object)
 *  @return <tt>true</tt> if o is a key contained
 */
      @Override public boolean contains(final Object o){
         if (!(o instanceof CharSequence)) return false;
         final CharSequence ocs = (CharSequence)o;
         final int i = indexOfKey(ocs);
         return i >= 0;
      } //  contains(Object 


/** Delete all keys. <br />
 *  <br />
 *  The effect is a complete clearance of the enclosing {@link PropMap} object
 *  (by just calling {@link PropMap#clear() clear()}).<br />  
 *  <br />
 *  @see java.util.AbstractCollection#clear()
 *  @see PropMap#clear()
 */   
      @Override public void clear(){ PropMap.this.clear(); }
      
      
/** Add a new entry (key, null). <br />
 *  <br />
 *  If the enclosing {@link PropMap} did not yet contain an entry with 
 *  {@code key}, it is (in the end by 
 *  {@link PropMap#add(CharSequence, CharSequence) add(key, null)}) newly generated
 *  and true is returned. This only happens if key is accordance with 
 *  {@link PropMap}'s contract.<br />
 *  <br />
 *  Is there already an entry ({@code key}), it is left unchanged and false is
 *  returned.<br />
 *  <br />
 *  @param key the key of the new entry
 *  @return  true, if an new entry was added.     
 *  @throws  ClassCastException if type or value of {@code key} violates 
 *           the contract.
 *  @see #add(CharSequence, CharSequence)
 *  @see java.util.AbstractCollection#add(java.lang.Object)
 */  
      @Override public boolean add(final Object key){
         if (!(key instanceof CharSequence))
            throw new ClassCastException(PropMap.keyStringMld);
         return PropMap.this.add((CharSequence)key, null);
      } // add(Object)

/** Remove a key. <br />
 *  <br />
 *  If existing, an entry with key {@code o} will be removed from the 
 *  enclosing {@link PropMap}.<br />
 *  <br />
 *  @param o the key of the entry to be removed
 *  @see java.util.AbstractCollection#remove(java.lang.Object) 
 *  @return true, in case of a removal
 */  
      @Override public boolean remove(final Object o){
         if (!(o instanceof CharSequence)) return false;
         final CharSequence ocs = (CharSequence)o;
         final int le = ocs.length();
         if (le == 0 || ocs.charAt(0) == ' ' || ocs.charAt(le -1) == ' ')
                                         return false; // not allowed as key
         try { wLock.lock();  // write synchronise / lock 
            final int ind = indexOfKey(ocs);
            if (ind < 0) return false;
            return PropMap.this.remove(ind);
         } finally { wLock.unlock(); } // write synchronise / lock 
      } // remove(Object)

   } // class KeySet  ==============

// --------------------------------------------------------------------------     
   
/** KeySet view (singleton). <br />
 * <br />
 * Implementation hint: set on construction and clone; would be final if not
 * for clone().
 */
   protected transient volatile KeySet<CharSequence> internalKeySet;

/** The keys as Set. <br />
 *  <br />
 *  This method always returns the same (singleton) {@link KeySet}
 *  object.<br />
 *  <br />
 */
   @Override public final Set<CharSequence> keySet(){ 
      /// if (internalKeySet == null) internalKeySet = new KeySet<CharSequence>();
      return internalKeySet; 
   } // keySet()
   

/** Fetch a sorted array of all keys. <br />
 *  <br />
 *  @return the sorted list of all keys as array
 *          (never null, but may be empty)  
 *  @see #keySet()
 */
   public String[] sortedKeys(){
      if (size == 0) return ComVar.NO_STRINGS;
      String[] ret =  (String[]) keySet().toArray();
      java.util.Arrays.sort(ret);
      return ret;
   } // sortedKeys()

   
   
/** Entry set view (singleton). */
   protected transient volatile 
        AbstractSet<Map.Entry<CharSequence,String>> internalEntrySet;

/** Message on value to be a character sequence, for exceptions. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final String valStringMld =
      "de.frame4j.util.ProMap.Entry values must be CharSequences (or null).";

/** Message on key to be non empty a character sequence, 
 *                                                     for exceptions. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 */
   static public final String keyStringMld =
    "de.frame4j.util.ProMap.Entry keys must be non empty (trimmed) Strings.";
      

/** The entries as Set. <br />
 *  <br />
 *  This method always returns the same (singleton) {@link EntrySet} object
 *  for this {@link PropMap}.<br />
 *  <br />
 *  The method {@link Set#toArray()} returns (every time) a new filled
 *  {@link PropMapHelper.Entry} array.<br />
 */
   @Override public final Set<Map.Entry<CharSequence, String>> entrySet(){
      return internalEntrySet;
   } // entrySet()
   
   
   private void readObject(ObjectInputStream ois)
                                throws IOException, ClassNotFoundException {
      try { wLock.lock();  // write synchronise / lock 
         ois.defaultReadObject();
         internalEntrySet = this.new EntrySet();
         internalKeySet   = this.new KeySet<CharSequence>();
      } finally { wLock.unlock(); } // write synchronise / lock 
   } // readObject(ObjectInputStream)

//==========================================================================   

/** <b>The entries as Set</b>. <br />
 *  <br />
 *  An object of this inner class features a set view of the enclosing 
 *  {@link PropMap}'s entries.<br />
 *  <br />
 *  That {@link PropMap.EntrySet EntrySet} object always mirrors (1:1) the 
 *  enclosing {@link PropMap}'s state. For that reason on will get just one 
 *  (pseudo singleton per {@link PropMap}) {@link PropMap.EntrySet} object
 *  no matter how many are requested.<br /> 
 *  <br />
 *  Hint: The seldom used (partly expensive or not recommended) methods
 *  {@link java.util.AbstractSet#retainAll(java.util.Collection)},
 *  {@link java.util.AbstractSet#removeAll(java.util.Collection)},
 *  {@link java.util.AbstractSet#containsAll(java.util.Collection)},
 *  {@link java.util.AbstractSet#addAll(java.util.Collection)} and
 *  {@link java.util.AbstractSet#toArray(Object[])}
 *  are unchanged inheritance.<br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2004 &nbsp; Albrecht Weinert.<br />
 *  <br /> 
 *  @author   Albrecht Weinert
 *  @version  like the enclosing class {@link PropMap}
 *  @see java.util.Map#size()
 *  @see PropMap#size()
 */
   public final class EntrySet 
                   extends  AbstractSet <Map.Entry<CharSequence, String>>
                            implements Set<Map.Entry<CharSequence, String>> {

/** No objects made directly. */
      private  EntrySet() {}
            
/** Number of entries. <br />
 *  <br />
 *  @see java.util.Map#size()
 *  @return {@link PropMap#size()}
 */
      @Override public int size(){ return size; }

/** HashCode (of the enclosing {@link PropMap}). <br /> */      
      @Override public int hashCode(){ return PropMap.this.hashCode(); }
      

/** Equality with other object. <br />
 *  <br />
 *  Returns true is the enclosing PropMap share the same internal entries
 *  array.<br />
 *  @param other the object to compare to 
 *  @return true if other is an  EntrySet holding the same entries
 */      
      @Override public boolean equals(Object other){
         if (!(other instanceof EntrySet) ) return false;
         final EntrySet tmp = (EntrySet)other;
         return PropMap.this.entries == tmp.getEnclosing().entries;
      } // equals(Object)
      
      private PropMap getEnclosing(){ return PropMap.this; }

/** Delete all entries. <br />
 *  <br />
 *  Deletes all entries of the enclosing {@link PropMap} (by  calling
 *  {@link PropMap#clear() clear()}).<br />  
 *  <br />
 *  @see java.util.AbstractCollection#clear()
 *  @see PropMap#clear()
 */   
      @Override public void clear(){ PropMap.this.clear(); }
      
/** Is it empty.<br />
 *  <br />
 *  @see java.util.AbstractCollection#isEmpty()
 */
      @Override public final boolean isEmpty(){ return  size == 0; }

/** Add an entry. <br />
 *  <br />     
 */
      @Override public boolean add(final 
                      java.util.Map.Entry<CharSequence, java.lang.String> e){
         if (e == null) throw new NullPointerException(
                                           "null entry forbidden in PropMap");
         if (e instanceof PropMapHelper.Entry) {
            return PropMap.this.put((PropMapHelper.Entry) e) == null;
         }
         return PropMap.this.add(e.getKey(), e.getValue());
      } // add(Entry)

/** Remove an entry. <br />
 *  <br />
 *  If an entry equal to o is found in the enclosing {@link PropMap} it is
 *  removed.<br />
 */
      @Override public boolean remove(final Object o){
         if (!(o instanceof Entry)) return false;
         final Entry<?, ?> oE = (Entry<?, ?>)o;
         return PropMap.this.remove(oE.getKey(), oE.getValue());
      } // remove(Object)

/** The entries as array.<br />
 *  <br />
 *  This method returns an array of all entries of the enclosing 
 *  {@link PropMap}. It is dense (contains no null) and is of the actual
 *  {@link #size() size}.<br />
 *  <br />    
 *  @see java.util.AbstractCollection#toArray()
 *  @return an array of ({@link PropMapHelper.Entry Entry[])}, may be
 *      empty but never null
 */
      @Override public Object[] toArray(){ 
         try { rLock.lock();  // read synchronise / lock 
            final int sz = size;
            PropMapHelper.Entry[] ret = new PropMapHelper.Entry[sz];
            final int cap = entries.length;
            int setIn = 0;
            for (int i = 0; i < cap && setIn < sz; ++i ) {
               PropMapHelper.Entry e = entries[i];
               ///System.out.println(" /// TEST PropMap.EntrySet.toArray ["
                  ///         + setIn + ", " + i + " / " + sz +  ", " + cap 
                     ///      + "] =\n   " + e);
               if (e == null) continue;
               ret[setIn] = e;
               ++setIn;
            } // do the set in
            return ret;
         } finally { rLock.unlock(); } // read synchronise / lock 
      } // toArray()

/** An Iterator over all entries.<br />
 *  <br />
 *  This method returns an Iterator over all entries. Even though generating
 *  a new {@link Iterator} object, the call is relatively cheap.<br />
 *  <br />
 *  If the underlying PropMap is modified by others while using the returned
 *  Iterator (against the intended use case pattern) the Iterator's behaviour
 *  can get unpredictable.<br />
 *  <br /> 
 *  @return  A new {@link Iterator} object
 */
      @Override public Iterator<Map.Entry<CharSequence, String>> iterator(){ 
         return new Iterator<Map.Entry<CharSequence, String>>() {
            int ind = -1; // start- aktIndex
            int remains = size;
            @Override public boolean hasNext() {
               if (size == 0) remains = 0; // notice a clear()
               return remains > 0;
            } // hasNext()
            
            @Override public void remove() {
               PropMap.this.remove(ind);
            } // remove()

            @Override public Map.Entry<CharSequence, String> next() {
               if (!hasNext()) 
                  throw new NoSuchElementException("no more entries");
               --remains;
               while(++ind < entries.length) {
                  PropMapHelper.Entry e = entries[ind];
                  if (e != null) return e; // next Entry
               }
               throw new NoSuchElementException("removed in between");
                                                       // should not happen
            } // next()
         };
      } // iterator()

/** Is an Entry contained. <br />
 *  <br />
 *  This method returns true if {@code o} is of the type 
 *  {@link PropMapHelper.Entry} and contained in the underlying 
 *  {@link PropMap} object.<br />
 *  <br />
 *  @param o entry to look for
 *  @return <tt>true</tt> if contained in this map
 */
      @Override public boolean contains(Object o){
         if (!(o instanceof PropMapHelper.Entry)) return false;
         final PropMapHelper.Entry eSu = (PropMapHelper.Entry)o;
         final String eK = eSu.key;
         for (PropMapHelper.Entry e : entries) {
            if (e == null) continue;
            if (e == eSu) return true;
            if (e.key == eK) return false; // same key but not same entry
         }
         return false;
      } // contains(Object)

      
   }  // EntrySet =======================================================
 
} // class PropMap (20.10.2004, 28.10.2004, 22.09.2006)