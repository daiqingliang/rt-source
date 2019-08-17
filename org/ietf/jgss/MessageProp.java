package org.ietf.jgss;

public class MessageProp {
  private boolean privacyState;
  
  private int qop;
  
  private boolean dupToken;
  
  private boolean oldToken;
  
  private boolean unseqToken;
  
  private boolean gapToken;
  
  private int minorStatus;
  
  private String minorString;
  
  public MessageProp(boolean paramBoolean) { this(0, paramBoolean); }
  
  public MessageProp(int paramInt, boolean paramBoolean) {
    this.qop = paramInt;
    this.privacyState = paramBoolean;
    resetStatusValues();
  }
  
  public int getQOP() { return this.qop; }
  
  public boolean getPrivacy() { return this.privacyState; }
  
  public void setQOP(int paramInt) { this.qop = paramInt; }
  
  public void setPrivacy(boolean paramBoolean) { this.privacyState = paramBoolean; }
  
  public boolean isDuplicateToken() { return this.dupToken; }
  
  public boolean isOldToken() { return this.oldToken; }
  
  public boolean isUnseqToken() { return this.unseqToken; }
  
  public boolean isGapToken() { return this.gapToken; }
  
  public int getMinorStatus() { return this.minorStatus; }
  
  public String getMinorString() { return this.minorString; }
  
  public void setSupplementaryStates(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt, String paramString) {
    this.dupToken = paramBoolean1;
    this.oldToken = paramBoolean2;
    this.unseqToken = paramBoolean3;
    this.gapToken = paramBoolean4;
    this.minorStatus = paramInt;
    this.minorString = paramString;
  }
  
  private void resetStatusValues() {
    this.dupToken = false;
    this.oldToken = false;
    this.unseqToken = false;
    this.gapToken = false;
    this.minorStatus = 0;
    this.minorString = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\ietf\jgss\MessageProp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */