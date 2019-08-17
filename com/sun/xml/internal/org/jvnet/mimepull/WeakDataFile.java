package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

final class WeakDataFile extends WeakReference<DataFile> {
  private static final Logger LOGGER = Logger.getLogger(WeakDataFile.class.getName());
  
  private static ReferenceQueue<DataFile> refQueue = new ReferenceQueue();
  
  private static List<WeakDataFile> refList = new ArrayList();
  
  private final File file;
  
  private final RandomAccessFile raf;
  
  private static boolean hasCleanUpExecutor = false;
  
  WeakDataFile(DataFile paramDataFile, File paramFile) {
    super(paramDataFile, refQueue);
    refList.add(this);
    this.file = paramFile;
    try {
      this.raf = new RandomAccessFile(paramFile, "rw");
    } catch (IOException iOException) {
      throw new MIMEParsingException(iOException);
    } 
    if (!hasCleanUpExecutor)
      drainRefQueueBounded(); 
  }
  
  void read(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    try {
      this.raf.seek(paramLong);
      this.raf.readFully(paramArrayOfByte, paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new MIMEParsingException(iOException);
    } 
  }
  
  long writeTo(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    try {
      this.raf.seek(paramLong);
      this.raf.write(paramArrayOfByte, paramInt1, paramInt2);
      return this.raf.getFilePointer();
    } catch (IOException iOException) {
      throw new MIMEParsingException(iOException);
    } 
  }
  
  void close() {
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "Deleting file = {0}", this.file.getName()); 
    refList.remove(this);
    try {
      this.raf.close();
      boolean bool = this.file.delete();
      if (!bool && LOGGER.isLoggable(Level.INFO))
        LOGGER.log(Level.INFO, "File {0} was not deleted", this.file.getAbsolutePath()); 
    } catch (IOException iOException) {
      throw new MIMEParsingException(iOException);
    } 
  }
  
  void renameTo(File paramFile) {
    if (LOGGER.isLoggable(Level.FINE))
      LOGGER.log(Level.FINE, "Moving file={0} to={1}", new Object[] { this.file, paramFile }); 
    refList.remove(this);
    try {
      this.raf.close();
      boolean bool = this.file.renameTo(paramFile);
      if (!bool && LOGGER.isLoggable(Level.INFO))
        LOGGER.log(Level.INFO, "File {0} was not moved to {1}", new Object[] { this.file.getAbsolutePath(), paramFile.getAbsolutePath() }); 
    } catch (IOException iOException) {
      throw new MIMEParsingException(iOException);
    } 
  }
  
  static void drainRefQueueBounded() {
    WeakDataFile weakDataFile;
    while ((weakDataFile = (WeakDataFile)refQueue.poll()) != null) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Cleaning file = {0} from reference queue.", weakDataFile.file); 
      weakDataFile.close();
    } 
  }
  
  static  {
    CleanUpExecutorFactory cleanUpExecutorFactory = CleanUpExecutorFactory.newInstance();
    if (cleanUpExecutorFactory != null) {
      if (LOGGER.isLoggable(Level.FINE))
        LOGGER.log(Level.FINE, "Initializing clean up executor for MIMEPULL: {0}", cleanUpExecutorFactory.getClass().getName()); 
      Executor executor = cleanUpExecutorFactory.getExecutor();
      executor.execute(new Runnable() {
            public void run() {
              while (true) {
                try {
                  WeakDataFile weakDataFile;
                  if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.log(Level.FINE, "Cleaning file = {0} from reference queue.", weakDataFile.file); 
                  weakDataFile.close();
                } catch (InterruptedException interruptedException) {}
              } 
            }
          });
      hasCleanUpExecutor = true;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\WeakDataFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */