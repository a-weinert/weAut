/*  Copyright 2018 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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


import de.frame4j.util.MinDoc;
import java.util.Arrays;
import de.frame4j.util.ComVar;

/** <b>Executing a Program</b>. <br />
 *  <br />
 *  This application runs the {@link de.frame4j de.frame4j} Java programme
 *  given as first parameter passing on the other parameters, if any, to that
 *  programme. If no parameter is given {@link de.frame4j.AskAlert AskAlert}
 *  will be run (with no parameters). Additionally de.frame4j.Exec replaces
 *  {@link de.frame4j.AskAlert} as frame4j.jar's Main-Class (in the 
 *  manifest).<br />
 *  <br />
 *  The rationale behind is Java 11, i.e. all Java &gt;=9, lacking the
 *  &quot;installed extension&quot; mechanism. Until Java 8 it <ul>
 *  <li> made available all frame4j tools without further ado and</li>
 *  <li> was / is essential for many Java server applications.</li></ul>
 *  The first point is partly overcome by Exec:<br />
 *  Instead of e.g. <code><pre>
 java de.frame4j.SVNkeys . -omitDirs CVS;doc-files;.svn </pre></code>
 *  or <code><pre>
 java -cp ....frame4j.jar de.frame4j.SVNkeys . -omitDirs CVS;doc-files;.svn</pre></code>
 *  you may say <code><pre>
 java -jar ...frame4j.jar SVNkeys . -omitDirs CVS;doc-files;.svn</pre></code>
 *  For comfort you may prepare two one line batch files <code><pre>
 {@literal @}REM frame4j.bat
 {@literal @}C:&#92;util&#92;jdk&#92;bin&#92;java.exe -jar C:&#92;util&#92;jdk&#92;jre&#92;lib&#92;ext&#92;frame4j.jar %*
 
 {@literal @}REM frame4j11.bat
 {@literal @}C:&#92;util&#92;jdk11&#92;bin\java.exe -jar C:&#92;util&#92;jdk11&#92;lib&#92;frame4j.jar %*
</pre></code>
 *  to start a frame4j tool with Java8 or Java11 by, e.g.,.<code><pre>
 frame4j SVNkeys . -omitDirs CVS;doc-files;.svn</pre></code>
 *  Note: A Java 8 compiled .jar may run under Java11 but not vice versa.<br />
 *  <br />
 *  The list of Frame4J tools, helpers and demos startable by Exec is: <br />
 *  
{@link AskAlert}, 
{@link de.frame4j.AskAlert de.frame4j.AskAlert}, <br />
{@link SVNkeys}, 
{@link de.frame4j.SVNkeys de.frame4j.SVNkeys}, 
{@link SVNkeysFilter},  <br />
{@link de.frame4j.SVNkeysFilter de.frame4j.SVNkeysFilter}, 
<a href="../../ComplDemo.html" 
 title="starter in &lt;Unnamed&gt;"><code>ComplDemo</code></a>, 
{@link de.frame4j.demos.ComplDemo de.frame4j.demos.ComplDemo}, 
<a href="../../TvH.html"
  title="starter in &lt;Unnamed&gt;"><code>TvH</code></a>, 
{@link de.frame4j.demos.TvH de.frame4j.demos.TvH}, 
{@link de.frame4j.demos.HelloFrame4J de.frame4j.demos.HelloFrame4J},  <br />
{@link de.frame4j.time.TimeHelper de.frame4j.time.TimeHelper}, 
{@link de.frame4j.util.AppLangMap de.frame4j.util.AppLangMap}, <br />
{@link Del}, 
{@link de.frame4j.Del de.frame4j.Del}, 
{@link Era}, 
{@link de.frame4j.Era de.frame4j.Era}, 
{@link FS}, 
{@link de.frame4j.FS de.frame4j.FS}, <br />
{@link FuR}, 
{@link de.frame4j.FuR de.frame4j.FuR}, <br />
{@link MakeDigest}, 
{@link de.frame4j.MakeDigest de.frame4j.MakeDigest}, 
{@link PKextr}, 
{@link de.frame4j.PKextr de.frame4j.PKextr}, 
{@link SendMail}, 
{@link de.frame4j.SendMail de.frame4j.SendMail}, <br />
{@link de.frame4j.io.ShowPorts de.frame4j.io.ShowPorts},
{@link ShowProps}, 
{@link de.frame4j.ShowProps de.frame4j.ShowProps}, <br /> 
{@link UCopy}, 
{@link de.frame4j.UCopy de.frame4j.UCopy}, 
{@link Update}, 
{@link de.frame4j.Update de.frame4j.Update}, <br />
{@link XMLio}, 
{@link de.frame4j.XMLio de.frame4j.XMLio}, 
{@link de.frame4j.net.ClientLil de.frame4j.net.ClientLil},<br >
{@code TestOnPi}, {@link de.weAut.TestOnPi}. <br />
 * <br />
 *  de.frame4j.Exec accepts the fully qualified class name as well as the
 *  pure name as first parameter.<br />

 *  <a href="./package-summary.html#co">&copy;</a> 
 *  Copyright 2018 &nbsp; Albrecht Weinert 
 *  @see      de.frame4j.AskAlert
 */
 // so far    V.006  19.11.2018 : new
 //           V.008  23.11.2018 : errors by Java11 handling improved
 //           V.o14  08.03.2019 : TvH added
 //           V.o15  10.03.2019 : option displays short help
 //           V.031  12.03.2021 : itext etc. removed
 
@MinDoc(
   copyright = "Copyright 2019  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 42 $",
   lastModified   = "$Date: 2021-05-01 18:54:54 +0200 (Sa, 01 Mai 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application (-? for help)",  
   purpose = "starts another java programme / frame4j tool given as 1st param"
) public final class Exec{
      
   private Exec(){} // no objects; no javaDoc 

/** The program. <br />
 *  <br />
 *  Delegation to  {@link de.frame4j.AskAlert}.<br />
 *  <br />
 *  @param  args tool + its command line parameter<br />
 *  @see de.frame4j.AskAlert#main(String[])
 */
   public static void main(String[] args){
      final int argLen = args.length;
      if (argLen == 0) {
         de.frame4j.AskAlert.main(ComVar.NO_STRINGS);
         return;
      } // no args
      
      final String progName = args[0];
      if (progName.charAt(0) == '-') { // option instead of name (e.g. -?)
         final String[] errorArg = {"-kk", "-wm ", // no buttons 50s
     "usage: Exec frame4jTool [tool parameters ..\n"
   + "Aufruf: Exec frame4j-Programm [Programmparameter ..\n\n"
   + "See available frame4j tools in de.frame4j package documentation\n"
   + "( weinert-automation.de/java/docs/frame4j/de/frame4j/package-summary.html).",
     "Close to proceed" };
         de.frame4j.AskAlert.main(errorArg);
         System.exit(17); // signal error  by return code 17
         return;
      } // -option

      String[] argForw = argLen > 1 ?
             Arrays.copyOfRange(args, 1, argLen)  :  ComVar.NO_STRINGS;
      switch (progName) {
      case "AskAlert":
      case "de.frame4j.AskAlert":
         de.frame4j.AskAlert.main(argForw);
         return;

      case "CVSkeys":                  // deprecated and removed in between 
      case "de.frame4j.CVSkeys":      // deprecated and removed in between 
      case "SVNkeys":
      case "de.frame4j.SVNkeys":
         de.frame4j.SVNkeys.main(argForw);
         return;
      case "SVNkeysFilter":
      case "de.frame4j.SVNkeysFilter":
         de.frame4j.SVNkeysFilter.main(argForw);
         return;
         
      case "ComplDemo":
      case "de.frame4j.demos.ComplDemo":
         de.frame4j.demos.ComplDemo.main(argForw);
         return;
      case "TvH":
      case "de.frame4j.demos.TvH":
         de.frame4j.demos.TvH.main(argForw);
         return;
      case "HelloFrame4J":
      case "de.frame4j.demos.HelloFrame4J":
         de.frame4j.demos.TvH.main(argForw);
         return;
         
      case "TimeHelper":
      case "de.frame4j.time.TimeHelper":
         de.frame4j.time.TimeHelper.main(argForw);
         return;
      case "AppLangMap":
      case "de.frame4j.util.AppLangMap":
         de.frame4j.util.AppLangMap.main(argForw);
         return;

      case "Del":
      case "de.frame4j.Del":
         de.frame4j.Del.main(argForw);
         return;
      case "Era":
      case "de.frame4j.Era":
         de.frame4j.Era.main(argForw);
         return;
      case "FS":
      case "de.frame4j.FS":
         de.frame4j.FS.main(argForw);
         return;
      case "FuR":
      case "de.frame4j.FuR":
         de.frame4j.FuR.main(argForw);
         return;

      case "MakeDigest":
      case "de.frame4j.MakeDigest":
         de.frame4j.MakeDigest.main(argForw);
         return;
      case "PKextr":
      case "de.frame4j.PKextr":
         de.frame4j.PKextr.main(argForw);
         return;
      case "SendMail":
      case "de.frame4j.SendMail":
         try {  // Ugly ugly ugly Java 9 .. 11 kill mail extension 
            @SuppressWarnings("rawtypes")
            Class smClass =  Class.forName("de.frame4j.SendMail");
            @SuppressWarnings("unchecked")
            java.lang.reflect.Method mainMethod
                             = smClass.getMethod("main", String[].class);
            Object[] params = {argForw};
            mainMethod.invoke(null, params);
            return;
         } catch (Throwable e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("\n");
         }
         break;    
      case "ShowPorts":
      case "de.frame4j.io.ShowPorts":
         de.frame4j.io.ShowPorts.main(argForw);
         return;

      case "ShowProps":
      case "de.frame4j.ShowProps":
         de.frame4j.ShowProps.main(argForw);
         return;
      case "UCopy":
      case "de.frame4j.UCopy":
         de.frame4j.UCopy.main(argForw);
         return;
      case "Update":
      case "de.frame4j.Update":
         de.frame4j.Update.main(argForw);
         return;
      case "XMLio":
      case "de.frame4j.XMLio":
         de.frame4j.XMLio.main(argForw);
         return;
         
      case "ClientLil":
      case "de.frame4j.net.ClientLil":
         de.frame4j.net.ClientLil.main(argForw);
         return;
         
      case "RdGnPiGpioDBlink":
      case " de.weAut.demos.RdGnPiGpioDBlink":
          de.weAut.demos.RdGnPiGpioDBlink.main(argForw);
         return;
         
      case "TestOnPi":
      case " de.weAut.TestOnPi":
          de.weAut.TestOnPi.main(argForw);
         return;
    
         
      default:  // show  Exec unknown frame4j tool on console and graphically
         final String[] errorArg = {"-kk", "-wm ", // no buttons 50s
     "Exec unknown frame4j tool\n"
   + "Exec unbekanntes frame4j Programm\n\n"
   + "See available frame4j tools in de.frame4j package documentation\n"
   + "( weinert-automation.de/java/docs/frame4j/de/frame4j/package-summary.html).",
     "Close to proceed" };
         de.frame4j.AskAlert.main(errorArg);
         System.exit(17); // signal error  by return code 17
         return;
      } //  switch (progName)
      
   // show  Exec missing extension on console and graphically
      final String[] errorArg = {"-kk", "-wm ", // no buttons 50s
         "Exec: Java9+ missing extension for frame4j tool\n"
       + "de.frame4j.Exec: fehlende Eigenschaft bei Java 9+\n\n"
       + "See available frame4j tools in de.frame4j package documentation\n"
+ "( weinert-automation.de/java/docs/frame4j/de/frame4j/package-summary.html).",
         "Close to proceed" };
       de.frame4j.AskAlert.main(errorArg);
       System.exit(19); // signal error  by return code 19

   } // main(String[])
} // class Exec (19.11.2018, 23.11.2018, 12.03.2021)
