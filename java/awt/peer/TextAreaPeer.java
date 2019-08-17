package java.awt.peer;

import java.awt.Dimension;

public interface TextAreaPeer extends TextComponentPeer {
  void insert(String paramString, int paramInt);
  
  void replaceRange(String paramString, int paramInt1, int paramInt2);
  
  Dimension getPreferredSize(int paramInt1, int paramInt2);
  
  Dimension getMinimumSize(int paramInt1, int paramInt2);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\TextAreaPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */