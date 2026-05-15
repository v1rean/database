const KEY = 'browseHistory';
const MAX = 20;

export const browseHistory = {
  get() {
    try {
      return JSON.parse(localStorage.getItem(KEY) || '[]');
    } catch { return []; }
  },

  add(productId) {
    let list = this.get();
    list = list.filter(id => id !== productId);
    list.unshift(productId);
    if (list.length > MAX) list = list.slice(0, MAX);
    localStorage.setItem(KEY, JSON.stringify(list));
  },

  clear() {
    localStorage.removeItem(KEY);
  }
};
