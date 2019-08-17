package sun.text.normalizer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class NormalizerDataReader implements ICUBinary.Authenticate {
  private DataInputStream dataInputStream;
  
  private byte[] unicodeVersion;
  
  private static final byte[] DATA_FORMAT_ID = { 78, 111, 114, 109 };
  
  private static final byte[] DATA_FORMAT_VERSION = { 2, 2, 5, 2 };
  
  protected NormalizerDataReader(InputStream paramInputStream) throws IOException {
    this.unicodeVersion = ICUBinary.readHeader(paramInputStream, DATA_FORMAT_ID, this);
    this.dataInputStream = new DataInputStream(paramInputStream);
  }
  
  protected int[] readIndexes(int paramInt) throws IOException {
    int[] arrayOfInt = new int[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = this.dataInputStream.readInt(); 
    return arrayOfInt;
  }
  
  protected void read(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, char[] paramArrayOfChar1, char[] paramArrayOfChar2) throws IOException {
    this.dataInputStream.readFully(paramArrayOfByte1);
    byte b;
    for (b = 0; b < paramArrayOfChar1.length; b++)
      paramArrayOfChar1[b] = this.dataInputStream.readChar(); 
    for (b = 0; b < paramArrayOfChar2.length; b++)
      paramArrayOfChar2[b] = this.dataInputStream.readChar(); 
    this.dataInputStream.readFully(paramArrayOfByte2);
    this.dataInputStream.readFully(paramArrayOfByte3);
  }
  
  public byte[] getDataFormatVersion() { return DATA_FORMAT_VERSION; }
  
  public boolean isDataVersionAcceptable(byte[] paramArrayOfByte) { return (paramArrayOfByte[0] == DATA_FORMAT_VERSION[0] && paramArrayOfByte[2] == DATA_FORMAT_VERSION[2] && paramArrayOfByte[3] == DATA_FORMAT_VERSION[3]); }
  
  public byte[] getUnicodeVersion() { return this.unicodeVersion; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\normalizer\NormalizerDataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */