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
package de.frame4j.xml;

import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** <b>Handling HTML, XML &mdash; support for Java applications</b>. <br />
 *  <br />
 *  This class contains static methods for formatting, analysing and modifying
 *  of HTML and XML texts. It is an endorsement to the more common 
 *  {@link TextHelper}.<br /> 
 *  <br />
 *  <br /> 
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2003- 2007, 2016 &nbsp; Albrecht Weinert  
 */
 // so far    V02.00 (17.09.2003 09:46) : new, collection of static helpers
 //           V02.0x (01.06.2003 12:00) : away with 1.3 compatibility; CharSeq 
 //           V02.02 (22.02.2005 09:57) : JAXP_..., from text to xml 
 //           V02.03 (25.10.2006 14:32) : LDAP additions
 //           V02.08 (26.10.2007 07:51) : listAllWithAtts using PropMap
 //           V.128- (19.07.2009 21:13) : SVN, Frame4J
 //           V.142+ (06.01.2016) : FileHelper

@MinDoc(
   copyright = "Copyright   2003 - 2007, 2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use in XML / HTML applications",  
   purpose = "provide helper methods and values"
) public abstract class MLHelper  {
 
/** No objects. */
   private MLHelper(){}  // no JavaDoc  ==> package

   
/** Scheme for JAXP. <br />
 *  <br />
 *  The value of this String is a property name.<br />
 *  <br />
 *  Value: {@value}<br />
 *  <br />
 *  Hint: http is here and in other constants like that no hypertext URL but
 *  XML standardised nonsense. To say it clear: do not try to set it into your
 *  browser's address line; it will lead to nowhere. Java's package naming
 *  conventions show an intelligent way to bind worldwide unambiguous
 *  namespaces to URLs without causing such confusion.<br />
 */
   public static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
   

/** Scheme of W3C.<br />
 *  <br />
 *  Possible value of the property {@link #JAXP_SCHEMA_LANGUAGE}.<br />
 *  <br />
 *  See the hint at {@link #JAXP_SCHEMA_LANGUAGE }.
 *  <br />
 *  Value: {@value}<br />
 */
   public static final String W3C_XML_SCHEMA =
                                        "http://www.w3.org/2001/XMLSchema";
   
/** Scheme source for JAXP.<br />
 *  <br />
 *  The value of this String is a property name.<br />
 *  <br />
 *  See the hint at {@link #JAXP_SCHEMA_LANGUAGE }.
 *  <br />
 *  Value: {@value}<br />
 */
    static public final String JAXP_SCHEMA_SOURCE =
                    "http://java.sun.com/xml/jaxp/properties/schemaSource";  
   

/** &quot;Un-percent&quot; a text (a Http request). <br />
 *  <br />
 *  HTTP clients do often code special and sometimes normal characters 
 *  (like ;, :, *  =, %, ,, +, _, Umlauts and others) in their requests 
 *  in the form %2B (i.e. percent and 2 times hexadecimal).<br />
 *  This method replaces all those triplets by their real Unicode value.<br />
 *  Parameter null returns an empty StringBuilder.<br />
 *  <br />
 *  Percent sequences, where no two 0..9, a..f or A..F follow the % will be 
 *  left unchanged.<br /> 
 *  <br />
 *  @param request the original text
 *  @return the un-percented text
 *  @see #zifValue(char)
 */
   public static StringBuilder unEscapePercent(final CharSequence request){
      if (request == null) return  new StringBuilder(8);
      final int reqLen = request.length();
      StringBuilder bastel = new StringBuilder(reqLen);
      for (int i = 0; i < reqLen; ++i) {
         char c = request.charAt(i);
         hexDec: if (c == '%' && i < reqLen - 2) {
            int v16 = zifValue(request.charAt(i + 1));
            if (v16 == -1) break hexDec;
            int v01 = zifValue(request.charAt(i + 2));
            if (v01 == -1) break hexDec;
            i = i + 2;
            c = (char)(v16 * 16 + v01);
         }
         bastel.append(c);
      }
      return bastel;       
   } // unEscapePercent


/** Transform digit (hexadecimal) to number (0-15). <br />
 *  <br />
 *  This method returns for one hexadecimal digit, i.e.
 *  0..9, A..F or a..f the corresponding value 0..9, 10..15.<br />
 *  <br />
 *  All other characters return -1.<br />
 *  <br />
 * @param c the digit character
 * @return  the number
 */
   static public int zifValue(final char c){
      if (c < '0' || c > 'f') return -1;
      if (c <= '9') return c - '0';
      if (c >= 'a') return c - ('a' - 10);
      return c >= 'A' && c <= 'F' ?  c - ('A' - 10) : -1;   
   } // zifValue(char)

//---------------------------------------------------------------------

   
   
/** XML encode an object and write to a file. <br />
 *  <br />
 *  The object supplied as parameter {@code mb} and all objects associated to
 *  it have to be Java beans. Otherwise a senseless XML file (empty except
 *  start and end tag) will be generated.<br />
 *  <br />
 *  The object {@code mb} will be XML encoded and the result will be written
 *  into a file named {@code file}.<br />
 *  <br />
 *  In case of failure exceptions IllegalArgument (parameter)
 *  IO (file problems) or ClassNotFound (wrong JDK &lt; 1.4) are thrown.<br />
 *  <br />
 * @param mb   the (bean) object to encode
 * @param file the file for encoded output
 * @throws IllegalArgumentException wrong parameter values
 * @throws ClassNotFoundException  JDK problems in getting a XML encoder
 * @throws IOException problems with streams or files
 */
   public static void xmlEncode(final Object mb, final String file) 
         throws IllegalArgumentException, ClassNotFoundException, 
                IOException {
      if (mb == null) 
         throw new IllegalArgumentException("No Object for XML-Encode");
      if (file == null || file.length() == 0) 
         throw new IllegalArgumentException("No Filename for XML-Encode");
      XMLEncoder e;
      try  { 
         e = new XMLEncoder(new FileOutputStream(file));
      } catch (NoClassDefFoundError ndf) {
         throw new ClassNotFoundException("No XML-Encoder / no JDK >= 1.4.0");
      }
      e.writeObject(mb);
      e.close();
   } // xmlEncode(Object, String)


/** Generate an object from a XML file. <br />
 *  <br />
 *  The parameter {@code fileName} has to denote a file containing a XML
 *  document. This must describe an object / bean. This will be constructed
 *  and returned.<br />
 *  <br />
 *  If the file does not exist and does {@code fileName} not end with .xml,
 *  .xml is appended and the trial repeated.<br />
 *  <br />
 *  After finding the file and opening this method delegates to
 *  {@link #xmlDecode(java.io.InputStream)
 *   xmlDecode(InputStream}.<br />
 * @param fileName the file for input to be decoded
 * @return the (bean) object decoded respectively made
 * @throws IllegalArgumentException wrong parameter values
 * @throws ClassNotFoundException  JDK problems in getting a XML encoder
 * @throws IOException problems with streams or files
 */
   public static Object xmlDecode(String fileName) throws 
                IllegalArgumentException, ClassNotFoundException, 
                IOException   {
      fileName = TextHelper.makeFName(fileName, null); // not empty /\ LinWin
      if (fileName == null)
         throw new IllegalArgumentException("No Filename for XML-Decode");
      FileInputStream fis = null;
      if (!TextHelper.simpLowerC(fileName).endsWith(".xml")) try {
         fis = new FileInputStream(fileName);
         return xmlDecode(fis);
      } catch (IOException ioe) {
         fileName = fileName + ".xml";
      }
      fis = new FileInputStream(fileName);
      return xmlDecode(fis);
   } // xmlDecode(String)


/** Generate an object (bean) from a XML description. <br />
 *  <br />
 *  The parameter {@code in} must be an input stream delivering a XML
 *  document. This must describe an object / bean. This will be constructed
 *  and returned.<br />
 *  <br />
 *  In case of failure exceptions IllegalArgument (parameter)
 *  IO (file problems) or ClassNotFound (wrong JDK &lt; 1.4) are thrown.<br />
 *  <br />
 * @param  in the XML input stream
 * @return the (bean) object generated according to the XML description 
 * @throws IllegalArgumentException wrong parameter value
 * @throws ClassNotFoundException problems in getting a XML decoder 
 * @throws IOException stream problems
 * @throws ClassCastException forwarded from ? (obsolete?) 
 */
   public static Object xmlDecode(InputStream in) throws 
                IllegalArgumentException, ClassNotFoundException, 
                IOException, ClassCastException  {
      if (in == null) 
         throw new IllegalArgumentException("No InputStream for XML-Decode");
      XMLDecoder dec;
      try  { 
         dec = new XMLDecoder(in);
      } catch (NoClassDefFoundError ndf) {
         throw new ClassNotFoundException(
                                         "No XML-Decoder / no JDK >= 1.4.0");
      }
      Object mb = dec.readObject();
      dec.close();
      return mb;
   } // xmlDecode(InputStream)

//---------------------------------------------------------------------------

/** List a node and all attributes to a PrintWriter. <br />
 *  <br /> 
 * @param node a Node with attributes to be listed
 * @param log  the destination writer
 * @param recurs if &gt; 0 descend recurs levels to list sub-Nodes too
 */
public static void listNode(Node node, PrintWriter log, int recurs){
     if (node == null || log == null) return; 

     String parentName = node.getNodeName();
     String nodeValue  = node.getNodeValue();
     int nVL = -1;
     if (nodeValue != null) {
        nodeValue = nodeValue.trim();
        nVL = nodeValue.length();
     } // Length content white space trimmed

     if (parentName.equals("#text") && nVL == 0) {
        log.println(" /// Empty Text Node ");
        return;
     }

     log.print(" /// Node " + parentName + " /// Start < ");

   ///  int anzAtts = 0;
     NamedNodeMap atts = node.getAttributes();
   ///  if (atts != null) anzAtts = atts.getLength(); 
     if (atts != null) {
        final int anzAtts = atts.getLength(); 
        log.print(anzAtts + " Attributes :\n       ");
        for (int i = 0; i < anzAtts; ++i) {
           Node n = atts.item(i);
           String nodeName  = n.getNodeName();
           nodeValue = n.getNodeValue();
           log.print( nodeName + " = \"" + nodeValue + "\", ");
        }   // for Atts
     } // atts are there
     log.println();   

     if (nVL >= 0) {
        log.print(" /// Node " + parentName + " : Content : ");
        if (nVL < 36) {
           log.println(nVL == 0 ? "<empty>" : "\"" + nodeValue + "\"");
           } else {
           log.println("\n "
                 + nodeValue + "\n");
           }
     } // has content   

     int anzNodes = 0;
     NodeList nNL = node.getChildNodes();
     if (nNL != null) anzNodes = nNL.getLength(); 
     if (anzNodes > 1) log.println(" /// Node " + parentName 
                            +  " : " +  anzNodes + " Children :");
     
     boolean doRec = recurs > 1;           

     for (int i = 0; i < anzNodes; ++i) {
        Node n = nNL.item(i);
        if (doRec) {
           listNode(n, log, recurs - 1);
           continue;
        } 
        String nodeName  = n.getNodeName();
        log.println(" /// Node " +  parentName  + " node[" + i 
              + "] " + nodeName );
     } // for over Child-nodesnodes   
     log.println(" /// Node " +  parentName  + " /// End > ");
  } // listNode(Node, PrintWriter, int)
  
    
//---------------------------------------------------------------------------

/** Determine (MIME-) content type from the first (4-11) bytes. <br />
 *  <br />
 *  It will be tried to determine the content type by the start of the
 *  content.<br />
 *  Recognised are: <ul>
 *  <li>image/jpeg</li>
 *  <li>application/java-vm</li>
 *  <li>application/x-java-serialized-object</li>
 *  <li>image/x-pixmap</li>
 *  <li>image/x-bitmap</li>
 *  <li>image/gif</li>
 *  <li>image/png</li>
 *  <li>audio/basic</li>
 *  <li>audio/x-wav</li>
 *  <li>text/html</li>
 *  <li>application/xml</li></ul>
 *  <br />
 *  Reference to source: A similar algorithm from a old version of
 *  {@link java.net.URLConnection#guessContentTypeFromStream} is used here
 *  a bit &quot;tuned&quot;.<br />
 *  <br />
 * @param  start the start of the content (minimum) 4 or 6, 8 or 11 byte!
 * @return a (good) suggestion on the contents MIME type or null
 * @see #guessContentType(java.lang.String)
 */
   public static String guessContentType(byte[] start){
      if (start == null || start.length < 4) return null;
      int c1 = start[0];
      int c2 = start[1];
      int c3 = start[2];
      int c4 = start[3];

      if (c1 == '<' && c2 == '!')  // starts with HTML-Comment
         return "text/html";

      if (c1 == 0xCA && c2 == 0xFE && c3 == 0xBA && c4 == 0xBE)
         return "application/java-vm";

      if (c1 == 0xAC && c2 == 0xED)
         // next two bytes are version number, currently 0x00 0x05
         return "application/x-java-serialized-object";

      if (c1 == 'G' && c2 == 'I' && c3 == 'F' && c4 == '8')
         return "image/gif";

      if (c1 == '#' && c2 == 'd' && c3 == 'e' && c4 == 'f')
         return "image/x-bitmap";

      if (c1 == 0x2E && c2 == 0x73 && c3 == 0x6E && c4 == 0x64)
         return "audio/basic";  // .au format, big endian

      if (c1 == 0x64 && c2 == 0x6E && c3 == 0x73 && c4 == 0x2E)
         return "audio/basic";  // .au format, little endian
                  

      if (c1 == 0xFF && c2 == 0xD8 && c3 == 0xFF && 
                           (c4 == 0xE0  || c4 == 0xEE ))
         return "image/jpeg";

      if (c1 == 'R' && c2 == 'I' && c3 == 'F' && c4 == 'F')
       /* sun dosn't know if this is official but evidence
        * suggests that .wav files start with "RIFF"  */
         return "audio/x-wav";  

   // so far decidable by  c1...c4 

      if (c1 == '<') { // may be HTML
         if (start.length < 5) return null;
         String s = TextHelper.simpLowerC(new String(start, 1, 4));
         if (s.equals("html") || s.equals("body") || s.equals("head"))
            return "text/html";
         if (s.equals("!xml")) 
            return "application/xml";
           return null;
      } // HTML, XML oder nix

      if (start.length < 6) return null;
      int   c5 = start[4];
      int   c6 = start[5];

      if (c1 == '!' && c2 == ' ' && c3 == 'X' && c4 == 'P' && 
          c5 == 'M' && c6 == '2')
         return "image/x-pixmap";
         

      if (start.length < 8) return null;
      int   c7 = start[7];
      int   c8 = start[8];
 
      // big and little endian UTF-16 encodings, with byte order mark
      if (c1 == 0xfe && c2 == 0xff) {
          if (c3 == 0 && c4 == '<' && c5 == 0 && c6 == '?' &&
         c7 == 0 && c8 == 'x') {
         return "application/xml";
          }
      }

      if (c1 == 0xff && c2 == 0xfe) {
          if (c3 == '<' && c4 == 0 && c5 == '?' && c6 == 0 &&
         c7 == 'x' && c8 == 0) {
         return "application/xml";
          }
      }

      if (c1 == 137 && c2 == 80 && c3 == 78 &&
         c4 == 71 && c5 == 13 && c6 == 10 &&
         c7 == 26 && c8 == 10) {
          return "image/png";
      }

      if (start.length < 11) return null;
      int   c9 = start[9];
      int   c10 = start[10];
      int   c11 = start[11];

   /* File format used by digital cameras to store images.
    * Exif Format can be read by any application supporting
    * JPEG. Exif Spec can be found at:
    * https://www.loc.gov/preservation/digital/formats/fdd/fdd000146.shtml */
      if (c1 == 0xFF && c2 == 0xD8 && c3 == 0xFF && c4 == 0xE1
             &&  c7 == 'E' && c8 == 'x' && c9 == 'i' && c10 =='f'
              &&  c11 == 0) 
         return "image/jpeg";

      return null;
   } // guessContentType(byte[]


/** Determine (MIME-) content type from file type (extension). <br />
 *  <br />
 *  It will be tried to determine the content type by the file extension.<br />
 *  <br />
 *  @param  ext the file extension
 * @return a (good) suggestion on the contents MIME type or null
 *  @see #guessContentType(byte[])
 *  @see #MIME_TYPES
 */
   static public String guessContentType(String ext){
      if (ext == null) return null;
      final int extL = ext.length();
      if (extL < 1) return null;
      if (ext.charAt(0)== '.') {
         if (extL < 2) return null;
      } else ext = '.' + ext;
      ext = TextHelper.simpLowerC(ext);
   // quick decission on the most important types
      if (ext.equals(".html") || ext.equals(".htm"))
         return "text/html";
      if (ext.equals(".gif") )
         return "image/gif";
      if (ext.equals(".png") )
         return "image/png";
      if (ext.equals(".jpg") || ext.equals(".jpeg"))
         return "image/jpeg";
      if (ext.equals(".xml") )
         return "application/xml";

   // Search in the list
   // Hint: types in  s: MIME_TYPES have the form ".abc,"   
      ext = ext + ',';
      for (String s: MIME_TYPES) {
         int dp = s.indexOf(':');
         if (dp < 2) continue;          // 16 is start of first .ext,
         if (TextHelper.indexOfOpt(s, ext, 16, false) < 0) continue;
         return s.substring(0, dp); 
      }
      return null; 
   } // String guessContentType(String 

/** List MIME type: file extension(s). <br />
 *  <br />
 *  The syntax of the list is:<br />
 *  One String per MIME type in the format<br /> &nbsp;
 *   &nbsp; MIME/TYP: .erw,.erw2,<br />
 *  <br />
 *  Implementation hint for changes /or extensions:.<br />
 *  The colon (:) at the MIME types end is important as is the comma (,) at
 *  the end of each extension, even the last one.<br />
 *  Spaces around the extensions are allowed; the first extension must start
 *  not earlier than index 16, even if the MIME type is shorter.<br />
 *  Example:<br /> &nbsp; 
 {@code &quot;image/jpeg:      .jfif,.jfif-tbnl,.jpe,.jpg,.jpeg,&quot;}<br /> 
 *  <br />
 *  Hint 2: This final array is public for sake of efficient use. Do never 
 *  modify its components.<br />
 *  <br /> 
 *  @see #guessContentType(java.lang.String) guessContentType(String)
 */  
   public static final String[] MIME_TYPES = {
   //123456789x123456789v /// first ext at >= 16 !!!   
   "application/oda: .oda,",
   "application/pdf: .pdf,",
   "application/postscript:   .eps,.ai,.ps,",
   "application/rtf: .rtf,",
   "application/x-dvi: .dvi,",
   "application/x-hdf: .hdf,",
   "application/x-latex: .latex,",
   "application/x-netcdf: .nc,.cdf,",
   "application/x-tex: .tex,",
   "application/x-texinfo: .texinfo,.texi,",
   "application/x-troff:   .t,.tr,.roff,",
   "application/x-troff-man: .man,",
   "application/x-troff-me:  .me,",
   "application/x-troff-ms:  .ms,",
   "application/x-wais-source: .src,.wsrc,",
   "application/zip:.zip,",
   "application/x-bcpio: .bcpio,",
   "application/x-cpio:  .cpio,",
   "application/x-gtar:  .gtar,",
   "application/x-shar:  .sh,.shar,",
   "application/x-sv4cpio: .sv4cpio,",
   "application/x-sv4crc:  .sv4crc,",
   "application/x-tar:   .tar,",
   "application/x-ustar:   .ustar,",
   "audio/basic:    .snd,.au,",
   "audio/x-aiff:   .aifc,.aif,.aiff,",
   "audio/x-wav:    .wav,",
   "image/gif:      .gif,",
   "image/png:      .png,",
   "image/ief:      .ief,",
   "image/jpeg:     .jfif,.jfif-tbnl,.jpe,.jpg,.jpeg,",
   "image/tiff:     .tif,.tiff,",
   "image/vnd.fpx:  .fpx,.fpix,",
   "image/x-cmu-rast: .ras,",
   "image/x-portable-anymap: .pnm,",
   "image/x-portable-bitmap: .pbm,",
   "image/x-portable-graymap:.pgm,",
   "image/x-portable-pixmap: .ppm,",
   "image/x-rgb:    .rgb,",
   "image/x-xbitmap: .xbm,.xpm,",
   "image/x-xwindowdump:   .xwd,",
   "text/html:      .htm,.html,",
   "application/xml: .xml",
   "text/plain:     .text,.c,.cc,.c++,.h,.pl,.txt,.java,.el,.asm,",
   "text/tab-separated-values:   .tsv,",
   "text/x-setext:  .etx,",
   "video/mpeg:     .mpg,.mpe,.mpeg,",
   "video/quicktime: .mov,.qt,",
   "application/x-troff-msvideo:   .avi,",
   "video/x-sgi-movie:   .movie,.mv,",
   "message/rfc822: .mime,",
   "application/octet-stream:"
      +   " .saveme,.dump,.hqx,.arc,.obj,.lib,.bin,.exe,.zip,.gz,",
   };  //  String[] MIME_TYPES
   
//------------------------------------------------------------------------
   

/** Is the node a (part of) a document. <br />
 *  <br />
 *  This method returns true if {@code node} is not null and is of type
 *  {@link DocumentFragment} or  {@link Element}. These types may have 
 *  children and hence may be start of a (document) structure.<br />
 */
   public static boolean isDocNode(Node node){
      return node != null   // is null? an improvement? who knows?
         && (node instanceof Document
             || node instanceof DocumentFragment
                 || node instanceof Element);
   } // isDocNode(Node)
   

/** Shorten a HTML document by not necessary default attributes. <br />
 *  <br />
 *  If a HTML document is parsed (to DOM) as XML using a W3C-DTD all default
 *  attributes (normally not necessary for further HTML processing) are added,
 *  namely:<dl>
 *  <dt>rowspan=&quot;1&quot;</dt><dd>bei &lt;td&gt;</dd>
 *  <dt>rowspan=&quot;1&quot;</dt><dd>bei &lt;th&gt;</dd>
 *  <dt>colspan=&quot;1&quot;</dt><dd>bei &lt;td&gt;</dd>
 *  <dt>colspan=&quot;1&quot;</dt><dd>bei &lt;th&gt;</dd>
 *  <dt>clear=&quot;none&quot;</dt><dd>bei &lt;br /&gt;</dd>
 *  <dt>schape=&quot;rect&quot;</dt><dd>bei &lt;a&gt;</dd>
 *  <dt>schape=&quot;rect&quot;</dt><dd>bei &lt;area&gt;</dd>
 *  </dl>
 *
 *  The method exterminates these {@link org.w3c.dom.Attr dom.Attr}ibutes from
 *  a {@link org.w3c.dom.Element dom.Element} and (recursively) from all its
 *  children.<br />
 *  <br />
 *  Attention: This methods effect is almost nil, if the document is coupled
 *  to a grammar (DTD or Schema), as this resurrects the missing attributes 
 *  with default values at once.<br />
 *  <br />
 *  @param root start element of the (HTLM) document or a part, to be cleaned.
 *  @return     number of exterminated default attributes
 */
   public static int clearHTMLdefAtts(Element root){
      if (root == null) return 0;
      int       sum = 0;
      String   name = root.getTagName();
      if (name != null 
              && (name = name.trim()).length() != 0) {
         name = TextHelper.simpLowerC(name);
         Attr att = null;
         if ("td".equals(name) || "th".equals(name) ) {
            att = root.getAttributeNode("rowspan");
            if (att != null && att.getValue().equals("1")) {
               root.removeAttribute("rowspan");
               ++sum;
            }
            att = root.getAttributeNode("colspan");
            if (att != null && att.getValue().equals("1")) {
               root.removeAttribute("colspan");
               ++sum;
            }
         } else if ("br".equals(name)) {
            att = root.getAttributeNode("clear");
            if (att != null && att.getValue().equals("none")) {
               root.removeAttribute("clear");
               ++sum;
             }
         } else if ("a".equals(name) || "area".equals(name) ) {
            att = root.getAttributeNode("shape");
            if (att != null && att.getValue().equals("rect")) {
               root.removeAttribute("shape");
               ++sum;
            }
         }
      } // has name
      NodeList list = root.getChildNodes();
      int len = list.getLength();
      for (int i= 0; i < len; ++i) {
         Node n = list.item(i);
         if (! (n instanceof Element)) continue; // ignore Attr etc.
         sum += clearHTMLdefAtts((Element)n);
      } // for over Children
      return sum;
   }  // clearHTMLdefAtts(Element)
   
      
} // MLHelper (25.10.2006, 26.10.2007, 03.02.2009)
