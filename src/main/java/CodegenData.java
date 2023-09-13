import org.gradle.api.provider.Property;

abstract public class CodegenData {
    abstract public Property<String> getOpenApiPath();
    abstract public Property<String> getOutDir();
    abstract public Property<String> getSrcDir();
    abstract public Property<String> getListenerPackage();
    abstract public Property<String> getModelPackage();
    abstract public Property<Boolean> getKafkaClient();
    abstract public Property<Boolean> getInterfaceOnly();
    abstract public Property<String> getResultWrapper();

    abstract public Property<String> getSecurityAnnotation();
    abstract public Property<Boolean> getSendBytes();

    abstract public Property<Boolean> getUseSpring3();

    abstract public Property<Boolean> getUseAutoconfig();
    public abstract Property<String> getMessageIdName();
    public abstract Property<String> getCorrelationIdName();
    public abstract Property<Boolean> getGenerateMessageId();
    public abstract Property<Boolean> getGenerateCorrelationId();
}
