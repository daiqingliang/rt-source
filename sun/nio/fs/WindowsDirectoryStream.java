package sun.nio.fs;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.NoSuchElementException;

class WindowsDirectoryStream extends Object implements DirectoryStream<Path> {
  private final WindowsPath dir;
  
  private final DirectoryStream.Filter<? super Path> filter;
  
  private final long handle;
  
  private final String firstName;
  
  private final NativeBuffer findDataBuffer;
  
  private final Object closeLock = new Object();
  
  private boolean isOpen = true;
  
  private Iterator<Path> iterator;
  
  WindowsDirectoryStream(WindowsPath paramWindowsPath, DirectoryStream.Filter<? super Path> paramFilter) throws IOException {
    this.dir = paramWindowsPath;
    this.filter = paramFilter;
    try {
      String str = paramWindowsPath.getPathForWin32Calls();
      char c = str.charAt(str.length() - 1);
      if (c == ':' || c == '\\') {
        str = str + "*";
      } else {
        str = str + "\\*";
      } 
      WindowsNativeDispatcher.FirstFile firstFile = WindowsNativeDispatcher.FindFirstFile(str);
      this.handle = firstFile.handle();
      this.firstName = firstFile.name();
      this.findDataBuffer = WindowsFileAttributes.getBufferForFindData();
    } catch (WindowsException windowsException) {
      if (windowsException.lastError() == 267)
        throw new NotDirectoryException(paramWindowsPath.getPathForExceptionMessage()); 
      windowsException.rethrowAsIOException(paramWindowsPath);
      throw new AssertionError();
    } 
  }
  
  public void close() throws IOException {
    synchronized (this.closeLock) {
      if (!this.isOpen)
        return; 
      this.isOpen = false;
    } 
    this.findDataBuffer.release();
    try {
      WindowsNativeDispatcher.FindClose(this.handle);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(this.dir);
    } 
  }
  
  public Iterator<Path> iterator() {
    if (!this.isOpen)
      throw new IllegalStateException("Directory stream is closed"); 
    synchronized (this) {
      if (this.iterator != null)
        throw new IllegalStateException("Iterator already obtained"); 
      this.iterator = new WindowsDirectoryIterator(this.firstName);
      return this.iterator;
    } 
  }
  
  private class WindowsDirectoryIterator extends Object implements Iterator<Path> {
    private boolean atEof = false;
    
    private String first;
    
    private Path nextEntry;
    
    private String prefix;
    
    WindowsDirectoryIterator(String param1String) {
      this.first = param1String;
      if (this$0.dir.needsSlashWhenResolving()) {
        this.prefix = this$0.dir.toString() + "\\";
      } else {
        this.prefix = this$0.dir.toString();
      } 
    }
    
    private boolean isSelfOrParent(String param1String) { return (param1String.equals(".") || param1String.equals("..")); }
    
    private Path acceptEntry(String param1String, BasicFileAttributes param1BasicFileAttributes) {
      WindowsPath windowsPath = WindowsPath.createFromNormalizedPath(WindowsDirectoryStream.this.dir.getFileSystem(), this.prefix + param1String, param1BasicFileAttributes);
      try {
        if (WindowsDirectoryStream.this.filter.accept(windowsPath))
          return windowsPath; 
      } catch (IOException iOException) {
        throw new DirectoryIteratorException(iOException);
      } 
      return null;
    }
    
    private Path readNextEntry() {
      Path path;
      if (this.first != null) {
        this.nextEntry = isSelfOrParent(this.first) ? null : acceptEntry(this.first, null);
        this.first = null;
        if (this.nextEntry != null)
          return this.nextEntry; 
      } 
      while (true) {
        WindowsFileAttributes windowsFileAttributes;
        String str = null;
        synchronized (WindowsDirectoryStream.this.closeLock) {
          try {
            if (WindowsDirectoryStream.this.isOpen)
              str = WindowsNativeDispatcher.FindNextFile(WindowsDirectoryStream.this.handle, WindowsDirectoryStream.this.findDataBuffer.address()); 
          } catch (WindowsException windowsException) {
            IOException iOException = windowsException.asIOException(WindowsDirectoryStream.this.dir);
            throw new DirectoryIteratorException(iOException);
          } 
          if (str == null) {
            this.atEof = true;
            return null;
          } 
          if (isSelfOrParent(str))
            continue; 
          windowsFileAttributes = WindowsFileAttributes.fromFindData(WindowsDirectoryStream.this.findDataBuffer.address());
        } 
        path = acceptEntry(str, windowsFileAttributes);
        if (path != null)
          break; 
      } 
      return path;
    }
    
    public boolean hasNext() {
      if (this.nextEntry == null && !this.atEof)
        this.nextEntry = readNextEntry(); 
      return (this.nextEntry != null);
    }
    
    public Path next() {
      Path path = null;
      if (this.nextEntry == null && !this.atEof) {
        path = readNextEntry();
      } else {
        path = this.nextEntry;
        this.nextEntry = null;
      } 
      if (path == null)
        throw new NoSuchElementException(); 
      return path;
    }
    
    public void remove() throws IOException { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsDirectoryStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */