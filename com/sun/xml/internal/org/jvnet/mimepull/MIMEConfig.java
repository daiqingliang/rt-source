package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MIMEConfig {
  private static final int DEFAULT_CHUNK_SIZE = 8192;
  
  private static final long DEFAULT_MEMORY_THRESHOLD = 1048576L;
  
  private static final String DEFAULT_FILE_PREFIX = "MIME";
  
  private static final Logger LOGGER = Logger.getLogger(MIMEConfig.class.getName());
  
  boolean parseEagerly;
  
  int chunkSize;
  
  long memoryThreshold;
  
  File tempDir;
  
  String prefix;
  
  String suffix;
  
  private MIMEConfig(boolean paramBoolean, int paramInt, long paramLong, String paramString1, String paramString2, String paramString3) {
    this.parseEagerly = paramBoolean;
    this.chunkSize = paramInt;
    this.memoryThreshold = paramLong;
    this.prefix = paramString2;
    this.suffix = paramString3;
    setDir(paramString1);
  }
  
  public MIMEConfig() { this(false, 8192, 1048576L, null, "MIME", null); }
  
  boolean isParseEagerly() { return this.parseEagerly; }
  
  public void setParseEagerly(boolean paramBoolean) { this.parseEagerly = paramBoolean; }
  
  int getChunkSize() { return this.chunkSize; }
  
  void setChunkSize(int paramInt) { this.chunkSize = paramInt; }
  
  long getMemoryThreshold() { return this.memoryThreshold; }
  
  public void setMemoryThreshold(long paramLong) { this.memoryThreshold = paramLong; }
  
  boolean isOnlyMemory() { return (this.memoryThreshold == -1L); }
  
  File getTempDir() { return this.tempDir; }
  
  String getTempFilePrefix() { return this.prefix; }
  
  String getTempFileSuffix() { return this.suffix; }
  
  public final void setDir(String paramString) {
    if (this.tempDir == null && paramString != null && !paramString.equals(""))
      this.tempDir = new File(paramString); 
  }
  
  public void validate() {
    if (!isOnlyMemory())
      try {
        File file = (this.tempDir == null) ? File.createTempFile(this.prefix, this.suffix) : File.createTempFile(this.prefix, this.suffix, this.tempDir);
        boolean bool = file.delete();
        if (!bool && LOGGER.isLoggable(Level.INFO))
          LOGGER.log(Level.INFO, "File {0} was not deleted", file.getAbsolutePath()); 
      } catch (Exception exception) {
        this.memoryThreshold = -1L;
      }  
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */