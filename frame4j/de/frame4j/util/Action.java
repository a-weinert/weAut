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

import de.frame4j.text.TextHelper;


/** <b>An Action: Action code (int) plus parameter (int)</b>. <br />
 *  <br />
 *  Objects of this class subsume an action-({@link #code}) and an
 *  action-({@link #value}).<br />
 *  <br />
 *  An {@link Action}, for example, would be <br /> &nbsp; &nbsp; 
 *    &quot;Set day of week&quot; {@code (= code = 3)} to &quot;Monday&quot; 
 *    {@code (= value = 2)}.<br />
 *  <br />
 *  Such an Action object always has a keyword or a list of keywords. 
 *  Preferably the list is multilingual featuring at least English and 
 *  German and hopefully also Italian, French, Spanish
 *  Portuguese and Dutch. Any keyword in the list would choose the action.
 *  The exemplary Action above, would have the keywords:<br />  &nbsp; &nbsp;
 *      Montag  Monday  Lundi lunedí  lunedi lunes<br />  &nbsp; &nbsp;
 *      (That's de, en, fr, 2*it and sp.)<br />
 *  <br />
 *  The method ({@link #selectedBy(CharSequence, boolean) selectedBy()}) can 
 *  ascertain, if a given String hits one of the keywords fully (Monday) or 
 *  as an abbreviation (Mo). Case can optionally be ignored (mO, LuNd).<br />
 *  <br />
 *  Purpose in life is to enrich parsers by the human friendly utilisation 
 *  of keywords (like in {@link de.frame4j.time.TimeHelper 
 *  TimeHelper}.{@link 
 *  de.frame4j.time.TimeHelper#parse(CharSequence) parse()}).<br />
 *  <br />
 *  Some action codes are pre-defined here (could be overridden by applying
 *  classes).<br />
 *  <br style="clear:both;" />
 *  <table cols="4" border="1" cellpadding="3" cellspacing="0" 
 *  style="width:90%;" style="text-align:center;" summary="Abbreviations">
 *  <tr><th style="width:18%;">Abbreviation</th><th style="width:10%;">code</th>
 *     <th style="width:30%;"> Meaning </th>
 *     <th style="width:57%;"> &nbsp;Value&nbsp; </th></tr>
 *  <tr><td>NOP</td><td style="text-align:center;">0</td><td>Do nothing</td>
 *               <td>any,&nbsp; no meaning.</td></tr>
 *  <tr><td>SWD</td><td style="text-align:center;">3</td><td>Set day of week</td>
 *                         <td>0:&nbsp;Sunday .. 6:&nbsp;Saturday</td></tr>
 *  <tr><td>SMON</td><td style="text-align:center;">4</td><td>Set Month</td>
 *                          <td>1:&nbsp;January .. 12:&nbsp;December</td></tr>
 *  <tr><td>STZO</td><td style="text-align:center;">5</td>
 *              <td>Set&nbsp;time zone offset<br /> in minutes</td>
 *                  <td>-12*60 .. +12*60<br /> should be n*30</td></tr>
 *  <tr><td>STIM</td><td style="text-align:center;">6</td>
 *                 <td>Set time of day or the 0/12 hour add on<br />
 *                      hour:minute:second.ms</td>
 *                          <td>0: Midnight 0:0:0<br />
 *                              1: AM, ante meridiem)<br />
 *                              2: Noon 12:00:00<br />
 *                              3: Afternoon (PM, post meridiem, 
 *                                  = add 12 h to clock reading)<br />
 *                              4: End of day 23:59:59,999 (1 ms before next 
 *                                                             day)</td></tr>
 *  <tr><td>SDAY</td><td style="text-align:center;">7</td><td>Set day relative to today</td>
 *                          <td>0: today, +1:&nbsp;tomorrow<br />
 *                              -1: yesterday, -2:&nbsp;day before ...</td></tr>
 *  <tr><td>SDAT</td><td style="text-align:center;">8</td><td>Set complete time / date <br />
 *                                day.month.year hour:minute:second.ms</td>
 *                          <td>0: now</td></tr>
 *  <tr><td>SRATE</td><td style="text-align:center;">11</td><td>Set a rate / frequency</td>
 *                          <td>1 : every second,<br />
 *                          2: every minute,<br />
 *                          3: every hour, ..
 *                          </td></tr>
 *  <tr><td>SVERBOS</td><td style="text-align:center;">13</td><td>Set a level of 
 *          verbosity for logs or reports</td>
 *                          <td>{@link AppHelper#DEBUG} ..
 *                          {@link AppHelper#SILENT}
 *                          </td></tr></table>
 *                          
 *  <br style="clear:both;">
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2003, 2009 &nbsp; Albrecht Weinert  
 *  @see de.frame4j.text.TextHelper
 *  @see de.frame4j.time.TimeHelper#parse(CharSequence)
 *  @see de.frame4j.graf.ColorHelper#getColor(CharSequence)
 *  @see de.frame4j.time.TimeHelper#TIME_CHOOSE
 * 
 *  @version  $Revision: 33 $ ($Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $)
 */
 // until now  V02.01 (17.06.2003 17:00) : new (for ZeitHelper)
 //            V.o48+ (19.01.2009 11:37) : ported (Frame4J) translation start
 //            V.o53+ (26.01.2009 14:30) : debug (ambigAbbr. hides full match)

@MinDoc(
   copyright = "Copyright 2004, 2009  A. Weinert",
   author = "Albrecht Weinert",
   version = "V.$Revision: 33 $",
   lastModified = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage = "use for human friendly parsers",  
   purpose = "translate (polyglotly) keywords to unique integer codes"
) public class Action implements Cloneable {
    
/** Action code: Don't do anything. <br />
 *  <br />
 *  Value: {@value}
 */      
   public static final int NOP = 0;

/** Action code: Set day of week. <br />
 *  <br />
 *  {@link #code} = 0 : Sunday, 1 : Monday, ... 6 : Saturday.<br />
 *  Value: {@value}
 */   
   public static final int SWD = 3;

/** Action code: Set month. <br />
 *  <br />
 *  {@link #code} = 1 : January ... 12 : December.<br />
 *  Value: {@value}
 */   
   public static final int SMON = 4;

/** Action code: Set time zone offset. <br />
 *  <br />
 *  {@link #code} =  Offset in minutes  -12*60..+12*60 in 60 (30,15) steps<br />
 *  Value: {@value}
 */   
   public static final int STZO = 5;

/** Action code: Set time of day. <br />
 *  <br />
 *  {@link #code} = 0 : midnight, 1: AM, 2 : noon, 3: PM, 4: end of day.
 */   
   public static final int STOD = 6;

/** Action code: Set the day relative to today. <br />
 *  <br />
 *  {@link #code} = 0 : today, -1 : yesterday, +1 : tomorrow, and so on..<br />
 *  Value: {@value}
 */   
   public static final int SDAY = 7;

/** Action code: Set complete date. <br />
 *  <br />
 *  {@link #code} = 0 : (right) now<br />
 *  Value: {@value}
 */   
   public static final int SDAT = 8;


/** Action code: Rate. <br />
 *  <br />
 *  {@link #code} = 1 : every second, 2: every minute, 3 : every hour,
 *   4 : daily, 5 : weekly.<br />
 *  Value: {@value}
 */   
   public static final int SRATE = 11;


/** Action code: Report level. <br />
 *  <br />
 *  {@link #code} = 300 (debug) .. 1000 (silent / severe).<br />
 *  Value: {@value}<br />
 *  <br />
 *  @see AppHelper#getVerbosityAsString(int)
 */   
   public static final int SVERBOS = 13;


/** Action code: Select colour. <br />
 *  <br />
 *  {@link #code} = 0xFF0000 : red (e.g.); any of  0xrrggbb.<br />
 *  Value: {@value}
 */   
   public static final int SCOLOR = 32;


/** Action code. <br />
 */   
   public final int code;

   
/** Actions parameter (value). <br />
 */ 
   public final int value;

   
/** Compare to other Action. <br /> 
 *  <br />
 *  Two action with same {@link #code} and {@link #value} are considered
 *  as equal.<br />
 *  <br />
 */     
   @Override public final boolean equals(Object other) {
      if (!(other instanceof Action)) return false;
      if (other == this) return true;
      return ((Action)other).code == this.code 
            && ((Action)other).value == this.value;
   } // equals(Object 

/** Hash code. <br /> 
 *  <br />
 *  Depends on both {@link #code} and {@link #value} (fitting 
 *  {@link #equals equals()}).
 */     
   @Override public final int hashCode() {
      return value << 15 ^ code;
   } // hashCode()


/** State as String. <br /> 
 *  <br />
 *  Gives &quot;Action({@link #code}, {@link #value}): all 
 *  {@link #getKey(int) keys}&quot;.
 */     
   @Override public String toString(){
      StringBuilder bastel = new StringBuilder(50).append("Action(");
      bastel.append(code).append(", ").append(value).append(") : ");
      bastel.append(anzKeys == 0 ?" < no keys >" : keys[0]);
      for (int i = 1; i < anzKeys; ++i) {
         bastel.append(", ").append(keys[i]);
      }
      return bastel.toString();
   } // toString() 

/** Copy this Action. <br /> 
 *  <br />
 *  As Action objects are immutable, this method returns the object itself
 *  (this).<br />
 *  <br />
 *  @return this immutable object 
 */
   @Override public final Object clone(){ return this; }
   
/** The keywords. <br />
 */
   final String[] keys;
   
/** Number of keywords. */
   public final int anzKeys;
   
/** Get one of the keywords (by index). <br />
 *  <br />
 *  One of the keywords is returned.<br />
 *  The index range is 1..{@link #anzKeys number} and an index may be given
 *  negative (absolute
 *  value is used); compare {@link #selectedBy selectedBy()}.<br />
 *  <br />
 *  Hint: Regard the 1 based numbering.<br />
 *  <br />
 *  @param number number of key + or - 1..{@link #anzKeys}
 *  @return the key if abs(number) is within range, else null
 */
   public String getKey(int number){
      if (number < 0) number = -number;
      if (number == 0 || number > anzKeys) return null;
      return keys[number -1];
   } //  getKey(int)
   
           
/** Common Constructor (without any checks). <br />
 *  <br />
 *  This constructor accepts all parameters without checks and uses / stores
 *  the array <code>keys</code> as is, i.e without copying it.<br />
 *  <br />
 *  This extreme confidentialness is seldom meaningful. It is used here 
 *  as usually no single Action objects generated  out of the blue by a 
 *  programmer. Usually large arrays of Action by trusted / tested code,
 *  to be used for
 *  {@link #select(Action[], Filter, CharSequence, boolean)}.<br />
 *  <br />
 *  Examples of such action lists are 
 *  {@link de.frame4j.time.TimeHelper#TIME_CHOOSE}, for human friendly time 
 *  parsers or {@link de.frame4j.graf.ColorVal#COLOR_CHOOSE} for the same 
 *  with colours.<br />
 *  <br /> 
 *  @param code   action code
 *  @param value  action parameter, value
 *  @param keys   all keywords meaning (code, value).
 */   
   public Action(int code, int value, String[] keys) {
      this.value = value;
      this.code  = code;
      this.keys  = keys;
      anzKeys    = keys == null ? 0 : keys.length;
   } // Action(2int,String[])

//-----------------------------------------------------------------------

/** Can this action be chosen by key. <br />
 *  <br />
 *  It is checked, if parameter key fits or abbreviates one of this 
 *  action's keywords.<br >
 *  <br />
 *  @param key null or shorter than 2 (excluding an end dot) returns 0
 *  @param ignoreCase true means case insensitive matching
 *  @return 0 : no match, <br />
 *     &gt; 0 : full match (= + list number / index of keyword)<br />
 *     &lt; 0 : match as abbreviation (= -index of keyword)<br /> 
 *  @see TextHelper#startsWith(CharSequence, CharSequence, boolean)  TextHelper.startsWith()
 */
   public int selectedBy(CharSequence key, boolean ignoreCase){
      if (key == null) return 0;
      int keyLen = key.length();
      if (keyLen < 2) return 0;
      if (key.charAt(keyLen -1) == '.') { // away with abbreviation dot
         --keyLen;
         if (keyLen < 2) return 0;
         key = key.subSequence(0, keyLen);
      } // // away with abbreviation dot
      for (int i = 0; i < anzKeys; ++i) {
         String theKey = keys[i];
         if (theKey == null) continue;
         int theKeyLen = theKey.length();
         if (keyLen > theKeyLen) continue;
         if (TextHelper.startsWith(theKey, key, ignoreCase)) {
            return keyLen == theKeyLen ? i + 1 : -1 -i;
         }
      } // for
      return 0;
   } // selectedBy(CharSequence

/** Find an Action from a list according to key. <br />
 *  <br />
 *  This method returns an Action from a <code>list</code> (an array) of 
 *  {@link Action}s, if that {@link Action}is selected by the 
 *  <code>key</code> and null otherwise.<br />
 *  <br />
 *  The first matching {@link Action}, that matches <code>key</code> directly
 *  and completely, is returned. Other {@link Action}s found, that would match
 *  <code>key</code> as an abbreviation, are ignored. (The first complete
 *  match ends the search; complete matches are not checked for 
 *  ambiguousness.) If <code>ignoreCase</code> is true, the matching is case
 *  insensitive.<br />
 *  <br />
 *  If an action is found, that is matched by key as an abbreviation, it is
 *  returned, if and only if that abbreviation (match) is unambiguous
 *  (and if no complete match is found thereafter).<br />
 *  <br />
 *  Abbreviated unambiguously means: There is no other {@link Action} in list,
 *  not {@link #equals(Object) equal} to the fist found, that matches key
 *  as an abbreviation.<br />
 *  <br />
 *  In the whole process {@link Action}s rejected by the optional 
 *  <code>filter</code> are ignored (as if not on list).<br />
 *  <br />
 *  @param list        the list of possible {@link Action}s to match key to
 *  @param filter      an optional filter, to select a subset from list. null
 *                     accepts all {link {@link Action}s from list. 
 *  @param key         the choice; if key ends with a dot (.) that is removed; 
 *                     if key is (then) shorter than 2, null is returned
 *  @param ignoreCase  true: match non regarding case
 *  @return            unambiguously matching {@link Action} object or null
 *  @see de.frame4j.time.TimeHelper#parseDuration(CharSequence)
 *  @see TextHelper#startsWith(CharSequence, CharSequence, boolean)
 *  @see Filter
 *  @see #makeCodeFilter(int)
 */   
   public static Action select(Action[] list, Filter filter,
                 CharSequence key, boolean ignoreCase){
      if (key == null || list == null || list.length == 0) return null;
      int keyLen = key.length();
      if (keyLen < 2) return null;
      if (key.charAt(keyLen -1) == '.') { // away with abbreviation dot
         --keyLen;
         if (keyLen < 2) return null;
         key = key.subSequence(0, keyLen);
      } // away with abbreviation dot
      Action abbrevFound = null;
      boolean abFoUnique = true;
     
      listLoop: for (Action theAct:  list){
         if (theAct == null) continue listLoop;
         if (filter != null && !filter.accept(theAct)) continue listLoop;
         int sel = theAct.selectedBy(key, ignoreCase);
         if (sel > 0) return theAct; // direct full match: take it
         if (sel < 0 ) { // abbreviated match
            if (abbrevFound == null) { // memorise first abbreviated match
               abbrevFound = theAct;
               continue listLoop;   
            }  // memorise first
            if (!theAct.equals(abbrevFound)) abFoUnique = false; // ambiguous  
         } // abbreviated match
      } // listLoop 
      
      return abFoUnique ? abbrevFound : null;                 
   } //  select(Action[] 

/** A filter for Actions by code. <br />
 *  <br />
 *  This method produces and returns a filter that accepts non null
 *  actions, that have {@link Action#code} <code>code</code>.<br />
 *  <br />   
 *  @param code the action code
 *  @return the filter
 */
   public static Filter makeCodeFilter(final int code){
      return new Filter(){
         @Override public boolean accept(Action action) {
            if (action == null) return false;
            return action.code == code;
         }   
      };
   } // makeCodeFilter(int)

/** A filter for (all) Actions. <br />
 *  <br />
 *  This is a filter that accepts (all) non null actions.<br />
 *  <br />   
 */
   public static final Filter ALL = new Filter();
   
/** <b>A filter for Actions</b>. <br />
 *  <br />
 *  This type is also the base class for more sophisticated filters.<br />
 *  Extending classes just overwrite {@link #accept(Action)}. In all 
 *  implementations {@link #accept(Action) accept(null)} must return 
 *  false.<br />
 *  <br /> 
 *  @see Action
 *  @see Action#makeCodeFilter(int)
 *  @see Action#select(Action[], Filter, CharSequence, boolean)
 */
   @MinDoc(
      copyright = "Copyright 2004, 2009  A. Weinert",
      author    = "Albrecht Weinert",
      version   = "see enclosing class Action",
      lastModified   = "see enclosing class Action",
      lastModifiedBy = "see enclosing class Action",
      usage   = "use to accept or reject an Action on base of its properties",  
      purpose = "filter a subset out of Actions"
   ) public static class Filter implements Cloneable {
      
/** The Action filter. <br />
 *  <br />
 *  This implementation accepts every non null <code>action</code>.<br />
 *  <br />
 *  To be overridden in inheriting classes.<br />
 *  <br />
 *  @param action the Action to be checked for acceptance
 *  @return true if action is not null 
 */
      public boolean accept(Action action){ return action != null; }

/** Make an Action Filter, that accepts all non null Actions. <br />
 *  <br />
 *  As this basic Filter is immutable one (singleton) instance would
 *  serve this purpose. Use the filter {@link #ALL Action.All} for that
 *  purpose.<br />
 */
      protected Filter(){ super(); }
      
   }  //  class Filter (29.01.2009)                  
 
} // class Action (18.06.2003, 19.01.2009)