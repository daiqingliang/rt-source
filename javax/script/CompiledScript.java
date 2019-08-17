package javax.script;

public abstract class CompiledScript {
  public abstract Object eval(ScriptContext paramScriptContext) throws ScriptException;
  
  public Object eval(Bindings paramBindings) throws ScriptException {
    ScriptContext scriptContext = getEngine().getContext();
    if (paramBindings != null) {
      SimpleScriptContext simpleScriptContext = new SimpleScriptContext();
      simpleScriptContext.setBindings(paramBindings, 100);
      simpleScriptContext.setBindings(scriptContext.getBindings(200), 200);
      simpleScriptContext.setWriter(scriptContext.getWriter());
      simpleScriptContext.setReader(scriptContext.getReader());
      simpleScriptContext.setErrorWriter(scriptContext.getErrorWriter());
      scriptContext = simpleScriptContext;
    } 
    return eval(scriptContext);
  }
  
  public Object eval() throws ScriptException { return eval(getEngine().getContext()); }
  
  public abstract ScriptEngine getEngine();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\CompiledScript.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */