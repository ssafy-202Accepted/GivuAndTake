import { create } from 'zustand';

const useProfileStore = create((set) => ({
  profileImageUrl: '',
  sido: '', // 추가
  sigungu: '', // 추가
  setProfileImageUrl: (url) => set({ profileImageUrl: url }),
  setSido: (sido) => set({ sido }), // 추가
  setSigungu: (sigungu) => set({ sigungu }), // 추가
}));

export default useProfileStore;
