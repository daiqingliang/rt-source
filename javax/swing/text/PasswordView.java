package javax.swing.text;

import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.JPasswordField;
import sun.swing.SwingUtilities2;

public class PasswordView extends FieldView {
  static char[] ONE = new char[1];
  
  public PasswordView(Element paramElement) { super(paramElement); }
  
  protected int drawUnselectedText(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws BadLocationException {
    Container container = getContainer();
    if (container instanceof JPasswordField) {
      JPasswordField jPasswordField = (JPasswordField)container;
      if (!jPasswordField.echoCharIsSet())
        return super.drawUnselectedText(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); 
      if (jPasswordField.isEnabled()) {
        paramGraphics.setColor(jPasswordField.getForeground());
      } else {
        paramGraphics.setColor(jPasswordField.getDisabledTextColor());
      } 
      char c = jPasswordField.getEchoChar();
      int i = paramInt4 - paramInt3;
      for (byte b = 0; b < i; b++)
        paramInt1 = drawEchoCharacter(paramGraphics, paramInt1, paramInt2, c); 
    } 
    return paramInt1;
  }
  
  protected int drawSelectedText(Graphics paramGraphics, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws BadLocationException {
    paramGraphics.setColor(this.selected);
    Container container = getContainer();
    if (container instanceof JPasswordField) {
      JPasswordField jPasswordField = (JPasswordField)container;
      if (!jPasswordField.echoCharIsSet())
        return super.drawSelectedText(paramGraphics, paramInt1, paramInt2, paramInt3, paramInt4); 
      char c = jPasswordField.getEchoChar();
      int i = paramInt4 - paramInt3;
      for (byte b = 0; b < i; b++)
        paramInt1 = drawEchoCharacter(paramGraphics, paramInt1, paramInt2, c); 
    } 
    return paramInt1;
  }
  
  protected int drawEchoCharacter(Graphics paramGraphics, int paramInt1, int paramInt2, char paramChar) {
    ONE[0] = paramChar;
    SwingUtilities2.drawChars(Utilities.getJComponent(this), paramGraphics, ONE, 0, 1, paramInt1, paramInt2);
    return paramInt1 + paramGraphics.getFontMetrics().charWidth(paramChar);
  }
  
  public Shape modelToView(int paramInt, Shape paramShape, Position.Bias paramBias) throws BadLocationException {
    Container container = getContainer();
    if (container instanceof JPasswordField) {
      JPasswordField jPasswordField = (JPasswordField)container;
      if (!jPasswordField.echoCharIsSet())
        return super.modelToView(paramInt, paramShape, paramBias); 
      char c = jPasswordField.getEchoChar();
      FontMetrics fontMetrics = jPasswordField.getFontMetrics(jPasswordField.getFont());
      Rectangle rectangle = adjustAllocation(paramShape).getBounds();
      int i = (paramInt - getStartOffset()) * fontMetrics.charWidth(c);
      rectangle.x += i;
      rectangle.width = 1;
      return rectangle;
    } 
    return null;
  }
  
  public int viewToModel(float paramFloat1, float paramFloat2, Shape paramShape, Position.Bias[] paramArrayOfBias) {
    paramArrayOfBias[0] = Position.Bias.Forward;
    int i = 0;
    Container container = getContainer();
    if (container instanceof JPasswordField) {
      JPasswordField jPasswordField = (JPasswordField)container;
      if (!jPasswordField.echoCharIsSet())
        return super.viewToModel(paramFloat1, paramFloat2, paramShape, paramArrayOfBias); 
      char c = jPasswordField.getEchoChar();
      int j = jPasswordField.getFontMetrics(jPasswordField.getFont()).charWidth(c);
      paramShape = adjustAllocation(paramShape);
      Rectangle rectangle = (paramShape instanceof Rectangle) ? (Rectangle)paramShape : paramShape.getBounds();
      i = (j > 0) ? (((int)paramFloat1 - rectangle.x) / j) : Integer.MAX_VALUE;
      if (i < 0) {
        i = 0;
      } else if (i > getStartOffset() + getDocument().getLength()) {
        i = getDocument().getLength() - getStartOffset();
      } 
    } 
    return getStartOffset() + i;
  }
  
  public float getPreferredSpan(int paramInt) {
    Container container;
    switch (paramInt) {
      case 0:
        container = getContainer();
        if (container instanceof JPasswordField) {
          JPasswordField jPasswordField = (JPasswordField)container;
          if (jPasswordField.echoCharIsSet()) {
            char c = jPasswordField.getEchoChar();
            FontMetrics fontMetrics = jPasswordField.getFontMetrics(jPasswordField.getFont());
            Document document = getDocument();
            return (fontMetrics.charWidth(c) * getDocument().getLength());
          } 
        } 
        break;
    } 
    return super.getPreferredSpan(paramInt);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\PasswordView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */