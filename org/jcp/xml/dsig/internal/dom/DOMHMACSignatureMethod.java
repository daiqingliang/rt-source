package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.HMACParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.jcp.xml.dsig.internal.MacOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class DOMHMACSignatureMethod extends AbstractDOMSignatureMethod {
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  
  static final String HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
  
  static final String HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
  
  static final String HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
  
  private Mac hmac;
  
  private int outputLength;
  
  private boolean outputLengthSet;
  
  private SignatureMethodParameterSpec params;
  
  DOMHMACSignatureMethod(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
    checkParams((SignatureMethodParameterSpec)paramAlgorithmParameterSpec);
    this.params = (SignatureMethodParameterSpec)paramAlgorithmParameterSpec;
  }
  
  DOMHMACSignatureMethod(Element paramElement) throws MarshalException {
    Element element = DOMUtils.getFirstChildElement(paramElement);
    if (element != null)
      this.params = unmarshalParams(element); 
    try {
      checkParams(this.params);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new MarshalException(invalidAlgorithmParameterException);
    } 
  }
  
  void checkParams(SignatureMethodParameterSpec paramSignatureMethodParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramSignatureMethodParameterSpec != null) {
      if (!(paramSignatureMethodParameterSpec instanceof HMACParameterSpec))
        throw new InvalidAlgorithmParameterException("params must be of type HMACParameterSpec"); 
      this.outputLength = ((HMACParameterSpec)paramSignatureMethodParameterSpec).getOutputLength();
      this.outputLengthSet = true;
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Setting outputLength from HMACParameterSpec to: " + this.outputLength); 
    } 
  }
  
  public final AlgorithmParameterSpec getParameterSpec() { return this.params; }
  
  SignatureMethodParameterSpec unmarshalParams(Element paramElement) throws MarshalException {
    this.outputLength = Integer.valueOf(paramElement.getFirstChild().getNodeValue()).intValue();
    this.outputLengthSet = true;
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "unmarshalled outputLength: " + this.outputLength); 
    return new HMACParameterSpec(this.outputLength);
  }
  
  void marshalParams(Element paramElement, String paramString) throws MarshalException {
    Document document = DOMUtils.getOwnerDocument(paramElement);
    Element element = DOMUtils.createElement(document, "HMACOutputLength", "http://www.w3.org/2000/09/xmldsig#", paramString);
    element.appendChild(document.createTextNode(String.valueOf(this.outputLength)));
    paramElement.appendChild(element);
  }
  
  boolean verify(Key paramKey, SignedInfo paramSignedInfo, byte[] paramArrayOfByte, XMLValidateContext paramXMLValidateContext) throws InvalidKeyException, SignatureException, XMLSignatureException {
    if (paramKey == null || paramSignedInfo == null || paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (!(paramKey instanceof SecretKey))
      throw new InvalidKeyException("key must be SecretKey"); 
    if (this.hmac == null)
      try {
        this.hmac = Mac.getInstance(getJCAAlgorithm());
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new XMLSignatureException(noSuchAlgorithmException);
      }  
    if (this.outputLengthSet && this.outputLength < getDigestLength())
      throw new XMLSignatureException("HMACOutputLength must not be less than " + getDigestLength()); 
    this.hmac.init((SecretKey)paramKey);
    ((DOMSignedInfo)paramSignedInfo).canonicalize(paramXMLValidateContext, new MacOutputStream(this.hmac));
    byte[] arrayOfByte = this.hmac.doFinal();
    return MessageDigest.isEqual(paramArrayOfByte, arrayOfByte);
  }
  
  byte[] sign(Key paramKey, SignedInfo paramSignedInfo, XMLSignContext paramXMLSignContext) throws InvalidKeyException, XMLSignatureException {
    if (paramKey == null || paramSignedInfo == null)
      throw new NullPointerException(); 
    if (!(paramKey instanceof SecretKey))
      throw new InvalidKeyException("key must be SecretKey"); 
    if (this.hmac == null)
      try {
        this.hmac = Mac.getInstance(getJCAAlgorithm());
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new XMLSignatureException(noSuchAlgorithmException);
      }  
    if (this.outputLengthSet && this.outputLength < getDigestLength())
      throw new XMLSignatureException("HMACOutputLength must not be less than " + getDigestLength()); 
    this.hmac.init((SecretKey)paramKey);
    ((DOMSignedInfo)paramSignedInfo).canonicalize(paramXMLSignContext, new MacOutputStream(this.hmac));
    return this.hmac.doFinal();
  }
  
  boolean paramsEqual(AlgorithmParameterSpec paramAlgorithmParameterSpec) {
    if (getParameterSpec() == paramAlgorithmParameterSpec)
      return true; 
    if (!(paramAlgorithmParameterSpec instanceof HMACParameterSpec))
      return false; 
    HMACParameterSpec hMACParameterSpec = (HMACParameterSpec)paramAlgorithmParameterSpec;
    return (this.outputLength == hMACParameterSpec.getOutputLength());
  }
  
  AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.HMAC; }
  
  abstract int getDigestLength();
  
  static final class SHA1 extends DOMHMACSignatureMethod {
    SHA1(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA1(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2000/09/xmldsig#hmac-sha1"; }
    
    String getJCAAlgorithm() { return "HmacSHA1"; }
    
    int getDigestLength() { return 160; }
  }
  
  static final class SHA256 extends DOMHMACSignatureMethod {
    SHA256(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA256(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256"; }
    
    String getJCAAlgorithm() { return "HmacSHA256"; }
    
    int getDigestLength() { return 256; }
  }
  
  static final class SHA384 extends DOMHMACSignatureMethod {
    SHA384(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA384(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384"; }
    
    String getJCAAlgorithm() { return "HmacSHA384"; }
    
    int getDigestLength() { return 384; }
  }
  
  static final class SHA512 extends DOMHMACSignatureMethod {
    SHA512(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA512(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512"; }
    
    String getJCAAlgorithm() { return "HmacSHA512"; }
    
    int getDigestLength() { return 512; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMHMACSignatureMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */