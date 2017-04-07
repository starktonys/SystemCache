# SystemCache
- 获得当前应用详情里面的应用大小，应用数据大小，应用缓存大小
- 如何获取安装包的大小，包括缓存大小(cachesize)、数据大小(datasize)、应用程序大小(codesize)，如下图所示的RE文件管理器的应用程序信息截图：

![](http://i.imgur.com/vkpHeol.png)


本部分的知识点涉及到AIDL、Java反射机制。理解起来也不是很难。

关于安装包得大小信息封装在PackageStats类中，该类很简单，只有几个字段

PackageStats类：

常用字段：

- public long cachesize           缓存大小

- public long codesize             应用程序包大小

- public long datasize              数据大小

- public String packageName  包名

PS：应用程序的总大小 = cachesize  + codesize  + datasize

也就是说只要获得了安装包所对应的

PackageStats对象，就可以获得信息了。但是在AndroidSDK中并没有显示提供获得该对象的API，是不是很苦恼呢？但是，我们可以通过反射机制来调用系统中隐藏的函数(@hide)来获得每个安装包得信息。



> 具体方法如下：

第一步、  通过反射机制调用

getPackageSizeInfo()  方法原型为：

内部调用流程如下，这个知识点较为复杂，知道即可，

getPackageSizeInfo方法内部调用getPackageSizeInfoLI(packageName, pStats)方法来完成包状态获取。

getPackageSizeInfoLI方法内部调用Installer.getSizeInfo(String pkgName, String apkPath,String fwdLockApkPath,   PackageStats pStats)，继而将包状态信息返回给参数pStats。getSizeInfo这个方法内部是以本机Socket方式连接到Server，

然后向server发送一个文本字符串命令，格式：getsize apkPath fwdLockApkPath 给server。Server将结果返回，并解析到pStats中。掌握这个调用知识链即可。

第二步、  由于需要获得系统级的服务或类，我们必须加入Android系统形成的AIDL文件，共两个：

IPackageStatsObserver.aidl 和 PackageStats.aidl文件。并将其放置在android.pm.content包路径下。


第三步、  创建一个类继承IPackageStatsObserver.Stub 它本质上实现了Binder机制。当我们把该类的一个实例通过getPackageSizeInfo()调用时，该函数继而启动了启动中间流程 去获取相关包得信息大小，当扫描完成后，最后将查询信息回调至该类的onGetStatsCompleted(in PackageStats pStats, boolean succeeded)方法，信息大小封装在此实例上。


![](http://i.imgur.com/DYhKOgh.png)

#  #License
Talk is cheap ,show me the code .I will publish more blog latter, follow me or star repo

Copyright (c) 2016 Stark.Tony.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[http://www.jianshu.com/u/b00c71168e32](http://www.jianshu.com/u/b00c71168e32 "简书")
[http://my.csdn.net/?ref=toolbar](http://my.csdn.net/?ref=toolbar "CSDN")