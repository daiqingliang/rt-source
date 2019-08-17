package java.util.jar;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import sun.misc.ASCIICaseInsensitiveComparator;
import sun.util.logging.PlatformLogger;

public class Attributes extends Object implements Map<Object, Object>, Cloneable {
  protected Map<Object, Object> map;
  
  public Attributes() { this(11); }
  
  public Attributes(int paramInt) { this.map = new HashMap(paramInt); }
  
  public Attributes(Attributes paramAttributes) { this.map = new HashMap(paramAttributes); }
  
  public Object get(Object paramObject) { return this.map.get(paramObject); }
  
  public String getValue(String paramString) { return (String)get(new Name(paramString)); }
  
  public String getValue(Name paramName) { return (String)get(paramName); }
  
  public Object put(Object paramObject1, Object paramObject2) { return this.map.put((Name)paramObject1, (String)paramObject2); }
  
  public String putValue(String paramString1, String paramString2) { return (String)put(new Name(paramString1), paramString2); }
  
  public Object remove(Object paramObject) { return this.map.remove(paramObject); }
  
  public boolean containsValue(Object paramObject) { return this.map.containsValue(paramObject); }
  
  public boolean containsKey(Object paramObject) { return this.map.containsKey(paramObject); }
  
  public void putAll(Map<?, ?> paramMap) {
    if (!Attributes.class.isInstance(paramMap))
      throw new ClassCastException(); 
    for (Map.Entry entry : paramMap.entrySet())
      put(entry.getKey(), entry.getValue()); 
  }
  
  public void clear() { this.map.clear(); }
  
  public int size() { return this.map.size(); }
  
  public boolean isEmpty() { return this.map.isEmpty(); }
  
  public Set<Object> keySet() { return this.map.keySet(); }
  
  public Collection<Object> values() { return this.map.values(); }
  
  public Set<Map.Entry<Object, Object>> entrySet() { return this.map.entrySet(); }
  
  public boolean equals(Object paramObject) { return this.map.equals(paramObject); }
  
  public int hashCode() { return this.map.hashCode(); }
  
  public Object clone() { return new Attributes(this); }
  
  void write(DataOutputStream paramDataOutputStream) throws IOException {
    for (Map.Entry entry : entrySet()) {
      StringBuffer stringBuffer = new StringBuffer(((Name)entry.getKey()).toString());
      stringBuffer.append(": ");
      String str = (String)entry.getValue();
      if (str != null) {
        byte[] arrayOfByte = str.getBytes("UTF8");
        str = new String(arrayOfByte, 0, 0, arrayOfByte.length);
      } 
      stringBuffer.append(str);
      stringBuffer.append("\r\n");
      Manifest.make72Safe(stringBuffer);
      paramDataOutputStream.writeBytes(stringBuffer.toString());
    } 
    paramDataOutputStream.writeBytes("\r\n");
  }
  
  void writeMain(DataOutputStream paramDataOutputStream) throws IOException {
    String str1 = Name.MANIFEST_VERSION.toString();
    String str2 = getValue(str1);
    if (str2 == null) {
      str1 = Name.SIGNATURE_VERSION.toString();
      str2 = getValue(str1);
    } 
    if (str2 != null)
      paramDataOutputStream.writeBytes(str1 + ": " + str2 + "\r\n"); 
    for (Map.Entry entry : entrySet()) {
      String str = ((Name)entry.getKey()).toString();
      if (str2 != null && !str.equalsIgnoreCase(str1)) {
        StringBuffer stringBuffer = new StringBuffer(str);
        stringBuffer.append(": ");
        String str3 = (String)entry.getValue();
        if (str3 != null) {
          byte[] arrayOfByte = str3.getBytes("UTF8");
          str3 = new String(arrayOfByte, 0, 0, arrayOfByte.length);
        } 
        stringBuffer.append(str3);
        stringBuffer.append("\r\n");
        Manifest.make72Safe(stringBuffer);
        paramDataOutputStream.writeBytes(stringBuffer.toString());
      } 
    } 
    paramDataOutputStream.writeBytes("\r\n");
  }
  
  void read(Manifest.FastInputStream paramFastInputStream, byte[] paramArrayOfByte) throws IOException {
    String str1 = null;
    String str2 = null;
    byte[] arrayOfByte = null;
    int i;
    while ((i = paramFastInputStream.readLine(paramArrayOfByte)) != -1) {
      boolean bool = false;
      if (paramArrayOfByte[--i] != 10)
        throw new IOException("line too long"); 
      if (i > 0 && paramArrayOfByte[i - 1] == 13)
        i--; 
      if (i == 0)
        break; 
      int j = 0;
      if (paramArrayOfByte[0] == 32) {
        if (str1 == null)
          throw new IOException("misplaced continuation line"); 
        bool = true;
        byte[] arrayOfByte1 = new byte[arrayOfByte.length + i - 1];
        System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, arrayOfByte.length);
        System.arraycopy(paramArrayOfByte, 1, arrayOfByte1, arrayOfByte.length, i - 1);
        if (paramFastInputStream.peek() == 32) {
          arrayOfByte = arrayOfByte1;
          continue;
        } 
        str2 = new String(arrayOfByte1, 0, arrayOfByte1.length, "UTF8");
        arrayOfByte = null;
      } else {
        while (paramArrayOfByte[j++] != 58) {
          if (j >= i)
            throw new IOException("invalid header field"); 
        } 
        if (paramArrayOfByte[j++] != 32)
          throw new IOException("invalid header field"); 
        str1 = new String(paramArrayOfByte, 0, 0, j - 2);
        if (paramFastInputStream.peek() == 32) {
          arrayOfByte = new byte[i - j];
          System.arraycopy(paramArrayOfByte, j, arrayOfByte, 0, i - j);
          continue;
        } 
        str2 = new String(paramArrayOfByte, j, i - j, "UTF8");
      } 
      try {
        if (putValue(str1, str2) != null && !bool)
          PlatformLogger.getLogger("java.util.jar").warning("Duplicate name in Manifest: " + str1 + ".\nEnsure that the manifest does not have duplicate entries, and\nthat blank lines separate individual sections in both your\nmanifest and in the META-INF/MANIFEST.MF entry in the jar file."); 
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new IOException("invalid header field name: " + str1);
      } 
    } 
  }
  
  public static class Name {
    private String name;
    
    private int hashCode = -1;
    
    public static final Name MANIFEST_VERSION = new Name("Manifest-Version");
    
    public static final Name SIGNATURE_VERSION = new Name("Signature-Version");
    
    public static final Name CONTENT_TYPE = new Name("Content-Type");
    
    public static final Name CLASS_PATH = new Name("Class-Path");
    
    public static final Name MAIN_CLASS = new Name("Main-Class");
    
    public static final Name SEALED = new Name("Sealed");
    
    public static final Name EXTENSION_LIST = new Name("Extension-List");
    
    public static final Name EXTENSION_NAME = new Name("Extension-Name");
    
    @Deprecated
    public static final Name EXTENSION_INSTALLATION = new Name("Extension-Installation");
    
    public static final Name IMPLEMENTATION_TITLE = new Name("Implementation-Title");
    
    public static final Name IMPLEMENTATION_VERSION = new Name("Implementation-Version");
    
    public static final Name IMPLEMENTATION_VENDOR = new Name("Implementation-Vendor");
    
    @Deprecated
    public static final Name IMPLEMENTATION_VENDOR_ID = new Name("Implementation-Vendor-Id");
    
    @Deprecated
    public static final Name IMPLEMENTATION_URL = new Name("Implementation-URL");
    
    public static final Name SPECIFICATION_TITLE = new Name("Specification-Title");
    
    public static final Name SPECIFICATION_VERSION = new Name("Specification-Version");
    
    public static final Name SPECIFICATION_VENDOR = new Name("Specification-Vendor");
    
    public Name(String param1String) {
      if (param1String == null)
        throw new NullPointerException("name"); 
      if (!isValid(param1String))
        throw new IllegalArgumentException(param1String); 
      this.name = param1String.intern();
    }
    
    private static boolean isValid(String param1String) {
      int i = param1String.length();
      if (i > 70 || i == 0)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (!isValid(param1String.charAt(b)))
          return false; 
      } 
      return true;
    }
    
    private static boolean isValid(char param1Char) { return (isAlpha(param1Char) || isDigit(param1Char) || param1Char == '_' || param1Char == '-'); }
    
    private static boolean isAlpha(char param1Char) { return ((param1Char >= 'a' && param1Char <= 'z') || (param1Char >= 'A' && param1Char <= 'Z')); }
    
    private static boolean isDigit(char param1Char) { return (param1Char >= '0' && param1Char <= '9'); }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof Name) {
        Comparator comparator = ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER;
        return (comparator.compare(this.name, ((Name)param1Object).name) == 0);
      } 
      return false;
    }
    
    public int hashCode() {
      if (this.hashCode == -1)
        this.hashCode = ASCIICaseInsensitiveComparator.lowerCaseHashCode(this.name); 
      return this.hashCode;
    }
    
    public String toString() { return this.name; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\jar\Attributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */