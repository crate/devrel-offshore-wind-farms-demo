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
        windFarm: {
          id: windFarm.id,
          name: windFarm.name
        },
        turbines: {
          howMany: windFarm.turbines.howmany,
          make: windFarm.turbines.brand,
          model: windFarm.turbines.model
        }
      },
      geometry: boundaries
    };

    windFarmBoundaries.addLayer(L.geoJSON(geoJSON, {
      onEachFeature: function (feature, layer) {
        layer.bindPopup('<p>Loading data...</p>');

        layer.on({
          click: async function (e) {
            const props = e.target.feature.properties;
            console.log(props.windFarm.id);

            const dataResponse = await fetch(`/api/dailymaxpct/${props.windFarm.id}/10`);
            const dataJson = await dataResponse.json();
            const data = dataJson.results;
      
            this.setPopupContent(`
              <h2>${props.windFarm.name}</h2>
              <hr/>
              <span class="turbine-info"><b>Turbines:</b> ${props.turbines.howMany} x ${props.turbines.model}</span>
              <hr/>
              <table>
                <thead>
                  <tr>
                    <th scope="col">Day</th>
                    <th scope="col">Max Output</th>
                  </tr>
                </thead>
                <tbody>
                  ${data.map(item => '<tr><td>' + new Date(item.day).toLocaleDateString('en-UK', { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' }) + '</td><td>' + item.maxOutputPercentage + '%</td></tr>').join('')}
                </tbody>
              </table>
            `);
          }     
        })
      }
    }));

    const windFarmMarker = L.marker([windFarm.location.y, windFarm.location.x], { 
      windFarm: {
        id: windFarm.id,
        name: windFarm.name
      }
    });
    
    windFarmMarker.on('click', async function(e) {
      // Get the latest summary data for this wind farm.
      const latestResponse = await fetch(`/api/latest/${this.options.windFarm.id}`);
      const latestJson = await latestResponse.json();
      const details = latestJson.results[0];

      const responses = await Promise.all([
        `/api/avgpctformonth/${this.options.windFarm.id}/${details.month}`,
        `/api/outputforday/${this.options.windFarm.id}/${details.day}`
      ].map(async url => {
        const resp = await fetch(url);
        return resp.json();
      }));

      const monthlyAvgOutput = responses[0].results[0].avgPct;
      const hourlyCumulativeOutput = responses[1].results;
      const updatedAt = new Date(details.timestamp);

      this.setPopupContent(`
        <h2>${this.options.windFarm.name}</h2>
        <span class="update-time">${updatedAt.toLocaleString('en-UK')}</span>
        <hr/>
        <ul>
          <li><b>Output:</b> ${details.output} (${details.outputPercentage}%)</li>
          <li><b>Monthly Avg:</b> ${monthlyAvgOutput}%</li>
        </ul>
        <hr/>
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


// Fix the bounds here so that the popups on the Northern wind farms can be seen but the 
// user can't wander too far away from the area we want them to be looking at.
myMap.setMaxBounds(
  L.latLngBounds(
    L.latLng(48.67645370777654, -14.897460937500002), // South West corner.
    L.latLng(72.91963546581484, 10.437011718750002) // North East corner.
  )
);
myMap.setMinZoom(INITIAL_ZOOM);

L.tileLayer(
  'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', 
  {
    maxZoom: MAX_ZOOM,
    attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
  }
).addTo(myMap);

showAllWindFarms();