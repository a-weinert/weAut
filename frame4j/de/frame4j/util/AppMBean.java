/*  Copyright 2021 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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

/** <b>Frame4J application as MBean</b>. <br />
 *  <br />
 *  This interface describes the actions and properties an App (inheritor) 
 *  can expose e.g. via JConsole. An interface named TheAppInheritorMBean may
 *  extend this interface. <br />
 *  <br />
 *  Copyright  &copy;  2021  Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 42 $ ($Date: 2021-05-01 18:54:54 +0200 (Sa, 01 Mai 2021) $)
 */
// so far:   V.  36  (07.04.2021) : new, replace AppMBean.xml
//           V.  41  (04.04.2021) : registration method put here   

public interface AppMBean {
  
/** Stop the application. <br />
 *  <br />
 *  This method shall gracefully end this application. It will set the 
 *  {@link de.frame4j.util.App#isRunFlag() runFlag} to false.
 *  @see App#stop()
 */   
  public void stop();
  
/** The application's start up time as nationalised Text. <br />
 *  <br />
 *  @see de.frame4j.util.App#getStartTime()
 *  @return the application's start time
 */
   public String getStartTime();

/** The start parameters. <br />
 *  <br />
 *  This method returns the originally supplied start parameters (see
 *  {@link de.frame4j.util.App#args args}) space separated (as from the
 *  shell's command line).<br />
 *  <br />
 *  @see de.frame4j.util.App#getArgs()
 *  @see TextHelper#prepParams(String[])
 *  @return the application's start parameters
 */
   public String getArgs();
   
/** Detailedness of reports or logging. */   
   public String getVerbose();
   
/** Get the short (2 character) notation of the user's language. <br />*/
   public String getLanguage();
   
/** The actual (system) time as text. */
   public String getActTime();
   
/** The application's execution time (milliseconds) so far. <br />
 * 
 * @return Elapsed (not CPU) time in (Long) milliseconds since startup;
 * @see de.frame4j.util.App#getExecTimeMsL()
 */
   public Long getExecTimeMsL();


/** The copyright notice. <br />
 *     
 * @see de.frame4j.util.App#getCopyright()
 */
   public String getCopyright();

/** The purpose of this application. <br />
 *  
 *  @see de.frame4j.util.App#getPurpose()
 */
   public String getPurpose();

/* * A (short) description of this application (about text). <br />
 *
 *  @see de.frame4j.util.App#getAboutText()
 */
//   public String getAboutText();
   
/** The program's version and revision date. <br /> */    
   public String getVersDate();

/** The program's name. */     
   public String getName();
  
} // AppMBean
