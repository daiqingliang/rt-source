package javax.swing.plaf.basic;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Element;
import javax.swing.text.PasswordView;
import javax.swing.text.View;

public class BasicPasswordFieldUI extends BasicTextFieldUI {
  public static ComponentUI createUI(JComponent paramJComponent) { return new BasicPasswordFieldUI(); }
  
  protected String getPropertyPrefix() { return "PasswordField"; }
  
  protected void installDefaults() {
    super.installDefaults();
    String str = getPropertyPrefix();
    Character character = (Character)UIManager.getDefaults().get(str + ".echoChar");
    if (character != null)
      LookAndFeel.installProperty(getComponent(), "echoChar", character); 
  }
  
  public View create(Element paramElement) { return new PasswordView(paramElement); }
  
  ActionMap createActionMap() {
    ActionMap actionMap = super.createActionMap();
    if (actionMap.get("select-word") != null) {
      Action action = actionMap.get("select-line");
      if (action != null) {
        actionMap.remove("select-word");
        actionMap.put("select-word", action);
      } 
    } 
    return actionMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicPasswordFieldUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */