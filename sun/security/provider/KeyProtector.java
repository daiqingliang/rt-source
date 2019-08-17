package sun.security.provider;

import java.io.IOException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import sun.security.pkcs.EncryptedPrivateKeyInfo;
import sun.security.pkcs.PKCS8Key;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

final class KeyProtector {
  private static final int SALT_LEN = 20;
  
  private static final String DIGEST_ALG = "SHA";
  
  private static final int DIGEST_LEN = 20;
  
  private static final String KEY_PROTECTOR_OID = "1.3.6.1.4.1.42.2.17.1.1";
  
  private byte[] passwdBytes;
  
  private MessageDigest md;
  
  public KeyProtector(char[] paramArrayOfChar) throws NoSuchAlgorithmException {
    if (paramArrayOfChar == null)
      throw new IllegalArgumentException("password can't be null"); 
    this.md = MessageDigest.getInstance("SHA");
    this.passwdBytes = new byte[paramArrayOfChar.length * 2];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < paramArrayOfChar.length) {
      this.passwdBytes[b2++] = (byte)(paramArrayOfChar[b1] >> '\b');
      this.passwdBytes[b2++] = (byte)paramArrayOfChar[b1];
      b1++;
    } 
  }
  
  protected void finalize() {
    if (this.passwdBytes != null) {
      Arrays.fill(this.passwdBytes, (byte)0);
      this.passwdBytes = null;
    } 
  }
  
  public byte[] protect(Key paramKey) throws KeyStoreException {
    int k = 0;
    if (paramKey == null)
      throw new IllegalArgumentException("plaintext key can't be null"); 
    if (!"PKCS#8".equalsIgnoreCase(paramKey.getFormat()))
      throw new KeyStoreException("Cannot get key bytes, not PKCS#8 encoded"); 
    byte[] arrayOfByte2 = paramKey.getEncoded();
    if (arrayOfByte2 == null)
      throw new KeyStoreException("Cannot get key bytes, encoding not supported"); 
    int i = arrayOfByte2.length / 20;
    if (arrayOfByte2.length % 20 != 0)
      i++; 
    byte[] arrayOfByte3 = new byte[20];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(arrayOfByte3);
    byte[] arrayOfByte4 = new byte[arrayOfByte2.length];
    byte b = 0;
    int j = 0;
    byte[] arrayOfByte1 = arrayOfByte3;
    while (b < i) {
      this.md.update(this.passwdBytes);
      this.md.update(arrayOfByte1);
      arrayOfByte1 = this.md.digest();
      this.md.reset();
      if (b < i - 1) {
        System.arraycopy(arrayOfByte1, 0, arrayOfByte4, j, arrayOfByte1.length);
      } else {
        System.arraycopy(arrayOfByte1, 0, arrayOfByte4, j, arrayOfByte4.length - j);
      } 
      b++;
      j += 20;
    } 
    byte[] arrayOfByte5 = new byte[arrayOfByte2.length];
    for (b = 0; b < arrayOfByte5.length; b++)
      arrayOfByte5[b] = (byte)(arrayOfByte2[b] ^ arrayOfByte4[b]); 
    byte[] arrayOfByte6 = new byte[arrayOfByte3.length + arrayOfByte5.length + 20];
    System.arraycopy(arrayOfByte3, 0, arrayOfByte6, k, arrayOfByte3.length);
    k += arrayOfByte3.length;
    System.arraycopy(arrayOfByte5, 0, arrayOfByte6, k, arrayOfByte5.length);
    k += arrayOfByte5.length;
    this.md.update(this.passwdBytes);
    Arrays.fill(this.passwdBytes, (byte)0);
    this.passwdBytes = null;
    this.md.update(arrayOfByte2);
    arrayOfByte1 = this.md.digest();
    this.md.reset();
    System.arraycopy(arrayOfByte1, 0, arrayOfByte6, k, arrayOfByte1.length);
    try {
      AlgorithmId algorithmId = new AlgorithmId(new ObjectIdentifier("1.3.6.1.4.1.42.2.17.1.1"));
      return (new EncryptedPrivateKeyInfo(algorithmId, arrayOfByte6)).getEncoded();
    } catch (IOException iOException) {
      throw new KeyStoreException(iOException.getMessage());
    } 
  }
  
  public Key recover(EncryptedPrivateKeyInfo paramEncryptedPrivateKeyInfo) throws UnrecoverableKeyException {
    AlgorithmId algorithmId = paramEncryptedPrivateKeyInfo.getAlgorithm();
    if (!algorithmId.getOID().toString().equals("1.3.6.1.4.1.42.2.17.1.1"))
      throw new UnrecoverableKeyException("Unsupported key protection algorithm"); 
    byte[] arrayOfByte2 = paramEncryptedPrivateKeyInfo.getEncryptedData();
    byte[] arrayOfByte3 = new byte[20];
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 0, 20);
    int m = arrayOfByte2.length - 20 - 20;
    int j = m / 20;
    if (m % 20 != 0)
      j++; 
    byte[] arrayOfByte4 = new byte[m];
    System.arraycopy(arrayOfByte2, 20, arrayOfByte4, 0, m);
    byte[] arrayOfByte5 = new byte[arrayOfByte4.length];
    int i = 0;
    int k = 0;
    byte[] arrayOfByte1 = arrayOfByte3;
    while (i < j) {
      this.md.update(this.passwdBytes);
      this.md.update(arrayOfByte1);
      arrayOfByte1 = this.md.digest();
      this.md.reset();
      if (i < j - 1) {
        System.arraycopy(arrayOfByte1, 0, arrayOfByte5, k, arrayOfByte1.length);
      } else {
        System.arraycopy(arrayOfByte1, 0, arrayOfByte5, k, arrayOfByte5.length - k);
      } 
      i++;
      k += 20;
    } 
    byte[] arrayOfByte6 = new byte[arrayOfByte4.length];
    for (i = 0; i < arrayOfByte6.length; i++)
      arrayOfByte6[i] = (byte)(arrayOfByte4[i] ^ arrayOfByte5[i]); 
    this.md.update(this.passwdBytes);
    Arrays.fill(this.passwdBytes, (byte)0);
    this.passwdBytes = null;
    this.md.update(arrayOfByte6);
    arrayOfByte1 = this.md.digest();
    this.md.reset();
    for (i = 0; i < arrayOfByte1.length; i++) {
      if (arrayOfByte1[i] != arrayOfByte2[20 + m + i])
        throw new UnrecoverableKeyException("Cannot recover key"); 
    } 
    try {
      return PKCS8Key.parseKey(new DerValue(arrayOfByte6));
    } catch (IOException iOException) {
      throw new UnrecoverableKeyException(iOException.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\KeyProtector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */