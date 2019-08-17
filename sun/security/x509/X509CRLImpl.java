package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.provider.X509Factory;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class X509CRLImpl extends X509CRL implements DerEncoder {
  private byte[] signedCRL = null;
  
  private byte[] signature = null;
  
  private byte[] tbsCertList = null;
  
  private AlgorithmId sigAlgId = null;
  
  private int version;
  
  private AlgorithmId infoSigAlgId;
  
  private X500Name issuer = null;
  
  private X500Principal issuerPrincipal = null;
  
  private Date thisUpdate = null;
  
  private Date nextUpdate = null;
  
  private Map<X509IssuerSerial, X509CRLEntry> revokedMap = new TreeMap();
  
  private List<X509CRLEntry> revokedList = new LinkedList();
  
  private CRLExtensions extensions = null;
  
  private static final boolean isExplicit = true;
  
  private static final long YR_2050 = 2524636800000L;
  
  private boolean readOnly = false;
  
  private PublicKey verifiedPublicKey;
  
  private String verifiedProvider;
  
  private X509CRLImpl() {}
  
  public X509CRLImpl(byte[] paramArrayOfByte) throws CRLException {
    try {
      parse(new DerValue(paramArrayOfByte));
    } catch (IOException iOException) {
      this.signedCRL = null;
      throw new CRLException("Parsing error: " + iOException.getMessage());
    } 
  }
  
  public X509CRLImpl(DerValue paramDerValue) throws CRLException {
    try {
      parse(paramDerValue);
    } catch (IOException iOException) {
      this.signedCRL = null;
      throw new CRLException("Parsing error: " + iOException.getMessage());
    } 
  }
  
  public X509CRLImpl(InputStream paramInputStream) throws CRLException {
    try {
      parse(new DerValue(paramInputStream));
    } catch (IOException iOException) {
      this.signedCRL = null;
      throw new CRLException("Parsing error: " + iOException.getMessage());
    } 
  }
  
  public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2) {
    this.issuer = paramX500Name;
    this.thisUpdate = paramDate1;
    this.nextUpdate = paramDate2;
  }
  
  public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2, X509CRLEntry[] paramArrayOfX509CRLEntry) throws CRLException {
    this.issuer = paramX500Name;
    this.thisUpdate = paramDate1;
    this.nextUpdate = paramDate2;
    if (paramArrayOfX509CRLEntry != null) {
      X500Principal x500Principal1 = getIssuerX500Principal();
      X500Principal x500Principal2 = x500Principal1;
      for (byte b = 0; b < paramArrayOfX509CRLEntry.length; b++) {
        X509CRLEntryImpl x509CRLEntryImpl = (X509CRLEntryImpl)paramArrayOfX509CRLEntry[b];
        try {
          x500Principal2 = getCertIssuer(x509CRLEntryImpl, x500Principal2);
        } catch (IOException iOException) {
          throw new CRLException(iOException);
        } 
        x509CRLEntryImpl.setCertificateIssuer(x500Principal1, x500Principal2);
        X509IssuerSerial x509IssuerSerial = new X509IssuerSerial(x500Principal2, x509CRLEntryImpl.getSerialNumber());
        this.revokedMap.put(x509IssuerSerial, x509CRLEntryImpl);
        this.revokedList.add(x509CRLEntryImpl);
        if (x509CRLEntryImpl.hasExtensions())
          this.version = 1; 
      } 
    } 
  }
  
  public X509CRLImpl(X500Name paramX500Name, Date paramDate1, Date paramDate2, X509CRLEntry[] paramArrayOfX509CRLEntry, CRLExtensions paramCRLExtensions) throws CRLException {
    this(paramX500Name, paramDate1, paramDate2, paramArrayOfX509CRLEntry);
    if (paramCRLExtensions != null) {
      this.extensions = paramCRLExtensions;
      this.version = 1;
    } 
  }
  
  public byte[] getEncodedInternal() throws CRLException {
    if (this.signedCRL == null)
      throw new CRLException("Null CRL to encode"); 
    return this.signedCRL;
  }
  
  public byte[] getEncoded() throws CRLException { return (byte[])getEncodedInternal().clone(); }
  
  public void encodeInfo(OutputStream paramOutputStream) throws CRLException {
    try {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      DerOutputStream derOutputStream2 = new DerOutputStream();
      DerOutputStream derOutputStream3 = new DerOutputStream();
      if (this.version != 0)
        derOutputStream1.putInteger(this.version); 
      this.infoSigAlgId.encode(derOutputStream1);
      if (this.version == 0 && this.issuer.toString() == null)
        throw new CRLException("Null Issuer DN not allowed in v1 CRL"); 
      this.issuer.encode(derOutputStream1);
      if (this.thisUpdate.getTime() < 2524636800000L) {
        derOutputStream1.putUTCTime(this.thisUpdate);
      } else {
        derOutputStream1.putGeneralizedTime(this.thisUpdate);
      } 
      if (this.nextUpdate != null)
        if (this.nextUpdate.getTime() < 2524636800000L) {
          derOutputStream1.putUTCTime(this.nextUpdate);
        } else {
          derOutputStream1.putGeneralizedTime(this.nextUpdate);
        }  
      if (!this.revokedList.isEmpty()) {
        for (X509CRLEntry x509CRLEntry : this.revokedList)
          ((X509CRLEntryImpl)x509CRLEntry).encode(derOutputStream2); 
        derOutputStream1.write((byte)48, derOutputStream2);
      } 
      if (this.extensions != null)
        this.extensions.encode(derOutputStream1, true); 
      derOutputStream3.write((byte)48, derOutputStream1);
      this.tbsCertList = derOutputStream3.toByteArray();
      paramOutputStream.write(this.tbsCertList);
    } catch (IOException iOException) {
      throw new CRLException("Encoding error: " + iOException.getMessage());
    } 
  }
  
  public void verify(PublicKey paramPublicKey) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException { verify(paramPublicKey, ""); }
  
  public void verify(PublicKey paramPublicKey, String paramString) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    if (paramString == null)
      paramString = ""; 
    if (this.verifiedPublicKey != null && this.verifiedPublicKey.equals(paramPublicKey) && paramString.equals(this.verifiedProvider))
      return; 
    if (this.signedCRL == null)
      throw new CRLException("Uninitialized CRL"); 
    Signature signature1 = null;
    if (paramString.length() == 0) {
      signature1 = Signature.getInstance(this.sigAlgId.getName());
    } else {
      signature1 = Signature.getInstance(this.sigAlgId.getName(), paramString);
    } 
    signature1.initVerify(paramPublicKey);
    if (this.tbsCertList == null)
      throw new CRLException("Uninitialized CRL"); 
    signature1.update(this.tbsCertList, 0, this.tbsCertList.length);
    if (!signature1.verify(this.signature))
      throw new SignatureException("Signature does not match."); 
    this.verifiedPublicKey = paramPublicKey;
    this.verifiedProvider = paramString;
  }
  
  public void verify(PublicKey paramPublicKey, Provider paramProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    if (this.signedCRL == null)
      throw new CRLException("Uninitialized CRL"); 
    Signature signature1 = null;
    if (paramProvider == null) {
      signature1 = Signature.getInstance(this.sigAlgId.getName());
    } else {
      signature1 = Signature.getInstance(this.sigAlgId.getName(), paramProvider);
    } 
    signature1.initVerify(paramPublicKey);
    if (this.tbsCertList == null)
      throw new CRLException("Uninitialized CRL"); 
    signature1.update(this.tbsCertList, 0, this.tbsCertList.length);
    if (!signature1.verify(this.signature))
      throw new SignatureException("Signature does not match."); 
    this.verifiedPublicKey = paramPublicKey;
  }
  
  public static void verify(X509CRL paramX509CRL, PublicKey paramPublicKey, Provider paramProvider) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, SignatureException { paramX509CRL.verify(paramPublicKey, paramProvider); }
  
  public void sign(PrivateKey paramPrivateKey, String paramString) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException { sign(paramPrivateKey, paramString, null); }
  
  public void sign(PrivateKey paramPrivateKey, String paramString1, String paramString2) throws CRLException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    try {
      if (this.readOnly)
        throw new CRLException("cannot over-write existing CRL"); 
      Signature signature1 = null;
      if (paramString2 == null || paramString2.length() == 0) {
        signature1 = Signature.getInstance(paramString1);
      } else {
        signature1 = Signature.getInstance(paramString1, paramString2);
      } 
      signature1.initSign(paramPrivateKey);
      this.sigAlgId = AlgorithmId.get(signature1.getAlgorithm());
      this.infoSigAlgId = this.sigAlgId;
      DerOutputStream derOutputStream1 = new DerOutputStream();
      DerOutputStream derOutputStream2 = new DerOutputStream();
      encodeInfo(derOutputStream2);
      this.sigAlgId.encode(derOutputStream2);
      signature1.update(this.tbsCertList, 0, this.tbsCertList.length);
      this.signature = signature1.sign();
      derOutputStream2.putBitString(this.signature);
      derOutputStream1.write((byte)48, derOutputStream2);
      this.signedCRL = derOutputStream1.toByteArray();
      this.readOnly = true;
    } catch (IOException iOException) {
      throw new CRLException("Error while encoding data: " + iOException.getMessage());
    } 
  }
  
  public String toString() { return toStringWithAlgName("" + this.sigAlgId); }
  
  public String toStringWithAlgName(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("X.509 CRL v" + (this.version + 1) + "\n");
    if (this.sigAlgId != null)
      stringBuffer.append("Signature Algorithm: " + paramString.toString() + ", OID=" + this.sigAlgId.getOID().toString() + "\n"); 
    if (this.issuer != null)
      stringBuffer.append("Issuer: " + this.issuer.toString() + "\n"); 
    if (this.thisUpdate != null)
      stringBuffer.append("\nThis Update: " + this.thisUpdate.toString() + "\n"); 
    if (this.nextUpdate != null)
      stringBuffer.append("Next Update: " + this.nextUpdate.toString() + "\n"); 
    if (this.revokedList.isEmpty()) {
      stringBuffer.append("\nNO certificates have been revoked\n");
    } else {
      stringBuffer.append("\nRevoked Certificates: " + this.revokedList.size());
      byte b = 1;
      for (X509CRLEntry x509CRLEntry : this.revokedList)
        stringBuffer.append("\n[" + b++ + "] " + x509CRLEntry.toString()); 
    } 
    if (this.extensions != null) {
      Collection collection = this.extensions.getAllExtensions();
      Object[] arrayOfObject = collection.toArray();
      stringBuffer.append("\nCRL Extensions: " + arrayOfObject.length);
      for (byte b = 0; b < arrayOfObject.length; b++) {
        stringBuffer.append("\n[" + (b + true) + "]: ");
        Extension extension = (Extension)arrayOfObject[b];
        try {
          if (OIDMap.getClass(extension.getExtensionId()) == null) {
            stringBuffer.append(extension.toString());
            byte[] arrayOfByte = extension.getExtensionValue();
            if (arrayOfByte != null) {
              DerOutputStream derOutputStream = new DerOutputStream();
              derOutputStream.putOctetString(arrayOfByte);
              arrayOfByte = derOutputStream.toByteArray();
              HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
              stringBuffer.append("Extension unknown: DER encoded OCTET string =\n" + hexDumpEncoder.encodeBuffer(arrayOfByte) + "\n");
            } 
          } else {
            stringBuffer.append(extension.toString());
          } 
        } catch (Exception exception) {
          stringBuffer.append(", Error parsing this extension");
        } 
      } 
    } 
    if (this.signature != null) {
      HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
      stringBuffer.append("\nSignature:\n" + hexDumpEncoder.encodeBuffer(this.signature) + "\n");
    } else {
      stringBuffer.append("NOT signed yet\n");
    } 
    return stringBuffer.toString();
  }
  
  public boolean isRevoked(Certificate paramCertificate) {
    if (this.revokedMap.isEmpty() || !(paramCertificate instanceof X509Certificate))
      return false; 
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    X509IssuerSerial x509IssuerSerial = new X509IssuerSerial(x509Certificate);
    return this.revokedMap.containsKey(x509IssuerSerial);
  }
  
  public int getVersion() { return this.version + 1; }
  
  public Principal getIssuerDN() { return this.issuer; }
  
  public X500Principal getIssuerX500Principal() {
    if (this.issuerPrincipal == null)
      this.issuerPrincipal = this.issuer.asX500Principal(); 
    return this.issuerPrincipal;
  }
  
  public Date getThisUpdate() { return new Date(this.thisUpdate.getTime()); }
  
  public Date getNextUpdate() { return (this.nextUpdate == null) ? null : new Date(this.nextUpdate.getTime()); }
  
  public X509CRLEntry getRevokedCertificate(BigInteger paramBigInteger) {
    if (this.revokedMap.isEmpty())
      return null; 
    X509IssuerSerial x509IssuerSerial = new X509IssuerSerial(getIssuerX500Principal(), paramBigInteger);
    return (X509CRLEntry)this.revokedMap.get(x509IssuerSerial);
  }
  
  public X509CRLEntry getRevokedCertificate(X509Certificate paramX509Certificate) {
    if (this.revokedMap.isEmpty())
      return null; 
    X509IssuerSerial x509IssuerSerial = new X509IssuerSerial(paramX509Certificate);
    return (X509CRLEntry)this.revokedMap.get(x509IssuerSerial);
  }
  
  public Set<X509CRLEntry> getRevokedCertificates() { return this.revokedList.isEmpty() ? null : new TreeSet(this.revokedList); }
  
  public byte[] getTBSCertList() throws CRLException {
    if (this.tbsCertList == null)
      throw new CRLException("Uninitialized CRL"); 
    return (byte[])this.tbsCertList.clone();
  }
  
  public byte[] getSignature() throws CRLException { return (this.signature == null) ? null : (byte[])this.signature.clone(); }
  
  public String getSigAlgName() { return (this.sigAlgId == null) ? null : this.sigAlgId.getName(); }
  
  public String getSigAlgOID() {
    if (this.sigAlgId == null)
      return null; 
    ObjectIdentifier objectIdentifier = this.sigAlgId.getOID();
    return objectIdentifier.toString();
  }
  
  public byte[] getSigAlgParams() throws CRLException {
    if (this.sigAlgId == null)
      return null; 
    try {
      return this.sigAlgId.getEncodedParams();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public AlgorithmId getSigAlgId() { return this.sigAlgId; }
  
  public KeyIdentifier getAuthKeyId() throws IOException {
    AuthorityKeyIdentifierExtension authorityKeyIdentifierExtension = getAuthKeyIdExtension();
    return (authorityKeyIdentifierExtension != null) ? (KeyIdentifier)authorityKeyIdentifierExtension.get("key_id") : null;
  }
  
  public AuthorityKeyIdentifierExtension getAuthKeyIdExtension() throws IOException {
    Object object = getExtension(PKIXExtensions.AuthorityKey_Id);
    return (AuthorityKeyIdentifierExtension)object;
  }
  
  public CRLNumberExtension getCRLNumberExtension() throws IOException {
    Object object = getExtension(PKIXExtensions.CRLNumber_Id);
    return (CRLNumberExtension)object;
  }
  
  public BigInteger getCRLNumber() throws IOException {
    CRLNumberExtension cRLNumberExtension = getCRLNumberExtension();
    return (cRLNumberExtension != null) ? cRLNumberExtension.get("value") : null;
  }
  
  public DeltaCRLIndicatorExtension getDeltaCRLIndicatorExtension() throws IOException {
    Object object = getExtension(PKIXExtensions.DeltaCRLIndicator_Id);
    return (DeltaCRLIndicatorExtension)object;
  }
  
  public BigInteger getBaseCRLNumber() throws IOException {
    DeltaCRLIndicatorExtension deltaCRLIndicatorExtension = getDeltaCRLIndicatorExtension();
    return (deltaCRLIndicatorExtension != null) ? deltaCRLIndicatorExtension.get("value") : null;
  }
  
  public IssuerAlternativeNameExtension getIssuerAltNameExtension() throws IOException {
    Object object = getExtension(PKIXExtensions.IssuerAlternativeName_Id);
    return (IssuerAlternativeNameExtension)object;
  }
  
  public IssuingDistributionPointExtension getIssuingDistributionPointExtension() throws IOException {
    Object object = getExtension(PKIXExtensions.IssuingDistributionPoint_Id);
    return (IssuingDistributionPointExtension)object;
  }
  
  public boolean hasUnsupportedCriticalExtension() { return (this.extensions == null) ? false : this.extensions.hasUnsupportedCriticalExtension(); }
  
  public Set<String> getCriticalExtensionOIDs() {
    if (this.extensions == null)
      return null; 
    TreeSet treeSet = new TreeSet();
    for (Extension extension : this.extensions.getAllExtensions()) {
      if (extension.isCritical())
        treeSet.add(extension.getExtensionId().toString()); 
    } 
    return treeSet;
  }
  
  public Set<String> getNonCriticalExtensionOIDs() {
    if (this.extensions == null)
      return null; 
    TreeSet treeSet = new TreeSet();
    for (Extension extension : this.extensions.getAllExtensions()) {
      if (!extension.isCritical())
        treeSet.add(extension.getExtensionId().toString()); 
    } 
    return treeSet;
  }
  
  public byte[] getExtensionValue(String paramString) {
    if (this.extensions == null)
      return null; 
    try {
      String str = OIDMap.getName(new ObjectIdentifier(paramString));
      Extension extension = null;
      if (str == null) {
        ObjectIdentifier objectIdentifier = new ObjectIdentifier(paramString);
        Extension extension1 = null;
        Enumeration enumeration = this.extensions.getElements();
        while (enumeration.hasMoreElements()) {
          extension1 = (Extension)enumeration.nextElement();
          ObjectIdentifier objectIdentifier1 = extension1.getExtensionId();
          if (objectIdentifier1.equals(objectIdentifier)) {
            extension = extension1;
            break;
          } 
        } 
      } else {
        extension = this.extensions.get(str);
      } 
      if (extension == null)
        return null; 
      byte[] arrayOfByte = extension.getExtensionValue();
      if (arrayOfByte == null)
        return null; 
      DerOutputStream derOutputStream = new DerOutputStream();
      derOutputStream.putOctetString(arrayOfByte);
      return derOutputStream.toByteArray();
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Object getExtension(ObjectIdentifier paramObjectIdentifier) { return (this.extensions == null) ? null : this.extensions.get(OIDMap.getName(paramObjectIdentifier)); }
  
  private void parse(DerValue paramDerValue) throws CRLException {
    if (this.readOnly)
      throw new CRLException("cannot over-write existing CRL"); 
    if (paramDerValue.getData() == null || paramDerValue.tag != 48)
      throw new CRLException("Invalid DER-encoded CRL data"); 
    this.signedCRL = paramDerValue.toByteArray();
    DerValue[] arrayOfDerValue = new DerValue[3];
    arrayOfDerValue[0] = paramDerValue.data.getDerValue();
    arrayOfDerValue[1] = paramDerValue.data.getDerValue();
    arrayOfDerValue[2] = paramDerValue.data.getDerValue();
    if (paramDerValue.data.available() != 0)
      throw new CRLException("signed overrun, bytes = " + paramDerValue.data.available()); 
    if ((arrayOfDerValue[0]).tag != 48)
      throw new CRLException("signed CRL fields invalid"); 
    this.sigAlgId = AlgorithmId.parse(arrayOfDerValue[1]);
    this.signature = arrayOfDerValue[2].getBitString();
    if ((arrayOfDerValue[1]).data.available() != 0)
      throw new CRLException("AlgorithmId field overrun"); 
    if ((arrayOfDerValue[2]).data.available() != 0)
      throw new CRLException("Signature field overrun"); 
    this.tbsCertList = arrayOfDerValue[0].toByteArray();
    DerInputStream derInputStream = (arrayOfDerValue[0]).data;
    this.version = 0;
    byte b = (byte)derInputStream.peekByte();
    if (b == 2) {
      this.version = derInputStream.getInteger();
      if (this.version != 1)
        throw new CRLException("Invalid version"); 
    } 
    DerValue derValue = derInputStream.getDerValue();
    AlgorithmId algorithmId = AlgorithmId.parse(derValue);
    if (!algorithmId.equals(this.sigAlgId))
      throw new CRLException("Signature algorithm mismatch"); 
    this.infoSigAlgId = algorithmId;
    this.issuer = new X500Name(derInputStream);
    if (this.issuer.isEmpty())
      throw new CRLException("Empty issuer DN not allowed in X509CRLs"); 
    b = (byte)derInputStream.peekByte();
    if (b == 23) {
      this.thisUpdate = derInputStream.getUTCTime();
    } else if (b == 24) {
      this.thisUpdate = derInputStream.getGeneralizedTime();
    } else {
      throw new CRLException("Invalid encoding for thisUpdate (tag=" + b + ")");
    } 
    if (derInputStream.available() == 0)
      return; 
    b = (byte)derInputStream.peekByte();
    if (b == 23) {
      this.nextUpdate = derInputStream.getUTCTime();
    } else if (b == 24) {
      this.nextUpdate = derInputStream.getGeneralizedTime();
    } 
    if (derInputStream.available() == 0)
      return; 
    b = (byte)derInputStream.peekByte();
    if (b == 48 && (b & 0xC0) != 128) {
      DerValue[] arrayOfDerValue1 = derInputStream.getSequence(4);
      X500Principal x500Principal1 = getIssuerX500Principal();
      X500Principal x500Principal2 = x500Principal1;
      for (byte b1 = 0; b1 < arrayOfDerValue1.length; b1++) {
        X509CRLEntryImpl x509CRLEntryImpl = new X509CRLEntryImpl(arrayOfDerValue1[b1]);
        x500Principal2 = getCertIssuer(x509CRLEntryImpl, x500Principal2);
        x509CRLEntryImpl.setCertificateIssuer(x500Principal1, x500Principal2);
        X509IssuerSerial x509IssuerSerial = new X509IssuerSerial(x500Principal2, x509CRLEntryImpl.getSerialNumber());
        this.revokedMap.put(x509IssuerSerial, x509CRLEntryImpl);
        this.revokedList.add(x509CRLEntryImpl);
      } 
    } 
    if (derInputStream.available() == 0)
      return; 
    derValue = derInputStream.getDerValue();
    if (derValue.isConstructed() && derValue.isContextSpecific((byte)0))
      this.extensions = new CRLExtensions(derValue.data); 
    this.readOnly = true;
  }
  
  public static X500Principal getIssuerX500Principal(X509CRL paramX509CRL) {
    try {
      byte[] arrayOfByte1 = paramX509CRL.getEncoded();
      DerInputStream derInputStream1 = new DerInputStream(arrayOfByte1);
      DerValue derValue1 = derInputStream1.getSequence(3)[0];
      DerInputStream derInputStream2 = derValue1.data;
      byte b = (byte)derInputStream2.peekByte();
      if (b == 2)
        DerValue derValue = derInputStream2.getDerValue(); 
      DerValue derValue2 = derInputStream2.getDerValue();
      derValue2 = derInputStream2.getDerValue();
      byte[] arrayOfByte2 = derValue2.toByteArray();
      return new X500Principal(arrayOfByte2);
    } catch (Exception exception) {
      throw new RuntimeException("Could not parse issuer", exception);
    } 
  }
  
  public static byte[] getEncodedInternal(X509CRL paramX509CRL) throws CRLException { return (paramX509CRL instanceof X509CRLImpl) ? ((X509CRLImpl)paramX509CRL).getEncodedInternal() : paramX509CRL.getEncoded(); }
  
  public static X509CRLImpl toImpl(X509CRL paramX509CRL) throws CRLException { return (paramX509CRL instanceof X509CRLImpl) ? (X509CRLImpl)paramX509CRL : X509Factory.intern(paramX509CRL); }
  
  private X500Principal getCertIssuer(X509CRLEntryImpl paramX509CRLEntryImpl, X500Principal paramX500Principal) throws IOException {
    CertificateIssuerExtension certificateIssuerExtension = paramX509CRLEntryImpl.getCertificateIssuerExtension();
    if (certificateIssuerExtension != null) {
      GeneralNames generalNames = certificateIssuerExtension.get("issuer");
      X500Name x500Name = (X500Name)generalNames.get(0).getName();
      return x500Name.asX500Principal();
    } 
    return paramX500Principal;
  }
  
  public void derEncode(OutputStream paramOutputStream) throws CRLException {
    if (this.signedCRL == null)
      throw new IOException("Null CRL to encode"); 
    paramOutputStream.write((byte[])this.signedCRL.clone());
  }
  
  private static final class X509IssuerSerial extends Object implements Comparable<X509IssuerSerial> {
    final X500Principal issuer;
    
    final BigInteger serial;
    
    X509IssuerSerial(X500Principal param1X500Principal, BigInteger param1BigInteger) {
      this.issuer = param1X500Principal;
      this.serial = param1BigInteger;
    }
    
    X509IssuerSerial(X509Certificate param1X509Certificate) { this(param1X509Certificate.getIssuerX500Principal(), param1X509Certificate.getSerialNumber()); }
    
    X500Principal getIssuer() { return this.issuer; }
    
    BigInteger getSerial() throws IOException { return this.serial; }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof X509IssuerSerial))
        return false; 
      X509IssuerSerial x509IssuerSerial = (X509IssuerSerial)param1Object;
      return (this.serial.equals(x509IssuerSerial.getSerial()) && this.issuer.equals(x509IssuerSerial.getIssuer()));
    }
    
    public int hashCode() {
      if (this.hashcode == 0) {
        int i = 17;
        i = 37 * i + this.issuer.hashCode();
        i = 37 * i + this.serial.hashCode();
        this.hashcode = i;
      } 
      return this.hashcode;
    }
    
    public int compareTo(X509IssuerSerial param1X509IssuerSerial) {
      int i = this.issuer.toString().compareTo(param1X509IssuerSerial.issuer.toString());
      return (i != 0) ? i : this.serial.compareTo(param1X509IssuerSerial.serial);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\X509CRLImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */