package com.sun.xml.internal.bind.v2.runtime.property;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;

public final class UnmarshallerChain {
  private int offset = 0;
  
  public final JAXBContextImpl context;
  
  public UnmarshallerChain(JAXBContextImpl paramJAXBContextImpl) { this.context = paramJAXBContextImpl; }
  
  public int allocateOffset() { return this.offset++; }
  
  public int getScopeSize() { return this.offset; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\property\UnmarshallerChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */