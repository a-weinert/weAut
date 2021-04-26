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
package de.frame4j.io;

import java.io.File;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.io.FilenameFilter;
import java.io.FileFilter;

import de.frame4j.util.ComVar;
import de.frame4j.text.FastStringSet;
import de.frame4j.util.MinDoc;
import de.frame4j.util.PropMap;
import de.frame4j.text.TextHelper;
import de.frame4j.time.TimeHelper;


/** <b>Criteria for choosing files</b>. <br />
 *  <br />
 *  Objects of this class are a set of encompassing file criteria. These 
 *  relate to <ul>
 *  <li> the name (exact match or a wildcard expression)</li>
 *  <li> names to exclude for the path (single or list)</li>
 *  <li> the type (as part of the name or as list of types)</li>
 *  <li> the species (file, directory or both)</li>
 *  <li> the time of the last modification (as range)</li>
 *  <li> the length  (exact or as range)</li>
 *  </ul>
 *  All object fields defining those criteria are properties with the 
 *  conventional (JavaBeans schema) setters and getters. Multiple properties
 *  can be set at once by a {@link PropMap} object.<br />
 *  <br />
 *  With those criteria set object of this class are quite complex
 *  {@link java.io.FilenameFilter FilenameFilter}. The description of the
 *  criteria in the documentation below related directly to their effect on 
 *  the {@link java.io.FilenameFilter java.io.FilenameFilter}; see also
 *  {@link #accept accept()}.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 1997 - 2003, 2005 Albrecht Weinert<br />
 *  <br /> 
 */
 // so far    V00.00 (11:54 03.05.1998) :  inner class Criteria, new
 //           V00.22 (11.04.2003 17:56) :  Eclipse improvements
 //           V02.03 (21.05.2003 16:54) :  toString()
 //           V02.06 (08.08.2003 16:09) :  time date criteria clarified
 //           V02.12 (12.07.2005 09:13) :  wildEqual improved (TextHelper)
 //           V.135+ (06.01.2016) : FileHelper

@MinDoc(
   copyright = "Copyright  1997 - 2005, 2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 38 $",
   lastModified   = "$Date: 2021-04-16 19:38:01 +0200 (Fr, 16 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use when files and directories are to be selected",  
   purpose = "file and directory criteria"
) public class FileCriteria implements Cloneable, Serializable, 
                    FilenameFilter, FileFilter, DirectoryStream.Filter<File>{

/** Version number for serialising.  */
   static final long serialVersionUID = 260153007400201L;
//                                      magic /Id./maMi

/** The name criterion (with wild cards). <br />
 *  <br />
 *  If the character sequence {@code wildName} is not null, it is the (one)
 *  criterion on the file's or directory's name. Default is null.<br />
 *  <br />
 *  If {@link #isIgnoreCase ignoreCase} is true (default), the matching of 
 *  names to {@code wildName} will be not case sensitive.<br />
 *  <br />
 *  @see TextHelper#wildEqual TextHelper.WildEqual()
 *  @see #ignoreCase
 *  @return the wild (card) name if set
 */ 
   public final String getWildName(){ return wildName; }

/** The name criterion (with wild cards). <br />
 *  <br />
 *  @see #getWildName()
 */
   protected String  wildName;

/** Set the name criterion (with wild cards). <br />
 *  <br />
 *  Surrounding white spaces will be stripped. An empty character sequence 
 *  will be taken as null (no wild name criterion).<br />
 *  <br />
 *  Plus signs + will be transformed to asterisks *.<br /> 
 *  *.* will be taken as null (no wild name criterion, as accept all).<br />
 *  <br />
 *  If {@code wildName} contains list separators (normally ; respectively : 
 *  at Linux) this method will set {@link #getTypes() types} instead of 
 *  {@link #getWildName() wildName}.<br />
 *  <br />
 *  @see TextHelper#wildEqual TextHelper.WildEqual()
 *  @see #wildName
 *  @see #getWildName
 *  @param wildName  the name criterion (with wild cards)
 */
   public void setWildName(String wildName){
      this.wildName = null;
      if (wildName == null || wildName.isEmpty()) return;
      wildName = wildName.trim().replace('+','*');
      if (wildName.isEmpty()) return;
      if ("*.*".equals(wildName)) return;
      int lsp  = wildName.indexOf(File.pathSeparatorChar);
      if (lsp < 0 && File.pathSeparatorChar != ';')
         lsp  = wildName.indexOf(';');
      if (lsp >= 0) {
         setTypes(wildName);
       } else
         this.wildName = wildName;
   } // setWildName(String)


/** Ignoring case in names. <br />
 *  <br />
 *  This property says, whether in wildName and excludeNames case should 
 *  matter.<br /> 
 *  <br />
 *  default: ! {@link ComVar
 *                ComVar}.{@link ComVar#NOT_WINDOWS NOT_WINDOWS}<br />
 *  @see #wildName 
 *  @return true if case is ignored in names   
 */
   public final boolean isIgnoreCase(){ return ignoreCase; }

/** Ignore case in names. <br />
 *  <br />
 *  @see #isIgnoreCase    
 */
   protected boolean ignoreCase = ! ComVar.NOT_WINDOWS;

/** Ignore case in names. <br />
 *  <br />
 *  @see #isIgnoreCase() 
 *  @param ignoreCase   true if case shall be ignored in names   
 */
   public void setIgnoreCase(boolean ignoreCase){
        this.ignoreCase = ignoreCase;
   } // setIgnoreCase(boolean)


/** Exclude criteria for names (a list without wild cards). <br />
 *  <br />
 *  If the FastStringSet excludeNames is not empty, it is an 
 *  exclude criterion for the file or directory names. If one of the names to
 *  exclude is found to be a part of the regarded path, that will be 
 *  rejected.<br />
 *  <br />
 *  excludeNames can be set as a list of names separated by list separator 
 *  (; or : depending on the operating system), example:<br />
 *  &quot;D:\Programme; WinNT;&quot; <br />
 *  <br />
 *  default: null<br />
 *  <br />
 *  @see #addExcludeNames(CharSequence)
 *  @see #isIgnoreCase() igorecase
 *  @see #getWildName()  wildName
 *  @return the exclude exclude criteria for names (a list without wild cards)
 */ 
   public final FastStringSet getExcludeNames(){ return  excludeNames; }

/** Exclude criteria for names (list without wild cards). <br />
 *  <br />
 *  Implementation hint: set only in constructor and in {@link #clone()}
 *  &mdash; treat as if final.<br />
 *  <br />
 *  @see #getExcludeNames()
 */
   FastStringSet excludeNames;

/** Set exclude criteria for names (list without wild cards). <br />
 *  <br />
 *  The list of exclude criteria for names will be cleared. Afterwards its
 *  like {@link #addExcludeNames(CharSequence)}.<br />
 *  <br />
 *  Example: &quot;.svn; .cvs; build; .settings&quot; <br />
 *  <br />
 *  @see #getExcludeNames()
 *  @see #addExcludeNames(CharSequence)
 *  @param excludeNames exclude criteria for names 
 *                      (a ; separated list without wild cards)
 */
   public void setExcludeNames(final CharSequence excludeNames){
      this.excludeNames.clear();
      addExcludeNames(excludeNames);
   } // setExcludeNames(CharSequence)

/** Add exclude criteria for names (to the list). <br />
 *  <br />
 *  Surrounding white spaces will be stripped. An empty sequence will be taken
 *  as null. Plus signs + will be transformed to asterisk * even if wildcards
 *  won't be regarded in excludeNames.<br />
 *  A list / path  separator {@link ComVar}.{@link ComVar#PS PS} will be 
 *  used to separate {@code #getExcludeNames() excludeNames} to token.<br />
 *  Every token / entry will be surrounded by
 *  {@link ComVar}.{@link ComVar#FS FS} if not yet so and then added 
 *  to the list of exclude criteria for names.<br />
 *  <br />
 *  If no entry / token is to be added to the  
 *  {@link #getExcludeNames() exclude criteria} they will be left 
 *  unmodified.<br />
 *  @param excludeNames exclude criteria for names to be added
 */
   public void addExcludeNames(final CharSequence excludeNames){
      String namesToExclude = TextHelper.trimUq(excludeNames, null);
      if (namesToExclude == null) return;
      
      namesToExclude = namesToExclude.replace('+','*');
      if (ComVar.FS != '/') {
         namesToExclude.replace('/', ComVar.FS);
      } else {
         namesToExclude.replace('\\', ComVar.FS);
      }
      final int len = namesToExclude.length();
      int colonPos = namesToExclude.indexOf(ComVar.PS);
      if (colonPos < 0)  colonPos = len;
      for (int startPos = 0; startPos < len; ) {
         String actName = namesToExclude.substring(startPos, colonPos).trim();
         startPos = colonPos + 1;
         colonPos = namesToExclude.indexOf(ComVar.PS, startPos);
         if (colonPos < 0) colonPos = len;
         int aLen = actName.length();
         if (aLen < 1) continue;
         if (actName.charAt(0) == ComVar.FS) {
            if (aLen < 2) continue;
         } else {
            actName = ComVar.FS + actName;
            ++aLen;
         }
         if (actName.charAt(aLen - 1) == ComVar.FS) {
            if (aLen < 3) continue;
         } else {
            actName =  actName + ComVar.FS;
         }
         this.excludeNames.add(actName);
      }
   } // addExcludeNames(CharSequence) 


/** The file type criterion. <br />
 *  <br />
 *  If the set types is not empty it is the criterion on the
 *  file type or extension. default is empty.<br />
 *  <br />
 *  The entries in the set will have the form &quot;.aha&quot;, 
 *  &quot;.ht*&quot;, &quot;.java&quot; and the like.<br />. They will be
 *  in lower case.<br />
 *  <br />
 *  @see #setTypes(CharSequence)
 *  @return the file type criterion
 */
   public final FastStringSet getTypes(){ return types; }

/** The file type criterion. <br />
 *  <br />
 *  Implementation hint: set only in constructor and in {@link #clone()}
 *  &mdash; treat as if final.<br />
 *  <br />
 *  @see #getTypes()
 */
   FastStringSet types;

/** Set the file type criterion. <br />
 *  <br />
 *  The list of file types will be cleared. Then its like 
 *  {@link #addTypes(CharSequence)}.<br />
 *  @see #getTypes()
 *  @param types the file type criterion
 */
   public void setTypes(final CharSequence types){
      this.types.setTypes(types);
   } // setTypes(CharSequence) 


/** Add to the file type criteria. <br />
 *  <br />
 *  Surrounding white spaces will be stripped. An empty sequence will be taken
 *  as null. <br />
 *  <br />
 *  Multiple types to be accepted may be given as a semicolon separated
 *  list. Case does not matter for the file types (a Window only concept
 *  foreign to Linux).<br />
 *  <br />
 *  Entries may freely have forms like "typ", "*.typ" or "+.typ"; plus 
 *  characters + will be transformed to asterisks *. (A leading *. will bear
 *  no information anyway.)<br />
 *  <br />
 *  @see #getTypes()
 *  @see FastStringSet#addTypes(CharSequence)
 *  @see FastStringSet#setTypes(CharSequence)
 *  @param types additional file type criteria 
 */
   public void addTypes(final CharSequence types){
      if (types == null) return;
      this.types.addTypes(types);
   } // addTypes(CharSequence) 



/** Acceptance of (real) files. <br />
 *  <br />
 *  Files respectively directories are accepted only if allowFile respectively
 *  allowDir is true (the default).<br />
 *  @return true if (real) files are to be accepted
 */
   public final boolean isAllowFile(){ return allowFile; }

/** Acceptance of (real) files. <br />
 *  <br />
 *  @see #isAllowFile
 */
   protected boolean allowFile = true;

/** Acceptance of (real) files. <br />
 *  <br />
 *  @see #isAllowDir
 *  @see #isAllowFile
 *  @param allowFile states if (real) files are to be accepted
 */
   public void setAllowFile(boolean allowFile){ this.allowFile = allowFile; }

/** Acceptance of directories. <br />
 *  <br />
 *  Files respectively directories are accepted only if allowFile respectively
 *  allowDir is true (the default).<br />
 *  <br />
 *  @see #isAllowFile
 *  @return true if directories are to be accepted
 */
   public final boolean isAllowDir(){ return allowDir; }

/** Acceptance of directories. <br />
 *  <br />
 *  @see #isAllowDir()
 */
   protected boolean allowDir = true;

/** Acceptance of directories. <br />
 *  <br />
 *  @see #isAllowDir
 *  @see #isAllowFile
 *  @param allowDir states if directories are to be accepted
 */
   public void setAllowDir(boolean allowDir){ this.allowDir = allowDir; }

//-------------------------------------------------------------------

/** The time criterion, minimal time (&quot;since&quot;). <br />
 *  <br />
 *  Files are accepted only it the time of their last modification is
 *  &gt;= minTime .<br />
 *  <br />
 *  The criterion is only used if it is &gt;= 0 (= begin of year 1970) an if 
 *  it is &lt;= {@link #getMaxTime maxTime} .<br />
 *  <br />
 *  Implementation (history) hint: This class and its predecessor assumes 
 *  since many years that it will never have to handle files written the last
 *  time before 1970. In all its usages that assumption was never challenged, 
 *  but may  nevertheless be questionable.<br />
 *  <br />
 *  default: -1 (criterion not in use)<br />
 *  <br />
 *  @see #getMaxTime
 *  @see #isTimeSet
 *  @return the minimal (from) time
 */
   public final long getMinTime(){ return minTime; }

/** The time criterion, minimal time (&quot;since&quot;).  <br />
 *  <br />
 *  @see #getMinTime
 */
   protected long    minTime = -1L;  //     0L == Year 1970

 /** The time criterion, minimal time (&quot;since&quot;).  <br />
 *  <br />
 *  @see #getMaxTime
 *  @see #getMinTime
 *  @param minTime the minimal (from) time
 */
   public void setMinTime(final long minTime){
        this.minTime = minTime >= 0 ? minTime : -1L;
   }

 /** Set the time criterion, minimal time (&quot;since&quot;) by String. <br />
 *  <br />
 *  If minTime is empty or not parsable as time {@link #minTime} will be set
 *  to -1 .<br />
 *  <br />
 *  For accepted time formats see 
 *  {@link de.frame4j.time.TimeHelper#parse(CharSequence, boolean) TimeHelper.parse()}.<br />
 *  <br />
 *  A missing time of day will be interpreted as 00:00:00.000 .<br />
 *  <br />
 *  @param minTime the minimal (from) time  
 */
   public void setMinTime(final CharSequence minTime){
      final String s = TextHelper.trimUq(minTime, null);
      long erg = -1L;
      if (s != null) try {
         erg = TimeHelper.parse(s, false);
      } catch (IllegalArgumentException iae) {} // ignore false formats
      this.minTime = erg;
   } 


/** The time criterion, maximal time (&quot;until&quot;). <br />
 *  <br />
 *  Files are accepted only it the time of their last modification is
 *  &lt;= maxTime .<br />
 *  <br />
 *  The criterion is only used if it is &gt;= 0 (= begin of year 1970) an if 
 *  it is &gt;= {@link #getMinTime() minTime}.<br />
 *  <br />
 *  default: -1 (criterion not in use)<br />
 *  <br />
 *  @see #getMinTime
 *  @see #isTimeSet
 *  @return the maximum (until) time
 */
   public final long getMaxTime(){ return maxTime; }

/** The time criterion, maximal time (&quot;until&quot;). <br />
 *  <br />
 *  @see #getMaxTime                 */
   protected long    maxTime    = -1L;      // max < min : irrelevant    

 /** The time criterion, maximal time (&quot;until&quot;). <br />
 *  <br />
 *  @see #maxTime
 *  @see #minTime
 *  @param maxTime the maximum (until) time
 */
   public void setMaxTime(final long maxTime){
      this.maxTime = maxTime >= 0 ? maxTime : -1L;
   } // setMaxTime(long)

 /** Set the time criterion, maximal time (&quot;until&quot;) by String. <br />
 *  <br />
 *  If minTime is empty or not parsable as time {@link #minTime} will be set
 *  to -1 .<br />
 *  <br />
 *  For accepted time formats see 
 {@link de.frame4j.time.TimeHelper#parse(CharSequence, boolean) TimeHelper.parse()}.<br />
 *  <br />
 *  A missing time of day will be interpreted as 23:59:59.999 .<br />
 *  <br />
 *  The last point says, if using the same date without time of day for 
 *  both times, it is the whole day.<br />
 *  <br />
 *  @see #setMinTime setMinTime()
 *  @param maxTime the maximum (until) time
 */
   public void setMaxTime(final CharSequence maxTime){
      final String s = TextHelper.trimUq(maxTime, null);
      long erg = -1L;
      if (s != null) try {
         erg = TimeHelper.parse(s, true);
      } catch (IllegalArgumentException iae) {}
      this.maxTime = erg;
   } 

/** One time criterion is set. <br />
 *  <br />
 *  This method returns true, if at least one of the time criteria
 *  {@link #getMinTime minTime} or {@link #getMaxTime maxTime} is effectual.
 *  See {@link #getMinTime() there}.<br />
 *  <br />
 *  @return true if time criteria are effectual
 */
   public final boolean isTimeSet(){
      if (minTime >= 0L) { // minTime effectual if ...
          return maxTime < 0L || maxTime >= minTime;
      } // minTime 
      return maxTime >= 0;
   } // isTimeSet()

/** The time criterion (minTime) is set. <br />
 *  <br />
 *  This method returns true, if at least one of the time criteria
 *  {@link #getMinTime minTime} is effectual.<br />
 *  <br />
 *  @return true if the minimum time criterion is effectual
 */
   public final boolean isMinTimeSet(){
      if (minTime < 0) return false;
      return maxTime < 0 || maxTime >= minTime;
   }

/** The time criterion (max) is set. <br />
 *  <br />
 *  This method returns true, if at least one of the time criteria
 *  {@link #getMaxTime maxTime} is effectual.<br />
 *  <br />
 *  @return true if the maximum (until) time criterion is effectual
 */
   public final boolean isMaxTimeSet(){
      return maxTime >= 0 && maxTime >= minTime;
   }

//----------------------------------------------------------------------

/** The length criterion (minimal size). <br />
 *  <br />
 *  @see #getMinLen
 */
   protected long  minLen;                         // 0 Bytes

/** The length criterion (minimal size). <br />
 *  <br />
 *  Files will only be accepted if for their length len<br /> &nbsp; &nbsp;
 *      minLen  &lt;= len &lt;= maxLen <br />
 *  <br />    
 *  minLen's default is 0 and maxLens's default is -1.<br />
 *  The above length criterion will not be effectual, if <br /> &nbsp; &nbsp;
 *     maxLen &lt; minLen (default) &nbsp; or &nbsp; minLen &lt; 0 <br />
 *  @see #getMaxLen() maxLen
 *  @return the minimum length (in bytes) criterion
 */
   public final long getMinLen(){ return minLen; }

/** The length criterion (minimal size).<br />
 *  <br />
 *  @see #getMaxLen()
 *  @see #getMinLen()
 *  @param minLen the minimum length (in bytes) criterion
 */
   public void setMinLen(final long minLen){ this.minLen = minLen; }

/** The length criterion (maximal size). <br />
 *  <br />
 *  @see #getMinLen() 
 */
   protected long maxLen = -1L;                // - = irrelevant

/** The length criterion (maximal size).  <br />
 *  <br />
 *  Default : -1 (no effect)
 *  @see #getMinLen 
 *  @return the maximum length (in bytes) criterion
 */
   public long getMaxLen(){ return maxLen; }

/** Set the length criterion (maximal size).  <br />
 *  <br />
 *  @see #getMinLen 
 *  @param maxLen the maximum length (in bytes) criterion
 */
   public void setMaxLen(long maxLen){ this.maxLen = maxLen; }

/** The length criteria (minimal and maximal size) are given.  <br />
 *  <br />
 *  This method returns true if the length criteria are effectual.<br />
 *  <br />
 *  @see #getMinLen 
 *  @return true if the length criteria are effectual
 */
   public boolean isLenSet(){ return minLen >= 0 && maxLen >= minLen; }

//----------------------------------------------------

/** Set the criteria according to a PropMap. <br />
 *  <br />
 *  This objects criteria will be set according to those properties if
 *  they are contained in the supplied {@link PropMap} object prop:<ul>
 *  <li> minLen</li>
 *  <li> maxLen</li>
 *  <li> minTime respectively since </li>
 *  <li> maxTime respectively til </li>
 *  <li> allowDir</li>
 *  <li> allowFile</li>
 *  <li> types</li>
 *  <li> ignoreCase</li>
 *  <li> wildName</li>
 *  <li> excludeNames</li>
 *  </ul>
 *  If a property is not given in prop (or prop is null) nothing happens in
 *  each case.<br />
 *  <br />
 *  If prefix is not empty it will be prepended to the searched property's
 *  name transforming their first character to upper case. Example:<br />
 *  If the prefix &quot;files&quot; the property minLen would be looked up
 *  as filesMinLen.<br />
 *  <br />
 *  @param prefix an optional prefix for the keys to search for
 *  @param prop   the properties map to take the criteria from
 */ 
   public void set(String prefix, final PropMap prop){
      if (prop == null) return;
      prefix = TextHelper.trimUq(prefix, null);
      
      String key = prefix == null ? "minLen" : prefix + "MinLen";
      minLen = prop.getLong(key, minLen);
      key = prefix == null ? "maxLen" : prefix + "MaxLen";
      maxLen = prop.getLong(key, maxLen);

      key = prefix == null ? "minTime" : prefix + "MinTime";
      String value = prop.getProperty(key, null);
      if (value == null) {
         key   = prefix == null ? "since" : prefix + "Since";
         value = prop.getProperty(key, null);
      }
      if (value != null) setMinTime(value);

      key = prefix == null ? "maxTime" : prefix + "MaxTime";
      value = prop.getProperty(key, null);
      if (value == null) {
         key   = prefix == null ? "til" : prefix + "Til";
         value = prop.getProperty(key, null);
      }
      if (value != null) setMaxTime(value);

      key = prefix == null ? "allowFile" : prefix + "AllowFile";
      allowFile = prop.getBoolean(key, allowFile);
      key = prefix == null ? "allowDir" : prefix + "AllowDir";
      allowDir = prop.getBoolean(key, allowDir);
      key = prefix == null ? "ignoreCase" : prefix + "IgnoreCase";
      ignoreCase = prop.getBoolean(key, ignoreCase);

      key = prefix == null ? "wildName" : prefix + "WildName";
      value = prop.getProperty(key, null);
      if (value != null) setWildName(value);

      key = prefix == null ? "excludeNames" : prefix + "ExcludeNames";
      value = prop.getProperty(key, null);
      if (value != null) setExcludeNames(value);

      key = prefix == null ? "types" : prefix + "Types";
      value = prop.getProperty(key, null);
      if (value != null) setTypes(value);
    } // set(String, Prop)

/** The sole constructor. <br />
 *  <br />
 *  A FileCriteria object is made all of its properties having their specific
 *  default values. As a {@link java.io.FilenameFilter java.io.FilenameFilter}
 *  the object in this state will accept every file and every directory.<br />
 *  <br />
 *  @see #accept(File, String) accept()
 */
   public FileCriteria(){
      excludeNames = new FastStringSet(5);
      types = new FastStringSet(5);
   } // FileCriteria()


/** Check, if a named file (or directory) fulfils the criteria. <br />
 *  <br />
 *  This method implements the interface 
 *  {@link java.io.FilenameFilter java.io.FilenameFilter}.<br />
 *  <br />
 *  @param dirf  the directory in which the file resides (may be null)
 *  @param name  the (pure) name of the file (null or empty returns false)
 *  @return true if dir/name is acceptable by this FileCriteria
 */
   @Override public boolean accept(File dirf, String name){
      // System.out.println("  ///  TEST accept(" + dirf + ", " + name);
      if (name == null || name.isEmpty()) return false;
      if (wildName != null) { // Name criterion
        if (!TextHelper.wildEqual(wildName, name, ignoreCase)) return false;
      }  // Name criterion
      if (!types.isEmpty()) { // types criterion
        final String myType = FileHelper.getType(name);
        if (!types.contains(myType, true, true)) return false;
      }  // types criterion
      File dD = new File(dirf, name);
      if ( (!allowDir && dD.isDirectory()) 
                               || (!allowFile && dD.isFile()) ) return false;
      if  (isTimeSet()) {
         long ftime = dD.lastModified();
       if ((minTime >= 0 && ftime < minTime) 
                          || (maxTime > 0 &&  ftime > maxTime)) return false;
      }
      if  (minLen >= 0L && maxLen >= minLen) {
         long fLen = dD.length();
         if (fLen < minLen || fLen > maxLen)  return false;
      }  
      if (excludeNames != null) {
         String thePath = ComVar.FS + dD.getPath() + ComVar.FS;
         for (String act : excludeNames.asArray()) {
            if (act == null) break; // end of list
            // act is surrounded by FS; hence optimistic is OK
            if (TextHelper.indexOfOpt(thePath, act, 0, ignoreCase) >=0 )
               return false; // if act is contained in thePath that's out
         }
      } // excludeNames
      //System.out.println("  ///  TEST accepted  " + dirf + ", " + name);
      return true;
   } // accept(File, String)


/** Check, if a file (or directory) fulfils the criteria. <br />
 *  <br />
 *  This method implements the function defined by 
 *  {@link javax.swing.filechooser.FileFilter 
 FileFilter}.{@link javax.swing.filechooser.FileFilter#accept(File) accept()}.
 *  <br />
 *  @param file the file or directory 
 *  @return true if the file is acceptable by this FileCriteria
 */
   @Override public boolean accept(File file){
      if (file == null) return false;
      String name = file.getName();
      //System.out.println("  ///  TEST accept(" + file + ",  " + name);
      if (name == null || name.isEmpty()) return false;
      if (wildName != null) { // Name criterion
        if (!TextHelper.wildEqual(wildName, name, ignoreCase)) return false;
      }  // Name criterion
      if (!types.isEmpty()) { // types criterion
        final String myType = FileHelper.getType(name);
        if (!types.contains(myType, true, true)) return false;
      }  // types criterion
      if ( (!allowDir && file.isDirectory()) 
                            || (!allowFile && file.isFile()) ) return false;
      if  (isTimeSet()) {
         long ftime = file.lastModified();
       if ((minTime >= 0 && ftime < minTime) 
                          || (maxTime > 0 &&  ftime > maxTime)) return false;
      }
      if  (minLen >= 0L && maxLen >= minLen) {
         long fLen = file.length();
         if (fLen < minLen || fLen > maxLen)  return false;
      }  
      if (excludeNames != null) {
         String thePath = ComVar.FS + file.getPath() + ComVar.FS;
         for (String act : excludeNames.asArray()) {
            if (act == null) break; // end of list
            // act is surrounded by FS; hence optimistic is OK
            if (TextHelper.indexOfOpt(thePath, act, 0, ignoreCase) >=0 )
               return false; // if act is contained in thePath that's out
         }
      } // excludeNames
     // System.out.println("  ///  TEST accepted   "  + name);
      return true;
   } // accept(File)
   
   
/** Get a javax.swing.filechooser.FileFilter. <br />
 *  <br />
 *  This method returns a FileFilter governed by this {@link FileCriteria}
 *  object.<br />
 *  Hint this extra method (pitifully) is necessary as 
 {@link javax.swing.filechooser.FileFilter javax.swing.filechooser.FileFilter}
 *  is neither an interface nor does declare any interfaces not even the
 *  partly identical {@link java.io.FileFilter java.io.FileFilter}.<br />
 *  <br />
 *  @return a FileFilter for Swing
 */
   public final javax.swing.filechooser.FileFilter getFileFilter(){
      return new javax.swing.filechooser.FileFilter() {
         @Override public boolean accept(File file) {
            return FileCriteria.this.accept(file);
         }

         @Override public String getDescription() {
            if (wildName != null) return wildName;
            if (types != null && ! types.isEmpty()) {
               return types.csL(null).toString();
            }
            return "*.*";
         }
         
      };
   } // getFileFilter() 
   
//------------------------------------------------------------------------   


/** A copy of this object. <br />
 *  <br />
 *  A copy of this object will be made and returned.<br />
 *  Hint: The copy is deep, as {@link #getExcludeNames() excludeNames} and
 *  {@link #getTypes() types} are cloned in the process.<br />
 */ 
   @Override public FileCriteria clone(){
      FileCriteria ret = null;
      try { ret = (FileCriteria)super.clone(); } catch (Exception e){}
      ret.excludeNames = new FastStringSet(this.excludeNames);
      ret.types = new FastStringSet(this.types);
      return ret;
   }

/** Compare to other FileCriteria object. <br />
 *  <br />
 *  The comparison is not made field by field, but regarding this objects
 *  effect as file filter ({@link FilenameFilter}).<br />
 *  true is returned if the other object is of the same class and would 
 *  act in the same way.<br />
 */
   @Override public final boolean equals(Object other){
      if (other == null) return false;
      if (other == this) return true;
      if (other.getClass() != this.getClass()) return false;

      FileCriteria or = (FileCriteria)other;
      if ((allowDir != or.allowDir) || (allowFile != or.allowFile)
           || (ignoreCase != or.ignoreCase)) return false;
      boolean ts = isLenSet();
      if (ts != or.isLenSet()) return false;
      if (ts) {
         if (minLen  != or.minLen || maxLen  != or.maxLen)
            return false;        
      }

      ts = isTimeSet();
      if (ts != or.isTimeSet()) return false;
      if (ts) {  // time criterion
         if (or.minTime != minTime && or.minTime >= 0 && minTime >= 0)
            return false;
         if (or.maxTime != maxTime && or.maxTime >= 0 && maxTime >= 0)
            return false;
       } 
      if (types != null) {
         if (or.types == null || !(types.equals(or.types))) return false;
      } else if (or.types != null) return false;

      if (wildName != null) {
         if (or.wildName == null 
                   || !wildName.equals(or.wildName)) return false;
      } else if (or.wildName != null) return false;

      return excludeNames.equals(or.excludeNames);
   } // equals(Object)


/** Hashcode for FileCriteria. <br />
 *  <br />
 *  The value is computed using most fields that have an effect on file 
 *  filtering ({@link FilenameFilter}).<br />
 *  @see #equals
 */
   @Override public final int hashCode(){
      int ret = allowDir ? 17 * 31 : 17;
      ret = ret * 31 + (ignoreCase ? 1 : 0);
      if (isLenSet()){
         ret = (ret * 31 + (int) (minLen ^ (minLen >>> 32)) * 31) +
                                       (int)  (maxLen ^ (maxLen >>> 32));
      } else ret = ret * 31;

      if (isTimeSet()){
         ret = (ret * 31 + (int) (minTime ^ (minTime >>> 32)) * 31) +
                                      (int)  (maxTime ^ (maxTime >>> 32));
      } else ret = ret * 31;      

      if (types != null) {
         ret = ret * 31 + types.hashCode();
      } else ret = ret * 31; 

      if (wildName != null) {
         ret = ret * 31 + wildName.hashCode();
      } else ret = ret * 31; 

      return ret;
   } // hashCode()

/** State as String. <br />
 *  <br />
 *  @return a multi-line text describing this object's file criteria
 */
   @Override public String toString(){
      StringBuilder dest = new StringBuilder(500);
      dest.append("de.frame4j.io.FileCriteria  :  {");
 
      dest.append("\n  ** allowDir     = ").append(allowDir);
      dest.append("\n  ** allowFile    = ").append(allowFile);
      dest.append("\n  ** ignoreCase   = ").append(ignoreCase);
      dest.append("\n  ** wildName     = ").append(wildName);

      dest.append("\n  ** ");
      TextHelper.format(dest, "types",  types.asArray(), types.size());
      dest.append("\n  ** ");
      TextHelper.format(dest, "excludeNames",
                            excludeNames.asArray(), excludeNames.size());

      dest.append("\n  ** time         = ");
      if (isTimeSet()) {
         boolean minTs = isMinTimeSet();
         boolean maxTs = !minTs || isMaxTimeSet();
         if (!maxTs) dest.append(" >= ");
         if (!minTs) {
            dest.append(" <= ");
         }  else  dest.append(minTime);
         if (minTs && maxTs ) dest.append(" .. ");
         if (maxTs) dest.append(maxTime);
         dest.append(" ms");
      }  else {
         dest.append("any");
      }
      dest.append("\n  ** length       = ");
      if (minLen < 0L || maxLen < minLen) { // no length criterion
         dest.append("any");
      }else {
         dest.append(minLen).append(" .. ").append(maxLen).append(" bytes");
      }
      dest.append("\n  **  } // de.frame4j.io.FileCriteria \n");
      return dest.toString();
   } // toString



/** Parse / separate a file specification into path and wildcard name. <br />
 *  <br />
 *  The call is equivalent to parse(path, null).<br />
 *  <br />
 *  @param   path   the file denomination to start with  (+ becomes *)
 *  @return         the parent / directory path part of path
 */
   public String parse(String path){ return parse(path, null); }


/** Parse / separate a file specification into path and wildcard name.  <br />
 *  <br />
 *  This method transforms most comfortably an intuitive String notation
 *  into a (parent) path and file name or type criteria for this 
 *  {@link FileCriteria} object. Parameters {@code path} like<ul>
 *  <li>\institute\plan&#92;uhu.html</li>
 *  <li>\institute\plan\+.html</li>
 *  <li>\institute\plan\*.html</li>
 *  <li>\institute\plan\html;</li>
 *  <li>\institute\plan\.html;.xml</li>
 *  <li>\institute\plan\.html;.xml;css</li>
 *  </ul>
 *  will be treated correctly.<br />
 *  <br />
 *  As a first step {@link #getWildName() wildName} will be set null and 
 *  {@link #getTypes() types} will be set to {@code defTypes}.<br />
 *  <br />
 *  The parameter {@code path} will be processed bay  
 {@link TextHelper}.{@link TextHelper#makeFName(CharSequence, String) makeFNL()}.<br />
 *  The result will be split at the last file separator
 *  ({@link ComVar}.{@link ComVar#FS FS} \ or /) or at an colon :  
 *  if {@link ComVar}.{@link ComVar#PS PS} is not a colon.<br />
 *  This split, if feasible, may have provided a (parent) path and a name 
 *  part. If no name part was found {@link #getWildName() wildName} and
 *  {@link #getTypes() types} will keep their values from step one 
 *  above.<br />
 *  <br />
 *  If there is a name part found, that will become either wildName or types
 *  of this FileCriteria object, taking "." or "*.*" as null.<br />
 *  It will become types instead of wildName if it contains at least one
 *  list separator character {@link ComVar}.{@link ComVar#PS PS}.<br />
 *  <br />
 *  The (parent) path part will be returned by this method. If none was
 *  found (in above split) .\ respectively ./ (the actual directory's 
 *  placeholder) is returned.<br />
 *  <br />
 *  Hint: After this method only / at most one of wildName or types will be 
 *  not null.<br />
 *  <br />
 *  Hint 2: It might be thought that the effect of ...\*.html and ...\html; 
 *  (see above), i.e. a wildcard name ending in .html and a type list with
 *  just one type html, is the same. This is true in most cases, but not 
 *  always. A filename &quot;uhu.html&quot; is, obviously, accepted by both
 *  settings (hence the belief in equivalence). But 
 *  &quot;uhu.29.08.2009.html&quot; will only be accepted by the type list
 *  entry.<br />
 *  <br />
 *  <br />
 *  @param   path   the file denomination to start with  (+ becomes *)
 *  @param defTypes the default for type criteria
 *  @return         the parent / directory path part of path
 *  @see #wildName
 *  @see #getTypes() types
 */
   public String parse(String path, final CharSequence defTypes){
      wildName    = null; 
      //  types.clear();
      if (defTypes != null) setTypes(defTypes);
      String pFs = "." + ComVar.FS;
      String ret = path = TextHelper.makeFNL(path);
      if (path == null) return pFs;
      int ql = path.length();

      int lsp = path.lastIndexOf(ComVar.FS);
      if (lsp < 0 && ComVar.PS != ':') lsp = path.lastIndexOf(':'); 
      if ( lsp < 0 ) {
         ret = pFs;
         wildName = path;
         // types.clear();
         // setTypes(defTypes);

      } else  if ( lsp < (ql - 1) ) {
         ret  = path.substring(0, lsp + 1);
         wildName = path.substring(lsp + 1, ql); 
         // types.clear();
         // setTypes(defTypes);
      } 
      final boolean all =  "*.*".equals(wildName);
      if (all || defTypes != null) setTypes(defTypes);
      
      
      if (".".equals(wildName) || all ) {
         wildName = null;
      }  else if ("..".equals(wildName)) {
         ret +=  '.' + pFs;
         wildName = null;
      }
      
      if (wildName != null && (wildName.length() > 2) &&
                                         (wildName.indexOf(';') >= 0)) {
         setTypes(wildName);
         wildName = null;
      } 
      
     // System.out.println("\n   ///  TEST parse (" + path + ", " + defTypes
       //                       +  " = " + ret + "\n   ///  " + toString());
      return ret;
   } // parse
   
} // class  FileCriteria (08.08.2003)
