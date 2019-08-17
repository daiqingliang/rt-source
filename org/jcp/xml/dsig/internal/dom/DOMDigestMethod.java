package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.spec.DigestMethodParameterSpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMDigestMethod extends DOMStructure implements DigestMethod {
  static final String SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
  
  private DigestMethodParameterSpec params;
  
  DOMDigestMethod(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramAlgorithmParameterSpec != null && !(paramAlgorithmParameterSpec instanceof DigestMethodParameterSpec))
      throw new InvalidAlgorithmParameterException("params must be of type DigestMethodParameterSpec"); 
    checkParams((DigestMethodParameterSpec)paramAlgorithmParameterSpec);
    this.params = (DigestMethodParameterSpec)paramAlgorithmParameterSpec;
  }
  
  DOMDigestMethod(Element paramElement) throws MarshalException {
    Element element = DOMUtils.getFirstChildElement(paramElement);
    if (element != null)
      this.params = unmarshalParams(element); 
    try {
      checkParams(this.params);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new MarshalException(invalidAlgorithmParameterException);
    } 
  }
  
  static DigestMethod unmarshal(Element paramElement) throws MarshalException {
    String str = DOMUtils.getAttributeValue(paramElement, "Algorithm");
    if (str.equals("http://www.w3.org/2000/09/xmldsig#sha1"))
      return new SHA1(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmlenc#sha256"))
      return new SHA256(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#sha384"))
      return new SHA384(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmlenc#sha512"))
      return new SHA512(paramElement); 
    throw new MarshalException("unsupported DigestMethod algorithm: " + str);
  }
  
  void checkParams(DigestMethodParameterSpec paramDigestMethodParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramDigestMethodParameterSpec != null)
      throw new InvalidAlgorithmParameterException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm"); 
  }
  
  public final AlgorithmParameterSpec getParameterSpec() { return this.params; }
  
  DigestMethodParameterSpec unmarshalParams(Element paramElement) throws MarshalException { throw new MarshalException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm"); }
  
  public void marshal(Node paramNode, String paramString, DOMCryptoContext paramDOMCryptoContext) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramNode);
    Element element = DOMUtils.createElement(document, "DigestMethod", "http://www.w3.org/2000/09/xmldsig#", paramString);
    DOMUtils.setAttribute(element, "Algorithm", getAlgorithm());
    if (this.params != null)
      marshalParams(element, paramString); 
    paramNode.appendChild(element);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof DigestMethod))
      return false; 
    DigestMethod digestMethod = (DigestMethod)paramObject;
    boolean bool = (this.params == null) ? ((digestMethod.getParameterSpec() == null) ? 1 : 0) : this.params.equals(digestMethod.getParameterSpec());
    return (getAlgorithm().equals(digestMethod.getAlgorithm()) && bool);
  }
  
  public int hashCode() {
    null = 17;
    if (this.params != null)
      null = 31 * null + this.params.hashCode(); 
    return 31 * null + getAlgorithm().hashCode();
  }
  
  void marshalParams(Element paramElement, String paramString) throws MarshalException { throw new MarshalException("no parameters should be specified for the " + getMessageDigestAlgorithm() + " DigestMethod algorithm"); }
  
  abstract String getMessageDigestAlgorithm();
  
  static final class SHA1 extends DOMDigestMethod {
    SHA1(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA1(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2000/09/xmldsig#sha1"; }
    
    String getMessageDigestAlgorithm() { return "SHA-1"; }
  }
  
  static final class SHA256 extends DOMDigestMethod {
    SHA256(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA256(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmlenc#sha256"; }
    
    String getMessageDigestAlgorithm() { return "SHA-256"; }
  }
  
  static final class SHA384 extends DOMDigestMethod {
    SHA384(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA384(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#sha384"; }
    
    String getMessageDigestAlgorithm() { return "SHA-384"; }
  }
  
  static final class SHA512 extends DOMDigestMethod {
    SHA512(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA512(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmlenc#sha512"; }
    
    String getMessageDigestAlgorithm() { return "SHA-512"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMDigestMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */