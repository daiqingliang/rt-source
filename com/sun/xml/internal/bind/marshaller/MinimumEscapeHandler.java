package com.sun.xml.internal.bind.marshaller;

import java.io.IOException;
import java.io.Writer;

public class MinimumEscapeHandler implements CharacterEscapeHandler {
  public static final CharacterEscapeHandler theInstance = new MinimumEscapeHandler();
  
  public void escape(char[] paramArrayOfChar, int paramInt1, int paramInt2, boolean paramBoolean, Writer paramWriter) throws IOException {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      char c = paramArrayOfChar[j];
      if (c == '&' || c == '<' || c == '>' || c == '\r' || (c == '"' && paramBoolean)) {
        if (j != paramInt1)
          paramWriter.write(paramArrayOfChar, paramInt1, j - paramInt1); 
        paramInt1 = j + 1;
        switch (paramArrayOfChar[j]) {
          case '&':
            paramWriter.write("&amp;");
            break;
          case '<':
            paramWriter.write("&lt;");
            break;
          case '>':
            paramWriter.write("&gt;");
            break;
          case '"':
            paramWriter.write("&quot;");
            break;
        } 
      } 
    } 
    if (paramInt1 != i)
      paramWriter.write(paramArrayOfChar, paramInt1, i - paramInt1); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\marshaller\MinimumEscapeHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */