package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import com.sun.xml.internal.ws.wsdl.SDDocumentResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ServiceDefinitionImpl implements ServiceDefinition, SDDocumentResolver {
  private final List<SDDocumentImpl> docs;
  
  private final Map<String, SDDocumentImpl> bySystemId;
  
  @NotNull
  private final SDDocumentImpl primaryWsdl;
  
  WSEndpointImpl<?> owner;
  
  final List<SDDocumentFilter> filters = new ArrayList();
  
  public ServiceDefinitionImpl(List<SDDocumentImpl> paramList, @NotNull SDDocumentImpl paramSDDocumentImpl) {
    assert paramList.contains(paramSDDocumentImpl);
    this.docs = paramList;
    this.primaryWsdl = paramSDDocumentImpl;
    this.bySystemId = new HashMap(paramList.size());
    for (SDDocumentImpl sDDocumentImpl : paramList) {
      this.bySystemId.put(sDDocumentImpl.getURL().toExternalForm(), sDDocumentImpl);
      sDDocumentImpl.setFilters(this.filters);
      sDDocumentImpl.setResolver(this);
    } 
  }
  
  void setOwner(WSEndpointImpl<?> paramWSEndpointImpl) {
    assert paramWSEndpointImpl != null && this.owner == null;
    this.owner = paramWSEndpointImpl;
  }
  
  @NotNull
  public SDDocument getPrimary() { return this.primaryWsdl; }
  
  public void addFilter(SDDocumentFilter paramSDDocumentFilter) { this.filters.add(paramSDDocumentFilter); }
  
  public Iterator<SDDocument> iterator() { return this.docs.iterator(); }
  
  public SDDocument resolve(String paramString) { return (SDDocument)this.bySystemId.get(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\server\ServiceDefinitionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */