package conversion7.aop

import conversion7.engine.utils.Utils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.tools.GeneralUtils
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.slf4j.Logger

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class TestStepsTransformer implements ASTTransformation {

    private static final Logger LOG = Utils.getLoggerForClass();

    static final StringBuilder DEBUG = new StringBuilder()

    // TODO use @SkipLoggerTransformation annotation
    /**Hide these methods from handling.*/
    static List<String> SYSTEM_METHODS = [
            "\$getStaticMetaClass"
            , "initialization"
            , "methodInvoked"
            , "propertyChange"
            , "handleInputSysProp"
            , "handleInputProps"
            , "handleInputProps"
            , "println"
            , "shouldTrace"
    ]

    @Override
    public void visit(final ASTNode[] nodes, final SourceUnit source) {
        DEBUG.setLength(0)

        AnnotationNode annotationNode = (AnnotationNode) nodes[0];
        ClassNode classNode = (ClassNode) nodes[1];
        ClassNode declaringClass = classNode.getDeclaringClass();

        DEBUG.append("TRANSFORM: ").append(classNode).append("\n")
        addMethodInterceptors(source, classNode)
//        throwDebugError()
    }

    static void throwError(Throwable e) {
        throw new RuntimeException("DEBUG:\n" + DEBUG.toString() + "\nERROR:\n" + e.getMessage(), e);
    }


    static void throwDebugError() {
        throwError(new Exception("DEBUGGING..."))
    }

    static void addMethodInterceptors(final SourceUnit sourceUnit, final ClassNode classNode) {
        classNode.methods.each { method ->
            DEBUG.append("\n=====\n")
            if (!(method.code instanceof BlockStatement)
                    || method.name in SYSTEM_METHODS
            ) {
                DEBUG.append("SKIP METHOD: " + method.text).append("\n===\n")
                return
            }
            DEBUG.append("HANDLING METHOD: " + method.text).append("\n")

            def methodExpression
            try {
                methodExpression = (BlockStatement) method.code
            } catch (Throwable t) {
                DEBUG.append("ERROR ON: " + method.text).append("\n===\n")
                return
            }
            DEBUG.append(methodExpression.text).append("\n===\n")

            addCheckCoreCrashBlock(methodExpression, method)
            addMethodLogBlock(methodExpression, method)
        }

    }

    static def addCheckCoreCrashBlock(BlockStatement methodExpression, MethodNode method) {
        def statement = new ExpressionStatement(
                GeneralUtils.callX(
                        GeneralUtils.classX(Class.forName("shared.TestingStub"))
                        , "handleCoreErrors"

                ))
        methodExpression.getStatements().add(0, statement)
    }

    static def addMethodLogBlock(BlockStatement methodExpression, MethodNode method) {
        def methodLog = new ExpressionStatement(
                GeneralUtils.callX(
                        GeneralUtils.varX("LOG")
                        , "debug"
                        , GeneralUtils.args(GeneralUtils.constX("AOP method [" + method.name + "]"))

                ))
        DEBUG.append("methodLog: " + methodLog.text).append("\n")
        methodExpression.getStatements().add(0, methodLog)
    }
}
