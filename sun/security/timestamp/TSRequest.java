package sun.security.timestamp;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Extension;
import sun.security.util.DerOutputStream;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

public class TSRequest {
  private int version = 1;
  
  private AlgorithmId hashAlgorithmId = null;
  
  private byte[] hashValue;
  
  private String policyId = null;
  
  private BigInteger nonce = null;
  
  private boolean returnCertificate = false;
  
  private X509Extension[] extensions = null;
  
  public TSRequest(String paramString, byte[] paramArrayOfByte, MessageDigest paramMessageDigest) throws NoSuchAlgorithmException {
    this.policyId = paramString;
    this.hashAlgorithmId = AlgorithmId.get(paramMessageDigest.getAlgorithm());
    this.hashValue = paramMessageDigest.digest(paramArrayOfByte);
  }
  
  public byte[] getHashedMessage() { return (byte[])this.hashValue.clone(); }
  
  public void setVersion(int paramInt) { this.version = paramInt; }
  
  public void setPolicyId(String paramString) { this.policyId = paramString; }
  
  public void setNonce(BigInteger paramBigInteger) { this.nonce = paramBigInteger; }
  
  public void requestCertificate(boolean paramBoolean) { this.returnCertificate = paramBoolean; }
  
  public void setExtensions(X509Extension[] paramArrayOfX509Extension) { this.extensions = paramArrayOfX509Extension; }
  
  public byte[] encode() {
    DerOutputStream derOutputStream1 = new DerOutputStream();
    derOutputStream1.putInteger(this.version);
    DerOutputStream derOutputStream2 = new DerOutputStream();
    this.hashAlgorithmId.encode(derOutputStream2);
    derOutputStream2.putOctetString(this.hashValue);
    derOutputStream1.write((byte)48, derOutputStream2);
    if (this.policyId != null)
      derOutputStream1.putOID(new ObjectIdentifier(this.policyId)); 
    if (this.nonce != null)
      derOutputStream1.putInteger(this.nonce); 
    if (this.returnCertificate)
      derOutputStream1.putBoolean(true); 
    DerOutputStream derOutputStream3 = new DerOutputStream();
    derOutputStream3.write((byte)48, derOutputStream1);
    return derOutputStream3.toByteArray();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\timestamp\TSRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */