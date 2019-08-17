package javax.swing.colorchooser;

import java.awt.Color;
import javax.swing.event.ChangeListener;

public interface ColorSelectionModel {
  Color getSelectedColor();
  
  void setSelectedColor(Color paramColor);
  
  void addChangeListener(ChangeListener paramChangeListener);
  
  void removeChangeListener(ChangeListener paramChangeListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\ColorSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */