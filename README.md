# booksAPI practical coding automation task

## Specification
* Java _21.0.1_
* Junit _5.10.1_
* Cucumber _7.15.0_
* lombok _1.18.30_
* Maven _3.8.1_
* RestAssured _5.4.0_

# Configuration
Service URI, base path, and default users are configured in 
src/test/resources/config.properties file

# How to run tests
The following option is desirable and creates cucumber test report: 
- run RunCucumberTest class from intelliJ,

Other options:
- execute maven "test" goal 
- execute individual scenario from IDE using cucumber java runner

Using one of above allows for seamless integration into CI/CD

## Reporting
Test report is created in two formats (html, json) and it is available in
target/cucumber-reports folder after running RunCucumberTest.class

## Found issues and bugs
As the service is flaky and produces variable results, I've run the tests 3 times and produced 3 reports that can be found in src/test/resources/reports

The service has the following issues and errors uncovered by automation tests:
1. Delete method failes with 500 error and "Error while deleting book from database" description.
   In the background section I included removind all books to prepare environment for testing (to check adding duplicated books, and re-use same test data visible on the data table levels) - [cucumber-report-1](src/test/resources/reports/cucumber-report-1.html)
2. It is possible to add book with price<0 - [cucumber-report-2](src/test/resources/reports/cucumber-report-2.html) Scenario: Not possible to add book with price < 0
3. It is possible to add book with pages<0 - [cucumber-report-2](src/test/resources/reports/cucumber-report-2.html) Scenario: Not possible to add book with pages < 0
4. It is possible to add book with same data as already present in the DB - [cucumber-report-2](src/test/resources/reports/cucumber-report-2.html) Scenario: Not possible to add duplicated book
5. Once in few runs price is getting 000001 added at the end - [cucumber-report-3](src/test/resources/reports/cucumber-report-3.html) Scenario: Book correctly removed
6. Once in few runs update method does not change book's data, although it returns updated object in response - [cucumber-report-3](src/test/resources/reports/cucumber-report-3.html)  Scenario: Updated book has correct data
7. Once in few runs add book method adds 2 to number of pages. - [cucumber-report-2](src/test/resources/reports/cucumber-report-2.html) Scenario: Updated book has correct data


## To-do
The following areas need further development:
* add default username/password encryption
* add tests covering authorisation,
* field validation tests with extreme values,
* add suite that will re-run tests and catch bugs occurring every few runs
* in case more environments are available add test parameter - env - that would load set of env specific properties

