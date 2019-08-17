package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public abstract class KeyIntMap {
  public static final int NOT_PRESENT = -1;
  
  static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  static final int MAXIMUM_CAPACITY = 1048576;
  
  static final float DEFAULT_LOAD_FACTOR = 0.75F;
  
  int _readOnlyMapSize;
  
  int _size;
  
  int _capacity;
  
  int _threshold;
  
  final float _loadFactor;
  
  public KeyIntMap(int paramInt, float paramFloat) {
    if (paramInt < 0)
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalInitialCapacity", new Object[] { Integer.valueOf(paramInt) })); 
    if (paramInt > 1048576)
      paramInt = 1048576; 
    if (paramFloat <= 0.0F || Float.isNaN(paramFloat))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalLoadFactor", new Object[] { Float.valueOf(paramFloat) })); 
    if (paramInt != 16) {
      this._capacity = 1;
      while (this._capacity < paramInt)
        this._capacity <<= 1; 
      this._loadFactor = paramFloat;
      this._threshold = (int)(this._capacity * this._loadFactor);
    } else {
      this._capacity = 16;
      this._loadFactor = 0.75F;
      this._threshold = 12;
    } 
  }
  
  public KeyIntMap(int paramInt) { this(paramInt, 0.75F); }
  
  public KeyIntMap() {
    this._capacity = 16;
    this._loadFactor = 0.75F;
    this._threshold = 12;
  }
  
  public final int size() { return this._size + this._readOnlyMapSize; }
  
  public abstract void clear();
  
  public abstract void setReadOnlyMap(KeyIntMap paramKeyIntMap, boolean paramBoolean);
  
  public static final int hashHash(int paramInt) {
    paramInt += (paramInt << 9 ^ 0xFFFFFFFF);
    paramInt ^= paramInt >>> 14;
    paramInt += (paramInt << 4);
    return paramInt >>> 10;
  }
  
  public static final int indexFor(int paramInt1, int paramInt2) { return paramInt1 & paramInt2 - 1; }
  
  static class BaseEntry {
    final int _hash;
    
    final int _value;
    
    public BaseEntry(int param1Int1, int param1Int2) {
      this._hash = param1Int1;
      this._value = param1Int2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\KeyIntMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */