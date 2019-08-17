package sun.security.pkcs10;

import java.io.IOException;
import java.io.OutputStream;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.util.DerEncoder;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class PKCS10Attribute implements DerEncoder {
  protected ObjectIdentifier attributeId = null;
  
  protected Object attributeValue = null;
  
  public PKCS10Attribute(DerValue paramDerValue) throws IOException {
    PKCS9Attribute pKCS9Attribute = new PKCS9Attribute(paramDerValue);
    this.attributeId = pKCS9Attribute.getOID();
    this.attributeValue = pKCS9Attribute.getValue();
  }
  
  public PKCS10Attribute(ObjectIdentifier paramObjectIdentifier, Object paramObject) {
    this.attributeId = paramObjectIdentifier;
    this.attributeValue = paramObject;
  }
  
  public PKCS10Attribute(PKCS9Attribute paramPKCS9Attribute) {
    this.attributeId = paramPKCS9Attribute.getOID();
    this.attributeValue = paramPKCS9Attribute.getValue();
  }
  
  public void derEncode(OutputStream paramOutputStream) throws IOException {
    PKCS9Attribute pKCS9Attribute = new PKCS9Attribute(this.attributeId, this.attributeValue);
    pKCS9Attribute.derEncode(paramOutputStream);
  }
  
  public ObjectIdentifier getAttributeId() { return this.attributeId; }
  
  public Object getAttributeValue() { return this.attributeValue; }
  
  public String toString() { return this.attributeValue.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs10\PKCS10Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */