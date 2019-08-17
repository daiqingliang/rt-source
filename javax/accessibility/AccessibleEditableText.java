package javax.accessibility;

import javax.swing.text.AttributeSet;

public interface AccessibleEditableText extends AccessibleText {
  void setTextContents(String paramString);
  
  void insertTextAtIndex(int paramInt, String paramString);
  
  String getTextRange(int paramInt1, int paramInt2);
  
  void delete(int paramInt1, int paramInt2);
  
  void cut(int paramInt1, int paramInt2);
  
  void paste(int paramInt);
  
  void replaceText(int paramInt1, int paramInt2, String paramString);
  
  void selectText(int paramInt1, int paramInt2);
  
  void setAttributes(int paramInt1, int paramInt2, AttributeSet paramAttributeSet);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\accessibility\AccessibleEditableText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */