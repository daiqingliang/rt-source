package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public abstract class KeyResolverSpi {
  protected Map<String, String> properties = null;
  
  protected boolean globalResolver = false;
  
  protected boolean secureValidation;
  
  public void setSecureValidation(boolean paramBoolean) { this.secureValidation = paramBoolean; }
  
  public boolean engineCanResolve(Element paramElement, String paramString, StorageResolver paramStorageResolver) { throw new UnsupportedOperationException(); }
  
  public PublicKey engineResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { throw new UnsupportedOperationException(); }
  
  public PublicKey engineLookupAndResolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    KeyResolverSpi keyResolverSpi = cloneIfNeeded();
    return !keyResolverSpi.engineCanResolve(paramElement, paramString, paramStorageResolver) ? null : keyResolverSpi.engineResolvePublicKey(paramElement, paramString, paramStorageResolver);
  }
  
  private KeyResolverSpi cloneIfNeeded() throws KeyResolverException {
    KeyResolverSpi keyResolverSpi = this;
    if (this.globalResolver)
      try {
        keyResolverSpi = (KeyResolverSpi)getClass().newInstance();
      } catch (InstantiationException instantiationException) {
        throw new KeyResolverException("", instantiationException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new KeyResolverException("", illegalAccessException);
      }  
    return keyResolverSpi;
  }
  
  public X509Certificate engineResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { throw new UnsupportedOperationException(); }
  
  public X509Certificate engineLookupResolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    KeyResolverSpi keyResolverSpi = cloneIfNeeded();
    return !keyResolverSpi.engineCanResolve(paramElement, paramString, paramStorageResolver) ? null : keyResolverSpi.engineResolveX509Certificate(paramElement, paramString, paramStorageResolver);
  }
  
  public SecretKey engineResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { throw new UnsupportedOperationException(); }
  
  public SecretKey engineLookupAndResolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    KeyResolverSpi keyResolverSpi = cloneIfNeeded();
    return !keyResolverSpi.engineCanResolve(paramElement, paramString, paramStorageResolver) ? null : keyResolverSpi.engineResolveSecretKey(paramElement, paramString, paramStorageResolver);
  }
  
  public PrivateKey engineLookupAndResolvePrivateKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return null; }
  
  public void engineSetProperty(String paramString1, String paramString2) {
    if (this.properties == null)
      this.properties = new HashMap(); 
    this.properties.put(paramString1, paramString2);
  }
  
  public String engineGetProperty(String paramString) { return (this.properties == null) ? null : (String)this.properties.get(paramString); }
  
  public boolean understandsProperty(String paramString) { return (this.properties == null) ? false : ((this.properties.get(paramString) != null)); }
  
  public void setGlobalResolver(boolean paramBoolean) { this.globalResolver = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\KeyResolverSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */