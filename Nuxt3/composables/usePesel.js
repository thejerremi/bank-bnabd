import { ref, computed, watch } from 'vue';

export function usePesel() {
  const pesel = ref('');

  const peselRules = [
    v => !!v || 'PESEL jest wymagany',
    v => (v && v.length === 11) || 'PESEL musi mieć 11 cyfr',
    v => isValidPesel(v) || 'PESEL jest niepoprawny'
  ];

  function isValidPesel(pesel) {
    if (!/^\d{11}$/.test(pesel)) {
      return false;
    }

    const weights = [1, 3, 7, 9, 1, 3, 7, 9, 1, 3];
    let sum = 0;

    for (let i = 0; i < 10; i++) {
      sum += parseInt(pesel[i]) * weights[i];
    }

    const controlDigit = (10 - (sum % 10)) % 10;
    return controlDigit === parseInt(pesel[10]);
  }

  function getBirthDateFromPesel(pesel) {
    if (!/^\d{11}$/.test(pesel)) {
      return null;
    }

    const year = parseInt(pesel.substr(0, 2), 10);
    let month = parseInt(pesel.substr(2, 2), 10);
    const day = parseInt(pesel.substr(4, 2), 10);

    let fullYear;
    if (month > 80) {
      fullYear = 1800 + year;
      month -= 80;
    } else if (month > 60) {
      fullYear = 2200 + year;
      month -= 60;
    } else if (month > 40) {
      fullYear = 2100 + year;
      month -= 40;
    } else if (month > 20) {
      fullYear = 2000 + year;
      month -= 20;
    } else {
      fullYear = 1900 + year;
    }

    return new Date(fullYear, month - 1, day, 12, 0, 0, 0);
  }


  return {
    pesel,
    peselRules,
    isValidPesel,
    getBirthDateFromPesel,
  };
}
