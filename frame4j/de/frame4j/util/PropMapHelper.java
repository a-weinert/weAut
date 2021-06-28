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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Map;

import de.frame4j.graf.ColorHelper;
import de.frame4j.net.AttrSettable;
import de.frame4j.time.SynClock;
import de.frame4j.time.TimeHelper;
import de.frame4j.text.TextHelper;

/** <b>Helpers for maps and properties</b>. <br />
 *  <br />
 *  This class has static (helper) methods for {@link Map}s and properties
 *  especially for
 *  {@link AbstractMap AbstractMap&lt;CharSequence, String&gt;}, used among 
 *  others in {@link PropMap}, {@link Prop} and {@link AppLangMap}.<br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2009 &nbsp; Albrecht Weinert<br />
 */

@MinDoc(
   copyright = "Copyright  2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 56 $",
   lastModified   = "$Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "import",  
   purpose = "common map and properties utilities"
) public  abstract class PropMapHelper {
  
  final static boolean RESTEST = Prop.RESTEST;
                             // || true; // enable development logs on S.out

/** <b>Designation of an indexed property</b>. <br />
 *  <br />
 *  An object of this helper class ties together the (pure) name or key of an 
 *  indexed property and an index. It represents the parsing result of
 *  a key + index, like in:<ul>
 *  <li>loko[60]<br />
 *      (key: loko; index: 60),</li>
 *  <li>loko04<br />
 *      (key: loko; index: 4),</li>
 *  <li>Milch2.fOffColor4<br />
 *      (key: Milch2.fOffColor; index: 4).</li></ul>
 *      
 *  The Syntax: The index number is always decimal and &gt;=0. It is connected
 *  directly (without gap) to the key name. For better readability or 
 *  elucidating the semantics the number may be enclosed in index 
 *  brackets [ ].<br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2004 &nbsp; Albrecht Weinert.<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @see Prop
 *  @see TextHelper
 */
   public static final class Indexed implements Cloneable {
      
/** The key without the index. <br />
 *  <br />
 *  key is the pure property's name without the index number.<br />
 *  <br />
 *  If the indexed property's key is for example given as oTTo99, then 
 *  {@link #key} is oTTo and {@link #index} is 99.<br />
 *  <br /> 
 */
      public final String key;
   
/** The index without the key. <br />
 *  <br />
 *  {@link #index} is just the (pure) index number.<br />
 *  <br />
 *  If the indexed property's key is for example given as oTTo[99], then 
 *  the {@link #index} is 99 and the {@link #key} is oTTo.<br />
 *  <br /> 
 */
      public final int index;
      
/** Constructor without any checks. <br />
 *  <br />
 *  @throws NullPointerException if key is null
 */      
      private Indexed(final CharSequence key, final int index){
         this.index = index;
         this.key = key.toString();
      } // Indexed(CharSequence, int)

/** Make (if feasible) from a character sequence. <br />
 *  <br />
 *  It is tried interpret {@code name} as key to an indexed property according
 *  to the syntax:<br />
 *  otto9; otto09; otto[9] or otto[09]<br />
 *  <br />
 *  @param name complete key to an indexed property
 *  @return either a {@link PropMapHelper.Indexed} object with the separated (parsed)
 *         key and index or null, if not interpretable as such.
 */      
      public static Indexed make(final CharSequence name){
         String ns = TextHelper.trimUq(name, null);
         if (ns == null) return null;
         int len = ns.length();
         if (len < 2) return null;
         int lZif = len - 1;
         char c = ns.charAt(lZif);
         boolean braced = c == ']';
         if (braced) {     ////   m[2] z.B.
            if (len < 4) return null;
            --lZif;
            c = ns.charAt(lZif);
         }  ////   m[2] z.B.
         if (c < '0' || c > '9') return null;
         int index = c - '0';
         int mul = 10;
         while (--lZif > 0) {
            c = ns.charAt(lZif);
            if (c < '0' || c > '9') {
               if (!braced) break;
               if (c != '[' || lZif < 1) return null;
               --lZif;
               break;
            }
            index += (c - '0') * mul;
            mul *= 10;
         } // 
         return new Indexed(ns.substring(0, lZif + 1), index);
      } // make(CharSequence)
      
/** A copy of this object. <br />
 *  <br />
 *  Returns this as {@link Indexed} objects are immutable.<br />      
 */
      @Override public Object clone(){ return this; } // immutable

/** Compare to other object. <br />
 *  <br />
 *  Returns true if the the other object is an  {@link Indexed} object
 *  representing the same pure key and index.<br /> 
 */
      @Override public final boolean equals(final Object other){
         if (other == this) return true;
         if (! (other instanceof Indexed)) return false;
         return this.index == ((Indexed)other).index 
              && this.key.equals(((Indexed)other).key);
      } // equals(Object other)

      @Override public final int hashCode(){
         return (31 * 17 + index) * 17 + key.hashCode();  
      } //  hashCode()

/** State as String. <br />
 *  <br />
 *  Returns the &quot;canonical&quot; form of an indexed property's key, that
 *  is like &quot;otto[135]&quot;.<br />
 */
      @Override public String toString() {
         return key + '[' + index + ']';
      } // toString()
      
   }  // class Indexed (03.08.2004, 8.8.2004)  ==========================

   
/** <b>An entry: key value pair for (Prop)Maps</b>. <br />
 *  <br />
 *  An object of this class represents one map entry of type
 *  {@code  Map.Entry<CharSequence, String>}.<br />
 *  <br />
 *  According to the properties rules documented and implemented by the class
 *  {@link PropMap} (using this class) the {@link #key} is a non empty String
 *  that neither begins or ends with white space.<br />
 *  <br />
 *  The {@link #key} of an {@code Entry} object is always final.<br />
 *  <br />
 *  For the {@link #getValue() value} any String including null and empty is
 *  allowed. The {@link #setValue(String) value} may be changed any time,
 *  if {@link #immutable} is false. {@code immutable} itself is set finally
 *  an construction.<br />
 *  <br />
 *  Hint: Allowing the {@link #getValue() value} null is (slightly) 
 *  contradictory to {@link Map}'s rules inherited by the using classes 
 *  {@link PropMap}, {@link AppLangMap} and {@link Prop} but is very useful
 *  enhancement especially for {@link Prop} allowing to distinguish an empty
 *  and no or not yet set value. (The rationale behind non null value for Map
 *  is not quite clear &mdash; but the disadvantages in some applications 
 *  are.)<br />
 *  <br />
 *  Implementation hint: All keys and also all values shorter than 19 are 
 *  made unique (by {@link String#intern()}).<br />
 *  <br />  
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2004 &nbsp; Albrecht Weinert.<br />
 *  <br /> 
 *  @author   Albrecht Weinert
 *  @version  like the enclosing class {@link PropMapHelper}
 *  @see  java.util.Map
 *  @see  java.util.Map.Entry
 */
   public static final class Entry implements Map.Entry<CharSequence, String>,
                                Comparable<Object>, Serializable , Cloneable {
    
/** The value of this entry. <br /> */      
      protected String value;
      
/** The hash code of this entry. <br /> */
     protected int entHash;

/** The key of this entry. <br /> */      
    protected final String key;

/** The hash value of this entry. <br />
 *  <br />
 *  The positive value only depends on the (final) key. It is calculated by
 *  {@link PropMapHelper#posHash(String)}.<br />
 */      
    protected  final int keyHash;

/** The value of this entry is immutable. <br />
 *  <br />
 *  Objects of this class either allow any change of value any time or forbid
 *  it forever.
 */      
    protected  final boolean immutable;

/** The key of this Entry. <br />
 *  <br />
 *  @return a non empty (non white space surrounded) String
 *  @see java.util.Map.Entry#getKey()
 */
      @Override public final String getKey(){ return key; }

/** The value of this Entry. <br />
 *  <br />
 *  @return a String value, may be empty, or null
 *  @see #getKey()
 */
      @Override public final String getValue(){ return value; }

/** Constructor, package access, without (!) any checks. <br />
 *  <br />
 */
      Entry(final String kS, String value, final boolean immutable){
         this.immutable = immutable;
         this.key = kS.intern();
         entHash = kS.hashCode(); 
         this.keyHash = entHash & 0x7FFFFFFF;
         if (value != null && value.length() < 19) value = value.intern();
         this.value = value;
         if (value != null) entHash ^= value.hashCode();
      } // common constructor

/** Making an entry. <br />
 *  <br />
 *  {@code key} will be stripped from surrounding white space and must
 *  (still) not be empty.<br />
 *  <br />
 *  If {@code key} is OK an {@link Entry} object is made containing the
 *  {@code value}. If {@code immutable} is true the made {@link Entry}
 *  is (finally) immutable.<br />
 *  @throws   IllegalArgumentException if the (trimmed) key is empty
 */
      public static Entry make(final CharSequence key, 
                                     final CharSequence value,
                                           final boolean immutable) {
         final String kS = TextHelper.trimUq(key, null);
         if (kS == null)
            throw new IllegalArgumentException(PropMap.keyStringMld);
         final String v = value == null ? null : value.toString();
         return new Entry(kS, v, immutable);
      } // make(...)
      
/** Hash code. <br />
 *  <br />
 *  Returns the entry's hash value as defined by 
 *  {@link java.util.Map.Entry Map.Entry}'s contract not to be confused with 
 *  the {@code key}'s positive hash value
 *  ({@link #keyHash}).<br />
 *  <br />
 *  @see #equals(Object)
 */
      @Override public int hashCode(){ return entHash; }

/** Compare to other entry. <br />
 *  <br />
 *  Returns true, if the other object {@code o} is of type 
 *  {@link java.util.Map.Entry Map.Entry} and key and value are equal to 
 *  those of this object.<br />
 */      
      @Override public final boolean equals(Object o){
         if (o == this)  return true;
         if (!(o instanceof java.util.Map.Entry)) return false;
         final Map.Entry<?,?> lE = (java.util.Map.Entry<?,?>)o;
         if (!this.key.equals(lE.getKey())) return false;
         Object oVa = lE.getValue();
         if (this.value == null) return oVa == null;
         if (!(oVa instanceof String)) return false;
         return this.value.equals(oVa);
      } // equals(Object)
      

/** A copy of this entry. <br />
 *  <br />
 *  Returns a complete copy of this Entry.<br />
 *  If {@link #immutable} is true, this is returned as copies of (totally 
 *  and deeply) immutable objects are sheer waste.<br />
 */      
      @Override public Object clone(){
         if (immutable) return this;
         try {
            return super.clone();
         } catch (CloneNotSupportedException e) { // should / can not happen
            return this;
         }
      } // clone()

      
/** State as String. <br />
 *  <br />
 *  Is equivalent to 
 *  {@link #appendTo(StringBuilder) appendTo(null)} <br />.<br />
 */
      @Override public String toString(){
         return appendTo(null).toString();
      } // toString()

/** State as String. <br />
 *  <br />
 *  appends to the {@link StringBuilder} stB <br /> &nbsp;
 *  &quot; key     = value&quot;<br />
 *  making a slight effort towards columns by appending an appropriate number 
 *  of spaces to a key shorter than 18 characters.<br />
 *  <br />
 *  If {@code stB} is null, it is generated with a starting capacity of 70.<br /> 
 *  <br /> 
 *  @param stB the StringBuilder to append to; will be made if null
 *  @return stB
 */
      public StringBuilder appendTo(StringBuilder stB){
         if (stB == null) stB = new StringBuilder(70);
         int ziLen = stB.length() + 18;
         stB.append(' ').append(key).append(' ');
         if (value != null) {
            while (stB.length() < ziLen) stB.append (' ');
            stB.append(" = ");
            stB.append(value);
         }
         return stB;
      } // toStringBuilder(StringBuilder)

/** Set / change the value. <br />
 *  <br />
 *  Any character sequences, including empty and null are allowed as 
 *  (new) {@code value}.<br />
 *  <br />
 *  Is this object constructed as {@link #immutable} this method called 
 *  with a {@code value} not the same (object) as original value throws a
 *  {@link UnsupportedOperationException}.<br />
 *
 *  @see java.util.Map.Entry#setValue(java.lang.Object)
 *  @return old value 
 *  @throws UnsupportedOperationException if immutable and value not the same 
 *          as the original one
 */
      @Override public String setValue(final String value)
                                        throws UnsupportedOperationException {
         final String oldValue = this.value;
         if (value == oldValue) return oldValue;
         if (immutable) {
            throw new UnsupportedOperationException("immutable Map.Entry "
                                    + key);
         } // if (immutable 
         // value must change 
         entHash = key.hashCode();
         if (value == null) {
            this.value = null;
            return oldValue;
         }
         entHash ^= value.hashCode();
         this.value = value.length() < 19 ? value.intern() : value;
         return oldValue;
      } // setValue(String)
      
/** A package access value setter without any checks. <br />
 *  <br />      
 *  Implementation hint: Even within package direct writing to {@link #value}
 *  is forbidden due to a) interning longer values and updating the cached
 *  hash value {@link #entHash}.<br />
 *  <br />
 */
      void setVal(final String value){
         if (this.value == value) return;
         entHash = key.hashCode();
         if (value == null) {
            this.value = null;
            return;
         }
         entHash ^= value.hashCode();
         this.value = value.length() < 19 ? value.intern() : value;
      } // setVal(String)
   
/** Compare this entry to another. <br />
 *  <br />
 *  This method only compares the keys.<br />
 *  <br />
 *  @see java.lang.Comparable#compareTo(java.lang.Object)
 *  @throws ClassCastException if the other object is no
 *          {@link java.util.Map.Entry} with a String key
 */
      @Override public int compareTo(Object o){
         if (o == this) return 0;
         return key.compareTo((String)((Map.Entry<?,?>)o).getKey());
      } // compareTo(Object)

   } // class Entry  =======================================================
   

/** Characters to be transformed into backslash (\) escape sequences. <br />
 *  <br />
 *  A short string containing the characters, which within keys and values 
 *  have to be transformed into backslash escape sequences for external text
 *  representations. It's necessary because of their syntactic meaning to the
 *  parsing process.<br />
 *  <br />
 *  Contains = : # ! as well as  tabulator, return, line feed and form 
 *  feed.<br />
 */   
   public static final String specialCharsToEscape = "=:\t\r\n\f#!\\";

/** A small list of prime integers. <br />
 *  <br />
 *  Some prime numbers from 71 up to 479001599.<br />
 */   
   static final int[] primint = {
      71,  89, 157,   271,  547,  919,
      1657, 4219, 7057, 9241,
      13267, 19927, 26227, 41047, 65543, 83339,
      100183, 174469, 269117, 333667, 611953, 925607, 1001593,
      2015861, 3497867,  4087267, 7368791, 8163047, 
      11381633,  12195251, 39916801,
      479001599
   };

/** Modifier public static final. <br />
 *  <br />
 *  Value: <code>{@value}</code>
 *  @see Modifier
 */      
   static public final int PUSTFI =
      Modifier.STATIC | Modifier.FINAL | Modifier.PUBLIC;

/* * package access: do not change value */
  // static final Class<?>[] STRINGPAR  = {String.class};

/** No objects. */
   private  PropMapHelper(){}  // no JavaDoc >= package

/** Last successful file encoding for reading or default. <br />
 *  <br />
 *  default / start value: {@link ComVar#FILE_ENCODING}
 */
   protected volatile static String lastSuccReadEnco = ComVar.FILE_ENCODING;
   

/** Read a list of properties from a stream. <br />
 *  <br />
 *  This method reads an input stream line by line. Using the standard
 *  rules for .properties files key value pairs are extracted from the 
 *  text input by the parsing process.<br />
 *  <br />
 *  These key value pairs are used as arguments to the method 
 *  {@link java.util.Map}.{@link
 *  java.util.Map#put(java.lang.Object, java.lang.Object) put(key, value)}
 *  of the {@code Map<CharSequence, String> map} passed as parameter.<br />
 *  <br />
 *  The whole operation is synchronised with {@code map}.<br />
 *  <br />
 *  The mentioned syntactic rules are documented and (with one tiny bug)
 *  implemented by {@link java.util.Properties}.{@link 
 *  java.util.Properties#load(InputStream) load(InputStream)}. <br />
 *  Hint / warning: Those java.util.Properties rules are not fully 
 *  implemented by the Eclipse .properties plug-in distributed 2008.
 *  Do NOT use it.<br />
 *  <br />
 *  One little addition to the mentioned rules by this method is<ul>
 *  <li>A single (lonely) key in one input line (&nbsp;uhu&nbsp;&nbsp;) 
 *     gets the value null.</li>
 *  <li>A key in a line followed by a key value separator sign, but no
 *     explicit value (uhu&nbsp;=&nbsp;&nbsp;) gets the empty String
 *     ({@link ComVar#EMPTY_STRING}) as value.</li>
 *  </ul>
 *  
 *  This addition was made as some applications need the distinction between
 *  no value set as initial default (null) and no value set afterwards
 *  explicitly ({@link ComVar#EMPTY_STRING}) by parameter parsing.<br />
 *  <br />
 *  {@code encoding} is stripped from surrounding white space. If null or 
 *  (then) empty {@link ComVar#FILE_ENCODING} is used. If this or the given 
 *  {@code encoding} fails ISO-8859-1 is used.<br />
 *  <br />
 *  @param input the stream to read the properties from
 *  @param encoding the inputs encoding (or character set)
 *  @param map the map to put the properties in
 *  @throws IOException when problems reading the input
 *  @throws NullPointerException when input or map are null
 *  @throws UnsupportedOperationException if a <tt>put</tt> is rejected by
 *          {@code map}, the reason the often being the changing of an
 *          immutable entry's value.
 *  @throws IllegalArgumentException if put(String, String) is so rejected by
 *          {@code map}.
 */
   public static void load(InputStream input, 
                           CharSequence encoding,
                           Map<CharSequence, String> map)
               throws IOException, NullPointerException {
     // System.out.println(" //  TEST load map from " + input.toString());
      
      String enco = TextHelper.trimUq(encoding, lastSuccReadEnco);
      InputStreamReader isr;
      try {
         isr = new InputStreamReader(input, enco);
      } catch (UnsupportedEncodingException usope) {
         enco = "ISO-8859-1";
         isr = new InputStreamReader(input, enco);
      }   

      BufferedReader in = new BufferedReader(isr);
      boolean lineProcessed = false;
     /// synchronized (map) { // snyc
         lineByLine: while (true) { // outer loop
            String line = in.readLine();
            if (line == null) {
               if (lineProcessed) 
                  lastSuccReadEnco = enco;  // record successful read
               return;
            }
            int len = line.length();
            if (len ==  0) continue lineByLine; // skip empty lines
         
            //  find key start
            int keyStart;
            for (keyStart = 0; keyStart < len; ++keyStart) { // start search
               char firstChar = line.charAt(keyStart);
               // comment lines start with # or !
               if (firstChar == '#' || firstChar == '!')
                  continue lineByLine; // skip comment lines
               if (firstChar > ' ') break; // instead of    \t\r\n\f
            }  //  start search
            if (keyStart == len)  continue lineByLine; // skip empty lines

            // does the actual line want a continuation line 
            while (PropMapHelper.oddEndSlashs(line)) {  // while contiLi
               String fortsZeil = in.readLine();
               if (fortsZeil == null) // should never happen
                     fortsZeil = ""; // want continuation ... and out !?
               String ohneSlash = line.substring(0, len - 1);
               // Advance beyond whitespace on new line
               int startIndex;
               int fZLen = fortsZeil.length();
               for (startIndex = 0; startIndex < fZLen; ++startIndex) { //f1
                  if (fortsZeil.charAt(startIndex) > ' ') break;
               } // f1
               fortsZeil = fortsZeil.substring(startIndex, fZLen);
               line = new String(ohneSlash + fortsZeil);
               len = line.length();
            } // while contiLi

            // the "real" input line is now ready (or assembled)

            int separatorIndex;
            for (separatorIndex = keyStart; separatorIndex < len;
                                ++separatorIndex) { // look for separator
                  char currentChar = line.charAt(separatorIndex);
                  if (currentChar == '\\')
                     separatorIndex++;
                  else {
                     if (currentChar <=  ' ' || currentChar == '='
                        || currentChar == ':') break; //white space
                   }
            } // for look for separator

            // Skip over whitespace after key if any
            int valueIndex = separatorIndex;
            boolean sepFound = false;  // = or : found as separator
            for  ( ; valueIndex < len; ++valueIndex) {
               char actChar = line.charAt(valueIndex);
               if (actChar  > ' ') {
                  if (sepFound) break;
                  sepFound = actChar == '=' ||  actChar == ':';
                  if (!sepFound) break; // start of value
               }
            } // skip over whitespace and 1* : or =

            final String key =
                     unEscape(line.substring(keyStart, separatorIndex));
            
            String value = valueIndex < len //   separatorIndex < len
                    ? unEscape(line.substring(valueIndex, len))
                    : sepFound ? ComVar.EMPTY_STRING : null;
            
            ///System.out.println("  // Test PropMap load : \"" + key + "\"");
            map.put(key, value);
            lineProcessed = true;
         } // line by line 
     /// } // sync  outer loop
   } // load

/** Convert backslash escape sequences to the character. <br />
 *  <br />
 *  In the input sequence {@code sequ} the backslash escape sequences,
 *  namely \t, \r, \n, \f will be converted to the corresponding control
 *  character.<br />
 *  <br />
 *  &#92;uxxxx expects xxxx as four digit hex number; and that six character 
 *  sequence is converted to the corresponding unicode character.<br />
 *  <br />
 *  All other characters after the backslash \ (especially a second \) will be
 *  left unchanged.<br />
 *  <br />
 *  @param  sequ sequence to convert
 *  @return result as String
 *  @throws IllegalArgumentException if &#92;uxxxx - syntactical faults
 *  @throws NullPointerException  sequ == null
 */
   public static String unEscape(final CharSequence sequ){
      int len = sequ.length();
      StringBuilder outBuffer = new StringBuilder(len);

      loopThru:  for (int i = 0; i < len; ++i) {
         char ch = sequ.charAt(i);
         if (ch != '\\') {  // no Escape \ (normal case)
            outBuffer.append(ch);
            continue loopThru;
         } // no Escape \ (normal case)
         
         if (++i == len) break loopThru; // just ignore ending by \ 
         ch = sequ.charAt(i); //                          
         swSlash2: switch (ch) {
            case 't' : ch = '\t';
                       break swSlash2;
            case 'r' : ch = '\r';
                       break swSlash2;
            case 'n':  ch = '\n';
                       break swSlash2;
            case 'f':  ch = '\f';
                       break swSlash2;
            case 'u': ////  uHHHH sequence
                       if ((i + 4) > len)
                          throw new IllegalArgumentException(
                                     "\\uxxxx encoding too short");
                       int val = 0;
                       for (int j = 0; j < 4; j++) {
                          ch = sequ.charAt(++i);
                          if (ch >= 'a') ch -= 32; // a..f -> AF
                          int sv = -1;
                          if (ch >= 'A') {
                             sv = ch - ('A' - 10);
                          } else if(ch < '9' )
                             sv = ch - '0';
                             
                          if (sv < 0 || sv > 15)
                             throw new IllegalArgumentException(
                               "Malformed \\uxxxx encoding x!=0..9A..Fa..f.");
                          val = (val << 4) + sv;
                       }
                       ch = (char) val;
                       break swSlash2;
         } // swSlash2: switch 
         outBuffer.append(ch);
         // continue loopThru;
      } // loopThru:  for 
      return outBuffer.toString();
   } // unEscape(CharSequence)

/** Put a list of properties to an output stream. <br />
 *  <br />
 *  This method stores all key value pairs contained in the passed 
 *  {@link Map} {@code map} using the syntactical rules documented with
 *  {@link java.util.Properties}.{@link 
 *  java.util.Properties#store(java.io.OutputStream, java.lang.String)
 *  store(OutputStream, String)} and
 *  {@link #load(InputStream, CharSequence, Map)}.<br />
 *  <br />
 *  Hint:
 *  {@link java.util.Properties}.{@link 
 *  java.util.Properties#store(java.io.OutputStream, java.lang.String)
 *  store(OutputStream, String)} respectively an internal helper do (at least
 *  did) faultily not act as there documented.<br />
 *  <br />
 *  The output is preceded by two comment lines beginning by a sharp #.
 *  The first one is the content of the parameters {@code startComment};
 *  the second one giving the actual date by 
 *  {@link de.frame4j.time.TimeHelper}.{@link 
 *   de.frame4j.time.TimeHelper#format(CharSequence, java.time.ZonedDateTime)}.<br />
 *  <br />
 *  The key value pairs are extracted via {@link Map#entrySet()} and
 *  {@link java.util.Set#toArray()}. (This operation is synchronised with
 *  map.) Every non null entry in that array will be output in the text 
 *  form {@code key = value }. Special characters
 *  are converted before by the method
 *  {@link #doEscape(CharSequence, boolean) doEscape((CharSequence, true)})
 *  to backslash escape sequences.<br />
 *  <br />
 *  {@code encoding} is stripped from surrounding white space. If null or 
 *  (then) empty {@link ComVar#FILE_ENCODING} is used. If this or the given 
 *  {@code encoding} fails ISO-8859-1 is used.<br />
 *  <br />
 *  Hint: A null {@code startComment} will be substituted by a raw hint
 *  to the syntax &quot;Map: key = value&quot;. If {@code startComment} is
 *  multi line, each line except the first one must start by # or 
 *  &quot;smuggle in&quot; a key value pair, strictly obeying the syntax.
 *  This is NOT checked in this method.<br />
 *
 *  @param map           the properties
 *  @param output        the stream to write the properties to
 *  @param startComment  a describing comment to put at the begin
 *  @param encoding      the streams encoding (character set)
 *  @throws IOException when output problems
 *  @throws NullPointerException when output or map are null
 */
   public static void store(final Map<?, ?> map, final OutputStream output,
                            final CharSequence startComment, 
                            final CharSequence encoding)  throws IOException {
      final String enco = TextHelper.trimUq(encoding, ComVar.FILE_ENCODING);
      OutputStreamWriter osw;
      try {
         osw = new OutputStreamWriter(output, enco);
      } catch (UnsupportedEncodingException usope) {
         osw =new OutputStreamWriter(output, "ISO-8859-1");
      }   
      final BufferedWriter buWr = new BufferedWriter(osw);
      buWr.write("# "  
                    + (startComment == null ? " Map: key = value "
                      : startComment));
      buWr.newLine();
      buWr.write("# " + TimeHelper.formatRFC(SynClock.sys.setActTime(), null));
      buWr.newLine();
      final Map.Entry<?,?>[] entries =
                           (java.util.Map.Entry[]) map.entrySet().toArray();

      for (Map.Entry<?,?> e : entries) {
         if (e == null) continue;
         Object keyO = e.getKey();
         if (keyO == null) continue; // would not be necessary for PropMap
         String key = doEscape(keyO.toString(), true);
         Object valO = e.getValue();
         String val = valO == null ? " " 
                                 : doEscape(valO.toString(), true);
         buWr.write(key + "=" + val);
         buWr.newLine();
      }
      buWr.flush();
   } // store(Map, 

/** Converting special and control characters to backslash escape
 *            sequences. <br />
 *  <br />
 *  If {@code escapeSurSpace} is true, a given leading and / trailing space
 *  in {@code sequ} are replaced by &quot;&#92;&nbsp;&quot;.<br />
 *  <br />
 *  All following character values are converted to backslash escape 
 *  sequences: <br />
 *  <code>\</code>, tab, form feed, line feed and return as well as
 *   = # : and ! will be converted to<br />
 *  <code>\\</code>, <code>\t</code>, <code>\f</code>, <code>\n</code>, 
 *  <code>\r</code> respectively <code>\=</code>, <code>\#</code> and so
 *  on.<br />
 *  <br />
 *  The perhaps astonishing &quot;escaping&quot; of =, #, : and ! is 
 *  compelled by the syntax of reading properties, see 
 *  {@link #load(InputStream, CharSequence, Map)}.<br />
 *  <br />
 *  characters with a (uni) code less then 0x0020, grater than 0x00FD as well
 *  as between 0x007F and 0x00A0 will be coded as <code>&#92;uxxxx</code>,
 *  xxxx being the four digit hexadecimal Unicode number (see.
 *  {@link TextHelper#fourDigitHex(Appendable, int)}.<br />
 *  <br />
 *  @param sequ   the sequence to convert
 *  @param escapeSurSpace  true: convert starting and trailing blank (if 
 *            there) to &quot;&#92;&nbsp;&quot;
 *  @return converted sequence as String (may be empty but never null)
 */
   public static String doEscape(CharSequence sequ, boolean escapeSurSpace){
      if (sequ == null) return ComVar.EMPTY_STRING;
      final int len = sequ.length();
      if (len == 0) return ComVar.EMPTY_STRING;
      StringBuilder bastel = new StringBuilder(len / 4 + len + 12);
      char ch = sequ.charAt(0);
      if (escapeSurSpace && ch == ' ')
         bastel.append('\\');
      int i = 1;
      loopthru: while (true) {
         if (specialCharsToEscape.indexOf(ch) != -1) { // special
            bastel.append('\\');
            switch (ch) {
               case '\t' : ch = 't'; break; 
               case '\n' : ch = 'n'; break; 
               case '\r' : ch = 'r'; break; 
               case '\f' : ch = 'f'; break; 
            }
            bastel.append(ch);  // \ itself or  \ transcoded
         } else if (ch < 0x0020 || ch > 0x00FE 
                       || (ch >= 0x007F &&  ch <= 0x00A0)) { // u escape
            bastel.append("\\u");
            TextHelper.fourDigitHex(bastel, ch);
         } else { // other "normal" characters 
            bastel.append(ch);            
         }
         if (i == len) break loopthru;
         ch = sequ.charAt(i);
         if (++i == len && escapeSurSpace && ch == ' ')
            bastel.append('\\');
      } // loopthru: while (true)
     return bastel.toString();
   } // doEscape(CharSequence, boolean)

/** Positive hash code.<br />
 *  <br />
 *  Returns the {@link String}.{@link String#hashCode()} of {@code key} made
 *  non negative by cutting the (31st) sign bit off.<br />
 *  <br />
 *  The reason of being is Java's strange modulo (%) implementation having
 *  disastrous effects when naively &mdash; using the text book's algorithm
 *  &mdash; converting a hash value to a first try index by modulo 
 *  length.<br /> 
 *  <br />
 *  @param key may be empty but never null
 *  @return a  positive hash value
 */
   public static final int posHash(final String key){
      return key.hashCode()  & 0x7FFFFFFF; 
   } // posHash(String)

/** A character sequence ends with a odd number of (\) backslashes. <br />
 *  <br />
 *  This method returns true if the character sequence {@code line} ends 
 *  with one or a higher odd number of sequential \ backslashes.<br />
 *  <br />
 *  The reason of being is a syntax considering two consecutive backslashes
 *  as an escaped &quote;real&quot; one and one trailing \ backslash as 
 *  an order for a continuation line.<br />
 *  <br />
 *  This method implements this &quot;to&nbsp;be&nbsp;continued&quot;
 *  condition.<br />
 *  <br />
 *  @param line sequence to be considered
 *  @throws NullPointerException if line is null
 *  @return true: to be continued
 */
   public static boolean oddEndSlashs(CharSequence line){
      int slashCount = 0;
      int index = line.length() - 1;
      while ((index >= 0) && (line.charAt(index--) == '\\'))
         slashCount++;
      return (slashCount % 2 == 1);
   } // ungeradeEndSlashs(CharSequence)

/** A next prime number. <br />
 *  <br />
 *  This method chooses out of a sparsely populated list of prime numbers
 *  the smallest &gt;= the parameter {@code val}. One usage is getting the 
 *  next about double prime size, when enlarging / rehashing a 
 *  container.<br />
 *  <br />
 *  The returned value is in the range 71 .. 479.001.599 and will be in
 *  average the double of the parameter value. Larger parameter values will
 *  get a -1.<br />
 *  <br />
 *  @return prime  number &gt;= val in the range &lt;=479.001.599; -1 without
 */   
   public static int gtPrim(final int val){
      if (val > 479001599) return -1;
      
      int untInd = 0;
      int obrInd = primint.length-1;

      while (untInd <= obrInd) { // bin search.
          int mitInd = (untInd + obrInd) >> 1;
          int aktWert = primint[mitInd];

          if (aktWert < val)
         untInd = mitInd + 1;
          else if (aktWert > val)
         obrInd = mitInd - 1;
          else
         return aktWert; // exact match
      } // bin search.
      return primint[untInd];  // key not found.
   } // gtPrim(int)

/* * Is it language or region. <br />
 *  <br />
 *  Background: setting an object's property language or region 
 *  often &mdash; as in {@link Prop} &mdash; has 
 *  normally some (badly wanted) side effects. This helper method just 
 *  implements the condition on the key used in a lot of places.<br />
 *  <br /> 
 *  @param keyName the parameter name
 *  @return true, only if parName equals 
 *                     &quot;language&quot; or &quot;region&quot;
 *  /
   public static final boolean lanOrReg(final String keyName){
      if (keyName == null) return false;
      final int pnL = keyName.length();
      if (pnL == 8) { // may be language
         return keyName.equals("language");
      } else  if (pnL == 6) { // may be region
         return keyName.equals("region");
      }
      return false;
   } // lanOrReg(final String    xxxxxxxxxxxxxxx   out 04.05.2021  */
   
/** Get a method by signature for a class. <br />
 *  <br />
 *  <br />
 *  This method is the equivalent of 
 *  {@code cl.getMethod(name, parTypes)} except returning null instead of
 *  raising exceptions when no method object could be delivered.   
 * @param cl        the class having the method searched for (hopefully)
 * @param name      the name of the method
 * @param parTypes  the parameter types
 * @return          a fitting method or null
 */
   public static Method getMethod(final Class<?> cl, final String name,
                           Class<?>... parTypes){
     if (cl == null || name == null || name.length() == 0) return null;
     try {
       return cl.getMethod(name, parTypes);
     } catch (NoSuchMethodException e) {
       return null;
     } catch (SecurityException e) {
       return null;
     }
   } // getMethod(Class<?>, String, Class<?>...)

/** Setting an object's field (beans property). <br />
 *  <br />
 *  If {@code obj} or {@code key} is null, nothing will happen except 
 *  returning false.<br />
 *  <br />
 *  Otherwise this method (heavily) utilises introspection of the object 
 *  {@code obj} to set its property named {@code key} by the value
 *  {@code value}.<br />
 *  The only case, where no (expensive) introspection is used is if<ul>
 *  <li>{@code obj} is of type {@link AttrSettable} and</li>
 *  <li>{@link AttrSettable}.{@link AttrSettable#setAttribute(String, Object)
 *          setAttribute(key, value} does not return
 *        {@link AttrSettable#NO_KNOWN_ATTRIBUTE NO_KNOWN_ATTRIBUTE}.<li>    
 *  </ul>
 *  
 *  In all other cases the following (introspection) steps are taken:<br />
 *  If the platform or runtime forbids introspection into {@code obj} a
 *  SecurityException will happen.<br />
 *  <br />
 *  The setting of {@code obj}'s property named {@code key} by the
 *  {@code value} is effected either by accessing a public object variable
 *  or by using a public method named <code>setKey()</code>.<br />
 *  <br />
 *  In determining the setter method it is additionally evaluated, if the 
 *  parameter {@code key} may designate an indexed property according to the 
 *  rules of
 *  {@link Indexed}.{@link Indexed#make(CharSequence) make(key)}. If this is
 *  the case, with the  {@code key} shortened by the index two parameter 
 *  set methods {@code setKey(int index, ..)} are searched for.<br />
 *  <br />
 *  The setKey method is searched with the following signatures in this
 *  sequence:.<ul>
 *  <li> setKey(String) and afterwards setKey(CharSequence).<br />
 *       Does one of 
 *       this (public) methods exist, it is called as setKey(null) if
 *        {@code value} is null.</li>
 *  <li> If {@code key} designates possibly an indexed property,
 *       setKey(int, String) and afterwards  setKey(int, CharSequence).<br />
 *       Does one of this (public) methods exist, it is called as 
 *       setKey(index, null) if  {@code value} is null.</li>
 *  <li> setKey(int) and afterwards  setKey(int, int).<br /> 
 *       These methods are only searched if {@code value} is decodable 
 *       as int by ({@link TextHelper#asIntObj(CharSequence)}.</li> 
 *  <li> setKey(boolean)  and afterwards  setKey(int, boolean).<br />
 *       These methods are only searched if {@code value} is decodable 
 *       as a binary (&quot;truth&quot;) value by 
 *       {@link TextHelper#asBoolObj(CharSequence)}.</li>
 *  <li> setKey(Color) and afterwards  setKey(int, Color).<br />
 *       Is one of these methods found, it is now tried to interpret 
 *       {@code value} as colour by 
 *       {@link ColorHelper#getColor(CharSequence)}). Is that impossible an
 *       {@link IllegalArgumentException} is thrown, otherwise of cause the 
 *       setter is called.</li></ul>
 * 
 *  Implementation hint: The rationale behind the last &quot;colour&quot; 
 *  point is a) the assumption of searching one method or if possible indexed
 *  two methods by introspection is cheaper than the multi language colour 
 *  interpretation trials. And b) the assumption, that if a class provides a 
 *  setter for {@link Color} the property has to be either a colour or is 
 *  wrong. Hence the IllegalArgumentException before the search for 
 *  public variables.<br />
 *  <br />   
 *  For the corresponding method setKey() the first letter after
 *  &quot;set&quot; is always made upper case according to beans and set/get 
 *  method conventions. As a consequence two properties named (keyed) otto and
 *  Otto would both take the same setters setOtto(..). But they would not 
 *  both succeed with 
 *  {@link AttrSettable}.{@link AttrSettable#setAttribute(String, Object)
 *          setAttribute(key, value}; see above.<br />
 *  <br />
 *  The called setter may itself throw exceptions &mdash; may be because of
 *  detesting the passed {@code value}. This case if forwarded by this method
 *  as an {@link IllegalArgumentException}, too.<br />
 *  <br />
 *  If all described toils found no fitting setter method the direct access to
 *  a variable named {@code key} is tried. Preconditions:<ul>
 *  <li> The object variable is declared in the class of the object 
 *       {@code obj} and not inherited.<br />
 *       Note: The public setters mentioned above may very well be 
 *       inherited.</li>
 *  <li> The variable's type boolean, int or String (explicitly excluding
 *       {@link CharSequence} is gladly accepted for setters).</li>
 *  <li> The variable is public and neither static nor final.</li>
 *  </ul><br />
 *  The (found) variable remains unchanged, if {@code value} is null or not 
 *  interpretable according to the variable's (int or boolean) type. 
 *  No exception is raised due to non fitting types or values.<br /> 
 *  Please note that the String or CharSequence setters would have been 
 *  called with null value and that all setters might react to values
 *  unwanted by throwing exceptions.<br />
 *  <br />
 *  Hint: If {@code key} contains white spaces or otherwise does not comply
 *  to Java's rules for variable names, with all described efforts nothing 
 *  will be effected. To avoid these waste of resources and time a filtering
 *  of keys before or while looping over all properties should be done by 
 *  the caller. The method 
 *  {@link Prop#setFields(Object)} which uses this method, tries to avoid
 *  those wasteful calls.<br />
 *  <br />
 *  @param obj    the object, whose property key might be changed
 *  @param key    name of the property
 *  @param value  new value of the property
 *  @return       true if the property key could be set 
 *  @throws SecurityException if the runtime forbids introspection into obj
 *  @throws IllegalArgumentException if a called setter throws itself
 *          an exception , like for example
 *          {@link IllegalArgumentException} or a
 *          {@link java.beans.PropertyVetoException}. The exception's message
 *          is forwarded via the new {@link IllegalArgumentException}
 *  @see Prop#setFields(Object)
 */  
   static public boolean setField(final Object obj, final String key, 
                             final String value) throws SecurityException, 
                                                    IllegalArgumentException {
      if (obj == null || key == null ) return false;
      final int keyLen =  key.length();
      if (keyLen == 0) return false;  
      if (RESTEST) {
        System.out.println(" ///  TEST PMpH " + key + " = " + value 
                       + " on " + obj);
      }

      if (obj instanceof AttrSettable) { // MBean implementation cheaper than introsp.
         final int done = ((AttrSettable)obj).setAttribute(key, value);
         if (RESTEST) if (done != AttrSettable.NO_KNOWN_ATTRIBUTE) { 
           System.out.println(" ///  TEST PMpH " + key
                             + " atrSet: " + AttrSettable.retVtext(done));
         }  // test output
         if (done == AttrSettable.OK) return true;
         if (done != AttrSettable.NO_KNOWN_ATTRIBUTE) return false;
      } // MBean indirect implementation setAttribute(String, String)

      StringBuilder bastel = new StringBuilder(keyLen + 3);
      bastel.append("set").append(key);
      bastel.setCharAt(3, Character.toUpperCase(key.charAt(0)));
      final String  setMn = bastel.toString(); bastel = null;
      final Class<? extends Object> cl = obj.getClass();
    //  Object[] par1  = { value }; // new Object[1]; // exactly one parameter

      Method  setMe = getMethod(cl, setMn, String.class); // setLeProp(String)
      if (setMe == null) setMe = getMethod(cl, setMn, CharSequence.class); 
      if (setMe != null) try { // setPropName(String | CharSequence)
         setMe.invoke(obj, value);
         return true;
      } catch (Exception e) {
         if (e instanceof SecurityException)
            throw (SecurityException)e; // 14.11.00
         if (e instanceof InvocationTargetException) {  // 20.07.2004
            Throwable thr = e.getCause();
            if (thr != null) // 20.07.2004
               throw new IllegalArgumentException(thr.getMessage());
            new IllegalArgumentException(e.getMessage());
         }   // invoked Method caused Exception
      } // try catch  setEigenschaft(String)
      
      Indexed indexed = Indexed.make(key);
      String setMnK = null;
      int index = 0;
 //     Object[] par2 = null;
      if (indexed != null) { // it's indexed
         setMnK = setMn.substring(0, indexed.key.length() + 3); // 3:set
         index = indexed.index;
         indexed = null;
      } // it's indexed
      
      if (setMnK != null) { // may be indexed, try int,String / CharSequence
        setMe = getMethod(cl, setMnK, int.class, String.class);
        if (setMe == null) {
          setMe = getMethod(cl, setMnK, int.class, CharSequence.class);  
        }
        if (setMe != null) try { // setLeProp([int,] int | InCharSequence)
            setMe.invoke(obj, index, value);
            return true;
         } catch (Exception e) {
            if (e instanceof SecurityException)
               throw (SecurityException)e; // 14.11.2000
            if (e instanceof InvocationTargetException) {  // 20.07.2004
               Throwable thr = e.getCause();
               if (thr != null) // 20.07.2004
                  throw new IllegalArgumentException(thr.getMessage());
               new IllegalArgumentException(e.getMessage());
            }   // invoked Method caused Exception
          } // setEigenschaft(int, String)
      } // may be indexed
      
      Integer intInt = TextHelper.asIntObj(value);
      if (intInt != null) {  // leProp is int (or Integer)
        if (RESTEST) {
         System.out.println(" ///  TEST PMpH " + key + " = " + value + ";//i");
        }
        setMe = getMethod(cl, setMnK, int.class, int.class); // setIt(int,int)
        if (setMe == null) {
          setMe = getMethod(cl, setMnK, int.class, Integer.class); 
        }  // setIt(int,Integer)
        if (setMe != null) try { // setLeProp(int, int | Integer)
          if (RESTEST) System.out.println(" ///  TEST PMpH found " + setMe);
          setMe.invoke(obj, index, intInt);
          return true;
       } catch (Exception e) {
          if (e instanceof SecurityException)
             throw (SecurityException)e; // 14.11.00
          if (e instanceof InvocationTargetException) {  // 20.07.2004
             Throwable thr = e.getCause();
             if (thr != null) // 20.07.2004
                throw new IllegalArgumentException(thr.getMessage());
             new IllegalArgumentException(e.getMessage());
          }   // invoked Method caused Exception
        } // setLeProp(int, int|Integer)

        setMe = getMethod(cl, setMn, int.class); //setLeProp(int)
        if (setMe == null) { 
          setMe = getMethod(cl, setMn, Integer.class); // setLeProp(Integer)
        }
        if (setMe != null) try { // setLeProp(int | Integer)
          if (RESTEST) System.out.println(" ///  TEST PMpH found " + setMe);
            setMe.invoke(obj, intInt);   ///   par1); TEST 04.05.2021
            return true;
         } catch (Exception e) {
            if (e instanceof SecurityException)
               throw (SecurityException)e; // 14.11.00
            if (e instanceof InvocationTargetException) {  // 20.07.2004
               Throwable thr = e.getCause();
               if (thr != null) // 20.07.2004
                  throw new IllegalArgumentException(thr.getMessage());
               new IllegalArgumentException(e.getMessage());
            }   // invoked Method caused Exception
         }
         if (RESTEST) {
           System.out.println(" ///  TEST PMpH  no method " 
                                  + setMn + "(I) in " + cl);
         }
      } // may be int or Integer is not to be interpreted
 
      Boolean boolBool = TextHelper.asBoolObj(value); // interpret as bool
      if (boolBool != null) { // is bool
        setMe = getMethod(cl, setMnK, int.class, boolean.class); 
        if (setMe == null) {
          setMe = getMethod(cl, setMnK, int.class, Boolean.class); 
        }  // setIt(int,Boolean)
        if (setMe != null) try { // setLeProp(int, boolean | Boolean)
          setMe.invoke(obj, index, boolBool);
          return true;
        } catch (Exception e) {
          if (e instanceof SecurityException)
            throw (SecurityException)e; // 14.11.00
          if (e instanceof InvocationTargetException) {  // 20.07.2004
            Throwable thr = e.getCause();
            if (thr != null) // 20.07.2004
              throw new IllegalArgumentException(thr.getMessage());
            new IllegalArgumentException(e.getMessage());
          }   // invoked Method caused Exception
        } // setLeProp(int, boolean | Boolean)
        
        setMe = getMethod(cl, setMn, boolean.class); 
        if (setMe == null) {
          setMe = getMethod(cl, setMn, Boolean.class); 
        }  // setIt(int,Boolean)
        if (setMe != null) try { // setLeProp(boolean | Boolean)
          setMe.invoke(obj, boolBool);
          return true;
        } catch (Exception e) {
          if (e instanceof SecurityException)
            throw (SecurityException)e; // 14.11.00
          if (e instanceof InvocationTargetException) {  // 20.07.2004
            Throwable thr = e.getCause();
            if (thr != null) // 20.07.2004
              throw new IllegalArgumentException(thr.getMessage());
            new IllegalArgumentException(e.getMessage());
          }   // invoked Method caused Exception
        } // setLeProp(int, boolean | Boolean)
        
        try { // setEigenschaft(boolean)
            setMe.invoke(obj, boolBool);
            return true;
         } catch (Exception e) {
            if (e instanceof SecurityException)
               throw (SecurityException)e; // 14.11.00
            if (e instanceof InvocationTargetException) {  // 20.07.2004
               Throwable thr = e.getCause();
               if (thr != null) // 20.07.2004
                  throw new IllegalArgumentException(thr.getMessage());
               new IllegalArgumentException(e.getMessage());
            }   // invoked Method caused Exception
         } // catch
         // can be interpreted as boolean but has no setter
      } // can be interpreted as Boolean

      if (setMnK != null) try { // setEigenschaft(int Color)
         setMe = cl.getMethod(setMnK, int.class, Color.class);
         Color set = ColorHelper.getColor(value);
         if (set != null) try {
            setMe.invoke(obj, index, set);
            return true;
         } catch (InvocationTargetException e) {
            Throwable thr = e.getCause();
            if (thr != null) // 20.07.2004
               throw new IllegalArgumentException(thr.getMessage());
            new IllegalArgumentException(e.getMessage());
         } catch (Exception e2) { } // do nothing on other exceptions.
      } catch (NoSuchMethodException e1) {} // do nothing, go ahead

      //-----   Direct setting of variables   ----------------------------

      try {
         Field field = cl.getDeclaredField(key);
         if (RESTEST) {
             System.out.println(" ///  TEST PMpH " 
                               + key + " field " + field + " ;");
         }
         int modi = field.getModifiers();
         if ((modi & PUSTFI) != Modifier.PUBLIC) {
           if (RESTEST) System.out.println(" ///  TEST PMpH " 
                               + key + " static or final or not public!!");
           return false;
         } // not public non final not static
         Class<?> type  = field.getType();
         if (type == Boolean.TYPE) {
            if (boolBool != null) {
               field.setBoolean(obj, boolBool == Boolean.TRUE);
               return true;
            }
            return false;
         }
         if (type == Integer.TYPE) {
              if (intInt != null) {
               field.setInt(obj, intInt.intValue());
               if (RESTEST) {
                    System.out.println(" ///  TEST PMpH " + key
                                        + " I " + intInt.intValue() + " ;");
               }
               return true;
            }
            return false;
         }
         if (type == String.class) {
            if (value != null) {
               field.set(obj, value);
               return true;
            }
            return false;
         }
      } catch (Exception e) {
         if (e instanceof SecurityException)
            throw (SecurityException)e; // 14.11.00
      }
      if (RESTEST) System.out.println(" ///  TEST PMpH " + key + " = " + value
                              + "; // failed !!!");
   
      return false;  // all toils failed
   } // setField(Object 
 
} // class PropMapHelper