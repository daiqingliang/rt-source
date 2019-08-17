package sun.nio.fs;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.NotLinkException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class WindowsLinkSupport {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  static String readLink(WindowsPath paramWindowsPath) throws IOException {
    l = 0L;
    try {
      l = paramWindowsPath.openForReadAttributeAccess(false);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath);
    } 
    try {
      return readLinkImpl(l);
    } finally {
      WindowsNativeDispatcher.CloseHandle(l);
    } 
  }
  
  static String getFinalPath(WindowsPath paramWindowsPath) throws IOException {
    l = 0L;
    try {
      l = paramWindowsPath.openForReadAttributeAccess(true);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath);
    } 
    try {
      return stripPrefix(WindowsNativeDispatcher.GetFinalPathNameByHandle(l));
    } catch (WindowsException windowsException) {
      if (windowsException.lastError() != 124)
        windowsException.rethrowAsIOException(paramWindowsPath); 
    } finally {
      WindowsNativeDispatcher.CloseHandle(l);
    } 
    return null;
  }
  
  static String getFinalPath(WindowsPath paramWindowsPath, boolean paramBoolean) throws IOException {
    WindowsFileSystem windowsFileSystem = paramWindowsPath.getFileSystem();
    try {
      if (!paramBoolean || !windowsFileSystem.supportsLinks())
        return paramWindowsPath.getPathForWin32Calls(); 
      if (!WindowsFileAttributes.get(paramWindowsPath, false).isSymbolicLink())
        return paramWindowsPath.getPathForWin32Calls(); 
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath);
    } 
    String str = getFinalPath(paramWindowsPath);
    if (str != null)
      return str; 
    WindowsPath windowsPath = paramWindowsPath;
    byte b = 0;
    do {
      try {
        WindowsFileAttributes windowsFileAttributes = WindowsFileAttributes.get(windowsPath, false);
        if (!windowsFileAttributes.isSymbolicLink())
          return windowsPath.getPathForWin32Calls(); 
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(windowsPath);
      } 
      WindowsPath windowsPath1 = WindowsPath.createFromNormalizedPath(windowsFileSystem, readLink(windowsPath));
      WindowsPath windowsPath2 = windowsPath.getParent();
      if (windowsPath2 == null) {
        final WindowsPath t = windowsPath;
        windowsPath = (WindowsPath)AccessController.doPrivileged(new PrivilegedAction<WindowsPath>() {
              public WindowsPath run() { return t.toAbsolutePath(); }
            });
        windowsPath2 = windowsPath.getParent();
      } 
      windowsPath = windowsPath2.resolve(windowsPath1);
    } while (++b < 32);
    throw new FileSystemException(paramWindowsPath.getPathForExceptionMessage(), null, "Too many links");
  }
  
  static String getRealPath(WindowsPath paramWindowsPath, boolean paramBoolean) throws IOException {
    int i;
    WindowsFileSystem windowsFileSystem = paramWindowsPath.getFileSystem();
    if (paramBoolean && !windowsFileSystem.supportsLinks())
      paramBoolean = false; 
    String str = null;
    try {
      str = paramWindowsPath.toAbsolutePath().toString();
    } catch (IOError iOError) {
      throw (IOException)iOError.getCause();
    } 
    if (str.indexOf('.') >= 0)
      try {
        str = WindowsNativeDispatcher.GetFullPathName(str);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(paramWindowsPath);
      }  
    StringBuilder stringBuilder = new StringBuilder(str.length());
    char c1 = str.charAt(0);
    char c2 = str.charAt(1);
    if (((c1 <= 'z' && c1 >= 'a') || (c1 <= 'Z' && c1 >= 'A')) && c2 == ':' && str.charAt(2) == '\\') {
      stringBuilder.append(Character.toUpperCase(c1));
      stringBuilder.append(":\\");
      i = 3;
    } else if (c1 == '\\' && c2 == '\\') {
      int k = str.length() - 1;
      int m = str.indexOf('\\', 2);
      if (m == -1 || m == k)
        throw new FileSystemException(paramWindowsPath.getPathForExceptionMessage(), null, "UNC has invalid share"); 
      m = str.indexOf('\\', m + 1);
      if (m < 0) {
        m = k;
        stringBuilder.append(str).append("\\");
      } else {
        stringBuilder.append(str, 0, m + 1);
      } 
      i = m + 1;
    } else {
      throw new AssertionError("path type not recognized");
    } 
    if (i >= str.length()) {
      String str1 = stringBuilder.toString();
      try {
        WindowsNativeDispatcher.GetFileAttributes(str1);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(str);
      } 
      return str1;
    } 
    int j;
    for (j = i; j < str.length(); j = m + 1) {
      int k = str.indexOf('\\', j);
      int m = (k == -1) ? str.length() : k;
      String str1 = stringBuilder.toString() + str.substring(j, m);
      try {
        WindowsNativeDispatcher.FirstFile firstFile = WindowsNativeDispatcher.FindFirstFile(WindowsPath.addPrefixIfNeeded(str1));
        WindowsNativeDispatcher.FindClose(firstFile.handle());
        if (paramBoolean && WindowsFileAttributes.isReparsePoint(firstFile.attributes())) {
          String str2 = getFinalPath(paramWindowsPath);
          if (str2 == null) {
            WindowsPath windowsPath = resolveAllLinks(WindowsPath.createFromNormalizedPath(windowsFileSystem, str));
            str2 = getRealPath(windowsPath, false);
          } 
          return str2;
        } 
        stringBuilder.append(firstFile.name());
        if (k != -1)
          stringBuilder.append('\\'); 
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(str);
      } 
    } 
    return stringBuilder.toString();
  }
  
  private static String readLinkImpl(long paramLong) throws IOException {
    char c = '䀀';
    nativeBuffer = NativeBuffers.getNativeBuffer(c);
    try {
      try {
        WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(paramLong, nativeBuffer.address(), c);
      } catch (WindowsException windowsException) {
        if (windowsException.lastError() == 4390)
          throw new NotLinkException(null, null, windowsException.errorString()); 
        windowsException.rethrowAsIOException((String)null);
      } 
      int i = (int)unsafe.getLong(nativeBuffer.address() + 0L);
      if (i != -1610612724)
        throw new NotLinkException(null, null, "Reparse point is not a symbolic link"); 
      short s1 = unsafe.getShort(nativeBuffer.address() + 8L);
      short s2 = unsafe.getShort(nativeBuffer.address() + 10L);
      if (s2 % 2 != 0)
        throw new FileSystemException(null, null, "Symbolic link corrupted"); 
      char[] arrayOfChar = new char[s2 / 2];
      unsafe.copyMemory(null, nativeBuffer.address() + 20L + s1, arrayOfChar, Unsafe.ARRAY_CHAR_BASE_OFFSET, s2);
      String str = stripPrefix(new String(arrayOfChar));
      if (str.length() == 0)
        throw new IOException("Symbolic link target is invalid"); 
      return str;
    } finally {
      nativeBuffer.release();
    } 
  }
  
  private static WindowsPath resolveAllLinks(WindowsPath paramWindowsPath) throws IOException {
    assert paramWindowsPath.isAbsolute();
    WindowsFileSystem windowsFileSystem = paramWindowsPath.getFileSystem();
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramWindowsPath.getNameCount(); b2++) {
      WindowsPath windowsPath = paramWindowsPath.getRoot().resolve(paramWindowsPath.subpath(0, b2 + true));
      WindowsFileAttributes windowsFileAttributes = null;
      try {
        windowsFileAttributes = WindowsFileAttributes.get(windowsPath, false);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(windowsPath);
      } 
      if (windowsFileAttributes.isSymbolicLink()) {
        if (++b1 > 32)
          throw new IOException("Too many links"); 
        WindowsPath windowsPath1 = WindowsPath.createFromNormalizedPath(windowsFileSystem, readLink(windowsPath));
        WindowsPath windowsPath2 = null;
        int i = paramWindowsPath.getNameCount();
        if (b2 + true < i)
          windowsPath2 = paramWindowsPath.subpath(b2 + true, i); 
        paramWindowsPath = windowsPath.getParent().resolve(windowsPath1);
        try {
          String str = WindowsNativeDispatcher.GetFullPathName(paramWindowsPath.toString());
          if (!str.equals(paramWindowsPath.toString()))
            paramWindowsPath = WindowsPath.createFromNormalizedPath(windowsFileSystem, str); 
        } catch (WindowsException windowsException) {
          windowsException.rethrowAsIOException(paramWindowsPath);
        } 
        if (windowsPath2 != null)
          paramWindowsPath = paramWindowsPath.resolve(windowsPath2); 
        b2 = 0;
        continue;
      } 
    } 
    return paramWindowsPath;
  }
  
  private static String stripPrefix(String paramString) {
    if (paramString.startsWith("\\\\?\\")) {
      if (paramString.startsWith("\\\\?\\UNC\\")) {
        paramString = "\\" + paramString.substring(7);
      } else {
        paramString = paramString.substring(4);
      } 
      return paramString;
    } 
    if (paramString.startsWith("\\??\\")) {
      if (paramString.startsWith("\\??\\UNC\\")) {
        paramString = "\\" + paramString.substring(7);
      } else {
        paramString = paramString.substring(4);
      } 
      return paramString;
    } 
    return paramString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsLinkSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */