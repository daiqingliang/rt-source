package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.OutputStream;

public abstract class OutputUtil {
  private static byte[] newline = { 13, 10 };
  
  public static void writeln(String paramString, OutputStream paramOutputStream) throws IOException {
    writeAsAscii(paramString, paramOutputStream);
    writeln(paramOutputStream);
  }
  
  public static void writeAsAscii(String paramString, OutputStream paramOutputStream) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++)
      paramOutputStream.write((byte)paramString.charAt(b)); 
  }
  
  public static void writeln(OutputStream paramOutputStream) throws IOException { paramOutputStream.write(newline); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mim\\util\OutputUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */