package sun.security.validator;

import java.io.IOException;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import sun.security.provider.certpath.AlgorithmChecker;
import sun.security.provider.certpath.UntrustedChecker;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.NetscapeCertTypeExtension;
import sun.security.x509.X509CertImpl;

public final class SimpleValidator extends Validator {
  static final String OID_BASIC_CONSTRAINTS = "2.5.29.19";
  
  static final String OID_NETSCAPE_CERT_TYPE = "2.16.840.1.113730.1.1";
  
  static final String OID_KEY_USAGE = "2.5.29.15";
  
  static final String OID_EXTENDED_KEY_USAGE = "2.5.29.37";
  
  static final String OID_EKU_ANY_USAGE = "2.5.29.37.0";
  
  static final ObjectIdentifier OBJID_NETSCAPE_CERT_TYPE = NetscapeCertTypeExtension.NetscapeCertType_Id;
  
  private static final String NSCT_SSL_CA = "ssl_ca";
  
  private static final String NSCT_CODE_SIGNING_CA = "object_signing_ca";
  
  private final Map<X500Principal, List<X509Certificate>> trustedX500Principals;
  
  private final Collection<X509Certificate> trustedCerts;
  
  SimpleValidator(String paramString, Collection<X509Certificate> paramCollection) {
    super("Simple", paramString);
    this.trustedCerts = paramCollection;
    this.trustedX500Principals = new HashMap();
    for (X509Certificate x509Certificate : paramCollection) {
      X500Principal x500Principal = x509Certificate.getSubjectX500Principal();
      List list = (List)this.trustedX500Principals.get(x500Principal);
      if (list == null) {
        list = new ArrayList(2);
        this.trustedX500Principals.put(x500Principal, list);
      } 
      list.add(x509Certificate);
    } 
  }
  
  public Collection<X509Certificate> getTrustedCertificates() { return this.trustedCerts; }
  
  X509Certificate[] engineValidate(X509Certificate[] paramArrayOfX509Certificate, Collection<X509Certificate> paramCollection, AlgorithmConstraints paramAlgorithmConstraints, Object paramObject) throws CertificateException {
    if (paramArrayOfX509Certificate == null || paramArrayOfX509Certificate.length == 0)
      throw new CertificateException("null or zero-length certificate chain"); 
    paramArrayOfX509Certificate = buildTrustedChain(paramArrayOfX509Certificate);
    Date date = this.validationDate;
    if (date == null)
      date = new Date(); 
    UntrustedChecker untrustedChecker = new UntrustedChecker();
    X509Certificate x509Certificate = paramArrayOfX509Certificate[paramArrayOfX509Certificate.length - 1];
    try {
      untrustedChecker.check(x509Certificate);
    } catch (CertPathValidatorException certPathValidatorException) {
      throw new ValidatorException("Untrusted certificate: " + x509Certificate.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, x509Certificate, certPathValidatorException);
    } 
    TrustAnchor trustAnchor = new TrustAnchor(x509Certificate, null);
    AlgorithmChecker algorithmChecker1 = new AlgorithmChecker(trustAnchor, this.variant);
    AlgorithmChecker algorithmChecker2 = null;
    if (paramAlgorithmConstraints != null)
      algorithmChecker2 = new AlgorithmChecker(trustAnchor, paramAlgorithmConstraints, null, null, this.variant); 
    int i = paramArrayOfX509Certificate.length - 1;
    for (int j = paramArrayOfX509Certificate.length - 2; j >= 0; j--) {
      X509Certificate x509Certificate1 = paramArrayOfX509Certificate[j + 1];
      X509Certificate x509Certificate2 = paramArrayOfX509Certificate[j];
      try {
        untrustedChecker.check(x509Certificate2, Collections.emptySet());
      } catch (CertPathValidatorException certPathValidatorException) {
        throw new ValidatorException("Untrusted certificate: " + x509Certificate2.getSubjectX500Principal(), ValidatorException.T_UNTRUSTED_CERT, x509Certificate2, certPathValidatorException);
      } 
      try {
        algorithmChecker1.check(x509Certificate2, Collections.emptySet());
        if (algorithmChecker2 != null)
          algorithmChecker2.check(x509Certificate2, Collections.emptySet()); 
      } catch (CertPathValidatorException certPathValidatorException) {
        throw new ValidatorException(ValidatorException.T_ALGORITHM_DISABLED, x509Certificate2, certPathValidatorException);
      } 
      if (!this.variant.equals("code signing") && !this.variant.equals("jce signing"))
        x509Certificate2.checkValidity(date); 
      if (!x509Certificate2.getIssuerX500Principal().equals(x509Certificate1.getSubjectX500Principal()))
        throw new ValidatorException(ValidatorException.T_NAME_CHAINING, x509Certificate2); 
      try {
        x509Certificate2.verify(x509Certificate1.getPublicKey());
      } catch (GeneralSecurityException generalSecurityException) {
        throw new ValidatorException(ValidatorException.T_SIGNATURE_ERROR, x509Certificate2, generalSecurityException);
      } 
      if (j != 0)
        i = checkExtensions(x509Certificate2, i); 
    } 
    return paramArrayOfX509Certificate;
  }
  
  private int checkExtensions(X509Certificate paramX509Certificate, int paramInt) throws CertificateException {
    Set set = paramX509Certificate.getCriticalExtensionOIDs();
    if (set == null)
      set = Collections.emptySet(); 
    int i = checkBasicConstraints(paramX509Certificate, set, paramInt);
    checkKeyUsage(paramX509Certificate, set);
    checkNetscapeCertType(paramX509Certificate, set);
    if (!set.isEmpty())
      throw new ValidatorException("Certificate contains unknown critical extensions: " + set, ValidatorException.T_CA_EXTENSIONS, paramX509Certificate); 
    return i;
  }
  
  private void checkNetscapeCertType(X509Certificate paramX509Certificate, Set<String> paramSet) throws CertificateException {
    if (!this.variant.equals("generic"))
      if (this.variant.equals("tls client") || this.variant.equals("tls server")) {
        if (!getNetscapeCertTypeBit(paramX509Certificate, "ssl_ca"))
          throw new ValidatorException("Invalid Netscape CertType extension for SSL CA certificate", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate); 
        paramSet.remove("2.16.840.1.113730.1.1");
      } else if (this.variant.equals("code signing") || this.variant.equals("jce signing")) {
        if (!getNetscapeCertTypeBit(paramX509Certificate, "object_signing_ca"))
          throw new ValidatorException("Invalid Netscape CertType extension for code signing CA certificate", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate); 
        paramSet.remove("2.16.840.1.113730.1.1");
      } else {
        throw new CertificateException("Unknown variant " + this.variant);
      }  
  }
  
  static boolean getNetscapeCertTypeBit(X509Certificate paramX509Certificate, String paramString) {
    try {
      NetscapeCertTypeExtension netscapeCertTypeExtension;
      if (paramX509Certificate instanceof X509CertImpl) {
        X509CertImpl x509CertImpl = (X509CertImpl)paramX509Certificate;
        ObjectIdentifier objectIdentifier = OBJID_NETSCAPE_CERT_TYPE;
        netscapeCertTypeExtension = (NetscapeCertTypeExtension)x509CertImpl.getExtension(objectIdentifier);
        if (netscapeCertTypeExtension == null)
          return true; 
      } else {
        byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.16.840.1.113730.1.1");
        if (arrayOfByte1 == null)
          return true; 
        DerInputStream derInputStream = new DerInputStream(arrayOfByte1);
        byte[] arrayOfByte2 = derInputStream.getOctetString();
        arrayOfByte2 = (new DerValue(arrayOfByte2)).getUnalignedBitString().toByteArray();
        netscapeCertTypeExtension = new NetscapeCertTypeExtension(arrayOfByte2);
      } 
      Boolean bool = netscapeCertTypeExtension.get(paramString);
      return bool.booleanValue();
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  private int checkBasicConstraints(X509Certificate paramX509Certificate, Set<String> paramSet, int paramInt) throws CertificateException {
    paramSet.remove("2.5.29.19");
    int i = paramX509Certificate.getBasicConstraints();
    if (i < 0)
      throw new ValidatorException("End user tried to act as a CA", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate); 
    if (!X509CertImpl.isSelfIssued(paramX509Certificate)) {
      if (paramInt <= 0)
        throw new ValidatorException("Violated path length constraints", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate); 
      paramInt--;
    } 
    if (paramInt > i)
      paramInt = i; 
    return paramInt;
  }
  
  private void checkKeyUsage(X509Certificate paramX509Certificate, Set<String> paramSet) throws CertificateException {
    paramSet.remove("2.5.29.15");
    paramSet.remove("2.5.29.37");
    boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
    if (arrayOfBoolean != null && (arrayOfBoolean.length < 6 || !arrayOfBoolean[5]))
      throw new ValidatorException("Wrong key usage: expected keyCertSign", ValidatorException.T_CA_EXTENSIONS, paramX509Certificate); 
  }
  
  private X509Certificate[] buildTrustedChain(X509Certificate[] paramArrayOfX509Certificate) throws CertificateException {
    ArrayList arrayList = new ArrayList(paramArrayOfX509Certificate.length);
    for (byte b = 0; b < paramArrayOfX509Certificate.length; b++) {
      X509Certificate x509Certificate1 = paramArrayOfX509Certificate[b];
      X509Certificate x509Certificate2 = getTrustedCertificate(x509Certificate1);
      if (x509Certificate2 != null) {
        arrayList.add(x509Certificate2);
        return (X509Certificate[])arrayList.toArray(CHAIN0);
      } 
      arrayList.add(x509Certificate1);
    } 
    X509Certificate x509Certificate = paramArrayOfX509Certificate[paramArrayOfX509Certificate.length - 1];
    X500Principal x500Principal1 = x509Certificate.getSubjectX500Principal();
    X500Principal x500Principal2 = x509Certificate.getIssuerX500Principal();
    List list = (List)this.trustedX500Principals.get(x500Principal2);
    if (list != null) {
      X509Certificate x509Certificate1 = (X509Certificate)list.iterator().next();
      arrayList.add(x509Certificate1);
      return (X509Certificate[])arrayList.toArray(CHAIN0);
    } 
    throw new ValidatorException(ValidatorException.T_NO_TRUST_ANCHOR);
  }
  
  private X509Certificate getTrustedCertificate(X509Certificate paramX509Certificate) {
    X500Principal x500Principal1 = paramX509Certificate.getSubjectX500Principal();
    List list = (List)this.trustedX500Principals.get(x500Principal1);
    if (list == null)
      return null; 
    X500Principal x500Principal2 = paramX509Certificate.getIssuerX500Principal();
    PublicKey publicKey = paramX509Certificate.getPublicKey();
    for (X509Certificate x509Certificate : list) {
      if (x509Certificate.equals(paramX509Certificate))
        return paramX509Certificate; 
      if (!x509Certificate.getIssuerX500Principal().equals(x500Principal2) || !x509Certificate.getPublicKey().equals(publicKey))
        continue; 
      return x509Certificate;
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\validator\SimpleValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */