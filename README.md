## Hummer

### 1. 环境搭建

#### 1.1. 准备所需工具
* JDK
* NDK
* Android SDK
* Android Studio

#### 搭建开发环境
上述工具无需我们一一下载，Google已将所需工具集成好了，可到安卓官网下载，[下载地址](https://developer.android.com/studio/)，需要科学上网。下载下来Android Studio安装包，点击安装即可（Next按钮）。

>注意： 
>安装过程中可能会遇到选在安装类型，有Standard和Custom两种。
> 
> * Standard，表示一切使用默认配置。
> * Custom，顾名思义可以根据用户需求自定义。
> 
> 这里我们使用 Standard 即可。
>

### 2. 工程结构
运行Android Studio，打开 hummer-android 项目。项目的关键目录如下所示。

#### 2.1. 主要结构说明
* **.gradle和.idea**

这俩目录主要放置Android Studio自动生成的一些文件，无需关心，建议不要手动编辑。

* **app**

安卓应用项目中的代码、资源等几乎都放置在此目录下，我们后期 Hummer 内核功能验证基本都在此目录下进行。

* **complier**

Hummer 导出类收集，编译时处理注解，注解处理器（APT），用于动态将注解生成Java文件。

* **build**

无需过多关心，它主要包含了一些在编译时自动生成的文件。

* **gradle**

这个目录包含了gradle wrapper的配置文件，使用gradle wrapper方式不需要提前将gradle下载好，而是自动根据本地缓存情况决定是否从网络下载gradle，Android Studio默认开启此方式。若手动配置可点击导航栏 *Android Studio -> Preferences... -> Build,Execution,Deploymen -> Gradle* 进行配置更改。

* **hummer**

hummer 项目工作目录，hummer 内核开发在此目录中进行。

#### 2.2. Script

Hummer 开发服务器，可实时监测工程业务js代码的变化并实时打包。（目前已配置到Gradle中，编译时可以自动启动，也可以进入项目目录中，执行start脚本运行）。

* **build.gradle**

项目的全局gradle的构建脚本，通常无需更改。

* **gradle.properties**

全局gradle配置文件，这里的配置属性将会影响到项目中所有的gradle编译脚本。

* **gradlew和gradle.bat**

用于终端中执行gradle命令，gradlew在类Unix系统如Linux或Mac中使用，gradle.bat在Windows系统中使用。

* **native-js-android.iml**

iml文件是Intel IDEA项目自动生成的文件（Android Studio基于它开发的），主要是用于标识作用，可忽略。

* **local.properties**

用于指定本机的Android SDK路径，通常自动生成的，可无需修改。除非本机SDK位置发生变化，可编辑此文件，修改SDK路径至新位置。

* **settings.gradle**

用于指定项目中引入的模块。

* **buildScript**

模块发布脚本，可用于发布到公司maven仓库或者发布到本地。

### 3. 安装依赖
Hummer开发服务器启动，需要用到ruby运行环境。可打开终端进入到工程中```Script```目录，执行下列命令安装：

```
$ bundle install
```

>注意：
>若本地未安装bundle，可打开终端，执行下列命令安装：
>
>```$ gem install bundler ```
>
>具体可参见bundle官方文档，[Bundler](https://bundler.io)

### 4. 编译并运行
确保先运行了模拟器或者连接了真机，然后编译并运行Android Studio即可。Demo 运行时会启动一个 Server 服务，该服务主要是用于 Hummer 脚本的开发与调试。

### 5. 模块编译发布
hummer安卓项目核心模块包含：annotation、compiler、core、sdk。编译依赖关系是：  sdk 依赖 core、annotation，compiler，core 依赖 annotation。

Hummer安卓项目开发采用本地工程依赖的方式，不多赘述。


### 6. 常用终端命令
#### 6.1. Java版本切换

可用通过修改终端环境变量，如果安装多个Java版本时可能用到，一些工具不支持高版本（如：buck，支持到Java8）。如：

```
$ export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/Contents/Home/ 
$ export PATH=$JAVA_HOME/bin:$PATH
```
#### 6.2. Mac删除指定版本Java

```
$ cd /Library/Java/JavaVirtualMachines
$ ls
$ sudo rm -rf jdk-xxxxx.jdk
```
#### 6.3. 安卓gradlew初始化

进入到工程目录执行如下命令即可：

```
$ ./gradlew tasks
```

#### 6.4. 查看日志

```
$ adb logcat
```


