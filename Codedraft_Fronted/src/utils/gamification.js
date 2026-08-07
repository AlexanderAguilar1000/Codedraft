// CodeCraftHub — presentational gamification helpers (level, streak, achievements).
// These are pure view-layer derivations from data already returned by the
// backend (profile, experience points, course stats). No new business rules
// or endpoints are introduced — this only shapes how existing data is shown.

const XP_PER_LEVEL = 100;
const STREAK_STORAGE_KEY = 'ccs_study_streak';

// Level is a simple presentational curve over the real XP total.
export function computeLevel(xp = 0) {
  const safeXp = Math.max(0, xp);
  const level = Math.floor(safeXp / XP_PER_LEVEL) + 1;
  const xpIntoLevel = safeXp % XP_PER_LEVEL;
  const pct = Math.round((xpIntoLevel / XP_PER_LEVEL) * 100);
  return { level, xpIntoLevel, xpForNext: XP_PER_LEVEL, pct };
}

// Local, front-end-only "days active" streak (no backend field exists for this).
// Increments once per new calendar day the app is opened; resets if a day is skipped.
export function getStudyStreak() {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const todayKey = today.toISOString().split('T')[0];

  let data = null;
  try { data = JSON.parse(localStorage.getItem(STREAK_STORAGE_KEY)); } catch { data = null; }

  if (!data || !data.lastDate) {
    data = { count: 1, lastDate: todayKey };
  } else if (data.lastDate !== todayKey) {
    const last = new Date(data.lastDate + 'T00:00:00');
    const diffDays = Math.round((today.getTime() - last.getTime()) / 86400000);
    data = { count: diffDays === 1 ? (data.count || 0) + 1 : 1, lastDate: todayKey };
  }

  try { localStorage.setItem(STREAK_STORAGE_KEY, JSON.stringify(data)); } catch { /* storage unavailable */ }
  return data.count;
}

// Achievement definitions unlocked purely from real, already-fetched data.
export function buildAchievements({ hasProfile = false, streak = 0, level = 1, stats = {} } = {}) {
  const completed = stats.completed ?? 0;
  const total = stats.totalCourses ?? 0;

  return [
    { id: 'onboarding', icon: 'user', title: 'Primeros pasos', desc: 'Completaste tu perfil profesional.', unlocked: hasProfile },
    { id: 'streak3', icon: 'flame', title: 'Constancia x3', desc: '3 días seguidos de actividad.', unlocked: streak >= 3 },
    { id: 'streak7', icon: 'flame', title: 'Racha semanal', desc: '7 días seguidos de actividad.', unlocked: streak >= 7 },
    { id: 'finisher', icon: 'check', title: 'Primer curso completado', desc: 'Terminaste tu primer curso.', unlocked: completed >= 1 },
    { id: 'collector', icon: 'book', title: 'Coleccionista', desc: 'Agregaste 5 cursos o más.', unlocked: total >= 5 },
    { id: 'level5', icon: 'rocket', title: 'Nivel 5', desc: 'Alcanzaste el nivel 5.', unlocked: level >= 5 },
  ];
}
