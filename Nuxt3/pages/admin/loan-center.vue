<template>
  <v-sheet class="d-flex flex-wrap h-100">
    <v-card class="flex-1-0 ma-16 pa-2" :elevation="24" rounded>
      <v-card-title>
        <div class="text-h3 text-center font-weight-bold mb-16">Zarządzaj oczekującymi pożyczkami</div>
      </v-card-title>
      <v-card-text>
        <v-expansion-panels variant="accordion">
          <v-expansion-panel v-for="loan in loanStore.pendingLoans" :key="loan.id"
            :title="loan.loanResponse.amount + ' PLN - ' + loan.user.firstname + ' ' + loan.user.lastname">
            <v-expansion-panel-text>
              <v-list>
                <v-list-item>
                  <v-list-item-title>Status pożyczki</v-list-item-title>
                  {{ loan.loanResponse.status === 'PENDING' ? 'Oczekująca' : 'Zaakceptowana' }}
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>Typ pożyczki</v-list-item-title>
                  {{ loan.loanResponse.type === 'consumer' ? 'Pożyczka konsumpcyjna' : 'Pożyczka hipoteczna' }}
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>Kwota pożyczki</v-list-item-title>
                  {{ loan.loanResponse.amount }} PLN
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>Długość spłaty pożyczki</v-list-item-title>
                  {{ loan.loanResponse.length }} miesięcy
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>Miesięczna rata</v-list-item-title>
                  {{ loan.loanResponse.monthlyRate }} PLN
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>Całkowita kwota do spłacenia</v-list-item-title>
                  {{ loan.loanResponse.paymentLeft }} PLN
                </v-list-item>
              </v-list>
              <v-expansion-panels>
                <v-expansion-panel title="Dane o kliencie">
                  <v-expansion-panel-text>
                    <v-list>
                      <v-list-item>
                        <v-list-item-title>Imię i nazwisko</v-list-item-title>
                        {{ loan.user.firstname }} {{ loan.user.lastname }}
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Numer konta:</v-list-item-title>
                        {{ loan.user.accountNumber }} PLN
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Aktualne saldo:</v-list-item-title>
                        {{ loan.user.balance }} PLN
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Data urodzenia:</v-list-item-title>
                        {{ loan.user.dob }}
                      </v-list-item>
                      <v-list-item>
                        <v-list-item-title>Pesel:</v-list-item-title>
                        {{ loan.user.pesel }}
                      </v-list-item>
                    </v-list>
                  </v-expansion-panel-text>
                </v-expansion-panel>
              </v-expansion-panels>
              <div class="text-center mt-6">
                <v-btn class="mr-6" color="green" @click="acceptLoan(loan.id)">Zaakceptuj pożyczkę</v-btn>
                <v-btn color="red" @click="rejectLoan(loan.id)">Odrzuć pożyczkę</v-btn>
                </div>
            </v-expansion-panel-text>
          </v-expansion-panel>
        </v-expansion-panels>
      </v-card-text>
    </v-card>
  </v-sheet>
</template>

<script setup>
import { useLoanStore } from '~/stores/LoanStore';
const loanStore = useLoanStore();
onMounted(() => {
  loanStore.fetchPendingLoans();
});
const acceptLoan = async (id) => {
  await loanStore.approvePendingLoan(id);
}
const rejectLoan = async (id) => {
  await loanStore.rejectPendingLoan(id);
}
</script>

<style scoped></style>