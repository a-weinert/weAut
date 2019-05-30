package de.weAut.tests;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import de.weAut.PiUtil;  // Raspberry Pi handling utilities (IO lock, watchdog)

/** <b>Demo (Test) of using a 1-wire thermometer on Raspberry PI with Java</b>.<br />
 *  <br />
 *  On a Raspberry Pi with initialiesed 1-wire interface this programme 
 *  reads an thermometer device DS28B20 or alike. 
 *  <br />
 *  Run by: <pre><code>
 *    java de.weAut.tests.Pi1WireThDemo thermometerID 
 *  </pre></code>Example:<pre><code>    0123456789x12345
 *    java de.weAut.tests.Pi1WireThDemo 28-02049245dde6 
 *  </pre></code>
 *  To use a Raspberry GPIO pin as 1wire interface this interface and its
 *  driver have to be initialised; see the publication
 *  <a href="http://a-weinert.de/pub/raspberry4remoteServices.pdf" 
 *  title="by Albrecht Weinert">Raspberry for remote services</a>.<br />
 *  <br />
 *  Copyright  &copy;  2019   Albrecht Weinert <br />
 *  @author   Albrecht Weinert a-weinert.de
 *  @version  $Revision: 22 $ ($Date: 2019-05-22 20:22:28 +0200 (Mi, 22 Mai 2019) $)
 */
// so far:   V. 25  (27.05.2019) :  new, minimal functionality
//           V. 26  (30.05.2019) :  minor, typo, docu 

public class Pi1WireThDemo implements PiUtil {


   /** The application start.
 *  
 *  Can be stopped by signal (cntlC), kill command and the like.
 *  @param args start parameters, none or --boot
 */
  public static void main(String[] args){
     if (args.length < 1 || args[0].length() != 15) {
        System.out.println(
"\nPi1WireThDemo needs a 15 character thermometer ID as start parameter\n"
+ " Run by:  java de.weAut.tests.Pi1WireThDemo thermometerID \n"
+ " Example: java de.weAut.tests.Pi1WireThDemo 28-02049245dde6 \n");
        System.exit(12);
     }
     System.out.println("\nPi1WireThDemo start");
     new Pi1WireThDemo().doIt(args);  // need be an PiUtil object
  } // main(String[])
  
  String thermPath;
  BufferedReader thermometer;
  String line1;
  String line2;
  

/** The application's work.  */
  public void doIt(String[] args){ 
     Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        System.out.println("\nPi1WireThDemo shutdown");
        try {
         thermometer.close();
      } catch (IOException e) { } // ignore
     })); // shutdownHook.run()

     thermPath = "/sys/bus/w1/devices/w1_bus_master1/" + args[0] + "/w1_slave";

     try {
        thermometer = new BufferedReader(new FileReader(thermPath));
     } catch (FileNotFoundException e) {
        System.out.println("Pi1WireThDemo open error");
        e.printStackTrace();
        System.exit(13);
     }

     try {
        line1 = thermometer.readLine();
        line2 = thermometer.readLine();
     } catch (IOException e) {
        System.out.println("Pi1WireThDemo read error");
        e.printStackTrace();
        System.exit(14);

     }
     
     System.out.println(line1);
     System.out.println(line2);
     
     try {
      thermometer.close();
     } catch (IOException e) {
        System.out.println("Pi1WireThDemo read error");
        e.printStackTrace();
        System.exit(15);
     }
     



     System.out.println("Pi1WireThDemo stop\n");
     return;
  } // doIt()
} // PiWDogDemo (28.05.2019)
