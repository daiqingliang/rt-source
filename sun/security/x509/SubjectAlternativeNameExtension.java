package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class SubjectAlternativeNameExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.SubjectAlternativeName";
  
  public static final String NAME = "SubjectAlternativeName";
  
  public static final String SUBJECT_NAME = "subject_name";
  
  GeneralNames names = null;
  
  private void encodeThis() throws IOException {
    if (this.names == null || this.names.isEmpty()) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream = new DerOutputStream();
    this.names.encode(derOutputStream);
    this.extensionValue = derOutputStream.toByteArray();
  }
  
  public SubjectAlternativeNameExtension(GeneralNames paramGeneralNames) throws IOException { this(Boolean.FALSE, paramGeneralNames); }
  
  public SubjectAlternativeNameExtension(Boolean paramBoolean, GeneralNames paramGeneralNames) throws IOException {
    this.names = paramGeneralNames;
    this.extensionId = PKIXExtensions.SubjectAlternativeName_Id;
    this.critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public SubjectAlternativeNameExtension() throws IOException {
    this.extensionId = PKIXExtensions.SubjectAlternativeName_Id;
    this.critical = false;
    this.names = new GeneralNames();
  }
  
  public SubjectAlternativeNameExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.SubjectAlternativeName_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.data == null) {
      this.names = new GeneralNames();
      return;
    } 
    this.names = new GeneralNames(derValue);
  }
  
  public String toString() {
    null = super.toString() + "SubjectAlternativeName [\n";
    if (this.names == null) {
      null = null + "  null\n";
    } else {
      for (GeneralName generalName : this.names.names())
        null = null + "  " + generalName + "\n"; 
    } 
    return null + "]\n";
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.SubjectAlternativeName_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("subject_name")) {
      if (!(paramObject instanceof GeneralNames))
        throw new IOException("Attribute value should be of type GeneralNames."); 
      this.names = (GeneralNames)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
    } 
    encodeThis();
  }
  
  public GeneralNames get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("subject_name"))
      return this.names; 
    throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("subject_name")) {
      this.names = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:SubjectAlternativeName.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("subject_name");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "SubjectAlternativeName"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\SubjectAlternativeNameExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */