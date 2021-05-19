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

import de.frame4j.util.ComVar;

/** <b>Type independent definitions for Raspberry Pis.</b> <br />
 *  <br />
 *  <a href=package-summary.html#co>&copy;</a> 
 *  Copyright 2021 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  This interface contains values for all Raspberry Pis and related 
 *  applications. 
 *  @author   Albrecht Weinert
 *  @version  $Revision: 49 $ ($Date: 2021-05-19 16:47:26 +0200 (Mi, 19 Mai 2021) $)
 */
// so far:  V. 38  (16.04.2021) :  new
//          V. 4x  (21.05.202x) :  ...

public interface PiVals extends ComVar { 
  
/** Defines maximal existing GPIO number. <br />
 *  <br />
 *  Value: {@value}
 *  @see #GPIOmin
 *  @see #GPIOutM #PINig
 */
    int GPIOmax = 56; // this value must be 

/** Defines smallest existing GPIO number. <br />
 *  <br />
 *  Value: {@value}
 *  @see #GPIOmax
 *  @see #GPIOutM #PINig
 */
    int GPIOmin = 0; // this value must be 

/** Defines highest GPIO number allowed for output. <br />
 *  <br />
 *  Principally all GPIO numbers in the range 
 *  {@code  {@link #GPIOmin} .. {@link #GPIOmax} = 0..56} could be 
 *  used for input and output &ndash; and pigpiod would allow this. <br />
 *  Nevertheless it is highly recommended not to use GPIO numbers above
 *  {@code  {@link #GPIOouM} = 31} for output. <br />
 *  Rationale: Any output related operation to GPIO 32 to 56 may spoil
 *  the operating system. <br />
 *  Hence, all output related methods of this framework will reject the
 *  attempt as error. <br />
 *  <br />
 *  Value: {@value}
 *  @see #GPIOmin
 *  @see #GPIOmax
 *  @see #PINig
 *  @see ThePi#gpioMayOut(int)
 */
    int GPIOutM = 31; // this value must be 
  
/** Defines non existing GPIO number for a disabled GPIO pin. <br />
 *  <br />
 *  Assigning this special undefined GPIO number to a GPIO variable or 
 *  parameter means not assigned or disabled. All IO methods of this
 *  framework will accept this value as GPIO parameter and and won't do any
 *  action on it nor raise an error.<br />
 *  Value: {code {@value} = {@link #GPIOmax} + 1} 
 */
  int PINig = 57;

/** A GPIO number to mark a non existing pin. <br />
 *  <br />
 *  This special undefined GPIO number will be returned for / assigned to
 *  non existing pin numbers, i.e. outside [1..40] for Pi3, Pi4 and Pi0
 *  and outside [1..26] for Pi1. In any sequence of GPIO numbers this 
 *  value, also, marks the end (as last+1 entry).<br />
 *  Value: {@value}<br />
 *  Nota bene: PINix as all constants called PIN** are GPIO numbers.
 *  A non existing pin or one for a non existent signal will get the pin
 *  number 0. The smallest possible pin number on a pin connector is 1.
 */
  int PINix = 99;
  
/** Defines a Ground pin. <br />
 *  <br />
 *  This special undefined GPIO number will be assigned to respectively
 *  returned for ground pins (Gnd, 0V). Methods asked to act on this
 *  pseudo GPIO number are free to do nothing (ignore) or raise an
 *  error. The recommended behaviour is ignore.<br />
 *  Value: {@value}
 */
  int PIN0V = 90;
  
/** Defines a 3.3 V pin. <br />
 *  <br />
 *  This special undefined GPIO number will be assigned to respectively
 *  returned for µC supply voltage pins (3.3V). Methods asked to act on this
 *  pseudo GPIO number are free to do nothing (ignore) or raise an
 *  error. The recommended behaviour is ignore.<br />
 *  Value: {@value}
 */
  int PIN3V = 93;

/** Defines a 5 V pin. <br />
 *  <br />
 *  This special undefined GPIO number will be assigned to respectively
 *  returned for µC supply voltage pins (3.3V). Methods asked to act on this
 *  pseudo GPIO number are free to do nothing (ignore) or raise an
 *  error. The recommended behaviour is ignore.<br />
 *  Value: {@value}
 */
  int PIN5V = 95;
  
//--  pin connector assignment common to all Pi types  
   
/** The Pi's GPIO pin connector mapping. <br />
 *  <br />
 *  There are int constants by name PINn and PINnn with hn resp. n in the
 *  range 1 resp. 01 ti 40 with the GPIO number or function of the Pi type
 *  as value. <br />
 *  <br />
 *  As there is no pin 0 the extra constants {@link #PIN00} and 
 *  {@link #PIN0} have the value {@link #PINix} (non existent pin; error). <br />
 *  <br />
 *  This is ported from arch/config_raspberry_03.h 
 */
  public static final int PIN00  = PINix, PIN0 = PINix;


/** The Pi's 3.3 V µC supply pins. <br />
 *  <br />
 *  The 3.3 V pins are 1 and 17 on all Pi types denoted by constants
 *  {@link #PIN1}, {@link #PIN01} and {@link #PIN17} with value
 *  {@link #PIN3V}.
 */
  public static final int PIN01 = PIN3V, PIN1 = PIN3V, 
                          PIN17 = PIN3V; // 3.3 V µC supply


/** The Pi's 5.0 V µC board pins. <br />
 *  <br />
 *  The 5 V pins are 2 and 4 on all Pi types denoted by constants
 *  {@link PIN2}, {@link PIN02}, {@link PIN4} and {@link PIN04} with value
 *  {@link PIN5V}.
 */
  public static final int PIN02 = PIN5V, PIN2 = PIN5V, // 5.0 V Pi supply
                          PIN04 = PIN5V, PIN4 = PIN5V ; // 5.0 V Pi supply

/** The Pi's ground pins. <br />
 *  <br />
 *  The pins 6 ({@link PIN6}, {@link PIN06}), 9, 14 20 and 25 are 
 *  ground (gnd, 0V) pins on all Pi types, value {@link PIN0V}. <br />
 *  On those with a 40 pin connector (Pi 3,4 0) the pins 30, 34 and 39
 *  are Gnd, too.
 */
  public static final int PIN06 = PIN0V,  PIN6 = PIN0V, // Gnd
                          PIN09 = PIN0V,  PIN9 = PIN0V, // Gnd
                          PIN14 = PIN0V, PIN20 = PIN0V, // Gnd    
                          PIN25 = PIN0V; // Gnd
  

  

//------------  common constants for Pi GPIO ----------------------------- 
  
/** Hi, ON. <br />
 *  <br />
 *  This is the boolean variant of High, On, An, Go. See also the int variant
 *  {@link H1}. In pure C software this distinction is not necessary as there
 *  is no boolean and 0 is false and any other value, as e.g. 1, is considered
 *  true. <br />
 *  In pure Java the numeric variant would be obsolet, but here we communicate
 *  with C software partly expecting integer values for true and false.<br />
 *  See {@link HI},  {@link LO};  {@link H1},  {@link L0} and note the boolean
 *  variants ending with capital letters (I, O) and the int variants with
 *  digits.
 */
  public static final boolean HI = true;
  
/** Lo, OFF. <br />
 *  <br />
 *  This is the boolean variant of Low, Off, Aus, Halt.<br />
 *  See the explanation at {@link HI}.
 */
  public static final boolean LO = false;

/** Hi, ON. <br />
 *  <br />
 *  See the explanation at {@link HI}; see also {@link L0}.
 */
  public static final int H1 = 1;

/** Lo, OFF. <br />
 *  <br />
 *  See the explanation at {@link HI}; see also {@link H1}.
 */
  public static final int L0 = 0;

 
/** GPIO input mode. <br /> */
  public static final int GPIO_INP = 0;

/** GPIO output mode. <br /> */
  public static final int GPIO_OUT = 1;

/** GPIO alternative mode 0. <br /> */
  public static final int PIO_ALT0 = 4;

/** GPIO alternative mode 1. <br /> */
  public static final int PIO_ALT1 = 5;
/** GPIO alternative mode 2. <br /> */
  public static final int PIO_ALT2 = 6;
/** GPIO alternative mode 3. <br /> */
  public static final int PIO_ALT3 = 7;
/** GPIO alternative mode 4. */
  public static final int PIO_ALT4 = 3;
/** GPIO alternative mode 5. <br /> */
  public static final int PIO_ALT5 = 2;

  public static final int PI_PUD_OFF   =  0; // free float Off none
  public static final int PI_PUD_DOWN  =  1; // pull down
  public static final int PI_PUD_UP    =  2; // pull up

/** Leave pull resistor setting unchanged. <br />
 *  <br />
 *  This is an illegal value (4) just one above the legal ones (0..2).
 *  Its purpose is to state the current whatever pull setting shall be left
 *  untouched.
 */
  public static final int PI_PUD_KP = 4;

/** Leave pull resistor setting as defaulted. <br />
 *  <br />
 *  This is an illegal value (3) just one above the legal ones (0..2).
 *  Its purpose is to state that an otherwise defined default value shall be
 *  used. If no such default value is known, the current pull setting shall
 *  be left untouched.
 */
  public static final int PI_PUD_DT = 3;
} // PiVals (16.04.2021)
