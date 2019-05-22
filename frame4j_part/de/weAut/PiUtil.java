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

import java.util.concurrent.TimeUnit;

/** <b>Definitions for the usage of the Raspberry Pi and its I/O</b>.<br />
 *  <br />
 *  <a href=./de/weAut/package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 21 $ ($Date: 2019-05-22 13:35:56 +0200 (Mi, 22 Mai 2019) $)
 */
// so far:   V. 19  (17.05.2019) :  new
//           V. 21  (19.05.2019) :  ALT numbers, typo

public interface PiUtil {

// ------------  common constants for Pi GPIO -----------------------------   
/** Hi, ON.
 * 
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
   
/** Lo, OFF.
 * 
 *  This is the boolean variant of Low, Off, Aus, Halt.<br />
 *  See the explanation at {@link HI}.
 */
   public static final boolean LO = false;

/**  Hi, ON.
 * 
 *  See the explanation at {@link HI}; see also {@link L0}.
 */
   public static final int H1 = 1;

/** Lo, OFF.
 * 
 *  See the explanation at {@link HI}; see also {@link H1}.
 */
   public static final int L0 = 0;

// GPIO constants deprecated, use PiGpioDdefs    
/** GPIO input mode. */
   public static final int GPIO_INP = 0;

/** GPIO output mode. */
   public static final int GPIO_OUT = 1;

/** GPIO alternative mode. */
   public static final int PIO_ALT0 = 4;
   public static final int PIO_ALT1 = 5;
   public static final int PIO_ALT2 = 6;
   public static final int PIO_ALT3 = 7;
   public static final int PIO_ALT4 = 3;
   public static final int PIO_ALT5 = 2;

   
// ------------  GPIO lock (file) handling    -----------------------------   
   
/** The default lock file.
 * 
 *  This is the (default) path of the file to lock the exclusive use of
 *  the pigpoi[d] on the RasPi. It is (has to be) the same as with control
 *  programs written in C.   
 */
  public static final String lckPiGpioPth = "/home/pi/bin/.lockPiGpio";
  
/** Some (default) implementations. */  
  public final Impl impl = new Impl(); // singleton
  
  
/** Open and lock lock file. 
* 
*  @param lckPiGpioFil if not null or empty use this path instead of the
*                       default one ({@link lckPiGpioPth})
*  @param perr when true protocol errors (on System.out as of now)                    
*/
   public default boolean openLock(final String lckPiGpioFil, 
                                                     final boolean perr){
       return impl.openLock(lckPiGpioFil, perr);
   } // openLock(String, boolean)

/**  Unlock the lock file. */
  public default void closeLock(){ impl.closeLock(); } 
  

// ------------  method (default) implementations    -----------------------   

/** <b>Implementations for Raspberry Pi IO</b>.
 *  
 *  This class provides implementations for the enclosing interface's 
 *  methods and their (minimal) state. In most cases these are sufficient
 *  and would not be overridden.
 */
  public class Impl {
     private Impl() {}; // singleton

/** The process holding the lock file.
 * 
 *  Not to be touched except when overriding {@link openLock}. 
 */
     public Process lockProcess;

/** Open and lock lock lock file. 
* 
*  @param lckPiGpioFil if not null or empty use this path instead of the
*                       default one ({@link lckPiGpioPth})
*  @param perr when true protocol errors (on System.out as of now)                    
*/
    boolean openLock(final String lckPiGpioFil, final boolean perr){
       final String lckPiGpio = lckPiGpioFil != null
             &&  lckPiGpioFil.length() > 3 ? lckPiGpioFil : lckPiGpioPth;
       try {
          lockProcess = Runtime.getRuntime().exec("justLock " + lckPiGpio); 
          // little delay needed here 
          lockProcess.waitFor(10, TimeUnit.MILLISECONDS);
          if (! (lockProcess.isAlive())) {
             int exV = lockProcess.exitValue();
             if (perr) System.out.println("justLock has stopped " + exV);
             return false;
          } // not running lock process
       } catch (Throwable ex) {
          if (perr) System.out.println("can't run justLock " + lckPiGpio);
          ex.printStackTrace();
          return false;
       }
       return true;
    } // openLock(String, boolean)

/**  Unlock the lock file. */
    void closeLock(){
        if (lockProcess != null) lockProcess.destroy();
    } // closeLock()
  } // Impl
} // PiUsage
