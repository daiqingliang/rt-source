package javax.script;

import java.util.List;

public interface ScriptEngineFactory {
  String getEngineName();
  
  String getEngineVersion();
  
  List<String> getExtensions();
  
  List<String> getMimeTypes();
  
  List<String> getNames();
  
  String getLanguageName();
  
  String getLanguageVersion();
  
  Object getParameter(String paramString);
  
  String getMethodCallSyntax(String paramString1, String paramString2, String... paramVarArgs);
  
  String getOutputStatement(String paramString);
  
  String getProgram(String... paramVarArgs);
  
  ScriptEngine getScriptEngine();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\ScriptEngineFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */