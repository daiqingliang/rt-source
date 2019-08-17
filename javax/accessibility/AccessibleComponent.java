package javax.accessibility;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusListener;

public interface AccessibleComponent {
  Color getBackground();
  
  void setBackground(Color paramColor);
  
  Color getForeground();
  
  void setForeground(Color paramColor);
  
  Cursor getCursor();
  
  void setCursor(Cursor paramCursor);
  
  Font getFont();
  
  void setFont(Font paramFont);
  
  FontMetrics getFontMetrics(Font paramFont);
  
  boolean isEnabled();
  
  void setEnabled(boolean paramBoolean);
  
  boolean isVisible();
  
  void setVisible(boolean paramBoolean);
  
  boolean isShowing();
  
  boolean contains(Point paramPoint);
  
  Point getLocationOnScreen();
  
  Point getLocation();
  
  void setLocation(Point paramPoint);
  
  Rectangle getBounds();
  
  void setBounds(Rectangle paramRectangle);
  
  Dimension getSize();
  
  void setSize(Dimension paramDimension);
  
  Accessible getAccessibleAt(Point paramPoint);
  
  boolean isFocusTraversable();
  
  void requestFocus();
  
  void addFocusListener(FocusListener paramFocusListener);
  
  void removeFocusListener(FocusListener paramFocusListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */