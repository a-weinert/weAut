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

import java.lang.ref.SoftReference;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import de.frame4j.text.TextHelper;

/** <b>Language specifics for applications</b>. <br />
 *  <br />
 *  One {@link AppLangMap} object encompasses for one language the basic set 
 *  of catch words and commonplace phrases including output patterns. All 
 *  values can be got by a language independent set of keys and partly
 *  indices. Switching the language is done by changing the {@link AppLangMap}
 *  object in use.<br />
 *  <br />
 *  The common keyword set is partitioned in a basic standard set and in 
 *  special special keys. Standard set values like month's names, weekdays and
 *  so on can be got by keyword and hence hashing but also directly (and
 *  faster) by predefined indices and methods.<br />
 *  <br />
 *  By inheritance an {@link AppLangMap} object is a {@link PropMap} (and 
 *  hence a {@link java.util.Map}) featuring all possibilities of the parent
 *  classes, like adding more key-value-pairs as 
 *  {@link de.frame4j.util.PropMapHelper.Entry}s). Compared to the parent
 *  classes' possibilities the standard entries mentioned can't be 
 *  removed.<br />
 *  <br />
 *  As a {@link java.util.Map} an {@link AppLangMap} object by a given key, 
 *  like e.g. &quot;jan&quot; provides a language specific value, like here
 *  &quot;gennaio&quot; for it etc. To get the January in the object's 
 *  language one can use the method {@link #month(int) month(1)} also.<br />
 *  <br />
 *  For any language (like de, en, fr, es, pt, nl, da) there will be exactly
 *  one {@link AppLangMap} object, making it a singleton with regard to the 
 *  language supported. Each one provides at least the the following basic
 *  set of entries<ol>
 *  <li> months' names [1..12] (jan, feb, mar ...), </li>
 *  <li> weekdays' names [0-Su..6-Sa,7-Su] (su, mo, tu, ..), </li>
 *  <li> language denotations, </li>
 *  <li> common words, cliches and pattern for
 *       {@link TextHelper#messageFormat(StringBuilder, CharSequence, Object) 
 *       TextHelper.messageFormat(..)}, </li>
 *  <li> denotations for durations and time keeping conventions like day light
 *       saving etc., </li>
 *  <li> denotations for time zones, MEZ, WEZ, EEZ, CET and so on with
 *       daylight saving variant in each case GMT and UCT (wet west utc).</li>
 *  </ol>
 *  For those standard values, except for category 4) there is a complete and
 *  an abbreviated version in each case. The short form's key is the long
 *  one's by appending _S: sat, sat_S, mesz, mesz_S, etc.<br />
 *  <br />
 *  By convention all {@link AppLangMap} keys are lower case only. For the 
 *  standard values' keys this is irrelevant insofar as they are recognised in 
 *  any case combination. Providing the keys in lower case is recommended and
 *  faster: sat_s or Sat_S e.g. would get both  Sáb for the Portuguese  (pt)
 *  AppLangMap (Saturday abbreviated).<br />
 *  <br />
 *  The set of methods that directly fetch words, cliches, names etc. do so
 *  in their {@link AppLangMap}'s language. If nothing is found there it will
 *  be searched for in {@link #MAP_en} and {@link #MAP_de} as substitutes.
 *  Those both must always be comprehensive. Feedback on gaps or faults
 *  are welcome.<br />
 *  <br />
 *  This class is their own demo application. This is, by the way, 
 *  implemented as an anonymous inner {@link App} extension. Execute it 
 *  by<br />
 *  <br /> &nbsp; &nbsp; 
 *   java &nbsp; de.frame4j.util.AppLangMap &nbsp; [-options] <br />
 *  <br />
 *  to see a list of all implemented common keys. An option -de, -en or -fr, 
 *  or simply the two letter language code, like it (for Italian), shows
 *  which keys are supported for that language (if anyway).<br />
 *  <br />
 *  Compared to {@link java.util.Locale}, {@link java.text.DateFormatSymbols}
 *  etc. this class brings higher comfort and better performance. This is
 *  restricted to languages of Western European origin (in the widest sense)
 *  or those that could live with ISO8859-1 / Latin1, -15 or similar.<br />
 *  <br />
 *  Hints for application programmers:<br />
 *  An inheritor of {@link App} gets by &nbsp;
 *  {@link AppLangMap}{@link #getUMap() .getUMap()} &nbsp; quite simply the 
 *  {@link AppLangMap} fitting the platforms language or that set by standard
 *  start parameters. That's all.<br />
 *  Most of Frame4J's formatting methods (of class
 *  {@link de.frame4j.time.TimeHelper} e.g.) use essentially the same 
 *  proceeding.<br />
 *  <br />
 *  Implementation hint: To this class belongs (as integral part) a
 *  .properties file, named <a href="doc-files/AppLangMap.properties"
 *  target="_top">AppLangMap.properties</a>. Suggestions, error reports and
 *  additional translations are very welcome.<br />
 *  <br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2004, 2009 &nbsp; Albrecht Weinert.<br />
 *  <br /> 
 *  @author   Albrecht Weinert
 *  @version  $Revision: 56 $ ($Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $)
 *
 *  @see de.frame4j
 *  @see de.frame4j.time.TimeHelper
 *  @see de.frame4j.util.App
 *  @see de.frame4j.util
 *  @see de.frame4j.text.TextHelper
 */
 // so far    V02.01 (05.10.2004 13:28) :  new, alpha
 //           V02.02 (11.10.2004 07:46) :  time zones , /**
 //           V02.07 (09.12.2004 18:24) :  more cliches added, /**
 //           V02.08 (10.12.2004 18:24) :  .properties generalised
 //           V02.15 (27.02.2005 13:48) :  /**
 //           V02.16 (17.05.2005 07:54) :  jarUnsigned.
 //           V.134+ (02.11.2015) : Class<?>

@MinDoc(
   copyright = "Copyright 2004, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 56 $",
   lastModified   = "$Date: 2021-06-28 12:11:29 +0200 (Mo, 28 Jun 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage = 
  "use ApplangMap as a (zoo of) PropMap(s) for language specific properties  or\n"
 + "\n  java de.frame4j.util.ApplangMap [options] langCode",  
   purpose = "properties for (inter) nationalisation"
) public class AppLangMap extends PropMap { 

/** Version number for serialising.  */
   static final long serialVersionUID = 260153008100201L;
//                                      magic /Id./maMi
   
   static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
/** The common lock for reads. <br />
 *  <br />
 *  This is only to guard multi thread reads against critical writes 
 *  guarded by {@link #wLock}.<br />
 */
   protected static final ReentrantReadWriteLock.ReadLock rLock =
                                                           rwLock.readLock();
/** The common lock for writes. <br />
 *  <br />
 *  This is only to guard writes that must not interfere with multi thread 
 *  reads (guarded by {@link #rLock}).<br />
 */
   protected static final ReentrantReadWriteLock.WriteLock wLock =
                                                           rwLock.writeLock();

/** Cache size for valueUL() look up. <br />
*  <br />
*  Value: <code>{@value}</code>
*  @see #valueUL(CharSequence, String)
*  @see #valueUL(CharSequence)
*/    
    public static final int VL_CL = 17;  // cache size
    private static final String[] cchKeys = new String[VL_CL];  // key-Cache
    private static String[] cchVals = new String[VL_CL];   // value-cache dto.
    private static int cchputInd, cchsrchInd;  // indices, Fül dto.

/** Keys for indicating date's and language's notations. <br />
 *  <br />
 *  For any supported language a complete set (translation) thereof must be
 *  available.<br />
 */
   static final String[] datLangKeys = {
     "lang",                       // 0 lanuage's short denotation like de en 
     
     "jan", "feb", "mar", "apr",   // 1..12 months long
     "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec",
     
     "jan_s", "feb_s", "mar_s", "apr_s",    // 13..24 months short
     "may_s", "jun_s", "jul_s", "aug_s", "sep_s", "oct_s", "nov_s", "dec_s",
     
     "sun", "mon", "tue", "wed", "thu", "fre",    // 25..31 days of week long
         "sat",    

     "sun_s", "mon_s", "tue_s", "wed_s",        // 32..38 days of week short
     "thu_s", "fre_s", "sat_s",

     ///  until here months, days of week (always there) fixed indices
     
     ///  from here on languages // may grow in future
     
     "de", "de_s", "en", "en_s",     // 39  +0
     "fr", "fr_s", "it", "it_s",     // 39  +4
     "es", "es_s", "pt", "pt_s",     // 39  +8
     "nl", "nl_s", "da", "da_s",     // 39  +12
   
    }; //  datLangKeys  

/** Begin of the language index range. <br />
 *  <br />
 *  Internal container number; start of first language entry (de).<br />
 *  <br />
 *  Value: <code>{@value}</code> 
 */   
   public static final int S_B = 39;

   
/** Number of date keys. <br />  */
   public static final int anzDatLangKeys = datLangKeys.length;
   
/** End of the language index range. <br />
 *  <br />
 *  Internal container number.<br />
 *  Value: {@link #anzDatLangKeys} 
 */   
   public static final int S_E = anzDatLangKeys; 
   

/** Keys for special entries. <br />
 *  <br />
 *  Those keys will be supported by every {@link AppLangMap}. In case of 
 *  entry / translation missing a substitute language's value will be 
 *  provided; usually en (English) then de (German; Deutsch).<br />
 *  <br />
 *  Hint for future work: to The value set for both English and German must 
 *  be kept comprehensive by all future extensions.<br />
 *  <br />
 */
   static final String[] suplKeys = {
     "sprspez",   // headline for test output, AppLangMap as application
     "yes", "no", "canc", "abort", "proceed",   // 55  +0
     

     "file", "dir",   // File, directory,  Datei Verzeichnis 
     "para",          // parameter
     "empt",          // empty leer 
     "male0", "fem0", // no (male and female; German: kein, keine)  
     "strt_pt",      // pattern {name} {start time} 2 line
     "wedaclock",   // format string for day of week, date and time
     
     "wintit",    // window title                Fenstertitel
     "strtargs",
     "logfmode",
     "strttime",
     "runtime",
     "systime",

     //  Exception texts (all ending by a blank / space (!)
     "noprfil",          // missing properties file, Fehlende Properties-Datei
     "noapprfil",        // missing .apps properties file        
     
     "nobaprfil",        // no base properties file

     "nofil",            // No file            Keine Datei 
     "nodir",            // No directory       Kein  Verzeichnis 
     "unknopt",          // Unknown option     Unbekannte Option  
     "unknprop",         // Unknown property   Unbekannte Eigenschaft
     "morespar",         // more than / Mehr als {0} (Standard-) Parameter
     "missval",          // Denotation for {0} missing  Angabe für {0} fehlt
     
   }; // suplKeys 
   
/** Number of special entries. <br />
 */   public static final int anzSuplKeys = suplKeys.length;

/** Begin of time zone's range. <br />
 *  <br />
 *  Internal container number.<br />
 *  Value: {@link #anzDatLangKeys} + {@link #anzSuplKeys} 
 */   
    public static final int Z_B = anzDatLangKeys + anzSuplKeys; 


/** Keys for time zones. <br />
 *  <br />
 *  For every supported language there has to be a complete translation.
 *  English is mandatory.<br />
 */
   static final String[] timeZoneKeys = {
     ///  SZ-WZ and CET, WET, EET and GMT_UTC in this sequence at start

     "nrmzt",   "somzt",         // 59 Z_Z,    +0 normal , +1 summer
     "nrmzt_s", "somzt_s",                  // +2 , +3 ditto short 
         
     "cet", "cest", "cet_s", "cest_s",      // +4 MEZ nLo suLo nSh suSh 
     "wet", "west", "wet_s", "west_s",      // +8 WEZ nLo suLo nSh suSh 
     "eet", "eest", "eet_s", "eest_s",      // +12 EEZ nLo suLo nSh suSh 
     "gmt", "utc",  "gmt_s", "utc_s",       // +16..58 gmt utc short short
    
   }; // timeZoneKeys

/** Number of keys for time zones. <br />  */
   public static final int anzTimeZoneKeysKeys = timeZoneKeys.length;


/** Number of standard keys. <br />
 *  <br />
 *  Value: {@link #anzDatLangKeys} + {@link #anzTimeZoneKeysKeys}
 *       + {@link #anzSuplKeys}
 */
   public static final int anzStdKeys =
            anzSuplKeys + anzDatLangKeys + anzTimeZoneKeysKeys;
   
/** Keys for standard entries. <br />
 *  <br />
 *  Those standard entry keys will be supported by every {@link AppLangMap}
 *  object. Additionally an index is directly assigned to them, that allows
 *  direct (without hash) access to the values (and keys). The full months'
 *  names index range, e.g., is of course 1 to 12.<br />
 *  <br />
 */
   static final String[] standardKeys = new String[anzStdKeys];
   
   static {
      System.arraycopy(datLangKeys, 0, standardKeys, 0, anzDatLangKeys);
      System.arraycopy(suplKeys,     0, 
                        standardKeys, anzDatLangKeys,  anzSuplKeys);
      System.arraycopy(timeZoneKeys, 0, 
                        standardKeys, anzDatLangKeys + anzSuplKeys,
                                                    anzTimeZoneKeysKeys);
   } // static 
   

/** Language specifics for applications, German, Deutsch, de. <br />
 *  <br />
 *  Implementation hints: basic endowment; always made.<br />
 */
   static public final AppLangMap MAP_de = new AppLangMap("de");
   
/** Language specifics for applications, English, en. <br />
 *  <br />
 *  Implementation hint: basic endowment; always made.<br />
 */  
   static public final AppLangMap MAP_en = new AppLangMap("en");

//==========================================================================   
   
/** The (two letter) language code. <br />
 *  <br />
 *  This is the two lower case characters language code, according to
 *  <a href="https://www.loc.gov/standards/iso639-2/php/code_list.php"
 *     target="_top">http-&gt;.ISO 639-2</a>.
 *  This property is fixed. And for one value like &quot;en&quot; for English
 *  or &quot;de&quot; (for Deutsch = German) there will be just one 
 *  {@link AppLangMap} object (langCode singleton).<br />
 *  <br />    
 *  Length 2: de, en fr, pt, nl, es etc.
 */
   public final String langCode;

//--------------------------------------------------------------------------
   
/** PropMap from resource files. */
   static protected SoftReference<PropMap> langPropMapRef;
   
/** jar file for resource is not signed. <br />
 *  <br />
 *  This will be evaluated on the very first call of 
 *  {@link #getLangPropMap()}. {@code jarUnsigned} is set true, if the 
 *  resource de/frame4j/util/AppLangMap.properties is not available or was not
 *  read from a verified (=signed .jar) stream.<br />
 */   
   static boolean jarUnsigned;

/** jar file for resource is signed. <br />
 *  <br />
 *  This will be evaluated on the very first call of 
 *  {@link #getLangPropMap()}. {@code jarUnsigned} is set true, if the 
 *  resource de/frame4j/util/AppLangMap.properties is available and was read
 *  from a verified (=signed .jar) stream.<br />
 */   
   static boolean jarSigned;
   

/** jar file for resource is signed. <br />
 *  <br />
 *  This method returns true, , if the resource 
 *  de/frame4j/util/AppLangMap.properties was read from a correctly signed
 *  .jar-file (frame4j.jar).<br />
 *  <br />
 */   
   static public boolean isJarSigned(){ return jarSigned && !jarUnsigned; }

   
/** PropMap from resource files. <br /> */
   static PropMap getLangPropMap(){
      PropMap ret = null;
      if (langPropMapRef != null) {
         ret = langPropMapRef.get();
         if (ret != null) return ret; // noch da
      }
      ret = new PropMap(971);
      Class<?> streamCl = ret.load1Impl(AppLangMap.class,
                "de/frame4j/util/AppLangMap.properties", ComVar.JAR_ENCODING);
      if (streamCl == null) {
         jarUnsigned = true;
         throw new IllegalStateException("No AppLangMap resource. ");
      }
      String clNam = streamCl.getName(); 
      // on signed jars: java.util.jar.JarVerifier$VerifierStream
      // on unsigned:    java.util.zip.ZipFile$1
      jarSigned = clNam.indexOf("Verifier", 9) != -1;
      jarUnsigned = jarUnsigned || !jarSigned;

      /// System.out.println( "  //////  TEST load1 AppLangMap  " + clNam 
          ///              + (jarUnsigned ? "  unsigned" : " signed"));

      langPropMapRef = new SoftReference<PropMap>(ret); // merken
      // System.out.println("  TEST getLangPropMap() loaded from res. ");
      return ret;
   } // getLangPropMap()
   
      
/** Language specifics for applications, user setting. <br />
 *  <br />
 *  If it was explicitly set this method return the {@link AppLangMap} to the
 *  last set user's language (like reported e,g, via {@link App} and 
 *  {@link Prop}).<br />
 *  <br />
 *  Default is the platform's setting according to {@link ComVar#UL}.<br />
 *  <br />
 *  This method never returns null, but delivers quickly a fitting 
 *  {@link AppLangMap} object prepared before. So if in doubt about the user's
 *  language settings use this method instead of using a stored language.<br />
 *  <br />
 *  @return nie null
 */  
   public static AppLangMap getUMap(){ return uMap; }
    
/** User setting of language. <br />
 *  <br />
 *  default = platform setting = {@link ComVar#UL}.<br />
 *  <br />
 *  Package access: Prop writes here when (inter) nationalising.<br />
 *  <br /> 
 */      
   static AppLangMap setULang(String userLangChoose){
      wLock.lock();
      AppLangMap olduMap = uMap; 
      uMap = getMap(userLangChoose);
         if (olduMap != uMap) {
            cchputInd =  cchsrchInd = 0;
            Arrays.fill(cchKeys, 0, VL_CL, null);
            Arrays.fill(cchVals, 0, VL_CL, null);
         }
      wLock.unlock();
      return uMap;
   } // setULang(String); package used by Prop's inter nationalising

/** Language specifics for applications, user setting. <br />
 *  <br />
 *  Hint 1: let it never be null!<br />
 *  Hint 2: do set only by setULang() cause of cache initialisation.
 *  @see #setULang
 *  @see #getUMap()
 */
   private static volatile AppLangMap uMap = setULang(ComVar.UL);

/** List of the entries as multi-line sequence. <br />
 *  <br />
 *  Appended to the {@link StringBuilder} bastel will be a list of all entries
 *  using at least one line per entry ({@link PropMapHelper.Entry}). The list
 *  starts with all standard entries in their natural order. All other entries
 *  follow in alphabetical order.<br />
 *  <br />
 *  Every line starts with its two letter language code allowing to 
 *  distinguish genuine entries from English or German substitute values.<br />
 *  <br>
 *  @param bastel StringBuilder to append to. If null it will be made with 
 *                ~ {@link #size()} * 43 characters starting capacity.
 *  @return bastel
 *  @see #toString()
 *  @see #hashQuality(StringBuilder)
 *  @see PropMap#sortedList(StringBuilder)
 *  @see #completeList(StringBuilder)
 */   
    public StringBuilder completeList(StringBuilder bastel){
       String[] list = (String[]) keySet().toArray();
       Arrays.sort(list);
       final int liLe = list.length;
       boolean[] omitt = new boolean[liLe];
       int liSe = liLe;
       if (bastel == null) bastel = new StringBuilder(liLe * 43 + 122);
       String key = null;
       
       overSpecialKeys: for (int i = 0 ; i < anzStdKeys;  ++i) {
          key = standardKeys[i];
          PropMapHelper.Entry ent = entry(key);
          
    ///  System.out.println("  /// TEST completeList " + key + " = " + ent);
          bastel.append("   (").append(lruMap.langCode).append(".)  ");
          ent.appendTo(bastel).append('\n');
          int inFullList = Arrays.binarySearch(list, key);
          if (inFullList >= 0) {
             omitt[inFullList] = true;
             --liSe;
          }
       } // overSpecialKeys: for 

       if (liSe <= 0) return bastel;
       
       bastel.append("\n  #-----\n\n");
       
       overOtherKeys: for (int i = 0 ; i < liLe;  ++i) {
          if (omitt[i]) continue overOtherKeys;
          key = list[i];
          PropMapHelper.Entry ent = entry(key);
          bastel.append("   (").append(lruMap.langCode).append(".)  ");
          ent.appendTo(bastel).append('\n');
       } // overOtherKeys: for 
       
       return bastel;
    } //  completeList(StringBuilder)
   
//-----------------------------------------------------------------------
   
/** Standard entries. <br />
 *  <br />
 */
   protected final PropMapHelper.Entry[] stdEntries;

/** Package constructor. <br />
 *  <br />
 *  Language related singleton.<br />
 */   
   AppLangMap(String langCode){
      this(langCode, langSubSet(langCode));
   } // AppLangMap(String)

/** Package constructor.<br />
 *  <br />
 *  Language related singleton.<br />
 */   
   AppLangMap(String langCode, PropMap sub){
      super(267);  // calls  clear(int)
      this.langCode     = langCode.intern();
      this.stdEntries   = new PropMapHelper.Entry[anzStdKeys];
      PropMapHelper.Entry langEntry = new PropMapHelper.Entry(standardKeys[0],
                                                        this.langCode, true);
      put(langEntry);
      if (sub != null) {
            this.putAll(sub);
            return; // subset available, took it, OK
      }
      // to here only with no subset (= unknown to AppLangMap.properties) 
      DateFormatSymbols dFS = null;
      try {
         locale = new Locale(langCode);
         dFS    = new DateFormatSymbols(locale);
      } catch (Exception e) { return; } //no Locale or dateFS
      
      String[] tSymb = dFS.getMonths();
      for (int i = 1; i <= 12; ++i) {
         String monthName = tSymb[i-1];
         PropMapHelper.Entry newEntry = 
                  new PropMapHelper.Entry(standardKeys[i], monthName , true);
         super.put(newEntry);
         stdEntries[i] = newEntry; // Standard-Entry months
         if (monthName.length() > 4 ) {
            monthName = monthName.substring(0, 4) + '.';
         }
         newEntry = 
              new PropMapHelper.Entry(standardKeys[i + 12],monthName , true);
         super.put(newEntry);
         stdEntries[i + 12] = newEntry; // Standard-Entry months short
      } // make locale's months
      tSymb = dFS.getWeekdays();
      String[] tSymb2 = dFS.getShortWeekdays(); // Sunday 1  Saturday  7
      for (int i = 1; i <= 7; ++i) {
         String weekDay = tSymb[i];
         String shWeekDay = tSymb2[i];
         PropMapHelper.Entry newEntry = 
                new PropMapHelper.Entry(standardKeys[i + 24], weekDay, true);
         super.put(newEntry);
         stdEntries[i + 24] = newEntry; // Standard-Entry months
         newEntry = 
              new PropMapHelper.Entry(standardKeys[i + 31], shWeekDay, true);
         super.put(newEntry);
         stdEntries[i + 31] = newEntry; // Standard-Entry months short
      } // make locale's weekdays 
      String[][] zoneStrings = dFS.getZoneStrings();
      int tempCnt = 0;
      for (String[] zonI : zoneStrings ) {
         if (zonI == null || zonI.length < 5) continue;
         String leShort = zonI[0];
         if (leShort == null || leShort.length() != 3) continue;
         if (leShort.equals("CET")) { // 1
            put("cet",  zonI[1]);
            put("cet_s",  zonI[2]);
            put("cest",  zonI[3]);
            put("cest_s",  zonI[4]);
            if (++tempCnt == 5) break;
            continue;
         }
         if (leShort.equals("WET")) { // 2
            put("wet",  zonI[1]);
            put("wet_s",  zonI[2]);
            put("west",  zonI[3]);
            put("west_s",  zonI[4]);
            if (++tempCnt == 5) break;
            continue;
          }
         if (leShort.equals("EET")) { // 3
            put("eet",  zonI[1]);
            put("eet_s",  zonI[2]);
            put("eest",  zonI[3]);
            put("eest_s",  zonI[4]);
            if (++tempCnt == 5) break;
            continue;
          }
          if (leShort.equals("UTC")) { // 4
            put("utc",  zonI[1]);
            put("utc_s",  zonI[2]);
            if (++tempCnt == 5) break;
            continue;
          }
          if (leShort.equals("GMT")) { // 5
             put("gmt",  zonI[1]);
             put("gmt_s",  zonI[2]);
             if (++tempCnt == 5) break;
             continue;
           }
      } // over  zoneStrings
   } // AppLangMap(String)

/** PropMap object with language subset. <br />
 *  <br />
 *  From the base .properties file  
 *  <a href="doc-files/AppLangMap.properties"
 *  target="_top">AppLangMap.properties</a> the subset for the language 
 *  {@code langCode} will be determined. If {@code langCode}'s length is not
 *  2 or if the subset is empty  null  will be returned.<br />
 *  <br />
 */   
   protected static PropMap langSubSet(CharSequence langCode){
      String code = TextHelper.checkLanguage(langCode, null);
      if (code == null)  return null;
      PropMap apLm = getLangPropMap();
      PropMap sub = apLm.subSet(code + '.');
      return sub.size == 0 ? null : sub;
   } // langSubSet
   
/** Language specifics for applications. <br />
  *  <br />
  *  Internal singleton list.<br />
  *  Contains all made AppLangMaps except de and en.
  */   
    private static AppLangMap[] alms;
    private static int almsLen; 

/** Fetch language specifics for applications. <br />
 *  <br />
 *  An {@link AppLangMap} object fitting the {@code langCode} will be 
 *  determined respectively made on first request end returned.<br />
 *  <br />
 *  The parameter will be stripped from surrounding white space and converted
 *  to lower case. If it's length is (then) not 2 the user's language setting
 *  (by {@link App} inheritors or {@link Prop}) is taken. To get this, it's
 *  recommended to use {@link #getUMap()} instead of this method with a null
 *  parameter.<br />
 *  <br />
 *  <br />
 *  @param langCode two character language code; default {@link ComVar#UL}.
 *  @see #langCode
 *  @return the ordered language object or a default / substitute (never null)
 */   
   public static AppLangMap getMap(final CharSequence langCode){
      if (langCode == null ) {
         if (uMap != null) return uMap;
      }
      String code = TextHelper.checkLanguage(langCode, null);
      
      if ("de".equals(code)) return MAP_de;  // we always have German (de)
      if ("en".equals(code)) return MAP_en;  // we always have English (en)

      int empty = -1;
      try { // sync. common re-build in  singleton liste
         wLock.lock();
         for (int i = 0; i < almsLen; ++i) {  // search for already build
            AppLangMap akt = alms[i];
            if (akt == null) {
               empty = i;
               break; // last entry (found as null)
            }
            if (akt.langCode.equals(code)) return akt; // found
         }  // search for already build ones
         
         PropMap apLm = getLangPropMap();
         PropMap subS = apLm.subSet(code + '.');
         final int origSize = subS.size;
         // System.out.println("  /// TEST getMap(" + code 
           //                        + ") size : "  + origSize);
         if (origSize  == 0) { // unknown
             // System.out.println("  /// TEST getMap(empty) : " + subS);
             subS = null; // Constructor(..., null) uses locale 
         } // unknown
         AppLangMap akt = new AppLangMap(code, subS);
        
         if (almsLen == 0) { // first additional
            almsLen = 10;
            alms = new AppLangMap[almsLen];
            return alms[0] = akt;
         } // first additional
         
         if (empty >= 0) { // empty entry found already (above)
            return alms[empty] = akt;
         } // empty entry found already (above)
         
         // make longer
         AppLangMap[] tmp = alms;
         alms = new AppLangMap[almsLen + 10];
         System.arraycopy(tmp, 0, alms, 0, almsLen);
         alms[almsLen] = akt;
         almsLen += 10;
         return akt;
      } finally {
         wLock.unlock();  
      }   // sync.
   } // getMap(CharSequence) 
   
/** Clear  / delete. <br />
 *  <br />
 *  This methods leaves this {@link AppLangMap} empty, except for the 
 *  special entries (also available indexed).<br />
 *  <br >
 *  {@code startCap} will be raised to a minimum of 267.<br />
 *  <br />
 */
   @Override public synchronized void clear(int startCap) {
      hashMissMax = hashMissSum = size = 0;
      if (startCap < 267) startCap = 267;
      super.clear(startCap);
      for (int i = 0; i < anzStdKeys; ++i) {  // special entries in again
         PropMapHelper.Entry ent = this.stdEntries[i];
         if (ent == null) continue;
         put(ent);
      } // special entries in again
   } // clear(int)
  
//-----------------------------------------------------------------------
   
/** Fetching summer / winter time. <br />
 *  <br />
 *  Returns the words or abbreviations for summer or winter time respectively
 *  their abbreviations. 
 *  For example the German language object {@link #MAP_de} would return
 *  according to the parameter values:<br /> &nbsp; 
 *  Sommerzeit  Winterzeit SZ  WZ<br />
 *  <br /> 
 *  @param summer true : Summer or day light saving time 
 *  @param abbrev true: abbreviate instead of full words
 */   
   public final String summerTime(boolean summer, boolean abbrev){
      return specValue(summer ? abbrev ? Z_B + 3 : Z_B + 1 
                             : abbrev ? Z_B + 2 : Z_B);
   } // summerTime

/** Fetching time zone descriptors. <br />
 *  <br />
 *  Returns the words or abbreviations for the given time zone, including
 *  summer or winter time, respectively their abbreviations. 
 *  For example the German language object {@link #MAP_de} and the CET zone
 *  this method would return according to the boolean parameter 
 *  values:<br /> &nbsp; 
 *  &quot;Mitteleuropäische Zeit&quot;, 
 *  &quot;Mitteleuropäische Sommerzeit&quot;, &quot;MEZ&quot;  or 
 *  &quot;MESZ&quot;.<br />
 *  <br /> 
 *  @param zone the time zone like CET (MEZ), EET, WET, GMT, UTC
 *                 (three letter code) 
 *  @param summer true : Summer or day light saving time if applicable 
 *                       (irrelevant for UTC, GMT) 
 *  @param abbrev true: abbreviate instead of full words
 */   
   public final String timeZone(final CharSequence zone,
                           boolean summer, final boolean abbrev){
      int base = Z_B + 4;  // Begin CET in time zone range
                     // Hint GMT is +12; UTC is by index GMT summer  
      findZone: if (zone != null) {
         if (zone.length() != 3) break findZone;
         char c1 = zone.charAt(0);
         if (c1 < 'a') c1 += 32; // so simple, as time zones use ASCII only
         char c2 = zone.charAt(1);
         if (c2 < 'a') c2 += 32; // so dto.
         char c3 = zone.charAt(2);
         if (c3 < 'a') c3 += 32; 
         final boolean c2_E = c2 == 'e';
         final boolean c3_T = c3 == 't';
         if (c1 == 'c' && c2_E && c3_T) break findZone; // shortcut CET 
         if (!c2_E) { // not xEy
            if (c1 == 'u' && c2 == 't' && c3 == 'c') { // UTC
               base += 12; 
               summer = true;
               break findZone;
            } // UTC
            if (c1 == 'g' && c2 == 'm' && c3_T) {
               base+= 12; 
               summer = false;
               break findZone;
            }
         } else {  // nicht xEy // xEy
            if (c1 == 'm'  && c3 == 'z') break findZone;  // MEZ
            if (c3_T) {
               if (c1 == 'w') {
                  base += 4; // WET 
                  break findZone;
               }
               if (c1 == 'e') {
                  base += 8; // EET
                  //break findZone;
               }
            } // xET
         } //xEy  
         // End quick western Europe
         searK: while ((base += 4) < anzStdKeys) { // 1. key==zeitzon3BuBez
            String key = standardKeys[base];
            if (key.charAt(0) != c1) continue searK;
            if (key.charAt(1) != c2) continue searK;
            if (key.charAt(2) == c3) break findZone;
         } // earK: while 
         base = Z_B + 4; // again CET
         // break findZone; // to here only with  CET or unimplemented zone
      } // findZone: if 
      if (abbrev) base += 2;
      if (summer) ++base;
      return specValue(base);
   } // timeZone(String) 

/** Fetching a language descriptor. <br />
 *  <br />
 *  This method returns languages names in this objects language. For the 
 *  French {@link AppLangMap} object for nd (= Dutch) &quot;hollandaise&quot;
 *  respectively abbreviated &quot;holl.&quot; would be returned.<br />
 *  <br />
 *  If the language to be denoted is unknown the empty String is returned.
 *  If there's no translation for the languages denotation is there English
 *  or German (i.e. Dutch or Niederländisch for above example) would be 
 *  returned.<br />
 *  <br />
 *  If the parameter lang is null or empty this {@link AppLangMap}'s own
 *  language {@link #langCode} is taken.<br />
 *  <br />
 *  @param lang language to denote as ISO 2 character code (de,
 *          en, fr, it, es, pt, nl, da ...); case or surrounding white space
 *          are ignored; null is own language
 *  @param abbrev true: abbreviate (angl.) instead of full name (anglais)
 *  @return language denotation or abbreviation
 */   
   public final String languageName(final CharSequence lang,
                           final boolean abbrev){
      String langC = TextHelper.trimUq(lang, langCode);
      if (langC.length() != 2) return ComVar.EMPTY_STRING;
      char c1 = langC.charAt(0);
      if (c1 < 'a') c1 += 32; // so simple, as ISO-LangCodes ASCII only
      char c2 = langC.charAt(1);
      if (c2 < 'a') c2 += 32; // so simple, as ...
      int base =  S_B;  //first language index
      langSear:  for ( ;  base < S_E; base += 2) {
         String aktKey = standardKeys[base];
         if (aktKey.charAt(0) != c1) continue langSear;
         if (aktKey.charAt(1) != c2) continue langSear;
         return specValue(abbrev ? base + 1 : base);    // found
      } // langSear:  for 
      return ComVar.EMPTY_STRING; 
   } // languageName(CharSequence, boolean)

/** Fetching the full month. <br />
 *  <br />
 *  For English this will get: January, February, ...<br />
 *  <br />
 *  @param m Month (1=January .. 12=December)
 *  @return the month's name or the empty String  ({@link ComVar#EMPTY_STRING})
 *  @see #shortMonth(int)
 *  @see #weekDay(int)
 */
   public final String month(final int m){ 
      return m < 1 || m > 12 ? ComVar.EMPTY_STRING : specValue(m); 
  } // month(int)

/** Fetching the abbreviated month. <br />
 *  <br />
 *  The abbreviations end with a dot.<br />
 *  <br />
 *  The abbreviation's lengths are equal for all months but language dependent.
 *  The minimal length for unambiguous month abbreviations in English and 
 *  German is three making a four with the dot. As a consequence (for that
 *  languages May June and July (Mai, Juni, Juli) can / will not be 
 *  abbreviated.<br />
 *  <br />
 *  @param m Month (1=January .. 12=December)
 *  @return the month's abbreviation or the empty String 
 *            ({@link ComVar#EMPTY_STRING})
 *  @see #month(int)          
 */
   public final String shortMonth(final int m){
      if (m < 1 || m > 12) return ComVar.EMPTY_STRING;
      return  specValue(m + 12);
   } // shortMonat(int)

// days of week  ----------------   

/** Fetching the full day of week. <br />
 *  <br />
 *  In case of German one would get Sonntag, Montag, ... and so on.<br />
 *  Sunday is grabbed by indices 0 and 7 fulfilling two counting standards 
 *  directly.<br />  
 *  <br />
 *  @param wd day of week (0=Sunday .. 6=Saturday, 7=Sunday)
 *  @return the weekdays name or the empty String 
 *            ({@link ComVar#EMPTY_STRING})
 *  @see #shortWeekDay(int)
 *  @see #month(int)          
 */
   public final String weekDay(final int wd){ 
      if (wd == 0 || wd == 7) return specValue(25); // Sunday as 0 and 7 
      return wd < 0 || wd > 6 ? ComVar.EMPTY_STRING : specValue(wd + 25); 
   } // weekDay(int)
 
/** Fetching the abbreviated day of week. <br />
 *  <br />
 *  Abbreviated days of week will be returned without a trailing dot.<br />
 *  <br />
 *  The abbreviation's lengths are equal for all days of week but they are
 *  language dependent. The minimal length for an unambiguous abbreviations in
 *  English, German  and French is two. Portuguese requires three (Seg / 
 *  Sex).<br />
 *  English returns: Su Mo Tu We Th Fr Sa (Su) (for 0..7)<br />
 *  German returns; So Mo Di Mi Do Fr Sa (So)<br />
 *  <br />
 *  @param wd day of week (0=Sunday .. 6=Saturday, 7=Sunday)
 *  @return the weekdays abbreviation or the empty String 
 *            ({@link ComVar#EMPTY_STRING})
 *  @see #weekDay(int)
 */
   public final String shortWeekDay(final int wd){
      if (wd == 0 || wd == 7) return specValue(32); // Sunday as 0 and 7 
      if (wd < 0  || wd > 6) return  ComVar.EMPTY_STRING;
      return specValue(wd + 32);
   } // shortWeekDay(int)

//------------------------------------------------------------------------   

/** Internal number for standard keys. <br />
 *  <br />
 *  If there is a standard key {@code key} non regarding case or surrounding
 *  white space its assigned index is returned; or -1 otherwise.<br />
 *  <br />
 *  Hint: Internal helper function
 *  <br />
 *  @see #containsKey(java.lang.Object)
 */
   public static int contNo(final CharSequence key){
      final String k = TextHelper.trimUq(key, null);
      if (k == null) return -1;
      return spezKontOKo(k);
   } // contNo(CharSequence)

   
/** Internal number for special keys. <br />
 *  <br />
 *  If there is a special key {@code key} non regarding case or surrounding
 *  white space its assigned index is returned; or -1 otherwise.<br />
 *  <br />
 *  Hint: Internal helper function. <br />
 *  <br />
 *  @see #containsKey(java.lang.Object)
 */
   static int spezKontOKo(final String key){
      final int kL = key.length();
      if (kL == 0) return -1;
      final char kC0 = key.charAt(0);
      int i = S_B;
      int j = anzStdKeys;
      indSearch: while (j != 0) {
         --j;
         if (++i == anzStdKeys) i = 0;
         String vgl = standardKeys[i];
         if (vgl == key) return i;  // Optimising same
         if (vgl.length() != kL)        continue indSearch;
         if (!TextHelper.simpCharEqu(vgl.charAt(0), kC0)) continue indSearch;
         for (int g = 1; g < kL; ++g) {
            if (!TextHelper.simpCharEqu(vgl.charAt(g), key.charAt(g)))
                                                          continue indSearch;
         } // for equals simple ignore case (keys are ascii only 88591 at best
         return i;
         /// if(key.regionMatches(true, 0, compare, 0, kL))  return i;
      } // search while, start at sentence 2
      return -1;
   } // contNo(String)

/** Fetch value for internal number. <br />
 *  <br />
 *  Is contNo &gt;= 0 and &lt: {@link #anzStdKeys} the value to this internal
 *  index is returned and null otherwise.<br />
 *  <br />
 *  Hint: internal helper.
 *
 *  @see #containsKey(java.lang.Object)
 *  @return value or null
 */
   final String specValue(final int contNo){
      if (contNo < 0 || contNo >= anzStdKeys) return null;
      PropMapHelper.Entry ret = stdEntries[contNo];
      if (ret == null) {
         ret = (contNo >= Z_B ? MAP_en : MAP_de).stdEntries[contNo]; 
      }
      return ret.value;
   } // specValue(int) 

/** Fetch value in user language for a key. <br />
 *  <br />
 *  The call is equivalent to
 *  {@link #valueUL(CharSequence, String) valueUL(key, null)}.
 *
 *  @see #getUMap()
 *  @see Prop#valueLang(CharSequence)
 */
   public static final String valueUL(final CharSequence key){
      return valueUL(key, null);
   } // valueUL(CharSequence) 

/** Fetch value in user language for a key. <br />
 *  <br />
 *  {@code key} will be stripped from surrounding whitespace. If {@code key}
 *  is (then) empty, {@code def} will be returned.<br />
 *  <br />
 *  For the user's set language a value fitting the {@code key} is 
 *  searched for. If found and not null it is returned or otherwise
 *  {@code def}.<br />
 *  <br />
 *  Hint: Due to the elaborate look&nbsp;up by {@link #entry(CharSequence)} 
 *  (bringing the comfort to internationalised applications in the end) the
 *  last {@link #VL_CL} findings will be cached for quick retrieval. So,
 *  applications may just use this method without concerns or caching 
 *  exertions.<br />
 *  <br />
 *  @see #valueUL(CharSequence)
 *  @see Prop#valueLang(CharSequence)
 *  @see Prop#valueLang(CharSequence, String)
 */
   public static final String valueUL(final CharSequence key, 
                                                           final String def){
      final String tKey = TextHelper.trimUq(key, null); 
      if (tKey == null) return def;
      try { // cache search
         rLock.lock();
         for (int z  = VL_CL, si = cchsrchInd; z != 0; --z) { // cache search
            final String cachKey = cchKeys[si];
            if (cachKey != null && tKey.equals(cachKey)) { // found in cache
               final String fnd =  cchVals[si];
               cchsrchInd = si;    
               /// System.out.println("  /// TEST valueLang(" + key 
               ///                    + ", ) hit: "  + fnd);
               return fnd == null ? def : fnd;
            }  // found in cache
         } // for 
      } finally {
         rLock.unlock();
      }
      final PropMapHelper.Entry found = uMap.entry(tKey);
      final String ret = found == null ? null : found.value;
      
      wLock.lock();
      final int pi = cchputInd;  // putIndex
      if (++cchputInd == VL_CL) cchputInd = 0; // next put (round robin)
      cchKeys[pi] = tKey;  // cache it
      cchVals[pi] = ret;   // even if the result is null
      wLock.unlock();
      return ret != null ? ret : def;
   } //  valueUL(CharSequence) 
   

/** Make a (inter) nationalised massage. <br />
 *  <br />
 *  According to the key {@code key} and the  user's set language a pattern
 *  will be determined by 
 *  {@link #valueUL(CharSequence, String) valueUL(key, patternDefault)}.<br />
 *  If indeterminable null is returned.<br />
 *  <br />
 *  If {@code param} is null the pattern just determined is returned as 
 *  is.<br />
 *  Otherwise the pattern is used with {@code param} to form a message as by
 {@link de.frame4j.text.TextHelper#messageFormat(StringBuilder, CharSequence, Object)
 *     messageFormat(null, pattern, param)}. This message is returned.<br />
 *  <br />
 *  @param key   a language independent key for the user language dependent 
 *               pattern
 *  @param patternDefault substitute pattern if indeterminable (as not null) 
 *                        by key
 *  @param param the parameters to be parts of the message
 *  @return      the nationalised (user's language) message
 */
  public static StringBuilder formMessageUL(final CharSequence key,
                  final String patternDefault, final Object param){
      final String pattern = valueUL(key, patternDefault);
      return TextHelper.messageFormat(null, pattern, param);
   } ///  formMessageUL(CharSequence, ...)

//------------------ modified. Map or PropMap methods  --------------

/** Enter a key value pair. <br />
 *  <br />
 *  @see PropMap#put(de.frame4j.util.PropMapHelper.Entry)
 */   
  @Override public PropMapHelper.Entry put(PropMapHelper.Entry newEntry)
                                         throws UnsupportedOperationException{
      if (newEntry == null) return null;
      PropMapHelper.Entry oldEntry = super.put(newEntry);
      if (oldEntry != null) return oldEntry;  // was in already; do nil
      
      final String kS   = newEntry.key;
      final int specialIndex = spezKontOKo(kS);
      if (specialIndex >= 0)
         stdEntries[specialIndex] = newEntry; // Standard entry consistent !
      return null;
   } // put(Entry)
   

/** Fetching the entry for a key. <br />
 *  <br />
 *  Keys have to be non empty character sequences.<br />
 *  <br />
 *  @see java.util.Map#get(java.lang.Object)
 *  @see #get(Object)
 */
   @Override public final PropMapHelper.Entry entry(final CharSequence key){
      String k = TextHelper.trimUq(key, null);
      if (k == null) return null;
      final int kL = k.length();
      if (kL == 0) return null;
      int ind = indexOfKey(key); // PropMap hash-Look-Up
      lruMap = this;
      if (ind >= 0) return entries[ind];

      final int specialIndex = contNo(k);
      if (specialIndex == -1) { // no standard entry
         if (this != MAP_en) { // try en first substitute
            ind = MAP_en.indexOfKey(key);
            lruMap = MAP_en;
            if (ind >= 0) return MAP_en.entries[ind];
         } // try en
         if (this != MAP_de) { // try de second substitute
            ind = MAP_de.indexOfKey(key);
            lruMap = MAP_de;
            if (ind >= 0) return MAP_de.entries[ind];
         } // try de 
         return null;
      } // no standard entry
      PropMapHelper.Entry ret = stdEntries[specialIndex];
      if (ret == null) {
         final AppLangMap tmp = specialIndex >= Z_B ? MAP_en : MAP_de;
         lruMap = tmp;
         ret = tmp.stdEntries[specialIndex]; 
      }
      return ret;
   } // entry(CharSequence)
   
   private static AppLangMap lruMap; // should be volat., but is for list only 

/** Remove an entry by index. <br />
 *  <br />
 *  Internal helper method.<br />
 *  <br />
 *  Implementation hint: In removes any entry,except if it belongs to one of
 *  the special keys.<br />
 *  <br /> 
 *  @return true if existing entry was removed.
 */
   @Override protected boolean remove(final int i){
       if (i < 0 || i >= entries.length || size == 0) return false;
       PropMapHelper.Entry akt = entries[i];
       if (akt == null) return false; // empty already
       
       int specIndex = contNo(akt.key);
       if (specIndex != -1) return false;
       
       entries[i] = null;  // from here on copy from PropMap and paste
       --size;
       return true;
    } // remove(int)

/** The Locale used for otherwise not supported languages. <br /> */
    protected volatile Locale locale;
    
/** Get the Locale best fitting this application language map. <br />
 *  <br />     
 *  @return a fitting Locale
 */
    public Locale getLocale(){
       if (locale != null) return locale;
       locale = new Locale(langCode, region, ComVar.EMPTY_STRING);
       return  locale;
    } // getLocale()
    
//----------------------------------------------------------------------------

/** Start as application. <br />
 *  <br />
 *  Execute:  java de.frame4j.util.apps.AppLangMap [options] [language]<br />
 *  <br />
 *  Possible options are all {@link Prop}'s default options (see
 *  {@link Prop#Prop()}).<br />
 *  <br />
 *  language is the ISO two letter abbreviation. Without that parameter the
 *  platform (default) language or the language chosen by  {@link Prop}'s
 *  option parameter is used.<br />
 *  <br />
 *  <br />
 *  This application gets the ordered {@link AppLangMap} object and lists 
 *  its key-value-pairs. If not available for the language, a substitute
 *  according to {@link AppLangMap}'s rules is taken.<br />
 *  <br />
 *  The application is an anonymous inheritor of {@link App}.<br />
 */   
   public static void main(String[] args){
      new App(args){
         @Override protected int doIt() {
               String langK  = null;
               for (int i = 0; i < args.length; ++i) {
                  langK = args[i]; 
                  if (langK != null) break; // 1. non partially used parameter
               } // search 1st unused parameter 

               AppLangMap mapUse  = getUMap();
               AppLangMap mapShow = langK != null ? getMap(langK) : mapUse;

               langK = mapShow.langCode;
               log.println(twoLineStartMsg());
               
               log.println("  ///   " + getCopyright() +  "\n\n\n   " 
               + valueUL("sprspez") + ' ' + langK + " (ISO) = "
               +        mapUse.languageName(langK, false)
               +        " (" + mapUse.languageName(langK, true) + ") : "

               +  "\n\n   (lang.)  key = value  \n\n");

               log.println(mapShow.completeList(null));
               
               if (isDebug()) {
                  log.println("\n \n AppLangMap resource properties \n");
                  PropMap apLm = getLangPropMap();
                  log.println(apLm .sortedList(null));
               }
               log.println(twoLineEndMsg());
               return 0;
            }  // doIt
         }; // anonymous App inheritor
   } // main(String[]) 
    
} // class ApplangMap (05.10.2004, 20.06.2009)