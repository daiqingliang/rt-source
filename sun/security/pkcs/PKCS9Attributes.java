package sun.security.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class PKCS9Attributes {
  private final Hashtable<ObjectIdentifier, PKCS9Attribute> attributes = new Hashtable(3);
  
  private final Hashtable<ObjectIdentifier, ObjectIdentifier> permittedAttributes;
  
  private final byte[] derEncoding;
  
  private boolean ignoreUnsupportedAttributes = false;
  
  public PKCS9Attributes(ObjectIdentifier[] paramArrayOfObjectIdentifier, DerInputStream paramDerInputStream) throws IOException {
    if (paramArrayOfObjectIdentifier != null) {
      this.permittedAttributes = new Hashtable(paramArrayOfObjectIdentifier.length);
      for (byte b = 0; b < paramArrayOfObjectIdentifier.length; b++)
        this.permittedAttributes.put(paramArrayOfObjectIdentifier[b], paramArrayOfObjectIdentifier[b]); 
    } else {
      this.permittedAttributes = null;
    } 
    this.derEncoding = decode(paramDerInputStream);
  }
  
  public PKCS9Attributes(DerInputStream paramDerInputStream) throws IOException { this(paramDerInputStream, false); }
  
  public PKCS9Attributes(DerInputStream paramDerInputStream, boolean paramBoolean) throws IOException {
    this.ignoreUnsupportedAttributes = paramBoolean;
    this.derEncoding = decode(paramDerInputStream);
    this.permittedAttributes = null;
  }
  
  public PKCS9Attributes(PKCS9Attribute[] paramArrayOfPKCS9Attribute) throws IllegalArgumentException, IOException {
    for (byte b = 0; b < paramArrayOfPKCS9Attribute.length; b++) {
      ObjectIdentifier objectIdentifier = paramArrayOfPKCS9Attribute[b].getOID();
      if (this.attributes.containsKey(objectIdentifier))
        throw new IllegalArgumentException("PKCSAttribute " + paramArrayOfPKCS9Attribute[b].getOID() + " duplicated while constructing PKCS9Attributes."); 
      this.attributes.put(objectIdentifier, paramArrayOfPKCS9Attribute[b]);
    } 
    this.derEncoding = generateDerEncoding();
    this.permittedAttributes = null;
  }
  
  private byte[] decode(DerInputStream paramDerInputStream) throws IOException {
    DerValue derValue = paramDerInputStream.getDerValue();
    byte[] arrayOfByte = derValue.toByteArray();
    arrayOfByte[0] = 49;
    DerInputStream derInputStream = new DerInputStream(arrayOfByte);
    DerValue[] arrayOfDerValue = derInputStream.getSet(3, true);
    boolean bool = true;
    for (byte b = 0; b < arrayOfDerValue.length; b++) {
      PKCS9Attribute pKCS9Attribute;
      try {
        pKCS9Attribute = new PKCS9Attribute(arrayOfDerValue[b]);
      } catch (ParsingException parsingException) {
        if (this.ignoreUnsupportedAttributes) {
          bool = false;
        } else {
          throw parsingException;
        } 
      } 
      ObjectIdentifier objectIdentifier = pKCS9Attribute.getOID();
      if (this.attributes.get(objectIdentifier) != null)
        throw new IOException("Duplicate PKCS9 attribute: " + objectIdentifier); 
      if (this.permittedAttributes != null && !this.permittedAttributes.containsKey(objectIdentifier))
        throw new IOException("Attribute " + objectIdentifier + " not permitted in this attribute set"); 
      this.attributes.put(objectIdentifier, pKCS9Attribute);
    } 
    return bool ? arrayOfByte : generateDerEncoding();
  }
  
  public void encode(byte paramByte, OutputStream paramOutputStream) throws IOException {
    paramOutputStream.write(paramByte);
    paramOutputStream.write(this.derEncoding, 1, this.derEncoding.length - 1);
  }
  
  private byte[] generateDerEncoding() throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    Object[] arrayOfObject = this.attributes.values().toArray();
    derOutputStream.putOrderedSetOf((byte)49, castToDerEncoder(arrayOfObject));
    return derOutputStream.toByteArray();
  }
  
  public byte[] getDerEncoding() throws IOException { return (byte[])this.derEncoding.clone(); }
  
  public PKCS9Attribute getAttribute(ObjectIdentifier paramObjectIdentifier) { return (PKCS9Attribute)this.attributes.get(paramObjectIdentifier); }
  
  public PKCS9Attribute getAttribute(String paramString) { return (PKCS9Attribute)this.attributes.get(PKCS9Attribute.getOID(paramString)); }
  
  public PKCS9Attribute[] getAttributes() {
    PKCS9Attribute[] arrayOfPKCS9Attribute = new PKCS9Attribute[this.attributes.size()];
    byte b1 = 0;
    for (byte b2 = 1; b2 < PKCS9Attribute.PKCS9_OIDS.length && b1 < arrayOfPKCS9Attribute.length; b2++) {
      arrayOfPKCS9Attribute[b1] = getAttribute(PKCS9Attribute.PKCS9_OIDS[b2]);
      if (arrayOfPKCS9Attribute[b1] != null)
        b1++; 
    } 
    return arrayOfPKCS9Attribute;
  }
  
  public Object getAttributeValue(ObjectIdentifier paramObjectIdentifier) throws IOException {
    try {
      return getAttribute(paramObjectIdentifier).getValue();
    } catch (NullPointerException nullPointerException) {
      throw new IOException("No value found for attribute " + paramObjectIdentifier);
    } 
  }
  
  public Object getAttributeValue(String paramString) throws IOException {
    ObjectIdentifier objectIdentifier = PKCS9Attribute.getOID(paramString);
    if (objectIdentifier == null)
      throw new IOException("Attribute name " + paramString + " not recognized or not supported."); 
    return getAttributeValue(objectIdentifier);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(200);
    stringBuffer.append("PKCS9 Attributes: [\n\t");
    boolean bool = true;
    for (byte b = 1; b < PKCS9Attribute.PKCS9_OIDS.length; b++) {
      PKCS9Attribute pKCS9Attribute = getAttribute(PKCS9Attribute.PKCS9_OIDS[b]);
      if (pKCS9Attribute != null) {
        if (bool) {
          bool = false;
        } else {
          stringBuffer.append(";\n\t");
        } 
        stringBuffer.append(pKCS9Attribute.toString());
      } 
    } 
    stringBuffer.append("\n\t] (end PKCS9 Attributes)");
    return stringBuffer.toString();
  }
  
  static DerEncoder[] castToDerEncoder(Object[] paramArrayOfObject) {
    DerEncoder[] arrayOfDerEncoder = new DerEncoder[paramArrayOfObject.length];
    for (byte b = 0; b < arrayOfDerEncoder.length; b++)
      arrayOfDerEncoder[b] = (DerEncoder)paramArrayOfObject[b]; 
    return arrayOfDerEncoder;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs\PKCS9Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */