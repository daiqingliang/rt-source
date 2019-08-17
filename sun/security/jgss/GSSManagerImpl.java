package sun.security.jgss;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSContextSpi;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spi.MechanismFactory;

public class GSSManagerImpl extends GSSManager {
  private static final String USE_NATIVE_PROP = "sun.security.jgss.native";
  
  private static final Boolean USE_NATIVE = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() {
          String str = System.getProperty("os.name");
          return (str.startsWith("SunOS") || str.contains("OS X") || str.startsWith("Linux")) ? new Boolean(System.getProperty("sun.security.jgss.native")) : Boolean.FALSE;
        }
      });
  
  private ProviderList list;
  
  public GSSManagerImpl(GSSCaller paramGSSCaller, boolean paramBoolean) { this.list = new ProviderList(paramGSSCaller, paramBoolean); }
  
  public GSSManagerImpl(GSSCaller paramGSSCaller) { this.list = new ProviderList(paramGSSCaller, USE_NATIVE.booleanValue()); }
  
  public GSSManagerImpl() { this.list = new ProviderList(GSSCaller.CALLER_UNKNOWN, USE_NATIVE.booleanValue()); }
  
  public Oid[] getMechs() { return this.list.getMechs(); }
  
  public Oid[] getNamesForMech(Oid paramOid) throws GSSException {
    MechanismFactory mechanismFactory = this.list.getMechFactory(paramOid);
    return (Oid[])mechanismFactory.getNameTypes().clone();
  }
  
  public Oid[] getMechsForName(Oid paramOid) throws GSSException {
    Oid[] arrayOfOid1 = this.list.getMechs();
    Oid[] arrayOfOid2 = new Oid[arrayOfOid1.length];
    byte b1 = 0;
    if (paramOid.equals(GSSNameImpl.oldHostbasedServiceName))
      paramOid = GSSName.NT_HOSTBASED_SERVICE; 
    for (byte b2 = 0; b2 < arrayOfOid1.length; b2++) {
      Oid oid = arrayOfOid1[b2];
      try {
        Oid[] arrayOfOid = getNamesForMech(oid);
        if (paramOid.containedIn(arrayOfOid))
          arrayOfOid2[b1++] = oid; 
      } catch (GSSException gSSException) {
        GSSUtil.debug("Skip " + oid + ": error retrieving supported name types");
      } 
    } 
    if (b1 < arrayOfOid2.length) {
      Oid[] arrayOfOid = new Oid[b1];
      for (byte b = 0; b < b1; b++)
        arrayOfOid[b] = arrayOfOid2[b]; 
      arrayOfOid2 = arrayOfOid;
    } 
    return arrayOfOid2;
  }
  
  public GSSName createName(String paramString, Oid paramOid) throws GSSException { return new GSSNameImpl(this, paramString, paramOid); }
  
  public GSSName createName(byte[] paramArrayOfByte, Oid paramOid) throws GSSException { return new GSSNameImpl(this, paramArrayOfByte, paramOid); }
  
  public GSSName createName(String paramString, Oid paramOid1, Oid paramOid2) throws GSSException { return new GSSNameImpl(this, paramString, paramOid1, paramOid2); }
  
  public GSSName createName(byte[] paramArrayOfByte, Oid paramOid1, Oid paramOid2) throws GSSException { return new GSSNameImpl(this, paramArrayOfByte, paramOid1, paramOid2); }
  
  public GSSCredential createCredential(int paramInt) throws GSSException { return new GSSCredentialImpl(this, paramInt); }
  
  public GSSCredential createCredential(GSSName paramGSSName, int paramInt1, Oid paramOid, int paramInt2) throws GSSException { return new GSSCredentialImpl(this, paramGSSName, paramInt1, paramOid, paramInt2); }
  
  public GSSCredential createCredential(GSSName paramGSSName, int paramInt1, Oid[] paramArrayOfOid, int paramInt2) throws GSSException { return new GSSCredentialImpl(this, paramGSSName, paramInt1, paramArrayOfOid, paramInt2); }
  
  public GSSContext createContext(GSSName paramGSSName, Oid paramOid, GSSCredential paramGSSCredential, int paramInt) throws GSSException { return new GSSContextImpl(this, paramGSSName, paramOid, paramGSSCredential, paramInt); }
  
  public GSSContext createContext(GSSCredential paramGSSCredential) throws GSSException { return new GSSContextImpl(this, paramGSSCredential); }
  
  public GSSContext createContext(byte[] paramArrayOfByte) throws GSSException { return new GSSContextImpl(this, paramArrayOfByte); }
  
  public void addProviderAtFront(Provider paramProvider, Oid paramOid) throws GSSException { this.list.addProviderAtFront(paramProvider, paramOid); }
  
  public void addProviderAtEnd(Provider paramProvider, Oid paramOid) throws GSSException { this.list.addProviderAtEnd(paramProvider, paramOid); }
  
  public GSSCredentialSpi getCredentialElement(GSSNameSpi paramGSSNameSpi, int paramInt1, int paramInt2, Oid paramOid, int paramInt3) throws GSSException {
    MechanismFactory mechanismFactory = this.list.getMechFactory(paramOid);
    return mechanismFactory.getCredentialElement(paramGSSNameSpi, paramInt1, paramInt2, paramInt3);
  }
  
  public GSSNameSpi getNameElement(String paramString, Oid paramOid1, Oid paramOid2) throws GSSException {
    MechanismFactory mechanismFactory = this.list.getMechFactory(paramOid2);
    return mechanismFactory.getNameElement(paramString, paramOid1);
  }
  
  public GSSNameSpi getNameElement(byte[] paramArrayOfByte, Oid paramOid1, Oid paramOid2) throws GSSException {
    MechanismFactory mechanismFactory = this.list.getMechFactory(paramOid2);
    return mechanismFactory.getNameElement(paramArrayOfByte, paramOid1);
  }
  
  GSSContextSpi getMechanismContext(GSSNameSpi paramGSSNameSpi, GSSCredentialSpi paramGSSCredentialSpi, int paramInt, Oid paramOid) throws GSSException {
    Provider provider = null;
    if (paramGSSCredentialSpi != null)
      provider = paramGSSCredentialSpi.getProvider(); 
    MechanismFactory mechanismFactory = this.list.getMechFactory(paramOid, provider);
    return mechanismFactory.getMechanismContext(paramGSSNameSpi, paramGSSCredentialSpi, paramInt);
  }
  
  GSSContextSpi getMechanismContext(GSSCredentialSpi paramGSSCredentialSpi, Oid paramOid) throws GSSException {
    Provider provider = null;
    if (paramGSSCredentialSpi != null)
      provider = paramGSSCredentialSpi.getProvider(); 
    MechanismFactory mechanismFactory = this.list.getMechFactory(paramOid, provider);
    return mechanismFactory.getMechanismContext(paramGSSCredentialSpi);
  }
  
  GSSContextSpi getMechanismContext(byte[] paramArrayOfByte) throws GSSException {
    if (paramArrayOfByte == null || paramArrayOfByte.length == 0)
      throw new GSSException(12); 
    GSSContextSpi gSSContextSpi = null;
    Oid[] arrayOfOid = this.list.getMechs();
    for (byte b = 0; b < arrayOfOid.length; b++) {
      MechanismFactory mechanismFactory = this.list.getMechFactory(arrayOfOid[b]);
      if (mechanismFactory.getProvider().getName().equals("SunNativeGSS")) {
        gSSContextSpi = mechanismFactory.getMechanismContext(paramArrayOfByte);
        if (gSSContextSpi != null)
          break; 
      } 
    } 
    if (gSSContextSpi == null)
      throw new GSSException(16); 
    return gSSContextSpi;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\GSSManagerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */