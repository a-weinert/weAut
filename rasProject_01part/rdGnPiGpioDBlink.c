/**
 * \file rdGnPiGpioDBlink.c
 *
  A fifth programme for Raspberry's GPIO pins

  Rev. $Revision: 14 $  $Date: 2019-04-26 14:27:14 +0200 (Fr, 26 Apr 2019) $

  Copyright  (c)  2017   Albrecht Weinert <br />
  weinert-automation.de      a-weinert.de

  It uses three pins as output assuming LEDs connected to as Hi=On
  Pi 1  / Pi 3       Pin
  GPIO17/ 17 : red    11
  GPIO21/ 27 : green  13
  GPIO25     : yellow 22  (since Revision 45)
  This program forces application singleton and may be used as service.

  Its functions are the same as rdGnBlinkBlink (even sharing the lockfile)
  except for using the pigpioD library. Our makefiles define MCU and PLATFORM
  as make variables and makros.
  So we make the GPIO pin and address assignment automatically.

  Library use: This programme uses the pigpiod socket interface, requiring a
  piGpio server/daemon running. This programme needs no sudo to run.

  cross-compile  by:
  arm-linux-gnueabihf-gcc  -DPLATFORM=raspberry_03 -DMCU=BCM2837
   -DTARGET=raspi48 -I./include   -c -o rdGnPiGpioDBlink.o rdGnPiGpioDBlink.c
  arm-linux-gnueabihf-gcc  -DPLATFORM=raspberry_03 -DMCU=BCM2837
    -DTARGET=raspi67 -I./include    -c -o weRasp/weUtil.o weRasp/weUtil.c
  arm-linux-gnueabihf-gcc -I. -DPLATFORM=raspberry_03 -DMCU=BCM2837
    -DTARGET=raspi67 -I./include   rdGnPiGpioDBlink.o weRasp/weUtil.o
      --output rdGnPiGpioDBlink.elf
       -Wl,-Map=rdGnPiGpioDBlink.map,--cref   -lpigpiod_if2 -lrt

   or by:
     make PROGRAM=rdGnPiGpioDBlink TARGET=raspi61 clean all


*/
#include "arch/config.h" // chooses target platform, Raspberry type, PINnn, ..
#include "weUtil.h"  // timeAdd timeCmp
#include "pigpiod_if2.h"   //

int thePi;      // the Pi used here (default, local host)
timespec actEnd;   // actual end of previous (100/200 ms) period

static void onExit(int status, void * arg){
   if (status) {
      fprintf(outLog,
            "\n   rdGnPiGPIODBlink is terminated, code : %d \n", status);
   } else {
      fprintf(outLog, "\n   rdGnPiGPIODBlink is shutting down normally \n");
   }
   set_mode(thePi, PIN11, PI_INPUT);  // release red LED pin
   set_mode(thePi, PIN13, PI_INPUT);  // green LED pin
   set_mode(thePi, PIN22, PI_INPUT);  // yellow LED
   pigpio_stop(thePi); // terminates connection to pigpio daemon
   closeLock();
} // onExit(int, void*)

int main(int argc, char * * argv){
   if (openLock(lckPiGpioPth, ON)) return retCode; // exit on no lock / singleton

   thePi = pigpio_start(NULL, NULL); // connect to pigpio daemon(local, 8888)
   if (thePi < 0) { // initialise pigpio  gpio
      perror("can't initialise IO handling (piGpioD)");
      closeLock();
      return 99;
   } // can't initialise wiringPi (this is essential)

   on_exit(onExit, NULL);   // register exit hook
   signal(SIGTERM, onSignalExit); // register signal hook
   signal(SIGABRT, onSignalExit);
   signal(SIGINT,  onSignalExit0); // cntlC terminates normally
   signal(SIGQUIT, onSignalExit);

   set_mode(thePi, PIN11, PI_OUTPUT);   // red LED pin as output
   set_mode(thePi, PIN13, PI_OUTPUT);  // green LED pin as output
   set_mode(thePi, PIN22, PI_OUTPUT); // yellow LED
   unsigned yLd = OFF;

   monoTimeInit(&actEnd); // initialise cycle end
   while(1) {                      // red green                   yellow
      gpio_write(thePi, PIN11, 1); // on
      timeStep(&actEnd, 200000);   //          200 ms red
      yLd = !yLd;         //                                      toggle
      gpio_write(thePi, PIN22, yLd);
      gpio_write(thePi, PIN13, 1); //      on
      timeStep(&actEnd, 100000);   //         100 ms both
      gpio_write(thePi, PIN11, 0); // off
      timeStep(&actEnd, 100000);   //          100 ms green
      gpio_write(thePi, PIN13, 0); //      off
      timeStep(&actEnd, 200000);   //          200 ms dark
   } // while endless 600 ms loop; long term exact(!)
} // main()
