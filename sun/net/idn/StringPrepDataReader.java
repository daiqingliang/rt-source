package sun.net.idn;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.text.normalizer.ICUBinary;

final class StringPrepDataReader implements ICUBinary.Authenticate {
  private DataInputStream dataInputStream;
  
  private byte[] unicodeVersion;
  
  private static final byte[] DATA_FORMAT_ID = { 83, 80, 82, 80 };
  
  private static final byte[] DATA_FORMAT_VERSION = { 3, 2, 5, 2 };
  
  public StringPrepDataReader(InputStream paramInputStream) throws IOException {
    this.unicodeVersion = ICUBinary.readHeader(paramInputStream, DATA_FORMAT_ID, this);
    this.dataInputStream = new DataInputStream(paramInputStream);
  }
  
  public void read(byte[] paramArrayOfByte, char[] paramArrayOfChar) throws IOException {
    this.dataInputStream.read(paramArrayOfByte);
    for (byte b = 0; b < paramArrayOfChar.length; b++)
      paramArrayOfChar[b] = this.dataInputStream.readChar(); 
  }
  
  public byte[] getDataFormatVersion() { return DATA_FORMAT_VERSION; }
  
  public boolean isDataVersionAcceptable(byte[] paramArrayOfByte) { return (paramArrayOfByte[0] == DATA_FORMAT_VERSION[0] && paramArrayOfByte[2] == DATA_FORMAT_VERSION[2] && paramArrayOfByte[3] == DATA_FORMAT_VERSION[3]); }
  
  public int[] readIndexes(int paramInt) throws IOException {
    int[] arrayOfInt = new int[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfInt[b] = this.dataInputStream.readInt(); 
    return arrayOfInt;
  }
  
  public byte[] getUnicodeVersion() { return this.unicodeVersion; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\idn\StringPrepDataReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */