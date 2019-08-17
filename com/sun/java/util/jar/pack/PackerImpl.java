package com.sun.java.util.jar.pack;

import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Pack200;

public class PackerImpl extends TLGlobals implements Pack200.Packer {
  public SortedMap<String, String> properties() { return this.props; }
  
  public void pack(JarFile paramJarFile, OutputStream paramOutputStream) throws IOException {
    assert Utils.currentInstance.get() == null;
    bool = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try {
      Utils.currentInstance.set(this);
      if (bool)
        Utils.changeDefaultTimeZoneToUtc(); 
      if ("0".equals(this.props.getProperty("pack.effort"))) {
        Utils.copyJarFile(paramJarFile, paramOutputStream);
      } else {
        (new DoPack(null)).run(paramJarFile, paramOutputStream);
      } 
    } finally {
      Utils.currentInstance.set(null);
      if (bool)
        Utils.restoreDefaultTimeZone(); 
      paramJarFile.close();
    } 
  }
  
  public void pack(JarInputStream paramJarInputStream, OutputStream paramOutputStream) throws IOException {
    assert Utils.currentInstance.get() == null;
    bool = !this.props.getBoolean("com.sun.java.util.jar.pack.default.timezone") ? 1 : 0;
    try {
      Utils.currentInstance.set(this);
      if (bool)
        Utils.changeDefaultTimeZoneToUtc(); 
      if ("0".equals(this.props.getProperty("pack.effort"))) {
        Utils.copyJarFile(paramJarInputStream, paramOutputStream);
      } else {
        (new DoPack(null)).run(paramJarInputStream, paramOutputStream);
      } 
    } finally {
      Utils.currentInstance.set(null);
      if (bool)
        Utils.restoreDefaultTimeZone(); 
      paramJarInputStream.close();
    } 
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.props.addListener(paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.props.removeListener(paramPropertyChangeListener); }
  
  private class DoPack {
    final int verbose = PackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.verbose");
    
    final Package pkg;
    
    final String unknownAttrCommand;
    
    final String classFormatCommand;
    
    final Map<Attribute.Layout, Attribute> attrDefs;
    
    final Map<Attribute.Layout, String> attrCommands;
    
    final boolean keepFileOrder;
    
    final boolean keepClassOrder;
    
    final boolean keepModtime;
    
    final boolean latestModtime;
    
    final boolean keepDeflateHint;
    
    long totalOutputSize;
    
    int segmentCount;
    
    long segmentTotalSize;
    
    long segmentSize;
    
    final long segmentLimit;
    
    final List<String> passFiles;
    
    private int nread;
    
    private DoPack() {
      PackerImpl.this.props.setInteger("pack.progress", 0);
      if (this.verbose > 0)
        Utils.log.info(PackerImpl.this.props.toString()); 
      this.pkg = new Package(Package.Version.makeVersion(PackerImpl.this.props, "min.class"), Package.Version.makeVersion(PackerImpl.this.props, "max.class"), Package.Version.makeVersion(PackerImpl.this.props, "package"));
      String str = PackerImpl.this.props.getProperty("pack.unknown.attribute", "pass");
      if (!"strip".equals(str) && !"pass".equals(str) && !"error".equals(str))
        throw new RuntimeException("Bad option: pack.unknown.attribute = " + str); 
      this.unknownAttrCommand = str.intern();
      str = PackerImpl.this.props.getProperty("com.sun.java.util.jar.pack.class.format.error", "pass");
      if (!"pass".equals(str) && !"error".equals(str))
        throw new RuntimeException("Bad option: com.sun.java.util.jar.pack.class.format.error = " + str); 
      this.classFormatCommand = str.intern();
      HashMap hashMap1 = new HashMap();
      HashMap hashMap2 = new HashMap();
      String[] arrayOfString = { "pack.class.attribute.", "pack.field.attribute.", "pack.method.attribute.", "pack.code.attribute." };
      int[] arrayOfInt = { 0, 1, 2, 3 };
      for (byte b = 0; b < arrayOfInt.length; b++) {
        String str1 = arrayOfString[b];
        SortedMap sortedMap = PackerImpl.this.props.prefixMap(str1);
        for (String str2 : sortedMap.keySet()) {
          assert str2.startsWith(str1);
          String str3 = str2.substring(str1.length());
          String str4 = PackerImpl.this.props.getProperty(str2);
          Attribute.Layout layout = Attribute.keyForLookup(arrayOfInt[b], str3);
          if ("strip".equals(str4) || "pass".equals(str4) || "error".equals(str4)) {
            hashMap2.put(layout, str4.intern());
            continue;
          } 
          Attribute.define(hashMap1, arrayOfInt[b], str3, str4);
          if (this.verbose > 1)
            Utils.log.fine("Added layout for " + Constants.ATTR_CONTEXT_NAME[b] + " attribute " + str3 + " = " + str4); 
          assert hashMap1.containsKey(layout);
        } 
      } 
      this.attrDefs = hashMap1.isEmpty() ? null : hashMap1;
      this.attrCommands = hashMap2.isEmpty() ? null : hashMap2;
      this.keepFileOrder = PackerImpl.this.props.getBoolean("pack.keep.file.order");
      this.keepClassOrder = PackerImpl.this.props.getBoolean("com.sun.java.util.jar.pack.keep.class.order");
      this.keepModtime = "keep".equals(PackerImpl.this.props.getProperty("pack.modification.time"));
      this.latestModtime = "latest".equals(PackerImpl.this.props.getProperty("pack.modification.time"));
      this.keepDeflateHint = "keep".equals(PackerImpl.this.props.getProperty("pack.deflate.hint"));
      if (!this.keepModtime && !this.latestModtime) {
        l = PackerImpl.this.props.getTime("pack.modification.time");
        if (l != 0)
          this.pkg.default_modtime = l; 
      } 
      if (!this.keepDeflateHint) {
        l = PackerImpl.this.props.getBoolean("pack.deflate.hint");
        if (l)
          this.pkg.default_options |= 0x20; 
      } 
      this.totalOutputSize = 0L;
      this.segmentCount = 0;
      this.segmentTotalSize = 0L;
      this.segmentSize = 0L;
      if (PackerImpl.this.props.getProperty("pack.segment.limit", "").equals("")) {
        l = -1L;
      } else {
        l = PackerImpl.this.props.getLong("pack.segment.limit");
      } 
      long l = Math.min(2147483647L, l);
      l = Math.max(-1L, l);
      if (l == -1L)
        l = Float.MAX_VALUE; 
      this.segmentLimit = l;
      this.passFiles = PackerImpl.this.props.getProperties("pack.pass.file.");
      ListIterator listIterator = this.passFiles.listIterator();
      while (listIterator.hasNext()) {
        String str1 = (String)listIterator.next();
        if (str1 == null) {
          listIterator.remove();
          continue;
        } 
        str1 = Utils.getJarEntryName(str1);
        if (str1.endsWith("/"))
          str1 = str1.substring(0, str1.length() - 1); 
        listIterator.set(str1);
      } 
      if (this.verbose > 0)
        Utils.log.info("passFiles = " + this.passFiles); 
      int i = PackerImpl.this.props.getInteger("com.sun.java.util.jar.pack.archive.options");
      if (i != 0)
        this.pkg.default_options |= i; 
      this.nread = 0;
    }
    
    boolean isClassFile(String param1String) {
      if (!param1String.endsWith(".class"))
        return false; 
      for (String str = param1String;; str = str.substring(0, i)) {
        if (this.passFiles.contains(str))
          return false; 
        int i = str.lastIndexOf('/');
        if (i < 0)
          break; 
      } 
      return true;
    }
    
    boolean isMetaInfFile(String param1String) { return (param1String.startsWith("/META-INF") || param1String.startsWith("META-INF")); }
    
    private void makeNextPackage() { this.pkg.reset(); }
    
    private void noteRead(InFile param1InFile) {
      this.nread++;
      if (this.verbose > 2)
        Utils.log.fine("...read " + param1InFile.name); 
      if (this.verbose > 0 && this.nread % 1000 == 0)
        Utils.log.info("Have read " + this.nread + " files..."); 
    }
    
    void run(JarInputStream param1JarInputStream, OutputStream param1OutputStream) throws IOException {
      if (param1JarInputStream.getManifest() != null) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        param1JarInputStream.getManifest().write(byteArrayOutputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        this.pkg.addFile(readFile("META-INF/MANIFEST.MF", byteArrayInputStream));
      } 
      JarEntry jarEntry;
      while ((jarEntry = param1JarInputStream.getNextJarEntry()) != null) {
        InFile inFile = new InFile(jarEntry);
        String str = inFile.name;
        Package.File file1 = readFile(str, param1JarInputStream);
        Package.File file2 = null;
        long l = isMetaInfFile(str) ? 0L : inFile.getInputLength();
        if (this.segmentSize += l > this.segmentLimit) {
          this.segmentSize -= l;
          byte b = -1;
          flushPartial(param1OutputStream, b);
        } 
        if (this.verbose > 1)
          Utils.log.fine("Reading " + str); 
        assert jarEntry.isDirectory() == str.endsWith("/");
        if (isClassFile(str))
          file2 = readClass(str, file1.getInputStream()); 
        if (file2 == null) {
          file2 = file1;
          this.pkg.addFile(file2);
        } 
        inFile.copyTo(file2);
        noteRead(inFile);
      } 
      flushAll(param1OutputStream);
    }
    
    void run(JarFile param1JarFile, OutputStream param1OutputStream) throws IOException {
      List list = scanJar(param1JarFile);
      if (this.verbose > 0)
        Utils.log.info("Reading " + list.size() + " files..."); 
      byte b = 0;
      for (InFile inFile : list) {
        String str = inFile.name;
        long l = isMetaInfFile(str) ? 0L : inFile.getInputLength();
        if (this.segmentSize += l > this.segmentLimit) {
          this.segmentSize -= l;
          float f1 = (b + true);
          float f2 = (this.segmentCount + 1);
          float f3 = list.size() - f1;
          float f4 = f3 * f2 / f1;
          if (this.verbose > 1)
            Utils.log.fine("Estimated segments to do: " + f4); 
          flushPartial(param1OutputStream, (int)Math.ceil(f4));
        } 
        InputStream inputStream = inFile.getInputStream();
        if (this.verbose > 1)
          Utils.log.fine("Reading " + str); 
        Package.File file = null;
        if (isClassFile(str)) {
          file = readClass(str, inputStream);
          if (file == null) {
            inputStream.close();
            inputStream = inFile.getInputStream();
          } 
        } 
        if (file == null) {
          file = readFile(str, inputStream);
          this.pkg.addFile(file);
        } 
        inFile.copyTo(file);
        inputStream.close();
        noteRead(inFile);
        b++;
      } 
      flushAll(param1OutputStream);
    }
    
    Package.File readClass(String param1String, InputStream param1InputStream) throws IOException {
      this.pkg.getClass();
      Package.Class clazz = new Package.Class(this.pkg, param1String);
      param1InputStream = new BufferedInputStream(param1InputStream);
      ClassReader classReader = new ClassReader(clazz, param1InputStream);
      classReader.setAttrDefs(this.attrDefs);
      classReader.setAttrCommands(this.attrCommands);
      classReader.unknownAttrCommand = this.unknownAttrCommand;
      try {
        classReader.read();
      } catch (IOException iOException) {
        String str = "Passing class file uncompressed due to";
        if (iOException instanceof Attribute.FormatException) {
          Attribute.FormatException formatException = (Attribute.FormatException)iOException;
          if (formatException.layout.equals("pass")) {
            Utils.log.info(formatException.toString());
            Utils.log.warning(str + " unrecognized attribute: " + param1String);
            return null;
          } 
        } else if (iOException instanceof ClassReader.ClassFormatException) {
          ClassReader.ClassFormatException classFormatException = (ClassReader.ClassFormatException)iOException;
          if (this.classFormatCommand.equals("pass")) {
            Utils.log.info(classFormatException.toString());
            Utils.log.warning(str + " unknown class format: " + param1String);
            return null;
          } 
        } 
        throw iOException;
      } 
      this.pkg.addClass(clazz);
      return clazz.file;
    }
    
    Package.File readFile(String param1String, InputStream param1InputStream) throws IOException {
      this.pkg.getClass();
      Package.File file = new Package.File(this.pkg, param1String);
      file.readFrom(param1InputStream);
      if (file.isDirectory() && file.getFileLength() != 0L)
        throw new IllegalArgumentException("Non-empty directory: " + file.getFileName()); 
      return file;
    }
    
    void flushPartial(OutputStream param1OutputStream, int param1Int) throws IOException {
      if (this.pkg.files.isEmpty() && this.pkg.classes.isEmpty())
        return; 
      flushPackage(param1OutputStream, Math.max(1, param1Int));
      PackerImpl.this.props.setInteger("pack.progress", 25);
      makeNextPackage();
      this.segmentCount++;
      this.segmentTotalSize += this.segmentSize;
      this.segmentSize = 0L;
    }
    
    void flushAll(OutputStream param1OutputStream) throws IOException {
      PackerImpl.this.props.setInteger("pack.progress", 50);
      flushPackage(param1OutputStream, 0);
      param1OutputStream.flush();
      PackerImpl.this.props.setInteger("pack.progress", 100);
      this.segmentCount++;
      this.segmentTotalSize += this.segmentSize;
      this.segmentSize = 0L;
      if (this.verbose > 0 && this.segmentCount > 1)
        Utils.log.info("Transmitted " + this.segmentTotalSize + " input bytes in " + this.segmentCount + " segments totaling " + this.totalOutputSize + " bytes"); 
    }
    
    void flushPackage(OutputStream param1OutputStream, int param1Int) throws IOException { // Byte code:
      //   0: aload_0
      //   1: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   4: getfield files : Ljava/util/ArrayList;
      //   7: invokevirtual size : ()I
      //   10: istore_3
      //   11: aload_0
      //   12: getfield keepFileOrder : Z
      //   15: ifne -> 53
      //   18: aload_0
      //   19: getfield verbose : I
      //   22: iconst_1
      //   23: if_icmple -> 34
      //   26: getstatic com/sun/java/util/jar/pack/Utils.log : Lcom/sun/java/util/jar/pack/Utils$Pack200Logger;
      //   29: ldc 'Reordering files.'
      //   31: invokevirtual fine : (Ljava/lang/String;)V
      //   34: iconst_1
      //   35: istore #4
      //   37: aload_0
      //   38: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   41: aload_0
      //   42: getfield keepClassOrder : Z
      //   45: iload #4
      //   47: invokevirtual reorderFiles : (ZZ)V
      //   50: goto -> 165
      //   53: getstatic com/sun/java/util/jar/pack/PackerImpl$DoPack.$assertionsDisabled : Z
      //   56: ifne -> 87
      //   59: aload_0
      //   60: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   63: getfield files : Ljava/util/ArrayList;
      //   66: aload_0
      //   67: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   70: invokevirtual getClassStubs : ()Ljava/util/List;
      //   73: invokevirtual containsAll : (Ljava/util/Collection;)Z
      //   76: ifne -> 87
      //   79: new java/lang/AssertionError
      //   82: dup
      //   83: invokespecial <init> : ()V
      //   86: athrow
      //   87: aload_0
      //   88: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   91: getfield files : Ljava/util/ArrayList;
      //   94: astore #4
      //   96: getstatic com/sun/java/util/jar/pack/PackerImpl$DoPack.$assertionsDisabled : Z
      //   99: ifne -> 134
      //   102: new java/util/ArrayList
      //   105: dup
      //   106: aload_0
      //   107: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   110: getfield files : Ljava/util/ArrayList;
      //   113: invokespecial <init> : (Ljava/util/Collection;)V
      //   116: dup
      //   117: astore #4
      //   119: aload_0
      //   120: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   123: invokevirtual getClassStubs : ()Ljava/util/List;
      //   126: invokeinterface retainAll : (Ljava/util/Collection;)Z
      //   131: ifne -> 134
      //   134: getstatic com/sun/java/util/jar/pack/PackerImpl$DoPack.$assertionsDisabled : Z
      //   137: ifne -> 165
      //   140: aload #4
      //   142: aload_0
      //   143: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   146: invokevirtual getClassStubs : ()Ljava/util/List;
      //   149: invokeinterface equals : (Ljava/lang/Object;)Z
      //   154: ifne -> 165
      //   157: new java/lang/AssertionError
      //   160: dup
      //   161: invokespecial <init> : ()V
      //   164: athrow
      //   165: aload_0
      //   166: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   169: invokevirtual trimStubs : ()V
      //   172: aload_0
      //   173: getfield this$0 : Lcom/sun/java/util/jar/pack/PackerImpl;
      //   176: getfield props : Lcom/sun/java/util/jar/pack/PropMap;
      //   179: ldc 'com.sun.java.util.jar.pack.strip.debug'
      //   181: invokevirtual getBoolean : (Ljava/lang/String;)Z
      //   184: ifeq -> 196
      //   187: aload_0
      //   188: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   191: ldc 'Debug'
      //   193: invokevirtual stripAttributeKind : (Ljava/lang/String;)V
      //   196: aload_0
      //   197: getfield this$0 : Lcom/sun/java/util/jar/pack/PackerImpl;
      //   200: getfield props : Lcom/sun/java/util/jar/pack/PropMap;
      //   203: ldc 'com.sun.java.util.jar.pack.strip.compile'
      //   205: invokevirtual getBoolean : (Ljava/lang/String;)Z
      //   208: ifeq -> 220
      //   211: aload_0
      //   212: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   215: ldc 'Compile'
      //   217: invokevirtual stripAttributeKind : (Ljava/lang/String;)V
      //   220: aload_0
      //   221: getfield this$0 : Lcom/sun/java/util/jar/pack/PackerImpl;
      //   224: getfield props : Lcom/sun/java/util/jar/pack/PropMap;
      //   227: ldc 'com.sun.java.util.jar.pack.strip.constants'
      //   229: invokevirtual getBoolean : (Ljava/lang/String;)Z
      //   232: ifeq -> 244
      //   235: aload_0
      //   236: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   239: ldc 'Constant'
      //   241: invokevirtual stripAttributeKind : (Ljava/lang/String;)V
      //   244: aload_0
      //   245: getfield this$0 : Lcom/sun/java/util/jar/pack/PackerImpl;
      //   248: getfield props : Lcom/sun/java/util/jar/pack/PropMap;
      //   251: ldc 'com.sun.java.util.jar.pack.strip.exceptions'
      //   253: invokevirtual getBoolean : (Ljava/lang/String;)Z
      //   256: ifeq -> 268
      //   259: aload_0
      //   260: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   263: ldc 'Exceptions'
      //   265: invokevirtual stripAttributeKind : (Ljava/lang/String;)V
      //   268: aload_0
      //   269: getfield this$0 : Lcom/sun/java/util/jar/pack/PackerImpl;
      //   272: getfield props : Lcom/sun/java/util/jar/pack/PropMap;
      //   275: ldc 'com.sun.java.util.jar.pack.strip.innerclasses'
      //   277: invokevirtual getBoolean : (Ljava/lang/String;)Z
      //   280: ifeq -> 292
      //   283: aload_0
      //   284: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   287: ldc 'InnerClasses'
      //   289: invokevirtual stripAttributeKind : (Ljava/lang/String;)V
      //   292: new com/sun/java/util/jar/pack/PackageWriter
      //   295: dup
      //   296: aload_0
      //   297: getfield pkg : Lcom/sun/java/util/jar/pack/Package;
      //   300: aload_1
      //   301: invokespecial <init> : (Lcom/sun/java/util/jar/pack/Package;Ljava/io/OutputStream;)V
      //   304: astore #4
      //   306: aload #4
      //   308: iload_2
      //   309: putfield archiveNextCount : I
      //   312: aload #4
      //   314: invokevirtual write : ()V
      //   317: aload_1
      //   318: invokevirtual flush : ()V
      //   321: aload_0
      //   322: getfield verbose : I
      //   325: ifle -> 408
      //   328: aload #4
      //   330: getfield archiveSize0 : J
      //   333: aload #4
      //   335: getfield archiveSize1 : J
      //   338: ladd
      //   339: lstore #5
      //   341: aload_0
      //   342: dup
      //   343: getfield totalOutputSize : J
      //   346: lload #5
      //   348: ladd
      //   349: putfield totalOutputSize : J
      //   352: aload_0
      //   353: getfield segmentSize : J
      //   356: lstore #7
      //   358: getstatic com/sun/java/util/jar/pack/Utils.log : Lcom/sun/java/util/jar/pack/Utils$Pack200Logger;
      //   361: new java/lang/StringBuilder
      //   364: dup
      //   365: invokespecial <init> : ()V
      //   368: ldc 'Transmitted '
      //   370: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   373: iload_3
      //   374: invokevirtual append : (I)Ljava/lang/StringBuilder;
      //   377: ldc ' files of '
      //   379: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   382: lload #7
      //   384: invokevirtual append : (J)Ljava/lang/StringBuilder;
      //   387: ldc ' input bytes in a segment of '
      //   389: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   392: lload #5
      //   394: invokevirtual append : (J)Ljava/lang/StringBuilder;
      //   397: ldc ' bytes'
      //   399: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   402: invokevirtual toString : ()Ljava/lang/String;
      //   405: invokevirtual info : (Ljava/lang/String;)V
      //   408: return }
    
    List<InFile> scanJar(JarFile param1JarFile) throws IOException {
      ArrayList arrayList = new ArrayList();
      try {
        for (JarEntry jarEntry : Collections.list(param1JarFile.entries())) {
          InFile inFile = new InFile(param1JarFile, jarEntry);
          assert jarEntry.isDirectory() == inFile.name.endsWith("/");
          arrayList.add(inFile);
        } 
      } catch (IllegalStateException illegalStateException) {
        throw new IOException(illegalStateException.getLocalizedMessage(), illegalStateException);
      } 
      return arrayList;
    }
    
    final class InFile {
      final String name;
      
      final JarFile jf;
      
      final JarEntry je;
      
      final File f;
      
      int modtime = 0;
      
      int options;
      
      InFile(String param2String) {
        this.name = Utils.getJarEntryName(param2String);
        this.f = new File(param2String);
        this.jf = null;
        this.je = null;
        int i = getModtime(this.f.lastModified());
        if (PackerImpl.DoPack.this.keepModtime && i != 0) {
          this.modtime = i;
        } else if (PackerImpl.DoPack.this.latestModtime && i > this$0.pkg.default_modtime) {
          this$0.pkg.default_modtime = i;
        } 
      }
      
      InFile(JarFile param2JarFile, JarEntry param2JarEntry) {
        this.name = Utils.getJarEntryName(param2JarEntry.getName());
        this.f = null;
        this.jf = param2JarFile;
        this.je = param2JarEntry;
        int i = getModtime(param2JarEntry.getTime());
        if (PackerImpl.DoPack.this.keepModtime && i != 0) {
          this.modtime = i;
        } else if (PackerImpl.DoPack.this.latestModtime && i > this$0.pkg.default_modtime) {
          this$0.pkg.default_modtime = i;
        } 
        if (PackerImpl.DoPack.this.keepDeflateHint && param2JarEntry.getMethod() == 8)
          this.options |= 0x1; 
      }
      
      InFile(JarEntry param2JarEntry) { this(null, param2JarEntry); }
      
      long getInputLength() {
        long l = (this.je != null) ? this.je.getSize() : this.f.length();
        assert l >= 0L : this + ".len=" + l;
        return Math.max(0L, l) + this.name.length() + 5L;
      }
      
      int getModtime(long param2Long) {
        long l = (param2Long + 500L) / 1000L;
        if ((int)l == l)
          return (int)l; 
        Utils.log.warning("overflow in modtime for " + this.f);
        return 0;
      }
      
      void copyTo(Package.File param2File) {
        if (this.modtime != 0)
          param2File.modtime = this.modtime; 
        param2File.options |= this.options;
      }
      
      InputStream getInputStream() throws IOException { return (this.jf != null) ? this.jf.getInputStream(this.je) : new FileInputStream(this.f); }
      
      public String toString() { return this.name; }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\PackerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */