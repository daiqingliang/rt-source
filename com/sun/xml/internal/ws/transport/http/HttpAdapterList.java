package com.sun.xml.internal.ws.transport.http;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;

public abstract class HttpAdapterList<T extends HttpAdapter> extends AbstractList<T> implements DeploymentDescriptorParser.AdapterFactory<T> {
  private final List<T> adapters = new ArrayList();
  
  private final Map<PortInfo, String> addressMap = new HashMap();
  
  public T createAdapter(String paramString1, String paramString2, WSEndpoint<?> paramWSEndpoint) {
    HttpAdapter httpAdapter = createHttpAdapter(paramString1, paramString2, paramWSEndpoint);
    this.adapters.add(httpAdapter);
    WSDLPort wSDLPort = paramWSEndpoint.getPort();
    if (wSDLPort != null) {
      PortInfo portInfo = new PortInfo(wSDLPort.getOwner().getName(), wSDLPort.getName().getLocalPart(), paramWSEndpoint.getImplementationClass());
      this.addressMap.put(portInfo, getValidPath(paramString2));
    } 
    return (T)httpAdapter;
  }
  
  protected abstract T createHttpAdapter(String paramString1, String paramString2, WSEndpoint<?> paramWSEndpoint);
  
  private String getValidPath(@NotNull String paramString) { return paramString.endsWith("/*") ? paramString.substring(0, paramString.length() - 2) : paramString; }
  
  public PortAddressResolver createPortAddressResolver(final String baseAddress, final Class<?> endpointImpl) { return new PortAddressResolver() {
        public String getAddressFor(@NotNull QName param1QName, @NotNull String param1String) {
          String str = (String)HttpAdapterList.this.addressMap.get(new HttpAdapterList.PortInfo(param1QName, param1String, endpointImpl));
          if (str == null)
            for (Map.Entry entry : HttpAdapterList.this.addressMap.entrySet()) {
              if (param1QName.equals(((HttpAdapterList.PortInfo)entry.getKey()).serviceName) && param1String.equals(((HttpAdapterList.PortInfo)entry.getKey()).portName)) {
                str = (String)entry.getValue();
                break;
              } 
            }  
          return (str == null) ? null : (baseAddress + str);
        }
      }; }
  
  public T get(int paramInt) { return (T)(HttpAdapter)this.adapters.get(paramInt); }
  
  public int size() { return this.adapters.size(); }
  
  private static class PortInfo {
    private final QName serviceName;
    
    private final String portName;
    
    private final Class<?> implClass;
    
    PortInfo(@NotNull QName param1QName, @NotNull String param1String, Class<?> param1Class) {
      this.serviceName = param1QName;
      this.portName = param1String;
      this.implClass = param1Class;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object instanceof PortInfo) {
        PortInfo portInfo = (PortInfo)param1Object;
        return (this.implClass == null) ? ((this.serviceName.equals(portInfo.serviceName) && this.portName.equals(portInfo.portName) && portInfo.implClass == null)) : ((this.serviceName.equals(portInfo.serviceName) && this.portName.equals(portInfo.portName) && this.implClass.equals(portInfo.implClass)));
      } 
      return false;
    }
    
    public int hashCode() {
      int i = this.serviceName.hashCode() + this.portName.hashCode();
      return (this.implClass != null) ? (i + this.implClass.hashCode()) : i;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\transport\http\HttpAdapterList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */