package sun.security.provider.certpath;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.SerialNumber;

class AdaptableX509CertSelector extends X509CertSelector {
  private static final Debug debug = Debug.getInstance("certpath");
  
  private Date startDate;
  
  private Date endDate;
  
  private byte[] ski;
  
  private BigInteger serial;
  
  void setValidityPeriod(Date paramDate1, Date paramDate2) {
    this.startDate = paramDate1;
    this.endDate = paramDate2;
  }
  
  public void setSubjectKeyIdentifier(byte[] paramArrayOfByte) { throw new IllegalArgumentException(); }
  
  public void setSerialNumber(BigInteger paramBigInteger) { throw new IllegalArgumentException(); }
  
  void setSkiAndSerialNumber(AuthorityKeyIdentifierExtension paramAuthorityKeyIdentifierExtension) throws IOException {
    this.ski = null;
    this.serial = null;
    if (paramAuthorityKeyIdentifierExtension != null) {
      this.ski = paramAuthorityKeyIdentifierExtension.getEncodedKeyIdentifier();
      SerialNumber serialNumber = (SerialNumber)paramAuthorityKeyIdentifierExtension.get("serial_number");
      if (serialNumber != null)
        this.serial = serialNumber.getNumber(); 
    } 
  }
  
  public boolean match(Certificate paramCertificate) {
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    if (!matchSubjectKeyID(x509Certificate))
      return false; 
    int i = x509Certificate.getVersion();
    if (this.serial != null && i > 2 && !this.serial.equals(x509Certificate.getSerialNumber()))
      return false; 
    if (i < 3) {
      if (this.startDate != null)
        try {
          x509Certificate.checkValidity(this.startDate);
        } catch (CertificateException certificateException) {
          return false;
        }  
      if (this.endDate != null)
        try {
          x509Certificate.checkValidity(this.endDate);
        } catch (CertificateException certificateException) {
          return false;
        }  
    } 
    return !!super.match(paramCertificate);
  }
  
  private boolean matchSubjectKeyID(X509Certificate paramX509Certificate) {
    if (this.ski == null)
      return true; 
    try {
      byte[] arrayOfByte1 = paramX509Certificate.getExtensionValue("2.5.29.14");
      if (arrayOfByte1 == null) {
        if (debug != null)
          debug.println("AdaptableX509CertSelector.match: no subject key ID extension. Subject: " + paramX509Certificate.getSubjectX500Principal()); 
        return true;
      } 
      DerInputStream derInputStream = new DerInputStream(arrayOfByte1);
      byte[] arrayOfByte2 = derInputStream.getOctetString();
      if (arrayOfByte2 == null || !Arrays.equals(this.ski, arrayOfByte2)) {
        if (debug != null)
          debug.println("AdaptableX509CertSelector.match: subject key IDs don't match. Expected: " + Arrays.toString(this.ski) + " Cert's: " + Arrays.toString(arrayOfByte2)); 
        return false;
      } 
    } catch (IOException iOException) {
      if (debug != null)
        debug.println("AdaptableX509CertSelector.match: exception in subject key ID check"); 
      return false;
    } 
    return true;
  }
  
  public Object clone() {
    AdaptableX509CertSelector adaptableX509CertSelector = (AdaptableX509CertSelector)super.clone();
    if (this.startDate != null)
      adaptableX509CertSelector.startDate = (Date)this.startDate.clone(); 
    if (this.endDate != null)
      adaptableX509CertSelector.endDate = (Date)this.endDate.clone(); 
    if (this.ski != null)
      adaptableX509CertSelector.ski = (byte[])this.ski.clone(); 
    return adaptableX509CertSelector;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\AdaptableX509CertSelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */