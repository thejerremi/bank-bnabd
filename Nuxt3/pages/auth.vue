<template>
  <div class="background">
    <v-container>
      <div class="text-h1 font-weight-bold text-center pb-12">TwójBank</div>
      <v-card>
        <v-card-title>
          <v-tabs fixed-tabs v-model="tab">
            <v-tab text="Zaloguj się" value="login"></v-tab>
            <v-tab text="Załóż konto" value="register"></v-tab>
          </v-tabs>
        </v-card-title>
        <v-tabs-window v-model="tab">
        <v-tabs-window-item value="login">
          <v-card-text>
          <loginForm />
        </v-card-text>
        </v-tabs-window-item>
        
        
        <v-tabs-window-item value="register">
          <v-card-text>
          <registerForm />
        </v-card-text>
        </v-tabs-window-item>
      </v-tabs-window>
      </v-card>
    </v-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute } from 'vue-router';
const route = useRoute();
definePageMeta({
  layout: 'auth',
})
onMounted(() => {
      if (route.query.openAccount === 'true') {
        tab.value = "register";
      }
    });
const tab = ref()
</script>

<style scoped>
.v-card {
  max-width: 100vh;
  margin-left: auto;
  margin-right: auto;
}

.background {
  background-color: #f2f2f2;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
</style>