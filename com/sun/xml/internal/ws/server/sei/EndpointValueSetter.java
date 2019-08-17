package com.sun.xml.internal.ws.server.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.xml.ws.Holder;

public abstract class EndpointValueSetter {
  private static final EndpointValueSetter[] POOL = new EndpointValueSetter[16];
  
  private EndpointValueSetter() {}
  
  abstract void put(Object paramObject, Object[] paramArrayOfObject);
  
  public static EndpointValueSetter get(ParameterImpl paramParameterImpl) {
    int i = paramParameterImpl.getIndex();
    return paramParameterImpl.isIN() ? ((i < POOL.length) ? POOL[i] : new Param(i)) : new HolderParam(i);
  }
  
  static  {
    for (byte b = 0; b < POOL.length; b++)
      POOL[b] = new Param(b); 
  }
  
  static final class HolderParam extends Param {
    public HolderParam(int param1Int) { super(param1Int); }
    
    void put(Object param1Object, Object[] param1ArrayOfObject) {
      Holder holder = new Holder();
      if (param1Object != null)
        holder.value = param1Object; 
      param1ArrayOfObject[this.idx] = holder;
    }
  }
  
  static class Param extends EndpointValueSetter {
    protected final int idx;
    
    public Param(int param1Int) {
      super(null);
      this.idx = param1Int;
    }
    
    void put(Object param1Object, Object[] param1ArrayOfObject) {
      if (param1Object != null)
        param1ArrayOfObject[this.idx] = param1Object; 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\sei\EndpointValueSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */