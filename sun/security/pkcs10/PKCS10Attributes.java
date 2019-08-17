package sun.security.pkcs10;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import sun.security.util.DerEncoder;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

public class PKCS10Attributes implements DerEncoder {
  private Hashtable<String, PKCS10Attribute> map = new Hashtable(3);
  
  public PKCS10Attributes() {}
  
  public PKCS10Attributes(PKCS10Attribute[] paramArrayOfPKCS10Attribute) {
    for (byte b = 0; b < paramArrayOfPKCS10Attribute.length; b++)
      this.map.put(paramArrayOfPKCS10Attribute[b].getAttributeId().toString(), paramArrayOfPKCS10Attribute[b]); 
  }
  
  public PKCS10Attributes(DerInputStream paramDerInputStream) throws IOException {
    DerValue[] arrayOfDerValue = paramDerInputStream.getSet(3, true);
    if (arrayOfDerValue == null)
      throw new IOException("Illegal encoding of attributes"); 
    for (byte b = 0; b < arrayOfDerValue.length; b++) {
      PKCS10Attribute pKCS10Attribute = new PKCS10Attribute(arrayOfDerValue[b]);
      this.map.put(pKCS10Attribute.getAttributeId().toString(), pKCS10Attribute);
    } 
  }
  
  public void encode(OutputStream paramOutputStream) throws IOException { derEncode(paramOutputStream); }
  
  public void derEncode(OutputStream paramOutputStream) throws IOException {
    Collection collection = this.map.values();
    PKCS10Attribute[] arrayOfPKCS10Attribute = (PKCS10Attribute[])collection.toArray(new PKCS10Attribute[this.map.size()]);
    DerOutputStream derOutputStream = new DerOutputStream();
    derOutputStream.putOrderedSetOf(DerValue.createTag(-128, true, (byte)0), arrayOfPKCS10Attribute);
    paramOutputStream.write(derOutputStream.toByteArray());
  }
  
  public void setAttribute(String paramString, Object paramObject) {
    if (paramObject instanceof PKCS10Attribute)
      this.map.put(paramString, (PKCS10Attribute)paramObject); 
  }
  
  public Object getAttribute(String paramString) { return this.map.get(paramString); }
  
  public void deleteAttribute(String paramString) { this.map.remove(paramString); }
  
  public Enumeration<PKCS10Attribute> getElements() { return this.map.elements(); }
  
  public Collection<PKCS10Attribute> getAttributes() { return Collections.unmodifiableCollection(this.map.values()); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof PKCS10Attributes))
      return false; 
    Collection collection = ((PKCS10Attributes)paramObject).getAttributes();
    PKCS10Attribute[] arrayOfPKCS10Attribute = (PKCS10Attribute[])collection.toArray(new PKCS10Attribute[collection.size()]);
    int i = arrayOfPKCS10Attribute.length;
    if (i != this.map.size())
      return false; 
    String str = null;
    for (byte b = 0; b < i; b++) {
      PKCS10Attribute pKCS10Attribute2 = arrayOfPKCS10Attribute[b];
      str = pKCS10Attribute2.getAttributeId().toString();
      if (str == null)
        return false; 
      PKCS10Attribute pKCS10Attribute1 = (PKCS10Attribute)this.map.get(str);
      if (pKCS10Attribute1 == null)
        return false; 
      if (!pKCS10Attribute1.equals(pKCS10Attribute2))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() { return this.map.hashCode(); }
  
  public String toString() { return this.map.size() + "\n" + this.map.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\pkcs10\PKCS10Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */