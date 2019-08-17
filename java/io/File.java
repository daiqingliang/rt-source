package java.io;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.ArrayList;
import sun.misc.Unsafe;
import sun.security.action.GetPropertyAction;

public class File extends Object implements Serializable, Comparable<File> {
  private static final FileSystem fs = DefaultFileSystem.getFileSystem();
  
  private final String path;
  
  private PathStatus status = null;
  
  private final int prefixLength;
  
  public static final char separatorChar = fs.getSeparator();
  
  public static final String separator = "" + separatorChar;
  
  public static final char pathSeparatorChar = fs.getPathSeparator();
  
  public static final String pathSeparator = "" + pathSeparatorChar;
  
  private static final long PATH_OFFSET;
  
  private static final long PREFIX_LENGTH_OFFSET;
  
  private static final Unsafe UNSAFE;
  
  private static final long serialVersionUID = 301077366599181567L;
  
  final boolean isInvalid() {
    if (this.status == null)
      this.status = (this.path.indexOf(false) < 0) ? PathStatus.CHECKED : PathStatus.INVALID; 
    return (this.status == PathStatus.INVALID);
  }
  
  int getPrefixLength() { return this.prefixLength; }
  
  private File(String paramString, int paramInt) {
    this.path = paramString;
    this.prefixLength = paramInt;
  }
  
  private File(String paramString, File paramFile) {
    assert paramFile.path != null;
    assert !paramFile.path.equals("");
    this.path = fs.resolve(paramFile.path, paramString);
    this.prefixLength = paramFile.prefixLength;
  }
  
  public File(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.path = fs.normalize(paramString);
    this.prefixLength = fs.prefixLength(this.path);
  }
  
  public File(String paramString1, String paramString2) {
    if (paramString2 == null)
      throw new NullPointerException(); 
    if (paramString1 != null) {
      if (paramString1.equals("")) {
        this.path = fs.resolve(fs.getDefaultParent(), fs.normalize(paramString2));
      } else {
        this.path = fs.resolve(fs.normalize(paramString1), fs.normalize(paramString2));
      } 
    } else {
      this.path = fs.normalize(paramString2);
    } 
    this.prefixLength = fs.prefixLength(this.path);
  }
  
  public File(File paramFile, String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    if (paramFile != null) {
      if (paramFile.path.equals("")) {
        this.path = fs.resolve(fs.getDefaultParent(), fs.normalize(paramString));
      } else {
        this.path = fs.resolve(paramFile.path, fs.normalize(paramString));
      } 
    } else {
      this.path = fs.normalize(paramString);
    } 
    this.prefixLength = fs.prefixLength(this.path);
  }
  
  public File(URI paramURI) {
    if (!paramURI.isAbsolute())
      throw new IllegalArgumentException("URI is not absolute"); 
    if (paramURI.isOpaque())
      throw new IllegalArgumentException("URI is not hierarchical"); 
    String str1 = paramURI.getScheme();
    if (str1 == null || !str1.equalsIgnoreCase("file"))
      throw new IllegalArgumentException("URI scheme is not \"file\""); 
    if (paramURI.getAuthority() != null)
      throw new IllegalArgumentException("URI has an authority component"); 
    if (paramURI.getFragment() != null)
      throw new IllegalArgumentException("URI has a fragment component"); 
    if (paramURI.getQuery() != null)
      throw new IllegalArgumentException("URI has a query component"); 
    String str2 = paramURI.getPath();
    if (str2.equals(""))
      throw new IllegalArgumentException("URI path component is empty"); 
    str2 = fs.fromURIPath(str2);
    if (separatorChar != '/')
      str2 = str2.replace('/', separatorChar); 
    this.path = fs.normalize(str2);
    this.prefixLength = fs.prefixLength(this.path);
  }
  
  public String getName() {
    int i = this.path.lastIndexOf(separatorChar);
    return (i < this.prefixLength) ? this.path.substring(this.prefixLength) : this.path.substring(i + 1);
  }
  
  public String getParent() {
    int i = this.path.lastIndexOf(separatorChar);
    return (i < this.prefixLength) ? ((this.prefixLength > 0 && this.path.length() > this.prefixLength) ? this.path.substring(0, this.prefixLength) : null) : this.path.substring(0, i);
  }
  
  public File getParentFile() {
    String str = getParent();
    return (str == null) ? null : new File(str, this.prefixLength);
  }
  
  public String getPath() { return this.path; }
  
  public boolean isAbsolute() { return fs.isAbsolute(this); }
  
  public String getAbsolutePath() { return fs.resolve(this); }
  
  public File getAbsoluteFile() {
    String str = getAbsolutePath();
    return new File(str, fs.prefixLength(str));
  }
  
  public String getCanonicalPath() {
    if (isInvalid())
      throw new IOException("Invalid file path"); 
    return fs.canonicalize(fs.resolve(this));
  }
  
  public File getCanonicalFile() {
    String str = getCanonicalPath();
    return new File(str, fs.prefixLength(str));
  }
  
  private static String slashify(String paramString, boolean paramBoolean) {
    String str = paramString;
    if (separatorChar != '/')
      str = str.replace(separatorChar, '/'); 
    if (!str.startsWith("/"))
      str = "/" + str; 
    if (!str.endsWith("/") && paramBoolean)
      str = str + "/"; 
    return str;
  }
  
  @Deprecated
  public URL toURL() throws MalformedURLException {
    if (isInvalid())
      throw new MalformedURLException("Invalid file path"); 
    return new URL("file", "", slashify(getAbsolutePath(), isDirectory()));
  }
  
  public URI toURI() {
    try {
      File file;
      String str = (file = getAbsoluteFile()).slashify(file.getPath(), file.isDirectory());
      if (str.startsWith("//"))
        str = "//" + str; 
      return new URI("file", null, str, null);
    } catch (URISyntaxException uRISyntaxException) {
      throw new Error(uRISyntaxException);
    } 
  }
  
  public boolean canRead() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(this.path); 
    return isInvalid() ? false : fs.checkAccess(this, 4);
  }
  
  public boolean canWrite() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(this.path); 
    return isInvalid() ? false : fs.checkAccess(this, 2);
  }
  
  public boolean exists() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(this.path); 
    return isInvalid() ? false : (((fs.getBooleanAttributes(this) & true) != 0));
  }
  
  public boolean isDirectory() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(this.path); 
    return isInvalid() ? false : (((fs.getBooleanAttributes(this) & 0x4) != 0));
  }
  
  public boolean isFile() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(this.path); 
    return isInvalid() ? false : (((fs.getBooleanAttributes(this) & 0x2) != 0));
  }
  
  public boolean isHidden() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(this.path); 
    return isInvalid() ? false : (((fs.getBooleanAttributes(this) & 0x8) != 0));
  }
  
  public long lastModified() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(this.path); 
    return isInvalid() ? 0L : fs.getLastModifiedTime(this);
  }
  
  public long length() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(this.path); 
    return isInvalid() ? 0L : fs.getLength(this);
  }
  
  public boolean createNewFile() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(this.path); 
    if (isInvalid())
      throw new IOException("Invalid file path"); 
    return fs.createFileExclusively(this.path);
  }
  
  public boolean delete() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkDelete(this.path); 
    return isInvalid() ? false : fs.delete(this);
  }
  
  public void deleteOnExit() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkDelete(this.path); 
    if (isInvalid())
      return; 
    DeleteOnExitHook.add(this.path);
  }
  
  public String[] list() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(this.path); 
    return isInvalid() ? null : fs.list(this);
  }
  
  public String[] list(FilenameFilter paramFilenameFilter) {
    String[] arrayOfString = list();
    if (arrayOfString == null || paramFilenameFilter == null)
      return arrayOfString; 
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramFilenameFilter.accept(this, arrayOfString[b]))
        arrayList.add(arrayOfString[b]); 
    } 
    return (String[])arrayList.toArray(new String[arrayList.size()]);
  }
  
  public File[] listFiles() {
    String[] arrayOfString = list();
    if (arrayOfString == null)
      return null; 
    int i = arrayOfString.length;
    File[] arrayOfFile = new File[i];
    for (byte b = 0; b < i; b++)
      arrayOfFile[b] = new File(arrayOfString[b], this); 
    return arrayOfFile;
  }
  
  public File[] listFiles(FilenameFilter paramFilenameFilter) {
    String[] arrayOfString = list();
    if (arrayOfString == null)
      return null; 
    ArrayList arrayList = new ArrayList();
    for (String str : arrayOfString) {
      if (paramFilenameFilter == null || paramFilenameFilter.accept(this, str))
        arrayList.add(new File(str, this)); 
    } 
    return (File[])arrayList.toArray(new File[arrayList.size()]);
  }
  
  public File[] listFiles(FileFilter paramFileFilter) {
    String[] arrayOfString = list();
    if (arrayOfString == null)
      return null; 
    ArrayList arrayList = new ArrayList();
    for (String str : arrayOfString) {
      File file = new File(str, this);
      if (paramFileFilter == null || paramFileFilter.accept(file))
        arrayList.add(file); 
    } 
    return (File[])arrayList.toArray(new File[arrayList.size()]);
  }
  
  public boolean mkdir() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(this.path); 
    return isInvalid() ? false : fs.createDirectory(this);
  }
  
  public boolean mkdirs() {
    if (exists())
      return false; 
    if (mkdir())
      return true; 
    File file1 = null;
    try {
      file1 = getCanonicalFile();
    } catch (IOException iOException) {
      return false;
    } 
    File file2 = file1.getParentFile();
    return (file2 != null && (file2.mkdirs() || file2.exists()) && file1.mkdir());
  }
  
  public boolean renameTo(File paramFile) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkWrite(this.path);
      securityManager.checkWrite(paramFile.path);
    } 
    if (paramFile == null)
      throw new NullPointerException(); 
    return (isInvalid() || paramFile.isInvalid()) ? false : fs.rename(this, paramFile);
  }
  
  public boolean setLastModified(long paramLong) {
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative time"); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(this.path); 
    return isInvalid() ? false : fs.setLastModifiedTime(this, paramLong);
  }
  
  public boolean setReadOnly() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(this.path); 
    return isInvalid() ? false : fs.setReadOnly(this);
  }
  
  public boolean setWritable(boolean paramBoolean1, boolean paramBoolean2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(this.path); 
    return isInvalid() ? false : fs.setPermission(this, 2, paramBoolean1, paramBoolean2);
  }
  
  public boolean setWritable(boolean paramBoolean) { return setWritable(paramBoolean, true); }
  
  public boolean setReadable(boolean paramBoolean1, boolean paramBoolean2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(this.path); 
    return isInvalid() ? false : fs.setPermission(this, 4, paramBoolean1, paramBoolean2);
  }
  
  public boolean setReadable(boolean paramBoolean) { return setReadable(paramBoolean, true); }
  
  public boolean setExecutable(boolean paramBoolean1, boolean paramBoolean2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(this.path); 
    return isInvalid() ? false : fs.setPermission(this, 1, paramBoolean1, paramBoolean2);
  }
  
  public boolean setExecutable(boolean paramBoolean) { return setExecutable(paramBoolean, true); }
  
  public boolean canExecute() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkExec(this.path); 
    return isInvalid() ? false : fs.checkAccess(this, 1);
  }
  
  public static File[] listRoots() { return fs.listRoots(); }
  
  public long getTotalSpace() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
      securityManager.checkRead(this.path);
    } 
    return isInvalid() ? 0L : fs.getSpace(this, 0);
  }
  
  public long getFreeSpace() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
      securityManager.checkRead(this.path);
    } 
    return isInvalid() ? 0L : fs.getSpace(this, 1);
  }
  
  public long getUsableSpace() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      securityManager.checkPermission(new RuntimePermission("getFileSystemAttributes"));
      securityManager.checkRead(this.path);
    } 
    return isInvalid() ? 0L : fs.getSpace(this, 2);
  }
  
  public static File createTempFile(String paramString1, String paramString2, File paramFile) throws IOException {
    File file2;
    if (paramString1.length() < 3)
      throw new IllegalArgumentException("Prefix string too short"); 
    if (paramString2 == null)
      paramString2 = ".tmp"; 
    File file1 = (paramFile != null) ? paramFile : TempDirectory.location();
    SecurityManager securityManager = System.getSecurityManager();
    do {
      file2 = TempDirectory.generateFile(paramString1, paramString2, file1);
      if (securityManager == null)
        continue; 
      try {
        securityManager.checkWrite(file2.getPath());
      } catch (SecurityException securityException) {
        if (paramFile == null)
          throw new SecurityException("Unable to create temporary file"); 
        throw securityException;
      } 
    } while ((fs.getBooleanAttributes(file2) & true) != 0);
    if (!fs.createFileExclusively(file2.getPath()))
      throw new IOException("Unable to create temporary file"); 
    return file2;
  }
  
  public static File createTempFile(String paramString1, String paramString2) throws IOException { return createTempFile(paramString1, paramString2, null); }
  
  public int compareTo(File paramFile) { return fs.compare(this, paramFile); }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof File) ? ((compareTo((File)paramObject) == 0)) : false; }
  
  public int hashCode() { return fs.hashCode(this); }
  
  public String toString() { return getPath(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeChar(separatorChar);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String str1 = (String)getField.get("path", null);
    char c = paramObjectInputStream.readChar();
    if (c != separatorChar)
      str1 = str1.replace(c, separatorChar); 
    String str2 = fs.normalize(str1);
    UNSAFE.putObject(this, PATH_OFFSET, str2);
    UNSAFE.putIntVolatile(this, PREFIX_LENGTH_OFFSET, fs.prefixLength(str2));
  }
  
  public Path toPath() {
    Path path1 = this.filePath;
    if (path1 == null)
      synchronized (this) {
        path1 = this.filePath;
        if (path1 == null) {
          path1 = FileSystems.getDefault().getPath(this.path, new String[0]);
          this.filePath = path1;
        } 
      }  
    return path1;
  }
  
  static  {
    try {
      Unsafe unsafe = Unsafe.getUnsafe();
      PATH_OFFSET = unsafe.objectFieldOffset(File.class.getDeclaredField("path"));
      PREFIX_LENGTH_OFFSET = unsafe.objectFieldOffset(File.class.getDeclaredField("prefixLength"));
      UNSAFE = unsafe;
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new Error(reflectiveOperationException);
    } 
  }
  
  private enum PathStatus {
    INVALID, CHECKED;
  }
  
  private static class TempDirectory {
    private static final File tmpdir = new File((String)AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")));
    
    private static final SecureRandom random = new SecureRandom();
    
    static File location() { return tmpdir; }
    
    static File generateFile(String param1String1, String param1String2, File param1File) throws IOException {
      long l = random.nextLong();
      if (l == Float.MIN_VALUE) {
        l = 0L;
      } else {
        l = Math.abs(l);
      } 
      param1String1 = (new File(param1String1)).getName();
      String str = param1String1 + Long.toString(l) + param1String2;
      File file = new File(param1File, str);
      if (!str.equals(file.getName()) || file.isInvalid()) {
        if (System.getSecurityManager() != null)
          throw new IOException("Unable to create temporary file"); 
        throw new IOException("Unable to create temporary file, " + file);
      } 
      return file;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\File.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */