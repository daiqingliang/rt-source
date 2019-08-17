package javax.lang.model.type;

public interface WildcardType extends TypeMirror {
  TypeMirror getExtendsBound();
  
  TypeMirror getSuperBound();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\lang\model\type\WildcardType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */