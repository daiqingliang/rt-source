package com.sun.xml.internal.fastinfoset.util;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;

public class QualifiedNameArray extends ValueArray {
  public QualifiedName[] _array;
  
  private QualifiedNameArray _readOnlyArray;
  
  public QualifiedNameArray(int paramInt1, int paramInt2) {
    this._array = new QualifiedName[paramInt1];
    this._maximumCapacity = paramInt2;
  }
  
  public QualifiedNameArray() { this(10, 2147483647); }
  
  public final void clear() { this._size = this._readOnlyArraySize; }
  
  public final QualifiedName[] getArray() {
    if (this._array == null)
      return null; 
    QualifiedName[] arrayOfQualifiedName = new QualifiedName[this._array.length];
    System.arraycopy(this._array, 0, arrayOfQualifiedName, 0, this._array.length);
    return arrayOfQualifiedName;
  }
  
  public final void setReadOnlyArray(ValueArray paramValueArray, boolean paramBoolean) {
    if (!(paramValueArray instanceof QualifiedNameArray))
      throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[] { paramValueArray })); 
    setReadOnlyArray((QualifiedNameArray)paramValueArray, paramBoolean);
  }
  
  public final void setReadOnlyArray(QualifiedNameArray paramQualifiedNameArray, boolean paramBoolean) {
    if (paramQualifiedNameArray != null) {
      this._readOnlyArray = paramQualifiedNameArray;
      this._readOnlyArraySize = paramQualifiedNameArray.getSize();
      if (paramBoolean)
        clear(); 
      this._array = getCompleteArray();
      this._size = this._readOnlyArraySize;
    } 
  }
  
  public final QualifiedName[] getCompleteArray() {
    if (this._readOnlyArray == null)
      return getArray(); 
    QualifiedName[] arrayOfQualifiedName1 = this._readOnlyArray.getCompleteArray();
    QualifiedName[] arrayOfQualifiedName2 = new QualifiedName[this._readOnlyArraySize + this._array.length];
    System.arraycopy(arrayOfQualifiedName1, 0, arrayOfQualifiedName2, 0, this._readOnlyArraySize);
    return arrayOfQualifiedName2;
  }
  
  public final QualifiedName getNext() { return (this._size == this._array.length) ? null : this._array[this._size]; }
  
  public final void add(QualifiedName paramQualifiedName) {
    if (this._size == this._array.length)
      resize(); 
    this._array[this._size++] = paramQualifiedName;
  }
  
  protected final void resize() {
    if (this._size == this._maximumCapacity)
      throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity")); 
    int i = this._size * 3 / 2 + 1;
    if (i > this._maximumCapacity)
      i = this._maximumCapacity; 
    QualifiedName[] arrayOfQualifiedName = new QualifiedName[i];
    System.arraycopy(this._array, 0, arrayOfQualifiedName, 0, this._size);
    this._array = arrayOfQualifiedName;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfose\\util\QualifiedNameArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */