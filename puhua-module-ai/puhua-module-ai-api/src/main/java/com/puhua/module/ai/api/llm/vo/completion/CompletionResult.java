package com.puhua.module.ai.api.llm.vo.completion;

import lombok.Data;
import com.puhua.module.ai.api.llm.vo.Usage;

import java.util.List;

/**
 * An object containing a response from the completion api
 * <p>
 * https://beta.openai.com/docs/api-reference/completions/create
 */
@Data
public class CompletionResult {
    /**
     * A unique id assigned to this completion.
     */
    String id;

    /**
     * https://beta.openai.com/docs/api-reference/create-completion
     * The type of object returned, should be "text_completion"
     */
    String object;

    /**
     * The creation time in epoch seconds.
     */
    long created;

    /**
     * The GPT model used.
     */
    String model;

    /**
     * A list of generated completions.
     */
    List<CompletionChoice> choices;

    /**
     * The API usage for this request
     */
    Usage usage;
}
