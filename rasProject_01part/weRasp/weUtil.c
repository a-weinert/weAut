/** \file weRasp/weUtil.c

   Some system related time and utility functions for Raspberry Pi

   Copyright  (c)  2017   Albrecht Weinert <br />
   weinert-automation.de      a-weinert.de

  Revision history \code
   Rev. $Revision: 209 $ $Date: 2019-07-24 11:31:10 +0200 (Mi, 24 Jul 2019) $
   Rev. 050+ 2017-10-16 : cycTask_t->mutex now pointer (allows common mutex)
   Rev. 054+ 2017-10-23 : timing enhanced, common mutex forced/standard
   Rev. 076  2017-12-01 : advanceTim month change debug.; ret 7 for offs. chg.
   Rev. 128+ 2018-04-16 : some cleaning on comments
   Rev. 155  27.06.2018 : time handling debugged
   Rev. 161  09.07.2018 : 20ms thread is now scheduled on end, too (was not)
   Rev. 187  14.10.2018 : minor typos
   Rev. 200  16.04.2019 : logging improved; formatting enh.
   Rev. 201  26.04.2019 : renamed from sysUtil.c
   Rev. 209  22.07.2019 : formFixed.. not void
\endcode

   cross-compile by: \code
   arm-linux-gnueabihf-gcc -DMCU=BCM2837  -I./include  -c -o weRasp/weUtil.o weRasp/weUtil.c
\endcode
   cross -build as library by: \code
   arm-linux-gnueabihf-gcc -Wall -DMCU=BCM2837  -I./include  -shared -o libweUtil.so -fPIC weRasp/weUtil.c
   copy libweUtil.so C:\util\WinRaspi\arm-linux-gnueabihf\sysroot\usr\lib\
\endcode
   And finally (ftp) transfer libweUtil.so to Raspy's /usr/local/lib/
   and run sudo ldconfig there. N.b.: In most cases, it brings no disadvantage
   and it is easier to link instead of building and using an own library.

   For documentation see the include files \ref weUtil.h and \ref sysBasic.h
*/
#include "weUtil.h" // indirectly includes some six system include files
#include <errno.h>   // strerror()


//-------------------------------------------   times   -------------------

/*  Add two times as new structure. */
timespec timeAdd(timespec const  t1, timespec const t2){
   timespec ret = { .tv_sec = t2.tv_sec + t1.tv_sec,
                .tv_nsec = t2.tv_nsec + t1.tv_nsec };
   if (ret.tv_nsec >= MILLIARD) {
       ret.tv_nsec -= MILLIARD;
       ++ret.tv_sec;
   }
   return ret;
} // timeAdd(2* timespec const)

/*  Add two times overwriting the first operand. */
void timeAddTo(timespec * t1, timespec const t2){
   (*t1).tv_sec += t2.tv_sec;
   (*t1).tv_nsec += t2.tv_nsec;
   if ((*t1).tv_nsec >= MILLIARD) {
       (*t1).tv_nsec -= MILLIARD;
       ++(*t1).tv_sec;
   }
} // timeAddTo(timespec *, timespec const)


/*  Compare two times. */
int timeCmp(timespec const  t1, timespec const t2){
   if (t1.tv_sec != t2.tv_sec)  return t1.tv_sec > t2.tv_sec ? 2 : -2;
   if (t1.tv_nsec != t2.tv_nsec)  return 0;
   return t1.tv_nsec > t2.tv_nsec ? 1 : -1;
} // timeCmp(2* timespec const)


/*  Relative delay for the specified number of µs. */
int timeSleep(unsigned int micros){
   if (micros < 30) return 0;
   struct timespec sleepTime;
   sleepTime.tv_sec  = 0;
   sleepTime.tv_nsec = (long)micros * 1000;
   return nanosleep(&sleepTime, NULL);
} // delay(unsigned int)

/*  Absolute time (source) resolution. */
void monoTimeResol(timespec * timeRes){ clock_getres(ABS_MONOTIME, timeRes); }


//---------------------- utilities   parse, check, format ------------------

/*  Set one char sequence left justified into another one.
 *
 *  This function copies n characters from src to dest left justified.
 *  If the length of src is less than n the remaining length on right in dest
 *  will be filled with blanks.
 *
 *  Attention: dest[n-1] must be within the char array provided by dest.
 *  This cannot and will not be checked!
 *
 *  @param dest the pointer to / into the destination sequence
 *              where src is to be copied to. If Null nothing happens.
 *  @param src  the sequence to be copied. If Null or empty fill is used
 *              from start
 *  @param n    the number of characters to be copied from src.
 */
void strLinto(char * dest,  char const * src, size_t n){
   if (dest == NULL || n == 0) return;
   uint8_t fillFill = src == NULL || *src == 0;
   for (; n; --n, ++dest) {
      if (fillFill) {
         *dest = ' ';
      } else {
         char c = *src;
         if (c) {
            ++src;
         } else {
            c = ' ';
            fillFill = ON;
         }
         *dest = c;
      }
   } // for
} // strLinto(char *dest,  char const * src, size_t n)

/*  Append one char sequence left justified at another one.
 *
 *  This function copies n characters from src to dest and lets dest then
 *  end with 0 (end of string). If n is negativ, -n characters will be copied
 *  and dest will end with new line and 0;
 *
 *  Attention: dest[n] respectively dest [-n + 1] must be within the char
 *  array provided by dest. This cannot and will not be checked!
 *
 *  @param dest the pointer to / into the destination sequence
 *              where src is to be copied to. If Null nothing happens.
 *  @param src  the sequence to be copied. If Null or empty fill is used
 *              from start
 *  @param n    the absolute value is the number of characters to be copied
 *              from src. If this number exceeds 300 it will be taken as 0.
 *              If n is negative a line feed will be appended, too.
 */
void strLappend(char * dest,  char const * src, int n){
   if (dest == NULL) return;
   uint8_t appN = n < 0;
   if (appN) n = -n;
   if (src != NULL && n <= 300) for (; n; --n, ++src) {
     char const c = *dest = *src;
     if (! c) return; // end reached and set
     ++dest;
   } // for
   if (appN) { *dest = '\n'; ++dest; }
   *dest = 0;
} // strLappend(char *dest,  char const * src, size_t n)


/*  Set one char sequence right justified into another one.
 *
 *  This function copies n characters from src to dest right justified.
 *  If the length of src is less than n the remaining length on left in dest
 *  will be filled with blanks.
 *
 *  Attention: dest[n-1] must be within the char array provided by dest.
 *  This cannot and will not be checked!
 *
 *  Hint to append instead of insert: If this operation shall be at the end
 *  of the changed char sequence do <br />
 *  dest[n] = 0; <br />
 *  Then, of course, dest[n] must be within the char array provided by dest.
 *
 *  @param dest the pointer to / into the destination sequence
 *              where src is to be copied to. If NULL nothing happens.
 *  @param src  the sequence to be copied. If NULL or empty fill is used
 *              from start
 *  @param n    the number of characters to be copied from src.
 */
void strRinto(char * dest,  char const * src, size_t n){
   if (dest == NULL || n == 0) return;
   int i = n; // number of blanks from left
   if (src && *src) {  // source not empty
      char const * look = src;
      for (; i; --i, ++look) {
        if (*look) continue;
        break; // end before n characters
      } // for
   } // source not empty

   for (; n; --n, ++dest) {
      if (i) {
         *dest = ' ';
         --i;
         continue;
      }
      *dest = *src;
      ++src;
   } // for
} // strRinto(char *dest,  char const * src, size_t n)


/*  Format 16 bit unsigned fixed point, right aligned.
 *
 *  formFixed16(target, 6, 1234, 2), e.g., will yield " 12.34". <br />
 *  formFixed16(target, 6,    4, 2), e.g., will yield "  0.04". <br />
 *
 *  @param target    pointer to first of targLen characters changed
 *  @param targetLen field length 2..16; number of characters changed
 *  @param value     the fixed point value
 *  @param dotPos    where the fixed point is 0..6 < targetLen
 *  @return          points to the most significant digit set
 *                   or NULL on error / no formatting
 */
char * formFixed16(char * target, uint8_t targetLen, uint16_t value,
                                                           uint8_t dotPos){
   char * ret = NULL;
   if (! target) return NULL;
   if (targetLen > 16 || targetLen < 2) return NULL;
   target += (targetLen -1);
   uint16_t rem = value;


   do {
      if (value > 9) {
        rem = value % 10;
        value /= 10;
     } else {
        rem = value;
        value = 0;
     }
     *target = (char)(rem + '0'); ret = target; --target;
     if (dotPos && targetLen > 1 && !(--dotPos)) {
        *target = '.'; ret = target; --target; --targetLen;
        if (value == 0 && targetLen > 1) {
            *target = '0'; ret = target; --target; --targetLen;
         }
     }
     if (value == 0 && !dotPos) while(targetLen > 1) {
         *target = ' '; --target; --targetLen;
     } // if while
   } while (--targetLen);
   return ret;
} // formFixed16(char *, uint8_t, uint16_t, uint8_t)


/*  Format 32 bit unsigned fixed point, right aligned.
 *
 *   dotPos wrong by +1
 *  @param target    pointer to first of targLen characters changed
 *  @param targetLen field length 2..16; number of characters changed
 *  @param value     the fixed point value
 *  @param dotPos    where the fixed point is 0..6 < targetLen
 *  @return          points to the most significant digit set
 *                   or NULL on error / no formatting
 */
char * formFixed32(char * target, uint8_t targetLen, uint32_t value,
                                                           uint8_t dotPos){
   char * ret = NULL;
   if (! target) return NULL;
   if (targetLen > 16 || targetLen < 2) return NULL;
   target += (targetLen -1);
   uint8_t rem = 0;
   //fprintf(outLog, "  TEST len: %4d, val: %11d, dotPos: %4d\n",
     //                                        targetLen, value, dotPos);
   do {
      if (value > 9) {
         rem = value % 10;
         value /= 10;
     } else {
         rem = value;
         value = 0;
     }
     // fprintf(outLog, " #TEST len: %4d, val: %11d, rem: %4d, dotPos: %4d\n",
       //             targetLen, value, rem, dotPos);

      *target = (char)(rem + '0'); ret = target; --target;
      if (dotPos && targetLen > 1 && !(--dotPos)) { // now the .
         *target = '.'; ret = target; --target; --targetLen;
         if (value == 0 && targetLen > 1) {
            *target = '0'; ret = target; --target; --targetLen;
         }
      }  // now the .
      if (value == 0 && !dotPos) while(targetLen > 1) {
         *target = ' '; --target; --targetLen;
      } // if while
   } while (--targetLen);
   return ret;
} // formFixed16(char *, uint8_t, uint32_t, uint8_t)

char const bin8digs[256][10] = {
   "0000_0000", "0000_0001", "0000_0010", "0000_0011", "0000_0100", "0000_0101", "0000_0110", "0000_0111",
   "0000_1000", "0000_1001", "0000_1010", "0000_1011", "0000_1100", "0000_1101", "0000_1110", "0000_1111",
   "0001_0000", "0001_0001", "0001_0010", "0001_0011", "0001_0100", "0001_0101", "0001_0110", "0001_0111",
   "0001_1000", "0001_1001", "0001_1010", "0001_1011", "0001_1100", "0001_1101", "0001_1110", "0001_1111",
   "0010_0000", "0010_0001", "0010_0010", "0010_0011", "0010_0100", "0010_0101", "0010_0110", "0010_0111",
   "0010_1000", "0010_1001", "0010_1010", "0010_1011", "0010_1100", "0010_1101", "0010_1110", "0010_1111",
   "0011_0000", "0011_0001", "0011_0010", "0011_0011", "0011_0100", "0011_0101", "0011_0110", "0011_0111",
   "0011_1000", "0011_1001", "0011_1010", "0011_1011", "0011_1100", "0011_1101", "0011_1110", "0011_1111",
   "0100_0000", "0100_0001", "0100_0010", "0100_0011", "0100_0100", "0100_0101", "0100_0110", "0100_0111",
   "0100_1000", "0100_1001", "0100_1010", "0100_1011", "0100_1100", "0100_1101", "0100_1110", "0100_1111",
   "0101_0000", "0101_0001", "0101_0010", "0101_0011", "0101_0100", "0101_0101", "0101_0110", "0101_0111",
   "0101_1000", "0101_1001", "0101_1010", "0101_1011", "0101_1100", "0101_1101", "0101_1110", "0101_1111",
   "0110_0000", "0110_0001", "0110_0010", "0110_0011", "0110_0100", "0110_0101", "0110_0110", "0110_0111",
   "0110_1000", "0110_1001", "0110_1010", "0110_1011", "0110_1100", "0110_1101", "0110_1110", "0110_1111",
   "0111_0000", "0111_0001", "0111_0010", "0111_0011", "0111_0100", "0111_0101", "0111_0110", "0111_0111",
   "0111_1000", "0111_1001", "0111_1010", "0111_1011", "0111_1100", "0111_1101", "0111_1110", "0111_1111",
   "1000_0000", "1000_0001", "1000_0010", "1000_0011", "1000_0100", "1000_0101", "1000_0110", "1000_0111",
   "1000_1000", "1000_1001", "1000_1010", "1000_1011", "1000_1100", "1000_1101", "1000_1110", "1000_1111",
   "1001_0000", "1001_0001", "1001_0010", "1001_0011", "1001_0100", "1001_0101", "1001_0110", "1001_0111",
   "1001_1000", "1001_1001", "1001_1010", "1001_1011", "1001_1100", "1001_1101", "1001_1110", "1001_1111",
   "1010_0000", "1010_0001", "1010_0010", "1010_0011", "1010_0100", "1010_0101", "1010_0110", "1010_0111",
   "1010_1000", "1010_1001", "1010_1010", "1010_1011", "1010_1100", "1010_1101", "1010_1110", "1010_1111",
   "1011_0000", "1011_0001", "1011_0010", "1011_0011", "1011_0100", "1011_0101", "1011_0110", "1011_0111",
   "1011_1000", "1011_1001", "1011_1010", "1011_1011", "1011_1100", "1011_1101", "1011_1110", "1011_1111",
   "1100_0000", "1100_0001", "1100_0010", "1100_0011", "1100_0100", "1100_0101", "1100_0110", "1100_0111",
   "1100_1000", "1100_1001", "1100_1010", "1100_1011", "1100_1100", "1100_1101", "1100_1110", "1100_1111",
   "1101_0000", "1101_0001", "1101_0010", "1101_0011", "1101_0100", "1101_0101", "1101_0110", "1101_0111",
   "1101_1000", "1101_1001", "1101_1010", "1101_1011", "1101_1100", "1101_1101", "1101_1110", "1101_1111",
   "1110_0000", "1110_0001", "1110_0010", "1110_0011", "1110_0100", "1110_0101", "1110_0110", "1110_0111",
   "1110_1000", "1110_1001", "1110_1010", "1110_1011", "1110_1100", "1110_1101", "1110_1110", "1110_1111",
   "1111_0000", "1111_0001", "1111_0010", "1111_0011", "1111_0100", "1111_0101", "1111_0110", "1111_0111",
   "1111_1000", "1111_1001", "1111_1010", "1111_1011", "1111_1100", "1111_1101", "1111_1110", "1111_1111"
};

const uint32_t csBit[32] = {1, 2, 4, 8,
                0x10,0x20,0x40,0x80,
                0x100,0x200,0x400,0x800,
                0x1000,0x2000,0x4000,0x8000,
                0x10000,0x20000,0x40000,0x80000,
                0x100000,0x200000,0x400000,0x800000,
                0x1000000,0x2000000,0x4000000,0x8000000,
                0x10000000,0x20000000,0x40000000,0x80000000};

/*  Fetch a clear and set select bit for a GPIO pin */
uint32_t ioSetClrSelect(uint8_t pin){ return csBit[pin & 31]; }

/*  Check if string is a valid IPv4 address. */
int isValidIp4(char const * str){
   if (str == NULL)  return 0;
   int segs = 0;  // Segment count;  0..3; 3 in the end
   int chCnt = 0; // Character count within segment 0..2
   int segV = 0;  // segment value
   char c = *str;
   for(; c != '\0';  c = *(++str)) {
      if (c == '.') { // end of segment
         if (chCnt == 0) return 0; // segment. must not be empty
         if (++segs == 4) return 0; // count segments, must be 0..3
         chCnt = segV = 0;
         continue;
      } // end of segment
      if ((c < '0') || (c > '9')) return 0; // only digits in segment
      if ((segV = segV * 10 + c - '0') > 255) return 0;
      if (++chCnt == 4) return 0; // .1234
   } // for
   if (segs != 3) return 0; // must be four segment 1.2.3.4
   if (chCnt == 0) return 0; // last segment must not be empty
   return 1; // IP V4 address OK
} // isValidIp4(char const *)

/*  Parse an int with checks.  */
int parsInt(const char * str, const int lower, const int upper, const int def){
   if (str == NULL) return def;
   if (lower > upper) return def; // no way around default value
   char c = *str;
   uint8_t neg = c == '-';
   if (neg || c == '+') c = *(++str);
   if (c < '1' || c > '9') {
      if (c != 'm' && c != 'M') return def;  // may not be min, med, max,
      if ((c = *(++str)) < 'A') return def;
      char c2 = *(++str);
      if (c2 < 'D') return def;
      if (c == 'i' || c == 'I') return c2 == 'n' || c2 == 'N' ? lower : def;
      if (c == 'a' || c == 'A') return c2 == 'x' || c2 == 'X' ? upper : def;
      if (c != 'e' && c != 'E') return def;
      if (c2 == 'd' || c2 == 'D') return  (upper + lower) / 2; // medium
      return def; // not starting with digit 1..9 (wrong, empty)
   }
   int val = c - '0';
   while ((c = *(++str)) != '\0') { // through the string [1...
      if (c < '0' || c > '9') return def; // not a decimal digit; syntax error
      val = val * 10 + c - '0';
      if (val < 0) return def; // overflow
   } // through the string [1...
   if (neg) val = -val;
   if (val < lower) return def;
   return val > upper ? def: val;
} //  parsInt(const char *, 3 * int)

/*  Character to hexadecimal.  */
int char2hexDig(char c){
   if (c <'0') return -1;
   if (c <='9') return c - '0';
   c |= 0x20; // A.. tolower
   if (c < 'a' || c > 'f') return -1;
   return c - ('A' - 10);
} //  char2hexDig(char)


//--------------------- too early definitions ------------------------------
// for sake of C's inability to look forward

cycTaskEventData_t cycTaskMED; // cyclic tasks master data
pthread_mutex_t comCycMutex;


//---------------------- logging -------------------------------------------

char const * const stmp23 = cycTaskMED.rTmTxt + 3;
uint32_t const * const stmpSec = & cycTaskMED.realSec;
char errorText[182] = "no error.";

/* Log error text with system error text appended. */
void logErrWithText(char const * txt){
   genErrWithText(txt);
   fputs(errorText, errLog);
   fputc('\n', errLog);
   //   fprintf(errLog, "%s\n", errorText);
   fflush(errLog);
} //  logErrWithText(char const *)


/* Log the (last) common error text generated. */
void logErrorText(){
   fputs(errorText, errLog);
   fputc('\n', errLog);
   //   fprintf(errLog, "%s\n", errorText);
   fflush(errLog);
} // logErrorText()


/* Generate error text (errorText) with system error text appended. */
int genErrWithText(char const * txt){
   int ret2 = 0;
   int ret = pthread_mutex_lock(&comCycMutex);       //  try mutex lock
   // if (ret) return ret; // lock failed; catastrophe

   if (txt == NULL) {
      sprintf(errorText, " %.23s ## %s",
                               cycTaskMED.rTmTxt + 3, strerror(errno));
   } else {
      sprintf(errorText, " %.23s ## %s\n                        ## %s",
                               cycTaskMED.rTmTxt + 3, txt, strerror(errno));
   }
   if (!ret) ret2 =  pthread_mutex_unlock(&comCycMutex);   //     mutex unlock
   return ret2 ? ret2 : ret;
} // genErrWithText(char const *)


/*  Log an error text on errLog. */
void logErrText(char const * txt){
   if (txt == NULL) return;
   fputs(txt, errLog);
   fflush(errLog);
} // logErrText(char const *)

/*  Log an event or a message on outLog as line with time stamp.
 *
 *  If txt is not null it will be output to outLog. A time stamp is prepended
 *  and a line feed is appended. txt will be shortened to 50 characters if
 *  longer.
 *
 *  @param txt the text to be output
 */
void logStampedText(char const * txt){
   if (txt == NULL || ! *txt) return;
   fprintf(outLog, " %.23s # %.50s\n", cycTaskMED.rTmTxt + 3, txt);
   fflush(outLog);
} // logStampedText(char const *)


// -----------------------  singleton support (application lock)  -----------

/*  Lock file handle. */
int lockFd;

/* Common path to a lock file for GpIO use */
char const  * const lckPiGpioPth = "/home/pi/bin/.lockPiGpio";

/*  Basic start-up function failure. */
int retCode;

/*  Open and lock the lock file.
 *
 *  This function is the basic implementation of ::openLock. Applications not
 *  wanting its optional logging or doing their own should use this function
 *  directly.
 *
 *  @param lckPiGpioFil   lock file path name
 *  @return 0: OK, locked; 97: lckPiGpioFil does not exist; 98: can't be locked
 */
int justLock(char const * lckPiGpioFil){
   char const * lckPiGpio = lckPiGpioFil != NULL ? lckPiGpioFil : lckPiGpioPth;
   if ((lockFd = open(lckPiGpio, O_RDWR, 0666))  < 0) {
      return retCode = 97;
   } // can't open lock file (must exist)
   if (flock(lockFd, LOCK_EX | LOCK_NB) < 0) {
      close (lockFd);
      return retCode = 98;
   } // can't lock lock file
   return retCode = 0;
} // justLock(char const *)

/*  Open and lock the lock file.
 *
 *  @param lckPiGpioFil   lock file name
 *  @param perr make error message
 *              when lock file does not exist or can't be locked
 *  @return 0: OK, locked; 97: lckPiGpioFil does not exist; 98: can't be locked
 */
int openLock(char const * lckPiGpioFil, uint8_t const perr){
   const int ret = justLock(lckPiGpioFil);
   if (ret && perr) {
      if (ret == 97) {
         logErrWithText("can't open lock file  (must exist)");
      } else if (ret == 98) {
         logErrWithText("can't lock lock file (other instance running)");
      }
   }
   return ret;
} // openLock(char const *, uint8_t const)

/*  Unlock the lock file. */
void closeLock(void){
   flock(lockFd, LOCK_UN);
   close(lockFd);
} // closeLock()


// ------------------------------------  signalling and exiting  ------------

/*  On signal exit. */
void onSignalExit(int s){ exit(s); }

/*  On signal exit 0. */
void onSignalExit0(int s){ exit(0); }

volatile uint8_t commonRun = 1;
volatile int sigRec = 0;

/*  On signal stop. */
void onSignalStop(int s){ sigRec = s;  commonRun = 0; }


//----------   event and cyclic task / thread support   ---------------------

uint8_t comCycMutexInited;

/*  Initialise the common mutex for cyclic and event tasks. */
int comCycMutexInit(){
   if (comCycMutexInited) return 0; // already made
   int ret = pthread_mutex_init(&comCycMutex, NULL);
   if (ret) return ret;
   comCycMutexInited = 1;
   return 0;
} // comCycMutexInit()

/*  Initialise a cyclic task / threads structure. */
int cycTaskInit(cycTask_t * cykTask){
   cykTask->count = 0;
   int ret = comCycMutexInit();
   if (ret) return ret;
   return pthread_cond_init(&cykTask->cond, NULL);
} // cycTaskInit(cycTask_t *)

/*  Destroy a cyclic task / threads structure. */
int cycTaskDestroy(cycTask_t * cykTask){
   if (cykTask == NULL) return 0;
   return pthread_cond_destroy(&cykTask->cond);
} //  cycTaskDestroy(cycTask_t *)

/*  Handle and signal events. */
int cycTaskEvent(cycTask_t * cycTask, uint8_t noEvents, timespec stamp,
                                         cycTaskEventData_t cycTaskEventData){
   int ret = pthread_mutex_lock(&comCycMutex);       //  try mutex lock
   if (ret) return ret; // lock failed; catastrophe
   cycTask->stamp = stamp;                           // under mutex lock
   cycTask->count += noEvents;                       //             :
   cycTask->cycTaskEventData = cycTaskEventData;     // under mutex lock
   ret = pthread_cond_broadcast(&cycTask->cond); // wake threads; unlock / lock
   int ret2 =  pthread_mutex_unlock(&comCycMutex);   //     mutex unlock
   return ret2 ? ret2 : ret;
} // cycTaskEvent(cycTask_t *, uint8_t)


/*  Wait on signalled event. */
int cycTaskWaitEvent(cycTask_t * cycTask, uint32_t eventsThreshold,
                                                    cycTask_t * cycTaskSnap){
   if (!commonRun) return 111; // event handler's last round w/o wait
   int ret = pthread_mutex_lock(&comCycMutex);                  // under lock
   while (commonRun) {                                          //       :
      int32_t cntDif = (int32_t)(cycTask->count - eventsThreshold);
      if (cntDif >= 0) break;  // no more wait (overflow hardened !?)    :
      ret = pthread_cond_wait(&cycTask->cond, &comCycMutex); // unlock / lock
      if (ret) break; // condWait failed (why ???)                      \:
   } // while below count or no event                                    :
   if (cycTaskSnap != NULL) *cycTaskSnap = *cycTask;            // under lock
   int ret2 =  pthread_mutex_unlock(&comCycMutex);            // release lock
   return ret2 ? ret2 : ret;
} // cycTaskWaitEvent(cycTask_t *, uint32_t)

/*  Get the current event counter. */
uint32_t getCykTaskCount(cycTask_t const * const cycTask){
   uint32_t ret = 0x7FfFfF7;
   if (cycTask == NULL) return ret;
   int lockFail = pthread_mutex_lock(&comCycMutex);
   ret = cycTask->count;
   if (!lockFail) pthread_mutex_unlock(&comCycMutex);
   return ret;
} // uint32_t getCykTaskCount(cycTask_t const * const)


//----------   cyclic timing by monotonic clock and signalling  --------------

timespec allCycStart;  // first start all cycles

// above cycTaskEventData_t cycTaskMED; // cyclic tasks master data

timespec cyc1msEnd;    // actual end of 1 ms cycle
int8_t vcoCorrNs = 0;  // ns count correct -: faster, +: slower
long absNanos1ms = 1000000;  // the adjusted 1ms of the ABS_MONOTIME clock
int msReal;

cycTask_t cyc100ms; // 100ms cycle (data structure)
cycTask_t cyc20ms;  // 20ms cycle (data structure)
cycTask_t cyc1ms;   // 1ms cycle (data structure)
cycTask_t cyc1sec;  // 1s cycle (data structure)

int startDelay[1];  // if set as 1..100, start delay for 1&100ms cycles

pthread_t threadCyclist;  // the thread running date time and cycles
uint8_t have1msCyc = ON;
uint8_t have20msCyc = OFF;
uint8_t have100msCyc = ON;
uint8_t have1secCyc = OFF;

/*  The cycles thread function.
 *
 *  Internal implementation and use; see theCyclistStart.
 */
void  * theCyclistThread(void * args){
   int delay = *((int*)args);
   pthread_attr_t attr;
   if (delay >= 12 && delay <= 1200) {
      --delay;
      cycTaskMED.cnt1ms += delay; // we increment at loop start and have 0 at first
      timeAddNs(&cyc1msEnd, absNanos1ms * delay); // + start delay
      clock_nanosleep(ABS_MONOTIME, TIMER_ABSTIME, &cyc1msEnd, NULL);
      initStartRTime(); // restore timing values
   } // start delay
   for(;;) { // timing loop in cyclist thread
      timeAddNs(&cyc1msEnd, absNanos1ms); // 1 ms time step
      clock_nanosleep(ABS_MONOTIME, TIMER_ABSTIME, &cyc1msEnd, NULL);
      int ret2 = 0;
      int ret = pthread_mutex_lock(&comCycMutex);       //  try mutex lock
      // if (ret) return ret; // lock failed; catastrophe
      ++cycTaskMED.cnt1ms; // adding the 1ms (cycle) counter
      if (++cycTaskMED.msTo100Cnt >= 100) {
         cycTaskMED.msTo100Cnt = 0;
         memcpy(cycTaskMED.rTmTxt + 24, "00", 2);
         cycTaskMED.rTmTxt[23] = '0' + (++cycTaskMED.cnt10inSec);
      } else {
         memcpy(cycTaskMED.rTmTxt + 24, dec2digs[cycTaskMED.msTo100Cnt], 2);
      }
      cycTaskMED.cycStart = cyc1msEnd;
      if (++cycTaskMED.cycStartMillis >= 1000) { // next second
         cycTaskMED.cycStartMillis = 0;
         cycTaskMED.cnt10inSec = 0;
         if (++cycTaskMED.cnt210sec == 210) cycTaskMED.cnt210sec = 0;
         memcpy(cycTaskMED.rTmTxt + 23, "000 ", 4);
         int advRet = advanceTmTim(&cycTaskMED.cycStartRTm, cycTaskMED.rTmTxt, 1);
         if (advRet >= 2) { // advanced min
            clock_gettime(CLOCK_REALTIME, &actRTime);
            // correct (VCO) every minute and or leap second somersault
            cycTaskMED.realSec = (uint32_t) actRTime.tv_sec;
         } else ++cycTaskMED.realSec;
         ret2 =  pthread_mutex_unlock(&comCycMutex);   //     mutex unlock
         if (advRet >= 2) { // advanced min
           if (advRet > 2) {  // hour change
              localtime_r(&actRTime.tv_sec, &actRTm); // structure safe
              //  todo put update RT here
              //    clock_gettime(CLOCK_REALTIME, &actRTime);  // epoch time
              //  localtime_r(&actRTime.tv_sec, &actRTm); // structure safe
              // Attention this is copy and paste
              todayInYear = actRTm.tm_yday;
              __time_t const utcSecInDay = actRTime.tv_sec % 86400;
              utcMidnight = actRTime.tv_sec - utcSecInDay;
              if (actRTm.tm_gmtoff > 0) { // east of Greenwich
                 if (utcSecInDay >= (86400 - actRTm.tm_gmtoff))
                    utcMidnight += 86400; // in next day already
              } else if (actRTm.tm_gmtoff < 0) { // west of Greenwich
                 if (utcSecInDay < -actRTm.tm_gmtoff)
                    utcMidnight -= 86400; // in previous day still
              } // west of Greenwich
              localMidnight = utcMidnight - actRTm.tm_gmtoff;

              cycTaskMED.cycStartRTm = actRTm; // renew local real time
              cycTaskMED.hourOffs = actRTm.tm_gmtoff / 3600;
            } // hour change (may got leap second, DST or other stuff

            // VCO corrective implementation. Since Jessie uses NTP tuned
            // clock base for MONOTONIC (as the C spec says) the only source
            // of deviation is a hard corrective NTP step (leap seconds or
            // DST) spoiling the seconds switch moment and hence the ms.
            msReal = actRTime.tv_nsec / 1000000; // 500..999 real behind
            if (msReal == 0) { // sync
               vcoCorrNs = 0;
               absNanos1ms = 1000000;
            } else { // sync else deviation
               if (msReal < 1000) { //calc OK
                  if (msReal > 500) { // real behind monotonic derived to fast
                     int tmp = (1000 - msReal) * 16; // 16ns every ms = 1ms/1min
                     vcoCorrNs = tmp < 122 ? tmp : 123;
                  } else {
                     int tmp = msReal * 15; // 15 en lieu de 16: good idea??
                     vcoCorrNs = tmp < 122 ? -tmp : -126; // -1 ... -126
                  }
                  absNanos1ms = 1000000 + vcoCorrNs;
               } // calculated time etc. OK
            } // deviation
         } // minute change
      } else {  // next second
         ret2 =  pthread_mutex_unlock(&comCycMutex);   //     mutex unlock
      }
      if (!commonRun) break;
      if (have1msCyc)
        cycTaskEvent(&cyc1ms, 1, cyc1msEnd, cycTaskMED); // signal 1ms cycle t.
      if (have100msCyc && !cycTaskMED.msTo100Cnt)
        cycTaskEvent(&cyc100ms, 1, cyc1msEnd, cycTaskMED); // 100ms cyc. tasks
      if (have1secCyc && cycTaskMED.cycStartMillis == 0)
        cycTaskEvent(&cyc1sec, 1, cyc1msEnd, cycTaskMED); // signal 1s cycle t.
      if (have20msCyc && cycTaskMED.msTo100Cnt % 20 == 0)
         cycTaskEvent(&cyc20ms, 1, cyc1msEnd, cycTaskMED); // signal 1s cycle t.

     //  ++cycTaskMED.cnt1ms; // adding 1ms cycle counter put up under lock
   } // timing loop
   // run all cycles a last time w/o commonRun to allow cleanup
   cycTaskEvent(&cyc1ms, 1, cyc1msEnd, cycTaskMED); // sign. 1ms cycle tasks
   cycTaskEvent(&cyc20ms, 1, cyc1msEnd, cycTaskMED); // sign. 20ms cycle tasks
   cycTaskEvent(&cyc100ms, 1, cyc1msEnd, cycTaskMED); // 100ms cyc. tasks
   cycTaskEvent(&cyc1sec, 1, cyc1msEnd, cycTaskMED); // signal 1s cycle task
   return NULL;
} // theCyclistThread(void *)

/*  Get a (stop-watch) ms reading.
 *
 *  This function provides an 16 bit reading of the cyclist's
 *  (64 bit ) milliseconds. It is intended for measuring short (<= 1min)
 *  durations.
 *
 *  Hint: This functions thread safety stems from the hope of 16 bit increments
 *  being atomic. Even if no problems in this respect were observed on
 *  Raspberry Pi 3s, it may be just hope in the end. Thread-safe values are,
 *  of course, provided in the ::cycTaskEventData_t structure. But those are
 *  frozen within one cycle task step, and, hence, not usable as stop-watch
 *  readings within such step.
 */
uint16_t stopMSwatch(){ return (uint16_t) cycTaskMED.cnt1ms; } // lower16bit ms

/*  Get a ms in s reading.
 *
 *  This function provides the cyclist's ms in sec as 16 bit unsigned reading.
 *  It is intended for measuring and testing durations.
 *  Hint: The (asynchronous) value returned may/would be 1 above the current
 *  ms cycle value and be even 1000.
 *
 *  Hint2: This functions does nothing for thread safety. It hopes 16 bit
 *  accesses being atomic. Even if no problems in this respect were observed on
 *  Raspberry Pi 3s, it may be just hope in the end. Thread-safe values are,
 *  of course, provided in the ::cycTaskEventData_t structure. But those are
 *  frozen within one cycle task step, and, hence, not usable as stop-watch
 *  readings within such step.
 */
uint16_t getMSinS(){ return cycTaskMED.cycStartMillis; }

/*  Get a 10th of second in s reading.
 *
 *  This function provides the cyclist's tenth in seconds reading (0..9).
 *  It is intended for cyclic tasks with times greater than 100 ms or
 *  asynchronous tasks to get a coarse second sub-division.
 *
 *  Hint: Cyclic tasks get this value in their task date valid at start. This
 *  function provides an actual value for tasks running longer than 100ms.
 */
uint8_t get10inS(){ return cycTaskMED.cnt10inSec; }

/*  Get the absolute s reading
 *
 *  This function provides the cyclist's epoch time in seconds, 0 being
 *  1.1.1970 00:00:00 UTC on almost all Linuxes an C libraries.
 *
 *  Hint: Cyclic tasks get this value in their task date valid at start. This
 *  function provides an actual value for asynchronous tasks or cyclic tasks
 *  with periods > 1s.
 */
uint32_t getAbsS(){ return cycTaskMED.realSec; }

/* The cycles handler  */
int theCyclistStart(int startMsDelay){
   if (actRTime.tv_sec == 0) initStartRTime();
   int ret = comCycMutexInit();
   if (ret) return retCode = ret;  // retCode replaced sigReg

   ret = cycTaskInit(&cyc100ms);
   if (ret) return retCode = ret;
   ret = cycTaskInit(&cyc1ms);
   if (ret) return retCode = ret;
   ret = cycTaskInit(&cyc1sec);
      if (ret) return retCode = ret;
   startDelay[0] =  (startMsDelay < 12 ||startMsDelay > 1200) ? 0
                    : startMsDelay - 1;  // []: transport parameter
   ret = pthread_create(&threadCyclist,  NULL, theCyclistThread,
                                                            (void*)startDelay);
   if (ret) return retCode = ret;
   return retCode = 0;
} // theCyclistStart(int)

/*  Wait for the end of the cycles thread. */
int theCyclistWaitEnd(){
   return retCode = pthread_join(threadCyclist, NULL);
} // theCyclistWaitEnd()


/*  The cycles handler arrived.
 *
 *  This function cleans up after theCyclist. It should be called after
 *  theCyclist() ending successfully on commonRun false. The controller
 *  thread (usually the main thread directly or by its exit hook) shall call
 *  this function after having joined and cleaned up all of its threads.
 */
int endCyclist(void){
   int ret = cycTaskDestroy(&cyc100ms);
   int ret2 = cycTaskDestroy(&cyc1ms);
   return ret2 ? ret2 : ret;
} // theCyclist()

//----------  date, time (local) by real time (NTP or else) clock   -----------

/*  Start time (structure, monotonic real time clock). */
timespec startRTime;

/* Actual broken down time (text).
 *
 *  The format is:  Fr 2017-10-20 13:55:12.987 UTC+20
 *  ................0123456789x123456789v123456789t1234
 *  The length is 32.
 */
char actRTmTxt[34];


/*  Initialise start (real) time. */
void initStartRTime(){
   monoTimeInit(&cyc1msEnd);

   if (allCycStart.tv_sec == 0) {
      allCycStart = cyc1msEnd; // init start (once)
      cycTaskMED.cnt1ms = 0; // should be so
   }
    //   monotonic / real
   // *) clock_gettime(CLOCK_REALTIME, &actRTime);  // epoch time
   updateReaLocalTime(); // replaces both *)
   if (startRTime.tv_sec == 0) startRTime =  actRTime;

   int ms = cycTaskMED.cycStartMillis = startRTime.tv_nsec / 1000000;
   int v = cycTaskMED.msTo100Cnt = ms % 100; // 0..99
   uint8_t v2 = cycTaskMED.cnt10inSec = (uint8_t)(ms / 100); // 0..9
   cycTaskMED.realSec = (uint32_t) startRTime.tv_sec;

   // *) localtime_r(&actRTime.tv_sec, &actRTm); // structure safe
   cycTaskMED.cycStartRTm = actRTm;
   cycTaskMED.hourOffs = actRTm.tm_gmtoff / 3600; // offset in hours
   formatTmTiMs(cycTaskMED.rTmTxt, &actRTm, cycTaskMED.cycStartMillis);
   memcpy(actRTmTxt, cycTaskMED.rTmTxt, 34);
} // initStartRTime()

/*  Advance broken down real time by seconds.
 *
 * @param rTm    pointer to broken down real time; NULL: error
 * @param sec    1..40  will be added; else: error
 * @param rTmTxt date text, length 32, format Fr 2017-10-20 13:55:12.987 UTC+20
 * @return       0: error (rTm NULL e.g.); 1: seconds changed; 2: minute;
 *               3: hour; 4: day; 5: month ; 6: year ; 7: zone offset
 */
int advanceTmTim(struct tm  * rTm,  char * rTmTxt, uint8_t sec){
   if (rTm == NULL) return 0;
   if (sec < 1 || sec > 40) return 0;
   if (rTmTxt == NULL) rTmTxt = actRTmTxt;
   int v = rTm->tm_sec + sec;
   int v2 = rTm->tm_sec = v <= 59 ? v : v - 60;
   memcpy(rTmTxt + 20, dec2digs[v2], 2); // update seconds text
   if (v <= 59) return 1; // only seconds changed

   v = rTm->tm_min + 1;
   v2 = rTm->tm_min = v <= 59 ? v : 0;
   memcpy(rTmTxt + 17, dec2digs[v2], 2); // update minutes text
   if (v <= 59) return 2; // min changed

   v = rTm->tm_hour + 1;
   v2 = rTm->tm_hour = v < 24 ? v : 0;
   memcpy(rTmTxt + 14, dec2digs[v2], 2); // update hours text
   if (v < 24) return 3; // hour changed

   // todo have a look at tm_isdst or automate
   ++rTm->tm_yday;
   v = rTm->tm_wday + 1;
   v2 = rTm->tm_wday = v <= 6 ? v : 0;
   memcpy(rTmTxt, dow[v2], 2); // weekdays text

   v2 = rTm->tm_mday + 1;
   if (v2 < 29) {
      memcpy(rTmTxt + 11, dec2digs[v2], 2); // update month day  text
      return 4; // harmless day changed
   }

   v = rTm->tm_mon;
   v2 = rTm->tm_gmtoff;
   timespec tmpRTime;
   clock_gettime(CLOCK_REALTIME , &tmpRTime);  // epoch time
   localtime_r(&tmpRTime.tv_sec, rTm);  // update all

   if (v2 != rTm->tm_gmtoff) {
      formatTmTiMs(rTmTxt, rTm, 0);
      return 7; // offset changed
   } // offset changed all else from day on may be wrong

   v2 = rTm->tm_mday;
   memcpy(rTmTxt + 11, dec2digs[v2], 2); // update month day  text

   if (v == rTm->tm_mon) return 4; // same month
   v = rTm->tm_mon;
   memcpy(rTmTxt + 8, dec2digs[v + 1], 2); // update month digits
   if (v) return 5; // not January --> not year change

   v2 = rTm->tm_year % 100; // assume 2000 .. 2099 (200..299)
                          ////  BUG its the month
   memcpy(rTmTxt + 5, dec2digs[v2], 2); // update last two year digits
   return 6;
} // advanceTmTim(struct tm  *, uint8_t)
