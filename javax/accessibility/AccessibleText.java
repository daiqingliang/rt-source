package javax.accessibility;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.text.AttributeSet;

public interface AccessibleText {
  public static final int CHARACTER = 1;
  
  public static final int WORD = 2;
  
  public static final int SENTENCE = 3;
  
  int getIndexAtPoint(Point paramPoint);
  
  Rectangle getCharacterBounds(int paramInt);
  
  int getCharCount();
  
  int getCaretPosition();
  
  String getAtIndex(int paramInt1, int paramInt2);
  
  String getAfterIndex(int paramInt1, int paramInt2);
  
  String getBeforeIndex(int paramInt1, int paramInt2);
  
  AttributeSet getCharacterAttribute(int paramInt);
  
  int getSelectionStart();
  
  int getSelectionEnd();
  
  String getSelectedText();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */