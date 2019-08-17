package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import sun.misc.SharedSecrets;

public class Password {
  public static char[] readPassword(InputStream paramInputStream) throws IOException { return readPassword(paramInputStream, false); }
  
  public static char[] readPassword(InputStream paramInputStream, boolean paramBoolean) throws IOException {
    arrayOfChar = null;
    arrayOfByte = null;
    try {
      Console console = null;
      if (!paramBoolean && paramInputStream == System.in && (console = System.console()) != null) {
        arrayOfChar = console.readPassword();
        if (arrayOfChar != null && arrayOfChar.length == 0)
          return null; 
        arrayOfByte = convertToBytes(arrayOfChar);
        paramInputStream = new ByteArrayInputStream(arrayOfByte);
      } 
      char[] arrayOfChar1 = new char[128];
      char[] arrayOfChar2 = arrayOfChar1;
      int i = arrayOfChar2.length;
      int j = 0;
      boolean bool = false;
      while (!bool) {
        int m;
        int k;
        switch (k = paramInputStream.read()) {
          case -1:
          case 10:
            bool = true;
            continue;
          case 13:
            m = paramInputStream.read();
            if (m != 10 && m != -1) {
              if (!(paramInputStream instanceof PushbackInputStream))
                paramInputStream = new PushbackInputStream(paramInputStream); 
              ((PushbackInputStream)paramInputStream).unread(m);
              break;
            } 
            bool = true;
            continue;
        } 
        if (--i < 0) {
          arrayOfChar2 = new char[j + ''];
          i = arrayOfChar2.length - j - 1;
          System.arraycopy(arrayOfChar1, 0, arrayOfChar2, 0, j);
          Arrays.fill(arrayOfChar1, ' ');
          arrayOfChar1 = arrayOfChar2;
        } 
        arrayOfChar2[j++] = (char)k;
      } 
      if (j == 0)
        return null; 
      char[] arrayOfChar3 = new char[j];
      System.arraycopy(arrayOfChar2, 0, arrayOfChar3, 0, j);
      Arrays.fill(arrayOfChar2, ' ');
      return arrayOfChar3;
    } finally {
      if (arrayOfChar != null)
        Arrays.fill(arrayOfChar, ' '); 
      if (arrayOfByte != null)
        Arrays.fill(arrayOfByte, (byte)0); 
    } 
  }
  
  private static byte[] convertToBytes(char[] paramArrayOfChar) {
    if (enc == null)
      synchronized (Password.class) {
        enc = SharedSecrets.getJavaIOAccess().charset().newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      }  
    byte[] arrayOfByte = new byte[(int)(enc.maxBytesPerChar() * paramArrayOfChar.length)];
    ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte);
    synchronized (enc) {
      enc.reset().encode(CharBuffer.wrap(paramArrayOfChar), byteBuffer, true);
    } 
    if (byteBuffer.position() < arrayOfByte.length)
      arrayOfByte[byteBuffer.position()] = 10; 
    return arrayOfByte;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\Password.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */