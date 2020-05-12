package com.caspar.base.annotations

/**
 * ElementType.METHOD // 该注解作用在方法上
 *
 * @ describe: 自动setContentView注解
 * @ author: Martin
 * @ createTime: 2019/3/25 15:30
 * @ version: 1.0
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS) // 该注解作用在类上
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME) // jvm运行时通过反射机制获取该注解的值
annotation class ContentView(val value: Int = ResId.DEFAULT_VALUE)