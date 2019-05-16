/** @file include/sysBasic.h
 *
 *  Some very basic definitions
 *
\code
   Copyright  (c)  2019   Albrecht Weinert
   weinert-automation.de      a-weinert.de

 *     /         /      /\
 *    /         /___   /  \      |
 *    \        /____\ /____\ |  _|__
 *     \  /\  / \    /      \|   |
 *      \/  \/   \__/        \__/|_                                 \endcode

   Revision history \code
   Rev. $Revision: 201 $ $Date: 2019-04-26 14:27:14 +0200 (Fr, 26 Apr 2019) $
   Rev. 66+ 16.11.2017 : new, excerpted from weUtil.h V.66
                          weModbus.h V.66 (and others)
   Rev. 108 12.02.2018 : Started to get Doxygen usable. Note: Not yet done.
   Rev. 147 16.06.2018 : time handling enhanced improved, Hippogreiff relay
   Rev. 152 21.06.2018 : some definitions put to basicTyCo.h
   Rev. 164 11.07.2018 : sunset/sunrise location parameters; string functions
   Rev. 190 14.02.2019 : minor, comments only
\endcode

 *  This file contains some definitions concerning system values and
 *  platform properties. <br />
 *  The latter are mainly made and probed with Raspberry Pi.
 */

#ifndef SYSBASIC_H
#define SYSBASIC_H
#ifndef __DOXYGEN__
#include <basicTyCo.h>  // uint32_t
#include <time.h>      // timespec, nanosleep
#include <stdio.h>    // FILE
#include <string.h>  // memset
// #include "sweetHomeLocal.h" // location data (macros): MEAN_SUN_RISE &c.
#endif

/** The Linux time structure.
 *
 *  This is the timespec structure consisting of two (type obfuscated) long
 *  variables: tv_sec and tv_nsec. <br />
 *  Note: This is to allow using timespec without prepending struct, only.
 */
typedef struct timespec timespec;


// ------------------ platform property endianess  ----------- -------------

/** Actual runtime / architecture is little endian.
 *
 *  This boolean function is evaluated by char* to int comparison.
 *
 *  To save runtime resources use the marco ::PLATFlittlE instead, which
 *  would fall back to littleEndian() (this function) when no target platform
 *  informations on endianness are available.
 *
 *  @return true when platform is little endian (evaluated at run time)
 */
uint8_t littleEndian();

/** Floating point NaN.
 *
 *  @param val the floating point value to be checked for IEEE754 NaN
 *  @return 0xFF (true) when not a number, else 0
 * */
uint8_t isFNaN(float const val);


//---------------------------- logging and standard streams ---------------

/** Event log output.
 *
 *  default: standard output; may be put to a file.
 */
extern FILE * outLog;

/** Use outLog for errors too.
 *
 *  When set true ::errLog will be set to ::outLog when using files.
 *  In this case there is just one event log file. Hence, doubling the same
 *  entry to both errLog and outLog should be avoided.
 */
extern uint8_t useOutLog4errLog;

/** Error log output.
 *
 *  default: standard error; may be put to a file.
 */
extern FILE * errLog;


/** Switch errlog to other file.
 *
 *  @param errFilNam the name of the file to switch to;
 *                   NULL or empty: switch (back) to stderr
 *  @return  96 : file name can't be opened for append, old state kept;
 *           97 : ::useOutLog4errLog is ON, nothing done
 *            0 : OK
 */
int switchErrorLog(char const * const errFilNam);

/** Switch outLog to other file.
 *
 *  If ::useOutLog4errLog is ON the ::errLog file will point to the same named
 *  file on success.
 *  @param logFilNam the name of the file to switch to;
 *                   NULL or empty: switch (back) to stdout
 *  @return  96 : file name can't be opened for append; old state kept.
 */
int switchEventLog(char const * const logFilNam);

/** Error log (errLog) is standard stream or outLog. */
uint8_t errLogIsStd(void);

/** Event log (outLog) is standard stream. */
uint8_t outLogIsStd(void);


// -----------------------  utilities    ------------------------------------

/** String copy with limit.
 *
 *  This function copies at most num - 1 characters from src to dst. If not
 *  terminated by a 0 from src, dest[num-1] will be set 0.
 *  Hence, except for num == 0, dest will be 0-terminated.
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
size_t strlcpy(char * dest, char const * src, size_t const num);

/** String concatenation with limit.
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
size_t strlcat(char * dest, char const * src, size_t num);


//-------------------------  time handling basics ----------------------------

/** Actual time (structure, real time clock). */
extern timespec actRTime;

/** Actual time (broken down structure / local).
 *
 *  This structure is initialised may be updated by some timing and cyclic
 *  functions. See ::initStartRTime() and others.
 */
extern struct tm  actRTm;

/** Today's day in year.
 *
 *  The value should be set at start (will be by ::updateReaLocalTime()) and
 *  updated at midnight (when used).
 */
extern int todayInYear;

/** Actual (local) UTC midnight.
 *
 *  This is the actual "local" UTC midnight. "Local" means that on early hours,
 *  i.e. those within zone offset, UTC midnight will be corrected to the next
 *  (east of Greenwich) respectively previous day (west). The rationale is
 *  to point to the same day or date at day time (around Europe). <br />
 *  Or, to put it simple, utcMidnight is to be set so, that the equation<br />
 *  &nbsp; &nbsp; &nbsp;   local Midnight = utcMidnight - UTV offset <br />
 *  holds.
 *
 *  The value will will be set correctly by  ::updateReaLocalTime(). It should
 *  be updated at day change (if used).
 */
extern __time_t utcMidnight;

/** Actual local midnight.
 *
 *  This is the UTC Linux timestamp of the actual day's / time's local
 *  midnight. See also ::utcMidnight for explanations.
 *
 *  Note: On days with DST changes this value will shift within the day. It's
 *  mostly better to make calculations relative to day start &mdash; sunrise
 *  and sunset e.g. &mdash; relative to UTC midnight.
 */
extern __time_t localMidnight;

/** Update local real time.
 *
 *  This function initialises / updates both ::actRTime and ::actRTm.
 */
void updateReaLocalTime(void);

/** Cosine of day in year, look up.
 *
 *  This lookup table provides the cosine by the day of the year without
 *  resource eating floating point arithmetic or math.h. The rationale is
 *  the approximate calculation of sunrise and sunset times based on earliest,
 *  latest and delta for any given location within the arctic circles.
 *
 *  The length of the look up table is abundant 192. According to cosine's
 *  periodic properties it shall be used in the range 0..183 by applying
 *  the following operations to the day in year value <br />
 *   absolute when &lt; 0,  <br />
 *   modulo FOURYEARS when &gt;= FOURYEARS, <br />
 *   modulo 365 when &gt;= 365 and <br />
 *   x = 365 - x when &gt; 190.
 *
 *   Note: These rules are implemented in the function ::cosDay()
 *   and in the function ::cosDay60() using the look up table ::cosDiY60
 */
extern float const cosDiY[192];

//#ifndef __DOXYGEN__
#ifndef int16_t // Eclipse CTD / indexer is sh.t
# define int16_t short int // type 'int16_t' could not be resolved
#endif // Eclipse CTD / indexer is sh.t
//  #endif // the shit Eclipse needs spoils Doxigen

/** Cosine of day in year * 60
 *
 *  This look up table is the same as ::cosDiY, except the values being
 *  multiplied by 60 which includes minutes to seconds conversion, avoiding
 *  a multiplication and all floating point operations for some applications.
 *
 *  lenght: 192
 */
extern int16_t const cosDiY60[192];


/** Cosine of day in year, function.
 *
 *  This function provides the cosine by the day of the year very efficiently
 *  by using a lookup table (::cosDiY) and cosine's periodic properties.
 *
 *  For the main purpose of approximate sunrise or sunset time determination
 *  the usual (approximate) algorithm relates to shortest day (23.12.). In this
 *  case add 8 to the real day in the year.
 *
 *  @param dayInYear the day in the year; 0: 1.1. (respectively 23.12.)
 */
float cosDay(int16_t dayInYear);

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
int16_t cosDay60(int16_t dayInYear);


/** Get sunrise in s from UTC midnight.
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
         uint32_t const meanSunriseSec, uint16_t const halfRiseDeltaMin);

/** Get sunset in s from UTC midnight.
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
             uint32_t const meanSunsetSec, uint16_t const halfSetDeltaMin);


//--------------------------------  formatting -------------------------------

/** The digits 0..9 repeated as 44 characters.
 *
 *  By using a number 0..43 as index this will give modulo 10 respectively the
 *  last decimal digit as character.
 */
extern char const zif2charMod10[44];

extern char const dec2digs[102][2]; //!< "00" .. "99" + "00", "XX"

/** English weekdays, two letter abbreviation.
 *
 *  Monday (Mo) is 1; Sunday (Su) is 0 or  also 8.
 */
extern char const dow[9][4];


/** Format broken down real time and date as standard text.
 *
 *  The format is:  Fr 2017-10-20 13:55:12 UTC+20
 *  ................0123456789x123456789v123456789t
 *  The length is 29.
 *  See ::formatTmTiMs() for a longer format with 3 digit ms.
 *
 *  @param rTmTxt pointer to the target text buffer,
 *                must have place for 30 characters (!)
 *  @param rTm    pointer to broken down real time; NULL will take ::actRTm
 *  @return       the number of characters put (should be 28)
 *                or 0: error (rTmTxT NULL)
 */
int formatTmTim(char * rTmTxt, struct tm  * rTm);

/** Format broken down real time clock+ms as standard text.
 *
 *  The format is:  Fr 2017-10-20 13:55:12.987 UTC+20
 *  ................0123456789x123456789v123456789t123
 *  +3 ................0123456789x123456789v123456789t
 *  The length is 33.
 *  See ::formatTmTim() for a shorter format without ms.
 *
 *
 *  @param rTmTxt pointer to the target text buffer,
 *                must have place for 34 characters (!)
 *  @param rTm    pointer to broken down real time; NULL will take ::actRTm
 *  @param millis milliseconds 0..999 supplement to rTm
 *  @return       the number of characters put (should be 32)
 *                or 0: error (rTmTxT NULL)
 */
int formatTmTiMs(char * rTmTxt, struct tm  * rTm, int millis);

/** Translation of directory entry typed to 8 char text.
 *
 *  direntry.d_type as index in the range 0..15 gives an 8 character short
 *  type text. Note: Only  0, 1, 2, 4, 6, 8, 10, 12 and 14 are defined
 *  d_type values. The undefined ones give undef3 .. undefF
 */
extern char const fType[16][8];

#endif /// SYSBASIC_H
