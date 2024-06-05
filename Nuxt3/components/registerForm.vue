<template>
  <div>
    <v-stepper
    model-value="xd"
      prev-text="cofnij"
      next-text="dalej"
      :items="['Dane osobowe', 'Dane logowania', '...i gotowe!']"
    >
      <template v-slot:[`item.1`]>
        <v-form v-model="firstStepValid">
        <v-card title="Dane osobowe" flat>
          <v-card-text>
            <v-text-field v-model="personalData.name" label="Imię" variant="outlined" :rules="nameRules" />
            <v-text-field v-model="personalData.surname" label="Nazwisko" variant="outlined" :rules="nameRules "/>
            <v-text-field v-model="personalData.pesel" label="PESEL" variant="outlined" :rules="peselRules"/>
            <v-date-input
              v-model="personalData.birthDate"
              disabled
              label="Data urodzenia"
              prepend-icon=""
              variant="outlined"
              placeholder="Data urodzenia zostanie wpisana automatycznie po wpisaniu PESEL"
              
              :active="true"
            ></v-date-input>
          </v-card-text>
        </v-card>
      </v-form>
      </template>

      <template v-slot:[`item.2`]>
        <v-form v-model="secondStepValid">
        <v-card title="Dane logowania" flat>
          <v-card-text>
            <v-text-field v-model="loginData.email" label="Email" variant="outlined" :rules="emailRules"/>
            <v-text-field v-model="loginData.password" label="Hasło" variant="outlined" :rules="passwordRules" type="password"/>
            <v-text-field v-model="loginData.passwordRepeat" label="Powtórz hasło" variant="outlined" :rules="passwordRepeatRules" type="password"/>
          </v-card-text>
        </v-card>
      </v-form>
      </template>

      <template v-slot:[`item.3`]>
        <v-card title="Podsumowanie" flat>
          <v-card-text>
            <div>Imię: {{ personalData.name }}</div>
          <div>Nazwisko: {{ personalData.surname }}</div>
          <div>PESEL: {{ personalData.pesel }}</div>
          <div>Data urodzenia: {{ date.format(personalData.birthDate, 'keyboardDate') }}</div>
          <div>Email: {{ loginData.email }}</div>
          <div>
                Hasło:
                <span v-if="showPassword" class="mr-2">{{ loginData.password }}</span>
                <v-icon @click="togglePasswordVisibility">
                  {{ showPassword ? 'mdi-eye-off' : 'mdi-eye' }}
                </v-icon>
              </div>
          

            <div align="center">
              <div class="text-h6 font-weight-bold text-red" v-if="!firstStepValid || !secondStepValid">Popraw dane by móc ukończyć rejestracje!</div>
            <v-btn color="success" variant="tonal" :disabled="!firstStepValid || !secondStepValid" @click="handleRegister()">Ukończ rejestrację</v-btn>
            </div>
          </v-card-text>
        </v-card>
      </template>
    </v-stepper>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useDate } from 'vuetify'
import { useAuthStore } from '~/stores/AuthStore';
import { usePesel } from '@/composables/usePesel';
import { useSnack } from '@/composables/useSnack';

const { snackbarSuccess, snackbarError } = useSnack();

const date = useDate();
const authStore = useAuthStore();

const { pesel, peselRules, getBirthDateFromPesel } = usePesel();

const firstStepValid = ref(false);
const secondStepValid = ref(false);

const personalData = reactive({
  name: '',
  surname: '',
  pesel: '',
  birthDate: null
})

const loginData = reactive({
  email: '',
  password: '',
  passwordRepeat: ''
})

const nameRules = [
  (v) => !!v || 'Imię/Nazwisko jest wymagane',
  (v) => (v && v.length >= 2) || 'Imię musi mieć co najmniej 2 znaki',
]

const passwordRules = [
  (v) => !!v || 'Hasło jest wymagane',
  (v) => (v && v.length >= 8) || 'Hasło musi mieć co najmniej 8 znaków',
]
const passwordRepeatRules = [
  (v) => !!v || 'Powtórz hasło',
  (v) => v === loginData.password || 'Hasła muszą być takie same'
]
const emailRules = [
  (v) => !!v || 'Email jest wymagany',
  (v) => /.+@.+\..+/.test(v) || 'Email musi być poprawny',
]

watch(() => personalData.pesel, (newPesel) => {
  pesel.value = newPesel;
  personalData.birthDate = getBirthDateFromPesel(newPesel);
})
watch(() => personalData.name, (newName) => {
  personalData.name = newName.charAt(0).toUpperCase() + newName.slice(1);
})

watch(() => personalData.surname, (newSurname) => {
  personalData.surname = newSurname.charAt(0).toUpperCase() + newSurname.slice(1);
})
const showPassword = ref(false);

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

const handleRegister = async () => {
  await authStore.registerUser({
    firstname: personalData.name,
    lastname: personalData.surname,
    pesel: personalData.pesel,
    dob: personalData.birthDate,
    email: loginData.email,
    password: loginData.password
  })
  .then(() => {
    snackbarSuccess('Rejestracja zakończona pomyślnie!')
  }).catch(() => {
    snackbarError('Rejestracja nie powiodła się. Spróbuj ponownie!')
  })
}
</script>

<style scoped>
</style>