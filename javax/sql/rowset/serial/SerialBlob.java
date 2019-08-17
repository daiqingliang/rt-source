package javax.sql.rowset.serial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

public class SerialBlob implements Blob, Serializable, Cloneable {
  private byte[] buf;
  
  private Blob blob;
  
  private long len;
  
  private long origLen;
  
  static final long serialVersionUID = -8144641928112860441L;
  
  public SerialBlob(byte[] paramArrayOfByte) throws SerialException, SQLException {
    this.len = paramArrayOfByte.length;
    this.buf = new byte[(int)this.len];
    for (byte b = 0; b < this.len; b++)
      this.buf[b] = paramArrayOfByte[b]; 
    this.origLen = this.len;
  }
  
  public SerialBlob(Blob paramBlob) throws SerialException, SQLException {
    if (paramBlob == null)
      throw new SQLException("Cannot instantiate a SerialBlob object with a null Blob object"); 
    this.len = paramBlob.length();
    this.buf = paramBlob.getBytes(1L, (int)this.len);
    this.blob = paramBlob;
    this.origLen = this.len;
  }
  
  public byte[] getBytes(long paramLong, int paramInt) throws SerialException {
    isValid();
    if (paramInt > this.len)
      paramInt = (int)this.len; 
    if (paramLong < 1L || this.len - paramLong < 0L)
      throw new SerialException("Invalid arguments: position cannot be less than 1 or greater than the length of the SerialBlob"); 
    paramLong--;
    byte[] arrayOfByte = new byte[paramInt];
    for (byte b = 0; b < paramInt; b++) {
      arrayOfByte[b] = this.buf[(int)paramLong];
      paramLong++;
    } 
    return arrayOfByte;
  }
  
  public long length() throws SerialException {
    isValid();
    return this.len;
  }
  
  public InputStream getBinaryStream() throws SerialException {
    isValid();
    return new ByteArrayInputStream(this.buf);
  }
  
  public long position(byte[] paramArrayOfByte, long paramLong) throws SerialException, SQLException {
    isValid();
    if (paramLong < 1L || paramLong > this.len)
      return -1L; 
    int i = (int)paramLong - 1;
    byte b = 0;
    long l = paramArrayOfByte.length;
    while (i < this.len) {
      if (paramArrayOfByte[b] == this.buf[i]) {
        if ((b + true) == l)
          return (i + 1) - l - 1L; 
        b++;
        i++;
        continue;
      } 
      if (paramArrayOfByte[b] != this.buf[i])
        i++; 
    } 
    return -1L;
  }
  
  public long position(Blob paramBlob, long paramLong) throws SerialException, SQLException {
    isValid();
    return position(paramBlob.getBytes(1L, (int)paramBlob.length()), paramLong);
  }
  
  public int setBytes(long paramLong, byte[] paramArrayOfByte) throws SerialException, SQLException { return setBytes(paramLong, paramArrayOfByte, 0, paramArrayOfByte.length); }
  
  public int setBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws SerialException, SQLException {
    isValid();
    if (paramInt1 < 0 || paramInt1 > paramArrayOfByte.length)
      throw new SerialException("Invalid offset in byte array set"); 
    if (paramLong < 1L || paramLong > length())
      throw new SerialException("Invalid position in BLOB object set"); 
    if (paramInt2 > this.origLen)
      throw new SerialException("Buffer is not sufficient to hold the value"); 
    if (paramInt2 + paramInt1 > paramArrayOfByte.length)
      throw new SerialException("Invalid OffSet. Cannot have combined offset and length that is greater that the Blob buffer"); 
    int i = 0;
    paramLong--;
    while (i < paramInt2 || paramInt1 + i + 1 < paramArrayOfByte.length - paramInt1) {
      this.buf[(int)paramLong + i] = paramArrayOfByte[paramInt1 + i];
      i++;
    } 
    return i;
  }
  
  public OutputStream setBinaryStream(long paramLong) throws SerialException, SQLException {
    isValid();
    if (this.blob != null)
      return this.blob.setBinaryStream(paramLong); 
    throw new SerialException("Unsupported operation. SerialBlob cannot return a writable binary stream, unless instantiated with a Blob object that provides a setBinaryStream() implementation");
  }
  
  public void truncate(long paramLong) throws SerialException {
    isValid();
    if (paramLong > this.len)
      throw new SerialException("Length more than what can be truncated"); 
    if ((int)paramLong == 0) {
      this.buf = new byte[0];
      this.len = paramLong;
    } else {
      this.len = paramLong;
      this.buf = getBytes(1L, (int)this.len);
    } 
  }
  
  public InputStream getBinaryStream(long paramLong1, long paramLong2) throws SQLException {
    isValid();
    if (paramLong1 < 1L || paramLong1 > length())
      throw new SerialException("Invalid position in BLOB object set"); 
    if (paramLong2 < 1L || paramLong2 > this.len - paramLong1 + 1L)
      throw new SerialException("length is < 1 or pos + length > total number of bytes"); 
    return new ByteArrayInputStream(this.buf, (int)paramLong1 - 1, (int)paramLong2);
  }
  
  public void free() throws SQLException {
    if (this.buf != null) {
      this.buf = null;
      if (this.blob != null)
        this.blob.free(); 
      this.blob = null;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SerialBlob) {
      SerialBlob serialBlob = (SerialBlob)paramObject;
      if (this.len == serialBlob.len)
        return Arrays.equals(this.buf, serialBlob.buf); 
    } 
    return false;
  }
  
  public int hashCode() { return ((31 + Arrays.hashCode(this.buf)) * 31 + (int)this.len) * 31 + (int)this.origLen; }
  
  public Object clone() {
    try {
      SerialBlob serialBlob = (SerialBlob)super.clone();
      serialBlob.buf = (this.buf != null) ? Arrays.copyOf(this.buf, (int)this.len) : null;
      serialBlob.blob = null;
      return serialBlob;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    byte[] arrayOfByte = (byte[])getField.get("buf", null);
    if (arrayOfByte == null)
      throw new InvalidObjectException("buf is null and should not be!"); 
    this.buf = (byte[])arrayOfByte.clone();
    this.len = getField.get("len", 0L);
    if (this.buf.length != this.len)
      throw new InvalidObjectException("buf is not the expected size"); 
    this.origLen = getField.get("origLen", 0L);
    this.blob = (Blob)getField.get("blob", null);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("buf", this.buf);
    putField.put("len", this.len);
    putField.put("origLen", this.origLen);
    putField.put("blob", (this.blob instanceof Serializable) ? this.blob : null);
    paramObjectOutputStream.writeFields();
  }
  
  private void isValid() throws SQLException {
    if (this.buf == null)
      throw new SerialException("Error: You cannot call a method on a SerialBlob instance once free() has been called."); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SerialBlob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */