import '@mdi/font/css/materialdesignicons.css'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import { pl } from 'vuetify/locale'

import { VDateInput } from 'vuetify/labs/VDateInput'


export default defineNuxtPlugin((app) => {
  const vuetify = createVuetify({
    ssr: true,
    components: {
      VDateInput,
    },
    locale: {
      locale: 'pl',
      fallback: 'pl',
      messages: { pl },
    },
    // theme: {
    //   defaultTheme: 'dark'
    // }
  })
  app.vueApp.use(vuetify)
})