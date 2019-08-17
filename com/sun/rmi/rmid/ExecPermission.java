package com.sun.rmi.rmid;

import java.io.FilePermission;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Vector;

public final class ExecPermission extends Permission {
  private static final long serialVersionUID = -6208470287358147919L;
  
  private FilePermission fp;
  
  public ExecPermission(String paramString) {
    super(paramString);
    init(paramString);
  }
  
  public ExecPermission(String paramString1, String paramString2) { this(paramString1); }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof ExecPermission))
      return false; 
    ExecPermission execPermission = (ExecPermission)paramPermission;
    return this.fp.implies(execPermission.fp);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof ExecPermission))
      return false; 
    ExecPermission execPermission = (ExecPermission)paramObject;
    return this.fp.equals(execPermission.fp);
  }
  
  public int hashCode() { return this.fp.hashCode(); }
  
  public String getActions() { return ""; }
  
  public PermissionCollection newPermissionCollection() { return new ExecPermissionCollection(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    init(getName());
  }
  
  private void init(String paramString) { this.fp = new FilePermission(paramString, "execute"); }
  
  private static class ExecPermissionCollection extends PermissionCollection implements Serializable {
    private Vector<Permission> permissions = new Vector();
    
    private static final long serialVersionUID = -3352558508888368273L;
    
    public void add(Permission param1Permission) {
      if (!(param1Permission instanceof ExecPermission))
        throw new IllegalArgumentException("invalid permission: " + param1Permission); 
      if (isReadOnly())
        throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
      this.permissions.addElement(param1Permission);
    }
    
    public boolean implies(Permission param1Permission) {
      if (!(param1Permission instanceof ExecPermission))
        return false; 
      Enumeration enumeration = this.permissions.elements();
      while (enumeration.hasMoreElements()) {
        ExecPermission execPermission = (ExecPermission)enumeration.nextElement();
        if (execPermission.implies(param1Permission))
          return true; 
      } 
      return false;
    }
    
    public Enumeration<Permission> elements() { return this.permissions.elements(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rmi\rmid\ExecPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */