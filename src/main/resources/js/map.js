let map;
var javaScriptBridge; // must be declared as var (will not be correctly assigned in java with let keyword)

let markers = [];
let routeCrashMarkers = [];
let crashHeatPoints = [];
let crashClusterLayer;
let crashClusterLayerRefreshTimer;
let routeCrashLayer;
var heatmapLayer;
let routeCrashPoints = [];
var route;
var routesDisplayed = [];

/** default heatmap configuration */
var heatmapConfig = {
    "radius": 10,
    "maxOpacity": 0.8,
    "scaleRadius": false,
    "useLocalExtrema": false,
    latField: 'lat',
    lngField: 'lng',
    valueField: 'severity',
    blur: .75,
    gradient: {
        1.0: '#f80500',
        0.8: '#ff6c16',
        0.6: '#f1f614',
        0.4: '#6cd722',
    }
};

/**
 * This object can be returned to our java code, where we can call the functions we define inside it
 */
let jsConnector = {
    addBox: addBox,
    closeCrashCard: closeCrashCard,
    addCrashMarker: addCrashMarker,
    displayRouteInf: displayRouteInf,
    displayRoute: displayRoute,
    removeRoute: removeRoute,
    initializeMap: initializeMap,
    togglePoints: togglePoints,
    toggleHeatMap: toggleHeatMap,
    clearCrashes: clearCrashes,
    setAllRouteCrash: setAllRouteCrash,
    clearRouteCrashes: clearRouteCrashes,
    addRouteMarkerToMarkers: addRouteMarkerToMarkers,
    initialMapConfiguration: initialMapConfiguration
};

/**
 * set new view to focus on the desired point at lat, lng
 * @param lat of entry
 * @param lng of entry
 */
function initialMapConfiguration(lat, lng) {
    // max zoom :(
    map.setView(new L.LatLng(lat, lng), 18);
}

/**
 * Helper function to produce a row for some field of the crash
 * @param field The name of the field being added
 * @param data The value for that field
 * @returns {string}
 */
function makePopupHTMLRow(field, data) {
    if (!!data) {
        return field + ": " + data + '<br>';
    }
    else {
        return field + ": null" + '<br>';
    }
    routeInstructionsList.appendChild(instructionsList);
}


/**
 * Adds datapoint to crashClusterLayer and crashHeatPoints list
 * @param {{
*  id: number,
*  lat: number,
*  lng: number,
*  crashSeverity: number,
*  weather: string,
*  crashLocation1: string,
*  crashLocation2: string,
*  bicycle: number,
*  crashYear: number
* }} crash the crash data
*/
function makePopupHTML(crash) {
    let popupHTML = "";

    popupHTML += "<strong>Crash " + crash.id + "</strong><br/><br/>";

    popupHTML += makePopupHTMLRow('Weather', crash.weather);
    popupHTML += makePopupHTMLRow('Location 1', crash.crashLocation1);
    popupHTML += makePopupHTMLRow('Location 2', crash.crashLocation2);
    popupHTML += makePopupHTMLRow('Severity', CrashSeverityToString( crash.crashSeverity));

    if (crash.bicycle === 0) {
        popupHTML += makePopupHTMLRow('Cyclist', 'No');
    } else {
        popupHTML += makePopupHTMLRow('Cyclist', 'Yes');
    }

    popupHTML += makePopupHTMLRow('Year', crash.crashYear);

    return popupHTML;
}

function CrashSeverityToString(crashSeverity) {
    switch (crashSeverity) {
        case 0: return "Non Injury";
        case 1: return "Minor Injury";
        case 2: return "Serious Injury";
        case 3: return "Fatal Crash";
    }
}

/**
 * creates and initialises the map, also defines on click event that calls java code
 */
function initializeMap() {
    var mapOptions = {
        center: [-41.171677, 174.281368],
        zoom: 5.3,
        preferCanvas: true
    }
    map = new L.map('map', mapOptions);
    new L.tileLayer('https://tile.csse.canterbury.ac.nz/hot/{z}/{x}/{y}.png', { // UCs tilemap server
        attribution: '© OpenStreetMap contributors<br>Served by University of Canterbury'
    }).addTo(map)

    // routeCrashLayer = L.markerClusterGroup();
    heatmapLayer = new HeatmapOverlay(heatmapConfig);
    crashClusterLayer = new PruneClusterForLeaflet();
    routeCrashLayer = new PruneClusterForLeaflet();
    crashClusterLayer.Cluster.Size = 10;

    crashClusterLayer.PrepareLeafletMarker = (marker, data) => {
        const popupFunction = () => {
            const fullCrashData = JSON.parse(javaScriptBridge.getCrashJSONString(data.ID));
            return makePopupHTML(fullCrashData);
        };
        marker.bindPopup(popupFunction);
    }

    console.log("initalized map");
}

function addBox(title, lat, lng) {
    var m = new L.Marker([lat, lng])
    m.bindPopup(title).openPopup()
    m.addTo(map)
    m._icon.style.filter = "hue-rotate(120deg)"
    markers.push(m)
}

/**
 * Adds datapoint to crashClusterLayer and crashHeatPoints list
 * @param {{
 *  id: number,
 *  lat: number,
 *  lng: number,
 *  crashSeverity: string,
 *  weather: string,
 *  crashLocation1: string,
 *  crashLocation2: string,
 *  bicycle: number,
 *  crashYear: number
 * }[]} crash List of crash data
 */
function addCrashMarker(crash) {
    if (crash.lng < 0) {
        crash.lng += 360;
    }

    var marker = new PruneCluster.Marker(crash.lat, crash.lng, {
        // needs to be uppercase because id is reserved
        ID: crash.id
    });

    crashClusterLayer.RegisterMarker(marker);
    point = { lat: crash.lat, lng: crash.lng, severity: severityToInt(crash.crashhSeverity) };
    crashHeatPoints.push(point);
}

function addRouteMarkerToMarkers(lat, lng) {
    var marker = new PruneCluster.Marker(lat, lng);
    point = {lat: lat, lng: lng};
    routeCrashLayer.RegisterMarker(marker);
    routeCrashPoints.push(point);
}

function setAllRouteCrash()
{
    routeCrashLayer.addTo(map);
}
function clearRouteCrashes() {

    map.removeLayer(routeCrashLayer);
    routeCrashLayer.RemoveMarkers()
}
/**
 * convert string severity to integer value
 * @param severity
 * @returns {number} integer value of given severity
 */
function severityToInt(severity) {
    switch (severity) {
        case "Non Injury": return 1;
        case "Minor Injury": return 2;
        case "Serious Injury": return 3;
        case "Fatal Crash": return 4;
    }
}

/**
 * Add crash markers to the app
 *
 * @param {{
*  id: number,
*  lat: number,
*  lng: number,
*  crashSeverity: number,
 *  weather: string,
 *  crashLocation1: string,
 *  crashLocation2: string,
 *  bicycle: number,
 *  crashYear: number
* }[]} crashList List of crash data
*/
function addCrashMarkers(crashList) {
    for (const index in crashList) {
        const crash = crashList[index];
        addCrashMarker(crash);
    }
    // make sure the clusters are re-drawn periodicly
    if (!!crashClusterLayerRefreshTimer) {
        clearTimeout(crashClusterLayerRefreshTimer);
    }
    crashClusterLayerRefreshTimer = setTimeout(() => {
        heatmapLayer.setData({ max: 120, data: crashHeatPoints });
        crashClusterLayer.ProcessView();
    }, 200)
}

/**
 * Clears crash data and visual markers on a map.
 */
function clearCrashes() {
    console.log("clear crashes");
    // removeAllLayers();

    crashClusterLayer.RemoveMarkers();
    crashHeatPoints = [];
    heatmapLayer.setData({ data: crashHeatPoints });
}


/**
 * Displays a route with two or more waypoints for cars (e.g. roads and ferries) and displays it on the map
 * @param waypointsIn a string representation of an array of lat lng json objects [("lat": -42.0, "lng": 173.0), ...]
 * @param color colour of route
 * @param id id of the route
 */
function displayRoute(waypointsIn, color, id) {
    var coordinates = JSON.parse(waypointsIn);
    // Create a polyline using the coordinates
    var polyline = L.polyline(coordinates, { color: color });
    routesDisplayed.push(polyline);
    // Add the polyline to the map
    polyline.routeData = {
        id:id
    }
    polyline.addTo(map);

    polyline.on('click', () => {
        var routeData = polyline.routeData;
        javaScriptBridge.polyLineInfoCall(routeData.id);
    })
}

/**
 * Removes the current route being displayed (will not do anything if there is no route currently displayed)
 */
function removeRoute() {
    for (const i in routesDisplayed) {
        routesDisplayed[i].remove();
    }
    clearRouteCrashes();
}

/**
 * Removes all map layers, if they exist.
 */
function removeAllLayers() {
    if (!!crashClusterLayer) {
        map.removeLayer(crashClusterLayer);
    }
    if (!!heatmapLayer) {
        map.removeLayer(heatmapLayer);
    }
}

/**
 * Toggles crash points on map, deals with heatmap appropriately
 */
function togglePoints() {
    const show = !map.hasLayer(crashClusterLayer);

    removeAllLayers();
    document.getElementById("heatmap-legend").style.display = "none";

    if (show) {
        map.addLayer(crashClusterLayer);
    }
}

/**
 * Toggle heatmap on and off, deals with points appropriately
 */
function toggleHeatMap() {
    const show = !map.hasLayer(heatmapLayer);

    removeAllLayers();
    document.getElementById("heatmap-legend").style.display = "none";

    if (show) {
        heatmapLayer.addTo(map);
        document.getElementById("heatmap-legend").style.display = "block";
    }
}


/**
 * Displays route information on a user interface element, typically a 'crash-card.'
 *
 * @param {string} routes - The route description.
 * @param {number} rating - The route rating.
 * @param {number} distance - The route distance in kilometers.
 * @param {number} duration - The route duration in minutes.
 * @param {number} crashesOnRoute - The number of crashes on the route.
 * @param {number} elevation - The average elevation along the route.
 * @param {number} averageCrashesPerKm - The average number of crashes per kilometer.
 * @param {string} directions - Route directions as a string.
 */
function displayRouteInf(routes, rating, distance, duration, crashesOnRoute, elevation, averageCrashesPerKm, directions) {
    document.getElementById('crash-card').style.display = 'block';
    document.querySelector('.crash-card-rating').textContent =  `(best route - ${routes}) Selected route rating: ${rating} `;
    document.querySelector('.crash-card-distance').textContent = `Route Distance: ${distance.toFixed(2)} km`;
    document.querySelector('.crash-card-duration').textContent = `Route Duration: ${duration} minutes`;
    document.querySelector('.crash-card-elevation').textContent = `Average Elevation: ${elevation.toFixed(2)}`;
    document.querySelector('.crash-card-calory').textContent = ` Estimated Calories: ${(distance*45).toFixed(2)} cal`;
    document.querySelector('.crash-card-crashes').textContent = `Average Crashes: ${averageCrashesPerKm.toFixed(2)} incidents per km`;
    document.querySelector('.crash-card-instructions').textContent = `Route instructions`;
    var routeInstructionsList = document.querySelector('.crash-card-instructions');

    while (routeInstructionsList.firstChild) {
        routeInstructionsList.removeChild(routeInstructionsList.firstChild);
    }

    var steps = directions.replace('[', '').replace(']', '').split(', ');

    var instructionsList = document.createElement('ul');

    for (let i = 0; i < steps.length; i++) {
        let step = steps[i].trim();
        if (step) {
            const listItem = document.createElement('li');

            // Check if the step contains directional words and add emojis
            if (step.includes('Continue')) {
                step = '↑ ' + step;
            } else if (step.includes('left')) {
                step = '← ' + step;
            } else if (step.includes('right')) {
                step = '→ ' + step;
            } else if (step.includes('roundabout')) {
                step = '🔄 ' + step;
            } else if (step.includes('Arrive')) {
                step = '🚲 ' + step;
            }

            listItem.textContent = `${step}`;
            instructionsList.appendChild(listItem);
        }
    }
    routeInstructionsList.appendChild(instructionsList);
}
/**
 * Hides the 'crash-card' element, typically used for route information.
 */
function closeCrashCard() {
    document.getElementById('crash-card').style.display = 'none';
}

