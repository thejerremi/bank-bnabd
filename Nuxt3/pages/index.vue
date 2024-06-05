<template>
  <v-container class="text-center grey d-flex flex-column align-center justify-center h-100">
    <div class="text-h2 font-weight-bold">TwójBank</div>
    <div class="text-h4 mt-5 text-center">Naszą misją jest dostarczanie bezpiecznych i nowoczesnych rozwiązań bankowych, <br> które ułatwią Ci zarządzanie finansami. <br> Dołącz do nas i odkryj wygodę bankowości na miarę XXI wieku.</div>
    <div class="text-h4 mt-3 font-weight-bold">Załóż konto i otrzymaj 300 PLN na start!</div>
    <div class="mt-5" v-if="!authStore.user">
      <v-btn class="mr-4" color="black" to="/auth" variant="outlined">Zaloguj się</v-btn>
      <v-btn color="black" to="/auth?openAccount=true">Otwórz konto</v-btn>
    </div>
    <div class="mt-5" v-else>
      <v-btn color="black" to="/user" variant="outlined">Przejdź do pulpitu</v-btn>
    </div>
    <v-timeline direction="horizontal" class="mt-12">
    <v-timeline-item>
      <template v-slot:opposite>
        <div class="font-weight-bold">Po pierwsze</div>
      </template>
      <div>
        <div class="text-h6 font-weight-bold">Proste założenie konta</div>
        <p>
          Założenie konta w naszym banku jest proste i szybkie. <br> Wystarczy kliknąć przycisk 'Otwórz konto' i postępować zgodnie z instrukcjami.
        </p>
      </div>
    </v-timeline-item>

    <v-timeline-item>
      <template v-slot:opposite>
        <div class="font-weight-bold">Po drugie</div>
      </template>
      <div>
        <div class="text-h6 font-weight-bold">Szybkie Przelewy</div>
        <p>
          Wykonuj przelewy krajowe i zagraniczne w zaledwie kilka sekund, z gwarancją bezpieczeństwa i szybkości.
        </p>
      </div>
    </v-timeline-item>

    <v-timeline-item>
      <template v-slot:opposite>
        <div class="font-weight-bold">I najważniejsze</div>
      </template>
      <div>
        <div class="text-h6 font-weight-bold">Bezpieczeństwo na Pierwszym Miejscu</div>
        <p>
          Twoje bezpieczeństwo jest dla nas priorytetem.<br> Korzystamy z najnowocześniejszych technologii, aby chronić Twoje dane i pieniądze.
        </p>
      </div>
    </v-timeline-item>
  </v-timeline>
  </v-container>
</template>

<script setup>
import { useAuthStore } from "~/stores/AuthStore";
const authStore = useAuthStore();
</script>

<style scoped>

</style>