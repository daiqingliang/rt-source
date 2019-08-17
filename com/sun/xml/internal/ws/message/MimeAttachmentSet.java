package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.encoding.MimeMultipartParser;
import com.sun.xml.internal.ws.resources.EncodingMessages;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.ws.WebServiceException;

public final class MimeAttachmentSet implements AttachmentSet {
  private final MimeMultipartParser mpp;
  
  private Map<String, Attachment> atts = new HashMap();
  
  public MimeAttachmentSet(MimeMultipartParser paramMimeMultipartParser) { this.mpp = paramMimeMultipartParser; }
  
  @Nullable
  public Attachment get(String paramString) {
    Attachment attachment = (Attachment)this.atts.get(paramString);
    if (attachment != null)
      return attachment; 
    try {
      attachment = this.mpp.getAttachmentPart(paramString);
      if (attachment != null)
        this.atts.put(paramString, attachment); 
    } catch (IOException iOException) {
      throw new WebServiceException(EncodingMessages.NO_SUCH_CONTENT_ID(paramString), iOException);
    } 
    return attachment;
  }
  
  public boolean isEmpty() { return (this.atts.size() <= 0 && this.mpp.getAttachmentParts().isEmpty()); }
  
  public void add(Attachment paramAttachment) { this.atts.put(paramAttachment.getContentId(), paramAttachment); }
  
  public Iterator<Attachment> iterator() {
    Map map = this.mpp.getAttachmentParts();
    for (Map.Entry entry : map.entrySet()) {
      if (this.atts.get(entry.getKey()) == null)
        this.atts.put(entry.getKey(), entry.getValue()); 
    } 
    return this.atts.values().iterator();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\MimeAttachmentSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */