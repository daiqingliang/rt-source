package org.ietf.jgss;

public interface GSSName {
  public static final Oid NT_HOSTBASED_SERVICE;
  
  public static final Oid NT_USER_NAME;
  
  public static final Oid NT_MACHINE_UID_NAME;
  
  public static final Oid NT_STRING_UID_NAME;
  
  public static final Oid NT_ANONYMOUS;
  
  public static final Oid NT_EXPORT_NAME = (NT_ANONYMOUS = (NT_STRING_UID_NAME = (NT_MACHINE_UID_NAME = (NT_USER_NAME = (NT_HOSTBASED_SERVICE = Oid.getInstance("1.2.840.113554.1.2.1.4")).getInstance("1.2.840.113554.1.2.1.1")).getInstance("1.2.840.113554.1.2.1.2")).getInstance("1.2.840.113554.1.2.1.3")).getInstance("1.3.6.1.5.6.3")).getInstance("1.3.6.1.5.6.4");
  
  boolean equals(GSSName paramGSSName) throws GSSException;
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  GSSName canonicalize(Oid paramOid) throws GSSException;
  
  byte[] export() throws GSSException;
  
  String toString();
  
  Oid getStringNameType() throws GSSException;
  
  boolean isAnonymous();
  
  boolean isMN();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\ietf\jgss\GSSName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */