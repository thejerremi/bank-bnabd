<template>
  <v-sheet>
    <v-card class="flex-1-0 ma-16 pa-2" :elevation="24" rounded>
      <v-card-title>
        <div class="text-h3 text-center font-weight-bold">
          Szczegółowa historia transakcji
        </div>
      </v-card-title>
      <v-card-text v-if="transactionStore.paginatedTransactions.length !== 0">
        <div v-if="
          transactionStore.paginatedTransactions[currentPage - 1] ===
          undefined
        ">
          <v-skeleton-loader type="list-item, list-item, list-item"></v-skeleton-loader>
        </div>
        <div v-if="transactionStore.paginatedTransactions[0].length === 0">
          <div class="text-center text-h4 mt-4">
            Brak transakcji do wyświetlenia.
          </div>
        </div>
        <v-expansion-panels variant="accordion" v-model="openedPanels" multiple v-else>
          <v-expansion-panel v-for="transaction in currentPaginatedTransactions" :key="transaction.id">
            <v-expansion-panel-title>
              {{ transaction.type }} - {{ transaction.amount }} -
              {{ transaction.createdAt }}
            </v-expansion-panel-title>
            <v-expansion-panel-text>
              <div>
                <p>Typ: {{ transaction.type }}</p>
                <p>Kwota: {{ transaction.amount }}</p>
                <p>Data: {{ transaction.createdAt }}</p>

                <v-tooltip text="Podajemy imię i nazwisko odbiorcy tylko jeśli jest on również klientem naszego banku."
                  location="bottom">
                  <template v-slot:activator="{ props }">
                    <p v-bind="props" v-if="transaction.recipient">
                      Odbiorca: {{ transaction.recipient }}
                    </p>
                  </template>
                </v-tooltip>
                <p>Opis: {{ transaction.description }}</p>
              </div>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
        <div class="d-flex justify-center mt-4">
          <v-pagination v-model="currentPage" :length="transactionStore.totalPages" :total-visible="5"
            @update:modelValue="onPageChange"></v-pagination>
        </div>
      </v-card-text>
      <v-card-text v-else>
        <v-skeleton-loader type="list-item, list-item, list-item"></v-skeleton-loader>
      </v-card-text>
    </v-card>
  </v-sheet>
</template>

<script setup>
import { useTransactionStore } from "~/stores/TransactionStore";
import { useDate } from "vuetify";
const date = useDate();
const transactionStore = useTransactionStore();

const currentPage = ref(1);
const currentPaginatedTransactions = ref([]);
const openedPanels = ref([]);

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

watch(
  () => transactionStore.paginatedTransactions[currentPage.value - 1],
  (newValue) => {
    if (newValue) {
      const formattedTransactions = newValue.map((transaction) => {
        return {
          ...transaction,
          type: transactionTypes[transaction.type] || transaction.type,
          amount: `${transaction.amount} zł`,
          createdAt: date.format(transaction.createdAt, "fullDateTime24h"),
        };
      });
      currentPaginatedTransactions.value = formattedTransactions;
      openedPanels.value = [];
    }
  },
  { immediate: true } // Ensures the watcher runs immediately on component mount
);

const onPageChange = async (page) => {
  currentPage.value = page;
  await transactionStore.fetchPaginatedTransactions(page - 1);
};

onMounted(() => {
  transactionStore.paginatedTransactions = [];
  transactionStore.fetchPaginatedTransactions(0);
});
onBeforeUnmount(() => { });
</script>

<style scoped></style>
