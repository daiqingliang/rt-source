package com.sun.jndi.toolkit.ctx;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ResolveResult;

public class Continuation extends ResolveResult {
  protected Name starter;
  
  protected Object followingLink = null;
  
  protected Hashtable<?, ?> environment = null;
  
  protected boolean continuing = false;
  
  protected Context resolvedContext = null;
  
  protected Name relativeResolvedName = null;
  
  private static final long serialVersionUID = 8162530656132624308L;
  
  public Continuation() {}
  
  public Continuation(Name paramName, Hashtable<?, ?> paramHashtable) {
    this.starter = paramName;
    this.environment = (Hashtable)((paramHashtable == null) ? null : paramHashtable.clone());
  }
  
  public boolean isContinue() { return this.continuing; }
  
  public void setSuccess() { this.continuing = false; }
  
  public NamingException fillInException(NamingException paramNamingException) {
    paramNamingException.setRemainingName(this.remainingName);
    paramNamingException.setResolvedObj(this.resolvedObj);
    if (this.starter == null || this.starter.isEmpty()) {
      paramNamingException.setResolvedName(null);
    } else if (this.remainingName == null) {
      paramNamingException.setResolvedName(this.starter);
    } else {
      paramNamingException.setResolvedName(this.starter.getPrefix(this.starter.size() - this.remainingName.size()));
    } 
    if (paramNamingException instanceof CannotProceedException) {
      CannotProceedException cannotProceedException = (CannotProceedException)paramNamingException;
      Hashtable hashtable = (this.environment == null) ? new Hashtable(11) : (Hashtable)this.environment.clone();
      cannotProceedException.setEnvironment(hashtable);
      cannotProceedException.setAltNameCtx(this.resolvedContext);
      cannotProceedException.setAltName(this.relativeResolvedName);
    } 
    return paramNamingException;
  }
  
  public void setErrorNNS(Object paramObject, Name paramName) {
    Name name = (Name)paramName.clone();
    try {
      name.add("");
    } catch (InvalidNameException invalidNameException) {}
    setErrorAux(paramObject, name);
  }
  
  public void setErrorNNS(Object paramObject, String paramString) {
    CompositeName compositeName = new CompositeName();
    try {
      if (paramString != null && !paramString.equals(""))
        compositeName.add(paramString); 
      compositeName.add("");
    } catch (InvalidNameException invalidNameException) {}
    setErrorAux(paramObject, compositeName);
  }
  
  public void setError(Object paramObject, Name paramName) {
    if (paramName != null) {
      this.remainingName = (Name)paramName.clone();
    } else {
      this.remainingName = null;
    } 
    setErrorAux(paramObject, this.remainingName);
  }
  
  public void setError(Object paramObject, String paramString) {
    CompositeName compositeName = new CompositeName();
    if (paramString != null && !paramString.equals(""))
      try {
        compositeName.add(paramString);
      } catch (InvalidNameException invalidNameException) {} 
    setErrorAux(paramObject, compositeName);
  }
  
  private void setErrorAux(Object paramObject, Name paramName) {
    this.remainingName = paramName;
    this.resolvedObj = paramObject;
    this.continuing = false;
  }
  
  private void setContinueAux(Object paramObject, Name paramName1, Context paramContext, Name paramName2) {
    if (paramObject instanceof javax.naming.LinkRef) {
      setContinueLink(paramObject, paramName1, paramContext, paramName2);
    } else {
      this.remainingName = paramName2;
      this.resolvedObj = paramObject;
      this.relativeResolvedName = paramName1;
      this.resolvedContext = paramContext;
      this.continuing = true;
    } 
  }
  
  public void setContinueNNS(Object paramObject, Name paramName, Context paramContext) {
    CompositeName compositeName = new CompositeName();
    setContinue(paramObject, paramName, paramContext, PartialCompositeContext._NNS_NAME);
  }
  
  public void setContinueNNS(Object paramObject, String paramString, Context paramContext) {
    CompositeName compositeName = new CompositeName();
    try {
      compositeName.add(paramString);
    } catch (NamingException namingException) {}
    setContinue(paramObject, compositeName, paramContext, PartialCompositeContext._NNS_NAME);
  }
  
  public void setContinue(Object paramObject, Name paramName, Context paramContext) { setContinueAux(paramObject, paramName, paramContext, (Name)PartialCompositeContext._EMPTY_NAME.clone()); }
  
  public void setContinue(Object paramObject, Name paramName1, Context paramContext, Name paramName2) {
    if (paramName2 != null) {
      this.remainingName = (Name)paramName2.clone();
    } else {
      this.remainingName = new CompositeName();
    } 
    setContinueAux(paramObject, paramName1, paramContext, this.remainingName);
  }
  
  public void setContinue(Object paramObject, String paramString1, Context paramContext, String paramString2) {
    CompositeName compositeName1 = new CompositeName();
    if (!paramString1.equals(""))
      try {
        compositeName1.add(paramString1);
      } catch (NamingException namingException) {} 
    CompositeName compositeName2 = new CompositeName();
    if (!paramString2.equals(""))
      try {
        compositeName2.add(paramString2);
      } catch (NamingException namingException) {} 
    setContinueAux(paramObject, compositeName1, paramContext, compositeName2);
  }
  
  @Deprecated
  public void setContinue(Object paramObject1, Object paramObject2) { setContinue(paramObject1, null, (Context)paramObject2); }
  
  private void setContinueLink(Object paramObject, Name paramName1, Context paramContext, Name paramName2) {
    this.followingLink = paramObject;
    this.remainingName = paramName2;
    this.resolvedObj = paramContext;
    this.relativeResolvedName = PartialCompositeContext._EMPTY_NAME;
    this.resolvedContext = paramContext;
    this.continuing = true;
  }
  
  public String toString() { return (this.remainingName != null) ? (this.starter.toString() + "; remainingName: '" + this.remainingName + "'") : this.starter.toString(); }
  
  public String toString(boolean paramBoolean) { return (!paramBoolean || this.resolvedObj == null) ? toString() : (toString() + "; resolvedObj: " + this.resolvedObj + "; relativeResolvedName: " + this.relativeResolvedName + "; resolvedContext: " + this.resolvedContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\ctx\Continuation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */