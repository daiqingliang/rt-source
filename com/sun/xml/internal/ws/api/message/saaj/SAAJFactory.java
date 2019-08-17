package com.sun.xml.internal.ws.api.message.saaj;

import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.Iterator;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class SAAJFactory {
  private static final SAAJFactory instance = new SAAJFactory();
  
  public static MessageFactory getMessageFactory(String paramString) throws SOAPException {
    for (SAAJFactory sAAJFactory : ServiceFinder.find(SAAJFactory.class)) {
      MessageFactory messageFactory = sAAJFactory.createMessageFactory(paramString);
      if (messageFactory != null)
        return messageFactory; 
    } 
    return instance.createMessageFactory(paramString);
  }
  
  public static SOAPFactory getSOAPFactory(String paramString) throws SOAPException {
    for (SAAJFactory sAAJFactory : ServiceFinder.find(SAAJFactory.class)) {
      SOAPFactory sOAPFactory = sAAJFactory.createSOAPFactory(paramString);
      if (sOAPFactory != null)
        return sOAPFactory; 
    } 
    return instance.createSOAPFactory(paramString);
  }
  
  public static Message create(SOAPMessage paramSOAPMessage) {
    for (SAAJFactory sAAJFactory : ServiceFinder.find(SAAJFactory.class)) {
      Message message = sAAJFactory.createMessage(paramSOAPMessage);
      if (message != null)
        return message; 
    } 
    return instance.createMessage(paramSOAPMessage);
  }
  
  public static SOAPMessage read(SOAPVersion paramSOAPVersion, Message paramMessage) throws SOAPException {
    for (SAAJFactory sAAJFactory : ServiceFinder.find(SAAJFactory.class)) {
      SOAPMessage sOAPMessage = sAAJFactory.readAsSOAPMessage(paramSOAPVersion, paramMessage);
      if (sOAPMessage != null)
        return sOAPMessage; 
    } 
    return instance.readAsSOAPMessage(paramSOAPVersion, paramMessage);
  }
  
  public static SOAPMessage read(SOAPVersion paramSOAPVersion, Message paramMessage, Packet paramPacket) throws SOAPException {
    for (SAAJFactory sAAJFactory : ServiceFinder.find(SAAJFactory.class)) {
      SOAPMessage sOAPMessage = sAAJFactory.readAsSOAPMessage(paramSOAPVersion, paramMessage, paramPacket);
      if (sOAPMessage != null)
        return sOAPMessage; 
    } 
    return instance.readAsSOAPMessage(paramSOAPVersion, paramMessage, paramPacket);
  }
  
  public static SAAJMessage read(Packet paramPacket) throws SOAPException {
    ServiceFinder serviceFinder = (paramPacket.component != null) ? ServiceFinder.find(SAAJFactory.class, paramPacket.component) : ServiceFinder.find(SAAJFactory.class);
    for (SAAJFactory sAAJFactory : serviceFinder) {
      SAAJMessage sAAJMessage = sAAJFactory.readAsSAAJ(paramPacket);
      if (sAAJMessage != null)
        return sAAJMessage; 
    } 
    return instance.readAsSAAJ(paramPacket);
  }
  
  public SAAJMessage readAsSAAJ(Packet paramPacket) throws SOAPException {
    SOAPVersion sOAPVersion = paramPacket.getMessage().getSOAPVersion();
    SOAPMessage sOAPMessage = readAsSOAPMessage(sOAPVersion, paramPacket.getMessage());
    return new SAAJMessage(sOAPMessage);
  }
  
  public MessageFactory createMessageFactory(String paramString) throws SOAPException { return MessageFactory.newInstance(paramString); }
  
  public SOAPFactory createSOAPFactory(String paramString) throws SOAPException { return SOAPFactory.newInstance(paramString); }
  
  public Message createMessage(SOAPMessage paramSOAPMessage) { return new SAAJMessage(paramSOAPMessage); }
  
  public SOAPMessage readAsSOAPMessage(SOAPVersion paramSOAPVersion, Message paramMessage) throws SOAPException {
    SOAPMessage sOAPMessage = paramSOAPVersion.getMessageFactory().createMessage();
    SaajStaxWriter saajStaxWriter = new SaajStaxWriter(sOAPMessage);
    try {
      paramMessage.writeTo(saajStaxWriter);
    } catch (XMLStreamException xMLStreamException) {
      throw (xMLStreamException.getCause() instanceof SOAPException) ? (SOAPException)xMLStreamException.getCause() : new SOAPException(xMLStreamException);
    } 
    sOAPMessage = saajStaxWriter.getSOAPMessage();
    addAttachmentsToSOAPMessage(sOAPMessage, paramMessage);
    if (sOAPMessage.saveRequired())
      sOAPMessage.saveChanges(); 
    return sOAPMessage;
  }
  
  public SOAPMessage readAsSOAPMessageSax2Dom(SOAPVersion paramSOAPVersion, Message paramMessage) throws SOAPException {
    SOAPMessage sOAPMessage = paramSOAPVersion.getMessageFactory().createMessage();
    SAX2DOMEx sAX2DOMEx = new SAX2DOMEx(sOAPMessage.getSOAPPart());
    try {
      paramMessage.writeTo(sAX2DOMEx, XmlUtil.DRACONIAN_ERROR_HANDLER);
    } catch (SAXException sAXException) {
      throw new SOAPException(sAXException);
    } 
    addAttachmentsToSOAPMessage(sOAPMessage, paramMessage);
    if (sOAPMessage.saveRequired())
      sOAPMessage.saveChanges(); 
    return sOAPMessage;
  }
  
  protected static void addAttachmentsToSOAPMessage(SOAPMessage paramSOAPMessage, Message paramMessage) {
    for (Attachment attachment : paramMessage.getAttachments()) {
      AttachmentPart attachmentPart = paramSOAPMessage.createAttachmentPart();
      attachmentPart.setDataHandler(attachment.asDataHandler());
      String str = attachment.getContentId();
      if (str != null)
        if (str.startsWith("<") && str.endsWith(">")) {
          attachmentPart.setContentId(str);
        } else {
          attachmentPart.setContentId('<' + str + '>');
        }  
      if (attachment instanceof AttachmentEx) {
        AttachmentEx attachmentEx = (AttachmentEx)attachment;
        Iterator iterator = attachmentEx.getMimeHeaders();
        while (iterator.hasNext()) {
          AttachmentEx.MimeHeader mimeHeader = (AttachmentEx.MimeHeader)iterator.next();
          if (!"Content-ID".equals(mimeHeader.getName()) && !"Content-Type".equals(mimeHeader.getName()))
            attachmentPart.addMimeHeader(mimeHeader.getName(), mimeHeader.getValue()); 
        } 
      } 
      paramSOAPMessage.addAttachmentPart(attachmentPart);
    } 
  }
  
  public SOAPMessage readAsSOAPMessage(SOAPVersion paramSOAPVersion, Message paramMessage, Packet paramPacket) throws SOAPException { return readAsSOAPMessage(paramSOAPVersion, paramMessage); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\saaj\SAAJFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */