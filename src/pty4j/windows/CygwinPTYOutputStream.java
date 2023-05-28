/*******************************************************************************
 * Copyright (c) 2000, 2011 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package pty4j.windows;

import java.io.IOException;
import java.io.OutputStream;

public class CygwinPTYOutputStream extends OutputStream {
  private final NamedPipe myNamedPipe;
  private boolean myClosed;

  public CygwinPTYOutputStream(NamedPipe namedPipe) {
    myNamedPipe = namedPipe;
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (myClosed) {
      return;
    }

    if (b == null) {
      throw new NullPointerException();
    }
    else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    }
    else if (len == 0) {
      return;
    }

    myNamedPipe.write(b, off, len);
  }

  @Override
  public void write(int b) throws IOException {
    byte[] buf = new byte[1];
    buf[0] = (byte)b;
    write(buf, 0, 1);
  }

  @Override
  public void close() throws IOException {
    myClosed = true;
    myNamedPipe.close();
  }

  @Override
  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }
}
