<template>
  <v-app>    
    <slot />
    <NuxtSnackbar />
  </v-app>
</template>

<script setup>
import { watch } from 'vue';
import { useAuthStore } from '~/stores/AuthStore';
const authStore = useAuthStore();
watch(() => authStore.user, (user) => {
  if (user) {
    navigateTo('/user')
  }
})

</script>

<style scoped>

</style>