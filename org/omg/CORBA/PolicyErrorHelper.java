package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class PolicyErrorHelper {
  private static String _id = "IDL:omg.org/CORBA/PolicyError:1.0";
  
  private static TypeCode __typeCode = null;
  
  private static boolean __active = false;
  
  public static void insert(Any paramAny, PolicyError paramPolicyError) {
    OutputStream outputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(outputStream, paramPolicyError);
    paramAny.read_value(outputStream.create_input_stream(), type());
  }
  
  public static PolicyError extract(Any paramAny) { return read(paramAny.create_input_stream()); }
  
  public static TypeCode type() {
    if (__typeCode == null)
      synchronized (TypeCode.class) {
        if (__typeCode == null) {
          if (__active)
            return ORB.init().create_recursive_tc(_id); 
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode typeCode = null;
          typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
          typeCode = ORB.init().create_alias_tc(PolicyErrorCodeHelper.id(), "PolicyErrorCode", typeCode);
          arrayOfStructMember[0] = new StructMember("reason", typeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "PolicyError", arrayOfStructMember);
          __active = false;
        } 
      }  
    return __typeCode;
  }
  
  public static String id() { return _id; }
  
  public static PolicyError read(InputStream paramInputStream) {
    PolicyError policyError = new PolicyError();
    paramInputStream.read_string();
    policyError.reason = paramInputStream.read_short();
    return policyError;
  }
  
  public static void write(OutputStream paramOutputStream, PolicyError paramPolicyError) {
    paramOutputStream.write_string(id());
    paramOutputStream.write_short(paramPolicyError.reason);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\PolicyErrorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */