package com.sun.xml.internal.ws.db;

import com.oracle.webservices.internal.api.databinding.Databinding;
import com.oracle.webservices.internal.api.databinding.DatabindingModeFeature;
import com.oracle.webservices.internal.api.databinding.WSDLGenerator;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.spi.db.DatabindingProvider;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public class DatabindingFactoryImpl extends DatabindingFactory {
  static final String WsRuntimeFactoryDefaultImpl = "com.sun.xml.internal.ws.db.DatabindingProviderImpl";
  
  protected Map<String, Object> properties = new HashMap();
  
  protected DatabindingProvider defaultRuntimeFactory;
  
  protected List<DatabindingProvider> providers;
  
  private static List<DatabindingProvider> providers() {
    ArrayList arrayList = new ArrayList();
    for (DatabindingProvider databindingProvider : ServiceFinder.find(DatabindingProvider.class))
      arrayList.add(databindingProvider); 
    return arrayList;
  }
  
  public Map<String, Object> properties() { return this.properties; }
  
  <T> T property(Class<T> paramClass, String paramString) {
    if (paramString == null)
      paramString = paramClass.getName(); 
    return (T)paramClass.cast(this.properties.get(paramString));
  }
  
  public DatabindingProvider provider(DatabindingConfig paramDatabindingConfig) {
    String str = databindingMode(paramDatabindingConfig);
    if (this.providers == null)
      this.providers = providers(); 
    DatabindingProvider databindingProvider = null;
    if (this.providers != null)
      for (DatabindingProvider databindingProvider1 : this.providers) {
        if (databindingProvider1.isFor(str))
          databindingProvider = databindingProvider1; 
      }  
    if (databindingProvider == null)
      databindingProvider = new DatabindingProviderImpl(); 
    return databindingProvider;
  }
  
  public Databinding createRuntime(DatabindingConfig paramDatabindingConfig) {
    DatabindingProvider databindingProvider = provider(paramDatabindingConfig);
    return databindingProvider.create(paramDatabindingConfig);
  }
  
  public WSDLGenerator createWsdlGen(DatabindingConfig paramDatabindingConfig) {
    DatabindingProvider databindingProvider = provider(paramDatabindingConfig);
    return databindingProvider.wsdlGen(paramDatabindingConfig);
  }
  
  String databindingMode(DatabindingConfig paramDatabindingConfig) {
    if (paramDatabindingConfig.getMappingInfo() != null && paramDatabindingConfig.getMappingInfo().getDatabindingMode() != null)
      return paramDatabindingConfig.getMappingInfo().getDatabindingMode(); 
    if (paramDatabindingConfig.getFeatures() != null)
      for (WebServiceFeature webServiceFeature : paramDatabindingConfig.getFeatures()) {
        if (webServiceFeature instanceof DatabindingModeFeature) {
          DatabindingModeFeature databindingModeFeature = (DatabindingModeFeature)webServiceFeature;
          paramDatabindingConfig.properties().putAll(databindingModeFeature.getProperties());
          return databindingModeFeature.getMode();
        } 
      }  
    return null;
  }
  
  ClassLoader classLoader() {
    ClassLoader classLoader = (ClassLoader)property(ClassLoader.class, null);
    if (classLoader == null)
      classLoader = Thread.currentThread().getContextClassLoader(); 
    return classLoader;
  }
  
  Properties loadPropertiesFile(String paramString) {
    ClassLoader classLoader = classLoader();
    Properties properties1 = new Properties();
    try {
      InputStream inputStream = null;
      if (classLoader == null) {
        inputStream = ClassLoader.getSystemResourceAsStream(paramString);
      } else {
        inputStream = classLoader.getResourceAsStream(paramString);
      } 
      if (inputStream != null)
        properties1.load(inputStream); 
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
    return properties1;
  }
  
  public Databinding.Builder createBuilder(Class<?> paramClass1, Class<?> paramClass2) { return new ConfigBuilder(this, paramClass1, paramClass2); }
  
  static class ConfigBuilder implements Databinding.Builder {
    DatabindingConfig config;
    
    DatabindingFactoryImpl factory;
    
    ConfigBuilder(DatabindingFactoryImpl param1DatabindingFactoryImpl, Class<?> param1Class1, Class<?> param1Class2) {
      this.factory = param1DatabindingFactoryImpl;
      this.config = new DatabindingConfig();
      this.config.setContractClass(param1Class1);
      this.config.setEndpointClass(param1Class2);
    }
    
    public Databinding.Builder targetNamespace(String param1String) {
      this.config.getMappingInfo().setTargetNamespace(param1String);
      return this;
    }
    
    public Databinding.Builder serviceName(QName param1QName) {
      this.config.getMappingInfo().setServiceName(param1QName);
      return this;
    }
    
    public Databinding.Builder portName(QName param1QName) {
      this.config.getMappingInfo().setPortName(param1QName);
      return this;
    }
    
    public Databinding.Builder wsdlURL(URL param1URL) {
      this.config.setWsdlURL(param1URL);
      return this;
    }
    
    public Databinding.Builder wsdlSource(Source param1Source) {
      this.config.setWsdlSource(param1Source);
      return this;
    }
    
    public Databinding.Builder entityResolver(EntityResolver param1EntityResolver) {
      this.config.setEntityResolver(param1EntityResolver);
      return this;
    }
    
    public Databinding.Builder classLoader(ClassLoader param1ClassLoader) {
      this.config.setClassLoader(param1ClassLoader);
      return this;
    }
    
    public Databinding.Builder feature(WebServiceFeature... param1VarArgs) {
      this.config.setFeatures(param1VarArgs);
      return this;
    }
    
    public Databinding.Builder property(String param1String, Object param1Object) {
      this.config.properties().put(param1String, param1Object);
      if (isfor(BindingID.class, param1String, param1Object))
        this.config.getMappingInfo().setBindingID((BindingID)param1Object); 
      if (isfor(WSBinding.class, param1String, param1Object))
        this.config.setWSBinding((WSBinding)param1Object); 
      if (isfor(WSDLPort.class, param1String, param1Object))
        this.config.setWsdlPort((WSDLPort)param1Object); 
      if (isfor(MetadataReader.class, param1String, param1Object))
        this.config.setMetadataReader((MetadataReader)param1Object); 
      return this;
    }
    
    boolean isfor(Class<?> param1Class, String param1String, Object param1Object) { return (param1Class.getName().equals(param1String) && param1Class.isInstance(param1Object)); }
    
    public Databinding build() { return this.factory.createRuntime(this.config); }
    
    public WSDLGenerator createWSDLGenerator() { return this.factory.createWsdlGen(this.config); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\db\DatabindingFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */