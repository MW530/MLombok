package priv.mw.processors;

import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import priv.mw.annotations.ToString;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

import static priv.mw.utils.TreeUtils.existToString;
import static priv.mw.utils.TreeUtils.isAnnotation;


@SupportedAnnotationTypes("priv.mw.annotations.ToString")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ToStringProcessor extends BaseProcessor{

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager.printMessage(Diagnostic.Kind.NOTE,"toString" );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "ToString！！");
        Set<? extends Element> toStringElements = roundEnv.getElementsAnnotatedWith(ToString.class);
        for (Element toStringElement : toStringElements) {
            JCTree curTree = rootTrees.getTree(toStringElement);
            curTree.accept(new TreeTranslator(){
                @Override
                public void visitClassDef(JCTree.JCClassDecl JCClassDeclTree) {
                    if(isAnnotation(JCClassDeclTree)) return;
                    String className = JCClassDeclTree.name.toString();
                    messager.printMessage(Diagnostic.Kind.NOTE, className +"@ToString generate begin!");
                    if(!existToString(JCClassDeclTree)){
                        ListBuffer<JCTree.JCVariableDecl> varsList = new ListBuffer();
                        JCClassDeclTree.accept(new TreeTranslator() {
                            @Override
                            public void visitVarDef(JCTree.JCVariableDecl variableDecl) {
                                varsList.add(variableDecl);
                                super.visitVarDef(variableDecl);
                            }
                        });
                        JCTree.JCMethodDecl toStringMethod = createToString(varsList, className);
                        JCClassDeclTree.defs = JCClassDeclTree.defs.append(toStringMethod);
                    }
                }
            });
        }
        return false;
    }

    private JCTree.JCMethodDecl createToString(ListBuffer<JCTree.JCVariableDecl> varsList, String className){
        JCTree.JCBinary content = null;
        for (JCTree.JCVariableDecl jcVariableDecl : varsList) {
            if(content == null){
                content = treeMaker.Binary(JCTree.Tag.PLUS, treeMaker.Literal(className + "[" + jcVariableDecl.getName().toString() +"="), treeMaker.Ident(names.fromString(jcVariableDecl.getName().toString())));
            }else{
                JCTree.JCBinary temp = treeMaker.Binary(JCTree.Tag.PLUS, treeMaker.Literal(", "+ jcVariableDecl.getName().toString()+"="), treeMaker.Ident(names.fromString(jcVariableDecl.getName().toString())));
                content = treeMaker.Binary(JCTree.Tag.PLUS, content, temp);

            }
        }
        content = treeMaker.Binary(JCTree.Tag.PLUS, content, treeMaker.Literal("]"));
        JCTree.JCReturn aReturn = treeMaker.Return(content);
        JCTree.JCBlock jcBlock = treeMaker.Block(0, new ListBuffer<JCTree.JCStatement>(){{append(aReturn);}}.toList());
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                names.fromString("toString"),
                treeMaker.Ident(names.fromString("String")),
                List.nil(),
                List.nil(),
                List.nil(),
                jcBlock,
                null
        );
    }
}
