package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

class WindowsAclFileAttributeView extends AbstractAclFileAttributeView {
  private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
  
  private final WindowsPath file;
  
  private final boolean followLinks;
  
  WindowsAclFileAttributeView(WindowsPath paramWindowsPath, boolean paramBoolean) {
    this.file = paramWindowsPath;
    this.followLinks = paramBoolean;
  }
  
  private void checkAccess(WindowsPath paramWindowsPath, boolean paramBoolean1, boolean paramBoolean2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      if (paramBoolean1)
        securityManager.checkRead(paramWindowsPath.getPathForPermissionCheck()); 
      if (paramBoolean2)
        securityManager.checkWrite(paramWindowsPath.getPathForPermissionCheck()); 
      securityManager.checkPermission(new RuntimePermission("accessUserInformation"));
    } 
  }
  
  static NativeBuffer getFileSecurity(String paramString, int paramInt) throws IOException {
    int i = 0;
    try {
      i = WindowsNativeDispatcher.GetFileSecurity(paramString, paramInt, 0L, 0);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(paramString);
    } 
    assert i > 0;
    NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(i);
    try {
      while (true) {
        int j = WindowsNativeDispatcher.GetFileSecurity(paramString, paramInt, nativeBuffer.address(), i);
        if (j <= i)
          return nativeBuffer; 
        nativeBuffer.release();
        nativeBuffer = NativeBuffers.getNativeBuffer(j);
        i = j;
      } 
    } catch (WindowsException windowsException) {
      nativeBuffer.release();
      windowsException.rethrowAsIOException(paramString);
      return null;
    } 
  }
  
  public UserPrincipal getOwner() throws IOException {
    checkAccess(this.file, true, false);
    String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
    nativeBuffer = getFileSecurity(str, 1);
    try {
      long l = WindowsNativeDispatcher.GetSecurityDescriptorOwner(nativeBuffer.address());
      if (l == 0L)
        throw new IOException("no owner"); 
      return WindowsUserPrincipals.fromSid(l);
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(this.file);
      return null;
    } finally {
      nativeBuffer.release();
    } 
  }
  
  public List<AclEntry> getAcl() throws IOException {
    checkAccess(this.file, true, false);
    String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
    nativeBuffer = getFileSecurity(str, 4);
    try {
      return WindowsSecurityDescriptor.getAcl(nativeBuffer.address());
    } finally {
      nativeBuffer.release();
    } 
  }
  
  public void setOwner(UserPrincipal paramUserPrincipal) throws IOException {
    if (paramUserPrincipal == null)
      throw new NullPointerException("'owner' is null"); 
    if (!(paramUserPrincipal instanceof WindowsUserPrincipals.User))
      throw new ProviderMismatchException(); 
    WindowsUserPrincipals.User user = (WindowsUserPrincipals.User)paramUserPrincipal;
    checkAccess(this.file, false, true);
    String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
    l = 0L;
    try {
      l = WindowsNativeDispatcher.ConvertStringSidToSid(user.sidString());
    } catch (WindowsException windowsException) {
      throw new IOException("Failed to get SID for " + user.getName() + ": " + windowsException.errorString());
    } 
    try {
      nativeBuffer = NativeBuffers.getNativeBuffer(20);
      try {
        WindowsNativeDispatcher.InitializeSecurityDescriptor(nativeBuffer.address());
        WindowsNativeDispatcher.SetSecurityDescriptorOwner(nativeBuffer.address(), l);
        privilege = WindowsSecurity.enablePrivilege("SeRestorePrivilege");
        try {
          WindowsNativeDispatcher.SetFileSecurity(str, 1, nativeBuffer.address());
        } finally {
          privilege.drop();
        } 
      } catch (WindowsException windowsException) {
        windowsException.rethrowAsIOException(this.file);
      } finally {
        nativeBuffer.release();
      } 
    } finally {
      WindowsNativeDispatcher.LocalFree(l);
    } 
  }
  
  public void setAcl(List<AclEntry> paramList) throws IOException {
    checkAccess(this.file, false, true);
    String str = WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
    windowsSecurityDescriptor = WindowsSecurityDescriptor.create(paramList);
    try {
      WindowsNativeDispatcher.SetFileSecurity(str, 4, windowsSecurityDescriptor.address());
    } catch (WindowsException windowsException) {
      windowsException.rethrowAsIOException(this.file);
    } finally {
      windowsSecurityDescriptor.release();
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsAclFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */