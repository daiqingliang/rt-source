package javax.xml.ws;

import javax.xml.bind.JAXBContext;
import javax.xml.transform.Source;

public interface LogicalMessage {
  Source getPayload();
  
  void setPayload(Source paramSource);
  
  Object getPayload(JAXBContext paramJAXBContext);
  
  void setPayload(Object paramObject, JAXBContext paramJAXBContext);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\LogicalMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */