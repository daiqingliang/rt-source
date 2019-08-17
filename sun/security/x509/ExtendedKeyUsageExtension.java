package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class ExtendedKeyUsageExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.ExtendedKeyUsage";
  
  public static final String NAME = "ExtendedKeyUsage";
  
  public static final String USAGES = "usages";
  
  private static final Map<ObjectIdentifier, String> map = new HashMap();
  
  private static final int[] anyExtendedKeyUsageOidData = { 2, 5, 29, 37, 0 };
  
  private static final int[] serverAuthOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 1 };
  
  private static final int[] clientAuthOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 2 };
  
  private static final int[] codeSigningOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 3 };
  
  private static final int[] emailProtectionOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 4 };
  
  private static final int[] ipsecEndSystemOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 5 };
  
  private static final int[] ipsecTunnelOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 6 };
  
  private static final int[] ipsecUserOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 7 };
  
  private static final int[] timeStampingOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 8 };
  
  private static final int[] OCSPSigningOidData = { 1, 3, 6, 1, 5, 5, 7, 3, 9 };
  
  private Vector<ObjectIdentifier> keyUsages;
  
  private void encodeThis() throws IOException {
    if (this.keyUsages == null || this.keyUsages.isEmpty()) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    for (byte b = 0; b < this.keyUsages.size(); b++)
      derOutputStream2.putOID((ObjectIdentifier)this.keyUsages.elementAt(b)); 
    derOutputStream1.write((byte)48, derOutputStream2);
    this.extensionValue = derOutputStream1.toByteArray();
  }
  
  public ExtendedKeyUsageExtension(Vector<ObjectIdentifier> paramVector) throws IOException { this(Boolean.FALSE, paramVector); }
  
  public ExtendedKeyUsageExtension(Boolean paramBoolean, Vector<ObjectIdentifier> paramVector) throws IOException {
    this.keyUsages = paramVector;
    this.extensionId = PKIXExtensions.ExtendedKeyUsage_Id;
    this.critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public ExtendedKeyUsageExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.ExtendedKeyUsage_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for ExtendedKeyUsageExtension."); 
    this.keyUsages = new Vector();
    while (derValue.data.available() != 0) {
      DerValue derValue1 = derValue.data.getDerValue();
      ObjectIdentifier objectIdentifier = derValue1.getOID();
      this.keyUsages.addElement(objectIdentifier);
    } 
  }
  
  public String toString() {
    if (this.keyUsages == null)
      return ""; 
    String str = "  ";
    boolean bool = true;
    for (ObjectIdentifier objectIdentifier : this.keyUsages) {
      if (!bool)
        str = str + "\n  "; 
      String str1 = (String)map.get(objectIdentifier);
      if (str1 != null) {
        str = str + str1;
      } else {
        str = str + objectIdentifier.toString();
      } 
      bool = false;
    } 
    return super.toString() + "ExtendedKeyUsages [\n" + str + "\n]\n";
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.ExtendedKeyUsage_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("usages")) {
      if (!(paramObject instanceof Vector))
        throw new IOException("Attribute value should be of type Vector."); 
      this.keyUsages = (Vector)paramObject;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:ExtendedKeyUsageExtension.");
    } 
    encodeThis();
  }
  
  public Vector<ObjectIdentifier> get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("usages"))
      return this.keyUsages; 
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:ExtendedKeyUsageExtension.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("usages")) {
      this.keyUsages = null;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:ExtendedKeyUsageExtension.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("usages");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "ExtendedKeyUsage"; }
  
  public List<String> getExtendedKeyUsage() {
    ArrayList arrayList = new ArrayList(this.keyUsages.size());
    for (ObjectIdentifier objectIdentifier : this.keyUsages)
      arrayList.add(objectIdentifier.toString()); 
    return arrayList;
  }
  
  static  {
    map.put(ObjectIdentifier.newInternal(anyExtendedKeyUsageOidData), "anyExtendedKeyUsage");
    map.put(ObjectIdentifier.newInternal(serverAuthOidData), "serverAuth");
    map.put(ObjectIdentifier.newInternal(clientAuthOidData), "clientAuth");
    map.put(ObjectIdentifier.newInternal(codeSigningOidData), "codeSigning");
    map.put(ObjectIdentifier.newInternal(emailProtectionOidData), "emailProtection");
    map.put(ObjectIdentifier.newInternal(ipsecEndSystemOidData), "ipsecEndSystem");
    map.put(ObjectIdentifier.newInternal(ipsecTunnelOidData), "ipsecTunnel");
    map.put(ObjectIdentifier.newInternal(ipsecUserOidData), "ipsecUser");
    map.put(ObjectIdentifier.newInternal(timeStampingOidData), "timeStamping");
    map.put(ObjectIdentifier.newInternal(OCSPSigningOidData), "OCSPSigning");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\ExtendedKeyUsageExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */