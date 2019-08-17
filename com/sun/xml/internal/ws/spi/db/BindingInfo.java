package com.sun.xml.internal.ws.spi.db;

import com.sun.xml.internal.ws.api.model.SEIModel;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BindingInfo {
  private String databindingMode;
  
  private String defaultNamespace;
  
  private Collection<Class> contentClasses = new ArrayList();
  
  private Collection<TypeInfo> typeInfos = new ArrayList();
  
  private Map<Class, Class> subclassReplacements = new HashMap();
  
  private Map<String, Object> properties = new HashMap();
  
  protected ClassLoader classLoader;
  
  private SEIModel seiModel;
  
  private URL wsdlURL;
  
  public String getDatabindingMode() { return this.databindingMode; }
  
  public void setDatabindingMode(String paramString) { this.databindingMode = paramString; }
  
  public String getDefaultNamespace() { return this.defaultNamespace; }
  
  public void setDefaultNamespace(String paramString) { this.defaultNamespace = paramString; }
  
  public Collection<Class> contentClasses() { return this.contentClasses; }
  
  public Collection<TypeInfo> typeInfos() { return this.typeInfos; }
  
  public Map<Class, Class> subclassReplacements() { return this.subclassReplacements; }
  
  public Map<String, Object> properties() { return this.properties; }
  
  public SEIModel getSEIModel() { return this.seiModel; }
  
  public void setSEIModel(SEIModel paramSEIModel) { this.seiModel = paramSEIModel; }
  
  public ClassLoader getClassLoader() { return this.classLoader; }
  
  public void setClassLoader(ClassLoader paramClassLoader) { this.classLoader = paramClassLoader; }
  
  public URL getWsdlURL() { return this.wsdlURL; }
  
  public void setWsdlURL(URL paramURL) { this.wsdlURL = paramURL; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\db\BindingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */