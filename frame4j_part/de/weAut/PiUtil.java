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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/** <b>Constants and methods for the Raspberry Pi and its I/O</b>.<br />
 *  <br />
 *  The methods here are all implemented. <br />
 *  <br />
 *  Copyright <a href=./de/weAut/package-summary.html#co>&copy;</a> 2019
 *           &nbsp; Albrecht Weinert<br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 30 $ ($Date: 2019-06-07 19:32:55 +0200 (Fr, 07 Jun 2019) $)
 */
// so far:   V. o19  (17.05.2019) : new
//           V.  21  (19.05.2019) : ALT numbers, typo
//           V.  25  (27.05.2019) : enhanced error numbers 

public interface PiUtil {

// ------------  common constants for Pi GPIO ----------------------------- 
   
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

// GPIO constants deprecated, use PiGpioDdefs    
/** GPIO input mode. */
   public static final int GPIO_INP = 0;

/** GPIO output mode. */
   public static final int GPIO_OUT = 1;

/** GPIO alternative mode 0. */
   public static final int PIO_ALT0 = 4;
/** GPIO alternative mode 1. */
   public static final int PIO_ALT1 = 5;
/** GPIO alternative mode 2. */
   public static final int PIO_ALT2 = 6;
/** GPIO alternative mode 3. */
   public static final int PIO_ALT3 = 7;
/** GPIO alternative mode 4. */
   public static final int PIO_ALT4 = 3;
/** GPIO alternative mode 5. */
   public static final int PIO_ALT5 = 2;
   
// ------------ de.weAut (Java on Pi) error handling ----------------------   

/** Lock process can not be started. <br />
 *  <br />
 *  This usually means no lock process existing on the Raspberry
 *  platform. More info in {@link Impl#lastExept}.
 */ 
   public static final int ERR_NoLOCKPROC = 96; // no lock process start

/** Lock file does not exist. <br />
 *  <br />
 *  This usually means locking respectively using GPIO is forbidden for now.
 */ 
   public static final int ERR_NoLOCKFILE = 97; // lock process exit value *)
   
/** Lock file could not be locked. <br />
 *  <br />
 *  This usually means locking respectively using GPIO is momentarily 
 *  forbidden.
 */ 
   public static final int ERR_NOT_LOCKED = 98; // lock process exit value *)

/** Program has no GPIO lock. <br />
 *  <br />
 *  Some operations (like e.g. {@link #openWatchdog()}) will be rejected 
 *  when the program is not owning the GPIO lock.
 */ 
   public static final int ERR_NoGPIOLOCK = 99; // lock process exit value *)

/** Can't open watchdog. */
   public static final int ERR_OPEN_W_DOG = 100;
   
/** Can't close the watchdog. <br />
 *  <br />
 *  Not being able to close the watchdog usually leads to a reset.   
 */
   public static final int ERR_CLOSE_WDOG = 101;

// *) Other programmes / librariy's exit values; do NOT change here    

/** Get error text. <br />
 *  <br >
 *  This function returns an error text for this type's error numbers.
 *  
 *  @param errNum the error number; 0: OK gets an empty String
 *  @return  the error text (never null)
 */
   public default String errorText(final int errNum){
       return impl.errorText(errNum);
   } // getErrorText(int)
 
  
//------------  GPIO lock (file) handling    -----------------------------  
   

/** Common path to a lock file for GpIO use. */
   static public final String lckPiGpioPth = "/home/pi/bin/.lockPiGpio";

  
  
/** Open and lock lock file. <br />
 * 
 *  @param lckPiGpioFil if not null or empty use this path instead of the
 *                       platform default one (/home/pi/bin/.lockPiGpio)
 *  @param verbose true: make the lock process verbose (by option -v) on
 *                       standard output                     
 *  @return  err 0: OK; else: error, see {@link #ERR_NoLOCKFILE}, 
 *                    {@link #ERR_NOT_LOCKED}
 */
   public default int openLock(final String lckPiGpioFil, boolean verbose){
       return impl.openLock(lckPiGpioFil,verbose);
   } // openLock(String, boolean)

/**  Unlock the lock file. */
  public default void closeLock(){ impl.closeLock(); } 
  
  
//------------  Raspberry Pi / BCM watchdog handling   -------------------   

/** Initialise respectively start the watchdog. <br />
 *  <br />
 *  Starting the watchdog will only be allowed when having got the GPIO lock
 *  by #openLock(). Additionally the watchdog will work only when allowed 
 *  for non root.<br />
 *  <br />
 *  As soon as initialised the watchdog will have to be triggered
 *  ({@link #triggerWatchdog()}) at least every 16 s or closed 
 *  ({@link #closeWatchdog()}). Otherwise the Raspberry Pi will be reset.<br />
 *  <br />
 *  Note: Obviously, for a Java program owning the (GPIO lock and) the 
 *  watchdog to (mis-) use it for a total re-Start.
 *  @return  err 0: OK; else: error
 */
  public default int openWatchdog(){ return impl.openWatchdog(); }
  
/** Trigger the watchdog. */
  public default void triggerWatchdog(){ impl.triggerWatchdog(); } 

/** Close the watchdog. <br />
 *  <br />
 *  @return  err 0: OK; else: error
 */
  public default int closeWatchdog(){ return impl.closeWatchdog(); }

  
//---------------------   thread delay -------------------------------------
/** Periodic delay. <br />
 *  <br />
 *  This method delays the calling thread for the given number of 
 *  milliseconds relative to its last call. Hence, it is able to implement
 *  strong long term periodicity. Called 86400 times with 1000 will end
 *  one day later, e.g..<br />
 *  <br />
 *  If this call's (corrected) relative end time would be in the past
 *  the current delay will be relative to now. In that case an existing long
 *  term periodicity would be destroyed. This would happen if other threads
 *  or processes hinder the wake up of this thread for more than 
 *  millies ms. <br />
 *  <br />
 *  @param millies the number of ms to delay relativ to the last call
 */  
   public default void thrDelay(int millies){ impl.thrDelay(millies); }

/** A tick as mutable object. <br />
 *  <br />
 *  Objects of this class essentially hold a mutable long variable intended 
 *  for absolute times in ms.
 */
   public final class LeTick {
      long tick;
      public long getTick(){ return tick; }
      public void setTick(final long tick){ this.tick = tick; }
      
      public long add(final long adv) { return this.tick += adv; }
      
      public LeTick(long tick) {this.tick = tick; }
   } // LeTick
   
   
// ------------  method (default) implementations    ---------------------  
   
/** Some (default) implementations. */  
   public final Impl impl = new Impl(); // singleton


/** <b>Implementations for Raspberry Pi IO</b>.
 *  <br />
 *  This class provides implementations for the enclosing interface's 
 *  methods and their (minimal) state. In most cases these are sufficient
 *  and would not be overridden.
 */
  public final class Impl {
     private Impl() {}; // singleton
     

/** Get error text. <br />
 *  <br />
 *  This function returns an error text for this type's error numbers.
 * 
 *  @param errNum the error number; 0: OK gets an empty String
 *  @return  the error text (never null)
 */
   public String errorText(final int errNum){
      switch (errNum) {
         case 0: return ""; 
         case ERR_NoLOCKPROC: // 96
            return "no lock process";
         case ERR_NoLOCKFILE: // 97
            return "no lock file";
         case ERR_NOT_LOCKED: // 98
            return "can't lock the lock file"; 
         case ERR_NoGPIOLOCK: // 99
            return "don't has the GPIO lock"; 
         case ERR_OPEN_W_DOG: // 100
            return "can't open watchdog"; 
         case ERR_CLOSE_WDOG: // 101
            return "can't close watchdog"; 
      }
      return "error " + errNum;
   } // getErrorText(int)

/** The process holding the lock file. <br />
 *  <br />
 *  Not to be touched except when overriding {@link openLock}. 
 */
     public Process lockProcess;

/**  Last exception. */     
     public Throwable lastExept;

/** Open and lock lock file. 
 * 
 *  @param lckPiGpioFil if not null or empty use this path instead of the
 *                       default one ({@link lckPiGpioPth})
 *  @param verbose true: make the lock process verbose (by option -v) on
 *                       standard output                     
 *  @return  err 0: OK; else: error, see {@link #ERR_NoLOCKFILE}, 
 *                    {@link #ERR_NOT_LOCKED}
 */
    public int openLock(final String lckPiGpioFil, boolean verbose){
       final String lckPiGpio = lckPiGpioFil != null
             &&  lckPiGpioFil.length() > 3 ? lckPiGpioFil : "";
       try {
          lockProcess = verbose ? 
                Runtime.getRuntime().exec("justLock --verbose " + lckPiGpio)
                      : Runtime.getRuntime().exec("justLock " + lckPiGpio); 
          // little delay needed here 
          lockProcess.waitFor(10, TimeUnit.MILLISECONDS);
          if (! (lockProcess.isAlive())) {
             return lockProcess.exitValue();
          } // not running lock process
       } catch (Throwable ex) {
          lastExept = ex;
          return ERR_NoLOCKPROC;
       }
       return 0;
    } // openLock(String, boolean)

/**  Unlock the lock file. */
    public void closeLock(){
        if (lockProcess != null) lockProcess.destroy();
    } // closeLock()
    
    FileOutputStream wDog;

/** Initialise respectively start the watchdog. <br />
 *  <vr />
 *  Starting the watchdog will only be allowed when having got the GPIO lock
 *  by #openLock(). Additionally the watchdog will work only when allowed 
 *  for non root. To use the watchdog in Java programs with this library the
 *  watchdog's access rights have to be changed; see the publication
 *  <a href="http://a-weinert.de/pub/raspberry4remoteServices.pdf" 
 *  title="by Albrecht Weinert">Raspberry for remote services</a>.<br />
 *  <br />
 *  As soon as initialised the watchdog will have to be triggered
 *  ({@link #triggerWatchdog()}) at least every 16 s or closed 
 *  ({@link #closeWatchdog()}). Otherwise the Raspberry Pi will be reset.<br />
 *  <br />
 *  Note: Obviously, for a Java program owning the (GPIO lock and) the 
 *  watchdog to (mis-) use it for a total re-Start.
 *  @return  err 0: OK; else: error
 */
     public int openWatchdog(){ 
        if (lockProcess == null) return ERR_NoGPIOLOCK;
        try {
           wDog = new FileOutputStream("/dev/watchdog");
        } catch (Throwable ex) {
           lastExept = ex;
           return ERR_OPEN_W_DOG;
        }
        return 0; 
     } // openWatchdog()
  
/** Trigger the watchdog. */
     public void triggerWatchdog(){ 
        if (wDog != null) try {
           wDog.write('X'); // write(watchdog, "X", 1);
        } catch (IOException e) {
           lastExept = e;
        } // ignore (write fault means reset)
     } // triggerWatchdog() 

/** Close the watchdog.
 * 
 *  @return  err 0: OK; else: error
 */
     public int closeWatchdog(){
      if (wDog != null) try {
         wDog.write('V');
         wDog.close();
      } catch (IOException e) {
         lastExept = e;
         return ERR_CLOSE_WDOG;
      } 
         return 0; 
    } // closeWatchdog()

/** Periodic delay. <br />
 *  <br />
 *  This method delays the calling thread for the given number of 
 *  milliseconds relative to its last call. Hence, it is able to implement
 *  strong long term periodicity. Called 86400 times with 1000 will end
 *  one day later, e.g..<br />
 *  <br />
 *  If this call's (corrected) relative end time would be in the past
 *  the current delay will be relative to now. In that case an existing long
 *  term periodicity would be destroyed. This would happen if other threads
 *  or processes hinder the wake up of this thread for more than 
 *  millies ms. <br />
 *  <br />
 *  @param millies the number of ms to delay relativ to the last call
 */  
   public void thrDelay(int millies){
      if (millies < 1) return; // must be positive
      long now = java.lang.System.currentTimeMillis();
      LeTick leTick = lastThTick.get();
      if (leTick == null) { //
         leTick = new LeTick(now + millies); // first time: start now
      } else { // have threads last tick
         long target = leTick.add(millies);
         if (target > now) {
            millies = (int)(target - now); // keep exact period
            // no new Long object leTick = target;
         } else leTick.setTick(now + millies); // lost exact period start now  
      } //  have threads last tick
      // no new long object lastThTick.set(leTick);
      try {
        Thread.sleep(millies);
     } catch (InterruptedException e) { } // ignore exception
   } // gpioDelay(int)
      
   static public final ThreadLocal<LeTick> lastThTick = new ThreadLocal<>();
   //  long lastTick = 0; // java.lang.System.currentTimeMillis()
   //  long now;
       
  } // Impl
} // PiUsage (28.05.2019)
