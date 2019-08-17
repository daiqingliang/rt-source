package javax.swing;

public interface DesktopManager {
  void openFrame(JInternalFrame paramJInternalFrame);
  
  void closeFrame(JInternalFrame paramJInternalFrame);
  
  void maximizeFrame(JInternalFrame paramJInternalFrame);
  
  void minimizeFrame(JInternalFrame paramJInternalFrame);
  
  void iconifyFrame(JInternalFrame paramJInternalFrame);
  
  void deiconifyFrame(JInternalFrame paramJInternalFrame);
  
  void activateFrame(JInternalFrame paramJInternalFrame);
  
  void deactivateFrame(JInternalFrame paramJInternalFrame);
  
  void beginDraggingFrame(JComponent paramJComponent);
  
  void dragFrame(JComponent paramJComponent, int paramInt1, int paramInt2);
  
  void endDraggingFrame(JComponent paramJComponent);
  
  void beginResizingFrame(JComponent paramJComponent, int paramInt);
  
  void resizeFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  void endResizingFrame(JComponent paramJComponent);
  
  void setBoundsForFrame(JComponent paramJComponent, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DesktopManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */