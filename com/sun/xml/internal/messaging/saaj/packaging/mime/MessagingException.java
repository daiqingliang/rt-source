package com.sun.xml.internal.messaging.saaj.packaging.mime;

public class MessagingException extends Exception {
  private Exception next;
  
  public MessagingException() {}
  
  public MessagingException(String paramString) { super(paramString); }
  
  public MessagingException(String paramString, Exception paramException) {
    super(paramString);
    this.next = paramException;
  }
  
  public Exception getNextException() { return this.next; }
  
  public boolean setNextException(Exception paramException) {
    Exception exception = this;
    while (exception instanceof MessagingException && ((MessagingException)exception).next != null)
      exception = ((MessagingException)exception).next; 
    if (exception instanceof MessagingException) {
      ((MessagingException)exception).next = paramException;
      return true;
    } 
    return false;
  }
  
  public String getMessage() {
    if (this.next == null)
      return super.getMessage(); 
    Exception exception = this.next;
    String str = super.getMessage();
    StringBuffer stringBuffer = new StringBuffer((str == null) ? "" : str);
    while (exception != null) {
      stringBuffer.append(";\n  nested exception is:\n\t");
      if (exception instanceof MessagingException) {
        MessagingException messagingException = (MessagingException)exception;
        stringBuffer.append(exception.getClass().toString());
        String str1 = messagingException.getSuperMessage();
        if (str1 != null) {
          stringBuffer.append(": ");
          stringBuffer.append(str1);
        } 
        exception = messagingException.next;
        continue;
      } 
      stringBuffer.append(exception.toString());
      exception = null;
    } 
    return stringBuffer.toString();
  }
  
  private String getSuperMessage() { return super.getMessage(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\MessagingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */