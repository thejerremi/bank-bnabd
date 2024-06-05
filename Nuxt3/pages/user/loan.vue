<template>
  <v-sheet class="d-flex flex-wrap h-100">
    <v-card class="flex-1-0 ma-16 pa-2" :elevation="24" rounded>
      <v-card-title>
        <div class="text-h3 text-center font-weight-bold mb-6">
          Wniosek o pożyczkę
        </div>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid" @submit.prevent="submitLoanApplication">
          <v-text-field v-model="loanAmount" label="Kwota pożyczki"
            :rules="[v => !!v || 'Kwota jest wymagana', v => v > 0 || 'Kwota musi być większa od zera']" type="number"
            variant="outlined"></v-text-field>

          <v-select v-model="loanTerm" :items="loanTerms" label="Okres spłaty" variant="outlined"
            :rules="[v => !!v || 'Okres spłaty jest wymagany']"></v-select>

          <v-radio-group inline v-model="loanType" required>
            <v-radio label="Pożyczka konsumpcyjna" value="consumer"></v-radio>
            <v-radio label="Pożyczka hipoteczna" value="mortgage"></v-radio>
          </v-radio-group>

          <v-text-field v-model="interestRate" label="Stopa procentowa"
            :rules="[v => !!v || 'Stopa procentowa jest wymagana']" type="number" variant="outlined"
            readonly></v-text-field>

          <v-row>
            <v-col cols="10">
              <v-text-field v-model="calculateMonthlyPayment" label="Miesięczna rata" readonly
                variant="outlined"></v-text-field>
            </v-col>
            <v-col cols="2">

              <v-tooltip text="Jak obliczamy miesięczną ratę?" location="bottom">
                <template v-slot:activator="{ props }">
                  <v-btn v-bind="props" class="mt-1 ml-2" icon="mdi-help-circle-outline"
                    href="https://www-investopedia-com.translate.goog/terms/a/amortization.asp?_x_tr_sl=en&_x_tr_tl=pl&_x_tr_hl=pl&_x_tr_pto=sc"
                    target="_blank"></v-btn>
                </template>
              </v-tooltip>
            </v-col>
          </v-row>



          <v-checkbox v-model="termsAccepted" :rules="[v => !!v || 'Musisz zaakceptować warunki']"
            label="Akceptuję warunki pożyczki"></v-checkbox>
            <div class="text-center">
        <v-btn color="green" type="submit" :disabled="!valid">Złóż wniosek</v-btn>
        </div>
        </v-form>
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

const valid = ref(false);
const loanAmount = ref(0);
const loanTerm = ref('');
const loanType = ref('consumer');
const interestRate = ref(5);
const termsAccepted = ref(false);


const loanTerms = ['6 miesięcy', '12 miesięcy', '24 miesiące', '36 miesięcy'];

const calculateMonthlyPayment = computed(() => {
  const principal = parseFloat(loanAmount.value);
  const monthlyInterestRate = parseFloat(interestRate.value) / 100 / 12;
  const numberOfPayments = parseInt(loanTerm.value);

  if (numberOfPayments && monthlyInterestRate && principal) {
    const monthlyPayment = (principal * monthlyInterestRate) / (1 - Math.pow(1 + monthlyInterestRate, -numberOfPayments));
    return monthlyPayment.toFixed(2) + ' PLN';
  }
  return '0 PLN';
});

const submitLoanApplication = async () => {
  if (valid.value) {
    await loanStore.applyForLoan({
      amount: loanAmount.value, 
      type: loanType.value,
      length: parseInt(loanTerm.value),
    }).then(() => {
      snackbarSuccess('Wniosek o pożyczkę został złożony.');
      authStore.user.hasLoan = true;
      navigateTo('/user/loan-details');
    }).catch(() => {
      snackbarError('Wystąpił błąd podczas składania wniosku o pożyczkę.');
    });
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
