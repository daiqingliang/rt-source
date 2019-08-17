package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.util.BitSet;

public class CoroutineManager {
  BitSet m_activeIDs = new BitSet();
  
  static final int m_unreasonableId = 1024;
  
  Object m_yield = null;
  
  static final int NOBODY = -1;
  
  static final int ANYBODY = -1;
  
  int m_nextCoroutine = -1;
  
  public int co_joinCoroutineSet(int paramInt) {
    if (paramInt >= 0) {
      if (paramInt >= 1024 || this.m_activeIDs.get(paramInt))
        return -1; 
    } else {
      for (paramInt = 0; paramInt < 1024 && this.m_activeIDs.get(paramInt); paramInt++);
      if (paramInt >= 1024)
        return -1; 
    } 
    this.m_activeIDs.set(paramInt);
    return paramInt;
  }
  
  public Object co_entry_pause(int paramInt) throws NoSuchMethodException {
    if (!this.m_activeIDs.get(paramInt))
      throw new NoSuchMethodException(); 
    while (this.m_nextCoroutine != paramInt) {
      try {
        wait();
      } catch (InterruptedException interruptedException) {}
    } 
    return this.m_yield;
  }
  
  public Object co_resume(Object paramObject, int paramInt1, int paramInt2) throws NoSuchMethodException {
    if (!this.m_activeIDs.get(paramInt2))
      throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_NOT_AVAIL", new Object[] { Integer.toString(paramInt2) })); 
    this.m_yield = paramObject;
    this.m_nextCoroutine = paramInt2;
    notify();
    while (this.m_nextCoroutine != paramInt1 || this.m_nextCoroutine == -1 || this.m_nextCoroutine == -1) {
      try {
        wait();
      } catch (InterruptedException interruptedException) {}
    } 
    if (this.m_nextCoroutine == -1) {
      co_exit(paramInt1);
      throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_CO_EXIT", null));
    } 
    return this.m_yield;
  }
  
  public void co_exit(int paramInt) {
    this.m_activeIDs.clear(paramInt);
    this.m_nextCoroutine = -1;
    notify();
  }
  
  public void co_exit_to(Object paramObject, int paramInt1, int paramInt2) throws NoSuchMethodException {
    if (!this.m_activeIDs.get(paramInt2))
      throw new NoSuchMethodException(XMLMessages.createXMLMessage("ER_COROUTINE_NOT_AVAIL", new Object[] { Integer.toString(paramInt2) })); 
    this.m_yield = paramObject;
    this.m_nextCoroutine = paramInt2;
    this.m_activeIDs.clear(paramInt1);
    notify();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\CoroutineManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */