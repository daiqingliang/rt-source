package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.MessageDigestAlgorithm;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class IntegrityHmac extends SignatureAlgorithmSpi {
  private static Logger log = Logger.getLogger(IntegrityHmac.class.getName());
  
  private Mac macAlgorithm = null;
  
  private int HMACOutputLength = 0;
  
  private boolean HMACOutputLengthSet = false;
  
  public abstract String engineGetURI();
  
  abstract int getDigestLength();
  
  public IntegrityHmac() throws XMLSignatureException {
    String str = JCEMapper.translateURItoJCEID(engineGetURI());
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Created IntegrityHmacSHA1 using " + str); 
    try {
      this.macAlgorithm = Mac.getInstance(str);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      Object[] arrayOfObject = { str, noSuchAlgorithmException.getLocalizedMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject);
    } 
  }
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws XMLSignatureException { throw new XMLSignatureException("empty"); }
  
  public void reset() throws XMLSignatureException {
    this.HMACOutputLength = 0;
    this.HMACOutputLengthSet = false;
    this.macAlgorithm.reset();
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte) throws XMLSignatureException {
    try {
      if (this.HMACOutputLengthSet && this.HMACOutputLength < getDigestLength()) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "HMACOutputLength must not be less than " + getDigestLength()); 
        Object[] arrayOfObject = { String.valueOf(getDigestLength()) };
        throw new XMLSignatureException("algorithms.HMACOutputLengthMin", arrayOfObject);
      } 
      byte[] arrayOfByte = this.macAlgorithm.doFinal();
      return MessageDigestAlgorithm.isEqual(arrayOfByte, paramArrayOfByte);
    } catch (IllegalStateException illegalStateException) {
      throw new XMLSignatureException("empty", illegalStateException);
    } 
  }
  
  protected void engineInitVerify(Key paramKey) throws XMLSignatureException {
    if (!(paramKey instanceof javax.crypto.SecretKey)) {
      String str1 = paramKey.getClass().getName();
      String str2 = javax.crypto.SecretKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    } 
    try {
      this.macAlgorithm.init(paramKey);
    } catch (InvalidKeyException invalidKeyException) {
      Mac mac = this.macAlgorithm;
      try {
        this.macAlgorithm = Mac.getInstance(this.macAlgorithm.getAlgorithm());
      } catch (Exception exception) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Exception when reinstantiating Mac:" + exception); 
        this.macAlgorithm = mac;
      } 
      throw new XMLSignatureException("empty", invalidKeyException);
    } 
  }
  
  protected byte[] engineSign() throws XMLSignatureException {
    try {
      if (this.HMACOutputLengthSet && this.HMACOutputLength < getDigestLength()) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "HMACOutputLength must not be less than " + getDigestLength()); 
        Object[] arrayOfObject = { String.valueOf(getDigestLength()) };
        throw new XMLSignatureException("algorithms.HMACOutputLengthMin", arrayOfObject);
      } 
      return this.macAlgorithm.doFinal();
    } catch (IllegalStateException illegalStateException) {
      throw new XMLSignatureException("empty", illegalStateException);
    } 
  }
  
  protected void engineInitSign(Key paramKey) throws XMLSignatureException {
    if (!(paramKey instanceof javax.crypto.SecretKey)) {
      String str1 = paramKey.getClass().getName();
      String str2 = javax.crypto.SecretKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    } 
    try {
      this.macAlgorithm.init(paramKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLSignatureException("empty", invalidKeyException);
    } 
  }
  
  protected void engineInitSign(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws XMLSignatureException {
    if (!(paramKey instanceof javax.crypto.SecretKey)) {
      String str1 = paramKey.getClass().getName();
      String str2 = javax.crypto.SecretKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    } 
    try {
      this.macAlgorithm.init(paramKey, paramAlgorithmParameterSpec);
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLSignatureException("empty", invalidKeyException);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new XMLSignatureException("empty", invalidAlgorithmParameterException);
    } 
  }
  
  protected void engineInitSign(Key paramKey, SecureRandom paramSecureRandom) throws XMLSignatureException { throw new XMLSignatureException("algorithms.CannotUseSecureRandomOnMAC"); }
  
  protected void engineUpdate(byte[] paramArrayOfByte) throws XMLSignatureException {
    try {
      this.macAlgorithm.update(paramArrayOfByte);
    } catch (IllegalStateException illegalStateException) {
      throw new XMLSignatureException("empty", illegalStateException);
    } 
  }
  
  protected void engineUpdate(byte paramByte) throws XMLSignatureException {
    try {
      this.macAlgorithm.update(paramByte);
    } catch (IllegalStateException illegalStateException) {
      throw new XMLSignatureException("empty", illegalStateException);
    } 
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws XMLSignatureException {
    try {
      this.macAlgorithm.update(paramArrayOfByte, paramInt1, paramInt2);
    } catch (IllegalStateException illegalStateException) {
      throw new XMLSignatureException("empty", illegalStateException);
    } 
  }
  
  protected String engineGetJCEAlgorithmString() { return this.macAlgorithm.getAlgorithm(); }
  
  protected String engineGetJCEProviderName() { return this.macAlgorithm.getProvider().getName(); }
  
  protected void engineSetHMACOutputLength(int paramInt) {
    this.HMACOutputLength = paramInt;
    this.HMACOutputLengthSet = true;
  }
  
  protected void engineGetContextFromElement(Element paramElement) {
    super.engineGetContextFromElement(paramElement);
    if (paramElement == null)
      throw new IllegalArgumentException("element null"); 
    Text text = XMLUtils.selectDsNodeText(paramElement.getFirstChild(), "HMACOutputLength", 0);
    if (text != null) {
      this.HMACOutputLength = Integer.parseInt(text.getData());
      this.HMACOutputLengthSet = true;
    } 
  }
  
  public void engineAddContextToElement(Element paramElement) {
    if (paramElement == null)
      throw new IllegalArgumentException("null element"); 
    if (this.HMACOutputLengthSet) {
      Document document = paramElement.getOwnerDocument();
      Element element = XMLUtils.createElementInSignatureSpace(document, "HMACOutputLength");
      Text text = document.createTextNode(Integer.valueOf(this.HMACOutputLength).toString());
      element.appendChild(text);
      XMLUtils.addReturnToElement(paramElement);
      paramElement.appendChild(element);
      XMLUtils.addReturnToElement(paramElement);
    } 
  }
  
  public static class IntegrityHmacMD5 extends IntegrityHmac {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#hmac-md5"; }
    
    int getDigestLength() { return 128; }
  }
  
  public static class IntegrityHmacRIPEMD160 extends IntegrityHmac {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160"; }
    
    int getDigestLength() { return 160; }
  }
  
  public static class IntegrityHmacSHA1 extends IntegrityHmac {
    public String engineGetURI() { return "http://www.w3.org/2000/09/xmldsig#hmac-sha1"; }
    
    int getDigestLength() { return 160; }
  }
  
  public static class IntegrityHmacSHA256 extends IntegrityHmac {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256"; }
    
    int getDigestLength() { return 256; }
  }
  
  public static class IntegrityHmacSHA384 extends IntegrityHmac {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384"; }
    
    int getDigestLength() { return 384; }
  }
  
  public static class IntegrityHmacSHA512 extends IntegrityHmac {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512"; }
    
    int getDigestLength() { return 512; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\implementations\IntegrityHmac.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */