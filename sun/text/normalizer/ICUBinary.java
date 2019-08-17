package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class ICUBinary {
  private static final byte MAGIC1 = -38;
  
  private static final byte MAGIC2 = 39;
  
  private static final byte BIG_ENDIAN_ = 1;
  
  private static final byte CHAR_SET_ = 0;
  
  private static final byte CHAR_SIZE_ = 2;
  
  private static final String MAGIC_NUMBER_AUTHENTICATION_FAILED_ = "ICU data file error: Not an ICU data file";
  
  private static final String HEADER_AUTHENTICATION_FAILED_ = "ICU data file error: Header authentication failed, please check if you have a valid ICU data file";
  
  public static final byte[] readHeader(InputStream paramInputStream, byte[] paramArrayOfByte, Authenticate paramAuthenticate) throws IOException {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    char c = dataInputStream.readChar();
    char c1 = '\002';
    byte b1 = dataInputStream.readByte();
    c1++;
    byte b2 = dataInputStream.readByte();
    c1++;
    if (b1 != -38 || b2 != 39)
      throw new IOException("ICU data file error: Not an ICU data file"); 
    dataInputStream.readChar();
    c1 += 2;
    dataInputStream.readChar();
    c1 += 2;
    byte b3 = dataInputStream.readByte();
    c1++;
    byte b4 = dataInputStream.readByte();
    c1++;
    byte b5 = dataInputStream.readByte();
    c1++;
    dataInputStream.readByte();
    c1++;
    byte[] arrayOfByte1 = new byte[4];
    dataInputStream.readFully(arrayOfByte1);
    c1 += 4;
    byte[] arrayOfByte2 = new byte[4];
    dataInputStream.readFully(arrayOfByte2);
    c1 += 4;
    byte[] arrayOfByte3 = new byte[4];
    dataInputStream.readFully(arrayOfByte3);
    c1 += 4;
    if (c < c1)
      throw new IOException("Internal Error: Header size error"); 
    dataInputStream.skipBytes(c - c1);
    if (b3 != 1 || b4 != 0 || b5 != 2 || !Arrays.equals(paramArrayOfByte, arrayOfByte1) || (paramAuthenticate != null && !paramAuthenticate.isDataVersionAcceptable(arrayOfByte2)))
      throw new IOException("ICU data file error: Header authentication failed, please check if you have a valid ICU data file"); 
    return arrayOfByte3;
  }
  
  public static interface Authenticate {
    boolean isDataVersionAcceptable(byte[] param1ArrayOfByte);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\ICUBinary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */