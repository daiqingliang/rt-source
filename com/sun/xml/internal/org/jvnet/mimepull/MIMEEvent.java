package com.sun.xml.internal.org.jvnet.mimepull;

import java.nio.ByteBuffer;

abstract class MIMEEvent {
  static final StartMessage START_MESSAGE = new StartMessage();
  
  static final StartPart START_PART = new StartPart();
  
  static final EndPart END_PART = new EndPart();
  
  static final EndMessage END_MESSAGE = new EndMessage();
  
  abstract EVENT_TYPE getEventType();
  
  static final class Content extends MIMEEvent {
    private final ByteBuffer buf;
    
    Content(ByteBuffer param1ByteBuffer) { this.buf = param1ByteBuffer; }
    
    MIMEEvent.EVENT_TYPE getEventType() { return MIMEEvent.EVENT_TYPE.CONTENT; }
    
    ByteBuffer getData() { return this.buf; }
  }
  
  enum EVENT_TYPE {
    START_MESSAGE, START_PART, HEADERS, CONTENT, END_PART, END_MESSAGE;
  }
  
  static final class EndMessage extends MIMEEvent {
    MIMEEvent.EVENT_TYPE getEventType() { return MIMEEvent.EVENT_TYPE.END_MESSAGE; }
  }
  
  static final class EndPart extends MIMEEvent {
    MIMEEvent.EVENT_TYPE getEventType() { return MIMEEvent.EVENT_TYPE.END_PART; }
  }
  
  static final class Headers extends MIMEEvent {
    InternetHeaders ih;
    
    Headers(InternetHeaders param1InternetHeaders) { this.ih = param1InternetHeaders; }
    
    MIMEEvent.EVENT_TYPE getEventType() { return MIMEEvent.EVENT_TYPE.HEADERS; }
    
    InternetHeaders getHeaders() { return this.ih; }
  }
  
  static final class StartMessage extends MIMEEvent {
    MIMEEvent.EVENT_TYPE getEventType() { return MIMEEvent.EVENT_TYPE.START_MESSAGE; }
  }
  
  static final class StartPart extends MIMEEvent {
    MIMEEvent.EVENT_TYPE getEventType() { return MIMEEvent.EVENT_TYPE.START_PART; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\MIMEEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */