package java.nio.file.attribute;

public interface BasicFileAttributes {
  FileTime lastModifiedTime();
  
  FileTime lastAccessTime();
  
  FileTime creationTime();
  
  boolean isRegularFile();
  
  boolean isDirectory();
  
  boolean isSymbolicLink();
  
  boolean isOther();
  
  long size();
  
  Object fileKey();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\BasicFileAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */