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

/** <b>Convenience starter of {@link de.frame4j.SVNkeys}</b>
 *   in the unnamed package. <br />
 *  <br />
 *  This class / application exists in spite of the (good) rule that no
 *  self respecting class shall dwell in the unnamed package. The only
 *  purpose of life is the snugness to type<br /> &nbsp; &nbsp;
 *  {@code java SVNkeys . -omitDirs CVS;doc-files;.svn}<br />
 *  instead of<br /> &nbsp; &nbsp;
 *  {@code java de.frame4j.SVNkeys . -omitDirs CVS;doc-files;.svn}<br />
 *  for example.<br />
 *  <br />
 *  As might be inferred this class' {@link #main(String[]) main()} just 
 *  delegates to main of the 
 *  &quot;real&quot; Frame4J tool in its right package. That's all.<br />
 *  <br />
 *  <a href=./de/frame4j/package-summary.html#co>&copy;</a> 
 *  Copyright 2009 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 44 $ ($Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $)
 *  @see de.frame4j.Del
 *  @see de.frame4j.Update
 *  @see de.frame4j.AskAlert
 *  @see de.frame4j.FuR
 *  @see de.frame4j.SVNkeysFilter
 *  @see de.frame4j.time.TimeHelper
 */
 // so far    V02.01 (20.05.2003) :  new
public final class SVNkeys {
   
   private SVNkeys(){} // no objects; no javaDoc 

/** Start forwarder for SVNkeys. <br />
 *  <br />
 *  Delegation to  {@link de.frame4j.SVNkeys}.<br />
 *  <br />
 *  @param  args command line parameter<br />
 *  @see de.frame4j.SVNkeys#main(String[])
 */
   public static void main(String[] args){
      de.frame4j.SVNkeys.main(args);
   } // main(String[])

} // Starter class
