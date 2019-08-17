package org.omg.CORBA;

public final class UnknownUserException extends UserException {
  public Any except;
  
  public UnknownUserException() {}
  
  public UnknownUserException(Any paramAny) { this.except = paramAny; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\UnknownUserException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */