package com.sun.xml.internal.ws.model;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.soap.SOAPBindingImpl;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.wsdl.ActionBasedOperationSignature;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.xml.namespace.QName;
import javax.xml.ws.Action;
import javax.xml.ws.WebServiceException;

public final class JavaMethodImpl implements JavaMethod {
  private String inputAction = "";
  
  private String outputAction = "";
  
  private final List<CheckedExceptionImpl> exceptions = new ArrayList();
  
  private final Method method;
  
  final List<ParameterImpl> requestParams = new ArrayList();
  
  final List<ParameterImpl> responseParams = new ArrayList();
  
  private final List<ParameterImpl> unmReqParams = Collections.unmodifiableList(this.requestParams);
  
  private final List<ParameterImpl> unmResParams = Collections.unmodifiableList(this.responseParams);
  
  private SOAPBinding binding;
  
  private MEP mep;
  
  private QName operationName;
  
  private WSDLBoundOperation wsdlOperation;
  
  final AbstractSEIModelImpl owner;
  
  private final Method seiMethod;
  
  private QName requestPayloadName;
  
  private String soapAction;
  
  private static final Logger LOGGER = Logger.getLogger(JavaMethodImpl.class.getName());
  
  public JavaMethodImpl(AbstractSEIModelImpl paramAbstractSEIModelImpl, Method paramMethod1, Method paramMethod2, MetadataReader paramMetadataReader) {
    this.owner = paramAbstractSEIModelImpl;
    this.method = paramMethod1;
    this.seiMethod = paramMethod2;
    setWsaActions(paramMetadataReader);
  }
  
  private void setWsaActions(MetadataReader paramMetadataReader) {
    Action action = (paramMetadataReader != null) ? (Action)paramMetadataReader.getAnnotation(Action.class, this.seiMethod) : (Action)this.seiMethod.getAnnotation(Action.class);
    if (action != null) {
      this.inputAction = action.input();
      this.outputAction = action.output();
    } 
    WebMethod webMethod = (paramMetadataReader != null) ? (WebMethod)paramMetadataReader.getAnnotation(WebMethod.class, this.seiMethod) : (WebMethod)this.seiMethod.getAnnotation(WebMethod.class);
    this.soapAction = "";
    if (webMethod != null)
      this.soapAction = webMethod.action(); 
    if (!this.soapAction.equals(""))
      if (this.inputAction.equals("")) {
        this.inputAction = this.soapAction;
      } else if (!this.inputAction.equals(this.soapAction)) {
      
      }  
  }
  
  public ActionBasedOperationSignature getOperationSignature() {
    QName qName = getRequestPayloadName();
    if (qName == null)
      qName = new QName("", ""); 
    return new ActionBasedOperationSignature(getInputAction(), qName);
  }
  
  public SEIModel getOwner() { return this.owner; }
  
  public Method getMethod() { return this.method; }
  
  public Method getSEIMethod() { return this.seiMethod; }
  
  public MEP getMEP() { return this.mep; }
  
  void setMEP(MEP paramMEP) { this.mep = paramMEP; }
  
  public SOAPBinding getBinding() { return (this.binding == null) ? new SOAPBindingImpl() : this.binding; }
  
  void setBinding(SOAPBinding paramSOAPBinding) { this.binding = paramSOAPBinding; }
  
  public WSDLBoundOperation getOperation() { return this.wsdlOperation; }
  
  public void setOperationQName(QName paramQName) { this.operationName = paramQName; }
  
  public QName getOperationQName() { return (this.wsdlOperation != null) ? this.wsdlOperation.getName() : this.operationName; }
  
  public String getSOAPAction() { return (this.wsdlOperation != null) ? this.wsdlOperation.getSOAPAction() : this.soapAction; }
  
  public String getOperationName() { return this.operationName.getLocalPart(); }
  
  public String getRequestMessageName() { return getOperationName(); }
  
  public String getResponseMessageName() { return this.mep.isOneWay() ? null : (getOperationName() + "Response"); }
  
  public void setRequestPayloadName(QName paramQName) { this.requestPayloadName = paramQName; }
  
  @Nullable
  public QName getRequestPayloadName() { return (this.wsdlOperation != null) ? this.wsdlOperation.getRequestPayloadName() : this.requestPayloadName; }
  
  @Nullable
  public QName getResponsePayloadName() { return (this.mep == MEP.ONE_WAY) ? null : this.wsdlOperation.getResponsePayloadName(); }
  
  public List<ParameterImpl> getRequestParameters() { return this.unmReqParams; }
  
  public List<ParameterImpl> getResponseParameters() { return this.unmResParams; }
  
  void addParameter(ParameterImpl paramParameterImpl) {
    if (paramParameterImpl.isIN() || paramParameterImpl.isINOUT()) {
      assert !this.requestParams.contains(paramParameterImpl);
      this.requestParams.add(paramParameterImpl);
    } 
    if (paramParameterImpl.isOUT() || paramParameterImpl.isINOUT()) {
      assert !this.responseParams.contains(paramParameterImpl);
      this.responseParams.add(paramParameterImpl);
    } 
  }
  
  void addRequestParameter(ParameterImpl paramParameterImpl) {
    if (paramParameterImpl.isIN() || paramParameterImpl.isINOUT())
      this.requestParams.add(paramParameterImpl); 
  }
  
  void addResponseParameter(ParameterImpl paramParameterImpl) {
    if (paramParameterImpl.isOUT() || paramParameterImpl.isINOUT())
      this.responseParams.add(paramParameterImpl); 
  }
  
  public int getInputParametersCount() {
    int i = 0;
    for (ParameterImpl parameterImpl : this.requestParams) {
      if (parameterImpl.isWrapperStyle()) {
        i += ((WrapperParameter)parameterImpl).getWrapperChildren().size();
        continue;
      } 
      i++;
    } 
    for (ParameterImpl parameterImpl : this.responseParams) {
      if (parameterImpl.isWrapperStyle()) {
        for (ParameterImpl parameterImpl1 : ((WrapperParameter)parameterImpl).getWrapperChildren()) {
          if (!parameterImpl1.isResponse() && parameterImpl1.isOUT())
            i++; 
        } 
        continue;
      } 
      if (!parameterImpl.isResponse() && parameterImpl.isOUT())
        i++; 
    } 
    return i;
  }
  
  void addException(CheckedExceptionImpl paramCheckedExceptionImpl) {
    if (!this.exceptions.contains(paramCheckedExceptionImpl))
      this.exceptions.add(paramCheckedExceptionImpl); 
  }
  
  public CheckedExceptionImpl getCheckedException(Class paramClass) {
    for (CheckedExceptionImpl checkedExceptionImpl : this.exceptions) {
      if (checkedExceptionImpl.getExceptionClass() == paramClass)
        return checkedExceptionImpl; 
    } 
    return null;
  }
  
  public List<CheckedExceptionImpl> getCheckedExceptions() { return Collections.unmodifiableList(this.exceptions); }
  
  public String getInputAction() { return this.inputAction; }
  
  public String getOutputAction() { return this.outputAction; }
  
  public CheckedExceptionImpl getCheckedException(TypeReference paramTypeReference) {
    for (CheckedExceptionImpl checkedExceptionImpl : this.exceptions) {
      TypeInfo typeInfo = checkedExceptionImpl.getDetailType();
      if (typeInfo.tagName.equals(paramTypeReference.tagName) && typeInfo.type == paramTypeReference.type)
        return checkedExceptionImpl; 
    } 
    return null;
  }
  
  public boolean isAsync() { return this.mep.isAsync; }
  
  void freeze(WSDLPort paramWSDLPort) {
    this.wsdlOperation = paramWSDLPort.getBinding().get(new QName(paramWSDLPort.getBinding().getPortType().getName().getNamespaceURI(), getOperationName()));
    if (this.wsdlOperation == null)
      throw new WebServiceException("Method " + this.seiMethod.getName() + " is exposed as WebMethod, but there is no corresponding wsdl operation with name " + this.operationName + " in the wsdl:portType" + paramWSDLPort.getBinding().getPortType().getName()); 
    if (this.inputAction.equals("")) {
      this.inputAction = this.wsdlOperation.getOperation().getInput().getAction();
    } else if (!this.inputAction.equals(this.wsdlOperation.getOperation().getInput().getAction())) {
      LOGGER.warning("Input Action on WSDL operation " + this.wsdlOperation.getName().getLocalPart() + " and @Action on its associated Web Method " + this.seiMethod.getName() + " did not match and will cause problems in dispatching the requests");
    } 
    if (!this.mep.isOneWay()) {
      if (this.outputAction.equals(""))
        this.outputAction = this.wsdlOperation.getOperation().getOutput().getAction(); 
      for (CheckedExceptionImpl checkedExceptionImpl : this.exceptions) {
        if (checkedExceptionImpl.getFaultAction().equals("")) {
          QName qName = (checkedExceptionImpl.getDetailType()).tagName;
          WSDLFault wSDLFault = this.wsdlOperation.getOperation().getFault(qName);
          if (wSDLFault == null) {
            LOGGER.warning("Mismatch between Java model and WSDL model found, For wsdl operation " + this.wsdlOperation.getName() + ",There is no matching wsdl fault with detail QName " + (checkedExceptionImpl.getDetailType()).tagName);
            checkedExceptionImpl.setFaultAction(checkedExceptionImpl.getDefaultFaultAction());
            continue;
          } 
          checkedExceptionImpl.setFaultAction(wSDLFault.getAction());
        } 
      } 
    } 
  }
  
  final void fillTypes(List<TypeInfo> paramList) {
    fillTypes(this.requestParams, paramList);
    fillTypes(this.responseParams, paramList);
    for (CheckedExceptionImpl checkedExceptionImpl : this.exceptions)
      paramList.add(checkedExceptionImpl.getDetailType()); 
  }
  
  private void fillTypes(List<ParameterImpl> paramList1, List<TypeInfo> paramList2) {
    for (ParameterImpl parameterImpl : paramList1)
      parameterImpl.fillTypes(paramList2); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\JavaMethodImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */