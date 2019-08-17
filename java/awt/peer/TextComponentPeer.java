package java.awt.peer;

import java.awt.im.InputMethodRequests;

public interface TextComponentPeer extends ComponentPeer {
  void setEditable(boolean paramBoolean);
  
  String getText();
  
  void setText(String paramString);
  
  int getSelectionStart();
  
  int getSelectionEnd();
  
  void select(int paramInt1, int paramInt2);
  
  void setCaretPosition(int paramInt);
  
  int getCaretPosition();
  
  InputMethodRequests getInputMethodRequests();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\peer\TextComponentPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */