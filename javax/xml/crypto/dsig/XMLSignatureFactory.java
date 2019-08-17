package javax.xml.crypto.dsig;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import sun.security.jca.GetInstance;

public abstract class XMLSignatureFactory {
  private String mechanismType;
  
  private Provider provider;
  
  public static XMLSignatureFactory getInstance(String paramString) {
    GetInstance.Instance instance;
    if (paramString == null)
      throw new NullPointerException("mechanismType cannot be null"); 
    try {
      instance = GetInstance.getInstance("XMLSignatureFactory", null, paramString);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchMechanismException(noSuchAlgorithmException);
    } 
    XMLSignatureFactory xMLSignatureFactory = (XMLSignatureFactory)instance.impl;
    xMLSignatureFactory.mechanismType = paramString;
    xMLSignatureFactory.provider = instance.provider;
    return xMLSignatureFactory;
  }
  
  public static XMLSignatureFactory getInstance(String paramString, Provider paramProvider) {
    GetInstance.Instance instance;
    if (paramString == null)
      throw new NullPointerException("mechanismType cannot be null"); 
    if (paramProvider == null)
      throw new NullPointerException("provider cannot be null"); 
    try {
      instance = GetInstance.getInstance("XMLSignatureFactory", null, paramString, paramProvider);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchMechanismException(noSuchAlgorithmException);
    } 
    XMLSignatureFactory xMLSignatureFactory = (XMLSignatureFactory)instance.impl;
    xMLSignatureFactory.mechanismType = paramString;
    xMLSignatureFactory.provider = instance.provider;
    return xMLSignatureFactory;
  }
  
  public static XMLSignatureFactory getInstance(String paramString1, String paramString2) throws NoSuchProviderException {
    GetInstance.Instance instance;
    if (paramString1 == null)
      throw new NullPointerException("mechanismType cannot be null"); 
    if (paramString2 == null)
      throw new NullPointerException("provider cannot be null"); 
    if (paramString2.length() == 0)
      throw new NoSuchProviderException(); 
    try {
      instance = GetInstance.getInstance("XMLSignatureFactory", null, paramString1, paramString2);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchMechanismException(noSuchAlgorithmException);
    } 
    XMLSignatureFactory xMLSignatureFactory = (XMLSignatureFactory)instance.impl;
    xMLSignatureFactory.mechanismType = paramString1;
    xMLSignatureFactory.provider = instance.provider;
    return xMLSignatureFactory;
  }
  
  public static XMLSignatureFactory getInstance() { return getInstance("DOM"); }
  
  public final String getMechanismType() { return this.mechanismType; }
  
  public final Provider getProvider() { return this.provider; }
  
  public abstract XMLSignature newXMLSignature(SignedInfo paramSignedInfo, KeyInfo paramKeyInfo);
  
  public abstract XMLSignature newXMLSignature(SignedInfo paramSignedInfo, KeyInfo paramKeyInfo, List paramList, String paramString1, String paramString2);
  
  public abstract Reference newReference(String paramString, DigestMethod paramDigestMethod);
  
  public abstract Reference newReference(String paramString1, DigestMethod paramDigestMethod, List paramList, String paramString2, String paramString3);
  
  public abstract Reference newReference(String paramString1, DigestMethod paramDigestMethod, List paramList, String paramString2, String paramString3, byte[] paramArrayOfByte);
  
  public abstract Reference newReference(String paramString1, DigestMethod paramDigestMethod, List paramList1, Data paramData, List paramList2, String paramString2, String paramString3);
  
  public abstract SignedInfo newSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List paramList);
  
  public abstract SignedInfo newSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List paramList, String paramString);
  
  public abstract XMLObject newXMLObject(List paramList, String paramString1, String paramString2, String paramString3);
  
  public abstract Manifest newManifest(List paramList);
  
  public abstract Manifest newManifest(List paramList, String paramString);
  
  public abstract SignatureProperty newSignatureProperty(List paramList, String paramString1, String paramString2);
  
  public abstract SignatureProperties newSignatureProperties(List paramList, String paramString);
  
  public abstract DigestMethod newDigestMethod(String paramString, DigestMethodParameterSpec paramDigestMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
  
  public abstract SignatureMethod newSignatureMethod(String paramString, SignatureMethodParameterSpec paramSignatureMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
  
  public abstract Transform newTransform(String paramString, TransformParameterSpec paramTransformParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
  
  public abstract Transform newTransform(String paramString, XMLStructure paramXMLStructure) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
  
  public abstract CanonicalizationMethod newCanonicalizationMethod(String paramString, C14NMethodParameterSpec paramC14NMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
  
  public abstract CanonicalizationMethod newCanonicalizationMethod(String paramString, XMLStructure paramXMLStructure) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException;
  
  public final KeyInfoFactory getKeyInfoFactory() { return KeyInfoFactory.getInstance(getMechanismType(), getProvider()); }
  
  public abstract XMLSignature unmarshalXMLSignature(XMLValidateContext paramXMLValidateContext) throws MarshalException;
  
  public abstract XMLSignature unmarshalXMLSignature(XMLStructure paramXMLStructure) throws MarshalException;
  
  public abstract boolean isFeatureSupported(String paramString);
  
  public abstract URIDereferencer getURIDereferencer();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\XMLSignatureFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */