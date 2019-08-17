package sun.security.x509;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import sun.misc.HexDumpEncoder;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class KeyIdentifier {
  private byte[] octetString;
  
  public KeyIdentifier(byte[] paramArrayOfByte) { this.octetString = (byte[])paramArrayOfByte.clone(); }
  
  public KeyIdentifier(DerValue paramDerValue) throws IOException { this.octetString = paramDerValue.getOctetString(); }
  
  public KeyIdentifier(PublicKey paramPublicKey) throws IOException {
    DerValue derValue = new DerValue(paramPublicKey.getEncoded());
    if (derValue.tag != 48)
      throw new IOException("PublicKey value is not a valid X.509 public key"); 
    AlgorithmId algorithmId = AlgorithmId.parse(derValue.data.getDerValue());
    byte[] arrayOfByte = derValue.data.getUnalignedBitString().toByteArray();
    MessageDigest messageDigest = null;
    try {
      messageDigest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new IOException("SHA1 not supported");
    } 
    messageDigest.update(arrayOfByte);
    this.octetString = messageDigest.digest();
  }
  
  public byte[] getIdentifier() { return (byte[])this.octetString.clone(); }
  
  public String toString() {
    null = "KeyIdentifier [\n";
    HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
    null = null + hexDumpEncoder.encodeBuffer(this.octetString);
    return null + "]\n";
  }
  
  void encode(DerOutputStream paramDerOutputStream) throws IOException { paramDerOutputStream.putOctetString(this.octetString); }
  
  public int hashCode() {
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.octetString.length; b2++)
      b1 += this.octetString[b2] * b2; 
    return b1;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof KeyIdentifier))
      return false; 
    byte[] arrayOfByte = ((KeyIdentifier)paramObject).octetString;
    return Arrays.equals(this.octetString, arrayOfByte);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\KeyIdentifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */