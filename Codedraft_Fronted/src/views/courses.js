// CodeCraftHub — Course Management view.
// Endpoints: GET /api/courses, GET /api/courses/stats, GET /api/courses/search,
// PATCH /api/courses/{id}/priority, /target-date

import { getState, setState, setLoading } from '../state/store.js';
import {
  getCourses, getCourseStats, searchCourses,
  updateCoursePriority, updateCourseTargetDate,
} from '../services/api.js';
import {
  icon, showToast, escapeHtml, formatDate, statusLabel, priorityLabel,
  statusOptions, priorityOptions, tableSkeletonHTML, errBox, kpiCard, pageHeader,
} from '../utils/ui.js';
import {
  openAddModal, openViewModal, openEditModal, openDeleteModal, openProgressModal,
} from './courseModals.js';

let searchTimer = null;

export async function renderCourses(root) {
  root.innerHTML = `
    ${pageHeader({
      icon: 'book',
      title: 'Mis cursos',
      subtitle: 'Gestiona tu lista de cursos, añade nuevos y haz seguimiento de tu progreso.',
      right: `<button class="btn btn--primary" id="add-course-btn">${icon('plus', 15)} <span>Añadir curso</span></button>`,
      tone: 'blue',
    })}
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
            <thead><tr><th>Curso</th><th>Prioridad</th><th>Fecha objetivo</th><th>Progreso</th><th class="col-actions">Acciones</th></tr></thead>
            <tbody id="courses-tbody">${tableSkeletonHTML(5)}</tbody>
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
    tbody.innerHTML = `<tr><td colspan="5">${errBox('No se pudieron cargar los cursos', err.message)}</td></tr>`;
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
  const total = stats.totalCourses ?? 0;
  root.querySelector('#courses-kpis').innerHTML = `
    ${kpiCard({ label: 'Total', value: total, ic: 'book', tone: 'primary', note: 'Cursos registrados' })}
    ${kpiCard({ label: 'En curso', value: stats.inProgress ?? 0, ic: 'clock', tone: 'warning', pct: sharePct(stats.inProgress, total) })}
    ${kpiCard({ label: 'Completados', value: stats.completed ?? 0, ic: 'check', tone: 'success', pct: sharePct(stats.completed, total) })}
    ${kpiCard({ label: 'No iniciados', value: stats.notStarted ?? 0, ic: 'target', tone: 'neutral', pct: sharePct(stats.notStarted, total) })}
  `;
}
// Share of the total courses this count represents (0-100). Real, derived
// from data already fetched — not a fabricated time-based trend.
function sharePct(n, total) { return total ? Math.round(((n ?? 0) / total) * 100) : 0; }

function renderTable(root, courses) {
  const tbody = root.querySelector('#courses-tbody');
  const countEl = root.querySelector('#courses-count');
  if (countEl) countEl.textContent = `${courses.length} curso(s)`;
  const f = getState().filters;

  if (!courses.length) {
    const hasFilters = f.name || f.status || f.priority;
    tbody.innerHTML = `<tr><td colspan="5"><div class="empty-state">
      <div class="empty-state__icon">${icon('book', 26)}</div>
      <h3>${hasFilters ? 'Sin resultados' : 'No hay cursos'}</h3>
      <p>${hasFilters ? 'Ningún curso coincide con los filtros aplicados.' : 'Añade tu primer curso para empezar a gestionar tu aprendizaje.'}</p>
      ${!hasFilters ? `<button class="btn btn--primary" style="margin-top:var(--s-5)" id="empty-add">${icon('plus', 15)} Añadir curso</button>` : ''}
    </div></td></tr>`;
    tbody.querySelector('#empty-add')?.addEventListener('click', openAddModal);
    return;
  }

  tbody.innerHTML = courses.map((c) => rowHTML(c)).join('');
  // Inline selects + date
  tbody.querySelectorAll('[data-priority-select]').forEach(bindPrioritySelect);
  tbody.querySelectorAll('[data-date-input]').forEach(bindDateInput);
  // Action buttons
  tbody.querySelectorAll('[data-progress]').forEach((b) => b.addEventListener('click', () => {
    const course = getState().courses.find((c) => c.id === b.dataset.progress);
    if (course) openProgressModal(course);
  }));
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
    <td><select class="inline-select ${priorityToneClass(c.priority)}" data-priority-select="${c.id}" data-original="${c.priority}">
      ${priorityOptions().map((o) => `<option value="${o.value}" ${o.value === c.priority ? 'selected' : ''}>${o.label}</option>`).join('')}
    </select></td>
    <td><input type="date" class="inline-select" data-date-input="${c.id}" value="${c.targetDate || ''}" /></td>
    <td>
      <div class="progress-row">
        <div class="progress"><div class="progress__bar ${c.progress >= 100 ? 'is-complete' : ''}" style="width:${c.progress}%"></div></div>
        <span data-progress-value="${c.id}">${c.progress}%</span>
      </div>
    </td>
    <td class="col-actions">
      <div class="action-group">
        <button class="action-btn action-btn--progress" data-progress="${c.id}" title="Registrar progreso" aria-label="Registrar progreso">${icon('trending', 15)}</button>
        <button class="action-btn action-btn--view" data-view="${c.id}" title="Ver detalle" aria-label="Ver">${icon('eye', 15)}</button>
        <button class="action-btn action-btn--edit" data-edit="${c.id}" title="Editar" aria-label="Editar">${icon('edit', 15)}</button>
        <button class="action-btn action-btn--delete" data-delete="${c.id}" title="Eliminar" aria-label="Eliminar">${icon('trash', 15)}</button>
      </div>
    </td>
  </tr>`;
}

function priorityToneClass(p) { return { ALTA: 'inline-select--high', MEDIA: 'inline-select--medium', BAJA: 'inline-select--low' }[p] || ''; }

function bindPrioritySelect(sel) {
  sel.addEventListener('change', async () => {
    const id = sel.dataset.prioritySelect;
    sel.disabled = true;
    try {
      const updated = await updateCoursePriority(id, sel.value);
      updateRowInState(updated);
      refreshRow(sel, updated);
      sel.classList.remove('inline-select--high', 'inline-select--medium', 'inline-select--low');
      sel.classList.add(priorityToneClass(updated.priority));
      sel.dataset.original = updated.priority;
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
  tbody.innerHTML = tableSkeletonHTML(5);
  const filters = {};
  if (name) filters.name = name;
  if (status) filters.status = status;
  if (priority) filters.priority = priority;
  try {
    const results = Object.keys(filters).length ? await searchCourses(filters) : await getCourses();
    renderTable(root, results);
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5">${errBox('Error en la búsqueda', err.message)}</td></tr>`;
    showToast(err.message || 'Error al filtrar.', 'error');
  }
}
