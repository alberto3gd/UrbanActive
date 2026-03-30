document.addEventListener("DOMContentLoaded", function () {
  const dataElement = document.getElementById("actividades-data");
  let actividades = [];

  try {
    actividades = JSON.parse(dataElement.textContent);
  } catch (e) {
    console.error("No se pudieron leer las actividades", e);
    actividades = [];
  }

  const map = L.map("map").setView([40.4168, -3.7038], 12);

  L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "&copy; OpenStreetMap"
  }).addTo(map);

  const markers = [];
  const bounds = [];

  actividades.forEach(actividad => {
    const lat = actividad.latitud;
    const lon = actividad.longitud;

    if (lat != null && lon != null) {
      const marker = L.marker([lat, lon]).addTo(map);

      marker.bindPopup(`
        <div class="popup-title">${actividad.titulo}</div>
        <div class="popup-meta">${actividad.tipo} · ${actividad.ubicacion}</div>
        <a class="popup-link" href="/actividades/${actividad.id}">Ver actividad</a>
      `);

      marker._actividadId = String(actividad.id);
      markers.push(marker);
      bounds.push([lat, lon]);
    }
  });

  if (bounds.length > 0) {
    map.fitBounds(bounds, { padding: [30, 30] });
  }

  const searchInput = document.getElementById("searchInput");
  const activityCount = document.getElementById("activityCount");
  const activityItems = Array.from(document.querySelectorAll(".activity-item"));

  function normalize(text) {
    return (text || "").toString().toLowerCase();
  }

  function filterActivities() {
    const query = normalize(searchInput.value);
    let visibles = 0;

    activityItems.forEach(item => {
      const title = normalize(item.dataset.title);
      const description = normalize(item.dataset.description);
      const type = normalize(item.dataset.type);
      const location = normalize(item.dataset.location);

      const visible =
        title.includes(query) ||
        description.includes(query) ||
        type.includes(query) ||
        location.includes(query);

      item.style.display = visible ? "flex" : "none";

      const id = item.dataset.id;
      const marker = markers.find(m => m._actividadId === id);

      if (marker) {
        if (visible) {
          marker.addTo(map);
        } else {
          map.removeLayer(marker);
        }
      }

      if (visible) {
        visibles++;
      }
    });

    activityCount.textContent = `${visibles} actividades disponibles`;
  }

  searchInput.addEventListener("input", filterActivities);
});