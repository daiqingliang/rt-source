package java.security;

public final class AllPermission extends Permission {
  private static final long serialVersionUID = -2916474571451318075L;
  
  public AllPermission() { super("<all permissions>"); }
  
  public AllPermission(String paramString1, String paramString2) { this(); }
  
  public boolean implies(Permission paramPermission) { return true; }
  
  public boolean equals(Object paramObject) { return paramObject instanceof AllPermission; }
  
  public int hashCode() { return 1; }
  
  public String getActions() { return "<all actions>"; }
  
  public PermissionCollection newPermissionCollection() { return new AllPermissionCollection(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\AllPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */