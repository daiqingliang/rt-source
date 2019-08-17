package com.sun.java.swing.plaf.windows;

import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

public class WindowsTextFieldUI extends BasicTextFieldUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new WindowsTextFieldUI(); }
  
  protected void paintBackground(Graphics paramGraphics) { super.paintBackground(paramGraphics); }
  
  protected Caret createCaret() { return new WindowsFieldCaret(); }
  
  static class WindowsFieldCaret extends DefaultCaret implements UIResource {
    protected void adjustVisibility(Rectangle param1Rectangle) { SwingUtilities.invokeLater(new SafeScroller(param1Rectangle)); }
    
    protected Highlighter.HighlightPainter getSelectionPainter() { return WindowsTextUI.WindowsPainter; }
    
    private class SafeScroller implements Runnable {
      private Rectangle r;
      
      SafeScroller(Rectangle param2Rectangle) { this.r = param2Rectangle; }
      
      public void run() {
        JTextField jTextField = (JTextField)WindowsTextFieldUI.WindowsFieldCaret.this.getComponent();
        if (jTextField != null) {
          TextUI textUI = jTextField.getUI();
          int i = WindowsTextFieldUI.WindowsFieldCaret.this.getDot();
          Position.Bias bias = Position.Bias.Forward;
          Rectangle rectangle = null;
          try {
            rectangle = textUI.modelToView(jTextField, i, bias);
          } catch (BadLocationException badLocationException) {}
          Insets insets = jTextField.getInsets();
          BoundedRangeModel boundedRangeModel = jTextField.getHorizontalVisibility();
          int j = this.r.x + boundedRangeModel.getValue() - insets.left;
          int k = boundedRangeModel.getExtent() / 4;
          if (this.r.x < insets.left) {
            boundedRangeModel.setValue(j - k);
          } else if (this.r.x + this.r.width > insets.left + boundedRangeModel.getExtent()) {
            boundedRangeModel.setValue(j - 3 * k);
          } 
          if (rectangle != null)
            try {
              Rectangle rectangle1 = textUI.modelToView(jTextField, i, bias);
              if (rectangle1 != null && !rectangle1.equals(rectangle))
                WindowsTextFieldUI.WindowsFieldCaret.this.damage(rectangle1); 
            } catch (BadLocationException badLocationException) {} 
        } 
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTextFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */