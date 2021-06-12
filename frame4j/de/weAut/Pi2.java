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
package de.weAut;

/** <b>Definitions for the usage of the Raspberry Pi2 and its I/O</b>.<br />
 *  <br />
 *  The {@link Pi2}  &ndash; seldomly used &ndash; was the first extension of
 *  {@link Pi1} adding an extra 8 pin IO connector (labelled P5) to 
 *  {@link Pi1}'s 26 pin connector. Here, for {@link Pi2} only, we assign
 *  pin numbers 31 to 38 to P5's pins 1 to 8. On a {@link Pi3}, 0, 4, of
 *  course pins 31..38 belong to the 40 pin IO connector (P1). <br /> 
 *  <br /> 
 *  <a href=package-summary.html#co>&copy;</a> 
 *  Copyright 2021 &nbsp; Albrecht Weinert<br />
 *  @see Pi1
 *  @see Pi3
 *  @see ClientPigpiod
 *  @author   Albrecht Weinert
 *  @version  $Revision: 52 $ ($Date: 2021-06-12 13:01:58 +0200 (Sa, 12 Jun 2021) $)
 */
// so far:   V. 35  (01.04.2021) :  new
//           V. 36  (06.04.2021) :  name ambiguity in anonymous inner class
//           V. 49  (17.05.2021) :  bug- gpio2pin()
public final class Pi2 extends ThePi.ComBeh { 

/** 26 pin connector GPIO assignment mapping. <br />
 *  <br />
 *  This is ported from arch/config_raspberry_01.h 
 */
//                   26 pin con | GPIO number
  public static final int PIN03 =  2; // 0 on Pi1
  public static final int PIN05 =  3; // 1 on Pi1
  public static final int PIN07 =  4;
  public static final int PIN08 = 14;  //TXDI
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
  public static final int PIN3 = PIN03;  // synonym
  public static final int PIN4 = PIN04;  // synonym
  public static final int PIN7 = PIN07;  // synonym
  public static final int PIN8 = PIN08;  // synonym

/** 8 pin connector P5  GPIO assignment mapping. <br />
 *  <br />
 *  The int constants names are P5_Cn with n = 3..6. <br />
 *  1 is 3.3V ({@link #PIN3V}), 2 is 5V ({@link #PIN3V}) and 7 and 8 are
 *  Ground ({@link #PIN0V}). <br />
 *  For the mapping to virtual pins 32 to 38 of the 26 pin connector those
 *  constants are also available as PIN3n with n = 3..6. 
 */
//              P5     8 pin con | GPIO number
  public static final int P5_C3  = 28; // SDA
  public static final int P5_C4  = 29; // SCL
  public static final int P5_C5  = 30;
  public static final int P5_C6  = 31;
  public static final int PIN33  = 28; // SDA
  public static final int PIN34  = 29; // SCL
  public static final int PIN35  = 30;
  public static final int PIN36  = 31;

/** Pin number to GPIO number lookup. <br />
 *
 *  @param pin 0, 1..26. 31.. 38 is the legal IO connector pin number
 *         where 31..38 map connector P5 pins 1..8
 *  @return 0..56 the GPIO number; {@link #PIN0V}, {@link #PIN3V},
 *     {@link #PIN5V}, {@link #PINix}: undefined, i.e. illegal pin number
 *      or {@link #PINig} ignore for pin = 0
 */
  @Override public final int gpio4pin(final int pin){
    if (pin < 0 || pin > 38) return PINix;
    return ThePi.Impl.pi2PIN2gpio[pin];
  } // gpio4pin(int)

/** Pin number to GPIO number lookup. <br />
 *
 *  @param gpio 0..26  GPIO number (eventually) available on the 
 *          Pi's 26 pin and 8 pin connectors.
 *  @return 1..26 or 31..38 as the respective pin or 0 if the GPIO
 *         is on no connector.
 */
  @Override public final int gpio2pin(final int gpio){
    if (gpio < 0 || gpio > 31) return 0; //  bug- 17.05.2021
    return ThePi.Impl.pi2GPIO2pin[gpio];
  } // gpio4pin(int)
    
/** Make a Pi2 object with default settings. <br />
 *   
 *  @return a Pi2 object with default {@link ThePi#defaultHost host},
 *    (socket) {@link ThePi#sockP() port} and {@link ThePi#timeout() timeout} 
 */
  static public Pi2 make(){ return make(null, 0, 0); }

/** Make a Pi2 object. <br />
 *   
 *  @return a PI2 object with the given {@link #host() host},
 *     (socket) {@link #sockP() port} and  {@link #timeout() timeout} 
 */
  static public Pi2 make(final String host, final int port, final int timeout){
    return new Pi2(host, port, timeout);
  } // make(String, 2*int)
  
  private Pi2(String host, int port, int timeout){
    super(2, host, port, timeout);
  } // P2(String, 2*int)
} // Pi2 (06.04.2021, 12.06.2021)
