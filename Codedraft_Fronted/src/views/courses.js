// CodeCraftHub — Course Management view.
// Endpoints: GET /api/courses, GET /api/courses/stats, GET /api/courses/search,
// PATCH /api/courses/{id}/status, /priority, /target-date, /progress

import { getState, setState, setLoading } from '../state/store.js';
import {
  getCourses, getCourseStats, searchCourses,
  updateCourseStatus, updateCoursePriority, updateCourseTargetDate, updateCourseProgress,
  getExperiencePoints,
} from '../services/api.js';
import {
  icon, showToast, escapeHtml, formatDate, statusLabel, priorityLabel,
  statusOptions, priorityOptions, tableSkeletonHTML,
} from '../utils/ui.js';
import { openAddModal, openViewModal, openEditModal, openDeleteModal } from './courseModals.js';

let searchTimer = null;

export async function renderCourses(root) {
  root.innerHTML = `
    <div class="page-head">
      <div class="page-head__left">
        <h1>${icon('book', 22, 'page-icon')} Mis cursos</h1>
        <p class="page-head__subtitle">Gestiona tu lista de cursos, añade nuevos y haz seguimiento de tu progreso.</p>
      </div>
      <button class="btn btn--primary" id="add-course-btn">${icon('plus', 15)} <span>Añadir curso</span></button>
    </div>
    <div id="courses-kpis" class="kpi-grid"></div>
    <div class="card">
      <div class="card__header"><span class="card__title">Lista de cursos</span><span class="result-count" id="courses-count"></span></div>
      <div class="card__body" style="padding:var(--s-5)">
        <div class="courses-toolbar">
          <div class="search-input">${icon('search', 15)}<input type="search" id="search-name" placeholder="Buscar por nombre…" /></div>
          <select id="filter-status" class="filter-select">
            <option value="">Todos los estados</option>
            ${statusOptions().map((o) => `<option value="${o.value}">${o.label}</option>`).join('')}
          </select>
          <select id="filter-priority" class="filter-select">
            <option value="">Todas las prioridades</option>
            ${priorityOptions().map((o) => `<option value="${o.value}">${o.label}</option>`).join('')}
          </select>
          <div class="courses-toolbar__right">
            <button class="btn btn--ghost btn--sm" id="clear-filters">${icon('close', 13)} Limpiar</button>
          </div>
        </div>
        <div class="table-wrap">
          <table class="data-table" id="courses-table">
            <thead><tr><th>Curso</th><th>Estado</th><th>Prioridad</th><th>Fecha objetivo</th><th>Progreso</th><th class="col-actions">Acciones</th></tr></thead>
            <tbody id="courses-tbody">${tableSkeletonHTML(6)}</tbody>
          </table>
        </div>
      </div>
    </div>
  `;
  bindToolbar(root);
  root.querySelector('#add-course-btn').addEventListener('click', openAddModal);
  await loadCourses(root);
}

async function loadCourses(root) {
  setLoading('courses', true);
  const tbody = root.querySelector('#courses-tbody');
  try {
    const [courses, stats] = await Promise.all([getCourses(), getCourseStats()]);
    setState({ courses, stats });
    renderKPIs(root, stats);
    renderTable(root, courses);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6">${errBox('No se pudieron cargar los cursos', err.message)}</td></tr>`;
    showToast(err.message || 'Error al cargar los cursos.', 'error');
  } finally {
    setLoading('courses', false);
  }
}

// Re-render after a mutation.
export async function refreshCourses() {
  const root = document.getElementById('view');
  if (!root) return;
  await loadCourses(root);
}

function renderKPIs(root, stats) {
  root.querySelector('#courses-kpis').innerHTML = `
    ${miniKPI('Total', stats.totalCourses ?? 0, 'book', 'primary')}
    ${miniKPI('En curso', stats.inProgress ?? 0, 'clock', 'warning')}
    ${miniKPI('Completados', stats.completed ?? 0, 'check', 'success')}
    ${miniKPI('No iniciados', stats.notStarted ?? 0, 'target', 'neutral')}
  `;
}
function miniKPI(label, value, ic, tone) {
  return `<div class="kpi"><div class="kpi__top"><span class="kpi__icon kpi__icon--${tone}">${icon(ic, 18)}</span></div><div class="kpi__value">${value}</div><div class="kpi__label">${label}</div></div>`;
}

function renderTable(root, courses) {
  const tbody = root.querySelector('#courses-tbody');
  const countEl = root.querySelector('#courses-count');
  if (countEl) countEl.textContent = `${courses.length} curso(s)`;
  const f = getState().filters;

  if (!courses.length) {
    const hasFilters = f.name || f.status || f.priority;
    tbody.innerHTML = `<tr><td colspan="6"><div class="empty-state">
      <div class="empty-state__icon">${icon('book', 26)}</div>
      <h3>${hasFilters ? 'Sin resultados' : 'No hay cursos'}</h3>
      <p>${hasFilters ? 'Ningún curso coincide con los filtros aplicados.' : 'Añade tu primer curso para empezar a gestionar tu aprendizaje.'}</p>
      ${!hasFilters ? `<button class="btn btn--primary" style="margin-top:var(--s-5)" id="empty-add">${icon('plus', 15)} Añadir curso</button>` : ''}
    </div></td></tr>`;
    tbody.querySelector('#empty-add')?.addEventListener('click', openAddModal);
    return;
  }

  tbody.innerHTML = courses.map((c) => rowHTML(c)).join('');
  // Inline selects + date + progress slider
  tbody.querySelectorAll('[data-status-select]').forEach(bindStatusSelect);
  tbody.querySelectorAll('[data-priority-select]').forEach(bindPrioritySelect);
  tbody.querySelectorAll('[data-date-input]').forEach(bindDateInput);
  tbody.querySelectorAll('[data-progress-slider]').forEach(bindProgressSlider);
  // Action buttons
  tbody.querySelectorAll('[data-view]').forEach((b) => b.addEventListener('click', () => openViewModal(b.dataset.view)));
  tbody.querySelectorAll('[data-edit]').forEach((b) => b.addEventListener('click', () => openEditModal(b.dataset.edit)));
  tbody.querySelectorAll('[data-delete]').forEach((b) => b.addEventListener('click', () => {
    const course = getState().courses.find((c) => c.id === b.dataset.delete);
    if (course) openDeleteModal(course);
  }));
}

function rowHTML(c) {
  return `<tr data-course-id="${c.id}">
    <td><div class="col-name">${escapeHtml(c.name)}</div><div class="col-desc">${escapeHtml(c.description || 'Sin descripción')}</div></td>
    <td><select class="inline-select" data-status-select="${c.id}" data-original="${c.status}">
      ${statusOptions().map((o) => `<option value="${o.value}" ${o.value === c.status ? 'selected' : ''}>${o.label}</option>`).join('')}
    </select></td>
    <td><select class="inline-select" data-priority-select="${c.id}" data-original="${c.priority}">
      ${priorityOptions().map((o) => `<option value="${o.value}" ${o.value === c.priority ? 'selected' : ''}>${o.label}</option>`).join('')}
    </select></td>
    <td><input type="date" class="inline-select" data-date-input="${c.id}" value="${c.targetDate || ''}" /></td>
    <td>
      <div class="progress-row">
        <div class="progress"><div class="progress__bar ${c.progress >= 100 ? 'is-complete' : ''}" style="width:${c.progress}%"></div></div>
        <span data-progress-value="${c.id}">${c.progress}%</span>
        <button class="action-btn" data-progress-slider="${c.id}" title="Ajustar progreso">${icon('trending', 14)}</button>
      </div>
    </td>
    <td class="col-actions">
      <button class="action-btn action-btn--view" data-view="${c.id}" title="Ver detalle" aria-label="Ver">${icon('eye', 15)}</button>
      <button class="action-btn action-btn--edit" data-edit="${c.id}" title="Editar" aria-label="Editar">${icon('edit', 15)}</button>
      <button class="action-btn action-btn--delete" data-delete="${c.id}" title="Eliminar" aria-label="Eliminar">${icon('trash', 15)}</button>
    </td>
  </tr>`;
}

function bindStatusSelect(sel) {
  sel.addEventListener('change', async () => {
    const id = sel.dataset.statusSelect;
    sel.disabled = true;
    try {
      const updated = await updateCourseStatus(id, sel.value);
      updateRowInState(updated);
      refreshRow(sel, updated);
      showToast('Estado actualizado.', 'success');
    } catch (err) {
      sel.value = sel.dataset.original;
      showToast(err.message || 'No se pudo actualizar el estado.', 'error');
    } finally { sel.disabled = false; }
  });
}

function bindPrioritySelect(sel) {
  sel.addEventListener('change', async () => {
    const id = sel.dataset.prioritySelect;
    sel.disabled = true;
    try {
      const updated = await updateCoursePriority(id, sel.value);
      updateRowInState(updated);
      refreshRow(sel, updated);
      showToast('Prioridad actualizada.', 'success');
    } catch (err) {
      sel.value = sel.dataset.original;
      showToast(err.message || 'No se pudo actualizar la prioridad.', 'error');
    } finally { sel.disabled = false; }
  });
}

function bindDateInput(inp) {
  inp.addEventListener('change', async () => {
    const id = inp.dataset.dateInput;
    inp.disabled = true;
    try {
      const updated = await updateCourseTargetDate(id, inp.value || null);
      updateRowInState(updated);
      showToast('Fecha objetivo actualizada.', 'success');
    } catch (err) {
      showToast(err.message || 'No se pudo actualizar la fecha.', 'error');
    } finally { inp.disabled = false; }
  });
}

function bindProgressSlider(btn) {
  btn.addEventListener('click', () => {
    const id = btn.dataset.progressSlider;
    const course = getState().courses.find((c) => c.id === id);
    if (course) openProgressModal(course);
  });
}

function openProgressModal(course) {
  const modal = document.createElement('div');
  modal.className = 'modal-backdrop';
  modal.id = 'modal-progress';
  modal.innerHTML = `
    <div class="modal modal--sm" role="dialog" aria-modal="true">
      <div class="modal__header"><span class="modal__title">${icon('trending', 18)} Progreso — ${escapeHtml(course.name)}</span>
        <button class="modal__close" data-close>${icon('close', 16)}</button></div>
      <div class="modal__body">
        <div class="field">
          <label class="field__label">Progreso: <span id="prog-val" class="range-value">${course.progress}%</span></label>
          <div class="range-row"><input type="range" id="prog-range" min="0" max="100" step="5" value="${course.progress}" /></div>
          <span class="field__hint">Al llegar a 100% el curso se marca como completado y sumas puntos de experiencia.</span>
        </div>
      </div>
      <div class="modal__footer">
        <button class="btn btn--ghost" data-close>Cancelar</button>
        <button class="btn btn--primary" id="prog-save">${icon('check', 15)} Guardar</button>
      </div>
    </div>`;
  document.body.appendChild(modal);
  const close = () => modal.remove();
  modal.addEventListener('click', (e) => { if (e.target === modal) close(); });
  modal.querySelectorAll('[data-close]').forEach((b) => b.addEventListener('click', close));
  const range = modal.querySelector('#prog-range');
  const val = modal.querySelector('#prog-val');
  range.addEventListener('input', () => (val.textContent = `${range.value}%`));
  modal.querySelector('#prog-save').addEventListener('click', async () => {
    const saveBtn = modal.querySelector('#prog-save');
    saveBtn.disabled = true;
    saveBtn.innerHTML = `<span class="spinner" style="width:14px;height:14px"></span> Guardando…`;
    try {
      const updated = await updateCourseProgress(course.id, parseInt(range.value, 10));
      updateRowInState(updated);
      // Refresh XP from backend
      try { const xp = await getExperiencePoints(); setState({ experiencePoints: xp.experiencePoints ?? 0 }); } catch {}
      showToast(`Progreso actualizado a ${updated.progress}%.`, 'success');
      if (updated.status === 'COMPLETADO' && course.status !== 'COMPLETADO') {
        showToast('¡Curso completado! +5 puntos de experiencia.', 'success', 5000);
      }
      close();
      await refreshCourses();
    } catch (err) {
      showToast(err.message || 'No se pudo actualizar el progreso.', 'error');
      saveBtn.disabled = false;
      saveBtn.innerHTML = `${icon('check', 15)} Guardar`;
    }
  });
}

function updateRowInState(updated) {
  const courses = getState().courses.map((c) => (c.id === updated.id ? updated : c));
  setState({ courses });
}

function refreshRow(sel, updated) {
  const row = sel.closest('tr');
  if (!row) return;
  const bar = row.querySelector('.progress__bar');
  const val = row.querySelector('[data-progress-value]');
  if (bar) { bar.style.width = `${updated.progress}%`; bar.classList.toggle('is-complete', updated.progress >= 100); }
  if (val) val.textContent = `${updated.progress}%`;
  // Update status select options to reflect backend change (progress 100 → COMPLETADO)
  const statusSel = row.querySelector('[data-status-select]');
  if (statusSel) { statusSel.value = updated.status; statusSel.dataset.original = updated.status; }
}

function bindToolbar(root) {
  const nameInput = root.querySelector('#search-name');
  const statusSel = root.querySelector('#filter-status');
  const prioritySel = root.querySelector('#filter-priority');
  const clearBtn = root.querySelector('#clear-filters');
  nameInput.addEventListener('input', () => { clearTimeout(searchTimer); searchTimer = setTimeout(() => applyFilters(root), 300); });
  statusSel.addEventListener('change', () => applyFilters(root));
  prioritySel.addEventListener('change', () => applyFilters(root));
  clearBtn.addEventListener('click', () => { nameInput.value = ''; statusSel.value = ''; prioritySel.value = ''; applyFilters(root); });
}

async function applyFilters(root) {
  const name = root.querySelector('#search-name').value.trim();
  const status = root.querySelector('#filter-status').value;
  const priority = root.querySelector('#filter-priority').value;
  setState({ filters: { name, status, priority } });
  const tbody = root.querySelector('#courses-tbody');
  tbody.innerHTML = tableSkeletonHTML(6);
  const filters = {};
  if (name) filters.name = name;
  if (status) filters.status = status;
  if (priority) filters.priority = priority;
  try {
    const results = Object.keys(filters).length ? await searchCourses(filters) : await getCourses();
    renderTable(root, results);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6">${errBox('Error en la búsqueda', err.message)}</td></tr>`;
    showToast(err.message || 'Error al filtrar.', 'error');
  }
}

function errBox(title, msg) {
  return `<div class="empty-state"><div class="empty-state__icon">${icon('close', 26)}</div><h3>${title}</h3><p>${escapeHtml(msg || 'Intenta de nuevo.')}</p></div>`;
}
