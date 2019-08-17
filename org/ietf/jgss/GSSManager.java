package org.ietf.jgss;

import java.security.Provider;
import sun.security.jgss.GSSManagerImpl;

public abstract class GSSManager {
  public static GSSManager getInstance() { return new GSSManagerImpl(); }
  
  public abstract Oid[] getMechs();
  
  public abstract Oid[] getNamesForMech(Oid paramOid) throws GSSException;
  
  public abstract Oid[] getMechsForName(Oid paramOid) throws GSSException;
  
  public abstract GSSName createName(String paramString, Oid paramOid) throws GSSException;
  
  public abstract GSSName createName(byte[] paramArrayOfByte, Oid paramOid) throws GSSException;
  
  public abstract GSSName createName(String paramString, Oid paramOid1, Oid paramOid2) throws GSSException;
  
  public abstract GSSName createName(byte[] paramArrayOfByte, Oid paramOid1, Oid paramOid2) throws GSSException;
  
  public abstract GSSCredential createCredential(int paramInt) throws GSSException;
  
  public abstract GSSCredential createCredential(GSSName paramGSSName, int paramInt1, Oid paramOid, int paramInt2) throws GSSException;
  
  public abstract GSSCredential createCredential(GSSName paramGSSName, int paramInt1, Oid[] paramArrayOfOid, int paramInt2) throws GSSException;
  
  public abstract GSSContext createContext(GSSName paramGSSName, Oid paramOid, GSSCredential paramGSSCredential, int paramInt) throws GSSException;
  
  public abstract GSSContext createContext(GSSCredential paramGSSCredential) throws GSSException;
  
  public abstract GSSContext createContext(byte[] paramArrayOfByte) throws GSSException;
  
  public abstract void addProviderAtFront(Provider paramProvider, Oid paramOid) throws GSSException;
  
  public abstract void addProviderAtEnd(Provider paramProvider, Oid paramOid) throws GSSException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\ietf\jgss\GSSManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */