package sun.misc;

import java.lang.ref.SoftReference;

@Deprecated
public abstract class Ref {
  private SoftReference soft = null;
  
  public Object get() {
    Object object = check();
    if (object == null) {
      object = reconstitute();
      setThing(object);
    } 
    return object;
  }
  
  public abstract Object reconstitute();
  
  public void flush() {
    SoftReference softReference = this.soft;
    if (softReference != null)
      softReference.clear(); 
    this.soft = null;
  }
  
  public void setThing(Object paramObject) {
    flush();
    this.soft = new SoftReference(paramObject);
  }
  
  public Object check() {
    SoftReference softReference = this.soft;
    return (softReference == null) ? null : softReference.get();
  }
  
  public Ref() {}
  
  public Ref(Object paramObject) { setThing(paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\Ref.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */