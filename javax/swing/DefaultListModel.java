package javax.swing;

import java.util.Enumeration;
import java.util.Vector;

public class DefaultListModel<E> extends AbstractListModel<E> {
  private Vector<E> delegate = new Vector();
  
  public int getSize() { return this.delegate.size(); }
  
  public E getElementAt(int paramInt) { return (E)this.delegate.elementAt(paramInt); }
  
  public void copyInto(Object[] paramArrayOfObject) { this.delegate.copyInto(paramArrayOfObject); }
  
  public void trimToSize() { this.delegate.trimToSize(); }
  
  public void ensureCapacity(int paramInt) { this.delegate.ensureCapacity(paramInt); }
  
  public void setSize(int paramInt) {
    int i = this.delegate.size();
    this.delegate.setSize(paramInt);
    if (i > paramInt) {
      fireIntervalRemoved(this, paramInt, i - 1);
    } else if (i < paramInt) {
      fireIntervalAdded(this, i, paramInt - 1);
    } 
  }
  
  public int capacity() { return this.delegate.capacity(); }
  
  public int size() { return this.delegate.size(); }
  
  public boolean isEmpty() { return this.delegate.isEmpty(); }
  
  public Enumeration<E> elements() { return this.delegate.elements(); }
  
  public boolean contains(Object paramObject) { return this.delegate.contains(paramObject); }
  
  public int indexOf(Object paramObject) { return this.delegate.indexOf(paramObject); }
  
  public int indexOf(Object paramObject, int paramInt) { return this.delegate.indexOf(paramObject, paramInt); }
  
  public int lastIndexOf(Object paramObject) { return this.delegate.lastIndexOf(paramObject); }
  
  public int lastIndexOf(Object paramObject, int paramInt) { return this.delegate.lastIndexOf(paramObject, paramInt); }
  
  public E elementAt(int paramInt) { return (E)this.delegate.elementAt(paramInt); }
  
  public E firstElement() { return (E)this.delegate.firstElement(); }
  
  public E lastElement() { return (E)this.delegate.lastElement(); }
  
  public void setElementAt(E paramE, int paramInt) {
    this.delegate.setElementAt(paramE, paramInt);
    fireContentsChanged(this, paramInt, paramInt);
  }
  
  public void removeElementAt(int paramInt) {
    this.delegate.removeElementAt(paramInt);
    fireIntervalRemoved(this, paramInt, paramInt);
  }
  
  public void insertElementAt(E paramE, int paramInt) {
    this.delegate.insertElementAt(paramE, paramInt);
    fireIntervalAdded(this, paramInt, paramInt);
  }
  
  public void addElement(E paramE) {
    int i = this.delegate.size();
    this.delegate.addElement(paramE);
    fireIntervalAdded(this, i, i);
  }
  
  public boolean removeElement(Object paramObject) {
    int i = indexOf(paramObject);
    boolean bool = this.delegate.removeElement(paramObject);
    if (i >= 0)
      fireIntervalRemoved(this, i, i); 
    return bool;
  }
  
  public void removeAllElements() {
    int i = this.delegate.size() - 1;
    this.delegate.removeAllElements();
    if (i >= 0)
      fireIntervalRemoved(this, 0, i); 
  }
  
  public String toString() { return this.delegate.toString(); }
  
  public Object[] toArray() {
    Object[] arrayOfObject = new Object[this.delegate.size()];
    this.delegate.copyInto(arrayOfObject);
    return arrayOfObject;
  }
  
  public E get(int paramInt) { return (E)this.delegate.elementAt(paramInt); }
  
  public E set(int paramInt, E paramE) {
    Object object = this.delegate.elementAt(paramInt);
    this.delegate.setElementAt(paramE, paramInt);
    fireContentsChanged(this, paramInt, paramInt);
    return (E)object;
  }
  
  public void add(int paramInt, E paramE) {
    this.delegate.insertElementAt(paramE, paramInt);
    fireIntervalAdded(this, paramInt, paramInt);
  }
  
  public E remove(int paramInt) {
    Object object = this.delegate.elementAt(paramInt);
    this.delegate.removeElementAt(paramInt);
    fireIntervalRemoved(this, paramInt, paramInt);
    return (E)object;
  }
  
  public void clear() {
    int i = this.delegate.size() - 1;
    this.delegate.removeAllElements();
    if (i >= 0)
      fireIntervalRemoved(this, 0, i); 
  }
  
  public void removeRange(int paramInt1, int paramInt2) {
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("fromIndex must be <= toIndex"); 
    for (int i = paramInt2; i >= paramInt1; i--)
      this.delegate.removeElementAt(i); 
    fireIntervalRemoved(this, paramInt1, paramInt2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\DefaultListModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */