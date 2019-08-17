package com.sun.xml.internal.bind.v2.util;

import java.util.AbstractList;
import java.util.Arrays;

public final class CollisionCheckStack<E> extends AbstractList<E> {
  private Object[] data = new Object[16];
  
  private int[] next = new int[16];
  
  private int size = 0;
  
  private boolean latestPushResult = false;
  
  private boolean useIdentity = true;
  
  private final int[] initialHash = new int[17];
  
  public void setUseIdentity(boolean paramBoolean) { this.useIdentity = paramBoolean; }
  
  public boolean getUseIdentity() { return this.useIdentity; }
  
  public boolean getLatestPushResult() { return this.latestPushResult; }
  
  public boolean push(E paramE) {
    if (this.data.length == this.size)
      expandCapacity(); 
    this.data[this.size] = paramE;
    int i = hash(paramE);
    boolean bool = findDuplicate(paramE, i);
    this.next[this.size] = this.initialHash[i];
    this.initialHash[i] = this.size + 1;
    this.size++;
    this.latestPushResult = bool;
    return this.latestPushResult;
  }
  
  public void pushNocheck(E paramE) {
    if (this.data.length == this.size)
      expandCapacity(); 
    this.data[this.size] = paramE;
    this.next[this.size] = -1;
    this.size++;
  }
  
  public boolean findDuplicate(E paramE) {
    int i = hash(paramE);
    return findDuplicate(paramE, i);
  }
  
  public E get(int paramInt) { return (E)this.data[paramInt]; }
  
  public int size() { return this.size; }
  
  private int hash(Object paramObject) { return ((this.useIdentity ? System.identityHashCode(paramObject) : paramObject.hashCode()) & 0x7FFFFFFF) % this.initialHash.length; }
  
  public E pop() {
    this.size--;
    Object object = this.data[this.size];
    this.data[this.size] = null;
    int i = this.next[this.size];
    if (i >= 0) {
      int j = hash(object);
      assert this.initialHash[j] == this.size + 1;
      this.initialHash[j] = i;
    } 
    return (E)object;
  }
  
  public E peek() { return (E)this.data[this.size - 1]; }
  
  private boolean findDuplicate(E paramE, int paramInt) {
    for (int i = this.initialHash[paramInt]; i != 0; i = this.next[i]) {
      Object object = this.data[--i];
      if (this.useIdentity) {
        if (object == paramE)
          return true; 
      } else if (paramE.equals(object)) {
        return true;
      } 
    } 
    return false;
  }
  
  private void expandCapacity() {
    int i = this.data.length;
    int j = i * 2;
    Object[] arrayOfObject = new Object[j];
    int[] arrayOfInt = new int[j];
    System.arraycopy(this.data, 0, arrayOfObject, 0, i);
    System.arraycopy(this.next, 0, arrayOfInt, 0, i);
    this.data = arrayOfObject;
    this.next = arrayOfInt;
  }
  
  public void reset() {
    if (this.size > 0) {
      this.size = 0;
      Arrays.fill(this.initialHash, 0);
    } 
  }
  
  public String getCycleString() {
    Object object2;
    StringBuilder stringBuilder = new StringBuilder();
    int i = size() - 1;
    Object object1 = get(i);
    stringBuilder.append(object1);
    do {
      stringBuilder.append(" -> ");
      object2 = get(--i);
      stringBuilder.append(object2);
    } while (object1 != object2);
    return stringBuilder.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v\\util\CollisionCheckStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */