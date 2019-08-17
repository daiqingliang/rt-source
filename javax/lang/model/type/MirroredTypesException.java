package javax.lang.model.type;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MirroredTypesException extends RuntimeException {
  private static final long serialVersionUID = 269L;
  
  List<? extends TypeMirror> types;
  
  MirroredTypesException(String paramString, TypeMirror paramTypeMirror) {
    super(paramString);
    ArrayList arrayList = new ArrayList();
    arrayList.add(paramTypeMirror);
    this.types = Collections.unmodifiableList(arrayList);
  }
  
  public MirroredTypesException(List<? extends TypeMirror> paramList) {
    super("Attempt to access Class objects for TypeMirrors " + (paramList = new ArrayList<? extends TypeMirror>(paramList)).toString());
    this.types = Collections.unmodifiableList(paramList);
  }
  
  public List<? extends TypeMirror> getTypeMirrors() { return this.types; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.types = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\MirroredTypesException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */