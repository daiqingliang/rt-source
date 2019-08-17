package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.addressing.policy.AddressingPolicyMapConfigurator;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.policy.ModelGenerator;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.encoding.policy.MtomPolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapUtil;
import com.sun.xml.internal.ws.policy.PolicyMerger;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.jaxws.spi.PolicyMapConfigurator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelMarshaller;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class PolicyWSDLGeneratorExtension extends WSDLGeneratorExtension {
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyWSDLGeneratorExtension.class);
  
  private PolicyMap policyMap;
  
  private SEIModel seiModel;
  
  private final Collection<PolicySubject> subjects = new LinkedList();
  
  private final PolicyModelMarshaller marshaller = PolicyModelMarshaller.getXmlMarshaller(true);
  
  private final PolicyMerger merger = PolicyMerger.getMerger();
  
  public void start(WSDLGenExtnContext paramWSDLGenExtnContext) {
    LOGGER.entering();
    try {
      this.seiModel = paramWSDLGenExtnContext.getModel();
      PolicyMapConfigurator[] arrayOfPolicyMapConfigurator = loadConfigurators();
      PolicyMapExtender[] arrayOfPolicyMapExtender = new PolicyMapExtender[arrayOfPolicyMapConfigurator.length];
      for (byte b = 0; b < arrayOfPolicyMapConfigurator.length; b++)
        arrayOfPolicyMapExtender[b] = PolicyMapExtender.createPolicyMapExtender(); 
      this.policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(this.policyMap, paramWSDLGenExtnContext.getContainer(), paramWSDLGenExtnContext.getEndpointClass(), false, arrayOfPolicyMapExtender));
      if (this.policyMap == null) {
        LOGGER.fine(PolicyMessages.WSP_1019_CREATE_EMPTY_POLICY_MAP());
        this.policyMap = PolicyMap.createPolicyMap(Arrays.asList(arrayOfPolicyMapExtender));
      } 
      WSBinding wSBinding = paramWSDLGenExtnContext.getBinding();
      try {
        LinkedList linkedList = new LinkedList();
        for (byte b1 = 0; b1 < arrayOfPolicyMapConfigurator.length; b1++) {
          linkedList.addAll(arrayOfPolicyMapConfigurator[b1].update(this.policyMap, this.seiModel, wSBinding));
          arrayOfPolicyMapExtender[b1].disconnect();
        } 
        PolicyMapUtil.insertPolicies(this.policyMap, linkedList, this.seiModel.getServiceQName(), this.seiModel.getPortName());
      } catch (PolicyException policyException) {
        throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1017_MAP_UPDATE_FAILED(), policyException));
      } 
      TypedXmlWriter typedXmlWriter = paramWSDLGenExtnContext.getRoot();
      typedXmlWriter._namespace(NamespaceVersion.v1_2.toString(), NamespaceVersion.v1_2.getDefaultNamespacePrefix());
      typedXmlWriter._namespace(NamespaceVersion.v1_5.toString(), NamespaceVersion.v1_5.getDefaultNamespacePrefix());
      typedXmlWriter._namespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu");
    } finally {
      LOGGER.exiting();
    } 
  }
  
  public void addDefinitionsExtension(TypedXmlWriter paramTypedXmlWriter) {
    try {
      LOGGER.entering();
      if (this.policyMap == null) {
        LOGGER.fine(PolicyMessages.WSP_1009_NOT_MARSHALLING_ANY_POLICIES_POLICY_MAP_IS_NULL());
      } else {
        this.subjects.addAll(this.policyMap.getPolicySubjects());
        PolicyModelGenerator policyModelGenerator = ModelGenerator.getGenerator();
        HashSet hashSet = new HashSet();
        for (PolicySubject policySubject : this.subjects) {
          Policy policy;
          if (policySubject.getSubject() == null) {
            LOGGER.fine(PolicyMessages.WSP_1008_NOT_MARSHALLING_WSDL_SUBJ_NULL(policySubject));
            continue;
          } 
          try {
            policy = policySubject.getEffectivePolicy(this.merger);
          } catch (PolicyException policyException) {
            throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(policySubject.toString()), policyException));
          } 
          if (null == policy.getIdOrName() || hashSet.contains(policy.getIdOrName())) {
            LOGGER.fine(PolicyMessages.WSP_1016_POLICY_ID_NULL_OR_DUPLICATE(policy));
            continue;
          } 
          try {
            PolicySourceModel policySourceModel = policyModelGenerator.translate(policy);
            this.marshaller.marshal(policySourceModel, paramTypedXmlWriter);
          } catch (PolicyException policyException) {
            throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1018_FAILED_TO_MARSHALL_POLICY(policy.getIdOrName()), policyException));
          } 
          hashSet.add(policy.getIdOrName());
        } 
      } 
    } finally {
      LOGGER.exiting();
    } 
  }
  
  public void addServiceExtension(TypedXmlWriter paramTypedXmlWriter) {
    LOGGER.entering();
    String str = (null == this.seiModel) ? null : this.seiModel.getServiceQName().getLocalPart();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLService.class, ScopeType.SERVICE, str);
    LOGGER.exiting();
  }
  
  public void addPortExtension(TypedXmlWriter paramTypedXmlWriter) {
    LOGGER.entering();
    String str = (null == this.seiModel) ? null : this.seiModel.getPortName().getLocalPart();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLPort.class, ScopeType.ENDPOINT, str);
    LOGGER.exiting();
  }
  
  public void addPortTypeExtension(TypedXmlWriter paramTypedXmlWriter) {
    LOGGER.entering();
    String str = (null == this.seiModel) ? null : this.seiModel.getPortTypeName().getLocalPart();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType.class, ScopeType.ENDPOINT, str);
    LOGGER.exiting();
  }
  
  public void addBindingExtension(TypedXmlWriter paramTypedXmlWriter) {
    LOGGER.entering();
    QName qName = (null == this.seiModel) ? null : this.seiModel.getBoundPortTypeName();
    selectAndProcessBindingSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType.class, ScopeType.ENDPOINT, qName);
    LOGGER.exiting();
  }
  
  public void addOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    LOGGER.entering();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation.class, ScopeType.OPERATION, (String)null);
    LOGGER.exiting();
  }
  
  public void addBindingOperationExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    LOGGER.entering();
    QName qName = (paramJavaMethod == null) ? null : new QName(paramJavaMethod.getOwner().getTargetNamespace(), paramJavaMethod.getOperationName());
    selectAndProcessBindingSubject(paramTypedXmlWriter, WSDLBoundOperation.class, ScopeType.OPERATION, qName);
    LOGGER.exiting();
  }
  
  public void addInputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    LOGGER.entering();
    String str = (null == paramJavaMethod) ? null : paramJavaMethod.getRequestMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage.class, ScopeType.INPUT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addOutputMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    LOGGER.entering();
    String str = (null == paramJavaMethod) ? null : paramJavaMethod.getResponseMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage.class, ScopeType.OUTPUT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addFaultMessageExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    LOGGER.entering();
    String str = (null == paramCheckedException) ? null : paramCheckedException.getMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage.class, ScopeType.FAULT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    LOGGER.entering();
    String str = (null == paramJavaMethod) ? null : paramJavaMethod.getRequestMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLInput.class, ScopeType.INPUT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    LOGGER.entering();
    String str = (null == paramJavaMethod) ? null : paramJavaMethod.getResponseMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput.class, ScopeType.OUTPUT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    LOGGER.entering();
    String str = (null == paramCheckedException) ? null : paramCheckedException.getMessageName();
    selectAndProcessSubject(paramTypedXmlWriter, com.sun.xml.internal.ws.api.model.wsdl.WSDLFault.class, ScopeType.FAULT_MESSAGE, str);
    LOGGER.exiting();
  }
  
  public void addBindingOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    LOGGER.entering();
    QName qName = new QName(paramJavaMethod.getOwner().getTargetNamespace(), paramJavaMethod.getOperationName());
    selectAndProcessBindingSubject(paramTypedXmlWriter, WSDLBoundOperation.class, ScopeType.INPUT_MESSAGE, qName);
    LOGGER.exiting();
  }
  
  public void addBindingOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) {
    LOGGER.entering();
    QName qName = new QName(paramJavaMethod.getOwner().getTargetNamespace(), paramJavaMethod.getOperationName());
    selectAndProcessBindingSubject(paramTypedXmlWriter, WSDLBoundOperation.class, ScopeType.OUTPUT_MESSAGE, qName);
    LOGGER.exiting();
  }
  
  public void addBindingOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    LOGGER.entering(new Object[] { paramTypedXmlWriter, paramJavaMethod, paramCheckedException });
    if (this.subjects != null)
      for (PolicySubject policySubject : this.subjects) {
        if (this.policyMap.isFaultMessageSubject(policySubject)) {
          Object object = policySubject.getSubject();
          if (object != null) {
            String str = (paramCheckedException == null) ? null : paramCheckedException.getMessageName();
            if (str == null)
              writePolicyOrReferenceIt(policySubject, paramTypedXmlWriter); 
            if (WSDLBoundFaultContainer.class.isInstance(object)) {
              WSDLBoundFaultContainer wSDLBoundFaultContainer = (WSDLBoundFaultContainer)object;
              WSDLBoundFault wSDLBoundFault = wSDLBoundFaultContainer.getBoundFault();
              WSDLBoundOperation wSDLBoundOperation = wSDLBoundFaultContainer.getBoundOperation();
              if (str.equals(wSDLBoundFault.getName()) && wSDLBoundOperation.getName().getLocalPart().equals(paramJavaMethod.getOperationName()))
                writePolicyOrReferenceIt(policySubject, paramTypedXmlWriter); 
              continue;
            } 
            if (WsdlBindingSubject.class.isInstance(object)) {
              WsdlBindingSubject wsdlBindingSubject = (WsdlBindingSubject)object;
              if (wsdlBindingSubject.getMessageType() == WsdlBindingSubject.WsdlMessageType.FAULT && paramCheckedException.getOwner().getTargetNamespace().equals(wsdlBindingSubject.getName().getNamespaceURI()) && str.equals(wsdlBindingSubject.getName().getLocalPart()))
                writePolicyOrReferenceIt(policySubject, paramTypedXmlWriter); 
            } 
          } 
        } 
      }  
    LOGGER.exiting();
  }
  
  private void selectAndProcessSubject(TypedXmlWriter paramTypedXmlWriter, Class paramClass, ScopeType paramScopeType, QName paramQName) {
    LOGGER.entering(new Object[] { paramTypedXmlWriter, paramClass, paramScopeType, paramQName });
    if (paramQName == null) {
      selectAndProcessSubject(paramTypedXmlWriter, paramClass, paramScopeType, (String)null);
    } else {
      if (this.subjects != null)
        for (PolicySubject policySubject : this.subjects) {
          if (paramQName.equals(policySubject.getSubject()))
            writePolicyOrReferenceIt(policySubject, paramTypedXmlWriter); 
        }  
      selectAndProcessSubject(paramTypedXmlWriter, paramClass, paramScopeType, paramQName.getLocalPart());
    } 
    LOGGER.exiting();
  }
  
  private void selectAndProcessBindingSubject(TypedXmlWriter paramTypedXmlWriter, Class paramClass, ScopeType paramScopeType, QName paramQName) {
    LOGGER.entering(new Object[] { paramTypedXmlWriter, paramClass, paramScopeType, paramQName });
    if (this.subjects != null && paramQName != null)
      for (PolicySubject policySubject : this.subjects) {
        if (policySubject.getSubject() instanceof WsdlBindingSubject) {
          WsdlBindingSubject wsdlBindingSubject = (WsdlBindingSubject)policySubject.getSubject();
          if (paramQName.equals(wsdlBindingSubject.getName()))
            writePolicyOrReferenceIt(policySubject, paramTypedXmlWriter); 
        } 
      }  
    selectAndProcessSubject(paramTypedXmlWriter, paramClass, paramScopeType, paramQName);
    LOGGER.exiting();
  }
  
  private void selectAndProcessSubject(TypedXmlWriter paramTypedXmlWriter, Class paramClass, ScopeType paramScopeType, String paramString) {
    LOGGER.entering(new Object[] { paramTypedXmlWriter, paramClass, paramScopeType, paramString });
    if (this.subjects != null)
      for (PolicySubject policySubject : this.subjects) {
        if (isCorrectType(this.policyMap, policySubject, paramScopeType)) {
          Object object = policySubject.getSubject();
          if (object != null && paramClass.isInstance(object)) {
            if (null == paramString) {
              writePolicyOrReferenceIt(policySubject, paramTypedXmlWriter);
              continue;
            } 
            try {
              Method method = paramClass.getDeclaredMethod("getName", new Class[0]);
              if (stringEqualsToStringOrQName(paramString, method.invoke(object, new Object[0])))
                writePolicyOrReferenceIt(policySubject, paramTypedXmlWriter); 
            } catch (NoSuchMethodException noSuchMethodException) {
              throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(paramClass.getName(), paramString), noSuchMethodException));
            } catch (IllegalAccessException illegalAccessException) {
              throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(paramClass.getName(), paramString), illegalAccessException));
            } catch (InvocationTargetException invocationTargetException) {
              throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1003_UNABLE_TO_CHECK_ELEMENT_NAME(paramClass.getName(), paramString), invocationTargetException));
            } 
          } 
        } 
      }  
    LOGGER.exiting();
  }
  
  private static boolean isCorrectType(PolicyMap paramPolicyMap, PolicySubject paramPolicySubject, ScopeType paramScopeType) {
    switch (paramScopeType) {
      case OPERATION:
        return (!paramPolicyMap.isInputMessageSubject(paramPolicySubject) && !paramPolicyMap.isOutputMessageSubject(paramPolicySubject) && !paramPolicyMap.isFaultMessageSubject(paramPolicySubject));
      case INPUT_MESSAGE:
        return paramPolicyMap.isInputMessageSubject(paramPolicySubject);
      case OUTPUT_MESSAGE:
        return paramPolicyMap.isOutputMessageSubject(paramPolicySubject);
      case FAULT_MESSAGE:
        return paramPolicyMap.isFaultMessageSubject(paramPolicySubject);
    } 
    return true;
  }
  
  private boolean stringEqualsToStringOrQName(String paramString, Object paramObject) { return (paramObject instanceof QName) ? paramString.equals(((QName)paramObject).getLocalPart()) : paramString.equals(paramObject); }
  
  private void writePolicyOrReferenceIt(PolicySubject paramPolicySubject, TypedXmlWriter paramTypedXmlWriter) {
    Policy policy;
    try {
      policy = paramPolicySubject.getEffectivePolicy(this.merger);
    } catch (PolicyException policyException) {
      throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1011_FAILED_TO_RETRIEVE_EFFECTIVE_POLICY_FOR_SUBJECT(paramPolicySubject.toString()), policyException));
    } 
    if (policy != null)
      if (null == policy.getIdOrName()) {
        PolicyModelGenerator policyModelGenerator = ModelGenerator.getGenerator();
        try {
          PolicySourceModel policySourceModel = policyModelGenerator.translate(policy);
          this.marshaller.marshal(policySourceModel, paramTypedXmlWriter);
        } catch (PolicyException policyException) {
          throw (WebServiceException)LOGGER.logSevereException(new WebServiceException(PolicyMessages.WSP_1002_UNABLE_TO_MARSHALL_POLICY_OR_POLICY_REFERENCE(), policyException));
        } 
      } else {
        TypedXmlWriter typedXmlWriter = paramTypedXmlWriter._element(policy.getNamespaceVersion().asQName(XmlToken.PolicyReference), TypedXmlWriter.class);
        typedXmlWriter._attribute(XmlToken.Uri.toString(), '#' + policy.getIdOrName());
      }  
  }
  
  private PolicyMapConfigurator[] loadConfigurators() {
    LinkedList linkedList = new LinkedList();
    linkedList.add(new AddressingPolicyMapConfigurator());
    linkedList.add(new MtomPolicyMapConfigurator());
    PolicyUtil.addServiceProviders(linkedList, PolicyMapConfigurator.class);
    return (PolicyMapConfigurator[])linkedList.toArray(new PolicyMapConfigurator[linkedList.size()]);
  }
  
  enum ScopeType {
    SERVICE, ENDPOINT, OPERATION, INPUT_MESSAGE, OUTPUT_MESSAGE, FAULT_MESSAGE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\PolicyWSDLGeneratorExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */