const INITIAL_ZOOM = 6;
const DETAIL_ZOOM = 8;
const TURBINE_ZOOM = 10;
const MAX_ZOOM = 16;

const myMap = L.map('mapId').setView([54.91451400766527, -3.5375976562500004], INITIAL_ZOOM);
const windFarmMarkers = L.layerGroup();
const windFarmBoundaries = L.layerGroup();
const turbineMarkers = L.layerGroup();

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
    const windFarmMarker = L.marker([windFarm.location.y, windFarm.location.x], { 
      windFarmId: windFarm.id,
      windFarmName: windFarm.name
    });
    
    windFarmMarker.on('click', async function(e) {
      const responses = await Promise.all([
        `/api/latest/${this.options.windFarmId}`,
        `/api/avgpctformonth/${this.options.windFarmId}/1722470400000`, // TODO dynamic TS which will need the result from the above.
        `/api/outputforday/${this.options.windFarmId}/1730073600000` // TODO dynamic TS which will need the result from the above.
      ].map(async url => {
        const resp = await fetch(url);
        return resp.json();
      }));

      const details = responses[0].results[0];
      const monthlyAvgOutput = responses[1].results[0].avgPct;
      const hourlyCumulativeOutput = responses[2].results;
      const updatedAt = new Date(details.timestamp);

      this.setPopupContent(`
        <h2>${this.options.windFarmName}</h2>
        <span class="update-time">${updatedAt.toLocaleString('en-UK')}</span>
        <hr/>
        <ul>
          <li><b>Output:</b> ${details.output} (${details.outputPercentage}%)</li>
          <li><b>Monthly Avg:</b> ${monthlyAvgOutput}%</li>
        </ul>
        <hr/>
        <!-- TODO Render the hourly output as a table! -->
        <table>
          <thead>
            <tr>
              <th scope="col">Hour</th>
              <th scope="col">Output</th>
              <th scope="col">Total</th>
            </tr>
          </thead>
          <tbody>
            ${hourlyCumulativeOutput.map(item => '<tr><td>' + item.hour + '</td><td>' + item.output + '</td><td>' + item.cumulativeOutput + '</td></tr>').join('')}
          </tbody>
        </table>
      `);
    });

    windFarmMarker.bindPopup('<p>Loading data...</p>');
    windFarmMarkers.addLayer(windFarmMarker);

    for (const turbineLocation of windFarm.turbines.locations) {
      turbineMarkers.addLayer(L.marker([turbineLocation[0], turbineLocation[1]]));
    }

    myMap.addLayer(windFarmMarkers);
  }
}

myMap.on('zoomstart', function() {
  previousZoom = myMap.getZoom();
});

myMap.on('zoomend', function() {
  const currentZoom = myMap.getZoom();

  if (currentZoom >= TURBINE_ZOOM && previousZoom < TURBINE_ZOOM) {
      myMap.addLayer(turbineMarkers);
  } else if (currentZoom < TURBINE_ZOOM && previousZoom >= TURBINE_ZOOM) {
      myMap.removeLayer(turbineMarkers);
  }

  if (myMap.getZoom() >= DETAIL_ZOOM && previousZoom < DETAIL_ZOOM) {
    myMap.removeLayer(windFarmMarkers);
    myMap.addLayer(windFarmBoundaries);
  } else if (myMap.getZoom() < DETAIL_ZOOM && previousZoom >= DETAIL_ZOOM) {
    myMap.removeLayer(windFarmBoundaries);
    myMap.addLayer(windFarmMarkers);
  }
});

// TODO fix the bounds here so that the popups on the Northern wind farms can be seen!
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