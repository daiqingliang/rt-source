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

@InstrumentationTarget("java.io.FileOutputStream")
public final class FileOutputStreamRMHooks {
  private final FileDescriptor fd = null;
  
  private final String path = null;
  
  private final Object closeLock = new Object();
  
  @InstrumentationMethod
  private void open(String paramString, boolean paramBoolean) throws FileNotFoundException {
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
      open(paramString, paramBoolean);
      bool = true;
    } finally {
      resourceRequest2.request(-(l2 - bool), resourceIdImpl);
      resourceRequest1.request(-(l1 - bool), resourceIdImpl);
    } 
  }
  
  @InstrumentationMethod
  public final FileDescriptor getFD() throws IOException { return getFD(); }
  
  @InstrumentationMethod
  public void write(int paramInt) throws IOException {
    resourceIdImpl = ResourceIdImpl.of(this.path);
    FileDescriptor fileDescriptor = getFD();
    if (fileDescriptor == FileDescriptor.err) {
      resourceRequest = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
    } else if (fileDescriptor == FileDescriptor.out) {
      resourceRequest = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
    } else {
      resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    } 
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
    FileDescriptor fileDescriptor = getFD();
    if (fileDescriptor == FileDescriptor.err) {
      resourceRequest = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
    } else if (fileDescriptor == FileDescriptor.out) {
      resourceRequest = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
    } else {
      resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    } 
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
    FileDescriptor fileDescriptor = getFD();
    if (fileDescriptor == FileDescriptor.err) {
      resourceRequest = ApproverGroup.STDERR_WRITE_GROUP.getApprover(this);
    } else if (fileDescriptor == FileDescriptor.out) {
      resourceRequest = ApproverGroup.STDOUT_WRITE_GROUP.getApprover(this);
    } else {
      resourceRequest = ApproverGroup.FILE_WRITE_GROUP.getApprover(this);
    } 
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\management\resource\internal\inst\FileOutputStreamRMHooks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */