package java.util.prefs;

import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventObject;

public class PreferenceChangeEvent extends EventObject {
  private String key;
  
  private String newValue;
  
  private static final long serialVersionUID = 793724513368024975L;
  
  public PreferenceChangeEvent(Preferences paramPreferences, String paramString1, String paramString2) {
    super(paramPreferences);
    this.key = paramString1;
    this.newValue = paramString2;
  }
  
  public Preferences getNode() { return (Preferences)getSource(); }
  
  public String getKey() { return this.key; }
  
  public String getNewValue() { return this.newValue; }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws NotSerializableException { throw new NotSerializableException("Not serializable."); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws NotSerializableException { throw new NotSerializableException("Not serializable."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\prefs\PreferenceChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */