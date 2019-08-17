package java.nio.file.attribute;

import java.io.IOException;

public interface FileOwnerAttributeView extends FileAttributeView {
  String name();
  
  UserPrincipal getOwner() throws IOException;
  
  void setOwner(UserPrincipal paramUserPrincipal) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\FileOwnerAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */