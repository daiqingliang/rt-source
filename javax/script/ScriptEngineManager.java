package javax.script;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

public class ScriptEngineManager {
  private static final boolean DEBUG = false;
  
  private HashSet<ScriptEngineFactory> engineSpis;
  
  private HashMap<String, ScriptEngineFactory> nameAssociations;
  
  private HashMap<String, ScriptEngineFactory> extensionAssociations;
  
  private HashMap<String, ScriptEngineFactory> mimeTypeAssociations;
  
  private Bindings globalScope;
  
  public ScriptEngineManager() {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    init(classLoader);
  }
  
  public ScriptEngineManager(ClassLoader paramClassLoader) { init(paramClassLoader); }
  
  private void init(ClassLoader paramClassLoader) {
    this.globalScope = new SimpleBindings();
    this.engineSpis = new HashSet();
    this.nameAssociations = new HashMap();
    this.extensionAssociations = new HashMap();
    this.mimeTypeAssociations = new HashMap();
    initEngines(paramClassLoader);
  }
  
  private ServiceLoader<ScriptEngineFactory> getServiceLoader(ClassLoader paramClassLoader) { return (paramClassLoader != null) ? ServiceLoader.load(ScriptEngineFactory.class, paramClassLoader) : ServiceLoader.loadInstalled(ScriptEngineFactory.class); }
  
  private void initEngines(final ClassLoader loader) {
    Iterator iterator = null;
    try {
      ServiceLoader serviceLoader = (ServiceLoader)AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<ScriptEngineFactory>>() {
            public ServiceLoader<ScriptEngineFactory> run() { return ScriptEngineManager.this.getServiceLoader(loader); }
          });
      iterator = serviceLoader.iterator();
    } catch (ServiceConfigurationError serviceConfigurationError) {
      System.err.println("Can't find ScriptEngineFactory providers: " + serviceConfigurationError.getMessage());
      return;
    } 
    try {
      while (iterator.hasNext()) {
        try {
          ScriptEngineFactory scriptEngineFactory = (ScriptEngineFactory)iterator.next();
          this.engineSpis.add(scriptEngineFactory);
        } catch (ServiceConfigurationError serviceConfigurationError) {
          System.err.println("ScriptEngineManager providers.next(): " + serviceConfigurationError.getMessage());
        } 
      } 
    } catch (ServiceConfigurationError serviceConfigurationError) {
      System.err.println("ScriptEngineManager providers.hasNext(): " + serviceConfigurationError.getMessage());
      return;
    } 
  }
  
  public void setBindings(Bindings paramBindings) {
    if (paramBindings == null)
      throw new IllegalArgumentException("Global scope cannot be null."); 
    this.globalScope = paramBindings;
  }
  
  public Bindings getBindings() { return this.globalScope; }
  
  public void put(String paramString, Object paramObject) { this.globalScope.put(paramString, paramObject); }
  
  public Object get(String paramString) { return this.globalScope.get(paramString); }
  
  public ScriptEngine getEngineByName(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    Object object;
    if (null != (object = this.nameAssociations.get(paramString))) {
      ScriptEngineFactory scriptEngineFactory = (ScriptEngineFactory)object;
      try {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        scriptEngine.setBindings(getBindings(), 200);
        return scriptEngine;
      } catch (Exception exception) {}
    } 
    for (ScriptEngineFactory scriptEngineFactory : this.engineSpis) {
      List list = null;
      try {
        list = scriptEngineFactory.getNames();
      } catch (Exception exception) {}
      if (list != null)
        for (String str : list) {
          if (paramString.equals(str))
            try {
              ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
              scriptEngine.setBindings(getBindings(), 200);
              return scriptEngine;
            } catch (Exception exception) {} 
        }  
    } 
    return null;
  }
  
  public ScriptEngine getEngineByExtension(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    Object object;
    if (null != (object = this.extensionAssociations.get(paramString))) {
      ScriptEngineFactory scriptEngineFactory = (ScriptEngineFactory)object;
      try {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        scriptEngine.setBindings(getBindings(), 200);
        return scriptEngine;
      } catch (Exception exception) {}
    } 
    for (ScriptEngineFactory scriptEngineFactory : this.engineSpis) {
      List list = null;
      try {
        list = scriptEngineFactory.getExtensions();
      } catch (Exception exception) {}
      if (list == null)
        continue; 
      for (String str : list) {
        if (paramString.equals(str))
          try {
            ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
            scriptEngine.setBindings(getBindings(), 200);
            return scriptEngine;
          } catch (Exception exception) {} 
      } 
    } 
    return null;
  }
  
  public ScriptEngine getEngineByMimeType(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    Object object;
    if (null != (object = this.mimeTypeAssociations.get(paramString))) {
      ScriptEngineFactory scriptEngineFactory = (ScriptEngineFactory)object;
      try {
        ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
        scriptEngine.setBindings(getBindings(), 200);
        return scriptEngine;
      } catch (Exception exception) {}
    } 
    for (ScriptEngineFactory scriptEngineFactory : this.engineSpis) {
      List list = null;
      try {
        list = scriptEngineFactory.getMimeTypes();
      } catch (Exception exception) {}
      if (list == null)
        continue; 
      for (String str : list) {
        if (paramString.equals(str))
          try {
            ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
            scriptEngine.setBindings(getBindings(), 200);
            return scriptEngine;
          } catch (Exception exception) {} 
      } 
    } 
    return null;
  }
  
  public List<ScriptEngineFactory> getEngineFactories() {
    ArrayList arrayList = new ArrayList(this.engineSpis.size());
    for (ScriptEngineFactory scriptEngineFactory : this.engineSpis)
      arrayList.add(scriptEngineFactory); 
    return Collections.unmodifiableList(arrayList);
  }
  
  public void registerEngineName(String paramString, ScriptEngineFactory paramScriptEngineFactory) {
    if (paramString == null || paramScriptEngineFactory == null)
      throw new NullPointerException(); 
    this.nameAssociations.put(paramString, paramScriptEngineFactory);
  }
  
  public void registerEngineMimeType(String paramString, ScriptEngineFactory paramScriptEngineFactory) {
    if (paramString == null || paramScriptEngineFactory == null)
      throw new NullPointerException(); 
    this.mimeTypeAssociations.put(paramString, paramScriptEngineFactory);
  }
  
  public void registerEngineExtension(String paramString, ScriptEngineFactory paramScriptEngineFactory) {
    if (paramString == null || paramScriptEngineFactory == null)
      throw new NullPointerException(); 
    this.extensionAssociations.put(paramString, paramScriptEngineFactory);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\ScriptEngineManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */