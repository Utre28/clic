document.addEventListener('DOMContentLoaded', function () {
  console.log('Página cargada correctamente');

  // —————— 1. Términos y Condiciones ——————
  const termsLink = document.querySelector('.terms-link');
  const termsContainer = document.querySelector('.terms-container');
  const closeTermsBtn = document.querySelector('.terms-close-btn');

  if (termsLink && termsContainer) {
    termsLink.addEventListener('click', () => {
      termsContainer.style.display = 'block';
    });
  }
  if (closeTermsBtn && termsContainer) {
    closeTermsBtn.addEventListener('click', () => {
      termsContainer.style.display = 'none';
    });
  }

  // —————— 2. Carga del panel (/panel.html) ——————
  // Solo ejecuta esto si el span existe en la página
  const userNameEl = document.getElementById('user-name');
  const userEmailEl = document.getElementById('user-email');

  if (userNameEl && userEmailEl) {
    fetch('/api/users/me', {
      credentials: 'include'  // fuerza envío de la cookie de sesión
    })
        .then(resp => {
          if (!resp.ok) {
            // Si no está autenticado, vuelve al login
            window.location.href = '/login.html';
            throw new Error('No autenticado');
          }
          return resp.json();
        })
        .then(user => {
          userNameEl.textContent = user.name;
          userEmailEl.textContent = user.email;
        })
        .catch(err => console.error(err));
  }

  // —————— 3. (Opcional) Otros handlers de UI ——————
  // Aquí puedes añadir más listeners específicos de otras páginas
});
