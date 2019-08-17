package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;

public class DuplicateAttributeVerifier {
  public static final int MAP_SIZE = 256;
  
  public int _currentIteration;
  
  private Entry[] _map;
  
  public final Entry _poolHead;
  
  public Entry _poolCurrent;
  
  private Entry _poolTail = this._poolHead = new Entry();
  
  public final void clear() {
    this._currentIteration = 0;
    for (Entry entry = this._poolHead; entry != null; entry = entry.poolNext)
      entry.iteration = 0; 
    reset();
  }
  
  public final void reset() {
    this._poolCurrent = this._poolHead;
    if (this._map == null)
      this._map = new Entry[256]; 
  }
  
  private final void increasePool(int paramInt) {
    if (this._map == null) {
      this._map = new Entry[256];
      this._poolCurrent = this._poolHead;
    } else {
      Entry entry = this._poolTail;
      for (byte b = 0; b < paramInt; b++) {
        Entry entry1;
        this._poolTail.poolNext = entry1;
        this._poolTail = entry1;
      } 
      this._poolCurrent = entry.poolNext;
    } 
  }
  
  public final void checkForDuplicateAttribute(int paramInt1, int paramInt2) throws FastInfosetException {
    if (this._poolCurrent == null)
      increasePool(16); 
    Entry entry1;
    this._poolCurrent = this._poolCurrent.poolNext;
    Entry entry2;
    if (entry2 == null || entry2.iteration < this._currentIteration) {
      entry1.hashNext = null;
      entry1.iteration = this._currentIteration;
      entry1.value = paramInt2;
    } else {
      Entry entry = entry2;
      do {
        if (entry.value == paramInt2) {
          reset();
          throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.duplicateAttribute"));
        } 
      } while ((entry = entry.hashNext) != null);
      entry1.hashNext = entry2;
      entry1.iteration = this._currentIteration;
      entry1.value = paramInt2;
    } 
  }
  
  public static class Entry {
    private int iteration;
    
    private int value;
    
    private Entry hashNext;
    
    private Entry poolNext;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\DuplicateAttributeVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */