package sun.security.provider.certpath;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;
import javax.security.auth.x500.X500Principal;
import sun.security.util.DerValue;
import sun.security.x509.KeyIdentifier;

public final class ResponderId {
  private Type type;
  
  private X500Principal responderName;
  
  private KeyIdentifier responderKeyId;
  
  private byte[] encodedRid;
  
  public ResponderId(X500Principal paramX500Principal) throws IOException {
    this.responderName = paramX500Principal;
    this.responderKeyId = null;
    this.encodedRid = principalToBytes();
    this.type = Type.BY_NAME;
  }
  
  public ResponderId(PublicKey paramPublicKey) throws IOException {
    this.responderKeyId = new KeyIdentifier(paramPublicKey);
    this.responderName = null;
    this.encodedRid = keyIdToBytes();
    this.type = Type.BY_KEY;
  }
  
  public ResponderId(byte[] paramArrayOfByte) throws IOException {
    DerValue derValue = new DerValue(paramArrayOfByte);
    if (derValue.isContextSpecific((byte)Type.BY_NAME.value()) && derValue.isConstructed()) {
      this.responderName = new X500Principal(derValue.getDataBytes());
      this.encodedRid = principalToBytes();
      this.type = Type.BY_NAME;
    } else if (derValue.isContextSpecific((byte)Type.BY_KEY.value()) && derValue.isConstructed()) {
      this.responderKeyId = new KeyIdentifier(new DerValue(derValue.getDataBytes()));
      this.encodedRid = keyIdToBytes();
      this.type = Type.BY_KEY;
    } else {
      throw new IOException("Invalid ResponderId content");
    } 
  }
  
  public byte[] getEncoded() { return (byte[])this.encodedRid.clone(); }
  
  public Type getType() { return this.type; }
  
  public int length() { return this.encodedRid.length; }
  
  public X500Principal getResponderName() { return this.responderName; }
  
  public KeyIdentifier getKeyIdentifier() { return this.responderKeyId; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (paramObject instanceof ResponderId) {
      ResponderId responderId = (ResponderId)paramObject;
      return Arrays.equals(this.encodedRid, responderId.getEncoded());
    } 
    return false;
  }
  
  public int hashCode() { return Arrays.hashCode(this.encodedRid); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    switch (this.type) {
      case BY_NAME:
        stringBuilder.append(this.type).append(": ").append(this.responderName);
        return stringBuilder.toString();
      case BY_KEY:
        stringBuilder.append(this.type).append(": ");
        for (byte b : this.responderKeyId.getIdentifier()) {
          stringBuilder.append(String.format("%02X", new Object[] { Byte.valueOf(b) }));
        } 
        return stringBuilder.toString();
    } 
    stringBuilder.append("Unknown ResponderId Type: ").append(this.type);
    return stringBuilder.toString();
  }
  
  private byte[] principalToBytes() {
    DerValue derValue = new DerValue(DerValue.createTag(-128, true, (byte)Type.BY_NAME.value()), this.responderName.getEncoded());
    return derValue.toByteArray();
  }
  
  private byte[] keyIdToBytes() {
    DerValue derValue1;
    DerValue derValue2 = new DerValue((derValue1 = new DerValue((byte)4, this.responderKeyId.getIdentifier())).createTag(-128, true, (byte)Type.BY_KEY.value()), derValue1.toByteArray());
    return derValue2.toByteArray();
  }
  
  public enum Type {
    BY_NAME(1, "byName"),
    BY_KEY(2, "byKey");
    
    private final int tagNumber;
    
    private final String ridTypeName;
    
    Type(String param1String1, String param1String2) {
      this.tagNumber = param1String1;
      this.ridTypeName = param1String2;
    }
    
    public int value() { return this.tagNumber; }
    
    public String toString() { return this.ridTypeName; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\certpath\ResponderId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */