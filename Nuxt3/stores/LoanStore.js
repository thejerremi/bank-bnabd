import { defineStore } from 'pinia'
import loanService from '@/services/loanService';
import { useTransactionStore } from './TransactionStore';

export const useLoanStore = defineStore('loanStore', {
  state: () => ({ 
    activeLoan: null,
    pendingLoans: []
  }),
  actions: {
    async fetchLoan(){
      const response = await loanService.fetchLoan();
      this.activeLoan = response.data;
    },
    async applyForLoan(data){
      await loanService.applyForLoan(data);
      await this.fetchLoan();
    },
    async payMonthlyRate(){
      const transactionStore = useTransactionStore();
      transactionStore.reset();
      await loanService.payMonthlyRate();
      await transactionStore.fetchLastTransactions();
    },
    async payLoanRepayment(amount){
      const transactionStore = useTransactionStore();
      transactionStore.reset();
      await loanService.payLoanRepayment(amount);
      await transactionStore.fetchLastTransactions();
    },
    async fetchPendingLoans(){
      this.pendingLoans = [];
      const response = await loanService.fetchPendingLoans();
      this.pendingLoans = response.data;
    },
    async rejectPendingLoan(id){
      await loanService.pendingLoanDecision("REJECT", id);
      await this.fetchPendingLoans();
    },
    async approvePendingLoan(id){
      await loanService.pendingLoanDecision("ACCEPT", id);
      await this.fetchPendingLoans();
    }
  }
})
