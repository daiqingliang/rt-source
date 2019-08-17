package sun.nio.fs;

import com.sun.nio.file.ExtendedWatchEventModifier;
import java.io.IOError;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

class WindowsPath extends AbstractPath {
  private static final int MAX_PATH = 247;
  
  private static final int MAX_LONG_PATH = 32000;
  
  private final WindowsFileSystem fs;
  
  private final WindowsPathType type;
  
  private final String root;
  
  private final String path;
  
  private int hash;
  
  private WindowsPath(WindowsFileSystem paramWindowsFileSystem, WindowsPathType paramWindowsPathType, String paramString1, String paramString2) {
    this.fs = paramWindowsFileSystem;
    this.type = paramWindowsPathType;
    this.root = paramString1;
    this.path = paramString2;
  }
  
  static WindowsPath parse(WindowsFileSystem paramWindowsFileSystem, String paramString) {
    WindowsPathParser.Result result = WindowsPathParser.parse(paramString);
    return new WindowsPath(paramWindowsFileSystem, result.type(), result.root(), result.path());
  }
  
  static WindowsPath createFromNormalizedPath(WindowsFileSystem paramWindowsFileSystem, String paramString, BasicFileAttributes paramBasicFileAttributes) {
    try {
      WindowsPathParser.Result result = WindowsPathParser.parseNormalizedPath(paramString);
      return (paramBasicFileAttributes == null) ? new WindowsPath(paramWindowsFileSystem, result.type(), result.root(), result.path()) : new WindowsPathWithAttributes(paramWindowsFileSystem, result.type(), result.root(), result.path(), paramBasicFileAttributes);
    } catch (InvalidPathException invalidPathException) {
      throw new AssertionError(invalidPathException.getMessage());
    } 
  }
  
  static WindowsPath createFromNormalizedPath(WindowsFileSystem paramWindowsFileSystem, String paramString) { return createFromNormalizedPath(paramWindowsFileSystem, paramString, null); }
  
  String getPathForExceptionMessage() { return this.path; }
  
  String getPathForPermissionCheck() { return this.path; }
  
  String getPathForWin32Calls() {
    if (isAbsolute() && this.path.length() <= 247)
      return this.path; 
    WeakReference weakReference = this.pathForWin32Calls;
    String str = (weakReference != null) ? (String)weakReference.get() : null;
    if (str != null)
      return str; 
    str = getAbsolutePath();
    if (str.length() > 247) {
      if (str.length() > 32000)
        throw new WindowsException("Cannot access file with path exceeding 32000 characters"); 
      str = addPrefixIfNeeded(WindowsNativeDispatcher.GetFullPathName(str));
    } 
    if (this.type != WindowsPathType.DRIVE_RELATIVE)
      synchronized (this.path) {
        this.pathForWin32Calls = new WeakReference(str);
      }  
    return str;
  }
  
  private String getAbsolutePath() {
    String str1;
    if (isAbsolute())
      return this.path; 
    if (this.type == WindowsPathType.RELATIVE) {
      str1 = getFileSystem().defaultDirectory();
      if (isEmpty())
        return str1; 
      if (str1.endsWith("\\"))
        return str1 + this.path; 
      StringBuilder stringBuilder = new StringBuilder(str1.length() + this.path.length() + 1);
      return stringBuilder.append(str1).append('\\').append(this.path).toString();
    } 
    if (this.type == WindowsPathType.DIRECTORY_RELATIVE) {
      str1 = getFileSystem().defaultRoot();
      return str1 + this.path.substring(1);
    } 
    if (isSameDrive(this.root, getFileSystem().defaultRoot())) {
      String str4;
      str1 = this.path.substring(this.root.length());
      String str3 = getFileSystem().defaultDirectory();
      if (str3.endsWith("\\")) {
        str4 = str3 + str1;
      } else {
        str4 = str3 + "\\" + str1;
      } 
      return str4;
    } 
    try {
      int i = WindowsNativeDispatcher.GetDriveType(this.root + "\\");
      if (i == 0 || i == 1)
        throw new WindowsException(""); 
      str1 = WindowsNativeDispatcher.GetFullPathName(this.root + ".");
    } catch (WindowsException windowsException) {
      throw new WindowsException("Unable to get working directory of drive '" + Character.toUpperCase(this.root.charAt(0)) + "'");
    } 
    String str2 = str1;
    if (str1.endsWith("\\")) {
      str2 = str2 + this.path.substring(this.root.length());
    } else if (this.path.length() > this.root.length()) {
      str2 = str2 + "\\" + this.path.substring(this.root.length());
    } 
    return str2;
  }
  
  private static boolean isSameDrive(String paramString1, String paramString2) { return (Character.toUpperCase(paramString1.charAt(0)) == Character.toUpperCase(paramString2.charAt(0))); }
  
  static String addPrefixIfNeeded(String paramString) {
    if (paramString.length() > 247)
      if (paramString.startsWith("\\\\")) {
        paramString = "\\\\?\\UNC" + paramString.substring(1, paramString.length());
      } else {
        paramString = "\\\\?\\" + paramString;
      }  
    return paramString;
  }
  
  public WindowsFileSystem getFileSystem() { return this.fs; }
  
  private boolean isEmpty() { return (this.path.length() == 0); }
  
  private WindowsPath emptyPath() { return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", ""); }
  
  public Path getFileName() {
    int i = this.path.length();
    if (i == 0)
      return this; 
    if (this.root.length() == i)
      return null; 
    int j = this.path.lastIndexOf('\\');
    if (j < this.root.length()) {
      j = this.root.length();
    } else {
      j++;
    } 
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", this.path.substring(j));
  }
  
  public WindowsPath getParent() {
    if (this.root.length() == this.path.length())
      return null; 
    int i = this.path.lastIndexOf('\\');
    return (i < this.root.length()) ? getRoot() : new WindowsPath(getFileSystem(), this.type, this.root, this.path.substring(0, i));
  }
  
  public WindowsPath getRoot() { return (this.root.length() == 0) ? null : new WindowsPath(getFileSystem(), this.type, this.root, this.root); }
  
  WindowsPathType type() { return this.type; }
  
  boolean isUnc() { return (this.type == WindowsPathType.UNC); }
  
  boolean needsSlashWhenResolving() { return this.path.endsWith("\\") ? false : ((this.path.length() > this.root.length())); }
  
  public boolean isAbsolute() { return (this.type == WindowsPathType.ABSOLUTE || this.type == WindowsPathType.UNC); }
  
  static WindowsPath toWindowsPath(Path paramPath) {
    if (paramPath == null)
      throw new NullPointerException(); 
    if (!(paramPath instanceof WindowsPath))
      throw new ProviderMismatchException(); 
    return (WindowsPath)paramPath;
  }
  
  public WindowsPath relativize(Path paramPath) {
    WindowsPath windowsPath = toWindowsPath(paramPath);
    if (equals(windowsPath))
      return emptyPath(); 
    if (this.type != windowsPath.type)
      throw new IllegalArgumentException("'other' is different type of Path"); 
    if (!this.root.equalsIgnoreCase(windowsPath.root))
      throw new IllegalArgumentException("'other' has different root"); 
    int i = getNameCount();
    int j = windowsPath.getNameCount();
    int k = (i > j) ? j : i;
    byte b1;
    for (b1 = 0; b1 < k && getName(b1).equals(windowsPath.getName(b1)); b1++);
    StringBuilder stringBuilder = new StringBuilder();
    byte b2;
    for (b2 = b1; b2 < i; b2++)
      stringBuilder.append("..\\"); 
    for (b2 = b1; b2 < j; b2++) {
      stringBuilder.append(windowsPath.getName(b2).toString());
      stringBuilder.append("\\");
    } 
    stringBuilder.setLength(stringBuilder.length() - 1);
    return createFromNormalizedPath(getFileSystem(), stringBuilder.toString());
  }
  
  public Path normalize() {
    int k;
    int i = getNameCount();
    if (i == 0 || isEmpty())
      return this; 
    boolean[] arrayOfBoolean = new boolean[i];
    int j = i;
    do {
      k = j;
      byte b1 = -1;
      for (byte b2 = 0; b2 < i; b2++) {
        if (!arrayOfBoolean[b2]) {
          String str = elementAsString(b2);
          if (str.length() > 2) {
            b1 = b2;
          } else if (str.length() == 1) {
            if (str.charAt(0) == '.') {
              arrayOfBoolean[b2] = true;
              j--;
            } else {
              b1 = b2;
            } 
          } else if (str.charAt(0) != '.' || str.charAt(1) != '.') {
            b1 = b2;
          } else if (b1 >= 0) {
            arrayOfBoolean[b1] = true;
            arrayOfBoolean[b2] = true;
            j -= 2;
            b1 = -1;
          } else if (isAbsolute() || this.type == WindowsPathType.DIRECTORY_RELATIVE) {
            boolean bool = false;
            for (byte b3 = 0; b3 < b2; b3++) {
              if (!arrayOfBoolean[b3]) {
                bool = true;
                break;
              } 
            } 
            if (!bool) {
              arrayOfBoolean[b2] = true;
              j--;
            } 
          } 
        } 
      } 
    } while (k > j);
    if (j == i)
      return this; 
    if (j == 0)
      return (this.root.length() == 0) ? emptyPath() : getRoot(); 
    StringBuilder stringBuilder = new StringBuilder();
    if (this.root != null)
      stringBuilder.append(this.root); 
    for (byte b = 0; b < i; b++) {
      if (!arrayOfBoolean[b]) {
        stringBuilder.append(getName(b));
        stringBuilder.append("\\");
      } 
    } 
    stringBuilder.setLength(stringBuilder.length() - 1);
    return createFromNormalizedPath(getFileSystem(), stringBuilder.toString());
  }
  
  public WindowsPath resolve(Path paramPath) {
    String str3;
    String str2;
    String str1;
    WindowsPath windowsPath = toWindowsPath(paramPath);
    if (windowsPath.isEmpty())
      return this; 
    if (windowsPath.isAbsolute())
      return windowsPath; 
    switch (windowsPath.type) {
      case RELATIVE:
        if (this.path.endsWith("\\") || this.root.length() == this.path.length()) {
          str1 = this.path + windowsPath.path;
        } else {
          str1 = this.path + "\\" + windowsPath.path;
        } 
        return new WindowsPath(getFileSystem(), this.type, this.root, str1);
      case DIRECTORY_RELATIVE:
        if (this.root.endsWith("\\")) {
          str1 = this.root + windowsPath.path.substring(1);
        } else {
          str1 = this.root + windowsPath.path;
        } 
        return createFromNormalizedPath(getFileSystem(), str1);
      case DRIVE_RELATIVE:
        if (!this.root.endsWith("\\"))
          return windowsPath; 
        str1 = this.root.substring(0, this.root.length() - 1);
        if (!str1.equalsIgnoreCase(windowsPath.root))
          return windowsPath; 
        str2 = windowsPath.path.substring(windowsPath.root.length());
        if (this.path.endsWith("\\")) {
          str3 = this.path + str2;
        } else {
          str3 = this.path + "\\" + str2;
        } 
        return createFromNormalizedPath(getFileSystem(), str3);
    } 
    throw new AssertionError();
  }
  
  private void initOffsets() {
    if (this.offsets == null) {
      ArrayList arrayList = new ArrayList();
      if (isEmpty()) {
        arrayList.add(Integer.valueOf(0));
      } else {
        int i = this.root.length();
        int j = this.root.length();
        while (j < this.path.length()) {
          if (this.path.charAt(j) != '\\') {
            j++;
            continue;
          } 
          arrayList.add(Integer.valueOf(i));
          i = ++j;
        } 
        if (i != j)
          arrayList.add(Integer.valueOf(i)); 
      } 
      synchronized (this) {
        if (this.offsets == null)
          this.offsets = (Integer[])arrayList.toArray(new Integer[arrayList.size()]); 
      } 
    } 
  }
  
  public int getNameCount() {
    initOffsets();
    return this.offsets.length;
  }
  
  private String elementAsString(int paramInt) {
    initOffsets();
    return (paramInt == this.offsets.length - 1) ? this.path.substring(this.offsets[paramInt].intValue()) : this.path.substring(this.offsets[paramInt].intValue(), this.offsets[paramInt + 1].intValue() - 1);
  }
  
  public WindowsPath getName(int paramInt) {
    initOffsets();
    if (paramInt < 0 || paramInt >= this.offsets.length)
      throw new IllegalArgumentException(); 
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", elementAsString(paramInt));
  }
  
  public WindowsPath subpath(int paramInt1, int paramInt2) {
    initOffsets();
    if (paramInt1 < 0)
      throw new IllegalArgumentException(); 
    if (paramInt1 >= this.offsets.length)
      throw new IllegalArgumentException(); 
    if (paramInt2 > this.offsets.length)
      throw new IllegalArgumentException(); 
    if (paramInt1 >= paramInt2)
      throw new IllegalArgumentException(); 
    StringBuilder stringBuilder = new StringBuilder();
    Integer[] arrayOfInteger = new Integer[paramInt2 - paramInt1];
    for (int i = paramInt1; i < paramInt2; i++) {
      arrayOfInteger[i - paramInt1] = Integer.valueOf(stringBuilder.length());
      stringBuilder.append(elementAsString(i));
      if (i != paramInt2 - 1)
        stringBuilder.append("\\"); 
    } 
    return new WindowsPath(getFileSystem(), WindowsPathType.RELATIVE, "", stringBuilder.toString());
  }
  
  public boolean startsWith(Path paramPath) {
    if (!(Objects.requireNonNull(paramPath) instanceof WindowsPath))
      return false; 
    WindowsPath windowsPath = (WindowsPath)paramPath;
    if (!this.root.equalsIgnoreCase(windowsPath.root))
      return false; 
    if (windowsPath.isEmpty())
      return isEmpty(); 
    int i = getNameCount();
    int j = windowsPath.getNameCount();
    if (j <= i) {
      while (--j >= 0) {
        String str1 = elementAsString(j);
        String str2 = windowsPath.elementAsString(j);
        if (!str1.equalsIgnoreCase(str2))
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  public boolean endsWith(Path paramPath) {
    if (!(Objects.requireNonNull(paramPath) instanceof WindowsPath))
      return false; 
    WindowsPath windowsPath = (WindowsPath)paramPath;
    if (windowsPath.path.length() > this.path.length())
      return false; 
    if (windowsPath.isEmpty())
      return isEmpty(); 
    int i = getNameCount();
    int j = windowsPath.getNameCount();
    if (j > i)
      return false; 
    if (windowsPath.root.length() > 0) {
      if (j < i)
        return false; 
      if (!this.root.equalsIgnoreCase(windowsPath.root))
        return false; 
    } 
    int k = i - j;
    while (--j >= 0) {
      String str1 = elementAsString(k + j);
      String str2 = windowsPath.elementAsString(j);
      if (!str1.equalsIgnoreCase(str2))
        return false; 
    } 
    return true;
  }
  
  public int compareTo(Path paramPath) {
    if (paramPath == null)
      throw new NullPointerException(); 
    String str1 = this.path;
    String str2 = ((WindowsPath)paramPath).path;
    int i = str1.length();
    int j = str2.length();
    int k = Math.min(i, j);
    for (byte b = 0; b < k; b++) {
      char c1 = str1.charAt(b);
      char c2 = str2.charAt(b);
      if (c1 != c2) {
        c1 = Character.toUpperCase(c1);
        c2 = Character.toUpperCase(c2);
        if (c1 != c2)
          return c1 - c2; 
      } 
    } 
    return i - j;
  }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof WindowsPath) ? ((compareTo((Path)paramObject) == 0)) : false; }
  
  public int hashCode() {
    int i = this.hash;
    if (i == 0) {
      for (byte b = 0; b < this.path.length(); b++)
        i = 31 * i + Character.toUpperCase(this.path.charAt(b)); 
      this.hash = i;
    } 
    return i;
  }
  
  public String toString() { return this.path; }
  
  long openForReadAttributeAccess(boolean paramBoolean) throws WindowsException {
    int i = 33554432;
    if (!paramBoolean && getFileSystem().supportsLinks())
      i |= 0x200000; 
    return WindowsNativeDispatcher.CreateFile(getPathForWin32Calls(), 128, 7, 0L, 3, i);
  }
  
  void checkRead() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkRead(getPathForPermissionCheck()); 
  }
  
  void checkWrite() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkWrite(getPathForPermissionCheck()); 
  }
  
  void checkDelete() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkDelete(getPathForPermissionCheck()); 
  }
  
  public URI toUri() { return WindowsUriSupport.toUri(this); }
  
  public WindowsPath toAbsolutePath() {
    if (isAbsolute())
      return this; 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPropertyAccess("user.dir"); 
    try {
      return createFromNormalizedPath(getFileSystem(), getAbsolutePath());
    } catch (WindowsException windowsException) {
      throw new IOError(new IOException(windowsException.getMessage()));
    } 
  }
  
  public WindowsPath toRealPath(LinkOption... paramVarArgs) throws IOException {
    checkRead();
    String str = WindowsLinkSupport.getRealPath(this, Util.followLinks(paramVarArgs));
    return createFromNormalizedPath(getFileSystem(), str);
  }
  
  public WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs) throws IOException {
    Modifier[] arrayOfModifier;
    if (paramWatchService == null)
      throw new NullPointerException(); 
    if (!(paramWatchService instanceof WindowsWatchService))
      throw new ProviderMismatchException(); 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      boolean bool = false;
      int i = paramVarArgs.length;
      if (i > 0) {
        arrayOfModifier = (Modifier[])Arrays.copyOf(paramVarArgs, i);
        byte b = 0;
        while (b < i) {
          if (arrayOfModifier[b++] == ExtendedWatchEventModifier.FILE_TREE) {
            bool = true;
            break;
          } 
        } 
      } 
      String str = getPathForPermissionCheck();
      securityManager.checkRead(str);
      if (bool)
        securityManager.checkRead(str + "\\-"); 
    } 
    return ((WindowsWatchService)paramWatchService).register(this, paramArrayOfKind, arrayOfModifier);
  }
  
  private static class WindowsPathWithAttributes extends WindowsPath implements BasicFileAttributesHolder {
    final WeakReference<BasicFileAttributes> ref;
    
    WindowsPathWithAttributes(WindowsFileSystem param1WindowsFileSystem, WindowsPathType param1WindowsPathType, String param1String1, String param1String2, BasicFileAttributes param1BasicFileAttributes) {
      super(param1WindowsFileSystem, param1WindowsPathType, param1String1, param1String2, null);
      this.ref = new WeakReference(param1BasicFileAttributes);
    }
    
    public BasicFileAttributes get() { return (BasicFileAttributes)this.ref.get(); }
    
    public void invalidate() { this.ref.clear(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */