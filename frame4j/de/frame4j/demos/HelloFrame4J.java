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
package de.frame4j.demos;

import java.io.PrintWriter;

import de.frame4j.io.TeeWriter;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.MinDoc;
import de.frame4j.util.Prop;
import de.frame4j.util.PropMapHelper;
import de.frame4j.text.TextHelper;

/** <b>Hello World as Frame4J application</b>. <br />
 *  <br />
 *  This application is just greeting. It's effect is very like the notorious
 *  HelloWorld.java, usually doing <br /> &nbsp; &nbsp;<code>
 *   {@link java.lang.System#out 
 *    System.out}.{@link java.io.PrintStream#println(java.lang.String) 
 *        println(&quot;Guten Tag, Welt!&quot;)}</code><br />
 *  <br />
 *  This application {@code HelloFrame4J} demonstrates, that by usage of a 
 *  good framework like <a href="../package-summary.html">Frame4J</a>, you go
 *  farther by orders of magnitude with virtually no extra programming 
 *  efforts.<br />
 *  <br />
 *  To judge the compactness of the 
 *  <a href="./doc-files/HelloFrame4J.java">source code</a>, see it without
 *  the comments (including this one), with no {@link MinDoc} annotation and 
 *  without the &quot;luxury&quot; of the  {@link #showDateTime switchable}
 *  showing of application start time.<br />
 *  <br />
 *  With quite little extra effort (compared to a standard &quot;naked&quot;
 *  HellWorld in this toy example) one gets as Frame4J's gift<ul>
 *  <li> (inter) nationalising,</li>
 *  <li> help output,<br />
 *  <li> comfortable evaluation and checking of command line parameters and
 *       configuration (.properties) files,</li>
 *  <li> provision of {@link PrintWriter}s {@link App#log log} and
 *       {@link App#out out} (featuring the correct encoding even for the
 *       Windows console / command shell), 
 *  <li> optional output branching of {@link App#log log} and
 *       {@link App#out out}) to (log) files (see
 *       {@link TeeWriter TeeWriter}),</li>
 *  <li> and, and and ... </li>
 *  </ul>
 *  The spell word for all this is just {@link App  extends App}.<br />
 *  <br />
 *  Insofar this toy applications's ({@link HelloFrame4J}'s)
 *  <a href="./doc-files/HelloFrame4J.java">source</a> can be (and was very
 *  often) the starting point for real world Java applications. This procedure
 *  gave birth to server applications (based on Frame4J's predecessor) running
 *  for years in uninterrupted 7d24h service.<br />
 *  <br />
 *  <br />
 *  <b>Hint on .properties</b>: To this application (source: 
 *  <a href="./doc-files/HelloFrame4J.java">HelloFrame4J.java</a>)
 *  belongs (as integral part) a .properties file named <a
 *  href="./doc-files/HelloFrame4J.properties">HelloFrame4J.properties</a>.
 *  <br />
 *  An extra properties file <a
 href="./doc-files/HelloFrame4J_pl.properties">HelloFrame4J_pl.properties</a>
 *  demonstrates the schema to bring extra languages in. In this case
 *  of Polish (Cze&#347; &#263;&#347;wiat!) setting<br />
 *   &nbsp; {@code chcp 1250 }<br /> 
 *   &nbsp; {@code set activeCodePage=Cp1250 }<br /> 
 *  might be necessary to display the greeting correctly. (The usual Cp850
 *  will fail.)<br />
 *  For the the handling of console font sets or &quot;code pages&quot; for
 *  Window's command shell see the script 
 *  <a href="./doc-files/cp.bat">cp.bat</a> as an example.<br />
 *  <br />
 *  <u>All</u> public fields (non inherited public object variables) of 
 *  a {@link HelloFrame4J} object are properties, controlling the
 *  application's working mode. They may be (automatically) set by
 *  {@link Prop}.<br />
 *  <br />
 *  <br />
 *  <a href="../package-summary.html#co">&copy;</a> 
 *  Copyright 2005, 2009 &nbsp; Albrecht Weinert<br /> 
 *  <br />
 *  @see      App
 *  @see      Prop
 */
 // so far    V02.00 (06.06.2005) :  new, empty application prototype 
 //           V.180+ (11.01.2010) :  (on Frame4J now)

@MinDoc(
   copyright = "Copyright 2005, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 39 $",
   lastModified   = "$Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application (-? for help)",  
   purpose = "a demo and introductory application on Frame4J (the hello)"
) public class HelloFrame4J  extends App {
  
/** HelloFrame4J wants only partial parameter parsing by Prop. */
  @Override public final boolean parsePartial(){ return true; }

   
/** Show also date and time. <br />
 *  <br />
 *  Set according to <a
 href="./doc-files/HelloFrame4J.properties">HelloFrame4J.properties</a>.<br />
 *  <br />
 *  default: true<br />
 *  <br />
 *  @see Prop#setFields(Object)
 *  @see PropMapHelper#setField(Object, String, String)
 */
    public boolean showDateTime = true;
    
/** The first normal (non -option) or word parameter. <br /> */    
    public String param1;


/** HelloFrame4J's start method. <br />
 *  <br />
 *  This is the static standard start for an application (forced on us by 
 *  the Java standard). In the case of a Frame4J based application extending 
 *  {@link App} directly or indirectly it just creates an object of its own
 *  {@link App} derived class and calls a suitable 
 *  {@link #go(String[]) go()} method.<br />
 *  The &quot;real work&quot; is done in {@link #doIt()}.<br />
 *  <br /> 
 *  Exit code  0: successfully run.<br />
 *  Exit code {@link App#INIT_ERROR}: 
 *         start or parameter evaluation problem.<br />
 *  Exit &gt; 0: problems<br />
 *  @param args the command line arguments 
 *         passed by the platform to the JVM (java.exe), here just forwarded
 */
   public static void main(String[] args){
      try {
         new HelloFrame4J().go(args);  // "make'n go" (App inheritor)
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR); // usually illegal start parameters
      }
   } // main(String[])


/** HelloFrame4J's working method. <br />
 *  <br />
 *  Here the world is greeted.<br />
 *  <br />
 *  Available languages are: de, en, fr, it, pt, es, nl, da<br />
 *  and for this demo pl by grace of a supplementary <a
 href="./doc-files/HelloFrame4J_pl.properties">HelloFrame4J_pl.properties</a>.<br />
 *  <br />
 *  Optionally one gets a start up report with time and date (avoidable by
 *  -noTime). For test or demo (switched on by -debug) a list of all 
 *  properties, as set by loaded .properties files and command line 
 *  parameters, will be output first.<br />
 */
   @Override public int doIt(){
      log.println();
      if (verbose || showDateTime) { // version, start time
          log.println();
          log.println(twoLineStartMsg().append('\n'));
      } // optional version, start time (nationalised)
      
      if (isDebug()) {
         log.println("\n" + TextHelper.format(null, "Parameter: ", args, -1));
      } else if (param1 != null) {
         log.println("\n   Hello "  + param1 + "!\n"); 
      }
      
      log.println("\n   "  + valueLang("hellWo")); 
      log.println();  //   ^  this is the "task" of a hello world application
      return 0; 
   } //  doIt        

} // class HelloFrame4J (15.04.2004 as HelloFW, 24.02.2009)

