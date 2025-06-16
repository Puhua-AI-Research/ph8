import { create } from 'zustand'
import { immer } from 'zustand/middleware/immer'

export const useGenerateVideoStore = create(
  immer((set, get) => ({
    modelParams: {
      model: '',
      prompt: '',
      seed: 1234,
      width: 720,
      height: 480,
      num_frames: 8,
    },
    updateParams: (params) => set((state) => { Object.assign(state.modelParams, params) }),

    respVideos: [],
    setRespVideos: (videos) => set((state) => {
      state.respVideos = videos
    }),

    processing: false,
    setProcessing: (processing) => set((state) => {
      state.processing = processing
    }),
  }))
)