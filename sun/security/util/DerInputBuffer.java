package sun.security.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.Gregorian;

class DerInputBuffer extends ByteArrayInputStream implements Cloneable {
  boolean allowBER = true;
  
  DerInputBuffer(byte[] paramArrayOfByte) { this(paramArrayOfByte, true); }
  
  DerInputBuffer(byte[] paramArrayOfByte, boolean paramBoolean) {
    super(paramArrayOfByte);
    this.allowBER = paramBoolean;
  }
  
  DerInputBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean) {
    super(paramArrayOfByte, paramInt1, paramInt2);
    this.allowBER = paramBoolean;
  }
  
  DerInputBuffer dup() {
    try {
      DerInputBuffer derInputBuffer = (DerInputBuffer)clone();
      derInputBuffer.mark(2147483647);
      return derInputBuffer;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new IllegalArgumentException(cloneNotSupportedException.toString());
    } 
  }
  
  byte[] toByteArray() {
    int i = available();
    if (i <= 0)
      return null; 
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(this.buf, this.pos, arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  int peek() throws IOException {
    if (this.pos >= this.count)
      throw new IOException("out of data"); 
    return this.buf[this.pos];
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof DerInputBuffer) ? equals((DerInputBuffer)paramObject) : 0; }
  
  boolean equals(DerInputBuffer paramDerInputBuffer) {
    if (this == paramDerInputBuffer)
      return true; 
    int i = available();
    if (paramDerInputBuffer.available() != i)
      return false; 
    for (int j = 0; j < i; j++) {
      if (this.buf[this.pos + j] != paramDerInputBuffer.buf[paramDerInputBuffer.pos + j])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() throws IOException {
    byte b = 0;
    int i = available();
    int j = this.pos;
    for (int k = 0; k < i; k++)
      b += this.buf[j + k] * k; 
    return b;
  }
  
  void truncate(int paramInt) throws IOException {
    if (paramInt > available())
      throw new IOException("insufficient data"); 
    this.count = this.pos + paramInt;
  }
  
  BigInteger getBigInteger(int paramInt, boolean paramBoolean) throws IOException {
    if (paramInt > available())
      throw new IOException("short read of integer"); 
    if (paramInt == 0)
      throw new IOException("Invalid encoding: zero length Int value"); 
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(this.buf, this.pos, arrayOfByte, 0, paramInt);
    skip(paramInt);
    if (!this.allowBER && paramInt >= 2 && arrayOfByte[0] == 0 && arrayOfByte[1] >= 0)
      throw new IOException("Invalid encoding: redundant leading 0s"); 
    return paramBoolean ? new BigInteger(1, arrayOfByte) : new BigInteger(arrayOfByte);
  }
  
  public int getInteger(int paramInt) throws IOException {
    BigInteger bigInteger;
    if (bigInteger.compareTo((bigInteger = getBigInteger(paramInt, false)).valueOf(-2147483648L)) < 0)
      throw new IOException("Integer below minimum valid value"); 
    if (bigInteger.compareTo(BigInteger.valueOf(2147483647L)) > 0)
      throw new IOException("Integer exceeds maximum valid value"); 
    return bigInteger.intValue();
  }
  
  public byte[] getBitString(int paramInt) throws IOException {
    if (paramInt > available())
      throw new IOException("short read of bit string"); 
    if (paramInt == 0)
      throw new IOException("Invalid encoding: zero length bit string"); 
    byte b = this.buf[this.pos];
    if (b < 0 || b > 7)
      throw new IOException("Invalid number of padding bits"); 
    byte[] arrayOfByte = new byte[paramInt - 1];
    System.arraycopy(this.buf, this.pos + 1, arrayOfByte, 0, paramInt - 1);
    if (b != 0)
      arrayOfByte[paramInt - 2] = (byte)(arrayOfByte[paramInt - 2] & 'ÿ' << b); 
    skip(paramInt);
    return arrayOfByte;
  }
  
  byte[] getBitString() { return getBitString(available()); }
  
  BitArray getUnalignedBitString() throws IOException {
    if (this.pos >= this.count)
      return null; 
    int i = available();
    byte b = this.buf[this.pos] & 0xFF;
    if (b > 7)
      throw new IOException("Invalid value for unused bits: " + b); 
    byte[] arrayOfByte = new byte[i - 1];
    byte b1 = (arrayOfByte.length == 0) ? 0 : (arrayOfByte.length * 8 - b);
    System.arraycopy(this.buf, this.pos + 1, arrayOfByte, 0, i - 1);
    BitArray bitArray = new BitArray(b1, arrayOfByte);
    this.pos = this.count;
    return bitArray;
  }
  
  public Date getUTCTime(int paramInt) throws IOException {
    if (paramInt > available())
      throw new IOException("short read of DER UTC Time"); 
    if (paramInt < 11 || paramInt > 17)
      throw new IOException("DER UTC Time length error"); 
    return getTime(paramInt, false);
  }
  
  public Date getGeneralizedTime(int paramInt) throws IOException {
    if (paramInt > available())
      throw new IOException("short read of DER Generalized Time"); 
    if (paramInt < 13 || paramInt > 23)
      throw new IOException("DER Generalized Time length error"); 
    return getTime(paramInt, true);
  }
  
  private Date getTime(int paramInt, boolean paramBoolean) throws IOException {
    int i3;
    int i2;
    byte b;
    int i;
    String str = null;
    if (paramBoolean) {
      str = "Generalized";
      i = 1000 * Character.digit((char)this.buf[this.pos++], 10);
      i += 100 * Character.digit((char)this.buf[this.pos++], 10);
      i += 10 * Character.digit((char)this.buf[this.pos++], 10);
      i += Character.digit((char)this.buf[this.pos++], 10);
      paramInt -= 2;
    } else {
      str = "UTC";
      i = 10 * Character.digit((char)this.buf[this.pos++], 10);
      i += Character.digit((char)this.buf[this.pos++], 10);
      if (i < 50) {
        i += 2000;
      } else {
        i += 1900;
      } 
    } 
    int j = 10 * Character.digit((char)this.buf[this.pos++], 10);
    j += Character.digit((char)this.buf[this.pos++], 10);
    int k = 10 * Character.digit((char)this.buf[this.pos++], 10);
    k += Character.digit((char)this.buf[this.pos++], 10);
    int m = 10 * Character.digit((char)this.buf[this.pos++], 10);
    m += Character.digit((char)this.buf[this.pos++], 10);
    int n = 10 * Character.digit((char)this.buf[this.pos++], 10);
    n += Character.digit((char)this.buf[this.pos++], 10);
    paramInt -= 10;
    int i1 = 0;
    if (paramInt > 2 && paramInt < 12) {
      b = 10 * Character.digit((char)this.buf[this.pos++], 10);
      b += Character.digit((char)this.buf[this.pos++], 10);
      paramInt -= 2;
      if (this.buf[this.pos] == 46 || this.buf[this.pos] == 44) {
        paramInt--;
        this.pos++;
        int i4 = 0;
        int i5 = this.pos;
        while (this.buf[i5] != 90 && this.buf[i5] != 43 && this.buf[i5] != 45) {
          i5++;
          i4++;
        } 
        switch (i4) {
          case 3:
            i1 += 100 * Character.digit((char)this.buf[this.pos++], 10);
            i1 += 10 * Character.digit((char)this.buf[this.pos++], 10);
            i1 += Character.digit((char)this.buf[this.pos++], 10);
            break;
          case 2:
            i1 += 100 * Character.digit((char)this.buf[this.pos++], 10);
            i1 += 10 * Character.digit((char)this.buf[this.pos++], 10);
            break;
          case 1:
            i1 += 100 * Character.digit((char)this.buf[this.pos++], 10);
            break;
          default:
            throw new IOException("Parse " + str + " time, unsupported precision for seconds value");
        } 
        paramInt -= i4;
      } 
    } else {
      b = 0;
    } 
    if (j == 0 || k == 0 || j > 12 || k > 31 || m >= 24 || n >= 60 || b >= 60)
      throw new IOException("Parse " + str + " time, invalid format"); 
    Gregorian gregorian = CalendarSystem.getGregorianCalendar();
    CalendarDate calendarDate = gregorian.newCalendarDate(null);
    calendarDate.setDate(i, j, k);
    calendarDate.setTimeOfDay(m, n, b, i1);
    long l = gregorian.getTime(calendarDate);
    if (paramInt != 1 && paramInt != 5)
      throw new IOException("Parse " + str + " time, invalid offset"); 
    switch (this.buf[this.pos++]) {
      case 43:
        i2 = 10 * Character.digit((char)this.buf[this.pos++], 10);
        i2 += Character.digit((char)this.buf[this.pos++], 10);
        i3 = 10 * Character.digit((char)this.buf[this.pos++], 10);
        i3 += Character.digit((char)this.buf[this.pos++], 10);
        if (i2 >= 24 || i3 >= 60)
          throw new IOException("Parse " + str + " time, +hhmm"); 
        l -= ((i2 * 60 + i3) * 60 * 1000);
      case 45:
        i2 = 10 * Character.digit((char)this.buf[this.pos++], 10);
        i2 += Character.digit((char)this.buf[this.pos++], 10);
        i3 = 10 * Character.digit((char)this.buf[this.pos++], 10);
        i3 += Character.digit((char)this.buf[this.pos++], 10);
        if (i2 >= 24 || i3 >= 60)
          throw new IOException("Parse " + str + " time, -hhmm"); 
        l += ((i2 * 60 + i3) * 60 * 1000);
      case 90:
        return new Date(l);
    } 
    throw new IOException("Parse " + str + " time, garbage offset");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\DerInputBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */