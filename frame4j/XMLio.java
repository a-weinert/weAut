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

import de.frame4j.util.App;
import de.frame4j.util.Prop;

/** <b>Convenience starter of {@link de.frame4j.XMLio de.frame4j.XMLio}
 *  <br />
 *  This class / application exists in spite of the (good) rule that no
 *  self respecting class shall dwell in the unnamed package. The only
 *  purpose of life is the snugness to type<br /> &nbsp; &nbsp;
 *  {@code java XMLio  in.xml out.xml -trans trans.xsl -nv }<br />
 *  instead of<br /> &nbsp; &nbsp;
 *  {@code java de.frame4j.XMLio  in.xml out.xml -trans trans.xsl -nv }<br />
 *  for example.<br />
 *  <br />
 *  As might be assumed this class' {@link #main(String[]) main()} just
 *  delegates to main of the 
 *  &quot;real&quot; Frame4J tool in its right package. That's all.<br />
 *  <br />
 *  <a href=./de/frame4j/package-summary.html#co>&copy;</a> 
 *  Copyright 2009 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 *  @see de.frame4j.Del
 *  @see de.frame4j.XMLio
 *  @see de.frame4j.AskAlert
 *  @see de.frame4j.FuR
 *  @see de.frame4j.SVNkeys
 *  @see  App
 *  @see  Prop
 */
 // so far    V.o80+ (06.03.2009 11:22  :  new in Frame4J

public final class XMLio {
   
   private XMLio(){} // no objects; no javaDoc 

/** Start forwarder for XMLio . <br />
 *  <br />
 *  Delegation to  {@link de.frame4j.XMLio}.<br />
 *  <br />
 *  @param  args command line parameter<br />
 *  @see de.frame4j.XMLio#main(String[])
 */
   public static void main(String[] args){
      de.frame4j.XMLio.main(args);
   } // main(String[])

} // Starter class
