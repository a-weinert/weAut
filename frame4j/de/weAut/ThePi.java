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
import de.frame4j.text.TextHelper;

/** <b>Definitions for the usage of a Raspberry Pi device</b>. <br />
 *  <br />
 *  <a href=package-summary.html#co>&copy;</a> 
 *  Copyright 2021 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  A derived interface of this type will contain constants and methods 
 *  describing a Pi type like {@link de.weAut.Pi1 Pi1},
 *  {@link de.weAut.Pi2 Pi2}, or 
 *  {@link de.weAut.Pi3 Pi3 for Pi3, P4 and Pi0}. Insofar this interface
 *  acts as an abstract class (allowing multiple inheritance for future
 *  applications).<br />
 *  <br />
 *  A {@link ThePi#make(String, int, int, int) make()} method here 
 *  (respectively the one called in said sub interfaces) will yield a Pi
 *  object holding the type and some application data of a concrete Pi
 *  device.<br />
 *  A Pi ({@link ThePi}) object will be usually held and usually 
 *  {@link ClientPigpiod#make(String, int, int, int, Object) made} within 
 *  {@link ClientPigpiod} object holding also the pigpiod (socket) access
 *  data and comfortably implementing the IO.<br />
 *  <br />
 *  Additionally, objects of the {@link ThePi}'s inner class 
 *  {@link de.weAut.ThePi.Port Port} may be used to hold data (and
 *  behaviour) of one of this Pi's IO ports. 
 *
 *  @see Pi1
 *  @see Pi2
 *  @see Pi3
 *  @see ClientPigpiod
 *  @author   Albrecht Weinert
 *  @version  $Revision: 52 $ ($Date: 2021-06-12 13:01:58 +0200 (Sa, 12 Jun 2021) $)
 */
// so far:   V. 36  (13.04.2021) :  new
//           V. 4x  (21.05.202x) :  ...

public interface ThePi extends PiVals { 

/** <b>A port on this Pi</b>. <br />
 *  <br />
 *  @see ThePi#portByGPIO(int, String)
 *  @see ThePi#portByPin(int, String)  
 */
  public class Port {

/** The pin and GPIO number. <br />
 *  <br />
 *  Note: Do not change by direct assignment.
 *  
 *  @see #setPin(int)
 *  @see #setGpio(int)
 *  @see #setPort(String)
 */
    int gpio, pin;
    
/** The pin number. <br />
 *  <br />
 *  @see #setPin(int)
 *  @see #getGpio()
 *  @see #setGpio(int)
 *  @see #setPort(String)
 */
    public int getPin(){ return this.pin;}
    
/** The pin and GPIO number. <br />
 *  <br />
 *  @see #getPin()
 *  @see #setPin(int)
 *  @see #setGpio(int)
 *  @see #setPort(String)
 */
    public int getGpio(){ return this.gpio; }
    
    
/** The port's (short) name. <br />
 *  <br />
 *  Neither null nor empty; recommended length: 7. 
 */    
    public final String name;
    final ThePi encl;
    
/** A flag to record the port's pull resistor setting. <br />
 *  <br />
 *  default: {@link #PI_PUD_KP}
 *  
 *  @see PiVals#PI_PUD_OFF
 *  @see PiVals#PI_PUD_DOWN
 *  @see PiVals#PI_PUD_UP
 *  @see PiVals#PI_PUD_DT
 *  @see ClientPigpiod
 */
    public int pud = PI_PUD_KP;

/** Usable for IO. <br />
 *  <br />
 *  This method returns {@code true} when this port's GPIO can be used for
 *  both real input and output operations.
 * 
 *  @return {@link #gpio} &lt; 32
 */
    public final boolean isIO(){ return gpio < 32; }

/** internal constructor. */    
    Port(int gpio, int pin, String name, ThePi encl){
      this.gpio = gpio;
      this.pin = pin;
      this.name = name;
      this.encl = encl;
    } // Port(2*int, String)
    
/** Set GPIO for IO operations. <br /> */
    public void setGpio(final int gpio){
      this.gpio = (gpio < 0 || gpio > 31) ? PINig : gpio;
      if (this.gpio >= PINig) { 
        this.pin = 0; this.pud = PI_PUD_KP; // ignore
        return;
      } 
      this.pin = encl.gpio2pin(this.gpio);
    } // setIntGpio(int)

/** Set pin for IO operations. <br /> */
    public void setPin(int pin){
      this.gpio =  encl.gpio4pin(pin);
      if (this.gpio < 32) { this.pin = pin; return; } // OK
      this.pin = 0; this.gpio = PINig; this.pud = PI_PUD_KP;  // error
    } // setInPin(int)
    
/** Change IO settings. <br /> 
 *  <br />
 *  This method sets the port's IO either as pin number or as GPIO with a
 *  prefix G. A postfix U, D or N will set the pull resistor flag as up,
 *  down or none.<br />
 *  Without postfix the pull resistor setting {@link #pud} will be kept
 *  untouched.
 *  
 *  @param port pin (12N, e.g.) or gpio (with leading G; G18, e.g.)
 */
    public void setPort(String port){ 
      port = TextHelper.trimUq(port, EMPTY_STRING);
      int len = port.length();
      if (len == 0) { setGpio(PINig); return; } // no IO
      char c0 = port.charAt(0);
      int num = 0;
      int nx = 1;
      boolean isGPIO = c0  == 'G' || c0 == 'g';
      if (isGPIO || c0  == 'P' || c0 == 'p') { // c0 said pin or gpio
        if (len == 1) { setGpio(PINig); return; } // none specified
        c0 = port.charAt(1); nx = 2;
      } // c0 (before) said pin or gpio
      if (c0 < '0' || (num = c0 - '0') > 9) { // c0 must be cif  but is not 
        setGpio(PINig); return; // none specified (format error)
      } // c0 must be cif  but is not
      while (nx < len) { // 2nd cif and / or PUD (U D N) follow
        c0 = port.charAt(nx); ++nx;
        if (c0 <= ' ') break; // end of value (start comment)
        if (c0 >= '0' &&  c0 <= '9') { // 2nd cif
          num = num * 10 + c0 - '0';
          if (num >= 99) break; // error ends c0 character loop
          continue;
        } // 2nd cif
        // to here no cif; must be U D N K (u d n k)
        if (c0 >= 'a') c0 -= 32; // poor man's toUpperCase for primitive USascii
        if (c0 == 'G' || c0 == 'P') break; // ignore redundant gpio or pin
        int pud = PI_PUD_KP;  // keep PUD (restore default)
        if (c0 == 'D') pud = PI_PUD_DOWN;
        else if (c0 == 'U') pud = PI_PUD_UP;
        else if (c0 == 'N') pud = PI_PUD_OFF;  // shall we accept O also ?
        else if (c0 != 'K') num = 100; // now in error anyway
        if (pud == PI_PUD_KP) break;
        this.pud = pud;
        break; // postfix ends char loop
      } // while c0 character loop
      if (isGPIO) { setGpio(num); // set by GPIO number (prefix G)
      } else setPin(num);  // set by pin number
    } // setPort(boolean, String)
    
/** Short description of the port. <br />
 *  <br />
 *  The text returned consists of the {@link #name}, the port number, a letter
 *  for the pull resistor setting and the gpio number with a prefix "G".     
 */
    @Override public final String toString(){
      StringBuilder dest = new StringBuilder(45).append(name)
                              .append(':').append(' ');
      if (gpio >= PiVals.PINig) {
        if (gpio == PiVals.PIN0V) return dest.append("gnd_0V").toString();
        if (gpio == PiVals.PIN3V) return dest.append("sup3V3").toString();
        if (gpio == PiVals.PIN5V) return dest.append("sup_5V").toString();
        return dest.append("noneIgn").toString();
      }
      PiUtil.twoDigitDec(dest, pin);
      if (pud >= PI_PUD_OFF && pud <= PI_PUD_UP) dest.append(pudC[pud]);
      dest.append('G');  
      PiUtil.twoDigitDec(dest, gpio);
      return dest.toString();
    } // toString()
    
