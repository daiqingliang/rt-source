package javax.xml.ws;

import java.io.StringWriter;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.spi.Provider;

@XmlTransient
public abstract class EndpointReference {
  public static EndpointReference readFrom(Source paramSource) { return Provider.provider().readEndpointReference(paramSource); }
  
  public abstract void writeTo(Result paramResult);
  
  public <T> T getPort(Class<T> paramClass, WebServiceFeature... paramVarArgs) { return (T)Provider.provider().getPort(this, paramClass, paramVarArgs); }
  
  public String toString() {
    StringWriter stringWriter = new StringWriter();
    writeTo(new StreamResult(stringWriter));
    return stringWriter.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\EndpointReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */