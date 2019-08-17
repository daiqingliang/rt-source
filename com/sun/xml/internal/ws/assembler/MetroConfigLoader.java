package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.api.ResourceLoader;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.resources.TubelineassemblyMessages;
import com.sun.xml.internal.ws.runtime.config.MetroConfig;
import com.sun.xml.internal.ws.runtime.config.TubeFactoryList;
import com.sun.xml.internal.ws.runtime.config.TubelineDefinition;
import com.sun.xml.internal.ws.runtime.config.TubelineMapping;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.ws.WebServiceException;

class MetroConfigLoader {
  private static final Logger LOGGER = Logger.getLogger(MetroConfigLoader.class);
  
  private MetroConfigName defaultTubesConfigNames;
  
  private static final TubeFactoryListResolver ENDPOINT_SIDE_RESOLVER = new TubeFactoryListResolver() {
      public TubeFactoryList getFactories(TubelineDefinition param1TubelineDefinition) { return (param1TubelineDefinition != null) ? param1TubelineDefinition.getEndpointSide() : null; }
    };
  
  private static final TubeFactoryListResolver CLIENT_SIDE_RESOLVER = new TubeFactoryListResolver() {
      public TubeFactoryList getFactories(TubelineDefinition param1TubelineDefinition) { return (param1TubelineDefinition != null) ? param1TubelineDefinition.getClientSide() : null; }
    };
  
  private MetroConfig defaultConfig;
  
  private URL defaultConfigUrl;
  
  private MetroConfig appConfig;
  
  private URL appConfigUrl;
  
  MetroConfigLoader(Container paramContainer, MetroConfigName paramMetroConfigName) {
    this.defaultTubesConfigNames = paramMetroConfigName;
    ResourceLoader resourceLoader = null;
    if (paramContainer != null)
      resourceLoader = (ResourceLoader)paramContainer.getSPI(ResourceLoader.class); 
    init(paramContainer, new ResourceLoader[] { resourceLoader, new MetroConfigUrlLoader(paramContainer) });
  }
  
  private void init(Container paramContainer, ResourceLoader... paramVarArgs) {
    String str1 = null;
    String str2 = null;
    if (paramContainer != null) {
      MetroConfigName metroConfigName = (MetroConfigName)paramContainer.getSPI(MetroConfigName.class);
      if (metroConfigName != null) {
        str1 = metroConfigName.getAppFileName();
        str2 = metroConfigName.getDefaultFileName();
      } 
    } 
    if (str1 == null)
      str1 = this.defaultTubesConfigNames.getAppFileName(); 
    if (str2 == null)
      str2 = this.defaultTubesConfigNames.getDefaultFileName(); 
    this.defaultConfigUrl = locateResource(str2, paramVarArgs);
    if (this.defaultConfigUrl == null)
      throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0001_DEFAULT_CFG_FILE_NOT_FOUND(str2))); 
    LOGGER.config(TubelineassemblyMessages.MASM_0002_DEFAULT_CFG_FILE_LOCATED(str2, this.defaultConfigUrl));
    this.defaultConfig = loadMetroConfig(this.defaultConfigUrl);
    if (this.defaultConfig == null)
      throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0003_DEFAULT_CFG_FILE_NOT_LOADED(str2))); 
    if (this.defaultConfig.getTubelines() == null)
      throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0004_NO_TUBELINES_SECTION_IN_DEFAULT_CFG_FILE(str2))); 
    if (this.defaultConfig.getTubelines().getDefault() == null)
      throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(TubelineassemblyMessages.MASM_0005_NO_DEFAULT_TUBELINE_IN_DEFAULT_CFG_FILE(str2))); 
    this.appConfigUrl = locateResource(str1, paramVarArgs);
    if (this.appConfigUrl != null) {
      LOGGER.config(TubelineassemblyMessages.MASM_0006_APP_CFG_FILE_LOCATED(this.appConfigUrl));
      this.appConfig = loadMetroConfig(this.appConfigUrl);
    } else {
      LOGGER.config(TubelineassemblyMessages.MASM_0007_APP_CFG_FILE_NOT_FOUND());
      this.appConfig = null;
    } 
  }
  
  TubeFactoryList getEndpointSideTubeFactories(URI paramURI) { return getTubeFactories(paramURI, ENDPOINT_SIDE_RESOLVER); }
  
  TubeFactoryList getClientSideTubeFactories(URI paramURI) { return getTubeFactories(paramURI, CLIENT_SIDE_RESOLVER); }
  
  private TubeFactoryList getTubeFactories(URI paramURI, TubeFactoryListResolver paramTubeFactoryListResolver) {
    if (this.appConfig != null && this.appConfig.getTubelines() != null) {
      for (TubelineMapping tubelineMapping : this.appConfig.getTubelines().getTubelineMappings()) {
        if (tubelineMapping.getEndpointRef().equals(paramURI.toString())) {
          TubeFactoryList tubeFactoryList = paramTubeFactoryListResolver.getFactories(getTubeline(this.appConfig, resolveReference(tubelineMapping.getTubelineRef())));
          if (tubeFactoryList != null)
            return tubeFactoryList; 
          break;
        } 
      } 
      if (this.appConfig.getTubelines().getDefault() != null) {
        TubeFactoryList tubeFactoryList = paramTubeFactoryListResolver.getFactories(getTubeline(this.appConfig, resolveReference(this.appConfig.getTubelines().getDefault())));
        if (tubeFactoryList != null)
          return tubeFactoryList; 
      } 
    } 
    for (TubelineMapping tubelineMapping : this.defaultConfig.getTubelines().getTubelineMappings()) {
      if (tubelineMapping.getEndpointRef().equals(paramURI.toString())) {
        TubeFactoryList tubeFactoryList = paramTubeFactoryListResolver.getFactories(getTubeline(this.defaultConfig, resolveReference(tubelineMapping.getTubelineRef())));
        if (tubeFactoryList != null)
          return tubeFactoryList; 
        break;
      } 
    } 
    return paramTubeFactoryListResolver.getFactories(getTubeline(this.defaultConfig, resolveReference(this.defaultConfig.getTubelines().getDefault())));
  }
  
  TubelineDefinition getTubeline(MetroConfig paramMetroConfig, URI paramURI) {
    if (paramMetroConfig != null && paramMetroConfig.getTubelines() != null)
      for (TubelineDefinition tubelineDefinition : paramMetroConfig.getTubelines().getTubelineDefinitions()) {
        if (tubelineDefinition.getName().equals(paramURI.getFragment()))
          return tubelineDefinition; 
      }  
    return null;
  }
  
  private static URI resolveReference(String paramString) {
    try {
      return new URI(paramString);
    } catch (URISyntaxException uRISyntaxException) {
      throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(TubelineassemblyMessages.MASM_0008_INVALID_URI_REFERENCE(paramString), uRISyntaxException));
    } 
  }
  
  private static URL locateResource(String paramString, ResourceLoader paramResourceLoader) {
    if (paramResourceLoader == null)
      return null; 
    try {
      return paramResourceLoader.getResource(paramString);
    } catch (MalformedURLException malformedURLException) {
      LOGGER.severe(TubelineassemblyMessages.MASM_0009_CANNOT_FORM_VALID_URL(paramString), malformedURLException);
      return null;
    } 
  }
  
  private static URL locateResource(String paramString, ResourceLoader[] paramArrayOfResourceLoader) {
    for (ResourceLoader resourceLoader : paramArrayOfResourceLoader) {
      URL uRL = locateResource(paramString, resourceLoader);
      if (uRL != null)
        return uRL; 
    } 
    return null;
  }
  
  private static MetroConfig loadMetroConfig(@NotNull URL paramURL) {
    MetroConfig metroConfig = null;
    try {
      JAXBContext jAXBContext = createJAXBContext();
      Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
      XMLInputFactory xMLInputFactory = XmlUtil.newXMLInputFactory(true);
      JAXBElement jAXBElement = unmarshaller.unmarshal(xMLInputFactory.createXMLStreamReader(paramURL.openStream()), MetroConfig.class);
      metroConfig = (MetroConfig)jAXBElement.getValue();
    } catch (Exception exception) {
      LOGGER.warning(TubelineassemblyMessages.MASM_0010_ERROR_READING_CFG_FILE_FROM_LOCATION(paramURL.toString()), exception);
    } 
    return metroConfig;
  }
  
  private static JAXBContext createJAXBContext() throws Exception { return isJDKInternal() ? (JAXBContext)AccessController.doPrivileged(new PrivilegedExceptionAction<JAXBContext>() {
          public JAXBContext run() throws Exception { return JAXBContext.newInstance(MetroConfig.class.getPackage().getName()); }
        },  createSecurityContext()) : JAXBContext.newInstance(MetroConfig.class.getPackage().getName()); }
  
  private static AccessControlContext createSecurityContext() {
    Permissions permissions = new Permissions();
    permissions.add(new RuntimePermission("accessClassInPackage.com.sun.xml.internal.ws.runtime.config"));
    permissions.add(new ReflectPermission("suppressAccessChecks"));
    return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) });
  }
  
  private static boolean isJDKInternal() { return MetroConfigLoader.class.getName().startsWith("com.sun.xml.internal.ws"); }
  
  private static class MetroConfigUrlLoader extends ResourceLoader {
    Container container;
    
    ResourceLoader parentLoader;
    
    MetroConfigUrlLoader(ResourceLoader param1ResourceLoader) { this.parentLoader = param1ResourceLoader; }
    
    MetroConfigUrlLoader(Container param1Container) {
      this((param1Container != null) ? (ResourceLoader)param1Container.getSPI(ResourceLoader.class) : null);
      this.container = param1Container;
    }
    
    public URL getResource(String param1String) throws MalformedURLException {
      LOGGER.entering(new Object[] { param1String });
      uRL = null;
      try {
        if (this.parentLoader != null) {
          if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine(TubelineassemblyMessages.MASM_0011_LOADING_RESOURCE(param1String, this.parentLoader)); 
          uRL = this.parentLoader.getResource(param1String);
        } 
        if (uRL == null)
          uRL = loadViaClassLoaders("com/sun/xml/internal/ws/assembler/" + param1String); 
        if (uRL == null && this.container != null)
          uRL = loadFromServletContext(param1String); 
        return uRL;
      } finally {
        LOGGER.exiting(uRL);
      } 
    }
    
    private static URL loadViaClassLoaders(String param1String) throws MalformedURLException {
      URL uRL = tryLoadFromClassLoader(param1String, Thread.currentThread().getContextClassLoader());
      if (uRL == null) {
        uRL = tryLoadFromClassLoader(param1String, MetroConfigLoader.class.getClassLoader());
        if (uRL == null)
          return ClassLoader.getSystemResource(param1String); 
      } 
      return uRL;
    }
    
    private static URL tryLoadFromClassLoader(String param1String, ClassLoader param1ClassLoader) { return (param1ClassLoader != null) ? param1ClassLoader.getResource(param1String) : null; }
    
    private URL loadFromServletContext(String param1String) throws MalformedURLException {
      Object object = null;
      try {
        Class clazz = Class.forName("javax.servlet.ServletContext");
        object = this.container.getSPI(clazz);
        if (object != null) {
          if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine(TubelineassemblyMessages.MASM_0012_LOADING_VIA_SERVLET_CONTEXT(param1String, object)); 
          try {
            Method method = object.getClass().getMethod("getResource", new Class[] { String.class });
            Object object1 = method.invoke(object, new Object[] { "/WEB-INF/" + param1String });
            return (URL)URL.class.cast(object1);
          } catch (Exception exception) {
            throw (RuntimeException)LOGGER.logSevereException(new RuntimeException(TubelineassemblyMessages.MASM_0013_ERROR_INVOKING_SERVLET_CONTEXT_METHOD("getResource()")), exception);
          } 
        } 
      } catch (ClassNotFoundException classNotFoundException) {
        if (LOGGER.isLoggable(Level.FINE))
          LOGGER.fine(TubelineassemblyMessages.MASM_0014_UNABLE_TO_LOAD_CLASS("javax.servlet.ServletContext")); 
      } 
      return null;
    }
  }
  
  private static interface TubeFactoryListResolver {
    TubeFactoryList getFactories(TubelineDefinition param1TubelineDefinition);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\MetroConfigLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */