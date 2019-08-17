package javax.script;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleScriptContext implements ScriptContext {
  protected Writer writer = new PrintWriter(System.out, true);
  
  protected Writer errorWriter = new PrintWriter(System.err, true);
  
  protected Reader reader = new InputStreamReader(System.in);
  
  protected Bindings engineScope = new SimpleBindings();
  
  protected Bindings globalScope = null;
  
  private static List<Integer> scopes = new ArrayList(2);
  
  public void setBindings(Bindings paramBindings, int paramInt) {
    switch (paramInt) {
      case 100:
        if (paramBindings == null)
          throw new NullPointerException("Engine scope cannot be null."); 
        this.engineScope = paramBindings;
        return;
      case 200:
        this.globalScope = paramBindings;
        return;
    } 
    throw new IllegalArgumentException("Invalid scope value.");
  }
  
  public Object getAttribute(String paramString) {
    checkName(paramString);
    return this.engineScope.containsKey(paramString) ? getAttribute(paramString, 100) : ((this.globalScope != null && this.globalScope.containsKey(paramString)) ? getAttribute(paramString, 200) : null);
  }
  
  public Object getAttribute(String paramString, int paramInt) {
    checkName(paramString);
    switch (paramInt) {
      case 100:
        return this.engineScope.get(paramString);
      case 200:
        return (this.globalScope != null) ? this.globalScope.get(paramString) : null;
    } 
    throw new IllegalArgumentException("Illegal scope value.");
  }
  
  public Object removeAttribute(String paramString, int paramInt) {
    checkName(paramString);
    switch (paramInt) {
      case 100:
        return (getBindings(100) != null) ? getBindings(100).remove(paramString) : null;
      case 200:
        return (getBindings('È') != null) ? getBindings(200).remove(paramString) : null;
    } 
    throw new IllegalArgumentException("Illegal scope value.");
  }
  
  public void setAttribute(String paramString, Object paramObject, int paramInt) {
    checkName(paramString);
    switch (paramInt) {
      case 100:
        this.engineScope.put(paramString, paramObject);
        return;
      case 200:
        if (this.globalScope != null)
          this.globalScope.put(paramString, paramObject); 
        return;
    } 
    throw new IllegalArgumentException("Illegal scope value.");
  }
  
  public Writer getWriter() { return this.writer; }
  
  public Reader getReader() { return this.reader; }
  
  public void setReader(Reader paramReader) { this.reader = paramReader; }
  
  public void setWriter(Writer paramWriter) { this.writer = paramWriter; }
  
  public Writer getErrorWriter() { return this.errorWriter; }
  
  public void setErrorWriter(Writer paramWriter) { this.errorWriter = paramWriter; }
  
  public int getAttributesScope(String paramString) {
    checkName(paramString);
    return this.engineScope.containsKey(paramString) ? 100 : ((this.globalScope != null && this.globalScope.containsKey(paramString)) ? 200 : -1);
  }
  
  public Bindings getBindings(int paramInt) {
    if (paramInt == 100)
      return this.engineScope; 
    if (paramInt == 200)
      return this.globalScope; 
    throw new IllegalArgumentException("Illegal scope value.");
  }
  
  public List<Integer> getScopes() { return scopes; }
  
  private void checkName(String paramString) {
    Objects.requireNonNull(paramString);
    if (paramString.isEmpty())
      throw new IllegalArgumentException("name cannot be empty"); 
  }
  
  static  {
    scopes.add(Integer.valueOf(100));
    scopes.add(Integer.valueOf(200));
    scopes = Collections.unmodifiableList(scopes);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\script\SimpleScriptContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */