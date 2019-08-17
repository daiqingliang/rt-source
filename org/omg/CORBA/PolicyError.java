package org.omg.CORBA;

public final class PolicyError extends UserException {
  public short reason;
  
  public PolicyError() {}
  
  public PolicyError(short paramShort) { this.reason = paramShort; }
  
  public PolicyError(String paramString, short paramShort) {
    super(paramString);
    this.reason = paramShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PolicyError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */