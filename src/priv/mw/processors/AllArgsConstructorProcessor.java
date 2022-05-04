package priv.mw.processors;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import priv.mw.annotations.AllArgsConstructor;
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
@SupportedAnnotationTypes("priv.mw.annotations.AllArgsConstructor")
public class AllArgsConstructorProcessor extends BaseProcessor{
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager.printMessage(Diagnostic.Kind.NOTE, "AllArgsConstructor");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(AllArgsConstructor.class);
        for (Element element : elementsAnnotatedWith) {
            if(!TreeUtils.isClassType(rootTrees.getTree(element))) continue;
            JCTree elementTree = rootTrees.getTree(element);
            ListBuffer<JCTree.JCVariableDecl> varList = new ListBuffer<>();
            elementTree.accept(new TreeTranslator(){
                @Override
                public void visitClassDef(JCTree.JCClassDecl classDecl) {
                    for (JCTree def : classDecl.defs) {
                        if(def.getKind().equals(Tree.Kind.VARIABLE)){
                            varList.add((JCTree.JCVariableDecl)def);
                        }
                    }
                    JCTree.JCMethodDecl allArgsConstructor = createAllArgsConstructor(varList, classDecl);
                    classDecl.defs = classDecl.defs.append(allArgsConstructor);
                }
            });
        }
        return false;
    }

    public JCTree.JCMethodDecl  createAllArgsConstructor(ListBuffer<JCTree.JCVariableDecl> varList, JCTree.JCClassDecl jcClassDecl){
        ListBuffer<JCTree.JCVariableDecl> parameterList = new ListBuffer<>();
        ListBuffer<JCTree.JCStatement> bodyList = new ListBuffer<>();
        for (JCTree.JCVariableDecl jcVariableDecl : varList) {
            JCTree.JCVariableDecl parameter = treeMaker.VarDef(
                    treeMaker.Modifiers(Flags.PARAMETER),
                    jcVariableDecl.name,
                    jcVariableDecl.vartype,
                    null
            );
            JCTree.JCStatement assign = treeMaker.Exec(
                    treeMaker.Assign(
                            treeMaker.Select(treeMaker.Ident(names.fromString("this")), jcVariableDecl.name),
                            treeMaker.Ident(jcVariableDecl.name)
                    )
            );
            bodyList.append(assign);
            parameterList.append(parameter);
        }
        JCTree.JCBlock body = treeMaker.Block(0, bodyList.toList());
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString("<init>"),
                treeMaker.TypeIdent(TypeTag.VOID),
                List.nil(),
                parameterList.toList(),
                List.nil(),
                body,
                null
        );
    }
}
