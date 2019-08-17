package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;

public class SerialArray implements Array, Serializable, Cloneable {
  private Object[] elements;
  
  private int baseType;
  
  private String baseTypeName;
  
  private int len;
  
  static final long serialVersionUID = -8466174297270688520L;
  
  public SerialArray(Array paramArray, Map<String, Class<?>> paramMap) throws SerialException, SQLException {
    if (paramArray == null || paramMap == null)
      throw new SQLException("Cannot instantiate a SerialArray object with null parameters"); 
    if ((this.elements = (Object[])paramArray.getArray()) == null)
      throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized"); 
    this.elements = (Object[])paramArray.getArray(paramMap);
    this.baseType = paramArray.getBaseType();
    this.baseTypeName = paramArray.getBaseTypeName();
    this.len = this.elements.length;
    switch (this.baseType) {
      case 2002:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialStruct((Struct)this.elements[b], paramMap); 
        break;
      case 2003:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialArray((Array)this.elements[b], paramMap); 
        break;
      case 2004:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialBlob((Blob)this.elements[b]); 
        break;
      case 2005:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialClob((Clob)this.elements[b]); 
        break;
      case 70:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialDatalink((URL)this.elements[b]); 
        break;
      case 2000:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialJavaObject(this.elements[b]); 
        break;
    } 
  }
  
  public void free() throws SQLException {
    if (this.elements != null) {
      this.elements = null;
      this.baseTypeName = null;
    } 
  }
  
  public SerialArray(Array paramArray) throws SerialException, SQLException {
    if (paramArray == null)
      throw new SQLException("Cannot instantiate a SerialArray object with a null Array object"); 
    if ((this.elements = (Object[])paramArray.getArray()) == null)
      throw new SQLException("Invalid Array object. Calls to Array.getArray() return null value which cannot be serialized"); 
    this.baseType = paramArray.getBaseType();
    this.baseTypeName = paramArray.getBaseTypeName();
    this.len = this.elements.length;
    switch (this.baseType) {
      case 2004:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialBlob((Blob)this.elements[b]); 
        break;
      case 2005:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialClob((Clob)this.elements[b]); 
        break;
      case 70:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialDatalink((URL)this.elements[b]); 
        break;
      case 2000:
        for (b = 0; b < this.len; b++)
          this.elements[b] = new SerialJavaObject(this.elements[b]); 
        break;
    } 
  }
  
  public Object getArray() throws SerialException {
    isValid();
    Object[] arrayOfObject = new Object[this.len];
    System.arraycopy(this.elements, 0, arrayOfObject, 0, this.len);
    return arrayOfObject;
  }
  
  public Object getArray(Map<String, Class<?>> paramMap) throws SerialException {
    isValid();
    Object[] arrayOfObject = new Object[this.len];
    System.arraycopy(this.elements, 0, arrayOfObject, 0, this.len);
    return arrayOfObject;
  }
  
  public Object getArray(long paramLong, int paramInt) throws SerialException {
    isValid();
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(this.elements, (int)paramLong, arrayOfObject, 0, paramInt);
    return arrayOfObject;
  }
  
  public Object getArray(long paramLong, int paramInt, Map<String, Class<?>> paramMap) throws SerialException {
    isValid();
    Object[] arrayOfObject = new Object[paramInt];
    System.arraycopy(this.elements, (int)paramLong, arrayOfObject, 0, paramInt);
    return arrayOfObject;
  }
  
  public int getBaseType() throws SerialException {
    isValid();
    return this.baseType;
  }
  
  public String getBaseTypeName() throws SerialException {
    isValid();
    return this.baseTypeName;
  }
  
  public ResultSet getResultSet(long paramLong, int paramInt) throws SerialException {
    SerialException serialException = new SerialException();
    serialException.initCause(new UnsupportedOperationException());
    throw serialException;
  }
  
  public ResultSet getResultSet(Map<String, Class<?>> paramMap) throws SerialException {
    SerialException serialException = new SerialException();
    serialException.initCause(new UnsupportedOperationException());
    throw serialException;
  }
  
  public ResultSet getResultSet() throws SerialException {
    SerialException serialException = new SerialException();
    serialException.initCause(new UnsupportedOperationException());
    throw serialException;
  }
  
  public ResultSet getResultSet(long paramLong, int paramInt, Map<String, Class<?>> paramMap) throws SerialException {
    SerialException serialException = new SerialException();
    serialException.initCause(new UnsupportedOperationException());
    throw serialException;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SerialArray) {
      SerialArray serialArray = (SerialArray)paramObject;
      return (this.baseType == serialArray.baseType && this.baseTypeName.equals(serialArray.baseTypeName) && Arrays.equals(this.elements, serialArray.elements));
    } 
    return false;
  }
  
  public int hashCode() throws SerialException { return (((31 + Arrays.hashCode(this.elements)) * 31 + this.len) * 31 + this.baseType) * 31 + this.baseTypeName.hashCode(); }
  
  public Object clone() throws SerialException {
    try {
      SerialArray serialArray = (SerialArray)super.clone();
      serialArray.elements = (this.elements != null) ? Arrays.copyOf(this.elements, this.len) : null;
      return serialArray;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Object[] arrayOfObject = (Object[])getField.get("elements", null);
    if (arrayOfObject == null)
      throw new InvalidObjectException("elements is null and should not be!"); 
    this.elements = (Object[])arrayOfObject.clone();
    this.len = getField.get("len", 0);
    if (this.elements.length != this.len)
      throw new InvalidObjectException("elements is not the expected size"); 
    this.baseType = getField.get("baseType", 0);
    this.baseTypeName = (String)getField.get("baseTypeName", null);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("elements", this.elements);
    putField.put("len", this.len);
    putField.put("baseType", this.baseType);
    putField.put("baseTypeName", this.baseTypeName);
    paramObjectOutputStream.writeFields();
  }
  
  private void isValid() throws SQLException {
    if (this.elements == null)
      throw new SerialException("Error: You cannot call a method on a SerialArray instance once free() has been called."); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SerialArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */