package org.dice.ida;

import org.dice.ida.controller.ChatController;

import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IdaApplicationTests {

	@Autowired
	private ChatController chatController;

    @Test
    void contextLoads() {
		assertThat(chatController).isNotNull();
    }

}
