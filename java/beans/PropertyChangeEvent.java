package java.beans;

import java.util.EventObject;

public class PropertyChangeEvent extends EventObject {
  private static final long serialVersionUID = 7042693688939648123L;
  
  private String propertyName;
  
  private Object newValue;
  
  private Object oldValue;
  
  private Object propagationId;
  
  public PropertyChangeEvent(Object paramObject1, String paramString, Object paramObject2, Object paramObject3) {
    super(paramObject1);
    this.propertyName = paramString;
    this.newValue = paramObject3;
    this.oldValue = paramObject2;
  }
  
  public String getPropertyName() { return this.propertyName; }
  
  public Object getNewValue() { return this.newValue; }
  
  public Object getOldValue() { return this.oldValue; }
  
  public void setPropagationId(Object paramObject) { this.propagationId = paramObject; }
  
  public Object getPropagationId() { return this.propagationId; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(getClass().getName());
    stringBuilder.append("[propertyName=").append(getPropertyName());
    appendTo(stringBuilder);
    stringBuilder.append("; oldValue=").append(getOldValue());
    stringBuilder.append("; newValue=").append(getNewValue());
    stringBuilder.append("; propagationId=").append(getPropagationId());
    stringBuilder.append("; source=").append(getSource());
    return stringBuilder.append("]").toString();
  }
  
  void appendTo(StringBuilder paramStringBuilder) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\PropertyChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */