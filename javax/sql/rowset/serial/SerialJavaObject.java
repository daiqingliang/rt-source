package javax.sql.rowset.serial;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Vector;
import javax.sql.rowset.RowSetWarning;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class SerialJavaObject implements Serializable, Cloneable {
  private Object obj;
  
  private Field[] fields;
  
  static final long serialVersionUID = -1465795139032831023L;
  
  Vector<RowSetWarning> chain;
  
  public SerialJavaObject(Object paramObject) throws SerialException {
    Class clazz = paramObject.getClass();
    if (!(paramObject instanceof Serializable))
      setWarning(new RowSetWarning("Warning, the object passed to the constructor does not implement Serializable")); 
    this.fields = clazz.getFields();
    if (hasStaticFields(this.fields))
      throw new SerialException("Located static fields in object instance. Cannot serialize"); 
    this.obj = paramObject;
  }
  
  public Object getObject() throws SerialException { return this.obj; }
  
  @CallerSensitive
  public Field[] getFields() throws SerialException {
    if (this.fields != null) {
      Class clazz = this.obj.getClass();
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        Class clazz1 = Reflection.getCallerClass();
        if (ReflectUtil.needsPackageAccessCheck(clazz1.getClassLoader(), clazz.getClassLoader()))
          ReflectUtil.checkPackageAccess(clazz); 
      } 
      return clazz.getFields();
    } 
    throw new SerialException("SerialJavaObject does not contain a serialized object instance");
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof SerialJavaObject) {
      SerialJavaObject serialJavaObject = (SerialJavaObject)paramObject;
      return this.obj.equals(serialJavaObject.obj);
    } 
    return false;
  }
  
  public int hashCode() { return 31 + this.obj.hashCode(); }
  
  public Object clone() throws SerialException {
    try {
      SerialJavaObject serialJavaObject = (SerialJavaObject)super.clone();
      serialJavaObject.fields = (Field[])Arrays.copyOf(this.fields, this.fields.length);
      if (this.chain != null)
        serialJavaObject.chain = new Vector(this.chain); 
      return serialJavaObject;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError();
    } 
  }
  
  private void setWarning(RowSetWarning paramRowSetWarning) {
    if (this.chain == null)
      this.chain = new Vector(); 
    this.chain.add(paramRowSetWarning);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Vector vector = (Vector)getField.get("chain", null);
    if (vector != null)
      this.chain = new Vector(vector); 
    this.obj = getField.get("obj", null);
    if (this.obj != null) {
      this.fields = this.obj.getClass().getFields();
      if (hasStaticFields(this.fields))
        throw new IOException("Located static fields in object instance. Cannot serialize"); 
    } else {
      throw new IOException("Object cannot be null!");
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("obj", this.obj);
    putField.put("chain", this.chain);
    paramObjectOutputStream.writeFields();
  }
  
  private static boolean hasStaticFields(Field[] paramArrayOfField) {
    for (Field field : paramArrayOfField) {
      if (field.getModifiers() == 8)
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SerialJavaObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */