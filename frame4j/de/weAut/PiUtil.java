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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import de.frame4j.text.TextHelper;
import de.frame4j.util.ComVar;

/** <b>Constants and methods for the Raspberry Pi and its I/O</b>.<br />
 *  <br />
 *  The methods here are all implemented
 *  (featuring a kind of multiple inheritance). <br />
 *  An application wanting to use {@link ThePi}, {@link ClientPigpiod}
 *  etc. must implement this interface {@link PiUtil}. 
 *  <br />
 *  Copyright <a href=package-summary.html#co>&copy;</a> 2019, 2021
 *           &nbsp; Albrecht Weinert<br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 42 $ ($Date: 2021-05-01 18:54:54 +0200 (Sa, 01 Mai 2021) $)
 */
// so far:   V. o19  (17.05.2019) : new
//           V.  21  (19.05.2019) : ALT numbers, typo
//           V.  25  (27.05.2019) : enhanced error numbers 
//           V.  36  (06.04.2021) : re-work
public interface PiUtil extends PiVals {
  
/** The standard output. <br />
 *  <br />
 *  Hint: A {@link PrintWriter} is an {@link Appendable}.
 */  
  public default PrintWriter getOut(){
    if (this instanceof de.frame4j.util.App) {
      return ((de.frame4j.util.App)this).out;
    }
    return Impl.getOut();
  } // getOut()

/** System exit. <br />
 *  <br />
 *  This method flushes {@link #getOut out}, waits a few ms to let that
 *  happen and then lets end all with {@code System.exit(ret)}. <br />
 *  This methods end the programme (the JVM) and, hence, never returns. <br />
 *  @param ret the return code 
 */
  public default void systemExit(final int ret){
    if (this instanceof de.frame4j.util.App) {
      ((de.frame4j.util.App)this).errorExit(ret, null);
    }
    getOut().flush();
    try { Thread.sleep(8);} catch (Exception ex) {} 
    System.exit(ret);
  } // systemExit(int)

  
/** Register this as standard MBean. <br />
 * 
 *  @return the registered object name
 *  @throws JMException if the registering fails 
 */
  public default String regAsStdMBean() throws JMException {
    MBeanServer platformMBeanServer =
                            ManagementFactory.getPlatformMBeanServer();
    ObjectName objectName = null;
    Class<?> clasz = this.getClass();
    String fullName = clasz.getName();
    String fullMBname = fullName + "MBean";
    Class<?>[] intfacs = clasz.getInterfaces();
    Class<?> mBintf = null;
    for (Class<?> i : intfacs) {
      if (fullMBname.equals(i.getName())) { mBintf = i; break; }
    } // for
    if (mBintf == null) throw  // not implementing m.y.SelfMBean
      new NotCompliantMBeanException("MBean interface not implemented");
    int fnL = fullName.length();
    int lastDot = fullName.lastIndexOf ('.');
    if (lastDot == -1 || (lastDot + 2) >= fnL) throw  // not package.class
      new NotCompliantMBeanException("no package.class");
    String oName = fullName.substring (0, lastDot) + ":type="
                                + fullName.substring (lastDot + 1);
    objectName = new ObjectName(oName);
    platformMBeanServer.registerMBean(this, objectName);
    return oName;
  } // regAsStdMBean()

/** Report an exception. <br />
 * 
 *  @param out the output for the report
 *  @param exc the exception to report on
 *  @param trace true: print also a stack trace
 *               (and only if out is a PrintWriter or a PrrintStream)
 */
  public default void repExc(final Appendable out, final Throwable exc,
                                     final boolean trace){
    if (out == null || exc == null) return;
    Throwable ex = exc.getCause();
    if (ex == null) ex = exc;
    try {
      out.append("\n  ").append(PROG_SHORT).append(' ')
         .append(ex.getClass().getName());
      out.append("\n  ").append(PROG_SHORT).append(' ')
         .append(ex.getMessage()).append('\n');
      if (out instanceof Flushable) ((Flushable)out).flush();
    } catch (IOException e) { // should not happen for usual output objects
      return; // just ignore
    } // should not happen for StringBuilder, PrintWriter, PrintStream etc.
    if (trace) {
      if (out instanceof PrintWriter) {
        exc.printStackTrace((PrintWriter) out);
      } else if (out instanceof PrintStream) {
        exc.printStackTrace((PrintStream) out);
      } 
    } // trace
  } // repExc(Appendable, Throwable) 

  
// ------------ common Frame4J resources ----------------------------------

/** Format as two digit decimal number with leading zero. <br />
 *  <br />
 *  Appended to {@code dest} is the two digit decimal representation of
 *  {@code value} if in the range 0..99's. Otherwise two blanks are appended 
 *  bits.<br />
 *  <br />
 *  Hint: This implementation delegates to
 *  {@link TextHelper#twoDigitDec(Appendable, int)}. It is here to facilitate
 *  using {@link PiUtil}, {@link ClientPigpiod} etc. without the 
 *  de.frame4j packages. But why should one want to do this? <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity of 6
 *  @param  value  the number 
 *  @return        dest (appended is &quot;00&quot;..&quot;99&quot;)
 */
   public static Appendable twoDigitDec(Appendable dest, final int value){
     return TextHelper.twoDigitDec(dest, value);
   } // twoDigitDec(Appendable, int)
   
/** Format as eight digit hexadecimal number. <br />
 *  <br />
 *  Appended to {@code dest} (generated StringBuilder if supplied as null) 
 *  is the six digit hexadecimal representation of {@code value}'s lower 24 
 *  bits. This representation is quite usual for 24 bit RGB colours.<br />
 *  <br />
 *  Hint: This implementation delegates to
 *  {@link TextHelper#eightDigitHex(Appendable, int)}. It is here 
 *  to facilitate using {@link PiUtil}, {@link ClientPigpiod} etc. without
 *  the de.frame4j packages. But why should one want to do this? <br />
 *  @param  dest   destination to append to; if null dest is made as
 *                 StringBuilder with initial capacity of 14
 *  @return        dest (appended is &quot;000000&quot;..&quot;FFFFFF&quot;
 *  @see java.awt.Color
 *  @see #twoDigitDec(Appendable, int)
 *  @see #getOut()
 */
   public static Appendable eightDigitHex(Appendable dest, final int value){
     return TextHelper.eightDigitHex(dest, value);
   } // eightDigitHex(Appendable, int)

  
// ------------ de.weAut (Java on Pi) error handling ----------------------   

/** Lock process can not be started. <br />
 *  <br />
 *   This usually means no lock process existing on the Raspberry
 *   platform. More info in {@link Impl#lastExept}.
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

/** Can't open watchdog. <br /> */
   public static final int ERR_OPEN_W_DOG = 100;
   
/** Can't close the watchdog. <br />
 *  <br />
 *  Not being able to close the watchdog usually leads to a reset.   
 */
   public static final int ERR_CLOSE_WDOG = 101;
   
/** Pin assignment error. <br />
 *  <br />
 *  Trying to use a pin for IO that is not existing or no IO port.
 */
    public static final int ERR_ASSIGN_PIN =  86;

/** Socket connecting error. <br />
 *  <br />
 *  Trying to connect to the pigpiod daemon failed.
 */
    public static final int ERR_PIGPIOD_CON =  85;

// *) Other programmes / librariy's exit values; do NOT change here    

/** Get error text. <br />
 *  <br />
 *  This function returns an error text for this type's error numbers
 *  {@link #ERR_NoLOCKPROC}, {@link #ERR_NoLOCKFILE}, {@link #ERR_NOT_LOCKED},
 *  {@link #ERR_NoGPIOLOCK}, {@link #ERR_OPEN_W_DOG},
 *  {@link #ERR_CLOSE_WDOG}. Other Errors return "error -123"
 * 
 *  @param errNum the error number; 0: OK gets an empty String
 *  @return  the error text (never null)
 */
   public static String errorText(final int errNum){
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
        case ERR_PIGPIOD_CON: // 85
          return "can't connect pigpioD";
        case ERR_ASSIGN_PIN: //  86
          return "no IO pin";
     }
     return "error " + errNum;
  } //errorText(int)
   
  
//------------  GPIO lock (file) handling    -----------------------------  

/** Common path to a lock file for Gpio use. <br /> */
  static public final String lckPiGpioPth = "/home/pi/bin/.lockPiGpio";

/** Common path to a lock file for use on Windows. <br /> */
  static public final String lckWinGpioPth = "C:\\util\\.lockPiGpio";

/** Do use lock. <br />
 *  <br />
 *  default: false
 *  @see #setUseLock(boolean)
 */  
  public default boolean getUseLock(){ return Impl.getUseLock(); }
 
/** Do use lock. <br />
 *  <br />
 *  The lock file or process usage is by default false. It will be set 
 *  true by this method. This setting is, thence on, final and valid for all
 *  (Pi IO) programs in this JVM.
 *  @param use true: usage will be set; false: will be ignored
 *  @see #getUseLock()
 */  
  public default void setUseLock(final boolean use){ 
    if (use) Impl.setUseLock();
  } // setUseLock(boolean)
  
/** Open and lock lock file. <br />
 *  <br />
 *  This method uses a C process to lock the file lckPiGpioFil in a manner
 *  compatible with historical standard C file lock:
 *  {code flock(lockFd, LOCK_EX | LOCK_NB);}<br />
 *  For the background see 
 *  <a href="https://a-weinert.github.io/javaIncompFlock.html"
 *     title="Java's incompatible file lock">this blog post</a>.<br />
 *  <br />   
 *  If {@link #getUseLock() useLock} is false this method does nothing and
 *  returns 0.   
 *  @param lckPiGpioFil if not null or empty use this path instead of the
 *                       platform default one (/home/pi/bin/.lockPiGpio)
 *  @param verbose true: make the lock process verbose (by option -v) on
 *                       standard output                     
 *  @return  err 0: OK; else: error, see {@link #ERR_NoLOCKFILE}, 
 *                    {@link #ERR_NOT_LOCKED}
 */
   public default int openLock(final String lckPiGpioFil, boolean verbose){
     return Impl.openLock(lckPiGpioFil, verbose);
   } // openLock(String, boolean)

/** Open and lock lock file on Pi, only. <br />
 *  <br />
 *  If running on a Pi this method does the same as 
 *  {@link #openLock(String, boolean)}.<br />
 *  If not running on a Pi this method does nothing and returns 0 (OK). 
 *  @see #openLock(String, boolean) #ERR_NoLOCKFILE #ERR_NOT_LOCKED
 */
   public default int openLockPi(final String lckPiGpioFil, boolean verbose){
       return ComVar.ON_PI ? Impl.openLock(lckPiGpioFil, verbose) : 0;
   } // openLockPi(String, boolean)

/** Unlock the lock file. <br />
 *  <br />
 *  If no lock file was locked by {@link #openLock(String, boolean)} or
 *  {@link #openLockPi(String, boolean)} this method just does nothing.<br />
 *  Hence, it shall be called in program at places where a lock could be
 *  released, no matter if the program did get the lock. The place would
 *  be after ending the use of IO and 
 *  {@link ClientPigpiod#releaseOutputs() releasing} the outputs used.   
 */
  public default void closeLock(){ if (ComVar.ON_PI) Impl.closeLock(); } 
  
  
//------------  Raspberry Pi / BCM watchdog handling   -------------------   

/** Initialise respectively start the watchdog. <br />
 *  <br />
 *  If not on a PI this method does nothing and returns 0 (OK). <br />
 *  <br />
 *  Starting the watchdog will only be allowed when having got the GPIO lock
 *  by #openLock(). Additionally the watchdog will work only when allowed 
 *  for non root. To use the watchdog in Java programs with this library the
 *  watchdog's access rights have to be changed; see the publication
 *  <a href="https://a-weinert.de/pub/raspberry4remoteServices.pdf" 
 *  title="by Albrecht Weinert">Raspberry for remote services</a>.<br />
 *  <br />
 *  Starting the watchdog will only be allowed when having got the GPIO lock
 *  by {@link #openLock(String, boolean)}. Additionally the watchdog will work only
 *  when allowed (on the Pi) for non root.<br />
 *  <br />
 *  As soon as initialised the watchdog will have to be triggered
 *  (by {@link #triggerWatchdog()}) at least every 16 s or closed 
 *  (by {@link #closeWatchdog()}). Otherwise the Raspberry Pi will be
 *  reset and reboot.<br />
 *  <br />
 *  Note: Obviously, for a Java program owning the (GPIO lock and) the 
 *  watchdog is an opportunity to (mis-) use it for a total re-Start.
 *  @return  err 0: OK; else: error
 */
  public default int openWatchdog(){ return Impl.openWatchdog(); }
  
/** Trigger the watchdog. <br /> */
  public default void triggerWatchdog(){ Impl.triggerWatchdog(); } 

/** Close the watchdog. <br />
 * 
 *  @return  err 0: OK; else: error
 */
  public default int closeWatchdog(){ return Impl.closeWatchdog(); }

  
//---------------------   thread delay -------------------------------------

/** Periodic delay. <br />
 *  <br />
 *  This method delays the calling thread for the given number of 
 *  milliseconds relative to its last call. Hence, it is able to implement
 *  strong long term periodicity. Called 86400 times with 1000 will end
 *  one day later, e.g.. The number of such successful delays can be 
 *  obtained by {@link #getCycCnt()}. <br />
 *  <br />
 *  If this call's (corrected) relative end time would be in the past
 *  the current delay will be relative to now. In that case an existing long
 *  term periodicity will be destroyed. This would happen if other threads
 *  or processes hinder the wake up of this thread for more than 
 *  millies ms. The number of delays having been spoiled so can be 
 *  obtained by {@link #getOvrCnt()}. <br />
 *  <br />
 *  @param millies the number of ms to delay relativ to the last call
 */  
   public default void thrDelay(int millies){ Impl.thrDelay(millies); }

/** Get the number of cycles respectively delays. <br />
 *  <br />   
 *  @return the number of successful thread delays
 *  @see #thrDelay(int) #getOvrCnt()
 */
   public default int getCycCnt(){ return Impl.getCycCnt(); } 

/** Get the number of spoiled delays. <br />
 *  <br />   
 *  @return the number of successful thread delays
 *  @see #thrDelay(int) #getCycCnt()
 */
   public default int getOvrCnt(){ return Impl.getOvrCnt(); } 

/** <b>A tick as mutable object</b>. <br />
 *  <br />
 *  Objects of this class essentially hold a mutable long variable intended 
 *  for absolute times in ms. LeTick is used to implement
 *  {@link PiUtil#thrDelay(int) thrDelay()} with no (or less) throw away 
 *  objects and one (thread local) LeTick object per thread. 
 */
   public final class LeTick {
      long tick;
      int cycCnt; // (sub-) cycle count (counts the adds)
      int ovrCnt; // overrun count (count the sets)

/** Get the tick value. <br />
 *  @see PiUtil#thrDelay(int)
 */      
      public long getTick(){ return tick; }
      
/** Set the tick value. */
      public void setTick(final long tick){ ++ovrCnt; this.tick = tick; }
      
/** Advance or add to the tick value. <br />
 *  <br />      
 *  @param adv the value to be added (will normally be &gt; 0)
 *  @return the advanced value
 */
      public long add(final long adv){ ++cycCnt; return this.tick += adv; }

/** Make with initial value. */      
      public LeTick(long tick){ this.tick = tick; }
   } // LeTick


/** Get the last lock file. <br />
 *  <br />  
 *  @return the name of the last lock file locked and eventually released
 *          (in case of success) or the last file having been tried to lock.
 */
   public default String getLastLockFN(){ return Impl.lastLockF; } 

/** Get the last (recorded) exception. <br />
 *  <br />
 *  The information provided by this method and {@link #getLastExcMess()} is
 *  not threadsafe. Use onla in single threaded program phases like first
 *  initialisation and final clean up.
 */   
   public default Throwable getLastExcept(){ return Impl.lastExept; }

/** Get the last expection's message. <br />
 *  @see  #getLastExcept()
 */    
   public default String getLastExcMess(){ 
     return Impl.lastExept != null ? Impl.lastExept.getMessage() : "none"; 
   } // getLastExcMess() 
   
   
//======   inner class for initialisations and default methods   ==========

/** <b>Internal implementation class</b>. <br />
 *  <br />
 *  The almost single purpose in life of this embedded class is giving
 *  the enclosing interface's default methods some extra abilities (like
 *  state and information hiding).<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright  2001 - 2009, 2015  Albrecht Weinert  
 *  @author   Albrecht Weinert
 *  @version  as enclosing Interface
 */   
static final class Impl {

  private Impl(){} // no objects, no docu

  private static PrintWriter myOut; 
   
  static PrintWriter getOut(){
    if (myOut != null) return myOut;
    // need no sync as System.out is fixed; constructor optimised for
    return myOut = new PrintWriter(System.out, true); // for PrintStream
  } // getOut()

/** The process holding the lock file. <br />
 *  <br />
 *  Not to be touched except when overriding {@link openLock}. 
 */
     static Process lockProcess;

/**  Last exception. <br />*/     
     static Throwable lastExept;

/** Use lock. <br /> */
     static boolean useLock; // default do not use lock file or process
     static void setUseLock(){ useLock = true; }
     static boolean getUseLock(){ return useLock; }
     static String lastLockF = "";
     static RandomAccessFile lockFile; // for Windows file (channel) lock
     static FileLock lock; // for Windows file only (remove when process made)

     
/** Open and lock lock file. <br />
 * 
 *  @param lckPiGpioFil if not null or empty use this path instead of the
 *                       default one ({@link #lckPiGpioPth})
 *  @param verbose true: make the lock process verbose (by option -v) on
 *                       standard output                     
 *  @return  err 0: OK; else: error, see {@link #ERR_NoLOCKFILE}, 
 *                    {@link #ERR_NOT_LOCKED}
 */
    static int openLock(final String lckPiGpioFil, boolean verbose){
      if (! useLock) return 0;
      int ret = 0;
      lastLockF = lckPiGpioFil != null
             &&  lckPiGpioFil.length() > 3 ? lckPiGpioFil
                      : (ComVar.NOT_WINDOWS ? lckPiGpioPth : lckWinGpioPth);
      if (! ComVar.ON_PI) {
        // Windows not implemented yet
        File lockFil;
        FileChannel lockFileChannel = null;
        try {
          lockFil = new File(lastLockF);
          if (lockFil.exists()) {
             lockFile = new RandomAccessFile(lockFil, "rw"); 
          } else {
             ret = ERR_NoLOCKFILE;
          }
        } catch (FileNotFoundException ex) {
          lastExept = ex;
          ret = ERR_NoLOCKFILE;
          lockFile = null;
        }
        if (lockFile != null) lockFileChannel = lockFile.getChannel();  
        if (lockFileChannel == null) {
          ret = ERR_NoLOCKFILE;
        } else try {
          lock =  lockFileChannel.tryLock();
          if (lock == null) ret =  ERR_NOT_LOCKED; // 98
        } catch (IOException ex) {
          lastExept = ex;
          ret =  ERR_NoLOCKPROC; // should not happen98
        }
      } else try { // not on Pi / else try on PI
        lockProcess = verbose ? 
             Runtime.getRuntime().exec("justLock --verbose " + lastLockF)
                      : Runtime.getRuntime().exec("justLock " + lastLockF); 
          // little delay needed here 
        lockProcess.waitFor(10, TimeUnit.MILLISECONDS);
        if (! (lockProcess.isAlive())) {
           ret = lockProcess.exitValue();
        } // not running lock process
      } catch (Throwable ex) {
         lastExept = ex;
         ret = ERR_NoLOCKPROC;
      } // end try on Pi
      if (ret != 0 && verbose) {
        System.out.println("JustNotFLock lock error: " 
                                                 + PiUtil.errorText(ret));
     }
     return ret; // 0: no error

    } // openLock(String, boolean)

/**  Unlock the lock file. <br /> */
    static void closeLock(){
      if (lockProcess != null) lockProcess.destroy();
      if (lock != null) {
        try {
            lock.release();
        } catch (IOException e) { } // ignore
      } // lock
      if (lockFile != null) {
        try {
          lockFile.close();
        } catch (IOException e) { } // ignore
      } // lockFile
    } // closeLock()
    
    static FileOutputStream wDog;

/** Initialise respectively start the watchdog. <br />
 *  <br />
 *  @see PiUtil#openWatchdog() PiUtil#closeWatchdog() PiUtil#triggerWatchdog() 
 *  @return  err 0: OK; else: error
 */
     static int openWatchdog(){
       if (! ComVar.ON_PI) return 0; // do nothing
       if (lockProcess == null) return ERR_NoGPIOLOCK;
       try {
          wDog = new FileOutputStream("/dev/watchdog");
       } catch (Throwable ex) {
          lastExept = ex;
          return ERR_OPEN_W_DOG;
       }
       return 0; 
     } // openWatchdog()
  
/** Trigger the watchdog. <br />
 *  <br />
 *  @see PiUtil#openWatchdog() PiUtil#closeWatchdog() PiUtil#triggerWatchdog() 
 */
     static void triggerWatchdog(){ 
        if (wDog != null) try {
           wDog.write('X'); // write(watchdog, "X", 1);
        } catch (IOException e) {
           lastExept = e;
        } // ignore (write fault means reset)
     } // triggerWatchdog() 

/** Close the watchdog. <br />
 *  <br />
 *  @see PiUtil#openWatchdog() PiUtil#closeWatchdog() PiUtil#triggerWatchdog() 
 *  @return  err 0: OK; else: error
 */
     static int closeWatchdog(){
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
 *  @see PiUtil#thrDelay(int) PiUtil#getCycCnt()
 *  @see PiUtil#getOvrCnt() #getCycCnt() #getOvrCnt()
 *  @param millies the number of ms to delay relativ to the last call
 */  
   static void thrDelay(int millies){
      if (millies < 1) return; // must be positive
      long now = java.lang.System.currentTimeMillis();
      LeTick leTick = lastThTick.get();
      if (leTick == null) { //
        lastThTick.set(leTick = new LeTick(now + millies)); // first time: start now
      } else { // have threads last tick
         long target = leTick.add(millies);
         if (target > now) {
            millies = (int)(target - now); // keep exact period
            // no new Long object leTick = target;
         } else leTick.setTick(now + millies); // lost exact period start now  
      } //  have threads last tick
      try {
        Thread.sleep(millies);
      } catch (InterruptedException e) { } // ignore exception
   } // thrDelay(int)

/** Get the number of cycles respectively delays. <br />
 *  <br /> 
 *  Implementation note on {@link PiUtil.LeTick): This is the count of
 *  {@link PiUtil.LeTick#add(long)} calls.
 *  @return the number of successful thread delays
 *  @see #thrDelay(int) #getOvrCnt()
 */
   static int getCycCnt(){
     LeTick leTick = lastThTick.get();
     if (leTick == null) return 0; // no thread local tick ==> no cycle count
     return leTick.cycCnt;
   } // getCycCnt()

/** Get the number of spoiled delays. <br />
 *  <br /> 
 *  Implementation note on {@link PiUtil.LeTick): This is the count of
 *  {@link PiUtil.LeTick#setTick(long)} calls.
 *  @return the number of successful thread delays
 *  @see #thrDelay(int) #getCycCnt()
 */
   static int getOvrCnt(){
     LeTick leTick = lastThTick.get();
     if (leTick == null) return 0; // no thread local tick ==> no overrun
     return leTick.ovrCnt;
   } // getCycCnt()

/** A container holding one LeTick object per thread. */  
   static final ThreadLocal<LeTick> lastThTick = new ThreadLocal<>();
  } // Impl
} // PiUtil (28.05.2019, 03.04.2021)
