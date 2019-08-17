package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.config.management.Reconfigurable;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.util.Pool;

public abstract class Adapter<TK extends Adapter.Toolkit> extends Object implements Reconfigurable, Component {
  protected final WSEndpoint<?> endpoint;
  
  protected Adapter(WSEndpoint paramWSEndpoint) {
    assert paramWSEndpoint != null;
    this.endpoint = paramWSEndpoint;
    paramWSEndpoint.getComponents().add(getEndpointComponent());
  }
  
  protected Component getEndpointComponent() { return new Component() {
        public <S> S getSPI(Class<S> param1Class) { return param1Class.isAssignableFrom(Reconfigurable.class) ? (S)param1Class.cast(Adapter.this) : null; }
      }; }
  
  public void reconfigure() { this.pool = new Pool<TK>() {
        protected TK create() { return (TK)Adapter.this.createToolkit(); }
      }; }
  
  public <S> S getSPI(Class<S> paramClass) { return paramClass.isAssignableFrom(Reconfigurable.class) ? (S)paramClass.cast(this) : ((this.endpoint != null) ? (S)this.endpoint.getSPI(paramClass) : null); }
  
  public WSEndpoint<?> getEndpoint() { return this.endpoint; }
  
  protected Pool<TK> getPool() { return this.pool; }
  
  protected abstract TK createToolkit();
  
  public class Toolkit {
    public final Codec codec = Adapter.this.endpoint.createCodec();
    
    public final WSEndpoint.PipeHead head = Adapter.this.endpoint.createPipeHead();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\Adapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */