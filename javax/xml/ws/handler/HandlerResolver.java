package javax.xml.ws.handler;

import java.util.List;

public interface HandlerResolver {
  List<Handler> getHandlerChain(PortInfo paramPortInfo);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\handler\HandlerResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */