package java.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Hashtable;
import sun.misc.IOUtils;
import sun.security.util.Debug;

public final class UnresolvedPermission extends Permission implements Serializable {
  private static final long serialVersionUID = -4821973115467008846L;
  
  private static final Debug debug = Debug.getInstance("policy,access", "UnresolvedPermission");
  
  private String type;
  
  private String name;
  
  private String actions;
  
  private Certificate[] certs;
  
  private static final Class[] PARAMS0 = new Class[0];
  
  private static final Class[] PARAMS1 = { String.class };
  
  private static final Class[] PARAMS2 = { String.class, String.class };
  
  public UnresolvedPermission(String paramString1, String paramString2, String paramString3, Certificate[] paramArrayOfCertificate) {
    super(paramString1);
    if (paramString1 == null)
      throw new NullPointerException("type can't be null"); 
    this.type = paramString1;
    this.name = paramString2;
    this.actions = paramString3;
    if (paramArrayOfCertificate != null) {
      byte b;
      for (b = 0; b < paramArrayOfCertificate.length; b++) {
        if (!(paramArrayOfCertificate[b] instanceof X509Certificate)) {
          this.certs = (Certificate[])paramArrayOfCertificate.clone();
          break;
        } 
      } 
      if (this.certs == null) {
        b = 0;
        byte b1 = 0;
        while (b < paramArrayOfCertificate.length) {
          b1++;
          while (b + 1 < paramArrayOfCertificate.length && ((X509Certificate)paramArrayOfCertificate[b]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[b + 1]).getSubjectDN()))
            b++; 
          b++;
        } 
        if (b1 == paramArrayOfCertificate.length)
          this.certs = (Certificate[])paramArrayOfCertificate.clone(); 
        if (this.certs == null) {
          ArrayList arrayList = new ArrayList();
          for (b = 0; b < paramArrayOfCertificate.length; b++) {
            arrayList.add(paramArrayOfCertificate[b]);
            while (b + 1 < paramArrayOfCertificate.length && ((X509Certificate)paramArrayOfCertificate[b]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[b + 1]).getSubjectDN()))
              b++; 
          } 
          this.certs = new Certificate[arrayList.size()];
          arrayList.toArray(this.certs);
        } 
      } 
    } 
  }
  
  Permission resolve(Permission paramPermission, Certificate[] paramArrayOfCertificate) {
    if (this.certs != null) {
      if (paramArrayOfCertificate == null)
        return null; 
      for (byte b = 0; b < this.certs.length; b++) {
        boolean bool = false;
        for (byte b1 = 0; b1 < paramArrayOfCertificate.length; b1++) {
          if (this.certs[b].equals(paramArrayOfCertificate[b1])) {
            bool = true;
            break;
          } 
        } 
        if (!bool)
          return null; 
      } 
    } 
    try {
      Class clazz = paramPermission.getClass();
      if (this.name == null && this.actions == null)
        try {
          Constructor constructor1 = clazz.getConstructor(PARAMS0);
          return (Permission)constructor1.newInstance(new Object[0]);
        } catch (NoSuchMethodException noSuchMethodException) {
          try {
            Constructor constructor1 = clazz.getConstructor(PARAMS1);
            return (Permission)constructor1.newInstance(new Object[] { this.name });
          } catch (NoSuchMethodException noSuchMethodException1) {
            Constructor constructor1 = clazz.getConstructor(PARAMS2);
            return (Permission)constructor1.newInstance(new Object[] { this.name, this.actions });
          } 
        }  
      if (this.name != null && this.actions == null)
        try {
          Constructor constructor1 = clazz.getConstructor(PARAMS1);
          return (Permission)constructor1.newInstance(new Object[] { this.name });
        } catch (NoSuchMethodException noSuchMethodException) {
          Constructor constructor1 = clazz.getConstructor(PARAMS2);
          return (Permission)constructor1.newInstance(new Object[] { this.name, this.actions });
        }  
      Constructor constructor = clazz.getConstructor(PARAMS2);
      return (Permission)constructor.newInstance(new Object[] { this.name, this.actions });
    } catch (NoSuchMethodException noSuchMethodException) {
      if (debug != null) {
        debug.println("NoSuchMethodException:\n  could not find proper constructor for " + this.type);
        noSuchMethodException.printStackTrace();
      } 
      return null;
    } catch (Exception exception) {
      if (debug != null) {
        debug.println("unable to instantiate " + this.name);
        exception.printStackTrace();
      } 
      return null;
    } 
  }
  
  public boolean implies(Permission paramPermission) { return false; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof UnresolvedPermission))
      return false; 
    UnresolvedPermission unresolvedPermission = (UnresolvedPermission)paramObject;
    if (!this.type.equals(unresolvedPermission.type))
      return false; 
    if (this.name == null) {
      if (unresolvedPermission.name != null)
        return false; 
    } else if (!this.name.equals(unresolvedPermission.name)) {
      return false;
    } 
    if (this.actions == null) {
      if (unresolvedPermission.actions != null)
        return false; 
    } else if (!this.actions.equals(unresolvedPermission.actions)) {
      return false;
    } 
    if ((this.certs == null && unresolvedPermission.certs != null) || (this.certs != null && unresolvedPermission.certs == null) || (this.certs != null && unresolvedPermission.certs != null && this.certs.length != unresolvedPermission.certs.length))
      return false; 
    byte b;
    for (b = 0; this.certs != null && b < this.certs.length; b++) {
      boolean bool = false;
      for (byte b1 = 0; b1 < unresolvedPermission.certs.length; b1++) {
        if (this.certs[b].equals(unresolvedPermission.certs[b1])) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return false; 
    } 
    for (b = 0; unresolvedPermission.certs != null && b < unresolvedPermission.certs.length; b++) {
      boolean bool = false;
      for (byte b1 = 0; b1 < this.certs.length; b1++) {
        if (unresolvedPermission.certs[b].equals(this.certs[b1])) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = this.type.hashCode();
    if (this.name != null)
      i ^= this.name.hashCode(); 
    if (this.actions != null)
      i ^= this.actions.hashCode(); 
    return i;
  }
  
  public String getActions() { return ""; }
  
  public String getUnresolvedType() { return this.type; }
  
  public String getUnresolvedName() { return this.name; }
  
  public String getUnresolvedActions() { return this.actions; }
  
  public Certificate[] getUnresolvedCerts() { return (this.certs == null) ? null : (Certificate[])this.certs.clone(); }
  
  public String toString() { return "(unresolved " + this.type + " " + this.name + " " + this.actions + ")"; }
  
  public PermissionCollection newPermissionCollection() { return new UnresolvedPermissionCollection(); }
  
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
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    Hashtable hashtable = null;
    ArrayList arrayList = null;
    paramObjectInputStream.defaultReadObject();
    if (this.type == null)
      throw new NullPointerException("type can't be null"); 
    int i = paramObjectInputStream.readInt();
    if (i > 0) {
      hashtable = new Hashtable(3);
      arrayList = new ArrayList((i > 20) ? 20 : i);
    } else if (i < 0) {
      throw new IOException("size cannot be negative");
    } 
    for (byte b = 0; b < i; b++) {
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
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\security\UnresolvedPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */