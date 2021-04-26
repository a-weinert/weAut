/** @file config_raspberry_01.h
 *
 *  Configuration settings for Raspberry Pi1
 *
 *  This file contains some platform (type) specific definitions. These
 *  settings influence the compilation and build process. Most of those
 *  settings can not be changed later at runtime.
 *
 *  Since end 2017 we mostly use Pi3.
\code
   Copyright  (c)  2019   Albrecht Weinert
   weinert-automation.de      a-weinert.de

 *     /         /      /\
 *    /         /___   /  \      |
 *    \        /____\ /____\ |  _|__
 *     \  /\  / \    /      \|   |
 *      \/  \/   \__/        \__/|_                                 \endcode

   Revision history \code
   Rev. $Revision: 238 $ $Date: 2021-03-01 14:38:58 +0100 (Mo, 01 Mrz 2021) $
   Rev. 190 12.02.2019 : minor, comments only
   Rev. 209 10.07.2019 : stdUARTpath
   Rev. 231 13.08.2020 : two digit PIN0x  and  GPIO2pin added
\endcode
 */

#ifndef CONFIG_PLATFORM_H
# define CONFIG_PLATFORM_H raspberry_01
#else
# error "more than one platform specific config_platform.h file"
#endif

// by using Joan NN's pigpio(d) we don't need those address values
//#define ARM_PERI_BASE 0x20000000
//#define ARM_GPIO_BASE 0x20000000
//#define ARM_PERI_SIZE 0x01000000

// 26 pin con|GPIO number
#define PIN3   0 // 2 on Pi3
#define PIN5   1 // 3 on Pi3
#define PIN7   4
#define PIN8  14   //TXDI
#define PIN03  0 // 2 on Pi3
#define PIN05  1 // 3 on Pi3
#define PIN07  4
#define PIN08 14   // TXDI
#define PIN10 15  //RXDI
#define PIN11 17
#define PIN12 18
#define PIN13 21 // 27 on Pi3
#define PIN15 22
#define PIN16 23
#define PIN18 24
#define PIN19 10  // MOSI | S
#define PIN21  9  // MISO | P
#define PIN22 25
#define PIN23 11  // SCLK | I
#define PIN24  8  // CE0  /
#define PIN26  7  // CE1 /
// 6 9 14 20 25 : gnd
// 1 17 : 3.3V
// 2 4  : 5V

//                 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
#define GPIO2pin   3, 5, 0, 0, 7, 0, 0, 0,24,21,19,23, 0, 0, 8,10, 0,11,12, 0,\
                   0,13,15,16,18,22, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

//                 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
#define PIN2gpio  99,93,95, 0,95, 1,90, 4,14,90,15,17,18,21,90,22,23,93,24,10,\
                  90, 9,25,11, 8,90, 7,99,99,99,99,99,99,99,99,99,99,99,99,99,\
                  99,99,99,99
//                 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15,16,17,18,19,
// 0..56(?) GPIO; 95: 5V; 93: 3.3V; 90: Earth 0V;
// 99: not existent; [1..40]: valid pin number/index; [1..26]: existing pins

/** /def stdUARTpath
 *  Pi's standard UART.
 *
 *  It is the one on the Pins 8 (GPIO14) for Tx and 10 (GPIO15) for Rx.
 */
#define stdUARTpath "/dev/ttyS0"
