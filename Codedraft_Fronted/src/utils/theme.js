// CodeCraftHub — light/dark theme switch, persisted in localStorage.
// The initial value is applied synchronously by an inline script in
// index.html (before CSS paints) to avoid a flash of the wrong theme.

const STORAGE_KEY = 'codecrafthub-theme';

export function currentTheme() {
  return document.documentElement.getAttribute('data-theme') === 'light' ? 'light' : 'dark';
}

export function applyTheme(theme) {
  document.documentElement.setAttribute('data-theme', theme);
  try { localStorage.setItem(STORAGE_KEY, theme); } catch { /* storage unavailable */ }
}

export function toggleTheme() {
  const next = currentTheme() === 'light' ? 'dark' : 'light';
  applyTheme(next);
  return next;
}
