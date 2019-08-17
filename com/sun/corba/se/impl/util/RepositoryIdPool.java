package com.sun.corba.se.impl.util;

import java.util.EmptyStackException;
import java.util.Stack;

class RepositoryIdPool extends Stack {
  private static int MAX_CACHE_SIZE = 4;
  
  private RepositoryIdCache cache;
  
  public final RepositoryId popId() {
    try {
      return (RepositoryId)pop();
    } catch (EmptyStackException emptyStackException) {
      increasePool(5);
      return (RepositoryId)pop();
    } 
  }
  
  final void increasePool(int paramInt) {
    for (int i = paramInt; i > 0; i--)
      push(new RepositoryId()); 
  }
  
  final void setCaches(RepositoryIdCache paramRepositoryIdCache) { this.cache = paramRepositoryIdCache; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\RepositoryIdPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */