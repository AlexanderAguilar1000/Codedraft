// CodeCraftHub — global state store with pub/sub.

const state = {
  route: 'profile',
  hasProfile: false,
  profile: null,
  courses: [],
  stats: null,
  filters: { name: '', status: '', priority: '', minProgress: '', maxProgress: '' },
  catalog: [],
  experiencePoints: 0,
  loading: {
    profile: false, courses: false, stats: false,
    recommendation: false, suggested: false, saving: false,
  },
};

const listeners = new Set();

export function getState() { return state; }

export function setState(patch) {
  Object.assign(state, patch);
  listeners.forEach((fn) => fn(state));
}

export function setLoading(key, value) {
  state.loading[key] = value;
  listeners.forEach((fn) => fn(state));
}

export function subscribe(fn) {
  listeners.add(fn);
  return () => listeners.delete(fn);
}
