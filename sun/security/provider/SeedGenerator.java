package sun.security.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import sun.security.util.Debug;

abstract class SeedGenerator {
  private static SeedGenerator instance;
  
  private static final Debug debug = Debug.getInstance("provider");
  
  public static void generateSeed(byte[] paramArrayOfByte) { instance.getSeedBytes(paramArrayOfByte); }
  
  abstract void getSeedBytes(byte[] paramArrayOfByte);
  
  static byte[] getSystemEntropy() {
    final MessageDigest md;
    try {
      messageDigest = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError("internal error: SHA-1 not available.", noSuchAlgorithmException);
    } 
    byte b = (byte)(int)System.currentTimeMillis();
    messageDigest.update(b);
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            try {
              Properties properties = System.getProperties();
              Enumeration enumeration = properties.propertyNames();
              while (enumeration.hasMoreElements()) {
                String str = (String)enumeration.nextElement();
                md.update(str.getBytes());
                md.update(properties.getProperty(str).getBytes());
              } 
              SeedGenerator.addNetworkAdapterInfo(md);
              File file = new File(properties.getProperty("java.io.tmpdir"));
              byte b = 0;
              try (DirectoryStream null = Files.newDirectoryStream(file.toPath())) {
                random = new Random();
                for (Path path : directoryStream) {
                  if (b < 'Ȁ' || random.nextBoolean())
                    md.update(path.getFileName().toString().getBytes()); 
                  if (b++ > 'Ѐ')
                    break; 
                } 
              } 
            } catch (Exception exception) {
              md.update((byte)exception.hashCode());
            } 
            Runtime runtime = Runtime.getRuntime();
            byte[] arrayOfByte = SeedGenerator.longToByteArray(runtime.totalMemory());
            md.update(arrayOfByte, 0, arrayOfByte.length);
            arrayOfByte = SeedGenerator.longToByteArray(runtime.freeMemory());
            md.update(arrayOfByte, 0, arrayOfByte.length);
            return null;
          }
        });
    return messageDigest.digest();
  }
  
  private static void addNetworkAdapterInfo(MessageDigest paramMessageDigest) {
    try {
      Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
      while (enumeration.hasMoreElements()) {
        NetworkInterface networkInterface = (NetworkInterface)enumeration.nextElement();
        paramMessageDigest.update(networkInterface.toString().getBytes());
        if (!networkInterface.isVirtual()) {
          byte[] arrayOfByte = networkInterface.getHardwareAddress();
          if (arrayOfByte != null) {
            paramMessageDigest.update(arrayOfByte);
            break;
          } 
        } 
      } 
    } catch (Exception exception) {}
  }
  
  private static byte[] longToByteArray(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    for (byte b = 0; b < 8; b++) {
      arrayOfByte[b] = (byte)(int)paramLong;
      paramLong >>= 8;
    } 
    return arrayOfByte;
  }
  
  static  {
    String str = SunEntries.getSeedSource();
    if (str.equals("file:/dev/random") || str.equals("file:/dev/urandom")) {
      try {
        instance = new NativeSeedGenerator(str);
        if (debug != null)
          debug.println("Using operating system seed generator" + str); 
      } catch (IOException iOException) {
        if (debug != null)
          debug.println("Failed to use operating system seed generator: " + iOException.toString()); 
      } 
    } else if (str.length() != 0) {
      try {
        instance = new URLSeedGenerator(str);
        if (debug != null)
          debug.println("Using URL seed generator reading from " + str); 
      } catch (IOException iOException) {
        if (debug != null)
          debug.println("Failed to create seed generator with " + str + ": " + iOException.toString()); 
      } 
    } 
    if (instance == null) {
      if (debug != null)
        debug.println("Using default threaded seed generator"); 
      instance = new ThreadedSeedGenerator();
    } 
  }
  
  private static class ThreadedSeedGenerator extends SeedGenerator implements Runnable {
    private byte[] pool = new byte[20];
    
    private int start = this.end = 0;
    
    private int end;
    
    private int count;
    
    ThreadGroup seedGroup;
    
    private static byte[] rndTab = { 
        56, 30, -107, -6, -86, 25, -83, 75, -12, -64, 
        5, Byte.MIN_VALUE, 78, 21, 16, 32, 70, -81, 37, -51, 
        -43, -46, -108, 87, 29, 17, -55, 22, -11, -111, 
        -115, 84, -100, 108, -45, -15, -98, 72, -33, -28, 
        31, -52, -37, -117, -97, -27, 93, -123, 47, 126, 
        -80, -62, -93, -79, 61, -96, -65, -5, -47, -119, 
        14, 89, 81, -118, -88, 20, 67, -126, -113, 60, 
        -102, 55, 110, 28, 85, 121, 122, -58, 2, 45, 
        43, 24, -9, 103, -13, 102, -68, -54, -101, -104, 
        19, 13, -39, -26, -103, 62, 77, 51, 44, 111, 
        73, 18, -127, -82, 4, -30, 11, -99, -74, 40, 
        -89, 42, -76, -77, -94, -35, -69, 35, 120, 76, 
        33, -73, -7, 82, -25, -10, 88, 125, -112, 58, 
        83, 95, 6, 10, 98, -34, 80, 15, -91, 86, 
        -19, 52, -17, 117, 49, -63, 118, -90, 36, -116, 
        -40, -71, 97, -53, -109, -85, 109, -16, -3, 104, 
        -95, 68, 54, 34, 26, 114, -1, 106, -121, 3, 
        66, 0, 100, -84, 57, 107, 119, -42, 112, -61, 
        1, 48, 38, 12, -56, -57, 39, -106, -72, 41, 
        7, 71, -29, -59, -8, -38, 79, -31, 124, -124, 
        8, 91, 116, 99, -4, 9, -36, -78, 63, -49, 
        -67, -87, 59, 101, -32, 92, 94, 53, -41, 115, 
        -66, -70, -122, 50, -50, -22, -20, -18, -21, 23, 
        -2, -48, 96, 65, -105, 123, -14, -110, 69, -24, 
        -120, -75, 74, Byte.MAX_VALUE, -60, 113, 90, -114, 105, 46, 
        27, -125, -23, -44, 64 };
    
    ThreadedSeedGenerator() {
      try {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new InternalError("internal error: SHA-1 not available.", noSuchAlgorithmException);
      } 
      final ThreadGroup[] finalsg = new ThreadGroup[1];
      Thread thread = (Thread)AccessController.doPrivileged(new PrivilegedAction<Thread>() {
            public Thread run() {
              ThreadGroup threadGroup1;
              ThreadGroup threadGroup2;
              for (threadGroup2 = Thread.currentThread().getThreadGroup(); (threadGroup1 = threadGroup2.getParent()) != null; threadGroup2 = threadGroup1);
              finalsg[0] = new ThreadGroup(threadGroup2, "SeedGenerator ThreadGroup");
              Thread thread = new Thread(finalsg[0], SeedGenerator.ThreadedSeedGenerator.this, "SeedGenerator Thread");
              thread.setPriority(1);
              thread.setDaemon(true);
              return thread;
            }
          });
      this.seedGroup = arrayOfThreadGroup[0];
      thread.start();
    }
    
    public final void run() {
      try {
        while (true) {
          synchronized (this) {
            while (this.count >= this.pool.length)
              wait(); 
          } 
          byte b = 0;
          byte b2 = 0;
          byte b1 = b2;
          while (b1 < '切' && b2 < 6) {
            try {
              BogusThread bogusThread = new BogusThread(null);
              Thread thread = new Thread(this.seedGroup, bogusThread, "SeedGenerator Thread");
              thread.start();
            } catch (Exception exception) {
              throw new InternalError("internal error: SeedGenerator thread creation error.", exception);
            } 
            byte b3 = 0;
            long l = System.currentTimeMillis() + 250L;
            while (System.currentTimeMillis() < l) {
              synchronized (this) {
              
              } 
              b3++;
            } 
            b = (byte)(b ^ rndTab[b3 % 'ÿ']);
            b1 += b3;
            b2++;
          } 
          synchronized (this) {
            this.pool[this.end] = b;
            this.end++;
            this.count++;
            if (this.end >= this.pool.length)
              this.end = 0; 
            notifyAll();
          } 
        } 
      } catch (Exception exception) {
        throw new InternalError("internal error: SeedGenerator thread generated an exception.", exception);
      } 
    }
    
    void getSeedBytes(byte[] param1ArrayOfByte) {
      for (byte b = 0; b < param1ArrayOfByte.length; b++)
        param1ArrayOfByte[b] = getSeedByte(); 
    }
    
    byte getSeedByte() {
      byte b;
      try {
        synchronized (this) {
          while (this.count <= 0)
            wait(); 
        } 
      } catch (Exception exception) {
        if (this.count <= 0)
          throw new InternalError("internal error: SeedGenerator thread generated an exception.", exception); 
      } 
      synchronized (this) {
        b = this.pool[this.start];
        this.pool[this.start] = 0;
        this.start++;
        this.count--;
        if (this.start == this.pool.length)
          this.start = 0; 
        notifyAll();
      } 
      return b;
    }
    
    private static class BogusThread implements Runnable {
      private BogusThread() {}
      
      public final void run() {
        try {
          for (byte b = 0; b < 5; b++)
            Thread.sleep(50L); 
        } catch (Exception exception) {}
      }
    }
  }
  
  static class URLSeedGenerator extends SeedGenerator {
    private String deviceName;
    
    private InputStream seedStream;
    
    URLSeedGenerator(String param1String) throws IOException {
      if (param1String == null)
        throw new IOException("No random source specified"); 
      this.deviceName = param1String;
      init();
    }
    
    private void init() {
      final URL device = new URL(this.deviceName);
      try {
        this.seedStream = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
              public InputStream run() throws IOException {
                if (device.getProtocol().equalsIgnoreCase("file")) {
                  File file = SunEntries.getDeviceFile(device);
                  return new FileInputStream(file);
                } 
                return device.openStream();
              }
            });
      } catch (Exception exception) {
        throw new IOException("Failed to open " + this.deviceName, exception.getCause());
      } 
    }
    
    void getSeedBytes(byte[] param1ArrayOfByte) {
      int i = param1ArrayOfByte.length;
      int j = 0;
      try {
        while (j < i) {
          int k = this.seedStream.read(param1ArrayOfByte, j, i - j);
          if (k < 0)
            throw new InternalError("URLSeedGenerator " + this.deviceName + " reached end of file"); 
          j += k;
        } 
      } catch (IOException iOException) {
        throw new InternalError("URLSeedGenerator " + this.deviceName + " generated exception: " + iOException.getMessage(), iOException);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\SeedGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */