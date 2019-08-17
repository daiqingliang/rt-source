package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignatureAlgorithm extends Algorithm {
  private static Logger log = Logger.getLogger(SignatureAlgorithm.class.getName());
  
  private static Map<String, Class<? extends SignatureAlgorithmSpi>> algorithmHash = new ConcurrentHashMap();
  
  private final SignatureAlgorithmSpi signatureAlgorithm;
  
  private final String algorithmURI;
  
  public SignatureAlgorithm(Document paramDocument, String paramString) throws XMLSecurityException {
    super(paramDocument, paramString);
    this.algorithmURI = paramString;
    this.signatureAlgorithm = getSignatureAlgorithmSpi(paramString);
    this.signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
  }
  
  public SignatureAlgorithm(Document paramDocument, String paramString, int paramInt) throws XMLSecurityException {
    super(paramDocument, paramString);
    this.algorithmURI = paramString;
    this.signatureAlgorithm = getSignatureAlgorithmSpi(paramString);
    this.signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
    this.signatureAlgorithm.engineSetHMACOutputLength(paramInt);
    ((IntegrityHmac)this.signatureAlgorithm).engineAddContextToElement(this.constructionElement);
  }
  
  public SignatureAlgorithm(Element paramElement, String paramString) throws XMLSecurityException { this(paramElement, paramString, false); }
  
  public SignatureAlgorithm(Element paramElement, String paramString, boolean paramBoolean) throws XMLSecurityException {
    super(paramElement, paramString);
    this.algorithmURI = getURI();
    Attr attr = paramElement.getAttributeNodeNS(null, "Id");
    if (attr != null)
      paramElement.setIdAttributeNode(attr, true); 
    if (paramBoolean && ("http://www.w3.org/2001/04/xmldsig-more#hmac-md5".equals(this.algorithmURI) || "http://www.w3.org/2001/04/xmldsig-more#rsa-md5".equals(this.algorithmURI))) {
      Object[] arrayOfObject = { this.algorithmURI };
      throw new XMLSecurityException("signature.signatureAlgorithm", arrayOfObject);
    } 
    this.signatureAlgorithm = getSignatureAlgorithmSpi(this.algorithmURI);
    this.signatureAlgorithm.engineGetContextFromElement(this.constructionElement);
  }
  
  private static SignatureAlgorithmSpi getSignatureAlgorithmSpi(String paramString) throws XMLSignatureException {
    try {
      Class clazz = (Class)algorithmHash.get(paramString);
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Create URI \"" + paramString + "\" class \"" + clazz + "\""); 
      return (SignatureAlgorithmSpi)clazz.newInstance();
    } catch (IllegalAccessException illegalAccessException) {
      Object[] arrayOfObject = { paramString, illegalAccessException.getMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, illegalAccessException);
    } catch (InstantiationException instantiationException) {
      Object[] arrayOfObject = { paramString, instantiationException.getMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, instantiationException);
    } catch (NullPointerException nullPointerException) {
      Object[] arrayOfObject = { paramString, nullPointerException.getMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, nullPointerException);
    } 
  }
  
  public byte[] sign() throws XMLSignatureException { return this.signatureAlgorithm.engineSign(); }
  
  public String getJCEAlgorithmString() { return this.signatureAlgorithm.engineGetJCEAlgorithmString(); }
  
  public String getJCEProviderName() { return this.signatureAlgorithm.engineGetJCEProviderName(); }
  
  public void update(byte[] paramArrayOfByte) throws XMLSignatureException { this.signatureAlgorithm.engineUpdate(paramArrayOfByte); }
  
  public void update(byte paramByte) throws XMLSignatureException { this.signatureAlgorithm.engineUpdate(paramByte); }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws XMLSignatureException { this.signatureAlgorithm.engineUpdate(paramArrayOfByte, paramInt1, paramInt2); }
  
  public void initSign(Key paramKey) throws XMLSignatureException { this.signatureAlgorithm.engineInitSign(paramKey); }
  
  public void initSign(Key paramKey, SecureRandom paramSecureRandom) throws XMLSignatureException { this.signatureAlgorithm.engineInitSign(paramKey, paramSecureRandom); }
  
  public void initSign(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws XMLSignatureException { this.signatureAlgorithm.engineInitSign(paramKey, paramAlgorithmParameterSpec); }
  
  public void setParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws XMLSignatureException { this.signatureAlgorithm.engineSetParameter(paramAlgorithmParameterSpec); }
  
  public void initVerify(Key paramKey) throws XMLSignatureException { this.signatureAlgorithm.engineInitVerify(paramKey); }
  
  public boolean verify(byte[] paramArrayOfByte) throws XMLSignatureException { return this.signatureAlgorithm.engineVerify(paramArrayOfByte); }
  
  public final String getURI() { return this.constructionElement.getAttributeNS(null, "Algorithm"); }
  
  public static void register(String paramString1, String paramString2) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
    JavaUtils.checkRegisterPermission();
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Try to register " + paramString1 + " " + paramString2); 
    Class clazz = (Class)algorithmHash.get(paramString1);
    if (clazz != null) {
      Object[] arrayOfObject = { paramString1, clazz };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
    } 
    try {
      Class clazz1 = ClassLoaderUtils.loadClass(paramString2, SignatureAlgorithm.class);
      algorithmHash.put(paramString1, clazz1);
    } catch (NullPointerException nullPointerException) {
      Object[] arrayOfObject = { paramString1, nullPointerException.getMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject, nullPointerException);
    } 
  }
  
  public static void register(String paramString, Class<? extends SignatureAlgorithmSpi> paramClass) throws AlgorithmAlreadyRegisteredException, ClassNotFoundException, XMLSignatureException {
    JavaUtils.checkRegisterPermission();
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Try to register " + paramString + " " + paramClass); 
    Class clazz = (Class)algorithmHash.get(paramString);
    if (clazz != null) {
      Object[] arrayOfObject = { paramString, clazz };
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered", arrayOfObject);
    } 
    algorithmHash.put(paramString, paramClass);
  }
  
  public static void registerDefaultAlgorithms() {
    algorithmHash.put("http://www.w3.org/2000/09/xmldsig#dsa-sha1", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA.class);
    algorithmHash.put("http://www.w3.org/2009/xmldsig11#dsa-sha256", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureDSA.SHA256.class);
    algorithmHash.put("http://www.w3.org/2000/09/xmldsig#rsa-sha1", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSASHA1.class);
    algorithmHash.put("http://www.w3.org/2000/09/xmldsig#hmac-sha1", IntegrityHmac.IntegrityHmacSHA1.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-md5", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSAMD5.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSARIPEMD160.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSASHA256.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSASHA384.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureBaseRSA.SignatureRSASHA512.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA.SignatureECDSASHA1.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA.SignatureECDSASHA256.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA.SignatureECDSASHA384.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512", com.sun.org.apache.xml.internal.security.algorithms.implementations.SignatureECDSA.SignatureECDSASHA512.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-md5", IntegrityHmac.IntegrityHmacMD5.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160", IntegrityHmac.IntegrityHmacRIPEMD160.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha256", IntegrityHmac.IntegrityHmacSHA256.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha384", IntegrityHmac.IntegrityHmacSHA384.class);
    algorithmHash.put("http://www.w3.org/2001/04/xmldsig-more#hmac-sha512", IntegrityHmac.IntegrityHmacSHA512.class);
  }
  
  public String getBaseNamespace() { return "http://www.w3.org/2000/09/xmldsig#"; }
  
  public String getBaseLocalName() { return "SignatureMethod"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\SignatureAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */