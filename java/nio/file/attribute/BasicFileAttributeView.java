package java.nio.file.attribute;

import java.io.IOException;

public interface BasicFileAttributeView extends FileAttributeView {
  String name();
  
  BasicFileAttributes readAttributes() throws IOException;
  
  void setTimes(FileTime paramFileTime1, FileTime paramFileTime2, FileTime paramFileTime3) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\BasicFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */