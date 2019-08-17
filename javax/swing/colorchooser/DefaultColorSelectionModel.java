package javax.swing.colorchooser;

import java.awt.Color;
import java.io.Serializable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class DefaultColorSelectionModel implements ColorSelectionModel, Serializable {
  protected ChangeEvent changeEvent = null;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  private Color selectedColor = Color.white;
  
  public DefaultColorSelectionModel() {}
  
  public DefaultColorSelectionModel(Color paramColor) {}
  
  public Color getSelectedColor() { return this.selectedColor; }
  
  public void setSelectedColor(Color paramColor) {
    if (paramColor != null && !this.selectedColor.equals(paramColor)) {
      this.selectedColor = paramColor;
      fireStateChanged();
    } 
  }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\DefaultColorSelectionModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */