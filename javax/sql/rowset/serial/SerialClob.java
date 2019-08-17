package javax.sql.rowset.serial;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Arrays;

public class SerialClob implements Clob, Serializable, Cloneable {
  private char[] buf;
  
  private Clob clob;
  
  private long len;
  
  private long origLen;
  
  static final long serialVersionUID = -1662519690087375313L;
  
  public SerialClob(char[] paramArrayOfChar) throws SerialException, SQLException {
    this.len = paramArrayOfChar.length;
    this.buf = new char[(int)this.len];
    for (byte b = 0; b < this.len; b++)
      this.buf[b] = paramArrayOfChar[b]; 
    this.origLen = this.len;
    this.clob = null;
  }
  
  public SerialClob(Clob paramClob) throws SerialException, SQLException {
    if (paramClob == null)
      throw new SQLException("Cannot instantiate a SerialClob object with a null Clob object"); 
    this.len = paramClob.length();
    this.clob = paramClob;
    this.buf = new char[(int)this.len];
    int i = 0;
    int j = 0;
    try (Reader null = paramClob.getCharacterStream()) {
      if (reader == null)
        throw new SQLException("Invalid Clob object. The call to getCharacterStream returned null which cannot be serialized."); 
      try (InputStream null = paramClob.getAsciiStream()) {
        if (inputStream == null)
          throw new SQLException("Invalid Clob object. The call to getAsciiStream returned null which cannot be serialized."); 
      } 
      try (BufferedReader null = new BufferedReader(reader)) {
        do {
          i = bufferedReader.read(this.buf, j, (int)(this.len - j));
          j += i;
        } while (i > 0);
      } 
    } catch (IOException iOException) {
      throw new SerialException("SerialClob: " + iOException.getMessage());
    } 
    this.origLen = this.len;
  }
  
  public long length() throws SerialException {
    isValid();
    return this.len;
  }
  
  public Reader getCharacterStream() throws SerialException {
    isValid();
    return new CharArrayReader(this.buf);
  }
  
  public InputStream getAsciiStream() throws SerialException, SQLException {
    isValid();
    if (this.clob != null)
      return this.clob.getAsciiStream(); 
    throw new SerialException("Unsupported operation. SerialClob cannot return a the CLOB value as an ascii stream, unless instantiated with a fully implemented Clob object.");
  }
  
  public String getSubString(long paramLong, int paramInt) throws SerialException {
    isValid();
    if (paramLong < 1L || paramLong > length())
      throw new SerialException("Invalid position in SerialClob object set"); 
    if (paramLong - 1L + paramInt > length())
      throw new SerialException("Invalid position and substring length"); 
    try {
      return new String(this.buf, (int)paramLong - 1, paramInt);
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      throw new SerialException("StringIndexOutOfBoundsException: " + stringIndexOutOfBoundsException.getMessage());
    } 
  }
  
  public long position(String paramString, long paramLong) throws SerialException, SQLException {
    isValid();
    if (paramLong < 1L || paramLong > this.len)
      return -1L; 
    char[] arrayOfChar = paramString.toCharArray();
    int i = (int)paramLong - 1;
    byte b = 0;
    long l = arrayOfChar.length;
    while (i < this.len) {
      if (arrayOfChar[b] == this.buf[i]) {
        if ((b + true) == l)
          return (i + 1) - l - 1L; 
        b++;
        i++;
        continue;
      } 
      if (arrayOfChar[b] != this.buf[i])
        i++; 
    } 
    return -1L;
  }
  
  public long position(Clob paramClob, long paramLong) throws SerialException, SQLException {
    isValid();
    return position(paramClob.getSubString(1L, (int)paramClob.length()), paramLong);
  }
  
  public int setString(long paramLong, String paramString) throws SerialException { return setString(paramLong, paramString, 0, paramString.length()); }
  
  public int setString(long paramLong, String paramString, int paramInt1, int paramInt2) throws SerialException {
    isValid();
    String str = paramString.substring(paramInt1);
    char[] arrayOfChar = str.toCharArray();
    if (paramInt1 < 0 || paramInt1 > paramString.length())
      throw new SerialException("Invalid offset in byte array set"); 
    if (paramLong < 1L || paramLong > length())
      throw new SerialException("Invalid position in Clob object set"); 
    if (paramInt2 > this.origLen)
      throw new SerialException("Buffer is not sufficient to hold the value"); 
    if (paramInt2 + paramInt1 > paramString.length())
      throw new SerialException("Invalid OffSet. Cannot have combined offset  and length that is greater that the Blob buffer"); 
    int i = 0;
    paramLong--;
    while (i < paramInt2 || paramInt1 + i + 1 < paramString.length() - paramInt1) {
      this.buf[(int)paramLong + i] = arrayOfChar[paramInt1 + i];
      i++;
    } 
    return i;
  }
  
  public OutputStream setAsciiStream(long paramLong) throws SerialException, SQLException {
    isValid();
    if (this.clob != null)
      return this.clob.setAsciiStream(paramLong); 
    throw new SerialException("Unsupported operation. SerialClob cannot return a writable ascii stream\n unless instantiated with a Clob object that has a setAsciiStream() implementation");
  }
  
  public Writer setCharacterStream(long paramLong) throws SerialException, SQLException {
    isValid();
    if (this.clob != null)
      return this.clob.setCharacterStream(paramLong); 
    throw new SerialException("Unsupported operation. SerialClob cannot return a writable character stream\n unless instantiated with a Clob object that has a setCharacterStream implementation");
  }
  
  public void truncate(long paramLong) throws SerialException {
    isValid();
    if (paramLong > this.len)
      throw new SerialException("Length more than what can be truncated"); 
    this.len = paramLong;
    if (this.len == 0L) {
      this.buf = new char[0];
    } else {
      this.buf = getSubString(1L, (int)this.len).toCharArray();
    } 
  }
  
  public Reader getCharacterStream(long paramLong1, long paramLong2) throws SQLException {
    isValid();
    if (paramLong1 < 1L || paramLong1 > this.len)
      throw new SerialException("Invalid position in Clob object set"); 
    if (paramLong1 - 1L + paramLong2 > this.len)
      throw new SerialException("Invalid position and substring length"); 
    if (paramLong2 <= 0L)
      throw new SerialException("Invalid length specified"); 
    return new CharArrayReader(this.buf, (int)paramLong1, (int)paramLong2);
  }
  
  public void free() throws SQLException {
    if (this.buf != null) {
      this.buf = null;
      if (this.clob != null)
        this.clob.free(); 
      this.clob = null;
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SerialClob) {
      SerialClob serialClob = (SerialClob)paramObject;
      if (this.len == serialClob.len)
        return Arrays.equals(this.buf, serialClob.buf); 
    } 
    return false;
  }
  
  public int hashCode() { return ((31 + Arrays.hashCode(this.buf)) * 31 + (int)this.len) * 31 + (int)this.origLen; }
  
  public Object clone() {
    try {
      SerialClob serialClob = (SerialClob)super.clone();
      serialClob.buf = (this.buf != null) ? Arrays.copyOf(this.buf, (int)this.len) : null;
      serialClob.clob = null;
      return serialClob;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    char[] arrayOfChar = (char[])getField.get("buf", null);
    if (arrayOfChar == null)
      throw new InvalidObjectException("buf is null and should not be!"); 
    this.buf = (char[])arrayOfChar.clone();
    this.len = getField.get("len", 0L);
    if (this.buf.length != this.len)
      throw new InvalidObjectException("buf is not the expected size"); 
    this.origLen = getField.get("origLen", 0L);
    this.clob = (Clob)getField.get("clob", null);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("buf", this.buf);
    putField.put("len", this.len);
    putField.put("origLen", this.origLen);
    putField.put("clob", (this.clob instanceof Serializable) ? this.clob : null);
    paramObjectOutputStream.writeFields();
  }
  
  private void isValid() throws SQLException {
    if (this.buf == null)
      throw new SerialException("Error: You cannot call a method on a SerialClob instance once free() has been called."); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SerialClob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */