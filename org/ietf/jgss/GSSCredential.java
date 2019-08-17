package org.ietf.jgss;

public interface GSSCredential extends Cloneable {
  public static final int INITIATE_AND_ACCEPT = 0;
  
  public static final int INITIATE_ONLY = 1;
  
  public static final int ACCEPT_ONLY = 2;
  
  public static final int DEFAULT_LIFETIME = 0;
  
  public static final int INDEFINITE_LIFETIME = 2147483647;
  
  void dispose() throws GSSException;
  
  GSSName getName() throws GSSException;
  
  GSSName getName(Oid paramOid) throws GSSException;
  
  int getRemainingLifetime() throws GSSException;
  
  int getRemainingInitLifetime(Oid paramOid) throws GSSException;
  
  int getRemainingAcceptLifetime(Oid paramOid) throws GSSException;
  
  int getUsage() throws GSSException;
  
  int getUsage(Oid paramOid) throws GSSException;
  
  Oid[] getMechs() throws GSSException;
  
  void add(GSSName paramGSSName, int paramInt1, int paramInt2, Oid paramOid, int paramInt3) throws GSSException;
  
  boolean equals(Object paramObject);
  
  int hashCode() throws GSSException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\ietf\jgss\GSSCredential.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */