package java.awt.peer;

import java.awt.Window;
import java.util.List;

public interface DialogPeer extends WindowPeer {
  void setTitle(String paramString);
  
  void setResizable(boolean paramBoolean);
  
  void blockWindows(List<Window> paramList);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\DialogPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */