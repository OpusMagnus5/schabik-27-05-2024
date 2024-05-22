package pl.schabik.application.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class SynchronousCommandHandlerExecutor implements CommandHandlerExecutor {
    private static final Logger log = LoggerFactory.getLogger(SynchronousCommandHandlerExecutor.class);
    private final Map<Class<? extends Command>, CommandHandler> handlerMap;

    public SynchronousCommandHandlerExecutor(List<CommandHandler> commandHandlers) {
        if (commandHandlers == null || commandHandlers.isEmpty()) {
            log.warn("Command handlers list is null or empty.");
            this.handlerMap = new HashMap<>();
        } else {
            this.handlerMap = commandHandlers.stream()
                    .peek(handler -> log.info("Adding support for command with class: {}", handler.handlingCommandClass().getSimpleName()))
                    .collect(Collectors.toMap(CommandHandler::handlingCommandClass, Function.identity()));
        }
    }

    @Override
    public void execute(Command command) {
        var handler = handlerMap.get(command.getClass());
        if (handler == null) {
            log.error("No handler found for command class: {}", command.getClass().getSimpleName());
            throw new IllegalStateException("No command handler registered for " + command.getClass().getName());
        }

        log.info("Executing command with class: {}", command.getClass().getSimpleName());
        var stopWatch = new StopWatch();
        stopWatch.start();
        try {
            handler.handle(command);
            log.info("Command successfully executed.");
        } catch (RuntimeException e) {
            log.error("Failed to execute command due to exception: ", e);
            throw e;
        } finally {
            stopWatch.stop();
            log.info("Command executed in {}ms", stopWatch.getTotalTimeMillis());
        }
    }
}


