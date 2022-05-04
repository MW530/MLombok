package priv.mw.processors;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import priv.mw.annotations.Setters;
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

@SupportedAnnotationTypes("priv.mw.annotations.Setters")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SettersProcessor extends BaseProcessor{

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager.printMessage(Diagnostic.Kind.NOTE,"setters" );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(Setters.class);
        messager.printMessage(Diagnostic.Kind.NOTE,"setter开始");
        for (Element element : elementsAnnotatedWith) {
            if(!TreeUtils.isClassType(rootTrees.getTree(element))) continue;
            messager.printMessage(Diagnostic.Kind.NOTE, "设置setter："+element.getSimpleName());
            JCTree elementTree = rootTrees.getTree(element);
            elementTree.accept(new TreeTranslator(){
                @Override
                public void visitClassDef(JCTree.JCClassDecl classDecl) {
                    for (JCTree def : classDecl.defs) {
                        if (def.getKind().equals(Tree.Kind.VARIABLE)) {
                            JCTree.JCVariableDecl varDef = (JCTree.JCVariableDecl)def;
                            if(!TreeUtils.existAccessMethod(classDecl, varDef, TreeUtils.SETTER)){
                                JCTree.JCMethodDecl setter = createSetter((JCTree.JCVariableDecl)def);
                                classDecl.defs = classDecl.defs.append(setter);
                            }
                        }
                    }
                }
            });
            messager.printMessage(Diagnostic.Kind.NOTE, "设置setter完成："+element.getSimpleName());
        }
        return false;
    }

    public JCTree.JCMethodDecl createSetter(JCTree.JCVariableDecl variableDecl){
        messager.printMessage(Diagnostic.Kind.NOTE, "创建getter："+variableDecl.getName().toString());
        JCTree.JCAssign assignment = treeMaker.Assign(
                treeMaker.Select(treeMaker.Ident(names.fromString("this")), variableDecl.name),
                treeMaker.Ident(variableDecl.name));
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();
        jcStatements.add(treeMaker.Exec(assignment));
        JCTree.JCBlock block = treeMaker.Block(0, jcStatements.toList());
        JCTree.JCVariableDecl paramVar = treeMaker.VarDef(treeMaker.Modifiers(Flags.PARAMETER), variableDecl.name, variableDecl.vartype, null);
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString("set" + TreeUtils.convertFirstLetter2UpperCase(variableDecl.name.toString())),
                treeMaker.TypeIdent(TypeTag.VOID),
                List.nil(),
                null,
                List.of(paramVar),
                List.nil(),
                block,
                null
        );
    }
}
