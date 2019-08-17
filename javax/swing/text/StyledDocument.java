package javax.swing.text;

import java.awt.Color;
import java.awt.Font;

public interface StyledDocument extends Document {
  Style addStyle(String paramString, Style paramStyle);
  
  void removeStyle(String paramString);
  
  Style getStyle(String paramString);
  
  void setCharacterAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean);
  
  void setParagraphAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet, boolean paramBoolean);
  
  void setLogicalStyle(int paramInt, Style paramStyle);
  
  Style getLogicalStyle(int paramInt);
  
  Element getParagraphElement(int paramInt);
  
  Element getCharacterElement(int paramInt);
  
  Color getForeground(AttributeSet paramAttributeSet);
  
  Color getBackground(AttributeSet paramAttributeSet);
  
  Font getFont(AttributeSet paramAttributeSet);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\StyledDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */