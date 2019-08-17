package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.keys.content.DEREncodedKeyValue;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class DEREncodedKeyValueResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(DEREncodedKeyValueResolver.class.getName());
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return XMLUtils.elementIsInSignature11Space(paramElement, "DEREncodedKeyValue"); }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName()); 
    if (!engineCanResolve(paramElement, paramString, paramStorageResolver))
      return null; 
    try {
      DEREncodedKeyValue dEREncodedKeyValue = new DEREncodedKeyValue(paramElement, paramString);
      return dEREncodedKeyValue.getPublicKey();
    } catch (XMLSecurityException xMLSecurityException) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "XMLSecurityException", xMLSecurityException); 
      return null;
    } 
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\DEREncodedKeyValueResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */