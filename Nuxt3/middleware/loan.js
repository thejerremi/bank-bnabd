import { useAuthStore } from "~/stores/AuthStore";

export default defineNuxtRouteMiddleware((to, from) => {
  const authStore = useAuthStore();
  
  if (authStore.user.hasLoan) {
    if (to.path === '/user/loan') {
      return navigateTo('/user/loan-details'); // Redirect to loan details if the user already has a loan
    }
  } else {
    if (to.path === '/user/loan-details') {
      return navigateTo('/user/loan'); // Redirect to loan if the user does not have a loan
    }
  }
});
