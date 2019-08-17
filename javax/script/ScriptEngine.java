package javax.script;

import java.io.Reader;

public interface ScriptEngine {
  public static final String ARGV = "javax.script.argv";
  
  public static final String FILENAME = "javax.script.filename";
  
  public static final String ENGINE = "javax.script.engine";
  
  public static final String ENGINE_VERSION = "javax.script.engine_version";
  
  public static final String NAME = "javax.script.name";
  
  public static final String LANGUAGE = "javax.script.language";
  
  public static final String LANGUAGE_VERSION = "javax.script.language_version";
  
  Object eval(String paramString, ScriptContext paramScriptContext) throws ScriptException;
  
  Object eval(Reader paramReader, ScriptContext paramScriptContext) throws ScriptException;
  
  Object eval(String paramString) throws ScriptException;
  
  Object eval(Reader paramReader) throws ScriptException;
  
  Object eval(String paramString, Bindings paramBindings) throws ScriptException;
  
  Object eval(Reader paramReader, Bindings paramBindings) throws ScriptException;
  
  void put(String paramString, Object paramObject);
  
  Object get(String paramString) throws ScriptException;
  
  Bindings getBindings(int paramInt);
  
  void setBindings(Bindings paramBindings, int paramInt);
  
  Bindings createBindings();
  
  ScriptContext getContext();
  
  void setContext(ScriptContext paramScriptContext);
  
  ScriptEngineFactory getFactory();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\ScriptEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */