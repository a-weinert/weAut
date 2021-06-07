package  de.weAut.demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import de.frame4j.util.ComVar;
import de.weAut.PiUtil;  // Raspberry Pi handling utilities (IO lock, watchdog)

/** <b>Demo (Test) of incompatible Java and C file lock</b>.<br />
 *  <br />
 *  This program tries to acquire a standard nio lock on the file optionally
 *  given by first argument or on /home/pi/bin/.lockPiGpio. On success it
 *  holds this lock for 1 min 20s or until being interrupted or killed. <br />
 *  <br />
 *  Run by: <pre><code>
 *    java  de.weAut.demos.JustNotFLock [filePath]</code>
 *    default filePath] is {@link de.weAut.PiUtil#lckPiGpioPth} or
 *    {@link de.weAut.PiUtil#lckWinGpioPth} (on Windows)</pre>
 *  
 *  On a Raspberry with a Raspian lite (Linux) this program may demonstrate
 *  that it won't respect a lock on the same file by the little C program
 *  JustLock e.g. &mdash; and vice versa. The reason is the Java'a FileLock
 *  not using the operating system's first (historic standard) file lock
 *  mechanism. <br />
 *  See the <a href="https://a-weinert.github.io/javaIncompFlock.html"
 *  title="Java's incompatible file lock" target="_blank">post</a>.<br /> 
 *  <br />
 *  The repair is to use {@link de.weAut.PiUtil#openLock(String, boolean)} and
 *  {@link de.weAut.PiUtil#closeLock()} instead of overriding them here.<br />
 *  <br />
 *  Copyright  &copy;  2019   Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 50 $ ($Date: 2021-06-04 19:53:05 +0200 (Fr, 04 Jun 2021) $)
 */
// so far:   V. 25  (27.05.2019) :  new, minimal functionality
//           V. 26  (31.05.2019) :  two reads with interpretation 
public class JustNotFLock implements PiUtil {
   
  File lockFil;
  RandomAccessFile lockFile;
  FileChannel lockFileChannel;
  FileLock lock;
  
/** Open and lock lock file. <br />
 * 
 *  @param lckPiGpioFil if not null or empty use this path instead of the
 *                       default one ({@link lckPiGpioPth})
 *  @param verbose true: make the lock process verbose (by option -v) on
 *                       standard output                     
 *  @return  err 0: OK; else: error, see {@link #ERR_NoLOCKFILE}, 
 *                    {@link #ERR_NOT_LOCKED}
 */
  @Override public int openLock(final String lckPiGpioFil, boolean verbose){
   final String lckPiGpio = lckPiGpioFil != null
           &&  lckPiGpioFil.length() > 3 ? lckPiGpioFil : lckPiGpioPth;
   int ret = 0;        
   try {
       lockFil = new File(lckPiGpio);
       if (lockFil.exists()) {
          lockFile = new RandomAccessFile(lockFil, "rw"); 
       } else {
          ret = ERR_NoLOCKFILE;
       }
    } catch (FileNotFoundException e) {
        ret = ERR_NoLOCKFILE;
        lockFile = null;
    }
    if (lockFile != null) lockFileChannel = lockFile.getChannel();  
    if (lockFileChannel == null) {
       ret = ERR_NoLOCKFILE;
    } else try {
       lock =  lockFileChannel.tryLock();
       if (lock == null) ret =  ERR_NOT_LOCKED; // 98
    } catch (IOException e) {
       ret =  ERR_NoLOCKPROC; // should not happen98
    }
    if (ret != 0 && verbose) {
       System.out.println("JustNotFLock lock error: " 
                                                 + PiUtil.errorText(ret));
    }
    return ret; // no error
  } // openLock(String, boolean)

/**  Unlock the lock file. */
  @Override public  void closeLock(){
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

/** The application start.<br />
 *  <br />
 *  If the first start parameter is longer than 3 characters, it will be
 *  taken as lock file path instead of  /home/pi/bin/.lockPiGpio. <br />
 *  <br />
 *  Can be stopped by signal (cntlC), kill command and the like.
 *  @param args start parameters
 */
  public static void main(String[] args){
     new JustNotFLock().doIt(args);  // must be a PiUtil object
  } // main(String[])
  
  boolean runOn;

/** The application's work. */
  public void doIt(String[] args){ 
     final String lockPath = args.length >=1 
                           && args[0].length() > 3 ? args[0]
             : (ComVar.NOT_WINDOWS ? lckPiGpioPth : lckWinGpioPth);
     System.out.println("\nJustNotFLock start: lock file " + lockPath);

     Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        if (runOn) System.out.println("\nJustNotFLock shutdown\n");
        closeLock();
     })); // shutdownHook.run()
     
     final int oL = openLock(lockPath, true);
     runOn = oL == 0;
     if (! runOn) {
       System.exit(oL);
     }
     System.out.println("\nJustNotFLock got lock on " + lockPath
                      + "\n             will hold for 80 s or until interrupt");
     thrDelay(60000); // 1 min
     System.out.println("JustNotFLock (" + lockPath + ") will stop in 20 s");
     thrDelay(20000); // 20s

     runOn = false;
     closeLock();
     System.out.println("JustNotFLock (" + 
                          lockFil.getAbsolutePath() + ") stop\n");
     return;
  } // doIt()
} // PiWDogDemo (28.05.2019, 31.05.2019, 05.2021)
