package java.security.cert;

import java.io.ByteArrayInputStream;
import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public abstract class CertPath implements Serializable {
  private static final long serialVersionUID = 6068470306649138683L;
  
  private String type;
  
  protected CertPath(String paramString) { this.type = paramString; }
  
  public String getType() { return this.type; }
  
  public abstract Iterator<String> getEncodings();
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CertPath))
      return false; 
    CertPath certPath = (CertPath)paramObject;
    if (!certPath.getType().equals(this.type))
      return false; 
    List list1 = getCertificates();
    List list2 = certPath.getCertificates();
    return list1.equals(list2);
  }
  
  public int hashCode() {
    null = this.type.hashCode();
    return 31 * null + getCertificates().hashCode();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    Iterator iterator = getCertificates().iterator();
    stringBuffer.append("\n" + this.type + " Cert Path: length = " + getCertificates().size() + ".\n");
    stringBuffer.append("[\n");
    for (byte b = 1; iterator.hasNext(); b++) {
      stringBuffer.append("=========================================================Certificate " + b + " start.\n");
      Certificate certificate = (Certificate)iterator.next();
      stringBuffer.append(certificate.toString());
      stringBuffer.append("\n=========================================================Certificate " + b + " end.\n\n\n");
    } 
    stringBuffer.append("\n]");
    return stringBuffer.toString();
  }
  
  public abstract byte[] getEncoded() throws CertificateEncodingException;
  
  public abstract byte[] getEncoded(String paramString) throws CertificateEncodingException;
  
  public abstract List<? extends Certificate> getCertificates();
  
  protected Object writeReplace() throws ObjectStreamException {
    try {
      return new CertPathRep(this.type, getEncoded());
    } catch (CertificateException certificateException) {
      NotSerializableException notSerializableException = new NotSerializableException("java.security.cert.CertPath: " + this.type);
      notSerializableException.initCause(certificateException);
      throw notSerializableException;
    } 
  }
  
  protected static class CertPathRep implements Serializable {
    private static final long serialVersionUID = 3015633072427920915L;
    
    private String type;
    
    private byte[] data;
    
    protected CertPathRep(String param1String, byte[] param1ArrayOfByte) {
      this.type = param1String;
      this.data = param1ArrayOfByte;
    }
    
    protected Object readResolve() throws ObjectStreamException {
      try {
        CertificateFactory certificateFactory = CertificateFactory.getInstance(this.type);
        return certificateFactory.generateCertPath(new ByteArrayInputStream(this.data));
      } catch (CertificateException certificateException) {
        NotSerializableException notSerializableException = new NotSerializableException("java.security.cert.CertPath: " + this.type);
        notSerializableException.initCause(certificateException);
        throw notSerializableException;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */