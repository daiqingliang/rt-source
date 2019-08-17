package javax.swing.plaf.basic;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import sun.reflect.misc.MethodUtil;

public class BasicComboBoxEditor implements ComboBoxEditor, FocusListener {
  protected JTextField editor = createEditorComponent();
  
  private Object oldValue;
  
  public Component getEditorComponent() { return this.editor; }
  
  protected JTextField createEditorComponent() {
    BorderlessTextField borderlessTextField = new BorderlessTextField("", 9);
    borderlessTextField.setBorder(null);
    return borderlessTextField;
  }
  
  public void setItem(Object paramObject) {
    String str;
    if (paramObject != null) {
      str = paramObject.toString();
      if (str == null)
        str = ""; 
      this.oldValue = paramObject;
    } else {
      str = "";
    } 
    if (!str.equals(this.editor.getText()))
      this.editor.setText(str); 
  }
  
  public Object getItem() {
    Object object = this.editor.getText();
    if (this.oldValue != null && !(this.oldValue instanceof String)) {
      if (object.equals(this.oldValue.toString()))
        return this.oldValue; 
      Class clazz = this.oldValue.getClass();
      try {
        Method method = MethodUtil.getMethod(clazz, "valueOf", new Class[] { String.class });
        object = MethodUtil.invoke(method, this.oldValue, new Object[] { this.editor.getText() });
      } catch (Exception exception) {}
    } 
    return object;
  }
  
  public void selectAll() {
    this.editor.selectAll();
    this.editor.requestFocus();
  }
  
  public void focusGained(FocusEvent paramFocusEvent) {}
  
  public void focusLost(FocusEvent paramFocusEvent) {}
  
  public void addActionListener(ActionListener paramActionListener) { this.editor.addActionListener(paramActionListener); }
  
  public void removeActionListener(ActionListener paramActionListener) { this.editor.removeActionListener(paramActionListener); }
  
  static class BorderlessTextField extends JTextField {
    public BorderlessTextField(String param1String, int param1Int) { super(param1String, param1Int); }
    
    public void setText(String param1String) {
      if (getText().equals(param1String))
        return; 
      super.setText(param1String);
    }
    
    public void setBorder(Border param1Border) {
      if (!(param1Border instanceof BasicComboBoxEditor.UIResource))
        super.setBorder(param1Border); 
    }
  }
  
  public static class UIResource extends BasicComboBoxEditor implements UIResource {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicComboBoxEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */