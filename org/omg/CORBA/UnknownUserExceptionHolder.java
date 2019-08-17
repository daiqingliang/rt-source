package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class UnknownUserExceptionHolder implements Streamable {
  public UnknownUserException value = null;
  
  public UnknownUserExceptionHolder() {}
  
  public UnknownUserExceptionHolder(UnknownUserException paramUnknownUserException) { this.value = paramUnknownUserException; }
  
  public void _read(InputStream paramInputStream) { this.value = UnknownUserExceptionHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { UnknownUserExceptionHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return UnknownUserExceptionHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\UnknownUserExceptionHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */