package java.security;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public abstract class PermissionCollection implements Serializable {
  private static final long serialVersionUID = -6727011328946861783L;
  
  public abstract void add(Permission paramPermission);
  
  public abstract boolean implies(Permission paramPermission);
  
  public abstract Enumeration<Permission> elements();
  
  public void setReadOnly() { this.readOnly = true; }
  
  public boolean isReadOnly() { return this.readOnly; }
  
  public String toString() {
    Enumeration enumeration = elements();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(super.toString() + " (\n");
    while (enumeration.hasMoreElements()) {
      try {
        stringBuilder.append(" ");
        stringBuilder.append(((Permission)enumeration.nextElement()).toString());
        stringBuilder.append("\n");
      } catch (NoSuchElementException noSuchElementException) {}
    } 
    stringBuilder.append(")\n");
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\PermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */