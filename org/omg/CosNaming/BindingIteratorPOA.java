package org.omg.CosNaming;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract class BindingIteratorPOA extends Servant implements BindingIteratorOperations, InvokeHandler {
  private static Hashtable _methods = new Hashtable();
  
  private static String[] __ids;
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler) {
    boolean bool2;
    boolean bool1;
    BindingListHolder bindingListHolder;
    int i;
    BindingHolder bindingHolder;
    null = null;
    Integer integer = (Integer)_methods.get(paramString);
    if (integer == null)
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE); 
    switch (integer.intValue()) {
      case 0:
        bindingHolder = new BindingHolder();
        bool1 = false;
        bool1 = next_one(bindingHolder);
        null = paramResponseHandler.createReply();
        null.write_boolean(bool1);
        BindingHelper.write(null, bindingHolder.value);
        return null;
      case 1:
        i = paramInputStream.read_ulong();
        bindingListHolder = new BindingListHolder();
        bool2 = false;
        bool2 = next_n(i, bindingListHolder);
        null = paramResponseHandler.createReply();
        null.write_boolean(bool2);
        BindingListHelper.write(null, bindingListHolder.value);
        return null;
      case 2:
        destroy();
        return paramResponseHandler.createReply();
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  public String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte) { return (String[])__ids.clone(); }
  
  public BindingIterator _this() { return BindingIteratorHelper.narrow(_this_object()); }
  
  public BindingIterator _this(ORB paramORB) { return BindingIteratorHelper.narrow(_this_object(paramORB)); }
  
  static  {
    _methods.put("next_one", new Integer(0));
    _methods.put("next_n", new Integer(1));
    _methods.put("destroy", new Integer(2));
    __ids = new String[] { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\BindingIteratorPOA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */