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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

import de.frame4j.demos.SAXdemo;
import de.frame4j.io.FileHelper;
import de.frame4j.io.Input;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.MinDoc;
import de.frame4j.xml.MLHelper;
import de.frame4j.xml.ParseErrorHandler;
import de.frame4j.xml.XMLconf;
import de.frame4j.xml.XMLdoc;


/** <b>XML input and output: XML &mdash;&gt; DOM &mdash;&gt; XML</b>. <br />
 *  <br />
 *  This application handles the input and output of XML documents as a DOM
 *  object. Multiple variants and combinations are possible:<ul>
 *    <li>Input<ul>
 *      <li>Parse a text file or URL according to XML rules.<br /> 
 *          The parser's strictness may be controlled in a wide range by 
 *          properties (.properties files or command line parameters).<br />
 *          This alone gives a quite good syntax check for all kinds of XML
 *          and (X)HTML files, by grace of clearly defined return codes
 *          suitable for automatic (batch) processing:<br />
 *             &nbsp; return 2 or more: errors occurred (plus may 
 *                           be warnings)<br />
 *             &nbsp; return 1 : only warnings 
 *                          (non fatal errors) occurred<br />
 *             &nbsp; return 0 : all OK (parsed, optionally validated).</li>
 *     <li>Read a serialised DOM object from a file or URL (option -deser).
 *         This will be done as serialised before without any checking /
 *         validating.<br />
 *         Remark: After serialisation's migration (begun with JDK1.4 and here
 *         and in between performed) from binary to XML format de-serialising
 *         is just parsing (w/o validation).</li></ul>
 *  </li><li>Transformation<ul>
 *     <li>Transforming the document (the DOM object) using a transforming
 *         XML style sheet (.xsl; option -trans).</li>
 *     <li>Applying a standard transformation by 
 *         {@link XMLdoc XMLdoc}.{@link XMLdoc#applyTransforms()
 *                     applyTransforms()} (options -noDefAtts, -compact).</li></ul>
 *  </li><li>Output<ul>
 *     <li>Output the document (the DOM object) as XML text to a file.<br />
 *         The output formatting can be controlled by properties.</li>
 *     <li>Output / Serialise the DOM object (the document) as is without all
 *         output formatting (option -ser).</li></ul>   
 *  </li></ul> 
 * 
 *  The application handles the XML document as a object of type
 *  {@link org.w3c.dom.Document org.w3c.dom.Document}.<br />
 *  <br />
 *  For further pre or post processing by this or other applications objects
 *  of this type can be serialised respectively de-serialised by this 
 *  application. In that sense this application may act as a common parser /
 *  reader / writer front respectively back end for any applications that 
 *  handle / transform DOM objects without bothering of how to get or get 
 *  rid off.<br />
 *  <br />
 *  A .xsl style sheet transformation can be inserted before output.<br />
 *  <br />
 *  <b>Hint 1</b>:  To this application {@link XMLio} belongs (as integral part)
 *  a .properties file named 
 *  <a href="./doc-files/XMLio.properties" target="_top">XMLio.properties</a>.
 *  It's part of the documentation.<br />
 *  See also the hints in the 
 *  <a href="./package-summary.html">package description</a>.<br />
 *  <br />
 *  <b>Hint 2</b>: If the document to be parsed relates to DTDs 
 *  (document type definitions) given as (web) URLs, like e.g.<br />
 *  &quot;https://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd&quot;<br />
 *  they will be loaded while parsing.<br />
 *  <br />
 *  Besides the possible performance impact, this may require proxy settings.
 *  They can comfortably put in a .properties file.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a> 
 *  Copyright 2001 - 2003, 2007 &nbsp; Albrecht Weinert 
 *  @author   Albrecht Weinert
 *  @version  $Revision: 44 $ ($Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $)
 *  @see App
 *  @see Input
 *  @see XMLconf
 *  @see SAXdemo
 */
 // so far   V00.00 (13.08.2001) : new
 //          V00.01 (27.08.2001) : clearHTMLdefAtts
 //          V00.20 (24.05.2002) : de, AppBase
 //          V01.30 (28.06.2002) : App/WinApp unify, Plug-In
 //          V01.31 (28.06.2002) : stripToRoot (experimental)
 //          V02.00 (23.04.2003) : CVS Eclipse
 //          V02.06 (01.08.2003) : DOM instead of JDOM   
 //          V02.07 (10.08.2003) : include Transforms
 //          V02.13 (14.03.2004) : structlist
 //          V02.27 (30.11.2005) : JDom deleted totally; XMLtrans to here
 //          V02.30 (22.10.2007) : return 1 on non fatal errors
 //          V.o01+ (03.02.2010) : moved to Assembla due to Oracle-Sun
 //          V.009  (21.09.2016) : seen xsl bugs; revJump due to svn server bug
@MinDoc(
   copyright = "Copyright 2001 - 2007, 2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 44 $",
   lastModified   = "$Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $",
   usage   = "start as Java application (-? for help)",  
   purpose = "input [check] [validate] [transform] [and output | list] XML"
) public class XMLio extends App {
   
   private XMLio(){} // no objects; no javaDoc 
     
/** Start method of XMLio. <br />
 *  <br />
 *  Execute : Java XMLio [options] source dest <br />
 *  <br />
 *  Exit code 0: All OK no errors or warnings<br />
 *  Exit code 1: Warnings or non fatal errors occurred; the result may 
 *              nevertheless be usable<br />
 *  Exit code 2 or higher:  Fatal errors occurred or application  or I/O 
 *              failure<br />
 *  <br >
 *  @param  args   command line parameter
 */
   public static void main(final String[] args){
      try {
         new XMLio().go(args);
      } catch (Exception e) {
         AppBase.exit(e, INIT_ERROR);
      }
   } // main(String[])

/** The source file's name or the source URL denomination. <br />
 *  <br />
 *  The content must either be parsable XML text or a serialised DOM depending
 *  on start options.<br />
 *  <br />
 *  default: null (means normal input; as keyboard it makes usually no sense.)
 */
   public String  source;

/** The destination file's name or the denotation of a destination directory. <br />
 *  <br />
 *  {@link #dest} may be a file or a directory. A directory makes sense only
 *  if {@link #source} denotes an input file. In that case name.ser 
 *  respectively name.out is taken as output file name in the destination
 *  directory.<br />
 *  <br />
 *  default: null; null means normal output (usually makes no sense)
 */
   public String  dest;

/** An XSL transformation file's name or URL. <br />
 *  <br />
 *  This names a  eXtensible StyLe sheet responsible for transforming the 
 *  document prior to output.<br />
 *  <br />
 *  default: null; null means no style sheet transformation
 */
   public String  trans;

/** Do a XML parsing instead of de-serialising. <br />
 *  <br />
 *  true is the normal case and the default.<br />
 */
   public boolean parse = true;

/** Serialise the DOM object on output instead of outputting XML text. <br />
 *  <br />
 *  If true the DOM object is output without formatting and transformations.
 *  That is as well a XML text.<br />
 *  <br />
 *  default: true
 */
   public boolean serialise = true;

/** List the DOM object's structure. <br />
 *  <br />
 *  If &gt; 0 the DOM's structure will be listed on output down to the tree
 *  depth / recursion level indicated by {@link #structList}'s value.<br />
 *  <br />
 *  Has precedence before {@link #serialise}.<br />
 *  <br />
 *  default: 0
 */
   public int structList;

/** Working method of XMLio. <br />
 *  <br />
 *  @see #main(String[])
 *  @see de.frame4j.util.App#go
 */
   @Override public int doIt(){
      if (verbose) {
         log.println();
         log.println(twoLineStartMsg().append('\n'));
      }

      XMLdoc xmlDoc = new XMLdoc(this);
      XMLconf xmlConf = xmlDoc.xmlConf;
      xmlDoc.setPublicId(source);
      //  System.gc();

// -----  Input (phase 1)  --------------------------------------

      String vorgang = parse ? "Parse" : "De-serialise";
      boolean proxySet = xmlConf.isProxySet();
      Input ein = null;

      try {
         ein = Input.openFileOrURL(source);
         if (verbose) {
            log.println("\n" + vorgang 
                             + " of : \n" + ein.listLine()); 
            if ((ein.url != null) && proxySet)
            log.println(   "        with : " +  xmlConf.appendProxy(null)); 
         }
      } catch (IOException e) {
         if (proxySet)
            return errMeld(11, xmlConf.appendProxy(null).append('\n')
                                                   .append(e.getMessage())); 
         return errMeld(13, e.getMessage());
      } // try-catch open source

      try {
         if (parse) {
            xmlDoc.parse(ein);
         } else {
            xmlDoc.readFrom(ein);
         }
      } catch (Exception e) {
         return errMeld(23, e.getMessage());
      }

      Node doc = xmlDoc.getDocument();
      ParseErrorHandler parseErrorHandler = xmlDoc.getErrorHandler();
      
      if ( dest == null && structList <= 0 || doc == null ) {
         if (verbose)
            log.println("No output\n");
         if (doc == null) return 4;
         if (parseErrorHandler.errorCount != 0) return 2;
         if (parseErrorHandler.warningCount != 0) return 1;
         return 0;
      }

// ----  Style sheet - Transformation  (if any) ------------------------
      
      boolean transToText = false;
      DOMSource ds = null;
      Transformer     trafo = null;
      
      if (trans != null) { // Style sheet - Transformation
         if (verbose) 
            log.println(" /// Style sheet transformation : " + trans);
         Source transI = null;
         try {
            transI = new StreamSource(trans);
         } catch (Exception e) {
            return errMeld(39, e.getMessage());
         }
         try {
            trafo = xmlConf.makeTransformer(transI);
         } catch (Exception e) {
            return errMeld(41, e.getMessage());
         }
         
         Properties outProp = trafo.getOutputProperties();
         if (outProp != null) {
         if (verbose) {
            log.println(" *** tranformer output : ");
            outProp.list(log);
         }
         transToText = "text".equals(outProp.get("method"));
         }
         
         ds = new DOMSource(xmlDoc.getDocument()); 
         if (transToText) {
            serialise = false;
            
            
         } else { // transform to text else DOM

         
         DOMResult dor = new DOMResult();
         try {
            trafo.transform(ds, dor);
         } catch (TransformerException e) {
            return errMeld(41, e.getMessage());
         }
         xmlDoc.setDocument(dor.getNode());
         
         }
      } // Style sheet - Transformation
      
// -----  Manipulation    ----------------------------------------------
      if (! transToText) xmlDoc.applyTransforms(); // parametrised transforms

// -----  Output         ----------------------------------------------
      File fileOut = null;
      if (dest != null) {
         fileOut = FileHelper.getInstance(dest);
         /// log.println(" ///   TEST  \"" + dest + "\" > " + fileOut);
         
         if (fileOut.isDirectory()) {
            String name = ein.getName();
            if (name == null) {
               return errMeld(13, "No filename for " + dest 
                                                    + " (dir) available");
            }

            name = name + (serialise ? ".ser" : ".out");
            if (verbose) 
               log.println("Output dir. + " + name );
            fileOut = new File(fileOut, name);
         } // directory  

         if (verbose)
            log.println("        to : \n" + fileOut.getPath() );
      } // Ziel != null
      
      if (transToText) {
         StreamResult result = new StreamResult(fileOut);
         try {
            trafo.transform(ds, result);
         } catch (TransformerException e) {
            return errMeld(43, e.getMessage());
         }
         return 0;
      }
     
      try {
         if (structList > 0) {
            PrintWriter opw = fileOut != null 
                ? new PrintWriter(FileHelper.makeOS(fileOut, outMode)) : out;
            MLHelper.listNode(doc, opw, structList);
            return 0;
         }
        
         if (serialise) { 
            if (fileOut != null
                  && xmlDoc.writeTo(FileHelper.makeOS(fileOut, outMode))) {
               log.println("Doc. successfully output (xml)  to " + dest );
            } else                
               log.println("XML-output (unformatted)  to " + dest + " failed");
         } else {
            if (fileOut != null 
                 && xmlDoc.write(FileHelper.makeOS(fileOut, outMode))) {
               log.println("Doc. successfully written (formatted)  to " + dest );
            } else                
               log.println("XML-output (formatted)  to " + dest + " failed");
         }
      } catch (Exception e) {
         return errMeld(31, e);  
      }
      return 0;
   } // doIt(

} // XMLio (10.08.2003, 13.03.2004, 27.03.2008, 06.02.2009)
