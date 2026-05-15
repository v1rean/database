import { reactive } from 'vue';

const state = reactive({
  visible: false,
  message: '',
  type: 'success'
});

let timer = null;

export const toast = {
  get visible() { return state.visible; },
  get message() { return state.message; },
  get type() { return state.type; },

  show(message, type = 'success') {
    clearTimeout(timer);
    state.message = message;
    state.type = type;
    state.visible = true;
    timer = setTimeout(() => {
      state.visible = false;
    }, 2200);
  },

  success(msg) {
    this.show(msg, 'success');
  },

  error(msg) {
    this.show(msg, 'error');
  },

  hide() {
    clearTimeout(timer);
    state.visible = false;
  }
};
