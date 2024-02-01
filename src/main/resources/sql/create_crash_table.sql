DROP TABLE IF EXISTS crash;
CREATE TABLE IF NOT EXISTS crash (
    id INTEGER PRIMARY KEY,
    bicycle INTEGER,
    bus INTEGER,
    carStationWagon INTEGER,
    moped INTEGER,
    motorcycle INTEGER,
    schoolBus INTEGER,
    suv INTEGER,
    taxi INTEGER,
    truck INTEGER,
    vanOrUtility INTEGER,
    otherVehicleType INTEGER,
    crashLocation1 TEXT,
    crashLocation2 TEXT,
    region TEXT,
    tlaName TEXT,
    lng REAL,
    lat REAL,
    crashSeverity INTEGER,
    crashYear INTEGER,
    fatalInjuryCount INTEGER,
    minorInjuryCount INTEGER,
    light TEXT,
    weather TEXT,
    speedLimit INTEGER,
    roadSurface TEXT,
    roadCharacter TEXT,
    roadLane INTEGER,
    numberOfLanes INTEGER
);
CREATE INDEX IF NOT EXISTS idx_crash_year ON crash(crashYear);
CREATE INDEX IF NOT EXISTS idx_crash_position ON crash(lng, lat);
CREATE INDEX IF NOT EXISTS idx_crash_region ON crash(region);
CREATE INDEX IF NOT EXISTS idx_crash_roads ON crash(crashLocation1, crashLocation2);
CREATE INDEX IF NOT EXISTS idx_crash_severity ON crash(crashSeverity);
CREATE INDEX IF NOT EXISTS idx_crash_involved ON crash(
    bicycle,
    bus,
    carStationWagon,
    moped,
    motorcycle,
    schoolBus,
    suv,
    taxi,
    truck,
    vanOrUtility,
    otherVehicleType
);
