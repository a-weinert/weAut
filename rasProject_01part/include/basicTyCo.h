/** @file include/basicTyCo.h
 *
 *  Basic types and constants
 *
 *  This file contains some basic type definitions and values, i.e. macro
 *  constants. It does not contain function definitions and, hence, has no
 *  implementing .c file.
 *
\code
   Copyright  (c)  2018   Albrecht Weinert
   weinert-automation.de      a-weinert.de

 *     /         /      /\
 *    /         /___   /  \      |
 *    \        /____\ /____\ |  _|__
 *     \  /\  / \    /      \|   |
 *      \/  \/   \__/        \__/|_                                 \endcode

   Revision history \code
   Rev. $Revision: 236 $ $Date: 2021-02-02 18:11:02 +0100 (Di, 02 Feb 2021) $
   Rev. 152 21.06.2018 : new, excerpted from sysBasic.h V.151
   Rev. 182 12.08.2018 : types reduced
   Rev. 190 12.02.2019 : minor, comments only
   Rev. 209 13.07.2019 : include stdint.h only (string.h no more)
   Rev. 215 26.08.2019 : type sdm124regs_t added
\endcode
 */

#ifndef BASICTYCO_H
#define BASICTYCO_H
#include <stdint.h>   // uint32_t


// ------------------ platform property endianess  -------------------------

//  try to evaluate target platform's endianness at compile time (Raspberry
//  should be little) and set the macro PLATFlittlE [sic!] accordingly.
//  If none of the descriptive macros are available we resort (without
//  warning) to runtime evaluation (by littleEndian())

/** \def PLATFlittlE
 *  The target platform is little endian.
 *
 *  values: 0 : no (known at compile time); 1 : yes (dto.);
 *          littleEndian() :  compute at runtime as no commonly used
 *                            target information macros are available
 */
#if defined(__BYTE_ORDER) && __BYTE_ORDER == __BIG_ENDIAN || \
    defined(__BIG_ENDIAN__) || \
    defined(__ARMEB__) || \
    defined(__THUMBEB__) || \
    defined(__AARCH64EB__) || \
    defined(_MIBSEB) || defined(__MIBSEB) || defined(__MIBSEB__)
#   define PLATFlittlE 0  // it's a big-endian target architecture
#elif defined(__BYTE_ORDER) && __BYTE_ORDER == __LITTLE_ENDIAN || \
    defined(__LITTLE_ENDIAN__) || \
    defined(__ARMEL__) || \
    defined(__THUMBEL__) || \
    defined(__AARCH64EL__) || \
    defined(_MIPSEL) || defined(__MIPSEL) || defined(__MIPSEL__)
#   define PLATFlittlE 1  // we have a little-endian target architecture
#else
#   define PLATFlittlE littleEndian()
#endif


// --------------------------- constants -----------------------------------

/** \def ON
 *  true On  An marche go.
 *
 *  value: 1
 */
#define ON 1

/** \def OFF
 *  false Off Aus arr&ecirc;t stop halt.
 *
 *  value: 0
 */
#define OFF   0

/** \def TRUE
 *  true  on  an marche go.
 *
 *  value: 1
 */
#define TRUE  1

/** \def FALSE
 *  false off aus arr&ecirc;t stop halt.
 *
 *  value: 0
 */
#define FALSE 0

/** \def MILLIARD
 *  MILLIARD 1/nano = Giga = 10**9
 *
 *  The constant Milliard. (Amercians, wrongly, call that Billion.)
 *
 *  value: 1000000000
 */
#define MILLIARD 1000000000
//               lOOOoooOOO

/** \def MILLION
 *  Million 1/µ = 1/micro = Mega = 10**6
 *
 *  value: 1000000
 */
#define MILLION     1000000

/** \def YEAR
 *  Days in normal year
 *
 *  value: 365
 */
#define YEAR 365
#define LEAPYEAR 366 //!< days in leap year
#define FOURYEARS 1461 //!< days in four years (3 * ::YEAR + ::LEAPYEAR)

#define MINUTEs 60  //!< seconds in minutes
#define HOURs 3600  //!< seconds in hours
#define DAYs 86400  //!< seconds in days w/o DST switch or leap seconds

/** \def ERAend
 *  A point in time far away.
 *
 *  This is 2.2.2106 in Unix seconds and very near the end of the unsigned
 *  32 bit era. In the sense of small embedded control applications we
 *  consider this (for tasks timers etc.) as beyond end of life and, hence,
 *  infinity. <br />
 *  value: 4294512000U
 */
#define ERAend 4294512000U


// --------------------------- macros as functions -------------------------

/** \def clearArray
 * Clear an array.
 *
 *  This is to set a real fixed size array to zero (0). Using this instead
 *  of looping or own optimising gets better code on almost all gcc compilers.
 *  Note: "Real" array means an array declared and defined with a fixed length;
 *        and no malloced pointer.
 *
 *  @param a the array to be set all 0 (not NULL, fixed length array)
 */
#define clearArray(a) memset(&(a), 0, sizeof(a))


//------------------  some types     ---------------------------------------

/** A 32 bit union.
 *
 *  This structure serves formatting and endianess plumbing purposes.
 *
 *  Besides that some Modbus devices use two (dual) so called registers of the
 *  standard Modbus 16bit size for one float. Some Modbus manufactures call
 *  such dual register a "parameter".
 *
 *  As modbus has no data types except for the 16 bit register (with whatever
 *  semantic) libmodbus will handle the endianess for that two byte registers
 *  but can do nothing for bigger data types. Many modbus servers will use
 *  32 bit and longer types. EASTRON smart meters handle all measurements as
 *  32 bit floats and call that type "parameter". Hence 1 parameter is 2
 *  registers in default big endian register ordering.
 *
 *  The union dualReg_t allows all endian repairs for such "parameter" type.
 */
typedef union {
   float  f;
   uint32_t i;
   uint16_t regs[2];
   uint8_t b[4];
} dualReg_t;


/** A type for 80 registers respectively 40 values of 32 bit.
 *
 *  Modbus RS484 has a very restricted maximum telegram length of 256,
 *  allowing for 252 data bytes, respectively 248 value bytes or 124 registers
 *  in say FC4 (read input registers).
 *  EASTRON smart meters restrict this further to 80 registers respectively
 *  a maximum of 40 float values, called parameters.
 */
typedef union {
   dualReg_t dRegs[40];
   float f[40];
   uint32_t i[40];
   uint16_t regs[80];
   uint8_t b[160];
} sdm80regs_t;

/** A type for 124 registers respectively 62 values of 32 bit.
 *
 *  Modbus RS484 has a very restricted maximum telegram length of 256,
 *  allowing for 252 data bytes, respectively 248 value bytes or 124 registers
 *  in say FC4 (read input registers).
 */
typedef union {
   dualReg_t dRegs[62];
   float f[62];
   uint32_t i[62];
   uint16_t regs[124];
   uint8_t b[248];
} sdm124regs_t;



//--------------------------   Serial link states and types

/** A set of possible states of a Modbus link.
 *
 *  Modbus link here means a connection to a concrete Modbus
 *  slave/server seen by the master/client.
 *
 *  Note: The numbering may change in future but the ordering
 *      off < operational < operated < error
 *  will not.
 *
 *  The set of states is limited by the interface type (RS485, Ethernet, ..)
 *  and may be further limited by the device or application. The subset
 *  ML_OFF ML_ON ML_INITED ML_ERR_REQ ML_ERR_RESP
 *  will be enough for some RS485 slaves.
 */
typedef enum modBusLinkState_t {
   ML_OFF = 0,       //!< do not use that Modbus device
   ML_ON = 1,        //!< may be used but connection not ready
   ML_IDLE = 2,      //!< may be usable, basic state

   ML_INITED  = 12,   //!< initialised and settings (if any)
   ML_REQSEND = 13,   //!< request sent, response pending
   ML_RESPREC = 14,   //!< response received --> ML_INITED

   ML_LISTEN = 22,   //!< listening
   ML_REQREC = 23,   //!< request received
   ML_RESPOND = 24,  //!< respond sent

   ML_ERR_ANY  = 32, //!< no concrete error, lower bound of all error states
   ML_ERR_INIT = 33, //!< initialisation error (hopeless when re-occurring)
   ML_ERR_REQ  = 34, //!< request error
   ML_ERR_RESP = 35, //!< response error
} modBusLinkState_t;


/** A structure for SMDx30 smart meters.
 *
 *  RS458 communication and state related data
 *  plus one set of 40 input data (two 16 bit MOdbus registers as one float,
 *  also called "parameter" by meter's manufacturer).
 */
typedef struct {
   char name[10]; //!< short meter name (8 characters max., 6..8 recommended)
   char title[32]; //!< meter explanation name (30 characters max.)
   char naPh[3][6]; //!> short phase i name (max. 5, 2 or 3 recommended; L1 e.g.)
   char tiPh[3][32]; //!> phase i title (max. 30; line1 e.g. or battery/heater)
   int slave; //!< Modbus slave number 1..247; 0: all undefined
   modBusLinkState_t linkState; //!< state of the (slave's) communication link
   uint16_t errorCount; //!< for the application to handle recurring errors
   int lastRetCode; //!< for the application to keep last return/error value
   sdm80regs_t sdm80regs;
} smdX30modbus_t;


//------------------  memory barrier ---------------------------------------

/** \def memBarrier
 *  Memory barrier.
 *
 *  This macro is an ARM memory fence instruction insuring cache updates.
 *
 *  Memory mapped IO, as used in Raspberries' ARM µPs BCM2835, BCM2835 and
 *  BCM2836 is quite problematic. As the semantics of memory is (non regarding
 *  caches and multiprocessing) extremely simple both compilers and processors
 *  may optimise memory access by all kinds of re-ordering and dropping.
 *
 *  A variable is an abstractions of memory cell respectively a pointer or
 *  reference is an abstraction of a memory cell address. If such variable
 *  points neither to nor is a memory cell but an IO register (say a USART
 *  buffer FIFO e.g.) this optimising by re-ordering or dropping would be
 *  disastrous. This has to be inhibited at compile time as well as at run
 *  time. Indeed, at runtime too as those newer µPs are clever enough to
 *  re-order and drop memory accesses by them self.
 *
 *  Compile time memory access optimisation is inhibited by the volatile
 *  keyword for every variable and reference meaning an IO register. Just
 *  one omission is good for effects hard to diagnose.
 *
 *  Run time optimisation and especially re-ordering is inhibited by putting
 *  memory fence instructions (this one) at the right places. The BCM2835
 *  data sheet says:
 *  "Accesses to the same peripheral will always arrive and return in-order.
 *  It is only when switching from one peripheral to another that data can
 *  arrive out-of-order. The simplest way to make sure that data is processed
 *  in-order is to place a memory barrier instruction at critical positions
 *  in the code. You should place:
 *   *  A memory write barrier before the first write to a peripheral.
 *   *  A memory read barrier after the last read of a peripheral.
 *  It is not required to put a memory barrier instruction after each read or
 *  write access. Only at those places in the code where it is possible that
 *  a peripheral read or write may be followed by a read or write of a
 *  different peripheral. This is normally at the entry and exit points of
 *  the peripheral service code.
 *
 *  As interrupts can appear anywhere in the code so you should safeguard
 *  those. If an interrupt routine reads from a peripheral the routine
 *  should start with a memory read barrier. If an interrupt routine writes
 *  to a peripheral the routine should end with a memory write barrier."
 *
 *  That's quite clear and applies to BCM2836 and BCM2837, too (we have no
 *  data sheet for those, only for BCM2835).
 *  And to repeat: Too many volatiles and memory fences makes programs
 *  longer and slower. But one too less is good for disaster.
 *
 *  Language hint: In Java this would be volatile, transient and synchronised.
 *  Implementation hint: This macro is   __sync_synchronize()
 *  Interrupt hint: We and most of us do not make a sequence of IO accesses
 *  atomic by interrupt disable. We rely on interrupt routines doing it right
 *  as described in the data sheet.
 */
#define memBarrier() __sync_synchronize()

#endif // BASICTYCO_H
