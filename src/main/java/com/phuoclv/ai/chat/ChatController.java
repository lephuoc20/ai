package com.phuoclv.ai.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.redis.AdvancedRedisChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("ai/chat")
public class ChatController {
    private final ChatClient chatClient;
    private final RedisChatMemoryRepository chatMemoryRepository;
    private final VectorStore vectorStore;

    public ChatController(ChatClient chatClient,
                          RedisChatMemoryRepository chatMemoryRepository,
                          VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.chatMemoryRepository = chatMemoryRepository;
        this.vectorStore = vectorStore;
    }

    @GetMapping
    public String chat(@RequestParam(name = "message") String message,
                       @RequestParam(name = "conversationId") String conversationId) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
        return this.chatClient.prompt(message)
                .user("1")
                .advisors(a -> a.advisors(
                                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                                QuestionAnswerAdvisor.builder(vectorStore).build())
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    @GetMapping("/history")
    public List<AdvancedRedisChatMemoryRepository.MessageWithConversation> getHistory(@RequestParam(name = "userId") int userId) {
        return chatMemoryRepository.findByType(MessageType.USER, userId);
    }
}
