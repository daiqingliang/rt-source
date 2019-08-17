package com.sun.beans.editors;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

public final class EnumEditor implements PropertyEditor {
  private final List<PropertyChangeListener> listeners = new ArrayList();
  
  private final Class type;
  
  private final String[] tags;
  
  private Object value;
  
  public EnumEditor(Class paramClass) {
    Object[] arrayOfObject = paramClass.getEnumConstants();
    if (arrayOfObject == null)
      throw new IllegalArgumentException("Unsupported " + paramClass); 
    this.type = paramClass;
    this.tags = new String[arrayOfObject.length];
    for (byte b = 0; b < arrayOfObject.length; b++)
      this.tags[b] = ((Enum)arrayOfObject[b]).name(); 
  }
  
  public Object getValue() { return this.value; }
  
  public void setValue(Object paramObject) {
    PropertyChangeListener[] arrayOfPropertyChangeListener;
    Object object;
    if (paramObject != null && !this.type.isInstance(paramObject))
      throw new IllegalArgumentException("Unsupported value: " + paramObject); 
    synchronized (this.listeners) {
      object = this.value;
      this.value = paramObject;
      if ((paramObject == null) ? (object == null) : paramObject.equals(object))
        return; 
      int i = this.listeners.size();
      if (i == 0)
        return; 
      arrayOfPropertyChangeListener = (PropertyChangeListener[])this.listeners.toArray(new PropertyChangeListener[i]);
    } 
    PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, null, object, paramObject);
    for (PropertyChangeListener propertyChangeListener : arrayOfPropertyChangeListener)
      propertyChangeListener.propertyChange(propertyChangeEvent); 
  }
  
  public String getAsText() { return (this.value != null) ? ((Enum)this.value).name() : null; }
  
  public void setAsText(String paramString) { setValue((paramString != null) ? Enum.valueOf(this.type, paramString) : null); }
  
  public String[] getTags() { return (String[])this.tags.clone(); }
  
  public String getJavaInitializationString() {
    String str = getAsText();
    return (str != null) ? (this.type.getName() + '.' + str) : "null";
  }
  
  public boolean isPaintable() { return false; }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {}
  
  public boolean supportsCustomEditor() { return false; }
  
  public Component getCustomEditor() { return null; }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    synchronized (this.listeners) {
      this.listeners.add(paramPropertyChangeListener);
    } 
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    synchronized (this.listeners) {
      this.listeners.remove(paramPropertyChangeListener);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\EnumEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */