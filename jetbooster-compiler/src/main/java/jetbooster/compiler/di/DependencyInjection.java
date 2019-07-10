/*
 * Copyright 2019 yinpinjiu@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbooster.compiler.di;

import com.squareup.javapoet.*;
import jetbooster.annotations.ActivityScope;
import jetbooster.compiler.Constants;
import jetbooster.compiler.code.CodeUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

public class DependencyInjection {

    DependencyGraph graph;
    Map<CharSequence, List<Element>> targets;

    Filer filer;
    Elements elementUtils;
    Types typeUtils;

    public DependencyInjection() {
        graph = new DependencyGraph();
        targets = new HashMap<>();
    }

    public void init(ProcessingEnvironment processingEnvironment) {
        filer = processingEnvironment.getFiler();
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
    }

    public void process(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Inject.class);
        ClassName viewModelProviders = ClassName.get("androidx.lifecycle", "ViewModelProviders");

        for (Element e : elements) {
            switch (e.getKind()) {
                case FIELD:
                    VariableElement ve = ((VariableElement) e);
                    TypeElement te = ((TypeElement) ve.getEnclosingElement());
                    if (targets.get(te.getQualifiedName()) == null) {
                        targets.put(te.getQualifiedName(), new ArrayList<>());
                    }
                    targets.get(te.getQualifiedName()).add(ve);
                    break;
            }
        }

        TypeSpec.Builder injectorMapBuilder =
                TypeSpec.classBuilder(ClassName.get("jetbooster.di", "InjectorMap"))
                        .addModifiers(Modifier.PUBLIC);
        CodeBlock.Builder staticBlock = CodeBlock.builder();

        for (Map.Entry<CharSequence, List<Element>> entry : targets.entrySet()) {
            TypeElement target = elementUtils.getTypeElement(entry.getKey());

            MethodSpec.Builder builder =
                    MethodSpec.methodBuilder("inject")
                            .addAnnotation(Override.class)
                            .addParameter(TypeName.get(target.asType()), "target")
                            .addModifiers(Modifier.PUBLIC);

            for (Element element : entry.getValue()) {
                if (element instanceof VariableElement) {
                    VariableElement ve = ((VariableElement) element);
                    ActivityScope scope = ve.getAnnotation(ActivityScope.class);
                    CodeBlock assignment =
                            CodeBlock.of(
                                    "target.$L = $T.of(target).get($T.class)",
                                    ve.getSimpleName(),
                                    viewModelProviders,
                                    ClassName.get(ve.asType()));
                    if (scope != null) {
                        if (typeUtils.isSubtype(
                                        target.asType(),
                                        elementUtils
                                                .getTypeElement(Constants.FRAGMENT_TYPE_NAME)
                                                .asType())
                                || typeUtils.isSubtype(
                                        target.asType(),
                                        elementUtils
                                                .getTypeElement(
                                                        Constants.LEGACY_SUPPORT_FRAGMENT_TYPE_NAME)
                                                .asType())
                                || typeUtils.isSubtype(
                                        target.asType(),
                                        elementUtils
                                                .getTypeElement(
                                                        Constants.SUPPORT_FRAGMENT_TYPE_NAME)
                                                .asType())) {
                            assignment =
                                    CodeBlock.of(
                                            "target.$L = $T.of(target.getActivity()).get($T.class)",
                                            ve.getSimpleName(),
                                            viewModelProviders,
                                            ClassName.get(ve.asType()));
                        }
                    }
                    builder.addStatement(assignment);
                }
            }

            ClassName outer = ClassName.get(target);
            ClassName injector = ClassName.get(outer.packageName(), outer.simpleName() + "$Injector");
            ClassName injectorInterface = ClassName.get("jetbooster.di", "Injector");
            TypeSpec typeSpec =
                    TypeSpec.classBuilder(injector)
                            .addModifiers(Modifier.PUBLIC)
                            .addSuperinterface(ParameterizedTypeName.get(injectorInterface, outer))
                            .addMethod(builder.build())
                            .build();

            CodeUtils.write(filer, elementUtils.getPackageOf(target).getQualifiedName(), typeSpec);

            staticBlock.addStatement("Injectors.register($T.class, new $T())", outer, injector);
        }

        injectorMapBuilder.addStaticBlock(staticBlock.build());
        CodeUtils.write(filer, "jetbooster.di", injectorMapBuilder.build());
    }
}
