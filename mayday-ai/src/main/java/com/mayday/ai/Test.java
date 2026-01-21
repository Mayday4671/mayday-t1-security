package com.mayday.ai;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;


public class Test
{
    public static void main(String[] args)
    {
        // ✅ 建议：后面统一从 DB / 配置中心拿
        String apiKey = "sk-XXXXXX";
        ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(apiKey)
            .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
            .modelName("qwen-plus")
            .build();

        SystemMessage systemMessage = SystemMessage.from("你是JAVA专家");
        UserMessage userMessage = UserMessage.from("你好");

        System.out.println(model.chat(systemMessage, userMessage).aiMessage().text());
    }
}
