// middleware/auth.js
import { useAuthStore } from "~/stores/AuthStore";

export default defineNuxtRouteMiddleware((to, from) => {
  
    const authStore = useAuthStore();
    
    if (to.path.startsWith('/user') && !authStore.user) {
      return navigateTo('/');
    } else if (to.path.startsWith('/admin') && (!authStore.user || authStore.user.role !== 'ADMIN')) {
      return navigateTo('/');
    }
  });
  