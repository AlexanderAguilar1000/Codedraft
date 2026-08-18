// CodeCraftHub — Course modals (add, view, edit, delete).
// Endpoints: POST /api/courses, GET /api/courses/{id},
// PATCH /api/courses/{id}/update, DELETE /api/courses/{id}

import { createCourse, updateCourse, deleteCourse, getCourse } from '../services/api.js';
import { getState, setLoading } from '../state/store.js';
import {
  icon, showToast, escapeHtml, wireModalClose,
  formatDate, statusLabel, priorityLabel, priorityOptions,
} from '../utils/ui.js';
import { refreshCourses } from './courses.js';

// ADD -----------------------------------------------------------------------
export function openAddModal() {
  const modal = createModal('add', 'Añadir nuevo curso', 'plus');
  modal.querySelector('.modal__body').innerHTML = courseFormHTML(null);
  modal.querySelector('.modal__footer').innerHTML = `
    <button class="btn btn--ghost" data-close>Cancelar</button>
    <button class="btn btn--primary" id="add-submit">${icon('check', 15)} <span>Guardar curso</span></button>`;
  document.body.appendChild(modal);
  wireModalClose(modal);
  bindClose(modal);
  modal.querySelector('#add-submit').addEventListener('click', () => submitAdd(modal));
  modal.querySelector('#course-name').focus();
}

async function submitAdd(modal) {
  const form = modal.querySelector('#course-form');
  const data = readForm(form);
  const v = validate(data);
  if (!v.ok) { showToast(v.message, 'error'); markErrors(form, v.fields); return; }
  const btn = modal.querySelector('#add-submit');
  btn.disabled = true;
  btn.innerHTML = `<span class="spinner" style="width:14px;height:14px"></span> <span>Guardando…</span>`;
  setLoading('saving', true);
  try {
    await createCourse(data);
    showToast('Curso creado correctamente.', 'success');
    modal.remove();
    await refreshCourses();
  } catch (err) {
    showToast(err.message || 'No se pudo crear el curso.', 'error');
    btn.disabled = false;
    btn.innerHTML = `${icon('check', 15)} <span>Guardar curso</span>`;
  } finally { setLoading('saving', false); }
}

// VIEW ----------------------------------------------------------------------
export async function openViewModal(id) {
  const modal = createModal('view', 'Detalle del curso', 'eye', 'modal--lg');
  modal.querySelector('.modal__body').innerHTML = `<div class="loading-overlay"><span class="spinner" style="width:22px;height:22px"></span></div>`;
  modal.querySelector('.modal__footer').innerHTML = `
    <button class="btn btn--secondary" data-action="edit-from-view">${icon('edit', 15)} Editar</button>
    <button class="btn btn--ghost" data-close>Cerrar</button>`;
  document.body.appendChild(modal);
  wireModalClose(modal);
  bindClose(modal);
  try {
    const course = await getCourse(id);
    modal.querySelector('.modal__body').innerHTML = courseDetailHTML(course);
    modal.querySelector('[data-action="edit-from-view"]').addEventListener('click', () => { modal.remove(); openEditModal(id); });
  } catch (err) {
    modal.querySelector('.modal__body').innerHTML = errDetail(err.message);
    showToast(err.message || 'No se pudo cargar el curso.', 'error');
  }
}

// EDIT ----------------------------------------------------------------------
export async function openEditModal(id) {
  const modal = createModal('edit', 'Editar curso', 'edit', 'modal--lg');
  modal.querySelector('.modal__body').innerHTML = `<div class="loading-overlay"><span class="spinner" style="width:22px;height:22px"></span></div>`;
  modal.querySelector('.modal__footer').innerHTML = `
    <button class="btn btn--ghost" data-close>Cancelar</button>
    <button class="btn btn--primary" id="edit-submit">${icon('check', 15)} <span>Guardar cambios</span></button>`;
  document.body.appendChild(modal);
  wireModalClose(modal);
  bindClose(modal);
  try {
    const course = await getCourse(id);
    modal.querySelector('.modal__body').innerHTML = courseFormHTML(course);
    modal.querySelector('#edit-submit').addEventListener('click', () => submitEdit(modal, id));
  } catch (err) {
    modal.querySelector('.modal__body').innerHTML = errDetail(err.message);
    showToast(err.message || 'No se pudo cargar el curso.', 'error');
  }
}

async function submitEdit(modal, id) {
  const form = modal.querySelector('#course-form');
  const data = readForm(form);
  const v = validate(data);
  if (!v.ok) { showToast(v.message, 'error'); markErrors(form, v.fields); return; }
  const btn = modal.querySelector('#edit-submit');
  btn.disabled = true;
  btn.innerHTML = `<span class="spinner" style="width:14px;height:14px"></span> <span>Guardando…</span>`;
  setLoading('saving', true);
  try {
    await updateCourse(id, data);
    showToast('Curso actualizado correctamente.', 'success');
    modal.remove();
    await refreshCourses();
  } catch (err) {
    showToast(err.message || 'No se pudo actualizar el curso.', 'error');
    btn.disabled = false;
    btn.innerHTML = `${icon('check', 15)} <span>Guardar cambios</span>`;
  } finally { setLoading('saving', false); }
}

// DELETE --------------------------------------------------------------------
export function openDeleteModal(course) {
  const modal = createModal('delete', 'Eliminar curso', 'trash', 'modal--sm');
  modal.querySelector('.modal__body').innerHTML = `
    <p style="margin-bottom:var(--s-3)">¿Seguro que quieres eliminar <strong style="color:var(--heading)">${escapeHtml(course.name)}</strong>?</p>
    <p class="muted" style="font-size:0.84rem">Esta acción no se puede deshacer.</p>`;
  modal.querySelector('.modal__footer').innerHTML = `
    <button class="btn btn--ghost" data-close>Cancelar</button>
    <button class="btn btn--danger-solid" id="delete-submit">${icon('trash', 15)} <span>Eliminar</span></button>`;
  document.body.appendChild(modal);
  wireModalClose(modal);
  bindClose(modal);
  modal.querySelector('#delete-submit').addEventListener('click', () => submitDelete(modal, course.id));
}

async function submitDelete(modal, id) {
  const btn = modal.querySelector('#delete-submit');
  btn.disabled = true;
  btn.innerHTML = `<span class="spinner" style="width:14px;height:14px"></span> <span>Eliminando…</span>`;
  setLoading('saving', true);
  try {
    await deleteCourse(id);
    showToast('Curso eliminado correctamente.', 'success');
    modal.remove();
    await refreshCourses();
  } catch (err) {
    showToast(err.message || 'No se pudo eliminar el curso.', 'error');
    btn.disabled = false;
    btn.innerHTML = `${icon('trash', 15)} <span>Eliminar</span>`;
  } finally { setLoading('saving', false); }
}

// Shared helpers ------------------------------------------------------------
function createModal(id, title, ic, size = '') {
  const div = document.createElement('div');
  div.className = 'modal-backdrop';
  div.id = `modal-${id}`;
  div.innerHTML = `
    <div class="modal ${size}" role="dialog" aria-modal="true" aria-label="${escapeHtml(title)}">
      <div class="modal__header">
        <span class="modal__title">${icon(ic, 18)} ${escapeHtml(title)}</span>
        <button class="modal__close" data-close aria-label="Cerrar">${icon('close', 16)}</button>
      </div>
      <div class="modal__body"></div>
      <div class="modal__footer"></div>
    </div>`;
  return div;
}

function bindClose(modal) {
  modal.querySelectorAll('[data-close]').forEach((b) => b.addEventListener('click', () => modal.remove()));
}

function courseFormHTML(course) {
  const c = course || {};
  return `
    <form id="course-form" novalidate>
      <div class="form-grid">
        <div class="field full">
          <label class="field__label" for="course-name">Nombre del curso<span class="req">*</span></label>
          <input type="text" id="course-name" name="name" value="${escapeHtml(c.name || '')}" placeholder="Ej. Spring Boot desde cero" required maxlength="120" />
          <span class="field__error">El nombre es obligatorio.</span>
        </div>
        <div class="field full">
          <label class="field__label" for="course-description">Descripción</label>
          <textarea id="course-description" name="description" placeholder="Breve descripción del contenido" maxlength="500">${escapeHtml(c.description || '')}</textarea>
        </div>
        <div class="field">
          <label class="field__label" for="course-priority">Prioridad</label>
          <select id="course-priority" name="priority">
            ${priorityOptions().map((o) => `<option value="${o.value}" ${o.value === c.priority ? 'selected' : ''}>${o.label}</option>`).join('')}
          </select>
        </div>
        <div class="field">
          <label class="field__label" for="course-targetDate">Fecha objetivo</label>
          <input type="date" id="course-targetDate" name="targetDate" value="${c.targetDate || ''}" />
        </div>
      </div>
    </form>`;
}

function courseDetailHTML(c) {
  return `<div class="detail-grid">
    <div class="detail-item full"><span class="detail-item__label">Nombre</span><span class="detail-item__value">${escapeHtml(c.name)}</span></div>
    <div class="detail-item full"><span class="detail-item__label">Descripción</span><span class="detail-item__value ${c.description ? '' : 'muted'}">${escapeHtml(c.description) || 'Sin descripción'}</span></div>
    <div class="detail-item"><span class="detail-item__label">Estado</span><span><span class="badge ${statusBadgeClass(c.status)}"><span class="badge__dot"></span> ${statusLabel(c.status)}</span></span></div>
    <div class="detail-item"><span class="detail-item__label">Prioridad</span><span><span class="badge ${priorityBadgeClass(c.priority)}"><span class="badge__dot"></span> ${priorityLabel(c.priority)}</span></span></div>
    <div class="detail-item"><span class="detail-item__label">Fecha objetivo</span><span class="detail-item__value">${formatDate(c.targetDate)}</span></div>
    <div class="detail-item"><span class="detail-item__label">Progreso</span>
      <div class="progress-row" style="margin-top:4px"><div class="progress" style="width:140px"><div class="progress__bar ${c.progress >= 100 ? 'is-complete' : ''}" style="width:${c.progress}%"></div></div><span>${c.progress}%</span></div>
    </div>
  </div>`;
}

function errDetail(msg) {
  return `<div class="empty-state"><div class="empty-state__icon">${icon('close', 26)}</div><h3>No se pudo cargar</h3><p>${escapeHtml(msg || 'Intenta de nuevo.')}</p></div>`;
}

function readForm(form) {
  return {
    name: form.name.value.trim(),
    description: form.description.value.trim(),
    priority: form.priority.value,
    targetDate: form.targetDate.value || null,
  };
}

function validate(data) {
  if (!data.name) return { ok: false, message: 'El nombre del curso es obligatorio.', fields: ['name'] };
  return { ok: true };
}

function markErrors(form, fields) {
  form.querySelectorAll('.field').forEach((f) => f.classList.remove('has-error'));
  fields.forEach((n) => form.querySelector(`[name="${n}"]`)?.closest('.field')?.classList.add('has-error'));
}

function statusBadgeClass(s) { return { COMPLETADO: 'badge--success', EN_CURSO: 'badge--warning', NO_INICIADO: 'badge--neutral' }[s] || 'badge--neutral'; }
function priorityBadgeClass(p) { return { ALTA: 'badge--high', MEDIA: 'badge--medium', BAJA: 'badge--low' }[p] || 'badge--neutral'; }
