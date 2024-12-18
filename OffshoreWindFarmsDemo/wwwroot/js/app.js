const INITIAL_ZOOM = 6;
const DETAIL_ZOOM = 9;
const MAX_ZOOM = 16;

const myMap = L.map('mapId').setView([54.91451400766527, -3.5375976562500004], INITIAL_ZOOM);
const windFarmMarkers = L.layerGroup();
const windFarmBoundaries = L.layerGroup();

let previousZoom = INITIAL_ZOOM;
let windFarmData;

async function showAllWindFarms() {
  const response = await fetch('/api/windfarms');
  const responseDoc = await response.json();
  
  windFarmData = responseDoc.results;

  for (const windFarm of windFarmData) {
    const boundaries = windFarm.boundaries;
    const geoJSON = {
      type: 'Feature',
      properties: {
        // TODO
      },
      geometry: boundaries
    };

    windFarmBoundaries.addLayer(L.geoJSON(geoJSON));
    windFarmMarkers.addLayer(L.marker([windFarm.location.y, windFarm.location.x], { windFarmId: windFarm.id }));
    myMap.addLayer(windFarmMarkers);
  }
}

myMap.on('zoomstart', function() {
  previousZoom = myMap.getZoom();
});

myMap.on('zoomend', function() {
  if (myMap.getZoom() >= DETAIL_ZOOM && previousZoom < DETAIL_ZOOM) {
    myMap.removeLayer(windFarmMarkers);
    myMap.addLayer(windFarmBoundaries);
    // Add all the turbines.
  } else if (myMap.getZoom() < DETAIL_ZOOM && previousZoom >= DETAIL_ZOOM) {
    myMap.removeLayer(windFarmBoundaries);
    // Remove all the turbines.
    myMap.addLayer(windFarmMarkers);
  }
});

myMap.setMaxBounds(myMap.getBounds());
myMap.setMinZoom(INITIAL_ZOOM);

L.tileLayer(
  'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', 
  {
    maxZoom: MAX_ZOOM,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }
).addTo(myMap);

showAllWindFarms();