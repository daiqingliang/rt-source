package javax.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

class ColorTracker implements ActionListener, Serializable {
  JColorChooser chooser;
  
  Color color;
  
  public ColorTracker(JColorChooser paramJColorChooser) { this.chooser = paramJColorChooser; }
  
  public void actionPerformed(ActionEvent paramActionEvent) { this.color = this.chooser.getColor(); }
  
  public Color getColor() { return this.color; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\ColorTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */