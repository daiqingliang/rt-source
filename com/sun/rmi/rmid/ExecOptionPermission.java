package com.sun.rmi.rmid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

public final class ExecOptionPermission extends Permission {
  private boolean wildcard;
  
  private String name;
  
  private static final long serialVersionUID = 5842294756823092756L;
  
  public ExecOptionPermission(String paramString) {
    super(paramString);
    init(paramString);
  }
  
  public ExecOptionPermission(String paramString1, String paramString2) { this(paramString1); }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof ExecOptionPermission))
      return false; 
    ExecOptionPermission execOptionPermission = (ExecOptionPermission)paramPermission;
    return this.wildcard ? (execOptionPermission.wildcard ? execOptionPermission.name.startsWith(this.name) : ((execOptionPermission.name.length() > this.name.length() && execOptionPermission.name.startsWith(this.name)) ? 1 : 0)) : (execOptionPermission.wildcard ? 0 : this.name.equals(execOptionPermission.name));
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject == null || paramObject.getClass() != getClass())
      return false; 
    ExecOptionPermission execOptionPermission = (ExecOptionPermission)paramObject;
    return getName().equals(execOptionPermission.getName());
  }
  
  public int hashCode() { return getName().hashCode(); }
  
  public String getActions() { return ""; }
  
  public PermissionCollection newPermissionCollection() { return new ExecOptionPermissionCollection(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    init(getName());
  }
  
  private void init(String paramString) {
    if (paramString == null)
      throw new NullPointerException("name can't be null"); 
    if (paramString.equals(""))
      throw new IllegalArgumentException("name can't be empty"); 
    if (paramString.endsWith(".*") || paramString.endsWith("=*") || paramString.equals("*")) {
      this.wildcard = true;
      if (paramString.length() == 1) {
        this.name = "";
      } else {
        this.name = paramString.substring(0, paramString.length() - 1);
      } 
    } else {
      this.name = paramString;
    } 
  }
  
  private static class ExecOptionPermissionCollection extends PermissionCollection implements Serializable {
    private Hashtable<String, Permission> permissions = new Hashtable(11);
    
    private boolean all_allowed = false;
    
    private static final long serialVersionUID = -1242475729790124375L;
    
    public void add(Permission param1Permission) {
      if (!(param1Permission instanceof ExecOptionPermission))
        throw new IllegalArgumentException("invalid permission: " + param1Permission); 
      if (isReadOnly())
        throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
      ExecOptionPermission execOptionPermission = (ExecOptionPermission)param1Permission;
      this.permissions.put(execOptionPermission.getName(), param1Permission);
      if (!this.all_allowed && execOptionPermission.getName().equals("*"))
        this.all_allowed = true; 
    }
    
    public boolean implies(Permission param1Permission) {
      if (!(param1Permission instanceof ExecOptionPermission))
        return false; 
      ExecOptionPermission execOptionPermission = (ExecOptionPermission)param1Permission;
      if (this.all_allowed)
        return true; 
      String str = execOptionPermission.getName();
      Permission permission = (Permission)this.permissions.get(str);
      if (permission != null)
        return permission.implies(param1Permission); 
      int i;
      int j;
      for (j = str.length() - 1; (i = str.lastIndexOf(".", j)) != -1; j = i - 1) {
        str = str.substring(0, i + 1) + "*";
        permission = (Permission)this.permissions.get(str);
        if (permission != null)
          return permission.implies(param1Permission); 
      } 
      str = execOptionPermission.getName();
      for (j = str.length() - 1; (i = str.lastIndexOf("=", j)) != -1; j = i - 1) {
        str = str.substring(0, i + 1) + "*";
        permission = (Permission)this.permissions.get(str);
        if (permission != null)
          return permission.implies(param1Permission); 
      } 
      return false;
    }
    
    public Enumeration<Permission> elements() { return this.permissions.elements(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rmi\rmid\ExecOptionPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */