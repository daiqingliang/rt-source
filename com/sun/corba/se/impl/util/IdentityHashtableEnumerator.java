package com.sun.corba.se.impl.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

class IdentityHashtableEnumerator implements Enumeration {
  boolean keys;
  
  int index;
  
  IdentityHashtableEntry[] table;
  
  IdentityHashtableEntry entry;
  
  IdentityHashtableEnumerator(IdentityHashtableEntry[] paramArrayOfIdentityHashtableEntry, boolean paramBoolean) {
    this.table = paramArrayOfIdentityHashtableEntry;
    this.keys = paramBoolean;
    this.index = paramArrayOfIdentityHashtableEntry.length;
  }
  
  public boolean hasMoreElements() {
    if (this.entry != null)
      return true; 
    while (this.index-- > 0) {
      if ((this.entry = this.table[this.index]) != null)
        return true; 
    } 
    return false;
  }
  
  public Object nextElement() {
    if (this.entry == null)
      while (this.index-- > 0 && (this.entry = this.table[this.index]) == null); 
    if (this.entry != null) {
      IdentityHashtableEntry identityHashtableEntry = this.entry;
      this.entry = identityHashtableEntry.next;
      return this.keys ? identityHashtableEntry.key : identityHashtableEntry.value;
    } 
    throw new NoSuchElementException("IdentityHashtableEnumerator");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\imp\\util\IdentityHashtableEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */