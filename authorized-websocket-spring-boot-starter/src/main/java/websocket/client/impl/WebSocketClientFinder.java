package websocket.client.impl;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import websocket.client.api.WebSocketClient;

import java.util.List;

public class WebSocketClientFinder {

    private static final String BASE_SCAN_PACKAGE = "";

    public List<? extends Class<?>> findAllWebSocketClientTypes() {
        return getCandidateProvider().findCandidateComponents(BASE_SCAN_PACKAGE).stream()
                .map(BeanDefinition::getBeanClassName)
                .sorted()
                .map(this::loadClass)
                .toList();
    }

    private ClassPathScanningCandidateComponentProvider getCandidateProvider() {
        var candidateProvider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface();
            }
        };
        candidateProvider.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            return metadataReader.getAnnotationMetadata().hasAnnotation(WebSocketClient.class.getName());
        });
        return candidateProvider;
    }

    @SneakyThrows
    private Class<?> loadClass(String name) {
        return Class.forName(name);
    }
}
