package javax.management.remote.rmi;

import java.io.IOException;
import java.rmi.Remote;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.ObjectImpl;
import org.omg.CORBA_2_3.portable.OutputStream;

public class _RMIServerImpl_Tie extends ObjectImpl implements Tie {
  private static final String[] _type_ids = { "RMI:javax.management.remote.rmi.RMIServer:0000000000000000" };
  
  public String[] _ids() { return (String[])_type_ids.clone(); }
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler) throws SystemException {
    try {
      RMIServerImpl rMIServerImpl = this.target;
      if (rMIServerImpl == null)
        throw new IOException(); 
      InputStream inputStream = (InputStream)paramInputStream;
      switch (paramString.length()) {
        case 9:
          if (paramString.equals("newClient")) {
            RMIConnection rMIConnection;
            Object object = Util.readAny(inputStream);
            try {
              rMIConnection = rMIServerImpl.newClient(object);
            } catch (IOException iOException) {
              String str = "IDL:java/io/IOEx:1.0";
              OutputStream outputStream1 = (OutputStream)paramResponseHandler.createExceptionReply();
              outputStream1.write_string(str);
              outputStream1.write_value(iOException, IOException.class);
              return outputStream1;
            } 
            OutputStream outputStream = paramResponseHandler.createReply();
            Util.writeRemoteObject(outputStream, rMIConnection);
            return outputStream;
          } 
        case 12:
          if (paramString.equals("_get_version")) {
            String str = rMIServerImpl.getVersion();
            OutputStream outputStream = (OutputStream)paramResponseHandler.createReply();
            outputStream.write_value(str, String.class);
            return outputStream;
          } 
          break;
      } 
      throw new BAD_OPERATION();
    } catch (SystemException systemException) {
      throw systemException;
    } catch (Throwable throwable) {
      throw new UnknownException(throwable);
    } 
  }
  
  public void deactivate() {
    _orb().disconnect(this);
    _set_delegate(null);
    this.target = null;
  }
  
  public Remote getTarget() { return this.target; }
  
  public ORB orb() { return _orb(); }
  
  public void orb(ORB paramORB) { paramORB.connect(this); }
  
  public void setTarget(Remote paramRemote) { this.target = (RMIServerImpl)paramRemote; }
  
  public Object thisObject() { return this; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\remote\rmi\_RMIServerImpl_Tie.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       1.0.7
 */