import { create } from "zustand";
import { immer } from 'zustand/middleware/immer'

export const useOrderStore = create(
  immer((set, get) => ({
    showOrderDialog: false,
    setShowOrderDialog: (show) => set({ showOrderDialog: show }),
  }))
)