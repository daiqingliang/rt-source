package sun.security.x509;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import sun.security.util.ObjectIdentifier;

public class OIDMap {
  private static final String ROOT = "x509.info.extensions";
  
  private static final String AUTH_KEY_IDENTIFIER = "x509.info.extensions.AuthorityKeyIdentifier";
  
  private static final String SUB_KEY_IDENTIFIER = "x509.info.extensions.SubjectKeyIdentifier";
  
  private static final String KEY_USAGE = "x509.info.extensions.KeyUsage";
  
  private static final String PRIVATE_KEY_USAGE = "x509.info.extensions.PrivateKeyUsage";
  
  private static final String POLICY_MAPPINGS = "x509.info.extensions.PolicyMappings";
  
  private static final String SUB_ALT_NAME = "x509.info.extensions.SubjectAlternativeName";
  
  private static final String ISSUER_ALT_NAME = "x509.info.extensions.IssuerAlternativeName";
  
  private static final String BASIC_CONSTRAINTS = "x509.info.extensions.BasicConstraints";
  
  private static final String NAME_CONSTRAINTS = "x509.info.extensions.NameConstraints";
  
  private static final String POLICY_CONSTRAINTS = "x509.info.extensions.PolicyConstraints";
  
  private static final String CRL_NUMBER = "x509.info.extensions.CRLNumber";
  
  private static final String CRL_REASON = "x509.info.extensions.CRLReasonCode";
  
  private static final String NETSCAPE_CERT = "x509.info.extensions.NetscapeCertType";
  
  private static final String CERT_POLICIES = "x509.info.extensions.CertificatePolicies";
  
  private static final String EXT_KEY_USAGE = "x509.info.extensions.ExtendedKeyUsage";
  
  private static final String INHIBIT_ANY_POLICY = "x509.info.extensions.InhibitAnyPolicy";
  
  private static final String CRL_DIST_POINTS = "x509.info.extensions.CRLDistributionPoints";
  
  private static final String CERT_ISSUER = "x509.info.extensions.CertificateIssuer";
  
  private static final String SUBJECT_INFO_ACCESS = "x509.info.extensions.SubjectInfoAccess";
  
  private static final String AUTH_INFO_ACCESS = "x509.info.extensions.AuthorityInfoAccess";
  
  private static final String ISSUING_DIST_POINT = "x509.info.extensions.IssuingDistributionPoint";
  
  private static final String DELTA_CRL_INDICATOR = "x509.info.extensions.DeltaCRLIndicator";
  
  private static final String FRESHEST_CRL = "x509.info.extensions.FreshestCRL";
  
  private static final String OCSPNOCHECK = "x509.info.extensions.OCSPNoCheck";
  
  private static final int[] NetscapeCertType_data = { 2, 16, 840, 1, 113730, 1, 1 };
  
  private static final Map<ObjectIdentifier, OIDInfo> oidMap = new HashMap();
  
  private static final Map<String, OIDInfo> nameMap = new HashMap();
  
  private static void addInternal(String paramString1, ObjectIdentifier paramObjectIdentifier, String paramString2) {
    OIDInfo oIDInfo = new OIDInfo(paramString1, paramObjectIdentifier, paramString2);
    oidMap.put(paramObjectIdentifier, oIDInfo);
    nameMap.put(paramString1, oIDInfo);
  }
  
  public static void addAttribute(String paramString1, String paramString2, Class<?> paramClass) throws CertificateException {
    ObjectIdentifier objectIdentifier;
    try {
      objectIdentifier = new ObjectIdentifier(paramString2);
    } catch (IOException iOException) {
      throw new CertificateException("Invalid Object identifier: " + paramString2);
    } 
    OIDInfo oIDInfo = new OIDInfo(paramString1, objectIdentifier, paramClass);
    if (oidMap.put(objectIdentifier, oIDInfo) != null)
      throw new CertificateException("Object identifier already exists: " + paramString2); 
    if (nameMap.put(paramString1, oIDInfo) != null)
      throw new CertificateException("Name already exists: " + paramString1); 
  }
  
  public static String getName(ObjectIdentifier paramObjectIdentifier) {
    OIDInfo oIDInfo = (OIDInfo)oidMap.get(paramObjectIdentifier);
    return (oIDInfo == null) ? null : oIDInfo.name;
  }
  
  public static ObjectIdentifier getOID(String paramString) {
    OIDInfo oIDInfo = (OIDInfo)nameMap.get(paramString);
    return (oIDInfo == null) ? null : oIDInfo.oid;
  }
  
  public static Class<?> getClass(String paramString) throws CertificateException {
    OIDInfo oIDInfo = (OIDInfo)nameMap.get(paramString);
    return (oIDInfo == null) ? null : oIDInfo.getClazz();
  }
  
  public static Class<?> getClass(ObjectIdentifier paramObjectIdentifier) throws CertificateException {
    OIDInfo oIDInfo = (OIDInfo)oidMap.get(paramObjectIdentifier);
    return (oIDInfo == null) ? null : oIDInfo.getClazz();
  }
  
  static  {
    addInternal("x509.info.extensions.SubjectKeyIdentifier", PKIXExtensions.SubjectKey_Id, "sun.security.x509.SubjectKeyIdentifierExtension");
    addInternal("x509.info.extensions.KeyUsage", PKIXExtensions.KeyUsage_Id, "sun.security.x509.KeyUsageExtension");
    addInternal("x509.info.extensions.PrivateKeyUsage", PKIXExtensions.PrivateKeyUsage_Id, "sun.security.x509.PrivateKeyUsageExtension");
    addInternal("x509.info.extensions.SubjectAlternativeName", PKIXExtensions.SubjectAlternativeName_Id, "sun.security.x509.SubjectAlternativeNameExtension");
    addInternal("x509.info.extensions.IssuerAlternativeName", PKIXExtensions.IssuerAlternativeName_Id, "sun.security.x509.IssuerAlternativeNameExtension");
    addInternal("x509.info.extensions.BasicConstraints", PKIXExtensions.BasicConstraints_Id, "sun.security.x509.BasicConstraintsExtension");
    addInternal("x509.info.extensions.CRLNumber", PKIXExtensions.CRLNumber_Id, "sun.security.x509.CRLNumberExtension");
    addInternal("x509.info.extensions.CRLReasonCode", PKIXExtensions.ReasonCode_Id, "sun.security.x509.CRLReasonCodeExtension");
    addInternal("x509.info.extensions.NameConstraints", PKIXExtensions.NameConstraints_Id, "sun.security.x509.NameConstraintsExtension");
    addInternal("x509.info.extensions.PolicyMappings", PKIXExtensions.PolicyMappings_Id, "sun.security.x509.PolicyMappingsExtension");
    addInternal("x509.info.extensions.AuthorityKeyIdentifier", PKIXExtensions.AuthorityKey_Id, "sun.security.x509.AuthorityKeyIdentifierExtension");
    addInternal("x509.info.extensions.PolicyConstraints", PKIXExtensions.PolicyConstraints_Id, "sun.security.x509.PolicyConstraintsExtension");
    addInternal("x509.info.extensions.NetscapeCertType", ObjectIdentifier.newInternal(new int[] { 2, 16, 840, 1, 113730, 1, 1 }, ), "sun.security.x509.NetscapeCertTypeExtension");
    addInternal("x509.info.extensions.CertificatePolicies", PKIXExtensions.CertificatePolicies_Id, "sun.security.x509.CertificatePoliciesExtension");
    addInternal("x509.info.extensions.ExtendedKeyUsage", PKIXExtensions.ExtendedKeyUsage_Id, "sun.security.x509.ExtendedKeyUsageExtension");
    addInternal("x509.info.extensions.InhibitAnyPolicy", PKIXExtensions.InhibitAnyPolicy_Id, "sun.security.x509.InhibitAnyPolicyExtension");
    addInternal("x509.info.extensions.CRLDistributionPoints", PKIXExtensions.CRLDistributionPoints_Id, "sun.security.x509.CRLDistributionPointsExtension");
    addInternal("x509.info.extensions.CertificateIssuer", PKIXExtensions.CertificateIssuer_Id, "sun.security.x509.CertificateIssuerExtension");
    addInternal("x509.info.extensions.SubjectInfoAccess", PKIXExtensions.SubjectInfoAccess_Id, "sun.security.x509.SubjectInfoAccessExtension");
    addInternal("x509.info.extensions.AuthorityInfoAccess", PKIXExtensions.AuthInfoAccess_Id, "sun.security.x509.AuthorityInfoAccessExtension");
    addInternal("x509.info.extensions.IssuingDistributionPoint", PKIXExtensions.IssuingDistributionPoint_Id, "sun.security.x509.IssuingDistributionPointExtension");
    addInternal("x509.info.extensions.DeltaCRLIndicator", PKIXExtensions.DeltaCRLIndicator_Id, "sun.security.x509.DeltaCRLIndicatorExtension");
    addInternal("x509.info.extensions.FreshestCRL", PKIXExtensions.FreshestCRL_Id, "sun.security.x509.FreshestCRLExtension");
    addInternal("x509.info.extensions.OCSPNoCheck", PKIXExtensions.OCSPNoCheck_Id, "sun.security.x509.OCSPNoCheckExtension");
  }
  
  private static class OIDInfo {
    final ObjectIdentifier oid;
    
    final String name;
    
    final String className;
    
    OIDInfo(String param1String1, ObjectIdentifier param1ObjectIdentifier, String param1String2) {
      this.name = param1String1;
      this.oid = param1ObjectIdentifier;
      this.className = param1String2;
    }
    
    OIDInfo(String param1String, ObjectIdentifier param1ObjectIdentifier, Class<?> param1Class) {
      this.name = param1String;
      this.oid = param1ObjectIdentifier;
      this.className = param1Class.getName();
      this.clazz = param1Class;
    }
    
    Class<?> getClazz() throws CertificateException {
      try {
        Class clazz1 = this.clazz;
        if (clazz1 == null) {
          clazz1 = Class.forName(this.className);
          this.clazz = clazz1;
        } 
        return clazz1;
      } catch (ClassNotFoundException classNotFoundException) {
        throw new CertificateException("Could not load class: " + classNotFoundException, classNotFoundException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\OIDMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */