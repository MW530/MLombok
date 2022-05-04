:: 1. Java version must lower than or equals to 1.8
:: 2. {JAVA_HOME} is an environment variable, you must configure it first. Or you can replace it with the absolute path of the tool.jar.

javac  -d target -sourcepath src src/priv/mw/annnotations/ToString.java
javac  -d target -sourcepath src src/priv/mw/annnotations/Getters.java
javac  -d target -sourcepath src src/priv/mw/annnotations/Setters.java
javac  -d target -sourcepath src src/priv/mw/annnotations/AllArgsConstructor.java

javac -d target  -sourcepath src -cp {JAVA_HOME}/lib/tools.jar;target -encoding UTF-8 src/priv/mw/processors/BaseProcessor.java
javac -d target  -sourcepath src -cp {JAVA_HOME}/lib/tools.jar;target -encoding UTF-8 src/priv/mw/processors/GettersProcessor.java
javac -d target  -sourcepath src -cp {JAVA_HOME}/lib/tools.jar;target -encoding UTF-8 src/priv/mw/processors/SettersProcessor.java
javac -d target  -sourcepath src -cp {JAVA_HOME}/lib/tools.jar;target -encoding UTF-8 src/priv/mw/processors/ToStringProcessor.java
javac -d target  -sourcepath src -cp {JAVA_HOME}/lib/tools.jar;target -encoding UTF-8 src/priv/mw/processors/AllArgsConstructorProcessor.java

:: javac -d target  -sourcepath src -cp C:/Users/MW/.jdks/corretto-1.8.0_312/lib/tools.jar;target -encoding UTF-8 -processorpath target -processor priv.mw.processors.GettersProcessor,priv.mw.processors.SettersProcessor,priv.mw.processors.ToStringProcessor src/priv/mw/User.java

javac -d target  -sourcepath src -cp {JAVA_HOME}/lib/tools.jar;target -encoding UTF-8 -processorpath target -processor priv.mw.processors.AllArgsConstructorProcessor,priv.mw.processors.GettersProcessor,priv.mw.processors.SettersProcessor,priv.mw.processors.ToStringProcessor src/priv/mw/User.java