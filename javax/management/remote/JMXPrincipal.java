package javax.management.remote;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Principal;

public class JMXPrincipal implements Principal, Serializable {
  private static final long serialVersionUID = -4184480100214577411L;
  
  private String name;
  
  public JMXPrincipal(String paramString) {
    validate(paramString);
    this.name = paramString;
  }
  
  public String getName() { return this.name; }
  
  public String toString() { return "JMXPrincipal:  " + this.name; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof JMXPrincipal))
      return false; 
    JMXPrincipal jMXPrincipal = (JMXPrincipal)paramObject;
    return getName().equals(jMXPrincipal.getName());
  }
  
  public int hashCode() { return this.name.hashCode(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String str = (String)getField.get("name", null);
    try {
      validate(str);
      this.name = str;
    } catch (NullPointerException nullPointerException) {
      throw new InvalidObjectException(nullPointerException.getMessage());
    } 
  }
  
  private static void validate(String paramString) {
    if (paramString == null)
      throw new NullPointerException("illegal null input"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\JMXPrincipal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */