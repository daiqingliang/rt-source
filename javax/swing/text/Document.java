package javax.swing.text;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;

public interface Document {
  public static final String StreamDescriptionProperty = "stream";
  
  public static final String TitleProperty = "title";
  
  int getLength();
  
  void addDocumentListener(DocumentListener paramDocumentListener);
  
  void removeDocumentListener(DocumentListener paramDocumentListener);
  
  void addUndoableEditListener(UndoableEditListener paramUndoableEditListener);
  
  void removeUndoableEditListener(UndoableEditListener paramUndoableEditListener);
  
  Object getProperty(Object paramObject);
  
  void putProperty(Object paramObject1, Object paramObject2);
  
  void remove(int paramInt1, int paramInt2) throws BadLocationException;
  
  void insertString(int paramInt, String paramString, AttributeSet paramAttributeSet) throws BadLocationException;
  
  String getText(int paramInt1, int paramInt2) throws BadLocationException;
  
  void getText(int paramInt1, int paramInt2, Segment paramSegment) throws BadLocationException;
  
  Position getStartPosition();
  
  Position getEndPosition();
  
  Position createPosition(int paramInt) throws BadLocationException;
  
  Element[] getRootElements();
  
  Element getDefaultRootElement();
  
  void render(Runnable paramRunnable);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\Document.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */