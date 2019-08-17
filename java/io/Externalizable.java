package java.io;

public interface Externalizable extends Serializable {
  void writeExternal(ObjectOutput paramObjectOutput) throws IOException;
  
  void readExternal(ObjectInput paramObjectInput) throws IOException, ClassNotFoundException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\Externalizable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */