import { reactive } from 'vue';

const state = reactive({
  items: []
});

export const cartStore = {
  get items() {
    return state.items;
  },

  get count() {
    return state.items.reduce((sum, item) => sum + item.quantity, 0);
  },

  get total() {
    return state.items.reduce((sum, item) => sum + item.price * item.quantity, 0);
  },

  addItem(product, quantity = 1) {
    const existing = state.items.find(i => i.productId === product.id);
    if (existing) {
      const newQty = existing.quantity + quantity;
      if (newQty <= product.stock) {
        existing.quantity = newQty;
        return true;
      }
      return false;
    }
    if (quantity <= product.stock) {
      state.items.push({
        productId: product.id,
        name: product.name,
        price: product.price,
        quantity,
        stock: product.stock
      });
      return true;
    }
    return false;
  },

  removeItem(productId) {
    const idx = state.items.findIndex(i => i.productId === productId);
    if (idx !== -1) state.items.splice(idx, 1);
  },

  updateQuantity(productId, quantity) {
    const item = state.items.find(i => i.productId === productId);
    if (item) {
      if (quantity <= 0) {
        this.removeItem(productId);
      } else if (quantity <= item.stock) {
        item.quantity = quantity;
      }
    }
  },

  clear() {
    state.items.splice(0);
  }
};
