package com.sun.corba.se.impl.resolver;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.naming.namingutil.CorbalocURL;
import com.sun.corba.se.impl.naming.namingutil.CorbanameURL;
import com.sun.corba.se.impl.naming.namingutil.IIOPEndpointInfo;
import com.sun.corba.se.impl.naming.namingutil.INSURL;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.IORTemplate;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import com.sun.corba.se.spi.ior.iiop.IIOPFactories;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.Operation;
import com.sun.corba.se.spi.resolver.Resolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import sun.corba.EncapsInputStreamFactory;

public class INSURLOperationImpl implements Operation {
  ORB orb;
  
  ORBUtilSystemException wrapper;
  
  OMGSystemException omgWrapper;
  
  Resolver bootstrapResolver;
  
  private NamingContextExt rootNamingContextExt;
  
  private Object rootContextCacheLock = new Object();
  
  private INSURLHandler insURLHandler = INSURLHandler.getINSURLHandler();
  
  private static final int NIBBLES_PER_BYTE = 2;
  
  private static final int UN_SHIFT = 4;
  
  public INSURLOperationImpl(ORB paramORB, Resolver paramResolver) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "orb.resolver");
    this.omgWrapper = OMGSystemException.get(paramORB, "orb.resolver");
    this.bootstrapResolver = paramResolver;
  }
  
  private Object getIORFromString(String paramString) {
    if ((paramString.length() & true) == 1)
      throw this.wrapper.badStringifiedIorLen(); 
    byte[] arrayOfByte = new byte[(paramString.length() - "IOR:".length()) / 2];
    int i = "IOR:".length();
    for (byte b = 0; i < paramString.length(); b++) {
      arrayOfByte[b] = (byte)(ORBUtility.hexOf(paramString.charAt(i)) << 4 & 0xF0);
      arrayOfByte[b] = (byte)(arrayOfByte[b] | (byte)(ORBUtility.hexOf(paramString.charAt(i + 1)) & 0xF));
      i += 2;
    } 
    EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(this.orb, arrayOfByte, arrayOfByte.length, this.orb.getORBData().getGIOPVersion());
    encapsInputStream.consumeEndian();
    return encapsInputStream.read_Object();
  }
  
  public Object operate(Object paramObject) {
    if (paramObject instanceof String) {
      String str = (String)paramObject;
      if (str.startsWith("IOR:"))
        return getIORFromString(str); 
      INSURL iNSURL = this.insURLHandler.parseURL(str);
      if (iNSURL == null)
        throw this.omgWrapper.soBadSchemeName(); 
      return resolveINSURL(iNSURL);
    } 
    throw this.wrapper.stringExpected();
  }
  
  private Object resolveINSURL(INSURL paramINSURL) { return paramINSURL.isCorbanameURL() ? resolveCorbaname((CorbanameURL)paramINSURL) : resolveCorbaloc((CorbalocURL)paramINSURL); }
  
  private Object resolveCorbaloc(CorbalocURL paramCorbalocURL) {
    Object object = null;
    if (paramCorbalocURL.getRIRFlag()) {
      object = this.bootstrapResolver.resolve(paramCorbalocURL.getKeyString());
    } else {
      object = getIORUsingCorbaloc(paramCorbalocURL);
    } 
    return object;
  }
  
  private Object resolveCorbaname(CorbanameURL paramCorbanameURL) {
    Object object = null;
    try {
      NamingContextExt namingContextExt = null;
      if (paramCorbanameURL.getRIRFlag()) {
        namingContextExt = getDefaultRootNamingContext();
      } else {
        Object object1 = getIORUsingCorbaloc(paramCorbanameURL);
        if (object1 == null)
          return null; 
        namingContextExt = NamingContextExtHelper.narrow(object1);
      } 
      String str = paramCorbanameURL.getStringifiedName();
      return (str == null) ? namingContextExt : namingContextExt.resolve_str(str);
    } catch (Exception exception) {
      clearRootNamingContextCache();
      return null;
    } 
  }
  
  private Object getIORUsingCorbaloc(INSURL paramINSURL) {
    HashMap hashMap = new HashMap();
    ArrayList arrayList1 = new ArrayList();
    List list = paramINSURL.getEndpointInfo();
    String str = paramINSURL.getKeyString();
    if (str == null)
      return null; 
    ObjectKey objectKey = this.orb.getObjectKeyFactory().create(str.getBytes());
    IORTemplate iORTemplate = IORFactories.makeIORTemplate(objectKey.getTemplate());
    for (IIOPEndpointInfo iIOPEndpointInfo : list) {
      IIOPAddress iIOPAddress = IIOPFactories.makeIIOPAddress(this.orb, iIOPEndpointInfo.getHost(), iIOPEndpointInfo.getPort());
      GIOPVersion gIOPVersion1 = GIOPVersion.getInstance((byte)iIOPEndpointInfo.getMajor(), (byte)iIOPEndpointInfo.getMinor());
      IIOPProfileTemplate iIOPProfileTemplate1 = null;
      if (gIOPVersion1.equals(GIOPVersion.V1_0)) {
        iIOPProfileTemplate1 = IIOPFactories.makeIIOPProfileTemplate(this.orb, gIOPVersion1, iIOPAddress);
        arrayList1.add(iIOPProfileTemplate1);
        continue;
      } 
      if (hashMap.get(gIOPVersion1) == null) {
        iIOPProfileTemplate1 = IIOPFactories.makeIIOPProfileTemplate(this.orb, gIOPVersion1, iIOPAddress);
        hashMap.put(gIOPVersion1, iIOPProfileTemplate1);
        continue;
      } 
      iIOPProfileTemplate1 = (IIOPProfileTemplate)hashMap.get(gIOPVersion1);
      AlternateIIOPAddressComponent alternateIIOPAddressComponent = IIOPFactories.makeAlternateIIOPAddressComponent(iIOPAddress);
      iIOPProfileTemplate1.add(alternateIIOPAddressComponent);
    } 
    GIOPVersion gIOPVersion = this.orb.getORBData().getGIOPVersion();
    IIOPProfileTemplate iIOPProfileTemplate = (IIOPProfileTemplate)hashMap.get(gIOPVersion);
    if (iIOPProfileTemplate != null) {
      iORTemplate.add(iIOPProfileTemplate);
      hashMap.remove(gIOPVersion);
    } 
    Comparator comparator = new Comparator() {
        public int compare(Object param1Object1, Object param1Object2) {
          GIOPVersion gIOPVersion1 = (GIOPVersion)param1Object1;
          GIOPVersion gIOPVersion2 = (GIOPVersion)param1Object2;
          return gIOPVersion1.lessThan(gIOPVersion2) ? 1 : (gIOPVersion1.equals(gIOPVersion2) ? 0 : -1);
        }
      };
    ArrayList arrayList2 = new ArrayList(hashMap.keySet());
    Collections.sort(arrayList2, comparator);
    Iterator iterator = arrayList2.iterator();
    while (iterator.hasNext()) {
      IIOPProfileTemplate iIOPProfileTemplate1 = (IIOPProfileTemplate)hashMap.get(iterator.next());
      iORTemplate.add(iIOPProfileTemplate1);
    } 
    iORTemplate.addAll(arrayList1);
    IOR iOR = iORTemplate.makeIOR(this.orb, "", objectKey.getId());
    return ORBUtility.makeObjectReference(iOR);
  }
  
  private NamingContextExt getDefaultRootNamingContext() {
    synchronized (this.rootContextCacheLock) {
      if (this.rootNamingContextExt == null)
        try {
          this.rootNamingContextExt = NamingContextExtHelper.narrow(this.orb.getLocalResolver().resolve("NameService"));
        } catch (Exception exception) {
          this.rootNamingContextExt = null;
        }  
    } 
    return this.rootNamingContextExt;
  }
  
  private void clearRootNamingContextCache() {
    synchronized (this.rootContextCacheLock) {
      this.rootNamingContextExt = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\resolver\INSURLOperationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */