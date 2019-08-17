package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class AuthorityKeyIdentifierExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.AuthorityKeyIdentifier";
  
  public static final String NAME = "AuthorityKeyIdentifier";
  
  public static final String KEY_ID = "key_id";
  
  public static final String AUTH_NAME = "auth_name";
  
  public static final String SERIAL_NUMBER = "serial_number";
  
  private static final byte TAG_ID = 0;
  
  private static final byte TAG_NAMES = 1;
  
  private static final byte TAG_SERIAL_NUM = 2;
  
  private KeyIdentifier id = null;
  
  private GeneralNames names = null;
  
  private SerialNumber serialNum = null;
  
  private void encodeThis() throws IOException {
    if (this.id == null && this.names == null && this.serialNum == null) {
      this.extensionValue = null;
      return;
    } 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    if (this.id != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      this.id.encode(derOutputStream);
      derOutputStream2.writeImplicit(DerValue.createTag(-128, false, (byte)0), derOutputStream);
    } 
    try {
      if (this.names != null) {
        DerOutputStream derOutputStream = new DerOutputStream();
        this.names.encode(derOutputStream);
        derOutputStream2.writeImplicit(DerValue.createTag(-128, true, (byte)1), derOutputStream);
      } 
    } catch (Exception exception) {
      throw new IOException(exception.toString());
    } 
    if (this.serialNum != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      this.serialNum.encode(derOutputStream);
      derOutputStream2.writeImplicit(DerValue.createTag(-128, false, (byte)2), derOutputStream);
    } 
    derOutputStream1.write((byte)48, derOutputStream2);
    this.extensionValue = derOutputStream1.toByteArray();
  }
  
  public AuthorityKeyIdentifierExtension(KeyIdentifier paramKeyIdentifier, GeneralNames paramGeneralNames, SerialNumber paramSerialNumber) throws IOException {
    this.id = paramKeyIdentifier;
    this.names = paramGeneralNames;
    this.serialNum = paramSerialNumber;
    this.extensionId = PKIXExtensions.AuthorityKey_Id;
    this.critical = false;
    encodeThis();
  }
  
  public AuthorityKeyIdentifierExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.AuthorityKey_Id;
    this.critical = paramBoolean.booleanValue();
    this.extensionValue = (byte[])paramObject;
    DerValue derValue = new DerValue(this.extensionValue);
    if (derValue.tag != 48)
      throw new IOException("Invalid encoding for AuthorityKeyIdentifierExtension."); 
    while (derValue.data != null && derValue.data.available() != 0) {
      DerValue derValue1 = derValue.data.getDerValue();
      if (derValue1.isContextSpecific((byte)0) && !derValue1.isConstructed()) {
        if (this.id != null)
          throw new IOException("Duplicate KeyIdentifier in AuthorityKeyIdentifier."); 
        derValue1.resetTag((byte)4);
        this.id = new KeyIdentifier(derValue1);
        continue;
      } 
      if (derValue1.isContextSpecific((byte)1) && derValue1.isConstructed()) {
        if (this.names != null)
          throw new IOException("Duplicate GeneralNames in AuthorityKeyIdentifier."); 
        derValue1.resetTag((byte)48);
        this.names = new GeneralNames(derValue1);
        continue;
      } 
      if (derValue1.isContextSpecific((byte)2) && !derValue1.isConstructed()) {
        if (this.serialNum != null)
          throw new IOException("Duplicate SerialNumber in AuthorityKeyIdentifier."); 
        derValue1.resetTag((byte)2);
        this.serialNum = new SerialNumber(derValue1);
        continue;
      } 
      throw new IOException("Invalid encoding of AuthorityKeyIdentifierExtension.");
    } 
  }
  
  public String toString() {
    String str = super.toString() + "AuthorityKeyIdentifier [\n";
    if (this.id != null)
      str = str + this.id.toString(); 
    if (this.names != null)
      str = str + this.names.toString() + "\n"; 
    if (this.serialNum != null)
      str = str + this.serialNum.toString() + "\n"; 
    return str + "]\n";
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.AuthorityKey_Id;
      this.critical = false;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramString.equalsIgnoreCase("key_id")) {
      if (!(paramObject instanceof KeyIdentifier))
        throw new IOException("Attribute value should be of type KeyIdentifier."); 
      this.id = (KeyIdentifier)paramObject;
    } else if (paramString.equalsIgnoreCase("auth_name")) {
      if (!(paramObject instanceof GeneralNames))
        throw new IOException("Attribute value should be of type GeneralNames."); 
      this.names = (GeneralNames)paramObject;
    } else if (paramString.equalsIgnoreCase("serial_number")) {
      if (!(paramObject instanceof SerialNumber))
        throw new IOException("Attribute value should be of type SerialNumber."); 
      this.serialNum = (SerialNumber)paramObject;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
    } 
    encodeThis();
  }
  
  public Object get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("key_id"))
      return this.id; 
    if (paramString.equalsIgnoreCase("auth_name"))
      return this.names; 
    if (paramString.equalsIgnoreCase("serial_number"))
      return this.serialNum; 
    throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("key_id")) {
      this.id = null;
    } else if (paramString.equalsIgnoreCase("auth_name")) {
      this.names = null;
    } else if (paramString.equalsIgnoreCase("serial_number")) {
      this.serialNum = null;
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:AuthorityKeyIdentifier.");
    } 
    encodeThis();
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("key_id");
    attributeNameEnumeration.addElement("auth_name");
    attributeNameEnumeration.addElement("serial_number");
    return attributeNameEnumeration.elements();
  }
  
  public String getName() { return "AuthorityKeyIdentifier"; }
  
  public byte[] getEncodedKeyIdentifier() throws IOException {
    if (this.id != null) {
      DerOutputStream derOutputStream = new DerOutputStream();
      this.id.encode(derOutputStream);
      return derOutputStream.toByteArray();
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\AuthorityKeyIdentifierExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */