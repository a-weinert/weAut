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
package de.weAut;

/** <b>Definitions for the usage of the Raspberry Pi1 and its I/O</b>.<br />
 *  <br />
 *  <a href=package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see Pi2 Pi3 ClientPigpiod
 *  @author   Albrecht Weinert
 *  @version  $Revision: 40 $ ($Date: 2021-04-19 21:47:30 +0200 (Mo, 19 Apr 2021) $)
 */
// so far:   V. 19  (17.05.2019) :  new
//           V. 36  (06.04.2021) :  polymorphism
public interface Pi1 extends ThePi { 

/** 26 pin connector GPIO assignment mapping. <br />
 *  <br />
 *  This is ported from arch/config_raspberry_01.h 
 */
//                   26 pin con | GPIO number
  public static final int PIN03 =  0; // 2 on Pi3 and Pi2
  public static final int PIN05 =  1; // 3 on Pi3 and Pi2
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
  public static final int PIN3 =  PIN03;  // synonym
  public static final int PIN4 =  PIN04;  // synonym
  public static final int PIN7 =  PIN07;  // synonym
  public static final int PIN8 =  PIN08;  // synonym


/** Pin number to GPIO number lookup. <br />
 *
 *  @param pin 1..26 is the legal IO connector pin number
 *  @return 0..56 the GPIO number; {@link #PIN0V}, {@link #PIN3V},
 *     {@link #PIN5V}, {@link #PINix}: undefined, i.e. illegal pin number
 */
@Override public default int gpio4pin(final int pin){
  if (pin < 1 || pin > 26) return PINix;
  return ThePi.Impl.pi1PIN2gpio[pin];
} // gpio4pin(int)

/** Pin number to GPIO number lookup. <br />
 *
 *  @param gpio 0..26  GPIO number (eventually) available on the 
 *          Pi's 26 pins connector.
 *  @return 1..26 as the respective pin or 0 if the GPIO is not on the
 *         pin connector.
 */
@Override public default int gpio2pin(final int gpio){
  if (gpio < 0 || gpio > 25) return 0; 
  return ThePi.Impl.pi1GPIO2pin[gpio];
} // gpio4pin(int)

/** The Pi's type. <br />
 *  <br />
 *  This method returns 1 meaning all Pi types with a 26 pin GPIO
 *  connector (i.e. Pi1).
 *  @return 1
 */
 @Override public default int type(){ return 1; }
    
/** Make a Pi1 object with default settings. <br />
 *   
 *  @return a Pi1 object with default {@link ThePi#defaultHost host},
 *     {@link ThePi#port() port} and  {@link ThePi#timeout() timeout} 
 */
 static public Pi1 make(){ return make(null, 0, 0);  }

/** Make a Pi1 object. <br />
 *   
 *  @return a PI1 object with the given {@link #host() host},
 *     {@link #port() port} and  {@link #timeout() timeout} 
 */
 static public Pi1 make(final String host, final int port, final int timeout){
    return new Pi1(){
      String hostPi;
      int portPi;
      int timoutPi;      
      { // pseudo "constructor" for anonymous class
        this.hostPi = host == null || host.length() < 3 ? defaultHost : host;
        this.portPi = port < 20 || port > 65535 ? 8888 : port;
        this.timoutPi = timeout < 300 || timeout > 50000 ? 10000 : timeout;
      } // initialiser
      @Override public int port(){ return this.portPi; }
      @Override public String host(){ return this.hostPi; }
      @Override public int timeout(){ return this.timoutPi; }
    }; // ano inner
 } // make(String, 3*int)
 
} // Pi1 (01.04.2021)
