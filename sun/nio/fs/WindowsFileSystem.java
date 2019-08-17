package sun.nio.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;
import sun.security.action.GetPropertyAction;

class WindowsFileSystem extends FileSystem {
  private final WindowsFileSystemProvider provider;
  
  private final String defaultDirectory;
  
  private final String defaultRoot;
  
  private final boolean supportsLinks;
  
  private final boolean supportsStreamEnumeration;
  
  private static final Set<String> supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[] { "basic", "dos", "acl", "owner", "user" })));
  
  private static final String GLOB_SYNTAX = "glob";
  
  private static final String REGEX_SYNTAX = "regex";
  
  WindowsFileSystem(WindowsFileSystemProvider paramWindowsFileSystemProvider, String paramString) {
    this.provider = paramWindowsFileSystemProvider;
    WindowsPathParser.Result result = WindowsPathParser.parse(paramString);
    if (result.type() != WindowsPathType.ABSOLUTE && result.type() != WindowsPathType.UNC)
      throw new AssertionError("Default directory is not an absolute path"); 
    this.defaultDirectory = result.path();
    this.defaultRoot = result.root();
    GetPropertyAction getPropertyAction = new GetPropertyAction("os.version");
    String str = (String)AccessController.doPrivileged(getPropertyAction);
    String[] arrayOfString = Util.split(str, '.');
    int i = Integer.parseInt(arrayOfString[0]);
    int j = Integer.parseInt(arrayOfString[1]);
    this.supportsLinks = (i >= 6);
    this.supportsStreamEnumeration = (i >= 6 || (i == 5 && j >= 2));
  }
  
  String defaultDirectory() { return this.defaultDirectory; }
  
  String defaultRoot() { return this.defaultRoot; }
  
  boolean supportsLinks() { return this.supportsLinks; }
  
  boolean supportsStreamEnumeration() { return this.supportsStreamEnumeration; }
  
  public FileSystemProvider provider() { return this.provider; }
  
  public String getSeparator() { return "\\"; }
  
  public boolean isOpen() { return true; }
  
  public boolean isReadOnly() { return false; }
  
  public void close() throws IOException { throw new UnsupportedOperationException(); }
  
  public Iterable<Path> getRootDirectories() {
    int i = 0;
    try {
      i = WindowsNativeDispatcher.GetLogicalDrives();
    } catch (WindowsException windowsException) {
      throw new AssertionError(windowsException.getMessage());
    } 
    ArrayList arrayList = new ArrayList();
    SecurityManager securityManager = System.getSecurityManager();
    for (byte b = 0; b <= 25; b++) {
      if ((i & true << b) != 0) {
        StringBuilder stringBuilder = new StringBuilder(3);
        stringBuilder.append((char)(65 + b));
        stringBuilder.append(":\\");
        String str = stringBuilder.toString();
        if (securityManager != null)
          try {
            securityManager.checkRead(str);
          } catch (SecurityException securityException) {} 
        arrayList.add(WindowsPath.createFromNormalizedPath(this, str));
      } 
    } 
    return Collections.unmodifiableList(arrayList);
  }
  
  public Iterable<FileStore> getFileStores() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
      } catch (SecurityException securityException) {
        return Collections.emptyList();
      }  
    return new Iterable<FileStore>() {
        public Iterator<FileStore> iterator() { return new WindowsFileSystem.FileStoreIterator(WindowsFileSystem.this); }
      };
  }
  
  public Set<String> supportedFileAttributeViews() { return supportedFileAttributeViews; }
  
  public final Path getPath(String paramString, String... paramVarArgs) {
    String str;
    if (paramVarArgs.length == 0) {
      str = paramString;
    } else {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      for (String str1 : paramVarArgs) {
        if (str1.length() > 0) {
          if (stringBuilder.length() > 0)
            stringBuilder.append('\\'); 
          stringBuilder.append(str1);
        } 
      } 
      str = stringBuilder.toString();
    } 
    return WindowsPath.parse(this, str);
  }
  
  public UserPrincipalLookupService getUserPrincipalLookupService() { return LookupService.instance; }
  
  public PathMatcher getPathMatcher(String paramString) {
    String str3;
    int i = paramString.indexOf(':');
    if (i <= 0 || i == paramString.length())
      throw new IllegalArgumentException(); 
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1);
    if (str1.equals("glob")) {
      str3 = Globs.toWindowsRegexPattern(str2);
    } else if (str1.equals("regex")) {
      str3 = str2;
    } else {
      throw new UnsupportedOperationException("Syntax '" + str1 + "' not recognized");
    } 
    final Pattern pattern = Pattern.compile(str3, 66);
    return new PathMatcher() {
        public boolean matches(Path param1Path) { return pattern.matcher(param1Path.toString()).matches(); }
      };
  }
  
  public WatchService newWatchService() throws IOException { return new WindowsWatchService(this); }
  
  private class FileStoreIterator extends Object implements Iterator<FileStore> {
    private final Iterator<Path> roots;
    
    private FileStore next;
    
    FileStoreIterator() { this.roots = this$0.getRootDirectories().iterator(); }
    
    private FileStore readNext() {
      assert Thread.holdsLock(this);
      while (true) {
        if (!this.roots.hasNext())
          return null; 
        WindowsPath windowsPath = (WindowsPath)this.roots.next();
        try {
          windowsPath.checkRead();
        } catch (SecurityException securityException) {
          continue;
        } 
        try {
          WindowsFileStore windowsFileStore = WindowsFileStore.create(windowsPath.toString(), true);
          if (windowsFileStore != null)
            return windowsFileStore; 
        } catch (IOException iOException) {}
      } 
    }
    
    public boolean hasNext() {
      if (this.next != null)
        return true; 
      this.next = readNext();
      return (this.next != null);
    }
    
    public FileStore next() {
      if (this.next == null)
        this.next = readNext(); 
      if (this.next == null)
        throw new NoSuchElementException(); 
      FileStore fileStore = this.next;
      this.next = null;
      return fileStore;
    }
    
    public void remove() throws IOException { throw new UnsupportedOperationException(); }
  }
  
  private static class LookupService {
    static final UserPrincipalLookupService instance = new UserPrincipalLookupService() {
        public UserPrincipal lookupPrincipalByName(String param2String) throws IOException { return WindowsUserPrincipals.lookup(param2String); }
        
        public GroupPrincipal lookupPrincipalByGroupName(String param2String) throws IOException {
          UserPrincipal userPrincipal = WindowsUserPrincipals.lookup(param2String);
          if (!(userPrincipal instanceof GroupPrincipal))
            throw new UserPrincipalNotFoundException(param2String); 
          return (GroupPrincipal)userPrincipal;
        }
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsFileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */