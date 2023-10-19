import com.kafka.company.codegen.RabbitCodeGenerator;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.io.File;
import java.util.List;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConstants;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.languages.features.CXFServerFeatures;

public abstract class RabbitCodegenTask extends DefaultTask {
    @Input
    abstract public Property<String> getOpenApiPath();

    @Input
    abstract public Property<String> getOutDir();

    @Input
    abstract public Property<String> getSrcDir();

    @Input
    abstract public Property<String> getListenerPackage();

    @Input
    abstract public Property<String> getModelPackage();

    @Input
    @Optional
    abstract public Property<Boolean> getRabbitClient();

    @Input
    @Optional
    abstract public Property<Boolean> getInterfaceOnly();

    @Input
    @Optional
    abstract public Property<String> getResultWrapper();

    @Input
    @Optional
    abstract public Property<String> getSecurityAnnotation();

    @Input
    @Optional
    abstract public Property<Boolean> getSendBytes();

    @Input
    @Optional
    abstract public Property<Boolean> getUseSpring3();

    @Input
    @Optional
    abstract public Property<Boolean> getUseAutoconfig();

    @Input
    @Optional
    public abstract Property<String> getMessageIdName();

    @Input
    @Optional
    public abstract Property<String> getCorrelationIdName();

    @Input
    @Optional
    public abstract Property<Boolean> getGenerateMessageId();

    @Input
    @Optional
    public abstract Property<Boolean> getGenerateCorrelationId();

    private final static boolean defaultInterfaceOnly = true;
    private final static boolean defaultRabbitClient = false;
    private final static String defaultSecurityAnnotation = null;
    private final static Boolean defaultSendBytes = true;

    private final static Boolean defaultUseAutoconfig = true;
    private final static Boolean defaultUseSpring3 = false;

    private final static Boolean defaultGenerateMessageId = true;
    private final static Boolean defaultGenerateCorrelationId = true;
    private final static String defaultMessageIdName = "rabbit_messageId";
    private final static String defaultCorrelationIdName = "rabbit_correlationId";

    @TaskAction
    public void javaTask() {

        OpenAPI openAPI = new OpenAPIParser()
                .readLocation(getOpenApiPath().get(), null, new ParseOptions()).getOpenAPI();
        RabbitCodeGenerator codegen = new RabbitCodeGenerator();
        codegen.setOutputDir(getOutDir().get());
        codegen.setApiPackage(getListenerPackage().get());
        codegen.setModelPackage(getModelPackage().get());
        codegen.setSourceFolder(getSrcDir().get());
        codegen.additionalProperties().put(CXFServerFeatures.LOAD_TEST_DATA_FROM_FILE, "true");
        codegen.setUseOneOfInterfaces(false);
        codegen.setRabbitClient(getRabbitClient().getOrElse(defaultRabbitClient));
        codegen.setSecurityAnnotation(getSecurityAnnotation().getOrElse(defaultSecurityAnnotation));
        codegen.setSendBytes(getSendBytes().getOrElse(defaultSendBytes));
        codegen.setGenerateCorrelationId(getGenerateCorrelationId().getOrElse(defaultGenerateCorrelationId));
        codegen.setGenerateMessageId(getGenerateCorrelationId().getOrElse(defaultGenerateMessageId));
        codegen.setMessageIdName(getMessageIdName().getOrElse(defaultMessageIdName));
        codegen.setCorrelationIdName(getCorrelationIdName().getOrElse(defaultCorrelationIdName));

        if (!getResultWrapper().getOrElse("").isBlank()) {
            codegen.setResultWrapper(getResultWrapper().getOrNull());
        }

        codegen.setInterfaceOnly(getInterfaceOnly().getOrElse(defaultInterfaceOnly));

        ClientOptInput input = new ClientOptInput();
        input.openAPI(openAPI);
        codegen.setUseOneOfInterfaces(false);
        codegen.setLegacyDiscriminatorBehavior(false);
        codegen.setUseTags(false);
        codegen.setSkipDefaultInterface(true);
        if (this.getUseSpring3().getOrElse(defaultUseSpring3)) {
            codegen.setUseSpringBoot3(true);
        }
        input.config(codegen);

        DefaultGenerator generator = new DefaultGenerator();
        generator.setGeneratorPropertyDefault(CodegenConstants.MODELS, "true");
        generator.setGeneratorPropertyDefault(CodegenConstants.LEGACY_DISCRIMINATOR_BEHAVIOR, "false");
        generator.setGeneratorPropertyDefault(CodegenConstants.MODEL_TESTS, "false");
        generator.setGeneratorPropertyDefault(CodegenConstants.MODEL_DOCS, "false");
        generator.setGeneratorPropertyDefault(CodegenConstants.APIS, "true");
        generator.setGeneratorPropertyDefault(CodegenConstants.SUPPORTING_FILES, "true");
        generator.setGenerateMetadata(false);

        System.out.println("start generate");
        List<File> files = generator.opts(input).generate();

        files.forEach(f -> System.out.println(f.getAbsolutePath()));
        System.out.println("end generate");
    }
}
