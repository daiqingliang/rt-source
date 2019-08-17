package com.sun.xml.internal.ws.binding;

import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import com.sun.xml.internal.ws.api.FeatureListValidator;
import com.sun.xml.internal.ws.api.FeatureListValidatorAnnotation;
import com.sun.xml.internal.ws.api.ImpliesWebServiceFeature;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.RuntimeModelerException;
import com.sun.xml.internal.ws.resources.ModelerMessages;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import javax.xml.ws.RespectBinding;
import javax.xml.ws.RespectBindingFeature;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.MTOMFeature;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

public final class WebServiceFeatureList extends AbstractMap<Class<? extends WebServiceFeature>, WebServiceFeature> implements WSFeatureList {
  private Map<Class<? extends WebServiceFeature>, WebServiceFeature> wsfeatures = new HashMap();
  
  private boolean isValidating = false;
  
  @Nullable
  private WSDLFeaturedObject parent;
  
  private static final Logger LOGGER = Logger.getLogger(WebServiceFeatureList.class.getName());
  
  public static WebServiceFeatureList toList(Iterable<WebServiceFeature> paramIterable) {
    if (paramIterable instanceof WebServiceFeatureList)
      return (WebServiceFeatureList)paramIterable; 
    WebServiceFeatureList webServiceFeatureList = new WebServiceFeatureList();
    if (paramIterable != null)
      webServiceFeatureList.addAll(paramIterable); 
    return webServiceFeatureList;
  }
  
  public WebServiceFeatureList() {}
  
  public WebServiceFeatureList(@NotNull WebServiceFeature... paramVarArgs) {
    if (paramVarArgs != null)
      for (WebServiceFeature webServiceFeature : paramVarArgs)
        addNoValidate(webServiceFeature);  
  }
  
  public void validate() {
    if (!this.isValidating) {
      this.isValidating = true;
      for (WebServiceFeature webServiceFeature : this)
        validate(webServiceFeature); 
    } 
  }
  
  private void validate(WebServiceFeature paramWebServiceFeature) {
    FeatureListValidatorAnnotation featureListValidatorAnnotation = (FeatureListValidatorAnnotation)paramWebServiceFeature.getClass().getAnnotation(FeatureListValidatorAnnotation.class);
    if (featureListValidatorAnnotation != null) {
      Class clazz = featureListValidatorAnnotation.bean();
      try {
        FeatureListValidator featureListValidator = (FeatureListValidator)clazz.newInstance();
        featureListValidator.validate(this);
      } catch (InstantiationException instantiationException) {
        throw new WebServiceException(instantiationException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new WebServiceException(illegalAccessException);
      } 
    } 
  }
  
  public WebServiceFeatureList(WebServiceFeatureList paramWebServiceFeatureList) {
    if (paramWebServiceFeatureList != null) {
      this.wsfeatures.putAll(paramWebServiceFeatureList.wsfeatures);
      this.parent = paramWebServiceFeatureList.parent;
      this.isValidating = paramWebServiceFeatureList.isValidating;
    } 
  }
  
  public WebServiceFeatureList(@NotNull Class<?> paramClass) { parseAnnotations(paramClass); }
  
  public void parseAnnotations(Iterable<Annotation> paramIterable) {
    for (Annotation annotation : paramIterable) {
      WebServiceFeature webServiceFeature = getFeature(annotation);
      if (webServiceFeature != null)
        add(webServiceFeature); 
    } 
  }
  
  public static WebServiceFeature getFeature(Annotation paramAnnotation) {
    WebServiceFeature webServiceFeature = null;
    if (!paramAnnotation.annotationType().isAnnotationPresent(WebServiceFeatureAnnotation.class)) {
      webServiceFeature = null;
    } else if (paramAnnotation instanceof Addressing) {
      Addressing addressing = (Addressing)paramAnnotation;
      try {
        webServiceFeature = new AddressingFeature(addressing.enabled(), addressing.required(), addressing.responses());
      } catch (NoSuchMethodError noSuchMethodError) {
        throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_ADDRESSING_RESPONSES_NOSUCHMETHOD(toJar(Which.which(Addressing.class))), new Object[0]);
      } 
    } else if (paramAnnotation instanceof MTOM) {
      MTOM mTOM = (MTOM)paramAnnotation;
      MTOMFeature mTOMFeature = new MTOMFeature(mTOM.enabled(), mTOM.threshold());
    } else if (paramAnnotation instanceof RespectBinding) {
      RespectBinding respectBinding = (RespectBinding)paramAnnotation;
      RespectBindingFeature respectBindingFeature = new RespectBindingFeature(respectBinding.enabled());
    } else {
      webServiceFeature = getWebServiceFeatureBean(paramAnnotation);
    } 
    return webServiceFeature;
  }
  
  public void parseAnnotations(Class<?> paramClass) {
    for (Annotation annotation : paramClass.getAnnotations()) {
      WebServiceFeature webServiceFeature = getFeature(annotation);
      if (webServiceFeature != null) {
        if (webServiceFeature instanceof MTOMFeature) {
          BindingID bindingID = BindingID.parse(paramClass);
          MTOMFeature mTOMFeature = (MTOMFeature)bindingID.createBuiltinFeatureList().get(MTOMFeature.class);
          if (mTOMFeature != null && mTOMFeature.isEnabled() ^ webServiceFeature.isEnabled())
            throw new RuntimeModelerException(ModelerMessages.RUNTIME_MODELER_MTOM_CONFLICT(bindingID, Boolean.valueOf(webServiceFeature.isEnabled())), new Object[0]); 
        } 
        add(webServiceFeature);
      } 
    } 
  }
  
  private static String toJar(String paramString) {
    if (!paramString.startsWith("jar:"))
      return paramString; 
    paramString = paramString.substring(4);
    return paramString.substring(0, paramString.lastIndexOf('!'));
  }
  
  private static WebServiceFeature getWebServiceFeatureBean(Annotation paramAnnotation) {
    WebServiceFeature webServiceFeature;
    WebServiceFeatureAnnotation webServiceFeatureAnnotation = (WebServiceFeatureAnnotation)paramAnnotation.annotationType().getAnnotation(WebServiceFeatureAnnotation.class);
    Class clazz = webServiceFeatureAnnotation.bean();
    Constructor constructor = null;
    String[] arrayOfString = null;
    for (Constructor constructor1 : clazz.getConstructors()) {
      FeatureConstructor featureConstructor = (FeatureConstructor)constructor1.getAnnotation(FeatureConstructor.class);
      if (featureConstructor != null)
        if (constructor == null) {
          constructor = constructor1;
          arrayOfString = featureConstructor.value();
        } else {
          throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_MORETHANONE_FTRCONSTRUCTOR(paramAnnotation, clazz));
        }  
    } 
    if (constructor == null) {
      webServiceFeature = getWebServiceFeatureBeanViaBuilder(paramAnnotation, clazz);
      if (webServiceFeature != null)
        return webServiceFeature; 
      throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_NO_FTRCONSTRUCTOR(paramAnnotation, clazz));
    } 
    if (constructor.getParameterTypes().length != arrayOfString.length)
      throw new WebServiceException(ModelerMessages.RUNTIME_MODELER_WSFEATURE_ILLEGAL_FTRCONSTRUCTOR(paramAnnotation, clazz)); 
    try {
      Object[] arrayOfObject = new Object[arrayOfString.length];
      for (byte b = 0; b < arrayOfString.length; b++) {
        Method method = paramAnnotation.annotationType().getDeclaredMethod(arrayOfString[b], new Class[0]);
        arrayOfObject[b] = method.invoke(paramAnnotation, new Object[0]);
      } 
      webServiceFeature = (WebServiceFeature)constructor.newInstance(arrayOfObject);
    } catch (Exception exception) {
      throw new WebServiceException(exception);
    } 
    return webServiceFeature;
  }
  
  private static WebServiceFeature getWebServiceFeatureBeanViaBuilder(Annotation paramAnnotation, Class<? extends WebServiceFeature> paramClass) {
    try {
      Method method1 = paramClass.getDeclaredMethod("builder", new Class[0]);
      Object object1 = method1.invoke(paramClass, new Object[0]);
      Method method2 = object1.getClass().getDeclaredMethod("build", new Class[0]);
      for (Method method : object1.getClass().getDeclaredMethods()) {
        if (!method.equals(method2)) {
          String str = method.getName();
          Method method3 = paramAnnotation.annotationType().getDeclaredMethod(str, new Class[0]);
          Object object = method3.invoke(paramAnnotation, new Object[0]);
          Object[] arrayOfObject = { object };
          if (!skipDuringOrgJvnetWsToComOracleWebservicesPackageMove(method, object))
            method.invoke(object1, arrayOfObject); 
        } 
      } 
      Object object2 = method2.invoke(object1, new Object[0]);
      if (object2 instanceof WebServiceFeature)
        return (WebServiceFeature)object2; 
      throw new WebServiceException("Not a WebServiceFeature: " + object2);
    } catch (NoSuchMethodException noSuchMethodException) {
      return null;
    } catch (IllegalAccessException illegalAccessException) {
      throw new WebServiceException(illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      throw new WebServiceException(invocationTargetException);
    } 
  }
  
  private static boolean skipDuringOrgJvnetWsToComOracleWebservicesPackageMove(Method paramMethod, Object paramObject) {
    Class clazz = paramObject.getClass();
    if (!clazz.isEnum())
      return false; 
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    if (arrayOfClass.length != 1)
      throw new WebServiceException("expected only 1 parameter"); 
    String str = arrayOfClass[0].getName();
    return (!str.startsWith("com.oracle.webservices.internal.test.features_annotations_enums.apinew") && !str.startsWith("com.oracle.webservices.internal.api")) ? false : false;
  }
  
  public Iterator<WebServiceFeature> iterator() { return (this.parent != null) ? new MergedFeatures(this.parent.getFeatures()) : this.wsfeatures.values().iterator(); }
  
  @NotNull
  public WebServiceFeature[] toArray() { return (this.parent != null) ? (new MergedFeatures(this.parent.getFeatures())).toArray() : (WebServiceFeature[])this.wsfeatures.values().toArray(new WebServiceFeature[0]); }
  
  public boolean isEnabled(@NotNull Class<? extends WebServiceFeature> paramClass) {
    WebServiceFeature webServiceFeature = get(paramClass);
    return (webServiceFeature != null && webServiceFeature.isEnabled());
  }
  
  public boolean contains(@NotNull Class<? extends WebServiceFeature> paramClass) {
    WebServiceFeature webServiceFeature = get(paramClass);
    return (webServiceFeature != null);
  }
  
  @Nullable
  public <F extends WebServiceFeature> F get(@NotNull Class<F> paramClass) {
    WebServiceFeature webServiceFeature = (WebServiceFeature)paramClass.cast(this.wsfeatures.get(paramClass));
    return (webServiceFeature == null && this.parent != null) ? (F)this.parent.getFeatures().get(paramClass) : (F)webServiceFeature;
  }
  
  public void add(@NotNull WebServiceFeature paramWebServiceFeature) {
    if (addNoValidate(paramWebServiceFeature) && this.isValidating)
      validate(paramWebServiceFeature); 
  }
  
  private boolean addNoValidate(@NotNull WebServiceFeature paramWebServiceFeature) {
    if (!this.wsfeatures.containsKey(paramWebServiceFeature.getClass())) {
      this.wsfeatures.put(paramWebServiceFeature.getClass(), paramWebServiceFeature);
      if (paramWebServiceFeature instanceof ImpliesWebServiceFeature)
        ((ImpliesWebServiceFeature)paramWebServiceFeature).implyFeatures(this); 
      return true;
    } 
    return false;
  }
  
  public void addAll(@NotNull Iterable<WebServiceFeature> paramIterable) {
    for (WebServiceFeature webServiceFeature : paramIterable)
      add(webServiceFeature); 
  }
  
  void setMTOMEnabled(boolean paramBoolean) { this.wsfeatures.put(MTOMFeature.class, new MTOMFeature(paramBoolean)); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof WebServiceFeatureList))
      return false; 
    WebServiceFeatureList webServiceFeatureList = (WebServiceFeatureList)paramObject;
    return (this.wsfeatures.equals(webServiceFeatureList.wsfeatures) && this.parent == webServiceFeatureList.parent);
  }
  
  public String toString() { return this.wsfeatures.toString(); }
  
  public void mergeFeatures(@NotNull Iterable<WebServiceFeature> paramIterable, boolean paramBoolean) {
    for (WebServiceFeature webServiceFeature : paramIterable) {
      if (get(webServiceFeature.getClass()) == null) {
        add(webServiceFeature);
        continue;
      } 
      if (paramBoolean && isEnabled(webServiceFeature.getClass()) != webServiceFeature.isEnabled())
        LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(get(webServiceFeature.getClass()), webServiceFeature)); 
    } 
  }
  
  public void mergeFeatures(WebServiceFeature[] paramArrayOfWebServiceFeature, boolean paramBoolean) {
    for (WebServiceFeature webServiceFeature : paramArrayOfWebServiceFeature) {
      if (get(webServiceFeature.getClass()) == null) {
        add(webServiceFeature);
      } else if (paramBoolean && isEnabled(webServiceFeature.getClass()) != webServiceFeature.isEnabled()) {
        LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(get(webServiceFeature.getClass()), webServiceFeature));
      } 
    } 
  }
  
  public void mergeFeatures(@NotNull WSDLPort paramWSDLPort, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1 && !isEnabled(RespectBindingFeature.class))
      return; 
    if (!paramBoolean1) {
      addAll(paramWSDLPort.getFeatures());
      return;
    } 
    for (WebServiceFeature webServiceFeature : paramWSDLPort.getFeatures()) {
      if (get(webServiceFeature.getClass()) == null)
        try {
          Method method = webServiceFeature.getClass().getMethod("isRequired", new Class[0]);
          try {
            boolean bool = ((Boolean)method.invoke(webServiceFeature, new Object[0])).booleanValue();
            if (bool)
              add(webServiceFeature); 
            continue;
          } catch (IllegalAccessException illegalAccessException) {
            throw new WebServiceException(illegalAccessException);
          } catch (InvocationTargetException invocationTargetException) {
            throw new WebServiceException(invocationTargetException);
          } 
        } catch (NoSuchMethodException noSuchMethodException) {
          add(webServiceFeature);
          continue;
        }  
      if (paramBoolean2 && isEnabled(webServiceFeature.getClass()) != webServiceFeature.isEnabled())
        LOGGER.warning(ModelerMessages.RUNTIME_MODELER_FEATURE_CONFLICT(get(webServiceFeature.getClass()), webServiceFeature)); 
    } 
  }
  
  public void setParentFeaturedObject(@NotNull WSDLFeaturedObject paramWSDLFeaturedObject) { this.parent = paramWSDLFeaturedObject; }
  
  @Nullable
  public static <F extends WebServiceFeature> F getFeature(@NotNull WebServiceFeature[] paramArrayOfWebServiceFeature, @NotNull Class<F> paramClass) {
    for (WebServiceFeature webServiceFeature : paramArrayOfWebServiceFeature) {
      if (webServiceFeature.getClass() == paramClass)
        return (F)webServiceFeature; 
    } 
    return null;
  }
  
  public Set<Map.Entry<Class<? extends WebServiceFeature>, WebServiceFeature>> entrySet() { return this.wsfeatures.entrySet(); }
  
  public WebServiceFeature put(Class<? extends WebServiceFeature> paramClass, WebServiceFeature paramWebServiceFeature) { return (WebServiceFeature)this.wsfeatures.put(paramClass, paramWebServiceFeature); }
  
  public static SOAPVersion getSoapVersion(WSFeatureList paramWSFeatureList) {
    EnvelopeStyleFeature envelopeStyleFeature = (EnvelopeStyleFeature)paramWSFeatureList.get(EnvelopeStyleFeature.class);
    if (envelopeStyleFeature != null)
      return SOAPVersion.from(envelopeStyleFeature); 
    envelopeStyleFeature = (EnvelopeStyleFeature)paramWSFeatureList.get(EnvelopeStyleFeature.class);
    return (envelopeStyleFeature != null) ? SOAPVersion.from(envelopeStyleFeature) : null;
  }
  
  public static boolean isFeatureEnabled(Class<? extends WebServiceFeature> paramClass, WebServiceFeature[] paramArrayOfWebServiceFeature) {
    WebServiceFeature webServiceFeature = getFeature(paramArrayOfWebServiceFeature, paramClass);
    return (webServiceFeature != null && webServiceFeature.isEnabled());
  }
  
  public static WebServiceFeature[] toFeatureArray(WSBinding paramWSBinding) {
    if (!paramWSBinding.isFeatureEnabled(EnvelopeStyleFeature.class)) {
      WebServiceFeature[] arrayOfWebServiceFeature = { paramWSBinding.getSOAPVersion().toFeature() };
      paramWSBinding.getFeatures().mergeFeatures(arrayOfWebServiceFeature, false);
    } 
    return paramWSBinding.getFeatures().toArray();
  }
  
  private final class MergedFeatures extends Object implements Iterator<WebServiceFeature> {
    private final Stack<WebServiceFeature> features = new Stack();
    
    public MergedFeatures(WSFeatureList param1WSFeatureList) {
      for (WebServiceFeature webServiceFeature : this$0.wsfeatures.values())
        this.features.push(webServiceFeature); 
      for (WebServiceFeature webServiceFeature : param1WSFeatureList) {
        if (!this$0.wsfeatures.containsKey(webServiceFeature.getClass()))
          this.features.push(webServiceFeature); 
      } 
    }
    
    public boolean hasNext() { return !this.features.empty(); }
    
    public WebServiceFeature next() {
      if (!this.features.empty())
        return (WebServiceFeature)this.features.pop(); 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      if (!this.features.empty())
        this.features.pop(); 
    }
    
    public WebServiceFeature[] toArray() { return (WebServiceFeature[])this.features.toArray(new WebServiceFeature[0]); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\binding\WebServiceFeatureList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */