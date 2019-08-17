package sun.tools.jar;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.jar.Pack200;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import sun.misc.JarIndex;

public class Main {
  String program;
  
  PrintStream out;
  
  PrintStream err;
  
  String fname;
  
  String mname;
  
  String ename;
  
  String zname = "";
  
  String[] files;
  
  String rootjar = null;
  
  Map<String, File> entryMap = new HashMap();
  
  Set<File> entries = new LinkedHashSet();
  
  Set<String> paths = new HashSet();
  
  boolean cflag;
  
  boolean uflag;
  
  boolean xflag;
  
  boolean tflag;
  
  boolean vflag;
  
  boolean flag0;
  
  boolean Mflag;
  
  boolean iflag;
  
  boolean nflag;
  
  boolean pflag;
  
  static final String MANIFEST_DIR = "META-INF/";
  
  static final String VERSION = "1.0";
  
  private static ResourceBundle rsrc;
  
  private static final boolean useExtractionTime = Boolean.getBoolean("sun.tools.jar.useExtractionTime");
  
  private boolean ok;
  
  private byte[] copyBuf = new byte[8192];
  
  private HashSet<String> jarPaths = new HashSet();
  
  private String getMsg(String paramString) {
    try {
      return rsrc.getString(paramString);
    } catch (MissingResourceException missingResourceException) {
      throw new Error("Error in message file");
    } 
  }
  
  private String formatMsg(String paramString1, String paramString2) {
    String str = getMsg(paramString1);
    String[] arrayOfString = new String[1];
    arrayOfString[0] = paramString2;
    return MessageFormat.format(str, (Object[])arrayOfString);
  }
  
  private String formatMsg2(String paramString1, String paramString2, String paramString3) {
    String str = getMsg(paramString1);
    String[] arrayOfString = new String[2];
    arrayOfString[0] = paramString2;
    arrayOfString[1] = paramString3;
    return MessageFormat.format(str, (Object[])arrayOfString);
  }
  
  public Main(PrintStream paramPrintStream1, PrintStream paramPrintStream2, String paramString) {
    this.out = paramPrintStream1;
    this.err = paramPrintStream2;
    this.program = paramString;
  }
  
  private static File createTempFileInSameDirectoryAs(File paramFile) throws IOException {
    File file = paramFile.getParentFile();
    if (file == null)
      file = new File("."); 
    return File.createTempFile("jartmp", null, file);
  }
  
  public boolean run(String[] paramArrayOfString) {
    this.ok = true;
    if (!parseArgs(paramArrayOfString))
      return false; 
    try {
      if ((this.cflag || this.uflag) && this.fname != null) {
        this.zname = this.fname.replace(File.separatorChar, '/');
        if (this.zname.startsWith("./"))
          this.zname = this.zname.substring(2); 
      } 
      if (this.cflag) {
        Manifest manifest = null;
        FileInputStream fileInputStream = null;
        if (!this.Mflag) {
          if (this.mname != null) {
            fileInputStream = new FileInputStream(this.mname);
            manifest = new Manifest(new BufferedInputStream(fileInputStream));
          } else {
            manifest = new Manifest();
          } 
          addVersion(manifest);
          addCreatedBy(manifest);
          if (isAmbiguousMainClass(manifest)) {
            if (fileInputStream != null)
              fileInputStream.close(); 
            return false;
          } 
          if (this.ename != null)
            addMainClass(manifest, this.ename); 
        } 
        expand(null, this.files, false);
        if (this.fname != null) {
          fileOutputStream1 = new FileOutputStream(this.fname);
        } else {
          fileOutputStream1 = new FileOutputStream(FileDescriptor.out);
          if (this.vflag)
            this.vflag = false; 
        } 
        file = null;
        FileOutputStream fileOutputStream2 = fileOutputStream1;
        String str = (this.fname == null) ? "tmpjar" : this.fname.substring(this.fname.indexOf(File.separatorChar) + 1);
        if (this.nflag) {
          file = createTemporaryFile(str, ".jar");
          fileOutputStream1 = new FileOutputStream(file);
        } 
        create(new BufferedOutputStream(fileOutputStream1, 4096), manifest);
        if (fileInputStream != null)
          fileInputStream.close(); 
        fileOutputStream1.close();
        if (this.nflag) {
          jarFile = null;
          file1 = null;
          jarOutputStream = null;
          try {
            Pack200.Packer packer = Pack200.newPacker();
            SortedMap sortedMap = packer.properties();
            sortedMap.put("pack.effort", "1");
            jarFile = new JarFile(file.getCanonicalPath());
            file1 = createTemporaryFile(str, ".pack");
            fileOutputStream1 = new FileOutputStream(file1);
            packer.pack(jarFile, fileOutputStream1);
            jarOutputStream = new JarOutputStream(fileOutputStream2);
            Pack200.Unpacker unpacker = Pack200.newUnpacker();
            unpacker.unpack(file1, jarOutputStream);
          } catch (IOException iOException) {
            fatalError(iOException);
          } finally {
            if (jarFile != null)
              jarFile.close(); 
            if (fileOutputStream1 != null)
              fileOutputStream1.close(); 
            if (jarOutputStream != null)
              jarOutputStream.close(); 
            if (file != null && file.exists())
              file.delete(); 
            if (file1 != null && file1.exists())
              file1.delete(); 
          } 
        } 
      } else if (this.uflag) {
        FileOutputStream fileOutputStream;
        FileInputStream fileInputStream1;
        File file1 = null;
        File file2 = null;
        if (this.fname != null) {
          file1 = new File(this.fname);
          file2 = createTempFileInSameDirectoryAs(file1);
          fileInputStream1 = new FileInputStream(file1);
          fileOutputStream = new FileOutputStream(file2);
        } else {
          fileInputStream1 = new FileInputStream(FileDescriptor.in);
          fileOutputStream = new FileOutputStream(FileDescriptor.out);
          this.vflag = false;
        } 
        FileInputStream fileInputStream2 = (!this.Mflag && this.mname != null) ? new FileInputStream(this.mname) : null;
        expand(null, this.files, true);
        boolean bool = update(fileInputStream1, new BufferedOutputStream(fileOutputStream), fileInputStream2, null);
        if (this.ok)
          this.ok = bool; 
        fileInputStream1.close();
        fileOutputStream.close();
        if (fileInputStream2 != null)
          fileInputStream2.close(); 
        if (this.ok && this.fname != null) {
          file1.delete();
          if (!file2.renameTo(file1)) {
            file2.delete();
            throw new IOException(getMsg("error.write.file"));
          } 
          file2.delete();
        } 
      } else if (this.tflag) {
        replaceFSC(this.files);
        if (this.fname != null) {
          list(this.fname, this.files);
        } else {
          fileInputStream = new FileInputStream(FileDescriptor.in);
          try {
            list(new BufferedInputStream(fileInputStream), this.files);
          } finally {
            fileInputStream.close();
          } 
        } 
      } else if (this.xflag) {
        replaceFSC(this.files);
        if (this.fname != null && this.files != null) {
          extract(this.fname, this.files);
        } else {
          fileInputStream = (this.fname == null) ? new FileInputStream(FileDescriptor.in) : new FileInputStream(this.fname);
          try {
            extract(new BufferedInputStream(fileInputStream), this.files);
          } finally {
            fileInputStream.close();
          } 
        } 
      } else if (this.iflag) {
        genIndex(this.rootjar, this.files);
      } 
    } catch (IOException iOException) {
      fatalError(iOException);
      this.ok = false;
    } catch (Error error) {
      error.printStackTrace();
      this.ok = false;
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      this.ok = false;
    } 
    this.out.flush();
    this.err.flush();
    return this.ok;
  }
  
  boolean parseArgs(String[] paramArrayOfString) {
    try {
      paramArrayOfString = CommandLine.parse(paramArrayOfString);
    } catch (FileNotFoundException fileNotFoundException) {
      fatalError(formatMsg("error.cant.open", fileNotFoundException.getMessage()));
      return false;
    } catch (IOException iOException) {
      fatalError(iOException);
      return false;
    } 
    int i = 1;
    try {
      String str = paramArrayOfString[0];
      if (str.startsWith("-"))
        str = str.substring(1); 
      for (byte b = 0; b < str.length(); b++) {
        switch (str.charAt(b)) {
          case 'c':
            if (this.xflag || this.tflag || this.uflag || this.iflag) {
              usageError();
              return false;
            } 
            this.cflag = true;
            break;
          case 'u':
            if (this.cflag || this.xflag || this.tflag || this.iflag) {
              usageError();
              return false;
            } 
            this.uflag = true;
            break;
          case 'x':
            if (this.cflag || this.uflag || this.tflag || this.iflag) {
              usageError();
              return false;
            } 
            this.xflag = true;
            break;
          case 't':
            if (this.cflag || this.uflag || this.xflag || this.iflag) {
              usageError();
              return false;
            } 
            this.tflag = true;
            break;
          case 'M':
            this.Mflag = true;
            break;
          case 'v':
            this.vflag = true;
            break;
          case 'f':
            this.fname = paramArrayOfString[i++];
            break;
          case 'm':
            this.mname = paramArrayOfString[i++];
            break;
          case '0':
            this.flag0 = true;
            break;
          case 'i':
            if (this.cflag || this.uflag || this.xflag || this.tflag) {
              usageError();
              return false;
            } 
            this.rootjar = paramArrayOfString[i++];
            this.iflag = true;
            break;
          case 'n':
            this.nflag = true;
            break;
          case 'e':
            this.ename = paramArrayOfString[i++];
            break;
          case 'P':
            this.pflag = true;
            break;
          default:
            error(formatMsg("error.illegal.option", String.valueOf(str.charAt(b))));
            usageError();
            return false;
        } 
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      usageError();
      return false;
    } 
    if (!this.cflag && !this.tflag && !this.xflag && !this.uflag && !this.iflag) {
      error(getMsg("error.bad.option"));
      usageError();
      return false;
    } 
    int j = paramArrayOfString.length - i;
    if (j > 0) {
      byte b = 0;
      String[] arrayOfString = new String[j];
      try {
        for (int k = i; k < paramArrayOfString.length; k++) {
          if (paramArrayOfString[k].equals("-C")) {
            String str = paramArrayOfString[++k];
            str = str.endsWith(File.separator) ? str : (str + File.separator);
            for (str = str.replace(File.separatorChar, '/'); str.indexOf("//") > -1; str = str.replace("//", "/"));
            this.paths.add(str.replace(File.separatorChar, '/'));
            arrayOfString[b++] = str + paramArrayOfString[++k];
          } else {
            arrayOfString[b++] = paramArrayOfString[k];
          } 
        } 
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        usageError();
        return false;
      } 
      this.files = new String[b];
      System.arraycopy(arrayOfString, 0, this.files, 0, b);
    } else {
      if (this.cflag && this.mname == null) {
        error(getMsg("error.bad.cflag"));
        usageError();
        return false;
      } 
      if (this.uflag) {
        if (this.mname != null || this.ename != null)
          return true; 
        error(getMsg("error.bad.uflag"));
        usageError();
        return false;
      } 
    } 
    return true;
  }
  
  void expand(File paramFile, String[] paramArrayOfString, boolean paramBoolean) {
    if (paramArrayOfString == null)
      return; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      File file;
      if (paramFile == null) {
        file = new File(paramArrayOfString[b]);
      } else {
        file = new File(paramFile, paramArrayOfString[b]);
      } 
      if (file.isFile()) {
        if (this.entries.add(file) && paramBoolean)
          this.entryMap.put(entryName(file.getPath()), file); 
      } else if (file.isDirectory()) {
        if (this.entries.add(file)) {
          if (paramBoolean) {
            String str = file.getPath();
            str = str.endsWith(File.separator) ? str : (str + File.separator);
            this.entryMap.put(entryName(str), file);
          } 
          expand(file, file.list(), paramBoolean);
        } 
      } else {
        error(formatMsg("error.nosuch.fileordir", String.valueOf(file)));
        this.ok = false;
      } 
    } 
  }
  
  void create(OutputStream paramOutputStream, Manifest paramManifest) throws IOException {
    JarOutputStream jarOutputStream = new JarOutputStream(paramOutputStream);
    if (this.flag0)
      jarOutputStream.setMethod(0); 
    if (paramManifest != null) {
      if (this.vflag)
        output(getMsg("out.added.manifest")); 
      ZipEntry zipEntry = new ZipEntry("META-INF/");
      zipEntry.setTime(System.currentTimeMillis());
      zipEntry.setSize(0L);
      zipEntry.setCrc(0L);
      jarOutputStream.putNextEntry(zipEntry);
      zipEntry = new ZipEntry("META-INF/MANIFEST.MF");
      zipEntry.setTime(System.currentTimeMillis());
      if (this.flag0)
        crc32Manifest(zipEntry, paramManifest); 
      jarOutputStream.putNextEntry(zipEntry);
      paramManifest.write(jarOutputStream);
      jarOutputStream.closeEntry();
    } 
    for (File file : this.entries)
      addFile(jarOutputStream, file); 
    jarOutputStream.close();
  }
  
  private char toUpperCaseASCII(char paramChar) { return (paramChar < 'a' || paramChar > 'z') ? paramChar : (char)(paramChar + 'A' - 'a'); }
  
  private boolean equalsIgnoreCase(String paramString1, String paramString2) {
    assert paramString2.toUpperCase(Locale.ENGLISH).equals(paramString2);
    int i;
    if ((i = paramString1.length()) != paramString2.length())
      return false; 
    for (byte b = 0; b < i; b++) {
      char c1 = paramString1.charAt(b);
      char c2 = paramString2.charAt(b);
      if (c1 != c2 && toUpperCaseASCII(c1) != c2)
        return false; 
    } 
    return true;
  }
  
  boolean update(InputStream paramInputStream1, OutputStream paramOutputStream, InputStream paramInputStream2, JarIndex paramJarIndex) throws IOException {
    ZipInputStream zipInputStream = new ZipInputStream(paramInputStream1);
    JarOutputStream jarOutputStream = new JarOutputStream(paramOutputStream);
    ZipEntry zipEntry = null;
    boolean bool = false;
    boolean bool1 = true;
    if (paramJarIndex != null)
      addIndex(paramJarIndex, jarOutputStream); 
    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
      String str = zipEntry.getName();
      boolean bool2 = equalsIgnoreCase(str, "META-INF/MANIFEST.MF");
      if ((paramJarIndex != null && equalsIgnoreCase(str, "META-INF/INDEX.LIST")) || (this.Mflag && bool2))
        continue; 
      if (bool2 && (paramInputStream2 != null || this.ename != null)) {
        bool = true;
        if (paramInputStream2 != null) {
          FileInputStream fileInputStream = new FileInputStream(this.mname);
          boolean bool3 = isAmbiguousMainClass(new Manifest(fileInputStream));
          fileInputStream.close();
          if (bool3)
            return false; 
        } 
        Manifest manifest = new Manifest(zipInputStream);
        if (paramInputStream2 != null)
          manifest.read(paramInputStream2); 
        if (!updateManifest(manifest, jarOutputStream))
          return false; 
        continue;
      } 
      if (!this.entryMap.containsKey(str)) {
        ZipEntry zipEntry1 = new ZipEntry(str);
        zipEntry1.setMethod(zipEntry.getMethod());
        zipEntry1.setTime(zipEntry.getTime());
        zipEntry1.setComment(zipEntry.getComment());
        zipEntry1.setExtra(zipEntry.getExtra());
        if (zipEntry.getMethod() == 0) {
          zipEntry1.setSize(zipEntry.getSize());
          zipEntry1.setCrc(zipEntry.getCrc());
        } 
        jarOutputStream.putNextEntry(zipEntry1);
        copy(zipInputStream, jarOutputStream);
        continue;
      } 
      File file = (File)this.entryMap.get(str);
      addFile(jarOutputStream, file);
      this.entryMap.remove(str);
      this.entries.remove(file);
    } 
    for (File file : this.entries)
      addFile(jarOutputStream, file); 
    if (!bool)
      if (paramInputStream2 != null) {
        Manifest manifest = new Manifest(paramInputStream2);
        bool1 = !isAmbiguousMainClass(manifest);
        if (bool1 && !updateManifest(manifest, jarOutputStream))
          bool1 = false; 
      } else if (this.ename != null && !updateManifest(new Manifest(), jarOutputStream)) {
        bool1 = false;
      }  
    zipInputStream.close();
    jarOutputStream.close();
    return bool1;
  }
  
  private void addIndex(JarIndex paramJarIndex, ZipOutputStream paramZipOutputStream) throws IOException {
    ZipEntry zipEntry = new ZipEntry("META-INF/INDEX.LIST");
    zipEntry.setTime(System.currentTimeMillis());
    if (this.flag0) {
      CRC32OutputStream cRC32OutputStream = new CRC32OutputStream();
      paramJarIndex.write(cRC32OutputStream);
      cRC32OutputStream.updateEntry(zipEntry);
    } 
    paramZipOutputStream.putNextEntry(zipEntry);
    paramJarIndex.write(paramZipOutputStream);
    paramZipOutputStream.closeEntry();
  }
  
  private boolean updateManifest(Manifest paramManifest, ZipOutputStream paramZipOutputStream) throws IOException {
    addVersion(paramManifest);
    addCreatedBy(paramManifest);
    if (this.ename != null)
      addMainClass(paramManifest, this.ename); 
    ZipEntry zipEntry = new ZipEntry("META-INF/MANIFEST.MF");
    zipEntry.setTime(System.currentTimeMillis());
    if (this.flag0)
      crc32Manifest(zipEntry, paramManifest); 
    paramZipOutputStream.putNextEntry(zipEntry);
    paramManifest.write(paramZipOutputStream);
    if (this.vflag)
      output(getMsg("out.update.manifest")); 
    return true;
  }
  
  private static final boolean isWinDriveLetter(char paramChar) { return ((paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'A' && paramChar <= 'Z')); }
  
  private String safeName(String paramString) {
    if (!this.pflag) {
      int i = paramString.length();
      int j = paramString.lastIndexOf("../");
      if (j == -1) {
        j = 0;
      } else {
        j += 3;
      } 
      if (File.separatorChar == '\\') {
        while (j < i) {
          int k = j;
          if (j + 1 < i && paramString.charAt(j + 1) == ':' && isWinDriveLetter(paramString.charAt(j)))
            j += 2; 
          while (j < i && paramString.charAt(j) == '/')
            j++; 
          if (j == k)
            break; 
        } 
      } else {
        while (j < i && paramString.charAt(j) == '/')
          j++; 
      } 
      if (j != 0)
        paramString = paramString.substring(j); 
    } 
    return paramString;
  }
  
  private String entryName(String paramString) {
    paramString = paramString.replace(File.separatorChar, '/');
    String str = "";
    for (String str1 : this.paths) {
      if (paramString.startsWith(str1) && str1.length() > str.length())
        str = str1; 
    } 
    paramString = paramString.substring(str.length());
    paramString = safeName(paramString);
    if (paramString.startsWith("./"))
      paramString = paramString.substring(2); 
    return paramString;
  }
  
  private void addVersion(Manifest paramManifest) {
    Attributes attributes = paramManifest.getMainAttributes();
    if (attributes.getValue(Attributes.Name.MANIFEST_VERSION) == null)
      attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0"); 
  }
  
  private void addCreatedBy(Manifest paramManifest) {
    Attributes attributes = paramManifest.getMainAttributes();
    if (attributes.getValue(new Attributes.Name("Created-By")) == null) {
      String str1 = System.getProperty("java.vendor");
      String str2 = System.getProperty("java.version");
      attributes.put(new Attributes.Name("Created-By"), str2 + " (" + str1 + ")");
    } 
  }
  
  private void addMainClass(Manifest paramManifest, String paramString) {
    Attributes attributes = paramManifest.getMainAttributes();
    attributes.put(Attributes.Name.MAIN_CLASS, paramString);
  }
  
  private boolean isAmbiguousMainClass(Manifest paramManifest) {
    if (this.ename != null) {
      Attributes attributes = paramManifest.getMainAttributes();
      if (attributes.get(Attributes.Name.MAIN_CLASS) != null) {
        error(getMsg("error.bad.eflag"));
        usageError();
        return true;
      } 
    } 
    return false;
  }
  
  void addFile(ZipOutputStream paramZipOutputStream, File paramFile) throws IOException {
    String str = paramFile.getPath();
    boolean bool = paramFile.isDirectory();
    if (bool)
      str = str.endsWith(File.separator) ? str : (str + File.separator); 
    str = entryName(str);
    if (str.equals("") || str.equals(".") || str.equals(this.zname))
      return; 
    if ((str.equals("META-INF/") || str.equals("META-INF/MANIFEST.MF")) && !this.Mflag) {
      if (this.vflag)
        output(formatMsg("out.ignore.entry", str)); 
      return;
    } 
    long l = bool ? 0L : paramFile.length();
    if (this.vflag)
      this.out.print(formatMsg("out.adding", str)); 
    ZipEntry zipEntry = new ZipEntry(str);
    zipEntry.setTime(paramFile.lastModified());
    if (l == 0L) {
      zipEntry.setMethod(0);
      zipEntry.setSize(0L);
      zipEntry.setCrc(0L);
    } else if (this.flag0) {
      crc32File(zipEntry, paramFile);
    } 
    paramZipOutputStream.putNextEntry(zipEntry);
    if (!bool)
      copy(paramFile, paramZipOutputStream); 
    paramZipOutputStream.closeEntry();
    if (this.vflag) {
      l = zipEntry.getSize();
      long l1 = zipEntry.getCompressedSize();
      this.out.print(formatMsg2("out.size", String.valueOf(l), String.valueOf(l1)));
      if (zipEntry.getMethod() == 8) {
        long l2 = 0L;
        if (l != 0L)
          l2 = (l - l1) * 100L / l; 
        output(formatMsg("out.deflated", String.valueOf(l2)));
      } else {
        output(getMsg("out.stored"));
      } 
    } 
  }
  
  private void copy(InputStream paramInputStream, OutputStream paramOutputStream) throws IOException {
    int i;
    while ((i = paramInputStream.read(this.copyBuf)) != -1)
      paramOutputStream.write(this.copyBuf, 0, i); 
  }
  
  private void copy(File paramFile, OutputStream paramOutputStream) throws IOException {
    fileInputStream = new FileInputStream(paramFile);
    try {
      copy(fileInputStream, paramOutputStream);
    } finally {
      fileInputStream.close();
    } 
  }
  
  private void copy(InputStream paramInputStream, File paramFile) throws IOException {
    fileOutputStream = new FileOutputStream(paramFile);
    try {
      copy(paramInputStream, fileOutputStream);
    } finally {
      fileOutputStream.close();
    } 
  }
  
  private void crc32Manifest(ZipEntry paramZipEntry, Manifest paramManifest) throws IOException {
    CRC32OutputStream cRC32OutputStream = new CRC32OutputStream();
    paramManifest.write(cRC32OutputStream);
    cRC32OutputStream.updateEntry(paramZipEntry);
  }
  
  private void crc32File(ZipEntry paramZipEntry, File paramFile) throws IOException {
    CRC32OutputStream cRC32OutputStream = new CRC32OutputStream();
    copy(paramFile, cRC32OutputStream);
    if (cRC32OutputStream.n != paramFile.length())
      throw new JarException(formatMsg("error.incorrect.length", paramFile.getPath())); 
    cRC32OutputStream.updateEntry(paramZipEntry);
  }
  
  void replaceFSC(String[] paramArrayOfString) {
    if (paramArrayOfString != null)
      for (byte b = 0; b < paramArrayOfString.length; b++)
        paramArrayOfString[b] = paramArrayOfString[b].replace(File.separatorChar, '/');  
  }
  
  Set<ZipEntry> newDirSet() { return new HashSet<ZipEntry>() {
        public boolean add(ZipEntry param1ZipEntry) { return (param1ZipEntry == null || useExtractionTime) ? false : super.add(param1ZipEntry); }
      }; }
  
  void updateLastModifiedTime(Set<ZipEntry> paramSet) throws IOException {
    for (ZipEntry zipEntry : paramSet) {
      long l = zipEntry.getTime();
      if (l != -1L) {
        String str = safeName(zipEntry.getName().replace(File.separatorChar, '/'));
        if (str.length() != 0) {
          File file = new File(str.replace('/', File.separatorChar));
          file.setLastModified(l);
        } 
      } 
    } 
  }
  
  void extract(InputStream paramInputStream, String[] paramArrayOfString) throws IOException {
    ZipInputStream zipInputStream = new ZipInputStream(paramInputStream);
    Set set = newDirSet();
    ZipEntry zipEntry;
    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
      if (paramArrayOfString == null) {
        set.add(extractFile(zipInputStream, zipEntry));
        continue;
      } 
      String str = zipEntry.getName();
      for (String str1 : paramArrayOfString) {
        if (str.startsWith(str1)) {
          set.add(extractFile(zipInputStream, zipEntry));
          break;
        } 
      } 
    } 
    updateLastModifiedTime(set);
  }
  
  void extract(String paramString, String[] paramArrayOfString) throws IOException {
    ZipFile zipFile = new ZipFile(paramString);
    Set set = newDirSet();
    Enumeration enumeration = zipFile.entries();
    while (enumeration.hasMoreElements()) {
      ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
      if (paramArrayOfString == null) {
        set.add(extractFile(zipFile.getInputStream(zipEntry), zipEntry));
        continue;
      } 
      String str = zipEntry.getName();
      for (String str1 : paramArrayOfString) {
        if (str.startsWith(str1)) {
          set.add(extractFile(zipFile.getInputStream(zipEntry), zipEntry));
          break;
        } 
      } 
    } 
    zipFile.close();
    updateLastModifiedTime(set);
  }
  
  ZipEntry extractFile(InputStream paramInputStream, ZipEntry paramZipEntry) throws IOException {
    ZipEntry zipEntry = null;
    String str = safeName(paramZipEntry.getName().replace(File.separatorChar, '/'));
    if (str.length() == 0)
      return zipEntry; 
    File file = new File(str.replace('/', File.separatorChar));
    if (paramZipEntry.isDirectory()) {
      if (file.exists()) {
        if (!file.isDirectory())
          throw new IOException(formatMsg("error.create.dir", file.getPath())); 
      } else {
        if (!file.mkdirs())
          throw new IOException(formatMsg("error.create.dir", file.getPath())); 
        zipEntry = paramZipEntry;
      } 
      if (this.vflag)
        output(formatMsg("out.create", str)); 
    } else {
      if (file.getParent() != null) {
        File file1 = new File(file.getParent());
        if ((!file1.exists() && !file1.mkdirs()) || !file1.isDirectory())
          throw new IOException(formatMsg("error.create.dir", file1.getPath())); 
      } 
      try {
        copy(paramInputStream, file);
      } finally {
        if (paramInputStream instanceof ZipInputStream) {
          ((ZipInputStream)paramInputStream).closeEntry();
        } else {
          paramInputStream.close();
        } 
      } 
      if (this.vflag)
        if (paramZipEntry.getMethod() == 8) {
          output(formatMsg("out.inflated", str));
        } else {
          output(formatMsg("out.extracted", str));
        }  
    } 
    if (!useExtractionTime) {
      long l = paramZipEntry.getTime();
      if (l != -1L)
        file.setLastModified(l); 
    } 
    return zipEntry;
  }
  
  void list(InputStream paramInputStream, String[] paramArrayOfString) throws IOException {
    ZipInputStream zipInputStream = new ZipInputStream(paramInputStream);
    ZipEntry zipEntry;
    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
      zipInputStream.closeEntry();
      printEntry(zipEntry, paramArrayOfString);
    } 
  }
  
  void list(String paramString, String[] paramArrayOfString) throws IOException {
    ZipFile zipFile = new ZipFile(paramString);
    Enumeration enumeration = zipFile.entries();
    while (enumeration.hasMoreElements())
      printEntry((ZipEntry)enumeration.nextElement(), paramArrayOfString); 
    zipFile.close();
  }
  
  void dumpIndex(String paramString, JarIndex paramJarIndex) throws IOException {
    File file = new File(paramString);
    Path path1 = file.toPath();
    path2 = createTempFileInSameDirectoryAs(file).toPath();
    try {
      if (update(Files.newInputStream(path1, new java.nio.file.OpenOption[0]), Files.newOutputStream(path2, new java.nio.file.OpenOption[0]), null, paramJarIndex))
        try {
          Files.move(path2, path1, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
        } catch (IOException iOException) {
          throw new IOException(getMsg("error.write.file"), iOException);
        }  
    } finally {
      Files.deleteIfExists(path2);
    } 
  }
  
  List<String> getJarPath(String paramString) throws IOException {
    ArrayList arrayList = new ArrayList();
    arrayList.add(paramString);
    this.jarPaths.add(paramString);
    String str = paramString.substring(0, Math.max(0, paramString.lastIndexOf('/') + 1));
    JarFile jarFile = new JarFile(paramString.replace('/', File.separatorChar));
    if (jarFile != null) {
      Manifest manifest = jarFile.getManifest();
      if (manifest != null) {
        Attributes attributes = manifest.getMainAttributes();
        if (attributes != null) {
          String str1 = attributes.getValue(Attributes.Name.CLASS_PATH);
          if (str1 != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(str1);
            while (stringTokenizer.hasMoreTokens()) {
              String str2 = stringTokenizer.nextToken();
              if (!str2.endsWith("/")) {
                str2 = str.concat(str2);
                if (!this.jarPaths.contains(str2))
                  arrayList.addAll(getJarPath(str2)); 
              } 
            } 
          } 
        } 
      } 
    } 
    jarFile.close();
    return arrayList;
  }
  
  void genIndex(String paramString, String[] paramArrayOfString) throws IOException {
    List list = getJarPath(paramString);
    int i = list.size();
    if (i == 1 && paramArrayOfString != null) {
      for (byte b = 0; b < paramArrayOfString.length; b++)
        list.addAll(getJarPath(paramArrayOfString[b])); 
      i = list.size();
    } 
    String[] arrayOfString = (String[])list.toArray(new String[i]);
    JarIndex jarIndex = new JarIndex(arrayOfString);
    dumpIndex(paramString, jarIndex);
  }
  
  void printEntry(ZipEntry paramZipEntry, String[] paramArrayOfString) throws IOException {
    if (paramArrayOfString == null) {
      printEntry(paramZipEntry);
    } else {
      String str = paramZipEntry.getName();
      for (String str1 : paramArrayOfString) {
        if (str.startsWith(str1)) {
          printEntry(paramZipEntry);
          return;
        } 
      } 
    } 
  }
  
  void printEntry(ZipEntry paramZipEntry) throws IOException {
    if (this.vflag) {
      StringBuilder stringBuilder = new StringBuilder();
      String str = Long.toString(paramZipEntry.getSize());
      for (int i = 6 - str.length(); i > 0; i--)
        stringBuilder.append(' '); 
      stringBuilder.append(str).append(' ').append((new Date(paramZipEntry.getTime())).toString());
      stringBuilder.append(' ').append(paramZipEntry.getName());
      output(stringBuilder.toString());
    } else {
      output(paramZipEntry.getName());
    } 
  }
  
  void usageError() { error(getMsg("usage")); }
  
  void fatalError(Exception paramException) { paramException.printStackTrace(); }
  
  void fatalError(String paramString) { error(this.program + ": " + paramString); }
  
  protected void output(String paramString) { this.out.println(paramString); }
  
  protected void error(String paramString) { this.err.println(paramString); }
  
  public static void main(String[] paramArrayOfString) {
    Main main = new Main(System.out, System.err, "jar");
    System.exit(main.run(paramArrayOfString) ? 0 : 1);
  }
  
  private File createTemporaryFile(String paramString1, String paramString2) {
    File file = null;
    try {
      file = File.createTempFile(paramString1, paramString2);
    } catch (IOException|SecurityException iOException) {}
    if (file == null)
      if (this.fname != null) {
        try {
          File file1;
          file = (file1 = (new File(this.fname)).getAbsoluteFile().getParentFile()).createTempFile(this.fname, ".tmp" + paramString2, file1);
        } catch (IOException iOException) {
          fatalError(iOException);
        } 
      } else {
        fatalError(new IOException(getMsg("error.create.tempfile")));
      }  
    return file;
  }
  
  static  {
    try {
      rsrc = ResourceBundle.getBundle("sun.tools.jar.resources.jar");
    } catch (MissingResourceException missingResourceException) {
      throw new Error("Fatal: Resource for jar is missing");
    } 
  }
  
  private static class CRC32OutputStream extends OutputStream {
    final CRC32 crc = new CRC32();
    
    long n = 0L;
    
    public void write(int param1Int) throws IOException {
      this.crc.update(param1Int);
      this.n++;
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      this.crc.update(param1ArrayOfByte, param1Int1, param1Int2);
      this.n += param1Int2;
    }
    
    public void updateEntry(ZipEntry param1ZipEntry) throws IOException {
      param1ZipEntry.setMethod(0);
      param1ZipEntry.setSize(this.n);
      param1ZipEntry.setCrc(this.crc.getValue());
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tools\jar\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */