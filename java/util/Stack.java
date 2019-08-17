package java.util;

public class Stack<E> extends Vector<E> {
  private static final long serialVersionUID = 1224463164541339165L;
  
  public E push(E paramE) {
    addElement(paramE);
    return paramE;
  }
  
  public E pop() {
    int i = size();
    Object object = peek();
    removeElementAt(i - 1);
    return (E)object;
  }
  
  public E peek() {
    int i = size();
    if (i == 0)
      throw new EmptyStackException(); 
    return (E)elementAt(i - 1);
  }
  
  public boolean empty() { return (size() == 0); }
  
  public int search(Object paramObject) {
    int i = lastIndexOf(paramObject);
    return (i >= 0) ? (size() - i) : -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\Stack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */