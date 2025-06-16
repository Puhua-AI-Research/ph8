import { create } from 'zustand'
import { immer } from 'zustand/middleware/immer'

export const useModelGalleryStore = create(
  immer((set, get) => ({
    modelList: [],
    setModelList: (list) => set((state) => { state.modelList = list }),

    selectModel: {},
    setSelectModel: (model) => set((state) => { state.selectModel = model }),
  }))
)