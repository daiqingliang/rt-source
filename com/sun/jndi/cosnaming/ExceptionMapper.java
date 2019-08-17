package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.NamingManager;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;

public final class ExceptionMapper {
  private static final boolean debug = false;
  
  public static final NamingException mapException(Exception paramException, CNCtx paramCNCtx, NameComponent[] paramArrayOfNameComponent) throws NamingException {
    NamingException namingException;
    if (paramException instanceof NamingException)
      return (NamingException)paramException; 
    if (paramException instanceof RuntimeException)
      throw (RuntimeException)paramException; 
    if (paramException instanceof NotFound) {
      if (paramCNCtx.federation)
        return tryFed((NotFound)paramException, paramCNCtx, paramArrayOfNameComponent); 
      namingException = new NameNotFoundException();
    } else if (paramException instanceof CannotProceed) {
      namingException = new CannotProceedException();
      NamingContext namingContext = ((CannotProceed)paramException).cxt;
      NameComponent[] arrayOfNameComponent = ((CannotProceed)paramException).rest_of_name;
      if (paramArrayOfNameComponent != null && paramArrayOfNameComponent.length > arrayOfNameComponent.length) {
        NameComponent[] arrayOfNameComponent1 = new NameComponent[paramArrayOfNameComponent.length - arrayOfNameComponent.length];
        System.arraycopy(paramArrayOfNameComponent, 0, arrayOfNameComponent1, 0, arrayOfNameComponent1.length);
        namingException.setResolvedObj(new CNCtx(paramCNCtx._orb, paramCNCtx.orbTracker, namingContext, paramCNCtx._env, paramCNCtx.makeFullName(arrayOfNameComponent1)));
      } else {
        namingException.setResolvedObj(paramCNCtx);
      } 
      namingException.setRemainingName(CNNameParser.cosNameToName(arrayOfNameComponent));
    } else if (paramException instanceof org.omg.CosNaming.NamingContextPackage.InvalidName) {
      namingException = new InvalidNameException();
    } else if (paramException instanceof org.omg.CosNaming.NamingContextPackage.AlreadyBound) {
      namingException = new NameAlreadyBoundException();
    } else if (paramException instanceof org.omg.CosNaming.NamingContextPackage.NotEmpty) {
      namingException = new ContextNotEmptyException();
    } else {
      namingException = new NamingException("Unknown reasons");
    } 
    namingException.setRootCause(paramException);
    return namingException;
  }
  
  private static final NamingException tryFed(NotFound paramNotFound, CNCtx paramCNCtx, NameComponent[] paramArrayOfNameComponent) throws NamingException {
    NameComponent[] arrayOfNameComponent1 = paramNotFound.rest_of_name;
    if (arrayOfNameComponent1.length == 1 && paramArrayOfNameComponent != null) {
      NameComponent nameComponent = paramArrayOfNameComponent[paramArrayOfNameComponent.length - 1];
      if (!(arrayOfNameComponent1[0]).id.equals(nameComponent.id) || (arrayOfNameComponent1[false]).kind == null || !(arrayOfNameComponent1[0]).kind.equals(nameComponent.kind)) {
        NameNotFoundException nameNotFoundException = new NameNotFoundException();
        nameNotFoundException.setRemainingName(CNNameParser.cosNameToName(arrayOfNameComponent1));
        nameNotFoundException.setRootCause(paramNotFound);
        throw nameNotFoundException;
      } 
    } 
    NameComponent[] arrayOfNameComponent2 = null;
    int i = 0;
    if (paramArrayOfNameComponent != null && paramArrayOfNameComponent.length >= arrayOfNameComponent1.length) {
      if (paramNotFound.why == NotFoundReason.not_context) {
        i = paramArrayOfNameComponent.length - arrayOfNameComponent1.length - 1;
        if (arrayOfNameComponent1.length == 1) {
          arrayOfNameComponent1 = null;
        } else {
          NameComponent[] arrayOfNameComponent = new NameComponent[arrayOfNameComponent1.length - 1];
          System.arraycopy(arrayOfNameComponent1, 1, arrayOfNameComponent, 0, arrayOfNameComponent.length);
          arrayOfNameComponent1 = arrayOfNameComponent;
        } 
      } else {
        i = paramArrayOfNameComponent.length - arrayOfNameComponent1.length;
      } 
      if (i > 0) {
        arrayOfNameComponent2 = new NameComponent[i];
        System.arraycopy(paramArrayOfNameComponent, 0, arrayOfNameComponent2, 0, i);
      } 
    } 
    CannotProceedException cannotProceedException = new CannotProceedException();
    cannotProceedException.setRootCause(paramNotFound);
    if (arrayOfNameComponent1 != null && arrayOfNameComponent1.length > 0)
      cannotProceedException.setRemainingName(CNNameParser.cosNameToName(arrayOfNameComponent1)); 
    cannotProceedException.setEnvironment(paramCNCtx._env);
    final Object resolvedObj = (arrayOfNameComponent2 != null) ? paramCNCtx.callResolve(arrayOfNameComponent2) : paramCNCtx;
    if (object1 instanceof Context) {
      RefAddr refAddr = new RefAddr("nns") {
          private static final long serialVersionUID = 669984699392133792L;
          
          public Object getContent() { return resolvedObj; }
        };
      Reference reference = new Reference("java.lang.Object", refAddr);
      CompositeName compositeName = new CompositeName();
      compositeName.add("");
      cannotProceedException.setResolvedObj(reference);
      cannotProceedException.setAltName(compositeName);
      cannotProceedException.setAltNameCtx((Context)object1);
      return cannotProceedException;
    } 
    Name name = CNNameParser.cosNameToName(arrayOfNameComponent2);
    Object object2 = null;
    try {
      if (CorbaUtils.isObjectFactoryTrusted(object1))
        object2 = NamingManager.getObjectInstance(object1, name, paramCNCtx, paramCNCtx._env); 
    } catch (NamingException namingException) {
      throw namingException;
    } catch (Exception exception) {
      NamingException namingException = new NamingException("problem generating object using object factory");
      namingException.setRootCause(exception);
      throw namingException;
    } 
    if (object2 instanceof Context) {
      cannotProceedException.setResolvedObj(object2);
    } else {
      name.add("");
      cannotProceedException.setAltName(name);
      final Object rf2 = object2;
      RefAddr refAddr = new RefAddr("nns") {
          private static final long serialVersionUID = -785132553978269772L;
          
          public Object getContent() { return rf2; }
        };
      Reference reference = new Reference("java.lang.Object", refAddr);
      cannotProceedException.setResolvedObj(reference);
      cannotProceedException.setAltNameCtx(paramCNCtx);
    } 
    return cannotProceedException;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\ExceptionMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */