package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

abstract class AbstractDOMSignatureMethod extends DOMStructure implements SignatureMethod {
  abstract boolean verify(Key paramKey, SignedInfo paramSignedInfo, byte[] paramArrayOfByte, XMLValidateContext paramXMLValidateContext) throws InvalidKeyException, SignatureException, XMLSignatureException;
  
  abstract byte[] sign(Key paramKey, SignedInfo paramSignedInfo, XMLSignContext paramXMLSignContext) throws InvalidKeyException, XMLSignatureException;
  
  abstract String getJCAAlgorithm();
  
  abstract Type getAlgorithmType();
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "SignatureMethod", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttribute(element, "Algorithm", getAlgorithm());
    if (getParameterSpec() != null)
      marshalParams(element, paramString); 
    paramNode.appendChild(element);
  }
  
  void marshalParams(Element paramElement, String paramString) throws MarshalException { throw new MarshalException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm"); }
  
  SignatureMethodParameterSpec unmarshalParams(Element paramElement) throws MarshalException { throw new MarshalException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm"); }
  
  void checkParams(SignatureMethodParameterSpec paramSignatureMethodParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramSignatureMethodParameterSpec != null)
      throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + getAlgorithm() + " SignatureMethod algorithm"); 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SignatureMethod))
      return false; 
    SignatureMethod signatureMethod = (SignatureMethod)paramObject;
    return (getAlgorithm().equals(signatureMethod.getAlgorithm()) && paramsEqual(signatureMethod.getParameterSpec()));
  }
  
  public int hashCode() {
    int i = 17;
    i = 31 * i + getAlgorithm().hashCode();
    AlgorithmParameterSpec algorithmParameterSpec = getParameterSpec();
    if (algorithmParameterSpec != null)
      i = 31 * i + algorithmParameterSpec.hashCode(); 
    return i;
  }
  
  boolean paramsEqual(AlgorithmParameterSpec paramAlgorithmParameterSpec) { return (getParameterSpec() == paramAlgorithmParameterSpec); }
  
  enum Type {
    DSA, RSA, ECDSA, HMAC;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\AbstractDOMSignatureMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */