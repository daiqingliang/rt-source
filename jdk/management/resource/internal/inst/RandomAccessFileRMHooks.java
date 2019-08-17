package jdk.management.resource.internal.inst;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import jdk.internal.instrumentation.InstrumentationMethod;
import jdk.internal.instrumentation.InstrumentationTarget;
import jdk.management.resource.ResourceRequest;
import jdk.management.resource.ResourceRequestDeniedException;
import jdk.management.resource.internal.ApproverGroup;
import jdk.management.resource.internal.ResourceIdImpl;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;

@InstrumentationTarget("java.io.RandomAccessFile")
public final class RandomAccessFileRMHooks {
  private FileDescriptor fd;
  
  private final String path = null;
  
  private final Object closeLock = new Object();
  
  @InstrumentationMethod
  private void open(String paramString, int paramInt) throws FileNotFoundException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest1 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
    l1 = 0L;
    l2 = 0L;
    try {
      l2 = resourceRequest1.request(1L, resourceIdImpl);
      if (l2 < 1L)
        throw new FileNotFoundException(paramString + ": resource limited: too many open files"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      FileNotFoundException fileNotFoundException = new FileNotFoundException(paramString + ": resource limited: too many open files");
      fileNotFoundException.initCause(resourceRequestDeniedException);
      throw fileNotFoundException;
    } 
    resourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
    bool = false;
    try {
      try {
        l1 = resourceRequest2.request(1L, resourceIdImpl);
        if (l1 < 1L)
          throw new FileNotFoundException(paramString + ": resource limited: too many open file descriptors"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        FileNotFoundException fileNotFoundException = new FileNotFoundException(paramString + ": resource limited: too many open file descriptors");
        fileNotFoundException.initCause(resourceRequestDeniedException);
        throw fileNotFoundException;
      } 
      open(paramString, paramInt);
      bool = true;
    } finally {
      resourceRequest2.request(-(l1 - bool), resourceIdImpl);
      resourceRequest1.request(-(l2 - bool), resourceIdImpl);
    } 
  }
  
  @InstrumentationMethod
  public int read() throws IOException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    l = 0L;
    try {
      l = resourceRequest.request(1L, resourceIdImpl);
      if (l < 1L)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    i = -1;
    try {
      i = read();
    } finally {
      resourceRequest.request(-(l - ((i == -1) ? 0 : 1)), resourceIdImpl);
    } 
    return i;
  }
  
  @InstrumentationMethod
  public int read(byte[] paramArrayOfByte) throws IOException {
    int k;
    if (paramArrayOfByte == null)
      return read(paramArrayOfByte); 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    int i = paramArrayOfByte.length;
    l = 0L;
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    try {
      if (l < i) {
        resourceRequest.request(-l, resourceIdImpl);
        k = read(paramArrayOfByte, 0, paramArrayOfByte.length);
        j = Math.max(k, 0);
      } else {
        k = read(paramArrayOfByte);
        j = Math.max(k, 0);
      } 
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
    return k;
  }
  
  @InstrumentationMethod
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0)
      return read(paramArrayOfByte, paramInt1, paramInt2); 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    l = 0L;
    try {
      l = Math.max(resourceRequest.request(paramInt2, resourceIdImpl), 0L);
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    paramInt2 = Math.min(paramInt2, (int)l);
    i = 0;
    int j = 0;
    try {
      j = read(paramArrayOfByte, paramInt1, paramInt2);
      i = Math.max(j, 0);
    } finally {
      resourceRequest.request(-(l - i), resourceIdImpl);
    } 
    return j;
  }
  
  @InstrumentationMethod
  public void write(int paramInt) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    l = 0L;
    try {
      l = resourceRequest.request(1L, resourceIdImpl);
      if (l < 1L)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    bool = false;
    try {
      write(paramInt);
      bool = true;
    } finally {
      resourceRequest.request(-(l - bool), resourceIdImpl);
    } 
  }
  
  @InstrumentationMethod
  public void write(byte[] paramArrayOfByte) throws IOException {
    if (paramArrayOfByte == null) {
      write(paramArrayOfByte);
      return;
    } 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    int i = paramArrayOfByte.length;
    l = 0L;
    try {
      l = resourceRequest.request(i, resourceIdImpl);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    try {
      write(paramArrayOfByte);
      j = i;
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
  }
  
  @InstrumentationMethod
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (paramInt2 < 0) {
      write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    } 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    l = 0L;
    try {
      l = resourceRequest.request(paramInt2, resourceIdImpl);
      if (l < paramInt2)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    i = 0;
    try {
      write(paramArrayOfByte, paramInt1, paramInt2);
      i = paramInt2;
    } finally {
      resourceRequest.request(-(l - i), resourceIdImpl);
    } 
  }
  
  @InstrumentationMethod
  public final void writeBytes(String paramString) throws IOException {
    if (paramString == null) {
      writeBytes(paramString);
      return;
    } 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    int i = paramString.length();
    l = 0L;
    try {
      l = resourceRequest.request(i, resourceIdImpl);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    try {
      writeBytes(paramString);
      j = i;
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
  }
  
  @InstrumentationMethod
  public final void writeChars(String paramString) throws IOException {
    if (paramString == null) {
      writeChars(paramString);
      return;
    } 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    int i = 2 * paramString.length();
    l = 0L;
    try {
      l = resourceRequest.request(i, resourceIdImpl);
      if (l < i)
        throw new IOException("Resource limited: insufficient bytes approved"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    try {
      writeChars(paramString);
      j = i;
    } finally {
      resourceRequest.request(-(l - j), resourceIdImpl);
    } 
  }
  
  @InstrumentationMethod
  public void close() {
    synchronized (this.closeLock) {
      if (this.closed)
        return; 
    } 
    javaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
    try {
      l = javaIOFileDescriptorAccess.getHandle(this.fd);
      if (l == -1L)
        l = javaIOFileDescriptorAccess.get(this.fd); 
    } catch (UnsupportedOperationException unsupportedOperationException) {
      l = javaIOFileDescriptorAccess.get(this.fd);
    } 
    try {
      close();
    } finally {
      long l1;
      try {
        l1 = javaIOFileDescriptorAccess.getHandle(this.fd);
        if (l1 == -1L)
          l1 = javaIOFileDescriptorAccess.get(this.fd); 
      } catch (UnsupportedOperationException unsupportedOperationException) {
        l1 = javaIOFileDescriptorAccess.get(this.fd);
      } 
      ResourceIdImpl resourceIdImpl = ResourceIdImpl.of(this.path);
      if (l1 != l) {
        ResourceRequest resourceRequest1 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(this.fd);
        resourceRequest1.request(-1L, resourceIdImpl);
      } 
      ResourceRequest resourceRequest = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
      resourceRequest.request(-1L, resourceIdImpl);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\RandomAccessFileRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */