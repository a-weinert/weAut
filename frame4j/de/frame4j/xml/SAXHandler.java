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

import java.io.PrintWriter;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import de.frame4j.graf.GrafHelper.MBarFactory;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;

/** <b>An event handler for SAX applications</b>. <br />
 *  <br />
 *  Extending {@link ParseErrorHandler}, objects of this class are error 
 *  handlers for XML parsers. Additionally they implement a content handler
 *  for SAX parser, hence being able to act as the one (and only) common
 *  handler for SAX applications.<br />
 *  <br />
 *  As the error handler behaviour most often can be utilised unchanged, the
 *  content handler part has at least partly be overridden in most cases, 
 *  {@link #startElement(String, String, String, Attributes)}. For many 
 *  application cases this as only change in an inheritor is sufficient.<br />
 *  Note that all other implementations of {@link org.xml.sax.ContentHandler}
 *  methods in this class do nothing.<br />
 *  <br />
 *  The (inner) class {@link MBarFactory  GrafHelper.MBarFactory} is one 
 *  example of a class extending {@link SAXHandler}. By comparably simple 
 *  overriding of just {@link #startDocument()},
 *  {@link #startElement(String, String, String, Attributes) startElement()}
 *  and {@link #endElement(String, String, String) endElement()} it is a
 *  powerful factory for {@link java.awt.MenuBar}s or 
 *  {@link javax.swing.JMenuBar}s.<br />
 *  <br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2005, 2009 &nbsp; Albrecht Weinert<br />  
 *  <br />
 *  @author   Albrecht Weinert
 *  @version  $Revision: 33 $ ($Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $)
 *  @see      de.frame4j
 */
 // so far:   V02.00 (26.04.2005 09:32) :  new
 //           V02.06 (03.06.2008 08:29) :  /**
 //           V.o63+ (02.02.2009 08:26) : Frame4J

@MinDoc(
   copyright = "Copyright  2005, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use as is or extend  for own (SAX) XML parsers",  
   purpose = "provide a comfortable (base for) XML parsers"
) public class SAXHandler extends ParseErrorHandler
                     implements  org.xml.sax.ErrorHandler, ContentHandler {

/** SAXHandler by name of the XML input and with report output. <br />
 *  <br />
 *  A {@link SAXHandler SAXHandler} object will be made named according to
 *  the parameter  {@link #name} or &quot;...&quot;.<br />
 *  <br />
 *  As PrintStream {@link #log} the parameter {@code log} is taken if not
 *  null and the (decorated) normal output (System.out) otherwise.<br />
 *  <br />
 *  @param name the handler's or the input's name (stripped from surrounding 
 *              white space; null or empty is taken as &quot;...&quot;
 *  @param log  destination for reports
 *  @param reThrow if true error exceptions are thrown (again) and not just
 *                 reported
 *  @see #init(CharSequence, boolean)
 @see ParseErrorHandler#ParseErrorHandler(CharSequence, PrintWriter, boolean)
 */
   public SAXHandler(CharSequence name, PrintWriter log, boolean reThrow) {
      super(name, log, reThrow);
   } // SAXHandler(CharSequence, PrintWriter, boolean)

 /** Deliver the product of the (ready, successful) parsing. <br />
  *  <br />
  *  May or must be overridden in extending (especially anonymous) classes if
  *  necessary.<br />
  *  <br />
  *  This implementation returns null.<br />
  *  <br />
  *  @return null or object of factory's product type
  */  
   public Object getProduct(){ return null; }
   
//--------------------------  content handler ------------------------------
   
/** Start of a XML elements. <br />
*  <br />
*  This implementation lists name an all attributes of the visited 
*  element on {@link #log}. This method and hence this class is a first useful
*  test step for the intended SAX application.<br />
*  For all other purposes this is the method that has to be overridden in an
*  inheritor. (Chances are good that it is the only one, if not to complex
*  state machines are sufficient.)<br />
*  <br />
@see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
*/
   @Override public void startElement(String namespaceURI,
                           String localName, String qName,
                           Attributes atts) throws SAXException {
      String tmp = TextHelper.trimUq(localName, qName);  
      log.println(" ** element < " + tmp);
      if (atts != null) {
         int len = atts.getLength();
         for (int i = 0; i < len; ++i) {
            tmp = atts.getLocalName(i);
            if (tmp == null || tmp.length() == 0)
               tmp = atts.getQName(i);
            log.println("   **  ...... " + tmp + "\t = " +  atts.getValue(i)); 
         } // for over attributes
      }
   } // startElement(3*String, Attributes)


// Hint: All other ContentHandler methods do nothing.

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#endDocument()
 */
   @Override public void endDocument() throws SAXException { }


/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#startDocument()
 */
   @Override public void startDocument() throws SAXException { }

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#characters(char[], int, int)
 */
   @Override public void characters(char[] ch, 
           int start, int length) throws SAXException {  }

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
 */
   @Override public void ignorableWhitespace(char[] ch, int start, int length)
                                                  throws SAXException { }

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
 */
   @Override public void endPrefixMapping(String prefix) 
                                                         throws SAXException { }

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
 */
   @Override public void skippedEntity(String name) throws SAXException { }

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
 */
   @Override public void setDocumentLocator(Locator locator) { }

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#processingInstruction(String, java.lang.String)
 */
   @Override public void processingInstruction(String target,
                      String data) throws SAXException {  }

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
 */
   @Override public void startPrefixMapping(String prefix, String uri)
                                              throws SAXException { }

/** This implementation does nothing. <br />   
 *  @see org.xml.sax.ContentHandler#endElement(String, String, String)
 */
   @Override public void endElement(String namespaceURI,
                      String localName, String qName) throws SAXException { }

} // class SAXHandler (26.04.2005, 06.02.2009)
