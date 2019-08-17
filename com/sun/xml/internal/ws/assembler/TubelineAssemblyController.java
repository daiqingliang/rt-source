package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryConfig;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryList;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import javax.xml.namespace.QName;

final class TubelineAssemblyController {
  private final MetroConfigName metroConfigName;
  
  TubelineAssemblyController(MetroConfigName paramMetroConfigName) { this.metroConfigName = paramMetroConfigName; }
  
  Collection<TubeCreator> getTubeCreators(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext) {
    URI uRI;
    if (paramClientTubelineAssemblyContext.getPortInfo() != null) {
      uRI = createEndpointComponentUri(paramClientTubelineAssemblyContext.getPortInfo().getServiceName(), paramClientTubelineAssemblyContext.getPortInfo().getPortName());
    } else {
      uRI = null;
    } 
    MetroConfigLoader metroConfigLoader = new MetroConfigLoader(paramClientTubelineAssemblyContext.getContainer(), this.metroConfigName);
    return initializeTubeCreators(metroConfigLoader.getClientSideTubeFactories(uRI));
  }
  
  Collection<TubeCreator> getTubeCreators(DefaultServerTubelineAssemblyContext paramDefaultServerTubelineAssemblyContext) {
    URI uRI;
    if (paramDefaultServerTubelineAssemblyContext.getEndpoint() != null) {
      uRI = createEndpointComponentUri(paramDefaultServerTubelineAssemblyContext.getEndpoint().getServiceName(), paramDefaultServerTubelineAssemblyContext.getEndpoint().getPortName());
    } else {
      uRI = null;
    } 
    MetroConfigLoader metroConfigLoader = new MetroConfigLoader(paramDefaultServerTubelineAssemblyContext.getEndpoint().getContainer(), this.metroConfigName);
    return initializeTubeCreators(metroConfigLoader.getEndpointSideTubeFactories(uRI));
  }
  
  private Collection<TubeCreator> initializeTubeCreators(TubeFactoryList paramTubeFactoryList) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    LinkedList linkedList = new LinkedList();
    for (TubeFactoryConfig tubeFactoryConfig : paramTubeFactoryList.getTubeFactoryConfigs())
      linkedList.addFirst(new TubeCreator(tubeFactoryConfig, classLoader)); 
    return linkedList;
  }
  
  private URI createEndpointComponentUri(@NotNull QName paramQName1, @NotNull QName paramQName2) {
    StringBuilder stringBuilder = (new StringBuilder(paramQName1.getNamespaceURI())).append("#wsdl11.port(").append(paramQName1.getLocalPart()).append('/').append(paramQName2.getLocalPart()).append(')');
    try {
      return new URI(stringBuilder.toString());
    } catch (URISyntaxException uRISyntaxException) {
      Logger.getLogger(TubelineAssemblyController.class).warning(TubelineassemblyMessages.MASM_0020_ERROR_CREATING_URI_FROM_GENERATED_STRING(stringBuilder.toString()), uRISyntaxException);
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\TubelineAssemblyController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */