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

import java.io.IOException;

/** <b>Definitions for the usage of a Raspberry Pi device</b>. <br />
 *  <br />
 *  <a href=package-summary.html#co>&copy;</a> 
 *  Copyright 2021 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  A derived interface of this type will contain constants and methods 
 *  describing a Pi type like {link de.weAut.Pi1 Pi1},
 *  {link de.weAut.Pi2 Pi2}, or 
 *  {link de.weAut.Pi3 Pi3 for Pi3, P4 and Pi0}. Insofar this interface
 *  acts as an abstract class (allowing multiple inheritance for future
 *  applications).<br />
 *  An object of this type (usually factored from an anonymous class) will
 *  additionally contain the data of a concrete Pi device, especially the
 *  pigpiod access data.
 *  @see Pi1
 *  @see Pi2
 *  @see Pi3
 *  @see ClientPigpiod
 *  @author   Albrecht Weinert
 *  @version  $Revision: 47 $ ($Date: 2021-05-13 19:06:22 +0200 (Do, 13 Mai 2021) $)
 */
// so far:   V. 36  (13.04.2021) :  new
//           V. 4x  (21.05.202x) :  ...

public interface ThePi extends PiVals { 
  
/** Allows a GPIO number output. <br />
 *  <br />
 *  This method return true for 0..31 and for {@link #PINig}.<br />
 *  Rationale: Output to gpio 32 to 56 may spoil the operating system (while
 *  input from there would be feasible). And operations on {@link #PINig}
 *  (ignore pin/gpio) will not be performed (and here ignored without error).
 *  @param gpio a GPIO number 
 *  @return true when output is principally allowable
 *  @see #GPIOutM {@link #PINig}
 */
  public static boolean gpioMayOut(final int gpio){
    return (gpio >= GPIOmin && gpio <= GPIOutM) || gpio == PINig;
  } // gpio2String(int)

/** Format a GPIO number. <br />
 *  <br />
 *  A legal gpio number will be formated as
 *  {@code GPIO00 .. GPIO31} or {@code gpio32 ..gpio56}. <br />
 *  Pseudo GPIO numbers for non IO pins like {@link #PIN3V} etc. will 
 *  be formated as {@code gnd}, {@code 3V3}, {@code 5V0}, {@code ignore}.<br />
 *  <br />
 *  All other values (and {@link #PINix}) will get {@code none}.   
 *   
 *  @param gpio a GPIO number 
 *  @return the text 
 */
  public static String gpio2String(final int gpio){
    if (gpio < GPIOmin) return "none";
    if (gpio == PIN3V) return "3V3";
    if (gpio == PIN5V) return "5V0";
    if (gpio == PIN0V) return "gnd";
    if (gpio == PINig) return "ignore";
    if (gpio > GPIOmax) return "none";
    StringBuilder dest = new StringBuilder(6);
    dest.append(gpio < 31 ? "GPIO" : "gpio");
    dest.append((char)('0' + gpio/10)).append((char)('0' + gpio%10));
    return new String(dest);
  } // gpio2String(int)

/** The pigpiod default host. <br />
 *  <br />
 *  If the Java application is running on a Raspberry Pi the default host
 *  is 127.0.0.1, i.e. the local machine itself. <br />
 *  On other platforms it is p.r.v.67 (would be 192.168.178.67 in standard
 *  Fritzbox home net). Ending in .67 is our standard experimental Pi in the
 *  private home respectively laboratory (C net) LAN. <br />
 *  If another value (as 67 on a C net) shall be used use the respective
 *  factory methods  in {@link Pi3}, {@link Pi1} or {@link Pi2}.
 */
  public static final String defaultHost = Impl.defaultHost(); 
  
  
//--------------------- Type properties --------------------------  

/** Pin number to GPIO number lookup. <br />
 *  <br />
 *  @param pin 0, 1..40 (26 [+8]) is the legal IO connector pin number
 *  @return 0..31 (56) the GPIO number; {@link #PIN0V}, {@link #PIN3V},
 *     {@link #PIN5V}, {@link #PINix}: undefined, i.e. illegal pin number
 *     or {@link #PINig} ignore for pin = 0
 */
 public int gpio4pin(final int pin);

/** Pin number to GPIO number lookup. <br />
 *
 *  @param gpio 0..39 a GPIO number (eventually) available on the 
 *          Pi's 40 (26) pins connector.
 *  @return 1..40 as the respective pin or 0 if the GPIO is not on the
 *         pin connector.
 */
  public int gpio2pin(final int gpio);
  
/** Pin number to GPIO number lookup with check. <br />
 *  <br />
 *  This method does (by delegating) the same pin to GPIO look up
 *  as {@link #gpio2pin(int)}. Additionally it raises an
 *  {@link IOException} with a comprehensive message, when the pin is no
 *  real IO pin for a GPIO in the range 0..31.
 *  
 *  @param signal a short (best 7 char) description of the pin's
 *         attached device like "red LED", "Ubat_12" etc. 
 *         It is used for the IOexception message, only
 *  @param pin 0, 1..40 (26 [+8]) is the legal IO connector pin number
 *  @return 0..31 (56) the GPIO number; {@link #PIN0V}, {@link #PIN3V},
 *     {@link #PIN5V}, {@link #PINix}: undefined, i.e. illegal pin number
 *       or {@link #PINig} ignore for pin = 0
 *  @throws IOException if the pin does not allow the IO operation intended
 *          with a comprehensive message     
 */
  public default int gpio4pinChck(CharSequence signal, final int pin)
                                                    throws IOException {
    final int gpio = gpio4pin(pin);
    if (gpio >= GPIOmin && gpio <= GPIOutM || gpio == PINig) return gpio;
    throw new IOException("pin " + pin + " for " + signal + " = "  
                                         + ThePi.gpio2String(gpio));
  } // gpio4pinChk(int)

 
//--------------------- Object properties --------------------------  

/** Get the pigpiod socket port number. <br />
 *  <br />
 *  This method returns the port number of the Pi's pigpiod server.<br />
 *  Once made this is an non mutable property of a ThePi object.<br />
 *  The default is 8888.
 */
   public int port();

/** Get the pigpiod socket host. <br />
 *  <br />
 *  This method returns pigpiod's host.<br />
 *  Once made this is an non mutable property of a ThePi object. <br />
 *  The default is {#defaultHost()} 
 */
  public String host();

/** Get the pigpiod socket timeout in ms. <br />
 *  <br />
 *  This method returns pigpiod's socket timeout.<br />
 *  Once made this is an non mutable property of a ThePi object.<br />
 *  The default is 10000 (10s).
 */
  public int timeout();
  
/** The Pi's type. <br />
 *  <br />
 *  3, 0 and 4 stand for the Pi3 pinout. <br />
 *  1 stands for Pi1 and 2 for the Pi2. <br />
 *  The type information 1 and 2 may also be got by instanceof {@link Pi1}
 *  and instanceof {@link Pi2}, respectively.
 *  @return 1, 2 or 3, 0, 4
 */
  public int type();  

/** Make a Pi. <br />
 *  <br />
 *  Depending on type this method either makes a {@link Pi3} (default),
 *  a {@link Pi2} or a {@link Pi1} object according to the other parameters.
 *  As the Pi4 and Pi0 have the same GPIO assignment as a Pi3 they all are 
 *  represented by a {@link Pi3} object. <br />
 *  
 *  @return a ThePi object of given type with the given {@link #host() host},
 *     {@link #port() port} and  {@link #timeout() timeout} 
 *  @see Pi3#make(String, int, int, int)
 *  @see  Pi1#make(String, int, int)    
 */
  static public ThePi make(final String host, final int port, 
                                         final int timeout, final int type){
    if (type == 1) return Pi1.make(host, port, timeout);
    if (type == 2) return Pi2.make(host, port, timeout);
    return Pi3.make(host, port, timeout, type); // 0, 4, 3(default)
  } // make(String, 3*int)
  
    
//======   inner class for initialisations and default methods   ==========
  
/** <b>Internal implementation class</b>. <br />
 *  <br />
 *  Often the almost single purpose in life of an embedded class within
 *  an interface was the evaluation of some final values. Albrecht Weinert's 
 *  &quot;embedded evaluation class&quot; pattern (first published 2001)
 *  allows for evaluations at runtime of any complexity, that initialisers
 *  for final values could never accomplish.<br />
 *  <br />
 *  Another purpose here is allowing complicated implementations for interface
 *  methods (default or static) as well as giving those non public arrays the
 *  values of which could (in Java) otherwise not be made immutable. <br />
 *  <br />
 *  In this sense this class is not for the user of the frame work, nor does
 *  she or he need it's documentation. <br />
 *  <br />
 *  Background: This (javaDoc) documentation is only visible because all 
 *  elements in an interface have to be public. This &quot;enforced 
 *  publicity&quot; mechanism also applies to embedded classes (which is a
 *  pity). Otherwise class would have gotten package visibility (by the 
 *  author's will), thus hiding it's existence from reader's eyes.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2021  Albrecht Weinert  
 *  @author   Albrecht Weinert
 *  @version  as enclosing Interface
 */   
  static final class Impl {

    private Impl(){} // no objects, no docu
    
// Implementation hint: Hiding a array as package is a weak (private 
// would be the only) way in Java to make its content constant.
// Nevertheless, we keep the following tables here instead of making Impl
// classes also in Pi3, Pi1 and Pi2 a) for sake of their simplicity and 
// b) for the (future) case of common lookup methods with a type parameter.

//----------------------------------  3 4 0 lookup  -----------------

   // 6 9 14 20 25 30 34 39 : gnd
   // 1 17 : 3.3V
   // 2 4  : 5V      
   // 27 28: ID_SD/SC (HAT board & EEPROM interface)  or GPIO 0 and 1
    static final int[] pi3GPIO2pin = {
//   0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
    27,28, 3, 5, 7,29,31,26,24,21,19,23,32,33, 8,10,36,11,12,35,
    38,40,15,16,18,22,37,13, 0, 0, 0, 0, 0 };
     
    static final int[] pi3PIN2gpio = {
//    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
     57,93,95, 2,95, 3,90, 4,14,90,15,17,18,27,90,22,23,93,24,10,
     90, 9,25,11, 8,90, 7, 0, 1, 5,90, 6,12,13,90,19,16,26,20,90,
     21,99,99,99};
//    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
//0..56(?) GPIO; 95: 5V; 93: 3.3V; 90: Ground 0V;
//99: not existent; [1..40]: valid pin number/index
     
//----------------------------------  type 1 lookup  ----------------
   // 6 9 14 20 25 : gnd
   // 1 17 : 3.3V
   // 2 4  : 5V
    static final int[] pi1GPIO2pin = {
//    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
      3, 5, 0, 0, 7, 0, 0, 0,24,21,19,23, 0, 0, 8,10, 0,11,12, 0,
      0,13,15,16,18,22, 0, 0, 0, 0, 0, 0, 0};
        
    static final int[] pi1PIN2gpio = {
//     0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
      57,93,95, 0,95, 1,90, 4,14,90,15,17,18,21,90,22,23,93,24,10,
      90, 9,25,11, 8,90, 7,99 };

//----------------------------------  type 2 lookup  ----------------
  // 6 9 14 20 25 : gnd
  // 1 17 : 3.3V
  // 2 4  : 5V
    static final int[] pi2GPIO2pin = {
//    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
      0, 0, 3, 5, 7, 0, 0, 0,24,21,19,23, 0, 0, 8,10, 0,11,12, 0,
      0,13,15,16,18,22, 0, 0,33,34,35,36, 0, 0, 0, 0};
// 8 pin connector P5 on Pi 2 will be handled as pin 31 to 38
// a GPIO > 25 is an error for Pi1     
     
    static final int[] pi2PIN2gpio = {
//    0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
     57,93,95, 2,95, 3,90, 4,14,90,15,17,18,21,90,22,23,93,24,10,
     90, 9,25,11, 8,90, 7,99,99,99,99,95,93,28,29,30,31,90,90,99,
     99,99,99,99};
//   0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
// 8 pin connector P5 on Pi 2 will be handled as pin 31 to 38     
// A pin > 26 is an error for Pi1,
// a pin 27..30 and > 38 (=P5 pin8) is an error also for Pi2


/** Get the pigpiod socket default host. <br />
 *  <br />
 *  This method returns pigpiod's default host. If running on a Raspberry Pi
 *  the default host is 127.0.0.1, i.e. the local machine itself. 
 *  On other platforms it is p.r.v.67 (would be 192.168.178.67 in standard
 *  Fritzbox home net). Ending in .67 is our standard experimental Pi in 
 *  the private home respectively laboratory (C net) LAN. <br />
 *  If another value value shall be used use the respective factory methods 
 *  in {@link Pi3} or {@link Pi1}. <br />
 *  <br />
 *  For other ones either this method mustn't be used or overriden.  
 */
    static String defaultHost(){
      if (ON_PI) return "127.0.0.1"; // on a Pi IPv4 local address
      if (! HOST_IPv4) return "192.168.178.67"; // have no base IPv4
      String tmp = HOST_IP;
      int lastByte = tmp.lastIndexOf('.');
      if (lastByte < 6) return "192.168.178.67"; // can't be p.r.v.
      return tmp.substring(0, lastByte) + ".67";
    }  // defaultHost()
  } // Impl
} // ThePi  (06.04.2021)
