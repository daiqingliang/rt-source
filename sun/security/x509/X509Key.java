package sun.security.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.util.BitArray;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class X509Key implements PublicKey {
  private static final long serialVersionUID = -5359250853002055002L;
  
  protected AlgorithmId algid;
  
  @Deprecated
  protected byte[] key = null;
  
  @Deprecated
  private int unusedBits = 0;
  
  private BitArray bitStringKey = null;
  
  protected byte[] encodedKey;
  
  public X509Key() {}
  
  private X509Key(AlgorithmId paramAlgorithmId, BitArray paramBitArray) throws InvalidKeyException {
    this.algid = paramAlgorithmId;
    setKey(paramBitArray);
    encode();
  }
  
  protected void setKey(BitArray paramBitArray) {
    this.bitStringKey = (BitArray)paramBitArray.clone();
    this.key = paramBitArray.toByteArray();
    int i = paramBitArray.length() % 8;
    this.unusedBits = (i == 0) ? 0 : (8 - i);
  }
  
  protected BitArray getKey() {
    this.bitStringKey = new BitArray(this.key.length * 8 - this.unusedBits, this.key);
    return (BitArray)this.bitStringKey.clone();
  }
  
  public static PublicKey parse(DerValue paramDerValue) throws IOException {
    PublicKey publicKey;
    if (paramDerValue.tag != 48)
      throw new IOException("corrupt subject key"); 
    AlgorithmId algorithmId = AlgorithmId.parse(paramDerValue.data.getDerValue());
    try {
      publicKey = buildX509Key(algorithmId, paramDerValue.data.getUnalignedBitString());
    } catch (InvalidKeyException invalidKeyException) {
      throw new IOException("subject key, " + invalidKeyException.getMessage(), invalidKeyException);
    } 
    if (paramDerValue.data.available() != 0)
      throw new IOException("excess subject key"); 
    return publicKey;
  }
  
  protected void parseKeyBits() { encode(); }
  
  static PublicKey buildX509Key(AlgorithmId paramAlgorithmId, BitArray paramBitArray) throws IOException, InvalidKeyException {
    DerOutputStream derOutputStream = new DerOutputStream();
    encode(derOutputStream, paramAlgorithmId, paramBitArray);
    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(derOutputStream.toByteArray());
    try {
      KeyFactory keyFactory = KeyFactory.getInstance(paramAlgorithmId.getName());
      return keyFactory.generatePublic(x509EncodedKeySpec);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
    
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new InvalidKeyException(invalidKeySpecException.getMessage(), invalidKeySpecException);
    } 
    String str = "";
    try {
      Provider provider = Security.getProvider("SUN");
      if (provider == null)
        throw new InstantiationException(); 
      str = provider.getProperty("PublicKey.X.509." + paramAlgorithmId.getName());
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
      if (object instanceof X509Key) {
        X509Key x509Key = (X509Key)object;
        x509Key.algid = paramAlgorithmId;
        x509Key.setKey(paramBitArray);
        x509Key.parseKeyBits();
        return x509Key;
      } 
    } catch (ClassNotFoundException classNotFoundException) {
    
    } catch (InstantiationException instantiationException) {
    
    } catch (IllegalAccessException illegalAccessException) {
      throw new IOException(str + " [internal error]");
    } 
    return new X509Key(paramAlgorithmId, paramBitArray);
  }
  
  public String getAlgorithm() { return this.algid.getName(); }
  
  public AlgorithmId getAlgorithmId() { return this.algid; }
  
  public final void encode(DerOutputStream paramDerOutputStream) throws IOException { encode(paramDerOutputStream, this.algid, getKey()); }
  
  public byte[] getEncoded() {
    try {
      return (byte[])getEncodedInternal().clone();
    } catch (InvalidKeyException invalidKeyException) {
      return null;
    } 
  }
  
  public byte[] getEncodedInternal() {
    byte[] arrayOfByte = this.encodedKey;
    if (arrayOfByte == null) {
      try {
        DerOutputStream derOutputStream = new DerOutputStream();
        encode(derOutputStream);
        arrayOfByte = derOutputStream.toByteArray();
      } catch (IOException iOException) {
        throw new InvalidKeyException("IOException : " + iOException.getMessage());
      } 
      this.encodedKey = arrayOfByte;
    } 
    return arrayOfByte;
  }
  
  public String getFormat() { return "X.509"; }
  
  public byte[] encode() { return (byte[])getEncodedInternal().clone(); }
  
  public String toString() {
    HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
    return "algorithm = " + this.algid.toString() + ", unparsed keybits = \n" + hexDumpEncoder.encodeBuffer(this.key);
  }
  
  public void decode(InputStream paramInputStream) throws InvalidKeyException {
    try {
      DerValue derValue = new DerValue(paramInputStream);
      if (derValue.tag != 48)
        throw new InvalidKeyException("invalid key format"); 
      this.algid = AlgorithmId.parse(derValue.data.getDerValue());
      setKey(derValue.data.getUnalignedBitString());
      parseKeyBits();
      if (derValue.data.available() != 0)
        throw new InvalidKeyException("excess key data"); 
    } catch (IOException iOException) {
      throw new InvalidKeyException("IOException: " + iOException.getMessage());
    } 
  }
  
  public void decode(byte[] paramArrayOfByte) throws InvalidKeyException { decode(new ByteArrayInputStream(paramArrayOfByte)); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { paramObjectOutputStream.write(getEncoded()); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException {
    try {
      decode(paramObjectInputStream);
    } catch (InvalidKeyException invalidKeyException) {
      invalidKeyException.printStackTrace();
      throw new IOException("deserialized key is invalid: " + invalidKeyException.getMessage());
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Key))
      return false; 
    try {
      byte[] arrayOfByte2;
      byte[] arrayOfByte1 = getEncodedInternal();
      if (paramObject instanceof X509Key) {
        arrayOfByte2 = ((X509Key)paramObject).getEncodedInternal();
      } else {
        arrayOfByte2 = ((Key)paramObject).getEncoded();
      } 
      return Arrays.equals(arrayOfByte1, arrayOfByte2);
    } catch (InvalidKeyException invalidKeyException) {
      return false;
    } 
  }
  
  public int hashCode() {
    try {
      byte[] arrayOfByte = getEncodedInternal();
      int i = arrayOfByte.length;
      for (byte b = 0; b < arrayOfByte.length; b++)
        i += (arrayOfByte[b] & 0xFF) * 37; 
      return i;
    } catch (InvalidKeyException invalidKeyException) {
      return 0;
    } 
  }
  
  static void encode(DerOutputStream paramDerOutputStream, AlgorithmId paramAlgorithmId, BitArray paramBitArray) throws IOException {
    DerOutputStream derOutputStream = new DerOutputStream();
    paramAlgorithmId.encode(derOutputStream);
    derOutputStream.putUnalignedBitString(paramBitArray);
    paramDerOutputStream.write((byte)48, derOutputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\X509Key.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */