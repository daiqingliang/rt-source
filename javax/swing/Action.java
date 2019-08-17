package javax.swing;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;

public interface Action extends ActionListener {
  public static final String DEFAULT = "Default";
  
  public static final String NAME = "Name";
  
  public static final String SHORT_DESCRIPTION = "ShortDescription";
  
  public static final String LONG_DESCRIPTION = "LongDescription";
  
  public static final String SMALL_ICON = "SmallIcon";
  
  public static final String ACTION_COMMAND_KEY = "ActionCommandKey";
  
  public static final String ACCELERATOR_KEY = "AcceleratorKey";
  
  public static final String MNEMONIC_KEY = "MnemonicKey";
  
  public static final String SELECTED_KEY = "SwingSelectedKey";
  
  public static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
  
  public static final String LARGE_ICON_KEY = "SwingLargeIconKey";
  
  Object getValue(String paramString);
  
  void putValue(String paramString, Object paramObject);
  
  void setEnabled(boolean paramBoolean);
  
  boolean isEnabled();
  
  void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
  
  void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\Action.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */