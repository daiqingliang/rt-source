package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509SKI;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509SKIResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(X509SKIResolver.class.getName());
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    X509Certificate x509Certificate = engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver);
    return (x509Certificate != null) ? x509Certificate.getPublicKey() : null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?"); 
    if (!XMLUtils.elementIsInSignatureSpace(paramElement, "X509Data")) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I can't"); 
      return null;
    } 
    XMLX509SKI[] arrayOfXMLX509SKI = null;
    Element[] arrayOfElement = null;
    arrayOfElement = XMLUtils.selectDsNodes(paramElement.getFirstChild(), "X509SKI");
    if (arrayOfElement == null || arrayOfElement.length <= 0) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "I can't"); 
      return null;
    } 
    try {
      if (paramStorageResolver == null) {
        Object[] arrayOfObject = { "X509SKI" };
        KeyResolverException keyResolverException = new KeyResolverException("KeyResolver.needStorageResolver", arrayOfObject);
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "", keyResolverException); 
        throw keyResolverException;
      } 
      arrayOfXMLX509SKI = new XMLX509SKI[arrayOfElement.length];
      for (byte b = 0; b < arrayOfElement.length; b++)
        arrayOfXMLX509SKI[b] = new XMLX509SKI(arrayOfElement[b], paramString); 
      Iterator iterator = paramStorageResolver.getIterator();
      while (iterator.hasNext()) {
        X509Certificate x509Certificate = (X509Certificate)iterator.next();
        XMLX509SKI xMLX509SKI = new XMLX509SKI(paramElement.getOwnerDocument(), x509Certificate);
        for (byte b1 = 0; b1 < arrayOfXMLX509SKI.length; b1++) {
          if (xMLX509SKI.equals(arrayOfXMLX509SKI[b1])) {
            if (log.isLoggable(Level.FINE))
              log.log(Level.FINE, "Return PublicKey from " + x509Certificate.getSubjectX500Principal().getName()); 
            return x509Certificate;
          } 
        } 
      } 
    } catch (XMLSecurityException xMLSecurityException) {
      throw new KeyResolverException("empty", xMLSecurityException);
    } 
    return null;
  }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\X509SKIResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */