package javax.script;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

public interface ScriptContext {
  public static final int ENGINE_SCOPE = 100;
  
  public static final int GLOBAL_SCOPE = 200;
  
  void setBindings(Bindings paramBindings, int paramInt);
  
  Bindings getBindings(int paramInt);
  
  void setAttribute(String paramString, Object paramObject, int paramInt);
  
  Object getAttribute(String paramString, int paramInt);
  
  Object removeAttribute(String paramString, int paramInt);
  
  Object getAttribute(String paramString);
  
  int getAttributesScope(String paramString);
  
  Writer getWriter();
  
  Writer getErrorWriter();
  
  void setWriter(Writer paramWriter);
  
  void setErrorWriter(Writer paramWriter);
  
  Reader getReader();
  
  void setReader(Reader paramReader);
  
  List<Integer> getScopes();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\ScriptContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */