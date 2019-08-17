package java.security.cert;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import javax.security.auth.x500.X500Principal;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.x509.CRLNumberExtension;
import sun.security.x509.X500Name;

public class X509CRLSelector implements CRLSelector {
  private static final Debug debug;
  
  private HashSet<Object> issuerNames;
  
  private HashSet<X500Principal> issuerX500Principals;
  
  private BigInteger minCRL;
  
  private BigInteger maxCRL;
  
  private Date dateAndTime;
  
  private X509Certificate certChecking;
  
  private long skew = 0L;
  
  public void setIssuers(Collection<X500Principal> paramCollection) {
    if (paramCollection == null || paramCollection.isEmpty()) {
      this.issuerNames = null;
      this.issuerX500Principals = null;
    } else {
      this.issuerX500Principals = new HashSet(paramCollection);
      this.issuerNames = new HashSet();
      for (X500Principal x500Principal : this.issuerX500Principals)
        this.issuerNames.add(x500Principal.getEncoded()); 
    } 
  }
  
  public void setIssuerNames(Collection<?> paramCollection) throws IOException {
    if (paramCollection == null || paramCollection.size() == 0) {
      this.issuerNames = null;
      this.issuerX500Principals = null;
    } else {
      HashSet hashSet = cloneAndCheckIssuerNames(paramCollection);
      this.issuerX500Principals = parseIssuerNames(hashSet);
      this.issuerNames = hashSet;
    } 
  }
  
  public void addIssuer(X500Principal paramX500Principal) { addIssuerNameInternal(paramX500Principal.getEncoded(), paramX500Principal); }
  
  public void addIssuerName(String paramString) throws IOException { addIssuerNameInternal(paramString, (new X500Name(paramString)).asX500Principal()); }
  
  public void addIssuerName(byte[] paramArrayOfByte) throws IOException { addIssuerNameInternal(paramArrayOfByte.clone(), (new X500Name(paramArrayOfByte)).asX500Principal()); }
  
  private void addIssuerNameInternal(Object paramObject, X500Principal paramX500Principal) {
    if (this.issuerNames == null)
      this.issuerNames = new HashSet(); 
    if (this.issuerX500Principals == null)
      this.issuerX500Principals = new HashSet(); 
    this.issuerNames.add(paramObject);
    this.issuerX500Principals.add(paramX500Principal);
  }
  
  private static HashSet<Object> cloneAndCheckIssuerNames(Collection<?> paramCollection) throws IOException {
    HashSet hashSet = new HashSet();
    for (Object object : paramCollection) {
      if (!(object instanceof byte[]) && !(object instanceof String))
        throw new IOException("name not byte array or String"); 
      if (object instanceof byte[]) {
        hashSet.add(((byte[])object).clone());
        continue;
      } 
      hashSet.add(object);
    } 
    return hashSet;
  }
  
  private static HashSet<Object> cloneIssuerNames(Collection<Object> paramCollection) {
    try {
      return cloneAndCheckIssuerNames(paramCollection);
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  private static HashSet<X500Principal> parseIssuerNames(Collection<Object> paramCollection) throws IOException {
    HashSet hashSet = new HashSet();
    for (Object object : paramCollection) {
      if (object instanceof String) {
        hashSet.add((new X500Name((String)object)).asX500Principal());
        continue;
      } 
      try {
        hashSet.add(new X500Principal((byte[])object));
      } catch (IllegalArgumentException illegalArgumentException) {
        throw (IOException)(new IOException("Invalid name")).initCause(illegalArgumentException);
      } 
    } 
    return hashSet;
  }
  
  public void setMinCRLNumber(BigInteger paramBigInteger) { this.minCRL = paramBigInteger; }
  
  public void setMaxCRLNumber(BigInteger paramBigInteger) { this.maxCRL = paramBigInteger; }
  
  public void setDateAndTime(Date paramDate) {
    if (paramDate == null) {
      this.dateAndTime = null;
    } else {
      this.dateAndTime = new Date(paramDate.getTime());
    } 
    this.skew = 0L;
  }
  
  void setDateAndTime(Date paramDate, long paramLong) {
    this.dateAndTime = (paramDate == null) ? null : new Date(paramDate.getTime());
    this.skew = paramLong;
  }
  
  public void setCertificateChecking(X509Certificate paramX509Certificate) { this.certChecking = paramX509Certificate; }
  
  public Collection<X500Principal> getIssuers() { return (this.issuerX500Principals == null) ? null : Collections.unmodifiableCollection(this.issuerX500Principals); }
  
  public Collection<Object> getIssuerNames() { return (this.issuerNames == null) ? null : cloneIssuerNames(this.issuerNames); }
  
  public BigInteger getMinCRL() { return this.minCRL; }
  
  public BigInteger getMaxCRL() { return this.maxCRL; }
  
  public Date getDateAndTime() { return (this.dateAndTime == null) ? null : (Date)this.dateAndTime.clone(); }
  
  public X509Certificate getCertificateChecking() { return this.certChecking; }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("X509CRLSelector: [\n");
    if (this.issuerNames != null) {
      stringBuffer.append("  IssuerNames:\n");
      Iterator iterator = this.issuerNames.iterator();
      while (iterator.hasNext())
        stringBuffer.append("    " + iterator.next() + "\n"); 
    } 
    if (this.minCRL != null)
      stringBuffer.append("  minCRLNumber: " + this.minCRL + "\n"); 
    if (this.maxCRL != null)
      stringBuffer.append("  maxCRLNumber: " + this.maxCRL + "\n"); 
    if (this.dateAndTime != null)
      stringBuffer.append("  dateAndTime: " + this.dateAndTime + "\n"); 
    if (this.certChecking != null)
      stringBuffer.append("  Certificate being checked: " + this.certChecking + "\n"); 
    stringBuffer.append("]");
    return stringBuffer.toString();
  }
  
  public boolean match(CRL paramCRL) {
    if (!(paramCRL instanceof X509CRL))
      return false; 
    X509CRL x509CRL = (X509CRL)paramCRL;
    if (this.issuerNames != null) {
      X500Principal x500Principal = x509CRL.getIssuerX500Principal();
      Iterator iterator = this.issuerX500Principals.iterator();
      boolean bool = false;
      while (!bool && iterator.hasNext()) {
        if (((X500Principal)iterator.next()).equals(x500Principal))
          bool = true; 
      } 
      if (!bool) {
        if (debug != null)
          debug.println("X509CRLSelector.match: issuer DNs don't match"); 
        return false;
      } 
    } 
    if (this.minCRL != null || this.maxCRL != null) {
      BigInteger bigInteger;
      byte[] arrayOfByte = x509CRL.getExtensionValue("2.5.29.20");
      if (arrayOfByte == null && debug != null)
        debug.println("X509CRLSelector.match: no CRLNumber"); 
      try {
        DerInputStream derInputStream = new DerInputStream(arrayOfByte);
        byte[] arrayOfByte1 = derInputStream.getOctetString();
        CRLNumberExtension cRLNumberExtension = new CRLNumberExtension(Boolean.FALSE, arrayOfByte1);
        bigInteger = cRLNumberExtension.get("value");
      } catch (IOException iOException) {
        if (debug != null)
          debug.println("X509CRLSelector.match: exception in decoding CRL number"); 
        return false;
      } 
      if (this.minCRL != null && bigInteger.compareTo(this.minCRL) < 0) {
        if (debug != null)
          debug.println("X509CRLSelector.match: CRLNumber too small"); 
        return false;
      } 
      if (this.maxCRL != null && bigInteger.compareTo(this.maxCRL) > 0) {
        if (debug != null)
          debug.println("X509CRLSelector.match: CRLNumber too large"); 
        return false;
      } 
    } 
    if (this.dateAndTime != null) {
      Date date1 = x509CRL.getThisUpdate();
      Date date2 = x509CRL.getNextUpdate();
      if (date2 == null) {
        if (debug != null)
          debug.println("X509CRLSelector.match: nextUpdate null"); 
        return false;
      } 
      Date date3 = this.dateAndTime;
      Date date4 = this.dateAndTime;
      if (this.skew > 0L) {
        date3 = new Date(this.dateAndTime.getTime() + this.skew);
        date4 = new Date(this.dateAndTime.getTime() - this.skew);
      } 
      if (date4.after(date2) || date3.before(date1)) {
        if (debug != null)
          debug.println("X509CRLSelector.match: update out-of-range"); 
        return false;
      } 
    } 
    return true;
  }
  
  public Object clone() {
    try {
      X509CRLSelector x509CRLSelector = (X509CRLSelector)super.clone();
      if (this.issuerNames != null) {
        x509CRLSelector.issuerNames = new HashSet(this.issuerNames);
        x509CRLSelector.issuerX500Principals = new HashSet(this.issuerX500Principals);
      } 
      return x509CRLSelector;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException.toString(), cloneNotSupportedException);
    } 
  }
  
  static  {
    CertPathHelperImpl.initialize();
    debug = Debug.getInstance("certpath");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\X509CRLSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */