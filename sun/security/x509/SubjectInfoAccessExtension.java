package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class SubjectInfoAccessExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.SubjectInfoAccess";
  
  public static final String NAME = "SubjectInfoAccess";
  
  public static final String DESCRIPTIONS = "descriptions";
  
  private List<AccessDescription> accessDescriptions;
  
  public SubjectInfoAccessExtension(List<AccessDescription> paramList) throws IOException {
    this.accessDescriptions = paramList;
    encodeThis();
  }
  
  public SubjectInfoAccessExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    if (!(paramObject instanceof byte[]))
      throw new IOException("Illegal argument type"); 
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for SubjectInfoAccessExtension."); 
    this.accessDescriptions = new ArrayList();
    while (derValue.data.available() != 0) {
      DerValue derValue1 = derValue.data.getDerValue();
      AccessDescription accessDescription = new AccessDescription(derValue1);
      this.accessDescriptions.add(accessDescription);
    } 
  }
  
  public List<AccessDescription> getAccessDescriptions() { return this.accessDescriptions; }
  
  public String getName() { return "SubjectInfoAccess"; }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.SubjectInfoAccess_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("descriptions")) {
      if (!(paramObject instanceof List))
        throw new IOException("Attribute value should be of type List."); 
      this.accessDescriptions = (List)paramObject;
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
    } 
    encodeThis();
  }
  
  public List<AccessDescription> get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("descriptions"))
      return this.accessDescriptions; 
    throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("descriptions")) {
      this.accessDescriptions = Collections.emptyList();
    } else {
      throw new IOException("Attribute name [" + paramString + "] not recognized by CertAttrSet:SubjectInfoAccessExtension.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("descriptions");
    return attributeNameEnumeration.elements();
  }
  
  private void encodeThis() throws IOException {
    if (this.accessDescriptions.isEmpty()) {
      this.extensionValue = null;
    } else {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      for (AccessDescription accessDescription : this.accessDescriptions)
        accessDescription.encode(derOutputStream1); 
      DerOutputStream derOutputStream2 = new DerOutputStream();
      derOutputStream2.write((byte)48, derOutputStream1);
      this.extensionValue = derOutputStream2.toByteArray();
    } 
  }
  
  public String toString() { return super.toString() + "SubjectInfoAccess [\n  " + this.accessDescriptions + "\n]\n"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\SubjectInfoAccessExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */