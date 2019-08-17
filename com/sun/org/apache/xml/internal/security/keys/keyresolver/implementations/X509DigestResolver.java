package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Digest;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509DigestResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(X509DigestResolver.class.getName());
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver) {
    if (XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data"))
      try {
        X509Data x509Data = new X509Data(paramElement, paramString);
        return x509Data.containsDigest();
      } catch (XMLSecurityException xMLSecurityException) {
        return false;
      }  
    return false;
  }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    X509Certificate x509Certificate = engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver);
    return (x509Certificate != null) ? x509Certificate.getPublicKey() : null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName()); 
    if (!engineCanResolve(paramElement, paramString, paramStorageResolver))
      return null; 
    try {
      return resolveCertificate(paramElement, paramString, paramStorageResolver);
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
      return null;
    } 
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
  
  private X509Certificate resolveCertificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    XMLX509Digest[] arrayOfXMLX509Digest = null;
    Element[] arrayOfElement = XMLUtils.selectDs11Nodes(paramElement.getFirstChild(), "X509Digest");
    if (arrayOfElement == null || arrayOfElement.length <= 0)
      return null; 
    try {
      checkStorage(paramStorageResolver);
      arrayOfXMLX509Digest = new XMLX509Digest[arrayOfElement.length];
      for (byte b = 0; b < arrayOfElement.length; b++)
        arrayOfXMLX509Digest[b] = new XMLX509Digest(arrayOfElement[b], paramString); 
      Iterator iterator = paramStorageResolver.getIterator();
      while (iterator.hasNext()) {
        X509Certificate x509Certificate = (X509Certificate)iterator.next();
        for (byte b1 = 0; b1 < arrayOfXMLX509Digest.length; b1++) {
          XMLX509Digest xMLX509Digest;
          byte[] arrayOfByte = (xMLX509Digest = arrayOfXMLX509Digest[b1]).getDigestBytesFromCert(x509Certificate, xMLX509Digest.getAlgorithm());
          if (Arrays.equals(xMLX509Digest.getDigestBytes(), arrayOfByte)) {
            if (log.isLoggable(Level.FINE))
              log.log(Level.FINE, "Found certificate with: " + x509Certificate.getSubjectX500Principal().getName()); 
            return x509Certificate;
          } 
        } 
      } 
    } catch (XMLSecurityException xMLSecurityException) {
      throw new KeyResolverException("empty", xMLSecurityException);
    } 
    return null;
  }
  
  private void checkStorage(StorageResolver paramStorageResolver) throws KeyResolverException {
    if (paramStorageResolver == null) {
      Object[] arrayOfObject = { "X509Digest" };
      KeyResolverException keyResolverException = new KeyResolverException("KeyResolver.needStorageResolver", arrayOfObject);
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "", keyResolverException); 
      throw keyResolverException;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\X509DigestResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */