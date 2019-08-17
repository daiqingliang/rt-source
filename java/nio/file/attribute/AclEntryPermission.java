package java.nio.file.attribute;

public static enum AclEntryPermission {
  READ_DATA, WRITE_DATA, APPEND_DATA, READ_NAMED_ATTRS, WRITE_NAMED_ATTRS, EXECUTE, DELETE_CHILD, READ_ATTRIBUTES, WRITE_ATTRIBUTES, DELETE, READ_ACL, WRITE_ACL, WRITE_OWNER, SYNCHRONIZE;
  
  public static final AclEntryPermission LIST_DIRECTORY;
  
  public static final AclEntryPermission ADD_FILE;
  
  public static final AclEntryPermission ADD_SUBDIRECTORY;
  
  static  {
    LIST_DIRECTORY = READ_DATA;
    ADD_FILE = WRITE_DATA;
    ADD_SUBDIRECTORY = APPEND_DATA;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\AclEntryPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */