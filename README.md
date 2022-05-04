# 这是一个什么项目？
这是一个简易版的Lombok，主要是为了学习注解处理器的用法。

# 实现了什么功能？
实现了几个常用的Lombok注解：
- AllArgsConstructor
- Getters
- Setters
- ToString
功能自不必多介绍，与lombok一致，分别用来生成全参构造器，getters方法，setters方法和ToString方法。都是用来类上。

## 未实现的功能
由于注解处理器只是作用在编译时，所以要想在idea中直接运行时可以像Lombok一样使用，还必须配合对应ide的插件。比如Lombok为不同的ide如idea，eclipse等制作了相应的插件。使之可以在运行时直接运行，这一部分是还没有完成的。

## 该如何使用呢？
由于没有ide的插件，所以要想使用，我们手动使用javac来编译，并且配置相应的参数。
### 需要如何配置？
首先由于Java9的模块化使com.sun.*包下的类都不公开了。无法直接调用，需要使用其他参数。所以这里目前只介绍了Java8下的用法。
其基本用法如下：
- 应当在根目录下创建target目录，用于存放我们生成的class文件。
- 配置JAVA_HOME环境变量，这里安装Java时应该是配置了的。
- 编译注解类，例如
  ```bat
        javac  -d target -sourcepath src src/priv/mw/annnotations/ToString.java
    ```
  其中的-d代表生成的class文件所在的目录，而-sourcepath用来表示后面的Java文件应该从哪个目录中搜索。、
- 编译注解处理器类，例如
  ```bat
        javac -d target  -sourcepath src -cp {JAVA_HOME}/lib/tools.jar;target -encoding UTF-8 src/priv/mw/processors/ToStringProcessor.java
    ```
  这里-cp是指后面的类中依赖的类应该在哪里查找。-encoding表示编码，为了显示中文。
- 编译测试实体
    ```bat
        javac -d target  -sourcepath src -cp {JAVA_HOME}/lib/tools.jar;target -encoding UTF-8 -processorpath target -processor priv.mw.processors.AllArgsConstructorProcessor,priv.mw.processors.GettersProcessor,priv.mw.processors.SettersProcessor,priv.mw.processors.ToStringProcessor src/priv/mw/User.java
    ```
  注意这里的`-processorpath target`表示注解类应当从哪里去找，由于我们前面编译的类都在target中，所以可以直接从这里这里查找。`-processor xxx`表示配置了哪些注解处理器。
- 反编译产生的User.class查看效果。
  ```bat
    javap src/User.class  
    ```
  这一步可能会找不到javap命令，则需要将`{JAVA_HOME}/bin`添加到环境变量中。 也可以直接拖class到idea中，会自动反编译。

关于javac命令，可以查看这篇文章，[javac命令参数参考](https://www.mw530.cn/2022/04/28/javac%E5%91%BD%E4%BB%A4%E5%8F%82%E6%95%B0%E5%8F%82%E8%80%83/)。
具体的命令，已经放在了`MCompile.bat`。

# 原理
写这个小玩意也是为了了解其底层原理-注解处理器，其可以在前端编译阶段让我们来读取甚至修改原始的文件。但是矛盾的是，虽然注解处理器有能力访问原始文件，Oracle却将com.sun.*系列类设为内置类。并建议一般开发者不要访问。但是要想修改原始文件，com.sun.*系列中的抽象语法树相关的类都必须使用到。
具体的
- 注解处理器看这里[深入理解JVM3-1-程序编译与代码优化-前端编译与优化](https://www.mw530.cn/2022/04/28/%E6%B7%B1%E5%85%A5%E7%90%86%E8%A7%A3JVM3-1-%E7%A8%8B%E5%BA%8F%E7%BC%96%E8%AF%91%E4%B8%8E%E4%BB%A3%E7%A0%81%E4%BC%98%E5%8C%96-%E5%89%8D%E7%AB%AF%E7%BC%96%E8%AF%91%E4%B8%8E%E4%BC%98%E5%8C%96/)
- 抽象语法树系列看这里[Java编译相关的JCTree和TreeMaker的API介绍](https://www.mw530.cn/2022/04/28/JCTree%E5%92%8CTreeMaker%E7%9A%84API%E4%BB%8B%E7%BB%8D/)
- javac的用法看这里[javac命令参数参考](https://www.mw530.cn/2022/04/28/javac%E5%91%BD%E4%BB%A4%E5%8F%82%E6%95%B0%E5%8F%82%E8%80%83/)