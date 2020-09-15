package com.lucky.jacklamb.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lucky.jacklamb.annotation.aop.After;
import com.lucky.jacklamb.annotation.aop.Aspect;
import com.lucky.jacklamb.annotation.aop.Before;
import com.lucky.jacklamb.aop.core.AopPoint;
import com.lucky.jacklamb.aop.core.PointRun;
import com.lucky.jacklamb.utils.reflect.ClassUtils;
import com.lucky.jacklamb.exception.NotAddIOCComponent;
import com.lucky.jacklamb.exception.NotFindBeanException;
import com.lucky.jacklamb.ioc.scan.ScanFactory;
import com.lucky.jacklamb.utils.base.LuckyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 代理对象集合
 * 
 * @author DELL
 *
 */
public class AspectAOP {

	private static final Logger log= LogManager.getLogger(AspectAOP.class);

	private static AspectAOP aspectAop;

	private Map<String, PointRun> aspectMap;

	private List<String> aspectIDS;

	private AspectAOP() {
		aspectMap = new HashMap<>();
		aspectIDS = new ArrayList<>();
		initAspectIOC(ScanFactory.createScan().getComponentClass("aspect"));
	}

	public static AspectAOP getAspectIOC() {
		if (aspectAop == null)
			aspectAop = new AspectAOP();
		return aspectAop;
	}

	public boolean containId(String id) {
		return aspectIDS.contains(id);
	}

	public PointRun getAspectBean(String id) {
		if (!containId(id))
			throw new NotFindBeanException("在Aspect(ioc)容器中找不到ID为--" + id + "--的Bean...");
		return aspectMap.get(id);
	}

	public Map<String, PointRun> getAspectMap() {
		return aspectMap;
	}

	public void setAspectMap(Map<String, PointRun> AspectMap) {
		this.aspectMap = AspectMap;
	}

	public void addAspectMap(String id, PointRun object) {
		if (containId(id))
			throw new NotAddIOCComponent(
					"Aspect(ioc)容器中已存在ID为--" + id + "--的组件，无法重复添加（您可能配置了同名的@Aspect组件，这将会导致异常的发生！）......");
		aspectMap.put(id, object);
		addAspectID(id);
	}

	public List<String> getAspectIDS() {
		return aspectIDS;
	}

	public void setAspectIDS(List<String> AspectIDS) {
		this.aspectIDS = AspectIDS;
	}

	public void addAspectID(String id) {
		aspectIDS.add(id);
	}

	/**
	 * 加载Aspect组件
	 * 
	 * @param AspectClass
	 * @return
	 */
	public void initAspectIOC(List<Class<?>> AspectClass){
		Aspect agann;
		Before before;
		After after;
		PointRun pointRun;
		Constructor<?> constructor;
		for (Class<?> aspect : AspectClass) {
			if (aspect.isAnnotationPresent(Aspect.class)) {
				String name;
				agann = aspect.getAnnotation(Aspect.class);
				if ("".equals(agann.value())) {
					name = LuckyUtils.TableToClass1(aspect.getSimpleName());
				} else {
					name = agann.value();
				}
				if (AopPoint.class.isAssignableFrom(aspect)) {
					name += ".proceed";
					pointRun = new PointRun((AopPoint) ClassUtils.newObject(aspect));
					addAspectMap(name, pointRun);
					log.info("@Aspect \"[location=Around id=" + name + " class=" + pointRun + "]\"");
				} else {
					Method[] enhanceMethods = aspect.getDeclaredMethods();
					for (Method method : enhanceMethods) {
						String Aspectid;
						if (method.isAnnotationPresent(Before.class)) {
							before = method.getAnnotation(Before.class);
							if ("".equals(before.value())) {
								Aspectid = name + ("." + LuckyUtils.TableToClass1(method.getName()));
							} else {
								Aspectid = name + ("." + before.value());
							}
							pointRun = new PointRun(ClassUtils.newObject(aspect), method);
							addAspectMap(Aspectid, pointRun);
							log.info("@Aspect \"[location=Before id=" + Aspectid + " class=" + pointRun
									+ "]\"");
						} else if (method.isAnnotationPresent(After.class)) {
							after = method.getAnnotation(After.class);
							if ("".equals(after.value())) {
								Aspectid = name + ("." + LuckyUtils.TableToClass1(method.getName()));
							} else {
								Aspectid = name + ("." + after.value());
							}
							pointRun = new PointRun(ClassUtils.newObject(aspect), method);
							addAspectMap(Aspectid, pointRun);
							log.info("@Aspect \"[location=After id=" + Aspectid + " class=" + pointRun
									+ "]\"");
						} else {
							continue;
						}
					}
				}
			}
		}
	}

}
