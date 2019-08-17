package sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CertificateSerialNumber extends Object implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.serialNumber";
  
  public static final String NAME = "serialNumber";
  
  public static final String NUMBER = "number";
  
  private SerialNumber serial;
  
  public CertificateSerialNumber(BigInteger paramBigInteger) { this.serial = new SerialNumber(paramBigInteger); }
  
  public CertificateSerialNumber(int paramInt) { this.serial = new SerialNumber(paramInt); }
  
  public CertificateSerialNumber(DerInputStream paramDerInputStream) throws IOException { this.serial = new SerialNumber(paramDerInputStream); }
  
  public CertificateSerialNumber(InputStream paramInputStream) throws IOException { this.serial = new SerialNumber(paramInputStream); }
  
  public CertificateSerialNumber(DerValue paramDerValue) throws IOException { this.serial = new SerialNumber(paramDerValue); }
  
  public String toString() { return (this.serial == null) ? "" : this.serial.toString(); }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    this.serial.encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof SerialNumber))
      throw new IOException("Attribute must be of type SerialNumber."); 
    if (paramString.equalsIgnoreCase("number")) {
      this.serial = (SerialNumber)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
    } 
  }
  
  public SerialNumber get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("number"))
      return this.serial; 
    throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("number")) {
      this.serial = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
    } 
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("number");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "serialNumber"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificateSerialNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */