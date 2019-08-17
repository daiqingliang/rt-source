package java.security;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Pattern;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public final class PKCS12Attribute implements KeyStore.Entry.Attribute {
  private static final Pattern COLON_SEPARATED_HEX_PAIRS = Pattern.compile("^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2})+$");
  
  private String name;
  
  private String value;
  
  private byte[] encoded;
  
  private int hashValue = -1;
  
  public PKCS12Attribute(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      throw new NullPointerException(); 
    try {
      objectIdentifier = new ObjectIdentifier(paramString1);
    } catch (IOException iOException) {
      throw new IllegalArgumentException("Incorrect format: name", iOException);
    } 
    this.name = paramString1;
    int i = paramString2.length();
    if (paramString2.charAt(0) == '[' && paramString2.charAt(i - 1) == ']') {
      arrayOfString = paramString2.substring(1, i - 1).split(", ");
    } else {
      arrayOfString = new String[] { paramString2 };
    } 
    this.value = paramString2;
    try {
      this.encoded = encode(objectIdentifier, arrayOfString);
    } catch (IOException iOException) {
      throw new IllegalArgumentException("Incorrect format: value", iOException);
    } 
  }
  
  public PKCS12Attribute(byte[] paramArrayOfByte) {
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    this.encoded = (byte[])paramArrayOfByte.clone();
    try {
      parse(paramArrayOfByte);
    } catch (IOException iOException) {
      throw new IllegalArgumentException("Incorrect format: encoded", iOException);
    } 
  }
  
  public String getName() { return this.name; }
  
  public String getValue() { return this.value; }
  
  public byte[] getEncoded() { return (byte[])this.encoded.clone(); }
  
  public boolean equals(Object paramObject) { return (this == paramObject) ? true : (!(paramObject instanceof PKCS12Attribute) ? false : Arrays.equals(this.encoded, ((PKCS12Attribute)paramObject).getEncoded())); }
  
  public int hashCode() {
    if (this.hashValue == -1)
      Arrays.hashCode(this.encoded); 
    return this.hashValue;
  }
  
  public String toString() { return this.name + "=" + this.value; }
  
  private byte[] encode(ObjectIdentifier paramObjectIdentifier, String[] paramArrayOfString) throws IOException {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putOID(paramObjectIdentifier);
    DerOutputStream derOutputStream2 = new DerOutputStream();
    for (String str : paramArrayOfString) {
      if (COLON_SEPARATED_HEX_PAIRS.matcher(str).matches()) {
        byte[] arrayOfByte = (new BigInteger(str.replace(":", ""), 16)).toByteArray();
        if (arrayOfByte[0] == 0)
          arrayOfByte = Arrays.copyOfRange(arrayOfByte, 1, arrayOfByte.length); 
        derOutputStream2.putOctetString(arrayOfByte);
      } else {
        derOutputStream2.putUTF8String(str);
      } 
    } 
    derOutputStream1.write((byte)49, derOutputStream2);
    DerOutputStream derOutputStream3 = new DerOutputStream();
    derOutputStream3.write((byte)48, derOutputStream1);
    return derOutputStream3.toByteArray();
  }
  
  private void parse(byte[] paramArrayOfByte) {
    DerInputStream derInputStream1 = new DerInputStream(paramArrayOfByte);
    DerValue[] arrayOfDerValue1 = derInputStream1.getSequence(2);
    ObjectIdentifier objectIdentifier = arrayOfDerValue1[0].getOID();
    DerInputStream derInputStream2 = new DerInputStream(arrayOfDerValue1[1].toByteArray());
    DerValue[] arrayOfDerValue2 = derInputStream2.getSet(1);
    String[] arrayOfString = new String[arrayOfDerValue2.length];
    for (byte b = 0; b < arrayOfDerValue2.length; b++) {
      if ((arrayOfDerValue2[b]).tag == 4) {
        arrayOfString[b] = Debug.toString(arrayOfDerValue2[b].getOctetString());
      } else {
        String str;
        if ((str = arrayOfDerValue2[b].getAsString()) != null) {
          arrayOfString[b] = str;
        } else if ((arrayOfDerValue2[b]).tag == 6) {
          arrayOfString[b] = arrayOfDerValue2[b].getOID().toString();
        } else if ((arrayOfDerValue2[b]).tag == 24) {
          arrayOfString[b] = arrayOfDerValue2[b].getGeneralizedTime().toString();
        } else if ((arrayOfDerValue2[b]).tag == 23) {
          arrayOfString[b] = arrayOfDerValue2[b].getUTCTime().toString();
        } else if ((arrayOfDerValue2[b]).tag == 2) {
          arrayOfString[b] = arrayOfDerValue2[b].getBigInteger().toString();
        } else if ((arrayOfDerValue2[b]).tag == 1) {
          arrayOfString[b] = String.valueOf(arrayOfDerValue2[b].getBoolean());
        } else {
          arrayOfString[b] = Debug.toString(arrayOfDerValue2[b].getDataBytes());
        } 
      } 
    } 
    this.name = objectIdentifier.toString();
    this.value = (arrayOfString.length == 1) ? arrayOfString[0] : Arrays.toString(arrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\PKCS12Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */