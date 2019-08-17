package sun.security.x509;

import sun.security.util.ObjectIdentifier;

public class PKIXExtensions {
  private static final int[] AuthorityKey_data = { 2, 5, 29, 35 };
  
  private static final int[] SubjectKey_data = { 2, 5, 29, 14 };
  
  private static final int[] KeyUsage_data = { 2, 5, 29, 15 };
  
  private static final int[] PrivateKeyUsage_data = { 2, 5, 29, 16 };
  
  private static final int[] CertificatePolicies_data = { 2, 5, 29, 32 };
  
  private static final int[] PolicyMappings_data = { 2, 5, 29, 33 };
  
  private static final int[] SubjectAlternativeName_data = { 2, 5, 29, 17 };
  
  private static final int[] IssuerAlternativeName_data = { 2, 5, 29, 18 };
  
  private static final int[] SubjectDirectoryAttributes_data = { 2, 5, 29, 9 };
  
  private static final int[] BasicConstraints_data = { 2, 5, 29, 19 };
  
  private static final int[] NameConstraints_data = { 2, 5, 29, 30 };
  
  private static final int[] PolicyConstraints_data = { 2, 5, 29, 36 };
  
  private static final int[] CRLDistributionPoints_data = { 2, 5, 29, 31 };
  
  private static final int[] CRLNumber_data = { 2, 5, 29, 20 };
  
  private static final int[] IssuingDistributionPoint_data = { 2, 5, 29, 28 };
  
  private static final int[] DeltaCRLIndicator_data = { 2, 5, 29, 27 };
  
  private static final int[] ReasonCode_data = { 2, 5, 29, 21 };
  
  private static final int[] HoldInstructionCode_data = { 2, 5, 29, 23 };
  
  private static final int[] InvalidityDate_data = { 2, 5, 29, 24 };
  
  private static final int[] ExtendedKeyUsage_data = { 2, 5, 29, 37 };
  
  private static final int[] InhibitAnyPolicy_data = { 2, 5, 29, 54 };
  
  private static final int[] CertificateIssuer_data = { 2, 5, 29, 29 };
  
  private static final int[] AuthInfoAccess_data = { 1, 3, 6, 1, 5, 5, 7, 1, 1 };
  
  private static final int[] SubjectInfoAccess_data = { 1, 3, 6, 1, 5, 5, 7, 1, 11 };
  
  private static final int[] FreshestCRL_data = { 2, 5, 29, 46 };
  
  private static final int[] OCSPNoCheck_data = { 1, 3, 6, 1, 5, 5, 7, 48, 1, 5 };
  
  private static final int[] OCSPNonce_data = { 1, 3, 6, 1, 5, 5, 7, 48, 1, 2 };
  
  public static final ObjectIdentifier AuthorityKey_Id;
  
  public static final ObjectIdentifier SubjectKey_Id;
  
  public static final ObjectIdentifier KeyUsage_Id;
  
  public static final ObjectIdentifier PrivateKeyUsage_Id;
  
  public static final ObjectIdentifier CertificatePolicies_Id;
  
  public static final ObjectIdentifier PolicyMappings_Id;
  
  public static final ObjectIdentifier SubjectAlternativeName_Id;
  
  public static final ObjectIdentifier IssuerAlternativeName_Id;
  
  public static final ObjectIdentifier SubjectDirectoryAttributes_Id;
  
  public static final ObjectIdentifier BasicConstraints_Id;
  
  public static final ObjectIdentifier NameConstraints_Id;
  
  public static final ObjectIdentifier PolicyConstraints_Id;
  
  public static final ObjectIdentifier CRLDistributionPoints_Id;
  
  public static final ObjectIdentifier CRLNumber_Id;
  
  public static final ObjectIdentifier IssuingDistributionPoint_Id;
  
  public static final ObjectIdentifier DeltaCRLIndicator_Id;
  
  public static final ObjectIdentifier ReasonCode_Id;
  
  public static final ObjectIdentifier HoldInstructionCode_Id;
  
  public static final ObjectIdentifier InvalidityDate_Id;
  
  public static final ObjectIdentifier ExtendedKeyUsage_Id;
  
  public static final ObjectIdentifier InhibitAnyPolicy_Id;
  
  public static final ObjectIdentifier CertificateIssuer_Id;
  
  public static final ObjectIdentifier AuthInfoAccess_Id;
  
  public static final ObjectIdentifier SubjectInfoAccess_Id;
  
  public static final ObjectIdentifier FreshestCRL_Id;
  
  public static final ObjectIdentifier OCSPNoCheck_Id;
  
  public static final ObjectIdentifier OCSPNonce_Id = (OCSPNoCheck_Id = (FreshestCRL_Id = (SubjectInfoAccess_Id = (AuthInfoAccess_Id = (CertificateIssuer_Id = (DeltaCRLIndicator_Id = (IssuingDistributionPoint_Id = (CRLNumber_Id = (CRLDistributionPoints_Id = (PolicyConstraints_Id = (NameConstraints_Id = (InvalidityDate_Id = (HoldInstructionCode_Id = (ReasonCode_Id = (BasicConstraints_Id = (SubjectDirectoryAttributes_Id = (InhibitAnyPolicy_Id = (ExtendedKeyUsage_Id = (IssuerAlternativeName_Id = (SubjectAlternativeName_Id = (PolicyMappings_Id = (CertificatePolicies_Id = (PrivateKeyUsage_Id = (KeyUsage_Id = (SubjectKey_Id = (AuthorityKey_Id = ObjectIdentifier.newInternal(AuthorityKey_data)).newInternal(SubjectKey_data)).newInternal(KeyUsage_data)).newInternal(PrivateKeyUsage_data)).newInternal(CertificatePolicies_data)).newInternal(PolicyMappings_data)).newInternal(SubjectAlternativeName_data)).newInternal(IssuerAlternativeName_data)).newInternal(ExtendedKeyUsage_data)).newInternal(InhibitAnyPolicy_data)).newInternal(SubjectDirectoryAttributes_data)).newInternal(BasicConstraints_data)).newInternal(ReasonCode_data)).newInternal(HoldInstructionCode_data)).newInternal(InvalidityDate_data)).newInternal(NameConstraints_data)).newInternal(PolicyConstraints_data)).newInternal(CRLDistributionPoints_data)).newInternal(CRLNumber_data)).newInternal(IssuingDistributionPoint_data)).newInternal(DeltaCRLIndicator_data)).newInternal(CertificateIssuer_data)).newInternal(AuthInfoAccess_data)).newInternal(SubjectInfoAccess_data)).newInternal(FreshestCRL_data)).newInternal(OCSPNoCheck_data)).newInternal(OCSPNonce_data);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\PKIXExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */