/*
 * Copyright (C) 2023 Axenix Innovations LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

public class KafkaCodegenPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        try {
            Configuration axenapiPluginConf = project.getConfigurations().create("axenapiPluginConf", conf -> {
                conf.setVisible(false);
                conf.setCanBeConsumed(false);
                conf.setCanBeResolved(true);
                conf.setDescription("Dependencies for axenapi plugin");
                conf.defaultDependencies(deps -> {
                    try {
                        deps.add(project.getDependencies().create("org.openapitools:jackson-databind-nullable:0.2.6"));
                    } catch (RuntimeException runtimeExc) {
                        System.out.println("KafkaCodegenPlugin.apply: " + runtimeExc.getMessage());
                    }
                });
            });

            // make it resolvable
            project.getConfigurations().getByName("implementation").extendsFrom(axenapiPluginConf);
        } catch (RuntimeException runtimeExc) {
            System.out.println("KafkaCodegenPlugin.apply: " + runtimeExc.getMessage());
        }

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
