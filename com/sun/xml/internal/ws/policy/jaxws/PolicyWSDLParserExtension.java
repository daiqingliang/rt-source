package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModelContext;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;

public final class PolicyWSDLParserExtension extends WSDLParserExtension {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyWSDLParserExtension.class);
  
  private static final StringBuffer AnonymnousPolicyIdPrefix = new StringBuffer("#__anonymousPolicy__ID");
  
  private int anonymousPoliciesCount;
  
  private final SafePolicyReader policyReader = new SafePolicyReader();
  
  private SafePolicyReader.PolicyRecord expandQueueHead = null;
  
  private Map<String, SafePolicyReader.PolicyRecord> policyRecordsPassedBy = null;
  
  private Map<String, PolicySourceModel> anonymousPolicyModels = null;
  
  private List<String> unresolvedUris = null;
  
  private final LinkedList<String> urisNeeded = new LinkedList();
  
  private final Map<String, PolicySourceModel> modelsNeeded = new HashMap();
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4ServiceMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4PortMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4PortTypeMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BoundOperationMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4OperationMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4MessageMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4InputMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4OutputMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4FaultMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingInputOpMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingOutputOpMap = null;
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> handlers4BindingFaultOpMap = null;
  
  private PolicyMapBuilder policyBuilder = new PolicyMapBuilder();
  
  private boolean isPolicyProcessed(String paramString) { return this.modelsNeeded.containsKey(paramString); }
  
  private void addNewPolicyNeeded(String paramString, PolicySourceModel paramPolicySourceModel) {
    if (!this.modelsNeeded.containsKey(paramString)) {
      this.modelsNeeded.put(paramString, paramPolicySourceModel);
      this.urisNeeded.addFirst(paramString);
    } 
  }
  
  private Map<String, PolicySourceModel> getPolicyModels() { return this.modelsNeeded; }
  
  private Map<String, SafePolicyReader.PolicyRecord> getPolicyRecordsPassedBy() {
    if (null == this.policyRecordsPassedBy)
      this.policyRecordsPassedBy = new HashMap(); 
    return this.policyRecordsPassedBy;
  }
  
  private Map<String, PolicySourceModel> getAnonymousPolicyModels() {
    if (null == this.anonymousPolicyModels)
      this.anonymousPolicyModels = new HashMap(); 
    return this.anonymousPolicyModels;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4ServiceMap() {
    if (null == this.handlers4ServiceMap)
      this.handlers4ServiceMap = new HashMap(); 
    return this.handlers4ServiceMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4PortMap() {
    if (null == this.handlers4PortMap)
      this.handlers4PortMap = new HashMap(); 
    return this.handlers4PortMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4PortTypeMap() {
    if (null == this.handlers4PortTypeMap)
      this.handlers4PortTypeMap = new HashMap(); 
    return this.handlers4PortTypeMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingMap() {
    if (null == this.handlers4BindingMap)
      this.handlers4BindingMap = new HashMap(); 
    return this.handlers4BindingMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4OperationMap() {
    if (null == this.handlers4OperationMap)
      this.handlers4OperationMap = new HashMap(); 
    return this.handlers4OperationMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BoundOperationMap() {
    if (null == this.handlers4BoundOperationMap)
      this.handlers4BoundOperationMap = new HashMap(); 
    return this.handlers4BoundOperationMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4MessageMap() {
    if (null == this.handlers4MessageMap)
      this.handlers4MessageMap = new HashMap(); 
    return this.handlers4MessageMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4InputMap() {
    if (null == this.handlers4InputMap)
      this.handlers4InputMap = new HashMap(); 
    return this.handlers4InputMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4OutputMap() {
    if (null == this.handlers4OutputMap)
      this.handlers4OutputMap = new HashMap(); 
    return this.handlers4OutputMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4FaultMap() {
    if (null == this.handlers4FaultMap)
      this.handlers4FaultMap = new HashMap(); 
    return this.handlers4FaultMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingInputOpMap() {
    if (null == this.handlers4BindingInputOpMap)
      this.handlers4BindingInputOpMap = new HashMap(); 
    return this.handlers4BindingInputOpMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingOutputOpMap() {
    if (null == this.handlers4BindingOutputOpMap)
      this.handlers4BindingOutputOpMap = new HashMap(); 
    return this.handlers4BindingOutputOpMap;
  }
  
  private Map<WSDLObject, Collection<PolicyRecordHandler>> getHandlers4BindingFaultOpMap() {
    if (null == this.handlers4BindingFaultOpMap)
      this.handlers4BindingFaultOpMap = new HashMap(); 
    return this.handlers4BindingFaultOpMap;
  }
  
  private List<String> getUnresolvedUris(boolean paramBoolean) {
    if (null == this.unresolvedUris || paramBoolean)
      this.unresolvedUris = new LinkedList(); 
    return this.unresolvedUris;
  }
  
  private void policyRecToExpandQueue(SafePolicyReader.PolicyRecord paramPolicyRecord) {
    if (null == this.expandQueueHead) {
      this.expandQueueHead = paramPolicyRecord;
    } else {
      this.expandQueueHead = this.expandQueueHead.insert(paramPolicyRecord);
    } 
  }
  
  private PolicyRecordHandler readSinglePolicy(SafePolicyReader.PolicyRecord paramPolicyRecord, boolean paramBoolean) {
    PolicyRecordHandler policyRecordHandler = null;
    String str = paramPolicyRecord.policyModel.getPolicyId();
    if (str == null)
      str = paramPolicyRecord.policyModel.getPolicyName(); 
    if (str != null) {
      policyRecordHandler = new PolicyRecordHandler(HandlerType.PolicyUri, paramPolicyRecord.getUri());
      getPolicyRecordsPassedBy().put(paramPolicyRecord.getUri(), paramPolicyRecord);
      policyRecToExpandQueue(paramPolicyRecord);
    } else if (paramBoolean) {
      String str1 = AnonymnousPolicyIdPrefix.append(this.anonymousPoliciesCount++).toString();
      policyRecordHandler = new PolicyRecordHandler(HandlerType.AnonymousPolicyId, str1);
      getAnonymousPolicyModels().put(str1, paramPolicyRecord.policyModel);
      if (null != paramPolicyRecord.unresolvedURIs)
        getUnresolvedUris(false).addAll(paramPolicyRecord.unresolvedURIs); 
    } 
    return policyRecordHandler;
  }
  
  private void addHandlerToMap(Map<WSDLObject, Collection<PolicyRecordHandler>> paramMap, WSDLObject paramWSDLObject, PolicyRecordHandler paramPolicyRecordHandler) {
    if (paramMap.containsKey(paramWSDLObject)) {
      ((Collection)paramMap.get(paramWSDLObject)).add(paramPolicyRecordHandler);
    } else {
      LinkedList linkedList = new LinkedList();
      linkedList.add(paramPolicyRecordHandler);
      paramMap.put(paramWSDLObject, linkedList);
    } 
  }
  
  private String getBaseUrl(String paramString) {
    if (null == paramString)
      return null; 
    int i = paramString.indexOf('#');
    return (i == -1) ? paramString : paramString.substring(0, i);
  }
  
  private void processReferenceUri(String paramString, WSDLObject paramWSDLObject, XMLStreamReader paramXMLStreamReader, Map<WSDLObject, Collection<PolicyRecordHandler>> paramMap) {
    if (null == paramString || paramString.length() == 0)
      return; 
    if ('#' != paramString.charAt(0))
      getUnresolvedUris(false).add(paramString); 
    addHandlerToMap(paramMap, paramWSDLObject, new PolicyRecordHandler(HandlerType.PolicyUri, SafePolicyReader.relativeToAbsoluteUrl(paramString, paramXMLStreamReader.getLocation().getSystemId())));
  }
  
  private boolean processSubelement(WSDLObject paramWSDLObject, XMLStreamReader paramXMLStreamReader, Map<WSDLObject, Collection<PolicyRecordHandler>> paramMap) {
    if (NamespaceVersion.resolveAsToken(paramXMLStreamReader.getName()) == XmlToken.PolicyReference) {
      processReferenceUri(this.policyReader.readPolicyReferenceElement(paramXMLStreamReader), paramWSDLObject, paramXMLStreamReader, paramMap);
      return true;
    } 
    if (NamespaceVersion.resolveAsToken(paramXMLStreamReader.getName()) == XmlToken.Policy) {
      PolicyRecordHandler policyRecordHandler = readSinglePolicy(this.policyReader.readPolicyElement(paramXMLStreamReader, (null == paramXMLStreamReader.getLocation().getSystemId()) ? "" : paramXMLStreamReader.getLocation().getSystemId()), true);
      if (null != policyRecordHandler)
        addHandlerToMap(paramMap, paramWSDLObject, policyRecordHandler); 
      return true;
    } 
    return false;
  }
  
  private void processAttributes(WSDLObject paramWSDLObject, XMLStreamReader paramXMLStreamReader, Map<WSDLObject, Collection<PolicyRecordHandler>> paramMap) {
    String[] arrayOfString = getPolicyURIsFromAttr(paramXMLStreamReader);
    if (null != arrayOfString)
      for (String str : arrayOfString)
        processReferenceUri(str, paramWSDLObject, paramXMLStreamReader, paramMap);  
  }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLPort, paramXMLStreamReader, getHandlers4PortMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portAttributes(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLPort, paramXMLStreamReader, getHandlers4PortMap());
    LOGGER.exiting();
  }
  
  public boolean serviceElements(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLService, paramXMLStreamReader, getHandlers4ServiceMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void serviceAttributes(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLService, paramXMLStreamReader, getHandlers4ServiceMap());
    LOGGER.exiting();
  }
  
  public boolean definitionsElements(XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    if (NamespaceVersion.resolveAsToken(paramXMLStreamReader.getName()) == XmlToken.Policy) {
      readSinglePolicy(this.policyReader.readPolicyElement(paramXMLStreamReader, (null == paramXMLStreamReader.getLocation().getSystemId()) ? "" : paramXMLStreamReader.getLocation().getSystemId()), false);
      LOGGER.exiting();
      return true;
    } 
    LOGGER.exiting();
    return false;
  }
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundPortType, paramXMLStreamReader, getHandlers4BindingMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void bindingAttributes(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundPortType, paramXMLStreamReader, getHandlers4BindingMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeElements(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLPortType, paramXMLStreamReader, getHandlers4PortTypeMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeAttributes(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLPortType, paramXMLStreamReader, getHandlers4PortTypeMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeOperationElements(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLOperation, paramXMLStreamReader, getHandlers4OperationMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeOperationAttributes(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLOperation, paramXMLStreamReader, getHandlers4OperationMap());
    LOGGER.exiting();
  }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BoundOperationMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void bindingOperationAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BoundOperationMap());
    LOGGER.exiting();
  }
  
  public boolean messageElements(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLMessage, paramXMLStreamReader, getHandlers4MessageMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void messageAttributes(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLMessage, paramXMLStreamReader, getHandlers4MessageMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeOperationInputElements(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLInput, paramXMLStreamReader, getHandlers4InputMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLInput, paramXMLStreamReader, getHandlers4InputMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeOperationOutputElements(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLOutput, paramXMLStreamReader, getHandlers4OutputMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLOutput, paramXMLStreamReader, getHandlers4OutputMap());
    LOGGER.exiting();
  }
  
  public boolean portTypeOperationFaultElements(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLFault, paramXMLStreamReader, getHandlers4FaultMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLFault, paramXMLStreamReader, getHandlers4FaultMap());
    LOGGER.exiting();
  }
  
  public boolean bindingOperationInputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BindingInputOpMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void bindingOperationInputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BindingInputOpMap());
    LOGGER.exiting();
  }
  
  public boolean bindingOperationOutputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BindingOutputOpMap());
    LOGGER.exiting();
    return bool;
  }
  
  public void bindingOperationOutputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader, getHandlers4BindingOutputOpMap());
    LOGGER.exiting();
  }
  
  public boolean bindingOperationFaultElements(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    boolean bool = processSubelement(paramEditableWSDLBoundFault, paramXMLStreamReader, getHandlers4BindingFaultOpMap());
    LOGGER.exiting(Boolean.valueOf(bool));
    return bool;
  }
  
  public void bindingOperationFaultAttributes(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader) {
    LOGGER.entering();
    processAttributes(paramEditableWSDLBoundFault, paramXMLStreamReader, getHandlers4BindingFaultOpMap());
    LOGGER.exiting();
  }
  
  private PolicyMapBuilder getPolicyMapBuilder() {
    if (null == this.policyBuilder)
      this.policyBuilder = new PolicyMapBuilder(); 
    return this.policyBuilder;
  }
  
  private Collection<String> getPolicyURIs(Collection<PolicyRecordHandler> paramCollection, PolicySourceModelContext paramPolicySourceModelContext) throws PolicyException {
    ArrayList arrayList = new ArrayList(paramCollection.size());
    for (PolicyRecordHandler policyRecordHandler : paramCollection) {
      String str = policyRecordHandler.handler;
      if (HandlerType.AnonymousPolicyId == policyRecordHandler.type) {
        PolicySourceModel policySourceModel = (PolicySourceModel)getAnonymousPolicyModels().get(str);
        policySourceModel.expand(paramPolicySourceModelContext);
        while (getPolicyModels().containsKey(str))
          str = AnonymnousPolicyIdPrefix.append(this.anonymousPoliciesCount++).toString(); 
        getPolicyModels().put(str, policySourceModel);
      } 
      arrayList.add(str);
    } 
    return arrayList;
  }
  
  private boolean readExternalFile(String paramString) {
    inputStream = null;
    xMLStreamReader = null;
    try {
      URL uRL = new URL(paramString);
      inputStream = uRL.openStream();
      xMLStreamReader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(inputStream);
      while (xMLStreamReader.hasNext()) {
        if (xMLStreamReader.isStartElement() && NamespaceVersion.resolveAsToken(xMLStreamReader.getName()) == XmlToken.Policy)
          readSinglePolicy(this.policyReader.readPolicyElement(xMLStreamReader, paramString), false); 
        xMLStreamReader.next();
      } 
      return true;
    } catch (IOException iOException) {
      return false;
    } catch (XMLStreamException xMLStreamException) {
      return false;
    } finally {
      PolicyUtils.IO.closeResource(xMLStreamReader);
      PolicyUtils.IO.closeResource(inputStream);
    } 
  }
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext) {
    LOGGER.entering(new Object[] { paramWSDLParserExtensionContext });
    if (null != this.expandQueueHead) {
      List list = getUnresolvedUris(false);
      getUnresolvedUris(true);
      LinkedList linkedList = new LinkedList();
      for (SafePolicyReader.PolicyRecord policyRecord = this.expandQueueHead; null != policyRecord; policyRecord = policyRecord.next)
        linkedList.addFirst(policyRecord.getUri()); 
      getUnresolvedUris(false).addAll(linkedList);
      this.expandQueueHead = null;
      getUnresolvedUris(false).addAll(list);
    } 
    while (!getUnresolvedUris(false).isEmpty()) {
      List list = getUnresolvedUris(false);
      getUnresolvedUris(true);
      for (String str : list) {
        if (!isPolicyProcessed(str)) {
          SafePolicyReader.PolicyRecord policyRecord = (SafePolicyReader.PolicyRecord)getPolicyRecordsPassedBy().get(str);
          if (null == policyRecord) {
            if (this.policyReader.getUrlsRead().contains(getBaseUrl(str))) {
              LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1014_CAN_NOT_FIND_POLICY(str)));
              continue;
            } 
            if (readExternalFile(getBaseUrl(str)))
              getUnresolvedUris(false).add(str); 
            continue;
          } 
          if (null != policyRecord.unresolvedURIs)
            getUnresolvedUris(false).addAll(policyRecord.unresolvedURIs); 
          addNewPolicyNeeded(str, policyRecord.policyModel);
        } 
      } 
    } 
    PolicySourceModelContext policySourceModelContext = PolicySourceModelContext.createContext();
    for (String str : this.urisNeeded) {
      PolicySourceModel policySourceModel = (PolicySourceModel)this.modelsNeeded.get(str);
      try {
        policySourceModel.expand(policySourceModelContext);
        policySourceModelContext.addModel(new URI(str), policySourceModel);
      } catch (URISyntaxException uRISyntaxException) {
        LOGGER.logSevereException(uRISyntaxException);
      } catch (PolicyException policyException) {
        LOGGER.logSevereException(policyException);
      } 
    } 
    try {
      HashSet hashSet = new HashSet();
      for (EditableWSDLService editableWSDLService : paramWSDLParserExtensionContext.getWSDLModel().getServices().values()) {
        if (getHandlers4ServiceMap().containsKey(editableWSDLService))
          getPolicyMapBuilder().registerHandler(new BuilderHandlerServiceScope(getPolicyURIs((Collection)getHandlers4ServiceMap().get(editableWSDLService), policySourceModelContext), getPolicyModels(), editableWSDLService, editableWSDLService.getName())); 
        for (EditableWSDLPort editableWSDLPort : editableWSDLService.getPorts()) {
          if (getHandlers4PortMap().containsKey(editableWSDLPort))
            getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(getPolicyURIs((Collection)getHandlers4PortMap().get(editableWSDLPort), policySourceModelContext), getPolicyModels(), editableWSDLPort, editableWSDLPort.getOwner().getName(), editableWSDLPort.getName())); 
          if (null != editableWSDLPort.getBinding()) {
            if (getHandlers4BindingMap().containsKey(editableWSDLPort.getBinding()))
              getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(getPolicyURIs((Collection)getHandlers4BindingMap().get(editableWSDLPort.getBinding()), policySourceModelContext), getPolicyModels(), editableWSDLPort.getBinding(), editableWSDLService.getName(), editableWSDLPort.getName())); 
            if (getHandlers4PortTypeMap().containsKey(editableWSDLPort.getBinding().getPortType()))
              getPolicyMapBuilder().registerHandler(new BuilderHandlerEndpointScope(getPolicyURIs((Collection)getHandlers4PortTypeMap().get(editableWSDLPort.getBinding().getPortType()), policySourceModelContext), getPolicyModels(), editableWSDLPort.getBinding().getPortType(), editableWSDLService.getName(), editableWSDLPort.getName())); 
            for (EditableWSDLBoundOperation editableWSDLBoundOperation : editableWSDLPort.getBinding().getBindingOperations()) {
              EditableWSDLOperation editableWSDLOperation = editableWSDLBoundOperation.getOperation();
              QName qName = new QName(editableWSDLBoundOperation.getBoundPortType().getName().getNamespaceURI(), editableWSDLBoundOperation.getName().getLocalPart());
              if (getHandlers4BoundOperationMap().containsKey(editableWSDLBoundOperation))
                getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(getPolicyURIs((Collection)getHandlers4BoundOperationMap().get(editableWSDLBoundOperation), policySourceModelContext), getPolicyModels(), editableWSDLBoundOperation, editableWSDLService.getName(), editableWSDLPort.getName(), qName)); 
              if (getHandlers4OperationMap().containsKey(editableWSDLOperation))
                getPolicyMapBuilder().registerHandler(new BuilderHandlerOperationScope(getPolicyURIs((Collection)getHandlers4OperationMap().get(editableWSDLOperation), policySourceModelContext), getPolicyModels(), editableWSDLOperation, editableWSDLService.getName(), editableWSDLPort.getName(), qName)); 
              EditableWSDLInput editableWSDLInput = editableWSDLOperation.getInput();
              if (null != editableWSDLInput) {
                EditableWSDLMessage editableWSDLMessage = editableWSDLInput.getMessage();
                if (editableWSDLMessage != null && getHandlers4MessageMap().containsKey(editableWSDLMessage))
                  hashSet.add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4MessageMap().get(editableWSDLMessage), policySourceModelContext), getPolicyModels(), editableWSDLMessage, BuilderHandlerMessageScope.Scope.InputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, null)); 
              } 
              if (getHandlers4BindingInputOpMap().containsKey(editableWSDLBoundOperation))
                getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4BindingInputOpMap().get(editableWSDLBoundOperation), policySourceModelContext), getPolicyModels(), editableWSDLBoundOperation, BuilderHandlerMessageScope.Scope.InputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, null)); 
              if (null != editableWSDLInput && getHandlers4InputMap().containsKey(editableWSDLInput))
                getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4InputMap().get(editableWSDLInput), policySourceModelContext), getPolicyModels(), editableWSDLInput, BuilderHandlerMessageScope.Scope.InputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, null)); 
              EditableWSDLOutput editableWSDLOutput = editableWSDLOperation.getOutput();
              if (null != editableWSDLOutput) {
                EditableWSDLMessage editableWSDLMessage = editableWSDLOutput.getMessage();
                if (editableWSDLMessage != null && getHandlers4MessageMap().containsKey(editableWSDLMessage))
                  hashSet.add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4MessageMap().get(editableWSDLMessage), policySourceModelContext), getPolicyModels(), editableWSDLMessage, BuilderHandlerMessageScope.Scope.OutputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, null)); 
              } 
              if (getHandlers4BindingOutputOpMap().containsKey(editableWSDLBoundOperation))
                getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4BindingOutputOpMap().get(editableWSDLBoundOperation), policySourceModelContext), getPolicyModels(), editableWSDLBoundOperation, BuilderHandlerMessageScope.Scope.OutputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, null)); 
              if (null != editableWSDLOutput && getHandlers4OutputMap().containsKey(editableWSDLOutput))
                getPolicyMapBuilder().registerHandler(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4OutputMap().get(editableWSDLOutput), policySourceModelContext), getPolicyModels(), editableWSDLOutput, BuilderHandlerMessageScope.Scope.OutputMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, null)); 
              for (EditableWSDLBoundFault editableWSDLBoundFault : editableWSDLBoundOperation.getFaults()) {
                EditableWSDLFault editableWSDLFault = editableWSDLBoundFault.getFault();
                if (editableWSDLFault == null) {
                  LOGGER.warning(PolicyMessages.WSP_1021_FAULT_NOT_BOUND(editableWSDLBoundFault.getName()));
                  continue;
                } 
                EditableWSDLMessage editableWSDLMessage = editableWSDLFault.getMessage();
                QName qName1 = new QName(editableWSDLBoundOperation.getBoundPortType().getName().getNamespaceURI(), editableWSDLBoundFault.getName());
                if (editableWSDLMessage != null && getHandlers4MessageMap().containsKey(editableWSDLMessage))
                  hashSet.add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4MessageMap().get(editableWSDLMessage), policySourceModelContext), getPolicyModels(), new WSDLBoundFaultContainer(editableWSDLBoundFault, editableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, qName1)); 
                if (getHandlers4FaultMap().containsKey(editableWSDLFault))
                  hashSet.add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4FaultMap().get(editableWSDLFault), policySourceModelContext), getPolicyModels(), new WSDLBoundFaultContainer(editableWSDLBoundFault, editableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, qName1)); 
                if (getHandlers4BindingFaultOpMap().containsKey(editableWSDLBoundFault))
                  hashSet.add(new BuilderHandlerMessageScope(getPolicyURIs((Collection)getHandlers4BindingFaultOpMap().get(editableWSDLBoundFault), policySourceModelContext), getPolicyModels(), new WSDLBoundFaultContainer(editableWSDLBoundFault, editableWSDLBoundOperation), BuilderHandlerMessageScope.Scope.FaultMessageScope, editableWSDLService.getName(), editableWSDLPort.getName(), qName, qName1)); 
              } 
            } 
          } 
        } 
      } 
      for (BuilderHandlerMessageScope builderHandlerMessageScope : hashSet)
        getPolicyMapBuilder().registerHandler(builderHandlerMessageScope); 
    } catch (PolicyException policyException) {
      LOGGER.logSevereException(policyException);
    } 
    LOGGER.exiting();
  }
  
  public void postFinished(WSDLParserExtensionContext paramWSDLParserExtensionContext) {
    PolicyMap policyMap;
    EditableWSDLModel editableWSDLModel = paramWSDLParserExtensionContext.getWSDLModel();
    try {
      if (paramWSDLParserExtensionContext.isClientSide()) {
        policyMap = paramWSDLParserExtensionContext.getPolicyResolver().resolve(new PolicyResolver.ClientContext(this.policyBuilder.getPolicyMap(new com.sun.xml.internal.ws.policy.PolicyMapMutator[0]), paramWSDLParserExtensionContext.getContainer()));
      } else {
        policyMap = paramWSDLParserExtensionContext.getPolicyResolver().resolve(new PolicyResolver.ServerContext(this.policyBuilder.getPolicyMap(new com.sun.xml.internal.ws.policy.PolicyMapMutator[0]), paramWSDLParserExtensionContext.getContainer(), null, new com.sun.xml.internal.ws.policy.PolicyMapMutator[0]));
      } 
      editableWSDLModel.setPolicyMap(policyMap);
    } catch (PolicyException policyException) {
      LOGGER.logSevereException(policyException);
      throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1007_POLICY_EXCEPTION_WHILE_FINISHING_PARSING_WSDL(), policyException));
    } 
    try {
      PolicyUtil.configureModel(editableWSDLModel, policyMap);
    } catch (PolicyException policyException) {
      LOGGER.logSevereException(policyException);
      throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1012_FAILED_CONFIGURE_WSDL_MODEL(), policyException));
    } 
    LOGGER.exiting();
  }
  
  private String[] getPolicyURIsFromAttr(XMLStreamReader paramXMLStreamReader) {
    StringBuilder stringBuilder = new StringBuilder();
    for (NamespaceVersion namespaceVersion : NamespaceVersion.values()) {
      String str = paramXMLStreamReader.getAttributeValue(namespaceVersion.toString(), XmlToken.PolicyUris.toString());
      if (str != null)
        stringBuilder.append(str).append(" "); 
    } 
    return (stringBuilder.length() > 0) ? stringBuilder.toString().split("[\\n ]+") : null;
  }
  
  enum HandlerType {
    PolicyUri, AnonymousPolicyId;
  }
  
  static final class PolicyRecordHandler {
    String handler;
    
    PolicyWSDLParserExtension.HandlerType type;
    
    PolicyRecordHandler(PolicyWSDLParserExtension.HandlerType param1HandlerType, String param1String) {
      this.type = param1HandlerType;
      this.handler = param1String;
    }
    
    PolicyWSDLParserExtension.HandlerType getType() { return this.type; }
    
    String getHandler() { return this.handler; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\PolicyWSDLParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */