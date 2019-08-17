package com.sun.nio.sctp;

import jdk.Exported;

@Exported
public class Association {
  private final int associationID;
  
  private final int maxInStreams;
  
  private final int maxOutStreams;
  
  protected Association(int paramInt1, int paramInt2, int paramInt3) {
    this.associationID = paramInt1;
    this.maxInStreams = paramInt2;
    this.maxOutStreams = paramInt3;
  }
  
  public final int associationID() { return this.associationID; }
  
  public final int maxInboundStreams() { return this.maxInStreams; }
  
  public final int maxOutboundStreams() { return this.maxOutStreams; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\sctp\Association.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */