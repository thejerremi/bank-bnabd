<template>
  <v-app>
    <headerComponent />
    <v-main>
      <slot />
      <NuxtSnackbar />
    </v-main>
  </v-app>
</template>

<script setup>
import { useAuthStore } from '~/stores/AuthStore';
import { useTransactionStore } from '~/stores/TransactionStore';
import { useSnack } from '@/composables/useSnack';
import { useDate } from 'vuetify'

const { snackbarSuccess, snackbarError } = useSnack();
const authStore = useAuthStore();
const transactionStore = useTransactionStore();
const date = useDate()

onMounted(async () => {
  if(localStorage.getItem('token') !== null) {
    await authStore.getUserByToken()
    .then(() => {
      snackbarSuccess('Zalogowano pomyślnie.')
    }).catch(() => {
      snackbarError('Twoja sesja wygasła. Zaloguj się ponownie.')
    })
  }
})
watch(() => authStore.user, (user) => {
  if (user === null) {
    navigateTo('/');
  }
});

watch(
  () => transactionStore.lastFiveTransactions,
  (newValue) => {

    newValue.forEach((transaction) => {
      transaction.type = transactionTypes[transaction.type];
      transaction.amount = `${transaction.amount} PLN`;
      transaction.createdAt = date.format(transaction.createdAt, 'fullDateTime24h')
    });
  },
);
const transactionTypes = {
  ATM_DEPOSIT: "Wpłatomat",
  DEPOSIT: "Depozyt",
  WITHDRAW: "Wypłata",
  USER_TRANSFER: "Wpływ od innego użytkownika",
  TRANSFER: "Przelew",
  LOAN: "Pożyczka",
  INTEREST: "Odsetki",
  MONTHLY_RATE: "Miesięczna rata",
  LOAN_PAYMENT: "Spłata pożyczki",
};
</script>

<style ></style>