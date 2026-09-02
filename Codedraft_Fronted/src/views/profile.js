// CodeCraftHub — Profile view (onboarding + edit).
// Endpoints: GET /api/profile, POST /api/profile, GET /api/profile/points

import { getState, setState, setLoading } from '../state/store.js';
import { createProfile, getProfile, getExperiencePoints } from '../services/api.js';
import { roleOptions, careerOptions, interestOptions } from '../data/mockData.js';
import { icon, showToast, escapeHtml, xpRing, pageHeader } from '../utils/ui.js';
import { navigate } from '../router.js';

let selectedInterests = [];

export async function renderProfile(root) {
  // Fetch latest profile + XP from backend on each visit.
  setLoading('profile', true);
  let profile = null;
  try {
    profile = await getProfile();
    const xp = await getExperiencePoints();
    setState({ profile, hasProfile: true, experiencePoints: xp.experiencePoints ?? 0 });
  } catch (err) {
    if (err.code === 'PROFILE_NOT_FOUND' || err.status === 404) {
      setState({ profile: null, hasProfile: false });
    } else {
      showToast(err.message || 'No se pudo cargar el perfil.', 'error');
    }
  } finally {
    setLoading('profile', false);
  }

  selectedInterests = profile ? [...(profile.intereses || [])] : [];
  root.innerHTML = pageHTML(getState());
  bindForm(root);
}

function pageHTML(state) {
  const profile = state.profile;
  const isOnboarding = !profile;
  const xp = state.experiencePoints ?? 0;
  const xpWidget = `
    <div class="xp-widget">
      <div class="xp-pill"><span class="xp-pill__flame">${icon('zap', 15)}</span><span class="xp-pill__value">${xp}</span> XP</div>
      ${xpRing(xp)}
    </div>
  `;

  return `
    ${pageHeader({
      icon: 'user',
      title: 'Mi perfil',
      subtitle: 'Configura tu rol, carrera e intereses para recibir recomendaciones personalizadas.',
      right: xpWidget,
      tone: 'purple',
    })}

    ${isOnboarding ? `<div class="onboard-hero"><h1>¡Bienvenido a CodeCraftHub!</h1><p>Completa tu perfil para empezar a organizar y priorizar tus cursos de aprendizaje.</p></div>` : ''}

    <div class="${profile ? 'profile-card-grid' : ''}">
      <div class="card">
        <div class="card__header"><span class="card__title">Información profesional</span></div>
        <div class="card__body">${profileFormHTML(profile, isOnboarding)}</div>
      </div>
      ${profile ? summaryCardHTML(profile) : ''}
    </div>
  `;
}

function profileFormHTML(profile, isOnboarding) {
  const rol = profile?.rol || '';
  const carrera = profile?.carrera || '';
  return `
    <form id="profile-form" novalidate>
      <p class="muted" style="margin-bottom:var(--s-5);font-size:0.86rem">
        ${isOnboarding ? 'Completa tu perfil para empezar a usar CodeCraftHub.' : 'Actualiza tu información cuando cambien tus objetivos de aprendizaje.'}
      </p>
      <div class="form-grid">
        <div class="field">
          <label class="field__label" for="profile-rol">Rol profesional<span class="req">*</span></label>
          <select id="profile-rol" name="rol" required>
            <option value="">Selecciona un rol</option>
            ${roleOptions.map((r) => `<option value="${r}" ${r === rol ? 'selected' : ''}>${r}</option>`).join('')}
          </select>
          <span class="field__error">Selecciona tu rol.</span>
        </div>
        <div class="field">
          <label class="field__label" for="profile-carrera">Carrera<span class="req">*</span></label>
          <select id="profile-carrera" name="carrera" required>
            <option value="">Selecciona una carrera</option>
            ${careerOptions.map((c) => `<option value="${c}" ${c === carrera ? 'selected' : ''}>${c}</option>`).join('')}
          </select>
          <span class="field__error">Selecciona tu carrera.</span>
        </div>
        <div class="field full">
          <label class="field__label">Intereses y tecnologías</label>
          <div class="chip-picker" id="interest-picker">
            ${interestOptions.map((opt) =>
              `<button type="button" class="chip-option ${selectedInterests.includes(opt) ? 'is-selected' : ''}" data-interest="${escapeHtml(opt)}">${escapeHtml(opt)}</button>`
            ).join('')}
          </div>
          <span class="field__hint">Selecciona las tecnologías y áreas que te interesan. Se usan para recomendarte cursos.</span>
        </div>
      </div>
      <div style="display:flex;justify-content:flex-end;gap:var(--s-3);margin-top:var(--s-6)">
        <button type="reset" class="btn btn--ghost" id="profile-reset">Limpiar</button>
        <button type="submit" class="btn btn--primary" id="profile-submit">${icon('check', 15)} <span>${isOnboarding ? 'Guardar perfil' : 'Guardar cambios'}</span></button>
      </div>
    </form>
  `;
}

function summaryCardHTML(profile) {
  return `
    <div class="card">
      <div class="card__header">
        <span class="card__title">Resumen</span>
      </div>
      <div class="card__body">
        <div class="profile-summary">
          <div class="profile-summary__row"><span class="muted">Rol</span><strong>${escapeHtml(profile.rol || '—')}</strong></div>
          <div class="profile-summary__row"><span class="muted">Carrera</span><strong>${escapeHtml(profile.carrera || '—')}</strong></div>
          <div class="profile-summary__row">
            <span class="muted">Intereses</span>
            <div class="chip-list">
              ${(profile.intereses || []).map((i) => `<span class="chip">${escapeHtml(i)}</span>`).join('') || '<span class="muted">Sin intereses seleccionados</span>'}
            </div>
          </div>
        </div>
      </div>
    </div>
  `;
}

function bindForm(root) {
  const picker = root.querySelector('#interest-picker');
  picker?.addEventListener('click', (e) => {
    const btn = e.target.closest('.chip-option');
    if (!btn) return;
    const v = btn.dataset.interest;
    if (selectedInterests.includes(v)) { selectedInterests = selectedInterests.filter((i) => i !== v); btn.classList.remove('is-selected'); }
    else { selectedInterests.push(v); btn.classList.add('is-selected'); }
  });

  const form = root.querySelector('#profile-form');
  form?.addEventListener('submit', onSubmit);
  form?.addEventListener('reset', () => {
    selectedInterests = [];
    root.querySelectorAll('.chip-option.is-selected').forEach((b) => b.classList.remove('is-selected'));
  });
}

async function onSubmit(e) {
  e.preventDefault();
  const form = e.currentTarget;
  const rol = form.rol.value.trim();
  const carrera = form.carrera.value.trim();

  let valid = true;
  form.querySelectorAll('.field').forEach((f) => f.classList.remove('has-error'));
  if (!rol) { form.rol.closest('.field').classList.add('has-error'); valid = false; }
  if (!carrera) { form.carrera.closest('.field').classList.add('has-error'); valid = false; }
  if (!valid) { showToast('Por favor completa los campos obligatorios.', 'error'); return; }

  const btn = form.querySelector('#profile-submit');
  const original = btn.innerHTML;
  btn.disabled = true;
  btn.innerHTML = `<span class="spinner" style="width:15px;height:15px"></span> <span>Guardando…</span>`;
  setLoading('saving', true);
  try {
    const saved = await createProfile({ rol, carrera, intereses: selectedInterests });
    setState({ profile: saved, hasProfile: true, experiencePoints: saved.experiencePoints ?? getState().experiencePoints });
    showToast('Perfil guardado correctamente.', 'success');
    // If onboarding, go to dashboard; otherwise re-render.
    if (!getState().route || getState().route === 'profile') {
      // stay on profile but refresh
    }
    renderProfile(document.getElementById('view'));
    showToast('Redirigiendo al dashboard…', 'info', 1500);
    setTimeout(() => navigate('dashboard'), 800);
  } catch (err) {
    showToast(err.message || 'No se pudo guardar el perfil.', 'error');
  } finally {
    setLoading('saving', false);
    btn.disabled = false;
    btn.innerHTML = original;
  }
}
