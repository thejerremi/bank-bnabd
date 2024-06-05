<template>
  <div class="mt-6">  
    <v-form @submit.prevent="handleLogin">
      <v-text-field v-model="loginData.email" class="mx-auto" label="Email" variant="outlined" type="email" :rules="emailRules"/>
      <v-text-field v-model="loginData.password" class="mx-auto" label="Hasło" variant="outlined" type="password" :rules="passwordRules"/>
      <div class="text-center">
        <v-btn type="submit">Zaloguj</v-btn>
      </div>
    </v-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import { useAuthStore } from '~/stores/AuthStore';
import { useSnack } from '@/composables/useSnack';

const { snackbarSuccess, snackbarError } = useSnack();
const authStore = useAuthStore();
const emailRules = [
  (v) => !!v || 'Email jest wymagany',
  (v) => /.+@.+\..+/.test(v) || 'Email musi być poprawny',
]
const passwordRules = [
  (v) => !!v || 'Hasło jest wymagane',
]

const loginData = reactive({
  email: '',
  password: ''
})

const handleLogin = async () => {
  await authStore.loginUser({
    email: loginData.email,
    password: loginData.password
  }).catch(() => {
    snackbarError('Nie udało się zalogować. Sprawdź dane i spróbuj ponownie.')
  })
}
</script>

<style scoped>
.v-text-field{
  max-width: 66%;
}
</style>