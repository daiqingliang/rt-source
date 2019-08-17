package java.text;

public class FieldPosition {
  int field = 0;
  
  int endIndex = 0;
  
  int beginIndex = 0;
  
  private Format.Field attribute;
  
  public FieldPosition(int paramInt) { this.field = paramInt; }
  
  public FieldPosition(Format.Field paramField) { this(paramField, -1); }
  
  public FieldPosition(Format.Field paramField, int paramInt) {
    this.attribute = paramField;
    this.field = paramInt;
  }
  
  public Format.Field getFieldAttribute() { return this.attribute; }
  
  public int getField() { return this.field; }
  
  public int getBeginIndex() { return this.beginIndex; }
  
  public int getEndIndex() { return this.endIndex; }
  
  public void setBeginIndex(int paramInt) { this.beginIndex = paramInt; }
  
  public void setEndIndex(int paramInt) { this.endIndex = paramInt; }
  
  Format.FieldDelegate getFieldDelegate() { return new Delegate(null); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof FieldPosition))
      return false; 
    FieldPosition fieldPosition = (FieldPosition)paramObject;
    if (this.attribute == null) {
      if (fieldPosition.attribute != null)
        return false; 
    } else if (!this.attribute.equals(fieldPosition.attribute)) {
      return false;
    } 
    return (this.beginIndex == fieldPosition.beginIndex && this.endIndex == fieldPosition.endIndex && this.field == fieldPosition.field);
  }
  
  public int hashCode() { return this.field << 24 | this.beginIndex << 16 | this.endIndex; }
  
  public String toString() { return getClass().getName() + "[field=" + this.field + ",attribute=" + this.attribute + ",beginIndex=" + this.beginIndex + ",endIndex=" + this.endIndex + ']'; }
  
  private boolean matchesField(Format.Field paramField) { return (this.attribute != null) ? this.attribute.equals(paramField) : 0; }
  
  private boolean matchesField(Format.Field paramField, int paramInt) { return (this.attribute != null) ? this.attribute.equals(paramField) : ((paramInt == this.field) ? 1 : 0); }
  
  private class Delegate implements Format.FieldDelegate {
    private boolean encounteredField;
    
    private Delegate() {}
    
    public void formatted(Format.Field param1Field, Object param1Object, int param1Int1, int param1Int2, StringBuffer param1StringBuffer) {
      if (!this.encounteredField && FieldPosition.this.matchesField(param1Field)) {
        FieldPosition.this.setBeginIndex(param1Int1);
        FieldPosition.this.setEndIndex(param1Int2);
        this.encounteredField = (param1Int1 != param1Int2);
      } 
    }
    
    public void formatted(int param1Int1, Format.Field param1Field, Object param1Object, int param1Int2, int param1Int3, StringBuffer param1StringBuffer) {
      if (!this.encounteredField && FieldPosition.this.matchesField(param1Field, param1Int1)) {
        FieldPosition.this.setBeginIndex(param1Int2);
        FieldPosition.this.setEndIndex(param1Int3);
        this.encounteredField = (param1Int2 != param1Int3);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\FieldPosition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */