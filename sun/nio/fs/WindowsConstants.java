package sun.nio.fs;

class WindowsConstants {
  public static final long INVALID_HANDLE_VALUE = -1L;
  
  public static final int GENERIC_READ = -2147483648;
  
  public static final int GENERIC_WRITE = 1073741824;
  
  public static final int FILE_SHARE_READ = 1;
  
  public static final int FILE_SHARE_WRITE = 2;
  
  public static final int FILE_SHARE_DELETE = 4;
  
  public static final int CREATE_NEW = 1;
  
  public static final int CREATE_ALWAYS = 2;
  
  public static final int OPEN_EXISTING = 3;
  
  public static final int OPEN_ALWAYS = 4;
  
  public static final int TRUNCATE_EXISTING = 5;
  
  public static final int FILE_ATTRIBUTE_READONLY = 1;
  
  public static final int FILE_ATTRIBUTE_HIDDEN = 2;
  
  public static final int FILE_ATTRIBUTE_SYSTEM = 4;
  
  public static final int FILE_ATTRIBUTE_DIRECTORY = 16;
  
  public static final int FILE_ATTRIBUTE_ARCHIVE = 32;
  
  public static final int FILE_ATTRIBUTE_DEVICE = 64;
  
  public static final int FILE_ATTRIBUTE_NORMAL = 128;
  
  public static final int FILE_ATTRIBUTE_REPARSE_POINT = 1024;
  
  public static final int FILE_FLAG_NO_BUFFERING = 536870912;
  
  public static final int FILE_FLAG_OVERLAPPED = 1073741824;
  
  public static final int FILE_FLAG_WRITE_THROUGH = -2147483648;
  
  public static final int FILE_FLAG_BACKUP_SEMANTICS = 33554432;
  
  public static final int FILE_FLAG_DELETE_ON_CLOSE = 67108864;
  
  public static final int FILE_FLAG_OPEN_REPARSE_POINT = 2097152;
  
  public static final int BACKUP_ALTERNATE_DATA = 4;
  
  public static final int BACKUP_SPARSE_BLOCK = 9;
  
  public static final int IO_REPARSE_TAG_SYMLINK = -1610612724;
  
  public static final int MAXIMUM_REPARSE_DATA_BUFFER_SIZE = 16384;
  
  public static final int SYMBOLIC_LINK_FLAG_DIRECTORY = 1;
  
  public static final int FILE_CASE_SENSITIVE_SEARCH = 1;
  
  public static final int FILE_CASE_PRESERVED_NAMES = 2;
  
  public static final int FILE_PERSISTENT_ACLS = 8;
  
  public static final int FILE_VOLUME_IS_COMPRESSED = 32768;
  
  public static final int FILE_NAMED_STREAMS = 262144;
  
  public static final int FILE_READ_ONLY_VOLUME = 524288;
  
  public static final int ERROR_FILE_NOT_FOUND = 2;
  
  public static final int ERROR_PATH_NOT_FOUND = 3;
  
  public static final int ERROR_ACCESS_DENIED = 5;
  
  public static final int ERROR_INVALID_HANDLE = 6;
  
  public static final int ERROR_INVALID_DATA = 13;
  
  public static final int ERROR_NOT_SAME_DEVICE = 17;
  
  public static final int ERROR_NOT_READY = 21;
  
  public static final int ERROR_SHARING_VIOLATION = 32;
  
  public static final int ERROR_FILE_EXISTS = 80;
  
  public static final int ERROR_INVALID_PARAMATER = 87;
  
  public static final int ERROR_DISK_FULL = 112;
  
  public static final int ERROR_INSUFFICIENT_BUFFER = 122;
  
  public static final int ERROR_INVALID_LEVEL = 124;
  
  public static final int ERROR_DIR_NOT_ROOT = 144;
  
  public static final int ERROR_DIR_NOT_EMPTY = 145;
  
  public static final int ERROR_ALREADY_EXISTS = 183;
  
  public static final int ERROR_MORE_DATA = 234;
  
  public static final int ERROR_DIRECTORY = 267;
  
  public static final int ERROR_NOTIFY_ENUM_DIR = 1022;
  
  public static final int ERROR_NONE_MAPPED = 1332;
  
  public static final int ERROR_NOT_A_REPARSE_POINT = 4390;
  
  public static final int ERROR_INVALID_REPARSE_DATA = 4392;
  
  public static final int FILE_NOTIFY_CHANGE_FILE_NAME = 1;
  
  public static final int FILE_NOTIFY_CHANGE_DIR_NAME = 2;
  
  public static final int FILE_NOTIFY_CHANGE_ATTRIBUTES = 4;
  
  public static final int FILE_NOTIFY_CHANGE_SIZE = 8;
  
  public static final int FILE_NOTIFY_CHANGE_LAST_WRITE = 16;
  
  public static final int FILE_NOTIFY_CHANGE_LAST_ACCESS = 32;
  
  public static final int FILE_NOTIFY_CHANGE_CREATION = 64;
  
  public static final int FILE_NOTIFY_CHANGE_SECURITY = 256;
  
  public static final int FILE_ACTION_ADDED = 1;
  
  public static final int FILE_ACTION_REMOVED = 2;
  
  public static final int FILE_ACTION_MODIFIED = 3;
  
  public static final int FILE_ACTION_RENAMED_OLD_NAME = 4;
  
  public static final int FILE_ACTION_RENAMED_NEW_NAME = 5;
  
  public static final int COPY_FILE_FAIL_IF_EXISTS = 1;
  
  public static final int COPY_FILE_COPY_SYMLINK = 2048;
  
  public static final int MOVEFILE_REPLACE_EXISTING = 1;
  
  public static final int MOVEFILE_COPY_ALLOWED = 2;
  
  public static final int DRIVE_UNKNOWN = 0;
  
  public static final int DRIVE_NO_ROOT_DIR = 1;
  
  public static final int DRIVE_REMOVABLE = 2;
  
  public static final int DRIVE_FIXED = 3;
  
  public static final int DRIVE_REMOTE = 4;
  
  public static final int DRIVE_CDROM = 5;
  
  public static final int DRIVE_RAMDISK = 6;
  
  public static final int OWNER_SECURITY_INFORMATION = 1;
  
  public static final int GROUP_SECURITY_INFORMATION = 2;
  
  public static final int DACL_SECURITY_INFORMATION = 4;
  
  public static final int SACL_SECURITY_INFORMATION = 8;
  
  public static final int SidTypeUser = 1;
  
  public static final int SidTypeGroup = 2;
  
  public static final int SidTypeDomain = 3;
  
  public static final int SidTypeAlias = 4;
  
  public static final int SidTypeWellKnownGroup = 5;
  
  public static final int SidTypeDeletedAccount = 6;
  
  public static final int SidTypeInvalid = 7;
  
  public static final int SidTypeUnknown = 8;
  
  public static final int SidTypeComputer = 9;
  
  public static final byte ACCESS_ALLOWED_ACE_TYPE = 0;
  
  public static final byte ACCESS_DENIED_ACE_TYPE = 1;
  
  public static final byte OBJECT_INHERIT_ACE = 1;
  
  public static final byte CONTAINER_INHERIT_ACE = 2;
  
  public static final byte NO_PROPAGATE_INHERIT_ACE = 4;
  
  public static final byte INHERIT_ONLY_ACE = 8;
  
  public static final int DELETE = 65536;
  
  public static final int READ_CONTROL = 131072;
  
  public static final int WRITE_DAC = 262144;
  
  public static final int WRITE_OWNER = 524288;
  
  public static final int SYNCHRONIZE = 1048576;
  
  public static final int FILE_LIST_DIRECTORY = 1;
  
  public static final int FILE_READ_DATA = 1;
  
  public static final int FILE_WRITE_DATA = 2;
  
  public static final int FILE_APPEND_DATA = 4;
  
  public static final int FILE_READ_EA = 8;
  
  public static final int FILE_WRITE_EA = 16;
  
  public static final int FILE_EXECUTE = 32;
  
  public static final int FILE_DELETE_CHILD = 64;
  
  public static final int FILE_READ_ATTRIBUTES = 128;
  
  public static final int FILE_WRITE_ATTRIBUTES = 256;
  
  public static final int FILE_GENERIC_READ = 1179785;
  
  public static final int FILE_GENERIC_WRITE = 1179926;
  
  public static final int FILE_GENERIC_EXECUTE = 1179808;
  
  public static final int FILE_ALL_ACCESS = 2032127;
  
  public static final int TOKEN_DUPLICATE = 2;
  
  public static final int TOKEN_IMPERSONATE = 4;
  
  public static final int TOKEN_QUERY = 8;
  
  public static final int TOKEN_ADJUST_PRIVILEGES = 32;
  
  public static final int SE_PRIVILEGE_ENABLED = 2;
  
  public static final int TokenUser = 1;
  
  public static final int PROCESS_QUERY_INFORMATION = 1024;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\WindowsConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */