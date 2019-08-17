package javax.swing.tree;

import javax.swing.event.TreeExpansionEvent;

public class ExpandVetoException extends Exception {
  protected TreeExpansionEvent event;
  
  public ExpandVetoException(TreeExpansionEvent paramTreeExpansionEvent) { this(paramTreeExpansionEvent, null); }
  
  public ExpandVetoException(TreeExpansionEvent paramTreeExpansionEvent, String paramString) {
    super(paramString);
    this.event = paramTreeExpansionEvent;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\tree\ExpandVetoException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */