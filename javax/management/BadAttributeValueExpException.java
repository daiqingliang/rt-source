package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;

public class BadAttributeValueExpException extends Exception {
  private static final long serialVersionUID = -3105272988410493376L;
  
  private Object val;
  
  public BadAttributeValueExpException(Object paramObject) { this.val = (paramObject == null) ? null : paramObject.toString(); }
  
  public String toString() { return "BadAttributeValueException: " + this.val; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Object object = getField.get("val", null);
    if (object == null) {
      this.val = null;
    } else if (object instanceof String) {
      this.val = object;
    } else if (System.getSecurityManager() == null || object instanceof Long || object instanceof Integer || object instanceof Float || object instanceof Double || object instanceof Byte || object instanceof Short || object instanceof Boolean) {
      this.val = object.toString();
    } else {
      this.val = System.identityHashCode(object) + "@" + object.getClass().getName();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\BadAttributeValueExpException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */