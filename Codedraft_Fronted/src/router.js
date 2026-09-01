// CodeCraftHub — client-side router and header (dark theme).
import { getState, setState, subscribe } from './state/store.js';
import { icon } from './utils/ui.js';
import { currentTheme, toggleTheme } from './utils/theme.js';
import { renderProfile } from './views/profile.js';
import { renderDashboard } from './views/dashboard.js';
import { renderCourses } from './views/courses.js';
import { renderSuggested } from './views/suggested.js';

const ROUTES = ['profile', 'dashboard', 'courses', 'suggested'];
const NAV_ITEMS = [
  { route: 'profile',   label: 'Perfil',           icon: 'user' },
  { route: 'dashboard', label: 'Dashboard',        icon: 'dashboard' },
  { route: 'courses',   label: 'Mis cursos',       icon: 'book' },
  { route: 'suggested', label: 'Cursos sugeridos', icon: 'sparkles' },
];

function currentRoute() {
  const h = (location.hash || '').replace('#/', '').replace('#', '');
  return ROUTES.includes(h) ? h : 'profile';
}

export function navigate(route) {
  if (!ROUTES.includes(route)) route = 'profile';
  location.hash = `/${route}`;
}

function handleRouteChange() {
  const route = currentRoute();
  setState({ route });
  renderActiveNav();
  renderView(route);
  window.scrollTo({ top: 0 });
}

function renderActiveNav() {
  const r = getState().route;
  document.querySelectorAll('[data-nav]').forEach((a) => a.classList.toggle('is-active', a.dataset.nav === r));
}

function renderView(route) {
  const main = document.getElementById('view');
  if (!main) return;
  switch (route) {
    case 'profile':   return renderProfile(main);
    case 'dashboard': return renderDashboard(main);
    case 'courses':   return renderCourses(main);
    case 'suggested': return renderSuggested(main);
    default:          return renderProfile(main);
  }
}

function renderHeader() {
  const state = getState();
  const header = document.querySelector('.app-header');
  if (!header) return;
  const initial = (state.profile?.rol || 'U').charAt(0).toUpperCase();

  const navLinks = NAV_ITEMS.map((item) =>
    `<a href="#/${item.route}" data-nav="${item.route}" class="${state.route === item.route ? 'is-active' : ''}">
       ${icon(item.icon, 16)} <span>${item.label}</span>
    </a>`
  ).join('');

  header.innerHTML = `
    <div class="app-header__brand">
      <span class="app-header__logo">${icon('layers', 26)}</span>
      <span>CodeCraftHub</span>
    </div>
    <nav class="app-header__nav" id="app-nav">${navLinks}</nav>
    <div class="app-header__right">
      <button class="app-header__theme-btn" id="theme-toggle" aria-label="Cambiar tema" title="Cambiar tema">
        ${icon(currentTheme() === 'light' ? 'moon' : 'sun', 17)}
      </button>
      <div class="header-profile" title="${escapeAttr(state.profile?.rol || 'Tu perfil')}">
        <span class="header-profile__avatar">${initial}</span>
        <span class="header-profile__name">${escapeText(state.profile?.rol || 'Perfil')}</span>
        ${icon('chevron-down', 14, 'header-profile__chevron')}
      </div>
      <button class="app-header__menu-btn" id="menu-toggle" aria-label="Abrir menú">${icon('menu', 22)}</button>
    </div>
  `;

  header.querySelector('#theme-toggle').addEventListener('click', (e) => {
    const next = toggleTheme();
    e.currentTarget.innerHTML = icon(next === 'light' ? 'moon' : 'sun', 17);
  });
  header.querySelector('.header-profile').addEventListener('click', () => navigate('profile'));
  header.querySelector('#menu-toggle')?.addEventListener('click', () =>
    header.querySelector('#app-nav').classList.toggle('is-open')
  );
  header.querySelectorAll('#app-nav a').forEach((a) =>
    a.addEventListener('click', () => header.querySelector('#app-nav').classList.remove('is-open'))
  );
}

function escapeText(s) { return String(s).replace(/[&<>]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;' }[c])); }
function escapeAttr(s) { return escapeText(s).replace(/"/g, '&quot;'); }

// Update XP pill when points change.
subscribe((state) => {
  const pill = document.querySelector('.xp-pill__value');
  if (pill) pill.textContent = state.experiencePoints ?? 0;
  renderActiveNav();
});

export function initRouter() {
  window.addEventListener('hashchange', handleRouteChange);
  renderHeader();
  handleRouteChange();
}
