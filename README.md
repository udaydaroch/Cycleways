# CycleWays
CycleWays is an application designed for cyclists in NZ who care about getting to their destination safley. It uses the CAS crash data to provide stats about crashes, including safe bike journey planning, crash danger heatmap and clustering.

## Authors
- Charlie Porter
- Hanan Fokkens
- Ryan Scofield
- Tobias Paul
- Tom Gallagher
- Uday Daroch
- SENG202 Teaching team

## Development guide

### Requirements
- JDK >= 17 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Maven [Download](https://gradle.org/releases/) and [Install](https://gradle.org/install/)

### Importing Project (Using IntelliJ)
IntelliJ has built-in support for Gradle. To import your project:

- Launch IntelliJ and choose `Open` from the start up window.
- Select the project and click open
- At this point in the bottom right notifications you may be prompted to 'load gradle scripts', If so, click load

**Note:** *If you run into dependency issues when running the app or the Gradle pop up doesn't appear then open the Gradle sidebar and click the Refresh icon.*

### Running the project
You can run the project through your IDE from the main class (App.java) or use the `./gradlew run` command from your terminal

### Build Project
1. Open a command line interface inside the project directory and run `./gradlew jar` to build a .jar file. The file is located at `build/libs/seng202_team3-1.0.jar`

### Running the jar
- If you haven't already, Build the project.
- Run the command `java -jar build/libs/seng202_team3-1.0.jar` to open the application.

### Loading data
Loading the data is not a feature offered to the user, the application should be shipped with a SQLite database.

However this can be done through the teminal.

Using gradle:

    ./gradlew run --args="load ./path/to/you/data.csv"

Or with the jar

    java -jar build/libs/seng202_team3-1.0.jar load ./path/to/you/data.csv
### Pre-commit
Pre-commit enforces code styling, but first it needs to be installed.

    pip3 install pre-commit

then in the project directory, run:

    pre-commit install
# Cycleways
