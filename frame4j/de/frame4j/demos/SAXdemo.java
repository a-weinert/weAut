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
package de.frame4j.demos;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import de.frame4j.XMLio;
import de.frame4j.io.Input;
import de.frame4j.util.App;
import de.frame4j.util.AppBase;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;
import de.frame4j.xml.SAXHandler;
import de.frame4j.xml.XMLconf;
import de.frame4j.xml.XMLdoc;

/** <b>Java XML demonstration application for SAX</b>. <br />
 *  <br />
 *  This application generates a {@link javax.xml.parsers.SAXParser} that is
 *  widely configurable. An input to this application is read and then parsed
 *  by taking the parser's {@link org.xml.sax.XMLReader}.<br />
 *  <br />
 *  As a demonstration the inputs XML elements are counted and their frequency
 *  (by sorted name) is listed. Apart from the manifoldly configurable 
 *  (grammar) checks {@link SAXdemo} does not much useful. The source, 
 *  <a href="./doc-files/SAXdemo.java" target="_top">SAXdemo.java</a>, and the 
 *  <a href="./doc-files/SAXdemo.properties"
 *  target="_top">SAXdemo.properties.properties</a> may be a good starting 
 *  point for XML applications based on 
 *  <a href="../package-summary.html">Frame4J</a>.<br />
 *  <br />
 *  <br />
 *  Copyright <a href="../package-summary.html#co">&copy;</a>
 *   2001 - 2005, 2009 &nbsp; Albrecht Weinert <br />
 *  <br />
 *  @see XMLio
 *  @see XMLdoc
 *  @see XMLconf
 *  @see SAXHandler
 */
 // so far: V02.23 (11.09.2004) : new
 //         V02.25 (26.04.2005) : SAXHandler use anonym. (l-38%)
 //         V.o56+ (03.02.2009) : ported to Kenai (SVN)
 //         V.o01+ (03.02.2010) : to Assembla due to Sun-Oracle's Kenai kill

@MinDoc(
   copyright = "Copyright 2004, 2005, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 44 $",
   lastModified   = "$Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "start as Java application (-? for help)",  
   purpose = "a demo and introductory application for SAX XML parsers"
) public class SAXdemo extends App {

/** Conditions for help output. <br />
 *  <br />
 *  This implementation makes the on-line help shown, when {@link #xmlFile} 
 *  is null, additionally to {@link #help} (by option -? or -help).t.<br />
 */
    @Override protected boolean isHelpLog(){
       return help || xmlFile == null;
    } // isHelpLog()

/** Start method of SAXDemo. <br />
 *  <br />
 *  Return-Code:<br />
 *  Exit 0, when run successfully  <br />
 *  <br />
 *  @param  args command line arguments  <br />
 */
   public static void main(final String[] args){
      try {
         new SAXdemo().go(args);
      } catch (Exception e) {
         AppBase.exit(e, 14);
      }
   } // main(String[])
    
    
//-----  Properties, automatic set by Prop's parameter parsing   --
   
/** Display the frequency of names. <br />
 *  <br />
 *  default: true<br />
 */   
   public boolean showCount = true;

/** Stop on errors. <br />
 *  <br />
 *  default: false<br />
 */   
   public boolean  stopOnError; 

/** The XML file (or URL); the input. <br /> */   
   public String xmlFile;

//--------------------------------------------------------------------------
   
/** Parser's properties. <br />
 *  <br />
 *  The XML parser's properties will be initialised, indirectly with
 *  {@link #prop}, according to start parameters.<br />
 */   
   protected XMLconf xmlConf;
   
/** The SAX parser. <br />
 *  <br />
 *  The {@link javax.xml.parsers.SAXParser SAXParser} will be made as
 *  {@link XMLReader} by {@link #xmlConf}'s method 
 *  {@link XMLconf#makeXMLReader(SAXHandler)}.<br />
 *  An object of a very simple anonymous inner class extending 
 *  {@link SAXHandler} serves as {@link #handler}.
 */   
   protected XMLReader xmlReader;
   
/** The XML input. <br />
 *  <br />
 *  Will be made according to {@link #xmlFile} and the proxy properties of
 *  {@link #xmlConf}. {@link #inp} may be a file or URL.<br />
 */   
   protected Input inp;
   
/** The error and content handler. <br />
 *
 *  @see #xmlReader
 */   
   protected SAXHandler handler; 
   
/** Hash table for counting names. <br />
 *  <br />
 *  Will be handled as &lt;String, Integer&gt; map:<br />
 *  key:  tag respectively tag/attribute<br />
 *  value: &nbsp; it's frequency in {@link #xmlFile} 
 *  respectively {@link #inp}.<br />
 */
   protected HashMap<String, Integer> tagCounts;


/** Working method of SaxDemo. <br /> */
   @Override public int doIt(){
      log.println();
      if (verbose) { // verbose
         log.println(twoLineStartMsg().append("\n XML ")
            .append(valueLang("input", "Input")).append(": ")
            .append(xmlFile).append('\n'));
      } // verbose

      xmlConf = new XMLconf(prop);    /// XML-Parser- etc. configuration
      boolean proxySet = xmlConf.isProxySet();

      try {
         inp = Input.openFileOrURL(xmlFile);
         if (verbose && proxySet && inp.url != null) {
            log.println("  " + xmlConf.appendProxy(null)); 
         }
      } catch (IOException e) {
         if (proxySet)
            return errMeld(11, xmlConf.appendProxy(null).append('\n')
                                                   .append(e.getMessage())); 
         return errMeld(13, e.getMessage());
      } // try-catch open source

//--- Application specific SAX implementation  --  begin   ---------------       
      
      handler = new SAXHandler(xmlFile, log, stopOnError) {

/** Start of a XML element. <br />
 *  <br />
 *  This Implementation in this anonymous inner class counts the frequency of
 *  local names.<br />
 */
         @Override public void startElement(final String namespaceURI,
                                  final String localName, final String qName,
                                  final Attributes atts) throws SAXException {
               countName(localName);
            if (atts != null) {
                  int len = atts.getLength();
                  for (int i = 0; i < len; ++i) { // through the attributes
                     String attName = atts.getLocalName(i);
                     countName(localName + '\\' + attName); 
                  } // through the attributes
            }
         } // startElement(String)
         
      };  // SAXHandler inheritor   ===================

//--- Application specific SAX implementation  ---  end  ----------------       
      
      try {  // Create a JAXP SAXParser // content, error handler
         xmlReader =    xmlConf.makeXMLReader(handler); 
      } catch (ParserConfigurationException e) {
         return errMeld(17, e);                // making so impossible
      } catch (SAXNotRecognizedException e2) { // unknown name
         // happens only, if parser does not support JAXP 1.2
         return errMeld(21, e2); // can not happen with JDK > 1.3
      }  catch (SAXNotSupportedException e3) { // unknown value
        return errMeld(23, e3);    // Schema's name unknown
      } catch (SAXException e) {   // all others
         return errMeld(19, e);
      }   // Create a JAXP SAXParser
       
      tagCounts = new HashMap<String, Integer>(271);
      InputSource ips = new InputSource(inp);

      try {
         xmlReader.parse(ips);
      } catch (IOException e3) {
         return errMeld(29, e3); // IO problem
       } catch (SAXException e3) {
         return errMeld(31, e3); // SAX problem, not to be handled
      }

      handler.report();
      
      if (! showCount || tagCounts.isEmpty()) return 0;
      
      Set<String> keySet = tagCounts.keySet();
      String[] keyA = keySet.toArray(ComVar.NO_STRINGS);
      Arrays.sort(keyA);
      
      log.println("\n\n No. \t| local name [\\ attribute]");

      for (String nam :  keyA) {
         int frq =  tagCounts.get(nam); // box out
         log.println("  " + frq + "  \t| \"" + nam + "\"");
      }
      log.println("\n\n");
      return 0;
   } // doIt()

/** Integer 1. */
   public final Integer INT1 = Integer.valueOf(1); 
   
/** Count a name. <br /> */
   protected void countName(final CharSequence name){
      String key = TextHelper.trimUq(name, null);
      if (key == null) return;
      Object value = tagCounts.get(key);
      if (value == null) {  // not yet in
          tagCounts.put(key, INT1); // Add a new entry
          return;
      }
      int count = ((Integer)value).intValue() + 1;
      tagCounts.put(key, Integer.valueOf(count));
   } // countName(CharSequence)

} // SAXDemo (21.05.2009)
