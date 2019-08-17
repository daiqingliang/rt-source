package com.sun.xml.internal.messaging.saaj.util;

import java.util.Iterator;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;

public class MimeHeadersUtil {
  public static MimeHeaders copy(MimeHeaders paramMimeHeaders) {
    MimeHeaders mimeHeaders = new MimeHeaders();
    Iterator iterator = paramMimeHeaders.getAllHeaders();
    while (iterator.hasNext()) {
      MimeHeader mimeHeader = (MimeHeader)iterator.next();
      mimeHeaders.addHeader(mimeHeader.getName(), mimeHeader.getValue());
    } 
    return mimeHeaders;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saa\\util\MimeHeadersUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */