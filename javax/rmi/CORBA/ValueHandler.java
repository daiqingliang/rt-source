package javax.rmi.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.SendingContext.RunTime;

public interface ValueHandler {
  void writeValue(OutputStream paramOutputStream, Serializable paramSerializable);
  
  Serializable readValue(InputStream paramInputStream, int paramInt, Class paramClass, String paramString, RunTime paramRunTime);
  
  String getRMIRepositoryID(Class paramClass);
  
  boolean isCustomMarshaled(Class paramClass);
  
  RunTime getRunTimeCodeBase();
  
  Serializable writeReplace(Serializable paramSerializable);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\CORBA\ValueHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */