package sun.misc;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class IOUtils {
  public static byte[] readFully(InputStream paramInputStream, int paramInt, boolean paramBoolean) throws IOException {
    byte[] arrayOfByte = new byte[0];
    if (paramInt == -1)
      paramInt = Integer.MAX_VALUE; 
    int i;
    for (i = 0; i < paramInt; i += k) {
      int j;
      if (i >= arrayOfByte.length) {
        j = Math.min(paramInt - i, arrayOfByte.length + 1024);
        if (arrayOfByte.length < i + j)
          arrayOfByte = Arrays.copyOf(arrayOfByte, i + j); 
      } else {
        j = arrayOfByte.length - i;
      } 
      int k = paramInputStream.read(arrayOfByte, i, j);
      if (k < 0) {
        if (paramBoolean && paramInt != Integer.MAX_VALUE)
          throw new EOFException("Detect premature EOF"); 
        if (arrayOfByte.length != i)
          arrayOfByte = Arrays.copyOf(arrayOfByte, i); 
        break;
      } 
    } 
    return arrayOfByte;
  }
  
  public static byte[] readNBytes(InputStream paramInputStream, int paramInt) throws IOException {
    if (paramInt < 0)
      throw new IOException("length cannot be negative: " + paramInt); 
    return readFully(paramInputStream, paramInt, true);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\IOUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */