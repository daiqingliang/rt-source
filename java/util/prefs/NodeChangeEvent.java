package java.util.prefs;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventObject;

public class NodeChangeEvent extends EventObject {
  private Preferences child;
  
  private static final long serialVersionUID = 8068949086596572957L;
  
  public NodeChangeEvent(Preferences paramPreferences1, Preferences paramPreferences2) {
    super(paramPreferences1);
    this.child = paramPreferences2;
  }
  
  public Preferences getParent() { return (Preferences)getSource(); }
  
  public Preferences getChild() { return this.child; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws NotSerializableException { throw new NotSerializableException("Not serializable."); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws NotSerializableException { throw new NotSerializableException("Not serializable."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\prefs\NodeChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */