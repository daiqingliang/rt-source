package com.sun.org.apache.xml.internal.security.keys.keyresolver;

import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DEREncodedKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.DSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.KeyInfoReferenceResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.RetrievalMethodResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509CertificateResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509DigestResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509IssuerSerialResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SKIResolver;
import com.sun.org.apache.xml.internal.security.keys.keyresolver.implementations.X509SubjectNameResolver;
import com.sun.org.apache.xml.internal.security.keys.storage.StorageResolver;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.w3c.dom.Element;

public class KeyResolver {
  private static Logger log = Logger.getLogger(KeyResolver.class.getName());
  
  private static List<KeyResolver> resolverVector = new CopyOnWriteArrayList();
  
  private final KeyResolverSpi resolverSpi;
  
  private KeyResolver(KeyResolverSpi paramKeyResolverSpi) { this.resolverSpi = paramKeyResolverSpi; }
  
  public static int length() { return resolverVector.size(); }
  
  public static final X509Certificate getX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    for (KeyResolver keyResolver : resolverVector) {
      if (keyResolver == null) {
        Object[] arrayOfObject1 = { (paramElement != null && paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
        throw new KeyResolverException("utils.resolver.noClass", arrayOfObject1);
      } 
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "check resolvability by class " + keyResolver.getClass()); 
      X509Certificate x509Certificate = keyResolver.resolveX509Certificate(paramElement, paramString, paramStorageResolver);
      if (x509Certificate != null)
        return x509Certificate; 
    } 
    Object[] arrayOfObject = { (paramElement != null && paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
    throw new KeyResolverException("utils.resolver.noClass", arrayOfObject);
  }
  
  public static final PublicKey getPublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException {
    for (KeyResolver keyResolver : resolverVector) {
      if (keyResolver == null) {
        Object[] arrayOfObject1 = { (paramElement != null && paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
        throw new KeyResolverException("utils.resolver.noClass", arrayOfObject1);
      } 
      if (log.isLoggable(Level.FINE))
        log.log(Level.FINE, "check resolvability by class " + keyResolver.getClass()); 
      PublicKey publicKey = keyResolver.resolvePublicKey(paramElement, paramString, paramStorageResolver);
      if (publicKey != null)
        return publicKey; 
    } 
    Object[] arrayOfObject = { (paramElement != null && paramElement.getNodeType() == 1) ? paramElement.getTagName() : "null" };
    throw new KeyResolverException("utils.resolver.noClass", arrayOfObject);
  }
  
  public static void register(String paramString, boolean paramBoolean) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    JavaUtils.checkRegisterPermission();
    KeyResolverSpi keyResolverSpi = (KeyResolverSpi)Class.forName(paramString).newInstance();
    keyResolverSpi.setGlobalResolver(paramBoolean);
    register(keyResolverSpi, false);
  }
  
  public static void registerAtStart(String paramString, boolean paramBoolean) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    JavaUtils.checkRegisterPermission();
    KeyResolverSpi keyResolverSpi = null;
    InstantiationException instantiationException = null;
    try {
      keyResolverSpi = (KeyResolverSpi)Class.forName(paramString).newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      instantiationException = classNotFoundException;
    } catch (IllegalAccessException illegalAccessException2) {
      IllegalAccessException illegalAccessException1 = illegalAccessException2;
    } catch (InstantiationException instantiationException1) {
      instantiationException = instantiationException1;
    } 
    if (instantiationException != null)
      throw (IllegalArgumentException)(new IllegalArgumentException("Invalid KeyResolver class name")).initCause(instantiationException); 
    keyResolverSpi.setGlobalResolver(paramBoolean);
    register(keyResolverSpi, true);
  }
  
  public static void register(KeyResolverSpi paramKeyResolverSpi, boolean paramBoolean) {
    JavaUtils.checkRegisterPermission();
    KeyResolver keyResolver = new KeyResolver(paramKeyResolverSpi);
    if (paramBoolean) {
      resolverVector.add(0, keyResolver);
    } else {
      resolverVector.add(keyResolver);
    } 
  }
  
  public static void registerClassNames(List<String> paramList) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
    JavaUtils.checkRegisterPermission();
    ArrayList arrayList = new ArrayList(paramList.size());
    for (String str : paramList) {
      KeyResolverSpi keyResolverSpi = (KeyResolverSpi)Class.forName(str).newInstance();
      keyResolverSpi.setGlobalResolver(false);
      arrayList.add(new KeyResolver(keyResolverSpi));
    } 
    resolverVector.addAll(arrayList);
  }
  
  public static void registerDefaultResolvers() {
    ArrayList arrayList = new ArrayList();
    arrayList.add(new KeyResolver(new RSAKeyValueResolver()));
    arrayList.add(new KeyResolver(new DSAKeyValueResolver()));
    arrayList.add(new KeyResolver(new X509CertificateResolver()));
    arrayList.add(new KeyResolver(new X509SKIResolver()));
    arrayList.add(new KeyResolver(new RetrievalMethodResolver()));
    arrayList.add(new KeyResolver(new X509SubjectNameResolver()));
    arrayList.add(new KeyResolver(new X509IssuerSerialResolver()));
    arrayList.add(new KeyResolver(new DEREncodedKeyValueResolver()));
    arrayList.add(new KeyResolver(new KeyInfoReferenceResolver()));
    arrayList.add(new KeyResolver(new X509DigestResolver()));
    resolverVector.addAll(arrayList);
  }
  
  public PublicKey resolvePublicKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return this.resolverSpi.engineLookupAndResolvePublicKey(paramElement, paramString, paramStorageResolver); }
  
  public X509Certificate resolveX509Certificate(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return this.resolverSpi.engineLookupResolveX509Certificate(paramElement, paramString, paramStorageResolver); }
  
  public SecretKey resolveSecretKey(Element paramElement, String paramString, StorageResolver paramStorageResolver) throws KeyResolverException { return this.resolverSpi.engineLookupAndResolveSecretKey(paramElement, paramString, paramStorageResolver); }
  
  public void setProperty(String paramString1, String paramString2) { this.resolverSpi.engineSetProperty(paramString1, paramString2); }
  
  public String getProperty(String paramString) { return this.resolverSpi.engineGetProperty(paramString); }
  
  public boolean understandsProperty(String paramString) { return this.resolverSpi.understandsProperty(paramString); }
  
  public String resolverClassName() { return this.resolverSpi.getClass().getName(); }
  
  public static Iterator<KeyResolverSpi> iterator() { return new ResolverIterator(resolverVector); }
  
  static class ResolverIterator extends Object implements Iterator<KeyResolverSpi> {
    List<KeyResolver> res;
    
    Iterator<KeyResolver> it;
    
    public ResolverIterator(List<KeyResolver> param1List) {
      this.res = param1List;
      this.it = this.res.iterator();
    }
    
    public boolean hasNext() { return this.it.hasNext(); }
    
    public KeyResolverSpi next() {
      KeyResolver keyResolver = (KeyResolver)this.it.next();
      if (keyResolver == null)
        throw new RuntimeException("utils.resolver.noClass"); 
      return keyResolver.resolverSpi;
    }
    
    public void remove() { throw new UnsupportedOperationException("Can't remove resolvers using the iterator"); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\security\keys\keyresolver\KeyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */