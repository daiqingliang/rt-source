package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverException;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class KeyStoreResolver extends StorageResolverSpi {
  private KeyStore keyStore = null;
  
  public KeyStoreResolver(KeyStore paramKeyStore) throws StorageResolverException {
    this.keyStore = paramKeyStore;
    try {
      paramKeyStore.aliases();
    } catch (KeyStoreException keyStoreException) {
      throw new StorageResolverException("generic.EmptyMessage", keyStoreException);
    } 
  }
  
  public Iterator<Certificate> getIterator() { return new KeyStoreIterator(this.keyStore); }
  
  static class KeyStoreIterator extends Object implements Iterator<Certificate> {
    KeyStore keyStore = null;
    
    Enumeration<String> aliases = null;
    
    Certificate nextCert = null;
    
    public KeyStoreIterator(KeyStore param1KeyStore) throws StorageResolverException {
      try {
        this.keyStore = param1KeyStore;
        this.aliases = this.keyStore.aliases();
      } catch (KeyStoreException keyStoreException) {
        this.aliases = new Enumeration<String>() {
            public boolean hasMoreElements() { return false; }
            
            public String nextElement() { return null; }
          };
      } 
    }
    
    public boolean hasNext() {
      if (this.nextCert == null)
        this.nextCert = findNextCert(); 
      return (this.nextCert != null);
    }
    
    public Certificate next() {
      if (this.nextCert == null) {
        this.nextCert = findNextCert();
        if (this.nextCert == null)
          throw new NoSuchElementException(); 
      } 
      Certificate certificate = this.nextCert;
      this.nextCert = null;
      return certificate;
    }
    
    public void remove() { throw new UnsupportedOperationException("Can't remove keys from KeyStore"); }
    
    private Certificate findNextCert() {
      while (this.aliases.hasMoreElements()) {
        String str = (String)this.aliases.nextElement();
        try {
          Certificate certificate = this.keyStore.getCertificate(str);
          if (certificate != null)
            return certificate; 
        } catch (KeyStoreException keyStoreException) {
          return null;
        } 
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\implementations\KeyStoreResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */