package java.util.logging;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

public class FileHandler extends StreamHandler {
  private MeteredStream meter;
  
  private boolean append;
  
  private int limit;
  
  private int count;
  
  private String pattern;
  
  private String lockFileName;
  
  private FileChannel lockFileChannel;
  
  private File[] files;
  
  private static final int DEFAULT_MAX_LOCKS = 100;
  
  private static int maxLocks;
  
  private static final Set<String> locks = new HashSet();
  
  private void open(File paramFile, boolean paramBoolean) throws IOException {
    int i = 0;
    if (paramBoolean)
      i = (int)paramFile.length(); 
    FileOutputStream fileOutputStream = new FileOutputStream(paramFile.toString(), paramBoolean);
    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
    this.meter = new MeteredStream(bufferedOutputStream, i);
    setOutputStream(this.meter);
  }
  
  private void configure() {
    LogManager logManager = LogManager.getLogManager();
    String str = getClass().getName();
    this.pattern = logManager.getStringProperty(str + ".pattern", "%h/java%u.log");
    this.limit = logManager.getIntProperty(str + ".limit", 0);
    if (this.limit < 0)
      this.limit = 0; 
    this.count = logManager.getIntProperty(str + ".count", 1);
    if (this.count <= 0)
      this.count = 1; 
    this.append = logManager.getBooleanProperty(str + ".append", false);
    setLevel(logManager.getLevelProperty(str + ".level", Level.ALL));
    setFilter(logManager.getFilterProperty(str + ".filter", null));
    setFormatter(logManager.getFormatterProperty(str + ".formatter", new XMLFormatter()));
    try {
      setEncoding(logManager.getStringProperty(str + ".encoding", null));
    } catch (Exception exception) {
      try {
        setEncoding(null);
      } catch (Exception exception1) {}
    } 
  }
  
  public FileHandler() {
    checkPermission();
    configure();
    openFiles();
  }
  
  public FileHandler(String paramString) throws IOException, SecurityException {
    if (paramString.length() < 1)
      throw new IllegalArgumentException(); 
    checkPermission();
    configure();
    this.pattern = paramString;
    this.limit = 0;
    this.count = 1;
    openFiles();
  }
  
  public FileHandler(String paramString, boolean paramBoolean) throws IOException, SecurityException {
    if (paramString.length() < 1)
      throw new IllegalArgumentException(); 
    checkPermission();
    configure();
    this.pattern = paramString;
    this.limit = 0;
    this.count = 1;
    this.append = paramBoolean;
    openFiles();
  }
  
  public FileHandler(String paramString, int paramInt1, int paramInt2) throws IOException, SecurityException {
    if (paramInt1 < 0 || paramInt2 < 1 || paramString.length() < 1)
      throw new IllegalArgumentException(); 
    checkPermission();
    configure();
    this.pattern = paramString;
    this.limit = paramInt1;
    this.count = paramInt2;
    openFiles();
  }
  
  public FileHandler(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) throws IOException, SecurityException {
    if (paramInt1 < 0 || paramInt2 < 1 || paramString.length() < 1)
      throw new IllegalArgumentException(); 
    checkPermission();
    configure();
    this.pattern = paramString;
    this.limit = paramInt1;
    this.count = paramInt2;
    this.append = paramBoolean;
    openFiles();
  }
  
  private boolean isParentWritable(Path paramPath) {
    Path path = paramPath.getParent();
    if (path == null)
      path = paramPath.toAbsolutePath().getParent(); 
    return (path != null && Files.isWritable(path));
  }
  
  private void openFiles() {
    LogManager logManager = LogManager.getLogManager();
    logManager.checkPermission();
    if (this.count < 1)
      throw new IllegalArgumentException("file count = " + this.count); 
    if (this.limit < 0)
      this.limit = 0; 
    InitializationErrorManager initializationErrorManager = new InitializationErrorManager(null);
    setErrorManager(initializationErrorManager);
    byte b = -1;
    while (true) {
      if (++b > maxLocks)
        throw new IOException("Couldn't get lock for " + this.pattern + ", maxLocks: " + maxLocks); 
      this.lockFileName = generate(this.pattern, 0, b).toString() + ".lck";
      synchronized (locks) {
        boolean bool2;
        if (locks.contains(this.lockFileName))
          continue; 
        Path path = Paths.get(this.lockFileName, new String[0]);
        FileChannel fileChannel = null;
        byte b2 = -1;
        boolean bool1 = false;
        while (fileChannel == null && b2++ < 1) {
          try {
            fileChannel = FileChannel.open(path, new OpenOption[] { StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE });
            bool1 = true;
          } catch (FileAlreadyExistsException fileAlreadyExistsException) {
            if (Files.isRegularFile(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }) && isParentWritable(path))
              try {
                fileChannel = FileChannel.open(path, new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.APPEND });
                continue;
              } catch (NoSuchFileException noSuchFileException) {
                continue;
              } catch (IOException iOException) {
                break;
              }  
            break;
          } 
        } 
        if (fileChannel == null)
          continue; 
        this.lockFileChannel = fileChannel;
        try {
          bool2 = (this.lockFileChannel.tryLock() != null) ? 1 : 0;
        } catch (IOException iOException) {
          bool2 = bool1;
        } catch (OverlappingFileLockException overlappingFileLockException) {
          bool2 = false;
        } 
        if (bool2) {
          locks.add(this.lockFileName);
          break;
        } 
        this.lockFileChannel.close();
      } 
    } 
    this.files = new File[this.count];
    for (byte b1 = 0; b1 < this.count; b1++)
      this.files[b1] = generate(this.pattern, b1, b); 
    if (this.append) {
      open(this.files[0], true);
    } else {
      rotate();
    } 
    Exception exception = initializationErrorManager.lastException;
    if (exception != null) {
      if (exception instanceof IOException)
        throw (IOException)exception; 
      if (exception instanceof SecurityException)
        throw (SecurityException)exception; 
      throw new IOException("Exception: " + exception);
    } 
    setErrorManager(new ErrorManager());
  }
  
  private File generate(String paramString, int paramInt1, int paramInt2) throws IOException {
    File file = null;
    String str = "";
    byte b = 0;
    boolean bool1 = false;
    boolean bool2 = false;
    while (b < paramString.length()) {
      char c = paramString.charAt(b);
      b++;
      char c1 = Character.MIN_VALUE;
      if (b < paramString.length())
        c1 = Character.toLowerCase(paramString.charAt(b)); 
      if (c == '/') {
        if (file == null) {
          file = new File(str);
        } else {
          file = new File(file, str);
        } 
        str = "";
        continue;
      } 
      if (c == '%') {
        if (c1 == 't') {
          String str1 = System.getProperty("java.io.tmpdir");
          if (str1 == null)
            str1 = System.getProperty("user.home"); 
          file = new File(str1);
          b++;
          str = "";
          continue;
        } 
        if (c1 == 'h') {
          file = new File(System.getProperty("user.home"));
          if (isSetUID())
            throw new IOException("can't use %h in set UID program"); 
          b++;
          str = "";
          continue;
        } 
        if (c1 == 'g') {
          str = str + paramInt1;
          bool1 = true;
          b++;
          continue;
        } 
        if (c1 == 'u') {
          str = str + paramInt2;
          bool2 = true;
          b++;
          continue;
        } 
        if (c1 == '%') {
          str = str + "%";
          b++;
          continue;
        } 
      } 
      str = str + c;
    } 
    if (this.count > 1 && !bool1)
      str = str + "." + paramInt1; 
    if (paramInt2 > 0 && !bool2)
      str = str + "." + paramInt2; 
    if (str.length() > 0)
      if (file == null) {
        file = new File(str);
      } else {
        file = new File(file, str);
      }  
    return file;
  }
  
  private void rotate() {
    Level level = getLevel();
    setLevel(Level.OFF);
    super.close();
    for (i = this.count - 2; i >= 0; i--) {
      File file1 = this.files[i];
      File file2 = this.files[i + 1];
      if (file1.exists()) {
        if (file2.exists())
          file2.delete(); 
        file1.renameTo(file2);
      } 
    } 
    try {
      open(this.files[0], false);
    } catch (IOException i) {
      IOException iOException;
      reportError(null, iOException, 4);
    } 
    setLevel(level);
  }
  
  public void publish(LogRecord paramLogRecord) {
    if (!isLoggable(paramLogRecord))
      return; 
    super.publish(paramLogRecord);
    flush();
    if (this.limit > 0 && this.meter.written >= this.limit)
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
              FileHandler.this.rotate();
              return null;
            }
          }); 
  }
  
  public void close() {
    super.close();
    if (this.lockFileName == null)
      return; 
    try {
      this.lockFileChannel.close();
    } catch (Exception exception) {}
    synchronized (locks) {
      locks.remove(this.lockFileName);
    } 
    (new File(this.lockFileName)).delete();
    this.lockFileName = null;
    this.lockFileChannel = null;
  }
  
  private static native boolean isSetUID();
  
  static  {
    maxLocks = ((Integer)AccessController.doPrivileged(() -> Integer.getInteger("jdk.internal.FileHandlerLogging.maxLocks", 100))).intValue();
    if (maxLocks <= 0)
      maxLocks = 100; 
  }
  
  private static class InitializationErrorManager extends ErrorManager {
    Exception lastException;
    
    private InitializationErrorManager() {}
    
    public void error(String param1String, Exception param1Exception, int param1Int) { this.lastException = param1Exception; }
  }
  
  private class MeteredStream extends OutputStream {
    final OutputStream out;
    
    int written;
    
    MeteredStream(OutputStream param1OutputStream, int param1Int) {
      this.out = param1OutputStream;
      this.written = param1Int;
    }
    
    public void write(int param1Int) throws IOException {
      this.out.write(param1Int);
      this.written++;
    }
    
    public void write(byte[] param1ArrayOfByte) throws IOException {
      this.out.write(param1ArrayOfByte);
      this.written += param1ArrayOfByte.length;
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      this.out.write(param1ArrayOfByte, param1Int1, param1Int2);
      this.written += param1Int2;
    }
    
    public void flush() { this.out.flush(); }
    
    public void close() { this.out.close(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\logging\FileHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */