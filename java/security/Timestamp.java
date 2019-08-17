package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.cert.CertPath;
import java.util.Date;
import java.util.List;

public final class Timestamp implements Serializable {
  private static final long serialVersionUID = -5502683707821851294L;
  
  private Date timestamp;
  
  private CertPath signerCertPath;
  
  private int myhash = -1;
  
  public Timestamp(Date paramDate, CertPath paramCertPath) {
    if (paramDate == null || paramCertPath == null)
      throw new NullPointerException(); 
    this.timestamp = new Date(paramDate.getTime());
    this.signerCertPath = paramCertPath;
  }
  
  public Date getTimestamp() { return new Date(this.timestamp.getTime()); }
  
  public CertPath getSignerCertPath() { return this.signerCertPath; }
  
  public int hashCode() {
    if (this.myhash == -1)
      this.myhash = this.timestamp.hashCode() + this.signerCertPath.hashCode(); 
    return this.myhash;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof Timestamp))
      return false; 
    Timestamp timestamp1 = (Timestamp)paramObject;
    return (this == timestamp1) ? true : ((this.timestamp.equals(timestamp1.getTimestamp()) && this.signerCertPath.equals(timestamp1.getSignerCertPath())));
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("(");
    stringBuffer.append("timestamp: " + this.timestamp);
    List list = this.signerCertPath.getCertificates();
    if (!list.isEmpty()) {
      stringBuffer.append("TSA: " + list.get(0));
    } else {
      stringBuffer.append("TSA: <empty>");
    } 
    stringBuffer.append(")");
    return stringBuffer.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.myhash = -1;
    this.timestamp = new Date(this.timestamp.getTime());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\Timestamp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */