package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.ws.addressing.W3CAddressingMetadataConstants;
import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGenExtnContext;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public class W3CAddressingMetadataWSDLGeneratorExtension extends WSDLGeneratorExtension {
  private static final Logger LOGGER = Logger.getLogger(W3CAddressingMetadataWSDLGeneratorExtension.class.getName());
  
  public void start(WSDLGenExtnContext paramWSDLGenExtnContext) {
    TypedXmlWriter typedXmlWriter = paramWSDLGenExtnContext.getRoot();
    typedXmlWriter._namespace("http://www.w3.org/2007/05/addressing/metadata", "wsam");
  }
  
  public void addOperationInputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) { paramTypedXmlWriter._attribute(W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getInputAction(paramJavaMethod)); }
  
  public void addOperationOutputExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod) { paramTypedXmlWriter._attribute(W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getOutputAction(paramJavaMethod)); }
  
  public void addOperationFaultExtension(TypedXmlWriter paramTypedXmlWriter, JavaMethod paramJavaMethod, CheckedException paramCheckedException) { paramTypedXmlWriter._attribute(W3CAddressingMetadataConstants.WSAM_ACTION_QNAME, getFaultAction(paramJavaMethod, paramCheckedException)); }
  
  private static final String getInputAction(JavaMethod paramJavaMethod) {
    String str = ((JavaMethodImpl)paramJavaMethod).getInputAction();
    if (str.equals(""))
      str = getDefaultInputAction(paramJavaMethod); 
    return str;
  }
  
  protected static final String getDefaultInputAction(JavaMethod paramJavaMethod) {
    String str1 = paramJavaMethod.getOwner().getTargetNamespace();
    String str2 = getDelimiter(str1);
    if (str1.endsWith(str2))
      str1 = str1.substring(0, str1.length() - 1); 
    String str3 = paramJavaMethod.getMEP().isOneWay() ? paramJavaMethod.getOperationName() : (paramJavaMethod.getOperationName() + "Request");
    return str1 + str2 + paramJavaMethod.getOwner().getPortTypeName().getLocalPart() + str2 + str3;
  }
  
  private static final String getOutputAction(JavaMethod paramJavaMethod) {
    String str = ((JavaMethodImpl)paramJavaMethod).getOutputAction();
    if (str.equals(""))
      str = getDefaultOutputAction(paramJavaMethod); 
    return str;
  }
  
  protected static final String getDefaultOutputAction(JavaMethod paramJavaMethod) {
    String str1 = paramJavaMethod.getOwner().getTargetNamespace();
    String str2 = getDelimiter(str1);
    if (str1.endsWith(str2))
      str1 = str1.substring(0, str1.length() - 1); 
    String str3 = paramJavaMethod.getOperationName() + "Response";
    return str1 + str2 + paramJavaMethod.getOwner().getPortTypeName().getLocalPart() + str2 + str3;
  }
  
  private static final String getDelimiter(String paramString) {
    String str = "/";
    try {
      URI uRI = new URI(paramString);
      if (uRI.getScheme() != null && uRI.getScheme().equalsIgnoreCase("urn"))
        str = ":"; 
    } catch (URISyntaxException uRISyntaxException) {
      LOGGER.warning("TargetNamespace of WebService is not a valid URI");
    } 
    return str;
  }
  
  private static final String getFaultAction(JavaMethod paramJavaMethod, CheckedException paramCheckedException) {
    String str = ((CheckedExceptionImpl)paramCheckedException).getFaultAction();
    if (str.equals(""))
      str = getDefaultFaultAction(paramJavaMethod, paramCheckedException); 
    return str;
  }
  
  protected static final String getDefaultFaultAction(JavaMethod paramJavaMethod, CheckedException paramCheckedException) { return WsaActionUtil.getDefaultFaultAction(paramJavaMethod, paramCheckedException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\W3CAddressingMetadataWSDLGeneratorExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */