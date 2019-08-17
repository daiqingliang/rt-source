package sun.rmi.log;

import java.io.InputStream;
import java.io.OutputStream;
import sun.rmi.server.MarshalInputStream;
import sun.rmi.server.MarshalOutputStream;

public abstract class LogHandler {
  public abstract Object initialSnapshot() throws Exception;
  
  public void snapshot(OutputStream paramOutputStream, Object paramObject) throws Exception {
    MarshalOutputStream marshalOutputStream = new MarshalOutputStream(paramOutputStream);
    marshalOutputStream.writeObject(paramObject);
    marshalOutputStream.flush();
  }
  
  public Object recover(InputStream paramInputStream) throws Exception {
    MarshalInputStream marshalInputStream = new MarshalInputStream(paramInputStream);
    return marshalInputStream.readObject();
  }
  
  public void writeUpdate(LogOutputStream paramLogOutputStream, Object paramObject) throws Exception {
    MarshalOutputStream marshalOutputStream = new MarshalOutputStream(paramLogOutputStream);
    marshalOutputStream.writeObject(paramObject);
    marshalOutputStream.flush();
  }
  
  public Object readUpdate(LogInputStream paramLogInputStream, Object paramObject) throws Exception {
    MarshalInputStream marshalInputStream = new MarshalInputStream(paramLogInputStream);
    return applyUpdate(marshalInputStream.readObject(), paramObject);
  }
  
  public abstract Object applyUpdate(Object paramObject1, Object paramObject2) throws Exception;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\log\LogHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */