package java.nio.file.attribute;

public interface DosFileAttributes extends BasicFileAttributes {
  boolean isReadOnly();
  
  boolean isHidden();
  
  boolean isArchive();
  
  boolean isSystem();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\DosFileAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */