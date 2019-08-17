package java.awt.peer;

import java.awt.Dimension;

public interface ListPeer extends ComponentPeer {
  int[] getSelectedIndexes();
  
  void add(String paramString, int paramInt);
  
  void delItems(int paramInt1, int paramInt2);
  
  void removeAll();
  
  void select(int paramInt);
  
  void deselect(int paramInt);
  
  void makeVisible(int paramInt);
  
  void setMultipleMode(boolean paramBoolean);
  
  Dimension getPreferredSize(int paramInt);
  
  Dimension getMinimumSize(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\ListPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */