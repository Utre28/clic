// visualizar-albumes.js
// Este script carga los álbumes de un evento (ej. "Bodas") y muestra las fotos cuando se hace clic

document.addEventListener('DOMContentLoaded', async () => {
    const galleryContainer = document.getElementById('albumGallery');

    const urlParams = new URLSearchParams(window.location.search);
    const eventId = urlParams.get('eventId');

    if (!eventId) {
        galleryContainer.innerHTML = '<p>Error: No se especificó un ID de evento.</p>';
        return;
    }

    try {
        const albumsResp = await fetch(`/api/albums/by-event/${eventId}`);
        const albums = await albumsResp.json();

        if (!albums.length) {
            galleryContainer.innerHTML = '<p>No hay álbumes para este evento.</p>';
            return;
        }

        albums.forEach(async album => {
            const albumCard = document.createElement('div');
            albumCard.className = 'album-card';

            // Obtener la portada o usar placeholder
            const photosResp = await fetch(`/api/photos/by-album/${album.id}`);
            const photos = await photosResp.json();
            const coverImg = photos.length > 0 ? photos[0].url : 'img/placeholder.jpg';

            albumCard.innerHTML = `
        <a href="ver-album.html?albumId=${album.id}">
          <img src="${coverImg}" alt="${album.name}" class="album-cover" />
          <h3>${album.name}</h3>
        </a>
      `;
            galleryContainer.appendChild(albumCard);
        });

    } catch (error) {
        console.error('Error al cargar álbumes:', error);
        galleryContainer.innerHTML = '<p>Hubo un error al cargar los álbumes.</p>';
    }
});
