package javax.swing.text;

import java.awt.Graphics;
import java.awt.Point;
import javax.swing.event.ChangeListener;

public interface Caret {
  void install(JTextComponent paramJTextComponent);
  
  void deinstall(JTextComponent paramJTextComponent);
  
  void paint(Graphics paramGraphics);
  
  void addChangeListener(ChangeListener paramChangeListener);
  
  void removeChangeListener(ChangeListener paramChangeListener);
  
  boolean isVisible();
  
  void setVisible(boolean paramBoolean);
  
  boolean isSelectionVisible();
  
  void setSelectionVisible(boolean paramBoolean);
  
  void setMagicCaretPosition(Point paramPoint);
  
  Point getMagicCaretPosition();
  
  void setBlinkRate(int paramInt);
  
  int getBlinkRate();
  
  int getDot();
  
  int getMark();
  
  void setDot(int paramInt);
  
  void moveDot(int paramInt);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\Caret.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */