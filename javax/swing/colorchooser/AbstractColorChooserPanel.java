package javax.swing.colorchooser;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;

public abstract class AbstractColorChooserPanel extends JPanel {
  private final PropertyChangeListener enabledListener = new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
        Object object = param1PropertyChangeEvent.getNewValue();
        if (object instanceof Boolean)
          AbstractColorChooserPanel.this.setEnabled(((Boolean)object).booleanValue()); 
      }
    };
  
  private JColorChooser chooser;
  
  public abstract void updateChooser();
  
  protected abstract void buildChooser();
  
  public abstract String getDisplayName();
  
  public int getMnemonic() { return 0; }
  
  public int getDisplayedMnemonicIndex() { return -1; }
  
  public abstract Icon getSmallDisplayIcon();
  
  public abstract Icon getLargeDisplayIcon();
  
  public void installChooserPanel(JColorChooser paramJColorChooser) {
    if (this.chooser != null)
      throw new RuntimeException("This chooser panel is already installed"); 
    this.chooser = paramJColorChooser;
    this.chooser.addPropertyChangeListener("enabled", this.enabledListener);
    setEnabled(this.chooser.isEnabled());
    buildChooser();
    updateChooser();
  }
  
  public void uninstallChooserPanel(JColorChooser paramJColorChooser) {
    this.chooser.removePropertyChangeListener("enabled", this.enabledListener);
    this.chooser = null;
  }
  
  public ColorSelectionModel getColorSelectionModel() { return (this.chooser != null) ? this.chooser.getSelectionModel() : null; }
  
  protected Color getColorFromModel() {
    ColorSelectionModel colorSelectionModel = getColorSelectionModel();
    return (colorSelectionModel != null) ? colorSelectionModel.getSelectedColor() : null;
  }
  
  void setSelectedColor(Color paramColor) {
    ColorSelectionModel colorSelectionModel = getColorSelectionModel();
    if (colorSelectionModel != null)
      colorSelectionModel.setSelectedColor(paramColor); 
  }
  
  public void paint(Graphics paramGraphics) { super.paint(paramGraphics); }
  
  int getInt(Object paramObject, int paramInt) {
    Object object = UIManager.get(paramObject, getLocale());
    if (object instanceof Integer)
      return ((Integer)object).intValue(); 
    if (object instanceof String)
      try {
        return Integer.parseInt((String)object);
      } catch (NumberFormatException numberFormatException) {} 
    return paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\AbstractColorChooserPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */