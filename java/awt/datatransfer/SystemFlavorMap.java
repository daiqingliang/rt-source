package java.awt.datatransfer;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import sun.awt.AppContext;
import sun.awt.datatransfer.DataTransferer;

public final class SystemFlavorMap implements FlavorMap, FlavorTable {
  private static String JavaMIME = "JAVA_DATAFLAVOR:";
  
  private static final Object FLAVOR_MAP_KEY = new Object();
  
  private static final String keyValueSeparators = "=: \t\r\n\f";
  
  private static final String strictKeyValueSeparators = "=:";
  
  private static final String whiteSpaceChars = " \t\r\n\f";
  
  private static final String[] UNICODE_TEXT_CLASSES = { "java.io.Reader", "java.lang.String", "java.nio.CharBuffer", "\"[C\"" };
  
  private static final String[] ENCODED_TEXT_CLASSES = { "java.io.InputStream", "java.nio.ByteBuffer", "\"[B\"" };
  
  private static final String TEXT_PLAIN_BASE_TYPE = "text/plain";
  
  private static final String HTML_TEXT_BASE_TYPE = "text/html";
  
  private final Map<String, LinkedHashSet<DataFlavor>> nativeToFlavor = new HashMap();
  
  private final Map<DataFlavor, LinkedHashSet<String>> flavorToNative = new HashMap();
  
  private Map<String, LinkedHashSet<String>> textTypeToNative = new HashMap();
  
  private boolean isMapInitialized = false;
  
  private final SoftCache<DataFlavor, String> nativesForFlavorCache = new SoftCache(null);
  
  private final SoftCache<String, DataFlavor> flavorsForNativeCache = new SoftCache(null);
  
  private Set<Object> disabledMappingGenerationKeys = new HashSet();
  
  private static final String[] htmlDocumntTypes = { "all", "selection", "fragment" };
  
  private Map<String, LinkedHashSet<DataFlavor>> getNativeToFlavor() {
    if (!this.isMapInitialized)
      initSystemFlavorMap(); 
    return this.nativeToFlavor;
  }
  
  private Map<DataFlavor, LinkedHashSet<String>> getFlavorToNative() {
    if (!this.isMapInitialized)
      initSystemFlavorMap(); 
    return this.flavorToNative;
  }
  
  private Map<String, LinkedHashSet<String>> getTextTypeToNative() {
    if (!this.isMapInitialized) {
      initSystemFlavorMap();
      this.textTypeToNative = Collections.unmodifiableMap(this.textTypeToNative);
    } 
    return this.textTypeToNative;
  }
  
  public static FlavorMap getDefaultFlavorMap() {
    AppContext appContext = AppContext.getAppContext();
    FlavorMap flavorMap = (FlavorMap)appContext.get(FLAVOR_MAP_KEY);
    if (flavorMap == null) {
      flavorMap = new SystemFlavorMap();
      appContext.put(FLAVOR_MAP_KEY, flavorMap);
    } 
    return flavorMap;
  }
  
  private void initSystemFlavorMap() {
    if (this.isMapInitialized)
      return; 
    this.isMapInitialized = true;
    BufferedReader bufferedReader1 = (BufferedReader)AccessController.doPrivileged(new PrivilegedAction<BufferedReader>() {
          public BufferedReader run() {
            String str = System.getProperty("java.home") + File.separator + "lib" + File.separator + "flavormap.properties";
            try {
              return new BufferedReader(new InputStreamReader((new File(str)).toURI().toURL().openStream(), "ISO-8859-1"));
            } catch (MalformedURLException malformedURLException) {
              System.err.println("MalformedURLException:" + malformedURLException + " while loading default flavormap.properties file:" + str);
            } catch (IOException iOException) {
              System.err.println("IOException:" + iOException + " while loading default flavormap.properties file:" + str);
            } 
            return null;
          }
        });
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Toolkit.getProperty("AWT.DnD.flavorMapFileURL", null); }
        });
    if (bufferedReader1 != null)
      try {
        parseAndStoreReader(bufferedReader1);
      } catch (IOException iOException) {
        System.err.println("IOException:" + iOException + " while parsing default flavormap.properties file");
      }  
    BufferedReader bufferedReader2 = null;
    if (str != null)
      try {
        bufferedReader2 = new BufferedReader(new InputStreamReader((new URL(str)).openStream(), "ISO-8859-1"));
      } catch (MalformedURLException malformedURLException) {
        System.err.println("MalformedURLException:" + malformedURLException + " while reading AWT.DnD.flavorMapFileURL:" + str);
      } catch (IOException iOException) {
        System.err.println("IOException:" + iOException + " while reading AWT.DnD.flavorMapFileURL:" + str);
      } catch (SecurityException securityException) {} 
    if (bufferedReader2 != null)
      try {
        parseAndStoreReader(bufferedReader2);
      } catch (IOException iOException) {
        System.err.println("IOException:" + iOException + " while parsing AWT.DnD.flavorMapFileURL");
      }  
  }
  
  private void parseAndStoreReader(BufferedReader paramBufferedReader) throws IOException {
    while (true) {
      String str = paramBufferedReader.readLine();
      if (str == null)
        return; 
      if (str.length() > 0) {
        char c = str.charAt(0);
        if (c != '#' && c != '!') {
          DataFlavor dataFlavor;
          while (continueLine(str)) {
            String str3 = paramBufferedReader.readLine();
            if (str3 == null)
              str3 = ""; 
            String str4 = str.substring(0, str.length() - 1);
            byte b;
            for (b = 0; b < str3.length() && " \t\r\n\f".indexOf(str3.charAt(b)) != -1; b++);
            str3 = str3.substring(b, str3.length());
            str = str4 + str3;
          } 
          int i = str.length();
          byte b1;
          for (b1 = 0; b1 < i && " \t\r\n\f".indexOf(str.charAt(b1)) != -1; b1++);
          if (b1 == i)
            continue; 
          byte b2;
          for (b2 = b1; b2 < i; b2++) {
            char c1 = str.charAt(b2);
            if (c1 == '\\') {
              b2++;
            } else if ("=: \t\r\n\f".indexOf(c1) != -1) {
              break;
            } 
          } 
          byte b3;
          for (b3 = b2; b3 < i && " \t\r\n\f".indexOf(str.charAt(b3)) != -1; b3++);
          if (b3 < i && "=:".indexOf(str.charAt(b3)) != -1)
            b3++; 
          while (b3 < i && " \t\r\n\f".indexOf(str.charAt(b3)) != -1)
            b3++; 
          String str1 = str.substring(b1, b2);
          String str2 = (b2 < i) ? str.substring(b3, i) : "";
          str1 = loadConvert(str1);
          str2 = loadConvert(str2);
          try {
            dataFlavor = new MimeType(str2);
            if ("text".equals(dataFlavor.getPrimaryType())) {
              String str3 = dataFlavor.getParameter("charset");
              if (DataTransferer.doesSubtypeSupportCharset(dataFlavor.getSubType(), str3)) {
                DataTransferer dataTransferer = DataTransferer.getInstance();
                if (dataTransferer != null)
                  dataTransferer.registerTextFlavorProperties(str1, str3, dataFlavor.getParameter("eoln"), dataFlavor.getParameter("terminators")); 
              } 
              dataFlavor.removeParameter("charset");
              dataFlavor.removeParameter("class");
              dataFlavor.removeParameter("eoln");
              dataFlavor.removeParameter("terminators");
              str2 = dataFlavor.toString();
            } 
          } catch (MimeTypeParseException null) {
            dataFlavor.printStackTrace();
            continue;
          } 
          try {
            dataFlavor = new DataFlavor(str2);
          } catch (Exception exception) {
            try {
              dataFlavor = new DataFlavor(str2, null);
            } catch (Exception exception1) {
              exception1.printStackTrace();
              continue;
            } 
          } 
          LinkedHashSet linkedHashSet = new LinkedHashSet();
          linkedHashSet.add(dataFlavor);
          if ("text".equals(dataFlavor.getPrimaryType())) {
            linkedHashSet.addAll(convertMimeTypeToDataFlavors(str2));
            store(dataFlavor.mimeType.getBaseType(), str1, getTextTypeToNative());
          } 
          for (DataFlavor dataFlavor1 : linkedHashSet) {
            store(dataFlavor1, str1, getFlavorToNative());
            store(str1, dataFlavor1, getNativeToFlavor());
          } 
        } 
      } 
    } 
  }
  
  private boolean continueLine(String paramString) {
    byte b = 0;
    int i = paramString.length() - 1;
    while (i >= 0 && paramString.charAt(i--) == '\\')
      b++; 
    return (b % 2 == 1);
  }
  
  private String loadConvert(String paramString) {
    int i = paramString.length();
    StringBuilder stringBuilder = new StringBuilder(i);
    byte b = 0;
    while (b < i) {
      char c = paramString.charAt(b++);
      if (c == '\\') {
        c = paramString.charAt(b++);
        if (c == 'u') {
          char c1 = Character.MIN_VALUE;
          for (byte b1 = 0; b1 < 4; b1++) {
            c = paramString.charAt(b++);
            switch (c) {
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
                c1 = (c1 << 4) + c - '0';
                break;
              case 'a':
              case 'b':
              case 'c':
              case 'd':
              case 'e':
              case 'f':
                c1 = (c1 << '\004') + '\n' + c - 'a';
                break;
              case 'A':
              case 'B':
              case 'C':
              case 'D':
              case 'E':
              case 'F':
                c1 = (c1 << '\004') + '\n' + c - 'A';
                break;
              default:
                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
            } 
          } 
          stringBuilder.append((char)c1);
          continue;
        } 
        if (c == 't') {
          c = '\t';
        } else if (c == 'r') {
          c = '\r';
        } else if (c == 'n') {
          c = '\n';
        } else if (c == 'f') {
          c = '\f';
        } 
        stringBuilder.append(c);
        continue;
      } 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  private <H, L> void store(H paramH, L paramL, Map<H, LinkedHashSet<L>> paramMap) {
    LinkedHashSet linkedHashSet = (LinkedHashSet)paramMap.get(paramH);
    if (linkedHashSet == null) {
      linkedHashSet = new LinkedHashSet(1);
      paramMap.put(paramH, linkedHashSet);
    } 
    if (!linkedHashSet.contains(paramL))
      linkedHashSet.add(paramL); 
  }
  
  private LinkedHashSet<DataFlavor> nativeToFlavorLookup(String paramString) {
    LinkedHashSet linkedHashSet = (LinkedHashSet)getNativeToFlavor().get(paramString);
    if (paramString != null && !this.disabledMappingGenerationKeys.contains(paramString)) {
      DataTransferer dataTransferer = DataTransferer.getInstance();
      if (dataTransferer != null) {
        LinkedHashSet linkedHashSet1 = dataTransferer.getPlatformMappingsForNative(paramString);
        if (!linkedHashSet1.isEmpty()) {
          if (linkedHashSet != null)
            linkedHashSet1.addAll(linkedHashSet); 
          linkedHashSet = linkedHashSet1;
        } 
      } 
    } 
    if (linkedHashSet == null && isJavaMIMEType(paramString)) {
      String str = decodeJavaMIMEType(paramString);
      DataFlavor dataFlavor = null;
      try {
        dataFlavor = new DataFlavor(str);
      } catch (Exception exception) {
        System.err.println("Exception \"" + exception.getClass().getName() + ": " + exception.getMessage() + "\"while constructing DataFlavor for: " + str);
      } 
      if (dataFlavor != null) {
        linkedHashSet = new LinkedHashSet(1);
        getNativeToFlavor().put(paramString, linkedHashSet);
        linkedHashSet.add(dataFlavor);
        this.flavorsForNativeCache.remove(paramString);
        LinkedHashSet linkedHashSet1 = (LinkedHashSet)getFlavorToNative().get(dataFlavor);
        if (linkedHashSet1 == null) {
          linkedHashSet1 = new LinkedHashSet(1);
          getFlavorToNative().put(dataFlavor, linkedHashSet1);
        } 
        linkedHashSet1.add(paramString);
        this.nativesForFlavorCache.remove(dataFlavor);
      } 
    } 
    return (linkedHashSet != null) ? linkedHashSet : new LinkedHashSet(0);
  }
  
  private LinkedHashSet<String> flavorToNativeLookup(DataFlavor paramDataFlavor, boolean paramBoolean) {
    LinkedHashSet linkedHashSet = (LinkedHashSet)getFlavorToNative().get(paramDataFlavor);
    if (paramDataFlavor != null && !this.disabledMappingGenerationKeys.contains(paramDataFlavor)) {
      DataTransferer dataTransferer = DataTransferer.getInstance();
      if (dataTransferer != null) {
        LinkedHashSet linkedHashSet1 = dataTransferer.getPlatformMappingsForFlavor(paramDataFlavor);
        if (!linkedHashSet1.isEmpty()) {
          if (linkedHashSet != null)
            linkedHashSet1.addAll(linkedHashSet); 
          linkedHashSet = linkedHashSet1;
        } 
      } 
    } 
    if (linkedHashSet == null)
      if (paramBoolean) {
        String str = encodeDataFlavor(paramDataFlavor);
        linkedHashSet = new LinkedHashSet(1);
        getFlavorToNative().put(paramDataFlavor, linkedHashSet);
        linkedHashSet.add(str);
        LinkedHashSet linkedHashSet1 = (LinkedHashSet)getNativeToFlavor().get(str);
        if (linkedHashSet1 == null) {
          linkedHashSet1 = new LinkedHashSet(1);
          getNativeToFlavor().put(str, linkedHashSet1);
        } 
        linkedHashSet1.add(paramDataFlavor);
        this.nativesForFlavorCache.remove(paramDataFlavor);
        this.flavorsForNativeCache.remove(str);
      } else {
        linkedHashSet = new LinkedHashSet(0);
      }  
    return new LinkedHashSet(linkedHashSet);
  }
  
  public List<String> getNativesForFlavor(DataFlavor paramDataFlavor) {
    LinkedHashSet linkedHashSet = this.nativesForFlavorCache.check(paramDataFlavor);
    if (linkedHashSet != null)
      return new ArrayList(linkedHashSet); 
    if (paramDataFlavor == null) {
      linkedHashSet = new LinkedHashSet(getNativeToFlavor().keySet());
    } else if (this.disabledMappingGenerationKeys.contains(paramDataFlavor)) {
      linkedHashSet = flavorToNativeLookup(paramDataFlavor, false);
    } else if (DataTransferer.isFlavorCharsetTextType(paramDataFlavor)) {
      linkedHashSet = new LinkedHashSet(0);
      if ("text".equals(paramDataFlavor.getPrimaryType())) {
        LinkedHashSet linkedHashSet2 = (LinkedHashSet)getTextTypeToNative().get(paramDataFlavor.mimeType.getBaseType());
        if (linkedHashSet2 != null)
          linkedHashSet.addAll(linkedHashSet2); 
      } 
      LinkedHashSet linkedHashSet1 = (LinkedHashSet)getTextTypeToNative().get("text/plain");
      if (linkedHashSet1 != null)
        linkedHashSet.addAll(linkedHashSet1); 
      if (linkedHashSet.isEmpty()) {
        linkedHashSet = flavorToNativeLookup(paramDataFlavor, true);
      } else {
        linkedHashSet.addAll(flavorToNativeLookup(paramDataFlavor, false));
      } 
    } else if (DataTransferer.isFlavorNoncharsetTextType(paramDataFlavor)) {
      linkedHashSet = (LinkedHashSet)getTextTypeToNative().get(paramDataFlavor.mimeType.getBaseType());
      if (linkedHashSet == null || linkedHashSet.isEmpty()) {
        linkedHashSet = flavorToNativeLookup(paramDataFlavor, true);
      } else {
        linkedHashSet.addAll(flavorToNativeLookup(paramDataFlavor, false));
      } 
    } else {
      linkedHashSet = flavorToNativeLookup(paramDataFlavor, true);
    } 
    this.nativesForFlavorCache.put(paramDataFlavor, linkedHashSet);
    return new ArrayList(linkedHashSet);
  }
  
  public List<DataFlavor> getFlavorsForNative(String paramString) {
    LinkedHashSet linkedHashSet = this.flavorsForNativeCache.check(paramString);
    if (linkedHashSet != null)
      return new ArrayList(linkedHashSet); 
    linkedHashSet = new LinkedHashSet();
    if (paramString == null) {
      for (String str : getNativesForFlavor(null))
        linkedHashSet.addAll(getFlavorsForNative(str)); 
    } else {
      LinkedHashSet linkedHashSet1 = nativeToFlavorLookup(paramString);
      if (this.disabledMappingGenerationKeys.contains(paramString))
        return new ArrayList(linkedHashSet1); 
      LinkedHashSet linkedHashSet2 = nativeToFlavorLookup(paramString);
      for (DataFlavor dataFlavor : linkedHashSet2) {
        linkedHashSet.add(dataFlavor);
        if ("text".equals(dataFlavor.getPrimaryType())) {
          String str = dataFlavor.mimeType.getBaseType();
          linkedHashSet.addAll(convertMimeTypeToDataFlavors(str));
        } 
      } 
    } 
    this.flavorsForNativeCache.put(paramString, linkedHashSet);
    return new ArrayList(linkedHashSet);
  }
  
  private static Set<DataFlavor> convertMimeTypeToDataFlavors(String paramString) {
    LinkedHashSet linkedHashSet = new LinkedHashSet();
    String str = null;
    try {
      MimeType mimeType = new MimeType(paramString);
      str = mimeType.getSubType();
    } catch (MimeTypeParseException mimeTypeParseException) {}
    if (DataTransferer.doesSubtypeSupportCharset(str, null)) {
      if ("text/plain".equals(paramString))
        linkedHashSet.add(DataFlavor.stringFlavor); 
      for (String str1 : UNICODE_TEXT_CLASSES) {
        String str2 = paramString + ";charset=Unicode;class=" + str1;
        LinkedHashSet linkedHashSet1 = handleHtmlMimeTypes(paramString, str2);
        for (String str3 : linkedHashSet1) {
          DataFlavor dataFlavor = null;
          try {
            dataFlavor = new DataFlavor(str3);
          } catch (ClassNotFoundException classNotFoundException) {}
          linkedHashSet.add(dataFlavor);
        } 
      } 
      for (String str1 : DataTransferer.standardEncodings()) {
        for (String str2 : ENCODED_TEXT_CLASSES) {
          String str3 = paramString + ";charset=" + str1 + ";class=" + str2;
          LinkedHashSet linkedHashSet1 = handleHtmlMimeTypes(paramString, str3);
          for (String str4 : linkedHashSet1) {
            DataFlavor dataFlavor = null;
            try {
              dataFlavor = new DataFlavor(str4);
              if (dataFlavor.equals(DataFlavor.plainTextFlavor))
                dataFlavor = DataFlavor.plainTextFlavor; 
            } catch (ClassNotFoundException classNotFoundException) {}
            linkedHashSet.add(dataFlavor);
          } 
        } 
      } 
      if ("text/plain".equals(paramString))
        linkedHashSet.add(DataFlavor.plainTextFlavor); 
    } else {
      for (String str1 : ENCODED_TEXT_CLASSES) {
        DataFlavor dataFlavor = null;
        try {
          dataFlavor = new DataFlavor(paramString + ";class=" + str1);
        } catch (ClassNotFoundException classNotFoundException) {}
        linkedHashSet.add(dataFlavor);
      } 
    } 
    return linkedHashSet;
  }
  
  private static LinkedHashSet<String> handleHtmlMimeTypes(String paramString1, String paramString2) {
    LinkedHashSet linkedHashSet = new LinkedHashSet();
    if ("text/html".equals(paramString1)) {
      for (String str : htmlDocumntTypes)
        linkedHashSet.add(paramString2 + ";document=" + str); 
    } else {
      linkedHashSet.add(paramString2);
    } 
    return linkedHashSet;
  }
  
  public Map<DataFlavor, String> getNativesForFlavors(DataFlavor[] paramArrayOfDataFlavor) {
    if (paramArrayOfDataFlavor == null) {
      List list = getFlavorsForNative(null);
      paramArrayOfDataFlavor = new DataFlavor[list.size()];
      list.toArray(paramArrayOfDataFlavor);
    } 
    HashMap hashMap = new HashMap(paramArrayOfDataFlavor.length, 1.0F);
    for (DataFlavor dataFlavor : paramArrayOfDataFlavor) {
      List list = getNativesForFlavor(dataFlavor);
      String str = list.isEmpty() ? null : (String)list.get(0);
      hashMap.put(dataFlavor, str);
    } 
    return hashMap;
  }
  
  public Map<String, DataFlavor> getFlavorsForNatives(String[] paramArrayOfString) {
    if (paramArrayOfString == null) {
      List list = getNativesForFlavor(null);
      paramArrayOfString = new String[list.size()];
      list.toArray(paramArrayOfString);
    } 
    HashMap hashMap = new HashMap(paramArrayOfString.length, 1.0F);
    for (String str : paramArrayOfString) {
      List list = getFlavorsForNative(str);
      DataFlavor dataFlavor = list.isEmpty() ? null : (DataFlavor)list.get(0);
      hashMap.put(str, dataFlavor);
    } 
    return hashMap;
  }
  
  public void addUnencodedNativeForFlavor(DataFlavor paramDataFlavor, String paramString) {
    Objects.requireNonNull(paramString, "Null native not permitted");
    Objects.requireNonNull(paramDataFlavor, "Null flavor not permitted");
    LinkedHashSet linkedHashSet = (LinkedHashSet)getFlavorToNative().get(paramDataFlavor);
    if (linkedHashSet == null) {
      linkedHashSet = new LinkedHashSet(1);
      getFlavorToNative().put(paramDataFlavor, linkedHashSet);
    } 
    linkedHashSet.add(paramString);
    this.nativesForFlavorCache.remove(paramDataFlavor);
  }
  
  public void setNativesForFlavor(DataFlavor paramDataFlavor, String[] paramArrayOfString) {
    Objects.requireNonNull(paramArrayOfString, "Null natives not permitted");
    Objects.requireNonNull(paramDataFlavor, "Null flavors not permitted");
    getFlavorToNative().remove(paramDataFlavor);
    for (String str : paramArrayOfString)
      addUnencodedNativeForFlavor(paramDataFlavor, str); 
    this.disabledMappingGenerationKeys.add(paramDataFlavor);
    this.nativesForFlavorCache.remove(paramDataFlavor);
  }
  
  public void addFlavorForUnencodedNative(String paramString, DataFlavor paramDataFlavor) {
    Objects.requireNonNull(paramString, "Null native not permitted");
    Objects.requireNonNull(paramDataFlavor, "Null flavor not permitted");
    LinkedHashSet linkedHashSet = (LinkedHashSet)getNativeToFlavor().get(paramString);
    if (linkedHashSet == null) {
      linkedHashSet = new LinkedHashSet(1);
      getNativeToFlavor().put(paramString, linkedHashSet);
    } 
    linkedHashSet.add(paramDataFlavor);
    this.flavorsForNativeCache.remove(paramString);
  }
  
  public void setFlavorsForNative(String paramString, DataFlavor[] paramArrayOfDataFlavor) {
    Objects.requireNonNull(paramString, "Null native not permitted");
    Objects.requireNonNull(paramArrayOfDataFlavor, "Null flavors not permitted");
    getNativeToFlavor().remove(paramString);
    for (DataFlavor dataFlavor : paramArrayOfDataFlavor)
      addFlavorForUnencodedNative(paramString, dataFlavor); 
    this.disabledMappingGenerationKeys.add(paramString);
    this.flavorsForNativeCache.remove(paramString);
  }
  
  public static String encodeJavaMIMEType(String paramString) { return (paramString != null) ? (JavaMIME + paramString) : null; }
  
  public static String encodeDataFlavor(DataFlavor paramDataFlavor) { return (paramDataFlavor != null) ? encodeJavaMIMEType(paramDataFlavor.getMimeType()) : null; }
  
  public static boolean isJavaMIMEType(String paramString) { return (paramString != null && paramString.startsWith(JavaMIME, 0)); }
  
  public static String decodeJavaMIMEType(String paramString) { return isJavaMIMEType(paramString) ? paramString.substring(JavaMIME.length(), paramString.length()).trim() : null; }
  
  public static DataFlavor decodeDataFlavor(String paramString) throws ClassNotFoundException {
    String str = decodeJavaMIMEType(paramString);
    return (str != null) ? new DataFlavor(str) : null;
  }
  
  private static final class SoftCache<K, V> extends Object {
    Map<K, SoftReference<LinkedHashSet<V>>> cache;
    
    private SoftCache() {}
    
    public void put(K param1K, LinkedHashSet<V> param1LinkedHashSet) {
      if (this.cache == null)
        this.cache = new HashMap(1); 
      this.cache.put(param1K, new SoftReference(param1LinkedHashSet));
    }
    
    public void remove(K param1K) {
      if (this.cache == null)
        return; 
      this.cache.remove(null);
      this.cache.remove(param1K);
    }
    
    public LinkedHashSet<V> check(K param1K) {
      if (this.cache == null)
        return null; 
      SoftReference softReference = (SoftReference)this.cache.get(param1K);
      return (softReference != null) ? (LinkedHashSet)softReference.get() : null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\datatransfer\SystemFlavorMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */