package javax.lang.model.type;

import java.util.List;

public interface ExecutableType extends TypeMirror {
  List<? extends TypeVariable> getTypeVariables();
  
  TypeMirror getReturnType();
  
  List<? extends TypeMirror> getParameterTypes();
  
  TypeMirror getReceiverType();
  
  List<? extends TypeMirror> getThrownTypes();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\ExecutableType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */