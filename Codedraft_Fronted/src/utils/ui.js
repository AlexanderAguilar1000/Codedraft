// CodeCraftHub — UI utilities: toast notifications, modal helpers,
// formatting, and shared presentational helpers.

const STATUS_LABELS = {
  NO_INICIADO: 'No iniciado',
  EN_CURSO: 'En curso',
  COMPLETADO: 'Completado',
};

const PRIORITY_LABELS = {
  ALTA: 'Alta',
  MEDIA: 'Media',
  BAJA: 'Baja',
};

export function statusLabel(value) {
  return STATUS_LABELS[value] || value;
}

export function priorityLabel(value) {
  return PRIORITY_LABELS[value] || value;
}

export function statusOptions() {
  return Object.entries(STATUS_LABELS).map(([value, label]) => ({ value, label }));
}

export function priorityOptions() {
  return Object.entries(PRIORITY_LABELS).map(([value, label]) => ({ value, label }));
}

// Format yyyy-MM-dd into a readable dd MMM yyyy date.
export function formatDate(iso) {
  if (!iso) return '—';
  const d = new Date(iso + 'T00:00:00');
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleDateString('es-ES', { day: '2-digit', month: 'short', year: 'numeric' });
}

// Returns yyyy-MM-dd for today (used as min on date inputs).
export function todayISO() {
  return new Date().toISOString().split('T')[0];
}

// Escape user-provided text before injecting into HTML strings.
export function escapeHtml(str) {
  if (str == null) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

// ---------------------------------------------------------------------------
// Toast notifications
// ---------------------------------------------------------------------------

let toastContainer = null;

function ensureToastContainer() {
  if (toastContainer) return toastContainer;
  toastContainer = document.getElementById('toast-container');
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    toastContainer.className = 'toast-container';
    document.body.appendChild(toastContainer);
  }
  return toastContainer;
}

export function showToast(message, type = 'info', duration = 4000) {
  const container = ensureToastContainer();
  const toast = document.createElement('div');
  toast.className = `toast toast--${type}`;
  const icons = { success: 'check-circle', error: 'alert-circle', info: 'info' };
  const icon = icons[type] || 'info';
  toast.innerHTML = `
    <svg class="toast__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
      ${icon === 'check-circle' ? '<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/>' : ''}
      ${icon === 'alert-circle' ? '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>' : ''}
      ${icon === 'info' ? '<circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>' : ''}
    </svg>
    <span class="toast__message">${escapeHtml(message)}</span>
    <button class="toast__close" aria-label="Cerrar">&times;</button>
  `;
  const close = () => {
    toast.classList.remove('toast--show');
    setTimeout(() => toast.remove(), 300);
  };
  toast.querySelector('.toast__close').addEventListener('click', close);
  container.appendChild(toast);
  requestAnimationFrame(() => toast.classList.add('toast--show'));
  if (duration > 0) setTimeout(close, duration);
  return toast;
}

// ---------------------------------------------------------------------------
// Modal helpers
// ---------------------------------------------------------------------------

export function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.remove();
}

// Close a modal when clicking the backdrop or pressing Escape.
export function wireModalClose(modal, onClose) {
  modal.addEventListener('click', (e) => {
    if (e.target === modal) {
      modal.remove();
      onClose && onClose();
    }
  });
  const escHandler = (e) => {
    if (e.key === 'Escape') {
      modal.remove();
      document.removeEventListener('keydown', escHandler);
      onClose && onClose();
    }
  };
  document.addEventListener('keydown', escHandler);
}

// ---------------------------------------------------------------------------
// Misc UI helpers
// ---------------------------------------------------------------------------

// Small inline SVG icon set used across the app.
export function icon(name, size = 18, className = 'icon') {
  const icons = {
    dashboard: '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/>',
    book: '<path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>',
    sparkles: '<path d="M12 3l1.9 5.1L19 10l-5.1 1.9L12 17l-1.9-5.1L5 10l5.1-1.9z"/><path d="M19 3l.6 1.6L21 5l-1.4.4L19 7l-.6-1.6L17 5l1.4-.4z"/>',
    user: '<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>',
    plus: '<line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>',
    search: '<circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>',
    eye: '<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>',
    edit: '<path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>',
    trash: '<polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>',
    close: '<line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>',
    trophy: '<path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2z"/>',
    target: '<circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/>',
    calendar: '<rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>',
    layers: '<polygon points="12 2 2 7 12 12 22 7 12 2"/><polyline points="2 17 12 22 22 17"/><polyline points="2 12 12 17 22 12"/>',
    clock: '<circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>',
    check: '<polyline points="20 6 9 17 4 12"/>',
    arrowRight: '<line x1="5" y1="12" x2="19" y2="12"/><polyline points="12 5 19 12 12 19"/>',
    flame: '<path d="M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.072-2.143-.224-4.054 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.153.433-2.294 1-3a2.5 2.5 0 0 0 2.5 2.5z"/>',
    logout: '<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/>',
    award: '<circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/>',
    filter: '<polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"/>',
    trending: '<polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/>',
    grid: '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/>',
    list: '<line x1="8" y1="6" x2="21" y2="6"/><line x1="8" y1="12" x2="21" y2="12"/><line x1="8" y1="18" x2="21" y2="18"/><line x1="3" y1="6" x2="3.01" y2="6"/><line x1="3" y1="12" x2="3.01" y2="12"/><line x1="3" y1="18" x2="3.01" y2="18"/>',
    'chevron-down': '<polyline points="6 9 12 15 18 9"/>',
    'chevron-right': '<polyline points="9 18 15 12 9 6"/>',
    menu: '<line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/>',
    zap: '<polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>',
    rocket: '<path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z"/><path d="M12 15l-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"/><path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0"/><path d="M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5"/>',
    bookmark: '<path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>',
    sun: '<circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>',
    moon: '<path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/>',
  };
  return `<svg class="${className}" width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">${icons[name] || ''}</svg>`;
}

// Circular SVG progress ring. `value` 0-100, `size` in px, `strokeWidth` px.
// `tone` optionally colors the bar: 'gold' | 'streak' | 'blue' | 'purple' (defaults to teal).
export function progressRing(value, size = 80, strokeWidth = 6, label = '', tone = '') {
  const r = (size - strokeWidth) / 2;
  const c = 2 * Math.PI * r;
  const pct = Math.max(0, Math.min(100, value));
  const offset = c - (pct / 100) * c;
  return `<div class="ring-wrap ${tone ? 'ring-wrap--' + tone : ''}" style="width:${size}px;height:${size}px">
    <svg width="${size}" height="${size}">
      <circle class="ring-track" cx="${size/2}" cy="${size/2}" r="${r}" stroke-width="${strokeWidth}"/>
      <circle class="ring-bar" cx="${size/2}" cy="${size/2}" r="${r}" stroke-width="${strokeWidth}"
        stroke-dasharray="${c}" stroke-dashoffset="${offset}"/>
    </svg>
    <div class="ring-label" style="font-size:${Math.round(size*0.22)}px">${label || pct + '%'}</div>
  </div>`;
}

// XP ring widget for page headers (value out of a max, e.g. points/100).
export function xpRing(points, size = 44, max = 100) {
  const r = (size - 3) / 2;
  const c = 2 * Math.PI * r;
  const pct = Math.max(0, Math.min(100, (points / max) * 100));
  const offset = c - (pct / 100) * c;
  return `<div class="xp-ring" style="width:${size}px;height:${size}px">
    <svg width="${size}" height="${size}">
      <circle class="xp-ring__track" cx="${size/2}" cy="${size/2}" r="${r}" stroke-width="3"/>
      <circle class="xp-ring__bar" cx="${size/2}" cy="${size/2}" r="${r}" stroke-width="3"
        stroke-dasharray="${c}" stroke-dashoffset="${offset}"/>
    </svg>
    <div class="xp-ring__label">XP</div>
  </div>`;
}

// Skeleton/loading row markup for tables.
export function tableSkeletonHTML(columns) {
  return Array.from({ length: 4 })
    .map(
      () => `<tr class="skeleton-row">
        ${Array.from({ length: columns })
          .map(() => `<td><div class="skeleton-bar"></div></td>`)
          .join('')}
      </tr>`
    )
    .join('');
}

// Decorative sparkline wave shapes for KPI cards (not tied to real historical
// data — the app has no time-series storage for stats yet). Kept as a fixed,
// small set so each tone always renders the same shape.
const KPI_SPARK_PATHS = [
  'M0 24 C10 8,20 8,30 20 C40 32,50 6,60 18 C70 30,80 10,90 22 C100 30,110 14,120 20',
  'M0 20 C12 30,24 4,36 18 C48 32,60 10,72 22 C84 30,96 12,108 20 L120 18',
  'M0 22 C15 6,30 30,45 16 C60 4,75 28,90 14 C100 6,110 20,120 16',
  'M0 18 C10 26,20 10,30 20 C45 34,55 4,70 18 C85 30,95 8,110 20 C115 24,118 18,120 20',
];
const KPI_TONE_SPARK_INDEX = { primary: 0, success: 1, warning: 2, accent: 3, neutral: 0 };

// Shared KPI stat card: icon badge + colored label, big value, a trend row
// and a decorative sparkline. Pass `pct` (0-100, share of a total) to show
// an up-arrow badge; pass `note` instead for a value that's already a
// percentage or otherwise has no meaningful "share of total".
export function kpiCard({ label, value, ic, tone, action = '', pct = null, note = '' }) {
  const trend = pct != null
    ? `<span class="kpi__trend-badge">${icon('trending', 11)} ${pct}%</span><span class="kpi__trend-note">del total</span>`
    : `<span class="kpi__trend-note">${note}</span>`;
  const sparkD = KPI_SPARK_PATHS[KPI_TONE_SPARK_INDEX[tone] ?? 0];
  return `<div class="kpi kpi--${tone}${action ? ' kpi--clickable' : ''}"${action ? ` data-action="${action}"` : ''}>
    <div class="kpi__head">
      <span class="kpi__icon">${icon(ic, 18)}</span>
      <span class="kpi__label">${label}</span>
    </div>
    <div class="kpi__value">${value}</div>
    <div class="kpi__trend">${trend}</div>
    <svg class="kpi__spark" viewBox="0 0 120 34" preserveAspectRatio="none"><path d="${sparkD}"/></svg>
  </div>`;
}

// A generic content spinner.
export function spinnerHTML(size = 24, label = 'Cargando…') {
  return `<div class="spinner-wrap"><span class="spinner" style="width:${size}px;height:${size}px"></span><span class="spinner__label">${escapeHtml(label)}</span></div>`;
}

// Shared page header (icon + title + subtitle, optional right-side content
// like an xp widget). Used by every view so the block isn't rebuilt per-page.
export function pageHeader({ icon: iconName, title, subtitle, right = '', tone = 'teal' }) {
  return `
    <div class="page-head">
      <div class="page-head__left">
        <span class="page-icon-badge page-icon-badge--${tone}">${icon(iconName, 22)}</span>
        <div>
          <h1>${title}</h1>
          <p class="page-head__subtitle">${subtitle}</p>
        </div>
      </div>
      ${right}
    </div>
  `;
}

// Shared empty-state box (e.g. "sin cursos en progreso").
export function emptyBox(iconName, title, desc) {
  return `<div class="empty-state" style="padding:var(--s-8)"><div class="empty-state__icon">${icon(iconName, 26)}</div><h3>${title}</h3><p>${desc}</p></div>`;
}

// Shared error box for failed section loads.
export function errBox(title, msg) {
  return `<div class="empty-state"><div class="empty-state__icon">${icon('close', 26)}</div><h3>${title}</h3><p>${escapeHtml(msg || 'Intenta de nuevo más tarde.')}</p></div>`;
}
