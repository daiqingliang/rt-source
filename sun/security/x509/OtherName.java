package sun.security.x509;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class OtherName implements GeneralNameInterface {
  private String name;
  
  private ObjectIdentifier oid;
  
  private byte[] nameValue = null;
  
  private GeneralNameInterface gni = null;
  
  private static final byte TAG_VALUE = 0;
  
  private int myhash = -1;
  
  public OtherName(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte) throws IOException {
    if (paramObjectIdentifier == null || paramArrayOfByte == null)
      throw new NullPointerException("parameters may not be null"); 
    this.oid = paramObjectIdentifier;
    this.nameValue = paramArrayOfByte;
    this.gni = getGNI(paramObjectIdentifier, paramArrayOfByte);
    if (this.gni != null) {
      this.name = this.gni.toString();
    } else {
      this.name = "Unrecognized ObjectIdentifier: " + paramObjectIdentifier.toString();
    } 
  }
  
  public OtherName(DerValue paramDerValue) throws IOException {
    DerInputStream derInputStream = paramDerValue.toDerInputStream();
    this.oid = derInputStream.getOID();
    DerValue derValue = derInputStream.getDerValue();
    this.nameValue = derValue.toByteArray();
    this.gni = getGNI(this.oid, this.nameValue);
    if (this.gni != null) {
      this.name = this.gni.toString();
    } else {
      this.name = "Unrecognized ObjectIdentifier: " + this.oid.toString();
    } 
  }
  
  public ObjectIdentifier getOID() { return this.oid; }
  
  public byte[] getNameValue() { return (byte[])this.nameValue.clone(); }
  
  private GeneralNameInterface getGNI(ObjectIdentifier paramObjectIdentifier, byte[] paramArrayOfByte) throws IOException {
    try {
      Class clazz = OIDMap.getClass(paramObjectIdentifier);
      if (clazz == null)
        return null; 
      Class[] arrayOfClass = { Object.class };
      Constructor constructor = clazz.getConstructor(arrayOfClass);
      Object[] arrayOfObject = { paramArrayOfByte };
      return (GeneralNameInterface)constructor.newInstance(arrayOfObject);
    } catch (Exception exception) {
      throw new IOException("Instantiation error: " + exception, exception);
    } 
  }
  
  public int getType() { return 0; }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    if (this.gni != null) {
      this.gni.encode(paramDerOutputStream);
      return;
    } 
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putOID(this.oid);
    derOutputStream.write(DerValue.createTag(-128, true, (byte)0), this.nameValue);
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public boolean equals(Object paramObject) {
    boolean bool;
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof OtherName))
      return false; 
    OtherName otherName = (OtherName)paramObject;
    if (!otherName.oid.equals(this.oid))
      return false; 
    GeneralNameInterface generalNameInterface = null;
    try {
      generalNameInterface = getGNI(otherName.oid, otherName.nameValue);
    } catch (IOException iOException) {
      return false;
    } 
    if (generalNameInterface != null) {
      try {
        bool = (generalNameInterface.constrains(this) == 0);
      } catch (UnsupportedOperationException unsupportedOperationException) {
        bool = false;
      } 
    } else {
      bool = Arrays.equals(this.nameValue, otherName.nameValue);
    } 
    return bool;
  }
  
  public int hashCode() {
    if (this.myhash == -1) {
      this.myhash = 37 + this.oid.hashCode();
      for (byte b = 0; b < this.nameValue.length; b++)
        this.myhash = 37 * this.myhash + this.nameValue[b]; 
    } 
    return this.myhash;
  }
  
  public String toString() { return "Other-Name: " + this.name; }
  
  public int constrains(GeneralNameInterface paramGeneralNameInterface) {
    byte b;
    if (paramGeneralNameInterface == null) {
      b = -1;
    } else if (paramGeneralNameInterface.getType() != 0) {
      b = -1;
    } else {
      throw new UnsupportedOperationException("Narrowing, widening, and matching are not supported for OtherName.");
    } 
    return b;
  }
  
  public int subtreeDepth() { throw new UnsupportedOperationException("subtreeDepth() not supported for generic OtherName"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\OtherName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */