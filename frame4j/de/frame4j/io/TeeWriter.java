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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import de.frame4j.util.App;
import de.frame4j.util.ComVar;
import de.frame4j.util.MinDoc;
import de.frame4j.text.TextHelper;

/** <b>A branching Writer</b>. <br />
 *  <br />
 *  It is a recurring requirement to branch one output to two streams. One
 *  standard example are log messages for a (console) output towards a human
 *  observer that should or must also go into a log file. Handling this 
 *  branching at the application programming is cumbersome, hardly 
 *  maintainable, resource consuming and error-prone. (And it is less trivial
 *  as fist sight may reveal.)<br />
 *  <br />
 *  <b> &nbsp; Structure and function</b><br />
 *  <br />
 *  A TeeWriter &mdash; so called after the capital letter <i>T&nbsp;</i>
 *  respectively the Unix tool Tee &mdash; is a {@link java.io.Writer}, that
 *  branches the output it receives to up to two other
 *  {@link java.io.Writer Writer}s.<code><pre>
 *   &nbsp;     out1 -- &lt; ----X-[ Thread ]----------- &gt; -- out2
 *   &nbsp;        (flush1()) block1()         /    (flush2())
 *   &nbsp;                   unBlock1()      /
 *   &nbsp;                                  /
 *   &nbsp;   (write())                     /
 *   &nbsp;    -- &gt; ------[ Buffer ]-------/
 *   &nbsp;                        :
 *   &nbsp;                        :.....- &gt;  (getContent()) </pre></code>
 *  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
 *           The TeeWriter's structure<br />
 *  <br >
 *  Branching to more than two {@link java.io.Writer Writer}s may be done by
 *  cascading / decorating. Extra abilities like line structure and formating
 *  (by {@link java.io.PrintWriter PrintWriter}) etc. will be put once and 
 *  for all in front of the (first) TeeWriter (decorator pattern). For a 
 *  decorating {@link java.io.PrintWriter PrintWriter}, almost always needed,
 *  there is a method {@link #getPrintWriter getPrintWriter()}. And the 
 *  buffering (usually used in line oriented output with good reason) is done
 *  in the TeeWriter.<br />
 *  <br />
 *  So, besides the basic branching function, a TeeWriter additionally 
 *  features those functions:<ul>
 *  <li>build in buffering. Prepending a decorating 
 *      {@link java.io.BufferedWriter BufferedWriter} is never needed.</li>
 *  <li>snap shoots of the actual buffer (as String). That is used for e.g.
 *      for extra display windows showing the last (tail) log outputs.</li>
 *  <li>De-coupling the output to one of the connected Writers {@link #out1}
 *      by a Thread. This (plus the internal buffering) the delivering / 
 *      outputting / working thread is effectively shielded from the 
 *      &quot;taking qualities&quot; of the first Writer {@link #out1}.</li>
 *  <li>Switchability of both output writers without bothering the 
 *      &quot;front end&quot;.</li>      
 *  </ul>
 *  The third point makes {@link TeeWriter} a de-coupling, non-blocking and
 *  in that sense &quot;better&quot; {@link java.io.BufferedWriter}.<br />
 *  The last point brings the switching of log files or encodings without the
 *  logging / working users having to take notice. Besides the obvious comfort
 *  effect, this switching ability allows publishing the used
 *  {@link TeeWriter} (log writer) as final, so solving a lot of concurrency
 *  problems without loosing the hinted flexibility.<br />
 *  For the input side {@link SwitchedReader} brings that switchabilty / 
 *  flexibility, being in this respect only {@link TeeWriter}'s 
 *  complement.<br />
 *  <br /> 
 *  <b> &nbsp; Exception handling</b><br />
 *  <br />
 *  It is unwanted, that Exceptions occurring to one of two output Writers
 *  {@link #out1} or {@link #out2} impede the writing to the {@link TeeWriter}
 *  as whole. So they are caught internally and the methods
 *  {@link #write write(..)} and others are free from menaced exceptions.
 *  About problems arisen one may extract information via {@link #exc1}, 
 *  {@link #exc2} and {@link #lostChars1}.<br />
 *  <br /> 
 *  <b> &nbsp; Buffer flushing</b><br />
 *  <br />
 *  The flushing to the connected Writers may be ordered explicitly by 
 *  {@link #flush1()}, {@link #flush2()} or {@link #flush()}). A prepended 
 *  {@link java.io.PrintWriter} with autoFlush (see 
 *  {@link #getPrintWriter(boolean)}) automatically calls {@link #flush()} at
 *  line boundaries.<br />
 *  The TeeWriter itself flushes 
 *  <ul><li>to every connected Writer individually, if its buffered and not 
 *      yet output share goes above a threshold {@link #autoFlushThr}</li>
 *  <li>and additionally for the thread de-coupled Writer {@link #out1} every
 *      6.8&nbsp;s (if something is ready to be output there) respectively as
 *      late as every 121&nbsp;s is {@link #noExplFlush1} is set.</li>
 *  </ul>
 *  The second point ensures the (human) user seeing anything (even not 
 *  complete lines) after at least 7 seconds or 2 minutes no matter what 
 *  implicit or explicit flushing is used (or not).<br />
 *  <br />
 *
 *  <b> &nbsp; Hints on blocking normal output</b><br />
 *  <br />
 *  It's not so widely known that console / shell outputs (usually taken
 *  as {@link java.lang.System#out System.out} or 
 *  {@link java.lang.System#err System.err}) may block. As a matter of fact a 
 *  (some Window's at least) console output is blocked for ever by just 
 *  marking text. (And this quite often happens inadvertently e.g. by mouse
 *  movements, may be cause on error about the focus.)<br />
 *  The frequent use case is a long running server application branching to a 
 *  log file ( {@link #out2}) and to the console ({@link #out1}). The latter 
 *  is just done for the seldom case of a human controller / observer 
 *  watching. Of course here it would be unwanted to catastrophic if a 
 *  night-watchman on his round would block the console (which may not be 
 *  avoided) and hence the server application.<br />
 *  <br /> 
 *  <b> &nbsp; The asymmetry of &quot;Thread de-coupling&quot;</b><br />
 *  <br />
 *  This feature is done only for one ({@link #out1}) of the both output
 *  Writers. The rationale behind is on one hand an expected technical
 *  asymmetry of the branches, like console and file in the above example.
 *  On the other hand it is an usual asymmetry of the requirements. One of the
 *  outputs (the log file in the above example) has to have the guarantee to
 *  get all outputs without loosing a single character. If it blocks due to 
 *  speed problems beyond what buffering can level the outputting thread has
 *  to be blocked. It is the responsibility of the user / programmer / 
 *  start-up operator to use performing and reliable resources (local file on 
 *  fast and big drive or RAID e.g.) for those purposes.<br />
 *  On the other extra / supplementary / unreliable branches outputs the 
 *  loosing of output is, of course, not quite desirable but much more 
 *  acceptable then blocking the application.<br />
 *  <br />
 *  TeeWriter's ability to tolerate (erroneous) transitional blocking of 
 *  {@link #out1} allows to use those blocking by will. This is done by the 
 *  methods ({@link #block1()} and {@link #unBlock1()}.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 1998 - 2003, Albrecht Weinert. 
 *
 *  @see App
 *  @see de.frame4j.io.Input
 */
 // so far    V00.00 (10:16 25.11.1998) :  new
 //           V00.01 (13:48 08.05.2000) :  ex weinertBib -> sub package 
 //           V01.00 (06.12.2001 16:58) :  Symmetric, bufferd, Threads  
 //           V01.01 (23.08.2002 16:23) :  close1 missing at setOut1  
 //           V02.00 (24.04.2003 16:54) :  CVS Eclipse
 //           V02.21 (16.05.2005 17:59) :  isOpen
 //           V.o75  (12.02.2009 19:21) :  ported to Frame4J
 //           V.o20+ (20.02.2010 17:10) :  fled from Kenai to new SVN

@MinDoc(
   copyright = "Copyright  2001 - 2002, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use where one Writer is handled but two are needed",  
   purpose = "a Writer that outputs the content to two branches"
) public class TeeWriter extends Writer {

/** Make a TeeWriter for an OutputStream. <br />
 *  <br />
 *  A TeeWriter is made and its first output Writer is connected to the 
 *  Stream {@code out} supplied as parameter using the default encoding.<br >
 *  <br />
 *  The call is equivalent to:  &nbsp; new 
 *  {@link #TeeWriter(java.io.Writer) TeeWriter}(new 
 *  {@link java.io.OutputStreamWriter}(out)).<br />
 *  <br />
 *  @param out the stream to be used as one T output
 *  @throws NullPointerException if out is null
 */
   public TeeWriter(OutputStream out){ this(new OutputStreamWriter(out)); }
   
/** Make a TeeWriter with one output. <br />
 *  <br />
 *  A TeeWriter is made and its first output Writer is connected to the 
 *  supplied {@code writer1}.<br />
 *  <br />
 *  The internal buffer size {@link #buffLen} is set to 20480 characters and
 *  the maximal blocking time {@link #maxBlockTime} to 50 ms.<br />
 *  @param writer1 the Writer to be used as one T output
 */
   public TeeWriter(Writer writer1){
      this(20480, 50);
      setOut1(writer1);
   } // TeeWriter(Writer


/** Make a TeeWriter with two outputs. <br />
 *  <br />
 *  A TeeWriter is made and its two outputs / branches are connected to the 
 *  supplied {@code writer1} and {@code writer2}.<br />
 *  @param writer1 the Writer to be used as one T output
 *  @param writer2 the Writer to be used as second T output
 */
   public TeeWriter(final Writer writer1, final Writer writer2){
      this(writer1);
      if (writer1 != writer2) setOut2(writer2);
   }  // TeeWriter(Writer

/** Make a TeeWriter with two outputs. <br />
 *  <br />
 *  A TeeWriter is made and its two outputs / branches are connected to the 
 *  supplied Writer {@code writer1} and OutputStream {@code os2}, the latter
 *  using default encoding.<br />
 *  @param writer1 the Writer to be used as one T output
 *  @param os2     the stream to be used as second T output
 */
   public TeeWriter(final Writer writer1, final OutputStream os2) {
       this(writer1);
       if (os2 != null) {
          setOut2(new OutputStreamWriter(os2));
       }
   } // TeeWriter(Writer, OutputStream)

/** Make a TeeWriter with no outputs. <br />
 *  <br />
 *  A TeeWriter is made and its two outputs are unconnected.<br />
 *  <br />
 *  The internal buffer size {@link #buffLen} and the maximal blocking time
 *  are set according to the supplied parameter values.<br />
 *  <br />
 *  The buffer size is limited to the range 1024 .. 40000.<br />
 *  <br />
 *  @see #setMaxBlockTime(int)
 *  @param maxBlockTime new maximal blocking time in ms; limited to 4...9800
 *  @param buffLen the buffer size (1025 .. 400001)
 */
   public TeeWriter(int buffLen, final int maxBlockTime){
      super(); // use this as lock 
      if (++buffLen < 1025) buffLen = 1025;
      else if (buffLen > 400001) buffLen = 400001;
      this.buffLen = buffLen;
      buff = new char[buffLen];
      autoFlushThr = buffLen * 7 / 10;
      setMaxBlockTime(maxBlockTime);
   } // TeeWriter(int, int)


/** Get the (one) PrintWriter connected to this TeeWriter. <br />
 *  <br />
 *  This method gets one {@link java.io.PrintWriter} set as decorator in
 *  front of this TeeWriter. It is made on the first call.<br />
 *  <br />
 *  @param  autoflush if true, the <tt>println</tt>, <tt>printf</tt>, or 
 *                   <tt>format</tt> methods will flush the output buffer
 */
   public PrintWriter getPrintWriter(boolean autoflush){
      if (vPw != null) return vPw;
      synchronized (lock) {
         if (vPw != null) return vPw; // (volatile)
         vPw = new PrintWriter(this, autoflush);
      }
      return vPw;
   } //  getPrintWriter(boolean)
   
/** The PrintWriter &quot;before&quot; this TeeWriter. <br />
 *  <br />
 *  @see #getPrintWriter(boolean) getPrintWriter()
 */
   protected volatile PrintWriter vPw;


//---- The buffer / input to this TeeWriter   ---- (state)  -----------------

/** Closed and dead. <br /> */
   volatile boolean closed;

/** Closed and dead. <br />
 *  <br />
 *  Returns true , if this TeeWriter was closed.<br />
 *  <br />
 *  You cannot output to a closed TeeWriter. The methods write(), flush..() 
 *  and close() don't do anything at all in this state. Only buffer content 
 *  (if there) can be retrieved by  {@link #getContent getContent()} and  
 *  {@link #getContentLen()}.<br /> 
 *  <br />
 *  @see #close()
 *  @return true if already closed
 */
   public boolean isClosed(){ return closed; }

   
/** Not yet closed / still usable. <br />
 *  <br />
 *  Returns false if this TeeWriter was closed.<br />
 *  <br />
 *  @see #close()
 *  @see #isClosed()
 *  @see java.nio.channels.Channel#isOpen()
 *  @return true if still open (and hopefully usable) 
 */
   public boolean isOpen(){ return !closed; }


/** Close the TeeWriter. <br />
 *  <br />
 *  After closing the TeeWriter is &quot;dead&quot;, see 
 *  {@link #isClosed()}.<br />
 *  <br />
 *  If there are (decorating) prepended Writers the most in front of those
 *  should get the close() and not this TeeWriter directly. For the 
 *  PrintWriter made / got by {@link #getPrintWriter getPrintWriter()} this
 *  is not necessary.<br />
 *  <br />
 *  Exceptions are caught silently.<br />
 *  <br />
 *  @see #isClosed()
 */
   @Override public void close(){
      if (closed) return;
      synchronized (lock) {
         if (closed) return; // done in between 
         PrintWriter oldVpW =  vPw;
         vPw = null;
         if (oldVpW != null) { // recursion on first call
            try {
               oldVpW .close();
            } catch(Exception e){}
            if (closed) return; // done in between by above close
         } // recursion on first call
         closed = true; // prohibit all further processing
      }
      close1();
      close2();
   } // close()

/** The buffer. <br /> */
   final char[] buff;

/** The buffer's length. <br />
 *  <br />
 *  @see #getBuffLen
 */
   public final int buffLen;

/** Write pointer / index. <br /> */
   int buffIn;

/** The buffer was at least once full. <br /> */
   boolean filled;


/** This TeeWriter's buffer length. <br />
 *  <br />
 *  The TeeWriter buffers its incoming output. The buffers has a size between
 *  1024 and 400000 characters; the default is 20480 (20K).<br />
 *  <br />
 *  @see #flush1()
 *  @see #flush2()
 *  @see #flush()
 *  @see #getPrintWriter(boolean) getPrintWriter()
 *  @return the buffer's size as set finally on construction
 */
   public final int getBuffLen(){ return buffLen - 1; }


/** The buffer's current length / the number of available characters. <br />
 *  <br />
 *  @see #write      write()
 *  @see #close
 *  @see #getContent getContent()
 */
   public final int getContentLen(){ return filled ? buffLen - 1 : buffIn; }
   

/** The buffer's current content as String. <br />
 *  <br />
 *  Is the parameter value {@code maxLen} more than the actual buffer content
 *  size (see {@link #getContentLen()}) it will be reduced to it. This method
 *  returns up to maxLen buffered characters as String. Is early false, the 
 *  end of the buffer is chosen (the latest output) otherwise the start of the
 *  buffer (the oldest output not overwritten yet).<br />
 *  <br />
 *  If {@code maxNLsearch} is greater than 0 and less than 1/3 of the (may be
 *  reduced) {@code maxLen} the start and end of chosen buffer range is 
 *  shortened by at most {@code maxNLsearch} character to let the String 
 *  returned start and end at line borders. (80 .. 150 are a good 
 *  choices). no such clipping occurs on very first or very last (before
 *  close) output.<br />
 *  <br /> 
 *  If the buffer has no content or if {@code maxLen} is &lt;= 0, the
 *  empty String is returned.<br >
 *  @see #write(char)     write()
 *  @see #getContentLen   getContentLen()
 */
   public final String getContent(int maxLen, 
                                       boolean early, final int maxNLsearch){
    if (maxLen <= 0) return ComVar.EMPTY_STRING;
    synchronized(lock){ 
      int FillLen = filled ? buffLen - 1 : buffIn;
      if (FillLen == 0) return ComVar.EMPTY_STRING;
      if (maxLen > FillLen) {
         maxLen = FillLen;
         early = false; // simple case
      }
      int startInd = 0;
      if (early) {
         if (filled) {
            startInd = buffIn + 1;
            if (startInd == buffLen) 
               startInd = 0;
         } // early filled
       } else { // late
          startInd = buffIn - maxLen;
          if (startInd < 0) startInd += buffLen;
       } // late

       if (maxNLsearch > 0 && maxNLsearch < maxLen / 3) { // clip
          if (filled || startInd != 0) { // skip to NL begin
             for (int i = 0; i < maxNLsearch; ++i) {
                int ind = startInd + i;
                if (ind >= buffLen) ind -= buffLen;
                if (buff[ind] == '\n') {
                   ++i;
                   maxLen-= i;
                   startInd += i;
                   if (startInd >= buffLen) startInd -= buffLen;
                   break;
                } // found
             } // for
          }  // skip to NL begin
          int endInd = startInd + maxLen; 
          if (endInd >= buffLen) endInd -=buffLen;
          if (endInd != buffIn)  { // skip to NL end
             for (int i = 0; i < maxNLsearch; ++i) {
                if (--endInd < 0) endInd += buffLen;
                if (buff[endInd] == '\n') {
                   maxLen-= i;
                   break;
                } // found
             } // for
          }  // skip to NL End
      } // clip

      int over = (startInd + maxLen) - buffLen; 
      if (over <= 0) { // best case one step
         return new String(buff, startInd, maxLen);
      }
      StringBuilder bastel = new StringBuilder(maxLen + 2);
      bastel.append(buff, startInd, maxLen - over);
      bastel.append(buff, 0, over);
      return bastel.toString();
    } // sync.
   } // getContent



/** The threshold for automatic flushing. <br />
 *  <br />
 *  If at least {@code autoFlushThr} characters are held back in the buffer
 *  from one of the output writers it will be flushed.<br />
 *  Set to about 70% of the buffer's size ({@link #buffLen}).
 */
   public final int autoFlushThr;


//----   The first Writer / output from the TeeWriter   ------------------

/** The first Writer. <br />
 *  <br />
 *  This is one branch for the output.<br />
 */ // all writes guarded by lock; volatile for sake of unguarded reads
   protected volatile Writer out1;

/** The first Writer. <br />
 *  <br />
 *  The associated Writers {@link #out1} and {@link #out2} would normally not
 *  be used or controlled directly. Whenever possible use 
 *  {@link #setOut1 setOut1()},  {@link #setOut2 setOut2()}, 
 *  {@link #flush1 flush1()},  {@link #flush2 flush2()}, 
 *  {@link #close1 close1()} and  {@link #close2 close2()} instead.<br />
 */
   public Writer getOut1(){ return out1; }

/** The first Writer's last caught exception. <br />
 *  <br />
 *  This will be set by every caught exception of {@link #getOut1() out1}. 
 *  It may be reset by the application. It is reset if {@link #getOut1() out1}
 *  is {@link #switchOut1(Writer) switched}.<br />
 */
   public volatile IOException exc1;

/** Read pointer 1. <br /> */
   int buffOut1 = -1111;

/** Write pointer 1. <br /> */
   int buffIn1 = -1111;

/** The first Writer is blocked. <br /> */
   volatile boolean blocked1 = true;

/** The first Writer ignores explicit flush(). <br />
 *  <br />
 *  If {@code noExplFlush1} is true, explicit calls of flush() 
 *  ({@link #flush1()}) will not be executed any more for {@link #out1}.
 *  Hence the output behind will receive any calls quite seldom using 
 *  TeeWriter's and {@link #out1}'s buffering (if given) much more 
 *  efficiently.<br />
 *  <br />
 *  The (internal) decoupling thread so efficiently becomes a background
 *  thread outputting large amounts seldom. This setting is only suitable 
 *  for a non visible branch of the TeeWriter (a log file e.g) due to the 
 *  latency. It restricts the effect of flushing the TeeWriter to it's
 *  other branch {@link #out2}.<br />
 *  <br />
 *  Hint: Remember that the decoupled {@link #out1} is normally taken for the
 *  visible branch blockable by humans (like standard out).<br />
 */
   public volatile boolean noExplFlush1;

/** Blocking the first Writer. <br />
 *  <br />
 *  One branch {@link #out1} of the TeeWriter shall be blocked by will.
 *  If this blocking is transitional (what it normally should be) it is 
 *  dismissed by {@link #unBlock1()}.<br />
 *  <br />
 *  A block like this enables the undisturbed (transitional) use of the
 *  first Writer {@link #out1} for other purposes (like e.g. an input dialogue
 *  via console).<br />
 */
   public void block1(){
      if (closed1 || out1 == null || blocked1) return;
      synchronized(lock) { 
         blocked1 = true; // lock
      }
   } // block1

/** Unblocking the first Writer. <br />
 *  <br />
 *  If blocked ({@link #block1()}), the first Writer {@link #out1} will be
 *  unblocked and flushed ({@link #flush1()}). If not blocked this method 
 *  has no effect.<br />
 */
   public void unBlock1(){
      if (closed1 || out1 == null || !blocked1) return;
      synchronized(lock) {
         if (closed1 || out1 == null || !blocked1) return;
         blocked1 = false; // unlock
         forceFlush1();
      }
   } // unBlock1


/** First Writer is closed / not connected. <br /> */
   volatile boolean closed1 = true;

/** Closing the first Writer. <br /> */
   public void close1(){
      if (closed1 || out1 == null) return;
      synchronized(lock) {
         closed1 = true; // Write on and lock after current
      }

      if (out1 != null) synchronized(flush1Thread) {
         if (buffIn1 != buffOut1) { // first flush
            forceFlush1();
            try { 
               flush1Thread.wait(maxBlockTime);
            } catch (InterruptedException iex) {}
            if (buffIn1 != buffOut1) { // second flush
               forceFlush1();
               try { 
                  flush1Thread.wait(maxBlockTime);
               } catch (InterruptedException iex) {}
            } // second flush
         } // first flush 
         try {
            out1.close();
         } catch (IOException e) {
            exc1 = e;
         }
         out1 = null;
         lostChars1 = 0;
         buffOut1 = -1111; // out of the way
         buffIn1  = -1111; // empty
         blocked1 = true;
      } // sync out1
   } // close1

   
//---    flushing thread   --------------

/** First Writer's flush thread. <br /> */
   protected Thread flush1Thread;

/** Maximum block or jamming time by the first Writer / ms . <br />
 *  <br />
 *  @see #getMaxBlockTime()
 */
   protected int maxBlockTime;

/** Maximum blocking / jamming time by the first Writer in ms  . <br />
 *  <br />
 *  If the first Writer {@link #out1} blocks and as consequence no further 
 *  buffering is possible without overriding output not yet forwarded to
 *  {@link #out1}, the whole TeeWriter is blocked at first. This blocking
 *  is (forcefully) removed after {@code maxBlockTime} milliseconds, even if 
 *  that would mean loss of (part of) output to {@link #out1}.<br />
 *  <br >
 *  The chance for such losses exists if the output to the TeeWriter (and
 *  {@link #getOut2() out2}'s speed also) is faster than {@link #out1} or 
 *  if {@link #out1} is simply jammed.<br />
 *  If such losses occur (which will be recorded in {@link #lostChars1}) 
 *  they can to a certain extend be reduced by larger buffers or longer 
 *  blocking times. But they are a symptom for {@link #out1} being slower
 *  than the application requires (and  {@link #getOut2() out2} allows).<br />
 *  <br />
 *  @see #setMaxBlockTime(int)
 */
   public final int getMaxBlockTime(){ return maxBlockTime; }


/** Set maximum Block or jamming time by the first Writer / ms  . <br />
 *  <br />
 *  @see #getMaxBlockTime
 *  @param maxBlockTime new maximal blocking time in ms; limited to 4...9800
 */
   public final void setMaxBlockTime(int maxBlockTime){
      if (maxBlockTime < 4) maxBlockTime = 4;
      else if (maxBlockTime > 9800) maxBlockTime = 9800;
      this.maxBlockTime = maxBlockTime;
   }

/** Number of characters lost by (blocked) first Writer. <br />
 *  <br /> 
 *  If the first Writer is blocked too long (or just jammed due to dead
 *  slowness), it will loose characters to its output (on first buffer 
 *  round robin after block at latest). The number of lost characters is
 *  summed up.<br />
 *  On closing ({@link #close1}) or switching
 *  ({@link #setOut1(OutputStream) setOut1()}) the first Writer the sum is
 *  reset to zero as well as by this method if {@code doClear} is true.<br />
 */
   public int getLostChars1(final boolean doClear){
      synchronized (lock) {
         final int oldLost = lostChars1;
         if (doClear) lostChars1 = 0;
         return oldLost;
      }
   } // getLostChars1(boolean)

/** Number of characters lost by (blocked) first Writer. <br />
 *  <br /> 
 * @see #getLostChars1(boolean) 
 */   
   protected int lostChars1; // guarded by lock


/** Flushing the first Writer.<br /> */
   public void flush1(){ 
      if (blocked1 || flush1Thread == null 
                                      || (noExplFlush1 && !closed)) return;
      flush1Thread.interrupt();
   } // flush1()

/** Flushing the first Writer (forcefully). <br />
 *  <br />
 *  In contrast to {@link #flush1()} this method's effect is not suspended
 *  by {@link #noExplFlush1}.<br />
 */
   public void forceFlush1() { 
      if (blocked1 || flush1Thread == null) return;
      flush1Thread.interrupt();
   } // forceFlush1()

/**  Flushing the first Writer (implementation). <br /> */
   void flush1Impl(){ 
      if (out1 == null || blocked1) return;
      int soFar = buffIn1;
      int len = soFar - buffOut1;
      if (len == 0) return;
      if (len < 0) len += buffLen;
      int lenA = len;
      int lenB = 0;
      if (soFar < buffOut1) { // two steps
          lenB = soFar;
          lenA -= lenB; 
      }
      try {
         if (lenA > 0)
            out1.write(buff, buffOut1, lenA); 
         if (lenB > 0)
            out1.write(buff, 0, lenB); 
         out1.flush();
      } catch (IOException e) {
         exc1 = e;
      }
      synchronized(flush1Thread){
         buffOut1 = soFar;
         flush1Thread.notify();
      }
      return;
   } // flush1Impl()

//------------------------------------------------------------------------


/** Setting / connecting the first Writer. <br />
 *  <br />
 *  If the Parameter {@code wr1} equals an already set Writer (be it 1 or 2)
 *  nothing happens.<br />
 *  <br />
 *  An existing Writer 1 will be flushed by {@link #flush1()} (if not blocked)
 *  and closed by {@link #close1()}. If the parameter {@code wr1} is null
 *  that was all.<br />
 *  <br />
 *  Otherwise  {@code wr1} will be set as new Writer 1. It starts not
 *  blocked ({@link #unBlock1()}), even if its predecessor switched from 
 *  was. The setting {@link #noExplFlush1} will be kept unchanged.<br />
 *  <br />
 *  @return this TeeWriter (this)
 */
   public TeeWriter setOut1(Writer wr1){
      if (out1 == wr1 || out2 == wr1) return this;
      if (flush1Thread != null)   // fast 24.08.2002
         close1();               // mandatory since 23.08.2002
      if (wr1 == null) return this;

      synchronized (lock) {
         exc1 = null; 
         buffOut1 = buffIn1 = buffIn;
         if (flush1Thread == null) {
            flush1Thread = new Thread(){
               @Override public void run() {
                while (!closed || !closed1 || buffOut1 != buffIn1) {
                  try {
                     sleep(noExplFlush1 ? 120970 : 6797);
                  } catch (InterruptedException iex){}
                  flush1Impl();
                  if (out1 != null) synchronized(this) {
                     notify();
                  }
                } // while endless
               } // run
            }; // new
            flush1Thread.setDaemon(true); 
            flush1Thread.start();
         }
         out1 = wr1;
         blocked1 = closed1 = false;
         lostChars1 = 0;
      } // sync. TeeWriter
      return this;
   } // setOut1(Writer)
 

/** Switching the first Writer. <br />
 *  <br />
 *  If the Parameter {@code wr1} equals an already set Writer (be it 1 or 2)
 *  nothing happens. If there is no set Writer 1 yet this method acts like 
 *  {@link #setOut1 setOut1}.<br />
 *  <br />
 *  Otherwise &mdash; contrary to  {@link #setOut1 setOut1} &mdash; the former
 *  first Writer is just disconnected, i.e neither flushed nor closed. It is
 *  replaced on the spot by the Writer {@code wr1} supplied as parameter. All 
 *  settings remain.<br />
 *  <br />
 *  @return this TeeWriter (this)
 */
   public TeeWriter switchOut1(Writer wr1){
      if (out1 == wr1 || out2 == wr1 || wr1 == null) return this;
      if (out1 == null || closed1 || flush1Thread == null)
         return setOut1(wr1);
      synchronized (flush1Thread){
         out1 = wr1;
         exc1 = null;
         lostChars1 = 0;
      } // sync. TeeWriter
      return this;
   } // switchOut1(Writer)

  
/** Setting / connecting the first Writer as OutputStream. <br />
 *  <br />
 *  The OutputStream {@code os1} will be decorated by a Writer (default 
 *  encoding) and connected by  
 *  {@link #setOut1(java.io.Writer) setOut1(Writer)}
 *  as first Writer.<br />
 *  <br />
 *  @param os1 the OutputStream to use as TeeWriter's first branch
 *  @return this TeeWriter (this)
 */
   public TeeWriter setOut1(final OutputStream os1){
      if (os1 == null)
          return setOut1((OutputStreamWriter)null);
      return setOut1(new OutputStreamWriter(os1));
   } // setOut1(OutputStream)
   
/** Setting / connecting the first Writer. <br />
 *  <br />
 *  The OutputStream {@code os1} will be decorated by a Writer with the 
 *  character encoding supplied by {@code cp1} and connected by  
 *  {@link #setOut1(java.io.Writer) setOut1(Writer)}
 *  as first Writer.<br />
 *  <br />
 *  @param os1 the OutputStream to use as TeeWriter's first branch
 *  @param cp1 the encoding. null will be taken Cp850 (Western Windows).
 *             A non implemented encoding will be replaced by the default
 *             encoding
 *  @return this TeeWriter (this)
 */
   public TeeWriter setOut1(final OutputStream os1, final CharSequence cp1){
      String cpS1 = TextHelper.trimUq(cp1, "Cp850");
      synchronized (lock) {
         if (os1 == null) {
            setOut1((OutputStreamWriter)null);
            return this;
         }
         OutputStreamWriter w1 = null;
         try {w1 = new OutputStreamWriter(os1, cpS1); }
         catch (UnsupportedEncodingException e) {
            w1  = new OutputStreamWriter(os1);
         } 
         return setOut1(w1);
      }
   } // setOut1(OutputStream, CharSequence)

//---    Second Writer  /  TeeWriter output   ------------------------

/** The second Writer.  <br />
 *  <br />
 *  This is the second branch for the output.<br />
 */ // all writes guarded by lock; volatile for sake of unguarded reads
   protected volatile Writer out2;

/** The second Writer.  <br />
 *  <br />
 *  The associated Writers {@link #out1} and {@link #out2} would normally not
 *  be used or controlled directly. Whenever possible use 
 *  {@link #setOut1 setOut1()},  {@link #setOut2 setOut2()}, 
 *  {@link #flush1 flush1()},  {@link #flush2 flush2()}, 
 *  {@link #close1 close1()} and  {@link #close2 close2()} instead.<br />
 */
   public Writer getOut2(){ return out2; }

//-------------------------------------------------------------------

/** The second Writer's last caught exception. <br />
 *  <br />
 *  This will be set by every caught exception of {@link #getOut2() out2}. 
 *  It may be reset by the application. It is reset if {@link #getOut2() out2}
 *  is {@link #setOut2(Writer) switched}.<br />
 */
   public volatile IOException exc2;

/** Read index  2. */
   int buffOut2 = -2222; // guarded by lock

//-------------------------------------------------------------------


/** The second Writer ignores explicit flush(). <br />
 *  <br />
 *  If {@code noExplFlush2} is true, explicit calls of flush() 
 *  ({@link #flush2()}) will not be executed any more for {@link #out2}.
 *  The effect will be {@link #out2}'s not receiving further calls.<br />
 *  <br />
 *  In contrast to {@link #noExplFlush1} this {@code noExplFlush2} will be
 *  (automatic) reset by the following events:<ul>
 *  <li>{@link #close2()} {@link #close()}</li>
 *  <li>{@link #setOut2(OutputStream) setOut2()}</li>
 *  <li>reaching the autoflush threshold {@link #autoFlushThr} 
 *      i(in the buffer)</li></ul>
 *  As {@link #out2} has no decoupling thread this auto reset avoids a 
 *  jamming of the TeeWriter due to {@code noExplFlush2}.<br />
 */
   public volatile boolean noExplFlush2;


/** Flushing the second Writer. <br /> */
   public void flush2(){
      if (out2 == null || noExplFlush2) return;
      synchronized (lock) {
         if (out2 == null || noExplFlush2) return;
         int soFar = buffIn;
         int len = soFar - buffOut2;
         if (len == 0) return;
         if (len < 0) len += buffLen;
         int lenA = len;
         int lenB = 0;
         if (soFar < buffOut2) { // two steps
            lenB = soFar;
            lenA -= lenB; 
         }
         try {
            if (lenA > 0)
               out2.write(buff, buffOut2, lenA); 
            if (lenB > 0)
               out2.write(buff, 0, lenB); 
            out2.flush();
         } catch (IOException e) {
            exc2 = e;
         }
         buffOut2 = soFar;
      } // sync lock
   } // flush2


/** Closing the second Writer. <br /> */
   public void close2(){
      if (out2 == null) return;
      synchronized(lock){
         if (out2 == null) return;
         noExplFlush2 = false;
         flush2();
         try {
            out2.close();
         } catch (IOException e) {
            exc2 = e;
         }
         out2 = null;
         buffOut2 = -2222; // out of the way
     } // sync
   } // close2()


/** Setting the second Writer. <br />
 *  <br />
 *  If the Parameter {@code wr2} equals an already set Writer (be it 1 or 2)
 *  nothing happens.<br />
 *  <br />
 *  An existing Writer 2 will be closed by {@link #close2()}. If the parameter
 *  {@code wr2} is null that was all.<br />
 *  <br />
 *  Otherwise  {@code wr2} will be set as new Writer 2.<br />
 *  <br />
 *  @return this TeeWriter (this)
 */
   public TeeWriter setOut2 (final Writer wr2){
      if (wr2 == null && out2 != null) {
         close2();
         return this;
      }
      synchronized(lock){
         if (out2 == wr2 || out1 == wr2) return this;
         close2();
         exc2 = null; 
         out2 = wr2;
         buffOut2 = buffIn;
      } // sync
      return this;
   } // setOut2
   
/** Setting the second Writer as OutputStream. <br />
 *  <br />
 *  The OutputStream {@code os2} will be decorated by a Writer (default 
 *  encoding) and connected by  
 *  {@link #setOut2(java.io.Writer) setOut2(decoOs2)} as second Writer.<br />
 *  <br />
 *  @param os2 the OutputStream to use as TeeWriter's second branch
 *  @return this TeeWriter (this)
 */
   public TeeWriter setOut2(final OutputStream os2){
      if (os2 == null) {
          setOut2((OutputStreamWriter)null);
          return this;
      }
      return setOut2 (new OutputStreamWriter(os2));
   } // setOut2(OutputStream)
   
/** Setting the second Writer. <br />
 *  <br />
 *  The OutputStream {@code os2} will be decorated by a Writer with the 
 *  character encoding supplied by {@code cp2} and connected by  
 *  {@link #setOut2(java.io.Writer) setOut2(Writer)}
 *  as second Writer.<br />
 *  <br />
 *  @param os2 the OutputStream to use as TeeWriter's second branch
 *  @param cp2 the encoding. null will be taken Cp850 (Western Windows).
 *             A not implemented encoding will be replaced by the default
 *             encoding
 *  @return this TeeWriter (this)
 */
   public TeeWriter setOut2(final OutputStream os2, final CharSequence cp2){
      String cpS2 = TextHelper.trimUq(cp2, "Cp850");
      synchronized (lock) {
         if (os2 == null) {
            setOut2((OutputStreamWriter)null);
            return this;
         }
         OutputStreamWriter w2 = null;
         try { w2 = new OutputStreamWriter(os2, cpS2); }
         catch (UnsupportedEncodingException e) {
            w2  = new OutputStreamWriter(os2);
         } 
         return setOut2(w2);
      }
   } // setOut2(OutputStream, CharSequence)

   
//---------- Operations on the TeeWriter as whole ----------------------------

/** Flushing the TeeWriter. <br />
 *  <br />
 *  Exceptions by (hopefully only) one of the branches are caught to
 *  keep the other ant the TeeWriter operable.<br />
 */
   @Override public void flush(){
         flush1();
         flush2();
   } // flush()

/** Output a char[] to the TeeWriter. <br />
 *  <br />
 *  This method is used by all other (inherited) Writer's output methods
 *  except {@link #write(char)} and {@link #write(int)}.<br />
 *  <br />
 *  Exceptions by (hopefully only) one of the branches are caught to
 *  keep the TeeWriter's other arm operable.<br />
 */
   @Override public void write(final char[] cbuf, int off, int len){
      if (closed || cbuf == null || len <= 0 || off < 0) return;
      int cbL = cbuf.length;
      if (cbL == 0 || off >= cbL) return;
      if (off + len > cbL)
         len = cbL - off;
      int count   = 0;
      synchronized (lock) { 

       while (count < len) { // while for output > Puffer
         int currLen = len - count;
         if (currLen > buffLen)
            currLen = buffLen - 1;
         if (!closed1 && out1 != null) { // first look out1 
            int lenW1 = buffIn1 - buffOut1;
            if (lenW1 < 0) lenW1 += buffLen;
            if (currLen + lenW1 >= buffLen) // full 1
                forceFlush1(); // non blocking
         } // first look out1 
         if (out2 != null) { // first look out2 
            int lenW2 = buffIn - buffOut2;
            if (lenW2 < 0) lenW2 += buffLen;
            if (lenW2 > autoFlushThr) noExplFlush2 = false;
            if (currLen + lenW2 >= buffLen) // full 2
                flush2(); // non blocking
         } // first look out2 
         if (!closed1 && out1 != null) 
                 synchronized(flush1Thread) { // second look out1 
            int lenW1 = buffIn1 - buffOut1;
            if (lenW1 < 0) lenW1 += buffLen;
            if (currLen + lenW1 >= buffLen) // full 1
               try { 
                  flush1Thread.wait(maxBlockTime);
               } catch (InterruptedException iex) {}
            if (!closed1 && out1 != null) { // third look out1 
               lenW1 = buffIn1 - buffOut1;
               if (lenW1 < 0) lenW1 += buffLen;
               if (currLen + lenW1 >= buffLen) { // full 1
                  int clear = currLen < 1024 ? 1024 : currLen;
                  if (clear > lenW1) clear = lenW1;
                  buffOut1 += clear; // forget clear chars for out1
                  lostChars1 += clear;
                  if (buffOut1 >= buffLen)  buffOut1 -= buffLen;
               } // full1
            } // third look out1 
         } // second look out1; sync.

         int locCount = 0;
         int locLen = buffIn + currLen < buffLen 
                      ? currLen : buffLen - buffIn;
 
         for (; locCount < currLen; locLen =  currLen - locCount) { // steps
            System.arraycopy(cbuf, off + count, buff, buffIn, locLen);
            buffIn += locLen;
            if (buffIn >= buffLen) {
               buffIn = 0;
               filled = true;
            }
            count += locLen;
            if (locLen == currLen) break; // one step optimised
            locCount += locLen;
         } // for one or two steps
         if (!closed1) synchronized(flush1Thread) {
             buffIn1 = buffIn;
         }
       } // while for output > Puffer

       // Auto flush
       int oLen;
       if (out1 != null) {
          oLen = buffIn - buffOut1;
          if (oLen < 0) oLen += buffLen;
          if (oLen > autoFlushThr) forceFlush1();
       }
       if (out2 != null) {
          oLen = buffIn - buffOut2;
          if (oLen < 0) oLen += buffLen;
          if (oLen > autoFlushThr) {
             noExplFlush2 = false;
             flush2();
          }
       }
      } // sync
  } // write(char[],int,int)


/** Output a character (char provided as int) to the TeeWriter. <br />
 *  <br />
 *  This method outputs the lower 16 Bit of {@code ic} as character (char) to
 *  this TeeWriter. The effect is like <code><br /> &nbsp;
 *  &nbsp; {@link #write(char) write}((char) ic)</code><br /> 
 *  Exceptions are caught.<br />
 */
   @Override public void write(int ic){ write((char) ic); }


/** Output a character (char) to the TeeWriter. <br />
 *  <br />
 *  This method outputs just one character extensively using the using the
 *  TeeWriters buffer. This method only flushes if one of the branches runs
 *  down to zero buffer space. The rationale behind is acceptable performance
 *  of not clever but sometimes easier looping over single character 
 *  outputs.<br />
 *  <br />
 *  Exceptions are caught silently.<br />
 */
   public void write(final char c){
      if (closed) return;
      synchronized (lock) { 
         int next = buffIn + 1;
         if (next == buffLen) {
            next = 0;
            filled = true;
         } 
         if (next == buffOut1) { // 1 full
         // This char[] "solution" keeps the complex case in the other method
            write (new char[]{c}, 0, 1);  // happens only every buffLen times
            return;
         } // 1 full
         if (next == buffOut2) { // 2 is full 
            noExplFlush2 = false;
            flush2();
         }
         buff[buffIn] = c;  // That's all: writing (TeeWriter's buffer)
         buffIn = next;
         if (!closed1)  // should work w/o synchronized(flush1Thread) ??
             buffIn1 = buffIn;
      } // sync
   } // write(char)

} // class TeeWriter (24.08.2002, 13.02.2009)
