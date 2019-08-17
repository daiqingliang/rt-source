package sun.security.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CRLException;
import java.security.cert.CRLReason;
import java.security.cert.Extension;
import java.security.cert.X509CRLEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class X509CRLEntryImpl extends X509CRLEntry implements Comparable<X509CRLEntryImpl> {
  private SerialNumber serialNumber = null;
  
  private Date revocationDate = null;
  
  private CRLExtensions extensions = null;
  
  private byte[] revokedCert = null;
  
  private X500Principal certIssuer;
  
  private static final boolean isExplicit = false;
  
  private static final long YR_2050 = 2524636800000L;
  
  public X509CRLEntryImpl(BigInteger paramBigInteger, Date paramDate) {
    this.serialNumber = new SerialNumber(paramBigInteger);
    this.revocationDate = paramDate;
  }
  
  public X509CRLEntryImpl(BigInteger paramBigInteger, Date paramDate, CRLExtensions paramCRLExtensions) {
    this.serialNumber = new SerialNumber(paramBigInteger);
    this.revocationDate = paramDate;
    this.extensions = paramCRLExtensions;
  }
  
  public X509CRLEntryImpl(byte[] paramArrayOfByte) throws CRLException {
    try {
      parse(new DerValue(paramArrayOfByte));
    } catch (IOException iOException) {
      this.revokedCert = null;
      throw new CRLException("Parsing error: " + iOException.toString());
    } 
  }
  
  public X509CRLEntryImpl(DerValue paramDerValue) throws CRLException {
    try {
      parse(paramDerValue);
    } catch (IOException iOException) {
      this.revokedCert = null;
      throw new CRLException("Parsing error: " + iOException.toString());
    } 
  }
  
  public boolean hasExtensions() { return (this.extensions != null); }
  
  public void encode(DerOutputStream paramDerOutputStream) throws CRLException {
    try {
      if (this.revokedCert == null) {
        DerOutputStream derOutputStream1 = new DerOutputStream();
        this.serialNumber.encode(derOutputStream1);
        if (this.revocationDate.getTime() < 2524636800000L) {
          derOutputStream1.putUTCTime(this.revocationDate);
        } else {
          derOutputStream1.putGeneralizedTime(this.revocationDate);
        } 
        if (this.extensions != null)
          this.extensions.encode(derOutputStream1, false); 
        DerOutputStream derOutputStream2 = new DerOutputStream();
        derOutputStream2.write((byte)48, derOutputStream1);
        this.revokedCert = derOutputStream2.toByteArray();
      } 
      paramDerOutputStream.write(this.revokedCert);
    } catch (IOException iOException) {
      throw new CRLException("Encoding error: " + iOException.toString());
    } 
  }
  
  public byte[] getEncoded() throws CRLException { return (byte[])getEncoded0().clone(); }
  
  private byte[] getEncoded0() throws CRLException {
    if (this.revokedCert == null)
      encode(new DerOutputStream()); 
    return this.revokedCert;
  }
  
  public X500Principal getCertificateIssuer() { return this.certIssuer; }
  
  void setCertificateIssuer(X500Principal paramX500Principal1, X500Principal paramX500Principal2) {
    if (paramX500Principal1.equals(paramX500Principal2)) {
      this.certIssuer = null;
    } else {
      this.certIssuer = paramX500Principal2;
    } 
  }
  
  public BigInteger getSerialNumber() { return this.serialNumber.getNumber(); }
  
  public Date getRevocationDate() { return new Date(this.revocationDate.getTime()); }
  
  public CRLReason getRevocationReason() {
    Extension extension = getExtension(PKIXExtensions.ReasonCode_Id);
    if (extension == null)
      return null; 
    CRLReasonCodeExtension cRLReasonCodeExtension = (CRLReasonCodeExtension)extension;
    return cRLReasonCodeExtension.getReasonCode();
  }
  
  public static CRLReason getRevocationReason(X509CRLEntry paramX509CRLEntry) {
    try {
      byte[] arrayOfByte1 = paramX509CRLEntry.getExtensionValue("2.5.29.21");
      if (arrayOfByte1 == null)
        return null; 
      DerValue derValue = new DerValue(arrayOfByte1);
      byte[] arrayOfByte2 = derValue.getOctetString();
      CRLReasonCodeExtension cRLReasonCodeExtension = new CRLReasonCodeExtension(Boolean.FALSE, arrayOfByte2);
      return cRLReasonCodeExtension.getReasonCode();
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public Integer getReasonCode() throws IOException {
    Extension extension = getExtension(PKIXExtensions.ReasonCode_Id);
    if (extension == null)
      return null; 
    CRLReasonCodeExtension cRLReasonCodeExtension = (CRLReasonCodeExtension)extension;
    return cRLReasonCodeExtension.get("reason");
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.serialNumber.toString());
    stringBuilder.append("  On: " + this.revocationDate.toString());
    if (this.certIssuer != null)
      stringBuilder.append("\n    Certificate issuer: " + this.certIssuer); 
    if (this.extensions != null) {
      Collection collection = this.extensions.getAllExtensions();
      Extension[] arrayOfExtension = (Extension[])collection.toArray(new Extension[0]);
      stringBuilder.append("\n    CRL Entry Extensions: " + arrayOfExtension.length);
      for (byte b = 0; b < arrayOfExtension.length; b++) {
        stringBuilder.append("\n    [" + (b + true) + "]: ");
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
    } 
    stringBuilder.append("\n");
    return stringBuilder.toString();
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
  
  public Extension getExtension(ObjectIdentifier paramObjectIdentifier) { return (this.extensions == null) ? null : this.extensions.get(OIDMap.getName(paramObjectIdentifier)); }
  
  private void parse(DerValue paramDerValue) throws CRLException {
    if (paramDerValue.tag != 48)
      throw new CRLException("Invalid encoded RevokedCertificate, starting sequence tag missing."); 
    if (paramDerValue.data.available() == 0)
      throw new CRLException("No data encoded for RevokedCertificates"); 
    this.revokedCert = paramDerValue.toByteArray();
    DerInputStream derInputStream = paramDerValue.toDerInputStream();
    DerValue derValue = derInputStream.getDerValue();
    this.serialNumber = new SerialNumber(derValue);
    int i = paramDerValue.data.peekByte();
    if ((byte)i == 23) {
      this.revocationDate = paramDerValue.data.getUTCTime();
    } else if ((byte)i == 24) {
      this.revocationDate = paramDerValue.data.getGeneralizedTime();
    } else {
      throw new CRLException("Invalid encoding for revocation date");
    } 
    if (paramDerValue.data.available() == 0)
      return; 
    this.extensions = new CRLExtensions(paramDerValue.toDerInputStream());
  }
  
  public static X509CRLEntryImpl toImpl(X509CRLEntry paramX509CRLEntry) throws CRLException { return (paramX509CRLEntry instanceof X509CRLEntryImpl) ? (X509CRLEntryImpl)paramX509CRLEntry : new X509CRLEntryImpl(paramX509CRLEntry.getEncoded()); }
  
  CertificateIssuerExtension getCertificateIssuerExtension() { return (CertificateIssuerExtension)getExtension(PKIXExtensions.CertificateIssuer_Id); }
  
  public Map<String, Extension> getExtensions() {
    if (this.extensions == null)
      return Collections.emptyMap(); 
    Collection collection = this.extensions.getAllExtensions();
    TreeMap treeMap = new TreeMap();
    for (Extension extension : collection)
      treeMap.put(extension.getId(), extension); 
    return treeMap;
  }
  
  public int compareTo(X509CRLEntryImpl paramX509CRLEntryImpl) {
    int i = getSerialNumber().compareTo(paramX509CRLEntryImpl.getSerialNumber());
    if (i != 0)
      return i; 
    try {
      byte[] arrayOfByte1 = getEncoded0();
      byte[] arrayOfByte2 = paramX509CRLEntryImpl.getEncoded0();
      for (byte b = 0; b < arrayOfByte1.length && b < arrayOfByte2.length; b++) {
        byte b1 = arrayOfByte1[b] & 0xFF;
        byte b2 = arrayOfByte2[b] & 0xFF;
        if (b1 != b2)
          return b1 - b2; 
      } 
      return arrayOfByte1.length - arrayOfByte2.length;
    } catch (CRLException cRLException) {
      return -1;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\X509CRLEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */