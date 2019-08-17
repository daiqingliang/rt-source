package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.internal.ws.api.config.management.ManagedEndpointFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.server.EndpointAwareTube;
import com.sun.xml.internal.ws.server.EndpointFactory;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.xml.namespace.QName;
import org.w3c.dom.Element;
import org.xml.sax.EntityResolver;

public abstract class WSEndpoint<T> extends Object implements ComponentRegistry {
  @NotNull
  public abstract Codec createCodec();
  
  @NotNull
  public abstract QName getServiceName();
  
  @NotNull
  public abstract QName getPortName();
  
  @NotNull
  public abstract Class<T> getImplementationClass();
  
  @NotNull
  public abstract WSBinding getBinding();
  
  @NotNull
  public abstract Container getContainer();
  
  @Nullable
  public abstract WSDLPort getPort();
  
  public abstract void setExecutor(@NotNull Executor paramExecutor);
  
  public final void schedule(@NotNull Packet paramPacket, @NotNull CompletionCallback paramCompletionCallback) { schedule(paramPacket, paramCompletionCallback, null); }
  
  public abstract void schedule(@NotNull Packet paramPacket, @NotNull CompletionCallback paramCompletionCallback, @Nullable FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor);
  
  public void process(@NotNull Packet paramPacket, @NotNull CompletionCallback paramCompletionCallback, @Nullable FiberContextSwitchInterceptor paramFiberContextSwitchInterceptor) { schedule(paramPacket, paramCompletionCallback, paramFiberContextSwitchInterceptor); }
  
  public Engine getEngine() { throw new UnsupportedOperationException(); }
  
  @NotNull
  public abstract PipeHead createPipeHead();
  
  public abstract void dispose();
  
  @Nullable
  public abstract ServiceDefinition getServiceDefinition();
  
  public List<BoundEndpoint> getBoundEndpoints() {
    Module module = (Module)getContainer().getSPI(Module.class);
    return (module != null) ? module.getBoundEndpoints() : null;
  }
  
  @NotNull
  public abstract Set<EndpointComponent> getComponentRegistry();
  
  @NotNull
  public Set<Component> getComponents() { return Collections.emptySet(); }
  
  @Nullable
  public <S> S getSPI(@NotNull Class<S> paramClass) {
    Set set = getComponents();
    if (set != null)
      for (Component component : set) {
        Object object = component.getSPI(paramClass);
        if (object != null)
          return (S)object; 
      }  
    return (S)getContainer().getSPI(paramClass);
  }
  
  @Nullable
  public abstract SEIModel getSEIModel();
  
  public abstract PolicyMap getPolicyMap();
  
  @NotNull
  public abstract ManagedObjectManager getManagedObjectManager();
  
  public abstract void closeManagedObjectManager();
  
  @NotNull
  public abstract ServerTubeAssemblerContext getAssemblerContext();
  
  public static <T> WSEndpoint<T> create(@NotNull Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, @Nullable EntityResolver paramEntityResolver, boolean paramBoolean2) { return create(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, true); }
  
  public static <T> WSEndpoint<T> create(@NotNull Class<T> paramClass, boolean paramBoolean1, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, @Nullable EntityResolver paramEntityResolver, boolean paramBoolean2, boolean paramBoolean3) {
    WSEndpoint wSEndpoint = EndpointFactory.createEndpoint(paramClass, paramBoolean1, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, paramBoolean2, paramBoolean3);
    Iterator iterator = ServiceFinder.find(ManagedEndpointFactory.class).iterator();
    if (iterator.hasNext()) {
      ManagedEndpointFactory managedEndpointFactory = (ManagedEndpointFactory)iterator.next();
      EndpointCreationAttributes endpointCreationAttributes = new EndpointCreationAttributes(paramBoolean1, paramInvoker, paramEntityResolver, paramBoolean2);
      WSEndpoint wSEndpoint1 = managedEndpointFactory.createEndpoint(wSEndpoint, endpointCreationAttributes);
      if (wSEndpoint.getAssemblerContext().getTerminalTube() instanceof EndpointAwareTube)
        ((EndpointAwareTube)wSEndpoint.getAssemblerContext().getTerminalTube()).setEndpoint(wSEndpoint1); 
      return wSEndpoint1;
    } 
    return wSEndpoint;
  }
  
  @Deprecated
  public static <T> WSEndpoint<T> create(@NotNull Class<T> paramClass, boolean paramBoolean, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, @Nullable EntityResolver paramEntityResolver) { return create(paramClass, paramBoolean, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, paramEntityResolver, false); }
  
  public static <T> WSEndpoint<T> create(@NotNull Class<T> paramClass, boolean paramBoolean, @Nullable Invoker paramInvoker, @Nullable QName paramQName1, @Nullable QName paramQName2, @Nullable Container paramContainer, @Nullable WSBinding paramWSBinding, @Nullable SDDocumentSource paramSDDocumentSource, @Nullable Collection<? extends SDDocumentSource> paramCollection, @Nullable URL paramURL) { return create(paramClass, paramBoolean, paramInvoker, paramQName1, paramQName2, paramContainer, paramWSBinding, paramSDDocumentSource, paramCollection, XmlUtil.createEntityResolver(paramURL), false); }
  
  @NotNull
  public static QName getDefaultServiceName(Class paramClass) { return getDefaultServiceName(paramClass, true, null); }
  
  @NotNull
  public static QName getDefaultServiceName(Class paramClass, MetadataReader paramMetadataReader) { return getDefaultServiceName(paramClass, true, paramMetadataReader); }
  
  @NotNull
  public static QName getDefaultServiceName(Class paramClass, boolean paramBoolean) { return getDefaultServiceName(paramClass, paramBoolean, null); }
  
  @NotNull
  public static QName getDefaultServiceName(Class paramClass, boolean paramBoolean, MetadataReader paramMetadataReader) { return EndpointFactory.getDefaultServiceName(paramClass, paramBoolean, paramMetadataReader); }
  
  @NotNull
  public static QName getDefaultPortName(@NotNull QName paramQName, Class paramClass) { return getDefaultPortName(paramQName, paramClass, null); }
  
  @NotNull
  public static QName getDefaultPortName(@NotNull QName paramQName, Class paramClass, MetadataReader paramMetadataReader) { return getDefaultPortName(paramQName, paramClass, true, paramMetadataReader); }
  
  @NotNull
  public static QName getDefaultPortName(@NotNull QName paramQName, Class paramClass, boolean paramBoolean) { return getDefaultPortName(paramQName, paramClass, paramBoolean, null); }
  
  @NotNull
  public static QName getDefaultPortName(@NotNull QName paramQName, Class paramClass, boolean paramBoolean, MetadataReader paramMetadataReader) { return EndpointFactory.getDefaultPortName(paramQName, paramClass, paramBoolean, paramMetadataReader); }
  
  public abstract <T extends javax.xml.ws.EndpointReference> T getEndpointReference(Class<T> paramClass, String paramString1, String paramString2, Element... paramVarArgs);
  
  public abstract <T extends javax.xml.ws.EndpointReference> T getEndpointReference(Class<T> paramClass, String paramString1, String paramString2, List<Element> paramList1, List<Element> paramList2);
  
  public boolean equalsProxiedInstance(WSEndpoint paramWSEndpoint) { return (paramWSEndpoint == null) ? false : equals(paramWSEndpoint); }
  
  @Nullable
  public abstract OperationDispatcher getOperationDispatcher();
  
  public abstract Packet createServiceResponseForException(ThrowableContainerPropertySet paramThrowableContainerPropertySet, Packet paramPacket, SOAPVersion paramSOAPVersion, WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding);
  
  public static interface CompletionCallback {
    void onCompletion(@NotNull Packet param1Packet);
  }
  
  public static interface PipeHead {
    @NotNull
    Packet process(@NotNull Packet param1Packet, @Nullable WebServiceContextDelegate param1WebServiceContextDelegate, @Nullable TransportBackChannel param1TransportBackChannel);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\WSEndpoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */