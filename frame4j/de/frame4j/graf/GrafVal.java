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
package de.frame4j.graf;

import java.awt.Font;
import java.awt.image.ImageObserver;
import java.util.concurrent.ConcurrentHashMap;

import de.frame4j.util.MinDoc;


/** <b>Common graphical constants</b>. <br />
 *  <br />
 *  This interface defines common final graphical values.<br />
 *  <br />
 *  The final values for colours are intentionally lower case (against
 *  Java style rules), to be compatible with (German) &quot;DIN&quot; standard
 *  two letter abbreviations used in industrial applications: rt, gn, gr,
 *  and so on. English two letters are supplemented as long as unambiguous 
 *  (rt = rot aka rd = red; ge ye; ..). <br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2003, 2004 &nbsp; Albrecht Weinert 
 */
 // so far    V00.00 (15:13 28.04.2000) :  ex weinertBib 
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V02.20 (20.07.2004 11:47) :  rosa and indigo
 //           V02.21 (02.09.2004 15:19) :  bl (blue, blau) added avoid bl.ack
 //           V02.24 (17.02.2005 11:35) :  /**, shortened
 //           V.o56+ (26.01.2009 15:06  :  1st translation
 //           V.  33 (23.03.2021) : now extending ColorVal
 // V. $Revision: 33 $ ($Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $)

@MinDoc(
   copyright = "Copyright 1998 - 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
//   usage   = "static import",  
   purpose = "common final values for graphics"
) public interface GrafVal extends ColorVal {
   
/** ERROR or ABORT from ImageObserver. */
   int A_E   = ImageObserver.ERROR | ImageObserver.ABORT;

/** WIDTH or HIGHT (info available) from ImageObserver. */
   int H_W   = ImageObserver.WIDTH | ImageObserver.HEIGHT;
   

/** HashMap for fonts. <br />
 *  <br />
 *  Used to avoid multiple decoding and generating.<br />
 *  Keys are preferably lower case.<br /> 
 *  Filled / checked by {@link  GrafHelper#getFont(CharSequence)} and
 *  {@link  GrafHelper#getFont(CharSequence, int, int)}.<br />
 *  <br /> 
 */
   public static final ConcurrentHashMap<String, Font> fontCmap 
                                     = new ConcurrentHashMap<>();   

   /** Standard font for Elements. <br />
 *  <br />
 *  sansSerif, 12.
 */
   public static final Font STD_FONT =
                        GrafHelper.getFont("SansSerif", Font.PLAIN, 12);

/** Standard font for buttons. <br />
 *  <br />
 *  sansSerif, 12, bold.
 */
   public static final Font STD_B_FONT =
                        GrafHelper.getFont("SansSerif", Font.BOLD, 12);


}  // GrafVal (24.04.2003, 26.01.2009)


