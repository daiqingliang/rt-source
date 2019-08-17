package javax.swing.plaf.metal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class MetalComboBoxEditor extends BasicComboBoxEditor {
  protected static Insets editorBorderInsets = new Insets(2, 2, 2, 0);
  
  public MetalComboBoxEditor() { this.editor.setBorder(new EditorBorder()); }
  
  class EditorBorder extends AbstractBorder {
    public void paintBorder(Component param1Component, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      param1Graphics.translate(param1Int1, param1Int2);
      if (MetalLookAndFeel.usingOcean()) {
        param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        param1Graphics.drawRect(0, 0, param1Int3, param1Int4 - 1);
        param1Graphics.setColor(MetalLookAndFeel.getControlShadow());
        param1Graphics.drawRect(1, 1, param1Int3 - 2, param1Int4 - 3);
      } else {
        param1Graphics.setColor(MetalLookAndFeel.getControlDarkShadow());
        param1Graphics.drawLine(0, 0, param1Int3 - 1, 0);
        param1Graphics.drawLine(0, 0, 0, param1Int4 - 2);
        param1Graphics.drawLine(0, param1Int4 - 2, param1Int3 - 1, param1Int4 - 2);
        param1Graphics.setColor(MetalLookAndFeel.getControlHighlight());
        param1Graphics.drawLine(1, 1, param1Int3 - 1, 1);
        param1Graphics.drawLine(1, 1, 1, param1Int4 - 1);
        param1Graphics.drawLine(1, param1Int4 - 1, param1Int3 - 1, param1Int4 - 1);
        param1Graphics.setColor(MetalLookAndFeel.getControl());
        param1Graphics.drawLine(1, param1Int4 - 2, 1, param1Int4 - 2);
      } 
      param1Graphics.translate(-param1Int1, -param1Int2);
    }
    
    public Insets getBorderInsets(Component param1Component, Insets param1Insets) {
      param1Insets.set(2, 2, 2, 0);
      return param1Insets;
    }
  }
  
  public static class UIResource extends MetalComboBoxEditor implements UIResource {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\metal\MetalComboBoxEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */