export const useSnack = () => {
  const snackbar = useSnackbar();

  function snackbarSuccess(text: string) {
    snackbar.add({
      type: 'success',
      text: text
    });
  }
  function snackbarError(text: string) {
    snackbar.add({
      type: 'error',
      text: text
    });
  }
  return { snackbarSuccess, snackbarError};
}
