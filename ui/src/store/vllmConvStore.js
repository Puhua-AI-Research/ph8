import { create } from 'zustand'
import { immer } from 'zustand/middleware/immer'

export const useVllmConvStore = create(
  immer((set, get) => ({
    modelParams: {
      model: '',
      temperature: 0.7,
      top_p: 1.0,
      max_tokens: 512,
      presence_penalty: 1.0,
      frequency_penalty: 1.0,

      systemPrompt: 'You are a helpful assistant',
      question: '',
    },
    updateParams: (params) => set((state) => { Object.assign(state.modelParams, params) }),
    historyConv: [],
    addHistoryConv: (content) => set((state) => {
      state.historyConv.push(
        {
          "role": "user",
          "content": content,
          "timestamp": new Date().toLocaleString(),
        }
      )

      state.historyConv.push(
        {
          "role": "assistant",
          "content": '',
          'isLoading': true,
        }
      )
    }),
    popHistoryConv: () => set((state) => {
      state.historyConv.splice(-2);
    }),
    updateAnswer: (answer) => set((state) => {
      state.historyConv[state.historyConv.length - 1].content = [{ type: 'text', text: answer }]
    }),
    updateThinkContent: (content) => set((state) => {
      state.historyConv[state.historyConv.length - 1].thinkContent = content
    }),
    finishAnswer: (answer) => set((state) => {
      state.historyConv[state.historyConv.length - 1].content = [{ type: 'text', text: answer }]
      state.historyConv[state.historyConv.length - 1].isLoading = false
      state.historyConv[state.historyConv.length - 1].timestamp = new Date().toLocaleString()
    }),
    hasSendImg: false,
    setHasSendImg: (hasSend) => set((state) => { state.hasSendImg = hasSend }),
    resetConv: () => set((state) => {
      state.historyConv = []
      state.hasSendImg = false
    }),
    processing: false,
    setProcessing: (processing) => set((state) => {
      state.processing = processing
    }),
  }))
)