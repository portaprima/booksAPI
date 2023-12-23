Feature: Books API test

  Scenario: Get all books
    Given correct username and password used
    When calling get all books API
    Then API returns 200

  Scenario: Add books
    Given correct username and password used
    When creating books with the following data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |
    Then API returns 200 for each book

  Scenario: Book correctly added
    Given correct username and password used
    When creating book with the following data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |
    Then API returns 200
    And Added book has correct data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |
    And Added book present in all books
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |

    Scenario: Book correctly removed

  Scenario: Not possible to add duplicated book
    Given correct username and password used
    Given creating book with the following data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |
    Then API returns 200
    When creating book with the following data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |
    Then API returns error - cannot add duplicated book