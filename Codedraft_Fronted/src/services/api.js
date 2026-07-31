// CodeCraftHub — API access layer (real backend calls).
// Base URL and mock flag live in config.js.

import { API_BASE } from './config.js';

// Parse error body from Spring Boot (can be {error:…} or {message:…})
async function parseError(res) {
  const body = await res.json().catch(() => ({}));
  const msg = body.error || body.message || `Error ${res.status}`;
  const err = new Error(msg);
  err.status = res.status;
  return err;
}

// ---------------------------------------------------------------------------
// Profile
// ---------------------------------------------------------------------------

// POST /api/profile
export async function createProfile({ rol, carrera, intereses }) {
  const res = await fetch(`${API_BASE}/api/profile`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ rol, carrera, intereses }),
  });
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// GET /api/profile
export async function getProfile() {
  const res = await fetch(`${API_BASE}/api/profile`);
  if (res.status === 404) {
    const err = new Error('El perfil no ha sido registrado.');
    err.code = 'PROFILE_NOT_FOUND';
    err.status = 404;
    throw err;
  }
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// GET /api/profile/points
export async function getExperiencePoints() {
  const res = await fetch(`${API_BASE}/api/profile/points`);
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// ---------------------------------------------------------------------------
// Courses
// ---------------------------------------------------------------------------

// GET /api/courses
export async function getCourses() {
  const res = await fetch(`${API_BASE}/api/courses`);
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// GET /api/courses/{id}
export async function getCourse(id) {
  const res = await fetch(`${API_BASE}/api/courses/${id}`);
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// POST /api/courses
export async function createCourse(course) {
  const res = await fetch(`${API_BASE}/api/courses/registerCurso`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(course),
  });
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// PATCH /api/courses/{id}/update
export async function updateCourse(id, fields) {
  const res = await fetch(`${API_BASE}/api/courses/${id}/update`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(fields),
  });
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// PATCH /api/courses/{id}/status
export async function updateCourseStatus(id, status) {
  const res = await fetch(`${API_BASE}/api/courses/${id}/status`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ status }),
  });
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// PATCH /api/courses/{id}/priority
export async function updateCoursePriority(id, priority) {
  const res = await fetch(`${API_BASE}/api/courses/${id}/priority`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ priority }),
  });
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// PATCH /api/courses/{id}/target-date
export async function updateCourseTargetDate(id, targetDate) {
  const res = await fetch(`${API_BASE}/api/courses/${id}/target-date`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ targetDate }),
  });
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// PATCH /api/courses/{id}/progress
export async function updateCourseProgress(id, progress) {
  const res = await fetch(`${API_BASE}/api/courses/${id}/progress`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ progress }),
  });
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// DELETE /api/courses/{id}
export async function deleteCourse(id) {
  const res = await fetch(`${API_BASE}/api/courses/${id}`, { method: 'DELETE' });
  if (!res.ok) throw await parseError(res);
  if (res.status === 204) return { message: 'Curso eliminado correctamente.' };
  return res.json().catch(() => ({ message: 'Curso eliminado correctamente.' }));
}

// GET /api/courses/search
export async function searchCourses(filters = {}) {
  const params = new URLSearchParams();
  if (filters.name) params.set('name', filters.name);
  if (filters.status) params.set('status', filters.status);
  if (filters.priority) params.set('priority', filters.priority);
  if (filters.minProgress != null && filters.minProgress !== '') params.set('minProgress', filters.minProgress);
  if (filters.maxProgress != null && filters.maxProgress !== '') params.set('maxProgress', filters.maxProgress);
  const res = await fetch(`${API_BASE}/api/courses/search?${params.toString()}`);
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// GET /api/courses/stats
export async function getCourseStats() {
  const res = await fetch(`${API_BASE}/api/courses/stats`);
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// GET /api/courses/recommendation
export async function getRecommendedCourse() {
  const res = await fetch(`${API_BASE}/api/courses/recommendation`);
  if (!res.ok) throw await parseError(res);
  return res.json();
}

// GET /api/courses/suggested
export async function getSuggestedCourses() {
  const res = await fetch(`${API_BASE}/api/catalogo/suggested`);
  if (!res.ok) throw await parseError(res);
  return res.json();
}
