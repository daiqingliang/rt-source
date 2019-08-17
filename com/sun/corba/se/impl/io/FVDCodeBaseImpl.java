package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.SendingContext._CodeBaseImplBase;
import java.util.Hashtable;
import java.util.Stack;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;

public class FVDCodeBaseImpl extends _CodeBaseImplBase {
  private static Hashtable fvds = new Hashtable();
  
  private ORB orb = null;
  
  private OMGSystemException wrapper = OMGSystemException.get("rpc.encoding");
  
  private ValueHandlerImpl vhandler = null;
  
  void setValueHandler(ValueHandler paramValueHandler) { this.vhandler = (ValueHandlerImpl)paramValueHandler; }
  
  public Repository get_ir() { return null; }
  
  public String implementation(String paramString) {
    try {
      if (this.vhandler == null)
        this.vhandler = ValueHandlerImpl.getInstance(false); 
      String str = Util.getCodebase(this.vhandler.getClassFromType(paramString));
      return (str == null) ? "" : str;
    } catch (ClassNotFoundException classNotFoundException) {
      throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE, classNotFoundException);
    } 
  }
  
  public String[] implementations(String[] paramArrayOfString) {
    String[] arrayOfString = new String[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++)
      arrayOfString[b] = implementation(paramArrayOfString[b]); 
    return arrayOfString;
  }
  
  public FullValueDescription meta(String paramString) {
    try {
      FullValueDescription fullValueDescription = (FullValueDescription)fvds.get(paramString);
      if (fullValueDescription == null) {
        if (this.vhandler == null)
          this.vhandler = ValueHandlerImpl.getInstance(false); 
        try {
          fullValueDescription = ValueUtility.translate(_orb(), ObjectStreamClass.lookup(this.vhandler.getAnyClassFromType(paramString)), this.vhandler);
        } catch (Throwable throwable) {
          if (this.orb == null)
            this.orb = ORB.init(); 
          fullValueDescription = ValueUtility.translate(this.orb, ObjectStreamClass.lookup(this.vhandler.getAnyClassFromType(paramString)), this.vhandler);
        } 
        if (fullValueDescription != null) {
          fvds.put(paramString, fullValueDescription);
        } else {
          throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE);
        } 
      } 
      return fullValueDescription;
    } catch (Throwable throwable) {
      throw this.wrapper.incompatibleValueImpl(CompletionStatus.COMPLETED_MAYBE, throwable);
    } 
  }
  
  public FullValueDescription[] metas(String[] paramArrayOfString) {
    FullValueDescription[] arrayOfFullValueDescription = new FullValueDescription[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++)
      arrayOfFullValueDescription[b] = meta(paramArrayOfString[b]); 
    return arrayOfFullValueDescription;
  }
  
  public String[] bases(String paramString) {
    try {
      if (this.vhandler == null)
        this.vhandler = ValueHandlerImpl.getInstance(false); 
      Stack stack = new Stack();
      for (Class clazz = ObjectStreamClass.lookup(this.vhandler.getClassFromType(paramString)).forClass().getSuperclass(); !clazz.equals(Object.class); clazz = clazz.getSuperclass())
        stack.push(this.vhandler.createForAnyType(clazz)); 
      String[] arrayOfString = new String[stack.size()];
      for (int i = arrayOfString.length - 1; i >= 0; i++)
        arrayOfString[i] = (String)stack.pop(); 
      return arrayOfString;
    } catch (Throwable throwable) {
      throw this.wrapper.missingLocalValueImpl(CompletionStatus.COMPLETED_MAYBE, throwable);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\FVDCodeBaseImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */