package com.sun.org.apache.xml.internal.security.algorithms.implementations;

import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithmSpi;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.IOException;
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

public abstract class SignatureECDSA extends SignatureAlgorithmSpi {
  private static Logger log = Logger.getLogger(SignatureECDSA.class.getName());
  
  private Signature signatureAlgorithm = null;
  
  public abstract String engineGetURI();
  
  public static byte[] convertASN1toXMLDSIG(byte[] paramArrayOfByte) throws IOException {
    int i;
    if (paramArrayOfByte.length < 8 || paramArrayOfByte[0] != 48)
      throw new IOException("Invalid ASN.1 format of ECDSA signature"); 
    if (paramArrayOfByte[1] > 0) {
      i = 2;
    } else if (paramArrayOfByte[1] == -127) {
      i = 3;
    } else {
      throw new IOException("Invalid ASN.1 format of ECDSA signature");
    } 
    byte b1 = paramArrayOfByte[i + 1];
    int j;
    for (j = b1; j > 0 && paramArrayOfByte[i + 2 + b1 - j] == 0; j--);
    byte b2 = paramArrayOfByte[i + 2 + b1 + 1];
    int k;
    for (k = b2; k > 0 && paramArrayOfByte[i + 2 + b1 + 2 + b2 - k] == 0; k--);
    int m = Math.max(j, k);
    if ((paramArrayOfByte[i - 1] & 0xFF) != paramArrayOfByte.length - i || (paramArrayOfByte[i - 1] & 0xFF) != 2 + b1 + 2 + b2 || paramArrayOfByte[i] != 2 || paramArrayOfByte[i + 2 + b1] != 2)
      throw new IOException("Invalid ASN.1 format of ECDSA signature"); 
    byte[] arrayOfByte = new byte[2 * m];
    System.arraycopy(paramArrayOfByte, i + 2 + b1 - j, arrayOfByte, m - j, j);
    System.arraycopy(paramArrayOfByte, i + 2 + b1 + 2 + b2 - k, arrayOfByte, 2 * m - k, k);
    return arrayOfByte;
  }
  
  public static byte[] convertXMLDSIGtoASN1(byte[] paramArrayOfByte) throws IOException {
    byte[] arrayOfByte;
    int i2;
    int i = paramArrayOfByte.length / 2;
    int j;
    for (j = i; j > 0 && paramArrayOfByte[i - j] == 0; j--);
    int k = j;
    if (paramArrayOfByte[i - j] < 0)
      k++; 
    int m;
    for (m = i; m > 0 && paramArrayOfByte[2 * i - m] == 0; m--);
    int n = m;
    if (paramArrayOfByte[2 * i - m] < 0)
      n++; 
    int i1 = 2 + k + 2 + n;
    if (i1 > 255)
      throw new IOException("Invalid XMLDSIG format of ECDSA signature"); 
    if (i1 < 128) {
      arrayOfByte = new byte[4 + k + 2 + n];
      i2 = 1;
    } else {
      arrayOfByte = new byte[5 + k + 2 + n];
      arrayOfByte[1] = -127;
      i2 = 2;
    } 
    arrayOfByte[0] = 48;
    arrayOfByte[i2++] = (byte)i1;
    arrayOfByte[i2++] = 2;
    arrayOfByte[i2++] = (byte)k;
    System.arraycopy(paramArrayOfByte, i - j, arrayOfByte, i2 + k - j, j);
    i2 += k;
    arrayOfByte[i2++] = 2;
    arrayOfByte[i2++] = (byte)n;
    System.arraycopy(paramArrayOfByte, 2 * i - m, arrayOfByte, i2 + n - m, m);
    return arrayOfByte;
  }
  
  public SignatureECDSA() throws XMLSignatureException {
    String str1 = JCEMapper.translateURItoJCEID(engineGetURI());
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Created SignatureECDSA using " + str1); 
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
      byte[] arrayOfByte = convertXMLDSIGtoASN1(paramArrayOfByte);
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Called ECDSA.verify() on " + Base64.encode(paramArrayOfByte)); 
      return this.signatureAlgorithm.verify(arrayOfByte);
    } catch (SignatureException signatureException) {
      throw new XMLSignatureException("empty", signatureException);
    } catch (IOException iOException) {
      throw new XMLSignatureException("empty", iOException);
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
      byte[] arrayOfByte = this.signatureAlgorithm.sign();
      return convertASN1toXMLDSIG(arrayOfByte);
    } catch (SignatureException signatureException) {
      throw new XMLSignatureException("empty", signatureException);
    } catch (IOException iOException) {
      throw new XMLSignatureException("empty", iOException);
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
  
  public static class SignatureECDSASHA1 extends SignatureECDSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1"; }
  }
  
  public static class SignatureECDSASHA256 extends SignatureECDSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256"; }
  }
  
  public static class SignatureECDSASHA384 extends SignatureECDSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384"; }
  }
  
  public static class SignatureECDSASHA512 extends SignatureECDSA {
    public String engineGetURI() { return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\implementations\SignatureECDSA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */