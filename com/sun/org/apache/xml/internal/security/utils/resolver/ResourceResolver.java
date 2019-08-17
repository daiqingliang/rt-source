package com.sun.org.apache.xml.internal.security.utils.resolver;

import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverDirectHTTP;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverFragment;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverLocalFilesystem;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverXPointer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Attr;

public class ResourceResolver {
  private static Logger log = Logger.getLogger(ResourceResolver.class.getName());
  
  private static List<ResourceResolver> resolverList = new ArrayList();
  
  private final ResourceResolverSpi resolverSpi;
  
  public ResourceResolver(ResourceResolverSpi paramResourceResolverSpi) { this.resolverSpi = paramResourceResolverSpi; }
  
  public static final ResourceResolver getInstance(Attr paramAttr, String paramString) throws ResourceResolverException { return getInstance(paramAttr, paramString, false); }
  
  public static final ResourceResolver getInstance(Attr paramAttr, String paramString, boolean paramBoolean) throws ResourceResolverException {
    ResourceResolverContext resourceResolverContext = new ResourceResolverContext(paramAttr, paramString, paramBoolean);
    return internalGetInstance(resourceResolverContext);
  }
  
  private static <N> ResourceResolver internalGetInstance(ResourceResolverContext paramResourceResolverContext) throws ResourceResolverException {
    synchronized (resolverList) {
      for (ResourceResolver resourceResolver1 : resolverList) {
        ResourceResolver resourceResolver2 = resourceResolver1;
        if (!resourceResolver1.resolverSpi.engineIsThreadSafe())
          try {
            resourceResolver2 = new ResourceResolver((ResourceResolverSpi)resourceResolver1.resolverSpi.getClass().newInstance());
          } catch (InstantiationException instantiationException) {
            throw new ResourceResolverException("", instantiationException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
          } catch (IllegalAccessException illegalAccessException) {
            throw new ResourceResolverException("", illegalAccessException, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
          }  
        if (log.isLoggable(Level.FINE))
          log.log(Level.FINE, "check resolvability by class " + resourceResolver2.getClass().getName()); 
        if (resourceResolver2 != null && resourceResolver2.canResolve(paramResourceResolverContext)) {
          if (paramResourceResolverContext.secureValidation && (resourceResolver2.resolverSpi instanceof ResolverLocalFilesystem || resourceResolver2.resolverSpi instanceof ResolverDirectHTTP)) {
            Object[] arrayOfObject1 = { resourceResolver2.resolverSpi.getClass().getName() };
            throw new ResourceResolverException("signature.Reference.ForbiddenResolver", arrayOfObject1, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
          } 
          return resourceResolver2;
        } 
      } 
    } 
    Object[] arrayOfObject = { (paramResourceResolverContext.uriToResolve != null) ? paramResourceResolverContext.uriToResolve : "null", paramResourceResolverContext.baseUri };
    throw new ResourceResolverException("utils.resolver.noClass", arrayOfObject, paramResourceResolverContext.attr, paramResourceResolverContext.baseUri);
  }
  
  public static ResourceResolver getInstance(Attr paramAttr, String paramString, List<ResourceResolver> paramList) throws ResourceResolverException { return getInstance(paramAttr, paramString, paramList, false); }
  
  public static ResourceResolver getInstance(Attr paramAttr, String paramString, List<ResourceResolver> paramList, boolean paramBoolean) throws ResourceResolverException {
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "I was asked to create a ResourceResolver and got " + ((paramList == null) ? 0 : paramList.size())); 
    ResourceResolverContext resourceResolverContext = new ResourceResolverContext(paramAttr, paramString, paramBoolean);
    if (paramList != null)
      for (byte b = 0; b < paramList.size(); b++) {
        ResourceResolver resourceResolver = (ResourceResolver)paramList.get(b);
        if (resourceResolver != null) {
          if (log.isLoggable(Level.FINE)) {
            String str = resourceResolver.resolverSpi.getClass().getName();
            log.log(Level.FINE, "check resolvability by class " + str);
          } 
          if (resourceResolver.canResolve(resourceResolverContext))
            return resourceResolver; 
        } 
      }  
    return internalGetInstance(resourceResolverContext);
  }
  
  public static void register(String paramString) {
    JavaUtils.checkRegisterPermission();
    try {
      Class clazz = Class.forName(paramString);
      register(clazz, false);
    } catch (ClassNotFoundException classNotFoundException) {
      log.log(Level.WARNING, "Error loading resolver " + paramString + " disabling it");
    } 
  }
  
  public static void registerAtStart(String paramString) {
    JavaUtils.checkRegisterPermission();
    try {
      Class clazz = Class.forName(paramString);
      register(clazz, true);
    } catch (ClassNotFoundException classNotFoundException) {
      log.log(Level.WARNING, "Error loading resolver " + paramString + " disabling it");
    } 
  }
  
  public static void register(Class<? extends ResourceResolverSpi> paramClass, boolean paramBoolean) {
    JavaUtils.checkRegisterPermission();
    try {
      ResourceResolverSpi resourceResolverSpi = (ResourceResolverSpi)paramClass.newInstance();
      register(resourceResolverSpi, paramBoolean);
    } catch (IllegalAccessException illegalAccessException) {
      log.log(Level.WARNING, "Error loading resolver " + paramClass + " disabling it");
    } catch (InstantiationException instantiationException) {
      log.log(Level.WARNING, "Error loading resolver " + paramClass + " disabling it");
    } 
  }
  
  public static void register(ResourceResolverSpi paramResourceResolverSpi, boolean paramBoolean) {
    JavaUtils.checkRegisterPermission();
    synchronized (resolverList) {
      if (paramBoolean) {
        resolverList.add(0, new ResourceResolver(paramResourceResolverSpi));
      } else {
        resolverList.add(new ResourceResolver(paramResourceResolverSpi));
      } 
    } 
    if (log.isLoggable(Level.FINE))
      log.log(Level.FINE, "Registered resolver: " + paramResourceResolverSpi.toString()); 
  }
  
  public static void registerDefaultResolvers() {
    synchronized (resolverList) {
      resolverList.add(new ResourceResolver(new ResolverFragment()));
      resolverList.add(new ResourceResolver(new ResolverLocalFilesystem()));
      resolverList.add(new ResourceResolver(new ResolverXPointer()));
      resolverList.add(new ResourceResolver(new ResolverDirectHTTP()));
    } 
  }
  
  @Deprecated
  public XMLSignatureInput resolve(Attr paramAttr, String paramString) throws ResourceResolverException { return resolve(paramAttr, paramString, true); }
  
  public XMLSignatureInput resolve(Attr paramAttr, String paramString, boolean paramBoolean) throws ResourceResolverException {
    ResourceResolverContext resourceResolverContext = new ResourceResolverContext(paramAttr, paramString, paramBoolean);
    return this.resolverSpi.engineResolveURI(resourceResolverContext);
  }
  
  public void setProperty(String paramString1, String paramString2) { this.resolverSpi.engineSetProperty(paramString1, paramString2); }
  
  public String getProperty(String paramString) { return this.resolverSpi.engineGetProperty(paramString); }
  
  public void addProperties(Map<String, String> paramMap) { this.resolverSpi.engineAddProperies(paramMap); }
  
  public String[] getPropertyKeys() { return this.resolverSpi.engineGetPropertyKeys(); }
  
  public boolean understandsProperty(String paramString) { return this.resolverSpi.understandsProperty(paramString); }
  
  private boolean canResolve(ResourceResolverContext paramResourceResolverContext) { return this.resolverSpi.engineCanResolveURI(paramResourceResolverContext); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\securit\\utils\resolver\ResourceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */