import create from 'zustand';

export const useFundingStore = create((set) => ({
  fundingTypes: [],
  setFundingTypes: (types) => set({ fundingTypes: types }),
}));
