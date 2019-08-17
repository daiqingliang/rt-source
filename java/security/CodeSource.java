package java.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.SocketPermission;
import java.net.URL;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import sun.misc.IOUtils;

public class CodeSource implements Serializable {
  private static final long serialVersionUID = 4977541819976013951L;
  
  private URL location;
  
  private CodeSigner[] signers = null;
  
  private Certificate[] certs = null;
  
  private SocketPermission sp;
  
  private CertificateFactory factory = null;
  
  public CodeSource(URL paramURL, Certificate[] paramArrayOfCertificate) {
    this.location = paramURL;
    if (paramArrayOfCertificate != null)
      this.certs = (Certificate[])paramArrayOfCertificate.clone(); 
  }
  
  public CodeSource(URL paramURL, CodeSigner[] paramArrayOfCodeSigner) {
    this.location = paramURL;
    if (paramArrayOfCodeSigner != null)
      this.signers = (CodeSigner[])paramArrayOfCodeSigner.clone(); 
  }
  
  public int hashCode() { return (this.location != null) ? this.location.hashCode() : 0; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof CodeSource))
      return false; 
    CodeSource codeSource = (CodeSource)paramObject;
    if (this.location == null) {
      if (codeSource.location != null)
        return false; 
    } else if (!this.location.equals(codeSource.location)) {
      return false;
    } 
    return matchCerts(codeSource, true);
  }
  
  public final URL getLocation() { return this.location; }
  
  public final Certificate[] getCertificates() {
    if (this.certs != null)
      return (Certificate[])this.certs.clone(); 
    if (this.signers != null) {
      ArrayList arrayList = new ArrayList();
      for (byte b = 0; b < this.signers.length; b++)
        arrayList.addAll(this.signers[b].getSignerCertPath().getCertificates()); 
      this.certs = (Certificate[])arrayList.toArray(new Certificate[arrayList.size()]);
      return (Certificate[])this.certs.clone();
    } 
    return null;
  }
  
  public final CodeSigner[] getCodeSigners() {
    if (this.signers != null)
      return (CodeSigner[])this.signers.clone(); 
    if (this.certs != null) {
      this.signers = convertCertArrayToSignerArray(this.certs);
      return (CodeSigner[])this.signers.clone();
    } 
    return null;
  }
  
  public boolean implies(CodeSource paramCodeSource) { return (paramCodeSource == null) ? false : ((matchCerts(paramCodeSource, false) && matchLocation(paramCodeSource))); }
  
  private boolean matchCerts(CodeSource paramCodeSource, boolean paramBoolean) {
    if (this.certs == null && this.signers == null)
      return paramBoolean ? ((paramCodeSource.certs == null && paramCodeSource.signers == null)) : true; 
    if (this.signers != null && paramCodeSource.signers != null) {
      if (paramBoolean && this.signers.length != paramCodeSource.signers.length)
        return false; 
      for (byte b = 0; b < this.signers.length; b++) {
        boolean bool = false;
        for (byte b1 = 0; b1 < paramCodeSource.signers.length; b1++) {
          if (this.signers[b].equals(paramCodeSource.signers[b1])) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          return false; 
      } 
      return true;
    } 
    if (this.certs != null && paramCodeSource.certs != null) {
      if (paramBoolean && this.certs.length != paramCodeSource.certs.length)
        return false; 
      for (byte b = 0; b < this.certs.length; b++) {
        boolean bool = false;
        for (byte b1 = 0; b1 < paramCodeSource.certs.length; b1++) {
          if (this.certs[b].equals(paramCodeSource.certs[b1])) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  private boolean matchLocation(CodeSource paramCodeSource) {
    if (this.location == null)
      return true; 
    if (paramCodeSource == null || paramCodeSource.location == null)
      return false; 
    if (this.location.equals(paramCodeSource.location))
      return true; 
    if (!this.location.getProtocol().equalsIgnoreCase(paramCodeSource.location.getProtocol()))
      return false; 
    int i = this.location.getPort();
    if (i != -1) {
      int j = paramCodeSource.location.getPort();
      int k = (j != -1) ? j : paramCodeSource.location.getDefaultPort();
      if (i != k)
        return false; 
    } 
    if (this.location.getFile().endsWith("/-")) {
      String str = this.location.getFile().substring(0, this.location.getFile().length() - 1);
      if (!paramCodeSource.location.getFile().startsWith(str))
        return false; 
    } else if (this.location.getFile().endsWith("/*")) {
      int j = paramCodeSource.location.getFile().lastIndexOf('/');
      if (j == -1)
        return false; 
      String str3 = this.location.getFile().substring(0, this.location.getFile().length() - 1);
      String str4 = paramCodeSource.location.getFile().substring(0, j + 1);
      if (!str4.equals(str3))
        return false; 
    } else if (!paramCodeSource.location.getFile().equals(this.location.getFile()) && !paramCodeSource.location.getFile().equals(this.location.getFile() + "/")) {
      return false;
    } 
    if (this.location.getRef() != null && !this.location.getRef().equals(paramCodeSource.location.getRef()))
      return false; 
    String str1 = this.location.getHost();
    String str2 = paramCodeSource.location.getHost();
    if (str1 != null && ((!"".equals(str1) && !"localhost".equals(str1)) || (!"".equals(str2) && !"localhost".equals(str2))) && !str1.equals(str2)) {
      if (str2 == null)
        return false; 
      if (this.sp == null)
        this.sp = new SocketPermission(str1, "resolve"); 
      if (paramCodeSource.sp == null)
        paramCodeSource.sp = new SocketPermission(str2, "resolve"); 
      if (!this.sp.implies(paramCodeSource.sp))
        return false; 
    } 
    return true;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("(");
    stringBuilder.append(this.location);
    if (this.certs != null && this.certs.length > 0) {
      for (byte b = 0; b < this.certs.length; b++)
        stringBuilder.append(" " + this.certs[b]); 
    } else if (this.signers != null && this.signers.length > 0) {
      for (byte b = 0; b < this.signers.length; b++)
        stringBuilder.append(" " + this.signers[b]); 
    } else {
      stringBuilder.append(" <no signer certificates>");
    } 
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (this.certs == null || this.certs.length == 0) {
      paramObjectOutputStream.writeInt(0);
    } else {
      paramObjectOutputStream.writeInt(this.certs.length);
      for (byte b = 0; b < this.certs.length; b++) {
        Certificate certificate = this.certs[b];
        try {
          paramObjectOutputStream.writeUTF(certificate.getType());
          byte[] arrayOfByte = certificate.getEncoded();
          paramObjectOutputStream.writeInt(arrayOfByte.length);
          paramObjectOutputStream.write(arrayOfByte);
        } catch (CertificateEncodingException certificateEncodingException) {
          throw new IOException(certificateEncodingException.getMessage());
        } 
      } 
    } 
    if (this.signers != null && this.signers.length > 0)
      paramObjectOutputStream.writeObject(this.signers); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    Hashtable hashtable = null;
    ArrayList arrayList = null;
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i > 0) {
      hashtable = new Hashtable(3);
      arrayList = new ArrayList((i > 20) ? 20 : i);
    } else if (i < 0) {
      throw new IOException("size cannot be negative");
    } 
    for (b = 0; b < i; b++) {
      CertificateFactory certificateFactory;
      String str = paramObjectInputStream.readUTF();
      if (hashtable.containsKey(str)) {
        certificateFactory = (CertificateFactory)hashtable.get(str);
      } else {
        try {
          certificateFactory = CertificateFactory.getInstance(str);
        } catch (CertificateException certificateException) {
          throw new ClassNotFoundException("Certificate factory for " + str + " not found");
        } 
        hashtable.put(str, certificateFactory);
      } 
      byte[] arrayOfByte = IOUtils.readNBytes(paramObjectInputStream, paramObjectInputStream.readInt());
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrayOfByte);
      try {
        arrayList.add(certificateFactory.generateCertificate(byteArrayInputStream));
      } catch (CertificateException certificateException) {
        throw new IOException(certificateException.getMessage());
      } 
      byteArrayInputStream.close();
    } 
    if (arrayList != null)
      this.certs = (Certificate[])arrayList.toArray(new Certificate[i]); 
    try {
      this.signers = (CodeSigner[])((CodeSigner[])paramObjectInputStream.readObject()).clone();
    } catch (IOException b) {
      IOException iOException;
    } 
  }
  
  private CodeSigner[] convertCertArrayToSignerArray(Certificate[] paramArrayOfCertificate) {
    if (paramArrayOfCertificate == null)
      return null; 
    try {
      if (this.factory == null)
        this.factory = CertificateFactory.getInstance("X.509"); 
      byte b = 0;
      ArrayList arrayList = new ArrayList();
      while (b < paramArrayOfCertificate.length) {
        ArrayList arrayList1 = new ArrayList();
        arrayList1.add(paramArrayOfCertificate[b++]);
        byte b1;
        for (b1 = b; b1 < paramArrayOfCertificate.length && paramArrayOfCertificate[b1] instanceof X509Certificate && ((X509Certificate)paramArrayOfCertificate[b1]).getBasicConstraints() != -1; b1++)
          arrayList1.add(paramArrayOfCertificate[b1]); 
        b = b1;
        CertPath certPath = this.factory.generateCertPath(arrayList1);
        arrayList.add(new CodeSigner(certPath, null));
      } 
      return arrayList.isEmpty() ? null : (CodeSigner[])arrayList.toArray(new CodeSigner[arrayList.size()]);
    } catch (CertificateException certificateException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\CodeSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */