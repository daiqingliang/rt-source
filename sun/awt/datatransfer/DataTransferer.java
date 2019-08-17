package sun.awt.datatransfer;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import sun.awt.AppContext;
import sun.awt.ComponentFactory;
import sun.awt.SunToolkit;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.ToolkitImage;
import sun.util.logging.PlatformLogger;

public abstract class DataTransferer {
  public static final DataFlavor plainTextStringFlavor;
  
  public static final DataFlavor javaTextEncodingFlavor;
  
  private static final Map textMIMESubtypeCharsetSupport;
  
  private static String defaultEncoding;
  
  private static final Set textNatives = Collections.synchronizedSet(new HashSet());
  
  private static final Map nativeCharsets = Collections.synchronizedMap(new HashMap());
  
  private static final Map nativeEOLNs = Collections.synchronizedMap(new HashMap());
  
  private static final Map nativeTerminators = Collections.synchronizedMap(new HashMap());
  
  private static final String DATA_CONVERTER_KEY = "DATA_CONVERTER_KEY";
  
  private static DataTransferer transferer;
  
  private static final PlatformLogger dtLog = PlatformLogger.getLogger("sun.awt.datatransfer.DataTransfer");
  
  private static final String[] DEPLOYMENT_CACHE_PROPERTIES;
  
  private static final ArrayList<File> deploymentCacheDirectoryList;
  
  public static DataTransferer getInstance() { return ((ComponentFactory)Toolkit.getDefaultToolkit()).getDataTransferer(); }
  
  public static String canonicalName(String paramString) {
    if (paramString == null)
      return null; 
    try {
      return Charset.forName(paramString).name();
    } catch (IllegalCharsetNameException illegalCharsetNameException) {
      return paramString;
    } catch (UnsupportedCharsetException unsupportedCharsetException) {
      return paramString;
    } 
  }
  
  public static String getTextCharset(DataFlavor paramDataFlavor) {
    if (!isFlavorCharsetTextType(paramDataFlavor))
      return null; 
    String str = paramDataFlavor.getParameter("charset");
    return (str != null) ? str : getDefaultTextCharset();
  }
  
  public static String getDefaultTextCharset() { return (defaultEncoding != null) ? defaultEncoding : (defaultEncoding = Charset.defaultCharset().name()); }
  
  public static boolean doesSubtypeSupportCharset(DataFlavor paramDataFlavor) {
    if (dtLog.isLoggable(PlatformLogger.Level.FINE) && !"text".equals(paramDataFlavor.getPrimaryType()))
      dtLog.fine("Assertion (\"text\".equals(flavor.getPrimaryType())) failed"); 
    String str = paramDataFlavor.getSubType();
    if (str == null)
      return false; 
    Object object = textMIMESubtypeCharsetSupport.get(str);
    if (object != null)
      return (object == Boolean.TRUE); 
    boolean bool = (paramDataFlavor.getParameter("charset") != null);
    textMIMESubtypeCharsetSupport.put(str, bool ? Boolean.TRUE : Boolean.FALSE);
    return bool;
  }
  
  public static boolean doesSubtypeSupportCharset(String paramString1, String paramString2) {
    Object object = textMIMESubtypeCharsetSupport.get(paramString1);
    if (object != null)
      return (object == Boolean.TRUE); 
    boolean bool = (paramString2 != null);
    textMIMESubtypeCharsetSupport.put(paramString1, bool ? Boolean.TRUE : Boolean.FALSE);
    return bool;
  }
  
  public static boolean isFlavorCharsetTextType(DataFlavor paramDataFlavor) {
    if (DataFlavor.stringFlavor.equals(paramDataFlavor))
      return true; 
    if (!"text".equals(paramDataFlavor.getPrimaryType()) || !doesSubtypeSupportCharset(paramDataFlavor))
      return false; 
    Class clazz = paramDataFlavor.getRepresentationClass();
    if (paramDataFlavor.isRepresentationClassReader() || String.class.equals(clazz) || paramDataFlavor.isRepresentationClassCharBuffer() || char[].class.equals(clazz))
      return true; 
    if (!paramDataFlavor.isRepresentationClassInputStream() && !paramDataFlavor.isRepresentationClassByteBuffer() && !byte[].class.equals(clazz))
      return false; 
    String str = paramDataFlavor.getParameter("charset");
    return (str != null) ? isEncodingSupported(str) : 1;
  }
  
  public static boolean isFlavorNoncharsetTextType(DataFlavor paramDataFlavor) { return (!"text".equals(paramDataFlavor.getPrimaryType()) || doesSubtypeSupportCharset(paramDataFlavor)) ? false : ((paramDataFlavor.isRepresentationClassInputStream() || paramDataFlavor.isRepresentationClassByteBuffer() || byte[].class.equals(paramDataFlavor.getRepresentationClass()))); }
  
  public static boolean isEncodingSupported(String paramString) {
    if (paramString == null)
      return false; 
    try {
      return Charset.isSupported(paramString);
    } catch (IllegalCharsetNameException illegalCharsetNameException) {
      return false;
    } 
  }
  
  public static boolean isRemote(Class<?> paramClass) { return RMI.isRemote(paramClass); }
  
  public static Set<String> standardEncodings() { return standardEncodings; }
  
  public static FlavorTable adaptFlavorMap(final FlavorMap map) { return (paramFlavorMap instanceof FlavorTable) ? (FlavorTable)paramFlavorMap : new FlavorTable() {
        public Map getNativesForFlavors(DataFlavor[] param1ArrayOfDataFlavor) { return map.getNativesForFlavors(param1ArrayOfDataFlavor); }
        
        public Map getFlavorsForNatives(String[] param1ArrayOfString) { return map.getFlavorsForNatives(param1ArrayOfString); }
        
        public List getNativesForFlavor(DataFlavor param1DataFlavor) {
          Map map = getNativesForFlavors(new DataFlavor[] { param1DataFlavor });
          String str = (String)map.get(param1DataFlavor);
          if (str != null) {
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(str);
            return arrayList;
          } 
          return Collections.EMPTY_LIST;
        }
        
        public List getFlavorsForNative(String param1String) {
          Map map = getFlavorsForNatives(new String[] { param1String });
          DataFlavor dataFlavor = (DataFlavor)map.get(param1String);
          if (dataFlavor != null) {
            ArrayList arrayList = new ArrayList(1);
            arrayList.add(dataFlavor);
            return arrayList;
          } 
          return Collections.EMPTY_LIST;
        }
      }; }
  
  public abstract String getDefaultUnicodeEncoding();
  
  public void registerTextFlavorProperties(String paramString1, String paramString2, String paramString3, String paramString4) {
    Long long = getFormatForNativeAsLong(paramString1);
    textNatives.add(long);
    nativeCharsets.put(long, (paramString2 != null && paramString2.length() != 0) ? paramString2 : getDefaultTextCharset());
    if (paramString3 != null && paramString3.length() != 0 && !paramString3.equals("\n"))
      nativeEOLNs.put(long, paramString3); 
    if (paramString4 != null && paramString4.length() != 0) {
      Integer integer = Integer.valueOf(paramString4);
      if (integer.intValue() > 0)
        nativeTerminators.put(long, integer); 
    } 
  }
  
  protected boolean isTextFormat(long paramLong) { return textNatives.contains(Long.valueOf(paramLong)); }
  
  protected String getCharsetForTextFormat(Long paramLong) { return (String)nativeCharsets.get(paramLong); }
  
  public abstract boolean isLocaleDependentTextFormat(long paramLong);
  
  public abstract boolean isFileFormat(long paramLong);
  
  public abstract boolean isImageFormat(long paramLong);
  
  protected boolean isURIListFormat(long paramLong) { return false; }
  
  public SortedMap<Long, DataFlavor> getFormatsForTransferable(Transferable paramTransferable, FlavorTable paramFlavorTable) {
    DataFlavor[] arrayOfDataFlavor = paramTransferable.getTransferDataFlavors();
    return (arrayOfDataFlavor == null) ? new TreeMap() : getFormatsForFlavors(arrayOfDataFlavor, paramFlavorTable);
  }
  
  public SortedMap getFormatsForFlavor(DataFlavor paramDataFlavor, FlavorTable paramFlavorTable) { return getFormatsForFlavors(new DataFlavor[] { paramDataFlavor }, paramFlavorTable); }
  
  public SortedMap<Long, DataFlavor> getFormatsForFlavors(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable) {
    HashMap hashMap1 = new HashMap(paramArrayOfDataFlavor.length);
    HashMap hashMap2 = new HashMap(paramArrayOfDataFlavor.length);
    HashMap hashMap3 = new HashMap(paramArrayOfDataFlavor.length);
    HashMap hashMap4 = new HashMap(paramArrayOfDataFlavor.length);
    int i = 0;
    for (int j = paramArrayOfDataFlavor.length - 1; j >= 0; j--) {
      DataFlavor dataFlavor = paramArrayOfDataFlavor[j];
      if (dataFlavor != null && (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote())) {
        List list = paramFlavorTable.getNativesForFlavor(dataFlavor);
        i += list.size();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
          Long long = getFormatForNativeAsLong((String)iterator.next());
          Integer integer = Integer.valueOf(i--);
          hashMap1.put(long, dataFlavor);
          hashMap3.put(long, integer);
          if (("text".equals(dataFlavor.getPrimaryType()) && "plain".equals(dataFlavor.getSubType())) || dataFlavor.equals(DataFlavor.stringFlavor)) {
            hashMap2.put(long, dataFlavor);
            hashMap4.put(long, integer);
          } 
        } 
        i += list.size();
      } 
    } 
    hashMap1.putAll(hashMap2);
    hashMap3.putAll(hashMap4);
    IndexOrderComparator indexOrderComparator = new IndexOrderComparator(hashMap3, false);
    TreeMap treeMap = new TreeMap(indexOrderComparator);
    treeMap.putAll(hashMap1);
    return treeMap;
  }
  
  public long[] getFormatsForTransferableAsArray(Transferable paramTransferable, FlavorTable paramFlavorTable) { return keysToLongArray(getFormatsForTransferable(paramTransferable, paramFlavorTable)); }
  
  public long[] getFormatsForFlavorAsArray(DataFlavor paramDataFlavor, FlavorTable paramFlavorTable) { return keysToLongArray(getFormatsForFlavor(paramDataFlavor, paramFlavorTable)); }
  
  public long[] getFormatsForFlavorsAsArray(DataFlavor[] paramArrayOfDataFlavor, FlavorTable paramFlavorTable) { return keysToLongArray(getFormatsForFlavors(paramArrayOfDataFlavor, paramFlavorTable)); }
  
  public Map getFlavorsForFormat(long paramLong, FlavorTable paramFlavorTable) { return getFlavorsForFormats(new long[] { paramLong }, paramFlavorTable); }
  
  public Map getFlavorsForFormats(long[] paramArrayOfLong, FlavorTable paramFlavorTable) {
    HashMap hashMap = new HashMap(paramArrayOfLong.length);
    HashSet hashSet1 = new HashSet(paramArrayOfLong.length);
    HashSet hashSet2 = new HashSet(paramArrayOfLong.length);
    for (byte b = 0; b < paramArrayOfLong.length; b++) {
      long l = paramArrayOfLong[b];
      String str = getNativeForFormat(l);
      List list = paramFlavorTable.getFlavorsForNative(str);
      for (DataFlavor dataFlavor : list) {
        if (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote()) {
          Long long = Long.valueOf(l);
          Object object = createMapping(long, dataFlavor);
          hashMap.put(dataFlavor, long);
          hashSet1.add(object);
          hashSet2.add(dataFlavor);
        } 
      } 
    } 
    for (DataFlavor dataFlavor : hashSet2) {
      List list = paramFlavorTable.getNativesForFlavor(dataFlavor);
      Iterator iterator = list.iterator();
      while (iterator.hasNext()) {
        Long long = getFormatForNativeAsLong((String)iterator.next());
        Object object = createMapping(long, dataFlavor);
        if (hashSet1.contains(object))
          hashMap.put(dataFlavor, long); 
      } 
    } 
    return hashMap;
  }
  
  public Set getFlavorsForFormatsAsSet(long[] paramArrayOfLong, FlavorTable paramFlavorTable) {
    HashSet hashSet = new HashSet(paramArrayOfLong.length);
    for (byte b = 0; b < paramArrayOfLong.length; b++) {
      String str = getNativeForFormat(paramArrayOfLong[b]);
      List list = paramFlavorTable.getFlavorsForNative(str);
      for (DataFlavor dataFlavor : list) {
        if (dataFlavor.isFlavorTextType() || dataFlavor.isFlavorJavaFileListType() || DataFlavor.imageFlavor.equals(dataFlavor) || dataFlavor.isRepresentationClassSerializable() || dataFlavor.isRepresentationClassInputStream() || dataFlavor.isRepresentationClassRemote())
          hashSet.add(dataFlavor); 
      } 
    } 
    return hashSet;
  }
  
  public DataFlavor[] getFlavorsForFormatAsArray(long paramLong, FlavorTable paramFlavorTable) { return getFlavorsForFormatsAsArray(new long[] { paramLong }, paramFlavorTable); }
  
  public DataFlavor[] getFlavorsForFormatsAsArray(long[] paramArrayOfLong, FlavorTable paramFlavorTable) { return setToSortedDataFlavorArray(getFlavorsForFormatsAsSet(paramArrayOfLong, paramFlavorTable)); }
  
  private static Object createMapping(Object paramObject1, Object paramObject2) { return Arrays.asList(new Object[] { paramObject1, paramObject2 }); }
  
  protected abstract Long getFormatForNativeAsLong(String paramString);
  
  protected abstract String getNativeForFormat(long paramLong);
  
  private String getBestCharsetForTextFormat(Long paramLong, Transferable paramTransferable) throws IOException {
    String str = null;
    if (paramTransferable != null && isLocaleDependentTextFormat(paramLong.longValue()) && paramTransferable.isDataFlavorSupported(javaTextEncodingFlavor)) {
      try {
        str = new String((byte[])paramTransferable.getTransferData(javaTextEncodingFlavor), "UTF-8");
      } catch (UnsupportedFlavorException unsupportedFlavorException) {}
    } else {
      str = getCharsetForTextFormat(paramLong);
    } 
    if (str == null)
      str = getDefaultTextCharset(); 
    return str;
  }
  
  private byte[] translateTransferableString(String paramString, long paramLong) throws IOException {
    Long long = Long.valueOf(paramLong);
    String str1 = getBestCharsetForTextFormat(long, null);
    String str2 = (String)nativeEOLNs.get(long);
    if (str2 != null) {
      int i = paramString.length();
      StringBuffer stringBuffer = new StringBuffer(i * 2);
      for (int j = 0; j < i; j++) {
        if (paramString.startsWith(str2, j)) {
          stringBuffer.append(str2);
          j += str2.length() - 1;
        } else {
          char c = paramString.charAt(j);
          if (c == '\n') {
            stringBuffer.append(str2);
          } else {
            stringBuffer.append(c);
          } 
        } 
      } 
      paramString = stringBuffer.toString();
    } 
    byte[] arrayOfByte = paramString.getBytes(str1);
    Integer integer = (Integer)nativeTerminators.get(long);
    if (integer != null) {
      int i = integer.intValue();
      byte[] arrayOfByte1 = new byte[arrayOfByte.length + i];
      System.arraycopy(arrayOfByte, 0, arrayOfByte1, 0, arrayOfByte.length);
      for (int j = arrayOfByte.length; j < arrayOfByte1.length; j++)
        arrayOfByte1[j] = 0; 
      arrayOfByte = arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  private String translateBytesToString(byte[] paramArrayOfByte, long paramLong, Transferable paramTransferable) throws IOException {
    int i;
    Long long = Long.valueOf(paramLong);
    String str1 = getBestCharsetForTextFormat(long, paramTransferable);
    String str2 = (String)nativeEOLNs.get(long);
    Integer integer = (Integer)nativeTerminators.get(long);
    if (integer != null) {
      int j = integer.intValue();
      i = 0;
      while (i < paramArrayOfByte.length - j + 1) {
        for (byte b = i; b < i + j; b++) {
          if (paramArrayOfByte[b] != 0) {
            i += j;
            continue;
          } 
        } 
      } 
    } else {
      i = paramArrayOfByte.length;
    } 
    String str3 = new String(paramArrayOfByte, 0, i, str1);
    if (str2 != null) {
      char[] arrayOfChar1 = str3.toCharArray();
      char[] arrayOfChar2 = str2.toCharArray();
      str3 = null;
      byte b = 0;
      int j = 0;
      while (j < arrayOfChar1.length) {
        if (j + arrayOfChar2.length > arrayOfChar1.length) {
          arrayOfChar1[b++] = arrayOfChar1[j++];
          continue;
        } 
        boolean bool = true;
        byte b1 = 0;
        for (int k = j; b1 < arrayOfChar2.length; k++) {
          if (arrayOfChar2[b1] != arrayOfChar1[k]) {
            bool = false;
            break;
          } 
          b1++;
        } 
        if (bool) {
          arrayOfChar1[b++] = '\n';
          j += arrayOfChar2.length;
          continue;
        } 
        arrayOfChar1[b++] = arrayOfChar1[j++];
      } 
      str3 = new String(arrayOfChar1, 0, b);
    } 
    return str3;
  }
  
  public byte[] translateTransferable(Transferable paramTransferable, DataFlavor paramDataFlavor, long paramLong) throws IOException {
    boolean bool;
    Object object;
    try {
      object = paramTransferable.getTransferData(paramDataFlavor);
      if (object == null)
        return null; 
      if (paramDataFlavor.equals(DataFlavor.plainTextFlavor) && !(object instanceof InputStream)) {
        object = paramTransferable.getTransferData(DataFlavor.stringFlavor);
        if (object == null)
          return null; 
        bool = true;
      } else {
        bool = false;
      } 
    } catch (UnsupportedFlavorException unsupportedFlavorException) {
      throw new IOException(unsupportedFlavorException.getMessage());
    } 
    if (bool || (String.class.equals(paramDataFlavor.getRepresentationClass()) && isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong))) {
      String str = removeSuspectedData(paramDataFlavor, paramTransferable, (String)object);
      return translateTransferableString(str, paramLong);
    } 
    if (paramDataFlavor.isRepresentationClassReader()) {
      if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
        throw new IOException("cannot transfer non-text data as Reader"); 
      StringBuffer stringBuffer = new StringBuffer();
      try (Reader null = (Reader)object) {
        while ((i = reader.read()) != -1)
          stringBuffer.append((char)i); 
      } 
      return translateTransferableString(stringBuffer.toString(), paramLong);
    } 
    if (paramDataFlavor.isRepresentationClassCharBuffer()) {
      if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
        throw new IOException("cannot transfer non-text data as CharBuffer"); 
      CharBuffer charBuffer = (CharBuffer)object;
      int i = charBuffer.remaining();
      char[] arrayOfChar = new char[i];
      charBuffer.get(arrayOfChar, 0, i);
      return translateTransferableString(new String(arrayOfChar), paramLong);
    } 
    if (char[].class.equals(paramDataFlavor.getRepresentationClass())) {
      if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
        throw new IOException("cannot transfer non-text data as char array"); 
      return translateTransferableString(new String((char[])object), paramLong);
    } 
    if (paramDataFlavor.isRepresentationClassByteBuffer()) {
      ByteBuffer byteBuffer = (ByteBuffer)object;
      int i = byteBuffer.remaining();
      byte[] arrayOfByte1 = new byte[i];
      byteBuffer.get(arrayOfByte1, 0, i);
      if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
        String str = getTextCharset(paramDataFlavor);
        return translateTransferableString(new String(arrayOfByte1, str), paramLong);
      } 
      return arrayOfByte1;
    } 
    if (byte[].class.equals(paramDataFlavor.getRepresentationClass())) {
      byte[] arrayOfByte1 = (byte[])object;
      if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
        String str = getTextCharset(paramDataFlavor);
        return translateTransferableString(new String(arrayOfByte1, str), paramLong);
      } 
      return arrayOfByte1;
    } 
    if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
      if (!isImageFormat(paramLong))
        throw new IOException("Data translation failed: not an image format"); 
      Image image = (Image)object;
      byte[] arrayOfByte1 = imageToPlatformBytes(image, paramLong);
      if (arrayOfByte1 == null)
        throw new IOException("Data translation failed: cannot convert java image to native format"); 
      return arrayOfByte1;
    } 
    byte[] arrayOfByte = null;
    if (isFileFormat(paramLong)) {
      if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor))
        throw new IOException("data translation failed"); 
      List list = (List)object;
      ProtectionDomain protectionDomain = getUserProtectionDomain(paramTransferable);
      ArrayList arrayList = castToFiles(list, protectionDomain);
      try (ByteArrayOutputStream null = convertFileListToBytes(arrayList)) {
        arrayOfByte = byteArrayOutputStream.toByteArray();
      } 
    } else if (isURIListFormat(paramLong)) {
      if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor))
        throw new IOException("data translation failed"); 
      String str1 = getNativeForFormat(paramLong);
      String str2 = null;
      if (str1 != null)
        try {
          str2 = (new DataFlavor(str1)).getParameter("charset");
        } catch (ClassNotFoundException classNotFoundException) {
          throw new IOException(classNotFoundException);
        }  
      if (str2 == null)
        str2 = "UTF-8"; 
      List list = (List)object;
      ProtectionDomain protectionDomain = getUserProtectionDomain(paramTransferable);
      ArrayList arrayList1 = castToFiles(list, protectionDomain);
      ArrayList arrayList2 = new ArrayList(arrayList1.size());
      for (String str : arrayList1) {
        URI uRI = (new File(str)).toURI();
        try {
          arrayList2.add((new URI(uRI.getScheme(), "", uRI.getPath(), uRI.getFragment())).toString());
        } catch (URISyntaxException uRISyntaxException) {
          throw new IOException(uRISyntaxException);
        } 
      } 
      byte[] arrayOfByte1 = "\r\n".getBytes(str2);
      try (ByteArrayOutputStream null = new ByteArrayOutputStream()) {
        for (b = 0; b < arrayList2.size(); b++) {
          byte[] arrayOfByte2 = ((String)arrayList2.get(b)).getBytes(str2);
          byteArrayOutputStream.write(arrayOfByte2, 0, arrayOfByte2.length);
          byteArrayOutputStream.write(arrayOfByte1, 0, arrayOfByte1.length);
        } 
        arrayOfByte = byteArrayOutputStream.toByteArray();
      } 
    } else if (paramDataFlavor.isRepresentationClassInputStream()) {
      if (!(object instanceof InputStream))
        return new byte[0]; 
      try (ByteArrayOutputStream null = new ByteArrayOutputStream()) {
        try (InputStream null = (InputStream)object) {
          bool1 = false;
          int i = inputStream.available();
          byte[] arrayOfByte1 = new byte[(i > 8192) ? i : 8192];
          do {
            int j;
            if (bool1 = ((j = inputStream.read(arrayOfByte1, 0, arrayOfByte1.length)) == -1) ? 1 : 0)
              continue; 
            byteArrayOutputStream.write(arrayOfByte1, 0, j);
          } while (!bool1);
        } 
        if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
          byte[] arrayOfByte1 = byteArrayOutputStream.toByteArray();
          String str = getTextCharset(paramDataFlavor);
          return translateTransferableString(new String(arrayOfByte1, str), paramLong);
        } 
        arrayOfByte = byteArrayOutputStream.toByteArray();
      } 
    } else if (paramDataFlavor.isRepresentationClassRemote()) {
      Object object1 = RMI.newMarshalledObject(object);
      arrayOfByte = convertObjectToBytes(object1);
    } else if (paramDataFlavor.isRepresentationClassSerializable()) {
      arrayOfByte = convertObjectToBytes(object);
    } else {
      throw new IOException("data translation failed");
    } 
    return arrayOfByte;
  }
  
  private static byte[] convertObjectToBytes(Object paramObject) throws IOException {
    try(ByteArrayOutputStream null = new ByteArrayOutputStream(); ObjectOutputStream null = new ObjectOutputStream(byteArrayOutputStream)) {
      objectOutputStream.writeObject(paramObject);
      return byteArrayOutputStream.toByteArray();
    } 
  }
  
  protected abstract ByteArrayOutputStream convertFileListToBytes(ArrayList<String> paramArrayList) throws IOException;
  
  private String removeSuspectedData(DataFlavor paramDataFlavor, Transferable paramTransferable, final String str) throws IOException {
    if (null == System.getSecurityManager() || !paramDataFlavor.isMimeTypeEqual("text/uri-list"))
      return paramString; 
    String str = "";
    final ProtectionDomain userProtectionDomain = getUserProtectionDomain(paramTransferable);
    try {
      str = (String)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() {
              StringBuffer stringBuffer = new StringBuffer(str.length());
              String[] arrayOfString = str.split("(\\s)+");
              for (String str : arrayOfString) {
                File file = new File(str);
                if (file.exists() && !DataTransferer.isFileInWebstartedCache(file) && !DataTransferer.this.isForbiddenToRead(file, userProtectionDomain)) {
                  if (0 != stringBuffer.length())
                    stringBuffer.append("\\r\\n"); 
                  stringBuffer.append(str);
                } 
              } 
              return stringBuffer.toString();
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new IOException(privilegedActionException.getMessage(), privilegedActionException);
    } 
    return str;
  }
  
  private static ProtectionDomain getUserProtectionDomain(Transferable paramTransferable) { return paramTransferable.getClass().getProtectionDomain(); }
  
  private boolean isForbiddenToRead(File paramFile, ProtectionDomain paramProtectionDomain) {
    if (null == paramProtectionDomain)
      return false; 
    try {
      FilePermission filePermission = new FilePermission(paramFile.getCanonicalPath(), "read, delete");
      if (paramProtectionDomain.implies(filePermission))
        return false; 
    } catch (IOException iOException) {}
    return true;
  }
  
  private ArrayList<String> castToFiles(final List files, final ProtectionDomain userProtectionDomain) throws IOException {
    final ArrayList fileList = new ArrayList();
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() {
              for (Object object : files) {
                File file = DataTransferer.this.castToFile(object);
                if (file != null && (null == System.getSecurityManager() || (!DataTransferer.isFileInWebstartedCache(file) && !DataTransferer.this.isForbiddenToRead(file, userProtectionDomain))))
                  fileList.add(file.getCanonicalPath()); 
              } 
              return null;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      throw new IOException(privilegedActionException.getMessage());
    } 
    return arrayList;
  }
  
  private File castToFile(Object paramObject) throws IOException {
    String str = null;
    if (paramObject instanceof File) {
      str = ((File)paramObject).getCanonicalPath();
    } else if (paramObject instanceof String) {
      str = (String)paramObject;
    } else {
      return null;
    } 
    return new File(str);
  }
  
  private static boolean isFileInWebstartedCache(File paramFile) {
    if (deploymentCacheDirectoryList.isEmpty())
      for (String str1 : DEPLOYMENT_CACHE_PROPERTIES) {
        String str2 = System.getProperty(str1);
        if (str2 != null)
          try {
            File file = (new File(str2)).getCanonicalFile();
            if (file != null)
              deploymentCacheDirectoryList.add(file); 
          } catch (IOException iOException) {} 
      }  
    for (File file1 : deploymentCacheDirectoryList) {
      for (File file2 = paramFile; file2 != null; file2 = file2.getParentFile()) {
        if (file2.equals(file1))
          return true; 
      } 
    } 
    return false;
  }
  
  public Object translateBytes(byte[] paramArrayOfByte, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable) throws IOException {
    Image image = null;
    if (isFileFormat(paramLong)) {
      if (!DataFlavor.javaFileListFlavor.equals(paramDataFlavor))
        throw new IOException("data translation failed"); 
      String[] arrayOfString = dragQueryFile(paramArrayOfByte);
      if (arrayOfString == null)
        return null; 
      File[] arrayOfFile = new File[arrayOfString.length];
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfFile[b] = new File(arrayOfString[b]); 
      image = Arrays.asList(arrayOfFile);
    } else if (isURIListFormat(paramLong) && DataFlavor.javaFileListFlavor.equals(paramDataFlavor)) {
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfByte)) {
        arrayOfURI = dragQueryURIs(byteArrayInputStream, paramLong, paramTransferable);
        if (arrayOfURI == null)
          return null; 
        ArrayList arrayList = new ArrayList();
        for (URI uRI : arrayOfURI) {
          try {
            arrayList.add(new File(uRI));
          } catch (IllegalArgumentException illegalArgumentException) {}
        } 
        image = arrayList;
      } 
    } else if (String.class.equals(paramDataFlavor.getRepresentationClass()) && isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
      String str = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable);
    } else if (paramDataFlavor.isRepresentationClassReader()) {
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfByte)) {
        Object object = translateStream(byteArrayInputStream, paramDataFlavor, paramLong, paramTransferable);
      } 
    } else if (paramDataFlavor.isRepresentationClassCharBuffer()) {
      if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
        throw new IOException("cannot transfer non-text data as CharBuffer"); 
      CharBuffer charBuffer = CharBuffer.wrap(translateBytesToString(paramArrayOfByte, paramLong, paramTransferable));
      Object object = constructFlavoredObject(charBuffer, paramDataFlavor, CharBuffer.class);
    } else if (char[].class.equals(paramDataFlavor.getRepresentationClass())) {
      if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
        throw new IOException("cannot transfer non-text data as char array"); 
      char[] arrayOfChar = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).toCharArray();
    } else if (paramDataFlavor.isRepresentationClassByteBuffer()) {
      if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong))
        paramArrayOfByte = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor)); 
      ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfByte);
      Object object = constructFlavoredObject(byteBuffer, paramDataFlavor, ByteBuffer.class);
    } else if (byte[].class.equals(paramDataFlavor.getRepresentationClass())) {
      if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
        byte[] arrayOfByte = translateBytesToString(paramArrayOfByte, paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
      } else {
        byte[] arrayOfByte = paramArrayOfByte;
      } 
    } else if (paramDataFlavor.isRepresentationClassInputStream()) {
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfByte)) {
        Object object = translateStream(byteArrayInputStream, paramDataFlavor, paramLong, paramTransferable);
      } 
    } else if (paramDataFlavor.isRepresentationClassRemote()) {
      try(ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfByte); ObjectInputStream null = new ObjectInputStream(byteArrayInputStream)) {
        Object object = RMI.getMarshalledObject(objectInputStream.readObject());
      } catch (Exception exception) {
        throw new IOException(exception.getMessage());
      } 
    } else if (paramDataFlavor.isRepresentationClassSerializable()) {
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfByte)) {
        Object object = translateStream(byteArrayInputStream, paramDataFlavor, paramLong, paramTransferable);
      } 
    } else if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
      if (!isImageFormat(paramLong))
        throw new IOException("data translation failed"); 
      image = platformImageBytesToImage(paramArrayOfByte, paramLong);
    } 
    if (image == null)
      throw new IOException("data translation failed"); 
    return image;
  }
  
  public Object translateStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable) throws IOException {
    Image image = null;
    if (isURIListFormat(paramLong) && DataFlavor.javaFileListFlavor.equals(paramDataFlavor)) {
      URI[] arrayOfURI = dragQueryURIs(paramInputStream, paramLong, paramTransferable);
      if (arrayOfURI == null)
        return null; 
      ArrayList arrayList = new ArrayList();
      for (URI uRI : arrayOfURI) {
        try {
          arrayList.add(new File(uRI));
        } catch (IllegalArgumentException illegalArgumentException) {}
      } 
      image = arrayList;
    } else {
      if (String.class.equals(paramDataFlavor.getRepresentationClass()) && isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong))
        return translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable); 
      if (DataFlavor.plainTextFlavor.equals(paramDataFlavor)) {
        StringReader stringReader = new StringReader(translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable));
      } else if (paramDataFlavor.isRepresentationClassInputStream()) {
        Object object = translateStreamToInputStream(paramInputStream, paramDataFlavor, paramLong, paramTransferable);
      } else if (paramDataFlavor.isRepresentationClassReader()) {
        if (!isFlavorCharsetTextType(paramDataFlavor) || !isTextFormat(paramLong))
          throw new IOException("cannot transfer non-text data as Reader"); 
        InputStream inputStream = (InputStream)translateStreamToInputStream(paramInputStream, DataFlavor.plainTextFlavor, paramLong, paramTransferable);
        String str = getTextCharset(DataFlavor.plainTextFlavor);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, str);
        Object object = constructFlavoredObject(inputStreamReader, paramDataFlavor, Reader.class);
      } else if (byte[].class.equals(paramDataFlavor.getRepresentationClass())) {
        if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong)) {
          byte[] arrayOfByte = translateBytesToString(inputStreamToByteArray(paramInputStream), paramLong, paramTransferable).getBytes(getTextCharset(paramDataFlavor));
        } else {
          byte[] arrayOfByte = inputStreamToByteArray(paramInputStream);
        } 
      } else if (paramDataFlavor.isRepresentationClassRemote()) {
        try (ObjectInputStream null = new ObjectInputStream(paramInputStream)) {
          Object object = RMI.getMarshalledObject(objectInputStream.readObject());
        } catch (Exception exception) {
          throw new IOException(exception.getMessage());
        } 
      } else if (paramDataFlavor.isRepresentationClassSerializable()) {
        try (ObjectInputStream null = new ObjectInputStream(paramInputStream)) {
          Object object = objectInputStream.readObject();
        } catch (Exception exception) {
          throw new IOException(exception.getMessage());
        } 
      } else if (DataFlavor.imageFlavor.equals(paramDataFlavor)) {
        if (!isImageFormat(paramLong))
          throw new IOException("data translation failed"); 
        image = platformImageBytesToImage(inputStreamToByteArray(paramInputStream), paramLong);
      } 
    } 
    if (image == null)
      throw new IOException("data translation failed"); 
    return image;
  }
  
  private Object translateStreamToInputStream(InputStream paramInputStream, DataFlavor paramDataFlavor, long paramLong, Transferable paramTransferable) throws IOException {
    if (isFlavorCharsetTextType(paramDataFlavor) && isTextFormat(paramLong))
      paramInputStream = new ReencodingInputStream(paramInputStream, paramLong, getTextCharset(paramDataFlavor), paramTransferable); 
    return constructFlavoredObject(paramInputStream, paramDataFlavor, InputStream.class);
  }
  
  private Object constructFlavoredObject(Object paramObject, DataFlavor paramDataFlavor, Class paramClass) throws IOException {
    final Class dfrc = paramDataFlavor.getRepresentationClass();
    if (paramClass.equals(clazz))
      return paramObject; 
    Constructor[] arrayOfConstructor = null;
    try {
      arrayOfConstructor = (Constructor[])AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() { return dfrc.getConstructors(); }
          });
    } catch (SecurityException securityException) {
      throw new IOException(securityException.getMessage());
    } 
    Constructor constructor = null;
    for (b = 0; b < arrayOfConstructor.length; b++) {
      if (Modifier.isPublic(arrayOfConstructor[b].getModifiers())) {
        Class[] arrayOfClass = arrayOfConstructor[b].getParameterTypes();
        if (arrayOfClass != null && arrayOfClass.length == 1 && paramClass.equals(arrayOfClass[0])) {
          constructor = arrayOfConstructor[b];
          break;
        } 
      } 
    } 
    if (constructor == null)
      throw new IOException("can't find <init>(L" + paramClass + ";)V for class: " + clazz.getName()); 
    try {
      return constructor.newInstance(new Object[] { paramObject });
    } catch (Exception b) {
      Exception exception;
      throw new IOException(exception.getMessage());
    } 
  }
  
  protected abstract String[] dragQueryFile(byte[] paramArrayOfByte);
  
  protected URI[] dragQueryURIs(InputStream paramInputStream, long paramLong, Transferable paramTransferable) throws IOException { throw new IOException(new UnsupportedOperationException("not implemented on this platform")); }
  
  protected abstract Image platformImageBytesToImage(byte[] paramArrayOfByte, long paramLong) throws IOException;
  
  protected Image standardImageBytesToImage(byte[] paramArrayOfByte, String paramString) throws IOException {
    Iterator iterator = ImageIO.getImageReadersByMIMEType(paramString);
    if (!iterator.hasNext())
      throw new IOException("No registered service provider can decode  an image from " + paramString); 
    IOException iOException = null;
    while (iterator.hasNext()) {
      imageReader = (ImageReader)iterator.next();
      try (ByteArrayInputStream null = new ByteArrayInputStream(paramArrayOfByte)) {
        imageInputStream = ImageIO.createImageInputStream(byteArrayInputStream);
        try {
          ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
          imageReader.setInput(imageInputStream, true, true);
          BufferedImage bufferedImage = imageReader.read(imageReader.getMinIndex(), imageReadParam);
          if (bufferedImage != null)
            return bufferedImage; 
        } finally {
          imageInputStream.close();
          imageReader.dispose();
        } 
      } catch (IOException iOException1) {
        iOException = iOException1;
      } 
    } 
    if (iOException == null)
      iOException = new IOException("Registered service providers failed to decode an image from " + paramString); 
    throw iOException;
  }
  
  protected abstract byte[] imageToPlatformBytes(Image paramImage, long paramLong) throws IOException;
  
  protected byte[] imageToStandardBytes(Image paramImage, String paramString) throws IOException {
    IOException iOException = null;
    Iterator iterator = ImageIO.getImageWritersByMIMEType(paramString);
    if (!iterator.hasNext())
      throw new IOException("No registered service provider can encode  an image to " + paramString); 
    if (paramImage instanceof RenderedImage)
      try {
        return imageToStandardBytesImpl((RenderedImage)paramImage, paramString);
      } catch (IOException iOException1) {
        iOException = iOException1;
      }  
    int i = 0;
    int j = 0;
    if (paramImage instanceof ToolkitImage) {
      ImageRepresentation imageRepresentation = ((ToolkitImage)paramImage).getImageRep();
      imageRepresentation.reconstruct(32);
      i = imageRepresentation.getWidth();
      j = imageRepresentation.getHeight();
    } else {
      i = paramImage.getWidth(null);
      j = paramImage.getHeight(null);
    } 
    ColorModel colorModel = ColorModel.getRGBdefault();
    WritableRaster writableRaster = colorModel.createCompatibleWritableRaster(i, j);
    BufferedImage bufferedImage = new BufferedImage(colorModel, writableRaster, colorModel.isAlphaPremultiplied(), null);
    graphics = bufferedImage.getGraphics();
    try {
      graphics.drawImage(paramImage, 0, 0, i, j, null);
    } finally {
      graphics.dispose();
    } 
    try {
      return imageToStandardBytesImpl(bufferedImage, paramString);
    } catch (IOException iOException1) {
      if (iOException != null)
        throw iOException; 
      throw iOException1;
    } 
  }
  
  protected byte[] imageToStandardBytesImpl(RenderedImage paramRenderedImage, String paramString) throws IOException {
    Iterator iterator = ImageIO.getImageWritersByMIMEType(paramString);
    ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(paramRenderedImage);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    IOException iOException = null;
    while (iterator.hasNext()) {
      ImageWriter imageWriter = (ImageWriter)iterator.next();
      ImageWriterSpi imageWriterSpi = imageWriter.getOriginatingProvider();
      if (!imageWriterSpi.canEncodeImage(imageTypeSpecifier))
        continue; 
      try {
        imageOutputStream = ImageIO.createImageOutputStream(byteArrayOutputStream);
        try {
          imageWriter.setOutput(imageOutputStream);
          imageWriter.write(paramRenderedImage);
          imageOutputStream.flush();
        } finally {
          imageOutputStream.close();
        } 
      } catch (IOException iOException1) {
        imageWriter.dispose();
        byteArrayOutputStream.reset();
        iOException = iOException1;
        continue;
      } 
      imageWriter.dispose();
      byteArrayOutputStream.close();
      return byteArrayOutputStream.toByteArray();
    } 
    byteArrayOutputStream.close();
    if (iOException == null)
      iOException = new IOException("Registered service providers failed to encode " + paramRenderedImage + " to " + paramString); 
    throw iOException;
  }
  
  private Object concatData(Object paramObject1, Object paramObject2) {
    InputStream inputStream1 = null;
    InputStream inputStream2 = null;
    if (paramObject1 instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject1;
      if (paramObject2 instanceof byte[]) {
        byte[] arrayOfByte1 = (byte[])paramObject2;
        byte[] arrayOfByte2 = new byte[arrayOfByte.length + arrayOfByte1.length];
        System.arraycopy(arrayOfByte, 0, arrayOfByte2, 0, arrayOfByte.length);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, arrayOfByte.length, arrayOfByte1.length);
        return arrayOfByte2;
      } 
      inputStream1 = new ByteArrayInputStream(arrayOfByte);
      inputStream2 = (InputStream)paramObject2;
    } else {
      inputStream1 = (InputStream)paramObject1;
      if (paramObject2 instanceof byte[]) {
        inputStream2 = new ByteArrayInputStream((byte[])paramObject2);
      } else {
        inputStream2 = (InputStream)paramObject2;
      } 
    } 
    return new SequenceInputStream(inputStream1, inputStream2);
  }
  
  public byte[] convertData(Object paramObject, final Transferable contents, final long format, final Map formatMap, boolean paramBoolean) throws IOException {
    byte[] arrayOfByte = null;
    if (paramBoolean) {
      try {
        final Stack stack = new Stack();
        Runnable runnable = new Runnable() {
            private boolean done = false;
            
            public void run() {
              if (this.done)
                return; 
              byte[] arrayOfByte = null;
              try {
                DataFlavor dataFlavor = (DataFlavor)formatMap.get(Long.valueOf(format));
                if (dataFlavor != null)
                  arrayOfByte = DataTransferer.this.translateTransferable(contents, dataFlavor, format); 
              } catch (Exception exception) {
                exception.printStackTrace();
                arrayOfByte = null;
              } 
              try {
                DataTransferer.this.getToolkitThreadBlockedHandler().lock();
                stack.push(arrayOfByte);
                DataTransferer.this.getToolkitThreadBlockedHandler().exit();
              } finally {
                DataTransferer.this.getToolkitThreadBlockedHandler().unlock();
                this.done = true;
              } 
            }
          };
        AppContext appContext = SunToolkit.targetToAppContext(paramObject);
        getToolkitThreadBlockedHandler().lock();
        if (appContext != null)
          appContext.put("DATA_CONVERTER_KEY", runnable); 
        SunToolkit.executeOnEventHandlerThread(paramObject, runnable);
        while (stack.empty())
          getToolkitThreadBlockedHandler().enter(); 
        if (appContext != null)
          appContext.remove("DATA_CONVERTER_KEY"); 
        arrayOfByte = (byte[])stack.pop();
      } finally {
        getToolkitThreadBlockedHandler().unlock();
      } 
    } else {
      DataFlavor dataFlavor = (DataFlavor)paramMap.get(Long.valueOf(paramLong));
      if (dataFlavor != null)
        arrayOfByte = translateTransferable(paramTransferable, dataFlavor, paramLong); 
    } 
    return arrayOfByte;
  }
  
  public void processDataConversionRequests() {
    if (EventQueue.isDispatchThread()) {
      AppContext appContext = AppContext.getAppContext();
      getToolkitThreadBlockedHandler().lock();
      try {
        Runnable runnable = (Runnable)appContext.get("DATA_CONVERTER_KEY");
        if (runnable != null) {
          runnable.run();
          appContext.remove("DATA_CONVERTER_KEY");
        } 
      } finally {
        getToolkitThreadBlockedHandler().unlock();
      } 
    } 
  }
  
  public abstract ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler();
  
  public static long[] keysToLongArray(SortedMap paramSortedMap) {
    Set set = paramSortedMap.keySet();
    long[] arrayOfLong = new long[set.size()];
    byte b = 0;
    Iterator iterator = set.iterator();
    while (iterator.hasNext()) {
      arrayOfLong[b] = ((Long)iterator.next()).longValue();
      b++;
    } 
    return arrayOfLong;
  }
  
  public static DataFlavor[] setToSortedDataFlavorArray(Set paramSet) {
    DataFlavor[] arrayOfDataFlavor = new DataFlavor[paramSet.size()];
    paramSet.toArray(arrayOfDataFlavor);
    DataFlavorComparator dataFlavorComparator = new DataFlavorComparator(false);
    Arrays.sort(arrayOfDataFlavor, dataFlavorComparator);
    return arrayOfDataFlavor;
  }
  
  protected static byte[] inputStreamToByteArray(InputStream paramInputStream) throws IOException {
    try (ByteArrayOutputStream null = new ByteArrayOutputStream()) {
      int i = 0;
      byte[] arrayOfByte = new byte[8192];
      while ((i = paramInputStream.read(arrayOfByte)) != -1)
        byteArrayOutputStream.write(arrayOfByte, 0, i); 
      return byteArrayOutputStream.toByteArray();
    } 
  }
  
  public LinkedHashSet<DataFlavor> getPlatformMappingsForNative(String paramString) { return new LinkedHashSet(); }
  
  public LinkedHashSet<String> getPlatformMappingsForFlavor(DataFlavor paramDataFlavor) { return new LinkedHashSet(); }
  
  static  {
    DataFlavor dataFlavor1 = null;
    try {
      dataFlavor1 = new DataFlavor("text/plain;charset=Unicode;class=java.lang.String");
    } catch (ClassNotFoundException classNotFoundException) {}
    plainTextStringFlavor = dataFlavor1;
    DataFlavor dataFlavor2 = null;
    try {
      dataFlavor2 = new DataFlavor("application/x-java-text-encoding;class=\"[B\"");
    } catch (ClassNotFoundException classNotFoundException) {}
    javaTextEncodingFlavor = dataFlavor2;
    HashMap hashMap = new HashMap(17);
    hashMap.put("sgml", Boolean.TRUE);
    hashMap.put("xml", Boolean.TRUE);
    hashMap.put("html", Boolean.TRUE);
    hashMap.put("enriched", Boolean.TRUE);
    hashMap.put("richtext", Boolean.TRUE);
    hashMap.put("uri-list", Boolean.TRUE);
    hashMap.put("directory", Boolean.TRUE);
    hashMap.put("css", Boolean.TRUE);
    hashMap.put("calendar", Boolean.TRUE);
    hashMap.put("plain", Boolean.TRUE);
    hashMap.put("rtf", Boolean.FALSE);
    hashMap.put("tab-separated-values", Boolean.FALSE);
    hashMap.put("t140", Boolean.FALSE);
    hashMap.put("rfc822-headers", Boolean.FALSE);
    hashMap.put("parityfec", Boolean.FALSE);
    textMIMESubtypeCharsetSupport = Collections.synchronizedMap(hashMap);
    DEPLOYMENT_CACHE_PROPERTIES = new String[] { "deployment.system.cachedir", "deployment.user.cachedir", "deployment.javaws.cachedir", "deployment.javapi.cachedir" };
    deploymentCacheDirectoryList = new ArrayList();
  }
  
  public static class CharsetComparator extends IndexedComparator {
    private static final Map charsets;
    
    private static String defaultEncoding;
    
    private static final Integer DEFAULT_CHARSET_INDEX;
    
    private static final Integer OTHER_CHARSET_INDEX;
    
    private static final Integer WORST_CHARSET_INDEX;
    
    private static final Integer UNSUPPORTED_CHARSET_INDEX = (WORST_CHARSET_INDEX = (OTHER_CHARSET_INDEX = (DEFAULT_CHARSET_INDEX = Integer.valueOf(2)).valueOf(1)).valueOf(0)).valueOf(-2147483648);
    
    private static final String UNSUPPORTED_CHARSET = "UNSUPPORTED";
    
    public CharsetComparator() { this(true); }
    
    public CharsetComparator(boolean param1Boolean) { super(param1Boolean); }
    
    public int compare(Object param1Object1, Object param1Object2) {
      String str1 = null;
      String str2 = null;
      if (this.order == true) {
        str1 = (String)param1Object1;
        str2 = (String)param1Object2;
      } else {
        str1 = (String)param1Object2;
        str2 = (String)param1Object1;
      } 
      return compareCharsets(str1, str2);
    }
    
    protected int compareCharsets(String param1String1, String param1String2) {
      param1String1 = getEncoding(param1String1);
      param1String2 = getEncoding(param1String2);
      int i = compareIndices(charsets, param1String1, param1String2, OTHER_CHARSET_INDEX);
      return (i == 0) ? param1String2.compareTo(param1String1) : i;
    }
    
    protected static String getEncoding(String param1String) {
      if (param1String == null)
        return null; 
      if (!DataTransferer.isEncodingSupported(param1String))
        return "UNSUPPORTED"; 
      String str = DataTransferer.canonicalName(param1String);
      return charsets.containsKey(str) ? str : param1String;
    }
    
    static  {
      HashMap hashMap = new HashMap(8, 1.0F);
      hashMap.put(DataTransferer.canonicalName("UTF-16LE"), Integer.valueOf(4));
      hashMap.put(DataTransferer.canonicalName("UTF-16BE"), Integer.valueOf(5));
      hashMap.put(DataTransferer.canonicalName("UTF-8"), Integer.valueOf(6));
      hashMap.put(DataTransferer.canonicalName("UTF-16"), Integer.valueOf(7));
      hashMap.put(DataTransferer.canonicalName("US-ASCII"), WORST_CHARSET_INDEX);
      String str = DataTransferer.canonicalName(DataTransferer.getDefaultTextCharset());
      if (hashMap.get(defaultEncoding) == null)
        hashMap.put(defaultEncoding, DEFAULT_CHARSET_INDEX); 
      hashMap.put("UNSUPPORTED", UNSUPPORTED_CHARSET_INDEX);
      charsets = Collections.unmodifiableMap(hashMap);
    }
  }
  
  public static class DataFlavorComparator extends IndexedComparator {
    private final DataTransferer.CharsetComparator charsetComparator;
    
    private static final Map exactTypes;
    
    private static final Map primaryTypes;
    
    private static final Map nonTextRepresentations;
    
    private static final Map textTypes;
    
    private static final Map decodedTextRepresentations;
    
    private static final Map encodedTextRepresentations;
    
    private static final Integer UNKNOWN_OBJECT_LOSES;
    
    private static final Integer UNKNOWN_OBJECT_WINS = (UNKNOWN_OBJECT_LOSES = Integer.valueOf(-2147483648)).valueOf(2147483647);
    
    private static final Long UNKNOWN_OBJECT_LOSES_L;
    
    private static final Long UNKNOWN_OBJECT_WINS_L = (UNKNOWN_OBJECT_LOSES_L = Long.valueOf(Float.MIN_VALUE)).valueOf(Float.MAX_VALUE);
    
    public DataFlavorComparator() { this(true); }
    
    public DataFlavorComparator(boolean param1Boolean) {
      super(param1Boolean);
      this.charsetComparator = new DataTransferer.CharsetComparator(param1Boolean);
    }
    
    public int compare(Object param1Object1, Object param1Object2) {
      DataFlavor dataFlavor1 = null;
      DataFlavor dataFlavor2 = null;
      if (this.order == true) {
        dataFlavor1 = (DataFlavor)param1Object1;
        dataFlavor2 = (DataFlavor)param1Object2;
      } else {
        dataFlavor1 = (DataFlavor)param1Object2;
        dataFlavor2 = (DataFlavor)param1Object1;
      } 
      if (dataFlavor1.equals(dataFlavor2))
        return 0; 
      int i = 0;
      String str1 = dataFlavor1.getPrimaryType();
      String str2 = dataFlavor1.getSubType();
      String str3 = str1 + "/" + str2;
      Class clazz1 = dataFlavor1.getRepresentationClass();
      String str4 = dataFlavor2.getPrimaryType();
      String str5 = dataFlavor2.getSubType();
      String str6 = str4 + "/" + str5;
      Class clazz2 = dataFlavor2.getRepresentationClass();
      if (dataFlavor1.isFlavorTextType() && dataFlavor2.isFlavorTextType()) {
        i = compareIndices(textTypes, str3, str6, UNKNOWN_OBJECT_LOSES);
        if (i != 0)
          return i; 
        if (DataTransferer.doesSubtypeSupportCharset(dataFlavor1)) {
          i = compareIndices(decodedTextRepresentations, clazz1, clazz2, UNKNOWN_OBJECT_LOSES);
          if (i != 0)
            return i; 
          i = this.charsetComparator.compareCharsets(DataTransferer.getTextCharset(dataFlavor1), DataTransferer.getTextCharset(dataFlavor2));
          if (i != 0)
            return i; 
        } 
        i = compareIndices(encodedTextRepresentations, clazz1, clazz2, UNKNOWN_OBJECT_LOSES);
        if (i != 0)
          return i; 
      } else {
        if (dataFlavor1.isFlavorTextType())
          return 1; 
        if (dataFlavor2.isFlavorTextType())
          return -1; 
        i = compareIndices(primaryTypes, str1, str4, UNKNOWN_OBJECT_LOSES);
        if (i != 0)
          return i; 
        i = compareIndices(exactTypes, str3, str6, UNKNOWN_OBJECT_WINS);
        if (i != 0)
          return i; 
        i = compareIndices(nonTextRepresentations, clazz1, clazz2, UNKNOWN_OBJECT_LOSES);
        if (i != 0)
          return i; 
      } 
      return dataFlavor1.getMimeType().compareTo(dataFlavor2.getMimeType());
    }
    
    static  {
      HashMap hashMap = new HashMap(4, 1.0F);
      hashMap.put("application/x-java-file-list", Integer.valueOf(0));
      hashMap.put("application/x-java-serialized-object", Integer.valueOf(1));
      hashMap.put("application/x-java-jvm-local-objectref", Integer.valueOf(2));
      hashMap.put("application/x-java-remote-object", Integer.valueOf(3));
      exactTypes = Collections.unmodifiableMap(hashMap);
      hashMap = new HashMap(1, 1.0F);
      hashMap.put("application", Integer.valueOf(0));
      primaryTypes = Collections.unmodifiableMap(hashMap);
      hashMap = new HashMap(3, 1.0F);
      hashMap.put(InputStream.class, Integer.valueOf(0));
      hashMap.put(java.io.Serializable.class, Integer.valueOf(1));
      Class clazz = DataTransferer.RMI.remoteClass();
      if (clazz != null)
        hashMap.put(clazz, Integer.valueOf(2)); 
      nonTextRepresentations = Collections.unmodifiableMap(hashMap);
      hashMap = new HashMap(16, 1.0F);
      hashMap.put("text/plain", Integer.valueOf(0));
      hashMap.put("application/x-java-serialized-object", Integer.valueOf(1));
      hashMap.put("text/calendar", Integer.valueOf(2));
      hashMap.put("text/css", Integer.valueOf(3));
      hashMap.put("text/directory", Integer.valueOf(4));
      hashMap.put("text/parityfec", Integer.valueOf(5));
      hashMap.put("text/rfc822-headers", Integer.valueOf(6));
      hashMap.put("text/t140", Integer.valueOf(7));
      hashMap.put("text/tab-separated-values", Integer.valueOf(8));
      hashMap.put("text/uri-list", Integer.valueOf(9));
      hashMap.put("text/richtext", Integer.valueOf(10));
      hashMap.put("text/enriched", Integer.valueOf(11));
      hashMap.put("text/rtf", Integer.valueOf(12));
      hashMap.put("text/html", Integer.valueOf(13));
      hashMap.put("text/xml", Integer.valueOf(14));
      hashMap.put("text/sgml", Integer.valueOf(15));
      textTypes = Collections.unmodifiableMap(hashMap);
      hashMap = new HashMap(4, 1.0F);
      hashMap.put(char[].class, Integer.valueOf(0));
      hashMap.put(CharBuffer.class, Integer.valueOf(1));
      hashMap.put(String.class, Integer.valueOf(2));
      hashMap.put(Reader.class, Integer.valueOf(3));
      decodedTextRepresentations = Collections.unmodifiableMap(hashMap);
      hashMap = new HashMap(3, 1.0F);
      hashMap.put(byte[].class, Integer.valueOf(0));
      hashMap.put(ByteBuffer.class, Integer.valueOf(1));
      hashMap.put(InputStream.class, Integer.valueOf(2));
      encodedTextRepresentations = Collections.unmodifiableMap(hashMap);
    }
  }
  
  public static class IndexOrderComparator extends IndexedComparator {
    private final Map indexMap;
    
    private static final Integer FALLBACK_INDEX = Integer.valueOf(-2147483648);
    
    public IndexOrderComparator(Map param1Map) {
      super(true);
      this.indexMap = param1Map;
    }
    
    public IndexOrderComparator(Map param1Map, boolean param1Boolean) {
      super(param1Boolean);
      this.indexMap = param1Map;
    }
    
    public int compare(Object param1Object1, Object param1Object2) { return !this.order ? -compareIndices(this.indexMap, param1Object1, param1Object2, FALLBACK_INDEX) : compareIndices(this.indexMap, param1Object1, param1Object2, FALLBACK_INDEX); }
  }
  
  public static abstract class IndexedComparator implements Comparator {
    public static final boolean SELECT_BEST = true;
    
    public static final boolean SELECT_WORST = false;
    
    protected final boolean order;
    
    public IndexedComparator() { this(true); }
    
    public IndexedComparator(boolean param1Boolean) { this.order = param1Boolean; }
    
    protected static int compareIndices(Map param1Map, Object param1Object1, Object param1Object2, Integer param1Integer) {
      Integer integer1 = (Integer)param1Map.get(param1Object1);
      Integer integer2 = (Integer)param1Map.get(param1Object2);
      if (integer1 == null)
        integer1 = param1Integer; 
      if (integer2 == null)
        integer2 = param1Integer; 
      return integer1.compareTo(integer2);
    }
    
    protected static int compareLongs(Map param1Map, Object param1Object1, Object param1Object2, Long param1Long) {
      Long long1 = (Long)param1Map.get(param1Object1);
      Long long2 = (Long)param1Map.get(param1Object2);
      if (long1 == null)
        long1 = param1Long; 
      if (long2 == null)
        long2 = param1Long; 
      return long1.compareTo(long2);
    }
  }
  
  private static class RMI {
    private static final Class<?> remoteClass = getClass("java.rmi.Remote");
    
    private static final Class<?> marshallObjectClass = getClass("java.rmi.MarshalledObject");
    
    private static final Constructor<?> marshallCtor = getConstructor(marshallObjectClass, new Class[] { Object.class });
    
    private static final Method marshallGet = getMethod(marshallObjectClass, "get", new Class[0]);
    
    private static Class<?> getClass(String param1String) {
      try {
        return Class.forName(param1String, true, null);
      } catch (ClassNotFoundException classNotFoundException) {
        return null;
      } 
    }
    
    private static Constructor<?> getConstructor(Class<?> param1Class, Class<?>... param1VarArgs) {
      try {
        return (param1Class == null) ? null : param1Class.getDeclaredConstructor(param1VarArgs);
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new AssertionError(noSuchMethodException);
      } 
    }
    
    private static Method getMethod(Class<?> param1Class, String param1String, Class<?>... param1VarArgs) {
      try {
        return (param1Class == null) ? null : param1Class.getMethod(param1String, param1VarArgs);
      } catch (NoSuchMethodException noSuchMethodException) {
        throw new AssertionError(noSuchMethodException);
      } 
    }
    
    static boolean isRemote(Class<?> param1Class) { return ((remoteClass == null) ? null : Boolean.valueOf(remoteClass.isAssignableFrom(param1Class))).booleanValue(); }
    
    static Class<?> remoteClass() { return remoteClass; }
    
    static Object newMarshalledObject(Object param1Object) throws IOException {
      try {
        return marshallCtor.newInstance(new Object[] { param1Object });
      } catch (InstantiationException instantiationException) {
        throw new AssertionError(instantiationException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof IOException)
          throw (IOException)throwable; 
        throw new AssertionError(invocationTargetException);
      } 
    }
    
    static Object getMarshalledObject(Object param1Object) throws IOException {
      try {
        return marshallGet.invoke(param1Object, new Object[0]);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof IOException)
          throw (IOException)throwable; 
        if (throwable instanceof ClassNotFoundException)
          throw (ClassNotFoundException)throwable; 
        throw new AssertionError(invocationTargetException);
      } 
    }
  }
  
  public class ReencodingInputStream extends InputStream {
    protected BufferedReader wrapped;
    
    protected final char[] in = new char[2];
    
    protected byte[] out;
    
    protected CharsetEncoder encoder;
    
    protected CharBuffer inBuf;
    
    protected ByteBuffer outBuf;
    
    protected char[] eoln;
    
    protected int numTerminators;
    
    protected boolean eos;
    
    protected int index;
    
    protected int limit;
    
    public ReencodingInputStream(InputStream param1InputStream, long param1Long, String param1String, Transferable param1Transferable) throws IOException {
      Long long = Long.valueOf(param1Long);
      String str1 = null;
      if (this$0.isLocaleDependentTextFormat(param1Long) && param1Transferable != null && param1Transferable.isDataFlavorSupported(DataTransferer.javaTextEncodingFlavor)) {
        try {
          str1 = new String((byte[])param1Transferable.getTransferData(DataTransferer.javaTextEncodingFlavor), "UTF-8");
        } catch (UnsupportedFlavorException unsupportedFlavorException) {}
      } else {
        str1 = this$0.getCharsetForTextFormat(long);
      } 
      if (str1 == null)
        str1 = DataTransferer.getDefaultTextCharset(); 
      this.wrapped = new BufferedReader(new InputStreamReader(param1InputStream, str1));
      if (param1String == null)
        throw new NullPointerException("null target encoding"); 
      try {
        this.encoder = Charset.forName(param1String).newEncoder();
        this.out = new byte[(int)((this.encoder.maxBytesPerChar() * 2.0F) + 0.5D)];
        this.inBuf = CharBuffer.wrap(this.in);
        this.outBuf = ByteBuffer.wrap(this.out);
      } catch (IllegalCharsetNameException illegalCharsetNameException) {
        throw new IOException(illegalCharsetNameException.toString());
      } catch (UnsupportedCharsetException unsupportedCharsetException) {
        throw new IOException(unsupportedCharsetException.toString());
      } catch (UnsupportedOperationException unsupportedOperationException) {
        throw new IOException(unsupportedOperationException.toString());
      } 
      String str2 = (String)nativeEOLNs.get(long);
      if (str2 != null)
        this.eoln = str2.toCharArray(); 
      Integer integer = (Integer)nativeTerminators.get(long);
      if (integer != null)
        this.numTerminators = integer.intValue(); 
    }
    
    private int readChar() throws IOException {
      int i = this.wrapped.read();
      if (i == -1) {
        this.eos = true;
        return -1;
      } 
      if (this.numTerminators > 0 && i == 0) {
        this.eos = true;
        return -1;
      } 
      if (this.eoln != null && matchCharArray(this.eoln, i))
        i = 10; 
      return i;
    }
    
    public int read() throws IOException {
      if (this.eos)
        return -1; 
      if (this.index >= this.limit) {
        int i = readChar();
        if (i == -1)
          return -1; 
        this.in[0] = (char)i;
        this.in[1] = Character.MIN_VALUE;
        this.inBuf.limit(1);
        if (Character.isHighSurrogate((char)i)) {
          i = readChar();
          if (i != -1) {
            this.in[1] = (char)i;
            this.inBuf.limit(2);
          } 
        } 
        this.inBuf.rewind();
        this.outBuf.limit(this.out.length).rewind();
        this.encoder.encode(this.inBuf, this.outBuf, false);
        this.outBuf.flip();
        this.limit = this.outBuf.limit();
        this.index = 0;
        return read();
      } 
      return this.out[this.index++] & 0xFF;
    }
    
    public int available() throws IOException { return this.eos ? 0 : (this.limit - this.index); }
    
    public void close() { this.wrapped.close(); }
    
    private boolean matchCharArray(char[] param1ArrayOfChar, int param1Int) throws IOException {
      this.wrapped.mark(param1ArrayOfChar.length);
      byte b = 0;
      if ((char)param1Int == param1ArrayOfChar[0])
        for (b = 1; b < param1ArrayOfChar.length; b++) {
          param1Int = this.wrapped.read();
          if (param1Int == -1 || (char)param1Int != param1ArrayOfChar[b])
            break; 
        }  
      if (b == param1ArrayOfChar.length)
        return true; 
      this.wrapped.reset();
      return false;
    }
  }
  
  private static class StandardEncodingsHolder {
    private static final SortedSet<String> standardEncodings = load();
    
    private static SortedSet<String> load() {
      DataTransferer.CharsetComparator charsetComparator = new DataTransferer.CharsetComparator(false);
      TreeSet treeSet = new TreeSet(charsetComparator);
      treeSet.add("US-ASCII");
      treeSet.add("ISO-8859-1");
      treeSet.add("UTF-8");
      treeSet.add("UTF-16BE");
      treeSet.add("UTF-16LE");
      treeSet.add("UTF-16");
      treeSet.add(DataTransferer.getDefaultTextCharset());
      return Collections.unmodifiableSortedSet(treeSet);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\datatransfer\DataTransferer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */