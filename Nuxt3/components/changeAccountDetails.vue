<template>
  <div>
    <v-form @submit.prevent="handleUpdate" v-model="valid">
      <v-text-field v-model="userDetails.firstName" label="Imię" variant="outlined" :rules="inputRules" />
      <v-text-field v-model="userDetails.lastName" label="Nazwisko" variant="outlined" :rules="inputRules" />
      <v-tooltip text="Nie ma możliwości zaktualizowania adresu email." location="bottom">
        <template v-slot:activator="{ props }">
          <v-text-field v-bind="props" v-model="userDetails.email" label="Email" readonly variant="outlined" />
        </template>
      </v-tooltip>

      <v-date-input v-model="userDetails.birthDate" label="Data urodzenia" prepend-icon="" variant="outlined" :max="today" :rules="inputRules" readonly/>
      <v-tooltip text="Nie ma możliwości zaktualizowania numeru pesel." location="bottom">
        <template v-slot:activator="{ props }">
          <v-text-field v-bind="props" v-model="userDetails.pesel" label="Pesel" readonly variant="outlined" />
        </template>
      </v-tooltip>
      <div class="text-center">
        <v-btn type="submit" color="green" :disabled="!valid">Zapisz zmiany</v-btn>
      </div>
    </v-form>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useDate } from 'vuetify'
import { useAuthStore } from "~/stores/AuthStore";
import { useSnack } from '@/composables/useSnack';

const { snackbarSuccess, snackbarError } = useSnack();
const authStore = useAuthStore();
const date = useDate()

const valid = ref(false);

const today = date.parseISO(new Date().toISOString().substr(0, 10));
const userDetails = reactive({
  firstName: authStore.user.firstname,
  lastName: authStore.user.lastname,
  email: authStore.user.email,
  birthDate: new Date(authStore.user.dob),
  pesel: authStore.user.pesel,
});

const inputRules = [
  (v) => !!v || 'Pole jest wymagane',
]

watch(() => userDetails.firstName, (newFirstName) => {
  userDetails.firstName = newFirstName.charAt(0).toUpperCase() + newFirstName.slice(1);
})

watch(() => userDetails.lastName, (newLastName) => {
  userDetails.lastName = newLastName.charAt(0).toUpperCase() + newLastName.slice(1);
})

const handleUpdate = async () => {
  const parsedDate = new Date(userDetails.birthDate);
  const year = parsedDate.getFullYear();
  const month = parsedDate.getMonth();
  const day = parsedDate.getDate();
  userDetails.birthDate = new Date(year, month, day, 12, 0, 0, 0);
  await authStore.updateDetails(userDetails)
  .then(() => {
    snackbarSuccess('Dane zostały zaktualizowane.')
  }).catch(() => {
    snackbarError('Wystąpił błąd podczas aktualizacji danych. Spróbuj ponownie.')
  });
}
</script>

<style scoped></style>