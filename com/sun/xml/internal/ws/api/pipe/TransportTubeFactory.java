package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public abstract class TransportTubeFactory {
  private static final TransportTubeFactory DEFAULT = new DefaultTransportTubeFactory(null);
  
  private static final Logger logger = Logger.getLogger(TransportTubeFactory.class.getName());
  
  public abstract Tube doCreate(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext);
  
  public static Tube create(@Nullable ClassLoader paramClassLoader, @NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext) {
    for (TransportTubeFactory transportTubeFactory : ServiceFinder.find(TransportTubeFactory.class, paramClassLoader, paramClientTubeAssemblerContext.getContainer())) {
      Tube tube = transportTubeFactory.doCreate(paramClientTubeAssemblerContext);
      if (tube != null) {
        if (logger.isLoggable(Level.FINE))
          logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { transportTubeFactory.getClass(), tube }); 
        return tube;
      } 
    } 
    ClientPipeAssemblerContext clientPipeAssemblerContext = new ClientPipeAssemblerContext(paramClientTubeAssemblerContext.getAddress(), paramClientTubeAssemblerContext.getWsdlModel(), paramClientTubeAssemblerContext.getService(), paramClientTubeAssemblerContext.getBinding(), paramClientTubeAssemblerContext.getContainer());
    clientPipeAssemblerContext.setCodec(paramClientTubeAssemblerContext.getCodec());
    for (TransportPipeFactory transportPipeFactory : ServiceFinder.find(TransportPipeFactory.class, paramClassLoader)) {
      Pipe pipe = transportPipeFactory.doCreate(clientPipeAssemblerContext);
      if (pipe != null) {
        if (logger.isLoggable(Level.FINE))
          logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { transportPipeFactory.getClass(), pipe }); 
        return PipeAdapter.adapt(pipe);
      } 
    } 
    return DEFAULT.createDefault(clientPipeAssemblerContext);
  }
  
  protected Tube createDefault(ClientTubeAssemblerContext paramClientTubeAssemblerContext) {
    String str = paramClientTubeAssemblerContext.getAddress().getURI().getScheme();
    if (str != null && (str.equalsIgnoreCase("http") || str.equalsIgnoreCase("https")))
      return createHttpTransport(paramClientTubeAssemblerContext); 
    throw new WebServiceException("Unsupported endpoint address: " + paramClientTubeAssemblerContext.getAddress());
  }
  
  protected Tube createHttpTransport(ClientTubeAssemblerContext paramClientTubeAssemblerContext) { return new HttpTransportPipe(paramClientTubeAssemblerContext.getCodec(), paramClientTubeAssemblerContext.getBinding()); }
  
  private static class DefaultTransportTubeFactory extends TransportTubeFactory {
    private DefaultTransportTubeFactory() {}
    
    public Tube doCreate(ClientTubeAssemblerContext param1ClientTubeAssemblerContext) { return createDefault(param1ClientTubeAssemblerContext); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\TransportTubeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */