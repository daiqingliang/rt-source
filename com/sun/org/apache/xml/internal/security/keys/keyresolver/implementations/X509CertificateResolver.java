package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.x509.XMLX509Certificate;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class X509CertificateResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(X509CertificateResolver.class.getName());
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    X509Certificate x509Certificate = engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver);
    return (x509Certificate != null) ? x509Certificate.getPublicKey() : null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    try {
      Element[] arrayOfElement = XMLUtils.selectDsNodes(paramElement.getFirstChild(), "X509Certificate");
      if (arrayOfElement == null || arrayOfElement.length == 0) {
        Element element = XMLUtils.selectDsNode(paramElement.getFirstChild(), "X509Data", 0);
        return (element != null) ? engineLookupResolveX509Certificate(element, paramString, paramStorageResolver) : null;
      } 
      for (byte b = 0; b < arrayOfElement.length; b++) {
        XMLX509Certificate xMLX509Certificate = new XMLX509Certificate(arrayOfElement[b], paramString);
        X509Certificate x509Certificate = xMLX509Certificate.getX509Certificate();
        if (x509Certificate != null)
          return x509Certificate; 
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\X509CertificateResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */