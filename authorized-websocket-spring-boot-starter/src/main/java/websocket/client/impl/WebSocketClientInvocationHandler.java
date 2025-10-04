package websocket.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import websocket.client.api.UserPath;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class WebSocketClientInvocationHandler implements InvocationHandler {

    private final String clientName;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public WebSocketClientInvocationHandler(Class<?> type, BeanFactory beanFactory) {
        this.clientName = type.getSimpleName();
        this.simpMessagingTemplate = beanFactory.getBean(SimpMessagingTemplate.class);
        log.debug("Instantiated WebSocket client: {}", clientName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        var arguments = new Arguments(method, args);
        log.debug("Invoking {}.{}{}", clientName, method.getName(), arguments);
        var userPath = method.getAnnotation(UserPath.class);
        if (userPath != null) {
            var userId = arguments.extractArgument("userId", Object.class).toString();
            var path = "/users/%s%s".formatted(userId, buildPath(userPath.value(), arguments));
            var body = arguments.getBody();
            simpMessagingTemplate.convertAndSend(path, body);
            log.debug("Sent to {}: {}", path, body);
        }
        return null;
    }

    private String buildPath(String template, Arguments arguments) {
        return Pattern.compile("\\{([^}]+)}").matcher(template)
                .replaceAll(result -> arguments.extractArgument(result.group(1), Object.class).toString());
    }

    private class Arguments {
        private final Method method;
        private final Map<String, Object> argumentsByName;

        public Arguments(Method method, Object[] arguments) {
            var argumentsByName = new LinkedHashMap<String, Object>();
            var parameters = method.getParameters();
            for (var index = 0; index < parameters.length; index++) {
                argumentsByName.put(parameters[index].getName(), arguments[index]);
            }
            this.method = method;
            this.argumentsByName = argumentsByName;
        }

        @SuppressWarnings("unchecked")
        public <T> T extractArgument(String name, Class<T> type) {
            var value = argumentsByName.remove(name);
            if (value == null) {
                throw new IllegalArgumentException("%s.%s: required argument %s is missing".formatted(clientName, method.getName(), name));
            }
            if (!type.isAssignableFrom(value.getClass())) {
                throw new IllegalArgumentException("%s.%s: argument %s is expected to be of type %s".formatted(clientName, method.getName(), name, type));
            }
            return (T) value;
        }

        public Object getBody() {
            if (argumentsByName.size() > 1) {
                throw new IllegalArgumentException("%s.%s: too many arguments: %s".formatted(clientName, method.getName(), argumentsByName.keySet()));
            }
            if (argumentsByName.isEmpty()) {
                throw new IllegalArgumentException("%s.%s: body is missing".formatted(clientName, method.getName()));
            }
            return argumentsByName.values().iterator().next();
        }

        @Override
        public String toString() {
            return argumentsByName.toString();
        }
    }
}
