package sun.nio.ch;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockImpl extends FileLock {
  FileLockImpl(FileChannel paramFileChannel, long paramLong1, long paramLong2, boolean paramBoolean) { super(paramFileChannel, paramLong1, paramLong2, paramBoolean); }
  
  FileLockImpl(AsynchronousFileChannel paramAsynchronousFileChannel, long paramLong1, long paramLong2, boolean paramBoolean) { super(paramAsynchronousFileChannel, paramLong1, paramLong2, paramBoolean); }
  
  public boolean isValid() { return this.valid; }
  
  void invalidate() {
    assert Thread.holdsLock(this);
    this.valid = false;
  }
  
  public void release() {
    Channel channel = acquiredBy();
    if (!channel.isOpen())
      throw new ClosedChannelException(); 
    if (this.valid) {
      if (channel instanceof FileChannelImpl) {
        ((FileChannelImpl)channel).release(this);
      } else if (channel instanceof AsynchronousFileChannelImpl) {
        ((AsynchronousFileChannelImpl)channel).release(this);
      } else {
        throw new AssertionError();
      } 
      this.valid = false;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\FileLockImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */