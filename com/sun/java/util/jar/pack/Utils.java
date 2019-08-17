package com.sun.java.util.jar.pack;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import sun.util.logging.PlatformLogger;

class Utils {
  static final String COM_PREFIX = "com.sun.java.util.jar.pack.";
  
  static final String METAINF = "META-INF";
  
  static final String DEBUG_VERBOSE = "com.sun.java.util.jar.pack.verbose";
  
  static final String DEBUG_DISABLE_NATIVE = "com.sun.java.util.jar.pack.disable.native";
  
  static final String PACK_DEFAULT_TIMEZONE = "com.sun.java.util.jar.pack.default.timezone";
  
  static final String UNPACK_MODIFICATION_TIME = "com.sun.java.util.jar.pack.unpack.modification.time";
  
  static final String UNPACK_STRIP_DEBUG = "com.sun.java.util.jar.pack.unpack.strip.debug";
  
  static final String UNPACK_REMOVE_PACKFILE = "com.sun.java.util.jar.pack.unpack.remove.packfile";
  
  static final String NOW = "now";
  
  static final String PACK_KEEP_CLASS_ORDER = "com.sun.java.util.jar.pack.keep.class.order";
  
  static final String PACK_ZIP_ARCHIVE_MARKER_COMMENT = "PACK200";
  
  static final String CLASS_FORMAT_ERROR = "com.sun.java.util.jar.pack.class.format.error";
  
  static final ThreadLocal<TLGlobals> currentInstance = new ThreadLocal();
  
  private static TimeZone tz;
  
  private static int workingPackerCount = 0;
  
  static final boolean nolog = Boolean.getBoolean("com.sun.java.util.jar.pack.nolog");
  
  static final boolean SORT_MEMBERS_DESCR_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.members.descr.major");
  
  static final boolean SORT_HANDLES_KIND_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.handles.kind.major");
  
  static final boolean SORT_INDY_BSS_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.indy.bss.major");
  
  static final boolean SORT_BSS_BSM_MAJOR = Boolean.getBoolean("com.sun.java.util.jar.pack.sort.bss.bsm.major");
  
  static final Pack200Logger log = new Pack200Logger("java.util.jar.Pack200");
  
  static TLGlobals getTLGlobals() { return (TLGlobals)currentInstance.get(); }
  
  static PropMap currentPropMap() {
    Object object = currentInstance.get();
    return (object instanceof PackerImpl) ? ((PackerImpl)object).props : ((object instanceof UnpackerImpl) ? ((UnpackerImpl)object).props : null);
  }
  
  static void changeDefaultTimeZoneToUtc() {
    if (workingPackerCount++ == 0)
      TimeZone.setDefault((tz = TimeZone.getDefault()).getTimeZone("UTC")); 
  }
  
  static void restoreDefaultTimeZone() {
    if (--workingPackerCount == 0) {
      if (tz != null)
        TimeZone.setDefault(tz); 
      tz = null;
    } 
  }
  
  static String getVersionString() { return "Pack200, Vendor: " + System.getProperty("java.vendor") + ", Version: " + Constants.MAX_PACKAGE_VERSION; }
  
  static void markJarFile(JarOutputStream paramJarOutputStream) throws IOException { paramJarOutputStream.setComment("PACK200"); }
  
  static void copyJarFile(JarInputStream paramJarInputStream, JarOutputStream paramJarOutputStream) throws IOException {
    if (paramJarInputStream.getManifest() != null) {
      ZipEntry zipEntry = new ZipEntry("META-INF/MANIFEST.MF");
      paramJarOutputStream.putNextEntry(zipEntry);
      paramJarInputStream.getManifest().write(paramJarOutputStream);
      paramJarOutputStream.closeEntry();
    } 
    byte[] arrayOfByte = new byte[16384];
    JarEntry jarEntry;
    while ((jarEntry = paramJarInputStream.getNextJarEntry()) != null) {
      paramJarOutputStream.putNextEntry(jarEntry);
      int i;
      while (0 < (i = paramJarInputStream.read(arrayOfByte)))
        paramJarOutputStream.write(arrayOfByte, 0, i); 
    } 
    paramJarInputStream.close();
    markJarFile(paramJarOutputStream);
  }
  
  static void copyJarFile(JarFile paramJarFile, JarOutputStream paramJarOutputStream) throws IOException {
    byte[] arrayOfByte = new byte[16384];
    for (JarEntry jarEntry : Collections.list(paramJarFile.entries())) {
      paramJarOutputStream.putNextEntry(jarEntry);
      InputStream inputStream = paramJarFile.getInputStream(jarEntry);
      int i;
      while (0 < (i = inputStream.read(arrayOfByte)))
        paramJarOutputStream.write(arrayOfByte, 0, i); 
    } 
    paramJarFile.close();
    markJarFile(paramJarOutputStream);
  }
  
  static void copyJarFile(JarInputStream paramJarInputStream, OutputStream paramOutputStream) throws IOException {
    paramOutputStream = new BufferedOutputStream(paramOutputStream);
    paramOutputStream = new NonCloser(paramOutputStream);
    try (JarOutputStream null = new JarOutputStream(paramOutputStream)) {
      copyJarFile(paramJarInputStream, jarOutputStream);
    } 
  }
  
  static void copyJarFile(JarFile paramJarFile, OutputStream paramOutputStream) throws IOException {
    paramOutputStream = new BufferedOutputStream(paramOutputStream);
    paramOutputStream = new NonCloser(paramOutputStream);
    try (JarOutputStream null = new JarOutputStream(paramOutputStream)) {
      copyJarFile(paramJarFile, jarOutputStream);
    } 
  }
  
  static String getJarEntryName(String paramString) { return (paramString == null) ? null : paramString.replace(File.separatorChar, '/'); }
  
  static String zeString(ZipEntry paramZipEntry) {
    int i = (paramZipEntry.getCompressedSize() > 0L) ? (int)((1.0D - paramZipEntry.getCompressedSize() / paramZipEntry.getSize()) * 100.0D) : 0;
    return paramZipEntry.getSize() + "\t" + paramZipEntry.getMethod() + "\t" + paramZipEntry.getCompressedSize() + "\t" + i + "%\t" + new Date(paramZipEntry.getTime()) + "\t" + Long.toHexString(paramZipEntry.getCrc()) + "\t" + paramZipEntry.getName();
  }
  
  static byte[] readMagic(BufferedInputStream paramBufferedInputStream) throws IOException {
    paramBufferedInputStream.mark(4);
    byte[] arrayOfByte = new byte[4];
    for (byte b = 0; b < arrayOfByte.length && 1 == paramBufferedInputStream.read(arrayOfByte, b, 1); b++);
    paramBufferedInputStream.reset();
    return arrayOfByte;
  }
  
  static boolean isJarMagic(byte[] paramArrayOfByte) { return (paramArrayOfByte[0] == 80 && paramArrayOfByte[1] == 75 && paramArrayOfByte[2] >= 1 && paramArrayOfByte[2] < 8 && paramArrayOfByte[3] == paramArrayOfByte[2] + 1); }
  
  static boolean isPackMagic(byte[] paramArrayOfByte) { return (paramArrayOfByte[0] == -54 && paramArrayOfByte[1] == -2 && paramArrayOfByte[2] == -48 && paramArrayOfByte[3] == 13); }
  
  static boolean isGZIPMagic(byte[] paramArrayOfByte) { return (paramArrayOfByte[0] == 31 && paramArrayOfByte[1] == -117 && paramArrayOfByte[2] == 8); }
  
  private static class NonCloser extends FilterOutputStream {
    NonCloser(OutputStream param1OutputStream) { super(param1OutputStream); }
    
    public void close() { flush(); }
  }
  
  static class Pack200Logger {
    private final String name;
    
    private PlatformLogger log;
    
    Pack200Logger(String param1String) { this.name = param1String; }
    
    private PlatformLogger getLogger() {
      if (this.log == null)
        this.log = PlatformLogger.getLogger(this.name); 
      return this.log;
    }
    
    public void warning(String param1String, Object param1Object) { getLogger().warning(param1String, new Object[] { param1Object }); }
    
    public void warning(String param1String) { warning(param1String, null); }
    
    public void info(String param1String) {
      int i = Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
      if (i > 0)
        if (Utils.nolog) {
          System.out.println(param1String);
        } else {
          getLogger().info(param1String);
        }  
    }
    
    public void fine(String param1String) {
      int i = Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose");
      if (i > 0)
        System.out.println(param1String); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Utils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */