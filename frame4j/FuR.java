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

/** <b>Convenience starter of {@link de.frame4j.FuR}</b>
 *   in the unnamed package. <br />
 *  <br />
 *  This class / application exists in spite of the (good) rule that no
 *  self respecting class shall dwell in the unnamed package. The only
 *  purpose of life is the snugness to type<br /> &nbsp; &nbsp;
 *  {@code java FuR   .\+.html     ../../&#47;pac     ../../pac   -r  -v}<br />
 *  instead of<br /> &nbsp; &nbsp;
 *  {@code java de.frame4j.FuR .\+.html ../..&#47;/pac ../../pac -r -v}<br />
 *  for example.<br />
 *  <br />
 *  As might be inferred this class' {@link #main(String[]) main()} just
 *  delegates to main of the &quot;real&quot; Frame4J tool in its right
 *  package. That's all.<br />
 *  As might as well be inferred from the above example that's a real use case
 *  to repair a Java6 javaDoc bug that sometimes generates
 *  ..&#47;/package within href= links much to the annoyance to link checkers 
 *  (in the best case).<br />
 *  <br />
 *  <a href=./de/frame4j/package-summary.html#co>&copy;</a> 
 *  Copyright 2009 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 *  @see de.frame4j.Del
 *  @see de.frame4j.Update
 *  @see de.frame4j.AskAlert
 *  @see de.frame4j.FuR
 *  @see de.frame4j.SVNkeys
 *  @see de.frame4j.time.TimeHelper
 *  @see de.frame4j.graf.DisplayPattern
 *  @see de.frame4j
 */
 // so far    V02.01 (20.05.2003 10:00) :  new
 //           V.010+ (03.02.2009 15:00  :  ported to Frame4J
 //           V.135+ (26.06.2015 11:03) : /**, while repairing FuR

public final class FuR {
   
   private FuR(){} // no objects; no javaDoc 

/** Start forwarder for FuR . <br />
 *  <br />
 *  Delegation to {@link de.frame4j.FuR}.<br />
 *  <br />
 *  @param  args command line parameter 
 *  @see de.frame4j.FuR#main(String[])
 */
   public static void main(String[] args){ de.frame4j.FuR.main(args); }
   
} // Starter class
