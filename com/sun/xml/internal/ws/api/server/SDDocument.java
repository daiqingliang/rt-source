package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

@ManagedData
public interface SDDocument {
  @ManagedAttribute
  QName getRootName();
  
  @ManagedAttribute
  boolean isWSDL();
  
  @ManagedAttribute
  boolean isSchema();
  
  @ManagedAttribute
  Set<String> getImports();
  
  @ManagedAttribute
  URL getURL();
  
  void writeTo(@Nullable PortAddressResolver paramPortAddressResolver, DocumentAddressResolver paramDocumentAddressResolver, OutputStream paramOutputStream) throws IOException;
  
  void writeTo(PortAddressResolver paramPortAddressResolver, DocumentAddressResolver paramDocumentAddressResolver, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException, IOException;
  
  public static interface Schema extends SDDocument {
    @ManagedAttribute
    String getTargetNamespace();
  }
  
  public static interface WSDL extends SDDocument {
    @ManagedAttribute
    String getTargetNamespace();
    
    @ManagedAttribute
    boolean hasPortType();
    
    @ManagedAttribute
    boolean hasService();
    
    @ManagedAttribute
    Set<QName> getAllServices();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\SDDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */