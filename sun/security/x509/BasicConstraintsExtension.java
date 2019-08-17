package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class BasicConstraintsExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.BasicConstraints";
  
  public static final String NAME = "BasicConstraints";
  
  public static final String IS_CA = "is_ca";
  
  public static final String PATH_LEN = "path_len";
  
  private boolean ca = false;
  
  private int pathLen = -1;
  
  private void encodeThis() throws IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    if (this.ca) {
      derOutputStream2.putBoolean(this.ca);
      if (this.pathLen >= 0)
        derOutputStream2.putInteger(this.pathLen); 
    } 
    derOutputStream1.write((byte)48, derOutputStream2);
    this.extensionValue = derOutputStream1.toByteArray();
  }
  
  public BasicConstraintsExtension(boolean paramBoolean, int paramInt) throws IOException { this(Boolean.valueOf(paramBoolean), paramBoolean, paramInt); }
  
  public BasicConstraintsExtension(Boolean paramBoolean, boolean paramBoolean1, int paramInt) throws IOException {
    this.ca = paramBoolean1;
    this.pathLen = paramInt;
    this.extensionId = PKIXExtensions.BasicConstraints_Id;
    this.critical = paramBoolean.booleanValue();
    encodeThis();
  }
  
  public BasicConstraintsExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.BasicConstraints_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue1 = new DerValue(this.extensionValue);
    if (derValue1.tag != 48)
      throw new IOException("Invalid encoding of BasicConstraints"); 
    if (derValue1.data == null || derValue1.data.available() == 0)
      return; 
    DerValue derValue2 = derValue1.data.getDerValue();
    if (derValue2.tag != 1)
      return; 
    this.ca = derValue2.getBoolean();
    if (derValue1.data.available() == 0) {
      this.pathLen = Integer.MAX_VALUE;
      return;
    } 
    derValue2 = derValue1.data.getDerValue();
    if (derValue2.tag != 2)
      throw new IOException("Invalid encoding of BasicConstraints"); 
    this.pathLen = derValue2.getInteger();
  }
  
  public String toString() {
    String str = super.toString() + "BasicConstraints:[\n";
    str = str + (this.ca ? "  CA:true" : "  CA:false") + "\n";
    if (this.pathLen >= 0) {
      str = str + "  PathLen:" + this.pathLen + "\n";
    } else {
      str = str + "  PathLen: undefined\n";
    } 
    return str + "]\n";
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.BasicConstraints_Id;
      if (this.ca) {
        this.critical = true;
      } else {
        this.critical = false;
      } 
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("is_ca")) {
      if (!(paramObject instanceof Boolean))
        throw new IOException("Attribute value should be of type Boolean."); 
      this.ca = ((Boolean)paramObject).booleanValue();
    } else if (paramString.equalsIgnoreCase("path_len")) {
      if (!(paramObject instanceof Integer))
        throw new IOException("Attribute value should be of type Integer."); 
      this.pathLen = ((Integer)paramObject).intValue();
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
    } 
    encodeThis();
  }
  
  public Object get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("is_ca"))
      return Boolean.valueOf(this.ca); 
    if (paramString.equalsIgnoreCase("path_len"))
      return Integer.valueOf(this.pathLen); 
    throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("is_ca")) {
      this.ca = false;
    } else if (paramString.equalsIgnoreCase("path_len")) {
      this.pathLen = -1;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:BasicConstraints.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("is_ca");
    attributeNameEnumeration.addElement("path_len");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "BasicConstraints"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\BasicConstraintsExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */