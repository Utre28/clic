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

// 2. Mostrar notificación en la parte inferior derecha
function showNotification(message, isError = false) {
  const notification = document.getElementById("notification");
  notification.textContent = message;
  notification.classList.toggle("error", isError);
  notification.style.display = "block";

  // Ocultar el mensaje después de 5 segundos
  setTimeout(() => {
    notification.style.display = "none";
  }, 5000);
}

// 3. Llenar el select de clientes dinámicamente
async function loadClients() {
  const clientSelect = document.getElementById("eventClientId");
  const response = await fetch('/api/users/api/clients');  // Suponiendo que tienes este endpoint para obtener clientes
  if (response.ok) {
    const clients = await response.json();
    clients.forEach(client => {
      const option = document.createElement("option");
      option.value = client.id;
      option.textContent = client.name;
      clientSelect.appendChild(option);
    });
  } else {
    console.error("Error al cargar los clientes");
  }
}

// 4. Establecer la fecha mínima en el input para la fecha del evento (no permitir fechas pasadas)
function setMinDate() {
  const today = new Date().toISOString().split('T')[0];
  document.getElementById("eventDate").setAttribute("min", today);
}

// 5. Manejar formulario de creación de eventos
function initCreateEvent() {
  const form = document.getElementById('createEventForm');
  if (!form) return;
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const name = form.eventName.value;
    const date = form.eventDate.value;
    const loc = form.eventLocation.value.trim();
    const category = form.eventCategory.value;
    const client = form.eventClientId.value || null;
    const privado = form.eventPrivado.checked;

    // Validación de campos
    if (!name || !date || !loc || !category) {
      showNotification('Completa todos los campos obligatorios', true);
      return;
    }

    // Si no hay cliente seleccionado, el evento va al portafolio
    const eventType = client ? 'cliente' : 'portafolio';

    // Validar que el tipo de evento esté entre las 5 opciones
    const validCategories = ["Bodas", "Bautizos", "Comuniones", "Retratos", "Conciertos"];
    if (!validCategories.includes(category)) {
      showNotification('Categoría de evento no válida', true);
      return;
    }

    try {
      const resp = await fetch('/api/events', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name, date, location: loc, category, clientId: client ? Number(client) : null, privado, type: eventType
        })
      });

      if (resp.ok) {
        showNotification('Evento creado con éxito');
        form.reset();
        loadEvents(); // Refresca el select de eventos
      } else {
        let errMsg = 'Error al crear evento';
        try {
          const err = await resp.json();
          errMsg = 'Error: ' + (err.message || JSON.stringify(err));
        } catch {}
        showNotification(errMsg, true);
      }
    } catch (err) {
      showNotification('Error de red: ' + err.message, true);
    }
  });
}

// 6. Llenar el select de eventos dinámicamente
async function loadEvents() {
  const eventSelect = document.getElementById("eventSelect");
  if (!eventSelect) return;
  eventSelect.innerHTML = '';
  const response = await fetch('/api/events');
  if (response.ok) {
    const events = await response.json();
    events.forEach(event => {
      const option = document.createElement("option");
      option.value = event.id;
      option.textContent = event.name;
      eventSelect.appendChild(option);
    });
  } else {
    console.error("Error al cargar los eventos");
  }
}

// Llenar el select de álbumes dinámicamente
async function loadAlbums() {
  const albumSelect = document.getElementById("albumSelect");
  if (!albumSelect) return;
  albumSelect.innerHTML = '';
  const response = await fetch('/api/albums');
  if (response.ok) {
    const albums = await response.json();
    albums.forEach(album => {
      const option = document.createElement("option");
      option.value = album.id;
      option.textContent = album.name;
      albumSelect.appendChild(option);
    });
  } else {
    console.error("Error al cargar los álbumes");
  }
}

// 7. Inicializar todas las funciones cuando carga la página
document.addEventListener('DOMContentLoaded', () => {
  loadClients();  // Llenar clientes
  loadEvents();   // Llenar eventos
  loadAlbums();   // Llenar álbumes
  setMinDate();   // Establecer la fecha mínima a hoy
  initCreateEvent();
  initCreateAlbum(); // Inicializa el formulario de crear álbum
});

// 8. Manejar formulario de creación de álbum
function initCreateAlbum() {
  const form = document.getElementById('createAlbumForm');
  if (!form) return;
  form.addEventListener('submit', async e => {
    e.preventDefault();
    const name = form.newAlbum.value;
    const eventId = form.eventSelect.value;
    if (!name || !eventId) {
      showNotification('Completa todos los campos obligatorios del álbum', true);
      return;
    }
    try {
      const resp = await fetch('/api/albums', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, eventId: Number(eventId) })
      });
      if (resp.ok) {
        showNotification('Álbum creado con éxito');
        form.reset();
        loadAlbums(); // Refresca el select de álbumes
      } else {
        let errMsg = 'Error al crear álbum';
        try {
          const err = await resp.json();
          errMsg = 'Error: ' + (err.message || JSON.stringify(err));
        } catch {}
        showNotification(errMsg, true);
      }
    } catch (err) {
      showNotification('Error de red: ' + err.message, true);
    }
  });
}
