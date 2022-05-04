package priv.mw.utils;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;

public class TreeUtils {

    public static final int GETTER = 0;
    public static final int SETTER = 1;

    public static boolean existToString(JCTree.JCClassDecl classDecl){
        for (JCTree jcTree : classDecl.defs){
            if(jcTree.getKind().equals(Tree.Kind.METHOD)){
                JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl)jcTree;
                if (methodDecl.name.equals("toString")){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param classDecl
     * @param variableDecl
     * @param type GETTER/SETTER
     * @return
     */
    public static boolean existAccessMethod(JCTree.JCClassDecl classDecl, JCTree.JCVariableDecl variableDecl, int type){
        String methodName;
        if (type == GETTER){
            methodName = "get" + convertFirstLetter2UpperCase(variableDecl.name.toString());
        }else if (type == SETTER){
            methodName = "set" + convertFirstLetter2UpperCase(variableDecl.name.toString());
        }else{
            throw new RuntimeException("existAccessMethod type error");
        }
        final boolean[] flag = {false};
        classDecl.accept(new TreeTranslator(){
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl tree) {
                if(tree.name.toString().equals(methodName)){
                    flag[0] = true;
                }
                super.visitMethodDef(tree);
            }
        });
        return flag[0];
    }

//    public static boolean existGetter(JCTree.JCClassDecl classDecl, JCTree.JCVariableDecl variableDecl){
//
//        final boolean[] flag = {false};
//        classDecl.accept(new TreeTranslator(){
//            @Override
//            public void visitMethodDef(JCTree.JCMethodDecl tree) {
//                if(tree.name.toString().equals(methodName)){
//                    flag[0] = true;
//                }
//                super.visitMethodDef(tree);
//            }
//        });
//        return flag[0];
//    }
//
//    public static boolean existSetter(JCTree.JCClassDecl classDecl, JCTree.JCVariableDecl variableDecl){
//        String methodName = "set" + convertFirstLetter2UpperCase(variableDecl.name.toString());
//        final boolean[] flag = {false};
//        classDecl.accept(new TreeTranslator(){
//            @Override
//            public void visitMethodDef(JCTree.JCMethodDecl tree) {
//                if(tree.name.toString().equals(methodName)){
//                    flag[0] = true;
//                }
//                super.visitMethodDef(tree);
//            }
//        });
//        return flag[0];
//    }

    public static boolean isAnnotation(JCTree.JCClassDecl classDecl){
        if(classDecl.getKind().equals(Tree.Kind.ANNOTATION_TYPE)){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isClassType(JCTree tree){
        if(tree.getKind().equals(Tree.Kind.CLASS)){
            return true;
        }else{
            return false;
        }
    }

    public static String convertFirstLetter2UpperCase(String str){
        String firstLetter2Upper = str.substring(0, 1).toUpperCase();
        return firstLetter2Upper + str.substring(1);
    }
}
