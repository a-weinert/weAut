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
package de.frame4j;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.frame4j.text.CleverSSS;
import de.frame4j.text.RK;
import de.frame4j.text.TextHelper;
import de.frame4j.util.ComVar;


/** <b>Display the system and environment properties</b>. <br />
 *  <br />
 *  This application display the Java runtime environment's so called 
 *  &quot;system properties&quot;.<br />
 *  <br />
 *  Small optional function (since 12.2005): If there is a command line 
 *  argument<br /> &nbsp;
 *     java de.frame4j.ShowProps lookFor [-case]<br />
 *  the application returns 1 (as process exit code) instead of 0, if the 
 *  String to be looked for can't be found as substring in any system
 *  property value. The search will ignore case, when no second argument
 *  -case was given to make the search case sensitive.<br />
 *  Hint: This value matching is applied to the system properties only.
 *  The system property with key sun.java.command will be ignored; it would
 *  always contain the search pattern as value (May 2015).<br />
 *  <br />    
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *   Copyright 1998 - 2005, 2021  &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 *  @see      de.frame4j
 *  @see      de.frame4j.util.PropMap
 */
 // so far    V00.00 (22.05.1998) : new
 //           V02.20 (04.10.2004) : sorting
 //           V02.24 (12.12.2005) : optional pattern search 
 //           V02.25 (11.04.2007) : no System.exit for 0 (cause of JMX)
 //           V.50+  (20.01.2009) : ported (SVN, Frame4J)
 //           V.121+ (21.11.2009) : environment added
 //           V.128+ (28.05.2015) : value search debugged
 // last change  $Author: albrecht $, $Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $

public final class ShowProps {
   
   private ShowProps(){} // no objects; no javaDoc 

/** Name and version. <br />
 *  <br />
 *  {@value}
 */
   static final public String version = 
     "ShowProps  V$Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)";


/** Copyright note. <br />
 *  <br />
 *  {@value}
 */
   static final public String copyRight = 
     "Copyright (c) 1998 - 2016, 2021 Albrecht Weinert, Bochum (a-weinert.de)";


/** Start method of ShowProps. <br />
 *  <br />
 *  The call is: java de.frame4j.ShowProps<br />
 *  <br />
 *  @param  args     none or search-pattern or search-pattern -case or
 *                   -javaMainVers
 */
   public final static void main(final String[] args){
      StringBuilder stB = new StringBuilder(200);
      Properties   sP  = System.getProperties();
      int   anzProps   = sP.size();
      String   name    = null;
      String   propVal = null;
      int ret = 0;
      
      final int anzArg = args == null ? 0 : args.length;
      if (anzArg == 1 && TextHelper.startsWith(args[0], "-javamainv", true)) {
         
         String javSecVers = sP.getProperty("java.specification.version");
         if (javSecVers == null) sP.getProperty("java.version");
         if (javSecVers == null) sP.getProperty("java.vm.specification.version");
         if (javSecVers == null) {
            ret = 99;
         }
         if (javSecVers.length() > 2 && javSecVers.startsWith("1.")) 
                                   javSecVers = javSecVers.substring(2);
         int pPos = javSecVers.indexOf('.');
         if (pPos >= 1) javSecVers = javSecVers.substring(0, pPos);
         pPos = javSecVers.indexOf('-');
         if (pPos >= 1) javSecVers = javSecVers.substring(0, pPos);
         
         try {
            ret = Integer.parseInt(javSecVers, 10);
         } catch (NumberFormatException ex) {
            ret = 99;
         }

         
         if (ret == 99)  javSecVers = "unknown";
         System.out.println(javSecVers);
         if (ret != 0) System.exit(ret);
      } // -javaMainVersion
      
      
      String[] listP   = new String[anzProps];
      
      System.out.println(
         "\n\n - - - - - -  Java - System - Properties - - - - - - -    ("
            + anzProps + ")\n\n  "
                +  version + "\n  " + copyRight + "\n\n");
      
      String foundInProp = null;
      final boolean ignCase = anzArg < 2 
                              || !TextHelper.areEqual("-case", args[1], true);

      final CleverSSS searchInProp = anzArg != 0 
                                    ? RK.make(args[0], ignCase, false) : null;
// System.out.println(" TEST ignCase = " + ignCase + " on " + searchInProp);
 
      int ind = 0;
      for (Enumeration<?> e = sP.propertyNames();
                                     e.hasMoreElements(); ) { // for get them
         name = (String) e.nextElement();
         stB.setLength(0);
         stB.append(name);
         while (stB.length() < 25 ) stB.append (' ');
         propVal =  sP.getProperty(name);
         if (propVal != null) {
            final int propVLe =  propVal.length();
            stB.append (" = ");
            if (searchInProp != null && foundInProp == null
                        && !"sun.java.command".equals(name)) { // pattern s.
              if (searchInProp.indexOf(propVal, 0) != -1) foundInProp = name;
            }  // pattern search 
            if (TextHelper.areEqual("line.separator", name, true)) { 
               stB.append("(\\n = ) ");
               for (int i = 0; i < propVLe; i++)
                  stB.append((int)propVal.charAt(i)).append(' ');
            } /* line.separator */  else {
               if (propVal.length() + stB.length() > 78) 
                  stB.append ("\n    ").append(propVal).append("\n ");
               else    
                  stB.append(propVal);

            } // other property 
         }  else {   // propVal == null
            stB.append (" = <null>");
         }
         listP[ind] = stB.toString();
         ++ind;
      } // for
      
      Arrays.sort(listP);
      for (String s: listP) System.out.println(s);
      

      stB.setLength(0);
      stB.append('\n');   

      if (searchInProp != null) { // pattern to search
         stB.append("\n  /// Value \"").append(searchInProp);
         stB.append("\"\n  ///    (");
         stB.append(ignCase ? "ign. case) " : "with case) ");
         if (foundInProp != null) {
            stB.append("contained in ").append(foundInProp);
         } else {
            stB.append("not found in any system property.");
            ret = 1;
         }   
         stB.append('\n');   
      } // pattern to search
      
      Map<String,String> env = null;
      Set<Map.Entry<String,String>> eS = null;
      int anzEnv = 0;
      
      try {
         env = System.getenv();
         eS  = env.entrySet();
         anzEnv = eS.size();
      } catch (Exception ignoreIt){}
      
      if (eS == null) {
         stB.append("\n  /// no environment found.\n");
         System.out.println(stB.toString());
         if (ret != 0) System.exit(ret);
         return;
      }
      
      stB.append("\n\n  - - - - - -  OS environment - - - - - - - - - - ("
                                                         + anzEnv + ")\n");
      System.out.println(stB.toString());
      listP = new String[anzEnv];
      int i = 0;
      for (Map.Entry<String, String> enVar : eS) {
         name = enVar.getKey();
         stB.setLength(0);
         stB.append(name);
         while (stB.length() < 25 ) stB.append (' ');
         stB.append (" = ");
         stB.append(enVar.getValue());
         listP[i] = stB.toString();
         ++i;
      }
      Arrays.sort(listP);
      for (String s: listP) System.out.println(s);
      
      System.out.println(
         "\n\n  - - - - - -  OS assumptions - - - - - - - - - -\n\n"
        + ComVar.OS  + (ComVar.ON_PI ? ", on a Raspberry Pi"
                          : (ComVar.NOT_WINDOWS ? ", not a Windows" : "")));
      System.out.println(    
               "file encoding = " + ComVar.FILE_ENCODING 
           + ",  jar encoding = " + ComVar.JAR_ENCODING 
           + "\nconsole encoding = " + ComVar.CONSOL_ENCODING 
           + (ComVar.hasCons ? " + \n" : " (no console)\n"));
 
      System.out.println('\n');
      if (ret != 0) System.exit(ret);
   } // main(String[])
   
   
} // ShowProps (24.04.03, 04.10.2004, 12.12.2005, 20.01.2009, 28.05.2015)
