package sun.rmi.log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

public class ReliableLog {
  public static final int PreferredMajorVersion = 0;
  
  public static final int PreferredMinorVersion = 2;
  
  private boolean Debug = false;
  
  private static String snapshotPrefix = "Snapshot.";
  
  private static String logfilePrefix = "Logfile.";
  
  private static String versionFile = "Version_Number";
  
  private static String newVersionFile = "New_Version_Number";
  
  private static int intBytes = 4;
  
  private static long diskPageSize = 512L;
  
  private File dir;
  
  private int version = 0;
  
  private String logName = null;
  
  private LogFile log = null;
  
  private long snapshotBytes = 0L;
  
  private long logBytes = 0L;
  
  private int logEntries = 0;
  
  private long lastSnapshot = 0L;
  
  private long lastLog = 0L;
  
  private LogHandler handler;
  
  private final byte[] intBuf = new byte[4];
  
  private int majorFormatVersion = 0;
  
  private int minorFormatVersion = 0;
  
  private static final Constructor<? extends LogFile> logClassConstructor = getLogClassConstructor();
  
  public ReliableLog(String paramString, LogHandler paramLogHandler, boolean paramBoolean) throws IOException {
    this.Debug = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.log.debug"))).booleanValue();
    this.dir = new File(paramString);
    if ((!this.dir.exists() || !this.dir.isDirectory()) && !this.dir.mkdir())
      throw new IOException("could not create directory for log: " + paramString); 
    this.handler = paramLogHandler;
    this.lastSnapshot = 0L;
    this.lastLog = 0L;
    getVersion();
    if (this.version == 0)
      try {
        snapshot(paramLogHandler.initialSnapshot());
      } catch (IOException iOException) {
        throw iOException;
      } catch (Exception exception) {
        throw new IOException("initial snapshot failed with exception: " + exception);
      }  
  }
  
  public ReliableLog(String paramString, LogHandler paramLogHandler) throws IOException { this(paramString, paramLogHandler, false); }
  
  public Object recover() throws IOException {
    Object object;
    if (this.Debug)
      System.err.println("log.debug: recover()"); 
    if (this.version == 0)
      return null; 
    String str = versionName(snapshotPrefix);
    File file = new File(str);
    bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
    if (this.Debug)
      System.err.println("log.debug: recovering from " + str); 
    try {
      try {
        object = this.handler.recover(bufferedInputStream);
      } catch (IOException iOException) {
        throw iOException;
      } catch (Exception exception) {
        if (this.Debug)
          System.err.println("log.debug: recovery failed: " + exception); 
        throw new IOException("log recover failed with exception: " + exception);
      } 
      this.snapshotBytes = file.length();
    } finally {
      bufferedInputStream.close();
    } 
    return recoverUpdates(object);
  }
  
  public void update(Object paramObject) throws IOException { update(paramObject, true); }
  
  public void update(Object paramObject, boolean paramBoolean) throws IOException {
    if (this.log == null)
      throw new IOException("log is inaccessible, it may have been corrupted or closed"); 
    long l1 = this.log.getFilePointer();
    boolean bool = this.log.checkSpansBoundary(l1);
    writeInt(this.log, bool ? Integer.MIN_VALUE : 0);
    try {
      this.handler.writeUpdate(new LogOutputStream(this.log), paramObject);
    } catch (IOException iOException) {
      throw iOException;
    } catch (Exception exception) {
      throw (IOException)(new IOException("write update failed")).initCause(exception);
    } 
    this.log.sync();
    long l2 = this.log.getFilePointer();
    int i = (int)(l2 - l1 - intBytes);
    this.log.seek(l1);
    if (bool) {
      writeInt(this.log, i | 0x80000000);
      this.log.sync();
      this.log.seek(l1);
      this.log.writeByte(i >> 24);
      this.log.sync();
    } else {
      writeInt(this.log, i);
      this.log.sync();
    } 
    this.log.seek(l2);
    this.logBytes = l2;
    this.lastLog = System.currentTimeMillis();
    this.logEntries++;
  }
  
  private static Constructor<? extends LogFile> getLogClassConstructor() {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.rmi.log.class"));
    if (str != null)
      try {
        ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
              public ClassLoader run() { return ClassLoader.getSystemClassLoader(); }
            });
        Class clazz = classLoader.loadClass(str).asSubclass(LogFile.class);
        return clazz.getConstructor(new Class[] { String.class, String.class });
      } catch (Exception exception) {
        System.err.println("Exception occurred:");
        exception.printStackTrace();
      }  
    return null;
  }
  
  public void snapshot(Object paramObject) throws IOException {
    int i = this.version;
    incrVersion();
    String str = versionName(snapshotPrefix);
    file = new File(str);
    fileOutputStream = new FileOutputStream(file);
    try {
      try {
        this.handler.snapshot(fileOutputStream, paramObject);
      } catch (IOException iOException) {
        throw iOException;
      } catch (Exception exception) {
        throw new IOException("snapshot failed", exception);
      } 
      this.lastSnapshot = System.currentTimeMillis();
    } finally {
      fileOutputStream.close();
      this.snapshotBytes = file.length();
    } 
    openLogFile(true);
    writeVersionFile(true);
    commitToNewVersion();
    deleteSnapshot(i);
    deleteLogFile(i);
  }
  
  public void close() throws IOException {
    if (this.log == null)
      return; 
    try {
      this.log.close();
    } finally {
      this.log = null;
    } 
  }
  
  public long snapshotSize() { return this.snapshotBytes; }
  
  public long logSize() { return this.logBytes; }
  
  private void writeInt(DataOutput paramDataOutput, int paramInt) throws IOException {
    this.intBuf[0] = (byte)(paramInt >> 24);
    this.intBuf[1] = (byte)(paramInt >> 16);
    this.intBuf[2] = (byte)(paramInt >> 8);
    this.intBuf[3] = (byte)paramInt;
    paramDataOutput.write(this.intBuf);
  }
  
  private String fName(String paramString) { return this.dir.getPath() + File.separator + paramString; }
  
  private String versionName(String paramString) { return versionName(paramString, 0); }
  
  private String versionName(String paramString, int paramInt) {
    paramInt = (paramInt == 0) ? this.version : paramInt;
    return fName(paramString) + String.valueOf(paramInt);
  }
  
  private void incrVersion() throws IOException {
    do {
      this.version++;
    } while (this.version == 0);
  }
  
  private void deleteFile(String paramString) throws IOException {
    File file = new File(paramString);
    if (!file.delete())
      throw new IOException("couldn't remove file: " + paramString); 
  }
  
  private void deleteNewVersionFile() throws IOException { deleteFile(fName(newVersionFile)); }
  
  private void deleteSnapshot(int paramInt) throws IOException {
    if (paramInt == 0)
      return; 
    deleteFile(versionName(snapshotPrefix, paramInt));
  }
  
  private void deleteLogFile(int paramInt) throws IOException {
    if (paramInt == 0)
      return; 
    deleteFile(versionName(logfilePrefix, paramInt));
  }
  
  private void openLogFile(boolean paramBoolean) throws IOException {
    try {
      close();
    } catch (IOException iOException) {}
    this.logName = versionName(logfilePrefix);
    try {
      this.log = (logClassConstructor == null) ? new LogFile(this.logName, "rw") : (LogFile)logClassConstructor.newInstance(new Object[] { this.logName, "rw" });
    } catch (Exception exception) {
      throw (IOException)(new IOException("unable to construct LogFile instance")).initCause(exception);
    } 
    if (paramBoolean)
      initializeLogFile(); 
  }
  
  private void initializeLogFile() throws IOException {
    this.log.setLength(0L);
    this.majorFormatVersion = 0;
    writeInt(this.log, 0);
    this.minorFormatVersion = 2;
    writeInt(this.log, 2);
    this.logBytes = (intBytes * 2);
    this.logEntries = 0;
  }
  
  private void writeVersionFile(boolean paramBoolean) throws IOException {
    String str;
    if (paramBoolean) {
      str = newVersionFile;
    } else {
      str = versionFile;
    } 
    try(FileOutputStream null = new FileOutputStream(fName(str)); DataOutputStream null = new DataOutputStream(fileOutputStream)) {
      writeInt(dataOutputStream, this.version);
    } 
  }
  
  private void createFirstVersion() throws IOException {
    this.version = 0;
    writeVersionFile(false);
  }
  
  private void commitToNewVersion() throws IOException {
    writeVersionFile(false);
    deleteNewVersionFile();
  }
  
  private int readVersion(String paramString) throws IOException {
    try (DataInputStream null = new DataInputStream(new FileInputStream(paramString))) {
      return dataInputStream.readInt();
    } 
  }
  
  private void getVersion() throws IOException {
    try {
      this.version = readVersion(fName(newVersionFile));
      commitToNewVersion();
    } catch (IOException iOException) {
      try {
        deleteNewVersionFile();
      } catch (IOException iOException1) {}
      try {
        this.version = readVersion(fName(versionFile));
      } catch (IOException iOException1) {
        createFirstVersion();
      } 
    } 
  }
  
  private Object recoverUpdates(Object paramObject) throws IOException {
    this.logBytes = 0L;
    this.logEntries = 0;
    if (this.version == 0)
      return paramObject; 
    String str = versionName(logfilePrefix);
    bufferedInputStream = new BufferedInputStream(new FileInputStream(str));
    DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
    if (this.Debug)
      System.err.println("log.debug: reading updates from " + str); 
    try {
      this.majorFormatVersion = dataInputStream.readInt();
      this.logBytes += intBytes;
      this.minorFormatVersion = dataInputStream.readInt();
      this.logBytes += intBytes;
    } catch (EOFException eOFException) {
      openLogFile(true);
      bufferedInputStream = null;
    } 
    if (this.majorFormatVersion != 0) {
      if (this.Debug)
        System.err.println("log.debug: major version mismatch: " + this.majorFormatVersion + "." + this.minorFormatVersion); 
      throw new IOException("Log file " + this.logName + " has a version " + this.majorFormatVersion + "." + this.minorFormatVersion + " format, and this implementation  understands only version " + Character.MIN_VALUE + "." + '\002');
    } 
    try {
      while (bufferedInputStream != null) {
        int i = 0;
        try {
          i = dataInputStream.readInt();
        } catch (EOFException eOFException) {
          if (this.Debug)
            System.err.println("log.debug: log was sync'd cleanly"); 
          break;
        } 
        if (i <= 0) {
          if (this.Debug)
            System.err.println("log.debug: last update incomplete, updateLen = 0x" + Integer.toHexString(i)); 
          break;
        } 
        if (bufferedInputStream.available() < i) {
          if (this.Debug)
            System.err.println("log.debug: log was truncated"); 
          break;
        } 
        if (this.Debug)
          System.err.println("log.debug: rdUpdate size " + i); 
        try {
          paramObject = this.handler.readUpdate(new LogInputStream(bufferedInputStream, i), paramObject);
        } catch (IOException iOException) {
          throw iOException;
        } catch (Exception exception) {
          exception.printStackTrace();
          throw new IOException("read update failed with exception: " + exception);
        } 
        this.logBytes += (intBytes + i);
        this.logEntries++;
      } 
    } finally {
      if (bufferedInputStream != null)
        bufferedInputStream.close(); 
    } 
    if (this.Debug)
      System.err.println("log.debug: recovered updates: " + this.logEntries); 
    openLogFile(false);
    if (this.log == null)
      throw new IOException("rmid's log is inaccessible, it may have been corrupted or closed"); 
    this.log.seek(this.logBytes);
    this.log.setLength(this.logBytes);
    return paramObject;
  }
  
  public static class LogFile extends RandomAccessFile {
    private final FileDescriptor fd = getFD();
    
    public LogFile(String param1String1, String param1String2) throws FileNotFoundException, IOException { super(param1String1, param1String2); }
    
    protected void sync() throws IOException { this.fd.sync(); }
    
    protected boolean checkSpansBoundary(long param1Long) { return (param1Long % 512L > 508L); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\log\ReliableLog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */