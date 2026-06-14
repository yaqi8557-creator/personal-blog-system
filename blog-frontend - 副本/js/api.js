const API_BASE = 'http://localhost:8081/api';

class Api {
    constructor() {
        this.token = localStorage.getItem('token') || '';
    }

    setToken(token) {
        this.token = token;
        localStorage.setItem('token', token);
    }

    clearToken() {
        this.token = '';
        localStorage.removeItem('token');
    }

    getHeaders() {
        const headers = { 'Content-Type': 'application/json' };
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        return headers;
    }

    async request(url, options = {}) {
        const response = await fetch(`${API_BASE}${url}`, {
            ...options,
            headers: { ...this.getHeaders(), ...options.headers }
        });

        if (response.status === 401) {
            if (this.token) {
                this.clearToken();
                if (window.__onAuthExpired) window.__onAuthExpired();
            }
            throw new Error('登录已过期，请重新登录');
        }

        const data = await response.json();
        if (data.code !== 200) {
            throw new Error(data.msg || '请求失败');
        }
        return data.data;
    }

    // 用户相关
    async register(username, password, nickname) {
        return this.request('/user/register', {
            method: 'POST',
            body: JSON.stringify({ username, password, nickname })
        });
    }

    async login(username, password) {
        const data = await this.request('/user/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
        this.setToken(data);
        return data;
    }

    async getUserInfo() {
        return this.request('/user/info');
    }

    logout() {
        this.clearToken();
    }

    // 文章相关
    async getArticles(page = 1, size = 10, categoryId = null) {
        let url = `/article/list?page=${page}&size=${size}`;
        if (categoryId) url += `&categoryId=${categoryId}`;
        return this.request(url);
    }

    async getArticle(id) {
        return this.request(`/article/${id}`);
    }

    async createArticle(article) {
        return this.request('/article', {
            method: 'POST',
            body: JSON.stringify(article)
        });
    }

    async updateArticle(article) {
        return this.request('/article', {
            method: 'PUT',
            body: JSON.stringify(article)
        });
    }

    async deleteArticle(id) {
        return this.request(`/article/${id}`, { method: 'DELETE' });
    }

    // 分类相关
    async getCategories() {
        return this.request('/category/list');
    }

    async createCategory(category) {
        return this.request('/category', {
            method: 'POST',
            body: JSON.stringify(category)
        });
    }

    async updateCategory(category) {
        return this.request('/category', {
            method: 'PUT',
            body: JSON.stringify(category)
        });
    }

    async deleteCategory(id) {
        return this.request(`/category/${id}`, { method: 'DELETE' });
    }

    // 评论相关
    async getComments(articleId) {
        return this.request(`/comment/list/${articleId}`);
    }

    async addComment(articleId, content) {
        return this.request('/comment', {
            method: 'POST',
            body: JSON.stringify({ article_id: articleId, content })
        });
    }

    async deleteComment(id) {
        return this.request(`/comment/${id}`, { method: 'DELETE' });
    }

    // 点赞相关
    async like(articleId) {
        return this.request(`/like/${articleId}`, { method: 'POST' });
    }

    async unlike(articleId) {
        return this.request(`/like/${articleId}`, { method: 'DELETE' });
    }

    async getLikeStatus(articleId) {
        return this.request(`/like/status/${articleId}`);
    }

    // 搜索
    async searchArticles(keyword, page = 1, size = 10) {
        return this.request(`/article/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
    }
}

export default new Api();