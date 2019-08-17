package sun.security.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DerOutputStream extends ByteArrayOutputStream implements DerEncoder {
  private static ByteArrayLexOrder lexOrder = new ByteArrayLexOrder();
  
  private static ByteArrayTagOrder tagOrder = new ByteArrayTagOrder();
  
  public DerOutputStream(int paramInt) { super(paramInt); }
  
  public DerOutputStream() {}
  
  public void write(byte paramByte, byte[] paramArrayOfByte) throws IOException {
    write(paramByte);
    putLength(paramArrayOfByte.length);
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte paramByte, DerOutputStream paramDerOutputStream) throws IOException {
    write(paramByte);
    putLength(paramDerOutputStream.count);
    write(paramDerOutputStream.buf, 0, paramDerOutputStream.count);
  }
  
  public void writeImplicit(byte paramByte, DerOutputStream paramDerOutputStream) throws IOException {
    write(paramByte);
    write(paramDerOutputStream.buf, 1, paramDerOutputStream.count - 1);
  }
  
  public void putDerValue(DerValue paramDerValue) throws IOException { paramDerValue.encode(this); }
  
  public void putBoolean(boolean paramBoolean) throws IOException {
    write(1);
    putLength(1);
    if (paramBoolean) {
      write(255);
    } else {
      write(0);
    } 
  }
  
  public void putEnumerated(int paramInt) {
    write(10);
    putIntegerContents(paramInt);
  }
  
  public void putInteger(BigInteger paramBigInteger) throws IOException {
    write(2);
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    putLength(arrayOfByte.length);
    write(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public void putInteger(Integer paramInteger) throws IOException { putInteger(paramInteger.intValue()); }
  
  public void putInteger(int paramInt) {
    write(2);
    putIntegerContents(paramInt);
  }
  
  private void putIntegerContents(int paramInt) {
    byte[] arrayOfByte = new byte[4];
    byte b1 = 0;
    arrayOfByte[3] = (byte)(paramInt & 0xFF);
    arrayOfByte[2] = (byte)((paramInt & 0xFF00) >>> 8);
    arrayOfByte[1] = (byte)((paramInt & 0xFF0000) >>> 16);
    arrayOfByte[0] = (byte)((paramInt & 0xFF000000) >>> 24);
    if (arrayOfByte[0] == -1) {
      for (byte b = 0; b < 3 && arrayOfByte[b] == -1 && (arrayOfByte[b + true] & 0x80) == 128; b++)
        b1++; 
    } else if (arrayOfByte[0] == 0) {
      for (byte b = 0; b < 3 && arrayOfByte[b] == 0 && (arrayOfByte[b + true] & 0x80) == 0; b++)
        b1++; 
    } 
    putLength(4 - b1);
    for (byte b2 = b1; b2 < 4; b2++)
      write(arrayOfByte[b2]); 
  }
  
  public void putBitString(byte[] paramArrayOfByte) throws IOException {
    write(3);
    putLength(paramArrayOfByte.length + 1);
    write(0);
    write(paramArrayOfByte);
  }
  
  public void putUnalignedBitString(BitArray paramBitArray) throws IOException {
    byte[] arrayOfByte = paramBitArray.toByteArray();
    write(3);
    putLength(arrayOfByte.length + 1);
    write(arrayOfByte.length * 8 - paramBitArray.length());
    write(arrayOfByte);
  }
  
  public void putTruncatedUnalignedBitString(BitArray paramBitArray) throws IOException { putUnalignedBitString(paramBitArray.truncate()); }
  
  public void putOctetString(byte[] paramArrayOfByte) throws IOException { write((byte)4, paramArrayOfByte); }
  
  public void putNull() {
    write(5);
    putLength(0);
  }
  
  public void putOID(ObjectIdentifier paramObjectIdentifier) throws IOException { paramObjectIdentifier.encode(this); }
  
  public void putSequence(DerValue[] paramArrayOfDerValue) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    for (byte b = 0; b < paramArrayOfDerValue.length; b++)
      paramArrayOfDerValue[b].encode(derOutputStream); 
    write((byte)48, derOutputStream);
  }
  
  public void putSet(DerValue[] paramArrayOfDerValue) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    for (byte b = 0; b < paramArrayOfDerValue.length; b++)
      paramArrayOfDerValue[b].encode(derOutputStream); 
    write((byte)49, derOutputStream);
  }
  
  public void putOrderedSetOf(byte paramByte, DerEncoder[] paramArrayOfDerEncoder) throws IOException { putOrderedSet(paramByte, paramArrayOfDerEncoder, lexOrder); }
  
  public void putOrderedSet(byte paramByte, DerEncoder[] paramArrayOfDerEncoder) throws IOException { putOrderedSet(paramByte, paramArrayOfDerEncoder, tagOrder); }
  
  private void putOrderedSet(byte paramByte, DerEncoder[] paramArrayOfDerEncoder, Comparator<byte[]> paramComparator) throws IOException {
    DerOutputStream[] arrayOfDerOutputStream = new DerOutputStream[paramArrayOfDerEncoder.length];
    for (byte b1 = 0; b1 < paramArrayOfDerEncoder.length; b1++) {
      arrayOfDerOutputStream[b1] = new DerOutputStream();
      paramArrayOfDerEncoder[b1].derEncode(arrayOfDerOutputStream[b1]);
    } 
    byte[][] arrayOfByte = new byte[arrayOfDerOutputStream.length][];
    for (byte b2 = 0; b2 < arrayOfDerOutputStream.length; b2++)
      arrayOfByte[b2] = arrayOfDerOutputStream[b2].toByteArray(); 
    Arrays.sort(arrayOfByte, paramComparator);
    DerOutputStream derOutputStream = new DerOutputStream();
    for (byte b3 = 0; b3 < arrayOfDerOutputStream.length; b3++)
      derOutputStream.write(arrayOfByte[b3]); 
    write(paramByte, derOutputStream);
  }
  
  public void putUTF8String(String paramString) throws IOException { writeString(paramString, (byte)12, "UTF8"); }
  
  public void putPrintableString(String paramString) throws IOException { writeString(paramString, (byte)19, "ASCII"); }
  
  public void putT61String(String paramString) throws IOException { writeString(paramString, (byte)20, "ISO-8859-1"); }
  
  public void putIA5String(String paramString) throws IOException { writeString(paramString, (byte)22, "ASCII"); }
  
  public void putBMPString(String paramString) throws IOException { writeString(paramString, (byte)30, "UnicodeBigUnmarked"); }
  
  public void putGeneralString(String paramString) throws IOException { writeString(paramString, (byte)27, "ASCII"); }
  
  private void writeString(String paramString1, byte paramByte, String paramString2) throws IOException {
    byte[] arrayOfByte = paramString1.getBytes(paramString2);
    write(paramByte);
    putLength(arrayOfByte.length);
    write(arrayOfByte);
  }
  
  public void putUTCTime(Date paramDate) throws IOException { putTime(paramDate, (byte)23); }
  
  public void putGeneralizedTime(Date paramDate) throws IOException { putTime(paramDate, (byte)24); }
  
  private void putTime(Date paramDate, byte paramByte) throws IOException {
    TimeZone timeZone = TimeZone.getTimeZone("GMT");
    String str = null;
    if (paramByte == 23) {
      str = "yyMMddHHmmss'Z'";
    } else {
      paramByte = 24;
      str = "yyyyMMddHHmmss'Z'";
    } 
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str, Locale.US);
    simpleDateFormat.setTimeZone(timeZone);
    byte[] arrayOfByte = simpleDateFormat.format(paramDate).getBytes("ISO-8859-1");
    write(paramByte);
    putLength(arrayOfByte.length);
    write(arrayOfByte);
  }
  
  public void putLength(int paramInt) {
    if (paramInt < 128) {
      write((byte)paramInt);
    } else if (paramInt < 256) {
      write(-127);
      write((byte)paramInt);
    } else if (paramInt < 65536) {
      write(-126);
      write((byte)(paramInt >> 8));
      write((byte)paramInt);
    } else if (paramInt < 16777216) {
      write(-125);
      write((byte)(paramInt >> 16));
      write((byte)(paramInt >> 8));
      write((byte)paramInt);
    } else {
      write(-124);
      write((byte)(paramInt >> 24));
      write((byte)(paramInt >> 16));
      write((byte)(paramInt >> 8));
      write((byte)paramInt);
    } 
  }
  
  public void putTag(byte paramByte1, boolean paramBoolean, byte paramByte2) {
    byte b = (byte)(paramByte1 | paramByte2);
    if (paramBoolean)
      b = (byte)(b | 0x20); 
    write(b);
  }
  
  public void derEncode(OutputStream paramOutputStream) throws IOException { paramOutputStream.write(toByteArray()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\DerOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */