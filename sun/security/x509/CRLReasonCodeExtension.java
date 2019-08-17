package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CRLReason;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CRLReasonCodeExtension extends Extension implements CertAttrSet<String> {
  public static final String NAME = "CRLReasonCode";
  
  public static final String REASON = "reason";
  
  private static CRLReason[] values = CRLReason.values();
  
  private int reasonCode = 0;
  
  private void encodeThis() throws IOException {
    if (this.reasonCode == 0) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putEnumerated(this.reasonCode);
    this.extensionValue = derOutputStream.toByteArray();
  }
  
  public CRLReasonCodeExtension(int paramInt) throws IOException { this(false, paramInt); }
  
  public CRLReasonCodeExtension(boolean paramBoolean, int paramInt) throws IOException {
    this.critical = paramBoolean;
    this.reasonCode = paramInt;
    encodeThis();
  }
  
  public CRLReasonCodeExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    this.reasonCode = derValue.getEnumerated();
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof Integer))
      throw new IOException("Attribute must be of type Integer."); 
    if (paramString.equalsIgnoreCase("reason")) {
      this.reasonCode = ((Integer)paramObject).intValue();
    } else {
      throw new IOException("Name not supported by CRLReasonCodeExtension");
    } 
    encodeThis();
  }
  
  public Integer get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("reason"))
      return new Integer(this.reasonCode); 
    throw new IOException("Name not supported by CRLReasonCodeExtension");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("reason")) {
      this.reasonCode = 0;
    } else {
      throw new IOException("Name not supported by CRLReasonCodeExtension");
    } 
    encodeThis();
  }
  
  public String toString() { return super.toString() + "    Reason Code: " + getReasonCode(); }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.ReasonCode_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("reason");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "CRLReasonCode"; }
  
  public CRLReason getReasonCode() { return (this.reasonCode > 0 && this.reasonCode < values.length) ? values[this.reasonCode] : CRLReason.UNSPECIFIED; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CRLReasonCodeExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */