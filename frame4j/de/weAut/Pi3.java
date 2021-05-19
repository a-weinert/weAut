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

/** <b>Definitions for the usage of the Raspberry Pi3, 4 and 0's I/O. <br />
 *  <br />
 *  The {@link Pi3} introduced the 40 pin IO connector &ndash; currently 
 *  <i>the</i> standard &ndash; also used by the Pi 4 and the Pi zero. Hence,
 *  this type {@link Pi3} represents the IO behaviour of all those modern 
 *  Paspberry Pis. <br />
 *  <br />
 *  <a href=package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  @see Pi1
 *  @see Pi2
 *  @see ClientPigpiod
 *  @author   Albrecht Weinert
 *  @version  $Revision: 49 $ ($Date: 2021-05-19 16:47:26 +0200 (Mi, 19 Mai 2021) $)
 */
// so far:   V. 19  (17.05.2019) :  new
//           V. 36  (06.04.2021) :  polymorphism; type 3 4 0
//           V. 49  (17.05.2021) :  bug- gpio2pin()

public interface Pi3 extends ThePi {
  
/** Make a Pi3 object for a given host. <br />
 * 
 *  This is equivalent to
 *  {@link #make(String, int, int, int) make(host, 0, 0, 3)}.
 *   
 *  @return a Pi3 object with the given {@link #host() host},
 *     port 8888 and  timeout 10000 (10s)
 *  @param host might be given as name "raspi67" if known to DNS or IP
 *     address "192.168.178.67". null or empty will be
 *     {@link de.weAut.ThePi#defaultHost defaultHost}
 */
  static public Pi3 make(final String host){ return make(host, 0, 0, 3); }
  
/** Make a Pi3 object for Pi3, Pi or Pi0 (zero). <br />
 *   
 *  @return a Pi3 object with the given {@link #host() host},
 *     {@link #port() port}, {@link #timeout() timeout} and
 *     {@link #type() type}
 *  @param type 3, 4 and 0 are accepted; all else defaults to 3
 *  @param port 20..65535 will be accepted; other values default to 8888
 *  @param timeout for socket connection in ms, 300..50000 will be accepted;
 *           other values default to 10s 
 *  @see #make(String)            
 */
  static public Pi3 make(final String host, final int port,
                                      final int timeout, final int type){
    return new Pi3(){
      String hostPi;
      int portPi;
      int timoutPi;  
      int typePi;
      { // pseudo "constructor" for anonymous class
        this.hostPi = host == null || host.length() < 3 ? defaultHost : host;
        this.portPi = port < 20 || port > 65535 ? 8888 : port;
        this.timoutPi = timeout < 300 || timeout > 50000 ? 10000 : timeout;
        this.typePi = type != 0 && type!= 4 ? 3 : type;
      } // initialiser
      @Override public int port(){ return this.portPi; }
      @Override public String host(){ return this.hostPi; }
      @Override public int timeout(){ return this.timoutPi; }
      @Override public int type(){ return this.typePi; }
   }; // ano inner
  } // make(String, 3*int)

//----------------------------------------------------------------------  

/** 40 pin connector GPIO assignment mapping. <br />
 *  <br />
 *  A bundle of int constants. <br />
 *  This is ported from arch/config_raspberry_03.h 
 */
//                   40 pin con | GPIO number
  public static final int PIN03 =  2;  // SDA1  ( 0 on Pi1)
  public static final int PIN05 =  3;  // SCL   ( 1 on Pi1)
  public static final int PIN07 =  4;  // GPCLK0
  public static final int PIN08 = 14;  // TXDI
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
  public static final int PIN27 =  0;  // SDA0
  public static final int PIN28 =  1;  // SCL0
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
  
  public static final int PIN30 =  PIN0V ; // Gnd    
  public static final int PIN34 =  PIN0V ; // Gnd    
  public static final int PIN39 =  PIN0V ; // Gnd   
  
  public static final int PIN3  =  PIN03;  // synonym 
  public static final int PIN5  =  PIN05;  // synonym    
  public static final int PIN7  =  PIN07;  // synonym    
  public static final int PIN8  =  PIN08;  // synonym  


/** Pin number to GPIO number lookup. <br />
 *
 *  @param pin 0, 1..40 is the legal IO connector pin number
 *  @return 0..56 the GPIO number; {@link #PIN0V}, {@link #PIN3V},
 *     {@link #PIN5V}, {@link #PINix}: undefined, i.e. illegal pin number
 *       or {@link #PINig} ignore for pin = 0
 */
  @Override public default int gpio4pin(final int pin){
    if (pin < 0 || pin > 40) return PINix;
    return ThePi.Impl.pi3PIN2gpio[pin];
  } // gpio4pin(int)

/** Pin number to GPIO number lookup. <br />
 *
 *  @param gpio 0..26  GPIO number (eventually) available on the 
 *          Pi's 40 pin connector.
 *  @return 1..40 as the respective pin or 0 if the GPIO is not
 *           on the IO connector (P1).
 */
  @Override public default int gpio2pin(final int gpio){
    if (gpio < 0 || gpio > 31) return 0;  // bug- 17.05.2021
    return ThePi.Impl.pi3GPIO2pin[gpio];
  } // gpio2pin(int)
  
} // Pi3 (01.04.2021)
