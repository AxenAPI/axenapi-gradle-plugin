import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class KafkaCodegenPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        CodegenData codegenData =
                project.getExtensions().create("codegenData", CodegenData.class);

        project.getTasks().register("generateKafka", KafkaCodegenTask.class, task -> {
            task.getListenerPackage().set(codegenData.getListenerPackage());
            task.getModelPackage().set(codegenData.getModelPackage());
            task.getOpenApiPath().set(codegenData.getOpenApiPath());
            task.getSrcDir().set(codegenData.getSrcDir());
            task.getOutDir().set(codegenData.getOutDir());
            task.getKafkaClient().set(codegenData.getKafkaClient());
            task.getInterfaceOnly().set(codegenData.getInterfaceOnly());
            task.getResultWrapper().set(codegenData.getResultWrapper());
            task.getSecurityAnnotation().set(codegenData.getSecurityAnnotation());
            task.getSendBytes().set(codegenData.getSendBytes());
            task.getUseSpring3().set(codegenData.getUseSpring3());
            task.getUseAutoconfig().set(codegenData.getUseAutoconfig());
            task.getGenerateCorrelationId().set(codegenData.getGenerateCorrelationId());
            task.getGenerateMessageId().set(codegenData.getGenerateMessageId());
            task.getCorrelationIdName().set(codegenData.getCorrelationIdName());
            task.getMessageIdName().set(codegenData.getMessageIdName());
        });
    }
}
