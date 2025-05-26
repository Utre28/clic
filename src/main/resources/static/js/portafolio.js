async function loadCategories() {
    const cont = document.getElementById('categorias');
    cont.innerHTML = '<p>Cargando categorías…</p>';
    try {
        const res = await fetch('/api/events');
        if (!res.ok) throw new Error();
        const events = await res.json();
        if (!events.length) {
            cont.innerHTML = '<p>No hay eventos disponibles.</p>';
            return;
        }
        cont.innerHTML = '';
        events.forEach(evt => {
            const a = document.createElement('a');
            a.className = 'categoria';
            const imgSrc = evt.coverUrl ? `/uploads/${evt.coverUrl}` : '/img/default-event.jpg';
            a.href = `/categoria?event=${encodeURIComponent(evt.name)}`;
            a.innerHTML = `
        <img th:src="@{${imgSrc}}" src="${imgSrc}" alt="${evt.name}">
        <h3>${evt.name}</h3>
      `;
            cont.appendChild(a);
        });
    } catch (e) {
        cont.innerHTML = '<p>Error cargando categorías.</p>';
        console.error(e);
    }
}
document.addEventListener('DOMContentLoaded', loadCategories);
