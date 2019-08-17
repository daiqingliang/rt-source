package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class X509CertInfo extends Object implements CertAttrSet<String> {
  public static final String IDENT = "x509.info";
  
  public static final String NAME = "info";
  
  public static final String DN_NAME = "dname";
  
  public static final String VERSION = "version";
  
  public static final String SERIAL_NUMBER = "serialNumber";
  
  public static final String ALGORITHM_ID = "algorithmID";
  
  public static final String ISSUER = "issuer";
  
  public static final String SUBJECT = "subject";
  
  public static final String VALIDITY = "validity";
  
  public static final String KEY = "key";
  
  public static final String ISSUER_ID = "issuerID";
  
  public static final String SUBJECT_ID = "subjectID";
  
  public static final String EXTENSIONS = "extensions";
  
  protected CertificateVersion version = new CertificateVersion();
  
  protected CertificateSerialNumber serialNum = null;
  
  protected CertificateAlgorithmId algId = null;
  
  protected X500Name issuer = null;
  
  protected X500Name subject = null;
  
  protected CertificateValidity interval = null;
  
  protected CertificateX509Key pubKey = null;
  
  protected UniqueIdentity issuerUniqueId = null;
  
  protected UniqueIdentity subjectUniqueId = null;
  
  protected CertificateExtensions extensions = null;
  
  private static final int ATTR_VERSION = 1;
  
  private static final int ATTR_SERIAL = 2;
  
  private static final int ATTR_ALGORITHM = 3;
  
  private static final int ATTR_ISSUER = 4;
  
  private static final int ATTR_VALIDITY = 5;
  
  private static final int ATTR_SUBJECT = 6;
  
  private static final int ATTR_KEY = 7;
  
  private static final int ATTR_ISSUER_ID = 8;
  
  private static final int ATTR_SUBJECT_ID = 9;
  
  private static final int ATTR_EXTENSIONS = 10;
  
  private byte[] rawCertInfo = null;
  
  private static final Map<String, Integer> map = new HashMap();
  
  public X509CertInfo() {}
  
  public X509CertInfo(byte[] paramArrayOfByte) throws CertificateParsingException {
    try {
      DerValue derValue = new DerValue(paramArrayOfByte);
      parse(derValue);
    } catch (IOException iOException) {
      throw new CertificateParsingException(iOException);
    } 
  }
  
  public X509CertInfo(DerValue paramDerValue) throws CertificateParsingException {
    try {
      parse(paramDerValue);
    } catch (IOException iOException) {
      throw new CertificateParsingException(iOException);
    } 
  }
  
  public void encode(OutputStream paramOutputStream) throws CertificateException, IOException {
    if (this.rawCertInfo == null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      emit(derOutputStream);
      this.rawCertInfo = derOutputStream.toByteArray();
    } 
    paramOutputStream.write((byte[])this.rawCertInfo.clone());
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("version");
    attributeNameEnumeration.addElement("serialNumber");
    attributeNameEnumeration.addElement("algorithmID");
    attributeNameEnumeration.addElement("issuer");
    attributeNameEnumeration.addElement("validity");
    attributeNameEnumeration.addElement("subject");
    attributeNameEnumeration.addElement("key");
    attributeNameEnumeration.addElement("issuerID");
    attributeNameEnumeration.addElement("subjectID");
    attributeNameEnumeration.addElement("extensions");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "info"; }
  
  public byte[] getEncodedInfo() throws CertificateEncodingException {
    try {
      if (this.rawCertInfo == null) {
        DerOutputStream derOutputStream = new DerOutputStream();
        emit(derOutputStream);
        this.rawCertInfo = derOutputStream.toByteArray();
      } 
      return (byte[])this.rawCertInfo.clone();
    } catch (IOException iOException) {
      throw new CertificateEncodingException(iOException.toString());
    } catch (CertificateException certificateException) {
      throw new CertificateEncodingException(certificateException.toString());
    } 
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof X509CertInfo) ? equals((X509CertInfo)paramObject) : 0; }
  
  public boolean equals(X509CertInfo paramX509CertInfo) {
    if (this == paramX509CertInfo)
      return true; 
    if (this.rawCertInfo == null || paramX509CertInfo.rawCertInfo == null)
      return false; 
    if (this.rawCertInfo.length != paramX509CertInfo.rawCertInfo.length)
      return false; 
    for (byte b = 0; b < this.rawCertInfo.length; b++) {
      if (this.rawCertInfo[b] != paramX509CertInfo.rawCertInfo[b])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    byte b1 = 0;
    for (byte b2 = 1; b2 < this.rawCertInfo.length; b2++)
      b1 += this.rawCertInfo[b2] * b2; 
    return b1;
  }
  
  public String toString() {
    if (this.subject == null || this.pubKey == null || this.interval == null || this.issuer == null || this.algId == null || this.serialNum == null)
      throw new NullPointerException("X.509 cert is incomplete"); 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("[\n");
    stringBuilder.append("  " + this.version.toString() + "\n");
    stringBuilder.append("  Subject: " + this.subject.toString() + "\n");
    stringBuilder.append("  Signature Algorithm: " + this.algId.toString() + "\n");
    stringBuilder.append("  Key:  " + this.pubKey.toString() + "\n");
    stringBuilder.append("  " + this.interval.toString() + "\n");
    stringBuilder.append("  Issuer: " + this.issuer.toString() + "\n");
    stringBuilder.append("  " + this.serialNum.toString() + "\n");
    if (this.issuerUniqueId != null)
      stringBuilder.append("  Issuer Id:\n" + this.issuerUniqueId.toString() + "\n"); 
    if (this.subjectUniqueId != null)
      stringBuilder.append("  Subject Id:\n" + this.subjectUniqueId.toString() + "\n"); 
    if (this.extensions != null) {
      Collection collection = this.extensions.getAllExtensions();
      Extension[] arrayOfExtension = (Extension[])collection.toArray(new Extension[0]);
      stringBuilder.append("\nCertificate Extensions: " + arrayOfExtension.length);
      for (byte b = 0; b < arrayOfExtension.length; b++) {
        stringBuilder.append("\n[" + (b + true) + "]: ");
        Extension extension = arrayOfExtension[b];
        try {
          if (OIDMap.getClass(extension.getExtensionId()) == null) {
            stringBuilder.append(extension.toString());
            byte[] arrayOfByte = extension.getExtensionValue();
            if (arrayOfByte != null) {
              DerOutputStream derOutputStream = new DerOutputStream();
              derOutputStream.putOctetString(arrayOfByte);
              arrayOfByte = derOutputStream.toByteArray();
              HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
              stringBuilder.append("Extension unknown: DER encoded OCTET string =\n" + hexDumpEncoder.encodeBuffer(arrayOfByte) + "\n");
            } 
          } else {
            stringBuilder.append(extension.toString());
          } 
        } catch (Exception exception) {
          stringBuilder.append(", Error parsing this extension");
        } 
      } 
      Map map1 = this.extensions.getUnparseableExtensions();
      if (!map1.isEmpty()) {
        stringBuilder.append("\nUnparseable certificate extensions: " + map1.size());
        byte b1 = 1;
        for (Extension extension : map1.values()) {
          stringBuilder.append("\n[" + b1++ + "]: ");
          stringBuilder.append(extension);
        } 
      } 
    } 
    stringBuilder.append("\n]");
    return stringBuilder.toString();
  }
  
  public void set(String paramString, Object paramObject) throws CertificateException, IOException {
    X509AttributeName x509AttributeName = new X509AttributeName(paramString);
    int i = attributeMap(x509AttributeName.getPrefix());
    if (i == 0)
      throw new CertificateException("Attribute name not recognized: " + paramString); 
    this.rawCertInfo = null;
    String str = x509AttributeName.getSuffix();
    switch (i) {
      case 1:
        if (str == null) {
          setVersion(paramObject);
          break;
        } 
        this.version.set(str, paramObject);
        break;
      case 2:
        if (str == null) {
          setSerialNumber(paramObject);
          break;
        } 
        this.serialNum.set(str, paramObject);
        break;
      case 3:
        if (str == null) {
          setAlgorithmId(paramObject);
          break;
        } 
        this.algId.set(str, paramObject);
        break;
      case 4:
        setIssuer(paramObject);
        break;
      case 5:
        if (str == null) {
          setValidity(paramObject);
          break;
        } 
        this.interval.set(str, paramObject);
        break;
      case 6:
        setSubject(paramObject);
        break;
      case 7:
        if (str == null) {
          setKey(paramObject);
          break;
        } 
        this.pubKey.set(str, paramObject);
        break;
      case 8:
        setIssuerUniqueId(paramObject);
        break;
      case 9:
        setSubjectUniqueId(paramObject);
        break;
      case 10:
        if (str == null) {
          setExtensions(paramObject);
          break;
        } 
        if (this.extensions == null)
          this.extensions = new CertificateExtensions(); 
        this.extensions.set(str, paramObject);
        break;
    } 
  }
  
  public void delete(String paramString) throws CertificateException, IOException {
    X509AttributeName x509AttributeName = new X509AttributeName(paramString);
    int i = attributeMap(x509AttributeName.getPrefix());
    if (i == 0)
      throw new CertificateException("Attribute name not recognized: " + paramString); 
    this.rawCertInfo = null;
    String str = x509AttributeName.getSuffix();
    switch (i) {
      case 1:
        if (str == null) {
          this.version = null;
          break;
        } 
        this.version.delete(str);
        break;
      case 2:
        if (str == null) {
          this.serialNum = null;
          break;
        } 
        this.serialNum.delete(str);
        break;
      case 3:
        if (str == null) {
          this.algId = null;
          break;
        } 
        this.algId.delete(str);
        break;
      case 4:
        this.issuer = null;
        break;
      case 5:
        if (str == null) {
          this.interval = null;
          break;
        } 
        this.interval.delete(str);
        break;
      case 6:
        this.subject = null;
        break;
      case 7:
        if (str == null) {
          this.pubKey = null;
          break;
        } 
        this.pubKey.delete(str);
        break;
      case 8:
        this.issuerUniqueId = null;
        break;
      case 9:
        this.subjectUniqueId = null;
        break;
      case 10:
        if (str == null) {
          this.extensions = null;
          break;
        } 
        if (this.extensions != null)
          this.extensions.delete(str); 
        break;
    } 
  }
  
  public Object get(String paramString) throws CertificateException, IOException {
    X509AttributeName x509AttributeName = new X509AttributeName(paramString);
    int i = attributeMap(x509AttributeName.getPrefix());
    if (i == 0)
      throw new CertificateParsingException("Attribute name not recognized: " + paramString); 
    String str = x509AttributeName.getSuffix();
    switch (i) {
      case 10:
        return (str == null) ? this.extensions : ((this.extensions == null) ? null : this.extensions.get(str));
      case 6:
        return (str == null) ? this.subject : getX500Name(str, false);
      case 4:
        return (str == null) ? this.issuer : getX500Name(str, true);
      case 7:
        return (str == null) ? this.pubKey : this.pubKey.get(str);
      case 3:
        return (str == null) ? this.algId : this.algId.get(str);
      case 5:
        return (str == null) ? this.interval : this.interval.get(str);
      case 1:
        return (str == null) ? this.version : this.version.get(str);
      case 2:
        return (str == null) ? this.serialNum : this.serialNum.get(str);
      case 8:
        return this.issuerUniqueId;
      case 9:
        return this.subjectUniqueId;
    } 
    return null;
  }
  
  private Object getX500Name(String paramString, boolean paramBoolean) throws IOException {
    if (paramString.equalsIgnoreCase("dname"))
      return paramBoolean ? this.issuer : this.subject; 
    if (paramString.equalsIgnoreCase("x500principal"))
      return paramBoolean ? this.issuer.asX500Principal() : this.subject.asX500Principal(); 
    throw new IOException("Attribute name not recognized.");
  }
  
  private void parse(DerValue paramDerValue) throws CertificateParsingException {
    if (paramDerValue.tag != 48)
      throw new CertificateParsingException("signed fields invalid"); 
    this.rawCertInfo = paramDerValue.toByteArray();
    DerInputStream derInputStream = paramDerValue.data;
    DerValue derValue = derInputStream.getDerValue();
    if (derValue.isContextSpecific((byte)0)) {
      this.version = new CertificateVersion(derValue);
      derValue = derInputStream.getDerValue();
    } 
    this.serialNum = new CertificateSerialNumber(derValue);
    this.algId = new CertificateAlgorithmId(derInputStream);
    this.issuer = new X500Name(derInputStream);
    if (this.issuer.isEmpty())
      throw new CertificateParsingException("Empty issuer DN not allowed in X509Certificates"); 
    this.interval = new CertificateValidity(derInputStream);
    this.subject = new X500Name(derInputStream);
    if (this.version.compare(0) == 0 && this.subject.isEmpty())
      throw new CertificateParsingException("Empty subject DN not allowed in v1 certificate"); 
    this.pubKey = new CertificateX509Key(derInputStream);
    if (derInputStream.available() != 0) {
      if (this.version.compare(0) == 0)
        throw new CertificateParsingException("no more data allowed for version 1 certificate"); 
    } else {
      return;
    } 
    derValue = derInputStream.getDerValue();
    if (derValue.isContextSpecific((byte)1)) {
      this.issuerUniqueId = new UniqueIdentity(derValue);
      if (derInputStream.available() == 0)
        return; 
      derValue = derInputStream.getDerValue();
    } 
    if (derValue.isContextSpecific((byte)2)) {
      this.subjectUniqueId = new UniqueIdentity(derValue);
      if (derInputStream.available() == 0)
        return; 
      derValue = derInputStream.getDerValue();
    } 
    if (this.version.compare(2) != 0)
      throw new CertificateParsingException("Extensions not allowed in v2 certificate"); 
    if (derValue.isConstructed() && derValue.isContextSpecific((byte)3))
      this.extensions = new CertificateExtensions(derValue.data); 
    verifyCert(this.subject, this.extensions);
  }
  
  private void verifyCert(X500Name paramX500Name, CertificateExtensions paramCertificateExtensions) throws CertificateParsingException, IOException {
    if (paramX500Name.isEmpty()) {
      if (paramCertificateExtensions == null)
        throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and certificate has no extensions"); 
      SubjectAlternativeNameExtension subjectAlternativeNameExtension = null;
      Object object = null;
      GeneralNames generalNames = null;
      try {
        subjectAlternativeNameExtension = (SubjectAlternativeNameExtension)paramCertificateExtensions.get("SubjectAlternativeName");
        generalNames = subjectAlternativeNameExtension.get("subject_name");
      } catch (IOException iOException) {
        throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and SubjectAlternativeName extension is absent");
      } 
      if (generalNames == null || generalNames.isEmpty())
        throw new CertificateParsingException("X.509 Certificate is incomplete: subject field is empty, and SubjectAlternativeName extension is empty"); 
      if (!subjectAlternativeNameExtension.isCritical())
        throw new CertificateParsingException("X.509 Certificate is incomplete: SubjectAlternativeName extension MUST be marked critical when subject field is empty"); 
    } 
  }
  
  private void emit(DerOutputStream paramDerOutputStream) throws CertificateException, IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    this.version.encode(derOutputStream);
    this.serialNum.encode(derOutputStream);
    this.algId.encode(derOutputStream);
    if (this.version.compare(0) == 0 && this.issuer.toString() == null)
      throw new CertificateParsingException("Null issuer DN not allowed in v1 certificate"); 
    this.issuer.encode(derOutputStream);
    this.interval.encode(derOutputStream);
    if (this.version.compare(0) == 0 && this.subject.toString() == null)
      throw new CertificateParsingException("Null subject DN not allowed in v1 certificate"); 
    this.subject.encode(derOutputStream);
    this.pubKey.encode(derOutputStream);
    if (this.issuerUniqueId != null)
      this.issuerUniqueId.encode(derOutputStream, DerValue.createTag(-128, false, (byte)1)); 
    if (this.subjectUniqueId != null)
      this.subjectUniqueId.encode(derOutputStream, DerValue.createTag(-128, false, (byte)2)); 
    if (this.extensions != null)
      this.extensions.encode(derOutputStream); 
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  private int attributeMap(String paramString) {
    Integer integer = (Integer)map.get(paramString);
    return (integer == null) ? 0 : integer.intValue();
  }
  
  private void setVersion(Object paramObject) throws CertificateException {
    if (!(paramObject instanceof CertificateVersion))
      throw new CertificateException("Version class type invalid."); 
    this.version = (CertificateVersion)paramObject;
  }
  
  private void setSerialNumber(Object paramObject) throws CertificateException {
    if (!(paramObject instanceof CertificateSerialNumber))
      throw new CertificateException("SerialNumber class type invalid."); 
    this.serialNum = (CertificateSerialNumber)paramObject;
  }
  
  private void setAlgorithmId(Object paramObject) throws CertificateException {
    if (!(paramObject instanceof CertificateAlgorithmId))
      throw new CertificateException("AlgorithmId class type invalid."); 
    this.algId = (CertificateAlgorithmId)paramObject;
  }
  
  private void setIssuer(Object paramObject) throws CertificateException {
    if (!(paramObject instanceof X500Name))
      throw new CertificateException("Issuer class type invalid."); 
    this.issuer = (X500Name)paramObject;
  }
  
  private void setValidity(Object paramObject) throws CertificateException {
    if (!(paramObject instanceof CertificateValidity))
      throw new CertificateException("CertificateValidity class type invalid."); 
    this.interval = (CertificateValidity)paramObject;
  }
  
  private void setSubject(Object paramObject) throws CertificateException {
    if (!(paramObject instanceof X500Name))
      throw new CertificateException("Subject class type invalid."); 
    this.subject = (X500Name)paramObject;
  }
  
  private void setKey(Object paramObject) throws CertificateException {
    if (!(paramObject instanceof CertificateX509Key))
      throw new CertificateException("Key class type invalid."); 
    this.pubKey = (CertificateX509Key)paramObject;
  }
  
  private void setIssuerUniqueId(Object paramObject) throws CertificateException {
    if (this.version.compare(1) < 0)
      throw new CertificateException("Invalid version"); 
    if (!(paramObject instanceof UniqueIdentity))
      throw new CertificateException("IssuerUniqueId class type invalid."); 
    this.issuerUniqueId = (UniqueIdentity)paramObject;
  }
  
  private void setSubjectUniqueId(Object paramObject) throws CertificateException {
    if (this.version.compare(1) < 0)
      throw new CertificateException("Invalid version"); 
    if (!(paramObject instanceof UniqueIdentity))
      throw new CertificateException("SubjectUniqueId class type invalid."); 
    this.subjectUniqueId = (UniqueIdentity)paramObject;
  }
  
  private void setExtensions(Object paramObject) throws CertificateException {
    if (this.version.compare(2) < 0)
      throw new CertificateException("Invalid version"); 
    if (!(paramObject instanceof CertificateExtensions))
      throw new CertificateException("Extensions class type invalid."); 
    this.extensions = (CertificateExtensions)paramObject;
  }
  
  static  {
    map.put("version", Integer.valueOf(1));
    map.put("serialNumber", Integer.valueOf(2));
    map.put("algorithmID", Integer.valueOf(3));
    map.put("issuer", Integer.valueOf(4));
    map.put("validity", Integer.valueOf(5));
    map.put("subject", Integer.valueOf(6));
    map.put("key", Integer.valueOf(7));
    map.put("issuerID", Integer.valueOf(8));
    map.put("subjectID", Integer.valueOf(9));
    map.put("extensions", Integer.valueOf(10));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\X509CertInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */