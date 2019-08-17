package java.security.cert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.x500.X500Principal;
import sun.misc.IOUtils;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.Extension;
import sun.security.x509.InvalidityDateExtension;

public class CertificateRevokedException extends CertificateException {
  private static final long serialVersionUID = 7839996631571608627L;
  
  private Date revocationDate;
  
  private final CRLReason reason;
  
  private final X500Principal authority;
  
  private Map<String, Extension> extensions;
  
  public CertificateRevokedException(Date paramDate, CRLReason paramCRLReason, X500Principal paramX500Principal, Map<String, Extension> paramMap) {
    if (paramDate == null || paramCRLReason == null || paramX500Principal == null || paramMap == null)
      throw new NullPointerException(); 
    this.revocationDate = new Date(paramDate.getTime());
    this.reason = paramCRLReason;
    this.authority = paramX500Principal;
    this.extensions = Collections.checkedMap(new HashMap(), String.class, Extension.class);
    this.extensions.putAll(paramMap);
  }
  
  public Date getRevocationDate() { return (Date)this.revocationDate.clone(); }
  
  public CRLReason getRevocationReason() { return this.reason; }
  
  public X500Principal getAuthorityName() { return this.authority; }
  
  public Date getInvalidityDate() {
    Extension extension = (Extension)getExtensions().get("2.5.29.24");
    if (extension == null)
      return null; 
    try {
      Date date = InvalidityDateExtension.toImpl(extension).get("DATE");
      return new Date(date.getTime());
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public Map<String, Extension> getExtensions() { return Collections.unmodifiableMap(this.extensions); }
  
  public String getMessage() { return "Certificate has been revoked, reason: " + this.reason + ", revocation date: " + this.revocationDate + ", authority: " + this.authority + ", extension OIDs: " + this.extensions.keySet(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.extensions.size());
    for (Map.Entry entry : this.extensions.entrySet()) {
      Extension extension = (Extension)entry.getValue();
      paramObjectOutputStream.writeObject(extension.getId());
      paramObjectOutputStream.writeBoolean(extension.isCritical());
      byte[] arrayOfByte = extension.getValue();
      paramObjectOutputStream.writeInt(arrayOfByte.length);
      paramObjectOutputStream.write(arrayOfByte);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.revocationDate = new Date(this.revocationDate.getTime());
    int i = paramObjectInputStream.readInt();
    if (i == 0) {
      this.extensions = Collections.emptyMap();
    } else {
      if (i < 0)
        throw new IOException("size cannot be negative"); 
      this.extensions = new HashMap((i > 20) ? 20 : i);
    } 
    for (byte b = 0; b < i; b++) {
      String str = (String)paramObjectInputStream.readObject();
      boolean bool = paramObjectInputStream.readBoolean();
      byte[] arrayOfByte = IOUtils.readNBytes(paramObjectInputStream, paramObjectInputStream.readInt());
      Extension extension = Extension.newExtension(new ObjectIdentifier(str), bool, arrayOfByte);
      this.extensions.put(str, extension);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\cert\CertificateRevokedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */