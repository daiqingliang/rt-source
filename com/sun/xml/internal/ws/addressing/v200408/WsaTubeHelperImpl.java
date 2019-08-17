package com.sun.xml.internal.ws.addressing.v200408;

import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;

public class WsaTubeHelperImpl extends WsaTubeHelper {
  static final JAXBContext jc;
  
  public WsaTubeHelperImpl(WSDLPort paramWSDLPort, SEIModel paramSEIModel, WSBinding paramWSBinding) { super(paramWSBinding, paramSEIModel, paramWSDLPort); }
  
  private Marshaller createMarshaller() throws JAXBException {
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
    return marshaller;
  }
  
  public final void getProblemActionDetail(String paramString, Element paramElement) {
    ProblemAction problemAction = new ProblemAction(paramString);
    try {
      createMarshaller().marshal(problemAction, paramElement);
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } 
  }
  
  public final void getInvalidMapDetail(QName paramQName, Element paramElement) {
    ProblemHeaderQName problemHeaderQName = new ProblemHeaderQName(paramQName);
    try {
      createMarshaller().marshal(problemHeaderQName, paramElement);
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } 
  }
  
  public final void getMapRequiredDetail(QName paramQName, Element paramElement) { getInvalidMapDetail(paramQName, paramElement); }
  
  static  {
    try {
      jc = JAXBContext.newInstance(new Class[] { ProblemAction.class, ProblemHeaderQName.class });
    } catch (JAXBException jAXBException) {
      throw new WebServiceException(jAXBException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\v200408\WsaTubeHelperImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */