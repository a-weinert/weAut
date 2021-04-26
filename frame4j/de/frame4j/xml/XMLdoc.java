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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.frame4j.io.Input;
import de.frame4j.util.App;
import de.frame4j.util.MinDoc;
import de.frame4j.util.PropMap;

/** <b>A XML document (DOM)</b>. <br />
 *  <br />
 *  Objects of this class contain or encapsulate a  
 *  {@link org.w3c.dom.Document (DOM-) Document} as well as properties and
 *  methods for input, manipulation and output. {@link XMLdoc} thereby
 *  facilitates the (partly laborious and restricted) handling of 
 *  DOM (org.w3c.dom) documents.<br />
 *  <br />
 *  The motive of going to JDom is hence mostly gone by this class, that
 *  (in the predecessor framework) had been a &quot;come back from JDom
 *  enabler&quot;. Also, the serialisation, not supported by 
 *  {@link org.w3c.dom.Document (DOM-) Document} (but by JDom) is supported
 *  here &mdash; and is still for those historical reasons. In between this
 *  feature seems a bit questionable in the light of XML itself being a (the)
 *  serialisation format.<br />
 *  <br />
 *  The  org.w3c.dom.{@link org.w3c.dom.Document Document} encapsulated in
 *  an object of this class is of the more common (parent) type 
 *  {@link org.w3c.dom.Node Node}, as a matter of fact. But it is settable
 *  or available just as {@link org.w3c.dom.Document Document},
 *  {@link org.w3c.dom.DocumentFragment DocumentFragment} or
 *  {@link org.w3c.dom.Element Element}.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2001 - 2005, 2009  &nbsp; Albrecht Weinert<br />  
 *  <br />
 */
 // so far:  V00.00 (27.08.2001 11:04) :  new
 //          V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //          V02.05 (23.07.2003 16:56) :  DOM and (still) JDOM
 //          V02.16 (25.03.2003 17:24) :  /**
 //          V02.24 (30.11.2005 17:22) :  JDom thrown away; and XMLtrans
 //          V02.28 (03.06.2008 08:34) :  /**
 //          V.o61+ (03.02.2009 11:50) :  ported to Frame4J (and Kenai SVN)
 //          V.o01+ (03.02.2010 12:11) :  moved to Assembla due to Oracle-Sun

@MinDoc(
   copyright = "Copyright   2001 - 2005, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use in XML / HTML applications",  
   purpose = "represents a XML document"
) public class XMLdoc implements Cloneable, Serializable {

/** Make with standard settings. <br />
 *  <br />
 *  An {@link XMLdoc} object without a {@link #getDocument document} with log
 *  output and all properties in default setting will be made.<br />
 *  <br />
 *  As PrintStream {@link #log} the parameter {@code log} is taken or, if 
 *  null, the  normal output (decorated System.out).<br />
 *  <br />
 *  @param log  destination of reports
 */
   public XMLdoc(PrintWriter log){
      this.log = log != null ? log : new PrintWriter(System.out);
      xmlConf = new XMLconf();
   } // XMLdoc(PrintWriter

/** Make for an application (App inheritor). <br />
 *  <br />
 *  An {@link XMLdoc} object without a {@link #getDocument document} will be 
 *  made.<br />
 *  <br />
 *  The log output and other properties will be taken from the application,
 *  the {@link App} inheritor, supplied.<br />
 *  <br />
 *  @param app the application get output streams and properties from
 *  @see #set(PropMap)
 *  @throws NullPointerException if app is null
 */
   public XMLdoc(App app) throws NullPointerException {
      this(app.log);
      xmlConf.set(app.getProp());
   } // XMLdoc(App)
   

/** Parser properties &mdash; plus some others. <br />
 *  <br />
 *  All parser, input, output, transformer and else properties wanted / used
 *  for this {@link XMLdoc} object lie in this associated {@link XMLconf} 
 *  object. It will be set on construction by {@link #XMLdoc(App)} according
 *  to the  {@link App}lication's properties 
 *  ({@link App#getProp() app.prop}).<br />
 *  <br />
 *  @see #set(PropMap)
 */   
   public final XMLconf xmlConf;



/** Set the properties by a PropMap object. <br />
 *  <br />
 *  @param prop source of the properties to be used
 *  @throws IllegalArgumentException parameter value problems
 *  @see XMLconf#set(PropMap)
 *  <br />
 */
   public void set(PropMap prop) throws IllegalArgumentException {
      if (prop == null) return;
      xmlConf.set(prop);
   } // set(PropMap)
   

//-------------------    Document,  Serialising, clone ----------------
   
/** The document. */
   transient Node document; // transient as not serialisable

/** The document (contained). <br />
 *  <br />
 *  It is of type {@link Document}, {@link DocumentFragment} or
 *  {@link Element} or it is null.<br />
 */
   public  Node getDocument(){ return document; }

/** The node (contained) is a document or a part. <br />
 *  <br />
 *  Returns true, if {@link #getDocument document} is not null and of type 
 *  {@link Document}, {@link DocumentFragment} or
 *  {@link Element}  (that is it may have children nodes).<br />
 */
   public final boolean isDocNode(){
      return MLHelper.isDocNode(this.document);
   } // isDocNode()
   
   
/** Set the document.<br />
 *  <br />
 *  @param document  the new document (DOM tree or null).<br />
 *          document has to be of type Document, DocumentFragment or Element
 * @throws ClassCastException on wrong type; <br />&nbsp; &nbsp; 
 *     this.{@link #getDocument  document} is null then
 */
   public final void setDocument(Node document) throws ClassCastException {
      if (this.document == document) return;
      if (document == null || MLHelper.isDocNode(document)) {
         this.document = document;
         return;
      }
      this.document = null;
      throw new ClassCastException(
           "document not  Document[Fragment], Element or null");
   } // setDocument(Node)   


/** The document's name (just for report and documentation). <br />
 *  <br />
 *  It is either not empty or it is null.<br />
 */  
   public final String getPublicId(){ return publicId; }

/** The document's name (just for report and documentation). <br />
 *  <br />
 *  If {@code publicId} is not empty it is taken as new 
 *  {link #getPublicId publicId}. Otherwise that is set null.<br />
 */  
   public void setPublicId(String publicId){
      this.publicId = publicId == null 
                       || publicId.length() == 0 ? null : publicId;
   } // setPublicId(String
   
/** The document's name (just for report and documentation). <br /> */  
   String publicId;
  
/** A copy of this object. <br />
 *  <br />
 *  This  XMLdoc object is cloned deeply, including a deep clone of an 
 *  associated {@link #getDocument document}.<br />
 *  <br />
 */
   @Override public XMLdoc clone(){
      XMLdoc ret = null;
      try {
         ret = (XMLdoc) super.clone();
         if (document == null) return ret;
         ret.document = document.cloneNode(true); 
      } catch (CloneNotSupportedException e) {} // can not happen
      return ret;
   } // clone
 

/** Writer (output) for error and warning reports. <br />
 *  <br />
 *  Is set finally on construction. It is never null; default is (decorated)
 *  System.out.<br />
 */
   protected final PrintWriter log;
 

//------------------  Serialising ------------------------------------

/** Write the XMLdoc. <br />*/
   private void writeObject(ObjectOutputStream oos) throws IOException {
      oos.defaultWriteObject();  
      final boolean docIsNull = document == null;
      oos.writeBoolean(docIsNull);
      if (!docIsNull) {
         doc2xml(document,  oos, NO_CLOSE);  
      }
   } // writeObject(ObjectOutputStream

/** Read the XMLdoc. */
   private void readObject(ObjectInputStream ois) 
                     throws IOException, ClassNotFoundException {
      ois.defaultReadObject();       
      boolean docIsNull = ois.readBoolean();
      if (!docIsNull) {
         if (xmlInputer == null) getXMLInputer();
         synchronized (xmlInputer) {
            document = xml2doc(ois);
            if(document == null) {
               throw new ClassNotFoundException(
                 "DOM document could not be read: " 
                    + lastXml2docError.getMessage());
            }
         } // sync
      }
   } // readObject(ObjectInputStream)
 
//--------- Parse    --------------------------------------------

/** ErrorHandler for parsing. <br > */
   volatile ParseErrorHandler errorHandler; 

/** ErrorHandler for parsing. <br > */
   public void setErrorHanlder(ParseErrorHandler errorHandler) {
      this.errorHandler = errorHandler; 
   } // setErrorHanlder(DocumentBuilderErrorHandler

/**  rrorHandler for parsing.. <br > */
   public ParseErrorHandler getErrorHandler(){  return this.errorHandler; }

   
/** ErrorHandler for parsing. <br >
 *  <br />
 *  If an ErrorHandler was already set it is returned.<br />
 *  <br />
 *  Otherwise a {@link ParseErrorHandler} is made with (hopefully set
 *  accordingly) properties log and {@link #getPublicId publicId}.<br />
 *  <br />
 *  @return the ErrorHandler set
 */
   ParseErrorHandler getErrorHandler(boolean reThrow){
      if (errorHandler != null) return errorHandler;
      errorHandler = new ParseErrorHandler(publicId, log, reThrow);
      return errorHandler;
   } // getErrorHandler()


/** Parse from an input stream. <br />
 *  <br />
 *  @param is    the XML input stream to be parsed 
 *  @throws IllegalArgumentException (forwarded)
 *  @throws IOException stream problem
 *  @throws SAXException (forwarded from parsing)
 *  @throws FactoryConfigurationError XML problem
 *  @throws ParserConfigurationException XML prolem
 *  @see #parse(org.xml.sax.InputSource) parse(InputSource)
 */
   public void parse(InputStream is)
         throws  IllegalArgumentException, IOException,
         SAXException, FactoryConfigurationError,
         ParserConfigurationException {
     if (is == null) throw new IOException("null is no usable input");
     InputSource ips = new InputSource(is);
     parse(ips); 
   } // parse(InputStream)  


/** Parse from a file or URL. <br />
 *  <br />
 *  @param fileOrURL names a file or URL as XML input 
 *  @param encoding  the input's encoding (character set)
 *  @see #parse(org.xml.sax.InputSource) parse(InputSource)
 *  @see XMLconf#setProxyPort(CharSequence)
 *  @see XMLconf#setProxyHost(CharSequence)
 *  @throws IllegalArgumentException (forwarded)
 *  @throws IOException stream problem
 *  @throws SAXException (forwarded from parsing)
 *  @throws FactoryConfigurationError  XML problem
 *  @throws ParserConfigurationException  XML problem
 */
   public void parse(String fileOrURL, String encoding)
         throws  IllegalArgumentException, IOException,
         SAXException, FactoryConfigurationError,
         ParserConfigurationException {
     if (fileOrURL == null || fileOrURL.length() == 0)
        throw new IOException ("empty name for file or URL");
     
     xmlConf.setProxySet(xmlConf.proxySet);   
     if (encoding == null && xmlConf.impEncoding != null) 
        encoding = xmlConf.impEncoding;

     Input ein = Input.openFileOrURL(fileOrURL);
     String inAsS = ein.getAsString(encoding); 
     StringReader str = new StringReader(inAsS);
     InputSource ips = new InputSource(str);
     parse(ips); 
   } // parse(String fileOrURL, String  
  


/** Parse from an input source. <br />
 *  <br />
 *  All  (DOM-) parse() methods allowing for other input types make an
 *  InputSource in the end (and delegate to here).<br />
 *  <br />
 *  This method is either successful or throws an Exception, see also
 *  {@link XMLconf#makeDocumentBuilder(ParseErrorHandler)}.<br />
 *  <br />
 *  @param  is the XML input
 *  @throws IllegalArgumentException (forwarded)
 *  @throws IOException stream problem
 *  @throws SAXException (forwarded from parsing)
 *  @throws FactoryConfigurationError  XML problem
 *  @throws ParserConfigurationException  XML problem
 */
   public void parse(InputSource is) 
      throws  IOException, SAXException, IllegalArgumentException,
      FactoryConfigurationError, ParserConfigurationException {

      if (is == null) throw new IOException ("null is no usable input");
      
      ParseErrorHandler dbeh = getErrorHandler(false); // ToDO
      DocumentBuilder sab = xmlConf.makeDocumentBuilder(dbeh);
         
      SAXException saxe = null;
      try {
         document = sab.parse(is);
      } catch (SAXException e) {
         saxe = e;
         Exception u = e.getException();
         log.println("DOM-Parser parsing Error : " 
               + e.getMessage());
         if (u != null) 
         log.println("DOM-Parser embedded - Error : " 
           + u.getMessage());
      }
      dbeh.reportErrors();
      if (saxe != null) throw saxe;
   } // parse(InputStream )

//-------  Simple reading and writing -----------------------------------   

/** Get (in facsimile) from an input stream. <br />
 *  <br />
 *  The DOM tree is fetched directly and without any validation from 
 *  {@code is}.<br />
 *  <br />
 *  @param is   the input stream to read the DOM from
 *  @throws IOException  stream or file problems
 *  @throws SAXException XML problems
 *  @throws ClassNotFoundException no DOM could be made
 *  @see #xml2doc xml2doc() 
 */
   public void readFrom(InputStream is) 
        throws IOException, SAXException, ClassNotFoundException {
      
      if (xmlInputer == null) getXMLInputer();
      synchronized (xmlInputer) {
         document = xml2doc(is);
         if (lastXml2docError != null)
            throw lastXml2docError;
         if(document == null) {
            throw new ClassNotFoundException( // should be handled
              "DOM document could not be read" );
         }
      } // sync
   } // readFrom
 

/** Write (in facsimile) to an output stream. <br />
 *  <br />
 *  <br />
 *  The DOM tree is written directly and without any special formating or 
 *  transforming to the OutputStream {@code os}.<br />
 *  <br />
 *  The only property influencing this simple proceeding is
 *  {@link XMLconf#encoding encoding}.<br />
 *  <br />
 *  @param os the output stream to write the DOM as XML to
 *  @return false if nothing (no XML) could be written (e.g. due to wrong
 *          parameter values 
 *  @throws IOException  stream or file problems
 *  @see #doc2xml doc2xml()
 *  @see #write write()
 */
   public boolean writeTo(OutputStream os) throws IOException { 
      if (os == null || document == null) return false;
         return doc2xml(document, os, xmlConf.encoding);
   } // writeTo

//-------- Transformations -------------------------------------


/** Apply the transformation. <br />
 *  <br />
 *  THe transformations defined by all transformation properties will be 
 *  applied to the embedded DOM document.<br />
 *  <br />
 *  @return true if the transformations were applied successfully 
 *  @see XMLconf#clearDefAtts
 *  @see XMLconf#textNormalize
 *  @see MLHelper#clearHTMLdefAtts(Element)
 *  @see org.w3c.dom.Node#normalize() Node.normalize()
 */
  public boolean applyTransforms(){
      if (document == null) return false;
      if (xmlConf.clearDefAtts && document instanceof Document) {
         Element root = ((Document)document).getDocumentElement();
         int n = MLHelper.clearHTMLdefAtts(root);
         log.println("cleared " + n + " html-DefAtts of " + document );
      }
      if (xmlConf.textNormalize) {
         ///   log.println("  ///  TEST normalize  " + document );
         document.normalize();
      }
      return true; 
   } // applyTransforms
   
//----------- Output -------------------------------------------------   

/** Write (with transformations) to an output stream.. <br />
 *  <br />
 *  The DOM tree will be output to the stream {@code os} using all 
 *  formating etc. specified by the output properties.<br />
 *  <br />
 *  If the transformations are wanted and not yet done, 
 *  {@link #applyTransforms()} must be called before this method.<br />
 *  aufzurufen.<br />
 *  <br />
 *  @param os the stream to write to
 *  @return true if the XML was written successfully
 *  @throws IOException  file or stream problems
 *  @throws TransformerException errors while applying the transformations 
 *  @see #writeTo writeTo()
 */
   public boolean write(OutputStream os) 
                              throws IOException, TransformerException { 
      if (os == null || document == null) return false;
      if (!MLHelper.isDocNode(document)) return false;
      
  ////    applyTransforms();

      if (xmlOutputer == null) getXMLOutputer();  
      if (xmlOutputer == null) return false;
      synchronized (xmlOutputer) {
         xmlOutputer.setOutputProperty(OutputKeys.ENCODING, xmlConf.encoding);
         xmlOutputer.setOutputProperty(OutputKeys.INDENT, 
                              xmlConf.indentSize > 0 ? "yes" : "no");
         xmlOutputer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                              xmlConf.omitDeclaration ? "yes" : "no");
 //// xmlOutputer.setOutputProperty(OutputKeys.METHOD , "html"); //// TEST

         DOMSource ds = new DOMSource(document); 
         StreamResult oos =  new StreamResult(os);
         xmlOutputer.transform(ds, oos);
         os.close();   
      }
      return true;   
   } // writeTo

  
   
//----------------   static (XmlHelper) methods --------------------   


/** Supply a DOM -&gt; XML - output with no transformation. <br />
 *  <br />
 *  This method returns always the same simple &quot;one to one 
 *  transformer&quot;.<br />
 *  <br >
 *  That transformer is not threadsafe. The output properties of the last
 *  use remain set in each case.<br >
 *  <br />
 *  The guaranteed usage in one thread only is OK. One may also (better) 
 *  synchronise the proceeding with this object, that is for 
 *  {@link javax.xml.transform.Transformer#setOutputProperty
 *  setOutputProperty()}, etc. until  
 *  {@link javax.xml.transform.Transformer#transform
 *  transform()} respectively {@link #doc2xml doc2xml()}. 
 *  Other methods using this singleton object do this also.<br />
 *  <br />
 *  @return the simple (singleton) XML outputer 
 *          = &quot;one to one transformer&quot;<br />
 *          null will only returne if the whole XML installation (JDK/JRE)
 *          is insufficient
 */
   public static Transformer getXMLOutputer() {
      if (xmlOutputer == null) synchronized (XMLdoc.class){
         if (xmlOutputer != null) return xmlOutputer;
         TransformerFactory tf = 
               TransformerFactory.newInstance();
         try {
            xmlOutputer = tf.newTransformer();
         } catch (TransformerConfigurationException e) {
            // do nothing here; Error ==> return null
         } // may not happen, there's no transformation here
      } // null sync
      return xmlOutputer;
   } // getXMLOutputer()
   private static volatile Transformer xmlOutputer; 

/** Output coding &quot;UTF-8 without closing the output stream&quot;. <br />
 *  <br />
 *  This (pseudo-) output encoding signalises to output methods like 
 *  {@link #doc2xml(Node, OutputStream, String)} or 
 *  {@link #writeTo(OutputStream)} to use UTF-8 and not close the output.<br />
 *  <br />
 *  The rationale behind this somewhat exotic approach is the XML 
 *  serialisation of many objects to one {@link ObjectOutputStream}.<br />
 *  N.B.: {@link XMLdoc} is {@link Serializable serializable}.
 *  <br />
 *  Value: &quot;NO_CLOSE&quot;
 */   
   public static final String NO_CLOSE = "NO_CLOSE";

/** Output (facsimile) a DOM tree as XML.<br />
 *  <br />
 *  The output will be closed by this method after work done. This will 
 *  not happen if {@code encoding} is equal to 
 *  {@link #NO_CLOSE &quot;NO_CLOSE&quot;}; in that case the encoding is 
 *  UTF-8.<br />
 *  <br />
 *  @param doc  the DOM tree to output; it's the starting node and it has to
 *              be of type Document, DocumentFragment or Element
 *  @param os   the destination, the output; it will be closed afterwards
 *              (except when ... see above)
 *  @param encoding the output encoding (default UTF-8); ISO-8859-1 and some
 *          others are possible too
 *  @return true on success
 *  @throws IOException on output problems with os
 */
   public static boolean doc2xml(Node doc, 
           OutputStream os, String encoding) throws IOException {
      if (doc == null || os == null ) return false;
      if (!(doc instanceof Document 
            || doc instanceof DocumentFragment
             || doc instanceof Element)) return false;
      boolean noClose = NO_CLOSE == encoding || "NO_CLOSE".equals(encoding);
      if (noClose || encoding == null || encoding.length() == 0) {
         encoding = "UTF-8";     
      }
      if (xmlOutputer == null) getXMLOutputer();  
      if (xmlOutputer == null) return false;
      boolean res = true;
      synchronized (xmlOutputer) {
         xmlOutputer.setOutputProperty(OutputKeys.ENCODING, encoding);
         DOMSource ds = new DOMSource(doc); 
         StreamResult oos =  new StreamResult(os);
         try {
            xmlOutputer.transform(ds, oos);
         } catch (TransformerException e) {
            res = false; // should not happen, 1:1, no transformation
         }
         if (!noClose) os.close();   
      }
      return res;   
   } // doc2xml
   
   
 
//-------

/** Make a DOM parser. <br />
 *  <br />
 *  A DOM parser will be supplied; all its properties, except validating and
 *  namespace, will be set to its default in each case. If both parameters
 *  {@code valid} and {@code namespace} are false this will be the most simple
 *  or most direct (1 to 1) translation from XML input to DOM tree (compare
 *  {@link #doc2xml doc2xml()} and {@link #getXMLOutputer()}).<br />
 *  <br /> 
 *  @param valid true: validating
 *  @param nameSpace true: namespace aware
 *  @param log Writer for error reports (if any); may be null
 *  @return a parser (on success, or null)
 */   
   public static DocumentBuilder makeBuilder(
        boolean valid, boolean nameSpace, PrintWriter log) {
      DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
      try {
         dBF.setValidating(valid);
         dBF.setNamespaceAware(nameSpace);
         return  dBF.newDocumentBuilder();    
      } catch (FactoryConfigurationError fCE) {
         if (log != null) 
            log.println("DOM-Parser-Factory-Error (fatal) " 
               + fCE.getMessage());
      } catch (ParserConfigurationException pex) {
         if (log != null) 
            log.println("DOM-Parser not configurable " 
               + pex.getMessage());
      }
     return null; // failure 
   } // makeBuilder

   
/** Make a simple XML -&gt; DOM input. <br />
 *  <br />
 *  This method returns always the same simple &quot;one to one parser&quot;
 *  without validation, namespace awareness and the like 
 *  schnick-schnack.<br />
 *  <br >
 *  That parse is not threadsafe.<br >
 *  Usage in one thread is OK. Better is the synchronisation of the 
 *  proceeding with the object. Other Frame4J methods using this singleton
 *  parser do this also.<br />
 *  <br />
 *  @return the simple 1-to-1 parser. null is returned on failure; that
 *          normally means an insufficient XML implementation in the 
 *          JDK/JRE
 */
   public static DocumentBuilder getXMLInputer() {
      if (xmlInputer == null) synchronized (XMLdoc.class){
         if (xmlInputer != null) return xmlInputer;
         xmlInputer = makeBuilder(false, false, null);
      } // null sync
      return xmlInputer;
   } // getXMLInputer()
   
   private static volatile DocumentBuilder xmlInputer; 
   static volatile SAXException lastXml2docError;

/** Fetch last XML error of {@link #xml2doc xml2doc()} call (if any). <br />
 *  <br />
 *  This method is not threadsafe. It just returns the last Exception
 *  internally caught. If the whole input proceeding is synchronised by the 
 *  {@link #getXMLInputer() xmlInputer} the method will give the right answer
 *  within the synchronized block.<br />
 *  <br />
 * @return den ggf. Fehler (SAXException) or null;
 */
   public static SAXException getLastXml2docError(){
         return lastXml2docError;
   }


/** Input (facsimile) from XML to DOM tree.<br />
 *  <br />
 *  The InputStream {@code is} will be parsed without any validating,
 *  namespace awareness and the like and the DOM object made in the process
 *  is returned. This method is the complement to
 *  {@link #doc2xml doc2xml()}.<br />
 *  <br />
 *  @param is  the input
 *  @return the document  or null on failure
 *  @throws IOException on input problems with is
 */
   public static Document xml2doc(InputStream is) throws IOException {
      if (is == null ) return null;
      if (xmlInputer == null) getXMLInputer();
      synchronized (xmlInputer) {
         lastXml2docError = null;
         try {
            return xmlInputer.parse(is);
         } catch (SAXException e) {
            lastXml2docError = e;
            return null;
         }
      }
   } // xml2doc(InputStream
   
} // class XmlDoc (30.07.2003, 06.02.2009)
