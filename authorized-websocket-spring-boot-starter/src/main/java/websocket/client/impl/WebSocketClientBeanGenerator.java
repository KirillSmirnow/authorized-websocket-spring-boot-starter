package websocket.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Slf4j
@Component
public class WebSocketClientBeanGenerator implements BeanDefinitionRegistryPostProcessor {

    private final WebSocketClientFinder webSocketClientFinder = new WebSocketClientFinder();
    private BeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        for (var type : webSocketClientFinder.findAllWebSocketClientTypes()) {
            var beanName = type.getSimpleName();
            var beanDefinition = buildBeanDefinition(type);
            registry.registerBeanDefinition(beanName, beanDefinition);
            log.debug("Registered WebSocket client: {}", beanName);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> BeanDefinition buildBeanDefinition(Class<T> type) {
        return BeanDefinitionBuilder.genericBeanDefinition(type, () ->
                (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{type}, new WebSocketClientInvocationHandler(type, beanFactory))
        ).getBeanDefinition();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
