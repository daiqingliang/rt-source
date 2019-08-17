package com.sun.xml.internal.ws.api.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.addressing.WsaTubeHelperImpl;
import com.sun.xml.internal.ws.addressing.v200408.WsaTubeHelperImpl;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.message.stream.OutboundStreamHeader;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.AddressingFeature;

public static final abstract enum AddressingVersion {
  W3C, MEMBER;
  
  public final String nsUri;
  
  public final String wsdlNsUri;
  
  public final EPR eprType;
  
  public final String policyNsUri;
  
  @NotNull
  public final String anonymousUri;
  
  @NotNull
  public final String noneUri;
  
  public final WSEndpointReference anonymousEpr;
  
  public final QName toTag;
  
  public final QName fromTag;
  
  public final QName replyToTag;
  
  public final QName faultToTag;
  
  public final QName actionTag;
  
  public final QName messageIDTag;
  
  public final QName relatesToTag;
  
  public final QName mapRequiredTag;
  
  public final QName actionMismatchTag;
  
  public final QName actionNotSupportedTag;
  
  public final String actionNotSupportedText;
  
  public final QName invalidMapTag;
  
  public final QName invalidCardinalityTag;
  
  public final QName invalidAddressTag;
  
  public final QName problemHeaderQNameTag;
  
  public final QName problemActionTag;
  
  public final QName faultDetailTag;
  
  public final QName fault_missingAddressInEpr;
  
  public final QName wsdlActionTag;
  
  public final QName wsdlExtensionTag;
  
  public final QName wsdlAnonymousTag;
  
  public final QName isReferenceParameterTag;
  
  private static final String EXTENDED_FAULT_NAMESPACE = "http://jax-ws.dev.java.net/addressing/fault";
  
  public static final String UNSET_OUTPUT_ACTION = "http://jax-ws.dev.java.net/addressing/output-action-not-set";
  
  public static final String UNSET_INPUT_ACTION = "http://jax-ws.dev.java.net/addressing/input-action-not-set";
  
  public static final QName fault_duplicateAddressInEpr;
  
  AddressingVersion(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, EPR paramEPR1, EPR paramEPR2) {
    this.nsUri = paramString1;
    this.wsdlNsUri = paramString4;
    this.policyNsUri = paramString5;
    this.anonymousUri = paramString6;
    this.noneUri = paramEPR1;
    this.toTag = new QName(paramString1, "To", paramString2);
    this.fromTag = new QName(paramString1, "From", paramString2);
    this.replyToTag = new QName(paramString1, "ReplyTo", paramString2);
    this.faultToTag = new QName(paramString1, "FaultTo", paramString2);
    this.actionTag = new QName(paramString1, "Action", paramString2);
    this.messageIDTag = new QName(paramString1, "MessageID", paramString2);
    this.relatesToTag = new QName(paramString1, "RelatesTo", paramString2);
    this.mapRequiredTag = new QName(paramString1, getMapRequiredLocalName(), paramString2);
    this.actionMismatchTag = new QName(paramString1, getActionMismatchLocalName(), paramString2);
    this.actionNotSupportedTag = new QName(paramString1, "ActionNotSupported", paramString2);
    this.actionNotSupportedText = "The \"%s\" cannot be processed at the receiver";
    this.invalidMapTag = new QName(paramString1, getInvalidMapLocalName(), paramString2);
    this.invalidAddressTag = new QName(paramString1, getInvalidAddressLocalName(), paramString2);
    this.invalidCardinalityTag = new QName(paramString1, getInvalidCardinalityLocalName(), paramString2);
    this.faultDetailTag = new QName(paramString1, "FaultDetail", paramString2);
    this.problemHeaderQNameTag = new QName(paramString1, "ProblemHeaderQName", paramString2);
    this.problemActionTag = new QName(paramString1, "ProblemAction", paramString2);
    this.fault_missingAddressInEpr = new QName(paramString1, "MissingAddressInEPR", paramString2);
    this.isReferenceParameterTag = new QName(paramString1, getIsReferenceParameterLocalName(), paramString2);
    this.wsdlActionTag = new QName(paramString4, "Action", paramString2);
    this.wsdlExtensionTag = new QName(paramString4, "UsingAddressing", paramString2);
    this.wsdlAnonymousTag = new QName(paramString4, getWsdlAnonymousLocalName(), paramString2);
    try {
      this.anonymousEpr = new WSEndpointReference(new ByteArrayInputStream(paramString3.getBytes("UTF-8")), this);
    } catch (XMLStreamException xMLStreamException) {
      throw new Error(xMLStreamException);
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new Error(unsupportedEncodingException);
    } 
    this.eprType = paramEPR2;
  }
  
  abstract String getActionMismatchLocalName();
  
  public static AddressingVersion fromNsUri(String paramString) { return paramString.equals(W3C.nsUri) ? W3C : (paramString.equals(MEMBER.nsUri) ? MEMBER : null); }
  
  @Nullable
  public static AddressingVersion fromBinding(WSBinding paramWSBinding) { return paramWSBinding.isFeatureEnabled(AddressingFeature.class) ? W3C : (paramWSBinding.isFeatureEnabled(MemberSubmissionAddressingFeature.class) ? MEMBER : null); }
  
  public static AddressingVersion fromPort(WSDLPort paramWSDLPort) {
    if (paramWSDLPort == null)
      return null; 
    WebServiceFeature webServiceFeature = paramWSDLPort.getFeature(AddressingFeature.class);
    if (webServiceFeature == null)
      webServiceFeature = paramWSDLPort.getFeature(MemberSubmissionAddressingFeature.class); 
    return (webServiceFeature == null) ? null : fromFeature(webServiceFeature);
  }
  
  public String getNsUri() { return this.nsUri; }
  
  public abstract boolean isReferenceParameter(String paramString);
  
  public abstract WsaTubeHelper getWsaHelper(WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding);
  
  public final String getNoneUri() { return this.noneUri; }
  
  public final String getAnonymousUri() { return this.anonymousUri; }
  
  public String getDefaultFaultAction() { return this.nsUri + "/fault"; }
  
  abstract String getMapRequiredLocalName();
  
  public abstract String getMapRequiredText();
  
  abstract String getInvalidAddressLocalName();
  
  abstract String getInvalidMapLocalName();
  
  public abstract String getInvalidMapText();
  
  abstract String getInvalidCardinalityLocalName();
  
  abstract String getWsdlAnonymousLocalName();
  
  public abstract String getPrefix();
  
  public abstract String getWsdlPrefix();
  
  public abstract Class<? extends WebServiceFeature> getFeatureClass();
  
  abstract Header createReferenceParameterHeader(XMLStreamBuffer paramXMLStreamBuffer, String paramString1, String paramString2);
  
  abstract String getIsReferenceParameterLocalName();
  
  public static AddressingVersion fromFeature(WebServiceFeature paramWebServiceFeature) { return paramWebServiceFeature.getID().equals("http://www.w3.org/2005/08/addressing/module") ? W3C : (paramWebServiceFeature.getID().equals("http://java.sun.com/xml/ns/jaxws/2004/08/addressing") ? MEMBER : null); }
  
  @NotNull
  public static WebServiceFeature getFeature(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramString.equals(W3C.policyNsUri))
      return new AddressingFeature(paramBoolean1, paramBoolean2); 
    if (paramString.equals(MEMBER.policyNsUri))
      return new MemberSubmissionAddressingFeature(paramBoolean1, paramBoolean2); 
    throw new WebServiceException("Unsupported namespace URI: " + paramString);
  }
  
  @NotNull
  public static AddressingVersion fromSpecClass(Class<? extends EndpointReference> paramClass) {
    if (paramClass == javax.xml.ws.wsaddressing.W3CEndpointReference.class)
      return W3C; 
    if (paramClass == com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference.class)
      return MEMBER; 
    throw new WebServiceException("Unsupported EPR type: " + paramClass);
  }
  
  public static boolean isRequired(WebServiceFeature paramWebServiceFeature) {
    if (paramWebServiceFeature.getID().equals("http://www.w3.org/2005/08/addressing/module"))
      return ((AddressingFeature)paramWebServiceFeature).isRequired(); 
    if (paramWebServiceFeature.getID().equals("http://java.sun.com/xml/ns/jaxws/2004/08/addressing"))
      return ((MemberSubmissionAddressingFeature)paramWebServiceFeature).isRequired(); 
    throw new WebServiceException("WebServiceFeature not an Addressing feature: " + paramWebServiceFeature.getID());
  }
  
  public static boolean isRequired(WSBinding paramWSBinding) {
    AddressingFeature addressingFeature = (AddressingFeature)paramWSBinding.getFeature(AddressingFeature.class);
    if (addressingFeature != null)
      return addressingFeature.isRequired(); 
    MemberSubmissionAddressingFeature memberSubmissionAddressingFeature = (MemberSubmissionAddressingFeature)paramWSBinding.getFeature(MemberSubmissionAddressingFeature.class);
    return (memberSubmissionAddressingFeature != null) ? memberSubmissionAddressingFeature.isRequired() : 0;
  }
  
  public static boolean isEnabled(WSBinding paramWSBinding) { return (paramWSBinding.isFeatureEnabled(MemberSubmissionAddressingFeature.class) || paramWSBinding.isFeatureEnabled(AddressingFeature.class)); }
  
  static  {
    // Byte code:
    //   0: new com/sun/xml/internal/ws/api/addressing/AddressingVersion$1
    //   3: dup
    //   4: ldc 'W3C'
    //   6: iconst_0
    //   7: ldc 'http://www.w3.org/2005/08/addressing'
    //   9: ldc 'wsa'
    //   11: ldc '<EndpointReference xmlns="http://www.w3.org/2005/08/addressing">\\n    <Address>http://www.w3.org/2005/08/addressing/anonymous</Address>\\n</EndpointReference>'
    //   13: ldc 'http://www.w3.org/2006/05/addressing/wsdl'
    //   15: ldc 'http://www.w3.org/2006/05/addressing/wsdl'
    //   17: ldc 'http://www.w3.org/2005/08/addressing/anonymous'
    //   19: ldc 'http://www.w3.org/2005/08/addressing/none'
    //   21: new com/sun/xml/internal/ws/api/addressing/AddressingVersion$EPR
    //   24: dup
    //   25: ldc javax/xml/ws/wsaddressing/W3CEndpointReference
    //   27: ldc 'Address'
    //   29: ldc 'ServiceName'
    //   31: ldc 'EndpointName'
    //   33: ldc 'InterfaceName'
    //   35: new javax/xml/namespace/QName
    //   38: dup
    //   39: ldc 'http://www.w3.org/2005/08/addressing'
    //   41: ldc 'Metadata'
    //   43: ldc 'wsa'
    //   45: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   48: ldc 'ReferenceParameters'
    //   50: aconst_null
    //   51: invokespecial <init> : (Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljavax/xml/namespace/QName;Ljava/lang/String;Ljava/lang/String;)V
    //   54: invokespecial <init> : (Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion$EPR;)V
    //   57: putstatic com/sun/xml/internal/ws/api/addressing/AddressingVersion.W3C : Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion;
    //   60: new com/sun/xml/internal/ws/api/addressing/AddressingVersion$2
    //   63: dup
    //   64: ldc 'MEMBER'
    //   66: iconst_1
    //   67: ldc 'http://schemas.xmlsoap.org/ws/2004/08/addressing'
    //   69: ldc 'wsa'
    //   71: ldc '<EndpointReference xmlns="http://schemas.xmlsoap.org/ws/2004/08/addressing">\\n    <Address>http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous</Address>\\n</EndpointReference>'
    //   73: ldc 'http://schemas.xmlsoap.org/ws/2004/08/addressing'
    //   75: ldc 'http://schemas.xmlsoap.org/ws/2004/08/addressing/policy'
    //   77: ldc 'http://schemas.xmlsoap.org/ws/2004/08/addressing/role/anonymous'
    //   79: ldc ''
    //   81: new com/sun/xml/internal/ws/api/addressing/AddressingVersion$EPR
    //   84: dup
    //   85: ldc com/sun/xml/internal/ws/developer/MemberSubmissionEndpointReference
    //   87: ldc 'Address'
    //   89: ldc 'ServiceName'
    //   91: ldc 'PortName'
    //   93: ldc 'PortType'
    //   95: getstatic com/sun/xml/internal/ws/addressing/v200408/MemberSubmissionAddressingConstants.MEX_METADATA : Ljavax/xml/namespace/QName;
    //   98: ldc 'ReferenceParameters'
    //   100: ldc 'ReferenceProperties'
    //   102: invokespecial <init> : (Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljavax/xml/namespace/QName;Ljava/lang/String;Ljava/lang/String;)V
    //   105: invokespecial <init> : (Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion$EPR;)V
    //   108: putstatic com/sun/xml/internal/ws/api/addressing/AddressingVersion.MEMBER : Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion;
    //   111: iconst_2
    //   112: anewarray com/sun/xml/internal/ws/api/addressing/AddressingVersion
    //   115: dup
    //   116: iconst_0
    //   117: getstatic com/sun/xml/internal/ws/api/addressing/AddressingVersion.W3C : Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion;
    //   120: aastore
    //   121: dup
    //   122: iconst_1
    //   123: getstatic com/sun/xml/internal/ws/api/addressing/AddressingVersion.MEMBER : Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion;
    //   126: aastore
    //   127: putstatic com/sun/xml/internal/ws/api/addressing/AddressingVersion.$VALUES : [Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion;
    //   130: new javax/xml/namespace/QName
    //   133: dup
    //   134: ldc 'http://jax-ws.dev.java.net/addressing/fault'
    //   136: ldc 'DuplicateAddressInEpr'
    //   138: ldc 'wsa'
    //   140: invokespecial <init> : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
    //   143: putstatic com/sun/xml/internal/ws/api/addressing/AddressingVersion.fault_duplicateAddressInEpr : Ljavax/xml/namespace/QName;
    //   146: return
  }
  
  public static final class EPR {
    public final Class<? extends EndpointReference> eprClass;
    
    public final String address;
    
    public final String serviceName;
    
    public final String portName;
    
    public final String portTypeName;
    
    public final String referenceParameters;
    
    public final QName wsdlMetadata;
    
    public final String referenceProperties;
    
    public EPR(Class<? extends EndpointReference> param1Class, String param1String1, String param1String2, String param1String3, String param1String4, QName param1QName, String param1String5, String param1String6) {
      this.eprClass = param1Class;
      this.address = param1String1;
      this.serviceName = param1String2;
      this.portName = param1String3;
      this.portTypeName = param1String4;
      this.referenceParameters = param1String5;
      this.referenceProperties = param1String6;
      this.wsdlMetadata = param1QName;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\addressing\AddressingVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */