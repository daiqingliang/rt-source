package org.omg.CosNaming;

import java.util.Dictionary;
import java.util.Hashtable;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.DynamicImplementation;
import org.omg.CORBA.NVList;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.ServerRequest;
import org.omg.CORBA.TCKind;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;

public abstract class _NamingContextImplBase extends DynamicImplementation implements NamingContext {
  private static final String[] _type_ids = { "IDL:omg.org/CosNaming/NamingContext:1.0" };
  
  private static Dictionary _methods = new Hashtable();
  
  public String[] _ids() { return (String[])_type_ids.clone(); }
  
  public void invoke(ServerRequest paramServerRequest) {
    Any any8;
    BindingIteratorHolder bindingIteratorHolder;
    BindingListHolder bindingListHolder;
    Any any5;
    Any any6;
    NamingContext namingContext2;
    NameComponent[] arrayOfNameComponent2;
    NameComponent[] arrayOfNameComponent1;
    Any any4;
    Any any3;
    NamingContext namingContext1;
    Any any2;
    NVList nVList;
    switch (((Integer)_methods.get(paramServerRequest.op_name())).intValue()) {
      case 0:
        nVList = _orb().create_list(0);
        any2 = _orb().create_any();
        any2.type(NameHelper.type());
        nVList.add_value("n", any2, 1);
        any4 = _orb().create_any();
        any4.type(ORB.init().get_primitive_tc(TCKind.tk_objref));
        nVList.add_value("obj", any4, 1);
        paramServerRequest.params(nVList);
        object1 = NameHelper.extract(any2);
        object2 = any4.extract_Object();
        try {
          bind(object1, object2);
        } catch (NotFound notFound) {
          Any any = _orb().create_any();
          NotFoundHelper.insert(any, notFound);
          paramServerRequest.except(any);
          return;
        } catch (CannotProceed cannotProceed) {
          Any any = _orb().create_any();
          CannotProceedHelper.insert(any, cannotProceed);
          paramServerRequest.except(any);
          return;
        } catch (InvalidName invalidName) {
          Any any = _orb().create_any();
          InvalidNameHelper.insert(any, invalidName);
          paramServerRequest.except(any);
          return;
        } catch (AlreadyBound alreadyBound) {
          Any any = _orb().create_any();
          AlreadyBoundHelper.insert(any, alreadyBound);
          paramServerRequest.except(any);
          return;
        } 
        any7 = _orb().create_any();
        any7.type(_orb().get_primitive_tc(TCKind.tk_void));
        paramServerRequest.result(any7);
        return;
      case 1:
        nVList = _orb().create_list(0);
        any2 = _orb().create_any();
        any2.type(NameHelper.type());
        nVList.add_value("n", any2, 1);
        any4 = _orb().create_any();
        any4.type(NamingContextHelper.type());
        nVList.add_value("nc", any4, 1);
        paramServerRequest.params(nVList);
        object1 = NameHelper.extract(any2);
        object2 = NamingContextHelper.extract(any4);
        try {
          bind_context(object1, object2);
        } catch (NotFound any7) {
          Any any = _orb().create_any();
          NotFoundHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } catch (CannotProceed any7) {
          Any any = _orb().create_any();
          CannotProceedHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } catch (InvalidName any7) {
          Any any = _orb().create_any();
          InvalidNameHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } catch (AlreadyBound any7) {
          Any any = _orb().create_any();
          AlreadyBoundHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } 
        any7 = _orb().create_any();
        any7.type(_orb().get_primitive_tc(TCKind.tk_void));
        paramServerRequest.result(any7);
        return;
      case 2:
        nVList = _orb().create_list(0);
        any2 = _orb().create_any();
        any2.type(NameHelper.type());
        nVList.add_value("n", any2, 1);
        any4 = _orb().create_any();
        any4.type(ORB.init().get_primitive_tc(TCKind.tk_objref));
        nVList.add_value("obj", any4, 1);
        paramServerRequest.params(nVList);
        object1 = NameHelper.extract(any2);
        object2 = any4.extract_Object();
        try {
          rebind(object1, object2);
        } catch (NotFound any7) {
          Any any = _orb().create_any();
          NotFoundHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } catch (CannotProceed any7) {
          Any any = _orb().create_any();
          CannotProceedHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } catch (InvalidName any7) {
          Any any = _orb().create_any();
          InvalidNameHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } 
        any7 = _orb().create_any();
        any7.type(_orb().get_primitive_tc(TCKind.tk_void));
        paramServerRequest.result(any7);
        return;
      case 3:
        nVList = _orb().create_list(0);
        any2 = _orb().create_any();
        any2.type(NameHelper.type());
        nVList.add_value("n", any2, 1);
        any4 = _orb().create_any();
        any4.type(NamingContextHelper.type());
        nVList.add_value("nc", any4, 1);
        paramServerRequest.params(nVList);
        object1 = NameHelper.extract(any2);
        object2 = NamingContextHelper.extract(any4);
        try {
          rebind_context(object1, object2);
        } catch (NotFound any7) {
          Any any = _orb().create_any();
          NotFoundHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } catch (CannotProceed any7) {
          Any any = _orb().create_any();
          CannotProceedHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } catch (InvalidName any7) {
          Any any = _orb().create_any();
          InvalidNameHelper.insert(any, any7);
          paramServerRequest.except(any);
          return;
        } 
        any7 = _orb().create_any();
        any7.type(_orb().get_primitive_tc(TCKind.tk_void));
        paramServerRequest.result(any7);
        return;
      case 4:
        nVList = _orb().create_list(0);
        any2 = _orb().create_any();
        any2.type(NameHelper.type());
        nVList.add_value("n", any2, 1);
        paramServerRequest.params(nVList);
        arrayOfNameComponent2 = NameHelper.extract(any2);
        try {
          object1 = resolve(arrayOfNameComponent2);
        } catch (NotFound object2) {
          any7 = _orb().create_any();
          NotFoundHelper.insert(any7, object2);
          paramServerRequest.except(any7);
          return;
        } catch (CannotProceed object2) {
          any7 = _orb().create_any();
          CannotProceedHelper.insert(any7, object2);
          paramServerRequest.except(any7);
          return;
        } catch (InvalidName object2) {
          any7 = _orb().create_any();
          InvalidNameHelper.insert(any7, object2);
          paramServerRequest.except(any7);
          return;
        } 
        any6 = _orb().create_any();
        any6.insert_Object(object1);
        paramServerRequest.result(any6);
        return;
      case 5:
        nVList = _orb().create_list(0);
        any2 = _orb().create_any();
        any2.type(NameHelper.type());
        nVList.add_value("n", any2, 1);
        paramServerRequest.params(nVList);
        arrayOfNameComponent2 = NameHelper.extract(any2);
        try {
          unbind(arrayOfNameComponent2);
        } catch (NotFound object1) {
          any6 = _orb().create_any();
          NotFoundHelper.insert(any6, object1);
          paramServerRequest.except(any6);
          return;
        } catch (CannotProceed object1) {
          any6 = _orb().create_any();
          CannotProceedHelper.insert(any6, object1);
          paramServerRequest.except(any6);
          return;
        } catch (InvalidName object1) {
          any6 = _orb().create_any();
          InvalidNameHelper.insert(any6, object1);
          paramServerRequest.except(any6);
          return;
        } 
        namingContext2 = _orb().create_any();
        namingContext2.type(_orb().get_primitive_tc(TCKind.tk_void));
        paramServerRequest.result(namingContext2);
        return;
      case 6:
        nVList = _orb().create_list(0);
        any2 = _orb().create_any();
        any2.type(ORB.init().get_primitive_tc(TCKind.tk_ulong));
        nVList.add_value("how_many", any2, 1);
        any3 = _orb().create_any();
        any3.type(BindingListHelper.type());
        nVList.add_value("bl", any3, 2);
        namingContext2 = _orb().create_any();
        namingContext2.type(BindingIteratorHelper.type());
        nVList.add_value("bi", namingContext2, 2);
        paramServerRequest.params(nVList);
        i = any2.extract_ulong();
        bindingListHolder = new BindingListHolder();
        bindingIteratorHolder = new BindingIteratorHolder();
        list(i, bindingListHolder, bindingIteratorHolder);
        BindingListHelper.insert(any3, bindingListHolder.value);
        BindingIteratorHelper.insert(namingContext2, bindingIteratorHolder.value);
        any8 = _orb().create_any();
        any8.type(_orb().get_primitive_tc(TCKind.tk_void));
        paramServerRequest.result(any8);
        return;
      case 7:
        nVList = _orb().create_list(0);
        paramServerRequest.params(nVList);
        namingContext1 = new_context();
        any3 = _orb().create_any();
        NamingContextHelper.insert(any3, namingContext1);
        paramServerRequest.result(any3);
        return;
      case 8:
        nVList = _orb().create_list(0);
        any1 = _orb().create_any();
        any1.type(NameHelper.type());
        nVList.add_value("n", any1, 1);
        paramServerRequest.params(nVList);
        arrayOfNameComponent1 = NameHelper.extract(any1);
        try {
          namingContext2 = bind_new_context(arrayOfNameComponent1);
        } catch (NotFound i) {
          NotFound notFound;
          Any any = _orb().create_any();
          NotFoundHelper.insert(any, notFound);
          paramServerRequest.except(any);
          return;
        } catch (AlreadyBound i) {
          AlreadyBound alreadyBound;
          Any any = _orb().create_any();
          AlreadyBoundHelper.insert(any, alreadyBound);
          paramServerRequest.except(any);
          return;
        } catch (CannotProceed i) {
          CannotProceed cannotProceed;
          Any any = _orb().create_any();
          CannotProceedHelper.insert(any, cannotProceed);
          paramServerRequest.except(any);
          return;
        } catch (InvalidName i) {
          InvalidName invalidName;
          Any any = _orb().create_any();
          InvalidNameHelper.insert(any, invalidName);
          paramServerRequest.except(any);
          return;
        } 
        any5 = _orb().create_any();
        NamingContextHelper.insert(any5, namingContext2);
        paramServerRequest.result(any5);
        return;
      case 9:
        nVList = _orb().create_list(0);
        paramServerRequest.params(nVList);
        try {
          destroy();
        } catch (NotEmpty any1) {
          Any any = _orb().create_any();
          NotEmptyHelper.insert(any, any1);
          paramServerRequest.except(any);
          return;
        } 
        any1 = _orb().create_any();
        any1.type(_orb().get_primitive_tc(TCKind.tk_void));
        paramServerRequest.result(any1);
        return;
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  static  {
    _methods.put("bind", new Integer(0));
    _methods.put("bind_context", new Integer(1));
    _methods.put("rebind", new Integer(2));
    _methods.put("rebind_context", new Integer(3));
    _methods.put("resolve", new Integer(4));
    _methods.put("unbind", new Integer(5));
    _methods.put("list", new Integer(6));
    _methods.put("new_context", new Integer(7));
    _methods.put("bind_new_context", new Integer(8));
    _methods.put("destroy", new Integer(9));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CosNaming\_NamingContextImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */