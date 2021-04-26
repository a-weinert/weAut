/** @file weRasp/sysBasic.c
 *
   Some system related basic functions for Raspberry Pis

\code
   Copyright  (c)  2020   Albrecht Weinert
   weinert-automation.de      a-weinert.de

 *     /         /      /\
 *    /         /___   /  \      |
 *    \        /____\ /____\ |  _|__
 *     \  /\  / \    /      \|   |
 *      \/  \/   \__/        \__/|_                                 \endcode

   Revision history \code
   Rev. $Revision: 236 $ $Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $
   Rev.  105 06.02.2018 : new (transfered parts from sysutil.c)
   Rev.  108 12.02.2018 : parts moved out, one event file; Started to get
                         Doxygen usable. Note: Not yet done.
   Rev. 147 16.06.2018 : time handling enhanced improved
   Rev. 155 27.06.2018 : time handling debugged
   Rev. 164 11.07.2018 : sunset/sunrise location parameters; string functions
   Rev. 229 23.07.2020 : UTF 8 BOM for log and error file
\endcode
   cross-compile by: \code
   arm-linux-gnueabihf-gcc -DMCU=BCM2837 -I./include -c -o weRasp/sysBasic.o weRasp/sysBasic.c
\endcode

   For documentation see the include file \ref sysBasic.h
*/
#include "sysBasic.h" // indirectly includes ...

/*  Basic start-up function failure. */
int retCode;

/* Common path to a lock file for GpIO use */
char const  * const lckPiGpioPth = "/home/pi/bin/.lockPiGpio";


//----------------------------   program name date etc.  ------------------

///  char progNamPure[] = PROGNAME;
uint8_t progNamLen = 0;
char progNamBlnk[] = "                                       \0\0";
//                  01234 fixed length 4 right justified
char progSVNrevi[] = "   ?\0\0";
char progSVNdate[] = "yyyy-mm-dd\0\0\0";

/*  The program name length.
 *
 *  This function determines the programs name's length and prepares the
 *  program strings if not yet done
 *
 *  @return the programs name's length
 */
uint8_t progNameLen() {
   if (progNamLen) return progNamLen;
   char c = prgNamPure[0];
   for (;;) {
      if (c == '\0') break;
      progNamBlnk[progNamLen] = c;
      c = prgNamPure[++progNamLen];
   } // for copy and find end
   progNamBlnk[progNamLen < 17 ? 17 : progNamLen + 1] = '\0';

   int i = 11;
   while (prgSVNrev[i] >= '0' ) { ++i; }
   int j = 3;
   for (; j >= 0; --j) {
      c = prgSVNrev[--i];
      if (c < '0') break;
      progSVNrevi[j] = c;
   } // for

   j = 7; i = 0;
   for (; i < 10; ++j, ++i) {  progSVNdate[i] = prgSVNdat[j]; }
/*  TEST xxxx
   fprintf(outLog,
      "T E S T \n"
      "progNamLen  = %d \n"
      "prgNamPure  = %s \n"
      "progNamBlnk = #%s#\n"
      "progSVNrevi = %s (%s)\n",
   progNamLen, prgNamPure, progNamBlnk, progSVNrevi, progSVNdate);
xxxxx TEST */

   return progNamLen;
} // progNamLen()

/*  The program name.
 *
 *  @return the program's name as pure text, "homeDoorPhone", e.g.
 */
char  const * progNam(){
   if (progNamLen) return prgNamPure;
   progNameLen();
   return prgNamPure;
} // progNam()

/** The program name with blank.
 *
 *  Same as ::progNam but with at least one trailing blank or so many blanks
 *  to get a minimal length of 17, , "homeDoorPhone    ", e.g.
 *
 *  @return the program's name with trailing blank(s)
 */
char const * progNamB(){
   if (progNamLen) return progNamBlnk;
   progNameLen();
   return progNamBlnk;
} // progNamB()


/*  The program revision.
 *
 *  @return the program's SVN revision as pure text, "0", "341" e.g.
 */
char const * progRev(){
   if (progNamLen) return progSVNrevi;
   progNameLen();
   return progSVNrevi;
} // progRev()

/*  The program date.
 *
 *  @return the program's SVN date "2020-07-23" e.g., length 10
 */
char const * progDat(){
   if (progNamLen) return progSVNdate;
   progNameLen();
   return progSVNdate;
} // progDat()


char const revDatFrm[] = "    Revision %s (%.16s)\n";

/* Print the program SVN revision and date. */
void printRevDat(void){
   progNameLen(); // ensure prepared strings
   fprintf(outLog, "    Revision %s (%s)\n", progSVNrevi, progSVNdate);
} // printRevDat()

/*  Print the program name, SVN revision and date. */
void printNamRevDat(void){
   progNameLen(); // ensure prepared strings
   fprintf(outLog, "    %sR. %s (%s)\n",
           progNamBlnk, progSVNrevi, progSVNdate);
} // printNamRevDat()

//----------------------------   floating point helper  -------------------

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

/* Log on files
 *
 *  If true (default) logging and errors go to files or one file, otherwise
 *  to console
 */
int useErrLogFiles = ON; // if logging, then on files


/*  Event log output.
 *  default: standard output; may be put to a file.
 */
FILE * outLog;

/*  Number of events logged.
 *
 *  Counter for lines put to or events logged on ::outLog.
 */
uint32_t noLgdEvnt;

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

static void initStreams(void) __attribute__((constructor));
static void initStreams(void){ // Warning by Eclipse: Unused static function
   outLog = stdout;          // Eclipse bugs: 474776 and 389577 (2014..2017)
   errLog = stderr;        // Eclipse bugs have a long life, partly.
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
   if (errFilNam != NULL && errFilNam[0]) { // real file name
      f = fopen(errFilNam, "a"); // create or append to
      if (f == NULL) return 96; // no open
      fputs("\xEF\xBB\xBF", f); // append UTF8 BOM
   } // name of a real file (hopefully)
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
   if (logFilNam != NULL && logFilNam[0]) { // real file name
      f = fopen(logFilNam, "a"); // create or append to
      if (f == NULL) return 96; // no open
      fputs("\xEF\xBB\xBF", f); // append UTF8 BOM
   } // name of a real file (hopefully)
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
   noLgdEvnt = 0;
   outLog = f;
   return 0; // success
} // switchEventLog(char const * const)

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
 *  @param txt text to be output
 */
void logEventText(char const * txt){
   if (txt == NULL || ! *txt) return;
   fputs(txt, outLog);
   fflush(outLog);
   //  ??? ++noLgdEvnt;
} // logEventText(char const *)


//------------------------------   times -------------------------------------

/*  Absolute time instant initialisation.  */
void monoTimeInit(timespec * timer){ clock_gettime(ABS_MONOTIME, timer); }

// &mu; is µ (my spoiled in Doxygen documentation viewed in European browser)
/*  Absolute delay for the specified number of &mu;s. */
int timeStep(timespec * timeSp, unsigned int micros){
   timeAddNs(timeSp, (long)(micros) * 1000); // timer += micros
   return clock_nanosleep(ABS_MONOTIME,  // default: CLOCK_MONOTONIC
                         TIMER_ABSTIME, timeSp, NULL);
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



//---------------------- string utilities -----------------------------------

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
 *  This function appends at most num - 1 characters from src to the end
 *  of dest. If not terminated by a 0 from src, dest[num-1] will be set 0.
 *  Hence, except for num == 0, dest will be 0-terminated.
 *
 *  The value returned is the length of string src (if no truncation occurred).
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

/* English weekdays, two letter abbreviation (Su 0 & 7) */
char const dow[9][4] = {"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su", "--\0"};


char const dec2digs[128][2] = {
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
  "00", "_1", "_2", "_3", "_4", "_5", "_6", "_7", "_8", "_9", // 00 optimise
  "_0", "_1", "_2", "_3", "_4", "_5", "_6", "_7", "_8", "_9", // "_x" for fault
  "_0", "_1", "_2", "_3", "_4", "_5", "_6", "_7" };

/* Format number as two digit decimal number with leading zeroes.
 *
 *  The format is:  00 to 99
 *
 *  The length is always 2. There is no trailing character zero appended. <br />
 *  returned is the number of leading zeroes in the range 0 o 1. N.B.
 *  the value 0 yielding "00" is considered to have one leading zero.
 *  @see dec2digs formatDec3Digs
 *  @param targTxt pointer to the target text buffer,
 *                 must have place for 3 characters (!)
 *  @param value   the value to be formatted; values outside 0 .. 999
 *                 will yield incorrect results
 *  @return        the number of leading zeroes (0 or 1)
 */
int formatDec2Digs(char * targTxt, uint32_t value){
  value &= 127;
  char const * sp = dec2digs[value];
  char * dp = targTxt;
  *dp = *sp;
  *++dp = *++sp;
  return value > 9 ? 0 : 1;
} // formatDec2Digs(char *, uint32_t)


char const dec3digs[1024][4] = {
    "000", "001", "002", "003", "004", "005", "006", "007", "008", "009",
    "010", "011", "012", "013", "014", "015", "016", "017", "018", "019",
    "020", "021", "022", "023", "024", "025", "026", "027", "028", "029",
    "030", "031", "032", "033", "034", "035", "036", "037", "038", "039",
    "040", "041", "042", "043", "044", "045", "046", "047", "048", "049",
    "050", "051", "052", "053", "054", "055", "056", "057", "058", "059",
    "060", "061", "062", "063", "064", "065", "066", "067", "068", "069",
    "070", "071", "072", "073", "074", "075", "076", "077", "078", "079",
    "080", "081", "082", "083", "084", "085", "086", "087", "088", "089",
    "090", "091", "092", "093", "094", "095", "096", "097", "098", "099",

    "100", "101", "102", "103", "104", "105", "106", "107", "108", "109",
    "110", "111", "112", "113", "114", "115", "116", "117", "118", "119",
    "120", "121", "122", "123", "124", "125", "126", "127", "128", "129",
    "130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
    "140", "141", "142", "143", "144", "145", "146", "147", "148", "149",
    "150", "151", "152", "153", "154", "155", "156", "157", "158", "159",
    "160", "161", "162", "163", "164", "165", "166", "167", "168", "169",
    "170", "171", "172", "173", "174", "175", "176", "177", "178", "179",
    "180", "181", "182", "183", "184", "185", "186", "187", "188", "189",
    "190", "191", "192", "193", "194", "195", "196", "197", "198", "199",

    "200", "201", "202", "203", "204", "205", "206", "207", "208", "209",
    "210", "211", "212", "213", "214", "215", "216", "217", "218", "219",
    "220", "221", "222", "223", "224", "225", "226", "227", "228", "229",
    "230", "231", "232", "233", "234", "235", "236", "237", "238", "239",
    "240", "241", "242", "243", "244", "245", "246", "247", "248", "249",
    "250", "251", "252", "253", "254", "255", "256", "257", "258", "259",
    "260", "261", "262", "263", "264", "265", "266", "267", "268", "269",
    "270", "271", "272", "273", "274", "275", "276", "277", "278", "279",
    "280", "281", "282", "283", "284", "285", "286", "287", "288", "289",
    "290", "291", "292", "293", "294", "295", "296", "297", "298", "299",

    "300", "301", "302", "303", "304", "305", "306", "307", "308", "309",
    "310", "311", "312", "313", "314", "315", "316", "317", "318", "319",
    "320", "321", "322", "323", "324", "325", "326", "327", "328", "329",
    "330", "331", "332", "333", "334", "335", "336", "337", "338", "339",
    "340", "341", "342", "343", "344", "345", "346", "347", "348", "349",
    "350", "351", "352", "353", "354", "355", "356", "357", "358", "359",
    "360", "361", "362", "363", "364", "365", "366", "367", "368", "369",
    "370", "371", "372", "373", "374", "375", "376", "377", "378", "379",
    "380", "381", "382", "383", "384", "385", "386", "387", "388", "389",
    "390", "391", "392", "393", "394", "395", "396", "397", "398", "399",

    "400", "401", "402", "403", "404", "405", "406", "407", "408", "409",
    "410", "411", "412", "413", "414", "415", "416", "417", "418", "419",
    "420", "421", "422", "423", "424", "425", "426", "427", "428", "429",
    "430", "431", "432", "433", "434", "435", "436", "437", "438", "439",
    "440", "441", "442", "443", "444", "445", "446", "447", "448", "449",
    "450", "451", "452", "453", "454", "455", "456", "457", "458", "459",
    "460", "461", "462", "463", "464", "465", "466", "467", "468", "469",
    "470", "471", "472", "473", "474", "475", "476", "477", "478", "479",
    "480", "481", "482", "483", "484", "485", "486", "487", "488", "489",
    "490", "491", "492", "493", "494", "495", "496", "497", "498", "499",

    "500", "501", "502", "503", "504", "505", "506", "507", "508", "509",
    "510", "511", "512", "513", "514", "515", "516", "517", "518", "519",
    "520", "521", "522", "523", "524", "525", "526", "527", "528", "529",
    "530", "531", "532", "533", "534", "535", "536", "537", "538", "539",
    "540", "541", "542", "543", "544", "545", "546", "547", "548", "549",
    "550", "551", "552", "553", "554", "555", "556", "557", "558", "559",
    "560", "561", "562", "563", "564", "565", "566", "567", "568", "569",
    "570", "571", "572", "573", "574", "575", "576", "577", "578", "579",
    "580", "581", "582", "583", "584", "585", "586", "587", "588", "589",
    "590", "591", "592", "593", "594", "595", "596", "597", "598", "599",

    "600", "601", "602", "603", "604", "605", "606", "607", "608", "609",
    "610", "611", "612", "613", "614", "615", "616", "617", "618", "619",
    "620", "621", "622", "623", "624", "625", "626", "627", "628", "629",
    "630", "631", "632", "633", "634", "635", "636", "637", "638", "639",
    "640", "641", "642", "643", "644", "645", "646", "647", "648", "649",
    "650", "651", "652", "653", "654", "655", "656", "657", "658", "659",
    "660", "661", "662", "663", "664", "665", "666", "667", "668", "669",
    "670", "671", "672", "673", "674", "675", "676", "677", "678", "679",
    "680", "681", "682", "683", "684", "685", "686", "687", "688", "689",
    "690", "691", "692", "693", "694", "695", "696", "697", "698", "699",

    "700", "701", "702", "703", "704", "705", "706", "707", "708", "709",
    "710", "711", "712", "713", "714", "715", "716", "717", "718", "719",
    "720", "721", "722", "723", "724", "725", "726", "727", "728", "729",
    "730", "731", "732", "733", "734", "735", "736", "737", "738", "739",
    "740", "741", "742", "743", "744", "745", "746", "747", "748", "749",
    "750", "751", "752", "753", "754", "755", "756", "757", "758", "759",
    "760", "761", "762", "763", "764", "765", "766", "767", "768", "769",
    "770", "771", "772", "773", "774", "775", "776", "777", "778", "779",
    "780", "781", "782", "783", "784", "785", "786", "787", "788", "789",
    "790", "791", "792", "793", "794", "795", "796", "797", "798", "799",

    "800", "801", "802", "803", "804", "805", "806", "807", "808", "809",
    "810", "811", "812", "813", "814", "815", "816", "817", "818", "819",
    "820", "821", "822", "823", "824", "825", "826", "827", "828", "829",
    "830", "831", "832", "833", "834", "835", "836", "837", "838", "839",
    "840", "841", "842", "843", "844", "845", "846", "847", "848", "849",
    "850", "851", "852", "853", "854", "855", "856", "857", "858", "859",
    "860", "861", "862", "863", "864", "865", "866", "867", "868", "869",
    "870", "871", "872", "873", "874", "875", "876", "877", "878", "879",
    "880", "881", "882", "883", "884", "885", "886", "887", "888", "889",
    "890", "891", "892", "893", "894", "895", "896", "897", "898", "899",

    "900", "901", "902", "903", "904", "905", "906", "907", "908", "909",
    "910", "911", "912", "913", "914", "915", "916", "917", "918", "919",
    "920", "921", "922", "923", "924", "925", "926", "927", "928", "929",
    "930", "931", "932", "933", "934", "935", "936", "937", "938", "939",
    "940", "941", "942", "943", "944", "945", "946", "947", "948", "949",
    "950", "951", "952", "953", "954", "955", "956", "957", "958", "959",
    "960", "961", "962", "963", "964", "965", "966", "967", "968", "969",
    "970", "971", "972", "973", "974", "975", "976", "977", "978", "979",
    "980", "981", "982", "983", "984", "985", "986", "987", "988", "989",
    "990", "991", "992", "993", "994", "995", "996", "997", "998", "999",

    "000", "001", "002", "003", "004", "005", "006", "007", "008", "009",
    "010", "011", "012", "013", "014", "015", "016", "017", "018", "019",
    "020", "021", "022", "023"  // 000 .. 023 for an optimisation
};

/*  Format number as three digit decimal number with leading zeroes.
 *
 *  The format is:  000 to 999
 *
 *  The length is always 3. There is no trailing zero appended.
 *  See ::dec3digs
 *
 *  @param targTxt pointer to the target text buffer,
 *                 must have place for 3 characters (!)
 *  @param value   the value to be formatted; values outside 0 .. 999
 *                 will yield incorrect results
 *  @return        the number of leading zeroes (0..2)
 */
int formatDec3Digs(char * targTxt, uint32_t value){
  value &= 1023;
  char const * sp = dec3digs[value];
  char * dp = targTxt;
  *dp = *sp;
  *++dp = *++sp;
  *++dp = *++sp;
  return value > 99 ? 0 : value > 9 ? 1 : 2;
} // formatDec3Digs(char *, uint32_t)



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
