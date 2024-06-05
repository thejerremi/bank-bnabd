import { defineStore } from 'pinia'
import transactionService from '@/services/transactionService';
import { useAuthStore } from './AuthStore';

export const useTransactionStore = defineStore('transactionStore',{
  state: () => ({ 
    lastFiveTransactions: [],
    paginatedTransactions: [],
    totalPages: 0,
    currentPage: 0,
  }),
  actions: {
    reset(){
      this.lastFiveTransactions = [];
      this.paginatedTransactions = [];
      this.totalPages = 0;
      this.currentPage = 0;
    },
    async fetchLastTransactions() {
      const response = await transactionService.fetchLastTransactions();
      this.lastFiveTransactions = response.data;
    },
    async fetchPaginatedTransactions(page) {
      if (this.paginatedTransactions[page]) {
        return;
      }

      try {
        const response = await transactionService.fetchPaginatedTransactions(page);
        const data = response.data;
        
        // Store paginated transactions
        this.paginatedTransactions[page] = data.content;
        
        // Update pagination metadata
        this.totalPages = data.totalPages;
        this.currentPage = page;
      } catch (error) {
        console.error('Failed to fetch paginated transactions:', error);
      }
    },
    async atmDeposit(amount) {
      try {
        await transactionService.atmDeposit(amount);
        const authStore = useAuthStore();
        authStore.user.balance += amount;
        this.fetchLastTransactions();
      }catch (error) {
        throw error;
      }      
    },
    async sendTransfer(transferDetails) {
      try {
        await transactionService.sendTransfer(transferDetails);
        const authStore = useAuthStore();
        authStore.user.balance -= transferDetails.amount;
        this.fetchLastTransactions();
        navigateTo('/user')
      }catch (error) {
        throw error;
      }      
    },
  }
})
