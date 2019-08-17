package javax.script;

import java.io.Reader;

public interface Compilable {
  CompiledScript compile(String paramString) throws ScriptException;
  
  CompiledScript compile(Reader paramReader) throws ScriptException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\Compilable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */