package javax.xml.bind;

import org.xml.sax.ContentHandler;

public interface UnmarshallerHandler extends ContentHandler {
  Object getResult() throws JAXBException, IllegalStateException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\UnmarshallerHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */