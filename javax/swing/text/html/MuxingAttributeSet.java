package javax.swing.text.html;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

class MuxingAttributeSet implements AttributeSet, Serializable {
  private AttributeSet[] attrs;
  
  public MuxingAttributeSet(AttributeSet[] paramArrayOfAttributeSet) { this.attrs = paramArrayOfAttributeSet; }
  
  protected MuxingAttributeSet() {}
  
  protected void setAttributes(AttributeSet[] paramArrayOfAttributeSet) { this.attrs = paramArrayOfAttributeSet; }
  
  protected AttributeSet[] getAttributes() { return this.attrs; }
  
  protected void insertAttributeSetAt(AttributeSet paramAttributeSet, int paramInt) {
    int i = this.attrs.length;
    AttributeSet[] arrayOfAttributeSet = new AttributeSet[i + 1];
    if (paramInt < i) {
      if (paramInt > 0) {
        System.arraycopy(this.attrs, 0, arrayOfAttributeSet, 0, paramInt);
        System.arraycopy(this.attrs, paramInt, arrayOfAttributeSet, paramInt + 1, i - paramInt);
      } else {
        System.arraycopy(this.attrs, 0, arrayOfAttributeSet, 1, i);
      } 
    } else {
      System.arraycopy(this.attrs, 0, arrayOfAttributeSet, 0, i);
    } 
    arrayOfAttributeSet[paramInt] = paramAttributeSet;
    this.attrs = arrayOfAttributeSet;
  }
  
  protected void removeAttributeSetAt(int paramInt) {
    int i = this.attrs.length;
    AttributeSet[] arrayOfAttributeSet = new AttributeSet[i - 1];
    if (i > 0)
      if (paramInt == 0) {
        System.arraycopy(this.attrs, 1, arrayOfAttributeSet, 0, i - 1);
      } else if (paramInt < i - 1) {
        System.arraycopy(this.attrs, 0, arrayOfAttributeSet, 0, paramInt);
        System.arraycopy(this.attrs, paramInt + 1, arrayOfAttributeSet, paramInt, i - paramInt - 1);
      } else {
        System.arraycopy(this.attrs, 0, arrayOfAttributeSet, 0, i - 1);
      }  
    this.attrs = arrayOfAttributeSet;
  }
  
  public int getAttributeCount() {
    AttributeSet[] arrayOfAttributeSet = getAttributes();
    int i = 0;
    for (byte b = 0; b < arrayOfAttributeSet.length; b++)
      i += arrayOfAttributeSet[b].getAttributeCount(); 
    return i;
  }
  
  public boolean isDefined(Object paramObject) {
    AttributeSet[] arrayOfAttributeSet = getAttributes();
    for (byte b = 0; b < arrayOfAttributeSet.length; b++) {
      if (arrayOfAttributeSet[b].isDefined(paramObject))
        return true; 
    } 
    return false;
  }
  
  public boolean isEqual(AttributeSet paramAttributeSet) { return (getAttributeCount() == paramAttributeSet.getAttributeCount() && containsAttributes(paramAttributeSet)); }
  
  public AttributeSet copyAttributes() {
    AttributeSet[] arrayOfAttributeSet = getAttributes();
    SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
    boolean bool = false;
    for (int i = arrayOfAttributeSet.length - 1; i >= 0; i--)
      simpleAttributeSet.addAttributes(arrayOfAttributeSet[i]); 
    return simpleAttributeSet;
  }
  
  public Object getAttribute(Object paramObject) {
    AttributeSet[] arrayOfAttributeSet = getAttributes();
    int i = arrayOfAttributeSet.length;
    for (byte b = 0; b < i; b++) {
      Object object = arrayOfAttributeSet[b].getAttribute(paramObject);
      if (object != null)
        return object; 
    } 
    return null;
  }
  
  public Enumeration getAttributeNames() { return new MuxingAttributeNameEnumeration(); }
  
  public boolean containsAttribute(Object paramObject1, Object paramObject2) { return paramObject2.equals(getAttribute(paramObject1)); }
  
  public boolean containsAttributes(AttributeSet paramAttributeSet) {
    boolean bool = true;
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (bool && enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      bool = paramAttributeSet.getAttribute(object).equals(getAttribute(object));
    } 
    return bool;
  }
  
  public AttributeSet getResolveParent() { return null; }
  
  private class MuxingAttributeNameEnumeration implements Enumeration {
    private int attrIndex;
    
    private Enumeration currentEnum;
    
    MuxingAttributeNameEnumeration() { updateEnum(); }
    
    public boolean hasMoreElements() { return (this.currentEnum == null) ? false : this.currentEnum.hasMoreElements(); }
    
    public Object nextElement() {
      if (this.currentEnum == null)
        throw new NoSuchElementException("No more names"); 
      Object object = this.currentEnum.nextElement();
      if (!this.currentEnum.hasMoreElements())
        updateEnum(); 
      return object;
    }
    
    void updateEnum() {
      AttributeSet[] arrayOfAttributeSet = MuxingAttributeSet.this.getAttributes();
      this.currentEnum = null;
      while (this.currentEnum == null && this.attrIndex < arrayOfAttributeSet.length) {
        this.currentEnum = arrayOfAttributeSet[this.attrIndex++].getAttributeNames();
        if (!this.currentEnum.hasMoreElements())
          this.currentEnum = null; 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\MuxingAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */