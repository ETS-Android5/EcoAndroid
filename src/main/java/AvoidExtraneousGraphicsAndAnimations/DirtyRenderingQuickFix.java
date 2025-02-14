package AvoidExtraneousGraphicsAndAnimations;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.codeStyle.IndentHelper;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class DirtyRenderingQuickFix implements LocalQuickFix {
    private static final String QUICK_FIX_NAME = "EcoAndroid: Apply pattern Avoid Extraneous Graphics and Animations [Switching to Dirty Rendering]";

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() { return QUICK_FIX_NAME; }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiExpression argExpression  = (PsiExpression) descriptor.getPsiElement();
        PsiMethod psiMethod = PsiTreeUtil.getParentOfType(argExpression, PsiMethod.class);
        PsiClass psiClass = psiMethod.getContainingClass();
        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        PsiFile psiFile = psiClass.getContainingFile();

        try {
            PsiExpression psiNewExpression = factory.createExpressionFromText("GLSurfaceView.RENDERMODE_WHEN_DIRTY",null);
            argExpression.replace(psiNewExpression);

            PsiComment comment = factory.createCommentFromText("/* \n "
                    + StringUtils.repeat(" ", IndentHelper.getInstance().getIndent(psiFile, psiMethod.getNode()))
                    + "* EcoAndroid: AVOID EXTRANEOUS GRAPHICS AND ANIMATIONS ENERGY PATTERN APPLIED \n"
                    + StringUtils.repeat(" ", IndentHelper.getInstance().getIndent(psiFile, psiMethod.getNode()))
                    + "* Changing rendering mode to one that only renders when it is created or when is it requested by the method \"requestRender()\"\n"
                    + StringUtils.repeat(" ", IndentHelper.getInstance().getIndent(psiFile, psiMethod.getNode()))
                    + "* Application changed java file \"" + psiClass.getContainingFile().getName() + "\"\n"
                    + StringUtils.repeat(" ", IndentHelper.getInstance().getIndent(psiFile, psiMethod.getNode()))
                    + "*/", psiClass.getContainingFile());
            psiMethod.addBefore(comment, psiMethod.getFirstChild());
        }catch(Throwable e) {
            PsiComment comment = factory.createCommentFromText("/* \n"
                    + StringUtils.repeat(" ", IndentHelper.getInstance().getIndent(psiFile, psiMethod.getNode()))
                    + "* EcoAndroid:AVOID EXTRANEOUS GRAPHICS AND ANIMATIONS ENERGY PATTERN NOT APPLIED \n"
                    + StringUtils.repeat(" ", IndentHelper.getInstance().getIndent(psiFile, psiMethod.getNode()))
                    + "* Something went wrong and the pattern could not be applied! \n"
                    + StringUtils.repeat(" ", IndentHelper.getInstance().getIndent(psiFile, psiMethod.getNode()))
                    +"*/", psiFile);
            psiMethod.addBefore(comment, psiMethod.getFirstChild());
        }



    }
}
