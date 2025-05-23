// JavaScript principal para manejar interacciones de la página
// Se inicia cuando el DOM está listo

// 1. Mostrar y ocultar Términos y Condiciones
function initTerms() {
  const termsLink = document.querySelector('.terms-link');
  const termsContainer = document.querySelector('.terms-container');
  const closeBtn = document.querySelector('.terms-close-btn');

  if (termsLink && termsContainer) {
    termsLink.addEventListener('click', () => {
      termsContainer.style.display = 'block';
    });
  }
  if (closeBtn && termsContainer) {
    closeBtn.addEventListener('click', () => {
      termsContainer.style.display = 'none';
    });
  }
}

// 2. Cargar datos del usuario en el panel o redirigir si no está autenticado
async function loadUserPanel() {
  const nameEl = document.getElementById('user-name');
  const emailEl = document.getElementById('user-email');
  if (!nameEl || !emailEl) return;

  const resp = await fetch('/api/users/me', { credentials: 'include' });
  if (!resp.ok) {
    window.location.href = '/login.html';
    return;
  }
  const user = await resp.json();
  nameEl.textContent = user.name;
  emailEl.textContent = user.email;
}

// 3. Cargar select de eventos y álbumes relacionados
async function loadEvents() {
  const resp = await fetch('/api/events');
  const events = await resp.json();
  const sel = document.getElementById('eventSelect');
  if (!sel) return;

  sel.innerHTML = '';
  events.forEach(e => {
    const opt = document.createElement('option');
    opt.value = e.id;
    opt.textContent = e.name;
    sel.appendChild(opt);
  });
  if (events.length) loadAlbums(events[0].id);
}
async function loadAlbums(eventId) {
  const resp = await fetch(`/api/albums/by-event/${eventId}`);
  const albums = await resp.json();
  const sel = document.getElementById('albumSelect');
  if (!sel) return;
  sel.innerHTML = '';
  albums.forEach(a => {
    const opt = document.createElement('option');
    opt.value = a.id;
    opt.textContent = a.name;
    sel.appendChild(opt);
  });
  const uploadId = document.getElementById('uploadAlbumId');
  if (uploadId) uploadId.value = albums[0]?.id || '';
}

// 4. Manejar formulario de creación de eventos
function initCreateEvent() {
  const form = document.getElementById('createEventForm');
  if (!form) return;
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const name = form.eventName.value.trim();
    const date = form.eventDate.value;
    const loc = form.eventLocation.value.trim();
    const client = form.eventClientId.value.trim() || null;
    if (!name || !date || !loc) {
      alert('Completa todos los campos obligatorios');
      return;
    }
    try {
      const resp = await fetch('/api/events', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, date, location: loc, clientId: client ? Number(client) : null })
      });
      if (resp.ok) {
        alert('Evento creado');
        form.reset();
        loadEvents();
      } else {
        const err = await resp.json();
        alert('Error: ' + (err.message || resp.statusText));
      }
    } catch (err) {
      alert('Error de red: ' + err.message);
    }
  });
}

// 5. Manejar formulario de creación de álbumes
function initCreateAlbum() {
  const form = document.getElementById('createAlbumForm');
  if (!form) return;
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const name = form.newAlbum.value.trim();
    const eventId = document.getElementById('eventSelect')?.value;
    if (!name) {
      alert('Nombre de álbum obligatorio');
      return;
    }
    try {
      const resp = await fetch('/api/albums', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, eventId: Number(eventId) })
      });
      if (resp.ok) {
        alert('Álbum creado');
        form.reset();
        if (eventId) loadAlbums(eventId);
      } else {
        const err = await resp.json();
        alert('Error: ' + (err.message || resp.statusText));
      }
    } catch (err) {
      alert('Error de red: ' + err.message);
    }
  });
}

// 6. Manejar subida de múltiples fotos
function initUploadPhotos() {
  const form = document.getElementById('uploadForm');
  if (!form) return;
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const files = document.getElementById('files').files;
    const albumId = document.getElementById('uploadAlbumId')?.value;
    if (!albumId || !files.length) {
      alert('Selecciona un álbum y al menos una foto');
      return;
    }
    const fd = new FormData();
    for (const f of files) fd.append('files', f);
    fd.append('albumId', albumId);
    const resp = await fetch('/api/photos/upload-multiple', { method: 'POST', body: fd });
    const status = document.getElementById('uploadStatus');
    if (resp.ok) {
      status.textContent = 'Fotos subidas correctamente.';
      form.reset();
    } else {
      status.textContent = 'Error al subir fotos.';
    }
  });
}

// 7. Inicializar todas las funciones cuando carga la página
function init() {
  console.log('Init página');
  initTerms();
  loadUserPanel();
  loadEvents();
  document.getElementById('eventSelect')?.addEventListener('change', e => loadAlbums(e.target.value));
  document.getElementById('albumSelect')?.addEventListener('change', e => document.getElementById('uploadAlbumId').value = e.target.value);
  initCreateEvent();
  initCreateAlbum();
  initUploadPhotos();
}
document.addEventListener('DOMContentLoaded', init);
