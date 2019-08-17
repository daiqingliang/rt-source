package javax.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;

public class MirroredTypeException extends MirroredTypesException {
  private static final long serialVersionUID = 269L;
  
  private TypeMirror type;
  
  public MirroredTypeException(TypeMirror paramTypeMirror) {
    super("Attempt to access Class object for TypeMirror " + paramTypeMirror.toString(), paramTypeMirror);
    this.type = paramTypeMirror;
  }
  
  public TypeMirror getTypeMirror() { return this.type; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.type = null;
    this.types = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\MirroredTypeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */