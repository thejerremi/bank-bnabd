<template>
  <v-sheet class="d-flex flex-wrap h-100">
    <v-card class="flex-1-0 ma-16 pa-2" :elevation="24" rounded>
      <v-card-title>
        <div class="text-h3 text-center font-weight-bold mb-16">Wykonaj przelew</div>
    </v-card-title>
    <v-card-text>
      <v-text-field variant="outlined" label="Z rachunku" v-model="accountInfo" readonly />
      <v-form @submit.prevent="handleTransfer" ref="transfer" v-model="valid">
        <v-text-field clearable variant="outlined" label="Na rachunek" v-model="transferDetails.accountNumberDest" hint="00 0000 0000 0000 0000 0000 0000" :rules="accountNumberRules"/>
        <v-text-field clearable variant="outlined" label="Kwota" v-model="transferDetails.amount" :rules="amountRules"/>
        <div class="text-center">
          <v-btn class="mr-4" color="red" @click="$refs.transfer.reset()">Wyczyść</v-btn>
          <v-btn type="submit" color="green" :disabled="!valid">Wykonaj przelew</v-btn>
        </div>
      </v-form>
    </v-card-text>
    </v-card>
  </v-sheet>
</template>

<script setup>
import { useAuthStore } from "~/stores/AuthStore";
import { useTransactionStore } from "~/stores/TransactionStore";
import { useSnack } from '@/composables/useSnack';

const { snackbarSuccess, snackbarError } = useSnack();
const authStore = useAuthStore();
const transactionStore = useTransactionStore();

const valid = ref(false);
const user = authStore.user;
const formatAccountNumber = (accountNumber) => {
  return accountNumber.replace(/(\d{2})(\d{4})(\d{4})(\d{4})(\d{4})(\d{4})(\d{4})/, '$1 $2 $3 $4 $5 $6 $7');
};

const accountInfo = computed(() => {
  return user ? `${formatAccountNumber(user.accountNumber)} (${user.balance} PLN)` : '';
});
const accountNumberRules = [
v => !!v || 'Pole nie może być puste',
    v => v.replace(/\s/g, '').length === 26 || 'Numer rachunku musi mieć 26 znaków',
    v => /^\d+$/.test(v.replace(/\s/g, '')) || 'Numer rachunku musi składać się z samych cyfr',
    v => v.replace(/\s/g, '') !== user.accountNumber || 'Nie możesz wykonać przelewu na własne konto'
];
const amountRules = [
    v => !!v || 'Pole nie może być puste',
    v => /^\d+(\.\d{1,2})?$/.test(v) || 'Nieprawidłowa kwota',
    v => v < user.balance || 'Nie masz wystarczających środków'
]
const transferDetails = reactive({
  accountNumberDest: '',
  amount: null,
});
const handleTransfer = async () => {
  await transactionStore.sendTransfer(transferDetails)
  .then(() => {
    snackbarSuccess('Pomyślnie wykonano przelew.');
  })
  .catch(() => {
    snackbarError('Wystąpił błąd podczas wykonywania przelewu.');
  });
} 
</script>

<style scoped>

</style>