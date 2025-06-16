import { create } from 'zustand'
import { immer } from 'zustand/middleware/immer'

export const useTextConvStore = create(
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
    addHistoryConv: (question) => set((state) => {
      state.historyConv.push(
        {
          "role": "user",
          "content": question,
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
      state.historyConv[state.historyConv.length - 1].content = answer
    }),
    updateThinkContent: (content) => set((state) => {
      state.historyConv[state.historyConv.length - 1].thinkContent = content
    }),
    finishAnswer: (answer) => set((state) => {
      state.historyConv[state.historyConv.length - 1].content = answer
      state.historyConv[state.historyConv.length - 1].isLoading = false
      state.historyConv[state.historyConv.length - 1].timestamp = new Date().toLocaleString()
    }),
    resetConv: () => set((state) => { state.historyConv = [] }),
    processing: false,
    setProcessing: (processing) => set((state) => {
      state.processing = processing
    }),
  }))
)