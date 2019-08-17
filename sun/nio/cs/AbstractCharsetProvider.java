package sun.nio.cs;

import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import sun.misc.ASCIICaseInsensitiveComparator;

public class AbstractCharsetProvider extends CharsetProvider {
  private Map<String, String> classMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
  
  private Map<String, String> aliasMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
  
  private Map<String, String[]> aliasNameMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
  
  private Map<String, SoftReference<Charset>> cache = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
  
  private String packagePrefix = "sun.nio.cs";
  
  protected AbstractCharsetProvider() {}
  
  protected AbstractCharsetProvider(String paramString) {}
  
  private static <K, V> void put(Map<K, V> paramMap, K paramK, V paramV) {
    if (!paramMap.containsKey(paramK))
      paramMap.put(paramK, paramV); 
  }
  
  private static <K, V> void remove(Map<K, V> paramMap, K paramK) {
    Object object = paramMap.remove(paramK);
    assert object != null;
  }
  
  protected void charset(String paramString1, String paramString2, String[] paramArrayOfString) {
    synchronized (this) {
      put(this.classMap, paramString1, paramString2);
      for (byte b = 0; b < paramArrayOfString.length; b++)
        put(this.aliasMap, paramArrayOfString[b], paramString1); 
      put(this.aliasNameMap, paramString1, paramArrayOfString);
      this.cache.clear();
    } 
  }
  
  protected void deleteCharset(String paramString, String[] paramArrayOfString) {
    synchronized (this) {
      remove(this.classMap, paramString);
      for (byte b = 0; b < paramArrayOfString.length; b++)
        remove(this.aliasMap, paramArrayOfString[b]); 
      remove(this.aliasNameMap, paramString);
      this.cache.clear();
    } 
  }
  
  protected void init() {}
  
  private String canonicalize(String paramString) {
    String str = (String)this.aliasMap.get(paramString);
    return (str != null) ? str : paramString;
  }
  
  private Charset lookup(String paramString) {
    SoftReference softReference = (SoftReference)this.cache.get(paramString);
    if (softReference != null) {
      Charset charset = (Charset)softReference.get();
      if (charset != null)
        return charset; 
    } 
    String str = (String)this.classMap.get(paramString);
    if (str == null)
      return null; 
    try {
      Class clazz = Class.forName(this.packagePrefix + "." + str, true, getClass().getClassLoader());
      Charset charset = (Charset)clazz.newInstance();
      this.cache.put(paramString, new SoftReference(charset));
      return charset;
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } catch (IllegalAccessException illegalAccessException) {
      return null;
    } catch (InstantiationException instantiationException) {
      return null;
    } 
  }
  
  public final Charset charsetForName(String paramString) {
    synchronized (this) {
      init();
      return lookup(canonicalize(paramString));
    } 
  }
  
  public final Iterator<Charset> charsets() {
    final ArrayList ks;
    synchronized (this) {
      init();
      arrayList = new ArrayList(this.classMap.keySet());
    } 
    return new Iterator<Charset>() {
        Iterator<String> i = ks.iterator();
        
        public boolean hasNext() { return this.i.hasNext(); }
        
        public Charset next() {
          String str = (String)this.i.next();
          synchronized (AbstractCharsetProvider.this) {
            return AbstractCharsetProvider.this.lookup(str);
          } 
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      };
  }
  
  public final String[] aliases(String paramString) {
    synchronized (this) {
      init();
      return (String[])this.aliasNameMap.get(paramString);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\cs\AbstractCharsetProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */