package sun.security.jgss.krb5;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;

class SubjectComber {
  private static final boolean DEBUG = Krb5Util.DEBUG;
  
  static <T> T find(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass) { return (T)paramClass.cast(findAux(paramSubject, paramString1, paramString2, paramClass, true)); }
  
  static <T> List<T> findMany(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass) { return (List)findAux(paramSubject, paramString1, paramString2, paramClass, false); }
  
  private static <T> Object findAux(Subject paramSubject, String paramString1, String paramString2, Class<T> paramClass, boolean paramBoolean) {
    if (paramSubject == null)
      return null; 
    ArrayList arrayList = paramBoolean ? null : new ArrayList();
    if (paramClass == KeyTab.class) {
      for (KeyTab keyTab : paramSubject.getPrivateCredentials(KeyTab.class)) {
        if (paramString1 != null && keyTab.isBound()) {
          KerberosPrincipal kerberosPrincipal = keyTab.getPrincipal();
          if (kerberosPrincipal != null) {
            if (!paramString1.equals(kerberosPrincipal.getName()))
              continue; 
          } else {
            boolean bool = false;
            for (KerberosPrincipal kerberosPrincipal1 : paramSubject.getPrincipals(KerberosPrincipal.class)) {
              if (kerberosPrincipal1.getName().equals(paramString1)) {
                bool = true;
                break;
              } 
            } 
            if (!bool)
              continue; 
          } 
        } 
        if (DEBUG)
          System.out.println("Found " + paramClass.getSimpleName() + " " + keyTab); 
        if (paramBoolean)
          return keyTab; 
        arrayList.add(paramClass.cast(keyTab));
      } 
    } else if (paramClass == KerberosKey.class) {
      for (KerberosKey kerberosKey : paramSubject.getPrivateCredentials(KerberosKey.class)) {
        String str = kerberosKey.getPrincipal().getName();
        if (paramString1 == null || paramString1.equals(str)) {
          if (DEBUG)
            System.out.println("Found " + paramClass.getSimpleName() + " for " + str); 
          if (paramBoolean)
            return kerberosKey; 
          arrayList.add(paramClass.cast(kerberosKey));
        } 
      } 
    } else if (paramClass == KerberosTicket.class) {
      Set set = paramSubject.getPrivateCredentials();
      synchronized (set) {
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
          Object object = iterator.next();
          if (object instanceof KerberosTicket) {
            KerberosTicket kerberosTicket = (KerberosTicket)object;
            if (DEBUG)
              System.out.println("Found ticket for " + kerberosTicket.getClient() + " to go to " + kerberosTicket.getServer() + " expiring on " + kerberosTicket.getEndTime()); 
            if (!kerberosTicket.isCurrent()) {
              if (!paramSubject.isReadOnly()) {
                iterator.remove();
                try {
                  kerberosTicket.destroy();
                  if (DEBUG)
                    System.out.println("Removed and destroyed the expired Ticket \n" + kerberosTicket); 
                  continue;
                } catch (DestroyFailedException destroyFailedException) {
                  if (DEBUG)
                    System.out.println("Expired ticket not detroyed successfully. " + destroyFailedException); 
                  continue;
                } 
              } 
              continue;
            } 
            if ((paramString1 == null || kerberosTicket.getServer().getName().equals(paramString1)) && (paramString2 == null || paramString2.equals(kerberosTicket.getClient().getName()))) {
              if (paramBoolean)
                return kerberosTicket; 
              if (paramString2 == null)
                paramString2 = kerberosTicket.getClient().getName(); 
              if (paramString1 == null)
                paramString1 = kerberosTicket.getServer().getName(); 
              arrayList.add(paramClass.cast(kerberosTicket));
            } 
          } 
        } 
      } 
    } 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\jgss\krb5\SubjectComber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */