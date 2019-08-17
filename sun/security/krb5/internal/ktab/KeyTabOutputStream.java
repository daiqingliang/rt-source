package sun.security.krb5.internal.ktab;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import sun.security.krb5.internal.util.KrbDataOutputStream;

public class KeyTabOutputStream extends KrbDataOutputStream implements KeyTabConstants {
  private KeyTabEntry entry;
  
  private int keyType;
  
  private byte[] keyValue;
  
  public int version;
  
  public KeyTabOutputStream(OutputStream paramOutputStream) { super(paramOutputStream); }
  
  public void writeVersion(int paramInt) throws IOException {
    this.version = paramInt;
    write16(paramInt);
  }
  
  public void writeEntry(KeyTabEntry paramKeyTabEntry) throws IOException {
    write32(paramKeyTabEntry.entryLength());
    String[] arrayOfString = paramKeyTabEntry.service.getNameStrings();
    int i = arrayOfString.length;
    if (this.version == 1281) {
      write16(i + 1);
    } else {
      write16(i);
    } 
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = paramKeyTabEntry.service.getRealmString().getBytes("8859_1");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    write16(arrayOfByte.length);
    write(arrayOfByte);
    for (byte b = 0; b < i; b++) {
      try {
        write16(arrayOfString[b].getBytes("8859_1").length);
        write(arrayOfString[b].getBytes("8859_1"));
      } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    } 
    write32(paramKeyTabEntry.service.getNameType());
    write32((int)(paramKeyTabEntry.timestamp.getTime() / 1000L));
    write8(paramKeyTabEntry.keyVersion % 256);
    write16(paramKeyTabEntry.keyType);
    write16(paramKeyTabEntry.keyblock.length);
    write(paramKeyTabEntry.keyblock);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\ktab\KeyTabOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */