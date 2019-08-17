package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Date;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateValidity extends Object implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.validity";
  
  public static final String NAME = "validity";
  
  public static final String NOT_BEFORE = "notBefore";
  
  public static final String NOT_AFTER = "notAfter";
  
  private static final long YR_2050 = 2524636800000L;
  
  private Date notBefore;
  
  private Date notAfter;
  
  private Date getNotBefore() { return new Date(this.notBefore.getTime()); }
  
  private Date getNotAfter() { return new Date(this.notAfter.getTime()); }
  
  private void construct(DerValue paramDerValue) throws IOException {
    if (paramDerValue.tag != 48)
      throw new IOException("Invalid encoded CertificateValidity, starting sequence tag missing."); 
    if (paramDerValue.data.available() == 0)
      throw new IOException("No data encoded for CertificateValidity"); 
    DerInputStream derInputStream = new DerInputStream(paramDerValue.toByteArray());
    DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
    if (arrayOfDerValue.length != 2)
      throw new IOException("Invalid encoding for CertificateValidity"); 
    if ((arrayOfDerValue[0]).tag == 23) {
      this.notBefore = paramDerValue.data.getUTCTime();
    } else if ((arrayOfDerValue[0]).tag == 24) {
      this.notBefore = paramDerValue.data.getGeneralizedTime();
    } else {
      throw new IOException("Invalid encoding for CertificateValidity");
    } 
    if ((arrayOfDerValue[1]).tag == 23) {
      this.notAfter = paramDerValue.data.getUTCTime();
    } else if ((arrayOfDerValue[1]).tag == 24) {
      this.notAfter = paramDerValue.data.getGeneralizedTime();
    } else {
      throw new IOException("Invalid encoding for CertificateValidity");
    } 
  }
  
  public CertificateValidity() {}
  
  public CertificateValidity(Date paramDate1, Date paramDate2) {
    this.notBefore = paramDate1;
    this.notAfter = paramDate2;
  }
  
  public CertificateValidity(DerInputStream paramDerInputStream) throws IOException {
    DerValue derValue = paramDerInputStream.getDerValue();
    construct(derValue);
  }
  
  public String toString() { return (this.notBefore == null || this.notAfter == null) ? "" : ("Validity: [From: " + this.notBefore.toString() + ",\n               To: " + this.notAfter.toString() + "]"); }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    if (this.notBefore == null || this.notAfter == null)
      throw new IOException("CertAttrSet:CertificateValidity: null values to encode.\n"); 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    if (this.notBefore.getTime() < 2524636800000L) {
      derOutputStream1.putUTCTime(this.notBefore);
    } else {
      derOutputStream1.putGeneralizedTime(this.notBefore);
    } 
    if (this.notAfter.getTime() < 2524636800000L) {
      derOutputStream1.putUTCTime(this.notAfter);
    } else {
      derOutputStream1.putGeneralizedTime(this.notAfter);
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    paramOutputStream.write(derOutputStream2.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof Date))
      throw new IOException("Attribute must be of type Date."); 
    if (paramString.equalsIgnoreCase("notBefore")) {
      this.notBefore = (Date)paramObject;
    } else if (paramString.equalsIgnoreCase("notAfter")) {
      this.notAfter = (Date)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
    } 
  }
  
  public Date get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("notBefore"))
      return getNotBefore(); 
    if (paramString.equalsIgnoreCase("notAfter"))
      return getNotAfter(); 
    throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("notBefore")) {
      this.notBefore = null;
    } else if (paramString.equalsIgnoreCase("notAfter")) {
      this.notAfter = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet: CertificateValidity.");
    } 
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("notBefore");
    attributeNameEnumeration.addElement("notAfter");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "validity"; }
  
  public void valid() {
    Date date = new Date();
    valid(date);
  }
  
  public void valid(Date paramDate) throws CertificateNotYetValidException, CertificateExpiredException {
    if (this.notBefore.after(paramDate))
      throw new CertificateNotYetValidException("NotBefore: " + this.notBefore.toString()); 
    if (this.notAfter.before(paramDate))
      throw new CertificateExpiredException("NotAfter: " + this.notAfter.toString()); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificateValidity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */