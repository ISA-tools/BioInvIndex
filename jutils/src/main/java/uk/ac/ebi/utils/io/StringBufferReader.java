package uk.ac.ebi.utils.io;
/*
 * __________
 * CREDITS
 * __________
 *
 * Team page: http://isatab.sf.net/
 * - Marco Brandizi (software engineer: ISAvalidator, ISAconverter, BII data management utility, BII model)
 * - Eamonn Maguire (software engineer: ISAcreator, ISAcreator configurator, ISAvalidator, ISAconverter,  BII data management utility, BII web)
 * - Nataliya Sklyar (software engineer: BII web application, BII model,  BII data management utility)
 * - Philippe Rocca-Serra (technical coordinator: user requirements and standards compliance for ISA software, ISA-tab format specification, BII model, ISAcreator wizard, ontology)
 * - Susanna-Assunta Sansone (coordinator: ISA infrastructure design, standards compliance, ISA-tab format specification, BII model, funds raising)
 *
 * Contributors:
 * - Manon Delahaye (ISA team trainee:  BII web services)
 * - Richard Evans (ISA team trainee: rISAtab)
 *
 *
 * ______________________
 * Contacts and Feedback:
 * ______________________
 *
 * Project overview: http://isatab.sourceforge.net/
 *
 * To follow general discussion: isatab-devel@list.sourceforge.net
 * To contact the developers: isatools@googlegroups.com
 *
 * To report bugs: http://sourceforge.net/tracker/?group_id=215183&atid=1032649
 * To request enhancements:  http://sourceforge.net/tracker/?group_id=215183&atid=1032652
 *
 *
 * __________
 * License:
 * __________
 *
 * This work is licenced under the Creative Commons Attribution-Share Alike 2.0 UK: England & Wales License. To view a copy of this licence, visit http://creativecommons.org/licenses/by-sa/2.0/uk/ or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California 94105, USA.
 *
 * __________
 * Sponsors
 * __________
 * This work has been funded mainly by the EU Carcinogenomics (http://www.carcinogenomics.eu) [PL 037712] and in part by the
 * EU NuGO [NoE 503630](http://www.nugo.org/everyone) projects and in part by EMBL-EBI.
 */

import java.io.IOException;
import java.io.Reader;

/**
 * This comes from the Lucene project (org.apache.lucene.benchmark.byTask.utils) and was copied-pasted here.
 * 
 * Implements a {@link Reader} over a {@link StringBuffer} instance. Although
 * one can use {@link java.io.StringReader} by passing it
 * {@link StringBuffer#toString()}, it is better to use this class, as it
 * doesn't mark the passed-in {@link StringBuffer} as shared (which will cause
 * inner char[] allocations at the next append() attempt).<br>
 * Notes:
 * <ul>
 * <li>This implementation assumes the underlying {@link StringBuffer} is not
 * changed during the use of this {@link Reader} implementation.
 * <li>This implementation is thread-safe.
 * <li>The implementation looks very much like {@link java.io.StringReader} (for
 * the right reasons).
 * <li>If one wants to reuse that instance, then the following needs to be done:
 * <pre>
 * StringBuffer sb = new StringBuffer("some text");
 * Reader reader = new StringBufferReader(sb);
 * ... read from reader - don't close it ! ...
 * sb.setLength(0);
 * sb.append("some new text");
 * reader.reset();
 * ... read the new string from the reader ...
 * </pre>
 * </ul>
 */
public class StringBufferReader extends Reader {
  
  // TODO (3.0): change to StringBuffer (including the name of the class)
  
  // The StringBuffer to read from.
  private StringBuffer sb;

  // The length of 'sb'.
  private int length;

  // The next position to read from the StringBuffer.
  private int next = 0;

  // The mark position. The default value 0 means the start of the text.
  private int mark = 0;

  public StringBufferReader(StringBuffer sb) {
    set(sb);
  }

  /** Check to make sure that the stream has not been closed. */
  private void ensureOpen() throws IOException {
    if (sb == null) {
      throw new IOException("Stream has already been closed");
    }
  }

  @Override
  public void close() {
    synchronized (lock) {
      sb = null;
    }
  }

  /**
   * Mark the present position in the stream. Subsequent calls to reset() will
   * reposition the stream to this point.
   * 
   * @param readAheadLimit Limit on the number of characters that may be read
   *        while still preserving the mark. Because the stream's input comes
   *        from a StringBuffer, there is no actual limit, so this argument 
   *        must not be negative, but is otherwise ignored.
   * @exception IllegalArgumentException If readAheadLimit is < 0
   * @exception IOException If an I/O error occurs
   */
  @Override
  public void mark(int readAheadLimit) throws IOException {
    if (readAheadLimit < 0){
      throw new IllegalArgumentException("Read-ahead limit cannpt be negative: " + readAheadLimit);
    }
    synchronized (lock) {
      ensureOpen();
      mark = next;
    }
  }

  @Override
  public boolean markSupported() {
    return true;
  }

  @Override
  public int read() throws IOException {
    synchronized (lock) {
      ensureOpen();
      return next >= length ? -1 : sb.charAt(next++);
    }
  }

  @Override
  public int read(char cbuf[], int off, int len) throws IOException {
    synchronized (lock) {
      ensureOpen();

      // Validate parameters
      if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length) {
        throw new IndexOutOfBoundsException("off=" + off + " len=" + len + " cbuf.length=" + cbuf.length);
      }

      if (len == 0) {
        return 0;
      }

      if (next >= length) {
        return -1;
      }

      int n = Math.min(length - next, len);
      sb.getChars(next, next + n, cbuf, off);
      next += n;
      return n;
    }
  }

  @Override
  public boolean ready() throws IOException {
    synchronized (lock) {
      ensureOpen();
      return true;
    }
  }

  @Override
  public void reset() throws IOException {
    synchronized (lock) {
      ensureOpen();
      next = mark;
      length = sb.length();
    }
  }

  public void set(StringBuffer sb) {
    synchronized (lock) {
      this.sb = sb;
      length = sb.length();
    }
  }
  @Override
  public long skip(long ns) throws IOException {
    synchronized (lock) {
      ensureOpen();
      if (next >= length) {
        return 0;
      }

      // Bound skip by beginning and end of the source
      long n = Math.min(length - next, ns);
      n = Math.max(-next, n);
      next += n;
      return n;
    }
  }

}