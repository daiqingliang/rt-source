package com.sun.org.apache.xml.internal.security.algorithms;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import org.w3c.dom.Document;

public class MessageDigestAlgorithm extends Algorithm {
  public static final String ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#md5";
  
  public static final String ALGO_ID_DIGEST_SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
  
  public static final String ALGO_ID_DIGEST_SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
  
  public static final String ALGO_ID_DIGEST_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#sha384";
  
  public static final String ALGO_ID_DIGEST_SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
  
  public static final String ALGO_ID_DIGEST_RIPEMD160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
  
  private final MessageDigest algorithm;
  
  private MessageDigestAlgorithm(Document paramDocument, String paramString) throws XMLSignatureException {
    super(paramDocument, paramString);
    this.algorithm = getDigestInstance(paramString);
  }
  
  public static MessageDigestAlgorithm getInstance(Document paramDocument, String paramString) throws XMLSignatureException { return new MessageDigestAlgorithm(paramDocument, paramString); }
  
  private static MessageDigest getDigestInstance(String paramString) throws XMLSignatureException {
    MessageDigest messageDigest;
    String str1 = JCEMapper.translateURItoJCEID(paramString);
    if (str1 == null) {
      messageDigest = new Object[] { paramString };
      throw new XMLSignatureException("algorithms.NoSuchMap", messageDigest);
    } 
    String str2 = JCEMapper.getProviderId();
    try {
      if (str2 == null) {
        messageDigest = MessageDigest.getInstance(str1);
      } else {
        messageDigest = MessageDigest.getInstance(str1, str2);
      } 
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      Object[] arrayOfObject = { str1, noSuchAlgorithmException.getLocalizedMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject);
    } catch (NoSuchProviderException noSuchProviderException) {
      Object[] arrayOfObject = { str1, noSuchProviderException.getLocalizedMessage() };
      throw new XMLSignatureException("algorithms.NoSuchAlgorithm", arrayOfObject);
    } 
    return messageDigest;
  }
  
  public MessageDigest getAlgorithm() { return this.algorithm; }
  
  public static boolean isEqual(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) { return MessageDigest.isEqual(paramArrayOfByte1, paramArrayOfByte2); }
  
  public byte[] digest() { return this.algorithm.digest(); }
  
  public byte[] digest(byte[] paramArrayOfByte) { return this.algorithm.digest(paramArrayOfByte); }
  
  public int digest(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws DigestException { return this.algorithm.digest(paramArrayOfByte, paramInt1, paramInt2); }
  
  public String getJCEAlgorithmString() { return this.algorithm.getAlgorithm(); }
  
  public Provider getJCEProvider() { return this.algorithm.getProvider(); }
  
  public int getDigestLength() { return this.algorithm.getDigestLength(); }
  
  public void reset() { this.algorithm.reset(); }
  
  public void update(byte[] paramArrayOfByte) { this.algorithm.update(paramArrayOfByte); }
  
  public void update(byte paramByte) { this.algorithm.update(paramByte); }
  
  public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.algorithm.update(paramArrayOfByte, paramInt1, paramInt2); }
  
  public String getBaseNamespace() { return "http://www.w3.org/2000/09/xmldsig#"; }
  
  public String getBaseLocalName() { return "DigestMethod"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\algorithms\MessageDigestAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */