package com.sun.xml.internal.ws.api.pipe;

import com.sun.xml.internal.ws.api.message.Packet;

public final class NextAction {
  int kind;
  
  Tube next;
  
  Packet packet;
  
  Throwable throwable;
  
  Runnable onExitRunnable;
  
  static final int INVOKE = 0;
  
  static final int INVOKE_AND_FORGET = 1;
  
  static final int RETURN = 2;
  
  static final int THROW = 3;
  
  static final int SUSPEND = 4;
  
  static final int THROW_ABORT_RESPONSE = 5;
  
  static final int ABORT_RESPONSE = 6;
  
  static final int INVOKE_ASYNC = 7;
  
  private void set(int paramInt, Tube paramTube, Packet paramPacket, Throwable paramThrowable) {
    this.kind = paramInt;
    this.next = paramTube;
    this.packet = paramPacket;
    this.throwable = paramThrowable;
  }
  
  public void invoke(Tube paramTube, Packet paramPacket) { set(0, paramTube, paramPacket, null); }
  
  public void invokeAndForget(Tube paramTube, Packet paramPacket) { set(1, paramTube, paramPacket, null); }
  
  public void returnWith(Packet paramPacket) { set(2, null, paramPacket, null); }
  
  public void throwException(Packet paramPacket, Throwable paramThrowable) { set(2, null, paramPacket, paramThrowable); }
  
  public void throwException(Throwable paramThrowable) {
    assert paramThrowable instanceof RuntimeException || paramThrowable instanceof Error;
    set(3, null, null, paramThrowable);
  }
  
  public void throwExceptionAbortResponse(Throwable paramThrowable) { set(5, null, null, paramThrowable); }
  
  public void abortResponse(Packet paramPacket) { set(6, null, paramPacket, null); }
  
  public void invokeAsync(Tube paramTube, Packet paramPacket) { set(7, paramTube, paramPacket, null); }
  
  public void suspend() { suspend(null, null); }
  
  public void suspend(Runnable paramRunnable) { suspend(null, paramRunnable); }
  
  public void suspend(Tube paramTube) { suspend(paramTube, null); }
  
  public void suspend(Tube paramTube, Runnable paramRunnable) {
    set(4, paramTube, null, null);
    this.onExitRunnable = paramRunnable;
  }
  
  public Tube getNext() { return this.next; }
  
  public void setNext(Tube paramTube) { this.next = paramTube; }
  
  public Packet getPacket() { return this.packet; }
  
  public Throwable getThrowable() { return this.throwable; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(super.toString()).append(" [");
    stringBuilder.append("kind=").append(getKindString()).append(',');
    stringBuilder.append("next=").append(this.next).append(',');
    stringBuilder.append("packet=").append((this.packet != null) ? this.packet.toShortString() : null).append(',');
    stringBuilder.append("throwable=").append(this.throwable).append(']');
    return stringBuilder.toString();
  }
  
  public String getKindString() {
    switch (this.kind) {
      case 0:
        return "INVOKE";
      case 1:
        return "INVOKE_AND_FORGET";
      case 2:
        return "RETURN";
      case 3:
        return "THROW";
      case 4:
        return "SUSPEND";
      case 5:
        return "THROW_ABORT_RESPONSE";
      case 6:
        return "ABORT_RESPONSE";
      case 7:
        return "INVOKE_ASYNC";
    } 
    throw new AssertionError(this.kind);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\NextAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */