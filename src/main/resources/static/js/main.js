/* =========================================================
   AutoCare Pro — Core Frontend Interactions
   ========================================================= */

document.addEventListener('DOMContentLoaded', () => {
    initLoadingScreen();
    initThemeToggle();
    initRipple();
    initCounters();
    initScrollReveal();
    initSidebarToggle();
    initPasswordToggle();
});

/* ---------- Loading Screen ---------- */
function initLoadingScreen() {
    const screen = document.getElementById('loading-screen');
    if (!screen) return;
    window.addEventListener('load', () => {
        setTimeout(() => screen.classList.add('hide'), 350);
    });
    // Fallback in case load event already fired
    setTimeout(() => screen.classList.add('hide'), 2000);
}

/* ---------- Dark / Light Mode ---------- */
function initThemeToggle() {
    const toggleBtns = document.querySelectorAll('.theme-toggle-btn');
    const root = document.documentElement;
    const saved = localStorage.getItem('acp-theme') || 'dark';
    if (saved === 'light') root.setAttribute('data-theme', 'light');

    updateThemeIcons();

    toggleBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const isLight = root.getAttribute('data-theme') === 'light';
            if (isLight) {
                root.removeAttribute('data-theme');
                localStorage.setItem('acp-theme', 'dark');
            } else {
                root.setAttribute('data-theme', 'light');
                localStorage.setItem('acp-theme', 'light');
            }
            updateThemeIcons();
        });
    });

    function updateThemeIcons() {
        const isLight = root.getAttribute('data-theme') === 'light';
        document.querySelectorAll('.theme-toggle-btn i').forEach(icon => {
            icon.className = isLight ? 'fa-solid fa-moon' : 'fa-solid fa-sun';
        });
    }
}

/* ---------- Ripple Effect ---------- */
function initRipple() {
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('.btn');
        if (!btn) return;
        const rect = btn.getBoundingClientRect();
        const ripple = document.createElement('span');
        const size = Math.max(rect.width, rect.height);
        ripple.style.width = ripple.style.height = size + 'px';
        ripple.style.left = (e.clientX - rect.left - size / 2) + 'px';
        ripple.style.top = (e.clientY - rect.top - size / 2) + 'px';
        ripple.classList.add('ripple');
        btn.style.position = btn.style.position || 'relative';
        btn.appendChild(ripple);
        setTimeout(() => ripple.remove(), 600);
    });
}

/* ---------- Animated Counters ---------- */
function initCounters() {
    const counters = document.querySelectorAll('[data-counter]');
    if (!counters.length) return;

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateCounter(entry.target);
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.4 });

    counters.forEach(c => observer.observe(c));

    function animateCounter(el) {
        const target = parseFloat(el.getAttribute('data-counter'));
        const duration = 1400;
        const start = performance.now();
        const isDecimal = target % 1 !== 0;

        function step(now) {
            const progress = Math.min((now - start) / duration, 1);
            const eased = 1 - Math.pow(1 - progress, 3);
            const value = target * eased;
            el.textContent = isDecimal ? value.toFixed(1) : Math.floor(value).toLocaleString();
            if (progress < 1) requestAnimationFrame(step);
            else el.textContent = isDecimal ? target.toFixed(1) : target.toLocaleString();
        }
        requestAnimationFrame(step);
    }
}

/* ---------- Scroll Reveal ---------- */
function initScrollReveal() {
    const items = document.querySelectorAll('.fade-up');
    if (!items.length) return;
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('in-view');
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.15 });
    items.forEach(i => observer.observe(i));
}

/* ---------- Sidebar toggle (mobile) ---------- */
function initSidebarToggle() {
    const btn = document.getElementById('sidebar-toggle');
    const sidebar = document.querySelector('.sidebar');
    if (!btn || !sidebar) return;
    btn.addEventListener('click', () => sidebar.classList.toggle('show'));
}

/* ---------- Show / Hide Password ---------- */
function initPasswordToggle() {
    document.querySelectorAll('.toggle-password').forEach(toggle => {
        toggle.addEventListener('click', () => {
            const input = document.querySelector(toggle.getAttribute('data-target'));
            if (!input) return;
            const isPassword = input.type === 'password';
            input.type = isPassword ? 'text' : 'password';
            toggle.innerHTML = isPassword
                ? '<i class="fa-solid fa-eye-slash"></i>'
                : '<i class="fa-solid fa-eye"></i>';
        });
    });
}

/* ---------- Toast Notifications ---------- */
function showToast(message, type = 'info') {
    let container = document.querySelector('.toast-container-premium');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container-premium';
        document.body.appendChild(container);
    }

    const icons = {
        success: 'fa-circle-check text-success',
        error: 'fa-circle-exclamation text-danger',
        warning: 'fa-triangle-exclamation text-warning',
        info: 'fa-circle-info text-info'
    };

    const toast = document.createElement('div');
    toast.className = 'toast-premium';
    toast.innerHTML = `
        <i class="fa-solid ${icons[type] || icons.info}"></i>
        <span style="flex:1">${message}</span>
        <i class="fa-solid fa-xmark" style="cursor:pointer;opacity:.6" onclick="this.parentElement.remove()"></i>
    `;
    container.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('hide');
        setTimeout(() => toast.remove(), 400);
    }, 4500);
}

// Expose globally
window.showToast = showToast;