    static final char[] pudC = {'N', 'D', 'U', '~', 'K'};
    
/** Equal with other port. <br />
 *  <br />
 *  From the physical sight of a Raspberry Pi's IO to outside world two ports
 *  have equal effects or information when the GPIO numbers are equal.<br />
 *  This sight is implemented here and, consistently, in {@link #hashCode()}. 
 *  
 *   @return true when other is a {@code Port} object with the same GPIO
 */
    @Override public final boolean equals(final Object other){
      if (! (other instanceof Port)) return false;
      return this.gpio == ((Port)other).gpio;
    } // equals(Object)
    
/** Hashcode. <br />
 *  
 *   @see #equals(Object)
 *   @return {@link #gpio}
 */
    @Override public final int hashCode(){ return this.gpio; }    
  } // Port (06.06.2021) 

/** Make an input/output port for this Pi. <br />
 *  <br />
 *  For this {@link ThePi} object and associated with it a {@link Port}
 *  object will be made an returned. <br />
 *  Hint: Due to the association a {@link ThePi} object can't be garbage
 *  collected before all of the {@link Port} objects made for it.
 *
 *  @param name a short (best 7 char) description of the pin's attached device
 *              like "red LED", "servo16" etc.; must not be empty 
 *  @param pin 0, 1..40 (26 [+8]) is the legal IO connector pin number
 *  @return an operable Port object on success
 *  @throws IOException if the pin does not allow input and output operations
 *          with a comprehensive message     
 */  
  public default Port portByPin(int pin, String name) throws IOException {
    name = TextHelper.trimUq(name, null);
    if (name == null) throw new IOException("pin " + pin 
                            + " for no or empty signal name");
    final int gpio = gpio4pin(pin);
    if (gpio >= GPIOmin && gpio <= GPIOutM) return new Port(gpio, pin, name, this);
    
    throw new IOException("pin " + pin + " for " + name + " = "  
                                                        + gpio2String(gpio));
  } // portByPin(int, String)

/** Make an input/output port for this Pi. <br /> 
 * 
 *  @param name a short (best 7 char) description of the pin's attached device
 *              like "red LED", "servo16" etc.; must not be empty 
 *  @param gpio 0..32 or {@link #PINig} an operable / legal GPIO number
 *  @return an operable Port object on success
 *  @throws IOException if the pin does not allow the IO operation intended
 *          with a comprehensive message     
 */  
public default Port portByGPIO(int gpio, String name) throws IOException {
  name = TextHelper.trimUq(name, null);
  int pin = gpio2pin(gpio);
  if (name == null) throw new IOException("pin " + pin 
                                         + " for no or empty signal name");
  if (pin != 0 || gpio == PINig) return new Port(gpio, pin, name, this);
  throw new IOException("pin " + pin + " for " + name + " = "  
                                                      + gpio2String(gpio));
} // portByGPIO(int, String)
  
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
  } // gpioMayOut(int)

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
  
  public default String pinDescr(int pin, int pud){
    if (pin <= 0 || pin > 40) return  "noneIgn"; 
    int gpio = gpio4pin(pin);
    if (gpio >= PiVals.PINig) {
      if (gpio == PiVals.PIN0V) return "gnd_0V";
      if (gpio == PiVals.PIN3V) return "sup3V3";
      if (gpio == PiVals.PIN5V) return "sup_5V";
      return  "noneIgn";
    }
    StringBuilder dest = new StringBuilder(10);
    PiUtil.twoDigitDec(dest, pin);
    if (pud >= PI_PUD_OFF && pud <= PI_PUD_UP) dest.append(Port.pudC[pud]);
    dest.append('G');  
    PiUtil.twoDigitDec(dest, gpio);
    return dest.toString();    
  } // pinDescr(2*int)

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
 
/** Pin number array to GPIO mask lookup. <br />
 *  <br />
 *  @param pins an array of legal pin numbers 1..40 (26 [+8]) or 0 for ignore
 *  @return a bit mask for the GPIO numbers 0..31 set by the array pins.
 *     0 means no non 0 entry in pins or erroneous entry or entries in pins
 *  @see #gpios4pinsChck(CharSequence, int[])   
 */
 public default int gpios4pins(final int[] pins) {
   int ret = 0; // the mask to return 
   for (int pin : pins) {
      final int gpio = gpio4pin(pin);
      if (gpio == PINig) continue; // ignore do nothing
      if (gpio < GPIOmin || gpio > GPIOutM) return 0; // return 0 on error
      ret |= ClientPigpiod.gpio2bit[gpio]; 
   }  // for
   return ret;
 } // gpios4pins(int[])

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
 *  as {@link #gpio4pin(int)}. Additionally it raises an
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

/** Pins array to GPIO mask lookup with check. <br />
 *  <br />
 *  This method does the same pin to GPIO look up as
 *  {@link #gpios4pins(int[])}. Additionally it raises an
 *  {@link IOException} with a comprehensive message, when the pin is no
 *  real IO pin for a GPIO in the range 0..31 (instead of just returning 0).
 *  
 *  @param signal a short (best 7 char) description of the pin / signal
 *         or device group like "redLEDs", "relaysH" etc. 
 *         It is used for the IOexception message, only
 *  @throws IOException on the first pinin the array not allowing
 *         the IO operation intended with a comprehensive message
 *  @see #gpios4pins(int[])   
 */
  public default int gpios4pinsChck(CharSequence signal, final int[] pins)
                                                         throws IOException {
    int ret = 0; // the mask to return 
    for (int pin : pins) {
       final int gpio = gpio4pin(pin);
       if (gpio == PINig) continue; // ignore do nothing
       if (gpio < GPIOmin || gpio > GPIOutM) throw new IOException("pin "
               + pin + " for " + signal + " = "  + ThePi.gpio2String(gpio));
       ret |= ClientPigpiod.gpio2bit[gpio]; 
    }  // for
    return ret;
  } // gpios4pinsChck(CharSequence,int[])


 
//--------------------- Object properties --------------------------  

/** The Pi's socket port number for a pigpiod socket connection. <br />
 *  <br />
 *  Not used by {@link ThePi} itself; see {@link ClientPigpiod}.<br />
 *  default: 8888    
 */
   public int sockP();

/** The Pi's hostname for a pigpiod socket connection. <br />
 *  <br />
 *  Not used by {@link ThePi} itself; see {@link ClientPigpiod}.<br />
 *  default: {@link ThePi#defaultHost}  
 */
  public String host();

/** The Pi's tiemout for a pigpiod socket connect in ms. <br />
 *  <br />
 *  Not used by {@link ThePi} itself; see {@link ClientPigpiod}.<br />
 *  default: 10000  (10s)  
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
 *     (socket) {@link #sockP() port} and  {@link #timeout() timeout} 
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
  
/** <b>Predefined behaviour for ThePi objects</b>. <br />
 *  <br />
 */
  public abstract class ComBeh implements ThePi {
    
/** Hidden (package) constructor. <br > */    
    ComBeh(final int type,
                    final String host, final int port, final int timeout){
      this.type = type == 0 || type == 1 || type == 2 || type == 4 ? type : 3;
      this.hostPi = host == null || host.length() < 3 ? defaultHost : host;
      this.portPi = port < 20 || port > 65535 ? 8888 : port;
      this.timoutPi = timeout < 300 || timeout > 50000 ? 10000 : timeout;
    } // ComBeh(int)
    
/** The type. <br />
 *  <br />
 *  The Pi's type as a (one decimal digit) value: 0, 1, 2, 3 or 4.    
 */
    public final int type;
    
/** The Pi's type. <br />
 *
 *  @return {@link #type}.
 */
    @Override public int type(){ return this.type; }
    
    String hostPi;
    int portPi;
    int timoutPi;      

/** The Pi's socket port number for a pigpiod socket connection. <br />
 *  <br />
 *  Not used by {@link ThePi} itself; see {@link ClientPigpiod}.<br />
 *  default: 8888    
 */
   @Override public int sockP(){ return this.portPi; }
    
    
/** The Pi's hostname for a pigpiod socket connection. <br />
 *  <br />
 *  Not used by {@link ThePi} itself; see {@link ClientPigpiod}.<br />
 *  default: {@link ThePi#defaultHost}  
 */
    @Override public String host(){ return this.hostPi; }
    
/** The Pi's tiemout for a pigpiod socket connect in ms. <br />
 *  <br />
 *  Not used by {@link ThePi} itself; see {@link ClientPigpiod}.<br />
 *  default: 10000  (10s)  
 */
    @Override public int timeout(){ return this.timoutPi; }

/** The Pi as text. <br />
 *  
 *  @return Pi0, Pi1 ... Pi4 (depending on {@link #type()}
 */
    @Override public String toString(){
      char a[] = {'P', 'i', (char)('0' + type()) };
      return String.valueOf(a);
    } // toString()

/** Equal with other Pi. <br />
 *  <br />
 *  From the physical sight of a Raspberry Pi's process IO to outside world
 *  i.e. the IO and supply pins and properties two Pis of the same 
 *  {@link #type()} are equal.<br />
 *  The {@link #host() host} etc. have multiple possible values (like 
 *  localhost, names, varying and multiple IPs) that do not affect the
 *  Pi's process IO aspect and hence neither {@link #equals(Object)} nor
 *  {@link #hashCode()} here. But it will in an associated 
 *  {@link ClientPigpiod} object.<br />
 *  This sight is implemented here and, consistently, in {@link #hashCode()}. 
 *   @return true when other is a {@code ThePi} object of same type
 */
    @Override public final boolean equals(final Object other){
      if (! (other instanceof ThePi)) return false;
      return this.type == ((ThePi)other).type();
    } // equals(Object)
    
/** Hashcode. <br />
 *  
 *   @see #equals(Object)
 *   @return {@link #type}
 */
    @Override public final int hashCode(){ return this.type; }    
    
  } // ThePi.ComBeh
} // ThePi  (06.04.2021)
