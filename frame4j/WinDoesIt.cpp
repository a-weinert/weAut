/*  Copyright 2009 Albrecht Weinert, Bochum, Germany (a-weinert.de)
 *  All rights reserved.
 *  
 *  This file is part of Frame4J 
 *  ( frame4j.de  https://weinert-automation.de/software/frame4j/ )
 * 
 *  Frame4J is made available under the terms of the 
 *  Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/  or as text in
 https://weinert-automation.de/java/docs/frame4j/de/frame4j/doc-files/epl.txt
 *  within the source distribution 
 */

/*  JNI C++ Implementation for de.frame4j.io.WinDoesIt.java
 *
 *  This source was made / is kept compatible with the javaH-generated
 *  de.frame4j.io.WinDoesIt .
 * 
 *  The making as 32 bit .dll (as of 2009): <pre><code>

javah -o de_frame4j_io_WinDoesIt.h de.frame4j.io.WinDoesIt
set pushPATH=%PATH%
PATH C:\util\BCC55\bin\;%PATH%
bcc32.exe  -tWM -tWD  WinDoesIt.cpp
PATH %pushPATH%
copy /Y  WinDoesIt.dll  C:\util\jdk\jre\bin\bsDoesItNative.dll
</code></pre>
 *  Hints: Change exemplary directories accordingly. <br />
 *  If the first step changes the generated .h file stop and adapt the .cpp.
 *  
 *  Precondition for bcc32's correct working are two  .ini files:
 *  A) bcc32.cfg : <pre><code>
-w
-I"C:\util\jdk\include"
-I"C:\util\jdk\include\win32"
-I"C:\util\BCC55\Include"
-L"C:\util\BCC55\lib;C:\Programme\BCC55\lib\psdk"

 * B) ilink32.cfg:

-x
-L"C:\util\BCC55\lib;C:\util\BCC55\lib\psdk"
</code></pre>
 *  The making of as 64 bit .dll (as of 2013): <pre><code>
 
javah -o de_frame4j_io_WinDoesIt.h de.frame4j.io.WinDoesIt
C:\util\mingw64\bin\gcc.exe -Wl,--add-stdcall-alias -I"%JAVA_HOME%\include"  -I"%JAVA_HOME%\include\win32" -shared -o WinDoesIt.dll  WinDoesIt.cpp
copy /Y  WinDoesIt.dll  %JAVA_HOME%\bin\bsDoesItNative.dll
</code></pre>
 
 *  Copyright 2009, 2013, 2015 Albrecht Weinert  <a-weinert.de>
 *  @author   Albrecht Weinert
 *  @version  $Revision: 32 $ ($Date: 2021-03-22 18:35:41 +0100 (Mo, 22 Mrz 2021) $)
 */
 //    V.118+ (23.02.2015 16:35) : moved  de.a-weinert.io -> de.frame4j.io

#include <stdio.h>
#include <stdarg.h>
#include <jni.h>
#include <stdio.h>
#include <windows.h>
#include <wtypes.h>

#include "de_frame4j_io_WinDoesIt.h"

/* Save error numbers for later evaluation.
 * Only calling GetLastError() immediately after any failure may yield the
 * correct value. Hence we do it in this lib  and save it for later retrieval
 * by the user software.
 */ 
   jint cacheLastError;


/* Class:     de.frame4j.io.WinDoesIt
 * Method:    openSerialPort
 * Signature: (Ljava/lang/String;)J
 */
   JNIEXPORT jlong JNICALL Java_de_frame4j_io_WinDoesIt_openSerialPort
                                      (JNIEnv *env, jclass, jstring comPort){
	   HANDLE hCom = INVALID_HANDLE_VALUE;
	   cacheLastError = 0;
	   const char * buffer = env->GetStringUTFChars(comPort, NULL);
	   if (buffer == NULL) goto ret;
	   hCom = CreateFile(buffer, GENERIC_READ | GENERIC_WRITE,
			   0,    // comm devices must be opened with exclusive-access
			   NULL, // no security attributes
			   OPEN_EXISTING, // comm devices must (?) use OPEN_EXISTING
			   0,    // not overlapped I/O
			   NULL  // hTemplate must be NULL for comm devices
	   );
	   if (hCom == INVALID_HANDLE_VALUE) {
		   cacheLastError = (jint) GetLastError();	
	   } 
	   env->ReleaseStringUTFChars(comPort, buffer);
	   ret:  return (jlong)hCom;
   } // Java_de_frame4j_io_WinDoesIt_openSerialPort
  

/* Class:     de_frame4j_io_WinDoesIt
 * Method:    getLastError
 * Signature: ()I
 */
   JNIEXPORT jint JNICALL Java_de_frame4j_io_WinDoesIt_getLastError
                                                          (JNIEnv *, jclass) {
      jint retVal =  (jint) GetLastError();  
      if (retVal == 0) {
         retVal = cacheLastError;
      }   
      cacheLastError = 0;
      return retVal;     
   } // Java_de_frame4j_io_WinDoesIt_getLastError


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    errorMessage
 * Signature: (I)Ljava/lang/String;
 */
   JNIEXPORT jstring JNICALL Java_de_frame4j_io_WinDoesIt_errorMessage
                                           (JNIEnv * env, jclass, jint errNo){
      if (!errNo) return NULL;
      const char * lpMsgBuf;
      //LPVOID lpMsgBuf;
      FormatMessage( FORMAT_MESSAGE_ALLOCATE_BUFFER 
                       | FORMAT_MESSAGE_FROM_SYSTEM
                       | FORMAT_MESSAGE_IGNORE_INSERTS,
                     NULL,  errNo,
                     MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
                     (LPTSTR) &lpMsgBuf, 0, NULL );
      jstring retVal = env->NewStringUTF(lpMsgBuf);
      // LocalFree(lpMsgBuf);
      return retVal;
   } // Java_de_frame4j_io_WinDoesIt_errorMessage


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    setSerialParams
 * Signature: (JIIIII)Z
 */
  JNIEXPORT jboolean JNICALL 
    Java_de_frame4j_io_WinDoesIt_setSerialParams
                    (JNIEnv*, jclass, jlong port, jint baud, jint dataBits,
                               jint stopBits, jint parity, jint flowControl){
   
      if ((void*)port == INVALID_HANDLE_VALUE) return JNI_FALSE;
      DCB dcb;
      if (! GetCommState((HANDLE)port, &dcb)) {
         cacheLastError = (jint) GetLastError();	
         return JNI_FALSE;
      }
      cacheLastError = 0;
      int anyChange = 0;
      if (baud != -1  && (DWORD)baud != dcb.BaudRate) {
         dcb.BaudRate = baud;
         anyChange = 1;
      }
      if (stopBits != -1 && (unsigned char)stopBits != dcb.StopBits) {
         dcb.StopBits = (unsigned char) stopBits;
         anyChange = 1;
      }
      if (dataBits != -1) {
         if (dataBits < 5 || dataBits > 8) dataBits = 8; 
         if (dataBits != dcb.ByteSize) {
            dcb.ByteSize = (unsigned char)dataBits;
            anyChange = 1;
         }
      }
      if (parity != -1 && parity != dcb.Parity) {
         dcb.Parity = (unsigned char)parity;
         anyChange = 1;
      }
      if (flowControl == 0) { // set to default: no flow control
		 	dcb.fOutxCtsFlow = FALSE;
			dcb.fOutxDsrFlow = FALSE;
			dcb.fDtrControl = DTR_CONTROL_DISABLE;
			dcb.fDsrSensitivity = FALSE;
			dcb.fTXContinueOnXoff = FALSE;
			dcb.fOutX = FALSE;
			dcb.fInX = FALSE;
			dcb.fRtsControl = RTS_CONTROL_DISABLE;
			dcb.XonLim = 0;
			dcb.XoffLim = 0;
         anyChange = 1;
      } // set to default: no flow control
   
     if (anyChange) {
         if (! SetCommState((HANDLE)port, &dcb)) {
            cacheLastError = (jint) GetLastError();	
            return JNI_FALSE;
         }
      }
      cacheLastError = 0;
      return JNI_TRUE;
  } // Java_de_frame4j_io_WinDoesIt_setSerialParams()
  

/* Class:     de_frame4j_io_WinDoesIt
 * Method:    getSerialParams
 * Signature: (J)[I
 * 0: baud     Baud rate
 * 1: dataBits 5..8 
 * 2: stopBits fitting constant  (winbase.h)
 * 3: parity   Parity  (see {@link SerialDefs})
 * 4: flowControl (see {@link SerialDefs} net implemented yet, just 0)
 *
 FLOWCONTROL_RTSCTS_IN  = 1;
 FLOWCONTROL_RTSCTS_OUT = 2;
 FLOWCONTROL_XONXOFF_IN = 4
 FLOWCONTROL_XONXOFF_OUT = 8
 *
 */
   JNIEXPORT jintArray JNICALL 
       Java_de_frame4j_io_WinDoesIt_getSerialParams(JNIEnv *env, jclass,
                                                                 jlong port){
      if ((void*)port == INVALID_HANDLE_VALUE) return NULL;
      DCB dcb;
      if (! GetCommState((HANDLE)port, &dcb)) {
         cacheLastError = (jint) GetLastError();	
         return NULL;
      }
      cacheLastError = 0;
      
      jintArray retArr = env->NewIntArray(5);
      jint *retArrByC;
      retArrByC = env->GetIntArrayElements(retArr, 0);
      retArrByC[0] = dcb.BaudRate;
      retArrByC[1] = dcb.ByteSize;
      retArrByC[2] = dcb.StopBits;
      retArrByC[3] = dcb.Parity;

      // mapping Windows I/O features to javax does destroy information
      jint floCo = 0;
      if (dcb.fOutX) floCo = 8;
      if (dcb.fInX)  floCo|= 4;
      if (dcb.fDsrSensitivity) floCo |= 1;
      if (dcb.fRtsControl == RTS_CONTROL_TOGGLE) floCo |= 2;
      retArrByC[4] = floCo;
      
      env->ReleaseIntArrayElements(retArr, retArrByC, 0);
      return retArr;
  } // Java_de_frame4j_io_WinDoesIt_getSerialParams


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    setSerialTimeouts
 * Signature: (JIIIII)Z
 * typedef struct _COMMTIMEOUTS {
   DWORD ReadIntervalTimeout;
   DWORD ReadTotalTimeoutMultiplier;
   DWORD ReadTotalTimeoutConstant;
   DWORD WriteTotalTimeoutMultiplier;
   DWORD WriteTotalTimeoutConstant;}
 */
   JNIEXPORT jboolean JNICALL 
              Java_de_frame4j_io_WinDoesIt_setSerialTimeouts(JNIEnv*,
                       jclass, jlong port,
                         jint readIntervalTimeout,
                           jint readTotalTimeoutMultiplier,
                             jint readTotalTimeoutConstant,
                               jint writeTotalTimeoutMultiplier,
                                              jint writeTotalTimeoutConstant){
  
      if ((void*)port == INVALID_HANDLE_VALUE) return JNI_FALSE;
      COMMTIMEOUTS	timeouts;
      cacheLastError = 0;
      timeouts.ReadIntervalTimeout         = readIntervalTimeout;
      timeouts.ReadTotalTimeoutMultiplier  = readTotalTimeoutMultiplier;
      timeouts.ReadTotalTimeoutConstant    = readTotalTimeoutConstant;
      timeouts.WriteTotalTimeoutMultiplier = writeTotalTimeoutMultiplier;
      timeouts.WriteTotalTimeoutConstant   = writeTotalTimeoutConstant;

      if (! SetCommTimeouts((HANDLE)port, &timeouts)) {
            cacheLastError = (jint) GetLastError();	
            return JNI_FALSE;
      }
      return JNI_TRUE;
   } // Java_de_frame4j_io_WinDoesIt_setSerialTimeouts


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    getSerialTimeouts
 * Signature: (J)[I
 * typedef struct _COMMTIMEOUTS {
   DWORD ReadIntervalTimeout;
   DWORD ReadTotalTimeoutMultiplier;
   DWORD ReadTotalTimeoutConstant;
   DWORD WriteTotalTimeoutMultiplier;
   DWORD WriteTotalTimeoutConstant;}
 */
   JNIEXPORT jintArray JNICALL 
          Java_de_frame4j_io_WinDoesIt_getSerialTimeouts(JNIEnv *env,
                                                          jclass, jlong port){
      if ((void*)port == INVALID_HANDLE_VALUE) return NULL;
      COMMTIMEOUTS	timeouts;
      if (! GetCommTimeouts((HANDLE)port, &timeouts)) {
         cacheLastError = (jint) GetLastError();	
         return NULL;
      }
      cacheLastError = 0;
       
      jintArray retArr = env->NewIntArray(5);
      jint *retArrByC;
      retArrByC = env->GetIntArrayElements(retArr, 0);
      retArrByC[0] = timeouts.ReadIntervalTimeout;
      retArrByC[1] = timeouts.ReadTotalTimeoutMultiplier;
      retArrByC[2] = timeouts.ReadTotalTimeoutConstant;
      retArrByC[3] = timeouts.WriteTotalTimeoutMultiplier;
      retArrByC[4] = timeouts.WriteTotalTimeoutConstant;
      
      env->ReleaseIntArrayElements(retArr, retArrByC, 0);
      return retArr;
   } //  Java_de_frame4j_io_WinDoesIt_getSerialTimeouts


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    purgeSerialPort
 * Signature: (JI)Z
 */
   JNIEXPORT jboolean JNICALL Java_de_frame4j_io_WinDoesIt_purgeSerialPort
                                      (JNIEnv*, jclass, jlong port, jint ops){
      if ((void*)port == INVALID_HANDLE_VALUE) return JNI_FALSE;
      if (!PurgeComm((HANDLE)port, (DWORD) ops)) {
         cacheLastError = (jint) GetLastError();	
         return JNI_FALSE;
      } 
      cacheLastError = 0;
      return JNI_TRUE;
  } // Java_de_frame4j_io_WinDoesIt_purgeSerialPort


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    getSerialModemStatus
 * Signature: (J)I
 */
   JNIEXPORT jint JNICALL 
          Java_de_frame4j_io_WinDoesIt_getSerialModemStatus(JNIEnv*,
                                                          jclass, jlong port){
      if ((void*)port == INVALID_HANDLE_VALUE) return (jint)-1;
      DWORD		status;
      if (!GetCommModemStatus((HANDLE)port, &status)) {
         cacheLastError = (jint) GetLastError();	
         return (jint)-1;
      }
      cacheLastError = 0;
      return (jint) status;  
   } // Java_de_frame4j_io_WinDoesIt_getSerialModemStatus


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    writeSerial
 * Signature: (J[BII)I
 */
   JNIEXPORT jint JNICALL 
             Java_de_frame4j_io_WinDoesIt_writeSerial(JNIEnv* env,
                     jclass, jlong port, jbyteArray buff, jint off, jint len){
       if ((void*)port == INVALID_HANDLE_VALUE) return (jint) -1;
       if (len <= 0 || buff == NULL) return 0;
       int size = env->GetArrayLength(buff);
       if (off < 0) return -1; 
       if (size == 0) return 0;
       if (size < len) return -1; 
       signed char* bufi = env->GetByteArrayElements(buff, NULL);
       
       DWORD numOfBytesWr;
       if (! WriteFile((void*)port, bufi + off, (DWORD) len, 
                                           &numOfBytesWr, NULL)) {
          cacheLastError = (jint) GetLastError();
          numOfBytesWr = -1;	
          ///return (jint) -1;
       }  else {     
          cacheLastError = 0;
       }
       env->ReleaseByteArrayElements(buff, bufi, JNI_ABORT);
       return (jint) numOfBytesWr;  
   } // Java_de_frame4j_io_WinDoesIt_writeSerial__I_3BII


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    putSerial
 * Signature: (JB)I
 */
   JNIEXPORT jint JNICALL Java_de_frame4j_io_WinDoesIt_putSerial
                                    (JNIEnv*, jclass, jlong port, jbyte data){
      if ((void*)port == INVALID_HANDLE_VALUE) return (jint) -1;
      DWORD numOfBytesWr;
      if (! WriteFile((void*)port, &data, 1, &numOfBytesWr, NULL)){
          cacheLastError = (jint) GetLastError();	
          return (jint) -1;
       }       
       cacheLastError = 0;
       return (jint) 1;  
  } // putSerial
  

/* Class:     de_frame4j_io_WinDoesIt
 * Method:    flushSerial
 * Signature: (J)Z
 */
   JNIEXPORT jboolean JNICALL Java_de_frame4j_io_WinDoesIt_flushSerial
                                                (JNIEnv*, jclass, jlong port){
       if ((void*)port == INVALID_HANDLE_VALUE) return JNI_FALSE;
       if (! FlushFileBuffers((HANDLE)port)) {
         cacheLastError = (jint) GetLastError();	
         return JNI_FALSE;
      } 
      cacheLastError = 0;
      return JNI_TRUE;
  } // Java_de_frame4j_io_WinDoesIt_flushSerial
  

/* Class:     de_frame4j_io_WinDoesIt
 * Method:    readSerial
 * Signature: (J[BII)I
 */
   JNIEXPORT jint JNICALL 
          Java_de_frame4j_io_WinDoesIt_readSerial(JNIEnv* env,
                     jclass, jlong port, jbyteArray buff, jint off, jint len){
       if ((void*)port == INVALID_HANDLE_VALUE) return (jint) -1;
       if (len <= 0 || buff == NULL) return 0;
       if (off < 0) return -1;
       int size = env->GetArrayLength(buff);
       if (size == 0) return 0;
       if (size < len) return -1; 

       jbyte* bufi;
       bufi = env->GetByteArrayElements(buff, NULL);
       DWORD numOfBytesRd;
       if (! ReadFile((void*)port, bufi + off,
                                      (DWORD) len, &numOfBytesRd, NULL)){
          cacheLastError = (jint) GetLastError();	
          numOfBytesRd -1;
       }   else {     
          cacheLastError = 0;
       }
       env->ReleaseByteArrayElements(buff, bufi, 0);
       return (jint) numOfBytesRd;  
  } // Java_de_frame4j_io_WinDoesIt_readSerial


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    getSerial
 * Signature: (J)I
 */
   JNIEXPORT jint JNICALL Java_de_frame4j_io_WinDoesIt_getSerial
                                               (JNIEnv *, jclass, jlong port){
       if ((void*)port == INVALID_HANDLE_VALUE) return (jint) -1;
       
       jbyte  readV;
       DWORD numOfBytesRd;
       if (! ReadFile((void*)port, &readV, 1, &numOfBytesRd, NULL)){
          cacheLastError = (jint) GetLastError();
          return -1;	
       }
       cacheLastError = 0;
       if (numOfBytesRd != 1) return -1;
       return (jint) readV;
   } // Java_de_frame4j_io_WinDoesIt_getSerial


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    setDtrRts
 * Signature: (JZZ)Z
 */
   JNIEXPORT jboolean JNICALL Java_de_frame4j_io_WinDoesIt_setDtrRts
                                              (JNIEnv *, jclass, jlong port, 
                                                  jboolean dtr, jboolean rts){
      if ((void*)port == INVALID_HANDLE_VALUE) return JNI_FALSE;
      if (EscapeCommFunction((void*)port, dtr ? SETDTR : CLRDTR)
        &&  EscapeCommFunction((void*)port, rts ? SETRTS : CLRRTS)){
  	      cacheLastError = 0;
  	      return JNI_TRUE;
  	   }
     cacheLastError = (jint) GetLastError();	
     return JNI_FALSE;
   } // Java_de_frame4j_io_WinDoesIt_setDtrRts


/* Class:     de_frame4j_io_WinDoesIt
 * Method:    escapeComm
 * Signature: (JI)Z
 */
   JNIEXPORT jboolean JNICALL Java_de_frame4j_io_WinDoesIt_escapeComm
                                       (JNIEnv*, jclass, jlong port, jint op){
      if ((void*)port == INVALID_HANDLE_VALUE) return JNI_FALSE;
      if (EscapeCommFunction((void*)port, (DWORD)op)) {
  	      cacheLastError = 0;
  	      return JNI_TRUE;
  	   }
     cacheLastError = (jint) GetLastError();	
     return JNI_FALSE;
  } // Java_de_frame4j_io_WinDoesIt_escapeComm
  

/* Class:     de.frame4j.io.WinDoesIt
 * Method:    closePort
 * Signature: (J)Z
 */
   JNIEXPORT jboolean JNICALL Java_de_frame4j_io_WinDoesIt_closePort
                                               (JNIEnv*, jclass, jlong port){
      if ((void*)port == INVALID_HANDLE_VALUE) return JNI_FALSE;
      if (CloseHandle((void*)port)) {
  	      cacheLastError = 0;
  	      return JNI_TRUE;
  	   }
     cacheLastError = (jint) GetLastError();	
     return JNI_FALSE;
   }  // Java_de_frame4j_io_WinDoesIt_closePort
