# qr

URL.qual.is - URL shortening service

This project is developed using the [environment-qr](https://github.com/Qualis/environment-qr) vagrant environment.

## Starting the application

Start the application in:

1. developer mode (automatically detects code changes): `lein run-dev`
2. production mode: `lein run`

Navigate to [localhost:8081](http://localhost:8081/)

## Testing the application

Run: 

* tests with `lein test`
* code quality tests with `lein bikeshed`
* code coverage report generation with `lein cloverage`
  * report written to: target/coverage/index.html
* lines of code analysis with `lein vanity`

Using curl:

* create record: `curl -i -H 'Content-Type: application/json' -X POST -d '{"url":"http://qual.is/"}' http://localhost:8081/`
  * you can now grab the id from the link header (id highlighted in following)
    * Link: </`bjLyz9p47R`>;...
* retrieve url: `curl -i -X GET -H "Accept: text/plain"  http://localhost:8081/bjLyz9p47R`
* retrieve QR image: `curl -o ~/Documents/test.png -X GET -H "Accept: image/png"  http://localhost:8081/bjLyz9p47R`

## Configuration

Logging: config/logback.xml

## Links
* [Developer](http://www.qual.is)
