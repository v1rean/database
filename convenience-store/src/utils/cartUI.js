import { ref } from 'vue';

const expanded = ref(false);
const locked = ref(false);

export const cartUI = {
  get expanded() { return expanded.value; },
  set expanded(v) { expanded.value = v; },
  get locked() { return locked.value; },
  set locked(v) { locked.value = v; },

  expand() {
    locked.value = true;
    expanded.value = true;
  },

  collapse() {
    locked.value = false;
    expanded.value = false;
  },

  toggle() {
    locked.value = !locked.value;
    expanded.value = locked.value;
  }
};
