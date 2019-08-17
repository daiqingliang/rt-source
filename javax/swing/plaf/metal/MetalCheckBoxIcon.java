package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.plaf.UIResource;

public class MetalCheckBoxIcon implements Icon, UIResource, Serializable {
  protected int getControlSize() { return 13; }
  
  public void paintIcon(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
    JCheckBox jCheckBox = (JCheckBox)paramComponent;
    ButtonModel buttonModel = jCheckBox.getModel();
    int i = getControlSize();
    boolean bool = buttonModel.isSelected();
    if (buttonModel.isEnabled()) {
      if (jCheckBox.isBorderPaintedFlat()) {
        paramGraphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        paramGraphics.drawRect(paramInt1 + 1, paramInt2, i - 1, i - 1);
      } 
      if (buttonModel.isPressed() && buttonModel.isArmed()) {
        if (jCheckBox.isBorderPaintedFlat()) {
          paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
          paramGraphics.fillRect(paramInt1 + 2, paramInt2 + 1, i - 2, i - 2);
        } else {
          paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
          paramGraphics.fillRect(paramInt1, paramInt2, i - 1, i - 1);
          MetalUtils.drawPressed3DBorder(paramGraphics, paramInt1, paramInt2, i, i);
        } 
      } else if (!jCheckBox.isBorderPaintedFlat()) {
        MetalUtils.drawFlush3DBorder(paramGraphics, paramInt1, paramInt2, i, i);
      } 
      paramGraphics.setColor(MetalLookAndFeel.getControlInfo());
    } else {
      paramGraphics.setColor(MetalLookAndFeel.getControlShadow());
      paramGraphics.drawRect(paramInt1, paramInt2, i - 1, i - 1);
    } 
    if (bool) {
      if (jCheckBox.isBorderPaintedFlat())
        paramInt1++; 
      drawCheck(paramComponent, paramGraphics, paramInt1, paramInt2);
    } 
  }
  
  protected void drawCheck(Component paramComponent, Graphics paramGraphics, int paramInt1, int paramInt2) {
    int i = getControlSize();
    paramGraphics.fillRect(paramInt1 + 3, paramInt2 + 5, 2, i - 8);
    paramGraphics.drawLine(paramInt1 + i - 4, paramInt2 + 3, paramInt1 + 5, paramInt2 + i - 6);
    paramGraphics.drawLine(paramInt1 + i - 4, paramInt2 + 4, paramInt1 + 5, paramInt2 + i - 5);
  }
  
  public int getIconWidth() { return getControlSize(); }
  
  public int getIconHeight() { return getControlSize(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalCheckBoxIcon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */