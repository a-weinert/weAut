/** @file config_raspberry_03.h
 *
 *  Configuration settings for Raspberry Pi3
 *
 *  This file contains some platform (type) specific definitions. These
 *  settings influence the compilation and build process. Most of those
 *  settings can not be changed later at runtime.
 *
 *  With respect to (process) I/O pins there is no difference to Pi3b.
 *  Hence, for Pi3* define PLATFORM as raspberry_03.
\code
   Copyright  (c)  2019   Albrecht Weinert
   weinert-automation.de      a-weinert.de

 *     /         /      /\
 *    /         /___   /  \      |
 *    \        /____\ /____\ |  _|__
 *     \  /\  / \    /      \|   |
 *      \/  \/   \__/        \__/|_                                 \endcode

   Revision history \code
   Rev. $Revision: 240 $ $Date: 2021-04-10 09:45:45 +0200 (Sa, 10 Apr 2021) $
   Rev. 190 12.02.2019 : minor, comments only
   Rev. 209 10.07.2019 : stdUARTpath
   Rev. 231 13.08.2020 : two digit PIN0x  and  GPIO2pin added
\endcode
 */

#ifndef CONFIG_PLATFORM_H
# define CONFIG_PLATFORM_H raspberry_03
#else
# error "more than one platform specific config_platform.h file"
#endif

// As using Joan NN's pigpio(d) we don't need  those address values
//#define ARM_PERI_BASE 0x3F000000
//#define ARM_GPIO_BASE 0x3F200000
//#define ARM_PERI_SIZE 0x01000000

// 40 pin con|GPIO number
#define PIN3   2  // SDA1  ( 0 on Pi1)
#define PIN5   3  // SCL   ( 1 on Pi1)
#define PIN7   4  // GPCLK0
#define PIN03  2  // SDA1
#define PIN05  3  // SCL
#define PIN07  4  // GPCLK0
#define PIN08 14  // TXDI
#define PIN8  14  // TXDI
#define PIN10 15  // RXDI
#define PIN11 17
#define PIN12 18
#define PIN13 27  //       (21 on Pi1)
#define PIN15 22
#define PIN16 23
#define PIN18 24
#define PIN19 10  //  MOSI | S
#define PIN21  9  //  MISO | P
#define PIN22 25
#define PIN23 11  //  SCLK | I
#define PIN24  8  // CE0   /
#define PIN26  7  // CE1  /
#define PIN27  0  //!<  SDA0
#define PIN28  1  //!<  SCL0
#define PIN29  5
#define PIN31  6
#define PIN32 12
#define PIN33 13
#define PIN35 19
#define PIN36 16
#define PIN37 26
#define PIN38 20
#define PIN40 21
// 6 9 14 20 25 30 34 39 : gnd
// 1 17 : 3.3V
// 2 4  : 5V
// 27 28: ID_SD/SC (HAT board & EEPROM interface)  or GPIO 0 and 1

//                 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
#define GPIO2pin  27,28, 3, 5, 7,29,31,26,24,21,19,23,32,33, 8,10,36,11,12,35,\
                  38,40,15,16,18,22,37,13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

//                 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
#define PIN2gpio  99,93,95, 2,95, 3,90, 4,14,90,15,17,18,27,90,22,23,93,24,10,\
                  90, 9,25,11, 8,90, 7, 0, 1, 5,90, 6,12,13,90,19,16,26,20,90,\
                  21,99,99,99
//                 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
// 0..56(?) GPIO; 95: 5V; 93: 3.3V; 90: Earth 0V;
// 99: not existent; [1..40]: valid pin number/index

/** /def stdUARTpath
 *  Pi's standard UART.
 *
 *  It is the one on the Pins 8 (GPIO14) for Tx and 10 (GPIO15) for Rx.
 */
#define stdUARTpath "/dev/ttyS0"
