package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class SerialStruct implements Struct, Serializable, Cloneable {
  private String SQLTypeName;
  
  private Object[] attribs;
  
  static final long serialVersionUID = -8322445504027483372L;
  
  public SerialStruct(Struct paramStruct, Map<String, Class<?>> paramMap) throws SerialException {
    try {
      this.SQLTypeName = paramStruct.getSQLTypeName();
      System.out.println("SQLTypeName: " + this.SQLTypeName);
      this.attribs = paramStruct.getAttributes(paramMap);
      mapToSerial(paramMap);
    } catch (SQLException sQLException) {
      throw new SerialException(sQLException.getMessage());
    } 
  }
  
  public SerialStruct(SQLData paramSQLData, Map<String, Class<?>> paramMap) throws SerialException {
    try {
      this.SQLTypeName = paramSQLData.getSQLTypeName();
      Vector vector = new Vector();
      paramSQLData.writeSQL(new SQLOutputImpl(vector, paramMap));
      this.attribs = vector.toArray();
    } catch (SQLException sQLException) {
      throw new SerialException(sQLException.getMessage());
    } 
  }
  
  public String getSQLTypeName() throws SerialException { return this.SQLTypeName; }
  
  public Object[] getAttributes() throws SerialException {
    Object[] arrayOfObject = this.attribs;
    return (arrayOfObject == null) ? null : Arrays.copyOf(arrayOfObject, arrayOfObject.length);
  }
  
  public Object[] getAttributes(Map<String, Class<?>> paramMap) throws SerialException {
    Object[] arrayOfObject = this.attribs;
    return (arrayOfObject == null) ? null : Arrays.copyOf(arrayOfObject, arrayOfObject.length);
  }
  
  private void mapToSerial(Map<String, Class<?>> paramMap) throws SerialException {
    try {
      for (byte b = 0; b < this.attribs.length; b++) {
        if (this.attribs[b] instanceof Struct) {
          this.attribs[b] = new SerialStruct((Struct)this.attribs[b], paramMap);
        } else if (this.attribs[b] instanceof SQLData) {
          this.attribs[b] = new SerialStruct((SQLData)this.attribs[b], paramMap);
        } else if (this.attribs[b] instanceof Blob) {
          this.attribs[b] = new SerialBlob((Blob)this.attribs[b]);
        } else if (this.attribs[b] instanceof Clob) {
          this.attribs[b] = new SerialClob((Clob)this.attribs[b]);
        } else if (this.attribs[b] instanceof Ref) {
          this.attribs[b] = new SerialRef((Ref)this.attribs[b]);
        } else if (this.attribs[b] instanceof Array) {
          this.attribs[b] = new SerialArray((Array)this.attribs[b], paramMap);
        } 
      } 
    } catch (SQLException sQLException) {
      throw new SerialException(sQLException.getMessage());
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SerialStruct) {
      SerialStruct serialStruct = (SerialStruct)paramObject;
      return (this.SQLTypeName.equals(serialStruct.SQLTypeName) && Arrays.equals(this.attribs, serialStruct.attribs));
    } 
    return false;
  }
  
  public int hashCode() { return (31 + Arrays.hashCode(this.attribs)) * 31 * 31 + this.SQLTypeName.hashCode(); }
  
  public Object clone() {
    try {
      SerialStruct serialStruct = (SerialStruct)super.clone();
      serialStruct.attribs = Arrays.copyOf(this.attribs, this.attribs.length);
      return serialStruct;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Object[] arrayOfObject = (Object[])getField.get("attribs", null);
    this.attribs = (arrayOfObject == null) ? null : (Object[])arrayOfObject.clone();
    this.SQLTypeName = (String)getField.get("SQLTypeName", null);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("attribs", this.attribs);
    putField.put("SQLTypeName", this.SQLTypeName);
    paramObjectOutputStream.writeFields();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SerialStruct.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */