package javax.xml.bind.attachment;

import javax.activation.DataHandler;

public abstract class AttachmentUnmarshaller {
  public abstract DataHandler getAttachmentAsDataHandler(String paramString);
  
  public abstract byte[] getAttachmentAsByteArray(String paramString);
  
  public boolean isXOPPackage() { return false; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\bind\attachment\AttachmentUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */