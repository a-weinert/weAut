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


import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import de.frame4j.util.MinDoc;

/** <b>weinert-automations's Logo</b>. <br />
 *  <br />
 *  This class or features the logo of <br />
 *  &nbsp; &nbsp; <a href="https://weinert-automation.de/">weinert-automation</a><br />
 *  (weAut for short), Prof. Dr. Weinerts firm for development and 
 *  consulting.<br />
 *  <a href="./doc-files/weaut_logo_acr120t.png" target="_top"><img
 *  src="./doc-files/weaut_logo_acr120.png" width="160" height="120" 
 *  style="border:0; margin: 17px 62px; textalign:right;"
 *  alt="weinert-automation"
 *  title="weinert-automation"
 *  /></a>
 *  The Logo is featured as<ul>
 *  <li>as small (32*32) Image or &quot;tiny icon&quot;,<br />
 *      &nbsp; &nbsp;</li></ul>
 *  
 *  The icon is a drawing of the depicted Logo, minified to quadratic 
 *  shape.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2000 - 2009, 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 */
 // so far    V00.00 11.06.2000:  new
 //           V02.00 24.04.2003: CVS Eclipse
 //           V02.21 02.03.2005: No ImageInfo in getIcon()
 //           V.o52+ 21.01.2009: ported; 2 interfaces, singleton
 //           V.o10  11.01.2019: simplified from WeAutLogo

@MinDoc(
   copyright = "Copyright 2000 - 2009, 2019  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 39 $",
   lastModified   = "$Date: 2021-04-17 21:00:41 +0200 (Sa, 17 Apr 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "get as image",  
   purpose = "weinert-automation's Logo, the graphics"
) public final class WeAutLogo { 
   
/** No docu, no objects. */   
   private WeAutLogo() {}

/** Background alpha.<br />
 *  <br />
 *  The logos are watercolour respectively acrylic  painted on white 
 *  background wich is too eye-catching. Hence, white with a little
 *  transparency.   
 */
   private static final int   Bac___ounD =  0xCFFFFFFF;
   
/*  * Background fully transparent. *  /   
   private static final int TR______NT =  0X00000000;   xxxxx */
   
   
/** The logo's pixel data. <br />
 *  <br />
 *  They correspond to  weAutIco_PN2.gif (32*32 pixel) painted 
 *  on a paint.net canvas.<br />
 */
   private static int[] iconPixels = {
// weAutIco_PN3_gif.txt [32*32]
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 0,0
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 0,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 0,16
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 0,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 1,0
   0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 1,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000,  // 1,16
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 1,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD,  // 2,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 2,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 2,16
   0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 2,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD,  // 3,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 3,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 3,16
   0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 3,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 4,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 4,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 4,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 4,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 5,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 5,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFD6D6DB, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 5,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 5,24
   0X00000000, 0X00000000, 0XFF2615AD, 0XFFD6D6DB, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 6,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 6,8
   0XFFDDDDDD, 0XFFD6D6DB, 0XFF1400AE, 0XFFC4C1D6, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 6,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000,  // 6,24
   0X00000000, 0X00000000, 0XFF493AB9, 0XFFAAA5D1, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 7,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 7,8
   0XFFDDDDDD, 0XFFB7B3D4, 0XFF1F0CB0, 0XFF8E86CA, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 7,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000,  // 7,24
   0X00000000, 0X00000000, 0XFF776DC5, 0XFF776DC5, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 8,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 8,8
   0XFFDDDDDD, 0XFF847BC8, 0XFF4B3CBA, 0XFF493AB9, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 8,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000,  // 8,24
   0X00000000, 0XFFDDDDDD, 0XFFA7A2D0, 0XFF4C3EBB, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 9,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 9,8
   0XFFDDDDDD, 0XFF584CBD, 0XFF948CCB, 0XFF1D09AF, 0XFFCDCBD9, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 9,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000,  // 9,24
   0X00000000, 0XFFDDDDDD, 0XFFD3D2DA, 0XFF200CB0, 0XFFDAD9DC, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 10,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 10,8
   0XFFDDDDDD, 0XFF2614B1, 0XFFC9C8D8, 0XFF5244BC, 0XFF8E86CA, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 10,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000,  // 10,24
   0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF3929B6, 0XFFB7B3D4, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 11,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 11,8
   0XFFBDBAD5, 0XFF3322B4, 0XFFDDDDDD, 0XFF948CCB, 0XFF493AB9, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 11,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFFDDDDDD, 0X00000000,  // 11,24
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF6B60C2, 0XFF847BC8, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 12,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 12,8
   0XFF9089CB, 0XFF5F52BF, 0XFFDDDDDD, 0XFFD3D2DA, 0XFF1D09AF, 0XFFCDCBD9, 0XFFDDDDDD, 0XFFDDDDDD,  // 12,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFFDDDDDD, 0XFFDDDDDD,  // 12,24
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF9A93CD, 0XFF5244BC, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 13,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 13,8
   0XFF5F52BF, 0XFF9089CB, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF5244BC, 0XFF8E86CA, 0XFFDDDDDD, 0XFFDDDDDD,  // 13,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFFDDDDDD, 0XFFDDDDDD,  // 13,24
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFC9C8D8, 0XFF2614B1, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 14,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 14,8
   0XFF2F1FB4, 0XFFC3C1D7, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF948CCB, 0XFF493AB9, 0XFFDDDDDD, 0XFFDDDDDD,  // 14,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFFDDDDDD, 0XFFDDDDDD,  // 14,24
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF2F1FB4, 0XFFBDBAD5, 0XFFDDDDDD, 0XFFDDDDDD,  // 15,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFC9C8D8,  // 15,8
   0XFF2614B1, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFD3D2DA, 0XFF1D09AF, 0XFFCDCBD9, 0XFFDDDDDD,  // 15,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFFDDDDDD, 0XFFDDDDDD,  // 15,24
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF5F52BF, 0XFF8E86CA, 0XFFDDDDDD, 0XFFDDDDDD,  // 16,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF9790CD,  // 16,8
   0XFF2610AD, 0XFF2610AD, 0XFF2610AD, 0XFF2610AD, 0XFF2610AD, 0XFF2610AD, 0XFF8E86CA, 0XFFDDDDDD,  // 16,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF2610AD, 0XFF2610AD, 0XFF2610AD, 0XFF2610AD, 0XFF2610AD,  // 16,24
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF8A82C9, 0XFF5F52BF, 0XFFDDDDDD, 0XFFDDDDDD,  // 17,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFD0CFD9, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF685DC1,  // 17,8
   0XFF887FC9, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF948CCB, 0XFF493AB9, 0XFFDDDDDD,  // 17,16
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFFDDDDDD, 0XFFDDDDDD,  // 17,24
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFBDBAD5, 0XFF2D1BB3, 0XFFDDDDDD, 0XFFDDDDDD,  // 18,0
   0XFFDDDDDD, 0XFFCDCBD9, 0XFF1400AE, 0XFFC1BED6, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF3929B6,  // 18,8
   0XFFB7B3D4, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFD3D2DA, 0XFF1D09AF, 0XFFCDCBD9,  // 18,16
   0XFF2610AD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFFDDDDDD, 0XFFDDDDDD,  // 18,24
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF200CB0, 0XFFC9C8D8, 0XFFDDDDDD,  // 19,0
   0XFFDDDDDD, 0XFF948CCB, 0XFF220FB0, 0XFF8E86CA, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFD0CFD9, 0XFF200CB0,  // 19,8
   0XFFD6D6DB, 0XFF9790CD, 0XFF1400AE, 0XFF9790CD, 0XFFD6D6DB, 0XFFDDDDDD, 0XFF5244BC, 0XFF8E86CA,  // 19,16
   0XFF2610AD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF2610AD, 0XFF1400AE, 0XFFDDDDDD, 0XFFDDDDDD,  // 19,24
   0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF5244BC, 0XFF9790CD, 0XFFDDDDDD,  // 20,0
   0XFFDDDDDD, 0XFF5244BC, 0XFF5C4EBE, 0XFF493AB9, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF9D97CE, 0XFF493AB9,  // 20,8
   0XFF3625B5, 0XFF1400AE, 0XFF776DC5, 0XFF493AB9, 0XFF3625B5, 0XFFC3C1D7, 0XFF948CCB, 0XFF493AB9,  // 20,16
   0XFF2610AD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF2610AD, 0XFF1400AE, 0XFFDDDDDD, 0X00000000,  // 20,24
   0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF7E75C6, 0XFF6B60C2, 0XFFDDDDDD,  // 21,0
   0XFFD3D2DA, 0XFF1906AF, 0XFFBDBAD5, 0XFF1D09AF, 0XFFCDCBD9, 0XFFDDDDDD, 0XFF7267C3, 0XFF2917B2,  // 21,8
   0XFF8178C7, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF8178C7, 0XFF3C2CB7, 0XFFD0CFD9, 0XFF1400AE,  // 21,16
   0XFF2610AD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF2610AD, 0XFF2610AD, 0XFFD6D6DB, 0X00000000,  // 21,24
   0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFB0ACD2, 0XFF3929B6, 0XFFDDDDDD,  // 22,0
   0XFF948CCB, 0XFF4334B8, 0XFFDDDDDD, 0XFF5244BC, 0XFF8E86CA, 0XFFDAD9DC, 0XFF2C1AB2, 0XFF2311B1,  // 22,8
   0XFF776DC5, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF9089CB, 0XFF2D1CB3, 0XFFDDDDDD, 0XFF5042BC,  // 22,16
   0XFF2610AD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFF2610AD, 0XFFDDDDDD, 0X00000000,  // 22,24
   0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFD6D6DB, 0XFF1906AF, 0XFFD0CFD9,  // 23,0
   0XFF5244BC, 0XFF847BC8, 0XFFDDDDDD, 0XFF948CCB, 0XFF493AB9, 0XFF867EC8, 0XFF1400AE, 0XFF1400AE,  // 23,8
   0XFF1400AE, 0XFF1400AE, 0XFF1400AE, 0XFF1400AE, 0XFF1400AE, 0XFF1400AE, 0XFFDDDDDD, 0XFF8E86CA,  // 23,16
   0XFF1400AE, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFF2610AD, 0X00000000, 0X00000000,  // 23,24
   0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF4537B9, 0XFF9D96CD,  // 24,0
   0XFF1906AF, 0XFFC6C4D7, 0XFFDDDDDD, 0XFFD3D2DA, 0XFF1D09AF, 0XFF9C95CD, 0XFF2614B1, 0XFF4F40BA,  // 24,8
   0XFF776DC5, 0XFF776DC5, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFC6C4D7,  // 24,16
   0XFF1400AE, 0XFFD3D2DA, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF1400AE, 0XFF2610AD, 0X00000000, 0X00000000,  // 24,24
   0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF7267C3, 0XFF4F42BB,  // 25,0
   0XFF4334B8, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF5244BC, 0XFF5042BC, 0XFF5C4EBE, 0XFF1400AE,  // 25,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 25,16
   0XFF5548BC, 0XFF847BC8, 0XFFDDDDDD, 0XFFC6C4D7, 0XFF2614B0, 0XFF5548BC, 0X00000000, 0X00000000,  // 25,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFA19BCF, 0XFF220FB0,  // 26,0
   0XFF847BC8, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF948CCB, 0XFF220FB0, 0XFFA49FCF, 0XFF3C2CB7,  // 26,8
   0XFF847BC8, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFF847BC8, 0XFFDDDDDD, 0XFFDAD9DC, 0XFFDDDDDD,  // 26,16
   0XFFBAB6D4, 0XFF2614B1, 0XFFC0BDD6, 0XFF2610AD, 0XFF2B16AC, 0XFF726D92, 0X00000000, 0X00000000,  // 26,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFC9C8D8, 0XFF1400AE,  // 27,0
   0XFFC6C4D7, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFD3D2DA, 0XFF1400AE, 0XFFC9C8D8, 0XFFBDBAD5,  // 27,8
   0XFF2F1FB4, 0XFF1400AE, 0XFF1400AE, 0XFF4F41BB, 0XFF2F1FB4, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 27,16
   0XFFDDDDDD, 0XFF948CCB, 0XFF2917B3, 0XFF493DAC, 0XFF676095, 0XFF2F1CA7, 0X00000000, 0X00000000,  // 27,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFC4C1D7,  // 28,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFC9C8D8, 0XFFDDDDDD, 0XFFDDDDDD,  // 28,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 28,16
   0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000, 0XFF7A7982, 0X00000000, 0X00000000,  // 28,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD,  // 29,0
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 29,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 29,16
   0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 29,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 30,0
   0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 30,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000,  // 30,16
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 30,24
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 31,0
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD,  // 31,8
   0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0XFFDDDDDD, 0X00000000, 0X00000000, 0X00000000, 0X00000000,  // 31,16
   0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000, 0X00000000   // 31,24                                   };
 }; //  int[] iconPixels
   
   
/** weinert-automation's logo as icon (Image). <br /> */
   private static volatile Image iconImage;

/** weinert-automation's logo as icon; dimension; width = height. <br />
 *  <br />
 *  Value: {@value}<br />
 */
   static public final int iconWH = 32;

/** weinert-automation's logo as icon (Image). <br />
 *  <br />
 *  This method returns a tiny image of weinert-automation's logo on white
 *  background. The dimension is {@link #iconWH}*{@link #iconWH}.<br />
 *  <br />
 *  The returned {@link Image} is suitable to be set as a
 *  {@link java.awt.Frame Frame}'s icon.<br />
 *  <br />
 *  @see java.awt.Frame#setIconImage(java.awt.Image)
 */
   static public Image getIcon(){
      if (iconImage != null) return iconImage;
      synchronized (WeAutLogo.class)  {
         if (iconImage != null) return iconImage;
         iconImage =  new BufferedImage(iconWH, iconWH,
                                 BufferedImage.TYPE_INT_ARGB); 
         ((BufferedImage)iconImage).setRGB(0, 0, iconWH, iconWH,
                                 iconPixels, 0, iconWH);
         iconPixels = null; // no garbage
      } // sync
      return iconImage;
   } // getIcon()

                        
/** The logo's pixel data. <br />
 *  <br />
 *  They correspond to weaut_icon_acr32t.png (32*32 pixel).<br />
 */                       
   private static int[] iconPixels2 = {
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFFBFBFB, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080, 0XFF808080,
   0XFF808080, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   0XFF808080, 0XFFAFB4BE, 0XFF808080, 0XFF808080, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFF808080, 0XFFA9ADB8, 0XFFBDB6BB, 0XFF808080,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080, 0XFF808080, 0XFFADB3BD,
   0XFFC4C0C5, 0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080,
   0XFFCECED1, 0XFFD7DDE5, 0XFFC9CACF, 0XFFB83D40, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFF808080, 0XFFB1B4BC, 0XFFFAFBFC, 0XFFD2D3D7, 0XFFB83D40,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFF1F1F2,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080, 0XFFA5AAB7, Bac___ounD,
   0XFFDEE0E3, 0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFA5AAC1, 0XFFEEEEEE, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080,
   0XFFADB2BD, Bac___ounD, 0XFFE6E7E9, 0XFFBD4C4E, 0XFFB83D40, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFFA3A5B7, 0XFFD5D5D6, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFF808080, 0XFFBAC1CB, Bac___ounD, 0XFFEFF0F2, 0XFFAB6C6F,
   0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFA6A5B2,
   0XFFC9C9CC, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFF808080, 0XFF808080, 0XFFEEF1F6, Bac___ounD,
   0XFFFAFBFB, 0XFFA85962, 0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFA4A6B5, 0XFFC9CBD1, Bac___ounD, 0XFFD9E2F2, 0XFFD4D8E5,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFBDBDBD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080, 0XFFAEB3BD,
   0XFFFEFEFF, Bac___ounD, Bac___ounD, 0XFFA86169, 0XFFB83D40, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFFA1A1AE, 0XFFCBC9C9, Bac___ounD,
   0XFF787EA0, 0XFF8587AD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF929292,
   0XFF818181, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   0XFF808080, 0XFFA4A9B4, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFA77179,
   0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFFEFEFD, 0XFF817C86,
   0XFF746E70, 0XFFCECECF, 0XFF414873, 0XFF626593, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   0XFFFDFDFD, 0XFF808080, 0XFF808080, 0XFF808080, 0XFFFEFEFE, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFF808080, 0XFF6C707D, 0XFF977878, 0XFFC4AAA7,
   0XFFD2BDBA, 0XFF8F6263, 0XFFB83D40, Bac___ounD, Bac___ounD, 0XFFFBFBFB,
   0XFF464962, 0XFF31323D, 0XFF313341, 0XFF444650, 0XFF303C7F, 0XFF5D5968,
   0XFFFCFCFC, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFF808080, 0XFF808080, 0XFF90949F, 0XFF7E8493,
   0XFF808080, 0XFF808080, 0XFFEFEFF3, Bac___ounD, 0XFF808080, 0XFF343743,
   0XFF363A46, 0XFF3A3940, 0XFF37313A, 0XFF493636, 0XFFB83D40, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFFE4E4E7, 0XFF978487, 0XFFC4BFC0, 0XFFF6F6F6,
   0XFF38457E, 0XFF4D4347, 0XFFF6EDED, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080, 0XFF808080, 0XFFC4C6CA,
   Bac___ounD, Bac___ounD, 0XFFE4E8EF, 0XFF808080, 0XFF808080, 0XFFF8F8F7,
   0XFF808080, 0XFF898E96, 0XFFC1C1C4, 0XFFC8C9CB, 0XFFC2C0C2, 0XFF967378,
   0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF9B7170,
   0XFFF6F5F4, Bac___ounD, 0XFF576188, 0XFF4C4C4E, 0XFFD5ABA9, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080,
   0XFF6B7084, 0XFFF9F9F9, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFFBFBFB,
   0XFF808080, 0XFFBFBFC0, 0XFF808080, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFA78B90, 0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFF8E5959, Bac___ounD, Bac___ounD, 0XFF7981A3, 0XFF42434D,
   0XFFCCAEA8, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFF808080, 0XFF626777, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFF808080, 0XFFF9F9FA, 0XFF808080, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFFB4979A, 0XFFB83D40, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFF7E6065, Bac___ounD, Bac___ounD,
   0XFFA9ADAE, 0XFF454562, 0XFFB59B94, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFF808080, 0XFF808080, 0XFF7F828D, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080, 0XFFF9F9F9,
   0XFF808080, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFBEA9AC,
   0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFF9F4F4, 0XFF5C464F,
   0XFFFEFEFE, Bac___ounD, 0XFFD7DACA, 0XFF4E4145, 0XFF9A7971, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080, 0XFFC1C5CF,
   0XFFA5A8B1, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080,
   0XFF8A888B, 0XFF898989, 0XFF808080, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFC2B6BB, 0XFFB83D40, Bac___ounD, Bac___ounD, Bac___ounD,
   0XFFF3E7E8, 0XFF5A4044, 0XFFFEFEFE, Bac___ounD, 0XFFECEEE5, 0XFF675448,
   0XFF9B7971, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   0XFF808080, 0XFF8D92A1, 0XFFC4C6CA, Bac___ounD, Bac___ounD, Bac___ounD,
   0XFF808080, 0XFF808080, 0XFFF7F6F6, 0XFF898989, 0XFF808080, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFFC5C3C8, 0XFFB83D40, 0XFFFBF2F2,
   Bac___ounD, Bac___ounD, 0XFFF4E9E9, 0XFF64484D, Bac___ounD, Bac___ounD,
   0XFFFCFCFC, 0XFF80735D, 0XFF9A7D79, Bac___ounD, Bac___ounD, 0XFFFAFAFB,
   0XFFFCFCFC, Bac___ounD, 0XFF808080, 0XFF808080, 0XFF808080, 0XFF808080,
   0XFF808080, 0XFF808080, 0XFF808080, 0XFF4C494C, 0XFFF9F8F8, 0XFF808080,
   0XFFFAFCFD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFD3D3D8,
   0XFFB83D40, 0XFFF2D7D6, Bac___ounD, Bac___ounD, 0XFFFBF8F9, 0XFF786166,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFF999079, 0XFF736263, 0XFFFDFBFB,
   Bac___ounD, 0XFF808080, 0XFFB2B4BC, Bac___ounD, 0XFF808080, 0XFF808080,
   0XFF4E5363, 0XFF6D7183, 0XFF6A7084, 0XFF858999, 0XFF92939B, 0XFFFBFAFA,
   0XFFEEEDED, 0XFF808080, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFE8E9EC, 0XFFB83D40, 0XFFE8D3D1, Bac___ounD, Bac___ounD,
   0XFFE2CCCD, 0XFF7A6C6F, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFA6A191,
   0XFF624B44, 0XFFF6EEEB, Bac___ounD, 0XFF808080, 0XFFB7B9BF, Bac___ounD,
   0XFF808080, 0XFFCACCD0, 0XFFBBBDC2, 0XFFFCFCFC, Bac___ounD, 0XFFFFFFFE,
   Bac___ounD, Bac___ounD, 0XFFD7D7DB, 0XFF808080, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFFF3F4F6, 0XFFB83D40, 0XFFDFBEBA,
   Bac___ounD, Bac___ounD, 0XFFC1A2A3, 0XFF968C93, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFBDB7AA, 0XFF624137, 0XFFFCF6EC, 0XFFF6F6F7, 0XFF808080,
   0XFF808080, Bac___ounD, 0XFF808080, 0XFF808080, 0XFFAEB0B7, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFF808080, 0XFF808080,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFFDFDFD,
   0XFFB83D40, 0XFFD5ACA7, Bac___ounD, Bac___ounD, 0XFF9D7073, 0XFFA4979A,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFFD5D4CC, 0XFF634E44, 0XFFF9F6DE,
   0XFF808080, 0XFF808080, 0XFF808080, 0XFFF7F7F8, 0XFF808595, 0XFF808080,
   0XFFAEB0B7, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFF8F8F8,
   0XFF808080, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFFFFFFE, 0XFFB83D40, 0XFFB83D40, Bac___ounD, Bac___ounD,
   0XFF8E6163, 0XFFA69798, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFEDEDEC,
   0XFF564A45, 0XFFF5F0CC, 0XFF808080, Bac___ounD, 0XFF808080, 0XFF808080,
   0XFF808080, 0XFF808080, 0XFF808080, 0XFFF9F9FA, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFBDBEC4, 0XFF808080, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFA27576, 0XFFB83D40,
   Bac___ounD, 0XFFEEE2E2, 0XFF8C585C, 0XFF978D94, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFFEFEFE, 0XFF665359, 0XFF808080, 0XFF808080, Bac___ounD,
   0XFF808080, 0XFF808080, 0XFFD5D6DA, Bac___ounD, 0XFF808080, 0XFF808080,
   0XFFFBF9F9, Bac___ounD, 0XFF808080, 0XFF808080, 0XFF808080, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   0XFFBAA3A6, 0XFFB83D40, 0XFFFEFDFD, 0XFFB27C7A, 0XFFBE908B, 0XFF959099,
   0XFF858489, 0XFFB8B8BC, Bac___ounD, Bac___ounD, 0XFF857F90, 0XFF808080,
   0XFFD0D2D6, Bac___ounD, 0XFF808080, 0XFF60667A, 0XFFF9FAFA, Bac___ounD,
   0XFFFDFDFD, 0XFF808080, 0XFF808080, 0XFF808080, 0XFF808080, 0XFFBDAD79,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFFCECED4, 0XFF8C332D, 0XFFC39593, 0XFF8E3731,
   0XFFF0E5E4, 0XFFF3F3F5, 0XFF605F66, 0XFF272731, Bac___ounD, Bac___ounD,
   0XFFA0A3AC, 0XFF666A78, 0XFFFEFEFE, Bac___ounD, 0XFF808080, 0XFF70778A,
   Bac___ounD, Bac___ounD, Bac___ounD, 0XFFDFD6A4, 0XFFC0BB68, 0XFF919D62,
   0XFFB6AC6A, 0XFFF2EED1, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFFBFBFC, 0XFF764548,
   0XFF992F28, 0XFFBE958E, Bac___ounD, Bac___ounD, 0XFFFDFDFD, 0XFFE0E1E1,
   Bac___ounD, Bac___ounD, 0XFFF0F0F1, 0XFFE2E2E4, Bac___ounD, Bac___ounD,
   0XFF808080, 0XFFB9BBC0, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFF5F0E7,
   0XFFC8B66D, 0XFFCFBE66, 0XFFDDC68B, 0XFFFFFFFE, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, 0XFFC6C7CB, 0XFFAB9FA1, 0XFFFCFCFC, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFFF2F2F2, 0XFFFAFAFA, Bac___ounD, Bac___ounD,
   Bac___ounD, Bac___ounD, 0XFFF9F5E8, 0XFFF6EFD4, 0XFFFDFDF8, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD, 0XFFF7F7F7,
   0XFFFBFAFA, Bac___ounD, Bac___ounD, 0XFFDEDEDE, 0XFFFAFAF9, Bac___ounD,
   Bac___ounD, Bac___ounD, Bac___ounD, Bac___ounD };

                                    
/** weinert-automation's logo as icon (Image). <br /> */
   private static volatile Image iconImage2;
   

/** weinert-automation's logo as icon (Image version 2). <br />
 *  <br />
 *  This method returns a tiny image of weinert-automation's logo on white
 *  background. The dimension is {@link #iconWH}*{@link #iconWH}.<br />
 *  <br />
 *  The returned {@link Image} is suitable to be set as a
 *  {@link java.awt.Frame Frame}'s icon.<br />
 *  <br />
 *  @see java.awt.Frame#setIconImage(java.awt.Image)
 */
   static public Image getIcon2(){
      if (iconImage2 != null) return iconImage2;
      synchronized (WeAutLogo.class)  {
         if (iconImage2 != null) return iconImage2;
         iconImage2 =  new BufferedImage(iconWH, iconWH,
                                 BufferedImage.TYPE_INT_ARGB); 
         ((BufferedImage)iconImage2).setRGB(0, 0, iconWH, iconWH,
                                 iconPixels2, 0, iconWH);
         iconPixels = null; // no garbage
      } // sync
      return iconImage2;
   } // getIcon2()

/** Make an ARGB int array initialiser from image file.
 * 
 *  The initialiser is printed, 6 (or pixPerLine) hex values per line, to
 *  standard output.<br />
 *  Run by: java imageFile [ pixPerLine ]  (e.g.) <br >
 *  Example java de.frame4j.graf.WeAutLogo   weaut_icon_acr32t.png 8
 *    
 *  @param args existing image file path
 *  @throws IOException on file input errors
 */
   
   public static void main(String[] args) throws IOException{
     BufferedImage image = ImageIO.read( new File(args[0]));
     int width = image.getWidth();
     int height = image.getHeight();
     System.out.println("image " + args[0]
                           + " [" + width + "*" + height + "]\n {\n");
     
     int zS = 6;
     if (args.length > 1) {
        zS = Integer.decode(args[1]);
     }
     int z = zS;

     for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int pixel = image.getRGB(j, i);
          System.out.print(String.format("0X%08X, ", pixel));
          if (--z == 0) {
             System.out.print(String.format(" // %d,%d\n", i, j + 1 - zS));
             z = zS;
          }
        }
      }
     System.out.println("}");
   } // main(String[])

} // class WeAutLogo (16.12.2001, 02.03.2005, 21.01.2009, 15.02.2009)
