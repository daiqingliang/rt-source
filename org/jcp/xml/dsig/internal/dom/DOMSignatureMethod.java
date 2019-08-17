package org.jcp.xml.dsig.internal.dom;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import org.jcp.xml.dsig.internal.SignerOutputStream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sun.security.util.KeyUtil;

public abstract class DOMSignatureMethod extends AbstractDOMSignatureMethod {
  private static Logger log = Logger.getLogger("org.jcp.xml.dsig.internal.dom");
  
  private SignatureMethodParameterSpec params;
  
  private Signature signature;
  
  static final String RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
  
  static final String RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
  
  static final String RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
  
  static final String ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
  
  static final String ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
  
  static final String ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
  
  static final String ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
  
  static final String DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
  
  DOMSignatureMethod(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramAlgorithmParameterSpec != null && !(paramAlgorithmParameterSpec instanceof SignatureMethodParameterSpec))
      throw new InvalidAlgorithmParameterException("params must be of type SignatureMethodParameterSpec"); 
    checkParams((SignatureMethodParameterSpec)paramAlgorithmParameterSpec);
    this.params = (SignatureMethodParameterSpec)paramAlgorithmParameterSpec;
  }
  
  DOMSignatureMethod(Element paramElement) throws MarshalException {
    Element element = DOMUtils.getFirstChildElement(paramElement);
    if (element != null)
      this.params = unmarshalParams(element); 
    try {
      checkParams(this.params);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new MarshalException(invalidAlgorithmParameterException);
    } 
  }
  
  static SignatureMethod unmarshal(Element paramElement) throws MarshalException {
    String str = DOMUtils.getAttributeValue(paramElement, "Algorithm");
    if (str.equals("http://www.w3.org/2000/09/xmldsig#rsa-sha1"))
      return new SHA1withRSA(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"))
      return new SHA256withRSA(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384"))
      return new SHA384withRSA(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"))
      return new SHA512withRSA(paramElement); 
    if (str.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1"))
      return new SHA1withDSA(paramElement); 
    if (str.equals("http://www.w3.org/2009/xmldsig11#dsa-sha256"))
      return new SHA256withDSA(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"))
      return new SHA1withECDSA(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"))
      return new SHA256withECDSA(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384"))
      return new SHA384withECDSA(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512"))
      return new SHA512withECDSA(paramElement); 
    if (str.equals("http://www.w3.org/2000/09/xmldsig#hmac-sha1"))
      return new DOMHMACSignatureMethod.SHA1(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256"))
      return new DOMHMACSignatureMethod.SHA256(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384"))
      return new DOMHMACSignatureMethod.SHA384(paramElement); 
    if (str.equals("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512"))
      return new DOMHMACSignatureMethod.SHA512(paramElement); 
    throw new MarshalException("unsupported SignatureMethod algorithm: " + str);
  }
  
  public final AlgorithmParameterSpec getParameterSpec() { return this.params; }
  
  boolean verify(Key paramKey, SignedInfo paramSignedInfo, byte[] paramArrayOfByte, XMLValidateContext paramXMLValidateContext) throws InvalidKeyException, SignatureException, XMLSignatureException {
    if (paramKey == null || paramSignedInfo == null || paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (!(paramKey instanceof PublicKey))
      throw new InvalidKeyException("key must be PublicKey"); 
    checkKeySize(paramXMLValidateContext, paramKey);
    if (this.signature == null)
      try {
        Provider provider = (Provider)paramXMLValidateContext.getProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider");
        this.signature = (provider == null) ? Signature.getInstance(getJCAAlgorithm()) : Signature.getInstance(getJCAAlgorithm(), provider);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new XMLSignatureException(noSuchAlgorithmException);
      }  
    this.signature.initVerify((PublicKey)paramKey);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Signature provider:" + this.signature.getProvider());
      log.log(Level.FINE, "verifying with key: " + paramKey);
    } 
    ((DOMSignedInfo)paramSignedInfo).canonicalize(paramXMLValidateContext, new SignerOutputStream(this.signature));
    try {
      AbstractDOMSignatureMethod.Type type = getAlgorithmType();
      if (type == AbstractDOMSignatureMethod.Type.DSA) {
        int i = ((DSAKey)paramKey).getParams().getQ().bitLength();
        return this.signature.verify(JavaUtils.convertDsaXMLDSIGtoASN1(paramArrayOfByte, i / 8));
      } 
      return (type == AbstractDOMSignatureMethod.Type.ECDSA) ? this.signature.verify(SignatureECDSA.convertXMLDSIGtoASN1(paramArrayOfByte)) : this.signature.verify(paramArrayOfByte);
    } catch (IOException iOException) {
      throw new XMLSignatureException(iOException);
    } 
  }
  
  private static void checkKeySize(XMLCryptoContext paramXMLCryptoContext, Key paramKey) throws XMLSignatureException {
    if (Utils.secureValidation(paramXMLCryptoContext)) {
      int i = KeyUtil.getKeySize(paramKey);
      if (i == -1) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Size for " + paramKey.getAlgorithm() + " key cannot be determined"); 
        return;
      } 
      if (Policy.restrictKey(paramKey.getAlgorithm(), i))
        throw new XMLSignatureException(paramKey.getAlgorithm() + " keys less than " + Policy.minKeySize(paramKey.getAlgorithm()) + " bits are forbidden when secure validation is enabled"); 
    } 
  }
  
  byte[] sign(Key paramKey, SignedInfo paramSignedInfo, XMLSignContext paramXMLSignContext) throws InvalidKeyException, XMLSignatureException {
    if (paramKey == null || paramSignedInfo == null)
      throw new NullPointerException(); 
    if (!(paramKey instanceof PrivateKey))
      throw new InvalidKeyException("key must be PrivateKey"); 
    checkKeySize(paramXMLSignContext, paramKey);
    if (this.signature == null)
      try {
        Provider provider = (Provider)paramXMLSignContext.getProperty("org.jcp.xml.dsig.internal.dom.SignatureProvider");
        this.signature = (provider == null) ? Signature.getInstance(getJCAAlgorithm()) : Signature.getInstance(getJCAAlgorithm(), provider);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new XMLSignatureException(noSuchAlgorithmException);
      }  
    this.signature.initSign((PrivateKey)paramKey);
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Signature provider:" + this.signature.getProvider());
      log.log(Level.FINE, "Signing with key: " + paramKey);
    } 
    ((DOMSignedInfo)paramSignedInfo).canonicalize(paramXMLSignContext, new SignerOutputStream(this.signature));
    try {
      AbstractDOMSignatureMethod.Type type = getAlgorithmType();
      if (type == AbstractDOMSignatureMethod.Type.DSA) {
        int i = ((DSAKey)paramKey).getParams().getQ().bitLength();
        return JavaUtils.convertDsaASN1toXMLDSIG(this.signature.sign(), i / 8);
      } 
      return (type == AbstractDOMSignatureMethod.Type.ECDSA) ? SignatureECDSA.convertASN1toXMLDSIG(this.signature.sign()) : this.signature.sign();
    } catch (SignatureException signatureException) {
      throw new XMLSignatureException(signatureException);
    } catch (IOException iOException) {
      throw new XMLSignatureException(iOException);
    } 
  }
  
  static final class SHA1withDSA extends DOMSignatureMethod {
    SHA1withDSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA1withDSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2000/09/xmldsig#dsa-sha1"; }
    
    String getJCAAlgorithm() { return "SHA1withDSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.DSA; }
  }
  
  static final class SHA1withECDSA extends DOMSignatureMethod {
    SHA1withECDSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA1withECDSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"; }
    
    String getJCAAlgorithm() { return "SHA1withECDSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.ECDSA; }
  }
  
  static final class SHA1withRSA extends DOMSignatureMethod {
    SHA1withRSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA1withRSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2000/09/xmldsig#rsa-sha1"; }
    
    String getJCAAlgorithm() { return "SHA1withRSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.RSA; }
  }
  
  static final class SHA256withDSA extends DOMSignatureMethod {
    SHA256withDSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA256withDSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2009/xmldsig11#dsa-sha256"; }
    
    String getJCAAlgorithm() { return "SHA256withDSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.DSA; }
  }
  
  static final class SHA256withECDSA extends DOMSignatureMethod {
    SHA256withECDSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA256withECDSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"; }
    
    String getJCAAlgorithm() { return "SHA256withECDSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.ECDSA; }
  }
  
  static final class SHA256withRSA extends DOMSignatureMethod {
    SHA256withRSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA256withRSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"; }
    
    String getJCAAlgorithm() { return "SHA256withRSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.RSA; }
  }
  
  static final class SHA384withECDSA extends DOMSignatureMethod {
    SHA384withECDSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA384withECDSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384"; }
    
    String getJCAAlgorithm() { return "SHA384withECDSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.ECDSA; }
  }
  
  static final class SHA384withRSA extends DOMSignatureMethod {
    SHA384withRSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA384withRSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384"; }
    
    String getJCAAlgorithm() { return "SHA384withRSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.RSA; }
  }
  
  static final class SHA512withECDSA extends DOMSignatureMethod {
    SHA512withECDSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA512withECDSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512"; }
    
    String getJCAAlgorithm() { return "SHA512withECDSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.ECDSA; }
  }
  
  static final class SHA512withRSA extends DOMSignatureMethod {
    SHA512withRSA(AlgorithmParameterSpec param1AlgorithmParameterSpec) throws InvalidAlgorithmParameterException { super(param1AlgorithmParameterSpec); }
    
    SHA512withRSA(Element param1Element) throws MarshalException { super(param1Element); }
    
    public String getAlgorithm() { return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"; }
    
    String getJCAAlgorithm() { return "SHA512withRSA"; }
    
    AbstractDOMSignatureMethod.Type getAlgorithmType() { return AbstractDOMSignatureMethod.Type.RSA; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMSignatureMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */