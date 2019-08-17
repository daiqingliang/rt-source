package javax.script;

import java.io.Reader;

public abstract class AbstractScriptEngine implements ScriptEngine {
  protected ScriptContext context = new SimpleScriptContext();
  
  public AbstractScriptEngine() {}
  
  public AbstractScriptEngine(Bindings paramBindings) {
    this();
    if (paramBindings == null)
      throw new NullPointerException("n is null"); 
    this.context.setBindings(paramBindings, 100);
  }
  
  public void setContext(ScriptContext paramScriptContext) {
    if (paramScriptContext == null)
      throw new NullPointerException("null context"); 
    this.context = paramScriptContext;
  }
  
  public ScriptContext getContext() { return this.context; }
  
  public Bindings getBindings(int paramInt) {
    if (paramInt == 200)
      return this.context.getBindings(200); 
    if (paramInt == 100)
      return this.context.getBindings(100); 
    throw new IllegalArgumentException("Invalid scope value.");
  }
  
  public void setBindings(Bindings paramBindings, int paramInt) {
    if (paramInt == 200) {
      this.context.setBindings(paramBindings, 200);
    } else if (paramInt == 100) {
      this.context.setBindings(paramBindings, 100);
    } else {
      throw new IllegalArgumentException("Invalid scope value.");
    } 
  }
  
  public void put(String paramString, Object paramObject) {
    Bindings bindings = getBindings(100);
    if (bindings != null)
      bindings.put(paramString, paramObject); 
  }
  
  public Object get(String paramString) {
    Bindings bindings = getBindings(100);
    return (bindings != null) ? bindings.get(paramString) : null;
  }
  
  public Object eval(Reader paramReader, Bindings paramBindings) throws ScriptException {
    ScriptContext scriptContext = getScriptContext(paramBindings);
    return eval(paramReader, scriptContext);
  }
  
  public Object eval(String paramString, Bindings paramBindings) throws ScriptException {
    ScriptContext scriptContext = getScriptContext(paramBindings);
    return eval(paramString, scriptContext);
  }
  
  public Object eval(Reader paramReader) throws ScriptException { return eval(paramReader, this.context); }
  
  public Object eval(String paramString) { return eval(paramString, this.context); }
  
  protected ScriptContext getScriptContext(Bindings paramBindings) {
    SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
    Bindings bindings = getBindings(200);
    if (bindings != null)
      simpleScriptContext.setBindings(bindings, 200); 
    if (paramBindings != null) {
      simpleScriptContext.setBindings(paramBindings, 100);
    } else {
      throw new NullPointerException("Engine scope Bindings may not be null.");
    } 
    simpleScriptContext.setReader(this.context.getReader());
    simpleScriptContext.setWriter(this.context.getWriter());
    simpleScriptContext.setErrorWriter(this.context.getErrorWriter());
    return simpleScriptContext;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\AbstractScriptEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */