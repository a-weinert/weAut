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
import java.io.InputStreamReader;
import java.io.Reader;

import de.frame4j.util.MinDoc;

/** <b>A (filter) reader that can switch its input reader</b>. <br />
 *  <br />
 *  This Reader just forwards all calls to its associated Reader. In this
 *  sense it is just a no-op filter or decorator.<br />
 *  Its special function is its ability to switch the decorated input 
 *  Reader.<br />
 *  One exemplary use case is the changing of a input chain's character 
 *  encoding. As the {@link InputStreamReader} can't change the encoding it
 *  was constructed with, that part of the chain has to be replaced in the
 *  process. With {@link SwitchedReader} in front of the
 *  {@link InputStreamReader} no one else is troubled.<br />
 *  <br />
 *  Hint: In a certain sense this is a complement to {@link TeeWriter} that
 *  provides switching in an output chain.<br />
 *  <br />
 *  <a href="package-summary.html#co">&copy;</a>
 *  Copyright 2009 &nbsp; Albrecht Weinert. <br />
 *  <br />
 *  @see AppIO
 *  @see Input
 */
 // so far    V.104+ (22.05.2009 08:46) :  new (to improve / robust AppIO)
 //           V.o75+ (13.02.2009 17:38) :  ported to Frame4J

@MinDoc(
   copyright = "Copyright  2001 - 2002, 2009  A. Weinert",
   author    = "Albrecht Weinert",
   version   = "V.$Revision: 33 $",
   lastModified   = "$Date: 2021-03-27 19:01:12 +0100 (Sa, 27 Mrz 2021) $",
// lastModifiedBy = "$Author: albrecht $",
   usage   = "use for unbloody replacement of a Reader chain's element",  
   purpose = "a no-op Reader filter / decorator that can switch its input"
) public final class SwitchedReader extends Reader {

/** The Reader to get input from. <br /> */  
   protected volatile Reader in;

/** Switch the Reader to get input from. <br />
 *  <br />
 *  @param in  the new Reader to get input from (null or actual has no effect)
 *  @param closeOld  if true and if switched to in do close the old input 
 *  @throws IOException file or stream problems
 */  
   public final synchronized void switchInput(final Reader in, 
                                 final boolean closeOld) throws IOException {
      if (in == null || in == this.in) return;
      final Reader oldIn = this.in;
      this.in = in;
      if (closeOld) oldIn.close();
   } // switchInput(Reader)


/** Make a new SwitchedReader. <br />
 *  <br />
 *  @param in  a Reader object providing the underlying input (not null).
 *  @throws NullPointerException if <code>in</code> is <code>null</code>
 */
   public SwitchedReader(final Reader in){
      super(in);
      lock = this; // constantly use this as lock as in may vary here
      if (in == null) {
       throw new NullPointerException("SwitchedReader must have non null in");
      }
      this.in = in;
   } // SwitchedReader(Reader) 

/** Read a single character. <br /> */
   @Override public int read() throws IOException { return in.read(); }
   
/** Read some characters into an array. <br /> */
   @Override public int read(char[] cbuf, int off, int len)
                                                        throws IOException {
      return in.read(cbuf, off, len);
   } // read(char[],int,int)

/** Skip some characters. <br /> */
   @Override public long skip(long n) throws IOException {
      return in.skip(n);
   } // skip(long)

/** Tries to tell whether the associated input reader is ready. <br /> */
   @Override public boolean ready() throws IOException {
      return in.ready();
   } // ready()

/** Does the associated input reader support the mark() operation. <br />
 *  <br />
 *  Hint: marks and resets just get lost if the associated input reader is
 *  switched.<br />
 */
  @Override public boolean markSupported(){ return in.markSupported(); }


/** Mark the present position in the associated input reader. <br />
 *  <br />
 *  Hint: marks and resets just get lost if the associated input reader is
 *  switched.<br />
 */
  @Override public void mark(int readAheadLimit) throws IOException {
      in.mark(readAheadLimit);
   } // mark(int)

/** Reset the associated input reader to the last marked position. <br />
 *  <br />
 *  Hint: marks and resets just get lost if the associated input reader is
 *  switched.<br />
 */
  @Override public void reset() throws IOException { in.reset(); }

/** Close the associated input reader. <br />
 *  <br />
 *  Note: by {@link #switchInput(Reader, boolean) switching} to another non 
 *  null  input reader this {@link SwitchedReader} would be operable again.
 *  This might be an unwanted surprise.<br />
 *  <br />  
 */
   @Override public void close() throws IOException { in.close(); }

} // class SwitchedReader 22.05.2009
