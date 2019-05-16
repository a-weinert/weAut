/** <b>Definitions for the usage of the Raspberry Pi1 and its I/O. <br />
 *  <br />
 *  <a href=./de/weAut/package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 17 $ ($Date: 2019-05-15 21:51:04 +0200 (Mi, 15 Mai 2019) $)
 */
package de.weAut;
public interface Pi1Usage extends PiUsage {

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
//6 9 14 20 25 : gnd
//1 17 : 3.3V
//2 4  : 5V

} // Pi1Usage
