package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

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

public class SingleKeyResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(SingleKeyResolver.class.getName());
  
  private String keyName;
  
  private PublicKey publicKey;
  
  private PrivateKey privateKey;
  
  private SecretKey secretKey;
  
  public SingleKeyResolver(String paramString, PublicKey paramPublicKey) {
    this.keyName = paramString;
    this.publicKey = paramPublicKey;
  }
  
  public SingleKeyResolver(String paramString, PrivateKey paramPrivateKey) {
    this.keyName = paramString;
    this.privateKey = paramPrivateKey;
  }
  
  public SingleKeyResolver(String paramString, SecretKey paramSecretKey) {
    this.keyName = paramString;
    this.secretKey = paramSecretKey;
  }
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName"); }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?"); 
    if (this.publicKey != null && XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName")) {
      String str = paramElement.getFirstChild().getNodeValue();
      if (this.keyName.equals(str))
        return this.publicKey; 
    } 
    log.log(Level.FINE, "I can't");
    return null;
  }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
  
  public SecretKey engineResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?"); 
    if (this.secretKey != null && XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName")) {
      String str = paramElement.getFirstChild().getNodeValue();
      if (this.keyName.equals(str))
        return this.secretKey; 
    } 
    log.log(Level.FINE, "I can't");
    return null;
  }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Can I resolve " + paramElement.getTagName() + "?"); 
    if (this.privateKey != null && XMLUtils.elementIsInSignatureSpace(paramElement, "KeyName")) {
      String str = paramElement.getFirstChild().getNodeValue();
      if (this.keyName.equals(str))
        return this.privateKey; 
    } 
    log.log(Level.FINE, "I can't");
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\SingleKeyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */