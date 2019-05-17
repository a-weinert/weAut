/** <b>Definitions for the usage of the Raspberry Pi and its I/O. <br />
 *  <br />
 *  <a href=./de/weAut/package-summary.html#co>&copy;</a> 
 *  Copyright 2019 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 18 $ ($Date: 2019-05-17 14:18:26 +0200 (Fr, 17 Mai 2019) $)
 */
package de.weAut;

import java.util.concurrent.TimeUnit;

public interface PiUsage {
   
/** The default lock file.
 * 
 *  This is the (default) path of the file to lock the exclusive use of
 *  the pigpoi[d] on the RasPi. It is (has to be) the same as with control
 *  programs written in C.   
 */
  public final String lckPiGpioPth = "/home/pi/bin/.lockPiGpio";
  
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
  
/** <b>Implementations for Raspberry Pi IO</b>.
 *   
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
