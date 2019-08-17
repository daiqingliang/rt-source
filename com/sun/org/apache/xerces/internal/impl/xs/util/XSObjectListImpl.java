package com.sun.org.apache.xerces.internal.impl.xs.util;

import com.sun.org.apache.xerces.internal.xs.XSObject;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class XSObjectListImpl extends AbstractList implements XSObjectList {
  public static final XSObjectListImpl EMPTY_LIST = new XSObjectListImpl(new XSObject[0], 0);
  
  private static final ListIterator EMPTY_ITERATOR = new ListIterator() {
      public boolean hasNext() { return false; }
      
      public Object next() { throw new NoSuchElementException(); }
      
      public boolean hasPrevious() { return false; }
      
      public Object previous() { throw new NoSuchElementException(); }
      
      public int nextIndex() { return 0; }
      
      public int previousIndex() { return -1; }
      
      public void remove() { throw new UnsupportedOperationException(); }
      
      public void set(Object param1Object) { throw new UnsupportedOperationException(); }
      
      public void add(Object param1Object) { throw new UnsupportedOperationException(); }
    };
  
  private static final int DEFAULT_SIZE = 4;
  
  private XSObject[] fArray = null;
  
  private int fLength = 0;
  
  public XSObjectListImpl() {
    this.fArray = new XSObject[4];
    this.fLength = 0;
  }
  
  public XSObjectListImpl(XSObject[] paramArrayOfXSObject, int paramInt) {
    this.fArray = paramArrayOfXSObject;
    this.fLength = paramInt;
  }
  
  public int getLength() { return this.fLength; }
  
  public XSObject item(int paramInt) { return (paramInt < 0 || paramInt >= this.fLength) ? null : this.fArray[paramInt]; }
  
  public void clearXSObjectList() {
    for (byte b = 0; b < this.fLength; b++)
      this.fArray[b] = null; 
    this.fArray = null;
    this.fLength = 0;
  }
  
  public void addXSObject(XSObject paramXSObject) {
    if (this.fLength == this.fArray.length) {
      XSObject[] arrayOfXSObject = new XSObject[this.fLength + 4];
      System.arraycopy(this.fArray, 0, arrayOfXSObject, 0, this.fLength);
      this.fArray = arrayOfXSObject;
    } 
    this.fArray[this.fLength++] = paramXSObject;
  }
  
  public void addXSObject(int paramInt, XSObject paramXSObject) { this.fArray[paramInt] = paramXSObject; }
  
  public boolean contains(Object paramObject) { return (paramObject == null) ? containsNull() : containsObject(paramObject); }
  
  public Object get(int paramInt) {
    if (paramInt >= 0 && paramInt < this.fLength)
      return this.fArray[paramInt]; 
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  public int size() { return getLength(); }
  
  public Iterator iterator() { return listIterator0(0); }
  
  public ListIterator listIterator() { return listIterator0(0); }
  
  public ListIterator listIterator(int paramInt) {
    if (paramInt >= 0 && paramInt < this.fLength)
      return listIterator0(paramInt); 
    throw new IndexOutOfBoundsException("Index: " + paramInt);
  }
  
  private ListIterator listIterator0(int paramInt) { return (this.fLength == 0) ? EMPTY_ITERATOR : new XSObjectListIterator(paramInt); }
  
  private boolean containsObject(Object paramObject) {
    for (int i = this.fLength - 1; i >= 0; i--) {
      if (paramObject.equals(this.fArray[i]))
        return true; 
    } 
    return false;
  }
  
  private boolean containsNull() {
    for (int i = this.fLength - 1; i >= 0; i--) {
      if (this.fArray[i] == null)
        return true; 
    } 
    return false;
  }
  
  public Object[] toArray() {
    Object[] arrayOfObject = new Object[this.fLength];
    toArray0(arrayOfObject);
    return arrayOfObject;
  }
  
  public Object[] toArray(Object[] paramArrayOfObject) {
    if (paramArrayOfObject.length < this.fLength) {
      Class clazz1 = paramArrayOfObject.getClass();
      Class clazz2 = clazz1.getComponentType();
      paramArrayOfObject = (Object[])Array.newInstance(clazz2, this.fLength);
    } 
    toArray0(paramArrayOfObject);
    if (paramArrayOfObject.length > this.fLength)
      paramArrayOfObject[this.fLength] = null; 
    return paramArrayOfObject;
  }
  
  private void toArray0(Object[] paramArrayOfObject) {
    if (this.fLength > 0)
      System.arraycopy(this.fArray, 0, paramArrayOfObject, 0, this.fLength); 
  }
  
  private final class XSObjectListIterator implements ListIterator {
    private int index;
    
    public XSObjectListIterator(int param1Int) { this.index = param1Int; }
    
    public boolean hasNext() { return (this.index < XSObjectListImpl.this.fLength); }
    
    public Object next() {
      if (this.index < XSObjectListImpl.this.fLength)
        return XSObjectListImpl.this.fArray[this.index++]; 
      throw new NoSuchElementException();
    }
    
    public boolean hasPrevious() { return (this.index > 0); }
    
    public Object previous() {
      if (this.index > 0)
        return XSObjectListImpl.this.fArray[--this.index]; 
      throw new NoSuchElementException();
    }
    
    public int nextIndex() { return this.index; }
    
    public int previousIndex() { return this.index - 1; }
    
    public void remove() { throw new UnsupportedOperationException(); }
    
    public void set(Object param1Object) { throw new UnsupportedOperationException(); }
    
    public void add(Object param1Object) { throw new UnsupportedOperationException(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\x\\util\XSObjectListImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */