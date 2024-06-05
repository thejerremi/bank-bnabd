<template>
  <v-app-bar>
    <v-btn icon @click="goBack">
      <v-icon>mdi-arrow-left</v-icon>
    </v-btn>
    <v-btn icon @click="goForward">
      <v-icon>mdi-arrow-right</v-icon>
    </v-btn>

    <v-app-bar-title @click="navigateTo('/')">TwójBank</v-app-bar-title>

    <v-spacer></v-spacer>
    <v-btn v-if="authStore.user && authStore.user.role===`ADMIN`" variant="text" @click="navigateTo('/admin/loan-center')">Zarządzaj pożyczkami</v-btn>

    <v-menu open-on-hover v-if="authStore.user">
      <template v-slot:activator="{ props }">
        <v-avatar color="black" class="mr-6" v-bind="props">
          <span class="text-h5">{{ authStore.userInitials }}</span>
        </v-avatar>
      </template>
      <v-list>
        <v-list-item @click="navigateTo('/user/account')">
          <v-list-item-title>Ustawienia konta</v-list-item-title>
          <template v-slot:prepend>
            <v-icon icon="mdi-account-cog" />
          </template>
        </v-list-item>
        <v-list-item @click="handleLogout()">
          <v-list-item-title>Wyloguj</v-list-item-title>
          <template v-slot:prepend>
            <v-icon icon="mdi-logout" />
          </template>
        </v-list-item>
      </v-list>
    </v-menu>
    <v-btn v-else @click="navigateTo('/auth')">Zaloguj się</v-btn>
  </v-app-bar>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '~/stores/AuthStore';
const authStore = useAuthStore();
import { useRouter } from 'vue-router';
import { useSnack } from '@/composables/useSnack';
const { snackbarSuccess, snackbarError } = useSnack();

const router = useRouter();

const goBack = () => {
  router.back();
}

const goForward = () => {
  // Vue Router does not have a built-in 'forward' method, but you can use history.forward
  router.forward();
}

const handleLogout = () => {
  authStore.logoutUser()
  .then(() => {
    snackbarSuccess('Wylogowano pomyślnie.')
  }).catch(() => {
    snackbarError('Wystąpił błąd podczas wylogowywania.')
  })
}

</script>

<style scoped>
.v-app-bar {
  display: flex;
  align-items: center;
}
</style>
