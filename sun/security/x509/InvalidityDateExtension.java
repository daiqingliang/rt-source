package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.Extension;
import java.util.Date;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class InvalidityDateExtension extends Extension implements CertAttrSet<String> {
  public static final String NAME = "InvalidityDate";
  
  public static final String DATE = "date";
  
  private Date date;
  
  private void encodeThis() throws IOException {
    if (this.date == null) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putGeneralizedTime(this.date);
    this.extensionValue = derOutputStream.toByteArray();
  }
  
  public InvalidityDateExtension(Date paramDate) throws IOException { this(false, paramDate); }
  
  public InvalidityDateExtension(boolean paramBoolean, Date paramDate) throws IOException {
    this.critical = paramBoolean;
    this.date = paramDate;
    encodeThis();
  }
  
  public InvalidityDateExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    this.date = derValue.getGeneralizedTime();
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof Date))
      throw new IOException("Attribute must be of type Date."); 
    if (paramString.equalsIgnoreCase("date")) {
      this.date = (Date)paramObject;
    } else {
      throw new IOException("Name not supported by InvalidityDateExtension");
    } 
    encodeThis();
  }
  
  public Date get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("date"))
      return (this.date == null) ? null : new Date(this.date.getTime()); 
    throw new IOException("Name not supported by InvalidityDateExtension");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("date")) {
      this.date = null;
    } else {
      throw new IOException("Name not supported by InvalidityDateExtension");
    } 
    encodeThis();
  }
  
  public String toString() { return super.toString() + "    Invalidity Date: " + String.valueOf(this.date); }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.InvalidityDate_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("date");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "InvalidityDate"; }
  
  public static InvalidityDateExtension toImpl(Extension paramExtension) throws IOException { return (paramExtension instanceof InvalidityDateExtension) ? (InvalidityDateExtension)paramExtension : new InvalidityDateExtension(Boolean.valueOf(paramExtension.isCritical()), paramExtension.getValue()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\InvalidityDateExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */