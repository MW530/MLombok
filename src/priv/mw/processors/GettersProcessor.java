package priv.mw.processors;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import priv.mw.annotations.Getters;
import priv.mw.utils.TreeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("priv.mw.annotations.Getters")
public class GettersProcessor extends BaseProcessor{

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager.printMessage(Diagnostic.Kind.NOTE,"getters" );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE,"getter开始");
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Getters.class);
        for (Element element : elementsAnnotatedWith) {
            messager.printMessage(Diagnostic.Kind.NOTE, "设置getters："+element.getSimpleName());
            if(!TreeUtils.isClassType(rootTrees.getTree(element))) continue;
            JCTree elementTree = rootTrees.getTree(element);
            elementTree.accept(new TreeTranslator(){
                @Override
                public void visitClassDef(JCTree.JCClassDecl classDecl) {
                    for (JCTree def : classDecl.defs) {
                        if(def.getKind().equals(Tree.Kind.VARIABLE)){
                            JCTree.JCVariableDecl varDef = (JCTree.JCVariableDecl)def;
                            if(!TreeUtils.existAccessMethod(classDecl, varDef, TreeUtils.GETTER)){
                                JCTree.JCMethodDecl getter = createGetter(varDef);
                                classDecl.defs = classDecl.defs.append(getter);
                            }
                        }
                    }
                }
            });
            messager.printMessage(Diagnostic.Kind.NOTE, "设置getter完成："+element.getSimpleName());
        }
        return false;
    }

    public JCTree.JCMethodDecl createGetter(JCTree.JCVariableDecl jcVariableDecl){
        messager.printMessage(Diagnostic.Kind.NOTE, "创建getter："+jcVariableDecl.getName().toString());
        JCTree.JCReturn getReturn = treeMaker.Return(treeMaker.Select(
                treeMaker.Ident(names.fromString("this")),
                jcVariableDecl.name
        ));
        JCTree.JCBlock returnBlock = treeMaker.Block(0, new ListBuffer<JCTree.JCStatement>(){{append(getReturn);}}.toList());
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString("get"+ TreeUtils.convertFirstLetter2UpperCase(jcVariableDecl.name.toString())),
                jcVariableDecl.vartype,
                List.nil(),
                List.nil(),
                List.nil(),
                returnBlock,
                null);
    }
}
