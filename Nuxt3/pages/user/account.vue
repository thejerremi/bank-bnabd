<template>
  <v-sheet class="d-flex flex-wrap h-100">
    <v-card class="flex-1-0 ma-16 pa-2" :elevation="24" rounded>
      <v-card-title>
        <div class="text-h3 text-center font-weight-bold">Twoje konto</div>
    </v-card-title>
    <v-card-text>
      <v-expansion-panels variant="accordion">
      <v-expansion-panel
        title="Dane osobowe"
      >
        <v-expansion-panel-text>
          <changeAccountDetails />
        </v-expansion-panel-text>
    </v-expansion-panel>
    <v-expansion-panel
        title="Zmiana hasła"
      >
        <v-expansion-panel-text>
          <changePassword />
        </v-expansion-panel-text>
    </v-expansion-panel>
    <v-expansion-panel
        class="text-red"
        title="Usuń konto"
      >
        <v-expansion-panel-text>
          <deleteAccount />
        </v-expansion-panel-text>
    </v-expansion-panel>
    </v-expansion-panels>
    </v-card-text>
    </v-card>
  </v-sheet>
</template>

<script setup>
import { useAuthStore } from "~/stores/AuthStore";
const authStore = useAuthStore();
</script>

<style scoped>

</style>