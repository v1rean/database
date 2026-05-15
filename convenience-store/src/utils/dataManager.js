const API_BASE = '/api';

export const dataManager = {
  async login(username, password) {
    try {
      const res = await fetch(`${API_BASE}/accounts/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      if (!res.ok) return null;
      const data = await res.json();
      return data;
    } catch {
      return null;
    }
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

  async checkout(purchase) {
    try {
      const res = await fetch(`${API_BASE}/purchases/checkout`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(purchase)
      });
      return await res.json();
    } catch {
      return { success: false, message: '网络错误，请重试' };
    }
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
  },

  async register(username, password, name) {
    try {
      const res = await fetch(`${API_BASE}/accounts/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, name })
      });
      if (!res.ok) return null;
      return res.json();
    } catch {
      return null;
    }
  },

  async forgotPassword(username, newPassword) {
    try {
      const res = await fetch(`${API_BASE}/accounts/forgot-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, newPassword })
      });
      return res.ok;
    } catch {
      return false;
    }
  },

  async changePassword(userId, oldPassword, newPassword) {
    try {
      const res = await fetch(`${API_BASE}/accounts/change-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, oldPassword, newPassword })
      });
      return res.ok;
    } catch {
      return false;
    }
  },

  async loadPreference(userId) {
    try {
      const res = await fetch(`${API_BASE}/accounts/${userId}/preferences`);
      if (!res.ok) return { recommendEnabled: true };
      return await res.json();
    } catch {
      return { recommendEnabled: true };
    }
  },

  async savePreference(userId, recommendEnabled) {
    try {
      const res = await fetch(`${API_BASE}/accounts/${userId}/preferences`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ recommendEnabled })
      });
      return res.ok;
    } catch {
      return false;
    }
  },

  // ---- Admin: Users ----
  async getAllUsers() {
    const res = await fetch(`${API_BASE}/accounts`);
    return res.json();
  },
  async deleteUser(id) {
    const res = await fetch(`${API_BASE}/accounts/${id}`, { method: 'DELETE' });
    return res.ok;
  },

  // ---- Categories ----
  async getAllCategories() {
    const res = await fetch(`${API_BASE}/categories`);
    return res.json();
  },
  async addCategory(cat) {
    const res = await fetch(`${API_BASE}/categories`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cat)
    });
    return res.ok;
  },
  async updateCategory(cat) {
    const res = await fetch(`${API_BASE}/categories/${cat.id}`, {
      method: 'PUT', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cat)
    });
    return res.ok;
  },
  async deleteCategory(id) {
    const res = await fetch(`${API_BASE}/categories/${id}`, { method: 'DELETE' });
    return res.ok;
  },

  // ---- Inventory ----
  async getAllInventory() {
    const res = await fetch(`${API_BASE}/inventory`);
    return res.json();
  },
  async addInventory(log) {
    const res = await fetch(`${API_BASE}/inventory`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(log)
    });
    return res.ok;
  },

  // ---- Carousels ----
  async getAllCarousels() {
    const res = await fetch(`${API_BASE}/carousels`);
    return res.json();
  },
  async getActiveCarousels() {
    const res = await fetch(`${API_BASE}/carousels/active`);
    return res.json();
  },
  async addCarousel(c) {
    const res = await fetch(`${API_BASE}/carousels`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(c)
    });
    return res.ok;
  },
  async updateCarousel(c) {
    const res = await fetch(`${API_BASE}/carousels/${c.id}`, {
      method: 'PUT', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(c)
    });
    return res.ok;
  },
  async deleteCarousel(id) {
    const res = await fetch(`${API_BASE}/carousels/${id}`, { method: 'DELETE' });
    return res.ok;
  },

  // ---- Announcements ----
  async getAllAnnouncements() {
    const res = await fetch(`${API_BASE}/announcements`);
    return res.json();
  },
  async getActiveAnnouncements() {
    const res = await fetch(`${API_BASE}/announcements/active`);
    return res.json();
  },
  async addAnnouncement(a) {
    const res = await fetch(`${API_BASE}/announcements`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(a)
    });
    return res.ok;
  },
  async updateAnnouncement(a) {
    const res = await fetch(`${API_BASE}/announcements/${a.id}`, {
      method: 'PUT', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(a)
    });
    return res.ok;
  },
  async deleteAnnouncement(id) {
    const res = await fetch(`${API_BASE}/announcements/${id}`, { method: 'DELETE' });
    return res.ok;
  },

  // ---- Admin: Purchase Search ----
  async searchPurchases(start, end, username) {
    const params = new URLSearchParams();
    if (start) params.append('start', start);
    if (end) params.append('end', end);
    if (username) params.append('username', username);
    const res = await fetch(`${API_BASE}/purchases/search?${params}`);
    return res.json();
  },

  // ---- Admin: Refund ----
  async refundPurchase(id, operator) {
    const res = await fetch(`${API_BASE}/purchases/${id}/refund`, {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ operator })
    });
    return res.json();
  },

  // ---- Admin: Sales Report ----
  async getSalesReport(start, end) {
    const params = new URLSearchParams();
    if (start) params.append('start', start);
    if (end) params.append('end', end);
    const res = await fetch(`${API_BASE}/purchases/sales-report?${params}`);
    return res.json();
  }
};
