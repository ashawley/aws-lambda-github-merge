[AWS console]: http://console.aws.amazon.com

## Installation

Install is a developer task, see instructions in CONTRIBUTING file.

## Deploy with Serverless Framework

You can deploy easily to the production stage with:

    $ npx sls deploy -s prod

### Manually uploading to AWS Lambda

- From the [AWS console], click on **Configure function** from the
Lambda service

- Click **Create a Lambda Function**

- Don't select a blue print

- Click on **Configure triggers**

- Add an **API Gateway** trigger

- Select the API Gateway you created earlier

- Click the **Enable trigger** checkbox

- Click **Next**

- Provide a **Name** for the function, such as **GithubMyRepoTest**

- Leave the description blank

- Set the **Runtime** to **Java 8**

- For **Function package**, click the **Upload**

- Upload the JAR file created by SBT assembly at
`target/scala-2.11/aws-gh-prs-assembly-0.1-SNAPSHOT.jar`

- Enter the handler as, `prs.Main::handleRequest`

- Choose **Create new role from template(s)**

- Enter **LambdaTestRole** as the role name

- Leave **Policy templates** blank

- Reduce the memory to **384 MB**, and leave the timeout at *240 seconds**

- Keep the setting for **VPC** to none

- Click **Next**

- Click **Create function**

- Click **Actions** and then **Configure test event**

- From the **Sample event template** drop-down, choose **API Gateway**

- Click **Save and Test**

- The test will succeed but only return the empty array, `[]`

- To change the test to one that is closer to a GitHub event

 - Insert a minimal string of json in the API Gateway **body**
```
    "body": "{\"action\":\"opened\",\"number\":1,\"pull_request\":{\"state\":\"open\",\"head\":{\"ref\":\"changes\",\"repo\":{\"name\":\"public-repo\",\"owner\":{\"login\":\"baxterthehacker\"}}},\"base\":{\"ref\":\"master\",\"repo\":{\"name\":\"public-repo\",\"owner\":{\"login\":\"baxterthehacker\"}}}}}"
```
 - Add to **headers** an **X-GitHub-Event**
```
    "headers": {
      [...]
      "X-GitHub-Event": "pull_request",
      [...]
    }
```
