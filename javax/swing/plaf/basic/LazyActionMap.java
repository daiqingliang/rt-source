package javax.swing.plaf.basic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;

class LazyActionMap extends ActionMapUIResource {
  private Object _loader;
  
  static void installLazyActionMap(JComponent paramJComponent, Class paramClass, String paramString) {
    ActionMap actionMap = (ActionMap)UIManager.get(paramString);
    if (actionMap == null) {
      actionMap = new LazyActionMap(paramClass);
      UIManager.getLookAndFeelDefaults().put(paramString, actionMap);
    } 
    SwingUtilities.replaceUIActionMap(paramJComponent, actionMap);
  }
  
  static ActionMap getActionMap(Class paramClass, String paramString) {
    ActionMap actionMap = (ActionMap)UIManager.get(paramString);
    if (actionMap == null) {
      actionMap = new LazyActionMap(paramClass);
      UIManager.getLookAndFeelDefaults().put(paramString, actionMap);
    } 
    return actionMap;
  }
  
  private LazyActionMap(Class paramClass) { this._loader = paramClass; }
  
  public void put(Action paramAction) { put(paramAction.getValue("Name"), paramAction); }
  
  public void put(Object paramObject, Action paramAction) {
    loadIfNecessary();
    super.put(paramObject, paramAction);
  }
  
  public Action get(Object paramObject) {
    loadIfNecessary();
    return super.get(paramObject);
  }
  
  public void remove(Object paramObject) {
    loadIfNecessary();
    super.remove(paramObject);
  }
  
  public void clear() {
    loadIfNecessary();
    super.clear();
  }
  
  public Object[] keys() {
    loadIfNecessary();
    return super.keys();
  }
  
  public int size() {
    loadIfNecessary();
    return super.size();
  }
  
  public Object[] allKeys() {
    loadIfNecessary();
    return super.allKeys();
  }
  
  public void setParent(ActionMap paramActionMap) {
    loadIfNecessary();
    super.setParent(paramActionMap);
  }
  
  private void loadIfNecessary() {
    if (this._loader != null) {
      Object object = this._loader;
      this._loader = null;
      Class clazz = (Class)object;
      try {
        Method method = clazz.getDeclaredMethod("loadActionMap", new Class[] { LazyActionMap.class });
        method.invoke(clazz, new Object[] { this });
      } catch (NoSuchMethodException noSuchMethodException) {
        assert false : "LazyActionMap unable to load actions " + clazz;
      } catch (IllegalAccessException illegalAccessException) {
        assert false : "LazyActionMap unable to load actions " + illegalAccessException;
      } catch (InvocationTargetException invocationTargetException) {
        assert false : "LazyActionMap unable to load actions " + invocationTargetException;
      } catch (IllegalArgumentException illegalArgumentException) {
        assert false : "LazyActionMap unable to load actions " + illegalArgumentException;
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\LazyActionMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */