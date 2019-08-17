package sun.security.krb5.internal.rcache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.ReplayCache;

public class DflCache extends ReplayCache {
  private static final int KRB5_RV_VNO = 1281;
  
  private static final int EXCESSREPS = 30;
  
  private final String source;
  
  private static int uid;
  
  public DflCache(String paramString) { this.source = paramString; }
  
  private static String defaultPath() { return (String)AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")); }
  
  private static String defaultFile(String paramString) {
    int i = paramString.indexOf('/');
    if (i == -1)
      i = paramString.indexOf('@'); 
    if (i != -1)
      paramString = paramString.substring(0, i); 
    if (uid != -1)
      paramString = paramString + "_" + uid; 
    return paramString;
  }
  
  private static Path getFileName(String paramString1, String paramString2) {
    String str2;
    String str1;
    if (paramString1.equals("dfl")) {
      str1 = defaultPath();
      str2 = defaultFile(paramString2);
    } else if (paramString1.startsWith("dfl:")) {
      paramString1 = paramString1.substring(4);
      int i = paramString1.lastIndexOf('/');
      int j = paramString1.lastIndexOf('\\');
      if (j > i)
        i = j; 
      if (i == -1) {
        str1 = defaultPath();
        str2 = paramString1;
      } else if ((new File(paramString1)).isDirectory()) {
        str1 = paramString1;
        str2 = defaultFile(paramString2);
      } else {
        str1 = null;
        str2 = paramString1;
      } 
    } else {
      throw new IllegalArgumentException();
    } 
    return (new File(str1, str2)).toPath();
  }
  
  public void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash) throws KrbApErrException {
    try {
      checkAndStore0(paramKerberosTime, paramAuthTimeWithHash);
    } catch (IOException iOException) {
      KrbApErrException krbApErrException = new KrbApErrException(60);
      krbApErrException.initCause(iOException);
      throw krbApErrException;
    } 
  }
  
  private void checkAndStore0(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash) throws KrbApErrException {
    Path path = getFileName(this.source, paramAuthTimeWithHash.server);
    int i = 0;
    try (Storage null = new Storage(null)) {
      try {
        i = storage.loadAndCheck(path, paramAuthTimeWithHash, paramKerberosTime);
      } catch (IOException iOException) {
        Storage.create(path);
        i = storage.loadAndCheck(path, paramAuthTimeWithHash, paramKerberosTime);
      } 
      storage.append(paramAuthTimeWithHash);
    } 
    if (i > 30)
      Storage.expunge(path, paramKerberosTime); 
  }
  
  static  {
    try {
      Class clazz = Class.forName("com.sun.security.auth.module.UnixSystem");
      uid = (int)((Long)clazz.getMethod("getUid", new Class[0]).invoke(clazz.newInstance(), new Object[0])).longValue();
    } catch (Exception exception) {
      uid = -1;
    } 
  }
  
  private static class Storage implements Closeable {
    SeekableByteChannel chan;
    
    private Storage() {}
    
    private static void create(Path param1Path) throws IOException {
      SeekableByteChannel seekableByteChannel = createNoClose(param1Path);
      Object object = null;
      if (seekableByteChannel != null)
        if (object != null) {
          try {
            seekableByteChannel.close();
          } catch (Throwable throwable) {
            object.addSuppressed(throwable);
          } 
        } else {
          seekableByteChannel.close();
        }  
      makeMine(param1Path);
    }
    
    private static void makeMine(Path param1Path) throws IOException {
      try {
        HashSet hashSet = new HashSet();
        hashSet.add(PosixFilePermission.OWNER_READ);
        hashSet.add(PosixFilePermission.OWNER_WRITE);
        Files.setPosixFilePermissions(param1Path, hashSet);
      } catch (UnsupportedOperationException unsupportedOperationException) {}
    }
    
    private static SeekableByteChannel createNoClose(Path param1Path) throws IOException {
      SeekableByteChannel seekableByteChannel = Files.newByteChannel(param1Path, new OpenOption[] { StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE });
      ByteBuffer byteBuffer = ByteBuffer.allocate(6);
      byteBuffer.putShort((short)1281);
      byteBuffer.order(ByteOrder.nativeOrder());
      byteBuffer.putInt(KerberosTime.getDefaultSkew());
      byteBuffer.flip();
      seekableByteChannel.write(byteBuffer);
      return seekableByteChannel;
    }
    
    private static void expunge(Path param1Path, KerberosTime param1KerberosTime) throws IOException {
      Path path = Files.createTempFile(param1Path.getParent(), "rcache", null, new java.nio.file.attribute.FileAttribute[0]);
      try(SeekableByteChannel null = Files.newByteChannel(param1Path, new OpenOption[0]); SeekableByteChannel null = createNoClose(path)) {
        l = (param1KerberosTime.getSeconds() - readHeader(seekableByteChannel));
      } 
      makeMine(path);
      Files.move(path, param1Path, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE });
    }
    
    private int loadAndCheck(Path param1Path, AuthTimeWithHash param1AuthTimeWithHash, KerberosTime param1KerberosTime) throws IOException, KrbApErrException {
      byte b = 0;
      if (Files.isSymbolicLink(param1Path))
        throw new IOException("Symlink not accepted"); 
      try {
        Set set = Files.getPosixFilePermissions(param1Path, new java.nio.file.LinkOption[0]);
        if (uid != -1 && ((Integer)Files.getAttribute(param1Path, "unix:uid", new java.nio.file.LinkOption[0])).intValue() != uid)
          throw new IOException("Not mine"); 
        if (set.contains(PosixFilePermission.GROUP_READ) || set.contains(PosixFilePermission.GROUP_WRITE) || set.contains(PosixFilePermission.GROUP_EXECUTE) || set.contains(PosixFilePermission.OTHERS_READ) || set.contains(PosixFilePermission.OTHERS_WRITE) || set.contains(PosixFilePermission.OTHERS_EXECUTE))
          throw new IOException("Accessible by someone else"); 
      } catch (UnsupportedOperationException unsupportedOperationException) {}
      this.chan = Files.newByteChannel(param1Path, new OpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.READ });
      long l1 = (param1KerberosTime.getSeconds() - readHeader(this.chan));
      long l2 = 0L;
      boolean bool = false;
      try {
        while (true) {
          l2 = this.chan.position();
          AuthTime authTime = AuthTime.readFrom(this.chan);
          if (authTime instanceof AuthTimeWithHash) {
            if (param1AuthTimeWithHash.equals(authTime))
              throw new KrbApErrException(34); 
            if (param1AuthTimeWithHash.isSameIgnoresHash(authTime))
              bool = true; 
          } else if (param1AuthTimeWithHash.isSameIgnoresHash(authTime) && !bool) {
            throw new KrbApErrException(34);
          } 
          if (authTime.ctime < l1) {
            b++;
            continue;
          } 
          b--;
        } 
      } catch (BufferUnderflowException bufferUnderflowException) {
        this.chan.position(l2);
        return b;
      } 
    }
    
    private static int readHeader(SeekableByteChannel param1SeekableByteChannel) throws IOException {
      ByteBuffer byteBuffer = ByteBuffer.allocate(6);
      param1SeekableByteChannel.read(byteBuffer);
      if (byteBuffer.getShort(0) != 1281)
        throw new IOException("Not correct rcache version"); 
      byteBuffer.order(ByteOrder.nativeOrder());
      return byteBuffer.getInt(2);
    }
    
    private void append(AuthTimeWithHash param1AuthTimeWithHash) throws IOException {
      ByteBuffer byteBuffer = ByteBuffer.wrap(param1AuthTimeWithHash.encode(true));
      this.chan.write(byteBuffer);
      byteBuffer = ByteBuffer.wrap(param1AuthTimeWithHash.encode(false));
      this.chan.write(byteBuffer);
    }
    
    public void close() {
      if (this.chan != null)
        this.chan.close(); 
      this.chan = null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\krb5\internal\rcache\DflCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */