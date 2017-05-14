## Test Execution Report

##### Introduction:
Welcome to Advanced Test Execution Report Repo. This repo has implementation of eye catching and comprehensive execution Report. This report is built on XML+XSLT. This in turn uses css, jquery and java script for giving styles to the generated xml report. There are also icons for attractive UI. I also embedded full stack trace of the failure. You can see it by clicking the link under failure test case tag. Expand All and Collapse All are useful while you are triaging the test results. The report also has animated Pi chart which gives the overall result status.

### How it works:
* When you run your test cases it creates a XML file with the results.
* This XML report internally refers to XSLT file called Report.xsl. You find this in src\test\resources\reporting\
* This XSLT has the references to all css, js files.
* css files defines various properties of the components.
* js files has all the action scripts such as expand, collapse etc.
* All the required files would be generated under test-output\AutoBahn_Test_Execution_Reports along with execution report.
* The execution report can also be opened in the browser automatically based on the user's selection.
### Sample Report:
![Screenshot](\Report.png)
## Sample Report with Stack Trace:
![Screenshot](\Report_With_Stack_Trace.png)
### How to use it:
* As of today, this library is not published in maven repo, so you would need to clone this project and  deploy locally by running `mvn clean install` or deploy a snapshot in your own repository like `mvn clean -B deploy`. Currently there are some open issues. Please check the disclaimer section below.

Once the library is available in your local/internal maven repository, please add dependency like,

```xml
    <dependency>
      <groupId>com.advanced.testexecutionreport</groupId>
      <artifactId>TestExecution-Report</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <scope>test</scope>
    </dependency>
```
* Add the below listener to your testng.xml
```xml
<listeners>
   <listener class-name="com.report.core.HtmlReportListener"/>
</listeners>
```
* Create a file called testconfig.properties as I have added in this project and add the variables that are there in testconfig.properties. 
   1. ApplicationName: This is to display the project/Application name on the header of the report.
   2. ENV: This is to display the environment where tests are running in the table of the report
   3. suitname: Name of the suite that is running to display in the report
   4. openreport: This is the flag to open the report automatically after your execution gets completed. By giving True it    opens the report automatically.
   5. archive.report: This is to archive the report. If you give 'false', report will be generated in "test-output" folder. If you give 'True', report will be generated under "C:\Users\<username>\Advanced_Test_Execution_Reports" with time stamp.

* You can configure the logo of your choice by replacing the existing logo under "\src\main\resources\reporting\img\Logo.jpeg". For now I have added google logo.

#### Disclaimer

** This report has been built by considering Web services automation in mind. However, We can customize it for UI Automation as well by adding screenshots and other required details. I am working on that part.

** This Report doesn't work with parallel execution right Now. I am working on it.

### Support
For support contact me @ illi.nainappa@gmail.com.
