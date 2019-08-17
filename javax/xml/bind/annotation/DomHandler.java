package javax.xml.bind.annotation;

import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

public interface DomHandler<ElementT, ResultT extends Result> {
  ResultT createUnmarshaller(ValidationEventHandler paramValidationEventHandler);
  
  ElementT getElement(ResultT paramResultT);
  
  Source marshal(ElementT paramElementT, ValidationEventHandler paramValidationEventHandler);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\annotation\DomHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */