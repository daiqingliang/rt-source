package com.sun.xml.internal.txw2;

import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.txw2.annotation.XmlNamespace;
import com.sun.xml.internal.txw2.annotation.XmlValue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.xml.namespace.QName;

final class ContainerElement implements InvocationHandler, TypedXmlWriter {
  final Document document;
  
  StartTag startTag;
  
  final EndTag endTag = new EndTag();
  
  private final String nsUri;
  
  private Content tail;
  
  private ContainerElement prevOpen;
  
  private ContainerElement nextOpen;
  
  private final ContainerElement parent;
  
  private ContainerElement lastOpenChild;
  
  private boolean blocked;
  
  public ContainerElement(Document paramDocument, ContainerElement paramContainerElement, String paramString1, String paramString2) {
    this.parent = paramContainerElement;
    this.document = paramDocument;
    this.nsUri = paramString1;
    this.startTag = new StartTag(this, paramString1, paramString2);
    this.tail = this.startTag;
    if (isRoot())
      paramDocument.setFirstContent(this.startTag); 
  }
  
  private boolean isRoot() { return (this.parent == null); }
  
  private boolean isCommitted() { return (this.tail == null); }
  
  public Document getDocument() { return this.document; }
  
  boolean isBlocked() { return (this.blocked && !isCommitted()); }
  
  public void block() { this.blocked = true; }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable {
    if (paramMethod.getDeclaringClass() == TypedXmlWriter.class || paramMethod.getDeclaringClass() == Object.class)
      try {
        return paramMethod.invoke(this, paramArrayOfObject);
      } catch (InvocationTargetException invocationTargetException) {
        throw invocationTargetException.getTargetException();
      }  
    XmlAttribute xmlAttribute = (XmlAttribute)paramMethod.getAnnotation(XmlAttribute.class);
    XmlValue xmlValue = (XmlValue)paramMethod.getAnnotation(XmlValue.class);
    XmlElement xmlElement = (XmlElement)paramMethod.getAnnotation(XmlElement.class);
    if (xmlAttribute != null) {
      if (xmlValue != null || xmlElement != null)
        throw new IllegalAnnotationException(paramMethod.toString()); 
      addAttribute(xmlAttribute, paramMethod, paramArrayOfObject);
      return paramObject;
    } 
    if (xmlValue != null) {
      if (xmlElement != null)
        throw new IllegalAnnotationException(paramMethod.toString()); 
      _pcdata(paramArrayOfObject);
      return paramObject;
    } 
    return addElement(xmlElement, paramMethod, paramArrayOfObject);
  }
  
  private void addAttribute(XmlAttribute paramXmlAttribute, Method paramMethod, Object[] paramArrayOfObject) {
    assert paramXmlAttribute != null;
    checkStartTag();
    String str = paramXmlAttribute.value();
    if (paramXmlAttribute.value().length() == 0)
      str = paramMethod.getName(); 
    _attribute(paramXmlAttribute.ns(), str, paramArrayOfObject);
  }
  
  private void checkStartTag() {
    if (this.startTag == null)
      throw new IllegalStateException("start tag has already been written"); 
  }
  
  private Object addElement(XmlElement paramXmlElement, Method paramMethod, Object[] paramArrayOfObject) {
    Class clazz = paramMethod.getReturnType();
    String str1 = "##default";
    String str2 = paramMethod.getName();
    if (paramXmlElement != null) {
      if (paramXmlElement.value().length() != 0)
        str2 = paramXmlElement.value(); 
      str1 = paramXmlElement.ns();
    } 
    if (str1.equals("##default")) {
      Class clazz1 = paramMethod.getDeclaringClass();
      XmlElement xmlElement = (XmlElement)clazz1.getAnnotation(XmlElement.class);
      if (xmlElement != null)
        str1 = xmlElement.ns(); 
      if (str1.equals("##default"))
        str1 = getNamespace(clazz1.getPackage()); 
    } 
    if (clazz == void.class) {
      boolean bool = (paramMethod.getAnnotation(com.sun.xml.internal.txw2.annotation.XmlCDATA.class) != null) ? 1 : 0;
      StartTag startTag1 = new StartTag(this.document, str1, str2);
      addChild(startTag1);
      for (Object object : paramArrayOfObject) {
        Pcdata pcdata;
        if (bool) {
          pcdata = new Cdata(this.document, startTag1, object);
        } else {
          pcdata = new Pcdata(this.document, startTag1, object);
        } 
        addChild(pcdata);
      } 
      addChild(new EndTag());
      return null;
    } 
    if (TypedXmlWriter.class.isAssignableFrom(clazz))
      return _element(str1, str2, clazz); 
    throw new IllegalSignatureException("Illegal return type: " + clazz);
  }
  
  private String getNamespace(Package paramPackage) {
    String str;
    if (paramPackage == null)
      return ""; 
    XmlNamespace xmlNamespace = (XmlNamespace)paramPackage.getAnnotation(XmlNamespace.class);
    if (xmlNamespace != null) {
      str = xmlNamespace.value();
    } else {
      str = "";
    } 
    return str;
  }
  
  private void addChild(Content paramContent) {
    this.tail.setNext(this.document, paramContent);
    this.tail = paramContent;
  }
  
  public void commit() { commit(true); }
  
  public void commit(boolean paramBoolean) {
    _commit(paramBoolean);
    this.document.flush();
  }
  
  private void _commit(boolean paramBoolean) {
    if (isCommitted())
      return; 
    addChild(this.endTag);
    if (isRoot())
      addChild(new EndDocument()); 
    this.tail = null;
    if (paramBoolean)
      for (ContainerElement containerElement = this; containerElement != null; containerElement = containerElement.parent) {
        while (containerElement.prevOpen != null)
          containerElement.prevOpen._commit(false); 
      }  
    while (this.lastOpenChild != null)
      this.lastOpenChild._commit(false); 
    if (this.parent != null) {
      if (this.parent.lastOpenChild == this) {
        assert this.nextOpen == null : "this must be the last one";
        this.parent.lastOpenChild = this.prevOpen;
      } else {
        assert this.nextOpen.prevOpen == this;
        this.nextOpen.prevOpen = this.prevOpen;
      } 
      if (this.prevOpen != null) {
        assert this.prevOpen.nextOpen == this;
        this.prevOpen.nextOpen = this.nextOpen;
      } 
    } 
    this.nextOpen = null;
    this.prevOpen = null;
  }
  
  public void _attribute(String paramString, Object paramObject) { _attribute("", paramString, paramObject); }
  
  public void _attribute(String paramString1, String paramString2, Object paramObject) {
    checkStartTag();
    this.startTag.addAttribute(paramString1, paramString2, paramObject);
  }
  
  public void _attribute(QName paramQName, Object paramObject) { _attribute(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramObject); }
  
  public void _namespace(String paramString) { _namespace(paramString, false); }
  
  public void _namespace(String paramString1, String paramString2) {
    if (paramString2 == null)
      throw new IllegalArgumentException(); 
    checkStartTag();
    this.startTag.addNamespaceDecl(paramString1, paramString2, false);
  }
  
  public void _namespace(String paramString, boolean paramBoolean) {
    checkStartTag();
    this.startTag.addNamespaceDecl(paramString, null, paramBoolean);
  }
  
  public void _pcdata(Object paramObject) { addChild(new Pcdata(this.document, this.startTag, paramObject)); }
  
  public void _cdata(Object paramObject) { addChild(new Cdata(this.document, this.startTag, paramObject)); }
  
  public void _comment(Object paramObject) { addChild(new Comment(this.document, this.startTag, paramObject)); }
  
  public <T extends TypedXmlWriter> T _element(String paramString, Class<T> paramClass) { return (T)_element(this.nsUri, paramString, paramClass); }
  
  public <T extends TypedXmlWriter> T _element(QName paramQName, Class<T> paramClass) { return (T)_element(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramClass); }
  
  public <T extends TypedXmlWriter> T _element(Class<T> paramClass) { return (T)_element(TXW.getTagName(paramClass), paramClass); }
  
  public <T extends TypedXmlWriter> T _cast(Class<T> paramClass) { return (T)(TypedXmlWriter)paramClass.cast(Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, this)); }
  
  public <T extends TypedXmlWriter> T _element(String paramString1, String paramString2, Class<T> paramClass) {
    ContainerElement containerElement = new ContainerElement(this.document, this, paramString1, paramString2);
    addChild(containerElement.startTag);
    this.tail = containerElement.endTag;
    if (this.lastOpenChild != null) {
      assert this.lastOpenChild.parent == this;
      assert containerElement.prevOpen == null;
      assert containerElement.nextOpen == null;
      containerElement.prevOpen = this.lastOpenChild;
      assert this.lastOpenChild.nextOpen == null;
      this.lastOpenChild.nextOpen = containerElement;
    } 
    this.lastOpenChild = containerElement;
    return (T)containerElement._cast(paramClass);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\ContainerElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */