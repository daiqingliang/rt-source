package sun.nio.fs;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;

class WindowsException extends Exception {
  static final long serialVersionUID = 2765039493083748820L;
  
  private int lastError;
  
  private String msg;
  
  WindowsException(int paramInt) {
    this.lastError = paramInt;
    this.msg = null;
  }
  
  WindowsException(String paramString) {
    this.lastError = 0;
    this.msg = paramString;
  }
  
  int lastError() { return this.lastError; }
  
  String errorString() {
    if (this.msg == null) {
      this.msg = WindowsNativeDispatcher.FormatMessage(this.lastError);
      if (this.msg == null)
        this.msg = "Unknown error: 0x" + Integer.toHexString(this.lastError); 
    } 
    return this.msg;
  }
  
  public String getMessage() { return errorString(); }
  
  private IOException translateToIOException(String paramString1, String paramString2) { return (lastError() == 0) ? new IOException(errorString()) : ((lastError() == 2 || lastError() == 3) ? new NoSuchFileException(paramString1, paramString2, null) : ((lastError() == 80 || lastError() == 183) ? new FileAlreadyExistsException(paramString1, paramString2, null) : ((lastError() == 5) ? new AccessDeniedException(paramString1, paramString2, null) : new FileSystemException(paramString1, paramString2, errorString())))); }
  
  void rethrowAsIOException(String paramString) {
    IOException iOException = translateToIOException(paramString, null);
    throw iOException;
  }
  
  void rethrowAsIOException(WindowsPath paramWindowsPath1, WindowsPath paramWindowsPath2) throws IOException {
    String str1 = (paramWindowsPath1 == null) ? null : paramWindowsPath1.getPathForExceptionMessage();
    String str2 = (paramWindowsPath2 == null) ? null : paramWindowsPath2.getPathForExceptionMessage();
    IOException iOException = translateToIOException(str1, str2);
    throw iOException;
  }
  
  void rethrowAsIOException(WindowsPath paramWindowsPath) throws IOException { rethrowAsIOException(paramWindowsPath, null); }
  
  IOException asIOException(WindowsPath paramWindowsPath) { return translateToIOException(paramWindowsPath.getPathForExceptionMessage(), null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */