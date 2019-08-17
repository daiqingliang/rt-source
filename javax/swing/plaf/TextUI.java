package javax.swing.plaf;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

public abstract class TextUI extends ComponentUI {
  public abstract Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt) throws BadLocationException;
  
  public abstract Rectangle modelToView(JTextComponent paramJTextComponent, int paramInt, Position.Bias paramBias) throws BadLocationException;
  
  public abstract int viewToModel(JTextComponent paramJTextComponent, Point paramPoint);
  
  public abstract int viewToModel(JTextComponent paramJTextComponent, Point paramPoint, Position.Bias[] paramArrayOfBias);
  
  public abstract int getNextVisualPositionFrom(JTextComponent paramJTextComponent, int paramInt1, Position.Bias paramBias, int paramInt2, Position.Bias[] paramArrayOfBias) throws BadLocationException;
  
  public abstract void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2);
  
  public abstract void damageRange(JTextComponent paramJTextComponent, int paramInt1, int paramInt2, Position.Bias paramBias1, Position.Bias paramBias2);
  
  public abstract EditorKit getEditorKit(JTextComponent paramJTextComponent);
  
  public abstract View getRootView(JTextComponent paramJTextComponent);
  
  public String getToolTipText(JTextComponent paramJTextComponent, Point paramPoint) { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\TextUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */