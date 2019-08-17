package java.lang;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

final class ProcessImpl extends Process {
  private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
  
  private static final int VERIFICATION_CMD_BAT = 0;
  
  private static final int VERIFICATION_WIN32 = 1;
  
  private static final int VERIFICATION_LEGACY = 2;
  
  private static final char[][] ESCAPE_VERIFICATION = { { ' ', '\t', '<', '>', '&', '|', '^' }, { ' ', '\t', '<', '>' }, { ' ', '\t' } };
  
  private long handle = 0L;
  
  private OutputStream stdin_stream;
  
  private InputStream stdout_stream;
  
  private InputStream stderr_stream;
  
  private static final int STILL_ACTIVE = getStillActive();
  
  private static FileOutputStream newFileOutputStream(File paramFile, boolean paramBoolean) throws IOException {
    if (paramBoolean) {
      String str = paramFile.getPath();
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkWrite(str); 
      long l = openForAtomicAppend(str);
      final FileDescriptor fd = new FileDescriptor();
      fdAccess.setHandle(fileDescriptor, l);
      return (FileOutputStream)AccessController.doPrivileged(new PrivilegedAction<FileOutputStream>() {
            public FileOutputStream run() { return new FileOutputStream(fd); }
          });
    } 
    return new FileOutputStream(paramFile);
  }
  
  static Process start(String[] paramArrayOfString, Map<String, String> paramMap, String paramString, ProcessBuilder.Redirect[] paramArrayOfRedirect, boolean paramBoolean) throws IOException {
    String str = ProcessEnvironment.toEnvironmentBlock(paramMap);
    fileInputStream = null;
    fileOutputStream1 = null;
    fileOutputStream2 = null;
    try {
      long[] arrayOfLong;
      if (paramArrayOfRedirect == null) {
        arrayOfLong = new long[] { -1L, -1L, -1L };
      } else {
        arrayOfLong = new long[3];
        if (paramArrayOfRedirect[false] == ProcessBuilder.Redirect.PIPE) {
          arrayOfLong[0] = -1L;
        } else if (paramArrayOfRedirect[false] == ProcessBuilder.Redirect.INHERIT) {
          arrayOfLong[0] = fdAccess.getHandle(FileDescriptor.in);
        } else {
          fileInputStream = new FileInputStream(paramArrayOfRedirect[0].file());
          arrayOfLong[0] = fdAccess.getHandle(fileInputStream.getFD());
        } 
        if (paramArrayOfRedirect[true] == ProcessBuilder.Redirect.PIPE) {
          arrayOfLong[1] = -1L;
        } else if (paramArrayOfRedirect[true] == ProcessBuilder.Redirect.INHERIT) {
          arrayOfLong[1] = fdAccess.getHandle(FileDescriptor.out);
        } else {
          fileOutputStream1 = newFileOutputStream(paramArrayOfRedirect[1].file(), paramArrayOfRedirect[1].append());
          arrayOfLong[1] = fdAccess.getHandle(fileOutputStream1.getFD());
        } 
        if (paramArrayOfRedirect[2] == ProcessBuilder.Redirect.PIPE) {
          arrayOfLong[2] = -1L;
        } else if (paramArrayOfRedirect[2] == ProcessBuilder.Redirect.INHERIT) {
          arrayOfLong[2] = fdAccess.getHandle(FileDescriptor.err);
        } else {
          fileOutputStream2 = newFileOutputStream(paramArrayOfRedirect[2].file(), paramArrayOfRedirect[2].append());
          arrayOfLong[2] = fdAccess.getHandle(fileOutputStream2.getFD());
        } 
      } 
      return new ProcessImpl(paramArrayOfString, str, paramString, arrayOfLong, paramBoolean);
    } finally {
      try {
        if (fileInputStream != null)
          fileInputStream.close(); 
      } finally {
        try {
          if (fileOutputStream1 != null)
            fileOutputStream1.close(); 
        } finally {
          if (fileOutputStream2 != null)
            fileOutputStream2.close(); 
        } 
      } 
    } 
  }
  
  private static String[] getTokensFromCommand(String paramString) {
    ArrayList arrayList = new ArrayList(8);
    Matcher matcher = PATTERN.matcher(paramString);
    while (matcher.find())
      arrayList.add(matcher.group()); 
    return (String[])arrayList.toArray(new String[arrayList.size()]);
  }
  
  private static String createCommandLine(int paramInt, String paramString, String[] paramArrayOfString) {
    StringBuilder stringBuilder = new StringBuilder(80);
    stringBuilder.append(paramString);
    for (byte b = 1; b < paramArrayOfString.length; b++) {
      stringBuilder.append(' ');
      String str = paramArrayOfString[b];
      if (needsEscaping(paramInt, str)) {
        stringBuilder.append('"').append(str);
        if (paramInt != 0 && str.endsWith("\\"))
          stringBuilder.append('\\'); 
        stringBuilder.append('"');
      } else {
        stringBuilder.append(str);
      } 
    } 
    return stringBuilder.toString();
  }
  
  private static boolean isQuoted(boolean paramBoolean, String paramString1, String paramString2) {
    int i = paramString1.length() - 1;
    if (i >= 1 && paramString1.charAt(0) == '"' && paramString1.charAt(i) == '"') {
      if (paramBoolean && paramString1.indexOf('"', 1) != i)
        throw new IllegalArgumentException(paramString2); 
      return true;
    } 
    if (paramBoolean && paramString1.indexOf('"') >= 0)
      throw new IllegalArgumentException(paramString2); 
    return false;
  }
  
  private static boolean needsEscaping(int paramInt, String paramString) {
    boolean bool = isQuoted((paramInt == 0), paramString, "Argument has embedded quote, use the explicit CMD.EXE call.");
    if (!bool) {
      char[] arrayOfChar = ESCAPE_VERIFICATION[paramInt];
      for (byte b = 0; b < arrayOfChar.length; b++) {
        if (paramString.indexOf(arrayOfChar[b]) >= 0)
          return true; 
      } 
    } 
    return false;
  }
  
  private static String getExecutablePath(String paramString) throws IOException {
    boolean bool = isQuoted(true, paramString, "Executable name has embedded quote, split the arguments");
    File file = new File(bool ? paramString.substring(1, paramString.length() - 1) : paramString);
    return file.getPath();
  }
  
  private boolean isShellFile(String paramString) {
    String str = paramString.toUpperCase();
    return (str.endsWith(".CMD") || str.endsWith(".BAT"));
  }
  
  private String quoteString(String paramString) throws IOException {
    StringBuilder stringBuilder = new StringBuilder(paramString.length() + 2);
    return stringBuilder.append('"').append(paramString).append('"').toString();
  }
  
  private ProcessImpl(String[] paramArrayOfString, String paramString1, String paramString2, final long[] stdHandles, boolean paramBoolean) throws IOException {
    SecurityManager securityManager = System.getSecurityManager();
    boolean bool = false;
    if (securityManager == null) {
      bool = true;
      String str1 = System.getProperty("jdk.lang.Process.allowAmbiguousCommands");
      if (str1 != null)
        bool = !"false".equalsIgnoreCase(str1) ? 1 : 0; 
    } 
    if (bool) {
      String str1 = (new File(paramArrayOfString[0])).getPath();
      if (needsEscaping(2, str1))
        str1 = quoteString(str1); 
      str = createCommandLine(2, str1, paramArrayOfString);
    } else {
      String str1;
      try {
        str1 = getExecutablePath(paramArrayOfString[0]);
      } catch (IllegalArgumentException illegalArgumentException) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str2 : paramArrayOfString)
          stringBuilder.append(str2).append(' '); 
        paramArrayOfString = getTokensFromCommand(stringBuilder.toString());
        str1 = getExecutablePath(paramArrayOfString[0]);
        if (securityManager != null)
          securityManager.checkExec(str1); 
      } 
      str = createCommandLine(isShellFile(str1) ? 0 : 1, quoteString(str1), paramArrayOfString);
    } 
    this.handle = create(str, paramString1, paramString2, paramArrayOfLong, paramBoolean);
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            if (stdHandles[0] == -1L) {
              ProcessImpl.this.stdin_stream = ProcessBuilder.NullOutputStream.INSTANCE;
            } else {
              FileDescriptor fileDescriptor = new FileDescriptor();
              fdAccess.setHandle(fileDescriptor, stdHandles[0]);
              ProcessImpl.this.stdin_stream = new BufferedOutputStream(new FileOutputStream(fileDescriptor));
            } 
            if (stdHandles[1] == -1L) {
              ProcessImpl.this.stdout_stream = ProcessBuilder.NullInputStream.INSTANCE;
            } else {
              FileDescriptor fileDescriptor = new FileDescriptor();
              fdAccess.setHandle(fileDescriptor, stdHandles[1]);
              ProcessImpl.this.stdout_stream = new BufferedInputStream(new FileInputStream(fileDescriptor));
            } 
            if (stdHandles[2] == -1L) {
              ProcessImpl.this.stderr_stream = ProcessBuilder.NullInputStream.INSTANCE;
            } else {
              FileDescriptor fileDescriptor = new FileDescriptor();
              fdAccess.setHandle(fileDescriptor, stdHandles[2]);
              ProcessImpl.this.stderr_stream = new FileInputStream(fileDescriptor);
            } 
            return null;
          }
        });
  }
  
  public OutputStream getOutputStream() { return this.stdin_stream; }
  
  public InputStream getInputStream() { return this.stdout_stream; }
  
  public InputStream getErrorStream() { return this.stderr_stream; }
  
  protected void finalize() { closeHandle(this.handle); }
  
  private static native int getStillActive();
  
  public int exitValue() {
    int i = getExitCodeProcess(this.handle);
    if (i == STILL_ACTIVE)
      throw new IllegalThreadStateException("process has not exited"); 
    return i;
  }
  
  private static native int getExitCodeProcess(long paramLong);
  
  public int waitFor() {
    waitForInterruptibly(this.handle);
    if (Thread.interrupted())
      throw new InterruptedException(); 
    return exitValue();
  }
  
  private static native void waitForInterruptibly(long paramLong);
  
  public boolean waitFor(long paramLong, TimeUnit paramTimeUnit) throws InterruptedException {
    if (getExitCodeProcess(this.handle) != STILL_ACTIVE)
      return true; 
    if (paramLong <= 0L)
      return false; 
    long l1 = paramTimeUnit.toNanos(paramLong);
    long l2 = System.nanoTime() + l1;
    do {
      long l = TimeUnit.NANOSECONDS.toMillis(l1 + 999999L);
      waitForTimeoutInterruptibly(this.handle, l);
      if (Thread.interrupted())
        throw new InterruptedException(); 
      if (getExitCodeProcess(this.handle) != STILL_ACTIVE)
        return true; 
      l1 = l2 - System.nanoTime();
    } while (l1 > 0L);
    return (getExitCodeProcess(this.handle) != STILL_ACTIVE);
  }
  
  private static native void waitForTimeoutInterruptibly(long paramLong1, long paramLong2);
  
  public void destroy() { terminateProcess(this.handle); }
  
  public Process destroyForcibly() {
    destroy();
    return this;
  }
  
  private static native void terminateProcess(long paramLong);
  
  public boolean isAlive() { return isProcessAlive(this.handle); }
  
  private static native boolean isProcessAlive(long paramLong);
  
  private static native long create(String paramString1, String paramString2, String paramString3, long[] paramArrayOfLong, boolean paramBoolean) throws IOException;
  
  private static native long openForAtomicAppend(String paramString) throws IOException;
  
  private static native boolean closeHandle(long paramLong);
  
  private static class LazyPattern {
    private static final Pattern PATTERN = Pattern.compile("[^\\s\"]+|\"[^\"]*\"");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ProcessImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */