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
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;

import de.frame4j.io.FileHelper;
import de.frame4j.io.Input;
import de.frame4j.net.Authentication;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;


/** <b>Copy from file or URL to file</b>. <br />
 *  <br />
 *  This application copies from a file or an URL to an output file or to
 *  normal output. The source specification is understood as URL (e.g.
 *  &quot;http(s)://www.xyz.de/apps/t.txt&quot;)
 *  and as file (e.g.
 *  &quot;D:/uhu/tmp/edwin/t.txt&quot;), treating both (comfortably) as
 *  equal (at command line / script level). File specifications will be
 *  used with the &quot;correct&quot; file separator (see
 *  {@link ComVar ComVar}.{@link ComVar#FS FS}).<br />
 *  <br />
 *  If the destination is a directory the source's filename will be
 *  used.<br />
 *  <br />
 *  This tool fetches files from Internet, the download of which by
 *  &quot;save link as&quot; browsers may deny or not even offer.<br />
 *  To get files from the net an authentication (name, password) may be
 *  provided as well as proxy settings. Like with all Frame4J's
 *  ({@link App} inheritors) applications this may be done by command line or
 *  &mdash; with more comfort &mdash; by providing an extra .properties
 *  file.<br />
 *  <br />
 *  On copying to a destination file the source's last modification date will
 *  be transfered (when possible) as the date of the copying usually being
 *  irrelevant.<br />
 *  <br />
 *  The copying process is binary by default (byte by byte; one to one).<br />
 *  If different encodings are specified for source and destination two
 *  transformations to and from Unicode are inserted. The same applies if text
 *  transformations are ordered. In these cases only, source and destination
 *  file may be the same. This is ordered by just omitting the destination
 *  parameter.<br />
 *  <br />
 *  Use cases for changing the the file encoding in situ are compilers that
 *  insist in just one source file encoding no matter
 *  which the platform and all other tools use and the platform independent
 *  deployment of text files in archives (like .properties in .jar).<br />
 *  <br />
 *  The example of changing of a file's text encoding shows that the copy
 *  process by this tool is a good place for any sort of text manipulation.
 *  Those may be implemented by extending this class and just overriding the
 *  method {@link #manipContent(String)} that will be intervened  between
 *  reading and writing if {@link #textManip} is not empty. If it is
 *  &quot;eclipseDictionary&quot; this class' implementation of
 *  {@link #manipContent(String)} will <ul>
 *  <li>separate word in single lines</li>
 *  <li>delete a trailing dot or comma</li>
 *  <li>delete single letters and doublets (also those
 *      distinguished by first capital only)</li>
 *  <li>delete words containing digits or upper case characters only.</li>
 *  </ul><br />
 *  That's just what's needed to bring a text containing correctly spelled
 *  word forms to the most simple form of a .dic file (dictionary) usable for
 *  Eclipse.<br />
 *  <br />
 *  <b>Hint</b>:  To this application {@link UCopy} belongs (as integral part)
 *  a .properties file named
 *  <a href="./doc-files/UCopy.properties" target="_top">UCopy.properties</a>.
 *  It's part of the documentation.<br />
 *  See also the hints in the
 *  <a href="./package-summary.html">package description</a>.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 1997 - 2003, 2005, 2007 &nbsp; Albrecht Weinert <br />
 */
 // so far   V00.00 (20.03.2000) : overhaul of NCopy
 //          V00.01 (08.05.2000) : de.a_weinert changes
 //          V00.02 (26.04.2001) : Directory as destination
 //          V00.04 (13.09.2001) : optional authentication
 //          V00.05 (29.03.2002) : Authentication to package .net
 //          V01.00 (24.05.2002) : de, AppBase
 //          V02.27 (17.09.2007) : moved to de.a_weinert.apps; transcode
 //          V.137+ (11.08.2009) : -eclipDic option (Eclipse dictionary)
 //          V.140+ (21.08.2009) : trans-code improved
 //          V.143+ (06.01.2016) : FileHelper

@MinDoc(
   copyright = "Copyright 1997 - 2009, 2015  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 44 $",
   lastModified   = "$Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $",
   usage   = "start as Java application (-? for help)",
   purpose = "copy file or URL to file"
) public class UCopy extends App {

   private UCopy(){} // no objects; no javaDoc 

/** Start method of UCopy. <br />
 *  <br />
 *  Execution is: java UCopy [options] source [destination] <br />
 *  <br >
 *  @param  args command line arguments
 *  @see #doIt
 *  @see App#go(String[])
 */
   public static void main(final String[] args){
      try {
         new UCopy().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

//----  Elements set by Prop's parameter evaluation  -------------------

/** Name of source file or source URL. */
   public String  source;

/** Name of destination file or directory. <br /> */
   public String  destination;

/** Denomination of a text manipulation to be done while copying. <br />
 *  <br />
 *  default: null = do nothing, except perhaps character coding changes<br />
 *  possible values: null, &quot;eclipseDictionary&quot;, .. ..<br />
 *
 *  @see #manipContent(String)
 *  @see #inEncoding
 *  @see #outEncoding
 */
   public String textManip;

/** Use a proxy server when reading from URLs. <br />
 *  <br />
 *  If true a proxy will be used and properties {@link #proxyHost} and
 *  {@link #proxyHost} take effect.<br />
 *  <br />
 *  default: true
 */
   public boolean proxySet;

/** Name of the proxy server. <br />
 *  <br />
 *  default: cache<br />
 *
 *  @see #proxySet
 */
   public String  proxyHost;

/** Port of the proxy server.. <br />
 *  <br />
 *  default: 8080<br />
 *
 *  @see #proxySet
 */
   public String  proxyPort;


/** User name with password (name:passw). <br />
 *  <br />
 *  default: no name.
 */
   public String name;

/** The input's (text) encoding. <br />
 *  <br />
 *  default: no encoding / binary
 */
   public String inEncoding;

/**  The output's (text) encoding. <br />
 *  <br />
 *  default:  no encoding / binary.
 */
   public String outEncoding;

//----  End of Pop set properties     --------------------


/** Optional text manipulation method. <br />
 *  <br />
 *  If the property {@link #textManip} is not empty this method is called
 *  between reading and writing the text content. It may do any optional
 *  content manipulation.<br />
 *  This implementation will do the preparation of an (Eclipse) dictionary
 *  if  {@link #textManip} is  &quot;eclipseDictionary&quot;.<br />
 *  <br />
 *  In that case this implementation will <ul>
 *  <li>separate word in single lines</li>
 *  <li>delete a trailing dot or comma</li>
 *  <li>delete single letters and doublets (also those
 *      distinguished by first capital only)</li>
 *  <li>delete words containing digits or upper case characters only.</li>
 *  </ul>
 *  That is just what's needed to bring a text containing correctly spelled
 *  word forms to the most simple form of a .dic file (dictionary) usable
 *  for  Eclipse.<br />
 *  <br />
 *  For all other values of {@link #textManip} this implementation will
 *  return the {@code content} unchanged.<br />
 *  <br />
 *  @param content the original text content, null will return the
 *         empty String, no matter what {@link #textManip} says
 *  @return content, modified
 */
   protected String manipContent(final String content){

      if (content == null) return ComVar.EMPTY_STRING;
      final int contLen = content.length();
      if (contLen == 0) return ComVar.EMPTY_STRING;
      if (textManip == null) return content;

      if ("eclipseDictionary".equals(textManip)) { // (Eclipse) dictionary
         final String[] wordsArray = TextHelper.splitWords(content);
         final int noOfAllWords = wordsArray.length;
         if (noOfAllWords == 0) { // no words
            if (verbose) log.println(" // no (0) words found in input.");
            return ComVar.EMPTY_STRING;
         } // no words
         if (verbose) log.println(" // " + noOfAllWords + " words found.");


         HashMap<String, Integer> firstOccu =
                                       new HashMap<>(noOfAllWords + 3000);

         int removedDoublets = 0;
         wordCheck1: for (int i = 0; i < noOfAllWords; ++i) {
            String act = wordsArray[i];
            if (act == null) continue wordCheck1; // removed already
            int wL = act.length();
            if (wL >= 2) { // more than one char (look for .
               final char tmpLast = act.charAt(wL-1);
               if (tmpLast == '.') { // ends with .
                  if (--wL > 1) wordsArray[i] = act = act.substring(0, wL);
               }  // ends with .,
            } // more than one char

            if (wL <= 1) {
               wordsArray[i] = null;
               if (verbose) {
                  log.println(" // [" + i + "]\"" + act
                          + "\" removed as single character");
               }
               continue wordCheck1;
            } // single char
            boolean containsLC = false;
            boolean containsUConly = true;
            boolean containsUC     = false;
            boolean containsNumbOrSpecial = false;
            boolean startsWithUC = false;
            char firstLC = 0;

            charCheck: for (int j = 0; j < wL; ++j) {
               char actC = act.charAt(j);
               if (actC <= '@') {
                  containsNumbOrSpecial = true;
                  break charCheck;
               }
               final char actLc = TextHelper.lowerC(actC);
               if (actLc == actC) {   // UC / LC check
                  containsLC = true;
                  containsUConly = false;
               } else if (containsUConly || !containsUC) { // UC / LC check
                  final char actUC = TextHelper.upperC(actC);
                  if (actUC != actC) {
                     containsUConly = false;
                  } else  {
                     containsUC = true;
                     if (j == 0) {
                        startsWithUC = true;
                        firstLC = actLc;
                     }
                  }
               } // UC / LC check
            } // charCheck
            if (containsNumbOrSpecial || containsUConly || !containsLC) {
               wordsArray[i] = null;
               if (verbose) {
                  log.println(" // [" + i + "]\"" + act + "\" removed as "
                 + (containsUConly ? "upperCase only" : "no applicable word"));
               }
               continue wordCheck1;
            } // remove as not applicable

            final Integer gotAlredy = firstOccu.get(act);
            if (gotAlredy != null) { // doublet found
               ++removedDoublets;
               wordsArray[i] = null;
               if (verbose) {
                  log.println(" // [" + i + "]\"" + act
                        + "\" removed as doublet of " + gotAlredy);
               }
               continue wordCheck1;
            } // doublet found

            firstOccu.put(act, i); // record first occurrence
            if (!startsWithUC) continue wordCheck1;
            final String act1LC = firstLC + act.substring(1);

            boolean found = firstOccu.get(act1LC) != null;  // lC equiv. found
            if (!found) for(int k = i + 1; k < noOfAllWords; ++k) { // remove doublets
               if (act1LC.equals(wordsArray[k])) {
                 found = true;
                 break;
               }
            } //

            if (found) {
               wordsArray[i] = null;
               if (verbose) {
                  log.println(" // [" + i + "]\"" + act
                        + "\" removed cause other begins with lower case");
               }
               continue wordCheck1;
            }
         } // wordCheck1

         if (verbose && removedDoublets != 0) {
            log.println(" // " + removedDoublets  + " doublets / "
                                        + noOfAllWords + " words removed");
         }
         int wC = 0;
         StringBuilder bastel = new StringBuilder(contLen);
         for (String outS: wordsArray) {
            if (outS == null) continue;
            bastel.append(outS).append('\n');
            ++wC;
         }
         if (verbose) log.println(" // Prepared as (Eclipse) dictionary "
                                 + wC + " words.\n");

         return bastel.toString();
      } /// (Eclipse) dictionary -

      return content;
   } // manipContent(String)

//--------------------------------------------------------------------------

/** Working method of UCopy. <br />
 *  <br />
 *  @see #main(String[]) main(String[])
 *  @see App#go(String[])
 */
   @SuppressWarnings("resource")  // dow will be indirectly closed as pw  
   @Override public  int doIt() {
      log.println();
      if (verbose) log.println(twoLineStartMsg().append('\n'));

      if (isDebug()) {
         log.println("appBase.askGraf = " +   appBase.askGraf);
         prop.list(log);
         log.println();
      }

      textManip = TextHelper.trimUq(textManip, null);
      boolean binary = textManip == null;
      inEncoding  = TextHelper.trimUq(inEncoding,  binary ? null : FILE_ENCODING);
      outEncoding = TextHelper.trimUq(outEncoding, binary ? null : FILE_ENCODING);

      if (TextHelper.startsWith(outEncoding, "bin", true)) outEncoding = null;
      if (TextHelper.startsWith(inEncoding, "bin", true)) inEncoding = null;

      binary = binary && (outEncoding == inEncoding  ||
                     (inEncoding != null &&  inEncoding.equals(outEncoding)));


   //-- Parameter evaluation complete

      boolean destinationDisp = true; // log destination name if verbose
      if (destination == null && !binary) {
        destination = source;
        destinationDisp = false;
      }

      int bcop = 0;
      if (name != null && (bcop = name.length()) >= 3) {
         int p = name.indexOf(':'); // name:pass
         if (p > 0 && p < (bcop-1)) {
            new Authentication(name.substring(0,p),
                                       name.substring(p+1), true);
         } else {
            return errMeld(15, "Name but no password");
         }
      } else  // give name
         name = null;

      if (proxySet) {
         System.setProperty("proxySet", "true");
         System.setProperty("proxyHost", proxyHost);
         System.setProperty("proxyPort", proxyPort);
      }

      long date = -1;
      Input ein;
      try {
         ein = Input.openFileOrURL(source);
         date = ein.lastModified();
         if (verbose) {
            if (binary) {
               log.println("\nCopy : ");
            } else if (textManip == null) {
               log.println("\nTranscode : ");
            } else {
               log.println("\nManip.(" + textManip + ") : ");
            }
            log.println(  "         " + ein.listLine());
            if ((ein.url != null) && proxySet)
            log.println(  "         with : proxy host = " + proxyHost
                               + ", proxy port = " + proxyPort);
         }
      } catch (IOException e) {
         if (proxySet)
            return errMeld(21, "proxy host = " + proxyHost
                + ", proxy port = " + proxyPort + "\n" + e.getMessage());
         return errMeld(23, e);
      } // try-catch open source

      bcop = 0;
      String content = null;
      if (!binary) { // Text with optional re-coding
         try {
            content = ein.getAsString(inEncoding);
          } catch (Exception e) {
            return errMeld(47, e);
          }
          if (textManip != null) { // content manipulation
             content = manipContent(content);
          } // all content manipulation (optional) done here
          bcop  =  content.length();

      } // Text with re-coding


      if (destination == null || "+".equals(destination)
                                   || "System.out".equals(destination) ) {
         destinationDisp = false;
         if (binary) {
            bcop  =  ein.copyTo(System.out);
         } else {
            OutputStreamWriter osw;
            try {
               osw = new OutputStreamWriter(System.out,
                                                Charset.forName(outEncoding));
            } catch (Exception e) {
               return errMeld(49, e);
            }
            try {
               osw.write(content);
               osw.close();
            } catch (IOException e) {
               return errMeld(53, e);
            }
         }
      } else { // System.out else file
         File fileOut = FileHelper.getInstance(destination);
         if (fileOut.isDirectory()) {
             String name = ein.getName();
             if (name == null) {
                 return errMeld(27, "No filename for " + destination
                         + " (dir) available");
             }
             if (verbose)
                log.println("        dir. + " + name );
             fileOut = new File(fileOut, name);
         }
         if (verbose && destinationDisp) {
            log.println("        to : \n   " + fileOut.getPath() );
         }

         if (binary) {
            bcop  =  FileHelper.copyFrom(ein, fileOut, outMode);
         } else {
            FileHelper.OS dos = null;
            try {
               dos =  new FileHelper.OS(fileOut,
                                         false, Charset.forName(outEncoding));
            } catch (IOException e) {
               return errMeld(49, e);
            }

            Writer pw = dos.osw;
            try {
               pw.write(content);
               pw.close();
            } catch (IOException e) {
               return errMeld(53, e);
            }
            if (date > 0L) fileOut.setLastModified(date);
         } // text copy to file

         if (bcop == 0)
             log.println("No copying (0 Bytes)");
         else if (verbose && bcop > 0)
             log.println(bcop + " bytes / characters copied");
      }
      if (bcop == -1) { // update
         if (verbose)
            log.println("Output " + destination + " not older than input");
         return 1;
      }
      if (bcop < 0) {
         return errMeld(41, "Output " + destination + " (" + outMode
                                        + ") not allowed (" + bcop + ").");
      }
      return 0;
   } // doIt()

} // UCopy (28.06.2002, 21.04.2005, 17.09.2007, 01.03.2009)
