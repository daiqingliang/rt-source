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

@InstrumentationTarget("java.io.FileInputStream")
public final class FileInputStreamRMHooks {
  private final FileDescriptor fd = null;
  
  private String path = null;
  
  private final Object closeLock = new Object();
  
  @InstrumentationMethod
  public final FileDescriptor getFD() throws IOException { return getFD(); }
  
  @InstrumentationMethod
  private void open(String paramString) throws FileNotFoundException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    resourceRequest1 = ApproverGroup.FILE_OPEN_GROUP.getApprover(this);
    l1 = 0L;
    try {
      l1 = resourceRequest1.request(1L, resourceIdImpl);
      if (l1 < 1L)
        throw new FileNotFoundException(paramString + ": resource limited: too many open files"); 
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      FileNotFoundException fileNotFoundException = new FileNotFoundException(paramString + ": resource limited: too many open files");
      fileNotFoundException.initCause(resourceRequestDeniedException);
      throw fileNotFoundException;
    } 
    resourceRequest2 = null;
    l2 = 0L;
    bool = false;
    try {
      FileDescriptor fileDescriptor = null;
      try {
        fileDescriptor = getFD();
      } catch (IOException iOException) {}
      resourceRequest2 = ApproverGroup.FILEDESCRIPTOR_OPEN_GROUP.getApprover(fileDescriptor);
      try {
        l2 = resourceRequest2.request(1L, resourceIdImpl);
        if (l2 < 1L)
          throw new FileNotFoundException(paramString + ": resource limited: too many open file descriptors"); 
      } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
        FileNotFoundException fileNotFoundException = new FileNotFoundException(paramString + ": resource limited: too many open file descriptors");
        fileNotFoundException.initCause(resourceRequestDeniedException);
        throw fileNotFoundException;
      } 
      open(paramString);
      bool = true;
    } finally {
      resourceRequest2.request(-(l2 - bool), resourceIdImpl);
      resourceRequest1.request(-(l1 - bool), resourceIdImpl);
    } 
  }
  
  @InstrumentationMethod
  public int read() throws IOException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    if (getFD() == FileDescriptor.in) {
      resourceRequest = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
    } else {
      resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    } 
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
    if (paramArrayOfByte == null)
      return read(paramArrayOfByte); 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    if (getFD() == FileDescriptor.in) {
      resourceRequest = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
    } else {
      resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    } 
    int i = paramArrayOfByte.length;
    l = 0L;
    try {
      l = Math.max(resourceRequest.request(i, resourceIdImpl), 0L);
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    j = 0;
    int k = 0;
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
    int j;
    if (paramInt2 < 0)
      return read(paramArrayOfByte, paramInt1, paramInt2); 
    resourceIdImpl = ResourceIdImpl.of(this.path);
    if (getFD() == FileDescriptor.in) {
      resourceRequest = ApproverGroup.STDIN_READ_GROUP.getApprover(this);
    } else {
      resourceRequest = ApproverGroup.FILE_READ_GROUP.getApprover(this);
    } 
    l = 0L;
    try {
      l = Math.max(resourceRequest.request(paramInt2, resourceIdImpl), 0L);
    } catch (ResourceRequestDeniedException resourceRequestDeniedException) {
      throw new IOException("Resource limited", resourceRequestDeniedException);
    } 
    paramInt2 = Math.min(paramInt2, (int)l);
    i = 0;
    try {
      j = read(paramArrayOfByte, paramInt1, paramInt2);
      i = Math.max(j, 0);
    } finally {
      resourceRequest.request(-(l - i), resourceIdImpl);
    } 
    return j;
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\FileInputStreamRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */