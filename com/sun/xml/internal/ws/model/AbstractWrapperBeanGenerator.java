package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.ws.spi.db.BindingHelper;
import com.sun.xml.internal.ws.util.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceException;

public abstract class AbstractWrapperBeanGenerator<T, C, M, A extends Comparable> extends Object {
  private static final Logger LOGGER = Logger.getLogger(AbstractWrapperBeanGenerator.class.getName());
  
  private static final String RETURN = "return";
  
  private static final String EMTPY_NAMESPACE_ID = "";
  
  private static final Class[] jaxbAnns = { javax.xml.bind.annotation.XmlAttachmentRef.class, javax.xml.bind.annotation.XmlMimeType.class, javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.class, javax.xml.bind.annotation.XmlList.class, XmlElement.class };
  
  private static final Set<String> skipProperties = new HashSet();
  
  private final AnnotationReader<T, C, ?, M> annReader;
  
  private final Navigator<T, C, ?, M> nav;
  
  private final BeanMemberFactory<T, A> factory;
  
  private static final Map<String, String> reservedWords;
  
  protected AbstractWrapperBeanGenerator(AnnotationReader<T, C, ?, M> paramAnnotationReader, Navigator<T, C, ?, M> paramNavigator, BeanMemberFactory<T, A> paramBeanMemberFactory) {
    this.annReader = paramAnnotationReader;
    this.nav = paramNavigator;
    this.factory = paramBeanMemberFactory;
  }
  
  private List<Annotation> collectJAXBAnnotations(M paramM) {
    ArrayList arrayList = new ArrayList();
    for (Class clazz : jaxbAnns) {
      Annotation annotation = this.annReader.getMethodAnnotation(clazz, paramM, null);
      if (annotation != null)
        arrayList.add(annotation); 
    } 
    return arrayList;
  }
  
  private List<Annotation> collectJAXBAnnotations(M paramM, int paramInt) {
    ArrayList arrayList = new ArrayList();
    for (Class clazz : jaxbAnns) {
      Annotation annotation = this.annReader.getMethodParameterAnnotation(clazz, paramM, paramInt, null);
      if (annotation != null)
        arrayList.add(annotation); 
    } 
    return arrayList;
  }
  
  protected abstract T getSafeType(T paramT);
  
  protected abstract T getHolderValueType(T paramT);
  
  protected abstract boolean isVoidType(T paramT);
  
  public List<A> collectRequestBeanMembers(M paramM) {
    ArrayList arrayList = new ArrayList();
    byte b = -1;
    for (Object object : this.nav.getMethodParameters(paramM)) {
      WebParam webParam = (WebParam)this.annReader.getMethodParameterAnnotation(WebParam.class, paramM, ++b, null);
      if (webParam == null || (!webParam.header() && !webParam.mode().equals(WebParam.Mode.OUT))) {
        Object object1 = getHolderValueType(object);
        Object object2 = (object1 != null) ? object1 : getSafeType(object);
        String str1 = (webParam != null && webParam.name().length() > 0) ? webParam.name() : ("arg" + b);
        String str2 = (webParam != null && webParam.targetNamespace().length() > 0) ? webParam.targetNamespace() : "";
        List list = collectJAXBAnnotations(paramM, b);
        processXmlElement(list, str1, str2, object2);
        Comparable comparable = (Comparable)this.factory.createWrapperBeanMember(object2, getPropertyName(str1), list);
        arrayList.add(comparable);
      } 
    } 
    return arrayList;
  }
  
  public List<A> collectResponseBeanMembers(M paramM) {
    ArrayList arrayList = new ArrayList();
    String str1 = "return";
    String str2 = "";
    boolean bool = false;
    WebResult webResult = (WebResult)this.annReader.getMethodAnnotation(WebResult.class, paramM, null);
    if (webResult != null) {
      if (webResult.name().length() > 0)
        str1 = webResult.name(); 
      if (webResult.targetNamespace().length() > 0)
        str2 = webResult.targetNamespace(); 
      bool = webResult.header();
    } 
    Object object = getSafeType(this.nav.getReturnType(paramM));
    if (!isVoidType(object) && !bool) {
      List list = collectJAXBAnnotations(paramM);
      processXmlElement(list, str1, str2, object);
      arrayList.add(this.factory.createWrapperBeanMember(object, getPropertyName(str1), list));
    } 
    byte b = -1;
    for (Object object1 : this.nav.getMethodParameters(paramM)) {
      b++;
      Object object2 = getHolderValueType(object1);
      WebParam webParam = (WebParam)this.annReader.getMethodParameterAnnotation(WebParam.class, paramM, b, null);
      if (object2 != null && (webParam == null || !webParam.header())) {
        String str3 = (webParam != null && webParam.name().length() > 0) ? webParam.name() : ("arg" + b);
        String str4 = (webParam != null && webParam.targetNamespace().length() > 0) ? webParam.targetNamespace() : "";
        List list = collectJAXBAnnotations(paramM, b);
        processXmlElement(list, str3, str4, object2);
        Comparable comparable = (Comparable)this.factory.createWrapperBeanMember(object2, getPropertyName(str3), list);
        arrayList.add(comparable);
      } 
    } 
    return arrayList;
  }
  
  private void processXmlElement(List<Annotation> paramList, String paramString1, String paramString2, T paramT) {
    XmlElement xmlElement1 = null;
    for (Annotation annotation : paramList) {
      if (annotation.annotationType() == XmlElement.class) {
        xmlElement1 = (XmlElement)annotation;
        paramList.remove(annotation);
        break;
      } 
    } 
    String str1 = (xmlElement1 != null && !xmlElement1.name().equals("##default")) ? xmlElement1.name() : paramString1;
    String str2 = (xmlElement1 != null && !xmlElement1.namespace().equals("##default")) ? xmlElement1.namespace() : paramString2;
    boolean bool1 = (this.nav.isArray(paramT) || (xmlElement1 != null && xmlElement1.nillable()));
    boolean bool2 = (xmlElement1 != null && xmlElement1.required());
    XmlElementHandler xmlElementHandler = new XmlElementHandler(str1, str2, bool1, bool2);
    XmlElement xmlElement2 = (XmlElement)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { XmlElement.class }, xmlElementHandler);
    paramList.add(xmlElement2);
  }
  
  public Collection<A> collectExceptionBeanMembers(C paramC) { return collectExceptionBeanMembers(paramC, true); }
  
  public Collection<A> collectExceptionBeanMembers(C paramC, boolean paramBoolean) {
    TreeMap treeMap = new TreeMap();
    getExceptionProperties(paramC, treeMap, paramBoolean);
    XmlType xmlType = (XmlType)this.annReader.getClassAnnotation(XmlType.class, paramC, null);
    if (xmlType != null) {
      String[] arrayOfString = xmlType.propOrder();
      if (arrayOfString.length > 0 && arrayOfString[0].length() != 0) {
        ArrayList arrayList = new ArrayList();
        for (String str : arrayOfString) {
          Comparable comparable = (Comparable)treeMap.get(str);
          if (comparable != null) {
            arrayList.add(comparable);
          } else {
            throw new WebServiceException("Exception " + paramC + " has @XmlType and its propOrder contains unknown property " + str);
          } 
        } 
        return arrayList;
      } 
    } 
    return treeMap.values();
  }
  
  private void getExceptionProperties(C paramC, TreeMap<String, A> paramTreeMap, boolean paramBoolean) {
    Object object = this.nav.getSuperClass(paramC);
    if (object != null)
      getExceptionProperties(object, paramTreeMap, paramBoolean); 
    Collection collection = this.nav.getDeclaredMethods(paramC);
    for (Object object1 : collection) {
      if (!this.nav.isPublicMethod(object1) || (this.nav.isStaticMethod(object1) && this.nav.isFinalMethod(object1)) || !this.nav.isPublicMethod(object1))
        continue; 
      String str = this.nav.getMethodName(object1);
      if ((!str.startsWith("get") && !str.startsWith("is")) || skipProperties.contains(str) || str.equals("get") || str.equals("is"))
        continue; 
      Object object2 = getSafeType(this.nav.getReturnType(object1));
      if (this.nav.getMethodParameters(object1).length == 0) {
        String str1 = str.startsWith("get") ? str.substring(3) : str.substring(2);
        if (paramBoolean)
          str1 = StringUtils.decapitalize(str1); 
        paramTreeMap.put(str1, this.factory.createWrapperBeanMember(object2, str1, Collections.emptyList()));
      } 
    } 
  }
  
  private static String getPropertyName(String paramString) {
    String str = BindingHelper.mangleNameToVariableName(paramString);
    return getJavaReservedVarialbeName(str);
  }
  
  @NotNull
  private static String getJavaReservedVarialbeName(@NotNull String paramString) {
    String str = (String)reservedWords.get(paramString);
    return (str == null) ? paramString : str;
  }
  
  static  {
    skipProperties.add("getCause");
    skipProperties.add("getLocalizedMessage");
    skipProperties.add("getClass");
    skipProperties.add("getStackTrace");
    skipProperties.add("getSuppressed");
    reservedWords = new HashMap();
    reservedWords.put("abstract", "_abstract");
    reservedWords.put("assert", "_assert");
    reservedWords.put("boolean", "_boolean");
    reservedWords.put("break", "_break");
    reservedWords.put("byte", "_byte");
    reservedWords.put("case", "_case");
    reservedWords.put("catch", "_catch");
    reservedWords.put("char", "_char");
    reservedWords.put("class", "_class");
    reservedWords.put("const", "_const");
    reservedWords.put("continue", "_continue");
    reservedWords.put("default", "_default");
    reservedWords.put("do", "_do");
    reservedWords.put("double", "_double");
    reservedWords.put("else", "_else");
    reservedWords.put("extends", "_extends");
    reservedWords.put("false", "_false");
    reservedWords.put("final", "_final");
    reservedWords.put("finally", "_finally");
    reservedWords.put("float", "_float");
    reservedWords.put("for", "_for");
    reservedWords.put("goto", "_goto");
    reservedWords.put("if", "_if");
    reservedWords.put("implements", "_implements");
    reservedWords.put("import", "_import");
    reservedWords.put("instanceof", "_instanceof");
    reservedWords.put("int", "_int");
    reservedWords.put("interface", "_interface");
    reservedWords.put("long", "_long");
    reservedWords.put("native", "_native");
    reservedWords.put("new", "_new");
    reservedWords.put("null", "_null");
    reservedWords.put("package", "_package");
    reservedWords.put("private", "_private");
    reservedWords.put("protected", "_protected");
    reservedWords.put("public", "_public");
    reservedWords.put("return", "_return");
    reservedWords.put("short", "_short");
    reservedWords.put("static", "_static");
    reservedWords.put("strictfp", "_strictfp");
    reservedWords.put("super", "_super");
    reservedWords.put("switch", "_switch");
    reservedWords.put("synchronized", "_synchronized");
    reservedWords.put("this", "_this");
    reservedWords.put("throw", "_throw");
    reservedWords.put("throws", "_throws");
    reservedWords.put("transient", "_transient");
    reservedWords.put("true", "_true");
    reservedWords.put("try", "_try");
    reservedWords.put("void", "_void");
    reservedWords.put("volatile", "_volatile");
    reservedWords.put("while", "_while");
    reservedWords.put("enum", "_enum");
  }
  
  public static interface BeanMemberFactory<T, A> {
    A createWrapperBeanMember(T param1T, String param1String, List<Annotation> param1List);
  }
  
  private static class XmlElementHandler implements InvocationHandler {
    private String name;
    
    private String namespace;
    
    private boolean nillable;
    
    private boolean required;
    
    XmlElementHandler(String param1String1, String param1String2, boolean param1Boolean1, boolean param1Boolean2) {
      this.name = param1String1;
      this.namespace = param1String2;
      this.nillable = param1Boolean1;
      this.required = param1Boolean2;
    }
    
    public Object invoke(Object param1Object, Method param1Method, Object[] param1ArrayOfObject) throws Throwable {
      String str = param1Method.getName();
      if (str.equals("name"))
        return this.name; 
      if (str.equals("namespace"))
        return this.namespace; 
      if (str.equals("nillable"))
        return Boolean.valueOf(this.nillable); 
      if (str.equals("required"))
        return Boolean.valueOf(this.required); 
      throw new WebServiceException("Not handling " + str);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\AbstractWrapperBeanGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */