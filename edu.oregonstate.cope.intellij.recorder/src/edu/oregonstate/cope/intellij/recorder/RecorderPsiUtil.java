package edu.oregonstate.cope.intellij.recorder;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.codehaus.groovy.runtime.ReverseListIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by mihai on 5/8/14.
 */
public class RecorderPsiUtil {
    public static String getQualifiedName(PsiElement psiElement){

        if (psiElement instanceof PsiMember)
            return PsiUtil.getMemberQualifiedName((PsiMember) psiElement);

        if(psiElement instanceof PsiQualifiedNamedElement)
            return ((PsiQualifiedNamedElement)psiElement).getQualifiedName();

        List<PsiElement> ancestors = getPathToRoot(psiElement);

        List<String> nameFragments = mapAncestorsToNames(ancestors);

        String qualifiedName = stitchNameFragments(psiElement, nameFragments);

        return qualifiedName;
    }

    private static String stitchNameFragments(PsiElement psiElement, List<String> nameFragments) {
        String qualifiedName = "";

        ReverseListIterator<String> iterator = new ReverseListIterator<>(nameFragments);

        while (iterator.hasNext()){
            qualifiedName += iterator.next() + ".";
        }

        if(PsiUtilCore.getName(psiElement) != null)
            qualifiedName += PsiUtilCore.getName(psiElement);
        else
            qualifiedName += "unnamed";

        return qualifiedName;
    }

    private static List<String> mapAncestorsToNames(List<PsiElement> ancestors) {
        List<String> nameFragments = new ArrayList<>();

        for (PsiElement pathElement : ancestors){
            if(pathElement instanceof PsiQualifiedNamedElement){
                nameFragments.add(((PsiQualifiedNamedElement) pathElement).getQualifiedName());
                break;
            }

            String simpleName = PsiUtilCore.getName(pathElement);

            if (simpleName != null)
                nameFragments.add(simpleName);
            else
                continue;
        }

        return nameFragments;
    }

    /**
     * @param psiElement
     * @return list of psi elements starting from parent of psiElement up to root
     */
    private static List<PsiElement> getPathToRoot(PsiElement psiElement) {
        List<PsiElement> pathToRoot = new ArrayList<>();

        psiElement = psiElement.getParent();

        while (psiElement != null){
            pathToRoot.add(psiElement);
            psiElement = psiElement.getParent();
        }

        return pathToRoot;
    }

}
