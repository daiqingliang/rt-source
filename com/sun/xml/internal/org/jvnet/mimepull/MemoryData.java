package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

final class MemoryData implements Data {
  private static final Logger LOGGER = Logger.getLogger(MemoryData.class.getName());
  
  private final byte[] data;
  
  private final int len;
  
  private final MIMEConfig config;
  
  MemoryData(ByteBuffer paramByteBuffer, MIMEConfig paramMIMEConfig) {
    this.data = paramByteBuffer.array();
    this.len = paramByteBuffer.limit();
    this.config = paramMIMEConfig;
  }
  
  public int size() { return this.len; }
  
  public byte[] read() { return this.data; }
  
  public long writeTo(DataFile paramDataFile) { return paramDataFile.writeTo(this.data, 0, this.len); }
  
  public Data createNext(DataHead paramDataHead, ByteBuffer paramByteBuffer) {
    if (!this.config.isOnlyMemory() && paramDataHead.inMemory >= this.config.memoryThreshold) {
      try {
        String str1 = this.config.getTempFilePrefix();
        String str2 = this.config.getTempFileSuffix();
        File file = TempFiles.createTempFile(str1, str2, this.config.getTempDir());
        file.deleteOnExit();
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "Created temp file = {0}", file); 
        file.deleteOnExit();
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.log(Level.FINE, "Created temp file = {0}", file); 
        paramDataHead.dataFile = new DataFile(file);
      } catch (IOException iOException) {
        throw new MIMEParsingException(iOException);
      } 
      if (paramDataHead.head != null)
        for (Chunk chunk = paramDataHead.head; chunk != null; chunk = chunk.next) {
          long l = chunk.data.writeTo(paramDataHead.dataFile);
          chunk.data = new FileData(paramDataHead.dataFile, l, this.len);
        }  
      return new FileData(paramDataHead.dataFile, paramByteBuffer);
    } 
    return new MemoryData(paramByteBuffer, this.config);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MemoryData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */