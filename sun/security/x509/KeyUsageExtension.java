package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KeyUsageExtension extends Extension implements CertAttrSet<String> {
  public static final String IDENT = "x509.info.extensions.KeyUsage";
  
  public static final String NAME = "KeyUsage";
  
  public static final String DIGITAL_SIGNATURE = "digital_signature";
  
  public static final String NON_REPUDIATION = "non_repudiation";
  
  public static final String KEY_ENCIPHERMENT = "key_encipherment";
  
  public static final String DATA_ENCIPHERMENT = "data_encipherment";
  
  public static final String KEY_AGREEMENT = "key_agreement";
  
  public static final String KEY_CERTSIGN = "key_certsign";
  
  public static final String CRL_SIGN = "crl_sign";
  
  public static final String ENCIPHER_ONLY = "encipher_only";
  
  public static final String DECIPHER_ONLY = "decipher_only";
  
  private boolean[] bitString;
  
  private void encodeThis() throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putTruncatedUnalignedBitString(new BitArray(this.bitString));
    this.extensionValue = derOutputStream.toByteArray();
  }
  
  private boolean isSet(int paramInt) { return (paramInt < this.bitString.length && this.bitString[paramInt]); }
  
  private void set(int paramInt, boolean paramBoolean) {
    if (paramInt >= this.bitString.length) {
      boolean[] arrayOfBoolean = new boolean[paramInt + 1];
      System.arraycopy(this.bitString, 0, arrayOfBoolean, 0, this.bitString.length);
      this.bitString = arrayOfBoolean;
    } 
    this.bitString[paramInt] = paramBoolean;
  }
  
  public KeyUsageExtension(byte[] paramArrayOfByte) throws IOException {
    this.bitString = (new BitArray(paramArrayOfByte.length * 8, paramArrayOfByte)).toBooleanArray();
    this.extensionId = PKIXExtensions.KeyUsage_Id;
    this.critical = true;
    encodeThis();
  }
  
  public KeyUsageExtension(boolean[] paramArrayOfBoolean) throws IOException {
    this.bitString = paramArrayOfBoolean;
    this.extensionId = PKIXExtensions.KeyUsage_Id;
    this.critical = true;
    encodeThis();
  }
  
  public KeyUsageExtension(BitArray paramBitArray) throws IOException {
    this.bitString = paramBitArray.toBooleanArray();
    this.extensionId = PKIXExtensions.KeyUsage_Id;
    this.critical = true;
    encodeThis();
  }
  
  public KeyUsageExtension(Boolean paramBoolean, Object paramObject) throws IOException {
    this.extensionId = PKIXExtensions.KeyUsage_Id;
    this.critical = paramBoolean.booleanValue();
    byte[] arrayOfByte = (byte[])paramObject;
    if (arrayOfByte[0] == 4) {
      this.extensionValue = (new DerValue(arrayOfByte)).getOctetString();
    } else {
      this.extensionValue = arrayOfByte;
    } 
    DerValue derValue = new DerValue(this.extensionValue);
    this.bitString = derValue.getUnalignedBitString().toBooleanArray();
  }
  
  public KeyUsageExtension() throws IOException {
    this.extensionId = PKIXExtensions.KeyUsage_Id;
    this.critical = true;
    this.bitString = new boolean[0];
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (!(paramObject instanceof Boolean))
      throw new IOException("Attribute must be of type Boolean."); 
    boolean bool = ((Boolean)paramObject).booleanValue();
    if (paramString.equalsIgnoreCase("digital_signature")) {
      set(0, bool);
    } else if (paramString.equalsIgnoreCase("non_repudiation")) {
      set(1, bool);
    } else if (paramString.equalsIgnoreCase("key_encipherment")) {
      set(2, bool);
    } else if (paramString.equalsIgnoreCase("data_encipherment")) {
      set(3, bool);
    } else if (paramString.equalsIgnoreCase("key_agreement")) {
      set(4, bool);
    } else if (paramString.equalsIgnoreCase("key_certsign")) {
      set(5, bool);
    } else if (paramString.equalsIgnoreCase("crl_sign")) {
      set(6, bool);
    } else if (paramString.equalsIgnoreCase("encipher_only")) {
      set(7, bool);
    } else if (paramString.equalsIgnoreCase("decipher_only")) {
      set(8, bool);
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
    } 
    encodeThis();
  }
  
  public Boolean get(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("digital_signature"))
      return Boolean.valueOf(isSet(0)); 
    if (paramString.equalsIgnoreCase("non_repudiation"))
      return Boolean.valueOf(isSet(1)); 
    if (paramString.equalsIgnoreCase("key_encipherment"))
      return Boolean.valueOf(isSet(2)); 
    if (paramString.equalsIgnoreCase("data_encipherment"))
      return Boolean.valueOf(isSet(3)); 
    if (paramString.equalsIgnoreCase("key_agreement"))
      return Boolean.valueOf(isSet(4)); 
    if (paramString.equalsIgnoreCase("key_certsign"))
      return Boolean.valueOf(isSet(5)); 
    if (paramString.equalsIgnoreCase("crl_sign"))
      return Boolean.valueOf(isSet(6)); 
    if (paramString.equalsIgnoreCase("encipher_only"))
      return Boolean.valueOf(isSet(7)); 
    if (paramString.equalsIgnoreCase("decipher_only"))
      return Boolean.valueOf(isSet(8)); 
    throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
  }
  
  public void delete(String paramString) throws IOException {
    if (paramString.equalsIgnoreCase("digital_signature")) {
      set(0, false);
    } else if (paramString.equalsIgnoreCase("non_repudiation")) {
      set(1, false);
    } else if (paramString.equalsIgnoreCase("key_encipherment")) {
      set(2, false);
    } else if (paramString.equalsIgnoreCase("data_encipherment")) {
      set(3, false);
    } else if (paramString.equalsIgnoreCase("key_agreement")) {
      set(4, false);
    } else if (paramString.equalsIgnoreCase("key_certsign")) {
      set(5, false);
    } else if (paramString.equalsIgnoreCase("crl_sign")) {
      set(6, false);
    } else if (paramString.equalsIgnoreCase("encipher_only")) {
      set(7, false);
    } else if (paramString.equalsIgnoreCase("decipher_only")) {
      set(8, false);
    } else {
      throw new IOException("Attribute name not recognized by CertAttrSet:KeyUsage.");
    } 
    encodeThis();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(super.toString());
    stringBuilder.append("KeyUsage [\n");
    if (isSet(0))
      stringBuilder.append("  DigitalSignature\n"); 
    if (isSet(1))
      stringBuilder.append("  Non_repudiation\n"); 
    if (isSet(2))
      stringBuilder.append("  Key_Encipherment\n"); 
    if (isSet(3))
      stringBuilder.append("  Data_Encipherment\n"); 
    if (isSet(4))
      stringBuilder.append("  Key_Agreement\n"); 
    if (isSet(5))
      stringBuilder.append("  Key_CertSign\n"); 
    if (isSet(6))
      stringBuilder.append("  Crl_Sign\n"); 
    if (isSet(7))
      stringBuilder.append("  Encipher_Only\n"); 
    if (isSet(8))
      stringBuilder.append("  Decipher_Only\n"); 
    stringBuilder.append("]\n");
    return stringBuilder.toString();
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    if (this.extensionValue == null) {
      this.extensionId = PKIXExtensions.KeyUsage_Id;
      this.critical = true;
      encodeThis();
    } 
    encode(derOutputStream);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public Enumeration<String> getElements() {
    AttributeNameEnumeration attributeNameEnumeration = new AttributeNameEnumeration();
    attributeNameEnumeration.addElement("digital_signature");
    attributeNameEnumeration.addElement("non_repudiation");
    attributeNameEnumeration.addElement("key_encipherment");
    attributeNameEnumeration.addElement("data_encipherment");
    attributeNameEnumeration.addElement("key_agreement");
    attributeNameEnumeration.addElement("key_certsign");
    attributeNameEnumeration.addElement("crl_sign");
    attributeNameEnumeration.addElement("encipher_only");
    attributeNameEnumeration.addElement("decipher_only");
    return attributeNameEnumeration.elements();
  }
  
  public boolean[] getBits() { return (boolean[])this.bitString.clone(); }
  
  public String getName() { return "KeyUsage"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\KeyUsageExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */