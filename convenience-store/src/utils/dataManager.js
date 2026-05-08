const API_BASE = '/api';

export const dataManager = {
  async login(username, password) {
    const res = await fetch(`${API_BASE}/accounts/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    if (!res.ok) return null;
    return res.json();
  },

  async getProducts() {
    const res = await fetch(`${API_BASE}/products`);
    return res.json();
  },

  async addProduct(product) {
    const res = await fetch(`${API_BASE}/products`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(product)
    });
    return res.ok;
  },

  async updateProduct(product) {
    const res = await fetch(`${API_BASE}/products/${product.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(product)
    });
    return res.ok;
  },

  async deleteProduct(id) {
    const res = await fetch(`${API_BASE}/products/${id}`, {
      method: 'DELETE'
    });
    return res.ok;
  },

  async getAllPurchases() {
    const res = await fetch(`${API_BASE}/purchases`);
    return res.json();
  },

  async getUserPurchases(userId) {
    const res = await fetch(`${API_BASE}/purchases/user/${userId}`);
    return res.json();
  },

  async addPurchase(purchase) {
    const res = await fetch(`${API_BASE}/purchases`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(purchase)
    });
    return res.ok;
  },

  async deletePurchase(id) {
    const res = await fetch(`${API_BASE}/purchases/${id}`, {
      method: 'DELETE'
    });
    return res.ok;
  },

  async getCurrentUser() {
    const user = localStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
  },

  async setCurrentUser(user) {
    localStorage.setItem('currentUser', JSON.stringify(user));
  },

  async logout() {
    localStorage.removeItem('currentUser');
  }
};
