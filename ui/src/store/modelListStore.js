import { create } from 'zustand'
import { immer } from 'zustand/middleware/immer'

export const useModelListStore = create(
  immer((set, get) => ({
    modelList: {},
    modelDetailId: {},
    updateModelList: (params) => set((state) => { Object.assign(state.modelList, params) }),
    updateModelDetailId: (params) => set((state) => { Object.assign(state.modelDetailId, params) }),
  }))
)