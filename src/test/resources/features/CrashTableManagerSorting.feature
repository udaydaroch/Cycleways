Feature: CrashTableManager

    Background:
        Given the table is loaded with all data and page size 25

    Scenario: Sort by year column (AT_1)
        When the "crashYear" column is clicked
        Then the table data is sorted by "crashYear"

    Scenario: Sort by location column (AT_2)
        When the "location" column is clicked
        Then the table data is sorted by "location"

    Scenario: Sort by location column reverse (AT_3)
        Given the table is already sorted by a column "location"
        When the "location" column is clicked
        Then the table data is sorted by "location" in reverse

    Scenario: Sort by location return to norma (AT_4)
        Given the table is already sorted by a column "location"
        Given the table is already sorted by a column "location"
        When the "location" column is clicked
        Then the table data is not sorted by "location"
