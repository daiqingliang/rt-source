package com.sun.xml.internal.ws.util.exception;

import com.sun.istack.internal.localization.Localizable;
import com.sun.istack.internal.localization.LocalizableMessage;
import com.sun.istack.internal.localization.LocalizableMessageFactory;
import com.sun.istack.internal.localization.Localizer;
import com.sun.istack.internal.localization.NullLocalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import javax.xml.ws.WebServiceException;

public abstract class JAXWSExceptionBase extends WebServiceException implements Localizable {
  private static final long serialVersionUID = 1L;
  
  private Localizable msg;
  
  protected JAXWSExceptionBase(String paramString, Object... paramVarArgs) {
    super(findNestedException(paramVarArgs));
    this.msg = new LocalizableMessage(getDefaultResourceBundleName(), paramString, paramVarArgs);
  }
  
  protected JAXWSExceptionBase(String paramString) { this(new NullLocalizable(paramString)); }
  
  protected JAXWSExceptionBase(Throwable paramThrowable) { this(new NullLocalizable(paramThrowable.toString()), paramThrowable); }
  
  protected JAXWSExceptionBase(Localizable paramLocalizable) { this.msg = paramLocalizable; }
  
  protected JAXWSExceptionBase(Localizable paramLocalizable, Throwable paramThrowable) {
    super(paramThrowable);
    this.msg = paramLocalizable;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(this.msg.getResourceBundleName());
    paramObjectOutputStream.writeObject(this.msg.getKey());
    Object[] arrayOfObject = this.msg.getArguments();
    if (arrayOfObject == null) {
      paramObjectOutputStream.writeInt(-1);
      return;
    } 
    paramObjectOutputStream.writeInt(arrayOfObject.length);
    for (byte b = 0; b < arrayOfObject.length; b++) {
      if (arrayOfObject[b] == null || arrayOfObject[b] instanceof java.io.Serializable) {
        paramObjectOutputStream.writeObject(arrayOfObject[b]);
      } else {
        paramObjectOutputStream.writeObject(arrayOfObject[b].toString());
      } 
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    Object[] arrayOfObject;
    paramObjectInputStream.defaultReadObject();
    String str1 = (String)paramObjectInputStream.readObject();
    String str2 = (String)paramObjectInputStream.readObject();
    int i = paramObjectInputStream.readInt();
    if (i < -1)
      throw new NegativeArraySizeException(); 
    if (i == -1) {
      arrayOfObject = null;
    } else if (i < 255) {
      arrayOfObject = new Object[i];
      for (byte b = 0; b < arrayOfObject.length; b++)
        arrayOfObject[b] = paramObjectInputStream.readObject(); 
    } else {
      ArrayList arrayList = new ArrayList(Math.min(i, 1024));
      for (byte b = 0; b < i; b++)
        arrayList.add(paramObjectInputStream.readObject()); 
      arrayOfObject = arrayList.toArray(new Object[arrayList.size()]);
    } 
    this.msg = (new LocalizableMessageFactory(str1)).getMessage(str2, arrayOfObject);
  }
  
  private static Throwable findNestedException(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return null; 
    for (Object object : paramArrayOfObject) {
      if (object instanceof Throwable)
        return (Throwable)object; 
    } 
    return null;
  }
  
  public String getMessage() {
    Localizer localizer = new Localizer();
    return localizer.localize(this);
  }
  
  protected abstract String getDefaultResourceBundleName();
  
  public final String getKey() { return this.msg.getKey(); }
  
  public final Object[] getArguments() { return this.msg.getArguments(); }
  
  public final String getResourceBundleName() { return this.msg.getResourceBundleName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\exception\JAXWSExceptionBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */