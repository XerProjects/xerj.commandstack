package io.github.xerprojects.xerj.commandstack.providers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import io.github.xerprojects.xerj.commandstack.CommandHandler;
import io.github.xerprojects.xerj.commandstack.entities.TestCommand;
import io.github.xerprojects.xerj.commandstack.entities.TestCommandHandler;
import io.github.xerprojects.xerj.commandstack.exceptions.DuplicateCommandHandlerFoundException;
import io.github.xerprojects.xerj.commandstack.providers.CompositeCommandHandlerProvider;
import io.github.xerprojects.xerj.commandstack.providers.RegistryCommandHandlerProvider;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CompositeCommandHandlerProviderTests {
    @Nested
	public class Constructor {
		@Test
		public void shouldThrowWhenProviderListIsNull() {
			assertThrows(IllegalArgumentException.class, () -> {
				new CompositeCommandHandlerProvider(null);
			});
		}

		@Test
		public void shouldThrowWhenProviderListIsEmpty() {
			assertThrows(IllegalArgumentException.class, () -> {
				new CompositeCommandHandlerProvider(List.of());
			});
		}
	}
	
	@Nested
	public class GetCommandHandlerForMethod {
		@Test
		public void shouldProvideRegisteredCommandHandler() {
			CommandHandler<TestCommand> testCommandHandler = new TestCommandHandler();
			
			var provider1 = new RegistryCommandHandlerProvider(config ->
                config.registerCommandHandler(TestCommand.class, () -> testCommandHandler));
                
            var compositeProvider = new CompositeCommandHandlerProvider(List.of(provider1));
			
			Optional<CommandHandler<TestCommand>> resolvedHandler = compositeProvider.getCommandHandlerFor(TestCommand.class);			
			
			CommandHandler<TestCommand> instance = resolvedHandler.get();
			
			assertNotNull(instance);
			assertSame(testCommandHandler, instance);
        }
        
        @Test
		public void shouldThrowWhenCommandHandlerIsRegisteredInMultipleProviders() {
            
            assertThrows(DuplicateCommandHandlerFoundException.class, () -> {
                var provider1 = new RegistryCommandHandlerProvider(config ->
                    config.registerCommandHandler(TestCommand.class, TestCommandHandler::new));
                
                var provider2 = new RegistryCommandHandlerProvider(config ->
                    config.registerCommandHandler(TestCommand.class, TestCommandHandler::new));
                    
                var compositeProvider = new CompositeCommandHandlerProvider(List.of(provider1, provider2));
                
                compositeProvider.getCommandHandlerFor(TestCommand.class);
            });
		}

		@Test
		public void shouldThrowWhenCommandClassArgumentIsNull() {
			assertThrows(IllegalArgumentException.class, () -> {
                var provider = new RegistryCommandHandlerProvider(config ->
					config.registerCommandHandler(TestCommand.class, TestCommandHandler::new));
					
				var compositeProvider = new CompositeCommandHandlerProvider(List.of(provider));
				
				compositeProvider.getCommandHandlerFor(null);
			});
		}
	}
}