package sun.security.provider.certpath;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;

public class CollectionCertStore extends CertStoreSpi {
  private Collection<?> coll;
  
  public CollectionCertStore(CertStoreParameters paramCertStoreParameters) throws InvalidAlgorithmParameterException {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof CollectionCertStoreParameters))
      throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters"); 
    this.coll = ((CollectionCertStoreParameters)paramCertStoreParameters).getCollection();
  }
  
  public Collection<Certificate> engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException {
    if (this.coll == null)
      throw new CertStoreException("Collection is null"); 
    byte b = 0;
    while (b < 10) {
      try {
        HashSet hashSet = new HashSet();
        if (paramCertSelector != null) {
          for (Object object : this.coll) {
            if (object instanceof Certificate && paramCertSelector.match((Certificate)object))
              hashSet.add((Certificate)object); 
          } 
        } else {
          for (Object object : this.coll) {
            if (object instanceof Certificate)
              hashSet.add((Certificate)object); 
          } 
        } 
        return hashSet;
      } catch (ConcurrentModificationException concurrentModificationException) {
        b++;
      } 
    } 
    throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
  }
  
  public Collection<CRL> engineGetCRLs(CRLSelector paramCRLSelector) throws CertStoreException {
    if (this.coll == null)
      throw new CertStoreException("Collection is null"); 
    byte b = 0;
    while (b < 10) {
      try {
        HashSet hashSet = new HashSet();
        if (paramCRLSelector != null) {
          for (Object object : this.coll) {
            if (object instanceof CRL && paramCRLSelector.match((CRL)object))
              hashSet.add((CRL)object); 
          } 
        } else {
          for (Object object : this.coll) {
            if (object instanceof CRL)
              hashSet.add((CRL)object); 
          } 
        } 
        return hashSet;
      } catch (ConcurrentModificationException concurrentModificationException) {
        b++;
      } 
    } 
    throw new ConcurrentModificationException("Too many ConcurrentModificationExceptions");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\CollectionCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */