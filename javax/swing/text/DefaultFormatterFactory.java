package javax.swing.text;

import java.io.Serializable;
import javax.swing.JFormattedTextField;

public class DefaultFormatterFactory extends JFormattedTextField.AbstractFormatterFactory implements Serializable {
  private JFormattedTextField.AbstractFormatter defaultFormat;
  
  private JFormattedTextField.AbstractFormatter displayFormat;
  
  private JFormattedTextField.AbstractFormatter editFormat;
  
  private JFormattedTextField.AbstractFormatter nullFormat;
  
  public DefaultFormatterFactory() {}
  
  public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter paramAbstractFormatter) { this(paramAbstractFormatter, null); }
  
  public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter paramAbstractFormatter1, JFormattedTextField.AbstractFormatter paramAbstractFormatter2) { this(paramAbstractFormatter1, paramAbstractFormatter2, null); }
  
  public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter paramAbstractFormatter1, JFormattedTextField.AbstractFormatter paramAbstractFormatter2, JFormattedTextField.AbstractFormatter paramAbstractFormatter3) { this(paramAbstractFormatter1, paramAbstractFormatter2, paramAbstractFormatter3, null); }
  
  public DefaultFormatterFactory(JFormattedTextField.AbstractFormatter paramAbstractFormatter1, JFormattedTextField.AbstractFormatter paramAbstractFormatter2, JFormattedTextField.AbstractFormatter paramAbstractFormatter3, JFormattedTextField.AbstractFormatter paramAbstractFormatter4) {
    this.defaultFormat = paramAbstractFormatter1;
    this.displayFormat = paramAbstractFormatter2;
    this.editFormat = paramAbstractFormatter3;
    this.nullFormat = paramAbstractFormatter4;
  }
  
  public void setDefaultFormatter(JFormattedTextField.AbstractFormatter paramAbstractFormatter) { this.defaultFormat = paramAbstractFormatter; }
  
  public JFormattedTextField.AbstractFormatter getDefaultFormatter() { return this.defaultFormat; }
  
  public void setDisplayFormatter(JFormattedTextField.AbstractFormatter paramAbstractFormatter) { this.displayFormat = paramAbstractFormatter; }
  
  public JFormattedTextField.AbstractFormatter getDisplayFormatter() { return this.displayFormat; }
  
  public void setEditFormatter(JFormattedTextField.AbstractFormatter paramAbstractFormatter) { this.editFormat = paramAbstractFormatter; }
  
  public JFormattedTextField.AbstractFormatter getEditFormatter() { return this.editFormat; }
  
  public void setNullFormatter(JFormattedTextField.AbstractFormatter paramAbstractFormatter) { this.nullFormat = paramAbstractFormatter; }
  
  public JFormattedTextField.AbstractFormatter getNullFormatter() { return this.nullFormat; }
  
  public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField paramJFormattedTextField) {
    JFormattedTextField.AbstractFormatter abstractFormatter = null;
    if (paramJFormattedTextField == null)
      return null; 
    Object object = paramJFormattedTextField.getValue();
    if (object == null)
      abstractFormatter = getNullFormatter(); 
    if (abstractFormatter == null) {
      if (paramJFormattedTextField.hasFocus()) {
        abstractFormatter = getEditFormatter();
      } else {
        abstractFormatter = getDisplayFormatter();
      } 
      if (abstractFormatter == null)
        abstractFormatter = getDefaultFormatter(); 
    } 
    return abstractFormatter;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\DefaultFormatterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */