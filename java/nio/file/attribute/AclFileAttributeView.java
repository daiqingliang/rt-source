package java.nio.file.attribute;

import java.io.IOException;
import java.util.List;

public interface AclFileAttributeView extends FileOwnerAttributeView {
  String name();
  
  List<AclEntry> getAcl() throws IOException;
  
  void setAcl(List<AclEntry> paramList) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\AclFileAttributeView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */