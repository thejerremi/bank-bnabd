<template>
  <div>
    <v-card class="ma-12 mt-n6" variant="outlined" elevation="16" height="50vh">
      <v-card-title>Ostatnie transakcje</v-card-title>
      <v-card-text v-if="transactionStore.lastFiveTransactions !== undefined">
        <v-data-table
        v-model:sort-by="sortBy"
        :headers="headers"
        :items="userTransactions"
        no-data-text="Wykonaj swoją pierwszą transakcję!"
        hide-default-footer
      >
      </v-data-table>
      <div class="d-flex align-center flex-column mt-6"><v-btn variant="outlined" @click="navigateTo('/user/transaction-history')">Sprawdź pełną historię</v-btn></div>
      </v-card-text>
      <v-card-text v-else>
        <v-skeleton-loader type="card"></v-skeleton-loader>
      </v-card-text>
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

const userTransactions = computed(() => transactionStore.lastFiveTransactions);
onMounted(async () => {
  if(transactionStore.lastFiveTransactions.length === 0){
    await transactionStore.fetchLastTransactions();
  }
});


const headers = [
  { title: "Typ transakcji", value: "type" },
  { title: "Kwota", value: "amount" },
  { title: "Data operacji", value: "createdAt" },
];
const sortBy = [{ key: "createdAt", order: "desc" }];
</script>

<style scoped>
</style>