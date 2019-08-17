package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class AugmentationsImpl implements Augmentations {
  private AugmentationsItemsContainer fAugmentationsContainer = new SmallContainer();
  
  public Object putItem(String paramString, Object paramObject) {
    Object object = this.fAugmentationsContainer.putItem(paramString, paramObject);
    if (object == null && this.fAugmentationsContainer.isFull())
      this.fAugmentationsContainer = this.fAugmentationsContainer.expand(); 
    return object;
  }
  
  public Object getItem(String paramString) { return this.fAugmentationsContainer.getItem(paramString); }
  
  public Object removeItem(String paramString) { return this.fAugmentationsContainer.removeItem(paramString); }
  
  public Enumeration keys() { return this.fAugmentationsContainer.keys(); }
  
  public void removeAllItems() { this.fAugmentationsContainer.clear(); }
  
  public String toString() { return this.fAugmentationsContainer.toString(); }
  
  abstract class AugmentationsItemsContainer {
    public abstract Object putItem(Object param1Object1, Object param1Object2);
    
    public abstract Object getItem(Object param1Object);
    
    public abstract Object removeItem(Object param1Object);
    
    public abstract Enumeration keys();
    
    public abstract void clear();
    
    public abstract boolean isFull();
    
    public abstract AugmentationsItemsContainer expand();
  }
  
  class LargeContainer extends AugmentationsItemsContainer {
    final Map<Object, Object> fAugmentations = new HashMap();
    
    LargeContainer() { super(AugmentationsImpl.this); }
    
    public Object getItem(Object param1Object) { return this.fAugmentations.get(param1Object); }
    
    public Object putItem(Object param1Object1, Object param1Object2) { return this.fAugmentations.put(param1Object1, param1Object2); }
    
    public Object removeItem(Object param1Object) { return this.fAugmentations.remove(param1Object); }
    
    public Enumeration keys() { return Collections.enumeration(this.fAugmentations.keySet()); }
    
    public void clear() { this.fAugmentations.clear(); }
    
    public boolean isFull() { return false; }
    
    public AugmentationsImpl.AugmentationsItemsContainer expand() { return this; }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("LargeContainer");
      for (Object object : this.fAugmentations.keySet()) {
        stringBuilder.append("\nkey == ");
        stringBuilder.append(object);
        stringBuilder.append("; value == ");
        stringBuilder.append(this.fAugmentations.get(object));
      } 
      return stringBuilder.toString();
    }
  }
  
  class SmallContainer extends AugmentationsItemsContainer {
    static final int SIZE_LIMIT = 10;
    
    final Object[] fAugmentations = new Object[20];
    
    int fNumEntries = 0;
    
    SmallContainer() { super(AugmentationsImpl.this); }
    
    public Enumeration keys() { return new SmallContainerKeyEnumeration(); }
    
    public Object getItem(Object param1Object) {
      for (byte b = 0; b < this.fNumEntries * 2; b += 2) {
        if (this.fAugmentations[b].equals(param1Object))
          return this.fAugmentations[b + true]; 
      } 
      return null;
    }
    
    public Object putItem(Object param1Object1, Object param1Object2) {
      for (byte b = 0; b < this.fNumEntries * 2; b += 2) {
        if (this.fAugmentations[b].equals(param1Object1)) {
          Object object = this.fAugmentations[b + true];
          this.fAugmentations[b + true] = param1Object2;
          return object;
        } 
      } 
      this.fAugmentations[this.fNumEntries * 2] = param1Object1;
      this.fAugmentations[this.fNumEntries * 2 + 1] = param1Object2;
      this.fNumEntries++;
      return null;
    }
    
    public Object removeItem(Object param1Object) {
      for (byte b = 0; b < this.fNumEntries * 2; b += 2) {
        if (this.fAugmentations[b].equals(param1Object)) {
          Object object = this.fAugmentations[b + true];
          byte b1;
          for (b1 = b; b1 < this.fNumEntries * 2 - 2; b1 += 2) {
            this.fAugmentations[b1] = this.fAugmentations[b1 + 2];
            this.fAugmentations[b1 + true] = this.fAugmentations[b1 + 3];
          } 
          this.fAugmentations[this.fNumEntries * 2 - 2] = null;
          this.fAugmentations[this.fNumEntries * 2 - 1] = null;
          this.fNumEntries--;
          return object;
        } 
      } 
      return null;
    }
    
    public void clear() {
      for (byte b = 0; b < this.fNumEntries * 2; b += 2) {
        this.fAugmentations[b] = null;
        this.fAugmentations[b + true] = null;
      } 
      this.fNumEntries = 0;
    }
    
    public boolean isFull() { return (this.fNumEntries == 10); }
    
    public AugmentationsImpl.AugmentationsItemsContainer expand() {
      AugmentationsImpl.LargeContainer largeContainer = new AugmentationsImpl.LargeContainer(AugmentationsImpl.this);
      for (byte b = 0; b < this.fNumEntries * 2; b += 2)
        largeContainer.putItem(this.fAugmentations[b], this.fAugmentations[b + true]); 
      return largeContainer;
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("SmallContainer - fNumEntries == ").append(this.fNumEntries);
      for (byte b = 0; b < 20; b += 2)
        stringBuilder.append("\nfAugmentations[").append(b).append("] == ").append(this.fAugmentations[b]).append("; fAugmentations[").append(b + 1).append("] == ").append(this.fAugmentations[b + 1]); 
      return stringBuilder.toString();
    }
    
    class SmallContainerKeyEnumeration implements Enumeration {
      Object[] enumArray = new Object[AugmentationsImpl.SmallContainer.this.fNumEntries];
      
      int next = 0;
      
      SmallContainerKeyEnumeration() {
        for (byte b = 0; b < AugmentationsImpl.SmallContainer.this.fNumEntries; b++)
          this.enumArray[b] = AugmentationsImpl.SmallContainer.this.fAugmentations[b * 2]; 
      }
      
      public boolean hasMoreElements() { return (this.next < this.enumArray.length); }
      
      public Object nextElement() {
        if (this.next >= this.enumArray.length)
          throw new NoSuchElementException(); 
        Object object = this.enumArray[this.next];
        this.enumArray[this.next] = null;
        this.next++;
        return object;
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\AugmentationsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */