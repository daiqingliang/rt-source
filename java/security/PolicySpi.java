package java.security;

public abstract class PolicySpi {
  protected abstract boolean engineImplies(ProtectionDomain paramProtectionDomain, Permission paramPermission);
  
  protected void engineRefresh() {}
  
  protected PermissionCollection engineGetPermissions(CodeSource paramCodeSource) { return Policy.UNSUPPORTED_EMPTY_COLLECTION; }
  
  protected PermissionCollection engineGetPermissions(ProtectionDomain paramProtectionDomain) { return Policy.UNSUPPORTED_EMPTY_COLLECTION; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\PolicySpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */