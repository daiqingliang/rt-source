package java.nio.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.spi.CharsetProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import sun.misc.ASCIICaseInsensitiveComparator;
import sun.misc.VM;
import sun.nio.cs.StandardCharsets;
import sun.nio.cs.ThreadLocalCoders;
import sun.security.action.GetPropertyAction;

public abstract class Charset extends Object implements Comparable<Charset> {
  private static CharsetProvider standardProvider = new StandardCharsets();
  
  private static ThreadLocal<ThreadLocal<?>> gate = new ThreadLocal();
  
  private final String name;
  
  private final String[] aliases;
  
  private Set<String> aliasSet = null;
  
  static boolean atBugLevel(String paramString) {
    String str = bugLevel;
    if (str == null) {
      if (!VM.isBooted())
        return false; 
      bugLevel = str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.cs.bugLevel", ""));
    } 
    return str.equals(paramString);
  }
  
  private static void checkName(String paramString) {
    int i = paramString.length();
    if (!atBugLevel("1.4") && i == 0)
      throw new IllegalCharsetNameException(paramString); 
    byte b = 0;
    while (b < i) {
      char c = paramString.charAt(b);
      if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || (c == '-' && b != 0) || (c == '+' && b != 0) || (c == ':' && b != 0) || (c == '_' && b != 0) || (c == '.' && b != 0)) {
        b++;
        continue;
      } 
      throw new IllegalCharsetNameException(paramString);
    } 
  }
  
  private static void cache(String paramString, Charset paramCharset) {
    cache2 = cache1;
    cache1 = new Object[] { paramString, paramCharset };
  }
  
  private static Iterator<CharsetProvider> providers() { return new Iterator<CharsetProvider>() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        
        ServiceLoader<CharsetProvider> sl = ServiceLoader.load(CharsetProvider.class, this.cl);
        
        Iterator<CharsetProvider> i = this.sl.iterator();
        
        CharsetProvider next = null;
        
        private boolean getNext() {
          while (this.next == null) {
            try {
              if (!this.i.hasNext())
                return false; 
              this.next = (CharsetProvider)this.i.next();
            } catch (ServiceConfigurationError serviceConfigurationError) {
              if (serviceConfigurationError.getCause() instanceof SecurityException)
                continue; 
              throw serviceConfigurationError;
            } 
          } 
          return true;
        }
        
        public boolean hasNext() { return getNext(); }
        
        public CharsetProvider next() {
          if (!getNext())
            throw new NoSuchElementException(); 
          CharsetProvider charsetProvider = this.next;
          this.next = null;
          return charsetProvider;
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      }; }
  
  private static Charset lookupViaProviders(final String charsetName) {
    if (!VM.isBooted())
      return null; 
    if (gate.get() != null)
      return null; 
    try {
      gate.set(gate);
      return (Charset)AccessController.doPrivileged(new PrivilegedAction<Charset>() {
            public Charset run() {
              Iterator iterator = Charset.providers();
              while (iterator.hasNext()) {
                CharsetProvider charsetProvider = (CharsetProvider)iterator.next();
                Charset charset = charsetProvider.charsetForName(charsetName);
                if (charset != null)
                  return charset; 
              } 
              return null;
            }
          });
    } finally {
      gate.set(null);
    } 
  }
  
  private static Charset lookupExtendedCharset(String paramString) {
    CharsetProvider charsetProvider = ExtendedProviderHolder.extendedProvider;
    return (charsetProvider != null) ? charsetProvider.charsetForName(paramString) : null;
  }
  
  private static Charset lookup(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Null charset name"); 
    Object[] arrayOfObject;
    return ((arrayOfObject = cache1) != null && paramString.equals(arrayOfObject[0])) ? (Charset)arrayOfObject[1] : lookup2(paramString);
  }
  
  private static Charset lookup2(String paramString) {
    Object[] arrayOfObject;
    if ((arrayOfObject = cache2) != null && paramString.equals(arrayOfObject[0])) {
      cache2 = cache1;
      cache1 = arrayOfObject;
      return (Charset)arrayOfObject[1];
    } 
    Charset charset;
    if ((charset = standardProvider.charsetForName(paramString)) != null || (charset = lookupExtendedCharset(paramString)) != null || (charset = lookupViaProviders(paramString)) != null) {
      cache(paramString, charset);
      return charset;
    } 
    checkName(paramString);
    return null;
  }
  
  public static boolean isSupported(String paramString) { return (lookup(paramString) != null); }
  
  public static Charset forName(String paramString) {
    Charset charset = lookup(paramString);
    if (charset != null)
      return charset; 
    throw new UnsupportedCharsetException(paramString);
  }
  
  private static void put(Iterator<Charset> paramIterator, Map<String, Charset> paramMap) {
    while (paramIterator.hasNext()) {
      Charset charset = (Charset)paramIterator.next();
      if (!paramMap.containsKey(charset.name()))
        paramMap.put(charset.name(), charset); 
    } 
  }
  
  public static SortedMap<String, Charset> availableCharsets() { return (SortedMap)AccessController.doPrivileged(new PrivilegedAction<SortedMap<String, Charset>>() {
          public SortedMap<String, Charset> run() {
            TreeMap treeMap = new TreeMap(ASCIICaseInsensitiveComparator.CASE_INSENSITIVE_ORDER);
            Charset.put(standardProvider.charsets(), treeMap);
            CharsetProvider charsetProvider = Charset.ExtendedProviderHolder.extendedProvider;
            if (charsetProvider != null)
              Charset.put(charsetProvider.charsets(), treeMap); 
            Iterator iterator = Charset.providers();
            while (iterator.hasNext()) {
              CharsetProvider charsetProvider1 = (CharsetProvider)iterator.next();
              Charset.put(charsetProvider1.charsets(), treeMap);
            } 
            return Collections.unmodifiableSortedMap(treeMap);
          }
        }); }
  
  public static Charset defaultCharset() {
    if (defaultCharset == null)
      synchronized (Charset.class) {
        String str = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
        Charset charset = lookup(str);
        if (charset != null) {
          defaultCharset = charset;
        } else {
          defaultCharset = forName("UTF-8");
        } 
      }  
    return defaultCharset;
  }
  
  protected Charset(String paramString, String[] paramArrayOfString) {
    checkName(paramString);
    String[] arrayOfString = (paramArrayOfString == null) ? new String[0] : paramArrayOfString;
    for (byte b = 0; b < arrayOfString.length; b++)
      checkName(arrayOfString[b]); 
    this.name = paramString;
    this.aliases = arrayOfString;
  }
  
  public final String name() { return this.name; }
  
  public final Set<String> aliases() {
    if (this.aliasSet != null)
      return this.aliasSet; 
    int i = this.aliases.length;
    HashSet hashSet = new HashSet(i);
    for (byte b = 0; b < i; b++)
      hashSet.add(this.aliases[b]); 
    this.aliasSet = Collections.unmodifiableSet(hashSet);
    return this.aliasSet;
  }
  
  public String displayName() { return this.name; }
  
  public final boolean isRegistered() { return (!this.name.startsWith("X-") && !this.name.startsWith("x-")); }
  
  public String displayName(Locale paramLocale) { return this.name; }
  
  public abstract boolean contains(Charset paramCharset);
  
  public abstract CharsetDecoder newDecoder();
  
  public abstract CharsetEncoder newEncoder();
  
  public boolean canEncode() { return true; }
  
  public final CharBuffer decode(ByteBuffer paramByteBuffer) {
    try {
      return ThreadLocalCoders.decoderFor(this).onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).decode(paramByteBuffer);
    } catch (CharacterCodingException characterCodingException) {
      throw new Error(characterCodingException);
    } 
  }
  
  public final ByteBuffer encode(CharBuffer paramCharBuffer) {
    try {
      return ThreadLocalCoders.encoderFor(this).onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).encode(paramCharBuffer);
    } catch (CharacterCodingException characterCodingException) {
      throw new Error(characterCodingException);
    } 
  }
  
  public final ByteBuffer encode(String paramString) { return encode(CharBuffer.wrap(paramString)); }
  
  public final int compareTo(Charset paramCharset) { return name().compareToIgnoreCase(paramCharset.name()); }
  
  public final int hashCode() { return name().hashCode(); }
  
  public final boolean equals(Object paramObject) { return !(paramObject instanceof Charset) ? false : ((this == paramObject) ? true : this.name.equals(((Charset)paramObject).name())); }
  
  public final String toString() { return name(); }
  
  private static class ExtendedProviderHolder {
    static final CharsetProvider extendedProvider = extendedProvider();
    
    private static CharsetProvider extendedProvider() { return (CharsetProvider)AccessController.doPrivileged(new PrivilegedAction<CharsetProvider>() {
            public CharsetProvider run() {
              try {
                Class clazz = Class.forName("sun.nio.cs.ext.ExtendedCharsets");
                return (CharsetProvider)clazz.newInstance();
              } catch (ClassNotFoundException classNotFoundException) {
              
              } catch (InstantiationException|IllegalAccessException instantiationException) {
                throw new Error(instantiationException);
              } 
              return null;
            }
          }); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\charset\Charset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */