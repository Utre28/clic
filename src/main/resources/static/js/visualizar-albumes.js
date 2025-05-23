// visualizar-albumes.js
// Carga y muestra los álbumes de un evento y sus portadas

document.addEventListener('DOMContentLoaded', async () => {
    const galleryContainer = document.getElementById('albumGallery');

    // Obtener el ID de evento de la URL
    const urlParams = new URLSearchParams(window.location.search);
    const eventId = urlParams.get('eventId');

    if (!eventId) {
        galleryContainer.innerHTML = '<p>Error: No se especificó un ID de evento.</p>';
        return;
    }

    try {
        // Fetch de álbumes por evento
        const albumsResp = await fetch(`/api/albums/by-event/${eventId}`);
        const albums = await albumsResp.json();

        if (!albums.length) {
            galleryContainer.innerHTML = '<p>No hay álbumes para este evento.</p>';
            return;
        }

        // Por cada álbum, crear tarjeta con imagen de portada
        for (const album of albums) {
            // Obtener fotos del álbum para portada
            const photosResp = await fetch(`/api/photos/by-album/${album.id}`);
            const photos = await photosResp.json();
            const coverImg = photos.length ? photos[0].url : 'img/placeholder.jpg';

            // Estructura de la tarjeta
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

    } catch (error) {
        console.error('Error al cargar álbumes:', error);
        galleryContainer.innerHTML = '<p>Hubo un error al cargar los álbumes.</p>';
    }
});
