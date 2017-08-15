## Set up
Download Selenium jar:

http://www.seleniumhq.org/download/

Download ChromeDriver:

https://sites.google.com/a/chromium.org/chromedriver/downloads

Download Rest Assured jars:

https://github.com/rest-assured/rest-assured/wiki/Downloads

#### Set up your PATH and CLASSPATH

Linux profile:

````
export PATH=$PATH:/path/to/chrome
export PATH=$PATH:/path/to/chromedriver
export CLASSPATH=/path/to/jars
````

Windows:

- Start
- Edit the system environment variables
- System variables - Path - Edit...
  - New - `path/to/chrome`
  - New - `path/to/chromedriver`
- System variables - New...
  - name: CLASSPATH
  - value: `path/to/jars`

## Run
Windows:

````
gradlew.bat test
````

Linux:

````
./gradlew test
````
