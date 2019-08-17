package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Signature11ElementProxy;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DEREncodedKeyValue extends Signature11ElementProxy implements KeyInfoContent {
  private static final String[] supportedKeyTypes = { "RSA", "DSA", "EC" };
  
  public DEREncodedKeyValue(Element paramElement, String paramString) throws XMLSecurityException { super(paramElement, paramString); }
  
  public DEREncodedKeyValue(Document paramDocument, PublicKey paramPublicKey) throws XMLSecurityException {
    super(paramDocument);
    addBase64Text(getEncodedDER(paramPublicKey));
  }
  
  public DEREncodedKeyValue(Document paramDocument, byte[] paramArrayOfByte) {
    super(paramDocument);
    addBase64Text(paramArrayOfByte);
  }
  
  public void setId(String paramString) {
    if (paramString != null) {
      this.constructionElement.setAttributeNS(null, "Id", paramString);
      this.constructionElement.setIdAttributeNS(null, "Id", true);
    } else {
      this.constructionElement.removeAttributeNS(null, "Id");
    } 
  }
  
  public String getId() { return this.constructionElement.getAttributeNS(null, "Id"); }
  
  public String getBaseLocalName() { return "DEREncodedKeyValue"; }
  
  public PublicKey getPublicKey() throws XMLSecurityException {
    byte[] arrayOfByte = getBytesFromTextChild();
    for (String str : supportedKeyTypes) {
      try {
        KeyFactory keyFactory = KeyFactory.getInstance(str);
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(arrayOfByte);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        if (publicKey != null)
          return publicKey; 
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      
      } catch (InvalidKeySpecException invalidKeySpecException) {}
    } 
    throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedEncodedKey");
  }
  
  protected byte[] getEncodedDER(PublicKey paramPublicKey) throws XMLSecurityException {
    try {
      KeyFactory keyFactory = KeyFactory.getInstance(paramPublicKey.getAlgorithm());
      X509EncodedKeySpec x509EncodedKeySpec = (X509EncodedKeySpec)keyFactory.getKeySpec(paramPublicKey, X509EncodedKeySpec.class);
      return x509EncodedKeySpec.getEncoded();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      Object[] arrayOfObject = { paramPublicKey.getAlgorithm(), paramPublicKey.getFormat(), paramPublicKey.getClass().getName() };
      throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedPublicKey", arrayOfObject, noSuchAlgorithmException);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      Object[] arrayOfObject = { paramPublicKey.getAlgorithm(), paramPublicKey.getFormat(), paramPublicKey.getClass().getName() };
      throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedPublicKey", arrayOfObject, invalidKeySpecException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\content\DEREncodedKeyValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */