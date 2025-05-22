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
  const userNameEl = document.getElementById('user-name');
  const userEmailEl = document.getElementById('user-email');

  if (userNameEl && userEmailEl) {
    fetch('/api/users/me', { credentials: 'include' })
        .then(resp => {
          if (!resp.ok) {
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

  // —————— 3. Funciones para cargar eventos y álbumes ——————
  async function loadEvents() {
    const resp = await fetch('/api/events');
    const events = await resp.json();
    const eventSelect = document.getElementById('eventSelect');
    if (!eventSelect) return;

    eventSelect.innerHTML = '';
    events.forEach(evt => {
      const option = document.createElement('option');
      option.value = evt.id;
      option.textContent = evt.name;
      eventSelect.appendChild(option);
    });
    if (events.length > 0) loadAlbums(events[0].id);
  }

  async function loadAlbums(eventId) {
    const resp = await fetch(`/api/albums/by-event/${eventId}`);
    const albums = await resp.json();
    const albumSelect = document.getElementById('albumSelect');
    if (!albumSelect) return;

    albumSelect.innerHTML = '';
    albums.forEach(album => {
      const option = document.createElement('option');
      option.value = album.id;
      option.textContent = album.name;
      albumSelect.appendChild(option);
    });

    const uploadAlbumId = document.getElementById('uploadAlbumId');
    if (uploadAlbumId) {
      uploadAlbumId.value = albums.length > 0 ? albums[0].id : '';
    }
  }

  const eventSelect = document.getElementById('eventSelect');
  if (eventSelect) {
    eventSelect.addEventListener('change', (e) => {
      loadAlbums(e.target.value);
    });
  }

  const albumSelect = document.getElementById('albumSelect');
  if (albumSelect) {
    albumSelect.addEventListener('change', (e) => {
      const uploadAlbumId = document.getElementById('uploadAlbumId');
      if (uploadAlbumId) {
        uploadAlbumId.value = e.target.value;
      }
    });
  }

  // —————— 4. Crear nuevo evento ——————
  const createEventForm = document.getElementById('createEventForm');
  if (createEventForm) {
    createEventForm.addEventListener('submit', async e => {
      e.preventDefault();

      const name = document.getElementById('eventName').value.trim();
      const date = document.getElementById('eventDate').value;
      const location = document.getElementById('eventLocation').value.trim();
      const clientIdValue = document.getElementById('eventClientId').value.trim();
      const clientId = clientIdValue ? Number(clientIdValue) : null;

      if (!name || !date || !location) {
        alert('Por favor, completa todos los campos obligatorios');
        return;
      }

      const eventData = {
        name,
        date,        // debe estar en formato 'YYYY-MM-DD'
        location,
        clientId     // puede ser null
      };

      try {
        const resp = await fetch('/api/events', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(eventData)
        });

        if (resp.ok) {
          alert('Evento creado con éxito');
          createEventForm.reset();
          loadEvents(); // recarga eventos para actualizar select
        } else {
          const errorData = await resp.json();
          alert('Error creando evento: ' + (errorData.message || resp.statusText));
        }
      } catch (error) {
        alert('Error de red: ' + error.message);
      }
    });
  }

  // —————— 5. Crear nuevo álbum ——————
  const createAlbumForm = document.getElementById('createAlbumForm');
  if (createAlbumForm) {
    createAlbumForm.addEventListener('submit', async e => {
      e.preventDefault();

      const name = document.getElementById('newAlbum').value.trim();
      const eventId = document.getElementById('eventSelect') ? document.getElementById('eventSelect').value : null;

      if (!name) {
        alert('Introduce un nombre válido para el álbum');
        return;
      }

      try {
        const resp = await fetch('/api/albums', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ name, eventId: Number(eventId) })
        });

        if (resp.ok) {
          alert('Álbum creado con éxito');
          createAlbumForm.reset();
          if (eventId) loadAlbums(eventId); // recarga álbumes para ese evento
        } else {
          const errorData = await resp.json();
          alert('Error creando álbum: ' + (errorData.message || resp.statusText));
        }
      } catch (error) {
        alert('Error de red: ' + error.message);
      }
    });
  }

  // —————— 6. Subir fotos ——————
  const uploadForm = document.getElementById('uploadForm');
  if (uploadForm) {
    uploadForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const files = document.getElementById('files').files;
      const albumId = document.getElementById('uploadAlbumId') ? document.getElementById('uploadAlbumId').value : null;

      if (!albumId || files.length === 0) {
        alert('Selecciona un álbum y al menos una foto');
        return;
      }

      const formData = new FormData();
      for (const file of files) {
        formData.append('files', file);
      }
      formData.append('albumId', albumId);

      const resp = await fetch('/api/photos/upload-multiple', {
        method: 'POST',
        body: formData
      });

      const status = document.getElementById('uploadStatus');
      if (resp.ok) {
        if(status) status.textContent = 'Fotos subidas correctamente.';
        uploadForm.reset();
      } else {
        if(status) status.textContent = 'Error al subir fotos.';
      }
    });
  }

  // —————— 7. Inicialización ——————
  loadEvents();
});
