package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import java.io.CharConversionException;
import java.util.Locale;

public class MalformedByteSequenceException extends CharConversionException {
  static final long serialVersionUID = 8436382245048328739L;
  
  private MessageFormatter fFormatter;
  
  private Locale fLocale;
  
  private String fDomain;
  
  private String fKey;
  
  private Object[] fArguments;
  
  private String fMessage;
  
  public MalformedByteSequenceException(MessageFormatter paramMessageFormatter, Locale paramLocale, String paramString1, String paramString2, Object[] paramArrayOfObject) {
    this.fFormatter = paramMessageFormatter;
    this.fLocale = paramLocale;
    this.fDomain = paramString1;
    this.fKey = paramString2;
    this.fArguments = paramArrayOfObject;
  }
  
  public String getDomain() { return this.fDomain; }
  
  public String getKey() { return this.fKey; }
  
  public Object[] getArguments() { return this.fArguments; }
  
  public String getMessage() {
    if (this.fMessage == null) {
      this.fMessage = this.fFormatter.formatMessage(this.fLocale, this.fKey, this.fArguments);
      this.fFormatter = null;
      this.fLocale = null;
    } 
    return this.fMessage;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\io\MalformedByteSequenceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */