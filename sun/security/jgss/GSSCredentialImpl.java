package sun.security.jgss;

import com.sun.security.jgss.ExtendedGSSCredential;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.spnego.SpNegoCredElement;

public class GSSCredentialImpl implements ExtendedGSSCredential {
  private GSSManagerImpl gssManager = null;
  
  private boolean destroyed = false;
  
  private Hashtable<SearchKey, GSSCredentialSpi> hashtable = null;
  
  private GSSCredentialSpi tempCred = null;
  
  GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, int paramInt) throws GSSException { this(paramGSSManagerImpl, null, 0, (Oid[])null, paramInt); }
  
  GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSName paramGSSName, int paramInt1, Oid paramOid, int paramInt2) throws GSSException {
    if (paramOid == null)
      paramOid = ProviderList.DEFAULT_MECH_OID; 
    init(paramGSSManagerImpl);
    add(paramGSSName, paramInt1, paramInt1, paramOid, paramInt2);
  }
  
  GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSName paramGSSName, int paramInt1, Oid[] paramArrayOfOid, int paramInt2) throws GSSException {
    init(paramGSSManagerImpl);
    boolean bool = false;
    if (paramArrayOfOid == null) {
      paramArrayOfOid = paramGSSManagerImpl.getMechs();
      bool = true;
    } 
    for (byte b = 0; b < paramArrayOfOid.length; b++) {
      try {
        add(paramGSSName, paramInt1, paramInt1, paramArrayOfOid[b], paramInt2);
      } catch (GSSException gSSException) {
        if (bool) {
          GSSUtil.debug("Ignore " + gSSException + " while acquring cred for " + paramArrayOfOid[b]);
        } else {
          throw gSSException;
        } 
      } 
    } 
    if (this.hashtable.size() == 0 || paramInt2 != getUsage())
      throw new GSSException(13); 
  }
  
  public GSSCredentialImpl(GSSManagerImpl paramGSSManagerImpl, GSSCredentialSpi paramGSSCredentialSpi) throws GSSException {
    init(paramGSSManagerImpl);
    byte b = 2;
    if (paramGSSCredentialSpi.isInitiatorCredential())
      if (paramGSSCredentialSpi.isAcceptorCredential()) {
        b = 0;
      } else {
        b = 1;
      }  
    SearchKey searchKey = new SearchKey(paramGSSCredentialSpi.getMechanism(), b);
    this.tempCred = paramGSSCredentialSpi;
    this.hashtable.put(searchKey, this.tempCred);
    if (!GSSUtil.isSpNegoMech(paramGSSCredentialSpi.getMechanism())) {
      searchKey = new SearchKey(GSSUtil.GSS_SPNEGO_MECH_OID, b);
      this.hashtable.put(searchKey, new SpNegoCredElement(paramGSSCredentialSpi));
    } 
  }
  
  void init(GSSManagerImpl paramGSSManagerImpl) {
    this.gssManager = paramGSSManagerImpl;
    this.hashtable = new Hashtable(paramGSSManagerImpl.getMechs().length);
  }
  
  public void dispose() throws GSSException {
    if (!this.destroyed) {
      Enumeration enumeration = this.hashtable.elements();
      while (enumeration.hasMoreElements()) {
        GSSCredentialSpi gSSCredentialSpi = (GSSCredentialSpi)enumeration.nextElement();
        gSSCredentialSpi.dispose();
      } 
      this.destroyed = true;
    } 
  }
  
  public GSSCredential impersonate(GSSName paramGSSName) throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    Oid oid = this.tempCred.getMechanism();
    GSSNameSpi gSSNameSpi = (paramGSSName == null) ? null : ((GSSNameImpl)paramGSSName).getElement(oid);
    GSSCredentialSpi gSSCredentialSpi = this.tempCred.impersonate(gSSNameSpi);
    return (gSSCredentialSpi == null) ? null : new GSSCredentialImpl(this.gssManager, gSSCredentialSpi);
  }
  
  public GSSName getName() throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    return GSSNameImpl.wrapElement(this.gssManager, this.tempCred.getName());
  }
  
  public GSSName getName(Oid paramOid) throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    SearchKey searchKey = null;
    GSSCredentialSpi gSSCredentialSpi = null;
    if (paramOid == null)
      paramOid = ProviderList.DEFAULT_MECH_OID; 
    searchKey = new SearchKey(paramOid, 1);
    gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    if (gSSCredentialSpi == null) {
      searchKey = new SearchKey(paramOid, 2);
      gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    } 
    if (gSSCredentialSpi == null) {
      searchKey = new SearchKey(paramOid, 0);
      gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    } 
    if (gSSCredentialSpi == null)
      throw new GSSExceptionImpl(2, paramOid); 
    return GSSNameImpl.wrapElement(this.gssManager, gSSCredentialSpi.getName());
  }
  
  public int getRemainingLifetime() throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    int i = 0;
    int j = 0;
    int k = 0;
    int m = Integer.MAX_VALUE;
    Enumeration enumeration = this.hashtable.keys();
    while (enumeration.hasMoreElements()) {
      SearchKey searchKey = (SearchKey)enumeration.nextElement();
      GSSCredentialSpi gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
      if (searchKey.getUsage() == 1) {
        i = gSSCredentialSpi.getInitLifetime();
      } else if (searchKey.getUsage() == 2) {
        i = gSSCredentialSpi.getAcceptLifetime();
      } else {
        j = gSSCredentialSpi.getInitLifetime();
        k = gSSCredentialSpi.getAcceptLifetime();
        i = (j < k) ? j : k;
      } 
      if (m > i)
        m = i; 
    } 
    return m;
  }
  
  public int getRemainingInitLifetime(Oid paramOid) throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    GSSCredentialSpi gSSCredentialSpi = null;
    SearchKey searchKey = null;
    boolean bool = false;
    int i = 0;
    if (paramOid == null)
      paramOid = ProviderList.DEFAULT_MECH_OID; 
    searchKey = new SearchKey(paramOid, 1);
    gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    if (gSSCredentialSpi != null) {
      bool = true;
      if (i < gSSCredentialSpi.getInitLifetime())
        i = gSSCredentialSpi.getInitLifetime(); 
    } 
    searchKey = new SearchKey(paramOid, 0);
    gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    if (gSSCredentialSpi != null) {
      bool = true;
      if (i < gSSCredentialSpi.getInitLifetime())
        i = gSSCredentialSpi.getInitLifetime(); 
    } 
    if (!bool)
      throw new GSSExceptionImpl(2, paramOid); 
    return i;
  }
  
  public int getRemainingAcceptLifetime(Oid paramOid) throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    GSSCredentialSpi gSSCredentialSpi = null;
    SearchKey searchKey = null;
    boolean bool = false;
    int i = 0;
    if (paramOid == null)
      paramOid = ProviderList.DEFAULT_MECH_OID; 
    searchKey = new SearchKey(paramOid, 2);
    gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    if (gSSCredentialSpi != null) {
      bool = true;
      if (i < gSSCredentialSpi.getAcceptLifetime())
        i = gSSCredentialSpi.getAcceptLifetime(); 
    } 
    searchKey = new SearchKey(paramOid, 0);
    gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    if (gSSCredentialSpi != null) {
      bool = true;
      if (i < gSSCredentialSpi.getAcceptLifetime())
        i = gSSCredentialSpi.getAcceptLifetime(); 
    } 
    if (!bool)
      throw new GSSExceptionImpl(2, paramOid); 
    return i;
  }
  
  public int getUsage() throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    boolean bool1 = false;
    boolean bool2 = false;
    Enumeration enumeration = this.hashtable.keys();
    while (enumeration.hasMoreElements()) {
      SearchKey searchKey = (SearchKey)enumeration.nextElement();
      if (searchKey.getUsage() == 1) {
        bool1 = true;
        continue;
      } 
      if (searchKey.getUsage() == 2) {
        bool2 = true;
        continue;
      } 
      return 0;
    } 
    return bool1 ? (bool2 ? 0 : 1) : 2;
  }
  
  public int getUsage(Oid paramOid) throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    GSSCredentialSpi gSSCredentialSpi = null;
    SearchKey searchKey = null;
    boolean bool1 = false;
    boolean bool2 = false;
    if (paramOid == null)
      paramOid = ProviderList.DEFAULT_MECH_OID; 
    searchKey = new SearchKey(paramOid, 1);
    gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    if (gSSCredentialSpi != null)
      bool1 = true; 
    searchKey = new SearchKey(paramOid, 2);
    gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    if (gSSCredentialSpi != null)
      bool2 = true; 
    searchKey = new SearchKey(paramOid, 0);
    gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
    if (gSSCredentialSpi != null) {
      bool1 = true;
      bool2 = true;
    } 
    if (bool1 && bool2)
      return 0; 
    if (bool1)
      return 1; 
    if (bool2)
      return 2; 
    throw new GSSExceptionImpl(2, paramOid);
  }
  
  public Oid[] getMechs() throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    Vector vector = new Vector(this.hashtable.size());
    Enumeration enumeration = this.hashtable.keys();
    while (enumeration.hasMoreElements()) {
      SearchKey searchKey = (SearchKey)enumeration.nextElement();
      vector.addElement(searchKey.getMech());
    } 
    return (Oid[])vector.toArray(new Oid[0]);
  }
  
  public void add(GSSName paramGSSName, int paramInt1, int paramInt2, Oid paramOid, int paramInt3) throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    if (paramOid == null)
      paramOid = ProviderList.DEFAULT_MECH_OID; 
    SearchKey searchKey = new SearchKey(paramOid, paramInt3);
    if (this.hashtable.containsKey(searchKey))
      throw new GSSExceptionImpl(17, "Duplicate element found: " + getElementStr(paramOid, paramInt3)); 
    GSSNameSpi gSSNameSpi = (paramGSSName == null) ? null : ((GSSNameImpl)paramGSSName).getElement(paramOid);
    this.tempCred = this.gssManager.getCredentialElement(gSSNameSpi, paramInt1, paramInt2, paramOid, paramInt3);
    if (this.tempCred != null)
      if (paramInt3 == 0 && (!this.tempCred.isAcceptorCredential() || !this.tempCred.isInitiatorCredential())) {
        byte b2;
        byte b1;
        if (!this.tempCred.isInitiatorCredential()) {
          b1 = 2;
          b2 = 1;
        } else {
          b1 = 1;
          b2 = 2;
        } 
        searchKey = new SearchKey(paramOid, b1);
        this.hashtable.put(searchKey, this.tempCred);
        this.tempCred = this.gssManager.getCredentialElement(gSSNameSpi, paramInt1, paramInt2, paramOid, b2);
        searchKey = new SearchKey(paramOid, b2);
        this.hashtable.put(searchKey, this.tempCred);
      } else {
        this.hashtable.put(searchKey, this.tempCred);
      }  
  }
  
  public boolean equals(Object paramObject) {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    return (this == paramObject) ? true : (!(paramObject instanceof GSSCredentialImpl) ? false : false);
  }
  
  public int hashCode() throws GSSException {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    return 1;
  }
  
  public GSSCredentialSpi getElement(Oid paramOid, boolean paramBoolean) throws GSSException {
    GSSCredentialSpi gSSCredentialSpi;
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    if (paramOid == null) {
      paramOid = ProviderList.DEFAULT_MECH_OID;
      SearchKey searchKey = new SearchKey(paramOid, paramBoolean ? 1 : 2);
      gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
      if (gSSCredentialSpi == null) {
        searchKey = new SearchKey(paramOid, 0);
        gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
        if (gSSCredentialSpi == null) {
          Object[] arrayOfObject = this.hashtable.entrySet().toArray();
          for (byte b = 0; b < arrayOfObject.length; b++) {
            gSSCredentialSpi = (GSSCredentialSpi)((Map.Entry)arrayOfObject[b]).getValue();
            if (gSSCredentialSpi.isInitiatorCredential() == paramBoolean)
              break; 
          } 
        } 
      } 
    } else {
      SearchKey searchKey;
      if (paramBoolean) {
        searchKey = new SearchKey(paramOid, 1);
      } else {
        searchKey = new SearchKey(paramOid, 2);
      } 
      gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
      if (gSSCredentialSpi == null) {
        searchKey = new SearchKey(paramOid, 0);
        gSSCredentialSpi = (GSSCredentialSpi)this.hashtable.get(searchKey);
      } 
    } 
    if (gSSCredentialSpi == null)
      throw new GSSExceptionImpl(13, "No credential found for: " + getElementStr(paramOid, paramBoolean ? 1 : 2)); 
    return gSSCredentialSpi;
  }
  
  Set<GSSCredentialSpi> getElements() {
    HashSet hashSet = new HashSet(this.hashtable.size());
    Enumeration enumeration = this.hashtable.elements();
    while (enumeration.hasMoreElements()) {
      GSSCredentialSpi gSSCredentialSpi = (GSSCredentialSpi)enumeration.nextElement();
      hashSet.add(gSSCredentialSpi);
    } 
    return hashSet;
  }
  
  private static String getElementStr(Oid paramOid, int paramInt) {
    String str = paramOid.toString();
    if (paramInt == 1) {
      str = str.concat(" usage: Initiate");
    } else if (paramInt == 2) {
      str = str.concat(" usage: Accept");
    } else {
      str = str.concat(" usage: Initiate and Accept");
    } 
    return str;
  }
  
  public String toString() {
    if (this.destroyed)
      throw new IllegalStateException("This credential is no longer valid"); 
    GSSCredentialSpi gSSCredentialSpi = null;
    StringBuffer stringBuffer = new StringBuffer("[GSSCredential: ");
    Object[] arrayOfObject = this.hashtable.entrySet().toArray();
    for (byte b = 0; b < arrayOfObject.length; b++) {
      try {
        stringBuffer.append('\n');
        gSSCredentialSpi = (GSSCredentialSpi)((Map.Entry)arrayOfObject[b]).getValue();
        stringBuffer.append(gSSCredentialSpi.getName());
        stringBuffer.append(' ');
        stringBuffer.append(gSSCredentialSpi.getMechanism());
        stringBuffer.append(gSSCredentialSpi.isInitiatorCredential() ? " Initiate" : "");
        stringBuffer.append(gSSCredentialSpi.isAcceptorCredential() ? " Accept" : "");
        stringBuffer.append(" [");
        stringBuffer.append(gSSCredentialSpi.getClass());
        stringBuffer.append(']');
      } catch (GSSException gSSException) {}
    } 
    stringBuffer.append(']');
    return stringBuffer.toString();
  }
  
  static class SearchKey {
    private Oid mechOid = null;
    
    private int usage = 0;
    
    public SearchKey(Oid param1Oid, int param1Int) {
      this.mechOid = param1Oid;
      this.usage = param1Int;
    }
    
    public Oid getMech() { return this.mechOid; }
    
    public int getUsage() throws GSSException { return this.usage; }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof SearchKey))
        return false; 
      SearchKey searchKey = (SearchKey)param1Object;
      return (this.mechOid.equals(searchKey.mechOid) && this.usage == searchKey.usage);
    }
    
    public int hashCode() throws GSSException { return this.mechOid.hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\GSSCredentialImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */