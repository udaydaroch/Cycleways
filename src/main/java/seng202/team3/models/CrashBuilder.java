package seng202.team3.models;

/**
 * CrashBuilder class for simplifying the build of large crash objects
 */
public class CrashBuilder {
    private int id;
    private Integer bicycle;
    private Integer bus;
    private Integer carStationWagon;
    private Integer moped;
    private Integer motorcycle;
    private Integer schoolBus;
    private Integer suv;
    private Integer taxi;
    private Integer truck;
    private Integer vanOrUtility;
    private Integer otherVehicleType;
    private String crashLocation1;
    private String crashLocation2;
    private String region;
    private String tlaName;
    private float lng;
    private float lat;
    private Integer crashSeverity;
    private Integer crashYear;
    private Integer fatalInjuryCount;
    private Integer minorInjuryCount;
    private String light;
    private String weather;
    private Integer speedLimit;
    private String roadSurface;
    private String roadCharacter;
    private Integer roadLane;
    private Integer numberOfLanes;


    /**
     * Sets the id for Crash object
     *
     * @param id - id of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Sets bicycle for Crash object
     *
     * @param bicycle - # bicycles in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setBicycle(Integer bicycle) {
        this.bicycle = bicycle;
        return this;
    }

    /**
     * Sets bus for Crash object
     *
     * @param bus - # buses in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setBus(Integer bus) {
        this.bus = bus;
        return this;
    }

    /**
     * Sets carStationWagon for Crash object
     *
     * @param carStationWagon - # cars or station wagons in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setCarStationWagon(Integer carStationWagon) {
        this.carStationWagon = carStationWagon;
        return this;
    }

    /**
     * Sets moped for Crash object
     *
     * @param moped - # mopeds in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setMoped(Integer moped) {
        this.moped = moped;
        return this;
    }

    /**
     * Sets motorcycle for Crash object
     *
     * @param motorcycle - # motorcycles in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setMotorcycle(Integer motorcycle) {
        this.motorcycle = motorcycle;
        return this;
    }

    /**
     * Sets schoolBus for Crash object
     *
     * @param schoolBus - # school buses in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setSchoolBus(Integer schoolBus) {
        this.schoolBus = schoolBus;
        return this;
    }

    /**
     * Sets suv for Crash object
     *
     * @param suv - # suvs in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setSuv(Integer suv) {
        this.suv = suv;
        return this;
    }

    /**
     * Sets taxi for Crash object
     *
     * @param taxi - # taxis in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setTaxi(Integer taxi) {
        this.taxi = taxi;
        return this;
    }

    /**
     * Sets truck for Crash object
     *
     * @param truck - # trucks in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setTruck(Integer truck) {
        this.truck = truck;
        return this;
    }

    /**
     * Sets vanOrUtility for Crash object
     *
     * @param vanOrUtility - # vans or utility vehicles in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setVanOrUtility(Integer vanOrUtility) {
        this.vanOrUtility = vanOrUtility;
        return this;
    }

    /**
     * Sets otherVehicleType for Crash object
     *
     * @param otherVehicleType - # other vehicles in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setOtherVehicleType(Integer otherVehicleType) {
        this.otherVehicleType = otherVehicleType;
        return this;
    }

    /**
     * Sets crashLocation1 for Crash object
     *
     * @param crashLocation1 - 1st location of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setCrashLocation1(String crashLocation1) {
        this.crashLocation1 = crashLocation1;
        return this;
    }

    /**
     * Sets crashLocation1 for Crash object
     *
     * @param crashLocation2 - 2nd location of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setCrashLocation2(String crashLocation2) {
        this.crashLocation2 = crashLocation2;
        return this;
    }

    /**
     * Sets region for Crash object
     *
     * @param region - region of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setRegion(String region) {
        this.region = region;
        return this;
    }

    /**
     * Sets tlaName for Crash object
     *
     * @param tlaName - tla of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setTlaName(String tlaName) {
        this.tlaName = tlaName;
        return this;
    }

    /**
     * Sets lng for Crash object
     *
     * @param lng - longitude of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setLng(float lng) {
        this.lng = lng;
        return this;
    }

    /**
     * Sets lat for Crash object
     *
     * @param lat - latitude of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setLat(float lat) {
        this.lat = lat;
        return this;
    }

    /**
     * Sets crashSeverity for Crash object
     *
     * @param crashSeverity - string severity of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setCrashSeverity(Integer crashSeverity) {
        this.crashSeverity = crashSeverity;
        return this;
    }

    /**
     * Sets crashYear for Crash object
     *
     * @param crashYear - year of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setCrashYear(Integer crashYear) {
        this.crashYear = crashYear;
        return this;
    }

    /**
     * Sets fatalInjuryCount for Crash object
     *
     * @param fatalInjuryCount - # fatal injuries in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setFatalInjuryCount(Integer fatalInjuryCount) {
        this.fatalInjuryCount = fatalInjuryCount;
        return this;
    }

    /**
     * Sets minorInjuryCount for Crash object
     *
     * @param minorInjuryCount - # minor injuries in crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setMinorInjuryCount(Integer minorInjuryCount) {
        this.minorInjuryCount = minorInjuryCount;
        return this;
    }

    /**
     * Sets light for Crash object
     *
     * @param light - light during crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setLight(String light) {
        this.light = light;
        return this;
    }

    /**
     * Sets weather for Crash object
     *
     * @param weather - weather during crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setWeather(String weather) {
        this.weather = weather;
        return this;
    }

    /**
     * Sets speedLimit for Crash object
     *
     * @param speedLimit - speed limit around crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
        return this;
    }

    /**
     * Sets roadSurface for Crash object
     *
     * @param roadSurface - road surface around crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setRoadSurface(String roadSurface) {
        this.roadSurface = roadSurface;
        return this;
    }

    /**
     * Sets roadCharacter for Crash object
     *
     * @param roadCharacter - road character around crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setRoadCharacter(String roadCharacter) {
        this.roadCharacter = roadCharacter;
        return this;
    }

    /**
     * Sets roadLane for Crash object
     *
     * @param roadLane - road lane of crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setRoadLane(Integer roadLane) {
        this.roadLane = roadLane;
        return this;
    }

    /**
     * Sets numberOfLanes for Crash object
     *
     * @param numberOfLanes - # lanes around crash
     * @return This CrashBuilder instance
     */
    public CrashBuilder setNumberOfLanes(Integer numberOfLanes) {
        this.numberOfLanes = numberOfLanes;
        return this;
    }

    /**
     * builds and returns Crash object with chosen attributes
     * @return Crash object
     */
    public Crash build() {
        return new Crash(
                id,
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
                otherVehicleType,
                crashLocation1,
                crashLocation2,
                region,
                tlaName,
                lng,
                lat,
                crashSeverity,
                crashYear,
                fatalInjuryCount,
                minorInjuryCount,
                light,
                weather,
                speedLimit,
                roadSurface,
                roadCharacter,
                roadLane,
                numberOfLanes
        );
    }
}