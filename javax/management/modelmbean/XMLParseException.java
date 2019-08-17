package javax.management.modelmbean;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;

public class XMLParseException extends Exception {
  private static final long oldSerialVersionUID = -7780049316655891976L;
  
  private static final long newSerialVersionUID = 3176664577895105181L;
  
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("msgStr", String.class) };
  
  private static final ObjectStreamField[] newSerialPersistentFields = new ObjectStreamField[0];
  
  private static final long serialVersionUID;
  
  private static final ObjectStreamField[] serialPersistentFields;
  
  private static boolean compat = false;
  
  public XMLParseException() { super("XML Parse Exception."); }
  
  public XMLParseException(String paramString) { super("XML Parse Exception: " + paramString); }
  
  public XMLParseException(Exception paramException, String paramString) { super("XML Parse Exception: " + paramString + ":" + paramException.toString()); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { paramObjectInputStream.defaultReadObject(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
      putField.put("msgStr", getMessage());
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
    } catch (Exception exception) {}
    if (compat) {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = -7780049316655891976L;
    } else {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 3176664577895105181L;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\modelmbean\XMLParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */