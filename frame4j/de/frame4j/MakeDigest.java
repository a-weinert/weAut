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
package de.frame4j;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.frame4j.graf.AskDialog;
import de.frame4j.io.Input;
//  import de.frame4j.net.ProxyProps; removed 23.03.2021
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.MinDoc;


/** <b>Generate a digest (default MD5) for a given text</b>. <br />
 *  <br />
 *  This Java application makes a digest for a text given as <ul>
 *  <li> command line parameter,</li>
 *  <li> a text input file <br />
 *  &nbsp; &nbsp; or from a</li>
 *  <li> a text input box hiding the key entries for password digest.</li></ul>
 *
 *  <br />
 *  The digest algorithms available are those of the Java platform. At Java6
 *  (2009) those were MD2, MD5 (default) and  SHA-1,
 *  <br />
 *  <b>Hint</b>: To this application {@link MakeDigest}
 *  (<a href="doc-files/MakeDigest.java" target="_top">source</a>)
 *  belongs a .properties file
 * <a href="./doc-files/MakeDigest.properties" target="_top">MakeDigest.properties</a>
 *  that may be considered as part of the documentation and may
 *  answer open questions.<br />
 *  <br />
 *  <br />
 *  Copyright 2010 &nbsp; Albrecht Weinert<br />
 *  <br />
 *  @see      App
 */
 //  V.$Revision: 33 $ ($Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $)
 // so far    V.0.0  (23.07.2010) : new
 //           V.033  (23.03.2021) : de.frame4j.net.ProxyProps removed 

@MinDoc(
   copyright = "Copyright 2010  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application (-? for help)",
   purpose = "generate a digest for a given text"
) public class MakeDigest extends App {

/** Type of the digest algorithm. <br />
 *  <br />
 *  It must denote an algorithm the java platform provides a factory for.<br />
 *  <br />
 *  Possible values (as of mid2010): MD2 MD5 SHA-1<br />
 *  default: MD5
 */
   public String alg = "MD5";

/** The input text (or file name). <br />
 *  <br />
 *  default: null
 */
   public String input;

/** The input is a file name. <br />
 *  <br />
 *  default: false
 */
   public boolean inputIsFile;  // by option parameter -file

/** The input's (text file) encoding. <br />
 *  <br />
 *  default: no encoding / binary
 */
   public String inEncoding;


/** Use hidden (password) as text. <br />
 *  <br />
 *  default: false
 */
   public boolean hiddenEntry;

/** Surround the digest by single quotes. <br />
 *  <br />
 *  default: false
 */
   public boolean singleQuote;

/** Start method of MakeDigest. <br />
 *  <br />
 *  The application end with exit-code 0 on success. Exit-code &gt; 0 means
 *  abort due to a problem.<br />
 *  <br />
 *  @param  args command line parameter   <br />
 *          Execute:  MakeDigest [options]
 */
   public static void main(final String[] args){
      try {
         new MakeDigest().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

/** The hexadecimal digits as char[]. <br /> */
   private static final char[] zif = {'0', '1', '2', '3', '4', '5', '6', '7',
                            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   char[] passWord;
   byte[] theInp = null;
   byte[] theDigest = null;

/** Working method of MakeDigest. <br />
 *  <br />
 *  @return &gt; 0: error; 0: OK
 */
   @Override public  int doIt(){
      if (!isSilent()) {
         log.println();
         log.println(twoLineStartMsg().append('\n'));

         if (isTest()) {
            log.println(prop.toString());
         } // >= TEST
      } // not silent
      if (hiddenEntry) {
         final char[] pwi = AskDialog.getAnswerText(
            valueLang("pkexpdtit"),  // titel PKextr password dialog
            "MakeDigest " + alg, // upper
            false,             // upper monospaced
            valueLang("pkexpasye"), valueLang("pkexpasno"), // yes, cancel
            990,            // waitMax = 99s
            valueLang("pkexpassr"),
            null, true);  //Color bg   password
         if (pwi != null) input = String.valueOf(pwi);
      }
      if (input == null || input.length() == 0) {
         return errMeld(5, valueLang("nospcinp", "Eingabe fehlt"));
      }
      MessageDigest md = null;
      try {
         md = MessageDigest.getInstance(alg);
      } catch (NoSuchAlgorithmException ex) {
         return errMeld(6, formMessage("cnmAlg", alg));
      }

      if (inputIsFile) {
         Input ein;
         try {
            ein = Input.openFileOrURL(input);
         } catch (IOException e) {
            return errMeld(23, e);
         } // try-catch open source
         try {
            if (inEncoding != null) {
               input = ein.getAsString(inEncoding);
               theInp = input.getBytes();
            } else {
               theInp = ein.getAsBytes();
            }           } catch (Exception e) {
               return errMeld(47, e);
            }
      } else {
         theInp = input.getBytes();
      }

     theDigest = md.digest(theInp);

     StringBuilder bastel = new StringBuilder(68);
     if (singleQuote) bastel.append('\'');
     for (byte b : theDigest) {
        int value = b;
        value &= 0x0FF;
        if (value == 0) {
           bastel.append("00");
           continue;
        }
        bastel.append(zif[(value & 0x0F0) >> 4]).append(zif[value & 0x0F]);
     } // for bastel
     if (singleQuote) bastel.append('\'');


     if (isSilent()) {
        log.print(bastel);
        log.flush();
        return 0;
     }
     out.println();
     log.print(bastel);
     log.flush();
     out.println();
     out.flush();
     return 0;
  } // doIt()

} // MakeDigest (27.12.2004, 09.02.2005, 07.11.2008)
