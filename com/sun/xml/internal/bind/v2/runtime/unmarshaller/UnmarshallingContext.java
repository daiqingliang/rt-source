package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.istack.internal.SAXParseException2;
import com.sun.xml.internal.bind.IDResolver;
import com.sun.xml.internal.bind.api.AccessorException;
import com.sun.xml.internal.bind.api.ClassResolver;
import com.sun.xml.internal.bind.unmarshaller.InfosetScanner;
import com.sun.xml.internal.bind.v2.ClassFactory;
import com.sun.xml.internal.bind.v2.runtime.AssociationMap;
import com.sun.xml.internal.bind.v2.runtime.Coordinator;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;

public final class UnmarshallingContext extends Coordinator implements NamespaceContext, ValidationEventHandler, ErrorHandler, XmlVisitor, XmlVisitor.TextPredictor {
  private static final Logger logger = Logger.getLogger(UnmarshallingContext.class.getName());
  
  private final State root;
  
  private State current;
  
  private static final LocatorEx DUMMY_INSTANCE;
  
  @NotNull
  private LocatorEx locator = DUMMY_INSTANCE;
  
  private Object result;
  
  private JaxBeanInfo expectedType;
  
  private IDResolver idResolver;
  
  private boolean isUnmarshalInProgress = true;
  
  private boolean aborted = false;
  
  public final UnmarshallerImpl parent;
  
  private final AssociationMap assoc;
  
  private boolean isInplaceMode;
  
  private InfosetScanner scanner;
  
  private Object currentElement;
  
  private NamespaceContext environmentNamespaceContext;
  
  @Nullable
  public ClassResolver classResolver;
  
  @Nullable
  public ClassLoader classLoader;
  
  private final Map<Class, Factory> factories = new HashMap();
  
  private Patcher[] patchers = null;
  
  private int patchersLen = 0;
  
  private String[] nsBind = new String[16];
  
  private int nsLen = 0;
  
  private Scope[] scopes = new Scope[16];
  
  private int scopeTop = 0;
  
  private static final Loader DEFAULT_ROOT_LOADER;
  
  private static final Loader EXPECTED_TYPE_ROOT_LOADER;
  
  public UnmarshallingContext(UnmarshallerImpl paramUnmarshallerImpl, AssociationMap paramAssociationMap) {
    for (byte b = 0; b < this.scopes.length; b++)
      this.scopes[b] = new Scope(this); 
    this.parent = paramUnmarshallerImpl;
    this.assoc = paramAssociationMap;
    this.root = this.current = new State(null, null);
  }
  
  public void reset(InfosetScanner paramInfosetScanner, boolean paramBoolean, JaxBeanInfo paramJaxBeanInfo, IDResolver paramIDResolver) {
    this.scanner = paramInfosetScanner;
    this.isInplaceMode = paramBoolean;
    this.expectedType = paramJaxBeanInfo;
    this.idResolver = paramIDResolver;
  }
  
  public JAXBContextImpl getJAXBContext() { return this.parent.context; }
  
  public State getCurrentState() { return this.current; }
  
  public Loader selectRootLoader(State paramState, TagName paramTagName) throws SAXException {
    try {
      Loader loader = getJAXBContext().selectRootLoader(paramState, paramTagName);
      if (loader != null)
        return loader; 
      if (this.classResolver != null) {
        Class clazz = this.classResolver.resolveElementName(paramTagName.uri, paramTagName.local);
        if (clazz != null) {
          JAXBContextImpl jAXBContextImpl = getJAXBContext().createAugmented(clazz);
          JaxBeanInfo jaxBeanInfo = jAXBContextImpl.getBeanInfo(clazz);
          return jaxBeanInfo.getLoader(jAXBContextImpl, true);
        } 
      } 
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      handleError(exception);
    } 
    return null;
  }
  
  public void clearStates() {
    State state;
    while (state.next != null)
      state = state.next; 
    while (state.prev != null) {
      state.loader = null;
      state.nil = false;
      state.receiver = null;
      state.intercepter = null;
      state.elementDefaultValue = null;
      state.target = null;
      state.next = null;
    } 
    this.current = state;
  }
  
  public void setFactories(Object paramObject) {
    this.factories.clear();
    if (paramObject == null)
      return; 
    if (paramObject instanceof Object[]) {
      for (Object object : (Object[])paramObject)
        addFactory(object); 
    } else {
      addFactory(paramObject);
    } 
  }
  
  private void addFactory(Object paramObject) {
    for (Method method : paramObject.getClass().getMethods()) {
      if (method.getName().startsWith("create") && method.getParameterTypes().length <= 0) {
        Class clazz = method.getReturnType();
        this.factories.put(clazz, new Factory(paramObject, method));
      } 
    } 
  }
  
  public void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext) throws SAXException {
    if (paramLocatorEx != null)
      this.locator = paramLocatorEx; 
    this.environmentNamespaceContext = paramNamespaceContext;
    this.result = null;
    this.current = this.root;
    this.patchersLen = 0;
    this.aborted = false;
    this.isUnmarshalInProgress = true;
    this.nsLen = 0;
    if (this.expectedType != null) {
      this.root.loader = EXPECTED_TYPE_ROOT_LOADER;
    } else {
      this.root.loader = DEFAULT_ROOT_LOADER;
    } 
    this.idResolver.startDocument(this);
  }
  
  public void startElement(TagName paramTagName) throws SAXException {
    pushCoordinator();
    try {
      _startElement(paramTagName);
    } finally {
      popCoordinator();
    } 
  }
  
  private void _startElement(TagName paramTagName) throws SAXException {
    if (this.assoc != null)
      this.currentElement = this.scanner.getCurrentElement(); 
    Loader loader = this.current.loader;
    this.current.push();
    loader.childElement(this.current, paramTagName);
    assert this.current.loader != null;
    this.current.loader.startElement(this.current, paramTagName);
  }
  
  public void text(CharSequence paramCharSequence) throws SAXException {
    pushCoordinator();
    try {
      if (this.current.elementDefaultValue != null && paramCharSequence.length() == 0)
        paramCharSequence = this.current.elementDefaultValue; 
      this.current.loader.text(this.current, paramCharSequence);
    } finally {
      popCoordinator();
    } 
  }
  
  public final void endElement(TagName paramTagName) throws SAXException {
    pushCoordinator();
    try {
      State state;
      state.loader.leaveElement(state, paramTagName);
      Object object = state.target;
      Receiver receiver = state.receiver;
      Intercepter intercepter = state.intercepter;
      state.pop();
      if (intercepter != null)
        object = intercepter.intercept(this.current, object); 
      if (receiver != null)
        receiver.receive(this.current, object); 
    } finally {
      popCoordinator();
    } 
  }
  
  public void endDocument() {
    runPatchers();
    this.idResolver.endDocument();
    this.isUnmarshalInProgress = false;
    this.currentElement = null;
    this.locator = DUMMY_INSTANCE;
    this.environmentNamespaceContext = null;
    assert this.root == this.current;
  }
  
  @Deprecated
  public boolean expectText() { return this.current.loader.expectText; }
  
  @Deprecated
  public XmlVisitor.TextPredictor getPredictor() { return this; }
  
  public UnmarshallingContext getContext() { return this; }
  
  public Object getResult() throws UnmarshalException {
    if (this.isUnmarshalInProgress)
      throw new IllegalStateException(); 
    if (!this.aborted)
      return this.result; 
    throw new UnmarshalException((String)null);
  }
  
  void clearResult() {
    if (this.isUnmarshalInProgress)
      throw new IllegalStateException(); 
    this.result = null;
  }
  
  public Object createInstance(Class<?> paramClass) throws SAXException {
    if (!this.factories.isEmpty()) {
      Factory factory = (Factory)this.factories.get(paramClass);
      if (factory != null)
        return factory.createInstance(); 
    } 
    return ClassFactory.create(paramClass);
  }
  
  public Object createInstance(JaxBeanInfo paramJaxBeanInfo) throws SAXException {
    if (!this.factories.isEmpty()) {
      Factory factory = (Factory)this.factories.get(paramJaxBeanInfo.jaxbType);
      if (factory != null)
        return factory.createInstance(); 
    } 
    try {
      return paramJaxBeanInfo.createInstance(this);
    } catch (IllegalAccessException illegalAccessException) {
      Loader.reportError("Unable to create an instance of " + paramJaxBeanInfo.jaxbType.getName(), illegalAccessException, false);
    } catch (InvocationTargetException invocationTargetException) {
      Loader.reportError("Unable to create an instance of " + paramJaxBeanInfo.jaxbType.getName(), invocationTargetException, false);
    } catch (InstantiationException instantiationException) {
      Loader.reportError("Unable to create an instance of " + paramJaxBeanInfo.jaxbType.getName(), instantiationException, false);
    } 
    return null;
  }
  
  public void handleEvent(ValidationEvent paramValidationEvent, boolean paramBoolean) throws SAXException {
    ValidationEventHandler validationEventHandler = this.parent.getEventHandler();
    boolean bool = validationEventHandler.handleEvent(paramValidationEvent);
    if (!bool)
      this.aborted = true; 
    if (!paramBoolean || !bool)
      throw new SAXParseException2(paramValidationEvent.getMessage(), this.locator, new UnmarshalException(paramValidationEvent.getMessage(), paramValidationEvent.getLinkedException())); 
  }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent) {
    try {
      boolean bool = this.parent.getEventHandler().handleEvent(paramValidationEvent);
      if (!bool)
        this.aborted = true; 
      return bool;
    } catch (RuntimeException runtimeException) {
      return false;
    } 
  }
  
  public void handleError(Exception paramException) throws SAXException { handleError(paramException, true); }
  
  public void handleError(Exception paramException, boolean paramBoolean) throws SAXException { handleEvent(new ValidationEventImpl(1, paramException.getMessage(), this.locator.getLocation(), paramException), paramBoolean); }
  
  public void handleError(String paramString) { handleEvent(new ValidationEventImpl(1, paramString, this.locator.getLocation())); }
  
  protected ValidationEventLocator getLocation() { return this.locator.getLocation(); }
  
  public LocatorEx getLocator() { return this.locator; }
  
  public void errorUnresolvedIDREF(Object paramObject, String paramString, LocatorEx paramLocatorEx) throws SAXException { handleEvent(new ValidationEventImpl(1, Messages.UNRESOLVED_IDREF.format(new Object[] { paramString }, ), paramLocatorEx.getLocation()), true); }
  
  public void addPatcher(Patcher paramPatcher) {
    if (this.patchers == null)
      this.patchers = new Patcher[32]; 
    if (this.patchers.length == this.patchersLen) {
      Patcher[] arrayOfPatcher = new Patcher[this.patchersLen * 2];
      System.arraycopy(this.patchers, 0, arrayOfPatcher, 0, this.patchersLen);
      this.patchers = arrayOfPatcher;
    } 
    this.patchers[this.patchersLen++] = paramPatcher;
  }
  
  private void runPatchers() {
    if (this.patchers != null)
      for (byte b = 0; b < this.patchersLen; b++) {
        this.patchers[b].run();
        this.patchers[b] = null;
      }  
  }
  
  public String addToIdTable(String paramString) throws SAXException {
    Object object = this.current.target;
    if (object == null)
      object = this.current.prev.target; 
    this.idResolver.bind(paramString, object);
    return paramString;
  }
  
  public Callable getObjectFromId(String paramString, Class paramClass) throws SAXException { return this.idResolver.resolve(paramString, paramClass); }
  
  public void startPrefixMapping(String paramString1, String paramString2) {
    if (this.nsBind.length == this.nsLen) {
      String[] arrayOfString = new String[this.nsLen * 2];
      System.arraycopy(this.nsBind, 0, arrayOfString, 0, this.nsLen);
      this.nsBind = arrayOfString;
    } 
    this.nsBind[this.nsLen++] = paramString1;
    this.nsBind[this.nsLen++] = paramString2;
  }
  
  public void endPrefixMapping(String paramString) { this.nsLen -= 2; }
  
  private String resolveNamespacePrefix(String paramString) throws SAXException {
    if (paramString.equals("xml"))
      return "http://www.w3.org/XML/1998/namespace"; 
    for (int i = this.nsLen - 2; i >= 0; i -= 2) {
      if (paramString.equals(this.nsBind[i]))
        return this.nsBind[i + 1]; 
    } 
    return (this.environmentNamespaceContext != null) ? this.environmentNamespaceContext.getNamespaceURI(paramString.intern()) : (paramString.equals("") ? "" : null);
  }
  
  public String[] getNewlyDeclaredPrefixes() { return getPrefixList(this.current.prev.numNsDecl); }
  
  public String[] getAllDeclaredPrefixes() { return getPrefixList(0); }
  
  private String[] getPrefixList(int paramInt) {
    int i = (this.current.numNsDecl - paramInt) / 2;
    String[] arrayOfString = new String[i];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfString[b] = this.nsBind[paramInt + b * 2]; 
    return arrayOfString;
  }
  
  public Iterator<String> getPrefixes(String paramString) { return Collections.unmodifiableList(getAllPrefixesInList(paramString)).iterator(); }
  
  private List<String> getAllPrefixesInList(String paramString) {
    ArrayList arrayList = new ArrayList();
    if (paramString == null)
      throw new IllegalArgumentException(); 
    if (paramString.equals("http://www.w3.org/XML/1998/namespace")) {
      arrayList.add("xml");
      return arrayList;
    } 
    if (paramString.equals("http://www.w3.org/2000/xmlns/")) {
      arrayList.add("xmlns");
      return arrayList;
    } 
    for (int i = this.nsLen - 2; i >= 0; i -= 2) {
      if (paramString.equals(this.nsBind[i + 1]) && getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1]))
        arrayList.add(this.nsBind[i]); 
    } 
    return arrayList;
  }
  
  public String getPrefix(String paramString) throws SAXException {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    if (paramString.equals("http://www.w3.org/XML/1998/namespace"))
      return "xml"; 
    if (paramString.equals("http://www.w3.org/2000/xmlns/"))
      return "xmlns"; 
    for (int i = this.nsLen - 2; i >= 0; i -= 2) {
      if (paramString.equals(this.nsBind[i + 1]) && getNamespaceURI(this.nsBind[i]).equals(this.nsBind[i + 1]))
        return this.nsBind[i]; 
    } 
    return (this.environmentNamespaceContext != null) ? this.environmentNamespaceContext.getPrefix(paramString) : null;
  }
  
  public String getNamespaceURI(String paramString) throws SAXException {
    if (paramString == null)
      throw new IllegalArgumentException(); 
    return paramString.equals("xmlns") ? "http://www.w3.org/2000/xmlns/" : resolveNamespacePrefix(paramString);
  }
  
  public void startScope(int paramInt) {
    this.scopeTop += paramInt;
    if (this.scopeTop >= this.scopes.length) {
      Scope[] arrayOfScope = new Scope[Math.max(this.scopeTop + 1, this.scopes.length * 2)];
      System.arraycopy(this.scopes, 0, arrayOfScope, 0, this.scopes.length);
      for (int i = this.scopes.length; i < arrayOfScope.length; i++)
        arrayOfScope[i] = new Scope(this); 
      this.scopes = arrayOfScope;
    } 
  }
  
  public void endScope(int paramInt) {
    try {
      while (paramInt > 0) {
        this.scopes[this.scopeTop].finish();
        paramInt--;
        this.scopeTop--;
      } 
    } catch (AccessorException accessorException) {
      handleError(accessorException);
      while (paramInt > 0) {
        this.scopes[this.scopeTop--] = new Scope(this);
        paramInt--;
      } 
    } 
  }
  
  public Scope getScope(int paramInt) { return this.scopes[this.scopeTop - paramInt]; }
  
  public void recordInnerPeer(Object paramObject) {
    if (this.assoc != null)
      this.assoc.addInner(this.currentElement, paramObject); 
  }
  
  public Object getInnerPeer() throws UnmarshalException { return (this.assoc != null && this.isInplaceMode) ? this.assoc.getInnerPeer(this.currentElement) : null; }
  
  public void recordOuterPeer(Object paramObject) {
    if (this.assoc != null)
      this.assoc.addOuter(this.currentElement, paramObject); 
  }
  
  public Object getOuterPeer() throws UnmarshalException { return (this.assoc != null && this.isInplaceMode) ? this.assoc.getOuterPeer(this.currentElement) : null; }
  
  public String getXMIMEContentType() {
    Object object = this.current.target;
    return (object == null) ? null : getJAXBContext().getXMIMEContentType(object);
  }
  
  public static UnmarshallingContext getInstance() { return (UnmarshallingContext)Coordinator._getInstance(); }
  
  public Collection<QName> getCurrentExpectedElements() {
    pushCoordinator();
    try {
      State state;
      Loader loader = state.loader;
      return (loader != null) ? loader.getExpectedChildElements() : null;
    } finally {
      popCoordinator();
    } 
  }
  
  public Collection<QName> getCurrentExpectedAttributes() {
    pushCoordinator();
    try {
      State state;
      Loader loader = state.loader;
      return (loader != null) ? loader.getExpectedAttributes() : null;
    } finally {
      popCoordinator();
    } 
  }
  
  public StructureLoader getStructureLoader() { return (this.current.loader instanceof StructureLoader) ? (StructureLoader)this.current.loader : null; }
  
  public boolean shouldErrorBeReported() {
    if (logger.isLoggable(Level.FINEST))
      return true; 
    if (errorsCounter >= 0) {
      errorsCounter--;
      if (errorsCounter == 0)
        handleEvent(new ValidationEventImpl(0, Messages.ERRORS_LIMIT_EXCEEDED.format(new Object[0]), getLocator().getLocation(), null), true); 
    } 
    return (errorsCounter >= 0);
  }
  
  static  {
    LocatorImpl locatorImpl = new LocatorImpl();
    locatorImpl.setPublicId(null);
    locatorImpl.setSystemId(null);
    locatorImpl.setLineNumber(-1);
    locatorImpl.setColumnNumber(-1);
    DUMMY_INSTANCE = new LocatorExWrapper(locatorImpl);
    errorsCounter = 10;
    DEFAULT_ROOT_LOADER = new DefaultRootLoader(null);
    EXPECTED_TYPE_ROOT_LOADER = new ExpectedTypeRootLoader(null);
  }
  
  private static final class DefaultRootLoader extends Loader implements Receiver {
    private DefaultRootLoader() {}
    
    public void childElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
      Loader loader = param1State.getContext().selectRootLoader(param1State, param1TagName);
      if (loader != null) {
        param1State.loader = loader;
        param1State.receiver = this;
        return;
      } 
      JaxBeanInfo jaxBeanInfo = XsiTypeLoader.parseXsiType(param1State, param1TagName, null);
      if (jaxBeanInfo == null) {
        reportUnexpectedChildElement(param1TagName, false);
        return;
      } 
      param1State.loader = jaxBeanInfo.getLoader(null, false);
      param1State.prev.backup = new JAXBElement(param1TagName.createQName(), Object.class, null);
      param1State.receiver = this;
    }
    
    public Collection<QName> getExpectedChildElements() { return UnmarshallingContext.getInstance().getJAXBContext().getValidRootNames(); }
    
    public void receive(UnmarshallingContext.State param1State, Object param1Object) {
      if (param1State.backup != null) {
        ((JAXBElement)param1State.backup).setValue(param1Object);
        param1Object = param1State.backup;
      } 
      if (param1State.nil)
        ((JAXBElement)param1Object).setNil(true); 
      (param1State.getContext()).result = param1Object;
    }
  }
  
  private static final class ExpectedTypeRootLoader extends Loader implements Receiver {
    private ExpectedTypeRootLoader() {}
    
    public void childElement(UnmarshallingContext.State param1State, TagName param1TagName) throws SAXException {
      UnmarshallingContext unmarshallingContext = param1State.getContext();
      QName qName = new QName(param1TagName.uri, param1TagName.local);
      param1State.prev.target = new JAXBElement(qName, unmarshallingContext.expectedType.jaxbType, null, null);
      param1State.receiver = this;
      param1State.loader = new XsiNilLoader(unmarshallingContext.expectedType.getLoader(null, true));
    }
    
    public void receive(UnmarshallingContext.State param1State, Object param1Object) {
      JAXBElement jAXBElement = (JAXBElement)param1State.target;
      jAXBElement.setValue(param1Object);
      param1State.getContext().recordOuterPeer(jAXBElement);
      (param1State.getContext()).result = jAXBElement;
    }
  }
  
  private static class Factory {
    private final Object factorInstance;
    
    private final Method method;
    
    public Factory(Object param1Object, Method param1Method) {
      this.factorInstance = param1Object;
      this.method = param1Method;
    }
    
    public Object createInstance() throws UnmarshalException {
      try {
        return this.method.invoke(this.factorInstance, new Object[0]);
      } catch (IllegalAccessException illegalAccessException) {
        UnmarshallingContext.getInstance().handleError(illegalAccessException, false);
      } catch (InvocationTargetException invocationTargetException) {
        UnmarshallingContext.getInstance().handleError(invocationTargetException, false);
      } 
      return null;
    }
  }
  
  public final class State {
    private Loader loader;
    
    private Receiver receiver;
    
    private Intercepter intercepter;
    
    private Object target;
    
    private Object backup;
    
    private int numNsDecl;
    
    private String elementDefaultValue;
    
    private State prev;
    
    private State next;
    
    private boolean nil = false;
    
    private boolean mixed = false;
    
    public UnmarshallingContext getContext() { return UnmarshallingContext.this; }
    
    private State(State param1State) {
      this.prev = param1State;
      if (param1State != null) {
        param1State.next = this;
        if (param1State.mixed)
          this.mixed = true; 
      } 
    }
    
    private void push() {
      if (logger.isLoggable(Level.FINEST))
        logger.log(Level.FINEST, "State.push"); 
      if (this.next == null) {
        assert UnmarshallingContext.this.current == this;
        this.next = new State(UnmarshallingContext.this, this);
      } 
      this.nil = false;
      State state = this.next;
      state.numNsDecl = UnmarshallingContext.this.nsLen;
      UnmarshallingContext.this.current = state;
    }
    
    private void pop() {
      if (logger.isLoggable(Level.FINEST))
        logger.log(Level.FINEST, "State.pop"); 
      assert this.prev != null;
      this.loader = null;
      this.nil = false;
      this.mixed = false;
      this.receiver = null;
      this.intercepter = null;
      this.elementDefaultValue = null;
      this.target = null;
      UnmarshallingContext.this.current = this.prev;
      this.next = null;
    }
    
    public boolean isMixed() { return this.mixed; }
    
    public Object getTarget() throws UnmarshalException { return this.target; }
    
    public void setLoader(Loader param1Loader) {
      if (param1Loader instanceof StructureLoader)
        this.mixed = !((StructureLoader)param1Loader).getBeanInfo().hasElementOnlyContentModel(); 
      this.loader = param1Loader;
    }
    
    public void setReceiver(Receiver param1Receiver) { this.receiver = param1Receiver; }
    
    public State getPrev() { return this.prev; }
    
    public void setIntercepter(Intercepter param1Intercepter) { this.intercepter = param1Intercepter; }
    
    public void setBackup(Object param1Object) { this.backup = param1Object; }
    
    public void setTarget(Object param1Object) { this.target = param1Object; }
    
    public Object getBackup() throws UnmarshalException { return this.backup; }
    
    public boolean isNil() { return this.nil; }
    
    public void setNil(boolean param1Boolean) { this.nil = param1Boolean; }
    
    public Loader getLoader() { return this.loader; }
    
    public String getElementDefaultValue() { return this.elementDefaultValue; }
    
    public void setElementDefaultValue(String param1String) { this.elementDefaultValue = param1String; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\UnmarshallingContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */