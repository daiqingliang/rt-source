package javax.management.modelmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;

public class InvalidTargetObjectTypeException extends Exception {
  private static final long oldSerialVersionUID = 3711724570458346634L;
  
  private static final long newSerialVersionUID = 1190536278266811217L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("msgStr", String.class), new ObjectStreamField("relatedExcept", Exception.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("exception", Exception.class) };
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  Exception exception = null;
  
  public InvalidTargetObjectTypeException() { super("InvalidTargetObjectTypeException: "); }
  
  public InvalidTargetObjectTypeException(String paramString) { super("InvalidTargetObjectTypeException: " + paramString); }
  
  public InvalidTargetObjectTypeException(Exception paramException, String paramString) { super("InvalidTargetObjectTypeException: " + paramString + ((paramException != null) ? ("\n\t triggered by:" + paramException.toString()) : "")); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
      this.exception = (Exception)getField.get("relatedExcept", null);
      if (getField.defaulted("relatedExcept"))
        throw new NullPointerException("relatedExcept"); 
    } else {
      paramObjectInputStream.defaultReadObject();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("relatedExcept", this.exception);
      putField.put("msgStr", (this.exception != null) ? this.exception.getMessage() : "");
      paramObjectOutputStream.writeFields();
    } else {
      paramObjectOutputStream.defaultWriteObject();
    } 
  }
  
  static  {
    try {
      GetPropertyAction getPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(getPropertyAction);
      compat = (str != null && str.equals("1.0"));
    } catch (Exception exception1) {}
    if (compat) {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 3711724570458346634L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 1190536278266811217L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\InvalidTargetObjectTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */