package com.sun.xml.internal.stream.buffer;

final class FragmentedArray<T> extends Object {
  private T _item;
  
  private FragmentedArray<T> _next;
  
  private FragmentedArray<T> _previous;
  
  FragmentedArray(T paramT) { this(paramT, null); }
  
  FragmentedArray(T paramT, FragmentedArray<T> paramFragmentedArray) {
    setArray(paramT);
    if (paramFragmentedArray != null) {
      paramFragmentedArray._next = this;
      this._previous = paramFragmentedArray;
    } 
  }
  
  T getArray() { return (T)this._item; }
  
  void setArray(T paramT) {
    assert paramT.getClass().isArray();
    this._item = paramT;
  }
  
  FragmentedArray<T> getNext() { return this._next; }
  
  void setNext(FragmentedArray<T> paramFragmentedArray) {
    this._next = paramFragmentedArray;
    if (paramFragmentedArray != null)
      paramFragmentedArray._previous = this; 
  }
  
  FragmentedArray<T> getPrevious() { return this._previous; }
  
  void setPrevious(FragmentedArray<T> paramFragmentedArray) {
    this._previous = paramFragmentedArray;
    if (paramFragmentedArray != null)
      paramFragmentedArray._next = this; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\FragmentedArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */