package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.util.JAXWSUtils;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;

final class SCAnnotations {
  final ArrayList<QName> portQNames = new ArrayList();
  
  final ArrayList<Class> classes = new ArrayList();
  
  SCAnnotations(final Class<?> sc) { AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            WebServiceClient webServiceClient = (WebServiceClient)sc.getAnnotation(WebServiceClient.class);
            if (webServiceClient == null)
              throw new WebServiceException("Service Interface Annotations required, exiting..."); 
            String str = webServiceClient.targetNamespace();
            try {
              JAXWSUtils.getFileOrURL(webServiceClient.wsdlLocation());
            } catch (IOException iOException) {
              throw new WebServiceException(iOException);
            } 
            for (Method method : sc.getDeclaredMethods()) {
              WebEndpoint webEndpoint = (WebEndpoint)method.getAnnotation(WebEndpoint.class);
              if (webEndpoint != null) {
                String str1 = webEndpoint.name();
                QName qName = new QName(str, str1);
                SCAnnotations.this.portQNames.add(qName);
              } 
              Class clazz = method.getReturnType();
              if (clazz != void.class)
                SCAnnotations.this.classes.add(clazz); 
            } 
            return null;
          }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\SCAnnotations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */