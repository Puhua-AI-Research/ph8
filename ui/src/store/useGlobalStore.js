import { create } from "zustand";
import { immer } from 'zustand/middleware/immer'
import { getUserInfoApi } from "@/request/user";
import { ACCESS_TOKEN } from "@/constant";

export const useGlobalStore = create(
  immer((set, get) => ({
    showCopyRight: false,
    setShowCopyRight: (show) => set({ showCopyRight: show }),
    profile: null,
    setProfile: (profile) => set({ profile }),
    fetchProfile: async ({ onSuccess, onFail } = {}) => {
      const { setProfile } = get()
      const res = await getUserInfoApi();

      if (!res || res.code !== 0 || !res?.data) {
        localStorage.removeItem(ACCESS_TOKEN)
        setProfile(null)
        onFail?.(res)
        return
      }

      setProfile(res?.data)
      onSuccess?.(res)
    },
    signOut: () => {
      const { setProfile } = get()
      localStorage.removeItem(ACCESS_TOKEN)
      setProfile(null)
      location.href = location.origin
    },

    // register, login, gzhScanLogin, gzhBindMobile
    loginType: "",
    setLoginType: (loginType) => set({ loginType }),

    ticket: null,
    setTicket: (ticket) => set({ ticket }),

    showSidebarDrawer: false,
    setShowSidebarDrawer: (show) => set({ showSidebarDrawer: show }),

    showDeleteUserModal: false,
    setShowDeleteUserModal: (show) => set({ showDeleteUserModal: show }),

    collapseSidebar: false,
    setCollapseSidebar: (collapse) => set({ collapseSidebar: collapse })
  }))
)