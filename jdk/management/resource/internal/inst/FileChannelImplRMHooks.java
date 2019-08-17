package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;

@InstrumentationTarget("sun.nio.ch.FileChannelImpl")
public final class FileChannelImplRMHooks {
  private final FileDescriptor fd = null;
  
  private String path = null;
  
  @InstrumentationMethod
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, Object paramObject) {
    l = 0L;
    bool = false;
    fileChannel = null;
    resourceIdImpl = null;
    resourceRequest = null;
    try {
      fileChannel = open(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, paramObject);
      resourceIdImpl = ResourceIdImpl.of(paramString);
      resourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(fileChannel);
      bool1 = false;
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new ResourceRequestDeniedException(paramString + ": resource limited: too many open files"); 
        bool1 = true;
      } finally {
        if (!bool1)
          try {
            fileChannel.close();
          } catch (IOException iOException) {} 
      } 
      bool = true;
    } finally {
      if (resourceRequest != null)
        resourceRequest.request(-(l - bool), resourceIdImpl); 
    } 
    return fileChannel;
  }
  
  @InstrumentationMethod
  public static FileChannel open(FileDescriptor paramFileDescriptor, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, Object paramObject) {
    fileChannel = open(paramFileDescriptor, paramString, paramBoolean1, paramBoolean2, paramBoolean3, paramObject);
    resourceIdImpl = ResourceIdImpl.of(paramString);
    resourceRequest = null;
    long l = 0L;
    bool = false;
    if (paramObject == null) {
      resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(paramFileDescriptor);
      try {
        l = resourceRequest.request(1L, resourceIdImpl);
        if (l < 1L)
          throw new ResourceRequestDeniedException(paramString + ": resource limited: too many open file descriptors"); 
        bool = true;
      } finally {
        if (!bool) {
          resourceRequest.request(-1L, resourceIdImpl);
          try {
            fileChannel.close();
          } catch (IOException iOException) {}
        } 
      } 
    } 
    bool = false;
    resourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(fileChannel);
    try {
      l = resourceRequest.request(1L, resourceIdImpl);
      if (l < 1L) {
        try {
          fileChannel.close();
        } catch (IOException iOException) {}
        throw new ResourceRequestDeniedException(paramString + ": resource limited: too many open files");
      } 
      bool = true;
    } finally {
      if (!bool) {
        resourceRequest.request(-1L, resourceIdImpl);
        try {
          fileChannel.close();
        } catch (IOException iOException) {}
      } 
    } 
    return fileChannel;
  }
  
  @InstrumentationMethod
  public int read(ByteBuffer paramByteBuffer) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    int k = 0;
    try {
      k = read(paramByteBuffer);
      j = Math.max(k, 0);
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
    return k;
  }
  
  @InstrumentationMethod
  public long read(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      return read(paramArrayOfByteBuffer, paramInt1, paramInt2); 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    l1 = 0L;
    int i = 0;
    int j = paramInt1 + paramInt2;
    for (k = paramInt1; k < j; k++)
      i += paramArrayOfByteBuffer[k].remaining(); 
    try {
      l1 = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l1 < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException k) {
      ResourceRequestDeniedException resourceRequestDeniedException;
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    l2 = 0L;
    long l3 = 0L;
    try {
      l3 = read(paramArrayOfByteBuffer, paramInt1, paramInt2);
      l2 = Math.max(l3, 0L);
    } finally {
      resourceRequest.request(-(l1 - l2), resourceIdImpl);
    } 
    return l3;
  }
  
  @InstrumentationMethod
  public int read(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    int k = 0;
    try {
      k = read(paramByteBuffer, paramLong);
      j = Math.max(k, 0);
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
    return k;
  }
  
  @InstrumentationMethod
  public int write(ByteBuffer paramByteBuffer) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    try {
      j = write(paramByteBuffer);
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
    return j;
  }
  
  @InstrumentationMethod
  public long write(ByteBuffer[] paramArrayOfByteBuffer, int paramInt1, int paramInt2) throws IOException {
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt1 > paramArrayOfByteBuffer.length - paramInt2)
      return write(paramArrayOfByteBuffer, paramInt1, paramInt2); 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    l1 = 0L;
    int i = 0;
    int j = paramInt1 + paramInt2;
    for (k = paramInt1; k < j; k++)
      i += paramArrayOfByteBuffer[k].remaining(); 
    try {
      l1 = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l1 < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException k) {
      ResourceRequestDeniedException resourceRequestDeniedException;
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    l2 = 0L;
    try {
      l2 = Math.max(write(paramArrayOfByteBuffer, paramInt1, paramInt2), 0L);
    } finally {
      resourceRequest.request(-(l1 - l2), resourceIdImpl);
    } 
    return l2;
  }
  
  @InstrumentationMethod
  public int write(ByteBuffer paramByteBuffer, long paramLong) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    l = 0L;
    int i = paramByteBuffer.remaining();
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    try {
      j = write(paramByteBuffer, paramLong);
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
    return j;
  }
  
  @InstrumentationMethod
  protected void implCloseChannel() {
    try {
      implCloseChannel();
    } finally {
      ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(this.path);
      ResourceRequest resourceRequest = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
      resourceRequest.request(-1L, resourceIdImpl);
      resourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      resourceRequest.request(-1L, resourceIdImpl);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\FileChannelImplRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */