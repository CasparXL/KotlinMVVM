# 避免 ViewBinding 类被混淆导致反射初始化失败
-keep public interface androidx.viewbinding.ViewBinding
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
    *;
}
