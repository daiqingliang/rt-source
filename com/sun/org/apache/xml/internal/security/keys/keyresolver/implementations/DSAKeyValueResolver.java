package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.keyvalues.DSAKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class DSAKeyValueResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(DSAKeyValueResolver.class.getName());
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) {
    if (paramElement == null)
      return null; 
    Element element = null;
    boolean bool = XMLUtils.elementIsInSignatureSpace(paramElement, "KeyValue");
    if (bool) {
      element = XMLUtils.selectDsNode(paramElement.getFirstChild(), "DSAKeyValue", 0);
    } else if (XMLUtils.elementIsInSignatureSpace(paramElement, "DSAKeyValue")) {
      element = paramElement;
    } 
    if (element == null)
      return null; 
    try {
      DSAKeyValue dSAKeyValue = new DSAKeyValue(element, paramString);
      return dSAKeyValue.getPublicKey();
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, xMLSecurityException.getMessage(), xMLSecurityException); 
      return null;
    } 
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return null; }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\DSAKeyValueResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */