# Chapter07
这个Sample是学习如何给代码加入Trace Tag, 大家可以将这个代码运用到自己的项目中，然后利用systrace查看结果

结果如下：

```
    protected void onResume() {
        TraceTag.i("com.sample.systrace.MainActivity.onResume.()V");
        super.onResume();
        Log.i("MainActivity", "[onResume]");
        TraceTag.o();
    }
```

## 操作步骤
操作步骤如下：

1. 使用Android Studio打开工程Chapter07
2. 运行gradle task ":systrace-gradle-plugin:buildAndPublishToLocalMaven" 编译plugin插件
3. 使用Android Studio单独打开工程systrace-sample-android
4. 编译app

## 注意事项
在systrace-sample-android工程中，需要注意以下几点：

1. 插桩代码会自动过滤短函数，过滤结果输出到`Chapter07/systrace-sample-android/app/build/systrace_output/Debug.ignoremethodmap`。
2. 我们也可以单独控制不插桩的白名单，配置文件位于`Chapter07/systrace-sample-android/app/blacklist/blackMethodList.txt`， 可以指定class或者包名。
3. 插桩后的class文件在目录`Chapter07/systrace-sample-android/app/build/systrace_output/classes`中查看。

然后运行应用，需要打开systrace
```
python $ANDROID_HOME/platform-tools/systrace/systrace.py gfx view wm am pm ss dalvik app sched -b 90960 -a com.sample.systrace  -o test.log.html
```