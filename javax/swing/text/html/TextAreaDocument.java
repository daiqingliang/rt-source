package javax.swing.text.html;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

class TextAreaDocument extends PlainDocument {
  String initialText;
  
  void reset() {
    try {
      remove(0, getLength());
      if (this.initialText != null)
        insertString(0, this.initialText, null); 
    } catch (BadLocationException badLocationException) {}
  }
  
  void storeInitialText() {
    try {
      this.initialText = getText(0, getLength());
    } catch (BadLocationException badLocationException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\TextAreaDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */