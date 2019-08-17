package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.cert.CertPath;

public final class CodeSigner implements Serializable {
  private static final long serialVersionUID = 6819288105193937581L;
  
  private CertPath signerCertPath;
  
  private Timestamp timestamp;
  
  private int myhash = -1;
  
  public CodeSigner(CertPath paramCertPath, Timestamp paramTimestamp) {
    if (paramCertPath == null)
      throw new NullPointerException(); 
    this.signerCertPath = paramCertPath;
    this.timestamp = paramTimestamp;
  }
  
  public CertPath getSignerCertPath() { return this.signerCertPath; }
  
  public Timestamp getTimestamp() { return this.timestamp; }
  
  public int hashCode() {
    if (this.myhash == -1)
      if (this.timestamp == null) {
        this.myhash = this.signerCertPath.hashCode();
      } else {
        this.myhash = this.signerCertPath.hashCode() + this.timestamp.hashCode();
      }  
    return this.myhash;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof CodeSigner))
      return false; 
    CodeSigner codeSigner = (CodeSigner)paramObject;
    if (this == codeSigner)
      return true; 
    Timestamp timestamp1 = codeSigner.getTimestamp();
    if (this.timestamp == null) {
      if (timestamp1 != null)
        return false; 
    } else if (timestamp1 == null || !this.timestamp.equals(timestamp1)) {
      return false;
    } 
    return this.signerCertPath.equals(codeSigner.getSignerCertPath());
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(");
    stringBuffer.append("Signer: " + this.signerCertPath.getCertificates().get(0));
    if (this.timestamp != null)
      stringBuffer.append("timestamp: " + this.timestamp); 
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.myhash = -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\CodeSigner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */