package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import java.io.Serializable;
import java.util.ArrayList;

public final class IteratorPool implements Serializable {
  static final long serialVersionUID = -460927331149566998L;
  
  private final DTMIterator m_orig;
  
  private final ArrayList m_freeStack;
  
  public IteratorPool(DTMIterator paramDTMIterator) {
    this.m_orig = paramDTMIterator;
    this.m_freeStack = new ArrayList();
  }
  
  public DTMIterator getInstanceOrThrow() throws CloneNotSupportedException { return this.m_freeStack.isEmpty() ? (DTMIterator)this.m_orig.clone() : (DTMIterator)this.m_freeStack.remove(this.m_freeStack.size() - 1); }
  
  public DTMIterator getInstance() throws CloneNotSupportedException {
    if (this.m_freeStack.isEmpty())
      try {
        return (DTMIterator)this.m_orig.clone();
      } catch (Exception exception) {
        throw new WrappedRuntimeException(exception);
      }  
    return (DTMIterator)this.m_freeStack.remove(this.m_freeStack.size() - 1);
  }
  
  public void freeInstance(DTMIterator paramDTMIterator) { this.m_freeStack.add(paramDTMIterator); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\axes\IteratorPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */