package com.hyj.lib.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.hyj.lib.tools.LogUtils;

/**
 * <pre>
 * 反射使用案例Demo
 * 反射使用诀窍：若要获取类的信息，则先获取类的类类型(也就是Class)，通过Class可以获取到类的信息
 * </pre>
 * 
 * @Author hyj
 * @Date 2016-2-18 下午3:37:52
 */
public class ReflectDemo {

	/**
	 * 获取类中所含方法信息
	 * 
	 * @param obj对象
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static String getClassMethodMsg(Object obj) {
		Class clz = obj.getClass();// 获取类类型

		String str = "类名：" + clz.getName();// 获取类的全名
		str += "\n";
		LogUtils.e(str);

		// 获取类里面所有的方法(获取的是所有public函数，含父类继承过来的)
		Method[] allMs = clz.getMethods();
		// 获取该类中自己声明的所有方法(与访问权限无关)
		Method[] myMs = clz.getDeclaredMethods();
		for (Method method : myMs) {
			// 获取访问权限
			int modify = method.getModifiers();
			String qx = Modifier.toString(modify);

			// 得到方法的返回值类型的类类型
			Class returnType = method.getReturnType();
			String returnTypeName = returnType.getName();
			// 得到方法的名称
			String methodName = method.getName();

			// 获取参数类型(得到的是参数列表的类的类类型)
			Class[] paramTypes = method.getParameterTypes();
			String paramTypeName = "";
			for (Class clzParams : paramTypes) {
				paramTypeName += clzParams.getName() + ",";
			}
			if (paramTypeName.length() > 1) {
				paramTypeName = paramTypeName.substring(0,
						paramTypeName.length() - 1);
			}
			paramTypeName = methodName + "(" + paramTypeName + ")";

			String temp = qx + " " + returnTypeName + " " + paramTypeName;
			str += temp + "\n";

			LogUtils.i(temp);
		}
		return str;
	}

	/**
	 * 获取类的成员变量信息
	 * 
	 * @param obj对象
	 */
	@SuppressWarnings("rawtypes")
	public static String getClassFieldMsg(Object obj) {
		String str = "成员变量信息：";
		LogUtils.e(str);
		str += "\n";

		Class clz = obj.getClass();

		// 获取所有public的成员的信息
		Field[] fields = clz.getFields();
		// 获取所有自己声明的成员变量的信息
		fields = clz.getDeclaredFields();

		for (Field field : fields) {
			// 获取访问权限
			int modify = field.getModifiers();
			String qx = Modifier.toString(modify);

			// 得到成员变量的类的类类型
			Class fieldType = field.getType();
			String typeName = fieldType.getName();
			// 得到成员变量的名字
			String fieldName = field.getName();

			String temp = qx + "　" + typeName + "　" + fieldName;
			str += temp + "\n";

			LogUtils.i(temp);
		}
		return str;
	}

	/**
	 * 获取类的构造函数信息
	 * 
	 * @param obj对象
	 */
	@SuppressWarnings("rawtypes")
	public static String getClassConstructorMsg(Object obj) {
		String str = "构造函数信息：";
		LogUtils.e(str);
		str += "\n";

		Class clz = obj.getClass();

		// 获取所有的public的构造函数
		Constructor[] cons = clz.getConstructors();
		// 得到自己声明的所有够着函数
		cons = clz.getDeclaredConstructors();

		for (Constructor con : cons) {
			// 获取访问权限
			int modify = con.getModifiers();
			String qx = Modifier.toString(modify);

			String conName = con.getName();
			// 获取够着函数的参数列表(得到的是参数列表的类类型)
			Class[] paramTypes = con.getParameterTypes();
			String paramType = "";
			for (Class c : paramTypes) {
				paramType += c.getName() + ",";
			}
			if (paramType.length() > 1) {
				paramType = paramType.substring(0, paramType.length() - 1);
			}
			paramType = "(" + paramType + ")";

			String temp = qx + "　" + conName + paramType;
			str += temp + "\n";

			LogUtils.i(temp);
		}
		return str;
	}

	/**
	 * 通过反射执行类的方法，并取得返回结果
	 * 
	 * @param obj对象
	 */
	public static String methodInvoke(Object obj) {
		String str = "通过反射执行类里面的方法：";
		LogUtils.e(str);
		str += "\n";

		Class<? extends Object> clz = obj.getClass();
		try {
			// 获取类的方法 print(int,int)
			Method m = clz.getDeclaredMethod("print", int.class, int.class);
			// 调用这个方法,若有返回值则returnVlaue就是返回值
			Object returnVlue = m.invoke(obj, 3, 5);

			String temp = m.getName() + "反射调用执行返回结果：" + returnVlue;
			str += temp + "\n";

			LogUtils.i(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
}

class A {
	public int print(int a, int b) {
		int result = a + b;
		LogUtils.i("print(int a, int b) 执行结果：" + result);
		return result;
	}

	public void print(String a, String b) {
		String result = a.toUpperCase() + "," + b.toLowerCase();
		LogUtils.i("print(String a, String b) 执行结果：" + result);
	}
}
