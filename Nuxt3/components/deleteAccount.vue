<template>
  <v-form v-model="valid" @submit.prevent="deleteAccount">
  <div class="text-black font-weight-bold">
    Czy na pewno chcesz usunąć swoje konto?
  </div>
  <div class="text-black mb-4">Wpisz swoje hasło by potwierdzić:</div>
  <v-text-field
    type="password"
    label="Hasło"
    variant="outlined"
    v-model="password"
    :rules="[v => !!v || 'To pole jest wymagane']"
  ></v-text-field>
  <div class="font-italic mt-4">Uwaga! Tej operacji nie można cofnąć!</div>
  <v-btn type="submit" color="red" dark prepend-icon="mdi-delete" :disabled="!valid"> Usuń konto </v-btn>
</v-form>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '~/stores/AuthStore';
import { useSnack } from '@/composables/useSnack';

const { snackbarSuccess, snackbarError } = useSnack();
const authStore = useAuthStore();
const valid = ref(false);
const password = ref('');
const deleteAccount = async () => {
  await authStore.deleteUser(password.value)
  .then(() => {
    snackbarSuccess('Konto usunięte. Do zobaczenia!')
  }).catch(() => {
    snackbarError('Nie udało się usunąć konta. Sprawdź hasło i spróbuj ponownie.')
  })
}
</script>

<style scoped></style>