package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.server.WSEndpointImpl;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class EPRSDDocumentFilter implements SDDocumentFilter {
  private final WSEndpointImpl<?> endpoint;
  
  List<BoundEndpoint> beList;
  
  public EPRSDDocumentFilter(@NotNull WSEndpointImpl<?> paramWSEndpointImpl) { this.endpoint = paramWSEndpointImpl; }
  
  @Nullable
  private WSEndpointImpl<?> getEndpoint(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return null; 
    if (this.endpoint.getServiceName().getLocalPart().equals(paramString1) && this.endpoint.getPortName().getLocalPart().equals(paramString2))
      return this.endpoint; 
    if (this.beList == null) {
      Module module = (Module)this.endpoint.getContainer().getSPI(Module.class);
      if (module != null) {
        this.beList = module.getBoundEndpoints();
      } else {
        this.beList = Collections.emptyList();
      } 
    } 
    for (BoundEndpoint boundEndpoint : this.beList) {
      WSEndpoint wSEndpoint = boundEndpoint.getEndpoint();
      if (wSEndpoint.getServiceName().getLocalPart().equals(paramString1) && wSEndpoint.getPortName().getLocalPart().equals(paramString2))
        return (WSEndpointImpl)wSEndpoint; 
    } 
    return null;
  }
  
  public XMLStreamWriter filter(SDDocument paramSDDocument, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException, IOException { return !paramSDDocument.isWSDL() ? paramXMLStreamWriter : new XMLStreamWriterFilter(paramXMLStreamWriter) {
        private boolean eprExtnFilterON = false;
        
        private boolean portHasEPR = false;
        
        private int eprDepth = -1;
        
        private String serviceName = null;
        
        private boolean onService = false;
        
        private int serviceDepth = -1;
        
        private String portName = null;
        
        private boolean onPort = false;
        
        private int portDepth = -1;
        
        private String portAddress;
        
        private boolean onPortAddress = false;
        
        private void handleStartElement(String param1String1, String param1String2) throws XMLStreamException {
          resetOnElementFlags();
          if (this.serviceDepth >= 0)
            this.serviceDepth++; 
          if (this.portDepth >= 0)
            this.portDepth++; 
          if (this.eprDepth >= 0)
            this.eprDepth++; 
          if (param1String2.equals(WSDLConstants.QNAME_SERVICE.getNamespaceURI()) && param1String1.equals(WSDLConstants.QNAME_SERVICE.getLocalPart())) {
            this.onService = true;
            this.serviceDepth = 0;
          } else if (param1String2.equals(WSDLConstants.QNAME_PORT.getNamespaceURI()) && param1String1.equals(WSDLConstants.QNAME_PORT.getLocalPart())) {
            if (this.serviceDepth >= 1) {
              this.onPort = true;
              this.portDepth = 0;
            } 
          } else if (param1String2.equals("http://www.w3.org/2005/08/addressing") && param1String1.equals("EndpointReference")) {
            if (this.serviceDepth >= 1 && this.portDepth >= 1) {
              this.portHasEPR = true;
              this.eprDepth = 0;
            } 
          } else if ((param1String2.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS.getNamespaceURI()) || param1String2.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS.getNamespaceURI())) && param1String1.equals("address") && this.portDepth == 1) {
            this.onPortAddress = true;
          } 
          WSEndpointImpl wSEndpointImpl = EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName);
          if (wSEndpointImpl != null && this.eprDepth == 1 && !param1String2.equals("http://www.w3.org/2005/08/addressing"))
            this.eprExtnFilterON = true; 
        }
        
        private void resetOnElementFlags() {
          if (this.onService)
            this.onService = false; 
          if (this.onPort)
            this.onPort = false; 
          if (this.onPortAddress)
            this.onPortAddress = false; 
        }
        
        private void writeEPRExtensions(Collection<WSEndpointReference.EPRExtension> param1Collection) throws XMLStreamException {
          if (param1Collection != null)
            for (WSEndpointReference.EPRExtension ePRExtension : param1Collection) {
              XMLStreamReaderToXMLStreamWriter xMLStreamReaderToXMLStreamWriter = new XMLStreamReaderToXMLStreamWriter();
              XMLStreamReader xMLStreamReader = ePRExtension.readAsXMLStreamReader();
              xMLStreamReaderToXMLStreamWriter.bridge(xMLStreamReader, this.writer);
              XMLStreamReaderFactory.recycle(xMLStreamReader);
            }  
        }
        
        public void writeStartElement(String param1String1, String param1String2, String param1String3) throws XMLStreamException {
          handleStartElement(param1String2, param1String3);
          if (!this.eprExtnFilterON)
            super.writeStartElement(param1String1, param1String2, param1String3); 
        }
        
        public void writeStartElement(String param1String1, String param1String2) throws XMLStreamException {
          handleStartElement(param1String2, param1String1);
          if (!this.eprExtnFilterON)
            super.writeStartElement(param1String1, param1String2); 
        }
        
        public void writeStartElement(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeStartElement(param1String); 
        }
        
        private void handleEndElement() {
          resetOnElementFlags();
          if (this.portDepth == 0 && !this.portHasEPR && EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName) != null) {
            this.writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", AddressingVersion.W3C.nsUri);
            this.writer.writeNamespace(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.nsUri);
            this.writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.address, AddressingVersion.W3C.nsUri);
            this.writer.writeCharacters(this.portAddress);
            this.writer.writeEndElement();
            writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName).getEndpointReferenceExtensions());
            this.writer.writeEndElement();
          } 
          if (this.eprDepth == 0) {
            if (this.portHasEPR && EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName) != null)
              writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(this.serviceName, this.portName).getEndpointReferenceExtensions()); 
            this.eprExtnFilterON = false;
          } 
          if (this.serviceDepth >= 0)
            this.serviceDepth--; 
          if (this.portDepth >= 0)
            this.portDepth--; 
          if (this.eprDepth >= 0)
            this.eprDepth--; 
          if (this.serviceDepth == -1)
            this.serviceName = null; 
          if (this.portDepth == -1) {
            this.portHasEPR = false;
            this.portAddress = null;
            this.portName = null;
          } 
        }
        
        public void writeEndElement() {
          handleEndElement();
          if (!this.eprExtnFilterON)
            super.writeEndElement(); 
        }
        
        private void handleAttribute(String param1String1, String param1String2) throws XMLStreamException {
          if (param1String1.equals("name"))
            if (this.onService) {
              this.serviceName = param1String2;
              this.onService = false;
            } else if (this.onPort) {
              this.portName = param1String2;
              this.onPort = false;
            }  
          if (param1String1.equals("location") && this.onPortAddress)
            this.portAddress = param1String2; 
        }
        
        public void writeAttribute(String param1String1, String param1String2, String param1String3, String param1String4) throws XMLStreamException {
          handleAttribute(param1String3, param1String4);
          if (!this.eprExtnFilterON)
            super.writeAttribute(param1String1, param1String2, param1String3, param1String4); 
        }
        
        public void writeAttribute(String param1String1, String param1String2, String param1String3) throws XMLStreamException {
          handleAttribute(param1String2, param1String3);
          if (!this.eprExtnFilterON)
            super.writeAttribute(param1String1, param1String2, param1String3); 
        }
        
        public void writeAttribute(String param1String1, String param1String2) throws XMLStreamException {
          handleAttribute(param1String1, param1String2);
          if (!this.eprExtnFilterON)
            super.writeAttribute(param1String1, param1String2); 
        }
        
        public void writeEmptyElement(String param1String1, String param1String2) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeEmptyElement(param1String1, param1String2); 
        }
        
        public void writeNamespace(String param1String1, String param1String2) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeNamespace(param1String1, param1String2); 
        }
        
        public void setNamespaceContext(NamespaceContext param1NamespaceContext) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.setNamespaceContext(param1NamespaceContext); 
        }
        
        public void setDefaultNamespace(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.setDefaultNamespace(param1String); 
        }
        
        public void setPrefix(String param1String1, String param1String2) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.setPrefix(param1String1, param1String2); 
        }
        
        public void writeProcessingInstruction(String param1String1, String param1String2) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeProcessingInstruction(param1String1, param1String2); 
        }
        
        public void writeEmptyElement(String param1String1, String param1String2, String param1String3) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeEmptyElement(param1String1, param1String2, param1String3); 
        }
        
        public void writeCData(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeCData(param1String); 
        }
        
        public void writeCharacters(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeCharacters(param1String); 
        }
        
        public void writeComment(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeComment(param1String); 
        }
        
        public void writeDTD(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeDTD(param1String); 
        }
        
        public void writeDefaultNamespace(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeDefaultNamespace(param1String); 
        }
        
        public void writeEmptyElement(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeEmptyElement(param1String); 
        }
        
        public void writeEntityRef(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeEntityRef(param1String); 
        }
        
        public void writeProcessingInstruction(String param1String) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeProcessingInstruction(param1String); 
        }
        
        public void writeCharacters(char[] param1ArrayOfChar, int param1Int1, int param1Int2) throws XMLStreamException {
          if (!this.eprExtnFilterON)
            super.writeCharacters(param1ArrayOfChar, param1Int1, param1Int2); 
        }
      }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\EPRSDDocumentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */