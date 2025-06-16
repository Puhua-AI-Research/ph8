import { create } from 'zustand'
import { immer } from 'zustand/middleware/immer'

export const useSD15GenImageStore = create(
  immer((set, get) => ({
    modelParams: {
      model: '',
      seed: 1234,
      steps: 20,
      batch_size: 1,
      width: 512,
      height: 512,
      sampler_name: 'Euler',
      cfg: 7,
      denoising_strength: 0.7,
      scheduler: 'Normal',
      prompt: '',
      negative_prompt: '',
    },
    updateParams: (params) => set((state) => { Object.assign(state.modelParams, params) }),

    respImages: [],
    setRespImages: (images) => set((state) => {
      state.respImages = images
    }),

    processing: false,
    setProcessing: (processing) => set((state) => {
      state.processing = processing
    }),
  }))
)