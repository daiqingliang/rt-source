package java.beans;

public class IndexedPropertyChangeEvent extends PropertyChangeEvent {
  private static final long serialVersionUID = -320227448495806870L;
  
  private int index;
  
  public IndexedPropertyChangeEvent(Object paramObject1, String paramString, Object paramObject2, Object paramObject3, int paramInt) {
    super(paramObject1, paramString, paramObject2, paramObject3);
    this.index = paramInt;
  }
  
  public int getIndex() { return this.index; }
  
  void appendTo(StringBuilder paramStringBuilder) { paramStringBuilder.append("; index=").append(getIndex()); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\IndexedPropertyChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */