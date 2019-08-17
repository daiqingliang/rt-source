package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.X509Data;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509IssuerSerial;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509IssuerSerialResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(X509IssuerSerialResolver.class.getName());
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    X509Certificate x509Certificate = engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver);
    return (x509Certificate != null) ? x509Certificate.getPublicKey() : null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?"); 
    X509Data x509Data = null;
    try {
      x509Data = new X509Data(paramElement, paramString);
    } catch (XMLSignatureException xMLSignatureException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I can't"); 
      return null;
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I can't"); 
      return null;
    } 
    if (!x509Data.containsIssuerSerial())
      return null; 
    try {
      if (paramStorageResolver == null) {
        Object[] arrayOfObject = { "X509IssuerSerial" };
        KeyResolverException keyResolverException = new KeyResolverException("KeyResolver.needStorageResolver", arrayOfObject);
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "", keyResolverException); 
        throw keyResolverException;
      } 
      int i = x509Data.lengthIssuerSerial();
      Iterator iterator = paramStorageResolver.getIterator();
      while (iterator.hasNext()) {
        X509Certificate x509Certificate = (X509Certificate)iterator.next();
        XMLX509IssuerSerial xMLX509IssuerSerial = new XMLX509IssuerSerial(paramElement.getOwnerDocument(), x509Certificate);
        if (log.isLoggable(Level.FINE)) {
          log.log(Level.FINE, "Found Certificate Issuer: " + xMLX509IssuerSerial.getIssuerName());
          log.log(Level.FINE, "Found Certificate Serial: " + xMLX509IssuerSerial.getSerialNumber().toString());
        } 
        for (byte b = 0; b < i; b++) {
          XMLX509IssuerSerial xMLX509IssuerSerial1 = x509Data.itemIssuerSerial(b);
          if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Found Element Issuer:     " + xMLX509IssuerSerial1.getIssuerName());
            log.log(Level.FINE, "Found Element Serial:     " + xMLX509IssuerSerial1.getSerialNumber().toString());
          } 
          if (xMLX509IssuerSerial.equals(xMLX509IssuerSerial1)) {
            if (log.isLoggable(Level.FINE))
              log.log(Level.FINE, "match !!! "); 
            return x509Certificate;
          } 
          if (log.isLoggable(Level.FINE))
            log.log(Level.FINE, "no match..."); 
        } 
      } 
      return null;
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
      throw new KeyResolverException("generic.EmptyMessage", xMLSecurityException);
    } 
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\X509IssuerSerialResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */