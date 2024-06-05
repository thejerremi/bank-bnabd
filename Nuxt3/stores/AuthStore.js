import { defineStore } from 'pinia'
import authService from '@/services/authService';
import { useTransactionStore } from "./TransactionStore";

export const useAuthStore = defineStore('authStore', {
  state: () => ({
    user: null,
  }),
  getters: {
    userInitials() {
      if (this.user) {
        return this.user.firstname.charAt(0) + this.user.lastname.charAt(0);
      }
    }
  },
  actions: {
    async registerUser(user) {
      const response = await authService.registerUser(user);
      localStorage.setItem('token', response.data.access_token);
      this.user = response.data.user;
    },
    async getUserByToken() {
      const response = await authService.getUserByToken();
      this.user = response.data;    
    },
    async loginUser(userDetails) {
      const response = await authService.loginUser(userDetails);
      localStorage.setItem('token', response.data.access_token);
      this.user = response.data.user;
    },
    async logoutUser() {
      await authService.logoutUser();
      localStorage.removeItem('token');
      const transactionStore = useTransactionStore();
      transactionStore.reset();
      this.user = null;
    },
    async deleteUser(password){
      await authService.deleteUser(password);
      localStorage.removeItem('token');
      const transactionStore = useTransactionStore();
      transactionStore.reset();
      this.user = null;
    },
    async changePassword(passwords){
      await authService.changePassword(passwords);
    },
    async updateDetails(userDetails){
      const updatedDetails = {
        firstname: userDetails.firstName,
        lastname: userDetails.lastName,
        dob: userDetails.birthDate
      }
      await authService.updateDetails(updatedDetails);
    }
  }
})

