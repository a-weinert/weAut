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
package de.frame4j.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.net.MalformedURLException;

import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;

/** <b>Handling and support for (byte-) Input</b>. <br />
 *  <br />
 *  This class facilitates the handling of reading bytes (8 bit data) from
 *  different types of sources comfortably. The sources can be<ul>
 *  <li>{@link File}s,</li> 
 *  <li>URLs,</li>
 *  <li>byte arrays or</li>
 *  <li>any source guised as {@link java.io.InputStream InputStream}</li></ul>
 *  
 *  Some constructors and factory methods do an automatic choice between
 *  URL and file by reasonable criteria or by trial and error.<br />
 *  <br />
 *  {@code Input} is an extension of  {@link java.io.InputStream} with 
 *   optional (build in) buffering. The (&quot;rewind&quot;) methods
 *  {@link #mark mark()} and {@link #reset reset()} being at start without
 *  function (as usual) get their full effect if all input is
 *  {@link #record(boolean) record}ed.<br /> 
 *  Methods {@link #read()} and 
 *  {@link #read(byte[],int,int) read(byte[], int, int)} are overridden.<br />
 *  <br />
 *  An {@link Input} object is itself an {@link InputStream}. The data source
 *  is set on construction being either a {@link File}
 *  ({@link #dataFile}), a URL ({@link #url}) (the not used choice is null)
 *  or another {@link InputStream} (like the standard input  System.in or
 *  another process' normal or error output).<br />
 *  <br /> 
 *  Besides all {@link java.io.InputStream InputStream} methods Input 
 *  features:<ul>
 *   <li>reading and buffering (recording) the content,</li>
 *   <li>(optionally) concurrent reading and buffering (recording) in an
 *       anonymous Thread,</li>
 *   <li>re activate the Input from an URL or a {@link File}.</li></ul>
 *
 *  The re-activating optimises the process of repeatedly reading from an URL
 *  or a file with dynamically changing content. It saves the otherwise
 *  needed re-creation of thwrowaway objects.<br />
 *  <br />
 *  Concurrent buffering is quite useful and sometimes indispensable for
 *  loading via network connections or reading pipes form other processes.
 *  Those sources deliver asynchronously and block (otherwise) in arbitrary 
 *  sequence. {@link Input} features this concurrent reading with all 
 *  comfort and threadsafe (so avoiding the often seen home bread 
 *  solutions).<br />
 *  Remark: This class {@link Input} is much older than 
 *  {@link java.util.stream.Stream Stream}s and delivered
 *  among others some of their features then, already. Now, as Frame4J 
 *  requires Java8 {@link Input} might get obsolete. The removal of
 *  {@link Input} from Frame4J or the use of
 *  {@link java.util.stream.Stream Stream} in this class {@link Input} 
 *  might be considered in future.<br />   
 *  <br />
 *  <br />   
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2008, 2019, 2021  &nbsp; Albrecht Weinert. 

 */
 // so far    V00.00 (12:03 12.03.1998) :  new (parts from FUCopy, AnaHTML)
 //           V00.03 (20.12.2000 11:34) :  getAsSAtring(),  /**
 //           V00.04 (20.12.2000 11:34) :  getName()
 //           V00.06 (14.08.2001 09:51) :  name as buffer for getName
 //           V00.07 (19.09.2002 15:19) :  Recorder
 //           V02.06 (01.07.2003 14:54) :  mark Support, byteArrayInputStream
 //           V02.23 (16.05.2005 18:24) :  isOpen
 //           V02.24 (30.06.2005 15:16) :  multi-threading in record (corr.)
 //           V02.30 (07.03.2008 10:18) :  getAsBytesAndClose() + sync
 //           V.069+ (12.02.2009 17:36) :  renamed for Frame4J
 //           V.139+ 06.01.2016 : FileHelper
 //           V.  17 14.03.2019 : getAsResourceStream
@MinDoc(
   copyright = "Copyright  1998 - 2009, 2016, 2019   A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 57 $",
   lastModified   = "$Date: 2021-07-04 17:53:10 +0200 (So, 04 Jul 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use instead of an InputStream",  
   purpose = "a byte input with useful extra capabilities"
) public class Input extends InputStream {

/** The File. <br />
 *  <br />
 *  There is at most either one {@code File} or one URL as 
 *  {@code Input}'s base  (or none of both).<br />
 *  <br />
 *  @see #url
 *  @see #urlConnection
 */
   public final File dataFile;

/** The URL. <br />
 *  <br />
 *  There is at most either one {@code File} or one URL as 
 *  {@code Input}'s base.<br />
 *  <br />
 *  @see #dataFile
 *  @see #urlConnection
 */
   public final URL url;

/** The URL connection. <br />
 *  <br />
 *  If {@link #url} is not null, the input is via an URL connection, 
 *  {@link #reopen(boolean) reopen}ed at every re-activation.<br />
 *  <br />
 *  @see #url
 */
   protected volatile URLConnection urlConnection;


/** The &quot;pure&quot; file name. <br />
 *  <br />
 *  It will be tried to get the pure file name (without directory path) for
 *  the {@link File} {@link #dataFile} respectively the URL {@link #url}.<br />
 *  <br />
 *  A &quot;FilterInputStream&quot; constructor allows the direct setting of
 *  this name.<br />
 *  This information &quot;survives&quot; a {@link #close()}.<br />
 *  <br />
 */
   public final String getName(){ return name; }

/** The &quot;pure&quot; file name. <br />
 *  <br />
 *  @see #getName()
 */
   public final String name;
   
/** The (actual) underlying input stream. <br /> 
 */   
   protected volatile InputStream in;

/** Was closed. */
   protected volatile boolean closed;

/** Was closed. */
   public boolean isClosed(){ return closed; }


/** Recording in progress. */
   protected volatile boolean recording;

/** Recording in progress. <br />
 *  <br />
 *  If a complete recording of the complete or remaining content was ordered
 *  this method returns true while this process is still running. During
 *  this time this Input object is not usable.<br />
 *  Attention: Most read methods would block.<br />
 */
   public boolean isRecording(){ return recording; }

/** It's buffered. </br > */
   protected volatile boolean  buffered;

/** It's buffered.. </br > 
 *  <br />
 *  If the whole input or an non empty part of it was buffered, this method
 *  returns true.<br />
 */
   public final boolean isBuffered(){ return buffered; }

/** Impossible to become totally buffered. </br > */
   protected boolean  partlyReadUnBuffered;

/** It's totally buffered. </br > 
 *  <br />
 *  If the whole input was buffered to the end and if no part of the content
 *  did escape buffering / this method returns true.<br />
 */
   public final boolean isTotallyBuffered(){
      return buffered && !partlyReadUnBuffered;
   }

/** Total number of bytes of this Input or its buffer. <br />
 *  <br />
 *  @see #getGesLen()
 */
   protected int count = -1;

/** Total number of bytes of this Input or its buffer. <br />
 *  <br />
 *  The total number of this Input's byte will be returned, if determinable,
 *  or -1.<br />
 *  <br />
 *  After buffering the whole input or of a non empty remaining part the 
 *  returned value is the buffer's length.<br />
 */
   public final int getGesLen(){ return count; }
   
/** Date and time of the last modification.<br />
 *  <br />
 *  For files as source of this Input's content their modification time is
 *  returned.<br />
 *  <br />
 *  For URLs the information is grabbed from the header. If available the
 *  modification date (last-modified)is returned or the date of delivery 
 *  (date) as substitute.<br />
 *  <br />
 *  If indeterminable 0 (OL) is returned.<br />
 *  <br />
 *  @since 04.05.2005
 */   
   public long lastModified(){ return date; }
   
/** Date and time of the last modification. <br />
 *  <br />
 *  @see #lastModified()
 *  @since 04.05.2005
 */   
   protected volatile long date;
   

/** The result of recording. <br />
 *  <br />
 *  If buffering was (fully) done by {@link #record(boolean) record()} 
 *  {@code buff} is no longer null.<br />
 *  From then on further read operations of this InputStream use this buffer
 *  (with markSupport). Its length is is {@link #count}.
 */
   protected volatile byte[] buf;

/** The number of bytes read from this Input. <br />
 *  <br />
 *  This variable counts the read or skipped (read(), skip()) bytes. After
 *  buffering / recording it is the  read index within the the buffer
 *  {@link #buf}.<br />
 */
   protected int pos; // all accesses sync this


/** Reset position. <br />
 *  <br />
 *  In case of buffering only ({@link #isBuffered isBuffered()} true) this is
 *  the rewind position for {@link #reset reset()}. <br />
 */
   protected int markPos;  // all accesses sync this

/** Error while reading. <br />
 *  <br />
 *  This method returns an exception that occurred to the methods 
 *  {@link #record record()}, {@link #getAsString getAsString()}, 
 *  {@link #getAsBytes getAsBytes()}, {@link #copyTo copyTo()} etc. If all
 *  went well null is returned.<br />
 *  <br />
 *  @return null, if all went well or 
 *             if an exception occurred has been re-thrown
 *  @since V00.07 (19.09.2002)
 */
   public final IOException getReadError(){ return  readError; }

/** Error while reading <br />
 *  <br />
 *  @see #getReadError()
 *  @since V00.07 (19.09.2002)
 */
   protected volatile IOException readError; // all writes guarded by this


/** Buffer for recording. */
   ByteArrayOutputStream bao;


// ---------   (static) factory methods  ---------------------------------

/** Fetch an Input object by complete path specification. <br />
 *  <br />
 *  Determined by the parameter {@code path} (stripped from surrounding white
 *  space) it will be tried to make an {@link Input} object.<br />
 *  <br />
 *  The first trial is to open a {@link File} of that name for reading.<br />
 *  On failure the second try will be to use  {@code path} as URL and open
 *  a connection.<br />
 *  If  {@code path} is empty System.in will be wrapped as Input object.<br />
 *  <br />
 *  The call is equivalent to 
 *  {@link #openFileOrURL(String, boolean) openFileOrURL(path, false)}.<br />
 *  <br /> 
 *  @param  path    the name of the file or the URL
 *  @return an Input object
 *  @exception IOException no construction possible.
 *  @see #name
 *  @see #dataFile
 *  @see #url
 *  @see #Input(File)
 *  @see #Input(URL, boolean)
 *  @see #Input(InputStream, String)
 */
  public static Input openFileOrURL(String path) throws IOException {
     return openFileOrURL(path, false);
  } // openFileOrURL(String)

/** Fetch an Input object by complete path specification. <br />
 *  <br />
 *  The method is equivalent to
 *  {@link #openFileOrURL(java.lang.String)  openFileOrURL(String path)}
 *  with the extra function to immediately start reading and recording if
 *  {@code thraedRec} is true. (This is done by an anonymous 
 *  inner Thread.)<br />
 *  @param path  names a file or resource for input (null gets standard in)
 *  @param threadRec true: use an extra thread for reading and recording
 *  @return  the input made
 *  @throws  IOException  file or stream problems
 */
  public static Input openFileOrURL(String path, boolean threadRec)
                                                        throws IOException {
     if (path != null) {
        path = path.trim();
        if (path.isEmpty()) path = null;
     }
     if (path == null) return new Input(System.in);
     Input nei  = null;  
     File  inFile = null;
     URL  url   = null;
     try {
        inFile = FileHelper.getInstance(path);
        nei   = new Input(inFile); 
     } catch (FileNotFoundException e) {
        try { // URL instead of file
           url = new URL(path);
           nei = new Input(url, true);
        } catch (MalformedURLException me) {
           throw new IOException("No file : "
                                   +  e.getMessage() + " \nand no URL "
                                   + me.getMessage() );
        } catch (IOException ie) {
           throw new IOException("No file : "
                             +  e.getMessage() + " \nand no accessable URL " 
                                   + ie.getMessage() );
        } // catch FnF und URL
     } catch (SecurityException e) {
        throw new IOException(path + ": input file " + 
                                " SecurityException " + e.getMessage()+"\n");
     } // try-catch open source
     if (threadRec) nei.record(true);
     return nei;
  } // openFileOrURL(String)

//-----    Constructors    -----------------------------------------------

/** Constructor for a File. <br />
 *  <br />
 *  An Input object is made that reads from a {@link File}.<br />
 *  <br />
 *  @param inFile  must represent a readable file
 *  @exception  java.io.FileNotFoundException as by FileInputStream(File)
 */
   public Input(File inFile) throws java.io.FileNotFoundException {
      in  =  new FileInputStream(inFile);
      this.dataFile = inFile;
      this.url = null;
      name = inFile.getName();
      long l = inFile.length();
      count = l >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) l;
      try {
         date = inFile.lastModified();
      } catch (SecurityException sex) {
         date = 0L;
      }
   } // Input(File) 


/** Constructor for an URL. <br />
 *  <br />
 *  An Input object is made that reads from a URL (connection).<br />
 *  <br />
 *  @param url  An URL object to get the input from
 *  @param open true: try to open the connection at once (in the constructor)
 *  @exception  java.io.IOException as by url.openStream
 */
   public Input(URL url, boolean open) throws IOException {
      this.url  = url;
      if (open) {
         this.urlConnection  = url.openConnection();
         in             = urlConnection.getInputStream();
         urlCnD();
      } else closed = true;   
      this.dataFile = null;
      String name = url.getFile();
      if (name == null || name.isEmpty()) { 
          name = null;
      } else {
         int trPos = name.lastIndexOf('/');
         if (trPos > 0)
            name = name.substring(trPos);
      }   
      this.name = name;
      //// urlCnD();
      /// System.out.println(" /// TEST Input(URL " + url.getAuthority() 
        ///     + " - " + name  + ", modif "        + new Time(date));
      if (count < 0) count = 0;
   } //  Input(URL 

/** Determine URL's date and length. <br /> */
   private void urlCnD() {
      count = urlConnection.getContentLength();
      if (count < 0) count = 0;
      date =  urlConnection.getHeaderFieldDate("last-modified", 0L);
      if (date == 0L) {
         date =  urlConnection.getHeaderFieldDate("date", 0L);
      }
   } //  urlCnD()  Hilfsmethode

   
/** Constructor for a named InputStream. <br />
 *  <br />
 *  An Input object is made that reads from another InputStream.<br />
 *  <br />
 *  {@link #dataFile}, {@link #url} and {@link #urlConnection} will be
 *  null.<br />
 *  <br />
 *  There will be no information on {@code is}'s kind or date.<br />
 *  <br />
 *  @param is    An InputStream to be used as input
 *  @param name  A simple name to tag to it; may be null
 */
   public Input(InputStream is, String name){   
      in = is;
      this.name = (name != null && name.length() > 0) ? name : null;
      this.dataFile = null;
      this.url = null;
      try {
         count = in.available();
      } catch (Exception e){} //ignore
   } // Input(InputStream, String) 


/** Constructor for an InputStream. <br />
 *  <br />
 *  It is equivalent to {@link #Input(InputStream, String) Input(is, null)}.
 */
   public Input(InputStream is){ this(is, null); }


/** Constructor for a named byte array. <br />
 *  <br />
 *  An Input object is made that reads from the supplied byte[] array.<br />
 *  <br />
 *  {@link #dataFile}, {@link #url} and {@link #urlConnection} will be
 *  null.<br />
 *  <br />
 *  The {@link Input} is (not as surprise) buffered / recorded from the
 *  beginning.<br />
 *  <br />
 *  @param ba   the content to read, must not be empty
 *  @param name  A simple name to tag to it; may be null
 */
   public Input(byte[] ba, String name, long date){   
      this.name = (name != null && name.length() > 0) ? name : null;
      this.dataFile = null;
      this.url = null;
      buf = ba;
      count = ba.length;
      buffered = true;
      this.date = date;
   } // Input(InputStream, String) 


/** Re-utilise with a byte array.  <br />
 *  <br />
 *  This method returns a (this) Input object, that reads from now on from
 *  the supplied byte[] array.<br />
 *  <br />
 *  {@link #dataFile}, {@link #url} keep their value. {@link #urlConnection} 
 *  will be null and {@link #date} will be set by the parameter
 *  {@code date}.<br />
 *  <br />
 *  The {@link Input} is buffered / recorded from then on.<br />
 *  <br />
 *  @param ba   the content to read, must not be empty.
 *    &nbsp;    No copy is made. If null the the internal buffer used so
 *              far is taken. (Know what you do using this efficiency
 *              option.)
 *  @param date the modification date to use (0L = undefined or invalid)
 *  @since V02.32 (07.03.2008)
 */
   public synchronized Input recycle(byte[] ba, long date){   
      if (ba != null) buf = ba;
      count = buf.length;
      buffered = true;
      this.date = date;
 
      in  = null;
      urlConnection = null;
      bao = null;
      readError = null;
      bao = null;
      pos = markPos = 0;
      closed = partlyReadUnBuffered = recording  = false;
      return this;
   } // Input(InputStream, String) 


//------------  methods   --------------------------------------------------

   @Override public int available() throws IOException {
      if (closed) return 0;
      synchronized (this) {
         if (buffered || count > 0 && url != null)
            return count - pos;
         return in == null ? 0 : in.available();   
      }
   } // available()

   @Override public synchronized void mark(int readlimit){
      if (closed) return;
      synchronized (this) {
         if (buffered) {
            markPos = pos;
            return;
         }
         if (in != null) in.mark(readlimit);
      }
   } // mark(int)

   @Override public boolean markSupported(){
      return !closed 
         && (buffered  || in != null && in.markSupported()) ;   
   } // markSupported()

   @Override public synchronized void reset() throws IOException {
      if (closed) return;
      synchronized (this) {
         if (buffered) {
            pos = markPos;
            return;
         }
         if (in != null) in.reset();
      }
   } // reset() 
   
/** Reading one byte. <br />
 *  <br />
 *  A single byte (unsigned 0..255 as int) will be read from this Input.<br />
 *  <br />
 *  EOF returns -1.<br />
 *  <br />
 *  @exception    java.io.IOException as by the underlying Stream (if any)
 *  @return       the unsigned 8-bit value or -1
 *  @see Input#read(byte[], int, int)
 */
   @Override public int read() throws IOException {
      int readB = -1;
      if (!closed) synchronized (this) {
         if (buffered) {
            if (pos >= 0 && pos < count) {
               readB = buf[pos];
               if (readB < 0) readB += 256; // -128 .. 0 .. +127 
            } else pos = count;
         } else if (in != null) {
            readB = in.read();
            wasRead |= readB >= 0;
            partlyReadUnBuffered = true;
         }
         if (readB >= 0) {
            ++pos;
            if (pos > count) count = pos;
         }
      } // sync
      return readB;
   } // read()


/** Reading multiple bytes. <br />
 *  <br />
 *  @see Input#read(byte[], int, int) Input.read(...)
 */
   @Override public int read(final byte buf[], final int off, final int len){
      if (closed) return -1;              
      if (buf == null) return 0;
      int bl = buf.length;
      if (bl == 0 || len < 1 || off < 0 || off >= bl)  return 0;
      bl = bl - off;
      if (len < bl) bl = len;  // bl to be read
      int st = off;
      if (!closed) synchronized (this) {
         if (buffered) {
            int avail = count - pos;
            if (pos >= 0 && avail > 0) {
               if (bl > avail) 
                  bl = avail;
               else 
                  avail = bl;
               for ( ; avail > 0; ++pos, ++st, --avail){
                  buf[st] = this.buf[pos];
               }
               return bl;
            }
            pos = count;
            return -1; 
         } else if (in != null) try {
            bl = in.read(buf, st, bl);
            partlyReadUnBuffered = true;
            wasRead |= bl > 0;
            if (bl >= 0){
               pos += bl;
               if (count < pos) count = pos;
               return bl; 
            }
            count = pos;
            ++pos;
            return -1;
         } catch (Exception e) {
            return -1;
         }
      } // sync !closed
      return -1;
   } // read ( , ,)

   @Override public long skip(final long n){
      if (closed || n <= 0) return 0;
      if (!closed) synchronized (this) {
         if (buffered) {
            int avail = count - pos;
            if (avail <= 0) return 0;
            int skl = n > avail ? avail : (int) n;
            pos += skl;
            return skl; 
         } else if (in != null) try {
            long ret = in.skip(n);
            partlyReadUnBuffered = true;
            int bl = (int) ret;
            if (bl >= 0){
               pos += bl;
               if (count < pos) count = pos;
            }
            return ret;
         } catch (Exception e) {
            return 0;
         }
      } // sync !closed
      return 0;
   } // skip(long)


/** Reading the begin of the Input into a buffer. <br />
 *  <br />
 *  The byte[] array {@code buf} will be filled with this Input's start of 
 *  content respectively start of content not yet read.<br />
 *  <br />
 *  Reading by {@link #read(byte[], int, int) read(...)} is used perhaps 
 *  multiple times until either the array supplied is filled, EOF is
 *  reached or a problem occurred. The multiplicity of the tries may be 
 *  necessary for some streams with diffuse buffering strategies or which
 *  never know how long the will be.<br />
 *  <br />
 *  returned is the number of bytes read. 0 if {@code buf} is ofzero length or
 *  null and -1 if the very first read shows EOF.<br />
 *  <br />
 *  The call is equivalent to (may be multiple) 
 *  {@link #read(byte[],int,int) read(byte[], int, int}.<br />
 *  <br />
 *  @param buf    to put the read content in
 *  @return       number of bytes read / put
 *  @see Input#read(byte[], int, int)
 *  @see Input#read()
 *  @see Input#close
 */
   public  int readStartOfContent(final byte[] buf){
     if (closed) return -1;
     if (buf == null) return 0;
     int gl = buf.length;
     syB: if (gl != 0) synchronized (this) { 
       gl = read(buf, 0, buf.length);
       if (gl <= 0) break syB; //  return gl;
       readLoop: while (gl < buf.length) {
          int rl = read (buf, gl , buf.length - gl);
          if (rl > 0) 
             gl += rl;
          else
             break readLoop;
       } // readLoop
     } // syB
     return gl;
   }  //  readStartOfContent(byte[])


/** Closing the Input. <br />
 *  <br />
 *  The underlying stream / connection or what else if given is closed and
 *  forgotten. Exceptions occurring on that process are silently ignored. The
 *  underlying input is not more usable here anyway.<br />
 *  <br />
 *  Not forgotten are the {@link #getName() name} as well as all buffered 
 *  content of {@link #getGesLen() gesLen} bytes. That can be used and 
 *  retrieved / copied as String or byte[].<br />
 *  <br />
 */
   @Override public void close(){
      if (closed) return;
      synchronized(this) {
         closed = true;
         if (in != null) try {in.close();} catch (Exception e) {}
         in  = null;
         urlConnection = null;
         bao = null;
      }
   } // close()

/** Re-open the source. <br />
 *  <br />
 *  This Input will be closed as by {@link #close close()}.<br />
 *  <br />
 *  If it is based on a {@link File File} {@link #dataFile} or an URL 
 *  {@link #url} that will be re-opened for reading. This can be quite 
 *  sensible for files and URLs having a dynamically changing content and it 
 *  is than cheaper than the re-making of all objects involved. If that 
 *  re-connecting / -opening succeeds true is returned.<br />
 *  <br />
 *  If {@code bufThread} is true, the &quot;new&quot; Input will be completely
 *  read / buffered by a concurrent anonymous thread.<br />
 *  <br /> 
 *  @see #record  record()
 *  @param bufThread true: use an extra thread for reading and recording
 *  @return  true if input is re-opened without errors (ready for use)
 *  @throws  IOException  file or stream problems
 */
   public synchronized boolean reopen(boolean bufThread) throws IOException {
      if (!closed) close();
      readError = null;
      bao = null;
      buf = null;
      pos = markPos = 0;
      count = -1;
      partlyReadUnBuffered = recording = buffered = false;
      
      if (url != null) {
         urlConnection = url.openConnection();
         in = urlConnection.getInputStream();
         urlCnD();
         closed = false;
      } else if (dataFile != null) {
         in  =  new FileInputStream(dataFile);
         long l = dataFile.length();
         count = l >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) l;
         closed = false;
      }
      if (bufThread && in != null) record(true); 
      return in != null && readError != null;
   } // reopen(boolean)


//-----------------------------------------------------------------------

/** Output a test description of this Input. <br />
 *  <br />
 *  A multi-line (state) description of this Input will be sent to 
 *  {@code out}.<br />
 *  <br />
 *  @param  out the Writer to put the descriptive lines to
 *  @return true if backed by a File
 */
   public boolean testDescribe(PrintWriter out){
      if (dataFile != null) return FileHelper.testDescribe(dataFile, out);
      if (url != null) 
         out.println("URL: " + url);
      else
         out.println("Input: "  + (name != null ? name 
              : (in == null ? "null" : in.toString() ) ));
      return false;
   } // testDescribe(PrintWriter)


/** Generate a describing line. <br />
 *  <br />
 *  In case of being backed up by a real file a line with its name, length,
 *  date and attributes is made; see
 *  {@link #dataFile}.{@link FileHelper#infoLine(File, StringBuilder, boolean, boolean)
 *   infoLine(dataFile,null, false, false)}.<br />
 *  <br />
 *  Otherwise &quot;URL: &quot; followed by {@link #url}'s description is
 *  returned, if {@link #url} is not null.<br />
 *  <br />
 *  If neither can be made 
 *  &quot;Input: &quot; + {@link #name} or  
 *  &quot;Input: &quot; + {@link #in}.toString() is returned.<br />
 *  <br />
 *  @return the line
 *  @see FileHelper#infoLine(File)
 *  @see Input#dataFile
 *  @see Input#url
 */
   public String listLine(){
      if (dataFile != null)
         return FileHelper.infoLine(dataFile, null, false, false).toString();
      if (url != null)   return "URL: " + url;
      if (name != null)  return "Input: " + name; 
      return "Input: " + in;
   } // listLine()

/** The Input has delivered something. <br />
 *  <br />
 *  Returns true, if the input has delivered since the inquiry by this 
 *  method.<br />
 */
   public boolean wasActive(){
      boolean ret = wasRead;
      wasRead = false;
      return ret;
   } // wasActive() 
   
   boolean wasRead;

/** Write / copy to an OutputStream. <br />
 *  <br />
 *  If this {@link Input} was {@link #isBuffered() buffered()} the whole 
 *  content will be written to {@code out}. This works even if this 
 *  {@link Input} was {@link Input#close() closed} in between. That means
 *  multiple calls again transport the same content.<br />
 *  <br />
 *  If on the other hand this {@link Input} was not 
 *  {@link #isBuffered() buffered()} all content not yet read will be copied
 *  to {@code out}. This {@link Input} will be {@link Input#close() closed}
 *  afterwards.<br />
 *  <br />
 *  @return &gt;= 0 : number of bytes copied,<br /> &nbsp;
 *          -1: out is null.<br /> &nbsp;
 *          -3: broken by exception.
 *  @see Input#close
 *  @param out the stream to copy to
 */
   public int copyTo(OutputStream out){
      if (out == null) return -1;
      synchronized (this) {
         if (buffered) {
            try {
                out.write(buf);
                return count;
             } catch (IOException ioeW) {
                return -3;
             }
         } // buf here may be closed
         if (in == null) return 0;
         
         byte by[] = new byte[2000];
         int  len, gesL = 0;
         try {      
            while ( (len = in.read(by)) > 0 ) { 
               wasRead = true;
               out.write(by, 0, len);
               gesL +=len;
            } // while
            out.flush();
            if (out == bao) { // only by record()
               count = gesL;
               markPos = pos = 0;
               in.close();
               in  = null;
               buf = null;
               if (count > 0) {
                  buf =  bao.toByteArray();
                  buffered = true;   
               }
               bao = null;
               recording = false;
            } else // by record else normal
               close(); // 19.09.2002
            return gesL;
         } catch (IOException e) {
            if (out == bao) {
               readError = e;
               recording = false;
            }
            return -3;
         }  
      } // sync this
   }  // copyTo(OutputStream)


/** Recording the input. <br />
 *  <br />
 *  This {@link Input}'s content respectively the yet unread rest will be 
 *  buffered internally. Afterwards, the buffer content can be retrieved (even 
 *  multiple times) by the methods {@link #getAsString getAsString()}, 
 *  {@link #getAsBytes getAsBytes()} or {@link #copyTo copyTo()}.<br />
 *  <br />
 *  The buffering / recording can be ordered to be done in a concurrent 
 *  thread by supplying setting {@code multiThread} as true). The method then
 *  returns immediately.<br />
 *  <br />
 *  @return true if no concurrent buffering is running or if no further
 *  input is possible (due to empty, EOF or closed). The method
 *  {@link #getGesLen getGesLen()} then gives the number of read / buffered 
 *  bytes.<br />
 *  false if buffering / recording is still operating in a 
 *  concurrent thread.<br />
 *  @param multiThread true: use another thread for recording
 */
   public boolean record(final boolean multiThread){
      if (closed) return true;
      synchronized (this) { // bao == null bat make only once
         if (bao != null || buf != null) { // running or ran
            if (multiThread) return !recording;  // out at once
            while (recording) {
               if (closed) return true;
               try {
                  wait(16030); // max 12 s
               } catch (InterruptedException e) {  }
            }  // while recording
            return !recording;
         } // runs or ran
         recording = true;
         bao = new ByteArrayOutputStream();
         if (multiThread) {
            Thread anoReadThread = new Thread() {
               @Override public void run() { 
                 synchronized (Input.this) {
                    copyTo(bao); // sets recording to false
                    Input.this.notifyAll();
                 } // sync
              } // run
           };
           anoReadThread.setDaemon(true);
           anoReadThread .start();
         } else {
            copyTo(bao); // sets recording false
         }
      }  // sync this bao == null but make only once
      return !recording;
   } // record(boolean)
   

/** Getting the Input's content as String. <br />
 *  <br />
 *  This {@link Input}'s content respectively the yet unread rest will be 
 *  buffered internally using {@link #record record()}, if that was not yet 
 *  done. If nothing is to be read and nothing was buffered null is
 *  returned.<br />
 *  <br />
 *  Otherwise the read bytes are converted to a not empty String using 
 *  {@code encoding}. If {@code encoding} is empty or 
 *  &quot;defaultEncoding&quot;,
 *  {@link ComVar ComVar}.{@link ComVar#FILE_ENCODING FILE_ENCODING} 
 *  will be used (most often ISO-8859-1 or Cp1252 on Windows).<br />
 *  <br />
 *  @param  encoding the encoding from byte to Unicode
 *  @return the input bytes converted to a String
 *  @exception UnsupportedEncodingException if the parameter encoding is wrong
 *  @exception IOException if one happened during {@link #record record()}
 *  @see #close
 *  @see #copyTo copyTo(InputStream)
 *  @see ComVar#FILE_ENCODING
 */
   public synchronized String getAsString(String encoding) 
                            throws UnsupportedEncodingException, IOException {
      if (readError != null) throw readError;
      if (buf == null) {
         record(false);
         if (readError != null) throw readError;
      }    
      if (count == 0 || buf == null) return null;
      if (encoding == null || encoding.isEmpty()
            || TextHelper.areEqual("defaultEncoding", encoding, true))
         encoding = ComVar.FILE_ENCODING;
      return new String(buf, encoding);
   }   // String getAsString(String)


/** Getting the Input's content as String. <br />
 *  <br />
 *  This {@link Input}'s content respectively the yet unread rest will be 
 *  buffered internally using {@link #record record()}, if that was not yet 
 *  done. If nothing is to be read and nothing was buffered null is
 *  returned.<br />
 *  <br />
 *  Otherwise the read bytes are converted to a not empty String using 
 *  {@code encoding}. If {@code encoding} is null 
 *  {@link ComVar ComVar}.{@link ComVar#FILE_ENCODING FILE_ENCODING} 
 *  will be used (most often ISO-8859-1 or Cp1252 on Windows).<br />
 *  <br />
 *  @param  encoding the encoding from byte to Unicode
 *  @return the input bytes converted to a String
 *  @exception UnsupportedEncodingException if the parameter encoding is wrong
 *  @exception IOException if one happened during {@link #record record()}
 *  @see #close
 *  @see #copyTo copyTo(InputStream)
 *  @see #getAsString(String)
 */
   public synchronized String getAsString(Charset encoding) 
                            throws UnsupportedEncodingException, IOException {
      if (readError != null) throw readError;
      if (buf == null) {
         record(false);
         if (readError != null) throw readError;
      }    
      if (count == 0 || buf == null) return null;
      if (encoding == null)  return new String(buf, ComVar.FILE_ENCODING);
      return new String(buf, encoding);
   }   // String getAsString(String)

/** Getting the Input's content as byte[]. <br />
 *  <br />
 *  This {@link Input}'s content respectively the yet unread rest will be 
 *  buffered internally using {@link #record record()}, if that was not yet 
 *  done. If nothing is to be read and nothing was buffered null is
 *  returned.<br />
 *  <br />
 *  Otherwise an non empty array with a copy of the input bytes  is 
 *  returned.<br />
 *  <br />
 *  @return the input as byte[] (copy)
 *  @exception IOException if one happened during {@link #record record()}
 *  @see #close close()
 *  @see #copyTo copyTo(InputStream)
 *  @since V00.04 19.09.2002
 */
   public synchronized byte[] getAsBytes() throws  IOException {
      if (readError != null) throw readError;
      if (buf == null) record(false); // try in this
      if (readError != null) throw readError;
      return buf.clone();
   } // getAsBytes
   

/** Getting the Input's content as byte[] and close. <br />
 *  <br />
 *  If this Input is closed already and reopen is true, it will be tried to
 *  reopen it by {@link #reopen(boolean) reopen(true)}.<br />
 *  <br />
 *  This {@link Input}'s content respectively the yet unread rest will be 
 *  buffered internally using {@link #record record()}, if that was not yet 
 *  done. If nothing is to be read and nothing was buffered null is
 *  returned.<br />
 *  <br />
 *  Otherwise an non empty array containing the input bytes is returned.<br />
 *  Attention: This array is the internal buffer itself, not a copy.<br />
 *  The contained must be processed or copied by the user before the next
 *  round ({@link #reopen(boolean) reopen()}) should that be planned.<br />Y
 *  <br />
 *  @return the input as byte[] (no copy)
 *  @exception IOException if one happened during {@link #record record()}
 *  @see #close close()
 *  @see #copyTo copyTo(InputStream)
 *  @since V00.04 19.09.2002
 */
   public byte[] getAsBytesAndClose(boolean reopen) throws  IOException {
      byte[] bB = null;
      synchronized(this) {
         if (!(closed && reopen)) {
            if  (readError != null) throw readError;
            bB = buf; // != null means ready
         }
         if (closed && reopen) {
            reopen(true);
            if (readError != null) throw readError;
         }
         if (bB == null) bB = buf; 
         if (bB == null) record(false); // try in this thread
         bB = buf;
         if (readError != null) throw readError;
         // close() in-line
         closed = true;
         if (in != null) try {in.close();} catch (Exception e) {}
         in  = null;
         urlConnection = null;
         bao = null;
      }
      return bB;
   } // getAsBytesAndClose(boolean)


final static boolean RESTEST2 = false; // enable development logs on S.out

/** Get an input stream for a file name as resource. <br />
*  <br />
*  It will be tried to determine an input stream 
*  ({@link java.io.InputStream})from the {@code fileName} provided, that must
*  contain no path (not even .\).<br />
*  <br />
*  It will will be tried to open:<ul>
*  <li>a resource related to baseClass. (This would in effect be
*      a file within the deployment .jar that class was loaded from),</li> 
*  </li></ul> as InputStream.
*  <br />
*  @return  an input stream in case of success; null else
*/ 
 public static InputStream getAsResourceStream(String fileName,
                                     Class<?> baseClass){
    if (baseClass == null) return null;

    if (RESTEST2) {
       System.out.println("   ///   TEST   getAsStream(" + fileName + ")");
       System.out.flush();
    }
    fileName = TextHelper.makeFName(fileName, null);
    if (fileName == null) return null;

    // check relative to actual path (not ../ or D:)
    if (fileName.indexOf(':') >= 0)  return null; 
    char firstC = fileName.charAt(0);
    if (firstC == ComVar.FS || firstC == '.') 
       return null;   // no path allowed

    if (ComVar.FS == '\\')  fileName = fileName.replace('\\','/');
    final String n = baseClass.getName(); 
    if (RESTEST2) {
       System.out.println("   ///   TEST   getAsStream " + fileName 
                                           + " by class " + n + ")");
       System.out.flush();
    }

    InputStream ret = null;
    try {
       ClassLoader clld = baseClass.getClassLoader();
       ret = clld.getResourceAsStream(fileName);
       if (ret == null) {
          if (RESTEST2) System.out.println("   ///   TEST   not found ");
       } 
    } catch (Exception e) {
       if (RESTEST2) System.out.println("   ///   TEST " + e);   
    }
    return ret;     
 } // getAsResourceStream(String, Class)

} // class Input (2003, 2008, 14.03.2019)
