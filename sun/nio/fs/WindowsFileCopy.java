package sun.nio.fs;

import com.sun.nio.file.ExtendedCopyOption;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;

class WindowsFileCopy {
  static void copy(final WindowsPath source, final WindowsPath target, CopyOption... paramVarArgs) throws IOException {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool = true;
    boolean bool3 = false;
    for (CopyOption copyOption : paramVarArgs) {
      if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
        bool1 = true;
      } else if (copyOption == LinkOption.NOFOLLOW_LINKS) {
        bool = false;
      } else if (copyOption == StandardCopyOption.COPY_ATTRIBUTES) {
        bool2 = true;
      } else if (copyOption == ExtendedCopyOption.INTERRUPTIBLE) {
        bool3 = true;
      } else {
        if (copyOption == null)
          throw new NullPointerException(); 
        throw new UnsupportedOperationException("Unsupported copy option");
      } 
    } 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      paramWindowsPath1.checkRead();
      paramWindowsPath2.checkWrite();
    } 
    WindowsFileAttributes windowsFileAttributes1 = null;
    WindowsFileAttributes windowsFileAttributes2 = null;
    l = 0L;
    try {
      l = paramWindowsPath1.openForReadAttributeAccess(bool);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath1);
    } 
    try {
      try {
        windowsFileAttributes1 = WindowsFileAttributes.readAttributes(l);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(paramWindowsPath1);
      } 
      l1 = 0L;
      try {
        l1 = paramWindowsPath2.openForReadAttributeAccess(false);
        try {
          if ((windowsFileAttributes2 = WindowsFileAttributes.readAttributes(l1)).isSameFile(windowsFileAttributes1, windowsFileAttributes2))
            return; 
          if (!bool1)
            throw new FileAlreadyExistsException(paramWindowsPath2.getPathForExceptionMessage()); 
        } finally {
          WindowsNativeDispatcher.CloseHandle(l1);
        } 
      } catch (WindowsException windowsException) {}
    } finally {
      WindowsNativeDispatcher.CloseHandle(l);
    } 
    if (securityManager != null && windowsFileAttributes1.isSymbolicLink())
      securityManager.checkPermission(new LinkPermission("symbolic")); 
    final String sourcePath = asWin32Path(paramWindowsPath1);
    final String targetPath = asWin32Path(paramWindowsPath2);
    if (windowsFileAttributes2 != null)
      try {
        if (windowsFileAttributes2.isDirectory() || windowsFileAttributes2.isDirectoryLink()) {
          WindowsNativeDispatcher.RemoveDirectory(str2);
        } else {
          WindowsNativeDispatcher.DeleteFile(str2);
        } 
      } catch (WindowsException windowsException) {
        if (windowsFileAttributes2.isDirectory() && (windowsException.lastError() == 145 || windowsException.lastError() == 183))
          throw new DirectoryNotEmptyException(paramWindowsPath2.getPathForExceptionMessage()); 
        windowsException.rethrowAsIOException(paramWindowsPath2);
      }  
    if (!windowsFileAttributes1.isDirectory() && !windowsFileAttributes1.isDirectoryLink()) {
      final char flags = (paramWindowsPath1.getFileSystem().supportsLinks() && !bool) ? 2048 : 0;
      if (bool3) {
        Cancellable cancellable = new Cancellable() {
            public int cancelValue() { return 1; }
            
            public void implRun() {
              try {
                WindowsNativeDispatcher.CopyFileEx(sourcePath, targetPath, flags, addressToPollForCancel());
              } catch (WindowsException windowsException) {
                windowsException.rethrowAsIOException(source, target);
              } 
            }
          };
        try {
          Cancellable.runInterruptibly(cancellable);
        } catch (ExecutionException executionException) {
          Throwable throwable = executionException.getCause();
          if (throwable instanceof IOException)
            throw (IOException)throwable; 
          throw new IOException(throwable);
        } 
      } else {
        try {
          WindowsNativeDispatcher.CopyFileEx(str1, str2, c, 0L);
        } catch (WindowsException windowsException) {
          windowsException.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2);
        } 
      } 
      if (bool2)
        try {
          copySecurityAttributes(paramWindowsPath1, paramWindowsPath2, bool);
        } catch (IOException iOException) {} 
      return;
    } 
    try {
      if (windowsFileAttributes1.isDirectory()) {
        WindowsNativeDispatcher.CreateDirectory(str2, 0L);
      } else {
        String str = WindowsLinkSupport.readLink(paramWindowsPath1);
        byte b = 1;
        WindowsNativeDispatcher.CreateSymbolicLink(str2, WindowsPath.addPrefixIfNeeded(str), b);
      } 
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath2);
    } 
    if (bool2) {
      WindowsFileAttributeViews.Dos dos = WindowsFileAttributeViews.createDosView(paramWindowsPath2, false);
      try {
        dos.setAttributes(windowsFileAttributes1);
      } catch (IOException iOException) {
        if (windowsFileAttributes1.isDirectory())
          try {
            WindowsNativeDispatcher.RemoveDirectory(str2);
          } catch (WindowsException windowsException) {} 
      } 
      try {
        copySecurityAttributes(paramWindowsPath1, paramWindowsPath2, bool);
      } catch (IOException iOException) {}
    } 
  }
  
  static void move(WindowsPath paramWindowsPath1, WindowsPath paramWindowsPath2, CopyOption... paramVarArgs) throws IOException {
    boolean bool1 = false;
    boolean bool2 = false;
    for (CopyOption copyOption : paramVarArgs) {
      if (copyOption == StandardCopyOption.ATOMIC_MOVE) {
        bool1 = true;
      } else if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
        bool2 = true;
      } else if (copyOption != LinkOption.NOFOLLOW_LINKS) {
        if (copyOption == null)
          throw new NullPointerException(); 
        throw new UnsupportedOperationException("Unsupported copy option");
      } 
    } 
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      paramWindowsPath1.checkWrite();
      paramWindowsPath2.checkWrite();
    } 
    String str1 = asWin32Path(paramWindowsPath1);
    String str2 = asWin32Path(paramWindowsPath2);
    if (bool1) {
      try {
        WindowsNativeDispatcher.MoveFileEx(str1, str2, 1);
      } catch (WindowsException windowsException) {
        if (windowsException.lastError() == 17)
          throw new AtomicMoveNotSupportedException(paramWindowsPath1.getPathForExceptionMessage(), paramWindowsPath2.getPathForExceptionMessage(), windowsException.errorString()); 
        windowsException.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2);
      } 
      return;
    } 
    WindowsFileAttributes windowsFileAttributes1 = null;
    WindowsFileAttributes windowsFileAttributes2 = null;
    l = 0L;
    try {
      l = paramWindowsPath1.openForReadAttributeAccess(false);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath1);
    } 
    try {
      try {
        windowsFileAttributes1 = WindowsFileAttributes.readAttributes(l);
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(paramWindowsPath1);
      } 
      l1 = 0L;
      try {
        l1 = paramWindowsPath2.openForReadAttributeAccess(false);
        try {
          if ((windowsFileAttributes2 = WindowsFileAttributes.readAttributes(l1)).isSameFile(windowsFileAttributes1, windowsFileAttributes2))
            return; 
          if (!bool2)
            throw new FileAlreadyExistsException(paramWindowsPath2.getPathForExceptionMessage()); 
        } finally {
          WindowsNativeDispatcher.CloseHandle(l1);
        } 
      } catch (WindowsException windowsException) {}
    } finally {
      WindowsNativeDispatcher.CloseHandle(l);
    } 
    if (windowsFileAttributes2 != null)
      try {
        if (windowsFileAttributes2.isDirectory() || windowsFileAttributes2.isDirectoryLink()) {
          WindowsNativeDispatcher.RemoveDirectory(str2);
        } else {
          WindowsNativeDispatcher.DeleteFile(str2);
        } 
      } catch (WindowsException windowsException) {
        if (windowsFileAttributes2.isDirectory() && (windowsException.lastError() == 145 || windowsException.lastError() == 183))
          throw new DirectoryNotEmptyException(paramWindowsPath2.getPathForExceptionMessage()); 
        windowsException.rethrowAsIOException(paramWindowsPath2);
      }  
    try {
      WindowsNativeDispatcher.MoveFileEx(str1, str2, 0);
      return;
    } catch (WindowsException windowsException) {
      if (windowsException.lastError() != 17)
        windowsException.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2); 
      if (!windowsFileAttributes1.isDirectory() && !windowsFileAttributes1.isDirectoryLink()) {
        try {
          WindowsNativeDispatcher.MoveFileEx(str1, str2, 2);
        } catch (WindowsException windowsException) {
          windowsException.rethrowAsIOException(paramWindowsPath1, paramWindowsPath2);
        } 
        try {
          copySecurityAttributes(paramWindowsPath1, paramWindowsPath2, false);
        } catch (IOException windowsException) {}
        return;
      } 
      assert windowsFileAttributes1.isDirectory() || windowsFileAttributes1.isDirectoryLink();
      try {
        if (windowsFileAttributes1.isDirectory()) {
          WindowsNativeDispatcher.CreateDirectory(str2, 0L);
        } else {
          String str = WindowsLinkSupport.readLink(paramWindowsPath1);
          WindowsNativeDispatcher.CreateSymbolicLink(str2, WindowsPath.addPrefixIfNeeded(str), 1);
        } 
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(paramWindowsPath2);
      } 
      WindowsFileAttributeViews.Dos dos = WindowsFileAttributeViews.createDosView(paramWindowsPath2, false);
      try {
        dos.setAttributes(windowsFileAttributes1);
      } catch (IOException iOException) {
        try {
          WindowsNativeDispatcher.RemoveDirectory(str2);
        } catch (WindowsException windowsException1) {}
        throw iOException;
      } 
      try {
        copySecurityAttributes(paramWindowsPath1, paramWindowsPath2, false);
      } catch (IOException iOException) {}
      try {
        WindowsNativeDispatcher.RemoveDirectory(str1);
      } catch (WindowsException windowsException1) {
        try {
          WindowsNativeDispatcher.RemoveDirectory(str2);
        } catch (WindowsException windowsException2) {}
        if (windowsException1.lastError() == 145 || windowsException1.lastError() == 183)
          throw new DirectoryNotEmptyException(paramWindowsPath2.getPathForExceptionMessage()); 
        windowsException1.rethrowAsIOException(paramWindowsPath1);
      } 
      return;
    } 
  }
  
  private static String asWin32Path(WindowsPath paramWindowsPath) throws IOException {
    try {
      return paramWindowsPath.getPathForWin32Calls();
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramWindowsPath);
      return null;
    } 
  }
  
  private static void copySecurityAttributes(WindowsPath paramWindowsPath1, WindowsPath paramWindowsPath2, boolean paramBoolean) throws IOException {
    String str = WindowsLinkSupport.getFinalPath(paramWindowsPath1, paramBoolean);
    privilege = WindowsSecurity.enablePrivilege("SeRestorePrivilege");
    try {
      byte b = 7;
      nativeBuffer = WindowsAclFileAttributeView.getFileSecurity(str, b);
      try {
        try {
          WindowsNativeDispatcher.SetFileSecurity(paramWindowsPath2.getPathForWin32Calls(), b, nativeBuffer.address());
        } catch (WindowsException windowsException) {
          windowsException.rethrowAsIOException(paramWindowsPath2);
        } 
      } finally {
        nativeBuffer.release();
      } 
    } finally {
      privilege.drop();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsFileCopy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */