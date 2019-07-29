/**
 * \file config.h
 *
   Organising platform specific includes for the make process
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
   Rev. 150 18.06.2018 : minor, comments only
   Rev. 209 22.07.2019 : work around a Doxygen bug
\endcode

  This file contains some definitions concerning hardware configuration.
  These settings influence the compilation and building process and can't
  be changed later at runtime.
 */

#ifndef CONFIG_H
#define CONFIG_H

//--- platform specific include     -------------//-------------------------
#define __header(x) #x                          // it's a kind
#define _header(x) __header(arch/config_##x.h) // of
#define header(x) _header(x)                  // magic

#ifndef PLATFORM
#  define PLATFORM raspberry_03
#  warning "undefined PLATFORM defaulted to raspberry_03"
#endif

#include header(PLATFORM)

#ifndef PIN13
#  define PIN13 27  // PI3 (default) (Pi1 would be 21)
#endif

#endif /* CONFIG_H */
