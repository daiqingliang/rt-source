package java.beans.beancontext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class BeanContextMembershipEvent extends BeanContextEvent {
  private static final long serialVersionUID = 3499346510334590959L;
  
  protected Collection children;
  
  public BeanContextMembershipEvent(BeanContext paramBeanContext, Collection paramCollection) {
    super(paramBeanContext);
    if (paramCollection == null)
      throw new NullPointerException("BeanContextMembershipEvent constructor:  changes is null."); 
    this.children = paramCollection;
  }
  
  public BeanContextMembershipEvent(BeanContext paramBeanContext, Object[] paramArrayOfObject) {
    super(paramBeanContext);
    if (paramArrayOfObject == null)
      throw new NullPointerException("BeanContextMembershipEvent:  changes is null."); 
    this.children = Arrays.asList(paramArrayOfObject);
  }
  
  public int size() { return this.children.size(); }
  
  public boolean contains(Object paramObject) { return this.children.contains(paramObject); }
  
  public Object[] toArray() { return this.children.toArray(); }
  
  public Iterator iterator() { return this.children.iterator(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\beancontext\BeanContextMembershipEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */