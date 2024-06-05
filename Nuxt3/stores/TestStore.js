import { defineStore } from 'pinia'

export const useTestStore = defineStore('testStore', {
  state: () => ({
      test: 'test pinia',
  }),
})

