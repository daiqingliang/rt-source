package com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations;

import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
import com.sun.org.apache.xml.internal.security.encryption.XMLEncryptionException;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolverSpi;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class EncryptedKeyResolver extends KeyResolverSpi {
  private static Logger log = Logger.getLogger(EncryptedKeyResolver.class.getName());
  
  private Key kek;
  
  private String algorithm;
  
  private List<KeyResolverSpi> internalKeyResolvers;
  
  public EncryptedKeyResolver(String paramString) {
    this.kek = null;
    this.algorithm = paramString;
  }
  
  public EncryptedKeyResolver(String paramString, Key paramKey) {
    this.algorithm = paramString;
    this.kek = paramKey;
  }
  
  public void registerInternalKeyResolver(KeyResolverSpi paramKeyResolverSpi) {
    if (this.internalKeyResolvers == null)
      this.internalKeyResolvers = new ArrayList(); 
    this.internalKeyResolvers.add(paramKeyResolverSpi);
  }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return null; }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) { return null; }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "EncryptedKeyResolver - Can I resolve " + paramElement.getTagName()); 
    if (paramElement == null)
      return null; 
    SecretKey secretKey = null;
    boolean bool = XMLUtils.elementIsInEncryptionSpace(paramElement, "EncryptedKey");
    if (bool) {
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "Passed an Encrypted Key"); 
      try {
        XMLCipher xMLCipher = XMLCipher.getInstance();
        xMLCipher.init(4, this.kek);
        if (this.internalKeyResolvers != null) {
          int i = this.internalKeyResolvers.size();
          for (byte b = 0; b < i; b++)
            xMLCipher.registerInternalKeyResolver((KeyResolverSpi)this.internalKeyResolvers.get(b)); 
        } 
        EncryptedKey encryptedKey = xMLCipher.loadEncryptedKey(paramElement);
        secretKey = (SecretKey)xMLCipher.decryptKey(encryptedKey, this.algorithm);
      } catch (XMLEncryptionException xMLEncryptionException) {
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, xMLEncryptionException.getMessage(), xMLEncryptionException); 
      } 
    } 
    return secretKey;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\implementations\EncryptedKeyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */