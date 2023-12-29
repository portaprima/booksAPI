# booksAPI

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

## To-do
The following areas need further development:
* add default username/password encryption
* add tests covering authorisation,
* field validation tests with extreme values,
* add suite that will re-run tests and catch bugs occurring every few runs

