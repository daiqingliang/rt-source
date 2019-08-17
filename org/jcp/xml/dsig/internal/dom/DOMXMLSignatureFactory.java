package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.TransformService;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMXMLSignatureFactory extends XMLSignatureFactory {
  public XMLSignature newXMLSignature(SignedInfo paramSignedInfo, KeyInfo paramKeyInfo) { return new DOMXMLSignature(paramSignedInfo, paramKeyInfo, null, null, null); }
  
  public XMLSignature newXMLSignature(SignedInfo paramSignedInfo, KeyInfo paramKeyInfo, List paramList, String paramString1, String paramString2) { return new DOMXMLSignature(paramSignedInfo, paramKeyInfo, paramList, paramString1, paramString2); }
  
  public Reference newReference(String paramString, DigestMethod paramDigestMethod) { return newReference(paramString, paramDigestMethod, null, null, null); }
  
  public Reference newReference(String paramString1, DigestMethod paramDigestMethod, List paramList, String paramString2, String paramString3) { return new DOMReference(paramString1, paramString2, paramDigestMethod, paramList, paramString3, getProvider()); }
  
  public Reference newReference(String paramString1, DigestMethod paramDigestMethod, List paramList1, Data paramData, List paramList2, String paramString2, String paramString3) {
    if (paramList1 == null)
      throw new NullPointerException("appliedTransforms cannot be null"); 
    if (paramList1.isEmpty())
      throw new NullPointerException("appliedTransforms cannot be empty"); 
    if (paramData == null)
      throw new NullPointerException("result cannot be null"); 
    return new DOMReference(paramString1, paramString2, paramDigestMethod, paramList1, paramData, paramList2, paramString3, getProvider());
  }
  
  public Reference newReference(String paramString1, DigestMethod paramDigestMethod, List paramList, String paramString2, String paramString3, byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      throw new NullPointerException("digestValue cannot be null"); 
    return new DOMReference(paramString1, paramString2, paramDigestMethod, null, null, paramList, paramString3, paramArrayOfByte, getProvider());
  }
  
  public SignedInfo newSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List paramList) { return newSignedInfo(paramCanonicalizationMethod, paramSignatureMethod, paramList, null); }
  
  public SignedInfo newSignedInfo(CanonicalizationMethod paramCanonicalizationMethod, SignatureMethod paramSignatureMethod, List paramList, String paramString) { return new DOMSignedInfo(paramCanonicalizationMethod, paramSignatureMethod, paramList, paramString); }
  
  public XMLObject newXMLObject(List paramList, String paramString1, String paramString2, String paramString3) { return new DOMXMLObject(paramList, paramString1, paramString2, paramString3); }
  
  public Manifest newManifest(List paramList) { return newManifest(paramList, null); }
  
  public Manifest newManifest(List paramList, String paramString) { return new DOMManifest(paramList, paramString); }
  
  public SignatureProperties newSignatureProperties(List paramList, String paramString) { return new DOMSignatureProperties(paramList, paramString); }
  
  public SignatureProperty newSignatureProperty(List paramList, String paramString1, String paramString2) { return new DOMSignatureProperty(paramList, paramString1, paramString2); }
  
  public XMLSignature unmarshalXMLSignature(XMLValidateContext paramXMLValidateContext) throws MarshalException {
    if (paramXMLValidateContext == null)
      throw new NullPointerException("context cannot be null"); 
    return unmarshal(((DOMValidateContext)paramXMLValidateContext).getNode(), paramXMLValidateContext);
  }
  
  public XMLSignature unmarshalXMLSignature(XMLStructure paramXMLStructure) throws MarshalException {
    if (paramXMLStructure == null)
      throw new NullPointerException("xmlStructure cannot be null"); 
    if (!(paramXMLStructure instanceof DOMStructure))
      throw new ClassCastException("xmlStructure must be of type DOMStructure"); 
    return unmarshal(((DOMStructure)paramXMLStructure).getNode(), new UnmarshalContext());
  }
  
  private XMLSignature unmarshal(Node paramNode, XMLCryptoContext paramXMLCryptoContext) throws MarshalException {
    paramNode.normalize();
    Element element = null;
    if (paramNode.getNodeType() == 9) {
      element = ((Document)paramNode).getDocumentElement();
    } else if (paramNode.getNodeType() == 1) {
      element = (Element)paramNode;
    } else {
      throw new MarshalException("Signature element is not a proper Node");
    } 
    String str = element.getLocalName();
    if (str == null)
      throw new MarshalException("Document implementation must support DOM Level 2 and be namespace aware"); 
    if (str.equals("Signature"))
      return new DOMXMLSignature(element, paramXMLCryptoContext, getProvider()); 
    throw new MarshalException("invalid Signature tag: " + str);
  }
  
  public boolean isFeatureSupported(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    return false;
  }
  
  public DigestMethod newDigestMethod(String paramString, DigestMethodParameterSpec paramDigestMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    if (paramString == null)
      throw new NullPointerException(); 
    if (paramString.equals("http://www.w3.org/2000/09/xmldsig#sha1"))
      return new DOMDigestMethod.SHA1(paramDigestMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmlenc#sha256"))
      return new DOMDigestMethod.SHA256(paramDigestMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#sha384"))
      return new DOMDigestMethod.SHA384(paramDigestMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmlenc#sha512"))
      return new DOMDigestMethod.SHA512(paramDigestMethodParameterSpec); 
    throw new NoSuchAlgorithmException("unsupported algorithm");
  }
  
  public SignatureMethod newSignatureMethod(String paramString, SignatureMethodParameterSpec paramSignatureMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    if (paramString == null)
      throw new NullPointerException(); 
    if (paramString.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
      return new DOMSignatureMethod.SHA1withRSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"))
      return new DOMSignatureMethod.SHA256withRSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384"))
      return new DOMSignatureMethod.SHA384withRSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
      return new DOMSignatureMethod.SHA512withRSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1"))
      return new DOMSignatureMethod.SHA1withDSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2009/xmldsig11#dsa-sha256"))
      return new DOMSignatureMethod.SHA256withDSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1"))
      return new DOMHMACSignatureMethod.SHA1(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256"))
      return new DOMHMACSignatureMethod.SHA256(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384"))
      return new DOMHMACSignatureMethod.SHA384(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512"))
      return new DOMHMACSignatureMethod.SHA512(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"))
      return new DOMSignatureMethod.SHA1withECDSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))
      return new DOMSignatureMethod.SHA256withECDSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384"))
      return new DOMSignatureMethod.SHA384withECDSA(paramSignatureMethodParameterSpec); 
    if (paramString.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512"))
      return new DOMSignatureMethod.SHA512withECDSA(paramSignatureMethodParameterSpec); 
    throw new NoSuchAlgorithmException("unsupported algorithm");
  }
  
  public Transform newTransform(String paramString, TransformParameterSpec paramTransformParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    TransformService transformService;
    if (getProvider() == null) {
      transformService = TransformService.getInstance(paramString, "DOM");
    } else {
      try {
        transformService = TransformService.getInstance(paramString, "DOM", getProvider());
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        transformService = TransformService.getInstance(paramString, "DOM");
      } 
    } 
    transformService.init(paramTransformParameterSpec);
    return new DOMTransform(transformService);
  }
  
  public Transform newTransform(String paramString, XMLStructure paramXMLStructure) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    TransformService transformService;
    if (getProvider() == null) {
      transformService = TransformService.getInstance(paramString, "DOM");
    } else {
      try {
        transformService = TransformService.getInstance(paramString, "DOM", getProvider());
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        transformService = TransformService.getInstance(paramString, "DOM");
      } 
    } 
    if (paramXMLStructure == null) {
      transformService.init(null);
    } else {
      transformService.init(paramXMLStructure, null);
    } 
    return new DOMTransform(transformService);
  }
  
  public CanonicalizationMethod newCanonicalizationMethod(String paramString, C14NMethodParameterSpec paramC14NMethodParameterSpec) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    TransformService transformService;
    if (getProvider() == null) {
      transformService = TransformService.getInstance(paramString, "DOM");
    } else {
      try {
        transformService = TransformService.getInstance(paramString, "DOM", getProvider());
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        transformService = TransformService.getInstance(paramString, "DOM");
      } 
    } 
    transformService.init(paramC14NMethodParameterSpec);
    return new DOMCanonicalizationMethod(transformService);
  }
  
  public CanonicalizationMethod newCanonicalizationMethod(String paramString, XMLStructure paramXMLStructure) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    TransformService transformService;
    if (getProvider() == null) {
      transformService = TransformService.getInstance(paramString, "DOM");
    } else {
      try {
        transformService = TransformService.getInstance(paramString, "DOM", getProvider());
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        transformService = TransformService.getInstance(paramString, "DOM");
      } 
    } 
    if (paramXMLStructure == null) {
      transformService.init(null);
    } else {
      transformService.init(paramXMLStructure, null);
    } 
    return new DOMCanonicalizationMethod(transformService);
  }
  
  public URIDereferencer getURIDereferencer() { return DOMURIDereferencer.INSTANCE; }
  
  private static class UnmarshalContext extends DOMCryptoContext {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMXMLSignatureFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */