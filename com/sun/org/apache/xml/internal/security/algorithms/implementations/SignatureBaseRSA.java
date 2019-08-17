package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SignatureBaseRSA extends SignatureAlgorithmSpi {
  private static Logger log = Logger.getLogger(SignatureBaseRSA.class.getName());
  
  private Signature signatureAlgorithm = null;
  
  public abstract String engineGetURI();
  
  public SignatureBaseRSA() throws XMLSignatureException {
    String str1 = JCEMapper.translateURItoJCEID(engineGetURI());
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Created SignatureRSA using " + str1); 
    String str2 = JCEMapper.getProviderId();
    try {
      if (str2 == null) {
        this.signatureAlgorithm = Signature.getInstance(str1);
      } else {
        this.signatureAlgorithm = Signature.getInstance(str1, str2);
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      Object[] arrayOfObject = { str1, noSuchAlgorithmException.getLocalizedMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject);
    } catch (NoSuchProviderException noSuchProviderException) {
      Object[] arrayOfObject = { str1, noSuchProviderException.getLocalizedMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject);
    } 
  }
  
  protected void engineSetParameter(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws XMLSignatureException {
    try {
      this.signatureAlgorithm.setParameter(paramAlgorithmParameterSpec);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      throw new XMLSignatureException("empty", invalidAlgorithmParameterException);
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfByte) throws XMLSignatureException {
    try {
      return this.signatureAlgorithm.verify(paramArrayOfByte);
    } catch (SignatureException signatureException) {
      throw new XMLSignatureException("empty", signatureException);
    } 
  }
  
  protected void engineInitVerify(Key paramKey) throws XMLSignatureException {
    if (!(paramKey instanceof PublicKey)) {
      String str1 = paramKey.getClass().getName();
      String str2 = PublicKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    } 
    try {
      this.signatureAlgorithm.initVerify((PublicKey)paramKey);
    } catch (InvalidKeyException invalidKeyException) {
      Signature signature = this.signatureAlgorithm;
      try {
        this.signatureAlgorithm = Signature.getInstance(this.signatureAlgorithm.getAlgorithm());
      } catch (Exception exception) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "Exception when reinstantiating Signature:" + exception); 
        this.signatureAlgorithm = signature;
      } 
      throw new XMLSignatureException("empty", invalidKeyException);
    } 
  }
  
  protected byte[] engineSign() throws XMLSignatureException {
    try {
      return this.signatureAlgorithm.sign();
    } catch (SignatureException signatureException) {
      throw new XMLSignatureException("empty", signatureException);
    } 
  }
  
  protected void engineInitSign(Key paramKey, SecureRandom paramSecureRandom) throws XMLSignatureException {
    if (!(paramKey instanceof PrivateKey)) {
      String str1 = paramKey.getClass().getName();
      String str2 = PrivateKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    } 
    try {
      this.signatureAlgorithm.initSign((PrivateKey)paramKey, paramSecureRandom);
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLSignatureException("empty", invalidKeyException);
    } 
  }
  
  protected void engineInitSign(Key paramKey) throws XMLSignatureException {
    if (!(paramKey instanceof PrivateKey)) {
      String str1 = paramKey.getClass().getName();
      String str2 = PrivateKey.class.getName();
      Object[] arrayOfObject = { str1, str2 };
      throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", arrayOfObject);
    } 
    try {
      this.signatureAlgorithm.initSign((PrivateKey)paramKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new XMLSignatureException("empty", invalidKeyException);
    } 
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte) throws XMLSignatureException {
    try {
      this.signatureAlgorithm.update(paramArrayOfByte);
    } catch (SignatureException signatureException) {
      throw new XMLSignatureException("empty", signatureException);
    } 
  }
  
  protected void engineUpdate(byte paramByte) throws XMLSignatureException {
    try {
      this.signatureAlgorithm.update(paramByte);
    } catch (SignatureException signatureException) {
      throw new XMLSignatureException("empty", signatureException);
    } 
  }
  
  protected void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws XMLSignatureException {
    try {
      this.signatureAlgorithm.update(paramArrayOfByte, paramInt1, paramInt2);
    } catch (SignatureException signatureException) {
      throw new XMLSignatureException("empty", signatureException);
    } 
  }
  
  protected String engineGetJCEAlgorithmString() { return this.signatureAlgorithm.getAlgorithm(); }
  
  protected String engineGetJCEProviderName() { return this.signatureAlgorithm.getProvider().getName(); }
  
  protected void engineSetHMACOutputLength(int paramInt) throws XMLSignatureException { throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC"); }
  
  protected void engineInitSign(Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws XMLSignatureException { throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA"); }
  
  public static class SignatureRSAMD5 extends SignatureBaseRSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#rsa-md5"; }
  }
  
  public static class SignatureRSARIPEMD160 extends SignatureBaseRSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160"; }
  }
  
  public static class SignatureRSASHA1 extends SignatureBaseRSA {
    public String engineGetURI() { return "http://www.w3.org/2000/09/xmldsig#rsa-sha1"; }
  }
  
  public static class SignatureRSASHA256 extends SignatureBaseRSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256"; }
  }
  
  public static class SignatureRSASHA384 extends SignatureBaseRSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384"; }
  }
  
  public static class SignatureRSASHA512 extends SignatureBaseRSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\implementations\SignatureBaseRSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */