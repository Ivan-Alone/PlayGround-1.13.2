package ru.ivan_alone.playground.minecraft.util;

import java.lang.reflect.Field;

public class ReflectMirror {
	
	public static <T> T call(Object obj, Class<T> _class, String[] names) {
		return call(obj, obj.getClass(), _class, names, new Class[] {}, new Object[] {});
	}
	
	public static <T> T call(Object obj, Class<T> _class, String[] names, @SuppressWarnings("rawtypes") Class[] types, Object[] args) {
		return call(obj, obj.getClass(), _class, names, types, args);
	} 
	
	public static <T> T call(Object obj, @SuppressWarnings("rawtypes") Class realClass, Class<T> _class, String[] names) {
		return call(obj, realClass, _class, names, new Class[] {}, new Object[] {});
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T call(Object obj, @SuppressWarnings("rawtypes") Class realClass, Class<T> _class, String[] names, @SuppressWarnings("rawtypes") Class[] types, Object[] args) {
		for (String name : names) {
			if (name.equals("")) continue;
			
			java.lang.reflect.Method m;
			try {
				m = realClass.getDeclaredMethod(name, types);
				m.setAccessible(true);

				return (T) m.invoke(obj, args);
			} catch (Exception e) {
			}
			try {
				m = realClass.getMethod(name, types);
				m.setAccessible(true);

				return (T) m.invoke(obj, args);
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	public static <T> T get(Object obj, Class<T> _class, String[] names) {
		return get(obj, obj.getClass(), _class, names);
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Object obj, @SuppressWarnings("rawtypes") Class realClass, Class<T> _class, String[] names) {
		for (String name : names) {
			if (name.equals("")) continue;
			
			Field f;
			try {
				f = realClass.getDeclaredField(name);
				f.setAccessible(true);
				return (T) f.get(obj);
			} catch (Exception e) {
			}
			try {
				f = realClass.getField(name);
				f.setAccessible(true);
				return (T) f.get(obj);
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static void set(Object obj, String[] names, Object value) {
		set(obj, obj.getClass(), names, value);
	}

	public static void set(Object obj, @SuppressWarnings("rawtypes") Class realClass, String[] names, Object value) {
		for (String name : names) {
			if (name.equals("")) continue;
			
			Field f;
			try {
				f = realClass.getDeclaredField(name);
				f.setAccessible(true);
				f.set(obj, value);
				return;
			} catch (Exception e) {
			}
			try {
				f = realClass.getField(name);
				f.setAccessible(true);
				f.set(obj, value);
				return;
			} catch (Exception e) {
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static class Method<T> {
		private Class realClass;
		private String[] names;
		private Class[] types;
		private Class<T> returnType;
		
		public Method(Class realClass, Class<T> _class, String[] names) {
			this(realClass, _class, names, new Class[0]);
		}
		
		public Method(Class realClass, Class<T> _class, String[] names, Class[] types) {
			this.realClass = realClass;
			this.returnType = _class;
			this.names = names;
			this.types = types;
		}
		
		public T call(Object obj) {
			return (T) this.call(obj, new Object[0]);
		}
		
		public T call(Object obj, Object... args) {
			return (T) ReflectMirror.call(obj, this.realClass, this.returnType, this.names, this.types, args);
		}
	}
}
