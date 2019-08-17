package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import java.util.ArrayList;
import java.util.Iterator;

public final class AttachmentSetImpl implements AttachmentSet {
  private final ArrayList<Attachment> attList = new ArrayList();
  
  public AttachmentSetImpl() {}
  
  public AttachmentSetImpl(Iterable<Attachment> paramIterable) {
    for (Attachment attachment : paramIterable)
      add(attachment); 
  }
  
  public Attachment get(String paramString) {
    for (int i = this.attList.size() - 1; i >= 0; i--) {
      Attachment attachment = (Attachment)this.attList.get(i);
      if (attachment.getContentId().equals(paramString))
        return attachment; 
    } 
    return null;
  }
  
  public boolean isEmpty() { return this.attList.isEmpty(); }
  
  public void add(Attachment paramAttachment) { this.attList.add(paramAttachment); }
  
  public Iterator<Attachment> iterator() { return this.attList.iterator(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\AttachmentSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */