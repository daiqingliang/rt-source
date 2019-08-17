package com.sun.xml.internal.ws.api.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import java.io.IOException;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public interface WSDLModel extends WSDLExtensible {
  WSDLPortType getPortType(@NotNull QName paramQName);
  
  WSDLBoundPortType getBinding(@NotNull QName paramQName);
  
  WSDLBoundPortType getBinding(@NotNull QName paramQName1, @NotNull QName paramQName2);
  
  WSDLService getService(@NotNull QName paramQName);
  
  @NotNull
  Map<QName, ? extends WSDLPortType> getPortTypes();
  
  @NotNull
  Map<QName, ? extends WSDLBoundPortType> getBindings();
  
  @NotNull
  Map<QName, ? extends WSDLService> getServices();
  
  QName getFirstServiceName();
  
  WSDLMessage getMessage(QName paramQName);
  
  @NotNull
  Map<QName, ? extends WSDLMessage> getMessages();
  
  PolicyMap getPolicyMap();
  
  public static class WSDLParser {
    @NotNull
    public static WSDLModel parse(XMLEntityResolver.Parser param1Parser, XMLEntityResolver param1XMLEntityResolver, boolean param1Boolean, WSDLParserExtension... param1VarArgs) throws IOException, XMLStreamException, SAXException { return parse(param1Parser, param1XMLEntityResolver, param1Boolean, Container.NONE, param1VarArgs); }
    
    @NotNull
    public static WSDLModel parse(XMLEntityResolver.Parser param1Parser, XMLEntityResolver param1XMLEntityResolver, boolean param1Boolean, @NotNull Container param1Container, WSDLParserExtension... param1VarArgs) throws IOException, XMLStreamException, SAXException { return parse(param1Parser, param1XMLEntityResolver, param1Boolean, param1Container, PolicyResolverFactory.create(), param1VarArgs); }
    
    @NotNull
    public static WSDLModel parse(XMLEntityResolver.Parser param1Parser, XMLEntityResolver param1XMLEntityResolver, boolean param1Boolean, @NotNull Container param1Container, PolicyResolver param1PolicyResolver, WSDLParserExtension... param1VarArgs) throws IOException, XMLStreamException, SAXException { return RuntimeWSDLParser.parse(param1Parser, param1XMLEntityResolver, param1Boolean, param1Container, param1PolicyResolver, param1VarArgs); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\WSDLModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */