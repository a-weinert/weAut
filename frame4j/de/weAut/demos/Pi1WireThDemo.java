package  de.weAut.demos;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import de.weAut.PiUtil;  // Raspberry Pi handling utilities (IO lock, watchdog)

/** <b>Demo (Test) of using a 1-wire thermometer on Raspberry PI with Java</b>.<br />
 *  <br />
 *  On a Raspberry Pi with initialised 1-wire interface this programme 
 *  reads an thermometer device DS28B20 or alike. <br />
 *  Hint: The 1 in the class name is for 1wire not for Pi1. 
 *  <br />
 *  Run by: <pre><code>
 *    java  de.weAut.demos.Pi1WireThDemo thermometerID 
 *  </pre></code>Example:<pre><code>    0123456789x12345
 *    java  de.weAut.demos.Pi1WireThDemo 28-02049245dde6 
 *  </pre></code>
 *  To use a Raspberry GPIO pin as 1wire interface this interface and its
 *  driver have to be initialised; see the publication
 *  <a href="https://a-weinert.de/pub/raspberry4remoteServices.pdf" 
 *  title="by Albrecht Weinert">Raspberry for remote services</a>.<br />
 *  <br />
 *  Copyright  &copy;  2019   Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 41 $ ($Date: 2021-04-23 20:44:27 +0200 (Fr, 23 Apr 2021) $)
 */
// so far:   V. 25  (27.05.2019) :  new, minimal functionality
//           V. 26  (31.05.2019) :  two reads with interpretation 

public class Pi1WireThDemo implements PiUtil {

/** The application start.<br />
 *  <br />
 *  Can be stopped by signal (cntlC), kill command and the like.
 *  @param args start parameters, none or --boot
 */
  public static void main(String[] args){
     if (args.length < 1 || args[0].length() != 15) {
        System.out.println(
"\nPi1WireThDemo needs a 15 character thermometer ID as start parameter\n"
+ " Run by:  java  de.weAut.demos.Pi1WireThDemo thermometerID \n"
+ " Example: java  de.weAut.demos.Pi1WireThDemo 28-02049245dde6 \n");
        System.exit(12);
     }
     System.out.println("\nPi1WireThDemo start");
     new Pi1WireThDemo().doIt(args);  // need be an PiUtil object
  } // main(String[])
  
  File thermPath;
  BufferedReader thermometer;
  String line1;
  String line2;
  StringBuilder erg = new StringBuilder(14);
  boolean run = true;

/** The application's work. */
  public void doIt(String[] args){ 
     Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        if (run) System.out.println("\nPi1WireThDemo shutdown\n");
        try {
         thermometer.close();
      } catch (IOException e) { } // ignore
     })); // shutdownHook.run()

     thermPath = new File(
              "/sys/bus/w1/devices/w1_bus_master1/" + args[0] + "/w1_slave");

     for(int i = 3;;) {
        try { // try open
           thermometer = new BufferedReader(new FileReader(thermPath));
        } catch (FileNotFoundException e) {
           System.out.println("Pi1WireThDemo open error");
           e.printStackTrace();
           System.exit(13);
        }  // try open
        try {  // try read
           line1 = thermometer.readLine();
           line2 = thermometer.readLine();
        } catch (IOException e) {
           System.out.println("Pi1WireThDemo read error");
           e.printStackTrace();
           System.exit(14);
        } // try read

// 0123456789x123456789v123456789t123456789
// 63 01 55 05 7f 7e 81 66 74 : crc=74 YES   line1
// 63 01 55 05 7f 7e 81 66 74 t=22187        line2

        if (line1.charAt(36) != 'Y') { // good
           System.out.println(line1);
           System.out.println(line2);
           System.out.println("              measurement bad\n");
        } else { // good else bad
           System.out.println(line2);
           erg.setLength(0);// 0123456789x    // clear
           erg.append(        "   0.000°C"); // position 
      //   erg.setCharAt(8, (char)0x00B0);  // work around for IS889-1 source
           for (int li2i = line2.length() -1, 
                                       ergI = 7; ergI > 0; --ergI,--li2i) {
              char c = line2.charAt(li2i);
              if (c >= '0' && c <= '9') {
                 erg.setCharAt(ergI, c); // copy digits
                 if (ergI == 5) ergI = 4; // jump over 
              } else if (c == '-') {
                 if (ergI >= 3) ergI = 2;
                 erg.setCharAt(ergI, '-');
                 break;
              } else break; // end of value in line2
           }
           
           System.out.println("              measurement " + erg);
       
        } // bad
        if (--i == 0) break;
        thrDelay(2000);
     } // for

// Closing can probably be omitted, as the device as pseudo file seems to
// auto-close after the two line measurement has been read.
     try { // try close 
      thermometer.close();
     } catch (IOException e) {
        System.out.println("Pi1WireThDemo close error");
        e.printStackTrace();
        System.exit(15);
     } // try close
     run = false;
     System.out.println("Pi1WireThDemo stop\n");
     return;
  } // doIt()
} // Pi1WireThDemo (28.05.2019, 31.05.2019)
