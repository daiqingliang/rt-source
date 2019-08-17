package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import sun.rmi.transport.LiveRef;

public class UnicastRef2 extends UnicastRef {
  private static final long serialVersionUID = 1829537514995881838L;
  
  public UnicastRef2() {}
  
  public UnicastRef2(LiveRef paramLiveRef) { super(paramLiveRef); }
  
  public String getRefClass(ObjectOutput paramObjectOutput) { return "UnicastRef2"; }
  
  public void writeExternal(ObjectOutput paramObjectOutput) throws IOException { this.ref.write(paramObjectOutput, true); }
  
  public void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException { this.ref = LiveRef.read(paramObjectInput, true); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\UnicastRef2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */