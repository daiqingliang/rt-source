package sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import sun.misc.HexDumpEncoder;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CertificateExtensions extends Object implements CertAttrSet<Extension> {
  public static final String IDENT = "x509.info.extensions";
  
  public static final String NAME = "extensions";
  
  private static final Debug debug = Debug.getInstance("x509");
  
  private Map<String, Extension> map = Collections.synchronizedMap(new TreeMap());
  
  private boolean unsupportedCritExt = false;
  
  private Map<String, Extension> unparseableExtensions;
  
  private static Class[] PARAMS = { Boolean.class, Object.class };
  
  public CertificateExtensions() {}
  
  public CertificateExtensions(DerInputStream paramDerInputStream) throws IOException { init(paramDerInputStream); }
  
  private void init(DerInputStream paramDerInputStream) throws IOException {
    DerValue[] arrayOfDerValue = paramDerInputStream.getSequence(5);
    for (byte b = 0; b < arrayOfDerValue.length; b++) {
      Extension extension = new Extension(arrayOfDerValue[b]);
      parseExtension(extension);
    } 
  }
  
  private void parseExtension(Extension paramExtension) throws IOException {
    try {
      Class clazz = OIDMap.getClass(paramExtension.getExtensionId());
      if (clazz == null) {
        if (paramExtension.isCritical())
          this.unsupportedCritExt = true; 
        if (this.map.put(paramExtension.getExtensionId().toString(), paramExtension) == null)
          return; 
        throw new IOException("Duplicate extensions not allowed");
      } 
      Constructor constructor = clazz.getConstructor(PARAMS);
      Object[] arrayOfObject = { Boolean.valueOf(paramExtension.isCritical()), paramExtension.getExtensionValue() };
      CertAttrSet certAttrSet = (CertAttrSet)constructor.newInstance(arrayOfObject);
      if (this.map.put(certAttrSet.getName(), (Extension)certAttrSet) != null)
        throw new IOException("Duplicate extensions not allowed"); 
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getTargetException();
      if (!paramExtension.isCritical()) {
        if (this.unparseableExtensions == null)
          this.unparseableExtensions = new TreeMap(); 
        this.unparseableExtensions.put(paramExtension.getExtensionId().toString(), new UnparseableExtension(paramExtension, throwable));
        if (debug != null) {
          debug.println("Error parsing extension: " + paramExtension);
          throwable.printStackTrace();
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          System.err.println(hexDumpEncoder.encodeBuffer(paramExtension.getExtensionValue()));
        } 
        return;
      } 
      if (throwable instanceof IOException)
        throw (IOException)throwable; 
      throw new IOException(throwable);
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw new IOException(exception);
    } 
  }
  
  public void encode(OutputStream paramOutputStream) throws CertificateException, IOException { encode(paramOutputStream, false); }
  
  public void encode(OutputStream paramOutputStream, boolean paramBoolean) throws CertificateException, IOException {
    DerOutputStream derOutputStream3;
    DerOutputStream derOutputStream1 = new DerOutputStream();
    Collection collection = this.map.values();
    Object[] arrayOfObject = collection.toArray();
    for (byte b = 0; b < arrayOfObject.length; b++) {
      if (arrayOfObject[b] instanceof CertAttrSet) {
        ((CertAttrSet)arrayOfObject[b]).encode(derOutputStream1);
      } else if (arrayOfObject[b] instanceof Extension) {
        ((Extension)arrayOfObject[b]).encode(derOutputStream1);
      } else {
        throw new CertificateException("Illegal extension object");
      } 
    } 
    DerOutputStream derOutputStream2 = new DerOutputStream();
    derOutputStream2.write((byte)48, derOutputStream1);
    if (!paramBoolean) {
      derOutputStream3 = new DerOutputStream();
      derOutputStream3.write(DerValue.createTag(-128, true, (byte)3), derOutputStream2);
    } else {
      derOutputStream3 = derOutputStream2;
    } 
    paramOutputStream.write(derOutputStream3.toByteArray());
  }
  
  public void set(String paramString, Object paramObject) throws IOException {
    if (paramObject instanceof Extension) {
      this.map.put(paramString, (Extension)paramObject);
    } else {
      throw new IOException("Unknown extension type.");
    } 
  }
  
  public Extension get(String paramString) throws IOException {
    Extension extension = (Extension)this.map.get(paramString);
    if (extension == null)
      throw new IOException("No extension found with name " + paramString); 
    return extension;
  }
  
  Extension getExtension(String paramString) throws IOException { return (Extension)this.map.get(paramString); }
  
  public void delete(String paramString) throws IOException {
    Object object = this.map.get(paramString);
    if (object == null)
      throw new IOException("No extension found with name " + paramString); 
    this.map.remove(paramString);
  }
  
  public String getNameByOid(ObjectIdentifier paramObjectIdentifier) throws IOException {
    for (String str : this.map.keySet()) {
      if (((Extension)this.map.get(str)).getExtensionId().equals(paramObjectIdentifier))
        return str; 
    } 
    return null;
  }
  
  public Enumeration<Extension> getElements() { return Collections.enumeration(this.map.values()); }
  
  public Collection<Extension> getAllExtensions() { return this.map.values(); }
  
  public Map<String, Extension> getUnparseableExtensions() { return (this.unparseableExtensions == null) ? Collections.emptyMap() : this.unparseableExtensions; }
  
  public String getName() { return "extensions"; }
  
  public boolean hasUnsupportedCriticalExtension() { return this.unsupportedCritExt; }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CertificateExtensions))
      return false; 
    Collection collection = ((CertificateExtensions)paramObject).getAllExtensions();
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
    return getUnparseableExtensions().equals(((CertificateExtensions)paramObject).getUnparseableExtensions());
  }
  
  public int hashCode() { return this.map.hashCode() + getUnparseableExtensions().hashCode(); }
  
  public String toString() { return this.map.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\x509\CertificateExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */