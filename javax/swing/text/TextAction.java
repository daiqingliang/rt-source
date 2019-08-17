package javax.swing.text;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractAction;
import javax.swing.Action;

public abstract class TextAction extends AbstractAction {
  public TextAction(String paramString) { super(paramString); }
  
  protected final JTextComponent getTextComponent(ActionEvent paramActionEvent) {
    if (paramActionEvent != null) {
      Object object = paramActionEvent.getSource();
      if (object instanceof JTextComponent)
        return (JTextComponent)object; 
    } 
    return getFocusedComponent();
  }
  
  public static final Action[] augmentList(Action[] paramArrayOfAction1, Action[] paramArrayOfAction2) {
    Hashtable hashtable = new Hashtable();
    for (Action action : paramArrayOfAction1) {
      String str = (String)action.getValue("Name");
      hashtable.put((str != null) ? str : "", action);
    } 
    for (Action action : paramArrayOfAction2) {
      String str = (String)action.getValue("Name");
      hashtable.put((str != null) ? str : "", action);
    } 
    Action[] arrayOfAction = new Action[hashtable.size()];
    byte b = 0;
    Enumeration enumeration = hashtable.elements();
    while (enumeration.hasMoreElements())
      arrayOfAction[b++] = (Action)enumeration.nextElement(); 
    return arrayOfAction;
  }
  
  protected final JTextComponent getFocusedComponent() { return JTextComponent.getFocusedComponent(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\TextAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */