/*  Copyright 2019 Albrecht Weinert, Bochum, Germany (a-weinert.de)
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


/** <b>Convenience starter of {@link de.frame4j.demos.TvH}</b>
 *   in the unnamed package. <br />
 *  <br />
 *  This class / application exists in spite of the (good) rule that no
 *  self respecting class shall dwell in the unnamed package. The only
 *  purpose of life is the snugness to type<br /> &nbsp; &nbsp;
 *  {@code java TvH}<br />
 *  instead of<br /> &nbsp; &nbsp;
 *  {@code java dde.frame4j.demos.TvH}<br />
 *  for example.<br />
 *  <br />
 *  As might be inferred this class' {@link #main(String[]) main()} just 
 *  delegates to main of the &quot;real&quot; Frame4J demo / game 
 *  in its right package. That's all.<br />
 *  <br />
 *  <br />
 *  Copyright 1998 - 2019  &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 *  @see de.frame4j.demos.TvH
 */
 // so far    V.14 09.03.2019 : new

public final class TvH {
   
   private TvH(){} // no objects; no javaDoc 
   
/** Start the application. <br />
 *  <br />
 *  @param args start parameters  (optional alternative background image)
 */
   public static void main(String[] args){
      de.frame4j.demos.TvH.main(args);
   }  // main(String[])
} // class TvH (09.03.2019)

