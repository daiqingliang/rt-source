package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolver;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public class XMLCipherInput {
  private static Logger logger = Logger.getLogger(XMLCipherInput.class.getName());
  
  private CipherData cipherData;
  
  private int mode;
  
  private boolean secureValidation;
  
  public XMLCipherInput(CipherData paramCipherData) throws XMLEncryptionException {
    this.cipherData = paramCipherData;
    this.mode = 2;
    if (this.cipherData == null)
      throw new XMLEncryptionException("CipherData is null"); 
  }
  
  public XMLCipherInput(EncryptedType paramEncryptedType) throws XMLEncryptionException {
    this.cipherData = (paramEncryptedType == null) ? null : paramEncryptedType.getCipherData();
    this.mode = 2;
    if (this.cipherData == null)
      throw new XMLEncryptionException("CipherData is null"); 
  }
  
  public void setSecureValidation(boolean paramBoolean) { this.secureValidation = paramBoolean; }
  
  public byte[] getBytes() throws XMLEncryptionException { return (this.mode == 2) ? getDecryptBytes() : null; }
  
  private byte[] getDecryptBytes() throws XMLEncryptionException {
    String str = null;
    if (this.cipherData.getDataType() == 2) {
      if (logger.isLoggable(Level.FINE))
        logger.log(Level.FINE, "Found a reference type CipherData"); 
      CipherReference cipherReference = this.cipherData.getCipherReference();
      Attr attr = cipherReference.getURIAsAttr();
      XMLSignatureInput xMLSignatureInput = null;
      try {
        ResourceResolver resourceResolver = ResourceResolver.getInstance(attr, null, this.secureValidation);
        xMLSignatureInput = resourceResolver.resolve(attr, null, this.secureValidation);
      } catch (ResourceResolverException resourceResolverException) {
        throw new XMLEncryptionException("empty", resourceResolverException);
      } 
      if (xMLSignatureInput != null) {
        if (logger.isLoggable(Level.FINE))
          logger.log(Level.FINE, "Managed to resolve URI \"" + cipherReference.getURI() + "\""); 
      } else if (logger.isLoggable(Level.FINE)) {
        logger.log(Level.FINE, "Failed to resolve URI \"" + cipherReference.getURI() + "\"");
      } 
      Transforms transforms = cipherReference.getTransforms();
      if (transforms != null) {
        if (logger.isLoggable(Level.FINE))
          logger.log(Level.FINE, "Have transforms in cipher reference"); 
        try {
          Transforms transforms1 = transforms.getDSTransforms();
          transforms1.setSecureValidation(this.secureValidation);
          xMLSignatureInput = transforms1.performTransforms(xMLSignatureInput);
        } catch (TransformationException transformationException) {
          throw new XMLEncryptionException("empty", transformationException);
        } 
      } 
      try {
        return xMLSignatureInput.getBytes();
      } catch (IOException iOException) {
        throw new XMLEncryptionException("empty", iOException);
      } catch (CanonicalizationException canonicalizationException) {
        throw new XMLEncryptionException("empty", canonicalizationException);
      } 
    } 
    if (this.cipherData.getDataType() == 1) {
      str = this.cipherData.getCipherValue().getValue();
    } else {
      throw new XMLEncryptionException("CipherData.getDataType() returned unexpected value");
    } 
    if (logger.isLoggable(Level.FINE))
      logger.log(Level.FINE, "Encrypted octets:\n" + str); 
    try {
      return Base64.decode(str);
    } catch (Base64DecodingException base64DecodingException) {
      throw new XMLEncryptionException("empty", base64DecodingException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\XMLCipherInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */