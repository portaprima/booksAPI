Feature: Books API test

  Background:
    Given correct username and password used
    And there are no books in the db

  Scenario: Book correctly added
    When creating book with the following data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |
    Then book has correct data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |
    And book present in all books
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |


  Scenario: Not possible to add book with empty name
    Then not possible to create book with wrong data
      | name    | author     | publication    | category | pages | price |
      | [blank] | Author One | New Dawn Books | New Age  | 100   | 99.9  |

  Scenario: Not possible to add book with empty author
    Then not possible to create book with wrong data
      | name     | author  | publication    | category | pages | price |
      | Book One | [blank] | New Dawn Books | New Age  | 100   | 99.9  |

  Scenario: Not possible to add book with empty pages
    Then not possible to create book with wrong data
      | name     | author     | publication    | category | pages   | price |
      | Book One | Author One | New Dawn Books | New Age  | [blank] | 99.9  |

  Scenario: Not possible to add book with empty price
    Then not possible to create book with wrong data
      | name     | author     | publication    | category | pages | price   |
      | Book One | Author One | New Dawn Books | New Age  | 100   | [blank] |

  Scenario: Not possible to add book with price < 0
    Then not possible to create book with wrong data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | -200  |

  Scenario: Not possible to add book with pages < 0
    Then not possible to create book with wrong data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | -100  | 200   |

  Scenario: Not possible to add duplicated book
    Given creating book with the following data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |
    Then not possible to create book with duplicated data
      | name     | author     | publication    | category | pages | price |
      | Book One | Author One | New Dawn Books | New Age  | 100   | 99.9  |


  Scenario: Book correctly removed
    Given creating book with the following data
      | name               | author     | publication   | category | pages | price |
      | Book To Be Removed | Author Two | Sun Set Books | New Age  | 200   | 199.9 |
    When book is deleted
    Then book is not longer returned by the service
    And book is no longer present in all books


  Scenario: Updated book has correct data
    Given creating book with the following data
      | name               | author     | publication   | category | pages | price |
      | Book To Be Updated | Author Two | Sun Set Books | New Age  | 200   | 199.9 |
    When book is updated
      | name         | author         | publication          | category       | pages | price |
      | Updated name | Updated Author | UpdatedSun Set Books | UpdatedNew Age | 300   | 299.9 |
    Then book has correct data
      | name         | author         | publication          | category       | pages | price |
      | Updated name | Updated Author | UpdatedSun Set Books | UpdatedNew Age | 300   | 299.9 |
    And book present in all books
      | name         | author         | publication          | category       | pages | price |
      | Updated name | Updated Author | UpdatedSun Set Books | UpdatedNew Age | 300   | 299.9 |

#      Zmienić dane książek albo kasować książki przed runem