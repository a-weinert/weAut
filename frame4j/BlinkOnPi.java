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

/** <b>Convenience starter of {@link  de.weAut.demos.BlinkOnPi}</b>
 *   in the unnamed package. <br />
 *  <br />
 *  This class / application exists in spite of the (good) rule that no
 *  self respecting class shall dwell in the unnamed package. The only
 *  purpose of life is the snugness to type<br /> &nbsp; &nbsp;
 *  {@code java BlinkOnPi}<br />
 *  instead of<br /> &nbsp; &nbsp;
 *  {@code java  de.weAut.demos.BlinkOnPi}<br /> for example.<br />
 *  <br />
 *  As might be inferred this class' {@link #main(String[]) main()} just 
 *  delegates to main of the &quot;real&quot; Frame4J demo in its right 
 *  package. That's all.<br />
 *  <br />
 *  <br />
 *  Copyright 2021  &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 41 $ ($Date: 2021-04-23 20:44:27 +0200 (Fr, 23 Apr 2021) $)
 *  @see de.frame4j.demos.ComplDemo
 */
 // so far    V.14 09.03.2019 : new

public final class BlinkOnPi {
   
   private BlinkOnPi(){} // no objects; no javaDoc 
   
/** Start the application. <br />
 *  <br />
 *  @param args start parameters  (irrelevant)
 */
   public static void main(String[] args){
       de.weAut.demos.BlinkOnPi.main(args);
   }  // main(String[])
   
} // class BlinkOnPi 19.04.2021)

