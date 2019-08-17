package com.sun.xml.internal.txw2.output;

import java.io.IOException;
import java.io.Writer;

public interface CharacterEscapeHandler {
  void escape(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, Writer paramWriter) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\CharacterEscapeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */