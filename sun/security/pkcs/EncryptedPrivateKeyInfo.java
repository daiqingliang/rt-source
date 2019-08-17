package sun.security.pkcs;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

public class EncryptedPrivateKeyInfo {
  private AlgorithmId algid;
  
  private byte[] encryptedData;
  
  private byte[] encoded;
  
  public EncryptedPrivateKeyInfo(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("encoding must not be null"); 
    DerValue derValue = new DerValue(paramArrayOfByte);
    DerValue[] arrayOfDerValue = new DerValue[2];
    arrayOfDerValue[0] = derValue.data.getDerValue();
    arrayOfDerValue[1] = derValue.data.getDerValue();
    if (derValue.data.available() != 0)
      throw new IOException("overrun, bytes = " + derValue.data.available()); 
    this.algid = AlgorithmId.parse(arrayOfDerValue[0]);
    if ((arrayOfDerValue[0]).data.available() != 0)
      throw new IOException("encryptionAlgorithm field overrun"); 
    this.encryptedData = arrayOfDerValue[1].getOctetString();
    if ((arrayOfDerValue[1]).data.available() != 0)
      throw new IOException("encryptedData field overrun"); 
    this.encoded = (byte[])paramArrayOfByte.clone();
  }
  
  public EncryptedPrivateKeyInfo(AlgorithmId paramAlgorithmId, byte[] paramArrayOfByte) {
    this.algid = paramAlgorithmId;
    this.encryptedData = (byte[])paramArrayOfByte.clone();
  }
  
  public AlgorithmId getAlgorithm() { return this.algid; }
  
  public byte[] getEncryptedData() { return (byte[])this.encryptedData.clone(); }
  
  public byte[] getEncoded() {
    if (this.encoded != null)
      return (byte[])this.encoded.clone(); 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    this.algid.encode(derOutputStream2);
    derOutputStream2.putOctetString(this.encryptedData);
    derOutputStream1.write((byte)48, derOutputStream2);
    this.encoded = derOutputStream1.toByteArray();
    return (byte[])this.encoded.clone();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof EncryptedPrivateKeyInfo))
      return false; 
    try {
      byte[] arrayOfByte1 = getEncoded();
      byte[] arrayOfByte2 = ((EncryptedPrivateKeyInfo)paramObject).getEncoded();
      if (arrayOfByte1.length != arrayOfByte2.length)
        return false; 
      for (byte b = 0; b < arrayOfByte1.length; b++) {
        if (arrayOfByte1[b] != arrayOfByte2[b])
          return false; 
      } 
      return true;
    } catch (IOException iOException) {
      return false;
    } 
  }
  
  public int hashCode() {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.encryptedData.length; b2++)
      b1 += this.encryptedData[b2] * b2; 
    return b1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs\EncryptedPrivateKeyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */