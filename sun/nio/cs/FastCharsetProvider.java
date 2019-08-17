package sun.nio.cs;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Iterator;
import java.util.Map;

public class FastCharsetProvider extends CharsetProvider {
  private Map<String, String> classMap;
  
  private Map<String, String> aliasMap;
  
  private Map<String, Charset> cache;
  
  private String packagePrefix;
  
  protected FastCharsetProvider(String paramString, Map<String, String> paramMap1, Map<String, String> paramMap2, Map<String, Charset> paramMap3) {
    this.packagePrefix = paramString;
    this.aliasMap = paramMap1;
    this.classMap = paramMap2;
    this.cache = paramMap3;
  }
  
  private String canonicalize(String paramString) {
    String str = (String)this.aliasMap.get(paramString);
    return (str != null) ? str : paramString;
  }
  
  private static String toLower(String paramString) {
    int i = paramString.length();
    boolean bool = true;
    for (byte b1 = 0; b1 < i; b1++) {
      char c = paramString.charAt(b1);
      if ((c - 'A' | 'Z' - c) >= '\000') {
        bool = false;
        break;
      } 
    } 
    if (bool)
      return paramString; 
    char[] arrayOfChar = new char[i];
    for (byte b2 = 0; b2 < i; b2++) {
      char c = paramString.charAt(b2);
      if ((c - 'A' | 'Z' - c) >= '\000') {
        arrayOfChar[b2] = (char)(c + ' ');
      } else {
        arrayOfChar[b2] = (char)c;
      } 
    } 
    return new String(arrayOfChar);
  }
  
  private Charset lookup(String paramString) {
    String str1 = canonicalize(toLower(paramString));
    Charset charset = (Charset)this.cache.get(str1);
    if (charset != null)
      return charset; 
    String str2 = (String)this.classMap.get(str1);
    if (str2 == null)
      return null; 
    if (str2.equals("US_ASCII")) {
      charset = new US_ASCII();
      this.cache.put(str1, charset);
      return charset;
    } 
    try {
      Class clazz = Class.forName(this.packagePrefix + "." + str2, true, getClass().getClassLoader());
      charset = (Charset)clazz.newInstance();
      this.cache.put(str1, charset);
      return charset;
    } catch (ClassNotFoundException|IllegalAccessException|InstantiationException classNotFoundException) {
      return null;
    } 
  }
  
  public final Charset charsetForName(String paramString) {
    synchronized (this) {
      return lookup(canonicalize(paramString));
    } 
  }
  
  public final Iterator<Charset> charsets() { return new Iterator<Charset>() {
        Iterator<String> i = FastCharsetProvider.this.classMap.keySet().iterator();
        
        public boolean hasNext() { return this.i.hasNext(); }
        
        public Charset next() {
          String str = (String)this.i.next();
          return FastCharsetProvider.this.lookup(str);
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\FastCharsetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */