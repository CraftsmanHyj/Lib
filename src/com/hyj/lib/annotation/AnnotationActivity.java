package com.hyj.lib.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.hyj.lib.R;
import com.hyj.lib.tools.Utils;

/**
 * 注解、反射使用案列
 * 
 * @Author hyj
 * @Date 2016-2-18 下午3:36:54
 */
public class AnnotationActivity extends Activity {

	private TextView tvSql;
	private TextView tvReflect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.anno_main);

		myInit();
	}

	private void myInit() {
		initView();
		initData();

		// 反射使用案例
		reflectDemo();
	}

	private void initView() {
		tvSql = (TextView) findViewById(R.id.annoTvSql);
		tvReflect = (TextView) findViewById(R.id.annoTvReflect);
	}

	/**
	 * 反射使用案例
	 */
	private void reflectDemo() {
		String s = "hello";

		// 获取类中所含方法信息
		String str = ReflectDemo.getClassMethodMsg(s) + "\n\n";
		// 获取类中字段信息
		str += ReflectDemo.getClassFieldMsg(s) + "\n\n";
		// 获取类中够着函数信息
		str += ReflectDemo.getClassConstructorMsg(s) + "\n\n";
		// 使用反射调用类中的方法
		str += ReflectDemo.methodInvoke(new A()) + "\n\n";

		tvReflect.setText(str);
	}

	private void initData() {
		BeanFilter f1 = new BeanFilter();
		f1.setId(10);
		f1.setUserName("async");

		BeanFilter f2 = new BeanFilter();
		f2.setUserName("lucky");

		String sql = "";
		String str = toSql(f1);
		if (!TextUtils.isEmpty(str)) {
			sql += "user表注解：\n";
			sql += str;
		}

		str = toSql(f2);
		if (!TextUtils.isEmpty(str)) {
			sql += str;
		}

		BeanDepartment depart = new BeanDepartment();
		depart.setAmount(10);
		depart.setName("研发部");
		depart.setLeader("async");
		str = toSql(depart);
		if (!TextUtils.isEmpty(str)) {
			sql += "department表注解：\n";
			sql += str;
		}

		tvSql.setText(sql);
	}

	@SuppressLint("DefaultLocale")
	private String toSql(Object filter) {
		String str = "";

		// 1、获取到Class
		Class<? extends Object> clz = filter.getClass();
		// 2、获取到Table的名字
		boolean exists = clz.isAnnotationPresent(Table.class);
		if (!exists) {// 判断是否带有Table的注解
			return null;
		}

		// 获取@Table注解的值
		Table table = clz.getAnnotation(Table.class);
		String temp = table.value();// 获取注解的值
		if (TextUtils.isEmpty(temp)) {
			return null;
		}
		str += "select * from " + temp + " where 1=1";

		// 3、遍历JavaBean所有的字段,查看字段上是否有注解
		Field[] fields = clz.getDeclaredFields();
		for (Field field : fields) {
			// 是否含有@Column的注解
			exists = field.isAnnotationPresent(Column.class);
			if (exists) {
				// 获取注解的值
				Column c = field.getAnnotation(Column.class);
				temp = c.value();
				if (TextUtils.isEmpty(temp)) {
					continue;
				}

				// 通过反射获取该属性的值
				String fieldName = field.getName();// 获取属性名
				String getMethodName = "get"
						+ fieldName.substring(0, 1).toUpperCase()
						+ fieldName.substring(1);
				try {
					Method getMethod = clz.getMethod(getMethodName);
					// 通过反射获取Filter对象中对应属性的值
					Object value = getMethod.invoke(filter);
					if (Utils.isEmpty(value)) {
						continue;
					}

					if (value instanceof String) {
						value = "\'" + value + "\'";
					} else if (value instanceof Integer) {
						if ((Integer) value <= 0) {
							continue;
						}
					}

					str += " and " + temp + " = " + value;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return str + "\n\n";
	}
}
