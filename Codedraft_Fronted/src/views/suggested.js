// CodeCraftHub — Suggested Courses view (featured card + grid, photo layout).
// Endpoint: GET /api/courses/suggested

import { getState, setLoading } from '../state/store.js';
import { getSuggestedCourses } from '../services/api.js';
import { icon, showToast, escapeHtml, spinnerHTML, progressRing, pageHeader } from '../utils/ui.js';
import { openAddSuggestedModal } from './courseModals.js';

export async function renderSuggested(root) {
  root.innerHTML = `
    ${pageHeader({
      icon: 'sparkles',
      title: 'Cursos sugeridos',
      subtitle: 'Recomendaciones personalizadas según tu perfil, rol e intereses.',
      tone: 'gold',
    })}
    <div id="suggested-content"><div class="loading-overlay">${spinnerHTML(26, 'Buscando cursos para ti…')}</div></div>
  `;
  const content = root.querySelector('#suggested-content');
  setLoading('suggested', true);
  try {
    const suggested = await getSuggestedCourses();
    content.innerHTML = suggestedHTML(suggested, getState().profile);
    content.querySelectorAll('[data-add-suggested]').forEach((btn) =>
      btn.addEventListener('click', () => {
        const course = suggested[Number(btn.dataset.addSuggested)];
        if (course) openAddSuggestedModal(course);
      })
    );
  } catch (err) {
    content.innerHTML = errBox('No se pudieron cargar las sugerencias', err.message);
    showToast(err.message || 'Error al cargar sugerencias.', 'error');
  } finally {
    setLoading('suggested', false);
  }
}

function suggestedHTML(suggested, profile) {
  if (!suggested.length) {
    return `<div class="empty-state"><div class="empty-state__icon">${icon('sparkles', 26)}</div><h3>Sin sugerencias por ahora</h3><p>Completa tu perfil con tu rol e intereses para recibir recomendaciones personalizadas.</p></div>`;
  }
  const top = suggested[0];
  const maxScore = top?.score || 0;
  const rest = suggested.slice(1);

  return `
    ${profile ? `<div class="card" style="margin-bottom:var(--s-6)"><div class="card__body" style="display:flex;align-items:center;gap:var(--s-4);flex-wrap:wrap">
      ${icon('user', 18)}
      <span class="muted" style="font-size:0.86rem">Sugerencias para <strong style="color:var(--text)">${escapeHtml(profile.rol || 'tu perfil')}</strong> · ${(profile.intereses || []).length} intereses seleccionados</span>
    </div></div>` : ''}

    <div class="catalog-grid">
      ${featuredCardHTML(top, maxScore, 0)}
      ${rest.map((c, i) => cardHTML(c, maxScore, i + 1)).join('')}
    </div>
  `;
}

function featuredCardHTML(course, maxScore, idx) {
  const score = course?.score ?? 0;
  const matchPct = score > 0 ? Math.round((score / (maxScore || 1)) * 100) : 0;
  return `
    <article class="catalog-card catalog-card--featured">
      <div class="catalog-card__content">
        <div class="rec-card__head" style="margin-bottom:var(--s-3)">${icon('sparkles', 15)} Recomendación destacada</div>
        <h2 class="catalog-card__name" style="font-size:1.4rem;padding-right:0">${escapeHtml(course.name)}</h2>
        <p class="catalog-card__desc" style="font-size:0.92rem">${escapeHtml(course.description || '')}</p>
        <div class="catalog-card__tags">
          ${(course.tags || []).map((t) => `<span class="tag-pill">${escapeHtml(t)}</span>`).join('')}
        </div>
        <div class="catalog-card__roles" style="margin-bottom:var(--s-4)">Para: ${(course.roles || []).slice(0, 3).map(escapeHtml).join(' · ')}</div>
        <div style="margin-top:auto">
          <button class="btn btn--success" data-add-suggested="${idx}">${icon('plus', 15)} Añadir a mis cursos</button>
        </div>
      </div>
      <div class="catalog-card__visual">
        ${progressRing(matchPct, 100, 7, `${matchPct}%`)}
        <div style="margin-top:var(--s-2);font-size:0.72rem;color:var(--text-muted);text-transform:uppercase;letter-spacing:0.05em">Afinidad</div>
      </div>
    </article>
  `;
}

function cardHTML(course, maxScore, idx) {
  const score = course.score ?? 0;
  const matchPct = score > 0 ? Math.round((score / (maxScore || 1)) * 100) : 0;
  const isStrong = score > 0 && score >= maxScore * 0.6;
  return `
    <article class="catalog-card">
      ${score > 0 ? `<span class="catalog-card__match ${isStrong ? 'is-strong' : ''}">${matchPct}% match</span>` : ''}
      <button class="catalog-card__add" data-add-suggested="${idx}" title="Añadir">${icon('plus', 14)}</button>
      <h3 class="catalog-card__name">${escapeHtml(course.name)}</h3>
      <p class="catalog-card__desc">${escapeHtml(course.description || '')}</p>
      <div class="catalog-card__tags">
        ${(course.tags || []).slice(0, 3).map((t) => `<span class="tag-pill">${escapeHtml(t)}</span>`).join('')}
      </div>
      <div class="catalog-card__roles">Para: ${(course.roles || []).slice(0, 3).map(escapeHtml).join(', ')}</div>
      <div class="catalog-card__cta">
        <button class="btn btn--success btn--block btn--sm" data-add-suggested="${idx}">${icon('plus', 14)} Añadir a mis cursos</button>
      </div>
    </article>
  `;
}

function errBox(title, msg) {
  return `<div class="empty-state"><div class="empty-state__icon">${icon('close', 26)}</div><h3>${title}</h3><p>${escapeHtml(msg || 'Intenta de nuevo más tarde.')}</p></div>`;
}
