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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import de.frame4j.io.Input;
import de.frame4j.util.App;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.util.PropMap;
import de.frame4j.text.TextHelper;

/** <b>Configuration for XML parser, generator and such.</b>. <br />
 *  <br />
 *  Objects of this class contain and handle commonly usable configuration
 *  settings especially for XML parsers. By connection to n {@link PropMap}
 *  respectively {@link App#getProp() App.prop} this class simplifies the
 *  usage of DOM (org.w3c.dom) and SAX parsers.<br />
 *  <br />
 *  Hence this class' the respective methods deliver SAX parsers (as 
 *  {@link org.xml.sax.XMLReader}), DOM builder (as 
 *  {@link javax.xml.parsers.DocumentBuilder}) and XML inputs (as
 *  {@link org.xml.sax.InputSource}) all according to the properties set in 
 *  this  {@link XMLconf} object.<br />
 *  <br />
 *  XML input objects made by {@link #makeInputSource(CharSequence)} can be 
 *  used for inputing XML in the narrower sense as as for inputing the 
 *  transforming style sheets.<br />
 *  <br />
 *  The class {@link XMLdoc} is build upon this class using an associated
 *  internal {@link XMLconf} object.<br />
 *  <br /> 
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2005 &nbsp; Albrecht Weinert<br />  
 *  <br />
 */
 // so far  V02.2n (21.02.2005 15:01) : new extracted out of XMLdoc
 //          V02.21 (25.04.2005 10:24) : /**, appendProxy, etc.
 //          V02.01 (29.11.2005 09:03) : /**, dest, trans and w/o JDOM now
 //          V02.03 (30.11.2005 17:22) : JDom away; no more XMLtrans

@MinDoc(
   copyright = "Copyright   2005, 2009, 2016  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use in XML applications",  
   purpose = "encapsulate the configuration"
) public class XMLconf implements Cloneable {


/** Make with standard settings. <br />
 *  <br />
 *  An {@link XMLconf} object with standard / default properties is
 *  made.<br />
 *  <br />
 *  @see #XMLconf(PropMap)
 *  @see #set(PropMap)
 *  @see #simplifyInput()
 */
   public XMLconf(){} 
   
/** Make with settings from a PropMap. <br />
 *  <br />
 *  An {@link XMLconf} object with properties set by {@code prop} is 
 *  <br />
 *  @param prop stating properties; null means default settings
 *  @see #set(PropMap)
 *  @see #simplifyInput()
 */
   public XMLconf(PropMap prop){ set(prop); }
   

/** Set the properties by a PropMap object. <br />
 *  <br />
 *  The properties and criteria of this {@link XMLconf} object will be set 
 *  according to the {@link PropMap} object {@code prop}.<br />
 *  Input- and parser properties:<ul>
 *  <li> proxyHost</li>
 *  <li> proxyPort</li>
 *  <li> proxySet (1)</li>
 *  <li> validating</li>
 *  <li> useSchema</li>
 *  <li> schemaSource</li>
 *  <li> namespaceAware</li>
 *  <li> ignoreWhiteSpace respectively ignoringElementWhiteSpaceContent</li>
 *  <li> expandEntityReferences</li>
 *  <li> coalescing</li>
 *  <li> ignoreComments</li>
 *  <li> impEncoding</li></ul>
 *  Transformer properties:<ul>
 *  <li> clearDefAtts</li>
 *  <li> textNormalize</li></ul>
 *  Output properties:<ul>
 *  <li> encoding</li>
 *  <li >omitDeclaration respectively omit-xml-declaration</li>
 *  <li> indentSize (2)</li><br />
 *  <li> indent (3)</li>
 *  <li> omitEncoding (4)</li>
 *  <li> newlines (4)</li>
 *  <li> expandEmptyElements (4)</li></ul>
 * 
 *  Is one of these properties not contained in {@code prop} nothing happens
 *  in each case.<br />
 *  If more than two names are available for the same property they will be
 *  looked for in  {@code prop} in the order given. The first name given above
 *  is the property name within  XMLdoc.<br />
 *  <br />
 *  Remark (1): The proxy settings will be evaluated in the order shown
 *  (compare  {@link #setProxySet(boolean) setProxySet()}).<br />
 *  Remark 2: methods of this class set the DOM filter property indent as
 *  indentSize &gt; 0 ? yes : no.<br />
 *  Remark 3: Setting the property indentSize (0..18) sets indent as String
 *  containing the respective number of space; setting the Strings indent
 *  to null or with a length in the range 0..18, sets indentSize 
 *  accordingly.<br />
 *  <br />
 *  @see #XMLconf(PropMap)
 *  @see #simplifyInput()
 *  @param prop source of the properties to be used
 */
   public void set(PropMap prop){
      if (prop == null) return;

      String tmProp = prop.getString("proxyHost", null);
      if (tmProp != null) setProxyHost(tmProp);
      tmProp = prop.getString("proxyPort", null);
      if (tmProp != null) setProxyPort(tmProp);
      setProxySet(prop.getBoolean("proxySet", proxySet));

      setUseSchema(  prop.getBoolean("useSchema", useSchema));
      tmProp = prop.getString("schemaSource", null);
      if (tmProp != null) setSchemaSource(tmProp);
      setValidating(  prop.getBoolean("validating", validating));
      setNamespaceAware(  prop.getBoolean("namespaceAware", namespaceAware));

      if (prop.containsKey("omitDeclaration")) {
         setOmitDeclaration(  prop.getBoolean("omitDeclaration"));
      } else if (prop.containsKey("omit-xml-declaration")) {
         setOmitDeclaration(  prop.getBoolean("omit-xml-declaration"));
      }   

      if (prop.containsKey("ignoreWhiteSpace")) {
         setIgnoreWhiteSpace(  prop.getBoolean("ignoreWhiteSpace"));
      } else if (prop.containsKey("ignoringElementWhiteSpaceContent")) {
         setIgnoreWhiteSpace(  
                 prop.getBoolean("ignoringElementWhiteSpaceContent"));
      }   
      setExpandEntityReferences(prop.getBoolean(
                   "expandEntityReferences", expandEntityReferences));
      setCoalescing(prop.getBoolean("coalescing", coalescing));
      setIgnoreComments(prop.getBoolean("ignoreComments", ignoreComments));

      setNewlines(prop.getBoolean("newlines", newlines));
      setEncoding(prop.getString("encoding", encoding));
      setImpEncoding(prop.getString("impEncoding", impEncoding));
      setOmitEncoding(  prop.getBoolean("omitEncoding", omitEncoding));
      setTextNormalize(  prop.getBoolean("textNormalize", textNormalize));
      setClearDefAtts(  prop.getBoolean("clearDefAtts", clearDefAtts));
      setExpandEmptyElements(  
               prop.getBoolean("expandEmptyElements", expandEmptyElements));
      tmProp = prop.getString("indent", null);
      if (tmProp != null) setIndent(tmProp);
      setIndentSize(  prop.getInt("indentSize", -19));
   } // set(PropMap)
   

/** Settings for simplified, non checking input. <br />
 *  <br />
 *  This method sets {@link #coalescing}, {@link #ignoreComments} as well as
 *  {@link #ignoreWhiteSpace} true and sets {@link #namespaceAware}
 *  and {@link #validating} false.<br />
 *  <br />
 *  @see #set(PropMap)
 */ 
   public void simplifyInput(){
      setCoalescing(true);
      setIgnoreWhiteSpace(true);
      setIgnoreComments(true);
      setNamespaceAware(false);
      setValidating(false);
      setUseSchema(false);
   } // simplifyInput()

   
//-------    Proxy  properties    -------------------------------------
   
/** Use a proxy server when fetching from URL. <br />
 *  <br />
 *  @see #isProxySet()
 */
   boolean proxySet;

/** Use a proxy server when fetching from URL. <br />
 *  <br />
 *  If true a proxy will be used and the properties {@link #proxyHost} and
 *  {@link #proxyHost} are effective.<br />
 *  <br />
 *  @return true if a proxy is to be used (default: false)
 */
   public boolean isProxySet(){ return proxySet; }

/** Use a proxy server when fetching from URL. <br />
 *  <br />
 *  If true a proxy will be used and the properties {@link #proxyHost} and
 *  {@link #proxyHost} are effective.<br />
 *  <br />
 *  If this method changes the previous setting the respective system 
 *  properties will be set accordingly.<br />
 *  <br />
 *  This method {@link #setProxySet(boolean) setProxySet()} has to be called
 *  after {@link #setProxyHost setProyHost()} and
 *  {@link #setProxyPort setProyPort()}, to make the changes effective. (If
 *  using  {@link #makeInputSource(CharSequence) makeInputSource()} one
 *  doesn't have to bother a this method does it right by itself.)<br />
 *  @param proxySet true if a proxy is to be used.
 */
   public void setProxySet(boolean proxySet){
      if (!proxySettingsChg && this. proxySet == proxySet) return;
      this. proxySet = proxySet;
      if (proxySet) {
         System.setProperty("proxySet", "true");
         System.setProperty("proxyHost", proxyHost);
         System.setProperty("proxyPort", proxyPort);
      } else {
         System.setProperty("proxySet", "false");
      }
      proxySettingsChg = false;
   } // setProxySet(boolean)
   

/** Name of the proxy server for fetching from URL. <br />
 *  <br />
 *  @see #getProxyHost()
 */
   protected String  proxyHost  = "cache";

/** Name of the proxy server for fetching from URL. <br />
 *  <br />
 *  default: &quote;cache&quote;<br />
 *  <br />
 *  @return the proxy server
 *  @see #isProxySet() proxySet
 */
   public String  getProxyHost(){ return proxyHost; }

/** Set the name of the proxy server for fetching from URL. <br />
 *  <br />
 *  The parameter {@code proxyHost} will be stripped from surrounding white
 *  space; empty will be taken as null.<br />
 *  <br />
 *  A change of the property {@link #getProxyHost() prooxyHost} or 
 *  {@link #getProxyPort() proxyPort} will only be effective after calling
 *  {@link #setProxySet(boolean) setProxySet(true)}.<br />
 *  <br />
 *  @param proxyHost the proxy server
 *  @see #getProxyHost()
 */
   public void setProxyHost(CharSequence proxyHost){
      this.proxyHost = TextHelper.trimUq(proxyHost, null);
      proxySettingsChg = true;
   } // setProxyHost(CharSequence)



/** Port of the proxy server for fetching from URL. <br />
 *  <br />
 *  default: 8080<br />
 *  <br />
 *  @return the proxy port
 *  @see #isProxySet() proxySet
 */
   public String  getProxyPort(){ return proxyPort; }

/** Port of the proxy server for fetching from URL. <br />
 *  <br />
 *  @see #getProxyPort()
 */
   protected String  proxyPort  = "8080";

/** Set the port of the proxy server for fetching from URL. <br />
 *  <br />
 *  The parameter {@code proxyPort} will be stripped from surrounding white
 *  space; empty will be taken as null.<br />
 *  <br />
 *  A change of the property {@link #getProxyHost() prooxyHost} or 
 *  {@link #getProxyPort() proxyPort} will only be effective after calling
 *  {@link #setProxySet(boolean) setProxySet(true)}.<br />
 *  <br />
 *  @see #getProxyPort()
 *  @param proxyPort the proxy port (a number as String)
 */
   public void setProxyPort(CharSequence proxyPort){
      this.proxyPort = TextHelper.trimUq(proxyPort, null);
      proxySettingsChg = true;
   } // setProxyPort(CharSequence)
   
/** Append host and port as text. <br />
 *  <br />
 *  According to the properties {@link #isProxySet() proxySet},
 *  {@link #getProxyHost() proxyHost} and {@link #getProxyPort() proxyPort}
 *  to {@code bastel} is appended either &quot;no&nbsp;proxy&quot; or
 *  &quot;proxy&nbsp;host&nbsp;=&nbsp;cache,&nbsp;port&nbsp;=&nbsp;8080&quot;
 *  (e.g. the default).<br />
 *  <br />
 *  @param bastel the StringBuilder to append to; if null it will be made with
 *                starting capacity 80 character
 *  @return bastel
 */   
   public StringBuilder appendProxy(StringBuilder bastel){
      if (bastel == null) bastel = new StringBuilder(90);
      if (proxySet) {
         bastel.append("proxy host = ").append(proxyHost);
         bastel.append(", port = ").append(proxyPort);
      } else {
         bastel.append("no proxy");
      }
      return bastel;
   } // appendProxy(StringBuilder)


//----     XML-Parser properties     ----------------------------------

/** Parse XML validating. <br />
 *  <br />
 *  If true a  &quot;valide&quot; XML input will be required. Non valid 
 *  inputs will lead to error reports, but will not in every case impede the
 *  generating of a DOM document.<br />
 *  <br />
 *  default: true
 */
   public boolean validating   = true;

/** Set validating XML parsing. <br />
 *  <br />
 *  @param validating true if XML parsing should be validating (requires 
 *                    schema or documentation type) 
 */
   public final void setValidating(boolean validating){ 
      this.validating = validating;
   } // setValidating(boolean)


/** Parse XML with XML namespace support. <br />
 *  <br />
 *  If true a XML parser with namespace support will be made and used.<br />
 *  <br />
 *  default: false
 */
   public boolean namespaceAware;
   

/** Parse XML validating with XML scheme en lieu de DTD. <br />
 *  <br />
 *  default: false
 */
   public boolean useSchema;    

/** Set parse XML validating with XML scheme en lieu de DTD. <br /> 
 *  @param useSchema true means a schema and not a DTD is to be used for
 *                   validating
 */
   public final void setUseSchema(boolean useSchema){
      this.useSchema = useSchema;
   }


/** Name of a local schema. <br />
 *  <br />
 *  It is either not empty or null.<br />
 *  In case of {@link #useSchema} true it will be taken as the name of a local
 *  schema file for schema validation. <br />
 *  @return a schema file name
 */  
   public final String getSchemaSource(){ return schemaSource; }

/** Name of a local schema. <br />
 *  <br />
 *  @see #getSchemaSource()
 */  
   String schemaSource;

/** Set the name of a local schema. <br />
 *  <br />
 *  If {@code schemaSource} stripped from surrounding white space is not empty
 *  it will be used as new local source (file) of a schema. Otherwise the
 *  property {@link #getSchemaSource() schemaSource} will be set null.<br />
 *  <br />
 *  @param schemaSource a schema file name
 *  @see TextHelper#trimUq(CharSequence, String)
 */  
   public void setSchemaSource(String schemaSource){
      this.schemaSource = TextHelper.trimUq(schemaSource, null);
   } //  setSchemaSource(String



/** Ignore white space. <br />
 *  <br />
 *  DOM-Name: ignoringElementWhiteSpaceContent<br />
 *  {@link #validating} has to be true on most XML implementations if 
 *  {@link #ignoreWhiteSpace} respectively
 *  ignoringElementWhiteSpaceContent is true.<br /> 
 *  <br />
 *  default: false
 */
   public boolean ignoreWhiteSpace;    


/** Replace references. <br />
 *  <br />
 *  default: false
 */
   public boolean expandEntityReferences;  
   

/** Unite character data in a text node. <br />
 *  <br />
 *  default: false
 */
   public boolean coalescing;
   

/** Delete comments. <br />
 *  <br />
 *  default: false
 */
   public boolean ignoreComments;    
      
 
//-------------------  Transformation properties   -----------------------

/** Delete HTML default attributes before output. <br />
 *  <br />
 *  In case of true the default attributes of some standard HTML elements
 *  like clear=&quot;none&quot; for &lt;br /&gt; and others will be 
 *  removed.<br />
 *  <br />
 *  The complete list and the rationale is given at the method
 *  {@link MLHelper#clearHTMLdefAtts(org.w3c.dom.Element) 
 *  clearHTMLdefAtts(Element)}.<br />
 *  <br />
 *  default: false
 */
   public boolean clearDefAtts;

/** Coding of the XML text input. <br />
 *  <br />
 *  The coding will be used for input.<br />
 *  <br />
 *  default: ISO-8859-1
 */
   public String impEncoding = "ISO-8859-1";
   

/** Set the coding of the XML text input. <br />
 *  <br />
 *  The parameter {@code encoding} will be stripped from surrounding white 
 *  spaces and then used to set  {@link #impEncoding}.<br />
 *  <br />
 *  null or empty will be taken as {@link  ComVar#FILE_ENCODING}.
 *  @param impEncoding the encoding for XML input
 */
   public void setImpEncoding(String impEncoding){
      this.impEncoding = TextHelper.trimUq(impEncoding, ComVar.FILE_ENCODING);
   }

   
//-------------------   Output properties           ---------------------


/** Coding of the XML text output. <br />
 *  <br />
 *  The coding will be used for the output and for a generated declaration of
 *  encoding (if  {@link #omitEncoding} is not true).<br />
 *  <br />
 *  default: ISO-8859-1
 */
   public String encoding = "ISO-8859-1";
   

/** Set the coding of the XML text (input and) output. <br />
 *  <br />
 *  The parameter {@code encoding} will be stripped from surrounding white 
 *  spaces and then used to set  {@link #encoding}.<br />
 *  <br />
 *  null or empty will be taken as ISO-8859-1.
 *  @param encoding the encoding for XML input and output
 */
   public void setEncoding(String encoding){
       this.encoding = TextHelper.trimUq(encoding, "ISO-8859-1");
  } // setEncoding(String)


/** Declare XML coding in the XML output text. <br />
 *  <br />
 *  In case of false a XML declaration will prepend the output.<br />
 *  W3C calls this property omit-xml-declaration.<br />
 *  <br />
 *  default: false
 */
   public boolean omitDeclaration;


///   ___________  End of / start JDOM inheritance   -------------------
  
/** Output line feeds with the XML text output. <br />
 *  <br />
 *  In the case of false line feeds will not be output (compact output).<br />
 *  <br />
 *  default: true
 */
   public boolean newlines = true;



/** Declare the coding with the XML text output. <br />
 *  <br />
 *  In the case of false the coding will be declared at the beginning of a
 *  XML output.<br />
 *  <br />
 *  default: false
 */
   public boolean omitEncoding;

/** Compact the XML text output. <br />
 *  <br />
 *  In case of true the XML output will get just one space for any 
 *  sequence of consecutive white spaces.<br />
 *  In case of false the white spaces in the input will be preserved.<br />
 *  <br />
 *  default: false
 */
   public boolean textNormalize;


/** Expand empty elements on the XML text output. <br />
 *  <br />
 *  In case of true the XML output will get a pair of tags (start and end tag)
 *  for every empty tag. In that case, for example, &lt;br /&gt; would become
 *  &lt;br&gt;&lt;/br&gt;.<br />
 *  <br />
 *  default: false
 */
   public boolean expandEmptyElements;

/** Indentation of the XML text output. <br />
 *  <br />
 *  The String indent respectively indentSize spaces will be used for the
 *  indentation according to the block structure.<br />
 *  <br />
 *  Allowed: A String of length 0..18 or null<br />
 *  default: null
 *  @return an indentation string or null
 */
   public String getIndent(){ return indent; }
   String indent;

/** Set the indentation String. <br />
 *  <br />
 *  @param indent  an indentation string or null
 *  @see #getIndent getIndent()
 *  @see #getIndentSize getIndentSize()
 */
   public void setIndent(String indent) {
      int len = indent == null ? 0 : indent.length();
      if (len == 0 || len > 18) {
         this.indent = "";
         this.indentSize = 0;
      }
      this.indent = indent;
      this.indentSize = len;
   } // setIndent(String


/** Indentation of the XML text output. <br />
 *  <br />
 *  The String indent respectively indentSize spaces will be used for the
 *  indentation according to the block structure.<br />
 *  <br />
 *  Allowed range: 0..18<br />
 *  default: 0
 *  @return the indentation size (number of spaces)
 */
   public int getIndentSize(){ return indentSize; }
   int indentSize;

/** Set the indentation length. <br />
 *  <br />
 *  @see #getIndent getIndent()
 *  @see #getIndentSize getIndentSize()
 *  @param indentSize new indentation length 0..18; <br /> &nbsp;
 *                  other values will not change the previous setting
 */
   public void setIndentSize(int indentSize) {
      if (indentSize < 0 || indentSize > 18) return;
      this.indentSize = indentSize;
      indent = indentSize == 0 ? "" 
            : "                   ".substring(0, indentSize);
   } // setIndentSize(int
   
//-----------------  Setter-Zoo -----   
  

/** Standard setter for {@link #clearDefAtts}. <br /> 
 * @param clearDefAtts true means delete HTML default attributes before output
 */
   public void setClearDefAtts(boolean clearDefAtts){
      this.clearDefAtts = clearDefAtts;
   }


/** Standard setter for {@link #expandEmptyElements}. <br /> 
 * @param expandEmptyElements true means expand respectively auto-close empty
 *                            elements on the XML text output
 */
   public void setExpandEmptyElements(boolean expandEmptyElements){
      this.expandEmptyElements = expandEmptyElements;
   }


/** Standard setter for {@link #namespaceAware}. <br /> 
 *  @param namespaceAware true means be aware of namespaces
 */
   public void setNamespaceAware(boolean namespaceAware){
      this.namespaceAware = namespaceAware;
   }

/** Standard setter for {@link #newlines}. <br /> 
 * @param newlines true means output line feeds with the XML text output
 */
   public void setNewlines(boolean newlines) {
      this.newlines = newlines;
   }

/** Standard setter for {@link #omitDeclaration}. <br /> 
 * @param omitDeclaration false means a XML declaration will prepend the output
 */
   public void setOmitDeclaration(boolean omitDeclaration) {
      this.omitDeclaration = omitDeclaration;
   }

/** Standard setter for {@link #omitEncoding}. <br /> 
 * @param omitEncoding true means declare the coding with the XML text output
 */
   public void setOmitEncoding(boolean omitEncoding){
      this.omitEncoding = omitEncoding;
   }

/** Proxy-Settings may have changed. <br /> */
   boolean proxySettingsChg = true;



/** Standard setter for {@link #textNormalize}. <br />
 *  @param textNormalize true means make a compact XML text output
 */
   public void setTextNormalize(boolean textNormalize){
       this.textNormalize = textNormalize;
   }


/** Standard setter for {@link #coalescing}. <br /> 
 * @param coalescing see {@link #coalescing}
 */
   public final void setCoalescing(boolean coalescing) {
      this.coalescing = coalescing;
   }

/** Standard setter for {@link #expandEntityReferences}. <br />
 *  @param expandEntityReferences  {@link #expandEntityReferences}
 */
   public void setExpandEntityReferences(boolean expandEntityReferences){
      this.expandEntityReferences = expandEntityReferences;
   }

/** Standard setter for {@link #ignoreComments}.  <br />
 *  @param ignoreComments  {@link #ignoreComments}
 */
   public final void setIgnoreComments(boolean ignoreComments){
      this.ignoreComments = ignoreComments;
   }

/** Set {@link #ignoreWhiteSpace}. <br />
 *  @param ignoreWhiteSpace {@link #ignoreWhiteSpace}
 */
   public final void setIgnoreWhiteSpace(boolean ignoreWhiteSpace){
      this.ignoreWhiteSpace = ignoreWhiteSpace;
   } //  setIgnoreWhiteSpace(boolean 
   

   
//--------------------------------------------------------------------------   
   
/** Make a XML parser / reader. <br />
 *  <br />
 *  @param  cnth a ContentHandler if given; may here be null, but a SAX parser
 *          without ContentHandler makes no sense in most cases
 *  @param  dbeh ErrorHandler, for example a {@link ParseErrorHandler} object;
 *          may be null
 *  @return the {@link SAXParser} as {@link XMLReader}
 *  @throws ParserConfigurationException if a SAXparser with the basic
 *          properties (namespace, validating) set in this XMLconf object
 *          can't be made
 *  @throws SAXException the rest of the errors (its the enclosing type) while
 *          making the internal SAXParser or the {@link XMLReader}
 *  @throws SAXNotRecognizedException if the XMLReader made does not honour
 *          a property to be set, like for example 
 *          {@link MLHelper#JAXP_SCHEMA_LANGUAGE}
 *  @throws SAXNotSupportedException  if the XMLReader made does know / 
 *          recognise a property to be set, but won't accept the value, 
 *          like e.g. for  {@link MLHelper#W3C_XML_SCHEMA}
 */
   public XMLReader makeXMLReader(ContentHandler cnth, ErrorHandler dbeh)
           throws ParserConfigurationException, SAXException,
           SAXNotRecognizedException, SAXNotSupportedException  {
      
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setNamespaceAware(namespaceAware);
      spf.setValidating(validating || useSchema);
      SAXParser saxParser = spf.newSAXParser();
      
      if (useSchema) {
         saxParser.setProperty(MLHelper.JAXP_SCHEMA_LANGUAGE,
                                                MLHelper.W3C_XML_SCHEMA);
         if (schemaSource != null) {
               saxParser.setProperty(MLHelper.JAXP_SCHEMA_SOURCE,
                                                 new File(schemaSource));
         }
      } //  if (useSchema)

      XMLReader xmlReader = saxParser.getXMLReader();
      if (cnth != null) xmlReader.setContentHandler(cnth);
      if (dbeh != null) xmlReader.setErrorHandler(dbeh);
      
      return xmlReader;
   } // makeXMLReader(ContentHandler, ErrorHandler)

   
/** Make a XML reader / SAX parser. <br />
 *  <br />
 *  The call is equivalent to <br /> &nbsp; &nbsp;
 *  {@link #makeXMLReader(ContentHandler, ErrorHandler)
 *       makeXMLReader(handler, handler)}.<br />
 *  <br />
 *  @param handler ContentHandler and ErrorHandler in one object &mdash; 
 *         supplied as an inheritor of {@link SAXHandler}; may be null, even
 *         if that does not make any sense in virtually all cases
 *  @return the {@link SAXParser} as {@link XMLReader}
 *  @throws ParserConfigurationException if a SAXparser with the basic
 *          properties (namespace, validating) set in this XMLconf object
 *          can't be made
 *  @throws SAXException the rest of the errors (its the enclosing type) while
 *          making the internal SAXParser or the {@link XMLReader}
 *  @throws SAXNotRecognizedException if the XMLReader made does not honour
 *          a property to be set, like for example 
 *          {@link MLHelper#JAXP_SCHEMA_LANGUAGE}
 *  @throws SAXNotSupportedException  if the XMLReader made does know / 
 *          recognise a property to be set, but won't accept the value, 
 *          like e.g. for  {@link MLHelper#W3C_XML_SCHEMA}
 */
   public XMLReader makeXMLReader(SAXHandler handler)
           throws ParserConfigurationException, SAXException,
           SAXNotRecognizedException, SAXNotSupportedException  {
      return makeXMLReader(handler, handler);
   } // makeXMLReader(SAXHandler


/** Make a DocumentBuilder. <br />
 *  <br />
 *  @param dbeh ErrorHandler, e.g. a
 *         {@link ParseErrorHandler}-Objekt; may be null
 *  @return the DocumentBuilder
 *  @throws FactoryConfigurationError forwarded
 *  @throws ParserConfigurationException if a DocumentBuilder with the basic
 *          properties (namespace, validating) set in this XMLconf object
 *          can't be made
 *  @throws IllegalArgumentException if a property to be set, that is not
 *          known by the SAX/DOM implementation, like for example 
 *          {@link MLHelper#JAXP_SCHEMA_LANGUAGE}
 */
   public DocumentBuilder makeDocumentBuilder(ParseErrorHandler dbeh)
             throws FactoryConfigurationError,
             IllegalArgumentException, ParserConfigurationException {
      
      DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
      
      dBF.setValidating(validating);
      dBF.setNamespaceAware(namespaceAware);
      if (useSchema && validating) {
         dBF.setAttribute(MLHelper.JAXP_SCHEMA_LANGUAGE,
                                             MLHelper.W3C_XML_SCHEMA);
         if (schemaSource != null) {
            dBF.setAttribute(MLHelper.JAXP_SCHEMA_SOURCE,
                  new java.io.File(schemaSource));  ///// das geht !!!!
         }         
      }
      dBF.setIgnoringElementContentWhitespace(ignoreWhiteSpace);
      dBF.setExpandEntityReferences(expandEntityReferences);
      dBF.setCoalescing(coalescing);
      dBF.setIgnoringComments(ignoreComments);
      DocumentBuilder sab = dBF.newDocumentBuilder();    
      if (dbeh != null)  sab.setErrorHandler(dbeh);
      return sab;
   } // makeDocumentBuilder(ParseErrorHandler 

   
/** Make a XML input. <br />
 *  <br />
 *  Relevant e properties: {@link #impEncoding},
 *  {@link #isProxySet() proxySet}, {@link #proxyHost} and
 *  {@link #proxyPort}.<br />
 *  <br />
 *  @param  fileOrURL denotation of the input
 *  @return XML input object as a {@link javax.xml.transform.Source Source}
 *  @throws IllegalArgumentException if fileOrURL null or empty
 *  @throws IOException on problems making the {@link Source}
 */  
   public StreamSource makeStreamSource(CharSequence fileOrURL) throws
                                 IllegalArgumentException, IOException {
      String name = TextHelper.trimUq(fileOrURL, null);
      if (name == null) {
         throw new IllegalArgumentException(
                 "XML-input needs fileOrURL (name)");       
      }
      setProxySet(proxySet);  // force the system settings
      Input ein = Input.openFileOrURL(name);
      StreamSource ips = new StreamSource(new InputStreamReader(ein, impEncoding));
      ips.setSystemId(ein.getName());
      return ips;
   } // makeStreamSource(CharSequence) 
   

/** Make a XML input. <br />
 *  <br />
 *  Relevant e properties: {@link #impEncoding},
 *  {@link #isProxySet() proxySet}, {@link #proxyHost} and
 *  {@link #proxyPort}.<br />
 *  <br />
 *  @param  fileOrURL denotation of the input
 *  @return XML input object as an {@link InputSource} object
 *  @throws IllegalArgumentException if fileOrURL null or empty
 *  @throws IOException on problems making the {@link Input}
 */  
   public InputSource makeInputSource(CharSequence fileOrURL) throws
                                 IllegalArgumentException, IOException {
      String name = TextHelper.trimUq(fileOrURL, null);
      if (name == null) {
         throw new IllegalArgumentException(
                 "XML-input needs fileOrURL (name)");       
      }
      setProxySet(proxySet);  // force the system settings
      Input ein = Input.openFileOrURL(name);
      InputSource ips = new InputSource(new InputStreamReader(ein, impEncoding));
      ips.setSystemId(ein.getName());
      return ips;
   } // makeInputSource(CharSequence) 

   

/** Make a XML style sheet transformer. <br />
 *  <br />
 *  Relevant properties: {@link #encoding},
 *  {@link #getIndentSize() indentSize &gt;0}, 
 *  {@link #omitDeclaration}
 *  <br />
 *  @param   transI input source, like for example one made by
 *           {@link #makeInputSource(CharSequence)}
 *  @return XML style sheet transformer
 *  @throws IllegalArgumentException if transI is null
 *  @throws TransformerConfigurationException on problems setting relevant
 *          properties
 */  
   public Transformer makeTransformer(final Source transI) throws
                 IllegalArgumentException, TransformerConfigurationException {
      if (transI == null) {
         throw new IllegalArgumentException(
                 "XML transforming style sheet needs InputSource");       
      }
      
      final TransformerFactory tf = TransformerFactory.newInstance();
      Transformer   trafo =  tf.newTransformer(transI);
      
/*
      Templates style = tf.newTemplates(new SAXSource(transI));
      Transformer     trafo = style.newTransformer();  
     // trafo.setOutputProperty("encoding", encoding);
    //  trafo.setOutputProperty("indent", indentSize > 0 ? "yes" : "no");
    trafo.setOutputProperty("omit-xml-declaration", 
                                       omitDeclaration ? "yes" : "no");
      /// trafo.setOutputProperty("method", method ); // text ?? 
                                       */
      return trafo;
   } //  makeTransformer(InputSource) 

   
} // class XMLconf (21.02.2005)
