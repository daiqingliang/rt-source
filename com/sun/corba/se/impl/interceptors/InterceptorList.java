package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;

public class InterceptorList {
  static final int INTERCEPTOR_TYPE_CLIENT = 0;
  
  static final int INTERCEPTOR_TYPE_SERVER = 1;
  
  static final int INTERCEPTOR_TYPE_IOR = 2;
  
  static final int NUM_INTERCEPTOR_TYPES = 3;
  
  static final Class[] classTypes = { org.omg.PortableInterceptor.ClientRequestInterceptor.class, org.omg.PortableInterceptor.ServerRequestInterceptor.class, org.omg.PortableInterceptor.IORInterceptor.class };
  
  private boolean locked = false;
  
  private InterceptorsSystemException wrapper;
  
  private Interceptor[][] interceptors = new Interceptor[3][];
  
  InterceptorList(InterceptorsSystemException paramInterceptorsSystemException) {
    this.wrapper = paramInterceptorsSystemException;
    initInterceptorArrays();
  }
  
  void register_interceptor(Interceptor paramInterceptor, int paramInt) throws DuplicateName {
    if (this.locked)
      throw this.wrapper.interceptorListLocked(); 
    String str = paramInterceptor.name();
    boolean bool = str.equals("");
    boolean bool1 = false;
    Interceptor[] arrayOfInterceptor = this.interceptors[paramInt];
    if (!bool) {
      int i = arrayOfInterceptor.length;
      for (byte b = 0; b < i; b++) {
        Interceptor interceptor = arrayOfInterceptor[b];
        if (interceptor.name().equals(str)) {
          bool1 = true;
          break;
        } 
      } 
    } 
    if (!bool1) {
      growInterceptorArray(paramInt);
      this.interceptors[paramInt][this.interceptors[paramInt].length - 1] = paramInterceptor;
    } else {
      throw new DuplicateName(str);
    } 
  }
  
  void lock() { this.locked = true; }
  
  Interceptor[] getInterceptors(int paramInt) { return this.interceptors[paramInt]; }
  
  boolean hasInterceptorsOfType(int paramInt) { return (this.interceptors[paramInt].length > 0); }
  
  private void initInterceptorArrays() {
    for (byte b = 0; b < 3; b++) {
      Class clazz = classTypes[b];
      this.interceptors[b] = (Interceptor[])Array.newInstance(clazz, 0);
    } 
  }
  
  private void growInterceptorArray(int paramInt) {
    Class clazz = classTypes[paramInt];
    int i = this.interceptors[paramInt].length;
    Interceptor[] arrayOfInterceptor = (Interceptor[])Array.newInstance(clazz, i + 1);
    System.arraycopy(this.interceptors[paramInt], 0, arrayOfInterceptor, 0, i);
    this.interceptors[paramInt] = arrayOfInterceptor;
  }
  
  void destroyAll() {
    int i = this.interceptors.length;
    for (byte b = 0; b < i; b++) {
      int j = this.interceptors[b].length;
      for (byte b1 = 0; b1 < j; b1++)
        this.interceptors[b][b1].destroy(); 
    } 
  }
  
  void sortInterceptors() {
    ArrayList arrayList1 = null;
    ArrayList arrayList2 = null;
    int i = this.interceptors.length;
    for (byte b = 0; b < i; b++) {
      int j = this.interceptors[b].length;
      if (j > 0) {
        arrayList1 = new ArrayList();
        arrayList2 = new ArrayList();
      } 
      for (byte b1 = 0; b1 < j; b1++) {
        Interceptor interceptor = this.interceptors[b][b1];
        if (interceptor instanceof Comparable) {
          arrayList1.add(interceptor);
        } else {
          arrayList2.add(interceptor);
        } 
      } 
      if (j > 0 && arrayList1.size() > 0) {
        Collections.sort(arrayList1);
        Iterator iterator1 = arrayList1.iterator();
        Iterator iterator2 = arrayList2.iterator();
        for (byte b2 = 0; b2 < j; b2++) {
          if (iterator1.hasNext()) {
            this.interceptors[b][b2] = (Interceptor)iterator1.next();
          } else if (iterator2.hasNext()) {
            this.interceptors[b][b2] = (Interceptor)iterator2.next();
          } else {
            throw this.wrapper.sortSizeMismatch();
          } 
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\InterceptorList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */