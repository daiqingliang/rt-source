package sun.security.pkcs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyRep;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import sun.security.util.Debug;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public class PKCS8Key implements PrivateKey {
  private static final long serialVersionUID = -3836890099307167124L;
  
  protected AlgorithmId algid;
  
  protected byte[] key;
  
  protected byte[] encodedKey;
  
  public static final BigInteger version = BigInteger.ZERO;
  
  public PKCS8Key() {}
  
  private PKCS8Key(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte) throws InvalidKeyException {
    this.algid = paramAlgorithmId;
    this.key = paramArrayOfByte;
    encode();
  }
  
  public static PKCS8Key parse(DerValue paramDerValue) throws IOException {
    PrivateKey privateKey = parseKey(paramDerValue);
    if (privateKey instanceof PKCS8Key)
      return (PKCS8Key)privateKey; 
    throw new IOException("Provider did not return PKCS8Key");
  }
  
  public static PrivateKey parseKey(DerValue paramDerValue) throws IOException {
    PrivateKey privateKey;
    if (paramDerValue.tag != 48)
      throw new IOException("corrupt private key"); 
    BigInteger bigInteger = paramDerValue.data.getBigInteger();
    if (!version.equals(bigInteger))
      throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(bigInteger)); 
    AlgorithmId algorithmId = AlgorithmId.parse(paramDerValue.data.getDerValue());
    try {
      privateKey = buildPKCS8Key(algorithmId, paramDerValue.data.getOctetString());
    } catch (InvalidKeyException invalidKeyException) {
      throw new IOException("corrupt private key");
    } 
    if (paramDerValue.data.available() != 0)
      throw new IOException("excess private key"); 
    return privateKey;
  }
  
  protected void parseKeyBits() { encode(); }
  
  static PrivateKey buildPKCS8Key(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte) throws IOException, InvalidKeyException {
    DerOutputStream derOutputStream = new DerOutputStream();
    encode(derOutputStream, paramAlgorithmId, paramArrayOfByte);
    PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(derOutputStream.toByteArray());
    try {
      KeyFactory keyFactory = KeyFactory.getInstance(paramAlgorithmId.getName());
      return keyFactory.generatePrivate(pKCS8EncodedKeySpec);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
    
    } catch (InvalidKeySpecException invalidKeySpecException) {}
    String str = "";
    try {
      Provider provider = Security.getProvider("SUN");
      if (provider == null)
        throw new InstantiationException(); 
      str = provider.getProperty("PrivateKey.PKCS#8." + paramAlgorithmId.getName());
      if (str == null)
        throw new InstantiationException(); 
      Class clazz = null;
      try {
        clazz = Class.forName(str);
      } catch (ClassNotFoundException classNotFoundException) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        if (classLoader != null)
          clazz = classLoader.loadClass(str); 
      } 
      Object object = null;
      if (clazz != null)
        object = clazz.newInstance(); 
      if (object instanceof PKCS8Key) {
        PKCS8Key pKCS8Key1 = (PKCS8Key)object;
        pKCS8Key1.algid = paramAlgorithmId;
        pKCS8Key1.key = paramArrayOfByte;
        pKCS8Key1.parseKeyBits();
        return pKCS8Key1;
      } 
    } catch (ClassNotFoundException classNotFoundException) {
    
    } catch (InstantiationException instantiationException) {
    
    } catch (IllegalAccessException illegalAccessException) {
      throw new IOException(str + " [internal error]");
    } 
    PKCS8Key pKCS8Key = new PKCS8Key();
    pKCS8Key.algid = paramAlgorithmId;
    pKCS8Key.key = paramArrayOfByte;
    return pKCS8Key;
  }
  
  public String getAlgorithm() { return this.algid.getName(); }
  
  public AlgorithmId getAlgorithmId() { return this.algid; }
  
  public final void encode(DerOutputStream paramDerOutputStream) throws IOException { encode(paramDerOutputStream, this.algid, this.key); }
  
  public byte[] getEncoded() {
    byte[] arrayOfByte = null;
    try {
      arrayOfByte = encode();
    } catch (InvalidKeyException invalidKeyException) {}
    return arrayOfByte;
  }
  
  public String getFormat() { return "PKCS#8"; }
  
  public byte[] encode() {
    if (this.encodedKey == null)
      try {
        DerOutputStream derOutputStream = new DerOutputStream();
        encode(derOutputStream);
        this.encodedKey = derOutputStream.toByteArray();
      } catch (IOException iOException) {
        throw new InvalidKeyException("IOException : " + iOException.getMessage());
      }  
    return (byte[])this.encodedKey.clone();
  }
  
  public void decode(InputStream paramInputStream) throws InvalidKeyException {
    try {
      DerValue derValue = new DerValue(paramInputStream);
      if (derValue.tag != 48)
        throw new InvalidKeyException("invalid key format"); 
      BigInteger bigInteger = derValue.data.getBigInteger();
      if (!bigInteger.equals(version))
        throw new IOException("version mismatch: (supported: " + Debug.toHexString(version) + ", parsed: " + Debug.toHexString(bigInteger)); 
      this.algid = AlgorithmId.parse(derValue.data.getDerValue());
      this.key = derValue.data.getOctetString();
      parseKeyBits();
      if (derValue.data.available() != 0);
    } catch (IOException iOException) {
      throw new InvalidKeyException("IOException : " + iOException.getMessage());
    } 
  }
  
  public void decode(byte[] paramArrayOfByte) throws InvalidKeyException { decode(new ByteArrayInputStream(paramArrayOfByte)); }
  
  protected Object writeReplace() throws ObjectStreamException { return new KeyRep(KeyRep.Type.PRIVATE, getAlgorithm(), getFormat(), getEncoded()); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException {
    try {
      decode(paramObjectInputStream);
    } catch (InvalidKeyException invalidKeyException) {
      invalidKeyException.printStackTrace();
      throw new IOException("deserialized key is invalid: " + invalidKeyException.getMessage());
    } 
  }
  
  static void encode(DerOutputStream paramDerOutputStream, AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putInteger(version);
    paramAlgorithmId.encode(derOutputStream);
    derOutputStream.putOctetString(paramArrayOfByte);
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof Key) {
      byte[] arrayOfByte1;
      if (this.encodedKey != null) {
        arrayOfByte1 = this.encodedKey;
      } else {
        arrayOfByte1 = getEncoded();
      } 
      byte[] arrayOfByte2 = ((Key)paramObject).getEncoded();
      return MessageDigest.isEqual(arrayOfByte1, arrayOfByte2);
    } 
    return false;
  }
  
  public int hashCode() {
    byte b1 = 0;
    byte[] arrayOfByte = getEncoded();
    for (byte b2 = 1; b2 < arrayOfByte.length; b2++)
      b1 += arrayOfByte[b2] * b2; 
    return b1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs\PKCS8Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */