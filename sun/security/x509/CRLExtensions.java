package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class CRLExtensions {
  private Map<String, Extension> map = Collections.synchronizedMap(new TreeMap());
  
  private boolean unsupportedCritExt = false;
  
  private static final Class[] PARAMS = { Boolean.class, Object.class };
  
  public CRLExtensions() {}
  
  public CRLExtensions(DerInputStream paramDerInputStream) throws CRLException { init(paramDerInputStream); }
  
  private void init(DerInputStream paramDerInputStream) throws CRLException {
    try {
      DerInputStream derInputStream = paramDerInputStream;
      byte b = (byte)paramDerInputStream.peekByte();
      if ((b & 0xC0) == 128 && (b & 0x1F) == 0) {
        DerValue derValue = derInputStream.getDerValue();
        derInputStream = derValue.data;
      } 
      DerValue[] arrayOfDerValue = derInputStream.getSequence(5);
      for (byte b1 = 0; b1 < arrayOfDerValue.length; b1++) {
        Extension extension = new Extension(arrayOfDerValue[b1]);
        parseExtension(extension);
      } 
    } catch (IOException iOException) {
      throw new CRLException("Parsing error: " + iOException.toString());
    } 
  }
  
  private void parseExtension(Extension paramExtension) throws CRLException {
    try {
      Class clazz = OIDMap.getClass(paramExtension.getExtensionId());
      if (clazz == null) {
        if (paramExtension.isCritical())
          this.unsupportedCritExt = true; 
        if (this.map.put(paramExtension.getExtensionId().toString(), paramExtension) != null)
          throw new CRLException("Duplicate extensions not allowed"); 
        return;
      } 
      Constructor constructor = clazz.getConstructor(PARAMS);
      Object[] arrayOfObject = { Boolean.valueOf(paramExtension.isCritical()), paramExtension.getExtensionValue() };
      CertAttrSet certAttrSet = (CertAttrSet)constructor.newInstance(arrayOfObject);
      if (this.map.put(certAttrSet.getName(), (Extension)certAttrSet) != null)
        throw new CRLException("Duplicate extensions not allowed"); 
    } catch (InvocationTargetException invocationTargetException) {
      throw new CRLException(invocationTargetException.getTargetException().getMessage());
    } catch (Exception exception) {
      throw new CRLException(exception.toString());
    } 
  }
  
  public void encode(OutputStream paramOutputStream, boolean paramBoolean) throws CRLException {
    try {
      DerOutputStream derOutputStream1 = new DerOutputStream();
      Collection collection = this.map.values();
      Object[] arrayOfObject = collection.toArray();
      for (byte b = 0; b < arrayOfObject.length; b++) {
        if (arrayOfObject[b] instanceof CertAttrSet) {
          ((CertAttrSet)arrayOfObject[b]).encode(derOutputStream1);
        } else if (arrayOfObject[b] instanceof Extension) {
          ((Extension)arrayOfObject[b]).encode(derOutputStream1);
        } else {
          throw new CRLException("Illegal extension object");
        } 
      } 
      DerOutputStream derOutputStream2 = new DerOutputStream();
      derOutputStream2.write((byte)48, derOutputStream1);
      DerOutputStream derOutputStream3 = new DerOutputStream();
      if (paramBoolean) {
        derOutputStream3.write(DerValue.createTag(-128, true, (byte)0), derOutputStream2);
      } else {
        derOutputStream3 = derOutputStream2;
      } 
      paramOutputStream.write(derOutputStream3.toByteArray());
    } catch (IOException iOException) {
      throw new CRLException("Encoding error: " + iOException.toString());
    } catch (CertificateException certificateException) {
      throw new CRLException("Encoding error: " + certificateException.toString());
    } 
  }
  
  public Extension get(String paramString) {
    String str1;
    X509AttributeName x509AttributeName = new X509AttributeName(paramString);
    String str2 = x509AttributeName.getPrefix();
    if (str2.equalsIgnoreCase("x509")) {
      int i = paramString.lastIndexOf(".");
      str1 = paramString.substring(i + 1);
    } else {
      str1 = paramString;
    } 
    return (Extension)this.map.get(str1);
  }
  
  public void set(String paramString, Object paramObject) { this.map.put(paramString, (Extension)paramObject); }
  
  public void delete(String paramString) { this.map.remove(paramString); }
  
  public Enumeration<Extension> getElements() { return Collections.enumeration(this.map.values()); }
  
  public Collection<Extension> getAllExtensions() { return this.map.values(); }
  
  public boolean hasUnsupportedCriticalExtension() { return this.unsupportedCritExt; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CRLExtensions))
      return false; 
    Collection collection = ((CRLExtensions)paramObject).getAllExtensions();
    Object[] arrayOfObject = collection.toArray();
    int i = arrayOfObject.length;
    if (i != this.map.size())
      return false; 
    String str = null;
    for (byte b = 0; b < i; b++) {
      if (arrayOfObject[b] instanceof CertAttrSet)
        str = ((CertAttrSet)arrayOfObject[b]).getName(); 
      Extension extension1 = (Extension)arrayOfObject[b];
      if (str == null)
        str = extension1.getExtensionId().toString(); 
      Extension extension2 = (Extension)this.map.get(str);
      if (extension2 == null)
        return false; 
      if (!extension2.equals(extension1))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() { return this.map.hashCode(); }
  
  public String toString() { return this.map.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CRLExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */