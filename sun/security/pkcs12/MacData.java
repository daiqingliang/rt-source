package sun.security.pkcs12;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import sun.security.pkcs.ParsingException;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.x509.AlgorithmId;

class MacData {
  private String digestAlgorithmName;
  
  private AlgorithmParameters digestAlgorithmParams;
  
  private byte[] digest;
  
  private byte[] macSalt;
  
  private int iterations;
  
  private byte[] encoded = null;
  
  MacData(DerInputStream paramDerInputStream) throws IOException, ParsingException {
    DerValue[] arrayOfDerValue1 = paramDerInputStream.getSequence(2);
    DerInputStream derInputStream = new DerInputStream(arrayOfDerValue1[0].toByteArray());
    DerValue[] arrayOfDerValue2 = derInputStream.getSequence(2);
    AlgorithmId algorithmId = AlgorithmId.parse(arrayOfDerValue2[0]);
    this.digestAlgorithmName = algorithmId.getName();
    this.digestAlgorithmParams = algorithmId.getParameters();
    this.digest = arrayOfDerValue2[1].getOctetString();
    this.macSalt = arrayOfDerValue1[1].getOctetString();
    if (arrayOfDerValue1.length > 2) {
      this.iterations = arrayOfDerValue1[2].getInteger();
    } else {
      this.iterations = 1;
    } 
  }
  
  MacData(String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws NoSuchAlgorithmException {
    if (paramString == null)
      throw new NullPointerException("the algName parameter must be non-null"); 
    AlgorithmId algorithmId = AlgorithmId.get(paramString);
    this.digestAlgorithmName = algorithmId.getName();
    this.digestAlgorithmParams = algorithmId.getParameters();
    if (paramArrayOfByte1 == null)
      throw new NullPointerException("the digest parameter must be non-null"); 
    if (paramArrayOfByte1.length == 0)
      throw new IllegalArgumentException("the digest parameter must not be empty"); 
    this.digest = (byte[])paramArrayOfByte1.clone();
    this.macSalt = paramArrayOfByte2;
    this.iterations = paramInt;
    this.encoded = null;
  }
  
  MacData(AlgorithmParameters paramAlgorithmParameters, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) throws NoSuchAlgorithmException {
    if (paramAlgorithmParameters == null)
      throw new NullPointerException("the algParams parameter must be non-null"); 
    AlgorithmId algorithmId = AlgorithmId.get(paramAlgorithmParameters);
    this.digestAlgorithmName = algorithmId.getName();
    this.digestAlgorithmParams = algorithmId.getParameters();
    if (paramArrayOfByte1 == null)
      throw new NullPointerException("the digest parameter must be non-null"); 
    if (paramArrayOfByte1.length == 0)
      throw new IllegalArgumentException("the digest parameter must not be empty"); 
    this.digest = (byte[])paramArrayOfByte1.clone();
    this.macSalt = paramArrayOfByte2;
    this.iterations = paramInt;
    this.encoded = null;
  }
  
  String getDigestAlgName() { return this.digestAlgorithmName; }
  
  byte[] getSalt() { return this.macSalt; }
  
  int getIterations() { return this.iterations; }
  
  byte[] getDigest() { return this.digest; }
  
  public byte[] getEncoded() {
    if (this.encoded != null)
      return (byte[])this.encoded.clone(); 
    DerOutputStream derOutputStream1 = new DerOutputStream();
    DerOutputStream derOutputStream2 = new DerOutputStream();
    DerOutputStream derOutputStream3 = new DerOutputStream();
    AlgorithmId algorithmId = AlgorithmId.get(this.digestAlgorithmName);
    algorithmId.encode(derOutputStream3);
    derOutputStream3.putOctetString(this.digest);
    derOutputStream2.write((byte)48, derOutputStream3);
    derOutputStream2.putOctetString(this.macSalt);
    derOutputStream2.putInteger(this.iterations);
    derOutputStream1.write((byte)48, derOutputStream2);
    this.encoded = derOutputStream1.toByteArray();
    return (byte[])this.encoded.clone();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs12\MacData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */