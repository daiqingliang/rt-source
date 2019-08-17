package com.sun.org.apache.xml.internal.security.keys.storage;

import com.sun.org.apache.xml.internal.security.keys.storage.implementations.KeyStoreResolver;
import com.sun.org.apache.xml.internal.security.keys.storage.implementations.SingleCertificateResolver;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageResolver {
  private static Logger log = Logger.getLogger(StorageResolver.class.getName());
  
  private List<StorageResolverSpi> storageResolvers = null;
  
  public StorageResolver() {}
  
  public StorageResolver(StorageResolverSpi paramStorageResolverSpi) { add(paramStorageResolverSpi); }
  
  public void add(StorageResolverSpi paramStorageResolverSpi) {
    if (this.storageResolvers == null)
      this.storageResolvers = new ArrayList(); 
    this.storageResolvers.add(paramStorageResolverSpi);
  }
  
  public StorageResolver(KeyStore paramKeyStore) { add(paramKeyStore); }
  
  public void add(KeyStore paramKeyStore) {
    try {
      add(new KeyStoreResolver(paramKeyStore));
    } catch (StorageResolverException storageResolverException) {
      log.log(Level.SEVERE, "Could not add KeyStore because of: ", storageResolverException);
    } 
  }
  
  public StorageResolver(X509Certificate paramX509Certificate) { add(paramX509Certificate); }
  
  public void add(X509Certificate paramX509Certificate) { add(new SingleCertificateResolver(paramX509Certificate)); }
  
  public Iterator<Certificate> getIterator() { return new StorageResolverIterator(this.storageResolvers.iterator()); }
  
  static class StorageResolverIterator extends Object implements Iterator<Certificate> {
    Iterator<StorageResolverSpi> resolvers = null;
    
    Iterator<Certificate> currentResolver = null;
    
    public StorageResolverIterator(Iterator<StorageResolverSpi> param1Iterator) {
      this.resolvers = param1Iterator;
      this.currentResolver = findNextResolver();
    }
    
    public boolean hasNext() {
      if (this.currentResolver == null)
        return false; 
      if (this.currentResolver.hasNext())
        return true; 
      this.currentResolver = findNextResolver();
      return (this.currentResolver != null);
    }
    
    public Certificate next() {
      if (hasNext())
        return (Certificate)this.currentResolver.next(); 
      throw new NoSuchElementException();
    }
    
    public void remove() { throw new UnsupportedOperationException("Can't remove keys from KeyStore"); }
    
    private Iterator<Certificate> findNextResolver() {
      while (this.resolvers.hasNext()) {
        StorageResolverSpi storageResolverSpi = (StorageResolverSpi)this.resolvers.next();
        Iterator iterator = storageResolverSpi.getIterator();
        if (iterator.hasNext())
          return iterator; 
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\StorageResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */