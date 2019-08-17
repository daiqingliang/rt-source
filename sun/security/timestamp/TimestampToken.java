package sun.security.timestamp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public class TimestampToken {
  private int version;
  
  private ObjectIdentifier policy;
  
  private BigInteger serialNumber;
  
  private AlgorithmId hashAlgorithm;
  
  private byte[] hashedMessage;
  
  private Date genTime;
  
  private BigInteger nonce;
  
  public TimestampToken(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte == null)
      throw new IOException("No timestamp token info"); 
    parse(paramArrayOfByte);
  }
  
  public Date getDate() { return this.genTime; }
  
  public AlgorithmId getHashAlgorithm() { return this.hashAlgorithm; }
  
  public byte[] getHashedMessage() { return this.hashedMessage; }
  
  public BigInteger getNonce() { return this.nonce; }
  
  public String getPolicyID() { return this.policy.toString(); }
  
  public BigInteger getSerialNumber() { return this.serialNumber; }
  
  private void parse(byte[] paramArrayOfByte) throws IOException {
    DerValue derValue1 = new DerValue(paramArrayOfByte);
    if (derValue1.tag != 48)
      throw new IOException("Bad encoding for timestamp token info"); 
    this.version = derValue1.data.getInteger();
    this.policy = derValue1.data.getOID();
    DerValue derValue2 = derValue1.data.getDerValue();
    this.hashAlgorithm = AlgorithmId.parse(derValue2.data.getDerValue());
    this.hashedMessage = derValue2.data.getOctetString();
    this.serialNumber = derValue1.data.getBigInteger();
    this.genTime = derValue1.data.getGeneralizedTime();
    while (derValue1.data.available() > 0) {
      DerValue derValue = derValue1.data.getDerValue();
      if (derValue.tag == 2) {
        this.nonce = derValue.getBigInteger();
        break;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\timestamp\TimestampToken.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */