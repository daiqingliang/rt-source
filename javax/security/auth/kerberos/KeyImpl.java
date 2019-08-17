package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import javax.crypto.SecretKey;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import sun.misc.HexDumpEncoder;
import sun.security.krb5.Asn1Exception;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.util.DerValue;

class KeyImpl implements SecretKey, Destroyable, Serializable {
  private static final long serialVersionUID = -7889313790214321193L;
  
  private byte[] keyBytes;
  
  private int keyType;
  
  public KeyImpl(byte[] paramArrayOfByte, int paramInt) {
    this.keyBytes = (byte[])paramArrayOfByte.clone();
    this.keyType = paramInt;
  }
  
  public KeyImpl(KerberosPrincipal paramKerberosPrincipal, char[] paramArrayOfChar, String paramString) {
    try {
      PrincipalName principalName = new PrincipalName(paramKerberosPrincipal.getName());
      EncryptionKey encryptionKey = new EncryptionKey(paramArrayOfChar, principalName.getSalt(), paramString);
      this.keyBytes = encryptionKey.getBytes();
      this.keyType = encryptionKey.getEType();
    } catch (KrbException krbException) {
      throw new IllegalArgumentException(krbException.getMessage());
    } 
  }
  
  public final int getKeyType() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return this.keyType;
  }
  
  public final String getAlgorithm() { return getAlgorithmName(this.keyType); }
  
  private String getAlgorithmName(int paramInt) {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    switch (paramInt) {
      case 1:
      case 3:
        return "DES";
      case 16:
        return "DESede";
      case 23:
        return "ArcFourHmac";
      case 17:
        return "AES128";
      case 18:
        return "AES256";
      case 0:
        return "NULL";
    } 
    throw new IllegalArgumentException("Unsupported encryption type: " + paramInt);
  }
  
  public final String getFormat() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return "RAW";
  }
  
  public final byte[] getEncoded() {
    if (this.destroyed)
      throw new IllegalStateException("This key is no longer valid"); 
    return (byte[])this.keyBytes.clone();
  }
  
  public void destroy() throws DestroyFailedException {
    if (!this.destroyed) {
      this.destroyed = true;
      Arrays.fill(this.keyBytes, (byte)0);
    } 
  }
  
  public boolean isDestroyed() { return this.destroyed; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.destroyed)
      throw new IOException("This key is no longer valid"); 
    try {
      paramObjectOutputStream.writeObject((new EncryptionKey(this.keyType, this.keyBytes)).asn1Encode());
    } catch (Asn1Exception asn1Exception) {
      throw new IOException(asn1Exception.getMessage());
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    try {
      EncryptionKey encryptionKey = new EncryptionKey(new DerValue((byte[])paramObjectInputStream.readObject()));
      this.keyType = encryptionKey.getEType();
      this.keyBytes = encryptionKey.getBytes();
    } catch (Asn1Exception asn1Exception) {
      throw new IOException(asn1Exception.getMessage());
    } 
  }
  
  public String toString() {
    HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
    return "EncryptionKey: keyType=" + this.keyType + " keyBytes (hex dump)=" + ((this.keyBytes == null || this.keyBytes.length == 0) ? " Empty Key" : ('\n' + hexDumpEncoder.encodeBuffer(this.keyBytes) + '\n'));
  }
  
  public int hashCode() {
    int i = 17;
    if (isDestroyed())
      return i; 
    i = 37 * i + Arrays.hashCode(this.keyBytes);
    return 37 * i + this.keyType;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof KeyImpl))
      return false; 
    KeyImpl keyImpl = (KeyImpl)paramObject;
    return (isDestroyed() || keyImpl.isDestroyed()) ? false : (!(this.keyType != keyImpl.getKeyType() || !Arrays.equals(this.keyBytes, keyImpl.getEncoded())));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\KeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */