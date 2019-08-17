package com.sun.xml.internal.ws.addressing.model;

import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.ws.WebServiceException;

public class ActionNotSupportedException extends WebServiceException {
  private String action;
  
  public ActionNotSupportedException(String paramString) {
    super(AddressingMessages.ACTION_NOT_SUPPORTED_EXCEPTION(paramString));
    this.action = paramString;
  }
  
  public String getAction() { return this.action; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\model\ActionNotSupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */