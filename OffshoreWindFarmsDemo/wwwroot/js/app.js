const myMap = L.map('mapId').setView([54.91451400766527, -3.5375976562500004], 6);

async function showAllWindFarms() {
  const response = await fetch('/api/windfarms');
  const responseDoc = await response.json();
  
  for (const windFarm of responseDoc.results) {
    const boundaries = windFarm.boundaries;
    const geoJSON = {
      type: 'Feature',
      properties: {
        // TODO
      },
      geometry: boundaries
    };

    console.log(windFarm.location);
    L.geoJSON(geoJSON).addTo(myMap);
  }
}

L.tileLayer(
  'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', 
  {
    maxZoom: 19,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }
).addTo(myMap);

showAllWindFarms();