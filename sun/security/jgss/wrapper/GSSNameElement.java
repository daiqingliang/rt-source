package sun.security.jgss.wrapper;

import java.io.IOException;
import java.security.Provider;
import javax.security.auth.kerberos.ServicePermission;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.GSSExceptionImpl;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.krb5.Realm;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

public class GSSNameElement implements GSSNameSpi {
  long pName = 0L;
  
  private String printableName;
  
  private Oid printableType;
  
  private GSSLibStub cStub;
  
  static final GSSNameElement DEF_ACCEPTOR = new GSSNameElement();
  
  private static Oid getNativeNameType(Oid paramOid, GSSLibStub paramGSSLibStub) {
    if (GSSUtil.NT_GSS_KRB5_PRINCIPAL.equals(paramOid)) {
      Oid[] arrayOfOid = null;
      try {
        arrayOfOid = paramGSSLibStub.inquireNamesForMech();
      } catch (GSSException gSSException) {
        if (gSSException.getMajor() == 2 && GSSUtil.isSpNegoMech(paramGSSLibStub.getMech())) {
          try {
            paramGSSLibStub = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID);
            arrayOfOid = paramGSSLibStub.inquireNamesForMech();
          } catch (GSSException gSSException1) {
            SunNativeProvider.debug("Name type list unavailable: " + gSSException1.getMajorString());
          } 
        } else {
          SunNativeProvider.debug("Name type list unavailable: " + gSSException.getMajorString());
        } 
      } 
      if (arrayOfOid != null) {
        for (byte b = 0; b < arrayOfOid.length; b++) {
          if (arrayOfOid[b].equals(paramOid))
            return paramOid; 
        } 
        SunNativeProvider.debug("Override " + paramOid + " with mechanism default(null)");
        return null;
      } 
    } 
    return paramOid;
  }
  
  private GSSNameElement() { this.printableName = "<DEFAULT ACCEPTOR>"; }
  
  GSSNameElement(long paramLong, GSSLibStub paramGSSLibStub) throws GSSException {
    assert paramGSSLibStub != null;
    if (paramLong == 0L)
      throw new GSSException(3); 
    this.pName = paramLong;
    this.cStub = paramGSSLibStub;
    setPrintables();
  }
  
  GSSNameElement(byte[] paramArrayOfByte, Oid paramOid, GSSLibStub paramGSSLibStub) throws GSSException {
    assert paramGSSLibStub != null;
    if (paramArrayOfByte == null)
      throw new GSSException(3); 
    this.cStub = paramGSSLibStub;
    byte[] arrayOfByte = paramArrayOfByte;
    if (paramOid != null) {
      paramOid = getNativeNameType(paramOid, paramGSSLibStub);
      if (GSSName.NT_EXPORT_NAME.equals(paramOid)) {
        byte[] arrayOfByte1 = null;
        DerOutputStream derOutputStream = new DerOutputStream();
        Oid oid = this.cStub.getMech();
        try {
          derOutputStream.putOID(new ObjectIdentifier(oid.toString()));
        } catch (IOException iOException) {
          throw new GSSExceptionImpl(11, iOException);
        } 
        arrayOfByte1 = derOutputStream.toByteArray();
        arrayOfByte = new byte[4 + arrayOfByte1.length + 4 + paramArrayOfByte.length];
        int i = 0;
        arrayOfByte[i++] = 4;
        arrayOfByte[i++] = 1;
        arrayOfByte[i++] = (byte)(arrayOfByte1.length >>> 8);
        arrayOfByte[i++] = (byte)arrayOfByte1.length;
        System.arraycopy(arrayOfByte1, 0, arrayOfByte, i, arrayOfByte1.length);
        i += arrayOfByte1.length;
        arrayOfByte[i++] = (byte)(paramArrayOfByte.length >>> 24);
        arrayOfByte[i++] = (byte)(paramArrayOfByte.length >>> 16);
        arrayOfByte[i++] = (byte)(paramArrayOfByte.length >>> 8);
        arrayOfByte[i++] = (byte)paramArrayOfByte.length;
        System.arraycopy(paramArrayOfByte, 0, arrayOfByte, i, paramArrayOfByte.length);
      } 
    } 
    this.pName = this.cStub.importName(arrayOfByte, paramOid);
    setPrintables();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && !Realm.AUTODEDUCEREALM) {
      String str = getKrbName();
      int i = str.lastIndexOf('@');
      if (i != -1) {
        String str1 = str.substring(i);
        if ((paramOid != null && !paramOid.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL)) || !(new String(paramArrayOfByte)).endsWith(str1))
          try {
            securityManager.checkPermission(new ServicePermission(str1, "-"));
          } catch (SecurityException securityException) {
            throw new GSSException(11);
          }  
      } 
    } 
    SunNativeProvider.debug("Imported " + this.printableName + " w/ type " + this.printableType);
  }
  
  private void setPrintables() {
    Object[] arrayOfObject = null;
    arrayOfObject = this.cStub.displayName(this.pName);
    assert arrayOfObject != null && arrayOfObject.length == 2;
    this.printableName = (String)arrayOfObject[0];
    assert this.printableName != null;
    this.printableType = (Oid)arrayOfObject[1];
    if (this.printableType == null)
      this.printableType = GSSName.NT_USER_NAME; 
  }
  
  public String getKrbName() throws GSSException {
    long l = 0L;
    GSSLibStub gSSLibStub = this.cStub;
    if (!GSSUtil.isKerberosMech(this.cStub.getMech()))
      gSSLibStub = GSSLibStub.getInstance(GSSUtil.GSS_KRB5_MECH_OID); 
    l = gSSLibStub.canonicalizeName(this.pName);
    Object[] arrayOfObject = gSSLibStub.displayName(l);
    gSSLibStub.releaseName(l);
    SunNativeProvider.debug("Got kerberized name: " + arrayOfObject[0]);
    return (String)arrayOfObject[0];
  }
  
  public Provider getProvider() { return SunNativeProvider.INSTANCE; }
  
  public boolean equals(GSSNameSpi paramGSSNameSpi) throws GSSException { return !(paramGSSNameSpi instanceof GSSNameElement) ? false : this.cStub.compareName(this.pName, ((GSSNameElement)paramGSSNameSpi).pName); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof GSSNameElement))
      return false; 
    try {
      return equals((GSSNameElement)paramObject);
    } catch (GSSException gSSException) {
      return false;
    } 
  }
  
  public int hashCode() { return (new Long(this.pName)).hashCode(); }
  
  public byte[] export() throws GSSException {
    byte[] arrayOfByte1 = this.cStub.exportName(this.pName);
    char c1 = Character.MIN_VALUE;
    if (arrayOfByte1[c1++] != 4 || arrayOfByte1[c1++] != 1)
      throw new GSSException(3); 
    char c2 = (0xFF & arrayOfByte1[c1++]) << '\b' | 0xFF & arrayOfByte1[c1++];
    ObjectIdentifier objectIdentifier = null;
    try {
      DerInputStream derInputStream = new DerInputStream(arrayOfByte1, c1, c2);
      objectIdentifier = new ObjectIdentifier(derInputStream);
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(3, iOException);
    } 
    Oid oid = new Oid(objectIdentifier.toString());
    assert oid.equals(getMechanism());
    c1 += c2;
    char c3 = (0xFF & arrayOfByte1[c1++]) << '\030' | (0xFF & arrayOfByte1[c1++]) << '\020' | (0xFF & arrayOfByte1[c1++]) << '\b' | 0xFF & arrayOfByte1[c1++];
    if (c3 < '\000')
      throw new GSSException(3); 
    byte[] arrayOfByte2 = new byte[c3];
    System.arraycopy(arrayOfByte1, c1, arrayOfByte2, 0, c3);
    return arrayOfByte2;
  }
  
  public Oid getMechanism() { return this.cStub.getMech(); }
  
  public String toString() throws GSSException { return this.printableName; }
  
  public Oid getStringNameType() { return this.printableType; }
  
  public boolean isAnonymousName() { return GSSName.NT_ANONYMOUS.equals(this.printableType); }
  
  public void dispose() {
    if (this.pName != 0L) {
      this.cStub.releaseName(this.pName);
      this.pName = 0L;
    } 
  }
  
  protected void finalize() { dispose(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\wrapper\GSSNameElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */