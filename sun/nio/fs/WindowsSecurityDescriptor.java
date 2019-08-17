package sun.nio.fs;

import java.io.IOException;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import sun.misc.Unsafe;

class WindowsSecurityDescriptor {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final short SIZEOF_ACL = 8;
  
  private static final short SIZEOF_ACCESS_ALLOWED_ACE = 12;
  
  private static final short SIZEOF_ACCESS_DENIED_ACE = 12;
  
  private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
  
  private static final short OFFSETOF_TYPE = 0;
  
  private static final short OFFSETOF_FLAGS = 1;
  
  private static final short OFFSETOF_ACCESS_MASK = 4;
  
  private static final short OFFSETOF_SID = 8;
  
  private static final WindowsSecurityDescriptor NULL_DESCRIPTOR = new WindowsSecurityDescriptor();
  
  private final List<Long> sidList;
  
  private final NativeBuffer aclBuffer;
  
  private final NativeBuffer sdBuffer;
  
  private WindowsSecurityDescriptor() {
    this.sidList = null;
    this.aclBuffer = null;
    this.sdBuffer = null;
  }
  
  private WindowsSecurityDescriptor(List<AclEntry> paramList) throws IOException {
    bool = false;
    paramList = new ArrayList<AclEntry>(paramList);
    this.sidList = new ArrayList(paramList.size());
    try {
      int i = 8;
      for (AclEntry aclEntry : paramList) {
        UserPrincipal userPrincipal = aclEntry.principal();
        if (!(userPrincipal instanceof WindowsUserPrincipals.User))
          throw new ProviderMismatchException(); 
        String str = ((WindowsUserPrincipals.User)userPrincipal).sidString();
        try {
          long l = WindowsNativeDispatcher.ConvertStringSidToSid(str);
          this.sidList.add(Long.valueOf(l));
          i += WindowsNativeDispatcher.GetLengthSid(l) + Math.max(12, 12);
        } catch (WindowsException windowsException) {
          throw new IOException("Failed to get SID for " + userPrincipal.getName() + ": " + windowsException.errorString());
        } 
      } 
      this.aclBuffer = NativeBuffers.getNativeBuffer(i);
      this.sdBuffer = NativeBuffers.getNativeBuffer(20);
      WindowsNativeDispatcher.InitializeAcl(this.aclBuffer.address(), i);
      for (byte b = 0; b < paramList.size(); b++) {
        AclEntry aclEntry = (AclEntry)paramList.get(b);
        long l = ((Long)this.sidList.get(b)).longValue();
        try {
          encode(aclEntry, l, this.aclBuffer.address());
        } catch (WindowsException windowsException) {
          throw new IOException("Failed to encode ACE: " + windowsException.errorString());
        } 
      } 
      WindowsNativeDispatcher.InitializeSecurityDescriptor(this.sdBuffer.address());
      WindowsNativeDispatcher.SetSecurityDescriptorDacl(this.sdBuffer.address(), this.aclBuffer.address());
      bool = true;
    } catch (WindowsException windowsException) {
      throw new IOException(windowsException.getMessage());
    } finally {
      if (!bool)
        release(); 
    } 
  }
  
  void release() {
    if (this.sdBuffer != null)
      this.sdBuffer.release(); 
    if (this.aclBuffer != null)
      this.aclBuffer.release(); 
    if (this.sidList != null)
      for (Long long : this.sidList)
        WindowsNativeDispatcher.LocalFree(long.longValue());  
  }
  
  long address() { return (this.sdBuffer == null) ? 0L : this.sdBuffer.address(); }
  
  private static AclEntry decode(long paramLong) throws IOException {
    AclEntryType aclEntryType;
    byte b1 = unsafe.getByte(paramLong + 0L);
    if (b1 != 0 && b1 != 1)
      return null; 
    if (b1 == 0) {
      aclEntryType = AclEntryType.ALLOW;
    } else {
      aclEntryType = AclEntryType.DENY;
    } 
    byte b2 = unsafe.getByte(paramLong + 1L);
    EnumSet enumSet1 = EnumSet.noneOf(AclEntryFlag.class);
    if ((b2 & true) != 0)
      enumSet1.add(AclEntryFlag.FILE_INHERIT); 
    if ((b2 & 0x2) != 0)
      enumSet1.add(AclEntryFlag.DIRECTORY_INHERIT); 
    if ((b2 & 0x4) != 0)
      enumSet1.add(AclEntryFlag.NO_PROPAGATE_INHERIT); 
    if ((b2 & 0x8) != 0)
      enumSet1.add(AclEntryFlag.INHERIT_ONLY); 
    int i = unsafe.getInt(paramLong + 4L);
    EnumSet enumSet2 = EnumSet.noneOf(AclEntryPermission.class);
    if ((i & true) > 0)
      enumSet2.add(AclEntryPermission.READ_DATA); 
    if ((i & 0x2) > 0)
      enumSet2.add(AclEntryPermission.WRITE_DATA); 
    if ((i & 0x4) > 0)
      enumSet2.add(AclEntryPermission.APPEND_DATA); 
    if ((i & 0x8) > 0)
      enumSet2.add(AclEntryPermission.READ_NAMED_ATTRS); 
    if ((i & 0x10) > 0)
      enumSet2.add(AclEntryPermission.WRITE_NAMED_ATTRS); 
    if ((i & 0x20) > 0)
      enumSet2.add(AclEntryPermission.EXECUTE); 
    if ((i & 0x40) > 0)
      enumSet2.add(AclEntryPermission.DELETE_CHILD); 
    if ((i & 0x80) > 0)
      enumSet2.add(AclEntryPermission.READ_ATTRIBUTES); 
    if ((i & 0x100) > 0)
      enumSet2.add(AclEntryPermission.WRITE_ATTRIBUTES); 
    if ((i & 0x10000) > 0)
      enumSet2.add(AclEntryPermission.DELETE); 
    if ((i & 0x20000) > 0)
      enumSet2.add(AclEntryPermission.READ_ACL); 
    if ((i & 0x40000) > 0)
      enumSet2.add(AclEntryPermission.WRITE_ACL); 
    if ((i & 0x80000) > 0)
      enumSet2.add(AclEntryPermission.WRITE_OWNER); 
    if ((i & 0x100000) > 0)
      enumSet2.add(AclEntryPermission.SYNCHRONIZE); 
    long l = paramLong + 8L;
    UserPrincipal userPrincipal = WindowsUserPrincipals.fromSid(l);
    return AclEntry.newBuilder().setType(aclEntryType).setPrincipal(userPrincipal).setFlags(enumSet1).setPermissions(enumSet2).build();
  }
  
  private static void encode(AclEntry paramAclEntry, long paramLong1, long paramLong2) throws WindowsException {
    if (paramAclEntry.type() != AclEntryType.ALLOW && paramAclEntry.type() != AclEntryType.DENY)
      return; 
    boolean bool = (paramAclEntry.type() == AclEntryType.ALLOW) ? 1 : 0;
    Set set1 = paramAclEntry.permissions();
    int i = 0;
    if (set1.contains(AclEntryPermission.READ_DATA))
      i |= true; 
    if (set1.contains(AclEntryPermission.WRITE_DATA))
      i |= 0x2; 
    if (set1.contains(AclEntryPermission.APPEND_DATA))
      i |= 0x4; 
    if (set1.contains(AclEntryPermission.READ_NAMED_ATTRS))
      i |= 0x8; 
    if (set1.contains(AclEntryPermission.WRITE_NAMED_ATTRS))
      i |= 0x10; 
    if (set1.contains(AclEntryPermission.EXECUTE))
      i |= 0x20; 
    if (set1.contains(AclEntryPermission.DELETE_CHILD))
      i |= 0x40; 
    if (set1.contains(AclEntryPermission.READ_ATTRIBUTES))
      i |= 0x80; 
    if (set1.contains(AclEntryPermission.WRITE_ATTRIBUTES))
      i |= 0x100; 
    if (set1.contains(AclEntryPermission.DELETE))
      i |= 0x10000; 
    if (set1.contains(AclEntryPermission.READ_ACL))
      i |= 0x20000; 
    if (set1.contains(AclEntryPermission.WRITE_ACL))
      i |= 0x40000; 
    if (set1.contains(AclEntryPermission.WRITE_OWNER))
      i |= 0x80000; 
    if (set1.contains(AclEntryPermission.SYNCHRONIZE))
      i |= 0x100000; 
    Set set2 = paramAclEntry.flags();
    byte b = 0;
    if (set2.contains(AclEntryFlag.FILE_INHERIT))
      b = (byte)(b | true); 
    if (set2.contains(AclEntryFlag.DIRECTORY_INHERIT))
      b = (byte)(b | 0x2); 
    if (set2.contains(AclEntryFlag.NO_PROPAGATE_INHERIT))
      b = (byte)(b | 0x4); 
    if (set2.contains(AclEntryFlag.INHERIT_ONLY))
      b = (byte)(b | 0x8); 
    if (bool) {
      WindowsNativeDispatcher.AddAccessAllowedAceEx(paramLong2, b, i, paramLong1);
    } else {
      WindowsNativeDispatcher.AddAccessDeniedAceEx(paramLong2, b, i, paramLong1);
    } 
  }
  
  static WindowsSecurityDescriptor create(List<AclEntry> paramList) throws IOException { return new WindowsSecurityDescriptor(paramList); }
  
  static WindowsSecurityDescriptor fromAttribute(FileAttribute<?>... paramVarArgs) throws IOException {
    WindowsSecurityDescriptor windowsSecurityDescriptor = NULL_DESCRIPTOR;
    for (FileAttribute<?> fileAttribute : paramVarArgs) {
      if (windowsSecurityDescriptor != NULL_DESCRIPTOR)
        windowsSecurityDescriptor.release(); 
      if (fileAttribute == null)
        throw new NullPointerException(); 
      if (fileAttribute.name().equals("acl:acl")) {
        List list = (List)fileAttribute.value();
        windowsSecurityDescriptor = new WindowsSecurityDescriptor(list);
      } else {
        throw new UnsupportedOperationException("'" + fileAttribute.name() + "' not supported as initial attribute");
      } 
    } 
    return windowsSecurityDescriptor;
  }
  
  static List<AclEntry> getAcl(long paramLong) throws IOException {
    long l = WindowsNativeDispatcher.GetSecurityDescriptorDacl(paramLong);
    int i = 0;
    if (l == 0L) {
      i = 0;
    } else {
      WindowsNativeDispatcher.AclInformation aclInformation = WindowsNativeDispatcher.GetAclInformation(l);
      i = aclInformation.aceCount();
    } 
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++) {
      long l1 = WindowsNativeDispatcher.GetAce(l, b);
      AclEntry aclEntry = decode(l1);
      if (aclEntry != null)
        arrayList.add(aclEntry); 
    } 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsSecurityDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */