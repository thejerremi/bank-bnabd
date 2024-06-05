<template>
  <div>
    <v-form @submit.prevent="handleChangePassword" v-model="valid">
      <v-text-field
        :type="showPassword"
        label="Stare hasło"
        variant="outlined"
        v-model="changePasswordForm.currentPassword"
        :rules="[(v) => !!v || 'To pole jest wymagane']"
      ></v-text-field>
      <v-text-field
        :type="showPassword"
        label="Nowe hasło"
        variant="outlined"
        v-model="changePasswordForm.newPassword"
        :rules="[(v) => !!v || 'To pole jest wymagane']"
      ></v-text-field>
      <v-text-field
        :type="showPassword"
        label="Potwórz nowe hasło"
        variant="outlined"
        v-model="changePasswordForm.confirmationPassword"
        :rules="[
          (v) => !!v || 'To pole jest wymagane',
          (v) => v === changePasswordForm.newPassword || 'Hasła nie są takie same',
        ]"
      ></v-text-field>
      <div class="text-center">
        <v-btn 
          @click="showPassword = `text`" 
          v-if="showPassword === `password`"
          prepend-icon="mdi-eye"
          class="mr-4"
        >
        Pokaż hasła
        </v-btn>
      <v-btn @click="showPassword = `password`" v-else prepend-icon="mdi-eye-off">Ukryj hasła</v-btn>
      <v-btn type="submit" color="green" :disabled="!valid">Zmień hasło</v-btn>
    </div>
    </v-form>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useAuthStore } from "~/stores/AuthStore";
import { useSnack } from '@/composables/useSnack';

const { snackbarSuccess, snackbarError } = useSnack();
const authStore = useAuthStore();
const showPassword = ref("password");
const valid = ref(false);
const changePasswordForm = reactive({
  currentPassword: "",
  newPassword: "",
  confirmationPassword: "",
});
const handleChangePassword = async () => {
  await authStore.changePassword(changePasswordForm)
  .then(() => {
    snackbarSuccess('Hasło zmienione pomyślnie!')
  }).catch(() => {
    snackbarError('Nie udało się zmienić hasła. Sprawdź poprawność danych i spróbuj ponownie.')
  });
};
</script>

<style scoped></style>