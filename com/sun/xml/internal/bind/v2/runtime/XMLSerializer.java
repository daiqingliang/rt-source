package com.sun.xml.internal.bind.v2.runtime;

import com.sun.istack.internal.SAXException2;
import com.sun.xml.internal.bind.CycleRecoverable;
import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
import com.sun.xml.internal.bind.util.ValidationEventLocatorExImpl;
import com.sun.xml.internal.bind.v2.runtime.output.MTOMXmlOutput;
import com.sun.xml.internal.bind.v2.runtime.output.NamespaceContextImpl;
import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.XmlOutput;
import com.sun.xml.internal.bind.v2.runtime.property.Property;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.IntData;
import com.sun.xml.internal.bind.v2.util.CollisionCheckStack;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.activation.MimeType;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.bind.helpers.NotIdentifiableEventImpl;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.SAXException;

public final class XMLSerializer extends Coordinator {
  public final JAXBContextImpl grammar;
  
  private XmlOutput out;
  
  public final NameList nameList;
  
  public final int[] knownUri2prefixIndexMap;
  
  private final NamespaceContextImpl nsContext;
  
  private NamespaceContextImpl.Element nse;
  
  ThreadLocal<Property> currentProperty = new ThreadLocal();
  
  private boolean textHasAlreadyPrinted = false;
  
  private boolean seenRoot = false;
  
  private final MarshallerImpl marshaller;
  
  private final Set<Object> idReferencedObjects = new HashSet();
  
  private final Set<Object> objectsWithId = new HashSet();
  
  private final CollisionCheckStack<Object> cycleDetectionStack = new CollisionCheckStack();
  
  private String schemaLocation;
  
  private String noNsSchemaLocation;
  
  private Transformer identityTransformer;
  
  private ContentHandlerAdaptor contentHandlerAdapter;
  
  private boolean fragment;
  
  private Base64Data base64Data;
  
  private final IntData intData = new IntData();
  
  public AttachmentMarshaller attachmentMarshaller;
  
  private MimeType expectedMimeType;
  
  private boolean inlineBinaryFlag;
  
  private QName schemaType;
  
  XMLSerializer(MarshallerImpl paramMarshallerImpl) {
    this.marshaller = paramMarshallerImpl;
    this.grammar = this.marshaller.context;
    this.nsContext = new NamespaceContextImpl(this);
    this.nameList = this.marshaller.context.nameList;
    this.knownUri2prefixIndexMap = new int[this.nameList.namespaceURIs.length];
  }
  
  public Base64Data getCachedBase64DataInstance() { return new Base64Data(); }
  
  private String getIdFromObject(Object paramObject) throws SAXException, JAXBException { return this.grammar.getBeanInfo(paramObject, true).getId(paramObject, this); }
  
  private void handleMissingObjectError(String paramString) throws SAXException, IOException, XMLStreamException {
    reportMissingObjectError(paramString);
    endNamespaceDecls(null);
    endAttributes();
  }
  
  public void reportError(ValidationEvent paramValidationEvent) throws SAXException {
    ValidationEventHandler validationEventHandler;
    try {
      validationEventHandler = this.marshaller.getEventHandler();
    } catch (JAXBException jAXBException) {
      throw new SAXException2(jAXBException);
    } 
    if (!validationEventHandler.handleEvent(paramValidationEvent)) {
      if (paramValidationEvent.getLinkedException() instanceof Exception)
        throw new SAXException2((Exception)paramValidationEvent.getLinkedException()); 
      throw new SAXException2(paramValidationEvent.getMessage());
    } 
  }
  
  public final void reportError(String paramString, Throwable paramThrowable) throws SAXException {
    ValidationEventImpl validationEventImpl = new ValidationEventImpl(1, paramThrowable.getMessage(), getCurrentLocation(paramString), paramThrowable);
    reportError(validationEventImpl);
  }
  
  public void startElement(Name paramName, Object paramObject) {
    startElement();
    this.nse.setTagName(paramName, paramObject);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Object paramObject) {
    startElement();
    int i = this.nsContext.declareNsUri(paramString1, paramString3, false);
    this.nse.setTagName(i, paramString2, paramObject);
  }
  
  public void startElementForce(String paramString1, String paramString2, String paramString3, Object paramObject) {
    startElement();
    int i = this.nsContext.force(paramString1, paramString3);
    this.nse.setTagName(i, paramString2, paramObject);
  }
  
  public void endNamespaceDecls(Object paramObject) throws IOException, XMLStreamException {
    this.nsContext.collectionMode = false;
    this.nse.startElement(this.out, paramObject);
  }
  
  public void endAttributes() throws SAXException, IOException, XMLStreamException {
    if (!this.seenRoot) {
      this.seenRoot = true;
      if (this.schemaLocation != null || this.noNsSchemaLocation != null) {
        int i = this.nsContext.getPrefixIndex("http://www.w3.org/2001/XMLSchema-instance");
        if (this.schemaLocation != null)
          this.out.attribute(i, "schemaLocation", this.schemaLocation); 
        if (this.noNsSchemaLocation != null)
          this.out.attribute(i, "noNamespaceSchemaLocation", this.noNsSchemaLocation); 
      } 
    } 
    this.out.endStartTag();
  }
  
  public void endElement() throws SAXException, IOException, XMLStreamException {
    this.nse.endElement(this.out);
    this.nse = this.nse.pop();
    this.textHasAlreadyPrinted = false;
  }
  
  public void leafElement(Name paramName, String paramString1, String paramString2) throws SAXException, IOException, XMLStreamException {
    if (this.seenRoot) {
      this.textHasAlreadyPrinted = false;
      this.nse = this.nse.push();
      this.out.beginStartTag(paramName);
      this.out.endStartTag();
      if (paramString1 != null)
        try {
          this.out.text(paramString1, false);
        } catch (IllegalArgumentException illegalArgumentException) {
          throw new IllegalArgumentException(Messages.ILLEGAL_CONTENT.format(new Object[] { paramString2, illegalArgumentException.getMessage() }));
        }  
      this.out.endTag(paramName);
      this.nse = this.nse.pop();
    } else {
      startElement(paramName, null);
      endNamespaceDecls(null);
      endAttributes();
      try {
        this.out.text(paramString1, false);
      } catch (IllegalArgumentException illegalArgumentException) {
        throw new IllegalArgumentException(Messages.ILLEGAL_CONTENT.format(new Object[] { paramString2, illegalArgumentException.getMessage() }));
      } 
      endElement();
    } 
  }
  
  public void leafElement(Name paramName, Pcdata paramPcdata, String paramString) throws SAXException, IOException, XMLStreamException {
    if (this.seenRoot) {
      this.textHasAlreadyPrinted = false;
      this.nse = this.nse.push();
      this.out.beginStartTag(paramName);
      this.out.endStartTag();
      if (paramPcdata != null)
        this.out.text(paramPcdata, false); 
      this.out.endTag(paramName);
      this.nse = this.nse.pop();
    } else {
      startElement(paramName, null);
      endNamespaceDecls(null);
      endAttributes();
      this.out.text(paramPcdata, false);
      endElement();
    } 
  }
  
  public void leafElement(Name paramName, int paramInt, String paramString) throws SAXException, IOException, XMLStreamException {
    this.intData.reset(paramInt);
    leafElement(paramName, this.intData, paramString);
  }
  
  public void text(String paramString1, String paramString2) throws SAXException, IOException, XMLStreamException {
    if (paramString1 == null) {
      reportMissingObjectError(paramString2);
      return;
    } 
    this.out.text(paramString1, this.textHasAlreadyPrinted);
    this.textHasAlreadyPrinted = true;
  }
  
  public void text(Pcdata paramPcdata, String paramString) throws SAXException, IOException, XMLStreamException {
    if (paramPcdata == null) {
      reportMissingObjectError(paramString);
      return;
    } 
    this.out.text(paramPcdata, this.textHasAlreadyPrinted);
    this.textHasAlreadyPrinted = true;
  }
  
  public void attribute(String paramString1, String paramString2, String paramString3) throws SAXException {
    int i;
    if (paramString1.length() == 0) {
      i = -1;
    } else {
      i = this.nsContext.getPrefixIndex(paramString1);
    } 
    try {
      this.out.attribute(i, paramString2, paramString3);
    } catch (IOException iOException) {
      throw new SAXException2(iOException);
    } catch (XMLStreamException xMLStreamException) {
      throw new SAXException2(xMLStreamException);
    } 
  }
  
  public void attribute(Name paramName, CharSequence paramCharSequence) throws IOException, XMLStreamException { this.out.attribute(paramName, paramCharSequence.toString()); }
  
  public NamespaceContext2 getNamespaceContext() { return this.nsContext; }
  
  public String onID(Object paramObject, String paramString) {
    this.objectsWithId.add(paramObject);
    return paramString;
  }
  
  public String onIDREF(Object paramObject) throws SAXException, JAXBException {
    String str;
    try {
      str = getIdFromObject(paramObject);
    } catch (JAXBException jAXBException) {
      reportError(null, jAXBException);
      return null;
    } 
    this.idReferencedObjects.add(paramObject);
    if (str == null)
      reportError(new NotIdentifiableEventImpl(1, Messages.NOT_IDENTIFIABLE.format(new Object[0]), new ValidationEventLocatorImpl(paramObject))); 
    return str;
  }
  
  public void childAsRoot(Object paramObject) throws IOException, XMLStreamException {
    JaxBeanInfo jaxBeanInfo = this.grammar.getBeanInfo(paramObject, true);
    this.cycleDetectionStack.pushNocheck(paramObject);
    boolean bool = jaxBeanInfo.lookForLifecycleMethods();
    if (bool)
      fireBeforeMarshalEvents(jaxBeanInfo, paramObject); 
    jaxBeanInfo.serializeRoot(paramObject, this);
    if (bool)
      fireAfterMarshalEvents(jaxBeanInfo, paramObject); 
    this.cycleDetectionStack.pop();
  }
  
  private Object pushObject(Object paramObject, String paramString) throws SAXException {
    if (!this.cycleDetectionStack.push(paramObject))
      return paramObject; 
    if (paramObject instanceof CycleRecoverable) {
      paramObject = ((CycleRecoverable)paramObject).onCycleDetected(new CycleRecoverable.Context() {
            public Marshaller getMarshaller() { return XMLSerializer.this.marshaller; }
          });
      if (paramObject != null) {
        this.cycleDetectionStack.pop();
        return pushObject(paramObject, paramString);
      } 
      return null;
    } 
    reportError(new ValidationEventImpl(1, Messages.CYCLE_IN_MARSHALLER.format(new Object[] { this.cycleDetectionStack.getCycleString() }, ), getCurrentLocation(paramString), null));
    return null;
  }
  
  public final void childAsSoleContent(Object paramObject, String paramString) throws SAXException, IOException, XMLStreamException {
    if (paramObject == null) {
      handleMissingObjectError(paramString);
    } else {
      JaxBeanInfo jaxBeanInfo;
      paramObject = pushObject(paramObject, paramString);
      if (paramObject == null) {
        endNamespaceDecls(null);
        endAttributes();
        this.cycleDetectionStack.pop();
      } 
      try {
        jaxBeanInfo = this.grammar.getBeanInfo(paramObject, true);
      } catch (JAXBException jAXBException) {
        reportError(paramString, jAXBException);
        endNamespaceDecls(null);
        endAttributes();
        this.cycleDetectionStack.pop();
        return;
      } 
      boolean bool = jaxBeanInfo.lookForLifecycleMethods();
      if (bool)
        fireBeforeMarshalEvents(jaxBeanInfo, paramObject); 
      jaxBeanInfo.serializeURIs(paramObject, this);
      endNamespaceDecls(paramObject);
      jaxBeanInfo.serializeAttributes(paramObject, this);
      endAttributes();
      jaxBeanInfo.serializeBody(paramObject, this);
      if (bool)
        fireAfterMarshalEvents(jaxBeanInfo, paramObject); 
      this.cycleDetectionStack.pop();
    } 
  }
  
  public final void childAsXsiType(Object paramObject, String paramString, JaxBeanInfo paramJaxBeanInfo, boolean paramBoolean) throws SAXException, IOException, XMLStreamException {
    if (paramObject == null) {
      handleMissingObjectError(paramString);
    } else {
      paramObject = pushObject(paramObject, paramString);
      if (paramObject == null) {
        endNamespaceDecls(null);
        endAttributes();
        return;
      } 
      boolean bool = (paramObject.getClass() == paramJaxBeanInfo.jaxbType) ? 1 : 0;
      JaxBeanInfo jaxBeanInfo = paramJaxBeanInfo;
      QName qName = null;
      if (bool && jaxBeanInfo.lookForLifecycleMethods())
        fireBeforeMarshalEvents(jaxBeanInfo, paramObject); 
      if (!bool) {
        try {
          jaxBeanInfo = this.grammar.getBeanInfo(paramObject, true);
          if (jaxBeanInfo.lookForLifecycleMethods())
            fireBeforeMarshalEvents(jaxBeanInfo, paramObject); 
        } catch (JAXBException jAXBException) {
          reportError(paramString, jAXBException);
          endNamespaceDecls(null);
          endAttributes();
          return;
        } 
        if (jaxBeanInfo == paramJaxBeanInfo) {
          bool = true;
        } else {
          qName = jaxBeanInfo.getTypeName(paramObject);
          if (qName == null) {
            reportError(new ValidationEventImpl(1, Messages.SUBSTITUTED_BY_ANONYMOUS_TYPE.format(new Object[] { paramJaxBeanInfo.jaxbType.getName(), paramObject.getClass().getName(), jaxBeanInfo.jaxbType.getName() }, ), getCurrentLocation(paramString)));
          } else {
            getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
            getNamespaceContext().declareNamespace(qName.getNamespaceURI(), null, false);
          } 
        } 
      } 
      jaxBeanInfo.serializeURIs(paramObject, this);
      if (paramBoolean)
        getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true); 
      endNamespaceDecls(paramObject);
      if (!bool)
        attribute("http://www.w3.org/2001/XMLSchema-instance", "type", DatatypeConverter.printQName(qName, getNamespaceContext())); 
      jaxBeanInfo.serializeAttributes(paramObject, this);
      boolean bool1 = jaxBeanInfo.isNilIncluded();
      if (paramBoolean && !bool1)
        attribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "true"); 
      endAttributes();
      jaxBeanInfo.serializeBody(paramObject, this);
      if (jaxBeanInfo.lookForLifecycleMethods())
        fireAfterMarshalEvents(jaxBeanInfo, paramObject); 
      this.cycleDetectionStack.pop();
    } 
  }
  
  private void fireAfterMarshalEvents(JaxBeanInfo paramJaxBeanInfo, Object paramObject) {
    if (paramJaxBeanInfo.hasAfterMarshalMethod()) {
      Method method = (paramJaxBeanInfo.getLifecycleMethods()).afterMarshal;
      fireMarshalEvent(paramObject, method);
    } 
    Marshaller.Listener listener = this.marshaller.getListener();
    if (listener != null)
      listener.afterMarshal(paramObject); 
  }
  
  private void fireBeforeMarshalEvents(JaxBeanInfo paramJaxBeanInfo, Object paramObject) {
    if (paramJaxBeanInfo.hasBeforeMarshalMethod()) {
      Method method = (paramJaxBeanInfo.getLifecycleMethods()).beforeMarshal;
      fireMarshalEvent(paramObject, method);
    } 
    Marshaller.Listener listener = this.marshaller.getListener();
    if (listener != null)
      listener.beforeMarshal(paramObject); 
  }
  
  private void fireMarshalEvent(Object paramObject, Method paramMethod) {
    try {
      paramMethod.invoke(paramObject, new Object[] { this.marshaller });
    } catch (Exception exception) {
      throw new IllegalStateException(exception);
    } 
  }
  
  public void attWildcardAsURIs(Map<QName, String> paramMap, String paramString) {
    if (paramMap == null)
      return; 
    for (Map.Entry entry : paramMap.entrySet()) {
      QName qName = (QName)entry.getKey();
      String str = qName.getNamespaceURI();
      if (str.length() > 0) {
        String str1 = qName.getPrefix();
        if (str1.length() == 0)
          str1 = null; 
        this.nsContext.declareNsUri(str, str1, true);
      } 
    } 
  }
  
  public void attWildcardAsAttributes(Map<QName, String> paramMap, String paramString) {
    if (paramMap == null)
      return; 
    for (Map.Entry entry : paramMap.entrySet()) {
      QName qName = (QName)entry.getKey();
      attribute(qName.getNamespaceURI(), qName.getLocalPart(), (String)entry.getValue());
    } 
  }
  
  public final void writeXsiNilTrue() throws SAXException, IOException, XMLStreamException {
    getNamespaceContext().declareNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi", true);
    endNamespaceDecls(null);
    attribute("http://www.w3.org/2001/XMLSchema-instance", "nil", "true");
    endAttributes();
  }
  
  public <E> void writeDom(E paramE, DomHandler<E, ?> paramDomHandler, Object paramObject, String paramString) throws SAXException {
    Source source = paramDomHandler.marshal(paramE, this);
    if (this.contentHandlerAdapter == null)
      this.contentHandlerAdapter = new ContentHandlerAdaptor(this); 
    try {
      getIdentityTransformer().transform(source, new SAXResult(this.contentHandlerAdapter));
    } catch (TransformerException transformerException) {
      reportError(paramString, transformerException);
    } 
  }
  
  public Transformer getIdentityTransformer() {
    if (this.identityTransformer == null)
      this.identityTransformer = JAXBContextImpl.createTransformer(this.grammar.disableSecurityProcessing); 
    return this.identityTransformer;
  }
  
  public void setPrefixMapper(NamespacePrefixMapper paramNamespacePrefixMapper) { this.nsContext.setPrefixMapper(paramNamespacePrefixMapper); }
  
  public void startDocument(XmlOutput paramXmlOutput, boolean paramBoolean, String paramString1, String paramString2) throws IOException, SAXException, XMLStreamException {
    pushCoordinator();
    this.nsContext.reset();
    this.nse = this.nsContext.getCurrent();
    if (this.attachmentMarshaller != null && this.attachmentMarshaller.isXOPPackage())
      paramXmlOutput = new MTOMXmlOutput(paramXmlOutput); 
    this.out = paramXmlOutput;
    this.objectsWithId.clear();
    this.idReferencedObjects.clear();
    this.textHasAlreadyPrinted = false;
    this.seenRoot = false;
    this.schemaLocation = paramString1;
    this.noNsSchemaLocation = paramString2;
    this.fragment = paramBoolean;
    this.inlineBinaryFlag = false;
    this.expectedMimeType = null;
    this.cycleDetectionStack.reset();
    paramXmlOutput.startDocument(this, paramBoolean, this.knownUri2prefixIndexMap, this.nsContext);
  }
  
  public void endDocument() throws SAXException, IOException, XMLStreamException { this.out.endDocument(this.fragment); }
  
  public void close() throws SAXException, IOException, XMLStreamException {
    this.out = null;
    clearCurrentProperty();
    popCoordinator();
  }
  
  public void addInscopeBinding(String paramString1, String paramString2) throws SAXException, IOException, XMLStreamException { this.nsContext.put(paramString1, paramString2); }
  
  public String getXMIMEContentType() {
    String str = this.grammar.getXMIMEContentType(this.cycleDetectionStack.peek());
    return (str != null) ? str : ((this.expectedMimeType != null) ? this.expectedMimeType.toString() : null);
  }
  
  private void startElement() throws SAXException, IOException, XMLStreamException {
    this.nse = this.nse.push();
    if (!this.seenRoot) {
      if (this.grammar.getXmlNsSet() != null)
        for (XmlNs xmlNs : this.grammar.getXmlNsSet())
          this.nsContext.declareNsUri(xmlNs.namespaceURI(), (xmlNs.prefix() == null) ? "" : xmlNs.prefix(), (xmlNs.prefix() != null));  
      String[] arrayOfString1 = this.nameList.namespaceURIs;
      for (byte b = 0; b < arrayOfString1.length; b++)
        this.knownUri2prefixIndexMap[b] = this.nsContext.declareNsUri(arrayOfString1[b], null, this.nameList.nsUriCannotBeDefaulted[b]); 
      String[] arrayOfString2 = this.nsContext.getPrefixMapper().getPreDeclaredNamespaceUris();
      if (arrayOfString2 != null)
        for (String str : arrayOfString2) {
          if (str != null)
            this.nsContext.declareNsUri(str, null, false); 
        }  
      String[] arrayOfString3 = this.nsContext.getPrefixMapper().getPreDeclaredNamespaceUris2();
      if (arrayOfString3 != null)
        for (boolean bool = false; bool < arrayOfString3.length; bool += true) {
          String str1 = arrayOfString3[bool];
          String str2 = arrayOfString3[bool + true];
          if (str1 != null && str2 != null)
            this.nsContext.put(str2, str1); 
        }  
      if (this.schemaLocation != null || this.noNsSchemaLocation != null)
        this.nsContext.declareNsUri("http://www.w3.org/2001/XMLSchema-instance", "xsi", true); 
    } 
    this.nsContext.collectionMode = true;
    this.textHasAlreadyPrinted = false;
  }
  
  public MimeType setExpectedMimeType(MimeType paramMimeType) {
    MimeType mimeType = this.expectedMimeType;
    this.expectedMimeType = paramMimeType;
    return mimeType;
  }
  
  public boolean setInlineBinaryFlag(boolean paramBoolean) {
    boolean bool = this.inlineBinaryFlag;
    this.inlineBinaryFlag = paramBoolean;
    return bool;
  }
  
  public boolean getInlineBinaryFlag() { return this.inlineBinaryFlag; }
  
  public QName setSchemaType(QName paramQName) {
    QName qName = this.schemaType;
    this.schemaType = paramQName;
    return qName;
  }
  
  public QName getSchemaType() { return this.schemaType; }
  
  public void setObjectIdentityCycleDetection(boolean paramBoolean) { this.cycleDetectionStack.setUseIdentity(paramBoolean); }
  
  public boolean getObjectIdentityCycleDetection() { return this.cycleDetectionStack.getUseIdentity(); }
  
  void reconcileID() throws SAXException, IOException, XMLStreamException {
    this.idReferencedObjects.removeAll(this.objectsWithId);
    for (Object object : this.idReferencedObjects) {
      try {
        String str = getIdFromObject(object);
        reportError(new NotIdentifiableEventImpl(1, Messages.DANGLING_IDREF.format(new Object[] { str }, ), new ValidationEventLocatorImpl(object)));
      } catch (JAXBException jAXBException) {}
    } 
    this.idReferencedObjects.clear();
    this.objectsWithId.clear();
  }
  
  public boolean handleError(Exception paramException) { return handleError(paramException, this.cycleDetectionStack.peek(), null); }
  
  public boolean handleError(Exception paramException, Object paramObject, String paramString) { return handleEvent(new ValidationEventImpl(1, paramException.getMessage(), new ValidationEventLocatorExImpl(paramObject, paramString), paramException)); }
  
  public boolean handleEvent(ValidationEvent paramValidationEvent) {
    try {
      return this.marshaller.getEventHandler().handleEvent(paramValidationEvent);
    } catch (JAXBException jAXBException) {
      throw new Error(jAXBException);
    } 
  }
  
  private void reportMissingObjectError(String paramString) throws SAXException, IOException, XMLStreamException { reportError(new ValidationEventImpl(1, Messages.MISSING_OBJECT.format(new Object[] { paramString }, ), getCurrentLocation(paramString), new NullPointerException())); }
  
  public void errorMissingId(Object paramObject) throws IOException, XMLStreamException { reportError(new ValidationEventImpl(1, Messages.MISSING_ID.format(new Object[] { paramObject }, ), new ValidationEventLocatorImpl(paramObject))); }
  
  public ValidationEventLocator getCurrentLocation(String paramString) { return new ValidationEventLocatorExImpl(this.cycleDetectionStack.peek(), paramString); }
  
  protected ValidationEventLocator getLocation() { return getCurrentLocation(null); }
  
  public Property getCurrentProperty() { return (Property)this.currentProperty.get(); }
  
  public void clearCurrentProperty() throws SAXException, IOException, XMLStreamException {
    if (this.currentProperty != null)
      this.currentProperty.remove(); 
  }
  
  public static XMLSerializer getInstance() { return (XMLSerializer)Coordinator._getInstance(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\XMLSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */