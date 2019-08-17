package javax.swing.text.html;

import java.awt.Color;
import java.awt.Component;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import javax.swing.JLabel;
import javax.swing.text.AttributeSet;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class ObjectView extends ComponentView {
  public ObjectView(Element paramElement) { super(paramElement); }
  
  protected Component createComponent() {
    AttributeSet attributeSet = getElement().getAttributes();
    String str = (String)attributeSet.getAttribute(HTML.Attribute.CLASSID);
    try {
      ReflectUtil.checkPackageAccess(str);
      Class clazz = Class.forName(str, true, Thread.currentThread().getContextClassLoader());
      Object object = clazz.newInstance();
      if (object instanceof Component) {
        Component component = (Component)object;
        setParameters(component, attributeSet);
        return component;
      } 
    } catch (Throwable throwable) {}
    return getUnloadableRepresentation();
  }
  
  Component getUnloadableRepresentation() {
    JLabel jLabel = new JLabel("??");
    jLabel.setForeground(Color.red);
    return jLabel;
  }
  
  private void setParameters(Component paramComponent, AttributeSet paramAttributeSet) {
    BeanInfo beanInfo;
    Class clazz = paramComponent.getClass();
    try {
      beanInfo = Introspector.getBeanInfo(clazz);
    } catch (IntrospectionException introspectionException) {
      System.err.println("introspector failed, ex: " + introspectionException);
      return;
    } 
    PropertyDescriptor[] arrayOfPropertyDescriptor = beanInfo.getPropertyDescriptors();
    for (byte b = 0; b < arrayOfPropertyDescriptor.length; b++) {
      Object object = paramAttributeSet.getAttribute(arrayOfPropertyDescriptor[b].getName());
      if (object instanceof String) {
        String str = (String)object;
        Method method = arrayOfPropertyDescriptor[b].getWriteMethod();
        if (method == null)
          return; 
        Class[] arrayOfClass = method.getParameterTypes();
        if (arrayOfClass.length != 1)
          return; 
        Object[] arrayOfObject = { str };
        try {
          MethodUtil.invoke(method, paramComponent, arrayOfObject);
        } catch (Exception exception) {
          System.err.println("Invocation failed");
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\ObjectView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */