package javax.tools;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

public interface JavaFileObject extends FileObject {
  Kind getKind();
  
  boolean isNameCompatible(String paramString, Kind paramKind);
  
  NestingKind getNestingKind();
  
  Modifier getAccessLevel();
  
  public enum Kind {
    SOURCE(".java"),
    CLASS(".class"),
    HTML(".html"),
    OTHER("");
    
    public final String extension;
    
    Kind(String param1String1) {
      param1String1.getClass();
      this.extension = param1String1;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\tools\JavaFileObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */