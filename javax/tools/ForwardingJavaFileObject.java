package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public class ForwardingJavaFileObject<F extends JavaFileObject> extends ForwardingFileObject<F> implements JavaFileObject {
  protected ForwardingJavaFileObject(F paramF) { super(paramF); }
  
  public JavaFileObject.Kind getKind() { return ((JavaFileObject)this.fileObject).getKind(); }
  
  public boolean isNameCompatible(String paramString, JavaFileObject.Kind paramKind) { return ((JavaFileObject)this.fileObject).isNameCompatible(paramString, paramKind); }
  
  public NestingKind getNestingKind() { return ((JavaFileObject)this.fileObject).getNestingKind(); }
  
  public Modifier getAccessLevel() { return ((JavaFileObject)this.fileObject).getAccessLevel(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\ForwardingJavaFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */