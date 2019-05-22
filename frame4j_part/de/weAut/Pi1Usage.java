/*  Copyright 2019 Albrecht Weinert, Bochum, Germany (a-weinert.de)
 *  All rights reserved.
 *  
 *  This file is part of Frame4J 
 *  ( frame4j.de  http://weinert-automation.de/software/frame4j/ )
 * 
 *  Frame4J is made available under the terms of the 
 *  Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/  or as text in
 *  [frame4jSourceRoot]/de/frame4j/doc-files/epl.txt within 
 *  the source distribution 
 */
package de.weAut;


/** <b>Definitions for the usage of the Raspberry Pi1 and its I/O</b>.<br />
 *  <br />
 *  <a href=./de/weAut/package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 21 $ ($Date: 2019-05-22 13:35:56 +0200 (Mi, 22 Mai 2019) $)
 */
// so far:   V. 19  (17.05.2019) :  new
//           V. 21  (21.05.2019) :  typo
public interface Pi1Usage { // extends PiUsage {

/** GPIO to 26 pin header mapping.
 * 
 *  This is ported from arch/config_raspberry_01.h 
 */
//                   26 pin con | GPIO number
  public static final int PIN3  =  0; // 2 on Pi3
  public static final int PIN5  =  1; // 3 on Pi3
  public static final int PIN7  =  4;
  public static final int PIN8  = 14;  //TXDI
  public static final int PIN10 = 15;  //RXDI
  public static final int PIN11 = 17;
  public static final int PIN12 = 18;
  public static final int PIN13 = 21; // 27 on Pi3
  public static final int PIN15 = 22;
  public static final int PIN16 = 23;
  public static final int PIN18 = 24;
  public static final int PIN19 = 10;  // MOSI | S
  public static final int PIN21 =  9;  // MISO | P
  public static final int PIN22 = 25;
  public static final int PIN23 = 11;  // SCLK | I
  public static final int PIN24 =  8;  // CE0  /
  public static final int PIN26 =  7;  // CE1 /
   // 6 9 14 20 25 : gnd
   // 1 17 : 3.3V
   // 2 4  : 5V
} // Pi1Usage
