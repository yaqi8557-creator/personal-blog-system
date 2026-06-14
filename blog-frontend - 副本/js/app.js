import api from './api.js';

const { createApp, ref, computed, onMounted, watch } = Vue;

const app = createApp({
    setup() {
        // 状态
        const currentPage = ref('home');
        const isLoggedIn = ref(false);
        const user = ref({});
        const loading = ref(false);

        // 文章相关
        const articles = ref([]);
        const adminArticles = ref([]);
        const currentArticle = ref(null);
        const currentPageNum = ref(1);
        const pageSize = 10;
        const total = ref(0);
        const currentCategory = ref(null);

        // 分类相关
        const categories = ref([]);

        // 评论相关
        const comments = ref([]);
        const commentContent = ref('');

        // 点赞相关
        const isLiked = ref(false);
        const likeCount = ref(0);

        // 统计
        const stats = ref({ articles: 0, categories: 0, totalViews: 0, totalLikes: 0 });

        // 模态框
        const showLoginModal = ref(false);
        const showRegisterModal = ref(false);
        const showArticleModal = ref(false);
        const showCategoryModal = ref(false);

        // 表单
        const loginForm = ref({ username: '', password: '' });
        const registerForm = ref({ username: '', password: '', nickname: '', confirmPassword: '' });
        const editingArticle = ref({});
        const editingCategory = ref({});

        // 加载状态
        const loginLoading = ref(false);
        const registerLoading = ref(false);

        // 消息提示
        const toast = ref({ show: false, message: '', type: 'success' });

        // 热门文章
        const hotArticles = ref([]);

        // 用户信息映射 (id -> user)
        const userMap = ref({});

        // 管理选项卡
        const adminTab = ref('articles');
        // 搜索
        const searchKeyword = ref('');
        const isSearching = ref(false);

        // 计算属性
        const totalPages = computed(() => Math.ceil(total.value / pageSize));
        const pageNumbers = computed(() => {
            const pages = [];
            const start = Math.max(1, currentPageNum.value - 2);
            const end = Math.min(totalPages.value, currentPageNum.value + 2);
            for (let i = start; i <= end; i++) {
                pages.push(i);
            }
            return pages;
        });

        // 方法
        function showToast(message, type = 'success') {
            toast.value = { show: true, message, type };
            setTimeout(() => { toast.value.show = false; }, 3000);
        }

        function formatDate(dateStr) {
            if (!dateStr) return '-';
            const date = new Date(dateStr);
            return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' });
        }

        function getCategoryName(id) {
            const cat = categories.value.find(c => c.id === id);
            return cat ? cat.name : '未分类';
        }

        async function navigate(page) {
            currentPage.value = page;
            if (page === 'home') {
                await loadArticles();
                await loadCategories();
                await loadHotArticles();
            } else if (page === 'admin') {
                await loadAdminData();
            }
        }

        async function loadArticles() {
            loading.value = true;
            try {
                const data = await api.getArticles(currentPageNum.value, pageSize, currentCategory.value);
                articles.value = data.records || [];
                total.value = data.total || 0;
            } catch (e) {
                showToast(e.message, 'error');
            }
            loading.value = false;
        }

        async function loadCategories() {
            try {
                categories.value = await api.getCategories();
                stats.value.categories = categories.value.length;
            } catch (e) {
                console.error(e);
            }
        }

        async function loadAdminData() {
            try {
                const data = await api.getArticles(1, 100);
                adminArticles.value = data.records || [];
                // 从后端获取统计数据
                const s = await api.request('/article/stats');
                stats.value = s;
                await loadCategories();
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        function filterByCategory(categoryId) {
            currentCategory.value = categoryId;
            currentPageNum.value = 1;
            loadArticles();
        }

        function changePage(page) {
            currentPageNum.value = page;
            loadArticles();
            window.scrollTo(0, 0);
        }

        async function viewArticle(id) {
            try {
                currentArticle.value = await api.getArticle(id);
                currentPage.value = 'article';
                await loadComments(id);
                if (isLoggedIn.value) {
                    await loadLikeStatus(id);
                }
                window.scrollTo(0, 0);
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function loadComments(articleId) {
            try {
                comments.value = await api.getComments(articleId);
                // 加载评论者的用户名
                for (const c of comments.value) {
                    if (c.user_id && !userMap.value[c.user_id]) {
                        try {
                            const data = await api.request('/user/info');
                            userMap.value[c.user_id] = data;
                        } catch {}
                    }
                }
            } catch (e) {
                console.error(e);
            }
        }

        async function loadLikeStatus(articleId) {
            try {
                const data = await api.getLikeStatus(articleId);
                isLiked.value = data.liked;
                likeCount.value = data.likeCount;
            } catch (e) {
                console.error(e);
            }
        }

        async function toggleLike() {
            if (!isLoggedIn.value) {
                showLoginModal.value = true;
                return;
            }
            try {
                const data = await api.like(currentArticle.value.id);
                isLiked.value = data.liked;
                likeCount.value = data.likeCount;
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function submitComment() {
            if (!commentContent.value.trim()) return;
            try {
                await api.addComment(currentArticle.value.id, commentContent.value);
                commentContent.value = '';
                await loadComments(currentArticle.value.id);
                showToast('评论成功');
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function deleteComment(id) {
            if (!confirm('确定删除评论？')) return;
            try {
                await api.deleteComment(id);
                await loadComments(currentArticle.value.id);
                showToast('删除成功');
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        // 用户相关
        async function handleLogin() {
            loginLoading.value = true;
            try {
                await api.login(loginForm.value.username, loginForm.value.password);
                isLoggedIn.value = true;
                user.value = await api.getUserInfo();
                showLoginModal.value = false;
                loginForm.value = { username: '', password: '' };
                showToast('登录成功');
            } catch (e) {
                showToast(e.message, 'error');
            }
            loginLoading.value = false;
        }

        async function handleRegister() {
            if (registerForm.value.password !== registerForm.value.confirmPassword) {
                showToast('两次密码不一致', 'error');
                return;
            }
            registerLoading.value = true;
            try {
                await api.register(registerForm.value.username, registerForm.value.password, registerForm.value.nickname);
                showRegisterModal.value = false;
                registerForm.value = { username: '', password: '', nickname: '', confirmPassword: '' };
                showLoginModal.value = true;
                showToast('注册成功，请登录');
            } catch (e) {
                showToast(e.message, 'error');
            }
            registerLoading.value = false;
        }

        function logout() {
            api.logout();
            isLoggedIn.value = false;
            user.value = {};
            navigate('home');
            showToast('已退出登录');
        }

        // 文章管理
        function openArticleModal(article = null) {
            editingArticle.value = article ? { ...article } : { title: '', category_id: '', summary: '', content: '' };
            showArticleModal.value = true;
        }

        async function saveArticle() {
            try {
                if (editingArticle.value.id) {
                    await api.updateArticle(editingArticle.value);
                    showToast('更新成功');
                } else {
                    await api.createArticle(editingArticle.value);
                    showToast('发布成功');
                }
                showArticleModal.value = false;
                await loadAdminData();
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function confirmDeleteArticle(id) {
            if (!confirm('确定删除文章？')) return;
            try {
                await api.deleteArticle(id);
                showToast('删除成功');
                await loadAdminData();
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        // 分类管理
        function openCategoryModal(category = null) {
            editingCategory.value = category ? { ...category } : { name: '', description: '' };
            showCategoryModal.value = true;
        }

        async function saveCategory() {
            try {
                if (editingCategory.value.id) {
                    await api.updateCategory(editingCategory.value);
                    showToast('更新成功');
                } else {
                    await api.createCategory(editingCategory.value);
                    showToast('创建成功');
                }
                showCategoryModal.value = false;
                await loadCategories();
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        async function confirmDeleteCategory(id) {
            if (!confirm('确定删除分类？')) return;
            try {
                await api.deleteCategory(id);
                showToast('删除成功');
                await loadCategories();
            } catch (e) {
                showToast(e.message, 'error');
            }
        }

        // 热门文章(从后端获取)
        async function loadHotArticles() {
            try {
                hotArticles.value = await api.request('/article/hot');
            } catch (e) {
                console.error(e);
            }
        }


        // 搜索
        async function doSearch() {
            const kw = searchKeyword.value.trim();
            if (!kw) { showToast('请输入搜索关键词', 'warning'); return; }
            isSearching.value = true;
            currentPageNum.value = 1;
            currentCategory.value = null;
            loading.value = true;
            try {
                const data = await api.searchArticles(kw, 1, pageSize);
                articles.value = data.records || [];
                total.value = data.total || 0;
            } catch (e) { showToast(e.message, 'error'); }
            loading.value = false;
        }

        async function clearSearch() {
            searchKeyword.value = '';
            isSearching.value = false;
            currentPageNum.value = 1;
            await loadArticles();
        }
        // 加载用户信息映射(评论显示用户名用)
        async function loadUserMap() {
            try {
                const data = await api.request('/user/info');
                userMap.value[data.id] = data;
            } catch (e) {
                console.error(e);
            }
        }

        // token过期自动登出
        function handleLogout() {
            if (!isLoggedIn.value) return;
            isLoggedIn.value = false;
            user.value = {};
            currentPage.value = 'home';
            currentPageNum.value = 1;
            currentCategory.value = null;
            searchKeyword.value = '';
            isSearching.value = false;
            loadArticles();
            loadCategories();
            loadHotArticles();
            showToast('登录已过期，请重新登录', 'error');
        }

        // 初始化
        onMounted(async () => {
            // 设置token过期回调
            window.__onAuthExpired = handleLogout;

            if (api.token) {
                try {
                    isLoggedIn.value = true;
                    user.value = await api.getUserInfo();
                } catch {
                    api.clearToken();
                    isLoggedIn.value = false;
                }
            }
            await loadArticles();
            await loadCategories();
            await loadHotArticles();
        });

        return {
            currentPage, isLoggedIn, user, loading,
            articles, adminArticles, currentArticle, currentPageNum, total, currentCategory,
            categories, comments, commentContent,
            isLiked, likeCount, stats, userMap,
            showLoginModal, showRegisterModal, showArticleModal, showCategoryModal,
            loginForm, registerForm, editingArticle, editingCategory,
            loginLoading, registerLoading, toast, hotArticles, adminTab,
            totalPages, pageNumbers,
            showToast, formatDate, getCategoryName, navigate, searchKeyword, isSearching, doSearch, clearSearch,
            filterByCategory, changePage, viewArticle,
            toggleLike, submitComment, deleteComment,
            handleLogin, handleRegister, handleLogout, logout,
            openArticleModal, saveArticle, confirmDeleteArticle,
            openCategoryModal, saveCategory, confirmDeleteCategory
        };
    }
});

app.mount('#app');
