package com.hummer.compiler;

import com.hummer.annotation.HM_EXPORT_CLASS;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.annotation.processing.Filer;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.hummer.annotation.HM_EXPORT_CLASS")
public class HMExportCompiler extends AbstractProcessor {

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (TypeElement typeElement : set) {
            Hashtable<String, String> hashtable = new Hashtable<>();

            for (Element element : roundEnvironment.getElementsAnnotatedWith(typeElement)) {
                if(element.getKind() == ElementKind.CLASS){
                    HM_EXPORT_CLASS exportClass = element.getAnnotation(HM_EXPORT_CLASS.class);
                    hashtable.put(exportClass.value(), ((TypeElement)element).getQualifiedName().toString());
                }
            }
            generateCodes(roundEnvironment, hashtable);
        }
        return false;
    }

    private void generateCodes(RoundEnvironment roundEnv, Hashtable <String, String> hashtable){
        String methodBody = "return new $T() {\n\t{\n";
        for (Map.Entry entry : hashtable.entrySet()) {
            methodBody += "\t\tput(\"" + entry.getKey() +"\",\""+ entry.getValue() +"\");\n";
        }
        methodBody += "\t}\n};\n";

        MethodSpec exportClasses = MethodSpec.methodBuilder("exportClasses")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Hashtable.class)
                .addCode(methodBody, Hashtable.class)
                .build();

        TypeSpec classFile = TypeSpec.classBuilder("HMExportCollection")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(exportClasses).build();

        JavaFile javaFile = JavaFile.builder("com.hummer.core", classFile)
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
