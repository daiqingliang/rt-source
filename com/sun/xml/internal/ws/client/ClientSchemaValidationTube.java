package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.util.MetadataUtil;
import com.sun.xml.internal.ws.util.pipe.AbstractSchemaValidationTube;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.ws.WebServiceException;
import org.xml.sax.SAXException;

public class ClientSchemaValidationTube extends AbstractSchemaValidationTube {
  private static final Logger LOGGER = Logger.getLogger(ClientSchemaValidationTube.class.getName());
  
  private final Schema schema;
  
  private final Validator validator;
  
  private final boolean noValidation;
  
  private final WSDLPort port;
  
  public ClientSchemaValidationTube(WSBinding paramWSBinding, WSDLPort paramWSDLPort, Tube paramTube) {
    super(paramWSBinding, paramTube);
    this.port = paramWSDLPort;
    if (paramWSDLPort != null) {
      String str = paramWSDLPort.getOwner().getParent().getLocation().getSystemId();
      AbstractSchemaValidationTube.MetadataResolverImpl metadataResolverImpl = new AbstractSchemaValidationTube.MetadataResolverImpl(this);
      Map map = MetadataUtil.getMetadataClosure(str, metadataResolverImpl, true);
      metadataResolverImpl = new AbstractSchemaValidationTube.MetadataResolverImpl(this, map.values());
      Source[] arrayOfSource = getSchemaSources(map.values(), metadataResolverImpl);
      for (Source source : arrayOfSource)
        LOGGER.fine("Constructing client validation schema from = " + source.getSystemId()); 
      if (arrayOfSource.length != 0) {
        this.noValidation = false;
        this.sf.setResourceResolver(metadataResolverImpl);
        try {
          this.schema = this.sf.newSchema(arrayOfSource);
        } catch (SAXException sAXException) {
          throw new WebServiceException(sAXException);
        } 
        this.validator = this.schema.newValidator();
        return;
      } 
    } 
    this.noValidation = true;
    this.schema = null;
    this.validator = null;
  }
  
  protected Validator getValidator() { return this.validator; }
  
  protected boolean isNoValidation() { return this.noValidation; }
  
  protected ClientSchemaValidationTube(ClientSchemaValidationTube paramClientSchemaValidationTube, TubeCloner paramTubeCloner) {
    super(paramClientSchemaValidationTube, paramTubeCloner);
    this.port = paramClientSchemaValidationTube.port;
    this.schema = paramClientSchemaValidationTube.schema;
    this.validator = this.schema.newValidator();
    this.noValidation = paramClientSchemaValidationTube.noValidation;
  }
  
  public AbstractTubeImpl copy(TubeCloner paramTubeCloner) { return new ClientSchemaValidationTube(this, paramTubeCloner); }
  
  public NextAction processRequest(Packet paramPacket) {
    if (isNoValidation() || !this.feature.isOutbound() || !paramPacket.getMessage().hasPayload() || paramPacket.getMessage().isFault())
      return super.processRequest(paramPacket); 
    try {
      doProcess(paramPacket);
    } catch (SAXException sAXException) {
      throw new WebServiceException(sAXException);
    } 
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket) {
    if (isNoValidation() || !this.feature.isInbound() || paramPacket.getMessage() == null || !paramPacket.getMessage().hasPayload() || paramPacket.getMessage().isFault())
      return super.processResponse(paramPacket); 
    try {
      doProcess(paramPacket);
    } catch (SAXException sAXException) {
      throw new WebServiceException(sAXException);
    } 
    return super.processResponse(paramPacket);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\ClientSchemaValidationTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */