package com.sun.java.swing.plaf.motif;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import javax.swing.KeyStroke;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class MotifTextUI {
  static final JTextComponent.KeyBinding[] defaultBindings = { new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(155, 2), "copy-to-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(155, 1), "paste-from-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(127, 1), "cut-to-clipboard"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(37, 1), "selection-backward"), new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(39, 1), "selection-forward") };
  
  public static Caret createCaret() { return new MotifCaret(); }
  
  public static class MotifCaret extends DefaultCaret implements UIResource {
    static final int IBeamOverhang = 2;
    
    public void focusGained(FocusEvent param1FocusEvent) {
      super.focusGained(param1FocusEvent);
      getComponent().repaint();
    }
    
    public void focusLost(FocusEvent param1FocusEvent) {
      super.focusLost(param1FocusEvent);
      getComponent().repaint();
    }
    
    protected void damage(Rectangle param1Rectangle) {
      if (param1Rectangle != null) {
        this.x = param1Rectangle.x - 2 - 1;
        this.y = param1Rectangle.y;
        this.width = param1Rectangle.width + 4 + 3;
        this.height = param1Rectangle.height;
        repaint();
      } 
    }
    
    public void paint(Graphics param1Graphics) {
      if (isVisible())
        try {
          JTextComponent jTextComponent = getComponent();
          Color color = jTextComponent.hasFocus() ? jTextComponent.getCaretColor() : jTextComponent.getDisabledTextColor();
          TextUI textUI = jTextComponent.getUI();
          int i = getDot();
          Rectangle rectangle = textUI.modelToView(jTextComponent, i);
          int j = rectangle.x - 2;
          int k = rectangle.x + 2;
          int m = rectangle.y + 1;
          int n = rectangle.y + rectangle.height - 2;
          param1Graphics.setColor(color);
          param1Graphics.drawLine(rectangle.x, m, rectangle.x, n);
          param1Graphics.drawLine(j, m, k, m);
          param1Graphics.drawLine(j, n, k, n);
        } catch (BadLocationException badLocationException) {} 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\motif\MotifTextUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */