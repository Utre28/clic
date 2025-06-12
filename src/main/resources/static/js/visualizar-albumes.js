document.addEventListener('DOMContentLoaded', async () => {
    // Carga álbumes según eventId
    const galleryContainer = document.getElementById('albumGallery');
    const urlParams = new URLSearchParams(window.location.search);
    const eventId = urlParams.get('eventId');

    if (galleryContainer) {
        if (!eventId) {
            galleryContainer.innerHTML = '<p>Error: No se especificó un ID de evento.</p>';
        } else {
            try {
                const albumsResp = await fetch(`/api/albums/by-event/${eventId}`);
                const albums = await albumsResp.json();

                if (!albums.length) {
                    galleryContainer.innerHTML = '<p>No hay álbumes para este evento.</p>';
                } else {
                    for (const album of albums) {
                        const photosResp = await fetch(`/api/photos/by-album/${album.id}`);
                        const photos = await photosResp.json();
                        const coverImg = photos.length ? photos[0].url : 'img/placeholder.jpg';

                        const card = document.createElement('div');
                        card.className = 'album-card';
                        card.innerHTML = `
                            <a href="ver-album.html?albumId=${album.id}">
                              <img src="${coverImg}" alt="${album.name}" class="album-cover" />
                              <h3>${album.name}</h3>
                            </a>
                        `;
                        galleryContainer.appendChild(card);
                    }
                }
            } catch (error) {
                console.error('Error al cargar álbumes:', error);
                galleryContainer.innerHTML = '<p>Hubo un error al cargar los álbumes.</p>';
            }
        }
    }

    // Carga clientes y fotógrafos para evento
    const clientSelect = document.getElementById('eventClientId');
    if (clientSelect) {
        try {
            // Limpiar opciones previas
            clientSelect.innerHTML = '<option value="">-- Selecciona un cliente o fotógrafo --</option>';

            // Cargar clientes
            const clientsResp = await fetch('/users/api/clients');
            if (!clientsResp.ok) throw new Error('Error al obtener clientes');
            const clients = await clientsResp.json();
            if (clients.length) {
                const optgroupClients = document.createElement('optgroup');
                optgroupClients.label = 'Clientes';
                clients.forEach(client => {
                    const option = document.createElement('option');
                    option.value = client.id;
                    option.textContent = client.name;
                    optgroupClients.appendChild(option);
                });
                clientSelect.appendChild(optgroupClients);
            }

            // Cargar fotógrafos
            const photogsResp = await fetch('/users/api/photographers');
            if (photogsResp.ok) {
                const photogs = await photogsResp.json();
                if (photogs.length) {
                    const optgroupPhotogs = document.createElement('optgroup');
                    optgroupPhotogs.label = 'Fotógrafos';
                    photogs.forEach(photog => {
                        const option = document.createElement('option');
                        option.value = photog.id;
                        option.textContent = photog.name;
                        optgroupPhotogs.appendChild(option);
                    });
                    clientSelect.appendChild(optgroupPhotogs);
                }
            }
        } catch (error) {
            console.error('Error cargando clientes/fotógrafos:', error);
        }
    }

    // Controla subida de fotos
    const uploadForm = document.getElementById('uploadForm');
    const uploadStatus = document.getElementById('uploadStatus');

    if (uploadForm) {
        uploadForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const filesInput = document.getElementById('files');
            const albumSelect = document.getElementById('albumSelect');

            if (!albumSelect.value) {
                uploadStatus.textContent = 'Por favor, selecciona un álbum.';
                return;
            }
            if (!filesInput.files.length) {
                uploadStatus.textContent = 'Por favor, selecciona al menos una foto.';
                return;
            }

            const formData = new FormData();
            for (const file of filesInput.files) {
                formData.append('files', file);
            }
            formData.append('albumId', albumSelect.value);

            uploadStatus.textContent = 'Subiendo fotos...';

            try {
                const response = await fetch('/api/photos/upload-multiple', {
                    method: 'POST',
                    body: formData
                });

                if (response.ok) {
                    uploadStatus.textContent = 'Fotos subidas correctamente.';
                    uploadForm.reset();
                } else {
                    const errorText = await response.text();
                    uploadStatus.textContent = `Error al subir fotos: ${errorText}`;
                }
            } catch (error) {
                uploadStatus.textContent = `Error al subir fotos: ${error.message}`;
            }
        });
    }
});
