# 避免 ViewBinding 类被混淆导致反射初始化失败
-keepclassmembers class ** implements androidx.viewbinding.ViewBinding {
    public static ** bind(***);
    public static ** inflate(***);
}
