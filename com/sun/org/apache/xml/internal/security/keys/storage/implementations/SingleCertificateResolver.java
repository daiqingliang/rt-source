package com.sun.org.apache.xml.internal.security.keys.storage.implementations;

import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolverSpi;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleCertificateResolver extends StorageResolverSpi {
  private X509Certificate certificate = null;
  
  public SingleCertificateResolver(X509Certificate paramX509Certificate) { this.certificate = paramX509Certificate; }
  
  public Iterator<Certificate> getIterator() { return new InternalIterator(this.certificate); }
  
  static class InternalIterator extends Object implements Iterator<Certificate> {
    boolean alreadyReturned = false;
    
    X509Certificate certificate = null;
    
    public InternalIterator(X509Certificate param1X509Certificate) { this.certificate = param1X509Certificate; }
    
    public boolean hasNext() { return !this.alreadyReturned; }
    
    public Certificate next() {
      if (this.alreadyReturned)
        throw new NoSuchElementException(); 
      this.alreadyReturned = true;
      return this.certificate;
    }
    
    public void remove() { throw new UnsupportedOperationException("Can't remove keys from KeyStore"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\storage\implementations\SingleCertificateResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */