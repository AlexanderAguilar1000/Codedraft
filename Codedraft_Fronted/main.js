import './src/styles/global.css';
import './src/styles/views.css';
import './src/styles/gamification.css';
import { initRouter } from './src/router.js';

// Build the app shell, then start the router.
// No mock seeding — all data is fetched from the Spring Boot backend.
document.querySelector('#app').innerHTML = `
  <div class="app-shell">
    <header class="app-header"></header>
    <main class="app-main" id="view"></main>
  </div>
`;

initRouter();
