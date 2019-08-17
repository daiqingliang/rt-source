package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import java.util.Iterator;

public interface AttachmentEx extends Attachment {
  @NotNull
  Iterator<MimeHeader> getMimeHeaders();
  
  public static interface MimeHeader {
    String getName();
    
    String getValue();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\message\AttachmentEx.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */