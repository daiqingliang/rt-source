package javax.imageio;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ImageOutputStreamSpi;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageReaderWriterSpi;
import javax.imageio.spi.ImageTranscoderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import sun.awt.AppContext;
import sun.security.action.GetPropertyAction;

public final class ImageIO {
  private static final IIORegistry theRegistry = IIORegistry.getDefaultInstance();
  
  private static Method readerFormatNamesMethod;
  
  private static Method readerFileSuffixesMethod;
  
  private static Method readerMIMETypesMethod;
  
  private static Method writerFormatNamesMethod;
  
  private static Method writerFileSuffixesMethod;
  
  private static Method writerMIMETypesMethod;
  
  public static void scanForPlugins() { theRegistry.registerApplicationClasspathSpis(); }
  
  private static CacheInfo getCacheInfo() {
    AppContext appContext = AppContext.getAppContext();
    CacheInfo cacheInfo = (CacheInfo)appContext.get(CacheInfo.class);
    if (cacheInfo == null) {
      cacheInfo = new CacheInfo();
      appContext.put(CacheInfo.class, cacheInfo);
    } 
    return cacheInfo;
  }
  
  private static String getTempDir() {
    GetPropertyAction getPropertyAction = new GetPropertyAction("java.io.tmpdir");
    return (String)AccessController.doPrivileged(getPropertyAction);
  }
  
  private static boolean hasCachePermission() {
    Boolean bool = getCacheInfo().getHasPermission();
    if (bool != null)
      return bool.booleanValue(); 
    try {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        String str1;
        File file = getCacheDirectory();
        if (file != null) {
          str1 = file.getPath();
        } else {
          str1 = getTempDir();
          if (str1 == null || str1.isEmpty()) {
            getCacheInfo().setHasPermission(Boolean.FALSE);
            return false;
          } 
        } 
        String str2 = str1;
        if (!str2.endsWith(File.separator))
          str2 = str2 + File.separator; 
        str2 = str2 + "*";
        securityManager.checkPermission(new FilePermission(str2, "read, write, delete"));
      } 
    } catch (SecurityException securityException) {
      getCacheInfo().setHasPermission(Boolean.FALSE);
      return false;
    } 
    getCacheInfo().setHasPermission(Boolean.TRUE);
    return true;
  }
  
  public static void setUseCache(boolean paramBoolean) { getCacheInfo().setUseCache(paramBoolean); }
  
  public static boolean getUseCache() { return getCacheInfo().getUseCache(); }
  
  public static void setCacheDirectory(File paramFile) {
    if (paramFile != null && !paramFile.isDirectory())
      throw new IllegalArgumentException("Not a directory!"); 
    getCacheInfo().setCacheDirectory(paramFile);
    getCacheInfo().setHasPermission(null);
  }
  
  public static File getCacheDirectory() { return getCacheInfo().getCacheDirectory(); }
  
  public static ImageInputStream createImageInputStream(Object paramObject) throws IOException {
    Iterator iterator;
    if (paramObject == null)
      throw new IllegalArgumentException("input == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageInputStreamSpi.class, true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    } 
    boolean bool = (getUseCache() && hasCachePermission());
    while (iterator.hasNext()) {
      ImageInputStreamSpi imageInputStreamSpi = (ImageInputStreamSpi)iterator.next();
      if (imageInputStreamSpi.getInputClass().isInstance(paramObject))
        try {
          return imageInputStreamSpi.createInputStreamInstance(paramObject, bool, getCacheDirectory());
        } catch (IOException iOException) {
          throw new IIOException("Can't create cache file!", iOException);
        }  
    } 
    return null;
  }
  
  public static ImageOutputStream createImageOutputStream(Object paramObject) throws IOException {
    Iterator iterator;
    if (paramObject == null)
      throw new IllegalArgumentException("output == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageOutputStreamSpi.class, true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return null;
    } 
    boolean bool = (getUseCache() && hasCachePermission());
    while (iterator.hasNext()) {
      ImageOutputStreamSpi imageOutputStreamSpi = (ImageOutputStreamSpi)iterator.next();
      if (imageOutputStreamSpi.getOutputClass().isInstance(paramObject))
        try {
          return imageOutputStreamSpi.createOutputStreamInstance(paramObject, bool, getCacheDirectory());
        } catch (IOException iOException) {
          throw new IIOException("Can't create cache file!", iOException);
        }  
    } 
    return null;
  }
  
  private static <S extends ImageReaderWriterSpi> String[] getReaderWriterInfo(Class<S> paramClass, SpiInfo paramSpiInfo) {
    Iterator iterator;
    try {
      iterator = theRegistry.getServiceProviders(paramClass, true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return new String[0];
    } 
    HashSet hashSet = new HashSet();
    while (iterator.hasNext()) {
      ImageReaderWriterSpi imageReaderWriterSpi = (ImageReaderWriterSpi)iterator.next();
      Collections.addAll(hashSet, paramSpiInfo.info(imageReaderWriterSpi));
    } 
    return (String[])hashSet.toArray(new String[hashSet.size()]);
  }
  
  public static String[] getReaderFormatNames() { return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.FORMAT_NAMES); }
  
  public static String[] getReaderMIMETypes() { return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.MIME_TYPES); }
  
  public static String[] getReaderFileSuffixes() { return getReaderWriterInfo(ImageReaderSpi.class, SpiInfo.FILE_SUFFIXES); }
  
  public static Iterator<ImageReader> getImageReaders(Object paramObject) {
    Iterator iterator;
    if (paramObject == null)
      throw new IllegalArgumentException("input == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new CanDecodeInputFilter(paramObject), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageReaderIterator(iterator);
  }
  
  public static Iterator<ImageReader> getImageReadersByFormatName(String paramString) {
    Iterator iterator;
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerFormatNamesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageReaderIterator(iterator);
  }
  
  public static Iterator<ImageReader> getImageReadersBySuffix(String paramString) {
    Iterator iterator;
    if (paramString == null)
      throw new IllegalArgumentException("fileSuffix == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerFileSuffixesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageReaderIterator(iterator);
  }
  
  public static Iterator<ImageReader> getImageReadersByMIMEType(String paramString) {
    Iterator iterator;
    if (paramString == null)
      throw new IllegalArgumentException("MIMEType == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, new ContainsFilter(readerMIMETypesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageReaderIterator(iterator);
  }
  
  public static String[] getWriterFormatNames() { return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.FORMAT_NAMES); }
  
  public static String[] getWriterMIMETypes() { return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.MIME_TYPES); }
  
  public static String[] getWriterFileSuffixes() { return getReaderWriterInfo(ImageWriterSpi.class, SpiInfo.FILE_SUFFIXES); }
  
  private static boolean contains(String[] paramArrayOfString, String paramString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramString.equalsIgnoreCase(paramArrayOfString[b]))
        return true; 
    } 
    return false;
  }
  
  public static Iterator<ImageWriter> getImageWritersByFormatName(String paramString) {
    Iterator iterator;
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerFormatNamesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageWriterIterator(iterator);
  }
  
  public static Iterator<ImageWriter> getImageWritersBySuffix(String paramString) {
    Iterator iterator;
    if (paramString == null)
      throw new IllegalArgumentException("fileSuffix == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerFileSuffixesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageWriterIterator(iterator);
  }
  
  public static Iterator<ImageWriter> getImageWritersByMIMEType(String paramString) {
    Iterator iterator;
    if (paramString == null)
      throw new IllegalArgumentException("MIMEType == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new ContainsFilter(writerMIMETypesMethod, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageWriterIterator(iterator);
  }
  
  public static ImageWriter getImageWriter(ImageReader paramImageReader) {
    if (paramImageReader == null)
      throw new IllegalArgumentException("reader == null!"); 
    ImageReaderSpi imageReaderSpi = paramImageReader.getOriginatingProvider();
    if (imageReaderSpi == null) {
      Iterator iterator;
      try {
        iterator = theRegistry.getServiceProviders(ImageReaderSpi.class, false);
      } catch (IllegalArgumentException illegalArgumentException) {
        return null;
      } 
      while (iterator.hasNext()) {
        ImageReaderSpi imageReaderSpi1 = (ImageReaderSpi)iterator.next();
        if (imageReaderSpi1.isOwnReader(paramImageReader)) {
          imageReaderSpi = imageReaderSpi1;
          break;
        } 
      } 
      if (imageReaderSpi == null)
        return null; 
    } 
    String[] arrayOfString = imageReaderSpi.getImageWriterSpiNames();
    if (arrayOfString == null)
      return null; 
    Class clazz = null;
    try {
      clazz = Class.forName(arrayOfString[0], true, ClassLoader.getSystemClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
    ImageWriterSpi imageWriterSpi = (ImageWriterSpi)theRegistry.getServiceProviderByClass(clazz);
    if (imageWriterSpi == null)
      return null; 
    try {
      return imageWriterSpi.createWriterInstance();
    } catch (IOException iOException) {
      theRegistry.deregisterServiceProvider(imageWriterSpi, ImageWriterSpi.class);
      return null;
    } 
  }
  
  public static ImageReader getImageReader(ImageWriter paramImageWriter) {
    if (paramImageWriter == null)
      throw new IllegalArgumentException("writer == null!"); 
    ImageWriterSpi imageWriterSpi = paramImageWriter.getOriginatingProvider();
    if (imageWriterSpi == null) {
      Iterator iterator;
      try {
        iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, false);
      } catch (IllegalArgumentException illegalArgumentException) {
        return null;
      } 
      while (iterator.hasNext()) {
        ImageWriterSpi imageWriterSpi1 = (ImageWriterSpi)iterator.next();
        if (imageWriterSpi1.isOwnWriter(paramImageWriter)) {
          imageWriterSpi = imageWriterSpi1;
          break;
        } 
      } 
      if (imageWriterSpi == null)
        return null; 
    } 
    String[] arrayOfString = imageWriterSpi.getImageReaderSpiNames();
    if (arrayOfString == null)
      return null; 
    Class clazz = null;
    try {
      clazz = Class.forName(arrayOfString[0], true, ClassLoader.getSystemClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      return null;
    } 
    ImageReaderSpi imageReaderSpi = (ImageReaderSpi)theRegistry.getServiceProviderByClass(clazz);
    if (imageReaderSpi == null)
      return null; 
    try {
      return imageReaderSpi.createReaderInstance();
    } catch (IOException iOException) {
      theRegistry.deregisterServiceProvider(imageReaderSpi, ImageReaderSpi.class);
      return null;
    } 
  }
  
  public static Iterator<ImageWriter> getImageWriters(ImageTypeSpecifier paramImageTypeSpecifier, String paramString) {
    Iterator iterator;
    if (paramImageTypeSpecifier == null)
      throw new IllegalArgumentException("type == null!"); 
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    try {
      iterator = theRegistry.getServiceProviders(ImageWriterSpi.class, new CanEncodeImageAndFormatFilter(paramImageTypeSpecifier, paramString), true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageWriterIterator(iterator);
  }
  
  public static Iterator<ImageTranscoder> getImageTranscoders(ImageReader paramImageReader, ImageWriter paramImageWriter) {
    Iterator iterator;
    if (paramImageReader == null)
      throw new IllegalArgumentException("reader == null!"); 
    if (paramImageWriter == null)
      throw new IllegalArgumentException("writer == null!"); 
    ImageReaderSpi imageReaderSpi = paramImageReader.getOriginatingProvider();
    ImageWriterSpi imageWriterSpi = paramImageWriter.getOriginatingProvider();
    TranscoderFilter transcoderFilter = new TranscoderFilter(imageReaderSpi, imageWriterSpi);
    try {
      iterator = theRegistry.getServiceProviders(ImageTranscoderSpi.class, transcoderFilter, true);
    } catch (IllegalArgumentException illegalArgumentException) {
      return Collections.emptyIterator();
    } 
    return new ImageTranscoderIterator(iterator);
  }
  
  public static BufferedImage read(File paramFile) throws IOException {
    if (paramFile == null)
      throw new IllegalArgumentException("input == null!"); 
    if (!paramFile.canRead())
      throw new IIOException("Can't read input file!"); 
    ImageInputStream imageInputStream = createImageInputStream(paramFile);
    if (imageInputStream == null)
      throw new IIOException("Can't create an ImageInputStream!"); 
    BufferedImage bufferedImage = read(imageInputStream);
    if (bufferedImage == null)
      imageInputStream.close(); 
    return bufferedImage;
  }
  
  public static BufferedImage read(InputStream paramInputStream) throws IOException {
    if (paramInputStream == null)
      throw new IllegalArgumentException("input == null!"); 
    ImageInputStream imageInputStream = createImageInputStream(paramInputStream);
    BufferedImage bufferedImage = read(imageInputStream);
    if (bufferedImage == null)
      imageInputStream.close(); 
    return bufferedImage;
  }
  
  public static BufferedImage read(URL paramURL) throws IOException {
    BufferedImage bufferedImage;
    if (paramURL == null)
      throw new IllegalArgumentException("input == null!"); 
    inputStream = null;
    try {
      inputStream = paramURL.openStream();
    } catch (IOException iOException) {
      throw new IIOException("Can't get input stream from URL!", iOException);
    } 
    ImageInputStream imageInputStream = createImageInputStream(inputStream);
    try {
      bufferedImage = read(imageInputStream);
      if (bufferedImage == null)
        imageInputStream.close(); 
    } finally {
      inputStream.close();
    } 
    return bufferedImage;
  }
  
  public static BufferedImage read(ImageInputStream paramImageInputStream) throws IOException {
    BufferedImage bufferedImage;
    if (paramImageInputStream == null)
      throw new IllegalArgumentException("stream == null!"); 
    Iterator iterator = getImageReaders(paramImageInputStream);
    if (!iterator.hasNext())
      return null; 
    imageReader = (ImageReader)iterator.next();
    ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
    imageReader.setInput(paramImageInputStream, true, true);
    try {
      bufferedImage = imageReader.read(0, imageReadParam);
    } finally {
      imageReader.dispose();
      paramImageInputStream.close();
    } 
    return bufferedImage;
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, ImageOutputStream paramImageOutputStream) throws IOException {
    if (paramRenderedImage == null)
      throw new IllegalArgumentException("im == null!"); 
    if (paramString == null)
      throw new IllegalArgumentException("formatName == null!"); 
    if (paramImageOutputStream == null)
      throw new IllegalArgumentException("output == null!"); 
    return doWrite(paramRenderedImage, getWriter(paramRenderedImage, paramString), paramImageOutputStream);
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, File paramFile) throws IOException {
    if (paramFile == null)
      throw new IllegalArgumentException("output == null!"); 
    imageOutputStream = null;
    ImageWriter imageWriter = getWriter(paramRenderedImage, paramString);
    if (imageWriter == null)
      return false; 
    try {
      paramFile.delete();
      imageOutputStream = createImageOutputStream(paramFile);
    } catch (IOException iOException) {
      throw new IIOException("Can't create output stream!", iOException);
    } 
    try {
      return doWrite(paramRenderedImage, imageWriter, imageOutputStream);
    } finally {
      imageOutputStream.close();
    } 
  }
  
  public static boolean write(RenderedImage paramRenderedImage, String paramString, OutputStream paramOutputStream) throws IOException {
    if (paramOutputStream == null)
      throw new IllegalArgumentException("output == null!"); 
    imageOutputStream = null;
    try {
      imageOutputStream = createImageOutputStream(paramOutputStream);
    } catch (IOException iOException) {
      throw new IIOException("Can't create output stream!", iOException);
    } 
    try {
      return doWrite(paramRenderedImage, getWriter(paramRenderedImage, paramString), imageOutputStream);
    } finally {
      imageOutputStream.close();
    } 
  }
  
  private static ImageWriter getWriter(RenderedImage paramRenderedImage, String paramString) {
    ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromRenderedImage(paramRenderedImage);
    Iterator iterator = getImageWriters(imageTypeSpecifier, paramString);
    return iterator.hasNext() ? (ImageWriter)iterator.next() : null;
  }
  
  private static boolean doWrite(RenderedImage paramRenderedImage, ImageWriter paramImageWriter, ImageOutputStream paramImageOutputStream) throws IOException {
    if (paramImageWriter == null)
      return false; 
    paramImageWriter.setOutput(paramImageOutputStream);
    try {
      paramImageWriter.write(paramRenderedImage);
    } finally {
      paramImageWriter.dispose();
      paramImageOutputStream.flush();
    } 
    return true;
  }
  
  static  {
    try {
      readerFormatNamesMethod = ImageReaderSpi.class.getMethod("getFormatNames", new Class[0]);
      readerFileSuffixesMethod = ImageReaderSpi.class.getMethod("getFileSuffixes", new Class[0]);
      readerMIMETypesMethod = ImageReaderSpi.class.getMethod("getMIMETypes", new Class[0]);
      writerFormatNamesMethod = ImageWriterSpi.class.getMethod("getFormatNames", new Class[0]);
      writerFileSuffixesMethod = ImageWriterSpi.class.getMethod("getFileSuffixes", new Class[0]);
      writerMIMETypesMethod = ImageWriterSpi.class.getMethod("getMIMETypes", new Class[0]);
    } catch (NoSuchMethodException noSuchMethodException) {
      noSuchMethodException.printStackTrace();
    } 
  }
  
  static class CacheInfo {
    boolean useCache = true;
    
    File cacheDirectory = null;
    
    Boolean hasPermission = null;
    
    public boolean getUseCache() { return this.useCache; }
    
    public void setUseCache(boolean param1Boolean) { this.useCache = param1Boolean; }
    
    public File getCacheDirectory() { return this.cacheDirectory; }
    
    public void setCacheDirectory(File param1File) { this.cacheDirectory = param1File; }
    
    public Boolean getHasPermission() { return this.hasPermission; }
    
    public void setHasPermission(Boolean param1Boolean) { this.hasPermission = param1Boolean; }
  }
  
  static class CanDecodeInputFilter implements ServiceRegistry.Filter {
    Object input;
    
    public CanDecodeInputFilter(Object param1Object) { this.input = param1Object; }
    
    public boolean filter(Object param1Object) {
      try {
        ImageReaderSpi imageReaderSpi = (ImageReaderSpi)param1Object;
        ImageInputStream imageInputStream = null;
        if (this.input instanceof ImageInputStream)
          imageInputStream = (ImageInputStream)this.input; 
        boolean bool = false;
        if (imageInputStream != null)
          imageInputStream.mark(); 
        bool = imageReaderSpi.canDecodeInput(this.input);
        if (imageInputStream != null)
          imageInputStream.reset(); 
        return bool;
      } catch (IOException iOException) {
        return false;
      } 
    }
  }
  
  static class CanEncodeImageAndFormatFilter implements ServiceRegistry.Filter {
    ImageTypeSpecifier type;
    
    String formatName;
    
    public CanEncodeImageAndFormatFilter(ImageTypeSpecifier param1ImageTypeSpecifier, String param1String) {
      this.type = param1ImageTypeSpecifier;
      this.formatName = param1String;
    }
    
    public boolean filter(Object param1Object) {
      ImageWriterSpi imageWriterSpi = (ImageWriterSpi)param1Object;
      return (Arrays.asList(imageWriterSpi.getFormatNames()).contains(this.formatName) && imageWriterSpi.canEncodeImage(this.type));
    }
  }
  
  static class ContainsFilter implements ServiceRegistry.Filter {
    Method method;
    
    String name;
    
    public ContainsFilter(Method param1Method, String param1String) {
      this.method = param1Method;
      this.name = param1String;
    }
    
    public boolean filter(Object param1Object) {
      try {
        return ImageIO.contains((String[])this.method.invoke(param1Object, new Object[0]), this.name);
      } catch (Exception exception) {
        return false;
      } 
    }
  }
  
  static class ImageReaderIterator extends Object implements Iterator<ImageReader> {
    public Iterator iter;
    
    public ImageReaderIterator(Iterator param1Iterator) { this.iter = param1Iterator; }
    
    public boolean hasNext() { return this.iter.hasNext(); }
    
    public ImageReader next() {
      ImageReaderSpi imageReaderSpi = null;
      try {
        imageReaderSpi = (ImageReaderSpi)this.iter.next();
        return imageReaderSpi.createReaderInstance();
      } catch (IOException iOException) {
        theRegistry.deregisterServiceProvider(imageReaderSpi, ImageReaderSpi.class);
        return null;
      } 
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  static class ImageTranscoderIterator extends Object implements Iterator<ImageTranscoder> {
    public Iterator iter;
    
    public ImageTranscoderIterator(Iterator param1Iterator) { this.iter = param1Iterator; }
    
    public boolean hasNext() { return this.iter.hasNext(); }
    
    public ImageTranscoder next() {
      ImageTranscoderSpi imageTranscoderSpi = null;
      imageTranscoderSpi = (ImageTranscoderSpi)this.iter.next();
      return imageTranscoderSpi.createTranscoderInstance();
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  static class ImageWriterIterator extends Object implements Iterator<ImageWriter> {
    public Iterator iter;
    
    public ImageWriterIterator(Iterator param1Iterator) { this.iter = param1Iterator; }
    
    public boolean hasNext() { return this.iter.hasNext(); }
    
    public ImageWriter next() {
      ImageWriterSpi imageWriterSpi = null;
      try {
        imageWriterSpi = (ImageWriterSpi)this.iter.next();
        return imageWriterSpi.createWriterInstance();
      } catch (IOException iOException) {
        theRegistry.deregisterServiceProvider(imageWriterSpi, ImageWriterSpi.class);
        return null;
      } 
    }
    
    public void remove() { throw new UnsupportedOperationException(); }
  }
  
  private final abstract enum SpiInfo {
    FORMAT_NAMES, MIME_TYPES, FILE_SUFFIXES;
    
    abstract String[] info(ImageReaderWriterSpi param1ImageReaderWriterSpi);
    
    static  {
      // Byte code:
      //   0: new javax/imageio/ImageIO$SpiInfo$1
      //   3: dup
      //   4: ldc 'FORMAT_NAMES'
      //   6: iconst_0
      //   7: invokespecial <init> : (Ljava/lang/String;I)V
      //   10: putstatic javax/imageio/ImageIO$SpiInfo.FORMAT_NAMES : Ljavax/imageio/ImageIO$SpiInfo;
      //   13: new javax/imageio/ImageIO$SpiInfo$2
      //   16: dup
      //   17: ldc 'MIME_TYPES'
      //   19: iconst_1
      //   20: invokespecial <init> : (Ljava/lang/String;I)V
      //   23: putstatic javax/imageio/ImageIO$SpiInfo.MIME_TYPES : Ljavax/imageio/ImageIO$SpiInfo;
      //   26: new javax/imageio/ImageIO$SpiInfo$3
      //   29: dup
      //   30: ldc 'FILE_SUFFIXES'
      //   32: iconst_2
      //   33: invokespecial <init> : (Ljava/lang/String;I)V
      //   36: putstatic javax/imageio/ImageIO$SpiInfo.FILE_SUFFIXES : Ljavax/imageio/ImageIO$SpiInfo;
      //   39: iconst_3
      //   40: anewarray javax/imageio/ImageIO$SpiInfo
      //   43: dup
      //   44: iconst_0
      //   45: getstatic javax/imageio/ImageIO$SpiInfo.FORMAT_NAMES : Ljavax/imageio/ImageIO$SpiInfo;
      //   48: aastore
      //   49: dup
      //   50: iconst_1
      //   51: getstatic javax/imageio/ImageIO$SpiInfo.MIME_TYPES : Ljavax/imageio/ImageIO$SpiInfo;
      //   54: aastore
      //   55: dup
      //   56: iconst_2
      //   57: getstatic javax/imageio/ImageIO$SpiInfo.FILE_SUFFIXES : Ljavax/imageio/ImageIO$SpiInfo;
      //   60: aastore
      //   61: putstatic javax/imageio/ImageIO$SpiInfo.$VALUES : [Ljavax/imageio/ImageIO$SpiInfo;
      //   64: return
    }
  }
  
  static class TranscoderFilter implements ServiceRegistry.Filter {
    String readerSpiName;
    
    String writerSpiName;
    
    public TranscoderFilter(ImageReaderSpi param1ImageReaderSpi, ImageWriterSpi param1ImageWriterSpi) {
      this.readerSpiName = param1ImageReaderSpi.getClass().getName();
      this.writerSpiName = param1ImageWriterSpi.getClass().getName();
    }
    
    public boolean filter(Object param1Object) {
      ImageTranscoderSpi imageTranscoderSpi = (ImageTranscoderSpi)param1Object;
      String str1 = imageTranscoderSpi.getReaderServiceProviderName();
      String str2 = imageTranscoderSpi.getWriterServiceProviderName();
      return (str1.equals(this.readerSpiName) && str2.equals(this.writerSpiName));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\imageio\ImageIO.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */