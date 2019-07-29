/** \file weRasp/sysBasic.c
 *
   Some system related basic functions for Raspberry Pis

\code
   Copyright  (c)  2018   Albrecht Weinert
   weinert-automation.de      a-weinert.de

 *     /         /      /\
 *    /         /___   /  \      |
 *    \        /____\ /____\ |  _|__
 *     \  /\  / \    /      \|   |
 *      \/  \/   \__/        \__/|_                                 \endcode

   Revision history \code
   Rev. $Revision: 209 $ $Date: 2019-07-24 11:31:10 +0200 (Mi, 24 Jul 2019) $
   Rev.  105 06.02.2018 : new (transfered parts from sysutil.c)
   Rev.  108 12.02.2018 : parts moved out, one event file; Started to get
                         Doxygen usable. Note: Not yet done.
   Rev. 147 16.06.2018 : time handling enhanced improved
   Rev. 155 27.06.2018 : time handling debugged
   Rev. 164 11.07.2018 : sunset/sunrise location parameters; string functions
\endcode
   cross-compile by: \code
   arm-linux-gnueabihf-gcc -DMCU=BCM2837 -I./include -c -o weRasp/sysBasic.o weRasp/sysBasic.c
\endcode
   This is a (basic) library to support CGI programmes written in C to be used
   under a web sever --  as e.g. Apache 2.4 here.

   For documentation see the include file \ref sysBasic.h
*/
#include "sysBasic.h" // indirectly includes ...


/*  Floating point NaN. */
uint8_t isFNaN(float const val){
   dualReg_t cand = {.f = val};
   uint32_t const exp = cand.i & 0x7F800000; // 32 bit exponent mask
   if (exp != 0x7F80000) return FALSE; // neither NaN nor infinity
   uint32_t const frac = cand.i & 0x007FFFFF; // 32 bit fraction mask
   return frac == 0x00000000 ? FALSE : 0xFF; // infinity : NaN
} // isFNaN(float)


//----------------------------   platform properties  ---------------------

const int numForEndianTest = 0x87654321;
uint8_t littleEndian(){ return *(char *)&numForEndianTest == 0x21; }


//---------------------------- logging and standard streams ---------------

/*  Event log output.
 *  default: standard output; may be put to a file.
 */
FILE * outLog;

/*  Use outLog for errors too.
 *
 *  When set true errLog will be set to outLog when using files.
 *  In this case there is just one event log file, and doubling the same
 *  entry to both errLog and outLog should be avoided.
 */
uint8_t useOutLog4errLog = OFF;

/*  Error log output.
 *  default: standard error; may be put to a file.
 */
FILE * errLog;


/*  Value output file.
 *  Normally a text file opened for append.
 *  For example CSV: one line = one value set.
 */
FILE * valFil;

static void initStreams(void) __attribute__((constructor));
static void initStreams(void){ // Warning by Eclipse: Unused static function
   outLog = stdout;          // Eclipse bugs: 474776 and 389577 (2014..2017)
   errLog = stderr;        // Eclipse bugs have a long life, partly.
   valFil = NULL; // should not be necessary
} // initStreams()   static initialiser


/* Switch errlog to file
 *
 * @param errFileNam the name of the file to switch to;
 *                   NULL: switch (back) to stderr
 * @return  96 : file name can't be opened for append; old state kept;
 *          97 : ::useOutLog4errLog is ON, nothing done
 */
int switchErrorLog(char const * const errFilNam){
   if (useOutLog4errLog) return 97;
   FILE * oldErr = errLog;
   FILE* f = stderr;
   if (errFilNam != NULL && errFilNam[0]) {
      f = fopen(errFilNam, "a"); // create or append to
      if (f == NULL) return 96; // no open
   }
   if (oldErr != stderr && oldErr != stdout && oldErr != NULL) {
      fflush(oldErr);
      fclose(oldErr);
   }
   errLog = f;
   return 0; // success
} // switchErrorLog(char const * const)

/*  Switch outLog to file.
 *
 *  @param logFilNam the name of the file to switch to;
 *                   NULL: switch (back) to stdout
 *  @return  96 : file name can't be opened for append; old state kept.
 */
int switchEventLog(char const * const logFilNam){
   FILE * oldOut = outLog;
   FILE * oldErr = errLog;
   FILE* f = stdout;
   if (logFilNam != NULL && logFilNam[0]) {
      f = fopen(logFilNam, "a"); // create or append to
      if (f == NULL) return 96; // no open
   }
   if (oldOut != stderr && oldOut != stdout && oldOut != NULL) {
      fflush(oldOut);
      fclose(oldOut);
   }
   if (useOutLog4errLog) {
      if (oldErr != stderr && oldErr != stdout && oldErr != NULL
            && (oldErr != oldOut || oldOut == NULL)) {
         fflush(oldErr);
         fclose(oldErr);
      }
      errLog = f;
   }
   outLog = f;
   return 0; // success
} // switchErrorLog(char const * const)

/*  Error log (errLog) is standard stream or outLog. */
uint8_t errLogIsStd(void){
   return (errLog == stderr) || (errLog == stdout)  || (errLog == outLog);
} // errLogIsStd()

/* Event log (outLog) is standard stream. */
uint8_t outLogIsStd(void){ return (outLog == stderr) || (outLog == stdout); }

/** Log an event/log message on outLog.
 *
 *  If txt is not null it will be output to outLog and outLog will be flushed.
 *
 *  @param txt text to be prepended
 */
void logEventText(char const * txt){
   if (txt == NULL || ! *txt) return;
   fputs(txt, outLog);
   fflush(outLog);
} // logEventText(char const *)


//------------------------------   times -------------------------------------


/*  Absolute time instant initialisation.  */
void monoTimeInit(timespec * timer){ clock_gettime(ABS_MONOTIME, timer); }

/*  Absolute delay for the specified number of µs. */
int timeStep(timespec * timeSp, unsigned int micros){
   timeAddNs(timeSp, (long)(micros) * 1000); // timer += micros
   return clock_nanosleep(ABS_MONOTIME, TIMER_ABSTIME, timeSp, NULL);
} // delay(unsigned int)

/*  Add a ns increment to a time overwriting it. */
void timeAddNs(timespec * t1, long ns){
   (*t1).tv_nsec += ns;
   if ((*t1).tv_nsec >= MILLIARD) {
       (*t1).tv_nsec -= MILLIARD;
       ++(*t1).tv_sec;
   }
} // timeAddNs(timT v*, long)



/*  Actual time (structure, real time clock). */
timespec actRTime;

/*  Actual time (broken down structure / local).  */
struct tm actRTm;

/*  Day in year. */
int todayInYear;

/*  Actual (local) UTC midnight. */
__time_t utcMidnight;

/*  Actual local midnight. */
__time_t localMidnight;


/*  Update local real time.  */
void updateReaLocalTime(void){
   clock_gettime(CLOCK_REALTIME, &actRTime);  // epoch time
   localtime_r(&actRTime.tv_sec, &actRTm); // structure safe
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
} // updateReaLocalTime()

/*  Cosine of day in year.
 *
 *  length: 192
 */
float const cosDiY[192] = {
 1.00000000,  0.99985184,  0.99940740,  0.99866682,  0.99763031,  0.99629817,
 0.99467082,  0.99274872,  0.99053245,  0.98802267,  0.98522011,  0.98212561,
 0.97874008,  0.97506453,  0.97110005,  0.96684781,  0.96230908,  0.95748519,
 0.95237758,  0.94698775,  0.94131732,  0.93536795,  0.92914141,  0.92263955,
 0.91586429,  0.90881764,  0.90150168,  0.89391860,  0.88607062,  0.87796008,
 0.86958939,  0.86096102,  0.85207752,  0.84294154,  0.83355577,  0.82392301,
 0.81404609,  0.80392796,  0.79357161,  0.78298010,  0.77215658,  0.76110426,
 0.74982640,  0.73832635,  0.72660752,  0.71467339,  0.70252747,  0.69017339,
 0.67761479,  0.66485540,  0.65189900,  0.63874942,  0.62541057,  0.61188640,
 0.59818091,  0.58429817,  0.57024229,  0.55601744,  0.54162782,  0.52707771,
 0.51237141,  0.49751329,  0.48250774,  0.46735922,  0.45207220,  0.43665123,
 0.42110087,  0.40542573,  0.38963045,  0.37371971,  0.35769824,  0.34157077,
 0.32534208,  0.30901699,  0.29260034,  0.27609697,  0.25951180,  0.24284972,
 0.22611569,  0.20931465,  0.19245158,  0.17553149,  0.15855939,  0.14154030,
 0.12447926,  0.10738135,  0.09025161,  0.07309513,  0.05591699,  0.03872228,
 0.02151610,  0.00430354, -0.01291030, -0.03012030, -0.04732139, -0.06450845,
-0.08167640, -0.09882014, -0.11593460, -0.13301471, -0.15005540, -0.16705163,
-0.18399835, -0.20089056, -0.21772323, -0.23449139, -0.25119006, -0.26781431,
-0.28435919, -0.30081981, -0.31719129, -0.33346878, -0.34964746, -0.36572252,
-0.38168922, -0.39754281, -0.41327861, -0.42889194, -0.44437818, -0.45973274,
-0.47495107, -0.49002867, -0.50496105, -0.51974381, -0.53437256, -0.54884296,
-0.56315072, -0.57729162, -0.59126144, -0.60505607, -0.61867140, -0.63210341,
-0.64534811, -0.65840158, -0.67125996, -0.68391942, -0.69637623, -0.70862668,
-0.72066715, -0.73249407, -0.74410394, -0.75549331, -0.76665882, -0.77759715,
-0.78830506, -0.79877937, -0.80901699, -0.81901489, -0.82877009, -0.83827971,
-0.84754092, -0.85655100, -0.86530725, -0.87380710, -0.88204802, -0.89002758,
-0.89774339, -0.90519319, -0.91237476, -0.91928597, -0.92592478, -0.93228921,
-0.93837739, -0.94418751, -0.94971784, -0.95496675, -0.95993269, -0.96461418,
-0.96900983, -0.97311834, -0.97693849, -0.98046916, -0.98370929, -0.98665793,
-0.98931420, -0.99167732, -0.99374658, -0.99552137, -0.99700117, -0.99818553,
-0.99907412, -0.99966665, -0.99996296, -0.99996296, -0.99966665, -0.99907412,
-0.99818553, -0.99700117, -0.99552137, -0.99374658, -0.99167732, -0.98931420
}; // cos(dayInYear) lookup table[192] day[191] 188.4grd 3.29rad -0.98931420

/*  Cosine of day in year * 60 .
 *
 *  includes minutes to seconds conversion
 *  lenght: 192
 */
int16_t const cosDiY60[192] = {
  60, 60, 60, 60, 60, 60, 60, 60, 59, 59,
  59, 59, 59, 59, 58, 58, 58, 57, 57, 57,
  56, 56, 56, 55, 55, 55, 54, 54, 53, 53,
  52, 52, 51, 51, 50, 49, 49, 48, 48, 47,
  46, 46, 45, 44, 44, 43, 42, 41, 41, 40,
  39, 38, 38, 37, 36, 35, 34, 33, 32, 32,
  31, 30, 29, 28, 27, 26, 25, 24, 23, 22,
  21, 20, 20, 19, 18, 17, 16, 15, 14, 13,
  12, 11, 10,  8,  7,  6,  5,  4,  3,  2,
   1,  0, -1, -2, -3, -4, -5, -6, -7, -8,
   -9, -10, -11, -12, -13, -14, -15, -16, -17, -18,
  -19, -20, -21, -22, -23, -24, -25, -26, -27, -28,
  -28, -29, -30, -31, -32, -33, -34, -35, -35, -36,
  -37, -38, -39, -40, -40, -41, -42, -43, -43, -44,
  -45, -45, -46, -47, -47, -48, -49, -49, -50, -50,
  -51, -51, -52, -52, -53, -53, -54, -54, -55, -55,
  -56, -56, -56, -57, -57, -57, -58, -58, -58, -58,
  -59, -59, -59, -59, -59, -60, -60, -60, -60, -60,
  -60, -60, -60, -60, -60, -60, -60, -60, -60, -60,
  -60, -59
}; // cos(dayInYear)*60 lookup table[192] day[191] 188.4grd 3.29rad -0.98931420


/*  Cosine of day in year, function.
 *
 *  This function provides the cosine by the day of the year very efficiently
 *  by using a lookup table (::cosDiY) and cosine's periodic properties.
 *
 *  For the main purpose of approximate sunrise or sunset time determination
 *  the usual (approximate) algorithm relates to shortest day (23.12.). In this
 *  case add 8 to the real day in the year.
 *
 *  @param dayInYear
 */
float cosDay(int16_t dayInYear){
   if (dayInYear < 0) dayInYear = - dayInYear;
   if (dayInYear > FOURYEARS) dayInYear %= FOURYEARS;
   if (dayInYear > YEAR) dayInYear %= YEAR;
   if (dayInYear >= 192) dayInYear = YEAR - dayInYear;
   return cosDiY[dayInYear];
} // cosDay(int16_t)

/*  Cosine of day in year * 60, function.
 *
 *  This function provides the cosine * 60 by the day of the year very
 *  efficiently by using a lookup table (::cosDiY) and cosine's periodic
 *  properties. The factor 60 includes a minutes to seconds conversion.
 *
 *  For the main purpose of approximate sunrise or sunset time determination
 *  the usual (approximate) algorithm relates to shortest day (23.12.). In this
 *  case add 8 to the real day in the year.
 *
 *  @param dayInYear
 */
int16_t  cosDay60(int16_t dayInYear){
   if (dayInYear < 0) dayInYear = -dayInYear;
   if (dayInYear > FOURYEARS) dayInYear %= FOURYEARS;
   if (dayInYear > YEAR) dayInYear %= YEAR;
   if (dayInYear >= 192) dayInYear = YEAR - dayInYear;
   return cosDiY60[dayInYear];
} // cosDay60(int16_t)

#define USE_COS_FLOAT 0

/*  Get sunrise in s from UTC midnight.
 *
 *  The value will be approximately (but very fast) calculated on base of the
 *  the location's (and optimally year's) sunrise data. <br />
 *  Caveat: Consider the units and bases of the parameters.
 *
 *  @param dayInYear day in the year
 *  @param meanSunriseSec location's mean sunrise time in s from midnight UTC
 *  @param halfRiseDeltaMin the location's half sunrise time swing in minutes
 *
 *  @return that days's sunrise in seconds from UTC midnight
 */
__time_t getDaySunrise(int16_t const dayInYear,
      uint32_t const meanSunriseSec, uint16_t const halfRiseDeltaMin){
#if USE_COS_FLOAT
   float const cDay = cosDay(dayInYear + 8);
   float delta = (halfRiseDeltaMin * 60) * cDay;
   delta += delta > 0 ? 0.5 : -0.5;
   return meanSunriseSec + (__time_t )delta;
#else
   int16_t const cDay60 = cosDay60(dayInYear + 8);
   return meanSunriseSec +  halfRiseDeltaMin * cDay60;
#endif
} // getDaySunrise(int16_t)

/*  Get sunset in s from UTC midnight.
 *
 *  The value will be approximately (but very fast) calculated on base of the
 *  the location's (and optimally year's) sunset data. <br />
 *  Caveat: Consider the units and bases of the parameters.
 *
 *  @param dayInYear day in the year (0 is January 1st)
 *  @param meanSunsetSec location's mean sunset time in s from midnight UTC
 *  @param halfSetDeltaMin the location's half sunset time swing in minutes
 *  @return that days's sunset in seconds from UTC midnight
 */
__time_t getDaySunset(int16_t const dayInYear,
      uint32_t const meanSunsetSec, uint16_t const halfSetDeltaMin){
#if USE_COS_FLOAT
   float const cDay = cosDay(dayInYear + 8);
   float delta = (halfSetDeltaMin * 60) * cDay;
   delta += delta > 0 ? 0.5 : -0.5;
   return MEAN_SUN_SET - (__time_t )delta;
#else
   int16_t const cDay60 = cosDay60(dayInYear + 8);
   return meanSunsetSec - halfSetDeltaMin * cDay60;
#endif
} // getDaySunrise(int16_t)



//---------------------- sting utilities ------------------------------------

/*  String copy with limit. */
size_t strlcpy(char * dest, char const * src, size_t const num){
   const char * s = src;
   size_t n = num;
   if (n != 0 && --n != 0) do { // copy as many bytes as will fit
      if ((*dest++ = *s++) == 0) return(s - src - 1);  // count not includes 0
   } while (--n != 0);
   // to here only when not enough room and no terminating 0, yet
   if (num != 0) *dest = '\0'; // replace last transferred char by terminating 0
   // this sh.t is done as anybody decided to return source length
   while (*s++);  // advance to source end
   return(s - src - 1);  // count does not include \0
} // strlcpy(char *, char const *, size_t)

/*  String cat with limit.
 *
 *  This function appends at most num - 1 characters from src to dst the end
 *  of dest. If not terminated by a 0 from src, dest[num-1] will be set 0.
 *  Hence, except for num == 0, dest will be 0-terminated.
 *
 *  num is the
 *
 *  The value returned is the length of string src; if this value is not
 *  less than num truncation occurred.
 *
 *  Hint: This function resembles the one from bsd/string.h usually not
 *  available with standard Linuxes and Raspbians .
 *
 *  @param dest the character array to copy to; must not be shorter than num
 *  @param src  the string to copy from
 *  @param num  the maximum allowed string length of dest
 *  @return     the length of src
 */
size_t strlcat(char * dest, char const * src, size_t num){
   char * d = dest;
   const char * s = src;
   size_t n = num;
   size_t dLen;

   // Find the end of dst within 0 .. num
   while (n-- != 0 && *d != '\0') ++d;
   dLen = d - dest; // length if 0 found
   n = num - dLen; // max. number of characters left to be appended
   if (n == 0) { // nothing to append, but we have to return src length
      while(*d != '\0') { ++d; ++dLen; }
      return dLen;
   }
   while (*s != '\0') {
      if (n != 1) {
         *d++ = *s;
         --n;
      }
      ++s;
   }
   *d = '\0';
   return(dLen + (s - src));
} // strlcat(char *, char const *, size_t)


//---------------------- formatting -----------------------------------------

/* English weekdays, two letter abbreviation (Su 0) */
char const dow[9][4] = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su", "--\0"};


char const dec2digs[102][2] = {
  "00", "01", "02", "03", "04", "05", "06", "07", "08", "09",
  "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
  "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
  "30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
  "40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
  "50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
  "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
  "70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
  "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
  "90", "91", "92", "93", "94", "95", "96", "97", "98", "99",
  "00", "_1" // 00 for an optimisation; "_1" for fault
};


char const zif2charMod10[44] = "01234567890123456789012345678901234567890123";
//                              0         x         v         t         q

/*  Format broken down real time as standard text.
 *
 *  The format is:  Fr 2017-10-20 13:55:12 UTC+01
 */
int formatTmTim(char * rTmTxt, struct tm  * rTm){
   if (rTmTxt == NULL) return 0;
   if (rTm == NULL) rTm = &actRTm;
   memcpy(rTmTxt, "Fr 2o17-ll-24 1o:Z9:12 UTC+o1\0\0", 29); // template
// ................0123456789x123456789v123456789t1234
   memcpy(rTmTxt, dow[rTm->tm_wday], 2);
   int v1 = rTm->tm_year;
   int v2 = v1 / 100 + 19;
   memcpy(rTmTxt +  3, dec2digs[v2], 2);
   v2 = v1 % 100;
   memcpy(rTmTxt +  5, dec2digs[v2], 2);
   memcpy(rTmTxt +  8, dec2digs[rTm->tm_mon + 1], 2); // brain dead: Jan. is 0!
   memcpy(rTmTxt + 11, dec2digs[rTm->tm_mday], 2);
   memcpy(rTmTxt + 14, dec2digs[rTm->tm_hour], 2);
   memcpy(rTmTxt + 17, dec2digs[rTm->tm_min], 2);
   memcpy(rTmTxt + 20, dec2digs[rTm->tm_sec], 2);

   memcpy(rTmTxt + 27, dec2digs[rTm->tm_gmtoff / 3600], 2);
   return 28;
} // formatTmTim(char *, struct tm)

/*  Format broken down real time clock+ms as standard text.
 *
 *  The format is:  Fr 2017-10-20 13:55:12.987 UTC+02
 */
int formatTmTiMs(char * rTmTxt, struct tm  * rTm, int millis){
   if (rTmTxt == NULL) return 0;
   if (rTm == NULL) rTm = &actRTm;
   memcpy(rTmTxt, "Fr 2o17-ll-24 1o:Z9:12.987 UTC+o1\0\0", 33); // template
// ................0123456789x123456789v123456789t1234
   memcpy(rTmTxt, dow[rTm->tm_wday], 2);
   int v1 = rTm->tm_year;
   int v2 = v1 / 100 + 19;
   memcpy(rTmTxt +  3, dec2digs[v2], 2);
   v2 = v1 % 100;
   memcpy(rTmTxt +  5, dec2digs[v2], 2);
   memcpy(rTmTxt +  8, dec2digs[rTm->tm_mon + 1], 2); // brain dead: Jan. is 0!
   memcpy(rTmTxt + 11, dec2digs[rTm->tm_mday], 2);
   memcpy(rTmTxt + 14, dec2digs[rTm->tm_hour], 2);
   memcpy(rTmTxt + 17, dec2digs[rTm->tm_min], 2);
   memcpy(rTmTxt + 20, dec2digs[rTm->tm_sec], 2);

   v1 = millis / 100;
   v2 = millis % 100;
   rTmTxt[23] = '0' + v1;
   memcpy(rTmTxt + 24, dec2digs[v2], 2);
   memcpy(rTmTxt + 31, dec2digs[rTm->tm_gmtoff / 3600], 2);
   return 32;
} // formatTmTiMs(char *, struct tm, int)

/*
 * File types

#define DT_UNKNOWN       0
#define DT_FIFO          1
#define DT_CHR           2
#define DT_DIR           4
#define DT_BLK           6
#define DT_REG           8
#define DT_LNK          10
#define DT_SOCK         12
#define DT_WHT          14
DT_UNKNOWN     -              unknown file type
          DT_FIFO        S_IFIFO        named pipe
          DT_CHR         S_IFCHR        character device
          DT_DIR         S_IFDIR        directory
          DT_BLK         S_IFBLK        block device
          DT_REG         S_IFREG        regular file
          DT_LNK         S_IFLNK        symbolic link
          DT_SOCK        S_IFSOCK       UNIX domain socket
          DT_WHT         S_IFWHT        dummy ``whiteout inode''
*/

/* translation of directory entry typed to 6 char text. */
char const fType[16][8] = {
//  012345678
   "unknwn\0", // 0   DT_UNKNOWN     -              unknown file type
   "pipeNn\0", // 1   DT_FIFO        S_IFIFO        named pipe
   "charDv\0", // 2   DT_CHR         S_IFCHR        character device
   "undef3\0", // 3
   "direct\0", // 4   DT_DIR         S_IFDIR        directory
   "undef5\0", // 5
   "blckDv\0", // 6   DT_BLK         S_IFBLK        block device
   "undef7\0", // 7
   "rgFile\0", // 8   DT_REG         S_IFREG        regular file
   "undef9\0", // 9
   "symLnk\0", // 10  DT_LNK         S_IFLNK        symbolic link
   "undefB\0", // 11
   "socket\0", // 12  DT_SOCK        S_IFSOCK       UNIX domain socket
   "undefD\0", // 13
   "dummyF\0", // 14  DT_WHT         S_IFWHT        dummy, whiteout inode
   "undefF\0"};// 15
