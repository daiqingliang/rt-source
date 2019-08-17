package org.omg.CosNaming;

import java.util.Dictionary;
import java.util.Hashtable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;

public abstract class _BindingIteratorImplBase extends DynamicImplementation implements BindingIterator {
  private static final String[] _type_ids = { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
  
  private static Dictionary _methods = new Hashtable();
  
  public String[] _ids() { return (String[])_type_ids.clone(); }
  
  public void invoke(ServerRequest paramServerRequest) {
    Any any4;
    boolean bool2;
    BindingListHolder bindingListHolder;
    Any any3;
    boolean bool1;
    int i;
    Any any2;
    BindingHolder bindingHolder;
    Any any1;
    NVList nVList;
    switch (((Integer)_methods.get(paramServerRequest.op_name())).intValue()) {
      case 0:
        nVList = _orb().create_list(0);
        any1 = _orb().create_any();
        any1.type(BindingHelper.type());
        nVList.add_value("b", any1, 2);
        paramServerRequest.params(nVList);
        bindingHolder = new BindingHolder();
        bool1 = next_one(bindingHolder);
        BindingHelper.insert(any1, bindingHolder.value);
        any3 = _orb().create_any();
        any3.insert_boolean(bool1);
        paramServerRequest.result(any3);
        return;
      case 1:
        nVList = _orb().create_list(0);
        any1 = _orb().create_any();
        any1.type(ORB.init().get_primitive_tc(TCKind.tk_ulong));
        nVList.add_value("how_many", any1, 1);
        any2 = _orb().create_any();
        any2.type(BindingListHelper.type());
        nVList.add_value("bl", any2, 2);
        paramServerRequest.params(nVList);
        i = any1.extract_ulong();
        bindingListHolder = new BindingListHolder();
        bool2 = next_n(i, bindingListHolder);
        BindingListHelper.insert(any2, bindingListHolder.value);
        any4 = _orb().create_any();
        any4.insert_boolean(bool2);
        paramServerRequest.result(any4);
        return;
      case 2:
        nVList = _orb().create_list(0);
        paramServerRequest.params(nVList);
        destroy();
        any1 = _orb().create_any();
        any1.type(_orb().get_primitive_tc(TCKind.tk_void));
        paramServerRequest.result(any1);
        return;
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  static  {
    _methods.put("next_one", new Integer(0));
    _methods.put("next_n", new Integer(1));
    _methods.put("destroy", new Integer(2));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\_BindingIteratorImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */