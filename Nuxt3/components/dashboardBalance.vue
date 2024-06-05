<template>
  <div>
    <v-card class="ma-12" variant="outlined" elevation="16" min-height="30vh">
      <v-card-title>Saldo</v-card-title>
      <v-card-text v-if="user">
        <div class="ml-6">
          <div class="text-h3 mt-6">
            Dostępne środki: {{ user.balance.toFixed(2) }} PLN
            <v-btn style="opacity: 0" @click="atmDeposit(100)">+</v-btn>
          </div>
          <div class="mt-3">
            Twój numer bankowy:
            <v-btn variant="text" prepend-icon="mdi-content-copy">{{
              formatAccountNumber(user.accountNumber)
            }}</v-btn>
          </div>
        </div>
      </v-card-text>
      <v-card-text v-else>
        <v-skeleton-loader type="card"></v-skeleton-loader>
      </v-card-text>
      <v-card-actions v-if="user" class="actions-container">
        <v-btn variant="outlined" @click="navigateTo('/user/transfer')">Wykonaj przelew</v-btn>
        <v-btn variant="outlined" @click="navigateTo('/user/transaction-history')">Historia transakcji</v-btn>
        <v-btn variant="outlined" @click="navigateTo('/user/loan')" v-if="!user.hasLoan">Weź pożyczkę</v-btn>
        <v-btn variant="outlined" @click="navigateTo('/user/loan-details')" v-else>Twoja pożyczka</v-btn>
        <v-btn variant="outlined" @click="navigateTo('/user/account')">Ustawienia konta</v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script setup>
import { useAuthStore } from "~/stores/AuthStore";
import { useTransactionStore } from "~/stores/TransactionStore";
import { useSnack } from '@/composables/useSnack';

const { snackbarSuccess, snackbarError } = useSnack();
const authStore = useAuthStore();
const transactionStore = useTransactionStore();
const user = ref(authStore.user);
watch(
  () => authStore.user,
  (newValue) => {
      user.value = newValue;
  }
);

const atmDeposit = async (amount) => {
  await transactionStore.atmDeposit(amount)
  .then(() => {
    snackbarSuccess('Pomyślnie wpłacono ' + amount + ' PLN we wpłatomacie.');
  })
  .catch(() => {
    snackbarError('Wystąpił błąd podczas wpłacania środków we wpłatomacie.');
  });
}

const formatAccountNumber = (accountNumber) => {
  return accountNumber.replace(/(\d{2})(\d{4})(\d{4})(\d{4})(\d{4})(\d{4})(\d{4})/, '$1 $2 $3 $4 $5 $6 $7');
};

</script>

<style scoped>
.actions-container {
  justify-content: center;
  display: flex;
  position: absolute;
  width: 100%;
}
</style>