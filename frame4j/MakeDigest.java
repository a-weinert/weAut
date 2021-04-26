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

/** <b>Convenience starter of {@link de.frame4j.MakeDigest}</b>
 *   in the unnamed package. <br />
 *  <br />
 *  This class / application exists in spite of the (good) rule that no
 *  self respecting class shall dwell in the unnamed package. The only
 *  purpose of life is the snugness to type<br /> &nbsp; &nbsp;
 *  {@code java MakeDigest -md2 myPassWord2digest -append toMysql.txt}<br />
 *  instead of<br /> &nbsp; &nbsp;
 {@code java de.frame4j.MakeDigest -md2 myPassWord2digest -append toMysql.txt}
 *  <br /> for example.<br />
 *  <br />
 *  As might be inferred this class' {@link #main(String[]) main()} method
 *  just delegates to main of the &quot;real&quot;
 *  Frame4J tool in its right package. That's all.<br />
 *  <br />
 *  <a href=./de/frame4j/package-summary.html#co>&copy;</a>
 *  Copyright 2010 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 40 $ ($Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $)
 *  @see de.frame4j.Del
 *  @see de.frame4j.Update
 *  @see de.frame4j.MakeDigest
 *  @see de.frame4j.AskAlert
 *  @see de.frame4j.FuR
 *  @see de.frame4j.SVNkeys
 *  @see de.frame4j
 */
 // so far    V02.01 (20.05.2003 10:00) :  new
 //           V.o10+ (03.02.2009 15:00  :  ported to Frame4J

public final class MakeDigest {

   private MakeDigest(){} // no objects; no javaDoc

/** Start forwarder for MakeDigest. <br />
 *  <br />
 *  Delegation to  {@link de.frame4j.MakeDigest}.<br />
 *  <br />
 *  @param  args command line parameter<br />
 *  @see de.frame4j.Update#main(String[])
 */
   public static void main(String[] args){
      de.frame4j.MakeDigest.main(args);
   } // main(String[])

} // Starter class
