package com.sun.xml.internal.ws.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NoCloseOutputStream extends FilterOutputStream {
  public NoCloseOutputStream(OutputStream paramOutputStream) { super(paramOutputStream); }
  
  public void close() throws IOException {}
  
  public void doClose() throws IOException { super.close(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\NoCloseOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */