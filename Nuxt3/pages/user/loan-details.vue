<template>
    <v-sheet class="d-flex flex-wrap h-100">
    <v-card class="flex-1-0 ma-16 pa-2" :elevation="24" rounded>
      <v-card-title>
        <div class="text-h3 text-center font-weight-bold mb-6">Twoja pożyczka</div>
    </v-card-title>
    <v-card-text v-if="activeLoan">
        <v-list>
            <v-list-item>
              <v-list-item-title>Status pożyczki</v-list-item-title>
              {{ loanStatusTypes[activeLoan.status] }}
          </v-list-item>
          <v-list-item>
              <v-list-item-title>Rodzaj pożyczki</v-list-item-title>
              {{ activeLoan.type === 'consumer' ? 'Pożyczka konsumpcyjna' : 'Pożyczka hipoteczna' }}
          </v-list-item>
          <v-list-item>
              <v-list-item-title>Okres spłaty (miesiące)</v-list-item-title>
              {{ activeLoan.length }}
          </v-list-item>
          <v-list-item>
              <v-list-item-title>Kwota pożyczki</v-list-item-title>
              {{ activeLoan.amount }} PLN
          </v-list-item>
          <v-list-item>
              <v-list-item-title>Pozostała kwota do spłaty</v-list-item-title>
              {{ activeLoan.paymentLeft.toFixed(2) }} PLN
          </v-list-item>
          <v-list-item>
              <v-list-item-title>Miesięczna rata</v-list-item-title>
              {{ activeLoan.monthlyRate }} PLN
          </v-list-item>
        </v-list>
        <v-divider />
        <div class="text-center mt-16" v-if="activeLoan.status === `PENDING`">
          Twoja pożyczka czeka na akceptację. <br>
          Zwykle trwa to do 24 godzin. <br>
          Nie możesz podjąć żadnych działań do czasu akceptacji. <br>
          Dziękujemy za cierpliwość.
        </div>
        <div class="text-center mt-6" v-else>
          <div class="font-weight-bold mb-6">Twoje saldo: {{ authStore.user.balance.toFixed(2) }} PLN</div>

          <v-btn color="green" :disabled="activeLoan.paymentLeft < activeLoan.monthlyRate || authStore.user.balance < activeLoan.monthlyRate" @click="payMonthlyRate">Spłać miesięczną ratę</v-btn>
          <div class="text-red" v-if="activeLoan.paymentLeft < activeLoan.monthlyRate || authStore.user.balance < activeLoan.monthlyRate">
            Miesięczna rata jest większa od pozostałej kwoty do zapłaty <br> lub nie posiadasz tyle na koncie by zapłacić miesieczną ratę.
          </div>
          <div class="mt-6">
            <v-form @submit.prevent="handlePayLoanRepayment" v-model="valid">
            <v-text-field v-model="amount" label="Kwota do spłaty" 
            :rules="[v => !!v || 'Kwota jest wymagana', 
              v => v > 0 || 'Kwota musi być większa od zera', 
              v => v <= activeLoan.paymentLeft || 'Kwota nie może być większa niż pozostała do spłaty',
              v => v <= authStore.user.balance || 'Nie masz tyle środków na koncie']" 
            type="number" variant="outlined">
          </v-text-field>
            <v-btn color="green" type="submit" :disabled="!valid">Spłać</v-btn>
          </v-form>
          </div>
        </div>
      </v-card-text>
      <v-card-text v-else>
        <v-skeleton-loader type="list-item"></v-skeleton-loader>
      </v-card-text>
    </v-card>
  </v-sheet>
</template>

<script setup>
definePageMeta({
  middleware: 'loan'
});
import { useAuthStore } from "~/stores/AuthStore";
import { useLoanStore } from "~/stores/LoanStore";
import { useSnack } from '@/composables/useSnack';
const { snackbarSuccess, snackbarError } = useSnack();
const loanStore = useLoanStore();
const authStore = useAuthStore();

const activeLoan = computed(() => loanStore.activeLoan);
const loanStatusTypes = {
  PENDING: "Oczekująca",
  ACCEPTED: "Zaakceptowana",
  REJECTED: "Odrzucona",
};

onMounted(async () => {
  if(authStore.user.hasLoan && loanStore.activeLoan === null) {
    await loanStore.fetchLoan();

  }
});

const valid = ref(false);
const amount = ref(0);

const payMonthlyRate = async () => {
  try {
    await loanStore.payMonthlyRate();
    loanStore.activeLoan.paymentLeft -= loanStore.activeLoan.monthlyRate;
    authStore.user.balance -= loanStore.activeLoan.monthlyRate;
    snackbarSuccess('Zapłacono miesięczną ratę.');
  } catch (error) {
    snackbarError("Wystąpił błąd podczas płacenia raty.");
  }
};

const handlePayLoanRepayment = async () => {
  if (valid.value) {
    try {
      await loanStore.payLoanRepayment(amount.value);
      loanStore.activeLoan.paymentLeft -= amount.value;
      authStore.user.balance -= amount.value;
      snackbarSuccess('Spłacono ' + amount.value + ' PLN z aktualnej pożyczki.');
    } catch (error) {
      console.log(error)
      snackbarError("Wystąpił błąd podczas spłacania pożyczki.");
    }
  }
};
</script>

<style scoped>
.v-card {
  max-width: 600px;
  margin: 20px auto;
}

.v-sheet {
  justify-content: center;
}
</style>