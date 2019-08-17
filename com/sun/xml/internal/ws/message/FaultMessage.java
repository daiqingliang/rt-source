package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.FilterMessageImpl;
import com.sun.xml.internal.ws.api.message.Message;
import javax.xml.namespace.QName;

public class FaultMessage extends FilterMessageImpl {
  @Nullable
  private final QName detailEntryName;
  
  public FaultMessage(Message paramMessage, @Nullable QName paramQName) {
    super(paramMessage);
    this.detailEntryName = paramQName;
  }
  
  @Nullable
  public QName getFirstDetailEntryName() { return this.detailEntryName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\FaultMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */