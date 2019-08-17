package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.Consumer;

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, Serializable {
  int size = 0;
  
  Node<E> first;
  
  Node<E> last;
  
  private static final long serialVersionUID = 876323262645176354L;
  
  public LinkedList() {}
  
  public LinkedList(Collection<? extends E> paramCollection) {
    this();
    addAll(paramCollection);
  }
  
  private void linkFirst(E paramE) {
    Node node1 = this.first;
    Node node2 = new Node(null, paramE, node1);
    this.first = node2;
    if (node1 == null) {
      this.last = node2;
    } else {
      node1.prev = node2;
    } 
    this.size++;
    this.modCount++;
  }
  
  void linkLast(E paramE) {
    Node node1 = this.last;
    Node node2 = new Node(node1, paramE, null);
    this.last = node2;
    if (node1 == null) {
      this.first = node2;
    } else {
      node1.next = node2;
    } 
    this.size++;
    this.modCount++;
  }
  
  void linkBefore(E paramE, Node<E> paramNode) {
    Node node1 = paramNode.prev;
    Node node2 = new Node(node1, paramE, paramNode);
    paramNode.prev = node2;
    if (node1 == null) {
      this.first = node2;
    } else {
      node1.next = node2;
    } 
    this.size++;
    this.modCount++;
  }
  
  private E unlinkFirst(Node<E> paramNode) {
    Object object = paramNode.item;
    Node node = paramNode.next;
    paramNode.item = null;
    paramNode.next = null;
    this.first = node;
    if (node == null) {
      this.last = null;
    } else {
      node.prev = null;
    } 
    this.size--;
    this.modCount++;
    return (E)object;
  }
  
  private E unlinkLast(Node<E> paramNode) {
    Object object = paramNode.item;
    Node node = paramNode.prev;
    paramNode.item = null;
    paramNode.prev = null;
    this.last = node;
    if (node == null) {
      this.first = null;
    } else {
      node.next = null;
    } 
    this.size--;
    this.modCount++;
    return (E)object;
  }
  
  E unlink(Node<E> paramNode) {
    Object object = paramNode.item;
    Node node1 = paramNode.next;
    Node node2 = paramNode.prev;
    if (node2 == null) {
      this.first = node1;
    } else {
      node2.next = node1;
      paramNode.prev = null;
    } 
    if (node1 == null) {
      this.last = node2;
    } else {
      node1.prev = node2;
      paramNode.next = null;
    } 
    paramNode.item = null;
    this.size--;
    this.modCount++;
    return (E)object;
  }
  
  public E getFirst() {
    Node node = this.first;
    if (node == null)
      throw new NoSuchElementException(); 
    return (E)node.item;
  }
  
  public E getLast() {
    Node node = this.last;
    if (node == null)
      throw new NoSuchElementException(); 
    return (E)node.item;
  }
  
  public E removeFirst() {
    Node node = this.first;
    if (node == null)
      throw new NoSuchElementException(); 
    return (E)unlinkFirst(node);
  }
  
  public E removeLast() {
    Node node = this.last;
    if (node == null)
      throw new NoSuchElementException(); 
    return (E)unlinkLast(node);
  }
  
  public void addFirst(E paramE) { linkFirst(paramE); }
  
  public void addLast(E paramE) { linkLast(paramE); }
  
  public boolean contains(Object paramObject) { return (indexOf(paramObject) != -1); }
  
  public int size() { return this.size; }
  
  public boolean add(E paramE) {
    linkLast(paramE);
    return true;
  }
  
  public boolean remove(Object paramObject) {
    if (paramObject == null) {
      for (Node node = this.first; node != null; node = node.next) {
        if (node.item == null) {
          unlink(node);
          return true;
        } 
      } 
    } else {
      for (Node node = this.first; node != null; node = node.next) {
        if (paramObject.equals(node.item)) {
          unlink(node);
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public boolean addAll(Collection<? extends E> paramCollection) { return addAll(this.size, paramCollection); }
  
  public boolean addAll(int paramInt, Collection<? extends E> paramCollection) {
    Node node2;
    Node node1;
    checkPositionIndex(paramInt);
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    if (i == 0)
      return false; 
    if (paramInt == this.size) {
      node2 = null;
      node1 = this.last;
    } else {
      node2 = node(paramInt);
      node1 = node2.prev;
    } 
    for (Object object1 : arrayOfObject) {
      Object object2 = object1;
      Node node = new Node(node1, object2, null);
      if (node1 == null) {
        this.first = node;
      } else {
        node1.next = node;
      } 
      node1 = node;
    } 
    if (node2 == null) {
      this.last = node1;
    } else {
      node1.next = node2;
      node2.prev = node1;
    } 
    this.size += i;
    this.modCount++;
    return true;
  }
  
  public void clear() {
    for (Node node = this.first; node != null; node = node1) {
      Node node1 = node.next;
      node.item = null;
      node.next = null;
      node.prev = null;
    } 
    this.first = this.last = null;
    this.size = 0;
    this.modCount++;
  }
  
  public E get(int paramInt) {
    checkElementIndex(paramInt);
    return (E)(node(paramInt)).item;
  }
  
  public E set(int paramInt, E paramE) {
    checkElementIndex(paramInt);
    Node node = node(paramInt);
    Object object = node.item;
    node.item = paramE;
    return (E)object;
  }
  
  public void add(int paramInt, E paramE) {
    checkPositionIndex(paramInt);
    if (paramInt == this.size) {
      linkLast(paramE);
    } else {
      linkBefore(paramE, node(paramInt));
    } 
  }
  
  public E remove(int paramInt) {
    checkElementIndex(paramInt);
    return (E)unlink(node(paramInt));
  }
  
  private boolean isElementIndex(int paramInt) { return (paramInt >= 0 && paramInt < this.size); }
  
  private boolean isPositionIndex(int paramInt) { return (paramInt >= 0 && paramInt <= this.size); }
  
  private String outOfBoundsMsg(int paramInt) { return "Index: " + paramInt + ", Size: " + this.size; }
  
  private void checkElementIndex(int paramInt) {
    if (!isElementIndex(paramInt))
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt)); 
  }
  
  private void checkPositionIndex(int paramInt) {
    if (!isPositionIndex(paramInt))
      throw new IndexOutOfBoundsException(outOfBoundsMsg(paramInt)); 
  }
  
  Node<E> node(int paramInt) {
    if (paramInt < this.size >> 1) {
      Node node1 = this.first;
      for (byte b = 0; b < paramInt; b++)
        node1 = node1.next; 
      return node1;
    } 
    Node node = this.last;
    for (int i = this.size - 1; i > paramInt; i--)
      node = node.prev; 
    return node;
  }
  
  public int indexOf(Object paramObject) {
    byte b = 0;
    if (paramObject == null) {
      for (Node node = this.first; node != null; node = node.next) {
        if (node.item == null)
          return b; 
        b++;
      } 
    } else {
      for (Node node = this.first; node != null; node = node.next) {
        if (paramObject.equals(node.item))
          return b; 
        b++;
      } 
    } 
    return -1;
  }
  
  public int lastIndexOf(Object paramObject) {
    int i = this.size;
    if (paramObject == null) {
      for (Node node = this.last; node != null; node = node.prev) {
        i--;
        if (node.item == null)
          return i; 
      } 
    } else {
      for (Node node = this.last; node != null; node = node.prev) {
        i--;
        if (paramObject.equals(node.item))
          return i; 
      } 
    } 
    return -1;
  }
  
  public E peek() {
    Node node = this.first;
    return (E)((node == null) ? null : node.item);
  }
  
  public E element() { return (E)getFirst(); }
  
  public E poll() {
    Node node = this.first;
    return (E)((node == null) ? null : unlinkFirst(node));
  }
  
  public E remove() { return (E)removeFirst(); }
  
  public boolean offer(E paramE) { return add(paramE); }
  
  public boolean offerFirst(E paramE) {
    addFirst(paramE);
    return true;
  }
  
  public boolean offerLast(E paramE) {
    addLast(paramE);
    return true;
  }
  
  public E peekFirst() {
    Node node = this.first;
    return (E)((node == null) ? null : node.item);
  }
  
  public E peekLast() {
    Node node = this.last;
    return (E)((node == null) ? null : node.item);
  }
  
  public E pollFirst() {
    Node node = this.first;
    return (E)((node == null) ? null : unlinkFirst(node));
  }
  
  public E pollLast() {
    Node node = this.last;
    return (E)((node == null) ? null : unlinkLast(node));
  }
  
  public void push(E paramE) { addFirst(paramE); }
  
  public E pop() { return (E)removeFirst(); }
  
  public boolean removeFirstOccurrence(Object paramObject) { return remove(paramObject); }
  
  public boolean removeLastOccurrence(Object paramObject) {
    if (paramObject == null) {
      for (Node node = this.last; node != null; node = node.prev) {
        if (node.item == null) {
          unlink(node);
          return true;
        } 
      } 
    } else {
      for (Node node = this.last; node != null; node = node.prev) {
        if (paramObject.equals(node.item)) {
          unlink(node);
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public ListIterator<E> listIterator(int paramInt) {
    checkPositionIndex(paramInt);
    return new ListItr(paramInt);
  }
  
  public Iterator<E> descendingIterator() { return new DescendingIterator(null); }
  
  private LinkedList<E> superClone() {
    try {
      return (LinkedList)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public Object clone() {
    LinkedList linkedList = superClone();
    linkedList.first = linkedList.last = null;
    linkedList.size = 0;
    linkedList.modCount = 0;
    for (Node node = this.first; node != null; node = node.next)
      linkedList.add(node.item); 
    return linkedList;
  }
  
  public Object[] toArray() {
    Object[] arrayOfObject = new Object[this.size];
    byte b = 0;
    for (Node node = this.first; node != null; node = node.next)
      arrayOfObject[b++] = node.item; 
    return arrayOfObject;
  }
  
  public <T> T[] toArray(T[] paramArrayOfT) {
    if (paramArrayOfT.length < this.size)
      paramArrayOfT = (T[])(Object[])Array.newInstance(paramArrayOfT.getClass().getComponentType(), this.size); 
    byte b = 0;
    T[] arrayOfT = paramArrayOfT;
    for (Node node = this.first; node != null; node = node.next)
      arrayOfT[b++] = node.item; 
    if (paramArrayOfT.length > this.size)
      paramArrayOfT[this.size] = null; 
    return paramArrayOfT;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.size);
    for (Node node = this.first; node != null; node = node.next)
      paramObjectOutputStream.writeObject(node.item); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    for (byte b = 0; b < i; b++)
      linkLast(paramObjectInputStream.readObject()); 
  }
  
  public Spliterator<E> spliterator() { return new LLSpliterator(this, -1, 0); }
  
  private class DescendingIterator extends Object implements Iterator<E> {
    private final LinkedList<E>.ListItr itr = new LinkedList.ListItr(LinkedList.this, LinkedList.this.size());
    
    private DescendingIterator() {}
    
    public boolean hasNext() { return this.itr.hasPrevious(); }
    
    public E next() { return (E)this.itr.previous(); }
    
    public void remove() { this.itr.remove(); }
  }
  
  static final class LLSpliterator<E> extends Object implements Spliterator<E> {
    static final int BATCH_UNIT = 1024;
    
    static final int MAX_BATCH = 33554432;
    
    final LinkedList<E> list;
    
    LinkedList.Node<E> current;
    
    int est;
    
    int expectedModCount;
    
    int batch;
    
    LLSpliterator(LinkedList<E> param1LinkedList, int param1Int1, int param1Int2) {
      this.list = param1LinkedList;
      this.est = param1Int1;
      this.expectedModCount = param1Int2;
    }
    
    final int getEst() {
      int i;
      if ((i = this.est) < 0) {
        LinkedList linkedList;
        if ((linkedList = this.list) == null) {
          i = this.est = 0;
        } else {
          this.expectedModCount = linkedList.modCount;
          this.current = linkedList.first;
          i = this.est = linkedList.size;
        } 
      } 
      return i;
    }
    
    public long estimateSize() { return getEst(); }
    
    public Spliterator<E> trySplit() {
      int i = getEst();
      LinkedList.Node node;
      if (i > 1 && (node = this.current) != null) {
        int j = this.batch + 1024;
        if (j > i)
          j = i; 
        if (j > 33554432)
          j = 33554432; 
        Object[] arrayOfObject = new Object[j];
        int k = 0;
        do {
          arrayOfObject[k++] = node.item;
        } while ((node = node.next) != null && k < j);
        this.current = node;
        this.batch = k;
        this.est = i - k;
        return Spliterators.spliterator(arrayOfObject, 0, k, 16);
      } 
      return null;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      LinkedList.Node node;
      int i;
      if ((i = getEst()) > 0 && (node = this.current) != null) {
        this.current = null;
        this.est = 0;
        do {
          Object object = node.item;
          node = node.next;
          param1Consumer.accept(object);
        } while (node != null && --i > 0);
      } 
      if (this.list.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
    
    public boolean tryAdvance(Consumer<? super E> param1Consumer) {
      if (param1Consumer == null)
        throw new NullPointerException(); 
      LinkedList.Node node;
      if (getEst() > 0 && (node = this.current) != null) {
        this.est--;
        Object object = node.item;
        this.current = node.next;
        param1Consumer.accept(object);
        if (this.list.modCount != this.expectedModCount)
          throw new ConcurrentModificationException(); 
        return true;
      } 
      return false;
    }
    
    public int characteristics() { return 16464; }
  }
  
  private class ListItr extends Object implements ListIterator<E> {
    private LinkedList.Node<E> lastReturned;
    
    private LinkedList.Node<E> next;
    
    private int nextIndex;
    
    private int expectedModCount = LinkedList.this.modCount;
    
    ListItr(int param1Int) {
      this.next = (param1Int == LinkedList.this.size) ? null : this$0.node(param1Int);
      this.nextIndex = param1Int;
    }
    
    public boolean hasNext() { return (this.nextIndex < LinkedList.this.size); }
    
    public E next() {
      checkForComodification();
      if (!hasNext())
        throw new NoSuchElementException(); 
      this.lastReturned = this.next;
      this.next = this.next.next;
      this.nextIndex++;
      return (E)this.lastReturned.item;
    }
    
    public boolean hasPrevious() { return (this.nextIndex > 0); }
    
    public E previous() {
      checkForComodification();
      if (!hasPrevious())
        throw new NoSuchElementException(); 
      this.lastReturned = this.next = (this.next == null) ? LinkedList.this.last : this.next.prev;
      this.nextIndex--;
      return (E)this.lastReturned.item;
    }
    
    public int nextIndex() { return this.nextIndex; }
    
    public int previousIndex() { return this.nextIndex - 1; }
    
    public void remove() {
      checkForComodification();
      if (this.lastReturned == null)
        throw new IllegalStateException(); 
      LinkedList.Node node = this.lastReturned.next;
      LinkedList.this.unlink(this.lastReturned);
      if (this.next == this.lastReturned) {
        this.next = node;
      } else {
        this.nextIndex--;
      } 
      this.lastReturned = null;
      this.expectedModCount++;
    }
    
    public void set(E param1E) {
      if (this.lastReturned == null)
        throw new IllegalStateException(); 
      checkForComodification();
      this.lastReturned.item = param1E;
    }
    
    public void add(E param1E) {
      checkForComodification();
      this.lastReturned = null;
      if (this.next == null) {
        LinkedList.this.linkLast(param1E);
      } else {
        LinkedList.this.linkBefore(param1E, this.next);
      } 
      this.nextIndex++;
      this.expectedModCount++;
    }
    
    public void forEachRemaining(Consumer<? super E> param1Consumer) {
      Objects.requireNonNull(param1Consumer);
      while (LinkedList.this.modCount == this.expectedModCount && this.nextIndex < LinkedList.this.size) {
        param1Consumer.accept(this.next.item);
        this.lastReturned = this.next;
        this.next = this.next.next;
        this.nextIndex++;
      } 
      checkForComodification();
    }
    
    final void checkForComodification() {
      if (LinkedList.this.modCount != this.expectedModCount)
        throw new ConcurrentModificationException(); 
    }
  }
  
  private static class Node<E> extends Object {
    E item;
    
    Node<E> next;
    
    Node<E> prev;
    
    Node(Node<E> param1Node1, E param1E, Node<E> param1Node2) {
      this.item = param1E;
      this.next = param1Node2;
      this.prev = param1Node1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\LinkedList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */