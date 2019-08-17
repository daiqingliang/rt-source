package javax.xml.soap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.activation.DataHandler;

public abstract class SOAPMessage {
  public static final String CHARACTER_SET_ENCODING = "javax.xml.soap.character-set-encoding";
  
  public static final String WRITE_XML_DECLARATION = "javax.xml.soap.write-xml-declaration";
  
  public abstract void setContentDescription(String paramString);
  
  public abstract String getContentDescription();
  
  public abstract SOAPPart getSOAPPart();
  
  public SOAPBody getSOAPBody() throws SOAPException { throw new UnsupportedOperationException("getSOAPBody must be overridden by all subclasses of SOAPMessage"); }
  
  public SOAPHeader getSOAPHeader() throws SOAPException { throw new UnsupportedOperationException("getSOAPHeader must be overridden by all subclasses of SOAPMessage"); }
  
  public abstract void removeAllAttachments();
  
  public abstract int countAttachments();
  
  public abstract Iterator getAttachments();
  
  public abstract Iterator getAttachments(MimeHeaders paramMimeHeaders);
  
  public abstract void removeAttachments(MimeHeaders paramMimeHeaders);
  
  public abstract AttachmentPart getAttachment(SOAPElement paramSOAPElement) throws SOAPException;
  
  public abstract void addAttachmentPart(AttachmentPart paramAttachmentPart);
  
  public abstract AttachmentPart createAttachmentPart();
  
  public AttachmentPart createAttachmentPart(DataHandler paramDataHandler) {
    AttachmentPart attachmentPart = createAttachmentPart();
    attachmentPart.setDataHandler(paramDataHandler);
    return attachmentPart;
  }
  
  public abstract MimeHeaders getMimeHeaders();
  
  public AttachmentPart createAttachmentPart(Object paramObject, String paramString) {
    AttachmentPart attachmentPart = createAttachmentPart();
    attachmentPart.setContent(paramObject, paramString);
    return attachmentPart;
  }
  
  public abstract void saveChanges();
  
  public abstract boolean saveRequired();
  
  public abstract void writeTo(OutputStream paramOutputStream) throws SOAPException, IOException;
  
  public void setProperty(String paramString, Object paramObject) throws SOAPException { throw new UnsupportedOperationException("setProperty must be overridden by all subclasses of SOAPMessage"); }
  
  public Object getProperty(String paramString) throws SOAPException { throw new UnsupportedOperationException("getProperty must be overridden by all subclasses of SOAPMessage"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\soap\SOAPMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */