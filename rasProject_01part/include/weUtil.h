/** @file include/weUtil.h
 *
 *  Some system related time and utility functions for Raspberry Pis

\code
   Copyright  (c)  2018   Albrecht Weinert
   weinert-automation.de      a-weinert.de

 *     /         /      /\
 *    /         /___   /  \      |
 *    \        /____\ /____\ |  _|__
 *     \  /\  / \    /      \|   |
 *      \/  \/   \__/        \__/|_                                 \endcode

   Revision history \code
   Rev. $Revision: 240 $ $Date: 2021-04-10 09:45:45 +0200 (Sa, 10 Apr 2021) $
   Rev. 50+ 16.10.2017 : cycTask_t->mutex now pointer (allows common mutex)
   Rev. 54+ 23.10.2017 : timing enhanced, common mutex forced/standard
   Rev. 200 16.04.2019 : logging improved; formatting enh.
   Rev. 201 26.04.2019 : renamed from sysUtil.h
   Rev. 202 28.04.2019 : timer macro change, more formatting functions
   Rev. 209 22.07.2019 : work around a Doxygen bug, formFixed.. not void
   Rev. 233  26.09.2020 : 10 ms cycle added
\endcode

  This file contains some definitions concerning system, time and IO.
  The IO part will work with the gpio/gpiod library as defined in
  the library's include file pigpiod_if2.h.
 */

#ifndef WEUTIL_H
#define WEUTIL_H
//  #ifndef __DOXYGEN__  omitted as Doxygen improved
#include "sysBasic.h" // stdint.h time.h stdio.h string.h (struct timespec)

#include <fcntl.h>    // O_RDWR

#include <unistd.h>   // close
#include <sys/file.h> // flock
#include <signal.h>   // signal SIGTERM SIGINT
#include <stdlib.h>   // atexit exit

#ifdef __CDT_PARSER__
# define __USE_GNU 1  // just for Eclipse CDT's wrongly handling cross platform
#endif
#include <pthread.h>  // pthread_t pthread_mutex_t pthread_cond_t
// #endif omitted as Doxygen improved

// ----------------------- formatting ---------------------------------------

#define VALUE_TO_STRING(x) #x
#define VALUE(x) VALUE_TO_STRING(x)
#define VAR_NAME_VALUE(var) #var "="  VALUE(var) //<! compile time macro output

/** Set one char sequence left justified into another one.
 *
 *  This function copies n characters from src to dest left justified.
 *  If the length of src is less than n the remaining length on right in dest
 *  will be filled with blanks.
 *
 *  Attention: dest[n-1] must be within the char array provided by dest.
 *  This cannot and will not be checked!
 *
 *  Hint: Contrary to strncpy there's no padding with 0. If you want dest to
 *  end after the insertion use ::strLappend().
 *  @param dest the pointer to / into the destination sequence
 *              where src is to be copied to. If NULL nothing happens.
 *  @param src  the sequence to be copied. If NULL or empty fill is used
 *              from start
 *  @param n    the number of characters to be copied from src.
 */
void strLinto(char * dest,  char const * src, size_t n);

/** Append one char sequence left justified at another one.
 *
 *  This function copies n characters from src to dest and lets dest then
 *  end with 0 (end of string). If n is negativ, -n characters will be copied
 *  and dest will end with new line and 0;
 *
 *  Attention: dest[n] respectively dest [-n + 1] must be within the char
 *  array provided by dest. This cannot and will not be checked!
 *  @param dest the pointer to / into the destination sequence
 *              where src is to be copied to. If Null nothing happens.
 *  @param src  the sequence to be copied. If Null or empty fill is used
 *              from start
 *  @param n    the absolute value is the number of characters to be copied
 *              from src. If this number exceeds 300 it will be taken as 0.
 *              If n is negative a line feed will be appended, too.
 */
void strLappend(char * dest,  char const * src, int n);

/** Set one char sequence right justified into another one.
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
 *  @param dest the pointer to / into the destination sequence
 *              where src is to be copied to. If NULL nothing happens.
 *  @param src  the sequence to be copied. If NULL or empty fill is used
 *              from start
 *  @param n    the number of characters to be copied from src.
 */
void strRinto(char * dest,  char const * src, size_t n);


/** Format 16 bit unsigned fixed point, right aligned.
 *
 *  formFixed16(target, 6, 1234, 2), e.g., will yield " 12.34". <br />
 *  formFixed16(target, 6,    4, 2), e.g., will yield "  0.04".
 *
 *  If the value would not fit within targetLen characters leading digits
 *  will be truncated.
 *
 *  @param target    pointer to first of targLen characters changed
 *  @param targetLen field length 2..16; number of characters changed
 *  @param value     the fixed point value
 *  @param dotPos    where the fixed point is 0..6 < targetLen
 *  @return          points to the most significant digit set
 *                   or NULL on error / no formatting
 */
char * formFixed16(char * target, uint8_t targetLen, uint16_t value,
                                                           uint8_t dotPos);

/** Format 32 bit unsigned fixed point, right aligned.
 *
 *  This function behaves like ::formFixed16() except for handling 32 bit
 *  values. ::formFixed16() should be preferred, when feasible.
 *
 *  @param target    pointer to first of targLen characters changed
 *  @param targetLen field length 2..16; number of characters changed
 *  @param value     the fixed point value
 *  @param dotPos    where the fixed point is 0..6 < targetLen
 *  @return          points to the most significant digit set
 *                   or NULL on error / no formatting
 */
char * formFixed32(char * target, uint8_t targetLen, uint32_t value,
                                                             uint8_t dotPos);

// -----------------------  parsing   ---------------------------------------

/** Check if a string is a valid IPv4 address.
 *
 *  Syntactically valid IPv4 addresses are:  0.0.0.0 .. 255.255.255.255
 *
 *  @param str The string containing the address, only; 0-terminated
 *  @return 0: no syntactically valid IPv4 address; 1: OK
 */
int isValidIp4(char const * str);

/** Character to hexadecimal.
 *
 *  Parameter values '0'..'9' return 0..9.
 *  Parameter values 'A'..'F' and 'a'..'f' return 10..15.
 *  Other values return -1.
 */
int char2hexDig(char c);

/** Parse int with checks.
 *
 *  This function expects parameter str to point to a null-terminated string.
 *  If not def is returned.
 *  If lower > upper def is returned.
 *  If the string str contains a decimal integer number n, fulfilling
 *  lower <= n <= upper n is returned, or def otherwise.
 *
 *  If the string str starts with [+|-][min|med|max] ignoring case
 *  lower respectively ((lower + upper) / 2) respectively upper is returned.
 *  A leading sign (+|-) as well as any trailing characters are ignored.
 *
 *  @param str   0-terminated string containing a decimal integer number,
 *               or one of the keywords described above
 *  @param lower lower limit
 *  @param upper upper limit
 *  @param def   default value, to be returned when str is not a pure decimal
 *               number one of the keyword starts or when the result violates
 *               the limits
 */
int parsInt(const char * str, const int lower, const int upper, const int def);

/** Long array of length 14.
 *
 *  Prepared for non thread safe use with ::parse2Long()
 */
extern long int parsResult[];

/** Parse a string of integer numbers.
 *
 *  The string optArg will be tokenised taking any occurrences of the
 *  characters " +,;" (blank, plus, comma, semicolon) as border. "Any
 *  occurrences" means two commas ",,", e.g., acting as one separator and
 *  not denoting an empty number. <br />
 *  N.b.: The string optArg will be modified (by replacing the first character
 *  of the token separators found by zero ('\0').
 *
 *  The number format accepted and parsed is decimal and hexadecimal.
 *  Hexadecimal starts with 0x or 0X. Leading zeros have no significance (in
 *  C stone age sense of being octal).
 *
 *  @param optArg the string to be passed to a number of integer numbers,
 *                passed as program parameter, e.g.
 *  @param parsResult pointer to an array of long int, minimal length 14 (!)
 *  @return the number of integer numbers parsed ab put into parsResult[],
 *          0..14
 */
unsigned int parse2Long(char * const optArg, long int * parsResult);


// ----------------------- time correction (may be obsolete in between) -----

extern int8_t vcoCorrNs; //!< external for test/debug only (don't change)


// -----------------------------  timing   ----------------------------------

/** Add two times as new structure.
 *
 * @param  t1 summand as time structure (not NULL!, will be left unchanged)
 * @param  t2 the second summand        (dto.)
 * @return the sum         (probably passed as hidden parameter by the way)
 */
timespec timeAdd(timespec const t1, timespec const t2);

/** Add two times overwriting the first operand.
 *
 *  @param  t1 the time structure to add to (not NULL!, will be modified)
 *  @param  t2 the summand            (not NULL!, will be left unchanged)
 */
void timeAddTo(timespec * t1, timespec const t2);

/** Compare two times.
 *
 *  @param  t1 the time structure to compare to t2 (not NULL!)
 *  @param  t2 the time structure to compare t1 with (not NULL!)
 *  @return 0: equal; +: t1 is greater (2 by s, 1 by ns); -: t1 is smaller
 */
int timeCmp(timespec const  t1, timespec const t2);

/** Absolute time (source) resolution.
 *
 *  This function sets the time structure provided to the absolute time's
 *  (ABS_MONOTIME default: CLOCK_MONOTONIC) resolution.
 *
 *  Raspian Jessie on a Raspberry Pi 3 always yielded 1ns, which one may
 *  believe or not. We took it as "sufficient for accurate 1ms cycles".
 *
 *  @param timeRes the time structure to be used (never NULL!)
 */
void monoTimeResol(timespec * timeRes);

/** Relative delay for the specified number of �s.
 *
 *  This is local sleep. It should not be used in combination with absolute
 *  times and cyclic threads. It is just an utility for test or very short
 *  delays (as a better replacement for spinning).
 *
 *  @param micros sleep time in �s; allowed 30 .. 63000
 *  @return sleep's return value if of interest (0: uninterrupted)
 */
int timeSleep(unsigned int micros);


//----------  date, time (local) by real time (NTP or else) clock   -----------

/** Start time (structure, monotonic real time clock).
 *
 * By ::initStartRTime() or by ::theCyclistStart() ::actRTime and this
 * ::startRTime will initially be set. ::actRTime may be updated on demand,
 * but ::startRTime should be left unchanged.
 * */
extern timespec startRTime;

/** Actual broken down time (text).
 *
 *                  |-3|-  10  -|1|- 12     -|
 *  The format is:  Fr 2017-10-20 13:55:12.987 UTC+20
 *  ................0123456789x123456789v123456789t1234
 *      was         Fr 2017-10-20 13:55:12 UTC+20
 *  The length is 32.
 */
extern char actRTmTxt[];

/** Initialise start (real) time.
 *
 *  This will be done in theCyclistStart(int). Hence, this function is for
 *  "non-cyclic" applications, mainly.
 *  Nevertheless it can be called before theCyclistStart(int) and won't be
 *  repeated therein.
 */
void initStartRTime();

/** Advance broken down real time by seconds.
 *
 *  This function just advances the broken down (local) time structure rTm and
 *  the fitting text rTmTxt by 1 to 40s. All fields not affected by adding
 *  to the seconds part, won't be touched.
 *
 *  This function won't care about leap seconds nor handle DST rules. If this
 *  is to be kept up to date, it is recommended to refresh it on every hour
 *  change (return >=3) by <br />
 *  clock_gettime(CLOCK_REALTIME,..) and localtime_r(..). <br />
 *  Depending on OS, that might be an expensive operation with extra locks.
 *
 *  With wrong parameter values this function does nothing (returns 0).
 *  @param rTm    pointer to broken down real time
 *  @param rTmTxt date text,
 *                length 32, format Fr 2017-10-20 13:55:12.987 UTC+20
 *                NULL is substituted by actRTmTxt
 *  @param sec    1..40  will be added; else: error
 *  @return       0: error (rTm NULL e.g.); 1: seconds changed; 2: minute;
 *                3: hour; 4: day; 5: month ; 6: year; 7: zone offset
 */
int advanceTmTim(struct tm * rTm, char * rTmTxt, uint8_t sec);


// --------------------------------  IO support --------------------------

extern const uint32_t csBit[]; //!< single bit set. 1 2 4 8 ... 0x80000000

/** Fetch a clear and set select bit for a GPIO pin.
 *
 *  For the masks to set or clear GPIO bits each bit 0..31 selects the
 *  GPIO pin 0..31 respectively 32..53.
 *  @param pin GPIO pin number (only 5 bits relevant here)
 *  @return the the function select bit (a value with one bit set)
 */
uint32_t ioSetClrSelect(uint8_t pin);

extern char const bin8digs[256][10]; //!< "0000_0000" .. "1111_1111"


/** Log error text (on errLog) with system error text appended.
 *
 *  Gives a (English) clear text translation of the latest system stored error.
 *  If txt is not null it will be prepended. <br />
 *  This function appends a linefeed and flushes errLog.
 *  @param txt text to be prepended (should nod be longer than 58 characters)
 */
void logErrWithText(char const * txt);

/** The current time as text.
 *  /code
 *  The format is:  2017-10-20 13:55:12.987 UTC+20
 *  ................0123456789x123456789v123456789 /endcode
 *  The length is 30. <br />
 *  Do NOT change the value provided by this pointer.
 */
extern char const * const stmp23;

/** The real time epoch seconds.
 *
 *  Do NOT change the value provided by this pointer.
 */
extern uint32_t const * const stmpSec;

/** Common error text.
 *
 *  This text is set by genErrWithText() and hence indirectly by (many)
 *  other functions optionally generating error texts.
 */
extern char errorText[182];

/** Generate error text (errorText) with system error text appended.
 *
 *  Gives a (English) clear text translation of the latest system stored error.
 *  If txt is not null it will be prepended. <br />
 *  Date and time will be prepended anyway.
 *
 *  @param txt text to be prepended (should nod be longer than 58 characters)
 *  @return 0: no error; else mutex error (time and date may be spoiled)
 */
int genErrWithText(char const * txt);

/** Log the (last) common error text generated.
 *
 *  This function outputs the last generated ::errorText (by ::genErrWithText()
 *  e.g.) to ::errLog. It appends a linefeed and flushes ::errLog.
 */
void logErrorText(void);

/** Log an error text on errLog.
 *
 *  If txt is not null it will be output to errLog and errLog will be flushed.
 *
 *  @param txt text to be output; n.b not LF appended
 */
void logErrText(char const * txt);

/** Log an event or a message on outLog as line with time stamp.
 *
 *  If txt is not null it will be output to outLog. A time stamp is prepended
 *  and a line feed is appended. txt will be shortened to 50 characters if
 *  longer.
 *
 *  @param txt the text to be output
 */
void logStampedText(char const * txt);


// ------------------------------------  signalling and exiting  ------------

/** On signal exit.
 *
 *  This function is intended as signal hook; see signal(s, hook).
 *  When called, this function calls exit(s) and never returns.
 *
 *  @param s the signal forwarded to exit
 */
void onSignalExit(int s);

/** On signal exit 0
 *
 *  This function is intended as signal hook; see signal(s, hook). <br />
 *  When called this function calls exit(0) and never returns. <br />
 *  This may be used as hook for s==SIGIN, to provide a normal
 *  return on cntl-C. <br />
 *
 *  @param s ignored
 */
void onSignalExit0(int s);


/** Common boolean run flag for all threads.
 *
 *  When set false, all threads must exit as soon as possible.
 *  On any case, a thread has to exit and clean up on next signal.
 *  Setting commonRun false implies the end of the application/program
 *  and all of its threads as soon as possible.
 *
 *  Initialised as 1 (true)
 *  Set 0 by onSignalStop() (or application program)
 */
extern volatile uint8_t commonRun;

/** Storage for the signal (number) requesting exit.
 *
 *  Set by: ::onSignalStop() <br />
 *  See also: ::retCode
 */
extern volatile int sigRec;

/** On signal stop.
 *
 *  This function is a prepared signal hook. When called it sets sigRec by
 *  s and clears commonRun.
 */
void onSignalStop(int s);


//------------------  cyclic task / thread support -------------------------

/** Event data for cyclic tasks.
 *
 *  This structure holds data &nbsp;&mdash; mainly time and date by diverse
 *  clocks and cycle counters &mdash;&nbsp; to be used by cyclic tasks. It
 *  will be provided as as ::cycTask_t.cycTaskEventData.
 */
typedef struct {

/** The real time epoch seconds.
 *
 *  The value may jump with coarse NTP corrections. And it will be not precise
 *  and or jump with leap seconds, which can't be handled in UTC.
 */
   uint32_t realSec;

/** The broken down calculated local start time.
 *
 *  This is the cycle start / event local time. <br />
 *  It is kept updated by the cyclist (when having been started; see
 *  ::theCyclistStart).
 */
   struct tm  cycStartRTm;
   int hourOffs; //!< actual time zone offset in hours (incl. DST +: east)
   char  rTmTxt[34];  //!< local time as text

   // monotonic time
   timespec cycStart;         //!< monotonic start time of the cycle

   // below seconds times and counters are valid for both (most of the time)
   int cycStartMillis;     //!< millisecond (0..999) missing in struct tm

/** A ms counter for cycles and tasks.
 *
 *  This ms counter may be used to time stamp IO values and other events in a
 *  unique and sortable manner. <br />
 *  It is 1 in all first cycles (signalled as event) when the cyclist is
 *  started without delay. Otherwise the first value is this delay. The value
 *  0 will will correspond to ::allCycStart.
 *
 *  Usually the lower 32 bits (cast (uint32_t)) should be sufficient. The
 *  lower 32 bit will wrap after 49.7 days. <br />
 *  For an absolute stamp in seconds resolution see
 *  ::cycTaskEventData_t.realSec and
 */
   uint64_t cnt1ms;

   uint8_t msTo100Cnt;     //!< 0..99; at 0 we will have a 100ms event
   uint8_t cnt10inSec;     //!< 0..9; counts 100ms events in the second
   uint8_t cnt210sec;      //!< 0..209 s counter (to provide n s periods)
} cycTaskEventData_t;

/** Cyclic or event driven task / threads structure.
 *
 *  This structure supports the organisation of tasks respectively threads
 *  to work all on a same event type. One common case is the event being a
 *  next time interval, like the next 100ms entered, and, as thread e.g.,
 *  the process control tasks to work on the 100ms cycle.
 *
 *  Such approach involves two types of threads: <br />
 *  One controller/manager determining the event, recording it by increasing
 *  the event counter and signalling all worker threads. <br />
 *  Zero to some worker threads doing work on every or every other etc. event,
 *  usually by holding and updating an event counter value at which to do the
 *  work.
 *
 *  The main purpose are absolute time driven cyclic tasks as usual in
 *  industrial PLCs. <br />
 *  For the standard cycles provided here, 1ms, 10ms .. 100ms, 1s the handler
 *  thread is provided as singleton doing other time and date related jobs
 *  for all; see ::theCyclistStart(), ::theCyclistWaitEnd() and
 *  ::endCyclist().
 *
 *  See ::cyc1ms, ::cyc10ms, ::cyc20ms, ::cyc100ms, ::cyc1sec <br />
 *  See also ::have1msCyc, ::have10msCyc ... ::have1secCyc
 */
typedef struct {
   pthread_cond_t cond;  //!< the event occurred condition
   uint32_t count;      //!< the event counter (modified by manager, only)
   timespec  stamp;  //!< absolute / monotonic event stamp (dto.)

/** Event data for cyclic and other event types.
 *
 *  This union allows for different event data types for different types
 *  of events like cyclic ticks, or any other event types. <br />
 *  Anyway the information must be copied by the event controller / manager
 *  under mutex lock to the event handler's (i.e. this) task structure.
 */
   union { //!< allow different event data for cyclic and other event types

     cycTaskEventData_t cycTaskEventData; //!< cyclic event data

      // void * eventDataBuf; //!< just a pointer for all else

   }; // event type data union
} cycTask_t;


/** Initialise a cyclic task / threads structure.
 *
 *  This function initialises a cyclic or non cyclic (asynchronous random
 *  event driven) task (thread) structure. Common mutex and an own condition
 *  are initialised, the event counter (.count) is set to 0.
 *
 *  Note: For the standard cycles provided here, 1ms, 100ms .., this
 *  initialisation is done in ::theCyclistStart() and the destruction
 *  (by ::cycTaskDestroy()) in ::endCyclist().
 *
 *  @param cykTask the task structure to initialise (not NULL!)
 *  @return 0: success, else: one of the error codes occurred
 */
int cycTaskInit(cycTask_t * cykTask);

/** Destroy a cyclic task / threads structure.
 *
 *  @param cykTask the task structure to destroy (not NULL!)
 *  @return 0: success, else: one of the error codes occurred
 */
int cycTaskDestroy(cycTask_t * cykTask);

/** Handle and signal events.
 *
 *  This is a helper function for the controller / manager to be called
 *  when having determined, that one or more events happened.
 *
 *  @param cycTask  the task structure (not NULL!)
 *  @param noEvents number of events (usually 1); summand to cykTask.count
 *  @param stamp    absolute monotonic time of the event; sets sykTask.stamp
 *  @param cycTaskEventData  actual cyclic event data
 *  @return 0: success, else: one of the error codes occurred
 */
int cycTaskEvent(cycTask_t * cycTask, uint8_t noEvents, timespec stamp,
                                         cycTaskEventData_t cycTaskEventData);

/** Wait on signalled event.
 *
 *  This is a helper function for a worker thread.
 *  It will return on reaching the signalled event(s) or on ! commonRun.
 *  If cykTaskSnap is not NULL cycTask will be assigned to it under mutex
 *  lock before returning. This is helpful if cykTask's events are broadcast
 *  to multiple handlers.
 *
 *  @param cycTask         the task structure (not NULL!)
 *  @param eventsThreshold threshold for cykTask.count (update for every round)
 *  @param cycTaskSnap     copy of cykTask under mutex lock before returning
 *  @return 0: success, else: one of the error codes occurred
 */
int cycTaskWaitEvent(cycTask_t * cycTask, uint32_t eventsThreshold,
                                                     cycTask_t * cycTaskSnap);

/** Get a cycle's/task's current event counter.
 *
 *  This is done under (cyclist's) mutex lock.
 *
 *  @param   cycTask         the task structure
 *  @return  cycTask's event counter value (.count) got under lock;
 *           0x7FffFFffFFf7  on any error (null, lock error) situation
 */
uint32_t getCykTaskCount(cycTask_t const * const cycTask);

/** Common absolute / monotonic start time of all cycles.
 *
 *  May be considered as program's start time when cycles are started
 *  early by theCyclist. Normally not to be modified.
 */
extern timespec allCycStart;

extern cycTask_t cyc1ms;     //!< 1ms cycle (data structure)
extern cycTask_t cyc10ms;    //!< 10ms cycle (data structure)
extern cycTask_t cyc20ms;    //!< 20ms cycle (data structure)
extern cycTask_t cyc100ms;   //!< 100ms cycle (data structure)
extern cycTask_t cyc1sec;    //!< 1s cycle (data structure)

/** Flag to enable the 1ms cycle.
 *
 *  As a rule no more than two oft the cycles offered &mdash;
 *  ::cyc1ms, ::cyc10ms, ::cyc20ms, ::cyc100ms, ::cyc1sec &mdash; shall be
 *  enabled. This is no restriction as a faster cycle can easily (and often
 *  should) implement slower cycles by sub-division.
 *
 *  The default setting is 1ms and 100ms ON and all others OFF. <br />
 *  If other settings are used the flags should be set at the program's early
 *  initialisation phase and afterwards left untouched.
 *
 *  default: ON
 *  @see cycTask_t cyc1ms
 */
extern uint8_t have1msCyc;

/** Flag to enable the 10ms cycle.
 *
 *  default: OFF
 *  @see have1msCyc cycTask_t cyc10ms
 */
extern uint8_t have10msCyc;


/** Flag to enable the 20ms cycle.
 *
 *  default: OFF
 *  @see have1msCyc cycTask_t cyc20ms
 */
extern uint8_t have20msCyc;

/** Flag to enable the 100ms cycle.
 *
 *  default: ON
 *  @see have1msCyc cycTask_t cyc100ms
 */
extern uint8_t have100msCyc;

/** Flag to enable the 1s cycle
 *
 *  default: OFF
 *  @see have1msCyc cycTask_t cyc1s
 */
extern uint8_t have1secCyc;

// Note: hide instead of commanding r/o by comment
//extern cycTaskEventData_t cycTaskMED; //!< cyclic tasks master data (r/o!)

/** Get a (stop-watch) ms reading.
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
uint16_t stopMSwatch(); // replaces  extern cycTaskEventData_t cycTaskMED;

/** Get a ms in s reading.
 *
 *  This function provides the cyclist's ms in sec as 16 bit unsigned reading.
 *  It is intended for measuring and testing durations.
 *
 *  Hint: This functions does nothing for thread safety. It hopes 16 bit
 *  accesses being atomic. Even if no problems in this respect were observed on
 *  Raspberry Pi 3s, it may be just hope in the end. Thread-safe values are,
 *  of course, provided in the ::cycTaskEventData_t structure. But those are
 *  frozen within one cycle task step, and, hence, not usable as stop-watch
 *  readings within such step.
 */
uint16_t getMSinS();

/** Get a 10th of second in s reading.
 *
 *  This function provides the cyclist's tenth in seconds reading (0..9).
 *  It is intended for cyclic tasks with times greater than 100 ms or
 *  asynchronous tasks to get a coarse second sub-division.
 *
 *  Hint: Cyclic tasks get this value in their task date valid at start. This
 *  function provides an actual value for tasks running longer than 100ms.
 */
uint8_t get10inS();

/** Get the absolute s reading.
 *
 *  This function provides a 32 bit monotonic seconds value. <br />
 *  Base of this 32 bit value is the cyclist's 64 bit epoch time in seconds,
 *  0 being 1.1.1970 00:00:00 UTC on almost all Linuxes and C libraries.
 *
 *  This unsigned 32 bit holds until 7. February 2106, which is far longer than
 *  the projected lifetime age of this library and of Raspberry Pi3s. (But who
 *  knows?) The value may be used for seconds-resolution, absolute (i.e. zone
 *  and DST independent) time-stamps and interval calculations (which will be
 *  incorrect with leap seconds).
 *
 *  Hint: Cyclic tasks get this value (.realSec) in their task date valid at
 *  tick start. Hence, this function is intended for asynchronous tasks or
 *  cyclic tasks with periods > 1s.
 *
 *  Since version R.110 we dare to fetch this value without lock, assuming
 *  ARMv7 32 bit load and stores being atomic.
 */
uint32_t getAbsS();


/** Start the cycles handler.
 *
 *  This function initialises and then runs the predefined cycles
 *  cycles (as of Sept. 2020: 1ms, 10ms, 20ms, 100ms and 1s; see ::have1msCyc)
 *  when enabled.
 *
 *  Besides the absolute / monotonic times for the cycles it also
 *  initialises real time and timers handling.
 *
 *  Timers and cycles are run in an extra thread made by this function.
 *  And to be precise, the cycles are not run here; instead, cyclic
 *  events are generated and broadcast.
 *
 *  As the thread started by this function also provides monotonic and
 *  civil times and stamps it should be started with the program (i.e.
 *  earliest in main()). Preparation time before the cycles should start
 *  can be handled by the delay parameter.
 *
 *  As of September 2020 five cycles (see above) are defined and handled.
 *  It is strongly recommended not to use more than two of them and implement
 *  other cycles with multiple periods by sub-division. That means, e.g.,
 *  do not enable the 20ms cycle when having the 10ms one.
 *
 *  @param startMsDelay number of ms before generating the first
 *                      cyclic event; allowed range 12 .. 1200; default 1
 *  @return 0: after having initialised all and having made and started the
 *              cyclist thread; other values signal errors
 */
int theCyclistStart(int startMsDelay);

/** Wait for the end of the cycles thread.
 *
 *  This function does so by unconditionally joining the cyclist thread.
 *  @return the return value of thread join; 0: join OK
 * */
int theCyclistWaitEnd();

#ifdef COMPILE4DEDUG
extern int msReal; //!< OS ms (0..999) at calc.d minute change may drift from msCalc
extern long absNanos1ms; //!< extern for debug TEST outputs only
#endif

/** The cycles handler arrived.
 *
 *  This function cleans up after theCyclist. It should be called after
 *  theCyclist() ending successfully on commonRun false. The controller
 *  thread shall call this function after having joined and cleaned up all
 *  of its threads. It may also be put in an exit hook.
 *
 *  @return 0: OK; else: a cycTaskDestroy() error
 */
int endCyclist(void);

#endif // WEUTIL_H
