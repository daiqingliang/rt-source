package org.omg.CORBA.portable;

import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class IndirectionException extends SystemException {
  public int offset;
  
  public IndirectionException(int paramInt) {
    super("", 0, CompletionStatus.COMPLETED_MAYBE);
    this.offset = paramInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\portable\IndirectionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */