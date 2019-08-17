package sun.nio.fs;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalNotFoundException;

class WindowsUserPrincipals {
  static UserPrincipal fromSid(long paramLong) throws IOException {
    String str2;
    String str1;
    try {
      str1 = WindowsNativeDispatcher.ConvertSidToStringSid(paramLong);
      if (str1 == null)
        throw new AssertionError(); 
    } catch (WindowsException windowsException) {
      throw new IOException("Unable to convert SID to String: " + windowsException.errorString());
    } 
    WindowsNativeDispatcher.Account account = null;
    try {
      account = WindowsNativeDispatcher.LookupAccountSid(paramLong);
      str2 = account.domain() + "\\" + account.name();
    } catch (WindowsException windowsException) {
      str2 = str1;
    } 
    byte b = (account == null) ? 8 : account.use();
    return (b == 2 || b == 5 || b == 4) ? new Group(str1, b, str2) : new User(str1, b, str2);
  }
  
  static UserPrincipal lookup(String paramString) throws IOException {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new RuntimePermission("lookupUserInformation")); 
    int i = 0;
    try {
      i = WindowsNativeDispatcher.LookupAccountName(paramString, 0L, 0);
    } catch (WindowsException windowsException) {
      if (windowsException.lastError() == 1332)
        throw new UserPrincipalNotFoundException(paramString); 
      throw new IOException(paramString + ": " + windowsException.errorString());
    } 
    assert i > 0;
    nativeBuffer = NativeBuffers.getNativeBuffer(i);
    try {
      int j = WindowsNativeDispatcher.LookupAccountName(paramString, nativeBuffer.address(), i);
      if (j != i)
        throw new AssertionError("SID change during lookup"); 
      return fromSid(nativeBuffer.address());
    } catch (WindowsException windowsException) {
      throw new IOException(paramString + ": " + windowsException.errorString());
    } finally {
      nativeBuffer.release();
    } 
  }
  
  static class Group extends User implements GroupPrincipal {
    Group(String param1String1, int param1Int, String param1String2) { super(param1String1, param1Int, param1String2); }
  }
  
  static class User implements UserPrincipal {
    private final String sidString;
    
    private final int sidType;
    
    private final String accountName;
    
    User(String param1String1, int param1Int, String param1String2) {
      this.sidString = param1String1;
      this.sidType = param1Int;
      this.accountName = param1String2;
    }
    
    String sidString() { return this.sidString; }
    
    public String getName() { return this.accountName; }
    
    public String toString() {
      switch (this.sidType) {
        case 1:
          str = "User";
          return this.accountName + " (" + str + ")";
        case 2:
          str = "Group";
          return this.accountName + " (" + str + ")";
        case 3:
          str = "Domain";
          return this.accountName + " (" + str + ")";
        case 4:
          str = "Alias";
          return this.accountName + " (" + str + ")";
        case 5:
          str = "Well-known group";
          return this.accountName + " (" + str + ")";
        case 6:
          str = "Deleted";
          return this.accountName + " (" + str + ")";
        case 7:
          str = "Invalid";
          return this.accountName + " (" + str + ")";
        case 9:
          str = "Computer";
          return this.accountName + " (" + str + ")";
      } 
      String str = "Unknown";
      return this.accountName + " (" + str + ")";
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof User))
        return false; 
      User user = (User)param1Object;
      return this.sidString.equals(user.sidString);
    }
    
    public int hashCode() { return this.sidString.hashCode(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsUserPrincipals.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */