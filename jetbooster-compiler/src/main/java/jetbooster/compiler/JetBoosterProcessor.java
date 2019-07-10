package jetbooster.compiler;

import jetbooster.compiler.di.DependencyInjection;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes({"javax.inject.Inject"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JetBoosterProcessor extends AbstractProcessor {

    DependencyInjection di;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        di = new DependencyInjection();
        di.init(processingEnvironment);
    }

    @Override
    public boolean process(
            Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        // Use JSR-330 Dependency Injection Annotations.
        // Some extra code is generated and it may interfere with third DI library like dagger.
        // So the invoking order of injection method of different library matters.
        di.process(roundEnvironment);
        return false;
    }
}
