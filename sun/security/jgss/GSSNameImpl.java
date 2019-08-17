package sun.security.jgss;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;

public class GSSNameImpl implements GSSName {
  static final Oid oldHostbasedServiceName;
  
  private GSSManagerImpl gssManager = null;
  
  private String appNameStr = null;
  
  private byte[] appNameBytes = null;
  
  private Oid appNameType = null;
  
  private String printableName = null;
  
  private Oid printableNameType = null;
  
  private HashMap<Oid, GSSNameSpi> elements = null;
  
  private GSSNameSpi mechElement = null;
  
  static GSSNameImpl wrapElement(GSSManagerImpl paramGSSManagerImpl, GSSNameSpi paramGSSNameSpi) throws GSSException { return (paramGSSNameSpi == null) ? null : new GSSNameImpl(paramGSSManagerImpl, paramGSSNameSpi); }
  
  GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, GSSNameSpi paramGSSNameSpi) {
    this.gssManager = paramGSSManagerImpl;
    this.appNameStr = this.printableName = paramGSSNameSpi.toString();
    this.appNameType = this.printableNameType = paramGSSNameSpi.getStringNameType();
    this.mechElement = paramGSSNameSpi;
    this.elements = new HashMap(1);
    this.elements.put(paramGSSNameSpi.getMechanism(), this.mechElement);
  }
  
  GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid) throws GSSException { this(paramGSSManagerImpl, paramObject, paramOid, null); }
  
  GSSNameImpl(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid1, Oid paramOid2) throws GSSException {
    if (oldHostbasedServiceName.equals(paramOid1))
      paramOid1 = GSSName.NT_HOSTBASED_SERVICE; 
    if (paramObject == null)
      throw new GSSExceptionImpl(3, "Cannot import null name"); 
    if (paramOid2 == null)
      paramOid2 = ProviderList.DEFAULT_MECH_OID; 
    if (NT_EXPORT_NAME.equals(paramOid1)) {
      importName(paramGSSManagerImpl, paramObject);
    } else {
      init(paramGSSManagerImpl, paramObject, paramOid1, paramOid2);
    } 
  }
  
  private void init(GSSManagerImpl paramGSSManagerImpl, Object paramObject, Oid paramOid1, Oid paramOid2) throws GSSException {
    this.gssManager = paramGSSManagerImpl;
    this.elements = new HashMap(paramGSSManagerImpl.getMechs().length);
    if (paramObject instanceof String) {
      this.appNameStr = (String)paramObject;
      if (paramOid1 != null) {
        this.printableName = this.appNameStr;
        this.printableNameType = paramOid1;
      } 
    } else {
      this.appNameBytes = (byte[])paramObject;
    } 
    this.appNameType = paramOid1;
    this.mechElement = getElement(paramOid2);
    if (this.printableName == null) {
      this.printableName = this.mechElement.toString();
      this.printableNameType = this.mechElement.getStringNameType();
    } 
  }
  
  private void importName(GSSManagerImpl paramGSSManagerImpl, Object paramObject) throws GSSException {
    char c1 = Character.MIN_VALUE;
    byte[] arrayOfByte1 = null;
    if (paramObject instanceof String) {
      try {
        arrayOfByte1 = ((String)paramObject).getBytes("UTF-8");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {}
    } else {
      arrayOfByte1 = (byte[])paramObject;
    } 
    if (arrayOfByte1[c1++] != 4 || arrayOfByte1[c1++] != 1)
      throw new GSSExceptionImpl(3, "Exported name token id is corrupted!"); 
    char c2 = (0xFF & arrayOfByte1[c1++]) << '\b' | 0xFF & arrayOfByte1[c1++];
    ObjectIdentifier objectIdentifier = null;
    try {
      DerInputStream derInputStream = new DerInputStream(arrayOfByte1, c1, c2);
      objectIdentifier = new ObjectIdentifier(derInputStream);
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(3, "Exported name Object identifier is corrupted!");
    } 
    Oid oid = new Oid(objectIdentifier.toString());
    c1 += c2;
    int i = (0xFF & arrayOfByte1[c1++]) << '\030' | (0xFF & arrayOfByte1[c1++]) << '\020' | (0xFF & arrayOfByte1[c1++]) << '\b' | 0xFF & arrayOfByte1[c1++];
    if (i < 0 || c1 > arrayOfByte1.length - i)
      throw new GSSExceptionImpl(3, "Exported name mech name is corrupted!"); 
    byte[] arrayOfByte2 = new byte[i];
    System.arraycopy(arrayOfByte1, c1, arrayOfByte2, 0, i);
    init(paramGSSManagerImpl, arrayOfByte2, NT_EXPORT_NAME, oid);
  }
  
  public GSSName canonicalize(Oid paramOid) throws GSSException {
    if (paramOid == null)
      paramOid = ProviderList.DEFAULT_MECH_OID; 
    return wrapElement(this.gssManager, getElement(paramOid));
  }
  
  public boolean equals(GSSName paramGSSName) throws GSSException {
    if (isAnonymous() || paramGSSName.isAnonymous())
      return false; 
    if (paramGSSName == this)
      return true; 
    if (!(paramGSSName instanceof GSSNameImpl))
      return equals(this.gssManager.createName(paramGSSName.toString(), paramGSSName.getStringNameType())); 
    GSSNameImpl gSSNameImpl = (GSSNameImpl)paramGSSName;
    GSSNameSpi gSSNameSpi1 = this.mechElement;
    GSSNameSpi gSSNameSpi2 = gSSNameImpl.mechElement;
    if (gSSNameSpi1 == null && gSSNameSpi2 != null) {
      gSSNameSpi1 = getElement(gSSNameSpi2.getMechanism());
    } else if (gSSNameSpi1 != null && gSSNameSpi2 == null) {
      gSSNameSpi2 = gSSNameImpl.getElement(gSSNameSpi1.getMechanism());
    } 
    if (gSSNameSpi1 != null && gSSNameSpi2 != null)
      return gSSNameSpi1.equals(gSSNameSpi2); 
    if (this.appNameType != null && gSSNameImpl.appNameType != null) {
      if (!this.appNameType.equals(gSSNameImpl.appNameType))
        return false; 
      byte[] arrayOfByte1 = null;
      byte[] arrayOfByte2 = null;
      try {
        arrayOfByte1 = (this.appNameStr != null) ? this.appNameStr.getBytes("UTF-8") : this.appNameBytes;
        arrayOfByte2 = (gSSNameImpl.appNameStr != null) ? gSSNameImpl.appNameStr.getBytes("UTF-8") : gSSNameImpl.appNameBytes;
      } catch (UnsupportedEncodingException unsupportedEncodingException) {}
      return Arrays.equals(arrayOfByte1, arrayOfByte2);
    } 
    return false;
  }
  
  public int hashCode() { return 1; }
  
  public boolean equals(Object paramObject) {
    try {
      if (paramObject instanceof GSSName)
        return equals((GSSName)paramObject); 
    } catch (GSSException gSSException) {}
    return false;
  }
  
  public byte[] export() throws GSSException {
    if (this.mechElement == null)
      this.mechElement = getElement(ProviderList.DEFAULT_MECH_OID); 
    byte[] arrayOfByte1 = this.mechElement.export();
    byte[] arrayOfByte2 = null;
    ObjectIdentifier objectIdentifier = null;
    try {
      objectIdentifier = new ObjectIdentifier(this.mechElement.getMechanism().toString());
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, "Invalid OID String ");
    } 
    DerOutputStream derOutputStream = new DerOutputStream();
    try {
      derOutputStream.putOID(objectIdentifier);
    } catch (IOException iOException) {
      throw new GSSExceptionImpl(11, "Could not ASN.1 Encode " + objectIdentifier.toString());
    } 
    arrayOfByte2 = derOutputStream.toByteArray();
    byte[] arrayOfByte3 = new byte[4 + arrayOfByte2.length + 4 + arrayOfByte1.length];
    int i = 0;
    arrayOfByte3[i++] = 4;
    arrayOfByte3[i++] = 1;
    arrayOfByte3[i++] = (byte)(arrayOfByte2.length >>> 8);
    arrayOfByte3[i++] = (byte)arrayOfByte2.length;
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, i, arrayOfByte2.length);
    i += arrayOfByte2.length;
    arrayOfByte3[i++] = (byte)(arrayOfByte1.length >>> 24);
    arrayOfByte3[i++] = (byte)(arrayOfByte1.length >>> 16);
    arrayOfByte3[i++] = (byte)(arrayOfByte1.length >>> 8);
    arrayOfByte3[i++] = (byte)arrayOfByte1.length;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i, arrayOfByte1.length);
    return arrayOfByte3;
  }
  
  public String toString() { return this.printableName; }
  
  public Oid getStringNameType() throws GSSException { return this.printableNameType; }
  
  public boolean isAnonymous() { return (this.printableNameType == null) ? false : GSSName.NT_ANONYMOUS.equals(this.printableNameType); }
  
  public boolean isMN() { return true; }
  
  public GSSNameSpi getElement(Oid paramOid) throws GSSException {
    GSSNameSpi gSSNameSpi = (GSSNameSpi)this.elements.get(paramOid);
    if (gSSNameSpi == null) {
      if (this.appNameStr != null) {
        gSSNameSpi = this.gssManager.getNameElement(this.appNameStr, this.appNameType, paramOid);
      } else {
        gSSNameSpi = this.gssManager.getNameElement(this.appNameBytes, this.appNameType, paramOid);
      } 
      this.elements.put(paramOid, gSSNameSpi);
    } 
    return gSSNameSpi;
  }
  
  Set<GSSNameSpi> getElements() { return new HashSet(this.elements.values()); }
  
  private static String getNameTypeStr(Oid paramOid) { return (paramOid == null) ? "(NT is null)" : (paramOid.equals(NT_USER_NAME) ? "NT_USER_NAME" : (paramOid.equals(NT_HOSTBASED_SERVICE) ? "NT_HOSTBASED_SERVICE" : (paramOid.equals(NT_EXPORT_NAME) ? "NT_EXPORT_NAME" : (paramOid.equals(GSSUtil.NT_GSS_KRB5_PRINCIPAL) ? "NT_GSS_KRB5_PRINCIPAL" : "Unknown")))); }
  
  static  {
    Oid oid = null;
    try {
      oid = new Oid("1.3.6.1.5.6.2");
    } catch (Exception exception) {}
    oldHostbasedServiceName = oid;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\GSSNameImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */