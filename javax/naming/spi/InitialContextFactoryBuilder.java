package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.NamingException;

public interface InitialContextFactoryBuilder {
  InitialContextFactory createInitialContextFactory(Hashtable<?, ?> paramHashtable) throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\spi\InitialContextFactoryBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */