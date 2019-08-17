package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.Nullable;

public interface AttachmentSet extends Iterable<Attachment> {
  @Nullable
  Attachment get(String paramString);
  
  boolean isEmpty();
  
  void add(Attachment paramAttachment);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\AttachmentSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */