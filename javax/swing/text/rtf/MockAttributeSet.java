package javax.swing.text.rtf;

import java.util.Dictionary;
import java.util.Enumeration;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

class MockAttributeSet implements AttributeSet, MutableAttributeSet {
  public Dictionary<Object, Object> backing;
  
  public boolean isEmpty() { return this.backing.isEmpty(); }
  
  public int getAttributeCount() { return this.backing.size(); }
  
  public boolean isDefined(Object paramObject) { return (this.backing.get(paramObject) != null); }
  
  public boolean isEqual(AttributeSet paramAttributeSet) { throw new InternalError("MockAttributeSet: charade revealed!"); }
  
  public AttributeSet copyAttributes() { throw new InternalError("MockAttributeSet: charade revealed!"); }
  
  public Object getAttribute(Object paramObject) { return this.backing.get(paramObject); }
  
  public void addAttribute(Object paramObject1, Object paramObject2) { this.backing.put(paramObject1, paramObject2); }
  
  public void addAttributes(AttributeSet paramAttributeSet) {
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      this.backing.put(object, paramAttributeSet.getAttribute(object));
    } 
  }
  
  public void removeAttribute(Object paramObject) { this.backing.remove(paramObject); }
  
  public void removeAttributes(AttributeSet paramAttributeSet) { throw new InternalError("MockAttributeSet: charade revealed!"); }
  
  public void removeAttributes(Enumeration<?> paramEnumeration) { throw new InternalError("MockAttributeSet: charade revealed!"); }
  
  public void setResolveParent(AttributeSet paramAttributeSet) { throw new InternalError("MockAttributeSet: charade revealed!"); }
  
  public Enumeration getAttributeNames() { return this.backing.keys(); }
  
  public boolean containsAttribute(Object paramObject1, Object paramObject2) { throw new InternalError("MockAttributeSet: charade revealed!"); }
  
  public boolean containsAttributes(AttributeSet paramAttributeSet) { throw new InternalError("MockAttributeSet: charade revealed!"); }
  
  public AttributeSet getResolveParent() { throw new InternalError("MockAttributeSet: charade revealed!"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\rtf\MockAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */