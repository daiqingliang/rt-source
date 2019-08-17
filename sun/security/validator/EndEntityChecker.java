package sun.security.validator;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class EndEntityChecker {
  private static final String OID_EXTENDED_KEY_USAGE = "2.5.29.37";
  
  private static final String OID_EKU_TLS_SERVER = "1.3.6.1.5.5.7.3.1";
  
  private static final String OID_EKU_TLS_CLIENT = "1.3.6.1.5.5.7.3.2";
  
  private static final String OID_EKU_CODE_SIGNING = "1.3.6.1.5.5.7.3.3";
  
  private static final String OID_EKU_TIME_STAMPING = "1.3.6.1.5.5.7.3.8";
  
  private static final String OID_EKU_ANY_USAGE = "2.5.29.37.0";
  
  private static final String OID_EKU_NS_SGC = "2.16.840.1.113730.4.1";
  
  private static final String OID_EKU_MS_SGC = "1.3.6.1.4.1.311.10.3.3";
  
  private static final String OID_SUBJECT_ALT_NAME = "2.5.29.17";
  
  private static final String NSCT_SSL_CLIENT = "ssl_client";
  
  private static final String NSCT_SSL_SERVER = "ssl_server";
  
  private static final String NSCT_CODE_SIGNING = "object_signing";
  
  private static final int KU_SIGNATURE = 0;
  
  private static final int KU_KEY_ENCIPHERMENT = 2;
  
  private static final int KU_KEY_AGREEMENT = 4;
  
  private static final Collection<String> KU_SERVER_SIGNATURE = Arrays.asList(new String[] { "DHE_DSS", "DHE_RSA", "ECDHE_ECDSA", "ECDHE_RSA", "RSA_EXPORT", "UNKNOWN" });
  
  private static final Collection<String> KU_SERVER_ENCRYPTION = Arrays.asList(new String[] { "RSA" });
  
  private static final Collection<String> KU_SERVER_KEY_AGREEMENT = Arrays.asList(new String[] { "DH_DSS", "DH_RSA", "ECDH_ECDSA", "ECDH_RSA" });
  
  private final String variant;
  
  private final String type;
  
  private EndEntityChecker(String paramString1, String paramString2) {
    this.type = paramString1;
    this.variant = paramString2;
  }
  
  static EndEntityChecker getInstance(String paramString1, String paramString2) { return new EndEntityChecker(paramString1, paramString2); }
  
  void check(X509Certificate paramX509Certificate, Object paramObject, boolean paramBoolean) throws CertificateException {
    if (this.variant.equals("generic"))
      return; 
    Set set = getCriticalExtensions(paramX509Certificate);
    if (this.variant.equals("tls server")) {
      checkTLSServer(paramX509Certificate, (String)paramObject, set);
    } else if (this.variant.equals("tls client")) {
      checkTLSClient(paramX509Certificate, set);
    } else if (this.variant.equals("code signing")) {
      checkCodeSigning(paramX509Certificate, set);
    } else if (this.variant.equals("jce signing")) {
      checkCodeSigning(paramX509Certificate, set);
    } else if (this.variant.equals("plugin code signing")) {
      checkCodeSigning(paramX509Certificate, set);
    } else if (this.variant.equals("tsa server")) {
      checkTSAServer(paramX509Certificate, set);
    } else {
      throw new CertificateException("Unknown variant: " + this.variant);
    } 
    if (paramBoolean)
      checkRemainingExtensions(set); 
  }
  
  private Set<String> getCriticalExtensions(X509Certificate paramX509Certificate) {
    Set set = paramX509Certificate.getCriticalExtensionOIDs();
    if (set == null)
      set = Collections.emptySet(); 
    return set;
  }
  
  private void checkRemainingExtensions(Set<String> paramSet) throws CertificateException {
    paramSet.remove("2.5.29.19");
    paramSet.remove("2.5.29.17");
    if (!paramSet.isEmpty())
      throw new CertificateException("Certificate contains unsupported critical extensions: " + paramSet); 
  }
  
  private boolean checkEKU(X509Certificate paramX509Certificate, Set<String> paramSet, String paramString) throws CertificateException {
    List list = paramX509Certificate.getExtendedKeyUsage();
    return (list == null) ? true : ((list.contains(paramString) || list.contains("2.5.29.37.0")));
  }
  
  private boolean checkKeyUsage(X509Certificate paramX509Certificate, int paramInt) throws CertificateException {
    boolean[] arrayOfBoolean = paramX509Certificate.getKeyUsage();
    return (arrayOfBoolean == null) ? true : ((arrayOfBoolean.length > paramInt && arrayOfBoolean[paramInt]));
  }
  
  private void checkTLSClient(X509Certificate paramX509Certificate, Set<String> paramSet) throws CertificateException {
    if (!checkKeyUsage(paramX509Certificate, 0))
      throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    if (!checkEKU(paramX509Certificate, paramSet, "1.3.6.1.5.5.7.3.2"))
      throw new ValidatorException("Extended key usage does not permit use for TLS client authentication", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    if (!SimpleValidator.getNetscapeCertTypeBit(paramX509Certificate, "ssl_client"))
      throw new ValidatorException("Netscape cert type does not permit use for SSL client", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    paramSet.remove("2.5.29.15");
    paramSet.remove("2.5.29.37");
    paramSet.remove("2.16.840.1.113730.1.1");
  }
  
  private void checkTLSServer(X509Certificate paramX509Certificate, String paramString, Set<String> paramSet) throws CertificateException {
    if (KU_SERVER_ENCRYPTION.contains(paramString)) {
      if (!checkKeyUsage(paramX509Certificate, 2))
        throw new ValidatorException("KeyUsage does not allow key encipherment", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    } else if (KU_SERVER_SIGNATURE.contains(paramString)) {
      if (!checkKeyUsage(paramX509Certificate, 0))
        throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    } else if (KU_SERVER_KEY_AGREEMENT.contains(paramString)) {
      if (!checkKeyUsage(paramX509Certificate, 4))
        throw new ValidatorException("KeyUsage does not allow key agreement", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    } else {
      throw new CertificateException("Unknown authType: " + paramString);
    } 
    if (!checkEKU(paramX509Certificate, paramSet, "1.3.6.1.5.5.7.3.1") && !checkEKU(paramX509Certificate, paramSet, "1.3.6.1.4.1.311.10.3.3") && !checkEKU(paramX509Certificate, paramSet, "2.16.840.1.113730.4.1"))
      throw new ValidatorException("Extended key usage does not permit use for TLS server authentication", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    if (!SimpleValidator.getNetscapeCertTypeBit(paramX509Certificate, "ssl_server"))
      throw new ValidatorException("Netscape cert type does not permit use for SSL server", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    paramSet.remove("2.5.29.15");
    paramSet.remove("2.5.29.37");
    paramSet.remove("2.16.840.1.113730.1.1");
  }
  
  private void checkCodeSigning(X509Certificate paramX509Certificate, Set<String> paramSet) throws CertificateException {
    if (!checkKeyUsage(paramX509Certificate, 0))
      throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    if (!checkEKU(paramX509Certificate, paramSet, "1.3.6.1.5.5.7.3.3"))
      throw new ValidatorException("Extended key usage does not permit use for code signing", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    if (!this.variant.equals("jce signing")) {
      if (!SimpleValidator.getNetscapeCertTypeBit(paramX509Certificate, "object_signing"))
        throw new ValidatorException("Netscape cert type does not permit use for code signing", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
      paramSet.remove("2.16.840.1.113730.1.1");
    } 
    paramSet.remove("2.5.29.15");
    paramSet.remove("2.5.29.37");
  }
  
  private void checkTSAServer(X509Certificate paramX509Certificate, Set<String> paramSet) throws CertificateException {
    if (!checkKeyUsage(paramX509Certificate, 0))
      throw new ValidatorException("KeyUsage does not allow digital signatures", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    if (paramX509Certificate.getExtendedKeyUsage() == null)
      throw new ValidatorException("Certificate does not contain an extended key usage extension required for a TSA server", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    if (!checkEKU(paramX509Certificate, paramSet, "1.3.6.1.5.5.7.3.8"))
      throw new ValidatorException("Extended key usage does not permit use for TSA server", ValidatorException.T_EE_EXTENSIONS, paramX509Certificate); 
    paramSet.remove("2.5.29.15");
    paramSet.remove("2.5.29.37");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\validator\EndEntityChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */