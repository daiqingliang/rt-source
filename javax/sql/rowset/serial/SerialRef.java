package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Ref;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

public class SerialRef implements Ref, Serializable, Cloneable {
  private String baseTypeName;
  
  private Object object;
  
  private Ref reference;
  
  static final long serialVersionUID = -4727123500609662274L;
  
  public SerialRef(Ref paramRef) throws SerialException, SQLException {
    if (paramRef == null)
      throw new SQLException("Cannot instantiate a SerialRef object with a null Ref object"); 
    this.reference = paramRef;
    this.object = paramRef;
    if (paramRef.getBaseTypeName() == null)
      throw new SQLException("Cannot instantiate a SerialRef object that returns a null base type name"); 
    this.baseTypeName = paramRef.getBaseTypeName();
  }
  
  public String getBaseTypeName() throws SerialException { return this.baseTypeName; }
  
  public Object getObject(Map<String, Class<?>> paramMap) throws SerialException {
    paramMap = new Hashtable<String, Class<?>>(paramMap);
    if (this.object != null)
      return paramMap.get(this.object); 
    throw new SerialException("The object is not set");
  }
  
  public Object getObject() throws SerialException {
    if (this.reference != null)
      try {
        return this.reference.getObject();
      } catch (SQLException sQLException) {
        throw new SerialException("SQLException: " + sQLException.getMessage());
      }  
    if (this.object != null)
      return this.object; 
    throw new SerialException("The object is not set");
  }
  
  public void setObject(Object paramObject) throws SerialException {
    try {
      this.reference.setObject(paramObject);
    } catch (SQLException sQLException) {
      throw new SerialException("SQLException: " + sQLException.getMessage());
    } 
    this.object = paramObject;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SerialRef) {
      SerialRef serialRef = (SerialRef)paramObject;
      return (this.baseTypeName.equals(serialRef.baseTypeName) && this.object.equals(serialRef.object));
    } 
    return false;
  }
  
  public int hashCode() { return (31 + this.object.hashCode()) * 31 + this.baseTypeName.hashCode(); }
  
  public Object clone() throws SerialException {
    try {
      SerialRef serialRef = (SerialRef)super.clone();
      serialRef.reference = null;
      return serialRef;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.object = getField.get("object", null);
    this.baseTypeName = (String)getField.get("baseTypeName", null);
    this.reference = (Ref)getField.get("reference", null);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException, ClassNotFoundException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("baseTypeName", this.baseTypeName);
    putField.put("object", this.object);
    putField.put("reference", (this.reference instanceof Serializable) ? this.reference : null);
    paramObjectOutputStream.writeFields();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SerialRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */