/**
 * \file justLock.c
 *
  A small program just to lock the piGpoi lock file
\code
   Copyright  (c)  2019   Albrecht Weinert
   weinert-automation.de      a-weinert.de

 *     /         /      /\
 *    /         /___   /  \      |
 *    \        /____\ /____\ |  _|__
 *     \  /\  / \    /      \|   |
 *      \/  \/   \__/        \__/|_                                 \endcode

   Revision history \code
   Rev. $Revision: 236 $ $Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $
   Rev. 209 22.07.2019 : minor improvements (docu)
\endcode

  This program tries to lock the standard lock file for piGPio if it exists.
  On success it will run respectively sleep until getting a signal, on which
  it will unlock the file and terminate.
*/
#include <getopt.h>  // no_argument, required_argument, optarg,
#include "weUtil.h"  // timeAdd timeCmp
#include "weLockWatch.h"


//---------  basic configuration and names  --------------------------------

char const prgNamPure[] = "justLock";
//............        ....0123456789x1234567
char const prgSVNrev[] = "$Revision: 236 $   ";
//........      ..........0123456789x123456789v123456789t123456789q
char const prgSVNdat[] = "$Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $ ";
//                               0123456789x123456

char prgDesTxt[] = "\n"
   "    justLock  \n"
   "    Copyright 2019 Albrecht Weinert <weinert-automation.de> \n\n"
   "    Program to enable programs and scripts written in other \n"
   "    languages to use standard Linux/C file locking. \n\n";

char optHlpTxt[] =
   "    Run by:    justLock [options] [lockfilePath] \n"
   "    justLock tries to lock the file given as argument or the \n"
   "    standard lock file for piGpio if it exists. \n"
   "    On success it will run respectively sleep until getting \n"
   "    a signal, on which it will unlock the file and terminate. \n\n"
   "    The start options: \n"
   "    --help -h -? (this) help output \n"
   "    --version -v show program revision and date  \n"
   "    --verbose    be verbose on console (for logging or debugging) \n"
   "    --normal     be silent except for errors (default) \n"
   "    --silent     be totally silent  \n"
   "                 Options are case sensitive \n"
   "    The return codes:    \n"
   "          0  OK  had lock file locked until signal \n"
   "         97      can't open the the lock file (probably not existing) \n"
   "         98      can't get the lock (probably other instance running) \n" ;

int verbose = 1;  // 1 normal 2 verbose 0 silent

static struct option longOptions[] = {
  {"silent",      no_argument, &verbose, 0}, // say nothing
  {"normal",      no_argument, &verbose, 1}, // default
  {"verbose",     no_argument, &verbose, 2}, // do talk

  {"help",        no_argument,     NULL, 'h'},
  {"version",     no_argument,     NULL, 'v'},
  {NULL, 0, NULL, 0} }; // longOptions (end marker)

char const * lckPiGpio; //!< The file path used

/** The shutdown hook.
 *
 *  Actions when killed or getting signal
 */
static void onExit(int status, void * arg){
   if (status) {
      if (verbose) fprintf(outLog,
       "\n   justLock (%s) was terminated, code : %d \n", lckPiGpio, status);
   } else {
      if (verbose > 1) fprintf(outLog,
       "\n   justLock (%s) is shutting down normally \n", lckPiGpio);
   }
   closeLock();
} // onExit(int, void*)

/** The program.
 *
 *  run by:  justLock [options] [lockFilePath]
 *
 *  For options see longOptions and :: optHlpTxt.
 */
int main(int argc, char * * argv){
   char * lckPth = NULL; // use weUtil.c lckPiGpioPth by default
   int ret = 0;

   for (;;) {
      int optIndex = 0; // index var for getopt
      ret = getopt_long (argc, argv, "h?v", longOptions, &optIndex);
      if (ret == -1) {
         lckPth = argv[optind]; // index or no option argument in getopt.h
         break; // no (more) options
      }
      switch (ret) {
      case 'h':
      case '?':
      case 'v':
         fputs(prgDesTxt, outLog);
         if (ret == 'v') printRevDat();
         if (ret != 'v') fputs(optHlpTxt, outLog);
         fputs("\n", outLog);
         return 0; // end program; help output only
         break;
      }
   } // over arguments

   lckPiGpio = lckPth != NULL ? lckPth : lckPiGpioPth;
   if (justLock(lckPiGpio)) { // not 0 no success
      if (verbose) fprintf(outLog,
           "\n   justLock can't lock %s, code : %d \n", lckPiGpio, retCode);
      return retCode; // exit on no lock
   } // not 0 no success
   on_exit(onExit, NULL);   // register exit hook
   signal(SIGTERM, onSignalExit); // register signal hook
   signal(SIGABRT, onSignalExit);
   signal(SIGINT,  onSignalExit0); // cntlC terminates normally
   signal(SIGQUIT, onSignalExit);
   if (verbose > 1) fprintf(outLog,
      "\n   justLock locked %s, going to sleep \n", lckPiGpio);
   while(1) sleep(100000);  // sleep for days
} // main(int, char * *)
