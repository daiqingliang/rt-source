package com.sun.java.swing.plaf.windows;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.View;

public abstract class WindowsTextUI extends BasicTextUI {
  static LayeredHighlighter.LayerPainter WindowsPainter = new WindowsHighlightPainter(null);
  
  protected Caret createCaret() { return new WindowsCaret(); }
  
  static class WindowsCaret extends DefaultCaret implements UIResource {
    protected Highlighter.HighlightPainter getSelectionPainter() { return WindowsTextUI.WindowsPainter; }
  }
  
  static class WindowsHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
    WindowsHighlightPainter(Color param1Color) { super(param1Color); }
    
    public void paint(Graphics param1Graphics, int param1Int1, int param1Int2, Shape param1Shape, JTextComponent param1JTextComponent) {
      Rectangle rectangle = param1Shape.getBounds();
      try {
        TextUI textUI = param1JTextComponent.getUI();
        Rectangle rectangle1 = textUI.modelToView(param1JTextComponent, param1Int1);
        Rectangle rectangle2 = textUI.modelToView(param1JTextComponent, param1Int2);
        Color color = getColor();
        if (color == null) {
          param1Graphics.setColor(param1JTextComponent.getSelectionColor());
        } else {
          param1Graphics.setColor(color);
        } 
        boolean bool1 = false;
        boolean bool2 = false;
        if (param1JTextComponent.isEditable()) {
          int i = param1JTextComponent.getCaretPosition();
          bool1 = (param1Int1 == i) ? 1 : 0;
          bool2 = (param1Int2 == i) ? 1 : 0;
        } 
        if (rectangle1.y == rectangle2.y) {
          Rectangle rectangle3 = rectangle1.union(rectangle2);
          if (rectangle3.width > 0)
            if (bool1) {
              rectangle3.x++;
              rectangle3.width--;
            } else if (bool2) {
              rectangle3.width--;
            }  
          param1Graphics.fillRect(rectangle3.x, rectangle3.y, rectangle3.width, rectangle3.height);
        } else {
          int i = rectangle.x + rectangle.width - rectangle1.x;
          if (bool1 && i > 0) {
            rectangle1.x++;
            i--;
          } 
          param1Graphics.fillRect(rectangle1.x, rectangle1.y, i, rectangle1.height);
          if (rectangle1.y + rectangle1.height != rectangle2.y)
            param1Graphics.fillRect(rectangle.x, rectangle1.y + rectangle1.height, rectangle.width, rectangle2.y - rectangle1.y + rectangle1.height); 
          if (bool2 && rectangle2.x > rectangle.x)
            rectangle2.x--; 
          param1Graphics.fillRect(rectangle.x, rectangle2.y, rectangle2.x - rectangle.x, rectangle2.height);
        } 
      } catch (BadLocationException badLocationException) {}
    }
    
    public Shape paintLayer(Graphics param1Graphics, int param1Int1, int param1Int2, Shape param1Shape, JTextComponent param1JTextComponent, View param1View) {
      Color color = getColor();
      if (color == null) {
        param1Graphics.setColor(param1JTextComponent.getSelectionColor());
      } else {
        param1Graphics.setColor(color);
      } 
      boolean bool1 = false;
      boolean bool2 = false;
      if (param1JTextComponent.isEditable()) {
        int i = param1JTextComponent.getCaretPosition();
        bool1 = (param1Int1 == i) ? 1 : 0;
        bool2 = (param1Int2 == i) ? 1 : 0;
      } 
      if (param1Int1 == param1View.getStartOffset() && param1Int2 == param1View.getEndOffset()) {
        Rectangle rectangle;
        if (param1Shape instanceof Rectangle) {
          rectangle = (Rectangle)param1Shape;
        } else {
          rectangle = param1Shape.getBounds();
        } 
        if (bool1 && rectangle.width > 0) {
          param1Graphics.fillRect(rectangle.x + 1, rectangle.y, rectangle.width - 1, rectangle.height);
        } else if (bool2 && rectangle.width > 0) {
          param1Graphics.fillRect(rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height);
        } else {
          param1Graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        } 
        return rectangle;
      } 
      try {
        Shape shape = param1View.modelToView(param1Int1, Position.Bias.Forward, param1Int2, Position.Bias.Backward, param1Shape);
        Rectangle rectangle = (shape instanceof Rectangle) ? (Rectangle)shape : shape.getBounds();
        if (bool1 && rectangle.width > 0) {
          param1Graphics.fillRect(rectangle.x + 1, rectangle.y, rectangle.width - 1, rectangle.height);
        } else if (bool2 && rectangle.width > 0) {
          param1Graphics.fillRect(rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height);
        } else {
          param1Graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        } 
        return rectangle;
      } catch (BadLocationException badLocationException) {
        return null;
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\java\swing\plaf\windows\WindowsTextUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */