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
package de.frame4j.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import de.frame4j.io.Input;
import de.frame4j.util.MinDoc;


/** <b>Authentication for java.net connections</b>. <br />
 *  <br />
 *  An object of this class is a simple authenticator for network connections
 *  needing a user name and password. If such an object is registered as 
 *  standard authenticator the package java.net does the authentication of 
 *  network connections in the background. An {@link Authentication} object
 *  is also effective for an {@link Input} if this uses an URL
 *  connection.<br />
 *  <br />
 *  The usage is quite simple::<pre>
 *  &#160; new  Authentication("name", "passw", true);
 *  &#160; Input ein = new Input("urlDenotation");
 *  &#160; ein.copyTo( // or what ever one likes to do with the data</pre>
 *  
 *  {@link Authentication} object store the {@link #pass password} in clear
 *  text in an immutable String. The latter will avoided as far as possible.
 *  {@link Authentication} objects are to be used for network connections 
 *  mainly to Linux servers requiring clear text passwords. Do not use for
 *  JMX or safety critical applications.<br />
 *  <br />
 *  <a href="./package-summary.html#co">&copy;</a>
 *  Copyright 2001 - 2004 &nbsp; Albrecht Weinert<br />
 *  <br />  
 *  @author   Albrecht Weinert
 *  @version  $Revision: 44 $ ($Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $)
 *  @see      Input
 */
 // so far:  V00.00 (13.09.2001) :  new
 //          V00.02 (29.03.2002) :  moved from package .html
 //          V02.00 (24.04.2003) :  CVS Eclipse
 //          V02.xx (19.05.2005) :  /**,CVS bug corrected,
 //          V02.22 (21.10.2006) :  LDAP, AD in LDAPauthRead ausgel.
 //          V.  39 (18.04.2021) : get rid of LDAPauthRead (here comment only)

@MinDoc(
   copyright = "Copyright 2001 - 2004, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 44 $",
   lastModified   = "$Date: 2021-05-06 19:43:45 +0200 (Do, 06 Mai 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "import",  
   purpose = "stateful class; objects to hold authentication info"
) public class Authentication extends Authenticator {

/** The user name. <br />
 *  <br />
 *  Will be stored as (clear text) String.<br />
 */ 
   protected String name;

/** The password. <br />
 *  <br />
 *  Will be stored in clear text, but as char[] not as String.<br />
 *  <br />
 *  Storing as char array instead of String has the advantage of not leaving
 *  a long lived memory trace after object garbage collection as is probable
 *  with Strings due to common usage of internal arrays.<br />
 *  <br />
 *  In so far the constructor
 *  {@link #Authentication(String, char[], boolean)} is to be preferred over
 *  {@link #Authentication(String, String, boolean)}.<br />
 * 
 *  @see #name
 *  @see #clearPass()
 */ 
   protected char[] pass;
   
/** Clear the password. <br />
 *  <br />
 *  This method clears {@link #pass} and {@link #pAuth} as well a by
 *  overwriting the memory used by the password; for the background please
 *  see {@link #pass}.<br />
 *  <br />
 *  After calling this method no further authentication with this object
 *  is possible. It shall be called (only) after the last usage.<br />
 */ 
   public void clearPass() {
      char[] tmp = pass;
      pass = null;
      if (tmp != null ) java.util.Arrays.fill(tmp, '\0');
      
      if (pAuth != null) {
         tmp = pAuth.getPassword();
         pAuth = null;
         if (tmp != null ) java.util.Arrays.fill(tmp,'\0');
         tmp = null;
      }
   } // clearPass()

/** Authentication with Password. <br />
 *  <br />
 *  Will be generated on first call of 
 *  {@link #getPasswordAuthentication()}.<br />
 */ 
   protected volatile PasswordAuthentication pAuth;

/** Authentication with Password. <br />
 *  <br />
 *  This method returns a singleton {@link PasswordAuthentication} object
 *  for this {@link Authentication} object.<br />
 *  It is generated at the very first call.<br />
 *  <br />
 */ 
   @Override protected PasswordAuthentication getPasswordAuthentication() {
      if (pAuth == null) { // we don't care if two threads make two pAuths
         pAuth = new PasswordAuthentication(this.name, this.pass);
      }  // don't care if two threads make two pAuths (weak singlet. guarant.)
      return pAuth;
   } //  getPasswordAuthentication()

/** Set as default authenticator. <br />
 *  <br />
 *  After calling this method  the {@link java.net} package will use this 
 *  object's setting if a connection require authentication.<br />
 *  <br />
 */ 
    public void setAsDefault() {
       Authenticator.setDefault(this);
    } // setAsDefault()

/** Construct with name, password and optional registration. <br />
 *  <br />
 *  An {@link Authentication} object is made and registered as default
 *  authenticator.<br />
 *  <br />
 *  @param name the name
 *  @param pass the password
 *  @param setD true: Set as default authenticator
 */
   public Authentication(String name, char[] pass, boolean setD) {
      this.name = name;
      this.pass = pass;
      if (setD) setAsDefault();
   } // Authentication(String,char[],boolean)


/** Construct with name, password and optional registration. <br />
 *  <br />
 *  An {@link Authentication} object will be made and set as default
 *  authenticator.<br />
 *  <br />
 *  @param name the name
 *  @param pass the password
 *  @param setD true: Set as default authenticator
 *  @see #Authentication(String, char[], boolean)
 *  @see #pass
 */
   public Authentication(String name, String pass, boolean setD) {
      this(name,  pass != null ? pass.toCharArray() : null, setD);
   } // Authentication(2*String,boolean)

 } // class Authentication (01.03.2009)