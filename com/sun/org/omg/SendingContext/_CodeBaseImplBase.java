package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.RepositoryHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _CodeBaseImplBase extends ObjectImpl implements CodeBase, InvokeHandler {
  private static Hashtable _methods = new Hashtable();
  
  private static String[] __ids;
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler) {
    String str4;
    String[] arrayOfString4;
    String[] arrayOfString3;
    FullValueDescription fullValueDescription;
    FullValueDescription[] arrayOfFullValueDescription;
    String str2;
    String str3;
    Repository repository;
    String[] arrayOfString1;
    String str1;
    String[] arrayOfString2;
    OutputStream outputStream = paramResponseHandler.createReply();
    Integer integer = (Integer)_methods.get(paramString);
    if (integer == null)
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE); 
    switch (integer.intValue()) {
      case 0:
        repository = null;
        repository = get_ir();
        RepositoryHelper.write(outputStream, repository);
        return outputStream;
      case 1:
        str3 = RepositoryIdHelper.read(paramInputStream);
        str4 = null;
        str4 = implementation(str3);
        outputStream.write_string(str4);
        return outputStream;
      case 2:
        arrayOfString2 = RepositoryIdSeqHelper.read(paramInputStream);
        str4 = null;
        arrayOfString4 = implementations(arrayOfString2);
        URLSeqHelper.write(outputStream, arrayOfString4);
        return outputStream;
      case 3:
        str2 = RepositoryIdHelper.read(paramInputStream);
        arrayOfString4 = null;
        fullValueDescription = meta(str2);
        FullValueDescriptionHelper.write(outputStream, fullValueDescription);
        return outputStream;
      case 4:
        arrayOfString1 = RepositoryIdSeqHelper.read(paramInputStream);
        fullValueDescription = null;
        arrayOfFullValueDescription = metas(arrayOfString1);
        ValueDescSeqHelper.write(outputStream, arrayOfFullValueDescription);
        return outputStream;
      case 5:
        str1 = RepositoryIdHelper.read(paramInputStream);
        arrayOfFullValueDescription = null;
        arrayOfString3 = bases(str1);
        RepositoryIdSeqHelper.write(outputStream, arrayOfString3);
        return outputStream;
    } 
    throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
  }
  
  public String[] _ids() { return (String[])__ids.clone(); }
  
  static  {
    _methods.put("get_ir", new Integer(0));
    _methods.put("implementation", new Integer(1));
    _methods.put("implementations", new Integer(2));
    _methods.put("meta", new Integer(3));
    _methods.put("metas", new Integer(4));
    _methods.put("bases", new Integer(5));
    __ids = new String[] { "IDL:omg.org/SendingContext/CodeBase:1.0", "IDL:omg.org/SendingContext/RunTime:1.0" };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\omg\SendingContext\_CodeBaseImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */