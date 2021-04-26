/** \file rdGnPiGpioDBlink.c
 *
  A demo program for Raspberry's GPIO pins using a pigpioD server
   \code
'
   Copyright  (c)  2017 2020 2021   Albrecht Weinert
   weinert-automation.de            a-weinert.de

     /         /      /\
    /         /___   /  \      |
    \        /____\ /____\ |  _|__
     \  /\  / \    /      \|   |
      \/  \/   \__/        \__/|_


   Revision history
   Rev. $Revision: 237 $ $Date: 2021-02-08 19:27:11 +0100 (Mo, 08 Feb 2021) $

   Rev.  45 11.08.2019 : one yellow LED added
   Rev. 231 27.08.2020 : use weGPIOd.h, enable piTraffic shield for LEDs
   Rev. 237 08.02.2021 : use bank mask functions in  weGPIOd.h

      . \endcode

   Purpose

This program demonstrates  <br />
 a) the usage if a pigpioD server for IO <br />
 b) the implementation of an exact (100 ms / 600ms) cycle <br />
 c) the singleton use by lock file.

  It uses three pins as output assuming LEDs connected to as Hi=On \code
     Pi 1 / 3,4,0 LED  Pin
  GPIO 17 / 17    red   11
  GPIO 21 / 27    green 13
  GPIO 25 / 25   yellow 22  \endcode

  Alternatively by option --traffic, it uses all 12 LEDs of the piTraffic
  shield; see trafficPi.h.

  Library use: This program uses the pigpiod socket interface, requiring a
  piGpio server/daemon running. This program needs no sudo to run.

  cross-compile  by:  \code
     make PROGRAM=rdGnPiGpioDBlink TARGET=pi4you clean all \endcode
   program by: \code
     make PROGRAM=rdGnPiGpioDBlink TARGET=pi4you FTPuser=pi:piPi progapp
     \endcode
   run by: \code
     rdGnPiGpioDBlink  [--traffic ] \endcode
*/
#include "trafficPi.h"   // Pin assignments for 4 traffic lights shield
#include "weGPIOd.h"    // piGPioD
#include "weUtil.h"    // timeAdd timeCmp parsing cycles ...
#include "weLockWatch.h" // lock handling and watchdog
#include <getopt.h>  // program parameters no_argument, required_argument ...

//---------  basic configuration and names  --------------------------------

#if SETPRGDATA
char const prgNamPure[] = "rdGnPiGpioDBlink";
//............        ....0123456789x1234567
char const prgSVNrev[] = "$Revision: 237 $  ";
//........      ..........0123456789x123456789v123456789t123456789q
char const prgSVNdat[] = "$Date: 2021-02-08 19:27:11 +0100 (Mo, 08 Feb 2021) $ ";
//                               0123456789x123456
#endif  // lock out Doxygen to avoid duplication of variable documentation

char prgDesTxt[] =
   "    Copyright 2017 2020 Albrecht Weinert <weinert-automation.de>  \n\n"
   "    Program to demonstrate the usage of piGpioD by blinking LEDs  \n";

char optHlpTxt[] =
   "               The options:    \n"
   "--useWD --noWD      use the watchdog / do not use it (default) \n"
   "--useLock --noLock  use the IO singleton lock (default) / do not use it \n"
   "--threeLED     Traditionell red green Blink rd,gn,ye: pin 11 13 22 \n"
   "--traffic      Red green blink with all 4*3 piTraffic shield lights \n"
   "--rdGnBlinkWL --rdGnTrafficWL   above including --useWD and --useLock\n"
   "--version -v   show program revision and date  \n"
   "--help -h -?   (this) help output \n"
   "               Options are case sensitive \n";

timespec actEnd;   // actual end of previous (100/200 ms) period

unsigned const theRDsTrad[] = {PIN11, 0x7F}; // default rdGnBlink red
unsigned const theGNsTrad[] = {PIN13, 0x7F}; // default rdGnBlink green
unsigned const theYEsTrad[] = {PIN22, 0x7F}; // default rdGnBlink green

unsigned const * theRDs = theRDsTrad; // default rdGnBlink red
unsigned const * theGNs = theGNsTrad; // default rdGnBlink green
unsigned const * theYEs = theYEsTrad; // default rdGnBlink green

// stupid C can use array expessions only on 1st time initialisation
unsigned const theRDsTraf[] = {RDall, 0x7F}; // piTraffic shield
unsigned const theGNsTraf[] = {GNall, 0x7F}; // piTraffic shield
unsigned const theYEsTraf[] = {YEall, 0x7F}; // piTraffic shield

int useTraffic = OFF; //!< flag to use piTraffic shield; default off

static struct option longOptions[] = {
  {"useWD",       no_argument,    &useWatchdog,  ON}, // log on console
  {"noWD",        no_argument,    &useWatchdog, OFF}, // on console
  {"useLock",     no_argument,      &useIOlock,  ON}, // log on console
  {"noLock",      no_argument,      &useIOlock, OFF}, // on console

  {"help",        no_argument,            NULL, 'h'},
  {"version",     no_argument,            NULL, 'v'},

  {"theeLED",     no_argument,     &useTraffic, OFF},
  {"traffic",     no_argument,     &useTraffic,  ON},

  {NULL, 0, NULL, 0} // longOptions end marker
}; // struct option (getopt.h)



static void onExit(int status, void * arg){
   commonRun = 0;
   stopWatchdog();
   if (status) {
      fprintf(outLog,
            "\n   rdGnPiGpioDBlink is terminated, code : %d \n", status);
   } else {
      fprintf(outLog, "\n   rdGnPiGpioDBlink is shutting down normally \n");
   }
   releaseOutputsReport(thePi); // release LED pin(s)
   pigpio_stop(thePi); // terminates connection to pigpio daemon
   closeLock();
} // onExit(int, void*)

int main(int argc, char * * argv){
   useIOlock = OFF;
   useWatchdog = OFF;

   int optNo = 0;
   // parse arguments if any as very first deed (even before start timing)
   for (;;) {
      int optIndex = 0; // index var for getopt
      optNo = getopt_long(argc, argv, "h?vtp:G:wd:s:",
                  longOptions, &optIndex);
      if (optNo == -1) break; // no (more) options
      switch (optNo) {
      case 'h':
      case '?':
      case 'v':
         fputs("   \n", outLog);
         if (optNo == 'v') {
            printNamRevDat();
         } else fputs("    testOnPi  \n", outLog);
         fputs(prgDesTxt, outLog);
         if (optNo != 'v') fputs(optHlpTxt, outLog);
         fputs("\n", outLog);
         return 0; // end program; help output only
         break;
      } // switch options
   } // for options

   if (openLock(lckPiGpioPth, ON)) return retCode; // exit on no lock / singleton

   thePi = pigpio_start(NULL, NULL); // connect to pigpio daemon(local, 8888)
   if (thePi < 0) { // initialise pigpio  gpio
      perror("can't initialise IO handling (piGpioD)");
      closeLock();

      return 99;
   } // can't initialise wiringPi (this is essential)

   if (initWatchdog()) return retCode; // exit on no watchdog
   on_exit(onExit, NULL);   // register exit hook
   signal(SIGTERM, onSignalExit); // register signal hook
   signal(SIGABRT, onSignalExit);
   signal(SIGINT,  onSignalExit0); // cntlC terminates normally
   signal(SIGQUIT, onSignalExit);

   if (useTraffic) {
      theRDs = theRDsTraf;
      theGNs = theGNsTraf;
      theYEs = theYEsTraf;
   } // use all piTraffic shield's LEDs

   uint32_t const lesRDs = initAsOutputs(theRDs);
   uint32_t const lesYEs = initAsOutputs(theYEs);
   uint32_t const lesGNs = initAsOutputs(theGNs);
   unsigned y = OFF;  // for toggle yellow
   monoTimeInit(&actEnd); // initialise cycle end
   unsigned wdCount = 2;
   while(commonRun) {                      // red green                   yellow
      setOutputs(lesRDs, ON);      // on
      timeStep(&actEnd, 200000);   //          200 ms red
      setOutputs(lesYEs, y = !y);  //                             toggle
      setOutputs(lesGNs,  ON);     //      on
      timeStep(&actEnd, 100000);   //          100 ms both
      setOutputs(lesRDs, OFF);     // off
      timeStep(&actEnd, 100000);   //          100 ms green
      setOutputs(lesGNs, OFF);     //      off
      timeStep(&actEnd, 200000);   //          200 ms dark  except on/off
      if (useWatchdog) {
         if (--wdCount) continue; // while
         triggerWatchdog();
         wdCount = 20; // * 600 ms = 12s
      }
   } // while endless 600 ms loop; long term exact(!)
   exit(sigRec);  // acts as jump to onExit(int, void *)
} // main()
