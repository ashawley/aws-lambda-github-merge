[Java]: https://www.oracle.com/technetwork/java/javase/downloads/index.html
[sbt]: https://scala-sbt.org
[AWS console]: http://console.aws.amazon.com
[SBT Assembly]: http://github.com/sbt/sbt-assembly

## Contributing

Requirements:

- [Java] 8
- [sbt] 1.3 or later

### Testing locally

This code is designed to run at AWS Lambda.  This makes it difficult
to test outside of AWS Lambda.  The app runs in Lambda with input from
API gateway based on GitHub webhooks and is called in AWS Lambda
in the peculiar way that Lambda operates.

The `LambdaApp` trait is defined to enforce a type signature for an
AWS Lambda functions, and can help with testing locally by providing
mock data inputs to run against.  However, certain activities are not
mocked, but instead operate against live systems: The application will
request data from GitHub's API and conduct Git operations against the
Git repo configured in `application.conf`.  Currently, these services
are not mocked in any tests.

Some of the steps for testing locally are the same as getting the
application to run in AWS Lambda.



- Rename the `application.conf.template` file in `src/main/resources`
to `application.conf`.  - Generate an SSH key with an empty passphrase

```bash
$ ssh-keygen -t rsa -f src/main/resources/ssh/id_rsa
```

- With the files `id_rsa` and `id_rsa.pub` in the `src/main/resources/ssh`
directory.  Specify the names of these files in the `application.conf`
file.

- Specify the name of the Git repository and the Git branch you want to
update with auto-merged PRs in the `application.conf` file.

- Add the public key to ~~the repository~~ your user account in GitHub
as ~~a deploy~~ an SSH key ~~with *write access*~~

 - Visit ~~http://github.com/my/repo/settings/keys~~
https://github.com/settings/ssh

- Create a GitHub access token in your user settings at

 - http://github.com/settings/tokens/new

 - Click on **Generate new token**

 - Provide a **Token description**, such as **AWS Lambda**

 - Select the **repo** scope for access

 - Click **Generate token**

 - Copy the personal access token

- Then type `run` in SBT

```
> run
[info] Running prs.Main
START RequestId: dc8beb69-1858-41f0-884c-9058930ea98f Version: $LATEST
22:07:26.356 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:105 - Starting aws-gh-prs 0.1-SNAPSHOT
22:07:26.375 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:107 - Inspecting API Gateway notification...
22:07:26.695 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:116 - Processed 1 event(s)
22:07:26.934 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:139 - Pull request was organization/master..username/hotfix1
22:07:26.935 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:180 - Querying GitHub for open pull request(s)...
22:07:32.775 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:202 - Found 2 pull request(s)
22:07:32.794 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:220 - Working directory is /tmp/sbt_39882bf2
22:07:32.794 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:222 - Cloning git@github.com:organization/example.git...
22:08:57.255 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:235 - Configuring repository...
22:08:57.256 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:242 - Set user to User Name
22:08:57.256 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:243 - Set email to user@example.com
22:08:57.256 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:245 - Configuring remotes...
22:08:57.714 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:263 - Remote username at git@github.com:username/example.git
22:08:57.733 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:274 - Configured 1 remote(s)
22:09:07.413 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:281 - Fetching username:example...
22:09:11.893 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:294 - Checking out organization/master...
22:09:13.093 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:302 - Creating branch staging...
22:09:13.716 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:313 - Merging username/feature1...
22:09:15.654 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:313 - Merging username/hotfix1...
22:09:22.554 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:361 - Pushing staging...
22:09:27.533 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:370 - Setting status on pull request(s)...
22:09:27.556 <dc8beb69-1858-41f0-884c-9058930ea98f> [main] INFO prs.Main$:383 - Success status set to pull request(s)
END RequestId: dc8beb69-1858-41f0-884c-9058930ea98f
REPORT RequestId: dc8beb69-1858-41f0-884c-9058930ea98f	Duration: 126355.07 ms	Billed Duration: 126400 ms Memory Size: 2047 MB  Max Memory Used: 518 MB
[
  "Merged branch(es) 'username/feature1', 'username/hotfix1' into 'staging'"
]
[success] Total time: 129 s, completed Mar 21, 2017 6:51:35 PM
```

### Building the JAR

To run this app on AWS Lambda, run the task provided by [SBT
Assembly], to build a JAR file.

```
> assembly
[info] Strategy 'discard' was applied to 69 files (Run the task at debug level to see details)
[info] Strategy 'first' was applied to 219 files (Run the task at debug level to see details)
[success] Total time: 13 s, completed Mar 26, 2023 10:19:33 PM
```

### Testing at AWS

- Follow the instructions for testing locally, see above

- Create a new API Gateway

 - From the [AWS console], click on the **API Gateway** service

 - Click on **Create API**

 - Enter a API name, such as **GitHub-My-Repo-Testing**

 - Click on **Create API**

 - Click on **Actions**

 - Click on **Create Resource**, such as "Web Hook"

 - Enter a **Resource Name**, such as "webhook"

 - Click on **Create**

 - Click on **Actions**

 - Click on **Create Method**

 - Select **POST** from the drop-down

 - Click on the new **POST** entry

 - Click the checkbox for **Use Lambda Proxy integration**

 - Enter the **Lambda Function** name

 - Click on **Save**

 - Copy the **ARN** for the API Gateway


- Connect the GitHub repo to the API Gateway topic

 - In GitHub, visit your repo's integration settings

   - http://github.com/my/repo/settings/hooks

 - Click **Add webhook**

 - For **Payload URL**, enter the **ARN** from earlier

 - For **Content type** choose **application/json**

 - For **Secret**, enter the **Authorization key** from earlier

 - For the **Aws secret**, enter the **Secret access key**

 - Choose **Let me select individual events**

 - Click the checkbox for **Pushes** (push) and **Pull requests** (pull_request)

 - Click **Add hook**

- Add the app to Lambda as a JAR

 - Follow the instructions below, see "Uploading to AWS Lambda"
