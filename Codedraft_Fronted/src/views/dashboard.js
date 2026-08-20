// CodeCraftHub — Dashboard view.
// Endpoints: GET /api/profile/points, GET /api/courses/stats,
// GET /api/courses/recommendation, GET /api/courses, GET /api/courses/suggested

import { getState, setState, setLoading } from '../state/store.js';
import {
  getExperiencePoints, getCourseStats, getRecommendedCourse,
  getCourses, getSuggestedCourses,
} from '../services/api.js';
import { icon, showToast, escapeHtml, formatDate, statusLabel, priorityLabel, spinnerHTML, pageHeader, emptyBox, errBox } from '../utils/ui.js';
import { computeLevel, getStudyStreak } from '../utils/gamification.js';
import { navigate } from '../router.js';

export async function renderDashboard(root) {
  root.innerHTML = `
    ${pageHeader({ icon: 'dashboard', title: 'Dashboard', subtitle: 'Tu progreso de aprendizaje de un vistazo.' })}
    <div id="dash-content"><div class="loading-overlay">${spinnerHTML(26, 'Cargando tu dashboard…')}</div></div>
  `;

  const content = root.querySelector('#dash-content');
  setLoading('stats', true);
  try {
    const [stats, xpRes, recommendation, courses] = await Promise.all([
      getCourseStats(), getExperiencePoints(), getRecommendedCourse(), getCourses(),
    ]);
    setState({ stats, experiencePoints: xpRes.experiencePoints ?? getState().experiencePoints, courses });
    content.innerHTML = dashboardHTML(getState(), recommendation);
    bindActions(root);
    loadDashSuggested(root);
  } catch (err) {
    content.innerHTML = errBox('No se pudo cargar el dashboard', err.message);
    showToast(err.message || 'Error al cargar el dashboard.', 'error');
  } finally {
    setLoading('stats', false);
  }
}

function dashboardHTML(state, recommendation) {
  const profile = state.profile || {};
  const stats = state.stats || {};
  const courses = state.courses || [];
  const inProgress = courses.filter((c) => c.status === 'EN_CURSO').sort((a, b) => b.progress - a.progress).slice(0, 4);
  const hasRec = recommendation && recommendation.id;
  const xp = state.experiencePoints ?? 0;
  const level = computeLevel(xp);
  const streak = getStudyStreak();

  return `
    <div class="dash-hero">
      <div class="welcome-card">
        <h2>Hola, ${escapeHtml(profile.rol || 'estudiante')}</h2>
        <p>${inProgress.length > 0 ? `Tienes <strong style="color:var(--teal)">${inProgress.length}</strong> curso(s) en curso. ¡Sigue con ese impulso!` : 'Comienza agregando un curso para ver tu progreso aquí.'}</p>
        <div class="welcome-card__xp">
          <span style="color:var(--gold)">${icon('zap', 26)}</span>
          <div>
            <div class="welcome-card__xp-num">${xp} <span style="font-size:0.9rem;color:var(--gold);font-weight:700">· Nv.${level.level}</span></div>
            <div class="welcome-card__xp-label">Puntos de experiencia</div>
          </div>
          <span class="streak-chip" style="margin-left:auto">${icon('flame', 15)} ${streak} ${streak === 1 ? 'día' : 'días'}</span>
        </div>
      </div>

      <div class="rec-card">
        <div class="rec-card__head">${icon('sparkles', 15)} Recomendado para hoy</div>
        ${hasRec
          ? `<div class="rec-card__name">${escapeHtml(recommendation.name)}</div>
             <div class="rec-card__desc">${escapeHtml(recommendation.description || '')}</div>
             <div class="rec-card__meta">
               <span class="badge ${priorityBadgeClass(recommendation.priority)}"><span class="badge__dot"></span> ${priorityLabel(recommendation.priority)}</span>
               <span class="badge badge--neutral"><span class="badge__dot"></span> ${statusLabel(recommendation.status)}</span>
               ${recommendation.targetDate ? `<span class="chip">${icon('calendar', 12)} ${formatDate(recommendation.targetDate)}</span>` : ''}
             </div>
             <div class="rec-card__cta"><button class="btn btn--primary btn--block" data-action="go-courses">${icon('arrowRight', 15)} Ir a mis cursos</button></div>`
          : `<div class="rec-card__name">¡Todo al día!</div>
             <div class="rec-card__desc">No tienes cursos pendientes. Agrega uno nuevo o explora las sugerencias.</div>
             <div class="rec-card__cta"><button class="btn btn--secondary btn--block" data-action="go-suggested">${icon('sparkles', 15)} Ver cursos sugeridos</button></div>`}
      </div>
    </div>

    <div class="kpi-grid">
      ${kpi('Total cursos', stats.totalCourses ?? 0, 'book', 'primary')}
      ${kpi('En curso', stats.inProgress ?? 0, 'clock', 'warning')}
      ${kpi('Completados', stats.completed ?? 0, 'check', 'success')}
      ${kpi('No iniciados', stats.notStarted ?? 0, 'target', 'neutral')}
      ${kpi('Prioridad alta', stats.highPriority ?? 0, 'flame', 'accent')}
      ${kpi('Progreso medio', `${stats.averageProgress ?? 0}%`, 'trending', 'primary')}
    </div>

    <div class="dash-grid">
      <div class="card">
        <div class="card__header">
          <span class="section-title">${icon('trending', 17)} Cursos en progreso</span>
          <a href="#/courses" class="section-link">Ver todos ${icon('arrowRight', 13)}</a>
        </div>
        <div class="card__body">
          ${inProgress.length
            ? `<div class="mini-list">${inProgress.map(miniItem).join('')}</div>`
            : emptyBox('book', 'Sin cursos en progreso', 'Cuando inicies un curso aparecerá aquí.')}
        </div>
      </div>
      <div class="card">
        <div class="card__header">
          <span class="section-title">${icon('sparkles', 17)} Sugerencias para ti</span>
          <a href="#/suggested" class="section-link">Ver todas ${icon('arrowRight', 13)}</a>
        </div>
        <div class="card__body" id="dash-suggested">
          <div class="spinner-wrap"><span class="spinner" style="width:18px;height:18px"></span><span class="spinner__label">Cargando…</span></div>
        </div>
      </div>
    </div>
  `;
}

function miniItem(c) {
  return `<div class="mini-list__item">
    <div class="mini-list__name">${escapeHtml(c.name)}</div>
    <div class="mini-list__meta">${statusLabel(c.status)} · ${priorityLabel(c.priority)}${c.targetDate ? ' · ' + formatDate(c.targetDate) : ''}</div>
    <div class="mini-list__progress">
      <div class="progress-row"><div class="progress"><div class="progress__bar ${c.progress >= 100 ? 'is-complete' : ''}" style="width:${c.progress}%"></div></div><span>${c.progress}%</span></div>
    </div>
  </div>`;
}

function kpi(label, value, ic, tone) {
  return `<div class="kpi kpi--clickable" data-action="go-courses">
    <div class="kpi__top"><span class="kpi__icon kpi__icon--${tone}">${icon(ic, 18)}</span></div>
    <div class="kpi__value">${value}</div><div class="kpi__label">${label}</div>
  </div>`;
}

function priorityBadgeClass(p) { return { ALTA: 'badge--high', MEDIA: 'badge--medium', BAJA: 'badge--low' }[p] || 'badge--neutral'; }

function bindActions(root) {
  root.querySelectorAll('[data-action]').forEach((el) =>
    el.addEventListener('click', () => {
      const a = el.dataset.action;
      if (a === 'go-courses') navigate('courses');
      if (a === 'go-suggested') navigate('suggested');
    })
  );
}

async function loadDashSuggested(root) {
  const target = root.querySelector('#dash-suggested');
  if (!target) return;
  setLoading('suggested', true);
  try {
    const suggested = await getSuggestedCourses();
    const top = suggested.slice(0, 3);
    target.innerHTML = top.length
      ? `<div class="mini-list">${top.map((c) => `<div class="mini-list__item"><div class="mini-list__name">${escapeHtml(c.name)}</div><div class="mini-list__meta">${(c.tags || []).slice(0, 3).map(escapeHtml).join(' · ')}</div></div>`).join('')}</div>`
      : emptyBox('sparkles', 'Sin sugerencias', 'Completa tu perfil para recibir recomendaciones.');
  } catch (err) {
    target.innerHTML = `<p class="muted">No se pudieron cargar las sugerencias.</p>`;
  } finally {
    setLoading('suggested', false);
  }
}
