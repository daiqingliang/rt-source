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
import java.security.cert.X509CRL;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.security.auth.x500.X500Principal;

public class IndexedCollectionCertStore extends CertStoreSpi {
  private Map<X500Principal, Object> certSubjects;
  
  private Map<X500Principal, Object> crlIssuers;
  
  private Set<Certificate> otherCertificates;
  
  private Set<CRL> otherCRLs;
  
  public IndexedCollectionCertStore(CertStoreParameters paramCertStoreParameters) throws InvalidAlgorithmParameterException {
    super(paramCertStoreParameters);
    if (!(paramCertStoreParameters instanceof CollectionCertStoreParameters))
      throw new InvalidAlgorithmParameterException("parameters must be CollectionCertStoreParameters"); 
    Collection collection = ((CollectionCertStoreParameters)paramCertStoreParameters).getCollection();
    if (collection == null)
      throw new InvalidAlgorithmParameterException("Collection must not be null"); 
    buildIndex(collection);
  }
  
  private void buildIndex(Collection<?> paramCollection) {
    this.certSubjects = new HashMap();
    this.crlIssuers = new HashMap();
    this.otherCertificates = null;
    this.otherCRLs = null;
    for (Object object : paramCollection) {
      if (object instanceof X509Certificate) {
        indexCertificate((X509Certificate)object);
        continue;
      } 
      if (object instanceof X509CRL) {
        indexCRL((X509CRL)object);
        continue;
      } 
      if (object instanceof Certificate) {
        if (this.otherCertificates == null)
          this.otherCertificates = new HashSet(); 
        this.otherCertificates.add((Certificate)object);
        continue;
      } 
      if (object instanceof CRL) {
        if (this.otherCRLs == null)
          this.otherCRLs = new HashSet(); 
        this.otherCRLs.add((CRL)object);
      } 
    } 
    if (this.otherCertificates == null)
      this.otherCertificates = Collections.emptySet(); 
    if (this.otherCRLs == null)
      this.otherCRLs = Collections.emptySet(); 
  }
  
  private void indexCertificate(X509Certificate paramX509Certificate) {
    X500Principal x500Principal = paramX509Certificate.getSubjectX500Principal();
    Object object = this.certSubjects.put(x500Principal, paramX509Certificate);
    if (object != null)
      if (object instanceof X509Certificate) {
        if (paramX509Certificate.equals(object))
          return; 
        ArrayList arrayList = new ArrayList(2);
        arrayList.add(paramX509Certificate);
        arrayList.add((X509Certificate)object);
        this.certSubjects.put(x500Principal, arrayList);
      } else {
        List list = (List)object;
        if (!list.contains(paramX509Certificate))
          list.add(paramX509Certificate); 
        this.certSubjects.put(x500Principal, list);
      }  
  }
  
  private void indexCRL(X509CRL paramX509CRL) {
    X500Principal x500Principal = paramX509CRL.getIssuerX500Principal();
    Object object = this.crlIssuers.put(x500Principal, paramX509CRL);
    if (object != null)
      if (object instanceof X509CRL) {
        if (paramX509CRL.equals(object))
          return; 
        ArrayList arrayList = new ArrayList(2);
        arrayList.add(paramX509CRL);
        arrayList.add((X509CRL)object);
        this.crlIssuers.put(x500Principal, arrayList);
      } else {
        List list = (List)object;
        if (!list.contains(paramX509CRL))
          list.add(paramX509CRL); 
        this.crlIssuers.put(x500Principal, list);
      }  
  }
  
  public Collection<? extends Certificate> engineGetCertificates(CertSelector paramCertSelector) throws CertStoreException {
    X500Principal x500Principal;
    if (paramCertSelector == null) {
      HashSet hashSet1 = new HashSet();
      matchX509Certs(new X509CertSelector(), hashSet1);
      hashSet1.addAll(this.otherCertificates);
      return hashSet1;
    } 
    if (!(paramCertSelector instanceof X509CertSelector)) {
      HashSet hashSet1 = new HashSet();
      matchX509Certs(paramCertSelector, hashSet1);
      for (Certificate certificate : this.otherCertificates) {
        if (paramCertSelector.match(certificate))
          hashSet1.add(certificate); 
      } 
      return hashSet1;
    } 
    if (this.certSubjects.isEmpty())
      return Collections.emptySet(); 
    X509CertSelector x509CertSelector = (X509CertSelector)paramCertSelector;
    X509Certificate x509Certificate = x509CertSelector.getCertificate();
    if (x509Certificate != null) {
      x500Principal = x509Certificate.getSubjectX500Principal();
    } else {
      x500Principal = x509CertSelector.getSubject();
    } 
    if (x500Principal != null) {
      Object object = this.certSubjects.get(x500Principal);
      if (object == null)
        return Collections.emptySet(); 
      if (object instanceof X509Certificate) {
        X509Certificate x509Certificate1 = (X509Certificate)object;
        return x509CertSelector.match(x509Certificate1) ? Collections.singleton(x509Certificate1) : Collections.emptySet();
      } 
      List list = (List)object;
      HashSet hashSet1 = new HashSet(16);
      for (X509Certificate x509Certificate1 : list) {
        if (x509CertSelector.match(x509Certificate1))
          hashSet1.add(x509Certificate1); 
      } 
      return hashSet1;
    } 
    HashSet hashSet = new HashSet(16);
    matchX509Certs(x509CertSelector, hashSet);
    return hashSet;
  }
  
  private void matchX509Certs(CertSelector paramCertSelector, Collection<Certificate> paramCollection) {
    for (Object object : this.certSubjects.values()) {
      if (object instanceof X509Certificate) {
        X509Certificate x509Certificate = (X509Certificate)object;
        if (paramCertSelector.match(x509Certificate))
          paramCollection.add(x509Certificate); 
        continue;
      } 
      List list = (List)object;
      for (X509Certificate x509Certificate : list) {
        if (paramCertSelector.match(x509Certificate))
          paramCollection.add(x509Certificate); 
      } 
    } 
  }
  
  public Collection<CRL> engineGetCRLs(CRLSelector paramCRLSelector) throws CertStoreException {
    if (paramCRLSelector == null) {
      HashSet hashSet1 = new HashSet();
      matchX509CRLs(new X509CRLSelector(), hashSet1);
      hashSet1.addAll(this.otherCRLs);
      return hashSet1;
    } 
    if (!(paramCRLSelector instanceof X509CRLSelector)) {
      HashSet hashSet1 = new HashSet();
      matchX509CRLs(paramCRLSelector, hashSet1);
      for (CRL cRL : this.otherCRLs) {
        if (paramCRLSelector.match(cRL))
          hashSet1.add(cRL); 
      } 
      return hashSet1;
    } 
    if (this.crlIssuers.isEmpty())
      return Collections.emptySet(); 
    X509CRLSelector x509CRLSelector = (X509CRLSelector)paramCRLSelector;
    Collection collection = x509CRLSelector.getIssuers();
    if (collection != null) {
      HashSet hashSet1 = new HashSet(16);
      for (X500Principal x500Principal : collection) {
        Object object = this.crlIssuers.get(x500Principal);
        if (object == null)
          continue; 
        if (object instanceof X509CRL) {
          X509CRL x509CRL = (X509CRL)object;
          if (x509CRLSelector.match(x509CRL))
            hashSet1.add(x509CRL); 
          continue;
        } 
        List list = (List)object;
        for (X509CRL x509CRL : list) {
          if (x509CRLSelector.match(x509CRL))
            hashSet1.add(x509CRL); 
        } 
      } 
      return hashSet1;
    } 
    HashSet hashSet = new HashSet(16);
    matchX509CRLs(x509CRLSelector, hashSet);
    return hashSet;
  }
  
  private void matchX509CRLs(CRLSelector paramCRLSelector, Collection<CRL> paramCollection) {
    for (Object object : this.crlIssuers.values()) {
      if (object instanceof X509CRL) {
        X509CRL x509CRL = (X509CRL)object;
        if (paramCRLSelector.match(x509CRL))
          paramCollection.add(x509CRL); 
        continue;
      } 
      List list = (List)object;
      for (X509CRL x509CRL : list) {
        if (paramCRLSelector.match(x509CRL))
          paramCollection.add(x509CRL); 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\IndexedCollectionCertStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */