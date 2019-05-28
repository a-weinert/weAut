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

/** <b>Definitions for the usage of the Raspberry Pi3 and its I/O. <br />
 *  <br />
 *  <a href=./de/weAut/package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 24 $ ($Date: 2019-05-27 20:36:56 +0200 (Mo, 27 Mai 2019) $)
 */
// so far:   V. 19  (17.05.2019) :  new
//           V. 21  (21.05.2019) :  typo

public interface Pi3Usage { 

/** GPIO to 40 pin header mapping.
 * 
 *  This is ported from arch/config_raspberry_03.h 
 */
//                   40 pin con | GPIO number
  public static final int PIN3  =  2;  // SDA1  ( 0 on Pi1)
  public static final int PIN5  =  3;  // SCL   ( 1 on Pi1)
  public static final int PIN7  =  4;  // GPCLK0
  public static final int PIN8  = 14;  // TXDI
  public static final int PIN10 = 15;  // RXDI
  public static final int PIN11 = 17;
  public static final int PIN12 = 18;
  public static final int PIN13 = 27;  //       (21 on Pi1)
  public static final int PIN15 = 22;
  public static final int PIN16 = 23;
  public static final int PIN18 = 24;
  public static final int PIN19 = 10;  //  MOSI | S
  public static final int PIN21 =  9;  //  MISO | P
  public static final int PIN22 = 25;
  public static final int PIN23 = 11;  //  SCLK | I
  public static final int PIN24 =  8;  // CE0   /
  public static final int PIN26 =  7;  // CE1  /
  public static final int PIN29 =  5;
  public static final int PIN31 =  6;
  public static final int PIN32 = 12;
  public static final int PIN33 = 13;
  public static final int PIN35 = 19;
  public static final int PIN36 = 16;
  public static final int PIN37 = 26;
  public static final int PIN38 = 20;
  public static final int PIN40 = 21;
   // 6 9 14 20 25 30 34 39 : gnd
   // 1 17 : 3.3V
   // 2 4  : 5V
   // 27 28: ID_SD/SC (HAT board & EEPROM interface)
} // Pi3Usage
