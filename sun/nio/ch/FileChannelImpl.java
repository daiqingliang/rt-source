package sun.nio.ch;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.OverlappingFileLockException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.WritableByteChannel;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import sun.misc.Cleaner;
import sun.misc.JavaNioAccess;
import sun.security.action.GetPropertyAction;

public class FileChannelImpl extends FileChannel {
  private static final long allocationGranularity;
  
  private final FileDispatcher nd;
  
  private final FileDescriptor fd;
  
  private final boolean writable;
  
  private final boolean readable;
  
  private final boolean append;
  
  private final Object parent;
  
  private final String path;
  
  private final NativeThreadSet threads = new NativeThreadSet(2);
  
  private final Object positionLock = new Object();
  
  private static final long MAPPED_TRANSFER_SIZE = 8388608L;
  
  private static final int TRANSFER_SIZE = 8192;
  
  private static final int MAP_RO = 0;
  
  private static final int MAP_RW = 1;
  
  private static final int MAP_PV = 2;
  
  private static boolean isSharedFileLockTable;
  
  private FileChannelImpl(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject) {
    this.fd = paramFileDescriptor;
    this.readable = paramBoolean1;
    this.writable = paramBoolean2;
    this.append = paramBoolean3;
    this.parent = paramObject;
    this.path = paramString;
    this.nd = new FileDispatcherImpl(paramBoolean3);
  }
  
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, Object paramObject) { return new FileChannelImpl(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, false, paramObject); }
  
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject) { return new FileChannelImpl(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, paramBoolean3, paramObject); }
  
  private void ensureOpen() throws IOException {
    if (!isOpen())
      throw new ClosedChannelException(); 
  }
  
  protected void implCloseChannel() throws IOException {
    if (this.fileLockTable != null)
      for (FileLock fileLock : this.fileLockTable.removeAll()) {
        synchronized (fileLock) {
          if (fileLock.isValid()) {
            this.nd.release(this.fd, fileLock.position(), fileLock.size());
            ((FileLockImpl)fileLock).invalidate();
          } 
        } 
      }  
    this.threads.signalAndWait();
    if (this.parent != null) {
      ((Closeable)this.parent).close();
    } else {
      this.nd.close(this.fd);
    } 
  }
  
  public int read(ByteBuffer paramByteBuffer) throws IOException {
    ensureOpen();
    if (!this.readable)
      throw new NonReadableChannelException(); 
    synchronized (this.positionLock) {
      i = 0;
      j = -1;
      try {
        begin();
        j = this.threads.add();
        if (!isOpen())
          return 0; 
        do {
          i = IOUtil.read(this.fd, paramByteBuffer, -1L, this.nd);
        } while (i == -3 && isOpen());
        return IOStatus.normalize(i);
      } finally {
        this.threads.remove(j);
        end((i > 0));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    ensureOpen();
    if (!this.readable)
      throw new NonReadableChannelException(); 
    synchronized (this.positionLock) {
      l = 0L;
      i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return 0L; 
        do {
          l = IOUtil.read(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, this.nd);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.threads.remove(i);
        end((l > 0L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public int write(ByteBuffer paramByteBuffer) throws IOException {
    ensureOpen();
    if (!this.writable)
      throw new NonWritableChannelException(); 
    synchronized (this.positionLock) {
      i = 0;
      j = -1;
      try {
        begin();
        j = this.threads.add();
        if (!isOpen())
          return 0; 
        do {
          i = IOUtil.write(this.fd, paramByteBuffer, -1L, this.nd);
        } while (i == -3 && isOpen());
        return IOStatus.normalize(i);
      } finally {
        this.threads.remove(j);
        end((i > 0));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      throw new IndexOutOfBoundsException(); 
    ensureOpen();
    if (!this.writable)
      throw new NonWritableChannelException(); 
    synchronized (this.positionLock) {
      l = 0L;
      i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return 0L; 
        do {
          l = IOUtil.write(this.fd, paramArrayOfByteBuffer, paramInt1, paramInt2, this.nd);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.threads.remove(i);
        end((l > 0L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public long position() throws IOException {
    ensureOpen();
    synchronized (this.positionLock) {
      l = -1L;
      i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return 0L; 
        do {
          l = this.append ? this.nd.size(this.fd) : position0(this.fd, -1L);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.threads.remove(i);
        end((l > -1L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public FileChannel position(long paramLong) throws IOException {
    ensureOpen();
    if (paramLong < 0L)
      throw new IllegalArgumentException(); 
    synchronized (this.positionLock) {
      l = -1L;
      i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return null; 
        do {
          l = position0(this.fd, paramLong);
        } while (l == -3L && isOpen());
        return this;
      } finally {
        this.threads.remove(i);
        end((l > -1L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public long size() throws IOException {
    ensureOpen();
    synchronized (this.positionLock) {
      l = -1L;
      i = -1;
      try {
        begin();
        i = this.threads.add();
        if (!isOpen())
          return -1L; 
        do {
          l = this.nd.size(this.fd);
        } while (l == -3L && isOpen());
        return IOStatus.normalize(l);
      } finally {
        this.threads.remove(i);
        end((l > -1L));
        assert IOStatus.check(l);
      } 
    } 
  }
  
  public FileChannel truncate(long paramLong) throws IOException {
    ensureOpen();
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative size"); 
    if (!this.writable)
      throw new NonWritableChannelException(); 
    synchronized (this.positionLock) {
      i = -1;
      long l1 = -1L;
      j = -1;
      long l2 = -1L;
      try {
        long l;
        begin();
        j = this.threads.add();
        if (!isOpen())
          return null; 
        do {
          l = this.nd.size(this.fd);
        } while (l == -3L && isOpen());
        if (!isOpen())
          return null; 
        do {
          l1 = position0(this.fd, -1L);
        } while (l1 == -3L && isOpen());
        if (!isOpen())
          return null; 
        assert l1 >= 0L;
        if (paramLong < l) {
          do {
            i = this.nd.truncate(this.fd, paramLong);
          } while (i == -3 && isOpen());
          if (!isOpen())
            return null; 
        } 
        if (l1 > paramLong)
          l1 = paramLong; 
        do {
          l2 = position0(this.fd, l1);
        } while (l2 == -3L && isOpen());
        return this;
      } finally {
        this.threads.remove(j);
        end((i > -1));
        assert IOStatus.check(i);
      } 
    } 
  }
  
  public void force(boolean paramBoolean) throws IOException {
    ensureOpen();
    i = -1;
    j = -1;
    try {
      begin();
      j = this.threads.add();
      if (!isOpen())
        return; 
      do {
        i = this.nd.force(this.fd, paramBoolean);
      } while (i == -3 && isOpen());
    } finally {
      this.threads.remove(j);
      end((i > -1));
      assert IOStatus.check(i);
    } 
  }
  
  private long transferToDirectlyInternal(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel, FileDescriptor paramFileDescriptor) throws IOException {
    assert !this.nd.transferToDirectlyNeedsPositionLock() || Thread.holdsLock(this.positionLock);
    l = -1L;
    i = -1;
    try {
      begin();
      i = this.threads.add();
      if (!isOpen())
        return -1L; 
      do {
        l = transferTo0(this.fd, paramLong, paramInt, paramFileDescriptor);
      } while (l == -3L && isOpen());
      if (l == -6L) {
        if (paramWritableByteChannel instanceof SinkChannelImpl)
          pipeSupported = false; 
        if (paramWritableByteChannel instanceof FileChannelImpl)
          fileSupported = false; 
        return -6L;
      } 
      if (l == -4L) {
        transferSupported = false;
        return -4L;
      } 
      return IOStatus.normalize(l);
    } finally {
      this.threads.remove(i);
      end((l > -1L));
    } 
  }
  
  private long transferToDirectly(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel) throws IOException {
    if (!transferSupported)
      return -4L; 
    FileDescriptor fileDescriptor = null;
    if (paramWritableByteChannel instanceof FileChannelImpl) {
      if (!fileSupported)
        return -6L; 
      fileDescriptor = ((FileChannelImpl)paramWritableByteChannel).fd;
    } else if (paramWritableByteChannel instanceof SelChImpl) {
      if (paramWritableByteChannel instanceof SinkChannelImpl && !pipeSupported)
        return -6L; 
      SelectableChannel selectableChannel = (SelectableChannel)paramWritableByteChannel;
      if (!this.nd.canTransferToDirectly(selectableChannel))
        return -6L; 
      fileDescriptor = ((SelChImpl)paramWritableByteChannel).getFD();
    } 
    if (fileDescriptor == null)
      return -4L; 
    int i = IOUtil.fdVal(this.fd);
    int j = IOUtil.fdVal(fileDescriptor);
    if (i == j)
      return -4L; 
    if (this.nd.transferToDirectlyNeedsPositionLock())
      synchronized (this.positionLock) {
        l = position();
        try {
          return transferToDirectlyInternal(paramLong, paramInt, paramWritableByteChannel, fileDescriptor);
        } finally {
          position(l);
        } 
      }  
    return transferToDirectlyInternal(paramLong, paramInt, paramWritableByteChannel, fileDescriptor);
  }
  
  private long transferToTrustedChannel(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel) throws IOException {
    boolean bool = paramWritableByteChannel instanceof SelChImpl;
    if (!(paramWritableByteChannel instanceof FileChannelImpl) && !bool)
      return -4L; 
    long l = paramLong2;
    while (l > 0L) {
      long l1 = Math.min(l, 8388608L);
      try {
        MappedByteBuffer mappedByteBuffer = map(FileChannel.MapMode.READ_ONLY, paramLong1, l1);
        try {
          int i = paramWritableByteChannel.write(mappedByteBuffer);
          assert i >= 0;
          l -= i;
          if (bool) {
            unmap(mappedByteBuffer);
            break;
          } 
          assert i > 0;
          paramLong1 += i;
          unmap(mappedByteBuffer);
        } finally {
          unmap(mappedByteBuffer);
        } 
      } catch (ClosedByInterruptException closedByInterruptException) {
        assert !paramWritableByteChannel.isOpen();
        try {
          close();
        } catch (Throwable throwable) {
          closedByInterruptException.addSuppressed(throwable);
        } 
        throw closedByInterruptException;
      } catch (IOException iOException) {
        if (l == paramLong2)
          throw iOException; 
      } 
      return paramLong2 - l;
    } 
    return paramLong2 - l;
  }
  
  private long transferToArbitraryChannel(long paramLong, int paramInt, WritableByteChannel paramWritableByteChannel) throws IOException {
    int i = Math.min(paramInt, 8192);
    byteBuffer = Util.getTemporaryDirectBuffer(i);
    long l1 = 0L;
    long l2 = paramLong;
    try {
      Util.erase(byteBuffer);
      while (l1 < paramInt) {
        byteBuffer.limit(Math.min((int)(paramInt - l1), 8192));
        int j = read(byteBuffer, l2);
        if (j <= 0)
          break; 
        byteBuffer.flip();
        int k = paramWritableByteChannel.write(byteBuffer);
        l1 += k;
        if (k != j)
          break; 
        l2 += k;
        byteBuffer.clear();
      } 
      return l1;
    } catch (IOException iOException) {
      if (l1 > 0L)
        return l1; 
      throw iOException;
    } finally {
      Util.releaseTemporaryDirectBuffer(byteBuffer);
    } 
  }
  
  public long transferTo(long paramLong1, long paramLong2, WritableByteChannel paramWritableByteChannel) throws IOException {
    ensureOpen();
    if (!paramWritableByteChannel.isOpen())
      throw new ClosedChannelException(); 
    if (!this.readable)
      throw new NonReadableChannelException(); 
    if (paramWritableByteChannel instanceof FileChannelImpl && !((FileChannelImpl)paramWritableByteChannel).writable)
      throw new NonWritableChannelException(); 
    if (paramLong1 < 0L || paramLong2 < 0L)
      throw new IllegalArgumentException(); 
    long l1 = size();
    if (paramLong1 > l1)
      return 0L; 
    int i = (int)Math.min(paramLong2, 2147483647L);
    if (l1 - paramLong1 < i)
      i = (int)(l1 - paramLong1); 
    long l2;
    return ((l2 = transferToDirectly(paramLong1, i, paramWritableByteChannel)) >= 0L) ? l2 : (((l2 = transferToTrustedChannel(paramLong1, i, paramWritableByteChannel)) >= 0L) ? l2 : transferToArbitraryChannel(paramLong1, i, paramWritableByteChannel));
  }
  
  private long transferFromFileChannel(FileChannelImpl paramFileChannelImpl, long paramLong1, long paramLong2) throws IOException {
    if (!paramFileChannelImpl.readable)
      throw new NonReadableChannelException(); 
    synchronized (paramFileChannelImpl.positionLock) {
      long l1 = paramFileChannelImpl.position();
      long l2 = Math.min(paramLong2, paramFileChannelImpl.size() - l1);
      long l3 = l2;
      long l4 = l1;
      while (l3 > 0L) {
        long l = Math.min(l3, 8388608L);
        mappedByteBuffer = paramFileChannelImpl.map(FileChannel.MapMode.READ_ONLY, l4, l);
        try {
          long l6 = write(mappedByteBuffer, paramLong1);
          assert l6 > 0L;
          l4 += l6;
          paramLong1 += l6;
          l3 -= l6;
        } catch (IOException iOException) {
          if (l3 == l2)
            throw iOException; 
          unmap(mappedByteBuffer);
        } finally {
          unmap(mappedByteBuffer);
        } 
      } 
      long l5 = l2 - l3;
      paramFileChannelImpl.position(l1 + l5);
      return l5;
    } 
  }
  
  private long transferFromArbitraryChannel(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2) throws IOException {
    int i = (int)Math.min(paramLong2, 8192L);
    byteBuffer = Util.getTemporaryDirectBuffer(i);
    long l1 = 0L;
    long l2 = paramLong1;
    try {
      Util.erase(byteBuffer);
      while (l1 < paramLong2) {
        byteBuffer.limit((int)Math.min(paramLong2 - l1, 8192L));
        int j = paramReadableByteChannel.read(byteBuffer);
        if (j <= 0)
          break; 
        byteBuffer.flip();
        int k = write(byteBuffer, l2);
        l1 += k;
        if (k != j)
          break; 
        l2 += k;
        byteBuffer.clear();
      } 
      return l1;
    } catch (IOException iOException) {
      if (l1 > 0L)
        return l1; 
      throw iOException;
    } finally {
      Util.releaseTemporaryDirectBuffer(byteBuffer);
    } 
  }
  
  public long transferFrom(ReadableByteChannel paramReadableByteChannel, long paramLong1, long paramLong2) throws IOException {
    ensureOpen();
    if (!paramReadableByteChannel.isOpen())
      throw new ClosedChannelException(); 
    if (!this.writable)
      throw new NonWritableChannelException(); 
    if (paramLong1 < 0L || paramLong2 < 0L)
      throw new IllegalArgumentException(); 
    return (paramLong1 > size()) ? 0L : ((paramReadableByteChannel instanceof FileChannelImpl) ? transferFromFileChannel((FileChannelImpl)paramReadableByteChannel, paramLong1, paramLong2) : transferFromArbitraryChannel(paramReadableByteChannel, paramLong1, paramLong2));
  }
  
  public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (!this.readable)
      throw new NonReadableChannelException(); 
    ensureOpen();
    if (this.nd.needsPositionLock())
      synchronized (this.positionLock) {
        return readInternal(paramByteBuffer, paramLong);
      }  
    return readInternal(paramByteBuffer, paramLong);
  }
  
  private int readInternal(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    assert !this.nd.needsPositionLock() || Thread.holdsLock(this.positionLock);
    i = 0;
    j = -1;
    try {
      begin();
      j = this.threads.add();
      if (!isOpen())
        return -1; 
      do {
        i = IOUtil.read(this.fd, paramByteBuffer, paramLong, this.nd);
      } while (i == -3 && isOpen());
      return IOStatus.normalize(i);
    } finally {
      this.threads.remove(j);
      end((i > 0));
      assert IOStatus.check(i);
    } 
  }
  
  public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    if (paramByteBuffer == null)
      throw new NullPointerException(); 
    if (paramLong < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (!this.writable)
      throw new NonWritableChannelException(); 
    ensureOpen();
    if (this.nd.needsPositionLock())
      synchronized (this.positionLock) {
        return writeInternal(paramByteBuffer, paramLong);
      }  
    return writeInternal(paramByteBuffer, paramLong);
  }
  
  private int writeInternal(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    assert !this.nd.needsPositionLock() || Thread.holdsLock(this.positionLock);
    i = 0;
    j = -1;
    try {
      begin();
      j = this.threads.add();
      if (!isOpen())
        return -1; 
      do {
        i = IOUtil.write(this.fd, paramByteBuffer, paramLong, this.nd);
      } while (i == -3 && isOpen());
      return IOStatus.normalize(i);
    } finally {
      this.threads.remove(j);
      end((i > 0));
      assert IOStatus.check(i);
    } 
  }
  
  private static void unmap(MappedByteBuffer paramMappedByteBuffer) {
    Cleaner cleaner = ((DirectBuffer)paramMappedByteBuffer).cleaner();
    if (cleaner != null)
      cleaner.clean(); 
  }
  
  public MappedByteBuffer map(FileChannel.MapMode paramMapMode, long paramLong1, long paramLong2) throws IOException {
    ensureOpen();
    if (paramMapMode == null)
      throw new NullPointerException("Mode is null"); 
    if (paramLong1 < 0L)
      throw new IllegalArgumentException("Negative position"); 
    if (paramLong2 < 0L)
      throw new IllegalArgumentException("Negative size"); 
    if (paramLong1 + paramLong2 < 0L)
      throw new IllegalArgumentException("Position + size overflow"); 
    if (paramLong2 > 2147483647L)
      throw new IllegalArgumentException("Size exceeds Integer.MAX_VALUE"); 
    byte b = -1;
    if (paramMapMode == FileChannel.MapMode.READ_ONLY) {
      b = 0;
    } else if (paramMapMode == FileChannel.MapMode.READ_WRITE) {
      b = 1;
    } else if (paramMapMode == FileChannel.MapMode.PRIVATE) {
      b = 2;
    } 
    assert b >= 0;
    if (paramMapMode != FileChannel.MapMode.READ_ONLY && !this.writable)
      throw new NonWritableChannelException(); 
    if (!this.readable)
      throw new NonReadableChannelException(); 
    l = -1L;
    i = -1;
    try {
      FileDescriptor fileDescriptor;
      int j;
      long l1;
      begin();
      i = this.threads.add();
      if (!isOpen())
        return null; 
      synchronized (this.positionLock) {
        long l2;
        do {
          l2 = this.nd.size(this.fd);
        } while (l2 == -3L && isOpen());
        if (!isOpen())
          return null; 
        if (l2 < paramLong1 + paramLong2) {
          int m;
          if (!this.writable)
            throw new IOException("Channel not open for writing - cannot extend file to required size"); 
          do {
            m = this.nd.allocate(this.fd, paramLong1 + paramLong2);
          } while (m == -3 && isOpen());
          if (!isOpen())
            return null; 
        } 
        if (paramLong2 == 0L) {
          l = 0L;
          FileDescriptor fileDescriptor1 = new FileDescriptor();
          if (!this.writable || b == 0)
            return Util.newMappedByteBufferR(0, 0L, fileDescriptor1, null); 
          return Util.newMappedByteBuffer(0, 0L, fileDescriptor1, null);
        } 
        j = (int)(paramLong1 % allocationGranularity);
        long l3 = paramLong1 - j;
        l1 = paramLong2 + j;
        try {
          l = map0(b, l3, l1);
        } catch (OutOfMemoryError outOfMemoryError) {
          System.gc();
          try {
            Thread.sleep(100L);
          } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
          } 
          try {
            l = map0(b, l3, l1);
          } catch (OutOfMemoryError outOfMemoryError1) {
            throw new IOException("Map failed", outOfMemoryError1);
          } 
        } 
      } 
      try {
        fileDescriptor = this.nd.duplicateForMapping(this.fd);
      } catch (IOException iOException) {
        unmap0(l, l1);
        throw iOException;
      } 
      assert IOStatus.checkAll(l);
      assert l % allocationGranularity == 0L;
      int k = (int)paramLong2;
      Unmapper unmapper = new Unmapper(l, l1, k, fileDescriptor, null);
      if (!this.writable || b == 0)
        return Util.newMappedByteBufferR(k, l + j, fileDescriptor, unmapper); 
      return Util.newMappedByteBuffer(k, l + j, fileDescriptor, unmapper);
    } finally {
      this.threads.remove(i);
      end(IOStatus.checkAll(l));
    } 
  }
  
  public static JavaNioAccess.BufferPool getMappedBufferPool() { return new JavaNioAccess.BufferPool() {
        public String getName() { return "mapped"; }
        
        public long getCount() throws IOException { return FileChannelImpl.Unmapper.count; }
        
        public long getTotalCapacity() throws IOException { return FileChannelImpl.Unmapper.totalCapacity; }
        
        public long getMemoryUsed() throws IOException { return FileChannelImpl.Unmapper.totalSize; }
      }; }
  
  private static boolean isSharedFileLockTable() {
    if (!propertyChecked)
      synchronized (FileChannelImpl.class) {
        if (!propertyChecked) {
          String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.ch.disableSystemWideOverlappingFileLockCheck"));
          isSharedFileLockTable = (str == null || str.equals("false"));
          propertyChecked = true;
        } 
      }  
    return isSharedFileLockTable;
  }
  
  private FileLockTable fileLockTable() throws IOException {
    if (this.fileLockTable == null)
      synchronized (this) {
        if (this.fileLockTable == null)
          if (isSharedFileLockTable()) {
            i = this.threads.add();
            try {
              ensureOpen();
              this.fileLockTable = FileLockTable.newSharedFileLockTable(this, this.fd);
            } finally {
              this.threads.remove(i);
            } 
          } else {
            this.fileLockTable = new SimpleFileLockTable();
          }  
      }  
    return this.fileLockTable;
  }
  
  public FileLock lock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
    ensureOpen();
    if (paramBoolean && !this.readable)
      throw new NonReadableChannelException(); 
    if (!paramBoolean && !this.writable)
      throw new NonWritableChannelException(); 
    fileLockImpl = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
    fileLockTable1 = fileLockTable();
    fileLockTable1.add(fileLockImpl);
    bool = false;
    i = -1;
    try {
      begin();
      i = this.threads.add();
      if (!isOpen())
        return null; 
      do {
        j = this.nd.lock(this.fd, true, paramLong1, paramLong2, paramBoolean);
      } while (j == 2 && isOpen());
      if (isOpen()) {
        if (j == 1) {
          assert paramBoolean;
          FileLockImpl fileLockImpl1 = new FileLockImpl(this, paramLong1, paramLong2, false);
          fileLockTable1.replace(fileLockImpl, fileLockImpl1);
          fileLockImpl = fileLockImpl1;
        } 
        bool = true;
      } 
    } finally {
      if (!bool)
        fileLockTable1.remove(fileLockImpl); 
      this.threads.remove(i);
      try {
        end(bool);
      } catch (ClosedByInterruptException closedByInterruptException) {
        throw new FileLockInterruptionException();
      } 
    } 
    return fileLockImpl;
  }
  
  public FileLock tryLock(long paramLong1, long paramLong2, boolean paramBoolean) throws IOException {
    ensureOpen();
    if (paramBoolean && !this.readable)
      throw new NonReadableChannelException(); 
    if (!paramBoolean && !this.writable)
      throw new NonWritableChannelException(); 
    FileLockImpl fileLockImpl = new FileLockImpl(this, paramLong1, paramLong2, paramBoolean);
    FileLockTable fileLockTable1 = fileLockTable();
    fileLockTable1.add(fileLockImpl);
    i = this.threads.add();
    try {
      int j;
      try {
        ensureOpen();
        j = this.nd.lock(this.fd, false, paramLong1, paramLong2, paramBoolean);
      } catch (IOException iOException) {
        fileLockTable1.remove(fileLockImpl);
        throw iOException;
      } 
      if (j == -1) {
        fileLockTable1.remove(fileLockImpl);
        return null;
      } 
      if (j == 1) {
        assert paramBoolean;
        FileLockImpl fileLockImpl1 = new FileLockImpl(this, paramLong1, paramLong2, false);
        fileLockTable1.replace(fileLockImpl, fileLockImpl1);
        return fileLockImpl1;
      } 
      return fileLockImpl;
    } finally {
      this.threads.remove(i);
    } 
  }
  
  void release(FileLockImpl paramFileLockImpl) throws IOException {
    i = this.threads.add();
    try {
      ensureOpen();
      this.nd.release(this.fd, paramFileLockImpl.position(), paramFileLockImpl.size());
    } finally {
      this.threads.remove(i);
    } 
    assert this.fileLockTable != null;
    this.fileLockTable.remove(paramFileLockImpl);
  }
  
  private native long map0(int paramInt, long paramLong1, long paramLong2) throws IOException;
  
  private static native int unmap0(long paramLong1, long paramLong2);
  
  private native long transferTo0(FileDescriptor paramFileDescriptor1, long paramLong1, long paramLong2, FileDescriptor paramFileDescriptor2);
  
  private native long position0(FileDescriptor paramFileDescriptor, long paramLong);
  
  private static native long initIDs() throws IOException;
  
  static  {
    IOUtil.load();
    allocationGranularity = initIDs();
  }
  
  private static class SimpleFileLockTable extends FileLockTable {
    private final List<FileLock> lockList = new ArrayList(2);
    
    private void checkList(long param1Long1, long param1Long2) throws OverlappingFileLockException {
      assert Thread.holdsLock(this.lockList);
      for (FileLock fileLock : this.lockList) {
        if (fileLock.overlaps(param1Long1, param1Long2))
          throw new OverlappingFileLockException(); 
      } 
    }
    
    public void add(FileLock param1FileLock) throws OverlappingFileLockException {
      synchronized (this.lockList) {
        checkList(param1FileLock.position(), param1FileLock.size());
        this.lockList.add(param1FileLock);
      } 
    }
    
    public void remove(FileLock param1FileLock) throws OverlappingFileLockException {
      synchronized (this.lockList) {
        this.lockList.remove(param1FileLock);
      } 
    }
    
    public List<FileLock> removeAll() {
      synchronized (this.lockList) {
        ArrayList arrayList = new ArrayList(this.lockList);
        this.lockList.clear();
        return arrayList;
      } 
    }
    
    public void replace(FileLock param1FileLock1, FileLock param1FileLock2) {
      synchronized (this.lockList) {
        this.lockList.remove(param1FileLock1);
        this.lockList.add(param1FileLock2);
      } 
    }
  }
  
  private static class Unmapper implements Runnable {
    private static final NativeDispatcher nd = new FileDispatcherImpl();
    
    private final long size;
    
    private final int cap;
    
    private final FileDescriptor fd;
    
    private Unmapper(long param1Long1, long param1Long2, int param1Int, FileDescriptor param1FileDescriptor) {
      assert param1Long1 != 0L;
      this.address = param1Long1;
      this.size = param1Long2;
      this.cap = param1Int;
      this.fd = param1FileDescriptor;
      synchronized (Unmapper.class) {
        count++;
        totalSize += param1Long2;
        totalCapacity += param1Int;
      } 
    }
    
    public void run() throws IOException {
      if (this.address == 0L)
        return; 
      FileChannelImpl.unmap0(this.address, this.size);
      this.address = 0L;
      if (this.fd.valid())
        try {
          nd.close(this.fd);
        } catch (IOException iOException) {} 
      synchronized (Unmapper.class) {
        count--;
        totalSize -= this.size;
        totalCapacity -= this.cap;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\ch\FileChannelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */