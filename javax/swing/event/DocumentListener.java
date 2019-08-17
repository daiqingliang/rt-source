package javax.swing.event;

import java.util.EventListener;

public interface DocumentListener extends EventListener {
  void insertUpdate(DocumentEvent paramDocumentEvent);
  
  void removeUpdate(DocumentEvent paramDocumentEvent);
  
  void changedUpdate(DocumentEvent paramDocumentEvent);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\event\DocumentListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */