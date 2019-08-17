package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;

public abstract class LocationPathPattern extends Pattern {
  private Template _template;
  
  private int _importPrecedence;
  
  private double _priority = NaND;
  
  private int _position = 0;
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return Type.Void; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  public void setTemplate(Template paramTemplate) {
    this._template = paramTemplate;
    this._priority = paramTemplate.getPriority();
    this._importPrecedence = paramTemplate.getImportPrecedence();
    this._position = paramTemplate.getPosition();
  }
  
  public Template getTemplate() { return this._template; }
  
  public final double getPriority() { return Double.isNaN(this._priority) ? getDefaultPriority() : this._priority; }
  
  public double getDefaultPriority() { return 0.5D; }
  
  public boolean noSmallerThan(LocationPathPattern paramLocationPathPattern) {
    if (this._importPrecedence > paramLocationPathPattern._importPrecedence)
      return true; 
    if (this._importPrecedence == paramLocationPathPattern._importPrecedence) {
      if (this._priority > paramLocationPathPattern._priority)
        return true; 
      if (this._priority == paramLocationPathPattern._priority && this._position > paramLocationPathPattern._position)
        return true; 
    } 
    return false;
  }
  
  public abstract StepPattern getKernelPattern();
  
  public abstract void reduceKernelPattern();
  
  public abstract boolean isWildcard();
  
  public int getAxis() {
    StepPattern stepPattern = getKernelPattern();
    return (stepPattern != null) ? stepPattern.getAxis() : 3;
  }
  
  public String toString() { return "root()"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\LocationPathPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */