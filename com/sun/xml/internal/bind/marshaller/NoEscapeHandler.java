package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class NoEscapeHandler implements CharacterEscapeHandler {
  public static final NoEscapeHandler theInstance = new NoEscapeHandler();
  
  public void escape(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, Writer paramWriter) throws IOException { paramWriter.write(paramArrayOfChar, paramInt1, paramInt2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\marshaller\NoEscapeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */