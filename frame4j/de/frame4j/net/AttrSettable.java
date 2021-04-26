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
package de.frame4j.net;

import de.frame4j.util.ComVar;
import de.frame4j.util.PropMapHelper;

/** <b>DynamicMBean - indirect implementation</b>. <br />
 *  <br />
 *  This interface just describes an indirect or helper implementation for
 *  (dynamic) MBean attribute setters. It may be used in initialisation and 
 *  other places, where the direct usage of DynamicMBean (or its 
 *  implementation) would be overkill.<br />
 *  <br />
 *  Copyright 2009 Albrecht Weinert<br />
 *  <br />
 *  @see PropMapHelper#setField(Object, String, String)
 *  @author A. Weinert
 *  @version "V.$Revision: 39 $" ($Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $")
 */
public interface AttrSettable {
   
/** Return value for {@link #setAttribute(String, Object) setAttribute()}. <br />
 *  <br />
 *  OK: attribute was set to the value specified.<br />    
 */
   public final int OK = 0;

/** Return value for {@link #setAttribute(String, Object) setAttribute()}. <br />
 *  <br />
 *  No attribute was specified.<br />    
 */
   public final int NO_ATTRIBUTE = 1;

/** Return value for {@link #setAttribute(String, Object) setAttribute()}. <br />
 *  <br />
 *  No value was specified and null is illegal as value.<br />  
 *  This case may also be signalled by {@link #ILLEGAL_TYPE} or by 
 *  {@link #ILLEGAL_VALUE}.<br />
 */
   public final int NO_VALUE = 3;

/** Return value for {@link #setAttribute(String, Object) setAttribute()}. <br />
 *  <br />
 *  The type of the value specified, cannot be used. This should scarcely
 *  happen (by contract of {@link #setAttribute(String, Object)}) if the 
 *  value parameter is a character sequence.<br />    
 */
   public final int ILLEGAL_TYPE = 4;

/** Return value for {@link #setAttribute(String, Object) setAttribute()}. <br />
 *  <br />
 *  The value specified is illegal for that attribute. This may also be 
 *  returned if a setter which {@link #setAttribute(String, Object)} delegates
 *  to throws an exception.<br />    
 */
   public final int ILLEGAL_VALUE = 5;
   
     
/** Return value for {@link #setAttribute(String, Object) setAttribute()}. <br />
 *  <br />
 *  The attribute specified is not known / implemented in method called.<br />    
 */
   public final int NO_KNOWN_ATTRIBUTE = 6;
   
/** Mapping return values to text. */
   String[] retVtext = {"OK", "no attribute", ComVar.EMPTY_STRING, "no value", 
                 "illegal type", "illegal value", "no known attribute",
                  ComVar.EMPTY_STRING};
   
/** Mapping return values to text. <br />
 *  <br />
 *  This method is for debugging.
 */
   public static String retVtext(final int ret) {
     if (ret < 1 || ret > 6) return  ComVar.EMPTY_STRING;
     return retVtext[ret];  
   } // retVtext(int)
   
/** Set a named attribute or property. <br />
 *  <br />
 *  This method implements the settings of a (sub) set of properties.
 *  The implementation may be used do do the real work in for in a 
 *  DynamicMBean's implementation attribute setters.<br />
 *  <br />
 *  The preferred type for value is {@link CharSequence} or derived. It is
 *  most strongly recommended that the type {@link String} is always accepted
 *  for any settable attribute / property.<br />
 *  <br />
 *  The value {@link #NO_KNOWN_ATTRIBUTE} is returned, if name and 
 *  value are given (non null / empty), but this method just knows nothing
 *  about a property {@code name}. This (plus perhaps {@link #NO_VALUE} if 
 *  other setters might accept null) is the only return value where
 *  further delegation to other setters makes any sense.<br />
 *  <br />
 *  Hint 1 for implementation (w/o delegation):
 *  <pre><code>  &#64;Override public int setAttribute(final String name, final Object value){
 *     if (name == null || name.isEmpty()) return NO_ATTRIBUTE;
 *     final boolean isNull = value == null;
 *     final Class&lt;? extends Object&gt;  cl = isNull ? null : value.getClass();
 *     final boolean isStringVal = !isNull &amp;&amp; cl == java.lang.String.class;
 *     // do own job; return 0 (OK) on set attribute
 *     // return failure on implemented attributes, but wrong value or type            
 *     return NO_KNOWN_ATTRIBUTE; // not implemented here (try other setters)
 *  } // setAttribute(String, Object)
 *  </code></pre>
 *  Hint 2 for implementation (with prior delegation to like above):
 *  <pre><code>  &#64;SuppressWarnings("null")
 *  &#64;Override public int setAttribute(final String name, final Object value) {
 *     final int ret = super.setAttribute(name, value); // (1)
 *     if (ret != NO_KNOWN_ATTRIBUTE) return ret; // all done by (1) 
 *     final boolean isNull = value == null;
 *     final Class&lt;? extends Object&gt;  cl = isNull ? null : value.getClass();
 *     final boolean isStringVal = !isNull &amp;&amp; cl == java.lang.String.class;
 *     /// rest as above
 *  </code></pre>
 *  
 *  @param name the name of the property to be set; null, empty and start with
 *         &lt;&nbsp;&apos;a&apos; will be rejected with
 *         {@link #NO_ATTRIBUTE NO_ATTRIBUTE}
 *  @param value the new value for the named property<br /> &nbsp; &nbsp;
 *         depending on the implementation and the named property  any
 *         object state and type including null might be accepted 
 *         (with {@link #OK OK}) or rejected (with
 *          {@link #NO_VALUE NO_VALUE},&nbsp; 
 *         {@link #ILLEGAL_TYPE ILLEGAL_TYPE}&nbsp;or&nbsp; 
 *         {@link #ILLEGAL_VALUE ILLEGAL_VALUE}) 
 *
 *  @return 0 = {@link #OK OK}, &nbsp; {@link #NO_ATTRIBUTE NO_ATTRIBUTE}, 
 *         &nbsp; 
 *         {@link #NO_KNOWN_ATTRIBUTE NO_KNOWN_ATTRIBUTE}, &nbsp; 
 *         {@link #NO_VALUE NO_VALUE},  &nbsp; 
 *         {@link #ILLEGAL_TYPE ILLEGAL_TYPE} &nbsp;or&nbsp;
 *         {@link #ILLEGAL_VALUE ILLEGAL_VALUE}
 */
   public abstract int setAttribute(final String name, final Object value);

} // interface AttrSettable 